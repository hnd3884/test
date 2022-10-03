package com.unboundid.ldap.sdk.unboundidds.logs;

import java.util.Collections;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class ModifyRequestAccessLogMessage extends OperationRequestAccessLogMessage
{
    private static final long serialVersionUID = -8276481811479971408L;
    private final List<String> attributeNames;
    private final String dn;
    
    public ModifyRequestAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public ModifyRequestAccessLogMessage(final LogMessage m) {
        super(m);
        this.dn = this.getNamedValue("dn");
        final String attrs = this.getNamedValue("attrs");
        if (attrs == null) {
            this.attributeNames = null;
        }
        else {
            final ArrayList<String> l = new ArrayList<String>(10);
            final StringTokenizer tokenizer = new StringTokenizer(attrs, ",");
            while (tokenizer.hasMoreTokens()) {
                l.add(tokenizer.nextToken());
            }
            this.attributeNames = Collections.unmodifiableList((List<? extends String>)l);
        }
    }
    
    public final String getDN() {
        return this.dn;
    }
    
    public final List<String> getAttributeNames() {
        return this.attributeNames;
    }
    
    @Override
    public final AccessLogOperationType getOperationType() {
        return AccessLogOperationType.MODIFY;
    }
}
