package kr.co.company.capstone.interceptor;


import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor{
    private final String accessToken;

    public TokenInterceptor(String accessToken) {
        this.accessToken = accessToken;
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder().header("Authorization", accessToken);
        Request request = builder.build();
        return chain.proceed(request);
    }
}
