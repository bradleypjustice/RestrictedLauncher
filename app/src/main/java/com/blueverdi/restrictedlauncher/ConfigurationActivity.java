package com.blueverdi.restrictedlauncher;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.RestrictionEntry;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ConfigurationActivity extends Activity {

	private static final String APPLICATION_RATED = "applicationRated";
	private class PInfo {
	    public String appname = "";
	    public String pname = "";
	    public Drawable icon;
	}

	private ArrayList<PInfo> apps;
	private Context context;
	private MyCustomAdapter adapter;
	private Spinner appSpinner;
	private SharedPreferences mPrefs;
	private ProgressDialog pd;
    private Handler handler;
	
	private ArrayList<PInfo> getInstalledApps(boolean getSysPackages) {
	    ArrayList<PInfo> res = new ArrayList<PInfo>();        	    
	    List<ApplicationInfo> packages = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
	    PInfo newInfo = new PInfo();
	    newInfo.appname = getResources().getString(R.string.please_select);
	    res.add(newInfo);
	    PackageManager pm = getPackageManager();
	    for (ApplicationInfo packageInfo : packages) 
	    {
		     if(pm.getLaunchIntentForPackage(packageInfo.packageName)!= null &&   
		        (!pm.getLaunchIntentForPackage(packageInfo.packageName).equals("")))
		    {
			        newInfo = new PInfo();
			        newInfo.appname = packageInfo.loadLabel(getPackageManager()).toString();
			        newInfo.pname = packageInfo.packageName;
			        newInfo.icon = pm.getApplicationIcon(packageInfo);
			        res.add(newInfo);
		    
		    }
	    }
	    return res; 
	}

    private class ShowDialogAsyncTask extends AsyncTask<Void, Integer, Void>{
	    	 
	     @Override
	     protected void onPreExecute() {
	      // update the UI immediately after the task is executed
	    	super.onPreExecute();
	    	pd = new ProgressDialog(context);
			pd.setTitle(context.getText(R.string.please_wait));
			pd.setMessage(context.getText(R.string.searching_apps));
			pd.setCancelable(false);
			pd.setIndeterminate(true);
			pd.show();	       
	        
	     }
	     
        @Override
        protected Void doInBackground(Void... params) {          
    		apps = getInstalledApps(false);
    		Collections.sort(apps, new Comparator<PInfo>() {
    		    public int compare(PInfo result1, PInfo result2) {
    		        return result1.appname.compareTo(result2.appname);
    		    }
    		});   		
    		return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
         super.onPostExecute(result);
         pd.dismiss();
         Message msgObj = handler.obtainMessage();
         handler.sendMessage(msgObj);
          
        }
    } 
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		android.os.Debug.waitForDebugger();	
		super.onCreate(savedInstanceState); 
		context = this;
		setContentView(R.layout.activity_configuration);
		appSpinner = (Spinner)findViewById(R.id.spinner1);
		handler = new Handler() {	    	 
	        public void handleMessage(Message msg) {
	       		adapter = new MyCustomAdapter(context,R.layout.spinner_row, apps);
	    		appSpinner.setAdapter(adapter);
	    		appSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	                @Override
	                public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
	                	if (position != 0) {
	    	            	PInfo theApp = apps.get(position);
	    			   		Toast.makeText(context,theApp.appname + " " + context.getString(R.string.selected), 
	    		                    Toast.LENGTH_SHORT).show();

	    	    			returnAutolaunchInfo(theApp.pname, getPackageLaunchActivity(theApp.pname));			
	                	}
	                }
	                @Override
	                public void onNothingSelected(AdapterView<?> parentView) {
	                }

	            });	        	
	        }
		};
	    new ShowDialogAsyncTask().execute();
				
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.configuration, menu);
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean appRated = mPrefs.getBoolean(APPLICATION_RATED, false);
		if (appRated) {
	       menu.removeItem(R.id.action_rate);
		}
	    return super.onCreateOptionsMenu(menu);
	}

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_about:
				Intent ia = new Intent(this, AboutActivity.class);
				startActivity(ia);
                return true;
                
            case R.id.action_help:
				Intent ih = new Intent(this, HelpActivity.class);
				startActivity(ih);           	
            	return true;
            	
            case R.id.action_rate:
        		SharedPreferences.Editor editor = mPrefs.edit();
        		editor.putBoolean(APPLICATION_RATED, true);
        		editor.commit(); // Very important to save the preference 
                this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.blueverdi.restrictedlauncher")));
                return true;
            default:
            	return false;
        }
    }
	
	public String getPackageLaunchActivity(String packageName) {
		PackageManager pm = getPackageManager();
		Intent intent=pm.getLaunchIntentForPackage(packageName);
		return intent.getComponent().getClassName();
	}

	private void returnAutolaunchInfo(String packageName, String launchActivity) {
		
		Intent returnIntent = new Intent(getIntent());
        ArrayList<RestrictionEntry> newEntries = new ArrayList<RestrictionEntry>();
        RestrictionEntry entry = new RestrictionEntry(RestrictedLauncher.LAUNCH_PACKAGE, packageName);
        newEntries.add(entry);
        entry = new RestrictionEntry(RestrictedLauncher.LAUNCH_ACTIVITY, launchActivity);
        newEntries.add(entry);  
        returnIntent.putParcelableArrayListExtra(Intent.EXTRA_RESTRICTIONS_LIST, newEntries);		
		setResult(RESULT_OK,returnIntent); 
		finish();
	}

	public class MyCustomAdapter extends ArrayAdapter<String> {

	    LayoutInflater inflater;
	    ArrayList<PInfo> data;

		public MyCustomAdapter(Context context, int textViewResourceId, ArrayList objects) {
			super(context, textViewResourceId, objects);
		     inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		     data = objects;
		}
		
		
		@Override
		public View getDropDownView(int position, View convertView,
		ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}
		
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}
		
		
		public View getCustomView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.spinner_row, parent, false);
			TextView label = (TextView) row.findViewById(R.id.name);
			PInfo ourApp = (PInfo) data.get(position);
			label.setText(ourApp.appname);			
			ImageView icon = (ImageView) row.findViewById(R.id.icon);
			icon.setImageDrawable(ourApp.icon);
			return row;
		}
	}
		
				
	public void configureDoClick(View view) {
    	switch(view.getId()) {
			
		case R.id.cancelButton: 
			returnAutolaunchInfo(null,null);
			break;
 	}
		
	}
}
