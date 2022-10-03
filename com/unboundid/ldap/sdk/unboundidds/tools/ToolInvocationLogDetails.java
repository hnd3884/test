package com.unboundid.ldap.sdk.unboundidds.tools;

import java.util.Iterator;
import com.unboundid.util.NullOutputStream;
import java.util.Collections;
import java.util.UUID;
import java.io.File;
import java.util.Set;
import java.io.PrintStream;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ToolInvocationLogDetails
{
    private final boolean logInvocation;
    private final PrintStream toolErrorStream;
    private final Set<File> logFiles;
    private final String commandName;
    private final String invocationID;
    
    private ToolInvocationLogDetails(final boolean logInvocation, final String commandName, final String invocationID, final Set<File> logFiles, final PrintStream toolErrorStream) {
        this.logInvocation = logInvocation;
        this.commandName = commandName;
        this.toolErrorStream = toolErrorStream;
        if (invocationID == null) {
            this.invocationID = UUID.randomUUID().toString();
        }
        else {
            this.invocationID = invocationID;
        }
        if (logFiles == null) {
            this.logFiles = Collections.emptySet();
        }
        else {
            this.logFiles = Collections.unmodifiableSet((Set<? extends File>)logFiles);
        }
    }
    
    static ToolInvocationLogDetails createDoNotLogDetails(final String commandName) {
        return new ToolInvocationLogDetails(false, commandName, "", Collections.emptySet(), NullOutputStream.getPrintStream());
    }
    
    static ToolInvocationLogDetails createLogDetails(final String commandName, final String invocationID, final Set<File> logFiles, final PrintStream toolErrorStream) {
        return new ToolInvocationLogDetails(true, commandName, invocationID, logFiles, toolErrorStream);
    }
    
    public String getCommandName() {
        return this.commandName;
    }
    
    public boolean logInvocation() {
        return this.logInvocation;
    }
    
    public String getInvocationID() {
        return this.invocationID;
    }
    
    public Set<File> getLogFiles() {
        return this.logFiles;
    }
    
    public PrintStream getToolErrorStream() {
        return this.toolErrorStream;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("ToolInvocationLogDetails(commandName='");
        buffer.append(this.commandName);
        buffer.append("', logInvocation=");
        buffer.append(this.logInvocation);
        if (this.logInvocation) {
            buffer.append(", invocationID='");
            buffer.append(this.invocationID);
            buffer.append("', logFiles={");
            final Iterator<File> fileIterator = this.logFiles.iterator();
            while (fileIterator.hasNext()) {
                buffer.append('\'');
                buffer.append(fileIterator.next().getAbsolutePath());
                buffer.append('\'');
                if (fileIterator.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
