package com.evg.scheduler.config.serviceImpl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.evg.scheduler.dao.GeneralDao;
import com.evg.scheduler.message.MailForm;
import com.evg.scheduler.repository.jdbc.ExecuteRepository;
import com.evg.scheduler.service.OCPPService;
import com.evg.scheduler.service.intervalService;
import com.evg.scheduler.service.ocppUserService;
import com.evg.scheduler.utils.utils;


@Service
public class OCPPServiceImpl implements OCPPService {

	private final static Logger logger = LoggerFactory.getLogger(OCPPServiceImpl.class);
	
	@Autowired
	private GeneralDao<?,?> generalDao;
	
	@Autowired
	private ExecuteRepository executeRepository;
	
	private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
	private static final String[] SCOPES = { MESSAGING_SCOPE };

	@Value("${ocpp.url}")
	private String ocppUrl;
	
	@Value("${customer.Instance}")
	protected String instance;

	@Value("${vMode}")
	private String vMode;

	@Value("${vAuthVoid}")
	private String vAuthVoid;

	@Value("${vAuthComplete}")
	private String vAuthComplete;
	
	@Value("${LOADMANAGEMENT_URL}")
	private String LOADMANAGEMENT_URL;

	@Autowired
	private EmailServiceImpl emailServiceImpl;

	@Autowired
	private ocppUserService ocppUserService;

	@Autowired
	private utils utils;

	@Autowired
	private intervalService intervalService;

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public void addOCPPStatusSendingData(String messageType, String portalReqID, long stationId, String status,
			String sessionId, long userId, String value, long connectorId, String data, String configurationKey,
			Long requestId, Date createdDate, String client) {

		try {
			String query = "insert into ocpp_statusSendingData (messageType,portalReqID,stationId,status,sessionId,userId,value,connectorId,data,configurationKey,requestId,createdDate,client) values ('"
					+ messageType + "','" + portalReqID + "','" + stationId + "','" + status + "','" + sessionId + "','"
					+ userId + "','" + value + "','" + connectorId + "','" + data + "','" + configurationKey + "','"
					+ requestId + "','" + createdDate + "','" + client + "')";
			generalDao.updateSqlQuiries(query);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public ResponseEntity<String> ocppCallingforMultiRequest(String reqType, List<Map<String, String>> listData) {
		return restTemplate.postForEntity(ocppUrl + "/amp/multirequest", listData, String.class);
	}

	@Override
	public ResponseEntity<String> ocppCallingforRequest(String reqType, Map<String, String> listData) {
		return restTemplate.postForEntity(ocppUrl + "/amp/request", listData, String.class);
	}

	@Override
	public void updateReservationId(long flag, long activeFlag, String sessionId, long cancellationFlag, int chargerFaultCase, long id) {
		try {
			String query = "update ocpp_reservation set flag = '" + flag + "' , activeFlag = '" + activeFlag
					+ "', transactionSessionId = '" + sessionId + "',cancellationFlag=" + cancellationFlag
					+ ",chargerFaultCase='" + chargerFaultCase + "' where id=" + id + "";
			generalDao.updateSqlQuiries(query);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateOcppStatusNotification(String Status, Long stationId, Long connectorId) {
		try {
			String stationStatusDB = getStationStatus(stationId, connectorId);
			String updateOcppstatusNotification = "update statusNotification set status='" + Status
					+ "' where StationID=" + stationId + " and port_id =" + connectorId;
			generalDao.updateSqlQuiries(updateOcppstatusNotification);
			updateConnectorInNteworkProfiles(Status, connectorId);
			if (!stationStatusDB.equalsIgnoreCase(Status)) {
				String utcDateFormate = utils.getUTCDateString();
				long siteId = Long.valueOf(ocppUserService.getSiteDetails(stationId).get("siteId").toString());
				ocppUserService.updatingLastUpdatedtime(utcDateFormate, stationId, siteId);
				ocppUserService.postlastupdated(connectorId);
			}
			ocppUserService.updatePortStatus(connectorId, Status);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateConnectorInNteworkProfiles(String status, long portId) {
		try {
			Thread th = new Thread() {
				public void run() {
					String updateConn_In_networkProfile = "update connectors_in_networkprofile set portStatus='" + status + "' where  portId = " + portId;
					generalDao.updateSqlQuiries(updateConn_In_networkProfile);
				}
			};
			th.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getStationStatus(long stationId, long connectorId) {
		String stationStatus = null;
		try {
			stationStatus = ("select status From statusNotification where port_id =" + connectorId + " AND stationId="
					+ stationId + " order by id desc");

			List<Map<String, Object>> map = executeRepository.findAll(stationStatus);
			if (map != null && map.size() > 0) {
				stationStatus = String.valueOf(map.get(0).get("stationStatus"));
			}
			if (stationStatus == null) {
				stationStatus = "Inoperative";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stationStatus;
	}

	@Override
	public void triggerMessage() {
		try {
			restTemplate.postForEntity(ocppUrl + "/schedule/trigger", "", String.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateReservation() {
		try {
			String query = "select ors.id,ors.reservationId, ors.transactionSessionId,isnull(u.orgId,1) as orgId, ors.stationId,ors.reserveAmount,ors.portId,ors.userId from ocpp_reservation ors "
					+ "inner join statusNotification sn on ors.portId = sn.port_id inner join users u on u.userid=ors.userId where ors.flag =1  and ors.endTime >= GETUTCDATE()  and "
					+ "(ors.transactionSessionId is null or ors.transactionSessionId='null') and (sn.status = 'Unavailable' or sn.status ='Inoperative' or sn.status = 'Blocked' or sn.status = 'SuspendedEVSE' or sn.status = 'SuspendedEV'"
					+ "or DATEDIFF(MINUTE,ISNULL((SELECT TOP 1 HeartbeatTime FROM ocpp_heartBeat oh WHERE oh.StationID = ors.stationId order by id desc),"
					+ "Dateadd(mi, -15, GETUTCDATE())),GETUTCDATE()) >= 30)";
			List<Map<String, Object>> lsData = executeRepository.findAll(query);
			lsData.forEach(map -> {
				String reservationId = String.valueOf(map.get("reservationId"));
				String sessoinId = String.valueOf(map.get("transactionSessionId"));
				long id = Long.valueOf(map.get("id").toString());
				long stnUniId = Long.valueOf(map.get("stationId").toString());
				long portUniId = Long.valueOf(map.get("portId").toString());
				long userId = Long.valueOf(map.get("userId").toString());
				if (sessoinId == null || sessoinId.equalsIgnoreCase("null")) {
					String stationRefNum = getstationRefNum(stnUniId);
					cancelReservationNotification(map, stationRefNum);
					updateOcppStatusNotification("Available", stnUniId, portUniId);
				}
				updateReservationId(0, 0, "null", 1, 1, id);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public void stationDownMailAlertEverDayMidnight() {
		try {
			logger.info("scheduler", "cron time at set at once in a day -----");
			String mailSubject = "EV charging station is disconnected from " + instance + " network";
			String queryForMailEnableStn = " SELECT convert(varchar,referNo,120) from station  where id NOT IN (SELECT Distinct StationID  FROM ocpp_heartBeat WHERE HeartbeatTime > Dateadd(mi, -15, GETUTCDATE())) and mailEnable = ?";
			Map<String, Object> secondaryPropety = getSecondaryPropety(1);
			String sendToMailId = String.valueOf(secondaryPropety.get("secondryEmail"));
			String downMailAlertDisabledStations = generalDao.listOfStringData(queryForMailEnableStn);
			if (!downMailAlertDisabledStations.isEmpty() && downMailAlertDisabledStations != null) {
				logger.info("scheduler", "cron time at set at once in a day for downMailAlertDisabledStations -----");
				String mailContent = "Event Name: EV Station DOWN alert \rSource : BC Hydro server \rStation ID : "
						+ downMailAlertDisabledStations
						+ " \rDescription of alert : BC Hydro server lost response from these stations from last 10 heart beats. Status is Unavailable (Disabled)";

				// Sending the Email to User Email
				emailServiceImpl.customerSupportMailService(new MailForm(sendToMailId, mailSubject, mailContent));
			}

			// For Mail alert enable Stations
			String downMailAlertEnableStations = generalDao.listOfStringData(queryForMailEnableStn);
			if (!downMailAlertEnableStations.isEmpty() && downMailAlertEnableStations != null) {
				logger.info("scheduler", "cron time at set at once in a day for downMailAlertEnableStations -----");
				String mailContent = "Event Name: EV Station DOWN alert \rSource : BC Hydro server \rStation ID : "
						+ downMailAlertEnableStations
						+ "\rDescription of alert : BC Hydro server lost response from these stations from last 10 heart beats. Status is Unavailable";

				// Sending the Email to User Email
				emailServiceImpl.customerSupportMailService(new MailForm(sendToMailId, mailSubject, mailContent));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void configurationKeys() {
		try {
			String query = "select st.id,referNo from station st inner join chargerActivities ca on st.id = ca.stationId where ca.configurationKeys=1";
			logger.info("configuration Key query : " + query);
			List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
			executeRepository.findAll(query).forEach(mapData -> {
				Map<String, String> map = new HashMap();
				String stationRefNum = mapData.get("referNo").toString();
				long stationId = Long.parseLong(String.valueOf(mapData.get("id")));
				try {
					Random rand1 = new Random();
					try {
						String uniqueId = utils.getStationRandomNumber(stationId) + ":GC";
						String msg = "[2,\"" + uniqueId + "\",\"GetConfiguration\",{}]";

						map.put("clientId", stationRefNum);
						map.put("message", msg);
						map.put("uniqueId", uniqueId);
						listData.add(map);

						logger.info(stationRefNum, "configuration Key request sent : " + msg);
					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			if (listData.size() > 0) {
				ResponseEntity<String> result = ocppCallingforMultiRequest("GetConfiguration",
						listData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean updateFlagCreditCardSession(String stnRefNo, long stnId, long portId, String sessionId,
			String paymentcode, BigDecimal finalAmount, String finalDateTime, String authDateTime,
			String TransactionType) {
		boolean flag = false;
		try {
			String hqlQuery = "UPDATE CreditCardWorldPayRes SET flag = 1, finalAmount = '" + finalAmount
					+ "',finalDateTime = '" + finalDateTime + "'," + " transactionType = '" + TransactionType
					+ "' where stationId = '" + stnId + "' and paymentCode = '" + paymentcode + "' and  dateTime = '"
					+ authDateTime + "'";
			generalDao.updateSqlQuiries(hqlQuery);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	@Override
	public Map<String, Object> getSecondaryPropety(long orgId) {
		Map<String, Object> recordBySql = new HashMap<String, Object>();
		try {
			String query = "select secondryEmail,secondryHost,secondryPassword,secondryPort from serverProperties where orgId = "
					+ orgId + "";
			recordBySql = executeRepository.findAll(query).get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recordBySql;
	}
	@Override
	public List getActiveTransactionIds(long stnUniqId) {
		List data = new ArrayList<>();
		try {
			String uniId = "select st.transactionId from ocpp_startTransaction st inner join ocpp_activeTransaction at on st.transactionId = at.transactionId inner join statusNotification sn on sn.port_id = st.connectorId where sn.status = 'Charging' and st.stationId = '"
					+ stnUniqId + "'";
			data = executeRepository.findAll(uniId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	@Override
	public String getstationRefNum(long stationId) {
		String stationRefnum = " ";
		try {
			String queryForStationRefNo = "select isNull(referNo,0) as stationRefnum from station where id=" + stationId
					+ "";

			List<Map<String, Object>> map = executeRepository.findAll(queryForStationRefNo);
			if (map != null && map.size() > 0) {
				stationRefnum = String.valueOf(map.get(0).get("stationRefnum"));

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return stationRefnum;
	}

	@Override
	public void cancelReservationNotification(Map<String, Object> map, String stationRefNum) {
		try {
			String reservationId = String.valueOf(map.get("reservationId"));
			BigDecimal reservationFee = new BigDecimal(String.valueOf(map.get("reserveAmount")));
			long stnUniId = Long.valueOf(map.get("stationId").toString());
			long portUniId = Long.valueOf(map.get("portId").toString());
			long userId = Long.valueOf(map.get("userId").toString());
			Map<String, Object> accountsObj = ocppUserService.accntsBeanObj(userId);
			Map<String, Object> siteObj = ocppUserService.getSiteDetails(stnUniId);
			String siteCurrency = siteObj.get("currencyType").toString();
			Long conncetorId = Long.valueOf(portUniId);
			BigDecimal reservationFee1 = reservationFee;
			if (accountsObj != null) {
				String userCurrency = accountsObj.get("currencyType").toString();
				BigDecimal currencyRate = new BigDecimal("0");
				if (!userCurrency.equalsIgnoreCase(siteCurrency)) {
					reservationFee1 = ocppUserService.convertCurrency(userCurrency, siteCurrency, reservationFee);
					currencyRate = ocppUserService.currencyRate(userCurrency, siteCurrency);
				}
				BigDecimal remainingBal = new BigDecimal(accountsObj.get("accountBalance").toString()).add(reservationFee1);
				ocppUserService.insertIntoAccountTransaction(accountsObj, 0.0,
						"Reservation Fee (Station ID : " + stationRefNum + ") ", utils.getUtcDateFormate(new Date()),
						Double.parseDouble(String.valueOf(remainingBal)), "SUCCESS", utils.getIntRandomNumber(), "0",
						stationRefNum, "USD", userCurrency, Float.parseFloat(String.valueOf(currencyRate)),
						Double.parseDouble(String.valueOf(reservationFee1)), "Wallet", "Credit");
				String reservationFeeForMail = utils.decimalwithtwoZeros(
						utils.decimalwithtwodecimals(Double.parseDouble(String.valueOf(reservationFee1))));
				intervalService.sendReservationRefundMail(accountsObj, reservationId, stationRefNum, conncetorId,
						reservationFeeForMail, userId);
				//ocppUserService.reservationPushNotification(map, "UnAvailable", stationRefNum);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void defaultconfigurationKeys() {
		try {
			String query = "select st.id,referNo from station st inner join chargerActivities ca on st.id = ca.stationId where ca.defaultconfigurationKeys = 1 and ca.configurationKeys=1";
			logger.info("119 defaultconfigurationKeys Key query : " + query);
			List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
			executeRepository.findAll(query).forEach(mapData -> {
				String stationRefNum = mapData.get("referNo").toString();
				long stationId = Long.parseLong(String.valueOf(mapData.get("id")));
				long heartbeatInterval = 0;
				long metervaluesampleInterval = 0;
				long HeartBeatInterval = 0;
				long MeterValueSampleInterval1 = 0;
				try {

					String Query = "select config_name,value from charger_configurations where config_name in  ('HeartbeatInterval' , 'MeterValueSampleInterval')";
					List<Map<String, Object>> ccdata = executeRepository.findAll(Query);

					for (Map<String, Object> Mapdata2 : ccdata) {
						if (String.valueOf(Mapdata2.get("config_name")).equalsIgnoreCase("heartbeatInterval")) {
							heartbeatInterval = Long.valueOf(String.valueOf(Mapdata2.get("value")));
						} else if (String.valueOf(Mapdata2.get("config_name"))
								.equalsIgnoreCase("metervaluesampleInterval")) {
							metervaluesampleInterval = Long.valueOf(String.valueOf(Mapdata2.get("value")));
						}
					}
					try {
						String Query1 = "select top 1 HeartBeatInterval, MeterValueSampleInterval from stationConfigForBootNotf where stationId = "
								+ stationId + " order by id desc";
						List<Map<String, Object>> ccdata1 = executeRepository.findAll(Query1);
						for (Map<String, Object> Mapdata3 : ccdata1) {

							HeartBeatInterval = Long.valueOf(String.valueOf(Mapdata3.get("HeartBeatInterval")));

							MeterValueSampleInterval1 = Long
									.valueOf(String.valueOf(Mapdata3.get("MeterValueSampleInterval")));

							if (heartbeatInterval != HeartBeatInterval) {

								String uniqueId1 = utils.getStationRandomNumber(stationId) + ":CNF";
								Map<String, String> map = new HashMap<>();
								String msg = "[2,\"" + uniqueId1 + "\",\"ChangeConfiguration\",{\"key\":\""
										+ "HeartbeatInterval" + "\",\"value\":\"" + heartbeatInterval + "\"}]";
								map.put("clientId", stationRefNum);
								map.put("message", msg);
								map.put("uniqueId", uniqueId1);
								listData.add(map);
							}
							if (metervaluesampleInterval != MeterValueSampleInterval1) {

								String uniqueId2 = utils.getStationRandomNumber(stationId) + ":CNF";
								String msg = "[2,\"" + uniqueId2 + "\",\"ChangeConfiguration\",{\"key\":\""
										+ "MeterValueSampleInterval" + "\",\"value\":\"" + metervaluesampleInterval
										+ "\"}]";
								Map<String, String> map = new HashMap<>();
								map.put("clientId", stationRefNum);
								map.put("message", msg);
								map.put("uniqueId", uniqueId2);
								listData.add(map);
							}

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			if (listData.size() > 0) {
				ResponseEntity<String> result = ocppCallingforMultiRequest("GetConfiguration",
						listData);
				logger.info(" 230 listData : " + listData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public void closeIdleSession(String utctime) {
		try {
			logger.info("enetered to close idle sessions");
			String FourhrsBackUTCTime = utils.minusSec(14400);
			String netQuery = "select f.connectorId,f.stationId,f.portId,f.profileId,n.closeIdleSessions,n.powerunit,n.maxThreshold as reservedPower,n.profileType from fleet_sessions f inner join network_profile n on f.profileId = n.id where n.closeIdleSessions=1 and f.meterEndTime < '"+FourhrsBackUTCTime+"' and f.status = 'Active'";
			logger.info("close idle sessions query 1372 : "+netQuery);
			List<Map<String, Object>> networkData = executeRepository.findAll(netQuery);
			logger.info("close idle sessions data 1374 : "+networkData);
			for (Map<String, Object> mapData : networkData) {
				String powerType = String.valueOf(mapData.get("powerUnit"));
				long stationId = Long.valueOf(String.valueOf(mapData.get("stationId")));
				long port_Id = Long.valueOf(String.valueOf(mapData.get("portId")));
				long profile_Id = Long.valueOf(String.valueOf(mapData.get("profileId")));
				long connector_Id = Long.valueOf(String.valueOf(mapData.get("connectorId")));
				double reservedPower = Double.valueOf(String.valueOf(mapData.get("reservedPower")));
				String profileType = String.valueOf(mapData.get("profileType"));
				String updateQuery = "update fleet_sessions set status = 'InActive' where meterEndTime < '"+FourhrsBackUTCTime+"'  and status = 'Active'";
				logger.info("updateQuery 718 : "+updateQuery);
				generalDao.updateSqlQuiries(updateQuery);
				logger.info("update fleetSessions:" + updateQuery);
				String updateConnectorsInNetworkQuery = "update connectors_in_networkprofile set portStatus ='Available', optFlag=0 where portId =" + port_Id+" and profileId="+profile_Id;
				generalDao.updateSqlQuiries(updateConnectorsInNetworkQuery);
				logger.info("updating charging portsList in ConnectorsInNetworkProfile : "+ updateConnectorsInNetworkQuery);
				logger.info(" before sending profileType "+profileType);
				deleteQuerys(profileType, port_Id, stationId, utctime, profile_Id);
				String jsonInputString = "{\"connectorId\":" + connector_Id + ","
						+ "\"unit\":\""+powerType+"\",\"limit\": " + reservedPower
						+ ",\"chargingProfile\": \"TxDefaultProfile\"," + "\"networkProfileId\": " + profile_Id
						+ "," + "\"stationId\": " + stationId + ",\"portId\":" + port_Id + "}";
				logger.info("EDIT Profile : " + jsonInputString);
				sendDataToWebHook(jsonInputString);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void deleteQuerys(String profileType, long portId, long stationId, String utctime,
			long profileId) {
		Date utcDate = utils.getUTCDate();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		String utc_time = sdf.format(utcDate);
		logger.info(" In delete method profileType "+profileType);
		if (profileType.equalsIgnoreCase("dynamic_load_sharing")) {
			String deleteQuery = "Delete from optimization_meter where portId = " + portId + " and stationId ="
					+ stationId;
			generalDao.updateSqlQuiries(deleteQuery);
			logger.info("delete from optimiztion_meter Table:" + deleteQuery);
		} else if (profileType.equalsIgnoreCase("fleet_with_tou_limits")) {
			String deleteQuery1 = "Delete from soc_priority where portId = " + portId + " and stationId =" + stationId;
			generalDao.updateSqlQuiries(deleteQuery1);
			logger.info("delete from soc_priority Table:" + deleteQuery1);
		} else if (profileType.equalsIgnoreCase("first_come_first_serve")) {
			String jsonInputString1 = "{\"methodType\":\"" + profileType + "\"," + "\"networkId\":" + profileId + ","
					+ "\"stationId\":" + stationId + "," + "\"portId\": " + portId + "," + "\"startTime\": \""
					+ utc_time + "\"," + "\"requestType\": \"" + "StopTransaction" + "\"}";
			logger.info("jsonInputString ******************: " + jsonInputString1);
			sendDataToLoadmnt(jsonInputString1);

		}
	}
	
	private void sendDataToWebHook(String jsonInputString) {
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(ocppUrl + "/amp/evgWebhook");
			logger.info("**** httpPost: " + httpPost);
			StringEntity entity = new StringEntity(jsonInputString);
			httpPost.setEntity(entity);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			CloseableHttpResponse response = client.execute(httpPost);
			logger.info(" OCPP URL_____: " + response);
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendDataToLoadmnt(String jsonInputString1) {
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(LOADMANAGEMENT_URL + "/load/stopSession");
			logger.info("**** httpPost: " + httpPost);

			StringEntity entity = new StringEntity(jsonInputString1);
			httpPost.setEntity(entity);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			CloseableHttpResponse response = client.execute(httpPost);
			logger.info(" LOADMANAGEMENT URL******: " + response);
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void deleteIndividualScheduleTime(String utctime) {
		try {
			String fiveMinutesBackUTCTime = utils.minusSec(300).replace(" ", "T")+".000Z";
			String query = "delete from individual_scheduleTime where endTime<'"+fiveMinutesBackUTCTime+"'";
			generalDao.updateSqlQuiries(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
//	@Override
//	public void getAccessToken(){
//		try {
//			GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new FileInputStream(googleService)).createScoped(Arrays.asList(SCOPES));
//			googleCredentials.refresh();
//			String token=String.valueOf(googleCredentials.getAccessToken().getTokenValue());
//			if(token!=null && !token.equalsIgnoreCase("null")) {
//				String query="update configurationSettings set token='"+token+"'";
//				executeRepository.update(query);
//			}
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
