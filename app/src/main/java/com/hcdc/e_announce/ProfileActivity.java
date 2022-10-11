package com.hcdc.e_announce;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    private Context context = this;
    private TextView tvName, tvEmail, tvPhoneNo, tvPurok;
    private Button logOutButton;
    private ImageButton backButton;
    private Intent prevIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        backButton = findViewById(R.id.backButton);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhoneNo = findViewById(R.id.tvPhoneNo);
        tvPurok = findViewById(R.id.tvPurok);
        logOutButton = findViewById(R.id.logOutButton);

        prevIntent = getIntent();
        tvName.setText(prevIntent.getStringExtra("name"));
        tvEmail.setText(prevIntent.getStringExtra("email"));
        tvPhoneNo.setText(prevIntent.getStringExtra("number"));
        tvPurok.setText(prevIntent.getStringExtra("purok"));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Log out");
                builder.setMessage("Are you sure you want to log out?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logOut();
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    private void logOut(){
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}