package project.bits.com.recandup;

import android.app.IntentService;
import android.content.Intent;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by tejeshwar on 28/1/17.
 */

public class FileDeletionService extends IntentService {

    public FileDeletionService() {
        super("FileDeletionService");
    }

    DBManager manager;
    @Override
    protected void onHandleIntent(Intent intent) {
        manager = new DBManager(this);
        ArrayList<String> toBeDeleted = new ArrayList<>();
        toBeDeleted = manager.getToBeDeleted();
        if (!toBeDeleted.isEmpty()) {
            for (int i = 0; i < toBeDeleted.size(); i++) {
                File fdelete = new File(toBeDeleted.get(i));
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        System.out.println("file Deleted :" + toBeDeleted.get(i));
                    } else {
                        System.out.println("file not Deleted :" + toBeDeleted.get(i));
                    }
                }
            }
        }
    }
}
