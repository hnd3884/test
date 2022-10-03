package com.me.mdm.api.error;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "api-error-constants")
public class APIErrorCodes
{
    protected List<ErrorCode> errorCodes;
    
    public APIErrorCodes() {
        this.errorCodes = new ArrayList<ErrorCode>();
    }
    
    @XmlElement(name = "value")
    public List<ErrorCode> getErrorCodes() {
        if (this.errorCodes == null) {
            this.errorCodes = new ArrayList<ErrorCode>();
        }
        return this.errorCodes;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ErrorCode
    {
        @XmlAttribute
        protected String name;
        @XmlElement
        protected String i18nkey;
        @XmlElement
        protected int httpstatus;
        
        public String getName() {
            return this.name;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public String getI18nkey() {
            return this.i18nkey;
        }
        
        public void setI18nkey(final String i18nkey) {
            this.i18nkey = i18nkey;
        }
        
        public int getHttpstatus() {
            return this.httpstatus;
        }
        
        public void setHttpstatus(final int httpstatus) {
            this.httpstatus = httpstatus;
        }
    }
}
