package com.sun.jndi.ldap;

import javax.naming.directory.Attribute;
import javax.naming.NameNotFoundException;
import java.util.Iterator;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.naming.CompositeName;
import java.util.Vector;
import java.util.Hashtable;
import javax.naming.Name;
import javax.naming.directory.DirContext;
import javax.naming.directory.BasicAttribute;

final class LdapAttribute extends BasicAttribute
{
    static final long serialVersionUID = -4288716561020779584L;
    private transient DirContext baseCtx;
    private Name rdn;
    private String baseCtxURL;
    private Hashtable<String, ? super String> baseCtxEnv;
    
    @Override
    public Object clone() {
        final LdapAttribute ldapAttribute = new LdapAttribute(this.attrID, this.baseCtx, this.rdn);
        ldapAttribute.values = (Vector)this.values.clone();
        return ldapAttribute;
    }
    
    @Override
    public boolean add(final Object o) {
        this.values.addElement(o);
        return true;
    }
    
    LdapAttribute(final String s) {
        super(s);
        this.baseCtx = null;
        this.rdn = new CompositeName();
    }
    
    private LdapAttribute(final String s, final DirContext baseCtx, final Name rdn) {
        super(s);
        this.baseCtx = null;
        this.rdn = new CompositeName();
        this.baseCtx = baseCtx;
        this.rdn = rdn;
    }
    
    void setParent(final DirContext baseCtx, final Name rdn) {
        this.baseCtx = baseCtx;
        this.rdn = rdn;
    }
    
    private DirContext getBaseCtx() throws NamingException {
        if (this.baseCtx == null) {
            if (this.baseCtxEnv == null) {
                this.baseCtxEnv = new Hashtable<String, Object>(3);
            }
            this.baseCtxEnv.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
            this.baseCtxEnv.put("java.naming.provider.url", this.baseCtxURL);
            this.baseCtx = new InitialDirContext(this.baseCtxEnv);
        }
        return this.baseCtx;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        this.setBaseCtxInfo();
        objectOutputStream.defaultWriteObject();
    }
    
    private void setBaseCtxInfo() {
        Hashtable<String, Object> envprops = null;
        Hashtable<String, Object> hashtable = null;
        if (this.baseCtx != null) {
            envprops = ((LdapCtx)this.baseCtx).envprops;
            this.baseCtxURL = ((LdapCtx)this.baseCtx).getURL();
        }
        if (envprops != null && envprops.size() > 0) {
            for (final String s : envprops.keySet()) {
                if (s.indexOf("security") != -1) {
                    if (hashtable == null) {
                        hashtable = (Hashtable)envprops.clone();
                    }
                    hashtable.remove(s);
                }
            }
        }
        this.baseCtxEnv = ((hashtable == null) ? envprops : hashtable);
    }
    
    @Override
    public DirContext getAttributeSyntaxDefinition() throws NamingException {
        final DirContext schema = this.getBaseCtx().getSchema(this.rdn);
        final Attribute value = ((DirContext)schema.lookup("AttributeDefinition/" + this.getID())).getAttributes("").get("SYNTAX");
        if (value == null || value.size() == 0) {
            throw new NameNotFoundException(this.getID() + "does not have a syntax associated with it");
        }
        return (DirContext)schema.lookup("SyntaxDefinition/" + (String)value.get());
    }
    
    @Override
    public DirContext getAttributeDefinition() throws NamingException {
        return (DirContext)this.getBaseCtx().getSchema(this.rdn).lookup("AttributeDefinition/" + this.getID());
    }
}
