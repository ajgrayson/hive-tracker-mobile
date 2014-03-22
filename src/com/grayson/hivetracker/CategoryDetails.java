package com.grayson.hivetracker;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.grayson.hivetracker.database.AppDataSource;
import com.grayson.hivetracker.database.Category;
import com.grayson.hivetracker.tools.Tools;

public class CategoryDetails extends Activity {
	private AppDataSource datasource;	
	
	private long existingId;
	
	EditText editTextName;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_details);
        
        editTextName =  (EditText)this.findViewById(R.id.editTextName);
        
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        datasource = AppDataSource.getInstance();	
        datasource.open();
        
		Intent intent = getIntent();
		existingId = intent.getLongExtra("category_id", -1);
		
		if (existingId >= 0){
			// load existing category
			loadData(existingId);
		}
		
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.category_details_menu, menu);
	    
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.save_category:
	        	
	        	if(!saveItem()) {
	        		return false;
	        	}
	        	
	        	finish();
	        	
	            return true;
	        case android.R.id.home:
	        	
	        	if (changesHaveBeenMade()) {
					new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("You've made changes")
							.setMessage("Save your changes?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									saveItem();
									finish();
								}
							}).setNegativeButton("No", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									finish();
								}
							}).show();
				} else {
					finish();
				}
	        	
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
	 * This method is called if the user is editing an existing category.
	 * @param id this is the ID of the category being edited
	 */
	public void loadData(long id) {
		Category category = datasource.getCategory(id);
		
		if (category != null) {
			EditText text = (EditText)this.findViewById(R.id.editTextName);
			text.setText(category.getName());
		}
	}
	
	public boolean saveItem() {
		String categoryName = editTextName.getText().toString();
		
		if (categoryName == null || categoryName.length() == 0) {
			return false;
		}
		
		if (existingId >= 0){
			if(changesHaveBeenMade()) {
				Category category = new Category();
				category.setId(existingId);
				category.setName(categoryName);
				
				datasource.updateCategory(category);
			}
		}
		else {
			//if (!datasource.doesCategoryAlreadyExist(categoryName)) {
				datasource.createCategory(categoryName);
			//} else {
			//	Toast.makeText(getApplicationContext(), "A run with that name already exists.", Toast.LENGTH_LONG).show();
			//	return false;
			//}
		}
		
		return true;
	}
	
	protected boolean changesHaveBeenMade() {
		if (existingId < 0) {
			// this is a new hive so changes have been made
			if(editTextName.getText().length() > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			// this is an existing hive so there may not have been changes
			Category category = datasource.getCategory(existingId);
			if(category == null)
				return true;
			
			if (!editTextName.getText().toString().equals(category.getName().toString())) {
				Tools.logInfo(this, "Category", "Name has changed.");
				return true;
			}
			return false;
		}
	}
	
	@Override
	protected void onResume() {
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}
}
