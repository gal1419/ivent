package ivent.com.ivent.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import ivent.com.ivent.service.AuthenticationService;
import ivent.com.ivent.service.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsActivity extends AppCompatActivity {

    private ImageView eventThumbnailOrQR;
    private Bitmap eventQRCode;
    private Bitmap eventCover;
    private ApiService apiService = RestClient.getApiService();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Event event = getIntent().getExtras().getParcelable("event");
        boolean enableCheckIn = getIntent().getExtras().getBoolean("enableCheckIn");

        Button checkInOrDeleteButton = findViewById(R.id.event_details_button);
        TextView eventTitle = findViewById(R.id.event_details_title);
        TextView eventAddress = findViewById(R.id.event_details_address);
        TextView eventOwner = findViewById(R.id.event_details_owner);
        TextView eventParticipants = findViewById(R.id.event_details_participants);
        eventThumbnailOrQR = findViewById(R.id.event_details_thumbnail_qr);
        Switch aSwitch = findViewById(R.id.event_details_image_switch);

        eventTitle.setText(event.getTitle());
        eventAddress.setText(event.getAddress());
        eventOwner.setText(event.getOwner().getFullName());
        eventParticipants.setText(String.valueOf(event.getParticipants().size()) + " Participants");

        if (enableCheckIn) {
            checkInOrDeleteButton.setVisibility(View.VISIBLE);
            checkInOrDeleteButton.setText("Check In");
            checkInOrDeleteButton.setOnClickListener(v -> {

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

        if (event.getOwner().getEmail().equals(AuthenticationService.getUserEmail(EventDetailsActivity.this))) {
            checkInOrDeleteButton.setVisibility(View.VISIBLE);
            checkInOrDeleteButton.setText("Delete Event");

            checkInOrDeleteButton.setOnClickListener(v -> {

                progressDialog = new ProgressDialog(EventDetailsActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Deleting Event...");
                progressDialog.show();

                Call<Event> call = apiService.deleteByEventId(String.valueOf(event.getId()));
                call.enqueue(new Callback<Event>() {
                    @Override
                    public void onResponse(Call<Event> call, Response<Event> response) {
                        if (response.isSuccessful()) {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("result", response.body());
                            setResult(3, returnIntent);
                            progressDialog.dismiss();
                            finish();
                        } else {
                            handleError();
                        }
                    }

                    @Override
                    public void onFailure(Call<Event> call, Throwable throwable) {
                        handleError();
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

    private void handleError() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "");
        setResult(4, returnIntent);
        progressDialog.dismiss();
        finish();
    }
}
