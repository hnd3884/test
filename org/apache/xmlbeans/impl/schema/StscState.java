package org.apache.xmlbeans.impl.schema;

import java.net.URISyntaxException;
import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.values.XmlStringImpl;
import org.apache.xmlbeans.SchemaAnnotation;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.SchemaComponent;
import org.apache.xmlbeans.impl.common.ResolverUtil;
import org.apache.xmlbeans.SystemProperties;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.util.HexBin;
import java.net.URL;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlObject;
import java.util.Collections;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaIdentityConstraint;
import org.apache.xmlbeans.SchemaGlobalAttribute;
import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaAttributeGroup;
import org.apache.xmlbeans.SchemaModelGroup;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Arrays;
import java.net.URI;
import org.apache.xmlbeans.SchemaType;
import java.io.File;
import org.xml.sax.EntityResolver;
import java.util.List;
import java.util.Set;
import org.apache.xmlbeans.SchemaTypeLoader;
import java.util.Map;
import org.apache.xmlbeans.BindingConfig;
import java.util.Collection;

public class StscState
{
    private String _givenStsName;
    private Collection _errorListener;
    private SchemaTypeSystemImpl _target;
    private BindingConfig _config;
    private Map _compatMap;
    private boolean _doingDownloads;
    private byte[] _digest;
    private boolean _noDigest;
    private boolean _allowPartial;
    private int _recoveredErrors;
    private SchemaTypeLoader _importingLoader;
    private Map _containers;
    private SchemaDependencies _dependencies;
    private Map _redefinedGlobalTypes;
    private Map _redefinedModelGroups;
    private Map _redefinedAttributeGroups;
    private Map _globalTypes;
    private Map _globalElements;
    private Map _globalAttributes;
    private Map _modelGroups;
    private Map _attributeGroups;
    private Map _documentTypes;
    private Map _attributeTypes;
    private Map _typesByClassname;
    private Map _misspelledNames;
    private Set _processingGroups;
    private Map _idConstraints;
    private Set _namespaces;
    private List _annotations;
    private boolean _noUpa;
    private boolean _noPvr;
    private boolean _noAnn;
    private boolean _mdefAll;
    private Set _mdefNamespaces;
    private EntityResolver _entityResolver;
    private File _schemasDir;
    public static final Object CHAMELEON_INCLUDE_URI;
    private static ThreadLocal tl_stscStack;
    private static final XmlValueRef XMLSTR_PRESERVE;
    private static final XmlValueRef XMLSTR_REPLACE;
    private static final XmlValueRef XMLSTR_COLLAPSE;
    static final SchemaType[] EMPTY_ST_ARRAY;
    static final SchemaType.Ref[] EMPTY_STREF_ARRAY;
    private static final XmlValueRef[] FACETS_NONE;
    private static final boolean[] FIXED_FACETS_NONE;
    private static final XmlValueRef[] FACETS_WS_COLLAPSE;
    private static final boolean[] FIXED_FACETS_WS;
    static final XmlValueRef[] FACETS_UNION;
    static final boolean[] FIXED_FACETS_UNION;
    static final XmlValueRef[] FACETS_LIST;
    static final boolean[] FIXED_FACETS_LIST;
    private static final String PROJECT_URL_PREFIX = "project://local";
    Map _sourceForUri;
    URI _baseURI;
    SchemaTypeLoader _s4sloader;
    
    private static Set buildDefaultMdefNamespaces() {
        return new HashSet(Arrays.asList("http://www.openuri.org/2002/04/soap/conversation/"));
    }
    
    private StscState() {
        this._digest = null;
        this._noDigest = false;
        this._allowPartial = false;
        this._recoveredErrors = 0;
        this._containers = new LinkedHashMap();
        this._redefinedGlobalTypes = new LinkedHashMap();
        this._redefinedModelGroups = new LinkedHashMap();
        this._redefinedAttributeGroups = new LinkedHashMap();
        this._globalTypes = new LinkedHashMap();
        this._globalElements = new LinkedHashMap();
        this._globalAttributes = new LinkedHashMap();
        this._modelGroups = new LinkedHashMap();
        this._attributeGroups = new LinkedHashMap();
        this._documentTypes = new LinkedHashMap();
        this._attributeTypes = new LinkedHashMap();
        this._typesByClassname = new LinkedHashMap();
        this._misspelledNames = new HashMap();
        this._processingGroups = new LinkedHashSet();
        this._idConstraints = new LinkedHashMap();
        this._namespaces = new HashSet();
        this._annotations = new ArrayList();
        this._mdefNamespaces = buildDefaultMdefNamespaces();
        this._sourceForUri = new HashMap();
        this._baseURI = URI.create("project://local/");
        this._s4sloader = XmlBeans.typeLoaderForClassLoader(SchemaDocument.class.getClassLoader());
    }
    
    public void initFromTypeSystem(final SchemaTypeSystemImpl system, final Set newNamespaces) {
        final SchemaContainer[] containers = system.containers();
        for (int i = 0; i < containers.length; ++i) {
            if (!newNamespaces.contains(containers[i].getNamespace())) {
                this.addContainer(containers[i]);
            }
        }
    }
    
    void addNewContainer(final String namespace) {
        if (this._containers.containsKey(namespace)) {
            return;
        }
        final SchemaContainer container = new SchemaContainer(namespace);
        container.setTypeSystem(this.sts());
        this.addNamespace(namespace);
        this._containers.put(namespace, container);
    }
    
    private void addContainer(final SchemaContainer container) {
        this._containers.put(container.getNamespace(), container);
        final List redefModelGroups = container.redefinedModelGroups();
        for (int i = 0; i < redefModelGroups.size(); ++i) {
            final QName name = redefModelGroups.get(i).getName();
            this._redefinedModelGroups.put(name, redefModelGroups.get(i));
        }
        final List redefAttrGroups = container.redefinedAttributeGroups();
        for (int j = 0; j < redefAttrGroups.size(); ++j) {
            final QName name2 = redefAttrGroups.get(j).getName();
            this._redefinedAttributeGroups.put(name2, redefAttrGroups.get(j));
        }
        final List redefTypes = container.redefinedGlobalTypes();
        for (int k = 0; k < redefTypes.size(); ++k) {
            final QName name3 = redefTypes.get(k).getName();
            this._redefinedGlobalTypes.put(name3, redefTypes.get(k));
        }
        final List globalElems = container.globalElements();
        for (int l = 0; l < globalElems.size(); ++l) {
            final QName name4 = globalElems.get(l).getName();
            this._globalElements.put(name4, globalElems.get(l));
        }
        final List globalAtts = container.globalAttributes();
        for (int m = 0; m < globalAtts.size(); ++m) {
            final QName name5 = globalAtts.get(m).getName();
            this._globalAttributes.put(name5, globalAtts.get(m));
        }
        final List modelGroups = container.modelGroups();
        for (int i2 = 0; i2 < modelGroups.size(); ++i2) {
            final QName name6 = modelGroups.get(i2).getName();
            this._modelGroups.put(name6, modelGroups.get(i2));
        }
        final List attrGroups = container.attributeGroups();
        for (int i3 = 0; i3 < attrGroups.size(); ++i3) {
            final QName name7 = attrGroups.get(i3).getName();
            this._attributeGroups.put(name7, attrGroups.get(i3));
        }
        final List globalTypes = container.globalTypes();
        for (int i4 = 0; i4 < globalTypes.size(); ++i4) {
            final SchemaType t = globalTypes.get(i4);
            final QName name8 = t.getName();
            this._globalTypes.put(name8, t);
            if (t.getFullJavaName() != null) {
                this.addClassname(t.getFullJavaName(), t);
            }
        }
        final List documentTypes = container.documentTypes();
        for (int i5 = 0; i5 < documentTypes.size(); ++i5) {
            final SchemaType t2 = documentTypes.get(i5);
            final QName name9 = t2.getProperties()[0].getName();
            this._documentTypes.put(name9, t2);
            if (t2.getFullJavaName() != null) {
                this.addClassname(t2.getFullJavaName(), t2);
            }
        }
        final List attributeTypes = container.attributeTypes();
        for (int i6 = 0; i6 < attributeTypes.size(); ++i6) {
            final SchemaType t3 = attributeTypes.get(i6);
            final QName name10 = t3.getProperties()[0].getName();
            this._attributeTypes.put(name10, t3);
            if (t3.getFullJavaName() != null) {
                this.addClassname(t3.getFullJavaName(), t3);
            }
        }
        final List identityConstraints = container.identityConstraints();
        for (int i7 = 0; i7 < identityConstraints.size(); ++i7) {
            final QName name10 = identityConstraints.get(i7).getName();
            this._idConstraints.put(name10, identityConstraints.get(i7));
        }
        this._annotations.addAll(container.annotations());
        this._namespaces.add(container.getNamespace());
        container.unsetImmutable();
    }
    
    SchemaContainer getContainer(final String namespace) {
        return this._containers.get(namespace);
    }
    
    Map getContainerMap() {
        return Collections.unmodifiableMap((Map<?, ?>)this._containers);
    }
    
    void registerDependency(final String sourceNs, final String targetNs) {
        this._dependencies.registerDependency(sourceNs, targetNs);
    }
    
    void registerContribution(final String ns, final String fileUrl) {
        this._dependencies.registerContribution(ns, fileUrl);
    }
    
    SchemaDependencies getDependencies() {
        return this._dependencies;
    }
    
    void setDependencies(final SchemaDependencies deps) {
        this._dependencies = deps;
    }
    
    boolean isFileProcessed(final String url) {
        return this._dependencies.isFileRepresented(url);
    }
    
    public void setImportingTypeLoader(final SchemaTypeLoader loader) {
        this._importingLoader = loader;
    }
    
    public void setErrorListener(final Collection errorListener) {
        this._errorListener = errorListener;
    }
    
    public void error(final String message, final int code, final XmlObject loc) {
        addError(this._errorListener, message, code, loc);
    }
    
    public void error(final String code, final Object[] args, final XmlObject loc) {
        addError(this._errorListener, code, args, loc);
    }
    
    public void recover(final String code, final Object[] args, final XmlObject loc) {
        addError(this._errorListener, code, args, loc);
        ++this._recoveredErrors;
    }
    
    public void warning(final String message, final int code, final XmlObject loc) {
        addWarning(this._errorListener, message, code, loc);
    }
    
    public void warning(final String code, final Object[] args, final XmlObject loc) {
        if (code == "reserved-type-name" && loc.documentProperties().getSourceName() != null && loc.documentProperties().getSourceName().indexOf("XMLSchema.xsd") > 0) {
            return;
        }
        addWarning(this._errorListener, code, args, loc);
    }
    
    public void info(final String message) {
        addInfo(this._errorListener, message);
    }
    
    public void info(final String code, final Object[] args) {
        addInfo(this._errorListener, code, args);
    }
    
    public static void addError(final Collection errorListener, final String message, final int code, final XmlObject location) {
        final XmlError err = XmlError.forObject(message, 0, location);
        errorListener.add(err);
    }
    
    public static void addError(final Collection errorListener, final String code, final Object[] args, final XmlObject location) {
        final XmlError err = XmlError.forObject(code, args, 0, location);
        errorListener.add(err);
    }
    
    public static void addError(final Collection errorListener, final String code, final Object[] args, final File location) {
        final XmlError err = XmlError.forLocation(code, args, 0, location.toURI().toString(), 0, 0, 0);
        errorListener.add(err);
    }
    
    public static void addError(final Collection errorListener, final String code, final Object[] args, final URL location) {
        final XmlError err = XmlError.forLocation(code, args, 0, location.toString(), 0, 0, 0);
        errorListener.add(err);
    }
    
    public static void addWarning(final Collection errorListener, final String message, final int code, final XmlObject location) {
        final XmlError err = XmlError.forObject(message, 1, location);
        errorListener.add(err);
    }
    
    public static void addWarning(final Collection errorListener, final String code, final Object[] args, final XmlObject location) {
        final XmlError err = XmlError.forObject(code, args, 1, location);
        errorListener.add(err);
    }
    
    public static void addInfo(final Collection errorListener, final String message) {
        final XmlError err = XmlError.forMessage(message, 2);
        errorListener.add(err);
    }
    
    public static void addInfo(final Collection errorListener, final String code, final Object[] args) {
        final XmlError err = XmlError.forMessage(code, args, 2);
        errorListener.add(err);
    }
    
    public void setGivenTypeSystemName(final String name) {
        this._givenStsName = name;
    }
    
    public void setTargetSchemaTypeSystem(final SchemaTypeSystemImpl target) {
        this._target = target;
    }
    
    public void addSchemaDigest(final byte[] digest) {
        if (this._noDigest) {
            return;
        }
        if (digest == null) {
            this._noDigest = true;
            this._digest = null;
            return;
        }
        if (this._digest == null) {
            this._digest = new byte[16];
        }
        int len = this._digest.length;
        if (digest.length < len) {
            len = digest.length;
        }
        for (int i = 0; i < len; ++i) {
            final byte[] digest2 = this._digest;
            final int n = i;
            digest2[n] ^= digest[i];
        }
    }
    
    public SchemaTypeSystemImpl sts() {
        if (this._target != null) {
            return this._target;
        }
        String name = this._givenStsName;
        if (name == null && this._digest != null) {
            name = "s" + new String(HexBin.encode(this._digest));
        }
        return this._target = new SchemaTypeSystemImpl(name);
    }
    
    public boolean shouldDownloadURI(final String uriString) {
        if (this._doingDownloads) {
            return true;
        }
        if (uriString == null) {
            return false;
        }
        try {
            final URI uri = new URI(uriString);
            if (uri.getScheme().equalsIgnoreCase("jar") || uri.getScheme().equalsIgnoreCase("zip")) {
                final String s = uri.getSchemeSpecificPart();
                final int i = s.lastIndexOf(33);
                return this.shouldDownloadURI((i > 0) ? s.substring(0, i) : s);
            }
            return uri.getScheme().equalsIgnoreCase("file");
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    public void setOptions(final XmlOptions options) {
        if (options == null) {
            return;
        }
        this._allowPartial = options.hasOption("COMPILE_PARTIAL_TYPESYSTEM");
        this._compatMap = (Map)options.get("COMPILE_SUBSTITUTE_NAMES");
        this._noUpa = (options.hasOption("COMPILE_NO_UPA_RULE") || !"true".equals(SystemProperties.getProperty("xmlbean.uniqueparticleattribution", "true")));
        this._noPvr = (options.hasOption("COMPILE_NO_PVR_RULE") || !"true".equals(SystemProperties.getProperty("xmlbean.particlerestriction", "true")));
        this._noAnn = (options.hasOption("COMPILE_NO_ANNOTATIONS") || !"true".equals(SystemProperties.getProperty("xmlbean.schemaannotations", "true")));
        this._doingDownloads = (options.hasOption("COMPILE_DOWNLOAD_URLS") || "true".equals(SystemProperties.getProperty("xmlbean.downloadurls", "false")));
        this._entityResolver = (EntityResolver)options.get("ENTITY_RESOLVER");
        if (this._entityResolver == null) {
            this._entityResolver = ResolverUtil.getGlobalEntityResolver();
        }
        if (this._entityResolver != null) {
            this._doingDownloads = true;
        }
        if (options.hasOption("COMPILE_MDEF_NAMESPACES")) {
            this._mdefNamespaces.addAll((Collection)options.get("COMPILE_MDEF_NAMESPACES"));
            final String local = "##local";
            final String any = "##any";
            if (this._mdefNamespaces.contains(local)) {
                this._mdefNamespaces.remove(local);
                this._mdefNamespaces.add("");
            }
            if (this._mdefNamespaces.contains(any)) {
                this._mdefNamespaces.remove(any);
                this._mdefAll = true;
            }
        }
    }
    
    public EntityResolver getEntityResolver() {
        return this._entityResolver;
    }
    
    public boolean noUpa() {
        return this._noUpa;
    }
    
    public boolean noPvr() {
        return this._noPvr;
    }
    
    public boolean noAnn() {
        return this._noAnn;
    }
    
    public boolean allowPartial() {
        return this._allowPartial;
    }
    
    public int getRecovered() {
        return this._recoveredErrors;
    }
    
    private QName compatName(QName name, final String chameleonNamespace) {
        if (name.getNamespaceURI().length() == 0 && chameleonNamespace != null && chameleonNamespace.length() > 0) {
            name = new QName(chameleonNamespace, name.getLocalPart());
        }
        if (this._compatMap == null) {
            return name;
        }
        final QName subst = this._compatMap.get(name);
        if (subst == null) {
            return name;
        }
        return subst;
    }
    
    public void setBindingConfig(final BindingConfig config) throws IllegalArgumentException {
        this._config = config;
    }
    
    public BindingConfig getBindingConfig() throws IllegalArgumentException {
        return this._config;
    }
    
    public String getPackageOverride(final String namespace) {
        if (this._config == null) {
            return null;
        }
        return this._config.lookupPackageForNamespace(namespace);
    }
    
    public String getJavaPrefix(final String namespace) {
        if (this._config == null) {
            return null;
        }
        return this._config.lookupPrefixForNamespace(namespace);
    }
    
    public String getJavaSuffix(final String namespace) {
        if (this._config == null) {
            return null;
        }
        return this._config.lookupSuffixForNamespace(namespace);
    }
    
    public String getJavaname(final QName qname, final int kind) {
        if (this._config == null) {
            return null;
        }
        return this._config.lookupJavanameForQName(qname, kind);
    }
    
    private static String crunchName(final QName name) {
        return name.getLocalPart().toLowerCase();
    }
    
    void addSpelling(final QName name, final SchemaComponent comp) {
        this._misspelledNames.put(crunchName(name), comp);
    }
    
    SchemaComponent findSpelling(final QName name) {
        return this._misspelledNames.get(crunchName(name));
    }
    
    void addNamespace(final String targetNamespace) {
        this._namespaces.add(targetNamespace);
    }
    
    String[] getNamespaces() {
        return this._namespaces.toArray(new String[this._namespaces.size()]);
    }
    
    boolean linkerDefinesNamespace(final String namespace) {
        return this._importingLoader.isNamespaceDefined(namespace);
    }
    
    SchemaTypeImpl findGlobalType(QName name, final String chameleonNamespace, final String sourceNamespace) {
        name = this.compatName(name, chameleonNamespace);
        SchemaTypeImpl result = this._globalTypes.get(name);
        boolean foundOnLoader = false;
        if (result == null) {
            result = (SchemaTypeImpl)this._importingLoader.findType(name);
            foundOnLoader = (result != null);
        }
        if (!foundOnLoader && sourceNamespace != null) {
            this.registerDependency(sourceNamespace, name.getNamespaceURI());
        }
        return result;
    }
    
    SchemaTypeImpl findRedefinedGlobalType(QName name, final String chameleonNamespace, final SchemaTypeImpl redefinedBy) {
        final QName redefinedName = redefinedBy.getName();
        name = this.compatName(name, chameleonNamespace);
        if (name.equals(redefinedName)) {
            return this._redefinedGlobalTypes.get(redefinedBy);
        }
        SchemaTypeImpl result = this._globalTypes.get(name);
        if (result == null) {
            result = (SchemaTypeImpl)this._importingLoader.findType(name);
        }
        return result;
    }
    
    void addGlobalType(final SchemaTypeImpl type, final SchemaTypeImpl redefined) {
        if (type != null) {
            final QName name = type.getName();
            final SchemaContainer container = this.getContainer(name.getNamespaceURI());
            assert container != null && container == type.getContainer();
            if (redefined != null) {
                if (this._redefinedGlobalTypes.containsKey(redefined)) {
                    if (!this.ignoreMdef(name)) {
                        if (this._mdefAll) {
                            this.warning("sch-props-correct.2", new Object[] { "global type", QNameHelper.pretty(name), this._redefinedGlobalTypes.get(redefined).getSourceName() }, type.getParseObject());
                        }
                        else {
                            this.error("sch-props-correct.2", new Object[] { "global type", QNameHelper.pretty(name), this._redefinedGlobalTypes.get(redefined).getSourceName() }, type.getParseObject());
                        }
                    }
                }
                else {
                    this._redefinedGlobalTypes.put(redefined, type);
                    container.addRedefinedType(type.getRef());
                }
            }
            else if (this._globalTypes.containsKey(name)) {
                if (!this.ignoreMdef(name)) {
                    if (this._mdefAll) {
                        this.warning("sch-props-correct.2", new Object[] { "global type", QNameHelper.pretty(name), this._globalTypes.get(name).getSourceName() }, type.getParseObject());
                    }
                    else {
                        this.error("sch-props-correct.2", new Object[] { "global type", QNameHelper.pretty(name), this._globalTypes.get(name).getSourceName() }, type.getParseObject());
                    }
                }
            }
            else {
                this._globalTypes.put(name, type);
                container.addGlobalType(type.getRef());
                this.addSpelling(name, type);
            }
        }
    }
    
    private boolean ignoreMdef(final QName name) {
        return this._mdefNamespaces.contains(name.getNamespaceURI());
    }
    
    SchemaType[] globalTypes() {
        return (SchemaType[])this._globalTypes.values().toArray(new SchemaType[this._globalTypes.size()]);
    }
    
    SchemaType[] redefinedGlobalTypes() {
        return (SchemaType[])this._redefinedGlobalTypes.values().toArray(new SchemaType[this._redefinedGlobalTypes.size()]);
    }
    
    SchemaTypeImpl findDocumentType(QName name, final String chameleonNamespace, final String sourceNamespace) {
        name = this.compatName(name, chameleonNamespace);
        SchemaTypeImpl result = this._documentTypes.get(name);
        boolean foundOnLoader = false;
        if (result == null) {
            result = (SchemaTypeImpl)this._importingLoader.findDocumentType(name);
            foundOnLoader = (result != null);
        }
        if (!foundOnLoader && sourceNamespace != null) {
            this.registerDependency(sourceNamespace, name.getNamespaceURI());
        }
        return result;
    }
    
    void addDocumentType(final SchemaTypeImpl type, final QName name) {
        if (this._documentTypes.containsKey(name)) {
            if (!this.ignoreMdef(name)) {
                if (this._mdefAll) {
                    this.warning("sch-props-correct.2", new Object[] { "global element", QNameHelper.pretty(name), this._documentTypes.get(name).getSourceName() }, type.getParseObject());
                }
                else {
                    this.error("sch-props-correct.2", new Object[] { "global element", QNameHelper.pretty(name), this._documentTypes.get(name).getSourceName() }, type.getParseObject());
                }
            }
        }
        else {
            this._documentTypes.put(name, type);
            final SchemaContainer container = this.getContainer(name.getNamespaceURI());
            assert container != null && container == type.getContainer();
            container.addDocumentType(type.getRef());
        }
    }
    
    SchemaType[] documentTypes() {
        return (SchemaType[])this._documentTypes.values().toArray(new SchemaType[this._documentTypes.size()]);
    }
    
    SchemaTypeImpl findAttributeType(QName name, final String chameleonNamespace, final String sourceNamespace) {
        name = this.compatName(name, chameleonNamespace);
        SchemaTypeImpl result = this._attributeTypes.get(name);
        boolean foundOnLoader = false;
        if (result == null) {
            result = (SchemaTypeImpl)this._importingLoader.findAttributeType(name);
            foundOnLoader = (result != null);
        }
        if (!foundOnLoader && sourceNamespace != null) {
            this.registerDependency(sourceNamespace, name.getNamespaceURI());
        }
        return result;
    }
    
    void addAttributeType(final SchemaTypeImpl type, final QName name) {
        if (this._attributeTypes.containsKey(name)) {
            if (!this.ignoreMdef(name)) {
                if (this._mdefAll) {
                    this.warning("sch-props-correct.2", new Object[] { "global attribute", QNameHelper.pretty(name), this._attributeTypes.get(name).getSourceName() }, type.getParseObject());
                }
                else {
                    this.error("sch-props-correct.2", new Object[] { "global attribute", QNameHelper.pretty(name), this._attributeTypes.get(name).getSourceName() }, type.getParseObject());
                }
            }
        }
        else {
            this._attributeTypes.put(name, type);
            final SchemaContainer container = this.getContainer(name.getNamespaceURI());
            assert container != null && container == type.getContainer();
            container.addAttributeType(type.getRef());
        }
    }
    
    SchemaType[] attributeTypes() {
        return (SchemaType[])this._attributeTypes.values().toArray(new SchemaType[this._attributeTypes.size()]);
    }
    
    SchemaGlobalAttributeImpl findGlobalAttribute(QName name, final String chameleonNamespace, final String sourceNamespace) {
        name = this.compatName(name, chameleonNamespace);
        SchemaGlobalAttributeImpl result = this._globalAttributes.get(name);
        boolean foundOnLoader = false;
        if (result == null) {
            result = (SchemaGlobalAttributeImpl)this._importingLoader.findAttribute(name);
            foundOnLoader = (result != null);
        }
        if (!foundOnLoader && sourceNamespace != null) {
            this.registerDependency(sourceNamespace, name.getNamespaceURI());
        }
        return result;
    }
    
    void addGlobalAttribute(final SchemaGlobalAttributeImpl attribute) {
        if (attribute != null) {
            final QName name = attribute.getName();
            this._globalAttributes.put(name, attribute);
            this.addSpelling(name, attribute);
            final SchemaContainer container = this.getContainer(name.getNamespaceURI());
            assert container != null && container == attribute.getContainer();
            container.addGlobalAttribute(attribute.getRef());
        }
    }
    
    SchemaGlobalAttribute[] globalAttributes() {
        return (SchemaGlobalAttribute[])this._globalAttributes.values().toArray(new SchemaGlobalAttribute[this._globalAttributes.size()]);
    }
    
    SchemaGlobalElementImpl findGlobalElement(QName name, final String chameleonNamespace, final String sourceNamespace) {
        name = this.compatName(name, chameleonNamespace);
        SchemaGlobalElementImpl result = this._globalElements.get(name);
        boolean foundOnLoader = false;
        if (result == null) {
            result = (SchemaGlobalElementImpl)this._importingLoader.findElement(name);
            foundOnLoader = (result != null);
        }
        if (!foundOnLoader && sourceNamespace != null) {
            this.registerDependency(sourceNamespace, name.getNamespaceURI());
        }
        return result;
    }
    
    void addGlobalElement(final SchemaGlobalElementImpl element) {
        if (element != null) {
            final QName name = element.getName();
            this._globalElements.put(name, element);
            final SchemaContainer container = this.getContainer(name.getNamespaceURI());
            assert container != null && container == element.getContainer();
            container.addGlobalElement(element.getRef());
            this.addSpelling(name, element);
        }
    }
    
    SchemaGlobalElement[] globalElements() {
        return (SchemaGlobalElement[])this._globalElements.values().toArray(new SchemaGlobalElement[this._globalElements.size()]);
    }
    
    SchemaAttributeGroupImpl findAttributeGroup(QName name, final String chameleonNamespace, final String sourceNamespace) {
        name = this.compatName(name, chameleonNamespace);
        SchemaAttributeGroupImpl result = this._attributeGroups.get(name);
        boolean foundOnLoader = false;
        if (result == null) {
            result = (SchemaAttributeGroupImpl)this._importingLoader.findAttributeGroup(name);
            foundOnLoader = (result != null);
        }
        if (!foundOnLoader && sourceNamespace != null) {
            this.registerDependency(sourceNamespace, name.getNamespaceURI());
        }
        return result;
    }
    
    SchemaAttributeGroupImpl findRedefinedAttributeGroup(QName name, final String chameleonNamespace, final SchemaAttributeGroupImpl redefinedBy) {
        final QName redefinitionFor = redefinedBy.getName();
        name = this.compatName(name, chameleonNamespace);
        if (name.equals(redefinitionFor)) {
            return this._redefinedAttributeGroups.get(redefinedBy);
        }
        SchemaAttributeGroupImpl result = this._attributeGroups.get(name);
        if (result == null) {
            result = (SchemaAttributeGroupImpl)this._importingLoader.findAttributeGroup(name);
        }
        return result;
    }
    
    void addAttributeGroup(final SchemaAttributeGroupImpl attributeGroup, final SchemaAttributeGroupImpl redefined) {
        if (attributeGroup != null) {
            final QName name = attributeGroup.getName();
            final SchemaContainer container = this.getContainer(name.getNamespaceURI());
            assert container != null && container == attributeGroup.getContainer();
            if (redefined != null) {
                if (this._redefinedAttributeGroups.containsKey(redefined)) {
                    if (!this.ignoreMdef(name)) {
                        if (this._mdefAll) {
                            this.warning("sch-props-correct.2", new Object[] { "attribute group", QNameHelper.pretty(name), this._redefinedAttributeGroups.get(redefined).getSourceName() }, attributeGroup.getParseObject());
                        }
                        else {
                            this.error("sch-props-correct.2", new Object[] { "attribute group", QNameHelper.pretty(name), this._redefinedAttributeGroups.get(redefined).getSourceName() }, attributeGroup.getParseObject());
                        }
                    }
                }
                else {
                    this._redefinedAttributeGroups.put(redefined, attributeGroup);
                    container.addRedefinedAttributeGroup(attributeGroup.getRef());
                }
            }
            else if (this._attributeGroups.containsKey(name)) {
                if (!this.ignoreMdef(name)) {
                    if (this._mdefAll) {
                        this.warning("sch-props-correct.2", new Object[] { "attribute group", QNameHelper.pretty(name), this._attributeGroups.get(name).getSourceName() }, attributeGroup.getParseObject());
                    }
                    else {
                        this.error("sch-props-correct.2", new Object[] { "attribute group", QNameHelper.pretty(name), this._attributeGroups.get(name).getSourceName() }, attributeGroup.getParseObject());
                    }
                }
            }
            else {
                this._attributeGroups.put(attributeGroup.getName(), attributeGroup);
                this.addSpelling(attributeGroup.getName(), attributeGroup);
                container.addAttributeGroup(attributeGroup.getRef());
            }
        }
    }
    
    SchemaAttributeGroup[] attributeGroups() {
        return (SchemaAttributeGroup[])this._attributeGroups.values().toArray(new SchemaAttributeGroup[this._attributeGroups.size()]);
    }
    
    SchemaAttributeGroup[] redefinedAttributeGroups() {
        return (SchemaAttributeGroup[])this._redefinedAttributeGroups.values().toArray(new SchemaAttributeGroup[this._redefinedAttributeGroups.size()]);
    }
    
    SchemaModelGroupImpl findModelGroup(QName name, final String chameleonNamespace, final String sourceNamespace) {
        name = this.compatName(name, chameleonNamespace);
        SchemaModelGroupImpl result = this._modelGroups.get(name);
        boolean foundOnLoader = false;
        if (result == null) {
            result = (SchemaModelGroupImpl)this._importingLoader.findModelGroup(name);
            foundOnLoader = (result != null);
        }
        if (!foundOnLoader && sourceNamespace != null) {
            this.registerDependency(sourceNamespace, name.getNamespaceURI());
        }
        return result;
    }
    
    SchemaModelGroupImpl findRedefinedModelGroup(QName name, final String chameleonNamespace, final SchemaModelGroupImpl redefinedBy) {
        final QName redefinitionFor = redefinedBy.getName();
        name = this.compatName(name, chameleonNamespace);
        if (name.equals(redefinitionFor)) {
            return this._redefinedModelGroups.get(redefinedBy);
        }
        SchemaModelGroupImpl result = this._modelGroups.get(name);
        if (result == null) {
            result = (SchemaModelGroupImpl)this._importingLoader.findModelGroup(name);
        }
        return result;
    }
    
    void addModelGroup(final SchemaModelGroupImpl modelGroup, final SchemaModelGroupImpl redefined) {
        if (modelGroup != null) {
            final QName name = modelGroup.getName();
            final SchemaContainer container = this.getContainer(name.getNamespaceURI());
            assert container != null && container == modelGroup.getContainer();
            if (redefined != null) {
                if (this._redefinedModelGroups.containsKey(redefined)) {
                    if (!this.ignoreMdef(name)) {
                        if (this._mdefAll) {
                            this.warning("sch-props-correct.2", new Object[] { "model group", QNameHelper.pretty(name), this._redefinedModelGroups.get(redefined).getSourceName() }, modelGroup.getParseObject());
                        }
                        else {
                            this.error("sch-props-correct.2", new Object[] { "model group", QNameHelper.pretty(name), this._redefinedModelGroups.get(redefined).getSourceName() }, modelGroup.getParseObject());
                        }
                    }
                }
                else {
                    this._redefinedModelGroups.put(redefined, modelGroup);
                    container.addRedefinedModelGroup(modelGroup.getRef());
                }
            }
            else if (this._modelGroups.containsKey(name)) {
                if (!this.ignoreMdef(name)) {
                    if (this._mdefAll) {
                        this.warning("sch-props-correct.2", new Object[] { "model group", QNameHelper.pretty(name), this._modelGroups.get(name).getSourceName() }, modelGroup.getParseObject());
                    }
                    else {
                        this.error("sch-props-correct.2", new Object[] { "model group", QNameHelper.pretty(name), this._modelGroups.get(name).getSourceName() }, modelGroup.getParseObject());
                    }
                }
            }
            else {
                this._modelGroups.put(modelGroup.getName(), modelGroup);
                this.addSpelling(modelGroup.getName(), modelGroup);
                container.addModelGroup(modelGroup.getRef());
            }
        }
    }
    
    SchemaModelGroup[] modelGroups() {
        return (SchemaModelGroup[])this._modelGroups.values().toArray(new SchemaModelGroup[this._modelGroups.size()]);
    }
    
    SchemaModelGroup[] redefinedModelGroups() {
        return (SchemaModelGroup[])this._redefinedModelGroups.values().toArray(new SchemaModelGroup[this._redefinedModelGroups.size()]);
    }
    
    SchemaIdentityConstraintImpl findIdConstraint(QName name, final String chameleonNamespace, final String sourceNamespace) {
        name = this.compatName(name, chameleonNamespace);
        if (sourceNamespace != null) {
            this.registerDependency(sourceNamespace, name.getNamespaceURI());
        }
        return this._idConstraints.get(name);
    }
    
    void addIdConstraint(final SchemaIdentityConstraintImpl idc) {
        if (idc != null) {
            final QName name = idc.getName();
            final SchemaContainer container = this.getContainer(name.getNamespaceURI());
            assert container != null && container == idc.getContainer();
            if (this._idConstraints.containsKey(name)) {
                if (!this.ignoreMdef(name)) {
                    this.warning("sch-props-correct.2", new Object[] { "identity constraint", QNameHelper.pretty(name), this._idConstraints.get(name).getSourceName() }, idc.getParseObject());
                }
            }
            else {
                this._idConstraints.put(name, idc);
                this.addSpelling(idc.getName(), idc);
                container.addIdentityConstraint(idc.getRef());
            }
        }
    }
    
    SchemaIdentityConstraintImpl[] idConstraints() {
        return (SchemaIdentityConstraintImpl[])this._idConstraints.values().toArray(new SchemaIdentityConstraintImpl[this._idConstraints.size()]);
    }
    
    void addAnnotation(final SchemaAnnotationImpl ann, final String targetNamespace) {
        if (ann != null) {
            final SchemaContainer container = this.getContainer(targetNamespace);
            assert container != null && container == ann.getContainer();
            this._annotations.add(ann);
            container.addAnnotation(ann);
        }
    }
    
    List annotations() {
        return this._annotations;
    }
    
    boolean isProcessing(final Object obj) {
        return this._processingGroups.contains(obj);
    }
    
    void startProcessing(final Object obj) {
        assert !this._processingGroups.contains(obj);
        this._processingGroups.add(obj);
    }
    
    void finishProcessing(final Object obj) {
        assert this._processingGroups.contains(obj);
        this._processingGroups.remove(obj);
    }
    
    Object[] getCurrentProcessing() {
        return this._processingGroups.toArray();
    }
    
    Map typesByClassname() {
        return Collections.unmodifiableMap((Map<?, ?>)this._typesByClassname);
    }
    
    void addClassname(final String classname, final SchemaType type) {
        this._typesByClassname.put(classname, type);
    }
    
    public static void clearThreadLocals() {
        StscState.tl_stscStack.remove();
    }
    
    public static StscState start() {
        StscStack stscStack = StscState.tl_stscStack.get();
        if (stscStack == null) {
            stscStack = new StscStack();
            StscState.tl_stscStack.set(stscStack);
        }
        return stscStack.push();
    }
    
    public static StscState get() {
        return StscState.tl_stscStack.get().current;
    }
    
    public static void end() {
        final StscStack stscStack = StscState.tl_stscStack.get();
        stscStack.pop();
        if (stscStack.stack.size() == 0) {
            StscState.tl_stscStack.set(null);
        }
    }
    
    static XmlValueRef build_wsstring(final int wsr) {
        switch (wsr) {
            case 1: {
                return StscState.XMLSTR_PRESERVE;
            }
            case 2: {
                return StscState.XMLSTR_REPLACE;
            }
            case 3: {
                return StscState.XMLSTR_COLLAPSE;
            }
            default: {
                return null;
            }
        }
    }
    
    static XmlValueRef buildString(final String str) {
        if (str == null) {
            return null;
        }
        try {
            final XmlStringImpl i = new XmlStringImpl();
            i.set(str);
            i.setImmutable();
            return new XmlValueRef(i);
        }
        catch (final XmlValueOutOfRangeException e) {
            return null;
        }
    }
    
    public void notFoundError(final QName itemName, final int code, final XmlObject loc, final boolean recovered) {
        final String expectedName = QNameHelper.pretty(itemName);
        String found = null;
        String foundName = null;
        String sourceName = null;
        if (recovered) {
            ++this._recoveredErrors;
        }
        String expected = null;
        switch (code) {
            case 0: {
                expected = "type";
                break;
            }
            case 1: {
                expected = "element";
                break;
            }
            case 3: {
                expected = "attribute";
                break;
            }
            case 6: {
                expected = "model group";
                break;
            }
            case 4: {
                expected = "attribute group";
                break;
            }
            case 5: {
                expected = "identity constraint";
                break;
            }
            default: {
                assert false;
                expected = "definition";
                break;
            }
        }
        final SchemaComponent foundComponent = this.findSpelling(itemName);
        if (foundComponent != null) {
            final QName name = foundComponent.getName();
            if (name != null) {
                switch (foundComponent.getComponentType()) {
                    case 0: {
                        found = "type";
                        sourceName = foundComponent.getSourceName();
                        break;
                    }
                    case 1: {
                        found = "element";
                        sourceName = foundComponent.getSourceName();
                        break;
                    }
                    case 3: {
                        found = "attribute";
                        sourceName = foundComponent.getSourceName();
                        break;
                    }
                    case 4: {
                        found = "attribute group";
                        break;
                    }
                    case 6: {
                        found = "model group";
                        break;
                    }
                }
                if (sourceName != null) {
                    sourceName = sourceName.substring(sourceName.lastIndexOf(47) + 1);
                }
                if (!name.equals(itemName)) {
                    foundName = QNameHelper.pretty(name);
                }
            }
        }
        if (found == null) {
            this.error("src-resolve", new Object[] { expected, expectedName }, loc);
        }
        else {
            this.error("src-resolve.a", new Object[] { expected, expectedName, found, (foundName == null) ? new Integer(0) : new Integer(1), foundName, (sourceName == null) ? new Integer(0) : new Integer(1), sourceName }, loc);
        }
    }
    
    public String sourceNameForUri(final String uri) {
        return this._sourceForUri.get(uri);
    }
    
    public Map sourceCopyMap() {
        return Collections.unmodifiableMap((Map<?, ?>)this._sourceForUri);
    }
    
    public void setBaseUri(final URI uri) {
        this._baseURI = uri;
    }
    
    public String relativize(final String uri) {
        return this.relativize(uri, false);
    }
    
    public String computeSavedFilename(final String uri) {
        return this.relativize(uri, true);
    }
    
    private String relativize(String uri, final boolean forSavedFilename) {
        if (uri == null) {
            return null;
        }
        if (uri.startsWith("/")) {
            uri = "project://local" + uri.replace('\\', '/');
        }
        else {
            final int colon = uri.indexOf(58);
            if (colon <= 1 || !uri.substring(0, colon).matches("^\\w+$")) {
                uri = "project://local/" + uri.replace('\\', '/');
            }
        }
        if (this._baseURI != null) {
            try {
                final URI relative = this._baseURI.relativize(new URI(uri));
                if (!relative.isAbsolute()) {
                    return relative.toString();
                }
                uri = relative.toString();
            }
            catch (final URISyntaxException ex) {}
        }
        if (!forSavedFilename) {
            return uri;
        }
        final int lastslash = uri.lastIndexOf(47);
        final String dir = QNameHelper.hexsafe((lastslash == -1) ? "" : uri.substring(0, lastslash));
        final int question = uri.indexOf(63, lastslash + 1);
        if (question == -1) {
            return dir + "/" + uri.substring(lastslash + 1);
        }
        final String query = QNameHelper.hexsafe((question == -1) ? "" : uri.substring(question));
        if (query.startsWith("URI_SHA_1_")) {
            return dir + "/" + uri.substring(lastslash + 1, question);
        }
        return dir + "/" + uri.substring(lastslash + 1, question) + query;
    }
    
    public void addSourceUri(final String uri, String nameToUse) {
        if (uri == null) {
            return;
        }
        if (nameToUse == null) {
            nameToUse = this.computeSavedFilename(uri);
        }
        this._sourceForUri.put(uri, nameToUse);
    }
    
    public Collection getErrorListener() {
        return this._errorListener;
    }
    
    public SchemaTypeLoader getS4SLoader() {
        return this._s4sloader;
    }
    
    public File getSchemasDir() {
        return this._schemasDir;
    }
    
    public void setSchemasDir(final File _schemasDir) {
        this._schemasDir = _schemasDir;
    }
    
    static {
        CHAMELEON_INCLUDE_URI = new Object();
        StscState.tl_stscStack = new ThreadLocal();
        XMLSTR_PRESERVE = buildString("preserve");
        XMLSTR_REPLACE = buildString("preserve");
        XMLSTR_COLLAPSE = buildString("preserve");
        EMPTY_ST_ARRAY = new SchemaType[0];
        EMPTY_STREF_ARRAY = new SchemaType.Ref[0];
        FACETS_NONE = new XmlValueRef[] { null, null, null, null, null, null, null, null, null, null, null, null };
        FIXED_FACETS_NONE = new boolean[] { false, false, false, false, false, false, false, false, false, false, false, false };
        FACETS_WS_COLLAPSE = new XmlValueRef[] { null, null, null, null, null, null, null, null, null, build_wsstring(3), null, null };
        FIXED_FACETS_WS = new boolean[] { false, false, false, false, false, false, false, false, false, true, false, false };
        FACETS_UNION = StscState.FACETS_NONE;
        FIXED_FACETS_UNION = StscState.FIXED_FACETS_NONE;
        FACETS_LIST = StscState.FACETS_WS_COLLAPSE;
        FIXED_FACETS_LIST = StscState.FIXED_FACETS_WS;
    }
    
    private static final class StscStack
    {
        StscState current;
        ArrayList stack;
        
        private StscStack() {
            this.stack = new ArrayList();
        }
        
        final StscState push() {
            this.stack.add(this.current);
            return this.current = new StscState(null);
        }
        
        final void pop() {
            this.current = this.stack.get(this.stack.size() - 1);
            this.stack.remove(this.stack.size() - 1);
        }
    }
}
