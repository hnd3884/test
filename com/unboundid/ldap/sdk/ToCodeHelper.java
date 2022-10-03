package com.unboundid.ldap.sdk;

import java.util.Iterator;
import com.unboundid.util.StaticUtils;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ToCodeHelper
{
    private ToCodeHelper() {
    }
    
    public static void generateMethodCall(final List<String> lineList, final int indentSpaces, final String returnType, final String variableName, final String methodName, final ToCodeArgHelper... methodArgs) {
        generateMethodCall(lineList, indentSpaces, returnType, variableName, methodName, StaticUtils.toList(methodArgs));
    }
    
    public static void generateMethodCall(final List<String> lineList, final int indentSpaces, final String returnType, final String variableName, final String methodName, final List<ToCodeArgHelper> methodArgs) {
        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < indentSpaces; ++i) {
            buffer.append(' ');
        }
        final String indent = buffer.toString();
        if (returnType != null) {
            buffer.append(returnType);
            buffer.append(' ');
        }
        if (variableName != null) {
            buffer.append(variableName);
            buffer.append(" = ");
        }
        buffer.append(methodName);
        buffer.append('(');
        if (methodArgs == null || methodArgs.isEmpty()) {
            buffer.append(");");
            lineList.add(buffer.toString());
        }
        else {
            lineList.add(buffer.toString());
            final Iterator<ToCodeArgHelper> argIterator = methodArgs.iterator();
            while (argIterator.hasNext()) {
                final ToCodeArgHelper arg = argIterator.next();
                boolean firstLine = true;
                final Iterator<String> argLineIterator = arg.getLines().iterator();
                while (argLineIterator.hasNext()) {
                    buffer.setLength(0);
                    buffer.append(indent);
                    buffer.append("     ");
                    buffer.append(argLineIterator.next());
                    if (!argLineIterator.hasNext()) {
                        if (argIterator.hasNext()) {
                            buffer.append(',');
                        }
                        else {
                            buffer.append(");");
                        }
                    }
                    if (firstLine) {
                        firstLine = false;
                        final String comment = arg.getComment();
                        if (comment != null) {
                            buffer.append(" // ");
                            buffer.append(comment);
                        }
                    }
                    lineList.add(buffer.toString());
                }
            }
        }
    }
    
    public static void generateVariableAssignment(final List<String> lineList, final int indentSpaces, final String dataType, final String variableName, final ToCodeArgHelper valueArg) {
        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < indentSpaces; ++i) {
            buffer.append(' ');
        }
        final String indent = buffer.toString();
        if (dataType != null) {
            buffer.append(dataType);
            buffer.append(' ');
        }
        buffer.append(variableName);
        buffer.append(" = ");
        boolean firstLine = true;
        final Iterator<String> valueLineIterator = valueArg.getLines().iterator();
        while (valueLineIterator.hasNext()) {
            final String s = valueLineIterator.next();
            if (!firstLine) {
                buffer.setLength(0);
                buffer.append(indent);
            }
            buffer.append(s);
            if (!valueLineIterator.hasNext()) {
                buffer.append(';');
            }
            if (firstLine) {
                firstLine = false;
                final String comment = valueArg.getComment();
                if (comment != null) {
                    buffer.append(" // ");
                    buffer.append(comment);
                }
            }
            lineList.add(buffer.toString());
        }
    }
}
