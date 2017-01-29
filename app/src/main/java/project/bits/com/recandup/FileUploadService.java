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
import project.bits.com.recandup.api.ApiInterface;
import project.bits.com.recandup.api.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

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
        manager = new DBManager(this);
        ArrayList<String> notUploaded = manager.getNotUploadedVideos();
        if (!notUploaded.isEmpty()) {
            for (int i = 0; i < notUploaded.size(); i++) {
                boolean success = uploadFile(Uri.parse(notUploaded.get(i)));
                Log.d(TAG, success + "");
            }
        }
    }

    boolean success;

    private boolean uploadFile(final Uri fileUri) {

        manager = new DBManager(this);

        // create upload service client
        ApiInterface service = ServiceGenerator.createService(ApiInterface.class);

        File file = new File(fileUri.getPath());

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("video", file.getName(), requestFile);

        // add another part within the multipart request
        String descriptionString = "hello, this is description speaking";
        RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);

        // finally, execute the request
        Call<ResponseBody> call = service.upload(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v("Upload", "success");
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
