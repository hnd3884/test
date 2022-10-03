package com.microsoft.sqlserver.jdbc;

import javax.xml.transform.sax.TransformerHandler;
import org.xml.sax.ContentHandler;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLInputFactory;
import org.xml.sax.XMLReader;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.EntityResolver;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import java.io.Reader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.io.IOException;
import java.util.logging.Level;
import java.io.ByteArrayInputStream;
import javax.xml.transform.TransformerException;
import java.text.MessageFormat;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.OutputStream;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;
import org.w3c.dom.Document;
import java.util.logging.Logger;
import java.sql.SQLXML;

final class SQLServerSQLXML implements SQLXML
{
    private final SQLServerConnection con;
    private final PLPXMLInputStream contents;
    private final InputStreamGetterArgs getterArgs;
    private final TypeInfo typeInfo;
    private boolean isUsed;
    private boolean isFreed;
    private static final Logger logger;
    private ByteArrayOutputStreamToInputStream outputStreamValue;
    private Document docValue;
    private String strValue;
    private static final AtomicInteger baseID;
    private final String traceID;
    
    @Override
    public final String toString() {
        return this.traceID;
    }
    
    private static int nextInstanceID() {
        return SQLServerSQLXML.baseID.incrementAndGet();
    }
    
    InputStream getValue() throws SQLServerException {
        this.checkClosed();
        if (!this.isUsed) {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_noDataXML"), null, true);
        }
        assert null == this.contents;
        ByteArrayInputStream o = null;
        if (null != this.outputStreamValue) {
            o = this.outputStreamValue.getInputStream();
            assert null == this.docValue;
            assert null == this.strValue;
        }
        else if (null != this.docValue) {
            assert null == this.outputStreamValue;
            assert null == this.strValue;
            final ByteArrayOutputStreamToInputStream strm = new ByteArrayOutputStreamToInputStream();
            try {
                final TransformerFactory factory = TransformerFactory.newInstance();
                factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
                factory.newTransformer().transform(new DOMSource(this.docValue), new StreamResult(strm));
            }
            catch (final TransformerException e) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_noParserSupport"));
                final Object[] msgArgs = { e.toString() };
                SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
            }
            o = strm.getInputStream();
        }
        else {
            assert null == this.outputStreamValue;
            assert null == this.docValue;
            assert null != this.strValue;
            o = new ByteArrayInputStream(this.strValue.getBytes(Encoding.UNICODE.charset()));
        }
        assert null != o;
        this.isFreed = true;
        return o;
    }
    
    SQLServerSQLXML(final SQLServerConnection connection) {
        this.isUsed = false;
        this.isFreed = false;
        this.contents = null;
        this.traceID = " SQLServerSQLXML:" + nextInstanceID();
        this.con = connection;
        if (SQLServerSQLXML.logger.isLoggable(Level.FINE)) {
            SQLServerSQLXML.logger.fine(this.toString() + " created by (" + connection.toString() + ")");
        }
        this.getterArgs = null;
        this.typeInfo = null;
    }
    
    SQLServerSQLXML(final InputStream stream, final InputStreamGetterArgs getterArgs, final TypeInfo typeInfo) throws SQLServerException {
        this.isUsed = false;
        this.isFreed = false;
        this.traceID = " SQLServerSQLXML:" + nextInstanceID();
        this.contents = (PLPXMLInputStream)stream;
        this.con = null;
        this.getterArgs = getterArgs;
        this.typeInfo = typeInfo;
        if (SQLServerSQLXML.logger.isLoggable(Level.FINE)) {
            SQLServerSQLXML.logger.fine(this.toString() + " created by (null connection)");
        }
    }
    
    InputStream getStream() {
        return this.contents;
    }
    
    @Override
    public void free() throws SQLException {
        if (!this.isFreed) {
            this.isFreed = true;
            if (null != this.contents) {
                try {
                    this.contents.close();
                }
                catch (final IOException e) {
                    SQLServerException.makeFromDriverError(null, null, e.getMessage(), null, true);
                }
            }
        }
    }
    
    private void checkClosed() throws SQLServerException {
        if (this.isFreed || (null != this.con && this.con.isClosed())) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_isFreed"));
            SQLServerException.makeFromDriverError(this.con, null, form.format(new Object[] { "SQLXML" }), null, true);
        }
    }
    
    private void checkReadXML() throws SQLException {
        if (null == this.contents) {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_writeOnlyXML"), null, true);
        }
        if (this.isUsed) {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_dataHasBeenReadXML"), null, true);
        }
        try {
            this.contents.checkClosed();
        }
        catch (final IOException e) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_isFreed"));
            SQLServerException.makeFromDriverError(this.con, null, form.format(new Object[] { "SQLXML" }), null, true);
        }
    }
    
    void checkWriteXML() throws SQLException {
        if (null != this.contents) {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_readOnlyXML"), null, true);
        }
        if (this.isUsed) {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_dataHasBeenSetXML"), null, true);
        }
    }
    
    @Override
    public InputStream getBinaryStream() throws SQLException {
        this.checkClosed();
        this.checkReadXML();
        this.isUsed = true;
        return this.contents;
    }
    
    @Override
    public OutputStream setBinaryStream() throws SQLException {
        this.checkClosed();
        this.checkWriteXML();
        this.isUsed = true;
        return this.outputStreamValue = new ByteArrayOutputStreamToInputStream();
    }
    
    @Override
    public Writer setCharacterStream() throws SQLException {
        this.checkClosed();
        this.checkWriteXML();
        this.isUsed = true;
        this.outputStreamValue = new ByteArrayOutputStreamToInputStream();
        return new OutputStreamWriter(this.outputStreamValue, Encoding.UNICODE.charset());
    }
    
    @Override
    public Reader getCharacterStream() throws SQLException {
        this.checkClosed();
        this.checkReadXML();
        this.isUsed = true;
        final StreamType type = StreamType.CHARACTER;
        final InputStreamGetterArgs newArgs = new InputStreamGetterArgs(type, this.getterArgs.isAdaptive, this.getterArgs.isStreaming, this.getterArgs.logContext);
        assert null != this.contents;
        try {
            this.contents.read();
            this.contents.read();
        }
        catch (final IOException e) {
            SQLServerException.makeFromDriverError(null, null, e.getMessage(), null, true);
        }
        final Reader rd = (Reader)DDC.convertStreamToObject(this.contents, this.typeInfo, type.getJDBCType(), newArgs);
        return rd;
    }
    
    @Override
    public String getString() throws SQLException {
        this.checkClosed();
        this.checkReadXML();
        this.isUsed = true;
        assert null != this.contents;
        try {
            this.contents.read();
            this.contents.read();
        }
        catch (final IOException e) {
            SQLServerException.makeFromDriverError(null, null, e.getMessage(), null, true);
        }
        final byte[] byteContents = this.contents.getBytes();
        return new String(byteContents, 0, byteContents.length, Encoding.UNICODE.charset());
    }
    
    @Override
    public void setString(final String value) throws SQLException {
        this.checkClosed();
        this.checkWriteXML();
        this.isUsed = true;
        if (null == value) {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_cantSetNull"), null, true);
        }
        this.strValue = value;
    }
    
    @Override
    public <T extends Source> T getSource(final Class<T> iface) throws SQLException {
        this.checkClosed();
        this.checkReadXML();
        if (null == iface) {
            final T src = (T)this.getSourceInternal(StreamSource.class);
            return src;
        }
        return (T)this.getSourceInternal((Class<Source>)iface);
    }
    
     <T extends Source> T getSourceInternal(final Class<T> iface) throws SQLException {
        this.isUsed = true;
        T src = null;
        if (DOMSource.class == iface) {
            src = iface.cast(this.getDOMSource());
        }
        else if (SAXSource.class == iface) {
            src = iface.cast(this.getSAXSource());
        }
        else if (StAXSource.class == iface) {
            src = iface.cast(this.getStAXSource());
        }
        else if (StreamSource.class == iface) {
            src = iface.cast(new StreamSource(this.contents));
        }
        else {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_notSupported"), null, true);
        }
        return src;
    }
    
    @Override
    public <T extends Result> T setResult(final Class<T> resultClass) throws SQLException {
        this.checkClosed();
        this.checkWriteXML();
        if (null == resultClass) {
            final T result = (T)this.setResultInternal(StreamResult.class);
            return result;
        }
        return (T)this.setResultInternal((Class<Result>)resultClass);
    }
    
     <T extends Result> T setResultInternal(final Class<T> resultClass) throws SQLException {
        this.isUsed = true;
        T result = null;
        if (DOMResult.class == resultClass) {
            result = resultClass.cast(this.getDOMResult());
        }
        else if (SAXResult.class == resultClass) {
            result = resultClass.cast(this.getSAXResult());
        }
        else if (StAXResult.class == resultClass) {
            result = resultClass.cast(this.getStAXResult());
        }
        else if (StreamResult.class == resultClass) {
            this.outputStreamValue = new ByteArrayOutputStreamToInputStream();
            result = resultClass.cast(new StreamResult(this.outputStreamValue));
        }
        else {
            SQLServerException.makeFromDriverError(this.con, null, SQLServerException.getErrString("R_notSupported"), null, true);
        }
        return result;
    }
    
    private DOMSource getDOMSource() throws SQLException {
        Document document = null;
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            final DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new SQLServerEntityResolver());
            try {
                document = builder.parse(this.contents);
            }
            catch (final IOException e) {
                final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_errorReadingStream"));
                final Object[] msgArgs = { e.toString() };
                SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), "", true);
            }
            final DOMSource inputSource = new DOMSource(document);
            return inputSource;
        }
        catch (final ParserConfigurationException e2) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_noParserSupport"));
            final Object[] msgArgs = { e2.toString() };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        catch (final SAXException e3) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_failedToParseXML"));
            final Object[] msgArgs = { e3.toString() };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        return null;
    }
    
    private SAXSource getSAXSource() throws SQLException {
        try {
            final InputSource src = new InputSource(this.contents);
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            final SAXParser parser = factory.newSAXParser();
            final XMLReader reader = parser.getXMLReader();
            final SAXSource saxSource = new SAXSource(reader, src);
            return saxSource;
        }
        catch (final SAXException | ParserConfigurationException e) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_failedToParseXML"));
            final Object[] msgArgs = { e.toString() };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
            return null;
        }
    }
    
    private StAXSource getStAXSource() throws SQLException {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            final XMLStreamReader r = factory.createXMLStreamReader(this.contents);
            final StAXSource result = new StAXSource(r);
            return result;
        }
        catch (final XMLStreamException e) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_noParserSupport"));
            final Object[] msgArgs = { e.toString() };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
            return null;
        }
    }
    
    private StAXResult getStAXResult() throws SQLException {
        final XMLOutputFactory factory = XMLOutputFactory.newInstance();
        this.outputStreamValue = new ByteArrayOutputStreamToInputStream();
        try {
            final XMLStreamWriter r = factory.createXMLStreamWriter(this.outputStreamValue);
            final StAXResult result = new StAXResult(r);
            return result;
        }
        catch (final XMLStreamException e) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_noParserSupport"));
            final Object[] msgArgs = { e.toString() };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
            return null;
        }
    }
    
    private SAXResult getSAXResult() throws SQLException {
        TransformerHandler handler = null;
        try {
            final SAXTransformerFactory stf = (SAXTransformerFactory)TransformerFactory.newInstance();
            stf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            handler = stf.newTransformerHandler();
        }
        catch (final TransformerConfigurationException e) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_noParserSupport"));
            final Object[] msgArgs = { e.toString() };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        catch (final ClassCastException e2) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_noParserSupport"));
            final Object[] msgArgs = { e2.toString() };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
        }
        this.outputStreamValue = new ByteArrayOutputStreamToInputStream();
        handler.setResult(new StreamResult(this.outputStreamValue));
        final SAXResult result = new SAXResult(handler);
        return result;
    }
    
    private DOMResult getDOMResult() throws SQLException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        assert null == this.outputStreamValue;
        try {
            final DocumentBuilder builder = factory.newDocumentBuilder();
            this.docValue = builder.newDocument();
            final DOMResult result = new DOMResult(this.docValue);
            return result;
        }
        catch (final ParserConfigurationException e) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_noParserSupport"));
            final Object[] msgArgs = { e.toString() };
            SQLServerException.makeFromDriverError(this.con, null, form.format(msgArgs), null, true);
            return null;
        }
    }
    
    static {
        logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerSQLXML");
        baseID = new AtomicInteger(0);
    }
}
