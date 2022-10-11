package com.hcdc.e_announce;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.FileInputStream;

public class ImageFullScreenActivity extends AppCompatActivity {

    private ImageView eventImage;
    private Intent intent;
    private Post post = new Post(this);

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_screen);
        eventImage = findViewById(R.id.eventImage);
        intent = getIntent();

        eventImage.setImageBitmap(getImage());
    }

    // Gets image from file name
    public Bitmap getImage() {
        Bitmap bitmap = null;
        String filename = getIntent().getStringExtra("filename");
        try {
            FileInputStream is = this.openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}