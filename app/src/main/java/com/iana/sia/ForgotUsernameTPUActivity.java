package com.iana.sia;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.iana.sia.model.User;
import com.iana.sia.utility.ApiResponse;
import com.iana.sia.utility.ApiResponseMessage;
import com.iana.sia.utility.GlobalVariables;
import com.iana.sia.utility.Internet_Check;
import com.iana.sia.utility.RestApiClient;
import com.iana.sia.utility.SIAUtility;

public class ForgotUsernameTPUActivity extends AppCompatActivity implements Animation.AnimationListener {

    private ProgressBar progressBar;

    Button backToHomeBtn;

    Animation slideLeft;

    EditText email;
    Button loginBtn;

    private String urlResponse;
    private int urlResponseCode;

    private String dialogTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_username_tpu);

        dialogTitle = getString(R.string.dialog_title_forgot_password);

        slideLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.set_in_left);
        slideLeft.setAnimationListener(this);

        progressBar = findViewById(R.id.processingBar);

        // below code is used to restrict auto populate keypad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        backToHomeBtn = findViewById(R.id.backToHomeBtn);
        backToHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPreviousPage();
            }
        });

        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processLogin();
            }
        });

        email    = findViewById(R.id.email);
        email.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    processLogin();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.rootLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });
    }

    void processLogin() {
        if (Internet_Check.checkInternetConnection(getApplicationContext())) {

            String email      = ((EditText) findViewById(R.id.email)).getText().toString();

            String error = validateFields(email);
            if(error == "") {

                User user = new User();

                user.setEmail(email.trim());
                user.setRoleName(GlobalVariables.ROLE_TPU);

                // code to disable background functionality when progress bar starts
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                Gson gson = new Gson();
                String jsonInString = gson.toJson(user, User.class);
                new ExecuteTask(jsonInString).execute();

            } else {
                new ViewDialog().showDialog(ForgotUsernameTPUActivity.this, dialogTitle, error);

            }

        } else {
            Intent intent = new Intent(ForgotUsernameTPUActivity.this, NoInternetActivity.class);
            startActivity(intent);
        }
    }

    private String validateFields(String email) {

        if(email == null || email == "" || email.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_empty_email);

        } else if(!SIAUtility.isValidEmail(email.toString())) {
            return getString(R.string.msg_error_invalid_email);
        }

        return "";
    }


    class ExecuteTask extends AsyncTask<String, Integer, String> {
        String requestString;

        public ExecuteTask(String requestString) {
            this.requestString = requestString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            ApiResponse apiResponse = RestApiClient.callPostApi(requestString, getString(R.string.base_url) +getString(R.string.api_forgot_username));

            urlResponse = apiResponse.getMessage();
            urlResponseCode = apiResponse.getCode();
            return urlResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);

            // code to regain disable backend functionality end
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            try {
                Log.v("log_tag", dialogTitle + " Response Code: " + urlResponseCode);
                Gson gson = new Gson();

                if (urlResponseCode == 200) {

                    email.setText("");
                    ApiResponseMessage successMessage = gson.fromJson(result, ApiResponseMessage.class);
                    new ViewDialog().showDialog(ForgotUsernameTPUActivity.this, dialogTitle, successMessage.getMessage());

                } else {

                    try {
                        ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                        new ViewDialog().showDialog(ForgotUsernameTPUActivity.this, dialogTitle, errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                    } catch(Exception e) {
                        new ViewDialog().showDialog(ForgotUsernameTPUActivity.this, dialogTitle, getString(R.string.msg_error_try_after_some_time));
                    }
                }

            } catch (Exception e) {
                Log.v("log_tag", "ForgotUsernameTPUActivity Exception Error ", e);
            }

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        backToHomeBtn.setVisibility(View.VISIBLE);
        backToHomeBtn.startAnimation(slideLeft);
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    void goToPreviousPage() {
        startActivity(new Intent(ForgotUsernameTPUActivity.this, LoginTPUActivity.class));
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            goToPreviousPage();
        }

        return super.onKeyDown(keyCode, event);
    }

}
