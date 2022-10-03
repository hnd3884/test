package org.apache.xmlbeans.impl.jam.provider;

import org.apache.xmlbeans.impl.jam.mutable.MClass;
import org.apache.xmlbeans.impl.jam.internal.elements.ElementContext;

public class CompositeJamClassBuilder extends JamClassBuilder
{
    private JamClassBuilder[] mBuilders;
    
    public CompositeJamClassBuilder(final JamClassBuilder[] builders) {
        if (builders == null) {
            throw new IllegalArgumentException("null builders");
        }
        this.mBuilders = builders;
    }
    
    @Override
    public void init(final ElementContext ctx) {
        for (int i = 0; i < this.mBuilders.length; ++i) {
            this.mBuilders[i].init(ctx);
        }
    }
    
    @Override
    public MClass build(final String pkg, final String cname) {
        MClass out = null;
        for (int i = 0; i < this.mBuilders.length; ++i) {
            out = this.mBuilders[i].build(pkg, cname);
            if (out != null) {
                return out;
            }
        }
        return null;
    }
}
