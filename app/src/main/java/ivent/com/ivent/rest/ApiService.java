package ivent.com.ivent.rest;

import java.util.List;

import ivent.com.ivent.model.Event;
import ivent.com.ivent.model.Picture;
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
import retrofit2.http.Path;

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

    @GET("picture/event/{eventId}")
    Call<List<Picture>> getEventPictures(@Path("eventId") String eventId);

    @GET("event/{eventId}")
    Call<Event> getEventById(@Path("eventId") String eventId);

    @Multipart
    @POST("picture/add")
    Call<Picture> addPicture(@Part MultipartBody.Part image, @Part("eventId") RequestBody eventId, @Part("description") RequestBody description);

    @Multipart
    @POST("upload-file/add")
    Call<ResponseBody> postImage(@Part MultipartBody.Part image, @Part("name") RequestBody name);

    @Multipart
    @POST("event/add")
    Call<ResponseBody> addEvent(@Part MultipartBody.Part image, @Part("title") RequestBody title, @Part("address") RequestBody address);


}
