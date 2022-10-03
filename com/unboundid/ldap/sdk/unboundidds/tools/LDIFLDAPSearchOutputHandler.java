package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.SearchResultReference;
import java.util.Iterator;
import com.unboundid.util.StaticUtils;
import java.util.List;
import com.unboundid.ldap.sdk.SearchResultEntry;
import java.util.ArrayList;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
final class LDIFLDAPSearchOutputHandler extends LDAPSearchOutputHandler
{
    private final ArrayList<String> formattedLines;
    private final int maxWidth;
    private final LDAPSearch ldapSearch;
    private final StringBuilder formattedLineBuffer;
    
    LDIFLDAPSearchOutputHandler(final LDAPSearch ldapSearch, final int maxWidth) {
        this.ldapSearch = ldapSearch;
        this.maxWidth = maxWidth;
        this.formattedLines = new ArrayList<String>(20);
        this.formattedLineBuffer = new StringBuilder(100);
    }
    
    public void formatHeader() {
    }
    
    public void formatSearchResultEntry(final SearchResultEntry entry) {
        this.formattedLines.clear();
        this.formattedLineBuffer.setLength(0);
        ResultUtils.formatSearchResultEntry(this.formattedLines, entry, this.maxWidth);
        for (final String s : this.formattedLines) {
            this.formattedLineBuffer.append(s);
            this.formattedLineBuffer.append(StaticUtils.EOL);
        }
        this.ldapSearch.writeOut(this.formattedLineBuffer.toString());
    }
    
    public void formatSearchResultReference(final SearchResultReference ref) {
        this.formattedLines.clear();
        this.formattedLineBuffer.setLength(0);
        ResultUtils.formatSearchResultReference(this.formattedLines, ref, this.maxWidth);
        for (final String s : this.formattedLines) {
            this.formattedLineBuffer.append(s);
            this.formattedLineBuffer.append(StaticUtils.EOL);
        }
        this.ldapSearch.writeOut(this.formattedLineBuffer.toString());
    }
    
    public void formatResult(final LDAPResult result) {
        this.formattedLines.clear();
        this.formattedLineBuffer.setLength(0);
        ResultUtils.formatResult(this.formattedLines, result, true, false, 0, this.maxWidth);
        for (final String s : this.formattedLines) {
            this.formattedLineBuffer.append(s);
            this.formattedLineBuffer.append(StaticUtils.EOL);
        }
        this.ldapSearch.writeOut(this.formattedLineBuffer.toString());
    }
    
    public void formatUnsolicitedNotification(final LDAPConnection connection, final ExtendedResult notification) {
        this.formattedLines.clear();
        this.formattedLineBuffer.setLength(0);
        ResultUtils.formatUnsolicitedNotification(this.formattedLines, notification, true, 0, this.maxWidth);
        for (final String s : this.formattedLines) {
            this.formattedLineBuffer.append(s);
            this.formattedLineBuffer.append(StaticUtils.EOL);
        }
        this.ldapSearch.writeOut(this.formattedLineBuffer.toString());
    }
}
