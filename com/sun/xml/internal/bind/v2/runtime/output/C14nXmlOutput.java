package com.sun.xml.internal.bind.v2.runtime.output;

import java.util.List;
import java.util.Collections;
import java.util.Arrays;
import java.io.IOException;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import java.io.OutputStream;
import com.sun.istack.internal.FinalArrayList;

public class C14nXmlOutput extends UTF8XmlOutput
{
    private StaticAttribute[] staticAttributes;
    private int len;
    private int[] nsBuf;
    private final FinalArrayList<DynamicAttribute> otherAttributes;
    private final boolean namedAttributesAreOrdered;
    
    public C14nXmlOutput(final OutputStream out, final Encoded[] localNames, final boolean namedAttributesAreOrdered, final CharacterEscapeHandler escapeHandler) {
        super(out, localNames, escapeHandler);
        this.staticAttributes = new StaticAttribute[8];
        this.len = 0;
        this.nsBuf = new int[8];
        this.otherAttributes = new FinalArrayList<DynamicAttribute>();
        this.namedAttributesAreOrdered = namedAttributesAreOrdered;
        for (int i = 0; i < this.staticAttributes.length; ++i) {
            this.staticAttributes[i] = new StaticAttribute();
        }
    }
    
    @Override
    public void attribute(final Name name, final String value) throws IOException {
        if (this.staticAttributes.length == this.len) {
            final int newLen = this.len * 2;
            final StaticAttribute[] newbuf = new StaticAttribute[newLen];
            System.arraycopy(this.staticAttributes, 0, newbuf, 0, this.len);
            for (int i = this.len; i < newLen; ++i) {
                this.staticAttributes[i] = new StaticAttribute();
            }
            this.staticAttributes = newbuf;
        }
        this.staticAttributes[this.len++].set(name, value);
    }
    
    @Override
    public void attribute(final int prefix, final String localName, final String value) throws IOException {
        this.otherAttributes.add(new DynamicAttribute(prefix, localName, value));
    }
    
    @Override
    public void endStartTag() throws IOException {
        if (this.otherAttributes.isEmpty()) {
            if (this.len != 0) {
                if (!this.namedAttributesAreOrdered) {
                    Arrays.sort(this.staticAttributes, 0, this.len);
                }
                for (int i = 0; i < this.len; ++i) {
                    this.staticAttributes[i].write();
                }
                this.len = 0;
            }
        }
        else {
            for (int i = 0; i < this.len; ++i) {
                this.otherAttributes.add(this.staticAttributes[i].toDynamicAttribute());
            }
            this.len = 0;
            Collections.sort(this.otherAttributes);
            for (int size = this.otherAttributes.size(), j = 0; j < size; ++j) {
                final DynamicAttribute a = this.otherAttributes.get(j);
                super.attribute(a.prefix, a.localName, a.value);
            }
            this.otherAttributes.clear();
        }
        super.endStartTag();
    }
    
    @Override
    protected void writeNsDecls(final int base) throws IOException {
        final int count = this.nsContext.getCurrent().count();
        if (count == 0) {
            return;
        }
        if (count > this.nsBuf.length) {
            this.nsBuf = new int[count];
        }
        for (int i = count - 1; i >= 0; --i) {
            this.nsBuf[i] = base + i;
        }
        for (int i = 0; i < count; ++i) {
            for (int j = i + 1; j < count; ++j) {
                final String p = this.nsContext.getPrefix(this.nsBuf[i]);
                final String q = this.nsContext.getPrefix(this.nsBuf[j]);
                if (p.compareTo(q) > 0) {
                    final int t = this.nsBuf[j];
                    this.nsBuf[j] = this.nsBuf[i];
                    this.nsBuf[i] = t;
                }
            }
        }
        for (int i = 0; i < count; ++i) {
            this.writeNsDecl(this.nsBuf[i]);
        }
    }
    
    final class StaticAttribute implements Comparable<StaticAttribute>
    {
        Name name;
        String value;
        
        public void set(final Name name, final String value) {
            this.name = name;
            this.value = value;
        }
        
        void write() throws IOException {
            UTF8XmlOutput.this.attribute(this.name, this.value);
        }
        
        DynamicAttribute toDynamicAttribute() {
            final int nsUriIndex = this.name.nsUriIndex;
            int prefix;
            if (nsUriIndex == -1) {
                prefix = -1;
            }
            else {
                prefix = C14nXmlOutput.this.nsUriIndex2prefixIndex[nsUriIndex];
            }
            return new DynamicAttribute(prefix, this.name.localName, this.value);
        }
        
        @Override
        public int compareTo(final StaticAttribute that) {
            return this.name.compareTo(that.name);
        }
    }
    
    final class DynamicAttribute implements Comparable<DynamicAttribute>
    {
        final int prefix;
        final String localName;
        final String value;
        
        public DynamicAttribute(final int prefix, final String localName, final String value) {
            this.prefix = prefix;
            this.localName = localName;
            this.value = value;
        }
        
        private String getURI() {
            if (this.prefix == -1) {
                return "";
            }
            return C14nXmlOutput.this.nsContext.getNamespaceURI(this.prefix);
        }
        
        @Override
        public int compareTo(final DynamicAttribute that) {
            final int r = this.getURI().compareTo(that.getURI());
            if (r != 0) {
                return r;
            }
            return this.localName.compareTo(that.localName);
        }
    }
}
