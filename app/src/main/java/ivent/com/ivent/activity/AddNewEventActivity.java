package ivent.com.ivent.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.File;
import java.util.UUID;

import ivent.com.ivent.R;
import ivent.com.ivent.rest.ApiService;
import ivent.com.ivent.rest.RestClient;
import ivent.com.ivent.service.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewEventActivity extends AppCompatActivity implements IPickResult {

    private Uri filePath;
    private EditText eventAddress;
    private EditText eventName;
    private ImageView eventImageView;
    private ApiService apiService = RestClient.getApiService();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);

        eventAddress = findViewById(R.id.eventAddress);
        eventName = findViewById(R.id.eventName);
        eventImageView = findViewById(R.id.eventImageView);
        eventImageView.setImageResource(R.drawable.image_placeholder);

        eventImageView.setOnClickListener(v -> {
            PickSetup setup = new PickSetup()
                    .setGalleryIcon(R.mipmap.gallery_colored)
                    .setCameraIcon(R.mipmap.camera_colored);
            PickImageDialog
                    .build(setup)
                    .show(this);
        });
    }

    public void onEventAdd(View view) {
        try {
            uploadImageToServer();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void uploadImageToServer() throws Exception {

        File file;

        if (filePath.getScheme().equals("file")) {
            file = new File(filePath.getPath());
        } else {
            file = new File(Utils.getPathFromURI(getContentResolver(), filePath));
        }


        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), eventName.getText().toString().trim());
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), eventAddress.getText().toString().trim());

        Call<ResponseBody> req = apiService.addEvent(body, title, address);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("AddNewEventActivity", "Picture was uploaded");
                finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                finish();
            }
        });
    }

    @Override
    public void onPickResult(PickResult pickResult) {
        if (pickResult.getError() == null) {
            eventImageView.setImageBitmap(pickResult.getBitmap());
            filePath = pickResult.getUri();
        }
    }

}
