package com.sun.jndi.toolkit.dir;

import java.util.NoSuchElementException;
import java.util.Enumeration;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.directory.AttributeModificationException;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.Attribute;
import javax.naming.CompositeName;
import javax.naming.Binding;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.directory.SchemaViolationException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.spi.DirStateFactory;
import javax.naming.InvalidNameException;
import javax.naming.Context;
import javax.naming.spi.DirectoryManager;
import javax.naming.NameNotFoundException;
import javax.naming.directory.BasicAttributes;
import javax.naming.OperationNotSupportedException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.Name;
import java.util.Hashtable;
import javax.naming.NameParser;
import javax.naming.directory.DirContext;

public class HierMemDirCtx implements DirContext
{
    private static final boolean debug = false;
    private static final NameParser defaultParser;
    protected Hashtable<String, Object> myEnv;
    protected Hashtable<Name, Object> bindings;
    protected Attributes attrs;
    protected boolean ignoreCase;
    protected NamingException readOnlyEx;
    protected NameParser myParser;
    private boolean alwaysUseFactory;
    
    @Override
    public void close() throws NamingException {
        this.myEnv = null;
        this.bindings = null;
        this.attrs = null;
    }
    
    @Override
    public String getNameInNamespace() throws NamingException {
        throw new OperationNotSupportedException("Cannot determine full name");
    }
    
    public HierMemDirCtx() {
        this(null, false, false);
    }
    
    public HierMemDirCtx(final boolean b) {
        this(null, b, false);
    }
    
    public HierMemDirCtx(final Hashtable<String, Object> hashtable, final boolean b) {
        this(hashtable, b, false);
    }
    
    protected HierMemDirCtx(final Hashtable<String, Object> myEnv, final boolean ignoreCase, final boolean alwaysUseFactory) {
        this.ignoreCase = false;
        this.readOnlyEx = null;
        this.myParser = HierMemDirCtx.defaultParser;
        this.myEnv = myEnv;
        this.ignoreCase = ignoreCase;
        this.init();
        this.alwaysUseFactory = alwaysUseFactory;
    }
    
    private void init() {
        this.attrs = new BasicAttributes(this.ignoreCase);
        this.bindings = new Hashtable<Name, Object>(11, 0.75f);
    }
    
    @Override
    public Object lookup(final String s) throws NamingException {
        return this.lookup(this.myParser.parse(s));
    }
    
    @Override
    public Object lookup(final Name name) throws NamingException {
        return this.doLookup(name, this.alwaysUseFactory);
    }
    
    public Object doLookup(Name canonizeName, final boolean b) throws NamingException {
        canonizeName = this.canonizeName(canonizeName);
        Object o = null;
        switch (canonizeName.size()) {
            case 0: {
                o = this;
                break;
            }
            case 1: {
                o = this.bindings.get(canonizeName);
                break;
            }
            default: {
                final HierMemDirCtx hierMemDirCtx = this.bindings.get(canonizeName.getPrefix(1));
                if (hierMemDirCtx == null) {
                    o = null;
                    break;
                }
                o = hierMemDirCtx.doLookup(canonizeName.getSuffix(1), false);
                break;
            }
        }
        if (o == null) {
            throw new NameNotFoundException(canonizeName.toString());
        }
        if (b) {
            try {
                return DirectoryManager.getObjectInstance(o, canonizeName, this, this.myEnv, (o instanceof HierMemDirCtx) ? ((HierMemDirCtx)o).attrs : null);
            }
            catch (final NamingException ex) {
                throw ex;
            }
            catch (final Exception rootCause) {
                final NamingException ex2 = new NamingException("Problem calling getObjectInstance");
                ex2.setRootCause(rootCause);
                throw ex2;
            }
        }
        return o;
    }
    
    @Override
    public void bind(final String s, final Object o) throws NamingException {
        this.bind(this.myParser.parse(s), o);
    }
    
    @Override
    public void bind(final Name name, final Object o) throws NamingException {
        this.doBind(name, o, null, this.alwaysUseFactory);
    }
    
    @Override
    public void bind(final String s, final Object o, final Attributes attributes) throws NamingException {
        this.bind(this.myParser.parse(s), o, attributes);
    }
    
    @Override
    public void bind(final Name name, final Object o, final Attributes attributes) throws NamingException {
        this.doBind(name, o, attributes, this.alwaysUseFactory);
    }
    
    protected void doBind(final Name name, Object object, Attributes attributes, final boolean b) throws NamingException {
        if (name.isEmpty()) {
            throw new InvalidNameException("Cannot bind empty name");
        }
        if (b) {
            final DirStateFactory.Result stateToBind = DirectoryManager.getStateToBind(object, name, this, this.myEnv, attributes);
            object = stateToBind.getObject();
            attributes = stateToBind.getAttributes();
        }
        ((HierMemDirCtx)this.doLookup(this.getInternalName(name), false)).doBindAux(this.getLeafName(name), object);
        if (attributes != null && attributes.size() > 0) {
            this.modifyAttributes(name, 1, attributes);
        }
    }
    
    protected void doBindAux(final Name name, final Object o) throws NamingException {
        if (this.readOnlyEx != null) {
            throw (NamingException)this.readOnlyEx.fillInStackTrace();
        }
        if (this.bindings.get(name) != null) {
            throw new NameAlreadyBoundException(name.toString());
        }
        if (o instanceof HierMemDirCtx) {
            this.bindings.put(name, o);
            return;
        }
        throw new SchemaViolationException("This context only supports binding objects of it's own kind");
    }
    
    @Override
    public void rebind(final String s, final Object o) throws NamingException {
        this.rebind(this.myParser.parse(s), o);
    }
    
    @Override
    public void rebind(final Name name, final Object o) throws NamingException {
        this.doRebind(name, o, null, this.alwaysUseFactory);
    }
    
    @Override
    public void rebind(final String s, final Object o, final Attributes attributes) throws NamingException {
        this.rebind(this.myParser.parse(s), o, attributes);
    }
    
    @Override
    public void rebind(final Name name, final Object o, final Attributes attributes) throws NamingException {
        this.doRebind(name, o, attributes, this.alwaysUseFactory);
    }
    
    protected void doRebind(final Name name, Object object, Attributes attributes, final boolean b) throws NamingException {
        if (name.isEmpty()) {
            throw new InvalidNameException("Cannot rebind empty name");
        }
        if (b) {
            final DirStateFactory.Result stateToBind = DirectoryManager.getStateToBind(object, name, this, this.myEnv, attributes);
            object = stateToBind.getObject();
            attributes = stateToBind.getAttributes();
        }
        ((HierMemDirCtx)this.doLookup(this.getInternalName(name), false)).doRebindAux(this.getLeafName(name), object);
        if (attributes != null && attributes.size() > 0) {
            this.modifyAttributes(name, 1, attributes);
        }
    }
    
    protected void doRebindAux(final Name name, final Object o) throws NamingException {
        if (this.readOnlyEx != null) {
            throw (NamingException)this.readOnlyEx.fillInStackTrace();
        }
        if (o instanceof HierMemDirCtx) {
            this.bindings.put(name, o);
            return;
        }
        throw new SchemaViolationException("This context only supports binding objects of it's own kind");
    }
    
    @Override
    public void unbind(final String s) throws NamingException {
        this.unbind(this.myParser.parse(s));
    }
    
    @Override
    public void unbind(final Name name) throws NamingException {
        if (name.isEmpty()) {
            throw new InvalidNameException("Cannot unbind empty name");
        }
        ((HierMemDirCtx)this.doLookup(this.getInternalName(name), false)).doUnbind(this.getLeafName(name));
    }
    
    protected void doUnbind(final Name name) throws NamingException {
        if (this.readOnlyEx != null) {
            throw (NamingException)this.readOnlyEx.fillInStackTrace();
        }
        this.bindings.remove(name);
    }
    
    @Override
    public void rename(final String s, final String s2) throws NamingException {
        this.rename(this.myParser.parse(s), this.myParser.parse(s2));
    }
    
    @Override
    public void rename(final Name name, final Name name2) throws NamingException {
        if (name2.isEmpty() || name.isEmpty()) {
            throw new InvalidNameException("Cannot rename empty name");
        }
        if (!this.getInternalName(name2).equals(this.getInternalName(name))) {
            throw new InvalidNameException("Cannot rename across contexts");
        }
        ((HierMemDirCtx)this.doLookup(this.getInternalName(name2), false)).doRename(this.getLeafName(name), this.getLeafName(name2));
    }
    
    protected void doRename(Name canonizeName, Name canonizeName2) throws NamingException {
        if (this.readOnlyEx != null) {
            throw (NamingException)this.readOnlyEx.fillInStackTrace();
        }
        canonizeName = this.canonizeName(canonizeName);
        canonizeName2 = this.canonizeName(canonizeName2);
        if (this.bindings.get(canonizeName2) != null) {
            throw new NameAlreadyBoundException(canonizeName2.toString());
        }
        final Object remove = this.bindings.remove(canonizeName);
        if (remove == null) {
            throw new NameNotFoundException(canonizeName.toString());
        }
        this.bindings.put(canonizeName2, remove);
    }
    
    @Override
    public NamingEnumeration<NameClassPair> list(final String s) throws NamingException {
        return this.list(this.myParser.parse(s));
    }
    
    @Override
    public NamingEnumeration<NameClassPair> list(final Name name) throws NamingException {
        return ((HierMemDirCtx)this.doLookup(name, false)).doList();
    }
    
    protected NamingEnumeration<NameClassPair> doList() throws NamingException {
        return new FlatNames(this.bindings.keys());
    }
    
    @Override
    public NamingEnumeration<Binding> listBindings(final String s) throws NamingException {
        return this.listBindings(this.myParser.parse(s));
    }
    
    @Override
    public NamingEnumeration<Binding> listBindings(final Name name) throws NamingException {
        return ((HierMemDirCtx)this.doLookup(name, false)).doListBindings(this.alwaysUseFactory);
    }
    
    protected NamingEnumeration<Binding> doListBindings(final boolean b) throws NamingException {
        return new FlatBindings(this.bindings, this.myEnv, b);
    }
    
    @Override
    public void destroySubcontext(final String s) throws NamingException {
        this.destroySubcontext(this.myParser.parse(s));
    }
    
    @Override
    public void destroySubcontext(final Name name) throws NamingException {
        ((HierMemDirCtx)this.doLookup(this.getInternalName(name), false)).doDestroySubcontext(this.getLeafName(name));
    }
    
    protected void doDestroySubcontext(Name canonizeName) throws NamingException {
        if (this.readOnlyEx != null) {
            throw (NamingException)this.readOnlyEx.fillInStackTrace();
        }
        canonizeName = this.canonizeName(canonizeName);
        this.bindings.remove(canonizeName);
    }
    
    @Override
    public Context createSubcontext(final String s) throws NamingException {
        return this.createSubcontext(this.myParser.parse(s));
    }
    
    @Override
    public Context createSubcontext(final Name name) throws NamingException {
        return this.createSubcontext(name, null);
    }
    
    @Override
    public DirContext createSubcontext(final String s, final Attributes attributes) throws NamingException {
        return this.createSubcontext(this.myParser.parse(s), attributes);
    }
    
    @Override
    public DirContext createSubcontext(final Name name, final Attributes attributes) throws NamingException {
        return ((HierMemDirCtx)this.doLookup(this.getInternalName(name), false)).doCreateSubcontext(this.getLeafName(name), attributes);
    }
    
    protected DirContext doCreateSubcontext(Name canonizeName, final Attributes attributes) throws NamingException {
        if (this.readOnlyEx != null) {
            throw (NamingException)this.readOnlyEx.fillInStackTrace();
        }
        canonizeName = this.canonizeName(canonizeName);
        if (this.bindings.get(canonizeName) != null) {
            throw new NameAlreadyBoundException(canonizeName.toString());
        }
        final HierMemDirCtx newCtx = this.createNewCtx();
        this.bindings.put(canonizeName, newCtx);
        if (attributes != null) {
            newCtx.modifyAttributes("", 1, attributes);
        }
        return newCtx;
    }
    
    @Override
    public Object lookupLink(final String s) throws NamingException {
        return this.lookupLink(this.myParser.parse(s));
    }
    
    @Override
    public Object lookupLink(final Name name) throws NamingException {
        return this.lookup(name);
    }
    
    @Override
    public NameParser getNameParser(final String s) throws NamingException {
        return this.myParser;
    }
    
    @Override
    public NameParser getNameParser(final Name name) throws NamingException {
        return this.myParser;
    }
    
    @Override
    public String composeName(final String s, final String s2) throws NamingException {
        return this.composeName(new CompositeName(s), new CompositeName(s2)).toString();
    }
    
    @Override
    public Name composeName(Name canonizeName, Name canonizeName2) throws NamingException {
        canonizeName = this.canonizeName(canonizeName);
        canonizeName2 = this.canonizeName(canonizeName2);
        final Name name = (Name)canonizeName2.clone();
        name.addAll(canonizeName);
        return name;
    }
    
    @Override
    public Object addToEnvironment(final String s, final Object o) throws NamingException {
        this.myEnv = (Hashtable<String, Object>)((this.myEnv == null) ? new Hashtable<String, Object>(11, 0.75f) : this.myEnv.clone());
        return this.myEnv.put(s, o);
    }
    
    @Override
    public Object removeFromEnvironment(final String s) throws NamingException {
        if (this.myEnv == null) {
            return null;
        }
        this.myEnv = (Hashtable)this.myEnv.clone();
        return this.myEnv.remove(s);
    }
    
    @Override
    public Hashtable<String, Object> getEnvironment() throws NamingException {
        if (this.myEnv == null) {
            return new Hashtable<String, Object>(5, 0.75f);
        }
        return (Hashtable)this.myEnv.clone();
    }
    
    @Override
    public Attributes getAttributes(final String s) throws NamingException {
        return this.getAttributes(this.myParser.parse(s));
    }
    
    @Override
    public Attributes getAttributes(final Name name) throws NamingException {
        return ((HierMemDirCtx)this.doLookup(name, false)).doGetAttributes();
    }
    
    protected Attributes doGetAttributes() throws NamingException {
        return (Attributes)this.attrs.clone();
    }
    
    @Override
    public Attributes getAttributes(final String s, final String[] array) throws NamingException {
        return this.getAttributes(this.myParser.parse(s), array);
    }
    
    @Override
    public Attributes getAttributes(final Name name, final String[] array) throws NamingException {
        return ((HierMemDirCtx)this.doLookup(name, false)).doGetAttributes(array);
    }
    
    protected Attributes doGetAttributes(final String[] array) throws NamingException {
        if (array == null) {
            return this.doGetAttributes();
        }
        final BasicAttributes basicAttributes = new BasicAttributes(this.ignoreCase);
        for (int i = 0; i < array.length; ++i) {
            final Attribute value = this.attrs.get(array[i]);
            if (value != null) {
                basicAttributes.put(value);
            }
        }
        return basicAttributes;
    }
    
    @Override
    public void modifyAttributes(final String s, final int n, final Attributes attributes) throws NamingException {
        this.modifyAttributes(this.myParser.parse(s), n, attributes);
    }
    
    @Override
    public void modifyAttributes(final Name name, final int n, final Attributes attributes) throws NamingException {
        if (attributes == null || attributes.size() == 0) {
            throw new IllegalArgumentException("Cannot modify without an attribute");
        }
        final NamingEnumeration<? extends Attribute> all = attributes.getAll();
        final ModificationItem[] array = new ModificationItem[attributes.size()];
        for (int n2 = 0; n2 < array.length && all.hasMoreElements(); ++n2) {
            array[n2] = new ModificationItem(n, all.next());
        }
        this.modifyAttributes(name, array);
    }
    
    @Override
    public void modifyAttributes(final String s, final ModificationItem[] array) throws NamingException {
        this.modifyAttributes(this.myParser.parse(s), array);
    }
    
    @Override
    public void modifyAttributes(final Name name, final ModificationItem[] array) throws NamingException {
        ((HierMemDirCtx)this.doLookup(name, false)).doModifyAttributes(array);
    }
    
    protected void doModifyAttributes(final ModificationItem[] array) throws NamingException {
        if (this.readOnlyEx != null) {
            throw (NamingException)this.readOnlyEx.fillInStackTrace();
        }
        applyMods(array, this.attrs);
    }
    
    protected static Attributes applyMods(final ModificationItem[] array, final Attributes attributes) throws NamingException {
        for (int i = 0; i < array.length; ++i) {
            final ModificationItem modificationItem = array[i];
            final Attribute attribute = modificationItem.getAttribute();
            switch (modificationItem.getModificationOp()) {
                case 1: {
                    final Attribute value = attributes.get(attribute.getID());
                    if (value == null) {
                        attributes.put((Attribute)attribute.clone());
                        break;
                    }
                    final NamingEnumeration<?> all = attribute.getAll();
                    while (all.hasMore()) {
                        value.add(all.next());
                    }
                    break;
                }
                case 2: {
                    if (attribute.size() == 0) {
                        attributes.remove(attribute.getID());
                        break;
                    }
                    attributes.put((Attribute)attribute.clone());
                    break;
                }
                case 3: {
                    final Attribute value2 = attributes.get(attribute.getID());
                    if (value2 == null) {
                        break;
                    }
                    if (attribute.size() == 0) {
                        attributes.remove(attribute.getID());
                        break;
                    }
                    final NamingEnumeration<?> all2 = attribute.getAll();
                    while (all2.hasMore()) {
                        value2.remove(all2.next());
                    }
                    if (value2.size() == 0) {
                        attributes.remove(attribute.getID());
                        break;
                    }
                    break;
                }
                default: {
                    throw new AttributeModificationException("Unknown mod_op");
                }
            }
        }
        return attributes;
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final Attributes attributes) throws NamingException {
        return this.search(s, attributes, null);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final Attributes attributes) throws NamingException {
        return this.search(name, attributes, null);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final Attributes attributes, final String[] array) throws NamingException {
        return this.search(this.myParser.parse(s), attributes, array);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final Attributes attributes, final String[] returningAttributes) throws NamingException {
        final HierMemDirCtx hierMemDirCtx = (HierMemDirCtx)this.doLookup(name, false);
        final SearchControls searchControls = new SearchControls();
        searchControls.setReturningAttributes(returningAttributes);
        return new LazySearchEnumerationImpl(hierMemDirCtx.doListBindings(false), new ContainmentFilter(attributes), searchControls, this, this.myEnv, false);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final String s, final SearchControls searchControls) throws NamingException {
        return new LazySearchEnumerationImpl(new HierContextEnumerator((Context)this.doLookup(name, false), (searchControls != null) ? searchControls.getSearchScope() : 1), new SearchFilter(s), searchControls, this, this.myEnv, this.alwaysUseFactory);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final Name name, final String s, final Object[] array, final SearchControls searchControls) throws NamingException {
        return this.search(name, SearchFilter.format(s, array), searchControls);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final String s2, final SearchControls searchControls) throws NamingException {
        return this.search(this.myParser.parse(s), s2, searchControls);
    }
    
    @Override
    public NamingEnumeration<SearchResult> search(final String s, final String s2, final Object[] array, final SearchControls searchControls) throws NamingException {
        return this.search(this.myParser.parse(s), s2, array, searchControls);
    }
    
    protected HierMemDirCtx createNewCtx() throws NamingException {
        return new HierMemDirCtx(this.myEnv, this.ignoreCase);
    }
    
    protected Name canonizeName(final Name name) throws NamingException {
        Name name2 = name;
        if (!(name instanceof HierarchicalName)) {
            name2 = new HierarchicalName();
            for (int size = name.size(), i = 0; i < size; ++i) {
                name2.add(i, name.get(i));
            }
        }
        return name2;
    }
    
    protected Name getInternalName(final Name name) throws NamingException {
        return name.getPrefix(name.size() - 1);
    }
    
    protected Name getLeafName(final Name name) throws NamingException {
        return name.getSuffix(name.size() - 1);
    }
    
    @Override
    public DirContext getSchema(final String s) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    @Override
    public DirContext getSchema(final Name name) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    @Override
    public DirContext getSchemaClassDefinition(final String s) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    @Override
    public DirContext getSchemaClassDefinition(final Name name) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    public void setReadOnly(final NamingException readOnlyEx) {
        this.readOnlyEx = readOnlyEx;
    }
    
    public void setIgnoreCase(final boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }
    
    public void setNameParser(final NameParser myParser) {
        this.myParser = myParser;
    }
    
    static {
        defaultParser = new HierarchicalNameParser();
    }
    
    private abstract class BaseFlatNames<T> implements NamingEnumeration<T>
    {
        Enumeration<Name> names;
        
        BaseFlatNames(final Enumeration<Name> names) {
            this.names = names;
        }
        
        @Override
        public final boolean hasMoreElements() {
            try {
                return this.hasMore();
            }
            catch (final NamingException ex) {
                return false;
            }
        }
        
        @Override
        public final boolean hasMore() throws NamingException {
            return this.names.hasMoreElements();
        }
        
        @Override
        public final T nextElement() {
            try {
                return this.next();
            }
            catch (final NamingException ex) {
                throw new NoSuchElementException(ex.toString());
            }
        }
        
        @Override
        public abstract T next() throws NamingException;
        
        @Override
        public final void close() {
            this.names = null;
        }
    }
    
    private final class FlatNames extends BaseFlatNames<NameClassPair>
    {
        FlatNames(final Enumeration<Name> enumeration) {
            super(enumeration);
        }
        
        @Override
        public NameClassPair next() throws NamingException {
            final Name name = this.names.nextElement();
            return new NameClassPair(name.toString(), HierMemDirCtx.this.bindings.get(name).getClass().getName());
        }
    }
    
    private final class FlatBindings extends BaseFlatNames<Binding>
    {
        private Hashtable<Name, Object> bds;
        private Hashtable<String, Object> env;
        private boolean useFactory;
        
        FlatBindings(final Hashtable<Name, Object> bds, final Hashtable<String, Object> env, final boolean useFactory) {
            super(bds.keys());
            this.env = env;
            this.bds = bds;
            this.useFactory = useFactory;
        }
        
        @Override
        public Binding next() throws NamingException {
            final Name name = this.names.nextElement();
            Object objectInstance;
            final HierMemDirCtx hierMemDirCtx = (HierMemDirCtx)(objectInstance = this.bds.get(name));
            if (this.useFactory) {
                final Attributes attributes = hierMemDirCtx.getAttributes("");
                try {
                    objectInstance = DirectoryManager.getObjectInstance(hierMemDirCtx, name, HierMemDirCtx.this, this.env, attributes);
                }
                catch (final NamingException ex) {
                    throw ex;
                }
                catch (final Exception rootCause) {
                    final NamingException ex2 = new NamingException("Problem calling getObjectInstance");
                    ex2.setRootCause(rootCause);
                    throw ex2;
                }
            }
            return new Binding(name.toString(), objectInstance);
        }
    }
    
    public class HierContextEnumerator extends ContextEnumerator
    {
        public HierContextEnumerator(final Context context, final int n) throws NamingException {
            super(context, n);
        }
        
        protected HierContextEnumerator(final Context context, final int n, final String s, final boolean b) throws NamingException {
            super(context, n, s, b);
        }
        
        @Override
        protected NamingEnumeration<Binding> getImmediateChildren(final Context context) throws NamingException {
            return ((HierMemDirCtx)context).doListBindings(false);
        }
        
        @Override
        protected ContextEnumerator newEnumerator(final Context context, final int n, final String s, final boolean b) throws NamingException {
            return new HierContextEnumerator(context, n, s, b);
        }
    }
}
