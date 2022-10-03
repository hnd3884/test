package org.apache.xmlbeans.impl.jam.annotation;

import org.apache.xmlbeans.impl.jam.mutable.MSourcePosition;
import com.sun.javadoc.SourcePosition;
import org.apache.xmlbeans.impl.jam.internal.elements.ElementContext;
import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.provider.JamLogger;
import org.apache.xmlbeans.impl.jam.mutable.MAnnotation;
import com.sun.javadoc.Tag;
import org.apache.xmlbeans.impl.jam.mutable.MAnnotatedElement;
import org.apache.xmlbeans.impl.jam.provider.JamServiceContext;

public abstract class JavadocTagParser
{
    private JamServiceContext mContext;
    private boolean mAddSingleValueMembers;
    
    public JavadocTagParser() {
        this.mContext = null;
        this.mAddSingleValueMembers = false;
    }
    
    public void setAddSingleValueMembers(final boolean b) {
        this.mAddSingleValueMembers = b;
    }
    
    public void init(final JamServiceContext ctx) {
        if (ctx == null) {
            throw new IllegalArgumentException("null logger");
        }
        if (this.mContext != null) {
            throw new IllegalStateException("JavadocTagParser.init() called twice");
        }
        this.mContext = ctx;
    }
    
    public abstract void parse(final MAnnotatedElement p0, final Tag p1);
    
    protected MAnnotation[] createAnnotations(final MAnnotatedElement target, final Tag tag) {
        final String tagName = tag.name().trim().substring(1);
        MAnnotation current = target.getMutableAnnotation(tagName);
        if (current == null) {
            current = target.findOrCreateAnnotation(tagName);
            this.setPosition(current, tag);
        }
        final MAnnotation literal = target.addLiteralAnnotation(tagName);
        this.setPosition(literal, tag);
        final MAnnotation[] out = { literal, current };
        if (this.mAddSingleValueMembers) {
            this.setSingleValueText(out, tag);
        }
        return out;
    }
    
    protected void setValue(final MAnnotation[] anns, String memberName, String value) {
        value = value.trim();
        memberName = memberName.trim();
        for (int i = 0; i < anns.length; ++i) {
            if (anns[i].getValue(memberName) == null) {
                anns[i].setSimpleValue(memberName, value, this.getStringType());
            }
        }
    }
    
    protected JamLogger getLogger() {
        return this.mContext.getLogger();
    }
    
    protected JClass getStringType() {
        return ((ElementContext)this.mContext).getClassLoader().loadClass("java.lang.String");
    }
    
    protected void setSingleValueText(final MAnnotation[] targets, final Tag tag) {
        final String tagText = tag.text();
        for (int i = 0; i < targets.length; ++i) {
            targets[i].setSimpleValue("value", tagText, this.getStringType());
        }
    }
    
    private void setPosition(final MAnnotation target, final Tag tag) {
        final SourcePosition pos = tag.position();
        if (pos != null) {
            final MSourcePosition mpos = target.createSourcePosition();
            mpos.setLine(pos.line());
            mpos.setColumn(pos.column());
            if (pos.file() != null) {
                mpos.setSourceURI(pos.file().toURI());
            }
        }
    }
}
