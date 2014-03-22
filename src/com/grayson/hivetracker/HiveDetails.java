package com.grayson.hivetracker;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grayson.hivetracker.database.AppDataSource;
import com.grayson.hivetracker.database.AppSQLiteHelper;
import com.grayson.hivetracker.database.Hive;
import com.grayson.hivetracker.database.ListValue;
import com.grayson.hivetracker.database.Location;
import com.grayson.hivetracker.tools.Tools;

public class HiveDetails extends Activity {
	private AppDataSource datasource;

	/**
	 * set if editing an existing hive
	 */
	private long existingHiveId;

	/**
	 * this is set if we add a task to the hive before it is saved.
	 */
	private long tempHiveId;

	/**
	 * location id for this hive
	 */
	private long locationId;

	/**
	 * scanned qr_code number
	 */
	private String qrCode;

	TextView textQrCode;
	TextView textQueenType;
	EditText textSplitType;
	Spinner spinnerBee;
	Spinner spinnerBrood;
	Spinner spinnerFood;
	Spinner spinnerHealth;
	Spinner spinnerVarroa;
	Spinner spinnerVirus;
	Spinner spinnerPollen;
	TextView textLocationName;
	TextView textMafId;
	EditText textVarroaCount;
	CheckBox checkBoxIsVarroaSample;
	CheckBox checkBoxIsGoodProducer;

	// bee
	List<ListValue> beeListValues;
	// brood
	List<ListValue> broodListValues;
	// food
	List<ListValue> foodListValues;
	// health
	List<ListValue> healthListValues;
	// varroa
	List<ListValue> varroaListValues;
	// virus
	List<ListValue> virusListValues;
	// pollen
	List<ListValue> pollenListValues;

	// split
	// List<ListValue> splitListValues;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hive_details);

		textQrCode = (TextView) this.findViewById(R.id.textViewQrCode);
		textQueenType = (TextView) this.findViewById(R.id.textEditQueenType);
		textSplitType = (EditText) this.findViewById(R.id.editTextHiveSplit);

		spinnerBee = (Spinner) findViewById(R.id.spinnerHiveBee);
		spinnerBrood = (Spinner) findViewById(R.id.spinnerHiveBrood);
		spinnerFood = (Spinner) findViewById(R.id.spinnerHiveFood);
		spinnerHealth = (Spinner) findViewById(R.id.spinnerHiveHealth);
		spinnerVarroa = (Spinner) findViewById(R.id.spinnerHiveVarroa);
		spinnerVirus = (Spinner) findViewById(R.id.spinnerHiveVirus);
		spinnerPollen = (Spinner) findViewById(R.id.spinnerHivePollen);

		textLocationName = (TextView) this.findViewById(R.id.textViewSiteName);
		textMafId = (TextView) this.findViewById(R.id.textViewMafId);

		textVarroaCount = (EditText) this.findViewById(R.id.editTextVarroaCount);
		checkBoxIsVarroaSample = (CheckBox) this.findViewById(R.id.checkBoxIsVarroaSample);
		checkBoxIsGoodProducer = (CheckBox) this.findViewById(R.id.checkBoxIsGoodProducer);
		textVarroaCount.setEnabled(false); // enabled by default - tied to
											// isVarroaSample state

		// add a listener to enable/disable the varroaCount textbox
		checkBoxIsVarroaSample.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				textVarroaCount.setEnabled(isChecked);
			}
		});

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		datasource = AppDataSource.getInstance();
		datasource.open();

		Intent intent = getIntent();
		qrCode = intent.getStringExtra("qr_code");

		initSpinners();

		loadData(qrCode);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.hive_details_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	protected void returnHome() {
		Intent intent2 = new Intent(getApplicationContext(), Home.class);
		intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent2);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save_hive:

			saveItem();

			// app icon in action bar clicked; go home
			finish();

			return true;
		case android.R.id.home:
			if (changesHaveBeenMade()) {
				new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("You've made changes")
						.setMessage("Save your changes?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								saveItem();
								returnHome();
							}
						}).setNegativeButton("No", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								returnHome();
							}
						}).show();
			} else {
				returnHome();
			}

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onButtonSetLocationClick(View v) {
		Intent intent = new Intent(getApplicationContext(), CategoryList.class);

		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setAction(Constants.ACTION_PICK);

		startActivityForResult(intent, Constants.REQUEST_CODE_PICK_LOCATION);
	}

	public void onBtnHiveTasksClicked(View v) {
		Intent target = new Intent(this, TaskList.class);

		saveItem();

		target.putExtra("hive_id", tempHiveId);

		startActivity(target);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_CODE_PICK_LOCATION) {
			if (resultCode == RESULT_OK) {
				long newLocationId = data.getLongExtra("location_id", 0); // .getStringExtra("locationId");
				if (newLocationId >= 0) {

					datasource.open();

					Location location = datasource.getLocation(newLocationId);
					if (location != null) {

						textLocationName.setText(location.getName());

						textMafId.setText(String.valueOf(location.getMafId()));

						locationId = location.getId();

					} else {
						textLocationName.setText(String.valueOf(""));
					}
				}
			}
		}
	}

	private void initSpinners() {
		// bee
		beeListValues = datasource.getAllValues(AppSQLiteHelper.TABLE_LIST_BEE);
		ArrayAdapter<ListValue> adapterBee = new ArrayAdapter<ListValue>(this, android.R.layout.simple_spinner_item, beeListValues);
		adapterBee.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerBee.setAdapter(adapterBee);

		// brood
		broodListValues = datasource.getAllValues(AppSQLiteHelper.TABLE_LIST_BROOD);
		ArrayAdapter<ListValue> adapterBrood = new ArrayAdapter<ListValue>(this, android.R.layout.simple_spinner_item, broodListValues);
		adapterBrood.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerBrood.setAdapter(adapterBrood);

		// food
		foodListValues = datasource.getAllValues(AppSQLiteHelper.TABLE_LIST_FOOD);
		ArrayAdapter<ListValue> adapterFood = new ArrayAdapter<ListValue>(this, android.R.layout.simple_spinner_item, foodListValues);
		adapterFood.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerFood.setAdapter(adapterFood);

		// health
		healthListValues = datasource.getAllValues(AppSQLiteHelper.TABLE_LIST_HEALTH);
		ArrayAdapter<ListValue> adapterHealth = new ArrayAdapter<ListValue>(this, android.R.layout.simple_spinner_item, healthListValues);
		adapterHealth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerHealth.setAdapter(adapterHealth);

		// varroa
		varroaListValues = datasource.getAllValues(AppSQLiteHelper.TABLE_LIST_VARROA);
		ArrayAdapter<ListValue> adapterVarroa = new ArrayAdapter<ListValue>(this, android.R.layout.simple_spinner_item, varroaListValues);
		adapterVarroa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerVarroa.setAdapter(adapterVarroa);

		// virus
		virusListValues = datasource.getAllValues(AppSQLiteHelper.TABLE_LIST_VIRUS);
		ArrayAdapter<ListValue> adapterVirus = new ArrayAdapter<ListValue>(this, android.R.layout.simple_spinner_item, virusListValues);
		adapterVirus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerVirus.setAdapter(adapterVirus);

		// pollen
		pollenListValues = datasource.getAllValues(AppSQLiteHelper.TABLE_LIST_POLLEN);

		ArrayAdapter<ListValue> adapterPollen = new ArrayAdapter<ListValue>(this, android.R.layout.simple_spinner_item, pollenListValues);
		adapterPollen.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerPollen.setAdapter(adapterPollen);
	}

	public void loadData(String qrCode) {
		Hive hive = datasource.getHive(qrCode);

		existingHiveId = -1;

		if (hive != null) {
			textQrCode.setText(hive.getQrCode());

			textQueenType.setText(hive.getQueenType());
			textSplitType.setText(hive.getSplitType());

			Location location = datasource.getLocation(hive.getLocationId());
			if (location != null) {
				textLocationName.setText(Tools.tidyString(location.getName(), 20));
				textMafId.setText(String.valueOf(location.getMafId()));
				locationId = location.getId();
			} else {
				textLocationName.setText(String.valueOf("-"));
			}

			checkBoxIsGoodProducer.setChecked(hive.getIsGoodProducer());
			checkBoxIsVarroaSample.setChecked(hive.getIsVarroaSample());
			if (hive.getIsVarroaSample()) {
				textVarroaCount.setEnabled(true);
				textVarroaCount.setText(String.valueOf(hive.getVarroaCount()));
			} else {
				textVarroaCount.setEnabled(false);
				textVarroaCount.setText("");
			}

			existingHiveId = hive.getId();

			int pos = AppDataSource.getIndex(beeListValues, hive.getBeeId());
			spinnerBee.setSelection(pos);

			pos = AppDataSource.getIndex(broodListValues, hive.getBroodId());
			spinnerBrood.setSelection(pos);

			pos = AppDataSource.getIndex(healthListValues, hive.getHealthId());
			spinnerHealth.setSelection(pos);

			pos = AppDataSource.getIndex(foodListValues, hive.getFoodId());
			spinnerFood.setSelection(pos);

			pos = AppDataSource.getIndex(varroaListValues, hive.getVarroaId());
			spinnerVarroa.setSelection(pos);

			pos = AppDataSource.getIndex(virusListValues, hive.getVirusId());
			spinnerVirus.setSelection(pos);

			pos = AppDataSource.getIndex(pollenListValues, hive.getPollenId());
			spinnerPollen.setSelection(pos);

			// pos = AppDataSource.getIndex(splitListValues, hive.getSplitId());
			// spinnerSplit.setSelection(pos);

		} else {
			textQrCode.setText(qrCode);
			textQueenType.setText("");
			textSplitType.setText("");
			textLocationName.setText("");
			textMafId.setText("");
			textVarroaCount.setText("");
		}
	}

	public boolean saveItem() {
		String queenType = textQueenType.getText().toString();
		String splitType = textSplitType.getText().toString();

		// ListValue queenValue = (ListValue) spinnerQueen.getSelectedItem();
		ListValue beeValue = (ListValue) spinnerBee.getSelectedItem();
		ListValue broodValue = (ListValue) spinnerBrood.getSelectedItem();
		ListValue foodValue = (ListValue) spinnerFood.getSelectedItem();
		ListValue healthValue = (ListValue) spinnerHealth.getSelectedItem();
		ListValue varroaValue = (ListValue) spinnerVarroa.getSelectedItem();
		ListValue virusValue = (ListValue) spinnerVirus.getSelectedItem();
		ListValue pollenValue = (ListValue) spinnerPollen.getSelectedItem();
		// ListValue splitValue = (ListValue) spinnerSplit.getSelectedItem();

		if (existingHiveId >= 0) {
			Hive hive = datasource.getHiveById(existingHiveId);
			if (changesHaveBeenMade()) {
				hive.setLocationId(locationId);
				hive.setQrCode(qrCode);

				hive.setQueenType(queenType);
				hive.setBeeId(beeValue.getId());
				hive.setBroodId(broodValue.getId());
				hive.setFoodId(foodValue.getId());
				hive.setHealthId(healthValue.getId());
				hive.setVarroaId(varroaValue.getId());
				hive.setVirusId(virusValue.getId());
				hive.setPollenId(pollenValue.getId());
				// hive.setSplitId(splitValue.getId());
				hive.setSplitType(splitType);

				hive.setIsVarroaSample(checkBoxIsVarroaSample.isChecked());
				if (checkBoxIsVarroaSample.isChecked()) {
					String varroaCountText = textVarroaCount.getText().toString();
					long varroaCount = 0;
					if(varroaCountText != null && varroaCountText.length() > 0) {
						varroaCount = Long.valueOf(varroaCountText);
					}
					hive.setVarroaCount(varroaCount);
				} else {
					hive.setVarroaCount(0);
				}
				hive.setIsGoodProducer(checkBoxIsGoodProducer.isChecked());

				datasource.updateHive(hive);
			}
			// tempHiveId captures the hiveId as the saveItem() method
			// is called before opening the hives task page
			tempHiveId = hive.getId();
		} else {

			String qrCode = textQrCode.getText().toString();

			Hive hive = datasource.createHive(qrCode);
			hive.setLocationId(locationId);

			hive.setQueenType(queenType);
			hive.setBeeId(beeValue.getId());
			hive.setBroodId(broodValue.getId());
			hive.setFoodId(foodValue.getId());
			hive.setHealthId(healthValue.getId());
			hive.setVarroaId(varroaValue.getId());
			hive.setVirusId(virusValue.getId());
			hive.setPollenId(pollenValue.getId());
			// hive.setSplitId(splitValue.getId());
			hive.setSplitType(splitType);

			hive.setIsVarroaSample(checkBoxIsVarroaSample.isChecked());
			if (checkBoxIsVarroaSample.isChecked()) {
				String varroaCountText = textVarroaCount.getText().toString();
				long varroaCount = 0;
				if(varroaCountText != null && varroaCountText.length() > 0) {
					varroaCount = Long.valueOf(varroaCountText);
				}
				hive.setVarroaCount(varroaCount);
			} else {
				hive.setVarroaCount(0);
			}
			hive.setIsGoodProducer(checkBoxIsGoodProducer.isChecked());

			datasource.updateHive(hive);
			tempHiveId = hive.getId();
		}

		return true;
	}

	protected boolean changesHaveBeenMade() {
		if (existingHiveId < 0) {
			// this is a new hive so changes have been made
			Tools.logInfo(this, "Hive", "Existing Hive ID < 0");
			return true;
		} else {
			// this is an existing hive so there may not have been changes
			Hive hive = datasource.getHiveById(existingHiveId);
			if (hive == null)
				return true;

			String queenType = textQueenType.getText().toString();
			String splitType = textSplitType.getText().toString();

			ListValue beeValue = (ListValue) spinnerBee.getSelectedItem();
			ListValue broodValue = (ListValue) spinnerBrood.getSelectedItem();
			ListValue foodValue = (ListValue) spinnerFood.getSelectedItem();
			ListValue healthValue = (ListValue) spinnerHealth.getSelectedItem();
			ListValue varroaValue = (ListValue) spinnerVarroa.getSelectedItem();
			ListValue virusValue = (ListValue) spinnerVirus.getSelectedItem();
			ListValue pollenValue = (ListValue) spinnerPollen.getSelectedItem();

			if (hive.getBeeId() != beeValue.getId()) {
				Tools.logInfo(this, "Hive", "BeeID changed.");
				return true;
			}
			if (hive.getBroodId() != broodValue.getId()) {
				Tools.logInfo(this, "Hive", "BroodID changed.");
				return true;
			}
			if (hive.getFoodId() != foodValue.getId()) {
				Tools.logInfo(this, "Hive", "FoodID changed.");
				return true;
			}
			if (hive.getHealthId() != healthValue.getId()) {
				Tools.logInfo(this, "Hive", "HealthID changed.");
				return true;
			}
			if (hive.getVarroaId() != varroaValue.getId()) {
				Tools.logInfo(this, "Hive", "VarroaID changed.");
				return true;
			}
			if (hive.getPollenId() != pollenValue.getId()) {
				Tools.logInfo(this, "Hive", "PollenID changed.");
				return true;
			}
			if (hive.getVirusId() != virusValue.getId()) {
				Tools.logInfo(this, "Hive", "VirusID changed.");
				return true;
			}
			if (!hive.getQueenType().equals(queenType)) {
				Tools.logInfo(this, "Hive", "QueenType changed.");
				return true;
			}
			if (!hive.getSplitType().equals(splitType)) {
				Tools.logInfo(this, "Hive", "SplitType changed.");
				return true;
			}
			if (hive.getLocationId() != locationId) {
				Tools.logInfo(this, "Hive", "LocationID changed.");
				return true;
			}
			if (hive.getIsVarroaSample() != checkBoxIsVarroaSample.isChecked()) {
				Tools.logInfo(this, "Hive", "IsVarroaSample changed.");
				return true;
			}
			
			String varroaCountText = textVarroaCount.getText().toString();
			long varroaCount = 0;
			if(varroaCountText != null && varroaCountText.length() > 0) {
				varroaCount = Long.valueOf(varroaCountText);
			}
			if (hive.getVarroaCount() != varroaCount) {
				Tools.logInfo(this, "Hive", "VarroaCount changed.");
				return true;
			}
			if (hive.getIsGoodProducer() != checkBoxIsGoodProducer.isChecked()) {
				Tools.logInfo(this, "Hive", "IsGoodProducer changed.");
				return true;
			}

			return false;
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
