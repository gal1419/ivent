package ivent.com.ivent.rest;

import android.content.Context;
import android.content.Intent;

import java.util.concurrent.TimeUnit;

import ivent.com.ivent.BuildConfig;
import ivent.com.ivent.activity.LoginActivity;
import ivent.com.ivent.service.AuthenticationService;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by galmalachi on 10/02/2018.
 */

public class RestClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static Context context;
    private static ApiService apiService = null;


    public static void setContext(Context appContext) {
        context = appContext;
    }

    public static ApiService getApiService() {

        if (apiService != null) {
            return apiService;
        }

        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.connectTimeout(5, TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            builder.addInterceptor(interceptor);
        }

        builder.addInterceptor(chain -> {
            Request request = chain.request().newBuilder().addHeader("ContentType", "application/json").build();

            if (AuthenticationService.isUserLoggedIn()) {
                Headers AuthHeader = request.headers().newBuilder().add("Authorization", AuthenticationService.getAuthToken()).build();
                request = request.newBuilder().headers(AuthHeader).build();
            }

            return chain.proceed(request);
        });

        builder.addInterceptor(chain -> {
            Request request = chain.request();
            okhttp3.Response response = chain.proceed(request);

            if (response.code() == 401) {
                AuthenticationService.clearToken(context);
                context.startActivity(
                        new Intent(
                                context.getApplicationContext(),
                                LoginActivity.class
                        )
                );
                return response;
            }
            return response;
        });

        OkHttpClient client = builder.build();

        Retrofit retrofit =
                new Retrofit.Builder().baseUrl(BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create()).build();

        apiService = retrofit.create(ApiService.class);
        return apiService;
    }
}