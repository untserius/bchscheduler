package com.evg.scheduler.service;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ocppUserService {
  Map<String, Object> getSecondaryPropety(long paramLong);
  
  Map<String, Object> getPrimaryPropety(long paramLong);
  
  Map<String, Object> logoDeatils(long paramLong);
  
  Map<String, Object> getOrgData(long paramLong);
  
  BigDecimal convertCurrency(String paramString1, String paramString2, BigDecimal paramBigDecimal);
  
  BigDecimal currencyRate(String paramString1, String paramString2);
  
  Map<String, Object> getSiteDetails(long paramLong);
  
  Map<String, Object> accntsBeanObj(long paramLong);
  
  void insertIntoAccountTransaction(Map<String, Object> paramMap, double paramDouble1, String paramString1, Date paramDate, double paramDouble2, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, float paramFloat, double paramDouble3, String paramString8, String paramString9);
  
  void reservationPushNotification(Map<String, Object> paramMap, String paramString1, String paramString2);
  
  String getDisplayNameByPortId(long paramLong);
  
  Map<String, Object> getOrgData(long paramLong, String paramString);
  
  void updatingLastUpdatedtime(String paramString, long paramLong1, long paramLong2);
  
  void updatePortStatus(long paramLong, String paramString);
  
  void postlastupdated(long paramLong);
  
  List<Map<String, Object>> getDeviceByUser(Long paramLong);

byte[] createExcelSheet(List<Map<String, Object>> registeredUser, List<Map<String, Object>> payGUser);

String internalMail();
}
