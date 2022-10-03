package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.ldap.sdk.LDAPException;
import java.util.Iterator;
import com.unboundid.ldap.sdk.LDAPResult;
import java.util.List;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.CommandLineTool;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.LDAPConnectionPoolHealthCheck;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ReportBindResultLDAPConnectionPoolHealthCheck extends LDAPConnectionPoolHealthCheck
{
    private final boolean displaySuccessResultWithControls;
    private final boolean displaySuccessResultWithoutControls;
    private final CommandLineTool tool;
    private final int wrapColumn;
    
    public ReportBindResultLDAPConnectionPoolHealthCheck(final CommandLineTool tool, final boolean displaySuccessResultWithControls, final boolean displaySuccessResultWithoutControls) {
        this.tool = tool;
        this.displaySuccessResultWithControls = displaySuccessResultWithControls;
        this.displaySuccessResultWithoutControls = displaySuccessResultWithoutControls;
        this.wrapColumn = StaticUtils.TERMINAL_WIDTH_COLUMNS - 1;
    }
    
    @Override
    public void ensureConnectionValidAfterAuthentication(final LDAPConnection connection, final BindResult bindResult) throws LDAPException {
        if (bindResult.getResultCode() == ResultCode.SUCCESS) {
            boolean displayResult;
            if (bindResult.hasResponseControl()) {
                displayResult = this.displaySuccessResultWithControls;
            }
            else {
                displayResult = this.displaySuccessResultWithoutControls;
            }
            if (displayResult) {
                final ArrayList<String> lines = new ArrayList<String>(10);
                lines.add("# " + ToolMessages.INFO_REPORT_BIND_RESULT_HEADER.get());
                ResultUtils.formatResult(lines, bindResult, true, false, 5, this.wrapColumn);
                for (final String line : lines) {
                    this.tool.out(line);
                }
                this.tool.out(new Object[0]);
            }
        }
        else {
            final ArrayList<String> lines2 = new ArrayList<String>(10);
            lines2.add("# " + ToolMessages.INFO_REPORT_BIND_RESULT_HEADER.get());
            ResultUtils.formatResult(lines2, bindResult, true, false, 0, this.wrapColumn);
            for (final String line2 : lines2) {
                this.tool.err(line2);
            }
            this.tool.err(new Object[0]);
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ReportBindResultLDAPConnectionPoolHealthCheck(displaySuccessResultWithControls=");
        buffer.append(this.displaySuccessResultWithControls);
        buffer.append(", displaySuccessResultWithoutControls=");
        buffer.append(this.displaySuccessResultWithoutControls);
        buffer.append(')');
    }
}
