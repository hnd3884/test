package org.apache.commons.digester;

import org.xml.sax.Attributes;

public class ObjectParamRule extends Rule
{
    protected String attributeName;
    protected int paramIndex;
    protected Object param;
    
    public ObjectParamRule(final int paramIndex, final Object param) {
        this(paramIndex, null, param);
    }
    
    public ObjectParamRule(final int paramIndex, final String attributeName, final Object param) {
        this.attributeName = null;
        this.paramIndex = 0;
        this.param = null;
        this.paramIndex = paramIndex;
        this.attributeName = attributeName;
        this.param = param;
    }
    
    public void begin(final String namespace, final String name, final Attributes attributes) throws Exception {
        Object anAttribute = null;
        final Object[] parameters = (Object[])this.digester.peekParams();
        if (this.attributeName != null) {
            anAttribute = attributes.getValue(this.attributeName);
            if (anAttribute != null) {
                parameters[this.paramIndex] = this.param;
            }
        }
        else {
            parameters[this.paramIndex] = this.param;
        }
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("ObjectParamRule[");
        sb.append("paramIndex=");
        sb.append(this.paramIndex);
        sb.append(", attributeName=");
        sb.append(this.attributeName);
        sb.append(", param=");
        sb.append(this.param);
        sb.append("]");
        return sb.toString();
    }
}
