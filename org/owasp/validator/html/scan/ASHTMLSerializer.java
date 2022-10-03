package org.owasp.validator.html.scan;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.xml.serialize.ElementState;
import org.apache.xml.serialize.HTMLdtd;
import org.owasp.validator.html.InternalPolicy;
import org.apache.xml.serialize.OutputFormat;
import java.io.Writer;
import org.owasp.validator.html.TagMatcher;
import org.slf4j.Logger;
import org.apache.xml.serialize.HTMLSerializer;

public class ASHTMLSerializer extends HTMLSerializer
{
    private static final Logger logger;
    private boolean encodeAllPossibleEntities;
    private final TagMatcher requireClosingTags;
    private boolean encodeNonBreakingSpace;
    private boolean skipEscapeHtmlTags;
    
    public ASHTMLSerializer(final Writer w, final OutputFormat format, final InternalPolicy policy) {
        super(w, format);
        this.encodeNonBreakingSpace = true;
        this.skipEscapeHtmlTags = false;
        this.encodeAllPossibleEntities = policy.isEntityEncodeIntlCharacters();
        this.requireClosingTags = policy.getRequiresClosingTags();
        this.skipEscapeHtmlTags = "true".equalsIgnoreCase(policy.getDirective("skipEscapeHtmlTags"));
        this.encodeNonBreakingSpace = (policy.getDirective("encodeNonBreakingSpace") == null || "true".equalsIgnoreCase(policy.getDirective("encodeNonBreakingSpace")));
    }
    
    protected String getEntityRef(final int charToPrint) {
        if (charToPrint == 160 && this.encodeNonBreakingSpace) {
            return "nbsp";
        }
        if (this.skipEscapeHtmlTags) {
            return null;
        }
        if (this.encodeAllPossibleEntities || "<>\"'&:".indexOf(charToPrint) != -1) {
            return super.getEntityRef(charToPrint);
        }
        return null;
    }
    
    public void endElementIO(final String namespaceURI, final String localName, final String rawName) throws IOException {
        this._printer.unindent();
        ElementState state = this.getElementState();
        if (state.empty) {
            this._printer.printText('>');
        }
        if (rawName == null || this.requiresClosingTag(rawName) || !HTMLdtd.isOnlyOpening(rawName)) {
            if (this._indenting && !state.preserveSpace && state.afterElement) {
                this._printer.breakLine();
            }
            if (state.inCData) {
                this._printer.printText("]]>");
            }
            this._printer.printText("</");
            this._printer.printText(state.rawName);
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
    
    protected String escapeURI(final String uri) {
        final String originalURI = uri;
        try {
            this.printEscaped(uri);
        }
        catch (final IOException e) {
            ASHTMLSerializer.logger.error("URI escaping failed for value: " + originalURI);
        }
        return "";
    }
    
    static {
        logger = LoggerFactory.getLogger((Class)ASHTMLSerializer.class);
    }
}
