package com.zoho.clustering.agent.filerepl.api;

import java.util.Iterator;
import com.zoho.clustering.filerepl.event.EventList;

public class APISerializer
{
    private static APISerializer singleton;
    
    public static APISerializer getInst() {
        return APISerializer.singleton;
    }
    
    public String generateErrorResponse(final String uri, final String errMsg) {
        final StringBuilder error = new StringBuilder();
        error.append("<error>");
        error.append("<message>").append(CDATA(errMsg)).append("</message>");
        error.append("</error>");
        return this.response(uri, error).toString();
    }
    
    public String generateGetEventsResponse(final String uri, final EventList eventList) {
        final StringBuilder result = new StringBuilder();
        result.append("<result>");
        result.append("<next>").append(eventList.getNextPos().toString()).append("</next>");
        result.append("<events>");
        for (final String eventStr : eventList.getEvents()) {
            result.append("<event>").append(CDATA(eventStr)).append("</event>");
        }
        result.append("</events>");
        result.append("</result>");
        return this.response(uri, result).toString();
    }
    
    public String generateTakeSnapshotResponse(final String uri, final String snapshotName) {
        final StringBuilder result = new StringBuilder();
        result.append("<result>");
        result.append("<name>").append(CDATA(snapshotName)).append("</name>");
        result.append("</result>");
        return this.response(uri, result).toString();
    }
    
    private StringBuffer response(final String uri, final StringBuilder content) {
        final StringBuffer buff = new StringBuffer();
        buff.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        buff.append("<response").append(" uri=\"" + uri + "\" >");
        buff.append((CharSequence)content);
        buff.append("</response>");
        return buff;
    }
    
    private static String CDATA(final Object content) {
        return "<![CDATA[" + content + "]]>";
    }
    
    static {
        APISerializer.singleton = new APISerializer();
    }
}
