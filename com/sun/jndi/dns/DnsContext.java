package com.sun.jndi.dns;

import javax.naming.ConfigurationException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.InvalidNameException;
import javax.naming.CompositeName;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.DirContext;
import javax.naming.directory.Attributes;
import javax.naming.OperationNotSupportedException;
import javax.naming.Binding;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.Context;
import javax.naming.spi.DirectoryManager;
import com.sun.jndi.toolkit.ctx.Continuation;
import javax.naming.Name;
import javax.naming.directory.InvalidAttributeIdentifierException;
import javax.naming.NamingException;
import javax.naming.NameParser;
import java.util.Hashtable;
import com.sun.jndi.toolkit.ctx.ComponentDirContext;

public class DnsContext extends ComponentDirContext
{
    DnsName domain;
    Hashtable<Object, Object> environment;
    private boolean envShared;
    private boolean parentIsDns;
    private String[] servers;
    private com.sun.jndi.dns.Resolver resolver;
    private boolean authoritative;
    private boolean recursion;
    private int timeout;
    private int retries;
    static final NameParser nameParser;
    private static final int DEFAULT_INIT_TIMEOUT = 1000;
    private static final int DEFAULT_RETRIES = 4;
    private static final String INIT_TIMEOUT = "com.sun.jndi.dns.timeout.initial";
    private static final String RETRIES = "com.sun.jndi.dns.timeout.retries";
    private CT lookupCT;
    private static final String LOOKUP_ATTR = "com.sun.jndi.dns.lookup.attr";
    private static final String RECURSION = "com.sun.jndi.dns.recursion";
    private static final int ANY = 255;
    private static final ZoneNode zoneTree;
    private static final boolean debug = false;
    
    public DnsContext(final String s, final String[] array, final Hashtable<?, ?> hashtable) throws NamingException {
        this.domain = new DnsName(s.endsWith(".") ? s : (s + "."));
        this.servers = (String[])((array == null) ? null : ((String[])array.clone()));
        this.environment = (Hashtable)hashtable.clone();
        this.envShared = false;
        this.parentIsDns = false;
        this.resolver = null;
        this.initFromEnvironment();
    }
    
    DnsContext(final DnsContext dnsContext, final DnsName domain) {
        this(dnsContext);
        this.domain = domain;
        this.parentIsDns = true;
    }
    
    private DnsContext(final DnsContext dnsContext) {
        this.environment = dnsContext.environment;
        final boolean b = true;
        dnsContext.envShared = b;
        this.envShared = b;
        this.parentIsDns = dnsContext.parentIsDns;
        this.domain = dnsContext.domain;
        this.servers = dnsContext.servers;
        this.resolver = dnsContext.resolver;
        this.authoritative = dnsContext.authoritative;
        this.recursion = dnsContext.recursion;
        this.timeout = dnsContext.timeout;
        this.retries = dnsContext.retries;
        this.lookupCT = dnsContext.lookupCT;
    }
    
    @Override
    public void close() {
        if (this.resolver != null) {
            this.resolver.close();
            this.resolver = null;
        }
    }
    
    @Override
    protected Hashtable<?, ?> p_getEnvironment() {
        return this.environment;
    }
    
    @Override
    public Hashtable<?, ?> getEnvironment() throws NamingException {
        return (Hashtable)this.environment.clone();
    }
    
    @Override
    public Object addToEnvironment(final String s, final Object o) throws NamingException {
        if (s.equals("com.sun.jndi.dns.lookup.attr")) {
            this.lookupCT = this.getLookupCT((String)o);
        }
        else if (s.equals("java.naming.authoritative")) {
            this.authoritative = "true".equalsIgnoreCase((String)o);
        }
        else if (s.equals("com.sun.jndi.dns.recursion")) {
            this.recursion = "true".equalsIgnoreCase((String)o);
        }
        else if (s.equals("com.sun.jndi.dns.timeout.initial")) {
            final int int1 = Integer.parseInt((String)o);
            if (this.timeout != int1) {
                this.timeout = int1;
                this.resolver = null;
            }
        }
        else if (s.equals("com.sun.jndi.dns.timeout.retries")) {
            final int int2 = Integer.parseInt((String)o);
            if (this.retries != int2) {
                this.retries = int2;
                this.resolver = null;
            }
        }
        if (!this.envShared) {
            return this.environment.put(s, o);
        }
        if (this.environment.get(s) != o) {
            this.environment = (Hashtable)this.environment.clone();
            this.envShared = false;
            return this.environment.put(s, o);
        }
        return o;
    }
    
    @Override
    public Object removeFromEnvironment(final String s) throws NamingException {
        if (s.equals("com.sun.jndi.dns.lookup.attr")) {
            this.lookupCT = this.getLookupCT(null);
        }
        else if (s.equals("java.naming.authoritative")) {
            this.authoritative = false;
        }
        else if (s.equals("com.sun.jndi.dns.recursion")) {
            this.recursion = true;
        }
        else if (s.equals("com.sun.jndi.dns.timeout.initial")) {
            if (this.timeout != 1000) {
                this.timeout = 1000;
                this.resolver = null;
            }
        }
        else if (s.equals("com.sun.jndi.dns.timeout.retries") && this.retries != 4) {
            this.retries = 4;
            this.resolver = null;
        }
        if (!this.envShared) {
            return this.environment.remove(s);
        }
        if (this.environment.get(s) != null) {
            this.environment = (Hashtable)this.environment.clone();
            this.envShared = false;
            return this.environment.remove(s);
        }
        return null;
    }
    
    void setProviderUrl(final String s) {
        this.environment.put("java.naming.provider.url", s);
    }
    
    private void initFromEnvironment() throws InvalidAttributeIdentifierException {
        this.lookupCT = this.getLookupCT(this.environment.get("com.sun.jndi.dns.lookup.attr"));
        this.authoritative = "true".equalsIgnoreCase(this.environment.get("java.naming.authoritative"));
        final String s = this.environment.get("com.sun.jndi.dns.recursion");
        this.recursion = (s == null || "true".equalsIgnoreCase(s));
        final String s2 = this.environment.get("com.sun.jndi.dns.timeout.initial");
        this.timeout = ((s2 == null) ? 1000 : Integer.parseInt(s2));
        final String s3 = this.environment.get("com.sun.jndi.dns.timeout.retries");
        this.retries = ((s3 == null) ? 4 : Integer.parseInt(s3));
    }
    
    private CT getLookupCT(final String s) throws InvalidAttributeIdentifierException {
        return (s == null) ? new CT(1, 16) : fromAttrId(s);
    }
    
    public Object c_lookup(final Name name, final Continuation continuation) throws NamingException {
        continuation.setSuccess();
        if (name.isEmpty()) {
            final DnsContext dnsContext = new DnsContext(this);
            dnsContext.resolver = new com.sun.jndi.dns.Resolver(this.servers, this.timeout, this.retries);
            return dnsContext;
        }
        try {
            final DnsName fullyQualify = this.fullyQualify(name);
            return DirectoryManager.getObjectInstance(new DnsContext(this, fullyQualify), name, this, this.environment, rrsToAttrs(this.getResolver().query(fullyQualify, this.lookupCT.rrclass, this.lookupCT.rrtype, this.recursion, this.authoritative), null));
        }
        catch (final NamingException ex) {
            continuation.setError(this, name);
            throw continuation.fillInException(ex);
        }
        catch (final Exception rootCause) {
            continuation.setError(this, name);
            final NamingException ex2 = new NamingException("Problem generating object using object factory");
            ex2.setRootCause(rootCause);
            throw continuation.fillInException(ex2);
        }
    }
    
    public Object c_lookupLink(final Name name, final Continuation continuation) throws NamingException {
        return this.c_lookup(name, continuation);
    }
    
    public NamingEnumeration<NameClassPair> c_list(final Name name, final Continuation continuation) throws NamingException {
        continuation.setSuccess();
        try {
            final DnsName fullyQualify = this.fullyQualify(name);
            return new NameClassPairEnumeration(new DnsContext(this, fullyQualify), this.getNameNode(fullyQualify).getChildren());
        }
        catch (final NamingException ex) {
            continuation.setError(this, name);
            throw continuation.fillInException(ex);
        }
    }
    
    public NamingEnumeration<Binding> c_listBindings(final Name name, final Continuation continuation) throws NamingException {
        continuation.setSuccess();
        try {
            final DnsName fullyQualify = this.fullyQualify(name);
            return new BindingEnumeration(new DnsContext(this, fullyQualify), this.getNameNode(fullyQualify).getChildren());
        }
        catch (final NamingException ex) {
            continuation.setError(this, name);
            throw continuation.fillInException(ex);
        }
    }
    
    public void c_bind(final Name name, final Object o, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    public void c_rebind(final Name name, final Object o, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    public void c_unbind(final Name name, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    public void c_rename(final Name name, final Name name2, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    public Context c_createSubcontext(final Name name, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    public void c_destroySubcontext(final Name name, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    public NameParser c_getNameParser(final Name name, final Continuation continuation) throws NamingException {
        continuation.setSuccess();
        return DnsContext.nameParser;
    }
    
    public void c_bind(final Name name, final Object o, final Attributes attributes, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    public void c_rebind(final Name name, final Object o, final Attributes attributes, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    public DirContext c_createSubcontext(final Name name, final Attributes attributes, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    public Attributes c_getAttributes(final Name name, final String[] array, final Continuation continuation) throws NamingException {
        continuation.setSuccess();
        try {
            final DnsName fullyQualify = this.fullyQualify(name);
            final CT[] attrIdsToClassesAndTypes = attrIdsToClassesAndTypes(array);
            final CT classAndTypeToQuery = getClassAndTypeToQuery(attrIdsToClassesAndTypes);
            return rrsToAttrs(this.getResolver().query(fullyQualify, classAndTypeToQuery.rrclass, classAndTypeToQuery.rrtype, this.recursion, this.authoritative), attrIdsToClassesAndTypes);
        }
        catch (final NamingException ex) {
            continuation.setError(this, name);
            throw continuation.fillInException(ex);
        }
    }
    
    public void c_modifyAttributes(final Name name, final int n, final Attributes attributes, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    public void c_modifyAttributes(final Name name, final ModificationItem[] array, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    public NamingEnumeration<SearchResult> c_search(final Name name, final Attributes attributes, final String[] array, final Continuation continuation) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    public NamingEnumeration<SearchResult> c_search(final Name name, final String s, final SearchControls searchControls, final Continuation continuation) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    public NamingEnumeration<SearchResult> c_search(final Name name, final String s, final Object[] array, final SearchControls searchControls, final Continuation continuation) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    public DirContext c_getSchema(final Name name, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    public DirContext c_getSchemaClassDefinition(final Name name, final Continuation continuation) throws NamingException {
        continuation.setError(this, name);
        throw continuation.fillInException(new OperationNotSupportedException());
    }
    
    @Override
    public String getNameInNamespace() {
        return this.domain.toString();
    }
    
    @Override
    public Name composeName(Name addAll, Name addAll2) throws NamingException {
        if (!(addAll2 instanceof DnsName) && !(addAll2 instanceof CompositeName)) {
            addAll2 = new DnsName().addAll(addAll2);
        }
        if (!(addAll instanceof DnsName) && !(addAll instanceof CompositeName)) {
            addAll = new DnsName().addAll(addAll);
        }
        if (addAll2 instanceof DnsName && addAll instanceof DnsName) {
            final DnsName dnsName = (DnsName)addAll2.clone();
            dnsName.addAll(addAll);
            return new CompositeName().add(dnsName.toString());
        }
        final Name name = (addAll2 instanceof CompositeName) ? addAll2 : new CompositeName().add(addAll2.toString());
        final Name name2 = (addAll instanceof CompositeName) ? addAll : new CompositeName().add(addAll.toString());
        final int n = name.size() - 1;
        if (name2.isEmpty() || name2.get(0).equals("") || name.isEmpty() || name.get(n).equals("")) {
            return super.composeName(name2, name);
        }
        final CompositeName compositeName = (CompositeName)((addAll2 == name) ? ((CompositeName)name.clone()) : name);
        compositeName.addAll(name2);
        if (this.parentIsDns) {
            final Object o = (addAll2 instanceof DnsName) ? addAll2.clone() : new DnsName(name.get(n));
            ((DnsName)o).addAll((addAll instanceof DnsName) ? addAll : new DnsName(name2.get(0)));
            compositeName.remove(n + 1);
            compositeName.remove(n);
            compositeName.add(n, ((DnsName)o).toString());
        }
        return compositeName;
    }
    
    private synchronized com.sun.jndi.dns.Resolver getResolver() throws NamingException {
        if (this.resolver == null) {
            this.resolver = new com.sun.jndi.dns.Resolver(this.servers, this.timeout, this.retries);
        }
        return this.resolver;
    }
    
    DnsName fullyQualify(final Name name) throws NamingException {
        if (name.isEmpty()) {
            return this.domain;
        }
        final DnsName dnsName = (name instanceof CompositeName) ? new DnsName(name.get(0)) : new DnsName().addAll(name);
        if (!dnsName.hasRootLabel()) {
            return (DnsName)dnsName.addAll(0, this.domain);
        }
        if (this.domain.size() == 1) {
            return dnsName;
        }
        throw new InvalidNameException("DNS name " + dnsName + " not relative to " + this.domain);
    }
    
    private static Attributes rrsToAttrs(final ResourceRecords resourceRecords, final CT[] array) {
        final BasicAttributes basicAttributes = new BasicAttributes(true);
        for (int i = 0; i < resourceRecords.answer.size(); ++i) {
            final ResourceRecord resourceRecord = resourceRecords.answer.elementAt(i);
            final int type = resourceRecord.getType();
            final int rrclass = resourceRecord.getRrclass();
            if (classAndTypeMatch(rrclass, type, array)) {
                final String attrId = toAttrId(rrclass, type);
                Attribute value = basicAttributes.get(attrId);
                if (value == null) {
                    value = new BasicAttribute(attrId);
                    basicAttributes.put(value);
                }
                value.add(resourceRecord.getRdata());
            }
        }
        return basicAttributes;
    }
    
    private static boolean classAndTypeMatch(final int n, final int n2, final CT[] array) {
        if (array == null) {
            return true;
        }
        for (int i = 0; i < array.length; ++i) {
            final CT ct = array[i];
            final boolean b = ct.rrclass == 255 || ct.rrclass == n;
            final boolean b2 = ct.rrtype == 255 || ct.rrtype == n2;
            if (b && b2) {
                return true;
            }
        }
        return false;
    }
    
    private static String toAttrId(final int n, final int n2) {
        String s = ResourceRecord.getTypeName(n2);
        if (n != 1) {
            s = ResourceRecord.getRrclassName(n) + " " + s;
        }
        return s;
    }
    
    private static CT fromAttrId(final String s) throws InvalidAttributeIdentifierException {
        if (s.equals("")) {
            throw new InvalidAttributeIdentifierException("Attribute ID cannot be empty");
        }
        final int index = s.indexOf(32);
        int rrclass;
        if (index < 0) {
            rrclass = 1;
        }
        else {
            final String substring = s.substring(0, index);
            rrclass = ResourceRecord.getRrclass(substring);
            if (rrclass < 0) {
                throw new InvalidAttributeIdentifierException("Unknown resource record class '" + substring + '\'');
            }
        }
        final String substring2 = s.substring(index + 1);
        final int type = ResourceRecord.getType(substring2);
        if (type < 0) {
            throw new InvalidAttributeIdentifierException("Unknown resource record type '" + substring2 + '\'');
        }
        return new CT(rrclass, type);
    }
    
    private static CT[] attrIdsToClassesAndTypes(final String[] array) throws InvalidAttributeIdentifierException {
        if (array == null) {
            return null;
        }
        final CT[] array2 = new CT[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = fromAttrId(array[i]);
        }
        return array2;
    }
    
    private static CT getClassAndTypeToQuery(final CT[] array) {
        int rrclass;
        int rrtype;
        if (array == null) {
            rrclass = 255;
            rrtype = 255;
        }
        else if (array.length == 0) {
            rrclass = 1;
            rrtype = 255;
        }
        else {
            rrclass = array[0].rrclass;
            rrtype = array[0].rrtype;
            for (int i = 1; i < array.length; ++i) {
                if (rrclass != array[i].rrclass) {
                    rrclass = 255;
                }
                if (rrtype != array[i].rrtype) {
                    rrtype = 255;
                }
            }
        }
        return new CT(rrclass, rrtype);
    }
    
    private NameNode getNameNode(final DnsName dnsName) throws NamingException {
        dprint("getNameNode(" + dnsName + ")");
        ZoneNode deepestPopulated;
        synchronized (DnsContext.zoneTree) {
            deepestPopulated = DnsContext.zoneTree.getDeepestPopulated(dnsName);
        }
        dprint("Deepest related zone in zone tree: " + ((deepestPopulated != null) ? deepestPopulated.getLabel() : "[none]"));
        if (deepestPopulated != null) {
            final NameNode contents;
            synchronized (deepestPopulated) {
                contents = deepestPopulated.getContents();
            }
            if (contents != null) {
                final NameNode value = contents.get(dnsName, deepestPopulated.depth() + 1);
                if (value != null && !value.isZoneCut()) {
                    dprint("Found node " + dnsName + " in zone tree");
                    final boolean zoneCurrent = this.isZoneCurrent(deepestPopulated, (DnsName)dnsName.getPrefix(deepestPopulated.depth() + 1));
                    boolean b = false;
                    synchronized (deepestPopulated) {
                        if (contents != deepestPopulated.getContents()) {
                            b = true;
                        }
                        else {
                            if (zoneCurrent) {
                                return value;
                            }
                            deepestPopulated.depopulate();
                        }
                    }
                    dprint("Zone not current; discarding node");
                    if (b) {
                        return this.getNameNode(dnsName);
                    }
                }
            }
        }
        dprint("Adding node " + dnsName + " to zone tree");
        final DnsName zoneName = this.getResolver().findZoneName(dnsName, 1, this.recursion);
        dprint("Node's zone is " + zoneName);
        synchronized (DnsContext.zoneTree) {
            deepestPopulated = (ZoneNode)DnsContext.zoneTree.add(zoneName, 1);
        }
        NameNode contents;
        synchronized (deepestPopulated) {
            contents = (deepestPopulated.isPopulated() ? deepestPopulated.getContents() : this.populateZone(deepestPopulated, zoneName));
        }
        final NameNode value2 = contents.get(dnsName, zoneName.size());
        if (value2 == null) {
            throw new ConfigurationException("DNS error: node not found in its own zone");
        }
        dprint("Found node in newly-populated zone");
        return value2;
    }
    
    private NameNode populateZone(final ZoneNode zoneNode, final DnsName dnsName) throws NamingException {
        dprint("Populating zone " + dnsName);
        final ResourceRecords queryZone = this.getResolver().queryZone(dnsName, 1, this.recursion);
        dprint("zone xfer complete: " + queryZone.answer.size() + " records");
        return zoneNode.populate(dnsName, queryZone);
    }
    
    private boolean isZoneCurrent(final ZoneNode zoneNode, final DnsName dnsName) throws NamingException {
        if (!zoneNode.isPopulated()) {
            return false;
        }
        final ResourceRecord soa = this.getResolver().findSoa(dnsName, 1, this.recursion);
        synchronized (zoneNode) {
            if (soa == null) {
                zoneNode.depopulate();
            }
            return zoneNode.isPopulated() && zoneNode.compareSerialNumberTo(soa) >= 0;
        }
    }
    
    private static final void dprint(final String s) {
    }
    
    static {
        nameParser = new DnsNameParser();
        zoneTree = new ZoneNode(null);
    }
}
