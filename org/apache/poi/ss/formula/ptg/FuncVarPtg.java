package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.ss.formula.function.FunctionMetadata;
import org.apache.poi.ss.formula.function.FunctionMetadataRegistry;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.BitField;

public final class FuncVarPtg extends AbstractFunctionPtg
{
    public static final byte sid = 34;
    private static final int SIZE = 4;
    private static final BitField ceFunc;
    public static final OperationPtg SUM;
    private final boolean _isCetab;
    
    private FuncVarPtg(final int functionIndex, final int returnClass, final byte[] paramClasses, final int numArgs, final boolean isCetab) {
        super(functionIndex, returnClass, paramClasses, numArgs);
        this._isCetab = isCetab;
    }
    
    public static FuncVarPtg create(final LittleEndianInput in) {
        return create(in.readByte(), in.readUShort());
    }
    
    public static FuncVarPtg create(final String pName, final int numArgs) {
        return create(numArgs, AbstractFunctionPtg.lookupIndex(pName));
    }
    
    private static FuncVarPtg create(final int numArgs, int functionIndex) {
        final boolean isCetab = FuncVarPtg.ceFunc.isSet(functionIndex);
        FunctionMetadata fm;
        if (isCetab) {
            functionIndex = FuncVarPtg.ceFunc.clear(functionIndex);
            fm = FunctionMetadataRegistry.getCetabFunctionByIndex(functionIndex);
        }
        else {
            fm = FunctionMetadataRegistry.getFunctionByIndex(functionIndex);
        }
        if (fm == null) {
            return new FuncVarPtg(functionIndex, 32, new byte[] { 32 }, numArgs, isCetab);
        }
        return new FuncVarPtg(functionIndex, fm.getReturnClassCode(), fm.getParameterClassCodes(), numArgs, isCetab);
    }
    
    @Override
    protected String lookupName(final short index) {
        return this.lookupName(index, this._isCetab);
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(34 + this.getPtgClass());
        out.writeByte(this.getNumberOfOperands());
        out.writeShort(this.getFunctionIndex());
    }
    
    @Override
    public int getSize() {
        return 4;
    }
    
    @Override
    public FuncVarPtg copy() {
        return this;
    }
    
    static {
        ceFunc = BitFieldFactory.getInstance(61440);
        SUM = create("SUM", 1);
    }
}
