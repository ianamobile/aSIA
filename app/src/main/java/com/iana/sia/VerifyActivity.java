package com.iana.sia;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iana.sia.model.FieldInfo;
import com.iana.sia.model.InterchangeRequests;
import com.iana.sia.model.NotificationAvail;
import com.iana.sia.model.SIASecurityObj;
import com.iana.sia.utility.ApiResponse;
import com.iana.sia.utility.ApiResponseMessage;
import com.iana.sia.utility.GlobalVariables;
import com.iana.sia.utility.Internet_Check;
import com.iana.sia.utility.RestApiClient;
import com.iana.sia.utility.SIAUtility;

import java.util.List;

public class VerifyActivity extends AppCompatActivity {

    ProgressBar progressBar;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    String urlResponse;
    int urlResponseCode;

    SIASecurityObj siaSecurityObj;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        context = this;

        SIAUtility.showActionBar(context, getSupportActionBar());

        ((TextView) findViewById(R.id.title)).setText(R.string.title_verify_details);
        ((TextView) findViewById(R.id.title)).setTextColor(ContextCompat.getColor(this, R.color.color_white));


        progressBar = findViewById(R.id.processingBar);

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        siaSecurityObj = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_SECURITY_OBJ, SIASecurityObj.class);

        // below code is used to restrict auto populate keypad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        processViewContent();

        BottomNavigationView bnv = findViewById(R.id.navigation_edit_submit);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (Internet_Check.checkInternetConnection(context)) {

                    switch (item.getItemId()) {
                        case R.id.navigation_edit:
                                goToPreviousPage();
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

        // Create custom dialog object
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_popup);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Set dialog title
        String dialogTitle = "";

        String requestOriginFrom = sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "");
        if(GlobalVariables.ORIGIN_FROM_STREET_TURN.equalsIgnoreCase(requestOriginFrom)) {
            dialogTitle = getString(R.string.dialog_title_street_turn_request);

        } else if(GlobalVariables.ORIGIN_FROM_STREET_INTERCHANGE.equalsIgnoreCase(requestOriginFrom)) {
            dialogTitle = getString(R.string.dialog_title_street_interchange_request);

        } else if(GlobalVariables.ORIGIN_FROM_NOTIF_AVAIl.equalsIgnoreCase(requestOriginFrom)) {
            dialogTitle = getString(R.string.dialog_title_add_equipment_to_pool);
        }

        ((TextView) dialog.findViewById(R.id.titleTextView)).setText(dialogTitle);
        ((TextView) dialog.findViewById(R.id.messageTextView)).setText(getString(R.string.dialog_submit_confirm_msg));

        dialog.show();

        Button declineButton = dialog.findViewById(R.id.noButton);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });

        Button acceptButton = dialog.findViewById(R.id.yesButton);
        // if decline button is clicked, close the custom dialog
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();

                String requestOriginFrom = sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "");
                if(GlobalVariables.ORIGIN_FROM_STREET_TURN.equalsIgnoreCase(requestOriginFrom) ||
                        GlobalVariables.ORIGIN_FROM_STREET_INTERCHANGE.equalsIgnoreCase(requestOriginFrom)) {

                    // code to disable background functionality when progress bar starts
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    InterchangeRequests ir = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_INTERCHANGE_REQUESTS_OBJ, InterchangeRequests.class);
                    Gson gson = new Gson();
                    new ExecuteTaskSubmit(gson.toJson(ir, InterchangeRequests.class)).execute();

                } else if(GlobalVariables.ORIGIN_FROM_NOTIF_AVAIl.equalsIgnoreCase(requestOriginFrom)) {

                    // code to disable background functionality when progress bar starts
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    NotificationAvail na = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_NOTIF_AVAIL_OBJ, NotificationAvail.class);
                    Gson gson = new Gson();
                    new ExecuteTaskSubmit(gson.toJson(na, NotificationAvail.class)).execute();
                }


            }
        });

    }
    public List<FieldInfo> readListOfModel() {
        Gson gson = new Gson();
        return (gson.fromJson(sharedPref.getString("fieldInfoList", "[]"),
                new TypeToken<List<FieldInfo>>() {
                }.getType()));
    }

    /*public Object readObjectOfModel(String string) {
        Gson gson = new Gson();
        return (gson.fromJson(sharedPref.getString(string, ""), InterchangeRequests.class));
    }*/

    private void processViewContent() {
        TableLayout tl = findViewById(R.id.tableLayout);

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        String chassisIEPSCACMsg = sharedPref.getString(GlobalVariables.KEY_IEP_SCAC_MESSAGE, "");

        List<FieldInfo> fieldInfoList =  readListOfModel();

        for(int i=0; i < fieldInfoList.size();i++) {
            FieldInfo fieldInfo = fieldInfoList.get(i);
            TableRow row;
            if(fieldInfo.getTitle().equalsIgnoreCase(GlobalVariables.FIELD_INFO_EMPTY)) {
                row = (TableRow)LayoutInflater.from(VerifyActivity.this).inflate(R.layout.verify_heading, null);
                ((TextView)row.findViewById(R.id.title)).setText(fieldInfo.getValue());
                ((TextView)row.findViewById(R.id.title)).setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

                tl.addView(row);

            } else if(fieldInfo.getTitle().equalsIgnoreCase(GlobalVariables.FIELD_INFO_TITLE)) {
                row = (TableRow)LayoutInflater.from(VerifyActivity.this).inflate(R.layout.verify_content, null);
                String chassisIEPSCACLbl = fieldInfo.getValue();
                ((TextView)row.findViewById(R.id.locationLbl)).setText(fieldInfo.getValue());
                ((TextView)row.findViewById(R.id.locationLbl)).setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

                fieldInfo = fieldInfoList.get(++i);
                if(chassisIEPSCACLbl.equalsIgnoreCase("CHASSIS IEP SCAC") && chassisIEPSCACMsg.trim().length() > 0) {
                    ((TextView)row.findViewById(R.id.locationValue)).setText(fieldInfo.getValue() + Html.fromHtml("<br/>"+chassisIEPSCACMsg));
                } else {
                    ((TextView) row.findViewById(R.id.locationValue)).setText(fieldInfo.getValue());
                }
                ((TextView) row.findViewById(R.id.locationValue)).setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
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

    private class ExecuteTaskSubmit extends AsyncTask<String, Integer, String> {
        String requestString;

        private ExecuteTaskSubmit(String requestString) {
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

                    editor.putString(GlobalVariables.SUCCESS, errorMessage.getMessage());

                    String requestOriginFrom = sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "");
                    if (GlobalVariables.ORIGIN_FROM_STREET_INTERCHANGE.equalsIgnoreCase(requestOriginFrom) ||
                        GlobalVariables.ORIGIN_FROM_STREET_TURN.equalsIgnoreCase(requestOriginFrom)) {

                        InterchangeRequests ir = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_INTERCHANGE_REQUESTS_OBJ, InterchangeRequests.class);
                        if(GlobalVariables.INITIATOR_MCA.equalsIgnoreCase(findInitiater(siaSecurityObj.getScac(), ir, siaSecurityObj.getRoleName(), siaSecurityObj.getMemType()))) {
                            editor.putString("isStreetInterchangeInitiatedByMCA", "true");

                        } else {
                            editor.putString("isStreetInterchangeInitiatedByMCA", "false");

                        }
                    }

                    editor.commit();

                    Intent intent = new Intent(VerifyActivity.this, SuccessActivity.class);
                    startActivity(intent);
                    finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */

                } else {

                    try {
                        new ViewDialog().showDialog(VerifyActivity.this, getString(R.string.dialog_title_verify_details), errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());
                    } catch(Exception e){
                        new ViewDialog().showDialog(VerifyActivity.this, getString(R.string.dialog_title_verify_details), getString(R.string.msg_error_try_after_some_time));
                    }
                }

            } catch (Exception e) {
                Log.v("log_tag", "VerifyActivity Exception Error ", e);
            }

        }

    }

    void goToPreviousPage() {
        if (Internet_Check.checkInternetConnection(context)) {

            String requestOriginFrom = sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "");

            if(GlobalVariables.ORIGIN_FROM_STREET_TURN.equalsIgnoreCase(requestOriginFrom)) {
                startActivity(new Intent(VerifyActivity.this, StreetTurnActivity.class));

            } else if(GlobalVariables.ORIGIN_FROM_STREET_INTERCHANGE.equalsIgnoreCase(requestOriginFrom)) {
                startActivity(new Intent(VerifyActivity.this, InitiateInterchangeActivity.class));

            } else if(GlobalVariables.ORIGIN_FROM_NOTIF_AVAIl.equalsIgnoreCase(requestOriginFrom)) {
                startActivity(new Intent(VerifyActivity.this, NotifAvailActivity.class));
            }

            editor.putString(GlobalVariables.KEY_RETURN_FROM, GlobalVariables.RETURN_FROM_VERIFY_DETAILS);
            editor.commit();

            finish(); /* This method will not display login page when click back (return) */

        } else {
            Intent intent = new Intent(VerifyActivity.this, NoInternetActivity.class);
            startActivity(intent);
        }
    }

    // Mobile/Phone back key event
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            goToPreviousPage();
        }

        return super.onKeyDown(keyCode, event);
    }

    private String findInitiater(String scac, InterchangeRequests ir, String roleName, String memType) {

        if((GlobalVariables.ROLE_MC.equalsIgnoreCase(roleName) || (GlobalVariables.ROLE_SEC.equalsIgnoreCase(roleName)
                && GlobalVariables.ROLE_MC.equalsIgnoreCase(memType)) || GlobalVariables.ROLE_IDD.equalsIgnoreCase(roleName))
                && scac.equalsIgnoreCase(ir.getMcBScac())){
            return GlobalVariables.INITIATOR_MCB;

        }else if(GlobalVariables.ROLE_EP.equalsIgnoreCase(roleName) || (GlobalVariables.ROLE_SEC.equalsIgnoreCase(roleName)
                && GlobalVariables.ROLE_EP.equalsIgnoreCase(memType)) || GlobalVariables.ROLE_TPU.equalsIgnoreCase(roleName)){
            return GlobalVariables.INITIATOR_EP;

        }else if(GlobalVariables.ROLE_MC.equalsIgnoreCase(roleName) || (GlobalVariables.ROLE_SEC.equalsIgnoreCase(roleName)
                && GlobalVariables.ROLE_MC.equalsIgnoreCase(memType)) || GlobalVariables.ROLE_IDD.equalsIgnoreCase(roleName)){
            return GlobalVariables.INITIATOR_MCA;

        }
        return "";
    }

}
