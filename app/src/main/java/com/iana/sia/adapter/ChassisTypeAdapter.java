package com.iana.sia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iana.sia.R;

/**
 * Created by Saumil on 6/28/2018.
 */

public class ChassisTypeAdapter extends BaseAdapter {
    Context context;
    String[] chassisTypeArray;
    LayoutInflater inflater;

    public ChassisTypeAdapter(Context applicationContext, String[] chassisTypeArray) {
        this.context = applicationContext;
        this.chassisTypeArray = chassisTypeArray;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return chassisTypeArray.length;
    }

    @Override
    public Object getItem(int i) {
        return chassisTypeArray[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.custom_spinner_chassis_type, null);
        TextView chassisTypeTextView = view.findViewById(R.id.chassisTypeTextView);
        chassisTypeTextView.setText(chassisTypeArray[i]);
        return view;
    }
}
