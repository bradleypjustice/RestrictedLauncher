package com.blueverdi.restrictedlauncher;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
 
public class AboutActivity extends Activity {

	private static final boolean FOR_V_V = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        if (FOR_V_V) {
            setContentView(R.layout.activity_about_vv);     	
        }
        else {
            setContentView(R.layout.activity_about);      	
        }
      }

    public void aboutDoClick (View view) {
    	
    	switch(view.getId()) {
    	     			
    		case R.id.exitAboutButton: 
    			finish();
    			break;
     	}
    }
 }
