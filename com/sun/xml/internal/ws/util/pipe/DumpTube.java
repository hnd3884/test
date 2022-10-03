package com.sun.xml.internal.ws.util.pipe;

import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import java.lang.reflect.Constructor;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.Tube;
import javax.xml.stream.XMLOutputFactory;
import java.io.PrintStream;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;

public class DumpTube extends AbstractFilterTubeImpl
{
    private final String name;
    private final PrintStream out;
    private final XMLOutputFactory staxOut;
    private static boolean warnStaxUtils;
    
    public DumpTube(final String name, final PrintStream out, final Tube next) {
        super(next);
        this.name = name;
        this.out = out;
        this.staxOut = XMLOutputFactory.newInstance();
    }
    
    protected DumpTube(final DumpTube that, final TubeCloner cloner) {
        super(that, cloner);
        this.name = that.name;
        this.out = that.out;
        this.staxOut = that.staxOut;
    }
    
    @Override
    public NextAction processRequest(final Packet request) {
        this.dump("request", request);
        return super.processRequest(request);
    }
    
    @Override
    public NextAction processResponse(final Packet response) {
        this.dump("response", response);
        return super.processResponse(response);
    }
    
    protected void dump(final String header, final Packet packet) {
        this.out.println("====[" + this.name + ":" + header + "]====");
        if (packet.getMessage() == null) {
            this.out.println("(none)");
        }
        else {
            try {
                XMLStreamWriter writer = this.staxOut.createXMLStreamWriter(new PrintStream(this.out) {
                    @Override
                    public void close() {
                    }
                });
                writer = this.createIndenter(writer);
                packet.getMessage().copy().writeTo(writer);
                writer.close();
            }
            catch (final XMLStreamException e) {
                e.printStackTrace(this.out);
            }
        }
        this.out.println("============");
    }
    
    private XMLStreamWriter createIndenter(XMLStreamWriter writer) {
        try {
            final Class clazz = this.getClass().getClassLoader().loadClass("javanet.staxutils.IndentingXMLStreamWriter");
            final Constructor c = clazz.getConstructor(XMLStreamWriter.class);
            writer = c.newInstance(writer);
        }
        catch (final Exception e) {
            if (!DumpTube.warnStaxUtils) {
                DumpTube.warnStaxUtils = true;
                this.out.println("WARNING: put stax-utils.jar to the classpath to indent the dump output");
            }
        }
        return writer;
    }
    
    @Override
    public AbstractTubeImpl copy(final TubeCloner cloner) {
        return new DumpTube(this, cloner);
    }
}
