package com.me.mdm.server.profiles;

import com.me.devicemanagement.framework.server.exception.SyMException;

public class ProfileException extends SyMException
{
    public String profileErrorCode;
    
    public ProfileException() {
    }
    
    public ProfileException(final String errorCode) {
        this.profileErrorCode = errorCode;
    }
    
    public void setProfileErrorCode(final String errorCode) {
        this.profileErrorCode = errorCode;
    }
    
    public String getProfileErrorCode() {
        return this.profileErrorCode;
    }
}
