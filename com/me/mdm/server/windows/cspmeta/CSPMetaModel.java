package com.me.mdm.server.windows.cspmeta;

import org.json.JSONObject;

public class CSPMetaModel
{
    private byte actionType;
    private byte dataType;
    
    public CSPMetaModel() {
        this.actionType = 0;
        this.dataType = -1;
    }
    
    public void addReplaceActionType() {
        this.actionType |= 0x1;
    }
    
    public void addAddActionType() {
        this.actionType |= 0x2;
    }
    
    public void addExecActionType() {
        this.actionType |= 0x4;
    }
    
    public void addDeleteActionType() {
        this.actionType |= 0x8;
    }
    
    public static JSONObject getCSPMetaJSON(final CSPMetaModel metaModel, final String locURI) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("loc_uri", (Object)locURI);
        jsonObject.put("docs_url", (Object)getDocsURL(locURI));
        if (metaModel != null) {
            jsonObject.put("action_type", getPreferredActionType(metaModel.getActionType()));
            jsonObject.put("data_type", metaModel.getDataType());
        }
        return jsonObject;
    }
    
    public byte getActionType() {
        return this.actionType;
    }
    
    public void setActionType(final byte actionType) {
        this.actionType = actionType;
    }
    
    public int getDataType() {
        return this.dataType;
    }
    
    public void setDataType(final String ddfDataTypeName) {
        switch (ddfDataTypeName) {
            case "int": {
                this.setDataType((byte)0);
                break;
            }
            case "float": {
                this.setDataType((byte)1);
                break;
            }
            case "bool": {
                this.setDataType((byte)2);
                break;
            }
            case "chr": {
                this.setDataType((byte)3);
                break;
            }
            case "xml": {
                this.setDataType((byte)4);
                break;
            }
            case "b64": {
                this.setDataType((byte)5);
                break;
            }
        }
    }
    
    public void setDataType(final byte dataType) {
        this.dataType = dataType;
    }
    
    private static int getPreferredActionType(final int actionType) {
        final int lsb = actionType & -actionType;
        switch (lsb) {
            case 1: {
                return 1;
            }
            case 2: {
                return 0;
            }
            case 4: {
                return 3;
            }
            case 8: {
                return 2;
            }
            default: {
                return -1;
            }
        }
    }
    
    private static String getDocsURL(final String locURI) {
        final int lastIndex = locURI.lastIndexOf("/");
        final int secondLastIndex = locURI.substring(0, lastIndex).lastIndexOf("/");
        final String category = getCSPCategory(locURI);
        if (category.toLowerCase().equals("policy")) {
            return "https://docs.microsoft.com/en-us/windows/client-management/mdm/policy-csp-" + locURI.substring(secondLastIndex + 1, lastIndex).toLowerCase() + "#" + getAnchorTag(locURI, lastIndex, secondLastIndex);
        }
        return "https://docs.microsoft.com/en-us/windows/client-management/mdm/" + category.toLowerCase() + "-csp";
    }
    
    private static String getCSPCategory(final String locURI) {
        final int startIndex = locURI.lastIndexOf("MSFT/") + 5;
        int endIndex = locURI.indexOf("/", startIndex);
        if (endIndex == -1) {
            endIndex = locURI.length();
        }
        if (startIndex < 5 || startIndex >= locURI.length() || startIndex > endIndex) {
            return "";
        }
        return locURI.substring(startIndex, endIndex);
    }
    
    private static String getAnchorTag(final String locURI, final int lastIndex, final int secondLastIndex) {
        return locURI.substring(secondLastIndex + 1).toLowerCase().replaceAll("/", "-");
    }
}
