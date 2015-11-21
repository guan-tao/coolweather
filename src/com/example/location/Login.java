package com.example.location;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Login extends Activity implements OnClickListener {
	private Button showmap;
	private Button showshake;
	private Button showxml;
	private Button showgps;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		showmap = (Button) findViewById(R.id.showmap);
		showshake = (Button) findViewById(R.id.showshake);
		showxml = (Button) findViewById(R.id.showxml);
		showgps = (Button) findViewById(R.id.gps_test);
		showmap.setOnClickListener(this);
		showshake.setOnClickListener(this);
		showxml.setOnClickListener(this);
		showgps.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.showmap:
			Intent intent1 = new Intent(Login.this, Showmap.class);
			startActivity(intent1);
			break;
        case R.id.showshake:
        	Intent intent2 = new Intent(Login.this, Sensor.class);
        	startActivity(intent2);
			break;
        case R.id.showxml:
        	Intent intent3 = new Intent(Login.this, Return.class);
        	startActivity(intent3);
	        break;
        case R.id.gps_test:
        	Intent intent4 = new Intent(Login.this, GPSTest.class);
        	startActivity(intent4);
	        break;


		default:
			break;
		}
		
	}

	
}
