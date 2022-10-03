package com.me.devicemanagement.framework.server.util;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductCodeMapping
{
    public static final String UEM = "Unified Endpoint Management";
    public static final String UES = "Unified Endpoint Security";
    public static final String UEMS = "Unified Endpoint Management and Security";
    private static final HashMap<String, String> PRODUCT_CODE_MAP;
    
    public static ArrayList<String> getProductCode(final String productName) {
        final ArrayList<String> productCodeList = new ArrayList<String>();
        if (ProductCodeMapping.PRODUCT_CODE_MAP != null && ProductCodeMapping.PRODUCT_CODE_MAP.containsKey(productName)) {
            productCodeList.add(ProductCodeMapping.PRODUCT_CODE_MAP.get(productName));
        }
        else {
            if (EMSServiceUtil.isPatchEnabled()) {
                productCodeList.add("PMP");
            }
            if (EMSServiceUtil.isVulnerabilityEnabled()) {
                productCodeList.add("VMP");
            }
            if (EMSServiceUtil.isSOMEnabled() && EMSServiceUtil.isMDMEnabled()) {
                productCodeList.add("DCEE");
            }
            if (EMSServiceUtil.isToolsEnabled()) {
                productCodeList.add("RAP");
            }
        }
        return productCodeList;
    }
    
    public static String getSingleProductCode(final String productName) {
        return ProductCodeMapping.PRODUCT_CODE_MAP.get(productName);
    }
    
    static {
        PRODUCT_CODE_MAP = new HashMap() {
            {
                this.put("ManageEngine Desktop Central", "DCEE");
                this.put("ManageEngine UEMS Summary Server", "DCEE");
                this.put("ManageEngine Desktop Central MSP", "DCMSP");
                this.put("ManageEngine Patch Manager Plus", "PMP");
                this.put("ManageEngine Vulnerability Manager Plus", "VMP");
                this.put("ManageEngine Device Control Plus", "DCP");
                this.put("ManageEngine Application Control Plus", "ACP");
                this.put("ManageEngine Remote Access Plus", "RAP");
                this.put("ManageEngine OS Deployer", "OSD");
                this.put("ManageEngine Unified Endpoint Security", "UES");
            }
        };
    }
}
