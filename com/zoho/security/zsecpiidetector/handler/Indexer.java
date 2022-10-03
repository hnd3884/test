package com.zoho.security.zsecpiidetector.handler;

import opennlp.tools.util.Span;
import java.util.List;

public class Indexer implements PIIHandler
{
    @Override
    public String handleData(final String data, final List<Span> listOfSpan) {
        return data;
    }
}
