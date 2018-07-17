package com.iana.sia;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.iana.sia.model.Company;
import com.iana.sia.model.FieldInfo;
import com.iana.sia.model.InterchangeRequests;
import com.iana.sia.model.NotificationAvail;
import com.iana.sia.model.SIASecurityObj;
import com.iana.sia.model.User;
import com.iana.sia.utility.ApiResponse;
import com.iana.sia.utility.ApiResponseMessage;
import com.iana.sia.utility.GlobalVariables;
import com.iana.sia.utility.Internet_Check;
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

    ProgressBar progressBar;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    SIASecurityObj siaSecurityObj;

    InterchangeRequests ir;

    Context context;

    String dialogTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_turn);

        context = this;

        dialogTitle = getString(R.string.dialog_title_street_turn_request);

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        siaSecurityObj = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_SECURITY_OBJ, SIASecurityObj.class);

        ir = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_INTERCHANGE_REQUESTS_OBJ, InterchangeRequests.class);

        if(ir == null) {
            ir = new InterchangeRequests();
        }

        mcScac = findViewById(R.id.mcScac);
        mcCompanyName = findViewById(R.id.mcCompanyName);
        epScac = findViewById(R.id.epScac);
        epCompanyName = findViewById(R.id.epCompanyName);
        containerNumber = findViewById(R.id.containerNumber);
        SIAUtility.setUpperCase(containerNumber);

        exportBookingNumber = findViewById(R.id.exportBookingNumber);
        importBL = findViewById(R.id.importBL);
        chassisNumber = findViewById(R.id.chassisNumber);
        SIAUtility.setUpperCase(chassisNumber);

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


        locationName.setText(ir.getOriginLocNm());
        locationAddress.setText(ir.getOriginLocAddr());
        zipCode.setText(ir.getOriginLocZip());
        city.setText(ir.getOriginLocCity());
        state.setText(ir.getOriginLocState());

        epScac.setText(ir.getEpScacs());
        epCompanyName.setText(ir.getEpCompanyName());

        mcScac.setText(ir.getMcAScac());
        mcCompanyName.setText(ir.getMcACompanyName());
        containerNumber.setText(ir.getContNum());
        exportBookingNumber.setText(ir.getBookingNum());
        importBL.setText(ir.getImportBookingNum());
        chassisNumber.setText(ir.getChassisNum());
        iepScac.setText(ir.getIepScac());

        showActionBar();
        ((TextView) findViewById(R.id.title)).setText(R.string.title_street_turn_request);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setText(R.string.title_back);
        backBtn.setVisibility(View.VISIBLE);

        progressBar = findViewById(R.id.processingBar);

        // below code is used to restrict auto populate keypad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        containerNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //here is your code

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
        });

        // code is to search Original Location start
        search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {

            if (Internet_Check.checkInternetConnection(getApplicationContext())) {

                if (null != epScac.getText() && (epScac.getText().toString().length() >= 2 && epScac.getText().toString().length() <= 4)) {

                    ir.setEpScacs(epScac.getText().toString());
                    ir.setEpCompanyName(epCompanyName.getText().toString());
                    ir.setMcAScac(mcScac.getText().toString());
                    ir.setMcACompanyName(mcCompanyName.getText().toString());
                    ir.setContNum(containerNumber.getText().toString());
                    ir.setBookingNum(exportBookingNumber.getText().toString());
                    ir.setImportBookingNum(importBL.getText().toString());
                    ir.setChassisNum(chassisNumber.getText().toString());
                    ir.setIepScac(iepScac.getText().toString());

                    Log.v("log_tag", "ir before StreerTurn search for Location:"+ir);

                    SIAUtility.setObject(editor, GlobalVariables.KEY_INTERCHANGE_REQUESTS_OBJ, ir);

                    editor.putString(GlobalVariables.KEY_ORIGIN_FROM, GlobalVariables.ORIGIN_FROM_STREET_TURN);

                    editor.commit();

                    Intent intent = new Intent(StreetTurnActivity.this, LocationActivity.class);
                    startActivity(intent);
                    finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */

                } else {
                    new ViewDialog().showDialog(StreetTurnActivity.this, dialogTitle, getString(R.string.msg_error_enter_container_provider_name_first));
                }

            } else {
                Intent intent = new Intent(StreetTurnActivity.this, NoInternetActivity.class);
                startActivity(intent);
            }

            }

        });
        // code is to search Original Location end

        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            goToPreviousPage();
            }
        });

        if(GlobalVariables.ROLE_EP.equalsIgnoreCase(siaSecurityObj.getRoleName()) || GlobalVariables.ROLE_TPU.equalsIgnoreCase(siaSecurityObj.getRoleName()) ||
                (GlobalVariables.ROLE_SEC.equalsIgnoreCase(siaSecurityObj.getRoleName()) && GlobalVariables.ROLE_EP.equalsIgnoreCase(siaSecurityObj.getMemType()))) {

            epCompanyName = findViewById(R.id.epCompanyName);
            epCompanyName.setEnabled(false);
            epCompanyName.setText(siaSecurityObj.getCompanyName());
            epScac.setText(siaSecurityObj.getScac());
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

                if (Internet_Check.checkInternetConnection(getApplicationContext())) {
                    String selectedString = (String) parent.getItemAtPosition(position);

                    String[] selectedLocationArray = selectedString.split(Pattern.quote("|"));

                    ((AutoCompleteTextView) findViewById(R.id.mcCompanyName)).setText(selectedLocationArray[1]);

                    ((EditText) findViewById(R.id.mcScac)).setText(selectedLocationArray[0]);

                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    findViewById(R.id.containerNumber).requestFocus();

                } else {
                    Intent intent = new Intent(StreetTurnActivity.this, NoInternetActivity.class);
                    startActivity(intent);
                }
                }
            });

        } else if(GlobalVariables.ROLE_MC.equalsIgnoreCase(siaSecurityObj.getRoleName()) || GlobalVariables.ROLE_IDD.equalsIgnoreCase(siaSecurityObj.getRoleName()) ||
                (GlobalVariables.ROLE_SEC.equalsIgnoreCase(siaSecurityObj.getRoleName()) && GlobalVariables.ROLE_MC.equalsIgnoreCase(siaSecurityObj.getMemType()))) {

            mcCompanyName = findViewById(R.id.mcCompanyName);
            mcCompanyName.setEnabled(false);
            mcCompanyName.setText(siaSecurityObj.getCompanyName());
            mcScac.setText(siaSecurityObj.getScac());
            mcCompanyName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_gray));

            epCompanyName = findViewById(R.id.epCompanyName);
            epCompanyName.setThreshold(2); //type char in after work....
            adapter = new LocationAdapter(this);

            epCompanyName.setAdapter(adapter);
            oppositeRole = GlobalVariables.ROLE_EP;

            epCompanyName.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {

                if (Internet_Check.checkInternetConnection(getApplicationContext())) {

                    String selectedString = (String) parent.getItemAtPosition(position);
                    String[] selectedLocationArray = selectedString.split(Pattern.quote("|"));

                    ((AutoCompleteTextView) findViewById(R.id.epCompanyName)).setText(selectedLocationArray[1]);
                    ((EditText) findViewById(R.id.epScac)).setText(selectedLocationArray[0]);

                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    findViewById(R.id.containerNumber).requestFocus();

                } else {
                    Intent intent = new Intent(StreetTurnActivity.this, NoInternetActivity.class);
                    startActivity(intent);
                }
                }
            });


        }


        BottomNavigationView bnv = findViewById(R.id.navigation_next_cancel);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (Internet_Check.checkInternetConnection(getApplicationContext())) {
                    switch (item.getItemId()) {
                        case R.id.navigation_next:
                            String returnMessage = validateFields();
                            if (!returnMessage.equalsIgnoreCase(GlobalVariables.SUCCESS)) {
                                new ViewDialog().showDialog(StreetTurnActivity.this, dialogTitle, returnMessage);

                            } else {

                                // code to disable background functionality when progress bar starts
                                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                Gson gson = new Gson();
                                String jsonString = gson.toJson(getInterchangeRequests(), InterchangeRequests.class);
                                new ExecuteTaskToValidate(jsonString).execute();
                            }
                            break;
                        case R.id.navigation_cancel:
                            goToPreviousPage();
                            break;
                    }

                } else {
                    Intent intent = new Intent(StreetTurnActivity.this, NoInternetActivity.class);
                    startActivity(intent);
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

                    if (Internet_Check.checkInternetConnection(getApplicationContext())) {
                        if(chassisNumber.getText() != null && chassisNumber.getText().toString().trim().length() > 0) {

                            if(!chassisNumber.getText().toString().trim().equalsIgnoreCase(GlobalVariables.DEFUALT_CHASSIS_NUM)) {
                                // code to disable background functionality when progress bar starts
                                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                String requestString = "chassisId=" + chassisNumber.getText().toString().trim();
                                new ExecuteChassisIdTask(requestString).execute();

                            } else {
                                iepScac.setText("");
                            }

                        }

                    } else {
                        Intent intent = new Intent(StreetTurnActivity.this, NoInternetActivity.class);
                        startActivity(intent);
                    }

                    return true;
                }

                return false;
            }
        });

    }

    @NonNull
    private InterchangeRequests getInterchangeRequests() {
        InterchangeRequests ir = new InterchangeRequests();

        ir.setIrRequestType(GlobalVariables.IR_REQUEST_TYPE_ST);
        ir.setEpScacs(epScac.getText().toString());
        ir.setEpCompanyName(epCompanyName.getText().toString());
        ir.setMcAScac(mcScac.getText().toString());
        ir.setMcACompanyName(mcCompanyName.getText().toString());
        ir.setContNum(containerNumber.getText().toString());
        if(null == chassisNumber.getText() || chassisNumber.getText().toString().trim().length() <= 0 ||
                chassisNumber.getText().toString().trim().equalsIgnoreCase(GlobalVariables.DEFUALT_CHASSIS_NUM)) {

            ir.setChassisNum(GlobalVariables.DEFUALT_CHASSIS_NUM);
            ir.setIepScac("");

        } else {
            ir.setChassisNum(chassisNumber.getText().toString());
            ir.setIepScac(iepScac.getText().toString());
        }

        ir.setBookingNum(exportBookingNumber.getText().toString());
        ir.setImportBookingNum(importBL.getText().toString());
        ir.setOriginLocNm(locationName.getText().toString());
        ir.setOriginLocAddr(locationAddress.getText().toString());
        ir.setOriginLocCity(city.getText().toString());
        ir.setOriginLocState(state.getText().toString());
        ir.setOriginLocZip(zipCode.getText().toString());
        ir.setAccessToken(siaSecurityObj.getAccessToken());
        return ir;
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

        String locationName = ((EditText)findViewById(R.id.locationName)).getText().toString();
        String locationAddress = ((EditText)findViewById(R.id.locationAddress)).getText().toString();
        String city = ((EditText)findViewById(R.id.city)).getText().toString();
        String state = ((EditText)findViewById(R.id.state)).getText().toString();
        String zipCode = ((EditText)findViewById(R.id.zipCode)).getText().toString();

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

        } else if(!SIAUtility.isValidContNum(containerNumber)) {
            return getString(R.string.msg_error_invalid_container_number);
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

        if(null == zipCode || zipCode.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_empty_origin_location_zip_code);
        }
        if(null == locationName || locationName.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_empty_origin_location_name);
        }
        if(null == locationAddress || locationAddress.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_empty_origin_location_address);
        }
        if(null == city || city.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_empty_origin_location_city);
        }
        if(null == state || state.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_empty_origin_location_state);
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

                        final String jsonInString = "role=" + oppositeRole + "&requestType=" + getString(R.string.request_type_ir_request) + "&companyName=" + SIAUtility.replaceWhiteSpaces(constraint.toString());
                        Thread timer = new Thread() { //new thread
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // code to disable background functionality when progress bar starts
                                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

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

                                        if(GlobalVariables.ROLE_MC.equalsIgnoreCase(oppositeRole)) {
                                            ((EditText) findViewById(R.id.mcScac)).setText("");

                                        } else if(GlobalVariables.ROLE_EP.equalsIgnoreCase(oppositeRole)){
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
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    if (results != null && results.count > 0) {
                        Log.v("log_tag", "publishResults notifyDataSetChanged()");
                        notifyDataSetChanged();

                    } else {
                        Log.v("log_tag", "publishResults notifyDataSetInvalidated()");
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
                ApiResponse apiResponse = RestApiClient.callGetApi(getString(R.string.base_url) + getString(R.string.api_get_companyname_and_scac_by_companyname) + "?" + requestString);
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

                    if (urlResponseCode == 200) {

                        Type listType = new TypeToken<ArrayList<Company>>() {
                        }.getType();
                        List<Company> companyList = gson.fromJson(result, listType);
                        suggestions = new ArrayList<>();
                        for (int i = 0; i < companyList.size(); i++) {
                            suggestions.add(companyList.get(i).getScac() + "|" + companyList.get(i).getCompanyName());
                        }

                        notifyDataSetChanged();

                    } else {

                        try {
                            suggestions = new ArrayList<>();
                            notifyDataSetChanged();

                            ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                            new ViewDialog().showDialog(StreetTurnActivity.this, dialogTitle, errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                        } catch(Exception e){
                            new ViewDialog().showDialog(StreetTurnActivity.this, dialogTitle, getString(R.string.msg_error_try_after_some_time));
                        }

                        if (GlobalVariables.ROLE_EP.equalsIgnoreCase(oppositeRole)) {
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
            ApiResponse apiResponse = RestApiClient.callGetApi(getString(R.string.base_url) + getString(R.string.api_get_iep_scac_by_chassis_id) + "?" + requestString);
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
                Log.v("log_tag", "Chassis IEP Scac urlResponseCode:=>" + urlResponseCode);
                Log.v("log_tag", "Chassis IEP Scac result:=> " + result);
                Gson gson = new Gson();

                if (urlResponseCode == 200) {
                    JsonObject responseJson = gson.fromJson(result, JsonObject.class);
                    ((EditText) findViewById(R.id.iepScac)).setText(responseJson.get("iepScac").getAsString());

                } else {
                    ((EditText) findViewById(R.id.iepScac)).setText("");

                    try {
                        ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                        new ViewDialog().showDialog(StreetTurnActivity.this, dialogTitle, errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                    } catch(Exception e) {
                        new ViewDialog().showDialog(StreetTurnActivity.this, dialogTitle, getString(R.string.msg_error_try_after_some_time));
                    }
                }

            } catch (Exception e) {
                Log.v("log_tag", "Error ", e);
            }

        }

    }

    class ExecuteTaskToValidate extends AsyncTask<String, Integer, String> {
        String requestString;

        public ExecuteTaskToValidate(String requestString) {
            this.requestString = requestString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            ApiResponse apiResponse = RestApiClient.callPostApi(requestString, getString(R.string.base_url) +getString(R.string.api_validate_initiate_interchange_details));
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

                ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);

                if (urlResponseCode == 200) {

                    if(null == chassisNumber.getText() || chassisNumber.getText().toString().trim().length() <= 0) {
                        iepScac.setText("");
                        ir.setIepScac("");
                        ir.setChassisNum(GlobalVariables.DEFUALT_CHASSIS_NUM);
                        chassisNumber.setText(GlobalVariables.DEFUALT_CHASSIS_NUM);
                    }

                    int[] streetTurnCategories = new int[]{9, 5};
                    String[] streetTurnCategoriesName = new String[]{"Street Turn Details", "Original Location"};
                    String[] streetTurnTitles = new String[]{"CONTAINER PROVIDER NAME", "CONTAINER PROVIDER SCAC",
                            "MOTOR CARRIER NAME", "MOTOR CARRIER SCAC",
                            "IMPORT B/L", "EXPORT BOOKING #",
                            "CONTAINER #", "CHASSIS #", "CHASSIS IEP SCAC",
                            "LOCATION NAME", "LOCATION ADDRESS", "ZIP CODE", "CITY", "STATE"};
                    String[] streetTurnValues = new String[]{epCompanyName.getText().toString(), epScac.getText().toString(),
                            mcCompanyName.getText().toString(), mcScac.getText().toString(),
                            importBL.getText().toString(), exportBookingNumber.getText().toString(),
                            containerNumber.getText().toString(), chassisNumber.getText().toString(),
                            iepScac.getText().toString(),
                            locationName.getText().toString(), locationAddress.getText().toString(),
                            zipCode.getText().toString(), city.getText().toString(),
                            state.getText().toString()};


                    List<FieldInfo> fieldInfoList = new ArrayList<>();
                    int counter = 0;
                    int innerLoopStart = 0;
                    for(int i=0;i<streetTurnCategories.length;i++) {
                        FieldInfo fieldInfo = new FieldInfo();
                        fieldInfo.setTitle(GlobalVariables.FIELD_INFO_EMPTY);
                        fieldInfo.setValue(streetTurnCategoriesName[i]);
                        fieldInfoList.add(fieldInfo);
                        counter = counter + streetTurnCategories[i];
                        for (int j = innerLoopStart; j < counter; j++) {
                            fieldInfo = new FieldInfo();
                            fieldInfo.setTitle(GlobalVariables.FIELD_INFO_TITLE);
                            fieldInfo.setValue(streetTurnTitles[j]);
                            fieldInfoList.add(fieldInfo);

                            fieldInfo = new FieldInfo();
                            fieldInfo.setTitle(GlobalVariables.FIELD_INFO_VALUE);
                            fieldInfo.setValue(streetTurnValues[j]);
                            fieldInfoList.add(fieldInfo);
                        }
                        innerLoopStart = innerLoopStart + streetTurnCategories[i];

                        if((i+1) < streetTurnCategories.length) {
                            fieldInfo = new FieldInfo();
                            fieldInfo.setTitle(GlobalVariables.FIELD_INFO_BLANK);
                            fieldInfoList.add(fieldInfo);
                        }
                    }

                    if(errorMessage.getCode() == 1) {
                        editor.putString(GlobalVariables.KEY_IEP_SCAC_MESSAGE, errorMessage.getMessage());
                    } else {
                        editor.putString(GlobalVariables.KEY_IEP_SCAC_MESSAGE, "");
                    }

                    editor.putString(GlobalVariables.KEY_ORIGIN_FROM, GlobalVariables.ORIGIN_FROM_STREET_TURN);

                    SIAUtility.setList(editor, "fieldInfoList", fieldInfoList);
                    SIAUtility.setObject(editor, GlobalVariables.KEY_INTERCHANGE_REQUESTS_OBJ, getInterchangeRequests());

                    editor.commit();

                    Intent intent = new Intent(StreetTurnActivity.this, VerifyActivity.class);
                    startActivity(intent);
                    finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */

                } else {
                    try {
                        new ViewDialog().showDialog(StreetTurnActivity.this, dialogTitle, errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                    } catch(Exception e) {
                        new ViewDialog().showDialog(StreetTurnActivity.this, dialogTitle, getString(R.string.msg_error_try_after_some_time));
                    }
                }

            } catch (Exception e) {
                Log.v("log_tag", "Error ", e);
            }

            // code to regain disable backend functionality end
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }

    }


    void goToPreviousPage() {
        if (Internet_Check.checkInternetConnection(getApplicationContext())) {

            // Create custom dialog object
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);

            // Include dialog.xml file
            dialog.setContentView(R.layout.dialog_popup);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ((TextView) dialog.findViewById(R.id.titleTextView)).setText(dialogTitle);
            ((TextView) dialog.findViewById(R.id.messageTextView)).setText(getString(R.string.dialog_cancel_confirm_msg));

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

                    editor.remove(GlobalVariables.KEY_RETURN_FROM);
                    editor.commit();

                    Intent intent = new Intent(StreetTurnActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */
                }
            });

        } else {
            Intent intent = new Intent(StreetTurnActivity.this, NoInternetActivity.class);
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

}
