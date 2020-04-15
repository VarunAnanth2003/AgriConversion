package com.FLNS.agriconversion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class AddNewActivity extends AppCompatActivity {

    private boolean isInEdit;
    private JSONObject toEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        Intent i = getIntent();
        EditText nameText = findViewById(R.id.addNew_itemName);
        nameText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        Spinner typeSpinner = findViewById(R.id.addNew_typeSpinner);
        EditText costPerUnitText = findViewById(R.id.addNew_costPerUnit);
        if (i.hasExtra("item")) {
            isInEdit = true;
            try {
                toEdit = new JSONObject(i.getStringExtra("item"));
                if (isInEdit) {
                    nameText.setText(toEdit.getString("name"));
                    typeSpinner.setSelection(toEdit.getInt("weightType") + 1);
                    costPerUnitText.setText(String.valueOf(toEdit.getDouble("costPerUnit")));
                }
            } catch (JSONException je) {
                je.printStackTrace();
                Log.d("ERRORS", "Intent extra extraction failed in Add New");
            }
        }
    }

    public void submitData(View view) {
        EditText nameText = findViewById(R.id.addNew_itemName);
        Spinner typeSpinner = findViewById(R.id.addNew_typeSpinner);
        EditText costPerUnitText = findViewById(R.id.addNew_costPerUnit);
        String name = nameText.getText().toString().trim();
        int weightType;
        switch (typeSpinner.getSelectedItem().toString()) {
            case "Per kg":
                weightType = 0;
                break;
            case "Per item":
                weightType = 1;
                break;
            case "Per dozen":
                weightType = 2;
                break;
            default:
                weightType = -1;
                break;
        }
        double costPerUnit = 0;
        try {
            costPerUnit = Double.parseDouble(costPerUnitText.getText().toString().trim());
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            Log.d("ERRORS", "Failed to convert costPerUnit to a double");
        }
        if (!name.equals("") && weightType != -1 && costPerUnit != 0) {
            SharedPreferences stored_data = getSharedPreferences("USER_PREFERENCES", MODE_PRIVATE);
            SharedPreferences.Editor stored_data_editor = stored_data.edit();
            int newTag = stored_data.getInt("maxIndex", -1) + 1;
            Item newItem = new Item(name, weightType, costPerUnit, newTag);
            stored_data_editor.putString(String.valueOf(newTag), newItem.toString());
            stored_data_editor.putInt("maxIndex", newTag);
            stored_data_editor.apply();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInputFromWindow(view.getWindowToken(), 0, 0);
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            Toast alertToast = Toast.makeText(this, "Please give input to all required fields", Toast.LENGTH_SHORT);
            alertToast.show();
        }
    }

    public void exit(View view) {
        if (isInEdit) {
            SharedPreferences stored_data = getSharedPreferences("USER_PREFERENCES", MODE_PRIVATE);
            SharedPreferences.Editor stored_data_editor = stored_data.edit();
            try {
                stored_data_editor.putString(toEdit.getString("index"), toEdit.toString());
            } catch (JSONException je) {
                je.printStackTrace();
                Log.d("ERRORS", "Could not exit");
            }
            stored_data_editor.apply();
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInputFromWindow(view.getWindowToken(), 0, 0);
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
