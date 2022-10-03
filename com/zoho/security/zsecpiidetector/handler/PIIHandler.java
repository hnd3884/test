package com.zoho.security.zsecpiidetector.handler;

import java.util.Collections;
import java.util.List;
import opennlp.tools.util.Span;
import java.util.Comparator;

public interface PIIHandler
{
    public static final Comparator<Span> SPAN_SORT_COMPARATOR = new PIIHandlerComparator();
    public static final PIIHandler DEFAULT_PII_HANDLER = new MaskHandler();
    
    default String handlePII(final String data, final List<Span> listOfSpan) {
        Collections.sort(listOfSpan, PIIHandler.SPAN_SORT_COMPARATOR);
        cleanDuplicatedAndOverlapedSpans(listOfSpan);
        final String maskedData = this.handleData(data, listOfSpan);
        return maskedData;
    }
    
    String handleData(final String p0, final List<Span> p1);
    
    default void cleanDuplicatedAndOverlapedSpans(final List<Span> listOfSpan) {
        int i = 0;
        int j = 1;
        while (i < listOfSpan.size() - 1) {
            final Span o1 = listOfSpan.get(i);
            final Span o2 = listOfSpan.get(j);
            if (o1.getStart() == o2.getStart()) {
                if (o2.getEnd() == o1.getEnd()) {
                    listOfSpan.remove(j);
                }
                else {
                    listOfSpan.set(i, listOfSpan.remove(j));
                }
            }
            else if (o1.getEnd() == o2.getEnd()) {
                listOfSpan.remove(j);
            }
            else if (o2.getEnd() < o1.getEnd()) {
                listOfSpan.remove(j);
            }
            else if (o2.getStart() < o1.getEnd()) {
                final Span newSpan = new Span(o1.getStart(), o2.getEnd());
                listOfSpan.remove(j);
                listOfSpan.set(i, newSpan);
            }
            else {
                j = ++i + 1;
            }
        }
    }
    
    public static class PIIHandlerComparator implements Comparator<Span>
    {
        @Override
        public int compare(final Span o1, final Span o2) {
            if (o1.getStart() == o2.getStart()) {
                if (o1.getEnd() > o2.getEnd()) {
                    return 1;
                }
                if (o1.getEnd() < o2.getEnd()) {
                    return -1;
                }
                return 0;
            }
            else {
                if (o1.getStart() > o2.getStart()) {
                    return 1;
                }
                return -1;
            }
        }
    }
}
