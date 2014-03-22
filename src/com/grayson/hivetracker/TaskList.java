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
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import com.grayson.hivetracker.database.AppDataSource;
import com.grayson.hivetracker.database.Task;

public class TaskList extends ListActivity {

	private AppDataSource datasource;

	private long hiveId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		datasource = AppDataSource.getInstance();
		datasource.open();

		Intent intent = getIntent();
		hiveId = intent.getLongExtra("hive_id", -1);

		loadData();
	}

	public void removeTask(Task task) {
		TaskListArrayAdapter adapter = (TaskListArrayAdapter) getListAdapter();
		adapter.remove(task);
		adapter.notifyDataSetChanged();
	}

	public void loadData() {
		List<Task> values;
		if (hiveId >= 0) {
			values = datasource.getAllTasksForHive(hiveId);
			setListAdapter(new TaskListArrayAdapter(this, R.layout.task_list_item, values));
		} else {
			values = datasource.getAllTasksBySite();
			setListAdapter(new TaskListArrayAdapter(this, R.layout.all_tasks_list_item, values));
		}

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		registerForContextMenu(lv);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		Task task = (Task) getListAdapter().getItem(position);

		Intent intent = new Intent(getApplicationContext(), TaskDetails.class);
		intent.putExtra("task_id", task.getId());

		startActivity(intent);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.task_list_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.edit_task:

			Task task = (Task) this.getListAdapter().getItem(info.position);

			Intent intent = new Intent(getApplicationContext(), TaskDetails.class);

			intent.putExtra("task_id", task.getId());

			startActivity(intent);

			return true;
		case R.id.delete_task:
			final Task task2 = (Task) this.getListAdapter().getItem(info.position);

			new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Delete").setMessage("Are you sure?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							removeTask(task2);
							datasource.deleteTask(task2);
						}
					}).setNegativeButton("No", null).show();

			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// we only want to show this if accessed via
		// hive details page
		//if (hiveId >= 0) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.task_menu, menu);
		//}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_task:

			Intent intent1 = new Intent(this, TaskDetails.class);
			intent1.putExtra("hive_id", hiveId);
			
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
