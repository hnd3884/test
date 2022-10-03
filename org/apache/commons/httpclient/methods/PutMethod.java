package org.apache.commons.httpclient.methods;

public class PutMethod extends EntityEnclosingMethod
{
    public PutMethod() {
    }
    
    public PutMethod(final String uri) {
        super(uri);
    }
    
    public String getName() {
        return "PUT";
    }
}
