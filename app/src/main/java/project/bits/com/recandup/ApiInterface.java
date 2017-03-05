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

/**
 * This interface is used for creating the methods for http request
 * these functions can be implemented in any class you want to perform requests
 */

public interface ApiInterface {
    @Multipart
    @POST("upload.php")
    Call<ResponseBody> upload(
            @Part MultipartBody.Part file
    );
}
