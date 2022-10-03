package HTTPClient;

public class HttpHeaderElement
{
    private String name;
    private String value;
    private NVPair[] parameters;
    
    public HttpHeaderElement(final String name) {
        this.name = name;
        this.value = null;
        this.parameters = new NVPair[0];
    }
    
    public HttpHeaderElement(final String name, final String value, final NVPair[] params) {
        this.name = name;
        this.value = value;
        if (params != null) {
            System.arraycopy(params, 0, this.parameters = new NVPair[params.length], 0, params.length);
        }
        else {
            this.parameters = new NVPair[0];
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public NVPair[] getParams() {
        return this.parameters;
    }
    
    public boolean equals(final Object obj) {
        if (obj != null && obj instanceof HttpHeaderElement) {
            final String other = ((HttpHeaderElement)obj).name;
            return this.name.equalsIgnoreCase(other);
        }
        return false;
    }
    
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        this.appendTo(buf);
        return buf.toString();
    }
    
    public void appendTo(final StringBuffer buf) {
        buf.append(this.name);
        if (this.value != null) {
            if (Util.needsQuoting(this.value)) {
                buf.append("=\"");
                buf.append(Util.quoteString(this.value, "\\\""));
                buf.append('\"');
            }
            else {
                buf.append('=');
                buf.append(this.value);
            }
        }
        for (int idx = 0; idx < this.parameters.length; ++idx) {
            buf.append(";");
            buf.append(this.parameters[idx].getName());
            final String pval = this.parameters[idx].getValue();
            if (pval != null) {
                if (Util.needsQuoting(pval)) {
                    buf.append("=\"");
                    buf.append(Util.quoteString(pval, "\\\""));
                    buf.append('\"');
                }
                else {
                    buf.append('=');
                    buf.append(pval);
                }
            }
        }
    }
}
