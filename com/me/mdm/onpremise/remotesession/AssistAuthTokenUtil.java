package com.me.mdm.onpremise.remotesession;

import java.util.Hashtable;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.net.PasswordAuthentication;
import java.net.Authenticator;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONException;
import java.net.MalformedURLException;
import java.io.IOException;
import java.util.logging.Level;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import java.util.logging.Logger;
import java.util.Properties;

public class AssistAuthTokenUtil
{
    private static final String ASSISTACCOUNTURL = "AssistAccountUrl";
    private static final String DOMAINS = "Domains";
    private static final String CUSTOMERDOMAIN = "CusDomain.";
    private static Properties assistProperties;
    private AssistAuthTokenAPIManager assistAuthTokenAPIManager;
    private static final Logger LOGGER;
    
    public AssistAuthTokenUtil() {
        this.assistAuthTokenAPIManager = new AssistAuthTokenAPIManager();
    }
    
    public JSONObject getAuthToken(final String email, final String password, final Long cusID) throws Exception {
        AssistAuthTokenUtil.assistProperties = new MDMUtil().getMDMApplicationProperties();
        final String domains = AssistAuthTokenUtil.assistProperties.getProperty("Domains");
        final String accountUrl = AssistAuthTokenUtil.assistProperties.getProperty("AssistAccountUrl");
        final List<String> domainList = Arrays.asList(domains.split(","));
        String tempUrl = null;
        Properties respProperties = null;
        JSONObject responseJSON = new JSONObject();
        responseJSON.put("Status", (Object)"None");
        for (int i = 0; i < domainList.size(); ++i) {
            tempUrl = accountUrl.replaceFirst("com", domainList.get(i));
            respProperties = this.connectToAssist(email, password, tempUrl);
            if (respProperties != null) {
                responseJSON = this.getResponseFromProperties(respProperties);
                responseJSON.put("domain", (Object)domainList.get(i));
                break;
            }
        }
        if (responseJSON.get("Status").equals("None")) {
            responseJSON.put("Status", (Object)"Failure");
            responseJSON.put("Remarks", (Object)this.assistAuthTokenAPIManager.getRemarkForCause("NO_SUCH_USER"));
        }
        return responseJSON;
    }
    
    public Properties connectToAssist(final String email, final String password, final String url) throws MalformedURLException, JSONException, IOException, Exception {
        final String type = "application/x-www-form-urlencoded";
        final String urlString = url + "&EMAIL_ID=" + URLEncoder.encode(email, "UTF-8") + "&PASSWORD=" + URLEncoder.encode(password, "UTF-8");
        final URL accountsUrl = new URL(urlString);
        HttpURLConnection con = null;
        BufferedReader in = null;
        try {
            final Proxy proxy = this.getProxy(accountsUrl.toString());
            con = (HttpURLConnection)accountsUrl.openConnection((proxy == null) ? Proxy.NO_PROXY : proxy);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            final Properties respProperties = new Properties();
            respProperties.load(in);
            final Boolean result = Boolean.parseBoolean(((Hashtable<K, String>)respProperties).get("RESULT"));
            if (result) {
                return respProperties;
            }
            final String cause = ((Hashtable<K, Object>)respProperties).get("CAUSE").toString();
            if (cause.equals("NO_SUCH_USER")) {
                return null;
            }
            return respProperties;
        }
        catch (final IOException e) {
            AssistAuthTokenUtil.LOGGER.log(Level.SEVERE, "IOException occured while trying to connect to assist", e);
            throw e;
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final IOException ex) {
                AssistAuthTokenUtil.LOGGER.log(Level.SEVERE, null, ex);
            }
            if (con != null) {
                con.disconnect();
            }
            AssistAuthTokenUtil.LOGGER.log(Level.INFO, "Logging in Assist account {0} - with url{1}", new Object[] { email, url });
        }
    }
    
    private Proxy getProxy(final String url) throws Exception {
        Proxy proxy = null;
        String proxyHost = null;
        Integer proxyPort = null;
        final Properties proxyDetails = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
        if (proxyDetails != null) {
            final int proxyType = DownloadManager.proxyType;
            if (proxyType == 4) {
                final Properties pacProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration(url, proxyDetails);
                proxyHost = ((Hashtable<K, String>)pacProps).get("proxyHost");
                proxyPort = Integer.valueOf(((Hashtable<K, String>)pacProps).get("proxyPort"));
            }
            else if (proxyType == 2) {
                proxyHost = ((Hashtable<K, String>)proxyDetails).get("proxyHost");
                proxyPort = Integer.valueOf(((Hashtable<K, String>)proxyDetails).get("proxyPort"));
            }
            final String proxyUsername = ((Hashtable<K, String>)proxyDetails).get("proxyUser");
            final String proxyPassword = ((Hashtable<K, String>)proxyDetails).get("proxyPass");
            if (proxyHost != null) {
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
                if (proxyUsername != null) {
                    final Authenticator authenticator = new Authenticator() {
                        public PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(proxyUsername, proxyPassword.toCharArray());
                        }
                    };
                    Authenticator.setDefault(authenticator);
                }
            }
        }
        return proxy;
    }
    
    public JSONObject getResponseFromProperties(final Properties responseProperties) {
        final JSONObject responseJSON = new JSONObject();
        final Boolean result = Boolean.parseBoolean(((Hashtable<K, String>)responseProperties).get("RESULT"));
        try {
            if (result) {
                final String authToken = ((Hashtable<K, String>)responseProperties).get("AUTHTOKEN");
                responseJSON.put("Status", (Object)"Success");
                responseJSON.put("Authtoken", (Object)authToken);
            }
            else {
                responseJSON.put("Status", (Object)"Failure");
                final String cause = ((Hashtable<K, Object>)responseProperties).get("CAUSE").toString();
                responseJSON.put("Remarks", (Object)this.assistAuthTokenAPIManager.getRemarkForCause(cause));
            }
            return responseJSON;
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public String getCustomerDomain(final Long cusID) {
        String domain = null;
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("AssistIntegrationDetails"));
        query.setCriteria(new Criteria(Column.getColumn("AssistIntegrationDetails", "CUSTOMER_ID"), (Object)cusID, 0));
        query.addSelectColumn(Column.getColumn("AssistIntegrationDetails", "CUSTOMER_ID"));
        query.addSelectColumn(Column.getColumn("AssistIntegrationDetails", "CUSTOMER_COUNTRY_CODE"));
        try {
            final DataObject dataObject = DataAccess.get(query);
            if (!dataObject.isEmpty()) {
                final Iterator rows = dataObject.getRows("AssistIntegrationDetails");
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    domain = (String)row.get("CUSTOMER_COUNTRY_CODE");
                }
                return domain;
            }
        }
        catch (final DataAccessException e) {
            e.printStackTrace();
        }
        return domain;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMRemoteControlLogger");
    }
}
