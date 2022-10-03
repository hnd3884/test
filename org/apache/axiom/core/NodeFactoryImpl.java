package org.apache.axiom.core;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collection;
import org.apache.axiom.core.util.TopologicalSort;
import org.apache.axiom.core.util.EdgeRelation;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.lang.reflect.Constructor;
import java.util.Map;

public class NodeFactoryImpl implements NodeFactory
{
    private final Map<Class<?>, Constructor<?>> constructorMap;
    
    public NodeFactoryImpl(final ClassLoader cl, final String... packages) {
        List<Class<?>> implementations = new ArrayList<Class<?>>();
        for (final String pkg : packages) {
            try {
                final BufferedReader in = new BufferedReader(new InputStreamReader(cl.getResourceAsStream(String.valueOf(pkg.replace('.', '/')) + "/nodetypes.index"), "UTF-8"));
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.startsWith("#")) {
                            continue;
                        }
                        final String className = String.valueOf(pkg) + "." + line;
                        try {
                            implementations.add(cl.loadClass(className));
                        }
                        catch (final ClassNotFoundException ex) {
                            throw new NodeFactoryException("Failed to load class " + className, ex);
                        }
                    }
                }
                finally {
                    in.close();
                }
                in.close();
            }
            catch (final IOException ex2) {
                throw new NodeFactoryException("Failed to load node type index for package " + pkg, ex2);
            }
        }
        implementations = TopologicalSort.sort(implementations, new EdgeRelation<Class<?>>() {
            public boolean isEdge(final Class<?> from, final Class<?> to) {
                return to.isAssignableFrom(from);
            }
        });
        final Map<Class<?>, Class<?>> interfaceToImplementationMap = new HashMap<Class<?>, Class<?>>();
        final Map<Class<?>, Constructor<?>> implementationToConstructorMap = new HashMap<Class<?>, Constructor<?>>();
        final Set<Class<?>> ambiguousInterfaces = new HashSet<Class<?>>();
        for (final Class<?> implementation : implementations) {
            final Set<Class<?>> interfaces = new HashSet<Class<?>>();
            collectInterfaces(implementation, interfaces);
            for (final Class<?> iface : interfaces) {
                if (!ambiguousInterfaces.contains(iface)) {
                    final Class<?> clazz = interfaceToImplementationMap.get(iface);
                    if (clazz == null || implementation.isAssignableFrom(clazz)) {
                        interfaceToImplementationMap.put(iface, implementation);
                    }
                    else {
                        if (clazz.isAssignableFrom(implementation)) {
                            continue;
                        }
                        interfaceToImplementationMap.remove(iface);
                        ambiguousInterfaces.add(iface);
                    }
                }
            }
            try {
                implementationToConstructorMap.put(implementation, implementation.getConstructor((Class<?>[])new Class[0]));
            }
            catch (final NoSuchMethodException ex3) {
                throw new NodeFactoryException("Failed to get constructor for " + implementation.getName(), ex3);
            }
        }
        this.constructorMap = new HashMap<Class<?>, Constructor<?>>();
        for (final Map.Entry<Class<?>, Class<?>> entry : interfaceToImplementationMap.entrySet()) {
            this.constructorMap.put(entry.getKey(), implementationToConstructorMap.get(entry.getValue()));
        }
        this.constructorMap.putAll(implementationToConstructorMap);
    }
    
    private static void collectInterfaces(final Class<?> clazz, final Set<Class<?>> interfaces) {
        Class<?>[] interfaces2;
        for (int length = (interfaces2 = clazz.getInterfaces()).length, i = 0; i < length; ++i) {
            final Class<?> iface = interfaces2[i];
            if (interfaces.add(iface)) {
                collectInterfaces(iface, interfaces);
            }
        }
        final Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            collectInterfaces(superclass, interfaces);
        }
    }
    
    public final <T extends CoreNode> T createNode(final Class<T> type) {
        final Constructor<?> constructor = this.constructorMap.get(type);
        if (constructor == null) {
            throw new NodeFactoryException("Unknown node type " + type.getName());
        }
        try {
            return type.cast(constructor.newInstance(new Object[0]));
        }
        catch (final InvocationTargetException ex) {
            throw new NodeFactoryException("Caught exception thrown by constructor", ex.getCause());
        }
        catch (final InstantiationException ex2) {
            throw new NodeFactoryException("Failed to invoke constructor", ex2);
        }
        catch (final IllegalAccessException ex3) {
            throw new NodeFactoryException("Failed to invoke constructor", ex3);
        }
    }
}
