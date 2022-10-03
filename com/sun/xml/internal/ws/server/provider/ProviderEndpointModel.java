package com.sun.xml.internal.ws.server.provider;

import javax.xml.ws.ServiceMode;
import javax.xml.transform.Source;
import java.lang.reflect.ParameterizedType;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.resources.ServerMessages;
import java.lang.reflect.Type;
import com.sun.xml.internal.ws.spi.db.BindingHelper;
import javax.xml.ws.Provider;
import com.sun.xml.internal.ws.api.server.AsyncProvider;
import javax.activation.DataSource;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.soap.SOAPBinding;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.istack.internal.NotNull;
import javax.xml.ws.Service;

final class ProviderEndpointModel<T>
{
    final boolean isAsync;
    @NotNull
    final Service.Mode mode;
    @NotNull
    final Class datatype;
    @NotNull
    final Class implClass;
    
    ProviderEndpointModel(final Class<T> implementorClass, final WSBinding binding) {
        assert implementorClass != null;
        assert binding != null;
        this.implClass = implementorClass;
        this.mode = getServiceMode(implementorClass);
        final Class otherClass = (binding instanceof SOAPBinding) ? SOAPMessage.class : DataSource.class;
        this.isAsync = AsyncProvider.class.isAssignableFrom(implementorClass);
        final Class<?> baseType = (Class<?>)(this.isAsync ? AsyncProvider.class : Provider.class);
        final Type baseParam = BindingHelper.getBaseType(implementorClass, baseType);
        if (baseParam == null) {
            throw new WebServiceException(ServerMessages.NOT_IMPLEMENT_PROVIDER(implementorClass.getName()));
        }
        if (!(baseParam instanceof ParameterizedType)) {
            throw new WebServiceException(ServerMessages.PROVIDER_NOT_PARAMETERIZED(implementorClass.getName()));
        }
        final ParameterizedType pt = (ParameterizedType)baseParam;
        final Type[] types = pt.getActualTypeArguments();
        if (!(types[0] instanceof Class)) {
            throw new WebServiceException(ServerMessages.PROVIDER_INVALID_PARAMETER_TYPE(implementorClass.getName(), types[0]));
        }
        this.datatype = (Class)types[0];
        if (this.mode == Service.Mode.PAYLOAD && this.datatype != Source.class) {
            throw new IllegalArgumentException("Illeagal combination - Mode.PAYLOAD and Provider<" + otherClass.getName() + ">");
        }
    }
    
    private static Service.Mode getServiceMode(final Class<?> c) {
        final ServiceMode mode = c.getAnnotation(ServiceMode.class);
        return (mode == null) ? Service.Mode.PAYLOAD : mode.value();
    }
}
