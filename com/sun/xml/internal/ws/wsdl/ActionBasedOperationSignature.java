package com.sun.xml.internal.ws.wsdl;

import com.sun.istack.internal.NotNull;
import javax.xml.namespace.QName;

public class ActionBasedOperationSignature
{
    private final String action;
    private final QName payloadQName;
    
    public ActionBasedOperationSignature(@NotNull final String action, @NotNull final QName payloadQName) {
        this.action = action;
        this.payloadQName = payloadQName;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ActionBasedOperationSignature that = (ActionBasedOperationSignature)o;
        return this.action.equals(that.action) && this.payloadQName.equals(that.payloadQName);
    }
    
    @Override
    public int hashCode() {
        int result = this.action.hashCode();
        result = 31 * result + this.payloadQName.hashCode();
        return result;
    }
}
