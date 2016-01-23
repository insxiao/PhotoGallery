package xiao.android.photogallery.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import xiao.android.photogallery.service.PollService;

public class StartupReceiver extends BroadcastReceiver {
    public StartupReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isAlarmOn = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(PollService.PREF_IS_ALARM_ON, false);

        PollService.setServiceAlarm(context, isAlarmOn);

    }
}
