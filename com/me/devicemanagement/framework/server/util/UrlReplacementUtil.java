package com.me.devicemanagement.framework.server.util;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.logging.Logger;

public class UrlReplacementUtil
{
    private static Logger logger;
    private static String didValue;
    private static String did;
    
    public static String replaceUrlAndAppendTrackCode(final String url) {
        return replaceUrlAndAppendTrackCode(url, null);
    }
    
    public static String replaceUrlAndAppendTrackCode(final String url, String trackingCode) {
        String resultUrl = url;
        trackingCode = ((trackingCode == null) ? ProductUrlLoader.getInstance().getValue("trackingcode") : trackingCode);
        if (url.contains("traceurl")) {
            resultUrl = url.replace("$(traceurl)", "?" + trackingCode + UrlReplacementUtil.did);
        }
        else if (url.contains("html?")) {
            resultUrl = url.replace("html?", "html?" + trackingCode + UrlReplacementUtil.did);
        }
        else if (url.contains("html")) {
            resultUrl = url.replace("html", "html?" + trackingCode + UrlReplacementUtil.did);
        }
        if (url.contains("mdmUrl")) {
            resultUrl = resultUrl.replaceAll("\\$\\(mdmUrl\\)", ProductUrlLoader.getInstance().getValue("mdmUrl"));
        }
        if (url.contains("dcUrl")) {
            resultUrl = resultUrl.replaceAll("\\$\\(dcUrl\\)", ProductUrlLoader.getInstance().getValue("dcUrl"));
        }
        if (url.contains("prodUrl")) {
            resultUrl = resultUrl.replaceAll("\\$\\(prodUrl\\)", ProductUrlLoader.getInstance().getValue("prodUrl"));
        }
        return resultUrl;
    }
    
    public static String getDefaultRoadMapParams() {
        String urlParams = "";
        try {
            final String prodTrackingCode = ProductUrlLoader.getInstance().getValue("trackingcode");
            final String prodBuildNumber = ProductUrlLoader.getInstance().getValue("buildnumber");
            final boolean isMultiLangPackEnabled = LicenseProvider.getInstance().isLanguagePackEnabled();
            final String licenseEdition = LicenseProvider.getInstance().getProductType();
            final String licenseType = LicenseProvider.getInstance().getLicenseType();
            final String didValue = SyMUtil.getDIDValue();
            urlParams = ((prodTrackingCode != null && prodTrackingCode.trim().length() > 0) ? (urlParams + "&prod=" + prodTrackingCode) : urlParams);
            urlParams = ((prodBuildNumber != null && prodBuildNumber.trim().length() > 0) ? (urlParams + "&buildNo=" + prodBuildNumber) : urlParams);
            urlParams = (isMultiLangPackEnabled ? (urlParams + "&licMLpkg=" + isMultiLangPackEnabled) : urlParams);
            urlParams = ((licenseEdition != null && licenseEdition.trim().length() > 0) ? (urlParams + "&licEdition=" + licenseEdition) : urlParams);
            urlParams = ((licenseType != null && licenseType.trim().length() > 0) ? (urlParams + "&licType=" + licenseType) : urlParams);
            urlParams = ((didValue.trim().length() > 0) ? (urlParams + "&DID=" + didValue) : urlParams);
            final String className = ProductClassLoader.getSingleImplProductClass("URL_CHANGE_LISTENER");
            if (className != null && className.trim().length() > 0) {
                final UrlChangeListener urlChangeListener = (UrlChangeListener)Class.forName(className).newInstance();
                final String productRoadMapUrlParams = urlChangeListener.getRoadMapUrlParams();
                if (productRoadMapUrlParams != null && productRoadMapUrlParams.trim().length() > 0) {
                    urlParams = urlParams + "&" + productRoadMapUrlParams;
                }
            }
        }
        catch (final Exception e) {
            UrlReplacementUtil.logger.log(Level.WARNING, "Exception while concatnating roadmap url params : ", e);
        }
        return urlParams;
    }
    
    static {
        UrlReplacementUtil.logger = Logger.getLogger(UrlReplacementUtil.class.getName());
        UrlReplacementUtil.didValue = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("DID_STRING", 2);
        UrlReplacementUtil.did = ((UrlReplacementUtil.didValue != null) ? ("&did=" + UrlReplacementUtil.didValue) : "&did=");
    }
}
