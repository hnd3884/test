package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.Type;

public final class AttributeSetMethodGenerator extends MethodGenerator
{
    protected static final int CURRENT_INDEX = 4;
    private static final int PARAM_START_INDEX = 5;
    private static final String[] argNames;
    private static final Type[] argTypes;
    
    public AttributeSetMethodGenerator(final String methodName, final ClassGenerator classGen) {
        super(2, Type.VOID, AttributeSetMethodGenerator.argTypes, AttributeSetMethodGenerator.argNames, methodName, classGen.getClassName(), new InstructionList(), classGen.getConstantPool());
    }
    
    @Override
    public int getLocalIndex(final String name) {
        if (name.equals("current")) {
            return 4;
        }
        return super.getLocalIndex(name);
    }
    
    public Instruction loadParameter(final int index) {
        return new ALOAD(index + 5);
    }
    
    public Instruction storeParameter(final int index) {
        return new ASTORE(index + 5);
    }
    
    static {
        argNames = new String[4];
        (argTypes = new Type[4])[0] = Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
        AttributeSetMethodGenerator.argTypes[1] = Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
        AttributeSetMethodGenerator.argTypes[2] = Util.getJCRefType("Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;");
        AttributeSetMethodGenerator.argTypes[3] = Type.INT;
        AttributeSetMethodGenerator.argNames[0] = "document";
        AttributeSetMethodGenerator.argNames[1] = "iterator";
        AttributeSetMethodGenerator.argNames[2] = "handler";
        AttributeSetMethodGenerator.argNames[3] = "node";
    }
}
