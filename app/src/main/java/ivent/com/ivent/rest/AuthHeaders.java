package ivent.com.ivent.rest;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import ivent.com.ivent.service.AuthenticationService;

/**
 * Created by galmalachi on 06/04/2018.
 */

public class AuthHeaders {

    public static GlideUrl getGlideUrlWithHeaders(String url) {
        return new GlideUrl(url, new LazyHeaders.Builder()
                .addHeader("Authorization", AuthenticationService.getAuthToken())
                .build());
    }
}