package com.iana.sia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iana.sia.model.SIASecurityObj;
import com.iana.sia.model.User;
import com.iana.sia.utility.ApiResponse;
import com.iana.sia.utility.ApiResponseMessage;
import com.iana.sia.utility.GlobalVariables;
import com.iana.sia.utility.Internet_Check;
import com.iana.sia.utility.RestApiClient;
import com.iana.sia.utility.SIAUtility;

public class LoginTPUActivity extends AppCompatActivity {

    String urlResponse;
    int urlResponseCode;

    TextView troubleSignOn;
    TextView forgotUsername;

    EditText passwordEditText;

    Button loginBtn;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_tpu);

        BottomNavigationView bnv = findViewById(R.id.navigation_login);
        bnv.setSelectedItemId(R.id.navigation_login_tpu);

        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_login_mc:
                        startActivity(new Intent(LoginTPUActivity.this, LoginMCActivity.class));
                        finish(); /* This method will not display login page when click back (return) from phone */
                        break;

                    case R.id.navigation_login_ep:
                        startActivity(new Intent(LoginTPUActivity.this, LoginEPActivity.class));
                        finish(); /* This method will not display login page when click back (return) from phone */
                        break;
                    case R.id.navigation_login_idd:
                        startActivity(new Intent(LoginTPUActivity.this, LoginIDDActivity.class));
                        finish(); /* This method will not display login page when click back (return) from phone */
                        break;
                }

                return true;
            }
        });

        SIAUtility.disableShiftMode(bnv);
        progressBar = findViewById(R.id.processingBar);

        // below code is used to restrict auto populate keypad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        troubleSignOn = findViewById(R.id.troubleSignOn);
        troubleSignOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(LoginTPUActivity.this, ForgotPasswordTPUActivity.class));
                finish(); /* This method will not display login page when click back (return) from phone */
            }
        });

        forgotUsername = findViewById(R.id.forgotUsername);
        forgotUsername.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(LoginTPUActivity.this, ForgotUsernameTPUActivity.class));
                finish(); /* This method will not display login page when click back (return) from phone */
            }
        });

        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processLogin();
            }
        });

        passwordEditText    = findViewById(R.id.password);

        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
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

            String userName          = ((EditText) findViewById(R.id.userName)).getText().toString();
            String password          = ((EditText) findViewById(R.id.password)).getText().toString();

            String error = validateLoginFields(userName, password);
            if(error.equalsIgnoreCase("")) {

                // code to disable background functionality when progress bar starts
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                User user = new User();

                user.setUserName(userName.trim());
                user.setPassword(SIAUtility.replaceWhiteSpaces(password.trim()));
                user.setRole(GlobalVariables.ROLE_TPU);

                Gson gson = new Gson();
                String jsonInString = gson.toJson(user, User.class);
                new LoginTPUActivity.ExecuteTask(jsonInString).execute();

            } else {
                new ViewDialog().showDialog(LoginTPUActivity.this, getString(R.string.dialog_title_tpu_login), error);

            }

        } else {
            Intent intent = new Intent(LoginTPUActivity.this, NoInternetActivity.class);
            startActivity(intent);
        }
    }

    private String validateLoginFields(String userName, String password) {
        if((userName == null || userName.equalsIgnoreCase("") || userName.trim().length() <= 0) &&
                (password == null || password.equalsIgnoreCase("") || password.trim().length() <= 0)) {
            return getString(R.string.msg_error_empty_username_password);
        }

        if(userName == null || userName.equalsIgnoreCase("") || userName.trim().length() <= 0) {
            return getString(R.string.msg_error_empty_userName);
        }

        if(password == null || password.equalsIgnoreCase("") || password.trim().length() <= 0) {
            return getString(R.string.msg_error_empty_password);
        }

        return "";
    }

    /**
     * Method which navigates from Login Activity to Home Activity
     */

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

            ApiResponse apiResponse = RestApiClient.callPostApi(requestString, getString(R.string.base_url) +getString(R.string.api_sia_login));

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
                Log.v("log_tag", "Login Response Code: " + urlResponseCode);
                Gson gson = new Gson();

                if (urlResponseCode == 200) {

                    SIASecurityObj siaSecurityObj = gson.fromJson(result, SIASecurityObj.class);

                    /* Code to store login information start */

                    SharedPreferences sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(GlobalVariables.KEY_ORIGIN_FROM, GlobalVariables.ROLE_TPU);
                    SIAUtility.setObject(editor, GlobalVariables.KEY_SECURITY_OBJ, siaSecurityObj);
                    editor.commit();

                    /* Code to store login information end */

                    Intent intent = new Intent(LoginTPUActivity.this, DashboardActivity.class);
                    startActivity(intent);

                    finish(); /* This method will not display login page once redirected to dashboard */

                } else {

                    try {
                        ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                        new ViewDialog().showDialog(LoginTPUActivity.this, getString(R.string.dialog_title_tpu_login), errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                    } catch(Exception e) {
                        new ViewDialog().showDialog(LoginTPUActivity.this, getString(R.string.dialog_title_tpu_login), getString(R.string.msg_error_try_after_some_time));
                    }

                }

            } catch (Exception e) {
                Log.v("log_tag", "LoginTPUActivity Exception Error ", e);
            }

        }

    }

}
