package com.unboundid.ldap.sdk.unboundidds.logs;

import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.Collections;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SearchReferenceAccessLogMessage extends SearchRequestAccessLogMessage
{
    private static final long serialVersionUID = 4413555391780641502L;
    private final List<String> responseControlOIDs;
    private final List<String> referralURLs;
    
    public SearchReferenceAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public SearchReferenceAccessLogMessage(final LogMessage m) {
        super(m);
        final String refStr = this.getNamedValue("referralURLs");
        if (refStr == null || refStr.isEmpty()) {
            this.referralURLs = Collections.emptyList();
        }
        else {
            final LinkedList<String> refs = new LinkedList<String>();
            int startPos = 0;
            while (true) {
                final int commaPos = refStr.indexOf(",ldap", startPos);
                if (commaPos < 0) {
                    break;
                }
                refs.add(refStr.substring(startPos, commaPos));
                startPos = commaPos + 1;
            }
            refs.add(refStr.substring(startPos));
            this.referralURLs = Collections.unmodifiableList((List<? extends String>)refs);
        }
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
    }
    
    public List<String> getReferralURLs() {
        return this.referralURLs;
    }
    
    public List<String> getResponseControlOIDs() {
        return this.responseControlOIDs;
    }
    
    @Override
    public AccessLogMessageType getMessageType() {
        return AccessLogMessageType.REFERENCE;
    }
}
