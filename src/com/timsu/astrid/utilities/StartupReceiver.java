package com.timsu.astrid.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class StartupReceiver extends BroadcastReceiver {

    private static boolean hasStartedUp = false;

    @Override
    /** Called when the system is started up */
    public void onReceive(Context context, Intent intent) {
        Notifications.scheduleAllAlarms(context);
    }

    /** Called when this application is started up */
    public static void onStartupApplication(final Context context) {
        if(hasStartedUp)
            return;

        int latestSetVersion = Preferences.getCurrentVersion(context);
        int version = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo("com.timsu.astrid", 0);
            version = pi.versionCode;
        } catch (Exception e) {
            Log.e("StartupAstrid", "Error getting version!", e);
        }

        // if we just got upgraded, set the alarms
        boolean justUpgraded = latestSetVersion != version;
        final int finalVersion = version;
        if(justUpgraded) {
            new Thread(new Runnable() {
                public void run() {
                    Notifications.scheduleAllAlarms(context);

                    // do this after all alarms are scheduled, so if we're
                    // interrupted, the thread will resume later
                    Preferences.setCurrentVersion(context, finalVersion);
                }
            }).start();
        }

        Preferences.setPreferenceDefaults(context);

        hasStartedUp = true;
    }
}
