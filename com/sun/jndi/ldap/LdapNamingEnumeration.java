package com.sun.jndi.ldap;

import javax.naming.directory.DirContext;
import javax.naming.directory.Attribute;
import javax.naming.CompositeName;
import javax.naming.ldap.Control;
import java.util.Vector;
import javax.naming.directory.Attributes;
import javax.naming.NamingException;
import com.sun.jndi.toolkit.ctx.Continuation;
import javax.naming.Name;
import javax.naming.NameClassPair;

final class LdapNamingEnumeration extends AbstractLdapNamingEnumeration<NameClassPair>
{
    private static final String defaultClassName;
    
    LdapNamingEnumeration(final LdapCtx ldapCtx, final LdapResult ldapResult, final Name name, final Continuation continuation) throws NamingException {
        super(ldapCtx, ldapResult, name, continuation);
    }
    
    @Override
    protected NameClassPair createItem(final String nameInNamespace, final Attributes attributes, final Vector<Control> vector) throws NamingException {
        final Attribute value;
        String defaultClassName;
        if ((value = attributes.get(Obj.JAVA_ATTRIBUTES[2])) != null) {
            defaultClassName = (String)value.get();
        }
        else {
            defaultClassName = LdapNamingEnumeration.defaultClassName;
        }
        final CompositeName compositeName = new CompositeName();
        compositeName.add(this.getAtom(nameInNamespace));
        NameClassPair nameClassPair;
        if (vector != null) {
            nameClassPair = new NameClassPairWithControls(compositeName.toString(), defaultClassName, this.homeCtx.convertControls(vector));
        }
        else {
            nameClassPair = new NameClassPair(compositeName.toString(), defaultClassName);
        }
        nameClassPair.setNameInNamespace(nameInNamespace);
        return nameClassPair;
    }
    
    @Override
    protected AbstractLdapNamingEnumeration<? extends NameClassPair> getReferredResults(final LdapReferralContext ldapReferralContext) throws NamingException {
        return (AbstractLdapNamingEnumeration)ldapReferralContext.list(this.listArg);
    }
    
    static {
        defaultClassName = DirContext.class.getName();
    }
}
