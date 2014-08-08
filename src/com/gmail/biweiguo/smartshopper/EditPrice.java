package com.gmail.biweiguo.smartshopper;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditPrice extends Activity {
	
	protected static DbHelper db;
	EditText editName, editStore, editDate, editPrice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_edit_price);
		Bundle extras = getIntent().getExtras();
	    final int id = extras.getInt("selected");
		
		db = DbHelper.getInstance(this);
		
		Item oldItem = new Item();
		oldItem = db.getPurchase(id);;
		
		Button saveButton = (Button)findViewById(R.id.button_save2);
		Button cancelButton = (Button)findViewById(R.id.button_cancel2);
		
		editName = (EditText) findViewById(R.id.textedit_item_name2);
		editName.setText(oldItem.getItemName());
		editName.setTextColor(Color.GRAY);
		
		editStore = (EditText) findViewById(R.id.textedit_store_name2);
		editStore.setText(oldItem.getStore());
		editStore.setTextColor(Color.GRAY);
		
		editDate = (EditText) findViewById(R.id.textedit_deadline2);
		editDate.setText(oldItem.getDateString());
		editDate.setTextColor(Color.GRAY);
		
		editPrice = (EditText) findViewById(R.id.textedit_item_price);
		
	     
        saveButton.setOnClickListener(new OnClickListener() {
           
            public void onClick(View v) {
            	Item newItem = new Item();
            	String name = editName.getText().toString();			
        		String store = editStore.getText().toString();			
        		String dateString = editDate.getText().toString();
        		Date date = Item.parseDate(dateString);
        		String priceStr = editPrice.getText().toString();
        		double price;
        		if(priceStr.equalsIgnoreCase("")) 
        			price = 0;
        		else
        			price = Double.valueOf(priceStr);
        		
        		newItem.setItemName(name);
        		//newItem.setCount(count);
        		newItem.setStore(store);
        		newItem.setDateString(dateString);
        		newItem.setDate(date);
        		newItem.setId(id);
        		newItem.setPrice(price);
        		newItem.setDefault();
        		
        		if (name.equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "enter the item name at least!!",
                            Toast.LENGTH_LONG).show();
                } 
                else {
                    db.updatePurchase(newItem);
                    Log.d("bought", "data updated");
                }
                finish();
               
            }
        });
        
        cancelButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		finish();
        	}
        });
	}
	
	
}
