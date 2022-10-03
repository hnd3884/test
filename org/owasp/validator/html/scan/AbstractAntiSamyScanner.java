package org.owasp.validator.html.scan;

import org.apache.xml.serialize.HTMLSerializer;
import java.io.Writer;
import org.apache.xml.serialize.OutputFormat;
import org.owasp.validator.html.util.ErrorMessageUtil;
import java.util.MissingResourceException;
import org.owasp.validator.html.PolicyException;
import java.util.ArrayList;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.CleanResults;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.List;
import org.owasp.validator.html.InternalPolicy;

public abstract class AbstractAntiSamyScanner
{
    protected final InternalPolicy policy;
    protected final List<String> errorMessages;
    protected static final ResourceBundle messages;
    protected final Locale locale;
    protected boolean isNofollowAnchors;
    protected boolean isValidateParamAsEmbed;
    
    public abstract CleanResults scan(final String p0) throws ScanException;
    
    public abstract CleanResults getResults();
    
    public AbstractAntiSamyScanner(final Policy policy) {
        this.errorMessages = new ArrayList<String>();
        this.locale = Locale.getDefault();
        this.isNofollowAnchors = false;
        this.isValidateParamAsEmbed = false;
        assert policy instanceof InternalPolicy : policy.getClass();
        this.policy = (InternalPolicy)policy;
    }
    
    public AbstractAntiSamyScanner() throws PolicyException {
        this.errorMessages = new ArrayList<String>();
        this.locale = Locale.getDefault();
        this.isNofollowAnchors = false;
        this.isValidateParamAsEmbed = false;
        this.policy = (InternalPolicy)Policy.getInstance();
    }
    
    public static ResourceBundle getResourceBundle() {
        try {
            return ResourceBundle.getBundle("AntiSamy", Locale.getDefault());
        }
        catch (final MissingResourceException mre) {
            return ResourceBundle.getBundle("AntiSamy", new Locale("en", "US"));
        }
    }
    
    protected void addError(final String errorKey, final Object[] objs) {
        this.errorMessages.add(ErrorMessageUtil.getMessage(AbstractAntiSamyScanner.messages, errorKey, objs));
    }
    
    protected OutputFormat getOutputFormat() {
        final OutputFormat format = new OutputFormat();
        format.setOmitXMLDeclaration(this.policy.isOmitXmlDeclaration());
        format.setOmitDocumentType(this.policy.isOmitDoctypeDeclaration());
        format.setPreserveEmptyAttributes(true);
        format.setPreserveSpace(this.policy.isPreserveSpace());
        if (this.policy.isFormatOutput()) {
            format.setLineWidth(80);
            format.setIndenting(true);
            format.setIndent(2);
        }
        return format;
    }
    
    protected HTMLSerializer getHTMLSerializer(final Writer w, final OutputFormat format) {
        if (this.policy.isUseXhtml()) {
            return (HTMLSerializer)new ASXHTMLSerializer(w, format, this.policy);
        }
        return new ASHTMLSerializer(w, format, this.policy);
    }
    
    protected String trim(final String original, String cleaned) {
        if (cleaned.endsWith("\n") && !original.endsWith("\n")) {
            if (cleaned.endsWith("\r\n")) {
                cleaned = cleaned.substring(0, cleaned.length() - 2);
            }
            else if (cleaned.endsWith("\n")) {
                cleaned = cleaned.substring(0, cleaned.length() - 1);
            }
        }
        return cleaned;
    }
    
    static {
        messages = getResourceBundle();
    }
}
