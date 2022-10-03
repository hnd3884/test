package com.sun.xml.internal.ws.commons.xmlutil;

import java.lang.reflect.Constructor;
import java.io.ByteArrayInputStream;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.istack.internal.NotNull;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.util.logging.Level;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.xml.stream.XMLOutputFactory;
import com.sun.istack.internal.logging.Logger;

public final class Converter
{
    public static final String UTF_8 = "UTF-8";
    private static final Logger LOGGER;
    private static final ContextClassloaderLocal<XMLOutputFactory> xmlOutputFactory;
    private static final AtomicBoolean logMissingStaxUtilsWarning;
    
    private Converter() {
    }
    
    public static String toString(final Throwable throwable) {
        if (throwable == null) {
            return "[ No exception ]";
        }
        final StringWriter stringOut = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringOut));
        return stringOut.toString();
    }
    
    public static String toString(final Packet packet) {
        if (packet == null) {
            return "[ Null packet ]";
        }
        if (packet.getMessage() == null) {
            return "[ Empty packet ]";
        }
        return toString(packet.getMessage());
    }
    
    public static String toStringNoIndent(final Packet packet) {
        if (packet == null) {
            return "[ Null packet ]";
        }
        if (packet.getMessage() == null) {
            return "[ Empty packet ]";
        }
        return toStringNoIndent(packet.getMessage());
    }
    
    public static String toString(final Message message) {
        return toString(message, true);
    }
    
    public static String toStringNoIndent(final Message message) {
        return toString(message, false);
    }
    
    private static String toString(final Message message, final boolean createIndenter) {
        if (message == null) {
            return "[ Null message ]";
        }
        StringWriter stringOut = null;
        try {
            stringOut = new StringWriter();
            XMLStreamWriter writer = null;
            try {
                writer = Converter.xmlOutputFactory.get().createXMLStreamWriter(stringOut);
                if (createIndenter) {
                    writer = createIndenter(writer);
                }
                message.copy().writeTo(writer);
            }
            catch (final Exception e) {
                Converter.LOGGER.log(Level.WARNING, "Unexpected exception occured while dumping message", e);
                if (writer != null) {
                    try {
                        writer.close();
                    }
                    catch (final XMLStreamException ignored) {
                        Converter.LOGGER.fine("Unexpected exception occured while closing XMLStreamWriter", ignored);
                    }
                }
            }
            finally {
                if (writer != null) {
                    try {
                        writer.close();
                    }
                    catch (final XMLStreamException ignored2) {
                        Converter.LOGGER.fine("Unexpected exception occured while closing XMLStreamWriter", ignored2);
                    }
                }
            }
            return stringOut.toString();
        }
        finally {
            if (stringOut != null) {
                try {
                    stringOut.close();
                }
                catch (final IOException ex) {
                    Converter.LOGGER.finest("An exception occured when trying to close StringWriter", ex);
                }
            }
        }
    }
    
    public static byte[] toBytes(final Message message, final String encoding) throws XMLStreamException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            if (message != null) {
                final XMLStreamWriter xsw = Converter.xmlOutputFactory.get().createXMLStreamWriter(baos, encoding);
                try {
                    message.writeTo(xsw);
                }
                finally {
                    try {
                        xsw.close();
                    }
                    catch (final XMLStreamException ex) {
                        Converter.LOGGER.warning("Unexpected exception occured while closing XMLStreamWriter", ex);
                    }
                }
            }
            return baos.toByteArray();
        }
        finally {
            try {
                baos.close();
            }
            catch (final IOException ex2) {
                Converter.LOGGER.warning("Unexpected exception occured while closing ByteArrayOutputStream", ex2);
            }
        }
    }
    
    public static Message toMessage(@NotNull final InputStream dataStream, final String encoding) throws XMLStreamException {
        final XMLStreamReader xsr = XmlUtil.newXMLInputFactory(true).createXMLStreamReader(dataStream, encoding);
        return Messages.create(xsr);
    }
    
    public static String messageDataToString(final byte[] data, final String encoding) {
        try {
            return toString(toMessage(new ByteArrayInputStream(data), encoding));
        }
        catch (final XMLStreamException ex) {
            Converter.LOGGER.warning("Unexpected exception occured while converting message data to string", ex);
            return "[ Message Data Conversion Failed ]";
        }
    }
    
    private static XMLStreamWriter createIndenter(XMLStreamWriter writer) {
        try {
            final Class<?> clazz = Converter.class.getClassLoader().loadClass("javanet.staxutils.IndentingXMLStreamWriter");
            final Constructor<?> c = clazz.getConstructor(XMLStreamWriter.class);
            writer = XMLStreamWriter.class.cast(c.newInstance(writer));
        }
        catch (final Exception ex) {
            if (Converter.logMissingStaxUtilsWarning.compareAndSet(false, true)) {
                Converter.LOGGER.log(Level.WARNING, "Put stax-utils.jar to the classpath to indent the dump output", ex);
            }
        }
        return writer;
    }
    
    static {
        LOGGER = Logger.getLogger(Converter.class);
        xmlOutputFactory = new ContextClassloaderLocal<XMLOutputFactory>() {
            @Override
            protected XMLOutputFactory initialValue() throws Exception {
                return XMLOutputFactory.newInstance();
            }
        };
        logMissingStaxUtilsWarning = new AtomicBoolean(false);
    }
}
