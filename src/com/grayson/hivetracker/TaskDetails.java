package com.grayson.hivetracker;

import java.util.Calendar;
import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.grayson.hivetracker.database.AppDataSource;
import com.grayson.hivetracker.database.Hive;
import com.grayson.hivetracker.database.Location;
import com.grayson.hivetracker.database.Task;
import com.grayson.hivetracker.tools.Tools;

public class TaskDetails extends Activity {
	private AppDataSource datasource;

	private EditText mDescription;
	private TextView mHiveId;
	private TextView mSite;
	private CheckBox mDone;
	private Date mDate;
	private long hiveId;

	private long existingId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_details);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		datasource = AppDataSource.getInstance();
		datasource.open();

		Intent intent = getIntent();
		existingId = intent.getLongExtra("task_id", -1);
		hiveId = intent.getLongExtra("hive_id", -1);

		mDescription = (EditText) findViewById(R.id.editTextTaskDescription);
		mHiveId = (TextView) findViewById(R.id.taskDetailsHiveId);
		mSite = (TextView) findViewById(R.id.taskDetailsSite);
		mDone = (CheckBox) findViewById(R.id.checkBoxTaskDone);

		loadData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.task_details_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save_task:

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

	/**
	 * This method is called if the user is editing an existing category.
	 * 
	 * @param id
	 *            this is the ID of the category being edited
	 */
	public void loadData() {
		Task task = null;
		if (existingId >= 0)
			task = datasource.getTask(existingId);

		if (task != null) {
			mDescription.setText(task.getDescription());
			mDate = new Date(task.getDate());
			//mHiveId.setText(task.getHiveId() + "");
			// mHiveId = task.getHiveId();
			if (task.getDone()) {
				mDone.setChecked(true);
			}

			Hive hive = datasource.getHiveById(task.getHiveId());
			if (hive != null) {
				mHiveId.setText(hive.getQrCode());
				
				Location loc = datasource.getLocation(hive.getLocationId());
				if (loc != null) {
					mSite.setText(Tools.tidyString(loc.getName(), 25));
				} else {
					mSite.setText("N/A");
				}
			} else {
				mHiveId.setText("N/A");
				mSite.setText("N/A");
			}
		} else {
			final Calendar c = Calendar.getInstance();

			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			int hour = c.get(Calendar.HOUR);
			int minute = c.get(Calendar.MINUTE);
			int second = c.get(Calendar.SECOND);

			// mHiveId = -1;
			mDate = new Date(year, month, day, hour, minute, second);
			//mHiveId.setText(hiveId + "");

			// can't set new task to done before saving
			mDone.setEnabled(false);
			
			Hive hive = datasource.getHiveById(hiveId);
			if (hive != null) {
				mHiveId.setText(hive.getQrCode());
				
				Location loc = datasource.getLocation(hive.getLocationId());
				if (loc != null) {
					mSite.setText(Tools.tidyString(loc.getName(), 25));
				} else {
					mSite.setText("N/A");
				}
			} else {
				mHiveId.setText("N/A");
				mSite.setText("N/A");
			}
		}
	}

	public boolean saveItem() {
		if (mDescription.length() == 0) {
			return false;
		}

		String descString = mDescription.getText().toString();
		long date = mDate.getTime();

		if (existingId >= 0) {
			Task task = datasource.getTask(existingId);
			task.setDescription(descString.trim());
			task.setDate(date);
			task.setDone(mDone.isChecked());

			datasource.updateTask(task);
		} else {
			datasource.createTask(descString, date, hiveId);
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
