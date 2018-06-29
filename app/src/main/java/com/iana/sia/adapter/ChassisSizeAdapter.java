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

public class ChassisSizeAdapter extends BaseAdapter {
    Context context;
    String[] chassisSizeArray;
    LayoutInflater inflater;

    public ChassisSizeAdapter(Context applicationContext, String[] chassisSizeArray) {
        this.context = applicationContext;
        this.chassisSizeArray = chassisSizeArray;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return chassisSizeArray.length;
    }

    @Override
    public Object getItem(int i) {
        return chassisSizeArray[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.custom_spinner_chassis_size, null);
        TextView chassisSizeTextView = view.findViewById(R.id.chassisSizeTextView);
        chassisSizeTextView.setText(chassisSizeArray[i]);
        return view;
    }
}
