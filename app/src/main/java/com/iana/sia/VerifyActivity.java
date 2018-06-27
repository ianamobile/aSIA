package com.iana.sia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iana.sia.model.FieldInfo;
import com.iana.sia.model.InterchangeRequests;
import com.iana.sia.utility.ApiResponse;
import com.iana.sia.utility.ApiResponseMessage;
import com.iana.sia.utility.GlobalVariables;
import com.iana.sia.utility.Internet_Check;
import com.iana.sia.utility.RestApiClient;
import com.iana.sia.utility.SIAUtility;

import java.util.ArrayList;
import java.util.List;

public class VerifyActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    String urlResponse;
    int urlResponseCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        showActionBar();
        ((TextView) findViewById(R.id.title)).setText(R.string.title_verify_details);

        progressBar = findViewById(R.id.processingBar);

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        // below code is used to restrict auto populate keypad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        processViewContent();

        BottomNavigationView bnv = findViewById(R.id.navigation_edit_submit);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (Internet_Check.checkInternetConnection(getApplicationContext())) {

                    switch (item.getItemId()) {
                        case R.id.navigation_edit:

                            String requestOriginFrom = sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "");
                            if(GlobalVariables.ORIGIN_FROM_STREET_TURN.equalsIgnoreCase(requestOriginFrom)) {

                                startActivity(new Intent(VerifyActivity.this, StreetTurnActivity.class));
                                finish(); /* This method will not display login page when click back (return) */

                            } else if(GlobalVariables.ORIGIN_FROM_STREET_INTERCHANGE.equalsIgnoreCase(requestOriginFrom)) {

                                startActivity(new Intent(VerifyActivity.this, InitiateInterchangeActivity.class));
                                finish(); /* This method will not display login page when click back (return) */
                            }

                            editor.putString(GlobalVariables.KEY_RETURN_FROM, GlobalVariables.RETURN_FROM_VERIFY_DETAILS);
                            editor.commit();

                            break;
                        case R.id.navigation_submit:

                            submitRequest();
                            break;
                    }

                } else {
                    Intent intent = new Intent(VerifyActivity.this, NoInternetActivity.class);
                    startActivity(intent);
                }

                return true;
            }
        });

    }

    private void submitRequest() {
        String requestOriginFrom = sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "");
        if(GlobalVariables.ORIGIN_FROM_STREET_TURN.equalsIgnoreCase(requestOriginFrom) ||
            GlobalVariables.ORIGIN_FROM_STREET_INTERCHANGE.equalsIgnoreCase(requestOriginFrom)) {

            // code to disable background functionality when progress bar starts
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            InterchangeRequests ir = (InterchangeRequests) readObjectOfModel("interchangeRequestObject");
            Gson gson = new Gson();
            new VerifyActivity.ExecuteTaskSubmit(gson.toJson(ir, InterchangeRequests.class)).execute();
        }
    }
    public List<FieldInfo> readListOfModel() {
        Gson gson = new Gson();
        return (gson.fromJson(sharedPref.getString("fieldInfoList", "[]"),
                new TypeToken<List<FieldInfo>>() {
                }.getType()));
    }

    public Object readObjectOfModel(String string) {
        Gson gson = new Gson();
        return (gson.fromJson(sharedPref.getString(string, ""), InterchangeRequests.class));
    }

    private void processViewContent() {
        TableLayout tl = findViewById(R.id.tableLayout);

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        List<FieldInfo> fieldInfoList =  readListOfModel();

        for(int i=0; i < fieldInfoList.size();i++) {
            FieldInfo fieldInfo = fieldInfoList.get(i);
            TableRow row;
            if(fieldInfo.getTitle().equalsIgnoreCase(GlobalVariables.FIELD_INFO_EMPTY)) {
                row = (TableRow)LayoutInflater.from(VerifyActivity.this).inflate(R.layout.verify_heading, null);
                ((TextView)row.findViewById(R.id.title)).setText(fieldInfo.getValue());
                tl.addView(row);

            } else if(fieldInfo.getTitle().equalsIgnoreCase(GlobalVariables.FIELD_INFO_TITLE)) {
                row = (TableRow)LayoutInflater.from(VerifyActivity.this).inflate(R.layout.verify_content, null);
                ((TextView)row.findViewById(R.id.locationLbl)).setText(fieldInfo.getValue());

                fieldInfo = fieldInfoList.get(++i);
                ((TextView)row.findViewById(R.id.locationValue)).setText(fieldInfo.getValue());
                tl.addView(row);

            } else  if(fieldInfo.getTitle().equalsIgnoreCase(GlobalVariables.FIELD_INFO_BLANK)) {
                row = new TableRow(this);
                LinearLayout ll = new LinearLayout(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 40, 1f);
                lp.setMargins(0, 10, 0, 0);
                ll.setLayoutParams(lp);
                ll.setBackground(getDrawable(R.drawable.cell_shape));
                row.addView(ll);
                tl.addView(row);

            }
        }

    }

    private void showActionBar() {
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.ab_custom, null);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled (false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(v);
    }

    class ExecuteTaskSubmit extends AsyncTask<String, Integer, String> {
        String requestString;

        public ExecuteTaskSubmit(String requestString) {
            this.requestString = requestString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            String requestOriginFrom = sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "");
            ApiResponse apiResponse;
            if(GlobalVariables.ORIGIN_FROM_STREET_TURN.equalsIgnoreCase(requestOriginFrom) || GlobalVariables.ORIGIN_FROM_STREET_INTERCHANGE.equalsIgnoreCase(requestOriginFrom)) {
                apiResponse = RestApiClient.callPostApi(requestString, getString(R.string.base_url) +getString(R.string.api_save_initiate_interchange_details));

            } else if(GlobalVariables.ORIGIN_FROM_NOTIF_AVAIl.equalsIgnoreCase(requestOriginFrom)) {
                apiResponse = RestApiClient.callPostApi(requestString, getString(R.string.base_url) +getString(R.string.api_add_equipment_to_pool));

            } else {
                return urlResponse;
            }

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
                Log.v("log_tag", "urlResponseCode:=>" + urlResponseCode);
                Log.v("log_tag", "result:=> " + result);
                Gson gson = new Gson();

                ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);

                if (urlResponseCode == 200) {

                    String requestOriginFrom = sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "");
                    if (GlobalVariables.ORIGIN_FROM_STREET_TURN.equalsIgnoreCase(requestOriginFrom)) {
                        editor.putString(GlobalVariables.SUCCESS, getString(R.string.success_msg_street_turn));

                    } else if (GlobalVariables.ORIGIN_FROM_STREET_INTERCHANGE.equalsIgnoreCase(requestOriginFrom)) {
                        editor.putString(GlobalVariables.SUCCESS, getString(R.string.success_msg_street_interchange));
                    }

                    editor.commit();

                    Intent intent = new Intent(VerifyActivity.this, SuccessActivity.class);
                    startActivity(intent);
                    finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */

                } else if (urlResponseCode != 0) {

                    try {
                        String requestOriginFrom = sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "");
                        if (GlobalVariables.ORIGIN_FROM_STREET_TURN.equalsIgnoreCase(requestOriginFrom)) {
                            new ViewDialog().showDialog(VerifyActivity.this, getString(R.string.dialog_title_verify_details), errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());
                        }
                    } catch(Exception e){
                        new ViewDialog().showDialog(VerifyActivity.this, getString(R.string.dialog_title_verify_details), getString(R.string.msg_error_try_after_some_time));
                    }

                } else {
                    new ViewDialog().showDialog(VerifyActivity.this, getString(R.string.dialog_title_verify_details), getString(R.string.msg_error_try_after_some_time));
                }

            } catch (Exception e) {
                Log.v("log_tag", "VerifyActivity Exception Error ", e);
            }

        }

    }
}
