package com.example.location;

import java.util.List;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationData.Builder;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class Showmap extends Activity{
	
	private MapView mapview;
	private BaiduMap baidumap;
	private LocationManager locationmanager;
	private String provider;
	private boolean isFirstLocate = true;
	private Location location;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	SDKInitializer.initialize(getApplicationContext());
	setContentView(R.layout.map_show);
	mapview = (MapView) findViewById(R.id.mapview);
	baidumap = mapview.getMap();
	baidumap.setMyLocationEnabled(true);
	locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	//获取所有的位置提供器
			List<String> providerlist = locationmanager.getProviders(true);
			if(providerlist.contains(LocationManager.NETWORK_PROVIDER)){
				provider = LocationManager.NETWORK_PROVIDER;
			}else if(providerlist.contains(LocationManager.GPS_PROVIDER)){
				provider = LocationManager.GPS_PROVIDER;
			}else{
				Toast.makeText(this, "No location provider", Toast.LENGTH_SHORT).show();
				return;
			}
		location = locationmanager.getLastKnownLocation(provider);
		if(location != null){
			showLocation(location);
		}
		locationmanager.requestLocationUpdates(provider, 5000, 1, listener);
			
}
    private void showLocation(Location location) {
    	if(isFirstLocate){
    		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    		MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
    		baidumap.animateMapStatus(update);
    		update = MapStatusUpdateFactory.zoomTo(16f);
    		baidumap.animateMapStatus(update);
    		isFirstLocate = false;
    	}
    	MyLocationData.Builder builder = new MyLocationData.Builder();
    	builder.latitude(location.getLatitude());
    	builder.longitude(location.getLongitude());
    	MyLocationData locationData = builder.build();
    	baidumap.setMyLocationData(locationData);
    	
    }
    	
    	LocationListener listener = new LocationListener() {	
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}
			@Override
			public void onProviderEnabled(String provider) {
			}
			@Override
			public void onProviderDisabled(String provider) {
			}
			@Override
			public void onLocationChanged(Location location) {
				if(location != null){
					showLocation(location);
				}
			}
		};
		
	@Override
     protected void onDestroy() {
	super.onDestroy();
	baidumap.setMyLocationEnabled(false);
	mapview.onDestroy();
	if(locationmanager != null){
		locationmanager.removeUpdates(listener);
	}
     }
     @Override
    protected void onPause() {
	super.onPause();
	mapview.onPause();
}
    @Override
    protected void onResume() {
	super.onResume();
	mapview.onResume();
}
}
