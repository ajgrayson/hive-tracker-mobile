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

import com.grayson.hivetracker.database.AppDataSource;
import com.grayson.hivetracker.database.LogEntry;

public class LogList extends ListActivity {
	private AppDataSource datasource;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		datasource = AppDataSource.getInstance();
		datasource.open();

		loadData();
	}

	public void removeLogEntry(LogEntry logentry) {
		@SuppressWarnings("unchecked")
		ArrayAdapter<LogEntry> adapter = (ArrayAdapter<LogEntry>) getListAdapter();
		adapter.remove(logentry);
		adapter.notifyDataSetChanged();
	}

	public void loadData() {
		List<LogEntry> values = datasource.getAllLogEntries();

		// Use the SimpleCursorAdapter to show the
		// elements in a ListView
		ArrayAdapter<LogEntry> adapter = new ArrayAdapter<LogEntry>(this, R.layout.logentry_list_item, values);
		setListAdapter(adapter);

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView parentView, View childView, int position, long id) {
				LogEntry loc = (LogEntry) getListAdapter().getItem(position);

				Intent intent = new Intent(getApplicationContext(), LogEntryDetails.class);
				intent.putExtra("logentry_id", loc.getId());

				startActivity(intent);

			}
		});

		registerForContextMenu(lv);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.logentry_list_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.edit_logentry:

			LogEntry loc1 = (LogEntry) this.getListAdapter().getItem(info.position);

			Intent intent = new Intent(getApplicationContext(), LogEntryDetails.class);

			intent.putExtra("logentry_id", loc1.getId());

			startActivity(intent);

			return true;
		case R.id.delete_logentry:
			final LogEntry loc2 = (LogEntry) this.getListAdapter().getItem(info.position);

			new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Delete").setMessage("Are you sure?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							removeLogEntry(loc2);
							datasource.deleteLogEntry(loc2);
						}
					}).setNegativeButton("No", null).show();

			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.logentry_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_logentry:

			Intent intent1 = new Intent(this, LogEntryDetails.class);

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

		loadData();

		super.onResume();
	}

	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}
}
