package org.apache.commons.compress.harmony.unpack200;

import org.apache.commons.compress.harmony.unpack200.bytecode.Attribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.LocalVariableTypeTableAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.LocalVariableTableAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.LineNumberTableAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPNameAndType;
import org.apache.commons.compress.harmony.unpack200.bytecode.EnclosingMethodAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.SourceFileAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.ExceptionsAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPClass;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFileEntry;
import org.apache.commons.compress.harmony.unpack200.bytecode.SignatureAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPUTF8;
import org.apache.commons.compress.harmony.unpack200.bytecode.ConstantValueAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.DeprecatedAttribute;
import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import java.io.IOException;
import org.apache.commons.compress.harmony.pack200.Codec;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

public class ClassBands extends BandSet
{
    private int[] classFieldCount;
    private long[] classFlags;
    private long[] classAccessFlags;
    private int[][] classInterfacesInts;
    private int[] classMethodCount;
    private int[] classSuperInts;
    private String[] classThis;
    private int[] classThisInts;
    private ArrayList[] classAttributes;
    private int[] classVersionMajor;
    private int[] classVersionMinor;
    private IcTuple[][] icLocal;
    private List[] codeAttributes;
    private int[] codeHandlerCount;
    private int[] codeMaxNALocals;
    private int[] codeMaxStack;
    private ArrayList[][] fieldAttributes;
    private String[][] fieldDescr;
    private int[][] fieldDescrInts;
    private long[][] fieldFlags;
    private long[][] fieldAccessFlags;
    private ArrayList[][] methodAttributes;
    private String[][] methodDescr;
    private int[][] methodDescrInts;
    private long[][] methodFlags;
    private long[][] methodAccessFlags;
    private final AttributeLayoutMap attrMap;
    private final CpBands cpBands;
    private final SegmentOptions options;
    private final int classCount;
    private int[] methodAttrCalls;
    private int[][] codeHandlerStartP;
    private int[][] codeHandlerEndPO;
    private int[][] codeHandlerCatchPO;
    private int[][] codeHandlerClassRCN;
    private boolean[] codeHasAttributes;
    
    public ClassBands(final Segment segment) {
        super(segment);
        this.attrMap = segment.getAttrDefinitionBands().getAttributeDefinitionMap();
        this.cpBands = segment.getCpBands();
        this.classCount = this.header.getClassCount();
        this.options = this.header.getOptions();
    }
    
    @Override
    public void read(final InputStream in) throws IOException, Pack200Exception {
        final int classCount = this.header.getClassCount();
        this.classThisInts = this.decodeBandInt("class_this", in, Codec.DELTA5, classCount);
        this.classThis = this.getReferences(this.classThisInts, this.cpBands.getCpClass());
        this.classSuperInts = this.decodeBandInt("class_super", in, Codec.DELTA5, classCount);
        final int[] classInterfaceLengths = this.decodeBandInt("class_interface_count", in, Codec.DELTA5, classCount);
        this.classInterfacesInts = this.decodeBandInt("class_interface", in, Codec.DELTA5, classInterfaceLengths);
        this.classFieldCount = this.decodeBandInt("class_field_count", in, Codec.DELTA5, classCount);
        this.classMethodCount = this.decodeBandInt("class_method_count", in, Codec.DELTA5, classCount);
        this.parseFieldBands(in);
        this.parseMethodBands(in);
        this.parseClassAttrBands(in);
        this.parseCodeBands(in);
    }
    
    @Override
    public void unpack() {
    }
    
    private void parseFieldBands(final InputStream in) throws IOException, Pack200Exception {
        this.fieldDescrInts = this.decodeBandInt("field_descr", in, Codec.DELTA5, this.classFieldCount);
        this.fieldDescr = this.getReferences(this.fieldDescrInts, this.cpBands.getCpDescriptor());
        this.parseFieldAttrBands(in);
    }
    
    private void parseFieldAttrBands(final InputStream in) throws IOException, Pack200Exception {
        this.fieldFlags = this.parseFlags("field_flags", in, this.classFieldCount, Codec.UNSIGNED5, this.options.hasFieldFlagsHi());
        final int fieldAttrCount = SegmentUtils.countBit16(this.fieldFlags);
        final int[] fieldAttrCounts = this.decodeBandInt("field_attr_count", in, Codec.UNSIGNED5, fieldAttrCount);
        final int[][] fieldAttrIndexes = this.decodeBandInt("field_attr_indexes", in, Codec.UNSIGNED5, fieldAttrCounts);
        final int callCount = this.getCallCount(fieldAttrIndexes, this.fieldFlags, 1);
        final int[] fieldAttrCalls = this.decodeBandInt("field_attr_calls", in, Codec.UNSIGNED5, callCount);
        this.fieldAttributes = new ArrayList[this.classCount][];
        for (int i = 0; i < this.classCount; ++i) {
            this.fieldAttributes[i] = new ArrayList[this.fieldFlags[i].length];
            for (int j = 0; j < this.fieldFlags[i].length; ++j) {
                this.fieldAttributes[i][j] = new ArrayList();
            }
        }
        final AttributeLayout constantValueLayout = this.attrMap.getAttributeLayout("ConstantValue", 1);
        final int constantCount = SegmentUtils.countMatches(this.fieldFlags, constantValueLayout);
        final int[] field_constantValue_KQ = this.decodeBandInt("field_ConstantValue_KQ", in, Codec.UNSIGNED5, constantCount);
        int constantValueIndex = 0;
        final AttributeLayout signatureLayout = this.attrMap.getAttributeLayout("Signature", 1);
        final int signatureCount = SegmentUtils.countMatches(this.fieldFlags, signatureLayout);
        final int[] fieldSignatureRS = this.decodeBandInt("field_Signature_RS", in, Codec.UNSIGNED5, signatureCount);
        int signatureIndex = 0;
        final AttributeLayout deprecatedLayout = this.attrMap.getAttributeLayout("Deprecated", 1);
        for (int k = 0; k < this.classCount; ++k) {
            for (int l = 0; l < this.fieldFlags[k].length; ++l) {
                final long flag = this.fieldFlags[k][l];
                if (deprecatedLayout.matches(flag)) {
                    this.fieldAttributes[k][l].add(new DeprecatedAttribute());
                }
                if (constantValueLayout.matches(flag)) {
                    final long result = field_constantValue_KQ[constantValueIndex];
                    final String desc = this.fieldDescr[k][l];
                    final int colon = desc.indexOf(58);
                    String type = desc.substring(colon + 1);
                    if (type.equals("B") || type.equals("S") || type.equals("C") || type.equals("Z")) {
                        type = "I";
                    }
                    final ClassFileEntry value = constantValueLayout.getValue(result, type, this.cpBands.getConstantPool());
                    this.fieldAttributes[k][l].add(new ConstantValueAttribute(value));
                    ++constantValueIndex;
                }
                if (signatureLayout.matches(flag)) {
                    final long result = fieldSignatureRS[signatureIndex];
                    final String desc = this.fieldDescr[k][l];
                    final int colon = desc.indexOf(58);
                    final String type = desc.substring(colon + 1);
                    final CPUTF8 value2 = (CPUTF8)signatureLayout.getValue(result, type, this.cpBands.getConstantPool());
                    this.fieldAttributes[k][l].add(new SignatureAttribute(value2));
                    ++signatureIndex;
                }
            }
        }
        int backwardsCallIndex;
        final int backwardsCallsUsed = backwardsCallIndex = this.parseFieldMetadataBands(in, fieldAttrCalls);
        final int limit = this.options.hasFieldFlagsHi() ? 62 : 31;
        final AttributeLayout[] otherLayouts = new AttributeLayout[limit + 1];
        final int[] counts = new int[limit + 1];
        final List[] otherAttributes = new List[limit + 1];
        for (int m = 0; m < limit; ++m) {
            final AttributeLayout layout = this.attrMap.getAttributeLayout(m, 1);
            if (layout != null && !layout.isDefaultLayout()) {
                otherLayouts[m] = layout;
                counts[m] = SegmentUtils.countMatches(this.fieldFlags, layout);
            }
        }
        for (int m = 0; m < counts.length; ++m) {
            if (counts[m] > 0) {
                final NewAttributeBands bands = this.attrMap.getAttributeBands(otherLayouts[m]);
                otherAttributes[m] = bands.parseAttributes(in, counts[m]);
                final int numBackwardsCallables = otherLayouts[m].numBackwardsCallables();
                if (numBackwardsCallables > 0) {
                    final int[] backwardsCalls = new int[numBackwardsCallables];
                    System.arraycopy(fieldAttrCalls, backwardsCallIndex, backwardsCalls, 0, numBackwardsCallables);
                    bands.setBackwardsCalls(backwardsCalls);
                    backwardsCallIndex += numBackwardsCallables;
                }
            }
        }
        for (int m = 0; m < this.classCount; ++m) {
            for (int j2 = 0; j2 < this.fieldFlags[m].length; ++j2) {
                final long flag2 = this.fieldFlags[m][j2];
                int othersAddedAtStart = 0;
                for (int k2 = 0; k2 < otherLayouts.length; ++k2) {
                    if (otherLayouts[k2] != null && otherLayouts[k2].matches(flag2)) {
                        if (otherLayouts[k2].getIndex() < 15) {
                            this.fieldAttributes[m][j2].add(othersAddedAtStart++, otherAttributes[k2].get(0));
                        }
                        else {
                            this.fieldAttributes[m][j2].add(otherAttributes[k2].get(0));
                        }
                        otherAttributes[k2].remove(0);
                    }
                }
            }
        }
    }
    
    private void parseMethodBands(final InputStream in) throws IOException, Pack200Exception {
        this.methodDescrInts = this.decodeBandInt("method_descr", in, Codec.MDELTA5, this.classMethodCount);
        this.methodDescr = this.getReferences(this.methodDescrInts, this.cpBands.getCpDescriptor());
        this.parseMethodAttrBands(in);
    }
    
    private void parseMethodAttrBands(final InputStream in) throws IOException, Pack200Exception {
        this.methodFlags = this.parseFlags("method_flags", in, this.classMethodCount, Codec.UNSIGNED5, this.options.hasMethodFlagsHi());
        final int methodAttrCount = SegmentUtils.countBit16(this.methodFlags);
        final int[] methodAttrCounts = this.decodeBandInt("method_attr_count", in, Codec.UNSIGNED5, methodAttrCount);
        final int[][] methodAttrIndexes = this.decodeBandInt("method_attr_indexes", in, Codec.UNSIGNED5, methodAttrCounts);
        final int callCount = this.getCallCount(methodAttrIndexes, this.methodFlags, 2);
        this.methodAttrCalls = this.decodeBandInt("method_attr_calls", in, Codec.UNSIGNED5, callCount);
        this.methodAttributes = new ArrayList[this.classCount][];
        for (int i = 0; i < this.classCount; ++i) {
            this.methodAttributes[i] = new ArrayList[this.methodFlags[i].length];
            for (int j = 0; j < this.methodFlags[i].length; ++j) {
                this.methodAttributes[i][j] = new ArrayList();
            }
        }
        final AttributeLayout methodExceptionsLayout = this.attrMap.getAttributeLayout("Exceptions", 2);
        final int count = SegmentUtils.countMatches(this.methodFlags, methodExceptionsLayout);
        final int[] numExceptions = this.decodeBandInt("method_Exceptions_n", in, Codec.UNSIGNED5, count);
        final int[][] methodExceptionsRS = this.decodeBandInt("method_Exceptions_RC", in, Codec.UNSIGNED5, numExceptions);
        final AttributeLayout methodSignatureLayout = this.attrMap.getAttributeLayout("Signature", 2);
        final int count2 = SegmentUtils.countMatches(this.methodFlags, methodSignatureLayout);
        final int[] methodSignatureRS = this.decodeBandInt("method_signature_RS", in, Codec.UNSIGNED5, count2);
        final AttributeLayout deprecatedLayout = this.attrMap.getAttributeLayout("Deprecated", 2);
        int methodExceptionsIndex = 0;
        int methodSignatureIndex = 0;
        for (int k = 0; k < this.methodAttributes.length; ++k) {
            for (int l = 0; l < this.methodAttributes[k].length; ++l) {
                final long flag = this.methodFlags[k][l];
                if (methodExceptionsLayout.matches(flag)) {
                    final int n = numExceptions[methodExceptionsIndex];
                    final int[] exceptions = methodExceptionsRS[methodExceptionsIndex];
                    final CPClass[] exceptionClasses = new CPClass[n];
                    for (int m = 0; m < n; ++m) {
                        exceptionClasses[m] = this.cpBands.cpClassValue(exceptions[m]);
                    }
                    this.methodAttributes[k][l].add(new ExceptionsAttribute(exceptionClasses));
                    ++methodExceptionsIndex;
                }
                if (methodSignatureLayout.matches(flag)) {
                    final long result = methodSignatureRS[methodSignatureIndex];
                    final String desc = this.methodDescr[k][l];
                    final int colon = desc.indexOf(58);
                    String type = desc.substring(colon + 1);
                    if (type.equals("B") || type.equals("H")) {
                        type = "I";
                    }
                    final CPUTF8 value = (CPUTF8)methodSignatureLayout.getValue(result, type, this.cpBands.getConstantPool());
                    this.methodAttributes[k][l].add(new SignatureAttribute(value));
                    ++methodSignatureIndex;
                }
                if (deprecatedLayout.matches(flag)) {
                    this.methodAttributes[k][l].add(new DeprecatedAttribute());
                }
            }
        }
        int backwardsCallIndex;
        final int backwardsCallsUsed = backwardsCallIndex = this.parseMethodMetadataBands(in, this.methodAttrCalls);
        final int limit = this.options.hasMethodFlagsHi() ? 62 : 31;
        final AttributeLayout[] otherLayouts = new AttributeLayout[limit + 1];
        final int[] counts = new int[limit + 1];
        final List[] otherAttributes = new List[limit + 1];
        for (int i2 = 0; i2 < limit; ++i2) {
            final AttributeLayout layout = this.attrMap.getAttributeLayout(i2, 2);
            if (layout != null && !layout.isDefaultLayout()) {
                otherLayouts[i2] = layout;
                counts[i2] = SegmentUtils.countMatches(this.methodFlags, layout);
            }
        }
        for (int i2 = 0; i2 < counts.length; ++i2) {
            if (counts[i2] > 0) {
                final NewAttributeBands bands = this.attrMap.getAttributeBands(otherLayouts[i2]);
                otherAttributes[i2] = bands.parseAttributes(in, counts[i2]);
                final int numBackwardsCallables = otherLayouts[i2].numBackwardsCallables();
                if (numBackwardsCallables > 0) {
                    final int[] backwardsCalls = new int[numBackwardsCallables];
                    System.arraycopy(this.methodAttrCalls, backwardsCallIndex, backwardsCalls, 0, numBackwardsCallables);
                    bands.setBackwardsCalls(backwardsCalls);
                    backwardsCallIndex += numBackwardsCallables;
                }
            }
        }
        for (int i2 = 0; i2 < this.methodAttributes.length; ++i2) {
            for (int j2 = 0; j2 < this.methodAttributes[i2].length; ++j2) {
                final long flag2 = this.methodFlags[i2][j2];
                int othersAddedAtStart = 0;
                for (int k2 = 0; k2 < otherLayouts.length; ++k2) {
                    if (otherLayouts[k2] != null && otherLayouts[k2].matches(flag2)) {
                        if (otherLayouts[k2].getIndex() < 15) {
                            this.methodAttributes[i2][j2].add(othersAddedAtStart++, otherAttributes[k2].get(0));
                        }
                        else {
                            this.methodAttributes[i2][j2].add(otherAttributes[k2].get(0));
                        }
                        otherAttributes[k2].remove(0);
                    }
                }
            }
        }
    }
    
    private int getCallCount(final int[][] methodAttrIndexes, final long[][] flags, final int context) throws Pack200Exception {
        int callCount = 0;
        for (int i = 0; i < methodAttrIndexes.length; ++i) {
            for (int j = 0; j < methodAttrIndexes[i].length; ++j) {
                final int index = methodAttrIndexes[i][j];
                final AttributeLayout layout = this.attrMap.getAttributeLayout(index, context);
                callCount += layout.numBackwardsCallables();
            }
        }
        int layoutsUsed = 0;
        for (int k = 0; k < flags.length; ++k) {
            for (int l = 0; l < flags[k].length; ++l) {
                layoutsUsed = (int)((long)layoutsUsed | flags[k][l]);
            }
        }
        for (int k = 0; k < 26; ++k) {
            if ((layoutsUsed & 1 << k) != 0x0) {
                final AttributeLayout layout2 = this.attrMap.getAttributeLayout(k, context);
                callCount += layout2.numBackwardsCallables();
            }
        }
        return callCount;
    }
    
    private void parseClassAttrBands(final InputStream in) throws IOException, Pack200Exception {
        final String[] cpUTF8 = this.cpBands.getCpUTF8();
        final String[] cpClass = this.cpBands.getCpClass();
        this.classAttributes = new ArrayList[this.classCount];
        for (int i = 0; i < this.classCount; ++i) {
            this.classAttributes[i] = new ArrayList();
        }
        this.classFlags = this.parseFlags("class_flags", in, this.classCount, Codec.UNSIGNED5, this.options.hasClassFlagsHi());
        final int classAttrCount = SegmentUtils.countBit16(this.classFlags);
        final int[] classAttrCounts = this.decodeBandInt("class_attr_count", in, Codec.UNSIGNED5, classAttrCount);
        final int[][] classAttrIndexes = this.decodeBandInt("class_attr_indexes", in, Codec.UNSIGNED5, classAttrCounts);
        final int callCount = this.getCallCount(classAttrIndexes, new long[][] { this.classFlags }, 0);
        final int[] classAttrCalls = this.decodeBandInt("class_attr_calls", in, Codec.UNSIGNED5, callCount);
        final AttributeLayout deprecatedLayout = this.attrMap.getAttributeLayout("Deprecated", 0);
        final AttributeLayout sourceFileLayout = this.attrMap.getAttributeLayout("SourceFile", 0);
        final int sourceFileCount = SegmentUtils.countMatches(this.classFlags, sourceFileLayout);
        final int[] classSourceFile = this.decodeBandInt("class_SourceFile_RUN", in, Codec.UNSIGNED5, sourceFileCount);
        final AttributeLayout enclosingMethodLayout = this.attrMap.getAttributeLayout("EnclosingMethod", 0);
        final int enclosingMethodCount = SegmentUtils.countMatches(this.classFlags, enclosingMethodLayout);
        final int[] enclosingMethodRC = this.decodeBandInt("class_EnclosingMethod_RC", in, Codec.UNSIGNED5, enclosingMethodCount);
        final int[] enclosingMethodRDN = this.decodeBandInt("class_EnclosingMethod_RDN", in, Codec.UNSIGNED5, enclosingMethodCount);
        final AttributeLayout signatureLayout = this.attrMap.getAttributeLayout("Signature", 0);
        final int signatureCount = SegmentUtils.countMatches(this.classFlags, signatureLayout);
        final int[] classSignature = this.decodeBandInt("class_Signature_RS", in, Codec.UNSIGNED5, signatureCount);
        final int backwardsCallsUsed = this.parseClassMetadataBands(in, classAttrCalls);
        final AttributeLayout innerClassLayout = this.attrMap.getAttributeLayout("InnerClasses", 0);
        final int innerClassCount = SegmentUtils.countMatches(this.classFlags, innerClassLayout);
        final int[] classInnerClassesN = this.decodeBandInt("class_InnerClasses_N", in, Codec.UNSIGNED5, innerClassCount);
        final int[][] classInnerClassesRC = this.decodeBandInt("class_InnerClasses_RC", in, Codec.UNSIGNED5, classInnerClassesN);
        final int[][] classInnerClassesF = this.decodeBandInt("class_InnerClasses_F", in, Codec.UNSIGNED5, classInnerClassesN);
        int flagsCount = 0;
        for (int j = 0; j < classInnerClassesF.length; ++j) {
            for (int k = 0; k < classInnerClassesF[j].length; ++k) {
                if (classInnerClassesF[j][k] != 0) {
                    ++flagsCount;
                }
            }
        }
        final int[] classInnerClassesOuterRCN = this.decodeBandInt("class_InnerClasses_outer_RCN", in, Codec.UNSIGNED5, flagsCount);
        final int[] classInnerClassesNameRUN = this.decodeBandInt("class_InnerClasses_name_RUN", in, Codec.UNSIGNED5, flagsCount);
        final AttributeLayout versionLayout = this.attrMap.getAttributeLayout("class-file version", 0);
        final int versionCount = SegmentUtils.countMatches(this.classFlags, versionLayout);
        final int[] classFileVersionMinorH = this.decodeBandInt("class_file_version_minor_H", in, Codec.UNSIGNED5, versionCount);
        final int[] classFileVersionMajorH = this.decodeBandInt("class_file_version_major_H", in, Codec.UNSIGNED5, versionCount);
        if (versionCount > 0) {
            this.classVersionMajor = new int[this.classCount];
            this.classVersionMinor = new int[this.classCount];
        }
        final int defaultVersionMajor = this.header.getDefaultClassMajorVersion();
        final int defaultVersionMinor = this.header.getDefaultClassMinorVersion();
        int backwardsCallIndex = backwardsCallsUsed;
        final int limit = this.options.hasClassFlagsHi() ? 62 : 31;
        final AttributeLayout[] otherLayouts = new AttributeLayout[limit + 1];
        final int[] counts = new int[limit + 1];
        final List[] otherAttributes = new List[limit + 1];
        for (int l = 0; l < limit; ++l) {
            final AttributeLayout layout = this.attrMap.getAttributeLayout(l, 0);
            if (layout != null && !layout.isDefaultLayout()) {
                otherLayouts[l] = layout;
                counts[l] = SegmentUtils.countMatches(this.classFlags, layout);
            }
        }
        for (int l = 0; l < counts.length; ++l) {
            if (counts[l] > 0) {
                final NewAttributeBands bands = this.attrMap.getAttributeBands(otherLayouts[l]);
                otherAttributes[l] = bands.parseAttributes(in, counts[l]);
                final int numBackwardsCallables = otherLayouts[l].numBackwardsCallables();
                if (numBackwardsCallables > 0) {
                    final int[] backwardsCalls = new int[numBackwardsCallables];
                    System.arraycopy(classAttrCalls, backwardsCallIndex, backwardsCalls, 0, numBackwardsCallables);
                    bands.setBackwardsCalls(backwardsCalls);
                    backwardsCallIndex += numBackwardsCallables;
                }
            }
        }
        int sourceFileIndex = 0;
        int enclosingMethodIndex = 0;
        int signatureIndex = 0;
        int innerClassIndex = 0;
        int innerClassC2NIndex = 0;
        int versionIndex = 0;
        this.icLocal = new IcTuple[this.classCount][];
        for (int m = 0; m < this.classCount; ++m) {
            final long flag = this.classFlags[m];
            if (deprecatedLayout.matches(this.classFlags[m])) {
                this.classAttributes[m].add(new DeprecatedAttribute());
            }
            if (sourceFileLayout.matches(flag)) {
                final long result = classSourceFile[sourceFileIndex];
                ClassFileEntry value = sourceFileLayout.getValue(result, this.cpBands.getConstantPool());
                if (value == null) {
                    String className = this.classThis[m].substring(this.classThis[m].lastIndexOf(47) + 1);
                    className = className.substring(className.lastIndexOf(46) + 1);
                    final char[] chars = className.toCharArray();
                    int index = -1;
                    for (int j2 = 0; j2 < chars.length; ++j2) {
                        if (chars[j2] <= '-') {
                            index = j2;
                            break;
                        }
                    }
                    if (index > -1) {
                        className = className.substring(0, index);
                    }
                    value = this.cpBands.cpUTF8Value(className + ".java", true);
                }
                this.classAttributes[m].add(new SourceFileAttribute((CPUTF8)value));
                ++sourceFileIndex;
            }
            if (enclosingMethodLayout.matches(flag)) {
                final CPClass theClass = this.cpBands.cpClassValue(enclosingMethodRC[enclosingMethodIndex]);
                CPNameAndType theMethod = null;
                if (enclosingMethodRDN[enclosingMethodIndex] != 0) {
                    theMethod = this.cpBands.cpNameAndTypeValue(enclosingMethodRDN[enclosingMethodIndex] - 1);
                }
                this.classAttributes[m].add(new EnclosingMethodAttribute(theClass, theMethod));
                ++enclosingMethodIndex;
            }
            if (signatureLayout.matches(flag)) {
                final long result = classSignature[signatureIndex];
                final CPUTF8 value2 = (CPUTF8)signatureLayout.getValue(result, this.cpBands.getConstantPool());
                this.classAttributes[m].add(new SignatureAttribute(value2));
                ++signatureIndex;
            }
            if (innerClassLayout.matches(flag)) {
                this.icLocal[m] = new IcTuple[classInnerClassesN[innerClassIndex]];
                for (int j3 = 0; j3 < this.icLocal[m].length; ++j3) {
                    final int icTupleCIndex = classInnerClassesRC[innerClassIndex][j3];
                    int icTupleC2Index = -1;
                    int icTupleNIndex = -1;
                    final String icTupleC = cpClass[icTupleCIndex];
                    int icTupleF = classInnerClassesF[innerClassIndex][j3];
                    String icTupleC2 = null;
                    String icTupleN = null;
                    if (icTupleF != 0) {
                        icTupleC2Index = classInnerClassesOuterRCN[innerClassC2NIndex];
                        icTupleNIndex = classInnerClassesNameRUN[innerClassC2NIndex];
                        icTupleC2 = cpClass[icTupleC2Index];
                        icTupleN = cpUTF8[icTupleNIndex];
                        ++innerClassC2NIndex;
                    }
                    else {
                        final IcBands icBands = this.segment.getIcBands();
                        final IcTuple[] icAll = icBands.getIcTuples();
                        for (int k2 = 0; k2 < icAll.length; ++k2) {
                            if (icAll[k2].getC().equals(icTupleC)) {
                                icTupleF = icAll[k2].getF();
                                icTupleC2 = icAll[k2].getC2();
                                icTupleN = icAll[k2].getN();
                                break;
                            }
                        }
                    }
                    final IcTuple icTuple = new IcTuple(icTupleC, icTupleF, icTupleC2, icTupleN, icTupleCIndex, icTupleC2Index, icTupleNIndex, j3);
                    this.icLocal[m][j3] = icTuple;
                }
                ++innerClassIndex;
            }
            if (versionLayout.matches(flag)) {
                this.classVersionMajor[m] = classFileVersionMajorH[versionIndex];
                this.classVersionMinor[m] = classFileVersionMinorH[versionIndex];
                ++versionIndex;
            }
            else if (this.classVersionMajor != null) {
                this.classVersionMajor[m] = defaultVersionMajor;
                this.classVersionMinor[m] = defaultVersionMinor;
            }
            for (int j3 = 0; j3 < otherLayouts.length; ++j3) {
                if (otherLayouts[j3] != null && otherLayouts[j3].matches(flag)) {
                    this.classAttributes[m].add(otherAttributes[j3].get(0));
                    otherAttributes[j3].remove(0);
                }
            }
        }
    }
    
    private void parseCodeBands(final InputStream in) throws Pack200Exception, IOException {
        final AttributeLayout layout = this.attrMap.getAttributeLayout("Code", 2);
        final int codeCount = SegmentUtils.countMatches(this.methodFlags, layout);
        final int[] codeHeaders = this.decodeBandInt("code_headers", in, Codec.BYTE1, codeCount);
        final boolean allCodeHasFlags = this.segment.getSegmentHeader().getOptions().hasAllCodeFlags();
        if (!allCodeHasFlags) {
            this.codeHasAttributes = new boolean[codeCount];
        }
        int codeSpecialHeader = 0;
        for (int i = 0; i < codeCount; ++i) {
            if (codeHeaders[i] == 0) {
                ++codeSpecialHeader;
                if (!allCodeHasFlags) {
                    this.codeHasAttributes[i] = true;
                }
            }
        }
        final int[] codeMaxStackSpecials = this.decodeBandInt("code_max_stack", in, Codec.UNSIGNED5, codeSpecialHeader);
        final int[] codeMaxNALocalsSpecials = this.decodeBandInt("code_max_na_locals", in, Codec.UNSIGNED5, codeSpecialHeader);
        final int[] codeHandlerCountSpecials = this.decodeBandInt("code_handler_count", in, Codec.UNSIGNED5, codeSpecialHeader);
        this.codeMaxStack = new int[codeCount];
        this.codeMaxNALocals = new int[codeCount];
        this.codeHandlerCount = new int[codeCount];
        int special = 0;
        for (int j = 0; j < codeCount; ++j) {
            final int header = 0xFF & codeHeaders[j];
            if (header < 0) {
                throw new IllegalStateException("Shouldn't get here");
            }
            if (header == 0) {
                this.codeMaxStack[j] = codeMaxStackSpecials[special];
                this.codeMaxNALocals[j] = codeMaxNALocalsSpecials[special];
                this.codeHandlerCount[j] = codeHandlerCountSpecials[special];
                ++special;
            }
            else if (header <= 144) {
                this.codeMaxStack[j] = (header - 1) % 12;
                this.codeMaxNALocals[j] = (header - 1) / 12;
                this.codeHandlerCount[j] = 0;
            }
            else if (header <= 208) {
                this.codeMaxStack[j] = (header - 145) % 8;
                this.codeMaxNALocals[j] = (header - 145) / 8;
                this.codeHandlerCount[j] = 1;
            }
            else {
                if (header > 255) {
                    throw new IllegalStateException("Shouldn't get here either");
                }
                this.codeMaxStack[j] = (header - 209) % 7;
                this.codeMaxNALocals[j] = (header - 209) / 7;
                this.codeHandlerCount[j] = 2;
            }
        }
        this.codeHandlerStartP = this.decodeBandInt("code_handler_start_P", in, Codec.BCI5, this.codeHandlerCount);
        this.codeHandlerEndPO = this.decodeBandInt("code_handler_end_PO", in, Codec.BRANCH5, this.codeHandlerCount);
        this.codeHandlerCatchPO = this.decodeBandInt("code_handler_catch_PO", in, Codec.BRANCH5, this.codeHandlerCount);
        this.codeHandlerClassRCN = this.decodeBandInt("code_handler_class_RCN", in, Codec.UNSIGNED5, this.codeHandlerCount);
        final int codeFlagsCount = allCodeHasFlags ? codeCount : codeSpecialHeader;
        this.codeAttributes = new List[codeFlagsCount];
        for (int k = 0; k < this.codeAttributes.length; ++k) {
            this.codeAttributes[k] = new ArrayList();
        }
        this.parseCodeAttrBands(in, codeFlagsCount);
    }
    
    private void parseCodeAttrBands(final InputStream in, final int codeFlagsCount) throws IOException, Pack200Exception {
        final long[] codeFlags = this.parseFlags("code_flags", in, codeFlagsCount, Codec.UNSIGNED5, this.segment.getSegmentHeader().getOptions().hasCodeFlagsHi());
        final int codeAttrCount = SegmentUtils.countBit16(codeFlags);
        final int[] codeAttrCounts = this.decodeBandInt("code_attr_count", in, Codec.UNSIGNED5, codeAttrCount);
        final int[][] codeAttrIndexes = this.decodeBandInt("code_attr_indexes", in, Codec.UNSIGNED5, codeAttrCounts);
        int callCount = 0;
        for (int i = 0; i < codeAttrIndexes.length; ++i) {
            for (int j = 0; j < codeAttrIndexes[i].length; ++j) {
                final int index = codeAttrIndexes[i][j];
                final AttributeLayout layout = this.attrMap.getAttributeLayout(index, 3);
                callCount += layout.numBackwardsCallables();
            }
        }
        final int[] codeAttrCalls = this.decodeBandInt("code_attr_calls", in, Codec.UNSIGNED5, callCount);
        final AttributeLayout lineNumberTableLayout = this.attrMap.getAttributeLayout("LineNumberTable", 3);
        final int lineNumberTableCount = SegmentUtils.countMatches(codeFlags, lineNumberTableLayout);
        final int[] lineNumberTableN = this.decodeBandInt("code_LineNumberTable_N", in, Codec.UNSIGNED5, lineNumberTableCount);
        final int[][] lineNumberTableBciP = this.decodeBandInt("code_LineNumberTable_bci_P", in, Codec.BCI5, lineNumberTableN);
        final int[][] lineNumberTableLine = this.decodeBandInt("code_LineNumberTable_line", in, Codec.UNSIGNED5, lineNumberTableN);
        final AttributeLayout localVariableTableLayout = this.attrMap.getAttributeLayout("LocalVariableTable", 3);
        final AttributeLayout localVariableTypeTableLayout = this.attrMap.getAttributeLayout("LocalVariableTypeTable", 3);
        final int lengthLocalVariableNBand = SegmentUtils.countMatches(codeFlags, localVariableTableLayout);
        final int[] localVariableTableN = this.decodeBandInt("code_LocalVariableTable_N", in, Codec.UNSIGNED5, lengthLocalVariableNBand);
        final int[][] localVariableTableBciP = this.decodeBandInt("code_LocalVariableTable_bci_P", in, Codec.BCI5, localVariableTableN);
        final int[][] localVariableTableSpanO = this.decodeBandInt("code_LocalVariableTable_span_O", in, Codec.BRANCH5, localVariableTableN);
        final CPUTF8[][] localVariableTableNameRU = this.parseCPUTF8References("code_LocalVariableTable_name_RU", in, Codec.UNSIGNED5, localVariableTableN);
        final CPUTF8[][] localVariableTableTypeRS = this.parseCPSignatureReferences("code_LocalVariableTable_type_RS", in, Codec.UNSIGNED5, localVariableTableN);
        final int[][] localVariableTableSlot = this.decodeBandInt("code_LocalVariableTable_slot", in, Codec.UNSIGNED5, localVariableTableN);
        final int lengthLocalVariableTypeTableNBand = SegmentUtils.countMatches(codeFlags, localVariableTypeTableLayout);
        final int[] localVariableTypeTableN = this.decodeBandInt("code_LocalVariableTypeTable_N", in, Codec.UNSIGNED5, lengthLocalVariableTypeTableNBand);
        final int[][] localVariableTypeTableBciP = this.decodeBandInt("code_LocalVariableTypeTable_bci_P", in, Codec.BCI5, localVariableTypeTableN);
        final int[][] localVariableTypeTableSpanO = this.decodeBandInt("code_LocalVariableTypeTable_span_O", in, Codec.BRANCH5, localVariableTypeTableN);
        final CPUTF8[][] localVariableTypeTableNameRU = this.parseCPUTF8References("code_LocalVariableTypeTable_name_RU", in, Codec.UNSIGNED5, localVariableTypeTableN);
        final CPUTF8[][] localVariableTypeTableTypeRS = this.parseCPSignatureReferences("code_LocalVariableTypeTable_type_RS", in, Codec.UNSIGNED5, localVariableTypeTableN);
        final int[][] localVariableTypeTableSlot = this.decodeBandInt("code_LocalVariableTypeTable_slot", in, Codec.UNSIGNED5, localVariableTypeTableN);
        int backwardsCallIndex = 0;
        final int limit = this.options.hasCodeFlagsHi() ? 62 : 31;
        final AttributeLayout[] otherLayouts = new AttributeLayout[limit + 1];
        final int[] counts = new int[limit + 1];
        final List[] otherAttributes = new List[limit + 1];
        for (int k = 0; k < limit; ++k) {
            final AttributeLayout layout2 = this.attrMap.getAttributeLayout(k, 3);
            if (layout2 != null && !layout2.isDefaultLayout()) {
                otherLayouts[k] = layout2;
                counts[k] = SegmentUtils.countMatches(codeFlags, layout2);
            }
        }
        for (int k = 0; k < counts.length; ++k) {
            if (counts[k] > 0) {
                final NewAttributeBands bands = this.attrMap.getAttributeBands(otherLayouts[k]);
                otherAttributes[k] = bands.parseAttributes(in, counts[k]);
                final int numBackwardsCallables = otherLayouts[k].numBackwardsCallables();
                if (numBackwardsCallables > 0) {
                    final int[] backwardsCalls = new int[numBackwardsCallables];
                    System.arraycopy(codeAttrCalls, backwardsCallIndex, backwardsCalls, 0, numBackwardsCallables);
                    bands.setBackwardsCalls(backwardsCalls);
                    backwardsCallIndex += numBackwardsCallables;
                }
            }
        }
        int lineNumberIndex = 0;
        int lvtIndex = 0;
        int lvttIndex = 0;
        for (int l = 0; l < codeFlagsCount; ++l) {
            if (lineNumberTableLayout.matches(codeFlags[l])) {
                final LineNumberTableAttribute lnta = new LineNumberTableAttribute(lineNumberTableN[lineNumberIndex], lineNumberTableBciP[lineNumberIndex], lineNumberTableLine[lineNumberIndex]);
                ++lineNumberIndex;
                this.codeAttributes[l].add(lnta);
            }
            if (localVariableTableLayout.matches(codeFlags[l])) {
                final LocalVariableTableAttribute lvta = new LocalVariableTableAttribute(localVariableTableN[lvtIndex], localVariableTableBciP[lvtIndex], localVariableTableSpanO[lvtIndex], localVariableTableNameRU[lvtIndex], localVariableTableTypeRS[lvtIndex], localVariableTableSlot[lvtIndex]);
                ++lvtIndex;
                this.codeAttributes[l].add(lvta);
            }
            if (localVariableTypeTableLayout.matches(codeFlags[l])) {
                final LocalVariableTypeTableAttribute lvtta = new LocalVariableTypeTableAttribute(localVariableTypeTableN[lvttIndex], localVariableTypeTableBciP[lvttIndex], localVariableTypeTableSpanO[lvttIndex], localVariableTypeTableNameRU[lvttIndex], localVariableTypeTableTypeRS[lvttIndex], localVariableTypeTableSlot[lvttIndex]);
                ++lvttIndex;
                this.codeAttributes[l].add(lvtta);
            }
            for (int m = 0; m < otherLayouts.length; ++m) {
                if (otherLayouts[m] != null && otherLayouts[m].matches(codeFlags[l])) {
                    this.codeAttributes[l].add(otherAttributes[m].get(0));
                    otherAttributes[m].remove(0);
                }
            }
        }
    }
    
    private int parseFieldMetadataBands(final InputStream in, final int[] fieldAttrCalls) throws Pack200Exception, IOException {
        int backwardsCallsUsed = 0;
        final String[] RxA = { "RVA", "RIA" };
        final AttributeLayout rvaLayout = this.attrMap.getAttributeLayout("RuntimeVisibleAnnotations", 1);
        final AttributeLayout riaLayout = this.attrMap.getAttributeLayout("RuntimeInvisibleAnnotations", 1);
        final int rvaCount = SegmentUtils.countMatches(this.fieldFlags, rvaLayout);
        final int riaCount = SegmentUtils.countMatches(this.fieldFlags, riaLayout);
        final int[] RxACount = { rvaCount, riaCount };
        final int[] backwardsCalls = { 0, 0 };
        if (rvaCount > 0) {
            backwardsCalls[0] = fieldAttrCalls[0];
            ++backwardsCallsUsed;
            if (riaCount > 0) {
                backwardsCalls[1] = fieldAttrCalls[1];
                ++backwardsCallsUsed;
            }
        }
        else if (riaCount > 0) {
            backwardsCalls[1] = fieldAttrCalls[0];
            ++backwardsCallsUsed;
        }
        final MetadataBandGroup[] mb = this.parseMetadata(in, RxA, RxACount, backwardsCalls, "field");
        final List rvaAttributes = mb[0].getAttributes();
        final List riaAttributes = mb[1].getAttributes();
        int rvaAttributesIndex = 0;
        int riaAttributesIndex = 0;
        for (int i = 0; i < this.fieldFlags.length; ++i) {
            for (int j = 0; j < this.fieldFlags[i].length; ++j) {
                if (rvaLayout.matches(this.fieldFlags[i][j])) {
                    this.fieldAttributes[i][j].add(rvaAttributes.get(rvaAttributesIndex++));
                }
                if (riaLayout.matches(this.fieldFlags[i][j])) {
                    this.fieldAttributes[i][j].add(riaAttributes.get(riaAttributesIndex++));
                }
            }
        }
        return backwardsCallsUsed;
    }
    
    private MetadataBandGroup[] parseMetadata(final InputStream in, final String[] RxA, final int[] RxACount, final int[] backwardsCallCounts, final String contextName) throws IOException, Pack200Exception {
        final MetadataBandGroup[] mbg = new MetadataBandGroup[RxA.length];
        for (int i = 0; i < RxA.length; ++i) {
            mbg[i] = new MetadataBandGroup(RxA[i], this.cpBands);
            final String rxa = RxA[i];
            if (rxa.indexOf(80) >= 0) {
                mbg[i].param_NB = this.decodeBandInt(contextName + "_" + rxa + "_param_NB", in, Codec.BYTE1, RxACount[i]);
            }
            int pairCount = 0;
            if (!rxa.equals("AD")) {
                mbg[i].anno_N = this.decodeBandInt(contextName + "_" + rxa + "_anno_N", in, Codec.UNSIGNED5, RxACount[i]);
                mbg[i].type_RS = this.parseCPSignatureReferences(contextName + "_" + rxa + "_type_RS", in, Codec.UNSIGNED5, mbg[i].anno_N);
                mbg[i].pair_N = this.decodeBandInt(contextName + "_" + rxa + "_pair_N", in, Codec.UNSIGNED5, mbg[i].anno_N);
                for (int j = 0; j < mbg[i].pair_N.length; ++j) {
                    for (int k = 0; k < mbg[i].pair_N[j].length; ++k) {
                        pairCount += mbg[i].pair_N[j][k];
                    }
                }
                mbg[i].name_RU = this.parseCPUTF8References(contextName + "_" + rxa + "_name_RU", in, Codec.UNSIGNED5, pairCount);
            }
            else {
                pairCount = RxACount[i];
            }
            mbg[i].T = this.decodeBandInt(contextName + "_" + rxa + "_T", in, Codec.BYTE1, pairCount + backwardsCallCounts[i]);
            int ICount = 0;
            int DCount = 0;
            int FCount = 0;
            int JCount = 0;
            int cCount = 0;
            int eCount = 0;
            int sCount = 0;
            int arrayCount = 0;
            int atCount = 0;
            for (int l = 0; l < mbg[i].T.length; ++l) {
                final char c = (char)mbg[i].T[l];
                switch (c) {
                    case 'B':
                    case 'C':
                    case 'I':
                    case 'S':
                    case 'Z': {
                        ++ICount;
                        break;
                    }
                    case 'D': {
                        ++DCount;
                        break;
                    }
                    case 'F': {
                        ++FCount;
                        break;
                    }
                    case 'J': {
                        ++JCount;
                        break;
                    }
                    case 'c': {
                        ++cCount;
                        break;
                    }
                    case 'e': {
                        ++eCount;
                        break;
                    }
                    case 's': {
                        ++sCount;
                        break;
                    }
                    case '[': {
                        ++arrayCount;
                        break;
                    }
                    case '@': {
                        ++atCount;
                        break;
                    }
                }
            }
            mbg[i].caseI_KI = this.parseCPIntReferences(contextName + "_" + rxa + "_caseI_KI", in, Codec.UNSIGNED5, ICount);
            mbg[i].caseD_KD = this.parseCPDoubleReferences(contextName + "_" + rxa + "_caseD_KD", in, Codec.UNSIGNED5, DCount);
            mbg[i].caseF_KF = this.parseCPFloatReferences(contextName + "_" + rxa + "_caseF_KF", in, Codec.UNSIGNED5, FCount);
            mbg[i].caseJ_KJ = this.parseCPLongReferences(contextName + "_" + rxa + "_caseJ_KJ", in, Codec.UNSIGNED5, JCount);
            mbg[i].casec_RS = this.parseCPSignatureReferences(contextName + "_" + rxa + "_casec_RS", in, Codec.UNSIGNED5, cCount);
            mbg[i].caseet_RS = this.parseReferences(contextName + "_" + rxa + "_caseet_RS", in, Codec.UNSIGNED5, eCount, this.cpBands.getCpSignature());
            mbg[i].caseec_RU = this.parseReferences(contextName + "_" + rxa + "_caseec_RU", in, Codec.UNSIGNED5, eCount, this.cpBands.getCpUTF8());
            mbg[i].cases_RU = this.parseCPUTF8References(contextName + "_" + rxa + "_cases_RU", in, Codec.UNSIGNED5, sCount);
            mbg[i].casearray_N = this.decodeBandInt(contextName + "_" + rxa + "_casearray_N", in, Codec.UNSIGNED5, arrayCount);
            mbg[i].nesttype_RS = this.parseCPUTF8References(contextName + "_" + rxa + "_nesttype_RS", in, Codec.UNSIGNED5, atCount);
            mbg[i].nestpair_N = this.decodeBandInt(contextName + "_" + rxa + "_nestpair_N", in, Codec.UNSIGNED5, atCount);
            int nestPairCount = 0;
            for (int m = 0; m < mbg[i].nestpair_N.length; ++m) {
                nestPairCount += mbg[i].nestpair_N[m];
            }
            mbg[i].nestname_RU = this.parseCPUTF8References(contextName + "_" + rxa + "_nestname_RU", in, Codec.UNSIGNED5, nestPairCount);
        }
        return mbg;
    }
    
    private int parseMethodMetadataBands(final InputStream in, final int[] methodAttrCalls) throws Pack200Exception, IOException {
        int backwardsCallsUsed = 0;
        final String[] RxA = { "RVA", "RIA", "RVPA", "RIPA", "AD" };
        final int[] rxaCounts = { 0, 0, 0, 0, 0 };
        final AttributeLayout rvaLayout = this.attrMap.getAttributeLayout("RuntimeVisibleAnnotations", 2);
        final AttributeLayout riaLayout = this.attrMap.getAttributeLayout("RuntimeInvisibleAnnotations", 2);
        final AttributeLayout rvpaLayout = this.attrMap.getAttributeLayout("RuntimeVisibleParameterAnnotations", 2);
        final AttributeLayout ripaLayout = this.attrMap.getAttributeLayout("RuntimeInvisibleParameterAnnotations", 2);
        final AttributeLayout adLayout = this.attrMap.getAttributeLayout("AnnotationDefault", 2);
        final AttributeLayout[] rxaLayouts = { rvaLayout, riaLayout, rvpaLayout, ripaLayout, adLayout };
        for (int i = 0; i < rxaLayouts.length; ++i) {
            rxaCounts[i] = SegmentUtils.countMatches(this.methodFlags, rxaLayouts[i]);
        }
        final int[] backwardsCalls = new int[5];
        int methodAttrIndex = 0;
        for (int j = 0; j < backwardsCalls.length; ++j) {
            if (rxaCounts[j] > 0) {
                ++backwardsCallsUsed;
                backwardsCalls[j] = methodAttrCalls[methodAttrIndex];
                ++methodAttrIndex;
            }
            else {
                backwardsCalls[j] = 0;
            }
        }
        final MetadataBandGroup[] mbgs = this.parseMetadata(in, RxA, rxaCounts, backwardsCalls, "method");
        final List[] attributeLists = new List[RxA.length];
        final int[] attributeListIndexes = new int[RxA.length];
        for (int k = 0; k < mbgs.length; ++k) {
            attributeLists[k] = mbgs[k].getAttributes();
            attributeListIndexes[k] = 0;
        }
        for (int k = 0; k < this.methodFlags.length; ++k) {
            for (int l = 0; l < this.methodFlags[k].length; ++l) {
                for (int m = 0; m < rxaLayouts.length; ++m) {
                    if (rxaLayouts[m].matches(this.methodFlags[k][l])) {
                        this.methodAttributes[k][l].add(attributeLists[m].get(attributeListIndexes[m]++));
                    }
                }
            }
        }
        return backwardsCallsUsed;
    }
    
    private int parseClassMetadataBands(final InputStream in, final int[] classAttrCalls) throws Pack200Exception, IOException {
        int numBackwardsCalls = 0;
        final String[] RxA = { "RVA", "RIA" };
        final AttributeLayout rvaLayout = this.attrMap.getAttributeLayout("RuntimeVisibleAnnotations", 0);
        final AttributeLayout riaLayout = this.attrMap.getAttributeLayout("RuntimeInvisibleAnnotations", 0);
        final int rvaCount = SegmentUtils.countMatches(this.classFlags, rvaLayout);
        final int riaCount = SegmentUtils.countMatches(this.classFlags, riaLayout);
        final int[] RxACount = { rvaCount, riaCount };
        final int[] backwardsCalls = { 0, 0 };
        if (rvaCount > 0) {
            ++numBackwardsCalls;
            backwardsCalls[0] = classAttrCalls[0];
            if (riaCount > 0) {
                ++numBackwardsCalls;
                backwardsCalls[1] = classAttrCalls[1];
            }
        }
        else if (riaCount > 0) {
            ++numBackwardsCalls;
            backwardsCalls[1] = classAttrCalls[0];
        }
        final MetadataBandGroup[] mbgs = this.parseMetadata(in, RxA, RxACount, backwardsCalls, "class");
        final List rvaAttributes = mbgs[0].getAttributes();
        final List riaAttributes = mbgs[1].getAttributes();
        int rvaAttributesIndex = 0;
        int riaAttributesIndex = 0;
        for (int i = 0; i < this.classFlags.length; ++i) {
            if (rvaLayout.matches(this.classFlags[i])) {
                this.classAttributes[i].add(rvaAttributes.get(rvaAttributesIndex++));
            }
            if (riaLayout.matches(this.classFlags[i])) {
                this.classAttributes[i].add(riaAttributes.get(riaAttributesIndex++));
            }
        }
        return numBackwardsCalls;
    }
    
    public ArrayList[] getClassAttributes() {
        return this.classAttributes;
    }
    
    public int[] getClassFieldCount() {
        return this.classFieldCount;
    }
    
    public long[] getRawClassFlags() {
        return this.classFlags;
    }
    
    public long[] getClassFlags() throws Pack200Exception {
        if (this.classAccessFlags == null) {
            long mask = 32767L;
            for (int i = 0; i < 16; ++i) {
                final AttributeLayout layout = this.attrMap.getAttributeLayout(i, 0);
                if (layout != null && !layout.isDefaultLayout()) {
                    mask &= ~(1 << i);
                }
            }
            this.classAccessFlags = new long[this.classFlags.length];
            for (int i = 0; i < this.classFlags.length; ++i) {
                this.classAccessFlags[i] = (this.classFlags[i] & mask);
            }
        }
        return this.classAccessFlags;
    }
    
    public int[][] getClassInterfacesInts() {
        return this.classInterfacesInts;
    }
    
    public int[] getClassMethodCount() {
        return this.classMethodCount;
    }
    
    public int[] getClassSuperInts() {
        return this.classSuperInts;
    }
    
    public int[] getClassThisInts() {
        return this.classThisInts;
    }
    
    public int[] getCodeMaxNALocals() {
        return this.codeMaxNALocals;
    }
    
    public int[] getCodeMaxStack() {
        return this.codeMaxStack;
    }
    
    public ArrayList[][] getFieldAttributes() {
        return this.fieldAttributes;
    }
    
    public int[][] getFieldDescrInts() {
        return this.fieldDescrInts;
    }
    
    public int[][] getMethodDescrInts() {
        return this.methodDescrInts;
    }
    
    public long[][] getFieldFlags() throws Pack200Exception {
        if (this.fieldAccessFlags == null) {
            long mask = 32767L;
            for (int i = 0; i < 16; ++i) {
                final AttributeLayout layout = this.attrMap.getAttributeLayout(i, 1);
                if (layout != null && !layout.isDefaultLayout()) {
                    mask &= ~(1 << i);
                }
            }
            this.fieldAccessFlags = new long[this.fieldFlags.length][];
            for (int i = 0; i < this.fieldFlags.length; ++i) {
                this.fieldAccessFlags[i] = new long[this.fieldFlags[i].length];
                for (int j = 0; j < this.fieldFlags[i].length; ++j) {
                    this.fieldAccessFlags[i][j] = (this.fieldFlags[i][j] & mask);
                }
            }
        }
        return this.fieldAccessFlags;
    }
    
    public ArrayList getOrderedCodeAttributes() {
        final ArrayList orderedAttributeList = new ArrayList(this.codeAttributes.length);
        for (int classIndex = 0; classIndex < this.codeAttributes.length; ++classIndex) {
            final ArrayList currentAttributes = new ArrayList(this.codeAttributes[classIndex].size());
            for (int attributeIndex = 0; attributeIndex < this.codeAttributes[classIndex].size(); ++attributeIndex) {
                final Attribute attribute = this.codeAttributes[classIndex].get(attributeIndex);
                currentAttributes.add(attribute);
            }
            orderedAttributeList.add(currentAttributes);
        }
        return orderedAttributeList;
    }
    
    public ArrayList[][] getMethodAttributes() {
        return this.methodAttributes;
    }
    
    public String[][] getMethodDescr() {
        return this.methodDescr;
    }
    
    public long[][] getMethodFlags() throws Pack200Exception {
        if (this.methodAccessFlags == null) {
            long mask = 32767L;
            for (int i = 0; i < 16; ++i) {
                final AttributeLayout layout = this.attrMap.getAttributeLayout(i, 2);
                if (layout != null && !layout.isDefaultLayout()) {
                    mask &= ~(1 << i);
                }
            }
            this.methodAccessFlags = new long[this.methodFlags.length][];
            for (int i = 0; i < this.methodFlags.length; ++i) {
                this.methodAccessFlags[i] = new long[this.methodFlags[i].length];
                for (int j = 0; j < this.methodFlags[i].length; ++j) {
                    this.methodAccessFlags[i][j] = (this.methodFlags[i][j] & mask);
                }
            }
        }
        return this.methodAccessFlags;
    }
    
    public int[] getClassVersionMajor() {
        return this.classVersionMajor;
    }
    
    public int[] getClassVersionMinor() {
        return this.classVersionMinor;
    }
    
    public int[] getCodeHandlerCount() {
        return this.codeHandlerCount;
    }
    
    public int[][] getCodeHandlerCatchPO() {
        return this.codeHandlerCatchPO;
    }
    
    public int[][] getCodeHandlerClassRCN() {
        return this.codeHandlerClassRCN;
    }
    
    public int[][] getCodeHandlerEndPO() {
        return this.codeHandlerEndPO;
    }
    
    public int[][] getCodeHandlerStartP() {
        return this.codeHandlerStartP;
    }
    
    public IcTuple[][] getIcLocal() {
        return this.icLocal;
    }
    
    public boolean[] getCodeHasAttributes() {
        return this.codeHasAttributes;
    }
}
