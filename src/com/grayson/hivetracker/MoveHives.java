package com.grayson.hivetracker;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.grayson.hivetracker.database.AppDataSource;
import com.grayson.hivetracker.database.Hive;
import com.grayson.hivetracker.database.Location;

public class MoveHives extends Activity {
	private AppDataSource datasource;

	private TextView mLocationName;
	private ListView mHiveList;

	private List<Hive> mHives;

	/**
	 * new location for the hives
	 */
	private long locationId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.move_hives);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		datasource = AppDataSource.getInstance();
		datasource.open();

		mLocationName = (TextView) findViewById(R.id.textViewNewLocation);
		mHiveList = (ListView) findViewById(R.id.listViewMoveHives);

		loadList();

		locationId = -1; // no location set

		// loadData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.move_hives_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save_move_hives:

			saveItem();

			finish();

			return true;
		case android.R.id.home:

			finish();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onButtonScanHiveClick(View v) {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan();
	}

	public void onButtonSetLocationClick(View v) {
		Intent intent = new Intent(getApplicationContext(), CategoryList.class);

		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setAction(Constants.ACTION_PICK); // .putExtra("action",
													// "select-location");

		startActivityForResult(intent, Constants.REQUEST_CODE_PICK_LOCATION);
	}

	private void loadList() {
		mHives = datasource.getAllHivesBeingMoved();
		mHiveList.setAdapter(new MoveListAdapter(getApplicationContext(), R.id.move_hive_list_label, (ArrayList<Hive>) mHives));

		registerForContextMenu(mHiveList);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.move_hive_list_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.delete_hive_from_move_list:
			Hive hive = (Hive) mHiveList.getAdapter().getItem(info.position);

			datasource.removeHiveFromMoveList(hive);

			ArrayAdapter<Hive> adapter = (ArrayAdapter<Hive>) mHiveList.getAdapter();
			adapter.remove(hive);
			adapter.notifyDataSetChanged();

			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		datasource.open();

		// selected a location
		if (requestCode == Constants.REQUEST_CODE_PICK_LOCATION) {
			if (resultCode == RESULT_OK) {
				long newLocationId = data.getLongExtra("location_id", -1);
				if (newLocationId >= 0) {

					Location location = datasource.getLocation(newLocationId);
					if (location != null) {

						mLocationName.setText(location.getName());

						locationId = location.getId();

					} else {
						mLocationName.setText(String.valueOf(""));
					}
				}
			}
		} else {

			// scanned a code
			IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

			if (scanResult != null && scanResult.getContents() != null) {

				String qrCode = scanResult.getContents();
				Hive hive = datasource.getHive(qrCode);

				if (hive != null) {
					hive.setMoving(true);
					datasource.updateHive(hive);
					loadList();
				} else {
					// create hive
					hive = datasource.createHive(qrCode);
					hive.setMoving(true);
					datasource.updateHive(hive);

					// add hive
					loadList();
				}

			}
		}
	}

	public boolean saveItem() {
		if (locationId >= 0) {
			datasource.updateLocationOnMovingHives(locationId);
		}
		return true;
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

	// ***ListAdapter***
	private class MoveListAdapter extends ArrayAdapter<Hive> { // --CloneChangeRequired
		private ArrayList<Hive> mList; // --CloneChangeRequired
		private Context mContext;

		public MoveListAdapter(Context context, int textViewResourceId, ArrayList<Hive> list) { // --CloneChangeRequired

			super(context, textViewResourceId, list);

			this.mList = list;
			this.mContext = context;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = inflater.inflate(R.layout.move_hive_list_item, parent, false);
			final TextView textView = (TextView) convertView.findViewById(R.id.move_hive_list_label);

			Hive hive = (Hive) mList.get(position);

			if (hive.getLocationId() > 0) {
				textView.setText(hive.getQrCode() + ", " + hive.getLocationName());
			} else {
				textView.setText(hive.getQrCode() + ", [new hive]");
			}
			return convertView;
		}
	}

}
