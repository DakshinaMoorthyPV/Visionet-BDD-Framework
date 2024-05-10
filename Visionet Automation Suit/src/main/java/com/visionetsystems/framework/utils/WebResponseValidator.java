package com.visionetsystems.framework.utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.openqa.selenium.WebDriver;

public class WebResponseValidator {
	private WebDriver driver;

	public WebResponseValidator(WebDriver sDriver) {
		super();
		this.driver = sDriver;
	}

	public String Url = "";

	public void verifyWebResponseStatus() throws Exception {
		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
		SSLContext.setDefault(ctx);
		Url = driver.getCurrentUrl();
		URL url = new URL(Url);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		});
		if (conn.getResponseCode() == 200)
			conn.disconnect();
		else {
			int resp = conn.getResponseCode();
			switch (resp) {
			case HttpURLConnection.HTTP_NOT_FOUND:
				throw new Exception("WebException raised! - 404 Server - Error");
			case HttpURLConnection.HTTP_INTERNAL_ERROR:
				break;
			// throw new Exception("WebException raised! - 500 Server - Error");
			case HttpURLConnection.HTTP_NOT_AUTHORITATIVE:
				throw new Exception("WebException raised! - 203 Non-Authoritative Information");
			case HttpURLConnection.HTTP_NO_CONTENT:
				throw new Exception("WebException raised! - 204 No Content");
			case HttpURLConnection.HTTP_MOVED_PERM:
				throw new Exception("WebException raised! - 301 Temporary Redirect");
			case HttpURLConnection.HTTP_BAD_REQUEST:
				throw new Exception("WebException raised! - 400 Bad Request");
			case HttpURLConnection.HTTP_UNAUTHORIZED:
				throw new Exception("WebException raised! - 401 Unauthorized");
			case HttpURLConnection.HTTP_BAD_METHOD:
				throw new Exception("WebException raised! - 405 Method Not Allowed");
			case HttpURLConnection.HTTP_VERSION:
				throw new Exception("WebException raised! - 505 HTTP Version Not Supported");
			default:
				throw new Exception(conn.getResponseMessage() + " - New Exception");
			}
		}
	}

	private static class DefaultTrustManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}
}