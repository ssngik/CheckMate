package kr.co.company.capstone.dto;

import com.google.gson.Gson;

import lombok.Data;
import retrofit2.Response;

@Data
public class ErrorMessage {
    private String timestamp;
    private int status;
    private String error;
    private String code;
    private String message;

    public static ErrorMessage getErrorByResponse(Response response) {
        return new Gson().fromJson(response.errorBody().charStream(), ErrorMessage.class);
    }
}
