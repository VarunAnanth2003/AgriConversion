package com.FLNS.agriconversion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class ConversionActivity extends Activity {

    EditText amount;
    TextView finalText;
    double costPerUnit;
    JSONObject item;
    double finalCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);
        TextView title = findViewById(R.id.conv_titleText);
        amount = findViewById(R.id.conv_amount);
        amount.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        TextView info = findViewById(R.id.conv_info);
        finalText = findViewById(R.id.conv_final);
        Intent i = getIntent();
        String itemString = i.getStringExtra("item");
        try {
            item = new JSONObject(itemString);
            title.setText(item.getString("name"));
            switch (item.getInt("weightType")) {
                case 0:
                    amount.setHint("Weight in kg");
                    info.setText(Html.fromHtml("<b>Cost per kg: </b>"));
                    break;
                case 1:
                    amount.setHint("Number of items");
                    info.setText(Html.fromHtml("<b>Cost per item: </b>"));
                    break;
                case 2:
                    amount.setHint("Number of dozens");
                    info.setText(Html.fromHtml("<b>Cost per dozen: </b>"));
                    break;
            }
            costPerUnit = item.getDouble("costPerUnit");
            info.setText(info.getText().toString() + "₹" + costPerUnit);
            amount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    calculate();
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    calculate();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    calculate();
                }
            });
        } catch (JSONException je) {
            je.printStackTrace();
            Log.d("ERRORS", "Could not populate fields");
        }
    }

    public void calculate() {
        if (!amount.getText().toString().trim().equals("")) {
            DecimalFormat df = new DecimalFormat("#.##");
            try {
                finalCost = Double.parseDouble(df.format(Double.parseDouble(amount.getText().toString().trim()) * costPerUnit));
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                finalCost = 0;
                Log.d("ERRORS", "Failed to convert amount to a double");
            }
            finalText.setText("Final Cost: " + "₹" + finalCost);
        } else {
            finalText.setText("Final Cost: " + "N/A");
        }
    }

    public void back(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInputFromWindow(view.getWindowToken(), 0, 0);
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
        finish();
    }

    public void save(View view) {
        if (!amount.getText().toString().trim().equals("")) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInputFromWindow(view.getWindowToken(), 0, 0);
            final SharedPreferences stored_data = getSharedPreferences("CART", MODE_PRIVATE);
            SharedPreferences.Editor stored_data_editor = stored_data.edit();
            try {
                int newTag = stored_data.getInt("maxCartIndex", -1) + 1;
                stored_data_editor.putString(String.valueOf(newTag),
                        item.toString()
                                + "!!!" +
                                Double.parseDouble(amount.getText().toString())
                                + "!!!" +
                                finalCost
                                + "!!!" +
                                newTag);
                stored_data_editor.putInt("maxCartIndex", newTag);
                stored_data_editor.apply();
                Intent i = new Intent(this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
                finish();
            } catch (NumberFormatException ne) {
                ne.printStackTrace();
                Toast alertToast = Toast.makeText(this, "Please input a number", Toast.LENGTH_SHORT);
                alertToast.show();
            }
        } else {
            Toast alertToast = Toast.makeText(this, "Please input a number", Toast.LENGTH_SHORT);
            alertToast.show();
        }
    }
}
