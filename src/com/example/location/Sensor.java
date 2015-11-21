package com.example.location;

import java.io.StringReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

     public class Sensor extends Activity{
    	 
    		public static final int SHOW = 0;
    		private TextView textshow;
    		private TextView yaoyi;
    		private LocationManager locationmanager;
    		private String provider;
    	    public Location location;
    	    private SensorManager sensormanager;
    	    private String address = "";
    @Override
     protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.sensor);
	    textshow = (TextView) findViewById(R.id.showlocation);
	    yaoyi = (TextView) findViewById(R.id.yaoyiyao);
	    sensormanager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//获取加速度传感器
		android.hardware.Sensor sensor = sensormanager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER);
		sensormanager.registerListener(sensorlistener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
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
//获取设备的位置
	location = locationmanager.getLastKnownLocation(provider);
	
//更新当前位置
	locationmanager.requestLocationUpdates(provider, 5000, 1, listener);	
    }
    @Override
	protected void onDestroy() {
		super.onDestroy();
		if(locationmanager !=null){
			locationmanager.removeUpdates(listener);
		}
		if(sensormanager !=null){
			sensormanager.unregisterListener(sensorlistener);
		}
	}
    private SensorEventListener sensorlistener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			float xValue = Math.abs(event.values[0]);
			float yValue = Math.abs(event.values[1]);
			float zValue = Math.abs(event.values[2]);
			if(xValue > 15||yValue > 15||zValue > 15){
				yaoyi.setText("Location is ");
				if(location != null){
					showLocation(location);
					Toast.makeText(Sensor.this,"当前的位置是："+address , Toast.LENGTH_SHORT).show();
				}
			}
			
		}
		@Override
		public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {	
		}
	};
	
	
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
		}
	};
	
	
	private void showLocation(final Location location) {
		new Thread(new Runnable() {
			public void run() {
				try {
					StringBuilder url = new StringBuilder();
					url.append("http://api.map.baidu.com/geocoder/v2/?ak=52a1BfFzwf60SBjKv626yMyj&callback=renderReverse&location=");
					url.append(location.getLatitude()).append(",");
					url.append(location.getLongitude());
					url.append("&output=xml&pois=1&mcode=90:25:77:F3:29:71:98:99:1E:94:55:E2:48:6E:07:26:D3:44:8B:D4;com.example.location");
					
					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(url.toString());
					httpGet.addHeader("Accept-languagetoString", "zh-CN");
					HttpResponse httpresponse = httpClient.execute(httpGet);
					if(httpresponse.getStatusLine().getStatusCode() == 200){
						HttpEntity entity = httpresponse.getEntity();
						String response = EntityUtils.toString(entity, "utf-8");

						XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
						XmlPullParser pullParser = factory.newPullParser();
						pullParser.setInput(new StringReader(response));
						int eventType = pullParser.getEventType();
			
						while(eventType!=XmlPullParser.END_DOCUMENT){
							String nodeName = pullParser.getName();
							Log.d("MainActivity", "nodename	is" + nodeName);
							switch(eventType){
							case XmlPullParser.START_TAG:{
								if("formatted_address".equals(nodeName)){
									address = pullParser.nextText();
								}
								break;
							}
							case XmlPullParser.END_TAG:{
								if("result".equals(nodeName)){
										Log.d("MainActivity", "address is " + address);	
							     }
								break;
							}
							default:
								break;
							}
							eventType = pullParser.next();
						}
							Message message = new Message();
							message.what = SHOW;
							message.obj = address;
							handler.sendMessage(message);
					    }
//				}
				}catch (Exception e) {
						e.printStackTrace();
				}
	       }
		}).start();
}

	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW:
				String hanyu = (String) msg.obj;
				textshow.setText(hanyu);
				break;
			default:
				break;
			}
		}
	};
    
}

