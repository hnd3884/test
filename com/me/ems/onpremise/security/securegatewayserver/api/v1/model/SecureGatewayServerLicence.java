package com.me.ems.onpremise.security.securegatewayserver.api.v1.model;

public class SecureGatewayServerLicence
{
    Boolean isValid;
    
    public Boolean getIsValid() {
        return this.isValid;
    }
    
    public void setIsValid(final Boolean isValid) {
        this.isValid = isValid;
    }
    
    @Override
    public String toString() {
        return "SecureGatewayServerLicence{isValid=" + this.isValid + '}';
    }
}
