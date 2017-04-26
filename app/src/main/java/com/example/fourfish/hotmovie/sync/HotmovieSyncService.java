package com.example.fourfish.hotmovie.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class HotmovieSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static HotmovieSyncAdapter sHotmovieSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("HotmovieSyncService", "onCreate - HotmovieSyncService");
        synchronized (sSyncAdapterLock) {
            if (sHotmovieSyncAdapter == null) {
                sHotmovieSyncAdapter = new HotmovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sHotmovieSyncAdapter.getSyncAdapterBinder();
    }
}