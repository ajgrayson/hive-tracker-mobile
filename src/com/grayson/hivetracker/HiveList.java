package com.grayson.hivetracker;

import java.util.List;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.grayson.hivetracker.database.AppDataSource;
import com.grayson.hivetracker.database.Hive;

public class HiveList extends ListActivity {
	private AppDataSource datasource;

	private long locationId;

	private long categoryId;

	private Menu actionBarMenu;
	
	private boolean hasHives = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		datasource = AppDataSource.getInstance();
		datasource.open();

		Intent intent = getIntent();
		locationId = intent.getLongExtra("location_id", -1);
		categoryId = intent.getLongExtra("category_id", -1);

		if (locationId >= 0 || categoryId > 0) {
			// load existing location
			loadData();
		}
	}

	public void loadData() {
		List<Hive> values = null;

		if (locationId > 0) {
			values = datasource.getAllHivesForLocation(locationId);
		} else if (categoryId > 0) {
			values = datasource.getAllHivesForCategory(categoryId);
		}

		if (actionBarMenu != null) {
			MenuItem item = actionBarMenu.getItem(0);
			if (values.size() == 0) {
				item.setEnabled(false); 
			} else {
				item.setEnabled(true);
			}
		}
		
		if(values != null && values.size() > 0) {
			hasHives = true;
		}

		// Use the SimpleCursorAdapter to show the
		// elements in a ListView
		ArrayAdapter<Hive> adapter = new ArrayAdapter<Hive>(this, android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView parentView, View childView, int position, long id) {
				Hive hive = (Hive) getListAdapter().getItem(position);

				Intent intent = new Intent(getApplicationContext(), HiveDetails.class);
				intent.putExtra("qr_code", hive.getQrCode());

				startActivity(intent);

			}
		});
		registerForContextMenu(lv);
	}

	public void removeHive(Hive hive) {
		@SuppressWarnings("unchecked")
		ArrayAdapter<Hive> adapter = (ArrayAdapter<Hive>) getListAdapter();
		adapter.remove(hive);
		adapter.notifyDataSetChanged();
	}

	// @Override
	// public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo
	// menuInfo) {
	// super.onCreateContextMenu(menu, v, menuInfo);
	// MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.menu.hive_list_context_menu, menu);
	// }
	//
	// @Override
	// public boolean onContextItemSelected(MenuItem item) {
	// AdapterContextMenuInfo info = (AdapterContextMenuInfo)
	// item.getMenuInfo();
	// switch (item.getItemId()) {
	// case R.id.delete_hive:
	// final Hive hive = (Hive) this.getListAdapter().getItem(info.position);
	//
	// new
	// AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Delete").setMessage("Are you sure?")
	// .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// removeHive(hive);
	// datasource.deleteHive(hive);
	// }
	// }).setNegativeButton("No", null).show();
	//
	// return true;
	// default:
	// return super.onContextItemSelected(item);
	// }
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.hive_list_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean result = super.onPrepareOptionsMenu(menu);
		
		actionBarMenu = menu;
		if(!hasHives) {
			MenuItem item = menu.getItem(0);
			if(item != null) {
				item.setEnabled(false);
			}
		}
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

			finish();

			return true;
		case R.id.hive_list_select_action:

			Intent intent2 = new Intent(getApplicationContext(), SelectAction.class);
			intent2.putExtra("location_id", locationId);
			intent2.putExtra("category_id", categoryId);
			startActivity(intent2);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		datasource.open();
		loadData();
		super.onResume();
	}

	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}
}
