package com.FLNS.agriconversion;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CartListAdapter extends BaseAdapter {

    private Context context;
    private JSONArray cartItems;
    private ArrayList<Double> cartAmounts;
    private ArrayList<Double> cartCosts;

    public CartListAdapter(Context context, JSONArray cartItems, ArrayList<Double> cartAmounts, ArrayList<Double> cartCosts) {
        this.context = context;
        this.cartItems = cartItems;
        this.cartAmounts = cartAmounts;
        this.cartCosts = cartCosts;
    }

    @Override
    public int getCount() {
        return cartItems.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return cartItems.get(i);
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
                    inflate(R.layout.listview_cart, viewGroup, false);
        }
        TextView name = view.findViewById(R.id.cartlistview_name);
        try {
            JSONObject currentItem = (JSONObject) cartItems.get(i);
            name.setText(currentItem.getString("name"));
            name.setText(name.getText().toString() + " [₹" + currentItem.getDouble("costPerUnit"));
            switch (currentItem.getInt("weightType")) {
                case 0:
                    name.setText(name.getText().toString() + " per kg]");
                    break;
                case 1:
                    name.setText(name.getText().toString() + " per item]");
                    break;
                case 2:
                    name.setText(name.getText().toString() + " per dozen]");
                    break;
                default:
                    name.setText(name.getText().toString() + " per error]");
                    break;
            }
        } catch (JSONException je) {
            je.printStackTrace();
            Log.d("ERRORS", "Could not extract currentItem");
        }
        TextView amount = view.findViewById(R.id.cartlistview_amount);
        amount.setText(Html.fromHtml("<b>Amount: </b>" + cartAmounts.get(i)));
        TextView cost = view.findViewById(R.id.cartlistview_cost);
        cost.setText(Html.fromHtml("<b>Cost: </b>" + cartCosts.get(i)));
        return view;
    }
}
