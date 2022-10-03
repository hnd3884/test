package com.sun.org.apache.xml.internal.utils;

import java.util.Locale;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;

public interface XMLString
{
    void dispatchCharactersEvents(final ContentHandler p0) throws SAXException;
    
    void dispatchAsComment(final LexicalHandler p0) throws SAXException;
    
    XMLString fixWhiteSpace(final boolean p0, final boolean p1, final boolean p2);
    
    int length();
    
    char charAt(final int p0);
    
    void getChars(final int p0, final int p1, final char[] p2, final int p3);
    
    boolean equals(final XMLString p0);
    
    boolean equals(final String p0);
    
    boolean equals(final Object p0);
    
    boolean equalsIgnoreCase(final String p0);
    
    int compareTo(final XMLString p0);
    
    int compareToIgnoreCase(final XMLString p0);
    
    boolean startsWith(final String p0, final int p1);
    
    boolean startsWith(final XMLString p0, final int p1);
    
    boolean startsWith(final String p0);
    
    boolean startsWith(final XMLString p0);
    
    boolean endsWith(final String p0);
    
    int hashCode();
    
    int indexOf(final int p0);
    
    int indexOf(final int p0, final int p1);
    
    int lastIndexOf(final int p0);
    
    int lastIndexOf(final int p0, final int p1);
    
    int indexOf(final String p0);
    
    int indexOf(final XMLString p0);
    
    int indexOf(final String p0, final int p1);
    
    int lastIndexOf(final String p0);
    
    int lastIndexOf(final String p0, final int p1);
    
    XMLString substring(final int p0);
    
    XMLString substring(final int p0, final int p1);
    
    XMLString concat(final String p0);
    
    XMLString toLowerCase(final Locale p0);
    
    XMLString toLowerCase();
    
    XMLString toUpperCase(final Locale p0);
    
    XMLString toUpperCase();
    
    XMLString trim();
    
    String toString();
    
    boolean hasString();
    
    double toDouble();
}
