package com.iana.sia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;

public class ForgotPasswordTPUActivity extends AppCompatActivity implements Animation.AnimationListener {

    private ProgressBar progressBar;

    Button backToHomeBtn;

    Animation slideLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_tpu);

        slideLeft = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.set_in_left);
        slideLeft.setAnimationListener(this);

        progressBar = (ProgressBar) findViewById(R.id.processingBar);

        // below code is used to restrict auto populate keypad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        backToHomeBtn = (Button) findViewById(R.id.backToHomeBtn);
        backToHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ForgotPasswordTPUActivity.this, LoginTPUActivity.class));
                finish();

            }
        });

        findViewById(R.id.rootLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        backToHomeBtn.setVisibility(View.VISIBLE);
        backToHomeBtn.startAnimation(slideLeft);
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

}
