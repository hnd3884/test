package com.sun.corba.se.impl.presentation.rmi;

import java.rmi.Remote;
import com.sun.corba.se.impl.orbutil.graph.Node;
import com.sun.corba.se.impl.orbutil.graph.GraphImpl;
import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory;
import com.sun.corba.se.spi.presentation.rmi.IDLNameTranslator;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import com.sun.corba.se.impl.orbutil.graph.Graph;
import javax.rmi.CORBA.Tie;
import com.sun.corba.se.spi.presentation.rmi.DynamicMethodMarshaller;
import java.lang.reflect.Method;
import java.util.HashMap;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.util.Map;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;

public final class PresentationManagerImpl implements PresentationManager
{
    private Map classToClassData;
    private Map methodToDMM;
    private StubFactoryFactory staticStubFactoryFactory;
    private StubFactoryFactory dynamicStubFactoryFactory;
    private ORBUtilSystemException wrapper;
    private boolean useDynamicStubs;
    
    public PresentationManagerImpl(final boolean useDynamicStubs) {
        this.wrapper = null;
        this.useDynamicStubs = useDynamicStubs;
        this.wrapper = ORBUtilSystemException.get("rpc.presentation");
        this.classToClassData = new HashMap();
        this.methodToDMM = new HashMap();
    }
    
    @Override
    public synchronized DynamicMethodMarshaller getDynamicMethodMarshaller(final Method method) {
        if (method == null) {
            return null;
        }
        DynamicMethodMarshaller dynamicMethodMarshaller = this.methodToDMM.get(method);
        if (dynamicMethodMarshaller == null) {
            dynamicMethodMarshaller = new DynamicMethodMarshallerImpl(method);
            this.methodToDMM.put(method, dynamicMethodMarshaller);
        }
        return dynamicMethodMarshaller;
    }
    
    @Override
    public synchronized ClassData getClassData(final Class clazz) {
        ClassData classData = this.classToClassData.get(clazz);
        if (classData == null) {
            classData = new ClassDataImpl(clazz);
            this.classToClassData.put(clazz, classData);
        }
        return classData;
    }
    
    @Override
    public StubFactoryFactory getStubFactoryFactory(final boolean b) {
        if (b) {
            return this.dynamicStubFactoryFactory;
        }
        return this.staticStubFactoryFactory;
    }
    
    @Override
    public void setStubFactoryFactory(final boolean b, final StubFactoryFactory stubFactoryFactory) {
        if (b) {
            this.dynamicStubFactoryFactory = stubFactoryFactory;
        }
        else {
            this.staticStubFactoryFactory = stubFactoryFactory;
        }
    }
    
    @Override
    public Tie getTie() {
        return this.dynamicStubFactoryFactory.getTie(null);
    }
    
    @Override
    public boolean useDynamicStubs() {
        return this.useDynamicStubs;
    }
    
    private Set getRootSet(final Class clazz, final NodeImpl nodeImpl, final Graph graph) {
        Set set;
        if (clazz.isInterface()) {
            graph.add(nodeImpl);
            set = graph.getRoots();
        }
        else {
            Class superclass = clazz;
            final HashSet set2 = new HashSet();
            while (superclass != null && !superclass.equals(Object.class)) {
                final NodeImpl nodeImpl2 = new NodeImpl(superclass);
                graph.add(nodeImpl2);
                set2.add(nodeImpl2);
                superclass = superclass.getSuperclass();
            }
            graph.getRoots();
            graph.removeAll(set2);
            set = graph.getRoots();
        }
        return set;
    }
    
    private Class[] getInterfaces(final Set set) {
        final Class[] array = new Class[set.size()];
        final Iterator iterator = set.iterator();
        int n = 0;
        while (iterator.hasNext()) {
            array[n++] = ((NodeImpl)iterator.next()).getInterface();
        }
        return array;
    }
    
    private String[] makeTypeIds(final NodeImpl nodeImpl, final Graph graph, final Set set) {
        final HashSet set2 = new HashSet(graph);
        set2.removeAll(set);
        final ArrayList list = new ArrayList();
        if (set.size() > 1) {
            list.add(nodeImpl.getTypeId());
        }
        this.addNodes(list, set);
        this.addNodes(list, set2);
        return (String[])list.toArray(new String[list.size()]);
    }
    
    private void addNodes(final List list, final Set set) {
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            list.add(((NodeImpl)iterator.next()).getTypeId());
        }
    }
    
    private class ClassDataImpl implements ClassData
    {
        private Class cls;
        private IDLNameTranslator nameTranslator;
        private String[] typeIds;
        private StubFactory sfactory;
        private InvocationHandlerFactory ihfactory;
        private Map dictionary;
        
        public ClassDataImpl(final Class cls) {
            this.cls = cls;
            final GraphImpl graphImpl = new GraphImpl();
            final NodeImpl nodeImpl = new NodeImpl(cls);
            final Set access$000 = PresentationManagerImpl.this.getRootSet(cls, nodeImpl, graphImpl);
            this.nameTranslator = IDLNameTranslatorImpl.get(PresentationManagerImpl.this.getInterfaces(access$000));
            this.typeIds = PresentationManagerImpl.this.makeTypeIds(nodeImpl, graphImpl, access$000);
            this.ihfactory = new InvocationHandlerFactoryImpl(PresentationManagerImpl.this, this);
            this.dictionary = new HashMap();
        }
        
        @Override
        public Class getMyClass() {
            return this.cls;
        }
        
        @Override
        public IDLNameTranslator getIDLNameTranslator() {
            return this.nameTranslator;
        }
        
        @Override
        public String[] getTypeIds() {
            return this.typeIds;
        }
        
        @Override
        public InvocationHandlerFactory getInvocationHandlerFactory() {
            return this.ihfactory;
        }
        
        @Override
        public Map getDictionary() {
            return this.dictionary;
        }
    }
    
    private static class NodeImpl implements Node
    {
        private Class interf;
        
        public Class getInterface() {
            return this.interf;
        }
        
        public NodeImpl(final Class interf) {
            this.interf = interf;
        }
        
        public String getTypeId() {
            return "RMI:" + this.interf.getName() + ":0000000000000000";
        }
        
        @Override
        public Set getChildren() {
            final HashSet set = new HashSet();
            final Class[] interfaces = this.interf.getInterfaces();
            for (int i = 0; i < interfaces.length; ++i) {
                final Class clazz = interfaces[i];
                if (Remote.class.isAssignableFrom(clazz) && !Remote.class.equals(clazz)) {
                    set.add(new NodeImpl(clazz));
                }
            }
            return set;
        }
        
        @Override
        public String toString() {
            return "NodeImpl[" + this.interf + "]";
        }
        
        @Override
        public int hashCode() {
            return this.interf.hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            return this == o || (o instanceof NodeImpl && ((NodeImpl)o).interf.equals(this.interf));
        }
    }
}
