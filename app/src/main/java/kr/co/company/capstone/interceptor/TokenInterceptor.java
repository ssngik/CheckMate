package kr.co.company.capstone.interceptor;


import android.util.Log;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@RequiredArgsConstructor
public class TokenInterceptor implements Interceptor{
    private final String accessToken;

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder().header("Authorization", accessToken);
        Request request = builder.build();
        return chain.proceed(request);
    }
}
