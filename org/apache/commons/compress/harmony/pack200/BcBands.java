package org.apache.commons.compress.harmony.pack200;

import org.objectweb.asm.Label;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class BcBands extends BandSet
{
    private final CpBands cpBands;
    private final Segment segment;
    private final IntList bcCodes;
    private final IntList bcCaseCount;
    private final IntList bcCaseValue;
    private final IntList bcByte;
    private final IntList bcShort;
    private final IntList bcLocal;
    private final List bcLabel;
    private final List bcIntref;
    private final List bcFloatRef;
    private final List bcLongRef;
    private final List bcDoubleRef;
    private final List bcStringRef;
    private final List bcClassRef;
    private final List bcFieldRef;
    private final List bcMethodRef;
    private final List bcIMethodRef;
    private List bcThisField;
    private final List bcSuperField;
    private List bcThisMethod;
    private List bcSuperMethod;
    private List bcInitRef;
    private String currentClass;
    private String superClass;
    private String currentNewClass;
    private static final int MULTIANEWARRAY = 197;
    private static final int ALOAD_0 = 42;
    private static final int WIDE = 196;
    private static final int INVOKEINTERFACE = 185;
    private static final int TABLESWITCH = 170;
    private static final int IINC = 132;
    private static final int LOOKUPSWITCH = 171;
    private static final int endMarker = 255;
    private final IntList bciRenumbering;
    private final Map labelsToOffsets;
    private int byteCodeOffset;
    private int renumberedOffset;
    private final IntList bcLabelRelativeOffsets;
    
    public BcBands(final CpBands cpBands, final Segment segment, final int effort) {
        super(effort, segment.getSegmentHeader());
        this.bcCodes = new IntList();
        this.bcCaseCount = new IntList();
        this.bcCaseValue = new IntList();
        this.bcByte = new IntList();
        this.bcShort = new IntList();
        this.bcLocal = new IntList();
        this.bcLabel = new ArrayList();
        this.bcIntref = new ArrayList();
        this.bcFloatRef = new ArrayList();
        this.bcLongRef = new ArrayList();
        this.bcDoubleRef = new ArrayList();
        this.bcStringRef = new ArrayList();
        this.bcClassRef = new ArrayList();
        this.bcFieldRef = new ArrayList();
        this.bcMethodRef = new ArrayList();
        this.bcIMethodRef = new ArrayList();
        this.bcThisField = new ArrayList();
        this.bcSuperField = new ArrayList();
        this.bcThisMethod = new ArrayList();
        this.bcSuperMethod = new ArrayList();
        this.bcInitRef = new ArrayList();
        this.bciRenumbering = new IntList();
        this.labelsToOffsets = new HashMap();
        this.bcLabelRelativeOffsets = new IntList();
        this.cpBands = cpBands;
        this.segment = segment;
    }
    
    public void setCurrentClass(final String name, final String superName) {
        this.currentClass = name;
        this.superClass = superName;
    }
    
    public void finaliseBands() {
        this.bcThisField = this.getIndexInClass(this.bcThisField);
        this.bcThisMethod = this.getIndexInClass(this.bcThisMethod);
        this.bcSuperMethod = this.getIndexInClass(this.bcSuperMethod);
        this.bcInitRef = this.getIndexInClassForConstructor(this.bcInitRef);
    }
    
    @Override
    public void pack(final OutputStream out) throws IOException, Pack200Exception {
        PackingUtils.log("Writing byte code bands...");
        byte[] encodedBand = this.encodeBandInt("bcCodes", this.bcCodes.toArray(), Codec.BYTE1);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcCodes[" + this.bcCodes.size() + "]");
        encodedBand = this.encodeBandInt("bcCaseCount", this.bcCaseCount.toArray(), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcCaseCount[" + this.bcCaseCount.size() + "]");
        encodedBand = this.encodeBandInt("bcCaseValue", this.bcCaseValue.toArray(), Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcCaseValue[" + this.bcCaseValue.size() + "]");
        encodedBand = this.encodeBandInt("bcByte", this.bcByte.toArray(), Codec.BYTE1);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcByte[" + this.bcByte.size() + "]");
        encodedBand = this.encodeBandInt("bcShort", this.bcShort.toArray(), Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcShort[" + this.bcShort.size() + "]");
        encodedBand = this.encodeBandInt("bcLocal", this.bcLocal.toArray(), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcLocal[" + this.bcLocal.size() + "]");
        encodedBand = this.encodeBandInt("bcLabel", this.integerListToArray(this.bcLabel), Codec.BRANCH5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcLabel[" + this.bcLabel.size() + "]");
        encodedBand = this.encodeBandInt("bcIntref", this.cpEntryListToArray(this.bcIntref), Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcIntref[" + this.bcIntref.size() + "]");
        encodedBand = this.encodeBandInt("bcFloatRef", this.cpEntryListToArray(this.bcFloatRef), Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcFloatRef[" + this.bcFloatRef.size() + "]");
        encodedBand = this.encodeBandInt("bcLongRef", this.cpEntryListToArray(this.bcLongRef), Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcLongRef[" + this.bcLongRef.size() + "]");
        encodedBand = this.encodeBandInt("bcDoubleRef", this.cpEntryListToArray(this.bcDoubleRef), Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcDoubleRef[" + this.bcDoubleRef.size() + "]");
        encodedBand = this.encodeBandInt("bcStringRef", this.cpEntryListToArray(this.bcStringRef), Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcStringRef[" + this.bcStringRef.size() + "]");
        encodedBand = this.encodeBandInt("bcClassRef", this.cpEntryOrNullListToArray(this.bcClassRef), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcClassRef[" + this.bcClassRef.size() + "]");
        encodedBand = this.encodeBandInt("bcFieldRef", this.cpEntryListToArray(this.bcFieldRef), Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcFieldRef[" + this.bcFieldRef.size() + "]");
        encodedBand = this.encodeBandInt("bcMethodRef", this.cpEntryListToArray(this.bcMethodRef), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcMethodRef[" + this.bcMethodRef.size() + "]");
        encodedBand = this.encodeBandInt("bcIMethodRef", this.cpEntryListToArray(this.bcIMethodRef), Codec.DELTA5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcIMethodRef[" + this.bcIMethodRef.size() + "]");
        encodedBand = this.encodeBandInt("bcThisField", this.integerListToArray(this.bcThisField), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcThisField[" + this.bcThisField.size() + "]");
        encodedBand = this.encodeBandInt("bcSuperField", this.integerListToArray(this.bcSuperField), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcSuperField[" + this.bcSuperField.size() + "]");
        encodedBand = this.encodeBandInt("bcThisMethod", this.integerListToArray(this.bcThisMethod), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcThisMethod[" + this.bcThisMethod.size() + "]");
        encodedBand = this.encodeBandInt("bcSuperMethod", this.integerListToArray(this.bcSuperMethod), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcSuperMethod[" + this.bcSuperMethod.size() + "]");
        encodedBand = this.encodeBandInt("bcInitRef", this.integerListToArray(this.bcInitRef), Codec.UNSIGNED5);
        out.write(encodedBand);
        PackingUtils.log("Wrote " + encodedBand.length + " bytes from bcInitRef[" + this.bcInitRef.size() + "]");
    }
    
    private List getIndexInClass(final List cPMethodOrFieldList) {
        final List indices = new ArrayList(cPMethodOrFieldList.size());
        for (int i = 0; i < cPMethodOrFieldList.size(); ++i) {
            final CPMethodOrField cpMF = cPMethodOrFieldList.get(i);
            indices.add(cpMF.getIndexInClass());
        }
        return indices;
    }
    
    private List getIndexInClassForConstructor(final List cPMethodList) {
        final List indices = new ArrayList(cPMethodList.size());
        for (int i = 0; i < cPMethodList.size(); ++i) {
            final CPMethodOrField cpMF = cPMethodList.get(i);
            indices.add(cpMF.getIndexInClassForConstructor());
        }
        return indices;
    }
    
    public void visitEnd() {
        for (int i = 0; i < this.bciRenumbering.size(); ++i) {
            if (this.bciRenumbering.get(i) == -1) {
                this.bciRenumbering.remove(i);
                this.bciRenumbering.add(i, ++this.renumberedOffset);
            }
        }
        if (this.renumberedOffset != 0) {
            if (this.renumberedOffset + 1 != this.bciRenumbering.size()) {
                throw new RuntimeException("Mistake made with renumbering");
            }
            for (int i = this.bcLabel.size() - 1; i >= 0; --i) {
                final Object label = this.bcLabel.get(i);
                if (label instanceof Integer) {
                    break;
                }
                if (label instanceof Label) {
                    this.bcLabel.remove(i);
                    final Integer offset = this.labelsToOffsets.get(label);
                    final int relativeOffset = this.bcLabelRelativeOffsets.get(i);
                    this.bcLabel.add(i, this.bciRenumbering.get(offset) - this.bciRenumbering.get(relativeOffset));
                }
            }
            this.bcCodes.add(255);
            this.segment.getClassBands().doBciRenumbering(this.bciRenumbering, this.labelsToOffsets);
            this.bciRenumbering.clear();
            this.labelsToOffsets.clear();
            this.byteCodeOffset = 0;
            this.renumberedOffset = 0;
        }
    }
    
    public void visitLabel(final Label label) {
        this.labelsToOffsets.put(label, this.byteCodeOffset);
    }
    
    public void visitFieldInsn(int opcode, final String owner, final String name, final String desc) {
        this.byteCodeOffset += 3;
        this.updateRenumbering();
        boolean aload_0 = false;
        if (this.bcCodes.size() > 0 && this.bcCodes.get(this.bcCodes.size() - 1) == 42) {
            this.bcCodes.remove(this.bcCodes.size() - 1);
            aload_0 = true;
        }
        final CPMethodOrField cpField = this.cpBands.getCPField(owner, name, desc);
        if (aload_0) {
            opcode += 7;
        }
        if (owner.equals(this.currentClass)) {
            opcode += 24;
            this.bcThisField.add(cpField);
        }
        else {
            if (aload_0) {
                opcode -= 7;
                this.bcCodes.add(42);
            }
            this.bcFieldRef.add(cpField);
        }
        aload_0 = false;
        this.bcCodes.add(opcode);
    }
    
    private void updateRenumbering() {
        if (this.bciRenumbering.isEmpty()) {
            this.bciRenumbering.add(0);
        }
        ++this.renumberedOffset;
        for (int i = this.bciRenumbering.size(); i < this.byteCodeOffset; ++i) {
            this.bciRenumbering.add(-1);
        }
        this.bciRenumbering.add(this.renumberedOffset);
    }
    
    public void visitIincInsn(final int var, final int increment) {
        if (var > 255 || increment > 255) {
            this.byteCodeOffset += 6;
            this.bcCodes.add(196);
            this.bcCodes.add(132);
            this.bcLocal.add(var);
            this.bcShort.add(increment);
        }
        else {
            this.byteCodeOffset += 3;
            this.bcCodes.add(132);
            this.bcLocal.add(var);
            this.bcByte.add(increment & 0xFF);
        }
        this.updateRenumbering();
    }
    
    public void visitInsn(final int opcode) {
        if (opcode >= 202) {
            throw new RuntimeException("Non-standard bytecode instructions not supported");
        }
        this.bcCodes.add(opcode);
        ++this.byteCodeOffset;
        this.updateRenumbering();
    }
    
    public void visitIntInsn(final int opcode, final int operand) {
        switch (opcode) {
            case 17: {
                this.bcCodes.add(opcode);
                this.bcShort.add(operand);
                this.byteCodeOffset += 3;
                break;
            }
            case 16:
            case 188: {
                this.bcCodes.add(opcode);
                this.bcByte.add(operand & 0xFF);
                this.byteCodeOffset += 2;
                break;
            }
        }
        this.updateRenumbering();
    }
    
    public void visitJumpInsn(final int opcode, final Label label) {
        this.bcCodes.add(opcode);
        this.bcLabel.add(label);
        this.bcLabelRelativeOffsets.add(this.byteCodeOffset);
        this.byteCodeOffset += 3;
        this.updateRenumbering();
    }
    
    public void visitLdcInsn(final Object cst) {
        final CPConstant constant = this.cpBands.getConstant(cst);
        if (this.segment.lastConstantHadWideIndex() || constant instanceof CPLong || constant instanceof CPDouble) {
            this.byteCodeOffset += 3;
            if (constant instanceof CPInt) {
                this.bcCodes.add(237);
                this.bcIntref.add(constant);
            }
            else if (constant instanceof CPFloat) {
                this.bcCodes.add(238);
                this.bcFloatRef.add(constant);
            }
            else if (constant instanceof CPLong) {
                this.bcCodes.add(20);
                this.bcLongRef.add(constant);
            }
            else if (constant instanceof CPDouble) {
                this.bcCodes.add(239);
                this.bcDoubleRef.add(constant);
            }
            else if (constant instanceof CPString) {
                this.bcCodes.add(19);
                this.bcStringRef.add(constant);
            }
            else {
                if (!(constant instanceof CPClass)) {
                    throw new RuntimeException("Constant should not be null");
                }
                this.bcCodes.add(236);
                this.bcClassRef.add(constant);
            }
        }
        else {
            this.byteCodeOffset += 2;
            if (constant instanceof CPInt) {
                this.bcCodes.add(234);
                this.bcIntref.add(constant);
            }
            else if (constant instanceof CPFloat) {
                this.bcCodes.add(235);
                this.bcFloatRef.add(constant);
            }
            else if (constant instanceof CPString) {
                this.bcCodes.add(18);
                this.bcStringRef.add(constant);
            }
            else if (constant instanceof CPClass) {
                this.bcCodes.add(233);
                this.bcClassRef.add(constant);
            }
        }
        this.updateRenumbering();
    }
    
    public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
        this.bcCodes.add(171);
        this.bcLabel.add(dflt);
        this.bcLabelRelativeOffsets.add(this.byteCodeOffset);
        this.bcCaseCount.add(keys.length);
        for (int i = 0; i < labels.length; ++i) {
            this.bcCaseValue.add(keys[i]);
            this.bcLabel.add(labels[i]);
            this.bcLabelRelativeOffsets.add(this.byteCodeOffset);
        }
        final int padding = ((this.byteCodeOffset + 1) % 4 == 0) ? 0 : (4 - (this.byteCodeOffset + 1) % 4);
        this.byteCodeOffset += 1 + padding + 8 + 8 * keys.length;
        this.updateRenumbering();
    }
    
    public void visitMethodInsn(int opcode, final String owner, final String name, final String desc) {
        this.byteCodeOffset += 3;
        switch (opcode) {
            case 182:
            case 183:
            case 184: {
                boolean aload_0 = false;
                if (this.bcCodes.size() > 0 && this.bcCodes.get(this.bcCodes.size() - 1) == 42) {
                    this.bcCodes.remove(this.bcCodes.size() - 1);
                    aload_0 = true;
                    opcode += 7;
                }
                if (owner.equals(this.currentClass)) {
                    opcode += 24;
                    if (name.equals("<init>") && opcode == 207) {
                        opcode = 230;
                        this.bcInitRef.add(this.cpBands.getCPMethod(owner, name, desc));
                    }
                    else {
                        this.bcThisMethod.add(this.cpBands.getCPMethod(owner, name, desc));
                    }
                }
                else if (owner.equals(this.superClass)) {
                    opcode += 38;
                    if (name.equals("<init>") && opcode == 221) {
                        opcode = 231;
                        this.bcInitRef.add(this.cpBands.getCPMethod(owner, name, desc));
                    }
                    else {
                        this.bcSuperMethod.add(this.cpBands.getCPMethod(owner, name, desc));
                    }
                }
                else {
                    if (aload_0) {
                        opcode -= 7;
                        this.bcCodes.add(42);
                    }
                    if (name.equals("<init>") && opcode == 183 && owner.equals(this.currentNewClass)) {
                        opcode = 232;
                        this.bcInitRef.add(this.cpBands.getCPMethod(owner, name, desc));
                    }
                    else {
                        this.bcMethodRef.add(this.cpBands.getCPMethod(owner, name, desc));
                    }
                }
                this.bcCodes.add(opcode);
                break;
            }
            case 185: {
                this.byteCodeOffset += 2;
                final CPMethodOrField cpIMethod = this.cpBands.getCPIMethod(owner, name, desc);
                this.bcIMethodRef.add(cpIMethod);
                this.bcCodes.add(185);
                break;
            }
        }
        this.updateRenumbering();
    }
    
    public void visitMultiANewArrayInsn(final String desc, final int dimensions) {
        this.byteCodeOffset += 4;
        this.updateRenumbering();
        this.bcCodes.add(197);
        this.bcClassRef.add(this.cpBands.getCPClass(desc));
        this.bcByte.add(dimensions & 0xFF);
    }
    
    public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label[] labels) {
        this.bcCodes.add(170);
        this.bcLabel.add(dflt);
        this.bcLabelRelativeOffsets.add(this.byteCodeOffset);
        this.bcCaseValue.add(min);
        final int count = labels.length;
        this.bcCaseCount.add(count);
        for (int i = 0; i < count; ++i) {
            this.bcLabel.add(labels[i]);
            this.bcLabelRelativeOffsets.add(this.byteCodeOffset);
        }
        final int padding = (this.byteCodeOffset % 4 == 0) ? 0 : (4 - this.byteCodeOffset % 4);
        this.byteCodeOffset += padding + 12 + 4 * labels.length;
        this.updateRenumbering();
    }
    
    public void visitTypeInsn(final int opcode, final String type) {
        this.byteCodeOffset += 3;
        this.updateRenumbering();
        this.bcCodes.add(opcode);
        this.bcClassRef.add(this.cpBands.getCPClass(type));
        if (opcode == 187) {
            this.currentNewClass = type;
        }
    }
    
    public void visitVarInsn(final int opcode, final int var) {
        if (var > 255) {
            this.byteCodeOffset += 4;
            this.bcCodes.add(196);
            this.bcCodes.add(opcode);
            this.bcLocal.add(var);
        }
        else if (var > 3 || opcode == 169) {
            this.byteCodeOffset += 2;
            this.bcCodes.add(opcode);
            this.bcLocal.add(var);
        }
        else {
            ++this.byteCodeOffset;
            switch (opcode) {
                case 21:
                case 54: {
                    this.bcCodes.add(opcode + 5 + var);
                    break;
                }
                case 22:
                case 55: {
                    this.bcCodes.add(opcode + 8 + var);
                    break;
                }
                case 23:
                case 56: {
                    this.bcCodes.add(opcode + 11 + var);
                    break;
                }
                case 24:
                case 57: {
                    this.bcCodes.add(opcode + 14 + var);
                    break;
                }
                case 25:
                case 58: {
                    this.bcCodes.add(opcode + 17 + var);
                    break;
                }
            }
        }
        this.updateRenumbering();
    }
}
