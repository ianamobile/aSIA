package com.iana.sia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import com.google.gson.Gson;
import com.iana.sia.model.SIASecurityObj;
import com.iana.sia.model.User;
import com.iana.sia.utility.ApiResponse;
import com.iana.sia.utility.ApiResponseMessage;
import com.iana.sia.utility.GlobalVariables;
import com.iana.sia.utility.Internet_Check;
import com.iana.sia.utility.RestApiClient;
import com.iana.sia.utility.SIAUtility;

public class LoginIDDActivity extends AppCompatActivity implements Animation.AnimationListener {

    String urlResponse;
    int urlResponseCode;

    Button loginBtn;

    EditText iddPinEditText;

    Button secondaryUserBtn;

    ProgressBar progressBar;

    Animation slideLeft;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_idd);

        context = this;

        secondaryUserBtn = findViewById(R.id.secondaryUserBtn);

        slideLeft = AnimationUtils.loadAnimation(context, R.anim.set_in_left);
        slideLeft.setAnimationListener(this);

        BottomNavigationView bnv = findViewById(R.id.navigation_login);
        bnv.setSelectedItemId(R.id.navigation_login_idd);

        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_login_mc:
                        startActivity(new Intent(LoginIDDActivity.this, LoginMCActivity.class));
                        finish(); /* This method will not display login page when click back (return) from phone */
                        break;
                    case R.id.navigation_login_ep:
                        startActivity(new Intent(LoginIDDActivity.this, LoginEPActivity.class));
                        finish(); /* This method will not display login page when click back (return) from phone */
                        break;
                    case R.id.navigation_login_tpu:
                        startActivity(new Intent(LoginIDDActivity.this, LoginTPUActivity.class));
                        finish(); /* This method will not display login page when click back (return) from phone */
                        break;                }

                return true;
            }
        });

        SIAUtility.disableShiftMode(bnv);
        progressBar = findViewById(R.id.processingBar);

        // below code is used to restrict auto populate keypad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        secondaryUserBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                startActivity(new Intent(LoginIDDActivity.this, LoginIDDLicActivity.class));
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

        iddPinEditText = findViewById(R.id.iddPin);
        iddPinEditText.setOnKeyListener(new View.OnKeyListener() {
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
        if (Internet_Check.checkInternetConnection(context)) {

            String scac          = ((EditText) findViewById(R.id.scac)).getText().toString();
            String iddPin          = ((EditText) findViewById(R.id.iddPin)).getText().toString();

            String error = validateLoginFields(scac, iddPin);
            if(error.equalsIgnoreCase("")) {

                User user = new User();

                user.setScac(scac.trim());
                user.setIddPin(iddPin);
                user.setRole(GlobalVariables.ROLE_IDD);
                user.setOriginFrom(GlobalVariables.ORIGIN_FROM_IDDPIN_SCAC);

                // code to disable background functionality when progress bar starts
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                Gson gson = new Gson();
                String jsonInString = gson.toJson(user, User.class);
                new ExecuteTask(jsonInString).execute();

            } else {
                new ViewDialog().showDialog(LoginIDDActivity.this, getString(R.string.dialog_title_idd_login), error);

            }

        } else {
            Intent intent = new Intent(LoginIDDActivity.this, NoInternetActivity.class);
            startActivity(intent);
        }
    }

    private String validateLoginFields(String scac, String iddPin) {
        if((scac == null || scac.equalsIgnoreCase("") || scac.trim().length() <= 0) &&
                (iddPin == null || iddPin.equalsIgnoreCase("") || iddPin.trim().length() <= 0)) {
            return getString(R.string.msg_error_empty_pin_scac);
        }

        if(scac == null || scac.equalsIgnoreCase("") || scac.trim().length() <= 0) {
            return getString(R.string.msg_error_empty_scac);

        } else if (!SIAUtility.isAlpha(scac)) {
            return getString(R.string.msg_error_char_scac);

        } else if (scac.length() != 4) {
            return getString(R.string.msg_error_length_scac);
        }

        if(iddPin == null || iddPin.equalsIgnoreCase("") || iddPin.trim().length() <= 0) {
            return getString(R.string.msg_error_empty_iddpin);
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
                Log.v("log_tag", " IDD PIN Login Response Code: " + urlResponseCode);
                Gson gson = new Gson();


                if (urlResponseCode == 200) {

                    SIASecurityObj siaSecurityObj = gson.fromJson(result, SIASecurityObj.class);

                    /* Code to store login information start */
                    SharedPreferences sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(GlobalVariables.KEY_ORIGIN_FROM, GlobalVariables.ORIGIN_FROM_IDDPIN_SCAC);
                    SIAUtility.setObject(editor, GlobalVariables.KEY_SECURITY_OBJ, siaSecurityObj);
                    editor.commit();

                    /* Code to store login information end */

                    Intent intent = new Intent(LoginIDDActivity.this, DashboardActivity.class);
                    startActivity(intent);

                    finish(); /* This method will not display login page once redirected to dashboard */

                } else {

                    try {
                        ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                        new ViewDialog().showDialog(LoginIDDActivity.this, getString(R.string.dialog_title_idd_login), errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                    } catch(Exception e) {
                        new ViewDialog().showDialog(LoginIDDActivity.this, getString(R.string.dialog_title_idd_login), getString(R.string.msg_error_try_after_some_time));
                    }
                }

            } catch (Exception e) {
                Log.v("log_tag", "LoginIDDActivity Exception Error ", e);
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
