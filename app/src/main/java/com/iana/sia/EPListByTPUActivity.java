package com.iana.sia;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iana.sia.model.EPUsersByTPUResult;
import com.iana.sia.model.IanaLocations;
import com.iana.sia.model.Profile;
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

public class EPListByTPUActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Profile> dataList= new ArrayList<>();
    EPUserListAdapter adapter;

    String urlResponse;
    int urlResponseCode;

    SharedPreferences sharedPref;
    ProgressBar progressBar;

    SIASecurityObj siaSecurityObj;

    Map<Integer, Integer> lastRecordMap = new HashMap<Integer,Integer>();

    Button backBtn;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eplist_by_tpu);

        context = this;

        progressBar = findViewById(R.id.processingBar);

        // below code is used to restrict auto populate keypad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        showActionBar();
        ((TextView) findViewById(R.id.title)).setText(R.string.title_list_ep_user);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setText(R.string.title_back);
        backBtn.setVisibility(View.VISIBLE);

        listView = findViewById(R.id.listView);
        adapter = new EPUserListAdapter(context, dataList);
        listView.setAdapter(adapter);

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        if(sharedPref != null) {

            siaSecurityObj = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_SECURITY_OBJ, SIASecurityObj.class);

            // code to disable background functionality when progress bar starts
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            String requestString = "accessToken="+siaSecurityObj.getAccessToken()+"&offset=" + getString(R.string.default_offset) + "&limit=" + getString(R.string.limit);
            new ExecuteTask(requestString).execute();


            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    int lastInScreen = firstVisibleItem + visibleItemCount;

                    int limit = Integer.valueOf(getString(R.string.limit));
                    if (dataList.size() >= 10 && lastInScreen == dataList.size() && (dataList.size() % limit) == 0) {
                        String requestString = "accessToken=" + siaSecurityObj.getAccessToken() + "&offset=" + ((totalItemCount / 10) + 1) + "&limit=" + limit;

                        if (lastRecordMap.size() == 0 || !lastRecordMap.containsKey(lastInScreen)) {

                            // code to disable background functionality when progress bar starts
                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            Log.v("log_tag", "EPListByTPUActivity on scroll: requestString:=> " + requestString);
                            lastRecordMap.put(lastInScreen, lastInScreen);
                            new ExecuteTask(requestString).execute();
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
    }

    void goToPreviousPage() {
        if (Internet_Check.checkInternetConnection(context)) {
            Intent intent = new Intent(EPListByTPUActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */
        } else {
            Intent intent = new Intent(EPListByTPUActivity.this, NoInternetActivity.class);
            startActivity(intent);
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
            Log.v("log_tag", "In EPListByTPUActivity: ExecuteTask: doInBackground requestString:=> " + requestString);
            ApiResponse apiResponse = RestApiClient.callGetApi(getString(R.string.base_url) + getString(R.string.api_get_ep_user_list_by_tpu)+"?"+requestString);
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
                Log.v("log_tag", "EPListByTPUActivity: urlResponseCode:=>" + urlResponseCode);
//                Log.v("log_tag", "EPListByTPUActivity: result:=> " + result);
                Gson gson = new Gson();

                if (urlResponseCode == 200) {
                    Type listType = new TypeToken<EPUsersByTPUResult>() {}.getType();
                    EPUsersByTPUResult epUsersByTPUResult = gson.fromJson(result, listType);
                    List<Profile> epUserList = epUsersByTPUResult.getEpUserList();
//                    Log.v("log_tag", "EPListByTPUActivity response: epUserList:=> " + epUserList);
                    for (Profile profile: epUserList) {
                        dataList.add(profile);
                    }

                    Log.v("log_tag", "ListBadOrderActivity: result: dataList.size() => " + dataList.size());

                    if(dataList.size() <= 10) {
                        adapter = new EPUserListAdapter(context, dataList);
                        listView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }

                } else {

                    try {

                        ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                        new ViewDialog().showDialog(EPListByTPUActivity.this, getString(R.string.dialog_title_ep_user_list), errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                    } catch(Exception e) {
                        new ViewDialog().showDialog(EPListByTPUActivity.this, getString(R.string.dialog_title_ep_user_list), getString(R.string.msg_error_try_after_some_time));
                    }
                }

            } catch (Exception e) {
                Log.v("log_tag", "Error ", e);
            }

        }

    }

    class EPUserListAdapter extends BaseAdapter {

        private Context mContext;
        private List<Profile> mProductList;

        // Constructor
        public EPUserListAdapter(Context mContext, List<Profile> mProductList) {
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
            Log.v("log_tag", "in getView position.."+position);
            View v = View.inflate(mContext, R.layout.ep_list_by_tpu_list_view, null);

            TextView epCompanyName = v.findViewById(R.id.epCompanyName);
            TextView epScac = v.findViewById(R.id.epScac);
            TextView status = v.findViewById(R.id.status);
            ImageView statusImageView = v.findViewById(R.id.statusImageView);
            LinearLayout leftPatternColor = v.findViewById(R.id.leftPatternColor);

            // set text for text view

            epCompanyName.setText(dataList.get(position).getCompanyName().toUpperCase());
            epScac.setText(dataList.get(position).getScac().toUpperCase());
            status.setText(dataList.get(position).getStatus().toUpperCase());

            if(GlobalVariables.STATUS_PENDING.equalsIgnoreCase(dataList.get(position).getStatus())) {
                statusImageView.setImageDrawable(context.getDrawable(R.drawable.pending_hourglass));
                leftPatternColor.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_color_pending));

            } else if(GlobalVariables.STATUS_DISABLED.equalsIgnoreCase(dataList.get(position).getStatus())) {
                statusImageView.setImageDrawable(context.getDrawable(R.drawable.pending_hourglass));
                leftPatternColor.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_color_disabled));
            }

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RelativeLayout mainL = ((RelativeLayout) v);
                    RelativeLayout r = (RelativeLayout) mainL.getChildAt(0);

                    if(GlobalVariables.STATUS_ACTIVE.equalsIgnoreCase(dataList.get(position).getStatus())) {

                        // code to disable background functionality when progress bar starts
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        String requestString = "accessToken="+siaSecurityObj.getAccessToken()+"&epScac="+dataList.get(position).getScac();
                        new ExecuteTaskToGetEPAccessToken(requestString).execute();

                    }
                }
            });


            return v;
        }
    } /* End */

    class ExecuteTaskToGetEPAccessToken extends AsyncTask<String, Integer, String> {
        String requestString;

        public ExecuteTaskToGetEPAccessToken(String requestString) {
            this.requestString = requestString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            Log.v("log_tag", "In EPListByTPUActivity: ExecuteTaskToGetEPAccessToken: doInBackground requestString:=> " + requestString);
            ApiResponse apiResponse = RestApiClient.callGetApi(getString(R.string.base_url) + getString(R.string.api_get_tpu_token_by_ep)+"?"+requestString);
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
                Log.v("log_tag", "EPListByTPUActivity: urlResponseCode:=>" + urlResponseCode);
                Log.v("log_tag", "EPListByTPUActivity: result:=> " + result);
                Gson gson = new Gson();

                if (urlResponseCode == 200) {

                    SIASecurityObj siaSecurityObj = gson.fromJson(result, SIASecurityObj.class);
                    Log.v("log_tag", "EPListByTPU getTPUAccessTokenByEP Response siaSecurityObj: " + siaSecurityObj);
                    /* Code to store login information start */

                    SharedPreferences.Editor editor = sharedPref.edit();
                    SIAUtility.setObject(editor, GlobalVariables.KEY_SECURITY_OBJ, siaSecurityObj);
                    editor.commit();

                    Intent intent = new Intent(EPListByTPUActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */

                } else {

                    try {

                        ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                        new ViewDialog().showDialog(EPListByTPUActivity.this, getString(R.string.dialog_title_ep_user_list), errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                    } catch(Exception e) {
                        new ViewDialog().showDialog(EPListByTPUActivity.this, getString(R.string.dialog_title_ep_user_list), getString(R.string.msg_error_try_after_some_time));
                    }
                }

            } catch (Exception e) {
                Log.v("log_tag", "Error ", e);
            }

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
