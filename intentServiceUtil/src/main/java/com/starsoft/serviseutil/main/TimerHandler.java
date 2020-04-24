package com.starsoft.serviseutil.main;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Copyright © 2019 Dmitry Starkin. All rights reserved. Contacts: t0506803080@gmail.com.
 */

//This File Created at 16.01.2019 11:32.
class TimerHandler extends Handler {
    
    TimerHandler() {
        
        super(Looper.getMainLooper());
    }
    
    @Override
    public void handleMessage(Message msg) {
        
        if (msg.what == WakeLockManager.RELEASE_WAKELOCK) {
            ((WakeLockManager) msg.obj).killWakeLockManager();
        }
    }
}
