package com.evg.scheduler.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.evg.scheduler.service.OCPPService;
import com.evg.scheduler.service.intervalService;
import com.evg.scheduler.utils.utils;

@Component
public class scheduledTasks {
	@Autowired
	private intervalService intervalService;

	@Autowired
	private utils utils;

	@Autowired
	private OCPPService ocppService;

	private static final Logger logger = LoggerFactory.getLogger(com.evg.scheduler.task.scheduledTasks.class);

	@Scheduled(cron = "0 0/15 * * * ?")//15 mins
	public void scheduler() throws ParseException {
		Date utcDate = utils.getUTCDate();
		logger.info("15 mins scheduler started at : " + utcDate);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String utctime = sdf.format(utcDate);

		try {
			intervalService.stationActiveRecordsSaving(utctime);
			logger.info("stationActiveRecordsSaving ended at : " + utils.getUTCDate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			intervalService.stationDownMailAlert();
			logger.info("stationDownMailAlert ended at : " + utils.getUTCDate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			intervalService.stationUpMailAlert();
			logger.info("stationUpMailAlert ended at : " + utils.getUTCDate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			intervalService.autoreloadTrigger();
			logger.info("autoreloadTrigger ended at : " + utils.getUTCDate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ocppService.updateReservation();
			logger.info("Update Reservation end time : " + new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ocppService.closeIdleSession(utctime);
			logger.info("updatingCloseidleSession : " + utils.getUTCDate());
		} catch (Exception e) {
			e.printStackTrace();
		}
//		try {
//			intervalService.ocpiInvalidTxnsSettlementOCPISession();
//			logger.info("ocpiTxnsSettlement ended at : " + utils.getUTCDate());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		try {
			intervalService.updatingInCompletedTrasactions();
			logger.info("updatingInCompletedTrasactions at : " + utils.getUTCDate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		try {
			intervalService.paygUserCancelAuth();
			logger.info("paygUserCancelAuth ended at : " + utils.getUTCDate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			 
			ocppService.deleteIndividualScheduleTime(utctime);
			logger.info("deleteIndividualScheduleTime at : " + utils.getUTCDate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("15 mins scheduler ended at : " + utils.getUTCDate());
	}

	@Scheduled(cron = "0 0/10 * * * ?")//10 mins
	public void tenMinsScheduler() {
		logger.info("10 mins scheduler started at : " + utils.getUTCDate());
		try {
			intervalService.updateStationStatusUnavailable();
			logger.info("updateStationStatusUnavailable ended at : " + utils.getUTCDate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			intervalService.updateStationStatusAvailable();
			logger.info("updateStationStatusAvailable ended at : " + utils.getUTCDate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("10 mins scheduler ended at : " + utils.getUTCDate());
	}

	@Scheduled(cron = "0 55 23 * * ?", zone = "UTC")//Daily Beginning of the day
	public void PSTMidNightStnMails() {
		logger.info("24 hours scheduler started at : " + utils.getUTCDate());

		Date utcDate = utils.getUTCDate();
//		try {
//			intervalService.stationDownMailAlertEverDayMidnight();
//			logger.info("stationDownMailAlertEverDayMidnight ended at : " + utils.getUTCDate());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		try {
			intervalService.updateCurrency();
			logger.info("updateCurrency ended at : " + utils.getUTCDate());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			intervalService.portStatusStoring(utcDate);
			logger.info("portStatusStoring ended at : " + new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			 intervalService.updateOncloseCount();
			 logger.info("OncloseCount ended at : " + utils.getUTCDate());
		}catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("24 hours scheduler ended at : " + utils.getUTCDate());
	}
	
	@Scheduled(cron = "0 0 0 * * ?", zone = "UTC")
	public void dayStart() {
		try {
			intervalService.registeredUsersAmountCapture();
			logger.info("negativeBalanceUsersData ended at : " + utils.getUTCDate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			intervalService.OCPIsettlement();
			logger.info("OCPIsettlement ended at : " + utils.getUTCDate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			intervalService.paygUserAmountCapture();
			logger.info("paygUserAmountCapture ended at : " + utils.getUTCDate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			intervalService.registeredUsersOfflineAmountCapture();
			logger.info("negativeBalanceUsersData ended at : " + utils.getUTCDate());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			intervalService.unsettledTransactions();
			logger.info("unsettledTransactions ended at : " + utils.getUTCDate());
		}catch (Exception e) {
			e.printStackTrace();
		}

		try {
			intervalService.cleanupOldSessionBillableData();
			logger.info("cleanupOldSessionBillableData ended at : " + utils.getUTCDate());
		} catch (Exception e) {
			e.printStackTrace();;
		}
		
		try {
			intervalService.stationUpAndDownData();
			logger.info("stationUpAndDownData ended at : " + utils.getUTCDate());
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Scheduled(cron = "0 0/30 * * * ?")
	public void thirtyMinutes() {
		try {
			logger.info("scheduler 30 mins scheduler : " + new Date());
			ocppService.triggerMessage();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		try {
//			ocppService.getAccessToken();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	@Scheduled(cron = "0 0/2 * * * ?")
	public void oneMinutes() {
		try {
			
			intervalService.deleteInActiveTransactionsData();
			logger.info("delete InActive transactions : " + utils.getUTCDate());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Scheduled(cron = "0 0/1 * * * ?")
	public void schedule_Charging() throws ParseException {
		Date utcDate = utils.getUTCDate();
		logger.info("1 mins scheduler started at : " + utcDate);
		try {
			logger.info("schedulerCharge Start Transcation started at : " + utils.getUTCDate());
			intervalService.schedulerCharging();
			logger.info("schedulerCharge Stop Transcation ended at : " + utils.getUTCDate());
		} catch (Exception e) {
			e.printStackTrace();
		}
 
		try {
			logger.info("send Trigger Meter For Variable Power : " + utils.getUTCDate());
			intervalService.sendTriggerMeterForVariablePower();
			logger.info("sendTriggerMeterForVariablePower ended at : " + utils.getUTCDate());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Scheduled(cron = "0 1 * * * ?")
//	public void oneHour() {
//		try {
//			intervalService.checkingIdleBillingExceededSessions();
//			logger.info("Every one Hour");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

//	@Scheduled(cron = "0 0/1 * * * ?")
//	public void schedule_Charging() throws ParseException {
//		Date utcDate = utils.getUTCDate();
//		logger.info("1 mins scheduler started at : " + utcDate);
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String utctime = sdf.format(utcDate);
//		try {
//			intervalService.paygUserCancelAuth();
//			logger.info("paygUserCancelAuth ended at : " + utils.getUTCDate());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
