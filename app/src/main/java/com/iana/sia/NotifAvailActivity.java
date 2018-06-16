package com.iana.sia;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class NotifAvailActivity extends AppCompatActivity {

    private Button backBtn;

    private ProgressBar progressBar;

    EditText epScac;
    EditText mcScac;
    EditText iepScac;

    Spinner loadStatus;
    String[] loadStatusArray;

    Spinner containerType;
    Spinner containerSize;

    Spinner chassisType;
    Spinner chassisSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_avail);

        progressBar = (ProgressBar) findViewById(R.id.processingBar);

        // below code is used to restrict auto populate keypad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        showActionBar();
        ((TextView) findViewById(R.id.title)).setText(R.string.title_notif_avail);
        backBtn = ((Button) findViewById(R.id.backBtn));
        backBtn.setText(R.string.title_back);
        backBtn.setVisibility(View.VISIBLE);

        epScac =  (EditText) findViewById(R.id.epScac);
        epScac.setFocusable(false);
        epScac.setClickable(false);
        epScac.setLongClickable(false);

        mcScac =  (EditText) findViewById(R.id.mcScac);
        mcScac.setFocusable(false);
        mcScac.setClickable(false);
        mcScac.setLongClickable(false);

        iepScac =  (EditText) findViewById(R.id.iepScac);
        iepScac.setFocusable(false);
        iepScac.setClickable(false);
        iepScac.setLongClickable(false);


        loadStatus = (Spinner) findViewById(R.id.loadStatus);
        loadStatusArray = getResources().getStringArray(R.array.load_status);
        CustomAdapter adapter=new CustomAdapter(this, loadStatusArray);
        loadStatus.setAdapter(adapter);

        containerType = (Spinner) findViewById(R.id.containerType);
        String[] containerTypeArray = new String[]{"SELECT CONTAINER TYPE", "Dry", "High Cube", "Refrigerated", "Flatrack", "Open Top", "Tank", "Other"};
        CustomAdapter containerTypeAdapter = new CustomAdapter(this, containerTypeArray);
        containerType.setAdapter(containerTypeAdapter);

        containerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout ll = (LinearLayout) containerType.getSelectedView(); // get the parent layout view
                TextView tv = (TextView) ll.findViewById(R.id.textView); // get the child text view
                String selectedText = tv.getText().toString();

                LinearLayout containerTypeOtherLayout = (LinearLayout) findViewById(R.id.containerTypeOtherLayout);
                if(selectedText.equalsIgnoreCase("OTHER")) {
                    containerTypeOtherLayout.setVisibility(View.VISIBLE);

                } else {
                    containerTypeOtherLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        containerSize = (Spinner) findViewById(R.id.containerSize);
        String[] containerSizeArray = new String[]{"SELECT CONTAINER SIZE", "20", "40", "45", "48", "53", "Other"};
        CustomAdapter containerSizeAdapter = new CustomAdapter(this, containerSizeArray);
        containerSize.setAdapter(containerSizeAdapter);

        containerSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout ll = (LinearLayout) containerSize.getSelectedView(); // get the parent layout view
                TextView tv = (TextView) ll.findViewById(R.id.textView); // get the child text view
                String selectedText = tv.getText().toString();

                LinearLayout containerSizeOtherLayout = (LinearLayout) findViewById(R.id.containerSizeOtherLayout);
                if(selectedText.equalsIgnoreCase("OTHER")) {
                    containerSizeOtherLayout.setVisibility(View.VISIBLE);

                } else {
                    containerSizeOtherLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        chassisType = (Spinner) findViewById(R.id.chassisType);
        String[] chassisTypeArray = new String[]{"SELECT CHASSIS TYPE", "Chassis", "Slider", "Tri-axle", "Quad-axle", "Other"};
        CustomAdapter chassisTypeAdapter = new CustomAdapter(this, chassisTypeArray);
        chassisType.setAdapter(chassisTypeAdapter);

        chassisType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout ll = (LinearLayout) chassisType.getSelectedView(); // get the parent layout view
                TextView tv = (TextView) ll.findViewById(R.id.textView); // get the child text view
                String selectedText = tv.getText().toString();

                LinearLayout chassisTypeOtherLayout = (LinearLayout) findViewById(R.id.chassisTypeOtherLayout);
                if(selectedText.equalsIgnoreCase("OTHER")) {
                    chassisTypeOtherLayout.setVisibility(View.VISIBLE);

                } else {
                    chassisTypeOtherLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        chassisSize = (Spinner) findViewById(R.id.chassisSize);
        String[] chassisSizeArray = new String[]{"SELECT CHASSIS SIZE", "20", "40", "45", "48", "53", "Other"};
        CustomAdapter chassisSizeAdapter = new CustomAdapter(this, chassisSizeArray);
        chassisSize.setAdapter(chassisSizeAdapter);

        chassisSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout ll = (LinearLayout) chassisSize.getSelectedView(); // get the parent layout view
                TextView tv = (TextView) ll.findViewById(R.id.textView); // get the child text view
                String selectedText = tv.getText().toString();

                LinearLayout chassisSizeOtherLayout = (LinearLayout) findViewById(R.id.chassisSizeOtherLayout);
                if(selectedText.equalsIgnoreCase("OTHER")) {
                    chassisSizeOtherLayout.setVisibility(View.VISIBLE);

                } else {
                    chassisSizeOtherLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
//        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.action_bar_background_color)));
        actionBar.setCustomView(v);
    }

    public class CustomAdapter extends BaseAdapter {
        Context context;
        String[] countryNames;
        LayoutInflater inflter;

        public CustomAdapter(Context applicationContext, String[] countryNames) {
            this.context = applicationContext;
            this.countryNames = countryNames;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return countryNames.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.custom_spinner_type_user, null);
            TextView names = (TextView) view.findViewById(R.id.textView);
            names.setText(countryNames[i]);
            return view;
        }
    }
}
