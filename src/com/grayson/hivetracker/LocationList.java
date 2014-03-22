package com.grayson.hivetracker;

import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.grayson.hivetracker.database.AppDataSource;
import com.grayson.hivetracker.database.Location;

public class LocationList extends ListActivity {
	private AppDataSource datasource;

	private long categoryId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		datasource = AppDataSource.getInstance();
		datasource.open();

		Intent intent = getIntent();
		categoryId = intent.getLongExtra("category_id", -1);

		if (categoryId >= 0) {
			// load existing location
			loadData(categoryId);
		}
	}

	public void removeLocation(Location location) {
		@SuppressWarnings("unchecked")
		ArrayAdapter<Location> adapter = (ArrayAdapter<Location>) getListAdapter();
		adapter.remove(location);
		adapter.notifyDataSetChanged();
	}

	public void loadData(long categoryId) {
		List<Location> values = datasource.getAllLocationsForCategory(categoryId);

		Intent curIntent = getIntent();

		if (curIntent.getAction() != null && curIntent.getAction().compareTo(Constants.ACTION_PICK) == 0) {
			// do nothing as we don't want to show the WHOLE RUN if in select mode
		} else {
			Location wholeRunLocation = new Location();
			wholeRunLocation.setId(-1);
			wholeRunLocation.setCategoryId(categoryId);
			wholeRunLocation.setName("[ WHOLE RUN ]");
			values.add(0, wholeRunLocation);
		}

		// Use the SimpleCursorAdapter to show the
		// elements in a ListView
		ArrayAdapter<Location> adapter = new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView parentView, View childView, int position, long id) {
				Location loc = (Location) getListAdapter().getItem(position);

				Intent curIntent = getIntent();

				if (curIntent.getAction() != null && curIntent.getAction().compareTo(Constants.ACTION_PICK) == 0) {
					Intent intent = new Intent();
					intent.putExtra("location_id", loc.getId());
					// user wants to select a location

					setResult(RESULT_OK, intent);

					finish();
				} else {
					Intent intent = new Intent(getApplicationContext(), HiveList.class);
					intent.putExtra("location_id", loc.getId());
					
					if(loc.getId() < 0) {
						// then this is the whole run item
						intent.putExtra("category_id", loc.getCategoryId());
					}
					
					startActivity(intent);
				}

			}
		});

		registerForContextMenu(lv);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		AdapterContextMenuInfo adapterInfo = (AdapterContextMenuInfo) menuInfo;
		if(adapterInfo.position == 0) {
			// this is the whole run - we don't want a context menu
			return; 
		} else {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.location_list_context_menu, menu);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		final Location loc = (Location) this.getListAdapter().getItem(info.position);

		switch (item.getItemId()) {
		case R.id.edit_location:

			Intent intent = new Intent(getApplicationContext(), LocationDetails.class);
			intent.putExtra("location_id", loc.getId());
			startActivity(intent);

			return true;
		case R.id.delete_location:

			if (datasource.isLocationEmpty(loc.getId())) {
				new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Delete").setMessage("Are you sure?")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								removeLocation(loc);
								datasource.deleteLocation(loc);
							}
						}).setNegativeButton("No", null).show();
			} else {
				Toast.makeText(this, "Failed: Please remove hives before deleting.", Toast.LENGTH_LONG).show();
			}
			return true;
//		case R.id.select_action_location:
//			Intent intent2 = new Intent(getApplicationContext(), SelectAction.class);
//			intent2.putExtra("location_id", loc.getId());
//			startActivity(intent2);
//
//			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.locations_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_location:

			Intent intent1 = new Intent(this, LocationDetails.class);
			intent1.putExtra("category_id", categoryId);

			startActivity(intent1);
			return true;
		case android.R.id.home:

			finish();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		datasource.open();

		// this is here because without it when returning from
		// creating a new location, calling finish() doesn't
		// call the onCreate method. so we need to reload the
		// list here to pull in the new item.
		// TODO put/pull categoryId from the bundle data rather than intent
		Intent intent = getIntent();
		categoryId = intent.getLongExtra("category_id", -1);

		if (categoryId >= 0) {
			loadData(categoryId);
		}

		super.onResume();
	}

	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}
}
