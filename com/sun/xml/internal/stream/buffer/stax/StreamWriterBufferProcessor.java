package com.sun.xml.internal.stream.buffer.stax;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;
import java.io.IOException;
import com.sun.xml.internal.org.jvnet.staxex.Base64Data;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.AbstractProcessor;

public class StreamWriterBufferProcessor extends AbstractProcessor
{
    public StreamWriterBufferProcessor() {
    }
    
    @Deprecated
    public StreamWriterBufferProcessor(final XMLStreamBuffer buffer) {
        this.setXMLStreamBuffer(buffer, buffer.isFragment());
    }
    
    public StreamWriterBufferProcessor(final XMLStreamBuffer buffer, final boolean produceFragmentEvent) {
        this.setXMLStreamBuffer(buffer, produceFragmentEvent);
    }
    
    public final void process(final XMLStreamBuffer buffer, final XMLStreamWriter writer) throws XMLStreamException {
        this.setXMLStreamBuffer(buffer, buffer.isFragment());
        this.process(writer);
    }
    
    public void process(final XMLStreamWriter writer) throws XMLStreamException {
        if (this._fragmentMode) {
            this.writeFragment(writer);
        }
        else {
            this.write(writer);
        }
    }
    
    @Deprecated
    public void setXMLStreamBuffer(final XMLStreamBuffer buffer) {
        this.setBuffer(buffer);
    }
    
    public void setXMLStreamBuffer(final XMLStreamBuffer buffer, final boolean produceFragmentEvent) {
        this.setBuffer(buffer, produceFragmentEvent);
    }
    
    public void write(final XMLStreamWriter writer) throws XMLStreamException {
        if (!this._fragmentMode) {
            if (this._treeCount > 1) {
                throw new IllegalStateException("forest cannot be written as a full infoset");
            }
            writer.writeStartDocument();
        }
        while (true) {
            final int item = AbstractProcessor.getEIIState(this.peekStructure());
            writer.flush();
            switch (item) {
                case 1: {
                    this.readStructure();
                    continue;
                }
                case 3:
                case 4:
                case 5:
                case 6: {
                    this.writeFragment(writer);
                    continue;
                }
                case 12: {
                    this.readStructure();
                    final int length = this.readStructure();
                    final int start = this.readContentCharactersBuffer(length);
                    final String comment = new String(this._contentCharactersBuffer, start, length);
                    writer.writeComment(comment);
                    continue;
                }
                case 13: {
                    this.readStructure();
                    final int length = this.readStructure16();
                    final int start = this.readContentCharactersBuffer(length);
                    final String comment = new String(this._contentCharactersBuffer, start, length);
                    writer.writeComment(comment);
                    continue;
                }
                case 14: {
                    this.readStructure();
                    final char[] ch = this.readContentCharactersCopy();
                    writer.writeComment(new String(ch));
                    continue;
                }
                case 16: {
                    this.readStructure();
                    writer.writeProcessingInstruction(this.readStructureString(), this.readStructureString());
                    continue;
                }
                case 17: {
                    this.readStructure();
                    writer.writeEndDocument();
                    return;
                }
                default: {
                    throw new XMLStreamException("Invalid State " + item);
                }
            }
        }
    }
    
    public void writeFragment(final XMLStreamWriter writer) throws XMLStreamException {
        if (writer instanceof XMLStreamWriterEx) {
            this.writeFragmentEx((XMLStreamWriterEx)writer);
        }
        else {
            this.writeFragmentNoEx(writer);
        }
    }
    
    public void writeFragmentEx(final XMLStreamWriterEx writer) throws XMLStreamException {
        int depth = 0;
        int item = AbstractProcessor.getEIIState(this.peekStructure());
        if (item == 1) {
            this.readStructure();
        }
        do {
            item = this.readEiiState();
            switch (item) {
                case 1: {
                    throw new AssertionError();
                }
                case 3: {
                    ++depth;
                    final String uri = this.readStructureString();
                    final String localName = this.readStructureString();
                    final String prefix = this.getPrefixFromQName(this.readStructureString());
                    writer.writeStartElement(prefix, localName, uri);
                    this.writeAttributes(writer, this.isInscope(depth));
                    continue;
                }
                case 4: {
                    ++depth;
                    final String prefix2 = this.readStructureString();
                    final String uri2 = this.readStructureString();
                    final String localName2 = this.readStructureString();
                    writer.writeStartElement(prefix2, localName2, uri2);
                    this.writeAttributes(writer, this.isInscope(depth));
                    continue;
                }
                case 5: {
                    ++depth;
                    final String uri = this.readStructureString();
                    final String localName = this.readStructureString();
                    writer.writeStartElement("", localName, uri);
                    this.writeAttributes(writer, this.isInscope(depth));
                    continue;
                }
                case 6: {
                    ++depth;
                    final String localName3 = this.readStructureString();
                    writer.writeStartElement(localName3);
                    this.writeAttributes(writer, this.isInscope(depth));
                    continue;
                }
                case 7: {
                    final int length = this.readStructure();
                    final int start = this.readContentCharactersBuffer(length);
                    writer.writeCharacters(this._contentCharactersBuffer, start, length);
                    continue;
                }
                case 8: {
                    final int length = this.readStructure16();
                    final int start = this.readContentCharactersBuffer(length);
                    writer.writeCharacters(this._contentCharactersBuffer, start, length);
                    continue;
                }
                case 9: {
                    final char[] c = this.readContentCharactersCopy();
                    writer.writeCharacters(c, 0, c.length);
                    continue;
                }
                case 10: {
                    final String s = this.readContentString();
                    writer.writeCharacters(s);
                    continue;
                }
                case 11: {
                    final CharSequence c2 = (CharSequence)this.readContentObject();
                    writer.writePCDATA(c2);
                    continue;
                }
                case 12: {
                    final int length = this.readStructure();
                    final int start = this.readContentCharactersBuffer(length);
                    final String comment = new String(this._contentCharactersBuffer, start, length);
                    writer.writeComment(comment);
                    continue;
                }
                case 13: {
                    final int length = this.readStructure16();
                    final int start = this.readContentCharactersBuffer(length);
                    final String comment = new String(this._contentCharactersBuffer, start, length);
                    writer.writeComment(comment);
                    continue;
                }
                case 14: {
                    final char[] ch = this.readContentCharactersCopy();
                    writer.writeComment(new String(ch));
                    continue;
                }
                case 16: {
                    writer.writeProcessingInstruction(this.readStructureString(), this.readStructureString());
                    continue;
                }
                case 17: {
                    writer.writeEndElement();
                    if (--depth == 0) {
                        --this._treeCount;
                        continue;
                    }
                    continue;
                }
                default: {
                    throw new XMLStreamException("Invalid State " + item);
                }
            }
        } while (depth > 0 || this._treeCount > 0);
    }
    
    public void writeFragmentNoEx(final XMLStreamWriter writer) throws XMLStreamException {
        int depth = 0;
        int item = AbstractProcessor.getEIIState(this.peekStructure());
        if (item == 1) {
            this.readStructure();
        }
        do {
            item = this.readEiiState();
            switch (item) {
                case 1: {
                    throw new AssertionError();
                }
                case 3: {
                    ++depth;
                    final String uri = this.readStructureString();
                    final String localName = this.readStructureString();
                    final String prefix = this.getPrefixFromQName(this.readStructureString());
                    writer.writeStartElement(prefix, localName, uri);
                    this.writeAttributes(writer, this.isInscope(depth));
                    continue;
                }
                case 4: {
                    ++depth;
                    final String prefix2 = this.readStructureString();
                    final String uri2 = this.readStructureString();
                    final String localName2 = this.readStructureString();
                    writer.writeStartElement(prefix2, localName2, uri2);
                    this.writeAttributes(writer, this.isInscope(depth));
                    continue;
                }
                case 5: {
                    ++depth;
                    final String uri = this.readStructureString();
                    final String localName = this.readStructureString();
                    writer.writeStartElement("", localName, uri);
                    this.writeAttributes(writer, this.isInscope(depth));
                    continue;
                }
                case 6: {
                    ++depth;
                    final String localName3 = this.readStructureString();
                    writer.writeStartElement(localName3);
                    this.writeAttributes(writer, this.isInscope(depth));
                    continue;
                }
                case 7: {
                    final int length = this.readStructure();
                    final int start = this.readContentCharactersBuffer(length);
                    writer.writeCharacters(this._contentCharactersBuffer, start, length);
                    continue;
                }
                case 8: {
                    final int length = this.readStructure16();
                    final int start = this.readContentCharactersBuffer(length);
                    writer.writeCharacters(this._contentCharactersBuffer, start, length);
                    continue;
                }
                case 9: {
                    final char[] c = this.readContentCharactersCopy();
                    writer.writeCharacters(c, 0, c.length);
                    continue;
                }
                case 10: {
                    final String s = this.readContentString();
                    writer.writeCharacters(s);
                    continue;
                }
                case 11: {
                    final CharSequence c2 = (CharSequence)this.readContentObject();
                    if (c2 instanceof Base64Data) {
                        try {
                            final Base64Data bd = (Base64Data)c2;
                            bd.writeTo(writer);
                            continue;
                        }
                        catch (final IOException e) {
                            throw new XMLStreamException(e);
                        }
                    }
                    writer.writeCharacters(c2.toString());
                    continue;
                }
                case 12: {
                    final int length = this.readStructure();
                    final int start = this.readContentCharactersBuffer(length);
                    final String comment = new String(this._contentCharactersBuffer, start, length);
                    writer.writeComment(comment);
                    continue;
                }
                case 13: {
                    final int length = this.readStructure16();
                    final int start = this.readContentCharactersBuffer(length);
                    final String comment = new String(this._contentCharactersBuffer, start, length);
                    writer.writeComment(comment);
                    continue;
                }
                case 14: {
                    final char[] ch = this.readContentCharactersCopy();
                    writer.writeComment(new String(ch));
                    continue;
                }
                case 16: {
                    writer.writeProcessingInstruction(this.readStructureString(), this.readStructureString());
                    continue;
                }
                case 17: {
                    writer.writeEndElement();
                    if (--depth == 0) {
                        --this._treeCount;
                        continue;
                    }
                    continue;
                }
                default: {
                    throw new XMLStreamException("Invalid State " + item);
                }
            }
        } while (depth > 0 || this._treeCount > 0);
    }
    
    private boolean isInscope(final int depth) {
        return this._buffer.getInscopeNamespaces().size() > 0 && depth == 1;
    }
    
    private void writeAttributes(final XMLStreamWriter writer, final boolean inscope) throws XMLStreamException {
        final Set<String> prefixSet = inscope ? new HashSet<String>() : Collections.emptySet();
        int item = this.peekStructure();
        if ((item & 0xF0) == 0x40) {
            item = this.writeNamespaceAttributes(item, writer, inscope, prefixSet);
        }
        if (inscope) {
            this.writeInscopeNamespaces(writer, prefixSet);
        }
        if ((item & 0xF0) == 0x30) {
            this.writeAttributes(item, writer);
        }
    }
    
    private static String fixNull(final String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
    
    private void writeInscopeNamespaces(final XMLStreamWriter writer, final Set<String> prefixSet) throws XMLStreamException {
        for (final Map.Entry<String, String> e : this._buffer.getInscopeNamespaces().entrySet()) {
            final String key = fixNull(e.getKey());
            if (!prefixSet.contains(key)) {
                writer.writeNamespace(key, e.getValue());
            }
        }
    }
    
    private int writeNamespaceAttributes(int item, final XMLStreamWriter writer, final boolean collectPrefixes, final Set<String> prefixSet) throws XMLStreamException {
        do {
            switch (AbstractProcessor.getNIIState(item)) {
                case 1: {
                    writer.writeDefaultNamespace("");
                    if (collectPrefixes) {
                        prefixSet.add("");
                        break;
                    }
                    break;
                }
                case 2: {
                    final String prefix = this.readStructureString();
                    writer.writeNamespace(prefix, "");
                    if (collectPrefixes) {
                        prefixSet.add(prefix);
                        break;
                    }
                    break;
                }
                case 3: {
                    final String prefix = this.readStructureString();
                    writer.writeNamespace(prefix, this.readStructureString());
                    if (collectPrefixes) {
                        prefixSet.add(prefix);
                        break;
                    }
                    break;
                }
                case 4: {
                    writer.writeDefaultNamespace(this.readStructureString());
                    if (collectPrefixes) {
                        prefixSet.add("");
                        break;
                    }
                    break;
                }
            }
            this.readStructure();
            item = this.peekStructure();
        } while ((item & 0xF0) == 0x40);
        return item;
    }
    
    private void writeAttributes(int item, final XMLStreamWriter writer) throws XMLStreamException {
        do {
            switch (AbstractProcessor.getAIIState(item)) {
                case 1: {
                    final String uri = this.readStructureString();
                    final String localName = this.readStructureString();
                    final String prefix = this.getPrefixFromQName(this.readStructureString());
                    writer.writeAttribute(prefix, uri, localName, this.readContentString());
                    break;
                }
                case 2: {
                    writer.writeAttribute(this.readStructureString(), this.readStructureString(), this.readStructureString(), this.readContentString());
                    break;
                }
                case 3: {
                    writer.writeAttribute(this.readStructureString(), this.readStructureString(), this.readContentString());
                    break;
                }
                case 4: {
                    writer.writeAttribute(this.readStructureString(), this.readContentString());
                    break;
                }
            }
            this.readStructureString();
            this.readStructure();
            item = this.peekStructure();
        } while ((item & 0xF0) == 0x30);
    }
}
