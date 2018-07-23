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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.iana.sia.adapter.ChassisSizeAdapter;
import com.iana.sia.adapter.ChassisTypeAdapter;
import com.iana.sia.adapter.ContainerSizeAdapter;
import com.iana.sia.adapter.ContainerTypeAdapter;
import com.iana.sia.model.Company;
import com.iana.sia.model.FieldInfo;
import com.iana.sia.model.FormOption;
import com.iana.sia.model.InterchangeRequests;
import com.iana.sia.model.NotificationAvail;
import com.iana.sia.model.SIASecurityObj;
import com.iana.sia.utility.ApiResponse;
import com.iana.sia.utility.ApiResponseMessage;
import com.iana.sia.utility.GlobalVariables;
import com.iana.sia.utility.Internet_Check;
import com.iana.sia.utility.RestApiClient;
import com.iana.sia.utility.SIAUtility;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class InitiateInterchangeActivity extends AppCompatActivity {

    String urlResponse;
    int urlResponseCode;

    ImageView equipLocationSearch;
    ImageView originLocationSearch;
    Button backBtn;

    EditText epScac;
    EditText iepScac;
    EditText containerNumber;
    EditText exportBookingNumber;
    EditText importBL;
    EditText chassisNumber;

    Spinner typeOfInterchangeSpinner;

    EditText mcAScac;
    EditText mcBScac;

    EditText gensetNumber;

    EditText equipLocationName;
    EditText equipLocationAddress;
    EditText equipLocationCity;
    EditText equipLocationState;
    EditText equipLocationZipCode;

    EditText originLocationName;
    EditText originLocationAddress;
    EditText originLocationCity;
    EditText originLocationState;
    EditText originLocationZipCode;

    AutoCompleteTextView mcACompanyName;
    AutoCompleteTextView mcBCompanyName;
    AutoCompleteTextView epCompanyName;

    ArrayAdapter<String> mcACompanyNameAdapter;
    ArrayAdapter<String> mcBCompanyNameAdapter;
    ArrayAdapter<String> epCompanyNameAdapter;

    Spinner containerTypeSpinner;
    Spinner containerSizeSpinner;
    Spinner chassisTypeSpinner;
    Spinner chassisSizeSpinner;

    TypeOfInterchangeAdapter typeOfInterchangeAdapter;
    ContainerTypeAdapter containerTypeAdapter;
    ContainerSizeAdapter containerSizeAdapter;
    ChassisTypeAdapter chassisTypeAdapter;
    ChassisSizeAdapter chassisSizeAdapter;

    SIASecurityObj siaSecurityObj;

    ProgressBar progressBar;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    InterchangeRequests ir;

    String baseOriginFrom = null;

    Context context;

    String dialogTitle;

    String searchMCACompanyName = "";
    String searchMCBCompanyName = "";
    String searchEPCompanyName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiate_interchange);

        context = this;

        dialogTitle = getString(R.string.dialog_title_street_interchange_request);

        showActionBar();
        ((TextView) findViewById(R.id.title)).setText(R.string.title_street_interchange_request);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setText(R.string.title_back);
        backBtn.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title)).setTextColor(ContextCompat.getColor(this, R.color.color_white));
        backBtn.setTextColor(ContextCompat.getColor(this, R.color.color_white));

        progressBar = findViewById(R.id.processingBar);


        mcACompanyName = findViewById(R.id.mcACompanyName);
        mcBCompanyName = findViewById(R.id.mcBCompanyName);
        epCompanyName = findViewById(R.id.epCompanyName);

        containerNumber = findViewById(R.id.containerNumber);
        SIAUtility.setUpperCase(containerNumber);

        exportBookingNumber = findViewById(R.id.exportBookingNumber);
        SIAUtility.setUpperCase(exportBookingNumber);

        importBL = findViewById(R.id.importBL);
        SIAUtility.setUpperCase(importBL);

        chassisNumber = findViewById(R.id.chassisNumber);
        SIAUtility.setUpperCase(chassisNumber);

        iepScac = findViewById(R.id.iepScac);

        gensetNumber = findViewById(R.id.gensetNumber);
        SIAUtility.setUpperCase(gensetNumber);

        typeOfInterchangeSpinner = findViewById(R.id.typeOfInterchange);
        containerTypeSpinner = findViewById(R.id.containerType);
        containerSizeSpinner = findViewById(R.id.containerSize);
        chassisTypeSpinner = findViewById(R.id.chassisType);
        chassisSizeSpinner = findViewById(R.id.chassisSize);


        epScac = findViewById(R.id.epScac);
        epScac.setFocusable(false);
        epScac.setClickable(false);
        epScac.setLongClickable(false);

        mcAScac = findViewById(R.id.mcAScac);
        mcAScac.setFocusable(false);
        mcAScac.setClickable(false);
        mcAScac.setLongClickable(false);

        mcBScac = findViewById(R.id.mcBScac);
        mcBScac.setFocusable(false);
        mcBScac.setClickable(false);
        mcBScac.setLongClickable(false);

        iepScac = findViewById(R.id.iepScac);
        iepScac.setFocusable(false);
        iepScac.setClickable(false);
        iepScac.setLongClickable(false);

        equipLocationName = findViewById(R.id.equipLocationName);
        equipLocationAddress = findViewById(R.id.equipLocationAddress);
        equipLocationCity = findViewById(R.id.equipLocationCity);
        equipLocationState = findViewById(R.id.equipLocationState);
        equipLocationZipCode = findViewById(R.id.equipLocationZipCode);

        originLocationName = findViewById(R.id.originLocationName);
        originLocationName.setFocusable(false);
        originLocationName.setClickable(false);
        originLocationName.setLongClickable(false);

        originLocationAddress = findViewById(R.id.originLocationAddress);
        originLocationAddress.setFocusable(false);
        originLocationAddress.setClickable(false);
        originLocationAddress.setLongClickable(false);

        originLocationCity = findViewById(R.id.originLocationCity);
        originLocationCity.setFocusable(false);
        originLocationCity.setClickable(false);
        originLocationCity.setLongClickable(false);

        originLocationState = findViewById(R.id.originLocationState);
        originLocationState.setFocusable(false);
        originLocationState.setClickable(false);
        originLocationState.setLongClickable(false);

        originLocationZipCode = findViewById(R.id.originLocationZipCode);
        originLocationZipCode.setFocusable(false);
        originLocationZipCode.setClickable(false);
        originLocationZipCode.setLongClickable(false);


        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        // below code is used for equipment + original location & verify page
        editor.putString(GlobalVariables.KEY_ORIGIN_FROM, GlobalVariables.ORIGIN_FROM_STREET_INTERCHANGE);

        siaSecurityObj = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_SECURITY_OBJ, SIASecurityObj.class);

        // below code is used to restrict auto populate keypad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        typeOfInterchangeAdapter = new TypeOfInterchangeAdapter(this, getResources().getStringArray(R.array.type_of_interchange));
        typeOfInterchangeSpinner.setAdapter(typeOfInterchangeAdapter);


        // code to disable background functionality when progress bar starts
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        ir = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_INTERCHANGE_REQUESTS_OBJ, InterchangeRequests.class);

        if (null == ir) {
            ir = new InterchangeRequests();
        }

        new ExecuteSetupPageTask().execute();

        Log.v("log_tag", "InitiateInterchangeActivity: InterchangeRequests:=> " + ir);

        epCompanyName.setText(null == ir.getEpCompanyName() ? "" : ir.getEpCompanyName());
        epScac.setText(null == ir.getEpScacs() ? "" : ir.getEpScacs());
        searchEPCompanyName = epCompanyName.getText().toString();

        mcACompanyName.setText(null == ir.getMcACompanyName() ? "" : ir.getMcACompanyName());
        mcAScac.setText(null == ir.getMcAScac() ? "" : ir.getMcAScac());
        searchMCACompanyName = mcACompanyName.getText().toString();

        mcBCompanyName.setText(null == ir.getMcBCompanyName() ? "" : ir.getMcBCompanyName());
        mcBScac.setText(null == ir.getMcBScac() ? "" : ir.getMcBScac());
        searchMCBCompanyName = mcBCompanyName.getText().toString();

        if (null != ir && null != ir.getIntchgType()) {
            for (int i = 0; i < typeOfInterchangeSpinner.getCount(); i++) {
                if (typeOfInterchangeSpinner.getItemAtPosition(i).equals(ir.getIntchgType())) {
                    typeOfInterchangeSpinner.setSelection(i);
                }
            }
        }

        importBL.setText(ir.getImportBookingNum());
        exportBookingNumber.setText(ir.getBookingNum());
        containerNumber.setText(ir.getContNum());

        chassisNumber.setText(ir.getChassisNum());
        iepScac.setText(ir.getIepScac());

        gensetNumber.setText(ir.getGensetNum());

        equipLocationName.setText(ir.getEquipLocNm());
        equipLocationAddress.setText(ir.getEquipLocAddr());
        equipLocationZipCode.setText(ir.getEquipLocZip());
        equipLocationCity.setText(ir.getEquipLocCity());
        equipLocationState.setText(ir.getEquipLocState());

        originLocationName.setText(ir.getOriginLocNm());
        originLocationAddress.setText(ir.getOriginLocAddr());
        originLocationZipCode.setText(ir.getOriginLocZip());
        originLocationCity.setText(ir.getOriginLocCity());
        originLocationState.setText(ir.getOriginLocState());

        // code when request come from notification of available starts

        baseOriginFrom = sharedPref.getString(GlobalVariables.KEY_BASE_ORIGIN_FROM, "");
        if (null != baseOriginFrom && GlobalVariables.ORIGIN_FROM_NOTIF_AVAIl.equalsIgnoreCase(baseOriginFrom)) {

            epCompanyName.setFocusable(false);
            epCompanyName.setClickable(false);
            epCompanyName.setLongClickable(false);
            epCompanyName.setTextColor(ContextCompat.getColor(context, R.color.darker_gray));

            mcACompanyName.setFocusable(false);
            mcACompanyName.setClickable(false);
            mcACompanyName.setLongClickable(false);
            mcACompanyName.setTextColor(ContextCompat.getColor(context, R.color.darker_gray));

            if(GlobalVariables.ROLE_MC.equalsIgnoreCase(siaSecurityObj.getRoleName()) ||
                GlobalVariables.ROLE_IDD.equalsIgnoreCase(siaSecurityObj.getRoleName()) ||
                (GlobalVariables.ROLE_SEC.equalsIgnoreCase(siaSecurityObj.getRoleName()) &&
                        null != siaSecurityObj.getMemType() &&
                        GlobalVariables.ROLE_MC.equalsIgnoreCase(siaSecurityObj.getMemType()))){

                mcBCompanyName.setText(siaSecurityObj.getCompanyName());
                mcBScac.setText(siaSecurityObj.getScac());
                searchMCBCompanyName = mcBCompanyName.getText().toString();
            }

            containerNumber.setFocusable(false);
            containerNumber.setClickable(false);
            containerNumber.setLongClickable(false);
            containerNumber.setTextColor(ContextCompat.getColor(context, R.color.darker_gray));

            chassisNumber.setFocusable(false);
            chassisNumber.setClickable(false);
            chassisNumber.setLongClickable(false);
            chassisNumber.setTextColor(ContextCompat.getColor(context, R.color.darker_gray));

            iepScac.setTextColor(ContextCompat.getColor(context, R.color.darker_gray));

            gensetNumber.setFocusable(false);
            gensetNumber.setClickable(false);
            gensetNumber.setLongClickable(false);
            gensetNumber.setTextColor(ContextCompat.getColor(context, R.color.darker_gray));

            ((TextView) findViewById(R.id.typeOfInterchangeLbl)).setTextColor(ContextCompat.getColor(context, R.color.appThemeColor));
            ((TextView) findViewById(R.id.exportBookingNumberLbl)).setTextColor(ContextCompat.getColor(context, R.color.appThemeColor));

        } else {
            epCompanyName.setThreshold(2); //type char in after work....
            epCompanyNameAdapter = new EPLocationAdapter(this);
            epCompanyName.setAdapter(epCompanyNameAdapter);


            mcACompanyName.setThreshold(2); //type char in after work....
            mcACompanyNameAdapter = new MCALocationAdapter(this);
            mcACompanyName.setAdapter(mcACompanyNameAdapter);

            epCompanyName.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {

                    if (Internet_Check.checkInternetConnection(context)) {

                        String selectedString = (String) parent.getItemAtPosition(position);
                        String[] selectedLocationArray = selectedString.split(Pattern.quote("|"));

                        ((AutoCompleteTextView) findViewById(R.id.epCompanyName)).setText(selectedLocationArray[1]);
                        ((EditText) findViewById(R.id.epScac)).setText(selectedLocationArray[0]);

                        ir.setEpCompanyName(selectedLocationArray[1]);
                        ir.setEpScacs(selectedLocationArray[0]);
                        searchEPCompanyName = selectedLocationArray[1];

                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                        findViewById(R.id.mcACompanyName).requestFocus();

                    } else {
                        Intent intent = new Intent(InitiateInterchangeActivity.this, NoInternetActivity.class);
                        startActivity(intent);
                    }
                }
            });

            mcACompanyName.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {

                    if (Internet_Check.checkInternetConnection(context)) {

                        String selectedString = (String) parent.getItemAtPosition(position);
                        String[] selectedLocationArray = selectedString.split(Pattern.quote("|"));

                        ((AutoCompleteTextView) findViewById(R.id.mcACompanyName)).setText(selectedLocationArray[1]);
                        ((EditText) findViewById(R.id.mcAScac)).setText(selectedLocationArray[0]);

                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                        ir.setMcACompanyName(selectedLocationArray[1]);
                        ir.setMcAScac(selectedLocationArray[0]);
                        searchMCACompanyName = selectedLocationArray[1];

                        findViewById(R.id.mcBCompanyName).requestFocus();

                    } else {
                        Intent intent = new Intent(InitiateInterchangeActivity.this, NoInternetActivity.class);
                        startActivity(intent);
                    }
                }
            });

        }

        mcBCompanyName.setThreshold(2); //type char in after work....
        mcBCompanyNameAdapter = new MCBLocationAdapter(this);
        mcBCompanyName.setAdapter(mcBCompanyNameAdapter);

        mcBCompanyName.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {

                if (Internet_Check.checkInternetConnection(context)) {

                    String selectedString = (String) parent.getItemAtPosition(position);
                    String[] selectedLocationArray = selectedString.split(Pattern.quote("|"));

                    ((AutoCompleteTextView) findViewById(R.id.mcBCompanyName)).setText(selectedLocationArray[1]);
                    ((EditText) findViewById(R.id.mcBScac)).setText(selectedLocationArray[0]);

                    ir.setMcBCompanyName(selectedLocationArray[1]);
                    ir.setMcBScac(selectedLocationArray[0]);
                    searchMCBCompanyName = selectedLocationArray[1];

                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    findViewById(R.id.typeOfInterchange).requestFocus();

                } else {
                    Intent intent = new Intent(InitiateInterchangeActivity.this, NoInternetActivity.class);
                    startActivity(intent);
                }
            }
        });

        containerTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String str =(String)parentView.getSelectedItem();
                if(str.equalsIgnoreCase(getString(R.string.lbl_other))) {
                    findViewById(R.id.containerTypeOtherLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.containerTypeOther).requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(findViewById(R.id.containerTypeOther), InputMethodManager.SHOW_IMPLICIT);

                } else {
                    findViewById(R.id.containerTypeOtherLayout).setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });
        containerSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String str =(String)parentView.getSelectedItem();
                if(str.equalsIgnoreCase(getString(R.string.lbl_other))) {
                    findViewById(R.id.containerSizeOtherLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.containerSizeOther).requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(findViewById(R.id.containerSizeOther), InputMethodManager.SHOW_IMPLICIT);

                } else {
                    findViewById(R.id.containerSizeOtherLayout).setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });

        chassisTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String str =(String)parentView.getSelectedItem();
                if(str.equalsIgnoreCase(getString(R.string.lbl_other))) {
                    findViewById(R.id.chassisTypeOtherLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.chassisTypeOther).requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(findViewById(R.id.chassisTypeOther), InputMethodManager.SHOW_IMPLICIT);
                } else {
                    findViewById(R.id.chassisTypeOtherLayout).setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });

        chassisSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String str =(String)parentView.getSelectedItem();
                if(str.equalsIgnoreCase(getString(R.string.lbl_other))) {
                    findViewById(R.id.chassisSizeOtherLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.chassisSizeOther).requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(findViewById(R.id.chassisSizeOther), InputMethodManager.SHOW_IMPLICIT);

                } else {
                    findViewById(R.id.chassisSizeOtherLayout).setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });

        // code is to search Equipment Location start
        equipLocationSearch = findViewById(R.id.equipLocationSearch);
        equipLocationSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {

                if (Internet_Check.checkInternetConnection(context)) {

                    setAndGetInterchangeRequestData();

                    SIAUtility.setObject(editor, GlobalVariables.KEY_INTERCHANGE_REQUESTS_OBJ, ir);
                    editor.remove(GlobalVariables.KEY_SEARCH_FOR_LOCATION);
                    editor.commit();

                    Intent intent = new Intent(InitiateInterchangeActivity.this, LocationActivity.class);
                    startActivity(intent);
                    finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */

                } else {
                    Intent intent = new Intent(InitiateInterchangeActivity.this, NoInternetActivity.class);
                    startActivity(intent);
                }

            }

        });
        // code is to search Equipment Location end

        // code is to search Original Location start
        originLocationSearch = findViewById(R.id.originLocationSearch);
        originLocationSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {

                if (Internet_Check.checkInternetConnection(context)) {

                    if (null != epScac.getText() && (epScac.getText().toString().length() >= 2 && epScac.getText().toString().length() <= 4)) {

                        setAndGetInterchangeRequestData();

                        editor.putString(GlobalVariables.KEY_SEARCH_FOR_LOCATION, GlobalVariables.ORIGIN_FROM_ORIGINAL);
                        SIAUtility.setObject(editor, GlobalVariables.KEY_INTERCHANGE_REQUESTS_OBJ, ir);
                        editor.commit();

                        Intent intent = new Intent(InitiateInterchangeActivity.this, LocationActivity.class);
                        startActivity(intent);
                        finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */

                    } else {
                        new ViewDialog().showDialog(InitiateInterchangeActivity.this, dialogTitle, getString(R.string.msg_error_enter_container_provider_name_first));
                    }

                } else {
                    Intent intent = new Intent(InitiateInterchangeActivity.this, NoInternetActivity.class);
                    startActivity(intent);
                }

            }

        });
        // code is to search Original Location end


        // below event is execute based on done button on mobile keypad
        chassisNumber = findViewById(R.id.chassisNumber);
        chassisNumber.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    Log.v("log_tag", "<===============================================================>1");

                    if (Internet_Check.checkInternetConnection(context)) {
                        Log.v("log_tag", "<===============================================================>2");
                        if(chassisNumber.getText() != null && chassisNumber.getText().toString().trim().length() > 0) {

                            if(SIAUtility.isAlphaNumeric(chassisNumber.getText().toString())) {

                                if (!chassisNumber.getText().toString().trim().equalsIgnoreCase(GlobalVariables.DEFUALT_CHASSIS_NUM)) {
                                    iepScac.setText("");
                                    // code to disable background functionality when progress bar starts
                                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    String requestString = "chassisId=" + chassisNumber.getText().toString().trim();
                                    new ExecuteChassisIdTask(requestString).execute();

                                } else {
                                    iepScac.setText("");
                                }

                            } else {
                                chassisNumber.setFocusable(true);
                                chassisNumber.setText("");
                                new ViewDialog().showDialog(InitiateInterchangeActivity.this, dialogTitle, getString(R.string.msg_error_alpha_num_chassis_number));
                                iepScac.setText("");
                            }

                        }

                    } else {
                        Intent intent = new Intent(InitiateInterchangeActivity.this, NoInternetActivity.class);
                        startActivity(intent);
                    }

                    return true;
                }

                return false;
            }
        });


        BottomNavigationView bnv = findViewById(R.id.navigation_next_cancel);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (Internet_Check.checkInternetConnection(context)) {
                    switch (item.getItemId()) {
                        case R.id.navigation_next:
                            String returnMessage = validateFields();
                            if (!returnMessage.equalsIgnoreCase(GlobalVariables.SUCCESS)) {
                                new ViewDialog().showDialog(InitiateInterchangeActivity.this, dialogTitle, returnMessage);

                            } else {

                                // code to disable background functionality when progress bar starts
                                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                setAndGetInterchangeRequestData();

                                ir.setIrRequestType(GlobalVariables.IR_REQUEST_TYPE_SI);

                                ir.setAccessToken(siaSecurityObj.getAccessToken());
                                Gson gson = new Gson();
                                String jsonString = gson.toJson(ir, InterchangeRequests.class);
                                Log.v("log_tag", "Validate InitiateInterchangeActivity: jsonString:=> " + jsonString);
                                new ExecuteTaskToValidate(jsonString).execute();
                            }
                            break;
                        case R.id.navigation_cancel:
                            goToPreviousPage();
                            break;
                    }

                } else {
                    Intent intent = new Intent(InitiateInterchangeActivity.this, NoInternetActivity.class);
                    startActivity(intent);
                }


                return true;
            }
        });

        SIAUtility.disableShiftMode(bnv);


        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToPreviousPage();
            }
        });


    }

    private void setAndGetInterchangeRequestData() {
        ir.setEpCompanyName(epCompanyName.getText().toString());
        ir.setEpScacs(epScac.getText().toString());
        ir.setMcACompanyName(mcACompanyName.getText().toString());
        ir.setMcAScac(mcAScac.getText().toString());
        ir.setMcBCompanyName(mcBCompanyName.getText().toString());
        ir.setMcBScac(mcBScac.getText().toString());

        LinearLayout ll = (LinearLayout) typeOfInterchangeSpinner.getSelectedView(); // get the parent layout view
        TextView selectedText = ll.findViewById(R.id.typeOfInterchangeTextView); // get the child text view
        ir.setIntchgType(selectedText.getText().toString());

        ll = (LinearLayout) containerTypeSpinner.getSelectedView(); // get the parent layout view
        selectedText = ll.findViewById(R.id.containerTypeTextView); // get the child text view
        ir.setContType(selectedText.getText().toString());
        if(selectedText.getText().toString().equalsIgnoreCase(getString(R.string.lbl_other))) {
            ir.setContType(((EditText) findViewById(R.id.containerTypeOther)).getText().toString().trim());
        }

        ll = (LinearLayout) containerSizeSpinner.getSelectedView(); // get the parent layout view
        selectedText = ll.findViewById(R.id.containerSizeTextView); // get the child text view
        ir.setContSize(selectedText.getText().toString());
        if(selectedText.getText().toString().equalsIgnoreCase(getString(R.string.lbl_other))) {
            ir.setContSize(((EditText) findViewById(R.id.containerSizeOther)).getText().toString().trim());
        }

        ir.setImportBookingNum(importBL.getText().toString());
        ir.setBookingNum(exportBookingNumber.getText().toString());
        ir.setContNum(containerNumber.getText().toString());

        if(null == chassisNumber.getText() || chassisNumber.getText().toString().trim().length() <= 0 ||
                chassisNumber.getText().toString().trim().equalsIgnoreCase(GlobalVariables.DEFUALT_CHASSIS_NUM)) {

            ir.setChassisNum(GlobalVariables.DEFUALT_CHASSIS_NUM);
            ir.setIepScac("");

        } else {
            ir.setChassisNum(chassisNumber.getText().toString());
            ir.setIepScac(iepScac.getText().toString());
        }

        ll = (LinearLayout) chassisTypeSpinner.getSelectedView(); // get the parent layout view
        selectedText = ll.findViewById(R.id.chassisTypeTextView); // get the child text view
        ir.setChassisType(selectedText.getText().toString());
        if(selectedText.getText().toString().equalsIgnoreCase(getString(R.string.select_chassis_type))) {
            ir.setChassisType("");

        } else if(selectedText.getText().toString().equalsIgnoreCase(getString(R.string.lbl_other))) {
            ir.setChassisType(((EditText) findViewById(R.id.chassisTypeOther)).getText().toString().trim());
        }

        ll = (LinearLayout) chassisSizeSpinner.getSelectedView(); // get the parent layout view
        selectedText = ll.findViewById(R.id.chassisSizeTextView); // get the child text view
        ir.setChassisSize(selectedText.getText().toString());
        if(selectedText.getText().toString().equalsIgnoreCase(getString(R.string.select_chassis_size))) {
            ir.setChassisSize("");

        } else if(selectedText.getText().toString().equalsIgnoreCase(getString(R.string.lbl_other))) {
            ir.setChassisSize(((EditText) findViewById(R.id.chassisSizeOther)).getText().toString().trim());
        }

        ir.setGensetNum(gensetNumber.getText().toString());

        ir.setOriginLocNm(originLocationName.getText().toString());
        ir.setOriginLocAddr(originLocationAddress.getText().toString());
        ir.setOriginLocCity(originLocationCity.getText().toString());
        ir.setOriginLocState(originLocationState.getText().toString());
        ir.setOriginLocZip(originLocationZipCode.getText().toString());

        ir.setEquipLocNm(equipLocationName.getText().toString());
        ir.setEquipLocAddr(equipLocationAddress.getText().toString());
        ir.setEquipLocCity(equipLocationCity.getText().toString());
        ir.setEquipLocState(equipLocationState.getText().toString());
        ir.setEquipLocZip(equipLocationZipCode.getText().toString());

    }

    String validateFields() {
        String epScac = ((EditText)findViewById(R.id.epScac)).getText().toString();
        String epCompanyName = ((EditText)findViewById(R.id.epCompanyName)).getText().toString();
        String mcAScac = ((EditText)findViewById(R.id.mcAScac)).getText().toString();
        String mcACompanyName = ((EditText)findViewById(R.id.mcACompanyName)).getText().toString();
        String mcBScac = ((EditText)findViewById(R.id.mcBScac)).getText().toString();
        String mcBCompanyName = ((EditText)findViewById(R.id.mcBCompanyName)).getText().toString();


        LinearLayout ll = (LinearLayout) typeOfInterchangeSpinner.getSelectedView(); // get the parent layout view
        TextView selectedTypeOfInterchange = ll.findViewById(R.id.typeOfInterchangeTextView); // get the child text view

        ll = (LinearLayout) containerTypeSpinner.getSelectedView(); // get the parent layout view
        TextView selectedContainerType = ll.findViewById(R.id.containerTypeTextView); // get the child text view

        ll = (LinearLayout) containerSizeSpinner.getSelectedView(); // get the parent layout view
        TextView selectedContainerSize = ll.findViewById(R.id.containerSizeTextView); // get the child text view

        ll = (LinearLayout) chassisTypeSpinner.getSelectedView(); // get the parent layout view
        TextView selectedChassisType = ll.findViewById(R.id.chassisTypeTextView); // get the child text view

        ll = (LinearLayout) chassisSizeSpinner.getSelectedView(); // get the parent layout view
        TextView selectedChassisSize = ll.findViewById(R.id.chassisSizeTextView); // get the child text view

        String containerNumber = ((EditText)findViewById(R.id.containerNumber)).getText().toString();
        String exportBookingNumber = ((EditText)findViewById(R.id.exportBookingNumber)).getText().toString();
        String importBL = ((EditText)findViewById(R.id.importBL)).getText().toString();
        String chassisNumber = ((EditText)findViewById(R.id.chassisNumber)).getText().toString();
        String gensetNumber = ((EditText)findViewById(R.id.gensetNumber)).getText().toString();

        String equipLocationName = ((EditText)findViewById(R.id.equipLocationName)).getText().toString();
        String equipLocationAddress = ((EditText)findViewById(R.id.equipLocationAddress)).getText().toString();
        String equipLocationCity = ((EditText)findViewById(R.id.equipLocationCity)).getText().toString();
        String equipLocationState = ((EditText)findViewById(R.id.equipLocationState)).getText().toString();
        String equipLocationZipCode = ((EditText)findViewById(R.id.equipLocationZipCode)).getText().toString();

        String originLocationName = ((EditText)findViewById(R.id.originLocationName)).getText().toString();
        String originLocationAddress = ((EditText)findViewById(R.id.originLocationAddress)).getText().toString();
        String originLocationCity = ((EditText)findViewById(R.id.originLocationCity)).getText().toString();
        String originLocationState = ((EditText)findViewById(R.id.originLocationState)).getText().toString();
        String originLocationZipCode = ((EditText)findViewById(R.id.originLocationZipCode)).getText().toString();


        if(null == epCompanyName || epCompanyName.trim().toString().length() <= 0) {
            return getString(R.string.msg_error_empty_container_provider_name);
        }

        if(null == epScac || epScac.trim().toString().length() <= 0) {
            return getString(R.string.msg_error_select_container_provider_name);
        }

        if(null == mcACompanyName || mcACompanyName.trim().toString().length() <= 0) {
            return getString(R.string.msg_error_empty_motor_carrier_a_name);
        }

        if(null == mcAScac || mcAScac.trim().toString().length() <= 0) {
            return getString(R.string.msg_error_select_motor_carrier_a_name);
        }

        if(null == mcBCompanyName || mcBCompanyName.trim().toString().length() <= 0) {
            return getString(R.string.msg_error_empty_motor_carrier_b_name);
        }

        if(null == mcBScac || mcBScac.trim().toString().length() <= 0) {
            return getString(R.string.msg_error_select_motor_carrier_b_name);
        }

        if(null == selectedTypeOfInterchange || null == selectedTypeOfInterchange.getText() ||
                selectedTypeOfInterchange.getText().toString().trim().equalsIgnoreCase("SELECT TYPE OF INTERCHANGE")) {
            return getString(R.string.msg_error_select_type_of_interchange);
        }

        if(null == selectedContainerType || null == selectedContainerType.getText() ||
                selectedContainerType.getText().toString().trim().equalsIgnoreCase(getString(R.string.select_container_type))) {
            return getString(R.string.msg_error_select_container_type);

        } else if(selectedContainerType.getText().toString().equalsIgnoreCase(getString(R.string.lbl_other))){
            EditText containerTypeOtherEditText = findViewById(R.id.containerTypeOther);
            if(null == containerTypeOtherEditText || null == containerTypeOtherEditText.getText() ||
                    containerTypeOtherEditText.getText().toString().trim().length() <= 0) {
                return getString(R.string.msg_error_empty_container_type);
            }
        }

        if(null == selectedContainerSize || null == selectedContainerSize.getText() ||
                selectedContainerSize.getText().toString().trim().equalsIgnoreCase(getString(R.string.select_container_size))) {
            return getString(R.string.msg_error_select_container_size);

        } else if(selectedContainerSize.getText().toString().equalsIgnoreCase(getString(R.string.lbl_other))){
            EditText containerSizeOtherEditText = findViewById(R.id.containerSizeOther);
            if(null == containerSizeOtherEditText || null == containerSizeOtherEditText.getText() ||
                    containerSizeOtherEditText.getText().toString().trim().length() <= 0) {
                return getString(R.string.msg_error_empty_container_size);
            }
        }

        if(null != importBL && importBL.toString().trim().length() > 0 && !SIAUtility.isAlphaNumeric(importBL)) {
            return getString(R.string.msg_error_alpha_num_import_booking_number);
        }

        if(null == exportBookingNumber || exportBookingNumber.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_empty_export_booking_number);

        } else if(!SIAUtility.isAlphaNumeric(exportBookingNumber)) {
            return getString(R.string.msg_error_alpha_num_export_booking_number);
        }

        if(null == containerNumber || containerNumber.trim().toString().length() <= 0) {
            return getString(R.string.msg_error_empty_container_number);

        } else if(!SIAUtility.isValidContNum(containerNumber)) {
            return getString(R.string.msg_error_invalid_container_number);
        }

        if(null != chassisNumber && chassisNumber.toString().trim().length() > 0 && !SIAUtility.isAlphaNumeric(chassisNumber)) {
            return getString(R.string.msg_error_alpha_num_chassis_number);
        }

        if(chassisNumber != null && chassisNumber.trim().length() > 0 && !chassisNumber.equalsIgnoreCase(GlobalVariables.DEFUALT_CHASSIS_NUM)) {

            if (null == selectedChassisType || null == selectedChassisType.getText() ||
                    selectedChassisType.getText().toString().trim().equalsIgnoreCase(getString(R.string.select_chassis_type))) {
                return getString(R.string.msg_error_select_chassis_type);

            } else if (selectedChassisType.getText().toString().equalsIgnoreCase(getString(R.string.lbl_other))) {
                EditText chassisTypeOtherEditText = findViewById(R.id.chassisTypeOther);
                if (null == chassisTypeOtherEditText || null == chassisTypeOtherEditText.getText() ||
                        chassisTypeOtherEditText.getText().toString().trim().length() <= 0) {
                    return getString(R.string.msg_error_empty_chassis_type);
                }
            }

            if (null == selectedChassisSize || null == selectedChassisSize.getText() ||
                    selectedChassisSize.getText().toString().trim().equalsIgnoreCase(getString(R.string.select_chassis_size))) {
                return getString(R.string.msg_error_select_chassis_size);

            } else if (selectedChassisSize.getText().toString().equalsIgnoreCase(getString(R.string.lbl_other))) {
                EditText chassisSizeOtherEditText = findViewById(R.id.chassisSizeOther);
                if (null == chassisSizeOtherEditText || null == chassisSizeOtherEditText.getText() ||
                        chassisSizeOtherEditText.getText().toString().trim().length() <= 0) {
                    return getString(R.string.msg_error_empty_chassis_size);
                }
            }
        }

        if(null != gensetNumber && gensetNumber.toString().trim().length() > 0 && !SIAUtility.isAlphaNumeric(gensetNumber)) {
            return getString(R.string.msg_error_alpha_num_genset_number);
        }

        if(null == equipLocationZipCode || equipLocationZipCode.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_empty_equip_location_zip_code);
        }
        if(null == equipLocationName || equipLocationName.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_empty_equip_location_name);
        }
        if(null == equipLocationAddress || equipLocationAddress.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_empty_equip_location_address);
        }
        if(null == equipLocationCity || equipLocationCity.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_empty_equip_location_city);
        }
        if(null == equipLocationState || equipLocationState.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_empty_equip_location_state);
        }


        if(null == originLocationZipCode || originLocationZipCode.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_empty_origin_location_zip_code);
        }
        if(null == originLocationName || originLocationName.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_empty_origin_location_name);
        }
        if(null == originLocationAddress || originLocationAddress.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_empty_origin_location_address);
        }
        if(null == originLocationCity || originLocationCity.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_empty_origin_location_city);
        }
        if(null == originLocationState || originLocationState.toString().trim().length() <= 0) {
            return getString(R.string.msg_error_empty_origin_location_state);
        }

        return GlobalVariables.SUCCESS;
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



    class EPLocationAdapter extends ArrayAdapter<String> {

        protected List<String> suggestions;

        public EPLocationAdapter(Activity context) {
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
                    if (constraint != null && (null == searchEPCompanyName || !searchEPCompanyName.trim().equalsIgnoreCase(constraint.toString().trim()))) {

                        final String jsonInString = "role="+ GlobalVariables.ROLE_EP+"&requestType="+getString(R.string.request_type_ir_request)+"&companyName="+SIAUtility.replaceWhiteSpaces(constraint.toString());
                        searchEPCompanyName = constraint.toString().trim();

                        Thread timer = new Thread() { //new thread
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        // code to disable background functionality when progress bar starts
                                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        new ExecuteEPTask(jsonInString).execute();
                                    }
                                });
                            }
                        };
                        timer.start();
                        // Now assign the values and count to the FilterResults
                        // object
                        filterResults.values = suggestions;
                        filterResults.count = suggestions.size();
                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence contraint, FilterResults results) {

                    if (results != null && results.count > 0) {
                        Log.v("log_tag", "notifyDataSetChanged()");
                        notifyDataSetChanged();

                    } else {
                        Log.v("log_tag", "notifyDataSetInvalidated()");
                        notifyDataSetInvalidated();
                    }
                }
            };
            return myFilter;
        }

        class ExecuteEPTask extends AsyncTask<String, Integer, String> {
            String requestString;

            public ExecuteEPTask(String requestString) {
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
                    Log.v("log_tag", "EP urlResponseCode:=>" + urlResponseCode);
                    Log.v("log_tag", "EP result:=> " + result);
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

                        suggestions = new ArrayList<>();
                        notifyDataSetChanged();
                        epScac.setText("");

                        try {
                            ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                            new ViewDialog().showDialog(InitiateInterchangeActivity.this, dialogTitle, errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                        } catch(Exception e) {
                            new ViewDialog().showDialog(InitiateInterchangeActivity.this, dialogTitle, getString(R.string.msg_error_try_after_some_time));
                        }

                    }

                } catch (Exception e) {
                    Log.v("log_tag", "Error ", e);
                }

            }

        }
    }




    class MCALocationAdapter extends ArrayAdapter<String> {

        protected List<String> suggestions;

        public MCALocationAdapter(Activity context) {
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
                    if (constraint != null && (null == searchMCACompanyName || !searchMCACompanyName.trim().equalsIgnoreCase(constraint.toString().trim()))) {

                        final String jsonInString = "role="+ GlobalVariables.ROLE_MC+"&requestType="+getString(R.string.request_type_ir_request)+"&companyName="+SIAUtility.replaceWhiteSpaces(constraint.toString());
//                        Log.v("log_tag", "In filterResults with jsonInString:=>"+jsonInString);
                        searchMCACompanyName = constraint.toString().trim();

                        Thread timer = new Thread() { //new thread
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // code to disable background functionality when progress bar starts
                                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        new ExecuteMCATask(jsonInString).execute();
                                    }
                                });
                            }
                        };
                        timer.start();
                        // Now assign the values and count to the FilterResults
                        // object
                        filterResults.values = suggestions;
                        filterResults.count = suggestions.size();
                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence contraint, FilterResults results) {

                    if (results != null && results.count > 0) {
                        Log.v("log_tag", "notifyDataSetChanged()");
                        notifyDataSetChanged();

                    } else {
                        Log.v("log_tag", "notifyDataSetInvalidated()");
                        notifyDataSetInvalidated();
                    }
                }
            };
            return myFilter;
        }

        class ExecuteMCATask extends AsyncTask<String, Integer, String> {
            String requestString;

            public ExecuteMCATask(String requestString) {
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

                    } else if (urlResponseCode != 0) {

                        try {
                            suggestions = new ArrayList<>();
                            notifyDataSetChanged();

                            ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);

                            new ViewDialog().showDialog(InitiateInterchangeActivity.this, dialogTitle, errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                            mcAScac.setText("");

                        } catch(Exception e) {
                            suggestions = new ArrayList<>();
                            notifyDataSetChanged();
                            mcAScac.setText("");

                            new ViewDialog().showDialog(InitiateInterchangeActivity.this, dialogTitle, getString(R.string.msg_error_try_after_some_time));
                        }

                } else {

                        suggestions = new ArrayList<>();
                        notifyDataSetChanged();

                        new ViewDialog().showDialog(InitiateInterchangeActivity.this, dialogTitle, getString(R.string.msg_error_try_after_some_time));

                        mcAScac.setText("");
                    }

                } catch (Exception e) {
                    Log.v("log_tag", "Error ", e);
                }

            }

        }
    }


    class MCBLocationAdapter extends ArrayAdapter<String> {

        protected List<String> suggestions;

        public MCBLocationAdapter(Activity context) {
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
                    if (constraint != null && (null == searchMCBCompanyName || !searchMCBCompanyName.trim().equalsIgnoreCase(constraint.toString().trim()))) {

                        final String jsonInString = "role="+ GlobalVariables.ROLE_MC+"&requestType="+getString(R.string.request_type_ir_request)+"&companyName="+SIAUtility.replaceWhiteSpaces(constraint.toString());
//                        Log.v("log_tag", "In filterResults with jsonInString:=>"+jsonInString);
                        searchMCBCompanyName = constraint.toString().trim();

                        Thread timer = new Thread() { //new thread
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // code to disable background functionality when progress bar starts
                                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        new ExecuteMCBTask(jsonInString).execute();
                                    }
                                });
                            }
                        };
                        timer.start();
                        // Now assign the values and count to the FilterResults
                        // object
                        filterResults.values = suggestions;
                        filterResults.count = suggestions.size();
                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    if (results != null && results.count > 0) {
                        Log.v("log_tag", "notifyDataSetChanged()");
                        notifyDataSetChanged();

                    } else {
                        Log.v("log_tag", "notifyDataSetInvalidated()");
                        notifyDataSetInvalidated();
                    }
                }
            };
            return myFilter;
        }

        class ExecuteMCBTask extends AsyncTask<String, Integer, String> {
            String requestString;

            public ExecuteMCBTask(String requestString) {
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
                    Log.v("log_tag", "MCB urlResponseCode:=>" + urlResponseCode);
                    Log.v("log_tag", "MCB result:=> " + result);
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

                        suggestions = new ArrayList<>();
                        notifyDataSetChanged();

                        try {

                            ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                            new ViewDialog().showDialog(InitiateInterchangeActivity.this, dialogTitle, errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                        } catch(Exception e) {
                            new ViewDialog().showDialog(InitiateInterchangeActivity.this, dialogTitle, getString(R.string.msg_error_try_after_some_time));
                        }
                        mcBScac.setText("");
                    }

                } catch (Exception e) {
                    Log.v("log_tag", "Error ", e);
                }

            }

        }
    }


    public class TypeOfInterchangeAdapter extends BaseAdapter {
        Context context;
        String[] typeOfInterchangeArray;
        LayoutInflater inflater;

        public TypeOfInterchangeAdapter(Context applicationContext, String[] typeOfInterchangeArray) {
            this.context = applicationContext;
            this.typeOfInterchangeArray = typeOfInterchangeArray;
            inflater = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return typeOfInterchangeArray.length;
        }

        @Override
        public Object getItem(int i) {
            return typeOfInterchangeArray[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflater.inflate(R.layout.custom_spinner_type_of_interchange, null);
            TextView typeOfInterchangeTxt = view.findViewById(R.id.typeOfInterchangeTextView);
            typeOfInterchangeTxt.setText(typeOfInterchangeArray[i]);
            return view;
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

                    try {
                        ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                        new ViewDialog().showDialog(InitiateInterchangeActivity.this, dialogTitle, errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                    } catch(Exception e) {
                        new ViewDialog().showDialog(InitiateInterchangeActivity.this, dialogTitle, getString(R.string.msg_error_try_after_some_time));
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

            // code to regain disable backend functionality end
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            try {
                Log.v("log_tag", "urlResponseCode:=>" + urlResponseCode);
                Log.v("log_tag", "verify result:=> " + result);
                Gson gson = new Gson();

                ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);

                if (urlResponseCode == 200) {

                    if(null == chassisNumber.getText() || chassisNumber.getText().toString().trim().length() <= 0) {
                        iepScac.setText("");
                        ir.setIepScac("");
                        ir.setChassisNum(GlobalVariables.DEFUALT_CHASSIS_NUM);
                        chassisNumber.setText(GlobalVariables.DEFUALT_CHASSIS_NUM);
                    }

                    String containerType = containerTypeSpinner.getSelectedItem().toString();
                    if(getString(R.string.lbl_other).equalsIgnoreCase(containerType)) {
                        containerType = ((EditText) findViewById(R.id.containerTypeOther)).getText().toString().trim();
                    } else if(getString(R.string.select_container_type).equalsIgnoreCase(containerType)) {
                        containerType = "";
                    }

                    String containerSize = containerSizeSpinner.getSelectedItem().toString();
                    if(getString(R.string.lbl_other).equalsIgnoreCase(containerSize)) {
                        containerSize = ((EditText) findViewById(R.id.containerSizeOther)).getText().toString().trim();
                    } else if(getString(R.string.select_container_size).equalsIgnoreCase(containerSize)) {
                        containerSize = "";
                    }

                    String chassisType = chassisTypeSpinner.getSelectedItem().toString();
                    if(getString(R.string.lbl_other).equalsIgnoreCase(chassisType)) {
                        chassisType = ((EditText) findViewById(R.id.chassisTypeOther)).getText().toString().trim();
                    } else if(getString(R.string.select_chassis_type).equalsIgnoreCase(chassisType)) {
                        chassisType = "";
                    }

                    String chassisSize = chassisSizeSpinner.getSelectedItem().toString();
                    if(getString(R.string.lbl_other).equalsIgnoreCase(chassisSize)) {
                        chassisSize = ((EditText) findViewById(R.id.chassisSizeOther)).getText().toString().trim();
                    } else if(getString(R.string.select_chassis_size).equalsIgnoreCase(chassisSize)) {
                        chassisSize = "";
                    }


                    Integer[] categories = new Integer[]{17, 5, 5};
                    String[] categoriesName = new String[]{"Street Interchange Details", "Equipment Location", "Original Interchange Location"};
                    String[] labelArray = new String[]{"CONTAINER PROVIDER NAME", "CONTAINER PROVIDER SCAC",
                            "MOTOR CARRIER A'S NAME", "MOTOR CARRIER A'S SCAC",
                            "MOTOR CARRIER B'S NAME", "MOTOR CARRIER B'S SCAC",
                            "TYPE OF INTERCHANGE", "CONTAINER TYPE",
                            "CONTAINER SIZE", "IMPORT B/L", "EXPORT BOOKING #",
                            "CONTAINER #", "CHASSIS #", "CHASSIS IEP SCAC",
                            "CHASSIS TYPE", "CHASSIS SIZE", "GENSET #",
                            "LOCATION NAME", "LOCATION ADDRESS", "ZIP CODE", "CITY", "STATE",
                            "LOCATION NAME", "LOCATION ADDRESS", "ZIP CODE", "CITY", "STATE"};
                    String[] valueArray = new String[]{epCompanyName.getText().toString(), epScac.getText().toString(),
                            mcACompanyName.getText().toString(), mcAScac.getText().toString(),
                            mcBCompanyName.getText().toString(), mcBScac.getText().toString(),
                            typeOfInterchangeSpinner.getSelectedItem().toString(), containerType,
                            containerSize, (null != importBL.getText() ? importBL.getText().toString().toUpperCase() : ""),
                            exportBookingNumber.getText().toString().toUpperCase(), containerNumber.getText().toString(),
                            chassisNumber.getText().toString(), iepScac.getText().toString(),
                            chassisType, chassisSize,
                            gensetNumber.getText().toString(),
                            equipLocationName.getText().toString(), equipLocationAddress.getText().toString(),
                            equipLocationZipCode.getText().toString(), equipLocationCity.getText().toString(),
                            equipLocationState.getText().toString(),
                            originLocationName.getText().toString(), originLocationAddress.getText().toString(),
                            originLocationZipCode.getText().toString(), originLocationCity.getText().toString(),
                            originLocationState.getText().toString()
                        };

                    List<FieldInfo> fieldInfoList = SIAUtility.prepareAndGetFieldInfoList(categories, categoriesName, labelArray, valueArray);

                    if(errorMessage.getCode() == 1) {
                        editor.putString(GlobalVariables.KEY_IEP_SCAC_MESSAGE, errorMessage.getMessage());
                    }

                    SIAUtility.setList(editor, "fieldInfoList", fieldInfoList);

                    setAndGetInterchangeRequestData();
                    SIAUtility.setObject(editor, GlobalVariables.KEY_INTERCHANGE_REQUESTS_OBJ, ir);

                    editor.commit();

                    Intent intent = new Intent(InitiateInterchangeActivity.this, VerifyActivity.class);
                    startActivity(intent);
                    finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */

                } else  {

                        try {
                            new ViewDialog().showDialog(InitiateInterchangeActivity.this, dialogTitle, errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                        } catch(Exception e) {
                            new ViewDialog().showDialog(InitiateInterchangeActivity.this, dialogTitle, getString(R.string.msg_error_try_after_some_time));
                        }

                    Log.v("log_tag", "verify result Error :=> ");
                }

            } catch (Exception e) {
                Log.v("log_tag", "Error ", e);
            }

        }

    }


    class ExecuteSetupPageTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            ApiResponse apiResponse = RestApiClient.callGetApi(getString(R.string.base_url) + getString(R.string.api_setup_page));
            urlResponse = apiResponse.getMessage();
            urlResponseCode = apiResponse.getCode();
            return urlResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);

            try {
                Log.v("log_tag", "API setupPage urlResponseCode:=>" + urlResponseCode);
                Log.v("log_tag", "API setupPage result:=> " + result);
                Gson gson = new Gson();

                if (urlResponseCode == 200) {
                    JsonObject jObj = gson.fromJson(result, JsonObject.class);
                    Type listType = new TypeToken<List<FormOption>>() {}.getType();

                    List<FormOption> contTypeList = gson.fromJson(jObj.get("contTypeList"), listType);
                    List<FormOption> contSizeList = gson.fromJson(jObj.get("contSizeList"), listType);
                    List<FormOption> chassisTypeList = gson.fromJson(jObj.get("chassisTypeList"), listType);
                    List<FormOption> chassisSizeList = gson.fromJson(jObj.get("chassisSizeList"), listType);

                    Map<String, Integer> dropDownMap = new HashMap<>();
                    List<String> setupList = new ArrayList<>();
                    int cnt = 1;
                    setupList.add(getString(R.string.select_container_type));
                    for(FormOption form : contTypeList){
                        setupList.add(form.getValue());
                        dropDownMap.put(form.getValue(), cnt++);
                    }

                    containerTypeAdapter = new ContainerTypeAdapter(context, setupList.toArray(new String[0]));
                    containerTypeSpinner.setAdapter(containerTypeAdapter);
                    if(null != ir && null != ir.getContType() && ir.getContType().trim().length() > 0 && !ir.getContType().equalsIgnoreCase(getString(R.string.select_container_type))) {
                        if(setupList.contains(ir.getContType())) {
                            containerTypeSpinner.setSelection(dropDownMap.get(ir.getContType()));
                        } else {
                            containerTypeSpinner.setSelection(dropDownMap.get(getString(R.string.lbl_other)));
                            findViewById(R.id.containerTypeOtherLayout).setVisibility(View.VISIBLE);
                            ((EditText) findViewById(R.id.containerTypeOther)).setText(ir.getContType());
                        }

                    }

                    cnt = 1;
                    dropDownMap.clear();
                    setupList = new ArrayList<>();
                    setupList.add(getString(R.string.select_container_size));
                    for(FormOption form : contSizeList){
                        setupList.add(form.getValue());
                        dropDownMap.put(form.getValue(), cnt++);
                    }
                    containerSizeAdapter = new ContainerSizeAdapter(context, setupList.toArray(new String[0]));
                    containerSizeSpinner.setAdapter(containerSizeAdapter);
                    if(null != ir && null != ir.getContSize() && ir.getContSize().trim().length() > 0 && !ir.getContSize().equalsIgnoreCase(getString(R.string.select_container_size))) {
                        if(setupList.contains(ir.getContSize())) {
                            containerSizeSpinner.setSelection(dropDownMap.get(ir.getContSize()));
                        } else {
                            containerSizeSpinner.setSelection(dropDownMap.get(getString(R.string.lbl_other)));
                            findViewById(R.id.containerSizeOtherLayout).setVisibility(View.VISIBLE);
                            ((EditText) findViewById(R.id.containerSizeOther)).setText(ir.getContSize());
                        }
                    }

                    cnt = 1;
                    dropDownMap.clear();
                    setupList = new ArrayList<>();
                    setupList.add(getString(R.string.select_chassis_type));
                    for(FormOption form : chassisTypeList){
                        setupList.add(form.getValue());
                        dropDownMap.put(form.getValue(), cnt++);
                    }
                    chassisTypeAdapter = new ChassisTypeAdapter(context, setupList.toArray(new String[0]));
                    chassisTypeSpinner.setAdapter(chassisTypeAdapter);
                    if(null != ir && null != ir.getChassisType() && ir.getChassisType().trim().length() > 0 && !ir.getChassisType().equalsIgnoreCase(getString(R.string.select_chassis_type))) {
                        if(setupList.contains(ir.getChassisType())) {
                            chassisTypeSpinner.setSelection(dropDownMap.get(ir.getChassisType()));
                        } else {
                            chassisTypeSpinner.setSelection(dropDownMap.get(getString(R.string.lbl_other)));
                            findViewById(R.id.chassisTypeOtherLayout).setVisibility(View.VISIBLE);
                            ((EditText) findViewById(R.id.chassisTypeOther)).setText(ir.getChassisType());
                        }
                    }

                    cnt = 1;
                    dropDownMap.clear();
                    setupList = new ArrayList<>();
                    setupList.add(getString(R.string.select_chassis_size));
                    for(FormOption form : chassisSizeList){
                        setupList.add(form.getValue());
                        dropDownMap.put(form.getValue(), cnt++);
                    }
                    chassisSizeAdapter = new ChassisSizeAdapter(context, setupList.toArray(new String[0]));
                    chassisSizeSpinner.setAdapter(chassisSizeAdapter);
                    if(null != ir && null != ir.getChassisSize() && ir.getChassisSize().trim().length() > 0 && !ir.getChassisSize().equalsIgnoreCase(getString(R.string.select_chassis_size))) {
                        if(setupList.contains(ir.getChassisSize())) {
                            chassisSizeSpinner.setSelection(dropDownMap.get(ir.getChassisSize()));
                        } else {
                            chassisSizeSpinner.setSelection(dropDownMap.get(getString(R.string.lbl_other)));
                            findViewById(R.id.chassisSizeOtherLayout).setVisibility(View.VISIBLE);
                            ((EditText) findViewById(R.id.chassisSizeOther)).setText(ir.getChassisSize());
                        }
                    }


                } else {

                    try {
                        ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                        new ViewDialog().showDialog(InitiateInterchangeActivity.this, dialogTitle, errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                    } catch(Exception e) {
                        new ViewDialog().showDialog(InitiateInterchangeActivity.this, dialogTitle, getString(R.string.msg_error_try_after_some_time));
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
        if (Internet_Check.checkInternetConnection(context)) {

            // Create custom dialog object
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);

            // Include dialog.xml file
            dialog.setContentView(R.layout.dialog_popup);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Set dialog title
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

                    Intent intent = null;

                    baseOriginFrom = sharedPref.getString(GlobalVariables.KEY_BASE_ORIGIN_FROM, "");
                    if (null != baseOriginFrom && GlobalVariables.ORIGIN_FROM_NOTIF_AVAIl.equalsIgnoreCase(baseOriginFrom)) {
                        intent = new Intent(InitiateInterchangeActivity.this, ListNotifAvailActivity.class);

                    } else {
                        intent = new Intent(InitiateInterchangeActivity.this, DashboardActivity.class);
                    }
                    startActivity(intent);
                    finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */

                }
            });


        } else {
            Intent intent = new Intent(InitiateInterchangeActivity.this, NoInternetActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            goToPreviousPage();
        }

        return super.onKeyDown(keyCode, event);
    }

}
