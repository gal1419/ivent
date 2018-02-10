package ivent.com.ivent.rest;

import ivent.com.ivent.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by galmalachi on 10/02/2018.
 */

public interface ApiService {


    @POST("users/sign-up")
    Call<Void> signUp(@Body User user);

    @POST("users/login")
    Call<Void> login(@Body User user);

}
