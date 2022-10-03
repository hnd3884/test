package org.apache.commons.compress.harmony.unpack200;

import org.apache.commons.compress.harmony.unpack200.bytecode.CPClass;
import org.apache.commons.compress.harmony.unpack200.bytecode.BCIRenumberedAttribute;
import java.util.Collections;
import org.apache.commons.compress.harmony.unpack200.bytecode.NewAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.Attribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.CodeAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.ExceptionTableEntry;
import org.apache.commons.compress.harmony.unpack200.bytecode.OperandManager;
import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import java.io.IOException;
import org.apache.commons.compress.harmony.pack200.Codec;
import org.apache.commons.compress.harmony.unpack200.bytecode.ByteCode;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.io.InputStream;
import java.util.List;

public class BcBands extends BandSet
{
    private byte[][][] methodByteCodePacked;
    private int[] bcCaseCount;
    private int[] bcCaseValue;
    private int[] bcByte;
    private int[] bcLocal;
    private int[] bcShort;
    private int[] bcLabel;
    private int[] bcIntRef;
    private int[] bcFloatRef;
    private int[] bcLongRef;
    private int[] bcDoubleRef;
    private int[] bcStringRef;
    private int[] bcClassRef;
    private int[] bcFieldRef;
    private int[] bcMethodRef;
    private int[] bcIMethodRef;
    private int[] bcThisField;
    private int[] bcSuperField;
    private int[] bcThisMethod;
    private int[] bcSuperMethod;
    private int[] bcInitRef;
    private int[] bcEscRef;
    private int[] bcEscRefSize;
    private int[] bcEscSize;
    private int[][] bcEscByte;
    private List wideByteCodes;
    
    public BcBands(final Segment segment) {
        super(segment);
    }
    
    @Override
    public void read(final InputStream in) throws IOException, Pack200Exception {
        final AttributeLayoutMap attributeDefinitionMap = this.segment.getAttrDefinitionBands().getAttributeDefinitionMap();
        final int classCount = this.header.getClassCount();
        final long[][] methodFlags = this.segment.getClassBands().getMethodFlags();
        int bcCaseCountCount = 0;
        int bcByteCount = 0;
        int bcShortCount = 0;
        int bcLocalCount = 0;
        int bcLabelCount = 0;
        int bcIntRefCount = 0;
        int bcFloatRefCount = 0;
        int bcLongRefCount = 0;
        int bcDoubleRefCount = 0;
        int bcStringRefCount = 0;
        int bcClassRefCount = 0;
        int bcFieldRefCount = 0;
        int bcMethodRefCount = 0;
        int bcIMethodRefCount = 0;
        int bcThisFieldCount = 0;
        int bcSuperFieldCount = 0;
        int bcThisMethodCount = 0;
        int bcSuperMethodCount = 0;
        int bcInitRefCount = 0;
        int bcEscCount = 0;
        int bcEscRefCount = 0;
        final AttributeLayout abstractModifier = attributeDefinitionMap.getAttributeLayout("ACC_ABSTRACT", 2);
        final AttributeLayout nativeModifier = attributeDefinitionMap.getAttributeLayout("ACC_NATIVE", 2);
        this.methodByteCodePacked = new byte[classCount][][];
        int bcParsed = 0;
        final List switchIsTableSwitch = new ArrayList();
        this.wideByteCodes = new ArrayList();
        for (int c = 0; c < classCount; ++c) {
            final int numberOfMethods = methodFlags[c].length;
            this.methodByteCodePacked[c] = new byte[numberOfMethods][];
            for (int m = 0; m < numberOfMethods; ++m) {
                final long methodFlag = methodFlags[c][m];
                if (!abstractModifier.matches(methodFlag) && !nativeModifier.matches(methodFlag)) {
                    final ByteArrayOutputStream codeBytes = new ByteArrayOutputStream();
                    byte code;
                    while ((code = (byte)(0xFF & in.read())) != -1) {
                        codeBytes.write(code);
                    }
                    this.methodByteCodePacked[c][m] = codeBytes.toByteArray();
                    bcParsed += this.methodByteCodePacked[c][m].length;
                    final int[] codes = new int[this.methodByteCodePacked[c][m].length];
                    for (int i = 0; i < codes.length; ++i) {
                        codes[i] = (this.methodByteCodePacked[c][m][i] & 0xFF);
                    }
                    for (int i = 0; i < this.methodByteCodePacked[c][m].length; ++i) {
                        final int codePacked = 0xFF & this.methodByteCodePacked[c][m][i];
                        switch (codePacked) {
                            case 16:
                            case 188: {
                                ++bcByteCount;
                                break;
                            }
                            case 17: {
                                ++bcShortCount;
                                break;
                            }
                            case 18:
                            case 19: {
                                ++bcStringRefCount;
                                break;
                            }
                            case 234:
                            case 237: {
                                ++bcIntRefCount;
                                break;
                            }
                            case 235:
                            case 238: {
                                ++bcFloatRefCount;
                                break;
                            }
                            case 197: {
                                ++bcByteCount;
                            }
                            case 187:
                            case 189:
                            case 192:
                            case 193:
                            case 233:
                            case 236: {
                                ++bcClassRefCount;
                                break;
                            }
                            case 20: {
                                ++bcLongRefCount;
                                break;
                            }
                            case 239: {
                                ++bcDoubleRefCount;
                                break;
                            }
                            case 169: {
                                ++bcLocalCount;
                                break;
                            }
                            case 167:
                            case 168:
                            case 200:
                            case 201: {
                                ++bcLabelCount;
                                break;
                            }
                            case 170: {
                                switchIsTableSwitch.add(true);
                                ++bcCaseCountCount;
                                ++bcLabelCount;
                                break;
                            }
                            case 171: {
                                switchIsTableSwitch.add(false);
                                ++bcCaseCountCount;
                                ++bcLabelCount;
                                break;
                            }
                            case 178:
                            case 179:
                            case 180:
                            case 181: {
                                ++bcFieldRefCount;
                                break;
                            }
                            case 182:
                            case 183:
                            case 184: {
                                ++bcMethodRefCount;
                                break;
                            }
                            case 185: {
                                ++bcIMethodRefCount;
                                break;
                            }
                            case 202:
                            case 203:
                            case 204:
                            case 205:
                            case 209:
                            case 210:
                            case 211:
                            case 212: {
                                ++bcThisFieldCount;
                                break;
                            }
                            case 206:
                            case 207:
                            case 208:
                            case 213:
                            case 214:
                            case 215: {
                                ++bcThisMethodCount;
                                break;
                            }
                            case 216:
                            case 217:
                            case 218:
                            case 219:
                            case 223:
                            case 224:
                            case 225:
                            case 226: {
                                ++bcSuperFieldCount;
                                break;
                            }
                            case 220:
                            case 221:
                            case 222:
                            case 227:
                            case 228:
                            case 229: {
                                ++bcSuperMethodCount;
                                break;
                            }
                            case 132: {
                                ++bcLocalCount;
                                ++bcByteCount;
                                break;
                            }
                            case 196: {
                                final int nextInstruction = 0xFF & this.methodByteCodePacked[c][m][i + 1];
                                this.wideByteCodes.add(nextInstruction);
                                if (nextInstruction == 132) {
                                    ++bcLocalCount;
                                    ++bcShortCount;
                                }
                                else if (this.endsWithLoad(nextInstruction) || this.endsWithStore(nextInstruction) || nextInstruction == 169) {
                                    ++bcLocalCount;
                                }
                                else {
                                    this.segment.log(2, "Found unhandled " + ByteCode.getByteCode(nextInstruction));
                                }
                                ++i;
                                break;
                            }
                            case 230:
                            case 231:
                            case 232: {
                                ++bcInitRefCount;
                                break;
                            }
                            case 253: {
                                ++bcEscRefCount;
                                break;
                            }
                            case 254: {
                                ++bcEscCount;
                                break;
                            }
                            default: {
                                if (this.endsWithLoad(codePacked) || this.endsWithStore(codePacked)) {
                                    ++bcLocalCount;
                                    break;
                                }
                                if (this.startsWithIf(codePacked)) {
                                    ++bcLabelCount;
                                    break;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        this.bcCaseCount = this.decodeBandInt("bc_case_count", in, Codec.UNSIGNED5, bcCaseCountCount);
        int bcCaseValueCount = 0;
        for (int j = 0; j < this.bcCaseCount.length; ++j) {
            final boolean isTableSwitch = switchIsTableSwitch.get(j);
            if (isTableSwitch) {
                ++bcCaseValueCount;
            }
            else {
                bcCaseValueCount += this.bcCaseCount[j];
            }
        }
        this.bcCaseValue = this.decodeBandInt("bc_case_value", in, Codec.DELTA5, bcCaseValueCount);
        for (int index = 0; index < bcCaseCountCount; ++index) {
            bcLabelCount += this.bcCaseCount[index];
        }
        this.bcByte = this.decodeBandInt("bc_byte", in, Codec.BYTE1, bcByteCount);
        this.bcShort = this.decodeBandInt("bc_short", in, Codec.DELTA5, bcShortCount);
        this.bcLocal = this.decodeBandInt("bc_local", in, Codec.UNSIGNED5, bcLocalCount);
        this.bcLabel = this.decodeBandInt("bc_label", in, Codec.BRANCH5, bcLabelCount);
        this.bcIntRef = this.decodeBandInt("bc_intref", in, Codec.DELTA5, bcIntRefCount);
        this.bcFloatRef = this.decodeBandInt("bc_floatref", in, Codec.DELTA5, bcFloatRefCount);
        this.bcLongRef = this.decodeBandInt("bc_longref", in, Codec.DELTA5, bcLongRefCount);
        this.bcDoubleRef = this.decodeBandInt("bc_doubleref", in, Codec.DELTA5, bcDoubleRefCount);
        this.bcStringRef = this.decodeBandInt("bc_stringref", in, Codec.DELTA5, bcStringRefCount);
        this.bcClassRef = this.decodeBandInt("bc_classref", in, Codec.UNSIGNED5, bcClassRefCount);
        this.bcFieldRef = this.decodeBandInt("bc_fieldref", in, Codec.DELTA5, bcFieldRefCount);
        this.bcMethodRef = this.decodeBandInt("bc_methodref", in, Codec.UNSIGNED5, bcMethodRefCount);
        this.bcIMethodRef = this.decodeBandInt("bc_imethodref", in, Codec.DELTA5, bcIMethodRefCount);
        this.bcThisField = this.decodeBandInt("bc_thisfield", in, Codec.UNSIGNED5, bcThisFieldCount);
        this.bcSuperField = this.decodeBandInt("bc_superfield", in, Codec.UNSIGNED5, bcSuperFieldCount);
        this.bcThisMethod = this.decodeBandInt("bc_thismethod", in, Codec.UNSIGNED5, bcThisMethodCount);
        this.bcSuperMethod = this.decodeBandInt("bc_supermethod", in, Codec.UNSIGNED5, bcSuperMethodCount);
        this.bcInitRef = this.decodeBandInt("bc_initref", in, Codec.UNSIGNED5, bcInitRefCount);
        this.bcEscRef = this.decodeBandInt("bc_escref", in, Codec.UNSIGNED5, bcEscRefCount);
        this.bcEscRefSize = this.decodeBandInt("bc_escrefsize", in, Codec.UNSIGNED5, bcEscRefCount);
        this.bcEscSize = this.decodeBandInt("bc_escsize", in, Codec.UNSIGNED5, bcEscCount);
        this.bcEscByte = this.decodeBandInt("bc_escbyte", in, Codec.BYTE1, this.bcEscSize);
    }
    
    @Override
    public void unpack() throws Pack200Exception {
        final int classCount = this.header.getClassCount();
        final long[][] methodFlags = this.segment.getClassBands().getMethodFlags();
        final int[] codeMaxNALocals = this.segment.getClassBands().getCodeMaxNALocals();
        final int[] codeMaxStack = this.segment.getClassBands().getCodeMaxStack();
        final ArrayList[][] methodAttributes = this.segment.getClassBands().getMethodAttributes();
        final String[][] methodDescr = this.segment.getClassBands().getMethodDescr();
        final AttributeLayoutMap attributeDefinitionMap = this.segment.getAttrDefinitionBands().getAttributeDefinitionMap();
        final AttributeLayout abstractModifier = attributeDefinitionMap.getAttributeLayout("ACC_ABSTRACT", 2);
        final AttributeLayout nativeModifier = attributeDefinitionMap.getAttributeLayout("ACC_NATIVE", 2);
        final AttributeLayout staticModifier = attributeDefinitionMap.getAttributeLayout("ACC_STATIC", 2);
        final int[] wideByteCodeArray = new int[this.wideByteCodes.size()];
        for (int index = 0; index < wideByteCodeArray.length; ++index) {
            wideByteCodeArray[index] = this.wideByteCodes.get(index);
        }
        final OperandManager operandManager = new OperandManager(this.bcCaseCount, this.bcCaseValue, this.bcByte, this.bcShort, this.bcLocal, this.bcLabel, this.bcIntRef, this.bcFloatRef, this.bcLongRef, this.bcDoubleRef, this.bcStringRef, this.bcClassRef, this.bcFieldRef, this.bcMethodRef, this.bcIMethodRef, this.bcThisField, this.bcSuperField, this.bcThisMethod, this.bcSuperMethod, this.bcInitRef, wideByteCodeArray);
        operandManager.setSegment(this.segment);
        int i = 0;
        final ArrayList orderedCodeAttributes = this.segment.getClassBands().getOrderedCodeAttributes();
        int codeAttributeIndex = 0;
        final int[] handlerCount = this.segment.getClassBands().getCodeHandlerCount();
        final int[][] handlerStartPCs = this.segment.getClassBands().getCodeHandlerStartP();
        final int[][] handlerEndPCs = this.segment.getClassBands().getCodeHandlerEndPO();
        final int[][] handlerCatchPCs = this.segment.getClassBands().getCodeHandlerCatchPO();
        final int[][] handlerClassTypes = this.segment.getClassBands().getCodeHandlerClassRCN();
        final boolean allCodeHasFlags = this.segment.getSegmentHeader().getOptions().hasAllCodeFlags();
        final boolean[] codeHasFlags = this.segment.getClassBands().getCodeHasAttributes();
        for (int c = 0; c < classCount; ++c) {
            for (int numberOfMethods = methodFlags[c].length, m = 0; m < numberOfMethods; ++m) {
                final long methodFlag = methodFlags[c][m];
                if (!abstractModifier.matches(methodFlag) && !nativeModifier.matches(methodFlag)) {
                    final int maxStack = codeMaxStack[i];
                    int maxLocal = codeMaxNALocals[i];
                    if (!staticModifier.matches(methodFlag)) {
                        ++maxLocal;
                    }
                    maxLocal += SegmentUtils.countInvokeInterfaceArgs(methodDescr[c][m]);
                    final String[] cpClass = this.segment.getCpBands().getCpClass();
                    operandManager.setCurrentClass(cpClass[this.segment.getClassBands().getClassThisInts()[c]]);
                    operandManager.setSuperClass(cpClass[this.segment.getClassBands().getClassSuperInts()[c]]);
                    final List exceptionTable = new ArrayList();
                    if (handlerCount != null) {
                        for (int j = 0; j < handlerCount[i]; ++j) {
                            final int handlerClass = handlerClassTypes[i][j] - 1;
                            CPClass cpHandlerClass = null;
                            if (handlerClass != -1) {
                                cpHandlerClass = this.segment.getCpBands().cpClassValue(handlerClass);
                            }
                            final ExceptionTableEntry entry = new ExceptionTableEntry(handlerStartPCs[i][j], handlerEndPCs[i][j], handlerCatchPCs[i][j], cpHandlerClass);
                            exceptionTable.add(entry);
                        }
                    }
                    final CodeAttribute codeAttr = new CodeAttribute(maxStack, maxLocal, this.methodByteCodePacked[c][m], this.segment, operandManager, exceptionTable);
                    final ArrayList methodAttributesList = methodAttributes[c][m];
                    int indexForCodeAttr = 0;
                    for (int index2 = 0; index2 < methodAttributesList.size(); ++index2) {
                        final Attribute attribute = methodAttributesList.get(index2);
                        if (!(attribute instanceof NewAttribute)) {
                            break;
                        }
                        if (((NewAttribute)attribute).getLayoutIndex() >= 15) {
                            break;
                        }
                        ++indexForCodeAttr;
                    }
                    methodAttributesList.add(indexForCodeAttr, codeAttr);
                    codeAttr.renumber(codeAttr.byteCodeOffsets);
                    List currentAttributes;
                    if (allCodeHasFlags) {
                        currentAttributes = orderedCodeAttributes.get(i);
                    }
                    else if (codeHasFlags[i]) {
                        currentAttributes = orderedCodeAttributes.get(codeAttributeIndex);
                        ++codeAttributeIndex;
                    }
                    else {
                        currentAttributes = Collections.EMPTY_LIST;
                    }
                    for (int index3 = 0; index3 < currentAttributes.size(); ++index3) {
                        final Attribute currentAttribute = currentAttributes.get(index3);
                        codeAttr.addAttribute(currentAttribute);
                        if (currentAttribute.hasBCIRenumbering()) {
                            ((BCIRenumberedAttribute)currentAttribute).renumber(codeAttr.byteCodeOffsets);
                        }
                    }
                    ++i;
                }
            }
        }
    }
    
    private boolean startsWithIf(final int codePacked) {
        return (codePacked >= 153 && codePacked <= 166) || codePacked == 198 || codePacked == 199;
    }
    
    private boolean endsWithLoad(final int codePacked) {
        return codePacked >= 21 && codePacked <= 25;
    }
    
    private boolean endsWithStore(final int codePacked) {
        return codePacked >= 54 && codePacked <= 58;
    }
    
    public byte[][][] getMethodByteCodePacked() {
        return this.methodByteCodePacked;
    }
    
    public int[] getBcCaseCount() {
        return this.bcCaseCount;
    }
    
    public int[] getBcCaseValue() {
        return this.bcCaseValue;
    }
    
    public int[] getBcByte() {
        return this.bcByte;
    }
    
    public int[] getBcClassRef() {
        return this.bcClassRef;
    }
    
    public int[] getBcDoubleRef() {
        return this.bcDoubleRef;
    }
    
    public int[] getBcFieldRef() {
        return this.bcFieldRef;
    }
    
    public int[] getBcFloatRef() {
        return this.bcFloatRef;
    }
    
    public int[] getBcIMethodRef() {
        return this.bcIMethodRef;
    }
    
    public int[] getBcInitRef() {
        return this.bcInitRef;
    }
    
    public int[] getBcIntRef() {
        return this.bcIntRef;
    }
    
    public int[] getBcLabel() {
        return this.bcLabel;
    }
    
    public int[] getBcLocal() {
        return this.bcLocal;
    }
    
    public int[] getBcLongRef() {
        return this.bcLongRef;
    }
    
    public int[] getBcMethodRef() {
        return this.bcMethodRef;
    }
    
    public int[] getBcShort() {
        return this.bcShort;
    }
    
    public int[] getBcStringRef() {
        return this.bcStringRef;
    }
    
    public int[] getBcSuperField() {
        return this.bcSuperField;
    }
    
    public int[] getBcSuperMethod() {
        return this.bcSuperMethod;
    }
    
    public int[] getBcThisField() {
        return this.bcThisField;
    }
    
    public int[] getBcThisMethod() {
        return this.bcThisMethod;
    }
}
