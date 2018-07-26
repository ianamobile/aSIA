package com.iana.sia;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.iana.sia.model.FieldInfo;
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

    FloatingActionMenu fam;
    FloatingActionButton cancelBtn, approveBtn, rejectBtn, onholdBtn, reinitiateBtn;

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

        context = this;

        dialogTitle = getString(R.string.dialog_title_interchange_request_operation);

        SIAUtility.showActionBar(context, getSupportActionBar());

        ((TextView) findViewById(R.id.title)).setText(R.string.title_view_details);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setText(R.string.title_back);
        backBtn.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.title)).setTextColor(ContextCompat.getColor(context, R.color.color_white));
        backBtn.setTextColor(ContextCompat.getColor(context, R.color.color_white));

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
                viewMenu();
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
        mDrawable = ContextCompat.getDrawable(context, R.drawable.menu);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN));
        fam.getMenuIconView().setImageDrawable(mDrawable);
        fam.setMenuButtonColorNormal(ContextCompat.getColor(context, R.color.appThemeColor));
        fam.setMenuButtonColorPressed(ContextCompat.getColor(context, R.color.appThemeColor));

        //handling menu status (open or close)
        fam.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                RelativeLayout relativeLayout = findViewById(R.id.layoutMain);
                if (opened) {
                    if(workFlowBtn.getVisibility() == View.VISIBLE) {
                        workFlowBtn.setVisibility(View.GONE);
                    }

                    mDrawable = ContextCompat.getDrawable(context, R.drawable.if_remove);
                    mDrawable.setColorFilter(new
                            PorterDuffColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN));
                    fam.getMenuIconView().setImageDrawable(SIAUtility.resizeIcon(mDrawable, getResources(), 80, 80));
                    relativeLayout.setAlpha(0);
                    setButtonVisibility(View.VISIBLE);

                } else {

                    if(workFlowBtn.getVisibility() == View.GONE) {
                        workFlowBtn.setVisibility(View.VISIBLE);
                    }

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

                cancelBtn.setImageDrawable(SIAUtility.resizeIcon(mDrawable, getResources(), 50, 50));
//                cancelBtn.setLabelTextColor(R.color.color_black);
            }

            approveBtn.setVisibility(visibleOrGone);
                mDrawable = ContextCompat.getDrawable(context, R.drawable.approve);
                mDrawable.setColorFilter(new
                        PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN));
            approveBtn.setImageDrawable(SIAUtility.resizeIcon(mDrawable, getResources(), 50, 50));


            if(null != irJson.getInProcessWf().getWfSeqType() && GlobalVariables.INITIATOR_MCA.equalsIgnoreCase(irJson.getInProcessWf().getWfSeqType())) {
                onholdBtn.setVisibility(visibleOrGone);
                    mDrawable = ContextCompat.getDrawable(context, R.drawable.onhold);
                    mDrawable.setColorFilter(new
                            PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN));
                onholdBtn.setImageDrawable(SIAUtility.resizeIcon(mDrawable, getResources(), 50, 50));
            }

            rejectBtn.setVisibility(visibleOrGone);
                mDrawable = ContextCompat.getDrawable(context, R.drawable.reject);
                mDrawable.setColorFilter(new
                        PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN));
            rejectBtn.setImageDrawable(SIAUtility.resizeIcon(mDrawable, getResources(), 50, 50));

            reinitiateBtn.setVisibility(visibleOrGone);
                mDrawable = ContextCompat.getDrawable(context, R.drawable.reinitiate);
                mDrawable.setColorFilter(new
                        PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN));
            reinitiateBtn.setImageDrawable(SIAUtility.resizeIcon(mDrawable, getResources(), 50, 50));


        } else {

                if(GlobalVariables.STATUS_PENDING.equalsIgnoreCase(irJson.getInterchangeRequests().getStatus())) {
                    if (null != irJson.getShowCancelButtons() && GlobalVariables.Y.equalsIgnoreCase(irJson.getShowCancelButtons())) {
                        cancelBtn.setVisibility(visibleOrGone);
                            mDrawable = ContextCompat.getDrawable(context, R.drawable.cross);
                            mDrawable.setColorFilter(new
                                    PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN));
                        cancelBtn.setImageDrawable(SIAUtility.resizeIcon(mDrawable, getResources(), 50, 50));
                    }
                }

            reinitiateBtn.setVisibility(visibleOrGone);
                mDrawable = ContextCompat.getDrawable(context, R.drawable.reinitiate);
                mDrawable.setColorFilter(new
                        PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN));
            reinitiateBtn.setImageDrawable(SIAUtility.resizeIcon(mDrawable, getResources(), 50, 50));
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
                row = (TableRow)LayoutInflater.from(context).inflate(R.layout.verify_heading, null);
                ((TextView)row.findViewById(R.id.title)).setText(fieldInfo.getValue());
                ((TextView)row.findViewById(R.id.title)).setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                tl.addView(row);

            } else if(fieldInfo.getTitle().equalsIgnoreCase(GlobalVariables.FIELD_INFO_TITLE)) {
                row = (TableRow)LayoutInflater.from(context).inflate(R.layout.verify_content, null);

                if(fieldInfo.getValue().equalsIgnoreCase("Remarks")) {
                    fieldInfo = fieldInfoList.get(++i);
                    ((TextView) row.findViewById(R.id.locationLbl)).setText(fieldInfo.getValue());
                    ((TextView) row.findViewById(R.id.locationLbl)).setTextColor(ContextCompat.getColor(context, R.color.appThemeColor));
                    ((TextView)row.findViewById(R.id.locationLbl)).setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

                    ((TextView) row.findViewById(R.id.locationValue)).setText("");
                    ((TextView)row.findViewById(R.id.locationValue)).setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                    tl.addView(row);

                } else {
                    ((TextView) row.findViewById(R.id.locationLbl)).setText(fieldInfo.getValue());
                    ((TextView)row.findViewById(R.id.locationLbl)).setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

                    fieldInfo = fieldInfoList.get(++i);
                    ((TextView) row.findViewById(R.id.locationValue)).setText(fieldInfo.getValue());
                    ((TextView)row.findViewById(R.id.locationValue)).setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                    tl.addView(row);
                }

            } else  if(fieldInfo.getTitle().equalsIgnoreCase(GlobalVariables.FIELD_INFO_BLANK)) {
                row = new TableRow(context);
                LinearLayout ll = new LinearLayout(context);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 40, 1f);
                lp.setMargins(0, 10, 0, 0);
                ll.setLayoutParams(lp);
                ll.setBackground(getDrawable(R.drawable.cell_shape));
                row.addView(ll);
                tl.addView(row);

            }
        }

    }

    // code to view / close workflow
    private void viewMenu() {
        int x = workFlowBtn.getRight();
        int y = workFlowBtn.getBottom();
        if(!isOpen) {

            if(fam.getVisibility() == View.VISIBLE) {
                fam.setVisibility(View.GONE);
            }

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

            workFlowBtn.setText("");
            workFlowBtn.setBackground(getDrawable(R.drawable.border_view_workflow_close));
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
                iconMap.put("fa fa-stop-circle fa-2x", ContextCompat.getDrawable(context, R.drawable.reject));
                iconMap.put("fa fa-hourglass-start fa-2x", ContextCompat.getDrawable(context, R.drawable.pending_hourglass));
                iconMap.put("fa fa-clock-o fa-2x", ContextCompat.getDrawable(context, R.drawable.awaiting_clock));
                iconMap.put("fa fa-check-circle fa-2x", ContextCompat.getDrawable(context, R.drawable.approve));


            LinearLayout allWorkFlowLL = new LinearLayout(this);
            allWorkFlowLL.setGravity(Gravity.CENTER);
            allWorkFlowLL.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams allWorkFlowLLLayoutParams =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            allWorkFlowLL.setLayoutParams(allWorkFlowLLLayoutParams);
            allWorkFlowLL.setGravity(Gravity.CENTER);
            allWorkFlowLL.setPadding(0, 20, 0, 0);

            int textSizeAction = 7;
            int textSizeDate = 7;

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
                        if(onHoldOrRejectRequestIndex.equals("")) {

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

                workFlowLLLayoutParams.setMargins(20, 0, 20, 0);
                workFlowLL.setLayoutParams(workFlowLLLayoutParams);
                workFlowLL.setGravity(Gravity.CENTER);
                workFlowLL.setElevation(5f);

                TableLayout tl = new TableLayout(this);
                tl.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                /* Create a new row to be added. */
                TableRow tr = new TableRow(this);
                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                LinearLayout imageViewLL = new LinearLayout(this);
                LinearLayout.LayoutParams imageViewLLLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                imageViewLL.setLayoutParams(imageViewLLLayoutParams);
                imageViewLL.setGravity(Gravity.CENTER);

                // ImageView in Linear Layout starts
                ImageView imageView = new ImageView(this);
                    LinearLayout.LayoutParams imageViewLayoutParams = new LinearLayout.LayoutParams(64, 64);
                    imageViewLayoutParams.setLayoutDirection(Gravity.CENTER);


                    // text view to display action starts
                LinearLayout actionDateLl = new LinearLayout(this);
                LinearLayout.LayoutParams actionDateLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                actionDateLayoutParams.setMargins(0, 5, 5, 5);
                actionDateLl.setLayoutParams(actionDateLayoutParams);
                actionDateLl.setOrientation(LinearLayout.VERTICAL);

                TextView actionTextView = new TextView(this);
                        LinearLayout.LayoutParams actionTextViewLayoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    actionTextViewLayoutParams.setMargins(0, 5, 5, 5);
                    actionTextView.setLayoutParams(actionTextViewLayoutParams);


                    LinearLayout.LayoutParams dateTextViewLayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        dateTextViewLayoutParams.setMargins(0, 5, 5, 5);

                /* index flow 0 start from 0*/
                if(i == 0) {

                    workFlowLL.setBackgroundColor(backgroundColorMap.get("wf approved"));

                    imageView.setBackground(ContextCompat.getDrawable(this, R.drawable.wf_approve_icon));
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setLayoutParams(imageViewLayoutParams);

                    imageViewLL.addView(imageView);

                    // ImageView in Linear Layout end

                    // text view starts
                        actionTextView.setLayoutParams(actionTextViewLayoutParams);
                        actionTextView.setText(wf.getAction());
                        actionTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                        actionTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        actionTextView.setGravity(Gravity.CENTER_VERTICAL);
//                        actionTextView.setTypeface(actionTextView.getTypeface(), Typeface.BOLD);
                        actionTextView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                        actionTextView.setTextSize(textSizeAction * getResources().getDisplayMetrics().density);

                        actionDateLl.addView(actionTextView);

                    // text view end

                    if(cssClassValue.equalsIgnoreCase("wf approved") || cssClassValue.equalsIgnoreCase("wf stop")) {

                        TextView dateTextView = new TextView(this);
                        dateTextView.setLayoutParams(dateTextViewLayoutParams);
                        dateTextView.setText("Date: ".concat(wf.getApprovedDate()));
                        dateTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                        dateTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        dateTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                        dateTextView.setGravity(Gravity.CENTER_VERTICAL);
                        dateTextView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                        dateTextView.setTextSize(textSizeDate * getResources().getDisplayMetrics().density);

                        actionDateLl.addView(dateTextView);
                    }

                }

                /* index flow 1 start from 0*/
                if(i == 1) {

                    workFlowLL.setBackgroundColor(backgroundColorMap.get(cssClassValue));

                    setWorkflow(imageView, iconMap, iconClassValue);

                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setLayoutParams(imageViewLayoutParams);

                    imageViewLL.addView(imageView);

                    // ImageView in Linear Layout end

                    // text view starts
                    actionTextView.setLayoutParams(actionTextViewLayoutParams);
                    actionTextView.setText(wf.getAction());
                    actionTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                    actionTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    actionTextView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                    actionTextView.setTextSize(textSizeAction * getResources().getDisplayMetrics().density);

                    actionDateLl.addView(actionTextView);
                    // text view end

                    if(cssClassValue.equalsIgnoreCase("wf approved") || cssClassValue.equalsIgnoreCase("wf stop")) {

                        TextView dateTextView = new TextView(this);
                        dateTextView.setLayoutParams(dateTextViewLayoutParams);
                        dateTextView.setText("Date: ".concat(wf.getApprovedDate()));
                        dateTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                        dateTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        dateTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                        dateTextView.setGravity(Gravity.CENTER_VERTICAL);
                        dateTextView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                        dateTextView.setTextSize(textSizeDate * getResources().getDisplayMetrics().density);

                        actionDateLl.addView(dateTextView);
                    }

                }


                /* index flow 2 start from 0*/
                if(i == 2) {

                    workFlowLL.setBackgroundColor(backgroundColorMap.get(cssClassValue));

                    setWorkflow(imageView, iconMap, iconClassValue);

                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setLayoutParams(imageViewLayoutParams);

                    imageViewLL.addView(imageView);

                    // ImageView in Linear Layout end

                    // text view starts
                    actionTextView.setLayoutParams(actionTextViewLayoutParams);
                    actionTextView.setText(wf.getAction());
                    actionTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                    actionTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    actionTextView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                    actionTextView.setTextSize(textSizeAction * getResources().getDisplayMetrics().density);

                    actionDateLl.addView(actionTextView);
                    // text view end

                    if(cssClassValue.equalsIgnoreCase("wf approved") || cssClassValue.equalsIgnoreCase("wf stop")) {

                        TextView dateTextView = new TextView(this);
                        dateTextView.setLayoutParams(dateTextViewLayoutParams);
                        dateTextView.setText("Date: ".concat(wf.getApprovedDate()));
                        dateTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                        dateTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        dateTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                        dateTextView.setGravity(Gravity.CENTER_VERTICAL);
                        dateTextView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                        dateTextView.setTextSize(textSizeDate * getResources().getDisplayMetrics().density);

                        actionDateLl.addView(dateTextView);
                    }

                }


                /* index flow 3 start from 0*/
                if(i == 3) {

                    workFlowLL.setBackgroundColor(backgroundColorMap.get(cssClassValue));

                    setWorkflow(imageView, iconMap, iconClassValue);

                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setLayoutParams(imageViewLayoutParams);

                    imageViewLL.addView(imageView);

                    // ImageView in Linear Layout end

                    // text view starts
                    actionTextView.setLayoutParams(actionTextViewLayoutParams);
                    actionTextView.setText(wf.getAction());
                    actionTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                    actionTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    actionTextView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                    actionTextView.setTextSize(textSizeAction * getResources().getDisplayMetrics().density);

                    actionDateLl.addView(actionTextView);
                    // text view end

                    if(cssClassValue.equalsIgnoreCase("wf approved") || cssClassValue.equalsIgnoreCase("wf stop")) {

                        TextView dateTextView = new TextView(this);
                        dateTextView.setLayoutParams(dateTextViewLayoutParams);
                        dateTextView.setText("Date: ".concat(wf.getApprovedDate()));
                        dateTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                        dateTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        dateTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                        dateTextView.setGravity(Gravity.CENTER_VERTICAL);
                        dateTextView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                        dateTextView.setTextSize(textSizeDate * getResources().getDisplayMetrics().density);

                        actionDateLl.addView(dateTextView);
                    }

                }

                /* index flow 4 start from 0*/
                if(i == 4) {

                    workFlowLL.setBackgroundColor(backgroundColorMap.get(cssClassValue));

                    setWorkflow(imageView, iconMap, iconClassValue);

                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setLayoutParams(imageViewLayoutParams);

                    imageViewLL.addView(imageView);

                    // ImageView in Linear Layout end

                    // text view starts
                    actionTextView.setLayoutParams(actionTextViewLayoutParams);
                    actionTextView.setText(wf.getAction());
                    actionTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                    actionTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    actionTextView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                    actionTextView.setTextSize(textSizeAction * getResources().getDisplayMetrics().density);

                    actionDateLl.addView(actionTextView);
                    // text view end

                    if(cssClassValue.equalsIgnoreCase("wf approved") || cssClassValue.equalsIgnoreCase("wf stop")) {

                        TextView dateTextView = new TextView(this);
                        dateTextView.setLayoutParams(dateTextViewLayoutParams);
                        dateTextView.setText("Date: ".concat(wf.getApprovedDate()));
                        dateTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                        dateTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        dateTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                        dateTextView.setGravity(Gravity.CENTER_VERTICAL);
                        dateTextView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                        dateTextView.setTextSize(textSizeDate * getResources().getDisplayMetrics().density);

                        actionDateLl.addView(dateTextView);
                    }

                }


                tr.addView(imageViewLL, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1.5f));

                tr.addView(actionDateLl, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 8.5f));

                tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                workFlowLL.addView(tl);

                allWorkFlowLL.addView(workFlowLL);

                if((workFlowList.size() - 1) != i) {
                    // Down Arrow ImageView in Linear Layout starts
                    ImageView downArrowImageView = new ImageView(this);
                    LinearLayout.LayoutParams downArrowImageViewLayoutParams = new LinearLayout.LayoutParams(48, 48);
                    downArrowImageViewLayoutParams.setLayoutDirection(Gravity.CENTER);
                    downArrowImageViewLayoutParams.setMargins(0, 0, 0, 0);

                    downArrowImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.down_arrow));

                    allWorkFlowLL.addView(downArrowImageView);
                }

                if((workFlowList.size() - 1) == i) {
                    workFlowLL = new LinearLayout(this);
                    workFlowLL.setGravity(Gravity.CENTER);
                    workFlowLL.setOrientation(LinearLayout.VERTICAL);
                    workFlowLL.setLayoutParams(workFlowLLLayoutParams);
                    workFlowLL.setGravity(Gravity.CENTER);

                    allWorkFlowLL.addView(workFlowLL);
                }
            }


            ScrollView scroll = new ScrollView(context);
            scroll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            scroll.addView(allWorkFlowLL);

            layoutButtons.addView(scroll);


        } else {

            if(fam.getVisibility() == View.GONE) {
                fam.setVisibility(View.VISIBLE);
            }

            workFlowBtn.setBackground(getDrawable(R.drawable.border_view_workflow));
            workFlowBtn.setText("VIEW\nWORKFLOW");
            workFlowBtn.setTextColor(ContextCompat.getColor(this, R.color.color_white));

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
                viewMenu();

            // code to disable background functionality when progress bar starts
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                progressBar.setVisibility(View.VISIBLE);

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

        progressBar.setVisibility(View.VISIBLE);

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

    private class ExecuteOperationTask extends AsyncTask<String, Integer, String> {
        String requestString;

        private ExecuteOperationTask(String requestString) {
            this.requestString = requestString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                ApiResponseMessage errorMessage = gson.fromJson(result, ApiResponseMessage.class);
                if (urlResponseCode == 200) {

                    editor.putString(GlobalVariables.KEY_SUCCESS_MESSAGE, errorMessage.getMessage());
                    editor.commit();

                    Intent intent = new Intent(InterchangeRequestOperationActivity.this, InterchangeRequestOperationAckActivity.class);
                    startActivity(intent);
                    finish(); /* This method will not display login page when click back (return) from phone */
                            /* End */

                } else {

                    try {

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


    private class ExecuteGetUIIAExhibitListTask extends AsyncTask<String, Integer, String> {

        final String opt;
        private ExecuteGetUIIAExhibitListTask(String opt) {
            this.opt = opt;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

            // code to regain disable backend functionality end
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

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
            uiiaExhibitListViewAdapter = new UIIAExhibitListViewAdapter(context, uiiaExhibitList);
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
                    Intent intent = new Intent(context, NoInternetActivity.class);
                    startActivity(intent);
                }


                return true;
            }
        });

        SIAUtility.disableShiftMode(bnv);


        dialog.show();
    }

    private class UIIAExhibitListViewAdapter extends BaseAdapter {

        private Context mContext;
        private List<UIIAExhibit> mProductList;

        // Constructor
        private UIIAExhibitListViewAdapter(Context mContext, List<UIIAExhibit> mProductList) {
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
            ((TextView) v.findViewById(R.id.itemDesc)).setText(uiiaExhibitList.get(position).getItemDesc());

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ImageView img = v.findViewById(R.id.selectImage);

                    if(img.getVisibility() == View.INVISIBLE) {

                        v.setBackgroundColor(ContextCompat.getColor(context, R.color.color_light_gray));

                        Drawable mDrawable = ContextCompat.getDrawable(context, R.drawable.approve);
                            mDrawable.setColorFilter(new
                                    PorterDuffColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN));
                        img.setImageDrawable(mDrawable);
                        img.setVisibility(View.VISIBLE);

                        selectedUIIAExhibitList.add(mProductList.get(position).getUeId());
                    } else {
                        img.setVisibility(View.INVISIBLE);
                        v.setBackgroundColor(ContextCompat.getColor(context, R.color.color_white));
                        selectedUIIAExhibitList.remove(mProductList.get(position).getUeId());
                    }
                }
            });

            return v;
        }
    } /* End */

    private void performRemarks(final String opt, final String uiiaExhibitStr) {

//        Log.v("log_tag", "uiiaExhibitStr start performRemarks(final String opt, final String uiiaExhibitStr):=>"+uiiaExhibitStr);
        // Create custom dialog object
        final Dialog dialog = new Dialog(InterchangeRequestOperationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_remarks);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Set dialog title
        ((TextView) dialog.findViewById(R.id.titleTextView)).setText(getString(R.string.dialog_title_add_remarks));

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


    private void setWorkflow(ImageView imageView, Map<String, Drawable> iconMap, String iconClassValue) {
        Drawable mDrawable = iconMap.get(iconClassValue);
        if(iconClassValue.equalsIgnoreCase("fa fa-hourglass-start fa-2x") ||
                iconClassValue.equalsIgnoreCase("fa fa-clock-o fa-2x")) {

            mDrawable.setColorFilter(new
                    PorterDuffColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN));
            imageView.setImageDrawable(mDrawable);

        } else if(iconClassValue.equalsIgnoreCase("fa fa-check-circle fa-2x")) {
            imageView.setBackground(ContextCompat.getDrawable(this, R.drawable.wf_approve_icon));

        } else if(iconClassValue.equalsIgnoreCase("fa fa-stop-circle fa-2x")){

            mDrawable.setColorFilter(new
                    PorterDuffColorFilter(Color.parseColor("#d9534f"), PorterDuff.Mode.SRC_IN));
            imageView.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_workflow_white_background));
            imageView.setPadding(10, 10, 10, 10);
            imageView.setImageDrawable(mDrawable);
        }

    }

}
