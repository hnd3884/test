package com.sun.org.apache.bcel.internal.generic;

public interface Visitor
{
    void visitStackInstruction(final StackInstruction p0);
    
    void visitLocalVariableInstruction(final LocalVariableInstruction p0);
    
    void visitBranchInstruction(final BranchInstruction p0);
    
    void visitLoadClass(final LoadClass p0);
    
    void visitFieldInstruction(final FieldInstruction p0);
    
    void visitIfInstruction(final IfInstruction p0);
    
    void visitConversionInstruction(final ConversionInstruction p0);
    
    void visitPopInstruction(final PopInstruction p0);
    
    void visitStoreInstruction(final StoreInstruction p0);
    
    void visitTypedInstruction(final TypedInstruction p0);
    
    void visitSelect(final Select p0);
    
    void visitJsrInstruction(final JsrInstruction p0);
    
    void visitGotoInstruction(final GotoInstruction p0);
    
    void visitUnconditionalBranch(final UnconditionalBranch p0);
    
    void visitPushInstruction(final PushInstruction p0);
    
    void visitArithmeticInstruction(final ArithmeticInstruction p0);
    
    void visitCPInstruction(final CPInstruction p0);
    
    void visitInvokeInstruction(final InvokeInstruction p0);
    
    void visitArrayInstruction(final ArrayInstruction p0);
    
    void visitAllocationInstruction(final AllocationInstruction p0);
    
    void visitReturnInstruction(final ReturnInstruction p0);
    
    void visitFieldOrMethod(final FieldOrMethod p0);
    
    void visitConstantPushInstruction(final ConstantPushInstruction p0);
    
    void visitExceptionThrower(final ExceptionThrower p0);
    
    void visitLoadInstruction(final LoadInstruction p0);
    
    void visitVariableLengthInstruction(final VariableLengthInstruction p0);
    
    void visitStackProducer(final StackProducer p0);
    
    void visitStackConsumer(final StackConsumer p0);
    
    void visitACONST_NULL(final ACONST_NULL p0);
    
    void visitGETSTATIC(final GETSTATIC p0);
    
    void visitIF_ICMPLT(final IF_ICMPLT p0);
    
    void visitMONITOREXIT(final MONITOREXIT p0);
    
    void visitIFLT(final IFLT p0);
    
    void visitLSTORE(final LSTORE p0);
    
    void visitPOP2(final POP2 p0);
    
    void visitBASTORE(final BASTORE p0);
    
    void visitISTORE(final ISTORE p0);
    
    void visitCHECKCAST(final CHECKCAST p0);
    
    void visitFCMPG(final FCMPG p0);
    
    void visitI2F(final I2F p0);
    
    void visitATHROW(final ATHROW p0);
    
    void visitDCMPL(final DCMPL p0);
    
    void visitARRAYLENGTH(final ARRAYLENGTH p0);
    
    void visitDUP(final DUP p0);
    
    void visitINVOKESTATIC(final INVOKESTATIC p0);
    
    void visitLCONST(final LCONST p0);
    
    void visitDREM(final DREM p0);
    
    void visitIFGE(final IFGE p0);
    
    void visitCALOAD(final CALOAD p0);
    
    void visitLASTORE(final LASTORE p0);
    
    void visitI2D(final I2D p0);
    
    void visitDADD(final DADD p0);
    
    void visitINVOKESPECIAL(final INVOKESPECIAL p0);
    
    void visitIAND(final IAND p0);
    
    void visitPUTFIELD(final PUTFIELD p0);
    
    void visitILOAD(final ILOAD p0);
    
    void visitDLOAD(final DLOAD p0);
    
    void visitDCONST(final DCONST p0);
    
    void visitNEW(final NEW p0);
    
    void visitIFNULL(final IFNULL p0);
    
    void visitLSUB(final LSUB p0);
    
    void visitL2I(final L2I p0);
    
    void visitISHR(final ISHR p0);
    
    void visitTABLESWITCH(final TABLESWITCH p0);
    
    void visitIINC(final IINC p0);
    
    void visitDRETURN(final DRETURN p0);
    
    void visitFSTORE(final FSTORE p0);
    
    void visitDASTORE(final DASTORE p0);
    
    void visitIALOAD(final IALOAD p0);
    
    void visitDDIV(final DDIV p0);
    
    void visitIF_ICMPGE(final IF_ICMPGE p0);
    
    void visitLAND(final LAND p0);
    
    void visitIDIV(final IDIV p0);
    
    void visitLOR(final LOR p0);
    
    void visitCASTORE(final CASTORE p0);
    
    void visitFREM(final FREM p0);
    
    void visitLDC(final LDC p0);
    
    void visitBIPUSH(final BIPUSH p0);
    
    void visitDSTORE(final DSTORE p0);
    
    void visitF2L(final F2L p0);
    
    void visitFMUL(final FMUL p0);
    
    void visitLLOAD(final LLOAD p0);
    
    void visitJSR(final JSR p0);
    
    void visitFSUB(final FSUB p0);
    
    void visitSASTORE(final SASTORE p0);
    
    void visitALOAD(final ALOAD p0);
    
    void visitDUP2_X2(final DUP2_X2 p0);
    
    void visitRETURN(final RETURN p0);
    
    void visitDALOAD(final DALOAD p0);
    
    void visitSIPUSH(final SIPUSH p0);
    
    void visitDSUB(final DSUB p0);
    
    void visitL2F(final L2F p0);
    
    void visitIF_ICMPGT(final IF_ICMPGT p0);
    
    void visitF2D(final F2D p0);
    
    void visitI2L(final I2L p0);
    
    void visitIF_ACMPNE(final IF_ACMPNE p0);
    
    void visitPOP(final POP p0);
    
    void visitI2S(final I2S p0);
    
    void visitIFEQ(final IFEQ p0);
    
    void visitSWAP(final SWAP p0);
    
    void visitIOR(final IOR p0);
    
    void visitIREM(final IREM p0);
    
    void visitIASTORE(final IASTORE p0);
    
    void visitNEWARRAY(final NEWARRAY p0);
    
    void visitINVOKEINTERFACE(final INVOKEINTERFACE p0);
    
    void visitINEG(final INEG p0);
    
    void visitLCMP(final LCMP p0);
    
    void visitJSR_W(final JSR_W p0);
    
    void visitMULTIANEWARRAY(final MULTIANEWARRAY p0);
    
    void visitDUP_X2(final DUP_X2 p0);
    
    void visitSALOAD(final SALOAD p0);
    
    void visitIFNONNULL(final IFNONNULL p0);
    
    void visitDMUL(final DMUL p0);
    
    void visitIFNE(final IFNE p0);
    
    void visitIF_ICMPLE(final IF_ICMPLE p0);
    
    void visitLDC2_W(final LDC2_W p0);
    
    void visitGETFIELD(final GETFIELD p0);
    
    void visitLADD(final LADD p0);
    
    void visitNOP(final NOP p0);
    
    void visitFALOAD(final FALOAD p0);
    
    void visitINSTANCEOF(final INSTANCEOF p0);
    
    void visitIFLE(final IFLE p0);
    
    void visitLXOR(final LXOR p0);
    
    void visitLRETURN(final LRETURN p0);
    
    void visitFCONST(final FCONST p0);
    
    void visitIUSHR(final IUSHR p0);
    
    void visitBALOAD(final BALOAD p0);
    
    void visitDUP2(final DUP2 p0);
    
    void visitIF_ACMPEQ(final IF_ACMPEQ p0);
    
    void visitIMPDEP1(final IMPDEP1 p0);
    
    void visitMONITORENTER(final MONITORENTER p0);
    
    void visitLSHL(final LSHL p0);
    
    void visitDCMPG(final DCMPG p0);
    
    void visitD2L(final D2L p0);
    
    void visitIMPDEP2(final IMPDEP2 p0);
    
    void visitL2D(final L2D p0);
    
    void visitRET(final RET p0);
    
    void visitIFGT(final IFGT p0);
    
    void visitIXOR(final IXOR p0);
    
    void visitINVOKEVIRTUAL(final INVOKEVIRTUAL p0);
    
    void visitFASTORE(final FASTORE p0);
    
    void visitIRETURN(final IRETURN p0);
    
    void visitIF_ICMPNE(final IF_ICMPNE p0);
    
    void visitFLOAD(final FLOAD p0);
    
    void visitLDIV(final LDIV p0);
    
    void visitPUTSTATIC(final PUTSTATIC p0);
    
    void visitAALOAD(final AALOAD p0);
    
    void visitD2I(final D2I p0);
    
    void visitIF_ICMPEQ(final IF_ICMPEQ p0);
    
    void visitAASTORE(final AASTORE p0);
    
    void visitARETURN(final ARETURN p0);
    
    void visitDUP2_X1(final DUP2_X1 p0);
    
    void visitFNEG(final FNEG p0);
    
    void visitGOTO_W(final GOTO_W p0);
    
    void visitD2F(final D2F p0);
    
    void visitGOTO(final GOTO p0);
    
    void visitISUB(final ISUB p0);
    
    void visitF2I(final F2I p0);
    
    void visitDNEG(final DNEG p0);
    
    void visitICONST(final ICONST p0);
    
    void visitFDIV(final FDIV p0);
    
    void visitI2B(final I2B p0);
    
    void visitLNEG(final LNEG p0);
    
    void visitLREM(final LREM p0);
    
    void visitIMUL(final IMUL p0);
    
    void visitIADD(final IADD p0);
    
    void visitLSHR(final LSHR p0);
    
    void visitLOOKUPSWITCH(final LOOKUPSWITCH p0);
    
    void visitDUP_X1(final DUP_X1 p0);
    
    void visitFCMPL(final FCMPL p0);
    
    void visitI2C(final I2C p0);
    
    void visitLMUL(final LMUL p0);
    
    void visitLUSHR(final LUSHR p0);
    
    void visitISHL(final ISHL p0);
    
    void visitLALOAD(final LALOAD p0);
    
    void visitASTORE(final ASTORE p0);
    
    void visitANEWARRAY(final ANEWARRAY p0);
    
    void visitFRETURN(final FRETURN p0);
    
    void visitFADD(final FADD p0);
    
    void visitBREAKPOINT(final BREAKPOINT p0);
}
