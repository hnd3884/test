package com.sun.jndi.ldap;

import javax.naming.NameClassPair;
import javax.naming.directory.BasicAttributes;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.spi.DirectoryManager;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.naming.CompositeName;
import javax.naming.ldap.Control;
import java.util.Vector;
import javax.naming.directory.Attributes;
import javax.naming.NamingException;
import javax.naming.ldap.LdapName;
import java.security.AccessController;
import com.sun.jndi.toolkit.ctx.Continuation;
import java.security.AccessControlContext;
import javax.naming.Name;
import javax.naming.directory.SearchResult;

final class LdapSearchEnumeration extends AbstractLdapNamingEnumeration<SearchResult>
{
    private Name startName;
    private LdapCtx.SearchArgs searchArgs;
    private final AccessControlContext acc;
    
    LdapSearchEnumeration(final LdapCtx ldapCtx, final LdapResult ldapResult, final String s, final LdapCtx.SearchArgs searchArgs, final Continuation continuation) throws NamingException {
        super(ldapCtx, ldapResult, searchArgs.name, continuation);
        this.searchArgs = null;
        this.acc = AccessController.getContext();
        this.startName = new LdapName(s);
        this.searchArgs = searchArgs;
    }
    
    @Override
    protected SearchResult createItem(final String nameInNamespace, final Attributes attributes, final Vector<Control> vector) throws NamingException {
        Object o = null;
        boolean b = true;
        String string;
        String s;
        try {
            final LdapName ldapName = new LdapName(nameInNamespace);
            if (this.startName != null && ldapName.startsWith(this.startName)) {
                string = ldapName.getSuffix(this.startName.size()).toString();
                s = ldapName.getSuffix(this.homeCtx.currentParsedDN.size()).toString();
            }
            else {
                b = false;
                string = (s = LdapURL.toUrlString(this.homeCtx.hostname, this.homeCtx.port_number, nameInNamespace, this.homeCtx.hasLdapsScheme));
            }
        }
        catch (final NamingException ex) {
            b = false;
            string = (s = LdapURL.toUrlString(this.homeCtx.hostname, this.homeCtx.port_number, nameInNamespace, this.homeCtx.hasLdapsScheme));
        }
        final CompositeName compositeName = new CompositeName();
        if (!string.equals("")) {
            compositeName.add(string);
        }
        final CompositeName compositeName2 = new CompositeName();
        if (!s.equals("")) {
            compositeName2.add(s);
        }
        this.homeCtx.setParents(attributes, compositeName2);
        if (this.searchArgs.cons.getReturningObjFlag()) {
            if (attributes.get(Obj.JAVA_ATTRIBUTES[2]) != null) {
                try {
                    o = AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                        @Override
                        public Object run() throws NamingException {
                            return Obj.decodeObject(attributes);
                        }
                    }, this.acc);
                }
                catch (final PrivilegedActionException ex2) {
                    throw (NamingException)ex2.getException();
                }
            }
            if (o == null) {
                o = new LdapCtx(this.homeCtx, nameInNamespace);
            }
            try {
                o = DirectoryManager.getObjectInstance(o, compositeName2, b ? this.homeCtx : null, this.homeCtx.envprops, attributes);
            }
            catch (final NamingException ex3) {
                throw ex3;
            }
            catch (final Exception rootCause) {
                final NamingException ex4 = new NamingException("problem generating object using object factory");
                ex4.setRootCause(rootCause);
                throw ex4;
            }
            final String[] reqAttrs;
            if ((reqAttrs = this.searchArgs.reqAttrs) != null) {
                final BasicAttributes basicAttributes = new BasicAttributes(true);
                for (int i = 0; i < reqAttrs.length; ++i) {
                    basicAttributes.put(reqAttrs[i], null);
                }
                for (int j = 0; j < Obj.JAVA_ATTRIBUTES.length; ++j) {
                    if (basicAttributes.get(Obj.JAVA_ATTRIBUTES[j]) == null) {
                        attributes.remove(Obj.JAVA_ATTRIBUTES[j]);
                    }
                }
            }
        }
        SearchResult searchResult;
        if (vector != null) {
            searchResult = new SearchResultWithControls(b ? compositeName.toString() : string, o, attributes, b, this.homeCtx.convertControls(vector));
        }
        else {
            searchResult = new SearchResult(b ? compositeName.toString() : string, o, attributes, b);
        }
        searchResult.setNameInNamespace(nameInNamespace);
        return searchResult;
    }
    
    @Override
    public void appendUnprocessedReferrals(final LdapReferralException ex) {
        this.startName = null;
        super.appendUnprocessedReferrals(ex);
    }
    
    @Override
    protected AbstractLdapNamingEnumeration<? extends NameClassPair> getReferredResults(final LdapReferralContext ldapReferralContext) throws NamingException {
        return (AbstractLdapNamingEnumeration)ldapReferralContext.search(this.searchArgs.name, this.searchArgs.filter, this.searchArgs.cons);
    }
    
    @Override
    protected void update(final AbstractLdapNamingEnumeration<? extends NameClassPair> abstractLdapNamingEnumeration) {
        super.update(abstractLdapNamingEnumeration);
        this.startName = ((LdapSearchEnumeration)abstractLdapNamingEnumeration).startName;
    }
    
    void setStartName(final Name startName) {
        this.startName = startName;
    }
}
