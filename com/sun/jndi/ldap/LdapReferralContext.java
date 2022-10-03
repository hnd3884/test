package com.sun.jndi.ldap;

import java.util.StringTokenizer;
import javax.naming.InvalidNameException;
import javax.naming.ldap.ExtendedResponse;
import javax.naming.ldap.ExtendedRequest;
import com.sun.jndi.toolkit.dir.SearchFilter;
import javax.naming.directory.SearchResult;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.Attributes;
import javax.naming.NameParser;
import javax.naming.Binding;
import javax.naming.directory.SearchControls;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.CompositeName;
import javax.naming.NotContextException;
import javax.naming.Context;
import javax.naming.spi.NamingManager;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.ldap.Control;
import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.Name;
import javax.naming.ldap.LdapContext;
import javax.naming.directory.DirContext;

final class LdapReferralContext implements DirContext, LdapContext
{
    private DirContext refCtx;
    private Name urlName;
    private String urlAttrs;
    private String urlScope;
    private String urlFilter;
    private LdapReferralException refEx;
    private boolean skipThisReferral;
    private int hopCount;
    private NamingException previousEx;
    
    LdapReferralContext(final LdapReferralException refEx, Hashtable<?, ?> hashtable, final Control[] array, final Control[] requestControls, final String s, final boolean skipThisReferral, final int n) throws NamingException {
        this.refCtx = null;
        this.urlName = null;
        this.urlAttrs = null;
        this.urlScope = null;
        this.urlFilter = null;
        this.refEx = null;
        this.skipThisReferral = false;
        this.hopCount = 1;
        this.previousEx = null;
        this.refEx = refEx;
        this.skipThisReferral = skipThisReferral;
        if (skipThisReferral) {
            return;
        }
        if (hashtable != null) {
            hashtable = (Hashtable)hashtable.clone();
            if (array == null) {
                hashtable.remove("java.naming.ldap.control.connect");
            }
        }
        else if (array != null) {
            hashtable = new Hashtable<Object, Object>(5);
        }
        if (array != null) {
            final Control[] array2 = new Control[array.length];
            System.arraycopy(array, 0, array2, 0, array.length);
            hashtable.put("java.naming.ldap.control.connect", array2);
        }
        String nextReferral;
        Object objectInstance;
        while (true) {
            try {
                nextReferral = this.refEx.getNextReferral();
                if (nextReferral == null) {
                    if (this.previousEx != null) {
                        throw (NamingException)this.previousEx.fillInStackTrace();
                    }
                    throw new NamingException("Illegal encoding: referral is empty");
                }
            }
            catch (final LdapReferralException refEx2) {
                if (n == 2) {
                    throw refEx2;
                }
                this.refEx = refEx2;
                continue;
            }
            final Reference reference = new Reference("javax.naming.directory.DirContext", new StringRefAddr("URL", nextReferral));
            try {
                objectInstance = NamingManager.getObjectInstance(reference, null, null, hashtable);
            }
            catch (final NamingException previousEx) {
                if (n == 2) {
                    throw previousEx;
                }
                this.previousEx = previousEx;
                continue;
            }
            catch (final Exception rootCause) {
                final NamingException ex = new NamingException("problem generating object using object factory");
                ex.setRootCause(rootCause);
                throw ex;
            }
            break;
        }
        if (objectInstance instanceof DirContext) {
            this.refCtx = (DirContext)objectInstance;
            if (this.refCtx instanceof LdapContext && requestControls != null) {
                ((LdapContext)this.refCtx).setRequestControls(requestControls);
            }
            this.initDefaults(nextReferral, s);
            return;
        }
        final NotContextException ex2 = new NotContextException("Cannot create context for: " + nextReferral);
        ex2.setRemainingName(new CompositeName().add(s));
        throw ex2;
    }
    
    private void initDefaults(final String s, final String s2) throws NamingException {
        String dn;
        try {
            final LdapURL ldapURL = new LdapURL(s);
            dn = ldapURL.getDN();
            this.urlAttrs = ldapURL.getAttributes();
            this.urlScope = ldapURL.getScope();
            this.urlFilter = ldapURL.getFilter();
        }
        catch (final NamingException ex) {
            dn = s;
            final String urlAttrs = null;
            this.urlFilter = urlAttrs;
            this.urlScope = urlAttrs;
            this.urlAttrs = urlAttrs;
        }
        String s3;
        if (dn == null) {
            s3 = s2;
        }
        else {
            s3 = "";
        }
        if (s3 == null) {
            this.urlName = null;
        }
        else {
            this.urlName = (s3.equals("") ? new CompositeName() : new CompositeName().add(s3));
        }
    }
    
    @Override
    public void close() throws NamingException {
        if (this.refCtx != null) {
            this.refCtx.close();
            this.refCtx = null;
        }
        this.refEx = null;
    }
    
    void setHopCount(final int n) {
        this.hopCount = n;
        if (this.refCtx != null && this.refCtx instanceof LdapCtx) {
            ((LdapCtx)this.refCtx).setHopCount(n);
        }
    }
    
    @Override
    public Object lookup(final String s) throws NamingException {
        return this.lookup(this.toName(s));
    }
    
    @Override
    public Object lookup(final Name name) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        return this.refCtx.lookup(this.overrideName(name));
    }
    
    @Override
    public void bind(final String s, final Object o) throws NamingException {
        this.bind(this.toName(s), o);
    }
    
    @Override
    public void bind(final Name name, final Object o) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        this.refCtx.bind(this.overrideName(name), o);
    }
    
    @Override
    public void rebind(final String s, final Object o) throws NamingException {
        this.rebind(this.toName(s), o);
    }
    
    @Override
    public void rebind(final Name name, final Object o) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        this.refCtx.rebind(this.overrideName(name), o);
    }
    
    @Override
    public void unbind(final String s) throws NamingException {
        this.unbind(this.toName(s));
    }
    
    @Override
    public void unbind(final Name name) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        this.refCtx.unbind(this.overrideName(name));
    }
    
    @Override
    public void rename(final String s, final String s2) throws NamingException {
        this.rename(this.toName(s), this.toName(s2));
    }
    
    @Override
    public void rename(final Name name, final Name name2) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        this.refCtx.rename(this.overrideName(name), this.toName(this.refEx.getNewRdn()));
    }
    
    @Override
    public NamingEnumeration<NameClassPair> list(final String s) throws NamingException {
        return this.list(this.toName(s));
    }
    
    @Override
    public NamingEnumeration<NameClassPair> list(final Name name) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        try {
            Object o;
            if (this.urlScope != null && this.urlScope.equals("base")) {
                final SearchControls searchControls = new SearchControls();
                searchControls.setReturningObjFlag(true);
                searchControls.setSearchScope(0);
                o = this.refCtx.search(this.overrideName(name), "(objectclass=*)", searchControls);
            }
            else {
                o = this.refCtx.list(this.overrideName(name));
            }
            this.refEx.setNameResolved(true);
            ((ReferralEnumeration)o).appendUnprocessedReferrals(this.refEx);
            return (NamingEnumeration<NameClassPair>)o;
        }
        catch (final LdapReferralException ex) {
            ex.appendUnprocessedReferrals(this.refEx);
            throw (NamingException)ex.fillInStackTrace();
        }
        catch (final NamingException namingException) {
            if (this.refEx != null && !this.refEx.hasMoreReferrals()) {
                this.refEx.setNamingException(namingException);
            }
            if (this.refEx != null && (this.refEx.hasMoreReferrals() || this.refEx.hasMoreReferralExceptions())) {
                throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
            }
            throw namingException;
        }
    }
    
    @Override
    public NamingEnumeration<Binding> listBindings(final String s) throws NamingException {
        return this.listBindings(this.toName(s));
    }
    
    @Override
    public NamingEnumeration<Binding> listBindings(final Name name) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        try {
            Object o;
            if (this.urlScope != null && this.urlScope.equals("base")) {
                final SearchControls searchControls = new SearchControls();
                searchControls.setReturningObjFlag(true);
                searchControls.setSearchScope(0);
                o = this.refCtx.search(this.overrideName(name), "(objectclass=*)", searchControls);
            }
            else {
                o = this.refCtx.listBindings(this.overrideName(name));
            }
            this.refEx.setNameResolved(true);
            ((ReferralEnumeration)o).appendUnprocessedReferrals(this.refEx);
            return (NamingEnumeration<Binding>)o;
        }
        catch (final LdapReferralException ex) {
            ex.appendUnprocessedReferrals(this.refEx);
            throw (NamingException)ex.fillInStackTrace();
        }
        catch (final NamingException namingException) {
            if (this.refEx != null && !this.refEx.hasMoreReferrals()) {
                this.refEx.setNamingException(namingException);
            }
            if (this.refEx != null && (this.refEx.hasMoreReferrals() || this.refEx.hasMoreReferralExceptions())) {
                throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
            }
            throw namingException;
        }
    }
    
    @Override
    public void destroySubcontext(final String s) throws NamingException {
        this.destroySubcontext(this.toName(s));
    }
    
    @Override
    public void destroySubcontext(final Name name) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        this.refCtx.destroySubcontext(this.overrideName(name));
    }
    
    @Override
    public Context createSubcontext(final String s) throws NamingException {
        return this.createSubcontext(this.toName(s));
    }
    
    @Override
    public Context createSubcontext(final Name name) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        return this.refCtx.createSubcontext(this.overrideName(name));
    }
    
    @Override
    public Object lookupLink(final String s) throws NamingException {
        return this.lookupLink(this.toName(s));
    }
    
    @Override
    public Object lookupLink(final Name name) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        return this.refCtx.lookupLink(this.overrideName(name));
    }
    
    @Override
    public NameParser getNameParser(final String s) throws NamingException {
        return this.getNameParser(this.toName(s));
    }
    
    @Override
    public NameParser getNameParser(final Name name) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        return this.refCtx.getNameParser(this.overrideName(name));
    }
    
    @Override
    public String composeName(final String s, final String s2) throws NamingException {
        return this.composeName(this.toName(s), this.toName(s2)).toString();
    }
    
    @Override
    public Name composeName(final Name name, final Name name2) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        return this.refCtx.composeName(name, name2);
    }
    
    @Override
    public Object addToEnvironment(final String s, final Object o) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        return this.refCtx.addToEnvironment(s, o);
    }
    
    @Override
    public Object removeFromEnvironment(final String s) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        return this.refCtx.removeFromEnvironment(s);
    }
    
    @Override
    public Hashtable<?, ?> getEnvironment() throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        return this.refCtx.getEnvironment();
    }
    
    @Override
    public Attributes getAttributes(final String s) throws NamingException {
        return this.getAttributes(this.toName(s));
    }
    
    @Override
    public Attributes getAttributes(final Name name) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        return this.refCtx.getAttributes(this.overrideName(name));
    }
    
    @Override
    public Attributes getAttributes(final String s, final String[] array) throws NamingException {
        return this.getAttributes(this.toName(s), array);
    }
    
    @Override
    public Attributes getAttributes(final Name name, final String[] array) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        return this.refCtx.getAttributes(this.overrideName(name), array);
    }
    
    @Override
    public void modifyAttributes(final String s, final int n, final Attributes attributes) throws NamingException {
        this.modifyAttributes(this.toName(s), n, attributes);
    }
    
    @Override
    public void modifyAttributes(final Name name, final int n, final Attributes attributes) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        this.refCtx.modifyAttributes(this.overrideName(name), n, attributes);
    }
    
    @Override
    public void modifyAttributes(final String s, final ModificationItem[] array) throws NamingException {
        this.modifyAttributes(this.toName(s), array);
    }
    
    @Override
    public void modifyAttributes(final Name name, final ModificationItem[] array) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        this.refCtx.modifyAttributes(this.overrideName(name), array);
    }
    
    @Override
    public void bind(final String s, final Object o, final Attributes attributes) throws NamingException {
        this.bind(this.toName(s), o, attributes);
    }
    
    @Override
    public void bind(final Name name, final Object o, final Attributes attributes) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        this.refCtx.bind(this.overrideName(name), o, attributes);
    }
    
    @Override
    public void rebind(final String s, final Object o, final Attributes attributes) throws NamingException {
        this.rebind(this.toName(s), o, attributes);
    }
    
    @Override
    public void rebind(final Name name, final Object o, final Attributes attributes) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        this.refCtx.rebind(this.overrideName(name), o, attributes);
    }
    
    @Override
    public DirContext createSubcontext(final String s, final Attributes attributes) throws NamingException {
        return this.createSubcontext(this.toName(s), attributes);
    }
    
    @Override
    public DirContext createSubcontext(final Name name, final Attributes attributes) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        return this.refCtx.createSubcontext(this.overrideName(name), attributes);
    }
    
    @Override
    public DirContext getSchema(final String s) throws NamingException {
        return this.getSchema(this.toName(s));
    }
    
    @Override
    public DirContext getSchema(final Name name) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        return this.refCtx.getSchema(this.overrideName(name));
    }
    
    @Override
    public DirContext getSchemaClassDefinition(final String s) throws NamingException {
        return this.getSchemaClassDefinition(this.toName(s));
    }
    
    @Override
    public DirContext getSchemaClassDefinition(final Name name) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        return this.refCtx.getSchemaClassDefinition(this.overrideName(name));
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final Attributes attributes) throws NamingException {
        return this.search(this.toName(s), SearchFilter.format(attributes), new SearchControls());
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final Attributes attributes) throws NamingException {
        return this.search(name, SearchFilter.format(attributes), new SearchControls());
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final Attributes attributes, final String[] returningAttributes) throws NamingException {
        final SearchControls searchControls = new SearchControls();
        searchControls.setReturningAttributes(returningAttributes);
        return this.search(this.toName(s), SearchFilter.format(attributes), searchControls);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final Attributes attributes, final String[] returningAttributes) throws NamingException {
        final SearchControls searchControls = new SearchControls();
        searchControls.setReturningAttributes(returningAttributes);
        return this.search(name, SearchFilter.format(attributes), searchControls);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final String s2, final SearchControls searchControls) throws NamingException {
        return this.search(this.toName(s), s2, searchControls);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final String s, final SearchControls searchControls) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        try {
            final NamingEnumeration<SearchResult> search = this.refCtx.search(this.overrideName(name), this.overrideFilter(s), this.overrideAttributesAndScope(searchControls));
            this.refEx.setNameResolved(true);
            ((ReferralEnumeration)search).appendUnprocessedReferrals(this.refEx);
            return search;
        }
        catch (final LdapReferralException ex) {
            ex.appendUnprocessedReferrals(this.refEx);
            throw (NamingException)ex.fillInStackTrace();
        }
        catch (final NamingException namingException) {
            if (this.refEx != null && !this.refEx.hasMoreReferrals()) {
                this.refEx.setNamingException(namingException);
            }
            if (this.refEx != null && (this.refEx.hasMoreReferrals() || this.refEx.hasMoreReferralExceptions())) {
                throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
            }
            throw namingException;
        }
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final String s2, final Object[] array, final SearchControls searchControls) throws NamingException {
        return this.search(this.toName(s), s2, array, searchControls);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final String s, final Object[] array, final SearchControls searchControls) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        try {
            NamingEnumeration<SearchResult> namingEnumeration;
            if (this.urlFilter != null) {
                namingEnumeration = this.refCtx.search(this.overrideName(name), this.urlFilter, this.overrideAttributesAndScope(searchControls));
            }
            else {
                namingEnumeration = this.refCtx.search(this.overrideName(name), s, array, this.overrideAttributesAndScope(searchControls));
            }
            this.refEx.setNameResolved(true);
            ((ReferralEnumeration)namingEnumeration).appendUnprocessedReferrals(this.refEx);
            return namingEnumeration;
        }
        catch (final LdapReferralException ex) {
            ex.appendUnprocessedReferrals(this.refEx);
            throw (NamingException)ex.fillInStackTrace();
        }
        catch (final NamingException namingException) {
            if (this.refEx != null && !this.refEx.hasMoreReferrals()) {
                this.refEx.setNamingException(namingException);
            }
            if (this.refEx != null && (this.refEx.hasMoreReferrals() || this.refEx.hasMoreReferralExceptions())) {
                throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
            }
            throw namingException;
        }
    }
    
    @Override
    public String getNameInNamespace() throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        return (this.urlName != null && !this.urlName.isEmpty()) ? this.urlName.get(0) : "";
    }
    
    @Override
    public ExtendedResponse extendedOperation(final ExtendedRequest extendedRequest) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        if (!(this.refCtx instanceof LdapContext)) {
            throw new NotContextException("Referral context not an instance of LdapContext");
        }
        return ((LdapContext)this.refCtx).extendedOperation(extendedRequest);
    }
    
    @Override
    public LdapContext newInstance(final Control[] array) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        if (!(this.refCtx instanceof LdapContext)) {
            throw new NotContextException("Referral context not an instance of LdapContext");
        }
        return ((LdapContext)this.refCtx).newInstance(array);
    }
    
    @Override
    public void reconnect(final Control[] array) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        if (!(this.refCtx instanceof LdapContext)) {
            throw new NotContextException("Referral context not an instance of LdapContext");
        }
        ((LdapContext)this.refCtx).reconnect(array);
    }
    
    @Override
    public Control[] getConnectControls() throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        if (!(this.refCtx instanceof LdapContext)) {
            throw new NotContextException("Referral context not an instance of LdapContext");
        }
        return ((LdapContext)this.refCtx).getConnectControls();
    }
    
    @Override
    public void setRequestControls(final Control[] requestControls) throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        if (!(this.refCtx instanceof LdapContext)) {
            throw new NotContextException("Referral context not an instance of LdapContext");
        }
        ((LdapContext)this.refCtx).setRequestControls(requestControls);
    }
    
    @Override
    public Control[] getRequestControls() throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        if (!(this.refCtx instanceof LdapContext)) {
            throw new NotContextException("Referral context not an instance of LdapContext");
        }
        return ((LdapContext)this.refCtx).getRequestControls();
    }
    
    @Override
    public Control[] getResponseControls() throws NamingException {
        if (this.skipThisReferral) {
            throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace();
        }
        if (!(this.refCtx instanceof LdapContext)) {
            throw new NotContextException("Referral context not an instance of LdapContext");
        }
        return ((LdapContext)this.refCtx).getResponseControls();
    }
    
    private Name toName(final String s) throws InvalidNameException {
        return s.equals("") ? new CompositeName() : new CompositeName().add(s);
    }
    
    private Name overrideName(final Name name) throws InvalidNameException {
        return (this.urlName == null) ? name : this.urlName;
    }
    
    private SearchControls overrideAttributesAndScope(final SearchControls searchControls) {
        if (this.urlScope != null || this.urlAttrs != null) {
            final SearchControls searchControls2 = new SearchControls(searchControls.getSearchScope(), searchControls.getCountLimit(), searchControls.getTimeLimit(), searchControls.getReturningAttributes(), searchControls.getReturningObjFlag(), searchControls.getDerefLinkFlag());
            if (this.urlScope != null) {
                if (this.urlScope.equals("base")) {
                    searchControls2.setSearchScope(0);
                }
                else if (this.urlScope.equals("one")) {
                    searchControls2.setSearchScope(1);
                }
                else if (this.urlScope.equals("sub")) {
                    searchControls2.setSearchScope(2);
                }
            }
            if (this.urlAttrs != null) {
                final StringTokenizer stringTokenizer = new StringTokenizer(this.urlAttrs, ",");
                final int countTokens = stringTokenizer.countTokens();
                final String[] returningAttributes = new String[countTokens];
                for (int i = 0; i < countTokens; ++i) {
                    returningAttributes[i] = stringTokenizer.nextToken();
                }
                searchControls2.setReturningAttributes(returningAttributes);
            }
            return searchControls2;
        }
        return searchControls;
    }
    
    private String overrideFilter(final String s) {
        return (this.urlFilter == null) ? s : this.urlFilter;
    }
}
