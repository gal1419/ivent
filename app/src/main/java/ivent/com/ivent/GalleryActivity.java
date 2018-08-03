package ivent.com.ivent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.util.ArrayList;
import java.util.List;

import ivent.com.ivent.Adapter.GalleryAdapter;
import ivent.com.ivent.model.Picture;
import ivent.com.ivent.rest.ApiService;
import ivent.com.ivent.rest.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryActivity extends AppCompatActivity implements IPickResult {

    RecyclerView mRecyclerView;
    GalleryAdapter galleryAdapter;
    TextView noPicturesTextView;
    ApiService apiService = RestClient.getApiService();
    ArrayList<Picture> eventPictures = new ArrayList<>();
    String eventId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_pictures);
        eventId = String.valueOf(getIntent().getExtras().get("eventId"));

        Toolbar toolbar = findViewById(R.id.gallery_toolbar);
        setSupportActionBar(toolbar);

        Call<List<Picture>> call = apiService.getEventPictures(eventId);
        call.enqueue(new Callback<List<Picture>>() {
            @Override
            public void onResponse(Call<List<Picture>> call, Response<List<Picture>> response) {
                eventPictures.addAll(response.body());

                if (eventPictures.isEmpty()) {
                    displayNoImagesMessage();
                } else {
                   displayImagesGrid();
                }
            }

            @Override
            public void onFailure(Call<List<Picture>> call, Throwable throwable) {
                Toast.makeText(GalleryActivity.this, "Cannot load photos, try again later", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void onAddPictureClicked(View view) {
        PickSetup setup = new PickSetup();
        setup.setGalleryIcon(R.mipmap.gallery_colored);
        setup.setCameraIcon(R.mipmap.camera_colored);
        PickImageDialog.build(setup)
                .show(this);
    }

    private void displayImagesGrid() {
        Context context = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.event_gallery);
        mRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        mRecyclerView.setHasFixedSize(true);

        galleryAdapter = new GalleryAdapter(GalleryActivity.this, eventPictures);
        mRecyclerView.setAdapter(galleryAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context,
                (view, position) -> {
                    Intent intent = new Intent(context, ImageDetailsActivity.class);
                    intent.putParcelableArrayListExtra("data", eventPictures);
                    intent.putExtra("pos", position);
                    startActivity(intent);
                }));

        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void displayNoImagesMessage() {
        noPicturesTextView = findViewById(R.id.noEventPictures);
        noPicturesTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPickResult(PickResult pickResult) {
        if (pickResult.getError() == null) {
            Intent intent = new Intent(GalleryActivity.this, NewGalleryImageActivity.class);
            intent.putExtra("imagePath", pickResult.getPath());
            intent.putExtra("imageUri", pickResult.getUri());
            intent.putExtra("eventId", eventId);
            startActivityForResult(intent, 1);
        } else {
            Toast.makeText(this, pickResult.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Picture result = (Picture) data.getExtras().get("result");
            eventPictures.add(result);

            if (eventPictures.size() == 1) {
                displayImagesGrid();
                noPicturesTextView.setVisibility(View.GONE);
                return;
            }

            eventPictures.add(result);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }
}
