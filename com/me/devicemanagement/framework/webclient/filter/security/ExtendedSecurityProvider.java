package com.me.devicemanagement.framework.webclient.filter.security;

import org.json.simple.JSONArray;
import java.util.Iterator;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.iam.security.ParameterRule;
import com.adventnet.iam.security.IAMSecurityException;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import com.adventnet.iam.security.SecurityRequestWrapper;
import com.adventnet.iam.security.UploadFileRule;
import com.adventnet.iam.security.UploadedFileItem;
import com.adventnet.iam.security.ActionRule;
import javax.servlet.http.HttpServletRequest;
import org.json.simple.JSONObject;
import com.adventnet.iam.security.DefaultSecurityProvider;

public class ExtendedSecurityProvider extends DefaultSecurityProvider
{
    private static JSONObject moduleConfigurations;
    
    public UploadFileRule getDynamicFileRule(final HttpServletRequest request, final ActionRule actionRule, final UploadedFileItem fileItem) {
        final SecurityRequestWrapper securedRequest = (SecurityRequestWrapper)request;
        final String moduleName = securedRequest.getHeader("Module");
        final String requestURI = SecurityUtil.getNormalizedRequestURI(request);
        if (!requestURI.contains("/dcapi/files")) {
            if (!requestURI.contains("/emsapi/files")) {
                return null;
            }
        }
        try {
            if (moduleName == null || moduleName.isEmpty()) {
                throw new IAMSecurityException("MODULE_HEADER_NOT_AVAILABLE");
            }
            final JSONObject configuration = this.getConfigurations(moduleName);
            final ParameterRule fileNameParam = new ParameterRule("file", "uploadedFileName", 1, 230);
            final UploadFileRule uploadFileRule = new UploadFileRule("fileName", (long)Integer.parseInt(configuration.get((Object)"maxFileSizeInKB").toString()));
            uploadFileRule.setFileNameRule(fileNameParam);
            uploadFileRule.setAllowEmptyFile(false);
            actionRule.setRoles(configuration.get((Object)"roles").toString().split(","));
            final String allowedContentTypeRuleName = (String)configuration.get((Object)"allowedContentTypeRuleName");
            if (allowedContentTypeRuleName != null && !allowedContentTypeRuleName.isEmpty()) {
                uploadFileRule.setAllowedContentTypeName(allowedContentTypeRuleName);
            }
            final String allowedExtensions = (String)configuration.get((Object)"allowedExtensions");
            final String disAllowedExtensions = (String)configuration.get((Object)"disAllowedExtensions");
            if (allowedExtensions != null && !allowedExtensions.isEmpty()) {
                uploadFileRule.setAllowedExtensions(allowedExtensions.split(","));
            }
            else if (disAllowedExtensions != null && !disAllowedExtensions.isEmpty()) {
                uploadFileRule.setDisAllowedExtensions(disAllowedExtensions.split(","));
            }
            final String zipSanitizer = (String)configuration.get((Object)"zipSanitizer");
            if (zipSanitizer != null) {
                ApiFactoryProvider.getZipUtilAPI().setZipSantizerName(uploadFileRule, zipSanitizer);
            }
            return uploadFileRule;
        }
        catch (final IAMSecurityException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            Logger.getLogger(ExtendedSecurityProvider.class.getName()).log(Level.SEVERE, "### Inside ExtendedSecurityProvider ### ", ex2);
            throw new IAMSecurityException("INVALID_CONFIGURATION");
        }
        return null;
    }
    
    public void authorize(final HttpServletRequest request, final HttpServletResponse response, final ActionRule actionRule) {
        boolean authorisedUser = false;
        final String[] configuredRoles = actionRule.getRoles();
        if (configuredRoles != null) {
            try {
                for (final String role : configuredRoles) {
                    if (authorisedUser) {
                        break;
                    }
                    authorisedUser = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains(role.trim());
                }
            }
            catch (final Exception e) {
                Logger.getLogger(ExtendedSecurityProvider.class.getName()).log(Level.SEVERE, "Exception while getting roles : {0}", e);
            }
        }
        else {
            authorisedUser = true;
        }
        if (!authorisedUser) {
            throw new IAMSecurityException("UNAUTHORISED");
        }
        final String configuredEdition = actionRule.getCustomAttribute("edition");
        if (configuredEdition != null) {
            String plan = "";
            if (CustomerInfoUtil.isVMPProduct()) {
                plan = LicenseProvider.getInstance().getProductCategoryString();
            }
            else {
                plan = LicenseProvider.getInstance().getLicenseVersion();
            }
            if (!configuredEdition.contains(plan)) {
                throw new IAMSecurityException("DC_EDITION_INVALID");
            }
        }
        final String ems_server_type = actionRule.getCustomAttribute("ems_server_type");
        if (ems_server_type != null) {
            boolean allow = false;
            final boolean isNegated = ems_server_type.contains("!");
            if (ems_server_type.contains("PROBE")) {
                allow = ((!isNegated && SyMUtil.isProbeServer()) || (isNegated && !SyMUtil.isProbeServer()));
            }
            else if (ems_server_type.contains("SUMMARY")) {
                allow = ((!isNegated && SyMUtil.isSummaryServer()) || (isNegated && !SyMUtil.isSummaryServer()));
            }
            else if (ems_server_type.contains("STANDALONE")) {
                allow = ((!isNegated && !SyMUtil.isSummaryServer() && !SyMUtil.isProbeServer()) || (isNegated && (SyMUtil.isSummaryServer() || SyMUtil.isProbeServer())));
            }
            if (!allow) {
                throw new IAMSecurityException("UNAUTHORISED");
            }
        }
        final String ss_extended_roles = actionRule.getCustomAttribute("ss_extended_roles");
        boolean rolecheck = false;
        if (ss_extended_roles != null && SyMUtil.isSummaryServer()) {
            try {
                rolecheck = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains(ss_extended_roles.trim());
            }
            catch (final Exception e2) {
                Logger.getLogger(ExtendedSecurityProvider.class.getName()).log(Level.SEVERE, "Exception while getting roles : {0}", e2);
            }
            if (!rolecheck) {
                throw new IAMSecurityException("UNAUTHORISED");
            }
        }
    }
    
    private JSONObject getConfigurations(final String moduleName) {
        if (ExtendedSecurityProvider.moduleConfigurations == null || ExtendedSecurityProvider.moduleConfigurations.isEmpty()) {
            Logger.getLogger(ExtendedSecurityProvider.class.getName()).log(Level.SEVERE, "### Inside ExtendedSecurityProvider -> getConfigurations ### NO MODULE CONFIGURATIONS FOUND IN JSON OBJECT, EITHER NO CONFIGURATIONS OR CHECKSUM FAILED ");
        }
        try {
            if (ExtendedSecurityProvider.moduleConfigurations.containsKey((Object)moduleName)) {
                return (JSONObject)ExtendedSecurityProvider.moduleConfigurations.get((Object)moduleName);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(ExtendedSecurityProvider.class.getName()).log(Level.SEVERE, "### Inside ExtendedSecurityProvider -> getConfigurations ### ", ex);
        }
        throw new IAMSecurityException("INVALID_CONFIGURATION");
    }
    
    static {
        ExtendedSecurityProvider.moduleConfigurations = new JSONObject();
        Logger.getLogger(ExtendedSecurityProvider.class.getName()).log(Level.INFO, "### Inside ExtendedSecurityProvider -> static block  ### ");
        try {
            final String outFileName = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "security" + File.separator + "filesapiconfig";
            final ArrayList filesListInFolder = FileAccessUtil.getFilesListInFolder(outFileName);
            for (final Object fileName : filesListInFolder) {
                final String fileNameStr = fileName.toString();
                if (fileNameStr.contains("FileUploadAPIConfiguration")) {
                    final JSONArray strings = FileAccessUtil.secureReadJSONArray(fileNameStr);
                    for (final Object obj : strings) {
                        final JSONObject module = (JSONObject)obj;
                        ExtendedSecurityProvider.moduleConfigurations.put(module.get((Object)"moduleName"), (Object)module);
                    }
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger(ExtendedSecurityProvider.class.getName()).log(Level.SEVERE, "### Inside ExtendedSecurityProvider -> static block  ### ", e);
        }
    }
}
