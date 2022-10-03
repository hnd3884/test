package org.apache.xmlbeans.impl.schema;

import org.apache.xmlbeans.impl.values.XmlObjectBase;
import org.apache.xmlbeans.SimpleValue;
import java.util.LinkedHashSet;
import org.apache.xmlbeans.soap.SchemaWSDLArrayType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.SchemaLocalElement;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaStringEnumEntry;
import org.apache.xmlbeans.impl.regex.RegularExpression;
import org.apache.xmlbeans.SchemaAttributeModel;
import org.apache.xmlbeans.impl.xb.xsdschema.AttributeGroupDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.GroupDocument;
import org.apache.xmlbeans.QNameSet;
import java.math.BigInteger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.soap.SOAPArrayType;
import org.apache.xmlbeans.impl.common.NameUtil;
import org.apache.xmlbeans.SchemaLocalAttribute;
import org.apache.xmlbeans.SchemaParticle;
import org.apache.xmlbeans.impl.util.HexBin;
import org.apache.xmlbeans.SystemProperties;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Collection;
import java.util.Arrays;
import java.util.LinkedHashMap;
import org.apache.xmlbeans.impl.repackage.Repackager;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.EOFException;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import org.apache.xmlbeans.impl.common.DefaultClassLoaderResourceLoader;
import org.apache.xmlbeans.impl.util.FilerImpl;
import javax.xml.namespace.QName;
import java.util.Iterator;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.SchemaComponent;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.io.IOException;
import org.apache.xmlbeans.SchemaTypeLoaderException;
import java.util.zip.ZipFile;
import java.io.File;
import org.apache.xmlbeans.impl.common.XBeanDebug;
import java.util.Collections;
import java.util.HashMap;
import org.apache.xmlbeans.SchemaAnnotation;
import org.apache.xmlbeans.SchemaIdentityConstraint;
import org.apache.xmlbeans.SchemaAttributeGroup;
import org.apache.xmlbeans.SchemaModelGroup;
import org.apache.xmlbeans.SchemaGlobalAttribute;
import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaType;
import java.util.Set;
import java.util.Map;
import java.util.List;
import org.apache.xmlbeans.Filer;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.ResourceLoader;
import java.util.Random;
import org.apache.xmlbeans.SchemaTypeSystem;

public class SchemaTypeSystemImpl extends SchemaTypeLoaderBase implements SchemaTypeSystem
{
    public static final int DATA_BABE = -629491010;
    public static final int MAJOR_VERSION = 2;
    public static final int MINOR_VERSION = 24;
    public static final int RELEASE_NUMBER = 0;
    public static final int FILETYPE_SCHEMAINDEX = 1;
    public static final int FILETYPE_SCHEMATYPE = 2;
    public static final int FILETYPE_SCHEMAELEMENT = 3;
    public static final int FILETYPE_SCHEMAATTRIBUTE = 4;
    public static final int FILETYPE_SCHEMAPOINTER = 5;
    public static final int FILETYPE_SCHEMAMODELGROUP = 6;
    public static final int FILETYPE_SCHEMAATTRIBUTEGROUP = 7;
    public static final int FILETYPE_SCHEMAIDENTITYCONSTRAINT = 8;
    public static final int FLAG_PART_SKIPPABLE = 1;
    public static final int FLAG_PART_FIXED = 4;
    public static final int FLAG_PART_NILLABLE = 8;
    public static final int FLAG_PART_BLOCKEXT = 16;
    public static final int FLAG_PART_BLOCKREST = 32;
    public static final int FLAG_PART_BLOCKSUBST = 64;
    public static final int FLAG_PART_ABSTRACT = 128;
    public static final int FLAG_PART_FINALEXT = 256;
    public static final int FLAG_PART_FINALREST = 512;
    public static final int FLAG_PROP_ISATTR = 1;
    public static final int FLAG_PROP_JAVASINGLETON = 2;
    public static final int FLAG_PROP_JAVAOPTIONAL = 4;
    public static final int FLAG_PROP_JAVAARRAY = 8;
    public static final int FIELD_NONE = 0;
    public static final int FIELD_GLOBAL = 1;
    public static final int FIELD_LOCALATTR = 2;
    public static final int FIELD_LOCALELT = 3;
    static final int FLAG_SIMPLE_TYPE = 1;
    static final int FLAG_DOCUMENT_TYPE = 2;
    static final int FLAG_ORDERED = 4;
    static final int FLAG_BOUNDED = 8;
    static final int FLAG_FINITE = 16;
    static final int FLAG_NUMERIC = 32;
    static final int FLAG_STRINGENUM = 64;
    static final int FLAG_UNION_OF_LISTS = 128;
    static final int FLAG_HAS_PATTERN = 256;
    static final int FLAG_ORDER_SENSITIVE = 512;
    static final int FLAG_TOTAL_ORDER = 1024;
    static final int FLAG_COMPILED = 2048;
    static final int FLAG_BLOCK_EXT = 4096;
    static final int FLAG_BLOCK_REST = 8192;
    static final int FLAG_FINAL_EXT = 16384;
    static final int FLAG_FINAL_REST = 32768;
    static final int FLAG_FINAL_UNION = 65536;
    static final int FLAG_FINAL_LIST = 131072;
    static final int FLAG_ABSTRACT = 262144;
    static final int FLAG_ATTRIBUTE_TYPE = 524288;
    public static String METADATA_PACKAGE_GEN;
    private static final String HOLDER_TEMPLATE_CLASS = "org.apache.xmlbeans.impl.schema.TypeSystemHolder";
    private static final String HOLDER_TEMPLATE_CLASSFILE = "TypeSystemHolder.template";
    private static final String[] HOLDER_TEMPLATE_NAMES;
    private static final int CONSTANT_UTF8 = 1;
    private static final int CONSTANT_UNICODE = 2;
    private static final int CONSTANT_INTEGER = 3;
    private static final int CONSTANT_FLOAT = 4;
    private static final int CONSTANT_LONG = 5;
    private static final int CONSTANT_DOUBLE = 6;
    private static final int CONSTANT_CLASS = 7;
    private static final int CONSTANT_STRING = 8;
    private static final int CONSTANT_FIELD = 9;
    private static final int CONSTANT_METHOD = 10;
    private static final int CONSTANT_INTERFACEMETHOD = 11;
    private static final int CONSTANT_NAMEANDTYPE = 12;
    private static final int MAX_UNSIGNED_SHORT = 65535;
    private static Random _random;
    private static byte[] _mask;
    private String _name;
    private String _basePackage;
    private boolean _incomplete;
    private ClassLoader _classloader;
    private ResourceLoader _resourceLoader;
    SchemaTypeLoader _linker;
    private HandlePool _localHandles;
    private Filer _filer;
    private List _annotations;
    private Map _containers;
    private SchemaDependencies _deps;
    private List _redefinedModelGroups;
    private List _redefinedAttributeGroups;
    private List _redefinedGlobalTypes;
    private Map _globalElements;
    private Map _globalAttributes;
    private Map _modelGroups;
    private Map _attributeGroups;
    private Map _globalTypes;
    private Map _documentTypes;
    private Map _attributeTypes;
    private Map _identityConstraints;
    private Map _typeRefsByClassname;
    private Set _namespaces;
    private static final SchemaType[] EMPTY_ST_ARRAY;
    private static final SchemaGlobalElement[] EMPTY_GE_ARRAY;
    private static final SchemaGlobalAttribute[] EMPTY_GA_ARRAY;
    private static final SchemaModelGroup[] EMPTY_MG_ARRAY;
    private static final SchemaAttributeGroup[] EMPTY_AG_ARRAY;
    private static final SchemaIdentityConstraint[] EMPTY_IC_ARRAY;
    private static final SchemaAnnotation[] EMPTY_ANN_ARRAY;
    static final byte[] SINGLE_ZERO_BYTE;
    private final Map _resolvedHandles;
    private boolean _allNonGroupHandlesResolved;
    
    private static String nameToPathString(String nameForSystem) {
        nameForSystem = nameForSystem.replace('.', '/');
        if (!nameForSystem.endsWith("/") && nameForSystem.length() > 0) {
            nameForSystem += "/";
        }
        return nameForSystem;
    }
    
    public SchemaTypeSystemImpl(final Class indexclass) {
        this._incomplete = false;
        this._containers = new HashMap();
        this._identityConstraints = Collections.EMPTY_MAP;
        this._typeRefsByClassname = new HashMap();
        this._resolvedHandles = new HashMap();
        this._allNonGroupHandlesResolved = false;
        final String fullname = indexclass.getName();
        this._name = fullname.substring(0, fullname.lastIndexOf(46));
        XBeanDebug.trace(1, "Loading type system " + this._name, 1);
        this._basePackage = nameToPathString(this._name);
        this._classloader = indexclass.getClassLoader();
        this._linker = SchemaTypeLoaderImpl.build(null, null, this._classloader, this.getMetadataPath());
        this._resourceLoader = new ClassLoaderResourceLoader(this._classloader);
        try {
            this.initFromHeader();
        }
        catch (final RuntimeException e) {
            XBeanDebug.logException(e);
            throw e;
        }
        catch (final Error e2) {
            XBeanDebug.logException(e2);
            throw e2;
        }
        XBeanDebug.trace(1, "Finished loading type system " + this._name, -1);
    }
    
    public static boolean fileContainsTypeSystem(final File file, final String name) {
        final String indexname = nameToPathString(name) + "index.xsb";
        if (file.isDirectory()) {
            return new File(file, indexname).isFile();
        }
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(file);
            final ZipEntry entry = zipfile.getEntry(indexname);
            return entry != null && !entry.isDirectory();
        }
        catch (final IOException e) {
            XBeanDebug.log("Problem loading SchemaTypeSystem, zipfilename " + file);
            XBeanDebug.logException(e);
            throw new SchemaTypeLoaderException(e.getMessage(), name, "index", 9);
        }
        finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                }
                catch (final IOException ex) {}
            }
        }
    }
    
    public static SchemaTypeSystemImpl forName(final String name, final ClassLoader loader) {
        try {
            final Class c = Class.forName(name + "." + "TypeSystemHolder", true, loader);
            return (SchemaTypeSystemImpl)c.getField("typeSystem").get(null);
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    public SchemaTypeSystemImpl(final ResourceLoader resourceLoader, final String name, final SchemaTypeLoader linker) {
        this._incomplete = false;
        this._containers = new HashMap();
        this._identityConstraints = Collections.EMPTY_MAP;
        this._typeRefsByClassname = new HashMap();
        this._resolvedHandles = new HashMap();
        this._allNonGroupHandlesResolved = false;
        this._name = name;
        this._basePackage = nameToPathString(this._name);
        this._linker = linker;
        this._resourceLoader = resourceLoader;
        try {
            this.initFromHeader();
        }
        catch (final RuntimeException e) {
            XBeanDebug.logException(e);
            throw e;
        }
        catch (final Error e2) {
            XBeanDebug.logException(e2);
            throw e2;
        }
    }
    
    private void initFromHeader() {
        XBeanDebug.trace(1, "Reading unresolved handles for type system " + this._name, 0);
        XsbReader reader = null;
        try {
            reader = new XsbReader("index", 1);
            reader.readHandlePool(this._localHandles = new HandlePool());
            this._globalElements = reader.readQNameRefMap();
            this._globalAttributes = reader.readQNameRefMap();
            this._modelGroups = reader.readQNameRefMap();
            this._attributeGroups = reader.readQNameRefMap();
            this._identityConstraints = reader.readQNameRefMap();
            this._globalTypes = reader.readQNameRefMap();
            this._documentTypes = reader.readQNameRefMap();
            this._attributeTypes = reader.readQNameRefMap();
            this._typeRefsByClassname = reader.readClassnameRefMap();
            this._namespaces = reader.readNamespaces();
            final List typeNames = new ArrayList();
            final List modelGroupNames = new ArrayList();
            final List attributeGroupNames = new ArrayList();
            if (reader.atLeast(2, 15, 0)) {
                this._redefinedGlobalTypes = reader.readQNameRefMapAsList(typeNames);
                this._redefinedModelGroups = reader.readQNameRefMapAsList(modelGroupNames);
                this._redefinedAttributeGroups = reader.readQNameRefMapAsList(attributeGroupNames);
            }
            if (reader.atLeast(2, 19, 0)) {
                this._annotations = reader.readAnnotations();
            }
            this.buildContainers(typeNames, modelGroupNames, attributeGroupNames);
        }
        finally {
            if (reader != null) {
                reader.readEnd();
            }
        }
    }
    
    void saveIndex() {
        final String handle = "index";
        final XsbReader saver = new XsbReader(handle);
        saver.writeIndexData();
        saver.writeRealHeader(handle, 1);
        saver.writeIndexData();
        saver.writeEnd();
    }
    
    void savePointers() {
        this.savePointersForComponents(this.globalElements(), this.getMetadataPath() + "/element/");
        this.savePointersForComponents(this.globalAttributes(), this.getMetadataPath() + "/attribute/");
        this.savePointersForComponents(this.modelGroups(), this.getMetadataPath() + "/modelgroup/");
        this.savePointersForComponents(this.attributeGroups(), this.getMetadataPath() + "/attributegroup/");
        this.savePointersForComponents(this.globalTypes(), this.getMetadataPath() + "/type/");
        this.savePointersForComponents(this.identityConstraints(), this.getMetadataPath() + "/identityconstraint/");
        this.savePointersForNamespaces(this._namespaces, this.getMetadataPath() + "/namespace/");
        this.savePointersForClassnames(this._typeRefsByClassname.keySet(), this.getMetadataPath() + "/javaname/");
        this.savePointersForComponents(this.redefinedModelGroups(), this.getMetadataPath() + "/redefinedmodelgroup/");
        this.savePointersForComponents(this.redefinedAttributeGroups(), this.getMetadataPath() + "/redefinedattributegroup/");
        this.savePointersForComponents(this.redefinedGlobalTypes(), this.getMetadataPath() + "/redefinedtype/");
    }
    
    void savePointersForComponents(final SchemaComponent[] components, final String dir) {
        for (int i = 0; i < components.length; ++i) {
            this.savePointerFile(dir + QNameHelper.hexsafedir(components[i].getName()), this._name);
        }
    }
    
    void savePointersForClassnames(final Set classnames, final String dir) {
        for (final String classname : classnames) {
            this.savePointerFile(dir + classname.replace('.', '/'), this._name);
        }
    }
    
    void savePointersForNamespaces(final Set namespaces, final String dir) {
        for (final String ns : namespaces) {
            this.savePointerFile(dir + QNameHelper.hexsafedir(new QName(ns, "xmlns")), this._name);
        }
    }
    
    void savePointerFile(final String filename, final String name) {
        final XsbReader saver = new XsbReader(filename);
        saver.writeString(name);
        saver.writeRealHeader(filename, 5);
        saver.writeString(name);
        saver.writeEnd();
    }
    
    void saveLoader() {
        final String indexClassName = SchemaTypeCodePrinter.indexClassForSystem(this);
        final String[] replace = makeClassStrings(indexClassName);
        assert replace.length == SchemaTypeSystemImpl.HOLDER_TEMPLATE_NAMES.length;
        InputStream is = null;
        OutputStream os = null;
        DataInputStream in = null;
        DataOutputStream out = null;
        Repackager repackager = null;
        if (this._filer instanceof FilerImpl) {
            repackager = ((FilerImpl)this._filer).getRepackager();
        }
        try {
            is = SchemaTypeSystemImpl.class.getResourceAsStream("TypeSystemHolder.template");
            if (is == null) {
                final DefaultClassLoaderResourceLoader clLoader = new DefaultClassLoaderResourceLoader();
                is = clLoader.getResourceAsStream("TypeSystemHolder.template");
            }
            if (is == null) {
                throw new SchemaTypeLoaderException("couldn't find resource: TypeSystemHolder.template", this._name, null, 9);
            }
            in = new DataInputStream(is);
            os = this._filer.createBinaryFile(indexClassName.replace('.', '/') + ".class");
            out = new DataOutputStream(os);
            out.writeInt(in.readInt());
            out.writeShort(in.readUnsignedShort());
            out.writeShort(in.readUnsignedShort());
            final int poolsize = in.readUnsignedShort();
            out.writeShort(poolsize);
            for (int i = 1; i < poolsize; ++i) {
                final int tag = in.readUnsignedByte();
                out.writeByte(tag);
                switch (tag) {
                    case 1: {
                        final String value = in.readUTF();
                        out.writeUTF(repackageConstant(value, replace, repackager));
                        break;
                    }
                    case 7:
                    case 8: {
                        out.writeShort(in.readUnsignedShort());
                        break;
                    }
                    case 9:
                    case 10:
                    case 11:
                    case 12: {
                        out.writeShort(in.readUnsignedShort());
                        out.writeShort(in.readUnsignedShort());
                        break;
                    }
                    case 3:
                    case 4: {
                        out.writeInt(in.readInt());
                        break;
                    }
                    case 5:
                    case 6: {
                        out.writeInt(in.readInt());
                        out.writeInt(in.readInt());
                        break;
                    }
                    default: {
                        throw new RuntimeException("Unexpected constant type: " + tag);
                    }
                }
            }
            Label_0441: {
                break Label_0441;
                try {
                    while (true) {
                        out.writeByte(in.readByte());
                    }
                }
                catch (final EOFException e) {}
            }
        }
        catch (final IOException e2) {}
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (final Exception ex) {}
            }
            if (os != null) {
                try {
                    os.close();
                }
                catch (final Exception ex2) {}
            }
        }
    }
    
    private static String repackageConstant(final String value, final String[] replace, final Repackager repackager) {
        for (int i = 0; i < SchemaTypeSystemImpl.HOLDER_TEMPLATE_NAMES.length; ++i) {
            if (SchemaTypeSystemImpl.HOLDER_TEMPLATE_NAMES[i].equals(value)) {
                return replace[i];
            }
        }
        if (repackager != null) {
            return repackager.repackage(new StringBuffer(value)).toString();
        }
        return value;
    }
    
    private static String[] makeClassStrings(final String classname) {
        final String[] result = { classname, classname.replace('.', '/'), null, null };
        result[2] = "L" + result[1] + ";";
        result[3] = "class$" + classname.replace('.', '$');
        return result;
    }
    
    private Map buildTypeRefsByClassname() {
        final List allSeenTypes = new ArrayList();
        final Map result = new LinkedHashMap();
        allSeenTypes.addAll(Arrays.asList(this.documentTypes()));
        allSeenTypes.addAll(Arrays.asList(this.attributeTypes()));
        allSeenTypes.addAll(Arrays.asList(this.globalTypes()));
        for (int i = 0; i < allSeenTypes.size(); ++i) {
            final SchemaType gType = allSeenTypes.get(i);
            final String className = gType.getFullJavaName();
            if (className != null) {
                result.put(className.replace('$', '.'), gType.getRef());
            }
            allSeenTypes.addAll(Arrays.asList(gType.getAnonymousTypes()));
        }
        return result;
    }
    
    private Map buildTypeRefsByClassname(final Map typesByClassname) {
        final Map result = new LinkedHashMap();
        for (final String className : typesByClassname.keySet()) {
            result.put(className, typesByClassname.get(className).getRef());
        }
        return result;
    }
    
    private static Map buildComponentRefMap(final SchemaComponent[] components) {
        final Map result = new LinkedHashMap();
        for (int i = 0; i < components.length; ++i) {
            result.put(components[i].getName(), components[i].getComponentRef());
        }
        return result;
    }
    
    private static List buildComponentRefList(final SchemaComponent[] components) {
        final List result = new ArrayList();
        for (int i = 0; i < components.length; ++i) {
            result.add(components[i].getComponentRef());
        }
        return result;
    }
    
    private static Map buildDocumentMap(final SchemaType[] types) {
        final Map result = new LinkedHashMap();
        for (int i = 0; i < types.length; ++i) {
            result.put(types[i].getDocumentElementName(), types[i].getRef());
        }
        return result;
    }
    
    private static Map buildAttributeTypeMap(final SchemaType[] types) {
        final Map result = new LinkedHashMap();
        for (int i = 0; i < types.length; ++i) {
            result.put(types[i].getAttributeTypeAttributeName(), types[i].getRef());
        }
        return result;
    }
    
    private SchemaContainer getContainer(final String namespace) {
        return this._containers.get(namespace);
    }
    
    private void addContainer(final String namespace) {
        final SchemaContainer c = new SchemaContainer(namespace);
        c.setTypeSystem(this);
        this._containers.put(namespace, c);
    }
    
    private SchemaContainer getContainerNonNull(final String namespace) {
        SchemaContainer result = this.getContainer(namespace);
        if (result == null) {
            this.addContainer(namespace);
            result = this.getContainer(namespace);
        }
        return result;
    }
    
    private void buildContainers(final List redefTypeNames, final List redefModelGroupNames, final List redefAttributeGroupNames) {
        for (final Map.Entry entry : this._globalElements.entrySet()) {
            final String ns = entry.getKey().getNamespaceURI();
            this.getContainerNonNull(ns).addGlobalElement(entry.getValue());
        }
        for (final Map.Entry entry : this._globalAttributes.entrySet()) {
            final String ns = entry.getKey().getNamespaceURI();
            this.getContainerNonNull(ns).addGlobalAttribute(entry.getValue());
        }
        for (final Map.Entry entry : this._modelGroups.entrySet()) {
            final String ns = entry.getKey().getNamespaceURI();
            this.getContainerNonNull(ns).addModelGroup(entry.getValue());
        }
        for (final Map.Entry entry : this._attributeGroups.entrySet()) {
            final String ns = entry.getKey().getNamespaceURI();
            this.getContainerNonNull(ns).addAttributeGroup(entry.getValue());
        }
        for (final Map.Entry entry : this._identityConstraints.entrySet()) {
            final String ns = entry.getKey().getNamespaceURI();
            this.getContainerNonNull(ns).addIdentityConstraint(entry.getValue());
        }
        for (final Map.Entry entry : this._globalTypes.entrySet()) {
            final String ns = entry.getKey().getNamespaceURI();
            this.getContainerNonNull(ns).addGlobalType(entry.getValue());
        }
        for (final Map.Entry entry : this._documentTypes.entrySet()) {
            final String ns = entry.getKey().getNamespaceURI();
            this.getContainerNonNull(ns).addDocumentType(entry.getValue());
        }
        for (final Map.Entry entry : this._attributeTypes.entrySet()) {
            final String ns = entry.getKey().getNamespaceURI();
            this.getContainerNonNull(ns).addAttributeType(entry.getValue());
        }
        if (this._redefinedGlobalTypes != null && this._redefinedModelGroups != null && this._redefinedAttributeGroups != null) {
            assert this._redefinedGlobalTypes.size() == redefTypeNames.size();
            Iterator it = this._redefinedGlobalTypes.iterator();
            Iterator itname = redefTypeNames.iterator();
            while (it.hasNext()) {
                final String ns = itname.next().getNamespaceURI();
                this.getContainerNonNull(ns).addRedefinedType(it.next());
            }
            it = this._redefinedModelGroups.iterator();
            itname = redefModelGroupNames.iterator();
            while (it.hasNext()) {
                final String ns = itname.next().getNamespaceURI();
                this.getContainerNonNull(ns).addRedefinedModelGroup(it.next());
            }
            it = this._redefinedAttributeGroups.iterator();
            itname = redefAttributeGroupNames.iterator();
            while (it.hasNext()) {
                final String ns = itname.next().getNamespaceURI();
                this.getContainerNonNull(ns).addRedefinedAttributeGroup(it.next());
            }
        }
        if (this._annotations != null) {
            for (final SchemaAnnotation ann : this._annotations) {
                this.getContainerNonNull("").addAnnotation(ann);
            }
        }
        Iterator it = this._containers.values().iterator();
        while (it.hasNext()) {
            it.next().setImmutable();
        }
    }
    
    private void fixupContainers() {
        for (final SchemaContainer container : this._containers.values()) {
            container.setTypeSystem(this);
            container.setImmutable();
        }
    }
    
    private void assertContainersSynchronized() {
        boolean assertEnabled = false;
        assert assertEnabled = true;
        if (!assertEnabled) {
            return;
        }
        Map temp = new HashMap();
        Iterator it = this._containers.values().iterator();
        while (it.hasNext()) {
            temp.putAll(buildComponentRefMap(it.next().globalElements().toArray(new SchemaComponent[0])));
        }
        assert this._globalElements.equals(temp);
        temp = new HashMap();
        it = this._containers.values().iterator();
        while (it.hasNext()) {
            temp.putAll(buildComponentRefMap(it.next().globalAttributes().toArray(new SchemaComponent[0])));
        }
        assert this._globalAttributes.equals(temp);
        temp = new HashMap();
        it = this._containers.values().iterator();
        while (it.hasNext()) {
            temp.putAll(buildComponentRefMap(it.next().modelGroups().toArray(new SchemaComponent[0])));
        }
        assert this._modelGroups.equals(temp);
        Set temp2 = new HashSet();
        Iterator it2 = this._containers.values().iterator();
        while (it2.hasNext()) {
            temp2.addAll(buildComponentRefList(it2.next().redefinedModelGroups().toArray(new SchemaComponent[0])));
        }
        assert new HashSet(this._redefinedModelGroups).equals(temp2);
        temp = new HashMap();
        it2 = this._containers.values().iterator();
        while (it2.hasNext()) {
            temp.putAll(buildComponentRefMap(it2.next().attributeGroups().toArray(new SchemaComponent[0])));
        }
        assert this._attributeGroups.equals(temp);
        temp2 = new HashSet();
        it2 = this._containers.values().iterator();
        while (it2.hasNext()) {
            temp2.addAll(buildComponentRefList(it2.next().redefinedAttributeGroups().toArray(new SchemaComponent[0])));
        }
        assert new HashSet(this._redefinedAttributeGroups).equals(temp2);
        temp = new HashMap();
        it2 = this._containers.values().iterator();
        while (it2.hasNext()) {
            temp.putAll(buildComponentRefMap(it2.next().globalTypes().toArray(new SchemaComponent[0])));
        }
        assert this._globalTypes.equals(temp);
        temp2 = new HashSet();
        it2 = this._containers.values().iterator();
        while (it2.hasNext()) {
            temp2.addAll(buildComponentRefList(it2.next().redefinedGlobalTypes().toArray(new SchemaComponent[0])));
        }
        assert new HashSet(this._redefinedGlobalTypes).equals(temp2);
        temp = new HashMap();
        it2 = this._containers.values().iterator();
        while (it2.hasNext()) {
            temp.putAll(buildDocumentMap(it2.next().documentTypes().toArray(new SchemaType[0])));
        }
        assert this._documentTypes.equals(temp);
        temp = new HashMap();
        it2 = this._containers.values().iterator();
        while (it2.hasNext()) {
            temp.putAll(buildAttributeTypeMap(it2.next().attributeTypes().toArray(new SchemaType[0])));
        }
        assert this._attributeTypes.equals(temp);
        temp = new HashMap();
        it2 = this._containers.values().iterator();
        while (it2.hasNext()) {
            temp.putAll(buildComponentRefMap(it2.next().identityConstraints().toArray(new SchemaComponent[0])));
        }
        assert this._identityConstraints.equals(temp);
        temp2 = new HashSet();
        it2 = this._containers.values().iterator();
        while (it2.hasNext()) {
            temp2.addAll(it2.next().annotations());
        }
        assert new HashSet(this._annotations).equals(temp2);
        temp2 = new HashSet();
        it2 = this._containers.values().iterator();
        while (it2.hasNext()) {
            temp2.add(it2.next().getNamespace());
        }
        assert this._namespaces.equals(temp2);
    }
    
    private static synchronized void nextBytes(final byte[] result) {
        if (SchemaTypeSystemImpl._random == null) {
            try {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final DataOutputStream daos = new DataOutputStream(baos);
                daos.writeInt(System.identityHashCode(SchemaTypeSystemImpl.class));
                final String[] props = { "user.name", "user.dir", "user.timezone", "user.country", "java.class.path", "java.home", "java.vendor", "java.version", "os.version" };
                for (int i = 0; i < props.length; ++i) {
                    final String prop = SystemProperties.getProperty(props[i]);
                    if (prop != null) {
                        daos.writeUTF(prop);
                        daos.writeInt(System.identityHashCode(prop));
                    }
                }
                daos.writeLong(Runtime.getRuntime().freeMemory());
                daos.close();
                final byte[] bytes = baos.toByteArray();
                for (int j = 0; j < bytes.length; ++j) {
                    final int k = j % SchemaTypeSystemImpl._mask.length;
                    final byte[] mask = SchemaTypeSystemImpl._mask;
                    final int n = k;
                    mask[n] *= 21;
                    final byte[] mask2 = SchemaTypeSystemImpl._mask;
                    final int n2 = k;
                    mask2[n2] += (byte)j;
                }
            }
            catch (final IOException e) {
                XBeanDebug.logException(e);
            }
            SchemaTypeSystemImpl._random = new Random(System.currentTimeMillis());
        }
        SchemaTypeSystemImpl._random.nextBytes(result);
        for (int l = 0; l < result.length; ++l) {
            final int m = l & SchemaTypeSystemImpl._mask.length;
            final int n3 = l;
            result[n3] ^= SchemaTypeSystemImpl._mask[m];
        }
    }
    
    public SchemaTypeSystemImpl(String nameForSystem) {
        this._incomplete = false;
        this._containers = new HashMap();
        this._identityConstraints = Collections.EMPTY_MAP;
        this._typeRefsByClassname = new HashMap();
        this._resolvedHandles = new HashMap();
        this._allNonGroupHandlesResolved = false;
        if (nameForSystem == null) {
            final byte[] bytes = new byte[16];
            nextBytes(bytes);
            nameForSystem = "s" + new String(HexBin.encode(bytes));
        }
        this._name = this.getMetadataPath().replace('/', '.') + ".system." + nameForSystem;
        this._basePackage = nameToPathString(this._name);
        this._classloader = null;
    }
    
    public void loadFromBuilder(final SchemaGlobalElement[] globalElements, final SchemaGlobalAttribute[] globalAttributes, final SchemaType[] globalTypes, final SchemaType[] documentTypes, final SchemaType[] attributeTypes) {
        assert this._classloader == null;
        this._localHandles = new HandlePool();
        this._globalElements = buildComponentRefMap(globalElements);
        this._globalAttributes = buildComponentRefMap(globalAttributes);
        this._globalTypes = buildComponentRefMap(globalTypes);
        this._documentTypes = buildDocumentMap(documentTypes);
        this._attributeTypes = buildAttributeTypeMap(attributeTypes);
        this._typeRefsByClassname = this.buildTypeRefsByClassname();
        this.buildContainers(Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        this._namespaces = new HashSet();
    }
    
    public void loadFromStscState(final StscState state) {
        assert this._classloader == null;
        this._localHandles = new HandlePool();
        this._globalElements = buildComponentRefMap(state.globalElements());
        this._globalAttributes = buildComponentRefMap(state.globalAttributes());
        this._modelGroups = buildComponentRefMap(state.modelGroups());
        this._redefinedModelGroups = buildComponentRefList(state.redefinedModelGroups());
        this._attributeGroups = buildComponentRefMap(state.attributeGroups());
        this._redefinedAttributeGroups = buildComponentRefList(state.redefinedAttributeGroups());
        this._globalTypes = buildComponentRefMap(state.globalTypes());
        this._redefinedGlobalTypes = buildComponentRefList(state.redefinedGlobalTypes());
        this._documentTypes = buildDocumentMap(state.documentTypes());
        this._attributeTypes = buildAttributeTypeMap(state.attributeTypes());
        this._typeRefsByClassname = this.buildTypeRefsByClassname(state.typesByClassname());
        this._identityConstraints = buildComponentRefMap(state.idConstraints());
        this._annotations = state.annotations();
        this._namespaces = new HashSet(Arrays.asList(state.getNamespaces()));
        this._containers = state.getContainerMap();
        this.fixupContainers();
        this.assertContainersSynchronized();
        this.setDependencies(state.getDependencies());
    }
    
    final SchemaTypeSystemImpl getTypeSystem() {
        return this;
    }
    
    void setDependencies(final SchemaDependencies deps) {
        this._deps = deps;
    }
    
    SchemaDependencies getDependencies() {
        return this._deps;
    }
    
    public boolean isIncomplete() {
        return this._incomplete;
    }
    
    void setIncomplete(final boolean incomplete) {
        this._incomplete = incomplete;
    }
    
    @Override
    public void saveToDirectory(final File classDir) {
        this.save(new FilerImpl(classDir, null, null, false, false));
    }
    
    @Override
    public void save(final Filer filer) {
        if (this._incomplete) {
            throw new IllegalStateException("Incomplete SchemaTypeSystems cannot be saved.");
        }
        if (filer == null) {
            throw new IllegalArgumentException("filer must not be null");
        }
        this._filer = filer;
        this._localHandles.startWriteMode();
        this.saveTypesRecursively(this.globalTypes());
        this.saveTypesRecursively(this.documentTypes());
        this.saveTypesRecursively(this.attributeTypes());
        this.saveGlobalElements(this.globalElements());
        this.saveGlobalAttributes(this.globalAttributes());
        this.saveModelGroups(this.modelGroups());
        this.saveAttributeGroups(this.attributeGroups());
        this.saveIdentityConstraints(this.identityConstraints());
        this.saveTypesRecursively(this.redefinedGlobalTypes());
        this.saveModelGroups(this.redefinedModelGroups());
        this.saveAttributeGroups(this.redefinedAttributeGroups());
        this.saveIndex();
        this.savePointers();
        this.saveLoader();
    }
    
    void saveTypesRecursively(final SchemaType[] types) {
        for (int i = 0; i < types.length; ++i) {
            if (types[i].getTypeSystem() == this.getTypeSystem()) {
                this.saveType(types[i]);
                this.saveTypesRecursively(types[i].getAnonymousTypes());
            }
        }
    }
    
    public void saveGlobalElements(final SchemaGlobalElement[] elts) {
        if (this._incomplete) {
            throw new IllegalStateException("This SchemaTypeSystem cannot be saved.");
        }
        for (int i = 0; i < elts.length; ++i) {
            this.saveGlobalElement(elts[i]);
        }
    }
    
    public void saveGlobalAttributes(final SchemaGlobalAttribute[] attrs) {
        if (this._incomplete) {
            throw new IllegalStateException("This SchemaTypeSystem cannot be saved.");
        }
        for (int i = 0; i < attrs.length; ++i) {
            this.saveGlobalAttribute(attrs[i]);
        }
    }
    
    public void saveModelGroups(final SchemaModelGroup[] groups) {
        if (this._incomplete) {
            throw new IllegalStateException("This SchemaTypeSystem cannot be saved.");
        }
        for (int i = 0; i < groups.length; ++i) {
            this.saveModelGroup(groups[i]);
        }
    }
    
    public void saveAttributeGroups(final SchemaAttributeGroup[] groups) {
        if (this._incomplete) {
            throw new IllegalStateException("This SchemaTypeSystem cannot be saved.");
        }
        for (int i = 0; i < groups.length; ++i) {
            this.saveAttributeGroup(groups[i]);
        }
    }
    
    public void saveIdentityConstraints(final SchemaIdentityConstraint[] idcs) {
        if (this._incomplete) {
            throw new IllegalStateException("This SchemaTypeSystem cannot be saved.");
        }
        for (int i = 0; i < idcs.length; ++i) {
            this.saveIdentityConstraint(idcs[i]);
        }
    }
    
    public void saveGlobalElement(final SchemaGlobalElement elt) {
        if (this._incomplete) {
            throw new IllegalStateException("This SchemaTypeSystem cannot be saved.");
        }
        final String handle = this._localHandles.handleForElement(elt);
        final XsbReader saver = new XsbReader(handle);
        saver.writeParticleData((SchemaParticle)elt);
        saver.writeString(elt.getSourceName());
        saver.writeRealHeader(handle, 3);
        saver.writeParticleData((SchemaParticle)elt);
        saver.writeString(elt.getSourceName());
        saver.writeEnd();
    }
    
    public void saveGlobalAttribute(final SchemaGlobalAttribute attr) {
        if (this._incomplete) {
            throw new IllegalStateException("This SchemaTypeSystem cannot be saved.");
        }
        final String handle = this._localHandles.handleForAttribute(attr);
        final XsbReader saver = new XsbReader(handle);
        saver.writeAttributeData(attr);
        saver.writeString(attr.getSourceName());
        saver.writeRealHeader(handle, 4);
        saver.writeAttributeData(attr);
        saver.writeString(attr.getSourceName());
        saver.writeEnd();
    }
    
    public void saveModelGroup(final SchemaModelGroup grp) {
        if (this._incomplete) {
            throw new IllegalStateException("This SchemaTypeSystem cannot be saved.");
        }
        final String handle = this._localHandles.handleForModelGroup(grp);
        final XsbReader saver = new XsbReader(handle);
        saver.writeModelGroupData(grp);
        saver.writeRealHeader(handle, 6);
        saver.writeModelGroupData(grp);
        saver.writeEnd();
    }
    
    public void saveAttributeGroup(final SchemaAttributeGroup grp) {
        if (this._incomplete) {
            throw new IllegalStateException("This SchemaTypeSystem cannot be saved.");
        }
        final String handle = this._localHandles.handleForAttributeGroup(grp);
        final XsbReader saver = new XsbReader(handle);
        saver.writeAttributeGroupData(grp);
        saver.writeRealHeader(handle, 7);
        saver.writeAttributeGroupData(grp);
        saver.writeEnd();
    }
    
    public void saveIdentityConstraint(final SchemaIdentityConstraint idc) {
        if (this._incomplete) {
            throw new IllegalStateException("This SchemaTypeSystem cannot be saved.");
        }
        final String handle = this._localHandles.handleForIdentityConstraint(idc);
        final XsbReader saver = new XsbReader(handle);
        saver.writeIdConstraintData(idc);
        saver.writeRealHeader(handle, 8);
        saver.writeIdConstraintData(idc);
        saver.writeEnd();
    }
    
    void saveType(final SchemaType type) {
        final String handle = this._localHandles.handleForType(type);
        final XsbReader saver = new XsbReader(handle);
        saver.writeTypeData(type);
        saver.writeRealHeader(handle, 2);
        saver.writeTypeData(type);
        saver.writeEnd();
    }
    
    public static String crackPointer(final InputStream stream) {
        DataInputStream input = null;
        try {
            input = new DataInputStream(stream);
            final int magic = input.readInt();
            if (magic != -629491010) {
                return null;
            }
            final int majorver = input.readShort();
            final int minorver = input.readShort();
            if (majorver != 2) {
                return null;
            }
            if (minorver > 24) {
                return null;
            }
            if (majorver > 2 || (majorver == 2 && minorver >= 18)) {
                input.readShort();
            }
            final int actualfiletype = input.readShort();
            if (actualfiletype != 5) {
                return null;
            }
            final StringPool stringPool = new StringPool("pointer", "unk");
            stringPool.readFrom(input);
            return stringPool.stringForCode(input.readShort());
        }
        catch (final IOException e) {
            return null;
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                }
                catch (final IOException ex) {}
            }
        }
    }
    
    @Override
    public SchemaType typeForHandle(final String handle) {
        synchronized (this._resolvedHandles) {
            return this._resolvedHandles.get(handle);
        }
    }
    
    @Override
    public SchemaType typeForClassname(final String classname) {
        final SchemaType.Ref ref = this._typeRefsByClassname.get(classname);
        return (ref != null) ? ref.get() : null;
    }
    
    @Override
    public SchemaComponent resolveHandle(final String handle) {
        SchemaComponent result;
        synchronized (this._resolvedHandles) {
            result = this._resolvedHandles.get(handle);
        }
        if (result == null) {
            final XsbReader reader = new XsbReader(handle, 65535);
            final int filetype = reader.getActualFiletype();
            switch (filetype) {
                case 2: {
                    XBeanDebug.trace(1, "Resolving type for handle " + handle, 0);
                    result = reader.finishLoadingType();
                    break;
                }
                case 3: {
                    XBeanDebug.trace(1, "Resolving element for handle " + handle, 0);
                    result = reader.finishLoadingElement();
                    break;
                }
                case 4: {
                    XBeanDebug.trace(1, "Resolving attribute for handle " + handle, 0);
                    result = reader.finishLoadingAttribute();
                    break;
                }
                case 6: {
                    XBeanDebug.trace(1, "Resolving model group for handle " + handle, 0);
                    result = reader.finishLoadingModelGroup();
                    break;
                }
                case 7: {
                    XBeanDebug.trace(1, "Resolving attribute group for handle " + handle, 0);
                    result = reader.finishLoadingAttributeGroup();
                    break;
                }
                case 8: {
                    XBeanDebug.trace(1, "Resolving id constraint for handle " + handle, 0);
                    result = reader.finishLoadingIdentityConstraint();
                    break;
                }
                default: {
                    throw new IllegalStateException("Illegal handle type");
                }
            }
            synchronized (this._resolvedHandles) {
                if (!this._resolvedHandles.containsKey(handle)) {
                    this._resolvedHandles.put(handle, result);
                }
                else {
                    result = this._resolvedHandles.get(handle);
                }
            }
        }
        return result;
    }
    
    @Override
    public void resolve() {
        XBeanDebug.trace(1, "Resolve called type system " + this._name, 0);
        if (this._allNonGroupHandlesResolved) {
            return;
        }
        XBeanDebug.trace(1, "Resolving all handles for type system " + this._name, 1);
        final List refs = new ArrayList();
        refs.addAll(this._globalElements.values());
        refs.addAll(this._globalAttributes.values());
        refs.addAll(this._globalTypes.values());
        refs.addAll(this._documentTypes.values());
        refs.addAll(this._attributeTypes.values());
        refs.addAll(this._identityConstraints.values());
        for (final SchemaComponent.Ref ref : refs) {
            ref.getComponent();
        }
        XBeanDebug.trace(1, "Finished resolving type system " + this._name, -1);
        this._allNonGroupHandlesResolved = true;
    }
    
    @Override
    public boolean isNamespaceDefined(final String namespace) {
        return this._namespaces.contains(namespace);
    }
    
    @Override
    public SchemaType.Ref findTypeRef(final QName name) {
        return this._globalTypes.get(name);
    }
    
    @Override
    public SchemaType.Ref findDocumentTypeRef(final QName name) {
        return this._documentTypes.get(name);
    }
    
    @Override
    public SchemaType.Ref findAttributeTypeRef(final QName name) {
        return this._attributeTypes.get(name);
    }
    
    @Override
    public SchemaGlobalElement.Ref findElementRef(final QName name) {
        return this._globalElements.get(name);
    }
    
    @Override
    public SchemaGlobalAttribute.Ref findAttributeRef(final QName name) {
        return this._globalAttributes.get(name);
    }
    
    @Override
    public SchemaModelGroup.Ref findModelGroupRef(final QName name) {
        return this._modelGroups.get(name);
    }
    
    @Override
    public SchemaAttributeGroup.Ref findAttributeGroupRef(final QName name) {
        return this._attributeGroups.get(name);
    }
    
    @Override
    public SchemaIdentityConstraint.Ref findIdentityConstraintRef(final QName name) {
        return this._identityConstraints.get(name);
    }
    
    @Override
    public SchemaType[] globalTypes() {
        if (this._globalTypes.isEmpty()) {
            return SchemaTypeSystemImpl.EMPTY_ST_ARRAY;
        }
        final SchemaType[] result = new SchemaType[this._globalTypes.size()];
        int j = 0;
        final Iterator i = this._globalTypes.values().iterator();
        while (i.hasNext()) {
            result[j] = i.next().get();
            ++j;
        }
        return result;
    }
    
    public SchemaType[] redefinedGlobalTypes() {
        if (this._redefinedGlobalTypes == null || this._redefinedGlobalTypes.isEmpty()) {
            return SchemaTypeSystemImpl.EMPTY_ST_ARRAY;
        }
        final SchemaType[] result = new SchemaType[this._redefinedGlobalTypes.size()];
        int j = 0;
        final Iterator i = this._redefinedGlobalTypes.iterator();
        while (i.hasNext()) {
            result[j] = i.next().get();
            ++j;
        }
        return result;
    }
    
    @Override
    public InputStream getSourceAsStream(String sourceName) {
        if (!sourceName.startsWith("/")) {
            sourceName = "/" + sourceName;
        }
        return this._resourceLoader.getResourceAsStream(this.getMetadataPath() + "/src" + sourceName);
    }
    
    SchemaContainer[] containers() {
        final SchemaContainer[] result = new SchemaContainer[this._containers.size()];
        int j = 0;
        final Iterator i = this._containers.values().iterator();
        while (i.hasNext()) {
            result[j] = i.next();
            ++j;
        }
        return result;
    }
    
    @Override
    public SchemaType[] documentTypes() {
        if (this._documentTypes.isEmpty()) {
            return SchemaTypeSystemImpl.EMPTY_ST_ARRAY;
        }
        final SchemaType[] result = new SchemaType[this._documentTypes.size()];
        int j = 0;
        final Iterator i = this._documentTypes.values().iterator();
        while (i.hasNext()) {
            result[j] = i.next().get();
            ++j;
        }
        return result;
    }
    
    @Override
    public SchemaType[] attributeTypes() {
        if (this._attributeTypes.isEmpty()) {
            return SchemaTypeSystemImpl.EMPTY_ST_ARRAY;
        }
        final SchemaType[] result = new SchemaType[this._attributeTypes.size()];
        int j = 0;
        final Iterator i = this._attributeTypes.values().iterator();
        while (i.hasNext()) {
            result[j] = i.next().get();
            ++j;
        }
        return result;
    }
    
    @Override
    public SchemaGlobalElement[] globalElements() {
        if (this._globalElements.isEmpty()) {
            return SchemaTypeSystemImpl.EMPTY_GE_ARRAY;
        }
        final SchemaGlobalElement[] result = new SchemaGlobalElement[this._globalElements.size()];
        int j = 0;
        final Iterator i = this._globalElements.values().iterator();
        while (i.hasNext()) {
            result[j] = i.next().get();
            ++j;
        }
        return result;
    }
    
    @Override
    public SchemaGlobalAttribute[] globalAttributes() {
        if (this._globalAttributes.isEmpty()) {
            return SchemaTypeSystemImpl.EMPTY_GA_ARRAY;
        }
        final SchemaGlobalAttribute[] result = new SchemaGlobalAttribute[this._globalAttributes.size()];
        int j = 0;
        final Iterator i = this._globalAttributes.values().iterator();
        while (i.hasNext()) {
            result[j] = i.next().get();
            ++j;
        }
        return result;
    }
    
    @Override
    public SchemaModelGroup[] modelGroups() {
        if (this._modelGroups.isEmpty()) {
            return SchemaTypeSystemImpl.EMPTY_MG_ARRAY;
        }
        final SchemaModelGroup[] result = new SchemaModelGroup[this._modelGroups.size()];
        int j = 0;
        final Iterator i = this._modelGroups.values().iterator();
        while (i.hasNext()) {
            result[j] = i.next().get();
            ++j;
        }
        return result;
    }
    
    public SchemaModelGroup[] redefinedModelGroups() {
        if (this._redefinedModelGroups == null || this._redefinedModelGroups.isEmpty()) {
            return SchemaTypeSystemImpl.EMPTY_MG_ARRAY;
        }
        final SchemaModelGroup[] result = new SchemaModelGroup[this._redefinedModelGroups.size()];
        int j = 0;
        final Iterator i = this._redefinedModelGroups.iterator();
        while (i.hasNext()) {
            result[j] = i.next().get();
            ++j;
        }
        return result;
    }
    
    @Override
    public SchemaAttributeGroup[] attributeGroups() {
        if (this._attributeGroups.isEmpty()) {
            return SchemaTypeSystemImpl.EMPTY_AG_ARRAY;
        }
        final SchemaAttributeGroup[] result = new SchemaAttributeGroup[this._attributeGroups.size()];
        int j = 0;
        final Iterator i = this._attributeGroups.values().iterator();
        while (i.hasNext()) {
            result[j] = i.next().get();
            ++j;
        }
        return result;
    }
    
    public SchemaAttributeGroup[] redefinedAttributeGroups() {
        if (this._redefinedAttributeGroups == null || this._redefinedAttributeGroups.isEmpty()) {
            return SchemaTypeSystemImpl.EMPTY_AG_ARRAY;
        }
        final SchemaAttributeGroup[] result = new SchemaAttributeGroup[this._redefinedAttributeGroups.size()];
        int j = 0;
        final Iterator i = this._redefinedAttributeGroups.iterator();
        while (i.hasNext()) {
            result[j] = i.next().get();
            ++j;
        }
        return result;
    }
    
    @Override
    public SchemaAnnotation[] annotations() {
        if (this._annotations == null || this._annotations.isEmpty()) {
            return SchemaTypeSystemImpl.EMPTY_ANN_ARRAY;
        }
        SchemaAnnotation[] result = new SchemaAnnotation[this._annotations.size()];
        result = this._annotations.toArray(result);
        return result;
    }
    
    public SchemaIdentityConstraint[] identityConstraints() {
        if (this._identityConstraints.isEmpty()) {
            return SchemaTypeSystemImpl.EMPTY_IC_ARRAY;
        }
        final SchemaIdentityConstraint[] result = new SchemaIdentityConstraint[this._identityConstraints.size()];
        int j = 0;
        final Iterator i = this._identityConstraints.values().iterator();
        while (i.hasNext()) {
            result[j] = i.next().get();
            ++j;
        }
        return result;
    }
    
    @Override
    public ClassLoader getClassLoader() {
        return this._classloader;
    }
    
    public String handleForType(final SchemaType type) {
        return this._localHandles.handleForType(type);
    }
    
    @Override
    public String getName() {
        return this._name;
    }
    
    public SchemaTypeSystem typeSystemForName(final String name) {
        if (this._name != null && name.equals(this._name)) {
            return this;
        }
        return null;
    }
    
    protected String getMetadataPath() {
        return "schema" + SchemaTypeSystemImpl.METADATA_PACKAGE_GEN;
    }
    
    static {
        final Package stsPackage = SchemaTypeSystem.class.getPackage();
        final String stsPackageName = (stsPackage == null) ? SchemaTypeSystem.class.getName().substring(0, SchemaTypeSystem.class.getName().lastIndexOf(".")) : stsPackage.getName();
        SchemaTypeSystemImpl.METADATA_PACKAGE_GEN = stsPackageName.replaceAll("\\.", "_");
        HOLDER_TEMPLATE_NAMES = makeClassStrings("org.apache.xmlbeans.impl.schema.TypeSystemHolder");
        SchemaTypeSystemImpl._mask = new byte[16];
        EMPTY_ST_ARRAY = new SchemaType[0];
        EMPTY_GE_ARRAY = new SchemaGlobalElement[0];
        EMPTY_GA_ARRAY = new SchemaGlobalAttribute[0];
        EMPTY_MG_ARRAY = new SchemaModelGroup[0];
        EMPTY_AG_ARRAY = new SchemaAttributeGroup[0];
        EMPTY_IC_ARRAY = new SchemaIdentityConstraint[0];
        EMPTY_ANN_ARRAY = new SchemaAnnotation[0];
        SINGLE_ZERO_BYTE = new byte[] { 0 };
    }
    
    static class StringPool
    {
        private List intsToStrings;
        private Map stringsToInts;
        private String _handle;
        private String _name;
        
        StringPool(final String handle, final String name) {
            this.intsToStrings = new ArrayList();
            this.stringsToInts = new HashMap();
            this._handle = handle;
            this._name = name;
            this.intsToStrings.add(null);
        }
        
        int codeForString(final String str) {
            if (str == null) {
                return 0;
            }
            Integer result = this.stringsToInts.get(str);
            if (result == null) {
                result = new Integer(this.intsToStrings.size());
                this.intsToStrings.add(str);
                this.stringsToInts.put(str, result);
            }
            return result;
        }
        
        String stringForCode(final int code) {
            if (code == 0) {
                return null;
            }
            return this.intsToStrings.get(code);
        }
        
        void writeTo(final DataOutputStream output) {
            if (this.intsToStrings.size() >= 65535) {
                throw new SchemaTypeLoaderException("Too many strings (" + this.intsToStrings.size() + ")", this._name, this._handle, 10);
            }
            try {
                output.writeShort(this.intsToStrings.size());
                final Iterator i = this.intsToStrings.iterator();
                i.next();
                while (i.hasNext()) {
                    final String str = i.next();
                    output.writeUTF(str);
                }
            }
            catch (final IOException e) {
                throw new SchemaTypeLoaderException(e.getMessage(), this._name, this._handle, 9);
            }
        }
        
        void readFrom(final DataInputStream input) {
            if (this.intsToStrings.size() != 1 || this.stringsToInts.size() != 0) {
                throw new IllegalStateException();
            }
            try {
                for (int size = input.readUnsignedShort(), i = 1; i < size; ++i) {
                    final String str = input.readUTF().intern();
                    final int code = this.codeForString(str);
                    if (code != i) {
                        throw new IllegalStateException();
                    }
                }
            }
            catch (final IOException e) {
                throw new SchemaTypeLoaderException((e.getMessage() == null) ? e.getMessage() : "IO Exception", this._name, this._handle, 9, e);
            }
        }
    }
    
    class HandlePool
    {
        private Map _handlesToRefs;
        private Map _componentsToHandles;
        private boolean _started;
        
        HandlePool() {
            this._handlesToRefs = new LinkedHashMap();
            this._componentsToHandles = new LinkedHashMap();
        }
        
        private String addUniqueHandle(final SchemaComponent obj, String base) {
            String handle;
            base = (handle = base.toLowerCase());
            for (int index = 2; this._handlesToRefs.containsKey(handle); handle = base + index, ++index) {}
            this._handlesToRefs.put(handle, obj.getComponentRef());
            this._componentsToHandles.put(obj, handle);
            return handle;
        }
        
        String handleForComponent(final SchemaComponent comp) {
            if (comp == null) {
                return null;
            }
            if (comp.getTypeSystem() != SchemaTypeSystemImpl.this.getTypeSystem()) {
                throw new IllegalArgumentException("Cannot supply handles for types from another type system");
            }
            if (comp instanceof SchemaType) {
                return this.handleForType((SchemaType)comp);
            }
            if (comp instanceof SchemaGlobalElement) {
                return this.handleForElement((SchemaGlobalElement)comp);
            }
            if (comp instanceof SchemaGlobalAttribute) {
                return this.handleForAttribute((SchemaGlobalAttribute)comp);
            }
            if (comp instanceof SchemaModelGroup) {
                return this.handleForModelGroup((SchemaModelGroup)comp);
            }
            if (comp instanceof SchemaAttributeGroup) {
                return this.handleForAttributeGroup((SchemaAttributeGroup)comp);
            }
            if (comp instanceof SchemaIdentityConstraint) {
                return this.handleForIdentityConstraint((SchemaIdentityConstraint)comp);
            }
            throw new IllegalStateException("Component type cannot have a handle");
        }
        
        String handleForElement(final SchemaGlobalElement element) {
            if (element == null) {
                return null;
            }
            if (element.getTypeSystem() != SchemaTypeSystemImpl.this.getTypeSystem()) {
                throw new IllegalArgumentException("Cannot supply handles for types from another type system");
            }
            String handle = this._componentsToHandles.get(element);
            if (handle == null) {
                handle = this.addUniqueHandle(element, NameUtil.upperCamelCase(element.getName().getLocalPart()) + "Element");
            }
            return handle;
        }
        
        String handleForAttribute(final SchemaGlobalAttribute attribute) {
            if (attribute == null) {
                return null;
            }
            if (attribute.getTypeSystem() != SchemaTypeSystemImpl.this.getTypeSystem()) {
                throw new IllegalArgumentException("Cannot supply handles for types from another type system");
            }
            String handle = this._componentsToHandles.get(attribute);
            if (handle == null) {
                handle = this.addUniqueHandle(attribute, NameUtil.upperCamelCase(attribute.getName().getLocalPart()) + "Attribute");
            }
            return handle;
        }
        
        String handleForModelGroup(final SchemaModelGroup group) {
            if (group == null) {
                return null;
            }
            if (group.getTypeSystem() != SchemaTypeSystemImpl.this.getTypeSystem()) {
                throw new IllegalArgumentException("Cannot supply handles for types from another type system");
            }
            String handle = this._componentsToHandles.get(group);
            if (handle == null) {
                handle = this.addUniqueHandle(group, NameUtil.upperCamelCase(group.getName().getLocalPart()) + "ModelGroup");
            }
            return handle;
        }
        
        String handleForAttributeGroup(final SchemaAttributeGroup group) {
            if (group == null) {
                return null;
            }
            if (group.getTypeSystem() != SchemaTypeSystemImpl.this.getTypeSystem()) {
                throw new IllegalArgumentException("Cannot supply handles for types from another type system");
            }
            String handle = this._componentsToHandles.get(group);
            if (handle == null) {
                handle = this.addUniqueHandle(group, NameUtil.upperCamelCase(group.getName().getLocalPart()) + "AttributeGroup");
            }
            return handle;
        }
        
        String handleForIdentityConstraint(final SchemaIdentityConstraint idc) {
            if (idc == null) {
                return null;
            }
            if (idc.getTypeSystem() != SchemaTypeSystemImpl.this.getTypeSystem()) {
                throw new IllegalArgumentException("Cannot supply handles for types from another type system");
            }
            String handle = this._componentsToHandles.get(idc);
            if (handle == null) {
                handle = this.addUniqueHandle(idc, NameUtil.upperCamelCase(idc.getName().getLocalPart()) + "IdentityConstraint");
            }
            return handle;
        }
        
        String handleForType(final SchemaType type) {
            if (type == null) {
                return null;
            }
            if (type.getTypeSystem() != SchemaTypeSystemImpl.this.getTypeSystem()) {
                throw new IllegalArgumentException("Cannot supply handles for types from another type system");
            }
            String handle = this._componentsToHandles.get(type);
            if (handle == null) {
                QName name = type.getName();
                String suffix = "";
                if (name == null) {
                    if (type.isDocumentType()) {
                        name = type.getDocumentElementName();
                        suffix = "Doc";
                    }
                    else if (type.isAttributeType()) {
                        name = type.getAttributeTypeAttributeName();
                        suffix = "AttrType";
                    }
                    else if (type.getContainerField() != null) {
                        name = type.getContainerField().getName();
                        suffix = (type.getContainerField().isAttribute() ? "Attr" : "Elem");
                    }
                }
                final String uniq = Integer.toHexString(type.toString().hashCode() | Integer.MIN_VALUE).substring(4).toUpperCase();
                String baseName;
                if (name == null) {
                    baseName = "Anon" + uniq + "Type";
                }
                else {
                    baseName = NameUtil.upperCamelCase(name.getLocalPart()) + uniq + suffix + "Type";
                }
                handle = this.addUniqueHandle(type, baseName);
            }
            return handle;
        }
        
        SchemaComponent.Ref refForHandle(final String handle) {
            if (handle == null) {
                return null;
            }
            return this._handlesToRefs.get(handle);
        }
        
        Set getAllHandles() {
            return this._handlesToRefs.keySet();
        }
        
        void startWriteMode() {
            this._started = true;
            this._componentsToHandles = new LinkedHashMap();
            for (final String handle : this._handlesToRefs.keySet()) {
                final SchemaComponent comp = this._handlesToRefs.get(handle).getComponent();
                this._componentsToHandles.put(comp, handle);
            }
        }
    }
    
    private class XsbReader
    {
        DataInputStream _input;
        DataOutputStream _output;
        StringPool _stringPool;
        String _handle;
        private int _majorver;
        private int _minorver;
        private int _releaseno;
        int _actualfiletype;
        
        public XsbReader(final String handle, final int filetype) {
            final String resourcename = SchemaTypeSystemImpl.this._basePackage + handle + ".xsb";
            final InputStream rawinput = this.getLoaderStream(resourcename);
            if (rawinput == null) {
                throw new SchemaTypeLoaderException("XML-BEANS compiled schema: Could not locate compiled schema resource " + resourcename, SchemaTypeSystemImpl.this._name, handle, 0);
            }
            this._input = new DataInputStream(rawinput);
            this._handle = handle;
            final int magic = this.readInt();
            if (magic != -629491010) {
                throw new SchemaTypeLoaderException("XML-BEANS compiled schema: Wrong magic cookie", SchemaTypeSystemImpl.this._name, handle, 1);
            }
            this._majorver = this.readShort();
            this._minorver = this.readShort();
            if (this._majorver != 2) {
                throw new SchemaTypeLoaderException("XML-BEANS compiled schema: Wrong major version - expecting 2, got " + this._majorver, SchemaTypeSystemImpl.this._name, handle, 2);
            }
            if (this._minorver > 24) {
                throw new SchemaTypeLoaderException("XML-BEANS compiled schema: Incompatible minor version - expecting up to 24, got " + this._minorver, SchemaTypeSystemImpl.this._name, handle, 3);
            }
            if (this._minorver < 14) {
                throw new SchemaTypeLoaderException("XML-BEANS compiled schema: Incompatible minor version - expecting at least 14, got " + this._minorver, SchemaTypeSystemImpl.this._name, handle, 3);
            }
            if (this.atLeast(2, 18, 0)) {
                this._releaseno = this.readShort();
            }
            final int actualfiletype = this.readShort();
            if (actualfiletype != filetype && filetype != 65535) {
                throw new SchemaTypeLoaderException("XML-BEANS compiled schema: File has the wrong type - expecting type " + filetype + ", got type " + actualfiletype, SchemaTypeSystemImpl.this._name, handle, 4);
            }
            (this._stringPool = new StringPool(this._handle, SchemaTypeSystemImpl.this._name)).readFrom(this._input);
            this._actualfiletype = actualfiletype;
        }
        
        protected boolean atLeast(final int majorver, final int minorver, final int releaseno) {
            return this._majorver > majorver || (this._majorver >= majorver && (this._minorver > minorver || (this._minorver >= minorver && this._releaseno >= releaseno)));
        }
        
        protected boolean atMost(final int majorver, final int minorver, final int releaseno) {
            return this._majorver <= majorver && (this._majorver < majorver || (this._minorver <= minorver && (this._minorver < minorver || this._releaseno <= releaseno)));
        }
        
        int getActualFiletype() {
            return this._actualfiletype;
        }
        
        XsbReader(final String handle) {
            this._handle = handle;
            this._stringPool = new StringPool(this._handle, SchemaTypeSystemImpl.this._name);
        }
        
        void writeRealHeader(final String handle, final int filetype) {
            String resourcename;
            if (handle.indexOf(47) >= 0) {
                resourcename = handle + ".xsb";
            }
            else {
                resourcename = SchemaTypeSystemImpl.this._basePackage + handle + ".xsb";
            }
            final OutputStream rawoutput = this.getSaverStream(resourcename);
            if (rawoutput == null) {
                throw new SchemaTypeLoaderException("Could not write compiled schema resource " + resourcename, SchemaTypeSystemImpl.this._name, handle, 12);
            }
            this._output = new DataOutputStream(rawoutput);
            this._handle = handle;
            this.writeInt(-629491010);
            this.writeShort(2);
            this.writeShort(24);
            this.writeShort(0);
            this.writeShort(filetype);
            this._stringPool.writeTo(this._output);
        }
        
        void readEnd() {
            try {
                if (this._input != null) {
                    this._input.close();
                }
            }
            catch (final IOException ex) {}
            this._input = null;
            this._stringPool = null;
            this._handle = null;
        }
        
        void writeEnd() {
            try {
                if (this._output != null) {
                    this._output.flush();
                    this._output.close();
                }
            }
            catch (final IOException e) {
                throw new SchemaTypeLoaderException(e.getMessage(), SchemaTypeSystemImpl.this._name, this._handle, 9);
            }
            this._output = null;
            this._stringPool = null;
            this._handle = null;
        }
        
        int fileTypeFromComponentType(final int componentType) {
            switch (componentType) {
                case 0: {
                    return 2;
                }
                case 1: {
                    return 3;
                }
                case 3: {
                    return 4;
                }
                case 6: {
                    return 6;
                }
                case 4: {
                    return 7;
                }
                case 5: {
                    return 8;
                }
                default: {
                    throw new IllegalStateException("Unexpected component type");
                }
            }
        }
        
        void writeIndexData() {
            this.writeHandlePool(SchemaTypeSystemImpl.this._localHandles);
            this.writeQNameMap(SchemaTypeSystemImpl.this.globalElements());
            this.writeQNameMap(SchemaTypeSystemImpl.this.globalAttributes());
            this.writeQNameMap(SchemaTypeSystemImpl.this.modelGroups());
            this.writeQNameMap(SchemaTypeSystemImpl.this.attributeGroups());
            this.writeQNameMap(SchemaTypeSystemImpl.this.identityConstraints());
            this.writeQNameMap(SchemaTypeSystemImpl.this.globalTypes());
            this.writeDocumentTypeMap(SchemaTypeSystemImpl.this.documentTypes());
            this.writeAttributeTypeMap(SchemaTypeSystemImpl.this.attributeTypes());
            this.writeClassnameMap(SchemaTypeSystemImpl.this._typeRefsByClassname);
            this.writeNamespaces(SchemaTypeSystemImpl.this._namespaces);
            this.writeQNameMap(SchemaTypeSystemImpl.this.redefinedGlobalTypes());
            this.writeQNameMap(SchemaTypeSystemImpl.this.redefinedModelGroups());
            this.writeQNameMap(SchemaTypeSystemImpl.this.redefinedAttributeGroups());
            this.writeAnnotations(SchemaTypeSystemImpl.this.annotations());
        }
        
        void writeHandlePool(final HandlePool pool) {
            this.writeShort(pool._componentsToHandles.size());
            for (final SchemaComponent comp : pool._componentsToHandles.keySet()) {
                final String handle = pool._componentsToHandles.get(comp);
                final int code = this.fileTypeFromComponentType(comp.getComponentType());
                this.writeString(handle);
                this.writeShort(code);
            }
        }
        
        void readHandlePool(final HandlePool pool) {
            if (pool._handlesToRefs.size() != 0 || pool._started) {
                throw new IllegalStateException("Nonempty handle set before read");
            }
            for (int size = this.readShort(), i = 0; i < size; ++i) {
                final String handle = this.readString();
                final int code = this.readShort();
                Object result = null;
                switch (code) {
                    case 2: {
                        result = new SchemaType.Ref(SchemaTypeSystemImpl.this.getTypeSystem(), handle);
                        break;
                    }
                    case 3: {
                        result = new SchemaGlobalElement.Ref(SchemaTypeSystemImpl.this.getTypeSystem(), handle);
                        break;
                    }
                    case 4: {
                        result = new SchemaGlobalAttribute.Ref(SchemaTypeSystemImpl.this.getTypeSystem(), handle);
                        break;
                    }
                    case 6: {
                        result = new SchemaModelGroup.Ref(SchemaTypeSystemImpl.this.getTypeSystem(), handle);
                        break;
                    }
                    case 7: {
                        result = new SchemaAttributeGroup.Ref(SchemaTypeSystemImpl.this.getTypeSystem(), handle);
                        break;
                    }
                    case 8: {
                        result = new SchemaIdentityConstraint.Ref(SchemaTypeSystemImpl.this.getTypeSystem(), handle);
                        break;
                    }
                    default: {
                        throw new SchemaTypeLoaderException("Schema index has an unrecognized entry of type " + code, SchemaTypeSystemImpl.this._name, handle, 5);
                    }
                }
                pool._handlesToRefs.put(handle, result);
            }
        }
        
        int readShort() {
            try {
                return this._input.readUnsignedShort();
            }
            catch (final IOException e) {
                throw new SchemaTypeLoaderException(e.getMessage(), SchemaTypeSystemImpl.this._name, this._handle, 9);
            }
        }
        
        void writeShort(final int s) {
            if (s >= 65535 || s < -1) {
                throw new SchemaTypeLoaderException("Value " + s + " out of range: must fit in a 16-bit unsigned short.", SchemaTypeSystemImpl.this._name, this._handle, 10);
            }
            if (this._output != null) {
                try {
                    this._output.writeShort(s);
                }
                catch (final IOException e) {
                    throw new SchemaTypeLoaderException(e.getMessage(), SchemaTypeSystemImpl.this._name, this._handle, 9);
                }
            }
        }
        
        int readInt() {
            try {
                return this._input.readInt();
            }
            catch (final IOException e) {
                throw new SchemaTypeLoaderException(e.getMessage(), SchemaTypeSystemImpl.this._name, this._handle, 9);
            }
        }
        
        void writeInt(final int i) {
            if (this._output != null) {
                try {
                    this._output.writeInt(i);
                }
                catch (final IOException e) {
                    throw new SchemaTypeLoaderException(e.getMessage(), SchemaTypeSystemImpl.this._name, this._handle, 9);
                }
            }
        }
        
        String readString() {
            return this._stringPool.stringForCode(this.readShort());
        }
        
        void writeString(final String str) {
            final int code = this._stringPool.codeForString(str);
            this.writeShort(code);
        }
        
        QName readQName() {
            final String namespace = this.readString();
            final String localname = this.readString();
            if (localname == null) {
                return null;
            }
            return new QName(namespace, localname);
        }
        
        void writeQName(final QName qname) {
            if (qname == null) {
                this.writeString(null);
                this.writeString(null);
                return;
            }
            this.writeString(qname.getNamespaceURI());
            this.writeString(qname.getLocalPart());
        }
        
        SOAPArrayType readSOAPArrayType() {
            final QName qName = this.readQName();
            final String dimensions = this.readString();
            if (qName == null) {
                return null;
            }
            return new SOAPArrayType(qName, dimensions);
        }
        
        void writeSOAPArrayType(final SOAPArrayType arrayType) {
            if (arrayType == null) {
                this.writeQName(null);
                this.writeString(null);
            }
            else {
                this.writeQName(arrayType.getQName());
                this.writeString(arrayType.soap11DimensionString());
            }
        }
        
        void writeAnnotation(final SchemaAnnotation a) {
            if (a == null) {
                this.writeInt(-1);
                return;
            }
            final SchemaAnnotation.Attribute[] attributes = a.getAttributes();
            this.writeInt(attributes.length);
            for (int i = 0; i < attributes.length; ++i) {
                final QName name = attributes[i].getName();
                final String value = attributes[i].getValue();
                final String valueURI = attributes[i].getValueUri();
                this.writeQName(name);
                this.writeString(value);
                this.writeString(valueURI);
            }
            final XmlObject[] documentationItems = a.getUserInformation();
            this.writeInt(documentationItems.length);
            final XmlOptions opt = new XmlOptions().setSaveOuter().setSaveAggressiveNamespaces();
            for (int j = 0; j < documentationItems.length; ++j) {
                final XmlObject doc = documentationItems[j];
                this.writeString(doc.xmlText(opt));
            }
            final XmlObject[] appInfoItems = a.getApplicationInformation();
            this.writeInt(appInfoItems.length);
            for (int k = 0; k < appInfoItems.length; ++k) {
                final XmlObject doc2 = appInfoItems[k];
                this.writeString(doc2.xmlText(opt));
            }
        }
        
        SchemaAnnotation readAnnotation(final SchemaContainer c) {
            if (!this.atLeast(2, 19, 0)) {
                return null;
            }
            int n = this.readInt();
            if (n == -1) {
                return null;
            }
            final SchemaAnnotation.Attribute[] attributes = new SchemaAnnotation.Attribute[n];
            for (int i = 0; i < n; ++i) {
                final QName name = this.readQName();
                final String value = this.readString();
                String valueUri = null;
                if (this.atLeast(2, 24, 0)) {
                    valueUri = this.readString();
                }
                attributes[i] = new SchemaAnnotationImpl.AttributeImpl(name, value, valueUri);
            }
            n = this.readInt();
            final String[] docStrings = new String[n];
            for (int j = 0; j < n; ++j) {
                docStrings[j] = this.readString();
            }
            n = this.readInt();
            final String[] appInfoStrings = new String[n];
            for (int k = 0; k < n; ++k) {
                appInfoStrings[k] = this.readString();
            }
            return new SchemaAnnotationImpl(c, appInfoStrings, docStrings, attributes);
        }
        
        void writeAnnotations(final SchemaAnnotation[] anns) {
            this.writeInt(anns.length);
            for (int i = 0; i < anns.length; ++i) {
                this.writeAnnotation(anns[i]);
            }
        }
        
        List readAnnotations() {
            final int n = this.readInt();
            final List result = new ArrayList(n);
            final SchemaContainer container = SchemaTypeSystemImpl.this.getContainerNonNull("");
            for (int i = 0; i < n; ++i) {
                result.add(this.readAnnotation(container));
            }
            return result;
        }
        
        SchemaComponent.Ref readHandle() {
            final String handle = this.readString();
            if (handle == null) {
                return null;
            }
            if (handle.charAt(0) != '_') {
                return SchemaTypeSystemImpl.this._localHandles.refForHandle(handle);
            }
            switch (handle.charAt(2)) {
                case 'I': {
                    SchemaType st = (SchemaType)BuiltinSchemaTypeSystem.get().resolveHandle(handle);
                    if (st != null) {
                        return st.getRef();
                    }
                    st = (SchemaType)XQuerySchemaTypeSystem.get().resolveHandle(handle);
                    return st.getRef();
                }
                case 'T': {
                    return SchemaTypeSystemImpl.this._linker.findTypeRef(QNameHelper.forPretty(handle, 4));
                }
                case 'E': {
                    return SchemaTypeSystemImpl.this._linker.findElementRef(QNameHelper.forPretty(handle, 4));
                }
                case 'A': {
                    return SchemaTypeSystemImpl.this._linker.findAttributeRef(QNameHelper.forPretty(handle, 4));
                }
                case 'M': {
                    return SchemaTypeSystemImpl.this._linker.findModelGroupRef(QNameHelper.forPretty(handle, 4));
                }
                case 'N': {
                    return SchemaTypeSystemImpl.this._linker.findAttributeGroupRef(QNameHelper.forPretty(handle, 4));
                }
                case 'D': {
                    return SchemaTypeSystemImpl.this._linker.findIdentityConstraintRef(QNameHelper.forPretty(handle, 4));
                }
                case 'R': {
                    final SchemaGlobalAttribute attr = SchemaTypeSystemImpl.this._linker.findAttribute(QNameHelper.forPretty(handle, 4));
                    if (attr == null) {
                        throw new SchemaTypeLoaderException("Cannot resolve attribute for handle " + handle, SchemaTypeSystemImpl.this._name, this._handle, 13);
                    }
                    return attr.getType().getRef();
                }
                case 'S': {
                    final SchemaGlobalElement elem = SchemaTypeSystemImpl.this._linker.findElement(QNameHelper.forPretty(handle, 4));
                    if (elem == null) {
                        throw new SchemaTypeLoaderException("Cannot resolve element for handle " + handle, SchemaTypeSystemImpl.this._name, this._handle, 13);
                    }
                    return elem.getType().getRef();
                }
                case 'O': {
                    return SchemaTypeSystemImpl.this._linker.findDocumentTypeRef(QNameHelper.forPretty(handle, 4));
                }
                case 'Y': {
                    final SchemaType type = SchemaTypeSystemImpl.this._linker.typeForSignature(handle.substring(4));
                    if (type == null) {
                        throw new SchemaTypeLoaderException("Cannot resolve type for handle " + handle, SchemaTypeSystemImpl.this._name, this._handle, 13);
                    }
                    return type.getRef();
                }
                default: {
                    throw new SchemaTypeLoaderException("Cannot resolve handle " + handle, SchemaTypeSystemImpl.this._name, this._handle, 13);
                }
            }
        }
        
        void writeHandle(final SchemaComponent comp) {
            if (comp == null || comp.getTypeSystem() == SchemaTypeSystemImpl.this.getTypeSystem()) {
                this.writeString(SchemaTypeSystemImpl.this._localHandles.handleForComponent(comp));
                return;
            }
            switch (comp.getComponentType()) {
                case 3: {
                    this.writeString("_XA_" + QNameHelper.pretty(comp.getName()));
                    return;
                }
                case 6: {
                    this.writeString("_XM_" + QNameHelper.pretty(comp.getName()));
                    return;
                }
                case 4: {
                    this.writeString("_XN_" + QNameHelper.pretty(comp.getName()));
                    return;
                }
                case 1: {
                    this.writeString("_XE_" + QNameHelper.pretty(comp.getName()));
                    return;
                }
                case 5: {
                    this.writeString("_XD_" + QNameHelper.pretty(comp.getName()));
                    return;
                }
                case 0: {
                    final SchemaType type = (SchemaType)comp;
                    if (type.isBuiltinType()) {
                        this.writeString("_BI_" + type.getName().getLocalPart());
                        return;
                    }
                    if (type.getName() != null) {
                        this.writeString("_XT_" + QNameHelper.pretty(type.getName()));
                    }
                    else if (type.isDocumentType()) {
                        this.writeString("_XO_" + QNameHelper.pretty(type.getDocumentElementName()));
                    }
                    else {
                        this.writeString("_XY_" + type.toString());
                    }
                    return;
                }
                default: {
                    assert false;
                    throw new SchemaTypeLoaderException("Cannot write handle for component " + comp, SchemaTypeSystemImpl.this._name, this._handle, 13);
                }
            }
        }
        
        SchemaType.Ref readTypeRef() {
            return (SchemaType.Ref)this.readHandle();
        }
        
        void writeType(final SchemaType type) {
            this.writeHandle(type);
        }
        
        Map readQNameRefMap() {
            final Map result = new HashMap();
            for (int size = this.readShort(), i = 0; i < size; ++i) {
                final QName name = this.readQName();
                final SchemaComponent.Ref obj = this.readHandle();
                result.put(name, obj);
            }
            return result;
        }
        
        List readQNameRefMapAsList(final List names) {
            final int size = this.readShort();
            final List result = new ArrayList(size);
            for (int i = 0; i < size; ++i) {
                final QName name = this.readQName();
                final SchemaComponent.Ref obj = this.readHandle();
                result.add(obj);
                names.add(name);
            }
            return result;
        }
        
        void writeQNameMap(final SchemaComponent[] components) {
            this.writeShort(components.length);
            for (int i = 0; i < components.length; ++i) {
                this.writeQName(components[i].getName());
                this.writeHandle(components[i]);
            }
        }
        
        void writeDocumentTypeMap(final SchemaType[] doctypes) {
            this.writeShort(doctypes.length);
            for (int i = 0; i < doctypes.length; ++i) {
                this.writeQName(doctypes[i].getDocumentElementName());
                this.writeHandle(doctypes[i]);
            }
        }
        
        void writeAttributeTypeMap(final SchemaType[] attrtypes) {
            this.writeShort(attrtypes.length);
            for (int i = 0; i < attrtypes.length; ++i) {
                this.writeQName(attrtypes[i].getAttributeTypeAttributeName());
                this.writeHandle(attrtypes[i]);
            }
        }
        
        SchemaType.Ref[] readTypeRefArray() {
            final int size = this.readShort();
            final SchemaType.Ref[] result = new SchemaType.Ref[size];
            for (int i = 0; i < size; ++i) {
                result[i] = this.readTypeRef();
            }
            return result;
        }
        
        void writeTypeArray(final SchemaType[] array) {
            this.writeShort(array.length);
            for (int i = 0; i < array.length; ++i) {
                this.writeHandle(array[i]);
            }
        }
        
        Map readClassnameRefMap() {
            final Map result = new HashMap();
            for (int size = this.readShort(), i = 0; i < size; ++i) {
                final String name = this.readString();
                final SchemaComponent.Ref obj = this.readHandle();
                result.put(name, obj);
            }
            return result;
        }
        
        void writeClassnameMap(final Map typesByClass) {
            this.writeShort(typesByClass.size());
            for (final String className : typesByClass.keySet()) {
                this.writeString(className);
                this.writeHandle(typesByClass.get(className).get());
            }
        }
        
        Set readNamespaces() {
            final Set result = new HashSet();
            for (int size = this.readShort(), i = 0; i < size; ++i) {
                final String ns = this.readString();
                result.add(ns);
            }
            return result;
        }
        
        void writeNamespaces(final Set namespaces) {
            this.writeShort(namespaces.size());
            for (final String ns : namespaces) {
                this.writeString(ns);
            }
        }
        
        OutputStream getSaverStream(final String name) {
            try {
                return SchemaTypeSystemImpl.this._filer.createBinaryFile(name);
            }
            catch (final IOException e) {
                throw new SchemaTypeLoaderException(e.getMessage(), SchemaTypeSystemImpl.this._name, this._handle, 9);
            }
        }
        
        InputStream getLoaderStream(final String resourcename) {
            return SchemaTypeSystemImpl.this._resourceLoader.getResourceAsStream(resourcename);
        }
        
        void checkContainerNotNull(final SchemaContainer container, final QName name) {
            if (container == null) {
                throw new LinkageError("Loading of resource " + SchemaTypeSystemImpl.this._name + '.' + this._handle + "failed, information from " + SchemaTypeSystemImpl.this._name + ".index.xsb is " + " out of sync (or conflicting index files found)");
            }
        }
        
        public SchemaGlobalElement finishLoadingElement() {
            final String handle = null;
            try {
                final int particleType = this.readShort();
                if (particleType != 4) {
                    throw new SchemaTypeLoaderException("Wrong particle type ", SchemaTypeSystemImpl.this._name, this._handle, 11);
                }
                final int particleFlags = this.readShort();
                final BigInteger minOccurs = this.readBigInteger();
                final BigInteger maxOccurs = this.readBigInteger();
                final QNameSet transitionRules = this.readQNameSet();
                final QName name = this.readQName();
                final SchemaContainer container = SchemaTypeSystemImpl.this.getContainer(name.getNamespaceURI());
                this.checkContainerNotNull(container, name);
                final SchemaGlobalElementImpl impl = new SchemaGlobalElementImpl(container);
                impl.setParticleType(particleType);
                impl.setMinOccurs(minOccurs);
                impl.setMaxOccurs(maxOccurs);
                impl.setTransitionRules(transitionRules, (particleFlags & 0x1) != 0x0);
                impl.setNameAndTypeRef(name, this.readTypeRef());
                impl.setDefault(this.readString(), (particleFlags & 0x4) != 0x0, null);
                if (this.atLeast(2, 16, 0)) {
                    impl.setDefaultValue(this.readXmlValueObject());
                }
                impl.setNillable((particleFlags & 0x8) != 0x0);
                impl.setBlock((particleFlags & 0x10) != 0x0, (particleFlags & 0x20) != 0x0, (particleFlags & 0x40) != 0x0);
                impl.setWsdlArrayType(this.readSOAPArrayType());
                impl.setAbstract((particleFlags & 0x80) != 0x0);
                impl.setAnnotation(this.readAnnotation(container));
                impl.setFinal((particleFlags & 0x100) != 0x0, (particleFlags & 0x200) != 0x0);
                if (this.atLeast(2, 17, 0)) {
                    impl.setSubstitutionGroup((SchemaGlobalElement.Ref)this.readHandle());
                }
                for (int substGroupCount = this.readShort(), i = 0; i < substGroupCount; ++i) {
                    impl.addSubstitutionGroupMember(this.readQName());
                }
                final SchemaIdentityConstraint.Ref[] idcs = new SchemaIdentityConstraint.Ref[this.readShort()];
                for (int j = 0; j < idcs.length; ++j) {
                    idcs[j] = (SchemaIdentityConstraint.Ref)this.readHandle();
                }
                impl.setIdentityConstraints(idcs);
                impl.setFilename(this.readString());
                return impl;
            }
            catch (final SchemaTypeLoaderException e) {
                throw e;
            }
            catch (final Exception e2) {
                throw new SchemaTypeLoaderException("Cannot load type from typesystem", SchemaTypeSystemImpl.this._name, handle, 14, e2);
            }
            finally {
                this.readEnd();
            }
        }
        
        public SchemaGlobalAttribute finishLoadingAttribute() {
            try {
                final QName name = this.readQName();
                final SchemaContainer container = SchemaTypeSystemImpl.this.getContainer(name.getNamespaceURI());
                this.checkContainerNotNull(container, name);
                final SchemaGlobalAttributeImpl impl = new SchemaGlobalAttributeImpl(container);
                this.loadAttribute(impl, name, container);
                impl.setFilename(this.readString());
                return impl;
            }
            catch (final SchemaTypeLoaderException e) {
                throw e;
            }
            catch (final Exception e2) {
                throw new SchemaTypeLoaderException("Cannot load type from typesystem", SchemaTypeSystemImpl.this._name, this._handle, 14, e2);
            }
            finally {
                this.readEnd();
            }
        }
        
        SchemaModelGroup finishLoadingModelGroup() {
            final QName name = this.readQName();
            final SchemaContainer container = SchemaTypeSystemImpl.this.getContainer(name.getNamespaceURI());
            this.checkContainerNotNull(container, name);
            final SchemaModelGroupImpl impl = new SchemaModelGroupImpl(container);
            try {
                impl.init(name, this.readString(), this.readShort() == 1, this.atLeast(2, 22, 0) ? this.readString() : null, this.atLeast(2, 22, 0) ? this.readString() : null, this.atLeast(2, 15, 0) && this.readShort() == 1, GroupDocument.Factory.parse(this.readString()).getGroup(), this.readAnnotation(container), null);
                if (this.atLeast(2, 21, 0)) {
                    impl.setFilename(this.readString());
                }
                return impl;
            }
            catch (final SchemaTypeLoaderException e) {
                throw e;
            }
            catch (final Exception e2) {
                throw new SchemaTypeLoaderException("Cannot load type from typesystem", SchemaTypeSystemImpl.this._name, this._handle, 14, e2);
            }
            finally {
                this.readEnd();
            }
        }
        
        SchemaIdentityConstraint finishLoadingIdentityConstraint() {
            try {
                final QName name = this.readQName();
                final SchemaContainer container = SchemaTypeSystemImpl.this.getContainer(name.getNamespaceURI());
                this.checkContainerNotNull(container, name);
                final SchemaIdentityConstraintImpl impl = new SchemaIdentityConstraintImpl(container);
                impl.setName(name);
                impl.setConstraintCategory(this.readShort());
                impl.setSelector(this.readString());
                impl.setAnnotation(this.readAnnotation(container));
                final String[] fields = new String[this.readShort()];
                for (int i = 0; i < fields.length; ++i) {
                    fields[i] = this.readString();
                }
                impl.setFields(fields);
                if (impl.getConstraintCategory() == 2) {
                    impl.setReferencedKey((SchemaIdentityConstraint.Ref)this.readHandle());
                }
                final int mapCount = this.readShort();
                final Map nsMappings = new HashMap();
                for (int j = 0; j < mapCount; ++j) {
                    final String prefix = this.readString();
                    final String uri = this.readString();
                    nsMappings.put(prefix, uri);
                }
                impl.setNSMap(nsMappings);
                if (this.atLeast(2, 21, 0)) {
                    impl.setFilename(this.readString());
                }
                return impl;
            }
            catch (final SchemaTypeLoaderException e) {
                throw e;
            }
            catch (final Exception e2) {
                throw new SchemaTypeLoaderException("Cannot load type from typesystem", SchemaTypeSystemImpl.this._name, this._handle, 14, e2);
            }
            finally {
                this.readEnd();
            }
        }
        
        SchemaAttributeGroup finishLoadingAttributeGroup() {
            final QName name = this.readQName();
            final SchemaContainer container = SchemaTypeSystemImpl.this.getContainer(name.getNamespaceURI());
            this.checkContainerNotNull(container, name);
            final SchemaAttributeGroupImpl impl = new SchemaAttributeGroupImpl(container);
            try {
                impl.init(name, this.readString(), this.readShort() == 1, this.atLeast(2, 22, 0) ? this.readString() : null, this.atLeast(2, 15, 0) && this.readShort() == 1, AttributeGroupDocument.Factory.parse(this.readString()).getAttributeGroup(), this.readAnnotation(container), null);
                if (this.atLeast(2, 21, 0)) {
                    impl.setFilename(this.readString());
                }
                return impl;
            }
            catch (final SchemaTypeLoaderException e) {
                throw e;
            }
            catch (final Exception e2) {
                throw new SchemaTypeLoaderException("Cannot load type from typesystem", SchemaTypeSystemImpl.this._name, this._handle, 14, e2);
            }
            finally {
                this.readEnd();
            }
        }
        
        public SchemaType finishLoadingType() {
            try {
                final SchemaContainer cNonNull = SchemaTypeSystemImpl.this.getContainerNonNull("");
                final SchemaTypeImpl impl = new SchemaTypeImpl(cNonNull, true);
                impl.setName(this.readQName());
                impl.setOuterSchemaTypeRef(this.readTypeRef());
                impl.setBaseDepth(this.readShort());
                impl.setBaseTypeRef(this.readTypeRef());
                impl.setDerivationType(this.readShort());
                impl.setAnnotation(this.readAnnotation(null));
                switch (this.readShort()) {
                    case 1: {
                        impl.setContainerFieldRef(this.readHandle());
                        break;
                    }
                    case 2: {
                        impl.setContainerFieldIndex((short)1, this.readShort());
                        break;
                    }
                    case 3: {
                        impl.setContainerFieldIndex((short)2, this.readShort());
                        break;
                    }
                }
                String jn = this.readString();
                impl.setFullJavaName((jn == null) ? "" : jn);
                jn = this.readString();
                impl.setFullJavaImplName((jn == null) ? "" : jn);
                impl.setAnonymousTypeRefs(this.readTypeRefArray());
                impl.setAnonymousUnionMemberOrdinal(this.readShort());
                final int flags = this.readInt();
                final boolean isComplexType = (flags & 0x1) == 0x0;
                impl.setCompiled((flags & 0x800) != 0x0);
                impl.setDocumentType((flags & 0x2) != 0x0);
                impl.setAttributeType((flags & 0x80000) != 0x0);
                impl.setSimpleType(!isComplexType);
                int complexVariety = 0;
                if (isComplexType) {
                    impl.setAbstractFinal((flags & 0x40000) != 0x0, (flags & 0x4000) != 0x0, (flags & 0x8000) != 0x0, (flags & 0x20000) != 0x0, (flags & 0x10000) != 0x0);
                    impl.setBlock((flags & 0x1000) != 0x0, (flags & 0x2000) != 0x0);
                    impl.setOrderSensitive((flags & 0x200) != 0x0);
                    complexVariety = this.readShort();
                    impl.setComplexTypeVariety(complexVariety);
                    if (this.atLeast(2, 23, 0)) {
                        impl.setContentBasedOnTypeRef(this.readTypeRef());
                    }
                    final SchemaAttributeModelImpl attrModel = new SchemaAttributeModelImpl();
                    for (int attrCount = this.readShort(), i = 0; i < attrCount; ++i) {
                        attrModel.addAttribute(this.readAttributeData());
                    }
                    attrModel.setWildcardSet(this.readQNameSet());
                    attrModel.setWildcardProcess(this.readShort());
                    final Map attrProperties = new LinkedHashMap();
                    for (int attrPropCount = this.readShort(), j = 0; j < attrPropCount; ++j) {
                        final SchemaProperty prop = this.readPropertyData();
                        if (!prop.isAttribute()) {
                            throw new SchemaTypeLoaderException("Attribute property " + j + " is not an attribute", SchemaTypeSystemImpl.this._name, this._handle, 6);
                        }
                        attrProperties.put(prop.getName(), prop);
                    }
                    SchemaParticle contentModel = null;
                    Map elemProperties = null;
                    int isAll = 0;
                    if (complexVariety == 3 || complexVariety == 4) {
                        isAll = this.readShort();
                        final SchemaParticle[] parts = this.readParticleArray();
                        if (parts.length == 1) {
                            contentModel = parts[0];
                        }
                        else {
                            if (parts.length != 0) {
                                throw new SchemaTypeLoaderException("Content model not well-formed", SchemaTypeSystemImpl.this._name, this._handle, 7);
                            }
                            contentModel = null;
                        }
                        elemProperties = new LinkedHashMap();
                        for (int elemPropCount = this.readShort(), k = 0; k < elemPropCount; ++k) {
                            final SchemaProperty prop2 = this.readPropertyData();
                            if (prop2.isAttribute()) {
                                throw new SchemaTypeLoaderException("Element property " + k + " is not an element", SchemaTypeSystemImpl.this._name, this._handle, 6);
                            }
                            elemProperties.put(prop2.getName(), prop2);
                        }
                    }
                    impl.setContentModel(contentModel, attrModel, elemProperties, attrProperties, isAll == 1);
                    final StscComplexTypeResolver.WildcardResult wcElt = StscComplexTypeResolver.summarizeEltWildcards(contentModel);
                    final StscComplexTypeResolver.WildcardResult wcAttr = StscComplexTypeResolver.summarizeAttrWildcards(attrModel);
                    impl.setWildcardSummary(wcElt.typedWildcards, wcElt.hasWildcards, wcAttr.typedWildcards, wcAttr.hasWildcards);
                }
                if (!isComplexType || complexVariety == 2) {
                    final int simpleVariety = this.readShort();
                    impl.setSimpleTypeVariety(simpleVariety);
                    final boolean isStringEnum = (flags & 0x40) != 0x0;
                    impl.setOrdered(((flags & 0x4) != 0x0) ? 0 : (((flags & 0x400) != 0x0) ? 2 : 1));
                    impl.setBounded((flags & 0x8) != 0x0);
                    impl.setFinite((flags & 0x10) != 0x0);
                    impl.setNumeric((flags & 0x20) != 0x0);
                    impl.setUnionOfLists((flags & 0x80) != 0x0);
                    impl.setSimpleFinal((flags & 0x8000) != 0x0, (flags & 0x20000) != 0x0, (flags & 0x10000) != 0x0);
                    final XmlValueRef[] facets = new XmlValueRef[12];
                    final boolean[] fixedFacets = new boolean[12];
                    for (int facetCount = this.readShort(), l = 0; l < facetCount; ++l) {
                        final int facetCode = this.readShort();
                        facets[facetCode] = this.readXmlValueObject();
                        fixedFacets[facetCode] = (this.readShort() == 1);
                    }
                    impl.setBasicFacets(facets, fixedFacets);
                    impl.setWhiteSpaceRule(this.readShort());
                    impl.setPatternFacet((flags & 0x100) != 0x0);
                    final int patternCount = this.readShort();
                    final RegularExpression[] patterns = new RegularExpression[patternCount];
                    for (int m = 0; m < patternCount; ++m) {
                        patterns[m] = new RegularExpression(this.readString(), "X");
                    }
                    impl.setPatterns(patterns);
                    final int enumCount = this.readShort();
                    final XmlValueRef[] enumValues = new XmlValueRef[enumCount];
                    for (int k = 0; k < enumCount; ++k) {
                        enumValues[k] = this.readXmlValueObject();
                    }
                    impl.setEnumerationValues((XmlValueRef[])((enumCount == 0) ? null : enumValues));
                    impl.setBaseEnumTypeRef(this.readTypeRef());
                    if (isStringEnum) {
                        final int seCount = this.readShort();
                        final SchemaStringEnumEntry[] entries = new SchemaStringEnumEntry[seCount];
                        for (int i2 = 0; i2 < seCount; ++i2) {
                            entries[i2] = new SchemaStringEnumEntryImpl(this.readString(), this.readShort(), this.readString());
                        }
                        impl.setStringEnumEntries(entries);
                    }
                    switch (simpleVariety) {
                        case 1: {
                            impl.setPrimitiveTypeRef(this.readTypeRef());
                            impl.setDecimalSize(this.readInt());
                            break;
                        }
                        case 3: {
                            impl.setPrimitiveTypeRef(BuiltinSchemaTypeSystem.ST_ANY_SIMPLE.getRef());
                            impl.setListItemTypeRef(this.readTypeRef());
                            break;
                        }
                        case 2: {
                            impl.setPrimitiveTypeRef(BuiltinSchemaTypeSystem.ST_ANY_SIMPLE.getRef());
                            impl.setUnionMemberTypeRefs(this.readTypeRefArray());
                            break;
                        }
                        default: {
                            throw new SchemaTypeLoaderException("Simple type does not have a recognized variety", SchemaTypeSystemImpl.this._name, this._handle, 8);
                        }
                    }
                }
                impl.setFilename(this.readString());
                if (impl.getName() != null) {
                    final SchemaContainer container = SchemaTypeSystemImpl.this.getContainer(impl.getName().getNamespaceURI());
                    this.checkContainerNotNull(container, impl.getName());
                    impl.setContainer(container);
                }
                else if (impl.isDocumentType()) {
                    final QName name = impl.getDocumentElementName();
                    if (name != null) {
                        final SchemaContainer container2 = SchemaTypeSystemImpl.this.getContainer(name.getNamespaceURI());
                        this.checkContainerNotNull(container2, name);
                        impl.setContainer(container2);
                    }
                }
                else if (impl.isAttributeType()) {
                    final QName name = impl.getAttributeTypeAttributeName();
                    if (name != null) {
                        final SchemaContainer container2 = SchemaTypeSystemImpl.this.getContainer(name.getNamespaceURI());
                        this.checkContainerNotNull(container2, name);
                        impl.setContainer(container2);
                    }
                }
                return impl;
            }
            catch (final SchemaTypeLoaderException e) {
                throw e;
            }
            catch (final Exception e2) {
                throw new SchemaTypeLoaderException("Cannot load type from typesystem", SchemaTypeSystemImpl.this._name, this._handle, 14, e2);
            }
            finally {
                this.readEnd();
            }
        }
        
        void writeTypeData(final SchemaType type) {
            this.writeQName(type.getName());
            this.writeType(type.getOuterType());
            this.writeShort(((SchemaTypeImpl)type).getBaseDepth());
            this.writeType(type.getBaseType());
            this.writeShort(type.getDerivationType());
            this.writeAnnotation(type.getAnnotation());
            if (type.getContainerField() == null) {
                this.writeShort(0);
            }
            else if (type.getOuterType().isAttributeType() || type.getOuterType().isDocumentType()) {
                this.writeShort(1);
                this.writeHandle((SchemaComponent)type.getContainerField());
            }
            else if (type.getContainerField().isAttribute()) {
                this.writeShort(2);
                this.writeShort(((SchemaTypeImpl)type.getOuterType()).getIndexForLocalAttribute((SchemaLocalAttribute)type.getContainerField()));
            }
            else {
                this.writeShort(3);
                this.writeShort(((SchemaTypeImpl)type.getOuterType()).getIndexForLocalElement((SchemaLocalElement)type.getContainerField()));
            }
            this.writeString(type.getFullJavaName());
            this.writeString(type.getFullJavaImplName());
            this.writeTypeArray(type.getAnonymousTypes());
            this.writeShort(type.getAnonymousUnionMemberOrdinal());
            int flags = 0;
            if (type.isSimpleType()) {
                flags |= 0x1;
            }
            if (type.isDocumentType()) {
                flags |= 0x2;
            }
            if (type.isAttributeType()) {
                flags |= 0x80000;
            }
            if (type.ordered() != 0) {
                flags |= 0x4;
            }
            if (type.ordered() == 2) {
                flags |= 0x400;
            }
            if (type.isBounded()) {
                flags |= 0x8;
            }
            if (type.isFinite()) {
                flags |= 0x10;
            }
            if (type.isNumeric()) {
                flags |= 0x20;
            }
            if (type.hasStringEnumValues()) {
                flags |= 0x40;
            }
            if (((SchemaTypeImpl)type).isUnionOfLists()) {
                flags |= 0x80;
            }
            if (type.hasPatternFacet()) {
                flags |= 0x100;
            }
            if (type.isOrderSensitive()) {
                flags |= 0x200;
            }
            if (type.blockExtension()) {
                flags |= 0x1000;
            }
            if (type.blockRestriction()) {
                flags |= 0x2000;
            }
            if (type.finalExtension()) {
                flags |= 0x4000;
            }
            if (type.finalRestriction()) {
                flags |= 0x4000;
            }
            if (type.finalList()) {
                flags |= 0x20000;
            }
            if (type.finalUnion()) {
                flags |= 0x10000;
            }
            if (type.isAbstract()) {
                flags |= 0x40000;
            }
            this.writeInt(flags);
            if (!type.isSimpleType()) {
                this.writeShort(type.getContentType());
                this.writeType(type.getContentBasedOnType());
                final SchemaAttributeModel attrModel = type.getAttributeModel();
                final SchemaLocalAttribute[] attrs = attrModel.getAttributes();
                this.writeShort(attrs.length);
                for (int i = 0; i < attrs.length; ++i) {
                    this.writeAttributeData(attrs[i]);
                }
                this.writeQNameSet(attrModel.getWildcardSet());
                this.writeShort(attrModel.getWildcardProcess());
                final SchemaProperty[] attrProperties = type.getAttributeProperties();
                this.writeShort(attrProperties.length);
                for (int j = 0; j < attrProperties.length; ++j) {
                    this.writePropertyData(attrProperties[j]);
                }
                if (type.getContentType() == 3 || type.getContentType() == 4) {
                    this.writeShort(type.hasAllContent() ? 1 : 0);
                    SchemaParticle[] parts;
                    if (type.getContentModel() != null) {
                        parts = new SchemaParticle[] { type.getContentModel() };
                    }
                    else {
                        parts = new SchemaParticle[0];
                    }
                    this.writeParticleArray(parts);
                    final SchemaProperty[] eltProperties = type.getElementProperties();
                    this.writeShort(eltProperties.length);
                    for (int k = 0; k < eltProperties.length; ++k) {
                        this.writePropertyData(eltProperties[k]);
                    }
                }
            }
            if (type.isSimpleType() || type.getContentType() == 2) {
                this.writeShort(type.getSimpleVariety());
                int facetCount = 0;
                for (int l = 0; l <= 11; ++l) {
                    if (type.getFacet(l) != null) {
                        ++facetCount;
                    }
                }
                this.writeShort(facetCount);
                for (int l = 0; l <= 11; ++l) {
                    final XmlAnySimpleType facet = type.getFacet(l);
                    if (facet != null) {
                        this.writeShort(l);
                        this.writeXmlValueObject(facet);
                        this.writeShort(type.isFacetFixed(l) ? 1 : 0);
                    }
                }
                this.writeShort(type.getWhiteSpaceRule());
                final RegularExpression[] patterns = ((SchemaTypeImpl)type).getPatternExpressions();
                this.writeShort(patterns.length);
                for (int i = 0; i < patterns.length; ++i) {
                    this.writeString(patterns[i].getPattern());
                }
                final XmlAnySimpleType[] enumValues = type.getEnumerationValues();
                if (enumValues == null) {
                    this.writeShort(0);
                }
                else {
                    this.writeShort(enumValues.length);
                    for (int j = 0; j < enumValues.length; ++j) {
                        this.writeXmlValueObject(enumValues[j]);
                    }
                }
                this.writeType(type.getBaseEnumType());
                if (type.hasStringEnumValues()) {
                    final SchemaStringEnumEntry[] entries = type.getStringEnumEntries();
                    this.writeShort(entries.length);
                    for (int m = 0; m < entries.length; ++m) {
                        this.writeString(entries[m].getString());
                        this.writeShort(entries[m].getIntValue());
                        this.writeString(entries[m].getEnumName());
                    }
                }
                switch (type.getSimpleVariety()) {
                    case 1: {
                        this.writeType(type.getPrimitiveType());
                        this.writeInt(type.getDecimalSize());
                        break;
                    }
                    case 3: {
                        this.writeType(type.getListItemType());
                        break;
                    }
                    case 2: {
                        this.writeTypeArray(type.getUnionMemberTypes());
                        break;
                    }
                }
            }
            this.writeString(type.getSourceName());
        }
        
        void readExtensionsList() {
            final int count = this.readShort();
            assert count == 0;
            for (int i = 0; i < count; ++i) {
                this.readString();
                this.readString();
                this.readString();
            }
        }
        
        SchemaLocalAttribute readAttributeData() {
            final SchemaLocalAttributeImpl result = new SchemaLocalAttributeImpl();
            this.loadAttribute(result, this.readQName(), null);
            return result;
        }
        
        void loadAttribute(final SchemaLocalAttributeImpl result, final QName name, final SchemaContainer container) {
            result.init(name, this.readTypeRef(), this.readShort(), this.readString(), null, this.atLeast(2, 16, 0) ? this.readXmlValueObject() : null, this.readShort() == 1, this.readSOAPArrayType(), this.readAnnotation(container), null);
        }
        
        void writeAttributeData(final SchemaLocalAttribute attr) {
            this.writeQName(attr.getName());
            this.writeType(attr.getType());
            this.writeShort(attr.getUse());
            this.writeString(attr.getDefaultText());
            this.writeXmlValueObject(attr.getDefaultValue());
            this.writeShort(attr.isFixed() ? 1 : 0);
            this.writeSOAPArrayType(((SchemaWSDLArrayType)attr).getWSDLArrayType());
            this.writeAnnotation(attr.getAnnotation());
        }
        
        void writeIdConstraintData(final SchemaIdentityConstraint idc) {
            this.writeQName(idc.getName());
            this.writeShort(idc.getConstraintCategory());
            this.writeString(idc.getSelector());
            this.writeAnnotation(idc.getAnnotation());
            final String[] fields = idc.getFields();
            this.writeShort(fields.length);
            for (int i = 0; i < fields.length; ++i) {
                this.writeString(fields[i]);
            }
            if (idc.getConstraintCategory() == 2) {
                this.writeHandle(idc.getReferencedKey());
            }
            final Set mappings = idc.getNSMap().entrySet();
            this.writeShort(mappings.size());
            for (final Map.Entry e : mappings) {
                final String prefix = e.getKey();
                final String uri = e.getValue();
                this.writeString(prefix);
                this.writeString(uri);
            }
            this.writeString(idc.getSourceName());
        }
        
        SchemaParticle[] readParticleArray() {
            final SchemaParticle[] result = new SchemaParticle[this.readShort()];
            for (int i = 0; i < result.length; ++i) {
                result[i] = this.readParticleData();
            }
            return result;
        }
        
        void writeParticleArray(final SchemaParticle[] spa) {
            this.writeShort(spa.length);
            for (int i = 0; i < spa.length; ++i) {
                this.writeParticleData(spa[i]);
            }
        }
        
        SchemaParticle readParticleData() {
            final int particleType = this.readShort();
            SchemaParticleImpl result;
            if (particleType != 4) {
                result = new SchemaParticleImpl();
            }
            else {
                result = new SchemaLocalElementImpl();
            }
            this.loadParticle(result, particleType);
            return result;
        }
        
        void loadParticle(final SchemaParticleImpl result, final int particleType) {
            final int particleFlags = this.readShort();
            result.setParticleType(particleType);
            result.setMinOccurs(this.readBigInteger());
            result.setMaxOccurs(this.readBigInteger());
            result.setTransitionRules(this.readQNameSet(), (particleFlags & 0x1) != 0x0);
            switch (particleType) {
                case 5: {
                    result.setWildcardSet(this.readQNameSet());
                    result.setWildcardProcess(this.readShort());
                    break;
                }
                case 4: {
                    final SchemaLocalElementImpl lresult = (SchemaLocalElementImpl)result;
                    lresult.setNameAndTypeRef(this.readQName(), this.readTypeRef());
                    lresult.setDefault(this.readString(), (particleFlags & 0x4) != 0x0, null);
                    if (this.atLeast(2, 16, 0)) {
                        lresult.setDefaultValue(this.readXmlValueObject());
                    }
                    lresult.setNillable((particleFlags & 0x8) != 0x0);
                    lresult.setBlock((particleFlags & 0x10) != 0x0, (particleFlags & 0x20) != 0x0, (particleFlags & 0x40) != 0x0);
                    lresult.setWsdlArrayType(this.readSOAPArrayType());
                    lresult.setAbstract((particleFlags & 0x80) != 0x0);
                    lresult.setAnnotation(this.readAnnotation(null));
                    final SchemaIdentityConstraint.Ref[] idcs = new SchemaIdentityConstraint.Ref[this.readShort()];
                    for (int i = 0; i < idcs.length; ++i) {
                        idcs[i] = (SchemaIdentityConstraint.Ref)this.readHandle();
                    }
                    lresult.setIdentityConstraints(idcs);
                    break;
                }
                case 1:
                case 2:
                case 3: {
                    result.setParticleChildren(this.readParticleArray());
                    break;
                }
                default: {
                    throw new SchemaTypeLoaderException("Unrecognized particle type ", SchemaTypeSystemImpl.this._name, this._handle, 11);
                }
            }
        }
        
        void writeParticleData(final SchemaParticle part) {
            this.writeShort(part.getParticleType());
            short flags = 0;
            if (part.isSkippable()) {
                flags |= 0x1;
            }
            if (part.getParticleType() == 4) {
                final SchemaLocalElement lpart = (SchemaLocalElement)part;
                if (lpart.isFixed()) {
                    flags |= 0x4;
                }
                if (lpart.isNillable()) {
                    flags |= 0x8;
                }
                if (lpart.blockExtension()) {
                    flags |= 0x10;
                }
                if (lpart.blockRestriction()) {
                    flags |= 0x20;
                }
                if (lpart.blockSubstitution()) {
                    flags |= 0x40;
                }
                if (lpart.isAbstract()) {
                    flags |= 0x80;
                }
                if (lpart instanceof SchemaGlobalElement) {
                    final SchemaGlobalElement gpart = (SchemaGlobalElement)lpart;
                    if (gpart.finalExtension()) {
                        flags |= 0x100;
                    }
                    if (gpart.finalRestriction()) {
                        flags |= 0x200;
                    }
                }
            }
            this.writeShort(flags);
            this.writeBigInteger(part.getMinOccurs());
            this.writeBigInteger(part.getMaxOccurs());
            this.writeQNameSet(part.acceptedStartNames());
            switch (part.getParticleType()) {
                case 5: {
                    this.writeQNameSet(part.getWildcardSet());
                    this.writeShort(part.getWildcardProcess());
                    break;
                }
                case 4: {
                    final SchemaLocalElement lpart = (SchemaLocalElement)part;
                    this.writeQName(lpart.getName());
                    this.writeType(lpart.getType());
                    this.writeString(lpart.getDefaultText());
                    this.writeXmlValueObject(lpart.getDefaultValue());
                    this.writeSOAPArrayType(((SchemaWSDLArrayType)lpart).getWSDLArrayType());
                    this.writeAnnotation(lpart.getAnnotation());
                    if (lpart instanceof SchemaGlobalElement) {
                        final SchemaGlobalElement gpart = (SchemaGlobalElement)lpart;
                        this.writeHandle(gpart.substitutionGroup());
                        final QName[] substGroupMembers = gpart.substitutionGroupMembers();
                        this.writeShort(substGroupMembers.length);
                        for (int i = 0; i < substGroupMembers.length; ++i) {
                            this.writeQName(substGroupMembers[i]);
                        }
                    }
                    final SchemaIdentityConstraint[] idcs = lpart.getIdentityConstraints();
                    this.writeShort(idcs.length);
                    for (int j = 0; j < idcs.length; ++j) {
                        this.writeHandle(idcs[j]);
                    }
                    break;
                }
                case 1:
                case 2:
                case 3: {
                    this.writeParticleArray(part.getParticleChildren());
                    break;
                }
                default: {
                    throw new SchemaTypeLoaderException("Unrecognized particle type ", SchemaTypeSystemImpl.this._name, this._handle, 11);
                }
            }
        }
        
        SchemaProperty readPropertyData() {
            final SchemaPropertyImpl prop = new SchemaPropertyImpl();
            prop.setName(this.readQName());
            prop.setTypeRef(this.readTypeRef());
            final int propflags = this.readShort();
            prop.setAttribute((propflags & 0x1) != 0x0);
            prop.setContainerTypeRef(this.readTypeRef());
            prop.setMinOccurs(this.readBigInteger());
            prop.setMaxOccurs(this.readBigInteger());
            prop.setNillable(this.readShort());
            prop.setDefault(this.readShort());
            prop.setFixed(this.readShort());
            prop.setDefaultText(this.readString());
            prop.setJavaPropertyName(this.readString());
            prop.setJavaTypeCode(this.readShort());
            prop.setExtendsJava(this.readTypeRef(), (propflags & 0x2) != 0x0, (propflags & 0x4) != 0x0, (propflags & 0x8) != 0x0);
            if (this.atMost(2, 19, 0)) {
                prop.setJavaSetterDelimiter(this.readQNameSet());
            }
            if (this.atLeast(2, 16, 0)) {
                prop.setDefaultValue(this.readXmlValueObject());
            }
            if (!prop.isAttribute() && this.atLeast(2, 17, 0)) {
                final int size = this.readShort();
                final LinkedHashSet qnames = new LinkedHashSet(size);
                for (int i = 0; i < size; ++i) {
                    qnames.add(this.readQName());
                }
                prop.setAcceptedNames(qnames);
            }
            prop.setImmutable();
            return prop;
        }
        
        void writePropertyData(final SchemaProperty prop) {
            this.writeQName(prop.getName());
            this.writeType(prop.getType());
            this.writeShort((prop.isAttribute() ? 1 : 0) | (prop.extendsJavaSingleton() ? 2 : 0) | (prop.extendsJavaOption() ? 4 : 0) | (prop.extendsJavaArray() ? 8 : 0));
            this.writeType(prop.getContainerType());
            this.writeBigInteger(prop.getMinOccurs());
            this.writeBigInteger(prop.getMaxOccurs());
            this.writeShort(prop.hasNillable());
            this.writeShort(prop.hasDefault());
            this.writeShort(prop.hasFixed());
            this.writeString(prop.getDefaultText());
            this.writeString(prop.getJavaPropertyName());
            this.writeShort(prop.getJavaTypeCode());
            this.writeType(prop.javaBasedOnType());
            this.writeXmlValueObject(prop.getDefaultValue());
            if (!prop.isAttribute()) {
                final QName[] names = prop.acceptedNames();
                this.writeShort(names.length);
                for (int i = 0; i < names.length; ++i) {
                    this.writeQName(names[i]);
                }
            }
        }
        
        void writeModelGroupData(final SchemaModelGroup grp) {
            final SchemaModelGroupImpl impl = (SchemaModelGroupImpl)grp;
            this.writeQName(impl.getName());
            this.writeString(impl.getTargetNamespace());
            this.writeShort((impl.getChameleonNamespace() != null) ? 1 : 0);
            this.writeString(impl.getElemFormDefault());
            this.writeString(impl.getAttFormDefault());
            this.writeShort(impl.isRedefinition() ? 1 : 0);
            this.writeString(impl.getParseObject().xmlText(new XmlOptions().setSaveOuter()));
            this.writeAnnotation(impl.getAnnotation());
            this.writeString(impl.getSourceName());
        }
        
        void writeAttributeGroupData(final SchemaAttributeGroup grp) {
            final SchemaAttributeGroupImpl impl = (SchemaAttributeGroupImpl)grp;
            this.writeQName(impl.getName());
            this.writeString(impl.getTargetNamespace());
            this.writeShort((impl.getChameleonNamespace() != null) ? 1 : 0);
            this.writeString(impl.getFormDefault());
            this.writeShort(impl.isRedefinition() ? 1 : 0);
            this.writeString(impl.getParseObject().xmlText(new XmlOptions().setSaveOuter()));
            this.writeAnnotation(impl.getAnnotation());
            this.writeString(impl.getSourceName());
        }
        
        XmlValueRef readXmlValueObject() {
            final SchemaType.Ref typeref = this.readTypeRef();
            if (typeref == null) {
                return null;
            }
            final int btc = this.readShort();
            switch (btc) {
                default: {
                    assert false;
                    return new XmlValueRef(typeref, null);
                }
                case 0: {
                    return new XmlValueRef(typeref, null);
                }
                case 65535: {
                    final int size = this.readShort();
                    final List values = new ArrayList();
                    this.writeShort(values.size());
                    for (int i = 0; i < size; ++i) {
                        values.add(this.readXmlValueObject());
                    }
                    return new XmlValueRef(typeref, values);
                }
                case 2:
                case 3:
                case 6:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21: {
                    return new XmlValueRef(typeref, this.readString());
                }
                case 4:
                case 5: {
                    return new XmlValueRef(typeref, this.readByteArray());
                }
                case 7:
                case 8: {
                    return new XmlValueRef(typeref, this.readQName());
                }
                case 9:
                case 10: {
                    return new XmlValueRef(typeref, new Double(this.readDouble()));
                }
            }
        }
        
        void writeXmlValueObject(final XmlAnySimpleType value) {
            final SchemaType type = (value == null) ? null : value.schemaType();
            this.writeType(type);
            if (type == null) {
                return;
            }
            final SchemaType iType = ((SimpleValue)value).instanceType();
            if (iType == null) {
                this.writeShort(0);
            }
            else if (iType.getSimpleVariety() == 3) {
                this.writeShort(-1);
                final List values = ((XmlObjectBase)value).xgetListValue();
                this.writeShort(values.size());
                final Iterator i = values.iterator();
                while (i.hasNext()) {
                    this.writeXmlValueObject(i.next());
                }
            }
            else {
                final int btc = iType.getPrimitiveType().getBuiltinTypeCode();
                this.writeShort(btc);
                switch (btc) {
                    case 2:
                    case 3:
                    case 6:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20:
                    case 21: {
                        this.writeString(value.getStringValue());
                        break;
                    }
                    case 4:
                    case 5: {
                        this.writeByteArray(((SimpleValue)value).getByteArrayValue());
                        break;
                    }
                    case 7:
                    case 8: {
                        this.writeQName(((SimpleValue)value).getQNameValue());
                        break;
                    }
                    case 9: {
                        this.writeDouble(((SimpleValue)value).getFloatValue());
                        break;
                    }
                    case 10: {
                        this.writeDouble(((SimpleValue)value).getDoubleValue());
                        break;
                    }
                }
            }
        }
        
        double readDouble() {
            try {
                return this._input.readDouble();
            }
            catch (final IOException e) {
                throw new SchemaTypeLoaderException(e.getMessage(), SchemaTypeSystemImpl.this._name, this._handle, 9);
            }
        }
        
        void writeDouble(final double d) {
            if (this._output != null) {
                try {
                    this._output.writeDouble(d);
                }
                catch (final IOException e) {
                    throw new SchemaTypeLoaderException(e.getMessage(), SchemaTypeSystemImpl.this._name, this._handle, 9);
                }
            }
        }
        
        QNameSet readQNameSet() {
            final int flag = this.readShort();
            final Set uriSet = new HashSet();
            for (int uriCount = this.readShort(), i = 0; i < uriCount; ++i) {
                uriSet.add(this.readString());
            }
            final Set qnameSet1 = new HashSet();
            for (int qncount1 = this.readShort(), j = 0; j < qncount1; ++j) {
                qnameSet1.add(this.readQName());
            }
            final Set qnameSet2 = new HashSet();
            for (int qncount2 = this.readShort(), k = 0; k < qncount2; ++k) {
                qnameSet2.add(this.readQName());
            }
            if (flag == 1) {
                return QNameSet.forSets(uriSet, null, qnameSet1, qnameSet2);
            }
            return QNameSet.forSets(null, uriSet, qnameSet2, qnameSet1);
        }
        
        void writeQNameSet(final QNameSet set) {
            final boolean invert = set.excludedURIs() != null;
            this.writeShort(invert ? 1 : 0);
            final Set uriSet = invert ? set.excludedURIs() : set.includedURIs();
            this.writeShort(uriSet.size());
            final Iterator i = uriSet.iterator();
            while (i.hasNext()) {
                this.writeString(i.next());
            }
            final Set qnameSet1 = invert ? set.excludedQNamesInIncludedURIs() : set.includedQNamesInExcludedURIs();
            this.writeShort(qnameSet1.size());
            final Iterator j = qnameSet1.iterator();
            while (j.hasNext()) {
                this.writeQName(j.next());
            }
            final Set qnameSet2 = invert ? set.includedQNamesInExcludedURIs() : set.excludedQNamesInIncludedURIs();
            this.writeShort(qnameSet2.size());
            final Iterator k = qnameSet2.iterator();
            while (k.hasNext()) {
                this.writeQName(k.next());
            }
        }
        
        byte[] readByteArray() {
            try {
                final int len = this._input.readShort();
                final byte[] result = new byte[len];
                this._input.readFully(result);
                return result;
            }
            catch (final IOException e) {
                throw new SchemaTypeLoaderException(e.getMessage(), SchemaTypeSystemImpl.this._name, this._handle, 9);
            }
        }
        
        void writeByteArray(final byte[] ba) {
            try {
                this.writeShort(ba.length);
                if (this._output != null) {
                    this._output.write(ba);
                }
            }
            catch (final IOException e) {
                throw new SchemaTypeLoaderException(e.getMessage(), SchemaTypeSystemImpl.this._name, this._handle, 9);
            }
        }
        
        BigInteger readBigInteger() {
            final byte[] result = this.readByteArray();
            if (result.length == 0) {
                return null;
            }
            if (result.length == 1 && result[0] == 0) {
                return BigInteger.ZERO;
            }
            if (result.length == 1 && result[0] == 1) {
                return BigInteger.ONE;
            }
            return new BigInteger(result);
        }
        
        void writeBigInteger(final BigInteger bi) {
            if (bi == null) {
                this.writeShort(0);
            }
            else if (bi.signum() == 0) {
                this.writeByteArray(SchemaTypeSystemImpl.SINGLE_ZERO_BYTE);
            }
            else {
                this.writeByteArray(bi.toByteArray());
            }
        }
    }
}
