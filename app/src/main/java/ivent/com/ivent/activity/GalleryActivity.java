package ivent.com.ivent.activity;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.util.ArrayList;
import java.util.List;

import ivent.com.ivent.Adapter.GalleryAdapter;
import ivent.com.ivent.R;
import ivent.com.ivent.RecyclerItemClickListener;
import ivent.com.ivent.model.Picture;
import ivent.com.ivent.rest.ApiService;
import ivent.com.ivent.rest.RestClient;
import ivent.com.ivent.service.AuthenticationService;
import ivent.com.ivent.service.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryActivity extends AppCompatActivity implements IPickResult, ActionMode.Callback {

    RecyclerView mRecyclerView;
    GalleryAdapter galleryAdapter;
    ImageView noPicturesBox;
    ImageView noPicturesText;
    AppBarLayout appBarLayout;
    ApiService apiService = RestClient.getApiService();
    DownloadManager downloadManager;
    ArrayList<Picture> eventPictures = new ArrayList<>();
    ActionMode actionMode;
    boolean isMultiSelect = false;
    List<Long> selectedIds = new ArrayList<>();
    String eventId;
    String eventTitle;
    ArrayList<Long> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_pictures);
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        eventId = String.valueOf(getIntent().getExtras().get("eventId"));
        eventTitle = String.valueOf(getIntent().getExtras().get("eventTitle"));
        boolean shouldOpenPicker = (boolean) getIntent().getExtras().get("shouldOpenPicker");
        appBarLayout = findViewById(R.id.gallery_app_bar);

        Toolbar toolbar = findViewById(R.id.gallery_toolbar);
        toolbar.setTitle(eventTitle);
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

        if (shouldOpenPicker) {
           openPicker();
        }
    }

    public void onAddPictureClicked(View view) {
        openPicker();
    }

    private void openPicker() {
        PickSetup setup = new PickSetup()
                .setGalleryIcon(R.mipmap.gallery_colored)
                .setCameraIcon(R.mipmap.camera_colored);
        PickImageDialog.build(setup)
                .show(this);
    }

    private void displayImagesGrid() {
        Context context = this;
        mRecyclerView = findViewById(R.id.event_gallery);
        mRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        mRecyclerView.setHasFixedSize(true);

        galleryAdapter = new GalleryAdapter(GalleryActivity.this, eventPictures);
        mRecyclerView.setAdapter(galleryAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (isMultiSelect) {
                    multiSelect(position);
                    return;
                }

                Intent intent = new Intent(context, ImageDetailsActivity.class);
                intent.putParcelableArrayListExtra("data", eventPictures);
                intent.putExtra("pos", position);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    selectedIds = new ArrayList<>();
                    isMultiSelect = true;

                    if (actionMode == null) {
                        appBarLayout.setVisibility(View.GONE);
                        actionMode = startActionMode(GalleryActivity.this);
                    }
                }

                multiSelect(position);
            }
        }));

        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void multiSelect(int position) {
        Picture data = galleryAdapter.getItem(position);

        if (data != null) {
            if (actionMode != null) {
                if (selectedIds.contains(data.getId()))
                    selectedIds.remove(data.getId());
                else
                    selectedIds.add(data.getId());

                if (selectedIds.size() > 0)
                    //show selected item count on action mode.
                    actionMode.setTitle(String.valueOf(selectedIds.size()));
                else {
                    //remove item count from action mode.
                    //hide action mode
                    actionMode.setTitle("");
                    actionMode.finish();
                }
                galleryAdapter.setSelectedIds(selectedIds);

            }
        }
    }

    private void displayNoImagesMessage() {
        noPicturesBox = findViewById(R.id.noEventPictures);
        noPicturesText = findViewById(R.id.noEventPicturesText);
        noPicturesBox.setVisibility(View.VISIBLE);
        noPicturesText.setVisibility(View.VISIBLE);
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
                noPicturesText.setVisibility(View.GONE);
                noPicturesBox.setVisibility(View.GONE);
                return;
            }

            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_select, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_download:
                for (Long id : selectedIds) {
                    List<String> paths = new ArrayList<>();
                    paths.add("picture");
                    paths.add(String.valueOf(id));

                    DownloadManager.Request request = new DownloadManager.Request(Utils.getRestUri(paths));
                    request.setTitle("Picture " + id + ".png");
                    request.setDescription("Picture " + id + ".png");
                    request.addRequestHeader("Authorization", AuthenticationService.getAuthToken());
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                    request.setShowRunningNotification(true);
                    request.setVisibleInDownloadsUi(true);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/Ivent/" + "/" + "Picture " + id + ".png");
                    list.add(downloadManager.enqueue(request));
                }
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        appBarLayout.setVisibility(View.VISIBLE);
        actionMode = null;
        isMultiSelect = false;
        selectedIds = new ArrayList<>();
        galleryAdapter.setSelectedIds(new ArrayList<>());
    }

}
