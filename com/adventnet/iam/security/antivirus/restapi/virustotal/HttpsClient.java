package com.adventnet.iam.security.antivirus.restapi.virustotal;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.Proxy;
import javax.net.ssl.HttpsURLConnection;

public class HttpsClient
{
    private final HttpsURLConnection connection;
    
    public HttpsClient(final String requestURL, final String queryString, final Proxy proxy) throws MalformedURLException, IOException {
        final URL url = new URL(requestURL + "?" + queryString);
        (this.connection = (HttpsURLConnection)((proxy != null) ? url.openConnection(proxy) : url.openConnection())).setUseCaches(false);
        this.connection.setRequestMethod("GET");
        this.connection.setDoInput(true);
    }
    
    public HttpsClient(final String requestURL, final MultipartEntity entity, final Proxy proxy) throws MalformedURLException, IOException {
        final URL url = new URL(requestURL);
        (this.connection = (HttpsURLConnection)((proxy != null) ? url.openConnection(proxy) : url.openConnection())).setUseCaches(false);
        this.connection.setDoOutput(true);
        this.connection.setRequestMethod("POST");
        this.connection.setDoInput(true);
        this.connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=e2a540ab4e6c5ed79c01157c255a2b5007e157d7");
        this.process(entity, this.connection);
    }
    
    public String execute() throws IOException {
        final int status = this.connection.getResponseCode();
        if (status == 200) {
            final StringBuilder response = new StringBuilder();
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(this.connection.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            this.connection.disconnect();
            return response.toString();
        }
        if (status == 204) {
            throw new VirusTotalException("Public API request rate limit exceed");
        }
        if (status == 403) {
            throw new VirusTotalException("You do not have the required privileges");
        }
        throw new VirusTotalException("Invalid Status code");
    }
    
    public static String prepareParameters(final ConcurrentMap<String, String> parameters) throws UnsupportedEncodingException {
        final StringBuilder requestParameters = new StringBuilder();
        for (final Map.Entry<String, String> parameter : parameters.entrySet()) {
            requestParameters.append(URLEncoder.encode(parameter.getKey(), "UTF-8"));
            requestParameters.append("=").append(URLEncoder.encode(parameter.getValue(), "UTF-8"));
            requestParameters.append("&");
        }
        requestParameters.deleteCharAt(requestParameters.lastIndexOf("&"));
        return requestParameters.toString();
    }
    
    private void process(final MultipartEntity entity, final HttpsURLConnection connection) throws IOException {
        try (final InputStream content = entity.getContent()) {
            final OutputStream outputStream = connection.getOutputStream();
            final byte[] buffer = new byte[1048576];
            int bytes;
            while ((bytes = content.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytes);
            }
            outputStream.flush();
        }
    }
}
