package com.unboundid.ldap.sdk.persist;

import com.unboundid.ldap.sdk.SearchResultListener;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.EntrySource;
import com.unboundid.ldap.sdk.LDAPEntrySource;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.DereferencePolicy;
import java.util.Map;
import com.unboundid.ldap.sdk.Attribute;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.Modification;
import java.util.Collection;
import java.util.Arrays;
import com.unboundid.util.StaticUtils;
import java.util.Set;
import java.util.LinkedHashSet;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ldap.sdk.schema.ObjectClassDefinition;
import java.util.Iterator;
import java.util.Collections;
import java.util.LinkedList;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import java.util.List;
import com.unboundid.util.Validator;
import java.util.concurrent.ConcurrentHashMap;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPPersister<T> implements Serializable
{
    private static final long serialVersionUID = -4001743482496453961L;
    private static final Control[] NO_CONTROLS;
    private static final ConcurrentHashMap<Class<?>, LDAPPersister<?>> INSTANCES;
    private final LDAPObjectHandler<T> handler;
    
    private LDAPPersister(final Class<T> type) throws LDAPPersistException {
        this.handler = new LDAPObjectHandler<T>(type);
    }
    
    public static <T> LDAPPersister<T> getInstance(final Class<T> type) throws LDAPPersistException {
        Validator.ensureNotNull(type);
        LDAPPersister<T> p = (LDAPPersister<T>)LDAPPersister.INSTANCES.get(type);
        if (p == null) {
            p = new LDAPPersister<T>(type);
            LDAPPersister.INSTANCES.put(type, p);
        }
        return p;
    }
    
    public LDAPObject getLDAPObjectAnnotation() {
        return this.handler.getLDAPObjectAnnotation();
    }
    
    public LDAPObjectHandler<T> getObjectHandler() {
        return this.handler;
    }
    
    public List<AttributeTypeDefinition> constructAttributeTypes() throws LDAPPersistException {
        return this.constructAttributeTypes(DefaultOIDAllocator.getInstance());
    }
    
    public List<AttributeTypeDefinition> constructAttributeTypes(final OIDAllocator a) throws LDAPPersistException {
        final LinkedList<AttributeTypeDefinition> attrList = new LinkedList<AttributeTypeDefinition>();
        for (final FieldInfo i : this.handler.getFields().values()) {
            attrList.add(i.constructAttributeType(a));
        }
        for (final GetterInfo j : this.handler.getGetters().values()) {
            attrList.add(j.constructAttributeType(a));
        }
        return Collections.unmodifiableList((List<? extends AttributeTypeDefinition>)attrList);
    }
    
    public List<ObjectClassDefinition> constructObjectClasses() throws LDAPPersistException {
        return this.constructObjectClasses(DefaultOIDAllocator.getInstance());
    }
    
    public List<ObjectClassDefinition> constructObjectClasses(final OIDAllocator a) throws LDAPPersistException {
        return this.handler.constructObjectClasses(a);
    }
    
    public boolean updateSchema(final LDAPInterface i) throws LDAPException {
        return this.updateSchema(i, DefaultOIDAllocator.getInstance());
    }
    
    public boolean updateSchema(final LDAPInterface i, final OIDAllocator a) throws LDAPException {
        final Schema s = i.getSchema();
        final List<AttributeTypeDefinition> generatedTypes = this.constructAttributeTypes(a);
        final List<ObjectClassDefinition> generatedClasses = this.constructObjectClasses(a);
        final LinkedList<String> newAttrList = new LinkedList<String>();
        for (final AttributeTypeDefinition d : generatedTypes) {
            if (s.getAttributeType(d.getNameOrOID()) == null) {
                newAttrList.add(d.toString());
            }
        }
        final LinkedList<String> newOCList = new LinkedList<String>();
        for (final ObjectClassDefinition d2 : generatedClasses) {
            final ObjectClassDefinition existing = s.getObjectClass(d2.getNameOrOID());
            if (existing == null) {
                newOCList.add(d2.toString());
            }
            else {
                final Set<AttributeTypeDefinition> existingRequired = existing.getRequiredAttributes(s, true);
                final Set<AttributeTypeDefinition> existingOptional = existing.getOptionalAttributes(s, true);
                final LinkedHashSet<String> newOptionalNames = new LinkedHashSet<String>(0);
                addMissingAttrs(d2.getRequiredAttributes(), existingRequired, existingOptional, newOptionalNames);
                addMissingAttrs(d2.getOptionalAttributes(), existingRequired, existingOptional, newOptionalNames);
                if (newOptionalNames.isEmpty()) {
                    continue;
                }
                final LinkedHashSet<String> newOptionalSet = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(20));
                newOptionalSet.addAll((Collection<?>)Arrays.asList(existing.getOptionalAttributes()));
                newOptionalSet.addAll((Collection<?>)newOptionalNames);
                final String[] newOptional = new String[newOptionalSet.size()];
                newOptionalSet.toArray(newOptional);
                final ObjectClassDefinition newOC = new ObjectClassDefinition(existing.getOID(), existing.getNames(), existing.getDescription(), existing.isObsolete(), existing.getSuperiorClasses(), existing.getObjectClassType(), existing.getRequiredAttributes(), newOptional, existing.getExtensions());
                newOCList.add(newOC.toString());
            }
        }
        final LinkedList<Modification> mods = new LinkedList<Modification>();
        if (!newAttrList.isEmpty()) {
            final String[] newAttrValues = new String[newAttrList.size()];
            mods.add(new Modification(ModificationType.ADD, "attributeTypes", (String[])newAttrList.toArray(newAttrValues)));
        }
        if (!newOCList.isEmpty()) {
            final String[] newOCValues = new String[newOCList.size()];
            mods.add(new Modification(ModificationType.ADD, "objectClasses", (String[])newOCList.toArray(newOCValues)));
        }
        if (mods.isEmpty()) {
            return false;
        }
        i.modify(s.getSchemaEntry().getDN(), mods);
        return true;
    }
    
    private static void addMissingAttrs(final String[] names, final Set<AttributeTypeDefinition> required, final Set<AttributeTypeDefinition> optional, final Set<String> missing) {
        for (final String name : names) {
            boolean found = false;
            for (final AttributeTypeDefinition eA : required) {
                if (eA.hasNameOrOID(name)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                for (final AttributeTypeDefinition eA : optional) {
                    if (eA.hasNameOrOID(name)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    missing.add(name);
                }
            }
        }
    }
    
    public Entry encode(final T o, final String parentDN) throws LDAPPersistException {
        Validator.ensureNotNull(o);
        return this.handler.encode(o, parentDN);
    }
    
    public T decode(final Entry entry) throws LDAPPersistException {
        Validator.ensureNotNull(entry);
        return this.handler.decode(entry);
    }
    
    public void decode(final T o, final Entry entry) throws LDAPPersistException {
        Validator.ensureNotNull(o, entry);
        this.handler.decode(o, entry);
    }
    
    public LDAPResult add(final T o, final LDAPInterface i, final String parentDN, final Control... controls) throws LDAPPersistException {
        Validator.ensureNotNull(o, i);
        final Entry e = this.encode(o, parentDN);
        try {
            final AddRequest addRequest = new AddRequest(e);
            if (controls != null) {
                addRequest.setControls(controls);
            }
            return i.add(addRequest);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPPersistException(le);
        }
    }
    
    public LDAPResult delete(final T o, final LDAPInterface i, final Control... controls) throws LDAPPersistException {
        Validator.ensureNotNull(o, i);
        final String dn = this.handler.getEntryDN(o);
        if (dn == null) {
            throw new LDAPPersistException(PersistMessages.ERR_PERSISTER_DELETE_NO_DN.get());
        }
        try {
            final DeleteRequest deleteRequest = new DeleteRequest(dn);
            if (controls != null) {
                deleteRequest.setControls(controls);
            }
            return i.delete(deleteRequest);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPPersistException(le);
        }
    }
    
    public List<Modification> getModifications(final T o, final boolean deleteNullValues, final String... attributes) throws LDAPPersistException {
        return this.getModifications(o, deleteNullValues, false, attributes);
    }
    
    public List<Modification> getModifications(final T o, final boolean deleteNullValues, final boolean byteForByte, final String... attributes) throws LDAPPersistException {
        Validator.ensureNotNull(o);
        return this.handler.getModifications(o, deleteNullValues, byteForByte, attributes);
    }
    
    public LDAPResult modify(final T o, final LDAPInterface i, final String dn, final boolean deleteNullValues, final String... attributes) throws LDAPPersistException {
        return this.modify(o, i, dn, deleteNullValues, attributes, LDAPPersister.NO_CONTROLS);
    }
    
    public LDAPResult modify(final T o, final LDAPInterface i, final String dn, final boolean deleteNullValues, final String[] attributes, final Control... controls) throws LDAPPersistException {
        return this.modify(o, i, dn, deleteNullValues, false, attributes, controls);
    }
    
    public LDAPResult modify(final T o, final LDAPInterface i, final String dn, final boolean deleteNullValues, final boolean byteForByte, final String[] attributes, final Control... controls) throws LDAPPersistException {
        Validator.ensureNotNull(o, i);
        final List<Modification> mods = this.handler.getModifications(o, deleteNullValues, byteForByte, attributes);
        if (mods.isEmpty()) {
            return null;
        }
        String targetDN;
        if (dn == null) {
            targetDN = this.handler.getEntryDN(o);
            if (targetDN == null) {
                throw new LDAPPersistException(PersistMessages.ERR_PERSISTER_MODIFY_NO_DN.get());
            }
        }
        else {
            targetDN = dn;
        }
        try {
            final ModifyRequest modifyRequest = new ModifyRequest(targetDN, mods);
            if (controls != null) {
                modifyRequest.setControls(controls);
            }
            return i.modify(modifyRequest);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPPersistException(le);
        }
    }
    
    public BindResult bind(final T o, final String baseDN, final String password, final LDAPConnection c, final Control... controls) throws LDAPException {
        Validator.ensureNotNull(o, password, c);
        String dn = this.handler.getEntryDN(o);
        if (dn == null) {
            String base = baseDN;
            if (base == null) {
                base = this.handler.getDefaultParentDN().toString();
            }
            final SearchRequest r = new SearchRequest(base, SearchScope.SUB, this.handler.createFilter(o), new String[] { "1.1" });
            r.setSizeLimit(1);
            final Entry e = c.searchForEntry(r);
            if (e == null) {
                throw new LDAPException(ResultCode.NO_RESULTS_RETURNED, PersistMessages.ERR_PERSISTER_BIND_NO_ENTRY_FOUND.get());
            }
            dn = e.getDN();
        }
        return c.bind(new SimpleBindRequest(dn, password, controls));
    }
    
    public T get(final T o, final LDAPInterface i, final String parentDN) throws LDAPPersistException {
        final String dn = this.handler.constructDN(o, parentDN);
        Entry entry;
        try {
            entry = i.getEntry(dn, this.handler.getAttributesToRequest());
            if (entry == null) {
                return null;
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPPersistException(le);
        }
        return this.decode(entry);
    }
    
    public T get(final String dn, final LDAPInterface i) throws LDAPPersistException {
        Entry entry;
        try {
            entry = i.getEntry(dn, this.handler.getAttributesToRequest());
            if (entry == null) {
                return null;
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPPersistException(le);
        }
        return this.decode(entry);
    }
    
    public void lazilyLoad(final T o, final LDAPInterface i, final FieldInfo... fields) throws LDAPPersistException {
        Validator.ensureNotNull(o, i);
        String[] attrs;
        if (fields == null || fields.length == 0) {
            attrs = this.handler.getLazilyLoadedAttributes();
        }
        else {
            final ArrayList<String> attrList = new ArrayList<String>(fields.length);
            for (final FieldInfo f : fields) {
                if (f.lazilyLoad()) {
                    attrList.add(f.getAttributeName());
                }
            }
            attrs = new String[attrList.size()];
            attrList.toArray(attrs);
        }
        if (attrs.length == 0) {
            return;
        }
        final String dn = this.handler.getEntryDN(o);
        if (dn == null) {
            throw new LDAPPersistException(PersistMessages.ERR_PERSISTER_LAZILY_LOAD_NO_DN.get());
        }
        Entry entry;
        try {
            entry = i.getEntry(this.handler.getEntryDN(o), attrs);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPPersistException(le);
        }
        if (entry == null) {
            throw new LDAPPersistException(PersistMessages.ERR_PERSISTER_LAZILY_LOAD_NO_ENTRY.get(dn));
        }
        boolean successful = true;
        final ArrayList<String> failureReasons = new ArrayList<String>(5);
        final Map<String, FieldInfo> fieldMap = this.handler.getFields();
        for (final Attribute a : entry.getAttributes()) {
            final String lowerName = StaticUtils.toLowerCase(a.getName());
            final FieldInfo f2 = fieldMap.get(lowerName);
            if (f2 != null) {
                successful &= f2.decode(o, entry, failureReasons);
            }
        }
        if (!successful) {
            throw new LDAPPersistException(StaticUtils.concatenateStrings(failureReasons), o, null);
        }
    }
    
    public PersistedObjects<T> search(final T o, final LDAPConnection c) throws LDAPPersistException {
        return this.search(o, c, null, SearchScope.SUB, DereferencePolicy.NEVER, 0, 0, null, LDAPPersister.NO_CONTROLS);
    }
    
    public PersistedObjects<T> search(final T o, final LDAPConnection c, final String baseDN, final SearchScope scope) throws LDAPPersistException {
        return this.search(o, c, baseDN, scope, DereferencePolicy.NEVER, 0, 0, null, LDAPPersister.NO_CONTROLS);
    }
    
    public PersistedObjects<T> search(final T o, final LDAPConnection c, final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final Filter extraFilter, final Control... controls) throws LDAPPersistException {
        Validator.ensureNotNull(o, c, scope, derefPolicy);
        String base;
        if (baseDN == null) {
            base = this.handler.getDefaultParentDN().toString();
        }
        else {
            base = baseDN;
        }
        Filter filter;
        if (extraFilter == null) {
            filter = this.handler.createFilter(o);
        }
        else {
            filter = Filter.createANDFilter(this.handler.createFilter(o), extraFilter);
        }
        final SearchRequest searchRequest = new SearchRequest(base, scope, derefPolicy, sizeLimit, timeLimit, false, filter, this.handler.getAttributesToRequest());
        if (controls != null) {
            searchRequest.setControls(controls);
        }
        LDAPEntrySource entrySource;
        try {
            entrySource = new LDAPEntrySource(c, searchRequest, false);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPPersistException(le);
        }
        return new PersistedObjects<T>(this, entrySource);
    }
    
    public SearchResult search(final T o, final LDAPInterface i, final ObjectSearchListener<T> l) throws LDAPPersistException {
        return this.search(o, i, null, SearchScope.SUB, DereferencePolicy.NEVER, 0, 0, null, l, LDAPPersister.NO_CONTROLS);
    }
    
    public SearchResult search(final T o, final LDAPInterface i, final String baseDN, final SearchScope scope, final ObjectSearchListener<T> l) throws LDAPPersistException {
        return this.search(o, i, baseDN, scope, DereferencePolicy.NEVER, 0, 0, null, l, LDAPPersister.NO_CONTROLS);
    }
    
    public SearchResult search(final T o, final LDAPInterface i, final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final Filter extraFilter, final ObjectSearchListener<T> l, final Control... controls) throws LDAPPersistException {
        Validator.ensureNotNull(o, i, scope, derefPolicy, l);
        String base;
        if (baseDN == null) {
            base = this.handler.getDefaultParentDN().toString();
        }
        else {
            base = baseDN;
        }
        Filter filter;
        if (extraFilter == null) {
            filter = this.handler.createFilter(o);
        }
        else {
            filter = Filter.simplifyFilter(Filter.createANDFilter(this.handler.createFilter(o), extraFilter), true);
        }
        final SearchListenerBridge<T> bridge = new SearchListenerBridge<T>(this, l);
        final SearchRequest searchRequest = new SearchRequest(bridge, base, scope, derefPolicy, sizeLimit, timeLimit, false, filter, this.handler.getAttributesToRequest());
        if (controls != null) {
            searchRequest.setControls(controls);
        }
        try {
            return i.search(searchRequest);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPPersistException(le);
        }
    }
    
    public PersistedObjects<T> search(final LDAPConnection c, final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final Filter filter, final Control... controls) throws LDAPPersistException {
        Validator.ensureNotNull(c, scope, derefPolicy, filter);
        String base;
        if (baseDN == null) {
            base = this.handler.getDefaultParentDN().toString();
        }
        else {
            base = baseDN;
        }
        final Filter f = Filter.createANDFilter(filter, this.handler.createBaseFilter());
        final SearchRequest searchRequest = new SearchRequest(base, scope, derefPolicy, sizeLimit, timeLimit, false, f, this.handler.getAttributesToRequest());
        if (controls != null) {
            searchRequest.setControls(controls);
        }
        LDAPEntrySource entrySource;
        try {
            entrySource = new LDAPEntrySource(c, searchRequest, false);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPPersistException(le);
        }
        return new PersistedObjects<T>(this, entrySource);
    }
    
    public SearchResult search(final LDAPInterface i, final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final Filter filter, final ObjectSearchListener<T> l, final Control... controls) throws LDAPPersistException {
        Validator.ensureNotNull(i, scope, derefPolicy, filter, l);
        String base;
        if (baseDN == null) {
            base = this.handler.getDefaultParentDN().toString();
        }
        else {
            base = baseDN;
        }
        final Filter f = Filter.simplifyFilter(Filter.createANDFilter(filter, this.handler.createBaseFilter()), true);
        final SearchListenerBridge<T> bridge = new SearchListenerBridge<T>(this, l);
        final SearchRequest searchRequest = new SearchRequest(bridge, base, scope, derefPolicy, sizeLimit, timeLimit, false, f, this.handler.getAttributesToRequest());
        if (controls != null) {
            searchRequest.setControls(controls);
        }
        try {
            return i.search(searchRequest);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPPersistException(le);
        }
    }
    
    public T searchForObject(final T o, final LDAPInterface i) throws LDAPPersistException {
        return this.searchForObject(o, i, null, SearchScope.SUB, DereferencePolicy.NEVER, 0, 0, null, LDAPPersister.NO_CONTROLS);
    }
    
    public T searchForObject(final T o, final LDAPInterface i, final String baseDN, final SearchScope scope) throws LDAPPersistException {
        return this.searchForObject(o, i, baseDN, scope, DereferencePolicy.NEVER, 0, 0, null, LDAPPersister.NO_CONTROLS);
    }
    
    public T searchForObject(final T o, final LDAPInterface i, final String baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final int sizeLimit, final int timeLimit, final Filter extraFilter, final Control... controls) throws LDAPPersistException {
        Validator.ensureNotNull(o, i, scope, derefPolicy);
        String base;
        if (baseDN == null) {
            base = this.handler.getDefaultParentDN().toString();
        }
        else {
            base = baseDN;
        }
        Filter filter;
        if (extraFilter == null) {
            filter = this.handler.createFilter(o);
        }
        else {
            filter = Filter.simplifyFilter(Filter.createANDFilter(this.handler.createFilter(o), extraFilter), true);
        }
        final SearchRequest searchRequest = new SearchRequest(base, scope, derefPolicy, sizeLimit, timeLimit, false, filter, this.handler.getAttributesToRequest());
        if (controls != null) {
            searchRequest.setControls(controls);
        }
        try {
            final Entry e = i.searchForEntry(searchRequest);
            if (e == null) {
                return null;
            }
            return this.decode(e);
        }
        catch (final LDAPPersistException lpe) {
            Debug.debugException(lpe);
            throw lpe;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPPersistException(le);
        }
    }
    
    public SearchResult getAll(final LDAPInterface i, final String baseDN, final ObjectSearchListener<T> l, final Control... controls) throws LDAPPersistException {
        Validator.ensureNotNull(i, l);
        String base;
        if (baseDN == null) {
            base = this.handler.getDefaultParentDN().toString();
        }
        else {
            base = baseDN;
        }
        final SearchListenerBridge<T> bridge = new SearchListenerBridge<T>(this, l);
        final SearchRequest searchRequest = new SearchRequest(bridge, base, SearchScope.SUB, DereferencePolicy.NEVER, 0, 0, false, this.handler.createBaseFilter(), this.handler.getAttributesToRequest());
        if (controls != null) {
            searchRequest.setControls(controls);
        }
        try {
            return i.search(searchRequest);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw new LDAPPersistException(le);
        }
    }
    
    static {
        NO_CONTROLS = new Control[0];
        INSTANCES = new ConcurrentHashMap<Class<?>, LDAPPersister<?>>(StaticUtils.computeMapCapacity(10));
    }
}
