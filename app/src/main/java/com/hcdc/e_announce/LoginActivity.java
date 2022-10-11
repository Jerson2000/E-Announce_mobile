package com.hcdc.e_announce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    Context context = this;
    TextView signUpButton, etEmail, etPassword;
    Button loginButton;
    CheckBox remember;
    Post post = new Post(context);
    SharedPreferences login;
    ConstraintLayout hideKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.btnLogin);
        signUpButton = findViewById(R.id.lblLoginSignUp);
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        remember = findViewById(R.id.remember);
        hideKeyboard = findViewById(R.id.hideKeyboard);

        if (rememberState()){
            remember.toggle();
            getSaveLogin();
        }

        remember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (remember.isChecked())
                    rememberState(true);
                else if (!remember.isChecked()) {
                    rememberState(false);
                    saveLogin(null, null);
                }
            }
        });

        hideKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    inputMethodManager = (InputMethodManager)getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SignUpActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if (rememberState()) saveLogin(email, password);
                post.validateUser(email, password);
            }
        });

    }

    public void saveLogin(String email, String password) {
        // get or create SharedPreferences
        login = getSharedPreferences("login", MODE_PRIVATE);
        // save your string in SharedPreferences
        login.edit().putString("email", email).apply();
        login.edit().putString("password", password).apply();
    }


    public void getSaveLogin() {
        login = getSharedPreferences("login", MODE_PRIVATE);
        etEmail.setText(login.getString("email", null));
        etPassword.setText(login.getString("password", null));
    }

    public void rememberState(Boolean state) {
        login = getSharedPreferences("login", MODE_PRIVATE);
        if (state)
            login.edit().putString("remember", "true").apply();
        else if (!state)
            login.edit().putString("remember", "false").apply();
    }

    public Boolean rememberState() {
        login = getSharedPreferences("login", MODE_PRIVATE);
        if (login.getString("remember", null) == null)
            return false;
        if (login.getString("remember", null).equals("true"))
            return true;
        return false;
    }

}