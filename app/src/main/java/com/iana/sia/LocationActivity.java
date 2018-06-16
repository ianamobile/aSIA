package com.iana.sia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import com.iana.sia.utility.ApiResponse;
import com.iana.sia.utility.GlobalVariables;
import com.iana.sia.utility.RestApiClient;

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
    EditText searchLocation;

    Button backBtn;

    ProgressBar progressBar;

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

        listView = findViewById(R.id.listView);

        String tempString = "";
        String tempEpScac = "";

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        if(sharedPref != null){
            if(sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "").equalsIgnoreCase(GlobalVariables.ORIGIN_FROM_EQUIPMENT)) {
                tempString = getString(R.string.api_get_equipment_location_list);
            } else if(sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "").equalsIgnoreCase(GlobalVariables.ORIGIN_FROM_ORIGINAL)){
                tempString = getString(R.string.api_get_original_location_list);
                tempEpScac = sharedPref.getString(GlobalVariables.KEY_EP_SCAC, "");
            }
        }

        final String epScac = tempEpScac;
        final String tempRequestString = tempString;

        searchLocation = findViewById(R.id.searchLocation);

        String requestString = tempRequestString + "?location=&epScac="+epScac;
        new ExecuteLocationSearchTask(requestString).execute();

        searchLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null != s && s.toString().trim().length() > 1) {
                    final String requestString = tempRequestString + "?location=" + s.toString().trim() + "&epScac="+epScac;
                    new ExecuteLocationSearchTask(requestString).execute();
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

                SharedPreferences sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(GlobalVariables.KEY_RETURN_FROM, GlobalVariables.RETURN_FROM_LOCATION_SEARCH);

                editor.putString(GlobalVariables.KEY_LOCATION_NAME, "");
                editor.putString(GlobalVariables.KEY_LOCATION_ADDRESS, "");
                editor.putString(GlobalVariables.KEY_LOCATION_ZIP, "");
                editor.putString(GlobalVariables.KEY_LOCATION_CITY, "");
                editor.putString(GlobalVariables.KEY_LOCATION_STATE, "");
                editor.putString(GlobalVariables.KEY_LOCATION_IANA_CODE, "");
                editor.putString(GlobalVariables.KEY_LOCATION_SPLC_CODE, "");

                editor.commit();

                Intent intent = new Intent(LocationActivity.this, StreetTurnActivity.class);
                startActivity(intent);
                finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */
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
            Log.v("log_tag", "In ListBadOrderActivity: doInBackground get request:=> " + getString(R.string.base_url) + requestString);
            ApiResponse apiResponse = RestApiClient.callGetApi(getString(R.string.base_url) + requestString);
            Log.v("log_tag", "in ListBadOrderActivity: doInBackground apiResponse:=> " + apiResponse);
            urlResponse = apiResponse.getMessage();
            urlResponseCode = apiResponse.getCode();
            return urlResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);

            try {
                Log.v("log_tag", "ListBadOrderActivity: urlResponseCode:=>" + urlResponseCode);
                Log.v("log_tag", "ListBadOrderActivity: result:=> " + result);
                Gson gson = new Gson();

                if (urlResponseCode == 200) {
                    Type listType = new TypeToken<List<IanaLocations>>() {}.getType();
                    dataList = gson.fromJson(result, listType);

                    adapter = new LocationListAdapter(getApplicationContext(), dataList, false);
                    listView.setAdapter(adapter);

                } else {
                    new ViewDialog().showDialog(LocationActivity.this, "LOCATION SEARCH", getString(R.string.msg_error_no_records_found));
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
                    SharedPreferences sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(GlobalVariables.KEY_LOCATION_NAME, dataList.get(position).getLocName());
                    editor.putString(GlobalVariables.KEY_LOCATION_ADDRESS, dataList.get(position).getAddr());
                    editor.putString(GlobalVariables.KEY_LOCATION_ZIP, dataList.get(position).getZip());
                    editor.putString(GlobalVariables.KEY_LOCATION_CITY, dataList.get(position).getCity());
                    editor.putString(GlobalVariables.KEY_LOCATION_STATE, dataList.get(position).getState());
                    editor.putString(GlobalVariables.KEY_LOCATION_IANA_CODE, dataList.get(position).getIanaCode());
                    editor.putString(GlobalVariables.KEY_LOCATION_SPLC_CODE, dataList.get(position).getSplcCode());
                    editor.putString(GlobalVariables.KEY_RETURN_FROM, GlobalVariables.RETURN_FROM_LOCATION_SEARCH);

//                    editor.putString(GlobalVariables.KEY_MC_SCAC, sharedPref.getString(GlobalVariables.KEY_MC_SCAC, ""));
//                    editor.putString(GlobalVariables.KEY_MC_COMPANY_NAME, sharedPref.getString(GlobalVariables.KEY_MC_COMPANY_NAME, ""));
//
//                    editor.putString(GlobalVariables.KEY_CONTAINER_NUMBER, sharedPref.getString(, ));
//                    editor.putString(GlobalVariables.KEY_EXPORT_BOOKING_NUMBER, sharedPref.getString(, ));
//                    editor.putString(GlobalVariables.KEY_IMPORT_BL, sharedPref.getString(, ));
//                    editor.putString(GlobalVariables.KEY_CHASSIS_ID, sharedPref.getString(, ));
//                    editor.putString(GlobalVariables.KEY_IEP_SCAC, sharedPref.getString(, ));


                    editor.commit();

                    Intent intent = new Intent(LocationActivity.this, StreetTurnActivity.class);
                    startActivity(intent);
                    finish(); /* This method will not display login page when click back (return) from phone */
                    /* End */

                        }
            });

            return v;
        }
    } /* End */

}
