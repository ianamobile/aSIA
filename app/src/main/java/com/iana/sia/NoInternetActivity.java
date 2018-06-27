package com.iana.sia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.iana.sia.utility.Internet_Check;

public class NoInternetActivity extends AppCompatActivity {

    Button intRetryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        intRetryBtn = findViewById(R.id.intRetryBtn);
        intRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Internet_Check.checkInternetConnection(getApplicationContext())) {

                    Intent intent = new Intent(NoInternetActivity.this, LoginMCActivity.class);
                    startActivity(intent);

                }
            }
        });
    }
}
