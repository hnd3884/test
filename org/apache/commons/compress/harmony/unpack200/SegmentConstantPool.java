package org.apache.commons.compress.harmony.unpack200;

import java.util.List;
import org.apache.commons.compress.harmony.unpack200.bytecode.ConstantPoolEntry;
import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFileEntry;

public class SegmentConstantPool
{
    private final CpBands bands;
    private final SegmentConstantPoolArrayCache arrayCache;
    public static final int ALL = 0;
    public static final int UTF_8 = 1;
    public static final int CP_INT = 2;
    public static final int CP_FLOAT = 3;
    public static final int CP_LONG = 4;
    public static final int CP_DOUBLE = 5;
    public static final int CP_STRING = 6;
    public static final int CP_CLASS = 7;
    public static final int SIGNATURE = 8;
    public static final int CP_DESCR = 9;
    public static final int CP_FIELD = 10;
    public static final int CP_METHOD = 11;
    public static final int CP_IMETHOD = 12;
    protected static final String REGEX_MATCH_ALL = ".*";
    protected static final String INITSTRING = "<init>";
    protected static final String REGEX_MATCH_INIT = "^<init>.*";
    
    public SegmentConstantPool(final CpBands bands) {
        this.arrayCache = new SegmentConstantPoolArrayCache();
        this.bands = bands;
    }
    
    public ClassFileEntry getValue(final int cp, final long value) throws Pack200Exception {
        final int index = (int)value;
        if (index == -1) {
            return null;
        }
        if (index < 0) {
            throw new Pack200Exception("Cannot have a negative range");
        }
        if (cp == 1) {
            return this.bands.cpUTF8Value(index);
        }
        if (cp == 2) {
            return this.bands.cpIntegerValue(index);
        }
        if (cp == 3) {
            return this.bands.cpFloatValue(index);
        }
        if (cp == 4) {
            return this.bands.cpLongValue(index);
        }
        if (cp == 5) {
            return this.bands.cpDoubleValue(index);
        }
        if (cp == 6) {
            return this.bands.cpStringValue(index);
        }
        if (cp == 7) {
            return this.bands.cpClassValue(index);
        }
        if (cp == 8) {
            return this.bands.cpSignatureValue(index);
        }
        if (cp == 9) {
            return this.bands.cpNameAndTypeValue(index);
        }
        throw new Error("Tried to get a value I don't know about: " + cp);
    }
    
    public ConstantPoolEntry getClassSpecificPoolEntry(final int cp, final long desiredIndex, final String desiredClassName) throws Pack200Exception {
        final int index = (int)desiredIndex;
        int realIndex = -1;
        String[] array = null;
        if (cp == 10) {
            array = this.bands.getCpFieldClass();
        }
        else if (cp == 11) {
            array = this.bands.getCpMethodClass();
        }
        else {
            if (cp != 12) {
                throw new Error("Don't know how to handle " + cp);
            }
            array = this.bands.getCpIMethodClass();
        }
        realIndex = this.matchSpecificPoolEntryIndex(array, desiredClassName, index);
        return this.getConstantPoolEntry(cp, realIndex);
    }
    
    public ConstantPoolEntry getClassPoolEntry(final String name) {
        final String[] classes = this.bands.getCpClass();
        final int index = this.matchSpecificPoolEntryIndex(classes, name, 0);
        if (index == -1) {
            return null;
        }
        try {
            return this.getConstantPoolEntry(7, index);
        }
        catch (final Pack200Exception ex) {
            throw new Error("Error getting class pool entry");
        }
    }
    
    public ConstantPoolEntry getInitMethodPoolEntry(final int cp, final long value, final String desiredClassName) throws Pack200Exception {
        int realIndex = -1;
        final String desiredRegex = "^<init>.*";
        if (cp != 11) {
            throw new Error("Nothing but CP_METHOD can be an <init>");
        }
        realIndex = this.matchSpecificPoolEntryIndex(this.bands.getCpMethodClass(), this.bands.getCpMethodDescriptor(), desiredClassName, "^<init>.*", (int)value);
        return this.getConstantPoolEntry(cp, realIndex);
    }
    
    protected int matchSpecificPoolEntryIndex(final String[] nameArray, final String compareString, final int desiredIndex) {
        return this.matchSpecificPoolEntryIndex(nameArray, nameArray, compareString, ".*", desiredIndex);
    }
    
    protected int matchSpecificPoolEntryIndex(final String[] primaryArray, final String[] secondaryArray, final String primaryCompareString, final String secondaryCompareRegex, final int desiredIndex) {
        int instanceCount = -1;
        final List indexList = this.arrayCache.indexesForArrayKey(primaryArray, primaryCompareString);
        if (indexList.isEmpty()) {
            return -1;
        }
        for (int index = 0; index < indexList.size(); ++index) {
            final int arrayIndex = indexList.get(index);
            if (regexMatches(secondaryCompareRegex, secondaryArray[arrayIndex]) && ++instanceCount == desiredIndex) {
                return arrayIndex;
            }
        }
        return -1;
    }
    
    protected static boolean regexMatches(final String regexString, final String compareString) {
        if (".*".equals(regexString)) {
            return true;
        }
        if ("^<init>.*".equals(regexString)) {
            return compareString.length() >= "<init>".length() && "<init>".equals(compareString.substring(0, "<init>".length()));
        }
        throw new Error("regex trying to match a pattern I don't know: " + regexString);
    }
    
    public ConstantPoolEntry getConstantPoolEntry(final int cp, final long value) throws Pack200Exception {
        final int index = (int)value;
        if (index == -1) {
            return null;
        }
        if (index < 0) {
            throw new Pack200Exception("Cannot have a negative range");
        }
        if (cp == 1) {
            return this.bands.cpUTF8Value(index);
        }
        if (cp == 2) {
            return this.bands.cpIntegerValue(index);
        }
        if (cp == 3) {
            return this.bands.cpFloatValue(index);
        }
        if (cp == 4) {
            return this.bands.cpLongValue(index);
        }
        if (cp == 5) {
            return this.bands.cpDoubleValue(index);
        }
        if (cp == 6) {
            return this.bands.cpStringValue(index);
        }
        if (cp == 7) {
            return this.bands.cpClassValue(index);
        }
        if (cp == 8) {
            throw new Error("I don't know what to do with signatures yet");
        }
        if (cp == 9) {
            throw new Error("I don't know what to do with descriptors yet");
        }
        if (cp == 10) {
            return this.bands.cpFieldValue(index);
        }
        if (cp == 11) {
            return this.bands.cpMethodValue(index);
        }
        if (cp == 12) {
            return this.bands.cpIMethodValue(index);
        }
        throw new Error("Get value incomplete");
    }
}
