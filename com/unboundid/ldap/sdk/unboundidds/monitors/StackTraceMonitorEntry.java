package com.unboundid.ldap.sdk.unboundidds.monitors;

import com.unboundid.ldap.sdk.Attribute;
import java.util.Arrays;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import java.util.Iterator;
import com.unboundid.util.Debug;
import java.util.ArrayList;
import java.util.Collections;
import com.unboundid.ldap.sdk.Entry;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class StackTraceMonitorEntry extends MonitorEntry
{
    static final String STACK_TRACE_MONITOR_OC = "ds-stack-trace-monitor-entry";
    private static final String ATTR_JVM_STACK_TRACE = "jvmThread";
    private static final long serialVersionUID = -9008690818438183908L;
    private final List<ThreadStackTrace> stackTraces;
    
    public StackTraceMonitorEntry(final Entry entry) {
        super(entry);
        final List<String> traceLines = this.getStrings("jvmThread");
        if (traceLines.isEmpty()) {
            this.stackTraces = Collections.emptyList();
        }
        else {
            final ArrayList<ThreadStackTrace> traces = new ArrayList<ThreadStackTrace>(100);
            try {
                int currentThreadID = -1;
                String currentName = null;
                ArrayList<StackTraceElement> currentElements = new ArrayList<StackTraceElement>(20);
                for (final String line : traceLines) {
                    final int equalPos = line.indexOf(61);
                    final int spacePos = line.indexOf(32, equalPos);
                    final int id = Integer.parseInt(line.substring(equalPos + 1, spacePos));
                    if (id != currentThreadID) {
                        if (currentThreadID >= 0) {
                            traces.add(new ThreadStackTrace(currentThreadID, currentName, currentElements));
                        }
                        currentThreadID = id;
                        currentElements = new ArrayList<StackTraceElement>(20);
                        final int dashesPos1 = line.indexOf("---------- ", spacePos);
                        final int dashesPos2 = line.indexOf(" ----------", dashesPos1);
                        currentName = line.substring(dashesPos1 + 11, dashesPos2);
                    }
                    else {
                        final int bePos = line.indexOf("]=");
                        final String traceLine = line.substring(bePos + 2);
                        int lineNumber = -1;
                        final int closeParenPos = traceLine.lastIndexOf(41);
                        final int openParenPos = traceLine.lastIndexOf(40, closeParenPos);
                        final int colonPos = traceLine.lastIndexOf(58, closeParenPos);
                        String fileName;
                        if (colonPos < 0) {
                            fileName = traceLine.substring(openParenPos + 1, closeParenPos);
                        }
                        else {
                            fileName = traceLine.substring(openParenPos + 1, colonPos);
                            final String lineNumberStr = traceLine.substring(colonPos + 1, closeParenPos);
                            if (lineNumberStr.equalsIgnoreCase("native")) {
                                lineNumber = -2;
                            }
                            else {
                                try {
                                    lineNumber = Integer.parseInt(lineNumberStr);
                                }
                                catch (final Exception ex) {}
                            }
                        }
                        final int periodPos = traceLine.lastIndexOf(46, openParenPos);
                        final String className = traceLine.substring(0, periodPos);
                        final String methodName = traceLine.substring(periodPos + 1, openParenPos);
                        currentElements.add(new StackTraceElement(className, methodName, fileName, lineNumber));
                    }
                }
                if (currentThreadID >= 0) {
                    traces.add(new ThreadStackTrace(currentThreadID, currentName, currentElements));
                }
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
            this.stackTraces = Collections.unmodifiableList((List<? extends ThreadStackTrace>)traces);
        }
    }
    
    public List<ThreadStackTrace> getStackTraces() {
        return this.stackTraces;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_STACK_TRACE_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_STACK_TRACE_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(1));
        final Attribute traceAttr = this.getEntry().getAttribute("jvmThread");
        if (traceAttr != null) {
            MonitorEntry.addMonitorAttribute(attrs, "jvmThread", MonitorMessages.INFO_STACK_TRACE_DISPNAME_TRACE.get(), MonitorMessages.INFO_STACK_TRACE_DESC_TRACE.get(), Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])traceAttr.getValues())));
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
