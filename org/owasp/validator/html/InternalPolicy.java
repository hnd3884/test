package org.owasp.validator.html;

import java.util.Map;
import com.zoho.security.validator.url.URLValidatorAPI;
import org.owasp.validator.html.model.Tag;

public class InternalPolicy extends Policy
{
    private final int maxInputSize;
    private final boolean isNofollowAnchors;
    private final boolean isValidateParamAsEmbed;
    private final boolean formatOutput;
    private final boolean preserveSpace;
    private final boolean omitXmlDeclaration;
    private final boolean omitDoctypeDeclaration;
    private final boolean entityEncodeIntlCharacters;
    private final boolean useXhtml;
    private final Tag embedTag;
    private final Tag styleTag;
    private final String onUnknownTag;
    private final boolean preserveComments;
    private final boolean embedStyleSheets;
    private final boolean isEncodeUnknownTag;
    private final boolean allowDynamicAttributes;
    private final boolean enableURLValidation;
    private final String attributeLimit;
    
    protected InternalPolicy(final ParseContext parseContext) throws PolicyException {
        super(parseContext);
        this.maxInputSize = this.determineMaxInputSize();
        this.isNofollowAnchors = this.isTrue("nofollowAnchors");
        this.isValidateParamAsEmbed = this.isTrue("validateParamAsEmbed");
        this.formatOutput = this.isTrue("formatOutput");
        this.preserveSpace = this.isTrue("preserveSpace");
        this.omitXmlDeclaration = this.isTrue("omitXmlDeclaration");
        this.omitDoctypeDeclaration = this.isTrue("omitDoctypeDeclaration");
        this.entityEncodeIntlCharacters = this.isTrue("entityEncodeIntlChars");
        this.useXhtml = this.isTrue("useXHTML");
        this.embedTag = this.getTagByLowercaseName("embed");
        this.onUnknownTag = this.getDirective("onUnknownTag");
        this.isEncodeUnknownTag = "encode".equals(this.onUnknownTag);
        this.preserveComments = this.isTrue("preserveComments");
        this.styleTag = this.getTagByLowercaseName("style");
        this.embedStyleSheets = this.isTrue("embedStyleSheets");
        this.allowDynamicAttributes = this.isTrue("allowDynamicAttributes");
        this.enableURLValidation = (this.getDirective("urlvalidation-enable") == null || this.isTrue("urlvalidation-enable"));
        if (this.enableURLValidation && this.urlValidator == null) {
            this.urlValidator = new URLValidatorAPI(true, true);
        }
        this.attributeLimit = this.getDirective("attributeLimit");
    }
    
    protected InternalPolicy(final Policy old, final Map<String, String> directives, final Map<String, Tag> tagRules) {
        super(old, directives, tagRules);
        this.maxInputSize = this.determineMaxInputSize();
        this.isNofollowAnchors = this.isTrue("nofollowAnchors");
        this.isValidateParamAsEmbed = this.isTrue("validateParamAsEmbed");
        this.formatOutput = this.isTrue("formatOutput");
        this.preserveSpace = this.isTrue("preserveSpace");
        this.omitXmlDeclaration = this.isTrue("omitXmlDeclaration");
        this.omitDoctypeDeclaration = this.isTrue("omitDoctypeDeclaration");
        this.entityEncodeIntlCharacters = this.isTrue("entityEncodeIntlChars");
        this.useXhtml = this.isTrue("useXHTML");
        this.embedTag = this.getTagByLowercaseName("embed");
        this.onUnknownTag = this.getDirective("onUnknownTag");
        this.isEncodeUnknownTag = "encode".equals(this.onUnknownTag);
        this.preserveComments = this.isTrue("preserveComments");
        this.styleTag = this.getTagByLowercaseName("style");
        this.embedStyleSheets = this.isTrue("embedStyleSheets");
        this.allowDynamicAttributes = this.isTrue("allowDynamicAttributes");
        this.enableURLValidation = (this.getDirective("urlvalidation-enable") == null || this.isTrue("urlvalidation-enable"));
        if (this.enableURLValidation && this.urlValidator == null) {
            this.urlValidator = new URLValidatorAPI(true, true);
        }
        this.attributeLimit = this.getDirective("attributeLimit");
    }
    
    public Tag getEmbedTag() {
        return this.embedTag;
    }
    
    public Tag getStyleTag() {
        return this.styleTag;
    }
    
    public boolean isEmbedStyleSheets() {
        return this.embedStyleSheets;
    }
    
    public boolean isPreserveComments() {
        return this.preserveComments;
    }
    
    public int getMaxInputSize() {
        return this.maxInputSize;
    }
    
    public boolean isEntityEncodeIntlCharacters() {
        return this.entityEncodeIntlCharacters;
    }
    
    public boolean isNofollowAnchors() {
        return this.isNofollowAnchors;
    }
    
    public boolean isValidateParamAsEmbed() {
        return this.isValidateParamAsEmbed;
    }
    
    public boolean isFormatOutput() {
        return this.formatOutput;
    }
    
    public boolean isPreserveSpace() {
        return this.preserveSpace;
    }
    
    public boolean isOmitXmlDeclaration() {
        return this.omitXmlDeclaration;
    }
    
    public boolean isUseXhtml() {
        return this.useXhtml;
    }
    
    public boolean isOmitDoctypeDeclaration() {
        return this.omitDoctypeDeclaration;
    }
    
    private boolean isTrue(final String anchorsNofollow) {
        return "true".equals(this.getDirective(anchorsNofollow));
    }
    
    public String getOnUnknownTag() {
        return this.onUnknownTag;
    }
    
    public boolean isEncodeUnknownTag() {
        return this.isEncodeUnknownTag;
    }
    
    public boolean isAllowDynamicAttributes() {
        return this.allowDynamicAttributes;
    }
    
    public int determineMaxInputSize() {
        int maxInputSize = 100000;
        try {
            maxInputSize = Integer.parseInt(this.getDirective("maxInputSize"));
        }
        catch (final NumberFormatException ex) {}
        return maxInputSize;
    }
    
    public String getAttributeLimit() {
        return this.attributeLimit;
    }
    
    public boolean isEnabledURLValidation() {
        return this.enableURLValidation;
    }
}
