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

public class ContainerSizeAdapter extends BaseAdapter {
    Context context;
    String[] containerSizeArray;
    LayoutInflater inflater;

    public ContainerSizeAdapter(Context applicationContext, String[] containerSizeArray) {
        this.context = applicationContext;
        this.containerSizeArray = containerSizeArray;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return containerSizeArray.length;
    }

    @Override
    public Object getItem(int i) {
        return containerSizeArray[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.custom_spinner_cont_size, null);
        TextView containerSizeTextView = view.findViewById(R.id.containerSizeTextView);
        containerSizeTextView.setText(containerSizeArray[i]);
        return view;
    }
}
