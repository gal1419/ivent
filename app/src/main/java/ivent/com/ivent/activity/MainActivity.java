package ivent.com.ivent.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import net.glxn.qrgen.android.QRCode;

import java.util.ArrayList;
import java.util.List;

import ivent.com.ivent.Adapter.EventAdapter;
import ivent.com.ivent.R;
import ivent.com.ivent.model.Event;
import ivent.com.ivent.rest.ApiService;
import ivent.com.ivent.rest.RestClient;
import ivent.com.ivent.service.AuthenticationService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements IPickResult {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<Event> eventList = new ArrayList<>();
    private ApiService apiService = RestClient.getApiService();
    public final int NEW_EVENT_OK_RESULT_CODE = 1;
    public final int NEW_EVENT_FAIL_RESULT_CODE = 2;
    public final int DELETE_EVENT_OK_RESULT_CODE = 3;
    public final int DELETE_EVENT_FAIL_RESULT_CODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RestClient.setContext(getApplicationContext());

        String loginToken = AuthenticationService.getAuthToken(getApplicationContext());
        if (loginToken.equals("")) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        initCollapsingToolbar();
        recyclerView = findViewById(R.id.recycler_view);
        eventList = new ArrayList<>();
        adapter = new EventAdapter(this, eventList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        prepareEvents();
    }

    public void onAddEventClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), AddNewEventActivity.class);
        startActivityForResult(intent, 1);
    }

    public void onScanQRClicked(View view) {
        PickSetup setup = new PickSetup()
                .setGalleryIcon(R.mipmap.gallery_colored)
                .setCameraIcon(R.mipmap.camera_colored);
        PickImageDialog
                .build(setup)
                .show(this);
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }


    private void prepareEvents() {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Getting Events...");
        progressDialog.show();

        Call<List<Event>> call = apiService.getUserEvents();

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                progressDialog.dismiss();
                if (!response.isSuccessful()) {
                    onEventsFetchFailed();
                    return;
                }
                onEventsFetchSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable throwable) {
                progressDialog.dismiss();
                onEventsFetchFailed();
            }
        });
    }

    private void onEventsFetchSuccess(List<Event> events) {
        this.eventList.clear();
        this.eventList.addAll(events);
        this.adapter.notifyDataSetChanged();
    }

    private void onEventsFetchFailed() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case NEW_EVENT_OK_RESULT_CODE:
                this.eventList.add((Event) data.getExtras().get("result"));
                this.adapter.notifyDataSetChanged();
                break;
            case NEW_EVENT_FAIL_RESULT_CODE:
                Toast.makeText(MainActivity.this, "There was a problem creating your event, try again later.", Toast.LENGTH_LONG).show();
                break;
            case DELETE_EVENT_OK_RESULT_CODE:
                this.eventList.remove((Event) data.getExtras().get("result"));
                this.adapter.notifyDataSetChanged();
                break;
            case DELETE_EVENT_FAIL_RESULT_CODE:
                Toast.makeText(MainActivity.this, "There was a problem deleting your event, try again later.", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onPickResult(PickResult pickResult) {
        if (pickResult.getError() == null) {
            Intent intent = new Intent(getApplicationContext(), ScanQRActivity.class);
            intent.putExtra("qrPath", pickResult.getPath());
            intent.putExtra("qrUri", pickResult.getUri());
            startActivity(intent);
        }
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}
