package com.sun.xml.internal.ws.model;

import javax.jws.WebParam;
import java.util.HashSet;
import javax.xml.namespace.QName;
import java.util.Set;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import java.util.Iterator;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;

public class SOAPSEIModel extends AbstractSEIModelImpl
{
    public SOAPSEIModel(final WebServiceFeatureList features) {
        super(features);
    }
    
    @Override
    protected void populateMaps() {
        int emptyBodyCount = 0;
        for (final JavaMethodImpl jm : this.getJavaMethods()) {
            this.put(jm.getMethod(), jm);
            boolean bodyFound = false;
            for (final ParameterImpl p : jm.getRequestParameters()) {
                final ParameterBinding binding = p.getBinding();
                if (binding.isBody()) {
                    this.put(p.getName(), jm);
                    bodyFound = true;
                }
            }
            if (!bodyFound) {
                this.put(this.emptyBodyName, jm);
                ++emptyBodyCount;
            }
        }
        if (emptyBodyCount > 1) {}
    }
    
    public Set<QName> getKnownHeaders() {
        final Set<QName> headers = new HashSet<QName>();
        for (final JavaMethodImpl method : this.getJavaMethods()) {
            Iterator<ParameterImpl> params = method.getRequestParameters().iterator();
            this.fillHeaders(params, headers, WebParam.Mode.IN);
            params = method.getResponseParameters().iterator();
            this.fillHeaders(params, headers, WebParam.Mode.OUT);
        }
        return headers;
    }
    
    private void fillHeaders(final Iterator<ParameterImpl> params, final Set<QName> headers, final WebParam.Mode mode) {
        while (params.hasNext()) {
            final ParameterImpl param = params.next();
            final ParameterBinding binding = (mode == WebParam.Mode.IN) ? param.getInBinding() : param.getOutBinding();
            final QName name = param.getName();
            if (binding.isHeader() && !headers.contains(name)) {
                headers.add(name);
            }
        }
    }
}
