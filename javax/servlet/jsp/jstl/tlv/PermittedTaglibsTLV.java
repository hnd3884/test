package javax.servlet.jsp.jstl.tlv;

import org.xml.sax.Attributes;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.servlet.jsp.tagext.ValidationMessage;
import javax.servlet.jsp.tagext.PageData;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.jsp.tagext.TagLibraryValidator;

public class PermittedTaglibsTLV extends TagLibraryValidator
{
    private static final String PERMITTED_TAGLIBS_PARAM = "permittedTaglibs";
    private static final String JSP_ROOT_URI = "http://java.sun.com/JSP/Page";
    private static final String JSP_ROOT_NAME = "root";
    private static final String JSP_ROOT_QN = "jsp:root";
    private static final PageParser parser;
    private final Set<String> permittedTaglibs;
    
    public PermittedTaglibsTLV() {
        this.permittedTaglibs = new HashSet<String>();
    }
    
    public void setInitParameters(final Map<String, Object> initParams) {
        super.setInitParameters((Map)initParams);
        this.permittedTaglibs.clear();
        final String uris = initParams.get("permittedTaglibs");
        if (uris != null) {
            final StringTokenizer st = new StringTokenizer(uris);
            while (st.hasMoreTokens()) {
                this.permittedTaglibs.add(st.nextToken());
            }
        }
    }
    
    public ValidationMessage[] validate(final String prefix, final String uri, final PageData page) {
        try {
            final PermittedTaglibsHandler h = new PermittedTaglibsHandler(prefix, uri);
            PermittedTaglibsTLV.parser.parse(page, h);
            return h.getResult();
        }
        catch (final SAXException ex) {
            return this.vmFromString(ex.toString());
        }
        catch (final ParserConfigurationException ex2) {
            return this.vmFromString(ex2.toString());
        }
        catch (final IOException ex3) {
            return this.vmFromString(ex3.toString());
        }
    }
    
    private ValidationMessage[] vmFromString(final String message) {
        return new ValidationMessage[] { new ValidationMessage((String)null, message) };
    }
    
    static {
        parser = new PageParser(false);
    }
    
    private class PermittedTaglibsHandler extends DefaultHandler
    {
        private final String prefix;
        private final String uri;
        private boolean failed;
        
        public PermittedTaglibsHandler(final String prefix, final String uri) {
            this.prefix = prefix;
            this.uri = uri;
        }
        
        @Override
        public void startElement(final String ns, final String ln, final String qn, final Attributes a) {
            if (qn.equals("jsp:root") || (ns.equals("http://java.sun.com/JSP/Page") && ln.equals("root"))) {
                for (int i = 0; i < a.getLength(); ++i) {
                    final String name = a.getQName(i);
                    if (name.startsWith("xmlns:")) {
                        final String value = a.getValue(i);
                        if (!value.equals(this.uri)) {
                            if (!value.equals("http://java.sun.com/JSP/Page")) {
                                if (!PermittedTaglibsTLV.this.permittedTaglibs.contains(value)) {
                                    this.failed = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        private ValidationMessage[] getResult() {
            if (this.failed) {
                return PermittedTaglibsTLV.this.vmFromString("taglib " + this.prefix + " (" + this.uri + ") allows only the " + "following taglibs to be imported: " + PermittedTaglibsTLV.this.permittedTaglibs);
            }
            return null;
        }
    }
}
