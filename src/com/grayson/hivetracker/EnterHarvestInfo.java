package com.grayson.hivetracker;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grayson.hivetracker.database.AppDataSource;
import com.grayson.hivetracker.database.AppSQLiteHelper;
import com.grayson.hivetracker.database.ListValue;

public class EnterHarvestInfo extends Activity {
	private AppDataSource datasource;

	private TextView textViewHarvestDate;

	private EditText editTextHarvestSupers;

	private CheckBox checkBoxHarvestDisease;

	private Spinner spinnerFloralType;

	//private long locationId;

	//private long categoryId;

	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;
	private int mSecond;
	
	static final int DATE_DIALOG_ID = 0;
	
	// the callback received when the user "sets" the date in the dialog
		private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				mYear = year;
				mMonth = monthOfYear;
				mDay = dayOfMonth;
				
				final Calendar c = Calendar.getInstance();
				mHour = c.get(Calendar.HOUR);
				mMinute = c.get(Calendar.MINUTE);
				mSecond = c.get(Calendar.SECOND);
				
				updateDisplay();
			}
		};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_harvest_info);

		datasource = AppDataSource.getInstance();
		datasource.open();

		textViewHarvestDate = (TextView) this.findViewById(R.id.textViewHarvestDate);
		editTextHarvestSupers = (EditText) this.findViewById(R.id.editTextHarvestSupers);
		checkBoxHarvestDisease = (CheckBox) this.findViewById(R.id.checkBoxHarvestDisease);
		spinnerFloralType = (Spinner) this.findViewById(R.id.spinnerHarvestFloralType);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		//Intent intent = getIntent();
		//locationId = intent.getLongExtra("location_id", -1);
		//categoryId = intent.getLongExtra("category_id", -1);

		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mHour = c.get(Calendar.HOUR);
		mMinute = c.get(Calendar.MINUTE);
		mSecond = c.get(Calendar.SECOND);
		
		loadData();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.enter_harvest_info_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

			finish();

			return true;
		case R.id.enter_harvest_info_done:

			save();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void loadData() {
		updateDisplay();
		
		List<ListValue> treatmentListValues = datasource.getAllValues(AppSQLiteHelper.TABLE_LIST_FLORAL_TYPE);
		ArrayAdapter<ListValue> adapterTreatment = new ArrayAdapter<ListValue>(this, android.R.layout.simple_spinner_item, treatmentListValues);
		adapterTreatment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerFloralType.setAdapter(adapterTreatment);
		spinnerFloralType.setSelection(0);
	}

	public boolean save() {
		if(editTextHarvestSupers.length() == 0)
			return false;
		
		Date dateObj = new Date(mYear-1900,mMonth,mDay,mHour,mMinute,mSecond);
		long date = dateObj.getTime();
		
		long supers = Long.valueOf(editTextHarvestSupers.getText().toString());
		
		boolean disease = checkBoxHarvestDisease.isChecked();
		
		ListValue value = (ListValue) spinnerFloralType.getSelectedItem();
		long floralTypeId = value.getId();
		
		Intent intent = new Intent();
		intent.putExtra("harvest_date", date);
		intent.putExtra("disease", disease);
		intent.putExtra("supers", supers);
		intent.putExtra("floral_type_id", floralTypeId);

		setResult(RESULT_OK, intent);

		finish();

		return true;
	}
	
	private void updateDisplay() {
		textViewHarvestDate.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(mDay).append("-").append(mMonth + 1).append("-")
				.append(mYear).append(" "));
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
	}

	public void onButtonPickHarvestDateClick(View v) {
		showDialog(DATE_DIALOG_ID);
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