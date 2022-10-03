package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.IfInstruction;
import com.sun.org.apache.bcel.internal.generic.DSTORE;
import com.sun.org.apache.bcel.internal.generic.FSTORE;
import com.sun.org.apache.bcel.internal.generic.LSTORE;
import com.sun.org.apache.bcel.internal.generic.DLOAD;
import com.sun.org.apache.bcel.internal.generic.FLOAD;
import com.sun.org.apache.bcel.internal.generic.LLOAD;
import com.sun.org.apache.bcel.internal.generic.InstructionTargeter;
import com.sun.org.apache.xalan.internal.xsltc.compiler.XSLTC;
import com.sun.org.apache.bcel.internal.generic.TargetLostException;
import com.sun.org.apache.bcel.internal.generic.Select;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.bcel.internal.classfile.Attribute;
import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.generic.IndexedInstruction;
import com.sun.org.apache.bcel.internal.generic.RET;
import com.sun.org.apache.bcel.internal.generic.LocalVariableInstruction;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;
import java.util.List;
import java.util.Collections;
import com.sun.org.apache.bcel.internal.classfile.Method;
import java.util.Iterator;
import java.util.Stack;
import java.util.ArrayList;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.ICONST;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import java.util.HashMap;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Pattern;
import java.util.Map;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Constants;
import com.sun.org.apache.bcel.internal.generic.MethodGen;

public class MethodGenerator extends MethodGen implements Constants
{
    protected static final int INVALID_INDEX = -1;
    private static final String START_ELEMENT_SIG = "(Ljava/lang/String;)V";
    private static final String END_ELEMENT_SIG = "(Ljava/lang/String;)V";
    private InstructionList _mapTypeSub;
    private static final int DOM_INDEX = 1;
    private static final int ITERATOR_INDEX = 2;
    private static final int HANDLER_INDEX = 3;
    private static final int MAX_METHOD_SIZE = 65535;
    private static final int MAX_BRANCH_TARGET_OFFSET = 32767;
    private static final int MIN_BRANCH_TARGET_OFFSET = -32768;
    private static final int TARGET_METHOD_SIZE = 60000;
    private static final int MINIMUM_OUTLINEABLE_CHUNK_SIZE = 1000;
    private Instruction _iloadCurrent;
    private Instruction _istoreCurrent;
    private final Instruction _astoreHandler;
    private final Instruction _aloadHandler;
    private final Instruction _astoreIterator;
    private final Instruction _aloadIterator;
    private final Instruction _aloadDom;
    private final Instruction _astoreDom;
    private final Instruction _startElement;
    private final Instruction _endElement;
    private final Instruction _startDocument;
    private final Instruction _endDocument;
    private final Instruction _attribute;
    private final Instruction _uniqueAttribute;
    private final Instruction _namespace;
    private final Instruction _setStartNode;
    private final Instruction _reset;
    private final Instruction _nextNode;
    private SlotAllocator _slotAllocator;
    private boolean _allocatorInit;
    private LocalVariableRegistry _localVariableRegistry;
    private Map<Pattern, InstructionList> _preCompiled;
    private int m_totalChunks;
    private int m_openChunks;
    
    public MethodGenerator(final int access_flags, final Type return_type, final Type[] arg_types, final String[] arg_names, final String method_name, final String class_name, final InstructionList il, final ConstantPoolGen cpg) {
        super(access_flags, return_type, arg_types, arg_names, method_name, class_name, il, cpg);
        this._allocatorInit = false;
        this._preCompiled = new HashMap<Pattern, InstructionList>();
        this.m_totalChunks = 0;
        this.m_openChunks = 0;
        this._astoreHandler = new ASTORE(3);
        this._aloadHandler = new ALOAD(3);
        this._astoreIterator = new ASTORE(2);
        this._aloadIterator = new ALOAD(2);
        this._aloadDom = new ALOAD(1);
        this._astoreDom = new ASTORE(1);
        final int startElement = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "startElement", "(Ljava/lang/String;)V");
        this._startElement = new INVOKEINTERFACE(startElement, 2);
        final int endElement = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "endElement", "(Ljava/lang/String;)V");
        this._endElement = new INVOKEINTERFACE(endElement, 2);
        final int attribute = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "addAttribute", "(Ljava/lang/String;Ljava/lang/String;)V");
        this._attribute = new INVOKEINTERFACE(attribute, 3);
        final int uniqueAttribute = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "addUniqueAttribute", "(Ljava/lang/String;Ljava/lang/String;I)V");
        this._uniqueAttribute = new INVOKEINTERFACE(uniqueAttribute, 4);
        final int namespace = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "namespaceAfterStartElement", "(Ljava/lang/String;Ljava/lang/String;)V");
        this._namespace = new INVOKEINTERFACE(namespace, 3);
        int index = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "startDocument", "()V");
        this._startDocument = new INVOKEINTERFACE(index, 1);
        index = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "endDocument", "()V");
        this._endDocument = new INVOKEINTERFACE(index, 1);
        index = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "setStartNode", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
        this._setStartNode = new INVOKEINTERFACE(index, 2);
        index = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "reset", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
        this._reset = new INVOKEINTERFACE(index, 1);
        index = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "next", "()I");
        this._nextNode = new INVOKEINTERFACE(index, 1);
        (this._slotAllocator = new SlotAllocator()).initialize(this.getLocalVariableRegistry().getLocals(false));
        this._allocatorInit = true;
    }
    
    @Override
    public LocalVariableGen addLocalVariable(final String name, final Type type, final InstructionHandle start, final InstructionHandle end) {
        LocalVariableGen lvg;
        if (this._allocatorInit) {
            lvg = this.addLocalVariable2(name, type, start);
        }
        else {
            lvg = super.addLocalVariable(name, type, start, end);
            this.getLocalVariableRegistry().registerLocalVariable(lvg);
        }
        return lvg;
    }
    
    public LocalVariableGen addLocalVariable2(final String name, final Type type, final InstructionHandle start) {
        final LocalVariableGen lvg = super.addLocalVariable(name, type, this._slotAllocator.allocateSlot(type), start, null);
        this.getLocalVariableRegistry().registerLocalVariable(lvg);
        return lvg;
    }
    
    private LocalVariableRegistry getLocalVariableRegistry() {
        if (this._localVariableRegistry == null) {
            this._localVariableRegistry = new LocalVariableRegistry();
        }
        return this._localVariableRegistry;
    }
    
    boolean offsetInLocalVariableGenRange(final LocalVariableGen lvg, final int offset) {
        InstructionHandle lvgStart = lvg.getStart();
        InstructionHandle lvgEnd = lvg.getEnd();
        if (lvgStart == null) {
            lvgStart = this.getInstructionList().getStart();
        }
        if (lvgEnd == null) {
            lvgEnd = this.getInstructionList().getEnd();
        }
        return lvgStart.getPosition() <= offset && lvgEnd.getPosition() + lvgEnd.getInstruction().getLength() >= offset;
    }
    
    @Override
    public void removeLocalVariable(final LocalVariableGen lvg) {
        this._slotAllocator.releaseSlot(lvg);
        this.getLocalVariableRegistry().removeByNameTracking(lvg);
        super.removeLocalVariable(lvg);
    }
    
    public Instruction loadDOM() {
        return this._aloadDom;
    }
    
    public Instruction storeDOM() {
        return this._astoreDom;
    }
    
    public Instruction storeHandler() {
        return this._astoreHandler;
    }
    
    public Instruction loadHandler() {
        return this._aloadHandler;
    }
    
    public Instruction storeIterator() {
        return this._astoreIterator;
    }
    
    public Instruction loadIterator() {
        return this._aloadIterator;
    }
    
    public final Instruction setStartNode() {
        return this._setStartNode;
    }
    
    public final Instruction reset() {
        return this._reset;
    }
    
    public final Instruction nextNode() {
        return this._nextNode;
    }
    
    public final Instruction startElement() {
        return this._startElement;
    }
    
    public final Instruction endElement() {
        return this._endElement;
    }
    
    public final Instruction startDocument() {
        return this._startDocument;
    }
    
    public final Instruction endDocument() {
        return this._endDocument;
    }
    
    public final Instruction attribute() {
        return this._attribute;
    }
    
    public final Instruction uniqueAttribute() {
        return this._uniqueAttribute;
    }
    
    public final Instruction namespace() {
        return this._namespace;
    }
    
    public Instruction loadCurrentNode() {
        if (this._iloadCurrent == null) {
            final int idx = this.getLocalIndex("current");
            if (idx > 0) {
                this._iloadCurrent = new ILOAD(idx);
            }
            else {
                this._iloadCurrent = new ICONST(0);
            }
        }
        return this._iloadCurrent;
    }
    
    public Instruction storeCurrentNode() {
        return (this._istoreCurrent != null) ? this._istoreCurrent : (this._istoreCurrent = new ISTORE(this.getLocalIndex("current")));
    }
    
    public Instruction loadContextNode() {
        return this.loadCurrentNode();
    }
    
    public Instruction storeContextNode() {
        return this.storeCurrentNode();
    }
    
    public int getLocalIndex(final String name) {
        return this.getLocalVariable(name).getIndex();
    }
    
    public LocalVariableGen getLocalVariable(final String name) {
        return this.getLocalVariableRegistry().lookUpByName(name);
    }
    
    @Override
    public void setMaxLocals() {
        final int prevLocals;
        int maxLocals = prevLocals = super.getMaxLocals();
        final LocalVariableGen[] localVars = super.getLocalVariables();
        if (localVars != null && localVars.length > maxLocals) {
            maxLocals = localVars.length;
        }
        if (maxLocals < 5) {
            maxLocals = 5;
        }
        super.setMaxLocals(maxLocals);
    }
    
    public void addInstructionList(final Pattern pattern, final InstructionList ilist) {
        this._preCompiled.put(pattern, ilist);
    }
    
    public InstructionList getInstructionList(final Pattern pattern) {
        return this._preCompiled.get(pattern);
    }
    
    private ArrayList getCandidateChunks(final ClassGenerator classGen, final int totalMethodSize) {
        final Iterator instructions = this.getInstructionList().iterator();
        final ArrayList candidateChunks = new ArrayList();
        ArrayList currLevelChunks = new ArrayList();
        final Stack subChunkStack = new Stack();
        boolean openChunkAtCurrLevel = false;
        boolean firstInstruction = true;
        if (this.m_openChunks != 0) {
            final String msg = new ErrorMsg("OUTLINE_ERR_UNBALANCED_MARKERS").toString();
            throw new InternalError(msg);
        }
        InstructionHandle currentHandle;
        do {
            currentHandle = (instructions.hasNext() ? instructions.next() : null);
            final Instruction inst = (currentHandle != null) ? currentHandle.getInstruction() : null;
            if (firstInstruction) {
                openChunkAtCurrLevel = true;
                currLevelChunks.add(currentHandle);
                firstInstruction = false;
            }
            if (inst instanceof OutlineableChunkStart) {
                if (openChunkAtCurrLevel) {
                    subChunkStack.push(currLevelChunks);
                    currLevelChunks = new ArrayList();
                }
                openChunkAtCurrLevel = true;
                currLevelChunks.add(currentHandle);
            }
            else {
                if (currentHandle != null && !(inst instanceof OutlineableChunkEnd)) {
                    continue;
                }
                ArrayList nestedSubChunks = null;
                if (!openChunkAtCurrLevel) {
                    nestedSubChunks = currLevelChunks;
                    currLevelChunks = subChunkStack.pop();
                }
                final InstructionHandle chunkStart = currLevelChunks.get(currLevelChunks.size() - 1);
                final int chunkEndPosition = (currentHandle != null) ? currentHandle.getPosition() : totalMethodSize;
                final int chunkSize = chunkEndPosition - chunkStart.getPosition();
                if (chunkSize <= 60000) {
                    currLevelChunks.add(currentHandle);
                }
                else {
                    if (!openChunkAtCurrLevel) {
                        final int childChunkCount = nestedSubChunks.size() / 2;
                        if (childChunkCount > 0) {
                            final Chunk[] childChunks = new Chunk[childChunkCount];
                            for (int i = 0; i < childChunkCount; ++i) {
                                final InstructionHandle start = nestedSubChunks.get(i * 2);
                                final InstructionHandle end = nestedSubChunks.get(i * 2 + 1);
                                childChunks[i] = new Chunk(start, end);
                            }
                            final ArrayList mergedChildChunks = this.mergeAdjacentChunks(childChunks);
                            for (int j = 0; j < mergedChildChunks.size(); ++j) {
                                final Chunk mergedChunk = mergedChildChunks.get(j);
                                final int mergedSize = mergedChunk.getChunkSize();
                                if (mergedSize >= 1000 && mergedSize <= 60000) {
                                    candidateChunks.add(mergedChunk);
                                }
                            }
                        }
                    }
                    currLevelChunks.remove(currLevelChunks.size() - 1);
                }
                openChunkAtCurrLevel = ((currLevelChunks.size() & 0x1) == 0x1);
            }
        } while (currentHandle != null);
        return candidateChunks;
    }
    
    private ArrayList mergeAdjacentChunks(final Chunk[] chunks) {
        final int[] adjacencyRunStart = new int[chunks.length];
        final int[] adjacencyRunLength = new int[chunks.length];
        final boolean[] chunkWasMerged = new boolean[chunks.length];
        int maximumRunOfChunks = 0;
        int numAdjacentRuns = 0;
        final ArrayList mergedChunks = new ArrayList();
        int startOfCurrentRun = 0;
        for (int i = 1; i < chunks.length; ++i) {
            if (!chunks[i - 1].isAdjacentTo(chunks[i])) {
                final int lengthOfRun = i - startOfCurrentRun;
                if (maximumRunOfChunks < lengthOfRun) {
                    maximumRunOfChunks = lengthOfRun;
                }
                if (lengthOfRun > 1) {
                    adjacencyRunLength[numAdjacentRuns] = lengthOfRun;
                    adjacencyRunStart[numAdjacentRuns] = startOfCurrentRun;
                    ++numAdjacentRuns;
                }
                startOfCurrentRun = i;
            }
        }
        if (chunks.length - startOfCurrentRun > 1) {
            final int lengthOfRun2 = chunks.length - startOfCurrentRun;
            if (maximumRunOfChunks < lengthOfRun2) {
                maximumRunOfChunks = lengthOfRun2;
            }
            adjacencyRunLength[numAdjacentRuns] = chunks.length - startOfCurrentRun;
            adjacencyRunStart[numAdjacentRuns] = startOfCurrentRun;
            ++numAdjacentRuns;
        }
        for (int numToMerge = maximumRunOfChunks; numToMerge > 1; --numToMerge) {
            for (int run = 0; run < numAdjacentRuns; ++run) {
                final int runStart = adjacencyRunStart[run];
                final int runEnd = runStart + adjacencyRunLength[run] - 1;
                boolean foundChunksToMerge = false;
                for (int mergeStart = runStart; mergeStart + numToMerge - 1 <= runEnd && !foundChunksToMerge; ++mergeStart) {
                    final int mergeEnd = mergeStart + numToMerge - 1;
                    int mergeSize = 0;
                    for (int j = mergeStart; j <= mergeEnd; ++j) {
                        mergeSize += chunks[j].getChunkSize();
                    }
                    if (mergeSize <= 60000) {
                        foundChunksToMerge = true;
                        for (int j = mergeStart; j <= mergeEnd; ++j) {
                            chunkWasMerged[j] = true;
                        }
                        mergedChunks.add(new Chunk(chunks[mergeStart].getChunkStart(), chunks[mergeEnd].getChunkEnd()));
                        adjacencyRunLength[run] = adjacencyRunStart[run] - mergeStart;
                        final int trailingRunLength = runEnd - mergeEnd;
                        if (trailingRunLength >= 2) {
                            adjacencyRunStart[numAdjacentRuns] = mergeEnd + 1;
                            adjacencyRunLength[numAdjacentRuns] = trailingRunLength;
                            ++numAdjacentRuns;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < chunks.length; ++i) {
            if (!chunkWasMerged[i]) {
                mergedChunks.add(chunks[i]);
            }
        }
        return mergedChunks;
    }
    
    public Method[] outlineChunks(final ClassGenerator classGen, final int originalMethodSize) {
        final ArrayList methodsOutlined = new ArrayList();
        int currentMethodSize = originalMethodSize;
        int outlinedCount = 0;
        String originalMethodName = this.getName();
        if (originalMethodName.equals("<init>")) {
            originalMethodName = "$lt$init$gt$";
        }
        else if (originalMethodName.equals("<clinit>")) {
            originalMethodName = "$lt$clinit$gt$";
        }
        boolean moreMethodsOutlined;
        do {
            final ArrayList candidateChunks = this.getCandidateChunks(classGen, currentMethodSize);
            Collections.sort((List<Comparable>)candidateChunks);
            moreMethodsOutlined = false;
            InstructionHandle lastInst;
            for (int i = candidateChunks.size() - 1; i >= 0 && currentMethodSize > 60000; currentMethodSize = lastInst.getPosition() + lastInst.getInstruction().getLength(), --i) {
                final Chunk chunkToOutline = candidateChunks.get(i);
                methodsOutlined.add(this.outline(chunkToOutline.getChunkStart(), chunkToOutline.getChunkEnd(), originalMethodName + "$outline$" + outlinedCount, classGen));
                ++outlinedCount;
                moreMethodsOutlined = true;
                final InstructionList il = this.getInstructionList();
                lastInst = il.getEnd();
                il.setPositions();
            }
        } while (moreMethodsOutlined && currentMethodSize > 60000);
        if (currentMethodSize > 65535) {
            final String msg = new ErrorMsg("OUTLINE_ERR_METHOD_TOO_BIG").toString();
            throw new InternalError(msg);
        }
        final Method[] methodsArr = new Method[methodsOutlined.size() + 1];
        methodsOutlined.toArray(methodsArr);
        methodsArr[methodsOutlined.size()] = this.getThisMethod();
        return methodsArr;
    }
    
    private Method outline(final InstructionHandle first, final InstructionHandle last, final String outlinedMethodName, final ClassGenerator classGen) {
        if (this.getExceptionHandlers().length != 0) {
            final String msg = new ErrorMsg("OUTLINE_ERR_TRY_CATCH").toString();
            throw new InternalError(msg);
        }
        final int outlineChunkStartOffset = first.getPosition();
        final int outlineChunkEndOffset = last.getPosition() + last.getInstruction().getLength();
        final ConstantPoolGen cpg = this.getConstantPool();
        final InstructionList newIL = new InstructionList();
        final XSLTC xsltc = classGen.getParser().getXSLTC();
        final String argTypeName = xsltc.getHelperClassName();
        final Type[] argTypes = { new ObjectType(argTypeName).toJCType() };
        final String argName = "copyLocals";
        final String[] argNames = { "copyLocals" };
        int methodAttributes = 18;
        final boolean isStaticMethod = (this.getAccessFlags() & 0x8) != 0x0;
        if (isStaticMethod) {
            methodAttributes |= 0x8;
        }
        final MethodGenerator outlinedMethodGen = new MethodGenerator(methodAttributes, Type.VOID, argTypes, argNames, outlinedMethodName, this.getClassName(), newIL, cpg);
        final ClassGenerator copyAreaCG = new ClassGenerator(argTypeName, "java.lang.Object", argTypeName + ".java", 49, null, classGen.getStylesheet()) {
            @Override
            public boolean isExternal() {
                return true;
            }
        };
        final ConstantPoolGen copyAreaCPG = copyAreaCG.getConstantPool();
        copyAreaCG.addEmptyConstructor(1);
        int copyAreaFieldCount = 0;
        final InstructionHandle limit = last.getNext();
        final InstructionList oldMethCopyInIL = new InstructionList();
        final InstructionList oldMethCopyOutIL = new InstructionList();
        final InstructionList newMethCopyInIL = new InstructionList();
        final InstructionList newMethCopyOutIL = new InstructionList();
        final InstructionHandle outlinedMethodCallSetup = oldMethCopyInIL.append(new NEW(cpg.addClass(argTypeName)));
        oldMethCopyInIL.append(InstructionConstants.DUP);
        oldMethCopyInIL.append(InstructionConstants.DUP);
        oldMethCopyInIL.append(new INVOKESPECIAL(cpg.addMethodref(argTypeName, "<init>", "()V")));
        InstructionHandle outlinedMethodRef;
        if (isStaticMethod) {
            outlinedMethodRef = oldMethCopyOutIL.append(new INVOKESTATIC(cpg.addMethodref(classGen.getClassName(), outlinedMethodName, outlinedMethodGen.getSignature())));
        }
        else {
            oldMethCopyOutIL.append(InstructionConstants.THIS);
            oldMethCopyOutIL.append(InstructionConstants.SWAP);
            outlinedMethodRef = oldMethCopyOutIL.append(new INVOKEVIRTUAL(cpg.addMethodref(classGen.getClassName(), outlinedMethodName, outlinedMethodGen.getSignature())));
        }
        boolean chunkStartTargetMappingsPending = false;
        InstructionHandle pendingTargetMappingHandle = null;
        InstructionHandle lastCopyHandle = null;
        final HashMap targetMap = new HashMap();
        final HashMap localVarMap = new HashMap();
        final HashMap revisedLocalVarStart = new HashMap();
        final HashMap revisedLocalVarEnd = new HashMap();
        for (InstructionHandle ih = first; ih != limit; ih = ih.getNext()) {
            final Instruction inst = ih.getInstruction();
            if (inst instanceof MarkerInstruction) {
                if (ih.hasTargeters()) {
                    if (inst instanceof OutlineableChunkEnd) {
                        targetMap.put(ih, lastCopyHandle);
                    }
                    else if (!chunkStartTargetMappingsPending) {
                        chunkStartTargetMappingsPending = true;
                        pendingTargetMappingHandle = ih;
                    }
                }
            }
            else {
                final Instruction c = inst.copy();
                if (c instanceof BranchInstruction) {
                    lastCopyHandle = newIL.append((BranchInstruction)c);
                }
                else {
                    lastCopyHandle = newIL.append(c);
                }
                if (c instanceof LocalVariableInstruction || c instanceof RET) {
                    final IndexedInstruction lvi = (IndexedInstruction)c;
                    final int oldLocalVarIndex = lvi.getIndex();
                    final LocalVariableGen oldLVG = this.getLocalVariableRegistry().lookupRegisteredLocalVariable(oldLocalVarIndex, ih.getPosition());
                    LocalVariableGen newLVG = localVarMap.get(oldLVG);
                    if (localVarMap.get(oldLVG) == null) {
                        final boolean copyInLocalValue = this.offsetInLocalVariableGenRange(oldLVG, (outlineChunkStartOffset != 0) ? (outlineChunkStartOffset - 1) : 0);
                        final boolean copyOutLocalValue = this.offsetInLocalVariableGenRange(oldLVG, outlineChunkEndOffset + 1);
                        if (copyInLocalValue || copyOutLocalValue) {
                            final String varName = oldLVG.getName();
                            final Type varType = oldLVG.getType();
                            newLVG = outlinedMethodGen.addLocalVariable(varName, varType, null, null);
                            final int newLocalVarIndex = newLVG.getIndex();
                            final String varSignature = varType.getSignature();
                            localVarMap.put(oldLVG, newLVG);
                            ++copyAreaFieldCount;
                            final String copyAreaFieldName = "field" + copyAreaFieldCount;
                            copyAreaCG.addField(new Field(1, copyAreaCPG.addUtf8(copyAreaFieldName), copyAreaCPG.addUtf8(varSignature), null, copyAreaCPG.getConstantPool()));
                            final int fieldRef = cpg.addFieldref(argTypeName, copyAreaFieldName, varSignature);
                            if (copyInLocalValue) {
                                oldMethCopyInIL.append(InstructionConstants.DUP);
                                final InstructionHandle copyInLoad = oldMethCopyInIL.append(loadLocal(oldLocalVarIndex, varType));
                                oldMethCopyInIL.append(new PUTFIELD(fieldRef));
                                if (!copyOutLocalValue) {
                                    revisedLocalVarEnd.put(oldLVG, copyInLoad);
                                }
                                newMethCopyInIL.append(InstructionConstants.ALOAD_1);
                                newMethCopyInIL.append(new GETFIELD(fieldRef));
                                newMethCopyInIL.append(storeLocal(newLocalVarIndex, varType));
                            }
                            if (copyOutLocalValue) {
                                newMethCopyOutIL.append(InstructionConstants.ALOAD_1);
                                newMethCopyOutIL.append(loadLocal(newLocalVarIndex, varType));
                                newMethCopyOutIL.append(new PUTFIELD(fieldRef));
                                oldMethCopyOutIL.append(InstructionConstants.DUP);
                                oldMethCopyOutIL.append(new GETFIELD(fieldRef));
                                final InstructionHandle copyOutStore = oldMethCopyOutIL.append(storeLocal(oldLocalVarIndex, varType));
                                if (!copyInLocalValue) {
                                    revisedLocalVarStart.put(oldLVG, copyOutStore);
                                }
                            }
                        }
                    }
                }
                if (ih.hasTargeters()) {
                    targetMap.put(ih, lastCopyHandle);
                }
                if (chunkStartTargetMappingsPending) {
                    do {
                        targetMap.put(pendingTargetMappingHandle, lastCopyHandle);
                        pendingTargetMappingHandle = pendingTargetMappingHandle.getNext();
                    } while (pendingTargetMappingHandle != ih);
                    chunkStartTargetMappingsPending = false;
                }
            }
        }
        InstructionHandle ih = first;
        InstructionHandle ch = newIL.getStart();
        while (ch != null) {
            final Instruction i = ih.getInstruction();
            final Instruction c2 = ch.getInstruction();
            if (i instanceof BranchInstruction) {
                final BranchInstruction bc = (BranchInstruction)c2;
                final BranchInstruction bi = (BranchInstruction)i;
                final InstructionHandle itarget = bi.getTarget();
                final InstructionHandle newTarget = targetMap.get(itarget);
                bc.setTarget(newTarget);
                if (bi instanceof Select) {
                    final InstructionHandle[] itargets = ((Select)bi).getTargets();
                    final InstructionHandle[] ctargets = ((Select)bc).getTargets();
                    for (int j = 0; j < itargets.length; ++j) {
                        ctargets[j] = targetMap.get(itargets[j]);
                    }
                }
            }
            else if (i instanceof LocalVariableInstruction || i instanceof RET) {
                final IndexedInstruction lvi2 = (IndexedInstruction)c2;
                final int oldLocalVarIndex2 = lvi2.getIndex();
                final LocalVariableGen oldLVG2 = this.getLocalVariableRegistry().lookupRegisteredLocalVariable(oldLocalVarIndex2, ih.getPosition());
                LocalVariableGen newLVG2 = localVarMap.get(oldLVG2);
                int newLocalVarIndex2;
                if (newLVG2 == null) {
                    final String varName = oldLVG2.getName();
                    final Type varType = oldLVG2.getType();
                    newLVG2 = outlinedMethodGen.addLocalVariable(varName, varType, null, null);
                    newLocalVarIndex2 = newLVG2.getIndex();
                    localVarMap.put(oldLVG2, newLVG2);
                    revisedLocalVarStart.put(oldLVG2, outlinedMethodRef);
                    revisedLocalVarEnd.put(oldLVG2, outlinedMethodRef);
                }
                else {
                    newLocalVarIndex2 = newLVG2.getIndex();
                }
                lvi2.setIndex(newLocalVarIndex2);
            }
            if (ih.hasTargeters()) {
                final InstructionTargeter[] targeters = ih.getTargeters();
                for (int idx = 0; idx < targeters.length; ++idx) {
                    final InstructionTargeter targeter = targeters[idx];
                    if (targeter instanceof LocalVariableGen && ((LocalVariableGen)targeter).getEnd() == ih) {
                        final Object newLVG3 = localVarMap.get(targeter);
                        if (newLVG3 != null) {
                            outlinedMethodGen.removeLocalVariable((LocalVariableGen)newLVG3);
                        }
                    }
                }
            }
            if (!(i instanceof MarkerInstruction)) {
                ch = ch.getNext();
            }
            ih = ih.getNext();
        }
        oldMethCopyOutIL.append(InstructionConstants.POP);
        for (final Map.Entry lvgRangeStartPair : revisedLocalVarStart.entrySet()) {
            final LocalVariableGen lvg = lvgRangeStartPair.getKey();
            final InstructionHandle startInst = lvgRangeStartPair.getValue();
            lvg.setStart(startInst);
        }
        for (final Map.Entry lvgRangeEndPair : revisedLocalVarEnd.entrySet()) {
            final LocalVariableGen lvg2 = lvgRangeEndPair.getKey();
            final InstructionHandle endInst = lvgRangeEndPair.getValue();
            lvg2.setEnd(endInst);
        }
        xsltc.dumpClass(copyAreaCG.getJavaClass());
        final InstructionList oldMethodIL = this.getInstructionList();
        oldMethodIL.insert(first, oldMethCopyInIL);
        oldMethodIL.insert(first, oldMethCopyOutIL);
        newIL.insert(newMethCopyInIL);
        newIL.append(newMethCopyOutIL);
        newIL.append(InstructionConstants.RETURN);
        try {
            oldMethodIL.delete(first, last);
        }
        catch (final TargetLostException e) {
            final InstructionHandle[] targets = e.getTargets();
            for (int k = 0; k < targets.length; ++k) {
                final InstructionHandle lostTarget = targets[k];
                final InstructionTargeter[] targeters2 = lostTarget.getTargeters();
                for (int j = 0; j < targeters2.length; ++j) {
                    if (targeters2[j] instanceof LocalVariableGen) {
                        final LocalVariableGen lvgTargeter = (LocalVariableGen)targeters2[j];
                        if (lvgTargeter.getStart() == lostTarget) {
                            lvgTargeter.setStart(outlinedMethodRef);
                        }
                        if (lvgTargeter.getEnd() == lostTarget) {
                            lvgTargeter.setEnd(outlinedMethodRef);
                        }
                    }
                    else {
                        targeters2[j].updateTarget(lostTarget, outlinedMethodCallSetup);
                    }
                }
            }
        }
        final String[] exceptions = this.getExceptions();
        for (int l = 0; l < exceptions.length; ++l) {
            outlinedMethodGen.addException(exceptions[l]);
        }
        return outlinedMethodGen.getThisMethod();
    }
    
    private static Instruction loadLocal(final int index, final Type type) {
        if (type == Type.BOOLEAN) {
            return new ILOAD(index);
        }
        if (type == Type.INT) {
            return new ILOAD(index);
        }
        if (type == Type.SHORT) {
            return new ILOAD(index);
        }
        if (type == Type.LONG) {
            return new LLOAD(index);
        }
        if (type == Type.BYTE) {
            return new ILOAD(index);
        }
        if (type == Type.CHAR) {
            return new ILOAD(index);
        }
        if (type == Type.FLOAT) {
            return new FLOAD(index);
        }
        if (type == Type.DOUBLE) {
            return new DLOAD(index);
        }
        return new ALOAD(index);
    }
    
    private static Instruction storeLocal(final int index, final Type type) {
        if (type == Type.BOOLEAN) {
            return new ISTORE(index);
        }
        if (type == Type.INT) {
            return new ISTORE(index);
        }
        if (type == Type.SHORT) {
            return new ISTORE(index);
        }
        if (type == Type.LONG) {
            return new LSTORE(index);
        }
        if (type == Type.BYTE) {
            return new ISTORE(index);
        }
        if (type == Type.CHAR) {
            return new ISTORE(index);
        }
        if (type == Type.FLOAT) {
            return new FSTORE(index);
        }
        if (type == Type.DOUBLE) {
            return new DSTORE(index);
        }
        return new ASTORE(index);
    }
    
    public void markChunkStart() {
        this.getInstructionList().append(OutlineableChunkStart.OUTLINEABLECHUNKSTART);
        ++this.m_totalChunks;
        ++this.m_openChunks;
    }
    
    public void markChunkEnd() {
        this.getInstructionList().append(OutlineableChunkEnd.OUTLINEABLECHUNKEND);
        --this.m_openChunks;
        if (this.m_openChunks < 0) {
            final String msg = new ErrorMsg("OUTLINE_ERR_UNBALANCED_MARKERS").toString();
            throw new InternalError(msg);
        }
    }
    
    Method[] getGeneratedMethods(final ClassGenerator classGen) {
        final InstructionList il = this.getInstructionList();
        InstructionHandle last = il.getEnd();
        il.setPositions();
        int instructionListSize = last.getPosition() + last.getInstruction().getLength();
        if (instructionListSize > 32767) {
            final boolean ilChanged = this.widenConditionalBranchTargetOffsets();
            if (ilChanged) {
                il.setPositions();
                last = il.getEnd();
                instructionListSize = last.getPosition() + last.getInstruction().getLength();
            }
        }
        Method[] generatedMethods;
        if (instructionListSize > 65535) {
            generatedMethods = this.outlineChunks(classGen, instructionListSize);
        }
        else {
            generatedMethods = new Method[] { this.getThisMethod() };
        }
        return generatedMethods;
    }
    
    protected Method getThisMethod() {
        this.stripAttributes(true);
        this.setMaxLocals();
        this.setMaxStack();
        this.removeNOPs();
        return this.getMethod();
    }
    
    boolean widenConditionalBranchTargetOffsets() {
        boolean ilChanged = false;
        int maxOffsetChange = 0;
        final InstructionList il = this.getInstructionList();
        for (InstructionHandle ih = il.getStart(); ih != null; ih = ih.getNext()) {
            final Instruction inst = ih.getInstruction();
            switch (inst.getOpcode()) {
                case 167:
                case 168: {
                    maxOffsetChange += 2;
                    break;
                }
                case 170:
                case 171: {
                    maxOffsetChange += 3;
                    break;
                }
                case 153:
                case 154:
                case 155:
                case 156:
                case 157:
                case 158:
                case 159:
                case 160:
                case 161:
                case 162:
                case 163:
                case 164:
                case 165:
                case 166:
                case 198:
                case 199: {
                    maxOffsetChange += 5;
                    break;
                }
            }
        }
        for (InstructionHandle ih = il.getStart(); ih != null; ih = ih.getNext()) {
            final Instruction inst = ih.getInstruction();
            if (inst instanceof IfInstruction) {
                final IfInstruction oldIfInst = (IfInstruction)inst;
                final BranchHandle oldIfHandle = (BranchHandle)ih;
                final InstructionHandle target = oldIfInst.getTarget();
                final int relativeTargetOffset = target.getPosition() - oldIfHandle.getPosition();
                if (relativeTargetOffset - maxOffsetChange < -32768 || relativeTargetOffset + maxOffsetChange > 32767) {
                    InstructionHandle nextHandle = oldIfHandle.getNext();
                    final IfInstruction invertedIfInst = oldIfInst.negate();
                    final BranchHandle invertedIfHandle = il.append(oldIfHandle, invertedIfInst);
                    final BranchHandle gotoHandle = il.append(invertedIfHandle, new GOTO(target));
                    if (nextHandle == null) {
                        nextHandle = il.append(gotoHandle, MethodGenerator.NOP);
                    }
                    invertedIfHandle.updateTarget(target, nextHandle);
                    if (oldIfHandle.hasTargeters()) {
                        final InstructionTargeter[] targeters = oldIfHandle.getTargeters();
                        for (int i = 0; i < targeters.length; ++i) {
                            final InstructionTargeter targeter = targeters[i];
                            if (targeter instanceof LocalVariableGen) {
                                final LocalVariableGen lvg = (LocalVariableGen)targeter;
                                if (lvg.getStart() == oldIfHandle) {
                                    lvg.setStart(invertedIfHandle);
                                }
                                else if (lvg.getEnd() == oldIfHandle) {
                                    lvg.setEnd(gotoHandle);
                                }
                            }
                            else {
                                targeter.updateTarget(oldIfHandle, invertedIfHandle);
                            }
                        }
                    }
                    try {
                        il.delete(oldIfHandle);
                    }
                    catch (final TargetLostException tle) {
                        final String msg = new ErrorMsg("OUTLINE_ERR_DELETED_TARGET", tle.getMessage()).toString();
                        throw new InternalError(msg);
                    }
                    ih = gotoHandle;
                    ilChanged = true;
                }
            }
        }
        return ilChanged;
    }
    
    protected class LocalVariableRegistry
    {
        protected ArrayList _variables;
        protected HashMap _nameToLVGMap;
        
        protected LocalVariableRegistry() {
            this._variables = new ArrayList();
            this._nameToLVGMap = new HashMap();
        }
        
        protected void registerLocalVariable(final LocalVariableGen lvg) {
            final int slot = lvg.getIndex();
            final int registrySize = this._variables.size();
            if (slot >= registrySize) {
                for (int i = registrySize; i < slot; ++i) {
                    this._variables.add(null);
                }
                this._variables.add(lvg);
            }
            else {
                final Object localsInSlot = this._variables.get(slot);
                if (localsInSlot != null) {
                    if (localsInSlot instanceof LocalVariableGen) {
                        final ArrayList listOfLocalsInSlot = new ArrayList();
                        listOfLocalsInSlot.add(localsInSlot);
                        listOfLocalsInSlot.add(lvg);
                        this._variables.set(slot, listOfLocalsInSlot);
                    }
                    else {
                        ((ArrayList)localsInSlot).add(lvg);
                    }
                }
                else {
                    this._variables.set(slot, lvg);
                }
            }
            this.registerByName(lvg);
        }
        
        protected LocalVariableGen lookupRegisteredLocalVariable(final int slot, final int offset) {
            final Object localsInSlot = (this._variables != null) ? this._variables.get(slot) : null;
            if (localsInSlot != null) {
                if (localsInSlot instanceof LocalVariableGen) {
                    final LocalVariableGen lvg = (LocalVariableGen)localsInSlot;
                    if (MethodGenerator.this.offsetInLocalVariableGenRange(lvg, offset)) {
                        return lvg;
                    }
                }
                else {
                    final ArrayList listOfLocalsInSlot = (ArrayList)localsInSlot;
                    for (int size = listOfLocalsInSlot.size(), i = 0; i < size; ++i) {
                        final LocalVariableGen lvg2 = listOfLocalsInSlot.get(i);
                        if (MethodGenerator.this.offsetInLocalVariableGenRange(lvg2, offset)) {
                            return lvg2;
                        }
                    }
                }
            }
            return null;
        }
        
        protected void registerByName(final LocalVariableGen lvg) {
            final Object duplicateNameEntry = this._nameToLVGMap.get(lvg.getName());
            if (duplicateNameEntry == null) {
                this._nameToLVGMap.put(lvg.getName(), lvg);
            }
            else {
                ArrayList sameNameList;
                if (duplicateNameEntry instanceof ArrayList) {
                    sameNameList = (ArrayList)duplicateNameEntry;
                    sameNameList.add(lvg);
                }
                else {
                    sameNameList = new ArrayList();
                    sameNameList.add(duplicateNameEntry);
                    sameNameList.add(lvg);
                }
                this._nameToLVGMap.put(lvg.getName(), sameNameList);
            }
        }
        
        protected void removeByNameTracking(final LocalVariableGen lvg) {
            final Object duplicateNameEntry = this._nameToLVGMap.get(lvg.getName());
            if (duplicateNameEntry instanceof ArrayList) {
                final ArrayList sameNameList = (ArrayList)duplicateNameEntry;
                for (int i = 0; i < sameNameList.size(); ++i) {
                    if (sameNameList.get(i) == lvg) {
                        sameNameList.remove(i);
                        break;
                    }
                }
            }
            else {
                this._nameToLVGMap.remove(lvg);
            }
        }
        
        protected LocalVariableGen lookUpByName(final String name) {
            LocalVariableGen lvg = null;
            final Object duplicateNameEntry = this._nameToLVGMap.get(name);
            if (duplicateNameEntry instanceof ArrayList) {
                final ArrayList sameNameList = (ArrayList)duplicateNameEntry;
                for (int i = 0; i < sameNameList.size(); ++i) {
                    lvg = sameNameList.get(i);
                    if (lvg.getName() == name) {
                        break;
                    }
                }
            }
            else {
                lvg = (LocalVariableGen)duplicateNameEntry;
            }
            return lvg;
        }
        
        protected LocalVariableGen[] getLocals(final boolean includeRemoved) {
            LocalVariableGen[] locals = null;
            final ArrayList allVarsEverDeclared = new ArrayList();
            if (includeRemoved) {
                for (int slotCount = allVarsEverDeclared.size(), i = 0; i < slotCount; ++i) {
                    final Object slotEntries = this._variables.get(i);
                    if (slotEntries != null) {
                        if (slotEntries instanceof ArrayList) {
                            final ArrayList slotList = (ArrayList)slotEntries;
                            for (int j = 0; j < slotList.size(); ++j) {
                                allVarsEverDeclared.add(slotList.get(i));
                            }
                        }
                        else {
                            allVarsEverDeclared.add(slotEntries);
                        }
                    }
                }
            }
            else {
                for (final Map.Entry nameVarsPair : this._nameToLVGMap.entrySet()) {
                    final Object vars = nameVarsPair.getValue();
                    if (vars != null) {
                        if (vars instanceof ArrayList) {
                            final ArrayList varsList = (ArrayList)vars;
                            for (int k = 0; k < varsList.size(); ++k) {
                                allVarsEverDeclared.add(varsList.get(k));
                            }
                        }
                        else {
                            allVarsEverDeclared.add(vars);
                        }
                    }
                }
            }
            locals = new LocalVariableGen[allVarsEverDeclared.size()];
            allVarsEverDeclared.toArray(locals);
            return locals;
        }
    }
    
    private class Chunk implements Comparable
    {
        private InstructionHandle m_start;
        private InstructionHandle m_end;
        private int m_size;
        
        Chunk(final InstructionHandle start, final InstructionHandle end) {
            this.m_start = start;
            this.m_end = end;
            this.m_size = end.getPosition() - start.getPosition();
        }
        
        boolean isAdjacentTo(final Chunk neighbour) {
            return this.getChunkEnd().getNext() == neighbour.getChunkStart();
        }
        
        InstructionHandle getChunkStart() {
            return this.m_start;
        }
        
        InstructionHandle getChunkEnd() {
            return this.m_end;
        }
        
        int getChunkSize() {
            return this.m_size;
        }
        
        @Override
        public int compareTo(final Object comparand) {
            return this.getChunkSize() - ((Chunk)comparand).getChunkSize();
        }
    }
}
