package com.grayson.hivetracker;

import java.util.Date;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.grayson.hivetracker.database.AppDataSource;
import com.grayson.hivetracker.database.AppSQLiteHelper;
import com.grayson.hivetracker.database.HarvestRecord;
import com.grayson.hivetracker.database.ListValue;

public class SelectAction extends Activity {

	private AppDataSource datasource;

	private CheckBox checkBoxHarvest;

	private CheckBox checkBoxTreat;

	private CheckBox checkBoxRemoveTreatment;

	private CheckBox checkBoxFeed;

	private CheckBox checkBoxWinterDown;

	// private Spinner spinnerTreatmentType;

	// private List<ListValue> treatmentListValues;

	private HarvestRecord harvestInfo;

	private long treatmentTypeId;

	private long locationId;

	private long categoryId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_action);

		datasource = AppDataSource.getInstance();
		datasource.open();

		checkBoxHarvest = (CheckBox) this.findViewById(R.id.checkBoxHarvest);
		checkBoxTreat = (CheckBox) this.findViewById(R.id.checkBoxTreat);
		checkBoxRemoveTreatment = (CheckBox) this.findViewById(R.id.checkBoxRemoveTreatment);
		checkBoxFeed = (CheckBox) this.findViewById(R.id.checkBoxFeed);
		checkBoxWinterDown = (CheckBox) this.findViewById(R.id.checkBoxWinterDown);
		// spinnerTreatmentType = (Spinner)
		// this.findViewById(R.id.spinnerTreatmentType);

		checkBoxHarvest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// getHarvestInfo(isChecked);
				onCheckHarvest(isChecked);
			}
		});

		checkBoxTreat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// getTreatment(isChecked);
				onCheckTreat(isChecked);
			}
		});

		checkBoxRemoveTreatment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				onCheckRemoveTreatment(isChecked);
			}
		});

		checkBoxFeed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				onCheckFeed(isChecked);
			}
		});

		checkBoxWinterDown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				onCheckWinterDown(isChecked);
			}
		});

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();
		locationId = intent.getLongExtra("location_id", -1);
		categoryId = intent.getLongExtra("category_id", -1);
		
		loadData();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.select_action_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.done_selecting_action:

			if (save()) {
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

	protected void onCheckHarvest(boolean isChecked) {
		if (isChecked) {
			Intent intent2 = new Intent(getApplicationContext(), EnterHarvestInfo.class);
			//intent2.putExtra("location_id", locationId);
			//intent2.putExtra("category_id", categoryId);
			startActivityForResult(intent2, Constants.REQUEST_CODE_GET_HARVEST_INFO);
		}
	}

	protected void onCheckTreat(boolean isChecked) {
		if (isChecked) {
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.select_treatment_dialog);
			dialog.setTitle("Select Treatment");

			final Spinner spinnerTreatmentType = (Spinner) dialog.findViewById(R.id.spinnerTreatmentType);

			List<ListValue> treatmentListValues = datasource.getAllValues(AppSQLiteHelper.TABLE_LIST_TREATMENT);
			ArrayAdapter<ListValue> adapterTreatment = new ArrayAdapter<ListValue>(this, android.R.layout.simple_spinner_item, treatmentListValues);
			adapterTreatment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerTreatmentType.setAdapter(adapterTreatment);
			spinnerTreatmentType.setSelection(0);

			Button dialogOkButton = (Button) dialog.findViewById(R.id.buttonOK);
			// if button is clicked, close the custom dialog
			dialogOkButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ListValue value = (ListValue) spinnerTreatmentType.getSelectedItem();
					treatmentTypeId = value.getId();

					if (checkBoxRemoveTreatment.isChecked()) {
						checkBoxRemoveTreatment.setChecked(false);
					}

					dialog.dismiss();
				}
			});

			Button dialogCancelButton = (Button) dialog.findViewById(R.id.buttonCancel);
			// if button is clicked, close the custom dialog
			dialogCancelButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					checkBoxTreat.setChecked(false);
					dialog.dismiss();
				}
			});

			dialog.show();
		} else {

		}
	}

	protected void onCheckRemoveTreatment(boolean isChecked) {
		if (isChecked) {
			new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Remove Treatment").setMessage("Are you sure?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (checkBoxTreat.isChecked()) {
								checkBoxTreat.setChecked(false);
							}
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							checkBoxRemoveTreatment.setChecked(false);
						}
					}).show();
		} else {

		}
	}

	protected void onCheckFeed(boolean isChecked) {
		if (isChecked) {
			new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Feed").setMessage("Are you sure?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// do nothing - we're good
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							checkBoxFeed.setChecked(false);
						}
					}).show();
		} else {

		}
	}

	protected void onCheckWinterDown(boolean isChecked) {
		if (isChecked) {
			new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Winter Down").setMessage("Are you sure?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// do nothing - we're good
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							checkBoxWinterDown.setChecked(false);
						}
					}).show();
		} else {

		}
	}

	protected void loadData() {

	}

	public boolean save() {
		long time = (new Date().getTime());

		if (checkBoxHarvest.isChecked()) {
			if (harvestInfo != null) {
				// save harvest
				datasource.enterHarvestRecord(categoryId, locationId, harvestInfo.getDate(), harvestInfo.isDisease(),
						harvestInfo.getSupers(), harvestInfo.getFloralType());

				// update harvest date on hives
				datasource.updateHarvestDateOnHives(categoryId, locationId, harvestInfo.getDate());
			}
		}
		if (checkBoxTreat.isChecked()) {
			// update treatment date and type on all hives
			datasource.updateTreatmentDateOnHives(categoryId, locationId, treatmentTypeId, time);
		}
		if (checkBoxRemoveTreatment.isChecked()) {
			// clear treatment type and date on all hives
			datasource.removeTreatmentFromHives(categoryId, locationId);
		}
		if (checkBoxFeed.isChecked()) {
			// update last fed date on all hives
			datasource.updateLastFedDateOnHives(categoryId, locationId, time);
		}
		if (checkBoxWinterDown.isChecked()) {
			// update wintered down date on all hives
			datasource.updateWinterDownDateOnHives(categoryId, locationId, time);
		}
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_CODE_GET_HARVEST_INFO) {
			if (resultCode == RESULT_OK) {

				long date = data.getLongExtra("harvest_date", 0);
				boolean disease = data.getBooleanExtra("disease", false);
				long supers = data.getLongExtra("supers", 0);
				long floralType = data.getLongExtra("floral_type_id", 0);
				
				if (date == 0) {
					// it was canceled
					checkBoxHarvest.setChecked(false);
				} else {
					
					harvestInfo = new HarvestRecord();
					harvestInfo.setDate(date);
					harvestInfo.setDisease(disease);
					harvestInfo.setSupers(supers);
					harvestInfo.setFloralType(floralType);

				}
			} else {
				checkBoxHarvest.setChecked(false);
			}
		}
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
