package ivent.com.ivent.rest;

import java.util.List;

import ivent.com.ivent.model.Event;
import ivent.com.ivent.model.User;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by galmalachi on 10/02/2018.
 */

public interface ApiService {


    @POST("users/sign-up")
    Call<Void> signUp(@Body User user);

    @POST("users/login")
    Call<Void> login(@Body User user);

    @GET("users/events")
    Call<List<Event>> getUserEvents();

    @Multipart
    @POST("upload-file/add")
    Call<ResponseBody> postImage(@Part MultipartBody.Part image, @Part("name") RequestBody name);

    @Multipart
    @POST("event/add")
    Call<ResponseBody> addEvent(@Part MultipartBody.Part image, @Part("title") RequestBody title, @Part("address") RequestBody address);


}
