package com.evg.scheduler.config.serviceImpl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.evg.scheduler.dao.GeneralDao;
import com.evg.scheduler.message.MailForm;
import com.evg.scheduler.message.PayloadData;
import com.evg.scheduler.model.es.StationUpAndDownData;
import com.evg.scheduler.model.es.portstatusindex;
import com.evg.scheduler.model.es.stationActiveRecords;
import com.evg.scheduler.model.ocpp.ActiveAndSessionForChargingActivityData;
import com.evg.scheduler.model.ocpp.CdrToken;
import com.evg.scheduler.model.ocpp.NotificationTracker;
import com.evg.scheduler.model.ocpp.OCPPActiveTransaction;
import com.evg.scheduler.ocpi.response.PriceComponent;
import com.evg.scheduler.ocpi.response.TariffElement;
import com.evg.scheduler.ocpi.response.TariffResponse;
import com.evg.scheduler.repository.jdbc.ExecuteRepository;
import com.evg.scheduler.service.intervalService;
import com.evg.scheduler.service.ocppUserService;
import com.evg.scheduler.utils.EsLoggerUtil;
import com.evg.scheduler.utils.utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class intervalServiceImpl implements intervalService {

	private final static Logger logger = LoggerFactory.getLogger(intervalServiceImpl.class);

	ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private GeneralDao<?, ?> generalDao;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private EmailServiceImpl emailServiceImpl;

	@Autowired
	private AppProperties appProperties;

	@Autowired
	private EsLoggerUtil esLoggerUtil;

	@Value("${customer.Instance}")
	protected String instance;

	@Value("${support.mail}")
	protected String supportMail;

	@Value("${currency.update}")
	protected boolean CurrencyUpdate;

	@Value("${mobileAuthKey}")
	protected String mobileAuthKey;

	@Value("${mobileServerUrl}")
	private String mobileServerUrl;

	@Value("${evgServerUrl}")
	private String evgServerUrl;

	@Value("${reportsServerUrl}")
	private String reportsServerUrl;

	@Value("${ocpp.url}")
	private String ocppURL;

	@Value("${ocpi.url}")
	private String ocpiUrl;

	@Value("${LOADMANAGEMENT_URL}")
	private String LOADMANAGEMENT_URL;

	@Autowired
	private utils utils;

	@Autowired
	private ocppUserService ocppUserService;

	@Autowired
	private ExecuteRepository executeRepository;
	
	@Override
	public void stationActiveRecordsSaving(String utcTime) {
		try {
			Thread th = new Thread() {
				public void run() {
					logger.info("sation active records functionality started at : " + utils.getUTCDate());
					try {
						String getStnUpDownRecordQuery = "select id as stationId,referno,case when stationTimeStamp > DATEADD(minute, -15, '"
								+ utcTime + "') then '1' else '0' end as stationStatusType from station";
						logger.info("sation active records query : " + getStnUpDownRecordQuery);
						List<Map<String, Object>> stationInterDataList = executeRepository
								.findAll(getStnUpDownRecordQuery);
						List<stationActiveRecords> stLs = new ArrayList<stationActiveRecords>();

						List<IndexQuery> in = new ArrayList<>();

						stationInterDataList.forEach(mapData -> {
							try {
								String stationStatusType = mapData.get("stationStatusType").toString();
								boolean flag = stationStatusType.equalsIgnoreCase("0") ? false
										: stationStatusType.equalsIgnoreCase("1") ? true : false;
								stationActiveRecords stnUpAndDownObj = new stationActiveRecords();
								stnUpAndDownObj.setId(utils.getRandomNumber("transactionId"));
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								Date parse2 = sdf.parse(utcTime);
								stnUpAndDownObj.setCreationDate(parse2);
								stnUpAndDownObj.setStationId(Long.parseLong(mapData.get("stationId").toString()));
								stnUpAndDownObj.setActivity(flag);
								stnUpAndDownObj.setIntervalTime(sdf.parse(utcTime));
								// stLs.add(stnUpAndDownObj);

								IndexQuery indexQuery = new IndexQueryBuilder()
										.withId(stnUpAndDownObj.getId().toString()).withObject(stnUpAndDownObj).build();
								in.add(indexQuery);

							} catch (Exception e) {
								e.printStackTrace();
							}
						});
						esLoggerUtil.createOcppLogsIndexBulk(in);
						stationInterDataList.clear();
					} catch (Exception e) {
						e.printStackTrace();
					}
					logger.info("sation active records functionality ended at : " + utils.getUTCDate());
				}
			};
			th.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stationUpAndDownData() {
		try {
			Thread th=new Thread() {
				public void run() {
					try {
						Date utcDate=utils.getUTCDate();
						String query="select id,referNo,ISNULL(stationTimeStamp,GETUTCDATE()) as stationTimeStamp from station";
						List<Map<String, Object>> list = executeRepository
								.findAll(query);
						List<stationActiveRecords> stLs = new ArrayList<stationActiveRecords>();

						List<IndexQuery> in = new ArrayList<>();
						list.forEach(map ->{
							StationUpAndDownData data=esLoggerUtil.getStationUpAndDownData(Long.parseLong(String.valueOf(map.get("id"))));
							if(data!=null && utils.getDateFrmt(utils.addMin(-60, utcDate)).compareTo(utils.getDateFrmt(data.getStartTimeStamp()))==0) {
						    	data.setEndTimeStamp(utils.getDateFrmt(utcDate));
						    	IndexQuery indexQuery = new IndexQueryBuilder()
										.withId(data.getId().toString()).withObject(data).build();
								in.add(indexQuery);
						    }
							if(data==null || utils.getDateFrmt(utcDate).compareTo(utils.getDateFrmt(data.getStartTimeStamp()))!=0) {
						    	Date stationActiveTime=utils.stringToDate(String.valueOf(map.get("stationTimeStamp")));
						    	Date stationTime=utils.addMin(-15, utcDate);
						    	boolean flag=stationActiveTime.compareTo(stationTime)>0 ?true :false;
						    	data=new StationUpAndDownData();
						    	data.setId(utils.getRandomNumber("txnSessionId"));
						    	data.setActivity(flag);
						    	data.setStationId(Long.parseLong(String.valueOf(map.get("id"))));
						    	data.setStnRefNum(String.valueOf(map.get("referNo")));
						    	data.setStartTimeStamp(utils.getDateFrmt(utcDate));
						    	data.setEndTimeStamp(utils.getDateFrmt(utcDate));
						    	
						    	IndexQuery indexQuery = new IndexQueryBuilder()
										.withId(data.getId().toString()).withObject(data).build();
								in.add(indexQuery);
						    }
						});
						if(in.size()>0) {
							esLoggerUtil.createStationUpAndDownDataBulk(in);
						}
						list.clear();
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			th.start();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void autoreloadTrigger() {
		try {
			boolean autoReload = false;
			String str = "select ISNULL(autoReload,'0') as autoReload from app_config_setting where orgId=1";
			List<Map<String, Object>> list = executeRepository.findAll(str);
			if (list.size() > 0) {
				autoReload = Boolean.parseBoolean(String.valueOf(list.get(0).get("autoReload")));
			}
			if (autoReload) {
				try {
					String query = "Select ac.id as accountId,ar.paymentId,ar.amount as amount,isnull(ar.currencyType,'USD') as currencyType,ac.accountBalance from "
							+ " accounts ac inner join autoReload ar on ar.accountId = ac.id inner join usercards uc on uc.userId = ar.userId where "
							+ " ac.autoReload = '1' and  ac.accountBalance <=  ar.lowBalance";
					logger.info("auto reload query for USD users : " + query);
					List<Map<String, Object>> autoReloadData = executeRepository.findAll(query);
					logger.info("auto reload data for USD users : " + autoReloadData);
					autoReloadData.forEach(maps -> {
						Long accountId = Long.valueOf(String.valueOf(maps.get("accountId")));
						Double amount = Double.valueOf(String.valueOf(maps.get("amount")));

						if (accountId != 0 && amount != 0) {
							String urlToRead = mobileServerUrl + "api/v3/payment/paymentIntent/autoReload";
							logger.info("auto reload urlToRead : " + urlToRead);
							try {
								Map<String, Object> params = new HashMap<String, Object>();
								params.put("accountId", accountId);
								logger.info("auto reload request obj : " + params);
								HttpHeaders headers = new HttpHeaders();
								headers.set("Content-Type", "application/json");
								headers.set("EVG-Correlation-ID", mobileAuthKey);
								HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);
								apicallingPOST(urlToRead, requestEntity);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updatingInCompletedTrasactions() {
		try {
			try {
				String listOfSessionIds = "select distinct oat.sessionId from ocpp_activeTransaction oat inner join ocpp_heartBeat hb on hb.stationId="
						+ "oat.stationId where hb.heartbeatTime <= Dateadd(MINUTE, -15, GETUTCDATE()) and oat.statusMobile='Charging'";
				String sessionIds = generalDao.listOfStringData(listOfSessionIds);
				if (sessionIds != null && !sessionIds.isEmpty() && !sessionIds.equalsIgnoreCase("''")) {
					String hqlQuery = "from OCPPActiveTransaction where sessionId in (" + sessionIds + ")";
					logger.info("query for list of activetransactions which are in session : " + hqlQuery);
					List<OCPPActiveTransaction> listOfActiveListData = generalDao.findAll(hqlQuery,
							new OCPPActiveTransaction());
					logger.info("list of activetransactions which are in session : " + listOfActiveListData);

					for (OCPPActiveTransaction activeSession : listOfActiveListData) {
						logger.info("sessionIds from ocppActiveTransactions : " + activeSession.getSessionId());
						boolean activeAndSessionForChargingActivityData = getActiveAndSessionForChargingActivityData(
								activeSession.getSessionId());
						if (activeAndSessionForChargingActivityData) {
							try {
								ActiveAndSessionForChargingActivityData activityData = new ActiveAndSessionForChargingActivityData();
								activityData.setSessionId(activeSession.getSessionId());
								activityData.setConnectorId(activeSession.getConnectorId());
								activityData.setMessageType(activeSession.getMessageType());
								activityData.setStationId(activeSession.getStationId());
								activityData.setRfId(activeSession.getRfId());
								activityData.setTransactionId(activeSession.getTransactionId());
								activityData.setStatus(activeSession.getStatus());
								activityData.setUserId(activeSession.getUserId());
								activityData.setRequestedID(activeSession.getRequestedID());
								activityData.setOrgId(activeSession.getOrgId());

								generalDao.saveOrupdate(activityData);
							} catch (Exception e) {
								// TODO: handle exception
							}
						} else {
							logger.info("activeAndSessionForChargingActivityData : "
									+ activeAndSessionForChargingActivityData);
						}
					}
					deleteActiveTransaction(sessionIds);
					logger.info("sessions moved to activeandsession table successfly");
					updateSessionsData(sessionIds);
					listOfActiveListData.clear();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean getActiveAndSessionForChargingActivityData(String sessionId) {
		boolean flag = false;
		try {
			List<ActiveAndSessionForChargingActivityData> findAll = generalDao.findAll(
					"FROM ActiveAndSessionForChargingActivityData where sessionId='" + sessionId + "'",
					new ActiveAndSessionForChargingActivityData());
			if (findAll.size() > 0) {
				flag = false;
			} else {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public void deleteActiveTransaction(String sessionIds) {
		try {
			String deleteActiveTrans = "delete from ocpp_activeTransaction where sessionId in(" + sessionIds + ")";
			logger.info("deleting ActiveTrans from ocpp_activeTransaction query : " + deleteActiveTrans);
			generalDao.updateSqlQuiries(deleteActiveTrans);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateSessionsData(String sessionIds) {
		try {
			String deleteActiveTrans = "Update Session set reasonForTer = 'EVDisconnected',transactionStatus='completed' where SessionId  in ("
					+ sessionIds + ") and reasonForTer = 'InSession'";
			logger.info("deleting ActiveTrans from Session query : " + deleteActiveTrans);
			generalDao.updateSqlQuiries(deleteActiveTrans);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateStationStatusUnavailable() {
		try {
			try {
				// we get stations which are active from last 30 minutes
				String activeStationsQuery = "SELECT Distinct convert(varchar,id,120) FROM station WHERE stationTimeStamp < Dateadd(mi, -10, GETUTCDATE())";
				List<Map<String, Object>> mapData = executeRepository.findAll(activeStationsQuery);
				String listOfActiveStations = String.valueOf(mapData).replace("[", "'").replace("]", "'")
						.replace(", ", "','").replace("{=", "").replace("}", "");
				if (mapData.size() > 0) {
					updatingStatusUnAvailableInStatusNotificationlist(listOfActiveStations);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateStationStatusAvailable() {
		try {
			try {
				// we get stations which are active from last 15 minutes
				String activeStationsQuery = "select p.id from port p inner join station st on p.station_id = st.id inner join statusNotification sn on sn.port_id = p.id WHERE st.stationTimeStamp > Dateadd(mi, -10, GETUTCDATE()) and p.status = 'Inoperative' and sn.inOperativeFlag='0'";
				List<Map<String, Object>> mapData = executeRepository.findAll(activeStationsQuery);
				String listOfActiveStations = String.valueOf(mapData).replace("{", "").replace("id", "")
						.replace("[", "'").replace("]", "'").replace(", ", "','").replace("}", "").replace("=", "");
				if (mapData.size() > 0) {
					updatingStatusAvailableInStatusNotificationlist(listOfActiveStations);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updatingStatusAvailableInStatusNotificationlist(String stationIds) {
		try {
			String utcDateFormate = utils.getUTCDateString();
			String updateOcppstatusNotification = "update statusNotification set status='Available' , timeStamp = '"
					+ utcDateFormate + "' where port_Id In (" + stationIds + ") and status != 'Charging'";
			generalDao.updateSqlQuiries(updateOcppstatusNotification);

			String sql = "update port set status = 'Available' where id In ( " + stationIds
					+ " ) and status != 'Charging'";
			generalDao.updateSqlQuiries(sql);

			String pofileStationsQuery = "SELECT Distinct(ref_stationId) as ref_stationId FROM connectors_in_networkprofile where ref_stationId in ("
					+ stationIds + ") and portStatus != 'Charging'";
			String profilelistOfActiveStations = executeRepository.findAll(pofileStationsQuery).toString()
					.replace("[", "'").replace("]", "'").replace(", ", "','").replace("{ref_stationId=", "")
					.replace("}", "");
			if (!profilelistOfActiveStations.isEmpty() && profilelistOfActiveStations != null
					&& !profilelistOfActiveStations.equalsIgnoreCase("''")) {
				String updateConnectorsInNetworkProfile = "update connectors_in_networkprofile set portStatus = 'Available' where ref_stationId in ("
						+ profilelistOfActiveStations + ") and portStatus != 'Charging'";
				executeRepository.update(updateConnectorsInNetworkProfile);

				String updateFleetSessions = "update fleet_sessions set status = 'InActive' where stationId in ("
						+ profilelistOfActiveStations + ")";
				executeRepository.update(updateFleetSessions);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updatingStatusUnAvailableInStatusNotificationlist(String stationIds) {
		logger.info("stationIds : " + stationIds);
		try {
			String updateOcppstatusNotification = "update statusNotification set status='Inoperative' , inOperativeFlag = '0' where stationId In ("
					+ stationIds + ")";
			generalDao.updateSqlQuiries(updateOcppstatusNotification);

			String sql = "update port set status = 'Inoperative' where station_id In (" + stationIds + ")";
			generalDao.updateSqlQuiries(sql);

			String updateNetworkProfiles = "update connectors_in_networkprofile set portStatus = 'UnAvailable' where ref_stationId in ("
					+ stationIds + ")";
			executeRepository.update(updateNetworkProfiles);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void autoRenewal() {
		try {
			String query = "select um.user_Id as userId,membershipId,autoRenewalFlag from userMEmberShip um inner join profile p on um.user_Id = p.user_Id where renewalDate <= GETUTCDATE() and p.status = 'Active'";
			logger.info("auto Renewal query : " + query);
			List<Map<String, Object>> autoRenewalData = executeRepository.findAll(query);
			logger.info("auto Renewal data : " + autoRenewalData);
			autoRenewalData.forEach(maps -> {
				Long userId = Long.valueOf(String.valueOf(maps.get("userId")));
				Long membershipId = Long.valueOf(String.valueOf(maps.get("membershipId")));
				boolean autoRenewalFlag = Boolean.valueOf(String.valueOf(maps.get("autoRenewalFlag")));
				logger.info("autoRenewalFlag : " + autoRenewalFlag + " , membershipId : " + membershipId);
				if (userId != 0 && membershipId != null) {
					if (autoRenewalFlag) {
						String urlToRead = evgServerUrl + "api/v2/zaap/auth/autoRenewal?userId=" + userId
								+ "&membershipId=" + membershipId + "";
						logger.info("auto Renewal urlToRead : " + urlToRead);
						StringBuilder result = new StringBuilder();
						URL url = null;
						try {
							url = new URL(urlToRead);
							HttpURLConnection conn = null;
							conn = (HttpURLConnection) url.openConnection();
							conn.setRequestMethod("POST");
							BufferedReader rd = null;
							rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
							String line;
							while ((line = rd.readLine()) != null) {
								result.append(line);
							}
							logger.info("auto Renewal url response : " + result + " , response code : "
									+ conn.getResponseCode());
							rd.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						generalDao.updateSqlQuiries(
								"update userMembership set paymentStatus = 'Auto-Renewal Disabled' where membershipId = "
										+ membershipId);
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stationDownMailAlert() {
		try {
			Thread th = new Thread() {
				public void run() {
					try {
						Map<String, Object> secondaryPropety = ocppUserService.getSecondaryPropety(1);
						String mailSubject = "EV charging station is disconnected from " + instance + " network";
						String supportDownMailStnQuery = "select distinct st.id as stationId,st.referNo as referNo,s.siteName as siteName,"
								+ "(IsNull(s.streetName,'')+','+ISNULL(s.streetNo,'')+','+ISNULL(s.city,'')+','+ISNULL(s.state,'')+','+','+ISNULL(s.country,'')) as siteAddress from station "
								+ " st inner join site s on st.siteId=s.siteId inner join  chargerActivities ca on st.id = ca.stationId where st.mailEnable='Enable' and ca.supportMailFlag=0 and"
								+ " DATEDIFF(MINUTE,ISNULL(st.stationTimeStamp, "
								+ " Dateadd(mi, -15, GETUTCDATE())),GETUTCDATE()) >= 30";

						logger.info("Query For Sending the Down Mail Alert : " + supportDownMailStnQuery);

						List<Map<String, Object>> supportDownMailStnList = executeRepository
								.findAll(supportDownMailStnQuery);
						logger.info("Data For Station down Mail Alert Support " + supportDownMailStnList);
						Map<String, Object> configs = ocppUserService.getPrimaryPropety(1);
						for (Map<String, Object> supportMapdata : supportDownMailStnList) {
							String supporMailContent = "BC Hydro server lost response for this station from last " + 10
									+ " heart beats. Status is Unavailable";

							Map<String, Object> mailDetails = new HashMap<String, Object>();
							mailDetails.put("event", "EV Station DOWN alert");
							mailDetails.put("Source", "BC Hydro server");
							mailDetails.put("StationId", supportMapdata.get("referNo"));
							mailDetails.put("description", supporMailContent);
							mailDetails.put("mailType", "upRdownAlert");
							mailDetails.put("Siteaddress", supportMapdata.get("siteAddress").toString());
							mailDetails.put("heading", "EV Station DOWN alert");
							mailDetails.put("curDate", String.valueOf(new Date()));
							mailDetails.put("orgId", "1");
							mailDetails.put("ownerMails", "0");
							mailDetails.put("orgAddress", configs.get("address"));
							mailDetails.put("orgName", configs.get("orgName"));
							mailDetails.put("support_mail", String.valueOf(configs.get("supportEmail")));
							mailDetails.put("support_phone", String.valueOf(configs.get("phoneNumber")));
							mailDetails.put("from_mail_port", configs.get("port"));
							mailDetails.put("from_mail_host", configs.get("host"));
							mailDetails.put("from_mail", configs.get("email"));
							mailDetails.put("from_mail_auth", configs.get("email_auth"));
							mailDetails.put("from_mail_password", configs.get("password"));
							mailDetails.put("from_mail_protocol", configs.get("protocol"));
							mailDetails.put("to_mail", supportMail);
							mailDetails.put("to_mail_cc", "");
							mailDetails.put("Status", "Disconnected");
							emailServiceImpl.sendEmail(new MailForm(configs.get("email").toString(), mailSubject, ""),
									mailDetails, 1, String.valueOf(supportMapdata.get("referNo")));

							// Updating the Support Mail flag to 1
							String sqlUpdateQuery = "update Station set  MailDateAndTime = CONVERT(VARCHAR,GETDATE(),127) where id in("
									+ supportMapdata.get("stationId")
									+ ") ; update chargerActivities set supportMailFlag =1 where stationId in("
									+ supportMapdata.get("stationId") + ") ";
							generalDao.updateSqlQuiries(sqlUpdateQuery);

							logger.info("stationDownMailAlert >>> 292 >>>");
						}

						String ownerDownMailStnQuery = "select DISTINCT st.id as stationId,st.referNo as referNo,s.siteName as siteName,"
								+ " ownerMailId as ownerMails,"
								+ " (IsNull(s.streetName,'')+','+ISNULL(s.streetNo,'')+','+ISNULL(s.city,'')+','+ISNULL(s.state,'')+','+','+ISNULL(s.country,'')) as siteAddress  "
								+ " from station st inner join station_owner_mailAlert sbe on sbe.stationUnqId=st.id inner join "
								+ " site s on st.siteId=s.siteId where st.mailEnable='Enable' and isNull(sbe.mailSendFlag,0) = 0 "
								+ "  and  DATEDIFF(MINUTE,ISNULL(st.stationTimeStamp, "
								+ "  Dateadd(mi, -15, GETUTCDATE())),GETUTCDATE()) >= 30";

						List<Map<String, Object>> ownerDownMailStnList = executeRepository
								.findAll(ownerDownMailStnQuery);

						logger.info("Query For Station down Mail Alert Owners " + ownerDownMailStnQuery);
						logger.info("Data For Station down Mail Alert Owners " + ownerDownMailStnList);

						for (Map<String, Object> ownerMapdata : ownerDownMailStnList) {
							String mailContent = "BC Hydro server lost response for this station from last " + 10
									+ " heart beats. Status is Unavailable";

							Map<String, Object> mailDetails = new HashMap();
							mailDetails.put("event", "EV Station DOWN alert");
							mailDetails.put("Source", "BC Hydro server");
							mailDetails.put("StationId", ownerMapdata.get("referNo"));
							mailDetails.put("description", mailContent);
							mailDetails.put("mailType", "upRdownAlert");
							mailDetails.put("Siteaddress", ownerMapdata.get("siteAddress"));
							mailDetails.put("heading", "EV Station DOWN alert");
							mailDetails.put("curDate", String.valueOf(new Date()));
							mailDetails.put("orgId", "1");
							mailDetails.put("ownerMails", "1");
							mailDetails.put("orgAddress", configs.get("address"));
							mailDetails.put("orgName", configs.get("orgName"));
							mailDetails.put("support_mail", String.valueOf(configs.get("supportEmail")));
							mailDetails.put("support_phone", String.valueOf(configs.get("phoneNumber")));
							mailDetails.put("from_mail_port", configs.get("port"));
							mailDetails.put("from_mail_host", configs.get("host"));
							mailDetails.put("from_mail", configs.get("email"));
							mailDetails.put("from_mail_auth", configs.get("email_auth"));
							mailDetails.put("from_mail_password", configs.get("password"));
							mailDetails.put("from_mail_protocol", configs.get("protocol"));
							mailDetails.put("to_mail", ownerMapdata.get("ownerMails").toString());
							mailDetails.put("to_mail_cc", ownerMapdata.get("ownerMails").toString());
							mailDetails.put("Status", "Disconnected");
							emailServiceImpl.sendEmail(
									new MailForm(ownerMapdata.get("ownerMails").toString(), mailSubject, ""),
									mailDetails, 1, String.valueOf(ownerMapdata.get("referNo")));
							logger.info("ownermail>>> :" + ownerMapdata.get("ownerMails").toString());
							// updating StationBasedEmails flag to 0
							String query = "update station_owner_mailAlert set mailSendFlag =1 where ownermailid = '"
									+ ownerMapdata.get("ownerMails").toString() + "' and stationunqId ="
									+ Long.parseLong(ownerMapdata.get("stationId").toString()) + "";
							generalDao.updateSqlQuiries(query);

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
	public void stationUpMailAlert() {
		try {
			Thread th = new Thread() {
				public void run() {
					try {
						String mailSubject = "EV charging station is Available from " + instance + " network";
						String supportUpMailStnQuery = "select distinct st.id as stationId,st.referNo as referNo,s.siteName as siteName,"
								+ "(IsNull(s.streetName,'')+','+ISNULL(s.streetNo,'')+','+ISNULL(s.city,'')+','+ISNULL(s.state,'')+','+','+ISNULL(s.country,'')) as siteAddress from station "
								+ " st inner join site s on st.siteId=s.siteId inner join  chargerActivities ca on st.id = ca.stationId where st.mailEnable='Enable' and ca.supportMailFlag=1 and"
								+ " DATEDIFF(MINUTE,ISNULL(st.stationTimeStamp, Dateadd(mi, -15, GETUTCDATE())),GETUTCDATE()) <= 15";
						List<Map<String, Object>> supportUpMailStnList = executeRepository
								.findAll(supportUpMailStnQuery);
						logger.info("Query For Sending the Up Mail Alert to support mail : " + supportUpMailStnQuery);
						logger.info("Data For Sending the Up Mail Alert to support mail : " + supportUpMailStnList);
						Map<String, Object> configs = ocppUserService.getPrimaryPropety(1);
						for (Map<String, Object> supportMapdata : supportUpMailStnList) {
							String supporMailContent = "BC Hydro server got response for this station from last " + 10
									+ " heart beats. Status is Availabe Now";

							Map<String, Object> upMailDetails = new HashMap<String, Object>();
							upMailDetails.put("event", "EV Station UP alert");
							upMailDetails.put("Source", "BC Hydro server");
							upMailDetails.put("StationId", supportMapdata.get("referNo"));
							upMailDetails.put("description", supporMailContent);
							upMailDetails.put("mailType", "upRdownAlert");
							upMailDetails.put("Siteaddress", supportMapdata.get("siteAddress").toString());
							upMailDetails.put("heading", "EV Station UP alert");
							upMailDetails.put("curDate", String.valueOf(new Date()));
							upMailDetails.put("orgId", "1");
							upMailDetails.put("ownerMails", "0");
							upMailDetails.put("orgAddress", configs.get("address"));
							upMailDetails.put("orgName", configs.get("orgName"));
							upMailDetails.put("support_mail", String.valueOf(configs.get("supportEmail")));
							upMailDetails.put("support_phone", String.valueOf(configs.get("phoneNumber")));
							upMailDetails.put("from_mail_port", configs.get("port"));
							upMailDetails.put("from_mail_host", configs.get("host"));
							upMailDetails.put("from_mail", configs.get("email"));
							upMailDetails.put("from_mail_auth", configs.get("email_auth"));
							upMailDetails.put("from_mail_password", configs.get("password"));
							upMailDetails.put("from_mail_protocol", configs.get("protocol"));
							upMailDetails.put("to_mail", supportMail);
							upMailDetails.put("to_mail_cc", "");
							upMailDetails.put("Status", "Connected");
							emailServiceImpl.sendEmail(new MailForm(configs.get("email").toString(), mailSubject, ""),
									upMailDetails, 1, String.valueOf(supportMapdata.get("referNo")));
							// Updating the Support Mail flag to 0
							String sqlUpdateQuery = "update chargerActivities set supportMailFlag =0 where stationId in("
									+ supportMapdata.get("stationId") + ") ";
							int updateSQL = generalDao.updateSqlQuiries(sqlUpdateQuery);

						}
						String ownerUpMailStnQuery = "select DISTINCT st.id as stationId,st.referNo as referNo,s.siteName as siteName,"
								+ " ownerMailId as ownerMails,"
								+ " (IsNull(s.streetName,'')+','+ISNULL(s.streetNo,'')+','+ISNULL(s.city,'')+','+ISNULL(s.state,'')+','+','+ISNULL(s.country,'')) as siteAddress  "
								+ " from station st inner join station_owner_mailAlert sbe on sbe.stationUnqId=st.id inner join "
								+ " site s on st.siteId=s.siteId where st.mailEnable='Enable' and isNull(sbe.mailSendFlag,1) = 1 "
								+ "  and  DATEDIFF(MINUTE,ISNULL(st.stationTimeStamp, Dateadd(mi, -15, GETUTCDATE())),GETUTCDATE()) <= 15";
						List<Map<String, Object>> ownerUpMailStnList = executeRepository.findAll(ownerUpMailStnQuery);
						logger.info("Query For Sending the Up Mail Alert to ownerMails : " + supportUpMailStnQuery);
						logger.info("Data For Sending the Up Mail Alert to ownerMails : " + ownerUpMailStnList);
						for (Map<String, Object> ownerMapdata : ownerUpMailStnList) {
							String mailContent = "BC Hydro server got response for this station from last " + 10
									+ " heart beats. Status is Availabe Now";

							Map<String, Object> stationAlert = new HashMap();
							stationAlert.put("heading", "EV Station UP alert");
							stationAlert.put("curDate", String.valueOf(new Date()));
							stationAlert.put("event", "EV Station UP alert");
							stationAlert.put("Source", "BC Hydro server");
							stationAlert.put("StationId", ownerMapdata.get("referNo"));
							stationAlert.put("Siteaddress", ownerMapdata.get("siteAddress").toString());
							stationAlert.put("mailType", "upRdownAlert");
							stationAlert.put("description", mailContent);
							stationAlert.put("orgId", "1");
							stationAlert.put("ownerMails", "1");
							stationAlert.put("orgAddress", configs.get("address"));
							stationAlert.put("orgName", configs.get("orgName"));
							stationAlert.put("support_mail", String.valueOf(configs.get("supportEmail")));
							stationAlert.put("support_phone", String.valueOf(configs.get("phoneNumber")));
							stationAlert.put("from_mail_port", configs.get("port"));
							stationAlert.put("from_mail_host", configs.get("host"));
							stationAlert.put("from_mail", configs.get("email"));
							stationAlert.put("from_mail_auth", configs.get("email_auth"));
							stationAlert.put("from_mail_password", configs.get("password"));
							stationAlert.put("from_mail_protocol", configs.get("protocol"));
							stationAlert.put("to_mail", ownerMapdata.get("ownerMails").toString());
							stationAlert.put("to_mail_cc", "");
							stationAlert.put("Status", "Connected");

							emailServiceImpl.sendEmail(
									new MailForm(ownerMapdata.get("ownerMails").toString(), mailSubject,
											"Station connectivity Alert"),
									stationAlert, 1, String.valueOf(ownerMapdata.get("referNo")));
							// updating StationBasedEmails flag to 1
							String query = "update station_owner_mailAlert set mailSendFlag =0 where ownermailId = '"
									+ ownerMapdata.get("ownerMails").toString() + "' and stationUnqId ="
									+ Long.parseLong(ownerMapdata.get("stationId").toString()) + "";
							generalDao.updateSqlQuiries(query);
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
	@Transactional(readOnly = true)
	public void stationDownMailAlertEverDayMidnight() {
		try {
			Thread th = new Thread() {
				public void run() {
					try {
						String sendToMailId = "";
						Map<String, Object> secondaryPropety = ocppUserService.getSecondaryPropety(1);
						if (secondaryPropety != null) {
							sendToMailId = String.valueOf(secondaryPropety.get("secondryEmail"));
						}
						String mailSubject = "EV charging station is disconnected from " + instance + " network";
						String queryForMailEnableStn = " SELECT convert(varchar,referNo,120) from station  where id NOT IN (SELECT Distinct StationID  FROM ocpp_heartBeat WHERE HeartbeatTime > Dateadd(mi, -15, GETUTCDATE())) and mailEnable = 'Disable'";

						// for mail alert disabled stations
						String downMailAlertDisabledStations = String
								.valueOf(executeRepository.findAll(queryForMailEnableStn)).replace("[", "")
								.replace("]", "").replace(", ", " ,\r\n");
						if (!downMailAlertDisabledStations.isEmpty() && downMailAlertDisabledStations != null) {
							String mailContent = "Event Name: EV Station DOWN alert \rSource : BC Hydro server \rStation ID : "
									+ downMailAlertDisabledStations
									+ " \rDescription of alert : BC Hydro server lost response from these stations from last 10 heart beats. Status is Unavailable (Disabled)";

							// Sending the Email to User Email
							emailServiceImpl
									.customerSupportMailService(new MailForm(sendToMailId, mailSubject, mailContent));
						}
						String queryForMailEnableStn1 = " SELECT convert(varchar,referNo,120) from station  where id NOT IN (SELECT Distinct StationID  FROM ocpp_heartBeat WHERE HeartbeatTime > Dateadd(mi, -15, GETUTCDATE())) and mailEnable = 'Enable'";

						// For Mail alert enable Stations
						String downMailAlertEnableStations = String
								.valueOf(executeRepository.findAll(queryForMailEnableStn1)).replace("[", "")
								.replace("]", "").replace(", ", " ,\r\n");
						if (!downMailAlertEnableStations.isEmpty() && downMailAlertEnableStations != null) {
							String mailContent = "Event Name: EV Station DOWN alert \rSource : BC Hydro server \rStation ID : "
									+ downMailAlertEnableStations
									+ "\rDescription of alert : BC Hydro server lost response from these stations from last 10 heart beats. Status is Unavailable";

							// Sending the Email to User Email
							emailServiceImpl
									.customerSupportMailService(new MailForm(sendToMailId, mailSubject, mailContent));
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
	public void urlCalling() {
		try {
			Thread th = new Thread() {
				public void run() {
					try {
						String urlToRead = evgServerUrl + "services/accounts/reportAndstats/sendmail/account";
						URL url = new URL(urlToRead);
						HttpURLConnection conn = null;
						conn = (HttpURLConnection) url.openConnection();
						conn.setRequestMethod("POST");
						BufferedReader rd = null;
						rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						String line;
						StringBuilder result = new StringBuilder();
						while ((line = rd.readLine()) != null) {
							result.append(line);
						}
						logger.info("weekly url response : " + result + " , response code : " + conn.getResponseCode());
						rd.close();
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
	public void updateCurrency() {
		try {
			Thread th = new Thread() {
				public void run() {
					if (CurrencyUpdate) {
						List<Map<String, Object>> queryForList = executeRepository.findAll(
								"select [id],[currencyName],[currency_code],[currency_rate],[lastUpdated] From [currency_rate] WHERE [currency_code] != 'USD'");
						queryForList.forEach(map -> {
							try {
								String currency_code = String.valueOf(map.get("currency_code"));
								Map<String, String> params = new HashMap<>();
								params.put("id", "1");
								params.put("from", "USD");
								params.put("to", currency_code);
								HttpHeaders headers = new HttpHeaders();
								headers.set("apikey", "1KsPYYyjnt3mAcxD0LctiuRI2NveIQF3");
								headers.add("user-agent",
										"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
								HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
								ResponseEntity<String> response = restTemplate.exchange(
										"https://api.apilayer.com/exchangerates_data/convert?to={to}&from={from}&amount={id}",
										HttpMethod.GET, requestEntity, String.class, params);
								logger.info(
										"currency conversion api calling response code : " + response.getStatusCode());
								logger.info("currency conversion api calling response : " + response.getBody());
								if (response.getStatusCode().toString().equalsIgnoreCase("200 OK")) {
									logger.info("response.getBody() : " + response.getBody());
									Map<String, Object> jsonParsing = utils.getJsonParsing(response.getBody());
									Map<String, Object> property = utils
											.getJsonParsing(String.valueOf(jsonParsing.get("info")));
									logger.info("jsonParsing : " + property.get("rate"));
									String rate = String.valueOf(property.get("rate"));
									generalDao.updateSqlQuiries("update currency_rate set currency_rate = '" + rate
											+ "',lastUpdated='" + utils.getUTCDateTimeString() + "' where id = "
											+ String.valueOf(map.get("id")));
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						});
					}
				}
			};
			th.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void paygUserAmountCapture() {
		try {
			try {
				String query = "select up.stationId,up.phone,se.finalCostInSlcCurrency as Revenue,up.id as preAuthId,se.sessionId,up.deviceType,up.paymentMode from userPayment up "
						+ "inner join session se  on up.sessionId = se.sessionId where userType='GuestUser' and se.endTimeStamp <= DATEADD(DAY,-1,GETUTCDATE()) and se.settlement='Inprogress'  "
						+ "and up.flag=1";
				logger.info("stripe refund query : " + query);
				List<Map<String, Object>> queryForList = executeRepository.findAll(query);
				logger.info("stripe refund data : " + queryForList);
				queryForList.forEach(map -> {
					String sessionId = String.valueOf(map.get("sessionId"));
					Long accTxnId = null;
					String payQuery = "select id from account_transaction_for_guestUser where sessionId='" + sessionId
							+ "'";
					List<Map<String, Object>> List = executeRepository.findAll(payQuery);
					if (List.size() > 0) {
						accTxnId = Long.parseLong(String.valueOf(List.get(0).get("id")));
						String update = "update account_transaction_for_guestUser set revenue="
								+ Double.valueOf(String.valueOf(map.get("Revenue"))) + " ,time=GETUTCDATE() where id="
								+ Long.parseLong(String.valueOf(List.get(0).get("id"))) + " ";
						generalDao.updateSqlQuiries(update);
					} else {
						String insert = "insert into account_transaction_for_guestUser (flag,phone,revenue,sessionId,stationId,time)"
								+ " values (1,'" + String.valueOf(map.get("phone")) + "',"
								+ Double.valueOf(String.valueOf(map.get("Revenue"))) + ",'" + sessionId + "','"
								+ String.valueOf(map.get("stationId")) + "',GETUTCDATE())";
						int result = generalDao.updateSqlQuiries(insert);
						if (result != 0) {
							payQuery = "select id from account_transaction_for_guestUser where sessionId='" + sessionId
									+ "'";
							List = executeRepository.findAll(payQuery);
							if (List.size() > 0) {
								accTxnId = Long.parseLong(String.valueOf(List.get(0).get("id")));
							}
						}
					}

					if (Double.valueOf(String.valueOf(map.get("Revenue"))) >= 0.5) {
						try {
							String userPaymentId = String.valueOf(map.get("preAuthId"));
							if (String.valueOf(map.get("deviceType")).equalsIgnoreCase("Android")
									|| String.valueOf(map.get("deviceType")).equalsIgnoreCase("iOs")) {
								String urlToRead = mobileServerUrl + "api/v3/payment/paymentIntent/capture";
								Map<String, Object> params = new HashMap<String, Object>();
								params.put("userPaymentId", userPaymentId);
								params.put("userType", "GuestUser");
								params.put("captureAmount", Double.valueOf(String.valueOf(map.get("Revenue"))));
								params.put("accTxnId", accTxnId);
								HttpHeaders headers = new HttpHeaders();
								headers.set("Content-Type", "application/json");
								headers.set("EVG-Correlation-ID", mobileAuthKey);
								HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);
								logger.info("paygUserAmountCapture url : " + urlToRead);
								logger.info("paygUserAmountCapture request body : " + params);
								apicallingPOST(urlToRead, requestEntity);
							} else if (String.valueOf(map.get("deviceType")).equalsIgnoreCase("Web")) {
								String urlToRead = mobileServerUrl + "api/v3/payment/stripe/capture";
								Map<String, Object> params = new HashMap<String, Object>();
								params.put("userPaymentId", userPaymentId);
								params.put("userType", "GuestUser");
								params.put("captureAmount", Double.valueOf(String.valueOf(map.get("Revenue"))));
								params.put("accTxnId", accTxnId);
								HttpHeaders headers = new HttpHeaders();
								headers.set("Content-Type", "application/json");
								headers.set("EVG-Correlation-ID", mobileAuthKey);
								HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);
								logger.info("paygUserAmountCapture url : " + urlToRead);
								logger.info("paygUserAmountCapture request body : " + params);
								apicallingPOST(urlToRead, requestEntity);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (Double.valueOf(String.valueOf(map.get("Revenue"))) < 0.5
							&& !String.valueOf(map.get("paymentMode")).equalsIgnoreCase("Freeven")) {
						try {
							String userPaymentId = String.valueOf(map.get("preAuthId"));
							if (String.valueOf(map.get("deviceType")).equalsIgnoreCase("Android")
									|| String.valueOf(map.get("deviceType")).equalsIgnoreCase("iOS")) {
								String urlToRead = mobileServerUrl + "api/v3/payment/paymentIntent/cancelAuthorization";
								Map<String, Object> params = new HashMap<String, Object>();
								params.put("userPaymentId", userPaymentId);
								HttpHeaders headers = new HttpHeaders();
								headers.set("Content-Type", "application/json");
								headers.set("EVG-Correlation-ID", mobileAuthKey);
								HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);
								logger.info("paygUserAmountCancelAuhotize Android/iOS url : " + urlToRead);
								logger.info("paygUserAmountCancelAuhotize request body : " + params);
								apicallingPOST(urlToRead, requestEntity);
							} else if (String.valueOf(map.get("deviceType")).equalsIgnoreCase("Web")) {
								String urlToRead = mobileServerUrl + "api/v3/payment/stripe/cancelAuthorization";
								Map<String, Object> params = new HashMap<String, Object>();
								params.put("userPaymentId", userPaymentId);
								HttpHeaders headers = new HttpHeaders();
								headers.set("Content-Type", "application/json");
								headers.set("EVG-Correlation-ID", mobileAuthKey);
								HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);
								logger.info("paygUserAmountCancelAuhotize Web url : " + urlToRead);
								logger.info("paygUserAmountCancelAuhotize request body : " + params);
								apicallingPOST(urlToRead, requestEntity);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					try {
						String reason = reasonForTerm(sessionId);
						if (reason == null || reason.equalsIgnoreCase("null") || reason.equalsIgnoreCase("")
								|| reason.equalsIgnoreCase("Insession")) {
							reason = "EVDisconnected";
						}
						String str = "update session set settlement='settled', settlementTimeStamp=GETUTCDATE(),reasonForTer='" + reason
								+ "' where sessionId = '" + sessionId + "'";
						generalDao.updateSqlQuiries(str);
						deleteTnxData(sessionId);

					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void apicallingPOST(String url, HttpEntity<Map<String, Object>> requestBody) {
		try {
			ResponseEntity<String> response = restTemplate.postForEntity(url, requestBody, String.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void apicallingGET(String urlToRead) {
		try {
			try {
				URL url = new URL(urlToRead);
				StringBuilder result = new StringBuilder();
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				BufferedReader rd = null;
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				logger.info("negative Balance user api result : " + result);
				rd.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void registeredUsersAmountCapture() {
		try {
			String str = "select se.sessionId,a.id as account_id,ISNULL(se.finalCostInSlcCurrency, 0) as revenue,ISNULL(se.accountTransaction_id, 0) as accountTransaction_id,"
					+ " a.user_id,se.port_id,se.paymentMode from session se inner join accounts a on a.user_id = se.userId  where se.settlement='Inprogress'"
					+ " and se.endTimeStamp < DATEADD(DAY,-1,GETUTCDATE()) and se.endTimeStamp > DATEADD(DAY,-2,GETUTCDATE()) and a.user_id!=0 and"
					+ " (accountTransaction_id=0 or accountTransaction_id is null)";
			logger.info("negativeBalanceUsersData str : " + str);
			List<Map<String, Object>> queryForList = executeRepository.findAll(str);
			logger.info("queryForList negativeBalanceUsersData str : " + queryForList);
			queryForList.forEach(map -> {
				String sessionId = String.valueOf(map.get("sessionId"));
				Long account_Id = Long.valueOf(String.valueOf(map.get("account_id")));
				Long accTxnId = Long.valueOf(String.valueOf(map.get("accountTransaction_id")));
				Long userId = Long.valueOf(String.valueOf(map.get("user_id")));
				Long port_id = Long.valueOf(String.valueOf(map.get("port_id")));
				Double revenue = Double.valueOf(String.valueOf(map.get("revenue")));
				Double accountBalance = getUserBalance(account_Id);
				logger.info("account_Id : " + account_Id + " , accountBalance : " + accountBalance);
				balanceDeductionWithNegativeBalanceUser(accountBalance, revenue, account_Id, sessionId, accTxnId,
						port_id, userId, String.valueOf(map.get("paymentMode")));
				deleteTnxData(sessionId);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String str = "select se.sessionId from session se inner join ocpp_startTransaction os on os.sessionId=se.sessionId where se.sessionStatus='Completed' and se.endTimeStamp < DATEADD(DAY,-1,GETUTCDATE()) and os.unPluged=0";
			logger.info("negativeBalanceUsersData str : " + str);
			List<Map<String, Object>> queryForList = executeRepository.findAll(str);
			queryForList.forEach(map -> {
				String update = "update ocpp_startTransaction set unPluged=1 where sessionId='"
						+ String.valueOf(map.get("sessionId")) + "'";
				executeRepository.update(update);
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cleanupOldSessionBillableData() {
		logger.info("Starting scheduled cleanup of old ocpp_sessionBillableData records (25-hour threshold)");

		try {
			// Delete records not updated in last 25 hours
			String cleanupQuery =
					"DELETE FROM ocpp_sessionBillableData " +
							"WHERE updated_date < DATEADD(HOUR, -25, GETUTCDATE())";

			int deletedCount = executeRepository.update(cleanupQuery);

			logger.info("Successfully cleaned up {} old records from ocpp_sessionBillableData (25-hour threshold)", deletedCount);
		} catch (Exception e) {
			logger.error("Error during cleanup of ocpp_sessionBillableData: {}", e.getMessage(), e);
		}
	}

	@Override
	public void registeredUsersOfflineAmountCapture() {
		try {
			String str = "select se.sessionId,a.id as account_id,ISNULL(se.finalCostInSlcCurrency, 0) as revenue,ISNULL(se.accountTransaction_id, 0) as accountTransaction_id,"
					+ " a.user_id,se.port_id,se.paymentMode from session se inner join accounts a on a.user_id = se.userId  where se.settlement='Inprogress'"
					+ " and se.creationDate < DATEADD(DAY,-1,GETUTCDATE()) and se.creationDate > DATEADD(DAY,-2,GETUTCDATE()) and a.user_id!=0 and reasonForTer='OfflineTransaction' and"
					+ " (accountTransaction_id=0 or accountTransaction_id is null)";
			logger.info("negativeBalanceUsersData str : " + str);
			List<Map<String, Object>> queryForList = executeRepository.findAll(str);
			logger.info("queryForList negativeBalanceUsersData str : " + queryForList);
			queryForList.forEach(map -> {
				String sessionId = String.valueOf(map.get("sessionId"));
				Long account_Id = Long.valueOf(String.valueOf(map.get("account_id")));
				Long accTxnId = Long.valueOf(String.valueOf(map.get("accountTransaction_id")));
				Long userId = Long.valueOf(String.valueOf(map.get("user_id")));
				Long port_id = Long.valueOf(String.valueOf(map.get("port_id")));
				Double revenue = Double.valueOf(String.valueOf(map.get("revenue")));
				Double accountBalance = getUserBalance(account_Id);
				logger.info("account_Id : " + account_Id + " , accountBalance : " + accountBalance);
				balanceDeductionWithNegativeBalanceUser(accountBalance, revenue, account_Id, sessionId, accTxnId,
						port_id, userId, String.valueOf(map.get("paymentMode")));
				deleteTnxData(sessionId);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void OCPIsettlement() {
		try {
			String query = "select se.sessionId,se.port_id,se.customerId,se.startTimeStamp,se.endTimeStamp,ot.tariffId,os.meter_id,os.currency,os.total_cost as total_cost_id,"
					+ " se.finalCostInSlcCurrency,se.kilowattHoursUsed,se.sessionElapsedInMin,sp.cost_info from session se inner join ocpi_session os on "
					+ " os.authorization_reference=se.sessionId inner join ocpi_TransactionData ot on ot.sessionId=se.sessionId inner join session_pricings sp on "
					+ " sp.sessionId=se.sessionId where se.settlement='Inprogress' and se.endTimeStamp < DATEADD(DAY,-1,GETUTCDATE()) and os.status!='INVALID'";
			List<Map<String, Object>> queryForList = executeRepository.findAll(query);
			queryForList.forEach(map -> {
				String sessionId = String.valueOf(map.get("sessionId"));
				long portId = Long.valueOf(String.valueOf(map.get("port_id")));
				String idTag = String.valueOf(map.get("customerId"));
				String startTime = String.valueOf(map.get("startTimeStamp"));
				String endTime = String.valueOf(map.get("endTimeStamp"));
				String tariffId = String.valueOf(map.get("tariffId"));
				String meter_id = String.valueOf(map.get("meter_id"));
				String currency = String.valueOf(map.get("currency"));
				String total_cost_id = String.valueOf(map.get("total_cost_id"));
				String finalCostInSlcCurrency = String.valueOf(map.get("finalCostInSlcCurrency"));
				String kilowattHoursUsed = String.valueOf(map.get("kilowattHoursUsed"));
				String sessionElapsedInMin = String.valueOf(map.get("sessionElapsedInMin"));
				String cost_info = String.valueOf(map.get("cost_info"));
				String reason = reasonForTermForOCPI(sessionId);
				if (reason == null || reason.equalsIgnoreCase("null") || reason.equalsIgnoreCase("")
						|| reason.equalsIgnoreCase("Insession")) {
					reason = "EVDisconnected";
				}
				deleteocpiTnxData(sessionId);
				try {
					String updateOCPiSession = "update ocpi_session set status='COMPLETED' where authorization_reference='"
							+ sessionId + "'";
					executeRepository.update(updateOCPiSession);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					String str = "update session set settlement='settled', settlementTimeStamp=GETUTCDATE(), reasonForTer='" + reason
							+ "' where sessionId = '" + sessionId + "'";
					generalDao.updateSqlQuiries(str);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Map<String, Object> ocpiData = getOCPIData(portId);
					if (ocpiData != null && !ocpiData.isEmpty()) {
						String geoLoId = UUID.randomUUID().toString();
						String insertGeoLocation = "insert into ocpi_geoLocation(id,latitude,longitude) values ('"
								+ geoLoId + "','" + String.valueOf(ocpiData.get("latitude")) + "','"
								+ String.valueOf(ocpiData.get("longitude")) + "')";
						executeRepository.update(insertGeoLocation);

						String cdrLocationId = UUID.randomUUID().toString();
						String insertCDRLoca = "insert into ocpi_cdr_location (id,address,city,connector_format,connector_id,connector_power_type,"
								+ " connector_standard,country,evse_id,evse_uid,name,postal_code,state,uuid,coordinate_id) values("
								+ " '" + cdrLocationId + "','" + String.valueOf(ocpiData.get("streetName")) + "','"
								+ String.valueOf(ocpiData.get("city")) + "'," + " '"
								+ String.valueOf(ocpiData.get("connectorFormat")) + "','"
								+ String.valueOf(ocpiData.get("connector_id")) + "','"
								+ String.valueOf(ocpiData.get("powerType")) + "'," + " '"
								+ String.valueOf(ocpiData.get("connectorType")) + "','"
								+ String.valueOf(ocpiData.get("country")) + "','"
								+ String.valueOf(ocpiData.get("referNo")) + "'," + " '"
								+ String.valueOf(ocpiData.get("portuuid")) + "','"
								+ String.valueOf(ocpiData.get("siteName")) + "'," + " '"
								+ String.valueOf(ocpiData.get("postal_code")) + "','"
								+ String.valueOf(ocpiData.get("state")) + "','"
								+ String.valueOf(ocpiData.get("siteuuid")) + "','" + geoLoId + "')";
						executeRepository.update(insertCDRLoca);

						String str = "select top 1 id,auth_id,contract_id,type,uid,party_id,country_code from ocpi_token where auth_token='"
								+ idTag + "' order by id desc";
						List<Map<String, Object>> OcpiToken = executeRepository.findAll(str);

						if (OcpiToken.size() > 0) {
							CdrToken cdrToken = new CdrToken();
							cdrToken.setId(UUID.randomUUID().toString());
							cdrToken.setContract_id(String.valueOf(OcpiToken.get(0).get("contract_id")));
							cdrToken.setCountry_code(String.valueOf(OcpiToken.get(0).get("country_code")));
							cdrToken.setParty_id(String.valueOf(OcpiToken.get(0).get("party_id")));
							cdrToken.setType(String.valueOf(OcpiToken.get(0).get("type")));
							cdrToken.setUid(String.valueOf(OcpiToken.get(0).get("uid")));
							cdrToken = generalDao.save(cdrToken);

							@SuppressWarnings("rawtypes")
							Map<String, Object> taxesTimeStop1 = new HashMap();
							taxesTimeStop1.put("amount", "0.0");
							taxesTimeStop1.put("description", "Idle Fee (Total duration 0.00 mins)");
							String idlejson = objectMapper.writeValueAsString(taxesTimeStop1);

							List<Map<String, Object>> taxes = new ArrayList();
							JsonNode prices = objectMapper.readTree(cost_info);
							logger.info("prices : " + prices);
							if (prices.size() > 0) {
								JsonNode cost_infoJson = objectMapper
										.readTree(String.valueOf(prices.get(0).get("cost_info")));
								if (cost_infoJson.size() > 0) {
									JsonNode aditional = objectMapper
											.readTree(String.valueOf(cost_infoJson.get(0).get("aditional")));
									if (aditional.size() > 0) {
										logger.info("1004 aditional : " + aditional);
										JsonNode rateRider = objectMapper
												.readTree(String.valueOf(aditional.get("rateRider")));
										logger.info("1006 rateRider : " + rateRider);
										if (rateRider.size() > 0) {
											logger.info("1008 rateRider : " + rateRider);
											Map<String, Object> taxesRRStop = new HashMap();
											String rateRiderType = rateRider.get("type").asText();
											if (rateRiderType.equalsIgnoreCase("-ve")) {
												taxesRRStop.put("description", "Rate Rider -"
														+ Double.valueOf(rateRider.get("percnt").asText()) + "%");
											} else if (rateRiderType.equalsIgnoreCase("+ve")) {
												taxesRRStop.put("description", "Rate Rider +"
														+ Double.valueOf(rateRider.get("percnt").asText()) + "%");
											}
											String amount = String.valueOf(rateRider.get("amount"));

											if (amount == null || amount.equalsIgnoreCase("null")
													|| amount.equalsIgnoreCase("")) {
												taxesRRStop.put("amount", 0.00);
											} else {
												taxesRRStop.put("amount", Double.valueOf(amount));
											}
											taxes.add(taxesRRStop);
										}
										JsonNode taxJsonLs = objectMapper
												.readTree(String.valueOf(aditional.get("tax")));
										if (taxJsonLs.size() > 0) {
											for (int i = 0; i < taxJsonLs.size(); i++) {
												JsonNode taxJsonMap = objectMapper
														.readTree(String.valueOf(taxJsonLs.get(i)));
												if (taxJsonMap.size() > 0) {
													Map<String, Object> taxesTimeStop = new HashMap();
													taxesTimeStop.put("description", taxJsonMap.get("name").asText()
															+ " " + taxJsonMap.get("percnt").asText() + "%");
													JsonNode jsonNode = taxJsonMap.get("amount");
													if (jsonNode != null) {
														taxesTimeStop.put("amount", jsonNode.asText());
													} else {
														taxesTimeStop.put("amount", 0.00);
													}

													taxes.add(taxesTimeStop);
												}
											}
										}
									}
								}
							}

							String chargingPeriod = getChargingPeriodforCdr(sessionId);
							String chargingPeriodToStore = chargingPeriod == null ? null : chargingPeriod.equalsIgnoreCase("null") ? null : "'" + chargingPeriod + "'";

							String insertCDR = "insert into ocpi_cdr (id,country_code,party_id,start_date_time,end_date_time,session_id,tariffs,cdr_token,auth_method,authorization_reference,"
									+ " cdr_location,meter_id,currency,total_cost,total_fixed_cost,total_energy,total_energy_cost,"
									+ " total_time,total_time_cost,remark,invoice_reference_id,"
									+ " credit,credit_reference_id,home_charging_compensation,taxes,last_updated,total_parking_time,charging_period) values ("
									+ " '" + sessionId + "','" + String.valueOf(OcpiToken.get(0).get("country_code"))
									+ "','" + String.valueOf(OcpiToken.get(0).get("party_id")) + "','" + startTime
									+ "'," + " '" + endTime + "','" + sessionId + "','"
									+ gettariffsById(Long.valueOf(tariffId),
											String.valueOf(OcpiToken.get(0).get("party_id")))
									+ "'," + " '" + String.valueOf(cdrToken.getId()) + "','AUTH_REQUEST','" + sessionId
									+ "'," + " '" + cdrLocationId + "','" + meter_id + "','" + currency + "'," + " '"
									+ total_cost_id + "','" + finalCostInSlcCurrency + "','" + kilowattHoursUsed + "',"
									+ " '" + 0 + "','" + (Double.parseDouble(sessionElapsedInMin) / 60) + "','"
									+ finalCostInSlcCurrency + "',"
									+ " 'Session ended because the cable was disconnected by user.','"
									+ String.valueOf(UUID.randomUUID()) + "'," + " '0','','0','"
									+ objectMapper.writeValueAsString(taxes) + "','" + utils.getUTCDateString() + "',"
									+ 0 + ","+chargingPeriodToStore+")";
							executeRepository.update(insertCDR);
							executeRepository.update("update ocpi_cdr set total_parking_cost='" + idlejson
									+ "' where session_id = '" + sessionId + "'");
							deleteChargingPeriod(sessionId);
							postStopSession(String.valueOf(sessionId),
									String.valueOf(OcpiToken.get(0).get("country_code")),
									String.valueOf(OcpiToken.get(0).get("party_id")));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void postStopSession(String cdrId, String country_code, String party_id) {
		logger.info("start postStopSession");
		Thread th = new Thread() {
			public void run() {
				String urlToRead = ocpiUrl + "ocpi/ocpp/cdr/" + country_code + "/" + party_id + "/" + cdrId + "";
				StringBuilder result = new StringBuilder();
				URL url = null;
				try {
					url = new URL(urlToRead);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("POST");
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(5000);
					BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

					String line;
					while ((line = rd.readLine()) != null) {
						result.append(line);
					}
					rd.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		};
		th.start();
		logger.info("end postStopSession");
	}

	public String gettariffsById(long tariff_id, String party_id) {
		logger.info("start gettariffsById");
		logger.info("tariff_id : " + tariff_id);

		String query = "DECLARE @json nvarchar(max);WITH src (n) AS ( "
				+ "SELECT t.uuid AS 'id',t.currency AS 'currency',tariff_alt_text = (SELECT dt.text, dt.language  "
				+ "FROM tariff_in_display_text titt INNER JOIN displayText dt ON dt.id = titt.displayText_id   "
				+ "WHERE titt.tariff_id = t.id FOR JSON PATH),  "
				+ "t.tariff_alt_url AS 'tariff_alt_url',FORMAT(t.last_updated, 'yyyy-MM-ddTHH:mm:ssZ') AS 'last_updated',  "
				+ "elements = (SELECT price_components = (SELECT  CASE WHEN pc.type = 'Time' THEN 'PARKING_TIME'  "
				+ "ELSE UPPER(pc.type) END AS 'type',  "
				+ "CASE  WHEN pc.step_size = 60 AND pc.type='Time' THEN (ROUND(pc.price * 60, 4))  "
				+ "WHEN pc.step_size = 3600 THEN (ROUND(pc.price, 4))  ELSE (ROUND(pc.price, 4))  "
				+ " END AS 'price', CASE WHEN pc.step_size = 60 AND pc.type='Time' THEN 1 "
				+ "WHEN pc.step_size = 3600 THEN 60  ELSE pc.step_size END AS 'step_size'  "
				+ " FROM tariff_priceComponent pc   INNER JOIN tariff_element tet ON tet.id = pc.element_id  "
				+ "WHERE tet.id = te.id AND pc.type NOT IN ('Parking', '+ve', '-ve', 'Flat')  FOR JSON PATH),  "
				+ "tr.start_time AS 'restrictions.start_time', tr.end_time AS 'restrictions.end_time',  "
				+ "CASE  WHEN tr.restrictionType = 'Idle Charge' THEN tr.gracePeriod * 60  ELSE NULL  "
				+ "END AS 'restrictions.min_duration',  tr.start_date AS 'restrictions.start_date',  "
				+ "tr.end_date AS 'restrictions.end_date', tr.min_kwh AS 'restrictions.min_kwh',  "
				+ "tr.max_kwh AS 'restrictions.max_kwh', tr.min_current AS 'restrictions.min_current', "
				+ "tr.max_current AS 'restrictions.max_current',  tr.min_power AS 'restrictions.min_power', "
				+ "tr.max_power AS 'restrictions.max_power',  tr.max_duration AS 'restrictions.max_duration', "
				+ "CASE  WHEN w.value = 'Mon' THEN 'MONDAY' WHEN w.value = 'Tue' THEN 'TUESDAY'  "
				+ " WHEN w.value = 'Wed' THEN 'WEDNESDAY'  WHEN w.value = 'Thu' THEN 'THURSDAY'  "
				+ " WHEN w.value = 'Fri' THEN 'FRIDAY'  WHEN w.value = 'Sat' THEN 'SATURDAY'  "
				+ " WHEN w.value = 'Sun' THEN 'SUNDAY'  END AS 'restrictions.day_of_week', "
				+ "tr.reservation AS 'restrictions.reservation'  FROM tariff_element te  "
				+ "LEFT JOIN tariff_restictions tr ON tr.id = te.restrictions   "
				+ "LEFT JOIN restriction_In_Day rid ON rid.restrictionId = tr.id "
				+ "LEFT JOIN week w ON w.id = rid.dayId "
				+ "INNER JOIN tariff_element_type tet ON tet.element_id = te.id  WHERE tet.tariff_id = t.id "
				+ "AND (tr.restrictionType != 'TAX' OR tr.restrictionType IS NULL) AND EXISTS (SELECT 1  "
				+ "FROM tariff_priceComponent pc  "
				+ "WHERE pc.element_id = te.id AND pc.type NOT IN ('Parking', '+ve', '-ve', 'Flat') "
				+ ") FOR JSON PATH)  FROM tariff t  INNER JOIN station_in_tariff sit ON sit.tariffId = t.id  "
				+ " INNER JOIN station st ON st.id = sit.stationId   "
				+ " INNER JOIN site s ON s.siteId = st.siteId where t.id='" + tariff_id + "'  "
				+ " GROUP BY t.uuid, t.currency, t.id, t.tariff_alt_url, t.last_updated "
				+ " FOR JSON PATH)SELECT @json = src.n FROM src SELECT @json AS 'tariff'";

		List<Map<String, Object>> list = executeRepository.findAll(query);

		logger.info("tariffs : " + list.size());

		List<TariffResponse> tariff = new ArrayList<>();

		try {

			TypeReference<List<TariffResponse>> mapType = new TypeReference<List<TariffResponse>>() {
			};

			if (list.size() > 0 && list.get(0).get("tariff") != null)
				tariff = objectMapper.readValue(list.get(0).get("tariff").toString(), mapType);

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		for (TariffResponse tariffResponse : tariff) {

			try {

				Map<String, Object> getroamingtariffpermission = getroamingtariffpermission(tariffResponse.getId(),
						party_id);

				if (getroamingtariffpermission != null) {

					String text = tariffResponse.getTariff_alt_text().iterator().next().getText() + " + " + "$"
							+ Double.valueOf(getroamingtariffpermission.get("price").toString())
							+ "(CAD) Transaction Fee";
					tariffResponse.getTariff_alt_text().iterator().next().setText(text);

					PriceComponent priceComponent = new PriceComponent();
					priceComponent.setPrice((double) getroamingtariffpermission.get("price"));
					priceComponent.setType((String) getroamingtariffpermission.get("type"));
					priceComponent.setStep_size((int) getroamingtariffpermission.get("step_size"));
					TariffElement tariffElement = new TariffElement();
					tariffElement.setPrice_components(Collections.singletonList(priceComponent));
					Set<TariffElement> elements = tariffResponse.getElements();
					elements.add(tariffElement);

					Thread.sleep(1000);

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		String tariffs = "[]";

		try {
			if (list.size() > 0 && list.get(0).get("tariff") != null
					&& !String.valueOf(list.get(0).get("tariff")).equalsIgnoreCase("null")) {

				String writeValueAsString = objectMapper.writeValueAsString(tariff);

				tariffs = String.valueOf(writeValueAsString);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("end gettariffsById");
		return tariffs;
	}

	public void deleteChargingPeriod(String sessionId) {
		try {
			executeRepository.execute("DELETE FROM ocpi_cdr_dimension "
					+ "WHERE chargingPeriod_id IN (SELECT id FROM ocpi_charging_period WHERE session_id  in ('"+ sessionId + "'))");
			executeRepository.execute("delete ocpi_charging_period where session_id in ('" + sessionId + "')");
		}
		catch (Exception e) {
		}
	}

	public String getChargingPeriodforCdr(String id) {

		String query = "DECLARE @json nvarchar(max); WITH src (n) AS (SELECT FORMAT(ocp.start_date_time, 'yyyy-MM-ddTHH:mm:ssZ') AS 'start_date_time', ocp.tariff_id AS 'tariff_id', "
				+ "(SELECT  ocd.type as 'type',ocd.volume  as 'volume' FROM ocpi_cdr_dimension ocd  "
				+ "WHERE ocd.chargingPeriod_id = ocp.id FOR JSON PATH ) AS 'dimensions' "
				+ "FROM ocpi_charging_period ocp WHERE ocp.session_id = '" + id + "' "
				+ "order by ocp.start_date_time desc  for json path) SELECT @json = src.n FROM src SELECT @json as 'chargingPeriod' ";

		List<Map<String, Object>> list = executeRepository.findAll(query);

		String chargingPeriod = null;

		try {

			if (list.size() > 0 && list.get(0).get("chargingPeriod") != null) {

				chargingPeriod = String.valueOf(list.get(0).get("chargingPeriod"));

			}

		} catch (Exception e) {

		}

		return chargingPeriod;

	}


	public void deleteTnxData(String sessionId) {
		try {
			String query = "delete from ocpp_TransactionData where sessionid='" + sessionId + "'";
			executeRepository.execute(query);
			
			String delete="delete from  session_energy where sessionId='"+sessionId+"'";
			executeRepository.update(delete);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteocpiTnxData(String sessionId) {
		try {
			String query = "delete from ocpi_TransactionData where sessionid='" + sessionId + "'";
			executeRepository.execute(query);
			
			String delete="delete from  session_energy where sessionId='"+sessionId+"'";
			executeRepository.update(delete);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map<String, Object> getOCPIData(long portId) {
		Map<String, Object> finalData = new HashMap<>();
		try {
			String connectorFormat = "";
			String powerType = "";
			String connectorType = "";
			String latitude = "";
			String longitude = "";
			String streetName = "";
			String city = "";
			String connector_id = "";
			String country = "";
			String portuuid = "";
			String siteName = "";
			String postal_code = "";
			String siteuuid = "";
			String state = "";
			String referNo = "";
			String query = "select isnull(power_type,1) as power_type,p.format,p.standard,s.coordinateId,s.streetName,s.city,connector_id,s.country,p.uuid as portuuid,"
					+ " s.siteName,postal_code,s.uuid as siteuuid,s.state,st.referNo from site s inner join station st on st.siteId=s.siteId inner join "
					+ " port p on st.id=p.station_id where p.id=" + portId + "";
			List<Map<String, Object>> list = executeRepository.findAll(query);
			if (list.size() > 0) {
				Map<String, Object> map = list.get(0);
				streetName = String.valueOf(map.get("streetName"));
				city = String.valueOf(map.get("city"));
				connector_id = String.valueOf(map.get("connector_id"));
				country = String.valueOf(map.get("country"));
				portuuid = String.valueOf(map.get("portuuid"));
				siteName = String.valueOf(map.get("siteName"));
				postal_code = String.valueOf(map.get("postal_code"));
				siteuuid = String.valueOf(map.get("siteuuid"));
				state = String.valueOf(map.get("state"));
				referNo = String.valueOf(map.get("referNo"));
				String geoLocation = "select latitude,longitude from geoLocation where id ='"
						+ String.valueOf(map.get("coordinateId")) + "'";
				List<Map<String, Object>> geoLocationList = executeRepository.findAll(geoLocation);
				if (geoLocationList.size() > 0) {
					latitude = String.valueOf(geoLocationList.get(0).get("latitude"));
					longitude = String.valueOf(geoLocationList.get(0).get("longitude"));
				}
				String format = "select name from connectorFormat where id='" + String.valueOf(map.get("format")) + "'";
				connectorFormat = executeRepository.getRecordBySqlStr(format, "name");

				String powerTypeQuery = "select name from powerType where id='" + String.valueOf(map.get("power_type"))
						+ "'";
				powerType = executeRepository.getRecordBySqlStr(powerTypeQuery, "name");

				String connector = "select name from connectorType where id='" + String.valueOf(map.get("standard"))
						+ "'";
				connectorType = executeRepository.getRecordBySqlStr(connector, "name");
			}
			finalData.put("connectorFormat", connectorFormat);
			finalData.put("powerType", powerType);
			finalData.put("connectorType", connectorType);
			finalData.put("latitude", latitude);
			finalData.put("longitude", longitude);
			finalData.put("streetName", streetName);
			finalData.put("city", city);
			finalData.put("connector_id", connector_id);
			finalData.put("country", country);
			finalData.put("portuuid", portuuid);
			finalData.put("siteName", siteName);
			finalData.put("postal_code", postal_code);
			finalData.put("siteuuid", siteuuid);
			finalData.put("state", state);
			finalData.put("referNo", referNo);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return finalData;
	}

	public Double getUserBalance(long accountId) {
		Double accBalance = 0.00;
		try {
			String str = "select accountBalance from accounts where id=" + accountId;
			List<Map<String, Object>> queryForList = executeRepository.findAll(str);
			if (queryForList.size() > 0) {
				accBalance = Double.valueOf(String.valueOf(queryForList.get(0).get("accountBalance")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accBalance;
	}

	public void balanceDeductionWithNegativeBalanceUser(Double accountBalance, Double revenue, Long account_id,
			String sessionId, long accTxnId, long port_id, long userId, String paymentType) {
		try {
			Map<String, Object> accntsBeanObj = ocppUserService.accntsBeanObj(userId);
			String reason = reasonForTerm(sessionId);
			if (reason == null || reason.equalsIgnoreCase("null") || reason.equalsIgnoreCase("")
					|| reason.equalsIgnoreCase("Insession")) {
				reason = "EVDisconnected";
			}
			String userCurrencyCheck = String.valueOf(accntsBeanObj.get("currencyType"));
			String siteCurrencyCheck = String.valueOf(ocppUserService.getSiteDetails(port_id).get("currencyType"));
			logger.info("892 >> revenue : " + revenue);
			logger.info("userCurrencyCheck : " + userCurrencyCheck + " , siteCurrencyCheck : " + siteCurrencyCheck);
			if (!String.valueOf(userCurrencyCheck).equalsIgnoreCase("null") && !String.valueOf(siteCurrencyCheck).equalsIgnoreCase("null") && !userCurrencyCheck.equalsIgnoreCase(siteCurrencyCheck)) {
				revenue = Double.parseDouble(String.valueOf(ocppUserService.convertCurrency(userCurrencyCheck,
						siteCurrencyCheck, new BigDecimal(String.valueOf(revenue)))));
			}
			revenue = utils.decimalwithtwodecimals(revenue);
			logger.info("896 >> revenue : " + revenue);
			if (paymentType.equalsIgnoreCase("Card") || paymentType.contains("Card") || paymentType.contains("card")) {
				String str = "update session set settlement='settled', settlementTimeStamp=GETUTCDATE(), reasonForTer='" + reason + "' where sessionId = '"
						+ sessionId + "'";
				generalDao.updateSqlQuiries(str);

				String str3 = "insert into account_transaction(amtCredit,amtDebit,comment,createTimeStamp,currencyType,currentBalance,paymentMode,status,tax1_amount,tax1_pct,tax2_amount,tax2_pct,"
						+ "tax3_amount,tax3_pct,transactionType,account_id,lastUpdatedTime,sessionId,currencyRate) values(0,0,'Vehicle charging','"
						+ utils.getUTCDateString() + "','CAD',0,'Credit Card','SUCCESS'," + "0,0,0,0,0,0,'session',"
						+ account_id + ",'" + utils.getUTCDateString() + "','" + sessionId + "',0)";

				generalDao.updateSqlQuiries(str3);

				charge(sessionId, revenue, String.valueOf(accntsBeanObj.get("uuid")), accTxnId);
			} else {
				if (accountBalance < revenue) {
					double bal = accountBalance - revenue;
					String str = "update session set settlement='settled',settlementTimeStamp=GETUTCDATE() ,reasonForTer='" + reason
							+ "' where sessionId = '" + sessionId + "'";
					generalDao.updateSqlQuiries(str);

					String str1 = "update Accounts set accountBalance=" + bal + " where id=" + account_id;
					generalDao.updateSqlQuiries(str1);

					String str3 = "insert into account_transaction(amtCredit,amtDebit,comment,createTimeStamp,currencyType,currentBalance,paymentMode,status,tax1_amount,tax1_pct,tax2_amount,tax2_pct,"
							+ "tax3_amount,tax3_pct,transactionType,account_id,lastUpdatedTime,sessionId,currencyRate) values(0,'"
							+ revenue + "','Vehicle charging','" + utils.getUTCDateString() + "','CAD','" + bal
							+ "','Wallet','SUCCESS'," + "0,0,0,0,0,0,'session'," + account_id + ",'"
							+ utils.getUTCDateString() + "','" + sessionId + "',0)";
					generalDao.updateSqlQuiries(str3);

					if (bal <= -0.50) {
						String url = mobileServerUrl + "api/v3/payment/paymentIntent/payDueAmount";
						logger.info("negative Balance user api calling : " + url);
						Map<String, Object> params = new HashMap<String, Object>();
						params.put("uid", String.valueOf(accntsBeanObj.get("uuid")));

						logger.info("negative Balance user api request body : " + params);
						HttpHeaders headers = new HttpHeaders();
						headers.set("Content-Type", "application/json");
						headers.set("EVG-Correlation-ID", mobileAuthKey);
						HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);
						apicallingPOST(url, requestEntity);

						logger.info("899 >> sessionId : " + sessionId + " , accountId : " + account_id
								+ " , negative Balance API : " + url + " , revenue : " + revenue);
					}
				} else if (accountBalance > revenue) {
					double bal = accountBalance - revenue;

					String str = "update session set settlement='settled', settlementTimeStamp=GETUTCDATE(),reasonForTer='" + reason
							+ "' where sessionId = '" + sessionId + "'";
					generalDao.updateSqlQuiries(str);

					String str1 = "update Accounts set accountBalance=" + bal + " where id=" + account_id;
					generalDao.updateSqlQuiries(str1);

					String str3 = "insert into account_transaction(amtCredit,amtDebit,comment,createTimeStamp,currencyType,currentBalance,paymentMode,status,tax1_amount,tax1_pct,tax2_amount,tax2_pct,"
							+ "tax3_amount,tax3_pct,transactionType,account_id,lastUpdatedTime,sessionId,currencyRate) values(0,'"
							+ revenue + "','Vehicle charging','" + utils.getUTCDateString() + "','CAD','" + bal
							+ "','Wallet','SUCCESS'," + "0,0,0,0,0,0,'session'," + account_id + ",'"
							+ utils.getUTCDateString() + "','" + sessionId + "',0)";
					generalDao.updateSqlQuiries(str3);

					logger.info("911 >> sessionId : " + sessionId + " , accountId : " + account_id
							+ " , no negative Balance API calling");
				} else {
					String str = "update session set settlement='settled',settlementTimeStamp=GETUTCDATE(),reasonForTer='" + reason
							+ "' where sessionId = '" + sessionId + "'";
					generalDao.updateSqlQuiries(str);

					String str1 = "update Accounts set accountBalance=" + 0 + " where id=" + account_id;
					generalDao.updateSqlQuiries(str1);

					String str3 = "insert into account_transaction(amtCredit,amtDebit,comment,createTimeStamp,currencyType,currentBalance,paymentMode,status,tax1_amount,tax1_pct,tax2_amount,tax2_pct,"
							+ "tax3_amount,tax3_pct,transactionType,account_id,lastUpdatedTime,sessionId,currencyRate) values(0,'"
							+ revenue + "','Vehicle charging','" + utils.getUTCDateString() + "','CAD','" + 0
							+ "','Wallet','SUCCESS'," + "0,0,0,0,0,0,'session'," + account_id + ",'"
							+ utils.getUTCDateString() + "','" + sessionId + "',0)";
					generalDao.updateSqlQuiries(str3);

					logger.info("922 >> sessionId : " + sessionId + " , accountId : " + account_id
							+ " , no negative Balance API calling");
				}
			}

			String easy = "select id from account_transaction where sessionId='" + sessionId + "' ";
			List<Map<String, Object>> acc = executeRepository.findAll(easy);
			if (acc != null && acc.size() > 0) {
				String Account_transactionId = String.valueOf(acc.get(0).get("id"));
				String str = "update session set accountTransaction_id='" + Account_transactionId
						+ "' where sessionId = '" + sessionId + "'";
				executeRepository.update(str);

				insertNotificationTracker(userId, Account_transactionId, sessionId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insertNotificationTracker(long userId, String Account_transactionId, String sessionId) {
		try {
			NotificationTracker notificationTracker = new NotificationTracker();

			notificationTracker.setUserId(userId);
			notificationTracker.setAccount_transactionId(Long.parseLong(Account_transactionId));
			notificationTracker.setSessionId(sessionId);
			notificationTracker.setEmailflag(false);
			notificationTracker.setSmsflag(false);
			notificationTracker.setPushNotificationFlag(false);
			notificationTracker.setModifiedDate(utils.getUTCDate());
			notificationTracker.setResend(false);
			notificationTracker.setResendCount(0);

			generalDao.save(notificationTracker);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String reasonForTerm(String sessionId) {
		String reasonForTerminate = "EVDisconnected";
		try {
			String reason = "select reasonForTer from ocpp_TransactionData where sessionid= '" + sessionId + "'";
			List<Map<String, Object>> list = executeRepository.findAll(reason);
			if (list.size() > 0) {
				reasonForTerminate = String.valueOf(list.get(0).get("reasonForTer"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reasonForTerminate;
	}

	public String reasonForTermForOCPI(String sessionId) {
		String reasonForTerminate = "EVDisconnected";
		try {
			String reason = "select reasonForTer from ocpi_TransactionData where sessionid= '" + sessionId + "'";
			List<Map<String, Object>> list = executeRepository.findAll(reason);
			if (list.size() > 0) {
				reasonForTerminate = String.valueOf(list.get(0).get("reasonForTer"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reasonForTerminate;
	}

	public List<Map<String, Object>> getUserPaymentDetailsBySessionId(String sessionId, String uuid) {
		List<Map<String, Object>> findAll = null;
		try {
			findAll = executeRepository.findAll(
					"select id from stripeCharge where sessionId='" + sessionId + "' and uuid = '" + uuid + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return findAll;
	}

	public void charge(String sessionId, double revenue, String uuid, long accTxnId) {
		try {
			List<Map<String, Object>> userPaymentDetailsBySessionId = getUserPaymentDetailsBySessionId(sessionId, uuid);
			logger.info("userPaymentDetailsBySessionId : " + userPaymentDetailsBySessionId);
			if (userPaymentDetailsBySessionId.size() > 0) {
				String stripeChargeId = String.valueOf(userPaymentDetailsBySessionId.get(0).get("id"));
				String urlToRead = mobileServerUrl + "api/v3/payment/chargeAmount";
				logger.info("urlToRead : " + urlToRead);
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("stripeChargeId", stripeChargeId);
				params.put("amount", revenue);
				params.put("accountTransId", accTxnId);
				HttpHeaders headers = new HttpHeaders();
				headers.set("Content-Type", "application/json");
				headers.set("EVG-Correlation-ID", mobileAuthKey);
				HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);
				logger.info("payment Type stripe capture request body : " + params);

				String updateSettlementTimestamp = "update session set settlementTimestamp=GETUTCDATE() where sessionId = '" + sessionId + "'";
				generalDao.updateSqlQuiries(updateSettlementTimestamp);
				apicallingPOST(urlToRead, requestEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendReservationRefundMail(Map<String, Object> accountsObj, String reservationId, String stationRefNum,
			long portId, String refund, Long userId) {
		try {
			String userName = "";
			String userTime = "";
			String userCurrencySymbol = "";
			if (accountsObj != null) {
				userName = String.valueOf(accountsObj.get("accountName"));
				userTime = String.valueOf(accountsObj.get("userTime"));
				userCurrencySymbol = String.valueOf(accountsObj.get("currencySymbol"));
			}
			String displayName = ocppUserService.getDisplayNameByPortId(portId);
			String mailQuery = "select email from Users where UserId = " + userId;
			String mail = "";
			List<Map<String, Object>> map = executeRepository.findAll(mailQuery);
			if (map != null && map.size() > 0) {
				mail = String.valueOf(map.get(0).get("email"));
				;
			}

			String refundMail = utils.decimalwithtwoZeros(utils.decimalwithtwodecimals(Double.parseDouble(refund)));
			Map<String, Object> tamplateData = new HashMap<String, Object>();
			tamplateData.put("curDate", userTime);
			tamplateData.put("userName", userName);
			tamplateData.put("reservationId", reservationId);
			tamplateData.put("stationId", stationRefNum);
			tamplateData.put("connectorId", displayName);
			tamplateData.put("refund", userCurrencySymbol + refundMail);
			tamplateData.put("reason", "UnAvailable");
			tamplateData.put("mailType", "reservationRefund");
			tamplateData.put("orgId", 1);
			tamplateData.put("StationId", stationRefNum);
			emailServiceImpl.sendEmail(new MailForm(mail, "Reservation was Cancelled", ""), tamplateData, 1,
					stationRefNum);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void portStatusStoring(Date utcTime) {
		try {
			String query = "select distinct p.id,p.station_id,ISNULL(sn.status,'Inoperative') as status,sn.requestId,isNull(scheduleMaintenance,'scheduleMaintenance') as scheduleMaintenance from port p left join statusNotification sn \r\n"
					+ "on p.id = sn.port_Id inner join station s on s.id=p.station_id where p.station_id is not null and p.station_id != 0";
			List<Map<String, Object>> ls = executeRepository.findAll(query);
			List<IndexQuery> portin = new ArrayList<>();
			if (ls.size() > 0) {
				ls.forEach(map -> {
					try {
						esLoggerUtil.updatePortStatusLogs(Long.valueOf(String.valueOf(map.get("id"))), utcTime);
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						boolean scheduleMaintenance=Boolean.parseBoolean(String.valueOf(map.get("scheduleMaintenance")));
						portstatusindex pes = new portstatusindex();
						pes.setId(utils.getRandomNumber("transactionId"));
						pes.setCreateDate(utils.getUTCDate());
						pes.setStatus(String.valueOf(map.get("status")));
						pes.setSource("OCPP");
						pes.setStationId(Long.valueOf(String.valueOf(map.get("station_id"))));
						pes.setPortId(Long.valueOf(String.valueOf(map.get("id"))));
						pes.setTimeStamp(utcTime);
						pes.setToTimeStamp(utils.addSec(1, utcTime));
						pes.setMaintenance(scheduleMaintenance);
						IndexQuery indexQuery = new IndexQueryBuilder().withId(pes.getId().toString()).withObject(pes)
								.build();
						portin.add(indexQuery);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				esLoggerUtil.createPortErrorStatusIndexBulk(portin);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("static-access")
	@Override
	public void schedulerCharging() throws ParseException {
		String query = "select sn.status,ist.flag,ist.portId,ist.stationId,ist.profileId,ist.idTag,ist.endTime from individual_ScheduleTime ist "
				+ " inner join statusnotification sn on ist.portId = sn.port_Id where startTime <= GETUTCDATE() and profileId > 0";
		logger.info("query start ScheduleCharge" + query);
		List<Map<String, Object>> ls = executeRepository.findAll(query);
		logger.info("query start ScheduleCharge ls " + ls);
		if (ls.size() > 0) {
			logger.info("ls.size() > 0 ");
			logger.info("getting schedule_Time start list : " + ls);
			for (Map<String, Object> data : ls) {
				if (!Boolean.valueOf(String.valueOf(data.get("flag")))) {
					if (String.valueOf(data.get("status")).equalsIgnoreCase("Preparing")
							|| String.valueOf(data.get("status")).equalsIgnoreCase("Planned")) {
						BigDecimal portId = (BigDecimal) data.get("portId");
						BigDecimal stationId = (BigDecimal) data.get("stationId");
						BigDecimal profileId = (BigDecimal) data.get("profileId");
						String idTag = (String) data.get("idTag");
						String jsonInputString = "{\"profileId\":" + profileId + ",\"stationId\": " + stationId
								+ ",\"portId\": " + portId + ",\"idTag\":\"" + idTag + "\"}";
						sendDataToWebHook(jsonInputString, "scheduleStart");
					}

				} else {
					Map<String, Double> map = utils.getTimeDifferenceInMiliSec(
							utils.stringToDate(String.valueOf(data.get("endTime")).replace("T", " ").replace("Z", "")),
							utils.getUTCDate());
					double diff = Double.valueOf(String.valueOf(map.get("timeDifference")));
					logger.info("diff : " + diff);
					if (diff > 0) {
						logger.info("flag false remote stop ");
						logger.info("Inside For loop : " + data.get("portId"));
						BigDecimal portId = (BigDecimal) data.get("portId");
						BigDecimal stationId = (BigDecimal) data.get("stationId");
						BigDecimal profileId = (BigDecimal) data.get("profileId");
						String idTag = (String) data.get("idTag");
						String jsonInputString = "{\"profileId\":" + profileId + ",\"stationId\": " + stationId
								+ ",\"portId\": " + portId + ",\"idTag\":\"" + idTag + "\"}";
						logger.info("EDIT Profile : " + jsonInputString);
						sendDataToWebHook(jsonInputString, "scheduleStop");
					}
				}
			}
		}
	}

	private void sendDataToWebHook(String jsonInputString, String type) {
		try {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(ocppURL + "/amp/" + type);
			logger.info("****##httpPost****: " + httpPost);
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

	@Override
	public void sendTriggerMeterForVariablePower() {
		try {
			List<PayloadData> payLoadList = new ArrayList<PayloadData>();
			String apiName = "load/variable_meterValue_scheduler";
			String date = utils.getUTCDateString();
			String utcTime = date.replace(" ", "T");
			logger.info("eneterd to sendTriggerMeterForVariablePower 1513 :");
			String query = "select n.powerUnit,f.stationId,f.portId,f.powerImportAvg,f.refSessionId,f.sessionId,f.transactionId,f.socValue,f.powerImportValue,f.RFID,f.connectorId,f.profileId,f.sessionEnd,isnull(f.powerActiveImportUnit,'W') as powerActiveImportUnit,isnull(f.meterEndTime,'"
					+ utils.getUTCDateString()
					+ "') as meterEndTime from fleet_sessions f inner join network_profile n on f.profileId = n.id where f.status= 'Active' and f.meterEndTime>'"
					+ utils.minusSec(14400) + "' and f.sessionEnd < '" + utils.minusSec(59)
					+ "' and f.priority !='Opt-Out'";
			logger.info("sendTriggerMeterForVariablePower query 1515 : " + query);
			List<Map<String, Object>> fleetSessionActiveData = executeRepository.findAll(query);
			logger.info("sendTriggerMeterForVariablePower query data 1517 : " + fleetSessionActiveData);
			if (fleetSessionActiveData.size() > 0) {
				fleetSessionActiveData.forEach(activeSessionData -> {
					// long profileId =
					// Long.valueOf(String.valueOf(activeSessionData.get("profileId")));
					// String powerUnitQuery = "Select powerUnit from network_Profile where id
					// ="+profileId;
					// logger.info("powerUnitQuery : "+powerUnitQuery);
					// List<Map<String,Object>> lsMap = jdbcTemplate1.queryForList(powerUnitQuery);
					// String powerUnit = String.valueOf(activeSessionData.get("powerUnit"));
					String powerActiveImportUnit = String.valueOf(activeSessionData.get("powerActiveImportUnit"));
					double powerImportValue = Double.valueOf(String.valueOf(activeSessionData.get("powerImportValue")));
					if (powerActiveImportUnit.equalsIgnoreCase("W")) {
						powerImportValue = powerImportValue * 1000;
					}
					String requestType = "MeterValue";
					PayloadData pd = new PayloadData();
					pd.setConnectorId(Long.valueOf(String.valueOf(activeSessionData.get("connectorId"))));
					pd.setIdTag(String.valueOf(activeSessionData.get("RFID")));
					pd.setNetworkId(Long.valueOf(String.valueOf(activeSessionData.get("profileId"))));
					pd.setPortId(Long.valueOf(String.valueOf(activeSessionData.get("portId"))));
					pd.setPowerImportUnit(powerActiveImportUnit);
					pd.setPowerImportValue(powerImportValue);
					pd.setRefSessionId(String.valueOf(activeSessionData.get("refSessionId")));
					pd.setRequestType(requestType);
					pd.setSessionId(String.valueOf(String.valueOf(activeSessionData.get("sessionId"))));
					pd.setSocValue(Double.valueOf(String.valueOf(activeSessionData.get("socValue"))));
					pd.setStationId(Long.valueOf(String.valueOf(activeSessionData.get("stationId"))));
					pd.setStartTime(String.valueOf(activeSessionData.get("sessionEnd")));
					pd.setTransactionId(Integer.valueOf(String.valueOf(activeSessionData.get("transactionId"))));
					pd.setPowerType(String.valueOf(activeSessionData.get("powerUnit")));
					pd.setMeterEndTime(String.valueOf(activeSessionData.get("meterEndTime")));
					pd.setPowerImportAvg(Double.valueOf(String.valueOf(activeSessionData.get("powerImportAvg"))));
					payLoadList.add(pd);
				});
			}
			if (payLoadList.size() > 0) {
				CloseableHttpClient client = HttpClients.createDefault();
				String URL = LOADMANAGEMENT_URL + "/" + apiName;
				HttpHeaders headers = new HttpHeaders();
				headers.set("Content-Type", "application/json");
				HttpEntity<Object> requestEntity = new HttpEntity<>(payLoadList, headers);
				ResponseEntity<String> response = restTemplate.postForEntity(URL, requestEntity, String.class);
				logger.info(" url hitting from scheduler -> load management : " + URL);
				logger.info(" status : " + response.getStatusCode());
				client.close();
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateOncloseCount() {
		try {
			String query = "update ocpp_activeSession set oncloseCount=0";
			executeRepository.update(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void expiresCreditCard() {
		try {
			String mailSubject = "EV Driver Expires Credit Card from " + instance + " network";
			String cardDetails = "SELECT u.email from worldPay_creditCard w  INNER JOIN accounts a ON w.accountId = a.id INNER JOIN Users u ON u.UserId =a.user_id"
					+ " WHERE w.expiryYear = Right(Year(GETDATE()),2) AND w.expiryMonth = MONTH(GETDATE())+1 ";

			logger.info("Query For expires Credit Card : " + cardDetails);

			List<Map<String, Object>> expiresCardDetails = executeRepository.findAll(cardDetails);
			logger.info("Data For expires Credit Card : " + expiresCardDetails);
			for (Map<String, Object> CardMapdata : expiresCardDetails) {
				String MailContent = "Your Credit card is expiring soon. Kindly, update your new credit card in application.";
				String emailId = String.valueOf(CardMapdata.get("email"));

				Map<String, Object> mailDetails = new HashMap<String, Object>();
				mailDetails.put("event", "Expires Credit Card alert");
				mailDetails.put("Source", "BC Hydro server");
				mailDetails.put("description", MailContent);
				mailDetails.put("mailType", "expiresCreditCard");
				mailDetails.put("heading", "Expires Credit Card");
				mailDetails.put("curDate", String.valueOf(new Date()));
				mailDetails.put("orgId", "1");
				emailServiceImpl.sendEmail(new MailForm(emailId, mailSubject, ""), mailDetails, 1, "");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void ocpiInvalidTxnsSettlementOCPISession() {
		try {
			String query = "select authorization_reference from ocpi_session where status='PENDING' and start_date_time < DATEADD(MINUTE,-15,GETUTCDATE());";
			// String query = "select authorization_reference from ocpi_session where
			// status='PENDING'";
			logger.info("ocpi_session pending sessions query : " + query);
			List<Map<String, Object>> findAll = executeRepository.findAll(query);
			logger.info("ocpi_session pending sessions data : " + findAll);
			findAll.forEach(map -> {
				String auth_reference = String.valueOf(map.get("authorization_reference"));
				String update_query = "update ocpi_session set status='INVALID',last_updated='"
						+ utils.getUTCDateTimeString() + "' where authorization_reference = '" + auth_reference + "'";
				executeRepository.update(update_query);
				logger.info("ocpi_session updating to Invalid : " + update_query);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteInActiveTransactionsData() {
		try {
			String str = "delete from ocpp_activeTransaction where timeStamp < DATEADD(MINUTE,-3,GETUTCDATE()) AND sessionId is null";
			executeRepository.execute(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void paygUserCancelAuth() {
		try {
			String str = "select id as preAuthId,deviceType from userPayment where userType='GuestUser' and authorizeAmount > 0 and authorizeDate < DATEADD(MINUTE,-15,GETUTCDATE()) AND sessionId "
					+ " is null and flag=1 and paymentMode != 'Freeven'";
			logger.info("paygUserCancelAuth query : " + str);
			List<Map<String, Object>> findAll = executeRepository.findAll(str);
			logger.info("paygUserCancelAuth data : " + findAll);
			findAll.forEach(map -> {
				String userPaymentId = String.valueOf(map.get("preAuthId"));
				try {
					if (String.valueOf(map.get("deviceType")).equalsIgnoreCase("Android")
							|| String.valueOf(map.get("deviceType")).equalsIgnoreCase("iOS")) {
						String urlToRead = mobileServerUrl + "api/v3/payment/paymentIntent/cancelAuthorization";
						Map<String, Object> params = new HashMap<String, Object>();
						params.put("userPaymentId", userPaymentId);
						HttpHeaders headers = new HttpHeaders();
						headers.set("Content-Type", "application/json");
						headers.set("EVG-Correlation-ID", mobileAuthKey);
						HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);
						logger.info("paygUserCancelAuth Android/iOS url : " + urlToRead);
						logger.info("paygUserCancelAuth request body : " + params);
						apicallingPOST(urlToRead, requestEntity);
					} else if (String.valueOf(map.get("deviceType")).equalsIgnoreCase("Web")) {
						String urlToRead = mobileServerUrl + "api/v3/payment/stripe/cancelAuthorization";
						Map<String, Object> params = new HashMap<String, Object>();
						params.put("userPaymentId", userPaymentId);
						HttpHeaders headers = new HttpHeaders();
						headers.set("Content-Type", "application/json");
						headers.set("EVG-Correlation-ID", mobileAuthKey);
						HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);
						logger.info("paygUserCancelAuth Web url : " + urlToRead);
						logger.info("paygUserCancelAuth request body : " + params);
						apicallingPOST(urlToRead, requestEntity);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void unsettledTransactions() {
		try {
			String query = "select distinct id as sessionId,emailId from session where userId>0 and (accountTransaction_id is null or accountTransaction_id = 0) and ((settlement!='settled' and"
					+ " endTimeStamp<DATEADD(DAY,-2,GETUTCDATE()) and endTimeStamp>DATEADD(DAY,-3,GETUTCDATE())) or(settlement='settled' and endTimeStamp<DATEADD(DAY,-1,GETUTCDATE()) and "
					+ "endTimeStamp>DATEADD(DAY,-2,GETUTCDATE())))";
			List<Map<String, Object>> registerList = executeRepository.findAll(query);

			String query1 = "select distinct s.id as sessionId,phone from session s left join userPayment u on u.sessionId=s.sessionId where txnType='PAYG' and finalCostInSlcCurrency>0 and flag=1 "
					+ "and captureAmount=0  and ((settlement!='settled' and endTimeStamp<DATEADD(DAY,-2,GETUTCDATE()) and endTimeStamp>DATEADD(DAY,-3,GETUTCDATE())) or(settlement='settled' and "
					+ "endTimeStamp<DATEADD(DAY,-1,GETUTCDATE()) and endTimeStamp>DATEADD(DAY,-2,GETUTCDATE())))";
			List<Map<String, Object>> payGList = executeRepository.findAll(query1);

			if (registerList.size() > 0 || payGList.size() > 0) {
				emailServiceImpl.internalMail(registerList, payGList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map<String, Object> getroamingtariffpermission(String tariffUid, String party_id) {

		String tariffpermissionQuery = "select UPPER(pc.type) AS 'type', "
				+ "CASE  WHEN pc.type = 'Flat' then (ROUND(pc.price, 4))  END AS 'price', "
				+ "1 AS 'step_size' from  tariff_priceComponent pc  "
				+ "inner join tariff_element te on te.id = pc.element_id   "
				+ "inner join tariff_element_type tet on tet.element_id = te.id "
				+ "inner join tariff t on t.id = tet.tariff_id "
				+ "left join tariff_restictions tr on tr.id= te.restrictions   "
				+ "left join restriction_in_partyid rip on rip.restrictionId = tr.id "
				+ "inner join station_in_tariff sit on sit.tariffId = t.id "
				+ "inner join station st on st.id = sit.stationId " + "inner join site s on s.siteId= st.siteId "
				+ "where s.ocpiflag = 1 and rip.partyId = '" + party_id + "' and t.uuid= '" + tariffUid
				+ "' AND pc.type = 'Flat'";

		List<Map<String, Object>> data = executeRepository.findAll(tariffpermissionQuery);

		if (data.size() > 0) {

			return data.get(0);

		} else {

			return null;

		}

	}
}
