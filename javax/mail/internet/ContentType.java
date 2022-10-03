package javax.mail.internet;

public class ContentType
{
    private String primaryType;
    private String subType;
    private ParameterList list;
    
    public ContentType() {
    }
    
    public ContentType(final String primaryType, final String subType, final ParameterList list) {
        this.primaryType = primaryType;
        this.subType = subType;
        this.list = list;
    }
    
    public ContentType(final String s) throws ParseException {
        final HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
        HeaderTokenizer.Token tk = h.next();
        if (tk.getType() != -1) {
            throw new ParseException("In Content-Type string <" + s + ">, expected MIME type, got " + tk.getValue());
        }
        this.primaryType = tk.getValue();
        tk = h.next();
        if ((char)tk.getType() != '/') {
            throw new ParseException("In Content-Type string <" + s + ">, expected '/', got " + tk.getValue());
        }
        tk = h.next();
        if (tk.getType() != -1) {
            throw new ParseException("In Content-Type string <" + s + ">, expected MIME subtype, got " + tk.getValue());
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
        if (this.primaryType == null || this.subType == null) {
            return "";
        }
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
    
    public void setPrimaryType(final String primaryType) {
        this.primaryType = primaryType;
    }
    
    public void setSubType(final String subType) {
        this.subType = subType;
    }
    
    public void setParameter(final String name, final String value) {
        if (this.list == null) {
            this.list = new ParameterList();
        }
        this.list.set(name, value);
    }
    
    public void setParameterList(final ParameterList list) {
        this.list = list;
    }
    
    @Override
    public String toString() {
        if (this.primaryType == null || this.subType == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(this.primaryType).append('/').append(this.subType);
        if (this.list != null) {
            sb.append(this.list.toString(sb.length() + 14));
        }
        return sb.toString();
    }
    
    public boolean match(final ContentType cType) {
        if ((this.primaryType != null || cType.getPrimaryType() != null) && (this.primaryType == null || !this.primaryType.equalsIgnoreCase(cType.getPrimaryType()))) {
            return false;
        }
        final String sType = cType.getSubType();
        return (this.subType != null && this.subType.startsWith("*")) || (sType != null && sType.startsWith("*")) || (this.subType == null && sType == null) || (this.subType != null && this.subType.equalsIgnoreCase(sType));
    }
    
    public boolean match(final String s) {
        try {
            return this.match(new ContentType(s));
        }
        catch (final ParseException pex) {
            return false;
        }
    }
}
