package com.hcdc.e_announce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    Context context = this;
    TextView loginButton, signUpButton, lblName, lblEmail, lblNumber, lblPassword, lblAddress;
    EditText etName, etEmail, etPassword, etPasswordConfirmation, etPhoneNumber, etAddress;
    Post post = new Post(context);
    Boolean nameExist = false, emailExist = false, numberExist = false;
    ConstraintLayout hideKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        loginButton = findViewById(R.id.lblSignUpLogin);
        signUpButton = findViewById(R.id.btnSignUp);
        etName = findViewById(R.id.etSignUpName);
        etEmail = findViewById(R.id.etSignUpEmail);
        etPassword = findViewById(R.id.etSignUpPassword);
        etPasswordConfirmation = findViewById(R.id.etSignUpPasswordConfirmation);
        etPhoneNumber = findViewById(R.id.etSignUpPhone);
        etAddress = findViewById(R.id.etSignUpAddress);
        lblName = findViewById(R.id.lblName);
        lblEmail = findViewById(R.id.lblEmail);
        lblNumber = findViewById(R.id.lblNumber);
        lblPassword = findViewById(R.id.lblPassword);
        lblAddress = findViewById(R.id.lblAddress);
        hideKeyboard = findViewById(R.id.hideKeyboard);

        lblName.setText("");
        lblEmail.setText("");
        lblNumber.setText("");
        lblPassword.setText("");
        lblAddress.setText("");

        hideKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    inputMethodManager = (InputMethodManager)getSystemService(SignUpActivity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();
                String pass2 = etPasswordConfirmation.getText().toString().trim();
                String number = etPhoneNumber.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                Boolean valid = validateName(name);
                Boolean valid2 = validateEmail(email);
                Boolean valid3 = validatePassword(pass, pass2);
                Boolean valid4 = validateNumber(number);
                Boolean valid5 = validateAddress(address);
                if (valid && valid2 && valid3 && valid4 && valid5) {
                    // This will continue till final check
                    firstCheck(name, email, pass, number, address);
                }
            }
        });
    }

    public Boolean validateName(String name) {
        if (name.isEmpty()) {
            lblName.setText("Name is empty");
            return false;
        }
        if (name.length() <= 2) {
            lblName.setText("Name is invalid");
            return false;
        }
        lblName.setText("");
        return true;
    }

    public Boolean validateEmail(String email) {
        if (email.isEmpty()) {
            lblEmail.setText("Email is empty");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            lblEmail.setText("Invalid email");
            return false;
        }
        lblEmail.setText("");
        return true;
    }

    public Boolean validatePassword(String password, String passwordConfirmation) {
        if (password.isEmpty()) {
            lblPassword.setText("Password is empty");
            return false;
        }
        if (passwordConfirmation.isEmpty()) {
            lblPassword.setText("Password confirmation is empty");
            return false;
        }
        if (password.length() < 8) {
            lblPassword.setText("Password must be 8 characters long or greater");
            return false;
        }
        if (!password.equals(passwordConfirmation)) {
            lblPassword.setText("Password not similar");
            return false;
        }
        lblPassword.setText("");
        return true;
    }

    public Boolean validateNumber(String number) {
        if (number.isEmpty()) {
            lblNumber.setText("Number is Empty");
            return false;
        }
        if (number.length() != 11) {
            lblNumber.setText("Invalid Number");
            return false;
        }
        if (number.charAt(0) != '0' || number.charAt(1) != '9') {
            lblNumber.setText("Invalid, must start with 09");
            return false;
        }
        lblNumber.setText("");
        return true;
    }

    public Boolean validateAddress(String address) {
        if (address.isEmpty()) {
            lblAddress.setText("Purok is empty");
            return false;
        }
        if (address.length() < 5) {
            lblAddress.setText("Purok invalid");
            return false;
        }
        lblAddress.setText("");
        return true;
    }

    public void firstCheck(
            String name, String email, String password,
            String number, String address
    ) {
        String filename = "checkName.php";
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        post.MYSQL(filename, params, new VolleyCallBack() {
            @Override
            public void onSuccess(JSONArray result) {
                try {
                    JSONObject userInfo = result.getJSONObject(0);
                    exist("name", true);
                    secondCheck(name, email, password, number, address);
                } catch (JSONException e) {
                    Log.e("checkName", e.getMessage());
                    exist("name", false);
                    secondCheck(name, email, password, number, address);
                }
            }
        });
    }

    public void secondCheck(
            String name, String email, String password,
            String number, String address
    ) {
        String filename = "checkEmail.php";
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        post.MYSQL(filename, params, new VolleyCallBack() {
            @Override
            public void onSuccess(JSONArray result) {
                try {
                    JSONObject userInfo = result.getJSONObject(0);
                    exist("email", true);
                    thirdCheck(name, email, password, number, address);
                } catch (JSONException e) {
                    exist("email", false);
                    Log.e("checkEmail", e.getMessage());
                    thirdCheck(name, email, password, number, address);
                }
            }
        });
    }

    public void thirdCheck(
            String name, String email, String password,
            String number, String address
    ) {
        String filename = "checkNumber.php";
        Map<String, String> params = new HashMap<String, String>();
        params.put("number", number);
        post.MYSQL(filename, params, new VolleyCallBack() {
            @Override
            public void onSuccess(JSONArray result) {
                try {
                    JSONObject userInfo = result.getJSONObject(0);
                    exist("number", true);
                    finalCheck(name, email, password, number, address);
                } catch (JSONException e) {
                    exist("number", false);
                    Log.e("checkNumber", e.getMessage());
                    finalCheck(name, email, password, number, address);
                }
            }
        });
    }

    public void finalCheck(
            String name, String email, String password, String number, String address
    ) {
        if (!nameExist && !emailExist && !numberExist) {
            signUpSuccess(name, email, password, number, address);
        }
    }

    public void exist(String name, Boolean exist) {
        if (name.equals("name") && exist) {
            nameExist = true;
            lblName.setText("Name already registered");
        }
        else if (name.equals("name") && !exist) {
            nameExist = false;
            lblName.setText("");
        }
        if (name.equals("email") && exist) {
            emailExist = true;
            lblEmail.setText("Email already registered");
        }
        else if (name.equals("email") && !exist) {
            emailExist = false;
            lblEmail.setText("");
        }
        if (name.equals("number") && exist) {
            numberExist = true;
            lblNumber.setText("Number already registered");
        }
        else if (name.equals("number") && !exist) {
            numberExist = false;
            lblNumber.setText("");
        }
    }

    public void signUpSuccess(
            String name, String email, String password, String number, String address
    ) {
        post.createAccount(name, email, password, number, address);
    }

}