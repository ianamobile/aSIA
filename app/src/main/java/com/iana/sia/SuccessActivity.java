package com.iana.sia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iana.sia.model.InterchangeRequests;
import com.iana.sia.utility.GlobalVariables;
import com.iana.sia.utility.Internet_Check;
import com.iana.sia.utility.SIAUtility;

import org.w3c.dom.Text;

public class SuccessActivity extends AppCompatActivity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    TextView successMessageTextView;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        context = this;

        showActionBar();
        ((TextView) findViewById(R.id.title)).setText(R.string.title_success);

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        String requestOriginFrom = sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "");
        String isStreetInterchangeInitiatedByMCA = sharedPref.getString("isStreetInterchangeInitiatedByMCA", "");
        String successMessage = sharedPref.getString(GlobalVariables.SUCCESS, "");

        if (GlobalVariables.ORIGIN_FROM_STREET_INTERCHANGE.equalsIgnoreCase(requestOriginFrom) ||
                GlobalVariables.ORIGIN_FROM_STREET_TURN.equalsIgnoreCase(requestOriginFrom)) {

            if ("TRUE".equalsIgnoreCase(isStreetInterchangeInitiatedByMCA)) {

                if(GlobalVariables.ORIGIN_FROM_STREET_INTERCHANGE.equalsIgnoreCase(requestOriginFrom)) {
                    ((TextView) findViewById(R.id.note)).setText(getString(R.string.lbl_disclaimerText));

                } else {
                    ((TextView) findViewById(R.id.note)).setText(getString(R.string.lbl_disclaimerText_st));
                }

                findViewById(R.id.noteLbl).setVisibility(View.VISIBLE);
                findViewById(R.id.note).setVisibility(View.VISIBLE);

            } else {
                ((TextView) findViewById(R.id.note)).setText("");
                findViewById(R.id.note).setVisibility(View.GONE);
                findViewById(R.id.noteLbl).setVisibility(View.GONE);
            }

        } else {
            ((TextView) findViewById(R.id.note)).setText(getString(R.string.lbl_disclaimerText));
            findViewById(R.id.noteLbl).setVisibility(View.VISIBLE);
            findViewById(R.id.note).setVisibility(View.VISIBLE);
        }

        successMessageTextView = findViewById(R.id.successMessage);
        successMessageTextView.setText(successMessage);


        BottomNavigationView bnv = findViewById(R.id.navigation_add_request_home);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (Internet_Check.checkInternetConnection(context)) {
                    switch (item.getItemId()) {
                        case R.id.navigation_add_request:

                            String requestOriginFrom = sharedPref.getString(GlobalVariables.KEY_ORIGIN_FROM, "");

                            if(GlobalVariables.ORIGIN_FROM_STREET_TURN.equalsIgnoreCase(requestOriginFrom)) {
                                startActivity(new Intent(SuccessActivity.this, StreetTurnActivity.class));

                            } else if(GlobalVariables.ORIGIN_FROM_STREET_INTERCHANGE.equalsIgnoreCase(requestOriginFrom)) {
                                startActivity(new Intent(SuccessActivity.this, InitiateInterchangeActivity.class));

                            } else if(GlobalVariables.ORIGIN_FROM_NOTIF_AVAIl.equalsIgnoreCase(requestOriginFrom)) {
                                startActivity(new Intent(SuccessActivity.this, NotifAvailActivity.class));
                            }

                            SIAUtility.removeAllKey(editor);

                            finish(); /* This method will not display login page when click back (return) */

                            break;

                        case R.id.navigation_home:
                            SIAUtility.removeAllKey(editor);
                            startActivity(new Intent(SuccessActivity.this, DashboardActivity.class));
                            finish(); /* This method will not display login page when click back (return) */

                            break;

                    }

                } else{

                    Intent intent = new Intent(SuccessActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */
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

}
