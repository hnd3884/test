package com.azul.crs.shared.models;

import java.util.Objects;

public class UserCredential
{
    private String userId;
    private String password;
    
    public String getUserId() {
        return this.userId;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setUserId(final String userId) {
        this.userId = userId;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public UserCredential userId(final String userId) {
        this.setUserId(userId);
        return this;
    }
    
    public UserCredential password(final String password) {
        this.setPassword(password);
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
        final UserCredential that = (UserCredential)o;
        return Objects.equals(this.userId, that.userId) && Objects.equals(this.password, that.password);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.userId, this.password);
    }
}
