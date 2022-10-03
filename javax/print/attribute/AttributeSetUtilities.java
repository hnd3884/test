package javax.print.attribute;

import java.io.Serializable;

public final class AttributeSetUtilities
{
    private AttributeSetUtilities() {
    }
    
    public static AttributeSet unmodifiableView(final AttributeSet set) {
        if (set == null) {
            throw new NullPointerException();
        }
        return new UnmodifiableAttributeSet(set);
    }
    
    public static DocAttributeSet unmodifiableView(final DocAttributeSet set) {
        if (set == null) {
            throw new NullPointerException();
        }
        return new UnmodifiableDocAttributeSet(set);
    }
    
    public static PrintRequestAttributeSet unmodifiableView(final PrintRequestAttributeSet set) {
        if (set == null) {
            throw new NullPointerException();
        }
        return new UnmodifiablePrintRequestAttributeSet(set);
    }
    
    public static PrintJobAttributeSet unmodifiableView(final PrintJobAttributeSet set) {
        if (set == null) {
            throw new NullPointerException();
        }
        return new UnmodifiablePrintJobAttributeSet(set);
    }
    
    public static PrintServiceAttributeSet unmodifiableView(final PrintServiceAttributeSet set) {
        if (set == null) {
            throw new NullPointerException();
        }
        return new UnmodifiablePrintServiceAttributeSet(set);
    }
    
    public static AttributeSet synchronizedView(final AttributeSet set) {
        if (set == null) {
            throw new NullPointerException();
        }
        return new SynchronizedAttributeSet(set);
    }
    
    public static DocAttributeSet synchronizedView(final DocAttributeSet set) {
        if (set == null) {
            throw new NullPointerException();
        }
        return new SynchronizedDocAttributeSet(set);
    }
    
    public static PrintRequestAttributeSet synchronizedView(final PrintRequestAttributeSet set) {
        if (set == null) {
            throw new NullPointerException();
        }
        return new SynchronizedPrintRequestAttributeSet(set);
    }
    
    public static PrintJobAttributeSet synchronizedView(final PrintJobAttributeSet set) {
        if (set == null) {
            throw new NullPointerException();
        }
        return new SynchronizedPrintJobAttributeSet(set);
    }
    
    public static PrintServiceAttributeSet synchronizedView(final PrintServiceAttributeSet set) {
        if (set == null) {
            throw new NullPointerException();
        }
        return new SynchronizedPrintServiceAttributeSet(set);
    }
    
    public static Class<?> verifyAttributeCategory(final Object o, final Class<?> clazz) {
        final Class clazz2 = (Class)o;
        if (clazz.isAssignableFrom(clazz2)) {
            return clazz2;
        }
        throw new ClassCastException();
    }
    
    public static Attribute verifyAttributeValue(final Object o, final Class<?> clazz) {
        if (o == null) {
            throw new NullPointerException();
        }
        if (clazz.isInstance(o)) {
            return (Attribute)o;
        }
        throw new ClassCastException();
    }
    
    public static void verifyCategoryForValue(final Class<?> clazz, final Attribute attribute) {
        if (!clazz.equals(attribute.getCategory())) {
            throw new IllegalArgumentException();
        }
    }
    
    private static class UnmodifiableAttributeSet implements AttributeSet, Serializable
    {
        private AttributeSet attrset;
        
        public UnmodifiableAttributeSet(final AttributeSet attrset) {
            this.attrset = attrset;
        }
        
        @Override
        public Attribute get(final Class<?> clazz) {
            return this.attrset.get(clazz);
        }
        
        @Override
        public boolean add(final Attribute attribute) {
            throw new UnmodifiableSetException();
        }
        
        @Override
        public synchronized boolean remove(final Class<?> clazz) {
            throw new UnmodifiableSetException();
        }
        
        @Override
        public boolean remove(final Attribute attribute) {
            throw new UnmodifiableSetException();
        }
        
        @Override
        public boolean containsKey(final Class<?> clazz) {
            return this.attrset.containsKey(clazz);
        }
        
        @Override
        public boolean containsValue(final Attribute attribute) {
            return this.attrset.containsValue(attribute);
        }
        
        @Override
        public boolean addAll(final AttributeSet set) {
            throw new UnmodifiableSetException();
        }
        
        @Override
        public int size() {
            return this.attrset.size();
        }
        
        @Override
        public Attribute[] toArray() {
            return this.attrset.toArray();
        }
        
        @Override
        public void clear() {
            throw new UnmodifiableSetException();
        }
        
        @Override
        public boolean isEmpty() {
            return this.attrset.isEmpty();
        }
        
        @Override
        public boolean equals(final Object o) {
            return this.attrset.equals(o);
        }
        
        @Override
        public int hashCode() {
            return this.attrset.hashCode();
        }
    }
    
    private static class UnmodifiableDocAttributeSet extends UnmodifiableAttributeSet implements DocAttributeSet, Serializable
    {
        public UnmodifiableDocAttributeSet(final DocAttributeSet set) {
            super(set);
        }
    }
    
    private static class UnmodifiablePrintRequestAttributeSet extends UnmodifiableAttributeSet implements PrintRequestAttributeSet, Serializable
    {
        public UnmodifiablePrintRequestAttributeSet(final PrintRequestAttributeSet set) {
            super(set);
        }
    }
    
    private static class UnmodifiablePrintJobAttributeSet extends UnmodifiableAttributeSet implements PrintJobAttributeSet, Serializable
    {
        public UnmodifiablePrintJobAttributeSet(final PrintJobAttributeSet set) {
            super(set);
        }
    }
    
    private static class UnmodifiablePrintServiceAttributeSet extends UnmodifiableAttributeSet implements PrintServiceAttributeSet, Serializable
    {
        public UnmodifiablePrintServiceAttributeSet(final PrintServiceAttributeSet set) {
            super(set);
        }
    }
    
    private static class SynchronizedAttributeSet implements AttributeSet, Serializable
    {
        private AttributeSet attrset;
        
        public SynchronizedAttributeSet(final AttributeSet attrset) {
            this.attrset = attrset;
        }
        
        @Override
        public synchronized Attribute get(final Class<?> clazz) {
            return this.attrset.get(clazz);
        }
        
        @Override
        public synchronized boolean add(final Attribute attribute) {
            return this.attrset.add(attribute);
        }
        
        @Override
        public synchronized boolean remove(final Class<?> clazz) {
            return this.attrset.remove(clazz);
        }
        
        @Override
        public synchronized boolean remove(final Attribute attribute) {
            return this.attrset.remove(attribute);
        }
        
        @Override
        public synchronized boolean containsKey(final Class<?> clazz) {
            return this.attrset.containsKey(clazz);
        }
        
        @Override
        public synchronized boolean containsValue(final Attribute attribute) {
            return this.attrset.containsValue(attribute);
        }
        
        @Override
        public synchronized boolean addAll(final AttributeSet set) {
            return this.attrset.addAll(set);
        }
        
        @Override
        public synchronized int size() {
            return this.attrset.size();
        }
        
        @Override
        public synchronized Attribute[] toArray() {
            return this.attrset.toArray();
        }
        
        @Override
        public synchronized void clear() {
            this.attrset.clear();
        }
        
        @Override
        public synchronized boolean isEmpty() {
            return this.attrset.isEmpty();
        }
        
        @Override
        public synchronized boolean equals(final Object o) {
            return this.attrset.equals(o);
        }
        
        @Override
        public synchronized int hashCode() {
            return this.attrset.hashCode();
        }
    }
    
    private static class SynchronizedDocAttributeSet extends SynchronizedAttributeSet implements DocAttributeSet, Serializable
    {
        public SynchronizedDocAttributeSet(final DocAttributeSet set) {
            super(set);
        }
    }
    
    private static class SynchronizedPrintRequestAttributeSet extends SynchronizedAttributeSet implements PrintRequestAttributeSet, Serializable
    {
        public SynchronizedPrintRequestAttributeSet(final PrintRequestAttributeSet set) {
            super(set);
        }
    }
    
    private static class SynchronizedPrintJobAttributeSet extends SynchronizedAttributeSet implements PrintJobAttributeSet, Serializable
    {
        public SynchronizedPrintJobAttributeSet(final PrintJobAttributeSet set) {
            super(set);
        }
    }
    
    private static class SynchronizedPrintServiceAttributeSet extends SynchronizedAttributeSet implements PrintServiceAttributeSet, Serializable
    {
        public SynchronizedPrintServiceAttributeSet(final PrintServiceAttributeSet set) {
            super(set);
        }
    }
}
