package com.example.nikolina.restaurantappreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.awareness.fence.FenceState;

public class FenceReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        FenceState fenceState = FenceState.extract(intent);

        switch(fenceState.getCurrentState()) {
            case FenceState.TRUE:
                Toast.makeText(context, "Stigli ste na lokaciju.", Toast.LENGTH_SHORT).show();
                Log.e("TAG", "Stigli ste na lokaciju.");
                break;
            case FenceState.FALSE:
                break;
            case FenceState.UNKNOWN:
                break;
        }
    }

}
