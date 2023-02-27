package kr.co.company.capstone.interceptor;


import java.io.IOException;

import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@RequiredArgsConstructor
public class JwtInterceptor implements Interceptor{
    private final String jwtToken;

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();
        System.out.println("intercaeptor : " + jwtToken);
        Request.Builder builder = original.newBuilder().header("Authorization", jwtToken);
        Request request = builder.build();
        return chain.proceed(request);
    }
}
