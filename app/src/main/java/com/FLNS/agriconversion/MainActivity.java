package com.FLNS.agriconversion;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    ListView itemListView;
    JSONArray allItems = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInputFromWindow(myToolbar.getWindowToken(), 0, 0);
        setupItems();
        refreshInfo();
        checkForData();
        final SharedPreferences stored_data = getSharedPreferences("USER_PREFERENCES", MODE_PRIVATE);
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                try {
                    Intent i = new Intent(getApplicationContext(), ConversionActivity.class);
                    i.putExtra("item", allItems.get(pos).toString());
                    startActivity(i);
                } catch (JSONException je) {
                    je.printStackTrace();
                    Log.d("ERRORS", "Failed to get item from allItems");
                }
            }
        });
        itemListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int pos, long l) {
                AlertDialog deleteDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Edit/Delete this item?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    SharedPreferences.Editor stored_data_editor = stored_data.edit();
                                    JSONObject currentObject = (JSONObject) allItems.get(pos);
                                    stored_data_editor.putString(String.valueOf(currentObject.get("index")), "");
                                    stored_data_editor.apply();
                                    allItems.remove(pos);
                                    refreshInfo();
                                    checkForData();
                                } catch (JSONException je) {
                                    je.printStackTrace();
                                    Log.d("ERRORS", "Failed during deletion");
                                }
                            }
                        })
                        .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    SharedPreferences.Editor stored_data_editor = stored_data.edit();
                                    JSONObject currentObject = (JSONObject) allItems.get(pos);
                                    stored_data_editor.putString(String.valueOf(currentObject.get("index")), "");
                                    stored_data_editor.apply();
                                    String prevItem = allItems.get(pos).toString();
                                    allItems.remove(pos);
                                    refreshInfo();
                                    Intent editIntent = new Intent(getApplicationContext(), AddNewActivity.class);
                                    editIntent.putExtra("item", prevItem);
                                    startActivity(editIntent);
                                } catch (JSONException je) {
                                    je.printStackTrace();
                                    Log.d("ERRORS", "Failed during editing");
                                }
                            }
                        })
                        .create();
                deleteDialog.show();
                return true;
            }
        });
    }

    private void refreshInfo() {
        final SharedPreferences stored_data = getSharedPreferences("USER_PREFERENCES", MODE_PRIVATE);

        /*SharedPreferences.Editor stored_data_editor = stored_data.edit();
        stored_data_editor.clear();
        stored_data_editor.apply();*/

        int cycleNum = stored_data.getInt("maxIndex", -1);
        allItems = new JSONArray();
        try {
            for (int i = 0; i <= cycleNum; i++) {
                if (!stored_data.getString(String.valueOf(i), "").equals("")) {
                    allItems.put(new JSONObject(stored_data.getString(String.valueOf(i), "")));
                }
            }
            ItemListAdapter itemListAdapter = new ItemListAdapter(this, allItems);
            itemListView = findViewById(R.id.main_listView);
            itemListView.setAdapter(itemListAdapter);

        } catch (JSONException je) {
            je.printStackTrace();
            Log.d("ERRORS", "Failed to refreshInfo");
        }
    }

    private void checkForData() {
        final TextView noDataText = findViewById(R.id.main_emptyListText);
        if (itemListView.getAdapter().getCount() > 0) {
            noDataText.setVisibility(View.INVISIBLE);
        } else {
            noDataText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_addNew:
                Intent i = new Intent(this, AddNewActivity.class);
                startActivity(i);
                finish();
                return true;
            default:
                return true;
        }
    }

    private void setupItems() {
        final SharedPreferences stored_data = getSharedPreferences("USER_PREFERENCES", MODE_PRIVATE);
        SharedPreferences.Editor e = stored_data.edit();
        String[] tempArr = {
                "{\"name\":\"Onion\",\"weightType\":0,\"costPerUnit\":30,\"index\":0}",
                "{\"name\":\"Potato\",\"weightType\":0,\"costPerUnit\":35,\"index\":1}",
                "{\"name\":\"Ridge Gourd (Beerakaya)\",\"weightType\":0,\"costPerUnit\":35,\"index\":2}",
                "{\"name\":\"Bitter Gourd (Kakarakaya)\",\"weightType\":0,\"costPerUnit\":30,\"index\":3}",
                "{\"name\":\"Okra\",\"weightType\":0,\"costPerUnit\":30,\"index\":4}",
                "{\"name\":\"Carrot\",\"weightType\":0,\"costPerUnit\":30,\"index\":5}",
                "{\"name\":\"Capsicum\",\"weightType\":0,\"costPerUnit\":30,\"index\":6}",
                "{\"name\":\"Beans (Local/French)\",\"weightType\":0,\"costPerUnit\":40,\"index\":7}",
                "{\"name\":\"Chikkudi (Broad Beans)\",\"weightType\":0,\"costPerUnit\":35,\"index\":8}",
                "{\"name\":\"Brinjal\",\"weightType\":0,\"costPerUnit\":25,\"index\":9}",
                "{\"name\":\"Cucumber (Kheera)\",\"weightType\":0,\"costPerUnit\":20,\"index\":10}",
                "{\"name\":\"Cucumber (English)\",\"weightType\":0,\"costPerUnit\":25,\"index\":11}",
                "{\"name\":\"Cauliflower\",\"weightType\":0,\"costPerUnit\":20,\"index\":12}",
                "{\"name\":\"Chili\",\"weightType\":0,\"costPerUnit\":20,\"index\":13}",
                "{\"name\":\"Tindora (Dondakaya)\",\"weightType\":0,\"costPerUnit\":25,\"index\":14}",
                "{\"name\":\"Bottle Gourd\",\"weightType\":1,\"costPerUnit\":20,\"index\":15}",
                "{\"name\":\"Drumstick\",\"weightType\":1,\"costPerUnit\":2.5,\"index\":16}",
                "{\"name\":\"Beetroot\",\"weightType\":0,\"costPerUnit\":20,\"index\":17}",
                "{\"name\":\"Cabbage\",\"weightType\":0,\"costPerUnit\":15,\"index\":18}",
                "{\"name\":\"Tomato\",\"weightType\":0,\"costPerUnit\":10,\"index\":19}",
                "{\"name\":\"Dosakaya\",\"weightType\":0,\"costPerUnit\":20,\"index\":20}",
                "{\"name\":\"Coriander Leaf\",\"weightType\":0,\"costPerUnit\":50,\"index\":21}",
                "{\"name\":\"Curry Leaf\",\"weightType\":0,\"costPerUnit\":50,\"index\":22}",
                "{\"name\":\"Mint Leaf\",\"weightType\":0,\"costPerUnit\":50,\"index\":23}",
                "{\"name\":\"Watermelon Kiran\",\"weightType\":1,\"costPerUnit\":50,\"index\":24}",
                "{\"name\":\"Papaya\",\"weightType\":1,\"costPerUnit\":50,\"index\":25}",
                "{\"name\":\"Musk Melon\",\"weightType\":1,\"costPerUnit\":50,\"index\":26}",
                "{\"name\":\"Banana Robusta\",\"weightType\":2,\"costPerUnit\":40,\"index\":27}",
                "{\"name\":\"Orange Nagpur\",\"weightType\":0,\"costPerUnit\":70,\"index\":28}"
        };
        for (int i = 0; i < tempArr.length; i++) {
            e.putString(String.valueOf(i), tempArr[i]);
        }
        e.putInt("maxIndex", tempArr.length - 1);
        e.apply();
    }
}
