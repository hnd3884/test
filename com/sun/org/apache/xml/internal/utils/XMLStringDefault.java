package com.sun.org.apache.xml.internal.utils;

import java.util.Locale;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;

public class XMLStringDefault implements XMLString
{
    private String m_str;
    
    public XMLStringDefault(final String str) {
        this.m_str = str;
    }
    
    @Override
    public void dispatchCharactersEvents(final ContentHandler ch) throws SAXException {
    }
    
    @Override
    public void dispatchAsComment(final LexicalHandler lh) throws SAXException {
    }
    
    @Override
    public XMLString fixWhiteSpace(final boolean trimHead, final boolean trimTail, final boolean doublePunctuationSpaces) {
        return new XMLStringDefault(this.m_str.trim());
    }
    
    @Override
    public int length() {
        return this.m_str.length();
    }
    
    @Override
    public char charAt(final int index) {
        return this.m_str.charAt(index);
    }
    
    @Override
    public void getChars(final int srcBegin, final int srcEnd, final char[] dst, final int dstBegin) {
        int destIndex = dstBegin;
        for (int i = srcBegin; i < srcEnd; ++i) {
            dst[destIndex++] = this.m_str.charAt(i);
        }
    }
    
    @Override
    public boolean equals(final String obj2) {
        return this.m_str.equals(obj2);
    }
    
    @Override
    public boolean equals(final XMLString anObject) {
        return this.m_str.equals(anObject.toString());
    }
    
    @Override
    public boolean equals(final Object anObject) {
        return this.m_str.equals(anObject);
    }
    
    @Override
    public boolean equalsIgnoreCase(final String anotherString) {
        return this.m_str.equalsIgnoreCase(anotherString);
    }
    
    @Override
    public int compareTo(final XMLString anotherString) {
        return this.m_str.compareTo(anotherString.toString());
    }
    
    @Override
    public int compareToIgnoreCase(final XMLString str) {
        return this.m_str.compareToIgnoreCase(str.toString());
    }
    
    @Override
    public boolean startsWith(final String prefix, final int toffset) {
        return this.m_str.startsWith(prefix, toffset);
    }
    
    @Override
    public boolean startsWith(final XMLString prefix, final int toffset) {
        return this.m_str.startsWith(prefix.toString(), toffset);
    }
    
    @Override
    public boolean startsWith(final String prefix) {
        return this.m_str.startsWith(prefix);
    }
    
    @Override
    public boolean startsWith(final XMLString prefix) {
        return this.m_str.startsWith(prefix.toString());
    }
    
    @Override
    public boolean endsWith(final String suffix) {
        return this.m_str.endsWith(suffix);
    }
    
    @Override
    public int hashCode() {
        return this.m_str.hashCode();
    }
    
    @Override
    public int indexOf(final int ch) {
        return this.m_str.indexOf(ch);
    }
    
    @Override
    public int indexOf(final int ch, final int fromIndex) {
        return this.m_str.indexOf(ch, fromIndex);
    }
    
    @Override
    public int lastIndexOf(final int ch) {
        return this.m_str.lastIndexOf(ch);
    }
    
    @Override
    public int lastIndexOf(final int ch, final int fromIndex) {
        return this.m_str.lastIndexOf(ch, fromIndex);
    }
    
    @Override
    public int indexOf(final String str) {
        return this.m_str.indexOf(str);
    }
    
    @Override
    public int indexOf(final XMLString str) {
        return this.m_str.indexOf(str.toString());
    }
    
    @Override
    public int indexOf(final String str, final int fromIndex) {
        return this.m_str.indexOf(str, fromIndex);
    }
    
    @Override
    public int lastIndexOf(final String str) {
        return this.m_str.lastIndexOf(str);
    }
    
    @Override
    public int lastIndexOf(final String str, final int fromIndex) {
        return this.m_str.lastIndexOf(str, fromIndex);
    }
    
    @Override
    public XMLString substring(final int beginIndex) {
        return new XMLStringDefault(this.m_str.substring(beginIndex));
    }
    
    @Override
    public XMLString substring(final int beginIndex, final int endIndex) {
        return new XMLStringDefault(this.m_str.substring(beginIndex, endIndex));
    }
    
    @Override
    public XMLString concat(final String str) {
        return new XMLStringDefault(this.m_str.concat(str));
    }
    
    @Override
    public XMLString toLowerCase(final Locale locale) {
        return new XMLStringDefault(this.m_str.toLowerCase(locale));
    }
    
    @Override
    public XMLString toLowerCase() {
        return new XMLStringDefault(this.m_str.toLowerCase());
    }
    
    @Override
    public XMLString toUpperCase(final Locale locale) {
        return new XMLStringDefault(this.m_str.toUpperCase(locale));
    }
    
    @Override
    public XMLString toUpperCase() {
        return new XMLStringDefault(this.m_str.toUpperCase());
    }
    
    @Override
    public XMLString trim() {
        return new XMLStringDefault(this.m_str.trim());
    }
    
    @Override
    public String toString() {
        return this.m_str;
    }
    
    @Override
    public boolean hasString() {
        return true;
    }
    
    @Override
    public double toDouble() {
        try {
            return Double.valueOf(this.m_str);
        }
        catch (final NumberFormatException nfe) {
            return Double.NaN;
        }
    }
}
