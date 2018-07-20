package com.iana.sia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iana.sia.model.InterchangeRequests;
import com.iana.sia.utility.GlobalVariables;
import com.iana.sia.utility.Internet_Check;
import com.iana.sia.utility.SIAUtility;

public class InterchangeRequestOperationAckActivity extends AppCompatActivity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    TextView successMessageTextView;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interchange_request_operation_ack);

        context = this;

        SIAUtility.showActionBar(context, getSupportActionBar());

        ((TextView) findViewById(R.id.title)).setText(R.string.title_success);

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        successMessageTextView = findViewById(R.id.successMessage);
        successMessageTextView.setText(sharedPref.getString(GlobalVariables.KEY_SUCCESS_MESSAGE, ""));

        BottomNavigationView bnv = findViewById(R.id.navigation_approve_another_request);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (Internet_Check.checkInternetConnection(context)) {
                    switch (item.getItemId()) {
                        case R.id.navigation_approve_another_request:
                            startActivity(new Intent(context, ListInterchangeRequestActivity.class));
                            finish();
                            break;

                        case R.id.navigation_home:
                            startActivity(new Intent(context, DashboardActivity.class));
                            finish();
                            break;
                    }

                } else {
                    startActivity(new Intent(context, NoInternetActivity.class));
                }
            return true;
            }
        });

        SIAUtility.disableShiftMode(bnv);
    }
}
