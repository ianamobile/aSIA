package com.iana.sia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
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

public class LoginSECActivity extends AppCompatActivity implements Animation.AnimationListener {

    private String urlResponse;
    private int urlResponseCode;

    private ProgressBar progressBar;

    Button backToHomeBtn;
    Button loginBtn;

    TextView troubleSignOn;

    EditText passwordEditText;

    Animation slideLeft;

    String dialogTitle = "";
    String role = "";

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sec);

        slideLeft = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.set_in_left);
        slideLeft.setAnimationListener(this);

        progressBar = findViewById(R.id.processingBar);

        // below code is used to restrict auto populate keypad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);

        Log.v("log_tag", "LoginSECActivity GlobalVariables.KEY_ORIGIN_FROM: " + sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, ""));

        if(sharedPref != null &&
            (sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "").equalsIgnoreCase(GlobalVariables.ROLE_MC) ||
                sharedPref.getString(GlobalVariables.KEY_MEM_TYPE, "").equalsIgnoreCase(GlobalVariables.ROLE_MC))) {

            dialogTitle = getString(R.string.dialog_title_mc_sec_login);
            role = GlobalVariables.ROLE_MC;

        } else if(sharedPref != null &&
            (sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "").equalsIgnoreCase(GlobalVariables.ROLE_EP)  ||
                sharedPref.getString(GlobalVariables.KEY_MEM_TYPE, "").equalsIgnoreCase(GlobalVariables.ROLE_EP))) {

            dialogTitle = getString(R.string.dialog_title_ep_sec_login);
            role = GlobalVariables.ROLE_EP;
        }


        backToHomeBtn = findViewById(R.id.backToHomeBtn);
        backToHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(role.equalsIgnoreCase(GlobalVariables.ROLE_MC)) {
                    // Perform action on click
                    startActivity(new Intent(LoginSECActivity.this, LoginMCActivity.class));

                } else if(role.equalsIgnoreCase(GlobalVariables.ROLE_EP)) {
                    // Perform action on click
                    startActivity(new Intent(LoginSECActivity.this, LoginEPActivity.class));
                }

                finish();

            }
        });

        troubleSignOn = findViewById(R.id.troubleSignOn);
        troubleSignOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(GlobalVariables.KEY_MEM_TYPE, role);
                editor.commit();

                startActivity(new Intent(LoginSECActivity.this, ForgotPasswordSECActivity.class));
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
            String userName          = ((EditText) findViewById(R.id.userName)).getText().toString();
            String password          = ((EditText) findViewById(R.id.password)).getText().toString();

            String error = validateLoginFields(scac, userName, password);
            if(error == "") {

                User user = new User();

                user.setScac(scac.trim());
                user.setUserName(SIAUtility.replaceWhiteSpaces(userName.trim()));
                user.setPassword(SIAUtility.replaceWhiteSpaces(password.trim()));
                user.setRole(GlobalVariables.ROLE_SEC);
                user.setMemType(role.equalsIgnoreCase(GlobalVariables.ROLE_MC) ? GlobalVariables.ROLE_MC : GlobalVariables.ROLE_EP);

                // code to disable background functionality when progress bar starts
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                Gson gson = new Gson();
                String jsonInString = gson.toJson(user, User.class);
                new LoginSECActivity.ExecuteTask(jsonInString).execute();

            } else {

                new ViewDialog().showDialog(LoginSECActivity.this, dialogTitle, error);

            }

        } else {
            Intent intent = new Intent(LoginSECActivity.this, NoInternetActivity.class);
            startActivity(intent);
        }
    }

    private String validateLoginFields(String scac, String userName, String password) {
        if(scac == null || scac == "" || scac.toString().trim().length() <= 0 ||
            password == null || password == "" || password.toString().trim().length() <= 0 ||
            userName == null || userName == "" || userName.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_all_mandatory);
        }

        if(scac == null || scac == "" || scac.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_blank_scac);

        } else if (!SIAUtility.isAlpha(scac)) {
            return getString(R.string.msg_error_char_scac);

        } else if (role.equalsIgnoreCase(GlobalVariables.ROLE_MC) && scac.length() != 4) {
            return getString(R.string.msg_error_length_scac);

        } else if (role.equalsIgnoreCase(GlobalVariables.ROLE_EP) &&
                !(scac.length() >= 2 && scac.length() <= 4) ) {
            return getString(R.string.msg_error_length_ep_scac);
        }

        if(userName == null || userName == "" || userName.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_blank_userName);
        }

        if(password == null || password == "" || password.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_blank_password);
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
                Log.v("log_tag", dialogTitle + " Response Code: " + urlResponseCode);
                Gson gson = new Gson();


                if (urlResponseCode == 200) {

                    SIASecurityObj siaSecurityObj = gson.fromJson(result, SIASecurityObj.class);

                    /* Code to store login information start */

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(GlobalVariables.KEY_ORIGIN_FROM, GlobalVariables.ROLE_SEC);
                    SIAUtility.setObject(editor, GlobalVariables.KEY_SECURITY_OBJ, siaSecurityObj);
                    editor.commit();

                    /* Code to store login information end */

                    Intent intent = new Intent(LoginSECActivity.this, DashboardActivity.class);
                    startActivity(intent);

                    finish(); /* This method will not display login page once redirected to dashboard */

                } else {

                    try {
                        ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                        new ViewDialog().showDialog(LoginSECActivity.this, dialogTitle, errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                    } catch(Exception e) {
                        new ViewDialog().showDialog(LoginSECActivity.this, dialogTitle, getString(R.string.msg_error_try_after_some_time));
                    }

                }

            } catch (Exception e) {
                Log.v("log_tag", dialogTitle + " LoginSECActivity Exception Error ", e);
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

}
