package com.example.location;

import java.io.StringReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class GPSTest extends Activity{
	private TextView textshow;
	private LocationManager locationmanager;
	private String provider;
    public Location location;
    public static final int SHOW_LOCA = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gps_test);
		textshow = (TextView) findViewById(R.id.gps_test);
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
		//获取设备的位置
				location = locationmanager.getLastKnownLocation(provider);
				if(location != null){
					showLocation(location);
					Log.d("MainActivity", "latitude is " +location.getLatitude());
				}		
			}
		private void showLocation(final Location location) {
			new Thread(new Runnable() {
				public void run() {
					try {
						StringBuilder url = new StringBuilder();
						url.append("http://maps.googleapis.com/maps/api/geocode/xml?latlng=");
						url.append(location.getLatitude()).append(",");
						url.append(location.getLongitude());
						url.append("&sensor=false");
						
						HttpClient httpClient = new DefaultHttpClient();
						HttpGet httpGet = new HttpGet(url.toString());
						httpGet.addHeader("Accept-Language", "zh-CN");
						HttpResponse httpresponse = httpClient.execute(httpGet);
						Log.d("MainActivity", "Status is ");
						if(httpresponse.getStatusLine().getStatusCode() == 200){
							Log.d("MainActivity", "请求发送完成");
							HttpEntity entity = httpresponse.getEntity();
							String response = EntityUtils.toString(entity, "utf-8");
							
								Message message = new Message();
								message.what = SHOW_LOCA;
								message.obj = response;
								handler.sendMessage(message);
						    }
					}catch (Exception e) {
							e.printStackTrace();
					}
		       }
			}).start();
		}
		
		
		private Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SHOW_LOCA:
					String xmlshuju = (String) msg.obj;
					textshow.setText(xmlshuju);
					break;

				default:
					break;
				}
				
			}
		  };
	}
		