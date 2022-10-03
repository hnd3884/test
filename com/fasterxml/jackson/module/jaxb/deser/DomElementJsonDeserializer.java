package com.fasterxml.jackson.module.jaxb.deser;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Iterator;
import org.w3c.dom.Node;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import org.w3c.dom.Document;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@Deprecated
public class DomElementJsonDeserializer extends StdDeserializer<Element>
{
    private static final long serialVersionUID = 1L;
    private final DocumentBuilder builder;
    
    public DomElementJsonDeserializer() {
        super((Class)Element.class);
        try {
            final DocumentBuilderFactory bf = DocumentBuilderFactory.newInstance();
            bf.setNamespaceAware(true);
            bf.setExpandEntityReferences(false);
            this.builder = bf.newDocumentBuilder();
            try {
                bf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            }
            catch (final Exception ex) {}
        }
        catch (final ParserConfigurationException e) {
            throw new RuntimeException();
        }
    }
    
    public DomElementJsonDeserializer(final DocumentBuilder b) {
        super((Class)Element.class);
        this.builder = b;
    }
    
    public Element deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final Document document = this.builder.newDocument();
        final JsonNode n = (JsonNode)p.readValueAsTree();
        return this.fromNode(p, document, n);
    }
    
    protected Element fromNode(final JsonParser p, final Document document, final JsonNode jsonNode) throws IOException {
        String ns = (jsonNode.get("namespace") != null) ? jsonNode.get("namespace").asText() : null;
        String name = (jsonNode.get("name") != null) ? jsonNode.get("name").asText() : null;
        if (name == null) {
            throw JsonMappingException.from(p, "No name for DOM element was provided in the JSON object.");
        }
        final Element element = document.createElementNS(ns, name);
        final JsonNode attributesNode = jsonNode.get("attributes");
        if (attributesNode != null && attributesNode instanceof ArrayNode) {
            final Iterator<JsonNode> atts = attributesNode.elements();
            while (atts.hasNext()) {
                final JsonNode node = atts.next();
                ns = ((node.get("namespace") != null) ? node.get("namespace").asText() : null);
                name = ((node.get("name") != null) ? node.get("name").asText() : null);
                final String value = (node.get("$") != null) ? node.get("$").asText() : null;
                if (name != null) {
                    element.setAttributeNS(ns, name, value);
                }
            }
        }
        final JsonNode childsNode = jsonNode.get("children");
        if (childsNode != null && childsNode instanceof ArrayNode) {
            final Iterator<JsonNode> els = childsNode.elements();
            while (els.hasNext()) {
                final JsonNode node2 = els.next();
                name = ((node2.get("name") != null) ? node2.get("name").asText() : null);
                final String value2 = (node2.get("$") != null) ? node2.get("$").asText() : null;
                if (value2 != null) {
                    element.appendChild(document.createTextNode(value2));
                }
                else {
                    if (name == null) {
                        continue;
                    }
                    element.appendChild(this.fromNode(p, document, node2));
                }
            }
        }
        return element;
    }
}
