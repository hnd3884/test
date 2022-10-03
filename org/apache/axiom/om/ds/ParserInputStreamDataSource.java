package org.apache.axiom.om.ds;

import org.apache.axiom.om.util.CommonUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.axiom.attachments.utils.BAAInputStream;
import org.apache.axiom.attachments.utils.BAAOutputStream;
import org.apache.axiom.om.OMDataSourceExt;
import java.io.ByteArrayOutputStream;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.util.StAXUtils;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.attachments.impl.BufferUtils;
import org.apache.axiom.om.OMOutputFormat;
import java.io.OutputStream;
import java.io.InputStream;
import org.apache.commons.logging.Log;

public class ParserInputStreamDataSource extends OMDataSourceExtBase
{
    private static final Log log;
    private Data data;
    private static final int defaultBehavior = 1;
    
    public ParserInputStreamDataSource(final InputStream payload, final String encoding) {
        this(payload, encoding, 1);
    }
    
    public ParserInputStreamDataSource(final InputStream payload, final String encoding, final int behavior) {
        this.data = null;
        this.data = new Data(payload, (encoding != null) ? encoding : "UTF-8", behavior);
    }
    
    @Override
    public void serialize(final OutputStream output, final OMOutputFormat format) throws XMLStreamException {
        if (ParserInputStreamDataSource.log.isDebugEnabled()) {
            ParserInputStreamDataSource.log.debug((Object)"Entry ParserInputStreamDataSource.serialize(OutputStream, OMOutputFormat");
        }
        final String encoding = (format != null) ? format.getCharSetEncoding() : null;
        try {
            if (!this.data.encoding.equalsIgnoreCase(encoding)) {
                final byte[] bytes = this.getXMLBytes(encoding);
                output.write(bytes);
            }
            else {
                final InputStream is = this.data.readParserInputStream();
                if (is != null) {
                    BufferUtils.inputStream2OutputStream(is, output);
                }
            }
            if (ParserInputStreamDataSource.log.isDebugEnabled()) {
                ParserInputStreamDataSource.log.debug((Object)"Exit ParserInputStreamDataSource.serialize(OutputStream, OMOutputFormat");
            }
        }
        catch (final UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        }
        catch (final IOException e2) {
            throw new XMLStreamException(e2);
        }
    }
    
    @Override
    public void serialize(final XMLStreamWriter xmlWriter) throws XMLStreamException {
        if (ParserInputStreamDataSource.log.isDebugEnabled()) {
            ParserInputStreamDataSource.log.debug((Object)"Entry ParserInputStreamDataSource.serialize(XMLStreamWriter)");
        }
        super.serialize(xmlWriter);
        if (ParserInputStreamDataSource.log.isDebugEnabled()) {
            ParserInputStreamDataSource.log.debug((Object)"Exit ParserInputStreamDataSource.serialize(XMLStreamWriter)");
        }
    }
    
    public XMLStreamReader getReader() throws XMLStreamException {
        if (ParserInputStreamDataSource.log.isDebugEnabled()) {
            ParserInputStreamDataSource.log.debug((Object)"Entry ParserInputStreamDataSource.getReader()");
        }
        final InputStream is = this.data.readParserInputStream();
        if (is == null && ParserInputStreamDataSource.log.isDebugEnabled()) {
            ParserInputStreamDataSource.log.warn((Object)"Parser content has already been read");
        }
        final XMLStreamReader reader = StAXUtils.createXMLStreamReader(is, this.data.encoding);
        if (ParserInputStreamDataSource.log.isDebugEnabled()) {
            ParserInputStreamDataSource.log.debug((Object)"Exit ParserInputStreamDataSource.getReader()");
        }
        return reader;
    }
    
    @Override
    public InputStream getXMLInputStream(final String encoding) throws UnsupportedEncodingException {
        try {
            return this.data.readParserInputStream();
        }
        catch (final XMLStreamException e) {
            throw new OMException(e);
        }
    }
    
    public int numReads() {
        return this.data.numReads;
    }
    
    public Object getObject() {
        return this.data;
    }
    
    public boolean isDestructiveRead() {
        return this.data.behavior == 0;
    }
    
    public boolean isDestructiveWrite() {
        return this.data.behavior == 0;
    }
    
    public byte[] getXMLBytes(final String encoding) {
        if (ParserInputStreamDataSource.log.isDebugEnabled()) {
            ParserInputStreamDataSource.log.debug((Object)"Entry ParserInputStreamDataSource.getXMLBytes(encoding)");
        }
        try {
            final InputStream is = this.data.readParserInputStream();
            if (is != null) {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final OMOutputFormat format = new OMOutputFormat();
                format.setCharSetEncoding(encoding);
                try {
                    BufferUtils.inputStream2OutputStream(is, baos);
                    if (ParserInputStreamDataSource.log.isDebugEnabled()) {
                        ParserInputStreamDataSource.log.debug((Object)"Exit ParserInputStreamDataSource.getXMLBytes(encoding)");
                    }
                    return baos.toByteArray();
                }
                catch (final IOException e) {
                    throw new OMException(e);
                }
            }
            if (ParserInputStreamDataSource.log.isDebugEnabled()) {
                ParserInputStreamDataSource.log.warn((Object)"Parser was already read, recovering by just returning new byte[0]");
                ParserInputStreamDataSource.log.debug((Object)"Exit ParserInputStreamDataSource.getXMLBytes(encoding)");
            }
            return new byte[0];
        }
        catch (final XMLStreamException e2) {
            throw new OMException(e2);
        }
    }
    
    public void close() {
        if (ParserInputStreamDataSource.log.isDebugEnabled()) {
            ParserInputStreamDataSource.log.debug((Object)"Entry ParserInputStreamDataSource.close()");
        }
        if (this.data.payload != null) {
            try {
                this.data.payload.close();
            }
            catch (final IOException e) {
                throw new OMException(e);
            }
            this.data.payload = null;
        }
        if (ParserInputStreamDataSource.log.isDebugEnabled()) {
            ParserInputStreamDataSource.log.debug((Object)"Exit ParserInputStreamDataSource.close()");
        }
    }
    
    public OMDataSourceExt copy() {
        if (ParserInputStreamDataSource.log.isDebugEnabled()) {
            ParserInputStreamDataSource.log.debug((Object)"Enter ParserInputStreamDataSource.copy()");
        }
        try {
            final BAAOutputStream baaos = new BAAOutputStream();
            BufferUtils.inputStream2OutputStream(this.data.readParserInputStream(), baaos);
            final BAAInputStream baais = new BAAInputStream(baaos.buffers(), baaos.length());
            if (ParserInputStreamDataSource.log.isDebugEnabled()) {
                ParserInputStreamDataSource.log.debug((Object)"Exit ParserInputStreamDataSource.copy()");
            }
            return new ParserInputStreamDataSource(baais, this.data.encoding, this.data.behavior);
        }
        catch (final Throwable t) {
            if (ParserInputStreamDataSource.log.isDebugEnabled()) {
                ParserInputStreamDataSource.log.debug((Object)"Error ParserInputStreamDataSource.copy(): ", t);
            }
            throw new OMException(t);
        }
    }
    
    static {
        log = LogFactory.getLog((Class)ParserInputStreamDataSource.class);
    }
    
    public class Data
    {
        private InputStream payload;
        private String encoding;
        private int behavior;
        private int numReads;
        private String firstUseStack;
        
        private Data(final InputStream payload, final String encoding, final int behavior) {
            this.payload = null;
            this.encoding = null;
            this.numReads = 0;
            this.firstUseStack = null;
            this.payload = payload;
            this.encoding = encoding;
            this.behavior = behavior;
            this.setInputStream(payload);
        }
        
        public InputStream readParserInputStream() throws XMLStreamException {
            ++this.numReads;
            if (ParserInputStreamDataSource.log.isDebugEnabled()) {
                ParserInputStreamDataSource.log.debug((Object)"Entry readParserInputStream()");
                ParserInputStreamDataSource.log.debug((Object)("Data Encoding = " + this.encoding));
                ParserInputStreamDataSource.log.debug((Object)("numReads = " + this.numReads));
                ParserInputStreamDataSource.log.debug((Object)("behavior = " + this.behavior));
                final String stack = CommonUtils.stackToString(new OMException());
                ParserInputStreamDataSource.log.debug((Object)("call stack:" + stack));
            }
            if (this.payload == null) {
                throw new OMException("ParserInputStreamDataSource's InputStream is null.");
            }
            Label_0377: {
                if (this.behavior == 1) {
                    if (this.numReads <= 1) {
                        break Label_0377;
                    }
                    try {
                        if (ParserInputStreamDataSource.log.isDebugEnabled()) {
                            ParserInputStreamDataSource.log.debug((Object)"reset InputStream for reuse");
                        }
                        this.payload.reset();
                        break Label_0377;
                    }
                    catch (final Throwable t) {
                        throw new OMException(t);
                    }
                }
                if (this.behavior == 2) {
                    if (this.numReads != 1) {
                        final OMException ome = new OMException("A second read of ParserInputStreamDataSource is not allowed.The first read was done here: " + this.firstUseStack);
                        if (ParserInputStreamDataSource.log.isDebugEnabled()) {
                            ParserInputStreamDataSource.log.debug((Object)("ONE_USE_UNSAFE second use exception:" + ome));
                        }
                        throw ome;
                    }
                    this.firstUseStack = CommonUtils.stackToString(new OMException());
                    if (ParserInputStreamDataSource.log.isDebugEnabled()) {
                        ParserInputStreamDataSource.log.debug((Object)("ONE_USE_UNSAFE mode stack:" + this.firstUseStack));
                    }
                }
            }
            if (ParserInputStreamDataSource.log.isDebugEnabled()) {
                ParserInputStreamDataSource.log.debug((Object)"Exit readParserInputStream()");
            }
            return this.payload;
        }
        
        public void setInputStream(final InputStream inputStream) {
            if (ParserInputStreamDataSource.log.isDebugEnabled()) {
                final String clsName = (inputStream == null) ? null : inputStream.getClass().getName();
                ParserInputStreamDataSource.log.debug((Object)("Enter setInputStream: The kind of InputStream is:" + clsName));
            }
            this.numReads = 0;
            this.firstUseStack = null;
            Label_0254: {
                if (inputStream == null) {
                    if (ParserInputStreamDataSource.log.isDebugEnabled()) {
                        ParserInputStreamDataSource.log.debug((Object)"The inputStream is null");
                    }
                    this.payload = null;
                }
                else {
                    if (this.behavior == 1) {
                        if (inputStream.markSupported()) {
                            if (ParserInputStreamDataSource.log.isDebugEnabled()) {
                                ParserInputStreamDataSource.log.debug((Object)"The inputStream supports mark().  Setting mark()");
                            }
                            (this.payload = inputStream).mark(Integer.MAX_VALUE);
                            break Label_0254;
                        }
                        try {
                            if (ParserInputStreamDataSource.log.isDebugEnabled()) {
                                ParserInputStreamDataSource.log.debug((Object)"The inputStream does not supports mark().  Copying Stream");
                            }
                            final BAAOutputStream baaos = new BAAOutputStream();
                            BufferUtils.inputStream2OutputStream(inputStream, baaos);
                            final BAAInputStream baais = new BAAInputStream(baaos.buffers(), baaos.length());
                            (this.payload = baais).mark(Integer.MAX_VALUE);
                            break Label_0254;
                        }
                        catch (final Throwable t) {
                            if (ParserInputStreamDataSource.log.isDebugEnabled()) {
                                ParserInputStreamDataSource.log.debug((Object)"Error:", t);
                            }
                            throw new OMException(t);
                        }
                    }
                    this.payload = inputStream;
                }
            }
            if (ParserInputStreamDataSource.log.isDebugEnabled()) {
                ParserInputStreamDataSource.log.debug((Object)"Exit setInputStream");
            }
        }
    }
}
