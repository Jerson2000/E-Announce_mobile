package com.hcdc.e_announce;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>{

    private Context context;
    private JSONArray images;
    private Post post;

    public ImageAdapter(Context context, JSONArray images) {
        this.context = context;
        this.images = images;
        this.post = new Post(context);
    }

    @NonNull
    @Override
    public ImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.image_holder, parent, false);
        return new ImageViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ImageViewHolder holder, int position) {
        try {
            JSONObject image = images.getJSONObject(position);
            holder.image = image;
            Bitmap imageBitmap = post.stringToBitmap(image.getString("image"));
            holder.eventImage.setImageBitmap(imageBitmap);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return images.length();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        JSONObject image;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            eventImage.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View view) {
                    try {
                        saveBitmapToFile(
                                post.stringToBitmap(image.getString("image")),
                                image.getString("id")
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        // Save the bitmap to a file to open on other activity
        public void saveBitmapToFile(Bitmap bitmap, String name) {
            try {
                //Write file
                String filename = name+".png";
                FileOutputStream stream = context.openFileOutput(filename, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                //Cleanup
                stream.close();
                bitmap.recycle();

                //Pop intent
                Intent intent = new Intent(context, ImageFullScreenActivity.class);
                intent.putExtra("filename", filename);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
