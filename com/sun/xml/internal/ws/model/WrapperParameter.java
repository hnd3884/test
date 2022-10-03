package com.sun.xml.internal.ws.model;

import java.util.Iterator;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import java.util.ArrayList;
import javax.jws.WebParam;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import java.util.List;

public class WrapperParameter extends ParameterImpl
{
    protected final List<ParameterImpl> wrapperChildren;
    
    public WrapperParameter(final JavaMethodImpl parent, final TypeInfo typeRef, final WebParam.Mode mode, final int index) {
        super(parent, typeRef, mode, index);
        this.wrapperChildren = new ArrayList<ParameterImpl>();
        typeRef.properties().put(WrapperParameter.class.getName(), this);
    }
    
    @Override
    @Deprecated
    public boolean isWrapperStyle() {
        return true;
    }
    
    public List<ParameterImpl> getWrapperChildren() {
        return this.wrapperChildren;
    }
    
    public void addWrapperChild(final ParameterImpl wrapperChild) {
        this.wrapperChildren.add(wrapperChild);
        wrapperChild.wrapper = this;
        assert wrapperChild.getBinding() == ParameterBinding.BODY;
    }
    
    public void clear() {
        this.wrapperChildren.clear();
    }
    
    @Override
    void fillTypes(final List<TypeInfo> types) {
        super.fillTypes(types);
        if (WrapperComposite.class.equals(this.getTypeInfo().type)) {
            for (final ParameterImpl p : this.wrapperChildren) {
                p.fillTypes(types);
            }
        }
    }
}
