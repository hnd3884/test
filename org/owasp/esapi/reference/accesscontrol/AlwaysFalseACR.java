package org.owasp.esapi.reference.accesscontrol;

public class AlwaysFalseACR extends BaseACR<Object, Object>
{
    @Override
    public boolean isAuthorized(final Object runtimeParameter) {
        return false;
    }
}
