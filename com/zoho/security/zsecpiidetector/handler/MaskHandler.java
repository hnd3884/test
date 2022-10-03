package com.zoho.security.zsecpiidetector.handler;

import java.util.Iterator;
import opennlp.tools.util.Span;
import java.util.List;

public class MaskHandler implements PIIHandler
{
    private static final String DEFAULT_MASK_STRING = "*****";
    private String maskString;
    
    public MaskHandler() {
        this("*****");
    }
    
    public MaskHandler(final String maskString) {
        this.maskString = maskString;
    }
    
    @Override
    public String handleData(final String data, final List<Span> listOfSpan) {
        final StringBuilder sb = new StringBuilder(data);
        int bias = 0;
        int start = 0;
        int end = 0;
        final int maskLength = this.maskString.length();
        for (final Span span : listOfSpan) {
            start = span.getStart();
            end = span.getEnd();
            sb.replace(start + bias, end + bias, this.maskString);
            bias += maskLength - (end - start);
        }
        return sb.toString();
    }
}
