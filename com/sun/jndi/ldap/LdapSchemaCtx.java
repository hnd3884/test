package com.sun.jndi.ldap;

import javax.naming.directory.ModificationItem;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.CompositeName;
import javax.naming.NameNotFoundException;
import javax.naming.directory.SchemaViolationException;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.Attributes;
import java.util.Hashtable;
import com.sun.jndi.toolkit.dir.HierMemDirCtx;

final class LdapSchemaCtx extends HierMemDirCtx
{
    private static final boolean debug = false;
    private static final int LEAF = 0;
    private static final int SCHEMA_ROOT = 1;
    static final int OBJECTCLASS_ROOT = 2;
    static final int ATTRIBUTE_ROOT = 3;
    static final int SYNTAX_ROOT = 4;
    static final int MATCHRULE_ROOT = 5;
    static final int OBJECTCLASS = 6;
    static final int ATTRIBUTE = 7;
    static final int SYNTAX = 8;
    static final int MATCHRULE = 9;
    private SchemaInfo info;
    private boolean setupMode;
    private int objectType;
    
    static DirContext createSchemaTree(final Hashtable<String, Object> hashtable, final String s, final LdapCtx ldapCtx, final Attributes attributes, final boolean b) throws NamingException {
        try {
            final LdapSchemaCtx ldapSchemaCtx = new LdapSchemaCtx(1, hashtable, new SchemaInfo(s, ldapCtx, new LdapSchemaParser(b)));
            LdapSchemaParser.LDAP2JNDISchema(attributes, ldapSchemaCtx);
            return ldapSchemaCtx;
        }
        catch (final NamingException ex) {
            ldapCtx.close();
            throw ex;
        }
    }
    
    private LdapSchemaCtx(final int objectType, final Hashtable<String, Object> hashtable, final SchemaInfo info) {
        super(hashtable, true);
        this.info = null;
        this.setupMode = true;
        this.objectType = objectType;
        this.info = info;
    }
    
    @Override
    public void close() throws NamingException {
        this.info.close();
    }
    
    @Override
    public final void bind(final Name name, final Object o, final Attributes attributes) throws NamingException {
        if (!this.setupMode) {
            if (o != null) {
                throw new IllegalArgumentException("obj must be null");
            }
            this.addServerSchema(attributes);
        }
        final LdapSchemaCtx ldapSchemaCtx = (LdapSchemaCtx)super.doCreateSubcontext(name, attributes);
    }
    
    @Override
    protected final void doBind(final Name name, final Object o, final Attributes attributes, final boolean b) throws NamingException {
        if (!this.setupMode) {
            throw new SchemaViolationException("Cannot bind arbitrary object; use createSubcontext()");
        }
        super.doBind(name, o, attributes, false);
    }
    
    @Override
    public final void rebind(final Name name, final Object o, final Attributes attributes) throws NamingException {
        try {
            this.doLookup(name, false);
            throw new SchemaViolationException("Cannot replace existing schema object");
        }
        catch (final NameNotFoundException ex) {
            this.bind(name, o, attributes);
        }
    }
    
    @Override
    protected final void doRebind(final Name name, final Object o, final Attributes attributes, final boolean b) throws NamingException {
        if (!this.setupMode) {
            throw new SchemaViolationException("Cannot bind arbitrary object; use createSubcontext()");
        }
        super.doRebind(name, o, attributes, false);
    }
    
    @Override
    protected final void doUnbind(final Name name) throws NamingException {
        if (!this.setupMode) {
            try {
                this.deleteServerSchema(((LdapSchemaCtx)this.doLookup(name, false)).attrs);
            }
            catch (final NameNotFoundException ex) {
                return;
            }
        }
        super.doUnbind(name);
    }
    
    @Override
    protected final void doRename(final Name name, final Name name2) throws NamingException {
        if (!this.setupMode) {
            throw new SchemaViolationException("Cannot rename a schema object");
        }
        super.doRename(name, name2);
    }
    
    @Override
    protected final void doDestroySubcontext(final Name name) throws NamingException {
        if (!this.setupMode) {
            try {
                this.deleteServerSchema(((LdapSchemaCtx)this.doLookup(name, false)).attrs);
            }
            catch (final NameNotFoundException ex) {
                return;
            }
        }
        super.doDestroySubcontext(name);
    }
    
    final LdapSchemaCtx setup(final int objectType, final String s, final Attributes attributes) throws NamingException {
        try {
            this.setupMode = true;
            final LdapSchemaCtx ldapSchemaCtx = (LdapSchemaCtx)super.doCreateSubcontext(new CompositeName(s), attributes);
            ldapSchemaCtx.objectType = objectType;
            ldapSchemaCtx.setupMode = false;
            return ldapSchemaCtx;
        }
        finally {
            this.setupMode = false;
        }
    }
    
    @Override
    protected final DirContext doCreateSubcontext(final Name name, final Attributes attributes) throws NamingException {
        if (attributes == null || attributes.size() == 0) {
            throw new SchemaViolationException("Must supply attributes describing schema");
        }
        if (!this.setupMode) {
            this.addServerSchema(attributes);
        }
        return super.doCreateSubcontext(name, attributes);
    }
    
    private static final Attributes deepClone(final Attributes attributes) throws NamingException {
        final BasicAttributes basicAttributes = new BasicAttributes(true);
        final NamingEnumeration<? extends Attribute> all = attributes.getAll();
        while (all.hasMore()) {
            basicAttributes.put((Attribute)((Attribute)all.next()).clone());
        }
        return basicAttributes;
    }
    
    @Override
    protected final void doModifyAttributes(final ModificationItem[] array) throws NamingException {
        if (this.setupMode) {
            super.doModifyAttributes(array);
        }
        else {
            final Attributes deepClone = deepClone(this.attrs);
            HierMemDirCtx.applyMods(array, deepClone);
            this.modifyServerSchema(this.attrs, deepClone);
            this.attrs = deepClone;
        }
    }
    
    @Override
    protected final HierMemDirCtx createNewCtx() {
        return new LdapSchemaCtx(0, this.myEnv, this.info);
    }
    
    private final void addServerSchema(final Attributes attributes) throws NamingException {
        Attribute attribute = null;
        switch (this.objectType) {
            case 2: {
                attribute = this.info.parser.stringifyObjDesc(attributes);
                break;
            }
            case 3: {
                attribute = this.info.parser.stringifyAttrDesc(attributes);
                break;
            }
            case 4: {
                attribute = this.info.parser.stringifySyntaxDesc(attributes);
                break;
            }
            case 5: {
                attribute = this.info.parser.stringifyMatchRuleDesc(attributes);
                break;
            }
            case 1: {
                throw new SchemaViolationException("Cannot create new entry under schema root");
            }
            default: {
                throw new SchemaViolationException("Cannot create child of schema object");
            }
        }
        final BasicAttributes basicAttributes = new BasicAttributes(true);
        basicAttributes.put(attribute);
        this.info.modifyAttributes(this.myEnv, 1, basicAttributes);
    }
    
    private final void deleteServerSchema(final Attributes attributes) throws NamingException {
        Attribute attribute = null;
        switch (this.objectType) {
            case 2: {
                attribute = this.info.parser.stringifyObjDesc(attributes);
                break;
            }
            case 3: {
                attribute = this.info.parser.stringifyAttrDesc(attributes);
                break;
            }
            case 4: {
                attribute = this.info.parser.stringifySyntaxDesc(attributes);
                break;
            }
            case 5: {
                attribute = this.info.parser.stringifyMatchRuleDesc(attributes);
                break;
            }
            case 1: {
                throw new SchemaViolationException("Cannot delete schema root");
            }
            default: {
                throw new SchemaViolationException("Cannot delete child of schema object");
            }
        }
        this.info.modifyAttributes(this.myEnv, new ModificationItem[] { new ModificationItem(3, attribute) });
    }
    
    private final void modifyServerSchema(final Attributes attributes, final Attributes attributes2) throws NamingException {
        Attribute attribute = null;
        Attribute attribute2 = null;
        switch (this.objectType) {
            case 6: {
                attribute = this.info.parser.stringifyObjDesc(attributes);
                attribute2 = this.info.parser.stringifyObjDesc(attributes2);
                break;
            }
            case 7: {
                attribute = this.info.parser.stringifyAttrDesc(attributes);
                attribute2 = this.info.parser.stringifyAttrDesc(attributes2);
                break;
            }
            case 8: {
                attribute = this.info.parser.stringifySyntaxDesc(attributes);
                attribute2 = this.info.parser.stringifySyntaxDesc(attributes2);
                break;
            }
            case 9: {
                attribute = this.info.parser.stringifyMatchRuleDesc(attributes);
                attribute2 = this.info.parser.stringifyMatchRuleDesc(attributes2);
                break;
            }
            default: {
                throw new SchemaViolationException("Cannot modify schema root");
            }
        }
        this.info.modifyAttributes(this.myEnv, new ModificationItem[] { new ModificationItem(3, attribute), new ModificationItem(1, attribute2) });
    }
    
    private static final class SchemaInfo
    {
        private LdapCtx schemaEntry;
        private String schemaEntryName;
        LdapSchemaParser parser;
        private String host;
        private int port;
        private boolean hasLdapsScheme;
        
        SchemaInfo(final String schemaEntryName, final LdapCtx schemaEntry, final LdapSchemaParser parser) {
            this.schemaEntryName = schemaEntryName;
            this.schemaEntry = schemaEntry;
            this.parser = parser;
            this.port = schemaEntry.port_number;
            this.host = schemaEntry.hostname;
            this.hasLdapsScheme = schemaEntry.hasLdapsScheme;
        }
        
        synchronized void close() throws NamingException {
            if (this.schemaEntry != null) {
                this.schemaEntry.close();
                this.schemaEntry = null;
            }
        }
        
        private LdapCtx reopenEntry(final Hashtable<?, ?> hashtable) throws NamingException {
            return new LdapCtx(this.schemaEntryName, this.host, this.port, hashtable, this.hasLdapsScheme);
        }
        
        synchronized void modifyAttributes(final Hashtable<?, ?> hashtable, final ModificationItem[] array) throws NamingException {
            if (this.schemaEntry == null) {
                this.schemaEntry = this.reopenEntry(hashtable);
            }
            this.schemaEntry.modifyAttributes("", array);
        }
        
        synchronized void modifyAttributes(final Hashtable<?, ?> hashtable, final int n, final Attributes attributes) throws NamingException {
            if (this.schemaEntry == null) {
                this.schemaEntry = this.reopenEntry(hashtable);
            }
            this.schemaEntry.modifyAttributes("", n, attributes);
        }
    }
}
