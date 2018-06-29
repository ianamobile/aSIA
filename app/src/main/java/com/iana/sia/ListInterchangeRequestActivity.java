package com.iana.sia;

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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.iana.sia.model.IanaLocations;
import com.iana.sia.model.InterchangeRequests;
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
import java.util.HashMap;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_interchange_request);

        progressBar = findViewById(R.id.processingBar);

        // below code is used to restrict auto populate keypad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        showActionBar();
        ((TextView) findViewById(R.id.title)).setText(R.string.title_search_location);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setText(R.string.title_back);
        backBtn.setVisibility(View.VISIBLE);

        dialogTitle = getString(R.string.dialog_title_interchange_request_list);

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        irSearch = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_INTERCHANGE_REQUESTS_SEARCH_OBJ, InterchangeRequestsSearch.class);
        siaSecurityObj = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_SECURITY_OBJ, SIASecurityObj.class);

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
                Log.v("log_tag", "ListBadOrderActivity: urlResponseCode:=>" + urlResponseCode);
                Log.v("log_tag", "ListBadOrderActivity: result:=> " + result);
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
                }

            } catch (Exception e) {
                Log.v("log_tag", "Error ", e);
            }

            progressBar.setVisibility(View.GONE);
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

            } else if(status.equalsIgnoreCase(GlobalVariables.STATUS_APPROVED)) {
                v.findViewById(R.id.leftPatternColor).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color_approved));

            } else if(status.equalsIgnoreCase(GlobalVariables.STATUS_REJECTED)) {
                v.findViewById(R.id.leftPatternColor).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color_rejected));
                ((TextView) v.findViewById(R.id.approvedOrRejectedDateTimeLbl)).setText("REJECTED DATE TIME");

            } else if(status.equalsIgnoreCase(GlobalVariables.STATUS_CANCELLED)) {
                v.findViewById(R.id.leftPatternColor).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color_cancelled));

            } else if(status.equalsIgnoreCase(GlobalVariables.STATUS_ONHOLD)) {
                v.findViewById(R.id.leftPatternColor).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_color_onhold));
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


            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!siaSecurityObj.getRoleName().equalsIgnoreCase(GlobalVariables.ROLE_TPU)) {


                    }
//                    SIAUtility.setObject(editor, GlobalVariables.KEY_INITIATE_INTERCHANGE, );
//                    editor.putString(GlobalVariables.KEY_LOCATION_NAME, dataList.get(position).getLocName());

//                    editor.commit();

//                    Intent intent = new Intent(ListInterchangeRequestActivity.this, StreetTurnActivity.class);
//                    startActivity(intent);
//                    finish(); /* This method will not display login page when click back (return) from phone */
                    /* End */
                }
            });

            return v;
        }
    } /* End */


    void goToPreviousPage() {
        if (Internet_Check.checkInternetConnection(getApplicationContext())) {

            Intent intent = new Intent(ListInterchangeRequestActivity.this, SearchInterchangeRequestActivity.class);
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
