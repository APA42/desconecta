/*
        Copyright 2011 Alberto Perez Alonso
 
        This file is part of Desconecta.

    Desconecta is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Desconecta is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Desconecta.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.apa42.desconecta;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

public class DesconectaActivity extends Activity 
{
	// for logs
	private final String CLASS_NAME = getClass().getName();

	private TextView _timeDisplay;
    private TimePickerDialog.OnTimeSetListener _timeSetListener; 

    private int _hour;
    private int _minute;
    
    static final int PICK_TIME_DIALOG_ID = 0;

    private ToggleButton _AlarmAirplaneMode = null;
    private CheckBox _Sunday = null;
	private CheckBox _Monday = null;
	private CheckBox _Tuesday = null;
	private CheckBox _Wednesday = null;
	private CheckBox _Thursday = null;
	private CheckBox _Friday = null;
	private CheckBox _Saturday = null;
	private CheckBox _AllDays = null;
    
	// Pair onCreate/onDestroy
	// -----------------------
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onCreate(Bundle savedInstanceState)");
		//
        super.onCreate(savedInstanceState);
        
		// Get first day of the week
		Calendar CalendarAux = Calendar.getInstance();
		CalendarAux.setTimeInMillis(System.currentTimeMillis());
		int firstDayOfWeek = CalendarAux.getFirstDayOfWeek();
		switch(firstDayOfWeek)
		{
			case Calendar.MONDAY:
				setContentView(R.layout.desconecta_monday);
				break;
			case Calendar.SUNDAY:
				setContentView(R.layout.desconecta_sunday);
				break;
			default:
				setContentView(R.layout.desconecta_sunday);
		}
        
        _AlarmAirplaneMode = (ToggleButton) findViewById(R.id.togglebuttonAlarmAirplaneMode);
        _timeDisplay = (TextView) findViewById(R.id.timeDisplay);
        //
        _Sunday = (CheckBox) findViewById(R.id.checkboxSunday);
    	_Monday = (CheckBox) findViewById(R.id.checkboxMonday);
    	_Tuesday = (CheckBox) findViewById(R.id.checkboxTuesday);
    	_Wednesday = (CheckBox) findViewById(R.id.checkboxWednesday);
    	_Thursday = (CheckBox) findViewById(R.id.checkboxThursday);
    	_Friday = (CheckBox) findViewById(R.id.checkboxFriday);
    	_Saturday = (CheckBox) findViewById(R.id.checkboxSaturday);
    	_AllDays = (CheckBox) findViewById(R.id.checkboxAllDays);
    	//
    	
    	loadPreferences();
    	displayAlarmTime();
    	
    	if ( _AlarmAirplaneMode.isChecked() && !Utils.isAlarmAirplaneModeSet(getApplicationContext()) )
    	{
    		Utils.calculateNextAlarm(getApplicationContext(), true);
    	}
        
        _timeSetListener = new TimePickerDialog.OnTimeSetListener() 
        {		
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) 
			{
				if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onTimeSet(TimePicker view, int hourOfDay, int minute)");
				//
				_hour = hourOfDay;
				_minute = minute;
				updateAlarmTimeDisplay();
			}
		};
    }

    @Override
	protected void onDestroy() 
    {
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onCreate(Bundle savedInstanceState)");
		//
		super.onDestroy();
	}
    
    
	// Pair onPause/onResume
	// ---------------------
	@Override
	protected void onPause() 
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onPause()" );
		//
		super.onPause();
		//
		savePreferences();
	}

	@Override
	protected void onResume() 
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onResume()" );
		//
		super.onResume();
		loadPreferences();
	}
    
	// Pair onStart/onStop
	// -------------------
	@Override
	protected void onStart() 
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onStart()" );
		//
		super.onStart();
	}

	@Override
	protected void onStop() 
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onStop()" );
		//
		super.onStop();
	}	
	
	@Override
	protected Dialog onCreateDialog(int id) 
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onCreateDialog(int id)");
		//
		switch (id) 
		{
	    	case PICK_TIME_DIALOG_ID:
	    		// Check if time is set to 24H or not
	    		boolean b24Hrs = Utils.is24Hour(getApplicationContext());
	    		return new TimePickerDialog(this,_timeSetListener, _hour, _minute, b24Hrs);
	    }
	    return null;
	}

	
	public void clickPickTime(View arg)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "clickPickTime(View arg)" );
		//
		showDialog(PICK_TIME_DIALOG_ID);
	}
	
	
	private void updateAlarmTimeDisplay()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "updateAlarmTimeDisplay()" );
		//
		displayAlarmTime();
	    // Disable alarms
	    Utils.cancelAlarmAirplaneMode(getApplicationContext());
    	_AlarmAirplaneMode.setChecked(false);
    	canEnableActivateAlarmButton();
    	//
    	savePreferences();
	}
	
	
	private void displayAlarmTime()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "displayAlarmTime()" );
		//
		StringBuilder TimeDisplay = new StringBuilder();
		if ( ! Utils.is24Hour(getApplicationContext()))
		{
			if ( _hour >= 12 ) 
			{
				TimeDisplay.append(Utils.pad(_hour-12));
				TimeDisplay.append(":");
				TimeDisplay.append(Utils.pad(_minute));
				TimeDisplay.append(" "+ getResources().getString(R.string.TIME_FORMAT_PM));
			}
			else
			{
				TimeDisplay.append(Utils.pad(_hour));
				TimeDisplay.append(":");
				TimeDisplay.append(Utils.pad(_minute));
				TimeDisplay.append(" "+ getResources().getString(R.string.TIME_FORMAT_AM));
			}
		}
		else
		{
			TimeDisplay.append(Utils.pad(_hour));
			TimeDisplay.append(":");
			TimeDisplay.append(Utils.pad(_minute));
		}
			
		_timeDisplay.setText(TimeDisplay.toString());
	}
	
	public void clickAlarmAirplaneMode(View arg)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "clickAlarmAirplaneMode(View arg)" );
		//
		savePreferences();
		Utils.cancelAlarmAirplaneMode(getApplicationContext());
		if ( _AlarmAirplaneMode.isChecked() )
			Utils.calculateNextAlarm(getApplicationContext(), true);
	}

	public void clickCheckBox_Days(View arg)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "clickCheckBox_Days(View arg)" );
		//
		_AllDays.setChecked(_Sunday.isChecked() && 
							_Monday.isChecked() &&
							_Tuesday.isChecked() &&
							_Wednesday.isChecked() && 
							_Thursday.isChecked() &&
							_Friday.isChecked() &&
							_Saturday.isChecked() );
		if ( _AlarmAirplaneMode.isChecked() )
		{
			Utils.cancelAlarmAirplaneMode(getApplicationContext());
			_AlarmAirplaneMode.setChecked(false);
		}
		canEnableActivateAlarmButton();
		savePreferences();
	}
	
	public void clickCheckBox_AllDays(View arg)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "clickCheckBox_AllDays(View arg)" );
		//
		_Sunday.setChecked(_AllDays.isChecked());
		_Monday.setChecked(_AllDays.isChecked());
		_Tuesday.setChecked(_AllDays.isChecked());
		_Wednesday.setChecked(_AllDays.isChecked());
		_Thursday.setChecked(_AllDays.isChecked());
		_Friday.setChecked(_AllDays.isChecked());
		_Saturday.setChecked(_AllDays.isChecked());
		//
		if ( _AlarmAirplaneMode.isChecked() )
		{
			Utils.cancelAlarmAirplaneMode(getApplicationContext());
			_AlarmAirplaneMode.setChecked(false);
		}
		canEnableActivateAlarmButton();
		savePreferences();
	}
	
	private void canEnableActivateAlarmButton()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "canEnableActivateAlarmButton()");
		//
		boolean enableActivateAlarmButton = _Sunday.isChecked() 
											|| _Monday.isChecked() 
											|| _Tuesday.isChecked() 
											|| _Wednesday.isChecked() 
											|| _Thursday.isChecked() 
											|| _Friday.isChecked() 
											|| _Saturday.isChecked();
		
		_AlarmAirplaneMode.setEnabled(enableActivateAlarmButton);
		if ( !enableActivateAlarmButton && _AlarmAirplaneMode.isChecked() )
		{
			// Have to delete the alarm
			Utils.cancelAlarmAirplaneMode(getApplicationContext());
			_AlarmAirplaneMode.setChecked(false);
		}
	}
	
	private void loadPreferences()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "loadPreferences()" );
		//
		SharedPreferences sharedPreferences = getSharedPreferences(ConfigAppValues.PREFERENCES_DESCONECTA,ConfigAppValues.PREFERENCES_MODE);
		//
		_AlarmAirplaneMode.setChecked(sharedPreferences.getBoolean(ConfigAppValues.PREF_ALARM_IS_SET,false));
		_hour = sharedPreferences.getInt(ConfigAppValues.PREF_ALARM_TIME_HOUR, ConfigAppValues.PREF_ALARM_TIME_HOUR_DEFAULT);
		_minute = sharedPreferences.getInt(ConfigAppValues.PREF_ALARM_TIME_MINUTE, ConfigAppValues.PREF_ALARM_TIME_MINUTE_DEFAULT);
		// Update UI
		_Sunday.setChecked(sharedPreferences.getBoolean(ConfigAppValues.PREF_SUNDAY_IS_SET,false));
		_Monday.setChecked(sharedPreferences.getBoolean(ConfigAppValues.PREF_MONDAY_IS_SET,false));
		_Tuesday.setChecked(sharedPreferences.getBoolean(ConfigAppValues.PREF_TUESDAY_IS_SET,false));
		_Wednesday.setChecked(sharedPreferences.getBoolean(ConfigAppValues.PREF_WEDNESDAY_IS_SET,false));
		_Thursday.setChecked(sharedPreferences.getBoolean(ConfigAppValues.PREF_THURSDAY_IS_SET,false));
		_Friday.setChecked(sharedPreferences.getBoolean(ConfigAppValues.PREF_FRIDAY_IS_SET,false));
		_Saturday.setChecked(sharedPreferences.getBoolean(ConfigAppValues.PREF_SATURDAY_IS_SET,false));
		_AllDays.setChecked(sharedPreferences.getBoolean(ConfigAppValues.PREF_ALLDAYS_IS_SET,false));
		
		canEnableActivateAlarmButton();
	}

	private void savePreferences()
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "savePreferences()" );
		//
		SharedPreferences sharedPreferences = getSharedPreferences(ConfigAppValues.PREFERENCES_DESCONECTA,ConfigAppValues.PREFERENCES_MODE);
    	Editor editor = sharedPreferences.edit();
    	//
    	editor.putBoolean(ConfigAppValues.PREF_ALARM_IS_SET, _AlarmAirplaneMode.isChecked());
    	editor.putInt(ConfigAppValues.PREF_ALARM_TIME_HOUR, _hour);
    	editor.putInt(ConfigAppValues.PREF_ALARM_TIME_MINUTE, _minute);
    	editor.putBoolean(ConfigAppValues.PREF_SUNDAY_IS_SET, _Sunday.isChecked());
    	editor.putBoolean(ConfigAppValues.PREF_MONDAY_IS_SET, _Monday.isChecked());
    	editor.putBoolean(ConfigAppValues.PREF_TUESDAY_IS_SET, _Tuesday.isChecked());
    	editor.putBoolean(ConfigAppValues.PREF_WEDNESDAY_IS_SET, _Wednesday.isChecked());
    	editor.putBoolean(ConfigAppValues.PREF_THURSDAY_IS_SET, _Thursday.isChecked());
    	editor.putBoolean(ConfigAppValues.PREF_FRIDAY_IS_SET, _Friday.isChecked());
    	editor.putBoolean(ConfigAppValues.PREF_SATURDAY_IS_SET, _Saturday.isChecked());
    	editor.putBoolean(ConfigAppValues.PREF_ALLDAYS_IS_SET, _AllDays.isChecked());
    	//
    	editor.commit();		
	}
}
