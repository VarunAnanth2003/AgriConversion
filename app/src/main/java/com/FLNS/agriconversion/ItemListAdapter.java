package com.FLNS.agriconversion;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ItemListAdapter extends BaseAdapter {

    private Context context;
    private JSONArray allItems;

    public ItemListAdapter(Context context, JSONArray allItems) {
        this.context = context;
        this.allItems = allItems;
    }

    @Override
    public int getCount() {
        return allItems.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return allItems.get(i);
        } catch (JSONException e) {
            return new Object();
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).
                    inflate(R.layout.listview_item, viewGroup, false);
        }
        TextView name = view.findViewById(R.id.listview_name);
        TextView info = view.findViewById(R.id.listview_info);
        try {
            JSONObject currentItem = (JSONObject) allItems.get(i);
            name.setText(currentItem.getString("name"));
            name.setText(name.getText().toString());
            info.setText(" [₹" + currentItem.getDouble("costPerUnit"));
            switch (currentItem.getInt("weightType")) {
                case 0:
                    info.setText(info.getText().toString() + " per kg]");
                    break;
                case 1:
                    info.setText(info.getText().toString() + " per item]");
                    break;
                case 2:
                    info.setText(info.getText().toString() + " per dozen]");
                    break;
                default:
                    info.setText(info.getText().toString() + " per error]");
                    break;
            }
        } catch (JSONException je) {
            je.printStackTrace();
            Log.d("ERRORS", "Could not extract currentItem");
        }
        return view;
    }
}
