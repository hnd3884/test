package java.beans.beancontext;

import java.beans.PropertyChangeEvent;
import java.util.Map;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.awt.Component;
import java.awt.Container;
import java.net.URL;
import java.io.InputStream;
import java.util.Collection;
import java.beans.Visibility;
import java.beans.PropertyVetoException;
import java.util.Iterator;
import java.io.IOException;
import java.beans.Beans;
import java.util.Locale;
import java.util.ArrayList;
import java.util.HashMap;
import java.beans.VetoableChangeListener;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

public class BeanContextSupport extends BeanContextChildSupport implements BeanContext, Serializable, PropertyChangeListener, VetoableChangeListener
{
    static final long serialVersionUID = -4879613978649577204L;
    protected transient HashMap children;
    private int serializable;
    protected transient ArrayList bcmListeners;
    protected Locale locale;
    protected boolean okToUseGui;
    protected boolean designTime;
    private transient PropertyChangeListener childPCL;
    private transient VetoableChangeListener childVCL;
    private transient boolean serializing;
    
    public BeanContextSupport(final BeanContext beanContext, final Locale locale, final boolean designTime, final boolean okToUseGui) {
        super(beanContext);
        this.serializable = 0;
        this.locale = ((locale != null) ? locale : Locale.getDefault());
        this.designTime = designTime;
        this.okToUseGui = okToUseGui;
        this.initialize();
    }
    
    public BeanContextSupport(final BeanContext beanContext, final Locale locale, final boolean b) {
        this(beanContext, locale, b, true);
    }
    
    public BeanContextSupport(final BeanContext beanContext, final Locale locale) {
        this(beanContext, locale, false, true);
    }
    
    public BeanContextSupport(final BeanContext beanContext) {
        this(beanContext, null, false, true);
    }
    
    public BeanContextSupport() {
        this(null, null, false, true);
    }
    
    public BeanContext getBeanContextPeer() {
        return (BeanContext)this.getBeanContextChildPeer();
    }
    
    @Override
    public Object instantiateChild(final String s) throws IOException, ClassNotFoundException {
        final BeanContext beanContextPeer = this.getBeanContextPeer();
        return Beans.instantiate(beanContextPeer.getClass().getClassLoader(), s, beanContextPeer);
    }
    
    @Override
    public int size() {
        synchronized (this.children) {
            return this.children.size();
        }
    }
    
    @Override
    public boolean isEmpty() {
        synchronized (this.children) {
            return this.children.isEmpty();
        }
    }
    
    @Override
    public boolean contains(final Object o) {
        synchronized (this.children) {
            return this.children.containsKey(o);
        }
    }
    
    public boolean containsKey(final Object o) {
        synchronized (this.children) {
            return this.children.containsKey(o);
        }
    }
    
    @Override
    public Iterator iterator() {
        synchronized (this.children) {
            return new BCSIterator(this.children.keySet().iterator());
        }
    }
    
    @Override
    public Object[] toArray() {
        synchronized (this.children) {
            return this.children.keySet().toArray();
        }
    }
    
    @Override
    public Object[] toArray(final Object[] array) {
        synchronized (this.children) {
            return this.children.keySet().toArray(array);
        }
    }
    
    protected BCSChild createBCSChild(final Object o, final Object o2) {
        return new BCSChild(o, o2);
    }
    
    @Override
    public boolean add(final Object o) {
        if (o == null) {
            throw new IllegalArgumentException();
        }
        if (this.children.containsKey(o)) {
            return false;
        }
        synchronized (BeanContext.globalHierarchyLock) {
            if (this.children.containsKey(o)) {
                return false;
            }
            if (!this.validatePendingAdd(o)) {
                throw new IllegalStateException();
            }
            final BeanContextChild childBeanContextChild = getChildBeanContextChild(o);
            BeanContextChild beanContextProxy = null;
            synchronized (o) {
                if (o instanceof BeanContextProxy) {
                    beanContextProxy = ((BeanContextProxy)o).getBeanContextProxy();
                    if (beanContextProxy == null) {
                        throw new NullPointerException("BeanContextPeer.getBeanContextProxy()");
                    }
                }
                final BCSChild bcsChild = this.createBCSChild(o, beanContextProxy);
                BCSChild bcsChild2 = null;
                synchronized (this.children) {
                    this.children.put(o, bcsChild);
                    if (beanContextProxy != null) {
                        this.children.put(beanContextProxy, bcsChild2 = this.createBCSChild(beanContextProxy, o));
                    }
                }
                if (childBeanContextChild != null) {
                    synchronized (childBeanContextChild) {
                        try {
                            childBeanContextChild.setBeanContext(this.getBeanContextPeer());
                        }
                        catch (final PropertyVetoException ex) {
                            synchronized (this.children) {
                                this.children.remove(o);
                                if (beanContextProxy != null) {
                                    this.children.remove(beanContextProxy);
                                }
                            }
                            throw new IllegalStateException();
                        }
                        childBeanContextChild.addPropertyChangeListener("beanContext", this.childPCL);
                        childBeanContextChild.addVetoableChangeListener("beanContext", this.childVCL);
                    }
                }
                final Visibility childVisibility = getChildVisibility(o);
                if (childVisibility != null) {
                    if (this.okToUseGui) {
                        childVisibility.okToUseGui();
                    }
                    else {
                        childVisibility.dontUseGui();
                    }
                }
                if (getChildSerializable(o) != null) {
                    ++this.serializable;
                }
                this.childJustAddedHook(o, bcsChild);
                if (beanContextProxy != null) {
                    final Visibility childVisibility2 = getChildVisibility(beanContextProxy);
                    if (childVisibility2 != null) {
                        if (this.okToUseGui) {
                            childVisibility2.okToUseGui();
                        }
                        else {
                            childVisibility2.dontUseGui();
                        }
                    }
                    if (getChildSerializable(beanContextProxy) != null) {
                        ++this.serializable;
                    }
                    this.childJustAddedHook(beanContextProxy, bcsChild2);
                }
            }
            this.fireChildrenAdded(new BeanContextMembershipEvent(this.getBeanContextPeer(), (beanContextProxy == null) ? new Object[] { o } : new Object[] { o, beanContextProxy }));
        }
        return true;
    }
    
    @Override
    public boolean remove(final Object o) {
        return this.remove(o, true);
    }
    
    protected boolean remove(final Object o, final boolean b) {
        if (o == null) {
            throw new IllegalArgumentException();
        }
        synchronized (BeanContext.globalHierarchyLock) {
            if (!this.containsKey(o)) {
                return false;
            }
            if (!this.validatePendingRemove(o)) {
                throw new IllegalStateException();
            }
            final BCSChild bcsChild = this.children.get(o);
            BCSChild bcsChild2 = null;
            Object proxyPeer = null;
            synchronized (o) {
                if (b) {
                    final BeanContextChild childBeanContextChild = getChildBeanContextChild(o);
                    if (childBeanContextChild != null) {
                        synchronized (childBeanContextChild) {
                            childBeanContextChild.removePropertyChangeListener("beanContext", this.childPCL);
                            childBeanContextChild.removeVetoableChangeListener("beanContext", this.childVCL);
                            try {
                                childBeanContextChild.setBeanContext(null);
                            }
                            catch (final PropertyVetoException ex) {
                                childBeanContextChild.addPropertyChangeListener("beanContext", this.childPCL);
                                childBeanContextChild.addVetoableChangeListener("beanContext", this.childVCL);
                                throw new IllegalStateException();
                            }
                        }
                    }
                }
                synchronized (this.children) {
                    this.children.remove(o);
                    if (bcsChild.isProxyPeer()) {
                        bcsChild2 = (BCSChild)this.children.get(proxyPeer = bcsChild.getProxyPeer());
                        this.children.remove(proxyPeer);
                    }
                }
                if (getChildSerializable(o) != null) {
                    --this.serializable;
                }
                this.childJustRemovedHook(o, bcsChild);
                if (proxyPeer != null) {
                    if (getChildSerializable(proxyPeer) != null) {
                        --this.serializable;
                    }
                    this.childJustRemovedHook(proxyPeer, bcsChild2);
                }
            }
            this.fireChildrenRemoved(new BeanContextMembershipEvent(this.getBeanContextPeer(), (proxyPeer == null) ? new Object[] { o } : new Object[] { o, proxyPeer }));
        }
        return true;
    }
    
    @Override
    public boolean containsAll(final Collection collection) {
        synchronized (this.children) {
            final Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                if (!this.contains(iterator.next())) {
                    return false;
                }
            }
            return true;
        }
    }
    
    @Override
    public boolean addAll(final Collection collection) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean removeAll(final Collection collection) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean retainAll(final Collection collection) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void addBeanContextMembershipListener(final BeanContextMembershipListener beanContextMembershipListener) {
        if (beanContextMembershipListener == null) {
            throw new NullPointerException("listener");
        }
        synchronized (this.bcmListeners) {
            if (this.bcmListeners.contains(beanContextMembershipListener)) {
                return;
            }
            this.bcmListeners.add(beanContextMembershipListener);
        }
    }
    
    @Override
    public void removeBeanContextMembershipListener(final BeanContextMembershipListener beanContextMembershipListener) {
        if (beanContextMembershipListener == null) {
            throw new NullPointerException("listener");
        }
        synchronized (this.bcmListeners) {
            if (!this.bcmListeners.contains(beanContextMembershipListener)) {
                return;
            }
            this.bcmListeners.remove(beanContextMembershipListener);
        }
    }
    
    @Override
    public InputStream getResourceAsStream(final String s, final BeanContextChild beanContextChild) {
        if (s == null) {
            throw new NullPointerException("name");
        }
        if (beanContextChild == null) {
            throw new NullPointerException("bcc");
        }
        if (this.containsKey(beanContextChild)) {
            final ClassLoader classLoader = beanContextChild.getClass().getClassLoader();
            return (classLoader != null) ? classLoader.getResourceAsStream(s) : ClassLoader.getSystemResourceAsStream(s);
        }
        throw new IllegalArgumentException("Not a valid child");
    }
    
    @Override
    public URL getResource(final String s, final BeanContextChild beanContextChild) {
        if (s == null) {
            throw new NullPointerException("name");
        }
        if (beanContextChild == null) {
            throw new NullPointerException("bcc");
        }
        if (this.containsKey(beanContextChild)) {
            final ClassLoader classLoader = beanContextChild.getClass().getClassLoader();
            return (classLoader != null) ? classLoader.getResource(s) : ClassLoader.getSystemResource(s);
        }
        throw new IllegalArgumentException("Not a valid child");
    }
    
    @Override
    public synchronized void setDesignTime(final boolean designTime) {
        if (this.designTime != designTime) {
            this.designTime = designTime;
            this.firePropertyChange("designMode", !designTime, designTime);
        }
    }
    
    @Override
    public synchronized boolean isDesignTime() {
        return this.designTime;
    }
    
    public synchronized void setLocale(final Locale locale) throws PropertyVetoException {
        if (this.locale != null && !this.locale.equals(locale) && locale != null) {
            final Locale locale2 = this.locale;
            this.fireVetoableChange("locale", locale2, locale);
            this.firePropertyChange("locale", locale2, this.locale = locale);
        }
    }
    
    public synchronized Locale getLocale() {
        return this.locale;
    }
    
    @Override
    public synchronized boolean needsGui() {
        final BeanContext beanContextPeer = this.getBeanContextPeer();
        if (beanContextPeer != this) {
            if (beanContextPeer instanceof Visibility) {
                return beanContextPeer.needsGui();
            }
            if (beanContextPeer instanceof Container || beanContextPeer instanceof Component) {
                return true;
            }
        }
        synchronized (this.children) {
            for (final Object next : this.children.keySet()) {
                try {
                    return ((Visibility)next).needsGui();
                }
                catch (final ClassCastException ex) {
                    if (next instanceof Container || next instanceof Component) {
                        return true;
                    }
                    continue;
                }
                break;
            }
        }
        return false;
    }
    
    @Override
    public synchronized void dontUseGui() {
        if (this.okToUseGui) {
            this.okToUseGui = false;
            synchronized (this.children) {
                final Iterator iterator = this.children.keySet().iterator();
                while (iterator.hasNext()) {
                    final Visibility childVisibility = getChildVisibility(iterator.next());
                    if (childVisibility != null) {
                        childVisibility.dontUseGui();
                    }
                }
            }
        }
    }
    
    @Override
    public synchronized void okToUseGui() {
        if (!this.okToUseGui) {
            this.okToUseGui = true;
            synchronized (this.children) {
                final Iterator iterator = this.children.keySet().iterator();
                while (iterator.hasNext()) {
                    final Visibility childVisibility = getChildVisibility(iterator.next());
                    if (childVisibility != null) {
                        childVisibility.okToUseGui();
                    }
                }
            }
        }
    }
    
    @Override
    public boolean avoidingGui() {
        return !this.okToUseGui && this.needsGui();
    }
    
    public boolean isSerializing() {
        return this.serializing;
    }
    
    protected Iterator bcsChildren() {
        synchronized (this.children) {
            return this.children.values().iterator();
        }
    }
    
    protected void bcsPreSerializationHook(final ObjectOutputStream objectOutputStream) throws IOException {
    }
    
    protected void bcsPreDeserializationHook(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
    }
    
    protected void childDeserializedHook(final Object o, final BCSChild bcsChild) {
        synchronized (this.children) {
            this.children.put(o, bcsChild);
        }
    }
    
    protected final void serialize(final ObjectOutputStream objectOutputStream, final Collection collection) throws IOException {
        int i = 0;
        final Object[] array = collection.toArray();
        for (int j = 0; j < array.length; ++j) {
            if (array[j] instanceof Serializable) {
                ++i;
            }
            else {
                array[j] = null;
            }
        }
        objectOutputStream.writeInt(i);
        int n = 0;
        while (i > 0) {
            final Object o = array[n];
            if (o != null) {
                objectOutputStream.writeObject(o);
                --i;
            }
            ++n;
        }
    }
    
    protected final void deserialize(final ObjectInputStream objectInputStream, final Collection collection) throws IOException, ClassNotFoundException {
        int int1 = objectInputStream.readInt();
        while (int1-- > 0) {
            collection.add(objectInputStream.readObject());
        }
    }
    
    public final void writeChildren(final ObjectOutputStream objectOutputStream) throws IOException {
        if (this.serializable <= 0) {
            return;
        }
        final boolean serializing = this.serializing;
        this.serializing = true;
        int n = 0;
        synchronized (this.children) {
            for (Iterator iterator = this.children.entrySet().iterator(); iterator.hasNext() && n < this.serializable; ++n) {
                final Map.Entry entry = (Map.Entry)iterator.next();
                if (entry.getKey() instanceof Serializable) {
                    try {
                        objectOutputStream.writeObject(entry.getKey());
                        objectOutputStream.writeObject(entry.getValue());
                    }
                    catch (final IOException ex) {
                        this.serializing = serializing;
                        throw ex;
                    }
                }
            }
        }
        this.serializing = serializing;
        if (n != this.serializable) {
            throw new IOException("wrote different number of children than expected");
        }
    }
    
    private synchronized void writeObject(final ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        this.serializing = true;
        synchronized (BeanContext.globalHierarchyLock) {
            try {
                objectOutputStream.defaultWriteObject();
                this.bcsPreSerializationHook(objectOutputStream);
                if (this.serializable > 0 && this.equals(this.getBeanContextPeer())) {
                    this.writeChildren(objectOutputStream);
                }
                this.serialize(objectOutputStream, this.bcmListeners);
            }
            finally {
                this.serializing = false;
            }
        }
    }
    
    public final void readChildren(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        int serializable = this.serializable;
        while (serializable-- > 0) {
            final Object object = objectInputStream.readObject();
            final BCSChild bcsChild = (BCSChild)objectInputStream.readObject();
            synchronized (object) {
                BeanContextChild beanContextChild = null;
                try {
                    beanContextChild = (BeanContextChild)object;
                }
                catch (final ClassCastException ex) {}
                if (beanContextChild != null) {
                    try {
                        beanContextChild.setBeanContext(this.getBeanContextPeer());
                        beanContextChild.addPropertyChangeListener("beanContext", this.childPCL);
                        beanContextChild.addVetoableChangeListener("beanContext", this.childVCL);
                    }
                    catch (final PropertyVetoException ex2) {
                        continue;
                    }
                }
                this.childDeserializedHook(object, bcsChild);
            }
        }
    }
    
    private synchronized void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        synchronized (BeanContext.globalHierarchyLock) {
            objectInputStream.defaultReadObject();
            this.initialize();
            this.bcsPreDeserializationHook(objectInputStream);
            if (this.serializable > 0 && this.equals(this.getBeanContextPeer())) {
                this.readChildren(objectInputStream);
            }
            this.deserialize(objectInputStream, this.bcmListeners = new ArrayList(1));
        }
    }
    
    @Override
    public void vetoableChange(final PropertyChangeEvent propertyChangeEvent) throws PropertyVetoException {
        final String propertyName = propertyChangeEvent.getPropertyName();
        final Object source = propertyChangeEvent.getSource();
        synchronized (this.children) {
            if ("beanContext".equals(propertyName) && this.containsKey(source) && !this.getBeanContextPeer().equals(propertyChangeEvent.getNewValue())) {
                if (!this.validatePendingRemove(source)) {
                    throw new PropertyVetoException("current BeanContext vetoes setBeanContext()", propertyChangeEvent);
                }
                ((BCSChild)this.children.get(source)).setRemovePending(true);
            }
        }
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        final String propertyName = propertyChangeEvent.getPropertyName();
        final Object source = propertyChangeEvent.getSource();
        synchronized (this.children) {
            if ("beanContext".equals(propertyName) && this.containsKey(source) && ((BCSChild)this.children.get(source)).isRemovePending()) {
                final BeanContext beanContextPeer = this.getBeanContextPeer();
                if (beanContextPeer.equals(propertyChangeEvent.getOldValue()) && !beanContextPeer.equals(propertyChangeEvent.getNewValue())) {
                    this.remove(source, false);
                }
                else {
                    ((BCSChild)this.children.get(source)).setRemovePending(false);
                }
            }
        }
    }
    
    protected boolean validatePendingAdd(final Object o) {
        return true;
    }
    
    protected boolean validatePendingRemove(final Object o) {
        return true;
    }
    
    protected void childJustAddedHook(final Object o, final BCSChild bcsChild) {
    }
    
    protected void childJustRemovedHook(final Object o, final BCSChild bcsChild) {
    }
    
    protected static final Visibility getChildVisibility(final Object o) {
        try {
            return (Visibility)o;
        }
        catch (final ClassCastException ex) {
            return null;
        }
    }
    
    protected static final Serializable getChildSerializable(final Object o) {
        try {
            return (Serializable)o;
        }
        catch (final ClassCastException ex) {
            return null;
        }
    }
    
    protected static final PropertyChangeListener getChildPropertyChangeListener(final Object o) {
        try {
            return (PropertyChangeListener)o;
        }
        catch (final ClassCastException ex) {
            return null;
        }
    }
    
    protected static final VetoableChangeListener getChildVetoableChangeListener(final Object o) {
        try {
            return (VetoableChangeListener)o;
        }
        catch (final ClassCastException ex) {
            return null;
        }
    }
    
    protected static final BeanContextMembershipListener getChildBeanContextMembershipListener(final Object o) {
        try {
            return (BeanContextMembershipListener)o;
        }
        catch (final ClassCastException ex) {
            return null;
        }
    }
    
    protected static final BeanContextChild getChildBeanContextChild(final Object o) {
        try {
            final BeanContextChild beanContextChild = (BeanContextChild)o;
            if (o instanceof BeanContextChild && o instanceof BeanContextProxy) {
                throw new IllegalArgumentException("child cannot implement both BeanContextChild and BeanContextProxy");
            }
            return beanContextChild;
        }
        catch (final ClassCastException ex) {
            try {
                return ((BeanContextProxy)o).getBeanContextProxy();
            }
            catch (final ClassCastException ex2) {
                return null;
            }
        }
    }
    
    protected final void fireChildrenAdded(final BeanContextMembershipEvent beanContextMembershipEvent) {
        final Object[] array;
        synchronized (this.bcmListeners) {
            array = this.bcmListeners.toArray();
        }
        for (int i = 0; i < array.length; ++i) {
            ((BeanContextMembershipListener)array[i]).childrenAdded(beanContextMembershipEvent);
        }
    }
    
    protected final void fireChildrenRemoved(final BeanContextMembershipEvent beanContextMembershipEvent) {
        final Object[] array;
        synchronized (this.bcmListeners) {
            array = this.bcmListeners.toArray();
        }
        for (int i = 0; i < array.length; ++i) {
            ((BeanContextMembershipListener)array[i]).childrenRemoved(beanContextMembershipEvent);
        }
    }
    
    protected synchronized void initialize() {
        this.children = new HashMap(this.serializable + 1);
        this.bcmListeners = new ArrayList(1);
        this.childPCL = new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
                BeanContextSupport.this.propertyChange(propertyChangeEvent);
            }
        };
        this.childVCL = new VetoableChangeListener() {
            @Override
            public void vetoableChange(final PropertyChangeEvent propertyChangeEvent) throws PropertyVetoException {
                BeanContextSupport.this.vetoableChange(propertyChangeEvent);
            }
        };
    }
    
    protected final Object[] copyChildren() {
        synchronized (this.children) {
            return this.children.keySet().toArray();
        }
    }
    
    protected static final boolean classEquals(final Class clazz, final Class clazz2) {
        return clazz.equals(clazz2) || clazz.getName().equals(clazz2.getName());
    }
    
    protected static final class BCSIterator implements Iterator
    {
        private Iterator src;
        
        BCSIterator(final Iterator src) {
            this.src = src;
        }
        
        @Override
        public boolean hasNext() {
            return this.src.hasNext();
        }
        
        @Override
        public Object next() {
            return this.src.next();
        }
        
        @Override
        public void remove() {
        }
    }
    
    protected class BCSChild implements Serializable
    {
        private static final long serialVersionUID = -5815286101609939109L;
        private Object child;
        private Object proxyPeer;
        private transient boolean removePending;
        
        BCSChild(final Object child, final Object proxyPeer) {
            this.child = child;
            this.proxyPeer = proxyPeer;
        }
        
        Object getChild() {
            return this.child;
        }
        
        void setRemovePending(final boolean removePending) {
            this.removePending = removePending;
        }
        
        boolean isRemovePending() {
            return this.removePending;
        }
        
        boolean isProxyPeer() {
            return this.proxyPeer != null;
        }
        
        Object getProxyPeer() {
            return this.proxyPeer;
        }
    }
}
