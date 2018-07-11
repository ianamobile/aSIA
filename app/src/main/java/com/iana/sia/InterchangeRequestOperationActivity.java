package com.iana.sia;

import android.animation.Animator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iana.sia.model.FieldInfo;
import com.iana.sia.model.InterchangeRequestsJson;
import com.iana.sia.model.WorkFlow;
import com.iana.sia.utility.GlobalVariables;
import com.iana.sia.utility.SIAUtility;

import java.util.ArrayList;
import java.util.List;

public class InterchangeRequestOperationActivity extends AppCompatActivity {

    ProgressBar progressBar;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    String urlResponse;
    int urlResponseCode;

    RelativeLayout layoutMain;
    RelativeLayout layoutContent;
    LinearLayout layoutButtons;

    Button button;

    boolean isOpen = false;

    private FloatingActionMenu fam;
    private FloatingActionButton fabEdit, fabDelete, fabAdd;

    private boolean show = true;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interchange_request_operation);

        context = getApplicationContext();

        showActionBar();
        ((TextView) findViewById(R.id.title)).setText(R.string.title_view_details);

        progressBar = findViewById(R.id.processingBar);

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        // below code is used to restrict auto populate keypad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        processViewContent();


        layoutMain = findViewById(R.id.layoutMain);
        layoutContent = findViewById(R.id.layoutContent);
        layoutButtons = findViewById(R.id.layoutButtons);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMenu("");
            }
        });


        // code to view buttons which needs to be displayed on button click start
        fabAdd = findViewById(R.id.fab2);
        fabDelete = findViewById(R.id.fab3);
        fabEdit = findViewById(R.id.fab1);

        fam = findViewById(R.id.fab_menu);

        //handling menu status (open or close)
        fam.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                RelativeLayout relativeLayout = findViewById(R.id.layoutMain);
                if (opened) {
                    relativeLayout.setAlpha(0);
                    showToast("Menu is opened");
                    viewMenu("fromFam");
                    fabEdit.setVisibility(View.VISIBLE);
                    fabDelete.setVisibility(View.VISIBLE);

                } else {
                    relativeLayout.setAlpha(1);
                    showToast("Menu is closed");
                    fabEdit.setVisibility(View.GONE);
                    fabDelete.setVisibility(View.GONE);
                }
            }
        });

        //handling each floating action button clicked
        fabDelete.setOnClickListener(onButtonClick());
        fabEdit.setOnClickListener(onButtonClick());
        fabAdd.setOnClickListener(onButtonClick());

        fam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fam.isOpened()) {
                    fam.close(true);
                }
            }
        });

    }

    private View.OnClickListener onButtonClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == fabAdd) {
                    showToast("Button Add clicked");
                } else if (view == fabDelete) {
                    showToast("Button Delete clicked");
                } else {
                    showToast("Button Edit clicked");
                }
                fam.close(true);
            }
        };
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    private void processViewContent() {
        TableLayout tl = findViewById(R.id.tableLayout);

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

//        List<FieldInfo> fieldInfoList =  SIAUtility.getObjectOfModel(sharedPref, "fieldInfoList", List.class);
        Gson gson = new Gson();
        List<FieldInfo> fieldInfoList =  (gson.fromJson(sharedPref.getString("fieldInfoList", "[]"),
                new TypeToken<List<FieldInfo>>() {
                }.getType()));

        for(int i=0; i < fieldInfoList.size();i++) {
            FieldInfo fieldInfo = fieldInfoList.get(i);
            TableRow row;
            if(fieldInfo.getTitle().equalsIgnoreCase(GlobalVariables.FIELD_INFO_EMPTY)) {
                row = (TableRow)LayoutInflater.from(InterchangeRequestOperationActivity.this).inflate(R.layout.verify_heading, null);
                ((TextView)row.findViewById(R.id.title)).setText(fieldInfo.getValue());
                tl.addView(row);

            } else if(fieldInfo.getTitle().equalsIgnoreCase(GlobalVariables.FIELD_INFO_TITLE)) {
                row = (TableRow)LayoutInflater.from(InterchangeRequestOperationActivity.this).inflate(R.layout.verify_content, null);
                ((TextView)row.findViewById(R.id.locationLbl)).setText(fieldInfo.getValue());

                fieldInfo = fieldInfoList.get(++i);
                ((TextView) row.findViewById(R.id.locationValue)).setText(fieldInfo.getValue());
                tl.addView(row);

            } else  if(fieldInfo.getTitle().equalsIgnoreCase(GlobalVariables.FIELD_INFO_BLANK)) {
                row = new TableRow(this);
                LinearLayout ll = new LinearLayout(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 40, 1f);
                lp.setMargins(0, 10, 0, 0);
                ll.setLayoutParams(lp);
                ll.setBackground(getDrawable(R.drawable.cell_shape));
                row.addView(ll);
                tl.addView(row);

            }
        }

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


    // code to view / close workflow
    private void viewMenu(String fromFam) {
        int x = button.getRight();
        int y = button.getBottom();
        if(!isOpen) {

            int startRadius = 0;
            int endRadius = (int) Math.hypot(layoutMain.getWidth(), layoutMain.getHeight());

            Animator anim = ViewAnimationUtils.createCircularReveal(layoutButtons, x, y, startRadius, endRadius);
            layoutButtons.setVisibility(View.VISIBLE);
            anim.start();
            isOpen = true;
            button.setText("CLOSE FLOW");



            /*if(null == fromFam || "" == fromFam) {
                if (fam.isOpened()) {
                    fam.close(true);
                }
            }*/

            layoutButtons.removeAllViews();

            InterchangeRequestsJson interchangeRequestsJson = SIAUtility.getObjectOfModel(sharedPref, "interchangeRequestsJson", InterchangeRequestsJson.class);
            List<WorkFlow> workFlowList = interchangeRequestsJson.getWorkFlowList();
            for(int i = 0; i< workFlowList.size();i++) {

                WorkFlow workFlow = workFlowList.get(i);

                LinearLayout workFlowLL = new LinearLayout(this);
                workFlowLL.setGravity(Gravity.CENTER);
                workFlowLL.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams relativeLayoutInCardViewLayoutParams =
                        new LinearLayout.LayoutParams(850, ViewGroup.LayoutParams.WRAP_CONTENT);

                relativeLayoutInCardViewLayoutParams.setMargins(20, 20, 20, 5);
                workFlowLL.setLayoutParams(relativeLayoutInCardViewLayoutParams);
                workFlowLL.setGravity(Gravity.CENTER);
                workFlowLL.setElevation(16f);
                if(getString(R.string.STATUS_APPROVED).equalsIgnoreCase(workFlow.getStatus())) {
                    workFlowLL.setBackgroundColor(ContextCompat.getColor(this, R.color.wf_bg_color_approved));

                } else {
                    workFlowLL.setBackgroundColor(ContextCompat.getColor(this, R.color.wf_bg_color_awaiting));
                }

                // ImageView in Linear Layout starts
                ImageView imageView = new ImageView(this);

                if(getString(R.string.STATUS_APPROVED).equalsIgnoreCase(workFlow.getStatus())) {
                    imageView.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_background));
                    Drawable mDrawable = ContextCompat.getDrawable(context, R.drawable.approve);
                    mDrawable.setColorFilter(new
                            PorterDuffColorFilter(Color.parseColor("#006400"), PorterDuff.Mode.SRC_IN));

                    imageView.setImageDrawable(mDrawable);

                } else {
                    Drawable mDrawable = ContextCompat.getDrawable(context, R.drawable.if_hourglass_start_1608934_3);
                    mDrawable.setColorFilter(new
                            PorterDuffColorFilter(Color.parseColor("#006400"), PorterDuff.Mode.SRC_IN));
                    imageView.setImageDrawable(mDrawable);
                }


                imageView.setPadding(20, 20, 20, 20);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                LinearLayout.LayoutParams imageViewLayputParams = new LinearLayout.LayoutParams(96, 96);
                imageViewLayputParams.setLayoutDirection(Gravity.CENTER);
                imageViewLayputParams.setMargins(0, 30, 0, 0);
                imageView.setLayoutParams(imageViewLayputParams);
                // ImageView in Linear Layout end

                workFlowLL.addView(imageView);

                // text view starts
                TextView textView = new TextView(this);
                LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textViewLayoutParams.setMargins(20, 20, 0, 0);
                textView.setLayoutParams(textViewLayoutParams);
                textView.setText(workFlow.getAction());
                textView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                textView.setTextSize(6 * getResources().getDisplayMetrics().density);
                // text view end

                TextView dateTextView = new TextView(this);
                textViewLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textViewLayoutParams.setMargins(20, 0, 0, 20);
                dateTextView.setLayoutParams(textViewLayoutParams);
                dateTextView.setText(workFlow.getApprovedDate());
                dateTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                dateTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                dateTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                dateTextView.setGravity(Gravity.CENTER_VERTICAL);
                dateTextView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                dateTextView.setTextSize(6 * getResources().getDisplayMetrics().density);

                workFlowLL.addView(textView);
                workFlowLL.addView(dateTextView);

                layoutButtons.addView(workFlowLL);
            }


        } else {

            int startRadius = (int) Math.hypot(layoutContent.getWidth(), layoutContent.getHeight());
            int endRadius = 0;

            Animator anim = ViewAnimationUtils.createCircularReveal(layoutButtons, x, y, startRadius, endRadius);
            layoutButtons.setVisibility(View.VISIBLE);
            anim.addListener(new Animator.AnimatorListener(){

                @Override
                public void onAnimationStart(Animator animator){

                }

                @Override
                public void onAnimationEnd(Animator animator){
                    layoutButtons.setVisibility(View.GONE);

                }

                @Override
                public void onAnimationCancel(Animator animator){

                }

                @Override
                public void onAnimationRepeat(Animator animator){

                }

            });
            anim.start();
            isOpen = false;

            button.setText("VIEW FLOW");
        }
    }



}
