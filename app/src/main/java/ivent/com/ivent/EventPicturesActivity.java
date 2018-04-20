package ivent.com.ivent;

import android.os.Bundle;
import android.app.Activity;
import android.widget.GridView;

import ivent.com.ivent.Adapter.GridViewAdapter;

public class EventPicturesActivity extends Activity {

    private GridView gridView;
    private GridViewAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_pictures);

        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.event_picture_item_layout, getIntent().getExtras().getParcelableArrayList("PictureList"));
        gridView.setAdapter(gridAdapter);
    }

}
