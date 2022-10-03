package com.me.ems.framework.securitysettings.api.v1.model;

import com.adventnet.i18n.I18N;
import java.util.HashMap;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.Map;

public class SecuritySettingsModel
{
    private double basicSettingTotalScore;
    private double basicSettingSecureScore;
    private double advancedSettingTotalScore;
    private double advancedSettingSecureScore;
    private Map settings;
    private Map basicSettings;
    private Map advancedSettings;
    private Map adviceMsg;
    private Long customerId;
    private User user;
    private boolean isRedirectionNeed;
    private boolean isSecurityIconNeed;
    private boolean isSecurityMsgNeed;
    private Map extraVal;
    private boolean isTfaBannerNeed;
    
    public SecuritySettingsModel() {
        this.basicSettingTotalScore = 0.0;
        this.basicSettingSecureScore = 0.0;
        this.advancedSettingTotalScore = 0.0;
        this.advancedSettingSecureScore = 0.0;
        this.settings = new HashMap();
        this.basicSettings = new HashMap();
        this.advancedSettings = new HashMap();
        this.adviceMsg = new HashMap();
        this.isRedirectionNeed = false;
        this.isSecurityIconNeed = false;
        this.isSecurityMsgNeed = false;
        this.extraVal = new HashMap();
        this.isTfaBannerNeed = false;
    }
    
    public boolean isTfaBannerNeed() {
        return this.isTfaBannerNeed;
    }
    
    public void setTfaBannerNeed(final boolean tfaBannerNeed) {
        this.isTfaBannerNeed = tfaBannerNeed;
    }
    
    public User getUser() {
        return this.user;
    }
    
    public void setUser(final User user) {
        this.user = user;
    }
    
    public Long getCustomerId() {
        return this.customerId;
    }
    
    public void setCustomerId(final Long customerId) {
        this.customerId = customerId;
    }
    
    public double getBasicSettingTotalScore() {
        return this.basicSettingTotalScore;
    }
    
    public void incrementBasicSettingTotalScore(final double basicSettingTotalScore) {
        this.basicSettingTotalScore += basicSettingTotalScore;
    }
    
    public double getBasicSettingSecureScore() {
        return this.basicSettingSecureScore;
    }
    
    public void incrementBasicSettingSecureScore(final double basicSettingSecureScore) {
        this.basicSettingSecureScore += basicSettingSecureScore;
    }
    
    public double getAdvancedSettingTotalScore() {
        return this.advancedSettingTotalScore;
    }
    
    public void incrementAdvancedSettingTotalScore(final double advancedSettingTotalScore) {
        this.advancedSettingTotalScore += advancedSettingTotalScore;
    }
    
    public double getAdvancedSettingSecureScore() {
        return this.advancedSettingSecureScore;
    }
    
    public void incrementAdvancedSettingSecureScore(final double advancedSettingSecureScore) {
        this.advancedSettingSecureScore += advancedSettingSecureScore;
    }
    
    public Map getSettings() throws Exception {
        this.settings.put("securityAdvice", this.getSecurityAdvice());
        this.settings.put("securePercentage", this.getSecurePercentage());
        this.settings.put("isBasicSettingsConfigured", this.isAllBasicSettingsConfigured());
        this.settings.put("basicSecuritySettingsDetails", this.getBasicSettings());
        this.settings.put("advancedSecuritySettingsDetails", this.getAdvancedSettings());
        this.settings.putAll(this.getExtraVal());
        return this.settings;
    }
    
    public long getSecurePercentage() {
        return Math.round((this.getBasicSettingSecureScore() + this.getAdvancedSettingSecureScore()) / (this.getBasicSettingTotalScore() + this.getAdvancedSettingTotalScore()) * 100.0);
    }
    
    public boolean isAllBasicSettingsConfigured() {
        return this.getBasicSettingSecureScore() == this.getBasicSettingTotalScore();
    }
    
    public Map getBasicSettings() {
        return this.basicSettings;
    }
    
    public void setBasicSettings(final String key, final Object value) {
        this.basicSettings.put(key, value);
    }
    
    public Map getAdvancedSettings() {
        return this.advancedSettings;
    }
    
    public void setAdvancedSettings(final String key, final Object value) {
        this.advancedSettings.put(key, value);
    }
    
    public boolean isRedirectionNeed() {
        return this.isRedirectionNeed;
    }
    
    public void setRedirectionNeed(final boolean redirectionNeed) {
        this.isRedirectionNeed = redirectionNeed;
    }
    
    public boolean isSecurityIconNeed() {
        return this.isSecurityIconNeed;
    }
    
    public void setSecurityIconNeed(final boolean securityIconNeed) {
        this.isSecurityIconNeed = securityIconNeed;
    }
    
    public void setSecurityMsgNeed(final boolean securityMsgNeeded) {
        this.isSecurityMsgNeed = securityMsgNeeded;
    }
    
    public boolean isSecurityMsgNeed() {
        return this.isSecurityMsgNeed;
    }
    
    public void setExtraVal(final String key, final Object val) {
        this.extraVal.put(key, val);
    }
    
    private Map getExtraVal() {
        return this.extraVal;
    }
    
    public void setSecurityAdvice(final String key, final Object val) {
        this.adviceMsg.put(key, val);
    }
    
    public void setSecurityAdvice(final Map list) {
        this.adviceMsg.putAll(list);
    }
    
    public Map getSecurityAdvice() {
        return this.adviceMsg;
    }
    
    public String getBasicSecurityAdvice() throws Exception {
        final long securePercentage = this.getSecurePercentage();
        return (securePercentage == 100L) ? I18N.getMsg("ems.security.settings.advice_100", new Object[0]) : ((securePercentage >= 50L) ? I18N.getMsg("ems.security.settings.advice_above_50", new Object[0]) : I18N.getMsg("ems.security.settings.advice_below_50", new Object[0]));
    }
    
    @Override
    public String toString() {
        return "SecuritySettingsModel{basicSettingTotalScore=" + this.basicSettingTotalScore + ", basicSettingSecureScore=" + this.basicSettingSecureScore + ", advancedSettingTotalScore=" + this.advancedSettingTotalScore + ", advancedSettingSecureScore=" + this.advancedSettingSecureScore + ", settings=" + this.settings + ", basicSettings=" + this.basicSettings + ", advancedSettings=" + this.advancedSettings + ", customerId=" + this.customerId + ", user=" + this.user + ", isRedirectionNeed=" + this.isRedirectionNeed + ", isSecurityIconNeed=" + this.isSecurityIconNeed + ", isSecurityMsgNeed=" + this.isSecurityMsgNeed + ", extraVal=" + this.extraVal + '}';
    }
}
