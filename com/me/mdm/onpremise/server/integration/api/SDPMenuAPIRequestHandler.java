package com.me.mdm.onpremise.server.integration.api;

import java.util.List;
import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import javax.xml.transform.Transformer;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import com.me.devicemanagement.framework.server.license.MDMLicenseAPI;
import com.adventnet.i18n.I18N;
import java.util.Map;
import org.json.JSONArray;
import org.json.XML;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilderFactory;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.util.SoMADUtil;
import com.me.mdm.api.APIUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class SDPMenuAPIRequestHandler extends ApiRequestHandler
{
    private Logger logger;
    
    public SDPMenuAPIRequestHandler() {
        this.logger = Logger.getLogger(SDPMenuAPIRequestHandler.class.getSimpleName());
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)this.fetchModuleUrl(apiRequest.toJSONObject()));
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "exception occurred in SDPMenuAPIRequestHandler", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject fetchModuleUrl(final JSONObject requestJSON) {
        JSONObject urlData = new JSONObject();
        final JSONObject responseData = new JSONObject();
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        final String dcMenuXml = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "conf" + File.separator + "MdmOnpremise" + File.separator + "mdm_url_link.xml";
        final File file = new File(dcMenuXml);
        try {
            final String loginUserName = APIUtil.optStringFilter(requestJSON, "mdm_user_name", APIUtil.getUserName(requestJSON));
            final String adDomainName = APIUtil.optStringFilter(requestJSON, "mdm_domain_name", "-");
            final String domainName = SoMADUtil.getInstance().getManagedDomain(adDomainName);
            Long technicianID = DMUserHandler.getLoginIdForUser(loginUserName, domainName);
            if (technicianID == null) {
                technicianID = DMUserHandler.getLoginIdForUser(loginUserName);
            }
            final Map<String, Long> authorizedRoleMap = DMUserHandler.getAuthorizedRolesForAccId(technicianID);
            final String userRole = DMUserHandler.getRoleForUser(loginUserName);
            final MDMLicenseAPI licenseAPI = LicenseProvider.getInstance().getMDMLicenseAPI();
            if (file.exists()) {
                final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
                factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                final DocumentBuilder builder = factory.newDocumentBuilder();
                final Document doc = builder.parse(dcMenuXml);
                final Node dcMenuNode = doc.getDocumentElement();
                String xmlString = null;
                try {
                    final StringWriter writer = new StringWriter();
                    final StreamResult result = new StreamResult(writer);
                    final Transformer xformer = TransformerFactory.newInstance().newTransformer();
                    final DOMSource source = new DOMSource(dcMenuNode);
                    xformer.setOutputProperty("indent", "yes");
                    xformer.transform(source, result);
                    xmlString = writer.toString();
                    urlData = XML.toJSONObject(xmlString);
                    final JSONArray menus = urlData.getJSONObject("MDMPMenus").getJSONArray("MDMPMenuItems");
                    final String currentLicense = licenseAPI.getMDMLiceseEditionType();
                    final JSONArray menuResult = new JSONArray();
                    for (int i = 0; i < menus.length(); ++i) {
                        final JSONObject menu = menus.getJSONObject(i);
                        final String menulicense = String.valueOf(menu.get("license"));
                        String menuRole = String.valueOf(menu.get("role"));
                        boolean roleAvailable = this.checkRoleAvailable(menuRole, authorizedRoleMap);
                        if (loginUserName.equalsIgnoreCase("administrator") || roleAvailable) {
                            if (menu.has("SUBMenuItems")) {
                                final JSONArray subMenuItems = menu.getJSONArray("SUBMenuItems");
                                int removeElement = 0;
                                for (int k = subMenuItems.length(), j = 0; j < k; ++j) {
                                    final JSONObject subMenu = subMenuItems.getJSONObject(removeElement);
                                    final String featureLicense = String.valueOf(subMenu.get("license"));
                                    menuRole = String.valueOf(subMenu.get("role"));
                                    roleAvailable = this.checkRoleAvailable(menuRole, authorizedRoleMap);
                                    if (loginUserName.equalsIgnoreCase("administrator")) {
                                        if (!featureLicense.contains(currentLicense)) {
                                            subMenuItems.remove(removeElement);
                                        }
                                        else {
                                            ++removeElement;
                                        }
                                    }
                                    else if (!featureLicense.contains(currentLicense) || !roleAvailable) {
                                        subMenuItems.remove(removeElement);
                                    }
                                    else {
                                        ++removeElement;
                                    }
                                }
                                final JSONArray subMenuItem = new JSONArray();
                                for (int subRole = 0; subRole < subMenuItems.length(); ++subRole) {
                                    final JSONObject subMenu2 = subMenuItems.getJSONObject(subRole);
                                    if (isMsp && subMenu2.has("mspdisplayname")) {
                                        subMenu2.put("displayname", (Object)I18N.getMsg(String.valueOf(subMenu2.get("mspdisplayname")), new Object[0]));
                                    }
                                    else {
                                        subMenu2.put("displayname", (Object)I18N.getMsg(String.valueOf(subMenu2.get("displayname")), new Object[0]));
                                    }
                                    final JSONArray featureMenuItem = new JSONArray();
                                    if (subMenu2.has("FeatureItems")) {
                                        final JSONArray featureItems = subMenu2.getJSONArray("FeatureItems");
                                        for (int featureRole = 0; featureRole < featureItems.length(); ++featureRole) {
                                            final JSONObject featureMenu = featureItems.getJSONObject(featureRole);
                                            if (isMsp && featureMenu.has("mspdisplayname")) {
                                                featureMenu.put("displayname", (Object)I18N.getMsg(String.valueOf(featureMenu.get("mspdisplayname")), new Object[0]));
                                            }
                                            else {
                                                featureMenu.put("displayname", (Object)I18N.getMsg(String.valueOf(featureMenu.get("displayname")), new Object[0]));
                                            }
                                            featureMenuItem.put((Object)featureMenu);
                                        }
                                        subMenu2.put("FeatureItems", (Object)featureMenuItem);
                                    }
                                    subMenuItem.put((Object)subMenu2);
                                }
                                if (menulicense.contains(currentLicense)) {
                                    final JSONObject menuValue = new JSONObject();
                                    menuValue.put("name", (Object)String.valueOf(menu.get("name")));
                                    menuValue.put("license", (Object)String.valueOf(menu.get("license")));
                                    if (isMsp && menu.has("mspdisplayname")) {
                                        menuValue.put("displayname", (Object)I18N.getMsg(String.valueOf(menu.get("displayname")), new Object[0]));
                                    }
                                    else {
                                        menuValue.put("displayname", (Object)I18N.getMsg(String.valueOf(menu.get("displayname")), new Object[0]));
                                    }
                                    menuValue.put("url", (Object)String.valueOf(menu.get("url")));
                                    menuValue.put("role", (Object)String.valueOf(menu.get("role")));
                                    menuValue.put("SUBMenuItems", (Object)subMenuItems);
                                    menuResult.put((Object)menuValue);
                                }
                            }
                            else if (menulicense.contains(currentLicense)) {
                                if (isMsp && menu.has("mspdisplayname")) {
                                    menu.put("displayname", (Object)I18N.getMsg(String.valueOf(menu.get("mspdisplayname")), new Object[0]));
                                }
                                else {
                                    menu.put("displayname", (Object)I18N.getMsg(String.valueOf(menu.get("displayname")), new Object[0]));
                                }
                                menuResult.put((Object)menu);
                            }
                        }
                        responseData.put("MDMPMenuItems", (Object)menuResult);
                    }
                }
                catch (final Exception ex) {
                    this.logger.log(Level.SEVERE, "Exception while parsing the MDMMenu xml", ex);
                    throw new APIHTTPException("COM0004", new Object[0]);
                }
                return responseData;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting the MDMMenu list", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return urlData;
    }
    
    public boolean checkRoleAvailable(final String role, final Map authorizedRoleMap) {
        final String[] roles = role.trim().split(",");
        final List userRoleList = new ArrayList(authorizedRoleMap.keySet());
        userRoleList.retainAll(Arrays.asList(roles));
        return userRoleList.size() > 0;
    }
}
