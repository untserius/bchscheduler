package com.evg.scheduler.utils;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;


@Service
public class utils {
	
	public Date addSec(int n,Date date)  { 
		Date time = null;
		try {
			//LocalDateTime actualDateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.of("UTC"));
			//actualDateTime=actualDateTime.plusSeconds(n);
			
			//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			//formatter.withZone(ZoneId.of("UTC"));
			//time = stringToDate(actualDateTime.format(formatter));
			
			
		    Calendar gcal = new GregorianCalendar();
		    gcal.setTime(date);
		    gcal.add(Calendar.SECOND, n);
		    time = gcal.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
	   return time;
	}
	public Date addMin(int n,Date date)  { 
		Date time = null;
		try {
			//LocalDateTime actualDateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.of("UTC"));
			//actualDateTime=actualDateTime.plusSeconds(n);
			
			//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			//formatter.withZone(ZoneId.of("UTC"));
			//time = stringToDate(actualDateTime.format(formatter));
			
			
		    Calendar gcal = new GregorianCalendar();
		    gcal.setTime(date);
		    gcal.add(Calendar.MINUTE, n);
		    time = gcal.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
	   return time;
	}
	
	public Date stringToDate(String val) {
		Date date = null;
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			date = dateFormat.parse(val);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return date;
	}
	public String getStationRandomNumber(long stationId) {
		String number=UUID.randomUUID().toString();
		try {
			number=String.valueOf(stationId)+System.currentTimeMillis();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return number;
	}
	public Map<String,Object> getJsonParsing(String content){
		Map<String,Object> map = new HashMap<>();
		try {
			JSONParser parser = new JSONParser();
			Object obj  = parser.parse(content);
			JSONArray array = new JSONArray();
			array.add(obj);
			for (Object object1 : array) {
				Iterator iter = ((JSONObject) object1).entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry me = (Map.Entry) iter.next();
					map.put(String.valueOf(me.getKey()), String.valueOf(me.getValue()));
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	public Map<String,Object> getObjectParsing(Object content){
		Map<String,Object> map = new HashMap<>();
		try {
			JSONArray array = new JSONArray();
			array.add(content);
			for (Object object1 : array) {
				Iterator iter = ((JSONObject) object1).entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry me = (Map.Entry) iter.next();
					map.put(String.valueOf(me.getKey()), String.valueOf(me.getValue()));
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	public Date getUTCDate()  {
		Date parse = null;
		try {
			Date date = new Date();
			SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			String sysDate = DateFormat.format(date);
			parse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sysDate);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return parse;
	}
	
	public static String getUTCDateString()  {
		String sysDate = null;
		try {
			Date date = new Date();
			SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			sysDate = DateFormat.format(date);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return sysDate;
	}
	public static String getUTCDateTimeString()  {
		String sysDate = null;
		try {
			Date date = new Date();
			SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
			DateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			sysDate = DateFormat.format(date);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return sysDate;
	}
	public static boolean isPathValid(String path) {
		boolean flag = false;
        try {
        	File file = new File(path);
			if(file.exists()){
				flag = true;
			}
        } catch (Exception ex) {
        	flag=false;
        }
        return flag;
    }
	public String getRandomNumber(String type) {
		StringBuilder val = new StringBuilder();
		try {
			if(type.equalsIgnoreCase("transactionId")) {
				//val.append(System.currentTimeMillis()).append("01");//TransactionId
				val.append(UUID.randomUUID().toString()).append("01");//TransactionId
			}else if(type.equalsIgnoreCase("txnSessionId")) {
//				val.append(UUID.randomUUID().toString()).append("02");//SessionId
				val.append(System.currentTimeMillis()).append("02");
			}else if(type.equalsIgnoreCase("RSTP")) {
				val.append(UUID.randomUUID().toString()).append("03");//RemoteStopTransaction RequestId
			}else if(type.equalsIgnoreCase("UC")) {
				val.append(UUID.randomUUID().toString()).append("04");//UnlockConnector RequestId
			}else {
				val.append(UUID.randomUUID().toString()).append("00");//UnlockConnector RequestId
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return val.toString();
	}
	public Double decimalwithtwodecimals(Double final_Cost) {
		
		try {
			String finalcostString = String.valueOf(final_Cost);
			String finalcostbeforedecimals = finalcostString.split("\\.")[0];			
			String finalcostafterdecimals = finalcostString.substring(finalcostString.indexOf(".")).substring(1, 3);
			String finalcoststringcombined = finalcostbeforedecimals + "." + finalcostafterdecimals;			
			final_Cost = Double.parseDouble(finalcoststringcombined);
			//final_Cost = Double.valueOf(new DecimalFormat("##.##").format(final_Cost));
			
		}catch(Exception e) {
			//e.printStackTrace();
		}
		return final_Cost;
	}
	public static Date getUtcDateFormate(Date date)  { 
		Date parse = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//2020-09-02T07:41:28Z  yyyy-MM-dd'T'HH:mm:ssZ
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			String utctime = dateFormat.format(date);
			parse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(utctime);
		}catch (Exception e) {
			e.printStackTrace();
		}
	   return parse;
	}

	public String getuuidRandomId() {
		return String.valueOf(UUID.randomUUID());
	}

	public static String getIntRandomNumber() {
		Random rand1 = new Random();
		String randomValues = String.valueOf(Math.abs(rand1.nextInt()));
		return randomValues;
	}
    public String decimalwithtwoZeros(Double final_Cost) {
		
		String finalcostString = String.valueOf(final_Cost);
		try {
			if(finalcostString.substring(finalcostString.indexOf(".")).length()==2) {
				finalcostString=finalcostString+"0";
			}
		}catch(Exception e) {
			//e.printStackTrace();
		}
		
		return finalcostString;		
	}
    
    public static String minusSec(int n)  { 
  		String time = null;
  		try {
  			LocalDateTime actualDateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.of("UTC"));
  			actualDateTime=actualDateTime.minusSeconds(n);
  			
  			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  			//formatter.withZone(ZoneId.of("UTC"));
  			//time = stringToDate(actualDateTime.format(formatter));
  			time = actualDateTime.format(formatter);
  		} catch (Exception e) {
  			e.printStackTrace();
  		}
  	   return time;
  	}
    
    public static Map<String, Double> getTimeDifferenceInMiliSec(Date startTimeStamp, Date endTimeStamp)
			throws ParseException {

		Map<String, Double> timeConvertMap = new HashMap<String, Double>();
		Long timeDifference = endTimeStamp.getTime() - startTimeStamp.getTime();
		double seconds = Math.abs(TimeUnit.MILLISECONDS.toSeconds(timeDifference));
		double hours = TimeUnit.MILLISECONDS.toHours(timeDifference);
		double totalDurationHours = (seconds / 3600);
		double totalDurationInminutes = (seconds / 60);

		timeConvertMap.put("Seconds", seconds);
		timeConvertMap.put("Minutes", totalDurationInminutes);
		timeConvertMap.put("Hours", hours);
		timeConvertMap.put("durationInHours", totalDurationHours);
		timeConvertMap.put("timeDifference", Double.valueOf(String.valueOf(timeDifference)));

		return timeConvertMap;
	}
    public static String getDateString()  {
		String sysDate = null;
		try {
			Date date = new Date();
			SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			sysDate = DateFormat.format(date);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return sysDate;
	}
    public static Date getDateFrmt(Date date)  {
		Date parse = null;
		try {
			parse = new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(date));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return parse;
	}
}
