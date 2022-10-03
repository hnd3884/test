package org.apache.xmlbeans.impl.config;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig;
import org.apache.xmlbeans.impl.jam.JamClassLoader;
import org.apache.xmlbeans.impl.jam.JMethod;
import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.PrePostExtension;

public class PrePostExtensionImpl implements PrePostExtension
{
    private static JClass[] PARAMTYPES_PREPOST;
    private static final String[] PARAMTYPES_STRING;
    private static final String SIGNATURE;
    private NameSet _xbeanSet;
    private JClass _delegateToClass;
    private String _delegateToClassName;
    private JMethod _preSet;
    private JMethod _postSet;
    
    static PrePostExtensionImpl newInstance(final JamClassLoader jamLoader, final NameSet xbeanSet, final Extensionconfig.PrePostSet prePostXO) {
        if (prePostXO == null) {
            return null;
        }
        final PrePostExtensionImpl result = new PrePostExtensionImpl();
        result._xbeanSet = xbeanSet;
        result._delegateToClassName = prePostXO.getStaticHandler();
        result._delegateToClass = InterfaceExtensionImpl.validateClass(jamLoader, result._delegateToClassName, prePostXO);
        if (result._delegateToClass == null) {
            BindingConfigImpl.warning("Handler class '" + prePostXO.getStaticHandler() + "' not found on classpath, skip validation.", prePostXO);
            return result;
        }
        if (!result.lookAfterPreAndPost(jamLoader, prePostXO)) {
            return null;
        }
        return result;
    }
    
    private boolean lookAfterPreAndPost(final JamClassLoader jamLoader, final XmlObject loc) {
        assert this._delegateToClass != null : "Delegate to class handler expected.";
        boolean valid = true;
        this.initParamPrePost(jamLoader);
        this._preSet = InterfaceExtensionImpl.getMethod(this._delegateToClass, "preSet", PrePostExtensionImpl.PARAMTYPES_PREPOST);
        if (this._preSet == null) {}
        if (this._preSet != null && !this._preSet.getReturnType().equals(jamLoader.loadClass("boolean"))) {
            BindingConfigImpl.warning("Method '" + this._delegateToClass.getSimpleName() + ".preSet" + PrePostExtensionImpl.SIGNATURE + "' " + "should return boolean to be considered for a preSet handler.", loc);
            this._preSet = null;
        }
        this._postSet = InterfaceExtensionImpl.getMethod(this._delegateToClass, "postSet", PrePostExtensionImpl.PARAMTYPES_PREPOST);
        if (this._postSet == null) {}
        if (this._preSet == null && this._postSet == null) {
            BindingConfigImpl.error("prePostSet handler specified '" + this._delegateToClass.getSimpleName() + "' but no preSet" + PrePostExtensionImpl.SIGNATURE + " or " + "postSet" + PrePostExtensionImpl.SIGNATURE + " methods found.", loc);
            valid = false;
        }
        return valid;
    }
    
    private void initParamPrePost(final JamClassLoader jamLoader) {
        if (PrePostExtensionImpl.PARAMTYPES_PREPOST == null) {
            PrePostExtensionImpl.PARAMTYPES_PREPOST = new JClass[PrePostExtensionImpl.PARAMTYPES_STRING.length];
            for (int i = 0; i < PrePostExtensionImpl.PARAMTYPES_PREPOST.length; ++i) {
                PrePostExtensionImpl.PARAMTYPES_PREPOST[i] = jamLoader.loadClass(PrePostExtensionImpl.PARAMTYPES_STRING[i]);
                if (PrePostExtensionImpl.PARAMTYPES_PREPOST[i] == null) {
                    throw new IllegalStateException("JAM should have access to the following types " + PrePostExtensionImpl.SIGNATURE);
                }
            }
        }
    }
    
    public NameSet getNameSet() {
        return this._xbeanSet;
    }
    
    public boolean contains(final String fullJavaName) {
        return this._xbeanSet.contains(fullJavaName);
    }
    
    @Override
    public boolean hasPreCall() {
        return this._preSet != null;
    }
    
    @Override
    public boolean hasPostCall() {
        return this._postSet != null;
    }
    
    @Override
    public String getStaticHandler() {
        return this._delegateToClassName;
    }
    
    public String getHandlerNameForJavaSource() {
        if (this._delegateToClass == null) {
            return null;
        }
        return InterfaceExtensionImpl.emitType(this._delegateToClass);
    }
    
    boolean hasNameSetIntersection(final PrePostExtensionImpl ext) {
        return !NameSet.EMPTY.equals(this._xbeanSet.intersect(ext._xbeanSet));
    }
    
    static {
        PrePostExtensionImpl.PARAMTYPES_PREPOST = null;
        PARAMTYPES_STRING = new String[] { "int", "org.apache.xmlbeans.XmlObject", "javax.xml.namespace.QName", "boolean", "int" };
        String sig = "(";
        for (int i = 0; i < PrePostExtensionImpl.PARAMTYPES_STRING.length; ++i) {
            final String t = PrePostExtensionImpl.PARAMTYPES_STRING[i];
            if (i != 0) {
                sig += ", ";
            }
            sig += t;
        }
        SIGNATURE = sig + ")";
    }
}
