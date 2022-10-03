package jdk.jfr.internal.tool;

import jdk.jfr.consumer.RecordedFrame;
import jdk.jfr.EventType;
import jdk.jfr.consumer.RecordedObject;
import jdk.jfr.ValueDescriptor;
import java.util.Iterator;
import jdk.jfr.consumer.RecordedEvent;
import java.util.List;
import java.io.PrintWriter;

final class XMLWriter extends EventPrintWriter
{
    public XMLWriter(final PrintWriter printWriter) {
        super(printWriter);
    }
    
    @Override
    protected void printBegin() {
        this.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        this.println("<recording xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
        this.indent();
        this.printIndent();
        this.println("<events>");
        this.indent();
    }
    
    @Override
    protected void printEnd() {
        this.retract();
        this.printIndent();
        this.println("</events>");
        this.retract();
        this.println("</recording>");
    }
    
    @Override
    protected void print(final List<RecordedEvent> list) {
        final Iterator<RecordedEvent> iterator = list.iterator();
        while (iterator.hasNext()) {
            this.printEvent(iterator.next());
        }
    }
    
    private void printEvent(final RecordedEvent recordedEvent) {
        final EventType eventType = recordedEvent.getEventType();
        this.printIndent();
        this.print("<event");
        this.printAttribute("type", eventType.getName());
        this.print(">");
        this.println();
        this.indent();
        for (final ValueDescriptor valueDescriptor : recordedEvent.getFields()) {
            this.printValueDescriptor(valueDescriptor, this.getValue(recordedEvent, valueDescriptor), -1);
        }
        this.retract();
        this.printIndent();
        this.println("</event>");
        this.println();
    }
    
    private void printAttribute(final String s, final String s2) {
        this.print(" ", s, "=\"", s2, "\"");
    }
    
    public void printObject(final RecordedObject recordedObject) {
        this.println();
        this.indent();
        for (final ValueDescriptor valueDescriptor : recordedObject.getFields()) {
            this.printValueDescriptor(valueDescriptor, this.getValue(recordedObject, valueDescriptor), -1);
        }
        this.retract();
    }
    
    private void printArray(final ValueDescriptor valueDescriptor, final Object[] array) {
        this.println();
        this.indent();
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            if (!(array[i] instanceof RecordedFrame) || n < this.getStackDepth()) {
                this.printValueDescriptor(valueDescriptor, array[i], i);
            }
            ++n;
        }
        this.retract();
    }
    
    private void printValueDescriptor(final ValueDescriptor valueDescriptor, final Object o, final int n) {
        final boolean b = n != -1;
        final String s = b ? null : valueDescriptor.getName();
        if (valueDescriptor.isArray() && !b) {
            if (this.printBeginElement("array", s, o, n)) {
                this.printArray(valueDescriptor, (Object[])o);
                this.printIndent();
                this.printEndElement("array");
            }
            return;
        }
        if (!valueDescriptor.getFields().isEmpty()) {
            if (this.printBeginElement("struct", s, o, n)) {
                this.printObject((RecordedObject)o);
                this.printIndent();
                this.printEndElement("struct");
            }
            return;
        }
        if (this.printBeginElement("value", s, o, n)) {
            this.printEscaped(String.valueOf(o));
            this.printEndElement("value");
        }
    }
    
    private boolean printBeginElement(final String s, final String s2, final Object o, final int n) {
        this.printIndent();
        this.print("<", s);
        if (s2 != null) {
            this.printAttribute("name", s2);
        }
        if (n != -1) {
            this.printAttribute("index", Integer.toString(n));
        }
        if (o == null) {
            this.printAttribute("xsi:nil", "true");
            this.println("/>");
            return false;
        }
        if (o.getClass().isArray()) {
            this.printAttribute("size", Integer.toString(((Object[])o).length));
        }
        this.print(">");
        return true;
    }
    
    private void printEndElement(final String s) {
        this.print("</");
        this.print(s);
        this.println(">");
    }
    
    private void printEscaped(final String s) {
        for (int i = 0; i < s.length(); ++i) {
            this.printEscaped(s.charAt(i));
        }
    }
    
    private void printEscaped(final char c) {
        if (c == '\"') {
            this.print("&quot;");
            return;
        }
        if (c == '&') {
            this.print("&amp;");
            return;
        }
        if (c == '\'') {
            this.print("&apos;");
            return;
        }
        if (c == '<') {
            this.print("&lt;");
            return;
        }
        if (c == '>') {
            this.print("&gt;");
            return;
        }
        if (c > '\u007f') {
            this.print("&#");
            this.print((int)c);
            this.print(';');
            return;
        }
        this.print(c);
    }
}
