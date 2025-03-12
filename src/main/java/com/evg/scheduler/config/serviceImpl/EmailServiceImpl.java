package com.evg.scheduler.config.serviceImpl;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.evg.scheduler.message.MailForm;
import com.evg.scheduler.service.ocppUserService;
import com.evg.scheduler.utils.utils;

@Service
public class EmailServiceImpl {

	private final static Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

	@Autowired
	private ocppUserService userService;
	
	@Autowired
	private utils utils;

	@Value("${customer.Instance}")
	String instance;

	public void sendEmail(MailForm mail, Map<String, Object> tamplateData, long orgId, String stationRefNum) {
		try {
			LOGGER.info("mail template data : "+tamplateData);
			FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
	        bean.setTemplateLoaderPath("/templates/");
	        freemarker.template.Template template = null;
	        Map<String, Object> logoData = userService.logoDeatils(1);
	        mail.setImgPath(logoData.get("url").toString());
	        
	        tamplateData.put("imagePath", String.valueOf(logoData.get("url")));
	        if(tamplateData.get("mailType").toString().equalsIgnoreCase("upRdownAlert")){
				
				template = bean.createConfiguration().getTemplate("stationAlert.ftl");
				if(tamplateData.get("heading").toString().equalsIgnoreCase("EV Station DOWN alert")) {
					mail.setMailSubject("EV charging station is disconnected from "+ instance +" network");
				}else if(tamplateData.get("heading").toString().equalsIgnoreCase("EV Station UP alert")){
					mail.setMailSubject("EV charging station is Available from "+ instance +" network");
				}
				if(tamplateData.get("ownerMails").toString().equals("1")) {
					
				}else {
					mail.setMailTo(String.valueOf(tamplateData.get("to_mail")));
				}
				LOGGER.info("up or down mail condition....."+mail);
			}
			String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, tamplateData);
	        
		Thread emailThread = new Thread() {
	        public void run() {

				JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
				mailSender.setHost(String.valueOf(tamplateData.get("from_mail_host")));
				mailSender.setPort(Integer.valueOf(String.valueOf(tamplateData.get("from_mail_port"))));
				mailSender.setUsername(String.valueOf(tamplateData.get("from_mail_auth")));
				mailSender.setPassword(String.valueOf(tamplateData.get("from_mail_password")));
				 Properties javaMailProperties = new Properties();
				 javaMailProperties.put("mail.smtp.ssl.enable", "true");
				 javaMailProperties.put("mail.smtp.auth", "true");
				 javaMailProperties.put("mail.transport.protocol", tamplateData.get("from_mail_protocol"));
				 javaMailProperties.put("mail.debug", "false");
			     mailSender.setJavaMailProperties(javaMailProperties);
			        
				MimeMessage mimeMessage = mailSender.createMimeMessage();

				try {
					MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,StandardCharsets.UTF_8.name());
					helper.addInline("logo.png", new ClassPathResource("evg.png"));
					helper.setTo(mail.getMailTo());
			        helper.setText(html, true);
			        helper.setSubject(mail.getMailSubject());
			        helper.setFrom(new InternetAddress(String.valueOf(tamplateData.get("from_mail"))));
			        if(!String.valueOf(tamplateData.get("MailCc")).equalsIgnoreCase("") && !String.valueOf(tamplateData.get("MailCc")).equalsIgnoreCase("null")) {
			        	 helper.setCc(String.valueOf(tamplateData.get("MailCc")).split(","));
			        }
					mailSender.send(mimeMessage);
					LOGGER.info("EmailServiceImpl.sendEmail() -mailTo [" + mail.getMailTo() + "] - Successfully Sent !.");
					helper = null;
				} catch (MessagingException e3) {
					e3.printStackTrace();
				}
			}
		};
		emailThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public void customerSupportMailService(MailForm mail) {
		try {
			Map<String, Object> orgData = userService.getOrgData(1);

			mail.setMailFrom(String.valueOf(orgData.get("email")));
			mail.setHost(String.valueOf(orgData.get("host")));
			mail.setPort(String.valueOf(orgData.get("port")));
			mail.setPassword(String.valueOf(orgData.get("password")));

			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			if (mail.getHost() == null || mail.getPort() == null || mail.getMailFrom() == null
					|| mail.getPassword() == null) {
				mailSender.setHost(String.valueOf(orgData.get("host")));
				mailSender.setPort(Integer.valueOf(String.valueOf(orgData.get("port"))));
				mailSender.setUsername(String.valueOf(orgData.get("email")));
				mailSender.setPassword(String.valueOf(orgData.get("password")));
			} else {
				mailSender.setHost(mail.getHost());
				mailSender.setPort(Integer.valueOf(mail.getPort()));
				mailSender.setUsername(mail.getMailFrom());
				mailSender.setPassword(mail.getPassword());
			}

			Properties javaMailProperties = new Properties();
			javaMailProperties.put("mail.smtp.ssl.enable", "true");
			javaMailProperties.put("mail.smtp.auth", "true");
			javaMailProperties.put("mail.transport.protocol", "smtp");
			javaMailProperties.put("mail.debug", "false");
			mailSender.setJavaMailProperties(javaMailProperties);

			MimeMessage mimeMessage = mailSender.createMimeMessage();
			mimeMessage.addHeader("Content-type", "text/HTML; charset=UTF-8");
			mimeMessage.addHeader("format", "flowed");
			mimeMessage.addHeader("Content-Transfer-Encoding", "8bit");

			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_NO,
					StandardCharsets.UTF_8.name());
			helper.setTo(mail.getMailTo());
			helper.setSubject(mail.getMailSubject());
			helper.setFrom(mail.getMailFrom());

			mimeMessage.setContent(mail.getMailContent(), "text/html");
			mimeMessage.setSentDate(new Date());
			mailSender.send(mimeMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void internalMail(List<Map<String,Object>> registeredUser,List<Map<String,Object>> payGUser) {
		try {
			Map<String, Object> configs = userService.getPrimaryPropety(1);
			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			mailSender.setHost(String.valueOf(configs.get("host")));
			mailSender.setPort(Integer.parseInt(String.valueOf(configs.get("port"))));
			mailSender.setUsername(String.valueOf(configs.get("email_auth")));
			mailSender.setPassword(String.valueOf(configs.get("password")));
			 Properties javaMailProperties = new Properties();
//			 javaMailProperties.put("mail.smtp.starttls.enable", "true");
			 javaMailProperties.put("mail.smtp.ssl.enable", "true");
			 javaMailProperties.put("mail.smtp.auth", "true");
			 javaMailProperties.put("mail.transport.protocol", String.valueOf(configs.get("protocol")));
			 javaMailProperties.put("mail.debug", "false");
		     mailSender.setJavaMailProperties(javaMailProperties);

			try {
				MimeMessage message = mailSender.createMimeMessage();

				message.setFrom(new InternetAddress(String.valueOf(configs.get("email"))));

				message.addRecipient(Message.RecipientType.TO, new InternetAddress(userService.internalMail()));

				message.setSubject("UnSettled Transactions from "+instance);

				MimeBodyPart messageBodyPart = new MimeBodyPart();

				messageBodyPart.setText("Please find the attached file.\n "+utils.getUTCDateString());

				Multipart multipart = new MimeMultipart();

				multipart.addBodyPart(messageBodyPart);

				byte[] byteArray = userService.createExcelSheet(registeredUser,payGUser); 

				MimeBodyPart attachmentBodyPart = new MimeBodyPart();
				attachmentBodyPart.setContent(byteArray, "application/octet-stream");
				attachmentBodyPart.setFileName("attachment_"+utils.getDateString()+".xlsx");

				multipart.addBodyPart(attachmentBodyPart);

				message.setContent(multipart);

				mailSender.send(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
