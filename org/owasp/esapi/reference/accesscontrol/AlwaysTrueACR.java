package org.owasp.esapi.reference.accesscontrol;

public class AlwaysTrueACR extends BaseACR<Object, Object>
{
    @Override
    public boolean isAuthorized(final Object runtimeParameter) {
        return true;
    }
}
