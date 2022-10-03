package jdk.internal.util.xml.impl;

import jdk.internal.org.xml.sax.SAXParseException;
import jdk.internal.org.xml.sax.Attributes;
import java.io.InputStream;
import jdk.internal.org.xml.sax.SAXException;
import java.io.IOException;
import jdk.internal.org.xml.sax.InputSource;
import jdk.internal.org.xml.sax.EntityResolver;
import jdk.internal.org.xml.sax.ErrorHandler;
import jdk.internal.org.xml.sax.DTDHandler;
import jdk.internal.org.xml.sax.ContentHandler;
import jdk.internal.org.xml.sax.helpers.DefaultHandler;
import jdk.internal.org.xml.sax.Locator;
import jdk.internal.org.xml.sax.XMLReader;

final class ParserSAX extends Parser implements XMLReader, Locator
{
    public static final String FEATURE_NS = "http://xml.org/sax/features/namespaces";
    public static final String FEATURE_PREF = "http://xml.org/sax/features/namespace-prefixes";
    private boolean mFNamespaces;
    private boolean mFPrefixes;
    private DefaultHandler mHand;
    private ContentHandler mHandCont;
    private DTDHandler mHandDtd;
    private ErrorHandler mHandErr;
    private EntityResolver mHandEnt;
    
    public ParserSAX() {
        this.mFNamespaces = true;
        this.mFPrefixes = false;
        this.mHand = new DefaultHandler();
        this.mHandCont = this.mHand;
        this.mHandDtd = this.mHand;
        this.mHandErr = this.mHand;
        this.mHandEnt = this.mHand;
    }
    
    @Override
    public ContentHandler getContentHandler() {
        return (this.mHandCont != this.mHand) ? this.mHandCont : null;
    }
    
    @Override
    public void setContentHandler(final ContentHandler mHandCont) {
        if (mHandCont == null) {
            throw new NullPointerException();
        }
        this.mHandCont = mHandCont;
    }
    
    @Override
    public DTDHandler getDTDHandler() {
        return (this.mHandDtd != this.mHand) ? this.mHandDtd : null;
    }
    
    @Override
    public void setDTDHandler(final DTDHandler mHandDtd) {
        if (mHandDtd == null) {
            throw new NullPointerException();
        }
        this.mHandDtd = mHandDtd;
    }
    
    @Override
    public ErrorHandler getErrorHandler() {
        return (this.mHandErr != this.mHand) ? this.mHandErr : null;
    }
    
    @Override
    public void setErrorHandler(final ErrorHandler mHandErr) {
        if (mHandErr == null) {
            throw new NullPointerException();
        }
        this.mHandErr = mHandErr;
    }
    
    @Override
    public EntityResolver getEntityResolver() {
        return (this.mHandEnt != this.mHand) ? this.mHandEnt : null;
    }
    
    @Override
    public void setEntityResolver(final EntityResolver mHandEnt) {
        if (mHandEnt == null) {
            throw new NullPointerException();
        }
        this.mHandEnt = mHandEnt;
    }
    
    @Override
    public String getPublicId() {
        return (this.mInp != null) ? this.mInp.pubid : null;
    }
    
    @Override
    public String getSystemId() {
        return (this.mInp != null) ? this.mInp.sysid : null;
    }
    
    @Override
    public int getLineNumber() {
        return -1;
    }
    
    @Override
    public int getColumnNumber() {
        return -1;
    }
    
    @Override
    public void parse(final String s) throws IOException, SAXException {
        this.parse(new InputSource(s));
    }
    
    @Override
    public void parse(final InputSource inputSource) throws IOException, SAXException {
        if (inputSource == null) {
            throw new IllegalArgumentException("");
        }
        this.mInp = new Input(512);
        this.mPh = -1;
        try {
            this.setinp(inputSource);
        }
        catch (final SAXException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final RuntimeException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            this.panic(ex4.toString());
        }
        this.parse();
    }
    
    public void parse(final InputStream inputStream, final DefaultHandler defaultHandler) throws SAXException, IOException {
        if (inputStream == null || defaultHandler == null) {
            throw new IllegalArgumentException("");
        }
        this.parse(new InputSource(inputStream), defaultHandler);
    }
    
    public void parse(final InputSource inputSource, final DefaultHandler defaultHandler) throws SAXException, IOException {
        if (inputSource == null || defaultHandler == null) {
            throw new IllegalArgumentException("");
        }
        this.mHandCont = defaultHandler;
        this.mHandDtd = defaultHandler;
        this.mHandErr = defaultHandler;
        this.mHandEnt = defaultHandler;
        this.mInp = new Input(512);
        this.mPh = -1;
        try {
            this.setinp(inputSource);
        }
        catch (final SAXException | IOException | RuntimeException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.panic(ex2.toString());
        }
        this.parse();
    }
    
    private void parse() throws SAXException, IOException {
        this.init();
        try {
            this.mHandCont.setDocumentLocator(this);
            this.mHandCont.startDocument();
            if (this.mPh != 1) {
                this.mPh = 1;
            }
            int n;
            do {
                this.wsskip();
                switch (n = this.step()) {
                    case 1:
                    case 2: {
                        this.mPh = 4;
                        continue;
                    }
                    case 6:
                    case 8: {
                        continue;
                    }
                    case 9: {
                        if (this.mPh >= 3) {
                            this.panic("");
                        }
                        this.mPh = 3;
                        continue;
                    }
                    default: {
                        this.panic("");
                        continue;
                    }
                }
            } while (this.mPh < 4);
            do {
                switch (n) {
                    case 1:
                    case 2: {
                        if (this.mIsNSAware) {
                            this.mHandCont.startElement(this.mElm.value, this.mElm.name, "", this.mAttrs);
                        }
                        else {
                            this.mHandCont.startElement("", "", this.mElm.name, this.mAttrs);
                        }
                        if (n == 2) {
                            n = this.step();
                            continue;
                        }
                    }
                    case 3: {
                        if (this.mIsNSAware) {
                            this.mHandCont.endElement(this.mElm.value, this.mElm.name, "");
                        }
                        else {
                            this.mHandCont.endElement("", "", this.mElm.name);
                        }
                        while (this.mPref.list == this.mElm) {
                            this.mHandCont.endPrefixMapping(this.mPref.name);
                            this.mPref = this.del(this.mPref);
                        }
                        this.mElm = this.del(this.mElm);
                        if (this.mElm == null) {
                            this.mPh = 5;
                            continue;
                        }
                        n = this.step();
                        continue;
                    }
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 10: {
                        n = this.step();
                        continue;
                    }
                    default: {
                        this.panic("");
                        continue;
                    }
                }
            } while (this.mPh == 4);
            while (this.wsskip() != '\uffff') {
                switch (this.step()) {
                    case 6:
                    case 8: {
                        break;
                    }
                    default: {
                        this.panic("");
                        break;
                    }
                }
                if (this.mPh != 5) {
                    this.mPh = 6;
                }
            }
        }
        catch (final SAXException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final RuntimeException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            this.panic(ex4.toString());
        }
        finally {
            this.mHandCont.endDocument();
            this.cleanup();
        }
    }
    
    @Override
    protected void docType(final String s, final String s2, final String s3) throws SAXException {
        this.mHandDtd.notationDecl(s, s2, s3);
    }
    
    @Override
    protected void comm(final char[] array, final int n) {
    }
    
    @Override
    protected void pi(final String s, final String s2) throws SAXException {
        this.mHandCont.processingInstruction(s, s2);
    }
    
    @Override
    protected void newPrefix() throws SAXException {
        this.mHandCont.startPrefixMapping(this.mPref.name, this.mPref.value);
    }
    
    @Override
    protected void skippedEnt(final String s) throws SAXException {
        this.mHandCont.skippedEntity(s);
    }
    
    @Override
    protected InputSource resolveEnt(final String s, final String s2, final String s3) throws SAXException, IOException {
        return this.mHandEnt.resolveEntity(s2, s3);
    }
    
    @Override
    protected void notDecl(final String s, final String s2, final String s3) throws SAXException {
        this.mHandDtd.notationDecl(s, s2, s3);
    }
    
    @Override
    protected void unparsedEntDecl(final String s, final String s2, final String s3, final String s4) throws SAXException {
        this.mHandDtd.unparsedEntityDecl(s, s2, s3, s4);
    }
    
    @Override
    protected void panic(final String s) throws SAXException {
        final SAXParseException ex = new SAXParseException(s, this);
        this.mHandErr.fatalError(ex);
        throw ex;
    }
    
    @Override
    protected void bflash() throws SAXException {
        if (this.mBuffIdx >= 0) {
            this.mHandCont.characters(this.mBuff, 0, this.mBuffIdx + 1);
            this.mBuffIdx = -1;
        }
    }
    
    @Override
    protected void bflash_ws() throws SAXException {
        if (this.mBuffIdx >= 0) {
            this.mHandCont.characters(this.mBuff, 0, this.mBuffIdx + 1);
            this.mBuffIdx = -1;
        }
    }
    
    @Override
    public boolean getFeature(final String s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setFeature(final String s, final boolean b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public Object getProperty(final String s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setProperty(final String s, final Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
