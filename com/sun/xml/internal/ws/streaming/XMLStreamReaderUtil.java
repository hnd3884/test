package com.sun.xml.internal.ws.streaming;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamReaderUtil
{
    private XMLStreamReaderUtil() {
    }
    
    public static void close(final XMLStreamReader reader) {
        try {
            reader.close();
        }
        catch (final XMLStreamException e) {
            throw wrapException(e);
        }
    }
    
    public static void readRest(final XMLStreamReader reader) {
        try {
            while (reader.getEventType() != 8) {
                reader.next();
            }
        }
        catch (final XMLStreamException e) {
            throw wrapException(e);
        }
    }
    
    public static int next(final XMLStreamReader reader) {
        try {
            int readerEvent = reader.next();
            while (readerEvent != 8) {
                switch (readerEvent) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 12: {
                        return readerEvent;
                    }
                    default: {
                        readerEvent = reader.next();
                        continue;
                    }
                }
            }
            return readerEvent;
        }
        catch (final XMLStreamException e) {
            throw wrapException(e);
        }
    }
    
    public static int nextElementContent(final XMLStreamReader reader) {
        final int state = nextContent(reader);
        if (state == 4) {
            throw new XMLStreamReaderException("xmlreader.unexpectedCharacterContent", new Object[] { reader.getText() });
        }
        return state;
    }
    
    public static void toNextTag(final XMLStreamReader reader, final QName name) {
        if (reader.getEventType() != 1 && reader.getEventType() != 2) {
            nextElementContent(reader);
        }
        if (reader.getEventType() == 2 && name.equals(reader.getName())) {
            nextElementContent(reader);
        }
    }
    
    public static String nextWhiteSpaceContent(final XMLStreamReader reader) {
        next(reader);
        return currentWhiteSpaceContent(reader);
    }
    
    public static String currentWhiteSpaceContent(final XMLStreamReader reader) {
        StringBuilder whiteSpaces = null;
        while (true) {
            switch (reader.getEventType()) {
                case 1:
                case 2:
                case 8: {
                    return (whiteSpaces == null) ? null : whiteSpaces.toString();
                }
                case 4: {
                    if (reader.isWhiteSpace()) {
                        if (whiteSpaces == null) {
                            whiteSpaces = new StringBuilder();
                        }
                        whiteSpaces.append(reader.getText());
                        break;
                    }
                    throw new XMLStreamReaderException("xmlreader.unexpectedCharacterContent", new Object[] { reader.getText() });
                }
            }
            next(reader);
        }
    }
    
    public static int nextContent(final XMLStreamReader reader) {
        while (true) {
            final int state = next(reader);
            switch (state) {
                case 1:
                case 2:
                case 8: {
                    return state;
                }
                case 4: {
                    if (!reader.isWhiteSpace()) {
                        return 4;
                    }
                    continue;
                }
            }
        }
    }
    
    public static void skipElement(final XMLStreamReader reader) {
        assert reader.getEventType() == 1;
        skipTags(reader, true);
        assert reader.getEventType() == 2;
    }
    
    public static void skipSiblings(final XMLStreamReader reader, final QName parent) {
        skipTags(reader, reader.getName().equals(parent));
        assert reader.getEventType() == 2;
    }
    
    private static void skipTags(final XMLStreamReader reader, final boolean exitCondition) {
        try {
            int tags = 0;
            int state;
            while ((state = reader.next()) != 8) {
                if (state == 1) {
                    ++tags;
                }
                else {
                    if (state != 2) {
                        continue;
                    }
                    if (tags == 0 && exitCondition) {
                        return;
                    }
                    --tags;
                }
            }
        }
        catch (final XMLStreamException e) {
            throw wrapException(e);
        }
    }
    
    public static String getElementText(final XMLStreamReader reader) {
        try {
            return reader.getElementText();
        }
        catch (final XMLStreamException e) {
            throw wrapException(e);
        }
    }
    
    public static QName getElementQName(final XMLStreamReader reader) {
        try {
            final String text = reader.getElementText().trim();
            final String prefix = text.substring(0, text.indexOf(58));
            String namespaceURI = reader.getNamespaceContext().getNamespaceURI(prefix);
            if (namespaceURI == null) {
                namespaceURI = "";
            }
            final String localPart = text.substring(text.indexOf(58) + 1, text.length());
            return new QName(namespaceURI, localPart);
        }
        catch (final XMLStreamException e) {
            throw wrapException(e);
        }
    }
    
    public static Attributes getAttributes(final XMLStreamReader reader) {
        return (reader.getEventType() == 1 || reader.getEventType() == 10) ? new AttributesImpl(reader) : null;
    }
    
    public static void verifyReaderState(final XMLStreamReader reader, final int expectedState) {
        final int state = reader.getEventType();
        if (state != expectedState) {
            throw new XMLStreamReaderException("xmlreader.unexpectedState", new Object[] { getStateName(expectedState), getStateName(state) });
        }
    }
    
    public static void verifyTag(final XMLStreamReader reader, final String namespaceURI, final String localName) {
        if (!localName.equals(reader.getLocalName()) || !namespaceURI.equals(reader.getNamespaceURI())) {
            throw new XMLStreamReaderException("xmlreader.unexpectedState.tag", new Object[] { "{" + namespaceURI + "}" + localName, "{" + reader.getNamespaceURI() + "}" + reader.getLocalName() });
        }
    }
    
    public static void verifyTag(final XMLStreamReader reader, final QName name) {
        verifyTag(reader, name.getNamespaceURI(), name.getLocalPart());
    }
    
    public static String getStateName(final XMLStreamReader reader) {
        return getStateName(reader.getEventType());
    }
    
    public static String getStateName(final int state) {
        switch (state) {
            case 10: {
                return "ATTRIBUTE";
            }
            case 12: {
                return "CDATA";
            }
            case 4: {
                return "CHARACTERS";
            }
            case 5: {
                return "COMMENT";
            }
            case 11: {
                return "DTD";
            }
            case 8: {
                return "END_DOCUMENT";
            }
            case 2: {
                return "END_ELEMENT";
            }
            case 15: {
                return "ENTITY_DECLARATION";
            }
            case 9: {
                return "ENTITY_REFERENCE";
            }
            case 13: {
                return "NAMESPACE";
            }
            case 14: {
                return "NOTATION_DECLARATION";
            }
            case 3: {
                return "PROCESSING_INSTRUCTION";
            }
            case 6: {
                return "SPACE";
            }
            case 7: {
                return "START_DOCUMENT";
            }
            case 1: {
                return "START_ELEMENT";
            }
            default: {
                return "UNKNOWN";
            }
        }
    }
    
    private static XMLStreamReaderException wrapException(final XMLStreamException e) {
        return new XMLStreamReaderException("xmlreader.ioException", new Object[] { e });
    }
    
    public static class AttributesImpl implements Attributes
    {
        static final String XMLNS_NAMESPACE_URI = "http://www.w3.org/2000/xmlns/";
        AttributeInfo[] atInfos;
        
        public AttributesImpl(final XMLStreamReader reader) {
            if (reader == null) {
                this.atInfos = new AttributeInfo[0];
            }
            else {
                int index = 0;
                final int namespaceCount = reader.getNamespaceCount();
                final int attributeCount = reader.getAttributeCount();
                this.atInfos = new AttributeInfo[namespaceCount + attributeCount];
                for (int i = 0; i < namespaceCount; ++i) {
                    String namespacePrefix = reader.getNamespacePrefix(i);
                    if (namespacePrefix == null) {
                        namespacePrefix = "";
                    }
                    this.atInfos[index++] = new AttributeInfo(new QName("http://www.w3.org/2000/xmlns/", namespacePrefix, "xmlns"), reader.getNamespaceURI(i));
                }
                for (int i = 0; i < attributeCount; ++i) {
                    this.atInfos[index++] = new AttributeInfo(reader.getAttributeName(i), reader.getAttributeValue(i));
                }
            }
        }
        
        @Override
        public int getLength() {
            return this.atInfos.length;
        }
        
        @Override
        public String getLocalName(final int index) {
            if (index >= 0 && index < this.atInfos.length) {
                return this.atInfos[index].getLocalName();
            }
            return null;
        }
        
        @Override
        public QName getName(final int index) {
            if (index >= 0 && index < this.atInfos.length) {
                return this.atInfos[index].getName();
            }
            return null;
        }
        
        @Override
        public String getPrefix(final int index) {
            if (index >= 0 && index < this.atInfos.length) {
                return this.atInfos[index].getName().getPrefix();
            }
            return null;
        }
        
        @Override
        public String getURI(final int index) {
            if (index >= 0 && index < this.atInfos.length) {
                return this.atInfos[index].getName().getNamespaceURI();
            }
            return null;
        }
        
        @Override
        public String getValue(final int index) {
            if (index >= 0 && index < this.atInfos.length) {
                return this.atInfos[index].getValue();
            }
            return null;
        }
        
        @Override
        public String getValue(final QName name) {
            final int index = this.getIndex(name);
            if (index != -1) {
                return this.atInfos[index].getValue();
            }
            return null;
        }
        
        @Override
        public String getValue(final String localName) {
            final int index = this.getIndex(localName);
            if (index != -1) {
                return this.atInfos[index].getValue();
            }
            return null;
        }
        
        @Override
        public String getValue(final String uri, final String localName) {
            final int index = this.getIndex(uri, localName);
            if (index != -1) {
                return this.atInfos[index].getValue();
            }
            return null;
        }
        
        @Override
        public boolean isNamespaceDeclaration(final int index) {
            return index >= 0 && index < this.atInfos.length && this.atInfos[index].isNamespaceDeclaration();
        }
        
        @Override
        public int getIndex(final QName name) {
            for (int i = 0; i < this.atInfos.length; ++i) {
                if (this.atInfos[i].getName().equals(name)) {
                    return i;
                }
            }
            return -1;
        }
        
        @Override
        public int getIndex(final String localName) {
            for (int i = 0; i < this.atInfos.length; ++i) {
                if (this.atInfos[i].getName().getLocalPart().equals(localName)) {
                    return i;
                }
            }
            return -1;
        }
        
        @Override
        public int getIndex(final String uri, final String localName) {
            for (int i = 0; i < this.atInfos.length; ++i) {
                final QName qName = this.atInfos[i].getName();
                if (qName.getNamespaceURI().equals(uri) && qName.getLocalPart().equals(localName)) {
                    return i;
                }
            }
            return -1;
        }
        
        static class AttributeInfo
        {
            private QName name;
            private String value;
            
            public AttributeInfo(final QName name, final String value) {
                this.name = name;
                if (value == null) {
                    this.value = "";
                }
                else {
                    this.value = value;
                }
            }
            
            QName getName() {
                return this.name;
            }
            
            String getValue() {
                return this.value;
            }
            
            String getLocalName() {
                if (!this.isNamespaceDeclaration()) {
                    return this.name.getLocalPart();
                }
                if (this.name.getLocalPart().equals("")) {
                    return "xmlns";
                }
                return "xmlns:" + this.name.getLocalPart();
            }
            
            boolean isNamespaceDeclaration() {
                return this.name.getNamespaceURI() == "http://www.w3.org/2000/xmlns/";
            }
        }
    }
}
