package org.apache.commons.compress.harmony.unpack200;

import org.apache.commons.compress.harmony.unpack200.bytecode.CPFieldRef;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPMethodRef;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPInterfaceMethodRef;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPNameAndType;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPDouble;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPClass;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPFloat;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPInteger;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPLong;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPString;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPUTF8;
import java.util.ArrayList;
import org.apache.commons.compress.harmony.pack200.Codec;
import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CpBands extends BandSet
{
    private final SegmentConstantPool pool;
    private String[] cpClass;
    private int[] cpClassInts;
    private int[] cpDescriptorNameInts;
    private int[] cpDescriptorTypeInts;
    private String[] cpDescriptor;
    private double[] cpDouble;
    private String[] cpFieldClass;
    private String[] cpFieldDescriptor;
    private int[] cpFieldClassInts;
    private int[] cpFieldDescriptorInts;
    private float[] cpFloat;
    private String[] cpIMethodClass;
    private String[] cpIMethodDescriptor;
    private int[] cpIMethodClassInts;
    private int[] cpIMethodDescriptorInts;
    private int[] cpInt;
    private long[] cpLong;
    private String[] cpMethodClass;
    private String[] cpMethodDescriptor;
    private int[] cpMethodClassInts;
    private int[] cpMethodDescriptorInts;
    private String[] cpSignature;
    private int[] cpSignatureInts;
    private String[] cpString;
    private int[] cpStringInts;
    private String[] cpUTF8;
    private final Map stringsToCPUTF8;
    private final Map stringsToCPStrings;
    private final Map longsToCPLongs;
    private final Map integersToCPIntegers;
    private final Map floatsToCPFloats;
    private final Map stringsToCPClass;
    private final Map doublesToCPDoubles;
    private final Map descriptorsToCPNameAndTypes;
    private Map mapClass;
    private Map mapDescriptor;
    private Map mapUTF8;
    private Map mapSignature;
    private int intOffset;
    private int floatOffset;
    private int longOffset;
    private int doubleOffset;
    private int stringOffset;
    private int classOffset;
    private int signatureOffset;
    private int descrOffset;
    private int fieldOffset;
    private int methodOffset;
    private int imethodOffset;
    
    public SegmentConstantPool getConstantPool() {
        return this.pool;
    }
    
    public CpBands(final Segment segment) {
        super(segment);
        this.pool = new SegmentConstantPool(this);
        this.stringsToCPUTF8 = new HashMap();
        this.stringsToCPStrings = new HashMap();
        this.longsToCPLongs = new HashMap();
        this.integersToCPIntegers = new HashMap();
        this.floatsToCPFloats = new HashMap();
        this.stringsToCPClass = new HashMap();
        this.doublesToCPDoubles = new HashMap();
        this.descriptorsToCPNameAndTypes = new HashMap();
    }
    
    @Override
    public void read(final InputStream in) throws IOException, Pack200Exception {
        this.parseCpUtf8(in);
        this.parseCpInt(in);
        this.parseCpFloat(in);
        this.parseCpLong(in);
        this.parseCpDouble(in);
        this.parseCpString(in);
        this.parseCpClass(in);
        this.parseCpSignature(in);
        this.parseCpDescriptor(in);
        this.parseCpField(in);
        this.parseCpMethod(in);
        this.parseCpIMethod(in);
        this.intOffset = this.cpUTF8.length;
        this.floatOffset = this.intOffset + this.cpInt.length;
        this.longOffset = this.floatOffset + this.cpFloat.length;
        this.doubleOffset = this.longOffset + this.cpLong.length;
        this.stringOffset = this.doubleOffset + this.cpDouble.length;
        this.classOffset = this.stringOffset + this.cpString.length;
        this.signatureOffset = this.classOffset + this.cpClass.length;
        this.descrOffset = this.signatureOffset + this.cpSignature.length;
        this.fieldOffset = this.descrOffset + this.cpDescriptor.length;
        this.methodOffset = this.fieldOffset + this.cpFieldClass.length;
        this.imethodOffset = this.methodOffset + this.cpMethodClass.length;
    }
    
    @Override
    public void unpack() {
    }
    
    private void parseCpClass(final InputStream in) throws IOException, Pack200Exception {
        final int cpClassCount = this.header.getCpClassCount();
        this.cpClassInts = this.decodeBandInt("cp_Class", in, Codec.UDELTA5, cpClassCount);
        this.cpClass = new String[cpClassCount];
        this.mapClass = new HashMap(cpClassCount);
        for (int i = 0; i < cpClassCount; ++i) {
            this.cpClass[i] = this.cpUTF8[this.cpClassInts[i]];
            this.mapClass.put(this.cpClass[i], i);
        }
    }
    
    private void parseCpDescriptor(final InputStream in) throws IOException, Pack200Exception {
        final int cpDescriptorCount = this.header.getCpDescriptorCount();
        this.cpDescriptorNameInts = this.decodeBandInt("cp_Descr_name", in, Codec.DELTA5, cpDescriptorCount);
        this.cpDescriptorTypeInts = this.decodeBandInt("cp_Descr_type", in, Codec.UDELTA5, cpDescriptorCount);
        final String[] cpDescriptorNames = this.getReferences(this.cpDescriptorNameInts, this.cpUTF8);
        final String[] cpDescriptorTypes = this.getReferences(this.cpDescriptorTypeInts, this.cpSignature);
        this.cpDescriptor = new String[cpDescriptorCount];
        this.mapDescriptor = new HashMap(cpDescriptorCount);
        for (int i = 0; i < cpDescriptorCount; ++i) {
            this.cpDescriptor[i] = cpDescriptorNames[i] + ":" + cpDescriptorTypes[i];
            this.mapDescriptor.put(this.cpDescriptor[i], i);
        }
    }
    
    private void parseCpDouble(final InputStream in) throws IOException, Pack200Exception {
        final int cpDoubleCount = this.header.getCpDoubleCount();
        final long[] band = this.parseFlags("cp_Double", in, cpDoubleCount, Codec.UDELTA5, Codec.DELTA5);
        this.cpDouble = new double[band.length];
        for (int i = 0; i < band.length; ++i) {
            this.cpDouble[i] = Double.longBitsToDouble(band[i]);
        }
    }
    
    private void parseCpField(final InputStream in) throws IOException, Pack200Exception {
        final int cpFieldCount = this.header.getCpFieldCount();
        this.cpFieldClassInts = this.decodeBandInt("cp_Field_class", in, Codec.DELTA5, cpFieldCount);
        this.cpFieldDescriptorInts = this.decodeBandInt("cp_Field_desc", in, Codec.UDELTA5, cpFieldCount);
        this.cpFieldClass = new String[cpFieldCount];
        this.cpFieldDescriptor = new String[cpFieldCount];
        for (int i = 0; i < cpFieldCount; ++i) {
            this.cpFieldClass[i] = this.cpClass[this.cpFieldClassInts[i]];
            this.cpFieldDescriptor[i] = this.cpDescriptor[this.cpFieldDescriptorInts[i]];
        }
    }
    
    private void parseCpFloat(final InputStream in) throws IOException, Pack200Exception {
        final int cpFloatCount = this.header.getCpFloatCount();
        this.cpFloat = new float[cpFloatCount];
        final int[] floatBits = this.decodeBandInt("cp_Float", in, Codec.UDELTA5, cpFloatCount);
        for (int i = 0; i < cpFloatCount; ++i) {
            this.cpFloat[i] = Float.intBitsToFloat(floatBits[i]);
        }
    }
    
    private void parseCpIMethod(final InputStream in) throws IOException, Pack200Exception {
        final int cpIMethodCount = this.header.getCpIMethodCount();
        this.cpIMethodClassInts = this.decodeBandInt("cp_Imethod_class", in, Codec.DELTA5, cpIMethodCount);
        this.cpIMethodDescriptorInts = this.decodeBandInt("cp_Imethod_desc", in, Codec.UDELTA5, cpIMethodCount);
        this.cpIMethodClass = new String[cpIMethodCount];
        this.cpIMethodDescriptor = new String[cpIMethodCount];
        for (int i = 0; i < cpIMethodCount; ++i) {
            this.cpIMethodClass[i] = this.cpClass[this.cpIMethodClassInts[i]];
            this.cpIMethodDescriptor[i] = this.cpDescriptor[this.cpIMethodDescriptorInts[i]];
        }
    }
    
    private void parseCpInt(final InputStream in) throws IOException, Pack200Exception {
        final int cpIntCount = this.header.getCpIntCount();
        this.cpInt = this.decodeBandInt("cpInt", in, Codec.UDELTA5, cpIntCount);
    }
    
    private void parseCpLong(final InputStream in) throws IOException, Pack200Exception {
        final int cpLongCount = this.header.getCpLongCount();
        this.cpLong = this.parseFlags("cp_Long", in, cpLongCount, Codec.UDELTA5, Codec.DELTA5);
    }
    
    private void parseCpMethod(final InputStream in) throws IOException, Pack200Exception {
        final int cpMethodCount = this.header.getCpMethodCount();
        this.cpMethodClassInts = this.decodeBandInt("cp_Method_class", in, Codec.DELTA5, cpMethodCount);
        this.cpMethodDescriptorInts = this.decodeBandInt("cp_Method_desc", in, Codec.UDELTA5, cpMethodCount);
        this.cpMethodClass = new String[cpMethodCount];
        this.cpMethodDescriptor = new String[cpMethodCount];
        for (int i = 0; i < cpMethodCount; ++i) {
            this.cpMethodClass[i] = this.cpClass[this.cpMethodClassInts[i]];
            this.cpMethodDescriptor[i] = this.cpDescriptor[this.cpMethodDescriptorInts[i]];
        }
    }
    
    private void parseCpSignature(final InputStream in) throws IOException, Pack200Exception {
        final int cpSignatureCount = this.header.getCpSignatureCount();
        this.cpSignatureInts = this.decodeBandInt("cp_Signature_form", in, Codec.DELTA5, cpSignatureCount);
        final String[] cpSignatureForm = this.getReferences(this.cpSignatureInts, this.cpUTF8);
        this.cpSignature = new String[cpSignatureCount];
        this.mapSignature = new HashMap();
        int lCount = 0;
        for (int i = 0; i < cpSignatureCount; ++i) {
            final String form = cpSignatureForm[i];
            final char[] chars = form.toCharArray();
            for (int j = 0; j < chars.length; ++j) {
                if (chars[j] == 'L') {
                    this.cpSignatureInts[i] = -1;
                    ++lCount;
                }
            }
        }
        final String[] cpSignatureClasses = this.parseReferences("cp_Signature_classes", in, Codec.UDELTA5, lCount, this.cpClass);
        int index = 0;
        for (int k = 0; k < cpSignatureCount; ++k) {
            final String form2 = cpSignatureForm[k];
            final int len = form2.length();
            final StringBuffer signature = new StringBuffer(64);
            final ArrayList list = new ArrayList();
            for (int l = 0; l < len; ++l) {
                final char c = form2.charAt(l);
                signature.append(c);
                if (c == 'L') {
                    final String className = cpSignatureClasses[index];
                    list.add(className);
                    signature.append(className);
                    ++index;
                }
            }
            this.cpSignature[k] = signature.toString();
            this.mapSignature.put(signature.toString(), k);
        }
    }
    
    private void parseCpString(final InputStream in) throws IOException, Pack200Exception {
        final int cpStringCount = this.header.getCpStringCount();
        this.cpStringInts = this.decodeBandInt("cp_String", in, Codec.UDELTA5, cpStringCount);
        this.cpString = new String[cpStringCount];
        for (int i = 0; i < cpStringCount; ++i) {
            this.cpString[i] = this.cpUTF8[this.cpStringInts[i]];
        }
    }
    
    private void parseCpUtf8(final InputStream in) throws IOException, Pack200Exception {
        final int cpUTF8Count = this.header.getCpUTF8Count();
        this.cpUTF8 = new String[cpUTF8Count];
        this.mapUTF8 = new HashMap(cpUTF8Count + 1);
        this.cpUTF8[0] = "";
        this.mapUTF8.put("", 0);
        final int[] prefix = this.decodeBandInt("cpUTF8Prefix", in, Codec.DELTA5, cpUTF8Count - 2);
        int charCount = 0;
        int bigSuffixCount = 0;
        final int[] suffix = this.decodeBandInt("cpUTF8Suffix", in, Codec.UNSIGNED5, cpUTF8Count - 1);
        for (int i = 0; i < suffix.length; ++i) {
            if (suffix[i] == 0) {
                ++bigSuffixCount;
            }
            else {
                charCount += suffix[i];
            }
        }
        final char[] data = new char[charCount];
        final int[] dataBand = this.decodeBandInt("cp_Utf8_chars", in, Codec.CHAR3, charCount);
        for (int j = 0; j < data.length; ++j) {
            data[j] = (char)dataBand[j];
        }
        final int[] bigSuffixCounts = this.decodeBandInt("cp_Utf8_big_suffix", in, Codec.DELTA5, bigSuffixCount);
        final int[][] bigSuffixDataBand = new int[bigSuffixCount][];
        for (int k = 0; k < bigSuffixDataBand.length; ++k) {
            bigSuffixDataBand[k] = this.decodeBandInt("cp_Utf8_big_chars " + k, in, Codec.DELTA5, bigSuffixCounts[k]);
        }
        final char[][] bigSuffixData = new char[bigSuffixCount][];
        for (int l = 0; l < bigSuffixDataBand.length; ++l) {
            bigSuffixData[l] = new char[bigSuffixDataBand[l].length];
            for (int m = 0; m < bigSuffixDataBand[l].length; ++m) {
                bigSuffixData[l][m] = (char)bigSuffixDataBand[l][m];
            }
        }
        charCount = 0;
        bigSuffixCount = 0;
        for (int l = 1; l < cpUTF8Count; ++l) {
            final String lastString = this.cpUTF8[l - 1];
            if (suffix[l - 1] == 0) {
                this.cpUTF8[l] = lastString.substring(0, (l > 1) ? prefix[l - 2] : 0) + new String(bigSuffixData[bigSuffixCount++]);
                this.mapUTF8.put(this.cpUTF8[l], l);
            }
            else {
                this.cpUTF8[l] = lastString.substring(0, (l > 1) ? prefix[l - 2] : 0) + new String(data, charCount, suffix[l - 1]);
                charCount += suffix[l - 1];
                this.mapUTF8.put(this.cpUTF8[l], l);
            }
        }
    }
    
    public String[] getCpClass() {
        return this.cpClass;
    }
    
    public String[] getCpDescriptor() {
        return this.cpDescriptor;
    }
    
    public String[] getCpFieldClass() {
        return this.cpFieldClass;
    }
    
    public String[] getCpIMethodClass() {
        return this.cpIMethodClass;
    }
    
    public int[] getCpInt() {
        return this.cpInt;
    }
    
    public long[] getCpLong() {
        return this.cpLong;
    }
    
    public String[] getCpMethodClass() {
        return this.cpMethodClass;
    }
    
    public String[] getCpMethodDescriptor() {
        return this.cpMethodDescriptor;
    }
    
    public String[] getCpSignature() {
        return this.cpSignature;
    }
    
    public String[] getCpUTF8() {
        return this.cpUTF8;
    }
    
    public CPUTF8 cpUTF8Value(final int index) {
        final String string = this.cpUTF8[index];
        CPUTF8 cputf8 = this.stringsToCPUTF8.get(string);
        if (cputf8 == null) {
            cputf8 = new CPUTF8(string, index);
            this.stringsToCPUTF8.put(string, cputf8);
        }
        else if (cputf8.getGlobalIndex() > index) {
            cputf8.setGlobalIndex(index);
        }
        return cputf8;
    }
    
    public CPUTF8 cpUTF8Value(final String string) {
        return this.cpUTF8Value(string, true);
    }
    
    public CPUTF8 cpUTF8Value(final String string, final boolean searchForIndex) {
        CPUTF8 cputf8 = this.stringsToCPUTF8.get(string);
        if (cputf8 == null) {
            Integer index = null;
            if (searchForIndex) {
                index = this.mapUTF8.get(string);
            }
            if (index != null) {
                return this.cpUTF8Value(index);
            }
            if (searchForIndex) {
                index = this.mapSignature.get(string);
            }
            if (index != null) {
                return this.cpSignatureValue(index);
            }
            cputf8 = new CPUTF8(string, -1);
            this.stringsToCPUTF8.put(string, cputf8);
        }
        return cputf8;
    }
    
    public CPString cpStringValue(final int index) {
        final String string = this.cpString[index];
        final int utf8Index = this.cpStringInts[index];
        final int globalIndex = this.stringOffset + index;
        CPString cpString = this.stringsToCPStrings.get(string);
        if (cpString == null) {
            cpString = new CPString(this.cpUTF8Value(utf8Index), globalIndex);
            this.stringsToCPStrings.put(string, cpString);
        }
        return cpString;
    }
    
    public CPLong cpLongValue(final int index) {
        final Long l = this.cpLong[index];
        CPLong cpLong = this.longsToCPLongs.get(l);
        if (cpLong == null) {
            cpLong = new CPLong(l, index + this.longOffset);
            this.longsToCPLongs.put(l, cpLong);
        }
        return cpLong;
    }
    
    public CPInteger cpIntegerValue(final int index) {
        final Integer i = this.cpInt[index];
        CPInteger cpInteger = this.integersToCPIntegers.get(i);
        if (cpInteger == null) {
            cpInteger = new CPInteger(i, index + this.intOffset);
            this.integersToCPIntegers.put(i, cpInteger);
        }
        return cpInteger;
    }
    
    public CPFloat cpFloatValue(final int index) {
        final Float f = this.cpFloat[index];
        CPFloat cpFloat = this.floatsToCPFloats.get(f);
        if (cpFloat == null) {
            cpFloat = new CPFloat(f, index + this.floatOffset);
            this.floatsToCPFloats.put(f, cpFloat);
        }
        return cpFloat;
    }
    
    public CPClass cpClassValue(final int index) {
        final String string = this.cpClass[index];
        final int utf8Index = this.cpClassInts[index];
        final int globalIndex = this.classOffset + index;
        CPClass cpString = this.stringsToCPClass.get(string);
        if (cpString == null) {
            cpString = new CPClass(this.cpUTF8Value(utf8Index), globalIndex);
            this.stringsToCPClass.put(string, cpString);
        }
        return cpString;
    }
    
    public CPClass cpClassValue(final String string) {
        CPClass cpString = this.stringsToCPClass.get(string);
        if (cpString == null) {
            final Integer index = this.mapClass.get(string);
            if (index != null) {
                return this.cpClassValue(index);
            }
            cpString = new CPClass(this.cpUTF8Value(string, false), -1);
            this.stringsToCPClass.put(string, cpString);
        }
        return cpString;
    }
    
    public CPDouble cpDoubleValue(final int index) {
        final Double dbl = this.cpDouble[index];
        CPDouble cpDouble = this.doublesToCPDoubles.get(dbl);
        if (cpDouble == null) {
            cpDouble = new CPDouble(dbl, index + this.doubleOffset);
            this.doublesToCPDoubles.put(dbl, cpDouble);
        }
        return cpDouble;
    }
    
    public CPNameAndType cpNameAndTypeValue(final int index) {
        final String descriptor = this.cpDescriptor[index];
        CPNameAndType cpNameAndType = this.descriptorsToCPNameAndTypes.get(descriptor);
        if (cpNameAndType == null) {
            final int nameIndex = this.cpDescriptorNameInts[index];
            final int descriptorIndex = this.cpDescriptorTypeInts[index];
            final CPUTF8 name = this.cpUTF8Value(nameIndex);
            final CPUTF8 descriptorU = this.cpSignatureValue(descriptorIndex);
            cpNameAndType = new CPNameAndType(name, descriptorU, index + this.descrOffset);
            this.descriptorsToCPNameAndTypes.put(descriptor, cpNameAndType);
        }
        return cpNameAndType;
    }
    
    public CPInterfaceMethodRef cpIMethodValue(final int index) {
        return new CPInterfaceMethodRef(this.cpClassValue(this.cpIMethodClassInts[index]), this.cpNameAndTypeValue(this.cpIMethodDescriptorInts[index]), index + this.imethodOffset);
    }
    
    public CPMethodRef cpMethodValue(final int index) {
        return new CPMethodRef(this.cpClassValue(this.cpMethodClassInts[index]), this.cpNameAndTypeValue(this.cpMethodDescriptorInts[index]), index + this.methodOffset);
    }
    
    public CPFieldRef cpFieldValue(final int index) {
        return new CPFieldRef(this.cpClassValue(this.cpFieldClassInts[index]), this.cpNameAndTypeValue(this.cpFieldDescriptorInts[index]), index + this.fieldOffset);
    }
    
    public CPUTF8 cpSignatureValue(final int index) {
        int globalIndex;
        if (this.cpSignatureInts[index] != -1) {
            globalIndex = this.cpSignatureInts[index];
        }
        else {
            globalIndex = index + this.signatureOffset;
        }
        final String string = this.cpSignature[index];
        CPUTF8 cpUTF8 = this.stringsToCPUTF8.get(string);
        if (cpUTF8 == null) {
            cpUTF8 = new CPUTF8(string, globalIndex);
            this.stringsToCPUTF8.put(string, cpUTF8);
        }
        return cpUTF8;
    }
    
    public CPNameAndType cpNameAndTypeValue(final String descriptor) {
        CPNameAndType cpNameAndType = this.descriptorsToCPNameAndTypes.get(descriptor);
        if (cpNameAndType == null) {
            final Integer index = this.mapDescriptor.get(descriptor);
            if (index != null) {
                return this.cpNameAndTypeValue(index);
            }
            final int colon = descriptor.indexOf(58);
            final String nameString = descriptor.substring(0, colon);
            final String descriptorString = descriptor.substring(colon + 1);
            final CPUTF8 name = this.cpUTF8Value(nameString, true);
            final CPUTF8 descriptorU = this.cpUTF8Value(descriptorString, true);
            cpNameAndType = new CPNameAndType(name, descriptorU, -1 + this.descrOffset);
            this.descriptorsToCPNameAndTypes.put(descriptor, cpNameAndType);
        }
        return cpNameAndType;
    }
    
    public int[] getCpDescriptorNameInts() {
        return this.cpDescriptorNameInts;
    }
    
    public int[] getCpDescriptorTypeInts() {
        return this.cpDescriptorTypeInts;
    }
}
