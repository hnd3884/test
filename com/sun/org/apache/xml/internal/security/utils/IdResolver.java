package com.sun.org.apache.xml.internal.security.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

@Deprecated
public class IdResolver
{
    private IdResolver() {
    }
    
    public static void registerElementById(final Element element, final Attr attr) {
        element.setIdAttributeNode(attr, true);
    }
    
    public static Element getElementById(final Document document, final String s) {
        return document.getElementById(s);
    }
}
