package javax.swing.text;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.io.Serializable;

public class SimpleAttributeSet implements MutableAttributeSet, Serializable, Cloneable
{
    private static final long serialVersionUID = -6631553454711782652L;
    public static final AttributeSet EMPTY;
    private transient LinkedHashMap<Object, Object> table;
    
    public SimpleAttributeSet() {
        this.table = new LinkedHashMap<Object, Object>(3);
    }
    
    public SimpleAttributeSet(final AttributeSet set) {
        this.table = new LinkedHashMap<Object, Object>(3);
        this.addAttributes(set);
    }
    
    public boolean isEmpty() {
        return this.table.isEmpty();
    }
    
    @Override
    public int getAttributeCount() {
        return this.table.size();
    }
    
    @Override
    public boolean isDefined(final Object o) {
        return this.table.containsKey(o);
    }
    
    @Override
    public boolean isEqual(final AttributeSet set) {
        return this.getAttributeCount() == set.getAttributeCount() && this.containsAttributes(set);
    }
    
    @Override
    public AttributeSet copyAttributes() {
        return (AttributeSet)this.clone();
    }
    
    @Override
    public Enumeration<?> getAttributeNames() {
        return Collections.enumeration((Collection<?>)this.table.keySet());
    }
    
    @Override
    public Object getAttribute(final Object o) {
        Object o2 = this.table.get(o);
        if (o2 == null) {
            final AttributeSet resolveParent = this.getResolveParent();
            if (resolveParent != null) {
                o2 = resolveParent.getAttribute(o);
            }
        }
        return o2;
    }
    
    @Override
    public boolean containsAttribute(final Object o, final Object o2) {
        return o2.equals(this.getAttribute(o));
    }
    
    @Override
    public boolean containsAttributes(final AttributeSet set) {
        boolean equals = true;
        Object nextElement;
        for (Enumeration<?> attributeNames = set.getAttributeNames(); equals && attributeNames.hasMoreElements(); equals = set.getAttribute(nextElement).equals(this.getAttribute(nextElement))) {
            nextElement = attributeNames.nextElement();
        }
        return equals;
    }
    
    @Override
    public void addAttribute(final Object o, final Object o2) {
        this.table.put(o, o2);
    }
    
    @Override
    public void addAttributes(final AttributeSet set) {
        final Enumeration<?> attributeNames = set.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final Object nextElement = attributeNames.nextElement();
            this.addAttribute(nextElement, set.getAttribute(nextElement));
        }
    }
    
    @Override
    public void removeAttribute(final Object o) {
        this.table.remove(o);
    }
    
    @Override
    public void removeAttributes(final Enumeration<?> enumeration) {
        while (enumeration.hasMoreElements()) {
            this.removeAttribute(enumeration.nextElement());
        }
    }
    
    @Override
    public void removeAttributes(final AttributeSet set) {
        if (set == this) {
            this.table.clear();
        }
        else {
            final Enumeration<?> attributeNames = set.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                final Object nextElement = attributeNames.nextElement();
                if (set.getAttribute(nextElement).equals(this.getAttribute(nextElement))) {
                    this.removeAttribute(nextElement);
                }
            }
        }
    }
    
    @Override
    public AttributeSet getResolveParent() {
        return this.table.get(StyleConstants.ResolveAttribute);
    }
    
    @Override
    public void setResolveParent(final AttributeSet set) {
        this.addAttribute(StyleConstants.ResolveAttribute, set);
    }
    
    public Object clone() {
        SimpleAttributeSet set;
        try {
            set = (SimpleAttributeSet)super.clone();
            set.table = (LinkedHashMap)this.table.clone();
        }
        catch (final CloneNotSupportedException ex) {
            set = null;
        }
        return set;
    }
    
    @Override
    public int hashCode() {
        return this.table.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof AttributeSet && this.isEqual((AttributeSet)o));
    }
    
    @Override
    public String toString() {
        String s = "";
        final Enumeration<?> attributeNames = this.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final Object nextElement = attributeNames.nextElement();
            final Object attribute = this.getAttribute(nextElement);
            if (attribute instanceof AttributeSet) {
                s = s + nextElement + "=**AttributeSet** ";
            }
            else {
                s = s + nextElement + "=" + attribute + " ";
            }
        }
        return s;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        StyleContext.writeAttributeSet(objectOutputStream, this);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        this.table = new LinkedHashMap<Object, Object>(3);
        StyleContext.readAttributeSet(objectInputStream, this);
    }
    
    static {
        EMPTY = new EmptyAttributeSet();
    }
    
    static class EmptyAttributeSet implements AttributeSet, Serializable
    {
        static final long serialVersionUID = -8714803568785904228L;
        
        @Override
        public int getAttributeCount() {
            return 0;
        }
        
        @Override
        public boolean isDefined(final Object o) {
            return false;
        }
        
        @Override
        public boolean isEqual(final AttributeSet set) {
            return set.getAttributeCount() == 0;
        }
        
        @Override
        public AttributeSet copyAttributes() {
            return this;
        }
        
        @Override
        public Object getAttribute(final Object o) {
            return null;
        }
        
        @Override
        public Enumeration getAttributeNames() {
            return Collections.emptyEnumeration();
        }
        
        @Override
        public boolean containsAttribute(final Object o, final Object o2) {
            return false;
        }
        
        @Override
        public boolean containsAttributes(final AttributeSet set) {
            return set.getAttributeCount() == 0;
        }
        
        @Override
        public AttributeSet getResolveParent() {
            return null;
        }
        
        @Override
        public boolean equals(final Object o) {
            return this == o || (o instanceof AttributeSet && ((AttributeSet)o).getAttributeCount() == 0);
        }
        
        @Override
        public int hashCode() {
            return 0;
        }
    }
}
