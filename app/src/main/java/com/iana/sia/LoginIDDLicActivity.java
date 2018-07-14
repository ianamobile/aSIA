package com.iana.sia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.iana.sia.model.SIASecurityObj;
import com.iana.sia.model.User;
import com.iana.sia.utility.ApiResponse;
import com.iana.sia.utility.ApiResponseMessage;
import com.iana.sia.utility.GlobalVariables;
import com.iana.sia.utility.Internet_Check;
import com.iana.sia.utility.RestApiClient;
import com.iana.sia.utility.SIAUtility;

public class LoginIDDLicActivity extends AppCompatActivity implements Animation.AnimationListener {

    private String urlResponse;
    private int urlResponseCode;

    private ProgressBar progressBar;

    Button backToHomeBtn;
    Button loginBtn;

    EditText drvLicState;

    Animation slideLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_iddlic);

        slideLeft = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.set_in_left);
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

        drvLicState = findViewById(R.id.drvLicState);
        SIAUtility.setUpperCase(drvLicState);
        drvLicState.setOnKeyListener(new View.OnKeyListener() {
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
            String drvLicNo          = ((EditText) findViewById(R.id.drvLicNo)).getText().toString();
            String drvLicState          = ((EditText) findViewById(R.id.drvLicState)).getText().toString();

            String error = validateLoginFields(scac, drvLicNo, drvLicState);
            if(error == "") {

                User user = new User();

                user.setScac(scac.trim());
                user.setDriverLicenseNumber(SIAUtility.replaceWhiteSpaces(drvLicNo.trim()));
                user.setDriverLicenseState(SIAUtility.replaceWhiteSpaces(drvLicState.trim()));
                user.setRole(GlobalVariables.ROLE_IDD);
                user.setOriginFrom(GlobalVariables.ORIGIN_FROM_DRVLIC_STATE_SCAC);

                Gson gson = new Gson();
                String jsonInString = gson.toJson(user, User.class);
                new LoginIDDLicActivity.ExecuteTask(jsonInString).execute();

            } else {
                new ViewDialog().showDialog(LoginIDDLicActivity.this, getString(R.string.dialog_title_idd_login), error);

            }

        } else {
            Intent intent = new Intent(LoginIDDLicActivity.this, NoInternetActivity.class);
            startActivity(intent);
        }
    }

    private String validateLoginFields(String scac, String drvLicNo, String drvLicState) {
        if((scac == null || scac == "" || scac.toString().trim().length() <= 0) &&
            (drvLicNo == null || drvLicNo == "" || drvLicNo.toString().trim().length() <= 0) &&
            (drvLicState == null || drvLicState == "" || drvLicState.toString().trim().length() <= 0)) {
            return getString(R.string.msg_error_all_mandatory);
        }

        if(scac == null || scac == "" || scac.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_empty_scac);

        } else if (!SIAUtility.isAlpha(scac)) {
            return getString(R.string.msg_error_char_scac);

        } else if (scac.length() != 4) {
            return getString(R.string.msg_error_length_scac);
        }

        if(drvLicNo == null || drvLicNo == "" || drvLicNo.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_empty_drvLicNo);

        } else if(!SIAUtility.isAlphaNumeric(drvLicNo)) {
            return getString(R.string.msg_error_alphanum_drvLicNo);
        }

        if(drvLicState == null || drvLicState == "" || drvLicState.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_empty_drvLicState);

        } else if(!SIAUtility.isAlpha(drvLicState)) {
            return getString(R.string.msg_error_char_drvLicState);

        } else if(drvLicState.length() != 2) {
            return getString(R.string.msg_error_length_drvLicState);
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
                Log.v("log_tag", " IDD License Login Response Code: " + urlResponseCode);
                Gson gson = new Gson();


                if (urlResponseCode == 200) {

                    SIASecurityObj siaSecurityObj = gson.fromJson(result, SIASecurityObj.class);

                    /* Code to store login information start */
                    SharedPreferences sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(GlobalVariables.KEY_ORIGIN_FROM, GlobalVariables.ORIGIN_FROM_DRVLIC_STATE_SCAC);
                    SIAUtility.setObject(editor, GlobalVariables.KEY_SECURITY_OBJ, siaSecurityObj);
                    editor.commit();

                    /* Code to store login information end */

                    Intent intent = new Intent(LoginIDDLicActivity.this, DashboardActivity.class);
                    startActivity(intent);

                    finish(); /* This method will not display login page once redirected to dashboard */

                } else {

                    try {
                        ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                        new ViewDialog().showDialog(LoginIDDLicActivity.this, getString(R.string.dialog_title_idd_login), errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                    } catch(Exception e) {
                        new ViewDialog().showDialog(LoginIDDLicActivity.this, getString(R.string.dialog_title_idd_login), getString(R.string.msg_error_try_after_some_time));
                    }
                }

            } catch (Exception e) {
                Log.v("log_tag", "LoginIDDLicActivity Exception Error ", e);
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
        startActivity(new Intent(LoginIDDLicActivity.this, LoginIDDActivity.class));
        finish();
    }

    // Mobile/Phone back key event
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            goToPreviousPage();
        }

        return super.onKeyDown(keyCode, event);
    }

}
