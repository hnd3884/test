package com.unboundid.ldap.sdk.unboundidds.logs;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.Collections;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SearchEntryAccessLogMessage extends SearchRequestAccessLogMessage
{
    private static final long serialVersionUID = 6423635071693560277L;
    private final List<String> attributesReturned;
    private final List<String> responseControlOIDs;
    private final String dn;
    
    public SearchEntryAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public SearchEntryAccessLogMessage(final LogMessage m) {
        super(m);
        this.dn = this.getNamedValue("dn");
        final String controlStr = this.getNamedValue("responseControls");
        if (controlStr == null) {
            this.responseControlOIDs = Collections.emptyList();
        }
        else {
            final LinkedList<String> controlList = new LinkedList<String>();
            final StringTokenizer t = new StringTokenizer(controlStr, ",");
            while (t.hasMoreTokens()) {
                controlList.add(t.nextToken());
            }
            this.responseControlOIDs = Collections.unmodifiableList((List<? extends String>)controlList);
        }
        final String attrs = this.getNamedValue("attrsReturned");
        if (attrs == null) {
            this.attributesReturned = null;
        }
        else {
            final ArrayList<String> l = new ArrayList<String>(10);
            final StringTokenizer tokenizer = new StringTokenizer(attrs, ",");
            while (tokenizer.hasMoreTokens()) {
                l.add(tokenizer.nextToken());
            }
            this.attributesReturned = Collections.unmodifiableList((List<? extends String>)l);
        }
    }
    
    public String getDN() {
        return this.dn;
    }
    
    public List<String> getAttributesReturned() {
        return this.attributesReturned;
    }
    
    public List<String> getResponseControlOIDs() {
        return this.responseControlOIDs;
    }
    
    @Override
    public AccessLogMessageType getMessageType() {
        return AccessLogMessageType.ENTRY;
    }
}
