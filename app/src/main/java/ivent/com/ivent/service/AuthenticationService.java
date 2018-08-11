package ivent.com.ivent.service;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by galmalachi on 10/02/2018.
 */

public class AuthenticationService {

    private static String authToken = "";
    private static String userEmail = "";

    public static String getAuthToken() {
        return authToken;
    }

    public static String getAuthToken(Context context) {
        if (!authToken.equals("")) {
            return authToken;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("ivent.com.ivent", Context.MODE_PRIVATE);
        authToken = sharedPreferences.getString("login-token", "");
        return authToken;
    }

    public static void setAuthToken(Context context, String authToken) {
        clearToken(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences("ivent.com.ivent", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("login-token", authToken).apply();
    }

    public static boolean isUserLoggedIn() {
        return !authToken.equals("");
    }

    public static void clearToken(Context context) {
        authToken = "";
        SharedPreferences sharedPreferences = context.getSharedPreferences("ivent.com.ivent", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("login-token");
    }

    public static void setUserEmail(Context context, String email) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ivent.com.ivent", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("user-email", email).apply();
    }

    public static String getUserEmail(Context context) {
        if (!userEmail.equals("")) {
            return userEmail;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("ivent.com.ivent", Context.MODE_PRIVATE);
        userEmail = sharedPreferences.getString("user-email", "");
        return userEmail;
    }
}
