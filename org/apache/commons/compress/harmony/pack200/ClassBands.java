package org.apache.commons.compress.harmony.pack200;

import java.util.Collection;
import org.objectweb.asm.Label;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class ClassBands extends BandSet
{
    private final CpBands cpBands;
    private final AttributeDefinitionBands attrBands;
    private final CPClass[] class_this;
    private final CPClass[] class_super;
    private final CPClass[][] class_interface;
    private final int[] class_interface_count;
    private final int[] major_versions;
    private final long[] class_flags;
    private int[] class_attr_calls;
    private final List classSourceFile;
    private final List classEnclosingMethodClass;
    private final List classEnclosingMethodDesc;
    private final List classSignature;
    private final IntList classFileVersionMinor;
    private final IntList classFileVersionMajor;
    private final int[] class_field_count;
    private final CPNameAndType[][] field_descr;
    private final long[][] field_flags;
    private int[] field_attr_calls;
    private final List fieldConstantValueKQ;
    private final List fieldSignature;
    private final int[] class_method_count;
    private final CPNameAndType[][] method_descr;
    private final long[][] method_flags;
    private int[] method_attr_calls;
    private final List methodSignature;
    private final IntList methodExceptionNumber;
    private final List methodExceptionClasses;
    private int[] codeHeaders;
    private final IntList codeMaxStack;
    private final IntList codeMaxLocals;
    private final IntList codeHandlerCount;
    private final List codeHandlerStartP;
    private final List codeHandlerEndPO;
    private final List codeHandlerCatchPO;
    private final List codeHandlerClass;
    private final List codeFlags;
    private int[] code_attr_calls;
    private final IntList codeLineNumberTableN;
    private final List codeLineNumberTableBciP;
    private final IntList codeLineNumberTableLine;
    private final IntList codeLocalVariableTableN;
    private final List codeLocalVariableTableBciP;
    private final List codeLocalVariableTableSpanO;
    private final List codeLocalVariableTableNameRU;
    private final List codeLocalVariableTableTypeRS;
    private final IntList codeLocalVariableTableSlot;
    private final IntList codeLocalVariableTypeTableN;
    private final List codeLocalVariableTypeTableBciP;
    private final List codeLocalVariableTypeTableSpanO;
    private final List codeLocalVariableTypeTableNameRU;
    private final List codeLocalVariableTypeTableTypeRS;
    private final IntList codeLocalVariableTypeTableSlot;
    private final MetadataBandGroup class_RVA_bands;
    private final MetadataBandGroup class_RIA_bands;
    private final MetadataBandGroup field_RVA_bands;
    private final MetadataBandGroup field_RIA_bands;
    private final MetadataBandGroup method_RVA_bands;
    private final MetadataBandGroup method_RIA_bands;
    private final MetadataBandGroup method_RVPA_bands;
    private final MetadataBandGroup method_RIPA_bands;
    private final MetadataBandGroup method_AD_bands;
    private final List classAttributeBands;
    private final List methodAttributeBands;
    private final List fieldAttributeBands;
    private final List codeAttributeBands;
    private final List tempFieldFlags;
    private final List tempFieldDesc;
    private final List tempMethodFlags;
    private final List tempMethodDesc;
    private TempParamAnnotation tempMethodRVPA;
    private TempParamAnnotation tempMethodRIPA;
    private boolean anySyntheticClasses;
    private boolean anySyntheticFields;
    private boolean anySyntheticMethods;
    private final Segment segment;
    private final Map classReferencesInnerClass;
    private final boolean stripDebug;
    private int index;
    private int numMethodArgs;
    private int[] class_InnerClasses_N;
    private CPClass[] class_InnerClasses_RC;
    private int[] class_InnerClasses_F;
    private List classInnerClassesOuterRCN;
    private List classInnerClassesNameRUN;
    
    public ClassBands(final Segment segment, final int numClasses, final int effort, final boolean stripDebug) throws IOException {
        super(effort, segment.getSegmentHeader());
        this.classSourceFile = new ArrayList();
        this.classEnclosingMethodClass = new ArrayList();
        this.classEnclosingMethodDesc = new ArrayList();
        this.classSignature = new ArrayList();
        this.classFileVersionMinor = new IntList();
        this.classFileVersionMajor = new IntList();
        this.fieldConstantValueKQ = new ArrayList();
        this.fieldSignature = new ArrayList();
        this.methodSignature = new ArrayList();
        this.methodExceptionNumber = new IntList();
        this.methodExceptionClasses = new ArrayList();
        this.codeMaxStack = new IntList();
        this.codeMaxLocals = new IntList();
        this.codeHandlerCount = new IntList();
        this.codeHandlerStartP = new ArrayList();
        this.codeHandlerEndPO = new ArrayList();
        this.codeHandlerCatchPO = new ArrayList();
        this.codeHandlerClass = new ArrayList();
        this.codeFlags = new ArrayList();
        this.codeLineNumberTableN = new IntList();
        this.codeLineNumberTableBciP = new ArrayList();
        this.codeLineNumberTableLine = new IntList();
        this.codeLocalVariableTableN = new IntList();
        this.codeLocalVariableTableBciP = new ArrayList();
        this.codeLocalVariableTableSpanO = new ArrayList();
        this.codeLocalVariableTableNameRU = new ArrayList();
        this.codeLocalVariableTableTypeRS = new ArrayList();
        this.codeLocalVariableTableSlot = new IntList();
        this.codeLocalVariableTypeTableN = new IntList();
        this.codeLocalVariableTypeTableBciP = new ArrayList();
        this.codeLocalVariableTypeTableSpanO = new ArrayList();
        this.codeLocalVariableTypeTableNameRU = new ArrayList();
        this.codeLocalVariableTypeTableTypeRS = new ArrayList();
        this.codeLocalVariableTypeTableSlot = new IntList();
        this.classAttributeBands = new ArrayList();
        this.methodAttributeBands = new ArrayList();
        this.fieldAttributeBands = new ArrayList();
        this.codeAttributeBands = new ArrayList();
        this.tempFieldFlags = new ArrayList();
        this.tempFieldDesc = new ArrayList();
        this.tempMethodFlags = new ArrayList();
        this.tempMethodDesc = new ArrayList();
        this.anySyntheticClasses = false;
        this.anySyntheticFields = false;
        this.anySyntheticMethods = false;
        this.classReferencesInnerClass = new HashMap();
        this.index = 0;
        this.numMethodArgs = 0;
        this.stripDebug = stripDebug;
        this.segment = segment;
        this.cpBands = segment.getCpBands();
        this.attrBands = segment.getAttrBands();
        this.class_this = new CPClass[numClasses];
        this.class_super = new CPClass[numClasses];
        this.class_interface_count = new int[numClasses];
        this.class_interface = new CPClass[numClasses][];
        this.class_field_count = new int[numClasses];
        this.class_method_count = new int[numClasses];
        this.field_descr = new CPNameAndType[numClasses][];
        this.field_flags = new long[numClasses][];
        this.method_descr = new CPNameAndType[numClasses][];
        this.method_flags = new long[numClasses][];
        for (int i = 0; i < numClasses; ++i) {
            this.field_flags[i] = new long[0];
            this.method_flags[i] = new long[0];
        }
        this.major_versions = new int[numClasses];
        this.class_flags = new long[numClasses];
        this.class_RVA_bands = new MetadataBandGroup("RVA", 0, this.cpBands, this.segmentHeader, effort);
        this.class_RIA_bands = new MetadataBandGroup("RIA", 0, this.cpBands, this.segmentHeader, effort);
        this.field_RVA_bands = new MetadataBandGroup("RVA", 1, this.cpBands, this.segmentHeader, effort);
        this.field_RIA_bands = new MetadataBandGroup("RIA", 1, this.cpBands, this.segmentHeader, effort);
        this.method_RVA_bands = new MetadataBandGroup("RVA", 2, this.cpBands, this.segmentHeader, effort);
        this.method_RIA_bands = new MetadataBandGroup("RIA", 2, this.cpBands, this.segmentHeader, effort);
        this.method_RVPA_bands = new MetadataBandGroup("RVPA", 2, this.cpBands, this.segmentHeader, effort);
        this.method_RIPA_bands = new MetadataBandGroup("RIPA", 2, this.cpBands, this.segmentHeader, effort);
        this.method_AD_bands = new MetadataBandGroup("AD", 2, this.cpBands, this.segmentHeader, effort);
        this.createNewAttributeBands();
    }
    
    private void createNewAttributeBands() throws IOException {
        final List classAttributeLayouts = this.attrBands.getClassAttributeLayouts();
        for (final AttributeDefinitionBands.AttributeDefinition def : classAttributeLayouts) {
            this.classAttributeBands.add(new NewAttributeBands(this.effort, this.cpBands, this.segment.getSegmentHeader(), def));
        }
        final List methodAttributeLayouts = this.attrBands.getMethodAttributeLayouts();
        for (final AttributeDefinitionBands.AttributeDefinition def2 : methodAttributeLayouts) {
            this.methodAttributeBands.add(new NewAttributeBands(this.effort, this.cpBands, this.segment.getSegmentHeader(), def2));
        }
        final List fieldAttributeLayouts = this.attrBands.getFieldAttributeLayouts();
        for (final AttributeDefinitionBands.AttributeDefinition def3 : fieldAttributeLayouts) {
            this.fieldAttributeBands.add(new NewAttributeBands(this.effort, this.cpBands, this.segment.getSegmentHeader(), def3));
        }
        final List codeAttributeLayouts = this.attrBands.getCodeAttributeLayouts();
        for (final AttributeDefinitionBands.AttributeDefinition def4 : codeAttributeLayouts) {
            this.codeAttributeBands.add(new NewAttributeBands(this.effort, this.cpBands, this.segment.getSegmentHeader(), def4));
        }
    }
    
    public void addClass(final int major, int flags, final String className, final String signature, final String superName, final String[] interfaces) {
        this.class_this[this.index] = this.cpBands.getCPClass(className);
        this.class_super[this.index] = this.cpBands.getCPClass(superName);
        this.class_interface_count[this.index] = interfaces.length;
        this.class_interface[this.index] = new CPClass[interfaces.length];
        for (int i = 0; i < interfaces.length; ++i) {
            this.class_interface[this.index][i] = this.cpBands.getCPClass(interfaces[i]);
        }
        this.major_versions[this.index] = major;
        this.class_flags[this.index] = flags;
        if (!this.anySyntheticClasses && (flags & 0x1000) != 0x0 && this.segment.getCurrentClassReader().hasSyntheticAttributes()) {
            this.cpBands.addCPUtf8("Synthetic");
            this.anySyntheticClasses = true;
        }
        if ((flags & 0x20000) != 0x0) {
            flags &= 0xFFFDFFFF;
            flags |= 0x100000;
        }
        if (signature != null) {
            final long[] class_flags = this.class_flags;
            final int index = this.index;
            class_flags[index] |= 0x80000L;
            this.classSignature.add(this.cpBands.getCPSignature(signature));
        }
    }
    
    public void currentClassReferencesInnerClass(final CPClass inner) {
        if (this.index < this.class_this.length) {
            final CPClass currentClass = this.class_this[this.index];
            if (currentClass != null && !currentClass.equals(inner) && !this.isInnerClassOf(currentClass.toString(), inner)) {
                Set referencedInnerClasses = this.classReferencesInnerClass.get(currentClass);
                if (referencedInnerClasses == null) {
                    referencedInnerClasses = new HashSet();
                    this.classReferencesInnerClass.put(currentClass, referencedInnerClasses);
                }
                referencedInnerClasses.add(inner);
            }
        }
    }
    
    private boolean isInnerClassOf(final String possibleInner, final CPClass possibleOuter) {
        if (this.isInnerClass(possibleInner)) {
            final String superClassName = possibleInner.substring(0, possibleInner.lastIndexOf(36));
            return superClassName.equals(possibleOuter.toString()) || this.isInnerClassOf(superClassName, possibleOuter);
        }
        return false;
    }
    
    private boolean isInnerClass(final String possibleInner) {
        return possibleInner.indexOf(36) != -1;
    }
    
    public void addField(int flags, final String name, final String desc, final String signature, final Object value) {
        flags &= 0xFFFF;
        this.tempFieldDesc.add(this.cpBands.getCPNameAndType(name, desc));
        if (signature != null) {
            this.fieldSignature.add(this.cpBands.getCPSignature(signature));
            flags |= 0x80000;
        }
        if ((flags & 0x20000) != 0x0) {
            flags &= 0xFFFDFFFF;
            flags |= 0x100000;
        }
        if (value != null) {
            this.fieldConstantValueKQ.add(this.cpBands.getConstant(value));
            flags |= 0x20000;
        }
        if (!this.anySyntheticFields && (flags & 0x1000) != 0x0 && this.segment.getCurrentClassReader().hasSyntheticAttributes()) {
            this.cpBands.addCPUtf8("Synthetic");
            this.anySyntheticFields = true;
        }
        this.tempFieldFlags.add((long)flags);
    }
    
    public void finaliseBands() {
        final int defaultMajorVersion = this.segmentHeader.getDefaultMajorVersion();
        for (int i = 0; i < this.class_flags.length; ++i) {
            final int major = this.major_versions[i];
            if (major != defaultMajorVersion) {
                final long[] class_flags = this.class_flags;
                final int n = i;
                class_flags[n] |= 0x1000000L;
                this.classFileVersionMajor.add(major);
                this.classFileVersionMinor.add(0);
            }
        }
        this.codeHeaders = new int[this.codeHandlerCount.size()];
        int removed = 0;
        for (int j = 0; j < this.codeHeaders.length; ++j) {
            final int numHandlers = this.codeHandlerCount.get(j - removed);
            final int maxLocals = this.codeMaxLocals.get(j - removed);
            final int maxStack = this.codeMaxStack.get(j - removed);
            if (numHandlers == 0) {
                final int header = maxLocals * 12 + maxStack + 1;
                if (header < 145 && maxStack < 12) {
                    this.codeHeaders[j] = header;
                }
            }
            else if (numHandlers == 1) {
                final int header = maxLocals * 8 + maxStack + 145;
                if (header < 209 && maxStack < 8) {
                    this.codeHeaders[j] = header;
                }
            }
            else if (numHandlers == 2) {
                final int header = maxLocals * 7 + maxStack + 209;
                if (header < 256 && maxStack < 7) {
                    this.codeHeaders[j] = header;
                }
            }
            if (this.codeHeaders[j] != 0) {
                this.codeHandlerCount.remove(j - removed);
                this.codeMaxLocals.remove(j - removed);
                this.codeMaxStack.remove(j - removed);
                ++removed;
            }
            else if (!this.segment.getSegmentHeader().have_all_code_flags()) {
                this.codeFlags.add(0L);
            }
        }
        final IntList innerClassesN = new IntList();
        final List icLocal = new ArrayList();
        for (int k = 0; k < this.class_this.length; ++k) {
            final CPClass cpClass = this.class_this[k];
            final Set referencedInnerClasses = this.classReferencesInnerClass.get(cpClass);
            if (referencedInnerClasses != null) {
                int innerN = 0;
                final List innerClasses = this.segment.getIcBands().getInnerClassesForOuter(cpClass.toString());
                if (innerClasses != null) {
                    final Iterator iterator2 = innerClasses.iterator();
                    while (iterator2.hasNext()) {
                        referencedInnerClasses.remove(iterator2.next().C);
                    }
                }
                final Iterator iterator2 = referencedInnerClasses.iterator();
                while (iterator2.hasNext()) {
                    final CPClass inner = iterator2.next();
                    final IcBands.IcTuple icTuple = this.segment.getIcBands().getIcTuple(inner);
                    if (icTuple != null && !icTuple.isAnonymous()) {
                        icLocal.add(icTuple);
                        ++innerN;
                    }
                }
                if (innerN != 0) {
                    innerClassesN.add(innerN);
                    final long[] class_flags2 = this.class_flags;
                    final int n2 = k;
                    class_flags2[n2] |= 0x800000L;
                }
            }
        }
        this.class_InnerClasses_N = innerClassesN.toArray();
        this.class_InnerClasses_RC = new CPClass[icLocal.size()];
        this.class_InnerClasses_F = new int[icLocal.size()];
        this.classInnerClassesOuterRCN = new ArrayList();
        this.classInnerClassesNameRUN = new ArrayList();
        for (int k = 0; k < this.class_InnerClasses_RC.length; ++k) {
            final IcBands.IcTuple icTuple2 = icLocal.get(k);
            this.class_InnerClasses_RC[k] = icTuple2.C;
            if (icTuple2.C2 == null && icTuple2.N == null) {
                this.class_InnerClasses_F[k] = 0;
            }
            else {
                if (icTuple2.F == 0) {
                    this.class_InnerClasses_F[k] = 65536;
                }
                else {
                    this.class_InnerClasses_F[k] = icTuple2.F;
                }
                this.classInnerClassesOuterRCN.add(icTuple2.C2);
                this.classInnerClassesNameRUN.add(icTuple2.N);
            }
        }
        final IntList classAttrCalls = new IntList();
        final IntList fieldAttrCalls = new IntList();
        final IntList methodAttrCalls = new IntList();
        final IntList codeAttrCalls = new IntList();
        if (this.class_RVA_bands.hasContent()) {
            classAttrCalls.add(this.class_RVA_bands.numBackwardsCalls());
        }
        if (this.class_RIA_bands.hasContent()) {
            classAttrCalls.add(this.class_RIA_bands.numBackwardsCalls());
        }
        if (this.field_RVA_bands.hasContent()) {
            fieldAttrCalls.add(this.field_RVA_bands.numBackwardsCalls());
        }
        if (this.field_RIA_bands.hasContent()) {
            fieldAttrCalls.add(this.field_RIA_bands.numBackwardsCalls());
        }
        if (this.method_RVA_bands.hasContent()) {
            methodAttrCalls.add(this.method_RVA_bands.numBackwardsCalls());
        }
        if (this.method_RIA_bands.hasContent()) {
            methodAttrCalls.add(this.method_RIA_bands.numBackwardsCalls());
        }
        if (this.method_RVPA_bands.hasContent()) {
            methodAttrCalls.add(this.method_RVPA_bands.numBackwardsCalls());
        }
        if (this.method_RIPA_bands.hasContent()) {
            methodAttrCalls.add(this.method_RIPA_bands.numBackwardsCalls());
        }
        if (this.method_AD_bands.hasContent()) {
            methodAttrCalls.add(this.method_AD_bands.numBackwardsCalls());
        }
        final Comparator comparator = (arg0, arg1) -> {
            final NewAttributeBands bands2 = (NewAttributeBands)arg0;
            final NewAttributeBands bands3 = (NewAttributeBands)arg1;
            return bands2.getFlagIndex() - bands3.getFlagIndex();
        };
        Collections.sort((List<Object>)this.classAttributeBands, comparator);
        Collections.sort((List<Object>)this.methodAttributeBands, comparator);
        Collections.sort((List<Object>)this.fieldAttributeBands, comparator);
        Collections.sort((List<Object>)this.codeAttributeBands, comparator);
        for (final NewAttributeBands bands : this.classAttributeBands) {
            if (bands.isUsedAtLeastOnce()) {
                final int[] backwardsCallCounts = bands.numBackwardsCalls();
                for (int l = 0; l < backwardsCallCounts.length; ++l) {
                    classAttrCalls.add(backwardsCallCounts[l]);
                }
            }
        }
        for (final NewAttributeBands bands : this.methodAttributeBands) {
            if (bands.isUsedAtLeastOnce()) {
                final int[] backwardsCallCounts = bands.numBackwardsCalls();
                for (int l = 0; l < backwardsCallCounts.length; ++l) {
                    methodAttrCalls.add(backwardsCallCounts[l]);
                }
            }
        }
        for (final NewAttributeBands bands : this.fieldAttributeBands) {
            if (bands.isUsedAtLeastOnce()) {
                final int[] backwardsCallCounts = bands.numBackwardsCalls();
                for (int l = 0; l < backwardsCallCounts.length; ++l) {
                    fieldAttrCalls.add(backwardsCallCounts[l]);
                }
            }
        }
        for (final NewAttributeBands bands : this.codeAttributeBands) {
            if (bands.isUsedAtLeastOnce()) {
                final int[] backwardsCallCounts = bands.numBackwardsCalls();
                for (int l = 0; l < backwardsCallCounts.length; ++l) {
                    codeAttrCalls.add(backwardsCallCounts[l]);
                }
            }
        }
        this.class_attr_calls = classAttrCalls.toArray();
        this.field_attr_calls = fieldAttrCalls.toArray();
        this.method_attr_calls = methodAttrCalls.toArray();
        this.code_attr_calls = codeAttrCalls.toArray();
    }
    
    @Override
    public void pack(final OutputStream out) throws IOException, Pack200Exception {
        PackingUtils.log("Writing class bands...");
        byte[] encodedBand = this.encodeBandInt("class_this", this.getInts(this.class_this), Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_this[" + this.class_this.length + "]");
        encodedBand = this.encodeBandInt("class_super", this.getInts(this.class_super), Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_super[" + this.class_super.length + "]");
        encodedBand = this.encodeBandInt("class_interface_count", this.class_interface_count, Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_interface_count[" + this.class_interface_count.length + "]");
        final int totalInterfaces = this.sum(this.class_interface_count);
        final int[] classInterface = new int[totalInterfaces];
        int k = 0;
        for (int i = 0; i < this.class_interface.length; ++i) {
            if (this.class_interface[i] != null) {
                for (int j = 0; j < this.class_interface[i].length; ++j) {
                    final CPClass cpClass = this.class_interface[i][j];
                    classInterface[k] = cpClass.getIndex();
                    ++k;
                }
            }
        }
        encodedBand = this.encodeBandInt("class_interface", classInterface, Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_interface[" + classInterface.length + "]");
        encodedBand = this.encodeBandInt("class_field_count", this.class_field_count, Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_field_count[" + this.class_field_count.length + "]");
        encodedBand = this.encodeBandInt("class_method_count", this.class_method_count, Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_method_count[" + this.class_method_count.length + "]");
        final int totalFields = this.sum(this.class_field_count);
        final int[] fieldDescr = new int[totalFields];
        k = 0;
        for (int l = 0; l < this.index; ++l) {
            for (int m = 0; m < this.field_descr[l].length; ++m) {
                final CPNameAndType descr = this.field_descr[l][m];
                fieldDescr[k] = descr.getIndex();
                ++k;
            }
        }
        encodedBand = this.encodeBandInt("field_descr", fieldDescr, Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from field_descr[" + fieldDescr.length + "]");
        this.writeFieldAttributeBands(out);
        final int totalMethods = this.sum(this.class_method_count);
        final int[] methodDescr = new int[totalMethods];
        k = 0;
        for (int i2 = 0; i2 < this.index; ++i2) {
            for (int j2 = 0; j2 < this.method_descr[i2].length; ++j2) {
                final CPNameAndType descr2 = this.method_descr[i2][j2];
                methodDescr[k] = descr2.getIndex();
                ++k;
            }
        }
        encodedBand = this.encodeBandInt("method_descr", methodDescr, Codec.MDELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from method_descr[" + methodDescr.length + "]");
        this.writeMethodAttributeBands(out);
        this.writeClassAttributeBands(out);
        this.writeCodeBands(out);
    }
    
    private int sum(final int[] ints) {
        int sum = 0;
        for (int i = 0; i < ints.length; ++i) {
            sum += ints[i];
        }
        return sum;
    }
    
    private void writeFieldAttributeBands(final OutputStream out) throws IOException, Pack200Exception {
        byte[] encodedBand = this.encodeFlags("field_flags", this.field_flags, Codec.UNSIGNED5, Codec.UNSIGNED5, this.segmentHeader.have_field_flags_hi());
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from field_flags[" + this.field_flags.length + "]");
        encodedBand = this.encodeBandInt("field_attr_calls", this.field_attr_calls, Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from field_attr_calls[" + this.field_attr_calls.length + "]");
        encodedBand = this.encodeBandInt("fieldConstantValueKQ", this.cpEntryListToArray(this.fieldConstantValueKQ), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from fieldConstantValueKQ[" + this.fieldConstantValueKQ.size() + "]");
        encodedBand = this.encodeBandInt("fieldSignature", this.cpEntryListToArray(this.fieldSignature), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from fieldSignature[" + this.fieldSignature.size() + "]");
        this.field_RVA_bands.pack(out);
        this.field_RIA_bands.pack(out);
        for (final NewAttributeBands bands : this.fieldAttributeBands) {
            bands.pack(out);
        }
    }
    
    private void writeMethodAttributeBands(final OutputStream out) throws IOException, Pack200Exception {
        byte[] encodedBand = this.encodeFlags("method_flags", this.method_flags, Codec.UNSIGNED5, Codec.UNSIGNED5, this.segmentHeader.have_method_flags_hi());
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from method_flags[" + this.method_flags.length + "]");
        encodedBand = this.encodeBandInt("method_attr_calls", this.method_attr_calls, Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from method_attr_calls[" + this.method_attr_calls.length + "]");
        encodedBand = this.encodeBandInt("methodExceptionNumber", this.methodExceptionNumber.toArray(), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from methodExceptionNumber[" + this.methodExceptionNumber.size() + "]");
        encodedBand = this.encodeBandInt("methodExceptionClasses", this.cpEntryListToArray(this.methodExceptionClasses), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from methodExceptionClasses[" + this.methodExceptionClasses.size() + "]");
        encodedBand = this.encodeBandInt("methodSignature", this.cpEntryListToArray(this.methodSignature), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from methodSignature[" + this.methodSignature.size() + "]");
        this.method_RVA_bands.pack(out);
        this.method_RIA_bands.pack(out);
        this.method_RVPA_bands.pack(out);
        this.method_RIPA_bands.pack(out);
        this.method_AD_bands.pack(out);
        for (final NewAttributeBands bands : this.methodAttributeBands) {
            bands.pack(out);
        }
    }
    
    private void writeClassAttributeBands(final OutputStream out) throws IOException, Pack200Exception {
        byte[] encodedBand = this.encodeFlags("class_flags", this.class_flags, Codec.UNSIGNED5, Codec.UNSIGNED5, this.segmentHeader.have_class_flags_hi());
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_flags[" + this.class_flags.length + "]");
        encodedBand = this.encodeBandInt("class_attr_calls", this.class_attr_calls, Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_attr_calls[" + this.class_attr_calls.length + "]");
        encodedBand = this.encodeBandInt("classSourceFile", this.cpEntryOrNullListToArray(this.classSourceFile), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from classSourceFile[" + this.classSourceFile.size() + "]");
        encodedBand = this.encodeBandInt("class_enclosing_method_RC", this.cpEntryListToArray(this.classEnclosingMethodClass), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_enclosing_method_RC[" + this.classEnclosingMethodClass.size() + "]");
        encodedBand = this.encodeBandInt("class_EnclosingMethod_RDN", this.cpEntryOrNullListToArray(this.classEnclosingMethodDesc), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_EnclosingMethod_RDN[" + this.classEnclosingMethodDesc.size() + "]");
        encodedBand = this.encodeBandInt("class_Signature_RS", this.cpEntryListToArray(this.classSignature), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_Signature_RS[" + this.classSignature.size() + "]");
        this.class_RVA_bands.pack(out);
        this.class_RIA_bands.pack(out);
        encodedBand = this.encodeBandInt("class_InnerClasses_N", this.class_InnerClasses_N, Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_InnerClasses_N[" + this.class_InnerClasses_N.length + "]");
        encodedBand = this.encodeBandInt("class_InnerClasses_RC", this.getInts(this.class_InnerClasses_RC), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_InnerClasses_RC[" + this.class_InnerClasses_RC.length + "]");
        encodedBand = this.encodeBandInt("class_InnerClasses_F", this.class_InnerClasses_F, Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_InnerClasses_F[" + this.class_InnerClasses_F.length + "]");
        encodedBand = this.encodeBandInt("class_InnerClasses_outer_RCN", this.cpEntryOrNullListToArray(this.classInnerClassesOuterRCN), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_InnerClasses_outer_RCN[" + this.classInnerClassesOuterRCN.size() + "]");
        encodedBand = this.encodeBandInt("class_InnerClasses_name_RUN", this.cpEntryOrNullListToArray(this.classInnerClassesNameRUN), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from class_InnerClasses_name_RUN[" + this.classInnerClassesNameRUN.size() + "]");
        encodedBand = this.encodeBandInt("classFileVersionMinor", this.classFileVersionMinor.toArray(), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from classFileVersionMinor[" + this.classFileVersionMinor.size() + "]");
        encodedBand = this.encodeBandInt("classFileVersionMajor", this.classFileVersionMajor.toArray(), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from classFileVersionMajor[" + this.classFileVersionMajor.size() + "]");
        for (final NewAttributeBands bands : this.classAttributeBands) {
            bands.pack(out);
        }
    }
    
    private int[] getInts(final CPClass[] cpClasses) {
        final int[] ints = new int[cpClasses.length];
        for (int i = 0; i < ints.length; ++i) {
            if (cpClasses[i] != null) {
                ints[i] = cpClasses[i].getIndex();
            }
        }
        return ints;
    }
    
    private void writeCodeBands(final OutputStream out) throws IOException, Pack200Exception {
        byte[] encodedBand = this.encodeBandInt("codeHeaders", this.codeHeaders, Codec.BYTE1);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeHeaders[" + this.codeHeaders.length + "]");
        encodedBand = this.encodeBandInt("codeMaxStack", this.codeMaxStack.toArray(), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeMaxStack[" + this.codeMaxStack.size() + "]");
        encodedBand = this.encodeBandInt("codeMaxLocals", this.codeMaxLocals.toArray(), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeMaxLocals[" + this.codeMaxLocals.size() + "]");
        encodedBand = this.encodeBandInt("codeHandlerCount", this.codeHandlerCount.toArray(), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeHandlerCount[" + this.codeHandlerCount.size() + "]");
        encodedBand = this.encodeBandInt("codeHandlerStartP", this.integerListToArray(this.codeHandlerStartP), Codec.BCI5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeHandlerStartP[" + this.codeHandlerStartP.size() + "]");
        encodedBand = this.encodeBandInt("codeHandlerEndPO", this.integerListToArray(this.codeHandlerEndPO), Codec.BRANCH5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeHandlerEndPO[" + this.codeHandlerEndPO.size() + "]");
        encodedBand = this.encodeBandInt("codeHandlerCatchPO", this.integerListToArray(this.codeHandlerCatchPO), Codec.BRANCH5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeHandlerCatchPO[" + this.codeHandlerCatchPO.size() + "]");
        encodedBand = this.encodeBandInt("codeHandlerClass", this.cpEntryOrNullListToArray(this.codeHandlerClass), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeHandlerClass[" + this.codeHandlerClass.size() + "]");
        this.writeCodeAttributeBands(out);
    }
    
    private void writeCodeAttributeBands(final OutputStream out) throws IOException, Pack200Exception {
        byte[] encodedBand = this.encodeFlags("codeFlags", this.longListToArray(this.codeFlags), Codec.UNSIGNED5, Codec.UNSIGNED5, this.segmentHeader.have_code_flags_hi());
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from codeFlags[" + this.codeFlags.size() + "]");
        encodedBand = this.encodeBandInt("code_attr_calls", this.code_attr_calls, Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_attr_calls[" + this.code_attr_calls.length + "]");
        encodedBand = this.encodeBandInt("code_LineNumberTable_N", this.codeLineNumberTableN.toArray(), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LineNumberTable_N[" + this.codeLineNumberTableN.size() + "]");
        encodedBand = this.encodeBandInt("code_LineNumberTable_bci_P", this.integerListToArray(this.codeLineNumberTableBciP), Codec.BCI5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LineNumberTable_bci_P[" + this.codeLineNumberTableBciP.size() + "]");
        encodedBand = this.encodeBandInt("code_LineNumberTable_line", this.codeLineNumberTableLine.toArray(), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LineNumberTable_line[" + this.codeLineNumberTableLine.size() + "]");
        encodedBand = this.encodeBandInt("code_LocalVariableTable_N", this.codeLocalVariableTableN.toArray(), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTable_N[" + this.codeLocalVariableTableN.size() + "]");
        encodedBand = this.encodeBandInt("code_LocalVariableTable_bci_P", this.integerListToArray(this.codeLocalVariableTableBciP), Codec.BCI5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTable_bci_P[" + this.codeLocalVariableTableBciP.size() + "]");
        encodedBand = this.encodeBandInt("code_LocalVariableTable_span_O", this.integerListToArray(this.codeLocalVariableTableSpanO), Codec.BRANCH5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTable_span_O[" + this.codeLocalVariableTableSpanO.size() + "]");
        encodedBand = this.encodeBandInt("code_LocalVariableTable_name_RU", this.cpEntryListToArray(this.codeLocalVariableTableNameRU), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTable_name_RU[" + this.codeLocalVariableTableNameRU.size() + "]");
        encodedBand = this.encodeBandInt("code_LocalVariableTable_type_RS", this.cpEntryListToArray(this.codeLocalVariableTableTypeRS), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTable_type_RS[" + this.codeLocalVariableTableTypeRS.size() + "]");
        encodedBand = this.encodeBandInt("code_LocalVariableTable_slot", this.codeLocalVariableTableSlot.toArray(), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTable_slot[" + this.codeLocalVariableTableSlot.size() + "]");
        encodedBand = this.encodeBandInt("code_LocalVariableTypeTable_N", this.codeLocalVariableTypeTableN.toArray(), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTypeTable_N[" + this.codeLocalVariableTypeTableN.size() + "]");
        encodedBand = this.encodeBandInt("code_LocalVariableTypeTable_bci_P", this.integerListToArray(this.codeLocalVariableTypeTableBciP), Codec.BCI5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTypeTable_bci_P[" + this.codeLocalVariableTypeTableBciP.size() + "]");
        encodedBand = this.encodeBandInt("code_LocalVariableTypeTable_span_O", this.integerListToArray(this.codeLocalVariableTypeTableSpanO), Codec.BRANCH5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTypeTable_span_O[" + this.codeLocalVariableTypeTableSpanO.size() + "]");
        encodedBand = this.encodeBandInt("code_LocalVariableTypeTable_name_RU", this.cpEntryListToArray(this.codeLocalVariableTypeTableNameRU), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTypeTable_name_RU[" + this.codeLocalVariableTypeTableNameRU.size() + "]");
        encodedBand = this.encodeBandInt("code_LocalVariableTypeTable_type_RS", this.cpEntryListToArray(this.codeLocalVariableTypeTableTypeRS), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTypeTable_type_RS[" + this.codeLocalVariableTypeTableTypeRS.size() + "]");
        encodedBand = this.encodeBandInt("code_LocalVariableTypeTable_slot", this.codeLocalVariableTypeTableSlot.toArray(), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from code_LocalVariableTypeTable_slot[" + this.codeLocalVariableTypeTableSlot.size() + "]");
        for (final NewAttributeBands bands : this.codeAttributeBands) {
            bands.pack(out);
        }
    }
    
    public void addMethod(int flags, final String name, final String desc, final String signature, final String[] exceptions) {
        final CPNameAndType nt = this.cpBands.getCPNameAndType(name, desc);
        this.tempMethodDesc.add(nt);
        if (signature != null) {
            this.methodSignature.add(this.cpBands.getCPSignature(signature));
            flags |= 0x80000;
        }
        if (exceptions != null) {
            this.methodExceptionNumber.add(exceptions.length);
            for (int i = 0; i < exceptions.length; ++i) {
                this.methodExceptionClasses.add(this.cpBands.getCPClass(exceptions[i]));
            }
            flags |= 0x40000;
        }
        if ((flags & 0x20000) != 0x0) {
            flags &= 0xFFFDFFFF;
            flags |= 0x100000;
        }
        this.tempMethodFlags.add((long)flags);
        this.numMethodArgs = countArgs(desc);
        if (!this.anySyntheticMethods && (flags & 0x1000) != 0x0 && this.segment.getCurrentClassReader().hasSyntheticAttributes()) {
            this.cpBands.addCPUtf8("Synthetic");
            this.anySyntheticMethods = true;
        }
    }
    
    public void endOfMethod() {
        if (this.tempMethodRVPA != null) {
            this.method_RVPA_bands.addParameterAnnotation(this.tempMethodRVPA.numParams, this.tempMethodRVPA.annoN, this.tempMethodRVPA.pairN, this.tempMethodRVPA.typeRS, this.tempMethodRVPA.nameRU, this.tempMethodRVPA.t, this.tempMethodRVPA.values, this.tempMethodRVPA.caseArrayN, this.tempMethodRVPA.nestTypeRS, this.tempMethodRVPA.nestNameRU, this.tempMethodRVPA.nestPairN);
            this.tempMethodRVPA = null;
        }
        if (this.tempMethodRIPA != null) {
            this.method_RIPA_bands.addParameterAnnotation(this.tempMethodRIPA.numParams, this.tempMethodRIPA.annoN, this.tempMethodRIPA.pairN, this.tempMethodRIPA.typeRS, this.tempMethodRIPA.nameRU, this.tempMethodRIPA.t, this.tempMethodRIPA.values, this.tempMethodRIPA.caseArrayN, this.tempMethodRIPA.nestTypeRS, this.tempMethodRIPA.nestNameRU, this.tempMethodRIPA.nestPairN);
            this.tempMethodRIPA = null;
        }
        if (this.codeFlags.size() > 0) {
            final long latestCodeFlag = this.codeFlags.get(this.codeFlags.size() - 1);
            final int latestLocalVariableTableN = this.codeLocalVariableTableN.get(this.codeLocalVariableTableN.size() - 1);
            if (latestCodeFlag == 4L && latestLocalVariableTableN == 0) {
                this.codeLocalVariableTableN.remove(this.codeLocalVariableTableN.size() - 1);
                this.codeFlags.remove(this.codeFlags.size() - 1);
                this.codeFlags.add(0);
            }
        }
    }
    
    protected static int countArgs(final String descriptor) {
        final int bra = descriptor.indexOf(40);
        final int ket = descriptor.indexOf(41);
        if (bra == -1 || ket == -1 || ket < bra) {
            throw new IllegalArgumentException("No arguments");
        }
        boolean inType = false;
        boolean consumingNextType = false;
        int count = 0;
        for (int i = bra + 1; i < ket; ++i) {
            final char charAt = descriptor.charAt(i);
            if (inType && charAt == ';') {
                inType = false;
                consumingNextType = false;
            }
            else if (!inType && charAt == 'L') {
                inType = true;
                ++count;
            }
            else if (charAt == '[') {
                consumingNextType = true;
            }
            else if (!inType) {
                if (consumingNextType) {
                    ++count;
                    consumingNextType = false;
                }
                else if (charAt == 'D' || charAt == 'J') {
                    count += 2;
                }
                else {
                    ++count;
                }
            }
        }
        return count;
    }
    
    public void endOfClass() {
        final int numFields = this.tempFieldDesc.size();
        this.class_field_count[this.index] = numFields;
        this.field_descr[this.index] = new CPNameAndType[numFields];
        this.field_flags[this.index] = new long[numFields];
        for (int i = 0; i < numFields; ++i) {
            this.field_descr[this.index][i] = this.tempFieldDesc.get(i);
            this.field_flags[this.index][i] = this.tempFieldFlags.get(i);
        }
        final int numMethods = this.tempMethodDesc.size();
        this.class_method_count[this.index] = numMethods;
        this.method_descr[this.index] = new CPNameAndType[numMethods];
        this.method_flags[this.index] = new long[numMethods];
        for (int j = 0; j < numMethods; ++j) {
            this.method_descr[this.index][j] = this.tempMethodDesc.get(j);
            this.method_flags[this.index][j] = this.tempMethodFlags.get(j);
        }
        this.tempFieldDesc.clear();
        this.tempFieldFlags.clear();
        this.tempMethodDesc.clear();
        this.tempMethodFlags.clear();
        ++this.index;
    }
    
    public void addSourceFile(final String source) {
        String implicitSourceFileName = this.class_this[this.index].toString();
        if (implicitSourceFileName.indexOf(36) != -1) {
            implicitSourceFileName = implicitSourceFileName.substring(0, implicitSourceFileName.indexOf(36));
        }
        implicitSourceFileName = implicitSourceFileName.substring(implicitSourceFileName.lastIndexOf(47) + 1) + ".java";
        if (source.equals(implicitSourceFileName)) {
            this.classSourceFile.add(null);
        }
        else {
            this.classSourceFile.add(this.cpBands.getCPUtf8(source));
        }
        final long[] class_flags = this.class_flags;
        final int index = this.index;
        class_flags[index] |= 0x20000L;
    }
    
    public void addEnclosingMethod(final String owner, final String name, final String desc) {
        final long[] class_flags = this.class_flags;
        final int index = this.index;
        class_flags[index] |= 0x40000L;
        this.classEnclosingMethodClass.add(this.cpBands.getCPClass(owner));
        this.classEnclosingMethodDesc.add((name == null) ? null : this.cpBands.getCPNameAndType(name, desc));
    }
    
    public void addClassAttribute(final NewAttribute attribute) {
        final String attributeName = attribute.type;
        for (final NewAttributeBands bands : this.classAttributeBands) {
            if (bands.getAttributeName().equals(attributeName)) {
                bands.addAttribute(attribute);
                final int flagIndex = bands.getFlagIndex();
                final long[] class_flags = this.class_flags;
                final int index = this.index;
                class_flags[index] |= 1 << flagIndex;
                return;
            }
        }
        throw new RuntimeException("No suitable definition for " + attributeName);
    }
    
    public void addFieldAttribute(final NewAttribute attribute) {
        final String attributeName = attribute.type;
        for (final NewAttributeBands bands : this.fieldAttributeBands) {
            if (bands.getAttributeName().equals(attributeName)) {
                bands.addAttribute(attribute);
                final int flagIndex = bands.getFlagIndex();
                final Long flags = this.tempFieldFlags.remove(this.tempFieldFlags.size() - 1);
                this.tempFieldFlags.add((long)flags | (long)(1 << flagIndex));
                return;
            }
        }
        throw new RuntimeException("No suitable definition for " + attributeName);
    }
    
    public void addMethodAttribute(final NewAttribute attribute) {
        final String attributeName = attribute.type;
        for (final NewAttributeBands bands : this.methodAttributeBands) {
            if (bands.getAttributeName().equals(attributeName)) {
                bands.addAttribute(attribute);
                final int flagIndex = bands.getFlagIndex();
                final Long flags = this.tempMethodFlags.remove(this.tempMethodFlags.size() - 1);
                this.tempMethodFlags.add((long)flags | (long)(1 << flagIndex));
                return;
            }
        }
        throw new RuntimeException("No suitable definition for " + attributeName);
    }
    
    public void addCodeAttribute(final NewAttribute attribute) {
        final String attributeName = attribute.type;
        for (final NewAttributeBands bands : this.codeAttributeBands) {
            if (bands.getAttributeName().equals(attributeName)) {
                bands.addAttribute(attribute);
                final int flagIndex = bands.getFlagIndex();
                final Long flags = this.codeFlags.remove(this.codeFlags.size() - 1);
                this.codeFlags.add((long)flags | (long)(1 << flagIndex));
                return;
            }
        }
        throw new RuntimeException("No suitable definition for " + attributeName);
    }
    
    public void addMaxStack(final int maxStack, int maxLocals) {
        final Long latestFlag = this.tempMethodFlags.remove(this.tempMethodFlags.size() - 1);
        final Long newFlag = (Long)(latestFlag.intValue() | 0x20000);
        this.tempMethodFlags.add(newFlag);
        this.codeMaxStack.add(maxStack);
        if (((long)newFlag & 0x8L) == 0x0L) {
            --maxLocals;
        }
        maxLocals -= this.numMethodArgs;
        this.codeMaxLocals.add(maxLocals);
    }
    
    public void addCode() {
        this.codeHandlerCount.add(0);
        if (!this.stripDebug) {
            this.codeFlags.add(4L);
            this.codeLocalVariableTableN.add(0);
        }
    }
    
    public void addHandler(final Label start, final Label end, final Label handler, final String type) {
        final int handlers = this.codeHandlerCount.remove(this.codeHandlerCount.size() - 1);
        this.codeHandlerCount.add(handlers + 1);
        this.codeHandlerStartP.add(start);
        this.codeHandlerEndPO.add(end);
        this.codeHandlerCatchPO.add(handler);
        this.codeHandlerClass.add((type == null) ? null : this.cpBands.getCPClass(type));
    }
    
    public void addLineNumber(final int line, final Label start) {
        final Long latestCodeFlag = this.codeFlags.get(this.codeFlags.size() - 1);
        if ((latestCodeFlag.intValue() & 0x2) == 0x0) {
            this.codeFlags.remove(this.codeFlags.size() - 1);
            this.codeFlags.add((long)(latestCodeFlag.intValue() | 0x2));
            this.codeLineNumberTableN.add(1);
        }
        else {
            this.codeLineNumberTableN.increment(this.codeLineNumberTableN.size() - 1);
        }
        this.codeLineNumberTableLine.add(line);
        this.codeLineNumberTableBciP.add(start);
    }
    
    public void addLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int indx) {
        if (signature != null) {
            final Long latestCodeFlag = this.codeFlags.get(this.codeFlags.size() - 1);
            if ((latestCodeFlag.intValue() & 0x8) == 0x0) {
                this.codeFlags.remove(this.codeFlags.size() - 1);
                this.codeFlags.add((long)(latestCodeFlag.intValue() | 0x8));
                this.codeLocalVariableTypeTableN.add(1);
            }
            else {
                this.codeLocalVariableTypeTableN.increment(this.codeLocalVariableTypeTableN.size() - 1);
            }
            this.codeLocalVariableTypeTableBciP.add(start);
            this.codeLocalVariableTypeTableSpanO.add(end);
            this.codeLocalVariableTypeTableNameRU.add(this.cpBands.getCPUtf8(name));
            this.codeLocalVariableTypeTableTypeRS.add(this.cpBands.getCPSignature(signature));
            this.codeLocalVariableTypeTableSlot.add(indx);
        }
        this.codeLocalVariableTableN.increment(this.codeLocalVariableTableN.size() - 1);
        this.codeLocalVariableTableBciP.add(start);
        this.codeLocalVariableTableSpanO.add(end);
        this.codeLocalVariableTableNameRU.add(this.cpBands.getCPUtf8(name));
        this.codeLocalVariableTableTypeRS.add(this.cpBands.getCPSignature(desc));
        this.codeLocalVariableTableSlot.add(indx);
    }
    
    public void doBciRenumbering(final IntList bciRenumbering, final Map labelsToOffsets) {
        this.renumberBci(this.codeLineNumberTableBciP, bciRenumbering, labelsToOffsets);
        this.renumberBci(this.codeLocalVariableTableBciP, bciRenumbering, labelsToOffsets);
        this.renumberOffsetBci(this.codeLocalVariableTableBciP, this.codeLocalVariableTableSpanO, bciRenumbering, labelsToOffsets);
        this.renumberBci(this.codeLocalVariableTypeTableBciP, bciRenumbering, labelsToOffsets);
        this.renumberOffsetBci(this.codeLocalVariableTypeTableBciP, this.codeLocalVariableTypeTableSpanO, bciRenumbering, labelsToOffsets);
        this.renumberBci(this.codeHandlerStartP, bciRenumbering, labelsToOffsets);
        this.renumberOffsetBci(this.codeHandlerStartP, this.codeHandlerEndPO, bciRenumbering, labelsToOffsets);
        this.renumberDoubleOffsetBci(this.codeHandlerStartP, this.codeHandlerEndPO, this.codeHandlerCatchPO, bciRenumbering, labelsToOffsets);
        for (final NewAttributeBands newAttributeBandSet : this.classAttributeBands) {
            newAttributeBandSet.renumberBci(bciRenumbering, labelsToOffsets);
        }
        for (final NewAttributeBands newAttributeBandSet : this.methodAttributeBands) {
            newAttributeBandSet.renumberBci(bciRenumbering, labelsToOffsets);
        }
        for (final NewAttributeBands newAttributeBandSet : this.fieldAttributeBands) {
            newAttributeBandSet.renumberBci(bciRenumbering, labelsToOffsets);
        }
        for (final NewAttributeBands newAttributeBandSet : this.codeAttributeBands) {
            newAttributeBandSet.renumberBci(bciRenumbering, labelsToOffsets);
        }
    }
    
    private void renumberBci(final List list, final IntList bciRenumbering, final Map labelsToOffsets) {
        for (int i = list.size() - 1; i >= 0; --i) {
            final Object label = list.get(i);
            if (label instanceof Integer) {
                break;
            }
            if (label instanceof Label) {
                list.remove(i);
                final Integer bytecodeIndex = labelsToOffsets.get(label);
                list.add(i, bciRenumbering.get(bytecodeIndex));
            }
        }
    }
    
    private void renumberOffsetBci(final List relative, final List list, final IntList bciRenumbering, final Map labelsToOffsets) {
        for (int i = list.size() - 1; i >= 0; --i) {
            final Object label = list.get(i);
            if (label instanceof Integer) {
                break;
            }
            if (label instanceof Label) {
                list.remove(i);
                final Integer bytecodeIndex = labelsToOffsets.get(label);
                final Integer renumberedOffset = bciRenumbering.get(bytecodeIndex) - relative.get(i);
                list.add(i, renumberedOffset);
            }
        }
    }
    
    private void renumberDoubleOffsetBci(final List relative, final List firstOffset, final List list, final IntList bciRenumbering, final Map labelsToOffsets) {
        for (int i = list.size() - 1; i >= 0; --i) {
            final Object label = list.get(i);
            if (label instanceof Integer) {
                break;
            }
            if (label instanceof Label) {
                list.remove(i);
                final Integer bytecodeIndex = labelsToOffsets.get(label);
                final Integer renumberedOffset = bciRenumbering.get(bytecodeIndex) - relative.get(i) - firstOffset.get(i);
                list.add(i, renumberedOffset);
            }
        }
    }
    
    public boolean isAnySyntheticClasses() {
        return this.anySyntheticClasses;
    }
    
    public boolean isAnySyntheticFields() {
        return this.anySyntheticFields;
    }
    
    public boolean isAnySyntheticMethods() {
        return this.anySyntheticMethods;
    }
    
    public void addParameterAnnotation(final int parameter, final String desc, final boolean visible, final List nameRU, final List t, final List values, final List caseArrayN, final List nestTypeRS, final List nestNameRU, final List nestPairN) {
        if (visible) {
            if (this.tempMethodRVPA == null) {
                (this.tempMethodRVPA = new TempParamAnnotation(this.numMethodArgs)).addParameterAnnotation(parameter, desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
            }
            final Long flag = this.tempMethodFlags.remove(this.tempMethodFlags.size() - 1);
            this.tempMethodFlags.add((long)flag | 0x800000L);
        }
        else {
            if (this.tempMethodRIPA == null) {
                (this.tempMethodRIPA = new TempParamAnnotation(this.numMethodArgs)).addParameterAnnotation(parameter, desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
            }
            final Long flag = this.tempMethodFlags.remove(this.tempMethodFlags.size() - 1);
            this.tempMethodFlags.add((long)flag | 0x1000000L);
        }
    }
    
    public void addAnnotation(final int context, final String desc, final boolean visible, final List nameRU, final List t, final List values, final List caseArrayN, final List nestTypeRS, final List nestNameRU, final List nestPairN) {
        switch (context) {
            case 0: {
                if (visible) {
                    this.class_RVA_bands.addAnnotation(desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
                    if ((this.class_flags[this.index] & 0x200000L) != 0x0L) {
                        this.class_RVA_bands.incrementAnnoN();
                        break;
                    }
                    this.class_RVA_bands.newEntryInAnnoN();
                    this.class_flags[this.index] |= 0x200000L;
                    break;
                }
                else {
                    this.class_RIA_bands.addAnnotation(desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
                    if ((this.class_flags[this.index] & 0x400000L) != 0x0L) {
                        this.class_RIA_bands.incrementAnnoN();
                        break;
                    }
                    this.class_RIA_bands.newEntryInAnnoN();
                    this.class_flags[this.index] |= 0x400000L;
                    break;
                }
                break;
            }
            case 1: {
                if (visible) {
                    this.field_RVA_bands.addAnnotation(desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
                    final Long flag = this.tempFieldFlags.remove(this.tempFieldFlags.size() - 1);
                    if ((flag.intValue() & 0x200000) != 0x0) {
                        this.field_RVA_bands.incrementAnnoN();
                    }
                    else {
                        this.field_RVA_bands.newEntryInAnnoN();
                    }
                    this.tempFieldFlags.add((long)(flag.intValue() | 0x200000));
                    break;
                }
                this.field_RIA_bands.addAnnotation(desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
                final Long flag = this.tempFieldFlags.remove(this.tempFieldFlags.size() - 1);
                if ((flag.intValue() & 0x400000) != 0x0) {
                    this.field_RIA_bands.incrementAnnoN();
                }
                else {
                    this.field_RIA_bands.newEntryInAnnoN();
                }
                this.tempFieldFlags.add((long)(flag.intValue() | 0x400000));
                break;
            }
            case 2: {
                if (visible) {
                    this.method_RVA_bands.addAnnotation(desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
                    final Long flag = this.tempMethodFlags.remove(this.tempMethodFlags.size() - 1);
                    if ((flag.intValue() & 0x200000) != 0x0) {
                        this.method_RVA_bands.incrementAnnoN();
                    }
                    else {
                        this.method_RVA_bands.newEntryInAnnoN();
                    }
                    this.tempMethodFlags.add((long)(flag.intValue() | 0x200000));
                    break;
                }
                this.method_RIA_bands.addAnnotation(desc, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
                final Long flag = this.tempMethodFlags.remove(this.tempMethodFlags.size() - 1);
                if ((flag.intValue() & 0x400000) != 0x0) {
                    this.method_RIA_bands.incrementAnnoN();
                }
                else {
                    this.method_RIA_bands.newEntryInAnnoN();
                }
                this.tempMethodFlags.add((long)(flag.intValue() | 0x400000));
                break;
            }
        }
    }
    
    public void addAnnotationDefault(final List nameRU, final List t, final List values, final List caseArrayN, final List nestTypeRS, final List nestNameRU, final List nestPairN) {
        this.method_AD_bands.addAnnotation(null, nameRU, t, values, caseArrayN, nestTypeRS, nestNameRU, nestPairN);
        final Long flag = this.tempMethodFlags.remove(this.tempMethodFlags.size() - 1);
        this.tempMethodFlags.add((long)flag | 0x2000000L);
    }
    
    public void removeCurrentClass() {
        if ((this.class_flags[this.index] & 0x20000L) != 0x0L) {
            this.classSourceFile.remove(this.classSourceFile.size() - 1);
        }
        if ((this.class_flags[this.index] & 0x40000L) != 0x0L) {
            this.classEnclosingMethodClass.remove(this.classEnclosingMethodClass.size() - 1);
            this.classEnclosingMethodDesc.remove(this.classEnclosingMethodDesc.size() - 1);
        }
        if ((this.class_flags[this.index] & 0x80000L) != 0x0L) {
            this.classSignature.remove(this.classSignature.size() - 1);
        }
        if ((this.class_flags[this.index] & 0x200000L) != 0x0L) {
            this.class_RVA_bands.removeLatest();
        }
        if ((this.class_flags[this.index] & 0x400000L) != 0x0L) {
            this.class_RIA_bands.removeLatest();
        }
        for (final Long flagsL : this.tempFieldFlags) {
            final long flags = flagsL;
            if ((flags & 0x80000L) != 0x0L) {
                this.fieldSignature.remove(this.fieldSignature.size() - 1);
            }
            if ((flags & 0x20000L) != 0x0L) {
                this.fieldConstantValueKQ.remove(this.fieldConstantValueKQ.size() - 1);
            }
            if ((flags & 0x200000L) != 0x0L) {
                this.field_RVA_bands.removeLatest();
            }
            if ((flags & 0x400000L) != 0x0L) {
                this.field_RIA_bands.removeLatest();
            }
        }
        for (final Long flagsL : this.tempMethodFlags) {
            final long flags = flagsL;
            if ((flags & 0x80000L) != 0x0L) {
                this.methodSignature.remove(this.methodSignature.size() - 1);
            }
            if ((flags & 0x40000L) != 0x0L) {
                for (int exceptions = this.methodExceptionNumber.remove(this.methodExceptionNumber.size() - 1), i = 0; i < exceptions; ++i) {
                    this.methodExceptionClasses.remove(this.methodExceptionClasses.size() - 1);
                }
            }
            if ((flags & 0x20000L) != 0x0L) {
                this.codeMaxLocals.remove(this.codeMaxLocals.size() - 1);
                this.codeMaxStack.remove(this.codeMaxStack.size() - 1);
                for (int handlers = this.codeHandlerCount.remove(this.codeHandlerCount.size() - 1), i = 0; i < handlers; ++i) {
                    final int index = this.codeHandlerStartP.size() - 1;
                    this.codeHandlerStartP.remove(index);
                    this.codeHandlerEndPO.remove(index);
                    this.codeHandlerCatchPO.remove(index);
                    this.codeHandlerClass.remove(index);
                }
                if (!this.stripDebug) {
                    final long cdeFlags = this.codeFlags.remove(this.codeFlags.size() - 1);
                    for (int numLocalVariables = this.codeLocalVariableTableN.remove(this.codeLocalVariableTableN.size() - 1), j = 0; j < numLocalVariables; ++j) {
                        final int location = this.codeLocalVariableTableBciP.size() - 1;
                        this.codeLocalVariableTableBciP.remove(location);
                        this.codeLocalVariableTableSpanO.remove(location);
                        this.codeLocalVariableTableNameRU.remove(location);
                        this.codeLocalVariableTableTypeRS.remove(location);
                        this.codeLocalVariableTableSlot.remove(location);
                    }
                    if ((cdeFlags & 0x8L) != 0x0L) {
                        for (int numLocalVariablesInTypeTable = this.codeLocalVariableTypeTableN.remove(this.codeLocalVariableTypeTableN.size() - 1), k = 0; k < numLocalVariablesInTypeTable; ++k) {
                            final int location2 = this.codeLocalVariableTypeTableBciP.size() - 1;
                            this.codeLocalVariableTypeTableBciP.remove(location2);
                            this.codeLocalVariableTypeTableSpanO.remove(location2);
                            this.codeLocalVariableTypeTableNameRU.remove(location2);
                            this.codeLocalVariableTypeTableTypeRS.remove(location2);
                            this.codeLocalVariableTypeTableSlot.remove(location2);
                        }
                    }
                    if ((cdeFlags & 0x2L) != 0x0L) {
                        for (int numLineNumbers = this.codeLineNumberTableN.remove(this.codeLineNumberTableN.size() - 1), k = 0; k < numLineNumbers; ++k) {
                            final int location2 = this.codeLineNumberTableBciP.size() - 1;
                            this.codeLineNumberTableBciP.remove(location2);
                            this.codeLineNumberTableLine.remove(location2);
                        }
                    }
                }
            }
            if ((flags & 0x200000L) != 0x0L) {
                this.method_RVA_bands.removeLatest();
            }
            if ((flags & 0x400000L) != 0x0L) {
                this.method_RIA_bands.removeLatest();
            }
            if ((flags & 0x800000L) != 0x0L) {
                this.method_RVPA_bands.removeLatest();
            }
            if ((flags & 0x1000000L) != 0x0L) {
                this.method_RIPA_bands.removeLatest();
            }
            if ((flags & 0x2000000L) != 0x0L) {
                this.method_AD_bands.removeLatest();
            }
        }
        this.class_this[this.index] = null;
        this.class_super[this.index] = null;
        this.class_interface_count[this.index] = 0;
        this.class_interface[this.index] = null;
        this.major_versions[this.index] = 0;
        this.class_flags[this.index] = 0L;
        this.tempFieldDesc.clear();
        this.tempFieldFlags.clear();
        this.tempMethodDesc.clear();
        this.tempMethodFlags.clear();
        if (this.index > 0) {
            --this.index;
        }
    }
    
    public int numClassesProcessed() {
        return this.index;
    }
    
    private static class TempParamAnnotation
    {
        int numParams;
        int[] annoN;
        IntList pairN;
        List typeRS;
        List nameRU;
        List t;
        List values;
        List caseArrayN;
        List nestTypeRS;
        List nestNameRU;
        List nestPairN;
        
        public TempParamAnnotation(final int numParams) {
            this.pairN = new IntList();
            this.typeRS = new ArrayList();
            this.nameRU = new ArrayList();
            this.t = new ArrayList();
            this.values = new ArrayList();
            this.caseArrayN = new ArrayList();
            this.nestTypeRS = new ArrayList();
            this.nestNameRU = new ArrayList();
            this.nestPairN = new ArrayList();
            this.numParams = numParams;
            this.annoN = new int[numParams];
        }
        
        public void addParameterAnnotation(final int parameter, final String desc, final List nameRU, final List t, final List values, final List caseArrayN, final List nestTypeRS, final List nestNameRU, final List nestPairN) {
            final int[] annoN = this.annoN;
            ++annoN[parameter];
            this.typeRS.add(desc);
            this.pairN.add(nameRU.size());
            this.nameRU.addAll(nameRU);
            this.t.addAll(t);
            this.values.addAll(values);
            this.caseArrayN.addAll(caseArrayN);
            this.nestTypeRS.addAll(nestTypeRS);
            this.nestNameRU.addAll(nestNameRU);
            this.nestPairN.addAll(nestPairN);
        }
    }
}
