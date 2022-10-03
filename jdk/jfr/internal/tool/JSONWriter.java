package jdk.jfr.internal.tool;

import jdk.jfr.consumer.RecordedFrame;
import jdk.jfr.consumer.RecordedObject;
import jdk.jfr.ValueDescriptor;
import java.util.Iterator;
import jdk.jfr.consumer.RecordedEvent;
import java.util.List;
import java.io.PrintWriter;

final class JSONWriter extends EventPrintWriter
{
    private boolean first;
    
    public JSONWriter(final PrintWriter printWriter) {
        super(printWriter);
        this.first = true;
    }
    
    @Override
    protected void printBegin() {
        this.printObjectBegin();
        this.printDataStructureName("recording");
        this.printObjectBegin();
        this.printDataStructureName("events");
        this.printArrayBegin();
    }
    
    @Override
    protected void print(final List<RecordedEvent> list) {
        for (final RecordedEvent recordedEvent : list) {
            this.printNewDataStructure(this.first, true, null);
            this.printEvent(recordedEvent);
            this.flush(false);
            this.first = false;
        }
    }
    
    @Override
    protected void printEnd() {
        this.printArrayEnd();
        this.printObjectEnd();
        this.printObjectEnd();
    }
    
    private void printEvent(final RecordedEvent recordedEvent) {
        this.printObjectBegin();
        this.printValue(true, false, "type", recordedEvent.getEventType().getName());
        this.printNewDataStructure(false, false, "values");
        this.printObjectBegin();
        boolean b = true;
        for (final ValueDescriptor valueDescriptor : recordedEvent.getFields()) {
            this.printValueDescriptor(b, false, valueDescriptor, this.getValue(recordedEvent, valueDescriptor));
            b = false;
        }
        this.printObjectEnd();
        this.printObjectEnd();
    }
    
    void printValue(final boolean b, final boolean b2, final String s, final Object o) {
        this.printNewDataStructure(b, b2, s);
        if (!this.printIfNull(o)) {
            if (o instanceof Boolean) {
                this.printAsString(o);
                return;
            }
            if (o instanceof Double) {
                final Double n = (Double)o;
                if (Double.isNaN(n) || Double.isInfinite(n)) {
                    this.printNull();
                    return;
                }
                this.printAsString(o);
            }
            else if (o instanceof Float) {
                final Float n2 = (Float)o;
                if (Float.isNaN(n2) || Float.isInfinite(n2)) {
                    this.printNull();
                    return;
                }
                this.printAsString(o);
            }
            else {
                if (o instanceof Number) {
                    this.printAsString(o);
                    return;
                }
                this.print("\"");
                this.printEscaped(String.valueOf(o));
                this.print("\"");
            }
        }
    }
    
    public void printObject(final RecordedObject recordedObject) {
        this.printObjectBegin();
        boolean b = true;
        for (final ValueDescriptor valueDescriptor : recordedObject.getFields()) {
            this.printValueDescriptor(b, false, valueDescriptor, this.getValue(recordedObject, valueDescriptor));
            b = false;
        }
        this.printObjectEnd();
    }
    
    private void printArray(final ValueDescriptor valueDescriptor, final Object[] array) {
        this.printArrayBegin();
        boolean b = true;
        int n = 0;
        for (final Object o : array) {
            if (!(o instanceof RecordedFrame) || n < this.getStackDepth()) {
                this.printValueDescriptor(b, true, valueDescriptor, o);
            }
            ++n;
            b = false;
        }
        this.printArrayEnd();
    }
    
    private void printValueDescriptor(final boolean b, final boolean b2, final ValueDescriptor valueDescriptor, final Object o) {
        if (valueDescriptor.isArray() && !b2) {
            this.printNewDataStructure(b, b2, valueDescriptor.getName());
            if (!this.printIfNull(o)) {
                this.printArray(valueDescriptor, (Object[])o);
            }
            return;
        }
        if (!valueDescriptor.getFields().isEmpty()) {
            this.printNewDataStructure(b, b2, valueDescriptor.getName());
            if (!this.printIfNull(o)) {
                this.printObject((RecordedObject)o);
            }
            return;
        }
        this.printValue(b, b2, valueDescriptor.getName(), o);
    }
    
    private void printNewDataStructure(final boolean b, final boolean b2, final String s) {
        if (!b) {
            this.print(", ");
            if (!b2) {
                this.println();
            }
        }
        if (!b2) {
            this.printDataStructureName(s);
        }
    }
    
    private boolean printIfNull(final Object o) {
        if (o == null) {
            this.printNull();
            return true;
        }
        return false;
    }
    
    private void printNull() {
        this.print("null");
    }
    
    private void printDataStructureName(final String s) {
        this.printIndent();
        this.print("\"");
        this.print(s);
        this.print("\": ");
    }
    
    private void printObjectEnd() {
        this.retract();
        this.println();
        this.printIndent();
        this.print("}");
    }
    
    private void printObjectBegin() {
        this.println("{");
        this.indent();
    }
    
    private void printArrayEnd() {
        this.print("]");
    }
    
    private void printArrayBegin() {
        this.print("[");
    }
    
    private void printEscaped(final String s) {
        for (int i = 0; i < s.length(); ++i) {
            this.printEscaped(s.charAt(i));
        }
    }
    
    private void printEscaped(final char c) {
        if (c == '\b') {
            this.print("\\b");
            return;
        }
        if (c == '\n') {
            this.print("\\n");
            return;
        }
        if (c == '\t') {
            this.print("\\t");
            return;
        }
        if (c == '\f') {
            this.print("\\f");
            return;
        }
        if (c == '\r') {
            this.print("\\r");
            return;
        }
        if (c == '\"') {
            this.print("\\\"");
            return;
        }
        if (c == '\\') {
            this.print("\\\\");
            return;
        }
        if (c == '/') {
            this.print("\\/");
            return;
        }
        if (c > '\u007f' || c < ' ') {
            this.print("\\u");
            this.print(Integer.toHexString(65536 + c).substring(1));
            return;
        }
        this.print(c);
    }
}
