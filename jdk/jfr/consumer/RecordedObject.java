package jdk.jfr.consumer;

import java.time.ZoneId;
import java.time.OffsetDateTime;
import jdk.jfr.internal.tool.PrettyWriter;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import jdk.jfr.Timestamp;
import java.time.Instant;
import jdk.jfr.Timespan;
import java.time.Duration;
import jdk.jfr.internal.PrivateAccess;
import java.util.Iterator;
import java.util.Objects;
import jdk.jfr.ValueDescriptor;
import java.util.List;

public class RecordedObject
{
    private final Object[] objects;
    private final List<ValueDescriptor> descriptors;
    private final TimeConverter timeConverter;
    
    RecordedObject(final List<ValueDescriptor> descriptors, final Object[] objects, final TimeConverter timeConverter) {
        this.descriptors = descriptors;
        this.objects = objects;
        this.timeConverter = timeConverter;
    }
    
    final <T> T getTyped(final String s, final Class<T> clazz, final T t) {
        if (!this.hasField(s)) {
            return t;
        }
        final T value = this.getValue(s);
        if (value == null || value.getClass().isAssignableFrom(clazz)) {
            return value;
        }
        return t;
    }
    
    public boolean hasField(final String s) {
        Objects.requireNonNull(s);
        final Iterator<ValueDescriptor> iterator = this.descriptors.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getName().equals(s)) {
                return true;
            }
        }
        final int index = s.indexOf(".");
        if (index > 0) {
            final String substring = s.substring(0, index);
            for (final ValueDescriptor valueDescriptor : this.descriptors) {
                if (!valueDescriptor.getFields().isEmpty() && valueDescriptor.getName().equals(substring)) {
                    final RecordedObject recordedObject = this.getValue(substring);
                    if (recordedObject != null) {
                        return recordedObject.hasField(s.substring(index + 1));
                    }
                    continue;
                }
            }
        }
        return false;
    }
    
    public final <T> T getValue(final String s) {
        return (T)this.getValue(s, false);
    }
    
    private Object getValue(final String s, final boolean b) {
        Objects.requireNonNull(s);
        int n = 0;
        for (final ValueDescriptor valueDescriptor : this.descriptors) {
            if (s.equals(valueDescriptor.getName())) {
                final Object o = this.objects[n];
                if (o == null) {
                    return null;
                }
                if (valueDescriptor.getFields().isEmpty()) {
                    if (!b || !PrivateAccess.getInstance().isUnsigned(valueDescriptor)) {
                        return o;
                    }
                    if (o instanceof Character || o instanceof Long) {
                        return o;
                    }
                    return new UnsignedValue(o);
                }
                else {
                    if (o instanceof RecordedObject) {
                        return o;
                    }
                    final Object[] array = (Object[])o;
                    if (valueDescriptor.isArray()) {
                        return this.structifyArray(valueDescriptor, array, 0);
                    }
                    return new RecordedObject(valueDescriptor.getFields(), (Object[])o, this.timeConverter);
                }
            }
            else {
                ++n;
            }
        }
        final int index = s.indexOf(".");
        if (index > 0) {
            final String substring = s.substring(0, index);
            for (final ValueDescriptor valueDescriptor2 : this.descriptors) {
                if (!valueDescriptor2.getFields().isEmpty() && valueDescriptor2.getName().equals(substring)) {
                    final RecordedObject recordedObject = this.getValue(substring);
                    final String substring2 = s.substring(index + 1);
                    if (recordedObject != null) {
                        return recordedObject.getValue(substring2, b);
                    }
                    this.getValueDescriptor(valueDescriptor2.getFields(), substring2, null);
                    throw new NullPointerException("Field value for \"" + substring + "\" was null. Can't access nested field \"" + substring2 + "\"");
                }
            }
        }
        throw new IllegalArgumentException("Could not find field with name " + s);
    }
    
    private ValueDescriptor getValueDescriptor(final List<ValueDescriptor> list, final String s, final String s2) {
        final int index = s.indexOf(".");
        if (index > 0) {
            final String substring = s.substring(0, index);
            final String substring2 = s.substring(index + 1);
            for (final ValueDescriptor valueDescriptor : list) {
                if (valueDescriptor.getName().equals(substring) && !valueDescriptor.getFields().isEmpty()) {
                    return this.getValueDescriptor(valueDescriptor.getFields(), substring2, s2);
                }
            }
            throw new IllegalArgumentException("Attempt to get unknown field \"" + substring + "\"");
        }
        for (final ValueDescriptor valueDescriptor2 : list) {
            if (valueDescriptor2.getName().equals(s)) {
                if (s2 != null && !valueDescriptor2.getTypeName().equals(s2)) {
                    throw new IllegalArgumentException("Attempt to get " + valueDescriptor2.getTypeName() + " field \"" + s + "\" with illegal data type conversion " + s2);
                }
                return valueDescriptor2;
            }
        }
        throw new IllegalArgumentException("\"Attempt to get unknown field \"" + s + "\"");
    }
    
    private <T> T getTypedValue(final String s, final String s2) {
        Objects.requireNonNull(s);
        this.getValueDescriptor(this.descriptors, s, s2);
        return this.getValue(s);
    }
    
    private Object[] structifyArray(final ValueDescriptor valueDescriptor, final Object[] array, final int n) {
        if (array == null) {
            return null;
        }
        final Object[] array2 = new Object[array.length];
        for (int i = 0; i < array2.length; ++i) {
            final Object o = array[i];
            if (n == 0) {
                if (this.isStackFrameType(valueDescriptor.getTypeName())) {
                    array2[i] = new RecordedFrame(valueDescriptor.getFields(), (Object[])o, this.timeConverter);
                }
                else {
                    array2[i] = new RecordedObject(valueDescriptor.getFields(), (Object[])o, this.timeConverter);
                }
            }
            else {
                array2[i] = this.structifyArray(valueDescriptor, (Object[])o, n - 1);
            }
        }
        return array2;
    }
    
    private boolean isStackFrameType(final String s) {
        return "com.oracle.jfr.types.StackFrame".equals(s) || "jdk.types.StackFrame".equals(s);
    }
    
    public List<ValueDescriptor> getFields() {
        return this.descriptors;
    }
    
    public final boolean getBoolean(final String s) {
        final Boolean value = this.getValue(s);
        if (value instanceof Boolean) {
            return value;
        }
        throw newIllegalArgumentException(s, "boolean");
    }
    
    public final byte getByte(final String s) {
        final Byte value = this.getValue(s);
        if (value instanceof Byte) {
            return value;
        }
        throw newIllegalArgumentException(s, "byte");
    }
    
    public final char getChar(final String s) {
        final Character value = this.getValue(s);
        if (value instanceof Character) {
            return value;
        }
        throw newIllegalArgumentException(s, "char");
    }
    
    public final short getShort(final String s) {
        final Object value = this.getValue(s, true);
        if (value instanceof Short) {
            return (short)value;
        }
        if (value instanceof Byte) {
            return (byte)value;
        }
        if (value instanceof UnsignedValue) {
            final Object value2 = ((UnsignedValue)value).value();
            if (value2 instanceof Short) {
                return (short)value2;
            }
            if (value2 instanceof Byte) {
                return (short)Byte.toUnsignedInt((byte)value2);
            }
        }
        throw newIllegalArgumentException(s, "short");
    }
    
    public final int getInt(final String s) {
        final Object value = this.getValue(s, true);
        if (value instanceof Integer) {
            return (int)value;
        }
        if (value instanceof Short) {
            return (int)value;
        }
        if (value instanceof Character) {
            return (char)value;
        }
        if (value instanceof Byte) {
            return (int)value;
        }
        if (value instanceof UnsignedValue) {
            final Object value2 = ((UnsignedValue)value).value();
            if (value2 instanceof Integer) {
                return (int)value2;
            }
            if (value2 instanceof Short) {
                return Short.toUnsignedInt((short)value2);
            }
            if (value2 instanceof Byte) {
                return Byte.toUnsignedInt((byte)value2);
            }
        }
        throw newIllegalArgumentException(s, "int");
    }
    
    public final float getFloat(final String s) {
        final Character value = this.getValue(s);
        if (value instanceof Float) {
            return value;
        }
        if (value instanceof Long) {
            return value;
        }
        if (value instanceof Integer) {
            return value;
        }
        if (value instanceof Short) {
            return value;
        }
        if (value instanceof Byte) {
            return value;
        }
        if (value instanceof Character) {
            return value;
        }
        throw newIllegalArgumentException(s, "float");
    }
    
    public final long getLong(final String s) {
        final Object value = this.getValue(s, true);
        if (value instanceof Long) {
            return (long)value;
        }
        if (value instanceof Integer) {
            return (long)value;
        }
        if (value instanceof Short) {
            return (long)value;
        }
        if (value instanceof Character) {
            return (char)value;
        }
        if (value instanceof Byte) {
            return (long)value;
        }
        if (value instanceof UnsignedValue) {
            final Object value2 = ((UnsignedValue)value).value();
            if (value2 instanceof Integer) {
                return Integer.toUnsignedLong((int)value2);
            }
            if (value2 instanceof Short) {
                return Short.toUnsignedLong((short)value2);
            }
            if (value2 instanceof Byte) {
                return Byte.toUnsignedLong((byte)value2);
            }
        }
        throw newIllegalArgumentException(s, "long");
    }
    
    public final double getDouble(final String s) {
        final Character value = this.getValue(s);
        if (value instanceof Double) {
            return value;
        }
        if (value instanceof Float) {
            return value;
        }
        if (value instanceof Long) {
            return value;
        }
        if (value instanceof Integer) {
            return value;
        }
        if (value instanceof Short) {
            return value;
        }
        if (value instanceof Byte) {
            return value;
        }
        if (value instanceof Character) {
            return value;
        }
        throw newIllegalArgumentException(s, "double");
    }
    
    public final String getString(final String s) {
        return this.getTypedValue(s, "java.lang.String");
    }
    
    public final Duration getDuration(final String s) {
        final UnsignedValue value = this.getValue(s);
        if (value instanceof Long) {
            return this.getDuration((long)value, s);
        }
        if (value instanceof Integer) {
            return this.getDuration((long)value, s);
        }
        if (value instanceof Short) {
            return this.getDuration((long)value, s);
        }
        if (value instanceof Character) {
            return this.getDuration((char)value, s);
        }
        if (value instanceof Byte) {
            return this.getDuration((long)value, s);
        }
        if (value instanceof UnsignedValue) {
            final Object value2 = value.value();
            if (value2 instanceof Integer) {
                return this.getDuration(Integer.toUnsignedLong((int)value2), s);
            }
            if (value2 instanceof Short) {
                return this.getDuration(Short.toUnsignedLong((short)value2), s);
            }
            if (value2 instanceof Byte) {
                return this.getDuration(Short.toUnsignedLong((byte)value2), s);
            }
        }
        throw newIllegalArgumentException(s, "java,time.Duration");
    }
    
    private Duration getDuration(final long n, final String s) throws InternalError {
        final ValueDescriptor valueDescriptor = this.getValueDescriptor(this.descriptors, s, null);
        if (n == Long.MIN_VALUE) {
            return Duration.ofSeconds(Long.MIN_VALUE, 0L);
        }
        final Timespan timespan = valueDescriptor.getAnnotation(Timespan.class);
        if (timespan == null) {
            throw new IllegalArgumentException("Attempt to get " + valueDescriptor.getTypeName() + " field \"" + s + "\" with missing @Timespan");
        }
        final String value = timespan.value();
        switch (value) {
            case "MICROSECONDS": {
                return Duration.ofNanos(1000L * n);
            }
            case "SECONDS": {
                return Duration.ofSeconds(n);
            }
            case "MILLISECONDS": {
                return Duration.ofMillis(n);
            }
            case "NANOSECONDS": {
                return Duration.ofNanos(n);
            }
            case "TICKS": {
                return Duration.ofNanos(this.timeConverter.convertTimespan(n));
            }
            default: {
                throw new IllegalArgumentException("Attempt to get " + valueDescriptor.getTypeName() + " field \"" + s + "\" with illegal timespan unit " + timespan.value());
            }
        }
    }
    
    public final Instant getInstant(final String s) {
        final Object value = this.getValue(s, true);
        if (value instanceof Long) {
            return this.getInstant((long)value, s);
        }
        if (value instanceof Integer) {
            return this.getInstant((long)value, s);
        }
        if (value instanceof Short) {
            return this.getInstant((long)value, s);
        }
        if (value instanceof Character) {
            return this.getInstant((char)value, s);
        }
        if (value instanceof Byte) {
            return this.getInstant((long)value, s);
        }
        if (value instanceof UnsignedValue) {
            final Object value2 = ((UnsignedValue)value).value();
            if (value2 instanceof Integer) {
                return this.getInstant(Integer.toUnsignedLong((int)value2), s);
            }
            if (value2 instanceof Short) {
                return this.getInstant(Short.toUnsignedLong((short)value2), s);
            }
            if (value2 instanceof Byte) {
                return this.getInstant(Short.toUnsignedLong((byte)value2), s);
            }
        }
        throw newIllegalArgumentException(s, "java.time.Instant");
    }
    
    private Instant getInstant(final long n, final String s) {
        final ValueDescriptor valueDescriptor = this.getValueDescriptor(this.descriptors, s, null);
        final Timestamp timestamp = valueDescriptor.getAnnotation(Timestamp.class);
        if (timestamp == null) {
            throw new IllegalArgumentException("Attempt to get " + valueDescriptor.getTypeName() + " field \"" + s + "\" with missing @Timestamp");
        }
        if (n == Long.MIN_VALUE) {
            return Instant.MIN;
        }
        final String value = timestamp.value();
        switch (value) {
            case "MILLISECONDS_SINCE_EPOCH": {
                return Instant.ofEpochMilli(n);
            }
            case "TICKS": {
                return Instant.ofEpochSecond(0L, this.timeConverter.convertTimestamp(n));
            }
            default: {
                throw new IllegalArgumentException("Attempt to get " + valueDescriptor.getTypeName() + " field \"" + s + "\" with illegal timestamp unit " + timestamp.value());
            }
        }
    }
    
    public final RecordedClass getClass(final String s) {
        return this.getTypedValue(s, "java.lang.Class");
    }
    
    public final RecordedThread getThread(final String s) {
        return this.getTypedValue(s, "java.lang.Thread");
    }
    
    @Override
    public final String toString() {
        final StringWriter stringWriter = new StringWriter();
        final PrettyWriter prettyWriter = new PrettyWriter(new PrintWriter(stringWriter));
        prettyWriter.setStackDepth(5);
        if (this instanceof RecordedEvent) {
            prettyWriter.print((RecordedEvent)this);
        }
        else {
            prettyWriter.print(this, "");
        }
        prettyWriter.flush(true);
        return stringWriter.toString();
    }
    
    OffsetDateTime getOffsetDateTime(final String s) {
        if (this.getInstant(s).equals(Instant.MIN)) {
            return OffsetDateTime.MIN;
        }
        return OffsetDateTime.ofInstant(this.getInstant(s), this.timeConverter.getZoneOffset());
    }
    
    private static IllegalArgumentException newIllegalArgumentException(final String s, final String s2) {
        return new IllegalArgumentException("Attempt to get field \"" + s + "\" with illegal data type conversion " + s2);
    }
    
    private static final class UnsignedValue
    {
        private final Object o;
        
        UnsignedValue(final Object o) {
            this.o = o;
        }
        
        Object value() {
            return this.o;
        }
    }
}
