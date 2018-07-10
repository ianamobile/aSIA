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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.iana.sia.model.InterchangeRequests;
import com.iana.sia.model.InterchangeRequestsSearch;
import com.iana.sia.model.NotifAvailSearch;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListNotifAvailActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<NotificationAvail> dataList= new ArrayList<>();
    NotifAvailListAdapter adapter;

    String urlResponse;
    int urlResponseCode;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Button backBtn;

    ProgressBar progressBar;

    NotifAvailSearch naSearch;

    String dialogTitle;

    Map<Integer, Integer> lastRecordMap = new HashMap<>();

    SIASecurityObj siaSecurityObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_notif_avail);

        progressBar = findViewById(R.id.processingBar);

        // below code is used to restrict auto populate keypad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        showActionBar();
        ((TextView) findViewById(R.id.title)).setText(R.string.title_list_results);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setText(R.string.title_back);
        backBtn.setVisibility(View.VISIBLE);

        dialogTitle = getString(R.string.dialog_title_interchange_request_list);

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        siaSecurityObj = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_SECURITY_OBJ, SIASecurityObj.class);

        naSearch = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_NOTIF_AVAIL_SEARCH_OBJ, NotifAvailSearch.class);

        Log.v("log_tag", "ListNotifAvailActivity: naSearch:=> " + naSearch);

        String requestString = "accessToken="+siaSecurityObj.getAccessToken()+"&startDate="+naSearch.getFromDate()+"&endDate="+naSearch.getToDate()+
                "&containerNo="+naSearch.getContainerNumber()+"&epSCAC="+naSearch.getEpScac()+"&mcSCAC="+naSearch.getMcScac()+
                "&offset="+naSearch.getOffset() + "&limit=" + naSearch.getLimit();


        // code to disable background functionality when progress bar starts
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        progressBar.setVisibility(View.VISIBLE);

        Log.v("log_tag", "ListInterchangeRequestActivity: requestString:=> " + requestString);
        new ExecuteNotifAvailSearchTask(requestString).execute();

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
                    String requestString = "accessToken="+siaSecurityObj.getAccessToken()+"&startDate="+naSearch.getFromDate()+"&endDate="+naSearch.getToDate()+
                            "&containerNo="+naSearch.getContainerNumber()+"&epSCAC="+naSearch.getEpScac()+"&mcSCAC="+naSearch.getMcScac()+
                            "&offset=" + ((totalItemCount / 10) + 1) + "&limit=" + limit;

                    if (lastRecordMap.size() == 0 || !lastRecordMap.containsKey(lastInScreen)) {
//                        Log.v("log_tag", "ListInterchangeRequestActivity on scroll: requestString:=> " + requestString);
                        lastRecordMap.put(lastInScreen, lastInScreen);

                        progressBar.setVisibility(View.VISIBLE);
                        new ExecuteNotifAvailSearchTask(requestString).execute();
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

    /* code to perform notification available (equipment pool) search functionality starts */

    class ExecuteNotifAvailSearchTask extends AsyncTask<String, Integer, String> {
        String requestString;

        public ExecuteNotifAvailSearchTask(String requestString) {
            this.requestString = requestString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            ApiResponse apiResponse = RestApiClient.callGetApi(getString(R.string.base_url) + getString(R.string.api_get_equipment_list) + "?" + requestString);
            urlResponse = apiResponse.getMessage();
            urlResponseCode = apiResponse.getCode();
            return urlResponse;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                Log.v("log_tag", "ListNotifAvailActivity: urlResponseCode:=>" + urlResponseCode);
                Log.v("log_tag", "ListNotifAvailActivity: result:=> " + result);
                Gson gson = new Gson();

                if (urlResponseCode == 200) {
                    JsonObject jObj = gson.fromJson(result, JsonObject.class);
                    Type listType = new TypeToken<List<NotificationAvail>>() {}.getType();
                    List<NotificationAvail> notificationAvailList = gson.fromJson(jObj.get("notificationAvailList"), listType);

                    dataList.addAll(notificationAvailList);

                    if(dataList.size() <= 10) {
                        adapter = new NotifAvailListAdapter(getApplicationContext(), dataList);
                        listView.setAdapter(adapter);

                    } else {
                        adapter.notifyDataSetChanged();
                    }

                } else {
                    try {
                        ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                        new ViewDialog().showDialog(ListNotifAvailActivity.this, dialogTitle, errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                    } catch(Exception e) {
                        new ViewDialog().showDialog(ListNotifAvailActivity.this, dialogTitle, getString(R.string.msg_error_try_after_some_time));
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

    class NotifAvailListAdapter extends BaseAdapter {

        private Context mContext;
        private List<NotificationAvail> notificationAvailList;

        // Constructor
        public NotifAvailListAdapter(Context mContext, List<NotificationAvail> notificationAvailList) {
            this.mContext = mContext;
            this.notificationAvailList = notificationAvailList;
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
            View v = View.inflate(mContext, R.layout.notif_avail_list_view, null);

            String showDeleteBtn = dataList.get(position).getShowDeleteBtn();

            if(null != showDeleteBtn && GlobalVariables.Y.equalsIgnoreCase(showDeleteBtn)) {
                Button deleteBtn = v.findViewById(R.id.deleteBtn);
                deleteBtn.setVisibility(View.VISIBLE);
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        new ExecuteDeleteNotifAvailTask("naId="+dataList.get(position).getNaId()+"&accessToken="+siaSecurityObj.getAccessToken()).execute();
                    }
                });

            } else {
                v.findViewById(R.id.deleteBtn).setVisibility(View.GONE);
            }

            ((TextView) v.findViewById(R.id.createdDate)).setText(dataList.get(position).getCreatedDate());
            ((TextView) v.findViewById(R.id.mcCompanyName)).setText(dataList.get(position).getMcCompanyName());
            ((TextView) v.findViewById(R.id.mcScac)).setText(dataList.get(position).getMcScac());
            ((TextView) v.findViewById(R.id.epCompanyName)).setText(dataList.get(position).getEpCompanyName());
            ((TextView) v.findViewById(R.id.epScac)).setText(dataList.get(position).getEpScac());
            ((TextView) v.findViewById(R.id.loadStatus)).setText(dataList.get(position).getLoadStatus());
            ((TextView) v.findViewById(R.id.containerNumber)).setText(dataList.get(position).getContNum());
            ((TextView) v.findViewById(R.id.containerType)).setText(dataList.get(position).getContType());
            ((TextView) v.findViewById(R.id.containerSize)).setText(dataList.get(position).getContSize());
            ((TextView) v.findViewById(R.id.chassisType)).setText(dataList.get(position).getChassisType());
            ((TextView) v.findViewById(R.id.chassisSize)).setText(dataList.get(position).getChassisSize());
            ((TextView) v.findViewById(R.id.chassisNumber)).setText(dataList.get(position).getChassisNum());
            ((TextView) v.findViewById(R.id.iepScac)).setText(dataList.get(position).getIepScac());

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    editor.putString(GlobalVariables.KEY_BASE_ORIGIN_FROM, GlobalVariables.ORIGIN_FROM_NOTIF_AVAIl);
                    InterchangeRequests ir = new InterchangeRequests();
                    ir.setNaId(dataList.get(position).getNaId());
                    ir.setEpCompanyName(dataList.get(position).getEpCompanyName());
                    ir.setEpScacs(dataList.get(position).getEpScac());
                    ir.setMcACompanyName(dataList.get(position).getMcCompanyName());
                    ir.setMcAScac(dataList.get(position).getMcScac());
                    ir.setContType(dataList.get(position).getContType());
                    ir.setContSize(dataList.get(position).getContSize());
                    ir.setContNum(dataList.get(position).getContNum());
                    ir.setChassisNum(dataList.get(position).getChassisNum());
                    ir.setChassisType(dataList.get(position).getChassisType());
                    ir.setChassisSize(dataList.get(position).getChassisSize());
                    ir.setIepScac(dataList.get(position).getIepScac());
                    ir.setGensetNum(dataList.get(position).getGensetNum());

                    ir.setOriginLocNm(dataList.get(position).getOriginLocNm());
                    ir.setOriginLocAddr(dataList.get(position).getOriginLocAddr());
                    ir.setOriginLocCity(dataList.get(position).getOriginLocCity());
                    ir.setOriginLocState(dataList.get(position).getOriginLocState());
                    ir.setOriginLocZip(dataList.get(position).getOriginLocZip());
                    ir.setOriginLocSplcCode(dataList.get(position).getOriginLocSplcCode());
                    ir.setOriginLocIanaCode(dataList.get(position).getOriginLocIanaCode());

                    ir.setEquipLocNm(dataList.get(position).getEquipLocNm());
                    ir.setEquipLocAddr(dataList.get(position).getEquipLocAddr());
                    ir.setEquipLocCity(dataList.get(position).getEquipLocCity());
                    ir.setEquipLocState(dataList.get(position).getEquipLocState());
                    ir.setEquipLocZip(dataList.get(position).getEquipLocZip());
                    ir.setEquipLocSplcCode(dataList.get(position).getEquipLocSplcCode());
                    ir.setEquipLocIanaCode(dataList.get(position).getEquipLocIanaCode());

                    SIAUtility.setObject(editor, GlobalVariables.KEY_INTERCHANGE_REQUESTS_OBJ, ir);
                    editor.commit();

                    Intent intent = new Intent(ListNotifAvailActivity.this, InitiateInterchangeActivity.class);
                    startActivity(intent);
                    finish(); /* This method will not display login page when click back (return) from phone */

                }
            });

            return v;
        }
    } /* End */


    /* code to perform delete operation of notification available (equipment pool) starts */

    class ExecuteDeleteNotifAvailTask extends AsyncTask<String, Integer, String> {
        String requestString;

        public ExecuteDeleteNotifAvailTask(String requestString) {
            this.requestString = requestString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            ApiResponse apiResponse = RestApiClient.callDeleteApi(getString(R.string.base_url) + getString(R.string.api_delete_notif_avail_request_by_na_id) + "?" + requestString);
            urlResponse = apiResponse.getMessage();
            urlResponseCode = apiResponse.getCode();
            return urlResponse;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                Log.v("log_tag", "ListNotifAvailActivity api_delete_notif_avail_request_by_na_id: urlResponseCode:=>" + urlResponseCode);
                Log.v("log_tag", "ListNotifAvailActivity api_delete_notif_avail_request_by_na_id: result:=> " + result);
                Gson gson = new Gson();

                ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);

                if (urlResponseCode == 200) {
                    new ViewDialog().showDialog(ListNotifAvailActivity.this, dialogTitle, errorMessage.getMessage());
                    dataList.clear();

                    String requestString = "accessToken="+siaSecurityObj.getAccessToken()+"&startDate="+naSearch.getFromDate()+"&endDate="+naSearch.getToDate()+
                            "&containerNo="+naSearch.getContainerNumber()+"&epSCAC="+naSearch.getEpScac()+"&mcSCAC="+naSearch.getMcScac()+
                            "&offset="+naSearch.getOffset() + "&limit=" + naSearch.getLimit();

                    progressBar.setVisibility(View.VISIBLE);
                    new ExecuteNotifAvailSearchTask(requestString).execute();

                } else {
                    try {
                        new ViewDialog().showDialog(ListNotifAvailActivity.this, dialogTitle, errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                    } catch(Exception e) {
                        new ViewDialog().showDialog(ListNotifAvailActivity.this, dialogTitle, getString(R.string.msg_error_try_after_some_time));
                    }
                }

            } catch (Exception e) {
                Log.v("log_tag", "Error ", e);
            }

            progressBar.setVisibility(View.GONE);
        }
    }


    void goToPreviousPage() {
        if (Internet_Check.checkInternetConnection(getApplicationContext())) {

            Intent intent = new Intent(ListNotifAvailActivity.this, SearchNotifAvailActivity.class);
            startActivity(intent);
            finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */
        } else {
            Intent intent = new Intent(ListNotifAvailActivity.this, NoInternetActivity.class);
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
