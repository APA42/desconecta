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

import android.app.Activity;

public class ConfigAppValues
{
	public static final boolean DEBUG = false;
	// Data to store
	public static final String PREFERENCES_DESCONECTA = "PREF_DESCONECTA";
	public static final int PREFERENCES_MODE = Activity.MODE_PRIVATE;
	// Is set and time
	public static final String PREF_ALARM_IS_SET = "Pref_AlarmIsSet";
	public static final String PREF_ALARM_TIME_HOUR = "Pref_AlarmTime_Hour";
	public static final int PREF_ALARM_TIME_HOUR_DEFAULT = 0;
	public static final String PREF_ALARM_TIME_MINUTE = "Pref_AlarmTime_Minute";
	public static final int PREF_ALARM_TIME_MINUTE_DEFAULT = 42;
	// Days
	public static final String PREF_SUNDAY_IS_SET = "Pref_SundayIsSet";
	public static final String PREF_MONDAY_IS_SET = "Pref_MondayIsSet";
	public static final String PREF_TUESDAY_IS_SET = "Pref_TuesdayIsSet";
	public static final String PREF_WEDNESDAY_IS_SET = "Pref_WednesdayIsSet";
	public static final String PREF_THURSDAY_IS_SET = "Pref_ThursdayIsSet";
	public static final String PREF_FRIDAY_IS_SET = "Pref_FridayIsSet";
	public static final String PREF_SATURDAY_IS_SET = "Pref_SaturdayIsSet";
	public static final String PREF_ALLDAYS_IS_SET = "Pref_AllDaysIsSet";
	// Intent
	public static final String INTENT_FILTER_NAME = "com.apa42.desconecta.ISTIME";
	// Notification Bar
	public static final int NOTIFICATION_DESACTIVAR_ID = 42;
	// Time to wait if cell is in use when Alarm Activate
	public static final int TIME_TO_POSTPONE = 10; // minutes
}

