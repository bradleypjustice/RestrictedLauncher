package com.blueverdi.restrictedlauncher;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.RestrictionEntry;
import android.os.Bundle;

public class GetRestrictionsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//		android.os.Debug.waitForDebugger();	 
        PendingResult result=goAsync();
        Bundle extras=new Bundle();
//        ArrayList<RestrictionEntry> newEntries = new ArrayList<RestrictionEntry>();
//        RestrictionEntry homeScreen = new RestrictionEntry(RestrictedUserWidget.ALLOW_HOME_SCREEN,false);
//        homeScreen.setTitle(context.getResources().getString(R.string.permit_home_screen));
//        homeScreen.setDescription(context.getResources().getString(R.string.permit_home_screen_description));
//        newEntries.add(homeScreen);
//        extras.putParcelableArrayList(Intent.EXTRA_RESTRICTIONS_INTENT, newEntries);
		Intent i = new Intent();
		i.setClass(context,ConfigurationActivity.class);
        extras.putParcelable(Intent.EXTRA_RESTRICTIONS_INTENT, i);
        ArrayList<RestrictionEntry> newEntries = new ArrayList<RestrictionEntry>();
        RestrictionEntry entry = new RestrictionEntry(RestrictedLauncher.LAUNCH_PACKAGE, "");
        entry.setSelectedState(false);
        newEntries.add(entry);
        entry = new RestrictionEntry(RestrictedLauncher.LAUNCH_ACTIVITY, "");
        entry.setSelectedState(false);
        newEntries.add(entry);       
        extras.putParcelableArrayList(Intent.EXTRA_RESTRICTIONS_LIST, newEntries);
        result.setResult(Activity.RESULT_OK, null, extras);
        result.finish();

    }
}
