package project.bits.com.recandup;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by tejeshwar on 28/1/17.
 */

/**
 * This service is used for deleting files which are already uploaded
 * It also deletes the data related to the file stored in the database.
 *
 * It also takes care of the memory overflow i.e if the memory of the phone is full, it deletes the
 * files which are of 24 hrs old.
 */

public class FileDeletionService extends IntentService {

    public FileDeletionService() {
        super("FileDeletionService");
    }

    DBManager manager;
    long success;
    @Override
    protected void onHandleIntent(Intent intent) {
        manager = new DBManager(this);
        ArrayList<String> toBeDeleted = manager.getToBeDeleted();
        if ((toBeDeleted.size()!=0)) {
            for (int i = 0; i < toBeDeleted.size(); i++) {
                File fdelete = new File(toBeDeleted.get(i));
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        System.out.println("file Deleted :" + toBeDeleted.get(i));
                        manager.feedDeletedSuccess(toBeDeleted.get(i));
                    } else {
                        System.out.println("file not Deleted :" + toBeDeleted.get(i));
                    }
                }
            }
        }
        success = manager.deleteRecords();

        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
        long megAvailable = bytesAvailable / (1024 * 1024);
        Log.e("","Available MB : "+megAvailable);
        if (megAvailable<30){
            ArrayList<String> deleteMemory = manager.deleteMemory();
            if ((deleteMemory.size()!=0)) {
                for (int i = 0; i < deleteMemory.size(); i++) {
                    File fdelete = new File(deleteMemory.get(i));
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {
                            System.out.println("file Deleted :" + deleteMemory.get(i));
                            manager.feedDeletedSuccess(deleteMemory.get(i));
                        } else {
                            System.out.println("file not Deleted :" + deleteMemory.get(i));
                        }
                    }
                }
            }
        }
    }
}
