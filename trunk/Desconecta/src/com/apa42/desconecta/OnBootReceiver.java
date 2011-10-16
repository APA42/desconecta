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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver 
{
	// for logs
	private final String CLASS_NAME = getClass().getName();

	@Override
	public void onReceive(Context arg0, Intent arg1) 
	{
		if (ConfigAppValues.DEBUG) Log.d(CLASS_NAME, "onReceive(Context context, Intent intent)");
		//
		try
		{
			SharedPreferences sharedPreferences = arg0.getSharedPreferences(ConfigAppValues.PREFERENCES_DESCONECTA, ConfigAppValues.PREFERENCES_MODE);
			boolean hasToActivate = sharedPreferences.getBoolean(ConfigAppValues.PREF_ALARM_IS_SET,false);
			if ( hasToActivate ) 
				Utils.calculateNextAlarm(arg0, true);
		}
		catch (Exception e)
		{
			if (ConfigAppValues.DEBUG) Log.e(CLASS_NAME, "onReceive(Context arg0, Intent arg1) FAILS: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
