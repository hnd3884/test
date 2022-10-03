package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPResult;
import java.util.Iterator;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.util.HorizontalAlignment;
import com.unboundid.util.FormattableColumn;
import java.util.List;
import com.unboundid.util.OutputFormat;
import com.unboundid.util.ColumnFormatter;
import java.util.ArrayList;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
final class ColumnFormatterLDAPSearchOutputHandler extends LDAPSearchOutputHandler
{
    private final ArrayList<String> formattedLines;
    private final ColumnFormatter formatter;
    private final int maxCommentWidth;
    private final LDAPSearch ldapSearch;
    private final Object[] columnValues;
    private final String[] attributes;
    private final StringBuilder formattedLineBuffer;
    
    ColumnFormatterLDAPSearchOutputHandler(final LDAPSearch ldapSearch, final OutputFormat outputFormat, final List<String> requestedAttributes, final int maxCommentWidth) {
        this.ldapSearch = ldapSearch;
        this.maxCommentWidth = maxCommentWidth;
        requestedAttributes.toArray(this.attributes = new String[requestedAttributes.size()]);
        this.columnValues = new Object[this.attributes.length + 1];
        final FormattableColumn[] columns = new FormattableColumn[this.attributes.length + 1];
        columns[0] = new FormattableColumn(10, HorizontalAlignment.LEFT, new String[] { "DN" });
        for (int i = 0; i < this.attributes.length; ++i) {
            columns[i + 1] = new FormattableColumn(10, HorizontalAlignment.LEFT, new String[] { this.attributes[i] });
        }
        this.formatter = new ColumnFormatter(false, null, outputFormat, " ", columns);
        this.formattedLines = new ArrayList<String>(20);
        this.formattedLineBuffer = new StringBuilder(100);
    }
    
    public void formatHeader() {
        for (final String headerLine : this.formatter.getHeaderLines(false)) {
            this.ldapSearch.writeOut("# " + headerLine);
        }
    }
    
    public void formatSearchResultEntry(final SearchResultEntry entry) {
        this.columnValues[0] = entry.getDN();
        int i = 1;
        for (final String attribute : this.attributes) {
            final String value = entry.getAttributeValue(attribute);
            if (value == null) {
                this.columnValues[i] = "";
            }
            else {
                this.columnValues[i] = value;
            }
            ++i;
        }
        this.ldapSearch.writeOut(this.formatter.formatRow(this.columnValues));
    }
    
    public void formatSearchResultReference(final SearchResultReference ref) {
        this.formattedLines.clear();
        this.formattedLineBuffer.setLength(0);
        ResultUtils.formatSearchResultReference(this.formattedLines, ref, this.maxCommentWidth);
        for (final String s : this.formattedLines) {
            this.formattedLineBuffer.append(s);
            this.formattedLineBuffer.append(StaticUtils.EOL);
        }
        this.ldapSearch.writeOut(this.formattedLineBuffer.toString());
    }
    
    public void formatResult(final LDAPResult result) {
        this.formattedLines.clear();
        this.formattedLineBuffer.setLength(0);
        ResultUtils.formatResult(this.formattedLines, result, true, false, 0, this.maxCommentWidth);
        for (final String s : this.formattedLines) {
            this.formattedLineBuffer.append(s);
            this.formattedLineBuffer.append(StaticUtils.EOL);
        }
        this.ldapSearch.writeOut(this.formattedLineBuffer.toString());
    }
    
    public void formatUnsolicitedNotification(final LDAPConnection connection, final ExtendedResult notification) {
        this.formattedLines.clear();
        this.formattedLineBuffer.setLength(0);
        ResultUtils.formatUnsolicitedNotification(this.formattedLines, notification, true, 0, this.maxCommentWidth);
        for (final String s : this.formattedLines) {
            this.formattedLineBuffer.append(s);
            this.formattedLineBuffer.append(StaticUtils.EOL);
        }
        this.ldapSearch.writeOut(this.formattedLineBuffer.toString());
    }
}
