package javax.mail.internet;

import com.sun.mail.util.PropUtil;

public class ContentDisposition
{
    private static final boolean contentDispositionStrict;
    private String disposition;
    private ParameterList list;
    
    public ContentDisposition() {
    }
    
    public ContentDisposition(final String disposition, final ParameterList list) {
        this.disposition = disposition;
        this.list = list;
    }
    
    public ContentDisposition(final String s) throws ParseException {
        final HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
        final HeaderTokenizer.Token tk = h.next();
        if (tk.getType() != -1) {
            if (ContentDisposition.contentDispositionStrict) {
                throw new ParseException("Expected disposition, got " + tk.getValue());
            }
        }
        else {
            this.disposition = tk.getValue();
        }
        final String rem = h.getRemainder();
        if (rem != null) {
            try {
                this.list = new ParameterList(rem);
            }
            catch (final ParseException px) {
                if (ContentDisposition.contentDispositionStrict) {
                    throw px;
                }
            }
        }
    }
    
    public String getDisposition() {
        return this.disposition;
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
    
    public void setDisposition(final String disposition) {
        this.disposition = disposition;
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
        if (this.disposition == null) {
            return "";
        }
        if (this.list == null) {
            return this.disposition;
        }
        final StringBuilder sb = new StringBuilder(this.disposition);
        sb.append(this.list.toString(sb.length() + 21));
        return sb.toString();
    }
    
    static {
        contentDispositionStrict = PropUtil.getBooleanSystemProperty("mail.mime.contentdisposition.strict", true);
    }
}
