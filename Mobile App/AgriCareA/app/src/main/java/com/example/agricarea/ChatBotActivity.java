package com.example.agricarea;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChatBotActivity extends AppCompatActivity {
    private static final String TAG = "ChatBotActivity";
    private ChatBot chatBot;
    protected static final int RESULT_SPEECH = 1;
    private ImageView btnSpeak;
    private EditText userInput;
    private Button sendButton;
    private TextView chatbotResponse;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);

        chatBot = new ChatBot();
        userInput = findViewById(R.id.user_input);
        sendButton = findViewById(R.id.send_button);
        btnSpeak=findViewById(R.id.speek);
        chatbotResponse = findViewById(R.id.chatbot_response);
        scrollView = findViewById(R.id.scroll_view);
        try {
            Intent intX = getIntent();
            Bundle t = intX.getExtras();
            String tt = "How can " + t.getString("disease") + " be treated?";
            TextView txtReq = findViewById(R.id.txtreq);
            userInput.setText(tt);
        } catch (NullPointerException e) {
            Log.e(TAG, "Intent extras are null", e);
        }
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    userInput.setText("");
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Your device doesn't support Speech to Text", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txtReq = findViewById(R.id.txtreq);
                txtReq.setText("Please Wait we're treating your request ");
                animateTextColor(txtReq, Color.BLACK, Color.GRAY);

                String message = userInput.getText().toString();
                chatBot.sendMessage(message, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "Network request failed", e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatbotResponse.setText("Network request failed");
                                scrollView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        scrollView.fullScroll(View.FOCUS_DOWN);
                                        AlertDialog.Builder alert = new AlertDialog.Builder(ChatBotActivity.this);
                                        alert.setMessage("You don't have network connection\nFix it or use our offline encyclopedia");
                                        alert.setTitle("Error");
                                        alert.setPositiveButton("Use the Encyclopedia", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Dismiss dialog if OK is clicked
                                                Intent itt=new Intent(ChatBotActivity.this,encyclo.class);
                                                startActivity(itt);
                                            }
                                        });
                                        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        alert.show();
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            Log.e(TAG, "Server response error: " + response);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chatbotResponse.setText("Server response error");
                                    scrollView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            scrollView.fullScroll(View.FOCUS_DOWN);

                                        }
                                    });
                                }
                            });
                            return;
                        }

                        String responseBody = response.body().string();
                        try {
                            JSONArray jsonArray = new JSONArray(responseBody);
                            if (jsonArray.length() > 0) {
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                final String generatedText = jsonObject.getString("generated_text");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        chatbotResponse.setText(generatedText);
                                        scrollView.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                scrollView.fullScroll(View.FOCUS_DOWN);
                                            }
                                        });
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        chatbotResponse.setText("No response from the server");
                                        scrollView.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                scrollView.fullScroll(View.FOCUS_DOWN);
                                            }
                                        });
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Failed to parse JSON response", e);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chatbotResponse.setText("Failed to parse JSON response");
                                    scrollView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            scrollView.fullScroll(View.FOCUS_DOWN);
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });
    }
    private void animateTextColor(TextView textView, int startColor, int endColor) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), startColor, endColor);
        colorAnimation.setDuration(1000); // duration of the animation in milliseconds
        colorAnimation.setRepeatCount(ValueAnimator.INFINITE); // repeat animation
        colorAnimation.setRepeatMode(ValueAnimator.REVERSE); // reverse animation at the end so it goes back to startColor

        colorAnimation.addUpdateListener(animator -> textView.setTextColor((int) animator.getAnimatedValue()));

        colorAnimation.start();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RESULT_SPEECH:
                if(resultCode == RESULT_OK && data != null){
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    userInput.setText(text.get(0));
                }
                break;
        }
    }
}
