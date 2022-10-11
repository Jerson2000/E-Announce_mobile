package com.hcdc.e_announce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MoreInfoActivity extends AppCompatActivity {

    private Context context = this;
    private Intent intent;
    private TextView eventName, eventDate, eventTime, eventDescription, tvEventGallery;
    private RecyclerView imageView;
    private ImageButton backButton;
    private Button loadGalleryButton;
    private HomeScreenActivity homeScreenActivity = new HomeScreenActivity();
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private ImageAdapter imageAdapter;
    private Post post = new Post(context);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);
        intent = getIntent();
        eventName = findViewById(R.id.eventName);
        eventDate = findViewById(R.id.eventDate);
        eventTime = findViewById(R.id.eventTime);
        eventDescription = findViewById(R.id.eventDescription);
        tvEventGallery = findViewById(R.id.tvEventGallery);
        imageView = findViewById(R.id.imageView);
        backButton = findViewById(R.id.backButton);
        loadGalleryButton = findViewById(R.id.loadGalleryButton);

        tvEventGallery.setVisibility(View.INVISIBLE);
        loadGalleryButton.setVisibility(imageView.INVISIBLE);
        post.checkIfImageExist(intent.getStringExtra("id"), new VolleyCallBack() {
            @Override
            public void onSuccess(JSONArray result) {
                try {
                    String numberOfImages = result.getString(0);
                    if (!numberOfImages.equals("0")) {
                        tvEventGallery.setVisibility(View.VISIBLE);
                        loadGalleryButton.setVisibility(imageView.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        eventName.setText(intent.getStringExtra("event_name"));
        eventDate.setText(changeDateFormat(intent.getStringExtra("date")));
        eventTime.setText(intent.getStringExtra("time"));
        eventDescription.setText(intent.getStringExtra("description"));
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(
                2, StaggeredGridLayoutManager.VERTICAL
        );
        imageView.setLayoutManager(staggeredGridLayoutManager);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loadGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadGalleryButton.setMinimumHeight(0);
                loadGalleryButton.setMinHeight(0);
                loadGalleryButton.setHeight(0);
                Map<String, String> params = new HashMap<String, String>();
                params.put("event_id", intent.getStringExtra("id"));
                post.getImage("imageDownload.php", params, new VolleyCallBack() {
                    @Override
                    public void onSuccess(JSONArray result) {
                        imageAdapter = new ImageAdapter(context, result);
                        imageView.setAdapter(imageAdapter);
                        imageView.setHasFixedSize(true);
                        imageView.setNestedScrollingEnabled(false);
                    }
                });
            }
        });

    }

    public String changeDateFormat(String date) {
        char month[] = {date.charAt(5), date.charAt(6)};
        char day[] = {date.charAt(8), date.charAt(9)};
        char year[] = {date.charAt(0), date.charAt(1), date.charAt(2), date.charAt(3)};
        date = homeScreenActivity.getMonthFormat(
                Integer.parseInt(String.copyValueOf(month))) + " " +
                String.copyValueOf(day) + ", " +
                String.copyValueOf(year);
        return date;
    }

}