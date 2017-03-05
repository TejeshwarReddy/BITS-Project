package project.bits.com.recandup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by tejeshwar on 29/1/17.
 */

/**
 * Background running service to upload and delete video.
 * this service manages the other two services FileUploadService and FileDeletionService
 */
public class ServiceManager extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        onStartCommand(null,0,0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Service Manager","Service Started");

        // calling the Upload Service to upload the recorded video.
        Intent uploadIntent = new Intent(this, FileUploadService.class);
        PendingIntent pintent = PendingIntent.getService(this,0,uploadIntent,0);
        AlarmManager alarm = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), 30000, pintent);

        /*calling the deletion service to delete the file which which is uploaded and also
        * delete the file name from the database.*/
        Intent deleteIntent = new Intent(this, FileDeletionService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,deleteIntent,0);
        AlarmManager alarm1 = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarm1.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), 30000, pendingIntent);
        return START_STICKY;
    }
}
