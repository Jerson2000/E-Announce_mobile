package com.hcdc.e_announce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatBotActivity extends AppCompatActivity {

    Context context = this;
    RecyclerView chatView;
    EditText userText;
    ImageButton backButton, sendButton;
    ArrayList<ChatModal> chats;
    ChatAdapter chatAdapter;
    private final String USER_KEY = "user";
    private final String OTHER_USER_KEY = "other_user";
    Post post = new Post(context);
    Intent intent;
    private ImageView chatBotImage;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);

        chatView = findViewById(R.id.chatView);
        userText = findViewById(R.id.etChatBotUserText);
        sendButton = findViewById(R.id.btnChatBotSend);
        backButton = findViewById(R.id.btnChatBotBack);
        chatBotImage = findViewById(R.id.imageView);

        chatBotImage.setImageDrawable(getDrawable(R.drawable.chat_bot_profile_image));
        intent = getIntent();
        chats = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chats);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        chatView.setLayoutManager(manager);
        chatView.setAdapter(chatAdapter);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                chats.add(
                        new ChatModal(
                                OTHER_USER_KEY,
                                "Good day, what can I help you with today?"
                        )
                );
                chatAdapter.notifyDataSetChanged();
                chatView.scrollToPosition(chats.size()-1);
            }
        }, 1000);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userText.getText().toString().isEmpty())
                    return;
                response(userText.getText().toString());
                userText.setText("");
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        userText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        chatView.scrollToPosition(chats.size()-1);
                    }
                }, 200);
                return false;
            }
        });

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", intent.getStringExtra("id"));
        post.MYSQL("prevComm.php", params, new VolleyCallBack() {
            @Override
            public void onSuccess(JSONArray result) {
                for (int i = 0; i < result.length(); i++) {
                    try {
                        JSONObject object = result.getJSONObject(i);
                        chats.add(new ChatModal(USER_KEY, object.getString("message")));
                        chats.add(new ChatModal(OTHER_USER_KEY, object.getString("bot")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                chatAdapter.notifyDataSetChanged();
                chatView.scrollToPosition(chats.size()-1);
            }
        });

    }

    public void response(String message) {
        message = message.trim();
        String user_id = intent.getStringExtra("id");
        chats.add(new ChatModal(USER_KEY, message));
        chatAdapter.notifyDataSetChanged();
        chatView.scrollToPosition(chats.size()-1);
        post.getResponse(user_id, message, new StringCallBack() {
            @Override
            public void onSuccess(String result) {
                chats.add(new ChatModal(OTHER_USER_KEY, result));
                chatAdapter.notifyDataSetChanged();
                chatView.scrollToPosition(chats.size()-1);
            }
        });
    }

}