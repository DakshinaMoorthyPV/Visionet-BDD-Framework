package com.visionetsystems.framework.utils;

/**
 *
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.TransportEvent;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.UIManager;

import org.apache.commons.lang3.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import com.github.javafaker.Faker;

import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;

/**
 * @author Dakshina.Moorthy
 *
 */
public class EMailAutomationExecutionReport {
	public static String smtphost = UIConstantsUtil.APP_CONFIG_MAP.get("SMTPHost");
	public static int smtpport = Integer.parseInt(UIConstantsUtil.APP_CONFIG_MAP.get("SMTPPort"));
	public static String userName = UIConstantsUtil.APP_CONFIG_MAP.get("SMTPUsername");
	public static String password = TestReportUtility.decodeBase64(UIConstantsUtil.APP_CONFIG_MAP.get("SMTPPassword"));
	private static String testname = UIConstantsUtil.APP_CONFIG_MAP.get("TestEnvironment") + "-"
			+ TestReportUtility.testName().replaceAll("Log", "");
	public static String mailSubject = capitalizeFully(
			UtilityHelper.extractCompanyName() + " execution summary report for " + testname + "");
	private static final Integer PASSEDTEST = UIConstantsUtil.PASSED_TEST_CASE_COUNT;
	private static final Integer FAILEDTEST = UIConstantsUtil.FAILED_TEST_CASE_COUNT;
	private static final Integer SKIPPEDTEST = UIConstantsUtil.SKIPPED_TEST_CASE_COUNT;
	private final static Integer TOTALNOOFTESTEXECUTED = PASSEDTEST + FAILEDTEST + SKIPPEDTEST;
	private final static String PASSTEST_PERCENTAGE = String
			.valueOf(Math.round((PASSEDTEST * 100) / TOTALNOOFTESTEXECUTED) + "%");
	private final static String FAILTEST_PERCENTAGE = String
			.valueOf(Math.round((FAILEDTEST * 100) / TOTALNOOFTESTEXECUTED) + "%");
	private final static String SKIPTEST_PERCENTAGE = String
			.valueOf(Math.round((SKIPPEDTEST * 100) / TOTALNOOFTESTEXECUTED) + "%");
	private static final String TEST_INITIATED_URL;

	static {
		String testType = UIConstantsUtil.APP_CONFIG_MAP.get("TestType");
		String platform = UIConstantsUtil.APP_CONFIG_MAP.get("Platform");

		if ("Web".equalsIgnoreCase(testType) || "Hybrid".equalsIgnoreCase(testType)) {
			TEST_INITIATED_URL = UIConstantsUtil.APP_CONFIG_MAP.get("Web_BaseURL").trim();
		} else if ("API".equalsIgnoreCase(testType)) {
			TEST_INITIATED_URL = UIConstantsUtil.APP_CONFIG_MAP.get("API_BaseURL");
		} else {
			String apkPath = "Android".equalsIgnoreCase(platform)
					? UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_AndroidApk")
					: UIConstantsUtil.APP_CONFIG_MAP.get("Mobile_iOSApp");
			ApkMeta apkMeta = null;
			try (ApkFile apkFile = new ApkFile(new File(apkPath))) {
				apkMeta = apkFile.getApkMeta();
			} catch (Exception e) {
				e.printStackTrace(); // Handle exceptions appropriately
			}
			TEST_INITIATED_URL = (apkMeta != null) ? apkMeta.getPackageName() : null;
		}
	}
	private final static String AUTOMATION_TYPE = UIConstantsUtil.APP_CONFIG_MAP.get("TestType");
	private static final String BROWSER_NAME = ("Web".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("TestType"))
			|| "Hybrid".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("TestType")))
					? UIConstantsUtil.APP_CONFIG_MAP.get("Web_Browser")
					: "-";

	private static final String BROWSER_VERSION = ("Web"
			.equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("TestType"))
			|| "Hybrid".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("TestType")))
					? UIConstantsUtil.BROWSER_VERSION
					: "-";
	private final static String RELEASE = testname;
	private final static String SYSTEMUSERNAME = System.getProperty("user.name");
	private final static String REPORTDATETIME = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss.sss aa",
			java.util.Locale.ENGLISH).format(Calendar.getInstance().getTime());
	private final String PDFREPORTFILE = UIConstantsUtil.PDFPATH;
	public static List<String> tosName;
	public static boolean ismailsentsucessfully;
	public static StringBuilder mailSentErrormessage;

	public void sendEmail() throws Exception {
		try {
			if (UIConstantsUtil.APP_CONFIG_MAP.get("SendReportEmail").equalsIgnoreCase("No")) {
				return;
			}
			mailSentErrormessage = new StringBuilder();
			ismailsentsucessfully = false;
			tosName = new ArrayList<>();
			boolean isuserNameBase64 = org.apache.commons.codec.binary.Base64.isBase64(userName);
			boolean ispasswordBase64 = org.apache.commons.codec.binary.Base64.isBase64(password);
			String encrypteduserName = !isuserNameBase64 ? FileEncryption.Encrypt(userName) : userName;
			String encryptedpassword = !ispasswordBase64 ? FileEncryption.Encrypt(password) : password;
			Properties props = new Properties();
			props.put("mail.smtp.host", smtphost);
			props.put("mail.smtp.port", smtpport);
			props.put("mail.smtp.auth", UIConstantsUtil.APP_CONFIG_MAP.get("TestType"));
			props.put("mail.smtp.starttls.enable", UIConstantsUtil.APP_CONFIG_MAP.get("SMTPTLS"));
			props.put("mail.smtp.socketFactory.port", UIConstantsUtil.APP_CONFIG_MAP.get("SMTPSSLSocketPort"));
			props.put("mail.smtp.socketFactory.class", UIConstantsUtil.APP_CONFIG_MAP.get("SMTPSSLSocketClass"));
			props.put("mail.debug", "false");
			Authenticator auth = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(TestReportUtility.decodeBase64(userName),
							TestReportUtility.decodeBase64(password));
				}
			};
			Session session = Session.getInstance(props, auth);
			session.setDebug(false);

			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress("noreply@email.automation.visionet.com", "Dakshina Moorthy PV"));
			message.setSubject(mailSubject, "UTF-8");
			message.setSentDate(new Date());
			message.isSet(Flag.RECENT);
			message.setSubject(mailSubject, "UTF-8");

			String ccs[] = null;
			String bccs[] = null;
			if (UIConstantsUtil.APP_CONFIG_MAP.get("ToEmailIDs").isEmpty()
					&& UIConstantsUtil.APP_CONFIG_MAP.get("ToEmailIDs") == null) {
				throw new Exception("to_email_ids Should be empty in ToEmailIDs.xml");
			}
			String tos[] = UIConstantsUtil.APP_CONFIG_MAP.get("ToEmailIDs").contains(",")
					? UIConstantsUtil.APP_CONFIG_MAP.get("ToEmailIDs").split(",")
					: new String[] { UIConstantsUtil.APP_CONFIG_MAP.get("ToEmailIDs") };
			if (UIConstantsUtil.APP_CONFIG_MAP.get("CCEmailIDs").trim() != null
					&& UIConstantsUtil.APP_CONFIG_MAP.get("CCEmailIDs").trim().length() != 0) {
				ccs = UIConstantsUtil.APP_CONFIG_MAP.get("CCEmailIDs").contains(",")
						? UIConstantsUtil.APP_CONFIG_MAP.get("CCEmailIDs").split(",")
						: new String[] { UIConstantsUtil.APP_CONFIG_MAP.get("CCEmailIDs") };
			}
			if (!UIConstantsUtil.APP_CONFIG_MAP.get("BCCEmailIDs").isEmpty()
					&& UIConstantsUtil.APP_CONFIG_MAP.get("BCCEmailIDs") != null) {
				bccs = UIConstantsUtil.APP_CONFIG_MAP.get("BCCEmailIDs").contains(",")
						? UIConstantsUtil.APP_CONFIG_MAP.get("BCCEmailIDs").split(",")
						: new String[] { UIConstantsUtil.APP_CONFIG_MAP.get("BCCEmailIDs") };
			}
			for (String to : tos) {
				String firstName;
				StringTokenizer st = new StringTokenizer(to, "@");
				String s2 = st.nextToken();
				String[] leftPartSplitted = s2.split("\\.");
				if (leftPartSplitted.length == 2) {
					firstName = capitalizeFully(s2.replaceAll("[^a-zA-Z0-9]", " "));
					tosName.add(firstName);
				} else {
					firstName = capitalizeFully(s2.replaceAll("[^a-zA-Z0-9]", " "));
					tosName.add(firstName);
				}
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(to.trim(), firstName));
			}
			boolean flag = ccs == null;
			if (!flag) {
				for (String cc : ccs) {
					String firstName;
					StringTokenizer st = new StringTokenizer(cc, "@");
					String s2 = st.nextToken();
					String[] leftPartSplitted = s2.split("\\.");
					if (leftPartSplitted.length == 2) {
						firstName = s2.replaceAll("[^a-zA-Z0-9]", " ");
						firstName = capitalizeFully(firstName);
					} else {
						firstName = capitalizeFully(s2.replaceAll("[^a-zA-Z0-9]", " "));
					}
					message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc.trim(), firstName));
				}
			}
			flag = bccs == null;
			if (!flag) {
				for (String bcc : bccs) {
					message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc.trim()));
				}
			}
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(messageBody(), "text/html;charset=ISO-8859-1");
			String piechartImgPath = createGraphicalChart();
			Multipart multipart = new MimeMultipart();
			messageBodyPart.setContent("This is the email content", "text/html; charset=utf-8");
			multipart.addBodyPart(messageBodyPart);

			if (PDFREPORTFILE != null && !PDFREPORTFILE.isEmpty()) {
				File pdffile = new File(PDFREPORTFILE);
				if (pdffile.exists() && pdffile.isFile() && !pdffile.isDirectory()) {
					MimeBodyPart attachmentPart = new MimeBodyPart();
					DataSource source = new FileDataSource(pdffile);
					attachmentPart.setDataHandler(new DataHandler(source));
					attachmentPart.setFileName(pdffile.getName());
					multipart.addBodyPart(attachmentPart);
				} else {
					System.err.println("Invalid PDF file path or the PDF file does not exist.");
				}
			} else {
				System.err.println("PDF File Path Variable is empty; no attachment will be included.");
			}

			Map<String, String> mapInlineImages = new HashMap<>();
			File logo = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "main"
					+ File.separator + "resources" + File.separator + "Resources" + File.separator + "Report_Images"
					+ File.separator + "Visionet_Logo.jpg");
			File signLogo = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "main"
					+ File.separator + "resources" + File.separator + "Resources" + File.separator + "Report_Images"
					+ File.separator + "signlogo.png");

			mapInlineImages.put("ID123456", logo.getAbsolutePath());
			mapInlineImages.put("ID924678", signLogo.getAbsolutePath());
			mapInlineImages.put("ID737964", piechartImgPath);
			if (mapInlineImages != null && mapInlineImages.size() > 0) {
				Set<String> setImageID = mapInlineImages.keySet();
				for (String contentId : setImageID) {
					MimeBodyPart imagePart = new MimeBodyPart();
					imagePart.setHeader("Content-ID", "<" + contentId + ">");
					imagePart.setDisposition(Part.INLINE);
					String imageFilePath = mapInlineImages.get(contentId);
					try {
						imagePart.attachFile(imageFilePath, "image/png", "base64");
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					multipart.addBodyPart(imagePart);
				}
			}
			message.setContent(multipart, "UTF-8");
			message.addHeader("X-Priority", "1");
			message.setSender(new InternetAddress("noreply@email.automation.visionet.com", false));
			message.addHeader("Return-Receipt-To", new Faker(getLocaleByIP()).internet().emailAddress());
			message.reply(false);
			Transport transport = session.getTransport("smtp");
			String emailHost = smtphost;
			transport.connect(emailHost, TestReportUtility.decodeBase64(encrypteduserName),
					TestReportUtility.decodeBase64(encryptedpassword));

			Thread.sleep(1000);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			System.out.println("EMail Sent Successfully with image!!");
			ismailsentsucessfully = true;
		} catch (MessagingException e) {
			mailSentErrormessage.append(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			mailSentErrormessage.append(e.getMessage());
		} catch (Exception e) {
			mailSentErrormessage.append(e.getMessage());
		}
		if (!EMailAutomationExecutionReport.ismailsentsucessfully) {
			throw new Exception(EMailAutomationExecutionReport.mailSentErrormessage.toString());
		}
	}

	public void opened(ConnectionEvent e) {
		System.out.println("Connection opened");
	}

	public void disconnected(ConnectionEvent e) {
		System.out.println("Connection disconnected");
	}

	public void closed(ConnectionEvent e) {
		System.out.println("Connection closed");
	}

	public void messageDelivered(TransportEvent e) {
		System.out.println("Message delivered for:");
		if (e != null) {
			Address[] a = e.getValidSentAddresses();
			if (a != null && a.length > 0) {
				for (Address element : a) {
					System.out.println(((InternetAddress) element).getAddress());
				}
			}
			System.out.println("");
		}
	}

	private static Locale getLocaleByIP() {
		try {
			URL url = new URL("https://ipapi.co/country/");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String countryCode = reader.readLine().trim();
			reader.close();
			return new Locale(countryCode.toLowerCase(), countryCode);
		} catch (Exception e) {
			e.printStackTrace();
			return Locale.getDefault();
		}
	}

	public void messageNotDelivered(TransportEvent e) {
		System.out.println("Message not delivered for:");
		if (e != null) {
			Address[] a = e.getValidUnsentAddresses();
			if (a != null && a.length > 0) {
				for (Address element : a) {
					System.out.println(((InternetAddress) element).getAddress());
				}
			}
			System.out.println("");
		}
	}

	public void messagePartiallyDelivered(TransportEvent e) {
		System.out.println("These addresses are invalid:");
		if (e != null) {
			Address[] a = e.getInvalidAddresses();
			if (a != null && a.length > 0) {
				for (Address element : a) {
					System.out.println(((InternetAddress) element).getAddress());
				}
			}
			System.out.println("");
		}
	}

	public static String capitalizeFully(String str) {
		str = str.toLowerCase(); // Convert the whole string to lower case
		// Capitalize each word; assume space as the word delimiter
		String[] words = str.split("\\s");
		for (int i = 0; i < words.length; i++) {
			words[i] = StringUtils.capitalize(words[i]);
		}
		return String.join(" ", words);
	}

	/**
	 * @return
	 * @throws Exception
	 */

	private String createGraphicalChart() throws Exception {
		DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
		dataset.setValue("Success", PASSEDTEST);
		dataset.setValue("Failure", FAILEDTEST);
		dataset.setValue("Skipped", SKIPPEDTEST);
		dataset.setValue("Ignored", 0);
		dataset.setValue("Blocked", 0);
		JFreeChart someChart = ChartFactory.createPieChart("", dataset, false, true, false);
		someChart.getTitle().setFont(new java.awt.Font("Constantia", java.awt.Font.BOLD, 27));
		someChart.getTitle().setPaint(new Color(21, 35, 143));
		Color trans = new Color(0xFF, 0xFF, 0xFF, 0);
		someChart.setBackgroundPaint(trans);
		someChart.getTitle().setBackgroundPaint(trans);
		someChart.getTitle().setVisible(false);
		@SuppressWarnings("unchecked")
		PiePlot<String> plot = (PiePlot<String>) someChart.getPlot();
		plot.setOutlineVisible(false);
		UIManager.put("ToolTip.background", new Color(206, 0, 0));
		UIManager.put("ToolTip.foreground", new Color(255, 255, 255));
		UIManager.put("ToolTip.font", "Constantia");
		plot.setSectionPaint("Success", new Color(70, 193, 99));
		plot.setSectionPaint("Failure", new Color(227, 30, 38));
		plot.setSectionPaint("Skipped", new Color(255, 203, 0));
		plot.setSectionPaint("Ignored", new Color(168, 168, 168));
		plot.setSectionPaint("Blocked", new Color(82, 81, 81));
		plot.setExplodePercent("Success", 0.10);
		plot.setExplodePercent("Failure", 0.10);
		plot.setExplodePercent("Skipped", 0.10);
		plot.setExplodePercent("Ignored", 0.10);
		plot.setExplodePercent("Blocked", 0.10);
		plot.setSimpleLabels(false);
		plot.setShadowPaint(new Color(32, 36, 41));
		plot.setLabelFont(new Font("Roboto", Font.BOLD, 11));
		plot.setLabelBackgroundPaint(new Color(0xCB, 0x00, 0x00, 0xB3));
		plot.setLabelOutlinePaint(new Color(206, 0, 0));
		plot.setLabelPaint(new Color(255, 255, 255));
		plot.setLabelLinkPaint(new Color(21, 35, 143));
		plot.setLabelShadowPaint(new Color(248, 248, 248));
		plot.setLabelLinkStroke(new BasicStroke(2.0f));
		plot.setBackgroundPaint(trans);
		plot.setDefaultSectionOutlinePaint(trans);
		plot.setStartAngle(270);
		plot.setForegroundAlpha(0.60f);
		plot.setInteriorGap(0.02);
		plot.setBackgroundAlpha(0.0f);
		plot.setIgnoreNullValues(true);
		plot.setIgnoreZeroValues(true);
		plot.setNoDataMessage("No data to display");
		PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator("{0}: {1} ({2})", new DecimalFormat("0"),
				new DecimalFormat("0%"));
		plot.setLabelGenerator(gen);
		plot.setOutlineVisible(false);
		plot.setOutlinePaint(Color.red);
		int width = 600;
		int height = 400;
		float quality = 1;
		ByteArrayOutputStream chart_out = new ByteArrayOutputStream();
		ChartUtils.writeChartAsJPEG(chart_out, quality, someChart, width, height);
		ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
		File file1 = new File("temp/piechart100.png").getAbsoluteFile();
		ChartUtils.saveChartAsPNG(file1, someChart, 600, 400, info);
		return file1.getAbsolutePath();
	}

	private static String messageBody() throws Exception {
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		String yearInString = String.valueOf(year);
		String receiverNames = StringUtils.join(tosName, ", ").replaceFirst(",(?=[^,]+$)", " and").trim();
		String htmlText = "<div class=\"BG\" style=\"background-color: #f7f7f7;\">";
		htmlText += "<p style=\"margin: 0cm; font-size: 12px; font-family: Constantia; color: #000;\">Dear "
				+ receiverNames + ",</br></p>";
		htmlText += "<p style=\"margin: 0cm; font-size: 12px; font-family: Constantia; color: #000;\">&nbsp;</p>";
		htmlText += "<p style=\"margin: 0cm; font-size: 12px; font-family: Constantia; color: #000000; text-indent: 40px;\">Please find attached the Automation Execution Report for <strong>"
				+ testname + "</strong>.</br></p>";
		htmlText += "<p style=\"margin: 0cm; font-size: 12px; font-family: Constantia; color: #000000;\">The outline of the test execution results is listed below for your reference.</br></p>";
		htmlText += "<p style=\"margin: 0cm; text-indent: 40px; font-size: 12px; font-family: Constantia; color: #000000;\">&nbsp;</p>";
		htmlText += "<table style=\"width: 100%; background-color: #000; height: thin;\">";
		htmlText += "<tbody>";
		htmlText += "<tr>";
		htmlText += "<td style=\"width: 100%; height: thin;\"> </td>";
		htmlText += "</tr>";
		htmlText += "</tbody>";
		htmlText += "</table>";
		htmlText += "<table style=\"width: 100%; border-collapse: collapse; height: 42px;\">";
		htmlText += "<tbody>";
		htmlText += "<tr>";
		htmlText += "<td style=\"width: 100%; height: 19px;\"><a title=\"Visionet Systems\" href=\"https://www.visionet.com/about-us\" target=\"_blank\" rel=\"noopener noreferrer\"><img style=\"width: 13thin;\" src=\"cid:ID123456\" /></a></td>";
		htmlText += "</tr>";
		htmlText += "<tr>";
		htmlText += "<td style=\"width: 100.225%; border-width: thin; border-style: solid solid none; border-color: #da1f27; border-image: initial; text-align: center; vertical-align: middle; height: 23px; font-family: Constantia; color: #0773bb; font-size: 10px; text-shadow: 3px 3px 2px rgba(136, 136, 136, 0.8) !important;\"><strong>Automation Execution Summary</strong></td>";
		htmlText += "</tr>";
		htmlText += "</tbody>";
		htmlText += "</table>";
		htmlText += "<table style=\"height: 62px; width: 100%; border-collapse: collapse;\">";
		htmlText += "<thead>";
		htmlText += "<tr style=\"height: 1thin;\">";
		htmlText += "<th style=\"width: 54.5056%; height: 1thin; text-align: left; border: thin solid #da1f27; vertical-align: middle;\"><span style=\"color: #0000ff;\"><span style=\"color: #0000ff; font-size: 10px;\"><span style=\"font-family: Constantia; color: #00417d;\"><strong>Report Info</strong></span></span></span></th>";
		htmlText += "<th style=\"width: 45.7197%; border: thin solid #da1f27; height: 1thin; text-align: left; vertical-align: middle;\"><span style=\"font-family: Constantia; color: #00417d; font-size: 10px;\"><strong>Client Info</strong></span></th>";
		htmlText += "</tr>";
		htmlText += "</thead>";
		htmlText += "<tbody>";
		htmlText += "<tr style=\"height: 35px;\">";
		htmlText += "<td style=\"width: 54.5056%; height: 35px; border: thin solid #da1f27; text-align: left; vertical-align: top;\">";
		htmlText += "<p style=\"margin: 0cm; font-size: 10px; font-family: Constantia; color: #5a5a5a; text-align: left;\"><span style=\"font-family: Constantia; color: #000000;\">Date & Time: "
				+ REPORTDATETIME + "<br/></span></p>";
		htmlText += "<p style=\"margin: 0cm; font-size: 10px; font-family: Constantia; color: #5a5a5a; text-align: left;\"><span style=\"font-family: Constantia; color: #000000;\">Product Scope: Oracle Banking Digital Experience (OBDX)</span></p>";
		htmlText += "</td>";
		htmlText += "<td style=\"height: 35px; text-align: left; vertical-align: top; border: thin solid #da1f27;\">";
		htmlText += "<p style=\"margin: 0cm; font-size: 10px; font-family: Constantia; color: #5a5a5a; text-align: left;\"><span style=\"color: #333333;\">Gulf Bank of Kuwait<br /></span></p>";
		htmlText += "<p style=\"margin: 0cm; font-size: 10px; font-family: Constantia; color: #5a5a5a; text-align: left;\"><span style=\"color: #333333;\"><a style=\"color: #333333; text-decoration: underline;\" title=\"Gulf Bank of Kuwait\" href=\"https://www.e-gulfbank.com/en/personal\" target=\"_blank\">www.e-gulfbank.com<br /></a></span></p>";
		htmlText += "</td>";
		htmlText += "</tr>";
		htmlText += "</tbody>";
		htmlText += "</table>";
		htmlText += "<table style=\"height: 217px; width: 100%; border-collapse: collapse;\">";
		htmlText += "<thead>";
		htmlText += "<tr style=\"height: 1thin;\">";
		htmlText += "<th style=\"width: 51.6978%; height: thin; text-align: left; vertical-align: middle; border: thin solid #00417d;\"><span style=\"color: #0000ff;\"><span style=\"font-size: 10px;\"><span style=\"font-family: Constantia; color: #00417d;\"><strong>PARAM</strong></span></span></span></th>";
		htmlText += "<th style=\"width: 48.5275%; height: 1thin; text-align: left; vertical-align: middle; border: thin  solid #00417d;\"><span style=\"font-family: Constantia; color: #00417d; font-size: 10px;\"><strong>VALUE</strong></span></th>";
		htmlText += "</tr>";
		htmlText += "</thead>";
		htmlText += "<caption> </caption>";
		htmlText += "<tbody>";
		htmlText += "<tr style=\"height: 18px;\">";
		htmlText += "<td style=\"width: 51.6978%; height: 18px; font-family: Constantia; color: #000000; font-size: 10px; border: thin solid #00417d;\">Test Initiated System User Name</td>";
		htmlText += "<td style=\"width: 48.5275%; height: 18px; font-family: Constantia; color: #000000; font-size: 10px; border: thin solid #00417d; vertical-align: middle; border-width: thin;\">"
				+ SYSTEMUSERNAME + "</td>";
		htmlText += "</tr>";
		htmlText += "<tr style=\"height: 18px;\">";
		htmlText += "<td style=\"width: 51.6978%; height: 18px; font-family: Constantia; color: #000000; font-size: 10px; border: thin solid #00417d;\">Automation Type</td>";
		htmlText += "<td style=\"width: 48.5275%; height: 18px; font-family: Constantia; color: #000000; font-size: 10px; border: thin solid #00417d;\">"
				+ AUTOMATION_TYPE + "</td>";
		htmlText += "</tr>";
		htmlText += "<tr style=\"height: 18px;\">";
		htmlText += "<td style=\"width: 51.6978%; height: 18px; font-family: Constantia; color: #000000; font-size: 10px; border: thin solid #00417d;\">Browser Name and Version</td>";
		htmlText += "<td style=\"width: 48.5275%; height: 18px; font-family: Constantia; color: #000000; font-size: 10px; border: thin solid #00417d;\">"
				+ BROWSER_NAME + " - " + BROWSER_VERSION + "</td>";
		htmlText += "</tr>";
		htmlText += "<tr style=\"height: 18px;\">";
		htmlText += "<td style=\"width: 51.6978%; height: 18px; font-family: Constantia; color: #000000; font-size: 10px; border: thin solid #00417d;\">URL</td>";
		htmlText += "<td style=\"width: 48.5275%; height: 18px; font-family: Cambria; color: #000000; font-size: 10px; border: thin solid #00417d;\"><span style=\"color: #000000;\"><a style=\"color: #000000;text-decoration: none;\" title=\""
				+ TEST_INITIATED_URL + "\" href=\"" + "www." + TEST_INITIATED_URL + ".com" + "\" target=\"_blank\">"
				+ "www." + TEST_INITIATED_URL + ".com" + "</a></span></td>";
		htmlText += "</tr>";
		htmlText += "<tr>";
		htmlText += "<td style=\"width: 51.6978%; font-family: Constantia; color: #000000; font-size: 10px; height: 13px; border: thin solid #00417d;\">Release</td>";
		htmlText += "<td style=\"width: 48.5275%; font-family: Constantia; color: #000000; font-size: 10px; height: 13px; border: thin solid #00417d;\">"
				+ RELEASE + "</td>";
		htmlText += "</tr>";
		htmlText += "<tr>";
		htmlText += "<td style=\"font-family: Constantia; color: #ec2028; font-size: 10px; text-align: center; width: 100%; height: 13px; border: thin solid #00417d; text-shadow: 3px 3px 2px rgba(136, 136, 136, 0.8);\" colspan=\"2\"><strong>Run: Documentation</strong></td>";
		htmlText += "</tr>";
		htmlText += "<tr style=\"height: 18px;\">";
		htmlText += "<td style=\"width: 51.6978%; height: 18px; font-family: Constantia; color: #000000; font-size: 10px; border: thin solid #00417d;\">Total Number of Test Cases Executed</td>";
		htmlText += "<td style=\"width: 48.5275%; height: 18px; font-family: Constantia; color: #00417d; font-size: 10px; text-shadow: rgba(136, 136, 136, 0.8) 3px 3px 2px; text-align: right; border: thin solid #00417d;\"><strong>"
				+ TOTALNOOFTESTEXECUTED + "</strong></td>";
		htmlText += "</tr>";
		htmlText += "<tr style=\"height: 18px;\">";
		htmlText += "<td style=\"width: 51.6978%; height: 18px; font-family: Constantia; color: #000000; font-size: 10px; border: thin solid #00417d;\">Passed</td>";
		htmlText += "<td style=\"width: 48.5275%; height: 18px; font-family: Constantia; color: #41a85f; font-size: 10px; text-shadow: rgba(136, 136, 136, 0.8) 3px 3px 2px; text-align: right; border: thin solid #00417d;\"><strong>"
				+ PASSEDTEST + "</strong></td>";
		htmlText += "</tr>";
		htmlText += "<tr style=\"height: 18px;\">";
		htmlText += "<td style=\"width: 51.6978%; height: 18px; font-family: Constantia; color: #000000; font-size: 10px; border: thin solid #00417d;\">Failed</td>";
		htmlText += "<td style=\"width: 48.5275%; height: 18px; font-family: Constantia; color: #41a85f; font-size: 10px; text-shadow: rgba(136, 136, 136, 0.8) 3px 3px 2px; text-align: right; border: thin solid #00417d;\"><span style=\"color: #ff0000;\"><strong>"
				+ FAILEDTEST + "</strong></span></td>";
		htmlText += "</tr>";
		htmlText += "<tr style=\"height: 18px;\">";
		htmlText += "<td style=\"width: 51.6978%; height: 18px; font-family: Constantia; color: #000000; font-size: 10px; border: thin solid #00417d;\">Skipped</td>";
		htmlText += "<td style=\"width: 48.5275%; height: 18px; font-family: Constantia; color: #41a85f; font-size: 10px; text-shadow: rgba(136, 136, 136, 0.8) 3px 3px 2px; text-align: right; border: thin solid #00417d;\"><span style=\"color: #ffcc00;\"><strong>"
				+ SKIPPEDTEST + "</strong></span></td>";
		htmlText += "</tr>";
		htmlText += "<tr style=\"height: 18px;\">";
		htmlText += "<td style=\"width: 51.6978%; height: 18px; font-family: Constantia; color: #000000; font-size: 10px; border: thin solid #00417d;\">Ignored</td>";
		htmlText += "<td style=\"width: 48.5275%; height: 18px; font-family: Constantia; color: #41a85f; font-size: 10px; text-shadow: rgba(136, 136, 136, 0.8) 3px 3px 2px; text-align: right; border: thin solid #00417d;\"><span style=\"color: #333333;\"><strong>0</strong></span></td>";
		htmlText += "</tr>";
		htmlText += "<tr style=\"height: 18px;\">";
		htmlText += "<td style=\"width: 51.6978%; height: 18px; font-family: Constantia; color: #000000; font-size: 10px; border: thin solid #00417d;\">Blocked</td>";
		htmlText += "<td style=\"width: 48.5275%; height: 18px; font-family: Constantia; color: #41a85f; font-size: 10px; text-shadow: rgba(136, 136, 136, 0.8) 3px 3px 2px; text-align: right; border: thin solid #00417d;\"><span style=\"color: #333333;\"><strong>0</strong></span></td>";
		htmlText += "</tr>";
		htmlText += "</tbody>";
		htmlText += "</table>";
		htmlText += "<div>";
		htmlText += "<div style=\"float: right;\">";
		htmlText += "<table style=\"border-collapse: collapse;\">";
		htmlText += "<tbody>";
		htmlText += "<tr>";
		htmlText += "<td style=\"height: thin; width: 100%; font-family: Constantia; color: #000000; font-size: 10px; float: right;\">Passed Percentage</td>";
		htmlText += "<td style=\"font-family: Constantia; color: #41a85f; font-size: 10px; text-align: right;\"><strong>"
				+ PASSTEST_PERCENTAGE + "</strong></td>";
		htmlText += "</tr>";
		htmlText += "<tr>";
		htmlText += "<td style=\"height: thin; width: 100%; font-family: Constantia; color: #000000; font-size: 10px; float: right;\">Failed Percentage</td>";
		htmlText += "<td style=\"font-family: Constantia; color: #ff0000; font-size: 10px; text-align: right;\"><strong>"
				+ FAILTEST_PERCENTAGE + "</strong></td>";
		htmlText += "</tr>";
		htmlText += "<tr>";
		htmlText += "<td style=\"height: thin; width: 100%; font-family: Constantia; color: #000000; font-size: 10px; float: right;\">Skipped Percentage</td>";
		htmlText += "<td style=\"font-family: Constantia; color: #ffcc00; font-size: 10px; text-align: right; border-width: 0px;\"><strong>"
				+ SKIPTEST_PERCENTAGE + "</strong></td>";
		htmlText += "</tr>";
		htmlText += "</tbody>";
		htmlText += "</table>";
		htmlText += "</div>";
		htmlText += "</div>";
		htmlText += "<p><br /> </p>";
		htmlText += "<table style=\"width: 100%; border-collapse: collapse;\">";
		htmlText += "<thead>";
		htmlText += "<tr>";
		htmlText += "<th style=\"width: 100%; text-align: center; vertical-align: middle; font-family: Constantia; color: #922a8e; font-size: 10px; text-shadow: 3px 3px 2px rgba(136, 136, 136, 0.8) !important;\"><strong>Graphical Representation</strong></th>";
		htmlText += "</tr>";
		htmlText += "</thead>";
		htmlText += "<tbody>";
		htmlText += "<tr>";
		htmlText += "<td style=\"width: 100%; text-align: center; vertical-align: middle;\"><img style=\"width: 600px;\" src=\"cid:ID737964\" /></td>";
		htmlText += "</tr>";
		htmlText += "</tbody>";
		htmlText += "</table>";
		htmlText += "<p> </p>";
		htmlText += "<table style=\"border-collapse: collapse; width: 100%; height: thin;\">";
		htmlText += "<tbody>";
		htmlText += "<tr>";
		htmlText += "<td style=\"background-color: #000; text-align: center; vertical-align: middle;\"><a style=\"text-decoration: none;\" title=\"Visionet Systems\" href=\"https://www.visionet.com/\" target=\"_blank\"><span style=\"font-family: Constantia; color: #fff; font-size: 10px;\">© "
				+ yearInString + "Visionet Systems. All rights reserved" + "<br /></span></a></td>";
		htmlText += "</tr>";
		htmlText += "</tbody>";
		htmlText += "</table>";
		htmlText += "<p style=\"margin: 0cm; font-family: Constantia; color: #000000; font-size: 11.5px;\"><br /> Note: This is an auto-generated mail. Please do not reply.</p>";
		htmlText += "<p style=\"margin: 0cm; font-family: Constantia; color: #000000; font-size: 11px;\"> </p>";
		htmlText += "<p style=\"margin: 0cm; font-size: 11px; font-family: Constantia; color: #5a5a5a; text-align: left;\">Regards,</p>";
		htmlText += "<p style=\"margin: 0cm; font-size: 11px; font-family: Constantia; color: #5a5a5a;\">Visionet Automation Team</p>";
		htmlText += "<p style=\"margin: 0cm; font-size: 11px; font-family: Constantia; color: #5a5a5a;\"> </p>";
		htmlText += "<table style=\"width: 32.3981%; border-collapse: collapse;\">";
		htmlText += "<tbody>";
		htmlText += "<tr>";
		htmlText += "<td style=\"width: 100%; text-align: left; vertical-align: middle;\" scope=\"row\"><a title=\"Visionet | Innovation. Simplified.\" href=\"https://www.visionet.com/\" target=\"_blank\"><img style=\"width: 180px;\" src=\"cid:ID924678\" /></a></td>";
		htmlText += "</tr>";
		htmlText += "</tbody>";
		htmlText += "</table>";
		htmlText += "<div class=\"mapouter\">";
		htmlText += "<a href=\"https://www.google.com/maps/place/Visionet+Systems/@12.898589587410067,77.62834607507523,17z\" target=\"_blank\">\r\n"
				+ "    <img src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAPEAAACgCAMAAADqx3ppAAAACXBIWXMAAAAcAAAAHAAPAbmPAAAA2FBMVEXx8/Ta3ODm6Or29/jf4eT////v8fL5+fr9/f77+/zg4uX09fbi5Ofy8/Tt7vDw8vPd3+Lj5ejp6+3k5ejs7u/l5+nd3+P7/Pzu8PHr7e7c3uL+/v7o6uzr7O7tVYjp6uz3xdbyja/j4+Ds6uDtSID+8fbwYpL81+Onqavx7uB7foHsOnbBw8X18eDxgKb1qcP94+z+9+Dyc57o5+DQ0dKUmJz0nLnrLGz4uM5wdXrpHmJOUlZaXWFlaGz//PLB5MvoQTP+5qH/8cr+6q/94pNaFBH+5Jq5MigjhP71AAA74klEQVR42u39h3rrOq41AKirS+7urunp6cku50y79/3f6F8ASImSnT3/fQB+ZybZjmxrieBCJWg5juUMLYyhYy26VmM8XnatQScrcp9G/xFjVlrlbMJ/tUvLGSX4ObH+MGzLSgb82zSZOQF+lr2u4ziDwYDeJx/9r3/r8dv3u0H15qDj+/gAK3Hx9fwpTuHHhTmWfXNkXWva993k6K2oTyHEDn+WYw1aVz7u8Zpd+H6FeLewrMwRAJ5lDZyBFSQj28ucQXD0W6xRYXUX6vegO+sOmn+eWPTRvyvE//J9N6v/bNeIl/wp3cAPEsccg8bgZyKIJ/yCcVujmBHjdgRxL7MGXvN2CPFil+WxRnyJjxqM5I9Zr75w0BsmMzvpHsGNCRYZ4lEuRl5gBQPc6TBJ8J6RF9Fn/7OC/Oj76/qpKMRA4Rd8l8Auv3w7cG1Oz6zn8ax3bBmzJMGH+XZieQOFmCbYXph3nBNiZ3+7KzTizsQqR3I/AeSbbrxX3V45WDDu4cKcx6EWndKxbVo1WAyZN5TJwbcN1vTZ8//RiP/h+1Etag3Eg8HCGbn+OsmSrKwu6eHJ8axP5XHjWn4mJBfGiF2XXnBTWmcyWwXmbtqdeXjvQiQkeHyGAOwf9/Le3eMzPslRa72L3z0syMyzR5kzndS4nW5iG6OLh0MPdNQtra78wzMfScIS9FhN8j99P3AMxMlgME0Isd3vL4lSAjzlRSWNhQcoKa3oft+ju5hmRTbFz6zAH2L/YFgjXmqWPHx6AxB3Ex4zmhMgfub3xc+3uHYyk6db4mot35OeI2t5+g2H2VaZiWh0uyxK5h9lkmvy+h+TvOzI79t9p2jMV4Q/JHpNDfr1H0Tep86U7zBjCDI6JOD0RUVhZdUN9EbDwzsuvcvHHX+cu+/gpr2pvE5TnfQad864j65lbzijewnwsLqjLktTPaY2P9Bj5FU6ndA/MsoSsqy/tu+HS8xwjhl1FtY3I4DILvBs3BHWac1Xk+FoNKzYj9ET4Ed5isUOBLTQXzQLRFhbQ63lxsCjSejDQFolrwZTJZRZpkAdktditm5IZeS6ReEQbfdqOQFiwMDqwowOrT8NkgZi8UFDC08UiWKM+HYB+NHh71tCnoOOmj96xN0/c6YeWLR0+xMvCXoEuTS5ezLTuu+AvAajvnoWsRsUy96C6AVvX/p+OirLBuIeZqjmMsiGUx7eiEYckH7qAdsogUTSp1ai/SjjVlE1APZmSpKxKrGSJ07igff/aIEITZXOjJYDQ671s7VgqS3aGorIa+SljNbBN4xMpsN9x7hdYx3HhYNVSi9NRK695VL0EY+h6O3FkJ4V3w00JgjCD108ymINxpOLHeil21sgvuSv9js8Mz0YToNB16OV3BtBI2CeRsEfAHdJgpioGSJBVjaYVXb7BHWdLJb8DQ3yCliO1zMsEgZXDqALsyHTt+91e2WNWAu9HXgFy4+tREOGMs7WffB83u9bzNFA3BgxEz4MuIx4joky2u0yvtMATD7EFM8STyFdJN/iLb2uVRE1PZye0Dz/wxOg4WzS4V8eTfKiUXS64Dy6egE9j8kaQtht3E6crzvecKAQR6KGInsgBuYg8f8wRA/3iL4dPAhR080hr7j7x8vOaFGtjwGbE9MhTViy+BYwIfT0gu8Rcn6N3kpiG62xZJaemqd/meQFCUycEeup6SiRh4sZHNiizfww6M+yRbBY++5M1FBPIYZlHdm2srTXaorlqcSxTwY+fRbou8ekBZmmi0H4qesaU58Th90+290JSRhugNbl0HN6IxDaN3KtAOslNyShmIiQWg5xdJwNZ7EfdRTi3/9jTHJsOwnL/ySpLBZYh1NvVujbitKlB7Pf9dQ8OEtGzJZ1qQzthaxiIJvhCS9JdcD/gbxMDXKgi3HRbrcDeq/PK8pf0pJ+zEuHODwZCSBancPhd3LNWFmQ+V8ZXd0bidbLnIhmsWeRlgz0WmyQ15LfWXZntdpnJyAYzvpFpacxa6kYz1AvBc83fqQjp8HeNCoHLClocdOKJbaqmVpR9e3e8BVvHWWAEGUPFKWMvpNrenFSLWGW7aGs/cmI+Z/9zBHdiJ61f1TkNWfTChxvKppEOQEDx+s4ecuEZLlluor8kIzODjFwZW/XiEd65UZgqyWYepZkUFITrZz28jg7YO5Lz1E2FxmY2uwZkFzPDhUgTfxkpJ7gdIQ3lolQds/Dd0biJMAAws2qtdkkrxgSMWusFyDWLls57Y6yZer/cYjNQtoHcjxSiEvbW8NEM1Zs7Oagcs/u7J73l7fKxowXpHsD5WfJJOvpI7l2sjZgssC1owWtNKFpFkkYZpidMMNTZDgZ5tvRj71BXrBWug07CiyfGMtvssjsfuC6of9fB6le/PBAvYNksoCJZtsZ2Co1mTrERU5fsUqovkRbD+Ykc1BEG9zVoBf0/XaTUk0zTfQajzf3bB3WCDqRH6uVfL5dnZycrJ5kkudWFXBRiFt+F92IYlsQTuhXARE4WUefQ963LYfsDWcaWMRW9N5+n6i8femkx0RQVlJmTjKFQyYtuaZJnyqLna0Qh305XEdMuPSCUagQWw5kuqBXT+9OVmc0VidnTF5u/Rn8sOyW32WOAEulv6gYuktYMiinoAXG6qZ+nBb9DsFmTIMpqBwyDf0c1M+p/7xjNqx4kydZizIFgppyPTECYvSXsitOfI/syqiP9QwiWYvAlLTCssg/XwlewczkFTRI8cDTbAx8YN8UeVicE6hcyAAJAKwN8ZYtTy1+gk2+veb0YHC5f951bE9A75iqb2sRIxO5coIO5JoXsAqIMXhP5LuXkT3L69mI30yxpp31w0kNmCHTJDeUPb7vmMOmRhOxtjjVvzCPDimIILPsWX+Z66kM8yXB5rBNLlStnWPF3HkuH8E+Qf2RbbkmMtPySPKvwLM5m8/4voZFHbGCQBb2mQkYkFcU2JyYwgMvb/C9RdtE3HNgmTR9HI4dda0RwjTZyAbsuIK9zmyvu2aTQznHbHFVI88ZcM/49qZcs8JSiomUVLUYQVdrZSHh+1P9AVnuF28nZ81xsgV5hUYU4li41RhNxPj40FC16gKKiKpoA2DPOv0i1bDdALB30E7iHBe3JuLHSwbsmS6pKdemUUJCWSkpQuwV6paWdVyZ7MLVWXusSEMF8vx4DB3Tt26PA8S8WJWqHcEVVgrZcOuDqZPMOggqKeUcuYXjVXFMY9weAG7KddcwPAn4sLrNSiVJuEaHf4D+HFN8asB9wiQzefXq92uT6/hoIkaA70DVUrw6sVrqrcehD6+GLaND010hJsuhBdiUawKv+WaRGHPEC9ZA7BuBjA/MsX92v3naPGy2Z2d3EeZ4S+SVBhXpJwMjmnAwWlw9HWpVaypmN7eaXk/Pgy7LyPWa2dk6qJ5SmMIYgRW2399KnP4AcC3X8ktPvWjKdOOuCq+B+I0Q38EGOcfXbc7ufSC+46zMsuIBBF3J5JqokFQrZ9RCzF/PqnbUULXNKzSOoNcddTB2dmbOdOwWS4T6ELc/ZkdzWgbT2TVILDMi3PquNA8Za7q0BfHTqX9PiP3NuUJMgc2pIoWJymHNUrn5pImvjRgPRieHAujl7giGTljkjfcIAytKB2rP3kk4IV8GOtjt7h53g9r2MAfBGw4JtF7QB8aYmUQx1jR+v2epxvI9V49XEBN59UQpL8hKJZvPVlf8F8R4MFC2MK2GkjcZwqwrksaNM+CuN4L5pSNR0wl/9pKNEZhsqVtkuW0dX05Cy3RfiZbptsFNashQwtUdjsINmMuUJyA+eQPif4C8WGiyhOMJEtThaJyWHRXPayCeDEq7Ekwno/wBOWxOw/oXwF3ibCx6hVpkX6j6do2oRFaSSlRpjNZgmrLpk+Tf3QPj0zHMDtzhsv49JsQf9SDEOjYQkFKmS6ek4ymyZRt+vLauQM56lVCazcuyIq1Ii6YbPzzHMEoEsNaQf/0FasADGfP1O2V58J8I8YGnpG6cVBHJYJf+bsq3Gg3Eaf075n67Iqmuhn/GHtS/mbz6FOameALd76CVINb/Rtqlsvp5vqeUJOmDf2u1A3Vr3C05y5SVoLmd/vXXX2QMlnxdNIIxApKWC4m1esdNH5brKTmu3qzr0Ac0MzWEOOjWvxfD+veHA5urjg249GQmnoonOMcRm6OScCRJkPjzDNuqvkjb/UPc7HT4l81huIUsBcAg40T5GRSGOC7WRsArWFByvB0Aq9LZ7d9hgG2bkE/ujNjAYgJLYXr4Php/RCxjQrefkx1iIi6TWVc+M1goWujNVERZvY/y4hNxFJ2WMtSDxD1Y6Pj5QfDenB+Ku/Lv8My7tDafGr7TU52Vmftxx9bR8Yac8NuPIEaEtD8w0it0Z1jTfaRAzcsCkkV2ki3iCmu401E9b6h11nSH1QxY5ex4wBYv92bdzM5IpQxH7QSNeXdsZiYq+oKHb69N/3h12ghsFrVyMLmARvXkjEGaz8tA6HX+nqi7A2JpXRksEr5Zi2Kvto7qdUe2jpQA8eMf8mykgEnoyx5io7PhwVPRiNmEB3lC3VW0YlvntVyfnPvzKrBJ5AVaUoJDjNc15q8t5TSotoAgwjWAR0Ruf4/yVaNj98w3m/UG09lffynnOLJ4HUvqmQnsm1wqezc6yXJ0sGaBd2q6aSqKijId60NDPvnw21mZdD3rMkqsUaSP+lXgvy3lNAjxrAp0ktvfoTzbt65mSejA1iqP6urbdURLHcuXqwFCO2qO6cHWgxGBoHixjpTjr9mdQF7d8cw3szKIAWWUBlIFHiYDHhTDEOKJB9eg4REhnJh8xz9/0XhWeVTHxjpmAbWVVjZrIsxBOSxljnWPZ1lt87nDa6X8vPrTAFG0ggMhq1WfAxF13cA/9LvWnQxubkgpQ/1kv0NMH4n8vU3yFFbS9F0e9PFyv/vrsip5ud0/d7AYhjtth9AqHh1WfpDtI5SGyTwmQ1SSJpFEI6TGoydRQzI2z042lsOc2SAvPVF9aB4vqct9ht8jlu9kS8TR77eOjkaMKxZZvr3c75VQW6p8p6p40Q8OJK4cu2MEaolT3BEObIzeSBZJz4OKWj3RwiJprOsGKKW8zLWE9o3qJg6StRDT4rFRwdb84tDO4PdXLyHIWRdzKcQS40oTmwMC6kWxvOpAOeFW1Wv0HIby5ccI1GoGPqqBsjZxg4B7ikk++aArOTHTIC/X7qxFPKNlnY4isVkvDr4mgqGV0bIRvUpBoLxLlXb6moXjpuuOrvZRwR1Zxi4LRubxusxVMBO3uDisXutWxlibQJGtpbqp0fLwQfSkdFHjXp2dSHKGl1SDvFAM4nkBT3S81ilHGDWVn60HKUHKrHMkw2PHKdV1jnpIICxcig0iiNUy1pdME8ObxmpNulS9ph8ijTKoKpcMN6FH6WwqqELeGiqpvbynbEwhiahKNu+u7+TD2DVvkFeEZM6kO5MkldsXsxOVmgdiw4irNDMcp4Kyl0nXLLxCiYIITBz07ezSXMawPWC4lGx8PGrQuqQWFmtmVq9VQbhqcfWyZuVce81RrA4p4Uw/uA14i8eiaE4yyCvn9/ZU1ZPrcUQkPlTHhNjL8JiXgRHeI62MusyJFFuRSeitRWdHudMhX6kKVYOqd2J8aMQ0lYaloTlMPQaIsKfDO+VM4YVGyYsiPoa4mA1R/KQzPNU8cP2iQV5zUJbk253Zkicw97IpwmVO7whiqmOCwe6R45RXuCMXsZFUW0/w8WxH8aG7zFSZ8frRHArx0QxQbya6aYKq105VEA1bB/kdrp7KKGS8bCMe0qcdS7BM+JH/0ySvWKczux0W2qjogOEOrLyGc8FiWFARKkuxZcRQLWViqgcSC/asgViDQ2nyaND+HvUYVHBc+JdyA7NK8x6JNxJcT8IM7Q8Updwgr0JPZ5B5/NcohgBb7Te2CZLstLU3IvVkoSa99WxHKXL2qV728fMxxBDFIGHeWtR2BGpoIO1hmJZzeqdtufi9toiocnIZ+84hYipVxTwJB7jj8TiVP4lSbmgoIq/qjZ7MTdrptsw7M8PDanlGxFXw8kMgK28awhQRW6LopZ/rZXwEcSKeNASGSotJGU9UWsgdn4+jzT0EyE7HD6fjGnFIULjCuTGoLNTmT5QC0fBhO9ZmwnTZnGQK2BtpmOmoL7VmgZc0wlCG4dkTtYyL9MSy+kYhY/WUWL9RHH75/TKm5TqpHjYrYxAX6lpBzOOvN8SsSNbS8epsvK4Uc7j5wjjbAE9j2EyCVB3KBiouq6/gFfa7QV6qtEuKL0HbomLy/qhXHkPMfkcYksWldA5XY3jLoso8Qr/FHYC7NGp82oiJqhP6BLO0eNCjMhHM8SkjjlJ3/PAwhq8fuWGqoKQPT+/j0KXpDt04jsMwDGw3duNx7ILM8XqIy6KI/jwe4yfP4e9//PPx8Z9Krl2lirzh0CO7ZbZWvtRaNp60EGe1WkYcd42qCFbfEORKtkm/0dqVOnIdxjwgLswLyJ+3CWR1NVh34Y63D0D8sH0rwvh+E4XB/faDVyagxKm/+TqPxsHHdpO659vibXsOaKdv2wdcOj7fbmlJAPH9ZgzpjqLt+dvmYev/8/Ht41GTV19w6QqJsjdSCzBedlTcykBcslo2LAKLZQ42mOYvSgc5tHZbqfKGUMN4LmzlDZSkjIXESB1Dqjf+9v16e31yGq22cXGy2l6vxqEgdv23d5Q/vD/dvX8A/PXd6uvBP32/vju53s7v3+/k5QhvHPNcf72f3G++/N+Pq7tHTV5Sbq6WP49pomzPCMJNgZemOxUMuHoVCQayhSxW36hk1LYp6U92g48ljjViKJ4A/sPMMDEnU4p1zwJBfH3qn16/+ast7t+PzlcPgni1OvnaAM/TeHx/TdMdxyfb6O7ajU/f8fPeje9WTcRv7hif4FeI2bxeLGBYdE2LYNLtqHmMC8/rtdypgolLTP++xUVVxM/K/8B0pyTJOhpwTKi15gzYxExU2A+mBIwIQbwCUzydEeKH97tzLElBvNl8nJ2c+l9vm8391+nmy03d1RbwwnIMjOPzt+1qBamO8S93gp/x18aNCfG8Qkwp5X6Xw3WNzUqBM9NlcO664zbcKapmTnVpgCVWOB6KLGTyvZYkycqNOGp/0MOtImtSVeF1F/BwoZErxOl2RVIdnj9dv98rxCEI7eve/zpZYZyDycKxIF7Tz/v3LRCPBbGa4w0xGe6jQkwaKpJvdg7Vqgi3H2FDyajeddbNTXNeVAcEP2IZ0brp8VmcsmctznneROw1dzYh9I9lAY2sEUfu2RPNMfLBmLXzCjEpr68NuHrsV4jvwpR+voPhtpVUx4I4TVuIKbDp0tfZSdO45IT0zNHxPIq0eBJ84FB1dati79NCZqsQCaCQdJOOBij7IzeTOsKTj7nY6V1jQxfsAMVc5+PN+z0hfrs+HZ/XiE/vQFVnq9Pxw7ZC/PYehOdfW//9bXx6QojH47Mz133C5YQY7wbXbR9Ny6uTqW8d1ttBpOigl3WWdVEAF3ZwHLdTmShihZcoO2ITX+umR1kUS01ciZG4E55Uf7hcd72qDgwmhGKu69XXU0SIg9X76v1OSTXGCZjrFK9BuDXicPV1Aq72779Ovs6+MLfQ6e/X7ydKqsdPXyfg/trYfKwtLwQyqi0/tEWTVnawGGVBo0SRi+b67MxACnXRVcqWKEdXaV4vpapa2x+5md4WU9hc37eXiBk67P2QWeynlOWHBYH/3PgBv8Y5W5kIpcLoSMFRm1P665hMkSjEP8dfH/74Aa+Ofbwej083D2yBjMM8xp9xK/N/m5aXss7NOKYRaJpgYo0gsR4xleJa6mFBKdFCHtIPwqCyEevOM88yPQljjuHuNC0TjN1UyvHhL1g97MJyQ3IdUtSHgpFJreGf0xAFmJaVwtQKXfozXR1uzh6K7fspTLEwlM0c5IOo38EgYVgSqTQCm8ptbCE2XQRe6qhlaW0B8a2pBsERQnA26yZlf0h0AKD3S8VsPKh27gAxOT97runLbw0XdBHSPaUE46iziLkv7t6/Ts4j2buSWkcGz1YdGyDyCjjF08pBH0RxJz2qElxTbW2VTdV8R38pZhQXZLJy6ocC0J6dY41UfiE5SZ0dCn8ubysDhTY1Jupfl2uiPzZXKK/kDqyxuIC0DXFAL4Y8h4KuTGFU8xLg4R5BHJ7DdG3GBnyng3U5a9QZMGLky4w6pAAE7tuoKqbyIN4nUv1pSFQVF0o3qWVcrQREB3Ley0uBDCdxOLLDWzoRDWLs+269tvcFFcg8CmIYE0C8XTFi0ggpLObV33//vdo+sIcBD9NduOl4C157P3sYH5llMl78g6wMAkmIIi15c8DQ6alAE9anEWaZOka+hne/1J/alQ2EopuUG+F1sqC5G4E3QsFANcKaE4rLkvKqJH2Hp832eJ6naY3YJfcY2O7+1uMujKKIJhg1W3TJ+PzkGt4TZjp2sdrpZ8pcKIhruv7H3LypWDYHcAbVSQJTuCkeE3xX2TjlcJnoJinjCvcI6kESj1flE9WG5sJztNfRidXaeHxkxPAht6swjB+22wf3YfV3PVabKN5u3japIA4B7dwfb7Zvp2MAvd/en+I1+GEfX5u534oNoEC5XSkfIXjogDhG1R4XDvf0vkGMKhAvdVmolTYmN+IWCyGxKaf97XYEAk5rPNHhElJleoEDMYw+wInD+6+np/eNCRgjZP9IIY7HHzBQnt7vVu/nPqwR7CGIoLZXT9cwsP0D8vIgWrw8l61K+WqPS1eHe4wxMzNeg92O79qIBlw6xLsDKmzyqBad9n4dAx8GEvIlEusr90sQX5MRfb2CU/gGNd0C/Pcd/IrxuCTEJ/Avrs/IpYLRtUJJ/Xh8Cmk+gaeFObbIfGySl2zMLoVRaGNcvwjM4Hgs20Fsj0IXlY6IpYhcwVa3qZaxrVio9lEC3iSWJDsN/hhqPCSOovAIV08bjCeyHnHJw9/t8XC9icuEEL+v2FBT0u1Hpx/bLRDDCocdtgk5zPcPk7zMrhK8SXUG5DPcWWuPC5Y5palnkp+TW9Ww1W3KMmZbBCx0EPKXTBz2Pz1TWBoR0bSJWqhaxm/yn9wQMD6+3Em6FZj/+5///Od/5dft2SakIBcz19Mq8u9AcuQwnZ6cKcTkP21CDvM1yWvdzB4qUp7oPS4oPzV5N5aUc51EZ9idZ5JMtYxJwkFc64OSRA3nkuT3FuxGm1kaRb2ZMtYIsZvY5Dpuvk7jaFUB1pAhAyFl2RnxA8zt7Qnm+OPLfzsBNxLiD5ljK6FvaJCX0VXiMEZNwRkqIi+4upJQyt9hEJvV1ALbMKrtg4pPAzFEnrWZTXvedh3HQB2TsbazE+jjcFCSFxhd343nMq//kaEmeRNShEG4+u7afwDE05On6O166d4jZHKGJ/H2tUFJA03E70ZKOWjoocMcPRMXtqeT3WCUjlCdVhO2/MKS2amzNA3ElzuosV3q5xwFzDL6ccuoa0M2H5Tk+I4VEZ9fX78dR0wmKV1C0Z97//795H2FsBE8Jgp/Bdfv10/garUym+RFlRWyz/RYcYRYXPJ72SzvOgKbtDGh2CWTI4hvPbd4JsW7lKtqa5MC/DqlEVspGRNOksKwtkJ383AEcTTmuWFDlD0vOE4bXn8bVXmsf1Jso0VeQsiodp+NYFZ3W6UHDYvr6FCwhYgCpV5bl1BeXBuSHUPxasTkZaFspM8Sng+76wFs0JI8iTRNj63jsculs+JskLfllDkEpVyUGLWNEdMWHTL3DfLy6xG5ZAjLFpCeDvf80eKqRn67l2VMfQQOESPylwyUPF+aileTmSMcf7un3Wuuszee2qLrHuFqt9EiiArBsQ8H1sZ8brH5SjYGxQAo7NEl0W5aXq1hbn7hPbDfWVxdu47fDFVig8wW7E/tNYiLQwNM4cu9Ji5z7CvVRGmc8VR+ZzN8AJvu/O/2OE+bZa2q/un87Ol8rvyoCW58zP8Iun6bvFKnaXY0kBNxLVq51ouLv/7qsXvMW5zQhWFiNh4gQzUzglkSGsjpR3KricsceC1QrxFtTDpq5iXeaYVPLcBPpaV7e6QR5pX+R1+82ry9P/jKg5xjC+ec62E5wdokL6RjIAfZ0Q2mPpXbYzJNUbd6GrEgDBxPtLFbF6DLw2DDrOdwaADFS5DdiriMYbyGX5ElIrVVxbStNGgh3g8opILAYbaYUzbm9O1+7sN3uL9evT35GxmwUiKZ8PKI5cUGEpsdyTdba13ZWMyirqe6e7DFiesc+qr/Gg1W1bYEkxBvUMR12UJMUq8aiRAfxAidV8VbFNp2Ng3AG4f7d2Xkf0ZPZ0jYbM/u/Ov7py0m+93fylg9Pd0pEe+RYdskL47Z6UkcNJHHDYGnfV6VdAtNG1uciNQn04zyM01VnWK2EfIs9kxcpSWhbDXIi6AWLLQJmjfSqI/nYlua6oH7YQD+cPEBEHaptbx7wKSOo/fTa//tbj6erzbQR+/nSNWdfZzpRc3VIS3yCicD2jY+O0Q+Yjvb3FhsUUyjmoQJRcNU+alMP0L34ag3tEM/bRhmeLLT56KqK6oQ75fVA41YuCbcNMTuVHV6eVhD/ggnI87p9DwqfDo/geFBS/j0bnWKFY1ftm9wG6/9++3pXDniXB3STClXO+AtE/lU+4S0C7lb7fOyKKbheHq/Nc2GMqqFqhJOz3DCMSNVHVSA7BJhNR08rJdxqLudqREEVra7rZN0JuSPfNFV3bIGTtKPzj7uaEvbanOHfNVmPl+dn0G86QVQdRV5YA3VchvrDag1ct5pzQZZtaWakFt6PgqHi0NBSmJ/9OUqcSRAVJyRmwwrIcfsQrRVRKkSa4ofrKWRSIY4GkggHkhsxAgT5eE9A75foIlbUCXM3DkW7/3X+fn16Qqbf65Pz6/9k9PTk3PMsVncIqVtDfIqEp5Ec+utDL2qGbkCXs+GbDP3RiK7YpuxFhowUfF3OkrIyYUMqgqmplArDQ32uiTdCpFlbaVvghKSAwp13algGXfLmgwm+fz07ORss7q+p/31b/fIxflvH3P8/tGsVWFBa5abJ2qvQ08hN5VRhVyFOPtLEJoBO5QpTsVDpMm1OTcjG3UAnb6ck6/TflVJZQp1akSyn6nSRUX8ctWCb5YsEUMB4jRPuQxikLu8I2voztWgbiG0POein7HnqlkExhrqSDFf9VAPeEzeJtMzQDuOgmL3zUDOUlgcRSGYXFrGkpEbcbxdzBCnIq6GUBc6ngsNRWYXtg7ZVKqs2iyWkN7TTbT9ezsWUwOcPHdVXXyOZHjglniUXPISI22Rp9bCb8X1OTbQJK/+sUrxJoMPtGnHFQTQTZ65s95XYQ9COJSZ5tm0GbqkbChVJdXVFeJL4XkVTcV2ADK7IIYwt3QyN3XnD9d3Zx+gLReFLQjUR6vTiLqg+ETUxCc2PUqs6vNoiEcUUILXrMqjKju6zwZ5uX/aqyDIq+RoXTOKTM1INTyJVEbd5qLHahnT5ILB8G84eCOSCipr7dja8No3416c1au2WZAMM0dsToLV5u9dx+tMxiS2d2/+PBrj5716U0xSPf84o3R/Xx5AdfNcSSkT0yAvBID+f0fSqCAQItTLmEvep0TYsozZuJRl7dRbHrB5VFleZFRLsiOoW13R1gR3rP91f38KQ/J9Vw7WaTzfkEV1ck/6doOf8/kpzA08E3oZGfcP/vRIExda+ehqWRqtYr7/C2KsEp0RL8SCWNZlMLRNCmTIy3jINiaT9JTmG+Y7J2qKfUVcMKpB0ijW3XFRdUA1JwlHqGiTz/zpaXv2gIhGmmPD8QabyVEUdOdvn57ONncol5mf3L0B6ebkZPt0fXq6ehJsTFylp9PCVBF9EPMKnP8bYi9wMzICh/II1TJWNaWhTizSChhasrB4vmE7ArjDNX77/d5uRQhub8mbcPYIiLpU29yLzs7PTyCwJ9HHyebj/eH0+jTC/2BMI2wL6f54gpivzh9WYOkthP1sOx44irgcmQyXq97bltdvraH+PDhqLRtXE1+VEEhdlOIBpOBVsafIjNgjbHmq+bbYmQJ3PVPTOtwTu9R1EIi8ifxZp97S+fz+5Bxozs7PPubzOxjMUTg/+9hC5SKude+/befgcYy5G21QPvNwPS+1EiS+RLjQk50Nh26j2/2vgLsZ7SNjxE6TctKR8u6ow4/X12XgJM5Qah6HsGW+q8x8oi1Lyv1wGnJHOuqZSqk6l+xcwggBHZ3OsVy3mxWsSIIW5fjh3yFxcf1+igWMPM12Cz8iQvcIqK4vn3RYBCJzS2pN1Kv8sEO30fmOvKolK80aBPESrd/qMgIpGFFO7cTW2ngqmXAp/gJ9EeNBR3EEf0hWerMRBemoJYUhnH3Cplhuzbd387d32I0Pb2enD9dIvY0tQoy+GPdnWOFv27MP2JUP1x/zhxMw2f3K/zjHf/O3h3kLxqHbGLe651WDdp9DH1MFRIWY0sxcl2X4ABr2sKqIEJtD7A9tY1bGNTWIkljEQWus/NlmTxqIT7GbabNaQQ9tT64/wNyRNb/Hv+5XJxvqnnB6fkYSfn52svo4fbqGoXn2hv/meARtHIduY6i657WtawpRcSdMWIRes1JhKq2V/eZsVxUR4izJvwdqedktgwjJZA7GebofI31KB99FfE4LWUbMER7SwxZrafwT8huNY3b+1nO2LfWQd7QRE6UZ5EXZxmpZ6dge+01Bp/4oEsu6Mxh1hhF5TKS3Ud13QIgLl2I+xf4A7pD2M9pRRMDRMLfeBUS1z3AkQN7kRrnUtFEiYhKjdSVOG7pO7i4m0tS07LkBrDI3WCKMGXRhraSIYiidPsFyCa0DxKNDt9EM41DAg4JciVe1TQWlBI1J0vxVoPXmRLd0ItiSayvlUrE/gHtsUf8j23a4XwNvpepJ+wk0nq38CbAWdhAJWefmDCU29XQtVYitP+tL5IRLcKj3YzBVUZkjSZEJ9kn3JY3WJC9bQnzGsoIVoHLDtmqXa+sOfzTHyq6mLqujbi+QmktdpcoWWCD2B823PPZJD1vnVffZpfQH5yMCKqXMKjkRn6pG6w3JXyXfuOtRFKGaCMxSTg1opd3HFN9vN9kIrVlm2bIinJq84FGg6MlobJNXQS7ySXR1Vs/cxpkZfiOlRXFbg7oSGTYHqrDF/mCLuyuCTLXHntheUmw0UJH7nb1Txnaqtkth6zZcFkFLQzfmdTw0wW1VXnELnxF1J677J+PtMDTzxpUN8qp235OvTH4TbdmPuXFotUM7MxHzR7h97Tq6xH3VbhlyHRO1a46eQwcCgHCRQ+0RJiILQtx4NOGIwwMcyCUHymMTbF91VuGblx34le+ZSvt06ohH65EQ69Ic7ruJ5tFI5FWTQtnLZrbx30csL8QCRmkjBU7v1Yh1iAtdfxqZUWXIe2xziP3hqOeAiAY2laB0QGrghBPYKsmrkh/4F/hlv2ugpZ5Bsq9TuL8rJTrcf5ebAbJULzLVzHXqZTOj41aUYvcSdlhmB+R1xPISwj2COE2Xaolb0r3JU/tM1DIW11E5kKqOU8aEdthQYF9pLKqeG+ZVyQ95zbGdKbTSIQl5+f1QshEJuH82SY4fFCHgqQdWtQMLkgf0YnxxVsYgr38esbzKlgKthNZFZZV8YrU3akQN/xChkwCRLOiBLOs6qmcJx2PrC25FPFm5+7rkh7iL/VfugvWseps996RrHnmdWGfu4fZDq7qRSRU4DhyISq0HF03LS8irOYRw639XkxXnVxesquNSB0p5OqsW70N+NpBB/D8TdnXiycKRGypUOJeKEacSPhB3YsldtsoaLT+HgolL7ANbFTAeH06PRZlc12Gro1DL8vpXo3WI3N26uW1PF1Zbsf3548fPq4sXlEgK5GbBrdpZIE+ICJs2c6rHPbQz6q/sqImXSoqeyqsSONwV7sNrpGzU2R0kOjjB5mj1hR7UWivNKvveHG3L6/GAvIat7e6F/qYQgZfXX1c/fvx4VYhVxEN1fSgrBwKouoEiEDirGe3Gl3SNLBjexS/8peyQZxAEbmNhJiJvNXHRUhg0eh0cDK68W3hHl3orYP+PA/JSTl41KuJCHWTnB1j15ueLQizaCM1SWOxlGReetrtcXfkg1kpG04CoCJWF9vlJJXVelTJU0RQCrNFeoijGUcQlS0Fc128GLfWkOFZOfOg2HpBXM6xlWVEd709fCPH8R6SMdtFGWihkGYv9wVkZ1Gvr/tFkrVTmjYtsnQtrEZXAhSpxYu6yWKwREdrpfLQQl1pWg+9Dc7RfCfOiT3xpjZbbiHBIk7zgx3pmbDespjx305+/Ll6ufkYq+ih6yFMlbCPexSoBbM7KdGF6I+RbN0hvNA3nPFuOkhSKiex3ZFzT+RG2pHpkEHHRw0v+SxyytxZnZuocbYT238hr4TRqd6o96eS0rbGMf94oxIq4lFmtmlAI/iznLgtoLUFmn7ZW0Id63eFyMb8BHZvpuVkzWlXqpBelJ2RPLwuN9edBxPWHApZWzIvJqzq/6PByravC6ObqShzJsfxFiEsTtjShUPaH6g0Qkrj0uMMR4mM5zVaPzmDCpUsYizG1xTeGWiwUFkdleiK7b0Vo/jz+VDKM7uyzrBmwR/1iSEeB9KvDf7xEHQJFD1zpqjR6+XH162fqdy466s6EuHQ8QMzpQUVfAS/hPp83gummiKaeLUgAWv/1kDgoyHkjI6ngmtUU+/8p8oeOarzZk6wV71jdZ2sIcR35Q4DdTbrzQZO8ZEb0dTzdsIdp/66ljGY3unr105+fP3+0iEv8f17GZE6z/cFZGa9DxyME6ryRCCBVlS7tncKHBlIzcUk/aCWnfAeJzqwmqumm7X9va6khxNVrL3Zs8THj9QZ5kUdBO2nKg9o7mhFlf4TRzxff//y8eIlM4hoOxP/n+6/MaZZEnX7rqU4iua1mi+Qfl/ICzVkxdZSCwkqWaieLd5ZRQ5yWlXN0EHFFATZLE8UrE5BOGqg4E0HdZsAe5BXMqvOLRtWBODLHQpRj+8dFJ/0Bg0wDpnuhzl8Kl8MLXuwPzr9V6TccC+UFFH5QVbqQihjtFjmqnZN3LMXWdLwMJnkkXUUKqYawnMa2ouOjCi1T7Y06bK4+TSIMslm3d0hedcWiHMrHR4fwOpbvi25+wdr6cfFSI66iQ4RLlrHYHzLfVfoNo0sbPZWaIaEOJA6OH+QqSkZZTXJPJnmhtg/rRfOnkRwr05KBBIVE7IfNCnt4FN9mG9Vdj6M4vXmFZf1DI0boKud9US4RjCxjmukyXPJ8S6JVgt/4PbUVcdE1pHbE1Mk7sc5KcXmXnmTU93A1hCSk/zxoa2nRqr1iWV53qn5T7VIJOmVFKe+gVlEoKrUtZX/kFj/I9OZCIwYzZMzuZFWrZdyL/YGNRgj+PByvZWHTDk7W1baac2JqekaU9i81f9HY4x1zvZKhmLTj1J/+F8RE1R7166ReGLGK2uHkn67RHaLtNkKuVesQOO+6K8SEnp3VV0Q5UCo2Gpv9jnHMHss+L+PuJExfQenjKe4A4DHReUZizibYSIgrgB2Ss8kzIYrhNyprmkpToetJrmk/mHac7P+SIauKpMuEdk6PKOCJb20X9rdKJf6ls43cdIa/QtappXWhylNSBvDIcXrkK6XLIry4iMd+/Hrjv96MaWFTcntYoDwoHJH5iqILOg4jCcw3VsWqlxRGCwnycyeQFtjD4kiHstbQ56tZ9Vk2KgPUvIxjXv825ZrJi1KSy6H+oH5Nk/HFq1oeiJIT7qHRv0bqATA+X4H24vOqf2XDsOmn8c0F7UKlQmqYr0OvT9kHYCh1GIHWf+Uj7igfCr9sSquvi22dqlzsz0Pap9FvLN68QfOY38hJ6kPyKvkEKZJrsw88CCy+uGrQAncLUV1SeLnlKKqzP/v9z77/+TL4vLq6GCNV8Ppph+PFhR3TYqR9gGQQOiVyMCh3G8j6r+MAHWJUeRRclJsf3XfSGlUXgHoPwPDYngg+JcpoK/ovfYghVWeLaUSGoaW0fzj++eP1Zo390I7R9Vm6pGATGC1U9Mq1IQevtv0J6GnxGU/S8PPqJo5fP29i3jUs5lcn4BWTIkbiGcuY/GJayiFin4uMw9e3iXacKFWKXOaxip1KlkW8qYifUkCHi4FTyi3yotep4xwvezzfqKPPGgjziytYmL9uxtxcDK0166LVOAAP0zeE+dXLy2dx8+rjP+B2w+Tz5ia2ryDj2HIZzW9uIl+qGuXNFO4L7X0d27qkU1IK7DGSeb9ca/uDLBGq85m1mkpadd8lJd4w+xKnf8zhKr8hrwkdLkSsQZZ/qhHzHa5vXjvSeUJ1C9G4Q+ns5kYvr68vgDu4uPBfaN2/XOHH1c2nC9Q3cz97QY6wBwFf9ia6oy3UpqcRIv7Dh5d21DNANEiFoFTJg09b7JqtRcuOlmURbwoB9jpHnWqp8zokL8ob8+4umu1qji+url4vbl58I2mpGiTK0ZZ9KlZ1QyQ7x+nry+tNeEOIb177VzevL1e+/eJioq/6xRpleLkVIxQclgudHYoRI+kIgfFZhq73rPVVsWfHAmUtVRFdc4lyp0s2k0S8OQQI2Mf8jyT+hrzwyWTTDuDUWoo34pfXK4plzg+2eJfTGQfuuZNpl54yxbWjGE/HD8Hcn1fOxYV78XpxZdtX/ivIPIrz6AVB0QgHYs5yXdcUFmx62CXdVbTmabaF12QXBbnNfdoQ2NyvUzdsYaqecmXDN/F8jg0YG1g1edHhQmw6YrYtiaeV8nivbqIjm9pLrzqxNeLyACyHgKZ7HN70oZmj15ub1/zl0725cD9fep+2G0QXkINX+J3hOA77KkbCGz/xdRxuTzv7yq96vFSngpbTzNPaUw8+FKGmalExUPNHtRqb10fIKwmYpukoKeVJuBE7mhdX0dHOBcP6lBwwGJtY0yFujQ9tHEc3mN2b11eAh3z70Fd5CApPP22UrNivkUuZCKfglDKZHjZ3i4uc3bPyq269SDtQZOrAlDO+m2SZ41tC1XyKEUeGj0VOviMvsgQ5Ww5jXiGe//gBFfvz4jhiVfVPJYyYALCl6xEbdLCPLOq5tNMsu4Et8tkHo9mk14vPlzEmfXBj37yS2OSqay5NcXd/22H/L+iovTS7nFxpvmOKJhgbhqwDqpbaomn/m+gYBzaPWV4klxySVZ2Mcv/mgrgrH1t/GFNRn1y0SO4StZYgDk9T6gfguxc+Mxp+fbl6/bx6Ka5eX69u+CNVsSoyLxy03nHTgjjidCtV7Oa7S+pWTOJTbTYRzHaTqumw+i67rccDCYfkFbJc88lolLGzhBjd9EWies11vHDqmnurct+JLR0qlqf+UNK4CilA9IAY+7SCb9Bx4uJifgFz9NV3P+24Qy1qGTKdoTNk3/FS7X+lnfjP1KFin1FHatxXvVmbIZMsSzd1oWrFWfZ3PhdNZou82G2kYx3FoxDKi29+UNLp6iKf/zZbrRVSO6Iaa6usHKo8RKjJhua44HDtptgahOBljskeB9HVS3xD65rsFNwxTQdBxhk61kzQ3O76NBt55/IRayzdIXLg7i4939xBRR+tY9XaqvYkB3mQkdH7A7J+c5IR8xK3kap6mBNZrRXj9S+I9eevnz9zA7EKPshhm8ivqPQBdTEkoaYjE2Pqt0u8ICNAmjjNqUkgYb24CMU/YSMRm4SQMq77K+zZyg4zWtT9Z6pFf34OGkYp3QetXtOqVslgeOESAq2RLs02/g3y4kRU4CmPIkG0bNKL2I94ucp/3czNOc6bjQgqS4fW0Xox4/JpxA2Kqq5Icm89imkt3f4VLDG4WCoBDf6CWE0qSLcdTpKi2aHbIfLybmveqma5ilULVZM34El+zh0dQUrzQ3fV0FCSiNIehVR7zS9+QXouPudXhLg6cm+ha0cqk1ERJAt1MJGGuzmMZRcbqXRFCgo0iNRRLR9FL1M6SbuiAjoavFuJbWegbLL+vsPk5TQ8LZ7lUaxLb6Xndr+KLUVtpFUb/2ZWhvrUsVwrj0Jdv/714/Pzx4X94/m3KVOm3PT7dfPpkdT7lcOOijMilEKnmBpVR1QX2Cthp5ipTDp4o8ow7mFDDNgOWFJX4eQSazlu7nXN21TNHSEsyzjH2kCq2vi3LS+9c4RsTLy50PXEWMcvcH7mbcQlHedOPwd21YpZhJp+Uw13XeW7YcOtbdbM0EEsS68+MMoaGu1/OnQCm3A29Tvq8L6SKnzAFct5m6qr/gg2pTqaSKvRCmzy6UEc8CSPwtpTsXtBgG+oAHKcVs9XAUa/VNmYOq2jcCLUCiTMEL9fm/Ul18xA4moGiNlVHtI1fMSGfP4znt7wucrCOs/Puo8SjUvsONzd5vW+eKHqqj8CMVjXOZ6CPWJ5cYKVPIqItyrd5v7nz19XL5+xSwGfnRy8J+9WpZoUHHcqmBytTcSTJaLGFA/ML5/QHroZFWcV1dZP1IZ1OSo/TWQOR+S5UoBzIHsKHMhQvVkKR6MhHDry9L54lUOusuZGOenhkFKJBnlxgpUC+YI4ffmZwkD8IVaIywfvSXdX8jriih9U4IEPBItJA6MQheIj/V43OyJgdFppl3YVqXICm/vSIVRNurlDCv6S9jk6UFz86SZvPUs8lGoeuGO+muzKaUrcPyBul0rQjig2NuFRaMS//NeL9MfMrwfXwme0AyOrjt1Q/kqdroCdRHbHTDbGVCTiOUb4puQyTKkopeQaFX88cuFLb0fe0xpBQeWER2ofEYn2TpoVRw2q5mOMpgrx8vvQfnBIXmxsQiAV4vTn1a+Lq19ENOiK09yXD+NPjt2o+qbCjo/No2zWDvodNBTFut1EG/t8qeG9I+0HeY6n084t4gNRh8o3L4X0w2xHejrpUK3yMx194/MZRVhdcjxCVY1m9ps7Mtoxr9/K2HTWsuUQaWNUByCIMaSuNfs+a2F11neoTxLs171xSf3T4boha2CKIU1axkCE9rPVblDjRoZ8ig4/5t3ulqZYs/NOCKPo7CHvzg41H5ePnHwXqpYun1zVW6p7+BNiyco0yIuVcjmiL/89VvXq8982++1swU/5rO9+lcKgvtaqEGxYqMquAjHAupCkMvikulEOVF40Fps0wat2/fEUq98pCcnPr0/uhi3pqn0jANIp65Bzq3PiAWJKd7ayjWxsDgjx/IYit4jDMmIUFtYOeVlXUozq/u1CH7KSQPgwzSn46ulGs6h27esiIGpbchCVBS/ZVJp5aSrgW+SH2NLGzsCSvDKq6XzWR31Ie2IytRR7OofHsJTG5gjoVHp8TfISqwBTPP91dfHz5+sV7C0KFJqIZQymxWTSqZP5AI8VLXWFsrAoFOnmOpdBBy24VRGIisouqtvJOU4NbYWgcCOajRXMng/iu6FYXx1tYyqqrnFOD05nQ1oArYoCyZTZilIa2UYxNoE4/7FARM6fw4tAgfAh4qLE8QHDWhsrlBxUpIWFl3XwVR0ThuMwMPVE8dryRZ2bp1FLZF5Gau8uzV1we7HB8H9rXZrNUqz61QyrrvaDfjvug1eiDp1mvMTrM2X3NcmLjU0gfvkx968u/PknI15fthEH0xTmMQpcVFtyQSm1iGphleAp8wTLtGCeoZ0OdVRWoyZtC7dDB/HNcDYRmvR0jCjYSXssXDoLT1O1sZH2IO5DNxTyh9pia/BokBcbm1jGqPK5+vX68vLjpUQtR9HZDZ1mY6egmK7R+iZWR9r3+gorPKmpPr5owrmMfuFXxf+K2Ckq2+d8lEZNzcqof1vtbKGEeFdlLi47pFkQ+LvlPYDBnlw51ZC5rjgk077TDFmP5DtQc2VJ9JLGvEFeZGwSYg5VY0gJfehyI0Lqe2UAt9XNUdSa6UNs+iaBkOyH3OfONWeAChxB+0WFWqxnoK6dLarkVVN92+E1EqBbTGWGCVUzd3gJxaJ6mNE4M0+24nnFhtARH70ZqERQs/iajE1iLv7by8uFbw4NXBiYJNmwS4BS8nvDppLgB4DqxWRtpsK4Tpl3CijUtduPJma23vtAZQ7SrE4F/vqMky9TR2DYYthRLIrVd6FPdGTbFuQR28I1g6yjC5+a3VK6bAzIXvh56FJP11aZYShnCFMhml1HrdVOTUf9/5Drh5BqhywFyWASdHnqca4QnRZVd7ln1CRuHSP79sgCrmy2EFuLSW8JgeGmxWG+FKomUa4H3Yk6gV8qd8XEt6jo3qv3y/xukFfIiKWRsXQgu6XeIV4LOLKqsD/sxWCqyjGA0nNVneZ6QZt2aQR0mpHrQK/SJcOSWm1RFZZYa6IuVeJp2aArCPiuqmmlwFEHpy3LxEtzqL1QNWf5l9oTXZMk1L5jj6PdFKgI5MAYffvNbW8WncEmT04dpCBSQg0mEtiR6Epb14mhkJqK2bqF2lSv1bHTbh9AY+lMhS9pwwmsNVGXPE38+GOWYRP1vtPRZVq0s6fDXxzwAlCWCCckHPZEkZOHyVM5zwK1W3A4mT2dPLOVXM8b297+H7OWZZ/hCa3pAAAAAElFTkSuQmCC\" alt=\"Visionet Systems Location\" style=\"width:600px; height:450px; border:0;\">\r\n"
				+ "  </a>";
		htmlText += "</div>";
		return htmlText;
	}
}