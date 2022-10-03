package org.apache.xmlbeans.impl.schema;

import java.util.IdentityHashMap;
import java.util.List;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import org.apache.xmlbeans.SchemaIdentityConstraint;
import org.apache.xmlbeans.SchemaAttributeGroup;
import org.apache.xmlbeans.SchemaModelGroup;
import org.apache.xmlbeans.SchemaGlobalAttribute;
import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaType;
import java.io.InputStream;
import org.apache.xmlbeans.impl.common.XBeanDebug;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.impl.common.QNameHelper;
import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.HashMap;
import org.apache.xmlbeans.impl.common.SystemCache;
import java.util.Map;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.ResourceLoader;

public class SchemaTypeLoaderImpl extends SchemaTypeLoaderBase
{
    private ResourceLoader _resourceLoader;
    private ClassLoader _classLoader;
    private SchemaTypeLoader[] _searchPath;
    private Map _classpathTypeSystems;
    private Map _classLoaderTypeSystems;
    private Map _elementCache;
    private Map _attributeCache;
    private Map _modelGroupCache;
    private Map _attributeGroupCache;
    private Map _idConstraintCache;
    private Map _typeCache;
    private Map _documentCache;
    private Map _attributeTypeCache;
    private Map _classnameCache;
    private final String _metadataPath;
    public static String METADATA_PACKAGE_LOAD;
    private static final Object CACHED_NOT_FOUND;
    private static final SchemaTypeLoader[] EMPTY_SCHEMATYPELOADER_ARRAY;
    
    public static SchemaTypeLoaderImpl getContextTypeLoader() {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        SchemaTypeLoaderImpl result = (SchemaTypeLoaderImpl)SystemCache.get().getFromTypeLoaderCache(cl);
        if (result == null) {
            result = new SchemaTypeLoaderImpl(new SchemaTypeLoader[] { BuiltinSchemaTypeSystem.get() }, null, cl, null);
            SystemCache.get().addToTypeLoaderCache(result, cl);
        }
        return result;
    }
    
    public static SchemaTypeLoader build(final SchemaTypeLoader[] searchPath, final ResourceLoader resourceLoader, final ClassLoader classLoader) {
        return build(searchPath, resourceLoader, classLoader, null);
    }
    
    public static SchemaTypeLoader build(final SchemaTypeLoader[] searchPath, final ResourceLoader resourceLoader, final ClassLoader classLoader, final String metadataPath) {
        SchemaTypeLoader[] sp;
        if (searchPath == null) {
            final boolean isDefaultPath = metadataPath == null || ("schema" + SchemaTypeSystemImpl.METADATA_PACKAGE_GEN).equals(metadataPath);
            if (isDefaultPath) {
                sp = null;
            }
            else {
                final String[] baseHolder = { "schemaorg_apache_xmlbeans.system.sXMLCONFIG.TypeSystemHolder", "schemaorg_apache_xmlbeans.system.sXMLLANG.TypeSystemHolder", "schemaorg_apache_xmlbeans.system.sXMLSCHEMA.TypeSystemHolder", "schemaorg_apache_xmlbeans.system.sXMLTOOLS.TypeSystemHolder" };
                sp = new SchemaTypeLoader[baseHolder.length];
                for (int i = 0; i < baseHolder.length; ++i) {
                    try {
                        final Class cls = Class.forName(baseHolder[i]);
                        sp[i] = (SchemaTypeLoader)cls.getDeclaredField("typeSystem").get(null);
                    }
                    catch (final Exception e) {
                        System.out.println("throw runtime: " + e.toString());
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        else {
            final SubLoaderList list = new SubLoaderList();
            list.add(searchPath);
            sp = list.toArray();
        }
        if (sp != null && sp.length == 1 && resourceLoader == null && classLoader == null) {
            return sp[0];
        }
        return new SchemaTypeLoaderImpl(sp, resourceLoader, classLoader, metadataPath);
    }
    
    private SchemaTypeLoaderImpl(final SchemaTypeLoader[] searchPath, final ResourceLoader resourceLoader, final ClassLoader classLoader, final String metadataPath) {
        if (searchPath == null) {
            this._searchPath = SchemaTypeLoaderImpl.EMPTY_SCHEMATYPELOADER_ARRAY;
        }
        else {
            this._searchPath = searchPath;
        }
        this._resourceLoader = resourceLoader;
        this._classLoader = classLoader;
        this._metadataPath = ((metadataPath == null) ? ("schema" + SchemaTypeLoaderImpl.METADATA_PACKAGE_LOAD) : metadataPath);
        this.initCaches();
    }
    
    private final void initCaches() {
        this._classpathTypeSystems = Collections.synchronizedMap(new HashMap<Object, Object>());
        this._classLoaderTypeSystems = Collections.synchronizedMap(new HashMap<Object, Object>());
        this._elementCache = Collections.synchronizedMap(new HashMap<Object, Object>());
        this._attributeCache = Collections.synchronizedMap(new HashMap<Object, Object>());
        this._modelGroupCache = Collections.synchronizedMap(new HashMap<Object, Object>());
        this._attributeGroupCache = Collections.synchronizedMap(new HashMap<Object, Object>());
        this._idConstraintCache = Collections.synchronizedMap(new HashMap<Object, Object>());
        this._typeCache = Collections.synchronizedMap(new HashMap<Object, Object>());
        this._documentCache = Collections.synchronizedMap(new HashMap<Object, Object>());
        this._attributeTypeCache = Collections.synchronizedMap(new HashMap<Object, Object>());
        this._classnameCache = Collections.synchronizedMap(new HashMap<Object, Object>());
    }
    
    SchemaTypeSystemImpl typeSystemForComponent(final String searchdir, final QName name) {
        final String searchfor = searchdir + QNameHelper.hexsafedir(name) + ".xsb";
        String tsname = null;
        if (this._resourceLoader != null) {
            tsname = crackEntry(this._resourceLoader, searchfor);
        }
        if (this._classLoader != null) {
            tsname = crackEntry(this._classLoader, searchfor);
        }
        if (tsname != null) {
            return (SchemaTypeSystemImpl)this.typeSystemForName(tsname);
        }
        return null;
    }
    
    public SchemaTypeSystem typeSystemForName(final String name) {
        if (this._resourceLoader != null) {
            final SchemaTypeSystem result = this.getTypeSystemOnClasspath(name);
            if (result != null) {
                return result;
            }
        }
        if (this._classLoader != null) {
            final SchemaTypeSystem result = this.getTypeSystemOnClassloader(name);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
    
    SchemaTypeSystemImpl typeSystemForClassname(final String searchdir, final String name) {
        final String searchfor = searchdir + name.replace('.', '/') + ".xsb";
        if (this._resourceLoader != null) {
            final String tsname = crackEntry(this._resourceLoader, searchfor);
            if (tsname != null) {
                return this.getTypeSystemOnClasspath(tsname);
            }
        }
        if (this._classLoader != null) {
            final String tsname = crackEntry(this._classLoader, searchfor);
            if (tsname != null) {
                return this.getTypeSystemOnClassloader(tsname);
            }
        }
        return null;
    }
    
    SchemaTypeSystemImpl getTypeSystemOnClasspath(final String name) {
        SchemaTypeSystemImpl result = this._classpathTypeSystems.get(name);
        if (result == null) {
            result = new SchemaTypeSystemImpl(this._resourceLoader, name, this);
            this._classpathTypeSystems.put(name, result);
        }
        return result;
    }
    
    SchemaTypeSystemImpl getTypeSystemOnClassloader(final String name) {
        XBeanDebug.trace(1, "Finding type system " + name + " on classloader", 0);
        SchemaTypeSystemImpl result = this._classLoaderTypeSystems.get(name);
        if (result == null) {
            XBeanDebug.trace(1, "Type system " + name + " not cached - consulting field", 0);
            result = SchemaTypeSystemImpl.forName(name, this._classLoader);
            this._classLoaderTypeSystems.put(name, result);
        }
        return result;
    }
    
    static String crackEntry(final ResourceLoader loader, final String searchfor) {
        final InputStream is = loader.getResourceAsStream(searchfor);
        if (is == null) {
            return null;
        }
        return crackPointer(is);
    }
    
    static String crackEntry(final ClassLoader loader, final String searchfor) {
        final InputStream stream = loader.getResourceAsStream(searchfor);
        if (stream == null) {
            return null;
        }
        return crackPointer(stream);
    }
    
    static String crackPointer(final InputStream stream) {
        return SchemaTypeSystemImpl.crackPointer(stream);
    }
    
    @Override
    public boolean isNamespaceDefined(final String namespace) {
        for (int i = 0; i < this._searchPath.length; ++i) {
            if (this._searchPath[i].isNamespaceDefined(namespace)) {
                return true;
            }
        }
        final SchemaTypeSystem sts = this.typeSystemForComponent(this._metadataPath + "/namespace/", new QName(namespace, "xmlns"));
        return sts != null;
    }
    
    @Override
    public SchemaType.Ref findTypeRef(final QName name) {
        final Object cached = this._typeCache.get(name);
        if (cached == SchemaTypeLoaderImpl.CACHED_NOT_FOUND) {
            return null;
        }
        SchemaType.Ref result = (SchemaType.Ref)cached;
        if (result == null) {
            for (int i = 0; i < this._searchPath.length && null == (result = this._searchPath[i].findTypeRef(name)); ++i) {}
            if (result == null) {
                final SchemaTypeSystem ts = this.typeSystemForComponent(this._metadataPath + "/type/", name);
                if (ts != null) {
                    result = ts.findTypeRef(name);
                    assert result != null : "Type system registered type " + QNameHelper.pretty(name) + " but does not return it";
                }
            }
            this._typeCache.put(name, (result == null) ? SchemaTypeLoaderImpl.CACHED_NOT_FOUND : result);
        }
        return result;
    }
    
    @Override
    public SchemaType typeForClassname(String classname) {
        classname = classname.replace('$', '.');
        final Object cached = this._classnameCache.get(classname);
        if (cached == SchemaTypeLoaderImpl.CACHED_NOT_FOUND) {
            return null;
        }
        SchemaType result = (SchemaType)cached;
        if (result == null) {
            for (int i = 0; i < this._searchPath.length && null == (result = this._searchPath[i].typeForClassname(classname)); ++i) {}
            if (result == null) {
                final SchemaTypeSystem ts = this.typeSystemForClassname(this._metadataPath + "/javaname/", classname);
                if (ts != null) {
                    result = ts.typeForClassname(classname);
                    assert result != null : "Type system registered type " + classname + " but does not return it";
                }
            }
            this._classnameCache.put(classname, (result == null) ? SchemaTypeLoaderImpl.CACHED_NOT_FOUND : result);
        }
        return result;
    }
    
    @Override
    public SchemaType.Ref findDocumentTypeRef(final QName name) {
        final Object cached = this._documentCache.get(name);
        if (cached == SchemaTypeLoaderImpl.CACHED_NOT_FOUND) {
            return null;
        }
        SchemaType.Ref result = (SchemaType.Ref)cached;
        if (result == null) {
            for (int i = 0; i < this._searchPath.length && null == (result = this._searchPath[i].findDocumentTypeRef(name)); ++i) {}
            if (result == null) {
                final SchemaTypeSystem ts = this.typeSystemForComponent(this._metadataPath + "/element/", name);
                if (ts != null) {
                    result = ts.findDocumentTypeRef(name);
                    assert result != null : "Type system registered element " + QNameHelper.pretty(name) + " but does not contain document type";
                }
            }
            this._documentCache.put(name, (result == null) ? SchemaTypeLoaderImpl.CACHED_NOT_FOUND : result);
        }
        return result;
    }
    
    @Override
    public SchemaType.Ref findAttributeTypeRef(final QName name) {
        final Object cached = this._attributeTypeCache.get(name);
        if (cached == SchemaTypeLoaderImpl.CACHED_NOT_FOUND) {
            return null;
        }
        SchemaType.Ref result = (SchemaType.Ref)cached;
        if (result == null) {
            for (int i = 0; i < this._searchPath.length && null == (result = this._searchPath[i].findAttributeTypeRef(name)); ++i) {}
            if (result == null) {
                final SchemaTypeSystem ts = this.typeSystemForComponent(this._metadataPath + "/attribute/", name);
                if (ts != null) {
                    result = ts.findAttributeTypeRef(name);
                    assert result != null : "Type system registered attribute " + QNameHelper.pretty(name) + " but does not contain attribute type";
                }
            }
            this._attributeTypeCache.put(name, (result == null) ? SchemaTypeLoaderImpl.CACHED_NOT_FOUND : result);
        }
        return result;
    }
    
    @Override
    public SchemaGlobalElement.Ref findElementRef(final QName name) {
        final Object cached = this._elementCache.get(name);
        if (cached == SchemaTypeLoaderImpl.CACHED_NOT_FOUND) {
            return null;
        }
        SchemaGlobalElement.Ref result = (SchemaGlobalElement.Ref)cached;
        if (result == null) {
            for (int i = 0; i < this._searchPath.length && null == (result = this._searchPath[i].findElementRef(name)); ++i) {}
            if (result == null) {
                final SchemaTypeSystem ts = this.typeSystemForComponent(this._metadataPath + "/element/", name);
                if (ts != null) {
                    result = ts.findElementRef(name);
                    assert result != null : "Type system registered element " + QNameHelper.pretty(name) + " but does not return it";
                }
            }
            this._elementCache.put(name, (result == null) ? SchemaTypeLoaderImpl.CACHED_NOT_FOUND : result);
        }
        return result;
    }
    
    @Override
    public SchemaGlobalAttribute.Ref findAttributeRef(final QName name) {
        final Object cached = this._attributeCache.get(name);
        if (cached == SchemaTypeLoaderImpl.CACHED_NOT_FOUND) {
            return null;
        }
        SchemaGlobalAttribute.Ref result = (SchemaGlobalAttribute.Ref)cached;
        if (result == null) {
            for (int i = 0; i < this._searchPath.length && null == (result = this._searchPath[i].findAttributeRef(name)); ++i) {}
            if (result == null) {
                final SchemaTypeSystem ts = this.typeSystemForComponent(this._metadataPath + "/attribute/", name);
                if (ts != null) {
                    result = ts.findAttributeRef(name);
                    assert result != null : "Type system registered attribute " + QNameHelper.pretty(name) + " but does not return it";
                }
            }
            this._attributeCache.put(name, (result == null) ? SchemaTypeLoaderImpl.CACHED_NOT_FOUND : result);
        }
        return result;
    }
    
    @Override
    public SchemaModelGroup.Ref findModelGroupRef(final QName name) {
        final Object cached = this._modelGroupCache.get(name);
        if (cached == SchemaTypeLoaderImpl.CACHED_NOT_FOUND) {
            return null;
        }
        SchemaModelGroup.Ref result = (SchemaModelGroup.Ref)cached;
        if (result == null) {
            for (int i = 0; i < this._searchPath.length && null == (result = this._searchPath[i].findModelGroupRef(name)); ++i) {}
            if (result == null) {
                final SchemaTypeSystem ts = this.typeSystemForComponent(this._metadataPath + "/modelgroup/", name);
                if (ts != null) {
                    result = ts.findModelGroupRef(name);
                    assert result != null : "Type system registered model group " + QNameHelper.pretty(name) + " but does not return it";
                }
            }
            this._modelGroupCache.put(name, (result == null) ? SchemaTypeLoaderImpl.CACHED_NOT_FOUND : result);
        }
        return result;
    }
    
    @Override
    public SchemaAttributeGroup.Ref findAttributeGroupRef(final QName name) {
        final Object cached = this._attributeGroupCache.get(name);
        if (cached == SchemaTypeLoaderImpl.CACHED_NOT_FOUND) {
            return null;
        }
        SchemaAttributeGroup.Ref result = (SchemaAttributeGroup.Ref)cached;
        if (result == null) {
            for (int i = 0; i < this._searchPath.length && null == (result = this._searchPath[i].findAttributeGroupRef(name)); ++i) {}
            if (result == null) {
                final SchemaTypeSystem ts = this.typeSystemForComponent(this._metadataPath + "/attributegroup/", name);
                if (ts != null) {
                    result = ts.findAttributeGroupRef(name);
                    assert result != null : "Type system registered attribute group " + QNameHelper.pretty(name) + " but does not return it";
                }
            }
            this._attributeGroupCache.put(name, (result == null) ? SchemaTypeLoaderImpl.CACHED_NOT_FOUND : result);
        }
        return result;
    }
    
    @Override
    public SchemaIdentityConstraint.Ref findIdentityConstraintRef(final QName name) {
        final Object cached = this._idConstraintCache.get(name);
        if (cached == SchemaTypeLoaderImpl.CACHED_NOT_FOUND) {
            return null;
        }
        SchemaIdentityConstraint.Ref result = (SchemaIdentityConstraint.Ref)cached;
        if (result == null) {
            for (int i = 0; i < this._searchPath.length && null == (result = this._searchPath[i].findIdentityConstraintRef(name)); ++i) {}
            if (result == null) {
                final SchemaTypeSystem ts = this.typeSystemForComponent(this._metadataPath + "/identityconstraint/", name);
                if (ts != null) {
                    result = ts.findIdentityConstraintRef(name);
                    assert result != null : "Type system registered identity constraint " + QNameHelper.pretty(name) + " but does not return it";
                }
            }
            this._idConstraintCache.put(name, (result == null) ? SchemaTypeLoaderImpl.CACHED_NOT_FOUND : result);
        }
        return result;
    }
    
    @Override
    public InputStream getSourceAsStream(String sourceName) {
        InputStream result = null;
        if (!sourceName.startsWith("/")) {
            sourceName = "/" + sourceName;
        }
        if (this._resourceLoader != null) {
            result = this._resourceLoader.getResourceAsStream(this._metadataPath + "/src" + sourceName);
        }
        if (result == null && this._classLoader != null) {
            return this._classLoader.getResourceAsStream(this._metadataPath + "/src" + sourceName);
        }
        return result;
    }
    
    static {
        SchemaTypeLoaderImpl.METADATA_PACKAGE_LOAD = SchemaTypeSystemImpl.METADATA_PACKAGE_GEN;
        CACHED_NOT_FOUND = new Object();
        EMPTY_SCHEMATYPELOADER_ARRAY = new SchemaTypeLoader[0];
        if (SystemCache.get() instanceof SystemCache) {
            SystemCache.set(new SchemaTypeLoaderCache());
        }
    }
    
    private static class SchemaTypeLoaderCache extends SystemCache
    {
        private ThreadLocal _cachedTypeSystems;
        
        private SchemaTypeLoaderCache() {
            this._cachedTypeSystems = new ThreadLocal() {
                @Override
                protected Object initialValue() {
                    return new ArrayList();
                }
            };
        }
        
        @Override
        public void clearThreadLocals() {
            this._cachedTypeSystems.remove();
            super.clearThreadLocals();
        }
        
        @Override
        public SchemaTypeLoader getFromTypeLoaderCache(final ClassLoader cl) {
            final ArrayList a = this._cachedTypeSystems.get();
            int candidate = -1;
            SchemaTypeLoaderImpl result = null;
            for (int i = 0; i < a.size(); ++i) {
                final SchemaTypeLoaderImpl tl = a.get(i).get();
                if (tl == null) {
                    assert i > candidate;
                    a.remove(i--);
                }
                else if (tl._classLoader == cl) {
                    assert candidate == -1 && result == null;
                    candidate = i;
                    result = tl;
                    break;
                }
            }
            if (candidate > 0) {
                final Object t = a.get(0);
                a.set(0, a.get(candidate));
                a.set(candidate, t);
            }
            return result;
        }
        
        @Override
        public void addToTypeLoaderCache(final SchemaTypeLoader stl, final ClassLoader cl) {
            assert stl instanceof SchemaTypeLoaderImpl && ((SchemaTypeLoaderImpl)stl)._classLoader == cl;
            final ArrayList a = this._cachedTypeSystems.get();
            if (a.size() > 0) {
                final Object t = a.get(0);
                a.set(0, new SoftReference(stl));
                a.add(t);
            }
            else {
                a.add(new SoftReference(stl));
            }
        }
    }
    
    private static class SubLoaderList
    {
        private final List<SchemaTypeLoader> theList;
        private final Map<SchemaTypeLoader, Object> seen;
        
        private SubLoaderList() {
            this.theList = new ArrayList<SchemaTypeLoader>();
            this.seen = new IdentityHashMap<SchemaTypeLoader, Object>();
        }
        
        void add(final SchemaTypeLoader[] searchPath) {
            if (searchPath == null) {
                return;
            }
            for (final SchemaTypeLoader stl : searchPath) {
                if (stl instanceof SchemaTypeLoaderImpl) {
                    final SchemaTypeLoaderImpl sub = (SchemaTypeLoaderImpl)stl;
                    if (sub._classLoader != null || sub._resourceLoader != null) {
                        this.add(sub);
                    }
                    else {
                        this.add(sub._searchPath);
                    }
                }
                else {
                    this.add(stl);
                }
            }
        }
        
        void add(final SchemaTypeLoader loader) {
            if (loader != null && !this.seen.containsKey(loader)) {
                this.theList.add(loader);
                this.seen.put(loader, null);
            }
        }
        
        SchemaTypeLoader[] toArray() {
            return this.theList.toArray(SchemaTypeLoaderImpl.EMPTY_SCHEMATYPELOADER_ARRAY);
        }
    }
}
