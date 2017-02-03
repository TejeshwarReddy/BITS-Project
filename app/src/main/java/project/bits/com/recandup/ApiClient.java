package project.bits.com.recandup;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by tejeshwar on 19/12/16.
 */

public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(String BASE_URL) {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
