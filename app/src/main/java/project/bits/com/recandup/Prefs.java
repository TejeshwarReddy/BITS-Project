package project.bits.com.recandup;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tejeshwar on 2/2/17.
 */

public class Prefs {

    private static final String PACKAGE_NAME = "project.bits.com.recandup";
    private static final String url_link = "url";
    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PACKAGE_NAME, 0);
    }

    public static String getUrlPref(Context context) {
        return getPrefs(context).getString(url_link, null);
    }

    public static void setUrl_link(Context context,String url_link) {
        getPrefs(context).edit().putString(url_link,url_link).apply();
    }
}
