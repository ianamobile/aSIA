package com.iana.sia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

public class LoginMCActivity extends AppCompatActivity implements Animation.AnimationListener {

    private String urlResponse;
    private int urlResponseCode;
    TextView troubleSignOn;

    Button secondaryUserBtn;

    EditText passwordEditText;

    private ProgressBar progressBar;

    Animation slideLeft;

    Button loginBtn;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_mc);

        secondaryUserBtn = findViewById(R.id.secondaryUserBtn);

        slideLeft = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.set_in_left);
        slideLeft.setAnimationListener(this);

        BottomNavigationView bnv = findViewById(R.id.navigation_login);
        bnv.setSelectedItemId(R.id.navigation_login_mc);

        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_login_mc:
                        break;
                    case R.id.navigation_login_ep:
                        startActivity(new Intent(LoginMCActivity.this, LoginEPActivity.class));
                        finish(); /* This method will not display login page when click back (return) from phone */
                        break;
                    case R.id.navigation_login_tpu:
                        startActivity(new Intent(LoginMCActivity.this, LoginTPUActivity.class));
                        finish(); /* This method will not display login page when click back (return) from phone */
                        break;
                    case R.id.navigation_login_idd:
                        startActivity(new Intent(LoginMCActivity.this, LoginIDDActivity.class));
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

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);

        if(sharedPref != null && !sharedPref.getString(GlobalVariables.KEY_ACCESS_TOKEN, "").equalsIgnoreCase("")){
            Intent i = new Intent(LoginMCActivity.this, DashboardActivity.class);
            startActivity(i);
            finish(); /* This method will not display login page when click back (return) from phone */
            return;
        }


        secondaryUserBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(GlobalVariables.KEY_ORIGIN_FROM, GlobalVariables.ROLE_MC);
                editor.putString(GlobalVariables.KEY_MEM_TYPE, "");
                editor.commit();
                // Perform action on click
                startActivity(new Intent(LoginMCActivity.this, LoginSECActivity.class));
                finish(); /* This method will not display login page when click back (return) from phone */
            }
        });

        troubleSignOn = findViewById(R.id.troubleSignOn);
        troubleSignOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(GlobalVariables.KEY_ORIGIN_FROM, GlobalVariables.ROLE_MC);
                editor.commit();
                // Perform action on click
                startActivity(new Intent(LoginMCActivity.this, ForgotPasswordActivity.class));
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

            String scac          = ((EditText) findViewById(R.id.scac)).getText().toString();
            String password          = ((EditText) findViewById(R.id.password)).getText().toString();

            String error = validateLoginFields(scac, password);
            if(error == "") {

                User user = new User();

                user.setScac(scac.trim());
                user.setPassword(SIAUtility.replaceWhiteSpaces(password.trim()));
                user.setRole(GlobalVariables.ROLE_MC);

                Gson gson = new Gson();
                String jsonInString = gson.toJson(user, User.class);
                new ExecuteTask(jsonInString).execute();

            } else {
                new ViewDialog().showDialog(LoginMCActivity.this, getString(R.string.dialog_title_mc_login), error);

            }

        } else {
            Intent intent = new Intent(LoginMCActivity.this, NoInternetActivity.class);
            startActivity(intent);
        }
    }

    private String validateLoginFields(String scac, String password) {
        if((scac == null || scac == "" || scac.toString().trim().length() <= 0) &&
                (password == null || password == "" || password.toString().trim().length() <= 0)) {
            return getString(R.string.msg_error_mc_ep_login);
        }

        if(scac == null || scac == "" || scac.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_blank_scac);
        }

        if(password == null || password == "" || password.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_blank_password);
        }

        if (!SIAUtility.isAlpha(scac)) {
            return getString(R.string.msg_error_char_scac);

        } else if (scac.length() != 4) {
            return getString(R.string.msg_error_length_scac);
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

            try {
                Log.v("log_tag", "Login Response Code: " + urlResponseCode);
                Gson gson = new Gson();


                if (urlResponseCode == 200) {

                    SIASecurityObj siaSecurityObj = gson.fromJson(result, SIASecurityObj.class);
                    Log.v("log_tag", "Login Response siaSecurityObj: " + siaSecurityObj);
                    /* Code to store login information start */

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(GlobalVariables.KEY_ORIGIN_FROM, GlobalVariables.ROLE_MC);
                    SIAUtility.setObject(editor, GlobalVariables.KEY_SECURITY_OBJ, siaSecurityObj);
                    editor.commit();

                    /* Code to store login information end */

                    Intent intent = new Intent(LoginMCActivity.this, DashboardActivity.class);
                    startActivity(intent);

                    finish(); /* This method will not display login page once redirected to dashboard */

                } else {

                    try {
                        ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                        new ViewDialog().showDialog(LoginMCActivity.this, getString(R.string.dialog_title_mc_login), errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                    } catch(Exception e) {
                        new ViewDialog().showDialog(LoginMCActivity.this, getString(R.string.dialog_title_mc_login), getString(R.string.msg_error_try_after_some_time));
                    }
                }

            } catch (Exception e) {
                Log.v("log_tag", "LoginMCActivity Exception Error ", e);
            }

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        secondaryUserBtn.setVisibility(View.VISIBLE);
        secondaryUserBtn.startAnimation(slideLeft);
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
}
