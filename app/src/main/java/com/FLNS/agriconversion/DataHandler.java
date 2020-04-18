package com.FLNS.agriconversion;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DataHandler {
    public static JSONArray packageData(ArrayList<String> names, ArrayList<Integer> weightTypes, ArrayList<Double> costPerUnits, ArrayList<Integer> index) {
        JSONArray itemsArray = new JSONArray();
        for (int i = 0; i < names.size(); i++) {
            JSONObject tempObj = new JSONObject();
            try {
                tempObj.put("name", names.get(i));
                tempObj.put("weightType", weightTypes.get(i));
                tempObj.put("costPerUnit", costPerUnits.get(i));
                tempObj.put("index", index.get(i));
            } catch (JSONException je) {
                je.printStackTrace();
                Log.d("ERRORS", "Error in packageData");
            }
            itemsArray.put(tempObj);
        }
        return itemsArray;
    }
}
