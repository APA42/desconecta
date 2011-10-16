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
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;


public class Utils 
{
	// for logs
	private static final String CLASS_NAME = "com.apa42.desconecta.Utils"; //getClass().getName();
	
	public static boolean is24Hour(Context context)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "is24Hour(Context context)");
		// Check if time is set to 24H or not
		// 
		boolean returned = false;
		String twentyFourHour = Settings.System.getString(context.getContentResolver(), Settings.System.TIME_12_24);
		if (null == twentyFourHour )
		{
			// Don't know why but returns null unless user set time format =>
			// Work around, use DateForma from android.text.format don't use java.text.DateFormat
			// Don't use import => qualified name
			returned = android.text.format.DateFormat.is24HourFormat(context);
			if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "Settings.System.TIME_12_24 return NULL");
		}
		else
		{
			returned = (! TextUtils.isEmpty(twentyFourHour) &&  twentyFourHour.contains("24") );
		}
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "is24Hour=> "+returned);
		return returned;
	}

	/*
	 * 
	 * AIRPLANE MODE
	 * -------------
	 */
	//http://dustinbreese.blogspot.com/2009/04/andoid-controlling-airplane-mode.html
	//http://www.mail-archive.com/android-beginners@googlegroups.com/msg06083.html
	private static boolean isAirplaneModeOn(Context context)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "isAirplaneModeOn(Context context)");
		//
		return Settings.System.getInt(context.getContentResolver(),Settings.System.AIRPLANE_MODE_ON, 0) != 0;
	}
	
	//http://dustinbreese.blogspot.com/2009/04/andoid-controlling-airplane-mode.html
	//http://www.mail-archive.com/android-beginners@googlegroups.com/msg06083.html
    public static void setAirplaneMode(Context context,boolean status)
    {
    	if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "setAirplaneMode(Context context,boolean status)");
    	//
    	boolean isAirplaneModeOn = isAirplaneModeOn(context); 
        
    	if(isAirplaneModeOn && status) 
        { 
    		return; 
        } 
        if(!isAirplaneModeOn && !status) 
        { 
        	return; 
        } 
        if(isAirplaneModeOn && !status) 
        {
        	Settings.System.putInt(context.getContentResolver(),Settings.System.AIRPLANE_MODE_ON, 0);
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra("state", 0); 
            context.sendBroadcast(intent); 
            return; 
        } 
        if(!isAirplaneModeOn && status) 
        {
        	Settings.System.putInt(context.getContentResolver(),Settings.System.AIRPLANE_MODE_ON, 1); 
            Intent intent = new Intent (Intent.ACTION_AIRPLANE_MODE_CHANGED); 
            intent.putExtra("state", 1); 
            context.sendBroadcast(intent);
            return; 
        }
    } 

    /*
     * NOTIFICATIONS
     * -------------
     */
	public static void createNotification(Context context,Calendar AlarmCalendar)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "createNotification(Context context,Calendar AlarmCalendar)" );
		//
		try
		{
		
			// Format Date and Time, get them from device
			//http://stackoverflow.com/questions/6981505/android-get-user-selected-date-format
			java.text.DateFormat dateFormat;
			String format = Settings.System.getString(context.getContentResolver(), Settings.System.DATE_FORMAT);
			if (TextUtils.isEmpty(format)) 
				dateFormat = android.text.format.DateFormat.getDateFormat(context);
			else 
			  dateFormat = new java.text.SimpleDateFormat(format);
			// Time
			int Minute = AlarmCalendar.get(Calendar.MINUTE);
			String DateTimeMessage = dateFormat.format(AlarmCalendar.getTime());
			DateTimeMessage += " ";
			
			int Hour;
			int AM_PM;
			if ( is24Hour(context) )
			{
				Hour = AlarmCalendar.get(Calendar.HOUR_OF_DAY);
				DateTimeMessage += pad(Hour) + ":" + pad(Minute);
			}
			else
			{
				Hour = AlarmCalendar.get(Calendar.HOUR);
				AM_PM = AlarmCalendar.get(Calendar.AM_PM);
				if ( AM_PM == Calendar.AM )
					DateTimeMessage += pad(Hour) + ":" + pad(Minute) + " " + context.getResources().getString(R.string.TIME_FORMAT_AM);
				else
					DateTimeMessage += pad(Hour) + ":" + pad(Minute) + " " + context.getResources().getString(R.string.TIME_FORMAT_PM);
			}
			
			String AlarmMessage = context.getResources().getString(R.string.NotificationText) + " " + DateTimeMessage;
			NotificationManager NotifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			//
			Notification notification = new Notification(R.drawable.ic_action_desconecta, AlarmMessage,0);
			
		    Intent intentNoteWindow = new Intent(context, DesconectaActivity.class);
		    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intentNoteWindow, 0);
		    
		    notification.setLatestEventInfo(context, context.getResources().getString(R.string.app_name), AlarmMessage, contentIntent);
		    notification.flags = Notification.FLAG_NO_CLEAR;
		    
		    NotifManager.notify(ConfigAppValues.NOTIFICATION_DESACTIVAR_ID, notification);
		}
		catch(Exception e)
		{
			if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "createNotification(Context context,Calendar AlarmCalendar) FAILS: " + e.getMessage());    		
			e.printStackTrace();
		}
	}

    public static void destroyNotification(Context context)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "destroyNotification(Context context)" );
		//
		try
		{
			NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(ConfigAppValues.NOTIFICATION_DESACTIVAR_ID);
		}
		catch(Exception e)
		{
			if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "destroyNotification(Context context) FAILS: " + e.getMessage());    		
			e.printStackTrace();
		}
	}

    /*
     * String
     * ------
     */
	public static String pad(int c) 
	{
	    if (c >= 10)
	        return String.valueOf(c);
	    else
	        return "0" + String.valueOf(c);
	}

	/*
	 * Alarm
	 * -----
	 */
	private static void setAlarmAirplaneMode(Context context,Calendar AlarmCalendar)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "setAlarmAirplaneMode(Context context,Calendar AlarmCalendar)" );
		//
		try
		{
			// With intent-filters
			Intent intentFilter = new Intent(ConfigAppValues.INTENT_FILTER_NAME);
			PendingIntent sender = PendingIntent.getBroadcast(context, 0, intentFilter, PendingIntent.FLAG_UPDATE_CURRENT);
			// Register the alarm    	 
			String AlarmsService = Context.ALARM_SERVICE;
			AlarmManager am = (AlarmManager) context.getSystemService(AlarmsService);
			am.set(AlarmManager.RTC_WAKEUP,AlarmCalendar.getTimeInMillis(),sender);
			createNotification(context,AlarmCalendar);
		}
      catch(Exception e)
      {
      	if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "setAlarmAirplaneMode(Context context,Calendar AlarmCalendar) FAILS: " + e.getMessage());    		
  		e.printStackTrace();
      }
	}

	public static void cancelAlarmAirplaneMode(Context context)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "cancelAlarmAirplaneMode(Context context)" );
		//
		// http://stackoverflow.com/questions/4963271/android-alarmmanager-problem-with-setting-resetting-an-alarm
		try
		{
			String AlarmsService = Context.ALARM_SERVICE;
			AlarmManager am = (AlarmManager) context.getSystemService(AlarmsService);	
			Intent intentFilter = new Intent(ConfigAppValues.INTENT_FILTER_NAME);
			PendingIntent sender = PendingIntent.getBroadcast(context, 0, intentFilter, PendingIntent.FLAG_CANCEL_CURRENT);
			am.cancel(sender);
			sender.cancel();
			//
			Utils.destroyNotification(context);
		}
      catch(Exception e)
      {
      	if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "cancelAlarmAirplaneMode(Context context) FAILS: " + e.getMessage());    		
  		e.printStackTrace();
      }

	}

	public static boolean isAlarmAirplaneModeSet(Context context)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "isAlarmAirplaneModeSet(Context context)" );
		//
		// http://stackoverflow.com/questions/4963271/android-alarmmanager-problem-with-setting-resetting-an-alarm
		boolean returned = false;
		try
		{
			Intent intentFilter = new Intent(ConfigAppValues.INTENT_FILTER_NAME);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentFilter, PendingIntent.FLAG_NO_CREATE);
			returned = (pendingIntent != null);
		}
		catch(Exception e)
		{
			if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "isAlarmAirplaneSet(Context context) FAILS: " + e.getMessage());    		
			e.printStackTrace();
		}
		return returned;
	}

	
	public static void postponeAlarmAirplaneMode(Context context)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "postpone10minutes()");
		//
		// Phone is in use => postpone AirPlanemode for 10 minutes
		Calendar CalendarAux = Calendar.getInstance();
		CalendarAux.setTimeInMillis(System.currentTimeMillis()); // @@ I've to do this??????
		CalendarAux.add(Calendar.MINUTE, ConfigAppValues.TIME_TO_POSTPONE);
		setAlarmAirplaneMode(context, CalendarAux);
		
	}
	public static void calculateNextAlarm(Context context, boolean FirsTimeOrBoot)
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "calculateNextAlarm(Context context, boolean FirsTimeOrBoot)" );
		//
		try
		{
			cancelAlarmAirplaneMode(context);
			
			/*
			 * Create structure from Days
			 */
			SharedPreferences sharedPreferences = context.getSharedPreferences(ConfigAppValues.PREFERENCES_DESCONECTA, ConfigAppValues.PREFERENCES_MODE);
			boolean[] DiasActivados = new boolean[7];

			DiasActivados[0] = sharedPreferences.getBoolean(ConfigAppValues.PREF_SUNDAY_IS_SET,false); 
			DiasActivados[1] = sharedPreferences.getBoolean(ConfigAppValues.PREF_MONDAY_IS_SET,false); 
			DiasActivados[2] = sharedPreferences.getBoolean(ConfigAppValues.PREF_TUESDAY_IS_SET,false);
			DiasActivados[3] = sharedPreferences.getBoolean(ConfigAppValues.PREF_WEDNESDAY_IS_SET,false);
			DiasActivados[4] = sharedPreferences.getBoolean(ConfigAppValues.PREF_THURSDAY_IS_SET,false);
			DiasActivados[5] = sharedPreferences.getBoolean(ConfigAppValues.PREF_FRIDAY_IS_SET,false);
			DiasActivados[6] = sharedPreferences.getBoolean(ConfigAppValues.PREF_SATURDAY_IS_SET,false);

			/*
			 * Get actual day and time
			 */
			Calendar CalendarAux = Calendar.getInstance();
			CalendarAux.setTimeInMillis(System.currentTimeMillis()); // @@ I've to do this??????
			int Today = CalendarAux.get(Calendar.DAY_OF_WEEK);
			int Hour = CalendarAux.get(Calendar.HOUR_OF_DAY);
			int Minutes = CalendarAux.get(Calendar.MINUTE);

			/*
			 * Find out Today which index it is for our array
			 */
			int OurIndex = 0;
			switch(Today)
			{
				case Calendar.SUNDAY:		OurIndex = 0;	break;
				case Calendar.MONDAY:		OurIndex = 1; 	break;
				case Calendar.TUESDAY:		OurIndex = 2; 	break;
				case Calendar.WEDNESDAY:	OurIndex = 3; 	break;
				case Calendar.THURSDAY:		OurIndex = 4;	break;
				case Calendar.FRIDAY:		OurIndex = 5; 	break;
				case Calendar.SATURDAY:		OurIndex = 6;	break;
			}
			
			int Alarm_hour = sharedPreferences.getInt(ConfigAppValues.PREF_ALARM_TIME_HOUR, ConfigAppValues.PREF_ALARM_TIME_HOUR_DEFAULT);
			int Alarm_minute = sharedPreferences.getInt(ConfigAppValues.PREF_ALARM_TIME_MINUTE, ConfigAppValues.PREF_ALARM_TIME_MINUTE_DEFAULT);
			
			/*
			 * Calculate when is the next alarm or if it's the first time/boot device
			 */
			boolean AlarmCreated = false;
			int PosDay = OurIndex;
			int HowManyDaysToAdd = 0;
			
			if ( !FirsTimeOrBoot ) 
			{
				// Alarm already activated at least once today=>Add one day
				PosDay = (PosDay == 6) ? 0 : PosDay+1;
				HowManyDaysToAdd++;
			}
			// Special case same day but early than the selected hour/minute
			if  ( (PosDay == OurIndex) && ( Alarm_hour < Hour || (Alarm_hour == Hour && Alarm_minute <= Minutes) ) )
			{
				// Add one day
				PosDay = (PosDay == 6) ? 0 : PosDay+1;
				HowManyDaysToAdd++;
			}
			// @@ Can get ANR => check again
			while(!AlarmCreated)
			{
				if ( DiasActivados[PosDay] )
				{
					AlarmCreated = true;
				}
				else
				{
					PosDay = (PosDay == 6) ? 0 : PosDay+1;
					HowManyDaysToAdd++;
				}
			}
			
			/*
			 * Create the alarm
			 */
			Calendar AlarmCalendar = Calendar.getInstance();
			AlarmCalendar.setTimeInMillis(System.currentTimeMillis());
			// Calculate the day from now
			AlarmCalendar.add(Calendar.DAY_OF_YEAR,HowManyDaysToAdd);
			if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "==>calculateNextAlarm => Adding " + String.valueOf(HowManyDaysToAdd) + " Days");
			AlarmCalendar.set(Calendar.HOUR_OF_DAY, Alarm_hour);
			AlarmCalendar.set(Calendar.MINUTE, Alarm_minute);
			AlarmCalendar.set(Calendar.SECOND, 0);
			AlarmCalendar.set(Calendar.MILLISECOND, 0);
			
			setAlarmAirplaneMode(context, AlarmCalendar);
		}
		catch(Exception e)
		{
			if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "calculateNextAlarm(Context context, boolean FirsTimeOrBoot) FAILS: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
