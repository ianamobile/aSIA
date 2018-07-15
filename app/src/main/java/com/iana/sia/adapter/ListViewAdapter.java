package com.iana.sia.adapter;

/**
 * Created by Saumil on 7/15/2018.
 */
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.iana.sia.R;
import com.iana.sia.model.UIIAExhibit;

public class ListViewAdapter extends ArrayAdapter<UIIAExhibit> {
    // Declare Variables
    Context context;
    LayoutInflater inflater;
    List<UIIAExhibit> uiiaExhibitList;
    private SparseBooleanArray mSelectedItemsIds;

    public ListViewAdapter(Context context, int resourceId,
                           List<UIIAExhibit> uiiaExhibitList) {
        super(context, resourceId, uiiaExhibitList);
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.uiiaExhibitList = uiiaExhibitList;
        inflater = LayoutInflater.from(context);
    }

    private class ViewHolder {
        TextView item;
    }

    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.uiia_exhibit_list_view, null);
            // Locate the TextViews in listview_item.xml
            holder.item = view.findViewById(R.id.item);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Capture position and set to the TextViews
        holder.item.setText(uiiaExhibitList.get(position).getItem());
        return view;
    }

    @Override
    public void remove(UIIAExhibit object) {
        uiiaExhibitList.remove(object);
        notifyDataSetChanged();
    }

    public List<UIIAExhibit> getUiiaExhibit() {
        return uiiaExhibitList;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}