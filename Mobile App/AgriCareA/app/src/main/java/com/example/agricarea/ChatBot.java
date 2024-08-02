package com.example.agricarea;

import android.util.Log;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import java.util.concurrent.TimeUnit;

public class ChatBot {
    private static final String TAG = "ChatBot";
    private OkHttpClient client;

    public ChatBot() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();
    }

    public void sendMessage(String message, Callback callback) {
        String json = "{"
                + "\"inputs\":\"" + message + "\","
                + "\"parameters\": {"
                + "\"max_new_tokens\": 450"
                + "}"
                + "}";

        Request request = new Request.Builder()
                .url("https://api-inference.huggingface.co/models/meta-llama/Meta-Llama-3-8B-Instruct")
                .header("Authorization", "Bearer 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")
                .post(RequestBody.create(
                        MediaType.parse("application/json"),
                        json
                ))
                .build();

        client.newCall(request).enqueue(callback);
    }
}
