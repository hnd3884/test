package javax.swing.text.html;

import java.util.NoSuchElementException;
import java.util.Enumeration;
import javax.swing.text.SimpleAttributeSet;
import java.io.Serializable;
import javax.swing.text.AttributeSet;

class MuxingAttributeSet implements AttributeSet, Serializable
{
    private AttributeSet[] attrs;
    
    public MuxingAttributeSet(final AttributeSet[] attrs) {
        this.attrs = attrs;
    }
    
    protected MuxingAttributeSet() {
    }
    
    protected synchronized void setAttributes(final AttributeSet[] attrs) {
        this.attrs = attrs;
    }
    
    protected synchronized AttributeSet[] getAttributes() {
        return this.attrs;
    }
    
    protected synchronized void insertAttributeSetAt(final AttributeSet set, final int n) {
        final int length = this.attrs.length;
        final AttributeSet[] attrs = new AttributeSet[length + 1];
        if (n < length) {
            if (n > 0) {
                System.arraycopy(this.attrs, 0, attrs, 0, n);
                System.arraycopy(this.attrs, n, attrs, n + 1, length - n);
            }
            else {
                System.arraycopy(this.attrs, 0, attrs, 1, length);
            }
        }
        else {
            System.arraycopy(this.attrs, 0, attrs, 0, length);
        }
        attrs[n] = set;
        this.attrs = attrs;
    }
    
    protected synchronized void removeAttributeSetAt(final int n) {
        final int length = this.attrs.length;
        final AttributeSet[] attrs = new AttributeSet[length - 1];
        if (length > 0) {
            if (n == 0) {
                System.arraycopy(this.attrs, 1, attrs, 0, length - 1);
            }
            else if (n < length - 1) {
                System.arraycopy(this.attrs, 0, attrs, 0, n);
                System.arraycopy(this.attrs, n + 1, attrs, n, length - n - 1);
            }
            else {
                System.arraycopy(this.attrs, 0, attrs, 0, length - 1);
            }
        }
        this.attrs = attrs;
    }
    
    @Override
    public int getAttributeCount() {
        final AttributeSet[] attributes = this.getAttributes();
        int n = 0;
        for (int i = 0; i < attributes.length; ++i) {
            n += attributes[i].getAttributeCount();
        }
        return n;
    }
    
    @Override
    public boolean isDefined(final Object o) {
        final AttributeSet[] attributes = this.getAttributes();
        for (int i = 0; i < attributes.length; ++i) {
            if (attributes[i].isDefined(o)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isEqual(final AttributeSet set) {
        return this.getAttributeCount() == set.getAttributeCount() && this.containsAttributes(set);
    }
    
    @Override
    public AttributeSet copyAttributes() {
        final AttributeSet[] attributes = this.getAttributes();
        final SimpleAttributeSet set = new SimpleAttributeSet();
        for (int i = attributes.length - 1; i >= 0; --i) {
            set.addAttributes(attributes[i]);
        }
        return set;
    }
    
    @Override
    public Object getAttribute(final Object o) {
        final AttributeSet[] attributes = this.getAttributes();
        for (int length = attributes.length, i = 0; i < length; ++i) {
            final Object attribute = attributes[i].getAttribute(o);
            if (attribute != null) {
                return attribute;
            }
        }
        return null;
    }
    
    @Override
    public Enumeration getAttributeNames() {
        return new MuxingAttributeNameEnumeration();
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
    public AttributeSet getResolveParent() {
        return null;
    }
    
    private class MuxingAttributeNameEnumeration implements Enumeration
    {
        private int attrIndex;
        private Enumeration currentEnum;
        
        MuxingAttributeNameEnumeration() {
            this.updateEnum();
        }
        
        @Override
        public boolean hasMoreElements() {
            return this.currentEnum != null && this.currentEnum.hasMoreElements();
        }
        
        @Override
        public Object nextElement() {
            if (this.currentEnum == null) {
                throw new NoSuchElementException("No more names");
            }
            final Object nextElement = this.currentEnum.nextElement();
            if (!this.currentEnum.hasMoreElements()) {
                this.updateEnum();
            }
            return nextElement;
        }
        
        void updateEnum() {
            final AttributeSet[] attributes = MuxingAttributeSet.this.getAttributes();
            this.currentEnum = null;
            while (this.currentEnum == null && this.attrIndex < attributes.length) {
                this.currentEnum = attributes[this.attrIndex++].getAttributeNames();
                if (!this.currentEnum.hasMoreElements()) {
                    this.currentEnum = null;
                }
            }
        }
    }
}
