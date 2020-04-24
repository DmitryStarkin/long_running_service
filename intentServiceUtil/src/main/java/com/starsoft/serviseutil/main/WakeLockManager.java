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

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;

/**
 * A helper class to manage the WakeLock of CPU
 * This class obtain Wake Lock as PARTIAL_WAKE_LOCK
 *
 * @version 1.0
 * @see <a href="https://developer.android.com/topic/performance/vitals/wakelock">https://developer.android.com/topic/performance/vitals/wakelock</a>
 */

class WakeLockManager {
    
    static final int RELEASE_WAKELOCK = 1;
    private static final long RELEASE_WAKELOCK_IDLE_TIME = 180000;
    private static WakeLockManager sWakeLockManager = null;
    private final String TAG = getClass().getSimpleName();
    private Handler mHandler;
    private int countWakeLock;
    private PowerManager.WakeLock mWakeLock;
    private int clientServiceCount = 0;
    
    private WakeLockManager(Context context) {
        
        mWakeLock = this.createWakeLock(context);
    }
    
    static synchronized WakeLockManager getInstance() {
        
        if (sWakeLockManager == null) {
            throw new RuntimeException("You need to create WakeLockManager first");
        }
        return sWakeLockManager;
    }
    
    @SuppressWarnings("all")
    static synchronized WakeLockManager create(Context context, long idleTime) {
        
        if (sWakeLockManager == null) {
            sWakeLockManager = new WakeLockManager(context);
            sWakeLockManager.acquireWakeLock();
            sWakeLockManager.startTimer(idleTime);
        }
        
        return sWakeLockManager;
    }
    
    static synchronized WakeLockManager create(Context context) {
        
        return create(context, RELEASE_WAKELOCK_IDLE_TIME);
    }
    
    private void startTimer(long idleTime) {
        
        if (mHandler == null) {
            mHandler = new TimerHandler();
        }
        mHandler.removeMessages(RELEASE_WAKELOCK);
        Message message = mHandler.obtainMessage(RELEASE_WAKELOCK, this);
        mHandler.sendMessageDelayed(message, idleTime);
    }
    
    private void stopTimer() {
        
        if (mHandler != null) {
            mHandler.removeMessages(RELEASE_WAKELOCK);
            mHandler = null;
        }
    }
    
    private synchronized boolean isCreate() {
        
        return sWakeLockManager != null;
    }
    
    @SuppressWarnings("all")
    synchronized int enterWakeLock() {
        
        stopTimer();
        return enter();
    }
    
    @SuppressWarnings("all")
    synchronized int leaveWakeLock() {
        
        return leave();
    }
    
    /**
     * Releases all locks and resets the Manager
     * usually never need to call this method
     */
    @SuppressWarnings("all")
    synchronized void killWakeLockManager() {
        
        if (isCreate()) {
            emptyWakeLockManager();
        }
    }
    
    synchronized void registerAsClient() {
        
        registerClientService();
    }
    
    synchronized void unRegisterAsClient() {
        
        unRegisterClientService();
    }
    
    /**
     * returns the current number of locks obtained
     *
     * @return current number of locks obtained, int
     */
    public synchronized int getCountWakeLock() {
        
        return countWakeLock;
    }
    
    private void assertSetup() {
        
        if (!isCreate()) {
            throw new RuntimeException("You need to create WakeLockManager first");
        }
    }
    
    private synchronized int enter() {
        
        countWakeLock++;
        return countWakeLock;
    }
    
    private int leave() {
        
        if (countWakeLock == 0) {
            return countWakeLock;
        }
        countWakeLock--;
        if (countWakeLock == 0) {
            releaseWakeLock();
        }
        return countWakeLock;
    }
    
    @SuppressWarnings("all")
    private void acquireWakeLock() {
        
        mWakeLock.acquire();
    }
    
    private void releaseWakeLock() {
        
        if (mWakeLock.isHeld()) {
            
            mWakeLock.release();
        }
    }
    
    private PowerManager.WakeLock createWakeLock(Context context) {
        
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        assert powerManager != null;
        return powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
    }
    
    @SuppressWarnings("all")
    private int registerClientService() {
        
        return ++clientServiceCount;
    }
    
    @SuppressWarnings("all")
    private int unRegisterClientService() {
        
        if (clientServiceCount == 0) {
            
            return clientServiceCount;
        }
        clientServiceCount--;
        if (clientServiceCount == 0) {
            emptyWakeLockManager();
        }
        return clientServiceCount;
    }
    
    private void emptyWakeLockManager() {
        
        countWakeLock = 0;
        releaseWakeLock();
        sWakeLockManager = null;
    }
}
