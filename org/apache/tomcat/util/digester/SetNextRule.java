package org.apache.tomcat.util.digester;

import org.apache.tomcat.util.IntrospectionUtils;

public class SetNextRule extends Rule
{
    protected String methodName;
    protected String paramType;
    protected boolean useExactMatch;
    
    public SetNextRule(final String methodName, final String paramType) {
        this.methodName = null;
        this.paramType = null;
        this.useExactMatch = false;
        this.methodName = methodName;
        this.paramType = paramType;
    }
    
    public boolean isExactMatch() {
        return this.useExactMatch;
    }
    
    public void setExactMatch(final boolean useExactMatch) {
        this.useExactMatch = useExactMatch;
    }
    
    @Override
    public void end(final String namespace, final String name) throws Exception {
        final Object child = this.digester.peek(0);
        final Object parent = this.digester.peek(1);
        if (this.digester.log.isDebugEnabled()) {
            if (parent == null) {
                this.digester.log.debug((Object)("[SetNextRule]{" + this.digester.match + "} Call [NULL PARENT]." + this.methodName + "(" + child + ")"));
            }
            else {
                this.digester.log.debug((Object)("[SetNextRule]{" + this.digester.match + "} Call " + parent.getClass().getName() + "." + this.methodName + "(" + child + ")"));
            }
        }
        IntrospectionUtils.callMethod1(parent, this.methodName, child, this.paramType, this.digester.getClassLoader());
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SetNextRule[");
        sb.append("methodName=");
        sb.append(this.methodName);
        sb.append(", paramType=");
        sb.append(this.paramType);
        sb.append(']');
        return sb.toString();
    }
}
