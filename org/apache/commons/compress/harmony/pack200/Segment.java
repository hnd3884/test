package org.apache.commons.compress.harmony.pack200;

import java.util.ArrayList;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import java.util.List;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.AnnotationVisitor;
import java.util.Iterator;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;

public class Segment implements ClassVisitor
{
    private SegmentHeader segmentHeader;
    private CpBands cpBands;
    private AttributeDefinitionBands attributeDefinitionBands;
    private IcBands icBands;
    private ClassBands classBands;
    private BcBands bcBands;
    private FileBands fileBands;
    private final SegmentFieldVisitor fieldVisitor;
    private final SegmentMethodVisitor methodVisitor;
    private Pack200ClassReader currentClassReader;
    private PackingOptions options;
    private boolean stripDebug;
    private Attribute[] nonStandardAttributePrototypes;
    
    public Segment() {
        this.fieldVisitor = new SegmentFieldVisitor();
        this.methodVisitor = new SegmentMethodVisitor();
    }
    
    public void pack(final Archive.SegmentUnit segmentUnit, final OutputStream out, final PackingOptions options) throws IOException, Pack200Exception {
        this.options = options;
        this.stripDebug = options.isStripDebug();
        final int effort = options.getEffort();
        this.nonStandardAttributePrototypes = options.getUnknownAttributePrototypes();
        PackingUtils.log("Start to pack a new segment with " + segmentUnit.fileListSize() + " files including " + segmentUnit.classListSize() + " classes");
        PackingUtils.log("Initialize a header for the segment");
        (this.segmentHeader = new SegmentHeader()).setFile_count(segmentUnit.fileListSize());
        this.segmentHeader.setHave_all_code_flags(!this.stripDebug);
        if (!options.isKeepDeflateHint()) {
            this.segmentHeader.setDeflate_hint("true".equals(options.getDeflateHint()));
        }
        PackingUtils.log("Setup constant pool bands for the segment");
        this.cpBands = new CpBands(this, effort);
        PackingUtils.log("Setup attribute definition bands for the segment");
        this.attributeDefinitionBands = new AttributeDefinitionBands(this, effort, this.nonStandardAttributePrototypes);
        PackingUtils.log("Setup internal class bands for the segment");
        this.icBands = new IcBands(this.segmentHeader, this.cpBands, effort);
        PackingUtils.log("Setup class bands for the segment");
        this.classBands = new ClassBands(this, segmentUnit.classListSize(), effort, this.stripDebug);
        PackingUtils.log("Setup byte code bands for the segment");
        this.bcBands = new BcBands(this.cpBands, this, effort);
        PackingUtils.log("Setup file bands for the segment");
        this.fileBands = new FileBands(this.cpBands, this.segmentHeader, options, segmentUnit, effort);
        this.processClasses(segmentUnit, this.nonStandardAttributePrototypes);
        this.cpBands.finaliseBands();
        this.attributeDefinitionBands.finaliseBands();
        this.icBands.finaliseBands();
        this.classBands.finaliseBands();
        this.bcBands.finaliseBands();
        this.fileBands.finaliseBands();
        final ByteArrayOutputStream bandsOutputStream = new ByteArrayOutputStream();
        PackingUtils.log("Packing...");
        final int finalNumberOfClasses = this.classBands.numClassesProcessed();
        this.segmentHeader.setClass_count(finalNumberOfClasses);
        this.cpBands.pack(bandsOutputStream);
        if (finalNumberOfClasses > 0) {
            this.attributeDefinitionBands.pack(bandsOutputStream);
            this.icBands.pack(bandsOutputStream);
            this.classBands.pack(bandsOutputStream);
            this.bcBands.pack(bandsOutputStream);
        }
        this.fileBands.pack(bandsOutputStream);
        final ByteArrayOutputStream headerOutputStream = new ByteArrayOutputStream();
        this.segmentHeader.pack(headerOutputStream);
        headerOutputStream.writeTo(out);
        bandsOutputStream.writeTo(out);
        segmentUnit.addPackedByteAmount(headerOutputStream.size());
        segmentUnit.addPackedByteAmount(bandsOutputStream.size());
        PackingUtils.log("Wrote total of " + segmentUnit.getPackedByteAmount() + " bytes");
        PackingUtils.log("Transmitted " + segmentUnit.fileListSize() + " files of " + segmentUnit.getByteAmount() + " input bytes in a segment of " + segmentUnit.getPackedByteAmount() + " bytes");
    }
    
    private void processClasses(final Archive.SegmentUnit segmentUnit, final Attribute[] attributes) throws Pack200Exception {
        this.segmentHeader.setClass_count(segmentUnit.classListSize());
        for (final Pack200ClassReader classReader : segmentUnit.getClassList()) {
            this.currentClassReader = classReader;
            int flags = 0;
            if (this.stripDebug) {
                flags |= 0x2;
            }
            try {
                classReader.accept((ClassVisitor)this, attributes, flags);
            }
            catch (final PassException pe) {
                this.classBands.removeCurrentClass();
                final String name = classReader.getFileName();
                this.options.addPassFile(name);
                this.cpBands.addCPUtf8(name);
                boolean found = false;
                for (final Archive.PackingFile file : segmentUnit.getFileList()) {
                    if (file.getName().equals(name)) {
                        found = true;
                        file.setContents(classReader.b);
                        break;
                    }
                }
                if (!found) {
                    throw new Pack200Exception("Error passing file " + name);
                }
                continue;
            }
        }
    }
    
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.bcBands.setCurrentClass(name, superName);
        this.segmentHeader.addMajorVersion(version);
        this.classBands.addClass(version, access, name, signature, superName, interfaces);
    }
    
    public void visitSource(final String source, final String debug) {
        if (!this.stripDebug) {
            this.classBands.addSourceFile(source);
        }
    }
    
    public void visitOuterClass(final String owner, final String name, final String desc) {
        this.classBands.addEnclosingMethod(owner, name, desc);
    }
    
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        return (AnnotationVisitor)new SegmentAnnotationVisitor(0, desc, visible);
    }
    
    public void visitAttribute(final Attribute attribute) {
        if (attribute.isUnknown()) {
            final String action = this.options.getUnknownAttributeAction();
            if (action.equals("pass")) {
                this.passCurrentClass();
            }
            else if (action.equals("error")) {
                throw new Error("Unknown attribute encountered");
            }
        }
        else {
            if (!(attribute instanceof NewAttribute)) {
                throw new RuntimeException("Unexpected attribute encountered: " + attribute.type);
            }
            final NewAttribute newAttribute = (NewAttribute)attribute;
            if (newAttribute.isUnknown(0)) {
                final String action2 = this.options.getUnknownClassAttributeAction(newAttribute.type);
                if (action2.equals("pass")) {
                    this.passCurrentClass();
                }
                else if (action2.equals("error")) {
                    throw new Error("Unknown attribute encountered");
                }
            }
            this.classBands.addClassAttribute(newAttribute);
        }
    }
    
    public void visitInnerClass(final String name, final String outerName, final String innerName, final int flags) {
        this.icBands.addInnerClass(name, outerName, innerName, flags);
    }
    
    public FieldVisitor visitField(final int flags, final String name, final String desc, final String signature, final Object value) {
        this.classBands.addField(flags, name, desc, signature, value);
        return (FieldVisitor)this.fieldVisitor;
    }
    
    public MethodVisitor visitMethod(final int flags, final String name, final String desc, final String signature, final String[] exceptions) {
        this.classBands.addMethod(flags, name, desc, signature, exceptions);
        return (MethodVisitor)this.methodVisitor;
    }
    
    public void visitEnd() {
        this.classBands.endOfClass();
    }
    
    public ClassBands getClassBands() {
        return this.classBands;
    }
    
    private void addValueAndTag(final Object value, final List T, final List values) {
        if (value instanceof Integer) {
            T.add("I");
            values.add(value);
        }
        else if (value instanceof Double) {
            T.add("D");
            values.add(value);
        }
        else if (value instanceof Float) {
            T.add("F");
            values.add(value);
        }
        else if (value instanceof Long) {
            T.add("J");
            values.add(value);
        }
        else if (value instanceof Byte) {
            T.add("B");
            values.add((int)value);
        }
        else if (value instanceof Character) {
            T.add("C");
            values.add((int)(char)value);
        }
        else if (value instanceof Short) {
            T.add("S");
            values.add((int)value);
        }
        else if (value instanceof Boolean) {
            T.add("Z");
            values.add((int)(((boolean)value) ? 1 : 0));
        }
        else if (value instanceof String) {
            T.add("s");
            values.add(value);
        }
        else if (value instanceof Type) {
            T.add("c");
            values.add(((Type)value).toString());
        }
    }
    
    public boolean lastConstantHadWideIndex() {
        return this.currentClassReader.lastConstantHadWideIndex();
    }
    
    public CpBands getCpBands() {
        return this.cpBands;
    }
    
    public SegmentHeader getSegmentHeader() {
        return this.segmentHeader;
    }
    
    public AttributeDefinitionBands getAttrBands() {
        return this.attributeDefinitionBands;
    }
    
    public IcBands getIcBands() {
        return this.icBands;
    }
    
    public Pack200ClassReader getCurrentClassReader() {
        return this.currentClassReader;
    }
    
    private void passCurrentClass() {
        throw new PassException();
    }
    
    public class SegmentMethodVisitor implements MethodVisitor
    {
        public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
            return (AnnotationVisitor)new SegmentAnnotationVisitor(2, desc, visible);
        }
        
        public AnnotationVisitor visitAnnotationDefault() {
            return (AnnotationVisitor)new SegmentAnnotationVisitor(2);
        }
        
        public void visitAttribute(final Attribute attribute) {
            if (attribute.isUnknown()) {
                final String action = Segment.this.options.getUnknownAttributeAction();
                if (action.equals("pass")) {
                    Segment.this.passCurrentClass();
                }
                else if (action.equals("error")) {
                    throw new Error("Unknown attribute encountered");
                }
            }
            else {
                if (!(attribute instanceof NewAttribute)) {
                    throw new RuntimeException("Unexpected attribute encountered: " + attribute.type);
                }
                final NewAttribute newAttribute = (NewAttribute)attribute;
                if (attribute.isCodeAttribute()) {
                    if (newAttribute.isUnknown(3)) {
                        final String action2 = Segment.this.options.getUnknownCodeAttributeAction(newAttribute.type);
                        if (action2.equals("pass")) {
                            Segment.this.passCurrentClass();
                        }
                        else if (action2.equals("error")) {
                            throw new Error("Unknown attribute encountered");
                        }
                    }
                    Segment.this.classBands.addCodeAttribute(newAttribute);
                }
                else {
                    if (newAttribute.isUnknown(2)) {
                        final String action2 = Segment.this.options.getUnknownMethodAttributeAction(newAttribute.type);
                        if (action2.equals("pass")) {
                            Segment.this.passCurrentClass();
                        }
                        else if (action2.equals("error")) {
                            throw new Error("Unknown attribute encountered");
                        }
                    }
                    Segment.this.classBands.addMethodAttribute(newAttribute);
                }
            }
        }
        
        public void visitCode() {
            Segment.this.classBands.addCode();
        }
        
        public void visitFrame(final int arg0, final int arg1, final Object[] arg2, final int arg3, final Object[] arg4) {
        }
        
        public void visitLabel(final Label label) {
            Segment.this.bcBands.visitLabel(label);
        }
        
        public void visitLineNumber(final int line, final Label start) {
            if (!Segment.this.stripDebug) {
                Segment.this.classBands.addLineNumber(line, start);
            }
        }
        
        public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index) {
            if (!Segment.this.stripDebug) {
                Segment.this.classBands.addLocalVariable(name, desc, signature, start, end, index);
            }
        }
        
        public void visitMaxs(final int maxStack, final int maxLocals) {
            Segment.this.classBands.addMaxStack(maxStack, maxLocals);
        }
        
        public AnnotationVisitor visitParameterAnnotation(final int parameter, final String desc, final boolean visible) {
            return (AnnotationVisitor)new SegmentAnnotationVisitor(2, parameter, desc, visible);
        }
        
        public void visitTryCatchBlock(final Label start, final Label end, final Label handler, final String type) {
            Segment.this.classBands.addHandler(start, end, handler, type);
        }
        
        public void visitEnd() {
            Segment.this.classBands.endOfMethod();
            Segment.this.bcBands.visitEnd();
        }
        
        public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
            Segment.this.bcBands.visitFieldInsn(opcode, owner, name, desc);
        }
        
        public void visitIincInsn(final int var, final int increment) {
            Segment.this.bcBands.visitIincInsn(var, increment);
        }
        
        public void visitInsn(final int opcode) {
            Segment.this.bcBands.visitInsn(opcode);
        }
        
        public void visitIntInsn(final int opcode, final int operand) {
            Segment.this.bcBands.visitIntInsn(opcode, operand);
        }
        
        public void visitJumpInsn(final int opcode, final Label label) {
            Segment.this.bcBands.visitJumpInsn(opcode, label);
        }
        
        public void visitLdcInsn(final Object cst) {
            Segment.this.bcBands.visitLdcInsn(cst);
        }
        
        public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
            Segment.this.bcBands.visitLookupSwitchInsn(dflt, keys, labels);
        }
        
        public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
            Segment.this.bcBands.visitMethodInsn(opcode, owner, name, desc);
        }
        
        public void visitMultiANewArrayInsn(final String desc, final int dimensions) {
            Segment.this.bcBands.visitMultiANewArrayInsn(desc, dimensions);
        }
        
        public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label[] labels) {
            Segment.this.bcBands.visitTableSwitchInsn(min, max, dflt, labels);
        }
        
        public void visitTypeInsn(final int opcode, final String type) {
            Segment.this.bcBands.visitTypeInsn(opcode, type);
        }
        
        public void visitVarInsn(final int opcode, final int var) {
            Segment.this.bcBands.visitVarInsn(opcode, var);
        }
    }
    
    public class SegmentAnnotationVisitor implements AnnotationVisitor
    {
        private int context;
        private int parameter;
        private String desc;
        private boolean visible;
        private final List nameRU;
        private final List T;
        private final List values;
        private final List caseArrayN;
        private final List nestTypeRS;
        private final List nestNameRU;
        private final List nestPairN;
        
        public SegmentAnnotationVisitor(final int context, final String desc, final boolean visible) {
            this.context = -1;
            this.parameter = -1;
            this.nameRU = new ArrayList();
            this.T = new ArrayList();
            this.values = new ArrayList();
            this.caseArrayN = new ArrayList();
            this.nestTypeRS = new ArrayList();
            this.nestNameRU = new ArrayList();
            this.nestPairN = new ArrayList();
            this.context = context;
            this.desc = desc;
            this.visible = visible;
        }
        
        public SegmentAnnotationVisitor(final int context) {
            this.context = -1;
            this.parameter = -1;
            this.nameRU = new ArrayList();
            this.T = new ArrayList();
            this.values = new ArrayList();
            this.caseArrayN = new ArrayList();
            this.nestTypeRS = new ArrayList();
            this.nestNameRU = new ArrayList();
            this.nestPairN = new ArrayList();
            this.context = context;
        }
        
        public SegmentAnnotationVisitor(final int context, final int parameter, final String desc, final boolean visible) {
            this.context = -1;
            this.parameter = -1;
            this.nameRU = new ArrayList();
            this.T = new ArrayList();
            this.values = new ArrayList();
            this.caseArrayN = new ArrayList();
            this.nestTypeRS = new ArrayList();
            this.nestNameRU = new ArrayList();
            this.nestPairN = new ArrayList();
            this.context = context;
            this.parameter = parameter;
            this.desc = desc;
            this.visible = visible;
        }
        
        public void visit(String name, final Object value) {
            if (name == null) {
                name = "";
            }
            this.nameRU.add(name);
            Segment.this.addValueAndTag(value, this.T, this.values);
        }
        
        public AnnotationVisitor visitAnnotation(String name, final String desc) {
            this.T.add("@");
            if (name == null) {
                name = "";
            }
            this.nameRU.add(name);
            this.nestTypeRS.add(desc);
            this.nestPairN.add(0);
            return (AnnotationVisitor)new AnnotationVisitor() {
                public void visit(final String name, final Object value) {
                    final Integer numPairs = SegmentAnnotationVisitor.this.nestPairN.remove(SegmentAnnotationVisitor.this.nestPairN.size() - 1);
                    SegmentAnnotationVisitor.this.nestPairN.add(numPairs + 1);
                    SegmentAnnotationVisitor.this.nestNameRU.add(name);
                    Segment.this.addValueAndTag(value, SegmentAnnotationVisitor.this.T, SegmentAnnotationVisitor.this.values);
                }
                
                public AnnotationVisitor visitAnnotation(final String arg0, final String arg1) {
                    throw new RuntimeException("Not yet supported");
                }
                
                public AnnotationVisitor visitArray(final String arg0) {
                    throw new RuntimeException("Not yet supported");
                }
                
                public void visitEnd() {
                }
                
                public void visitEnum(final String name, final String desc, final String value) {
                    final Integer numPairs = SegmentAnnotationVisitor.this.nestPairN.remove(SegmentAnnotationVisitor.this.nestPairN.size() - 1);
                    SegmentAnnotationVisitor.this.nestPairN.add(numPairs + 1);
                    SegmentAnnotationVisitor.this.T.add("e");
                    SegmentAnnotationVisitor.this.nestNameRU.add(name);
                    SegmentAnnotationVisitor.this.values.add(desc);
                    SegmentAnnotationVisitor.this.values.add(value);
                }
            };
        }
        
        public AnnotationVisitor visitArray(String name) {
            this.T.add("[");
            if (name == null) {
                name = "";
            }
            this.nameRU.add(name);
            this.caseArrayN.add(0);
            return (AnnotationVisitor)new ArrayVisitor(this.caseArrayN, this.T, this.nameRU, this.values);
        }
        
        public void visitEnd() {
            if (this.desc == null) {
                Segment.this.classBands.addAnnotationDefault(this.nameRU, this.T, this.values, this.caseArrayN, this.nestTypeRS, this.nestNameRU, this.nestPairN);
            }
            else if (this.parameter != -1) {
                Segment.this.classBands.addParameterAnnotation(this.parameter, this.desc, this.visible, this.nameRU, this.T, this.values, this.caseArrayN, this.nestTypeRS, this.nestNameRU, this.nestPairN);
            }
            else {
                Segment.this.classBands.addAnnotation(this.context, this.desc, this.visible, this.nameRU, this.T, this.values, this.caseArrayN, this.nestTypeRS, this.nestNameRU, this.nestPairN);
            }
        }
        
        public void visitEnum(String name, final String desc, final String value) {
            this.T.add("e");
            if (name == null) {
                name = "";
            }
            this.nameRU.add(name);
            this.values.add(desc);
            this.values.add(value);
        }
    }
    
    public class ArrayVisitor implements AnnotationVisitor
    {
        private final int indexInCaseArrayN;
        private final List caseArrayN;
        private final List values;
        private final List nameRU;
        private final List T;
        
        public ArrayVisitor(final List caseArrayN, final List T, final List nameRU, final List values) {
            this.caseArrayN = caseArrayN;
            this.T = T;
            this.nameRU = nameRU;
            this.values = values;
            this.indexInCaseArrayN = caseArrayN.size() - 1;
        }
        
        public void visit(String name, final Object value) {
            final Integer numCases = this.caseArrayN.remove(this.indexInCaseArrayN);
            this.caseArrayN.add(this.indexInCaseArrayN, numCases + 1);
            if (name == null) {
                name = "";
            }
            Segment.this.addValueAndTag(value, this.T, this.values);
        }
        
        public AnnotationVisitor visitAnnotation(final String arg0, final String arg1) {
            throw new RuntimeException("Not yet supported");
        }
        
        public AnnotationVisitor visitArray(String name) {
            this.T.add("[");
            if (name == null) {
                name = "";
            }
            this.nameRU.add(name);
            this.caseArrayN.add(0);
            return (AnnotationVisitor)new ArrayVisitor(this.caseArrayN, this.T, this.nameRU, this.values);
        }
        
        public void visitEnd() {
        }
        
        public void visitEnum(final String name, final String desc, final String value) {
            final Integer numCases = this.caseArrayN.remove(this.caseArrayN.size() - 1);
            this.caseArrayN.add(numCases + 1);
            this.T.add("e");
            this.values.add(desc);
            this.values.add(value);
        }
    }
    
    public class SegmentFieldVisitor implements FieldVisitor
    {
        public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
            return (AnnotationVisitor)new SegmentAnnotationVisitor(1, desc, visible);
        }
        
        public void visitAttribute(final Attribute attribute) {
            if (attribute.isUnknown()) {
                final String action = Segment.this.options.getUnknownAttributeAction();
                if (action.equals("pass")) {
                    Segment.this.passCurrentClass();
                }
                else if (action.equals("error")) {
                    throw new Error("Unknown attribute encountered");
                }
            }
            else {
                if (!(attribute instanceof NewAttribute)) {
                    throw new RuntimeException("Unexpected attribute encountered: " + attribute.type);
                }
                final NewAttribute newAttribute = (NewAttribute)attribute;
                if (newAttribute.isUnknown(1)) {
                    final String action2 = Segment.this.options.getUnknownFieldAttributeAction(newAttribute.type);
                    if (action2.equals("pass")) {
                        Segment.this.passCurrentClass();
                    }
                    else if (action2.equals("error")) {
                        throw new Error("Unknown attribute encountered");
                    }
                }
                Segment.this.classBands.addFieldAttribute(newAttribute);
            }
        }
        
        public void visitEnd() {
        }
    }
    
    public static class PassException extends RuntimeException
    {
    }
}
