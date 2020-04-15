package com.FLNS.agriconversion;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Item {
    private String name;
    private int weightType;
    private double costPerUnit;
    private int index;

    public Item(String name, int weightType, double costPerUnit, int index) {
        this.name = name;
        this.weightType = weightType;
        this.costPerUnit = costPerUnit;
        this.index = index;
    }

    @Override
    public String toString() {
        JSONObject ret_val = new JSONObject();
        try {
            ret_val.put("name", name);
            ret_val.put("weightType", weightType);
            ret_val.put("costPerUnit", costPerUnit);
            ret_val.put("index", index);
        } catch (JSONException je) {
            je.printStackTrace();
            Log.d("ERRORS", "Could not toString an Item");
        }
        return ret_val.toString();
    }
}
