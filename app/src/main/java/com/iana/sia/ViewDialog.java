package com.iana.sia;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Saumil on 3/15/2018.
 */

public class ViewDialog {

    public void showDialog(final Activity activity, String title, String message){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_layout);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ((TextView) dialog.findViewById(R.id.titleTextView)).setText(title);
        ((TextView) dialog.findViewById(R.id.messageTextView)).setText(message);

        Button dialogButton = (Button) dialog.findViewById(R.id.okBtnDialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent i = activity.getIntent();
                Intent intent = null;

                if(activity.getClass().getSimpleName().equalsIgnoreCase("ListDVIRActivity")) {
                    intent = setSearchDVIRFields(i, activity);
                    activity.startActivity(intent);
                    activity.finish(); /* This method will not display this page once redirected to login page */

                }/* else if(activity.getClass().getSimpleName().equalsIgnoreCase("ListDVIRActivity")) {

                }*/


            }
        });

        dialog.show();

    }

    @NonNull
    private Intent setSearchDVIRFields(Intent i, Activity activity) {
        Intent intent;
        /*intent = new Intent(activity, SearchDVIRActivity.class);

        intent.putExtra("dvirNo", (String) i.getSerializableExtra("dvirNo"));

        intent.putExtra("chassisId", (String) i.getSerializableExtra("chassisId"));
        intent.putExtra("locationCode", (String) i.getSerializableExtra("locationCode"));
        intent.putExtra("startDate", (String) i.getSerializableExtra("startDate"));
        intent.putExtra("endDate", (String) i.getSerializableExtra("endDate"));
        intent.putExtra("originFrom", (String) i.getSerializableExtra("originFrom"));
        intent.putExtra("includeNoDefects", (String) i.getSerializableExtra("includeNoDefects"));*/

        return null;
    }
}
