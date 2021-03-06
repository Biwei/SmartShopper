/*************************************************************
* Copyright (c) 2014 Biwei Guo
* [This program is licensed under the "MIT License"]
* Please see the file COPYING in the source
* distribution of this software for license terms.
**************************************************************/

package com.gmail.biweiguo.smartshopper;

import java.util.ArrayList;
import java.util.List;

import com.gmail.biweiguo.smartshopper.R;
import com.gmail.biweiguo.smartshopper.Item;
import com.gmail.biweiguo.smartshopper.DbHelper;
import com.gmail.biweiguo.smartshopper.AddItem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends CommonActivity {
	
	ToggleButton toggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        /** Reference to the delete button of the layout main.xml */
        Button del = (Button) findViewById(R.id.button_delete);
        Button bought = (Button)findViewById(R.id.button_bought);
        
        /** Prepare database */
        
        db = DbHelper.getInstance(this);
        list = db.getAllItems();
        
        addChoicesOnSpinner();
        
        toggle = (ToggleButton)findViewById(R.id.show_details);
        boolean mode = getPreferences("etatToggle",this);
	    toggle.setChecked(mode);
	    Item.setCartMode(mode);
	    setPreferences("etatToggle", toggle.isChecked(), this);
        
        /** Defining the ArrayAdapter to set items to ListView */
        adapter = new ArrayAdapter<Item>(this, R.layout.my_listview, list);
        ListView listItem = (ListView) findViewById(android.R.id.list);       
 
        /** Setting the event listener for the delete button */
        del.setOnClickListener(listenerDel);

        /** Defining a click event listener for the button "Bought" */
        OnClickListener listenerBought = new OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Getting the checked items from the listview */
                SparseBooleanArray checkedItemPositions = getListView().getCheckedItemPositions();
                int itemCount = getListView().getCount();
                Item selected = new Item ();
 
                for(int i=itemCount-1; i >= 0; i--){
                    if(checkedItemPositions.get(i)){
                    	selected = list.get(i);
                        adapter.remove(selected);
                        db.recordPurchase(selected.getId());
                    }
                }
                checkedItemPositions.clear();
                adapter.notifyDataSetChanged();
                goToBought();
            }
        };
 
 
        /** Setting the event listener for the delete button */
        bought.setOnClickListener(listenerBought);
        
        listItem.setAdapter(adapter);
        
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return super.onCreateOptionsMenu(menu);

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.go_to_bought:
	            goToBought();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }

	}
	
	public void addChoicesOnSpinner() {
		 
		sortBy = (Spinner) findViewById(R.id.spinner_sort);
		List<String> choiceList = new ArrayList<String>();
		choiceList.add("None");
		choiceList.add("Store");
		choiceList.add("Deadline");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, choiceList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sortBy.setAdapter(dataAdapter);
		sortBy.setOnItemSelectedListener(new MyOnItemSelectedListener());
	}

	public void quickAddButtonPressed(View view) {
	    // Do something in response to button
		
		EditText editName = (EditText) findViewById(R.id.text_quick_add);
		String name = editName.getText().toString();
		Item newItem;
			
		if (name.equalsIgnoreCase("")) {
            Toast.makeText(this, "enter the item name at least!!",
                    Toast.LENGTH_LONG).show();
        } 
		else if (db.getItemByName(name) != null) {
        	Toast.makeText(this, "item already exists!" , Toast.LENGTH_LONG).show();
        }
		else {
        	newItem = new Item(name);
        	newItem.setDefault();
            db.addItem(newItem);
            Log.d("my list", "data added");
            editName.setText("");
            adapter.add(newItem);
    		adapter.notifyDataSetChanged();
        }
		
	}
	
	public void addButtonPressed (View view) {
	    // Do something in response to button
		Intent intent = new Intent(this, AddItem.class);
		startActivity(intent);
	}
	
	public void goToBought () {
		
		Intent intent =  new Intent(this, BoughtActivity.class);	
		startActivity(intent);
	}
	
	public void onToggleClicked(View view) {
	    // Is the button now checked?
	    boolean on = ((ToggleButton) view).isChecked();
	    
	    Item.setCartMode(on);
	    
	    adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onStop(){
	    super.onStop();
	    setPreferences("etatToggle", toggle.isChecked(), this);
	}
	
	@Override
	public void onPause(){
	    super.onPause();
	    setPreferences("etatToggle", toggle.isChecked(), this);
	}
	
	@Override
	public void onRestart(){
	    super.onRestart();
	    boolean mode = getPreferences("etatToggle",this);
	    toggle.setChecked(mode);
	    Item.setCartMode(mode);
	}
	
	@Override
	public void onStart(){
	    super.onStart();
	    boolean mode = getPreferences("etatToggle",this);
	    toggle.setChecked(mode);
	    Item.setCartMode(mode);
	}
	

	@Override
    public void onResume(){
		super.onResume();
		boolean mode = getPreferences("etatToggle",this);
	    toggle.setChecked(mode);
	    Item.setCartMode(mode);
        adapter.clear();
        list = db.getAllItems();
        adapter = new ArrayAdapter<Item>(this, R.layout.my_listview, list);
        ListView updatedListTask = (ListView) findViewById(android.R.id.list);
        updatedListTask.setAdapter(adapter);
    }
	
}
