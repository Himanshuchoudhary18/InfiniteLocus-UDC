package utilsApi;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utilities.Base;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiUtils extends Base {
    public static Map<String, Long> latencyMap = new ConcurrentHashMap<>();
    public static List<APIResponseTime> apiList = new CopyOnWriteArrayList<>();

    static Logger logger = LoggerFactory.getLogger(ApiUtils.class);
    private ThreadLocal<HashMapNew> sTestDetails = new ThreadLocal<HashMapNew>() {
        @Override
        protected HashMapNew initialValue() {
            return null;
        }
    };

    public String readAll(InputStream is) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer, 0, 1024)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }

    public InputStream del(String url, String[] key, String[] value, boolean noReporting) throws Exception {
        InputStream is = null;
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }};

        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        URLConnection urlCon = new URL(url).openConnection();

        if (key != null && value != null)
            urlCon = setHeaders(urlCon, key, value);

        String rawResponse = "", rawRequest = "";

        int responseCode = 0;
        String responseMessage = "";

        if (url.trim().toLowerCase().startsWith("https://")) {
            ((HttpsURLConnection) urlCon).setSSLSocketFactory(sslSocketFactory);
            ((HttpsURLConnection) urlCon).setRequestMethod("DELETE");
            rawRequest = getRequestHeaders(urlCon, rawRequest);
            responseCode = ((HttpsURLConnection) urlCon).getResponseCode();
            responseMessage = ((HttpsURLConnection) urlCon).getResponseMessage();
        } else {
            ((HttpURLConnection) urlCon).setRequestMethod("DELETE");
            rawRequest = getRequestHeaders(urlCon, rawRequest);
            responseCode = ((HttpURLConnection) urlCon).getResponseCode();
            responseMessage = ((HttpURLConnection) urlCon).getResponseMessage();
        }

        rawRequest += "Url : " + url + "\n";
        rawRequest += "Request method : DELETE\n\n";

        if (responseCode == 200 || responseCode == 204) {
            is = urlCon.getInputStream();
            InputStream[] input = getClonedStream(is, 2);
            String response = readAll(input[0]);
            rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
            if (!noReporting && sTestDetails != null && sTestDetails.get() != null) {
                sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
                sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
                sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
                sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            }
            is = input[1];
        } else {
            if (url.trim().toLowerCase().startsWith("https://"))
                is = ((HttpsURLConnection) urlCon).getErrorStream();
            else
                is = ((HttpURLConnection) urlCon).getErrorStream();
            if (is != null) {
                InputStream[] input = getClonedStream(is, 2);
                String response = readAll(input[0]);
                rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
            }
            if (!noReporting && sTestDetails != null && sTestDetails.get() != null) {
                sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
                sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
                sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
                sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            }
            throw new Exception("Server response code : " + responseCode);
        }
        return is;
    }

    public InputStream post(String url, String payload, String[] key, String[] value, boolean noReporting) throws Exception {
        InputStream is = null;
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }};

        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        URLConnection urlCon = new URL(url).openConnection();
        urlCon.setRequestProperty("Method", "POST");

        if (key != null && value != null)
            urlCon = setHeaders(urlCon, key, value);

        String rawResponse = "", rawRequest = "";
        rawRequest += getRequestHeaders(urlCon, rawRequest);

        urlCon.setDoOutput(true);
        // Send request
        DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream());
        wr.writeBytes(payload);
        wr.flush();
        wr.close();

        int responseCode = 0;
        String responseMessage = "";

        if (url.trim().toLowerCase().startsWith("https://")) {
            ((HttpsURLConnection) urlCon).setSSLSocketFactory(sslSocketFactory);
            responseCode = ((HttpsURLConnection) urlCon).getResponseCode();
            responseMessage = ((HttpsURLConnection) urlCon).getResponseMessage();
        } else {
            responseCode = ((HttpURLConnection) urlCon).getResponseCode();
            responseMessage = ((HttpURLConnection) urlCon).getResponseMessage();
        }

        rawResponse += getResponseHeaders(urlCon, rawResponse) + "\n";
        rawRequest += "Url : " + url + "\n";
        rawRequest += "Request method : POST\n\n";
        rawRequest += payload.startsWith("{") ? new JSONObject(payload).toString(2) : payload.startsWith("[") ? new JSONArray(payload).toString(2) : payload + "\n";

        if (responseCode == 201 || responseCode == 200 || responseCode == 204) {
            is = urlCon.getInputStream();
            InputStream[] input = getClonedStream(is, 2);
            String response = readAll(input[0]);
            rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
            if (!noReporting && sTestDetails != null && sTestDetails.get() != null) {
                sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
                sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
                sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
                sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            }
            is = input[1];
        } else {
            if (url.trim().toLowerCase().startsWith("https://"))
                is = ((HttpsURLConnection) urlCon).getErrorStream();
            else
                is = ((HttpURLConnection) urlCon).getErrorStream();
            if (is != null) {
                InputStream[] input = getClonedStream(is, 2);
                String response = readAll(input[0]);
                rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
            }
            if (!noReporting && sTestDetails != null && sTestDetails.get() != null) {
                sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
                sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
                sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
                sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            }
            throw new Exception("Server response code : " + responseCode);
        }
        return is;
    }

    public Object[] post(String url, String payload, String[] key, String[] value, boolean skipException, boolean noReporting) throws Exception {
        InputStream is = null;
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }};

        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        URLConnection urlCon = new URL(url).openConnection();
        urlCon.setRequestProperty("Method", "POST");

        if (key != null && value != null)
            urlCon = setHeaders(urlCon, key, value);

        String rawResponse = "", rawRequest = "";
        rawRequest += getRequestHeaders(urlCon, rawRequest);

        urlCon.setDoOutput(true);
        // Send request
        DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream());
        wr.writeBytes(payload);
        wr.flush();
        wr.close();

        int responseCode = 0;
        String responseMessage = "";

        if (url.trim().toLowerCase().startsWith("https://")) {
            ((HttpsURLConnection) urlCon).setSSLSocketFactory(sslSocketFactory);
            responseCode = ((HttpsURLConnection) urlCon).getResponseCode();
            responseMessage = ((HttpsURLConnection) urlCon).getResponseMessage();
        } else {
            responseCode = ((HttpURLConnection) urlCon).getResponseCode();
            responseMessage = ((HttpURLConnection) urlCon).getResponseMessage();
        }

        rawResponse += getResponseHeaders(urlCon, rawResponse) + "\n";
        rawRequest += "Url : " + url + "\n";
        rawRequest += "Request method : POST\n\n";
        rawRequest += payload.startsWith("{") ? new JSONObject(payload).toString(2) : payload.startsWith("[") ? new JSONArray(payload).toString(2) : payload + "\n";
        if (responseCode == 201 || responseCode == 200 || responseCode == 204) {
            is = urlCon.getInputStream();
            InputStream[] input = getClonedStream(is, 2);
            String response = readAll(input[0]);
            rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
            if (!noReporting && sTestDetails != null && sTestDetails.get() != null) {
                sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
                sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
                sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
                sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            }
            is = input[1];
        } else {
            if (url.trim().toLowerCase().startsWith("https://"))
                is = ((HttpsURLConnection) urlCon).getErrorStream();
            else
                is = ((HttpURLConnection) urlCon).getErrorStream();
            if (is != null) {
                InputStream[] input = getClonedStream(is, 2);
                String response = readAll(input[0]);
                rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
                is = input[1];
            }
            if (!noReporting && sTestDetails != null && sTestDetails.get() != null) {
                sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
                sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
                sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
                sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            }
            if (!skipException)
                throw new Exception("Server response code : " + responseCode);
        }
        return new Object[]{is, responseCode};
    }

    public Object[] post(String url, byte[] payload, String[] key, String[] value, boolean skipException, boolean noReporting) throws Exception {
        InputStream is = null;
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }};

        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        URLConnection urlCon = new URL(url).openConnection();
        urlCon.setRequestProperty("Method", "POST");

        if (key != null && value != null)
            urlCon = setHeaders(urlCon, key, value);

        String rawResponse = "", rawRequest = "";
        rawRequest += getRequestHeaders(urlCon, rawRequest);

        urlCon.setDoOutput(true);
        // Send request
        DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream());
        wr.write(payload);
        wr.flush();
        wr.close();

        int responseCode = 0;
        String responseMessage = "";

        if (url.trim().toLowerCase().startsWith("https://")) {
            ((HttpsURLConnection) urlCon).setSSLSocketFactory(sslSocketFactory);
            responseCode = ((HttpsURLConnection) urlCon).getResponseCode();
            responseMessage = ((HttpsURLConnection) urlCon).getResponseMessage();
        } else {
            responseCode = ((HttpURLConnection) urlCon).getResponseCode();
            responseMessage = ((HttpURLConnection) urlCon).getResponseMessage();
        }

        rawResponse += getResponseHeaders(urlCon, rawResponse) + "\n";
        rawRequest += "Url : " + url + "\n";
        rawRequest += "Request method : POST\n\n";

        if (responseCode == 201 || responseCode == 200 || responseCode == 204) {
            is = urlCon.getInputStream();
            InputStream[] input = getClonedStream(is, 2);
            String response = readAll(input[0]);

            rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
            if (!noReporting && sTestDetails != null && sTestDetails.get() != null) {
                sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
                sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
                sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
                sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            }
            is = input[1];
        } else {
            if (url.trim().toLowerCase().startsWith("https://"))
                is = ((HttpsURLConnection) urlCon).getErrorStream();
            else
                is = ((HttpURLConnection) urlCon).getErrorStream();
            if (is != null) {
                InputStream[] input = getClonedStream(is, 2);
                String response = readAll(input[0]);
                rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
                is = input[1];
            }
            if (!noReporting && sTestDetails != null && sTestDetails.get() != null) {
                sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
                sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
                sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
                sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            }
            if (!skipException)
                throw new Exception("Server response code : " + responseCode);
        }
        return new Object[]{is, responseCode};
    }

    public Object[] post(String url, byte[] payload, String[] key, String[] value, boolean skipException, boolean noReporting, String str_payload) throws Exception {
        InputStream is = null;
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }};

        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        URLConnection urlCon = new URL(url).openConnection();
        urlCon.setRequestProperty("Method", "POST");

        if (key != null && value != null)
            urlCon = setHeaders(urlCon, key, value);

        String rawResponse = "", rawRequest = "";
        rawRequest += getRequestHeaders(urlCon, rawRequest);

        urlCon.setDoOutput(true);
        // Send request
        DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream());
        wr.write(payload);
        wr.flush();
        wr.close();

        int responseCode = 0;
        String responseMessage = "";

        if (url.trim().toLowerCase().startsWith("https://")) {
            ((HttpsURLConnection) urlCon).setSSLSocketFactory(sslSocketFactory);
            responseCode = ((HttpsURLConnection) urlCon).getResponseCode();
            responseMessage = ((HttpsURLConnection) urlCon).getResponseMessage();
        } else {
            responseCode = ((HttpURLConnection) urlCon).getResponseCode();
            responseMessage = ((HttpURLConnection) urlCon).getResponseMessage();
        }

        rawResponse += getResponseHeaders(urlCon, rawResponse) + "\n";
        rawRequest += "Url : " + url + "\n";
        rawRequest += "Request method : POST\n\n";
        rawRequest += str_payload.startsWith("{") ? new JSONObject(str_payload).toString(2) : str_payload.startsWith("[") ? new JSONArray(str_payload).toString(2) : str_payload + "\n";
        if (responseCode == 201 || responseCode == 200 || responseCode == 204) {
            is = urlCon.getInputStream();
            InputStream[] input = getClonedStream(is, 2);
            String response = readAll(input[0]);

            rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
            if (!noReporting && sTestDetails != null && sTestDetails.get() != null) {
                sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
                sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
                sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
                sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            }
            is = input[1];
        } else {
            if (url.trim().toLowerCase().startsWith("https://"))
                is = ((HttpsURLConnection) urlCon).getErrorStream();
            else
                is = ((HttpURLConnection) urlCon).getErrorStream();
            if (is != null) {
                InputStream[] input = getClonedStream(is, 2);
                String response = readAll(input[0]);
                rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
                is = input[1];
            }
            if (!noReporting && sTestDetails != null && sTestDetails.get() != null) {
                sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
                sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
                sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
                sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            }
            if (!skipException)
                throw new Exception("Server response code : " + responseCode);
        }
        return new Object[]{is, responseCode};
    }

    public InputStream patch(String url, String payload, String[] key, String[] value, boolean noReporting) throws Exception {
        InputStream is = null;
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }};

        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        URLConnection urlCon = new URL(url).openConnection();
        urlCon.setRequestProperty("X-HTTP-Method-Override", "PATCH");
        urlCon.setRequestProperty("Method", "POST");

        if (key != null && value != null)
            urlCon = setHeaders(urlCon, key, value);

        String rawResponse = "", rawRequest = "";
        rawRequest += getRequestHeaders(urlCon, rawRequest);

        urlCon.setDoOutput(true);
        // Send request
        DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream());
        wr.writeBytes(payload);
        wr.flush();
        wr.close();

        int responseCode = 0;
        String responseMessage = "";

        if (url.trim().toLowerCase().startsWith("https://")) {
            ((HttpsURLConnection) urlCon).setSSLSocketFactory(sslSocketFactory);
            responseCode = ((HttpsURLConnection) urlCon).getResponseCode();
            responseMessage = ((HttpsURLConnection) urlCon).getResponseMessage();
        } else {
            responseCode = ((HttpURLConnection) urlCon).getResponseCode();
            responseMessage = ((HttpURLConnection) urlCon).getResponseMessage();
        }

        rawResponse += getResponseHeaders(urlCon, rawResponse) + "\n";
        rawRequest += "Url : " + url + "\n";
        rawRequest += "Request method : PATCH\n\n";
        rawRequest += payload.startsWith("{") ? new JSONObject(payload).toString(2) : payload.startsWith("[") ? new JSONArray(payload).toString(2) : payload + "\n";

        if (responseCode == 201 || responseCode == 200 || responseCode == 204) {
            is = urlCon.getInputStream();
            InputStream[] input = getClonedStream(is, 2);
            String response = readAll(input[0]);
            rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
            if (!noReporting && sTestDetails != null && sTestDetails.get() != null) {
                sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
                sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
                sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
                sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            }
            is = input[1];
        } else {
            if (url.trim().toLowerCase().startsWith("https://"))
                is = ((HttpsURLConnection) urlCon).getErrorStream();
            else
                is = ((HttpURLConnection) urlCon).getErrorStream();
            if (is != null) {
                InputStream[] input = getClonedStream(is, 2);
                String response = readAll(input[0]);
                rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
            }
            if (!noReporting && sTestDetails != null && sTestDetails.get() != null) {
                sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
                sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
                sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
                sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            }
            throw new Exception("Server response code : " + responseCode);
        }
        return is;
    }

    public Object[] get(String url, String[] key, String[] value, boolean noReporting) throws Exception {
        InputStream is = null;
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }
        }};

        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        URLConnection urlCon = new URL(url).openConnection();
        urlCon.setRequestProperty("Method", "GET");

        if (key != null && value != null) {
            urlCon = setHeaders(urlCon, key, value);
        }

        String rawResponse = "", rawRequest = "";
        rawRequest += getRequestHeaders(urlCon, rawRequest);

        int responseCode = 0;
        String responseMessage = "";

        if (url.trim().toLowerCase().startsWith("https://")) {
            ((HttpsURLConnection) urlCon).setSSLSocketFactory(sslSocketFactory);
            responseCode = ((HttpsURLConnection) urlCon).getResponseCode();
            responseMessage = ((HttpsURLConnection) urlCon).getResponseMessage();
        } else {
            responseCode = ((HttpURLConnection) urlCon).getResponseCode();
            responseMessage = ((HttpURLConnection) urlCon).getResponseMessage();
        }

        rawResponse += getResponseHeaders(urlCon, rawResponse) + "\n";
        rawRequest += "Url : " + url + "\n";
        rawRequest += "Request method : GET\n\n";

        if (responseCode == 200 || responseCode == 204) {
            is = urlCon.getInputStream();
            if (!noReporting && sTestDetails != null && sTestDetails.get() != null) {
                InputStream[] input = getClonedStream(is, 2);
                String response = readAll(input[0]);
                rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
                sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
                sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
                sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
                sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
                is = input[1];
            }
        } else {
            if (url.trim().toLowerCase().startsWith("https://"))
                is = ((HttpsURLConnection) urlCon).getErrorStream();
            else
                is = ((HttpURLConnection) urlCon).getErrorStream();
            if (is != null && !noReporting) {
                InputStream[] input = getClonedStream(is, 2);
                String response = readAll(input[0]);
                rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
                is = input[1];
            }
            if (!noReporting && sTestDetails != null) {
                sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
                sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
                sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
                sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            }
        }
        return new Object[]{is, responseCode, responseMessage};
    }


    public InputStream[] getClonedStream(InputStream input, int count) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = input.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();

        InputStream[] is = new InputStream[count];
        for (int i = 0; i < count; i++) {
            is[i] = new ByteArrayInputStream(baos.toByteArray());
        }
        return is;
    }

    public URLConnection setHeaders(URLConnection urlCon, String[] key, String[] value) {
        for (int i = 0; i < key.length; i++) {
            urlCon.setRequestProperty(key[i].trim(), value[i].trim());
        }

        return urlCon;
    }

//    public URLConnection setHeaders(URLConnection urlCon, List<Header> headers){
//        if(headers != null && headers.size() > 0) {
//            for(int i = 0 ; i < headers.size(); i++){
//                Header header = headers.get(i);
//                urlCon.setRequestProperty(header.getName().trim(), header.getValue().trim());
//            }
//        }
//
//        return urlCon;
//    }

    public HttpURLConnection setHeaders(HttpURLConnection urlCon, String[] key, String[] value) {
        for (int i = 0; i < key.length; i++) {
            urlCon.setRequestProperty(key[i].trim(), value[i].trim());
        }

        return urlCon;
    }

    public JSONObject convertToJSON(InputStream is) throws JSONException, IOException {
        String jsonText = readAll(is);
        if (!jsonText.trim().equalsIgnoreCase("")) {
            JSONObject json = new JSONObject(jsonText);
            return json;
        } else
            return null;
    }

    public JSONArray convertToJSONArray(InputStream is) throws JSONException, IOException {
        String jsonText = readAll(is);
        if (!jsonText.trim().equalsIgnoreCase("")) {
            JSONArray json = new JSONArray(jsonText);
            return json;
        } else
            return null;
    }

    public String getRequestHeaders(URLConnection urlCon, String rawRequest) {
        Map<String, List<String>> requestheaders = urlCon.getRequestProperties();
        Set<String> keys = requestheaders.keySet();
        Iterator<String> iter = keys.iterator();
        while (iter.hasNext()) {
            String keyName = iter.next();
            if (keyName == null || keyName.trim().equalsIgnoreCase("null"))
                continue;
            rawRequest += keyName + " : ";
            List<String> values = requestheaders.get(keyName);
            int i = 0;
            rawRequest += values.get(i);
            for (i = 1; i < values.size(); i++) {
                rawRequest += ", " + values.get(i);
            }
            rawRequest += "\n";
        }
        if (this.sTestDetails != null && this.sTestDetails.get() != null)
            sTestDetails.get().put("REQUEST_HEADERS", rawRequest);
        return rawRequest;
    }

    public String getResponseHeaders(URLConnection urlCon, String rawResponse) {
        Map<String, List<String>> responseheaders = urlCon.getHeaderFields();
        Set<String> keys = responseheaders.keySet();
        Iterator<String> iter = keys.iterator();
        String cookies = "";
        while (iter.hasNext()) {
            String keyName = iter.next();
            if ("Set-Cookie".equalsIgnoreCase(keyName)) {
                List<String> headerFieldValue = responseheaders.get(keyName);
                for (String headerValue : headerFieldValue) {
                    String[] fields = headerValue.split(";\\s*");
                    String cookieValue = fields[0];
                    cookies += cookieValue + ";";
                }
            }

            if (keyName == null || keyName.trim().equalsIgnoreCase("null"))
                continue;
            rawResponse += keyName + " : ";
            List<String> values = responseheaders.get(keyName);
            int i = 0;
            rawResponse += values.get(i);
            for (i = 1; i < values.size(); i++) {
                rawResponse += ", " + values.get(i);
            }
            rawResponse += "\n";
        }
        if (this.sTestDetails != null && this.sTestDetails.get() != null) {
            sTestDetails.get().put("RESPONSE_HEADERS", rawResponse);
            sTestDetails.get().put("RESPONSE_COOKIES", cookies);
        }
        return rawResponse;
    }

    public String getCreditCardNumber(String cardType) throws Exception {
        int card = 0;
        String input = "";
        switch (cardType.trim().toUpperCase()) {
            case "VISA":
                card = 1;
                input = "visa";
                break;
            case "MASTERCARD":
                card = 2;
                input = "mastercard";
                break;
            default:
                card = 3;
                input = "amex";
        }

        try {
            String cookies = "__cfduid=d5cfc3521dde8b71e0f1e6b06d5f860f51500026598; __gads=ID=5dafddbceb1044b5:T=1500026603:S=ALNI_MbgbGMInCIoHet1ZW6NX0LuBP9LCg; __utma=19553963.1287276680.1500026607.1500026607.1500026607.1; __utmc=19553963; __utmz=19553963.1500026607.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided)";
            Object[] obj = (Object[]) get("https://api.bincodes.com/cc-gen/?format=json&api_key=f218881aaaef57bdc4f788bf8fbbf4e8&input=" + input, new String[]{"cookie"}, new String[]{cookies}, true);
            InputStream is = (InputStream) obj[0];
            JSONObject jsonObject = convertToJSON(is);
            return jsonObject.getString("number");
        } catch (Exception ex) {
            //Do Nothing
        }

        URL url = new URL("http://credit-card-generator.2-ee.com/gencc.htm");
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("card", card);

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        BufferedReader br;
        try {
            conn.getOutputStream().write(postDataBytes);
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        } catch (Exception ex) {
            return "4111111111111111";
        }

        String output = "";
        Boolean keepGoing = true;
        while (keepGoing) {
            String currentLine = br.readLine();
            if (currentLine == null) {
                keepGoing = false;
            } else {
                output += currentLine;
            }
        }

        Pattern r = Pattern.compile("(?:(\\d+ {2}\\d+ {2}\\d+[ ]{2}[0-9]+)|(\\d+ {2}\\d+ {2}\\d+))");
        Matcher m = r.matcher(output);
        String creditCardNumber = "";
        if (m.find()) {
            creditCardNumber = m.group(0).replaceAll(" ", "");
            logger.info("Found value: " + creditCardNumber);
        } else {
            creditCardNumber = "4111111111111111";
            logger.info("Found value: " + creditCardNumber);
        }

        return creditCardNumber;
    }

    public String toCamelCase(String s) {
        String[] parts = s.split(" ");
        String camelCaseString = "";
        for (String part : parts) {
            camelCaseString = camelCaseString + toProperCase(part) + " ";
        }
        return camelCaseString.trim();
    }

    String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }

//    public JSONObject parseJsonFile(String filename) throws Exception {
//        String jsonText = readAll(new FileReader(filename));
//        JSONObject obj = new JSONObject(jsonText);
//        return obj;
//    }

    public JSONObject update(JSONObject obj, String keyMain, Object newValue) throws Exception {
        Iterator<String> iterator = obj.keys();
        String key = null;
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            if ((key.equals(keyMain))) {
                obj.put(key, newValue);
                return obj;
            }
            if (obj.optJSONObject(key) != null) {
                update(obj.getJSONObject(key), keyMain, newValue);
            }

            // if it's jsonarray
            if (obj.optJSONArray(key) != null) {
                JSONArray jArray = obj.getJSONArray(key);
                int flag = 0;
                for (int i = 0; i < jArray.length(); i++) {
                    if (jArray.get(i) instanceof JSONObject) {
                        flag = 1;
                        update(jArray.getJSONObject(i), keyMain, newValue);
                    }
                }
                if (flag == 0) {
                    if (String.valueOf(newValue).trim().contains("&&")) {
                        if ((key.equals(keyMain))) {
                            // put new value
                            List<String> newValues = new ArrayList<String>(
                                    Arrays.asList(String.valueOf(newValue).trim().split("&&")));
                            obj.put(key, newValues);
                            return obj;
                        }
                    }
                }
            }
        }
        return obj;
    }

    public InputStream put(String url, String payload, String[] key, String[] value, boolean noReporting) throws Exception {
        InputStream is = null;
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }};

        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        URLConnection urlCon = new URL(url).openConnection();
        if (url.trim().toLowerCase().startsWith("https://")) {
            ((HttpsURLConnection) urlCon).setRequestMethod("PUT");
        } else {
            ((HttpURLConnection) urlCon).setRequestMethod("PUT");
        }

        if (key != null && value != null)
            urlCon = setHeaders(urlCon, key, value);

        String rawResponse = "", rawRequest = "";
        rawRequest += getRequestHeaders(urlCon, rawRequest);

        urlCon.setDoOutput(true);
        // Send request
        DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream());
        wr.writeBytes(payload);
        wr.flush();
        wr.close();

        int responseCode = 0;
        String responseMessage = "";

        if (url.trim().toLowerCase().startsWith("https://")) {
            ((HttpsURLConnection) urlCon).setSSLSocketFactory(sslSocketFactory);
            responseCode = ((HttpsURLConnection) urlCon).getResponseCode();
            responseMessage = ((HttpsURLConnection) urlCon).getResponseMessage();
        } else {
            responseCode = ((HttpURLConnection) urlCon).getResponseCode();
            responseMessage = ((HttpURLConnection) urlCon).getResponseMessage();
        }

        rawResponse += getResponseHeaders(urlCon, rawResponse) + "\n";
        rawRequest += "Url : " + url + "\n";
        rawRequest += "Request method : PUT\n\n";
        rawRequest += payload.startsWith("{") ? new JSONObject(payload).toString(2) : payload.startsWith("[") ? new JSONArray(payload).toString(2) : payload + "\n";

        if (responseCode == 201 || responseCode == 200 || responseCode == 204) {
            is = urlCon.getInputStream();
            InputStream[] input = getClonedStream(is, 2);
            String response = readAll(input[0]);
            rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
            if (!noReporting && sTestDetails != null && sTestDetails.get() != null) {
                sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
                sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
                sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
                sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            }
            is = input[1];
        } else {
            if (url.trim().toLowerCase().startsWith("https://"))
                is = ((HttpsURLConnection) urlCon).getErrorStream();
            else
                is = ((HttpURLConnection) urlCon).getErrorStream();
            if (is != null) {
                InputStream[] input = getClonedStream(is, 2);
                String response = readAll(input[0]);
                rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
            }
            if (!noReporting && sTestDetails != null && sTestDetails.get() != null) {
                sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
                sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
                sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
                sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            }
            throw new Exception("Server response code : " + responseCode);
        }
        return is;
    }

    public Object[] put(String url, String payload, String[] key, String[] value, boolean skipException, boolean noReporting) throws Exception {
        InputStream is = null;
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }};

        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        URLConnection urlCon = new URL(url).openConnection();
        if (url.trim().toLowerCase().startsWith("https://")) {
            ((HttpsURLConnection) urlCon).setRequestMethod("PUT");
        } else {
            ((HttpURLConnection) urlCon).setRequestMethod("PUT");
        }

        if (key != null && value != null)
            urlCon = setHeaders(urlCon, key, value);

        String rawResponse = "", rawRequest = "";
        rawRequest += getRequestHeaders(urlCon, rawRequest);

        urlCon.setDoOutput(true);
        // Send request
        DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream());
        wr.writeBytes(payload);
        wr.flush();
        wr.close();

        int responseCode = 0;
        String responseMessage = "";

        if (url.trim().toLowerCase().startsWith("https://")) {
            ((HttpsURLConnection) urlCon).setSSLSocketFactory(sslSocketFactory);
            responseCode = ((HttpsURLConnection) urlCon).getResponseCode();
            responseMessage = ((HttpsURLConnection) urlCon).getResponseMessage();
        } else {
            responseCode = ((HttpURLConnection) urlCon).getResponseCode();
            responseMessage = ((HttpURLConnection) urlCon).getResponseMessage();
        }

        rawResponse += getResponseHeaders(urlCon, rawResponse) + "\n";
        rawRequest += "Url : " + url + "\n";
        rawRequest += "Request method : PUT\n\n";
        rawRequest += payload.startsWith("{") ? new JSONObject(payload).toString(2) : payload.startsWith("[") ? new JSONArray(payload).toString(2) : payload + "\n";

        if (responseCode == 201 || responseCode == 200 || responseCode == 204) {
            is = urlCon.getInputStream();
            InputStream[] input = getClonedStream(is, 2);
            String response = readAll(input[0]);
            rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
            if (!noReporting && sTestDetails != null && sTestDetails.get() != null) {
                sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
                sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
                sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
                sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            }
            is = input[1];
        } else {
            if (url.trim().toLowerCase().startsWith("https://"))
                is = ((HttpsURLConnection) urlCon).getErrorStream();
            else
                is = ((HttpURLConnection) urlCon).getErrorStream();
            if (is != null) {
                InputStream[] input = getClonedStream(is, 2);
                String response = readAll(input[0]);
                rawResponse += response.startsWith("{") ? new JSONObject(response).toString(2) : response.startsWith("[") ? new JSONArray(response).toString(2) : response;
            }
            if (!noReporting && sTestDetails != null && sTestDetails.get() != null) {
                sTestDetails.get().put("LOG_RAW_REQUEST", rawRequest);
                sTestDetails.get().put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
                sTestDetails.get().put("LOG_RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "\n\n" + rawResponse);
                sTestDetails.get().put("RAW_RESPONSE", "Server response code : " + responseCode + " " + responseMessage + "<br /><br />" + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
            }
            if (!skipException)
                throw new Exception("Server response code : " + responseCode);
        }
        return new Object[]{is, responseCode};
    }

    public long getDownloadFileSize(InputStream is, String fileName) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(new File(fileName));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }
        return new File(fileName).length();
    }

    public static class HashMapNew extends HashMap<String, String> implements Cloneable {
        static final long serialVersionUID = 1L;

        public String get(Object key) {
            String value = (String) super.get(key);
            if (value == null) {
                return "";
            }
            return value;
        }

        public String put(String key, String value) {
            String val = super.put(key, value);
            return val;
        }

        @Override
        public Object clone() {
            HashMapNew cloned = (HashMapNew) super.clone();
            cloned.putAll(this);
            return cloned;
        }
    }
}
