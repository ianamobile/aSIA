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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.iana.sia.model.InterchangeRequestsSearch;
import com.iana.sia.model.NotifAvailSearch;
import com.iana.sia.model.SIASecurityObj;
import com.iana.sia.utility.GlobalVariables;
import com.iana.sia.utility.Internet_Check;
import com.iana.sia.utility.SIAUtility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class SearchNotifAvailActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText    containerNumber;
    EditText    mcScac;
    EditText    epScac;
    EditText    fromDate;
    EditText    toDate;

    ProgressBar progressBar;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    String clickOn = "";

    DatePickerFragment fragment;

    SIASecurityObj siaSecurityObj;

    String dialogTitle;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_notif_avail);

        context = this;

        dialogTitle = getString(R.string.dialog_title_request_pool_search);

        showActionBar();
        ((TextView) findViewById(R.id.title)).setText(R.string.title_request_pool_search);
        ((TextView) findViewById(R.id.title)).setTextColor(ContextCompat.getColor(this, R.color.color_white));


        progressBar = findViewById(R.id.processingBar);

        // below code is used to restrict auto populate keypad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        NotifAvailSearch naSearch = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_NOTIF_AVAIL_SEARCH_OBJ, NotifAvailSearch.class);
        siaSecurityObj = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_SECURITY_OBJ, SIASecurityObj.class);

        containerNumber = findViewById(R.id.containerNumber);
        mcScac = findViewById(R.id.mcScac);
        epScac = findViewById(R.id.epScac);
        fromDate = findViewById(R.id.fromDate);
        toDate = findViewById(R.id.toDate);

        if(null != naSearch) {
            containerNumber.setText(null != naSearch.getContainerNumber() ? naSearch.getContainerNumber() : "");
            mcScac.setText(null != naSearch.getMcScac() ? naSearch.getMcScac() : "");
            epScac.setText(null != naSearch.getEpScac() ? naSearch.getEpScac() : "");
            fromDate.setText(null != naSearch.getFromDate() ? naSearch.getFromDate() : "");
            toDate.setText(null != naSearch.getToDate() ? naSearch.getToDate() : "");
        }

        BottomNavigationView bnv = findViewById(R.id.navigation_search_cancel);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (Internet_Check.checkInternetConnection(context)) {
                    switch (item.getItemId()) {
                        case R.id.navigation_search:
                            String returnMessage = validateFields();
                            if (!returnMessage.equalsIgnoreCase(GlobalVariables.SUCCESS)) {
                                new ViewDialog().showDialog(SearchNotifAvailActivity.this, dialogTitle, returnMessage);

                            } else {

                                String error = "";
                                if(null != containerNumber.getText() && containerNumber.getText().toString().trim().length() > 0 && !SIAUtility.isAlphaNumeric(containerNumber.getText().toString())) {
                                    error = getString(R.string.msg_error_invalid_container_number);
                                }

                                if(error.length() == 0) {
                                    NotifAvailSearch naSearch = new NotifAvailSearch();

                                    naSearch.setContainerNumber(containerNumber.getText().toString().trim());
                                    naSearch.setMcScac(mcScac.getText().toString().trim());
                                    naSearch.setEpScac(epScac.getText().toString().trim());
                                    naSearch.setFromDate(fromDate.getText().toString());
                                    naSearch.setToDate(toDate.getText().toString());
                                    naSearch.setOffset(Integer.valueOf(getString(R.string.default_offset)));
                                    naSearch.setLimit(Integer.valueOf(getString(R.string.limit)));

                                    SIAUtility.setObject(editor, GlobalVariables.KEY_NOTIF_AVAIL_SEARCH_OBJ, naSearch);
                                    editor.commit();

                                    startActivity(new Intent(SearchNotifAvailActivity.this, ListNotifAvailActivity.class));
                                    finish(); /* This method will not display login page when click back (return) from phone */

                                } else {
                                    new ViewDialog().showDialog(SearchNotifAvailActivity.this, dialogTitle, error);
                                }
                            }

                            break;

                        case R.id.navigation_cancel:
                            goToPreviousPage();
                            break;

                        case R.id.navigation_reset:

                            containerNumber.setText("");
                            mcScac.setText("");
                            epScac.setText("");
                            fromDate.setText("");
                            toDate.setText("");

                            editor.remove(GlobalVariables.KEY_NOTIF_AVAIL_SEARCH_OBJ);
                            editor.commit();

                            break;

                    }

                } else {
                    Intent intent = new Intent(SearchNotifAvailActivity.this, NoInternetActivity.class);
                    startActivity(intent);
                }


                return true;
            }
        });

        SIAUtility.disableShiftMode(bnv);

    }

    String validateFields() {
        String containerNumber = ((EditText)findViewById(R.id.containerNumber)).getText().toString();
        String epScac = ((EditText)findViewById(R.id.epScac)).getText().toString();
        String mcScac = ((EditText)findViewById(R.id.mcScac)).getText().toString();


        if(null != mcScac && mcScac.trim().toString().length() > 0) {
            if(mcScac.length() != 4) {
                return getString(R.string.msg_error_length_motor_carrier_scac);

            } else if(!SIAUtility.isAlpha(mcScac)) {
                return getString(R.string.msg_error_char_motor_carrier_scac);
            }
        }

        if(null != epScac && epScac.trim().toString().length() > 0) {
            if(!(epScac.length() >= 2 && epScac.length() <= 4)) {
                return getString(R.string.lbl_container_provider)+" "+getString(R.string.msg_error_length_ep_scac);

            } else if(!SIAUtility.isAlpha(epScac)) {
                return getString(R.string.lbl_container_provider)+" "+getString(R.string.msg_error_char_scac);
            }
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

                startActivity(new Intent(SearchNotifAvailActivity.this, DashboardActivity.class));
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
