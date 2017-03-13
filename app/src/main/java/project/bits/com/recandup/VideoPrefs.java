package project.bits.com.recandup;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tejeshwar on 5/3/17.
 */

/**
 * Shared Preferences class for storing video settings of the video to be recorded.
 */

public class VideoPrefs {

    private static final String PACKAGE_NAME = "VIDEO_PREFS";
    private static final String quality = "quality";
    private static final String RECORDING_STATUS = "status";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PACKAGE_NAME,Context.MODE_PRIVATE);
    }

    public static int getQualityPref(Context context) {
        return getPrefs(context).getInt(quality, -1);
    }

    public static void setQuality(Context context,int quality) {
        getPrefs(context).edit().putInt(VideoPrefs.quality,quality).apply();
    }

    public static void setVideoRecordingStatus(Context context,int status){
        getPrefs(context).edit().putInt(VideoPrefs.RECORDING_STATUS,status).apply();
    }

    public static int getVideoRecordingStatus(Context context){
        return getPrefs(context).getInt(RECORDING_STATUS,0);
    }

}
