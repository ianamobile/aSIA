package com.iana.sia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iana.sia.utility.GlobalVariables;

public class VerifyStreetTurnActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_street_turn);

        showActionBar();
        ((TextView) findViewById(R.id.title)).setText(R.string.title_verify_details);

        progressBar = findViewById(R.id.processingBar);

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        ((TextView) findViewById(R.id.epCompanyName)).setText(sharedPref.getString(GlobalVariables.KEY_EP_COMPANY_NAME, ""));

        // below code is used to restrict auto populate keypad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


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
