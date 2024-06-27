package com.visionetsystems.framework.utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
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

        // First connection to capture cookies
        HttpsURLConnection initialConn = (HttpsURLConnection) url.openConnection();
        initialConn.setRequestProperty("User-Agent", "Mozilla/5.0 ...");
        initialConn.setHostnameVerifier((hostname, session) -> true);
        initialConn.setInstanceFollowRedirects(true);
        // Connect to capture cookies
        initialConn.connect();
        String cookiesHeader = initialConn.getHeaderField("Set-Cookie");
        initialConn.disconnect(); // Close the initial connection

        // Subsequent connection using captured cookies
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 ...");
        if (cookiesHeader != null) {
            conn.setRequestProperty("Cookie", cookiesHeader); // Set cookies before connecting
        }
        conn.setHostnameVerifier((hostname, session) -> true);
        conn.setInstanceFollowRedirects(true);

        try {
            if (conn.getResponseCode() == 200) {
                System.out.println("Connection successful.");
            } else {
                handleHttpError(conn.getResponseCode(), conn);
            }
        } finally {
            conn.disconnect();
        }
    }


    private void handleHttpError(int resp, HttpsURLConnection conn) throws Exception {
        String errorDetail = conn.getResponseMessage();  // Fetches the error message from the HTTP response
        switch (resp) {
            case HttpURLConnection.HTTP_NOT_FOUND:
                throw new Exception("404 Server - Error: Not Found");
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                throw new Exception("401 Server - Error: Unauthorized");
            case HttpURLConnection.HTTP_FORBIDDEN:
                throw new Exception("403 Server - Error: Forbidden");
            case HttpURLConnection.HTTP_BAD_REQUEST:
                throw new Exception("400 Server - Error: Bad Request");
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                throw new Exception("500 Server - Error: Internal Server Error");
            case HttpURLConnection.HTTP_BAD_GATEWAY:
                throw new Exception("502 Server - Error: Bad Gateway");
            case HttpURLConnection.HTTP_UNAVAILABLE:
                throw new Exception("503 Server - Error: Service Unavailable");
            case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                throw new Exception("504 Server - Error: Gateway Timeout");
            case HttpURLConnection.HTTP_MOVED_TEMP:
                throw new Exception("302 Server - Error: Moved Temporarily");
            case HttpURLConnection.HTTP_SEE_OTHER:
                throw new Exception("303 Server - Error: See Other");
            case HttpURLConnection.HTTP_NOT_MODIFIED:
                throw new Exception("304 Server - Error: Not Modified");
            default:
                throw new Exception(resp + " - " + errorDetail + " - New Exception");
        }
    }


    private static class DefaultTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}
