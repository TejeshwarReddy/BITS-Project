package project.bits.com.recandup;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by tejeshwar on 28/1/17.
 */

public class FileUploadService extends IntentService {

    DBManager manager;

    public FileUploadService() {
        super("FileUploadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        Toast.makeText(this, "FILE UPLOAD SERVICE", Toast.LENGTH_SHORT).show();
        manager = new DBManager(this);
        ArrayList<String> notUploaded = manager.getNotUploadedVideos();
        if (!(notUploaded.size()==0)) {
            for (int i = 0; i < notUploaded.size(); i++) {
//                Log.e("FILE UPLOAD SERVICE", notUploaded.get(i));
                boolean success = uploadFile(Uri.parse(notUploaded.get(i)));
                Log.e("FILE UPLOAD SERVICE", success + "");
            }
        }
    }

    boolean success;

    private boolean uploadFile(final Uri fileUri) {
        Log.e("upload function","hello from upload file");
        manager = new DBManager(this);
        // create upload service client
        ApiInterface service = ApiClient.getClient().create(ApiInterface.class);
        File file = new File(fileUri.getPath());
        // create RequestBody instance from file


        RequestBody videoBody = RequestBody.create(MediaType.parse("video/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("video", file.getName(), videoBody);

        // finally, execute the request
        Call<ResponseBody> call = service.upload(fileToUpload);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("Upload", "success");
                manager.feedUploadSuccess(fileUri.getPath());
                success = true;
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
                Toast.makeText(FileUploadService.this,"Upload Failed check internet connection",Toast.LENGTH_SHORT).show();
                success = false;
            }
        });
        return success;
    }
}
