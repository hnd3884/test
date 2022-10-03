package org.owasp.esapi.reference.accesscontrol;

public class EchoRuntimeParameterACR extends BaseACR<Object, Boolean>
{
    @Override
    public boolean isAuthorized(final Boolean runtimeParameter) throws ClassCastException {
        return runtimeParameter;
    }
}
