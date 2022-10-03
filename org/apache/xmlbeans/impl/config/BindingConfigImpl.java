package org.apache.xmlbeans.impl.config;

import org.apache.xmlbeans.impl.jam.JamService;
import org.apache.xmlbeans.impl.jam.JamServiceParams;
import java.io.IOException;
import org.apache.xmlbeans.impl.jam.JamServiceFactory;
import org.apache.xmlbeans.PrePostExtension;
import org.apache.xmlbeans.InterfaceExtension;
import org.apache.xmlbeans.UserType;
import org.apache.xmlbeans.impl.schema.StscState;
import org.apache.xmlbeans.impl.jam.JamClassLoader;
import java.util.Iterator;
import org.apache.xmlbeans.XmlObject;
import java.util.HashMap;
import org.apache.xmlbeans.impl.xb.xmlconfig.Usertypeconfig;
import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xmlconfig.Qnameconfig;
import org.apache.xmlbeans.impl.xb.xmlconfig.Nsconfig;
import org.apache.xmlbeans.impl.xb.xmlconfig.Qnametargetenum;
import java.util.LinkedHashMap;
import java.io.File;
import org.apache.xmlbeans.impl.xb.xmlconfig.ConfigDocument;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.xmlbeans.BindingConfig;

public class BindingConfigImpl extends BindingConfig
{
    private Map _packageMap;
    private Map _prefixMap;
    private Map _suffixMap;
    private Map _packageMapByUriPrefix;
    private Map _prefixMapByUriPrefix;
    private Map _suffixMapByUriPrefix;
    private Map _qnameTypeMap;
    private Map _qnameDocTypeMap;
    private Map _qnameElemMap;
    private Map _qnameAttMap;
    private List _interfaceExtensions;
    private List _prePostExtensions;
    private Map _userTypes;
    
    private BindingConfigImpl() {
        this._packageMap = Collections.EMPTY_MAP;
        this._prefixMap = Collections.EMPTY_MAP;
        this._suffixMap = Collections.EMPTY_MAP;
        this._packageMapByUriPrefix = Collections.EMPTY_MAP;
        this._prefixMapByUriPrefix = Collections.EMPTY_MAP;
        this._suffixMapByUriPrefix = Collections.EMPTY_MAP;
        this._qnameTypeMap = Collections.EMPTY_MAP;
        this._qnameDocTypeMap = Collections.EMPTY_MAP;
        this._qnameElemMap = Collections.EMPTY_MAP;
        this._qnameAttMap = Collections.EMPTY_MAP;
        this._interfaceExtensions = new ArrayList();
        this._prePostExtensions = new ArrayList();
        this._userTypes = Collections.EMPTY_MAP;
    }
    
    public static BindingConfig forConfigDocuments(final ConfigDocument.Config[] configs, final File[] javaFiles, final File[] classpath) {
        return new BindingConfigImpl(configs, javaFiles, classpath);
    }
    
    private BindingConfigImpl(final ConfigDocument.Config[] configs, final File[] javaFiles, final File[] classpath) {
        this._packageMap = new LinkedHashMap();
        this._prefixMap = new LinkedHashMap();
        this._suffixMap = new LinkedHashMap();
        this._packageMapByUriPrefix = new LinkedHashMap();
        this._prefixMapByUriPrefix = new LinkedHashMap();
        this._suffixMapByUriPrefix = new LinkedHashMap();
        this._qnameTypeMap = new LinkedHashMap();
        this._qnameDocTypeMap = new LinkedHashMap();
        this._qnameElemMap = new LinkedHashMap();
        this._qnameAttMap = new LinkedHashMap();
        this._interfaceExtensions = new ArrayList();
        this._prePostExtensions = new ArrayList();
        this._userTypes = new LinkedHashMap();
        for (int i = 0; i < configs.length; ++i) {
            final ConfigDocument.Config config = configs[i];
            final Nsconfig[] nsa = config.getNamespaceArray();
            for (int j = 0; j < nsa.length; ++j) {
                recordNamespaceSetting(nsa[j].getUri(), nsa[j].getPackage(), this._packageMap);
                recordNamespaceSetting(nsa[j].getUri(), nsa[j].getPrefix(), this._prefixMap);
                recordNamespaceSetting(nsa[j].getUri(), nsa[j].getSuffix(), this._suffixMap);
                recordNamespacePrefixSetting(nsa[j].getUriprefix(), nsa[j].getPackage(), this._packageMapByUriPrefix);
                recordNamespacePrefixSetting(nsa[j].getUriprefix(), nsa[j].getPrefix(), this._prefixMapByUriPrefix);
                recordNamespacePrefixSetting(nsa[j].getUriprefix(), nsa[j].getSuffix(), this._suffixMapByUriPrefix);
            }
            final Qnameconfig[] qnc = config.getQnameArray();
            for (int k = 0; k < qnc.length; ++k) {
                final List applyto = qnc[k].xgetTarget().xgetListValue();
                final QName name = qnc[k].getName();
                final String javaname = qnc[k].getJavaname();
                for (int l = 0; l < applyto.size(); ++l) {
                    final Qnametargetenum a = applyto.get(l);
                    switch (a.enumValue().intValue()) {
                        case 1: {
                            this._qnameTypeMap.put(name, javaname);
                            break;
                        }
                        case 2: {
                            this._qnameDocTypeMap.put(name, javaname);
                            break;
                        }
                        case 3: {
                            this._qnameElemMap.put(name, javaname);
                            break;
                        }
                        case 4: {
                            this._qnameAttMap.put(name, javaname);
                            break;
                        }
                    }
                }
            }
            final Extensionconfig[] ext = config.getExtensionArray();
            for (int m = 0; m < ext.length; ++m) {
                this.recordExtensionSetting(javaFiles, classpath, ext[m]);
            }
            final Usertypeconfig[] utypes = config.getUsertypeArray();
            for (int j2 = 0; j2 < utypes.length; ++j2) {
                this.recordUserTypeSetting(javaFiles, classpath, utypes[j2]);
            }
        }
        this.secondPhaseValidation();
    }
    
    void addInterfaceExtension(final InterfaceExtensionImpl ext) {
        if (ext == null) {
            return;
        }
        this._interfaceExtensions.add(ext);
    }
    
    void addPrePostExtension(final PrePostExtensionImpl ext) {
        if (ext == null) {
            return;
        }
        this._prePostExtensions.add(ext);
    }
    
    void secondPhaseValidation() {
        final Map methodSignatures = new HashMap();
        for (int i = 0; i < this._interfaceExtensions.size(); ++i) {
            final InterfaceExtensionImpl interfaceExtension = this._interfaceExtensions.get(i);
            final InterfaceExtensionImpl.MethodSignatureImpl[] methods = (InterfaceExtensionImpl.MethodSignatureImpl[])interfaceExtension.getMethods();
            for (int j = 0; j < methods.length; ++j) {
                final InterfaceExtensionImpl.MethodSignatureImpl ms = methods[j];
                if (methodSignatures.containsKey(methods[j])) {
                    final InterfaceExtensionImpl.MethodSignatureImpl ms2 = methodSignatures.get(methods[j]);
                    if (!ms.getReturnType().equals(ms2.getReturnType())) {
                        error("Colliding methods '" + ms.getSignature() + "' in interfaces " + ms.getInterfaceName() + " and " + ms2.getInterfaceName() + ".", null);
                    }
                    return;
                }
                methodSignatures.put(methods[j], methods[j]);
            }
        }
        for (int i = 0; i < this._prePostExtensions.size() - 1; ++i) {
            final PrePostExtensionImpl a = this._prePostExtensions.get(i);
            for (int k = 1; k < this._prePostExtensions.size(); ++k) {
                final PrePostExtensionImpl b = this._prePostExtensions.get(k);
                if (a.hasNameSetIntersection(b)) {
                    error("The applicable domain for handler '" + a.getHandlerNameForJavaSource() + "' intersects with the one for '" + b.getHandlerNameForJavaSource() + "'.", null);
                }
            }
        }
    }
    
    private static void recordNamespaceSetting(final Object key, final String value, final Map result) {
        if (value == null) {
            return;
        }
        if (key == null) {
            result.put("", value);
        }
        else if (key instanceof String && "##any".equals(key)) {
            result.put(key, value);
        }
        else if (key instanceof List) {
            for (String uri : (List)key) {
                if ("##local".equals(uri)) {
                    uri = "";
                }
                result.put(uri, value);
            }
        }
    }
    
    private static void recordNamespacePrefixSetting(final List list, final String value, final Map result) {
        if (value == null) {
            return;
        }
        if (list == null) {
            return;
        }
        final Iterator i = list.iterator();
        while (i.hasNext()) {
            result.put(i.next(), value);
        }
    }
    
    private void recordExtensionSetting(final File[] javaFiles, final File[] classpath, final Extensionconfig ext) {
        NameSet xbeanSet = null;
        final Object key = ext.getFor();
        if (key instanceof String && "*".equals(key)) {
            xbeanSet = NameSet.EVERYTHING;
        }
        else if (key instanceof List) {
            final NameSetBuilder xbeanSetBuilder = new NameSetBuilder();
            for (final String xbeanName : (List)key) {
                xbeanSetBuilder.add(xbeanName);
            }
            xbeanSet = xbeanSetBuilder.toNameSet();
        }
        if (xbeanSet == null) {
            error("Invalid value of attribute 'for' : '" + key + "'.", ext);
        }
        final Extensionconfig.Interface[] intfXO = ext.getInterfaceArray();
        final Extensionconfig.PrePostSet ppXO = ext.getPrePostSet();
        if (intfXO.length > 0 || ppXO != null) {
            final JamClassLoader jamLoader = this.getJamLoader(javaFiles, classpath);
            for (int j = 0; j < intfXO.length; ++j) {
                this.addInterfaceExtension(InterfaceExtensionImpl.newInstance(jamLoader, xbeanSet, intfXO[j]));
            }
            this.addPrePostExtension(PrePostExtensionImpl.newInstance(jamLoader, xbeanSet, ppXO));
        }
    }
    
    private void recordUserTypeSetting(final File[] javaFiles, final File[] classpath, final Usertypeconfig usertypeconfig) {
        final JamClassLoader jamLoader = this.getJamLoader(javaFiles, classpath);
        final UserTypeImpl userType = UserTypeImpl.newInstance(jamLoader, usertypeconfig);
        this._userTypes.put(userType.getName(), userType);
    }
    
    private String lookup(final Map map, final Map mapByUriPrefix, String uri) {
        if (uri == null) {
            uri = "";
        }
        String result = map.get(uri);
        if (result != null) {
            return result;
        }
        if (mapByUriPrefix != null) {
            result = this.lookupByUriPrefix(mapByUriPrefix, uri);
            if (result != null) {
                return result;
            }
        }
        return map.get("##any");
    }
    
    private String lookupByUriPrefix(final Map mapByUriPrefix, final String uri) {
        if (uri == null) {
            return null;
        }
        if (!mapByUriPrefix.isEmpty()) {
            String uriprefix = null;
            for (final String nextprefix : mapByUriPrefix.keySet()) {
                if (uriprefix != null && nextprefix.length() < uriprefix.length()) {
                    continue;
                }
                if (!uri.startsWith(nextprefix)) {
                    continue;
                }
                uriprefix = nextprefix;
            }
            if (uriprefix != null) {
                return mapByUriPrefix.get(uriprefix);
            }
        }
        return null;
    }
    
    static void warning(final String s, final XmlObject xo) {
        StscState.get().error(s, 1, xo);
    }
    
    static void error(final String s, final XmlObject xo) {
        StscState.get().error(s, 0, xo);
    }
    
    @Override
    public String lookupPackageForNamespace(final String uri) {
        return this.lookup(this._packageMap, this._packageMapByUriPrefix, uri);
    }
    
    @Override
    public String lookupPrefixForNamespace(final String uri) {
        return this.lookup(this._prefixMap, this._prefixMapByUriPrefix, uri);
    }
    
    @Override
    public String lookupSuffixForNamespace(final String uri) {
        return this.lookup(this._suffixMap, this._suffixMapByUriPrefix, uri);
    }
    
    @Override
    @Deprecated
    public String lookupJavanameForQName(final QName qname) {
        final String result = this._qnameTypeMap.get(qname);
        if (result != null) {
            return result;
        }
        return this._qnameDocTypeMap.get(qname);
    }
    
    @Override
    public String lookupJavanameForQName(final QName qname, final int kind) {
        switch (kind) {
            case 1: {
                return this._qnameTypeMap.get(qname);
            }
            case 2: {
                return this._qnameDocTypeMap.get(qname);
            }
            case 3: {
                return this._qnameElemMap.get(qname);
            }
            case 4: {
                return this._qnameAttMap.get(qname);
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public UserType lookupUserTypeForQName(final QName qname) {
        if (qname == null) {
            return null;
        }
        return this._userTypes.get(qname);
    }
    
    @Override
    public InterfaceExtension[] getInterfaceExtensions() {
        return this._interfaceExtensions.toArray(new InterfaceExtension[this._interfaceExtensions.size()]);
    }
    
    @Override
    public InterfaceExtension[] getInterfaceExtensions(final String fullJavaName) {
        final List result = new ArrayList();
        for (int i = 0; i < this._interfaceExtensions.size(); ++i) {
            final InterfaceExtensionImpl intfExt = this._interfaceExtensions.get(i);
            if (intfExt.contains(fullJavaName)) {
                result.add(intfExt);
            }
        }
        return result.toArray(new InterfaceExtension[result.size()]);
    }
    
    @Override
    public PrePostExtension[] getPrePostExtensions() {
        return this._prePostExtensions.toArray(new PrePostExtension[this._prePostExtensions.size()]);
    }
    
    @Override
    public PrePostExtension getPrePostExtension(final String fullJavaName) {
        for (int i = 0; i < this._prePostExtensions.size(); ++i) {
            final PrePostExtensionImpl prePostExt = this._prePostExtensions.get(i);
            if (prePostExt.contains(fullJavaName)) {
                return prePostExt;
            }
        }
        return null;
    }
    
    private JamClassLoader getJamLoader(final File[] javaFiles, final File[] classpath) {
        final JamServiceFactory jf = JamServiceFactory.getInstance();
        final JamServiceParams params = jf.createServiceParams();
        params.set14WarningsEnabled(false);
        params.setShowWarnings(false);
        if (javaFiles != null) {
            for (int i = 0; i < javaFiles.length; ++i) {
                params.includeSourceFile(javaFiles[i]);
            }
        }
        params.addClassLoader(this.getClass().getClassLoader());
        if (classpath != null) {
            for (int i = 0; i < classpath.length; ++i) {
                params.addClasspath(classpath[i]);
            }
        }
        JamService service;
        try {
            service = jf.createService(params);
        }
        catch (final IOException ioe) {
            error("Error when accessing .java files.", null);
            return null;
        }
        return service.getClassLoader();
    }
}
