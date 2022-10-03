package com.zoho.security.agent;

import java.util.Arrays;
import java.util.List;

public class Components
{
    public enum COMPONENT_NAME
    {
        SECURITYXML("securityxml"), 
        EVENTXML("eventxml"), 
        CACERT("cacert"), 
        MILESTONE("milestone"), 
        RULEADDITION("ruleaddition"), 
        RULEUPDATION("ruleupdation"), 
        RULEDELETION("ruledeletion"), 
        ENABLE_CSP_REPORT("enable.csp.report"), 
        CSP_REPORT_URI("csp.reporturi"), 
        ENBALE_REQINFO_FILEHASH("enable.requestinfo.filehash"), 
        REQINFO_FILEHASH_ALGO("requestinfo.filehash.algorithm"), 
        ENABLE_SECXML_PUSH("enable.securityxml.push"), 
        ENABLE_CACERT_PUSH("enable.cacert.push"), 
        ENABLE_MILESTONEVERSION_PUSH("enable.milestoneversion.push"), 
        SECRET_REQ_PARAM_NAMES("secret.request.param.names"), 
        SECRET_REQ_HEADER_NAMES("secret.request.header.names"), 
        SECRET_RES_HEADER_NAMES("secret.response.header.names"), 
        ENABLE_APP_FIREWALL("enable.app.firewall"), 
        AD_ADD("ad.add"), 
        AD_UPDATE("ad.update"), 
        AD_REMOVE("ad.remove");
        
        private String value;
        
        private COMPONENT_NAME(final String val) {
            this.value = null;
            this.value = val;
        }
        
        public String getValue() {
            return this.value;
        }
        
        public static COMPONENT_NAME getEnumByProperty(final String property) {
            switch (property) {
                case "enable.csp.report": {
                    return COMPONENT_NAME.ENABLE_CSP_REPORT;
                }
                case "csp.reporturi": {
                    return COMPONENT_NAME.CSP_REPORT_URI;
                }
                case "enable.requestinfo.filehash": {
                    return COMPONENT_NAME.ENBALE_REQINFO_FILEHASH;
                }
                case "requestinfo.filehash.algorithm": {
                    return COMPONENT_NAME.REQINFO_FILEHASH_ALGO;
                }
                case "enable.securityxml.push": {
                    return COMPONENT_NAME.ENABLE_SECXML_PUSH;
                }
                case "enable.cacert.push": {
                    return COMPONENT_NAME.ENABLE_CACERT_PUSH;
                }
                case "enable.milstoneversion.push": {
                    return COMPONENT_NAME.ENABLE_MILESTONEVERSION_PUSH;
                }
                case "securityxml": {
                    return COMPONENT_NAME.SECURITYXML;
                }
                case "ruleaddition": {
                    return COMPONENT_NAME.RULEADDITION;
                }
                case "ruleupdation": {
                    return COMPONENT_NAME.RULEUPDATION;
                }
                case "ruledeletion": {
                    return COMPONENT_NAME.RULEDELETION;
                }
                case "secret.request.param.names": {
                    return COMPONENT_NAME.SECRET_REQ_PARAM_NAMES;
                }
                case "secret.request.header.names": {
                    return COMPONENT_NAME.SECRET_REQ_HEADER_NAMES;
                }
                case "secret.response.header.names": {
                    return COMPONENT_NAME.SECRET_RES_HEADER_NAMES;
                }
                case "enable.app.firewall": {
                    return COMPONENT_NAME.ENABLE_APP_FIREWALL;
                }
                case "ad.add": {
                    return COMPONENT_NAME.AD_ADD;
                }
                case "ad.update": {
                    return COMPONENT_NAME.AD_UPDATE;
                }
                case "ad.remove": {
                    return COMPONENT_NAME.AD_REMOVE;
                }
                default: {
                    return null;
                }
            }
        }
    }
    
    public enum COMPONENT
    {
        HASH(Arrays.asList(COMPONENT_NAME.SECURITYXML, COMPONENT_NAME.EVENTXML)), 
        INVENTORY(Arrays.asList(COMPONENT_NAME.CACERT, COMPONENT_NAME.MILESTONE)), 
        APPFIREWALL(Arrays.asList(COMPONENT_NAME.RULEADDITION, COMPONENT_NAME.RULEUPDATION, COMPONENT_NAME.RULEDELETION)), 
        PROPERTY(Arrays.asList(COMPONENT_NAME.ENABLE_CSP_REPORT, COMPONENT_NAME.CSP_REPORT_URI, COMPONENT_NAME.ENBALE_REQINFO_FILEHASH, COMPONENT_NAME.REQINFO_FILEHASH_ALGO, COMPONENT_NAME.ENABLE_SECXML_PUSH, COMPONENT_NAME.ENABLE_CACERT_PUSH, COMPONENT_NAME.ENABLE_MILESTONEVERSION_PUSH, COMPONENT_NAME.SECRET_REQ_PARAM_NAMES, COMPONENT_NAME.SECRET_REQ_HEADER_NAMES, COMPONENT_NAME.SECRET_RES_HEADER_NAMES, COMPONENT_NAME.ENABLE_APP_FIREWALL)), 
        ATTACK_DISCOVERY(Arrays.asList(COMPONENT_NAME.AD_ADD, COMPONENT_NAME.AD_UPDATE, COMPONENT_NAME.AD_REMOVE));
        
        List<COMPONENT_NAME> subComponent;
        
        private COMPONENT(final List<COMPONENT_NAME> componentList) {
            this.subComponent = componentList;
        }
        
        public List<COMPONENT_NAME> getSubComponents() {
            return this.subComponent;
        }
    }
}
