package com.adventnet.persistence.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

public class DOMHandler
{
    public Element createElement(final Document doc, final String tagName) {
        return doc.createElement(tagName);
    }
}
