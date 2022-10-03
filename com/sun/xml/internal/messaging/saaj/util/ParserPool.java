package com.sun.xml.internal.messaging.saaj.util;

import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl;
import java.util.concurrent.ArrayBlockingQueue;
import javax.xml.parsers.SAXParserFactory;
import java.util.concurrent.BlockingQueue;

public class ParserPool
{
    private final BlockingQueue queue;
    private SAXParserFactory factory;
    private int capacity;
    
    public ParserPool(final int capacity) {
        this.capacity = capacity;
        this.queue = new ArrayBlockingQueue(capacity);
        (this.factory = new SAXParserFactoryImpl()).setNamespaceAware(true);
        for (int i = 0; i < capacity; ++i) {
            try {
                this.queue.put(this.factory.newSAXParser());
            }
            catch (final InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(ex);
            }
            catch (final ParserConfigurationException ex2) {
                throw new RuntimeException(ex2);
            }
            catch (final SAXException ex3) {
                throw new RuntimeException(ex3);
            }
        }
    }
    
    public SAXParser get() throws ParserConfigurationException, SAXException {
        try {
            return this.queue.take();
        }
        catch (final InterruptedException ex) {
            throw new SAXException(ex);
        }
    }
    
    public void put(final SAXParser parser) {
        this.queue.offer(parser);
    }
    
    public void returnParser(final SAXParser saxParser) {
        saxParser.reset();
        this.resetSaxParser(saxParser);
        this.put(saxParser);
    }
    
    private void resetSaxParser(final SAXParser parser) {
        try {
            final SymbolTable table = new SymbolTable();
            parser.setProperty("http://apache.org/xml/properties/internal/symbol-table", table);
        }
        catch (final SAXNotRecognizedException ex) {}
        catch (final SAXNotSupportedException ex2) {}
    }
}
