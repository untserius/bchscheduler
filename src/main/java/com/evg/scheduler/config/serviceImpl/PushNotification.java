package com.evg.scheduler.config.serviceImpl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PushNotification {

	final String apiURL = "https://fcm.googleapis.com/fcm/send";
	private final static Logger logger = LoggerFactory.getLogger(PushNotification.class);
	
	@SuppressWarnings("unchecked")
	public void pushNotificationForIosAndroid(JSONArray recipients,String deviceName,JSONObject msgInfo,String stationRefNum,String appKey) {
		try{
			URL url = new URL(apiURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.setUseCaches(false);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "key=" + appKey);
			conn.setRequestProperty("Content-Type","application/json");
			JSONObject json = new JSONObject();
			json.put("registration_ids", recipients);
			if(deviceName.equalsIgnoreCase("Android")) {
				json.put("data", msgInfo);
			}else {
				json.put("notification",msgInfo);
			}
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(json.toString());
			wr.flush();
		
			int responseCode = conn.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			if (responseCode == 200) {
				logger.info("PushNotification -  [Notification sent successfully] : "+responseCode);
			}
			in.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
