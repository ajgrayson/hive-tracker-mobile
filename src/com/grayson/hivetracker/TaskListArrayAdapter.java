package com.grayson.hivetracker;

import java.util.List;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.grayson.hivetracker.database.Task;
import com.grayson.hivetracker.tools.Tools;

public class TaskListArrayAdapter extends ArrayAdapter<Task> {
	private final Context context;
	private final List<Task> values;

	private final Boolean allTasks;

	public TaskListArrayAdapter(Context context, int textViewResourceId, List<Task> values) {
		super(context, textViewResourceId, values);
		this.context = context;
		this.values = values;

		this.allTasks = textViewResourceId == R.layout.task_list_item ? false : true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int pos = position;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView;
		
		if (allTasks)
			rowView = inflater.inflate(R.layout.all_tasks_list_item, parent, false);
		else
			rowView = inflater.inflate(R.layout.task_list_item, parent, false);
		
		final TextView textView = (TextView) rowView.findViewById(R.id.task_list_label);

		final TextView textViewSite = (TextView) rowView.findViewById(R.id.task_list_site_name);

		// final CheckBox checkbox = (CheckBox) rowView
		// .findViewById(R.id.task_list_checkbox);

		if (allTasks) {
			textView.setText(Tools.tidyString(values.get(position).getDescription(), 19));
			
			String siteName = values.get(position).getSiteName();
			if(siteName != null && siteName.length() > 0) {
				textViewSite.setText(Tools.tidyString(siteName, 18));
			} else {
				textViewSite.setText("N/A");
			}
		} else {
			textView.setText(Tools.tidyString(values.get(position).getDescription(), 35));
		}

		// Change icon based on name
		Boolean done = values.get(position).getDone();

		if (done) {
			textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		}

		// checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		// @Override
		// public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		// if (arg1) {
		// textView.setPaintFlags(textView.getPaintFlags()
		// | Paint.STRIKE_THRU_TEXT_FLAG);
		// } else {
		// textView.setPaintFlags(textView.getPaintFlags()
		// & (~Paint.STRIKE_THRU_TEXT_FLAG));
		// }
		// }
		// });

		return rowView;
	}
}