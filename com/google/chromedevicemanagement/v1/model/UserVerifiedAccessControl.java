package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class UserVerifiedAccessControl extends GenericJson
{
    @Key
    private List<String> accountsWithFullAccess;
    @Key
    private List<String> accountsWithLimitedAccess;
    
    public List<String> getAccountsWithFullAccess() {
        return this.accountsWithFullAccess;
    }
    
    public UserVerifiedAccessControl setAccountsWithFullAccess(final List<String> accountsWithFullAccess) {
        this.accountsWithFullAccess = accountsWithFullAccess;
        return this;
    }
    
    public List<String> getAccountsWithLimitedAccess() {
        return this.accountsWithLimitedAccess;
    }
    
    public UserVerifiedAccessControl setAccountsWithLimitedAccess(final List<String> accountsWithLimitedAccess) {
        this.accountsWithLimitedAccess = accountsWithLimitedAccess;
        return this;
    }
    
    public UserVerifiedAccessControl set(final String s, final Object o) {
        return (UserVerifiedAccessControl)super.set(s, o);
    }
    
    public UserVerifiedAccessControl clone() {
        return (UserVerifiedAccessControl)super.clone();
    }
}
