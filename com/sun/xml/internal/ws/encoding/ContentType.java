package com.sun.xml.internal.ws.encoding;

import javax.xml.ws.WebServiceException;

public final class ContentType
{
    private String primaryType;
    private String subType;
    private ParameterList list;
    
    public ContentType(final String s) throws WebServiceException {
        final HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
        HeaderTokenizer.Token tk = h.next();
        if (tk.getType() != -1) {
            throw new WebServiceException();
        }
        this.primaryType = tk.getValue();
        tk = h.next();
        if ((char)tk.getType() != '/') {
            throw new WebServiceException();
        }
        tk = h.next();
        if (tk.getType() != -1) {
            throw new WebServiceException();
        }
        this.subType = tk.getValue();
        final String rem = h.getRemainder();
        if (rem != null) {
            this.list = new ParameterList(rem);
        }
    }
    
    public String getPrimaryType() {
        return this.primaryType;
    }
    
    public String getSubType() {
        return this.subType;
    }
    
    public String getBaseType() {
        return this.primaryType + '/' + this.subType;
    }
    
    public String getParameter(final String name) {
        if (this.list == null) {
            return null;
        }
        return this.list.get(name);
    }
    
    public ParameterList getParameterList() {
        return this.list;
    }
}
