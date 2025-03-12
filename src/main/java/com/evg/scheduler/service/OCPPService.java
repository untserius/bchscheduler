package com.evg.scheduler.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

public interface OCPPService {
  void updateReservation();
  
  void stationDownMailAlertEverDayMidnight();
  
  void configurationKeys();
  
  void defaultconfigurationKeys();
  
  void triggerMessage();
  
  void addOCPPStatusSendingData(String paramString1, String paramString2, long paramLong1, String paramString3, String paramString4, long paramLong2, String paramString5, long paramLong3, String paramString6, String paramString7, Long paramLong, Date paramDate, String paramString8);
  
  ResponseEntity<String> ocppCallingforMultiRequest(String paramString, List<Map<String, String>> paramList);
  
  ResponseEntity<String> ocppCallingforRequest(String paramString, Map<String, String> paramMap);
  
  void updateReservationId(long paramBoolean1, long paramBoolean2, String paramString, long paramBoolean3, int paramInt, long paramLong);
  
  void updateOcppStatusNotification(String paramString, Long paramLong1, Long paramLong2);
  
  void updateConnectorInNteworkProfiles(String paramString, long paramLong);
  
  String getStationStatus(long paramLong1, long paramLong2);
  
  boolean updateFlagCreditCardSession(String paramString1, long paramLong1, long paramLong2, String paramString2, String paramString3, BigDecimal paramBigDecimal, String paramString4, String paramString5, String paramString6);
  
  Map<String, Object> getSecondaryPropety(long paramLong);
  
  void cancelReservationNotification(Map<String, Object> paramMap, String paramString);
  
  String getstationRefNum(long paramLong);
  
  List getActiveTransactionIds(long paramLong);

  void closeIdleSession(String utctime);

void deleteIndividualScheduleTime(String utctime);

}
