package project.bits.com.recandup;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by tejeshwar on 27/1/17.
 */
public interface ApiInterface {
    @Multipart
    @POST("upload.php")
    Call<ResponseBody> upload(
            @Part MultipartBody.Part file
    );
}
