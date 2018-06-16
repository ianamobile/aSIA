package com.iana.sia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.iana.sia.model.Company;
import com.iana.sia.utility.ApiResponse;
import com.iana.sia.utility.ApiResponseMessage;
import com.iana.sia.utility.GlobalVariables;
import com.iana.sia.utility.RestApiClient;
import com.iana.sia.utility.SIAUtility;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class StreetTurnActivity extends AppCompatActivity {

    String urlResponse;
    int urlResponseCode;

    ImageView search;
    Button backBtn;

    EditText epScac;
    EditText mcScac;
    EditText iepScac;
    EditText containerNumber;
    EditText exportBookingNumber;
    EditText importBL;
    EditText chassisNumber;
    EditText locationName;
    EditText locationAddress;
    EditText city;
    EditText state;
    EditText zipCode;
    AutoCompleteTextView mcCompanyName;
    AutoCompleteTextView epCompanyName;
    ArrayAdapter<String> adapter;

    String oppositeRole;
    String role;
    String memType;

    private ProgressBar progressBar;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_turn);

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        role =  sharedPref.getString(GlobalVariables.KEY_ROLE, "");
        memType =  sharedPref.getString(GlobalVariables.KEY_MEM_TYPE, "");

        mcScac = findViewById(R.id.mcScac);
        mcCompanyName = findViewById(R.id.mcCompanyName);
        epScac = findViewById(R.id.epScac);
        epCompanyName = findViewById(R.id.epCompanyName);
        containerNumber = findViewById(R.id.containerNumber);
        exportBookingNumber = findViewById(R.id.exportBookingNumber);
        importBL = findViewById(R.id.importBL);
        chassisNumber = findViewById(R.id.chassisNumber);
        iepScac = findViewById(R.id.iepScac);

        epScac = findViewById(R.id.epScac);
        epScac.setFocusable(false);
        epScac.setClickable(false);
        epScac.setLongClickable(false);


        mcScac = findViewById(R.id.mcScac);
        mcScac.setFocusable(false);
        mcScac.setClickable(false);
        mcScac.setLongClickable(false);

        iepScac = findViewById(R.id.iepScac);
        iepScac.setFocusable(false);
        iepScac.setClickable(false);
        iepScac.setLongClickable(false);


        locationName = findViewById(R.id.locationName);
        locationName.setFocusable(false);
        locationName.setClickable(false);
        locationName.setLongClickable(false);

        locationAddress = findViewById(R.id.locationAddress);
        locationAddress.setFocusable(false);
        locationAddress.setClickable(false);
        locationAddress.setLongClickable(false);

        city = findViewById(R.id.city);
        city.setFocusable(false);
        city.setClickable(false);
        city.setLongClickable(false);

        state = findViewById(R.id.state);
        state.setFocusable(false);
        state.setClickable(false);
        state.setLongClickable(false);

        zipCode = findViewById(R.id.zipCode);
        zipCode.setFocusable(false);
        zipCode.setClickable(false);
        zipCode.setLongClickable(false);


        if(sharedPref.getString(GlobalVariables.KEY_RETURN_FROM, "").equalsIgnoreCase(GlobalVariables.RETURN_FROM_LOCATION_SEARCH)) {
            locationName.setText(sharedPref.getString(GlobalVariables.KEY_LOCATION_NAME, ""));
            locationAddress.setText(sharedPref.getString(GlobalVariables.KEY_LOCATION_ADDRESS, ""));
            zipCode.setText(sharedPref.getString(GlobalVariables.KEY_LOCATION_ZIP, ""));
            city.setText(sharedPref.getString(GlobalVariables.KEY_LOCATION_CITY, ""));
            state.setText(sharedPref.getString(GlobalVariables.KEY_LOCATION_STATE, ""));

            epScac.setText(sharedPref.getString(GlobalVariables.KEY_EP_SCAC, ""));
            epCompanyName.setText(sharedPref.getString(GlobalVariables.KEY_EP_COMPANY_NAME, ""));

            mcScac.setText(sharedPref.getString(GlobalVariables.KEY_EP_SCAC, ""));
            mcCompanyName.setText(sharedPref.getString(GlobalVariables.KEY_EP_COMPANY_NAME, ""));
            containerNumber.setText(sharedPref.getString(GlobalVariables.KEY_CONTAINER_NUMBER, ""));
            exportBookingNumber.setText(sharedPref.getString(GlobalVariables.KEY_EXPORT_BOOKING_NUMBER, ""));
            importBL.setText(sharedPref.getString(GlobalVariables.KEY_IMPORT_BL, ""));
            iepScac.setText(sharedPref.getString(GlobalVariables.KEY_IEP_SCAC, ""));
            chassisNumber.setText(sharedPref.getString(GlobalVariables.KEY_CHASSIS_ID, ""));

        } else {
            locationName.setText("");
            locationAddress.setText("");
            zipCode.setText("");
            city.setText("");
            state.setText("");

            epScac.setText("");
            epCompanyName.setText("");

            mcScac.setText("");
            mcCompanyName.setText("");
            containerNumber.setText("");
            exportBookingNumber.setText("");
            importBL.setText("");
            chassisNumber.setText("");
            iepScac.setText("");
        }

        showActionBar();
        ((TextView) findViewById(R.id.title)).setText(R.string.title_street_turn_request);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setText(R.string.title_back);
        backBtn.setVisibility(View.VISIBLE);

        progressBar = findViewById(R.id.processingBar);

        // below code is used to restrict auto populate keypad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // code is to search Original Location start
        search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {

                if(null != epScac.getText() && (epScac.getText().toString().length()>=2 && epScac.getText().toString().length() <= 4)) {
                    editor.putString(GlobalVariables.KEY_ORIGIN_FROM, GlobalVariables.ORIGIN_FROM_ORIGINAL);
                    editor.putString(GlobalVariables.KEY_EP_SCAC, epScac.getText().toString());
                    editor.putString(GlobalVariables.KEY_EP_COMPANY_NAME, epCompanyName.getText().toString());

                    editor.putString(GlobalVariables.KEY_MC_SCAC, mcScac.getText().toString());
                    editor.putString(GlobalVariables.KEY_MC_COMPANY_NAME, mcCompanyName.getText().toString());

                    editor.putString(GlobalVariables.KEY_CONTAINER_NUMBER, containerNumber.getText().toString());
                    editor.putString(GlobalVariables.KEY_EXPORT_BOOKING_NUMBER, exportBookingNumber.getText().toString());
                    editor.putString(GlobalVariables.KEY_IMPORT_BL, importBL.getText().toString());
                    editor.putString(GlobalVariables.KEY_CHASSIS_ID, chassisNumber.getText().toString());
                    editor.putString(GlobalVariables.KEY_IEP_SCAC, iepScac.getText().toString());

                    editor.commit();

                    Intent intent = new Intent(StreetTurnActivity.this, LocationActivity.class);
                    startActivity(intent);
                    finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */

                } else {
                    new ViewDialog().showDialog(StreetTurnActivity.this, getString(R.string.dialog_title_street_turn_request), getString(R.string.msg_error_enter_container_provider_name_first));
                }
            }

        });
        // code is to search Original Location end

        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(StreetTurnActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */
            }
        });

        if(GlobalVariables.ROLE_EP.equalsIgnoreCase(role) || GlobalVariables.ROLE_TPU.equalsIgnoreCase(role) ||
                (GlobalVariables.ROLE_SEC.equalsIgnoreCase(role) && GlobalVariables.ROLE_EP.equalsIgnoreCase(memType))) {

            epCompanyName = findViewById(R.id.epCompanyName);
            epCompanyName.setEnabled(false);
            epCompanyName.setText(sharedPref.getString(GlobalVariables.KEY_COMPANY_NAME, ""));
            epScac.setText(sharedPref.getString(GlobalVariables.KEY_SCAC, ""));
            epCompanyName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_gray));

            mcCompanyName = findViewById(R.id.mcCompanyName);
            mcCompanyName.setThreshold(2); //type char in after work....
            adapter = new LocationAdapter(this);

            mcCompanyName.setAdapter(adapter);

            oppositeRole = GlobalVariables.ROLE_MC;

            mcCompanyName.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View arg1, int position,
                                        long arg3) {
                    String selectedString = (String) parent.getItemAtPosition(position);

                    String[] selectedLocationArray = selectedString.split(Pattern.quote("|"));

                    ((AutoCompleteTextView) findViewById(R.id.mcCompanyName)).setText(selectedLocationArray[1]);

                    ((EditText) findViewById(R.id.mcScac)).setText(selectedLocationArray[0]);

                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    findViewById(R.id.containerNumber).requestFocus();
                }
            });

        } else if(GlobalVariables.ROLE_MC.equalsIgnoreCase(role) || GlobalVariables.ROLE_IDD.equalsIgnoreCase(role) ||
                (GlobalVariables.ROLE_SEC.equalsIgnoreCase(role) && GlobalVariables.ROLE_MC.equalsIgnoreCase(memType))) {

            mcCompanyName = findViewById(R.id.mcCompanyName);
            mcCompanyName.setEnabled(false);
            mcCompanyName.setText(sharedPref.getString(GlobalVariables.KEY_COMPANY_NAME, ""));
            mcScac.setText(sharedPref.getString(GlobalVariables.KEY_SCAC, ""));
            mcCompanyName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_gray));

            epCompanyName = findViewById(R.id.epCompanyName);
            epCompanyName.setThreshold(2); //type char in after work....
            adapter = new LocationAdapter(this);

            epCompanyName.setAdapter(adapter);
            oppositeRole = GlobalVariables.ROLE_EP;

            epCompanyName.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {

                    String selectedString = (String) parent.getItemAtPosition(position);
                    String[] selectedLocationArray = selectedString.split(Pattern.quote("|"));


                    ((AutoCompleteTextView) findViewById(R.id.epCompanyName)).setText(selectedLocationArray[1]);
                    ((EditText) findViewById(R.id.epScac)).setText(selectedLocationArray[0]);

                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    findViewById(R.id.containerNumber).requestFocus();
                }
            });
        }


        BottomNavigationView bnv = findViewById(R.id.navigation_next_cancel);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_next:
                        String returnMessage = validateFields();
                        if(!returnMessage.equalsIgnoreCase(GlobalVariables.SUCCESS)) {
                            new ViewDialog().showDialog(StreetTurnActivity.this, getString(R.string.dialog_title_street_turn_request), returnMessage);

                        } else {

                            editor.putString(GlobalVariables.KEY_ORIGIN_FROM, GlobalVariables.ORIGIN_FROM_STREET_TURN);
                            editor.putString(GlobalVariables.KEY_EP_SCAC, epScac.getText().toString());
                            editor.putString(GlobalVariables.KEY_EP_COMPANY_NAME, epCompanyName.getText().toString());
                            editor.commit();

                            startActivity(new Intent(StreetTurnActivity.this, VerifyStreetTurnActivity.class));
                            finish(); /* This method will not display login page when click back (return) from phone */
                        }
                        break;
                    case R.id.navigation_cancel:
                        editor.remove(GlobalVariables.KEY_RETURN_FROM);
                        editor.commit();
                        startActivity(new Intent(StreetTurnActivity.this, DashboardActivity.class));
                        finish(); /* This method will not display login page when click back (return) from phone */
                        break;
                }

                return true;
            }
        });

        SIAUtility.disableShiftMode(bnv);


        // below event is execute based on done button on mobile keypad
        chassisNumber = findViewById(R.id.chassisNumber);
        chassisNumber.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String requestString = "chassisId=" + chassisNumber.getText().toString().trim();
                    new ExecuteChassisIdTask(requestString).execute();
                    return true;
                }
                return false;
            }
        });
        /*chassisNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //here is your code
                if (null != s && s.toString().trim().length() > 1) {
                    String requestString = "chassisId=" + s.toString().trim();
                    new ExecuteChassisIdTask(requestString).execute();
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });*/

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

    String validateFields() {
        String mcScac = ((EditText)findViewById(R.id.mcScac)).getText().toString();
        String mcCompanyName = ((EditText)findViewById(R.id.mcCompanyName)).getText().toString();
        String epScac = ((EditText)findViewById(R.id.epScac)).getText().toString();
        String epCompanyName = ((EditText)findViewById(R.id.epCompanyName)).getText().toString();

        String containerNumber = ((EditText)findViewById(R.id.containerNumber)).getText().toString();
        String exportBookingNumber = ((EditText)findViewById(R.id.exportBookingNumber)).getText().toString();
        String importBL = ((EditText)findViewById(R.id.importBL)).getText().toString();
        String chassisNumber = ((EditText)findViewById(R.id.chassisNumber)).getText().toString();

        if(null == mcCompanyName || mcCompanyName.trim().toString().length() <= 0) {
            return getString(R.string.msg_error_empty_motor_carrier_name);
        }

        if(null == mcScac || mcScac.trim().toString().length() <= 0) {
            return getString(R.string.msg_error_select_motor_carrier_name);
        }

        if(null == epCompanyName || epCompanyName.trim().toString().length() <= 0) {
            return getString(R.string.msg_error_empty_container_provider_name);
        }

        if(null == epScac || epScac.trim().toString().length() <= 0) {
            return getString(R.string.msg_error_select_container_provider_name);
        }

        if(null == containerNumber || containerNumber.trim().toString().length() <= 0) {
            return getString(R.string.msg_error_empty_container_number);

        } else if(!SIAUtility.isValidSTContNum(containerNumber)) {
            return getString(R.string.msg_error_alpha_num_container_number_st);
        }

        if(null == exportBookingNumber || exportBookingNumber.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_empty_export_booking_number);

        } else if(!SIAUtility.isAlphaNumeric(exportBookingNumber)) {
            return getString(R.string.msg_error_alpha_num_export_booking_number);
        }

        if(null != importBL && importBL.toString().trim().length() > 0 && !SIAUtility.isAlphaNumeric(importBL)) {
            return getString(R.string.msg_error_alpha_num_import_booking_number);
        }

        if(null != chassisNumber && chassisNumber.toString().trim().length() > 0 && !SIAUtility.isAlphaNumeric(chassisNumber)) {
            return getString(R.string.msg_error_alpha_num_chassis_number);
        }

        return GlobalVariables.SUCCESS;
    }

    class LocationAdapter extends ArrayAdapter<String> {

        protected List<String> suggestions;

        public LocationAdapter(Activity context) {
            super(context, android.R.layout.simple_dropdown_item_1line);
            suggestions = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return suggestions.size();
        }

        @Override
        public String getItem(int index) {
            return suggestions.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter myFilter = new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {

                        final String jsonInString = "role="+oppositeRole+"&requestType="+getString(R.string.request_type_ir_request)+"&companyName="+constraint.toString();
//                        Log.v("log_tag", "In filterResults with jsonInString:=>"+jsonInString);
                        Thread timer = new Thread() { //new thread
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new ExecuteTask(jsonInString).execute();
                                    }
                                });
                            }
                        };
                        timer.start();
                        // Now assign the values and count to the FilterResults
                        // object
                        filterResults.values = suggestions;
                        filterResults.count = suggestions.size();
                    } else {
                        Log.v("log_tag", "In else when constraint null");
                        Thread timer = new Thread() { //new thread
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.v("log_tag", "In filterResults with oppositeRole:=>"+oppositeRole);
                                        if(GlobalVariables.ROLE_MC.equalsIgnoreCase(oppositeRole)) {
                                            ((EditText) findViewById(R.id.mcScac)).setText("");

                                        } else if(GlobalVariables.ROLE_EP.equalsIgnoreCase(oppositeRole)){
                                            Log.v("log_tag", "In else when constraint null set epScac is empty");
                                            ((EditText) findViewById(R.id.epScac)).setText("");
                                        }
                                    }
                                });
                            }
                        };
                        timer.start();

                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence contraint,
                                              FilterResults results) {
                    Log.v("log_tag", "publishResults results:=>"+results);
                    if (results != null && results.count > 0) {
                        Log.v("log_tag", "notifyDataSetChanged()");
                        notifyDataSetChanged();

                    } else {
                        Log.v("log_tag", "notifyDataSetChanged()");
                        notifyDataSetInvalidated();
                    }
                }
            };
            return myFilter;
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
                Log.v("log_tag", "api_get_companyname_and_scac_by_companyname:=>"+getString(R.string.base_url) + getString(R.string.api_get_companyname_and_scac_by_companyname) + "?" + requestString);
                ApiResponse apiResponse = RestApiClient.callGetApi(getString(R.string.base_url) + getString(R.string.api_get_companyname_and_scac_by_companyname) + "?" + requestString);
                urlResponse = apiResponse.getMessage();
                urlResponseCode = apiResponse.getCode();
                return urlResponse;
            }

            @Override
            protected void onPostExecute(String result) {
                progressBar.setVisibility(View.GONE);

                try {
                    Log.v("log_tag", "urlResponseCode:=>" + urlResponseCode);
                    Log.v("log_tag", "result:=> " + result);
                    Gson gson = new Gson();

                    if (urlResponseCode == 200) {

                        Type listType = new TypeToken<ArrayList<Company>>() {
                        }.getType();
                        List<Company> companyList = gson.fromJson(result, listType);
                        suggestions = new ArrayList<>();
                        for (int i = 0; i < companyList.size(); i++) {
                            suggestions.add(companyList.get(i).getScac() + "|" + companyList.get(i).getCompanyName());
                        }

                        notifyDataSetChanged();

                    } else if (urlResponseCode != 400) {

                        suggestions = new ArrayList<>();
                        notifyDataSetChanged();

                        ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);

                        new ViewDialog().showDialog(StreetTurnActivity.this, getString(R.string.dialog_title_street_turn_request), errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                        if(GlobalVariables.ROLE_EP.equalsIgnoreCase(oppositeRole)) {
                            Log.v("log_tag", "urlResponseCode != 200 epScac is empty:=> ");
                            epScac.setText("");

                        } else {
                            mcScac.setText("");
                        }
                    }

                } catch (Exception e) {
                    Log.v("log_tag", "Error ", e);
                }

            }

        }
    }

    class ExecuteChassisIdTask extends AsyncTask<String, Integer, String> {
        String requestString;

        public ExecuteChassisIdTask(String requestString) {
            this.requestString = requestString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            Log.v("log_tag", "api_get_iep_scac_by_chassis_id:=>"+getString(R.string.base_url) + getString(R.string.api_get_iep_scac_by_chassis_id) + "?" + requestString);
            ApiResponse apiResponse = RestApiClient.callGetApi(getString(R.string.base_url) + getString(R.string.api_get_iep_scac_by_chassis_id) + "?" + requestString);
            urlResponse = apiResponse.getMessage();
            urlResponseCode = apiResponse.getCode();
            return urlResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);

            try {
                Log.v("log_tag", "Chassis IEP Scac urlResponseCode:=>" + urlResponseCode);
                Log.v("log_tag", "Chassis IEP Scac result:=> " + result);
                Gson gson = new Gson();

                if (urlResponseCode == 200) {

                    JsonObject responseJson = gson.fromJson(result, JsonObject.class);
                    ((EditText) findViewById(R.id.iepScac)).setText(responseJson.get("iepScac").getAsString());
                } else if (urlResponseCode != 400) {
                    ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                    new ViewDialog().showDialog(StreetTurnActivity.this, getString(R.string.dialog_title_street_turn_request), errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());
                }

            } catch (Exception e) {
                Log.v("log_tag", "Error ", e);
            }

        }

    }

}
