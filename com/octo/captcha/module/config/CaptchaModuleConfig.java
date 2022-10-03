package com.octo.captcha.module.config;

import java.util.ResourceBundle;
import com.octo.captcha.module.CaptchaModuleException;
import com.octo.captcha.service.CaptchaServiceException;

public class CaptchaModuleConfig
{
    private static CaptchaModuleConfig instance;
    public static final String MESSAGE_TYPE_BUNDLE = "bundle";
    public static final String ID_GENERATED = "generated";
    public static final String MESSAGE_TYPE_TEXT = "text";
    public static final String ID_SESSION = "session";
    public static final String JMX_REGISTERING_NAME = "com.octo.captcha.module.struts:object=CaptchaServicePlugin";
    private Boolean registerToMbean;
    private String responseKey;
    private String serviceClass;
    private String messageType;
    private String messageValue;
    private String messageKey;
    private String idType;
    private String idKey;
    
    public static CaptchaModuleConfig getInstance() {
        return CaptchaModuleConfig.instance;
    }
    
    private CaptchaModuleConfig() {
        this.registerToMbean = Boolean.FALSE;
        this.responseKey = "jcaptcha_response";
        this.serviceClass = "com.octo.captcha.service.image.DefaultManageableImageCaptchaService";
        this.messageType = "text";
        this.messageValue = "You failed the jcaptcha test";
        this.messageKey = "jcaptcha_fail";
        this.idType = "session";
        this.idKey = "jcaptcha_id";
    }
    
    public String getIdKey() {
        return this.idKey;
    }
    
    public void setIdKey(final String idKey) {
        this.idKey = idKey;
    }
    
    public String getMessageType() {
        return this.messageType;
    }
    
    public void setMessageType(final String messageType) {
        this.messageType = messageType;
    }
    
    public String getMessageValue() {
        return this.messageValue;
    }
    
    public void setMessageValue(final String messageValue) {
        this.messageValue = messageValue;
    }
    
    public String getMessageKey() {
        return this.messageKey;
    }
    
    public void setMessageKey(final String messageKey) {
        this.messageKey = messageKey;
    }
    
    public String getIdType() {
        return this.idType;
    }
    
    public void setIdType(final String idType) {
        this.idType = idType;
    }
    
    public String getServiceClass() {
        return this.serviceClass;
    }
    
    public void setServiceClass(final String serviceClass) {
        this.serviceClass = serviceClass;
    }
    
    public String getResponseKey() {
        return this.responseKey;
    }
    
    public void setResponseKey(final String responseKey) {
        this.responseKey = responseKey;
    }
    
    public Boolean getRegisterToMbean() {
        return this.registerToMbean;
    }
    
    public void setRegisterToMbean(final Boolean registerToMbean) {
        this.registerToMbean = registerToMbean;
    }
    
    public void validate() {
        if (!"text".equals(this.messageType) && !"bundle".equals(this.messageType)) {
            throw new CaptchaServiceException("messageType can only be set to 'text' or 'bundle'");
        }
        if (!"session".equals(this.idType) && !"generated".equals(this.idType)) {
            throw new CaptchaServiceException("idType can only be set to 'session' or 'generated'");
        }
        if (this.messageValue == null) {
            throw new CaptchaModuleException("messageValue cannot be null");
        }
        if (this.messageKey == null || "".equals(this.messageKey)) {
            throw new CaptchaModuleException("messageKey cannot be null or empty");
        }
        if (this.responseKey == null || "".equals(this.responseKey)) {
            throw new CaptchaModuleException("responseKey cannot be null or empty");
        }
        if (this.idType.equals("generated") && (this.idKey == null || "".equals(this.idKey))) {
            throw new CaptchaServiceException("idKey cannot be null or empty when id is generated (ie idType='generated'");
        }
        if (this.messageType.equals("bundle")) {
            final ResourceBundle bundle = ResourceBundle.getBundle(this.getMessageValue());
            if (bundle == null) {
                throw new CaptchaModuleException("can't initialize module config with a unfound bundle : resource bundle " + this.getMessageValue() + " has  not been found");
            }
            if (bundle.getString(this.getMessageKey()) == null) {
                throw new CaptchaModuleException("can't initialize module config with a unfound message : resource bundle " + this.getMessageValue() + " has  no key named :" + this.getMessageKey());
            }
        }
        try {
            Class.forName(this.serviceClass).newInstance();
        }
        catch (final Throwable t) {
            t.printStackTrace();
            throw new CaptchaModuleException("Error during Service Class initialization", t);
        }
    }
    
    static {
        CaptchaModuleConfig.instance = new CaptchaModuleConfig();
    }
}
