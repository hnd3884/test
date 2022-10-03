package com.unboundid.ldap.listener;

import java.util.logging.Handler;
import com.unboundid.ldap.sdk.schema.Schema;
import java.util.Map;
import com.unboundid.ldap.sdk.OperationType;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ReadOnlyInMemoryDirectoryServerConfig extends InMemoryDirectoryServerConfig
{
    public ReadOnlyInMemoryDirectoryServerConfig(final InMemoryDirectoryServerConfig config) {
        super(config);
    }
    
    @Override
    public DN[] getBaseDNs() {
        final DN[] origBaseDNs = super.getBaseDNs();
        final DN[] baseDNsCopy = new DN[origBaseDNs.length];
        System.arraycopy(origBaseDNs, 0, baseDNsCopy, 0, baseDNsCopy.length);
        return baseDNsCopy;
    }
    
    @Override
    public void setBaseDNs(final String... baseDNs) throws LDAPException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setBaseDNs(final DN... baseDNs) throws LDAPException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public List<InMemoryListenerConfig> getListenerConfigs() {
        return Collections.unmodifiableList((List<? extends InMemoryListenerConfig>)super.getListenerConfigs());
    }
    
    @Override
    public void setListenerConfigs(final InMemoryListenerConfig... listenerConfigs) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setListenerConfigs(final Collection<InMemoryListenerConfig> listenerConfigs) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Set<OperationType> getAllowedOperationTypes() {
        return Collections.unmodifiableSet((Set<? extends OperationType>)super.getAllowedOperationTypes());
    }
    
    @Override
    public void setAllowedOperationTypes(final OperationType... operationTypes) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setAllowedOperationTypes(final Collection<OperationType> operationTypes) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Set<OperationType> getAuthenticationRequiredOperationTypes() {
        return Collections.unmodifiableSet((Set<? extends OperationType>)super.getAuthenticationRequiredOperationTypes());
    }
    
    @Override
    public void setAuthenticationRequiredOperationTypes(final OperationType... operationTypes) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setAuthenticationRequiredOperationTypes(final Collection<OperationType> operationTypes) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Map<DN, byte[]> getAdditionalBindCredentials() {
        return Collections.unmodifiableMap((Map<? extends DN, ? extends byte[]>)super.getAdditionalBindCredentials());
    }
    
    @Override
    public void addAdditionalBindCredentials(final String dn, final String password) throws LDAPException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void addAdditionalBindCredentials(final String dn, final byte[] password) throws LDAPException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setListenerExceptionHandler(final LDAPListenerExceptionHandler exceptionHandler) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setSchema(final Schema schema) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setEnforceAttributeSyntaxCompliance(final boolean enforceAttributeSyntaxCompliance) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setEnforceSingleStructuralObjectClass(final boolean enforceSingleStructuralObjectClass) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setAccessLogHandler(final Handler accessLogHandler) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setLDAPDebugLogHandler(final Handler ldapDebugLogHandler) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public List<InMemoryExtendedOperationHandler> getExtendedOperationHandlers() {
        return Collections.unmodifiableList((List<? extends InMemoryExtendedOperationHandler>)super.getExtendedOperationHandlers());
    }
    
    @Override
    public void addExtendedOperationHandler(final InMemoryExtendedOperationHandler handler) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public List<InMemorySASLBindHandler> getSASLBindHandlers() {
        return Collections.unmodifiableList((List<? extends InMemorySASLBindHandler>)super.getSASLBindHandlers());
    }
    
    @Override
    public void addSASLBindHandler(final InMemorySASLBindHandler handler) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setGenerateOperationalAttributes(final boolean generateOperationalAttributes) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setMaxChangeLogEntries(final int maxChangeLogEntries) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public List<String> getEqualityIndexAttributes() {
        return Collections.unmodifiableList((List<? extends String>)super.getEqualityIndexAttributes());
    }
    
    @Override
    public void setEqualityIndexAttributes(final String... equalityIndexAttributes) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setEqualityIndexAttributes(final Collection<String> equalityIndexAttributes) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Set<String> getReferentialIntegrityAttributes() {
        return Collections.unmodifiableSet((Set<? extends String>)super.getReferentialIntegrityAttributes());
    }
    
    @Override
    public void setReferentialIntegrityAttributes(final String... referentialIntegrityAttributes) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setReferentialIntegrityAttributes(final Collection<String> referentialIntegrityAttributes) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setVendorName(final String vendorName) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setVendorVersion(final String vendorVersion) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
