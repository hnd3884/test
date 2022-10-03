package com.me.ems.framework.common.api.v1.service;

import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.lang.reflect.Method;
import com.me.devicemanagement.framework.server.util.EMSServerUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import org.json.simple.parser.JSONParser;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.List;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.logging.Logger;
import com.me.ems.framework.common.api.utils.AdminCommonUtil;

public class AdminHomePageService
{
    private AdminCommonUtil adminCommonUtil;
    private static Logger logger;
    
    public AdminHomePageService() {
        this.adminCommonUtil = new AdminCommonUtil();
    }
    
    public List getAdminTabComponents(final User user) throws APIException {
        final List<Map<String, Object>> moduleSectionList = new LinkedList<Map<String, Object>>();
        List<String> moduleFiles = null;
        moduleFiles = this.adminCommonUtil.getModuleFiles();
        for (final String file : moduleFiles) {
            final Map<String, Object> sectionDetailsMap = new LinkedHashMap<String, Object>();
            try {
                final JSONParser jsonParser = new JSONParser();
                final JSONObject modulesJSON = FileAccessUtil.secureReadJSON(file);
                if (modulesJSON.containsKey((Object)"sectionDetails")) {
                    final JSONObject sectionDetails = (JSONObject)modulesJSON.get((Object)"sectionDetails");
                    if (this.validateJsonData(sectionDetails, user)) {
                        final List<Map<String, String>> sectionPageList = new LinkedList<Map<String, String>>();
                        if (modulesJSON.containsKey((Object)"sectionPages")) {
                            final JSONArray parse = (JSONArray)jsonParser.parse(modulesJSON.get((Object)"sectionPages").toString());
                            for (final Object jsonInArray : parse) {
                                final Map<String, String> sectionPagesDetailsMap = new LinkedHashMap<String, String>();
                                final JSONObject sectionPageDetails = (JSONObject)jsonInArray;
                                if (this.validateJsonData(sectionPageDetails, user)) {
                                    final String name = (String)sectionPageDetails.get((Object)"name");
                                    final String displayName = (String)sectionPageDetails.get((Object)"displayName");
                                    final String pageIcon = (String)sectionPageDetails.get((Object)"pageIcon");
                                    final String pageURL = (String)sectionPageDetails.get((Object)"pageURL");
                                    final String pageRoute = (String)sectionPageDetails.get((Object)"route");
                                    final String isNew = (String)sectionPageDetails.get((Object)"isNew");
                                    if (name == null || name.isEmpty() || displayName == null || displayName.isEmpty() || pageIcon == null || pageIcon.isEmpty()) {
                                        continue;
                                    }
                                    sectionPagesDetailsMap.put("name", name);
                                    sectionPagesDetailsMap.put("displayName", I18N.getMsg(displayName, new Object[0]));
                                    sectionPagesDetailsMap.put("icon", pageIcon);
                                    if (pageRoute != null && !pageRoute.isEmpty()) {
                                        sectionPagesDetailsMap.put("route", pageRoute);
                                        sectionPageList.add(sectionPagesDetailsMap);
                                    }
                                    else if (pageURL != null && !pageURL.isEmpty()) {
                                        sectionPagesDetailsMap.put("url", pageURL);
                                        sectionPageList.add(sectionPagesDetailsMap);
                                    }
                                    if (isNew == null || !isNew.equalsIgnoreCase("true")) {
                                        continue;
                                    }
                                    sectionPagesDetailsMap.put("isNew", "true");
                                }
                            }
                        }
                        if (!sectionPageList.isEmpty()) {
                            final String sectionName = (String)sectionDetails.get((Object)"name");
                            final String displayName2 = (String)sectionDetails.get((Object)"displayName");
                            final String sectionIcon = (String)sectionDetails.get((Object)"sectionIcon");
                            final String isNew2 = (String)sectionDetails.get((Object)"isNew");
                            if (sectionName != null && !sectionName.isEmpty() && displayName2 != null && !displayName2.isEmpty() && sectionIcon != null && !sectionIcon.isEmpty()) {
                                sectionDetailsMap.put("name", sectionName);
                                sectionDetailsMap.put("displayName", I18N.getMsg(displayName2, new Object[0]));
                                sectionDetailsMap.put("icon", sectionIcon);
                                if (isNew2 != null && isNew2.equalsIgnoreCase("true")) {
                                    sectionDetailsMap.put("isNew", "true");
                                }
                                sectionDetailsMap.put("pages", sectionPageList);
                            }
                        }
                    }
                }
            }
            catch (final Exception ex) {
                AdminHomePageService.logger.log(Level.SEVERE, "Exception caught in AdminHomePageService", ex);
            }
            if (!sectionDetailsMap.isEmpty()) {
                moduleSectionList.add(sectionDetailsMap);
            }
        }
        return moduleSectionList;
    }
    
    private boolean validateJsonData(final JSONObject sectionDetails, final User user) {
        final String productCode = ProductUrlLoader.getInstance().getValue("productcode");
        final String productType = LicenseProvider.getInstance().getProductType();
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        final String licenseVersion = LicenseProvider.getInstance().getLicenseVersion();
        return sectionDetails != null && !sectionDetails.isEmpty() && this.isSasCheck((String)sectionDetails.get((Object)"forSAS")) && this.isMspCheck((String)sectionDetails.get((Object)"forMSP")) && this.roleCheck((String)sectionDetails.get((Object)"roles"), (String)sectionDetails.get((Object)"multiRoleCheck"), user) && this.validateData((String)sectionDetails.get((Object)"productCode"), (String)sectionDetails.get((Object)"isProductCodeNegation"), productCode) && this.validateData((String)sectionDetails.get((Object)"productType"), (String)sectionDetails.get((Object)"isProductTypeNegation"), productType) && this.validateData((String)sectionDetails.get((Object)"licenseType"), (String)sectionDetails.get((Object)"isLicenseTypeNegation"), licenseType) && this.validateData((String)sectionDetails.get((Object)"licenseVersion"), (String)sectionDetails.get((Object)"isLicenseVersionNegation"), licenseVersion) && EMSServerUtil.isMatchingServerType((String)sectionDetails.get((Object)"emsServerType")) && this.customCheck((String)sectionDetails.get((Object)"customCheckClass"), (String)sectionDetails.get((Object)"customCheckMethod"));
    }
    
    private boolean customCheck(final String customCheckClass, final String customCheckMethod) {
        if (customCheckClass != null && !customCheckClass.isEmpty() && customCheckMethod != null && !customCheckMethod.isEmpty()) {
            try {
                final Object classObject = Class.forName(customCheckClass).newInstance();
                if (classObject != null) {
                    final Method method = classObject.getClass().getDeclaredMethod(customCheckMethod, (Class<?>[])new Class[0]);
                    return (boolean)method.invoke(classObject, new Object[0]);
                }
            }
            catch (final Exception ex) {
                AdminHomePageService.logger.log(Level.SEVERE, "Exception caught in customCheck", ex);
                return false;
            }
        }
        return true;
    }
    
    private boolean validateData(final String key, final String keyNegate, final String valueComparator) {
        boolean isValid = false;
        if (key != null && !key.isEmpty()) {
            final String[] split;
            final String[] dataArray = split = key.split(",");
            for (final String data : split) {
                if (valueComparator != null && valueComparator.equalsIgnoreCase(data)) {
                    isValid = true;
                    break;
                }
            }
            if (keyNegate != null && keyNegate.equalsIgnoreCase("true")) {
                isValid = !isValid;
            }
        }
        else {
            isValid = true;
        }
        return isValid;
    }
    
    private boolean roleCheck(final String roles, final String multiRoleCheck, final User user) {
        boolean isValid = false;
        if (roles != null && !roles.isEmpty()) {
            final String[] split;
            final String[] rolesArray = split = roles.split(",");
            for (final String role : split) {
                if (multiRoleCheck != null && multiRoleCheck.equalsIgnoreCase("true")) {
                    if (!user.isUserInRole(role)) {
                        isValid = false;
                        break;
                    }
                    isValid = true;
                }
                else {
                    if (user.isUserInRole(role)) {
                        isValid = true;
                        break;
                    }
                    isValid = false;
                }
            }
        }
        else {
            isValid = true;
        }
        return isValid;
    }
    
    private boolean isSasCheck(final String forSAS) {
        if (forSAS == null || forSAS.isEmpty()) {
            return true;
        }
        CustomerInfoUtil.getInstance();
        if (CustomerInfoUtil.isSAS()) {
            return forSAS.equalsIgnoreCase("true");
        }
        return !forSAS.equalsIgnoreCase("true");
    }
    
    private boolean isMspCheck(final String forMSP) {
        if (forMSP == null || forMSP.isEmpty()) {
            return true;
        }
        if (CustomerInfoUtil.getInstance().isMSP()) {
            return forMSP.equalsIgnoreCase("true");
        }
        return !forMSP.equalsIgnoreCase("true");
    }
    
    static {
        AdminHomePageService.logger = Logger.getLogger(AdminHomePageService.class.getName());
    }
}
