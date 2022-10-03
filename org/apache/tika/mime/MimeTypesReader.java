package org.apache.tika.mime;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.net.URISyntaxException;
import java.net.URI;
import org.xml.sax.Attributes;
import java.io.ByteArrayInputStream;
import org.xml.sax.InputSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.xml.sax.ContentHandler;
import javax.xml.transform.sax.SAXResult;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import org.apache.tika.utils.XMLReaderUtils;
import org.w3c.dom.Document;
import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.apache.tika.exception.TikaException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import javax.xml.parsers.SAXParser;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.xml.sax.helpers.DefaultHandler;

public class MimeTypesReader extends DefaultHandler implements MimeTypesReaderMetKeys
{
    private static final ReentrantReadWriteLock READ_WRITE_LOCK;
    private static int POOL_SIZE;
    private static ArrayBlockingQueue<SAXParser> SAX_PARSERS;
    static Logger LOG;
    protected final MimeTypes types;
    protected MimeType type;
    protected int priority;
    protected StringBuilder characters;
    private ClauseRecord current;
    
    protected MimeTypesReader(final MimeTypes types) {
        this.type = null;
        this.characters = null;
        this.current = new ClauseRecord(null);
        this.types = types;
    }
    
    private static SAXParser acquireSAXParser() throws TikaException {
        SAXParser parser;
        do {
            parser = null;
            try {
                MimeTypesReader.READ_WRITE_LOCK.readLock().lock();
                parser = MimeTypesReader.SAX_PARSERS.poll(10L, TimeUnit.MILLISECONDS);
            }
            catch (final InterruptedException e) {
                throw new TikaException("interrupted while waiting for SAXParser", e);
            }
            finally {
                MimeTypesReader.READ_WRITE_LOCK.readLock().unlock();
            }
        } while (parser == null);
        return parser;
    }
    
    private static void releaseParser(final SAXParser parser) {
        try {
            parser.reset();
        }
        catch (final UnsupportedOperationException ex) {}
        try {
            MimeTypesReader.READ_WRITE_LOCK.readLock().lock();
            MimeTypesReader.SAX_PARSERS.offer(parser);
        }
        finally {
            MimeTypesReader.READ_WRITE_LOCK.readLock().unlock();
        }
    }
    
    public static void setPoolSize(final int poolSize) throws TikaException {
        try {
            MimeTypesReader.READ_WRITE_LOCK.writeLock().lock();
            MimeTypesReader.SAX_PARSERS = new ArrayBlockingQueue<SAXParser>(poolSize);
            for (int i = 0; i < poolSize; ++i) {
                MimeTypesReader.SAX_PARSERS.offer(newSAXParser());
            }
            MimeTypesReader.POOL_SIZE = poolSize;
        }
        finally {
            MimeTypesReader.READ_WRITE_LOCK.writeLock().unlock();
        }
    }
    
    private static SAXParser newSAXParser() throws TikaException {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(false);
        try {
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        }
        catch (final ParserConfigurationException | SAXException e) {
            MimeTypesReader.LOG.warn("can't set secure processing feature on: " + factory.getClass() + ". User assumes responsibility for consequences.");
        }
        try {
            return factory.newSAXParser();
        }
        catch (final ParserConfigurationException | SAXException e) {
            throw new TikaException("Can't create new sax parser", e);
        }
    }
    
    public void read(final InputStream stream) throws IOException, MimeTypeException {
        SAXParser parser = null;
        try {
            parser = acquireSAXParser();
            parser.parse(stream, this);
        }
        catch (final TikaException e) {
            throw new MimeTypeException("Unable to create an XML parser", e);
        }
        catch (final SAXException e2) {
            throw new MimeTypeException("Invalid type configuration", e2);
        }
        finally {
            if (parser != null) {
                releaseParser(parser);
            }
        }
    }
    
    public void read(final Document document) throws MimeTypeException {
        try {
            final Transformer transformer = XMLReaderUtils.getTransformer();
            transformer.transform(new DOMSource(document), new SAXResult(this));
        }
        catch (final TransformerException | TikaException e) {
            throw new MimeTypeException("Failed to parse type registry", e);
        }
    }
    
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) {
        return new InputSource(new ByteArrayInputStream(new byte[0]));
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        if (this.type == null) {
            if ("mime-type".equals(qName)) {
                final String name = attributes.getValue("type");
                final String interpretedAttr = attributes.getValue("interpreted");
                final boolean interpreted = "true".equals(interpretedAttr);
                try {
                    (this.type = this.types.forName(name)).setInterpreted(interpreted);
                }
                catch (final MimeTypeException e) {
                    this.handleMimeError(name, e, qName, attributes);
                }
            }
        }
        else if ("alias".equals(qName)) {
            final String alias = attributes.getValue("type");
            this.types.addAlias(this.type, MediaType.parse(alias));
        }
        else if ("sub-class-of".equals(qName)) {
            final String parent = attributes.getValue("type");
            this.types.setSuperType(this.type, MediaType.parse(parent));
        }
        else if ("acronym".equals(qName) || "_comment".equals(qName) || "tika:link".equals(qName) || "tika:uti".equals(qName)) {
            this.characters = new StringBuilder();
        }
        else if ("glob".equals(qName)) {
            final String pattern = attributes.getValue("pattern");
            final String isRegex = attributes.getValue("isregex");
            if (pattern != null) {
                try {
                    this.types.addPattern(this.type, pattern, Boolean.parseBoolean(isRegex));
                }
                catch (final MimeTypeException e2) {
                    this.handleGlobError(this.type, pattern, e2, qName, attributes);
                }
            }
        }
        else if ("root-XML".equals(qName)) {
            final String namespace = attributes.getValue("namespaceURI");
            final String name2 = attributes.getValue("localName");
            this.type.addRootXML(namespace, name2);
        }
        else if ("match".equals(qName)) {
            if (attributes.getValue("minShouldMatch") != null) {
                this.current = new ClauseRecord(new MinShouldMatchVal(Integer.parseInt(attributes.getValue("minShouldMatch"))));
            }
            else {
                String kind = attributes.getValue("type");
                final String offset = attributes.getValue("offset");
                final String value = attributes.getValue("value");
                final String mask = attributes.getValue("mask");
                if (kind == null) {
                    kind = "string";
                }
                this.current = new ClauseRecord(new MagicMatch(this.type.getType(), kind, offset, value, mask));
            }
        }
        else if ("magic".equals(qName)) {
            final String value2 = attributes.getValue("priority");
            if (value2 != null && value2.length() > 0) {
                this.priority = Integer.parseInt(value2);
            }
            else {
                this.priority = 50;
            }
            this.current = new ClauseRecord(null);
        }
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) {
        if (this.type != null) {
            if ("mime-type".equals(qName)) {
                this.type = null;
            }
            else if ("_comment".equals(qName)) {
                this.type.setDescription(this.characters.toString().trim());
                this.characters = null;
            }
            else if ("acronym".equals(qName)) {
                this.type.setAcronym(this.characters.toString().trim());
                this.characters = null;
            }
            else if ("tika:uti".equals(qName)) {
                this.type.setUniformTypeIdentifier(this.characters.toString().trim());
                this.characters = null;
            }
            else if ("tika:link".equals(qName)) {
                try {
                    this.type.addLink(new URI(this.characters.toString().trim()));
                }
                catch (final URISyntaxException e) {
                    throw new IllegalArgumentException("unable to parse link: " + (Object)this.characters, e);
                }
                this.characters = null;
            }
            else if ("match".equals(qName)) {
                this.current.stop();
            }
            else if ("magic".equals(qName)) {
                for (final Clause clause : this.current.getClauses()) {
                    this.type.addMagic(new Magic(this.type, this.priority, clause));
                }
                this.current = null;
            }
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) {
        if (this.characters != null) {
            this.characters.append(ch, start, length);
        }
    }
    
    protected void handleMimeError(final String input, final MimeTypeException ex, final String qName, final Attributes attributes) throws SAXException {
        throw new SAXException(ex);
    }
    
    protected void handleGlobError(final MimeType type, final String pattern, final MimeTypeException ex, final String qName, final Attributes attributes) throws SAXException {
        throw new SAXException(ex);
    }
    
    static {
        READ_WRITE_LOCK = new ReentrantReadWriteLock();
        MimeTypesReader.POOL_SIZE = 10;
        MimeTypesReader.SAX_PARSERS = new ArrayBlockingQueue<SAXParser>(MimeTypesReader.POOL_SIZE);
        MimeTypesReader.LOG = LoggerFactory.getLogger((Class)MimeTypesReader.class);
        try {
            setPoolSize(MimeTypesReader.POOL_SIZE);
        }
        catch (final TikaException e) {
            throw new RuntimeException("problem initializing SAXParser pool", e);
        }
    }
    
    private static class MinShouldMatchVal implements Clause
    {
        private final int val;
        
        MinShouldMatchVal(final int val) {
            this.val = val;
        }
        
        int getVal() {
            return this.val;
        }
        
        @Override
        public boolean eval(final byte[] data) {
            throw new IllegalStateException("This should never be used on this placeholder class");
        }
        
        @Override
        public int size() {
            return 0;
        }
    }
    
    private class ClauseRecord
    {
        private final ClauseRecord parent;
        private Clause clause;
        private List<Clause> subclauses;
        
        public ClauseRecord(final Clause clause) {
            this.subclauses = null;
            this.parent = MimeTypesReader.this.current;
            this.clause = clause;
        }
        
        public void stop() {
            if (this.clause instanceof MinShouldMatchVal) {
                this.clause = new MinShouldMatchClause(((MinShouldMatchVal)this.clause).getVal(), this.subclauses);
            }
            else if (this.subclauses != null) {
                Clause subclause;
                if (this.subclauses.size() == 1) {
                    subclause = this.subclauses.get(0);
                }
                else {
                    subclause = new OrClause(this.subclauses);
                }
                this.clause = new AndClause(new Clause[] { this.clause, subclause });
            }
            if (this.parent.subclauses == null) {
                this.parent.subclauses = Collections.singletonList(this.clause);
            }
            else {
                if (this.parent.subclauses.size() == 1) {
                    this.parent.subclauses = new ArrayList<Clause>(this.parent.subclauses);
                }
                this.parent.subclauses.add(this.clause);
            }
            MimeTypesReader.this.current = MimeTypesReader.this.current.parent;
        }
        
        public List<Clause> getClauses() {
            return this.subclauses;
        }
    }
}
