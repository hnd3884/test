package org.apache.xmlbeans.impl.jam.annotation;

import org.apache.xmlbeans.impl.jam.provider.JamLogger;
import org.apache.xmlbeans.impl.jam.JAnnotationValue;
import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.provider.JamServiceContext;

public abstract class AnnotationProxy
{
    public static final String SINGLE_MEMBER_NAME = "value";
    private static final String DEFAULT_NVPAIR_DELIMS = "\n\r";
    protected JamServiceContext mContext;
    
    public void init(final JamServiceContext ctx) {
        if (ctx == null) {
            throw new IllegalArgumentException("null logger");
        }
        this.mContext = ctx;
    }
    
    public abstract void setValue(final String p0, final Object p1, final JClass p2);
    
    public abstract JAnnotationValue[] getValues();
    
    public JAnnotationValue getValue(String named) {
        if (named == null) {
            throw new IllegalArgumentException("null name");
        }
        named = named.trim();
        final JAnnotationValue[] values = this.getValues();
        for (int i = 0; i < values.length; ++i) {
            if (named.equals(values[i].getName())) {
                return values[i];
            }
        }
        return null;
    }
    
    protected JamLogger getLogger() {
        return this.mContext.getLogger();
    }
}
