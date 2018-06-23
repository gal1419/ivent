package ivent.com.ivent;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ivent.com.ivent.Adapter.GridViewAdapter;
import ivent.com.ivent.model.Picture;
import ivent.com.ivent.rest.ApiService;
import ivent.com.ivent.rest.RestClient;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventPicturesActivity extends FragmentActivity implements IPickResult {

    private GridView picturesGrid;
    private GridViewAdapter gridAdapter;
    private TextView noPicturesTextView;
    private ApiService apiService = RestClient.getApiService();
    private List<Picture> eventPictures = new ArrayList<>();
    private String eventId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_pictures);

        eventId = String.valueOf(getIntent().getExtras().get("eventId"));

        Call<List<Picture>> call = apiService.getEventPictures(eventId);
        Context context = this;
        call.enqueue(new Callback<List<Picture>>() {
            @Override
            public void onResponse(Call<List<Picture>> call, Response<List<Picture>> response) {
                eventPictures.addAll(response.body());

                if (eventPictures.isEmpty()) {
                    noPicturesTextView = findViewById(R.id.noEventPictures);
                    noPicturesTextView.setVisibility(View.VISIBLE);
                } else {
                    picturesGrid = findViewById(R.id.picturesGrid);
                    gridAdapter = new GridViewAdapter(context, R.layout.event_picture_item_layout, eventPictures);
                    picturesGrid.setAdapter(gridAdapter);
                    picturesGrid.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Picture>> call, Throwable throwable) {
            }
        });

    }

    public void onAddPictureClicked(View view) {
        PickSetup setup = new PickSetup();

        PickImageDialog.build(setup)
                //.setOnClick(this)
                .show(this);
    }

    @Override
    public void onPickResult(PickResult pickResult) {
        if (pickResult.getError() == null) {
            Picture picture = new Picture();
            picture.setDescription("Uploaded by user");
            //picture.setId(Long.valueOf(UUID.randomUUID().toString()));
            //picture.setImage(pickResult.getBitmap());

            try {
                uploadImageToServer(pickResult.getPath());
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }


            //If you want the Uri.
            //Mandatory to refresh image from Uri.
            //getImageView().setImageURI(null);

            //Setting the real returned image.
            //getImageView().setImageURI(r.getUri());

            //If you want the Bitmap.
            //getImageView().setImageBitmap(pickResult.getBitmap());

            //Image path
            //r.getPath();
        } else {
            Toast.makeText(this, pickResult.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    protected void customize(PickSetup setup) {

        //setup.setWidth(800).setHeight(700);
        //setup.setVideo(true);

        setup.setGalleryIcon(R.mipmap.gallery_colored);
        setup.setCameraIcon(R.mipmap.camera_colored);
    }

    private void uploadImageToServer(String path) throws Exception {
        File file = new File(path);

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        RequestBody eventId = RequestBody.create(MediaType.parse("text/plain"), this.eventId);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "No description yet");

        Call<ResponseBody> req = apiService.addPicture(body, eventId, description);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(",", "ss");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
