package com.evg.scheduler.config.serviceImpl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.evg.scheduler.dao.GeneralDao;
import com.evg.scheduler.repository.jdbc.ExecuteRepository;
import com.evg.scheduler.service.ocppUserService;
import com.evg.scheduler.utils.utils;


@Service
public class OCPPUserServiceImpl implements ocppUserService{
	private final static Logger logger = LoggerFactory.getLogger(OCPPUserServiceImpl.class);

	@Autowired
	private GeneralDao<?,?> generalDao;
	
	@Autowired
	private ExecuteRepository executeRepository;
	
	@Autowired
	private utils utils;
	
	@Autowired
	private PushNotification pushNotification;
	
	@Value("${ocpi.url}")
    private String ocpiUrl;
	
	@Override
	public Map<String, Object> getSecondaryPropety(long orgId) {
		Map<String, Object> recordBySql = new HashMap<String, Object>();
		try {
			String query = "select secondryEmail,secondryHost,secondryPassword,secondryPort from serverProperties where orgId = "+orgId+"";
			recordBySql = executeRepository.findAll(query).get(0);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return recordBySql;
	}
	@Override
	public Map<String, Object> getPrimaryPropety(long orgId) {
		Map<String,Object> map = new HashMap<>();
		try{
			String query = "select address,email as email_auth,fromEmail as email,host,legacykey,cg.orgId,orgName,password,phoneNumber,port,portalLink,"
					+ " protocol,isnull(serverKey,'') as serverKey,li.url as logo_url,supportEmail from configurationSettings  cg inner join "
					+ " logo_image li on cg.orgId = li.orgId where cg.orgId=1 and li.logoType='main'";
			map = executeRepository.findAll(query).get(0);
			if(map != null && !map.isEmpty()) {
			}else {
				query = "select address,email as email_auth,fromEmail as email,host,legacykey,cg.orgId,orgName,password,phoneNumber,port,portalLink,"
						+ " protocol,isnull(serverKey,'') as serverKey,li.url as logo_url,supportEmail from configurationSettings  cg inner join "
						+ " logo_image li on cg.orgId = li.orgId where cg.orgId="+1+" and li.logoType='main'";
				map = executeRepository.findAll(query).get(0);
				if(map != null && !map.isEmpty()) {
					
				}else {
					map.put("orgName", "BC Hydro");
					map.put("orgId", "1");
					map.put("legacykey", "");
					map.put("serverKey", "");
					map.put("email", "");
					map.put("protocol", "");
					map.put("phoneNumber", "");
					map.put("portalLink", "");
					map.put("supportEmail", "evsupport@bchydro.com");
					map.put("host", "");
					map.put("port", "");
					map.put("password", "");
					map.put("logo_url", "");
					map.put("address", "333 Dunsmuir St. Vancouver, BC, V6B 5R3, CANADA.");
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	@Override
	public Map<String, Object> logoDeatils(long orgId) {
		Map<String, Object> map = new HashMap<>();
		List<Map<String, Object>> list = new ArrayList<>();
		try {
			String query = "select url from logo_image where orgId = '" + orgId + "'";
			list = executeRepository.findAll(query);
			if (list.size() > 0) {
				map = list.get(0);
			} else {
				map.put("url", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	@Override
	 public Map<String,Object> getOrgData(long orgId) {
			Map<String,Object> map = new HashMap<>();
			List<Map<String,Object>> list = new ArrayList<>();
			try{
				String query = "select address,email,host,legacykey,logoName,orgId,orgName,password,phoneNumber,port,portalLink,"
						+ "protocol,isnull(serverKey,'') as serverKey from configurationSettings where orgId = '"+orgId+"'";
				list = executeRepository.findAll(query);
				if(list.size() > 0) {
					map = list.get(0);
				}else {
					map.put("orgName", "BC Hydro");
					map.put("orgId", "1");
					map.put("email", "");
					map.put("host", "");
					map.put("port", "");
					map.put("password", "");
					map.put("address", "5251 California Ave, STE 150, Irvine, CA - 92617.");
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			return map;
	}

	@Override
	public BigDecimal convertCurrency(String ToCurrency, String FromCurrency, BigDecimal needToDebit) {
		try {
			BigDecimal currencyInUsd = new BigDecimal("0.00");
			BigDecimal currency = new BigDecimal("0.00");
			String query = "select ISNULL(c.currency_rate,0) as currency_rates from currency_rate c where currency_code = '"
					+ FromCurrency + "'";
			List<Map<String, Object>> querry = executeRepository.findAll(query);
			if (querry.size() > 0) {
				currencyInUsd = new BigDecimal(String.valueOf(querry.get(0).get("currency_rates")));
				needToDebit = needToDebit.divide(currencyInUsd, 5, RoundingMode.HALF_UP);
			}
			String query1 = "select ISNULL(c.currency_rate,0) as currency_rates from currency_rate c where currency_code = '"
					+ ToCurrency + "'";
			querry = executeRepository.findAll(query1);
			if (querry.size() > 0) {
				currency = new BigDecimal(String.valueOf(querry.get(0).get("currency_rates")));
				needToDebit = needToDebit.multiply(currency);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return needToDebit;
	}
	@Override
	 public BigDecimal currencyRate(String userCurrency, String siteCurrency) {
			BigDecimal usercurrencyRateInUSD = new BigDecimal("0.00");
			BigDecimal sitecurrencyRateInUSD = new BigDecimal("0.00");
			BigDecimal userCurrencyRate =new BigDecimal("0.00");
			try {
				String query = "select currency_rate from currency_rate where currency_code ='" + userCurrency + "'";
				List querry = executeRepository.findAll(query);
				if (querry.size() > 0) {
					usercurrencyRateInUSD = new BigDecimal(String.valueOf(querry.get(0)));				
				}
				String query1 = "select currency_rate from currency_rate where currency_code ='" + siteCurrency + "'";
				querry = executeRepository.findAll(query1);
				if (querry.size() > 0) {
					sitecurrencyRateInUSD = new BigDecimal(String.valueOf(querry.get(0)));
					sitecurrencyRateInUSD=new BigDecimal("1").divide(sitecurrencyRateInUSD, 5, RoundingMode.HALF_UP);
				}

				userCurrencyRate = sitecurrencyRateInUSD.multiply(usercurrencyRateInUSD);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return userCurrencyRate;
		}
	
	@Override
	 public Map<String, Object> getSiteDetails(long portId) {
        Map<String, Object> map = new HashMap<>();
        try {
            String str = "select isnull(si.currencySymbol,'&#36;') as currencySymbol,ISNULL(si.currencyType,'CAD') as currencyType,si.siteId,si.siteName as siteName from station st inner join site si on st.siteId = si.siteId inner join port p on p.station_Id = st.id where p.id ="+portId;
            List<Map<String, Object>> mapData = executeRepository.findAll(str);
            if(mapData.size() > 0) {
                map = mapData.get(0);
            }else {
                map.put("currencySymbol", "&#36;");
                map.put("currencyType", "CAD");
                map.put("siteId", "0");
                map.put("processingFee", "0.00");
                map.put("siteName", "-");
                map.put("saleTexPerc", "0.00");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return map;
	}
	
	@Override
	 public Map<String,Object> accntsBeanObj(long userid){
			Map<String,Object> map = new HashMap<String, Object>();
			try {
				String query = "select a.id as accid,a.accountBalance,a.accountName,a.activeAccount,a.autoReload,a.creationDate,a.lowBalanceFlag,a.oldRefId,a.user_id,a.notificationFlag,"
						+ "isnull(a.currencyType,'USD') as currencyType,isnull(a.currencySymbol,'&#36;') as currencySymbol,u.UserId,u.email,convert(varchar,isnull((select DATEADD(HOUR,"
						+ "CAST(SUBSTRING(replace(z.utc_code,'GMT',''),1,3) as int),DATEADD(MINUTE,CAST(SUBSTRING(replace(z.utc_code,'GMT',''),5,2) as int),getutcdate())) from zone z "
						+ "where z.zone_id = p.zone_id),GETUTCDATE()), 9)  + ' ' + isnull((select z.zone_name from zone z where z.zone_id = p.zone_id),'UTC') as userTime,u.uid as uuid from accounts a "
						+ "inner join profile p on a.user_id = p.user_id inner join  Users u  on a.user_id=u.UserId where a.user_id= '"+ userid +"' ";
				List<Map<String, Object>> mapData = executeRepository.findAll(query);
				if(mapData.size() > 0) {
					map = mapData.get(0);
				}else {
					map.put("currencyType", "USD");
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			return map;
		}
	
	@Override
	public void insertIntoAccountTransaction(Map<String, Object> account, double amtDebit,
				String comment, Date utcTime, double remainingBalance, String status, String sessionId, String customerId,
				String stationRefNum, String currencyType, String usercurrencyType, float currencyRate, double refundAmount, String paymentMode, String transactionType) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			try {
				remainingBalance = Double.parseDouble(String.valueOf(utils.decimalwithtwodecimals(remainingBalance)));
				String listquery = "select id from account_transaction WHERE sessionId = '" + sessionId + "' order by id desc";
				list = executeRepository.findAll(listquery);
				String utctime = utils.getUTCDateString();
				String query = "";
				if (list == null || list.size() <= 0) {
					query = "insert into account_transaction (amtCredit,amtDebit,comment,createTimeStamp,currentBalance,customerId,"
							+ " customerIdAtStationType,status,account_id,sessionId,currencyType,currencyRate,lastUpdatedTime,paymentMode,transactionType) values ('"+refundAmount+"','"
							+ amtDebit + "','" + comment + "'," + " '" + utctime + "','" + remainingBalance + "','"
							+ customerId + "','" + 1l + "','" + status + "','" + account.get("accid") + "','" + sessionId
							+ "','" + usercurrencyType + "','" +currencyRate+ "','"+ utctime +"' ,'"+paymentMode+"','"+transactionType+"')";
					generalDao.updateSqlQuiries(query);//queryExecute
					logger.info(stationRefNum + " , inserted into acc txn table : " + query);

				} else {
					long id = 0;
					if (list != null && list.size() > 0) {
					 id=Long.parseLong(String.valueOf(list.get(0).get("id")));
					}
					query = "update account_transaction set amtCredit='0',amtDebit='" + amtDebit + "',comment='" + comment
							+ "'," + " currentBalance='" + remainingBalance
							+ "',customerId='" + customerId + "',customerIdAtStationType='" + 1l + "',status='" + status
							+ "'," + " account_id='" + account.get("accid") + "',sessionId='" + sessionId
							+ "',currencyType='" + usercurrencyType + "',currencyRate='" + currencyRate + "',lastUpdatedTime='"+ utctime +"' ,paymentMode='"+paymentMode+"',transactionType='"+transactionType+"' where id = '"
							+ id + "'";
					generalDao.updateSqlQuiries(query);
				}
				String queryForAccountBalanceUpdate = "update Accounts set accountBalance=" + remainingBalance
						+ " where id=" + account.get("accid");
				logger.info(stationRefNum + " , update accnts table : " + queryForAccountBalanceUpdate);
				generalDao.updateSqlQuiries(queryForAccountBalanceUpdate);
			} catch (Exception e) {
				e.printStackTrace();
			}
					
		}
	@Override
	public void reservationPushNotification(Map<String, Object> reserveData, String reason,String stationRefNum) {
			try {
				Thread th = new Thread() {
					public void run() {
						try {
							long userId=Long.valueOf(String.valueOf(reserveData.get("userId")));
							long orgId = Long.valueOf(String.valueOf(reserveData.get("orgId")));
							long reservationId =Long.valueOf(String.valueOf(reserveData.get("reservationId")));
							long portId = Long.valueOf(String.valueOf(reserveData.get("portId")));
							long stationId =  Long.valueOf(String.valueOf(reserveData.get("stationId")));
							String randomId = utils.getRandomNumber("transactionId");

							List<Map<String, Object>> deviceDetails = getDeviceByUser(userId);
							JSONArray iOSRecipients = new JSONArray();
							Map<String, Object> orgData = getOrgData(orgId, stationRefNum);
							JSONArray androidRecipients = new JSONArray();

							if (deviceDetails != null && deviceDetails.size()>0) {
								deviceDetails.forEach(device -> {
									try {
										if (device != null && Long.parseLong(String.valueOf(device.get("orgId")))==orgId) {
											if (String.valueOf(device.get("deviceType")).equalsIgnoreCase("Android")) {
												androidRecipients.add(String.valueOf(device.get("deviceToken")));
											} else if (String.valueOf(device.get("deviceType")).equalsIgnoreCase("iOS")) {
												iOSRecipients.add(String.valueOf(device.get("deviceToken")));
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								});

								if (!androidRecipients.isEmpty()) {
									JSONObject info = new JSONObject();
									JSONObject extra = new JSONObject();
									info.put("notificationId", randomId);
									info.put("title", orgData.get("orgName").toString());
									info.put("userId", userId);
									info.put("body", "");
									info.put("action", "Cancel Reservation");
									extra.put("reason",reason);
									extra.put("reservationId",reservationId);
									extra.put("referNo",stationRefNum);
									extra.put("portId",portId);
									extra.put("stationId",stationId);
									info.put("extra",extra);
									pushNotification.pushNotificationForIosAndroid(androidRecipients, "Android",info, stationRefNum, orgData.get("legacykey").toString());
								}
								if (!iOSRecipients.isEmpty()) {
									JSONObject info = new JSONObject();
									JSONObject extra = new JSONObject();
									info.put("mutable_content", true);
									info.put("sound", "default");
									info.put("portId", portId);
									info.put("title", orgData.get("orgName").toString());
									info.put("type", "Cancel Reservation");
									info.put("body", "");
									info.put("categoryIdentifier", "notification");
									info.put("referNo",stationRefNum);
									info.put("reservationId",reservationId);
									info.put("content-available",1);
									info.put("stationId",stationId);
									info.put("reason",reason);
									pushNotification.pushNotificationForIosAndroid(iOSRecipients, "iOS", info,stationRefNum, orgData.get("serverKey").toString());
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				th.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	@Override
		public String getDisplayNameByPortId(long PortId) {
			String displayName = null;
			try {
				String query = "select isnull(displayName,'Port-1') as displayName from port where id="+PortId;
				List<Map<String, Object>> mapData =executeRepository.findAll(query);
				if(mapData.size() > 0) {
					displayName=String.valueOf(mapData.get(0).get("displayName"));
				}	
			} catch (Exception e) {
				e.printStackTrace();
			}
			return displayName;
		}
	
	@Override
		public Map<String,Object> getOrgData(long orgId,String stationRefNum) {
			Map<String,Object> map = new HashMap<>();
			List<Map<String,Object>> list = new ArrayList<>();
			try{
				String query = "select address,email,host,legacykey,logoName,orgId,orgName,password,phoneNumber,port,portalLink,"
						+ "protocol,isnull(serverKey,'') as serverKey from configurationSettings where orgId = '"+orgId+"'";
				list = executeRepository.findAll(query);
				if(list.size() > 0) {
					map = list.get(0);
				}else {
					map.put("orgName", "BC Hydro");
					map.put("orgId", "1");
					map.put("legacykey", "");
					map.put("serverKey", "");
					map.put("email", "");
					map.put("host", "");
					map.put("port", "");
					map.put("password", "");
					map.put("address", "5251 California Ave, STE 150, Irvine, CA - 92617.");
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			return map;
		}
		
		@Override
		public List<Map<String,Object>> getDeviceByUser(Long userId)  {
			List<Map<String,Object>> findAll = new ArrayList();
			try {
				String query = "select orgId,deviceType,deviceToken from device_details where userId="+userId ;
				findAll = executeRepository.findAll(query);
			}catch (Exception e) {
				e.printStackTrace();
			}
			return findAll;
		}
		
		@Override
		public void updatingLastUpdatedtime(String newDate,long stationId,long siteId) {
			try {
				String updateSql = "update station set lastUpdatedDate = '"+newDate+"' where id = "+stationId+";";
				
				updateSql += "update site set lastUpdatedDate = '"+newDate+"' where siteId = "+siteId+";";
				
				updateSql += "update port set lastUpdatedDate = '"+newDate+"' where station_id = "+stationId+";";
				generalDao.updateSqlQuiries(updateSql);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void updatePortStatus(long portUniId,String portStatus) {
			try {
				String sql="update port set status = '"+portStatus+"' where id = "+portUniId;
				generalDao.updateSqlQuiries(sql);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void postlastupdated(long id ) {
	        try {
	        	 String urlToRead = ocpiUrl+"ocpi/ocpp/update?id="+id;
	             StringBuilder result = new StringBuilder();
	             URL url = null;
	            url = new URL(urlToRead);
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("GET");
	            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	            String line;
	            while ((line = rd.readLine()) != null) {
	                result.append(line);
	            }
	            rd.close();
	        } catch (Exception e1) {
	            //e1.printStackTrace();
	        }       
	    }
		@Override
		public String internalMail() {
			String email="Alerts@evgateway.com";
			try {
				String query="select value from serverProperties where property='internalWorkMails'";
				email=executeRepository.findString(query);
			}catch(Exception e) {
				e.printStackTrace();
			}
			return email;
		}
		@Override
		public byte[] createExcelSheet(List<Map<String,Object>> registeredUser,List<Map<String,Object>> payGUser) {
			try {
				 Workbook workbook = new XSSFWorkbook();
			     Sheet sheet = workbook.createSheet("Registered Users");
			     
			     Font headerFont = workbook.createFont();
			     headerFont.setBold(true);
			     CellStyle headerCellStyle = workbook.createCellStyle();
			     headerCellStyle.setFont(headerFont);
			     
			     Row headerRow = sheet.createRow(0);
			     Cell headerCell = headerRow.createCell(0);
			     headerCell.setCellValue("SessionId");
			     headerCell.setCellStyle(headerCellStyle);

			     headerCell = headerRow.createCell(1);
			     headerCell.setCellValue("Email");
			     headerCell.setCellStyle(headerCellStyle);
			     
			     int i=1;
			     for(Map<String,Object> data: registeredUser) {
			    	 Row row = sheet.createRow(i);
			         row.createCell(0).setCellValue(String.valueOf(data.get("sessionId")));
			         row.createCell(1).setCellValue(String.valueOf(data.get("emailId")));
			         i=i+1;
			     }
			     
			     Sheet sheet1 = workbook.createSheet("PAYG Users");
			     Row headerRow1 = sheet1.createRow(0);
			     Cell headerCell1 = headerRow1.createCell(0);
			     headerCell1.setCellValue("SessionId");
			     headerCell1.setCellStyle(headerCellStyle);

			     headerCell1 = headerRow1.createCell(1);
			     headerCell1.setCellValue("Phone");
			     headerCell1.setCellStyle(headerCellStyle);
			     
			     int j=1;
			     for(Map<String,Object> data: payGUser) {
			    	 Row row = sheet1.createRow(j);
			         row.createCell(0).setCellValue(String.valueOf(data.get("sessionId")));
			         row.createCell(1).setCellValue(String.valueOf(data.get("phone")));
			         j=j+1;
			     }
			     
			     
			     ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			        try {
			            workbook.write(outputStream);
			        } catch (IOException e) {
			            e.printStackTrace();
			        } 

			        byte[] excelBytes = outputStream.toByteArray();
			        return 	excelBytes;	        
			     
			}catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
}
