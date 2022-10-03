package com.sun.xml.internal.ws.client.sei;

import java.util.Iterator;
import java.util.List;
import javax.jws.soap.SOAPBinding;
import com.sun.xml.internal.ws.model.WrapperParameter;
import com.sun.xml.internal.ws.model.ParameterImpl;
import com.sun.xml.internal.ws.api.message.MessageContextFactory;
import com.sun.xml.internal.ws.model.JavaMethodImpl;

public class StubAsyncHandler extends StubHandler
{
    private final Class asyncBeanClass;
    
    public StubAsyncHandler(final JavaMethodImpl jm, final JavaMethodImpl sync, final MessageContextFactory mcf) {
        super(sync, mcf);
        List<ParameterImpl> rp = sync.getResponseParameters();
        int size = 0;
        for (final ParameterImpl param : rp) {
            if (param.isWrapperStyle()) {
                final WrapperParameter wrapParam = (WrapperParameter)param;
                size += wrapParam.getWrapperChildren().size();
                if (sync.getBinding().getStyle() != SOAPBinding.Style.DOCUMENT) {
                    continue;
                }
                size += 2;
            }
            else {
                ++size;
            }
        }
        Class tempWrap = null;
        if (size > 1) {
            rp = jm.getResponseParameters();
            for (final ParameterImpl param2 : rp) {
                if (param2.isWrapperStyle()) {
                    final WrapperParameter wrapParam2 = (WrapperParameter)param2;
                    if (sync.getBinding().getStyle() == SOAPBinding.Style.DOCUMENT) {
                        tempWrap = (Class)wrapParam2.getTypeInfo().type;
                        break;
                    }
                    for (final ParameterImpl p : wrapParam2.getWrapperChildren()) {
                        if (p.getIndex() == -1) {
                            tempWrap = (Class)p.getTypeInfo().type;
                            break;
                        }
                    }
                    if (tempWrap != null) {
                        break;
                    }
                    continue;
                }
                else {
                    if (param2.getIndex() == -1) {
                        tempWrap = (Class)param2.getTypeInfo().type;
                        break;
                    }
                    continue;
                }
            }
        }
        this.asyncBeanClass = tempWrap;
        switch (size) {
            case 0: {
                this.responseBuilder = this.buildResponseBuilder(sync, ValueSetterFactory.NONE);
                break;
            }
            case 1: {
                this.responseBuilder = this.buildResponseBuilder(sync, ValueSetterFactory.SINGLE);
                break;
            }
            default: {
                this.responseBuilder = this.buildResponseBuilder(sync, new ValueSetterFactory.AsyncBeanValueSetterFactory(this.asyncBeanClass));
                break;
            }
        }
    }
    
    @Override
    protected void initArgs(final Object[] args) throws Exception {
        if (this.asyncBeanClass != null) {
            args[0] = this.asyncBeanClass.newInstance();
        }
    }
    
    @Override
    ValueGetterFactory getValueGetterFactory() {
        return ValueGetterFactory.ASYNC;
    }
}
