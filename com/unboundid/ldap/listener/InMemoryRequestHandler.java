package com.unboundid.ldap.listener;

import com.unboundid.ldap.sdk.LDAPURL;
import com.unboundid.ldap.sdk.controls.PostReadRequestControl;
import com.unboundid.ldap.sdk.controls.PreReadRequestControl;
import com.unboundid.ldap.sdk.controls.AssertionRequestControl;
import com.unboundid.ldap.sdk.controls.ProxiedAuthorizationV2RequestControl;
import com.unboundid.ldap.sdk.controls.ProxiedAuthorizationV1RequestControl;
import com.unboundid.ldap.sdk.ChangeLogEntry;
import com.unboundid.ldap.matchingrules.IntegerMatchingRule;
import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldif.LDIFModifyDNChangeRecord;
import com.unboundid.ldif.LDIFModifyChangeRecord;
import com.unboundid.ldif.LDIFDeleteChangeRecord;
import com.unboundid.ldif.LDIFAddChangeRecord;
import com.unboundid.ldif.LDIFWriter;
import com.unboundid.ldif.LDIFException;
import com.unboundid.ldif.LDIFReader;
import com.unboundid.ldap.sdk.extensions.AbortedTransactionExtendedResult;
import com.unboundid.util.ObjectPair;
import com.unboundid.ldap.sdk.controls.TransactionSpecificationRequestControl;
import java.util.TreeSet;
import java.util.SortedSet;
import com.unboundid.ldap.sdk.Filter;
import java.util.concurrent.atomic.AtomicBoolean;
import com.unboundid.ldap.sdk.controls.VirtualListViewResponseControl;
import com.unboundid.ldap.sdk.controls.SortKey;
import com.unboundid.ldap.sdk.controls.VirtualListViewRequestControl;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import com.unboundid.ldap.sdk.controls.ServerSideSortResponseControl;
import com.unboundid.ldap.sdk.EntrySorter;
import com.unboundid.ldap.sdk.controls.ServerSideSortRequestControl;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.protocol.SearchResultReferenceProtocolOp;
import com.unboundid.ldap.protocol.SearchResultDoneProtocolOp;
import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.protocol.SearchRequestProtocolOp;
import com.unboundid.ldap.protocol.ModifyDNResponseProtocolOp;
import com.unboundid.ldap.protocol.ModifyDNRequestProtocolOp;
import com.unboundid.ldap.sdk.ModifyDNRequest;
import com.unboundid.ldap.sdk.schema.MatchingRuleUseDefinition;
import com.unboundid.ldap.sdk.schema.DITStructureRuleDefinition;
import com.unboundid.ldap.sdk.schema.DITContentRuleDefinition;
import com.unboundid.ldap.sdk.schema.NameFormDefinition;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.protocol.ModifyResponseProtocolOp;
import com.unboundid.ldap.protocol.ModifyRequestProtocolOp;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.protocol.ExtendedResponseProtocolOp;
import com.unboundid.ldap.protocol.ExtendedRequestProtocolOp;
import com.unboundid.ldap.sdk.controls.PreReadResponseControl;
import com.unboundid.ldap.protocol.DeleteResponseProtocolOp;
import com.unboundid.ldap.protocol.DeleteRequestProtocolOp;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.protocol.CompareResponseProtocolOp;
import com.unboundid.ldap.protocol.CompareRequestProtocolOp;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.controls.AuthorizationIdentityResponseControl;
import com.unboundid.ldap.protocol.BindResponseProtocolOp;
import com.unboundid.ldap.protocol.BindRequestProtocolOp;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.controls.PostReadResponseControl;
import com.unboundid.ldap.sdk.RDN;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.schema.ObjectClassDefinition;
import com.unboundid.ldap.sdk.OperationType;
import com.unboundid.ldap.protocol.ProtocolOp;
import com.unboundid.ldap.protocol.AddResponseProtocolOp;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.protocol.AddRequestProtocolOp;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.util.Debug;
import java.util.LinkedHashMap;
import java.util.Iterator;
import com.unboundid.ldap.sdk.Entry;
import java.util.Date;
import com.unboundid.ldap.matchingrules.GeneralizedTimeMatchingRule;
import java.util.UUID;
import com.unboundid.ldap.matchingrules.MatchingRule;
import com.unboundid.ldap.matchingrules.DistinguishedNameMatchingRule;
import com.unboundid.ldap.sdk.Attribute;
import java.util.ArrayList;
import java.util.HashMap;
import com.unboundid.util.StaticUtils;
import java.util.Collections;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Arrays;
import java.util.TreeMap;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.Set;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import java.util.Map;
import java.util.List;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.ldap.sdk.ReadOnlyEntry;
import com.unboundid.ldap.sdk.schema.EntryValidator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class InMemoryRequestHandler extends LDAPListenerRequestHandler
{
    private static final Control[] NO_CONTROLS;
    static final String OID_INTERNAL_OPERATION_REQUEST_CONTROL = "1.3.6.1.4.1.30221.2.5.18";
    private final AtomicLong firstChangeNumber;
    private final AtomicLong lastChangeNumber;
    private final AtomicLong processingDelayMillis;
    private final AtomicReference<EntryValidator> entryValidatorRef;
    private final AtomicReference<ReadOnlyEntry> subschemaSubentryRef;
    private final AtomicReference<Schema> schemaRef;
    private final boolean generateOperationalAttributes;
    private DN authenticatedDN;
    private final DN changeLogBaseDN;
    private final DN subschemaSubentryDN;
    private final InMemoryDirectoryServerConfig config;
    private final InMemoryDirectoryServerSnapshot initialSnapshot;
    private final InMemoryPasswordEncoder primaryPasswordEncoder;
    private final int maxChangelogEntries;
    private final int maxSizeLimit;
    private final LDAPListenerClientConnection connection;
    private final List<InMemoryPasswordEncoder> passwordEncoders;
    private final List<String> configuredPasswordAttributes;
    private final List<String> extendedPasswordAttributes;
    private final Map<AttributeTypeDefinition, InMemoryDirectoryServerEqualityAttributeIndex> equalityIndexes;
    private final Map<DN, byte[]> additionalBindCredentials;
    private final Map<String, InMemoryExtendedOperationHandler> extendedRequestHandlers;
    private final Map<String, InMemorySASLBindHandler> saslBindHandlers;
    private final Map<String, Object> connectionState;
    private final Set<DN> baseDNs;
    private final Set<String> referentialIntegrityAttributes;
    private final Map<DN, ReadOnlyEntry> entryMap;
    
    public InMemoryRequestHandler(final InMemoryDirectoryServerConfig config) throws LDAPException {
        this.config = config;
        this.schemaRef = new AtomicReference<Schema>();
        this.entryValidatorRef = new AtomicReference<EntryValidator>();
        this.subschemaSubentryRef = new AtomicReference<ReadOnlyEntry>();
        final Schema schema = config.getSchema();
        this.schemaRef.set(schema);
        if (schema != null) {
            final EntryValidator entryValidator = new EntryValidator(schema);
            this.entryValidatorRef.set(entryValidator);
            entryValidator.setCheckAttributeSyntax(config.enforceAttributeSyntaxCompliance());
            entryValidator.setCheckStructuralObjectClasses(config.enforceSingleStructuralObjectClass());
        }
        final DN[] baseDNArray = config.getBaseDNs();
        if (baseDNArray == null || baseDNArray.length == 0) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_MEM_HANDLER_NO_BASE_DNS.get());
        }
        this.entryMap = new TreeMap<DN, ReadOnlyEntry>();
        final LinkedHashSet<DN> baseDNSet = new LinkedHashSet<DN>(Arrays.asList(baseDNArray));
        if (baseDNSet.contains(DN.NULL_DN)) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_MEM_HANDLER_NULL_BASE_DN.get());
        }
        this.changeLogBaseDN = new DN("cn=changelog", schema);
        if (baseDNSet.contains(this.changeLogBaseDN)) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_MEM_HANDLER_CHANGELOG_BASE_DN.get(this.changeLogBaseDN));
        }
        this.maxChangelogEntries = config.getMaxChangeLogEntries();
        if (config.getMaxSizeLimit() <= 0) {
            this.maxSizeLimit = Integer.MAX_VALUE;
        }
        else {
            this.maxSizeLimit = config.getMaxSizeLimit();
        }
        final TreeMap<String, InMemoryExtendedOperationHandler> extOpHandlers = new TreeMap<String, InMemoryExtendedOperationHandler>();
        for (final InMemoryExtendedOperationHandler h : config.getExtendedOperationHandlers()) {
            for (final String oid : h.getSupportedExtendedRequestOIDs()) {
                if (extOpHandlers.containsKey(oid)) {
                    throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_MEM_HANDLER_EXTENDED_REQUEST_HANDLER_CONFLICT.get(oid));
                }
                extOpHandlers.put(oid, h);
            }
        }
        this.extendedRequestHandlers = Collections.unmodifiableMap((Map<? extends String, ? extends InMemoryExtendedOperationHandler>)extOpHandlers);
        final TreeMap<String, InMemorySASLBindHandler> saslHandlers = new TreeMap<String, InMemorySASLBindHandler>();
        for (final InMemorySASLBindHandler h2 : config.getSASLBindHandlers()) {
            final String mech = h2.getSASLMechanismName();
            if (saslHandlers.containsKey(mech)) {
                throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_MEM_HANDLER_SASL_BIND_HANDLER_CONFLICT.get(mech));
            }
            saslHandlers.put(mech, h2);
        }
        this.saslBindHandlers = Collections.unmodifiableMap((Map<? extends String, ? extends InMemorySASLBindHandler>)saslHandlers);
        this.additionalBindCredentials = Collections.unmodifiableMap((Map<? extends DN, ? extends byte[]>)config.getAdditionalBindCredentials());
        final List<String> eqIndexAttrs = config.getEqualityIndexAttributes();
        this.equalityIndexes = new HashMap<AttributeTypeDefinition, InMemoryDirectoryServerEqualityAttributeIndex>(StaticUtils.computeMapCapacity(eqIndexAttrs.size()));
        for (final String s : eqIndexAttrs) {
            final InMemoryDirectoryServerEqualityAttributeIndex i = new InMemoryDirectoryServerEqualityAttributeIndex(s, schema);
            this.equalityIndexes.put(i.getAttributeType(), i);
        }
        final Set<String> pwAttrSet = config.getPasswordAttributes();
        final LinkedHashSet<String> basePWAttrSet = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(pwAttrSet.size()));
        final LinkedHashSet<String> extendedPWAttrSet = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(pwAttrSet.size() * 2));
        for (final String attr : pwAttrSet) {
            basePWAttrSet.add(attr);
            extendedPWAttrSet.add(StaticUtils.toLowerCase(attr));
            if (schema != null) {
                final AttributeTypeDefinition attrType = schema.getAttributeType(attr);
                if (attrType == null) {
                    continue;
                }
                for (final String name : attrType.getNames()) {
                    extendedPWAttrSet.add(StaticUtils.toLowerCase(name));
                }
                extendedPWAttrSet.add(StaticUtils.toLowerCase(attrType.getOID()));
            }
        }
        this.configuredPasswordAttributes = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(basePWAttrSet));
        this.extendedPasswordAttributes = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(extendedPWAttrSet));
        this.referentialIntegrityAttributes = Collections.unmodifiableSet((Set<? extends String>)config.getReferentialIntegrityAttributes());
        this.primaryPasswordEncoder = config.getPrimaryPasswordEncoder();
        final ArrayList<InMemoryPasswordEncoder> encoderList = new ArrayList<InMemoryPasswordEncoder>(10);
        if (this.primaryPasswordEncoder != null) {
            encoderList.add(this.primaryPasswordEncoder);
        }
        encoderList.addAll(config.getSecondaryPasswordEncoders());
        this.passwordEncoders = Collections.unmodifiableList((List<? extends InMemoryPasswordEncoder>)encoderList);
        this.baseDNs = Collections.unmodifiableSet((Set<? extends DN>)baseDNSet);
        this.generateOperationalAttributes = config.generateOperationalAttributes();
        this.authenticatedDN = new DN("cn=Internal Root User", schema);
        this.connection = null;
        this.connectionState = Collections.emptyMap();
        this.firstChangeNumber = new AtomicLong(0L);
        this.lastChangeNumber = new AtomicLong(0L);
        this.processingDelayMillis = new AtomicLong(0L);
        final ReadOnlyEntry subschemaSubentry = generateSubschemaSubentry(schema);
        this.subschemaSubentryRef.set(subschemaSubentry);
        this.subschemaSubentryDN = subschemaSubentry.getParsedDN();
        if (this.baseDNs.contains(this.subschemaSubentryDN)) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_MEM_HANDLER_SCHEMA_BASE_DN.get(this.subschemaSubentryDN));
        }
        if (this.maxChangelogEntries > 0) {
            baseDNSet.add(this.changeLogBaseDN);
            final ReadOnlyEntry changeLogBaseEntry = new ReadOnlyEntry(this.changeLogBaseDN, schema, new Attribute[] { new Attribute("objectClass", new String[] { "top", "namedObject" }), new Attribute("cn", "changelog"), new Attribute("entryDN", DistinguishedNameMatchingRule.getInstance(), "cn=changelog"), new Attribute("entryUUID", UUID.randomUUID().toString()), new Attribute("creatorsName", DistinguishedNameMatchingRule.getInstance(), DN.NULL_DN.toString()), new Attribute("createTimestamp", GeneralizedTimeMatchingRule.getInstance(), StaticUtils.encodeGeneralizedTime(new Date())), new Attribute("modifiersName", DistinguishedNameMatchingRule.getInstance(), DN.NULL_DN.toString()), new Attribute("modifyTimestamp", GeneralizedTimeMatchingRule.getInstance(), StaticUtils.encodeGeneralizedTime(new Date())), new Attribute("subschemaSubentry", DistinguishedNameMatchingRule.getInstance(), this.subschemaSubentryDN.toString()) });
            this.entryMap.put(this.changeLogBaseDN, changeLogBaseEntry);
            this.indexAdd(changeLogBaseEntry);
        }
        this.initialSnapshot = this.createSnapshot();
    }
    
    private InMemoryRequestHandler(final InMemoryRequestHandler parent, final LDAPListenerClientConnection connection) {
        this.connection = connection;
        this.authenticatedDN = DN.NULL_DN;
        this.connectionState = Collections.synchronizedMap(new LinkedHashMap<String, Object>(0));
        this.config = parent.config;
        this.generateOperationalAttributes = parent.generateOperationalAttributes;
        this.additionalBindCredentials = parent.additionalBindCredentials;
        this.baseDNs = parent.baseDNs;
        this.changeLogBaseDN = parent.changeLogBaseDN;
        this.firstChangeNumber = parent.firstChangeNumber;
        this.lastChangeNumber = parent.lastChangeNumber;
        this.processingDelayMillis = parent.processingDelayMillis;
        this.maxChangelogEntries = parent.maxChangelogEntries;
        this.maxSizeLimit = parent.maxSizeLimit;
        this.equalityIndexes = parent.equalityIndexes;
        this.referentialIntegrityAttributes = parent.referentialIntegrityAttributes;
        this.entryMap = parent.entryMap;
        this.entryValidatorRef = parent.entryValidatorRef;
        this.extendedRequestHandlers = parent.extendedRequestHandlers;
        this.saslBindHandlers = parent.saslBindHandlers;
        this.schemaRef = parent.schemaRef;
        this.subschemaSubentryRef = parent.subschemaSubentryRef;
        this.subschemaSubentryDN = parent.subschemaSubentryDN;
        this.initialSnapshot = parent.initialSnapshot;
        this.configuredPasswordAttributes = parent.configuredPasswordAttributes;
        this.extendedPasswordAttributes = parent.extendedPasswordAttributes;
        this.primaryPasswordEncoder = parent.primaryPasswordEncoder;
        this.passwordEncoders = parent.passwordEncoders;
    }
    
    @Override
    public InMemoryRequestHandler newInstance(final LDAPListenerClientConnection connection) throws LDAPException {
        return new InMemoryRequestHandler(this, connection);
    }
    
    public InMemoryDirectoryServerSnapshot createSnapshot() {
        synchronized (this.entryMap) {
            return new InMemoryDirectoryServerSnapshot(this.entryMap, this.firstChangeNumber.get(), this.lastChangeNumber.get());
        }
    }
    
    public void restoreSnapshot(final InMemoryDirectoryServerSnapshot snapshot) {
        synchronized (this.entryMap) {
            this.entryMap.clear();
            this.entryMap.putAll(snapshot.getEntryMap());
            for (final InMemoryDirectoryServerEqualityAttributeIndex i : this.equalityIndexes.values()) {
                i.clear();
                for (final Entry e : this.entryMap.values()) {
                    try {
                        i.processAdd(e);
                    }
                    catch (final Exception ex) {
                        Debug.debugException(ex);
                    }
                }
            }
            this.firstChangeNumber.set(snapshot.getFirstChangeNumber());
            this.lastChangeNumber.set(snapshot.getLastChangeNumber());
        }
    }
    
    public Schema getSchema() {
        return this.schemaRef.get();
    }
    
    public List<DN> getBaseDNs() {
        return Collections.unmodifiableList((List<? extends DN>)new ArrayList<DN>(this.baseDNs));
    }
    
    public LDAPListenerClientConnection getClientConnection() {
        return this.connection;
    }
    
    public synchronized DN getAuthenticatedDN() {
        return this.authenticatedDN;
    }
    
    public synchronized void setAuthenticatedDN(final DN authenticatedDN) {
        if (authenticatedDN == null) {
            this.authenticatedDN = DN.NULL_DN;
        }
        else {
            this.authenticatedDN = authenticatedDN;
        }
    }
    
    public Map<DN, byte[]> getAdditionalBindCredentials() {
        return this.additionalBindCredentials;
    }
    
    public byte[] getAdditionalBindCredentials(final DN dn) {
        return this.additionalBindCredentials.get(dn);
    }
    
    public Map<String, Object> getConnectionState() {
        return this.connectionState;
    }
    
    public long getProcessingDelayMillis() {
        return this.processingDelayMillis.get();
    }
    
    public void setProcessingDelayMillis(final long processingDelayMillis) {
        if (processingDelayMillis > 0L) {
            this.processingDelayMillis.set(processingDelayMillis);
        }
        else {
            this.processingDelayMillis.set(0L);
        }
    }
    
    public LDAPResult add(final AddRequest addRequest) throws LDAPException {
        final ArrayList<Control> requestControlList = new ArrayList<Control>(addRequest.getControlList());
        requestControlList.add(new Control("1.3.6.1.4.1.30221.2.5.18", false));
        final LDAPMessage responseMessage = this.processAddRequest(1, new AddRequestProtocolOp(addRequest.getDN(), addRequest.getAttributes()), requestControlList);
        final AddResponseProtocolOp addResponse = responseMessage.getAddResponseProtocolOp();
        final LDAPResult ldapResult = new LDAPResult(responseMessage.getMessageID(), ResultCode.valueOf(addResponse.getResultCode()), addResponse.getDiagnosticMessage(), addResponse.getMatchedDN(), addResponse.getReferralURLs(), responseMessage.getControls());
        switch (addResponse.getResultCode()) {
            case 0:
            case 16654: {
                return ldapResult;
            }
            default: {
                throw new LDAPException(ldapResult);
            }
        }
    }
    
    @Override
    public LDAPMessage processAddRequest(final int messageID, final AddRequestProtocolOp request, final List<Control> controls) {
        synchronized (this.entryMap) {
            this.sleepBeforeProcessing();
            Map<String, Control> controlMap;
            try {
                controlMap = RequestControlPreProcessor.processControls((byte)104, controls);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                return new LDAPMessage(messageID, new AddResponseProtocolOp(le.getResultCode().intValue(), null, le.getMessage(), null), new Control[0]);
            }
            final ArrayList<Control> responseControls = new ArrayList<Control>(1);
            final boolean isInternalOp = controlMap.containsKey("1.3.6.1.4.1.30221.2.5.18");
            if (!isInternalOp && !this.config.getAllowedOperationTypes().contains(OperationType.ADD)) {
                return new LDAPMessage(messageID, new AddResponseProtocolOp(53, null, ListenerMessages.ERR_MEM_HANDLER_ADD_NOT_ALLOWED.get(), null), new Control[0]);
            }
            if (this.authenticatedDN.isNullDN() && this.config.getAuthenticationRequiredOperationTypes().contains(OperationType.ADD)) {
                return new LDAPMessage(messageID, new AddResponseProtocolOp(50, null, ListenerMessages.ERR_MEM_HANDLER_ADD_REQUIRES_AUTH.get(), null), new Control[0]);
            }
            try {
                final ASN1OctetString txnID = this.processTransactionRequest(messageID, request, controlMap);
                if (txnID != null) {
                    return new LDAPMessage(messageID, new AddResponseProtocolOp(0, null, ListenerMessages.INFO_MEM_HANDLER_OP_IN_TXN.get(txnID.stringValue()), null), new Control[0]);
                }
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                return new LDAPMessage(messageID, new AddResponseProtocolOp(le2.getResultCode().intValue(), le2.getMatchedDN(), le2.getDiagnosticMessage(), StaticUtils.toList(le2.getReferralURLs())), le2.getResponseControls());
            }
            final Schema schema = this.schemaRef.get();
            Entry entry;
            if (schema == null) {
                entry = new Entry(request.getDN(), request.getAttributes());
            }
            else {
                final List<Attribute> providedAttrs = request.getAttributes();
                final List<Attribute> newAttrs = new ArrayList<Attribute>(providedAttrs.size());
                for (final Attribute a : providedAttrs) {
                    final String baseName = a.getBaseName();
                    final MatchingRule matchingRule = MatchingRule.selectEqualityMatchingRule(baseName, schema);
                    newAttrs.add(new Attribute(a.getName(), matchingRule, a.getRawValues()));
                }
                entry = new Entry(request.getDN(), schema, newAttrs);
            }
            DN dn;
            try {
                dn = entry.getParsedDN();
            }
            catch (final LDAPException le3) {
                Debug.debugException(le3);
                return new LDAPMessage(messageID, new AddResponseProtocolOp(34, null, ListenerMessages.ERR_MEM_HANDLER_ADD_MALFORMED_DN.get(request.getDN(), le3.getMessage()), null), new Control[0]);
            }
            if (dn.isNullDN()) {
                return new LDAPMessage(messageID, new AddResponseProtocolOp(68, null, ListenerMessages.ERR_MEM_HANDLER_ADD_ROOT_DSE.get(), null), new Control[0]);
            }
            if (dn.isDescendantOf(this.subschemaSubentryDN, true)) {
                return new LDAPMessage(messageID, new AddResponseProtocolOp(68, null, ListenerMessages.ERR_MEM_HANDLER_ADD_SCHEMA.get(this.subschemaSubentryDN.toString()), null), new Control[0]);
            }
            if (dn.isDescendantOf(this.changeLogBaseDN, true)) {
                return new LDAPMessage(messageID, new AddResponseProtocolOp(53, null, ListenerMessages.ERR_MEM_HANDLER_ADD_CHANGELOG.get(this.changeLogBaseDN.toString()), null), new Control[0]);
            }
            if (!controlMap.containsKey("2.16.840.1.113730.3.4.2")) {
                final Entry referralEntry = this.findNearestReferral(dn);
                if (referralEntry != null) {
                    return new LDAPMessage(messageID, new AddResponseProtocolOp(10, referralEntry.getDN(), ListenerMessages.INFO_MEM_HANDLER_REFERRAL_ENCOUNTERED.get(), getReferralURLs(dn, referralEntry)), new Control[0]);
                }
            }
            if (this.entryMap.containsKey(dn)) {
                return new LDAPMessage(messageID, new AddResponseProtocolOp(68, null, ListenerMessages.ERR_MEM_HANDLER_ADD_ALREADY_EXISTS.get(request.getDN()), null), new Control[0]);
            }
            final RDN rdn = dn.getRDN();
            final String[] rdnAttrNames = rdn.getAttributeNames();
            final byte[][] rdnAttrValues = rdn.getByteArrayAttributeValues();
            for (int i = 0; i < rdnAttrNames.length; ++i) {
                final MatchingRule matchingRule = MatchingRule.selectEqualityMatchingRule(rdnAttrNames[i], schema);
                entry.addAttribute(new Attribute(rdnAttrNames[i], matchingRule, rdnAttrValues[i]));
            }
            if (schema != null) {
                final String[] objectClasses = entry.getObjectClassValues();
                if (objectClasses != null) {
                    final LinkedHashMap<String, String> ocMap = new LinkedHashMap<String, String>(StaticUtils.computeMapCapacity(objectClasses.length));
                    for (final String ocName : objectClasses) {
                        final ObjectClassDefinition oc = schema.getObjectClass(ocName);
                        if (oc == null) {
                            ocMap.put(StaticUtils.toLowerCase(ocName), ocName);
                        }
                        else {
                            ocMap.put(StaticUtils.toLowerCase(oc.getNameOrOID()), ocName);
                            for (final ObjectClassDefinition supClass : oc.getSuperiorClasses(schema, true)) {
                                ocMap.put(StaticUtils.toLowerCase(supClass.getNameOrOID()), supClass.getNameOrOID());
                            }
                        }
                    }
                    final String[] newObjectClasses = new String[ocMap.size()];
                    ocMap.values().toArray(newObjectClasses);
                    entry.setAttribute("objectClass", newObjectClasses);
                }
            }
            final EntryValidator entryValidator = this.entryValidatorRef.get();
            if (entryValidator != null) {
                final ArrayList<String> invalidReasons = new ArrayList<String>(1);
                if (!entryValidator.entryIsValid(entry, invalidReasons)) {
                    return new LDAPMessage(messageID, new AddResponseProtocolOp(65, null, ListenerMessages.ERR_MEM_HANDLER_ADD_VIOLATES_SCHEMA.get(request.getDN(), StaticUtils.concatenateStrings(invalidReasons)), null), new Control[0]);
                }
                if (!isInternalOp && schema != null && !controlMap.containsKey("1.3.6.1.4.1.30221.2.5.5")) {
                    for (final Attribute a2 : entry.getAttributes()) {
                        final AttributeTypeDefinition at = schema.getAttributeType(a2.getBaseName());
                        if (at != null && at.isNoUserModification()) {
                            return new LDAPMessage(messageID, new AddResponseProtocolOp(19, null, ListenerMessages.ERR_MEM_HANDLER_ADD_CONTAINS_NO_USER_MOD.get(request.getDN(), a2.getName()), null), new Control[0]);
                        }
                    }
                }
            }
            DN authzDN;
            try {
                authzDN = this.handleProxiedAuthControl(controlMap);
            }
            catch (final LDAPException le4) {
                Debug.debugException(le4);
                return new LDAPMessage(messageID, new AddResponseProtocolOp(le4.getResultCode().intValue(), null, le4.getMessage(), null), new Control[0]);
            }
            if (this.generateOperationalAttributes) {
                final Date d = new Date();
                if (!entry.hasAttribute("entryDN")) {
                    entry.addAttribute(new Attribute("entryDN", DistinguishedNameMatchingRule.getInstance(), dn.toNormalizedString()));
                }
                if (!entry.hasAttribute("entryUUID")) {
                    entry.addAttribute(new Attribute("entryUUID", UUID.randomUUID().toString()));
                }
                if (!entry.hasAttribute("subschemaSubentry")) {
                    entry.addAttribute(new Attribute("subschemaSubentry", DistinguishedNameMatchingRule.getInstance(), this.subschemaSubentryDN.toString()));
                }
                if (!entry.hasAttribute("creatorsName")) {
                    entry.addAttribute(new Attribute("creatorsName", DistinguishedNameMatchingRule.getInstance(), authzDN.toString()));
                }
                if (!entry.hasAttribute("createTimestamp")) {
                    entry.addAttribute(new Attribute("createTimestamp", GeneralizedTimeMatchingRule.getInstance(), StaticUtils.encodeGeneralizedTime(d)));
                }
                if (!entry.hasAttribute("modifiersName")) {
                    entry.addAttribute(new Attribute("modifiersName", DistinguishedNameMatchingRule.getInstance(), authzDN.toString()));
                }
                if (!entry.hasAttribute("modifyTimestamp")) {
                    entry.addAttribute(new Attribute("modifyTimestamp", GeneralizedTimeMatchingRule.getInstance(), StaticUtils.encodeGeneralizedTime(d)));
                }
            }
            try {
                handleAssertionRequestControl(controlMap, entry);
            }
            catch (final LDAPException le4) {
                Debug.debugException(le4);
                return new LDAPMessage(messageID, new AddResponseProtocolOp(le4.getResultCode().intValue(), null, le4.getMessage(), null), new Control[0]);
            }
            if (!this.passwordEncoders.isEmpty() && !this.configuredPasswordAttributes.isEmpty()) {
                final ReadOnlyEntry readOnlyEntry = new ReadOnlyEntry(entry.duplicate());
                for (final String passwordAttribute : this.configuredPasswordAttributes) {
                    for (final Attribute attr : readOnlyEntry.getAttributesWithOptions(passwordAttribute, null)) {
                        final ArrayList<byte[]> newValues = new ArrayList<byte[]>(attr.size());
                        for (final ASN1OctetString value : attr.getRawValues()) {
                            try {
                                newValues.add(this.encodeAddPassword(value, readOnlyEntry, Collections.emptyList()).getValue());
                            }
                            catch (final LDAPException le5) {
                                Debug.debugException(le5);
                                return new LDAPMessage(messageID, new AddResponseProtocolOp(53, le5.getMatchedDN(), le5.getMessage(), null), new Control[0]);
                            }
                        }
                        final byte[][] newValuesArray = new byte[newValues.size()][];
                        newValues.toArray(newValuesArray);
                        entry.setAttribute(new Attribute(attr.getName(), schema, newValuesArray));
                    }
                }
            }
            final PostReadResponseControl postReadResponse = this.handlePostReadControl(controlMap, entry);
            if (postReadResponse != null) {
                responseControls.add(postReadResponse);
            }
            if (this.baseDNs.contains(dn)) {
                this.entryMap.put(dn, new ReadOnlyEntry(entry));
                this.indexAdd(entry);
                this.addChangeLogEntry(request, authzDN);
                return new LDAPMessage(messageID, new AddResponseProtocolOp(0, null, null, null), responseControls);
            }
            final DN parentDN = dn.getParent();
            if (parentDN != null && this.entryMap.containsKey(parentDN)) {
                this.entryMap.put(dn, new ReadOnlyEntry(entry));
                this.indexAdd(entry);
                this.addChangeLogEntry(request, authzDN);
                return new LDAPMessage(messageID, new AddResponseProtocolOp(0, null, null, null), responseControls);
            }
            for (final DN baseDN : this.baseDNs) {
                if (dn.isDescendantOf(baseDN, true)) {
                    return new LDAPMessage(messageID, new AddResponseProtocolOp(32, this.getMatchedDNString(dn), ListenerMessages.ERR_MEM_HANDLER_ADD_MISSING_PARENT.get(request.getDN(), dn.getParentString()), null), new Control[0]);
                }
            }
            return new LDAPMessage(messageID, new AddResponseProtocolOp(32, null, ListenerMessages.ERR_MEM_HANDLER_ADD_NOT_BELOW_BASE_DN.get(request.getDN()), null), new Control[0]);
        }
    }
    
    private ASN1OctetString encodeAddPassword(final ASN1OctetString password, final ReadOnlyEntry entry, final List<Modification> mods) throws LDAPException {
        for (final InMemoryPasswordEncoder encoder : this.passwordEncoders) {
            if (encoder.passwordStartsWithPrefix(password)) {
                encoder.ensurePreEncodedPasswordAppearsValid(password, entry, mods);
                return password;
            }
        }
        if (this.primaryPasswordEncoder != null) {
            return this.primaryPasswordEncoder.encodePassword(password, entry, mods);
        }
        return password;
    }
    
    @Override
    public LDAPMessage processBindRequest(final int messageID, final BindRequestProtocolOp request, final List<Control> controls) {
        synchronized (this.entryMap) {
            this.sleepBeforeProcessing();
            if (!this.config.getAllowedOperationTypes().contains(OperationType.BIND)) {
                return new LDAPMessage(messageID, new BindResponseProtocolOp(53, null, ListenerMessages.ERR_MEM_HANDLER_BIND_NOT_ALLOWED.get(), null, null), new Control[0]);
            }
            this.authenticatedDN = DN.NULL_DN;
            if (this.authenticatedDN.isNullDN() && this.config.getAuthenticationRequiredOperationTypes().contains(OperationType.BIND) && request.getCredentialsType() == -128 && (request.getSimplePassword() == null || request.getSimplePassword().getValueLength() == 0)) {
                return new LDAPMessage(messageID, new BindResponseProtocolOp(49, null, ListenerMessages.ERR_MEM_HANDLER_BIND_REQUIRES_AUTH.get(), null, null), new Control[0]);
            }
            DN bindDN;
            try {
                bindDN = new DN(request.getBindDN(), this.schemaRef.get());
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                return new LDAPMessage(messageID, new BindResponseProtocolOp(34, null, ListenerMessages.ERR_MEM_HANDLER_BIND_MALFORMED_DN.get(request.getBindDN(), le.getMessage()), null, null), new Control[0]);
            }
            if (request.getCredentialsType() == -93) {
                final String mechanism = request.getSASLMechanism();
                final InMemorySASLBindHandler handler = this.saslBindHandlers.get(mechanism);
                if (handler == null) {
                    return new LDAPMessage(messageID, new BindResponseProtocolOp(7, null, ListenerMessages.ERR_MEM_HANDLER_SASL_MECH_NOT_SUPPORTED.get(mechanism), null, null), new Control[0]);
                }
                try {
                    final BindResult bindResult = handler.processSASLBind(this, messageID, bindDN, request.getSASLCredentials(), controls);
                    if (bindResult.getResultCode() == ResultCode.SUCCESS && this.authenticatedDN == DN.NULL_DN && this.config.getAuthenticationRequiredOperationTypes().contains(OperationType.BIND)) {
                        return new LDAPMessage(messageID, new BindResponseProtocolOp(49, null, ListenerMessages.ERR_MEM_HANDLER_BIND_REQUIRES_AUTH.get(), null, null), new Control[0]);
                    }
                    return new LDAPMessage(messageID, new BindResponseProtocolOp(bindResult.getResultCode().intValue(), bindResult.getMatchedDN(), bindResult.getDiagnosticMessage(), Arrays.asList(bindResult.getReferralURLs()), bindResult.getServerSASLCredentials()), Arrays.asList(bindResult.getResponseControls()));
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    return new LDAPMessage(messageID, new BindResponseProtocolOp(80, null, ListenerMessages.ERR_MEM_HANDLER_SASL_BIND_FAILURE.get(StaticUtils.getExceptionMessage(e)), null, null), new Control[0]);
                }
            }
            Map<String, Control> controlMap;
            try {
                controlMap = RequestControlPreProcessor.processControls((byte)96, controls);
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                return new LDAPMessage(messageID, new BindResponseProtocolOp(le2.getResultCode().intValue(), null, le2.getMessage(), null, null), new Control[0]);
            }
            final ArrayList<Control> responseControls = new ArrayList<Control>(1);
            final ASN1OctetString bindPassword = request.getSimplePassword();
            if (bindDN.isNullDN()) {
                if (bindPassword.getValueLength() == 0) {
                    if (controlMap.containsKey("2.16.840.1.113730.3.4.16")) {
                        responseControls.add(new AuthorizationIdentityResponseControl(""));
                    }
                    return new LDAPMessage(messageID, new BindResponseProtocolOp(0, null, null, null, null), responseControls);
                }
                return new LDAPMessage(messageID, new BindResponseProtocolOp(49, this.getMatchedDNString(bindDN), ListenerMessages.ERR_MEM_HANDLER_BIND_WRONG_PASSWORD.get(request.getBindDN()), null, null), new Control[0]);
            }
            else {
                if (!bindDN.isNullDN() && bindPassword.getValueLength() == 0) {
                    return new LDAPMessage(messageID, new BindResponseProtocolOp(53, null, ListenerMessages.ERR_MEM_HANDLER_BIND_SIMPLE_DN_WITHOUT_PASSWORD.get(), null, null), new Control[0]);
                }
                final byte[] additionalCreds = this.additionalBindCredentials.get(bindDN);
                if (additionalCreds != null) {
                    if (Arrays.equals(additionalCreds, bindPassword.getValue())) {
                        this.authenticatedDN = bindDN;
                        if (controlMap.containsKey("2.16.840.1.113730.3.4.16")) {
                            responseControls.add(new AuthorizationIdentityResponseControl("dn:" + bindDN.toString()));
                        }
                        return new LDAPMessage(messageID, new BindResponseProtocolOp(0, null, null, null, null), responseControls);
                    }
                    return new LDAPMessage(messageID, new BindResponseProtocolOp(49, this.getMatchedDNString(bindDN), ListenerMessages.ERR_MEM_HANDLER_BIND_WRONG_PASSWORD.get(request.getBindDN()), null, null), new Control[0]);
                }
                else {
                    final ReadOnlyEntry userEntry = this.entryMap.get(bindDN);
                    if (userEntry == null) {
                        return new LDAPMessage(messageID, new BindResponseProtocolOp(49, this.getMatchedDNString(bindDN), ListenerMessages.ERR_MEM_HANDLER_BIND_NO_SUCH_USER.get(request.getBindDN()), null, null), new Control[0]);
                    }
                    final List<InMemoryDirectoryServerPassword> matchingPasswords = this.getPasswordsInEntry(userEntry, bindPassword);
                    if (matchingPasswords.isEmpty()) {
                        return new LDAPMessage(messageID, new BindResponseProtocolOp(49, this.getMatchedDNString(bindDN), ListenerMessages.ERR_MEM_HANDLER_BIND_WRONG_PASSWORD.get(request.getBindDN()), null, null), new Control[0]);
                    }
                    this.authenticatedDN = bindDN;
                    if (controlMap.containsKey("2.16.840.1.113730.3.4.16")) {
                        responseControls.add(new AuthorizationIdentityResponseControl("dn:" + bindDN.toString()));
                    }
                    return new LDAPMessage(messageID, new BindResponseProtocolOp(0, null, null, null, null), responseControls);
                }
            }
        }
    }
    
    @Override
    public LDAPMessage processCompareRequest(final int messageID, final CompareRequestProtocolOp request, final List<Control> controls) {
        synchronized (this.entryMap) {
            this.sleepBeforeProcessing();
            Map<String, Control> controlMap;
            try {
                controlMap = RequestControlPreProcessor.processControls((byte)110, controls);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                return new LDAPMessage(messageID, new CompareResponseProtocolOp(le.getResultCode().intValue(), null, le.getMessage(), null), new Control[0]);
            }
            final ArrayList<Control> responseControls = new ArrayList<Control>(1);
            final boolean isInternalOp = controlMap.containsKey("1.3.6.1.4.1.30221.2.5.18");
            if (!isInternalOp && !this.config.getAllowedOperationTypes().contains(OperationType.COMPARE)) {
                return new LDAPMessage(messageID, new CompareResponseProtocolOp(53, null, ListenerMessages.ERR_MEM_HANDLER_COMPARE_NOT_ALLOWED.get(), null), new Control[0]);
            }
            if (this.authenticatedDN.isNullDN() && this.config.getAuthenticationRequiredOperationTypes().contains(OperationType.COMPARE)) {
                return new LDAPMessage(messageID, new CompareResponseProtocolOp(50, null, ListenerMessages.ERR_MEM_HANDLER_COMPARE_REQUIRES_AUTH.get(), null), new Control[0]);
            }
            DN dn;
            try {
                dn = new DN(request.getDN(), this.schemaRef.get());
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                return new LDAPMessage(messageID, new CompareResponseProtocolOp(34, null, ListenerMessages.ERR_MEM_HANDLER_COMPARE_MALFORMED_DN.get(request.getDN(), le2.getMessage()), null), new Control[0]);
            }
            if (!controlMap.containsKey("2.16.840.1.113730.3.4.2")) {
                final Entry referralEntry = this.findNearestReferral(dn);
                if (referralEntry != null) {
                    return new LDAPMessage(messageID, new CompareResponseProtocolOp(10, referralEntry.getDN(), ListenerMessages.INFO_MEM_HANDLER_REFERRAL_ENCOUNTERED.get(), getReferralURLs(dn, referralEntry)), new Control[0]);
                }
            }
            Entry entry;
            if (dn.isNullDN()) {
                entry = this.generateRootDSE();
            }
            else if (dn.equals(this.subschemaSubentryDN)) {
                entry = this.subschemaSubentryRef.get();
            }
            else {
                entry = this.entryMap.get(dn);
            }
            if (entry == null) {
                return new LDAPMessage(messageID, new CompareResponseProtocolOp(32, this.getMatchedDNString(dn), ListenerMessages.ERR_MEM_HANDLER_COMPARE_NO_SUCH_ENTRY.get(request.getDN()), null), new Control[0]);
            }
            try {
                handleAssertionRequestControl(controlMap, entry);
                this.handleProxiedAuthControl(controlMap);
            }
            catch (final LDAPException le3) {
                Debug.debugException(le3);
                return new LDAPMessage(messageID, new CompareResponseProtocolOp(le3.getResultCode().intValue(), null, le3.getMessage(), null), new Control[0]);
            }
            int resultCode;
            if (entry.hasAttributeValue(request.getAttributeName(), request.getAssertionValue().getValue())) {
                resultCode = 6;
            }
            else {
                resultCode = 5;
            }
            return new LDAPMessage(messageID, new CompareResponseProtocolOp(resultCode, null, null, null), responseControls);
        }
    }
    
    public LDAPResult delete(final DeleteRequest deleteRequest) throws LDAPException {
        final ArrayList<Control> requestControlList = new ArrayList<Control>(deleteRequest.getControlList());
        requestControlList.add(new Control("1.3.6.1.4.1.30221.2.5.18", false));
        final LDAPMessage responseMessage = this.processDeleteRequest(1, new DeleteRequestProtocolOp(deleteRequest.getDN()), requestControlList);
        final DeleteResponseProtocolOp deleteResponse = responseMessage.getDeleteResponseProtocolOp();
        final LDAPResult ldapResult = new LDAPResult(responseMessage.getMessageID(), ResultCode.valueOf(deleteResponse.getResultCode()), deleteResponse.getDiagnosticMessage(), deleteResponse.getMatchedDN(), deleteResponse.getReferralURLs(), responseMessage.getControls());
        switch (deleteResponse.getResultCode()) {
            case 0:
            case 16654: {
                return ldapResult;
            }
            default: {
                throw new LDAPException(ldapResult);
            }
        }
    }
    
    @Override
    public LDAPMessage processDeleteRequest(final int messageID, final DeleteRequestProtocolOp request, final List<Control> controls) {
        synchronized (this.entryMap) {
            this.sleepBeforeProcessing();
            Map<String, Control> controlMap;
            try {
                controlMap = RequestControlPreProcessor.processControls((byte)74, controls);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                return new LDAPMessage(messageID, new DeleteResponseProtocolOp(le.getResultCode().intValue(), null, le.getMessage(), null), new Control[0]);
            }
            final ArrayList<Control> responseControls = new ArrayList<Control>(1);
            final boolean isInternalOp = controlMap.containsKey("1.3.6.1.4.1.30221.2.5.18");
            if (!isInternalOp && !this.config.getAllowedOperationTypes().contains(OperationType.DELETE)) {
                return new LDAPMessage(messageID, new DeleteResponseProtocolOp(53, null, ListenerMessages.ERR_MEM_HANDLER_DELETE_NOT_ALLOWED.get(), null), new Control[0]);
            }
            if (this.authenticatedDN.isNullDN() && this.config.getAuthenticationRequiredOperationTypes().contains(OperationType.DELETE)) {
                return new LDAPMessage(messageID, new DeleteResponseProtocolOp(50, null, ListenerMessages.ERR_MEM_HANDLER_DELETE_REQUIRES_AUTH.get(), null), new Control[0]);
            }
            try {
                final ASN1OctetString txnID = this.processTransactionRequest(messageID, request, controlMap);
                if (txnID != null) {
                    return new LDAPMessage(messageID, new DeleteResponseProtocolOp(0, null, ListenerMessages.INFO_MEM_HANDLER_OP_IN_TXN.get(txnID.stringValue()), null), new Control[0]);
                }
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                return new LDAPMessage(messageID, new DeleteResponseProtocolOp(le2.getResultCode().intValue(), le2.getMatchedDN(), le2.getDiagnosticMessage(), StaticUtils.toList(le2.getReferralURLs())), le2.getResponseControls());
            }
            DN dn;
            try {
                dn = new DN(request.getDN(), this.schemaRef.get());
            }
            catch (final LDAPException le3) {
                Debug.debugException(le3);
                return new LDAPMessage(messageID, new DeleteResponseProtocolOp(34, null, ListenerMessages.ERR_MEM_HANDLER_DELETE_MALFORMED_DN.get(request.getDN(), le3.getMessage()), null), new Control[0]);
            }
            if (!controlMap.containsKey("2.16.840.1.113730.3.4.2")) {
                final Entry referralEntry = this.findNearestReferral(dn);
                if (referralEntry != null) {
                    return new LDAPMessage(messageID, new DeleteResponseProtocolOp(10, referralEntry.getDN(), ListenerMessages.INFO_MEM_HANDLER_REFERRAL_ENCOUNTERED.get(), getReferralURLs(dn, referralEntry)), new Control[0]);
                }
            }
            if (dn.isNullDN()) {
                return new LDAPMessage(messageID, new DeleteResponseProtocolOp(53, null, ListenerMessages.ERR_MEM_HANDLER_DELETE_ROOT_DSE.get(), null), new Control[0]);
            }
            if (dn.equals(this.subschemaSubentryDN)) {
                return new LDAPMessage(messageID, new DeleteResponseProtocolOp(53, null, ListenerMessages.ERR_MEM_HANDLER_DELETE_SCHEMA.get(this.subschemaSubentryDN.toString()), null), new Control[0]);
            }
            if (dn.isDescendantOf(this.changeLogBaseDN, true)) {
                return new LDAPMessage(messageID, new DeleteResponseProtocolOp(53, null, ListenerMessages.ERR_MEM_HANDLER_DELETE_CHANGELOG.get(request.getDN()), null), new Control[0]);
            }
            final Entry entry = this.entryMap.get(dn);
            if (entry == null) {
                return new LDAPMessage(messageID, new DeleteResponseProtocolOp(32, this.getMatchedDNString(dn), ListenerMessages.ERR_MEM_HANDLER_DELETE_NO_SUCH_ENTRY.get(request.getDN()), null), new Control[0]);
            }
            final ArrayList<DN> subordinateDNs = new ArrayList<DN>(this.entryMap.size());
            for (final DN mapEntryDN : this.entryMap.keySet()) {
                if (mapEntryDN.isDescendantOf(dn, false)) {
                    subordinateDNs.add(mapEntryDN);
                }
            }
            if (!subordinateDNs.isEmpty() && !controlMap.containsKey("1.2.840.113556.1.4.805")) {
                return new LDAPMessage(messageID, new DeleteResponseProtocolOp(66, null, ListenerMessages.ERR_MEM_HANDLER_DELETE_HAS_SUBORDINATES.get(request.getDN()), null), new Control[0]);
            }
            DN authzDN;
            try {
                handleAssertionRequestControl(controlMap, entry);
                final PreReadResponseControl preReadResponse = this.handlePreReadControl(controlMap, entry);
                if (preReadResponse != null) {
                    responseControls.add(preReadResponse);
                }
                authzDN = this.handleProxiedAuthControl(controlMap);
            }
            catch (final LDAPException le4) {
                Debug.debugException(le4);
                return new LDAPMessage(messageID, new DeleteResponseProtocolOp(le4.getResultCode().intValue(), null, le4.getMessage(), null), new Control[0]);
            }
            for (int i = subordinateDNs.size() - 1; i >= 0; --i) {
                final DN subordinateDN = subordinateDNs.get(i);
                final Entry subEntry = this.entryMap.remove(subordinateDN);
                this.indexDelete(subEntry);
                this.addDeleteChangeLogEntry(subEntry, authzDN);
                this.handleReferentialIntegrityDelete(subordinateDN);
            }
            this.entryMap.remove(dn);
            this.indexDelete(entry);
            this.addDeleteChangeLogEntry(entry, authzDN);
            this.handleReferentialIntegrityDelete(dn);
            return new LDAPMessage(messageID, new DeleteResponseProtocolOp(0, null, null, null), responseControls);
        }
    }
    
    private void handleReferentialIntegrityDelete(final DN dn) {
        if (this.referentialIntegrityAttributes.isEmpty()) {
            return;
        }
        final ArrayList<DN> entryDNs = new ArrayList<DN>(this.entryMap.keySet());
        for (final DN mapDN : entryDNs) {
            final ReadOnlyEntry e = this.entryMap.get(mapDN);
            boolean referenceFound = false;
            final Schema schema = this.schemaRef.get();
            for (final String attrName : this.referentialIntegrityAttributes) {
                final Attribute a = e.getAttribute(attrName, schema);
                if (a != null && a.hasValue(dn.toNormalizedString(), DistinguishedNameMatchingRule.getInstance())) {
                    referenceFound = true;
                    break;
                }
            }
            if (referenceFound) {
                final Entry copy = e.duplicate();
                for (final String attrName2 : this.referentialIntegrityAttributes) {
                    copy.removeAttributeValue(attrName2, dn.toNormalizedString(), DistinguishedNameMatchingRule.getInstance());
                }
                this.entryMap.put(mapDN, new ReadOnlyEntry(copy));
                this.indexDelete(e);
                this.indexAdd(copy);
            }
        }
    }
    
    @Override
    public LDAPMessage processExtendedRequest(final int messageID, final ExtendedRequestProtocolOp request, final List<Control> controls) {
        synchronized (this.entryMap) {
            this.sleepBeforeProcessing();
            boolean isInternalOp = false;
            for (final Control c : controls) {
                if (c.getOID().equals("1.3.6.1.4.1.30221.2.5.18")) {
                    isInternalOp = true;
                    break;
                }
            }
            if (!isInternalOp && !this.config.getAllowedOperationTypes().contains(OperationType.EXTENDED)) {
                return new LDAPMessage(messageID, new ExtendedResponseProtocolOp(53, null, ListenerMessages.ERR_MEM_HANDLER_EXTENDED_NOT_ALLOWED.get(), null, null, null), new Control[0]);
            }
            if (this.authenticatedDN.isNullDN() && this.config.getAuthenticationRequiredOperationTypes().contains(OperationType.EXTENDED)) {
                return new LDAPMessage(messageID, new ExtendedResponseProtocolOp(50, null, ListenerMessages.ERR_MEM_HANDLER_EXTENDED_REQUIRES_AUTH.get(), null, null, null), new Control[0]);
            }
            final String oid = request.getOID();
            final InMemoryExtendedOperationHandler handler = this.extendedRequestHandlers.get(oid);
            if (handler == null) {
                return new LDAPMessage(messageID, new ExtendedResponseProtocolOp(53, null, ListenerMessages.ERR_MEM_HANDLER_EXTENDED_OP_NOT_SUPPORTED.get(oid), null, null, null), new Control[0]);
            }
            try {
                final Control[] controlArray = new Control[controls.size()];
                controls.toArray(controlArray);
                final ExtendedRequest extendedRequest = new ExtendedRequest(oid, request.getValue(), controlArray);
                final ExtendedResult extendedResult = handler.processExtendedOperation(this, messageID, extendedRequest);
                return new LDAPMessage(messageID, new ExtendedResponseProtocolOp(extendedResult.getResultCode().intValue(), extendedResult.getMatchedDN(), extendedResult.getDiagnosticMessage(), Arrays.asList(extendedResult.getReferralURLs()), extendedResult.getOID(), extendedResult.getValue()), extendedResult.getResponseControls());
            }
            catch (final Exception e) {
                Debug.debugException(e);
                return new LDAPMessage(messageID, new ExtendedResponseProtocolOp(80, null, ListenerMessages.ERR_MEM_HANDLER_EXTENDED_OP_FAILURE.get(StaticUtils.getExceptionMessage(e)), null, null, null), new Control[0]);
            }
        }
    }
    
    public LDAPResult modify(final ModifyRequest modifyRequest) throws LDAPException {
        final ArrayList<Control> requestControlList = new ArrayList<Control>(modifyRequest.getControlList());
        requestControlList.add(new Control("1.3.6.1.4.1.30221.2.5.18", false));
        final LDAPMessage responseMessage = this.processModifyRequest(1, new ModifyRequestProtocolOp(modifyRequest.getDN(), modifyRequest.getModifications()), requestControlList);
        final ModifyResponseProtocolOp modifyResponse = responseMessage.getModifyResponseProtocolOp();
        final LDAPResult ldapResult = new LDAPResult(responseMessage.getMessageID(), ResultCode.valueOf(modifyResponse.getResultCode()), modifyResponse.getDiagnosticMessage(), modifyResponse.getMatchedDN(), modifyResponse.getReferralURLs(), responseMessage.getControls());
        switch (modifyResponse.getResultCode()) {
            case 0:
            case 16654: {
                return ldapResult;
            }
            default: {
                throw new LDAPException(ldapResult);
            }
        }
    }
    
    @Override
    public LDAPMessage processModifyRequest(final int messageID, final ModifyRequestProtocolOp request, final List<Control> controls) {
        synchronized (this.entryMap) {
            this.sleepBeforeProcessing();
            Map<String, Control> controlMap;
            try {
                controlMap = RequestControlPreProcessor.processControls((byte)102, controls);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                return new LDAPMessage(messageID, new ModifyResponseProtocolOp(le.getResultCode().intValue(), null, le.getMessage(), null), new Control[0]);
            }
            final ArrayList<Control> responseControls = new ArrayList<Control>(1);
            final boolean isInternalOp = controlMap.containsKey("1.3.6.1.4.1.30221.2.5.18");
            if (!isInternalOp && !this.config.getAllowedOperationTypes().contains(OperationType.MODIFY)) {
                return new LDAPMessage(messageID, new ModifyResponseProtocolOp(53, null, ListenerMessages.ERR_MEM_HANDLER_MODIFY_NOT_ALLOWED.get(), null), new Control[0]);
            }
            if (this.authenticatedDN.isNullDN() && this.config.getAuthenticationRequiredOperationTypes().contains(OperationType.MODIFY)) {
                return new LDAPMessage(messageID, new ModifyResponseProtocolOp(50, null, ListenerMessages.ERR_MEM_HANDLER_MODIFY_REQUIRES_AUTH.get(), null), new Control[0]);
            }
            try {
                final ASN1OctetString txnID = this.processTransactionRequest(messageID, request, controlMap);
                if (txnID != null) {
                    return new LDAPMessage(messageID, new ModifyResponseProtocolOp(0, null, ListenerMessages.INFO_MEM_HANDLER_OP_IN_TXN.get(txnID.stringValue()), null), new Control[0]);
                }
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                return new LDAPMessage(messageID, new ModifyResponseProtocolOp(le2.getResultCode().intValue(), le2.getMatchedDN(), le2.getDiagnosticMessage(), StaticUtils.toList(le2.getReferralURLs())), le2.getResponseControls());
            }
            final Schema schema = this.schemaRef.get();
            DN dn;
            try {
                dn = new DN(request.getDN(), schema);
            }
            catch (final LDAPException le3) {
                Debug.debugException(le3);
                return new LDAPMessage(messageID, new ModifyResponseProtocolOp(34, null, ListenerMessages.ERR_MEM_HANDLER_MOD_MALFORMED_DN.get(request.getDN(), le3.getMessage()), null), new Control[0]);
            }
            if (!controlMap.containsKey("2.16.840.1.113730.3.4.2")) {
                final Entry referralEntry = this.findNearestReferral(dn);
                if (referralEntry != null) {
                    return new LDAPMessage(messageID, new ModifyResponseProtocolOp(10, referralEntry.getDN(), ListenerMessages.INFO_MEM_HANDLER_REFERRAL_ENCOUNTERED.get(), getReferralURLs(dn, referralEntry)), new Control[0]);
                }
            }
            if (dn.isNullDN()) {
                return new LDAPMessage(messageID, new ModifyResponseProtocolOp(53, null, ListenerMessages.ERR_MEM_HANDLER_MOD_ROOT_DSE.get(), null), new Control[0]);
            }
            Label_0644: {
                if (dn.equals(this.subschemaSubentryDN)) {
                    try {
                        this.validateSchemaMods(request);
                        break Label_0644;
                    }
                    catch (final LDAPException le3) {
                        return new LDAPMessage(messageID, new ModifyResponseProtocolOp(le3.getResultCode().intValue(), le3.getMatchedDN(), le3.getMessage(), null), new Control[0]);
                    }
                }
                if (dn.isDescendantOf(this.changeLogBaseDN, true)) {
                    return new LDAPMessage(messageID, new ModifyResponseProtocolOp(53, null, ListenerMessages.ERR_MEM_HANDLER_MOD_CHANGELOG.get(request.getDN()), null), new Control[0]);
                }
            }
            Entry entry = this.entryMap.get(dn);
            if (entry == null) {
                if (!dn.equals(this.subschemaSubentryDN)) {
                    return new LDAPMessage(messageID, new ModifyResponseProtocolOp(32, this.getMatchedDNString(dn), ListenerMessages.ERR_MEM_HANDLER_MOD_NO_SUCH_ENTRY.get(request.getDN()), null), new Control[0]);
                }
                entry = this.subschemaSubentryRef.get().duplicate();
            }
            final ReadOnlyEntry readOnlyEntry = new ReadOnlyEntry(entry);
            final List<Modification> unencodedMods = request.getModifications();
            final ArrayList<Modification> modifications = new ArrayList<Modification>(unencodedMods.size());
            for (final Modification m : unencodedMods) {
                try {
                    modifications.add(this.encodeModificationPasswords(m, readOnlyEntry, unencodedMods));
                }
                catch (final LDAPException le4) {
                    Debug.debugException(le4);
                    if (le4.getResultCode().isClientSideResultCode()) {
                        return new LDAPMessage(messageID, new ModifyResponseProtocolOp(53, le4.getMatchedDN(), le4.getMessage(), null), new Control[0]);
                    }
                    return new LDAPMessage(messageID, new ModifyResponseProtocolOp(le4.getResultCode().intValue(), le4.getMatchedDN(), le4.getMessage(), null), new Control[0]);
                }
            }
            Entry modifiedEntry;
            try {
                modifiedEntry = Entry.applyModifications(entry, controlMap.containsKey("1.2.840.113556.1.4.1413"), modifications);
            }
            catch (final LDAPException le5) {
                Debug.debugException(le5);
                return new LDAPMessage(messageID, new ModifyResponseProtocolOp(le5.getResultCode().intValue(), null, ListenerMessages.ERR_MEM_HANDLER_MOD_FAILED.get(request.getDN(), le5.getMessage()), null), new Control[0]);
            }
            final EntryValidator entryValidator = this.entryValidatorRef.get();
            if (entryValidator != null) {
                final ArrayList<String> invalidReasons = new ArrayList<String>(1);
                if (!entryValidator.entryIsValid(modifiedEntry, invalidReasons)) {
                    return new LDAPMessage(messageID, new ModifyResponseProtocolOp(65, null, ListenerMessages.ERR_MEM_HANDLER_MOD_VIOLATES_SCHEMA.get(request.getDN(), StaticUtils.concatenateStrings(invalidReasons)), null), new Control[0]);
                }
                for (final Modification i : modifications) {
                    final Attribute a = i.getAttribute();
                    final String baseName = a.getBaseName();
                    final AttributeTypeDefinition at = schema.getAttributeType(baseName);
                    if (!isInternalOp && at != null && at.isNoUserModification()) {
                        return new LDAPMessage(messageID, new ModifyResponseProtocolOp(19, null, ListenerMessages.ERR_MEM_HANDLER_MOD_NO_USER_MOD.get(request.getDN(), a.getName()), null), new Control[0]);
                    }
                }
            }
            DN authzDN;
            try {
                handleAssertionRequestControl(controlMap, entry);
                authzDN = this.handleProxiedAuthControl(controlMap);
            }
            catch (final LDAPException le6) {
                Debug.debugException(le6);
                return new LDAPMessage(messageID, new ModifyResponseProtocolOp(le6.getResultCode().intValue(), null, le6.getMessage(), null), new Control[0]);
            }
            if (this.generateOperationalAttributes) {
                modifiedEntry.setAttribute(new Attribute("modifiersName", DistinguishedNameMatchingRule.getInstance(), authzDN.toString()));
                modifiedEntry.setAttribute(new Attribute("modifyTimestamp", GeneralizedTimeMatchingRule.getInstance(), StaticUtils.encodeGeneralizedTime(new Date())));
            }
            final PreReadResponseControl preReadResponse = this.handlePreReadControl(controlMap, entry);
            if (preReadResponse != null) {
                responseControls.add(preReadResponse);
            }
            final PostReadResponseControl postReadResponse = this.handlePostReadControl(controlMap, modifiedEntry);
            if (postReadResponse != null) {
                responseControls.add(postReadResponse);
            }
            if (dn.equals(this.subschemaSubentryDN)) {
                final Schema newSchema = new Schema(modifiedEntry);
                this.subschemaSubentryRef.set(new ReadOnlyEntry(modifiedEntry));
                this.schemaRef.set(newSchema);
                this.entryValidatorRef.set(new EntryValidator(newSchema));
            }
            else {
                this.entryMap.put(dn, new ReadOnlyEntry(modifiedEntry));
                this.indexDelete(entry);
                this.indexAdd(modifiedEntry);
            }
            this.addChangeLogEntry(request, authzDN);
            return new LDAPMessage(messageID, new ModifyResponseProtocolOp(0, null, null, null), responseControls);
        }
    }
    
    private Modification encodeModificationPasswords(final Modification mod, final ReadOnlyEntry entry, final List<Modification> mods) throws LDAPException {
        final ASN1OctetString[] originalValues = mod.getRawValues();
        if (originalValues.length == 0) {
            return mod;
        }
        if (this.extendedPasswordAttributes.isEmpty() || this.passwordEncoders.isEmpty()) {
            return mod;
        }
        boolean isPasswordAttribute = false;
        for (final String passwordAttribute : this.extendedPasswordAttributes) {
            if (mod.getAttribute().getBaseName().equalsIgnoreCase(passwordAttribute)) {
                isPasswordAttribute = true;
                break;
            }
        }
        if (!isPasswordAttribute) {
            return mod;
        }
        final ASN1OctetString[] newValues = new ASN1OctetString[originalValues.length];
        for (int i = 0; i < originalValues.length; ++i) {
            newValues[i] = this.encodeModValue(originalValues[i], mod, entry, mods);
        }
        return new Modification(mod.getModificationType(), mod.getAttributeName(), newValues);
    }
    
    private ASN1OctetString encodeModValue(final ASN1OctetString value, final Modification mod, final ReadOnlyEntry entry, final List<Modification> mods) throws LDAPException {
        for (final InMemoryPasswordEncoder encoder : this.passwordEncoders) {
            if (encoder.passwordStartsWithPrefix(value)) {
                encoder.ensurePreEncodedPasswordAppearsValid(value, entry, mods);
                return value;
            }
        }
        final ModificationType modificationType = mod.getModificationType();
        if (modificationType == ModificationType.ADD || modificationType == ModificationType.REPLACE) {
            if (this.primaryPasswordEncoder == null) {
                return value;
            }
            return this.primaryPasswordEncoder.encodePassword(value, entry, mods);
        }
        else {
            if (modificationType != ModificationType.DELETE) {
                return value;
            }
            final Attribute existingAttribute = entry.getAttribute(mod.getAttributeName());
            if (existingAttribute == null) {
                return value;
            }
            for (final ASN1OctetString existingValue : existingAttribute.getRawValues()) {
                if (value.equalsIgnoreType(existingValue)) {
                    return value;
                }
                for (final InMemoryPasswordEncoder encoder2 : this.passwordEncoders) {
                    if (encoder2.clearPasswordMatchesEncodedPassword(value, existingValue, entry)) {
                        return existingValue;
                    }
                }
            }
            return value;
        }
    }
    
    private void validateSchemaMods(final ModifyRequestProtocolOp request) throws LDAPException {
        if (this.schemaRef.get() == null) {
            throw new LDAPException(ResultCode.UNWILLING_TO_PERFORM, ListenerMessages.ERR_MEM_HANDLER_MOD_SCHEMA.get(this.subschemaSubentryDN.toString()));
        }
        for (final Modification m : request.getModifications()) {
            final String attrName = m.getAttributeName();
            if (attrName.equalsIgnoreCase("ldapSyntaxes") || attrName.equalsIgnoreCase("matchingRules")) {
                throw new LDAPException(ResultCode.UNWILLING_TO_PERFORM, ListenerMessages.ERR_MEM_HANDLER_MOD_SCHEMA_DISALLOWED_ATTR.get(attrName));
            }
            if (attrName.equalsIgnoreCase("attributeTypes")) {
                if (m.getModificationType() == ModificationType.ADD) {
                    for (final String value : m.getValues()) {
                        new AttributeTypeDefinition(value);
                    }
                }
                else {
                    if (m.getModificationType() != ModificationType.DELETE) {
                        throw new LDAPException(ResultCode.UNWILLING_TO_PERFORM, ListenerMessages.ERR_MEM_HANDLER_MOD_SCHEMA_DISALLOWED_MOD_TYPE.get(m.getModificationType().getName(), attrName));
                    }
                    continue;
                }
            }
            else if (attrName.equalsIgnoreCase("objectClasses")) {
                if (m.getModificationType() == ModificationType.ADD) {
                    for (final String value : m.getValues()) {
                        new ObjectClassDefinition(value);
                    }
                }
                else {
                    if (m.getModificationType() != ModificationType.DELETE) {
                        throw new LDAPException(ResultCode.UNWILLING_TO_PERFORM, ListenerMessages.ERR_MEM_HANDLER_MOD_SCHEMA_DISALLOWED_MOD_TYPE.get(m.getModificationType().getName(), attrName));
                    }
                    continue;
                }
            }
            else if (attrName.equalsIgnoreCase("nameForms")) {
                if (m.getModificationType() == ModificationType.ADD) {
                    for (final String value : m.getValues()) {
                        new NameFormDefinition(value);
                    }
                }
                else {
                    if (m.getModificationType() != ModificationType.DELETE) {
                        throw new LDAPException(ResultCode.UNWILLING_TO_PERFORM, ListenerMessages.ERR_MEM_HANDLER_MOD_SCHEMA_DISALLOWED_MOD_TYPE.get(m.getModificationType().getName(), attrName));
                    }
                    continue;
                }
            }
            else if (attrName.equalsIgnoreCase("dITContentRules")) {
                if (m.getModificationType() == ModificationType.ADD) {
                    for (final String value : m.getValues()) {
                        new DITContentRuleDefinition(value);
                    }
                }
                else {
                    if (m.getModificationType() != ModificationType.DELETE) {
                        throw new LDAPException(ResultCode.UNWILLING_TO_PERFORM, ListenerMessages.ERR_MEM_HANDLER_MOD_SCHEMA_DISALLOWED_MOD_TYPE.get(m.getModificationType().getName(), attrName));
                    }
                    continue;
                }
            }
            else if (attrName.equalsIgnoreCase("dITStructureRules")) {
                if (m.getModificationType() == ModificationType.ADD) {
                    for (final String value : m.getValues()) {
                        new DITStructureRuleDefinition(value);
                    }
                }
                else {
                    if (m.getModificationType() != ModificationType.DELETE) {
                        throw new LDAPException(ResultCode.UNWILLING_TO_PERFORM, ListenerMessages.ERR_MEM_HANDLER_MOD_SCHEMA_DISALLOWED_MOD_TYPE.get(m.getModificationType().getName(), attrName));
                    }
                    continue;
                }
            }
            else {
                if (!attrName.equalsIgnoreCase("matchingRuleUse")) {
                    continue;
                }
                if (m.getModificationType() == ModificationType.ADD) {
                    for (final String value : m.getValues()) {
                        new MatchingRuleUseDefinition(value);
                    }
                }
                else {
                    if (m.getModificationType() != ModificationType.DELETE) {
                        throw new LDAPException(ResultCode.UNWILLING_TO_PERFORM, ListenerMessages.ERR_MEM_HANDLER_MOD_SCHEMA_DISALLOWED_MOD_TYPE.get(m.getModificationType().getName(), attrName));
                    }
                    continue;
                }
            }
        }
    }
    
    public LDAPResult modifyDN(final ModifyDNRequest modifyDNRequest) throws LDAPException {
        final ArrayList<Control> requestControlList = new ArrayList<Control>(modifyDNRequest.getControlList());
        requestControlList.add(new Control("1.3.6.1.4.1.30221.2.5.18", false));
        final LDAPMessage responseMessage = this.processModifyDNRequest(1, new ModifyDNRequestProtocolOp(modifyDNRequest.getDN(), modifyDNRequest.getNewRDN(), modifyDNRequest.deleteOldRDN(), modifyDNRequest.getNewSuperiorDN()), requestControlList);
        final ModifyDNResponseProtocolOp modifyDNResponse = responseMessage.getModifyDNResponseProtocolOp();
        final LDAPResult ldapResult = new LDAPResult(responseMessage.getMessageID(), ResultCode.valueOf(modifyDNResponse.getResultCode()), modifyDNResponse.getDiagnosticMessage(), modifyDNResponse.getMatchedDN(), modifyDNResponse.getReferralURLs(), responseMessage.getControls());
        switch (modifyDNResponse.getResultCode()) {
            case 0:
            case 16654: {
                return ldapResult;
            }
            default: {
                throw new LDAPException(ldapResult);
            }
        }
    }
    
    @Override
    public LDAPMessage processModifyDNRequest(final int messageID, final ModifyDNRequestProtocolOp request, final List<Control> controls) {
        synchronized (this.entryMap) {
            this.sleepBeforeProcessing();
            Map<String, Control> controlMap;
            try {
                controlMap = RequestControlPreProcessor.processControls((byte)108, controls);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(le.getResultCode().intValue(), null, le.getMessage(), null), new Control[0]);
            }
            final ArrayList<Control> responseControls = new ArrayList<Control>(1);
            final boolean isInternalOp = controlMap.containsKey("1.3.6.1.4.1.30221.2.5.18");
            if (!isInternalOp && !this.config.getAllowedOperationTypes().contains(OperationType.MODIFY_DN)) {
                return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(53, null, ListenerMessages.ERR_MEM_HANDLER_MODIFY_DN_NOT_ALLOWED.get(), null), new Control[0]);
            }
            if (this.authenticatedDN.isNullDN() && this.config.getAuthenticationRequiredOperationTypes().contains(OperationType.MODIFY_DN)) {
                return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(50, null, ListenerMessages.ERR_MEM_HANDLER_MODIFY_DN_REQUIRES_AUTH.get(), null), new Control[0]);
            }
            try {
                final ASN1OctetString txnID = this.processTransactionRequest(messageID, request, controlMap);
                if (txnID != null) {
                    return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(0, null, ListenerMessages.INFO_MEM_HANDLER_OP_IN_TXN.get(txnID.stringValue()), null), new Control[0]);
                }
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(le2.getResultCode().intValue(), le2.getMatchedDN(), le2.getDiagnosticMessage(), StaticUtils.toList(le2.getReferralURLs())), le2.getResponseControls());
            }
            final Schema schema = this.schemaRef.get();
            DN dn;
            try {
                dn = new DN(request.getDN(), schema);
            }
            catch (final LDAPException le3) {
                Debug.debugException(le3);
                return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(34, null, ListenerMessages.ERR_MEM_HANDLER_MOD_DN_MALFORMED_DN.get(request.getDN(), le3.getMessage()), null), new Control[0]);
            }
            RDN newRDN;
            try {
                newRDN = new RDN(request.getNewRDN(), schema);
            }
            catch (final LDAPException le4) {
                Debug.debugException(le4);
                return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(34, null, ListenerMessages.ERR_MEM_HANDLER_MOD_DN_MALFORMED_NEW_RDN.get(request.getDN(), request.getNewRDN(), le4.getMessage()), null), new Control[0]);
            }
            final String newSuperiorString = request.getNewSuperiorDN();
            DN newSuperiorDN;
            if (newSuperiorString == null) {
                newSuperiorDN = null;
            }
            else {
                try {
                    newSuperiorDN = new DN(newSuperiorString, schema);
                }
                catch (final LDAPException le5) {
                    Debug.debugException(le5);
                    return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(34, null, ListenerMessages.ERR_MEM_HANDLER_MOD_DN_MALFORMED_NEW_SUPERIOR.get(request.getDN(), request.getNewSuperiorDN(), le5.getMessage()), null), new Control[0]);
                }
            }
            if (!controlMap.containsKey("2.16.840.1.113730.3.4.2")) {
                final Entry referralEntry = this.findNearestReferral(dn);
                if (referralEntry != null) {
                    return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(10, referralEntry.getDN(), ListenerMessages.INFO_MEM_HANDLER_REFERRAL_ENCOUNTERED.get(), getReferralURLs(dn, referralEntry)), new Control[0]);
                }
            }
            if (dn.isNullDN()) {
                return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(53, null, ListenerMessages.ERR_MEM_HANDLER_MOD_DN_ROOT_DSE.get(), null), new Control[0]);
            }
            if (dn.equals(this.subschemaSubentryDN)) {
                return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(53, null, ListenerMessages.ERR_MEM_HANDLER_MOD_DN_SOURCE_IS_SCHEMA.get(), null), new Control[0]);
            }
            if (dn.isDescendantOf(this.changeLogBaseDN, true)) {
                return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(53, null, ListenerMessages.ERR_MEM_HANDLER_MOD_DN_SOURCE_IS_CHANGELOG.get(), null), new Control[0]);
            }
            DN newDN;
            if (newSuperiorDN == null) {
                final DN originalParent = dn.getParent();
                if (originalParent == null) {
                    newDN = new DN(new RDN[] { newRDN });
                }
                else {
                    newDN = new DN(newRDN, originalParent);
                }
            }
            else {
                newDN = new DN(newRDN, newSuperiorDN);
            }
            if (newDN.equals(dn)) {
                return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(53, null, ListenerMessages.ERR_MEM_HANDLER_MOD_DN_NEW_DN_SAME_AS_OLD.get(request.getDN()), null), new Control[0]);
            }
            if (!controlMap.containsKey("2.16.840.1.113730.3.4.2")) {
                final Entry referralEntry2 = this.findNearestReferral(newDN);
                if (referralEntry2 != null) {
                    return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(53, referralEntry2.getDN(), ListenerMessages.ERR_MEM_HANDLER_MOD_DN_NEW_DN_BELOW_REFERRAL.get(request.getDN(), referralEntry2.getDN().toString(), newDN.toString()), null), new Control[0]);
                }
            }
            final Entry originalEntry = this.entryMap.get(dn);
            if (originalEntry == null) {
                return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(32, this.getMatchedDNString(dn), ListenerMessages.ERR_MEM_HANDLER_MOD_DN_NO_SUCH_ENTRY.get(request.getDN()), null), new Control[0]);
            }
            if (newDN.equals(this.subschemaSubentryDN)) {
                return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(68, null, ListenerMessages.ERR_MEM_HANDLER_MOD_DN_TARGET_IS_SCHEMA.get(request.getDN(), newDN.toString()), null), new Control[0]);
            }
            if (newDN.isDescendantOf(this.changeLogBaseDN, true)) {
                return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(53, null, ListenerMessages.ERR_MEM_HANDLER_MOD_DN_TARGET_IS_CHANGELOG.get(request.getDN(), newDN.toString()), null), new Control[0]);
            }
            if (this.entryMap.containsKey(newDN)) {
                return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(68, null, ListenerMessages.ERR_MEM_HANDLER_MOD_DN_TARGET_ALREADY_EXISTS.get(request.getDN(), newDN.toString()), null), new Control[0]);
            }
            if (!this.baseDNs.contains(newDN)) {
                final DN newParent = newDN.getParent();
                if (newParent == null || !this.entryMap.containsKey(newParent)) {
                    return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(32, this.getMatchedDNString(newDN), ListenerMessages.ERR_MEM_HANDLER_MOD_DN_PARENT_DOESNT_EXIST.get(request.getDN(), newDN.toString()), null), new Control[0]);
                }
            }
            final RDN originalRDN = dn.getRDN();
            final Entry updatedEntry = originalEntry.duplicate();
            updatedEntry.setDN(newDN);
            if (request.deleteOldRDN()) {
                final String[] oldRDNNames = originalRDN.getAttributeNames();
                final byte[][] oldRDNValues = originalRDN.getByteArrayAttributeValues();
                for (int i = 0; i < oldRDNNames.length; ++i) {
                    updatedEntry.removeAttributeValue(oldRDNNames[i], oldRDNValues[i]);
                }
            }
            final String[] newRDNNames = newRDN.getAttributeNames();
            final byte[][] newRDNValues = newRDN.getByteArrayAttributeValues();
            for (int i = 0; i < newRDNNames.length; ++i) {
                final MatchingRule matchingRule = MatchingRule.selectEqualityMatchingRule(newRDNNames[i], schema);
                updatedEntry.addAttribute(new Attribute(newRDNNames[i], matchingRule, newRDNValues[i]));
            }
            final EntryValidator entryValidator = this.entryValidatorRef.get();
            if (entryValidator != null) {
                final ArrayList<String> invalidReasons = new ArrayList<String>(1);
                if (!entryValidator.entryIsValid(updatedEntry, invalidReasons)) {
                    return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(65, null, ListenerMessages.ERR_MEM_HANDLER_MOD_DN_VIOLATES_SCHEMA.get(request.getDN(), StaticUtils.concatenateStrings(invalidReasons)), null), new Control[0]);
                }
                final String[] oldRDNNames2 = originalRDN.getAttributeNames();
                for (int j = 0; j < oldRDNNames2.length; ++j) {
                    final String name = oldRDNNames2[j];
                    final AttributeTypeDefinition at = schema.getAttributeType(name);
                    if (!isInternalOp && at != null && at.isNoUserModification()) {
                        final byte[] value = originalRDN.getByteArrayAttributeValues()[j];
                        if (!updatedEntry.hasAttributeValue(name, value)) {
                            return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(19, null, ListenerMessages.ERR_MEM_HANDLER_MOD_DN_NO_USER_MOD.get(request.getDN(), name), null), new Control[0]);
                        }
                    }
                }
                for (int j = 0; j < newRDNNames.length; ++j) {
                    final String name = newRDNNames[j];
                    final AttributeTypeDefinition at = schema.getAttributeType(name);
                    if (!isInternalOp && at != null && at.isNoUserModification()) {
                        final byte[] value = newRDN.getByteArrayAttributeValues()[j];
                        if (!originalEntry.hasAttributeValue(name, value)) {
                            return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(19, null, ListenerMessages.ERR_MEM_HANDLER_MOD_DN_NO_USER_MOD.get(request.getDN(), name), null), new Control[0]);
                        }
                    }
                }
            }
            DN authzDN;
            try {
                handleAssertionRequestControl(controlMap, originalEntry);
                authzDN = this.handleProxiedAuthControl(controlMap);
            }
            catch (final LDAPException le6) {
                Debug.debugException(le6);
                return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(le6.getResultCode().intValue(), null, le6.getMessage(), null), new Control[0]);
            }
            if (this.generateOperationalAttributes) {
                updatedEntry.setAttribute(new Attribute("modifiersName", DistinguishedNameMatchingRule.getInstance(), authzDN.toString()));
                updatedEntry.setAttribute(new Attribute("modifyTimestamp", GeneralizedTimeMatchingRule.getInstance(), StaticUtils.encodeGeneralizedTime(new Date())));
                updatedEntry.setAttribute(new Attribute("entryDN", DistinguishedNameMatchingRule.getInstance(), newDN.toNormalizedString()));
            }
            final PreReadResponseControl preReadResponse = this.handlePreReadControl(controlMap, originalEntry);
            if (preReadResponse != null) {
                responseControls.add(preReadResponse);
            }
            final PostReadResponseControl postReadResponse = this.handlePostReadControl(controlMap, updatedEntry);
            if (postReadResponse != null) {
                responseControls.add(postReadResponse);
            }
            this.entryMap.remove(dn);
            this.entryMap.put(newDN, new ReadOnlyEntry(updatedEntry));
            this.indexDelete(originalEntry);
            this.indexAdd(updatedEntry);
            final RDN[] oldDNComps = dn.getRDNs();
            final RDN[] newDNComps = newDN.getRDNs();
            final Set<DN> dnSet = new LinkedHashSet<DN>(this.entryMap.keySet());
            for (final DN mapEntryDN : dnSet) {
                if (mapEntryDN.isDescendantOf(dn, false)) {
                    final Entry o = this.entryMap.remove(mapEntryDN);
                    final Entry e = o.duplicate();
                    final RDN[] oldMapEntryComps = mapEntryDN.getRDNs();
                    final int compsToSave = oldMapEntryComps.length - oldDNComps.length;
                    final RDN[] newMapEntryComps = new RDN[compsToSave + newDNComps.length];
                    System.arraycopy(oldMapEntryComps, 0, newMapEntryComps, 0, compsToSave);
                    System.arraycopy(newDNComps, 0, newMapEntryComps, compsToSave, newDNComps.length);
                    final DN newMapEntryDN = new DN(newMapEntryComps);
                    e.setDN(newMapEntryDN);
                    if (this.generateOperationalAttributes) {
                        e.setAttribute(new Attribute("entryDN", DistinguishedNameMatchingRule.getInstance(), newMapEntryDN.toNormalizedString()));
                    }
                    this.entryMap.put(newMapEntryDN, new ReadOnlyEntry(e));
                    this.indexDelete(o);
                    this.indexAdd(e);
                    this.handleReferentialIntegrityModifyDN(mapEntryDN, newMapEntryDN);
                }
            }
            this.addChangeLogEntry(request, authzDN);
            this.handleReferentialIntegrityModifyDN(dn, newDN);
            return new LDAPMessage(messageID, new ModifyDNResponseProtocolOp(0, null, null, null), responseControls);
        }
    }
    
    private void handleReferentialIntegrityModifyDN(final DN oldDN, final DN newDN) {
        if (this.referentialIntegrityAttributes.isEmpty()) {
            return;
        }
        final ArrayList<DN> entryDNs = new ArrayList<DN>(this.entryMap.keySet());
        for (final DN mapDN : entryDNs) {
            final ReadOnlyEntry e = this.entryMap.get(mapDN);
            boolean referenceFound = false;
            final Schema schema = this.schemaRef.get();
            for (final String attrName : this.referentialIntegrityAttributes) {
                final Attribute a = e.getAttribute(attrName, schema);
                if (a != null && a.hasValue(oldDN.toNormalizedString(), DistinguishedNameMatchingRule.getInstance())) {
                    referenceFound = true;
                    break;
                }
            }
            if (referenceFound) {
                final Entry copy = e.duplicate();
                for (final String attrName2 : this.referentialIntegrityAttributes) {
                    if (copy.removeAttributeValue(attrName2, oldDN.toNormalizedString(), DistinguishedNameMatchingRule.getInstance())) {
                        copy.addAttribute(attrName2, newDN.toString());
                    }
                }
                this.entryMap.put(mapDN, new ReadOnlyEntry(copy));
                this.indexDelete(e);
                this.indexAdd(copy);
            }
        }
    }
    
    @Override
    public LDAPMessage processSearchRequest(final int messageID, final SearchRequestProtocolOp request, final List<Control> controls) {
        synchronized (this.entryMap) {
            final List<SearchResultEntry> entryList = new ArrayList<SearchResultEntry>(this.entryMap.size());
            final List<SearchResultReference> referenceList = new ArrayList<SearchResultReference>(this.entryMap.size());
            final LDAPMessage returnMessage = this.processSearchRequest(messageID, request, controls, entryList, referenceList);
            for (final SearchResultEntry e : entryList) {
                try {
                    this.connection.sendSearchResultEntry(messageID, e, e.getControls());
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    return new LDAPMessage(messageID, new SearchResultDoneProtocolOp(le.getResultCode().intValue(), le.getMatchedDN(), le.getDiagnosticMessage(), StaticUtils.toList(le.getReferralURLs())), le.getResponseControls());
                }
            }
            for (final SearchResultReference r : referenceList) {
                try {
                    this.connection.sendSearchResultReference(messageID, new SearchResultReferenceProtocolOp(StaticUtils.toList(r.getReferralURLs())), r.getControls());
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    return new LDAPMessage(messageID, new SearchResultDoneProtocolOp(le.getResultCode().intValue(), le.getMatchedDN(), le.getDiagnosticMessage(), StaticUtils.toList(le.getReferralURLs())), le.getResponseControls());
                }
            }
            return returnMessage;
        }
    }
    
    LDAPMessage processSearchRequest(final int messageID, final SearchRequestProtocolOp request, final List<Control> controls, final List<SearchResultEntry> entryList, final List<SearchResultReference> referenceList) {
        synchronized (this.entryMap) {
            final long processingStartTime = System.currentTimeMillis();
            this.sleepBeforeProcessing();
            try {
                ensureFilterSupported(request.getFilter());
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                return new LDAPMessage(messageID, new SearchResultDoneProtocolOp(le.getResultCode().intValue(), null, le.getMessage(), null), new Control[0]);
            }
            final long timeLimitMillis = 1000L * request.getTimeLimit();
            if (timeLimitMillis > 0L) {
                final long timeLimitExpirationTime = processingStartTime + timeLimitMillis;
                if (System.currentTimeMillis() >= timeLimitExpirationTime) {
                    return new LDAPMessage(messageID, new SearchResultDoneProtocolOp(3, null, ListenerMessages.ERR_MEM_HANDLER_TIME_LIMIT_EXCEEDED.get(), null), new Control[0]);
                }
            }
            Map<String, Control> controlMap;
            try {
                controlMap = RequestControlPreProcessor.processControls((byte)99, controls);
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                return new LDAPMessage(messageID, new SearchResultDoneProtocolOp(le2.getResultCode().intValue(), null, le2.getMessage(), null), new Control[0]);
            }
            final ArrayList<Control> responseControls = new ArrayList<Control>(1);
            final boolean isInternalOp = controlMap.containsKey("1.3.6.1.4.1.30221.2.5.18");
            if (!isInternalOp && !this.config.getAllowedOperationTypes().contains(OperationType.SEARCH)) {
                return new LDAPMessage(messageID, new SearchResultDoneProtocolOp(53, null, ListenerMessages.ERR_MEM_HANDLER_SEARCH_NOT_ALLOWED.get(), null), new Control[0]);
            }
            if (this.authenticatedDN.isNullDN() && this.config.getAuthenticationRequiredOperationTypes().contains(OperationType.SEARCH)) {
                return new LDAPMessage(messageID, new SearchResultDoneProtocolOp(50, null, ListenerMessages.ERR_MEM_HANDLER_SEARCH_REQUIRES_AUTH.get(), null), new Control[0]);
            }
            final Schema schema = this.schemaRef.get();
            DN baseDN;
            try {
                baseDN = new DN(request.getBaseDN(), schema);
            }
            catch (final LDAPException le3) {
                Debug.debugException(le3);
                return new LDAPMessage(messageID, new SearchResultDoneProtocolOp(34, null, ListenerMessages.ERR_MEM_HANDLER_SEARCH_MALFORMED_BASE.get(request.getBaseDN(), le3.getMessage()), null), new Control[0]);
            }
            final boolean hasManageDsaIT = controlMap.containsKey("2.16.840.1.113730.3.4.2");
            if (!hasManageDsaIT) {
                final Entry referralEntry = this.findNearestReferral(baseDN);
                if (referralEntry != null) {
                    return new LDAPMessage(messageID, new SearchResultDoneProtocolOp(10, referralEntry.getDN(), ListenerMessages.INFO_MEM_HANDLER_REFERRAL_ENCOUNTERED.get(), getReferralURLs(baseDN, referralEntry)), new Control[0]);
                }
            }
            boolean includeChangeLog = true;
            Entry baseEntry;
            if (baseDN.isNullDN()) {
                baseEntry = this.generateRootDSE();
                includeChangeLog = false;
            }
            else if (baseDN.equals(this.subschemaSubentryDN)) {
                baseEntry = this.subschemaSubentryRef.get();
            }
            else {
                baseEntry = this.entryMap.get(baseDN);
            }
            if (baseEntry == null) {
                return new LDAPMessage(messageID, new SearchResultDoneProtocolOp(32, this.getMatchedDNString(baseDN), ListenerMessages.ERR_MEM_HANDLER_SEARCH_BASE_DOES_NOT_EXIST.get(request.getBaseDN()), null), new Control[0]);
            }
            try {
                handleAssertionRequestControl(controlMap, baseEntry);
                this.handleProxiedAuthControl(controlMap);
            }
            catch (final LDAPException le4) {
                Debug.debugException(le4);
                return new LDAPMessage(messageID, new SearchResultDoneProtocolOp(le4.getResultCode().intValue(), null, le4.getMessage(), null), new Control[0]);
            }
            final SearchScope scope = request.getScope();
            boolean includeSubEntries;
            boolean includeNonSubEntries;
            if (scope == SearchScope.BASE) {
                includeSubEntries = true;
                includeNonSubEntries = true;
            }
            else if (controlMap.containsKey("1.3.6.1.4.1.7628.5.101.1")) {
                includeSubEntries = true;
                includeNonSubEntries = false;
            }
            else if (baseEntry.hasObjectClass("ldapSubEntry") || baseEntry.hasObjectClass("inheritableLDAPSubEntry")) {
                includeSubEntries = true;
                includeNonSubEntries = true;
            }
            else {
                includeSubEntries = false;
                includeNonSubEntries = true;
            }
            final List<Entry> fullEntryList = new ArrayList<Entry>(this.entryMap.size());
            final Filter filter = request.getFilter();
            if (scope == SearchScope.BASE) {
                try {
                    if (filter.matchesEntry(baseEntry, schema)) {
                        this.processSearchEntry(baseEntry, includeSubEntries, includeNonSubEntries, includeChangeLog, hasManageDsaIT, fullEntryList, referenceList);
                    }
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                }
            }
            else if (scope == SearchScope.ONE && baseDN.isNullDN()) {
                for (final DN dn : this.baseDNs) {
                    final Entry e2 = this.entryMap.get(dn);
                    if (e2 != null) {
                        try {
                            if (!filter.matchesEntry(e2, schema)) {
                                continue;
                            }
                            this.processSearchEntry(e2, includeSubEntries, includeNonSubEntries, includeChangeLog, hasManageDsaIT, fullEntryList, referenceList);
                        }
                        catch (final Exception ex) {
                            Debug.debugException(ex);
                        }
                    }
                }
            }
            else {
                final Set<DN> candidateDNs = this.indexSearch(filter);
                if (candidateDNs == null) {
                    for (final Map.Entry<DN, ReadOnlyEntry> me : this.entryMap.entrySet()) {
                        final DN dn2 = me.getKey();
                        final Entry entry = me.getValue();
                        try {
                            if (!dn2.matchesBaseAndScope(baseDN, scope) || !filter.matchesEntry(entry, schema)) {
                                continue;
                            }
                            this.processSearchEntry(entry, includeSubEntries, includeNonSubEntries, includeChangeLog, hasManageDsaIT, fullEntryList, referenceList);
                        }
                        catch (final Exception e3) {
                            Debug.debugException(e3);
                        }
                    }
                }
                else {
                    for (final DN dn3 : candidateDNs) {
                        try {
                            if (!dn3.matchesBaseAndScope(baseDN, scope)) {
                                continue;
                            }
                            final Entry entry2 = this.entryMap.get(dn3);
                            if (!filter.matchesEntry(entry2, schema)) {
                                continue;
                            }
                            this.processSearchEntry(entry2, includeSubEntries, includeNonSubEntries, includeChangeLog, hasManageDsaIT, fullEntryList, referenceList);
                        }
                        catch (final Exception e4) {
                            Debug.debugException(e4);
                        }
                    }
                }
            }
            final ServerSideSortRequestControl sortRequestControl = controlMap.get("1.2.840.113556.1.4.473");
            if (sortRequestControl != null) {
                final EntrySorter entrySorter = new EntrySorter(false, schema, sortRequestControl.getSortKeys());
                final SortedSet<Entry> sortedEntrySet = entrySorter.sort(fullEntryList);
                fullEntryList.clear();
                fullEntryList.addAll(sortedEntrySet);
                responseControls.add(new ServerSideSortResponseControl(ResultCode.SUCCESS, null));
            }
            final SimplePagedResultsControl pagedResultsControl = controlMap.get("1.2.840.113556.1.4.319");
            if (pagedResultsControl != null) {
                final int totalSize = fullEntryList.size();
                final int pageSize = pagedResultsControl.getSize();
                final ASN1OctetString cookie = pagedResultsControl.getCookie();
                int offset;
                if (cookie == null || cookie.getValueLength() == 0) {
                    offset = 0;
                }
                else {
                    try {
                        final ASN1Integer offsetInteger = ASN1Integer.decodeAsInteger(cookie.getValue());
                        offset = offsetInteger.intValue();
                    }
                    catch (final Exception e3) {
                        Debug.debugException(e3);
                        return new LDAPMessage(messageID, new SearchResultDoneProtocolOp(2, null, ListenerMessages.ERR_MEM_HANDLER_MALFORMED_PAGED_RESULTS_COOKIE.get(), null), responseControls);
                    }
                }
                int pos;
                Iterator<Entry> iterator;
                for (pos = 0, iterator = fullEntryList.iterator(); iterator.hasNext() && pos < offset; ++pos) {
                    iterator.next();
                    iterator.remove();
                }
                for (int keptEntries = 0; iterator.hasNext() && keptEntries < pageSize; ++keptEntries) {
                    iterator.next();
                    ++pos;
                }
                if (iterator.hasNext()) {
                    responseControls.add(new SimplePagedResultsControl(totalSize, new ASN1OctetString(new ASN1Integer(pos).encode()), false));
                    while (iterator.hasNext()) {
                        iterator.next();
                        iterator.remove();
                    }
                }
                else {
                    responseControls.add(new SimplePagedResultsControl(totalSize, new ASN1OctetString(), false));
                }
            }
            final VirtualListViewRequestControl vlvRequest = controlMap.get("2.16.840.1.113730.3.4.9");
            if (vlvRequest != null) {
                final int totalEntries = fullEntryList.size();
                final ASN1OctetString assertionValue = vlvRequest.getAssertionValue();
                int offset = vlvRequest.getTargetOffset();
                if (assertionValue == null) {
                    --offset;
                    offset = Math.max(0, offset);
                    offset = Math.min(fullEntryList.size(), offset);
                }
                else {
                    final SortKey primarySortKey = sortRequestControl.getSortKeys()[0];
                    final Entry testEntry = new Entry("cn=test", schema, new Attribute[] { new Attribute(primarySortKey.getAttributeName(), new ASN1OctetString[] { assertionValue }) });
                    final EntrySorter entrySorter2 = new EntrySorter(false, schema, new SortKey[] { primarySortKey });
                    offset = fullEntryList.size();
                    for (int i = 0; i < fullEntryList.size(); ++i) {
                        if (entrySorter2.compare((Entry)fullEntryList.get(i), testEntry) >= 0) {
                            offset = i;
                            break;
                        }
                    }
                }
                final int beforeCount = Math.max(0, vlvRequest.getBeforeCount());
                final int afterCount = Math.max(0, vlvRequest.getAfterCount());
                final int start = Math.max(0, offset - beforeCount);
                final int end = Math.min(fullEntryList.size(), offset + afterCount + 1);
                int pos2 = 0;
                final Iterator<Entry> iterator2 = fullEntryList.iterator();
                while (iterator2.hasNext()) {
                    iterator2.next();
                    if (pos2 < start || pos2 >= end) {
                        iterator2.remove();
                    }
                    ++pos2;
                }
                responseControls.add(new VirtualListViewResponseControl(offset + 1, totalEntries, ResultCode.SUCCESS, null));
            }
            final AtomicBoolean allUserAttrs = new AtomicBoolean(false);
            final AtomicBoolean allOpAttrs = new AtomicBoolean(false);
            final Map<String, List<List<String>>> returnAttrs = this.processRequestedAttributes(request.getAttributes(), allUserAttrs, allOpAttrs);
            int sizeLimit;
            if (request.getSizeLimit() > 0) {
                sizeLimit = Math.min(request.getSizeLimit(), this.maxSizeLimit);
            }
            else {
                sizeLimit = this.maxSizeLimit;
            }
            int entryCount = 0;
            for (final Entry e5 : fullEntryList) {
                if (++entryCount > sizeLimit) {
                    return new LDAPMessage(messageID, new SearchResultDoneProtocolOp(4, null, ListenerMessages.ERR_MEM_HANDLER_SEARCH_SIZE_LIMIT_EXCEEDED.get(), null), responseControls);
                }
                final Entry trimmedEntry = this.trimForRequestedAttributes(e5, allUserAttrs.get(), allOpAttrs.get(), returnAttrs);
                if (request.typesOnly()) {
                    final Entry typesOnlyEntry = new Entry(trimmedEntry.getDN(), schema);
                    for (final Attribute a : trimmedEntry.getAttributes()) {
                        typesOnlyEntry.addAttribute(new Attribute(a.getName()));
                    }
                    entryList.add(new SearchResultEntry(typesOnlyEntry, new Control[0]));
                }
                else {
                    entryList.add(new SearchResultEntry(trimmedEntry, new Control[0]));
                }
            }
            return new LDAPMessage(messageID, new SearchResultDoneProtocolOp(0, null, null, null), responseControls);
        }
    }
    
    private static void ensureFilterSupported(final Filter filter) throws LDAPException {
        switch (filter.getFilterType()) {
            case -96:
            case -95: {
                for (final Filter component : filter.getComponents()) {
                    ensureFilterSupported(component);
                }
                return;
            }
            case -94: {
                ensureFilterSupported(filter.getNOTComponent());
                return;
            }
            case -121:
            case -93:
            case -92:
            case -91:
            case -90: {
                return;
            }
            case -88: {
                throw new LDAPException(ResultCode.INAPPROPRIATE_MATCHING, ListenerMessages.ERR_MEM_HANDLER_FILTER_UNSUPPORTED_APPROXIMATE_MATCH_FILTER.get());
            }
            case -87: {
                throw new LDAPException(ResultCode.INAPPROPRIATE_MATCHING, ListenerMessages.ERR_MEM_HANDLER_FILTER_UNSUPPORTED_EXTENSIBLE_MATCH_FILTER.get());
            }
            default: {
                throw new LDAPException(ResultCode.INAPPROPRIATE_MATCHING, ListenerMessages.ERR_MEM_HANDLER_FILTER_UNRECOGNIZED_FILTER_TYPE.get(StaticUtils.toHex(filter.getFilterType())));
            }
        }
    }
    
    private void indexAdd(final Entry entry) {
        for (final InMemoryDirectoryServerEqualityAttributeIndex i : this.equalityIndexes.values()) {
            try {
                i.processAdd(entry);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
            }
        }
    }
    
    private void indexDelete(final Entry entry) {
        for (final InMemoryDirectoryServerEqualityAttributeIndex i : this.equalityIndexes.values()) {
            try {
                i.processDelete(entry);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
            }
        }
    }
    
    private Set<DN> indexSearch(final Filter filter) {
        switch (filter.getFilterType()) {
            case -96: {
                final Filter[] comps = filter.getComponents();
                if (comps.length == 0) {
                    return null;
                }
                if (comps.length == 1) {
                    return this.indexSearch(comps[0]);
                }
                Set<DN> candidateSet = null;
                for (final Filter f : comps) {
                    final Set<DN> dnSet = this.indexSearch(f);
                    if (dnSet != null) {
                        if (candidateSet == null) {
                            candidateSet = new TreeSet<DN>(dnSet);
                        }
                        else {
                            candidateSet.retainAll(dnSet);
                        }
                    }
                }
                return candidateSet;
            }
            case -95: {
                final Filter[] comps = filter.getComponents();
                if (comps.length == 0) {
                    return Collections.emptySet();
                }
                if (comps.length == 1) {
                    return this.indexSearch(comps[0]);
                }
                Set<DN> candidateSet = null;
                for (final Filter f : comps) {
                    final Set<DN> dnSet = this.indexSearch(f);
                    if (dnSet == null) {
                        return null;
                    }
                    if (candidateSet == null) {
                        candidateSet = new TreeSet<DN>(dnSet);
                    }
                    else {
                        candidateSet.addAll(dnSet);
                    }
                }
                return candidateSet;
            }
            case -93: {
                final Schema schema = this.schemaRef.get();
                if (schema == null) {
                    return null;
                }
                final AttributeTypeDefinition at = schema.getAttributeType(filter.getAttributeName());
                if (at == null) {
                    return null;
                }
                final InMemoryDirectoryServerEqualityAttributeIndex i = this.equalityIndexes.get(at);
                if (i == null) {
                    return null;
                }
                try {
                    return i.getMatchingEntries(filter.getRawAssertionValue());
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    return null;
                }
                break;
            }
        }
        return null;
    }
    
    private ASN1OctetString processTransactionRequest(final int messageID, final ProtocolOp request, final Map<String, Control> controls) throws LDAPException {
        final TransactionSpecificationRequestControl txnControl = controls.remove("1.3.6.1.1.21.2");
        if (txnControl == null) {
            return null;
        }
        final ASN1OctetString txnID = txnControl.getTransactionID();
        final ObjectPair<ASN1OctetString, List<LDAPMessage>> txnInfo = this.connectionState.get("TXN-INFO");
        if (txnInfo == null) {
            throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_MEM_HANDLER_TXN_CONTROL_WITHOUT_TXN.get(txnID.stringValue()));
        }
        final ASN1OctetString existingTxnID = txnInfo.getFirst();
        if (!txnID.stringValue().equals(existingTxnID.stringValue())) {
            this.connectionState.remove("TXN-INFO");
            this.connection.sendUnsolicitedNotification(new AbortedTransactionExtendedResult(existingTxnID, ResultCode.CONSTRAINT_VIOLATION, ListenerMessages.ERR_MEM_HANDLER_TXN_ABORTED_BY_CONTROL_TXN_ID_MISMATCH.get(existingTxnID.stringValue(), txnID.stringValue()), null, null, null));
            throw new LDAPException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION, ListenerMessages.ERR_MEM_HANDLER_TXN_CONTROL_ID_MISMATCH.get(txnID.stringValue(), existingTxnID.stringValue()));
        }
        txnInfo.getSecond().add(new LDAPMessage(messageID, request, new ArrayList<Control>(controls.values())));
        return txnID;
    }
    
    private void sleepBeforeProcessing() {
        final long delay = this.processingDelayMillis.get();
        if (delay > 0L) {
            try {
                Thread.sleep(delay);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    public List<String> getPasswordAttributes() {
        return this.configuredPasswordAttributes;
    }
    
    public InMemoryPasswordEncoder getPrimaryPasswordEncoder() {
        return this.primaryPasswordEncoder;
    }
    
    public List<InMemoryPasswordEncoder> getAllPasswordEncoders() {
        return this.passwordEncoders;
    }
    
    public List<InMemoryDirectoryServerPassword> getPasswordsInEntry(final Entry entry, final ASN1OctetString clearPasswordToMatch) {
        final ArrayList<InMemoryDirectoryServerPassword> passwordList = new ArrayList<InMemoryDirectoryServerPassword>(5);
        final ReadOnlyEntry readOnlyEntry = new ReadOnlyEntry(entry);
        for (final String passwordAttributeName : this.configuredPasswordAttributes) {
            final List<Attribute> passwordAttributeList = entry.getAttributesWithOptions(passwordAttributeName, null);
            for (final Attribute passwordAttribute : passwordAttributeList) {
                for (final ASN1OctetString value : passwordAttribute.getRawValues()) {
                    final InMemoryDirectoryServerPassword password = new InMemoryDirectoryServerPassword(value, readOnlyEntry, passwordAttribute.getName(), this.passwordEncoders);
                    Label_0197: {
                        if (clearPasswordToMatch != null) {
                            try {
                                if (!password.matchesClearPassword(clearPasswordToMatch)) {
                                    break Label_0197;
                                }
                            }
                            catch (final Exception e) {
                                Debug.debugException(e);
                                break Label_0197;
                            }
                        }
                        passwordList.add(new InMemoryDirectoryServerPassword(value, readOnlyEntry, passwordAttribute.getName(), this.passwordEncoders));
                    }
                }
            }
        }
        return passwordList;
    }
    
    public int countEntries(final boolean includeChangeLog) {
        synchronized (this.entryMap) {
            if (includeChangeLog || this.maxChangelogEntries == 0) {
                return this.entryMap.size();
            }
            int count = 0;
            for (final DN dn : this.entryMap.keySet()) {
                if (!dn.isDescendantOf(this.changeLogBaseDN, true)) {
                    ++count;
                }
            }
            return count;
        }
    }
    
    public int countEntriesBelow(final String baseDN) throws LDAPException {
        synchronized (this.entryMap) {
            final DN parsedBaseDN = new DN(baseDN, this.schemaRef.get());
            int count = 0;
            for (final DN dn : this.entryMap.keySet()) {
                if (dn.isDescendantOf(parsedBaseDN, true)) {
                    ++count;
                }
            }
            return count;
        }
    }
    
    public void clear() {
        synchronized (this.entryMap) {
            this.restoreSnapshot(this.initialSnapshot);
        }
    }
    
    public int importFromLDIF(final boolean clear, final LDIFReader ldifReader) throws LDAPException {
        synchronized (this.entryMap) {
            final InMemoryDirectoryServerSnapshot snapshot = this.createSnapshot();
            boolean restoreSnapshot = true;
            try {
                if (clear) {
                    this.restoreSnapshot(this.initialSnapshot);
                }
                int entriesAdded = 0;
                while (true) {
                    Entry entry;
                    try {
                        entry = ldifReader.readEntry();
                        if (entry == null) {
                            restoreSnapshot = false;
                            return entriesAdded;
                        }
                    }
                    catch (final LDIFException le) {
                        Debug.debugException(le);
                        throw new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_MEM_HANDLER_INIT_FROM_LDIF_READ_ERROR.get(le.getMessage()), le);
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        throw new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_MEM_HANDLER_INIT_FROM_LDIF_READ_ERROR.get(StaticUtils.getExceptionMessage(e)), e);
                    }
                    this.addEntry(entry, true);
                    ++entriesAdded;
                }
            }
            finally {
                try {
                    ldifReader.close();
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                }
                if (restoreSnapshot) {
                    this.restoreSnapshot(snapshot);
                }
            }
        }
    }
    
    public int exportToLDIF(final LDIFWriter ldifWriter, final boolean excludeGeneratedAttrs, final boolean excludeChangeLog, final boolean closeWriter) throws LDAPException {
        synchronized (this.entryMap) {
            boolean exceptionThrown = false;
            try {
                int entriesWritten = 0;
                for (final Map.Entry<DN, ReadOnlyEntry> me : this.entryMap.entrySet()) {
                    final DN dn = me.getKey();
                    if (excludeChangeLog && dn.isDescendantOf(this.changeLogBaseDN, true)) {
                        continue;
                    }
                    Entry entry;
                    if (excludeGeneratedAttrs) {
                        entry = me.getValue().duplicate();
                        entry.removeAttribute("entryDN");
                        entry.removeAttribute("entryUUID");
                        entry.removeAttribute("subschemaSubentry");
                        entry.removeAttribute("creatorsName");
                        entry.removeAttribute("createTimestamp");
                        entry.removeAttribute("modifiersName");
                        entry.removeAttribute("modifyTimestamp");
                    }
                    else {
                        entry = me.getValue();
                    }
                    try {
                        ldifWriter.writeEntry(entry);
                        ++entriesWritten;
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        exceptionThrown = true;
                        throw new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_MEM_HANDLER_LDIF_WRITE_ERROR.get(entry.getDN(), StaticUtils.getExceptionMessage(e)), e);
                    }
                }
                return entriesWritten;
            }
            finally {
                if (closeWriter) {
                    try {
                        ldifWriter.close();
                    }
                    catch (final Exception e2) {
                        Debug.debugException(e2);
                        if (!exceptionThrown) {
                            throw new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_MEM_HANDLER_LDIF_WRITE_CLOSE_ERROR.get(StaticUtils.getExceptionMessage(e2)), e2);
                        }
                    }
                }
            }
        }
    }
    
    public int applyChangesFromLDIF(final LDIFReader ldifReader) throws LDAPException {
        synchronized (this.entryMap) {
            final InMemoryDirectoryServerSnapshot snapshot = this.createSnapshot();
            boolean restoreSnapshot = true;
            try {
                int changesApplied = 0;
                LDIFChangeRecord changeRecord;
                while (true) {
                    try {
                        changeRecord = ldifReader.readChangeRecord(true);
                        if (changeRecord == null) {
                            restoreSnapshot = false;
                            return changesApplied;
                        }
                    }
                    catch (final LDIFException le) {
                        Debug.debugException(le);
                        throw new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_MEM_HANDLER_APPLY_CHANGES_FROM_LDIF_READ_ERROR.get(le.getMessage()), le);
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        throw new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_MEM_HANDLER_APPLY_CHANGES_FROM_LDIF_READ_ERROR.get(StaticUtils.getExceptionMessage(e)), e);
                    }
                    if (changeRecord instanceof LDIFAddChangeRecord) {
                        final LDIFAddChangeRecord addChangeRecord = (LDIFAddChangeRecord)changeRecord;
                        this.add(addChangeRecord.toAddRequest());
                    }
                    else if (changeRecord instanceof LDIFDeleteChangeRecord) {
                        final LDIFDeleteChangeRecord deleteChangeRecord = (LDIFDeleteChangeRecord)changeRecord;
                        this.delete(deleteChangeRecord.toDeleteRequest());
                    }
                    else if (changeRecord instanceof LDIFModifyChangeRecord) {
                        final LDIFModifyChangeRecord modifyChangeRecord = (LDIFModifyChangeRecord)changeRecord;
                        this.modify(modifyChangeRecord.toModifyRequest());
                    }
                    else {
                        if (!(changeRecord instanceof LDIFModifyDNChangeRecord)) {
                            break;
                        }
                        final LDIFModifyDNChangeRecord modifyDNChangeRecord = (LDIFModifyDNChangeRecord)changeRecord;
                        this.modifyDN(modifyDNChangeRecord.toModifyDNRequest());
                    }
                    ++changesApplied;
                }
                throw new LDAPException(ResultCode.LOCAL_ERROR, ListenerMessages.ERR_MEM_HANDLER_APPLY_CHANGES_UNSUPPORTED_CHANGE.get(String.valueOf(changeRecord)));
            }
            finally {
                try {
                    ldifReader.close();
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                }
                if (restoreSnapshot) {
                    this.restoreSnapshot(snapshot);
                }
            }
        }
    }
    
    public void addEntry(final Entry entry, final boolean ignoreNoUserModification) throws LDAPException {
        List<Control> controls;
        if (ignoreNoUserModification) {
            controls = new ArrayList<Control>(1);
            controls.add(new Control("1.3.6.1.4.1.30221.2.5.18", false));
        }
        else {
            controls = Collections.emptyList();
        }
        final AddRequestProtocolOp addRequest = new AddRequestProtocolOp(entry.getDN(), new ArrayList<Attribute>(entry.getAttributes()));
        final LDAPMessage resultMessage = this.processAddRequest(-1, addRequest, controls);
        final AddResponseProtocolOp addResponse = resultMessage.getAddResponseProtocolOp();
        if (addResponse.getResultCode() != 0) {
            throw new LDAPException(ResultCode.valueOf(addResponse.getResultCode()), addResponse.getDiagnosticMessage(), addResponse.getMatchedDN(), stringListToArray(addResponse.getReferralURLs()));
        }
    }
    
    public void addEntries(final List<? extends Entry> entries) throws LDAPException {
        synchronized (this.entryMap) {
            final InMemoryDirectoryServerSnapshot snapshot = this.createSnapshot();
            boolean restoreSnapshot = true;
            try {
                for (final Entry e : entries) {
                    this.addEntry(e, false);
                }
                restoreSnapshot = false;
            }
            finally {
                if (restoreSnapshot) {
                    this.restoreSnapshot(snapshot);
                }
            }
        }
    }
    
    public int deleteSubtree(final String baseDN) throws LDAPException {
        synchronized (this.entryMap) {
            final DN dn = new DN(baseDN, this.schemaRef.get());
            if (dn.isNullDN()) {
                throw new LDAPException(ResultCode.UNWILLING_TO_PERFORM, ListenerMessages.ERR_MEM_HANDLER_DELETE_ROOT_DSE.get());
            }
            int numDeleted = 0;
            final Iterator<Map.Entry<DN, ReadOnlyEntry>> iterator = this.entryMap.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<DN, ReadOnlyEntry> e = iterator.next();
                if (e.getKey().isDescendantOf(dn, true)) {
                    iterator.remove();
                    ++numDeleted;
                }
            }
            return numDeleted;
        }
    }
    
    public void modifyEntry(final String dn, final List<Modification> mods) throws LDAPException {
        final ModifyRequestProtocolOp modifyRequest = new ModifyRequestProtocolOp(dn, mods);
        final LDAPMessage resultMessage = this.processModifyRequest(-1, modifyRequest, Collections.emptyList());
        final ModifyResponseProtocolOp modifyResponse = resultMessage.getModifyResponseProtocolOp();
        if (modifyResponse.getResultCode() != 0) {
            throw new LDAPException(ResultCode.valueOf(modifyResponse.getResultCode()), modifyResponse.getDiagnosticMessage(), modifyResponse.getMatchedDN(), stringListToArray(modifyResponse.getReferralURLs()));
        }
    }
    
    public ReadOnlyEntry getEntry(final String dn) throws LDAPException {
        return this.getEntry(new DN(dn, this.schemaRef.get()));
    }
    
    public ReadOnlyEntry getEntry(final DN dn) {
        synchronized (this.entryMap) {
            if (dn.isNullDN()) {
                return this.generateRootDSE();
            }
            if (dn.equals(this.subschemaSubentryDN)) {
                return this.subschemaSubentryRef.get();
            }
            final Entry e = this.entryMap.get(dn);
            if (e == null) {
                return null;
            }
            return new ReadOnlyEntry(e);
        }
    }
    
    public List<ReadOnlyEntry> search(final String baseDN, final SearchScope scope, final Filter filter) throws LDAPException {
        synchronized (this.entryMap) {
            final Schema schema = this.schemaRef.get();
            DN parsedDN;
            try {
                parsedDN = new DN(baseDN, schema);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, ListenerMessages.ERR_MEM_HANDLER_SEARCH_MALFORMED_BASE.get(baseDN, le.getMessage()), le);
            }
            ReadOnlyEntry baseEntry;
            if (parsedDN.isNullDN()) {
                baseEntry = this.generateRootDSE();
            }
            else if (parsedDN.equals(this.subschemaSubentryDN)) {
                baseEntry = this.subschemaSubentryRef.get();
            }
            else {
                final Entry e = this.entryMap.get(parsedDN);
                if (e == null) {
                    throw new LDAPException(ResultCode.NO_SUCH_OBJECT, ListenerMessages.ERR_MEM_HANDLER_SEARCH_BASE_DOES_NOT_EXIST.get(baseDN), this.getMatchedDNString(parsedDN), null);
                }
                baseEntry = new ReadOnlyEntry(e);
            }
            if (scope == SearchScope.BASE) {
                final List<ReadOnlyEntry> entryList = new ArrayList<ReadOnlyEntry>(1);
                try {
                    if (filter.matchesEntry(baseEntry, schema)) {
                        entryList.add(baseEntry);
                    }
                }
                catch (final LDAPException le2) {
                    Debug.debugException(le2);
                }
                return Collections.unmodifiableList((List<? extends ReadOnlyEntry>)entryList);
            }
            if (scope == SearchScope.ONE && parsedDN.isNullDN()) {
                final List<ReadOnlyEntry> entryList = new ArrayList<ReadOnlyEntry>(this.baseDNs.size());
                try {
                    for (final DN dn : this.baseDNs) {
                        final Entry e2 = this.entryMap.get(dn);
                        if (e2 != null && filter.matchesEntry(e2, schema)) {
                            entryList.add(new ReadOnlyEntry(e2));
                        }
                    }
                }
                catch (final LDAPException le2) {
                    Debug.debugException(le2);
                }
                return Collections.unmodifiableList((List<? extends ReadOnlyEntry>)entryList);
            }
            final List<ReadOnlyEntry> entryList = new ArrayList<ReadOnlyEntry>(10);
            for (final Map.Entry<DN, ReadOnlyEntry> me : this.entryMap.entrySet()) {
                final DN dn2 = me.getKey();
                if (dn2.matchesBaseAndScope(parsedDN, scope)) {
                    if (parsedDN.isNullDN() && dn2.isDescendantOf(this.changeLogBaseDN, true)) {
                        continue;
                    }
                    try {
                        final Entry entry = me.getValue();
                        if (!filter.matchesEntry(entry, schema)) {
                            continue;
                        }
                        entryList.add(new ReadOnlyEntry(entry));
                    }
                    catch (final LDAPException le3) {
                        Debug.debugException(le3);
                    }
                }
            }
            return Collections.unmodifiableList((List<? extends ReadOnlyEntry>)entryList);
        }
    }
    
    private ReadOnlyEntry generateRootDSE() {
        final ReadOnlyEntry rootDSEFromCfg = this.config.getRootDSEEntry();
        if (rootDSEFromCfg != null) {
            return rootDSEFromCfg;
        }
        final Entry rootDSEEntry = new Entry(DN.NULL_DN, this.schemaRef.get());
        rootDSEEntry.addAttribute("objectClass", "top", "ds-root-dse");
        rootDSEEntry.addAttribute(new Attribute("supportedLDAPVersion", IntegerMatchingRule.getInstance(), "3"));
        final String vendorName = this.config.getVendorName();
        if (vendorName != null) {
            rootDSEEntry.addAttribute("vendorName", vendorName);
        }
        final String vendorVersion = this.config.getVendorVersion();
        if (vendorVersion != null) {
            rootDSEEntry.addAttribute("vendorVersion", vendorVersion);
        }
        rootDSEEntry.addAttribute(new Attribute("subschemaSubentry", DistinguishedNameMatchingRule.getInstance(), this.subschemaSubentryDN.toString()));
        rootDSEEntry.addAttribute(new Attribute("entryDN", DistinguishedNameMatchingRule.getInstance(), ""));
        rootDSEEntry.addAttribute("entryUUID", UUID.randomUUID().toString());
        rootDSEEntry.addAttribute("supportedFeatures", "1.3.6.1.4.1.4203.1.5.1", "1.3.6.1.4.1.4203.1.5.2", "1.3.6.1.4.1.4203.1.5.3", "1.3.6.1.1.14");
        final TreeSet<String> ctlSet = new TreeSet<String>();
        ctlSet.add("1.3.6.1.1.12");
        ctlSet.add("2.16.840.1.113730.3.4.16");
        ctlSet.add("1.3.6.1.1.22");
        ctlSet.add("2.16.840.1.113730.3.4.2");
        ctlSet.add("1.3.6.1.4.1.4203.1.10.2");
        ctlSet.add("1.2.840.113556.1.4.1413");
        ctlSet.add("1.3.6.1.1.13.2");
        ctlSet.add("1.3.6.1.1.13.1");
        ctlSet.add("2.16.840.1.113730.3.4.12");
        ctlSet.add("2.16.840.1.113730.3.4.18");
        ctlSet.add("1.2.840.113556.1.4.473");
        ctlSet.add("1.2.840.113556.1.4.319");
        ctlSet.add("1.3.6.1.4.1.7628.5.101.1");
        ctlSet.add("1.2.840.113556.1.4.805");
        ctlSet.add("1.3.6.1.1.21.2");
        ctlSet.add("2.16.840.1.113730.3.4.9");
        ctlSet.add("1.3.6.1.4.1.30221.2.5.5");
        final String[] controlOIDs = new String[ctlSet.size()];
        rootDSEEntry.addAttribute("supportedControl", (String[])ctlSet.toArray(controlOIDs));
        if (!this.extendedRequestHandlers.isEmpty()) {
            final String[] oidArray = new String[this.extendedRequestHandlers.size()];
            rootDSEEntry.addAttribute("supportedExtension", (String[])this.extendedRequestHandlers.keySet().toArray(oidArray));
            for (final InMemoryListenerConfig c : this.config.getListenerConfigs()) {
                if (c.getStartTLSSocketFactory() != null) {
                    rootDSEEntry.addAttribute("supportedExtension", "1.3.6.1.4.1.1466.20037");
                    break;
                }
            }
        }
        if (!this.saslBindHandlers.isEmpty()) {
            final String[] mechanismArray = new String[this.saslBindHandlers.size()];
            rootDSEEntry.addAttribute("supportedSASLMechanisms", (String[])this.saslBindHandlers.keySet().toArray(mechanismArray));
        }
        int pos = 0;
        final String[] baseDNStrings = new String[this.baseDNs.size()];
        for (final DN baseDN : this.baseDNs) {
            baseDNStrings[pos++] = baseDN.toString();
        }
        rootDSEEntry.addAttribute(new Attribute("namingContexts", DistinguishedNameMatchingRule.getInstance(), baseDNStrings));
        if (this.maxChangelogEntries > 0) {
            rootDSEEntry.addAttribute(new Attribute("changeLog", DistinguishedNameMatchingRule.getInstance(), this.changeLogBaseDN.toString()));
            rootDSEEntry.addAttribute(new Attribute("firstChangeNumber", IntegerMatchingRule.getInstance(), this.firstChangeNumber.toString()));
            rootDSEEntry.addAttribute(new Attribute("lastChangeNumber", IntegerMatchingRule.getInstance(), this.lastChangeNumber.toString()));
        }
        return new ReadOnlyEntry(rootDSEEntry);
    }
    
    private static ReadOnlyEntry generateSubschemaSubentry(final Schema schema) {
        Entry e;
        if (schema == null) {
            e = new Entry("cn=schema", schema);
            e.addAttribute("objectClass", "namedObject", "ldapSubEntry", "subschema");
            e.addAttribute("cn", "schema");
        }
        else {
            e = schema.getSchemaEntry().duplicate();
        }
        try {
            e.addAttribute("entryDN", DN.normalize(e.getDN(), schema));
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            e.setAttribute("entryDN", StaticUtils.toLowerCase(e.getDN()));
        }
        e.addAttribute("entryUUID", UUID.randomUUID().toString());
        return new ReadOnlyEntry(e);
    }
    
    private Map<String, List<List<String>>> processRequestedAttributes(final List<String> attrList, final AtomicBoolean allUserAttrs, final AtomicBoolean allOpAttrs) {
        if (attrList.isEmpty()) {
            allUserAttrs.set(true);
            return Collections.emptyMap();
        }
        final Schema schema = this.schemaRef.get();
        final HashMap<String, List<List<String>>> m = new HashMap<String, List<List<String>>>(StaticUtils.computeMapCapacity(attrList.size() * 2));
        for (final String s : attrList) {
            if (s.equals("*")) {
                allUserAttrs.set(true);
            }
            else if (s.equals("+")) {
                allOpAttrs.set(true);
            }
            else if (s.startsWith("@")) {
                if (schema == null) {
                    continue;
                }
                final String ocName = s.substring(1);
                final ObjectClassDefinition oc = schema.getObjectClass(ocName);
                if (oc == null) {
                    continue;
                }
                for (final AttributeTypeDefinition at : oc.getRequiredAttributes(schema, true)) {
                    this.addAttributeOIDAndNames(at, m, Collections.emptyList());
                }
                for (final AttributeTypeDefinition at : oc.getOptionalAttributes(schema, true)) {
                    this.addAttributeOIDAndNames(at, m, Collections.emptyList());
                }
            }
            else {
                final ObjectPair<String, List<String>> nameWithOptions = getNameWithOptions(s);
                if (nameWithOptions == null) {
                    continue;
                }
                final String name = nameWithOptions.getFirst();
                final List<String> options = nameWithOptions.getSecond();
                if (schema == null) {
                    List<List<String>> optionLists = m.get(name);
                    if (optionLists == null) {
                        optionLists = new ArrayList<List<String>>(1);
                        m.put(name, optionLists);
                    }
                    optionLists.add(options);
                }
                else {
                    final AttributeTypeDefinition at = schema.getAttributeType(name);
                    if (at == null) {
                        List<List<String>> optionLists2 = m.get(name);
                        if (optionLists2 == null) {
                            optionLists2 = new ArrayList<List<String>>(1);
                            m.put(name, optionLists2);
                        }
                        optionLists2.add(options);
                    }
                    else {
                        this.addAttributeOIDAndNames(at, m, options);
                    }
                }
            }
        }
        return m;
    }
    
    private static ObjectPair<String, List<String>> getNameWithOptions(final String s) {
        if (!Attribute.nameIsValid(s, true)) {
            return null;
        }
        final String l = StaticUtils.toLowerCase(s);
        int semicolonPos = l.indexOf(59);
        if (semicolonPos < 0) {
            return new ObjectPair<String, List<String>>(l, Collections.emptyList());
        }
        final String name = l.substring(0, semicolonPos);
        final ArrayList<String> optionList = new ArrayList<String>(1);
        while (true) {
            final int nextSemicolonPos = l.indexOf(59, semicolonPos + 1);
            if (nextSemicolonPos < 0) {
                break;
            }
            optionList.add(l.substring(semicolonPos + 1, nextSemicolonPos));
            semicolonPos = nextSemicolonPos;
        }
        optionList.add(l.substring(semicolonPos + 1));
        return new ObjectPair<String, List<String>>(name, optionList);
    }
    
    private void addAttributeOIDAndNames(final AttributeTypeDefinition d, final Map<String, List<List<String>>> m, final List<String> o) {
        if (d == null) {
            return;
        }
        final String lowerOID = StaticUtils.toLowerCase(d.getOID());
        if (lowerOID != null) {
            List<List<String>> l = m.get(lowerOID);
            if (l == null) {
                l = new ArrayList<List<String>>(1);
                m.put(lowerOID, l);
            }
            l.add(o);
        }
        for (final String name : d.getNames()) {
            final String lowerName = StaticUtils.toLowerCase(name);
            List<List<String>> i = m.get(lowerName);
            if (i == null) {
                i = new ArrayList<List<String>>(1);
                m.put(lowerName, i);
            }
            i.add(o);
        }
        final Schema schema = this.schemaRef.get();
        if (schema != null) {
            for (final AttributeTypeDefinition subordinateType : schema.getSubordinateAttributeTypes(d)) {
                this.addAttributeOIDAndNames(subordinateType, m, o);
            }
        }
    }
    
    private void processSearchEntry(final Entry entry, final boolean includeSubEntries, final boolean includeNonSubEntries, final boolean includeChangeLog, final boolean hasManageDsaIT, final List<Entry> entryList, final List<SearchResultReference> referenceList) {
        if (entry.hasObjectClass("ldapSubEntry") || entry.hasObjectClass("inheritableLDAPSubEntry")) {
            if (!includeSubEntries) {
                return;
            }
        }
        else if (!includeNonSubEntries) {
            return;
        }
        try {
            if (!includeChangeLog && entry.getParsedDN().isDescendantOf(this.changeLogBaseDN, true)) {
                return;
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        if (!hasManageDsaIT && entry.hasObjectClass("referral") && entry.hasAttribute("ref")) {
            referenceList.add(new SearchResultReference(entry.getAttributeValues("ref"), InMemoryRequestHandler.NO_CONTROLS));
            return;
        }
        entryList.add(entry);
    }
    
    private Entry trimForRequestedAttributes(final Entry entry, final boolean allUserAttrs, final boolean allOpAttrs, final Map<String, List<List<String>>> returnAttrs) {
        final Schema schema = this.schemaRef.get();
        if (allUserAttrs && (allOpAttrs || schema == null)) {
            return entry;
        }
        final Entry copy = new Entry(entry.getDN(), schema);
        for (final Attribute a : entry.getAttributes()) {
            final ObjectPair<String, List<String>> nameWithOptions = getNameWithOptions(a.getName());
            final String name = nameWithOptions.getFirst();
            final List<String> options = nameWithOptions.getSecond();
            if (schema != null) {
                final AttributeTypeDefinition at = schema.getAttributeType(name);
                if (at != null && at.isOperational()) {
                    if (allOpAttrs) {
                        copy.addAttribute(a);
                        continue;
                    }
                    final List<List<String>> optionLists = returnAttrs.get(name);
                    if (optionLists == null) {
                        continue;
                    }
                    for (final List<String> optionList : optionLists) {
                        boolean matchAll = true;
                        for (final String option : optionList) {
                            if (!options.contains(option)) {
                                matchAll = false;
                                break;
                            }
                        }
                        if (matchAll) {
                            copy.addAttribute(a);
                            break;
                        }
                    }
                    continue;
                }
            }
            if (allUserAttrs) {
                copy.addAttribute(a);
            }
            else {
                final List<List<String>> optionLists2 = returnAttrs.get(name);
                if (optionLists2 == null) {
                    continue;
                }
                for (final List<String> optionList2 : optionLists2) {
                    boolean matchAll2 = true;
                    for (final String option2 : optionList2) {
                        if (!options.contains(option2)) {
                            matchAll2 = false;
                            break;
                        }
                    }
                    if (matchAll2) {
                        copy.addAttribute(a);
                        break;
                    }
                }
            }
        }
        return copy;
    }
    
    private String getMatchedDNString(final DN dn) {
        for (DN parentDN = dn.getParent(); parentDN != null; parentDN = parentDN.getParent()) {
            if (this.entryMap.containsKey(parentDN)) {
                return parentDN.toString();
            }
        }
        return null;
    }
    
    private static String[] stringListToArray(final List<String> l) {
        if (l == null) {
            return null;
        }
        final String[] a = new String[l.size()];
        return l.toArray(a);
    }
    
    private void addChangeLogEntry(final AddRequestProtocolOp addRequest, final DN authzDN) {
        if (this.maxChangelogEntries <= 0) {
            return;
        }
        final long changeNumber = this.lastChangeNumber.incrementAndGet();
        final LDIFAddChangeRecord changeRecord = new LDIFAddChangeRecord(addRequest.getDN(), addRequest.getAttributes());
        try {
            this.addChangeLogEntry(ChangeLogEntry.constructChangeLogEntry(changeNumber, changeRecord), authzDN);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
        }
    }
    
    private void addDeleteChangeLogEntry(final Entry e, final DN authzDN) {
        if (this.maxChangelogEntries <= 0) {
            return;
        }
        final long changeNumber = this.lastChangeNumber.incrementAndGet();
        final LDIFDeleteChangeRecord changeRecord = new LDIFDeleteChangeRecord(e.getDN());
        try {
            final ChangeLogEntry cle = ChangeLogEntry.constructChangeLogEntry(changeNumber, changeRecord);
            final StringBuilder deletedEntryAttrsBuffer = new StringBuilder();
            final String[] ldifLines = e.toLDIF(0);
            for (int i = 1; i < ldifLines.length; ++i) {
                deletedEntryAttrsBuffer.append(ldifLines[i]);
                deletedEntryAttrsBuffer.append(StaticUtils.EOL);
            }
            final Entry copy = cle.duplicate();
            copy.addAttribute("deletedEntryAttrs", deletedEntryAttrsBuffer.toString());
            this.addChangeLogEntry(new ChangeLogEntry(copy), authzDN);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
        }
    }
    
    private void addChangeLogEntry(final ModifyRequestProtocolOp modifyRequest, final DN authzDN) {
        if (this.maxChangelogEntries <= 0) {
            return;
        }
        final long changeNumber = this.lastChangeNumber.incrementAndGet();
        final LDIFModifyChangeRecord changeRecord = new LDIFModifyChangeRecord(modifyRequest.getDN(), modifyRequest.getModifications());
        try {
            this.addChangeLogEntry(ChangeLogEntry.constructChangeLogEntry(changeNumber, changeRecord), authzDN);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
        }
    }
    
    private void addChangeLogEntry(final ModifyDNRequestProtocolOp modifyDNRequest, final DN authzDN) {
        if (this.maxChangelogEntries <= 0) {
            return;
        }
        final long changeNumber = this.lastChangeNumber.incrementAndGet();
        final LDIFModifyDNChangeRecord changeRecord = new LDIFModifyDNChangeRecord(modifyDNRequest.getDN(), modifyDNRequest.getNewRDN(), modifyDNRequest.deleteOldRDN(), modifyDNRequest.getNewSuperiorDN());
        try {
            this.addChangeLogEntry(ChangeLogEntry.constructChangeLogEntry(changeNumber, changeRecord), authzDN);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
        }
    }
    
    private void addChangeLogEntry(final ChangeLogEntry e, final DN authzDN) {
        final long changeNumber = e.getChangeNumber();
        final Schema schema = this.schemaRef.get();
        final DN dn = new DN(new RDN("changeNumber", String.valueOf(changeNumber), schema), this.changeLogBaseDN);
        final Entry entry = e.duplicate();
        if (this.generateOperationalAttributes) {
            final Date d = new Date();
            entry.addAttribute(new Attribute("entryDN", DistinguishedNameMatchingRule.getInstance(), dn.toNormalizedString()));
            entry.addAttribute(new Attribute("entryUUID", UUID.randomUUID().toString()));
            entry.addAttribute(new Attribute("subschemaSubentry", DistinguishedNameMatchingRule.getInstance(), this.subschemaSubentryDN.toString()));
            entry.addAttribute(new Attribute("creatorsName", DistinguishedNameMatchingRule.getInstance(), authzDN.toString()));
            entry.addAttribute(new Attribute("createTimestamp", GeneralizedTimeMatchingRule.getInstance(), StaticUtils.encodeGeneralizedTime(d)));
            entry.addAttribute(new Attribute("modifiersName", DistinguishedNameMatchingRule.getInstance(), authzDN.toString()));
            entry.addAttribute(new Attribute("modifyTimestamp", GeneralizedTimeMatchingRule.getInstance(), StaticUtils.encodeGeneralizedTime(d)));
        }
        this.entryMap.put(dn, new ReadOnlyEntry(entry));
        this.indexAdd(entry);
        final long firstNumber = this.firstChangeNumber.get();
        if (changeNumber == 1L) {
            this.firstChangeNumber.set(1L);
        }
        else {
            final long numChangeLogEntries = changeNumber - firstNumber + 1L;
            if (numChangeLogEntries > this.maxChangelogEntries) {
                this.firstChangeNumber.incrementAndGet();
                final Entry deletedEntry = this.entryMap.remove(new DN(new RDN("changeNumber", String.valueOf(firstNumber), schema), this.changeLogBaseDN));
                this.indexDelete(deletedEntry);
            }
        }
    }
    
    private DN handleProxiedAuthControl(final Map<String, Control> m) throws LDAPException {
        final ProxiedAuthorizationV1RequestControl p1 = m.get("2.16.840.1.113730.3.4.12");
        if (p1 != null) {
            final DN authzDN = new DN(p1.getProxyDN(), this.schemaRef.get());
            if (authzDN.isNullDN() || this.entryMap.containsKey(authzDN) || this.additionalBindCredentials.containsKey(authzDN)) {
                return authzDN;
            }
            throw new LDAPException(ResultCode.AUTHORIZATION_DENIED, ListenerMessages.ERR_MEM_HANDLER_NO_SUCH_IDENTITY.get("dn:" + authzDN.toString()));
        }
        else {
            final ProxiedAuthorizationV2RequestControl p2 = m.get("2.16.840.1.113730.3.4.18");
            if (p2 != null) {
                return this.getDNForAuthzID(p2.getAuthorizationID());
            }
            return this.authenticatedDN;
        }
    }
    
    public DN getDNForAuthzID(final String authzID) throws LDAPException {
        synchronized (this.entryMap) {
            final String lowerAuthzID = StaticUtils.toLowerCase(authzID);
            if (lowerAuthzID.startsWith("dn:")) {
                if (lowerAuthzID.equals("dn:")) {
                    return DN.NULL_DN;
                }
                final DN dn = new DN(authzID.substring(3), this.schemaRef.get());
                if (this.entryMap.containsKey(dn) || this.additionalBindCredentials.containsKey(dn)) {
                    return dn;
                }
                throw new LDAPException(ResultCode.AUTHORIZATION_DENIED, ListenerMessages.ERR_MEM_HANDLER_NO_SUCH_IDENTITY.get(authzID));
            }
            else {
                if (!lowerAuthzID.startsWith("u:")) {
                    throw new LDAPException(ResultCode.AUTHORIZATION_DENIED, ListenerMessages.ERR_MEM_HANDLER_NO_SUCH_IDENTITY.get(authzID));
                }
                final Filter f = Filter.createEqualityFilter("uid", authzID.substring(2));
                final List<ReadOnlyEntry> entryList = this.search("", SearchScope.SUB, f);
                if (entryList.size() == 1) {
                    return entryList.get(0).getParsedDN();
                }
                throw new LDAPException(ResultCode.AUTHORIZATION_DENIED, ListenerMessages.ERR_MEM_HANDLER_NO_SUCH_IDENTITY.get(authzID));
            }
        }
    }
    
    private static void handleAssertionRequestControl(final Map<String, Control> m, final Entry e) throws LDAPException {
        final AssertionRequestControl c = m.get("1.3.6.1.1.12");
        if (c == null) {
            return;
        }
        try {
            if (c.getFilter().matchesEntry(e)) {
                return;
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
        }
        throw new LDAPException(ResultCode.ASSERTION_FAILED, ListenerMessages.ERR_MEM_HANDLER_ASSERTION_CONTROL_NOT_SATISFIED.get());
    }
    
    private PreReadResponseControl handlePreReadControl(final Map<String, Control> m, final Entry e) {
        final PreReadRequestControl c = m.get("1.3.6.1.1.13.1");
        if (c == null) {
            return null;
        }
        final AtomicBoolean allUserAttrs = new AtomicBoolean(false);
        final AtomicBoolean allOpAttrs = new AtomicBoolean(false);
        final Map<String, List<List<String>>> returnAttrs = this.processRequestedAttributes(Arrays.asList(c.getAttributes()), allUserAttrs, allOpAttrs);
        final Entry trimmedEntry = this.trimForRequestedAttributes(e, allUserAttrs.get(), allOpAttrs.get(), returnAttrs);
        return new PreReadResponseControl(new ReadOnlyEntry(trimmedEntry));
    }
    
    private PostReadResponseControl handlePostReadControl(final Map<String, Control> m, final Entry e) {
        final PostReadRequestControl c = m.get("1.3.6.1.1.13.2");
        if (c == null) {
            return null;
        }
        final AtomicBoolean allUserAttrs = new AtomicBoolean(false);
        final AtomicBoolean allOpAttrs = new AtomicBoolean(false);
        final Map<String, List<List<String>>> returnAttrs = this.processRequestedAttributes(Arrays.asList(c.getAttributes()), allUserAttrs, allOpAttrs);
        final Entry trimmedEntry = this.trimForRequestedAttributes(e, allUserAttrs.get(), allOpAttrs.get(), returnAttrs);
        return new PostReadResponseControl(new ReadOnlyEntry(trimmedEntry));
    }
    
    private Entry findNearestReferral(final DN dn) {
        DN d = dn;
        while (true) {
            final Entry e = this.entryMap.get(d);
            if (e == null) {
                d = d.getParent();
                if (d == null) {
                    return null;
                }
                continue;
            }
            else {
                if (e.hasObjectClass("referral")) {
                    return e;
                }
                return null;
            }
        }
    }
    
    private static List<String> getReferralURLs(final DN targetDN, final Entry referralEntry) {
        final String[] refs = referralEntry.getAttributeValues("ref");
        if (refs == null) {
            return null;
        }
        RDN[] retainRDNs;
        try {
            final DN parsedEntryDN = referralEntry.getParsedDN();
            if (targetDN.equals(parsedEntryDN) || !targetDN.isDescendantOf(parsedEntryDN, true)) {
                return Arrays.asList(refs);
            }
            final RDN[] targetRDNs = targetDN.getRDNs();
            final RDN[] refEntryRDNs = referralEntry.getParsedDN().getRDNs();
            retainRDNs = new RDN[targetRDNs.length - refEntryRDNs.length];
            System.arraycopy(targetRDNs, 0, retainRDNs, 0, retainRDNs.length);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            return Arrays.asList(refs);
        }
        final List<String> refList = new ArrayList<String>(refs.length);
        for (final String ref : refs) {
            try {
                final LDAPURL url = new LDAPURL(ref);
                final RDN[] refRDNs = url.getBaseDN().getRDNs();
                final RDN[] newRefRDNs = new RDN[retainRDNs.length + refRDNs.length];
                System.arraycopy(retainRDNs, 0, newRefRDNs, 0, retainRDNs.length);
                System.arraycopy(refRDNs, 0, newRefRDNs, retainRDNs.length, refRDNs.length);
                final DN newBaseDN = new DN(newRefRDNs);
                final LDAPURL newURL = new LDAPURL(url.getScheme(), url.getHost(), url.getPort(), newBaseDN, null, null, null);
                refList.add(newURL.toString());
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                refList.add(ref);
            }
        }
        return refList;
    }
    
    public boolean entryExists(final String dn) throws LDAPException {
        return this.getEntry(dn) != null;
    }
    
    public boolean entryExists(final String dn, final String filter) throws LDAPException {
        synchronized (this.entryMap) {
            final Entry e = this.getEntry(dn);
            if (e == null) {
                return false;
            }
            final Filter f = Filter.create(filter);
            try {
                return f.matchesEntry(e, this.schemaRef.get());
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                return false;
            }
        }
    }
    
    public boolean entryExists(final Entry entry) throws LDAPException {
        synchronized (this.entryMap) {
            final Entry e = this.getEntry(entry.getDN());
            if (e == null) {
                return false;
            }
            for (final Attribute a : entry.getAttributes()) {
                for (final byte[] value : a.getValueByteArrays()) {
                    if (!e.hasAttributeValue(a.getName(), value)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }
    
    public void assertEntryExists(final String dn) throws LDAPException, AssertionError {
        final Entry e = this.getEntry(dn);
        if (e == null) {
            throw new AssertionError((Object)ListenerMessages.ERR_MEM_HANDLER_TEST_ENTRY_MISSING.get(dn));
        }
    }
    
    public void assertEntryExists(final String dn, final String filter) throws LDAPException, AssertionError {
        synchronized (this.entryMap) {
            final Entry e = this.getEntry(dn);
            if (e == null) {
                throw new AssertionError((Object)ListenerMessages.ERR_MEM_HANDLER_TEST_ENTRY_MISSING.get(dn));
            }
            final Filter f = Filter.create(filter);
            try {
                if (!f.matchesEntry(e, this.schemaRef.get())) {
                    throw new AssertionError((Object)ListenerMessages.ERR_MEM_HANDLER_TEST_ENTRY_DOES_NOT_MATCH_FILTER.get(dn, filter));
                }
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                throw new AssertionError((Object)ListenerMessages.ERR_MEM_HANDLER_TEST_ENTRY_DOES_NOT_MATCH_FILTER.get(dn, filter));
            }
        }
    }
    
    public void assertEntryExists(final Entry entry) throws LDAPException, AssertionError {
        synchronized (this.entryMap) {
            final Entry e = this.getEntry(entry.getDN());
            if (e == null) {
                throw new AssertionError((Object)ListenerMessages.ERR_MEM_HANDLER_TEST_ENTRY_MISSING.get(entry.getDN()));
            }
            final Collection<Attribute> attrs = entry.getAttributes();
            final List<String> messages = new ArrayList<String>(attrs.size());
            final Schema schema = this.schemaRef.get();
            for (final Attribute a : entry.getAttributes()) {
                final Filter presFilter = Filter.createPresenceFilter(a.getName());
                if (!presFilter.matchesEntry(e, schema)) {
                    messages.add(ListenerMessages.ERR_MEM_HANDLER_TEST_ATTR_MISSING.get(entry.getDN(), a.getName()));
                }
                else {
                    for (final byte[] value : a.getValueByteArrays()) {
                        final Filter eqFilter = Filter.createEqualityFilter(a.getName(), value);
                        if (!eqFilter.matchesEntry(e, schema)) {
                            messages.add(ListenerMessages.ERR_MEM_HANDLER_TEST_VALUE_MISSING.get(entry.getDN(), a.getName(), StaticUtils.toUTF8String(value)));
                        }
                    }
                }
            }
            if (!messages.isEmpty()) {
                throw new AssertionError((Object)StaticUtils.concatenateStrings(messages));
            }
        }
    }
    
    public List<String> getMissingEntryDNs(final Collection<String> dns) throws LDAPException {
        synchronized (this.entryMap) {
            final List<String> missingDNs = new ArrayList<String>(dns.size());
            for (final String dn : dns) {
                final Entry e = this.getEntry(dn);
                if (e == null) {
                    missingDNs.add(dn);
                }
            }
            return missingDNs;
        }
    }
    
    public void assertEntriesExist(final Collection<String> dns) throws LDAPException, AssertionError {
        synchronized (this.entryMap) {
            final List<String> missingDNs = this.getMissingEntryDNs(dns);
            if (missingDNs.isEmpty()) {
                return;
            }
            final List<String> messages = new ArrayList<String>(missingDNs.size());
            for (final String dn : missingDNs) {
                messages.add(ListenerMessages.ERR_MEM_HANDLER_TEST_ENTRY_MISSING.get(dn));
            }
            throw new AssertionError((Object)StaticUtils.concatenateStrings(messages));
        }
    }
    
    public List<String> getMissingAttributeNames(final String dn, final Collection<String> attributeNames) throws LDAPException {
        synchronized (this.entryMap) {
            final Entry e = this.getEntry(dn);
            if (e == null) {
                return null;
            }
            final Schema schema = this.schemaRef.get();
            final List<String> missingAttrs = new ArrayList<String>(attributeNames.size());
            for (final String attr : attributeNames) {
                final Filter f = Filter.createPresenceFilter(attr);
                if (!f.matchesEntry(e, schema)) {
                    missingAttrs.add(attr);
                }
            }
            return missingAttrs;
        }
    }
    
    public void assertAttributeExists(final String dn, final Collection<String> attributeNames) throws LDAPException, AssertionError {
        synchronized (this.entryMap) {
            final List<String> missingAttrs = this.getMissingAttributeNames(dn, attributeNames);
            if (missingAttrs == null) {
                throw new AssertionError((Object)ListenerMessages.ERR_MEM_HANDLER_TEST_ENTRY_MISSING.get(dn));
            }
            if (missingAttrs.isEmpty()) {
                return;
            }
            final List<String> messages = new ArrayList<String>(missingAttrs.size());
            for (final String attr : missingAttrs) {
                messages.add(ListenerMessages.ERR_MEM_HANDLER_TEST_ATTR_MISSING.get(dn, attr));
            }
            throw new AssertionError((Object)StaticUtils.concatenateStrings(messages));
        }
    }
    
    public List<String> getMissingAttributeValues(final String dn, final String attributeName, final Collection<String> attributeValues) throws LDAPException {
        synchronized (this.entryMap) {
            final Entry e = this.getEntry(dn);
            if (e == null) {
                return null;
            }
            final Schema schema = this.schemaRef.get();
            final List<String> missingValues = new ArrayList<String>(attributeValues.size());
            for (final String value : attributeValues) {
                final Filter f = Filter.createEqualityFilter(attributeName, value);
                if (!f.matchesEntry(e, schema)) {
                    missingValues.add(value);
                }
            }
            return missingValues;
        }
    }
    
    public void assertValueExists(final String dn, final String attributeName, final Collection<String> attributeValues) throws LDAPException, AssertionError {
        synchronized (this.entryMap) {
            final List<String> missingValues = this.getMissingAttributeValues(dn, attributeName, attributeValues);
            if (missingValues == null) {
                throw new AssertionError((Object)ListenerMessages.ERR_MEM_HANDLER_TEST_ENTRY_MISSING.get(dn));
            }
            if (missingValues.isEmpty()) {
                return;
            }
            final Entry e = this.getEntry(dn);
            final Filter f = Filter.createPresenceFilter(attributeName);
            if (!f.matchesEntry(e, this.schemaRef.get())) {
                throw new AssertionError((Object)ListenerMessages.ERR_MEM_HANDLER_TEST_ATTR_MISSING.get(dn, attributeName));
            }
            final List<String> messages = new ArrayList<String>(missingValues.size());
            for (final String value : missingValues) {
                messages.add(ListenerMessages.ERR_MEM_HANDLER_TEST_VALUE_MISSING.get(dn, attributeName, value));
            }
            throw new AssertionError((Object)StaticUtils.concatenateStrings(messages));
        }
    }
    
    public void assertEntryMissing(final String dn) throws LDAPException, AssertionError {
        final Entry e = this.getEntry(dn);
        if (e != null) {
            throw new AssertionError((Object)ListenerMessages.ERR_MEM_HANDLER_TEST_ENTRY_EXISTS.get(dn));
        }
    }
    
    public void assertAttributeMissing(final String dn, final Collection<String> attributeNames) throws LDAPException, AssertionError {
        synchronized (this.entryMap) {
            final Entry e = this.getEntry(dn);
            if (e == null) {
                throw new AssertionError((Object)ListenerMessages.ERR_MEM_HANDLER_TEST_ENTRY_MISSING.get(dn));
            }
            final Schema schema = this.schemaRef.get();
            final List<String> messages = new ArrayList<String>(attributeNames.size());
            for (final String name : attributeNames) {
                final Filter f = Filter.createPresenceFilter(name);
                if (f.matchesEntry(e, schema)) {
                    messages.add(ListenerMessages.ERR_MEM_HANDLER_TEST_ATTR_EXISTS.get(dn, name));
                }
            }
            if (!messages.isEmpty()) {
                throw new AssertionError((Object)StaticUtils.concatenateStrings(messages));
            }
        }
    }
    
    public void assertValueMissing(final String dn, final String attributeName, final Collection<String> attributeValues) throws LDAPException, AssertionError {
        synchronized (this.entryMap) {
            final Entry e = this.getEntry(dn);
            if (e == null) {
                throw new AssertionError((Object)ListenerMessages.ERR_MEM_HANDLER_TEST_ENTRY_MISSING.get(dn));
            }
            final Schema schema = this.schemaRef.get();
            final List<String> messages = new ArrayList<String>(attributeValues.size());
            for (final String value : attributeValues) {
                final Filter f = Filter.createEqualityFilter(attributeName, value);
                if (f.matchesEntry(e, schema)) {
                    messages.add(ListenerMessages.ERR_MEM_HANDLER_TEST_VALUE_EXISTS.get(dn, attributeName, value));
                }
            }
            if (!messages.isEmpty()) {
                throw new AssertionError((Object)StaticUtils.concatenateStrings(messages));
            }
        }
    }
    
    static {
        NO_CONTROLS = new Control[0];
    }
}
