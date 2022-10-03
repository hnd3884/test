package com.me.ems.onpremise.uac.api.v1.model;

public class PasswordPolicy
{
    Integer minimumLength;
    Boolean isComplexPasswordEnabled;
    Integer preventReuseFor;
    Boolean isLoginRestrictionEnabled;
    Integer badAttemptCount;
    Integer lockPeriod;
    
    public Integer getMinimumLength() {
        return this.minimumLength;
    }
    
    public void setMinimumLength(final Integer minimumLength) {
        this.minimumLength = minimumLength;
    }
    
    public Boolean getComplexPasswordEnabled() {
        return this.isComplexPasswordEnabled;
    }
    
    public void setComplexPasswordEnabled(final Boolean complexPasswordEnabled) {
        this.isComplexPasswordEnabled = complexPasswordEnabled;
    }
    
    public Integer getPreventReuseFor() {
        return this.preventReuseFor;
    }
    
    public void setPreventReuseFor(final Integer preventReuseFor) {
        this.preventReuseFor = preventReuseFor;
    }
    
    public Boolean getLoginRestrictionEnabled() {
        return this.isLoginRestrictionEnabled;
    }
    
    public void setLoginRestrictionEnabled(final Boolean loginRestrictionEnabled) {
        this.isLoginRestrictionEnabled = loginRestrictionEnabled;
    }
    
    public Integer getBadAttemptCount() {
        return this.badAttemptCount;
    }
    
    public void setBadAttemptCount(final Integer badAttemptCount) {
        this.badAttemptCount = badAttemptCount;
    }
    
    public Integer getLockPeriod() {
        return this.lockPeriod;
    }
    
    public void setLockPeriod(final Integer lockPeriod) {
        this.lockPeriod = lockPeriod;
    }
}
