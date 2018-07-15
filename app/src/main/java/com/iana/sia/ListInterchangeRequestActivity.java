package com.iana.sia;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.iana.sia.model.FieldInfo;
import com.iana.sia.model.IanaLocations;
import com.iana.sia.model.InterchangeRequests;
import com.iana.sia.model.InterchangeRequestsJson;
import com.iana.sia.model.InterchangeRequestsSearch;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ListInterchangeRequestActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<InterchangeRequests> dataList= new ArrayList<>();
    InterchangeRequestsListAdapter adapter;

    String urlResponse;
    int urlResponseCode;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Button backBtn;

    ProgressBar progressBar;

    InterchangeRequests interchangeRequests;
    InterchangeRequestsSearch irSearch;

    String dialogTitle;

    Map<Integer, Integer> lastRecordMap = new HashMap<>();

    SIASecurityObj siaSecurityObj;

    String requestFrom = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_interchange_request);

        progressBar = findViewById(R.id.processingBar);

        // below code is used to restrict auto populate keypad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        showActionBar();
        ((TextView) findViewById(R.id.title)).setText(R.string.title_list_results);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setText(R.string.title_back);
        backBtn.setVisibility(View.VISIBLE);

        dialogTitle = getString(R.string.title_list_results);

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        irSearch = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_INTERCHANGE_REQUESTS_SEARCH_OBJ, InterchangeRequestsSearch.class);
        siaSecurityObj = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_SECURITY_OBJ, SIASecurityObj.class);
        requestFrom = sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "");

        if(null == irSearch) {
            irSearch = new InterchangeRequestsSearch();
        }

        Log.v("log_tag", "ListInterchangeRequestActivity: irSearch:=> " + irSearch);

        String requestString = "accessToken="+siaSecurityObj.getAccessToken()+"&startDate="+irSearch.getFromDate()+"&endDate="+irSearch.getToDate()+
                                "&status="+irSearch.getStatus()+"&containerNo="+irSearch.getContNum()+"&bookingNo="+irSearch.getBookingNum()+
                                "&offset="+getString(R.string.default_offset) + "&limit=" + getString(R.string.limit);

        if(siaSecurityObj.getRoleName().equalsIgnoreCase(GlobalVariables.ROLE_MC) ||
                siaSecurityObj.getRoleName().equalsIgnoreCase(GlobalVariables.ROLE_IDD) ||
                (siaSecurityObj.getRoleName().equalsIgnoreCase(GlobalVariables.ROLE_SEC) && null != siaSecurityObj.getMemType() &&
                        siaSecurityObj.getMemType().equalsIgnoreCase(GlobalVariables.ROLE_MC))) {

            requestString = requestString + "&epSCAC="+irSearch.getScac();
        } else {
            requestString = requestString + "&mcSCAC="+irSearch.getScac();
        }

        if(requestFrom != null && requestFrom.equalsIgnoreCase(GlobalVariables.MENU_TITLE_PENDING_INTERCHANGE_REQUESTS)) {
            requestString = "accessToken="+siaSecurityObj.getAccessToken()+"&actionRequired=Y"+
                    "&offset="+getString(R.string.default_offset) + "&limit=" + getString(R.string.limit);
        }


        // code to disable background functionality when progress bar starts
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        progressBar.setVisibility(View.VISIBLE);
        Log.v("log_tag", "ListInterchangeRequestActivity: requestString:=> " + requestString);
        new ExecuteInterchangeRequestSearchTask(requestString).execute();

        listView = findViewById(R.id.listView);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
//                Log.v("log_tag", "firstVisibleItem:=>"+firstVisibleItem);
                int limit = Integer.valueOf(getString(R.string.limit));
                if (dataList.size() >= 10 && lastInScreen == dataList.size() && (dataList.size() % limit) == 0) {
                    String requestString = "accessToken="+siaSecurityObj.getAccessToken()+"&startDate="+irSearch.getFromDate()+"&endDate="+irSearch.getToDate()+
                            "&status="+irSearch.getStatus()+"&containerNo="+irSearch.getContNum()+"&bookingNo="+irSearch.getBookingNum()+
                            "&offset=" + ((totalItemCount / 10) + 1) + "&limit=" + limit;

                    if(requestFrom != null && requestFrom.equalsIgnoreCase(GlobalVariables.MENU_TITLE_PENDING_INTERCHANGE_REQUESTS)) {
                        requestString = "accessToken="+siaSecurityObj.getAccessToken()+"&actionRequired=Y"+
                                "&offset=" + ((totalItemCount / 10) + 1) + "&limit=" + limit;
                    }

                    if (lastRecordMap.size() == 0 || !lastRecordMap.containsKey(lastInScreen)) {
//                        Log.v("log_tag", "ListInterchangeRequestActivity on scroll: requestString:=> " + requestString);
                        lastRecordMap.put(lastInScreen, lastInScreen);

                        progressBar.setVisibility(View.VISIBLE);
                        new ExecuteInterchangeRequestSearchTask(requestString).execute();
                    }

                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToPreviousPage();
            }
        });
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

        /* code to perform Location Search functionality starts */

    class ExecuteInterchangeRequestSearchTask extends AsyncTask<String, Integer, String> {
        String requestString;

        public ExecuteInterchangeRequestSearchTask(String requestString) {
            this.requestString = requestString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.v("log_tag", "doInBackground:=>" + (getString(R.string.base_url) + getString(R.string.api_sia_lookup) + "?" + requestString));
            ApiResponse apiResponse = RestApiClient.callGetApi(getString(R.string.base_url) + getString(R.string.api_sia_lookup) + "?" + requestString);
            urlResponse = apiResponse.getMessage();
            urlResponseCode = apiResponse.getCode();
            return urlResponse;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                Log.v("log_tag", "ListInterchangeRequestActivity: urlResponseCode:=>" + urlResponseCode);
//                Log.v("log_tag", "ListInterchangeRequestActivity: result:=> " + result);
                Gson gson = new Gson();

                if (urlResponseCode == 200) {
                    JsonObject jObj = gson.fromJson(result, JsonObject.class);
                    Type listType = new TypeToken<List<InterchangeRequests>>() {}.getType();
                    List<InterchangeRequests> interchangeRequestsList = gson.fromJson(jObj.get("listInterchangeRequest"), listType);

                    dataList.addAll(interchangeRequestsList);

                    if(dataList.size() <= 10) {
                        adapter = new InterchangeRequestsListAdapter(getApplicationContext(), dataList);
                        listView.setAdapter(adapter);

                    } else {
                        adapter.notifyDataSetChanged();
                    }

                } else {
                    try {
                        ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                        new ViewDialog().showDialog(ListInterchangeRequestActivity.this, dialogTitle, errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                    } catch(Exception e) {
                        new ViewDialog().showDialog(ListInterchangeRequestActivity.this, dialogTitle, getString(R.string.msg_error_try_after_some_time));
                    }

//                    goToPreviousPage();

                }

            } catch (Exception e) {
                Log.v("log_tag", "Error ", e);
            }

            progressBar.setVisibility(View.GONE);

            // code to regain disable backend functionality end
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    class InterchangeRequestsListAdapter extends BaseAdapter {

        private Context mContext;
        private List<InterchangeRequests> mProductList;

        // Constructor
        public InterchangeRequestsListAdapter(Context mContext, List<InterchangeRequests> mProductList) {
            this.mContext = mContext;
            this.mProductList = mProductList;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = View.inflate(mContext, R.layout.interchange_request_list_view, null);

            String status = dataList.get(position).getStatus();

            if(null == dataList.get(position).getIntchgType() || dataList.get(position).getIntchgType().length() <=0) {
                ((TextView) v.findViewById(R.id.irRequestType)).setText("STREET TURN");
            } else {
                ((TextView) v.findViewById(R.id.irRequestType)).setText("STREET INTERCHANGE");
            }
            if(status.equalsIgnoreCase(GlobalVariables.STATUS_PENDING)) {
                v.findViewById(R.id.leftPatternColor).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color_pending));
                ((TextView) v.findViewById(R.id.approvedOrRejectedDateTimeLbl)).setText(getString(R.string.lbl_approved_pending_date));

            } else if(status.equalsIgnoreCase(GlobalVariables.STATUS_APPROVED)) {
                v.findViewById(R.id.leftPatternColor).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color_approved));
                ((TextView) v.findViewById(R.id.approvedOrRejectedDateTimeLbl)).setText(getString(R.string.lbl_approved_pending_date));

            } else if(status.equalsIgnoreCase(GlobalVariables.STATUS_REJECTED)) {
                v.findViewById(R.id.leftPatternColor).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color_rejected));
                ((TextView) v.findViewById(R.id.approvedOrRejectedDateTimeLbl)).setText(getString(R.string.lbl_rejected_date));

            } else if(status.equalsIgnoreCase(GlobalVariables.STATUS_CANCELLED)) {
                v.findViewById(R.id.leftPatternColor).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color_cancelled));
                ((TextView) v.findViewById(R.id.approvedOrRejectedDateTimeLbl)).setText(getString(R.string.lbl_cancelled_date));

            } else if(status.equalsIgnoreCase(GlobalVariables.STATUS_ONHOLD)) {
                v.findViewById(R.id.leftPatternColor).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color_onhold));
                ((TextView) v.findViewById(R.id.approvedOrRejectedDateTimeLbl)).setText(getString(R.string.lbl_onhold_date));
            }

            ((TextView) v.findViewById(R.id.actionRequired)).setText(dataList.get(position).getActionRequired());
            ((TextView) v.findViewById(R.id.createdDate)).setText(dataList.get(position).getCreatedDate());
            ((TextView) v.findViewById(R.id.containerNumber)).setText(dataList.get(position).getContNum());
            ((TextView) v.findViewById(R.id.exportBookingNumber)).setText(dataList.get(position).getBookingNum());
            ((TextView) v.findViewById(R.id.epCompanyName)).setText(dataList.get(position).getEpCompanyName());
            ((TextView) v.findViewById(R.id.epScac)).setText(dataList.get(position).getEpScacs());
            ((TextView) v.findViewById(R.id.mcACompanyName)).setText(dataList.get(position).getMcACompanyName());
            ((TextView) v.findViewById(R.id.mcAScac)).setText(dataList.get(position).getMcAScac());
            ((TextView) v.findViewById(R.id.mcBCompanyName)).setText(dataList.get(position).getMcBCompanyName());
            ((TextView) v.findViewById(R.id.mcBScac)).setText(dataList.get(position).getMcBScac());
            ((TextView) v.findViewById(R.id.status)).setText(status);
            ((TextView) v.findViewById(R.id.approvedOrRejectedDateTime)).setText(dataList.get(position).getModifiedDate());

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!siaSecurityObj.getRoleName().equalsIgnoreCase(GlobalVariables.ROLE_TPU) ||
                        (siaSecurityObj.getRoleName().equalsIgnoreCase(GlobalVariables.ROLE_TPU) &&
                                null != siaSecurityObj.getScac() && siaSecurityObj.getScac().trim().length() > 0)) {


                        // code to get interchange request details to perform operation
                        //api_get_interchange_request_details
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("irId", dataList.get(position).getIrId());
                        jsonObject.addProperty("accessToken", siaSecurityObj.getAccessToken());

                        // code to disable background functionality when progress bar starts
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        progressBar.setVisibility(View.VISIBLE);

                        Log.v("log_tag", "ListInterchangeRequestActivity jsonObject:=> " + jsonObject.toString());
                        new ExecuteTaskToGetInterchangeRequestDetails(jsonObject.toString()).execute();

                    }
                }
            });

            return v;
        }
    } /* End */


    /* code to get interchange request details functionality starts */

    class ExecuteTaskToGetInterchangeRequestDetails extends AsyncTask<String, Integer, String> {
        String requestString;

        public ExecuteTaskToGetInterchangeRequestDetails(String requestString) {
            this.requestString = requestString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            ApiResponse apiResponse = RestApiClient.callPostApi(requestString, getString(R.string.base_url) + getString(R.string.api_get_interchange_request_details));
            urlResponse = apiResponse.getMessage();
            urlResponseCode = apiResponse.getCode();
            return urlResponse;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                Log.v("log_tag", "ListInterchangeRequestActivity: ExecuteTaskToGetInterchangeRequestDetails: urlResponseCode:=>" + urlResponseCode);
                Log.v("log_tag", "ListInterchangeRequestActivity: ExecuteTaskToGetInterchangeRequestDetails: result:=> " + result);
                Gson gson = new Gson();

                if (urlResponseCode == 200) {
                    InterchangeRequestsJson interchangeRequestsJson = gson.fromJson(result, InterchangeRequestsJson.class);
                    Log.v("log_tag", "interchangeRequestsJson:=>"+interchangeRequestsJson);

                    Integer[] categories = null;
                    String[] categoriesName = null;
                    String[] labelArray = null;
                    String[] valueArray = null;

                    List<String> labelList = new ArrayList<>();
                    List<String> valueList = new ArrayList<>();

                    List<Integer> categoriesList = new ArrayList<>();
                    List<String> categoriesNameList = new ArrayList<>();

                    InterchangeRequests ir = interchangeRequestsJson.getInterchangeRequests();

                    if(null == ir.getIntchgType() || ir.getIntchgType().trim().length()<= 0) {
                        categoriesList.add(9);
                        categoriesList.add(5);

                        categoriesNameList.add("Street Turn Details");
                        categoriesNameList.add("Original Location");

                        labelList.add("CONTAINER PROVIDER NAME");
                        labelList.add("CONTAINER PROVIDER SCAC");
                        labelList.add("MOTOR CARRIER A'S NAME");
                        labelList.add("MOTOR CARRIER A'S SCAC");
                        labelList.add("IMPORT B/L");
                        labelList.add("EXPORT BOOKING#");
                        labelList.add("CONTAINER#");
                        labelList.add("CHASSIS#");
                        labelList.add("CHASSIS IEP SCAC");
                        labelList.add("LOCATION NAME");
                        labelList.add("LOCATION ADDRESS");
                        labelList.add("ZIP CODE");
                        labelList.add("CITY");
                        labelList.add("STATE");

                        valueList.add(ir.getEpCompanyName());
                        valueList.add(ir.getEpScacs());
                        valueList.add(ir.getMcACompanyName());
                        valueList.add(ir.getMcAScac());
                        valueList.add(ir.getImportBookingNum());
                        valueList.add(ir.getBookingNum());
                        valueList.add(ir.getContNum());
                        valueList.add(ir.getChassisNum());
                        valueList.add(ir.getIepScac());
                        valueList.add(ir.getOriginLocNm());
                        valueList.add(ir.getOriginLocAddr());
                        valueList.add(ir.getOriginLocZip());
                        valueList.add(ir.getOriginLocCity());
                        valueList.add(ir.getOriginLocState());

                    } else {

                        categoriesList.add(17);
                        categoriesList.add(5);
                        categoriesList.add(5);

                        categoriesNameList.add("Street Interchange Details");
                        categoriesNameList.add("Equipment Location");
                        categoriesNameList.add("Original Location");


                        labelList.add("CONTAINER PROVIDER NAME");
                        labelList.add("CONTAINER PROVIDER SCAC");
                        labelList.add("MOTOR CARRIER A'S NAME");
                        labelList.add("MOTOR CARRIER A'S SCAC");
                        labelList.add("MOTOR CARRIER B'S NAME");
                        labelList.add("MOTOR CARRIER B'S SCAC");
                        labelList.add("TYPE OF INTERCHANGE");
                        labelList.add("CONTAINER TYPE");
                        labelList.add("CONTAINER SIZE");
                        labelList.add("IMPORT B/L");
                        labelList.add("EXPORT BOOKING#");
                        labelList.add("CONTAINER#");
                        labelList.add("CHASSIS#");
                        labelList.add("CHASSIS IEP SCAC");
                        labelList.add("CHASSIS TYPE");
                        labelList.add("CHASSIS SIZE");
                        labelList.add("GENSET#");
                        labelList.add("LOCATION NAME");
                        labelList.add("LOCATION ADDRESS");
                        labelList.add("ZIP CODE");
                        labelList.add("CITY");
                        labelList.add("STATE");
                        labelList.add("LOCATION NAME");
                        labelList.add("LOCATION ADDRESS");
                        labelList.add("ZIP CODE");
                        labelList.add("CITY");
                        labelList.add("STATE");

                        valueList.add(ir.getEpCompanyName());
                        valueList.add(ir.getEpScacs());
                        valueList.add(ir.getMcACompanyName());
                        valueList.add(ir.getMcAScac());
                        valueList.add(ir.getMcBCompanyName());
                        valueList.add(ir.getMcBScac());
                        valueList.add(ir.getIntchgType());
                        valueList.add(ir.getContType());
                        valueList.add(ir.getContSize());
                        valueList.add(ir.getImportBookingNum());
                        valueList.add(ir.getBookingNum());
                        valueList.add(ir.getContNum());
                        valueList.add(ir.getChassisNum());
                        valueList.add(ir.getIepScac());
                        valueList.add(ir.getChassisType());
                        valueList.add(ir.getChassisSize());
                        valueList.add(ir.getGensetNum());

                        valueList.add(ir.getEquipLocNm());
                        valueList.add(ir.getEquipLocAddr());
                        valueList.add(ir.getEquipLocZip());
                        valueList.add(ir.getEquipLocCity());
                        valueList.add(ir.getEquipLocState());

                        valueList.add(ir.getOriginLocNm());
                        valueList.add(ir.getOriginLocAddr());
                        valueList.add(ir.getOriginLocZip());
                        valueList.add(ir.getOriginLocCity());
                        valueList.add(ir.getOriginLocState());

                    }
                        if(null != interchangeRequestsJson.getUiiaExhibitDataList() && interchangeRequestsJson.getUiiaExhibitDataList().size() > 0) {
                            categoriesList.add(interchangeRequestsJson.getUiiaExhibitDataList().size());
                            categoriesNameList.add("Equipment Condition (per UIIA Exhibit A)");

                            for(int i= 0; i < interchangeRequestsJson.getUiiaExhibitDataList().size(); i++) {
                                labelList.add(interchangeRequestsJson.getUiiaExhibitDataList().get(i).get("item").toString());
                                valueList.add(interchangeRequestsJson.getUiiaExhibitDataList().get(i).get("item_desc").toString());
                            }
                        }

                        if(null != ir.getRemarks() && ir.getRemarks().length() > 0) {
                            String[] remarksArray = ir.getRemarks().split("\\|\\|");
                            categoriesList.add(remarksArray.length);
                            categoriesNameList.add("Previous Comments");
                            for(int i=0;i<remarksArray.length;i++) {
                                labelList.add("Remarks"+(i+1));
                                valueList.add(remarksArray[i]);
                            }
                        }

                    categories = categoriesList.toArray(new Integer[0]);
                    categoriesName = categoriesNameList.toArray(new String[0]);
                    labelArray = labelList.toArray(new String[0]);
                    valueArray = valueList.toArray(new String[0]);

                    List<FieldInfo> fieldInfoList = SIAUtility.prepareAndGetFieldInfoList(categories, categoriesName, labelArray, valueArray);
                    SIAUtility.setList(editor, "fieldInfoList", fieldInfoList);

                    SIAUtility.setObject(editor, GlobalVariables.KEY_OPERATION_IR_OBJ, interchangeRequestsJson);

                    editor.commit();

                    Intent intent = new Intent(ListInterchangeRequestActivity.this, InterchangeRequestOperationActivity.class);
                    startActivity(intent);
                    finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */

                } else {
                    try {
                        ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                        new ViewDialog().showDialog(ListInterchangeRequestActivity.this, dialogTitle, errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                    } catch(Exception e) {
                        new ViewDialog().showDialog(ListInterchangeRequestActivity.this, dialogTitle, getString(R.string.msg_error_try_after_some_time));
                    }
                }

            } catch (Exception e) {
                Log.v("log_tag", "Error ", e);
            }

            progressBar.setVisibility(View.GONE);

            // code to regain disable backend functionality end
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }



    void goToPreviousPage() {
        if (Internet_Check.checkInternetConnection(getApplicationContext())) {

            Intent intent = null;
            if(requestFrom != null && requestFrom.equalsIgnoreCase(GlobalVariables.MENU_TITLE_PENDING_INTERCHANGE_REQUESTS)) {
                intent = new Intent(ListInterchangeRequestActivity.this, DashboardActivity.class);

            } else {
                intent = new Intent(ListInterchangeRequestActivity.this, SearchInterchangeRequestActivity.class);
            }

            startActivity(intent);
            finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */
        } else {
            Intent intent = new Intent(ListInterchangeRequestActivity.this, NoInternetActivity.class);
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
