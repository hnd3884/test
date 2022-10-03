package com.me.ems.summaryserver.probe.util;

import java.util.Iterator;
import com.me.ems.summaryserver.probe.sync.factory.ProbeSyncAPI;
import java.io.IOException;
import javax.net.ssl.SSLHandshakeException;
import java.net.MalformedURLException;
import org.json.JSONException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.util.logging.Level;
import java.net.HttpURLConnection;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import org.json.JSONObject;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.ems.summaryserver.common.util.APIRedirectHandler;

public class ProbeAPIRedirectHandler extends APIRedirectHandler
{
    private static ProbeAPIRedirectHandler probeAPIRedirectHandler;
    private Logger logger;
    
    public ProbeAPIRedirectHandler() {
        this.logger = Logger.getLogger("probeActionsLogger");
    }
    
    public static ProbeAPIRedirectHandler getInstance() {
        if (ProbeAPIRedirectHandler.probeAPIRedirectHandler == null) {
            ProbeAPIRedirectHandler.probeAPIRedirectHandler = new ProbeAPIRedirectHandler();
        }
        return ProbeAPIRedirectHandler.probeAPIRedirectHandler;
    }
    
    @Override
    public String doAPICall(final Properties apiProps, final JSONObject requestBody, final JSONObject requestHeaders) throws Exception {
        return this.doAPICall(apiProps, requestBody, requestHeaders, false);
    }
    
    @Override
    public String doAPICall(final Properties apiProps, final JSONObject requestBody) throws Exception {
        return this.doAPICall(apiProps, requestBody, null, false);
    }
    
    private String doAPICall(final Properties apiProps, final JSONObject requestBody, final JSONObject headers, final boolean isRetry) throws Exception {
        String response = null;
        HttpURLConnection urlConnection = null;
        if (apiProps != null) {
            DataOutputStream dataOutStream = null;
            try {
                final ProbeSyncAPI probeSyncAPI = ProbeMgmtFactoryProvider.getProbeSyncAPI();
                urlConnection = (HttpURLConnection)probeSyncAPI.createSummaryServerConnection(apiProps);
                if (urlConnection != null) {
                    if (headers != null) {
                        final Iterator<String> iterator = headers.keys();
                        while (iterator.hasNext()) {
                            final String key = iterator.next();
                            urlConnection.setRequestProperty(key, String.valueOf(headers.get(key)));
                        }
                    }
                    if (requestBody != null) {
                        this.logger.log(Level.FINE, "content JSON :" + requestBody);
                        dataOutStream = new DataOutputStream(urlConnection.getOutputStream());
                        dataOutStream.writeBytes(requestBody.toString());
                        dataOutStream.flush();
                    }
                    final int httpResponseCode = urlConnection.getResponseCode();
                    this.logger.log(Level.INFO, "Summary Server Response Code: " + httpResponseCode);
                    if (httpResponseCode == 200) {
                        final InputStreamReader inputStream = new InputStreamReader(urlConnection.getInputStream());
                        final BufferedReader rd = new BufferedReader(inputStream);
                        final StringBuffer stringBuffer = new StringBuffer();
                        String line;
                        while ((line = rd.readLine()) != null) {
                            stringBuffer.append(line);
                        }
                        response = stringBuffer.toString();
                        this.logger.log(Level.INFO, "Response from Summary Server:" + response);
                        rd.close();
                        inputStream.close();
                    }
                }
            }
            catch (final JSONException je) {
                this.logger.log(Level.SEVERE, "Exception on adding header :", (Throwable)je);
            }
            catch (final MalformedURLException e) {
                this.logger.log(Level.SEVERE, "MalformedURLException in doAPICall: ", e);
                throw e;
            }
            catch (final SSLHandshakeException ex) {}
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Exception in doAPICall in ProbeAPIRedirectHandler: ", e2);
                throw e2;
            }
            finally {
                if (dataOutStream != null) {
                    try {
                        dataOutStream.close();
                        urlConnection.disconnect();
                    }
                    catch (final IOException e3) {
                        this.logger.log(Level.SEVERE, "IOException in closing dataOutputStream: ", e3);
                    }
                }
            }
        }
        return response;
    }
    
    static {
        ProbeAPIRedirectHandler.probeAPIRedirectHandler = null;
    }
}
