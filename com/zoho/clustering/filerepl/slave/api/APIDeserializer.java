package com.zoho.clustering.filerepl.slave.api;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.zoho.clustering.filerepl.event.EventLogPosition;
import org.w3c.dom.Element;
import com.zoho.clustering.filerepl.event.EventList;

public class APIDeserializer
{
    private static APIDeserializer singleton;
    private ResultNodeHandler<EventList> getEventsHandler;
    private ResultNodeHandler<String> takeSnapshotHandler;
    
    public APIDeserializer() {
        this.getEventsHandler = new ResultNodeHandler<EventList>() {
            @Override
            public EventList parseResultNode(final Element resultNode) {
                final EventList eventList = new EventList();
                final NodeList childNodes = resultNode.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); ++i) {
                    final Node child = childNodes.item(i);
                    if (child.getNodeType() == 1) {
                        if ("next".equals(child.getNodeName())) {
                            eventList.setNextPos(new EventLogPosition(APIDeserializer.extractText((Element)child)));
                        }
                        else if ("events".equals(child.getNodeName())) {
                            this.parseEventsNode((Element)child, eventList);
                        }
                    }
                }
                if (eventList.getNextPos() == null) {
                    throw new RuntimeException("Wrong Xml. <result> node has no <next> node.");
                }
                eventList.makeImmutable();
                return eventList;
            }
            
            private void parseEventsNode(final Element eventsNode, final EventList eventList) {
                final NodeList childNodes = eventsNode.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); ++i) {
                    final Node child = childNodes.item(i);
                    if (child.getNodeType() == 1 && "event".equals(child.getNodeName())) {
                        eventList.addEvent(APIDeserializer.extractText((Element)child));
                    }
                }
            }
        };
        this.takeSnapshotHandler = new ResultNodeHandler<String>() {
            @Override
            public String parseResultNode(final Element resultNode) {
                final Node nameNode = resultNode.getFirstChild();
                if (nameNode == null || nameNode.getNodeType() != 1) {
                    throw new RuntimeException("Wrong Xml. <result> node has no <name> node.");
                }
                return APIDeserializer.extractText((Element)nameNode);
            }
        };
    }
    
    public static APIDeserializer getInst() {
        return APIDeserializer.singleton;
    }
    
    public EventList parseGetEventsResponse(final InputStream in) {
        return this.parse(in, this.getEventsHandler);
    }
    
    public String parseTakeSnapshotResponse(final InputStream in) {
        return this.parse(in, this.takeSnapshotHandler);
    }
    
    private <V> V parse(final InputStream input, final ResultNodeHandler<V> resultHandler) throws APIResourceException, APIException {
        try {
            final DocumentBuilder domBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final Document doc = domBuilder.parse(input);
            return this.parseResponseNode(doc.getDocumentElement(), resultHandler);
        }
        catch (final ParserConfigurationException exp) {
            throw new RuntimeException(exp);
        }
        catch (final SAXException exp2) {
            throw new RuntimeException(exp2);
        }
        catch (final IOException exp3) {
            throw new APIResourceException(exp3);
        }
    }
    
    private <V> V parseResponseNode(final Element response, final ResultNodeHandler<V> resultHandler) {
        final NodeList childNodes = response.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            final Node child = childNodes.item(i);
            if (child.getNodeType() == 1) {
                final String nodeName = child.getNodeName();
                if ("error".equals(nodeName)) {
                    this.parseErrorNode((Element)child);
                }
                else if ("result".equals(nodeName)) {
                    return resultHandler.parseResultNode((Element)child);
                }
            }
        }
        throw new RuntimeException("Wrong Xml. <response> node has no <error> or <result> node.");
    }
    
    private void parseErrorNode(final Element errorNode) {
        String message = null;
        final NodeList childNodes = errorNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            final Node child = childNodes.item(i);
            if (child.getNodeType() == 1 && "message".equals(child.getNodeName())) {
                message = extractText((Element)child);
            }
        }
        if (message == null) {
            throw new RuntimeException("Wrong Xml. <error> node has no <message> node.");
        }
        throw new APIException(message);
    }
    
    public static String extractText(final Element element) {
        final Node firstChild = element.getFirstChild();
        if (firstChild != null) {
            switch (firstChild.getNodeType()) {
                case 3:
                case 4: {
                    final String text = firstChild.getNodeValue();
                    if (text != null && text.length() > 0) {
                        return text;
                    }
                    break;
                }
            }
        }
        throw new RuntimeException("Wrong Xml. <" + element.getLocalName() + "> has no text section");
    }
    
    static {
        APIDeserializer.singleton = new APIDeserializer();
    }
    
    private interface ResultNodeHandler<V>
    {
        V parseResultNode(final Element p0);
    }
}
