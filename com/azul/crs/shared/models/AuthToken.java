package com.azul.crs.shared.models;

import java.util.Objects;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthToken extends Payload
{
    private String token;
    private String refreshToken;
    private Long expirationTime;
    private String vmId;
    
    public String getToken() {
        return this.token;
    }
    
    public Long getExpirationTime() {
        return this.expirationTime;
    }
    
    public String getRefreshToken() {
        return this.refreshToken;
    }
    
    public String getVmId() {
        return this.vmId;
    }
    
    public void setToken(final String token) {
        this.token = token;
    }
    
    public void setRefreshToken(final String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public void setExpirationTime(final Long expirationTime) {
        this.expirationTime = expirationTime;
    }
    
    public void setVmId(final String vmId) {
        this.vmId = vmId;
    }
    
    public AuthToken token(final String token) {
        this.setToken(token);
        return this;
    }
    
    public AuthToken refreshToken(final String refreshToken) {
        this.setRefreshToken(refreshToken);
        return this;
    }
    
    public AuthToken expirationTime(final Long expirationTime) {
        this.setExpirationTime(expirationTime);
        return this;
    }
    
    public AuthToken vmId(final String vmId) {
        this.setVmId(vmId);
        return this;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final AuthToken authToken = (AuthToken)o;
        return Objects.equals(this.token, authToken.token) && Objects.equals(this.refreshToken, authToken.refreshToken) && Objects.equals(this.expirationTime, authToken.expirationTime);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.token, this.refreshToken, this.expirationTime);
    }
}
