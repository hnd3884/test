package org.w3c.tidy;

import java.util.HashMap;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.Writer;
import java.io.Reader;
import java.io.InputStream;
import org.w3c.dom.Document;
import java.util.Properties;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.io.Serializable;

public class Tidy implements Serializable
{
    static final long serialVersionUID = -2794371560623987718L;
    private static final Map CMDLINE_ALIAS;
    private PrintWriter errout;
    private PrintWriter stderr;
    private Configuration configuration;
    private String inputStreamName;
    private int parseErrors;
    private int parseWarnings;
    private Report report;
    
    public Tidy() {
        this.inputStreamName = "InputStream";
        this.report = new Report();
        this.configuration = new Configuration(this.report);
        final TagTable tt = new TagTable();
        tt.setConfiguration(this.configuration);
        this.configuration.tt = tt;
        this.configuration.errfile = null;
        this.stderr = new PrintWriter(System.err, true);
        this.errout = this.stderr;
    }
    
    public Configuration getConfiguration() {
        return this.configuration;
    }
    
    public PrintWriter getStderr() {
        return this.stderr;
    }
    
    public int getParseErrors() {
        return this.parseErrors;
    }
    
    public int getParseWarnings() {
        return this.parseWarnings;
    }
    
    public void setInputStreamName(final String inputStreamName) {
        if (inputStreamName != null) {
            this.inputStreamName = inputStreamName;
        }
    }
    
    public String getInputStreamName() {
        return this.inputStreamName;
    }
    
    public PrintWriter getErrout() {
        return this.errout;
    }
    
    public void setErrout(final PrintWriter errout) {
        this.errout = errout;
    }
    
    public void setConfigurationFromFile(final String s) {
        this.configuration.parseFile(s);
    }
    
    public void setConfigurationFromProps(final Properties properties) {
        this.configuration.addProps(properties);
    }
    
    public static Document createEmptyDocument() {
        final Node node = new Node((short)0, new byte[0], 0, 0);
        final Node node2 = new Node((short)5, new byte[0], 0, 0, "html", new TagTable());
        if (node != null && node2 != null) {
            node.insertNodeAtStart(node2);
            return (Document)node.getAdapter();
        }
        return null;
    }
    
    public Node parse(final InputStream inputStream, final OutputStream outputStream) {
        final StreamIn streamIn = StreamInFactory.getStreamIn(this.configuration, inputStream);
        Out out = null;
        if (outputStream != null) {
            out = OutFactory.getOut(this.configuration, outputStream);
        }
        return this.parse(streamIn, out);
    }
    
    public Node parse(final Reader reader, final OutputStream outputStream) {
        final StreamIn streamIn = StreamInFactory.getStreamIn(this.configuration, reader);
        Out out = null;
        if (outputStream != null) {
            out = OutFactory.getOut(this.configuration, outputStream);
        }
        return this.parse(streamIn, out);
    }
    
    public Node parse(final Reader reader, final Writer writer) {
        final StreamIn streamIn = StreamInFactory.getStreamIn(this.configuration, reader);
        Out out = null;
        if (writer != null) {
            out = OutFactory.getOut(this.configuration, writer);
        }
        return this.parse(streamIn, out);
    }
    
    public Node parse(final InputStream inputStream, final Writer writer) {
        final StreamIn streamIn = StreamInFactory.getStreamIn(this.configuration, inputStream);
        Out out = null;
        if (writer != null) {
            out = OutFactory.getOut(this.configuration, writer);
        }
        return this.parse(streamIn, out);
    }
    
    public Document parseDOM(final InputStream inputStream, final OutputStream outputStream) {
        final Node parse = this.parse(inputStream, outputStream);
        if (parse != null) {
            return (Document)parse.getAdapter();
        }
        return null;
    }
    
    public Document parseDOM(final Reader reader, final Writer writer) {
        final Node parse = this.parse(reader, writer);
        if (parse != null) {
            return (Document)parse.getAdapter();
        }
        return null;
    }
    
    public void pprint(final Document document, final OutputStream outputStream) {
        if (!(document instanceof DOMDocumentImpl)) {
            return;
        }
        this.pprint(((DOMDocumentImpl)document).adaptee, outputStream);
    }
    
    public void pprint(final org.w3c.dom.Node node, final OutputStream outputStream) {
        if (!(node instanceof DOMNodeImpl)) {
            return;
        }
        this.pprint(((DOMNodeImpl)node).adaptee, outputStream);
    }
    
    private Node parse(final StreamIn streamIn, final Out out) {
        if (this.errout == null) {
            return null;
        }
        this.configuration.adjust();
        this.parseErrors = 0;
        this.parseWarnings = 0;
        final Lexer lexer = new Lexer(streamIn, this.configuration, this.report);
        lexer.errout = this.errout;
        streamIn.setLexer(lexer);
        this.report.setFilename(this.inputStreamName);
        Node node;
        if (this.configuration.xmlTags) {
            node = ParserImpl.parseXMLDocument(lexer);
            if (!node.checkNodeIntegrity()) {
                if (!this.configuration.quiet) {
                    this.report.badTree(this.errout);
                }
                return null;
            }
        }
        else {
            lexer.warnings = 0;
            node = ParserImpl.parseDocument(lexer);
            if (!node.checkNodeIntegrity()) {
                if (!this.configuration.quiet) {
                    this.report.badTree(this.errout);
                }
                return null;
            }
            final Clean clean = new Clean(this.configuration.tt);
            clean.nestedEmphasis(node);
            clean.list2BQ(node);
            clean.bQ2Div(node);
            if (this.configuration.logicalEmphasis) {
                clean.emFromI(node);
            }
            if (this.configuration.word2000 && clean.isWord2000(node)) {
                clean.dropSections(lexer, node);
                clean.cleanWord2000(lexer, node);
            }
            if (this.configuration.makeClean || this.configuration.dropFontTags) {
                clean.cleanTree(lexer, node);
            }
            if (!node.checkNodeIntegrity()) {
                this.report.badTree(this.errout);
                return null;
            }
            Node node2 = node.findDocType();
            if (node2 != null) {
                node2 = node2.cloneNode(false);
            }
            if (node.content != null) {
                if (this.configuration.xHTML) {
                    lexer.setXHTMLDocType(node);
                }
                else {
                    lexer.fixDocType(node);
                }
                if (this.configuration.tidyMark) {
                    lexer.addGenerator(node);
                }
            }
            if (this.configuration.xmlOut && this.configuration.xmlPi) {
                lexer.fixXmlDecl(node);
            }
            if (!this.configuration.quiet && node.content != null) {
                this.report.reportVersion(this.errout, lexer, this.inputStreamName, node2);
            }
        }
        if (!this.configuration.quiet) {
            this.parseWarnings = lexer.warnings;
            this.parseErrors = lexer.errors;
            this.report.reportNumWarnings(this.errout, lexer);
        }
        if (!this.configuration.quiet && lexer.errors > 0 && !this.configuration.forceOutput) {
            this.report.needsAuthorIntervention(this.errout);
        }
        if (!this.configuration.onlyErrors && (lexer.errors == 0 || this.configuration.forceOutput)) {
            if (this.configuration.burstSlides) {
                final Node docType = node.findDocType();
                if (docType != null) {
                    Node.discardElement(docType);
                }
                final Lexer lexer2 = lexer;
                lexer2.versions |= 0x8;
                if (this.configuration.xHTML) {
                    lexer.setXHTMLDocType(node);
                }
                else {
                    lexer.fixDocType(node);
                }
                final Node body = node.findBody(this.configuration.tt);
                if (body != null) {
                    final PPrint pPrint = new PPrint(this.configuration);
                    if (!this.configuration.quiet) {
                        this.report.reportNumberOfSlides(this.errout, pPrint.countSlides(body));
                    }
                    pPrint.createSlides(lexer, node);
                }
                else if (!this.configuration.quiet) {
                    this.report.missingBody(this.errout);
                }
            }
            else if (out != null) {
                final PPrint pPrint2 = new PPrint(this.configuration);
                if (node.findDocType() == null) {
                    this.configuration.numEntities = true;
                }
                if (this.configuration.bodyOnly) {
                    pPrint2.printBody(out, lexer, node, this.configuration.xmlOut);
                }
                else if (this.configuration.xmlOut && !this.configuration.xHTML) {
                    pPrint2.printXMLTree(out, (short)0, 0, lexer, node);
                }
                else {
                    pPrint2.printTree(out, (short)0, 0, lexer, node);
                }
                pPrint2.flushLine(out, 0);
                out.flush();
            }
        }
        if (!this.configuration.quiet) {
            this.report.errorSummary(lexer);
        }
        return node;
    }
    
    private Node parse(InputStream in, final String inputStreamName, OutputStream outputStream) throws FileNotFoundException, IOException {
        Out out = null;
        boolean b = false;
        boolean b2 = false;
        if (inputStreamName != null) {
            in = new FileInputStream(inputStreamName);
            b = true;
            this.inputStreamName = inputStreamName;
        }
        else if (in == null) {
            in = System.in;
            this.inputStreamName = "stdin";
        }
        final StreamIn streamIn = StreamInFactory.getStreamIn(this.configuration, in);
        if (this.configuration.writeback && inputStreamName != null) {
            outputStream = new FileOutputStream(inputStreamName);
            b2 = true;
        }
        if (outputStream != null) {
            out = OutFactory.getOut(this.configuration, outputStream);
        }
        final Node parse = this.parse(streamIn, out);
        if (b) {
            try {
                in.close();
            }
            catch (final IOException ex) {}
        }
        if (b2) {
            try {
                outputStream.close();
            }
            catch (final IOException ex2) {}
        }
        return parse;
    }
    
    private void pprint(final Node node, final OutputStream outputStream) {
        if (outputStream != null) {
            final Out out = OutFactory.getOut(this.configuration, outputStream);
            final Lexer lexer = new Lexer(null, this.configuration, this.report);
            final PPrint pPrint = new PPrint(this.configuration);
            if (this.configuration.xmlTags) {
                pPrint.printXMLTree(out, (short)0, 0, lexer, node);
            }
            else {
                pPrint.printTree(out, (short)0, 0, lexer, node);
            }
            pPrint.flushLine(out, 0);
            out.flush();
        }
    }
    
    public static void main(final String[] array) {
        System.exit(new Tidy().mainExec(array));
    }
    
    protected int mainExec(final String[] array) {
        int i = array.length;
        int n = 0;
        final Properties properties = new Properties();
        while (i > 0) {
            if (array[n].startsWith("-")) {
                String s;
                for (s = array[n].toLowerCase(); s.length() > 0 && s.charAt(0) == '-'; s = s.substring(1)) {}
                if (s.equals("help") || s.equals("h") || s.equals("?")) {
                    this.report.helpText(new PrintWriter(System.out, true));
                    return 0;
                }
                if (s.equals("help-config")) {
                    this.configuration.printConfigOptions(new PrintWriter(System.out, true), false);
                    return 0;
                }
                if (s.equals("show-config")) {
                    this.configuration.adjust();
                    this.configuration.printConfigOptions(this.errout, true);
                    return 0;
                }
                if (s.equals("version") || s.equals("v")) {
                    this.report.showVersion(this.errout);
                    return 0;
                }
                String s2 = null;
                if (i > 2 && !array[n + 1].startsWith("-")) {
                    s2 = array[n + 1];
                    --i;
                    ++n;
                }
                final String s3 = Tidy.CMDLINE_ALIAS.get(s);
                if (s3 != null) {
                    s = s3;
                }
                if (Configuration.isKnownOption(s)) {
                    properties.setProperty(s, (s2 == null) ? "" : s2);
                }
                else if (s.equals("config")) {
                    if (s2 != null) {
                        this.configuration.parseFile(s2);
                    }
                }
                else if (TidyUtils.isCharEncodingSupported(s)) {
                    properties.setProperty("char-encoding", s);
                }
                else {
                    for (int j = 0; j < s.length(); ++j) {
                        switch (s.charAt(j)) {
                            case 'i': {
                                this.configuration.indentContent = true;
                                this.configuration.smartIndent = true;
                                break;
                            }
                            case 'o': {
                                this.configuration.hideEndTags = true;
                                break;
                            }
                            case 'u': {
                                this.configuration.upperCaseTags = true;
                                break;
                            }
                            case 'c': {
                                this.configuration.makeClean = true;
                                break;
                            }
                            case 'b': {
                                this.configuration.makeBare = true;
                                break;
                            }
                            case 'n': {
                                this.configuration.numEntities = true;
                                break;
                            }
                            case 'm': {
                                this.configuration.writeback = true;
                                break;
                            }
                            case 'e': {
                                this.configuration.onlyErrors = true;
                                break;
                            }
                            case 'q': {
                                this.configuration.quiet = true;
                                break;
                            }
                            default: {
                                this.report.unknownOption(this.errout, s.charAt(j));
                                break;
                            }
                        }
                    }
                }
                --i;
                ++n;
            }
            else {
                this.configuration.addProps(properties);
                this.configuration.adjust();
                if (this.configuration.errfile != null && !this.configuration.errfile.equals("stderr")) {
                    if (this.errout != this.stderr) {
                        this.errout.close();
                    }
                    try {
                        this.setErrout(new PrintWriter(new FileWriter(this.configuration.errfile), true));
                        final String errfile = this.configuration.errfile;
                    }
                    catch (final IOException ex) {
                        this.setErrout(this.stderr);
                    }
                }
                String s4;
                if (i > 0) {
                    s4 = array[n];
                }
                else {
                    s4 = "stdin";
                }
                try {
                    this.parse(null, s4, System.out);
                }
                catch (final FileNotFoundException ex2) {
                    this.report.unknownFile(this.errout, s4);
                }
                catch (final IOException ex3) {
                    this.report.unknownFile(this.errout, s4);
                }
                --i;
                ++n;
                if (i <= 0) {
                    break;
                }
                continue;
            }
        }
        if (this.parseErrors + this.parseWarnings > 0 && !this.configuration.quiet) {
            this.report.generalInfo(this.errout);
        }
        if (this.errout != this.stderr) {
            this.errout.close();
        }
        if (this.parseErrors > 0) {
            return 2;
        }
        if (this.parseWarnings > 0) {
            return 1;
        }
        return 0;
    }
    
    public void setMessageListener(final TidyMessageListener tidyMessageListener) {
        this.report.addMessageListener(tidyMessageListener);
    }
    
    public void setSpaces(final int spaces) {
        this.configuration.spaces = spaces;
    }
    
    public int getSpaces() {
        return this.configuration.spaces;
    }
    
    public void setWraplen(final int wraplen) {
        this.configuration.wraplen = wraplen;
    }
    
    public int getWraplen() {
        return this.configuration.wraplen;
    }
    
    public void setTabsize(final int tabsize) {
        this.configuration.tabsize = tabsize;
    }
    
    public int getTabsize() {
        return this.configuration.tabsize;
    }
    
    public void setErrfile(final String errfile) {
        this.configuration.errfile = errfile;
    }
    
    public String getErrfile() {
        return this.configuration.errfile;
    }
    
    public void setWriteback(final boolean writeback) {
        this.configuration.writeback = writeback;
    }
    
    public boolean getWriteback() {
        return this.configuration.writeback;
    }
    
    public void setOnlyErrors(final boolean onlyErrors) {
        this.configuration.onlyErrors = onlyErrors;
    }
    
    public boolean getOnlyErrors() {
        return this.configuration.onlyErrors;
    }
    
    public void setShowWarnings(final boolean showWarnings) {
        this.configuration.showWarnings = showWarnings;
    }
    
    public boolean getShowWarnings() {
        return this.configuration.showWarnings;
    }
    
    public void setQuiet(final boolean quiet) {
        this.configuration.quiet = quiet;
    }
    
    public boolean getQuiet() {
        return this.configuration.quiet;
    }
    
    public void setIndentContent(final boolean indentContent) {
        this.configuration.indentContent = indentContent;
    }
    
    public boolean getIndentContent() {
        return this.configuration.indentContent;
    }
    
    public void setSmartIndent(final boolean smartIndent) {
        this.configuration.smartIndent = smartIndent;
    }
    
    public boolean getSmartIndent() {
        return this.configuration.smartIndent;
    }
    
    public void setHideEndTags(final boolean hideEndTags) {
        this.configuration.hideEndTags = hideEndTags;
    }
    
    public boolean getHideEndTags() {
        return this.configuration.hideEndTags;
    }
    
    public void setXmlTags(final boolean xmlTags) {
        this.configuration.xmlTags = xmlTags;
    }
    
    public boolean getXmlTags() {
        return this.configuration.xmlTags;
    }
    
    public void setXmlOut(final boolean xmlOut) {
        this.configuration.xmlOut = xmlOut;
    }
    
    public boolean getXmlOut() {
        return this.configuration.xmlOut;
    }
    
    public void setXHTML(final boolean xhtml) {
        this.configuration.xHTML = xhtml;
    }
    
    public boolean getXHTML() {
        return this.configuration.xHTML;
    }
    
    public void setUpperCaseTags(final boolean upperCaseTags) {
        this.configuration.upperCaseTags = upperCaseTags;
    }
    
    public boolean getUpperCaseTags() {
        return this.configuration.upperCaseTags;
    }
    
    public void setUpperCaseAttrs(final boolean upperCaseAttrs) {
        this.configuration.upperCaseAttrs = upperCaseAttrs;
    }
    
    public boolean getUpperCaseAttrs() {
        return this.configuration.upperCaseAttrs;
    }
    
    public void setMakeClean(final boolean makeClean) {
        this.configuration.makeClean = makeClean;
    }
    
    public boolean getMakeClean() {
        return this.configuration.makeClean;
    }
    
    public void setMakeBare(final boolean makeBare) {
        this.configuration.makeBare = makeBare;
    }
    
    public boolean getMakeBare() {
        return this.configuration.makeBare;
    }
    
    public void setBreakBeforeBR(final boolean breakBeforeBR) {
        this.configuration.breakBeforeBR = breakBeforeBR;
    }
    
    public boolean getBreakBeforeBR() {
        return this.configuration.breakBeforeBR;
    }
    
    public void setBurstSlides(final boolean burstSlides) {
        this.configuration.burstSlides = burstSlides;
    }
    
    public boolean getBurstSlides() {
        return this.configuration.burstSlides;
    }
    
    public void setNumEntities(final boolean numEntities) {
        this.configuration.numEntities = numEntities;
    }
    
    public boolean getNumEntities() {
        return this.configuration.numEntities;
    }
    
    public void setQuoteMarks(final boolean quoteMarks) {
        this.configuration.quoteMarks = quoteMarks;
    }
    
    public boolean getQuoteMarks() {
        return this.configuration.quoteMarks;
    }
    
    public void setQuoteNbsp(final boolean quoteNbsp) {
        this.configuration.quoteNbsp = quoteNbsp;
    }
    
    public boolean getQuoteNbsp() {
        return this.configuration.quoteNbsp;
    }
    
    public void setQuoteAmpersand(final boolean quoteAmpersand) {
        this.configuration.quoteAmpersand = quoteAmpersand;
    }
    
    public boolean getQuoteAmpersand() {
        return this.configuration.quoteAmpersand;
    }
    
    public void setWrapAttVals(final boolean wrapAttVals) {
        this.configuration.wrapAttVals = wrapAttVals;
    }
    
    public boolean getWrapAttVals() {
        return this.configuration.wrapAttVals;
    }
    
    public void setWrapScriptlets(final boolean wrapScriptlets) {
        this.configuration.wrapScriptlets = wrapScriptlets;
    }
    
    public boolean getWrapScriptlets() {
        return this.configuration.wrapScriptlets;
    }
    
    public void setWrapSection(final boolean wrapSection) {
        this.configuration.wrapSection = wrapSection;
    }
    
    public boolean getWrapSection() {
        return this.configuration.wrapSection;
    }
    
    public void setAltText(final String altText) {
        this.configuration.altText = altText;
    }
    
    public String getAltText() {
        return this.configuration.altText;
    }
    
    public void setXmlPi(final boolean xmlPi) {
        this.configuration.xmlPi = xmlPi;
    }
    
    public boolean getXmlPi() {
        return this.configuration.xmlPi;
    }
    
    public void setDropFontTags(final boolean dropFontTags) {
        this.configuration.dropFontTags = dropFontTags;
    }
    
    public boolean getDropFontTags() {
        return this.configuration.dropFontTags;
    }
    
    public void setDropProprietaryAttributes(final boolean dropProprietaryAttributes) {
        this.configuration.dropProprietaryAttributes = dropProprietaryAttributes;
    }
    
    public boolean getDropProprietaryAttributes() {
        return this.configuration.dropProprietaryAttributes;
    }
    
    public void setDropEmptyParas(final boolean dropEmptyParas) {
        this.configuration.dropEmptyParas = dropEmptyParas;
    }
    
    public boolean getDropEmptyParas() {
        return this.configuration.dropEmptyParas;
    }
    
    public void setFixComments(final boolean fixComments) {
        this.configuration.fixComments = fixComments;
    }
    
    public boolean getFixComments() {
        return this.configuration.fixComments;
    }
    
    public void setWrapAsp(final boolean wrapAsp) {
        this.configuration.wrapAsp = wrapAsp;
    }
    
    public boolean getWrapAsp() {
        return this.configuration.wrapAsp;
    }
    
    public void setWrapJste(final boolean wrapJste) {
        this.configuration.wrapJste = wrapJste;
    }
    
    public boolean getWrapJste() {
        return this.configuration.wrapJste;
    }
    
    public void setWrapPhp(final boolean wrapPhp) {
        this.configuration.wrapPhp = wrapPhp;
    }
    
    public boolean getWrapPhp() {
        return this.configuration.wrapPhp;
    }
    
    public void setFixBackslash(final boolean fixBackslash) {
        this.configuration.fixBackslash = fixBackslash;
    }
    
    public boolean getFixBackslash() {
        return this.configuration.fixBackslash;
    }
    
    public void setIndentAttributes(final boolean indentAttributes) {
        this.configuration.indentAttributes = indentAttributes;
    }
    
    public boolean getIndentAttributes() {
        return this.configuration.indentAttributes;
    }
    
    public void setDocType(final String s) {
        if (s != null) {
            this.configuration.docTypeStr = (String)ParsePropertyImpl.DOCTYPE.parse(s, "doctype", this.configuration);
        }
    }
    
    public String getDocType() {
        String docTypeStr = null;
        switch (this.configuration.docTypeMode) {
            case 0: {
                docTypeStr = "omit";
                break;
            }
            case 1: {
                docTypeStr = "auto";
                break;
            }
            case 2: {
                docTypeStr = "strict";
                break;
            }
            case 3: {
                docTypeStr = "loose";
                break;
            }
            case 4: {
                docTypeStr = this.configuration.docTypeStr;
                break;
            }
        }
        return docTypeStr;
    }
    
    public void setLogicalEmphasis(final boolean logicalEmphasis) {
        this.configuration.logicalEmphasis = logicalEmphasis;
    }
    
    public boolean getLogicalEmphasis() {
        return this.configuration.logicalEmphasis;
    }
    
    public void setXmlPIs(final boolean xmlPIs) {
        this.configuration.xmlPIs = xmlPIs;
    }
    
    public boolean getXmlPIs() {
        return this.configuration.xmlPIs;
    }
    
    public void setEncloseText(final boolean encloseBodyText) {
        this.configuration.encloseBodyText = encloseBodyText;
    }
    
    public boolean getEncloseText() {
        return this.configuration.encloseBodyText;
    }
    
    public void setEncloseBlockText(final boolean encloseBlockText) {
        this.configuration.encloseBlockText = encloseBlockText;
    }
    
    public boolean getEncloseBlockText() {
        return this.configuration.encloseBlockText;
    }
    
    public void setWord2000(final boolean word2000) {
        this.configuration.word2000 = word2000;
    }
    
    public boolean getWord2000() {
        return this.configuration.word2000;
    }
    
    public void setTidyMark(final boolean tidyMark) {
        this.configuration.tidyMark = tidyMark;
    }
    
    public boolean getTidyMark() {
        return this.configuration.tidyMark;
    }
    
    public void setXmlSpace(final boolean xmlSpace) {
        this.configuration.xmlSpace = xmlSpace;
    }
    
    public boolean getXmlSpace() {
        return this.configuration.xmlSpace;
    }
    
    public void setEmacs(final boolean emacs) {
        this.configuration.emacs = emacs;
    }
    
    public boolean getEmacs() {
        return this.configuration.emacs;
    }
    
    public void setLiteralAttribs(final boolean literalAttribs) {
        this.configuration.literalAttribs = literalAttribs;
    }
    
    public boolean getLiteralAttribs() {
        return this.configuration.literalAttribs;
    }
    
    public void setPrintBodyOnly(final boolean bodyOnly) {
        this.configuration.bodyOnly = bodyOnly;
    }
    
    public boolean getPrintBodyOnly() {
        return this.configuration.bodyOnly;
    }
    
    public void setFixUri(final boolean fixUri) {
        this.configuration.fixUri = fixUri;
    }
    
    public boolean getFixUri() {
        return this.configuration.fixUri;
    }
    
    public void setLowerLiterals(final boolean lowerLiterals) {
        this.configuration.lowerLiterals = lowerLiterals;
    }
    
    public boolean getLowerLiterals() {
        return this.configuration.lowerLiterals;
    }
    
    public void setHideComments(final boolean hideComments) {
        this.configuration.hideComments = hideComments;
    }
    
    public boolean getHideComments() {
        return this.configuration.hideComments;
    }
    
    public void setIndentCdata(final boolean indentCdata) {
        this.configuration.indentCdata = indentCdata;
    }
    
    public boolean getIndentCdata() {
        return this.configuration.indentCdata;
    }
    
    public void setForceOutput(final boolean forceOutput) {
        this.configuration.forceOutput = forceOutput;
    }
    
    public boolean getForceOutput() {
        return this.configuration.forceOutput;
    }
    
    public void setShowErrors(final int showErrors) {
        this.configuration.showErrors = showErrors;
    }
    
    public int getShowErrors() {
        return this.configuration.showErrors;
    }
    
    public void setAsciiChars(final boolean asciiChars) {
        this.configuration.asciiChars = asciiChars;
    }
    
    public boolean getAsciiChars() {
        return this.configuration.asciiChars;
    }
    
    public void setJoinClasses(final boolean joinClasses) {
        this.configuration.joinClasses = joinClasses;
    }
    
    public boolean getJoinClasses() {
        return this.configuration.joinClasses;
    }
    
    public void setJoinStyles(final boolean joinStyles) {
        this.configuration.joinStyles = joinStyles;
    }
    
    public boolean getJoinStyles() {
        return this.configuration.joinStyles;
    }
    
    public void setTrimEmptyElements(final boolean trimEmpty) {
        this.configuration.trimEmpty = trimEmpty;
    }
    
    public boolean getTrimEmptyElements() {
        return this.configuration.trimEmpty;
    }
    
    public void setReplaceColor(final boolean replaceColor) {
        this.configuration.replaceColor = replaceColor;
    }
    
    public boolean getReplaceColor() {
        return this.configuration.replaceColor;
    }
    
    public void setEscapeCdata(final boolean escapeCdata) {
        this.configuration.escapeCdata = escapeCdata;
    }
    
    public boolean getEscapeCdata() {
        return this.configuration.escapeCdata;
    }
    
    public void setRepeatedAttributes(final int duplicateAttrs) {
        this.configuration.duplicateAttrs = duplicateAttrs;
    }
    
    public int getRepeatedAttributes() {
        return this.configuration.duplicateAttrs;
    }
    
    public void setKeepFileTimes(final boolean keepFileTimes) {
        this.configuration.keepFileTimes = keepFileTimes;
    }
    
    public boolean getKeepFileTimes() {
        return this.configuration.keepFileTimes;
    }
    
    public void setRawOut(final boolean rawOut) {
        this.configuration.rawOut = rawOut;
    }
    
    public boolean getRawOut() {
        return this.configuration.rawOut;
    }
    
    public void setInputEncoding(final String inCharEncodingName) {
        this.configuration.setInCharEncodingName(inCharEncodingName);
    }
    
    public String getInputEncoding() {
        return this.configuration.getInCharEncodingName();
    }
    
    public void setOutputEncoding(final String outCharEncodingName) {
        this.configuration.setOutCharEncodingName(outCharEncodingName);
    }
    
    public String getOutputEncoding() {
        return this.configuration.getOutCharEncodingName();
    }
    
    static {
        (CMDLINE_ALIAS = new HashMap()).put("xml", "input-xml");
        Tidy.CMDLINE_ALIAS.put("xml", "output-xhtml");
        Tidy.CMDLINE_ALIAS.put("asxml", "output-xhtml");
        Tidy.CMDLINE_ALIAS.put("ashtml", "output-html");
        Tidy.CMDLINE_ALIAS.put("omit", "hide-endtags");
        Tidy.CMDLINE_ALIAS.put("upper", "uppercase-tags");
        Tidy.CMDLINE_ALIAS.put("raw", "output-raw");
        Tidy.CMDLINE_ALIAS.put("numeric", "numeric-entities");
        Tidy.CMDLINE_ALIAS.put("change", "write-back");
        Tidy.CMDLINE_ALIAS.put("update", "write-back");
        Tidy.CMDLINE_ALIAS.put("modify", "write-back");
        Tidy.CMDLINE_ALIAS.put("errors", "only-errors");
        Tidy.CMDLINE_ALIAS.put("slides", "split");
        Tidy.CMDLINE_ALIAS.put("lang", "language");
        Tidy.CMDLINE_ALIAS.put("w", "wrap");
        Tidy.CMDLINE_ALIAS.put("file", "error-file");
        Tidy.CMDLINE_ALIAS.put("f", "error-file");
    }
}
