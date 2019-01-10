/*
 * Copyright © 2018. Dmitry Starkin Contacts: t0506803080@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the «License»);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * //www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an «AS IS» BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.starsoft.serviseutil.alarmutil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * A helper class for launching services or receiver
 * using Alarm Manager
 * on different versions of Android
 *
 * @version 1.0
 * See <a href="Source">https://habr.com/post/274169/</a>
 */
public class AlarmManagerUtil {
    
    /**
     * Setup the Alarm Manager task
     * at the expiration of the time interval
     *
     * @param alarmManager The instance of Alarm Manager
     * @param pi           An PendingIntent describing the receiver to be started.
     * @param timeInterval The time through which will run the receiver, milliseconds
     * @return Returns a time when it was installed, milliseconds, time since January 1 1970
     */
    
    @SuppressWarnings("all")
    protected static long setUpAlarm(AlarmManager alarmManager, PendingIntent pi, long timeInterval) {
        
        long curTime;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo((curTime = System.currentTimeMillis()) + timeInterval, pi);
            alarmManager.setAlarmClock(alarmClockInfo, pi);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, (curTime = System.currentTimeMillis()) + timeInterval, pi);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, (curTime = System.currentTimeMillis()) + timeInterval, pi);
        }
        return curTime;
    }
    
    /**
     * Checks whether a deferred task to run the receiver
     *
     * @param context    The Context in which this PendingIntent should start
     * @param intent     An Intent describing the receiver to be started.
     * @param identifier Unique identifier
     * @return Returns true if the receiver exists and is not already running, otherwise false
     */
    public static synchronized boolean isServiceAlarmOn(Context context, Intent intent, int identifier) {
        
        PendingIntent pi = PendingIntent.getService(context, identifier, intent, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }
    
    /**
     * Checks whether a deferred task to run the service
     *
     * @param context    The Context in which this PendingIntent should start
     * @param intent     An Intent describing the service to be started.
     * @param identifier Unique identifier
     * @return Returns true if the service exists and is not already running, otherwise false
     */
    public static synchronized boolean isBroadcastAlarmOn(Context context, Intent intent, int identifier) {
        
        PendingIntent pi = PendingIntent.getBroadcast(context, identifier, intent, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }
    
    /**
     * Starts the broadcast service
     * at the expiration of the time interval
     * previous run cancelled
     *
     * @param context      The Context in which this PendingIntent should start
     * @param intent       An Intent describing the receiver to be started.
     * @param timeInterval The time through which will run the receiver, milliseconds
     * @param identifier   Unique identifier
     * @return Returns a time when it was installed, milliseconds, time since January 1 1970
     */
    public static synchronized long setBroadcastAlarm(Context context, Intent intent, long timeInterval, int identifier) {
        
        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final PendingIntent pi = PendingIntent.getBroadcast(context, identifier, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return setUpAlarm(am, pi, timeInterval);
    }
    
    /**
     * Starts the service
     * at the expiration of the time interval
     * previous run cancelled
     *
     * @param context      The Context in which this PendingIntent should start
     * @param intent       An Intent describing the service to be started.
     * @param timeInterval The time through which will run the service, milliseconds
     * @param identifier   Unique identifier
     * @return Returns a time when it was installed, milliseconds, time since January 1 1970
     */
    public static synchronized long setServiceAlarm(Context context, Intent intent, long timeInterval, int identifier) {
        
        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final PendingIntent pi = PendingIntent.getService(context, identifier, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return setUpAlarm(am, pi, timeInterval);
    }
    
    /**
     * Cancel Starts the item
     * if it is not started
     *
     * @param context    The Context in which this PendingIntent should start
     * @param intent     An Intent describing the item to be started.
     * @param identifier Unique identifier
     */
    public static synchronized void cancelAlarm(Context context, Intent intent, int identifier) {
        
        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final PendingIntent pi = PendingIntent.getService(context, identifier, intent, PendingIntent.FLAG_NO_CREATE);
        if (pi != null && am != null) {
            am.cancel(pi);
            pi.cancel();
        }
    }
}
