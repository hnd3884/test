package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.XMLString;

abstract class Comparator
{
    abstract boolean compareStrings(final XMLString p0, final XMLString p1);
    
    abstract boolean compareNumbers(final double p0, final double p1);
}
