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

package com.starsoft.serviseutil.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Abstract class for creating long-running broadcast receivers
 * (by delegating the task to the intent service)
 * Inherit from the abstract class LongRunningReceiver and implement
 * the  method getServiceClass()
 * (You must return an object of class representative your service who will perform the task)
 */

public abstract class LongRunningReceiver extends BroadcastReceiver {
    
    private final String TAG = getClass().getSimpleName();
    
    @Override
    final public void onReceive(Context context, Intent intent) {
        
        WakeLockManager.create(context);
        startService(context, intent);
    }
    
    private void startService(Context context, Intent intent) {
        
        Intent serviceIntent = new Intent(context, getServiceClass());
        serviceIntent.putExtra("original_intent", intent);
        context.startService(serviceIntent);
    }
    
    /**
     * Override this method
     * to return an object of class,
     * which belongs to the class
     * nonSticky service.
     *
     * @return Returns an object of class representative your service
     * This service must inherit from {@link LongRunningBroadcastService}
     */
    public abstract Class<? extends LongRunningBroadcastService> getServiceClass();
}
