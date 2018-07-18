package com.iana.sia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iana.sia.model.IanaLocations;
import com.iana.sia.model.InterchangeRequests;
import com.iana.sia.model.NotificationAvail;
import com.iana.sia.utility.ApiResponse;
import com.iana.sia.utility.ApiResponseMessage;
import com.iana.sia.utility.GlobalVariables;
import com.iana.sia.utility.Internet_Check;
import com.iana.sia.utility.RestApiClient;
import com.iana.sia.utility.SIAUtility;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<IanaLocations> dataList= new ArrayList<>();
    LocationListAdapter adapter;

    String urlResponse;
    int urlResponseCode;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    EditText searchLocation;

    Button backBtn;

    ProgressBar progressBar;

    InterchangeRequests ir;
    NotificationAvail na;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        progressBar = findViewById(R.id.processingBar);

        // below code is used to restrict auto populate keypad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        showActionBar();
        ((TextView) findViewById(R.id.title)).setText(R.string.title_search_location);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setText(R.string.title_back);
        backBtn.setVisibility(View.VISIBLE);

        // code is to resize search icon in textview start
        Drawable drawable = getApplicationContext().getDrawable(R.drawable.search);
        drawable.setBounds(0, 0, 50, 50); // 50 => height & width of image search
        ScaleDrawable sd = new ScaleDrawable(drawable, 0, 0, 0);
        ((EditText) findViewById(R.id.searchLocation)).setCompoundDrawables(sd.getDrawable(), null, null, null);
        // code is to resize search icon in textview end

        listView = findViewById(R.id.listView);

        String tempString = "";
        String tempEpScac = "";

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        if(sharedPref != null){

            if(sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "").equalsIgnoreCase(GlobalVariables.ORIGIN_FROM_STREET_INTERCHANGE) ||
                sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "").equalsIgnoreCase(GlobalVariables.ORIGIN_FROM_STREET_TURN)) {

                ir = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_INTERCHANGE_REQUESTS_OBJ, InterchangeRequests.class);

            } else {
                na = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_NOTIF_AVAIL_OBJ, NotificationAvail.class);
            }



            if(sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "").equalsIgnoreCase(GlobalVariables.ORIGIN_FROM_STREET_INTERCHANGE)) {

                if(sharedPref.getString(GlobalVariables.KEY_SEARCH_FOR_LOCATION, "").equalsIgnoreCase(GlobalVariables.ORIGIN_FROM_ORIGINAL)) {
                    tempString = getString(R.string.api_get_original_location_list);
                    tempEpScac = ir.getEpScacs();

                } else {
                    tempString = getString(R.string.api_get_equipment_location_list);
                }

            }else if(sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "").equalsIgnoreCase(GlobalVariables.ORIGIN_FROM_NOTIF_AVAIl)) {

                if(sharedPref.getString(GlobalVariables.KEY_SEARCH_FOR_LOCATION, "").equalsIgnoreCase(GlobalVariables.ORIGIN_FROM_ORIGINAL)) {
                    tempString = getString(R.string.api_get_original_location_list);
                    tempEpScac = na.getEpScac();
                } else {
                    tempString = getString(R.string.api_get_equipment_location_list);
                }

            } else if(sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "").equalsIgnoreCase(GlobalVariables.ORIGIN_FROM_STREET_TURN)){
                tempString = getString(R.string.api_get_original_location_list);
                tempEpScac = ir.getEpScacs();
            }
        }

        final String tempRequestString = tempString;
        final String epScac = tempEpScac;

        searchLocation = findViewById(R.id.searchLocation);

        String requestString = tempRequestString + "?location=&epScac="+epScac;
        new ExecuteLocationSearchTask(requestString).execute();

        searchLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Internet_Check.checkInternetConnection(getApplicationContext())) {
                    if (null != s && s.toString().trim().length() > 1) {
                        if (SIAUtility.isAlphaNumSpaceHyphen(s.toString())) {
                                final String requestString = tempRequestString + "?location=" + s.toString().trim() + "&epScac=" + epScac;
                                new ExecuteLocationSearchTask(requestString).execute();

                        } else {
                            new ViewDialog().showDialog(LocationActivity.this, getString(R.string.dialog_title_location_search), getString(R.string.msg_error_invalid_location_search));
                        }
                    }

                } else {
                    Intent intent = new Intent(LocationActivity.this, NoInternetActivity.class);
                    startActivity(intent);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            if (Internet_Check.checkInternetConnection(getApplicationContext())) {

                editor.putString(GlobalVariables.KEY_RETURN_FROM, GlobalVariables.RETURN_FROM_LOCATION_SEARCH);
                editor.commit();

                goToPreviousPage();

            } else {
                Intent intent = new Intent(LocationActivity.this, NoInternetActivity.class);
                startActivity(intent);
            }

            }
        });

        ((ListView) findViewById(R.id.listView)).setOnScrollListener(new AbsListView.OnScrollListener(){
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if(mLastFirstVisibleItem<firstVisibleItem)
                {
                    Log.i("SCROLLING DOWN","TRUE");
                    findViewById(R.id.searchLocation).setVisibility(View.GONE);
                }
                if(mLastFirstVisibleItem>firstVisibleItem)
                {
                    Log.i("SCROLLING UP","TRUE");
                    findViewById(R.id.searchLocation).setVisibility(View.VISIBLE);
                }
                mLastFirstVisibleItem=firstVisibleItem;

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

    class ExecuteLocationSearchTask extends AsyncTask<String, Integer, String> {
        String requestString;

        public ExecuteLocationSearchTask(String requestString) {
            this.requestString = requestString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            Log.v("log_tag", "LocationActivity: doInBackground: requestString:=>" + requestString);
            ApiResponse apiResponse = RestApiClient.callGetApi(getString(R.string.base_url) + requestString);
            urlResponse = apiResponse.getMessage();
            urlResponseCode = apiResponse.getCode();
            return urlResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);

            try {
                Log.v("log_tag", "LocationActivity: urlResponseCode:=>" + urlResponseCode);
                Log.v("log_tag", "LocationActivity: result:=> " + result);
                Gson gson = new Gson();

                if (urlResponseCode == 200) {
                    Type listType = new TypeToken<List<IanaLocations>>() {}.getType();
                    dataList = gson.fromJson(result, listType);

                    adapter = new LocationListAdapter(getApplicationContext(), dataList, false);
                    listView.setAdapter(adapter);

                } else {
                    try {
                        ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                        new ViewDialog().showDialog(LocationActivity.this, getString(R.string.dialog_title_location_search), errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                    } catch(Exception e) {
                        new ViewDialog().showDialog(LocationActivity.this, getString(R.string.dialog_title_location_search), getString(R.string.msg_error_try_after_some_time));
                    }
                }

            } catch (Exception e) {
                Log.v("log_tag", "Error ", e);
            }
        }
    }

    class LocationListAdapter extends BaseAdapter {

        private Context mContext;
        private List<IanaLocations> mProductList;
        private boolean isShow;

        // Constructor
        public LocationListAdapter(Context mContext, List<IanaLocations> mProductList,boolean isShow ) {
            this.mContext = mContext;
            this.mProductList = mProductList;
            this.isShow = isShow;
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
            View v = View.inflate(mContext, R.layout.location_list_view, null);

            TextView locationName = v.findViewById(R.id.locationName);
            TextView locationZip = v.findViewById(R.id.locationZip);
            TextView locationState = v.findViewById(R.id.locationState);
            TextView locationCity = v.findViewById(R.id.locationCity);
            TextView locationIanaCode = v.findViewById(R.id.locationIanaCode);
            TextView locationSplcCode = v.findViewById(R.id.locationSplcCode);
            TextView locationAddress = v.findViewById(R.id.locationAddress);

            // set text for text view

            locationName.setText(dataList.get(position).getLocName());
            locationAddress.setText(dataList.get(position).getAddr());
            locationCity.setText(dataList.get(position).getCity());
            locationIanaCode.setText(dataList.get(position).getIanaCode());
            locationSplcCode.setText(dataList.get(position).getSplcCode());
            locationState.setText(dataList.get(position).getState());
            locationZip.setText(dataList.get(position).getZip());

            v.setOnClickListener(new View.OnClickListener() {
                        @Override
                public void onClick(View v) {

                    if(sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "").equalsIgnoreCase(GlobalVariables.ORIGIN_FROM_STREET_INTERCHANGE)) {

                        if(sharedPref.getString(GlobalVariables.KEY_SEARCH_FOR_LOCATION, "").equalsIgnoreCase(GlobalVariables.ORIGIN_FROM_ORIGINAL)) {
                            setSelectedLocation(GlobalVariables.ORIGIN_FROM_STREET_INTERCHANGE, position, GlobalVariables.ORIGIN_FROM_ORIGINAL);

                        } else {
                            setSelectedLocation(GlobalVariables.ORIGIN_FROM_STREET_INTERCHANGE, position, "");
                        }

                        SIAUtility.setObject(editor, GlobalVariables.KEY_INTERCHANGE_REQUESTS_OBJ, ir);

                    }else if(sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "").equalsIgnoreCase(GlobalVariables.ORIGIN_FROM_NOTIF_AVAIl)) {

                        if(sharedPref.getString(GlobalVariables.KEY_SEARCH_FOR_LOCATION, "").equalsIgnoreCase(GlobalVariables.ORIGIN_FROM_ORIGINAL)) {
                            setSelectedLocation(GlobalVariables.ORIGIN_FROM_NOTIF_AVAIl, position, GlobalVariables.ORIGIN_FROM_ORIGINAL);
                        } else {
                            setSelectedLocation(GlobalVariables.ORIGIN_FROM_NOTIF_AVAIl, position, "");
                        }

                        SIAUtility.setObject(editor, GlobalVariables.KEY_NOTIF_AVAIL_OBJ, na);

                    } else if(sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "").equalsIgnoreCase(GlobalVariables.ORIGIN_FROM_STREET_TURN)){
                        setSelectedLocation(GlobalVariables.ORIGIN_FROM_STREET_TURN, position, GlobalVariables.ORIGIN_FROM_ORIGINAL);
                        SIAUtility.setObject(editor, GlobalVariables.KEY_INTERCHANGE_REQUESTS_OBJ, ir);
                    }

                    editor.commit();

                    goToPreviousPage();

                        }
            });

            return v;
        }
    } /* End */

    void setSelectedLocation(String originFrom, int position, String equipOrOrigin){

        if(GlobalVariables.ORIGIN_FROM_NOTIF_AVAIl.equalsIgnoreCase(originFrom)) {
            if (null != equipOrOrigin && equipOrOrigin.equalsIgnoreCase(GlobalVariables.ORIGIN_FROM_ORIGINAL)) {
                na.setOriginLocNm(dataList.get(position).getLocName());
                na.setOriginLocAddr(dataList.get(position).getAddr());
                na.setOriginLocZip(dataList.get(position).getZip());
                na.setOriginLocCity(dataList.get(position).getCity());
                na.setOriginLocState(dataList.get(position).getState());
                na.setOriginLocIanaCode(dataList.get(position).getIanaCode());
                na.setOriginLocSplcCode(dataList.get(position).getSplcCode());

            } else {
                na.setEquipLocNm(dataList.get(position).getLocName());
                na.setEquipLocAddr(dataList.get(position).getAddr());
                na.setEquipLocZip(dataList.get(position).getZip());
                na.setEquipLocCity(dataList.get(position).getCity());
                na.setEquipLocState(dataList.get(position).getState());
                na.setEquipLocIanaCode(dataList.get(position).getIanaCode());
                na.setEquipLocSplcCode(dataList.get(position).getSplcCode());
            }

        } else {
            if (null != equipOrOrigin && equipOrOrigin.equalsIgnoreCase(GlobalVariables.ORIGIN_FROM_ORIGINAL)) {
                ir.setOriginLocNm(dataList.get(position).getLocName());
                ir.setOriginLocAddr(dataList.get(position).getAddr());
                ir.setOriginLocZip(dataList.get(position).getZip());
                ir.setOriginLocCity(dataList.get(position).getCity());
                ir.setOriginLocState(dataList.get(position).getState());
                ir.setOriginLocIanaCode(dataList.get(position).getIanaCode());
                ir.setOriginLocSplcCode(dataList.get(position).getSplcCode());

            } else {
                ir.setEquipLocNm(dataList.get(position).getLocName());
                ir.setEquipLocAddr(dataList.get(position).getAddr());
                ir.setEquipLocZip(dataList.get(position).getZip());
                ir.setEquipLocCity(dataList.get(position).getCity());
                ir.setEquipLocState(dataList.get(position).getState());
                ir.setEquipLocIanaCode(dataList.get(position).getIanaCode());
                ir.setEquipLocSplcCode(dataList.get(position).getSplcCode());
            }
        }
    }


    void goToPreviousPage() {
        if (Internet_Check.checkInternetConnection(getApplicationContext())) {

            if(sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "").equalsIgnoreCase(GlobalVariables.ORIGIN_FROM_STREET_INTERCHANGE)) {
                Intent intent = new Intent(LocationActivity.this, InitiateInterchangeActivity.class);
                startActivity(intent);

            }else if(sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "").equalsIgnoreCase(GlobalVariables.ORIGIN_FROM_NOTIF_AVAIl)) {
                Intent intent = new Intent(LocationActivity.this, NotifAvailActivity.class);
                startActivity(intent);

            } else if(sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "").equalsIgnoreCase(GlobalVariables.ORIGIN_FROM_STREET_TURN)){
                Intent intent = new Intent(LocationActivity.this, StreetTurnActivity.class);
                startActivity(intent);
            }

            finish(); /* This method will not display login page when click back (return) from phone */
                    /* End */
        } else {
            Intent intent = new Intent(LocationActivity.this, NoInternetActivity.class);
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
