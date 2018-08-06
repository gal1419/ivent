package ivent.com.ivent.activity;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import ivent.com.ivent.R;
import ivent.com.ivent.model.Event;
import ivent.com.ivent.rest.ApiService;
import ivent.com.ivent.rest.AuthHeaders;
import ivent.com.ivent.rest.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanQRActivity extends AppCompatActivity implements IPickResult {

    ApiService apiService = RestClient.getApiService();
    ImageView imageView;
    TextView eventNameTextView;
    TextView ownerTextView;
    TextView participantsTextView;
    TextView addressTextView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        imageView = findViewById(R.id.scan_image);
        eventNameTextView = findViewById(R.id.scan_event_name);
        ownerTextView = findViewById(R.id.scan_owner);
        participantsTextView = findViewById(R.id.scan_participants);
        addressTextView = findViewById(R.id.scan_address);
        button = findViewById(R.id.check_in);

        PickSetup setup = new PickSetup()
                .setGalleryIcon(R.mipmap.gallery_colored)
                .setCameraIcon(R.mipmap.camera_colored);
        PickImageDialog
                .build(setup)
                .show(this);
    }

    @Override
    public void onPickResult(PickResult pickResult) {
        if (pickResult.getError() == null) {
            imageView.setImageBitmap(pickResult.getBitmap());

            BarcodeDetector detector =
                    new BarcodeDetector.Builder(getApplicationContext())
                            .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                            .build();

            if (!detector.isOperational()) {
                return;
            }

            Frame frame = new Frame.Builder()
                    .setBitmap(pickResult.getBitmap()).build();
            SparseArray<Barcode> barcodes = detector.detect(frame);

            if (barcodes.size() != 0) {
                String eventId = barcodes.valueAt(0).rawValue;

                Call<Event> call = apiService.getEventById("1");
                call.enqueue(new Callback<Event>() {
                    @Override
                    public void onResponse(Call<Event> call, Response<Event> response) {

                        Event event = response.body();
                        // loading event cover using Glide library
                        Uri.Builder builder = new Uri.Builder();
                        builder.scheme("http").encodedAuthority("10.0.2.2:8080")
                                .appendPath("event")
                                .appendPath("thumbnail")
                                .appendPath("1");

                        Glide.with(ScanQRActivity.this)
                                .load(AuthHeaders.getGlideUrlWithHeaders(builder.build().toString()))
                                .thumbnail(0.5f)
                                .crossFade()
                                .into(imageView);

                        eventNameTextView.setText(event.getTitle());
                        eventNameTextView.setVisibility(View.VISIBLE);

                        ownerTextView.setText(event.getOwner().getFirstName() + " " + event.getOwner().getLastName());
                        ownerTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_key, 0, 0, 0);
                        ownerTextView.setVisibility(View.VISIBLE);

                        addressTextView.setText(event.getAddress());
                        addressTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_address, 0, 0, 0);
                        addressTextView.setVisibility(View.VISIBLE);

                        participantsTextView.setText(event.getParticipants().size() + " " + "Participants");
                        participantsTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_participants, 0, 0, 0);
                        participantsTextView.setVisibility(View.VISIBLE);

                        button.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Call<Event> call, Throwable throwable) {
                        Toast.makeText(ScanQRActivity.this, "Event was not found. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    }
}
