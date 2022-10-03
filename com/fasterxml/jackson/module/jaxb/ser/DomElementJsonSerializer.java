package com.fasterxml.jackson.module.jaxb.ser;

import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import org.w3c.dom.Element;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@Deprecated
public class DomElementJsonSerializer extends StdSerializer<Element>
{
    private static final long serialVersionUID = 1L;
    
    public DomElementJsonSerializer() {
        super((Class)Element.class);
    }
    
    public void serialize(final Element value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        jgen.writeStartObject();
        jgen.writeStringField("name", value.getTagName());
        if (value.getNamespaceURI() != null) {
            jgen.writeStringField("namespace", value.getNamespaceURI());
        }
        final NamedNodeMap attributes = value.getAttributes();
        if (attributes != null && attributes.getLength() > 0) {
            jgen.writeArrayFieldStart("attributes");
            for (int i = 0; i < attributes.getLength(); ++i) {
                final Attr attribute = (Attr)attributes.item(i);
                jgen.writeStartObject();
                jgen.writeStringField("$", attribute.getValue());
                jgen.writeStringField("name", attribute.getName());
                final String ns = attribute.getNamespaceURI();
                if (ns != null) {
                    jgen.writeStringField("namespace", ns);
                }
                jgen.writeEndObject();
            }
            jgen.writeEndArray();
        }
        final NodeList children = value.getChildNodes();
        if (children != null && children.getLength() > 0) {
            jgen.writeArrayFieldStart("children");
            for (int j = 0; j < children.getLength(); ++j) {
                final Node child = children.item(j);
                switch (child.getNodeType()) {
                    case 3:
                    case 4: {
                        jgen.writeStartObject();
                        jgen.writeStringField("$", child.getNodeValue());
                        jgen.writeEndObject();
                        break;
                    }
                    case 1: {
                        this.serialize((Element)child, jgen, provider);
                        break;
                    }
                }
            }
            jgen.writeEndArray();
        }
        jgen.writeEndObject();
    }
    
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        if (visitor != null) {
            visitor.expectStringFormat(typeHint);
        }
    }
    
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) throws JsonMappingException {
        return (JsonNode)this.createSchemaNode("string", true);
    }
}
