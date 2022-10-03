package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import java.math.BigInteger;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UserPosixAccount extends GenericJson
{
    @Key
    private String accountId;
    @Key
    private String gecos;
    @Key
    @JsonString
    private BigInteger gid;
    @Key
    private String homeDirectory;
    @Key
    private String operatingSystemType;
    @Key
    private Boolean primary;
    @Key
    private String shell;
    @Key
    private String systemId;
    @Key
    @JsonString
    private BigInteger uid;
    @Key
    private String username;
    
    public String getAccountId() {
        return this.accountId;
    }
    
    public UserPosixAccount setAccountId(final String accountId) {
        this.accountId = accountId;
        return this;
    }
    
    public String getGecos() {
        return this.gecos;
    }
    
    public UserPosixAccount setGecos(final String gecos) {
        this.gecos = gecos;
        return this;
    }
    
    public BigInteger getGid() {
        return this.gid;
    }
    
    public UserPosixAccount setGid(final BigInteger gid) {
        this.gid = gid;
        return this;
    }
    
    public String getHomeDirectory() {
        return this.homeDirectory;
    }
    
    public UserPosixAccount setHomeDirectory(final String homeDirectory) {
        this.homeDirectory = homeDirectory;
        return this;
    }
    
    public String getOperatingSystemType() {
        return this.operatingSystemType;
    }
    
    public UserPosixAccount setOperatingSystemType(final String operatingSystemType) {
        this.operatingSystemType = operatingSystemType;
        return this;
    }
    
    public Boolean getPrimary() {
        return this.primary;
    }
    
    public UserPosixAccount setPrimary(final Boolean primary) {
        this.primary = primary;
        return this;
    }
    
    public String getShell() {
        return this.shell;
    }
    
    public UserPosixAccount setShell(final String shell) {
        this.shell = shell;
        return this;
    }
    
    public String getSystemId() {
        return this.systemId;
    }
    
    public UserPosixAccount setSystemId(final String systemId) {
        this.systemId = systemId;
        return this;
    }
    
    public BigInteger getUid() {
        return this.uid;
    }
    
    public UserPosixAccount setUid(final BigInteger uid) {
        this.uid = uid;
        return this;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public UserPosixAccount setUsername(final String username) {
        this.username = username;
        return this;
    }
    
    public UserPosixAccount set(final String fieldName, final Object value) {
        return (UserPosixAccount)super.set(fieldName, value);
    }
    
    public UserPosixAccount clone() {
        return (UserPosixAccount)super.clone();
    }
}
