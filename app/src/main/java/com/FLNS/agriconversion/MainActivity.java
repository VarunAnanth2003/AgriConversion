package com.FLNS.agriconversion;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    ListView itemListView;
    JSONArray allItems = new JSONArray();
    JSONArray cartItems = new JSONArray();
    ArrayList<Double> cartAmounts = new ArrayList<>();
    ArrayList<Double> cartCosts = new ArrayList<>();
    ArrayList<Integer> cartIndexes = new ArrayList<>();

    ArrayList<String> a = new ArrayList<>();
    ArrayList<Integer> b = new ArrayList<>();
    ArrayList<Double> c = new ArrayList<>();
    ArrayList<Integer> d = new ArrayList<>();

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
        refreshCart();
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
        itemListView = findViewById(R.id.main_listView);
        try {
            for (int i = 0; i <= cycleNum; i++) {
                if (!stored_data.getString(String.valueOf(i), "").equals("")) {
                    allItems.put(new JSONObject(stored_data.getString(String.valueOf(i), "")));
                }
            }
            ItemListAdapter itemListAdapter = new ItemListAdapter(this, allItems);
            itemListView.setAdapter(itemListAdapter);
        } catch (JSONException je) {
            je.printStackTrace();
            Log.d("ERRORS", "Failed to refreshInfo");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCart();
    }

    private void refreshCart() {
        final SharedPreferences stored_data = getSharedPreferences("CART", MODE_PRIVATE);
        int cycleNum = stored_data.getInt("maxCartIndex", -1);
        cartItems = new JSONArray();
        cartAmounts = new ArrayList<>();
        cartCosts = new ArrayList<>();
        cartIndexes = new ArrayList<>();
        try {
            for (int i = 0; i <= cycleNum; i++) {
                if (!stored_data.getString(String.valueOf(i), "").equals("")) {
                    String[] tokens = stored_data.getString(String.valueOf(i), "").split("!!!");
                    cartItems.put(new JSONObject(tokens[0]));
                    cartAmounts.add(Double.parseDouble(tokens[1]));
                    cartCosts.add(Double.parseDouble(tokens[2]));
                    cartIndexes.add(Integer.parseInt(tokens[3]));
                }
            }
        } catch (JSONException je) {
            je.printStackTrace();
            Log.d("ERRORS", "Failed to refreshInfo");
        }
        TextView itemCount = findViewById(R.id.main_amountInCart);
        itemCount.setText(Html.fromHtml("<b>Items in Cart: <b>" + cartAmounts.size()));
    }

    public void clearCart(View view) {
        final SharedPreferences stored_data = getSharedPreferences("CART", MODE_PRIVATE);
        SharedPreferences.Editor stored_data_editor = stored_data.edit();
        stored_data_editor.clear();
        stored_data_editor.apply();
        refreshCart();
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
            case R.id.menu_cart:
                Intent j = new Intent(this, CartActivity.class);
                startActivity(j);
                finish();
                return true;
            case R.id.menu_refresh:
                setupItems();
                refreshInfo();
                refreshCart();
                checkForData();
                return true;
            default:
                return true;
        }
    }

    private void setupItems() {
        final SharedPreferences stored_data = getSharedPreferences("USER_PREFERENCES", MODE_PRIVATE);
        SharedPreferences.Editor e = stored_data.edit();
        connection();
        JSONArray tempArr = DataHandler.packageData(a, b, c, d);
        for (int i = 0; i < tempArr.length(); i++) {
            try {
                e.putString(String.valueOf(i), tempArr.get(i).toString());
            } catch (JSONException je) {
                je.printStackTrace();
                Log.d("ERRORS", "Could not setupitems");
            }
        }
        if (stored_data.getInt("maxIndex", 0) < tempArr.length() - 1) {
            e.putInt("maxIndex", tempArr.length() - 1);
        }
        e.apply();
        a = new ArrayList<>();
        b = new ArrayList<>();
        c = new ArrayList<>();
        d = new ArrayList<>();
    }

    //Get file here
    private void connection() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference textRef = storageRef.child("AgriPriceInformation.xlsx");
        try {
            final File localFile = File.createTempFile("info", "xlsx");
            textRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    saveData(localFile);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("ERRORS", "Download Failure");
                }
            });
        } catch (IOException ioe) {
            Log.d("ERRORS", "Failed to download file");
        }
    }

    private void saveData(File localFile) {
        try {
            XSSFSheet infoSheet = new XSSFWorkbook(new FileInputStream(localFile)).getSheetAt(0);
            Iterator<Row> rowIterator = infoSheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() != 0) {
                    try {
                        a.add(row.getCell(0).toString().trim());
                        b.add((int) Double.parseDouble(row.getCell(1).toString().trim()));
                        c.add(Double.parseDouble(row.getCell(2).toString().trim()));
                        d.add(row.getRowNum() - 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("ERRORS", "Failed to read excel sheet");
                    }
                }
            }
        } catch (Exception e) {
            Log.d("ERRORS", "saveData failed");
        }
    }
}
