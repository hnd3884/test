package com.me.ems.summaryserver.summary.util;

import com.me.ems.summaryserver.summary.sync.factory.SummaryServerSyncAPI;
import java.io.IOException;
import javax.net.ssl.SSLHandshakeException;
import java.net.MalformedURLException;
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

public class SummaryAPIRedirectHandler extends APIRedirectHandler
{
    private static SummaryAPIRedirectHandler summaryAPIRedirectHandler;
    private Logger logger;
    
    public SummaryAPIRedirectHandler() {
        this.logger = Logger.getLogger("probeActionsLogger");
    }
    
    public static SummaryAPIRedirectHandler getInstance() {
        if (SummaryAPIRedirectHandler.summaryAPIRedirectHandler == null) {
            SummaryAPIRedirectHandler.summaryAPIRedirectHandler = new SummaryAPIRedirectHandler();
        }
        return SummaryAPIRedirectHandler.summaryAPIRedirectHandler;
    }
    
    @Override
    public String doAPICall(final Properties apiProperties, final JSONObject request, final JSONObject requestHeaders) throws Exception {
        return null;
    }
    
    public String doAPICall(final Long probeId, final Properties apiProps, final JSONObject bodyContentJSON) throws Exception {
        return this.doAPICall(probeId, apiProps, bodyContentJSON, false);
    }
    
    private String doAPICall(final Long probeId, final Properties apiProps, final JSONObject bodyContentJSON, final boolean isRetry) throws Exception {
        String response = null;
        if (apiProps != null) {
            DataOutputStream dataOutStream = null;
            try {
                final SummaryServerSyncAPI summaryServerSyncAPI = ProbeMgmtFactoryProvider.getSummaryServerSyncAPI();
                final HttpURLConnection urlConnection = (HttpURLConnection)summaryServerSyncAPI.createProbeServerConnection(probeId, apiProps, true, true);
                if (urlConnection != null) {
                    if (bodyContentJSON != null) {
                        this.logger.log(Level.INFO, "content JSON :" + bodyContentJSON);
                        dataOutStream = new DataOutputStream(urlConnection.getOutputStream());
                        dataOutStream.writeBytes(bodyContentJSON.toString());
                        dataOutStream.flush();
                    }
                    final int httpResponseCode = urlConnection.getResponseCode();
                    this.logger.log(Level.INFO, "Probe Server Response Code: " + httpResponseCode);
                    if (httpResponseCode == 200) {
                        final InputStreamReader inputStream = new InputStreamReader(urlConnection.getInputStream());
                        final BufferedReader rd = new BufferedReader(inputStream);
                        final StringBuffer strbuff = new StringBuffer();
                        String line;
                        while ((line = rd.readLine()) != null) {
                            strbuff.append(line);
                        }
                        response = strbuff.toString();
                        this.logger.log(Level.INFO, "Response from Probe Server:" + response);
                    }
                }
            }
            catch (final MalformedURLException e) {
                this.logger.log(Level.SEVERE, "MalformedURLException in doAPICall in SummaryAPIRedirectHandler: ", e);
                throw e;
            }
            catch (final SSLHandshakeException ex) {}
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Exception in  doAPICall in SummaryAPIRedirectHandler: ", e2);
                throw e2;
            }
            finally {
                if (dataOutStream != null) {
                    try {
                        dataOutStream.close();
                    }
                    catch (final IOException e3) {
                        this.logger.log(Level.SEVERE, "IOException in closing dataOutputStream: ", e3);
                    }
                }
            }
        }
        return response;
    }
    
    @Deprecated
    @Override
    public String doAPICall(final Properties apiProps, final JSONObject bodyContentJSON) throws Exception {
        throw new Exception("ProbeID is required");
    }
    
    static {
        SummaryAPIRedirectHandler.summaryAPIRedirectHandler = null;
    }
}
