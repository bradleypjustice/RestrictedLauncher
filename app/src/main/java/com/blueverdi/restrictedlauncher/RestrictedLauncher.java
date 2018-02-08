package com.blueverdi.restrictedlauncher;



import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.UserManager;
import android.widget.TextView;
import android.widget.Toast;

public class RestrictedLauncher extends Activity {
	public static final String LAUNCH_PACKAGE = "com.blueverdi.launchpackage";
	public static final String LAUNCH_ACTIVITY = "com.blueverdi.launchactivity";
	private String packageName = "";
	private String launchActivity = "";
	private Bundle restrictions = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		android.os.Debug.waitForDebugger();	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restricted_launcher);		
		restrictions =  ((UserManager) getSystemService(Context.USER_SERVICE)).getApplicationRestrictions(getPackageName());
	}	
		
	
	@Override 
	protected void onResume() {
		super.onResume();
		TextView pn = (TextView) findViewById(R.id.autorun_package);
		pn.setText(packageName);
		pn = (TextView) findViewById(R.id.status);
		boolean successful = false;
		if (restrictions == null) {
			// according to online doc we should be here for owner - not true
			pn.setText(getResources().getString(R.string.disabled));
			
		}
		else {
			launchActivity = restrictions.getString(LAUNCH_ACTIVITY, "");
			if (launchActivity.length() == 0) {
				pn.setText(getResources().getString(R.string.disabled));

			}
			else {
				successful = true;
				pn.setText(getResources().getString(R.string.enabled));			
				packageName = restrictions.getString(LAUNCH_PACKAGE,"");
				launchActivity = restrictions.getString(LAUNCH_ACTIVITY);
			    ComponentName component = new ComponentName(packageName, launchActivity);
			    Intent i = Intent.makeMainActivity(component);
	        	startActivity(i);
			}
		}
		if (!successful) {
	   		Toast.makeText(this,getString(R.string.misconfigured_system), 
	                Toast.LENGTH_LONG).show();
	   		
	   		PackageManager pm = getPackageManager();
	
	        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
	        mainIntent.addCategory(Intent.CATEGORY_HOME);
	        List<ResolveInfo> appList = pm.queryIntentActivities(mainIntent, 0);
	        for (ResolveInfo la : appList) {
	        	String packageName = la.activityInfo.packageName;
	        	if (packageName.contains("com.android"))  {
	        		Intent i = new Intent();
	        		i.setClassName(packageName, la.activityInfo.name);
	        		startActivity(i);
	        	}
	        }		   		
		}
	}
	
	@Override 
	public void onBackPressed() {
		
	}
}
