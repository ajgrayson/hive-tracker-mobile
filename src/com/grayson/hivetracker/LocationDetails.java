package com.grayson.hivetracker;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.grayson.hivetracker.database.AppDataSource;
import com.grayson.hivetracker.database.Location;
import com.grayson.hivetracker.tools.Tools;

public class LocationDetails extends Activity {
	private AppDataSource datasource;

	private LocationManager locationManager;
	private LocationListener locationListener;

	private long existingId;
	private long categoryId;

	EditText textName;
	EditText textMafId;
	EditText textLatitude;
	EditText textLongitude;

	/**
	 * latitude is constantly updated while this activity is open
	 */
	private double curLatitude = 0;

	/**
	 * longitude is constantly updated when this activity is open
	 */
	private double curLongitude = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.location_details);

		textName = (EditText) this.findViewById(R.id.editTextName);
		textMafId = (EditText) this.findViewById(R.id.editTextMafId);
		textLatitude = (EditText) this.findViewById(R.id.editTextLatitude);
		textLongitude = (EditText) this.findViewById(R.id.editTextLongitude);

		disableUpdateLocationButton();

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		datasource = AppDataSource.getInstance();
		datasource.open();

		Intent intent = getIntent();
		existingId = intent.getLongExtra("location_id", -1);
		categoryId = intent.getLongExtra("category_id", -1);

		if (existingId >= 0) {
			loadData(existingId);
		}

		try {
			// Acquire a reference to the system Location Manager
			locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

			// TODO check that location provider is enabled and alert user
			// if its not. possibly disabled in settings. Check out
			// http://developer.android.com/training/basics/location/locationmanager.html

			// Define a listener that responds to location updates
			locationListener = new LocationListener() {
				public void onStatusChanged(String provider, int status, Bundle extras) {
					// enableUpdateLocationButton();
				}

				public void onProviderEnabled(String provider) {
				}

				public void onProviderDisabled(String provider) {
				}

				// as the location changes update fields for other methods to
				// access
				public void onLocationChanged(android.location.Location arg0) {
					curLatitude = arg0.getLatitude();
					curLongitude = arg0.getLongitude();

					if (curLatitude != 0 && curLongitude != 0) {
						enableUpdateLocationButton();
					}
				}
			};

			// Register the listener with the Location Manager to receive
			// location updates. Will be in the field mostly so unlikely to be
			// any network based location available. Using GPS.
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

			// locationManager.requestLocationUpdates(
			// LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

		} catch (Exception e) {
			Tools.logError(this, "Location", "Failed to start location service in LocationDetails.java.", e);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.location_details_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save_location:

			if (!saveItem())
				return false;

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
	 * enable the button for setting location. used to enable it when the
	 * location becomes available.
	 */
	private void enableUpdateLocationButton() {
		Button updateLocButton = (Button) this.findViewById(R.id.updateLocationButton);
		updateLocButton.setEnabled(true);
	}

	private void disableUpdateLocationButton() {
		Button updateLocButton = (Button) this.findViewById(R.id.updateLocationButton);
		updateLocButton.setEnabled(false);
	}

	public void onUpdateLocationButtonClick(View v) {
		// do something when the button is clicked
		textLatitude.setText(String.valueOf(curLatitude));
		textLongitude.setText(String.valueOf(curLongitude));
	}

	public void loadData(long id) {
		Location location = datasource.getLocation(id);

		if (location != null) {
			textName.setText(location.getName());
			categoryId = location.getCategoryId();

			textMafId.setText(String.valueOf(location.getMafId()));
			textLatitude.setText(String.valueOf(location.getLatitude()));
			textLongitude.setText(String.valueOf(location.getLongitude()));
		}
	}

	public boolean saveItem() {
		String locationName = textName.getText().toString();
		
		if (locationName == null || locationName.length() == 0) {
			return false;
		}
		
		int mafId = 0;
		double lat = 0;
		double lng = 0;

		if (textMafId.getText().toString().length() > 0)
			mafId = Integer.parseInt(textMafId.getText().toString());

		if (textLatitude.getText().toString().length() > 0)
			lat = Double.parseDouble(textLatitude.getText().toString());

		if (textLongitude.getText().toString().length() > 0)
			lng = Double.parseDouble(textLongitude.getText().toString());

		if (existingId >= 0){
			if(changesHaveBeenMade()) {
				Location location = new Location();
				location.setId(existingId);
				location.setCategoryId(categoryId);
				location.setName(locationName);
	
				location.setMafId(mafId);
				location.setLatitude(lat);
				location.setLongitude(lng);

				datasource.updateLocation(location);
			}
		} else {
			//if (!datasource.doesLocationAlreadyExist(locationName)) {
				datasource.createLocation(locationName, categoryId, mafId, lat, lng);
			//} else {
			//	Toast.makeText(getApplicationContext(), "A site with that name already exists.", Toast.LENGTH_LONG).show();
			//	return false;
			//}
		}
		
		return true;
	}

	protected boolean changesHaveBeenMade() {
		if (existingId < 0) {
			// this is a new hive so changes have been made
			if (textName.getText().length() > 0 || textMafId.getText().length() > 0 || textLatitude.getText().length() > 0
					|| textLongitude.getText().length() > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			// this is an existing hive so there may not have been changes
			Location location = datasource.getLocation(existingId);
			if (location == null)
				return true;

			if (!textName.getText().toString().equals(location.getName().toString())) {
				Tools.logInfo(this, "Location", "Name has changed");
				return true;
			}
			if (!textMafId.getText().toString().equals(String.valueOf(location.getMafId()))) {
				Tools.logInfo(this, "Location", "MafId has changed.");
				return true;
			}
			if (!textLatitude.getText().toString().equals(String.valueOf(location.getLatitude()))) {
				Tools.logInfo(this, "Location", "Latitude has changed.");
				return true;
			}
			if (!textLongitude.getText().toString().equals(String.valueOf(location.getLongitude()))) {
				Tools.logInfo(this, "Location", "Longitude has changed.");
				return true;
			}
			return false;
		}
	}

	@Override
	protected void onResume() {
		datasource.open();

		// location may not be available now so we should
		// disable this.
		disableUpdateLocationButton();

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

		super.onResume();
	}

	@Override
	protected void onPause() {
		datasource.close();

		locationManager.removeUpdates(locationListener);

		super.onPause();
	}
}
