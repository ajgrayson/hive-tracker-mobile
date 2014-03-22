package com.grayson.hivetracker;

import java.util.Calendar;
import java.util.Date;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.grayson.hivetracker.database.AppDataSource;
import com.grayson.hivetracker.database.LogEntry;

public class LogEntryDetails extends Activity {
	private AppDataSource datasource;

	private EditText mDescription;
	private TextView mDateDisplay;
	// private Button mPickDate;
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

	private long existingId;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logentry_details);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		datasource = AppDataSource.getInstance();
		datasource.open();
		
		Intent intent = getIntent();
		existingId = intent.getLongExtra("logentry_id", -1);
		
		// capture our View elements
		mDescription = (EditText) findViewById(R.id.editTextLogentryDescription);
		mDateDisplay = (TextView) findViewById(R.id.editTextLogentryDateDisplay);
		// mPickDate = (Button) findViewById(R.id.buttonPickDate);
		
		// get the current date
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mHour = c.get(Calendar.HOUR);
		mMinute = c.get(Calendar.MINUTE);
		mSecond = c.get(Calendar.SECOND);
		
		// display the current date (this method is below)
		if (existingId >= 0) {
			loadData(existingId);
		} else {
			updateDisplay();
		}

	}

	public void onButtonPickDateClick(View v) {
		showDialog(DATE_DIALOG_ID);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.logentry_details_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save_logentry:

			if(saveItem()) {
				finish();
			}

			return true;
		case android.R.id.home:

			finish();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// updates the date in the TextView
	private void updateDisplay() {
		mDateDisplay.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(mDay).append("-").append(mMonth + 1).append("-")
				.append(mYear).append(" "));
	}

	/**
	 * This method is called if the user is editing an existing category.
	 * 
	 * @param id
	 *            this is the ID of the category being edited
	 */
	public void loadData(long id) {
		LogEntry entry = datasource.getLogEntry(id);
		
		if (entry != null) {
			mDescription.setText(entry.getDescription());
			Date date = new Date(entry.getDate());
			mYear = date.getYear() + 1900;
			mMonth = date.getMonth();
			mDay = date.getDate();
			
		}

		updateDisplay();
	}

	public boolean saveItem() {
		if (mDescription.length() == 0) {
			return false;
		}
		
		String descString = mDescription.getText().toString();
		Date dateObj = new Date(mYear-1900,mMonth,mDay,mHour,mMinute,mSecond);
		long date = dateObj.getTime();
		
		if (existingId >= 0) {
			LogEntry entry= new LogEntry();
			entry.setId(existingId);
			entry.setDescription(descString);
			entry.setDate(date);
			
			datasource.updateLogEntry(entry);
		} else {
			datasource.createLogEntry(descString, date);
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
}
