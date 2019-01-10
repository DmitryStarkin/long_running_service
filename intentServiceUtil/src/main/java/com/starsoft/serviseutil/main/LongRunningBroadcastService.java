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

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;

/**
 * Abstract class to create an intent service with CPU WakeLock control
 * this service always runs as NOT STICKY and holding the lock CPU
 * while doing the tasks (intents).
 * Several services share the lock until the are complete.
 * this class acquires a wake lock the CPU in the method onCreate()
 * and releases it after perform all intents
 * <p>
 * Inherit from this abstract class
 * and implement them single method handleIntent,
 * perform long running operations in it.
 * Your service must have a default constructor
 * and call the superclass constructor and pass it the name.
 * <p>
 * public MyService() {
 * <p>
 * super("MyService");
 * }
 *
 * @version 1.0
 */

public abstract class LongRunningBroadcastService extends IntentService {
    
    /**
     * Creates an IntentService  with WakeLock control
     * Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LongRunningBroadcastService(String name) {
        
        super(name);
    }
    
    /**
     * Override this method for
     * Perform long running operations in it.
     * This is executed in a separate thread.
     *
     * @param intent - the intent which was used to launch
     * @return Returns optionally, you can return anything
     */
    @SuppressWarnings("all")
    protected abstract boolean handleIntent(Intent intent);
    
    /**
     * if you override this method you should call super.onCreate();
     */
    @Override
    public void onCreate() {
        
        super.onCreate();
        WakeLockManager.create(this.getApplicationContext());
        WakeLockManager.registerAsClient();
    }
    
    @Override
    final public int onStartCommand(Intent intent, int flag, int startId) {
        
        super.onStart(intent, startId);
        WakeLockManager.enterWakeLock();
        return Service.START_NOT_STICKY;
    }
    
    /**
     * if you override this method you should call super.onDestroy();
     */
    @Override
    public void onDestroy() {
        
        super.onDestroy();
        WakeLockManager.unRegisterAsClient();
    }
    
    @Override
    final protected void onHandleIntent(Intent intent) {
        
        try {
            Intent broadcastIntent = intent.getParcelableExtra("original_intent");
            if (broadcastIntent != null) {
                handleIntent(broadcastIntent);
            } else {
                handleIntent(intent);
            }
        } finally {
            WakeLockManager.leaveWakeLock();
        }
    }
}
