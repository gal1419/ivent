package ivent.com.ivent.activity;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        imageView = findViewById(R.id.scan_image);

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
                Toast.makeText(ScanQRActivity.this, "We could not process your QR Image. Try again", Toast.LENGTH_LONG).show();
            }

            Frame frame = new Frame.Builder()
                    .setBitmap(pickResult.getBitmap()).build();
            SparseArray<Barcode> barcodes = detector.detect(frame);

            if (barcodes.size() != 0) {
                String eventId = barcodes.valueAt(0).rawValue;

                Call<Event> call = apiService.getEventById(eventId);
                call.enqueue(new Callback<Event>() {
                    @Override
                    public void onResponse(Call<Event> call, Response<Event> response) {
                        if (response.isSuccessful()) {
                            Event event = response.body();
                            Intent intent = new Intent(ScanQRActivity.this, EventDetailsActivity.class);
                            intent.putExtra("event", event);
                            intent.putExtra("enableCheckIn", true);
                            startActivity(intent);
                        }
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Event> call, Throwable throwable) {
                        Toast.makeText(ScanQRActivity.this, "Event was not found. Please try again.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }

            Toast.makeText(ScanQRActivity.this, "We could not read the QR properly. Please make sure it is valid", Toast.LENGTH_LONG).show();
        }
    }
}
