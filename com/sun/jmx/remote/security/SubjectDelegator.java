package com.sun.jmx.remote.security;

import java.util.List;
import java.util.Collections;
import java.util.Arrays;
import java.util.Iterator;
import java.security.AccessController;
import java.security.Permission;
import java.util.Collection;
import java.security.PrivilegedAction;
import javax.management.remote.SubjectDelegationPermission;
import java.security.Principal;
import java.util.ArrayList;
import javax.security.auth.Subject;
import java.security.AccessControlContext;

public class SubjectDelegator
{
    public AccessControlContext delegatedContext(final AccessControlContext accessControlContext, final Subject subject, final boolean b) throws SecurityException {
        if (System.getSecurityManager() != null && accessControlContext == null) {
            throw new SecurityException("Illegal AccessControlContext: null");
        }
        final Collection<Principal> subjectPrincipals = getSubjectPrincipals(subject);
        final ArrayList list = new ArrayList(subjectPrincipals.size());
        for (final Principal principal : subjectPrincipals) {
            list.add((Object)new SubjectDelegationPermission(principal.getClass().getName() + "." + principal.getName()));
        }
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                final Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    AccessController.checkPermission((Permission)iterator.next());
                }
                return null;
            }
        }, accessControlContext);
        return this.getDelegatedAcc(subject, b);
    }
    
    private AccessControlContext getDelegatedAcc(final Subject subject, final boolean b) {
        if (b) {
            return JMXSubjectDomainCombiner.getDomainCombinerContext(subject);
        }
        return JMXSubjectDomainCombiner.getContext(subject);
    }
    
    public static synchronized boolean checkRemoveCallerContext(final Subject subject) {
        try {
            for (final Principal principal : getSubjectPrincipals(subject)) {
                AccessController.checkPermission(new SubjectDelegationPermission(principal.getClass().getName() + "." + principal.getName()));
            }
        }
        catch (final SecurityException ex) {
            return false;
        }
        return true;
    }
    
    private static Collection<Principal> getSubjectPrincipals(final Subject subject) {
        if (subject.isReadOnly()) {
            return subject.getPrincipals();
        }
        return (Collection<Principal>)Collections.unmodifiableList((List<?>)Arrays.asList((Object[])subject.getPrincipals().toArray((T[])new Principal[0])));
    }
}
