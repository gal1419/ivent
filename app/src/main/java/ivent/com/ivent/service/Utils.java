package ivent.com.ivent.service;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import ivent.com.ivent.rest.AuthHeaders;
import ivent.com.ivent.rest.RestClient;

/**
 * Created by galmalachi on 23/06/2018.
 */

public class Utils {

    public static String getPathFromURI(ContentResolver contentResolver, Uri uri) {
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = contentResolver.query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    public static Uri getRestUri(List<String> paths) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(RestClient.SCHEMA).encodedAuthority(RestClient.SERVER_ADDRESS);
        paths.forEach(builder::appendPath);
        return builder.build();
    }

    public static void downloadWithGlide(List<String> paths, Context context, ImageView imageView) {
        Glide.with(context)
                .load(AuthHeaders.getGlideUrlWithHeaders(getRestUri(paths).toString()))
                .thumbnail(0.5f)
                .crossFade()
                .into(imageView);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
