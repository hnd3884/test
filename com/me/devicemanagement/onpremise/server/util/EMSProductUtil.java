package com.me.devicemanagement.onpremise.server.util;

import com.me.devicemanagement.framework.server.util.ProductCodeMapping;
import com.me.devicemanagement.framework.server.util.EMSServiceUtil;
import com.me.devicemanagement.onpremise.properties.util.GeneralPropertiesLoader;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.ArrayList;

public class EMSProductUtil
{
    public static ArrayList currentproductList;
    
    public static ArrayList getEMSProductCode() {
        if (EMSProductUtil.currentproductList != null && !EMSProductUtil.currentproductList.isEmpty()) {
            return EMSProductUtil.currentproductList;
        }
        final String version = LicenseProvider.getInstance().getEMSLicenseVersion();
        if (!version.equalsIgnoreCase("11")) {
            EMSProductUtil.currentproductList = getEMSProductCodeFromGeneralProperties();
        }
        else {
            final String productList = SyMUtil.getProductProperty("activeproductcodes");
            if (productList != null) {
                final String[] str = productList.split(",");
                (EMSProductUtil.currentproductList = new ArrayList()).add(str[0]);
            }
            else {
                EMSProductUtil.currentproductList = getEMSProductCodeFromMap();
            }
        }
        return EMSProductUtil.currentproductList;
    }
    
    private static ArrayList getEMSProductCodeFromGeneralProperties() {
        final ArrayList productList = new ArrayList();
        final String code = GeneralPropertiesLoader.getInstance().getProperties().getProperty("productcode");
        productList.add(code);
        return productList;
    }
    
    public static ArrayList getEMSProductCodeFromMap() {
        String productName = LicenseProvider.getInstance().getProductName();
        if (LicenseProvider.getInstance().getLicenseVersion().equals("Vulnerability") || EMSServiceUtil.isVulnEnabledInOldLicenses()) {
            productName = "ManageEngine Vulnerability Manager Plus";
        }
        if (SyMUtil.isSummaryServer() || SyMUtil.isProbeServer()) {
            productName = "ManageEngine UEMS Summary Server";
        }
        final ArrayList productList = ProductCodeMapping.getProductCode(productName);
        return productList;
    }
    
    public static void regenerateProductCode() {
        removeEMSProductCode();
        getEMSProductCode();
    }
    
    private static void removeEMSProductCode() {
        EMSProductUtil.currentproductList = new ArrayList();
    }
    
    static {
        EMSProductUtil.currentproductList = new ArrayList();
    }
}
