package com.blueverdi.restrictedlauncher;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class HelpActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.activity_help);
     }

    public void helpDoClick (View view) {
    	
    	switch(view.getId()) {
    	     			
    		case R.id.exitAboutButton: 
    			finish();
    			break;
     	}
    }

}
