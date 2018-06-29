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

public class ContainerTypeAdapter extends BaseAdapter {
    Context context;
    String[] containerTypeArray;
    LayoutInflater inflater;

    public ContainerTypeAdapter(Context applicationContext, String[] containerTypeArray) {
        this.context = applicationContext;
        this.containerTypeArray = containerTypeArray;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return containerTypeArray.length;
    }

    @Override
    public Object getItem(int i) {
        return containerTypeArray[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.custom_spinner_cont_type, null);
        TextView containerTypeTextView = view.findViewById(R.id.containerTypeTextView);
        containerTypeTextView.setText(containerTypeArray[i]);
        return view;
    }
}
