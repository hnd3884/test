package javax.xml.stream.events;

import javax.xml.stream.XMLStreamException;
import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;

public interface XMLEvent extends XMLStreamConstants
{
    Characters asCharacters();
    
    EndElement asEndElement();
    
    StartElement asStartElement();
    
    int getEventType();
    
    Location getLocation();
    
    QName getSchemaType();
    
    boolean isAttribute();
    
    boolean isCharacters();
    
    boolean isEndDocument();
    
    boolean isEndElement();
    
    boolean isEntityReference();
    
    boolean isNamespace();
    
    boolean isProcessingInstruction();
    
    boolean isStartDocument();
    
    boolean isStartElement();
    
    void writeAsEncodedUnicode(final Writer p0) throws XMLStreamException;
}
