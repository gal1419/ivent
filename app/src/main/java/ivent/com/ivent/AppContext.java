package ivent.com.ivent;

import android.app.Application;
import android.content.Context;

/**
 * Created by galmalachi on 03/03/2018.
 */

class AppContext extends Application {

    private static Context mContext;

    public static Context getAppContext() {
        return mContext;
    }

    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
    }
}