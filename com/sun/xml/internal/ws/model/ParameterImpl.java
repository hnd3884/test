package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.ws.api.model.SEIModel;
import java.util.List;
import javax.xml.ws.Holder;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.spi.db.RepeatedElementBridge;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.bind.api.TypeReference;
import javax.jws.WebParam;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.Parameter;

public class ParameterImpl implements Parameter
{
    private ParameterBinding binding;
    private ParameterBinding outBinding;
    private String partName;
    private final int index;
    private final WebParam.Mode mode;
    @Deprecated
    private TypeReference typeReference;
    private TypeInfo typeInfo;
    private QName name;
    private final JavaMethodImpl parent;
    WrapperParameter wrapper;
    TypeInfo itemTypeInfo;
    
    public ParameterImpl(final JavaMethodImpl parent, final TypeInfo type, final WebParam.Mode mode, final int index) {
        assert type != null;
        this.typeInfo = type;
        this.name = type.tagName;
        this.mode = mode;
        this.index = index;
        this.parent = parent;
    }
    
    @Override
    public AbstractSEIModelImpl getOwner() {
        return this.parent.owner;
    }
    
    @Override
    public JavaMethod getParent() {
        return this.parent;
    }
    
    @Override
    public QName getName() {
        return this.name;
    }
    
    public XMLBridge getXMLBridge() {
        return this.getOwner().getXMLBridge(this.typeInfo);
    }
    
    public XMLBridge getInlinedRepeatedElementBridge() {
        final TypeInfo itemType = this.getItemType();
        if (itemType != null) {
            final XMLBridge xb = this.getOwner().getXMLBridge(itemType);
            if (xb != null) {
                return new RepeatedElementBridge(this.typeInfo, xb);
            }
        }
        return null;
    }
    
    public TypeInfo getItemType() {
        if (this.itemTypeInfo != null) {
            return this.itemTypeInfo;
        }
        if (this.parent.getBinding().isRpcLit() || this.wrapper == null) {
            return null;
        }
        if (!WrapperComposite.class.equals(this.wrapper.getTypeInfo().type)) {
            return null;
        }
        if (!this.getBinding().isBody()) {
            return null;
        }
        return this.itemTypeInfo = this.typeInfo.getItemType();
    }
    
    @Override
    @Deprecated
    public Bridge getBridge() {
        return this.getOwner().getBridge(this.typeReference);
    }
    
    @Deprecated
    protected Bridge getBridge(final TypeReference typeRef) {
        return this.getOwner().getBridge(typeRef);
    }
    
    @Deprecated
    public TypeReference getTypeReference() {
        return this.typeReference;
    }
    
    public TypeInfo getTypeInfo() {
        return this.typeInfo;
    }
    
    @Deprecated
    void setTypeReference(final TypeReference type) {
        this.typeReference = type;
        this.name = type.tagName;
    }
    
    @Override
    public WebParam.Mode getMode() {
        return this.mode;
    }
    
    @Override
    public int getIndex() {
        return this.index;
    }
    
    @Override
    public boolean isWrapperStyle() {
        return false;
    }
    
    @Override
    public boolean isReturnValue() {
        return this.index == -1;
    }
    
    @Override
    public ParameterBinding getBinding() {
        if (this.binding == null) {
            return ParameterBinding.BODY;
        }
        return this.binding;
    }
    
    public void setBinding(final ParameterBinding binding) {
        this.binding = binding;
    }
    
    public void setInBinding(final ParameterBinding binding) {
        this.binding = binding;
    }
    
    public void setOutBinding(final ParameterBinding binding) {
        this.outBinding = binding;
    }
    
    @Override
    public ParameterBinding getInBinding() {
        return this.binding;
    }
    
    @Override
    public ParameterBinding getOutBinding() {
        if (this.outBinding == null) {
            return this.binding;
        }
        return this.outBinding;
    }
    
    @Override
    public boolean isIN() {
        return this.mode == WebParam.Mode.IN;
    }
    
    @Override
    public boolean isOUT() {
        return this.mode == WebParam.Mode.OUT;
    }
    
    @Override
    public boolean isINOUT() {
        return this.mode == WebParam.Mode.INOUT;
    }
    
    @Override
    public boolean isResponse() {
        return this.index == -1;
    }
    
    @Override
    public Object getHolderValue(final Object obj) {
        if (obj != null && obj instanceof Holder) {
            return ((Holder)obj).value;
        }
        return obj;
    }
    
    @Override
    public String getPartName() {
        if (this.partName == null) {
            return this.name.getLocalPart();
        }
        return this.partName;
    }
    
    public void setPartName(final String partName) {
        this.partName = partName;
    }
    
    void fillTypes(final List<TypeInfo> types) {
        final TypeInfo itemType = this.getItemType();
        types.add((itemType != null) ? itemType : this.getTypeInfo());
    }
}
