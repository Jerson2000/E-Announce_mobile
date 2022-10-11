package com.hcdc.e_announce;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import pub.devrel.easypermissions.EasyPermissions;

public class SaveImageActivity extends AppCompatActivity {

    Context context = this;
    Button selectButton, saveButton, downloadButton;
    ImageView imageViewSelect;
    Post post = new Post(context);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_image);

        selectButton = findViewById(R.id.selectButton);
        saveButton = findViewById(R.id.saveButton);
        imageViewSelect = findViewById(R.id.imageViewSelect);
        downloadButton = findViewById(R.id.downloadButton);

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, 3);
                getImage();
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadImage("1");
            }
        });

    }

    public void getImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Browser Image"),
                Intent.CONTENTS_FILE_DESCRIPTOR);
    }



    public void downloadImage(String event_id) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("event_id", "1");
        final ProgressDialog progressDialog = ProgressDialog.show(
                context, "Image upload progress",
                "Uploading image", false, false
        );
        post.MYSQL("imageDownload.php", params, new VolleyCallBack() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(JSONArray result) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = result.getJSONObject(0);
                    imageViewSelect.setImageBitmap(post.stringToBitmap(
                            jsonObject.getString("image")
                    ));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void uploadImage(Bitmap bitmap) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("image", post.bitmapToString(bitmap));
        params.put("event_id", "1");
        final ProgressDialog progressDialog = ProgressDialog.show(
                context, "Image upload progress",
                "Uploading image", false, false
        );
        post.MYSQL("imageUpload.php", params, new VolleyCallBack() {
            @Override
            public void onSuccess(JSONArray result) {
                progressDialog.dismiss();
                Log.e("uploadImage", String.valueOf(result));
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == Intent.CONTENTS_FILE_DESCRIPTOR)
                && (resultCode == RESULT_OK) && (data != null)
                && (data.getData() != null)) {
            Uri filePath = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                imageViewSelect.setImageBitmap(bitmap);
                uploadImage(post.stringToBitmap(post.bitmapToString(bitmap)));
            } catch (IOException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}