package jdk.jfr.internal.tool;

import jdk.jfr.MemoryAddress;
import jdk.jfr.internal.Utils;
import jdk.jfr.Frequency;
import jdk.jfr.DataAmount;
import jdk.jfr.Percentage;
import java.time.temporal.TemporalAccessor;
import java.time.OffsetDateTime;
import java.time.Duration;
import java.util.ArrayList;
import jdk.jfr.consumer.RecordedMethod;
import jdk.jfr.consumer.RecordedClassLoader;
import jdk.jfr.consumer.RecordedClass;
import jdk.jfr.consumer.RecordedThread;
import jdk.jfr.consumer.RecordedFrame;
import jdk.jfr.consumer.RecordedStackTrace;
import jdk.jfr.consumer.RecordedObject;
import java.util.StringJoiner;
import jdk.jfr.AnnotationElement;
import jdk.jfr.internal.PrivateAccess;
import jdk.jfr.ValueDescriptor;
import jdk.jfr.internal.Type;
import java.util.Iterator;
import java.util.List;
import java.io.PrintWriter;
import jdk.jfr.consumer.RecordedEvent;
import java.time.format.DateTimeFormatter;

public final class PrettyWriter extends EventPrintWriter
{
    private static final String TYPE_OLD_OBJECT = "jdk.types.OldObject";
    private static final DateTimeFormatter TIME_FORMAT;
    private static final Long ZERO;
    private boolean showIds;
    private RecordedEvent currentEvent;
    
    public PrettyWriter(final PrintWriter printWriter) {
        super(printWriter);
    }
    
    @Override
    protected void print(final List<RecordedEvent> list) {
        final Iterator<RecordedEvent> iterator = list.iterator();
        while (iterator.hasNext()) {
            this.print(iterator.next());
            this.flush(false);
        }
    }
    
    public void printType(final Type type) {
        if (this.showIds) {
            this.print("// id: ");
            this.println(String.valueOf(type.getId()));
        }
        final int n = type.getName().length() + 10;
        final String name = type.getName();
        final int lastIndex = name.lastIndexOf(".");
        if (lastIndex != -1) {
            this.println("@Name(\"" + name + "\")");
        }
        this.printAnnotations(n, type.getAnnotationElements());
        this.print("class " + name.substring(lastIndex + 1));
        final String superType = type.getSuperType();
        if (superType != null) {
            this.print(" extends " + superType);
        }
        this.println(" {");
        this.indent();
        boolean b = true;
        final Iterator<ValueDescriptor> iterator = type.getFields().iterator();
        while (iterator.hasNext()) {
            this.printField(n, iterator.next(), b);
            b = false;
        }
        this.retract();
        this.println("}");
        this.println();
    }
    
    private void printField(final int n, final ValueDescriptor valueDescriptor, final boolean b) {
        if (!b) {
            this.println();
        }
        this.printAnnotations(n, valueDescriptor.getAnnotationElements());
        this.printIndent();
        if (Type.SUPER_TYPE_SETTING.equals(PrivateAccess.getInstance().getType(valueDescriptor).getSuperType())) {
            this.print("static ");
        }
        this.print(this.makeSimpleType(valueDescriptor.getTypeName()));
        if (valueDescriptor.isArray()) {
            this.print("[]");
        }
        this.print(" ");
        this.print(valueDescriptor.getName());
        this.print(";");
        this.printCommentRef(n, valueDescriptor.getTypeId());
    }
    
    private void printCommentRef(final int n, final long n2) {
        if (this.showIds) {
            int i = this.getColumn();
            if (i > n) {
                this.print("  ");
            }
            else {
                while (i < n) {
                    this.print(" ");
                    ++i;
                }
            }
            this.println(" // id=" + n2);
        }
        else {
            this.println();
        }
    }
    
    private void printAnnotations(final int n, final List<AnnotationElement> list) {
        for (final AnnotationElement annotationElement : list) {
            this.printIndent();
            this.print("@");
            this.print(this.makeSimpleType(annotationElement.getTypeName()));
            if (!annotationElement.getValueDescriptors().isEmpty()) {
                this.printAnnotation(annotationElement);
                this.printCommentRef(n, annotationElement.getTypeId());
            }
            else {
                this.println();
            }
        }
    }
    
    private void printAnnotation(final AnnotationElement annotationElement) {
        final StringJoiner stringJoiner = new StringJoiner(", ", "(", ")");
        final List<ValueDescriptor> valueDescriptors = annotationElement.getValueDescriptors();
        for (final ValueDescriptor valueDescriptor : valueDescriptors) {
            final Object value = annotationElement.getValue(valueDescriptor.getName());
            if (valueDescriptors.size() == 1 && valueDescriptor.getName().equals("value")) {
                stringJoiner.add(this.textify(value));
            }
            else {
                stringJoiner.add(valueDescriptor.getName() + "=" + this.textify(value));
            }
        }
        this.print(stringJoiner.toString());
    }
    
    private String textify(final Object o) {
        if (!o.getClass().isArray()) {
            return this.quoteIfNeeded(o);
        }
        final Object[] array = (Object[])o;
        if (array.length == 1) {
            return this.quoteIfNeeded(array[0]);
        }
        final StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
        final Object[] array2 = array;
        for (int length = array2.length, i = 0; i < length; ++i) {
            stringJoiner.add(this.quoteIfNeeded(array2[i]));
        }
        return stringJoiner.toString();
    }
    
    private String quoteIfNeeded(final Object o) {
        if (o instanceof String) {
            return "\"" + o + "\"";
        }
        return String.valueOf(o);
    }
    
    private String makeSimpleType(final String s) {
        return s.substring(s.lastIndexOf(".") + 1);
    }
    
    public void print(final RecordedEvent currentEvent) {
        this.currentEvent = currentEvent;
        this.print(currentEvent.getEventType().getName(), " ");
        this.println("{");
        this.indent();
        for (final ValueDescriptor valueDescriptor : currentEvent.getFields()) {
            final String name = valueDescriptor.getName();
            if (!this.isZeroDuration(currentEvent, name) && !this.isLateField(name)) {
                this.printFieldValue(currentEvent, valueDescriptor);
            }
        }
        if (currentEvent.getThread() != null) {
            this.printIndent();
            this.print("eventThread = ");
            this.printThread(currentEvent.getThread(), "");
        }
        if (currentEvent.getStackTrace() != null) {
            this.printIndent();
            this.print("stackTrace = ");
            this.printStackTrace(currentEvent.getStackTrace());
        }
        this.retract();
        this.printIndent();
        this.println("}");
        this.println();
    }
    
    private boolean isZeroDuration(final RecordedEvent recordedEvent, final String s) {
        return s.equals("duration") && PrettyWriter.ZERO.equals(recordedEvent.getValue("duration"));
    }
    
    private void printStackTrace(final RecordedStackTrace recordedStackTrace) {
        this.println("[");
        final List<RecordedFrame> frames = recordedStackTrace.getFrames();
        this.indent();
        int n;
        for (n = 0; n < frames.size() && n < this.getStackDepth(); ++n) {
            final RecordedFrame recordedFrame = frames.get(n);
            if (recordedFrame.isJavaFrame()) {
                this.printIndent();
                this.printValue(recordedFrame, null, "");
                this.println();
            }
        }
        if (recordedStackTrace.isTruncated() || n == this.getStackDepth()) {
            this.printIndent();
            this.println("...");
        }
        this.retract();
        this.printIndent();
        this.println("]");
    }
    
    public void print(final RecordedObject recordedObject, final String s) {
        this.println("{");
        this.indent();
        final Iterator<ValueDescriptor> iterator = recordedObject.getFields().iterator();
        while (iterator.hasNext()) {
            this.printFieldValue(recordedObject, iterator.next());
        }
        this.retract();
        this.printIndent();
        this.println("}" + s);
    }
    
    private void printFieldValue(final RecordedObject recordedObject, final ValueDescriptor valueDescriptor) {
        this.printIndent();
        this.print(valueDescriptor.getName(), " = ");
        this.printValue(this.getValue(recordedObject, valueDescriptor), valueDescriptor, "");
    }
    
    private void printArray(final Object[] array) {
        this.println("[");
        this.indent();
        for (int i = 0; i < array.length; ++i) {
            this.printIndent();
            this.printValue(array[i], null, (i + 1 < array.length) ? ", " : "");
        }
        this.retract();
        this.printIndent();
        this.println("]");
    }
    
    private void printValue(final Object o, final ValueDescriptor valueDescriptor, final String s) {
        if (o == null) {
            this.println("N/A" + s);
            return;
        }
        if (o instanceof RecordedObject) {
            if (o instanceof RecordedThread) {
                this.printThread((RecordedThread)o, s);
                return;
            }
            if (o instanceof RecordedClass) {
                this.printClass((RecordedClass)o, s);
                return;
            }
            if (o instanceof RecordedClassLoader) {
                this.printClassLoader((RecordedClassLoader)o, s);
                return;
            }
            if (o instanceof RecordedFrame && ((RecordedFrame)o).isJavaFrame()) {
                this.printJavaFrame((RecordedFrame)o, s);
                return;
            }
            if (o instanceof RecordedMethod) {
                this.println(this.formatMethod((RecordedMethod)o));
                return;
            }
            if (valueDescriptor.getTypeName().equals("jdk.types.OldObject")) {
                this.printOldObject((RecordedObject)o);
                return;
            }
            this.print((RecordedObject)o, s);
        }
        else {
            if (o.getClass().isArray()) {
                this.printArray((Object[])o);
                return;
            }
            if (o instanceof Double) {
                final Double n = (Double)o;
                if (Double.isNaN(n) || n == Double.NEGATIVE_INFINITY) {
                    this.println("N/A");
                    return;
                }
            }
            if (o instanceof Float) {
                final Float n2 = (Float)o;
                if (Float.isNaN(n2) || n2 == Float.NEGATIVE_INFINITY) {
                    this.println("N/A");
                    return;
                }
            }
            if (o instanceof Long && (long)o == Long.MIN_VALUE) {
                this.println("N/A");
                return;
            }
            if (o instanceof Integer && (int)o == Integer.MIN_VALUE) {
                this.println("N/A");
                return;
            }
            if (valueDescriptor.getContentType() != null && this.printFormatted(valueDescriptor, o)) {
                return;
            }
            String s2 = String.valueOf(o);
            if (o instanceof String) {
                s2 = "\"" + s2 + "\"";
            }
            this.println(s2);
        }
    }
    
    private void printOldObject(final RecordedObject recordedObject) {
        this.println(" [");
        this.indent();
        this.printIndent();
        try {
            this.printReferenceChain(recordedObject);
        }
        catch (final IllegalArgumentException ex) {}
        this.retract();
        this.printIndent();
        this.println("]");
    }
    
    private void printReferenceChain(RecordedObject recordedObject) {
        this.printObject(recordedObject, this.currentEvent.getLong("arrayElements"));
        for (RecordedObject recordedObject2 = recordedObject.getValue("referrer"); recordedObject2 != null; recordedObject2 = recordedObject.getValue("referrer")) {
            if (recordedObject2.getLong("skip") > 0L) {
                this.printIndent();
                this.println("...");
            }
            String s = "";
            long long1 = Long.MIN_VALUE;
            final RecordedObject recordedObject3 = recordedObject2.getValue("array");
            if (recordedObject3 != null) {
                final long long2 = recordedObject3.getLong("index");
                long1 = recordedObject3.getLong("size");
                s = "[" + long2 + "]";
            }
            final RecordedObject recordedObject4 = recordedObject2.getValue("field");
            if (recordedObject4 != null) {
                s = recordedObject4.getString("name");
            }
            this.printIndent();
            this.print(s);
            this.print(" : ");
            recordedObject = (RecordedObject)recordedObject2.getValue("object");
            if (recordedObject != null) {
                this.printObject(recordedObject, long1);
            }
        }
    }
    
    void printObject(final RecordedObject recordedObject, final long n) {
        final RecordedClass class1 = recordedObject.getClass("type");
        if (class1 != null) {
            String name = class1.getName();
            if (name != null && name.startsWith("[")) {
                name = this.decodeDescriptors(name, (n > 0L) ? Long.toString(n) : "").get(0);
            }
            this.print(name);
            final String string = recordedObject.getString("description");
            if (string != null) {
                this.print(" ");
                this.print(string);
            }
        }
        this.println();
    }
    
    private void printClassLoader(final RecordedClassLoader recordedClassLoader, final String s) {
        final RecordedClass type = recordedClassLoader.getType();
        this.print((type == null) ? "null" : type.getName());
        if (type != null) {
            this.print(" (");
            this.print("id = ");
            this.print(String.valueOf(recordedClassLoader.getId()));
            this.println(")");
        }
    }
    
    private void printJavaFrame(final RecordedFrame recordedFrame, final String s) {
        this.print(this.formatMethod(recordedFrame.getMethod()));
        final int lineNumber = recordedFrame.getLineNumber();
        if (lineNumber >= 0) {
            this.print(" line: " + lineNumber);
        }
        this.print(s);
    }
    
    private String formatMethod(final RecordedMethod recordedMethod) {
        final StringBuilder sb = new StringBuilder();
        sb.append(recordedMethod.getType().getName());
        sb.append(".");
        sb.append(recordedMethod.getName());
        sb.append("(");
        final StringJoiner stringJoiner = new StringJoiner(", ");
        final String replace = recordedMethod.getDescriptor().replace("/", ".");
        for (final String s : this.decodeDescriptors(replace.substring(1, replace.lastIndexOf(")")), "")) {
            stringJoiner.add(s.substring(s.lastIndexOf(46) + 1));
        }
        sb.append(stringJoiner);
        sb.append(")");
        return sb.toString();
    }
    
    private void printClass(final RecordedClass recordedClass, final String s) {
        final RecordedClassLoader classLoader = recordedClass.getClassLoader();
        String s2 = "null";
        if (classLoader != null) {
            if (classLoader.getName() != null) {
                s2 = classLoader.getName();
            }
            else {
                s2 = classLoader.getType().getName();
            }
        }
        String name = recordedClass.getName();
        if (name.startsWith("[")) {
            name = this.decodeDescriptors(name, "").get(0);
        }
        this.println(name + " (classLoader = " + s2 + ")" + s);
    }
    
    List<String> decodeDescriptors(final String s, String s2) {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < s.length(); ++i) {
            String string = "";
            while (s.charAt(i) == '[') {
                string = string + "[" + s2 + "]";
                s2 = "";
                ++i;
            }
            String substring = null;
            switch (s.charAt(i)) {
                case 'L': {
                    final int index = s.indexOf(59, i);
                    substring = s.substring(i + 1, index);
                    i = index;
                    break;
                }
                case 'I': {
                    substring = "int";
                    break;
                }
                case 'J': {
                    substring = "long";
                    break;
                }
                case 'Z': {
                    substring = "boolean";
                    break;
                }
                case 'D': {
                    substring = "double";
                    break;
                }
                case 'F': {
                    substring = "float";
                    break;
                }
                case 'S': {
                    substring = "short";
                    break;
                }
                case 'C': {
                    substring = "char";
                    break;
                }
                case 'B': {
                    substring = "byte";
                    break;
                }
                default: {
                    substring = "<unknown-descriptor-type>";
                    break;
                }
            }
            list.add(substring + string);
        }
        return list;
    }
    
    private void printThread(final RecordedThread recordedThread, final String s) {
        if (recordedThread.getJavaThreadId() > 0L) {
            this.println("\"" + recordedThread.getJavaName() + "\" (javaThreadId = " + recordedThread.getJavaThreadId() + ")" + s);
        }
        else {
            this.println("\"" + recordedThread.getOSName() + "\" (osThreadId = " + recordedThread.getOSThreadId() + ")" + s);
        }
    }
    
    private boolean printFormatted(final ValueDescriptor valueDescriptor, final Object o) {
        if (o instanceof Duration) {
            final Duration duration = (Duration)o;
            if (duration.getSeconds() == Long.MIN_VALUE && duration.getNano() == 0) {
                this.println("N/A");
                return true;
            }
            final double n = duration.getNano() / 1.0E9 + (int)(duration.getSeconds() % 60L);
            if (n < 1.0) {
                if (n < 0.001) {
                    this.println(String.format("%.3f", n * 1000000.0) + " us");
                }
                else {
                    this.println(String.format("%.3f", n * 1000.0) + " ms");
                }
            }
            else if (n < 1000.0) {
                this.println(String.format("%.3f", n) + " s");
            }
            else {
                this.println(String.format("%.0f", n) + " s");
            }
            return true;
        }
        else if (o instanceof OffsetDateTime) {
            final OffsetDateTime offsetDateTime = (OffsetDateTime)o;
            if (offsetDateTime.equals(OffsetDateTime.MIN)) {
                this.println("N/A");
                return true;
            }
            this.println(PrettyWriter.TIME_FORMAT.format(offsetDateTime));
            return true;
        }
        else {
            if (valueDescriptor.getAnnotation(Percentage.class) != null && o instanceof Number) {
                this.println(String.format("%.2f", ((Number)o).doubleValue() * 100.0) + "%");
                return true;
            }
            final DataAmount dataAmount = valueDescriptor.getAnnotation(DataAmount.class);
            if (dataAmount != null && o instanceof Number) {
                final long longValue = ((Number)o).longValue();
                if (valueDescriptor.getAnnotation(Frequency.class) != null) {
                    if (dataAmount.value().equals("BYTES")) {
                        this.println(Utils.formatBytesPerSecond(longValue));
                        return true;
                    }
                    if (dataAmount.value().equals("BITS")) {
                        this.println(Utils.formatBitsPerSecond(longValue));
                        return true;
                    }
                }
                else {
                    if (dataAmount.value().equals("BYTES")) {
                        this.println(Utils.formatBytes(longValue));
                        return true;
                    }
                    if (dataAmount.value().equals("BITS")) {
                        this.println(Utils.formatBits(longValue));
                        return true;
                    }
                }
            }
            if (valueDescriptor.getAnnotation(MemoryAddress.class) != null && o instanceof Number) {
                this.println(String.format("0x%08X", ((Number)o).longValue()));
                return true;
            }
            if (valueDescriptor.getAnnotation(Frequency.class) != null && o instanceof Number) {
                this.println(o + " Hz");
                return true;
            }
            return false;
        }
    }
    
    public void setShowIds(final boolean showIds) {
        this.showIds = showIds;
    }
    
    static {
        TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        ZERO = 0L;
    }
}
