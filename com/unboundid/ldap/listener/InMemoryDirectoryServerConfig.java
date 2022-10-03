package com.unboundid.ldap.listener;

import java.util.Collections;
import com.unboundid.ldap.sdk.Entry;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.OperationType;
import java.util.Set;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.ldap.sdk.ReadOnlyEntry;
import java.util.Map;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import java.util.List;
import java.util.logging.Handler;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import com.unboundid.util.NotExtensible;

@NotExtensible
@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public class InMemoryDirectoryServerConfig
{
    private boolean enforceAttributeSyntaxCompliance;
    private boolean enforceSingleStructuralObjectClass;
    private boolean generateOperationalAttributes;
    private boolean includeRequestProcessingInCodeLog;
    private DN[] baseDNs;
    private Handler accessLogHandler;
    private Handler ldapDebugLogHandler;
    private InMemoryPasswordEncoder primaryPasswordEncoder;
    private int maxChangeLogEntries;
    private int maxConnections;
    private int maxSizeLimit;
    private LDAPListenerExceptionHandler exceptionHandler;
    private final List<InMemoryExtendedOperationHandler> extendedOperationHandlers;
    private final List<InMemoryListenerConfig> listenerConfigs;
    private final List<InMemoryOperationInterceptor> operationInterceptors;
    private final List<InMemoryPasswordEncoder> secondaryPasswordEncoders;
    private final List<InMemorySASLBindHandler> saslBindHandlers;
    private final List<String> equalityIndexAttributes;
    private final Map<DN, byte[]> additionalBindCredentials;
    private ReadOnlyEntry rootDSEEntry;
    private Schema schema;
    private final Set<OperationType> allowedOperationTypes;
    private final Set<OperationType> authenticationRequiredOperationTypes;
    private final Set<String> referentialIntegrityAttributes;
    private final Set<String> passwordAttributes;
    private String codeLogPath;
    private String vendorName;
    private String vendorVersion;
    
    public InMemoryDirectoryServerConfig(final String... baseDNs) throws LDAPException {
        this(parseDNs(Schema.getDefaultStandardSchema(), baseDNs));
    }
    
    public InMemoryDirectoryServerConfig(final DN... baseDNs) throws LDAPException {
        if (baseDNs == null || baseDNs.length == 0) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_MEM_DS_CFG_NO_BASE_DNS.get());
        }
        this.baseDNs = baseDNs;
        (this.listenerConfigs = new ArrayList<InMemoryListenerConfig>(1)).add(InMemoryListenerConfig.createLDAPConfig("default"));
        this.additionalBindCredentials = new LinkedHashMap<DN, byte[]>(StaticUtils.computeMapCapacity(1));
        this.accessLogHandler = null;
        this.ldapDebugLogHandler = null;
        this.enforceAttributeSyntaxCompliance = true;
        this.enforceSingleStructuralObjectClass = true;
        this.generateOperationalAttributes = true;
        this.maxChangeLogEntries = 0;
        this.maxConnections = 0;
        this.maxSizeLimit = 0;
        this.exceptionHandler = null;
        this.equalityIndexAttributes = new ArrayList<String>(10);
        this.rootDSEEntry = null;
        this.schema = Schema.getDefaultStandardSchema();
        this.allowedOperationTypes = EnumSet.allOf(OperationType.class);
        this.authenticationRequiredOperationTypes = EnumSet.noneOf(OperationType.class);
        this.referentialIntegrityAttributes = new HashSet<String>(0);
        this.vendorName = "Ping Identity Corporation";
        this.vendorVersion = "UnboundID LDAP SDK for Java 4.0.14";
        this.codeLogPath = null;
        this.includeRequestProcessingInCodeLog = false;
        this.operationInterceptors = new ArrayList<InMemoryOperationInterceptor>(5);
        (this.extendedOperationHandlers = new ArrayList<InMemoryExtendedOperationHandler>(3)).add(new PasswordModifyExtendedOperationHandler());
        this.extendedOperationHandlers.add(new TransactionExtendedOperationHandler());
        this.extendedOperationHandlers.add(new WhoAmIExtendedOperationHandler());
        (this.saslBindHandlers = new ArrayList<InMemorySASLBindHandler>(1)).add(new PLAINBindHandler());
        (this.passwordAttributes = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(5))).add("userPassword");
        this.primaryPasswordEncoder = null;
        this.secondaryPasswordEncoders = new ArrayList<InMemoryPasswordEncoder>(5);
    }
    
    public InMemoryDirectoryServerConfig(final InMemoryDirectoryServerConfig cfg) {
        this.baseDNs = new DN[cfg.baseDNs.length];
        System.arraycopy(cfg.baseDNs, 0, this.baseDNs, 0, this.baseDNs.length);
        this.listenerConfigs = new ArrayList<InMemoryListenerConfig>(cfg.listenerConfigs);
        this.operationInterceptors = new ArrayList<InMemoryOperationInterceptor>(cfg.operationInterceptors);
        this.extendedOperationHandlers = new ArrayList<InMemoryExtendedOperationHandler>(cfg.extendedOperationHandlers);
        this.saslBindHandlers = new ArrayList<InMemorySASLBindHandler>(cfg.saslBindHandlers);
        this.additionalBindCredentials = new LinkedHashMap<DN, byte[]>(cfg.additionalBindCredentials);
        this.referentialIntegrityAttributes = new HashSet<String>(cfg.referentialIntegrityAttributes);
        (this.allowedOperationTypes = EnumSet.noneOf(OperationType.class)).addAll(cfg.allowedOperationTypes);
        (this.authenticationRequiredOperationTypes = EnumSet.noneOf(OperationType.class)).addAll(cfg.authenticationRequiredOperationTypes);
        this.equalityIndexAttributes = new ArrayList<String>(cfg.equalityIndexAttributes);
        this.enforceAttributeSyntaxCompliance = cfg.enforceAttributeSyntaxCompliance;
        this.enforceSingleStructuralObjectClass = cfg.enforceSingleStructuralObjectClass;
        this.generateOperationalAttributes = cfg.generateOperationalAttributes;
        this.accessLogHandler = cfg.accessLogHandler;
        this.ldapDebugLogHandler = cfg.ldapDebugLogHandler;
        this.maxChangeLogEntries = cfg.maxChangeLogEntries;
        this.maxConnections = cfg.maxConnections;
        this.maxSizeLimit = cfg.maxSizeLimit;
        this.exceptionHandler = cfg.exceptionHandler;
        this.rootDSEEntry = cfg.rootDSEEntry;
        this.schema = cfg.schema;
        this.vendorName = cfg.vendorName;
        this.vendorVersion = cfg.vendorVersion;
        this.codeLogPath = cfg.codeLogPath;
        this.includeRequestProcessingInCodeLog = cfg.includeRequestProcessingInCodeLog;
        this.primaryPasswordEncoder = cfg.primaryPasswordEncoder;
        this.passwordAttributes = new LinkedHashSet<String>(cfg.passwordAttributes);
        this.secondaryPasswordEncoders = new ArrayList<InMemoryPasswordEncoder>(cfg.secondaryPasswordEncoders);
    }
    
    public DN[] getBaseDNs() {
        return this.baseDNs;
    }
    
    public void setBaseDNs(final String... baseDNs) throws LDAPException {
        this.setBaseDNs(parseDNs(this.schema, baseDNs));
    }
    
    public void setBaseDNs(final DN... baseDNs) throws LDAPException {
        if (baseDNs == null || baseDNs.length == 0) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_MEM_DS_CFG_NO_BASE_DNS.get());
        }
        this.baseDNs = baseDNs;
    }
    
    public List<InMemoryListenerConfig> getListenerConfigs() {
        return this.listenerConfigs;
    }
    
    public void setListenerConfigs(final InMemoryListenerConfig... listenerConfigs) throws LDAPException {
        this.setListenerConfigs(StaticUtils.toList(listenerConfigs));
    }
    
    public void setListenerConfigs(final Collection<InMemoryListenerConfig> listenerConfigs) throws LDAPException {
        if (listenerConfigs == null || listenerConfigs.isEmpty()) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_MEM_DS_CFG_NO_LISTENERS.get());
        }
        final HashSet<String> listenerNames = new HashSet<String>(StaticUtils.computeMapCapacity(listenerConfigs.size()));
        for (final InMemoryListenerConfig c : listenerConfigs) {
            final String name = StaticUtils.toLowerCase(c.getListenerName());
            if (listenerNames.contains(name)) {
                throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_MEM_DS_CFG_CONFLICTING_LISTENER_NAMES.get(name));
            }
            listenerNames.add(name);
        }
        this.listenerConfigs.clear();
        this.listenerConfigs.addAll(listenerConfigs);
    }
    
    public Set<OperationType> getAllowedOperationTypes() {
        return this.allowedOperationTypes;
    }
    
    public void setAllowedOperationTypes(final OperationType... operationTypes) {
        this.allowedOperationTypes.clear();
        if (operationTypes != null) {
            this.allowedOperationTypes.addAll(Arrays.asList(operationTypes));
        }
    }
    
    public void setAllowedOperationTypes(final Collection<OperationType> operationTypes) {
        this.allowedOperationTypes.clear();
        if (operationTypes != null) {
            this.allowedOperationTypes.addAll(operationTypes);
        }
    }
    
    public Set<OperationType> getAuthenticationRequiredOperationTypes() {
        return this.authenticationRequiredOperationTypes;
    }
    
    public void setAuthenticationRequiredOperationTypes(final OperationType... operationTypes) {
        this.authenticationRequiredOperationTypes.clear();
        if (operationTypes != null) {
            this.authenticationRequiredOperationTypes.addAll(Arrays.asList(operationTypes));
        }
    }
    
    public void setAuthenticationRequiredOperationTypes(final Collection<OperationType> operationTypes) {
        this.authenticationRequiredOperationTypes.clear();
        if (operationTypes != null) {
            this.authenticationRequiredOperationTypes.addAll(operationTypes);
        }
    }
    
    public Map<DN, byte[]> getAdditionalBindCredentials() {
        return this.additionalBindCredentials;
    }
    
    public void addAdditionalBindCredentials(final String dn, final String password) throws LDAPException {
        this.addAdditionalBindCredentials(dn, StaticUtils.getBytes(password));
    }
    
    public void addAdditionalBindCredentials(final String dn, final byte[] password) throws LDAPException {
        if (dn == null) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_MEM_DS_CFG_NULL_ADDITIONAL_BIND_DN.get());
        }
        final DN parsedDN = new DN(dn, this.schema);
        if (parsedDN.isNullDN()) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_MEM_DS_CFG_NULL_ADDITIONAL_BIND_DN.get());
        }
        if (password == null || password.length == 0) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_MEM_DS_CFG_NULL_ADDITIONAL_BIND_PW.get());
        }
        this.additionalBindCredentials.put(parsedDN, password);
    }
    
    public LDAPListenerExceptionHandler getListenerExceptionHandler() {
        return this.exceptionHandler;
    }
    
    public void setListenerExceptionHandler(final LDAPListenerExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
    
    public Schema getSchema() {
        return this.schema;
    }
    
    public void setSchema(final Schema schema) {
        this.schema = schema;
    }
    
    public boolean enforceAttributeSyntaxCompliance() {
        return this.enforceAttributeSyntaxCompliance;
    }
    
    public void setEnforceAttributeSyntaxCompliance(final boolean enforceAttributeSyntaxCompliance) {
        this.enforceAttributeSyntaxCompliance = enforceAttributeSyntaxCompliance;
    }
    
    public boolean enforceSingleStructuralObjectClass() {
        return this.enforceSingleStructuralObjectClass;
    }
    
    public void setEnforceSingleStructuralObjectClass(final boolean enforceSingleStructuralObjectClass) {
        this.enforceSingleStructuralObjectClass = enforceSingleStructuralObjectClass;
    }
    
    public Handler getAccessLogHandler() {
        return this.accessLogHandler;
    }
    
    public void setAccessLogHandler(final Handler accessLogHandler) {
        this.accessLogHandler = accessLogHandler;
    }
    
    public Handler getLDAPDebugLogHandler() {
        return this.ldapDebugLogHandler;
    }
    
    public void setLDAPDebugLogHandler(final Handler ldapDebugLogHandler) {
        this.ldapDebugLogHandler = ldapDebugLogHandler;
    }
    
    public String getCodeLogPath() {
        return this.codeLogPath;
    }
    
    public boolean includeRequestProcessingInCodeLog() {
        return this.includeRequestProcessingInCodeLog;
    }
    
    public void setCodeLogDetails(final String codeLogPath, final boolean includeProcessing) {
        this.codeLogPath = codeLogPath;
        this.includeRequestProcessingInCodeLog = includeProcessing;
    }
    
    public List<InMemoryOperationInterceptor> getOperationInterceptors() {
        return this.operationInterceptors;
    }
    
    public void addInMemoryOperationInterceptor(final InMemoryOperationInterceptor interceptor) {
        this.operationInterceptors.add(interceptor);
    }
    
    public List<InMemoryExtendedOperationHandler> getExtendedOperationHandlers() {
        return this.extendedOperationHandlers;
    }
    
    public void addExtendedOperationHandler(final InMemoryExtendedOperationHandler handler) {
        this.extendedOperationHandlers.add(handler);
    }
    
    public List<InMemorySASLBindHandler> getSASLBindHandlers() {
        return this.saslBindHandlers;
    }
    
    public void addSASLBindHandler(final InMemorySASLBindHandler handler) {
        this.saslBindHandlers.add(handler);
    }
    
    public boolean generateOperationalAttributes() {
        return this.generateOperationalAttributes;
    }
    
    public void setGenerateOperationalAttributes(final boolean generateOperationalAttributes) {
        this.generateOperationalAttributes = generateOperationalAttributes;
    }
    
    public int getMaxChangeLogEntries() {
        return this.maxChangeLogEntries;
    }
    
    public void setMaxChangeLogEntries(final int maxChangeLogEntries) {
        if (maxChangeLogEntries < 0) {
            this.maxChangeLogEntries = 0;
        }
        else {
            this.maxChangeLogEntries = maxChangeLogEntries;
        }
    }
    
    public int getMaxConnections() {
        return this.maxConnections;
    }
    
    public void setMaxConnections(final int maxConnections) {
        if (maxConnections > 0) {
            this.maxConnections = maxConnections;
        }
        else {
            this.maxConnections = 0;
        }
    }
    
    public int getMaxSizeLimit() {
        return this.maxSizeLimit;
    }
    
    public void setMaxSizeLimit(final int maxSizeLimit) {
        if (maxSizeLimit > 0) {
            this.maxSizeLimit = maxSizeLimit;
        }
        else {
            this.maxSizeLimit = 0;
        }
    }
    
    public List<String> getEqualityIndexAttributes() {
        return this.equalityIndexAttributes;
    }
    
    public void setEqualityIndexAttributes(final String... equalityIndexAttributes) {
        this.setEqualityIndexAttributes(StaticUtils.toList(equalityIndexAttributes));
    }
    
    public void setEqualityIndexAttributes(final Collection<String> equalityIndexAttributes) {
        this.equalityIndexAttributes.clear();
        if (equalityIndexAttributes != null) {
            this.equalityIndexAttributes.addAll(equalityIndexAttributes);
        }
    }
    
    public Set<String> getReferentialIntegrityAttributes() {
        return this.referentialIntegrityAttributes;
    }
    
    public void setReferentialIntegrityAttributes(final String... referentialIntegrityAttributes) {
        this.setReferentialIntegrityAttributes(StaticUtils.toList(referentialIntegrityAttributes));
    }
    
    public void setReferentialIntegrityAttributes(final Collection<String> referentialIntegrityAttributes) {
        this.referentialIntegrityAttributes.clear();
        if (referentialIntegrityAttributes != null) {
            this.referentialIntegrityAttributes.addAll(referentialIntegrityAttributes);
        }
    }
    
    public String getVendorName() {
        return this.vendorName;
    }
    
    public void setVendorName(final String vendorName) {
        this.vendorName = vendorName;
    }
    
    public String getVendorVersion() {
        return this.vendorVersion;
    }
    
    public void setVendorVersion(final String vendorVersion) {
        this.vendorVersion = vendorVersion;
    }
    
    public ReadOnlyEntry getRootDSEEntry() {
        return this.rootDSEEntry;
    }
    
    public void setRootDSEEntry(final Entry rootDSEEntry) {
        if (rootDSEEntry == null) {
            this.rootDSEEntry = null;
            return;
        }
        final Entry e = rootDSEEntry.duplicate();
        e.setDN("");
        this.rootDSEEntry = new ReadOnlyEntry(e);
    }
    
    public Set<String> getPasswordAttributes() {
        return Collections.unmodifiableSet((Set<? extends String>)this.passwordAttributes);
    }
    
    public void setPasswordAttributes(final String... passwordAttributes) {
        this.setPasswordAttributes(StaticUtils.toList(passwordAttributes));
    }
    
    public void setPasswordAttributes(final Collection<String> passwordAttributes) {
        this.passwordAttributes.clear();
        if (passwordAttributes != null) {
            this.passwordAttributes.addAll(passwordAttributes);
        }
    }
    
    public InMemoryPasswordEncoder getPrimaryPasswordEncoder() {
        return this.primaryPasswordEncoder;
    }
    
    public List<InMemoryPasswordEncoder> getSecondaryPasswordEncoders() {
        return Collections.unmodifiableList((List<? extends InMemoryPasswordEncoder>)this.secondaryPasswordEncoders);
    }
    
    public void setPasswordEncoders(final InMemoryPasswordEncoder primaryEncoder, final InMemoryPasswordEncoder... secondaryEncoders) throws LDAPException {
        this.setPasswordEncoders(primaryEncoder, StaticUtils.toList(secondaryEncoders));
    }
    
    public void setPasswordEncoders(final InMemoryPasswordEncoder primaryEncoder, final Collection<InMemoryPasswordEncoder> secondaryEncoders) throws LDAPException {
        final LinkedHashMap<String, InMemoryPasswordEncoder> newEncoderMap = new LinkedHashMap<String, InMemoryPasswordEncoder>(StaticUtils.computeMapCapacity(10));
        if (primaryEncoder != null) {
            newEncoderMap.put(primaryEncoder.getPrefix(), primaryEncoder);
        }
        if (secondaryEncoders != null) {
            for (final InMemoryPasswordEncoder encoder : secondaryEncoders) {
                if (newEncoderMap.containsKey(encoder.getPrefix())) {
                    throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_MEM_DS_CFG_PW_ENCODER_CONFLICT.get(encoder.getPrefix()));
                }
                newEncoderMap.put(encoder.getPrefix(), encoder);
            }
        }
        if ((this.primaryPasswordEncoder = primaryEncoder) != null) {
            newEncoderMap.remove(primaryEncoder.getPrefix());
        }
        this.secondaryPasswordEncoders.clear();
        this.secondaryPasswordEncoders.addAll(newEncoderMap.values());
    }
    
    private static DN[] parseDNs(final Schema schema, final String... dnStrings) throws LDAPException {
        if (dnStrings == null) {
            return null;
        }
        final DN[] dns = new DN[dnStrings.length];
        for (int i = 0; i < dns.length; ++i) {
            dns[i] = new DN(dnStrings[i], schema);
        }
        return dns;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("InMemoryDirectoryServerConfig(baseDNs={");
        for (int i = 0; i < this.baseDNs.length; ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append('\'');
            this.baseDNs[i].toString(buffer);
            buffer.append('\'');
        }
        buffer.append('}');
        buffer.append(", listenerConfigs={");
        final Iterator<InMemoryListenerConfig> listenerCfgIterator = this.listenerConfigs.iterator();
        while (listenerCfgIterator.hasNext()) {
            listenerCfgIterator.next().toString(buffer);
            if (listenerCfgIterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append('}');
        buffer.append(", schemaProvided=");
        buffer.append(this.schema != null);
        buffer.append(", enforceAttributeSyntaxCompliance=");
        buffer.append(this.enforceAttributeSyntaxCompliance);
        buffer.append(", enforceSingleStructuralObjectClass=");
        buffer.append(this.enforceSingleStructuralObjectClass);
        if (!this.additionalBindCredentials.isEmpty()) {
            buffer.append(", additionalBindDNs={");
            final Iterator<DN> bindDNIterator = this.additionalBindCredentials.keySet().iterator();
            while (bindDNIterator.hasNext()) {
                buffer.append('\'');
                bindDNIterator.next().toString(buffer);
                buffer.append('\'');
                if (bindDNIterator.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append('}');
        }
        if (!this.equalityIndexAttributes.isEmpty()) {
            buffer.append(", equalityIndexAttributes={");
            final Iterator<String> attrIterator = this.equalityIndexAttributes.iterator();
            while (attrIterator.hasNext()) {
                buffer.append('\'');
                buffer.append(attrIterator.next());
                buffer.append('\'');
                if (attrIterator.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append('}');
        }
        if (!this.referentialIntegrityAttributes.isEmpty()) {
            buffer.append(", referentialIntegrityAttributes={");
            final Iterator<String> attrIterator = this.referentialIntegrityAttributes.iterator();
            while (attrIterator.hasNext()) {
                buffer.append('\'');
                buffer.append(attrIterator.next());
                buffer.append('\'');
                if (attrIterator.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append('}');
        }
        buffer.append(", generateOperationalAttributes=");
        buffer.append(this.generateOperationalAttributes);
        if (this.maxChangeLogEntries > 0) {
            buffer.append(", maxChangelogEntries=");
            buffer.append(this.maxChangeLogEntries);
        }
        buffer.append(", maxConnections=");
        buffer.append(this.maxConnections);
        buffer.append(", maxSizeLimit=");
        buffer.append(this.maxSizeLimit);
        if (!this.extendedOperationHandlers.isEmpty()) {
            buffer.append(", extendedOperationHandlers={");
            final Iterator<InMemoryExtendedOperationHandler> handlerIterator = this.extendedOperationHandlers.iterator();
            while (handlerIterator.hasNext()) {
                buffer.append(handlerIterator.next().toString());
                if (handlerIterator.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append('}');
        }
        if (!this.saslBindHandlers.isEmpty()) {
            buffer.append(", saslBindHandlers={");
            final Iterator<InMemorySASLBindHandler> handlerIterator2 = this.saslBindHandlers.iterator();
            while (handlerIterator2.hasNext()) {
                buffer.append(handlerIterator2.next().toString());
                if (handlerIterator2.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append('}');
        }
        buffer.append(", passwordAttributes={");
        final Iterator<String> pwAttrIterator = this.passwordAttributes.iterator();
        while (pwAttrIterator.hasNext()) {
            buffer.append('\'');
            buffer.append(pwAttrIterator.next());
            buffer.append('\'');
            if (pwAttrIterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append('}');
        if (this.primaryPasswordEncoder == null) {
            buffer.append(", primaryPasswordEncoder=null");
        }
        else {
            buffer.append(", primaryPasswordEncoderPrefix='");
            buffer.append(this.primaryPasswordEncoder.getPrefix());
            buffer.append('\'');
        }
        buffer.append(", secondaryPasswordEncoderPrefixes={");
        final Iterator<InMemoryPasswordEncoder> encoderIterator = this.secondaryPasswordEncoders.iterator();
        while (encoderIterator.hasNext()) {
            buffer.append('\'');
            buffer.append(encoderIterator.next().getPrefix());
            buffer.append('\'');
            if (encoderIterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append('}');
        if (this.accessLogHandler != null) {
            buffer.append(", accessLogHandlerClass='");
            buffer.append(this.accessLogHandler.getClass().getName());
            buffer.append('\'');
        }
        if (this.ldapDebugLogHandler != null) {
            buffer.append(", ldapDebugLogHandlerClass='");
            buffer.append(this.ldapDebugLogHandler.getClass().getName());
            buffer.append('\'');
        }
        if (this.codeLogPath != null) {
            buffer.append(", codeLogPath='");
            buffer.append(this.codeLogPath);
            buffer.append("', includeRequestProcessingInCodeLog=");
            buffer.append(this.includeRequestProcessingInCodeLog);
        }
        if (this.exceptionHandler != null) {
            buffer.append(", listenerExceptionHandlerClass='");
            buffer.append(this.exceptionHandler.getClass().getName());
            buffer.append('\'');
        }
        if (this.vendorName != null) {
            buffer.append(", vendorName='");
            buffer.append(this.vendorName);
            buffer.append('\'');
        }
        if (this.vendorVersion != null) {
            buffer.append(", vendorVersion='");
            buffer.append(this.vendorVersion);
            buffer.append('\'');
        }
        buffer.append(')');
    }
}
