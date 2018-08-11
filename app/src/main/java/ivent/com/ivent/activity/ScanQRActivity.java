package ivent.com.ivent.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.lang.ref.WeakReference;

import ivent.com.ivent.R;
import ivent.com.ivent.model.Event;
import ivent.com.ivent.rest.ApiService;
import ivent.com.ivent.rest.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanQRActivity extends AppCompatActivity {
    ImageView imageView;
    String imagePath;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        Uri imageUri = (Uri) getIntent().getExtras().get("qrUri");
        imagePath = (String) getIntent().getExtras().get("qrPath");
        imageView = findViewById(R.id.scan_image);
        imageView.setImageURI(imageUri);

        new DetectBarcodeTask(ScanQRActivity.this).execute(imagePath);

    }

    private static class DetectBarcodeTask extends AsyncTask<String, Void, String> {
        ApiService apiService = RestClient.getApiService();
        ProgressDialog progressDialog;

        private WeakReference<ScanQRActivity> activityReference;

        DetectBarcodeTask(ScanQRActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... path) {
            File imgFile = new File(path[0]);

            if (imgFile.exists()) {
                Bitmap qrBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                BarcodeDetector detector =
                        new BarcodeDetector.Builder(activityReference.get().getApplicationContext())
                                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                                .build();

                if (detector.isOperational()) {
                    Frame frame = new Frame.Builder()
                            .setBitmap(qrBitmap).build();
                    SparseArray<Barcode> barcodes = detector.detect(frame);

                    if (barcodes.size() != 0) {
                        return barcodes.valueAt(0).rawValue;
                    }

                }
            }
            return "";
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(activityReference.get(),
                    R.style.AppTheme_Dark_Dialog);

            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Searching for the event...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {

            if (!result.isEmpty()) {
                Call<Event> call = apiService.getEventById(result);
                call.enqueue(new Callback<Event>() {
                    @Override
                    public void onResponse(Call<Event> call, Response<Event> response) {
                        if (response.isSuccessful()) {
                            Event event = response.body();
                            Intent intent = new Intent(activityReference.get(), EventDetailsActivity.class);
                            intent.putExtra("event", event);
                            intent.putExtra("enableCheckIn", true);
                            activityReference.get().startActivity(intent);
                        }
                        progressDialog.dismiss();
                        activityReference.get().finish();
                    }

                    @Override
                    public void onFailure(Call<Event> call, Throwable throwable) {
                        handleError();
                    }
                });
            } else {
                handleError();
            }
        }

        private void handleError() {
            progressDialog.dismiss();
            Toast.makeText(activityReference.get(), "We could not process your QR Image. Try again", Toast.LENGTH_LONG).show();
            activityReference.get().finish();
        }
    }
}
