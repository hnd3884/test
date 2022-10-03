package com.me.ems.onpremise.summaryserver.probe.util;

import java.util.Hashtable;
import org.json.Property;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.UUID;
import java.net.HttpURLConnection;
import java.util.Iterator;
import javax.net.ssl.SSLHandshakeException;
import java.net.ConnectException;
import java.util.logging.Level;
import com.me.ems.onpremise.summaryserver.common.probeadministration.ProbeDetailsUtil;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.me.ems.onpremise.summaryserver.common.HttpsHandlerUtil;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.net.InetAddress;
import java.util.HashMap;
import java.net.URL;
import java.io.File;
import org.json.JSONObject;
import java.util.Properties;
import java.util.Map;
import java.util.logging.Logger;

public class ProbeInstallUtil
{
    public static final Logger LOGGER;
    private static final String serverHome;
    
    public static JSONObject installProbe(final Map probeDetail, final Map summaryDetail, final Properties proxyProps, final boolean isRetry) {
        String response = "";
        final JSONObject responseMap = new JSONObject();
        JSONObject defaultUserAndRoleIds = new JSONObject();
        String baseURL = "";
        String error = "";
        try {
            final File file = new File(ProbeInstallUtil.serverHome);
            final String serverName = summaryDetail.get("serverName");
            final String portNumber = summaryDetail.get("portNumber");
            final String protocol = summaryDetail.get("protocol");
            final String probeApiKey = generateAPIKey();
            final Long currentTime = System.currentTimeMillis();
            baseURL = protocol + "://" + serverName + ":" + portNumber + "/registerProbe";
            final URL urlObj = new URL(baseURL);
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("probeHost", InetAddress.getLocalHost().getHostName());
            if (probeDetail.get("port") != null) {
                params.put("probePort", probeDetail.get("port"));
            }
            if (probeDetail.get("protocol") != null) {
                params.put("probeProtocol", probeDetail.get("protocol"));
            }
            params.put("probeAuthKey", probeApiKey);
            params.put("timeZone", SyMUtil.getDefaultTimeZoneID());
            params.put("version", SyMUtil.getProductProperty("buildnumber"));
            params.put("installationKey", probeDetail.get("installationKey"));
            params.put("ipAddress", InetAddress.getLocalHost().getHostAddress());
            params.put("totalSpace", file.getTotalSpace() / 1073741824L);
            params.put("freeSpace", file.getFreeSpace() / 1073741824L);
            params.put("apiKeyGeneratedOn", currentTime);
            final StringBuilder postData = new StringBuilder();
            for (final Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) {
                    postData.append('&');
                }
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            final byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);
            HttpURLConnection conn = null;
            String proxyDefined = "false";
            if (!proxyProps.isEmpty()) {
                proxyDefined = "true";
            }
            conn = HttpsHandlerUtil.getProxyAppliedConnection(urlObj, proxyDefined, proxyProps);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            if (protocol.equalsIgnoreCase("https")) {
                conn = HttpsHandlerUtil.skipCertificateCheck(conn);
            }
            conn.getOutputStream().write(postDataBytes);
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            response = rd.readLine();
            final JSONObject jsonObject = new JSONObject(response);
            final HashMap probeApi = new HashMap();
            final HashMap summaryInfo = new HashMap();
            if (!jsonObject.has("errorMessage")) {
                if (jsonObject.has("probeAuthKey")) {
                    probeApi.put("probeAuthKey", jsonObject.get("probeAuthKey"));
                }
                if (jsonObject.has("summaryServerAuthKey")) {
                    probeApi.put("summaryServerAuthKey", jsonObject.get("summaryServerAuthKey"));
                }
                if (jsonObject.has("probeId")) {
                    probeApi.put("probeId", jsonObject.get("probeId"));
                }
                if (jsonObject.has("probeName")) {
                    probeApi.put("probeName", jsonObject.get("probeName"));
                }
                if (jsonObject.has("probeDesc")) {
                    probeApi.put("probeDesc", jsonObject.get("probeDesc"));
                }
                if (jsonObject.has("summaryServerIp")) {
                    summaryInfo.put("summaryServerIp", jsonObject.get("summaryServerIp"));
                }
                if (jsonObject.has("timeZone")) {
                    summaryInfo.put("timeZone", jsonObject.get("timeZone"));
                }
                if (jsonObject.has("version")) {
                    summaryInfo.put("version", jsonObject.get("version"));
                }
                if (jsonObject.has("apiKeyGeneratedBy")) {
                    probeApi.put("apiKeyGeneratedBy", jsonObject.get("apiKeyGeneratedBy"));
                }
                if (jsonObject.has("Associated_SummaryServer_ID")) {
                    summaryInfo.put("Associated_SummaryServer_ID", jsonObject.get("Associated_SummaryServer_ID"));
                }
                probeApi.put("apiKeyGeneratedOn", currentTime);
                summaryInfo.put("port", portNumber);
                summaryInfo.put("host", serverName);
                summaryInfo.put("protocol", protocol);
                storeAuthKeyInFile(probeApi, summaryInfo);
                setAutoGenValueRange(Long.parseLong(String.valueOf(jsonObject.get("uvhRange"))));
                if (proxyDefined.equalsIgnoreCase("true")) {
                    ProbeDetailsUtil.storeProxyDetails(proxyProps);
                }
                if (jsonObject.has("defaultUserAndRoleIds")) {
                    defaultUserAndRoleIds = jsonObject.getJSONObject("defaultUserAndRoleIds");
                    storeDefaultIdsInFile(defaultUserAndRoleIds);
                }
                responseMap.put("success", (Object)"true");
                return responseMap;
            }
            error = (String)jsonObject.get("errorMessage");
            ProbeInstallUtil.LOGGER.log(Level.SEVERE, "Error due to " + error);
            responseMap.put("errorMsg", (Object)error);
            rd.close();
        }
        catch (final ConnectException e) {
            responseMap.put("errorMsg", (Object)"Connect Exception");
            ProbeInstallUtil.LOGGER.log(Level.SEVERE, "Connect Exception occurred while registering probe!!!", e);
        }
        catch (final SSLHandshakeException e2) {
            responseMap.put("errorMsg", (Object)"Connect Exception");
            ProbeInstallUtil.LOGGER.log(Level.SEVERE, "SSL Exception ocurred while registering probe!!!", e2);
        }
        catch (final Exception e3) {
            if (e3.getMessage().contains("Proxy")) {
                responseMap.put("errorMsg", (Object)"Proxy Exception");
            }
            else {
                responseMap.put("errorMsg", (Object)"Exception");
            }
            ProbeInstallUtil.LOGGER.log(Level.SEVERE, "Exception occurred while registering probe!!!", e3);
        }
        responseMap.put("success", (Object)"false");
        return responseMap;
    }
    
    public static String generateAPIKey() {
        return UUID.randomUUID().toString().toUpperCase();
    }
    
    private static void storeAuthKeyInFile(final HashMap probeApi, final HashMap summaryInfo) {
        try {
            final Properties probeAuthProps = new Properties();
            ((Hashtable<String, String>)probeAuthProps).put("probeId", String.valueOf(probeApi.get("probeId")));
            ((Hashtable<String, Object>)probeAuthProps).put("probeAuthKey", probeApi.get("probeAuthKey"));
            ((Hashtable<String, Object>)probeAuthProps).put("summaryServerAuthKey", probeApi.get("summaryServerAuthKey"));
            ((Hashtable<String, Object>)probeAuthProps).put("probeName", probeApi.get("probeName"));
            ((Hashtable<String, Object>)probeAuthProps).put("probeDesc", probeApi.get("probeDesc"));
            ((Hashtable<String, Object>)probeAuthProps).put("summaryServerIp", summaryInfo.get("summaryServerIp"));
            ((Hashtable<String, Object>)probeAuthProps).put("summaryServerTimeZone", summaryInfo.get("timeZone"));
            ((Hashtable<String, Object>)probeAuthProps).put("summaryServerVersion", summaryInfo.get("version"));
            ((Hashtable<String, String>)probeAuthProps).put("summaryServerPort", summaryInfo.get("port") + "");
            ((Hashtable<String, Object>)probeAuthProps).put("summaryServerHost", summaryInfo.get("host"));
            ((Hashtable<String, Object>)probeAuthProps).put("summaryServerProtocol", summaryInfo.get("protocol"));
            ((Hashtable<String, String>)probeAuthProps).put("apiKeyGeneratedBy", probeApi.get("apiKeyGeneratedBy") + "");
            ((Hashtable<String, String>)probeAuthProps).put("apiKeyGeneratedOn", probeApi.get("apiKeyGeneratedOn") + "");
            final Properties meTrackProp = new Properties();
            ((Hashtable<String, Object>)meTrackProp).put("Associated_SummaryServer_ID", summaryInfo.get("Associated_SummaryServer_ID"));
            final String serverPropFile = ProbeInstallUtil.serverHome + File.separator + "conf" + File.separator + "server_properties.conf";
            final Path creatorPath = Paths.get(serverPropFile, new String[0]);
            if (!Files.exists(creatorPath, new LinkOption[0])) {
                Files.createFile(creatorPath, (FileAttribute<?>[])new FileAttribute[0]);
            }
            FileAccessUtil.storeProperties(meTrackProp, serverPropFile, true);
            final String probeAuthConf = ProbeInstallUtil.serverHome + File.separator + "conf" + File.separator + "probeDetailsTemp.conf";
            final Path confPath = Paths.get(probeAuthConf, new String[0]);
            if (!Files.exists(confPath, new LinkOption[0])) {
                Files.createFile(confPath, (FileAttribute<?>[])new FileAttribute[0]);
            }
            ProbeInstallUtil.LOGGER.log(Level.INFO, " after store key here here");
            FileAccessUtil.storeProperties(probeAuthProps, probeAuthConf, false);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void setAutoGenValueRange(final Long range) {
        final File fXmlFile = new File(ProbeInstallUtil.serverHome + File.separator + "conf" + File.separator + "customer-config.xml");
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            final Document doc = dBuilder.parse(fXmlFile);
            final Element rootElement = (Element)doc.getElementsByTagName("extended-configurations").item(0);
            final NodeList childNodes = rootElement.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); ++i) {
                final Node node = childNodes.item(i);
                if (node.getNodeType() == 1) {
                    final Element eElement = (Element)node;
                    if (eElement.getAttribute("name").equalsIgnoreCase("SeqGenStartValue")) {
                        eElement.setAttribute("value", String.valueOf(range));
                        writeToXML(fXmlFile, rootElement);
                        return;
                    }
                }
            }
            final Element element = doc.createElement("configuration");
            element.setAttribute("name", "SeqGenStartValue");
            element.setAttribute("value", String.valueOf(range));
            rootElement.appendChild(element);
            writeToXML(fXmlFile, rootElement);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void writeToXML(final File file, final Element root) throws Exception {
        final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        final String encoding = "UTF-8";
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        final DOMSource source = new DOMSource(root);
        final StreamResult result = new StreamResult(writer);
        try {
            final Properties prop = new Properties();
            ((Hashtable<String, String>)prop).put("indent", "yes");
            ((Hashtable<String, String>)prop).put("encoding", encoding);
            ((Hashtable<String, String>)prop).put("method", "xml");
            transformer.setOutputProperties(prop);
            transformer.transform(source, result);
        }
        catch (final Exception e) {
            ProbeInstallUtil.LOGGER.severe("Error while writing xml  " + e);
        }
        finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
    
    private static void storeDefaultIdsInFile(final JSONObject defaultUserAndRoleIds) {
        final String userIdFilePath = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "conf" + File.separator + "Authentication" + File.separator + "UserData.properties";
        final String roleIdFilePath = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "conf" + File.separator + "Authentication" + File.separator + "RoleData.properties";
        final Properties userProperties = Property.toProperties(defaultUserAndRoleIds.getJSONObject("users"));
        final Properties roleProperties = Property.toProperties(defaultUserAndRoleIds.getJSONObject("roles"));
        try {
            FileAccessUtil.storeProperties(userProperties, userIdFilePath, false);
            FileAccessUtil.storeProperties(roleProperties, roleIdFilePath, false);
        }
        catch (final Exception e) {
            ProbeInstallUtil.LOGGER.log(Level.SEVERE, "Exception Occurred while writing SS UVH data of Users and Roles to File", e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("probeActionsLogger");
        serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
    }
}
