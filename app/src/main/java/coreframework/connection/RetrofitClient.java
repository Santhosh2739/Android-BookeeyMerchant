package coreframework.connection;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class RetrofitClient {

    private static RetrofitClient instance = null;
    private final Api myApi;

    private RetrofitClient(String BASE_URL) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        myApi = retrofit.create(Api.class);
    }

    public static synchronized RetrofitClient getInstance(String BASE_URL) {
        if (instance == null) {
            instance = new RetrofitClient(BASE_URL);
        }
        return instance;
    }

    public Api getApi() {
        return myApi;
    }
}
