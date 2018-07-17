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

import com.iana.sia.utility.GlobalVariables;

import java.util.Map;

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

        Button dialogButton = dialog.findViewById(R.id.okBtnDialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if(activity instanceof ListInterchangeRequestActivity) {
                    ((ListInterchangeRequestActivity) activity).goToPreviousPage();

                }/* else if(activity instanceof ListNotifAvailActivity) {
                    ((ListNotifAvailActivity) activity).goToPreviousPage();
                }*/
            }
        });

        dialog.show();

    }

    public void showDialog(final Activity activity, String title, String message, final Map<String, Object> extraMap){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_layout);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ((TextView) dialog.findViewById(R.id.titleTextView)).setText(title);
        ((TextView) dialog.findViewById(R.id.messageTextView)).setText(message);

        Button dialogButton = dialog.findViewById(R.id.okBtnDialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if(activity instanceof ListNotifAvailActivity && null != extraMap &&
                        null != extraMap.get("goToPreviousPage") & extraMap.get("goToPreviousPage").equals(true)) {
                    ((ListNotifAvailActivity) activity).goToPreviousPage();
                }
            }
        });

        dialog.show();

    }

}
