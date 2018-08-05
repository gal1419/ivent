package ivent.com.ivent.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import ivent.com.ivent.R;
import ivent.com.ivent.rest.ApiService;
import ivent.com.ivent.rest.RestClient;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewGalleryImageActivity extends AppCompatActivity {

    Uri imageUri;
    TextView textView;
    ImageView imageView;
    TextView tagsView;
    ApiService apiService = RestClient.getApiService();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_gallery_image);

        imageUri = (Uri) getIntent().getExtras().get("imageUri");
        textView = findViewById(R.id.new_image_description);
        imageView = findViewById(R.id.new_image_img);
        tagsView = findViewById(R.id.new_image_tags);

        imageView.setImageURI(imageUri);
    }

    public void onAddNewImageClicked(View view) {
        try {
            uploadImageToServer((String) getIntent().getExtras().get("imagePath"));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        //If you want the Uri.
        //Mandatory to refresh image from Uri.
        //getImageView().setImageURI(null);

        //Setting the real returned image.
        //getImageView().setImageURI(r.getUri());

        //If you want the Bitmap.
        //getImageView().setImageBitmap(pickResult.getBitmap());

        //Image path
        //r.getPath();

    }

    private void uploadImageToServer(String path) throws Exception {
        File file = new File(path);

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        RequestBody eventId = RequestBody.create(MediaType.parse("text/plain"), (String) getIntent().getExtras().get("eventId"));
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), textView.getText().toString());

        Call<ivent.com.ivent.model.Picture> req = apiService.addPicture(body, eventId, description);
        req.enqueue(new Callback<ivent.com.ivent.model.Picture>() {
            @Override
            public void onResponse(Call<ivent.com.ivent.model.Picture> call, Response<ivent.com.ivent.model.Picture> response) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", response.body());
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }

            @Override
            public void onFailure(Call<ivent.com.ivent.model.Picture> call, Throwable t) {
                Toast.makeText(NewGalleryImageActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
