package com.unboundid.ldap.sdk;

import java.util.Iterator;
import java.util.LinkedHashSet;
import com.unboundid.util.Validator;
import com.unboundid.util.StaticUtils;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.List;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import java.io.Serializable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class GSSAPIBindRequestProperties implements Serializable
{
    private static final long serialVersionUID = 6872295509330315713L;
    private ASN1OctetString password;
    private boolean enableGSSAPIDebugging;
    private Boolean isInitiator;
    private boolean refreshKrb5Config;
    private boolean renewTGT;
    private boolean requireCachedCredentials;
    private boolean useKeyTab;
    private boolean useSubjectCredentialsOnly;
    private boolean useTicketCache;
    private List<SASLQualityOfProtection> allowedQoP;
    private Set<String> suppressedSystemProperties;
    private String authenticationID;
    private String authorizationID;
    private String configFilePath;
    private String jaasClientName;
    private String kdcAddress;
    private String keyTabPath;
    private String realm;
    private String saslClientServerName;
    private String servicePrincipalProtocol;
    private String ticketCachePath;
    
    public GSSAPIBindRequestProperties(final String authenticationID, final String password) {
        this(authenticationID, null, (password == null) ? null : new ASN1OctetString(password), null, null, null);
    }
    
    public GSSAPIBindRequestProperties(final String authenticationID, final byte[] password) {
        this(authenticationID, null, (password == null) ? null : new ASN1OctetString(password), null, null, null);
    }
    
    GSSAPIBindRequestProperties(final String authenticationID, final String authorizationID, final ASN1OctetString password, final String realm, final String kdcAddress, final String configFilePath) {
        this.authenticationID = authenticationID;
        this.authorizationID = authorizationID;
        this.password = password;
        this.realm = realm;
        this.kdcAddress = kdcAddress;
        this.configFilePath = configFilePath;
        this.servicePrincipalProtocol = "ldap";
        this.enableGSSAPIDebugging = false;
        this.jaasClientName = "GSSAPIBindRequest";
        this.isInitiator = null;
        this.refreshKrb5Config = false;
        this.renewTGT = false;
        this.useKeyTab = false;
        this.useSubjectCredentialsOnly = true;
        this.useTicketCache = true;
        this.requireCachedCredentials = false;
        this.saslClientServerName = null;
        this.keyTabPath = null;
        this.ticketCachePath = null;
        this.suppressedSystemProperties = Collections.emptySet();
        this.allowedQoP = Collections.singletonList(SASLQualityOfProtection.AUTH);
    }
    
    public String getAuthenticationID() {
        return this.authenticationID;
    }
    
    public void setAuthenticationID(final String authenticationID) {
        this.authenticationID = authenticationID;
    }
    
    public String getAuthorizationID() {
        return this.authorizationID;
    }
    
    public void setAuthorizationID(final String authorizationID) {
        this.authorizationID = authorizationID;
    }
    
    public ASN1OctetString getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        if (password == null) {
            this.password = null;
        }
        else {
            this.password = new ASN1OctetString(password);
        }
    }
    
    public void setPassword(final byte[] password) {
        if (password == null) {
            this.password = null;
        }
        else {
            this.password = new ASN1OctetString(password);
        }
    }
    
    public void setPassword(final ASN1OctetString password) {
        this.password = password;
    }
    
    public String getRealm() {
        return this.realm;
    }
    
    public void setRealm(final String realm) {
        this.realm = realm;
    }
    
    public List<SASLQualityOfProtection> getAllowedQoP() {
        return this.allowedQoP;
    }
    
    public void setAllowedQoP(final List<SASLQualityOfProtection> allowedQoP) {
        if (allowedQoP == null || allowedQoP.isEmpty()) {
            this.allowedQoP = Collections.singletonList(SASLQualityOfProtection.AUTH);
        }
        else {
            this.allowedQoP = Collections.unmodifiableList((List<? extends SASLQualityOfProtection>)new ArrayList<SASLQualityOfProtection>(allowedQoP));
        }
    }
    
    public void setAllowedQoP(final SASLQualityOfProtection... allowedQoP) {
        this.setAllowedQoP(StaticUtils.toList(allowedQoP));
    }
    
    public String getKDCAddress() {
        return this.kdcAddress;
    }
    
    public void setKDCAddress(final String kdcAddress) {
        this.kdcAddress = kdcAddress;
    }
    
    public String getJAASClientName() {
        return this.jaasClientName;
    }
    
    public void setJAASClientName(final String jaasClientName) {
        Validator.ensureNotNull(jaasClientName);
        this.jaasClientName = jaasClientName;
    }
    
    public String getConfigFilePath() {
        return this.configFilePath;
    }
    
    public void setConfigFilePath(final String configFilePath) {
        this.configFilePath = configFilePath;
    }
    
    public String getSASLClientServerName() {
        return this.saslClientServerName;
    }
    
    public void setSASLClientServerName(final String saslClientServerName) {
        this.saslClientServerName = saslClientServerName;
    }
    
    public String getServicePrincipalProtocol() {
        return this.servicePrincipalProtocol;
    }
    
    public void setServicePrincipalProtocol(final String servicePrincipalProtocol) {
        Validator.ensureNotNull(servicePrincipalProtocol);
        this.servicePrincipalProtocol = servicePrincipalProtocol;
    }
    
    public boolean refreshKrb5Config() {
        return this.refreshKrb5Config;
    }
    
    public void setRefreshKrb5Config(final boolean refreshKrb5Config) {
        this.refreshKrb5Config = refreshKrb5Config;
    }
    
    public boolean useSubjectCredentialsOnly() {
        return this.useSubjectCredentialsOnly;
    }
    
    public void setUseSubjectCredentialsOnly(final boolean useSubjectCredentialsOnly) {
        this.useSubjectCredentialsOnly = useSubjectCredentialsOnly;
    }
    
    public boolean useKeyTab() {
        return this.useKeyTab;
    }
    
    public void setUseKeyTab(final boolean useKeyTab) {
        this.useKeyTab = useKeyTab;
    }
    
    public String getKeyTabPath() {
        return this.keyTabPath;
    }
    
    public void setKeyTabPath(final String keyTabPath) {
        this.keyTabPath = keyTabPath;
    }
    
    public boolean useTicketCache() {
        return this.useTicketCache;
    }
    
    public void setUseTicketCache(final boolean useTicketCache) {
        this.useTicketCache = useTicketCache;
    }
    
    public boolean requireCachedCredentials() {
        return this.requireCachedCredentials;
    }
    
    public void setRequireCachedCredentials(final boolean requireCachedCredentials) {
        this.requireCachedCredentials = requireCachedCredentials;
    }
    
    public String getTicketCachePath() {
        return this.ticketCachePath;
    }
    
    public void setTicketCachePath(final String ticketCachePath) {
        this.ticketCachePath = ticketCachePath;
    }
    
    public boolean renewTGT() {
        return this.renewTGT;
    }
    
    public void setRenewTGT(final boolean renewTGT) {
        this.renewTGT = renewTGT;
    }
    
    public Boolean getIsInitiator() {
        return this.isInitiator;
    }
    
    public void setIsInitiator(final Boolean isInitiator) {
        this.isInitiator = isInitiator;
    }
    
    public Set<String> getSuppressedSystemProperties() {
        return this.suppressedSystemProperties;
    }
    
    public void setSuppressedSystemProperties(final Collection<String> suppressedSystemProperties) {
        if (suppressedSystemProperties == null) {
            this.suppressedSystemProperties = Collections.emptySet();
        }
        else {
            this.suppressedSystemProperties = Collections.unmodifiableSet((Set<? extends String>)new LinkedHashSet<String>(suppressedSystemProperties));
        }
    }
    
    public boolean enableGSSAPIDebugging() {
        return this.enableGSSAPIDebugging;
    }
    
    public void setEnableGSSAPIDebugging(final boolean enableGSSAPIDebugging) {
        this.enableGSSAPIDebugging = enableGSSAPIDebugging;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("GSSAPIBindRequestProperties(");
        if (this.authenticationID != null) {
            buffer.append("authenticationID='");
            buffer.append(this.authenticationID);
            buffer.append("', ");
        }
        if (this.authorizationID != null) {
            buffer.append("authorizationID='");
            buffer.append(this.authorizationID);
            buffer.append("', ");
        }
        if (this.realm != null) {
            buffer.append("realm='");
            buffer.append(this.realm);
            buffer.append("', ");
        }
        buffer.append("qop='");
        buffer.append(SASLQualityOfProtection.toString(this.allowedQoP));
        buffer.append("', ");
        if (this.kdcAddress != null) {
            buffer.append("kdcAddress='");
            buffer.append(this.kdcAddress);
            buffer.append("', ");
        }
        buffer.append(", refreshKrb5Config=");
        buffer.append(this.refreshKrb5Config);
        buffer.append(", useSubjectCredentialsOnly=");
        buffer.append(this.useSubjectCredentialsOnly);
        buffer.append(", useKeyTab=");
        buffer.append(this.useKeyTab);
        buffer.append(", ");
        if (this.keyTabPath != null) {
            buffer.append("keyTabPath='");
            buffer.append(this.keyTabPath);
            buffer.append("', ");
        }
        if (this.useTicketCache) {
            buffer.append("useTicketCache=true, requireCachedCredentials=");
            buffer.append(this.requireCachedCredentials);
            buffer.append(", renewTGT=");
            buffer.append(this.renewTGT);
            buffer.append(", ");
            if (this.ticketCachePath != null) {
                buffer.append("ticketCachePath='");
                buffer.append(this.ticketCachePath);
                buffer.append("', ");
            }
        }
        else {
            buffer.append("useTicketCache=false, ");
        }
        if (this.isInitiator != null) {
            buffer.append("isInitiator=");
            buffer.append(this.isInitiator);
            buffer.append(", ");
        }
        buffer.append("jaasClientName='");
        buffer.append(this.jaasClientName);
        buffer.append("', ");
        if (this.configFilePath != null) {
            buffer.append("configFilePath='");
            buffer.append(this.configFilePath);
            buffer.append("', ");
        }
        if (this.saslClientServerName != null) {
            buffer.append("saslClientServerName='");
            buffer.append(this.saslClientServerName);
            buffer.append("', ");
        }
        buffer.append("servicePrincipalProtocol='");
        buffer.append(this.servicePrincipalProtocol);
        buffer.append("', suppressedSystemProperties={");
        final Iterator<String> propIterator = this.suppressedSystemProperties.iterator();
        while (propIterator.hasNext()) {
            buffer.append('\'');
            buffer.append(propIterator.next());
            buffer.append('\'');
            if (propIterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("}, enableGSSAPIDebugging=");
        buffer.append(this.enableGSSAPIDebugging);
        buffer.append(')');
    }
}
