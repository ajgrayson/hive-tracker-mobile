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
import com.grayson.hivetracker.database.Category;

public class CategoryList extends ListActivity {
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

	public void removeCategory(Category category) {
		if (category == null)
			return;

		@SuppressWarnings("unchecked")
		ArrayAdapter<Category> adapter = (ArrayAdapter<Category>) getListAdapter();
		adapter.remove(category);
		adapter.notifyDataSetChanged();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_CODE_PICK_LOCATION) {
			if (resultCode == RESULT_OK) {

				setResult(RESULT_OK, data);

				finish();
			}
		}
	}

	public void loadData() {
		List<Category> values = datasource.getAllCategories();

		// Use the SimpleCursorAdapter to show the
		// elements in a ListView
		ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(this, android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView parentView, View childView, int position, long id) {
				Category cat1 = (Category) getListAdapter().getItem(position);

				Intent curIntent = getIntent();

				Intent intent = new Intent(getApplicationContext(), LocationList.class);
				intent.setClass(getApplicationContext(), LocationList.class); // setData(LocationList.class);
				intent.putExtra("category_id", cat1.getId());

				if (curIntent.getAction() != null && curIntent.getAction().compareTo(Constants.ACTION_PICK) == 0) {
					// user wants to select a location
					intent.setAction(Constants.ACTION_PICK);
					startActivityForResult(intent, Constants.REQUEST_CODE_PICK_LOCATION);
				} else {
					startActivity(intent);
				}

			}
		});

		registerForContextMenu(lv);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.category_list_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final Category cat = (Category) this.getListAdapter().getItem(info.position);

		switch (item.getItemId()) {
		case R.id.edit_category:

			Intent intent = new Intent(getApplicationContext(), CategoryDetails.class);
			intent.putExtra("category_id", cat.getId());
			startActivity(intent);

			return true;
		case R.id.delete_category:

			if (datasource.isCategoryEmpty(cat.getId())) {
				new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Delete").setMessage("Are you sure?")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								removeCategory(cat);
								datasource.deleteCategory(cat);
							}
						}).setNegativeButton("No", null).show();
			} else {
				Toast.makeText(this, "Failed: Please remove sites before deleting.", Toast.LENGTH_LONG).show();
			}

			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.categories_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_category:

			Intent intent1 = new Intent(this, CategoryDetails.class);
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
