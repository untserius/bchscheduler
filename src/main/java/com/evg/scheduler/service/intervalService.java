package com.evg.scheduler.service;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

public interface intervalService {

	void stationActiveRecordsSaving(String utcTime);

	//void scheduleReports();

	void autoreloadTrigger();

	void updatingInCompletedTrasactions();

	void updateStationStatusUnavailable();
	
	void autoRenewal();

	void updateStationStatusAvailable();

	void stationDownMailAlert();

	void stationUpMailAlert();

	void stationDownMailAlertEverDayMidnight();

	void urlCalling();

	void updateCurrency();

	void paygUserAmountCapture();

	void registeredUsersAmountCapture();

	//void operativeInOperativeFun();

	void sendReservationRefundMail(Map<String, Object> accountsObj, String reservationId, String stationRefNum, long portId, String refund, Long userId);

	void portStatusStoring(Date utcTime);

	void schedulerCharging() throws ParseException;

	void sendTriggerMeterForVariablePower();

	void updateOncloseCount();

	void expiresCreditCard();

	void OCPIsettlement();

	void ocpiInvalidTxnsSettlementOCPISession();

	void deleteInActiveTransactionsData();

	void paygUserCancelAuth();

	void unsettledTransactions();

	void registeredUsersOfflineAmountCapture();

	void stationUpAndDownData();

	//void configurationKeys();

	//void triggerMessage();

}
