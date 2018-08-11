package ivent.com.ivent.activity;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import net.glxn.qrgen.android.QRCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ivent.com.ivent.R;
import ivent.com.ivent.model.Event;
import ivent.com.ivent.rest.ApiService;
import ivent.com.ivent.rest.AuthHeaders;
import ivent.com.ivent.rest.RestClient;
import ivent.com.ivent.service.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsActivity extends AppCompatActivity {

    private ImageView eventThumbnailOrQR;
    private Bitmap eventQRCode;
    private Bitmap eventCover;
    private ApiService apiService = RestClient.getApiService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Event event = getIntent().getExtras().getParcelable("event");
        boolean enableCheckIn = getIntent().getExtras().getBoolean("enableCheckIn");

        Button checkInButton = findViewById(R.id.event_details_check_in_button);
        TextView eventTitle = findViewById(R.id.event_details_title);
        TextView eventAddress = findViewById(R.id.event_details_address);
        TextView eventOwner = findViewById(R.id.event_details_owner);
        TextView eventPictureNumber = findViewById(R.id.event_details_photos_number);
        TextView eventParticipants = findViewById(R.id.event_details_participants);
        eventThumbnailOrQR = findViewById(R.id.event_details_thumbnail_qr);
        Switch aSwitch = findViewById(R.id.event_details_image_switch);

        eventTitle.setText(event.getTitle());
        eventAddress.setText(event.getAddress());
        eventOwner.setText(event.getOwner().getFullName());
        eventPictureNumber.setText("11");
        eventParticipants.setText(String.valueOf(event.getParticipants().size()));

        if (enableCheckIn) {
            checkInButton.setVisibility(View.VISIBLE);
            checkInButton.setOnClickListener(v -> {

                Call<List<Event>> call = apiService.addUserToEvent(String.valueOf(event.getId()));
                call.enqueue(new Callback<List<Event>>() {
                    @Override
                    public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                        if (response.isSuccessful()) {
                            List<Event> userEvents = response.body();
                            Set<Long> eventIds = userEvents.stream().map(Event::getId).collect(Collectors.toSet());

                            if (eventIds.contains(event.getId())) {
                                Toast.makeText(EventDetailsActivity.this, "Your are checked in to this event!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Event>> call, Throwable throwable) {
                    }
                });

            });
        }

        eventQRCode = QRCode.from(event.getId().toString()).bitmap();

        List<String> paths = new ArrayList<>();
        paths.add("event");
        paths.add("thumbnail");
        paths.add(String.valueOf(event.getId()));

        try {
            Glide.with(getApplicationContext())
                    .load(AuthHeaders.getGlideUrlWithHeaders(Utils.getRestUri(paths).toString()))
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
