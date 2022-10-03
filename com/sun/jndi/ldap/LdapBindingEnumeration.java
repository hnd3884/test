package com.sun.jndi.ldap;

import javax.naming.NameClassPair;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.spi.DirectoryManager;
import javax.naming.CompositeName;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.naming.ldap.Control;
import java.util.Vector;
import javax.naming.directory.Attributes;
import javax.naming.NamingException;
import java.security.AccessController;
import com.sun.jndi.toolkit.ctx.Continuation;
import javax.naming.Name;
import java.security.AccessControlContext;
import javax.naming.Binding;

final class LdapBindingEnumeration extends AbstractLdapNamingEnumeration<Binding>
{
    private final AccessControlContext acc;
    
    LdapBindingEnumeration(final LdapCtx ldapCtx, final LdapResult ldapResult, final Name name, final Continuation continuation) throws NamingException {
        super(ldapCtx, ldapResult, name, continuation);
        this.acc = AccessController.getContext();
    }
    
    @Override
    protected Binding createItem(final String nameInNamespace, final Attributes attributes, final Vector<Control> vector) throws NamingException {
        Object doPrivileged = null;
        final String atom = this.getAtom(nameInNamespace);
        if (attributes.get(Obj.JAVA_ATTRIBUTES[2]) != null) {
            try {
                doPrivileged = AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                    @Override
                    public Object run() throws NamingException {
                        return Obj.decodeObject(attributes);
                    }
                }, this.acc);
            }
            catch (final PrivilegedActionException ex) {
                throw (NamingException)ex.getException();
            }
        }
        if (doPrivileged == null) {
            doPrivileged = new LdapCtx(this.homeCtx, nameInNamespace);
        }
        final CompositeName compositeName = new CompositeName();
        compositeName.add(atom);
        Object objectInstance;
        try {
            objectInstance = DirectoryManager.getObjectInstance(doPrivileged, compositeName, this.homeCtx, this.homeCtx.envprops, attributes);
        }
        catch (final NamingException ex2) {
            throw ex2;
        }
        catch (final Exception rootCause) {
            final NamingException ex3 = new NamingException("problem generating object using object factory");
            ex3.setRootCause(rootCause);
            throw ex3;
        }
        Binding binding;
        if (vector != null) {
            binding = new BindingWithControls(compositeName.toString(), objectInstance, this.homeCtx.convertControls(vector));
        }
        else {
            binding = new Binding(compositeName.toString(), objectInstance);
        }
        binding.setNameInNamespace(nameInNamespace);
        return binding;
    }
    
    @Override
    protected AbstractLdapNamingEnumeration<? extends NameClassPair> getReferredResults(final LdapReferralContext ldapReferralContext) throws NamingException {
        return (AbstractLdapNamingEnumeration)ldapReferralContext.listBindings(this.listArg);
    }
}
