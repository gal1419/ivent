package ivent.com.ivent.activity;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ivent.com.ivent.DepthPageTransformer;
import ivent.com.ivent.R;
import ivent.com.ivent.model.Picture;
import ivent.com.ivent.service.AuthenticationService;
import ivent.com.ivent.service.Utils;

public class ImageDetailsActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private DownloadManager downloadManager;

    public ArrayList<Picture> data = new ArrayList<>();
    BroadcastReceiver downloadListener;
    int pos;

    Toolbar toolbar;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        data = getIntent().getParcelableArrayListExtra("data");
        pos = getIntent().getIntExtra("pos", 0);
        setTitle(data.get(pos).getDescription());

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), data);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(pos);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setTitle(data.get(position).getDescription());
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        downloadListener = getDownloadListener();
        registerReceiver(downloadListener, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_download) {
            List<String> paths = new ArrayList<>();
            paths.add("picture");
            paths.add(String.valueOf(data.get(pos).getId()));

            DownloadManager.Request request = new DownloadManager.Request(Utils.getRestUri(paths));
            request.setTitle("Picture " + id + ".png");
            request.setDescription("Picture " + id + ".png");
            request.addRequestHeader("Authorization", AuthenticationService.getAuthToken());
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            request.setVisibleInDownloadsUi(true);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/Ivent/" + "/" + "Picture " + id + ".png");
            downloadManager.enqueue(request);
            Toast.makeText(ImageDetailsActivity.this, "Downloading...", Toast.LENGTH_LONG).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver getDownloadListener() {
        return new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, "Download completed successfully", Toast.LENGTH_LONG).show();
            }
        };

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(downloadListener);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public ArrayList<Picture> data = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm, ArrayList<Picture> data) {
            super(fm);
            this.data = data;
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position, data.get(position).getDescription(), String.valueOf(data.get(position).getId()));
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return data.get(position).getDescription();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        String description, imageId;
        int pos;
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_IMG_TITLE = "image_title";
        private static final String ARG_IMG_URL = "image_url";

        @Override
        public void setArguments(Bundle args) {
            super.setArguments(args);
            this.pos = args.getInt(ARG_SECTION_NUMBER);
            this.description = args.getString(ARG_IMG_TITLE);
            this.imageId = args.getString(ARG_IMG_URL);
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String name, String url) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_IMG_TITLE, name);
            args.putString(ARG_IMG_URL, url);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onStart() {
            super.onStart();

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            final ImageView imageView = rootView.findViewById(R.id.detail_image);
            List<String> paths = new ArrayList<>();
            paths.add("picture");
            paths.add(imageId);

            Utils.downloadWithGlide(paths, getActivity(), imageView);
            return rootView;
        }

    }
}
