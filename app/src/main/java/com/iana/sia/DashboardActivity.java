package com.iana.sia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.google.gson.Gson;
import com.iana.sia.model.InterchangeRequestsSearch;
import com.iana.sia.model.SIASecurityObj;
import com.iana.sia.model.User;
import com.iana.sia.utility.GlobalVariables;
import com.iana.sia.utility.Internet_Check;
import com.iana.sia.utility.SIAUtility;

public class DashboardActivity extends AppCompatActivity {

    //MC Section Start

    int[] mcMenuArr = new int[]{4, 2, 3, 0, 1, 20};
    int[] mcSecDefaultRightsMenuArr = new int[]{0, 20};
    int[] mcSecSingleRightsMenuArr = new int[]{4, 2, 0, 1, 20};
    int[] mcSecFullRightsMenuArr = new int[]{4, 2, 3, 0, 1, 20};


    //EP Section Start

    int[] epMenuArr = new int[]{2, 3, 0, 1, 20};
    int[] epSecDefaultRightsMenuArr = new int[]{20};
    int[] epSecSingleRightsMenuArr = new int[]{2, 0, 1, 20};
    int[] epSecFullRightsMenuArr = new int[]{2, 3, 0, 1, 20};


    //IDD Section

    int[] iddMenuArr = new int[]{4, 2, 3, 0, 1, 20};


    //TPU Section

    int[] tpuDefaultRightsMenuArr = new int[]{5, 6, 20};
    int[] tpuSingleRightsMenuArr = new int[]{2, 0, 1, 5, 6, 20};
    int[] tpuFullRightsMenuArr = new int[]{2, 3, 0, 1, 5, 6, 20};


    //final array which hold run time value from the rights based login.
    int[] finalArr = null;

    SharedPreferences sharedPref = null;
    SharedPreferences.Editor editor;

    SIASecurityObj siaSecurityObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        showActionBar();
        ((TextView) findViewById(R.id.title)).setText(R.string.title_dashboard);

        sharedPref = getSharedPreferences(GlobalVariables.KEY_SECURITY_OBJ, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        siaSecurityObj = SIAUtility.getObjectOfModel(sharedPref, GlobalVariables.KEY_SECURITY_OBJ, SIASecurityObj.class);

        if(siaSecurityObj.getRoleName().equalsIgnoreCase(GlobalVariables.ROLE_MC)) {
            finalArr = new int[mcMenuArr.length];
            for(int i=0;i<finalArr.length;i++){
                finalArr[i] = mcMenuArr[i];
            }
        } else if(siaSecurityObj.getRoleName().equalsIgnoreCase(GlobalVariables.ROLE_EP)) {
            finalArr = new int[epMenuArr.length];
            for(int i=0;i<finalArr.length;i++){
                finalArr[i] = epMenuArr[i];
            }
        }

        setupDashboard(finalArr);
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

    public void setupDashboard(int[] finalArr) {

        LinearLayout mainLayout = findViewById(R.id.rootLinearLayout);

        LinearLayout subLinearLayout = new LinearLayout(this);
        subLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams llLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llLayoutParams.setMargins(0, 0, 0, 10);
        subLinearLayout.setLayoutParams(llLayoutParams);
        subLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);

        int originalCount = finalArr.length;
        int count = (finalArr.length % 2 == 0) ? finalArr.length : (finalArr.length + 1);
        int marginBetweenCardView = 5;
        int marginTopBottomCardView = 10;

        for(int i = 0; i < count; i++) {

//            Log.d("myTag", "DashboardActivity finalArr["+i+"]:============================>"+finalArr[i]);
            if(i > 1 && i%2 == 0) {
                mainLayout.addView(subLinearLayout);
                subLinearLayout = new LinearLayout(this);
                subLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                subLinearLayout.setLayoutParams(llLayoutParams);
                subLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            }

            // code is when original count is ODD Number
            if(i > 1 && originalCount % 2 == 1 && count == (i+1)) {
                LinearLayout lastColumnLinearLayout = new LinearLayout(this);
                lastColumnLinearLayout.setOrientation(LinearLayout.VERTICAL);
                LayoutParams lastColumnLinearLayoutParams = new LayoutParams(0, 280, 1.0f);
                lastColumnLinearLayoutParams.setMargins(0, 0, 10, 0);
                lastColumnLinearLayout.setLayoutParams(lastColumnLinearLayoutParams);
                lastColumnLinearLayout.setGravity(Gravity.CENTER);
                subLinearLayout.addView(lastColumnLinearLayout);
//                Log.d("myTag", "This is my message");
                continue;
            }

            // card view start
                CardView cardView = new CardView(this);
                cardView.setClickable(true);
                // Set a background color for CardView
                cardView.setCardBackgroundColor(Color.parseColor("#F1F5F8"));

                // shadow starts
                    // Set the CardView maximum elevation
                    cardView.setMaxCardElevation(15.0f);
                    // Set CardView elevation
                    cardView.setCardElevation(9.0f);
                // shadow end

                cardView.setId(finalArr[i]);

                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    if (Internet_Check.checkInternetConnection(getApplicationContext())) {

                        if (null != GlobalVariables.menuTitleArr[v.getId()] &&
                                GlobalVariables.menuTitleArr[v.getId()].equalsIgnoreCase(GlobalVariables.MENU_TITLE_LOGOUT)) {

                            callLogout();

                        } else if (null != GlobalVariables.menuTitleArr[v.getId()] &&
                                GlobalVariables.menuTitleArr[v.getId()].equalsIgnoreCase(GlobalVariables.MENU_TITLE_INITIATE_ST)) {

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.remove(GlobalVariables.KEY_RETURN_FROM);
                            editor.commit();

                            Intent intent = new Intent(DashboardActivity.this, StreetTurnActivity.class);
                            startActivity(intent);
                            finish(); /* This method will not display login page when click back (return) from phone */
                                /* End */

                        } else if (null != GlobalVariables.menuTitleArr[v.getId()] &&
                                GlobalVariables.menuTitleArr[v.getId()].equalsIgnoreCase(GlobalVariables.MENU_TITLE_INITIATE_SI)) {

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.remove(GlobalVariables.KEY_RETURN_FROM);
                            editor.commit();

                            Intent intent = new Intent(DashboardActivity.this, InitiateInterchangeActivity.class);
                            startActivity(intent);
                            finish(); /* This method will not display login page when click back (return) from phone */
                                /* End */

                        } else if (null != GlobalVariables.menuTitleArr[v.getId()] &&
                                GlobalVariables.menuTitleArr[v.getId()].equalsIgnoreCase(GlobalVariables.MENU_TITLE_SEARCH_INTERCHANGE_REQUESTS)) {

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.remove(GlobalVariables.KEY_RETURN_FROM);
                            editor.commit();

                            Intent intent = new Intent(DashboardActivity.this, SearchInterchangeRequestActivity.class);
                            startActivity(intent);
                            finish(); /* This method will not display login page when click back (return) from phone */
                                /* End */
                        }

                    } else {
                        Intent intent = new Intent(DashboardActivity.this, NoInternetActivity.class);
                        startActivity(intent);
                    }

                    }
                });

                // Set the CardView layoutParams
                LayoutParams cardViewLayoutparams = new LayoutParams(0, 280, 1.0f);
                cardViewLayoutparams.setMargins(marginBetweenCardView, marginTopBottomCardView, marginBetweenCardView, marginTopBottomCardView);
                cardView.setLayoutParams(cardViewLayoutparams);

            // card view end

            // Linear Layout in CardView starts
                LinearLayout relativeLayoutInCardView = new LinearLayout(this);
                relativeLayoutInCardView.setGravity(Gravity.CENTER);
                relativeLayoutInCardView.setOrientation(LinearLayout.VERTICAL);
                RelativeLayout.LayoutParams relativeLayoutInCardViewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                relativeLayoutInCardView.setLayoutParams(relativeLayoutInCardViewLayoutParams);
                relativeLayoutInCardView.setGravity(Gravity.CENTER);
                relativeLayoutInCardView.setElevation(16f);

            // ImageView in Linear Layout starts
                ImageView imageView = new ImageView(this);
                imageView.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_background));
                imageView.setImageResource(this.getResources().getIdentifier(GlobalVariables.menuIconArr[finalArr[i]], "drawable", this.getPackageName()));
                imageView.setPadding(20, 20, 20, 20);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                LayoutParams imageViewLayputParams = new LayoutParams(96, 96);
                imageViewLayputParams.setLayoutDirection(Gravity.CENTER);
                imageView.setLayoutParams(imageViewLayputParams);
            // ImageView in Linear Layout end

            // View starts
                View view = new View(this);
                LayoutParams viewLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 2);
                viewLayoutParams.setMargins(10, 20, 10, 20);
                view.setLayoutParams(viewLayoutParams);
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_gray));
            // View end

            // text view starts
                TextView textView = new TextView(this);
                LayoutParams textViewLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                textViewLayoutParams.setMargins(5, 10, 0, 5);
                textView.setLayoutParams(textViewLayoutParams);
                textView.setText(GlobalVariables.menuTitleArr[finalArr[i]]);
                textView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
                textView.setGravity(Gravity.CENTER);
                textView.setLines(3);
                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                textView.setTextSize(6 * getResources().getDisplayMetrics().density);
            // text view end

                relativeLayoutInCardView.addView(imageView);
                relativeLayoutInCardView.addView(view);
                relativeLayoutInCardView.addView(textView);

            // Linear Layout in CardView end

            cardView.addView(relativeLayoutInCardView);

            subLinearLayout.addView(cardView);

        }

        mainLayout.addView(subLinearLayout);

    }

    private void callLogout() {
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(GlobalVariables.KEY_ORIGIN_FROM, "");
        editor.putString(GlobalVariables.KEY_ACCESS_TOKEN, "");
        editor.putString(GlobalVariables.KEY_SCAC, "");
        editor.putString(GlobalVariables.KEY_ROLE, "");

        editor.putString(GlobalVariables.KEY_COMPANY_NAME, "");
        editor.putString(GlobalVariables.KEY_MEM_TYPE, "");

        editor.putString(GlobalVariables.KEY_RETURN_FROM, ""); // used when return from location search screen

        editor.clear();
        editor.commit(); // commit changes

        Intent intent = new Intent(DashboardActivity.this, LoginMCActivity.class);
        startActivity(intent);
        finish(); /* This method will not display login page when click back (return) from phone */
                    /* End */
    }

}
