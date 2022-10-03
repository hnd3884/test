package org.owasp.validator.html.scan;

import java.io.IOException;
import org.apache.xml.serialize.ElementState;
import java.util.Locale;
import org.owasp.validator.html.InternalPolicy;
import org.apache.xml.serialize.OutputFormat;
import java.io.Writer;
import org.owasp.validator.html.TagMatcher;
import org.apache.xml.serialize.XHTMLSerializer;

public class ASXHTMLSerializer extends XHTMLSerializer
{
    private boolean encodeAllPossibleEntities;
    private final TagMatcher allowedEmptyTags;
    private final TagMatcher requireClosingTags;
    private boolean skipEscapeHtmlTags;
    private boolean skipEscapeSingleQuote;
    private boolean encodeNonBreakingSpace;
    
    public ASXHTMLSerializer(final Writer w, final OutputFormat format, final InternalPolicy policy) {
        super(w, format);
        this.skipEscapeHtmlTags = false;
        this.skipEscapeSingleQuote = false;
        this.encodeNonBreakingSpace = true;
        this.allowedEmptyTags = policy.getAllowedEmptyTags();
        this.requireClosingTags = policy.getRequiresClosingTags();
        this.encodeAllPossibleEntities = policy.isEntityEncodeIntlCharacters();
        this.skipEscapeHtmlTags = "true".equalsIgnoreCase(policy.getDirective("skipEscapeHtmlTags"));
        this.skipEscapeSingleQuote = "true".equalsIgnoreCase(policy.getDirective("skipEscapeSingleQuote"));
        this.encodeNonBreakingSpace = (policy.getDirective("encodeNonBreakingSpace") == null || "true".equalsIgnoreCase(policy.getDirective("encodeNonBreakingSpace")));
    }
    
    protected String getEntityRef(final int charToPrint) {
        if (charToPrint == 160 && this.encodeNonBreakingSpace) {
            return "nbsp";
        }
        if (this.skipEscapeHtmlTags) {
            return null;
        }
        if (!this.encodeAllPossibleEntities && "<>\"'&:".indexOf(charToPrint) == -1) {
            return null;
        }
        if (charToPrint == 39 && !this.skipEscapeSingleQuote) {
            return "#39";
        }
        return super.getEntityRef(charToPrint);
    }
    
    public void endElementIO(final String namespaceURI, final String localName, final String rawName) throws IOException {
        this._printer.unindent();
        ElementState state = this.getElementState();
        if (state.empty && this.isAllowedEmptyTag(rawName) && !this.requiresClosingTag(rawName)) {
            this._printer.printText(" />");
        }
        else {
            if (state.empty) {
                this._printer.printText('>');
            }
            if (state.inCData) {
                this._printer.printText("]]>");
            }
            this._printer.printText("</");
            this._printer.printText(state.rawName.toLowerCase(Locale.ENGLISH));
            this._printer.printText('>');
        }
        state = this.leaveElementState();
        if (rawName == null || (!rawName.equalsIgnoreCase("A") && !rawName.equalsIgnoreCase("TD"))) {
            state.afterElement = true;
        }
        state.empty = false;
        if (this.isDocumentState()) {
            this._printer.flush();
        }
    }
    
    private boolean requiresClosingTag(final String tagName) {
        return this.requireClosingTags.matches(tagName);
    }
    
    private boolean isAllowedEmptyTag(final String tagName) {
        return "head".equals(tagName) || this.allowedEmptyTags.matches(tagName);
    }
}
