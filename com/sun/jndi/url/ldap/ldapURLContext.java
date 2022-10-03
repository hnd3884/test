package com.sun.jndi.url.ldap;

import java.util.StringTokenizer;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.Attributes;
import javax.naming.NameParser;
import javax.naming.Context;
import javax.naming.Binding;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.InvalidNameException;
import javax.naming.CompositeName;
import com.sun.jndi.ldap.LdapURL;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ResolveResult;
import java.util.Hashtable;
import com.sun.jndi.toolkit.url.GenericURLDirContext;

public final class ldapURLContext extends GenericURLDirContext
{
    ldapURLContext(final Hashtable<?, ?> hashtable) {
        super(hashtable);
    }
    
    @Override
    protected ResolveResult getRootURLContext(final String s, final Hashtable<?, ?> hashtable) throws NamingException {
        return ldapURLContextFactory.getUsingURLIgnoreRootDN(s, hashtable);
    }
    
    @Override
    protected Name getURLSuffix(final String s, final String s2) throws NamingException {
        final LdapURL ldapURL = new LdapURL(s2);
        final String s3 = (ldapURL.getDN() != null) ? ldapURL.getDN() : "";
        final CompositeName compositeName = new CompositeName();
        if (!"".equals(s3)) {
            compositeName.add(s3);
        }
        return compositeName;
    }
    
    @Override
    public Object lookup(final String s) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            throw new InvalidNameException(s);
        }
        return super.lookup(s);
    }
    
    @Override
    public Object lookup(final Name name) throws NamingException {
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        return super.lookup(name);
    }
    
    @Override
    public void bind(final String s, final Object o) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            throw new InvalidNameException(s);
        }
        super.bind(s, o);
    }
    
    @Override
    public void bind(final Name name, final Object o) throws NamingException {
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        super.bind(name, o);
    }
    
    @Override
    public void rebind(final String s, final Object o) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            throw new InvalidNameException(s);
        }
        super.rebind(s, o);
    }
    
    @Override
    public void rebind(final Name name, final Object o) throws NamingException {
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        super.rebind(name, o);
    }
    
    @Override
    public void unbind(final String s) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            throw new InvalidNameException(s);
        }
        super.unbind(s);
    }
    
    @Override
    public void unbind(final Name name) throws NamingException {
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        super.unbind(name);
    }
    
    @Override
    public void rename(final String s, final String s2) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            throw new InvalidNameException(s);
        }
        if (LdapURL.hasQueryComponents(s2)) {
            throw new InvalidNameException(s2);
        }
        super.rename(s, s2);
    }
    
    @Override
    public void rename(final Name name, final Name name2) throws NamingException {
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        if (LdapURL.hasQueryComponents(name2.get(0))) {
            throw new InvalidNameException(name2.toString());
        }
        super.rename(name, name2);
    }
    
    @Override
    public NamingEnumeration<NameClassPair> list(final String s) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            throw new InvalidNameException(s);
        }
        return super.list(s);
    }
    
    @Override
    public NamingEnumeration<NameClassPair> list(final Name name) throws NamingException {
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        return super.list(name);
    }
    
    @Override
    public NamingEnumeration<Binding> listBindings(final String s) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            throw new InvalidNameException(s);
        }
        return super.listBindings(s);
    }
    
    @Override
    public NamingEnumeration<Binding> listBindings(final Name name) throws NamingException {
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        return super.listBindings(name);
    }
    
    @Override
    public void destroySubcontext(final String s) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            throw new InvalidNameException(s);
        }
        super.destroySubcontext(s);
    }
    
    @Override
    public void destroySubcontext(final Name name) throws NamingException {
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        super.destroySubcontext(name);
    }
    
    @Override
    public Context createSubcontext(final String s) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            throw new InvalidNameException(s);
        }
        return super.createSubcontext(s);
    }
    
    @Override
    public Context createSubcontext(final Name name) throws NamingException {
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        return super.createSubcontext(name);
    }
    
    @Override
    public Object lookupLink(final String s) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            throw new InvalidNameException(s);
        }
        return super.lookupLink(s);
    }
    
    @Override
    public Object lookupLink(final Name name) throws NamingException {
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        return super.lookupLink(name);
    }
    
    @Override
    public NameParser getNameParser(final String s) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            throw new InvalidNameException(s);
        }
        return super.getNameParser(s);
    }
    
    @Override
    public NameParser getNameParser(final Name name) throws NamingException {
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        return super.getNameParser(name);
    }
    
    @Override
    public String composeName(final String s, final String s2) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            throw new InvalidNameException(s);
        }
        if (LdapURL.hasQueryComponents(s2)) {
            throw new InvalidNameException(s2);
        }
        return super.composeName(s, s2);
    }
    
    @Override
    public Name composeName(final Name name, final Name name2) throws NamingException {
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        if (LdapURL.hasQueryComponents(name2.get(0))) {
            throw new InvalidNameException(name2.toString());
        }
        return super.composeName(name, name2);
    }
    
    @Override
    public Attributes getAttributes(final String s) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            throw new InvalidNameException(s);
        }
        return super.getAttributes(s);
    }
    
    @Override
    public Attributes getAttributes(final Name name) throws NamingException {
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        return super.getAttributes(name);
    }
    
    @Override
    public Attributes getAttributes(final String s, final String[] array) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            throw new InvalidNameException(s);
        }
        return super.getAttributes(s, array);
    }
    
    @Override
    public Attributes getAttributes(final Name name, final String[] array) throws NamingException {
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        return super.getAttributes(name, array);
    }
    
    @Override
    public void modifyAttributes(final String s, final int n, final Attributes attributes) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            throw new InvalidNameException(s);
        }
        super.modifyAttributes(s, n, attributes);
    }
    
    @Override
    public void modifyAttributes(final Name name, final int n, final Attributes attributes) throws NamingException {
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        super.modifyAttributes(name, n, attributes);
    }
    
    @Override
    public void modifyAttributes(final String s, final ModificationItem[] array) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            throw new InvalidNameException(s);
        }
        super.modifyAttributes(s, array);
    }
    
    @Override
    public void modifyAttributes(final Name name, final ModificationItem[] array) throws NamingException {
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        super.modifyAttributes(name, array);
    }
    
    @Override
    public void bind(final String s, final Object o, final Attributes attributes) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            throw new InvalidNameException(s);
        }
        super.bind(s, o, attributes);
    }
    
    @Override
    public void bind(final Name name, final Object o, final Attributes attributes) throws NamingException {
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        super.bind(name, o, attributes);
    }
    
    @Override
    public void rebind(final String s, final Object o, final Attributes attributes) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            throw new InvalidNameException(s);
        }
        super.rebind(s, o, attributes);
    }
    
    @Override
    public void rebind(final Name name, final Object o, final Attributes attributes) throws NamingException {
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        super.rebind(name, o, attributes);
    }
    
    @Override
    public DirContext createSubcontext(final String s, final Attributes attributes) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            throw new InvalidNameException(s);
        }
        return super.createSubcontext(s, attributes);
    }
    
    @Override
    public DirContext createSubcontext(final Name name, final Attributes attributes) throws NamingException {
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        return super.createSubcontext(name, attributes);
    }
    
    @Override
    public DirContext getSchema(final String s) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            throw new InvalidNameException(s);
        }
        return super.getSchema(s);
    }
    
    @Override
    public DirContext getSchema(final Name name) throws NamingException {
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        return super.getSchema(name);
    }
    
    @Override
    public DirContext getSchemaClassDefinition(final String s) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            throw new InvalidNameException(s);
        }
        return super.getSchemaClassDefinition(s);
    }
    
    @Override
    public DirContext getSchemaClassDefinition(final Name name) throws NamingException {
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        return super.getSchemaClassDefinition(name);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final Attributes attributes) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            return this.searchUsingURL(s);
        }
        return super.search(s, attributes);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final Attributes attributes) throws NamingException {
        if (name.size() == 1) {
            return this.search(name.get(0), attributes);
        }
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        return super.search(name, attributes);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final Attributes attributes, final String[] array) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            return this.searchUsingURL(s);
        }
        return super.search(s, attributes, array);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final Attributes attributes, final String[] array) throws NamingException {
        if (name.size() == 1) {
            return this.search(name.get(0), attributes, array);
        }
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        return super.search(name, attributes, array);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final String s2, final SearchControls searchControls) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            return this.searchUsingURL(s);
        }
        return super.search(s, s2, searchControls);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final String s, final SearchControls searchControls) throws NamingException {
        if (name.size() == 1) {
            return this.search(name.get(0), s, searchControls);
        }
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        return super.search(name, s, searchControls);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final String s2, final Object[] array, final SearchControls searchControls) throws NamingException {
        if (LdapURL.hasQueryComponents(s)) {
            return this.searchUsingURL(s);
        }
        return super.search(s, s2, array, searchControls);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final String s, final Object[] array, final SearchControls searchControls) throws NamingException {
        if (name.size() == 1) {
            return this.search(name.get(0), s, array, searchControls);
        }
        if (LdapURL.hasQueryComponents(name.get(0))) {
            throw new InvalidNameException(name.toString());
        }
        return super.search(name, s, array, searchControls);
    }
    
    private NamingEnumeration<SearchResult> searchUsingURL(final String s) throws NamingException {
        final LdapURL ldapURL = new LdapURL(s);
        final ResolveResult rootURLContext = this.getRootURLContext(s, this.myEnv);
        final DirContext dirContext = (DirContext)rootURLContext.getResolvedObj();
        try {
            return dirContext.search(rootURLContext.getRemainingName(), setFilterUsingURL(ldapURL), setSearchControlsUsingURL(ldapURL));
        }
        finally {
            dirContext.close();
        }
    }
    
    private static String setFilterUsingURL(final LdapURL ldapURL) {
        String filter = ldapURL.getFilter();
        if (filter == null) {
            filter = "(objectClass=*)";
        }
        return filter;
    }
    
    private static SearchControls setSearchControlsUsingURL(final LdapURL ldapURL) {
        final SearchControls searchControls = new SearchControls();
        final String scope = ldapURL.getScope();
        final String attributes = ldapURL.getAttributes();
        if (scope == null) {
            searchControls.setSearchScope(0);
        }
        else if (scope.equals("sub")) {
            searchControls.setSearchScope(2);
        }
        else if (scope.equals("one")) {
            searchControls.setSearchScope(1);
        }
        else if (scope.equals("base")) {
            searchControls.setSearchScope(0);
        }
        if (attributes == null) {
            searchControls.setReturningAttributes(null);
        }
        else {
            final StringTokenizer stringTokenizer = new StringTokenizer(attributes, ",");
            final int countTokens = stringTokenizer.countTokens();
            final String[] returningAttributes = new String[countTokens];
            for (int i = 0; i < countTokens; ++i) {
                returningAttributes[i] = stringTokenizer.nextToken();
            }
            searchControls.setReturningAttributes(returningAttributes);
        }
        return searchControls;
    }
}
