package org.apache.tomcat.util.modeler.modules;

import java.io.File;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.apache.juli.logging.LogFactory;
import java.util.Iterator;
import java.util.Enumeration;
import org.apache.tomcat.util.modeler.ParameterInfo;
import org.apache.tomcat.util.modeler.OperationInfo;
import java.util.Map;
import org.apache.tomcat.util.modeler.AttributeInfo;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import org.apache.tomcat.util.modeler.ManagedBean;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.management.ObjectName;
import java.util.List;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.juli.logging.Log;

public class MbeansDescriptorsIntrospectionSource extends ModelerSource
{
    private static final Log log;
    private Registry registry;
    private String type;
    private final List<ObjectName> mbeans;
    private static final Hashtable<String, String> specialMethods;
    private static final Class<?>[] supportedTypes;
    
    public MbeansDescriptorsIntrospectionSource() {
        this.mbeans = new ArrayList<ObjectName>();
    }
    
    public void setRegistry(final Registry reg) {
        this.registry = reg;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public void setSource(final Object source) {
        this.source = source;
    }
    
    @Override
    public List<ObjectName> loadDescriptors(final Registry registry, final String type, final Object source) throws Exception {
        this.setRegistry(registry);
        this.setType(type);
        this.setSource(source);
        this.execute();
        return this.mbeans;
    }
    
    public void execute() throws Exception {
        if (this.registry == null) {
            this.registry = Registry.getRegistry(null, null);
        }
        try {
            final ManagedBean managed = this.createManagedBean(this.registry, null, (Class<?>)this.source, this.type);
            if (managed == null) {
                return;
            }
            managed.setName(this.type);
            this.registry.addManagedBean(managed);
        }
        catch (final Exception ex) {
            MbeansDescriptorsIntrospectionSource.log.error((Object)MbeansDescriptorsIntrospectionSource.sm.getString("modules.readDescriptorsError"), (Throwable)ex);
        }
    }
    
    private boolean supportedType(final Class<?> ret) {
        for (final Class<?> supportedType : MbeansDescriptorsIntrospectionSource.supportedTypes) {
            if (ret == supportedType) {
                return true;
            }
        }
        return this.isBeanCompatible(ret);
    }
    
    private boolean isBeanCompatible(final Class<?> javaType) {
        if (javaType.isArray() || javaType.isPrimitive()) {
            return false;
        }
        if (javaType.getName().startsWith("java.") || javaType.getName().startsWith("javax.")) {
            return false;
        }
        try {
            javaType.getConstructor((Class<?>[])new Class[0]);
        }
        catch (final NoSuchMethodException e) {
            return false;
        }
        final Class<?> superClass = javaType.getSuperclass();
        return superClass == null || superClass == Object.class || superClass == Exception.class || superClass == Throwable.class || this.isBeanCompatible(superClass);
    }
    
    private void initMethods(final Class<?> realClass, final Method[] methods, final Hashtable<String, Method> attMap, final Hashtable<String, Method> getAttMap, final Hashtable<String, Method> setAttMap, final Hashtable<String, Method> invokeAttMap) {
        for (final Method method : methods) {
            String name = method.getName();
            if (!Modifier.isStatic(method.getModifiers())) {
                if (!Modifier.isPublic(method.getModifiers())) {
                    if (MbeansDescriptorsIntrospectionSource.log.isDebugEnabled()) {
                        MbeansDescriptorsIntrospectionSource.log.debug((Object)("Not public " + method));
                    }
                }
                else if (method.getDeclaringClass() != Object.class) {
                    final Class<?>[] params = method.getParameterTypes();
                    if (name.startsWith("get") && params.length == 0) {
                        final Class<?> ret = method.getReturnType();
                        if (!this.supportedType(ret)) {
                            if (MbeansDescriptorsIntrospectionSource.log.isDebugEnabled()) {
                                MbeansDescriptorsIntrospectionSource.log.debug((Object)("Unsupported type " + method));
                            }
                        }
                        else {
                            name = unCapitalize(name.substring(3));
                            getAttMap.put(name, method);
                            attMap.put(name, method);
                        }
                    }
                    else if (name.startsWith("is") && params.length == 0) {
                        final Class<?> ret = method.getReturnType();
                        if (Boolean.TYPE != ret) {
                            if (MbeansDescriptorsIntrospectionSource.log.isDebugEnabled()) {
                                MbeansDescriptorsIntrospectionSource.log.debug((Object)("Unsupported type " + method + " " + ret));
                            }
                        }
                        else {
                            name = unCapitalize(name.substring(2));
                            getAttMap.put(name, method);
                            attMap.put(name, method);
                        }
                    }
                    else if (name.startsWith("set") && params.length == 1) {
                        if (!this.supportedType(params[0])) {
                            if (MbeansDescriptorsIntrospectionSource.log.isDebugEnabled()) {
                                MbeansDescriptorsIntrospectionSource.log.debug((Object)("Unsupported type " + method + " " + params[0]));
                            }
                        }
                        else {
                            name = unCapitalize(name.substring(3));
                            setAttMap.put(name, method);
                            attMap.put(name, method);
                        }
                    }
                    else if (params.length == 0) {
                        if (MbeansDescriptorsIntrospectionSource.specialMethods.get(method.getName()) == null) {
                            invokeAttMap.put(name, method);
                        }
                    }
                    else {
                        boolean supported = true;
                        for (final Class<?> param : params) {
                            if (!this.supportedType(param)) {
                                supported = false;
                                break;
                            }
                        }
                        if (supported) {
                            invokeAttMap.put(name, method);
                        }
                    }
                }
            }
        }
    }
    
    public ManagedBean createManagedBean(final Registry registry, final String domain, final Class<?> realClass, final String type) {
        final ManagedBean mbean = new ManagedBean();
        Method[] methods = null;
        final Hashtable<String, Method> attMap = new Hashtable<String, Method>();
        final Hashtable<String, Method> getAttMap = new Hashtable<String, Method>();
        final Hashtable<String, Method> setAttMap = new Hashtable<String, Method>();
        final Hashtable<String, Method> invokeAttMap = new Hashtable<String, Method>();
        methods = realClass.getMethods();
        this.initMethods(realClass, methods, attMap, getAttMap, setAttMap, invokeAttMap);
        try {
            final Enumeration<String> en = attMap.keys();
            while (en.hasMoreElements()) {
                final String name = en.nextElement();
                final AttributeInfo ai = new AttributeInfo();
                ai.setName(name);
                final Method gm = getAttMap.get(name);
                if (gm != null) {
                    ai.setGetMethod(gm.getName());
                    final Class<?> t = gm.getReturnType();
                    if (t != null) {
                        ai.setType(t.getName());
                    }
                }
                final Method sm = setAttMap.get(name);
                if (sm != null) {
                    final Class<?> t2 = sm.getParameterTypes()[0];
                    if (t2 != null) {
                        ai.setType(t2.getName());
                    }
                    ai.setSetMethod(sm.getName());
                }
                ai.setDescription("Introspected attribute " + name);
                if (MbeansDescriptorsIntrospectionSource.log.isDebugEnabled()) {
                    MbeansDescriptorsIntrospectionSource.log.debug((Object)("Introspected attribute " + name + " " + gm + " " + sm));
                }
                if (gm == null) {
                    ai.setReadable(false);
                }
                if (sm == null) {
                    ai.setWriteable(false);
                }
                if (sm != null || gm != null) {
                    mbean.addAttribute(ai);
                }
            }
            for (final Map.Entry<String, Method> entry : invokeAttMap.entrySet()) {
                final String name2 = entry.getKey();
                final Method m = entry.getValue();
                final OperationInfo op = new OperationInfo();
                op.setName(name2);
                op.setReturnType(m.getReturnType().getName());
                op.setDescription("Introspected operation " + name2);
                final Class<?>[] parms = m.getParameterTypes();
                for (int i = 0; i < parms.length; ++i) {
                    final ParameterInfo pi = new ParameterInfo();
                    pi.setType(parms[i].getName());
                    pi.setName(("param" + i).intern());
                    pi.setDescription(("Introspected parameter param" + i).intern());
                    op.addParameter(pi);
                }
                mbean.addOperation(op);
            }
            if (MbeansDescriptorsIntrospectionSource.log.isDebugEnabled()) {
                MbeansDescriptorsIntrospectionSource.log.debug((Object)("Setting name: " + type));
            }
            mbean.setName(type);
            return mbean;
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private static String unCapitalize(final String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        final char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }
    
    static {
        log = LogFactory.getLog((Class)MbeansDescriptorsIntrospectionSource.class);
        (specialMethods = new Hashtable<String, String>()).put("preDeregister", "");
        MbeansDescriptorsIntrospectionSource.specialMethods.put("postDeregister", "");
        supportedTypes = new Class[] { Boolean.class, Boolean.TYPE, Byte.class, Byte.TYPE, Character.class, Character.TYPE, Short.class, Short.TYPE, Integer.class, Integer.TYPE, Long.class, Long.TYPE, Float.class, Float.TYPE, Double.class, Double.TYPE, String.class, String[].class, BigDecimal.class, BigInteger.class, ObjectName.class, Object[].class, File.class };
    }
}
