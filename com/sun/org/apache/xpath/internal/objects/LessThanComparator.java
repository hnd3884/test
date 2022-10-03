package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.XMLString;

class LessThanComparator extends Comparator
{
    @Override
    boolean compareStrings(final XMLString s1, final XMLString s2) {
        return s1.toDouble() < s2.toDouble();
    }
    
    @Override
    boolean compareNumbers(final double n1, final double n2) {
        return n1 < n2;
    }
}
