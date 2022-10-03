package org.apache.commons.digester;

import org.xml.sax.Attributes;
import org.apache.commons.collections.ArrayStack;

public class CallParamRule extends Rule
{
    protected String attributeName;
    protected int paramIndex;
    protected boolean fromStack;
    protected int stackIndex;
    protected ArrayStack bodyTextStack;
    
    public CallParamRule(final Digester digester, final int paramIndex) {
        this(paramIndex);
    }
    
    public CallParamRule(final Digester digester, final int paramIndex, final String attributeName) {
        this(paramIndex, attributeName);
    }
    
    public CallParamRule(final int paramIndex) {
        this(paramIndex, null);
    }
    
    public CallParamRule(final int paramIndex, final String attributeName) {
        this.attributeName = null;
        this.paramIndex = 0;
        this.fromStack = false;
        this.stackIndex = 0;
        this.paramIndex = paramIndex;
        this.attributeName = attributeName;
    }
    
    public CallParamRule(final int paramIndex, final boolean fromStack) {
        this.attributeName = null;
        this.paramIndex = 0;
        this.fromStack = false;
        this.stackIndex = 0;
        this.paramIndex = paramIndex;
        this.fromStack = fromStack;
    }
    
    public CallParamRule(final int paramIndex, final int stackIndex) {
        this.attributeName = null;
        this.paramIndex = 0;
        this.fromStack = false;
        this.stackIndex = 0;
        this.paramIndex = paramIndex;
        this.fromStack = true;
        this.stackIndex = stackIndex;
    }
    
    public void begin(final Attributes attributes) throws Exception {
        Object param = null;
        if (this.attributeName != null) {
            param = attributes.getValue(this.attributeName);
        }
        else if (this.fromStack) {
            param = this.digester.peek(this.stackIndex);
            if (this.digester.log.isDebugEnabled()) {
                final StringBuffer sb = new StringBuffer("[CallParamRule]{");
                sb.append(this.digester.match);
                sb.append("} Save from stack; from stack?").append(this.fromStack);
                sb.append("; object=").append(param);
                this.digester.log.debug((Object)sb.toString());
            }
        }
        if (param != null) {
            final Object[] parameters = (Object[])this.digester.peekParams();
            parameters[this.paramIndex] = param;
        }
    }
    
    public void body(final String bodyText) throws Exception {
        if (this.attributeName == null && !this.fromStack) {
            if (this.bodyTextStack == null) {
                this.bodyTextStack = new ArrayStack();
            }
            this.bodyTextStack.push((Object)bodyText.trim());
        }
    }
    
    public void end(final String namespace, final String name) {
        if (this.bodyTextStack != null && !this.bodyTextStack.empty()) {
            final Object[] parameters = (Object[])this.digester.peekParams();
            parameters[this.paramIndex] = this.bodyTextStack.pop();
        }
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("CallParamRule[");
        sb.append("paramIndex=");
        sb.append(this.paramIndex);
        sb.append(", attributeName=");
        sb.append(this.attributeName);
        sb.append(", from stack=");
        sb.append(this.fromStack);
        sb.append("]");
        return sb.toString();
    }
}
