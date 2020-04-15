package com.FLNS.agriconversion;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    ListView cartListView;
    JSONArray cartItems = new JSONArray();
    ArrayList<Double> cartAmounts = new ArrayList<>();
    ArrayList<Double> cartCosts = new ArrayList<>();
    ArrayList<Integer> cartIndexes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar myToolbar = findViewById(R.id.cartToolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);
        refreshCart();
        checkForData();
        final SharedPreferences stored_data = getSharedPreferences("CART", MODE_PRIVATE);
        cartListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int pos, long l) {
                AlertDialog deleteDialog = new AlertDialog.Builder(CartActivity.this)
                        .setTitle("Delete this item?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences.Editor stored_data_editor = stored_data.edit();
                                stored_data_editor.putString(String.valueOf(cartIndexes.get(pos)), "");
                                stored_data_editor.apply();
                                cartItems.remove(pos);
                                refreshCart();
                                checkForData();
                            }
                        })
                        .setNegativeButton("Keep", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .create();
                deleteDialog.show();
                return true;
            }
        });

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
        cartListView = findViewById(R.id.cart_listView);
        CartListAdapter cartListAdapter = new CartListAdapter(this, cartItems, cartAmounts, cartCosts);
        cartListView.setAdapter(cartListAdapter);

        TextView finalCostText = findViewById(R.id.cart_finalCost);
        double finalCost = 0;
        for (int i = 0; i < cartCosts.size(); i++) {
            finalCost += cartCosts.get(i);
        }
        finalCostText.setText(Html.fromHtml("<b>Final Cost: </b>" + "₹" + finalCost));
    }

    public void clearCart(View view) {
        final SharedPreferences stored_data = getSharedPreferences("CART", MODE_PRIVATE);
        SharedPreferences.Editor stored_data_editor = stored_data.edit();
        stored_data_editor.clear();
        stored_data_editor.apply();
        refreshCart();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void checkForData() {
        final TextView noDataText = findViewById(R.id.cart_emptyListText);
        if (cartListView.getAdapter().getCount() > 0) {
            noDataText.setVisibility(View.INVISIBLE);
        } else {
            noDataText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cartMenu_home:
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                finish();
                return true;
            default:
                return true;
        }
    }
}
