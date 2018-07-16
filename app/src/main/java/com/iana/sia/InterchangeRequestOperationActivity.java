package com.iana.sia;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.iana.sia.adapter.ListViewAdapter;
import com.iana.sia.model.Company;
import com.iana.sia.model.FieldInfo;
import com.iana.sia.model.InterchangeRequests;
import com.iana.sia.model.InterchangeRequestsJson;
import com.iana.sia.model.SIASecurityObj;
import com.iana.sia.model.UIIAExhibit;
import com.iana.sia.model.WorkFlow;
import com.iana.sia.utility.ApiResponse;
import com.iana.sia.utility.ApiResponseMessage;
import com.iana.sia.utility.GlobalVariables;
import com.iana.sia.utility.Internet_Check;
import com.iana.sia.utility.RestApiClient;
import com.iana.sia.utility.SIAUtility;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterchangeRequestOperationActivity extends AppCompatActivity {

    ProgressBar progressBar;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    String urlResponse;
    int urlResponseCode;

    RelativeLayout layoutMain;
    RelativeLayout layoutContent;
    LinearLayout layoutButtons;

    Button workFlowBtn;

    boolean isOpen = false;

    private FloatingActionMenu fam;
    private FloatingActionButton cancelBtn, approveBtn, rejectBtn, onholdBtn, reinitiateBtn;

    private boolean show = true;

    Context context;

    InterchangeRequestsJson irJson;

    Button backBtn;

    String dialogTitle;

    SIASecurityObj siaSecurityObj;

    ArrayList<UIIAExhibit> uiiaExhibitList = new ArrayList<>();

    ListView dialog_ListView;
    UIIAExhibitListViewAdapter uiiaExhibitListViewAdapter;

    List<Integer> selectedUIIAExhibitList = new ArrayList<>();

    Drawable mDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interchange_request_operation);

        context = getApplicationContext();

        dialogTitle = getString(R.string.dialog_title_interchange_request_operation);

        showActionBar();
        ((TextView) findViewById(R.id.title)).setText(R.string.title_view_details);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setText(R.string.title_back);
        backBtn.setVisibility(View.VISIBLE);

        progressBar = findViewById(R.id.processingBar);

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        siaSecurityObj = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_SECURITY_OBJ, SIASecurityObj.class);

        // below code is used to restrict auto populate keypad
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        processViewContent();

        layoutMain = findViewById(R.id.layoutMain);
        layoutContent = findViewById(R.id.layoutContent);
        layoutButtons = findViewById(R.id.layoutButtons);

        workFlowBtn = findViewById(R.id.button);
        workFlowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMenu("");
            }
        });


        irJson = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_OPERATION_IR_OBJ, InterchangeRequestsJson.class);

//        Log.v("log_tag", "Operation: irJson:=>"+irJson);

        // code to view buttons which needs to be displayed on button click start
        if(irJson.isLoggedInUserEligibleForApproval() && GlobalVariables.STATUS_PENDING.equalsIgnoreCase(irJson.getInterchangeRequests().getStatus())) {

            if(null != irJson.getShowButtons() && GlobalVariables.Y.equalsIgnoreCase(irJson.getShowButtons())) {
                cancelBtn = findViewById(R.id.cancelBtn);
                cancelBtn.setOnClickListener(onButtonClick());
            }

            approveBtn = findViewById(R.id.approveBtn);
            approveBtn.setOnClickListener(onButtonClick());

            if(GlobalVariables.INITIATOR_MCA.equalsIgnoreCase(irJson.getInProcessWf().getWfSeqType())) {
                onholdBtn = findViewById(R.id.onholdBtn);
                onholdBtn.setOnClickListener(onButtonClick());
            }

            rejectBtn = findViewById(R.id.rejectBtn);
            rejectBtn.setOnClickListener(onButtonClick());

            reinitiateBtn = findViewById(R.id.reinitiateBtn);
            reinitiateBtn.setOnClickListener(onButtonClick());


        } else {

            if(GlobalVariables.STATUS_PENDING.equalsIgnoreCase(irJson.getInterchangeRequests().getStatus())) {
                if (null != irJson.getShowCancelButtons() && GlobalVariables.Y.equalsIgnoreCase(irJson.getShowCancelButtons())) {
                    cancelBtn = findViewById(R.id.cancelBtn);
                    cancelBtn.setOnClickListener(onButtonClick());
                }
            }

            reinitiateBtn = findViewById(R.id.reinitiateBtn);
            reinitiateBtn.setOnClickListener(onButtonClick());
        }

        fam = findViewById(R.id.fab_menu);
        fam.setIconAnimated(false);
//        mDrawable = ContextCompat.getDrawable(context, R.drawable.menu);
//        mDrawable.setColorFilter(new
//                PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN));
//        fam.getMenuIconView().setImageDrawable(mDrawable);


        //handling menu status (open or close)
        fam.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                RelativeLayout relativeLayout = findViewById(R.id.layoutMain);
                if (opened) {
                    mDrawable = ContextCompat.getDrawable(context, R.drawable.cross);
                    mDrawable.setColorFilter(new
                            PorterDuffColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN));
                    fam.getMenuIconView().setImageDrawable(mDrawable);
                    relativeLayout.setAlpha(0);
                    setButtonVisibility(View.VISIBLE);

                } else {
                    mDrawable = ContextCompat.getDrawable(context, R.drawable.menu);
                    mDrawable.setColorFilter(new
                            PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN));
                    fam.getMenuIconView().setImageDrawable(mDrawable);
                    relativeLayout.setAlpha(1);
                    setButtonVisibility(View.GONE);
                }
            }
        });

        fam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fam.isOpened()) {
                    fam.close(true);
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToPreviousPage();
            }
        });

    }

    private void setButtonVisibility(int visibleOrGone) {
        if(irJson.isLoggedInUserEligibleForApproval() && GlobalVariables.STATUS_PENDING.equalsIgnoreCase(irJson.getInterchangeRequests().getStatus())) {

            if(null != irJson.getShowCancelButtons() && GlobalVariables.Y.equalsIgnoreCase(irJson.getShowCancelButtons())) {
                cancelBtn.setVisibility(visibleOrGone);
                mDrawable = ContextCompat.getDrawable(context, R.drawable.cross);
                mDrawable.setColorFilter(new
                        PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN));
                cancelBtn.setImageDrawable(mDrawable);
//                cancelBtn.setLabelTextColor(R.color.color_black);
            }

            approveBtn.setVisibility(visibleOrGone);
                mDrawable = ContextCompat.getDrawable(context, R.drawable.approve);
                mDrawable.setColorFilter(new
                        PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN));
            approveBtn.setImageDrawable(mDrawable);

//            approveBtn.setPadding(40, 10, 10, 10);
//            approveBtn.setLabelTextColor(R.color.color_black);


            if(null != irJson.getInProcessWf().getWfSeqType() && GlobalVariables.INITIATOR_MCA.equalsIgnoreCase(irJson.getInProcessWf().getWfSeqType())) {
                onholdBtn.setVisibility(visibleOrGone);
                    mDrawable = ContextCompat.getDrawable(context, R.drawable.onhold);
                    mDrawable.setColorFilter(new
                            PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN));
                onholdBtn.setImageDrawable(mDrawable);
//                onholdBtn.setLabelTextColor(R.color.color_black);
            }

            rejectBtn.setVisibility(visibleOrGone);
                mDrawable = ContextCompat.getDrawable(context, R.drawable.reject);
                mDrawable.setColorFilter(new
                        PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN));
            rejectBtn.setImageDrawable(mDrawable);
//            rejectBtn.setLabelTextColor(R.color.color_black);

            reinitiateBtn.setVisibility(visibleOrGone);
                mDrawable = ContextCompat.getDrawable(context, R.drawable.reinitiate);
                mDrawable.setColorFilter(new
                        PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN));
            reinitiateBtn.setImageDrawable(mDrawable);
//            reinitiateBtn.setLabelTextColor(R.color.color_black);

        } else {

                if(GlobalVariables.STATUS_PENDING.equalsIgnoreCase(irJson.getInterchangeRequests().getStatus())) {
                    if (null != irJson.getShowCancelButtons() && GlobalVariables.Y.equalsIgnoreCase(irJson.getShowCancelButtons())) {
                        cancelBtn.setVisibility(visibleOrGone);
                            mDrawable = ContextCompat.getDrawable(context, R.drawable.cross);
                            mDrawable.setColorFilter(new
                                    PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN));
                        cancelBtn.setImageDrawable(mDrawable);
//                        cancelBtn.setLabelTextColor(R.color.color_black);
                    }
                }

            reinitiateBtn.setVisibility(visibleOrGone);
                mDrawable = ContextCompat.getDrawable(context, R.drawable.reinitiate);
                mDrawable.setColorFilter(new
                        PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN));
            reinitiateBtn.setImageDrawable(mDrawable);
//            reinitiateBtn.setLabelTextColor(R.color.color_black);
        }
    }

    private View.OnClickListener onButtonClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == cancelBtn) {
                    performOperation("cancel");

                } else if (view == rejectBtn) {
                    performOperation("reject");

                } else if(view == approveBtn) {
                    performOperation("approve");

                } else if(view == onholdBtn) {
                    performOperation("onhold");

                } else if(view == reinitiateBtn) {

                    SIAUtility.setObject(editor, GlobalVariables.KEY_INTERCHANGE_REQUESTS_OBJ, irJson.getInterchangeRequests());
                    editor.commit();

                    if(GlobalVariables.IR_REQUEST_TYPE_SI.equalsIgnoreCase(irJson.getInterchangeRequests().getIrRequestType())) {
                        startActivity(new Intent(InterchangeRequestOperationActivity.this, InitiateInterchangeActivity.class));
                        finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */

                    } else {
                        startActivity(new Intent(InterchangeRequestOperationActivity.this, StreetTurnActivity.class));
                        finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */
                    }

                } else {
                    fam.close(true);
                }

            }
        };
    }

    private void processViewContent() {
        TableLayout tl = findViewById(R.id.tableLayout);

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

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
        int x = workFlowBtn.getRight();
        int y = workFlowBtn.getBottom();
        if(!isOpen) {

            int startRadius = 0;
            int endRadius = (int) Math.hypot(layoutMain.getWidth(), layoutMain.getHeight());

//            Log.v("log_tag", "Show Workflow: x:=>"+x);
//            Log.v("log_tag", "Show Workflow: y:=>"+y);

//            Log.v("log_tag", "Show Workflow: startRadius:=>"+startRadius);
//            Log.v("log_tag", "Show Workflow: endRadius:=>"+endRadius);

            Animator anim = ViewAnimationUtils.createCircularReveal(layoutButtons, x, y, startRadius, endRadius);
            layoutButtons.setVisibility(View.VISIBLE);
            anim.start();
            isOpen = true;

            layoutButtons.removeAllViews();

            InterchangeRequestsJson interchangeRequestsJson = SIAUtility.getObjectOfModel(sharedPref, "interchangeRequestsJson", InterchangeRequestsJson.class);
            List<WorkFlow> workFlowList = interchangeRequestsJson.getWorkFlowList();
            WorkFlow inProcessWf = interchangeRequestsJson.getInProcessWf();

            Map<String, Integer> backgroundColorMap = new HashMap<>();
                backgroundColorMap.put("wf approved", ContextCompat.getColor(this, R.color.wf_bg_color_approved));
                backgroundColorMap.put("wf stop", ContextCompat.getColor(this, R.color.wf_bg_color_stop));
                backgroundColorMap.put("wf inprocess", ContextCompat.getColor(this, R.color.wf_bg_color_inprocess));
                backgroundColorMap.put("wf pending", ContextCompat.getColor(this, R.color.wf_bg_color_pending));

            Map<String, Drawable> iconMap = new HashMap<>();
                iconMap.put("fa fa-stop-circle fa-2x", ContextCompat.getDrawable(context, R.drawable.onhold_32));
                iconMap.put("fa fa-hourglass-start fa-2x", ContextCompat.getDrawable(context, R.drawable.pending_hourglass));
                iconMap.put("fa fa-clock-o fa-2x", ContextCompat.getDrawable(context, R.drawable.onhold_32));
                iconMap.put("fa fa-check-circle fa-2x", ContextCompat.getDrawable(context, R.drawable.approve));


            LinearLayout allWorkFlowLL = new LinearLayout(this);
            allWorkFlowLL.setGravity(Gravity.CENTER);
            allWorkFlowLL.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams allWorkFlowLLLayoutParams =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            allWorkFlowLL.setLayoutParams(allWorkFlowLLLayoutParams);
            allWorkFlowLL.setGravity(Gravity.CENTER);

            int textSizeAction = 8;
            int textSizeDate = 8;

            for(int i = 0; i< workFlowList.size();i++) {

                WorkFlow wf = workFlowList.get(i);

                // initial setup start
                String inprocessFoundIndex = "";
                String onHoldOrRejectRequestIndex = "";
                String cssClassValue = "";
                String iconClassValue = "";

//                Log.v("log_tag", "Operation: cssClassValue:=>"+cssClassValue);
//                Log.v("log_tag", "Operation: iconClassValue:=>"+iconClassValue);

                if(GlobalVariables.STATUS_ONHOLD.equalsIgnoreCase(wf.getStatus()) || GlobalVariables.STATUS_REJECTED.equalsIgnoreCase(wf.getStatus()) ||
                        GlobalVariables.STATUS_CANCELLED.equalsIgnoreCase(wf.getStatus())) {

                        onHoldOrRejectRequestIndex = String.valueOf(i);
                        cssClassValue = "wf stop";
                        iconClassValue ="fa fa-stop-circle fa-2x";

                } else if(GlobalVariables.STATUS_PENDING.equalsIgnoreCase(wf.getStatus()) && wf.getWfId().intValue() == inProcessWf.getWfId().intValue()) {
                        if(onHoldOrRejectRequestIndex == "") {

                            inprocessFoundIndex = String.valueOf(i);
                            cssClassValue = "wf inprocess";
                            iconClassValue = "fa fa-hourglass-start fa-2x";

                        } else {
                            cssClassValue = "wf pending";
                            iconClassValue = "fa fa-clock-o fa-2x";
                        }

                } else if(GlobalVariables.STATUS_PENDING.equalsIgnoreCase(wf.getStatus())) {
                        cssClassValue = "wf pending";
                        iconClassValue = "fa fa-clock-o fa-2x";

                } else if(GlobalVariables.STATUS_APPROVED.equalsIgnoreCase(wf.getStatus())) {

                        if(inprocessFoundIndex.equalsIgnoreCase("") && onHoldOrRejectRequestIndex.equalsIgnoreCase("")) {
                            cssClassValue = "wf approved";
                            iconClassValue = "fa fa-check-circle fa-2x";

                        } else {
                            cssClassValue = "wf pending";
                            iconClassValue = "fa fa-clock-o fa-2x";
                        }
                }

                // end


                LinearLayout workFlowLL = new LinearLayout(this);
                workFlowLL.setGravity(Gravity.CENTER);
                workFlowLL.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams workFlowLLLayoutParams =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);

                workFlowLLLayoutParams.setMargins(20, 20, 20, 10);
                workFlowLL.setLayoutParams(workFlowLLLayoutParams);
                workFlowLL.setGravity(Gravity.CENTER);
                workFlowLL.setElevation(5f);

                // ImageView in Linear Layout starts
                ImageView imageView = new ImageView(this);
                    LinearLayout.LayoutParams imageViewLayputParams = new LinearLayout.LayoutParams(64, 64);
                    imageViewLayputParams.setLayoutDirection(Gravity.CENTER);
                    imageViewLayputParams.setMargins(0, 0, 0, 0);


                    // text view to display action starts
                    TextView actionTextView = new TextView(this);
                        LinearLayout.LayoutParams actionTextViewLayoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    actionTextViewLayoutParams.setMargins(20, 5, 0, 0);
                    actionTextView.setLayoutParams(actionTextViewLayoutParams);


                    LinearLayout.LayoutParams dateTextViewLayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        dateTextViewLayoutParams.setMargins(20, 0, 0, 20);

                /* index flow 0 start from 0*/
                if(i == 0) {

//                    workFlowLL.setBackgroundColor(backgroundColorMap.get(cssClassValue));
                    workFlowLL.setBackgroundColor(backgroundColorMap.get("wf approved"));
//                    Drawable mDrawable = iconMap.get(iconClassValue);
                    Drawable mDrawable = iconMap.get("fa fa-check-circle fa-2x");
                    mDrawable.setColorFilter(new
                            PorterDuffColorFilter(Color.parseColor("#006400"), PorterDuff.Mode.SRC_IN));

                    imageView.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_workflow_white_background));
                    imageView.setImageDrawable(mDrawable);
                    imageView.setPadding(10, 10, 10, 10);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setLayoutParams(imageViewLayputParams);

                    workFlowLL.addView(imageView);
                    // ImageView in Linear Layout end

                    // text view starts
                        actionTextView.setLayoutParams(actionTextViewLayoutParams);
                        actionTextView.setText(wf.getAction());
                        actionTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                        actionTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        actionTextView.setGravity(Gravity.CENTER_VERTICAL);
                        actionTextView.setTypeface(actionTextView.getTypeface(), Typeface.BOLD);
                        actionTextView.setTextSize(textSizeAction * getResources().getDisplayMetrics().density);

                    workFlowLL.addView(actionTextView);
                    // text view end

                    if(cssClassValue =="wf approved" || cssClassValue  == "wf stop") {

                        TextView dateTextView = new TextView(this);
                        dateTextView.setLayoutParams(dateTextViewLayoutParams);
                        dateTextView.setText("Date: " + wf.getApprovedDate());
                        dateTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                        dateTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        dateTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                        dateTextView.setGravity(Gravity.CENTER_VERTICAL);
                        dateTextView.setTypeface(dateTextView.getTypeface(), Typeface.BOLD);
                        dateTextView.setTextSize(textSizeDate * getResources().getDisplayMetrics().density);

                        workFlowLL.addView(dateTextView);
                    }

                }


                /* index flow 1 start from 0*/
                if(i == 1) {

                    workFlowLL.setBackgroundColor(backgroundColorMap.get(cssClassValue));

                    Drawable mDrawable = iconMap.get(iconClassValue);
                    if(iconClassValue.equalsIgnoreCase("fa fa-hourglass-start fa-2x")) {
                        mDrawable.setColorFilter(new
                                PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN));
                    } else {

                        mDrawable.setColorFilter(new
                                PorterDuffColorFilter(Color.parseColor("#006400"), PorterDuff.Mode.SRC_IN));
                        imageView.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_workflow_white_background));
                        imageView.setPadding(10, 10, 10, 10);
                    }

                    imageView.setImageDrawable(mDrawable);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setLayoutParams(imageViewLayputParams);

                    workFlowLL.addView(imageView);
                    // ImageView in Linear Layout end

                    // text view starts
                    actionTextView.setLayoutParams(actionTextViewLayoutParams);
                    actionTextView.setText(wf.getAction());
                    actionTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                    actionTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    actionTextView.setTypeface(actionTextView.getTypeface(), Typeface.BOLD);
                    actionTextView.setTextSize(textSizeAction * getResources().getDisplayMetrics().density);

                    workFlowLL.addView(actionTextView);
                    // text view end

                    if(cssClassValue =="wf approved" || cssClassValue  == "wf stop") {

                        TextView dateTextView = new TextView(this);
                        dateTextView.setLayoutParams(dateTextViewLayoutParams);
                        dateTextView.setText("Date: " + wf.getApprovedDate());
                        dateTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                        dateTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        dateTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                        dateTextView.setGravity(Gravity.CENTER_VERTICAL);
                        dateTextView.setTypeface(dateTextView.getTypeface(), Typeface.BOLD);
                        dateTextView.setTextSize(textSizeDate * getResources().getDisplayMetrics().density);

                        workFlowLL.addView(dateTextView);
                    }

                }


                /* index flow 2 start from 0*/
                if(i == 2) {

                    workFlowLL.setBackgroundColor(backgroundColorMap.get(cssClassValue));

                    Drawable mDrawable = iconMap.get(iconClassValue);
                    if(iconClassValue.equalsIgnoreCase("fa fa-hourglass-start fa-2x")) {
                        mDrawable.setColorFilter(new
                                PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN));

                    } else {
                        mDrawable.setColorFilter(new
                                PorterDuffColorFilter(Color.parseColor("#006400"), PorterDuff.Mode.SRC_IN));
                        imageView.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_workflow_white_background));
                        imageView.setPadding(10, 10, 10, 10);
                    }

                    imageView.setImageDrawable(mDrawable);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setLayoutParams(imageViewLayputParams);

                    workFlowLL.addView(imageView);
                    // ImageView in Linear Layout end

                    // text view starts
                    actionTextView.setLayoutParams(actionTextViewLayoutParams);
                    actionTextView.setText(wf.getAction());
                    actionTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                    actionTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    actionTextView.setTypeface(actionTextView.getTypeface(), Typeface.BOLD);
                    actionTextView.setTextSize(textSizeAction * getResources().getDisplayMetrics().density);

                    workFlowLL.addView(actionTextView);
                    // text view end

                    if(cssClassValue =="wf approved" || cssClassValue  == "wf stop") {

                        TextView dateTextView = new TextView(this);
                        dateTextView.setLayoutParams(dateTextViewLayoutParams);
                        dateTextView.setText("Date: " + wf.getApprovedDate());
                        dateTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                        dateTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        dateTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                        dateTextView.setGravity(Gravity.CENTER_VERTICAL);
                        dateTextView.setTypeface(dateTextView.getTypeface(), Typeface.BOLD);
                        dateTextView.setTextSize(textSizeDate * getResources().getDisplayMetrics().density);

                        workFlowLL.addView(dateTextView);
                    }

                }


                /* index flow 3 start from 0*/
                if(i == 3) {

                    workFlowLL.setBackgroundColor(backgroundColorMap.get(cssClassValue));

                    Drawable mDrawable = iconMap.get(iconClassValue);
                    if(iconClassValue.equalsIgnoreCase("fa fa-hourglass-start fa-2x")) {
                        mDrawable.setColorFilter(new
                                PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN));
                    } else {

                        mDrawable.setColorFilter(new
                                PorterDuffColorFilter(Color.parseColor("#006400"), PorterDuff.Mode.SRC_IN));
                        imageView.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_workflow_white_background));
                        imageView.setPadding(10, 10, 10, 10);
                    }

                    imageView.setImageDrawable(mDrawable);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setLayoutParams(imageViewLayputParams);

                    workFlowLL.addView(imageView);
                    // ImageView in Linear Layout end

                    // text view starts
                    actionTextView.setLayoutParams(actionTextViewLayoutParams);
                    actionTextView.setText(wf.getAction());
                    actionTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                    actionTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    actionTextView.setTypeface(actionTextView.getTypeface(), Typeface.BOLD);
                    actionTextView.setTextSize(textSizeAction * getResources().getDisplayMetrics().density);

                    workFlowLL.addView(actionTextView);
                    // text view end

                    if(cssClassValue =="wf approved" || cssClassValue  == "wf stop") {

                        TextView dateTextView = new TextView(this);
                        dateTextView.setLayoutParams(dateTextViewLayoutParams);
                        dateTextView.setText("Date: " + wf.getApprovedDate());
                        dateTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                        dateTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        dateTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                        dateTextView.setGravity(Gravity.CENTER_VERTICAL);
                        dateTextView.setTypeface(dateTextView.getTypeface(), Typeface.BOLD);
                        dateTextView.setTextSize(textSizeDate * getResources().getDisplayMetrics().density);

                        workFlowLL.addView(dateTextView);
                    }

                }

                /* index flow 4 start from 0*/
                if(i == 4) {

                    workFlowLL.setBackgroundColor(backgroundColorMap.get(cssClassValue));

                    Drawable mDrawable = iconMap.get(iconClassValue);
                    if(iconClassValue.equalsIgnoreCase("fa fa-hourglass-start fa-2x")) {

                        mDrawable.setColorFilter(new
                                PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN));
                    } else {

                        mDrawable.setColorFilter(new
                                PorterDuffColorFilter(Color.parseColor("#006400"), PorterDuff.Mode.SRC_IN));
                        imageView.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_workflow_white_background));
                        imageView.setPadding(10, 10, 10, 10);
                    }

                    imageView.setImageDrawable(mDrawable);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setLayoutParams(imageViewLayputParams);

                    workFlowLL.addView(imageView);
                    // ImageView in Linear Layout end

                    // text view starts
                    actionTextView.setLayoutParams(actionTextViewLayoutParams);
                    actionTextView.setText(wf.getAction());
                    actionTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                    actionTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    actionTextView.setTypeface(actionTextView.getTypeface(), Typeface.BOLD);
                    actionTextView.setTextSize(textSizeAction * getResources().getDisplayMetrics().density);

                    workFlowLL.addView(actionTextView);
                    // text view end

                    if(cssClassValue =="wf approved" || cssClassValue  == "wf stop") {

                        TextView dateTextView = new TextView(this);
                        dateTextView.setLayoutParams(dateTextViewLayoutParams);
                        dateTextView.setText("Date: " + wf.getApprovedDate());
                        dateTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                        dateTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        dateTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                        dateTextView.setGravity(Gravity.CENTER_VERTICAL);
                        dateTextView.setTypeface(dateTextView.getTypeface(), Typeface.BOLD);
                        dateTextView.setTextSize(textSizeDate * getResources().getDisplayMetrics().density);

                        workFlowLL.addView(dateTextView);
                    }

                }


                allWorkFlowLL.addView(workFlowLL);
            }


            ScrollView scroll = new ScrollView(context);
            scroll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            scroll.addView(allWorkFlowLL);

            layoutButtons.addView(scroll);


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

//            workFlowBtn.setText("VIEW FLOW");
        }
    }


    private void performOperation(String opt) {

        if(null != opt) {

            boolean uiiaExhibits = false;

            if(opt.equalsIgnoreCase("approve") && "StreetInterchange".equalsIgnoreCase(irJson.getInterchangeRequests().getIrRequestType())) {

                if(null != irJson.getInProcessWf().getWfSeqType() && "MCB".equalsIgnoreCase(irJson.getInProcessWf().getWfSeqType())) {
                    uiiaExhibits = true;
                }
            }

            if(!uiiaExhibits) {
                performRemarks(opt, "");

            } else {


                fam.close(true);
                isOpen = true;
                viewMenu("");

                new ExecuteGetUIIAExhibitListTask(opt).execute();
            }
        }

    }

    private void callInterchangeRequestOperations(String opt, String uiiaExhibits, String remarks) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("irId", irJson.getInterchangeRequests().getIrId());
        jsonObject.addProperty("wfId", irJson.getInProcessWf().getWfId());
        jsonObject.addProperty("opt", opt);
        jsonObject.addProperty("uiiaExhibitStr", uiiaExhibits);
        jsonObject.addProperty("remarks", remarks);
        jsonObject.addProperty("accessToken", siaSecurityObj.getAccessToken());

        // code to disable background functionality when progress bar starts
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        Log.v("log_tag", "InterchangeRequestOperationActivity ExecuteOperationTask: jsonObject.toString():=> " + jsonObject.toString());
        new ExecuteOperationTask(jsonObject.toString()).execute();

    }

    void goToPreviousPage() {
        if (Internet_Check.checkInternetConnection(getApplicationContext())) {
            startActivity(new Intent(InterchangeRequestOperationActivity.this, ListInterchangeRequestActivity.class));
            finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */
        } else {
            Intent intent = new Intent(InterchangeRequestOperationActivity.this, NoInternetActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            goToPreviousPage();
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Method which will perform interchange request operation Activity
     */

    class ExecuteOperationTask extends AsyncTask<String, Integer, String> {
        String requestString;

        public ExecuteOperationTask(String requestString) {
            this.requestString = requestString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            ApiResponse apiResponse = RestApiClient.callPostApi(requestString, getString(R.string.base_url) +getString(R.string.api_interchange_request_operations));

            urlResponse = apiResponse.getMessage();
            urlResponseCode = apiResponse.getCode();
            return urlResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);

            try {

                Log.v("log_tag", "Interchange Request Operations urlResponseCode: " + urlResponseCode);
                Log.v("log_tag", "Interchange Request Operations urlResponse: " + urlResponse);
                Gson gson = new Gson();

                if (urlResponseCode == 200) {

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("irId", irJson.getInterchangeRequests().getIrId());
                    jsonObject.addProperty("accessToken", siaSecurityObj.getAccessToken());

                    Log.v("log_tag", "InterchangeRequestOperationActivity jsonObject:=> " + jsonObject.toString());
                    new ExecuteTaskToGetInterchangeRequestDetails(jsonObject.toString()).execute();


                } else {

                    try {
                        ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                        new ViewDialog().showDialog(InterchangeRequestOperationActivity.this, dialogTitle, errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                    } catch(Exception e) {
                        new ViewDialog().showDialog(InterchangeRequestOperationActivity.this, dialogTitle, getString(R.string.msg_error_try_after_some_time));
                    }
                }

            } catch (Exception e) {
                Log.v("log_tag", "InterchangeRequestOperationActivity Exception Error ", e);
            }

            // code to regain disable backend functionality end
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }

    }


        /* code to get interchange request details functionality starts */

    class ExecuteTaskToGetInterchangeRequestDetails extends AsyncTask<String, Integer, String> {
        String requestString;

        public ExecuteTaskToGetInterchangeRequestDetails(String requestString) {
            this.requestString = requestString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            ApiResponse apiResponse = RestApiClient.callPostApi(requestString, getString(R.string.base_url) + getString(R.string.api_get_interchange_request_details));
            urlResponse = apiResponse.getMessage();
            urlResponseCode = apiResponse.getCode();
            return urlResponse;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                Log.v("log_tag", "InterchangeRequestOperationActivity: ExecuteTaskToGetInterchangeRequestDetails: urlResponseCode:=>" + urlResponseCode);
                Log.v("log_tag", "InterchangeRequestOperationActivity: ExecuteTaskToGetInterchangeRequestDetails: result:=> " + result);
                Gson gson = new Gson();

                if (urlResponseCode == 200) {
                    InterchangeRequestsJson interchangeRequestsJson = gson.fromJson(result, InterchangeRequestsJson.class);
                    Log.v("log_tag", "interchangeRequestsJson:=>"+interchangeRequestsJson);

                    Integer[] categories = null;
                    String[] categoriesName = null;
                    String[] labelArray = null;
                    String[] valueArray = null;

                    List<String> labelList = new ArrayList<>();
                    List<String> valueList = new ArrayList<>();

                    List<Integer> categoriesList = new ArrayList<>();
                    List<String> categoriesNameList = new ArrayList<>();

                    InterchangeRequests ir = interchangeRequestsJson.getInterchangeRequests();

                    if(null == ir.getIntchgType() || ir.getIntchgType().trim().length()<= 0) {
                        categoriesList.add(9);
                        categoriesList.add(5);

                        categoriesNameList.add("Street Turn Details");
                        categoriesNameList.add("Original Interchange Location");

                        labelList.add("CONTAINER PROVIDER NAME");
                        labelList.add("CONTAINER PROVIDER SCAC");
                        labelList.add("MOTOR CARRIER NAME");
                        labelList.add("MOTOR CARRIER SCAC");
                        labelList.add("IMPORT BL");
                        labelList.add("EXPORT BOOKING#");
                        labelList.add("CONTAINER#");
                        labelList.add("CHASSIS#");
                        labelList.add("CHASSIS IEP SCAC");
                        labelList.add("LOCATION NAME");
                        labelList.add("LOCATION ADDRESS");
                        labelList.add("ZIP CODE");
                        labelList.add("CITY");
                        labelList.add("STATE");

                        valueList.add(ir.getEpCompanyName());
                        valueList.add(ir.getEpScacs());
                        valueList.add(ir.getMcACompanyName());
                        valueList.add(ir.getMcAScac());
                        valueList.add(ir.getImportBookingNum());
                        valueList.add(ir.getBookingNum());
                        valueList.add(ir.getContNum());
                        valueList.add(ir.getChassisNum());
                        valueList.add(ir.getIepScac());
                        valueList.add(ir.getOriginLocNm());
                        valueList.add(ir.getOriginLocAddr());
                        valueList.add(ir.getOriginLocZip());
                        valueList.add(ir.getOriginLocCity());
                        valueList.add(ir.getOriginLocState());

                    } else {

                        categoriesList.add(17);
                        categoriesList.add(5);
                        categoriesList.add(5);

                        categoriesNameList.add("Street Interchange Details");
                        categoriesNameList.add("Equipment Interchange Location");
                        categoriesNameList.add("Original Interchange Location");


                        labelList.add("CONTAINER PROVIDER NAME");
                        labelList.add("CONTAINER PROVIDER SCAC");
                        labelList.add("MOTOR CARRIER A'S NAME");
                        labelList.add("MOTOR CARRIER A'S SCAC");
                        labelList.add("MOTOR CARRIER B'S NAME");
                        labelList.add("MOTOR CARRIER B'S SCAC");
                        labelList.add("TYPE OF INTERCHANGE");
                        labelList.add("CONTAINER TYPE");
                        labelList.add("CONTAINER SIZE");
                        labelList.add("IMPORT BL");
                        labelList.add("EXPORT BOOKING#");
                        labelList.add("CONTAINER#");
                        labelList.add("CHASSIS#");
                        labelList.add("CHASSIS IEP SCAC");
                        labelList.add("CHASSIS TYPE");
                        labelList.add("CHASSIS SIZE");
                        labelList.add("GENSET#");
                        labelList.add("LOCATION NAME");
                        labelList.add("LOCATION ADDRESS");
                        labelList.add("ZIP CODE");
                        labelList.add("CITY");
                        labelList.add("STATE");
                        labelList.add("LOCATION NAME");
                        labelList.add("LOCATION ADDRESS");
                        labelList.add("ZIP CODE");
                        labelList.add("CITY");
                        labelList.add("STATE");

                        valueList.add(ir.getEpCompanyName());
                        valueList.add(ir.getEpScacs());
                        valueList.add(ir.getMcACompanyName());
                        valueList.add(ir.getMcAScac());
                        valueList.add(ir.getMcBCompanyName());
                        valueList.add(ir.getMcBScac());
                        valueList.add(ir.getIntchgType());
                        valueList.add(ir.getContType());
                        valueList.add(ir.getContSize());
                        valueList.add(ir.getImportBookingNum());
                        valueList.add(ir.getBookingNum());
                        valueList.add(ir.getContNum());
                        valueList.add(ir.getChassisNum());
                        valueList.add(ir.getIepScac());
                        valueList.add(ir.getChassisType());
                        valueList.add(ir.getChassisSize());
                        valueList.add(ir.getGensetNum());

                        valueList.add(ir.getEquipLocNm());
                        valueList.add(ir.getEquipLocAddr());
                        valueList.add(ir.getEquipLocZip());
                        valueList.add(ir.getEquipLocCity());
                        valueList.add(ir.getEquipLocState());

                        valueList.add(ir.getOriginLocNm());
                        valueList.add(ir.getOriginLocAddr());
                        valueList.add(ir.getOriginLocZip());
                        valueList.add(ir.getOriginLocCity());
                        valueList.add(ir.getOriginLocState());

                    }
                    if(null != interchangeRequestsJson.getUiiaExhibitDataList() && interchangeRequestsJson.getUiiaExhibitDataList().size() > 0) {
                        categoriesList.add(interchangeRequestsJson.getUiiaExhibitDataList().size());
                        categoriesNameList.add("Equipment Condition (per UIIA Exhibit A)");

                        for(int i= 0; i < interchangeRequestsJson.getUiiaExhibitDataList().size(); i++) {
                            labelList.add(interchangeRequestsJson.getUiiaExhibitDataList().get(i).get("item").toString());
                            valueList.add(interchangeRequestsJson.getUiiaExhibitDataList().get(i).get("item_desc").toString());
                        }
                    }

                    if(null != ir.getRemarks() && ir.getRemarks().length() > 0) {
                        String[] remarksArray = ir.getRemarks().split("\\|\\|");
                        categoriesList.add(remarksArray.length);
                        categoriesNameList.add("Previous Comments");
                        for(int i=0;i<remarksArray.length;i++) {
                            labelList.add("Remarks"+(i+1));
                            valueList.add(remarksArray[i]);
                        }
                    }

                    categories = categoriesList.toArray(new Integer[0]);
                    categoriesName = categoriesNameList.toArray(new String[0]);
                    labelArray = labelList.toArray(new String[0]);
                    valueArray = valueList.toArray(new String[0]);

                    List<FieldInfo> fieldInfoList = SIAUtility.prepareAndGetFieldInfoList(categories, categoriesName, labelArray, valueArray);
                    SIAUtility.setList(editor, "fieldInfoList", fieldInfoList);

                    SIAUtility.setObject(editor, GlobalVariables.KEY_OPERATION_IR_OBJ, interchangeRequestsJson);

                    editor.commit();

                    Intent intent = new Intent(InterchangeRequestOperationActivity.this, InterchangeRequestOperationActivity.class);
                    startActivity(intent);
                    finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */

                } else {
                    try {
                        ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                        new ViewDialog().showDialog(InterchangeRequestOperationActivity.this, dialogTitle, errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                    } catch(Exception e) {
                        new ViewDialog().showDialog(InterchangeRequestOperationActivity.this, dialogTitle, getString(R.string.msg_error_try_after_some_time));
                    }
                }

            } catch (Exception e) {
                Log.v("log_tag", "Error ", e);
            }

            progressBar.setVisibility(View.GONE);
        }
    }

    class ExecuteGetUIIAExhibitListTask extends AsyncTask<String, Integer, String> {

        final String opt;
        public ExecuteGetUIIAExhibitListTask(String opt) {
            this.opt = opt;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            ApiResponse apiResponse = RestApiClient.callGetApi(getString(R.string.base_url) +getString(R.string.api_get_uiia_exhibit_list));

            urlResponse = apiResponse.getMessage();
            urlResponseCode = apiResponse.getCode();
            return urlResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);

            try {

                Log.v("log_tag", "Interchange api_get_uiia_exhibit_list urlResponseCode: " + urlResponseCode);
//                Log.v("log_tag", "Interchange api_get_uiia_exhibit_list urlResponse: " + urlResponse);
                Gson gson = new Gson();

                if (urlResponseCode == 200) {

                    Type listType = new TypeToken<ArrayList<UIIAExhibit>>() {
                    }.getType();
                    uiiaExhibitList = gson.fromJson(result, listType);

                    processUIIAExhibitList(opt);

                } else {

                    try {
                        ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                        new ViewDialog().showDialog(InterchangeRequestOperationActivity.this, dialogTitle, errorMessage.getApiReqErrors().getErrors().get(0).getErrorMessage());

                    } catch(Exception e) {
                        new ViewDialog().showDialog(InterchangeRequestOperationActivity.this, dialogTitle, getString(R.string.msg_error_try_after_some_time));
                    }
                }

            } catch (Exception e) {
                Log.v("log_tag", "InterchangeRequestOperationActivity: getUIIExhibitList:Exception Error ", e);
            }

            // code to regain disable backend functionality end
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }

    }

    private void processUIIAExhibitList(final String opt) {

        selectedUIIAExhibitList = new ArrayList<>();

        final Dialog dialog = new Dialog(InterchangeRequestOperationActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_uiia_exhibit);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            dialog_ListView = dialog.findViewById(R.id.listViewExhibit);
            uiiaExhibitListViewAdapter = new UIIAExhibitListViewAdapter(InterchangeRequestOperationActivity.this, uiiaExhibitList);
            dialog_ListView.setAdapter(uiiaExhibitListViewAdapter);

        BottomNavigationView bnv = dialog.findViewById(R.id.navigation_uiia_exhibit);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (Internet_Check.checkInternetConnection(context)) {
                    switch (item.getItemId()) {
                        case R.id.navigation_ok:
                            if (null == selectedUIIAExhibitList || selectedUIIAExhibitList.size() <= 0) {
                                new ViewDialog().showDialog(InterchangeRequestOperationActivity.this, dialogTitle, getString(R.string.msg_error_select_uiia_exhibit));

                            } else {

                                // code to disable background functionality when progress bar starts
                                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                String uiiaExhibitStr = "";
                                for(Integer ueId : selectedUIIAExhibitList) {
                                    uiiaExhibitStr = uiiaExhibitStr + "," + String.valueOf(ueId);
                                }
                                uiiaExhibitStr = uiiaExhibitStr.substring(1);

                                performRemarks(opt, uiiaExhibitStr);
                                dialog.dismiss();
                            }
                            break;
                        case R.id.navigation_cancel:
                            dialog.dismiss();
                            break;
                    }

                } else {
                    Intent intent = new Intent(InterchangeRequestOperationActivity.this, NoInternetActivity.class);
                    startActivity(intent);
                }


                return true;
            }
        });

        SIAUtility.disableShiftMode(bnv);


        dialog.show();
    }

    class UIIAExhibitListViewAdapter extends BaseAdapter {

        private Context mContext;
        private List<UIIAExhibit> mProductList;

        // Constructor
        public UIIAExhibitListViewAdapter(Context mContext, List<UIIAExhibit> mProductList) {
            this.mContext = mContext;
            this.mProductList = mProductList;
        }

        @Override
        public int getCount() {
            return mProductList.size();
        }

        @Override
        public Object getItem(int position) {
            return mProductList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = View.inflate(mContext, R.layout.uiia_exhibit_list_view, null);

            ((TextView) v.findViewById(R.id.item)).setText(uiiaExhibitList.get(position).getItem());

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ImageView img = v.findViewById(R.id.selectImage);

                    if(img.getVisibility() == View.INVISIBLE) {
                        img.setVisibility(View.VISIBLE);
                        selectedUIIAExhibitList.add(mProductList.get(position).getUeId());
                    } else {
                        img.setVisibility(View.INVISIBLE);
                        selectedUIIAExhibitList.remove(mProductList.get(position).getUeId());
                    }
                }
            });

            return v;
        }
    } /* End */

    private void performRemarks(final String opt, final String uiiaExhibitStr) {

        Log.v("log_tag", "uiiaExhibitStr start performRemarks(final String opt, final String uiiaExhibitStr):=>"+uiiaExhibitStr);
        // Create custom dialog object
        final Dialog dialog = new Dialog(InterchangeRequestOperationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_remarks);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Set dialog title
        ((TextView) dialog.findViewById(R.id.titleTextView)).setText(dialogTitle);

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
                EditText remarks = dialog.findViewById(R.id.remarks);

                Log.v("log_tag", "uiiaExhibitStr before callInterchangeRequestOperations:=>"+uiiaExhibitStr);
                callInterchangeRequestOperations(opt, uiiaExhibitStr, remarks.getText().toString());
            }
        });
    }

}
