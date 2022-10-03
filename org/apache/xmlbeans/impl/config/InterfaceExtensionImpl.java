package org.apache.xmlbeans.impl.config;

import org.apache.xmlbeans.impl.jam.JParameter;
import org.apache.xmlbeans.impl.jam.JMethod;
import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig;
import org.apache.xmlbeans.impl.jam.JamClassLoader;
import org.apache.xmlbeans.InterfaceExtension;

public class InterfaceExtensionImpl implements InterfaceExtension
{
    private NameSet _xbeanSet;
    private String _interfaceClassName;
    private String _delegateToClassName;
    private MethodSignatureImpl[] _methods;
    
    static InterfaceExtensionImpl newInstance(final JamClassLoader loader, final NameSet xbeanSet, final Extensionconfig.Interface intfXO) {
        final InterfaceExtensionImpl result = new InterfaceExtensionImpl();
        result._xbeanSet = xbeanSet;
        final JClass interfaceJClass = validateInterface(loader, intfXO.getName(), intfXO);
        if (interfaceJClass == null) {
            BindingConfigImpl.error("Interface '" + intfXO.getStaticHandler() + "' not found.", intfXO);
            return null;
        }
        result._interfaceClassName = interfaceJClass.getQualifiedName();
        result._delegateToClassName = intfXO.getStaticHandler();
        final JClass delegateJClass = validateClass(loader, result._delegateToClassName, intfXO);
        if (delegateJClass == null) {
            BindingConfigImpl.warning("Handler class '" + intfXO.getStaticHandler() + "' not found on classpath, skip validation.", intfXO);
            return result;
        }
        if (!result.validateMethods(interfaceJClass, delegateJClass, intfXO)) {
            return null;
        }
        return result;
    }
    
    private static JClass validateInterface(final JamClassLoader loader, final String intfStr, final XmlObject loc) {
        return validateJava(loader, intfStr, true, loc);
    }
    
    static JClass validateClass(final JamClassLoader loader, final String clsStr, final XmlObject loc) {
        return validateJava(loader, clsStr, false, loc);
    }
    
    static JClass validateJava(final JamClassLoader loader, final String clsStr, final boolean isInterface, final XmlObject loc) {
        if (loader == null) {
            return null;
        }
        final String ent = isInterface ? "Interface" : "Class";
        final JClass cls = loader.loadClass(clsStr);
        if (cls == null || cls.isUnresolvedType()) {
            BindingConfigImpl.error(ent + " '" + clsStr + "' not found.", loc);
            return null;
        }
        if ((isInterface && !cls.isInterface()) || (!isInterface && cls.isInterface())) {
            BindingConfigImpl.error("'" + clsStr + "' must be " + (isInterface ? "an interface" : "a class") + ".", loc);
        }
        if (!cls.isPublic()) {
            BindingConfigImpl.error(ent + " '" + clsStr + "' is not public.", loc);
        }
        return cls;
    }
    
    private boolean validateMethods(final JClass interfaceJClass, final JClass delegateJClass, final XmlObject loc) {
        boolean valid = true;
        final JMethod[] interfaceMethods = interfaceJClass.getMethods();
        this._methods = new MethodSignatureImpl[interfaceMethods.length];
        for (int i = 0; i < interfaceMethods.length; ++i) {
            final JMethod method = this.validateMethod(interfaceJClass, delegateJClass, interfaceMethods[i], loc);
            if (method != null) {
                this._methods[i] = new MethodSignatureImpl(this.getStaticHandler(), method);
            }
            else {
                valid = false;
            }
        }
        return valid;
    }
    
    private JMethod validateMethod(final JClass interfaceJClass, final JClass delegateJClass, final JMethod method, final XmlObject loc) {
        final String methodName = method.getSimpleName();
        final JParameter[] params = method.getParameters();
        final JClass returnType = method.getReturnType();
        final JClass[] delegateParams = new JClass[params.length + 1];
        delegateParams[0] = returnType.forName("org.apache.xmlbeans.XmlObject");
        for (int i = 1; i < delegateParams.length; ++i) {
            delegateParams[i] = params[i - 1].getType();
        }
        JMethod handlerMethod = null;
        handlerMethod = getMethod(delegateJClass, methodName, delegateParams);
        if (handlerMethod == null) {
            BindingConfigImpl.error("Handler class '" + delegateJClass.getQualifiedName() + "' does not contain method " + methodName + "(" + listTypes(delegateParams) + ")", loc);
            return null;
        }
        final JClass[] intfExceptions = method.getExceptionTypes();
        final JClass[] delegateExceptions = handlerMethod.getExceptionTypes();
        if (delegateExceptions.length != intfExceptions.length) {
            BindingConfigImpl.error("Handler method '" + delegateJClass.getQualifiedName() + "." + methodName + "(" + listTypes(delegateParams) + ")' must declare the same exceptions as the interface method '" + interfaceJClass.getQualifiedName() + "." + methodName + "(" + listTypes(params), loc);
            return null;
        }
        for (int j = 0; j < delegateExceptions.length; ++j) {
            if (delegateExceptions[j] != intfExceptions[j]) {
                BindingConfigImpl.error("Handler method '" + delegateJClass.getQualifiedName() + "." + methodName + "(" + listTypes(delegateParams) + ")' must declare the same exceptions as the interface method '" + interfaceJClass.getQualifiedName() + "." + methodName + "(" + listTypes(params), loc);
                return null;
            }
        }
        if (!handlerMethod.isPublic() || !handlerMethod.isStatic()) {
            BindingConfigImpl.error("Method '" + delegateJClass.getQualifiedName() + "." + methodName + "(" + listTypes(delegateParams) + ")' must be declared public and static.", loc);
            return null;
        }
        if (!returnType.equals(handlerMethod.getReturnType())) {
            BindingConfigImpl.error("Return type for method '" + handlerMethod.getReturnType() + " " + delegateJClass.getQualifiedName() + "." + methodName + "(" + listTypes(delegateParams) + ")' does not match the return type of the interface method :'" + returnType + "'.", loc);
            return null;
        }
        return method;
    }
    
    static JMethod getMethod(final JClass cls, final String name, final JClass[] paramTypes) {
        final JMethod[] methods = cls.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            final JMethod method = methods[i];
            if (name.equals(method.getSimpleName())) {
                final JParameter[] mParams = method.getParameters();
                if (mParams.length == paramTypes.length) {
                    for (int j = 0; j < mParams.length; ++j) {
                        final JParameter mParam = mParams[j];
                        if (!mParam.getType().equals(paramTypes[j])) {}
                    }
                    return method;
                }
            }
        }
        return null;
    }
    
    private static String listTypes(final JClass[] types) {
        final StringBuffer result = new StringBuffer();
        for (int i = 0; i < types.length; ++i) {
            final JClass type = types[i];
            if (i > 0) {
                result.append(", ");
            }
            result.append(emitType(type));
        }
        return result.toString();
    }
    
    private static String listTypes(final JParameter[] params) {
        final StringBuffer result = new StringBuffer();
        for (int i = 0; i < params.length; ++i) {
            final JClass type = params[i].getType();
            if (i > 0) {
                result.append(", ");
            }
            result.append(emitType(type));
        }
        return result.toString();
    }
    
    public static String emitType(final JClass cls) {
        if (cls.isArrayType()) {
            return emitType(cls.getArrayComponentType()) + "[]";
        }
        return cls.getQualifiedName().replace('$', '.');
    }
    
    public boolean contains(final String fullJavaName) {
        return this._xbeanSet.contains(fullJavaName);
    }
    
    @Override
    public String getStaticHandler() {
        return this._delegateToClassName;
    }
    
    @Override
    public String getInterface() {
        return this._interfaceClassName;
    }
    
    @Override
    public MethodSignature[] getMethods() {
        return this._methods;
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("  static handler: ").append(this._delegateToClassName).append("\n");
        buf.append("  interface: ").append(this._interfaceClassName).append("\n");
        buf.append("  name set: ").append(this._xbeanSet).append("\n");
        for (int i = 0; i < this._methods.length; ++i) {
            buf.append("  method[").append(i).append("]=").append(this._methods[i]).append("\n");
        }
        return buf.toString();
    }
    
    static class MethodSignatureImpl implements MethodSignature
    {
        private String _intfName;
        private final int NOTINITIALIZED = -1;
        private int _hashCode;
        private String _signature;
        private String _name;
        private String _return;
        private String[] _params;
        private String[] _exceptions;
        
        MethodSignatureImpl(final String intfName, final JMethod method) {
            this._hashCode = -1;
            if (intfName == null || method == null) {
                throw new IllegalArgumentException("Interface: " + intfName + " method: " + method);
            }
            this._intfName = intfName;
            this._hashCode = -1;
            this._signature = null;
            this._name = method.getSimpleName();
            this._return = method.getReturnType().getQualifiedName().replace('$', '.');
            final JParameter[] paramTypes = method.getParameters();
            this._params = new String[paramTypes.length];
            for (int i = 0; i < paramTypes.length; ++i) {
                this._params[i] = paramTypes[i].getType().getQualifiedName().replace('$', '.');
            }
            final JClass[] exceptionTypes = method.getExceptionTypes();
            this._exceptions = new String[exceptionTypes.length];
            for (int j = 0; j < exceptionTypes.length; ++j) {
                this._exceptions[j] = exceptionTypes[j].getQualifiedName().replace('$', '.');
            }
        }
        
        String getInterfaceName() {
            return this._intfName;
        }
        
        @Override
        public String getName() {
            return this._name;
        }
        
        @Override
        public String getReturnType() {
            return this._return;
        }
        
        @Override
        public String[] getParameterTypes() {
            return this._params;
        }
        
        @Override
        public String[] getExceptionTypes() {
            return this._exceptions;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof MethodSignatureImpl)) {
                return false;
            }
            final MethodSignatureImpl ms = (MethodSignatureImpl)o;
            if (!ms.getName().equals(this.getName())) {
                return false;
            }
            final String[] params = this.getParameterTypes();
            final String[] msParams = ms.getParameterTypes();
            if (msParams.length != params.length) {
                return false;
            }
            for (int i = 0; i < params.length; ++i) {
                if (!msParams[i].equals(params[i])) {
                    return false;
                }
            }
            return this._intfName.equals(ms._intfName);
        }
        
        @Override
        public int hashCode() {
            if (this._hashCode != -1) {
                return this._hashCode;
            }
            int hash = this.getName().hashCode();
            final String[] params = this.getParameterTypes();
            for (int i = 0; i < params.length; ++i) {
                hash *= 19;
                hash += params[i].hashCode();
            }
            hash += 21 * this._intfName.hashCode();
            return this._hashCode = hash;
        }
        
        String getSignature() {
            if (this._signature != null) {
                return this._signature;
            }
            final StringBuffer sb = new StringBuffer(60);
            sb.append(this._name).append("(");
            for (int i = 0; i < this._params.length; ++i) {
                sb.append((i == 0) ? "" : " ,").append(this._params[i]);
            }
            sb.append(")");
            return this._signature = sb.toString();
        }
        
        @Override
        public String toString() {
            final StringBuffer buf = new StringBuffer();
            buf.append(this.getReturnType()).append(" ").append(this.getSignature());
            return buf.toString();
        }
    }
}
