package ivent.com.ivent.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;

import net.glxn.qrgen.android.QRCode;

import ivent.com.ivent.R;
import ivent.com.ivent.model.Event;
import ivent.com.ivent.rest.AuthHeaders;

public class EventDetailsActivity extends AppCompatActivity {

    private ImageView eventThumbnailOrQR;
    private Bitmap eventQRCode;
    private Bitmap eventCover;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Event event = getIntent().getExtras().getParcelable("event");

        TextView eventTitle = findViewById(R.id.event_details_title);
        TextView eventAddress = findViewById(R.id.event_details_address);
        TextView eventOwner = findViewById(R.id.event_details_owner);
        TextView eventPictureNumber = findViewById(R.id.event_details_photos_number);
        TextView eventParticipants = findViewById(R.id.event_details_participants);
        eventThumbnailOrQR = findViewById(R.id.event_details_thumbnail_qr);
        Switch aSwitch = findViewById(R.id.event_details_image_switch);

        eventTitle.setText(event.getTitle());
        eventAddress.setText(event.getAddress());
        eventOwner.setText(event.getOwner().getFirstName());
        eventPictureNumber.setText("11");
        eventParticipants.setText(String.valueOf(event.getParticipants().size()));

        eventQRCode = QRCode.from(event.getId().toString()).bitmap();


        // loading event cover using Glide library
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").encodedAuthority("10.0.2.2:8080")
                .appendPath("event")
                .appendPath("thumbnail")
                .appendPath(String.valueOf(event.getId()));

        try {
            Glide.with(getApplicationContext())
                    .load(AuthHeaders.getGlideUrlWithHeaders(builder.build().toString()))
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(300,300) {

                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            eventCover = resource;
                            eventThumbnailOrQR.setImageBitmap(resource);
                        }
                    });

            aSwitch.setChecked(true);
            aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Bitmap bitmapToLoad = isChecked ? eventCover : eventQRCode;
                eventThumbnailOrQR.setImageBitmap(bitmapToLoad);
            });
        } catch (Exception e) {
            Log.e("Event Details Activity", e.getMessage());
        }


    }
}
