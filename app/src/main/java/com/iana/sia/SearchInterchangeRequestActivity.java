package com.iana.sia;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iana.sia.model.InterchangeRequests;
import com.iana.sia.model.InterchangeRequestsSearch;
import com.iana.sia.model.SIASecurityObj;
import com.iana.sia.model.User;
import com.iana.sia.utility.GlobalVariables;
import com.iana.sia.utility.Internet_Check;
import com.iana.sia.utility.SIAUtility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class SearchInterchangeRequestActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText    containerNumber;
    EditText    exportBookingNumber;
    EditText    fromDate;
    EditText    toDate;
    Spinner     status;
    EditText    scac;

    StatusAdapter statusAdapter;

    ProgressBar progressBar;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    String clickOn = "";

    DatePickerFragment fragment;

    String[] statusArray;
    SIASecurityObj siaSecurityObj;

    Context context;

    String dialogTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_interchange_request);

        context = this;

        dialogTitle = getString(R.string.title_search_interchange_request);

        showActionBar();
        ((TextView) findViewById(R.id.title)).setText(R.string.title_search_interchange_request);

        progressBar = findViewById(R.id.processingBar);

        // below code is used to restrict auto populate keypad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        InterchangeRequestsSearch irSearch = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_INTERCHANGE_REQUESTS_SEARCH_OBJ, InterchangeRequestsSearch.class);
        siaSecurityObj = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_SECURITY_OBJ, SIASecurityObj.class);

        containerNumber = findViewById(R.id.containerNumber);
        exportBookingNumber = findViewById(R.id.exportBookingNumber);
        fromDate = findViewById(R.id.fromDate);
        toDate = findViewById(R.id.toDate);

        status = findViewById(R.id.status);
        statusArray = getResources().getStringArray(R.array.status);
        statusAdapter = new StatusAdapter(this, statusArray);
        status.setAdapter(statusAdapter);

        scac = findViewById(R.id.scac);
        if(siaSecurityObj.getRoleName().equalsIgnoreCase(GlobalVariables.ROLE_MC) ||
                siaSecurityObj.getRoleName().equalsIgnoreCase(GlobalVariables.ROLE_IDD) ||
                (siaSecurityObj.getRoleName().equalsIgnoreCase(GlobalVariables.ROLE_SEC) && null != siaSecurityObj.getMemType() &&
                        siaSecurityObj.getMemType().equalsIgnoreCase(GlobalVariables.ROLE_MC))) {

            ((TextView)findViewById(R.id.scacLbl)).setText(getString(R.string.lbl_container_provider_scac));
        }

        if(null != irSearch) {
            if(irSearch.getContNum() != null) {
               containerNumber.setText(irSearch.getContNum());
            }
            if(irSearch.getBookingNum() != null) {
                exportBookingNumber.setText(irSearch.getBookingNum());
            }
            if(irSearch.getFromDate() != null) {
                fromDate.setText(irSearch.getFromDate());
            }
            if(irSearch.getToDate() != null) {
                toDate.setText(irSearch.getToDate());
            }
            if(irSearch.getStatus() != null) {
                int position = Arrays.asList(statusArray).indexOf(irSearch.getStatus());
                status.setSelection(position);
            }
            if(irSearch.getScac() != null) {
                scac.setText(irSearch.getScac());
            }
        }

        BottomNavigationView bnv = findViewById(R.id.navigation_search_cancel);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (Internet_Check.checkInternetConnection(getApplicationContext())) {
                    switch (item.getItemId()) {
                        case R.id.navigation_search:
                                // code to disable background functionality when progress bar starts
                                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            InterchangeRequestsSearch irSearch = new InterchangeRequestsSearch();

                            irSearch.setContNum(containerNumber.getText().toString());
                            irSearch.setBookingNum(exportBookingNumber.getText().toString());
                            irSearch.setFromDate(fromDate.getText().toString());
                            irSearch.setToDate(toDate.getText().toString());


                            LinearLayout ll = (LinearLayout) status.getSelectedView(); // get the parent layout view
                            TextView selectedText = ll.findViewById(R.id.statusTextView); // get the child text view
                            if(!statusArray[0].equalsIgnoreCase(selectedText.getText().toString())) {
                                irSearch.setStatus(selectedText.getText().toString());
                            } else {
                                irSearch.setStatus("");
                            }

                            irSearch.setScac(scac.getText().toString());

                            SIAUtility.setObject(editor, GlobalVariables.KEY_INTERCHANGE_REQUESTS_SEARCH_OBJ, irSearch);
                            editor.commit();

                            startActivity(new Intent(SearchInterchangeRequestActivity.this, ListInterchangeRequestActivity.class));
                            finish(); /* This method will not display login page when click back (return) from phone */
                            break;

                        case R.id.navigation_cancel:
                            goToPreviousPage();
                            break;

                        case R.id.navigation_reset:

                            containerNumber.setText("");
                            exportBookingNumber.setText("");
                            fromDate.setText("");
                            toDate.setText("");
                            status.setSelection(0); // get the parent layout view
                            scac.setText("");

                            editor.remove(GlobalVariables.KEY_INTERCHANGE_REQUESTS_SEARCH_OBJ);
                            editor.commit();

                            break;

                    }

                } else {
                    Intent intent = new Intent(SearchInterchangeRequestActivity.this, NoInternetActivity.class);
                    startActivity(intent);
                }


                return true;
            }
        });

        SIAUtility.disableShiftMode(bnv);


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

    public class StatusAdapter extends BaseAdapter {
        Context context;
        String[] status;
        LayoutInflater inflter;

        public StatusAdapter(Context applicationContext, String[] status) {
            this.context = applicationContext;
            this.status = status;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return status.length;
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
            view = inflter.inflate(R.layout.custom_spinner_status, null);
            TextView statusTextView = view.findViewById(R.id.statusTextView);
            statusTextView.setText(status[i]);
            return view;
        }
    }

    public void fromDate(View view) {
        clickOn = "fromDate";
        fragment = new DatePickerFragment();
        fragment.show(getFragmentManager(), "fromDatePicker");

    }

    public void toDate(View view) {
        clickOn = "toDate";
        fragment = new DatePickerFragment();
        fragment.show(getFragmentManager(), "toDatePicker");

    }

    private void setDate(final Calendar calendar) {
//        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        final DateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_format));
        if(clickOn == "fromDate") {
            ((EditText) findViewById(R.id.fromDate)).setText(dateFormat.format(calendar.getTime()));
        } else if(clickOn == "toDate") {
            ((EditText) findViewById(R.id.toDate)).setText(dateFormat.format(calendar.getTime()));
        }
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = new GregorianCalendar(year, month, day);
        setDate(c);
    }

    public static class DatePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Panel,
                    (DatePickerDialog.OnDateSetListener)
                            getActivity(), year, month, day);
        }
    }

    void goToPreviousPage() {

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

                startActivity(new Intent(SearchInterchangeRequestActivity.this, DashboardActivity.class));
                finish(); /* This method will not display login page when click back (return) from phone */

            }
        });

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
