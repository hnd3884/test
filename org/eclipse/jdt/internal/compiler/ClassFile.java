package org.eclipse.jdt.internal.compiler;

import java.util.HashSet;
import java.util.Iterator;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.codegen.VerificationTypeInfo;
import org.eclipse.jdt.internal.compiler.codegen.StackMapFrame;
import java.util.HashMap;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import java.util.Collections;
import org.eclipse.jdt.internal.compiler.ast.FunctionalExpression;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.impl.StringConstant;
import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.problem.ShouldNotImplement;
import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.Receiver;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.codegen.AttributeNamesConstants;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.codegen.ExceptionLabel;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
import org.eclipse.jdt.internal.compiler.problem.AbortType;
import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.codegen.AnnotationContext;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import java.util.Arrays;
import org.eclipse.jdt.core.compiler.CharOperation;
import java.util.Comparator;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.codegen.StackMapFrameCodeStream;
import org.eclipse.jdt.internal.compiler.codegen.TypeAnnotationCodeStream;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import java.util.Set;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import java.util.List;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import java.util.Map;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

public class ClassFile implements TypeConstants, TypeIds
{
    private byte[] bytes;
    public CodeStream codeStream;
    public ConstantPool constantPool;
    public int constantPoolOffset;
    public byte[] contents;
    public int contentsOffset;
    protected boolean creatingProblemType;
    public ClassFile enclosingClassFile;
    public byte[] header;
    public int headerOffset;
    public Map<TypeBinding, Boolean> innerClassesBindings;
    public List bootstrapMethods;
    public int methodCount;
    public int methodCountOffset;
    boolean isShared;
    public int produceAttributes;
    public SourceTypeBinding referenceBinding;
    public boolean isNestedType;
    public long targetJDK;
    public List<TypeBinding> missingTypes;
    public Set visitedTypes;
    public static final int INITIAL_CONTENTS_SIZE = 400;
    public static final int INITIAL_HEADER_SIZE = 1500;
    public static final int INNER_CLASSES_SIZE = 5;
    
    public static void createProblemType(final TypeDeclaration typeDeclaration, final CompilationResult unitResult) {
        final SourceTypeBinding typeBinding = typeDeclaration.binding;
        final ClassFile classFile = getNewInstance(typeBinding);
        classFile.initialize(typeBinding, null, true);
        if (typeBinding.hasMemberTypes()) {
            final ReferenceBinding[] members = typeBinding.memberTypes;
            for (int i = 0, l = members.length; i < l; ++i) {
                classFile.recordInnerClasses(members[i]);
            }
        }
        if (typeBinding.isNestedType()) {
            classFile.recordInnerClasses(typeBinding);
        }
        final TypeVariableBinding[] typeVariables = typeBinding.typeVariables();
        for (int i = 0, max = typeVariables.length; i < max; ++i) {
            final TypeVariableBinding typeVariableBinding = typeVariables[i];
            if ((typeVariableBinding.tagBits & 0x800L) != 0x0L) {
                Util.recordNestedType(classFile, typeVariableBinding);
            }
        }
        final FieldBinding[] fields = typeBinding.fields();
        if (fields != null && fields != Binding.NO_FIELDS) {
            classFile.addFieldInfos();
        }
        else {
            classFile.contents[classFile.contentsOffset++] = 0;
            classFile.contents[classFile.contentsOffset++] = 0;
        }
        classFile.setForMethodInfos();
        CategorizedProblem[] problems = unitResult.getErrors();
        if (problems == null) {
            problems = new CategorizedProblem[0];
        }
        final int problemsLength;
        final CategorizedProblem[] problemsCopy = new CategorizedProblem[problemsLength = problems.length];
        System.arraycopy(problems, 0, problemsCopy, 0, problemsLength);
        final AbstractMethodDeclaration[] methodDecls = typeDeclaration.methods;
        boolean abstractMethodsOnly = false;
        if (methodDecls != null) {
            if (typeBinding.isInterface()) {
                if (typeBinding.scope.compilerOptions().sourceLevel < 3407872L) {
                    abstractMethodsOnly = true;
                }
                classFile.addProblemClinit(problemsCopy);
            }
            for (int j = 0, length = methodDecls.length; j < length; ++j) {
                final AbstractMethodDeclaration methodDecl = methodDecls[j];
                final MethodBinding method = methodDecl.binding;
                if (method != null) {
                    if (abstractMethodsOnly) {
                        method.modifiers = 1025;
                    }
                    if (method.isConstructor()) {
                        if (!typeBinding.isInterface()) {
                            classFile.addProblemConstructor(methodDecl, method, problemsCopy);
                        }
                    }
                    else if (method.isAbstract()) {
                        classFile.addAbstractMethod(methodDecl, method);
                    }
                    else {
                        classFile.addProblemMethod(methodDecl, method, problemsCopy);
                    }
                }
            }
            classFile.addDefaultAbstractMethods();
        }
        if (typeDeclaration.memberTypes != null) {
            for (int j = 0, max2 = typeDeclaration.memberTypes.length; j < max2; ++j) {
                final TypeDeclaration memberType = typeDeclaration.memberTypes[j];
                if (memberType.binding != null) {
                    createProblemType(memberType, unitResult);
                }
            }
        }
        classFile.addAttributes();
        unitResult.record(typeBinding.constantPoolName(), classFile);
    }
    
    public static ClassFile getNewInstance(final SourceTypeBinding typeBinding) {
        final LookupEnvironment env = typeBinding.scope.environment();
        return env.classFilePool.acquire(typeBinding);
    }
    
    protected ClassFile() {
        this.bootstrapMethods = null;
        this.isShared = false;
        this.missingTypes = null;
    }
    
    public ClassFile(final SourceTypeBinding typeBinding) {
        this.bootstrapMethods = null;
        this.isShared = false;
        this.missingTypes = null;
        this.constantPool = new ConstantPool(this);
        final CompilerOptions options = typeBinding.scope.compilerOptions();
        this.targetJDK = options.targetJDK;
        this.produceAttributes = options.produceDebugAttributes;
        this.referenceBinding = typeBinding;
        this.isNestedType = typeBinding.isNestedType();
        if (this.targetJDK >= 3276800L) {
            this.produceAttributes |= 0x8;
            if (this.targetJDK >= 3407872L) {
                this.produceAttributes |= 0x20;
                this.codeStream = new TypeAnnotationCodeStream(this);
                if (options.produceMethodParameters) {
                    this.produceAttributes |= 0x40;
                }
            }
            else {
                this.codeStream = new StackMapFrameCodeStream(this);
            }
        }
        else if (this.targetJDK == 2949124L) {
            this.targetJDK = 2949123L;
            this.produceAttributes |= 0x10;
            this.codeStream = new StackMapFrameCodeStream(this);
        }
        else {
            this.codeStream = new CodeStream(this);
        }
        this.initByteArrays();
    }
    
    public void addAbstractMethod(final AbstractMethodDeclaration method, final MethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        final int methodAttributeOffset = this.contentsOffset;
        final int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        this.completeMethodInfo(methodBinding, methodAttributeOffset, attributeNumber);
    }
    
    public void addAttributes() {
        this.contents[this.methodCountOffset++] = (byte)(this.methodCount >> 8);
        this.contents[this.methodCountOffset] = (byte)this.methodCount;
        int attributesNumber = 0;
        int attributeOffset = this.contentsOffset;
        this.contentsOffset += 2;
        if ((this.produceAttributes & 0x1) != 0x0) {
            String fullFileName = new String(this.referenceBinding.scope.referenceCompilationUnit().getFileName());
            fullFileName = fullFileName.replace('\\', '/');
            final int lastIndex = fullFileName.lastIndexOf(47);
            if (lastIndex != -1) {
                fullFileName = fullFileName.substring(lastIndex + 1, fullFileName.length());
            }
            attributesNumber += this.generateSourceAttribute(fullFileName);
        }
        if (this.referenceBinding.isDeprecated()) {
            attributesNumber += this.generateDeprecatedAttribute();
        }
        final char[] genericSignature = this.referenceBinding.genericSignature();
        if (genericSignature != null) {
            attributesNumber += this.generateSignatureAttribute(genericSignature);
        }
        if (this.targetJDK >= 3211264L && this.referenceBinding.isNestedType() && !this.referenceBinding.isMemberType()) {
            attributesNumber += this.generateEnclosingMethodAttribute();
        }
        if (this.targetJDK >= 3145728L) {
            final TypeDeclaration typeDeclaration = this.referenceBinding.scope.referenceContext;
            if (typeDeclaration != null) {
                final Annotation[] annotations = typeDeclaration.annotations;
                if (annotations != null) {
                    long targetMask;
                    if (typeDeclaration.isPackageInfo()) {
                        targetMask = 8796093022208L;
                    }
                    else if (this.referenceBinding.isAnnotationType()) {
                        targetMask = 4466765987840L;
                    }
                    else {
                        targetMask = 9007267974217728L;
                    }
                    attributesNumber += this.generateRuntimeAnnotations(annotations, targetMask);
                }
            }
        }
        if (this.referenceBinding.isHierarchyInconsistent()) {
            final ReferenceBinding superclass = this.referenceBinding.superclass;
            if (superclass != null) {
                this.missingTypes = superclass.collectMissingTypes(this.missingTypes);
            }
            final ReferenceBinding[] superInterfaces = this.referenceBinding.superInterfaces();
            for (int i = 0, max = superInterfaces.length; i < max; ++i) {
                this.missingTypes = superInterfaces[i].collectMissingTypes(this.missingTypes);
            }
            attributesNumber += this.generateHierarchyInconsistentAttribute();
        }
        if (this.bootstrapMethods != null && !this.bootstrapMethods.isEmpty()) {
            attributesNumber += this.generateBootstrapMethods(this.bootstrapMethods);
        }
        final int numberOfInnerClasses = (this.innerClassesBindings == null) ? 0 : this.innerClassesBindings.size();
        if (numberOfInnerClasses != 0) {
            final ReferenceBinding[] innerClasses = new ReferenceBinding[numberOfInnerClasses];
            this.innerClassesBindings.keySet().toArray(innerClasses);
            Arrays.sort(innerClasses, new Comparator() {
                @Override
                public int compare(final Object o1, final Object o2) {
                    final TypeBinding binding1 = (TypeBinding)o1;
                    final TypeBinding binding2 = (TypeBinding)o2;
                    final Boolean onBottom1 = ClassFile.this.innerClassesBindings.get(o1);
                    final Boolean onBottom2 = ClassFile.this.innerClassesBindings.get(o2);
                    if (onBottom1) {
                        if (!onBottom2) {
                            return 1;
                        }
                    }
                    else if (onBottom2) {
                        return -1;
                    }
                    return CharOperation.compareTo(binding1.constantPoolName(), binding2.constantPoolName());
                }
            });
            attributesNumber += this.generateInnerClassAttribute(numberOfInnerClasses, innerClasses);
        }
        if (this.missingTypes != null) {
            this.generateMissingTypesAttribute();
            ++attributesNumber;
        }
        attributesNumber += this.generateTypeAnnotationAttributeForTypeDeclaration();
        if (attributeOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contents[attributeOffset++] = (byte)(attributesNumber >> 8);
        this.contents[attributeOffset] = (byte)attributesNumber;
        this.header = this.constantPool.poolContent;
        this.headerOffset = this.constantPool.currentOffset;
        final int constantPoolCount = this.constantPool.currentIndex;
        this.header[this.constantPoolOffset++] = (byte)(constantPoolCount >> 8);
        this.header[this.constantPoolOffset] = (byte)constantPoolCount;
    }
    
    public void addDefaultAbstractMethods() {
        final MethodBinding[] defaultAbstractMethods = this.referenceBinding.getDefaultAbstractMethods();
        for (int i = 0, max = defaultAbstractMethods.length; i < max; ++i) {
            final MethodBinding methodBinding = defaultAbstractMethods[i];
            this.generateMethodInfoHeader(methodBinding);
            final int methodAttributeOffset = this.contentsOffset;
            final int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
            this.completeMethodInfo(methodBinding, methodAttributeOffset, attributeNumber);
        }
    }
    
    private int addFieldAttributes(final FieldBinding fieldBinding, final int fieldAttributeOffset) {
        int attributesNumber = 0;
        final Constant fieldConstant = fieldBinding.constant();
        if (fieldConstant != Constant.NotAConstant) {
            attributesNumber += this.generateConstantValueAttribute(fieldConstant, fieldBinding, fieldAttributeOffset);
        }
        if (this.targetJDK < 3211264L && fieldBinding.isSynthetic()) {
            attributesNumber += this.generateSyntheticAttribute();
        }
        if (fieldBinding.isDeprecated()) {
            attributesNumber += this.generateDeprecatedAttribute();
        }
        final char[] genericSignature = fieldBinding.genericSignature();
        if (genericSignature != null) {
            attributesNumber += this.generateSignatureAttribute(genericSignature);
        }
        if (this.targetJDK >= 3145728L) {
            final FieldDeclaration fieldDeclaration = fieldBinding.sourceField();
            if (fieldDeclaration != null) {
                final Annotation[] annotations = fieldDeclaration.annotations;
                if (annotations != null) {
                    attributesNumber += this.generateRuntimeAnnotations(annotations, 137438953472L);
                }
                if ((this.produceAttributes & 0x20) != 0x0) {
                    final List allTypeAnnotationContexts = new ArrayList();
                    if (annotations != null && (fieldDeclaration.bits & 0x100000) != 0x0) {
                        fieldDeclaration.getAllAnnotationContexts(19, allTypeAnnotationContexts);
                    }
                    int invisibleTypeAnnotationsCounter = 0;
                    int visibleTypeAnnotationsCounter = 0;
                    final TypeReference fieldType = fieldDeclaration.type;
                    if (fieldType != null && (fieldType.bits & 0x100000) != 0x0) {
                        fieldType.getAllAnnotationContexts(19, allTypeAnnotationContexts);
                    }
                    final int size = allTypeAnnotationContexts.size();
                    if (size != 0) {
                        final AnnotationContext[] allTypeAnnotationContextsArray = new AnnotationContext[size];
                        allTypeAnnotationContexts.toArray(allTypeAnnotationContextsArray);
                        for (int i = 0, max = allTypeAnnotationContextsArray.length; i < max; ++i) {
                            final AnnotationContext annotationContext = allTypeAnnotationContextsArray[i];
                            if ((annotationContext.visibility & 0x2) != 0x0) {
                                ++invisibleTypeAnnotationsCounter;
                                allTypeAnnotationContexts.add(annotationContext);
                            }
                            else {
                                ++visibleTypeAnnotationsCounter;
                                allTypeAnnotationContexts.add(annotationContext);
                            }
                        }
                        attributesNumber += this.generateRuntimeTypeAnnotations(allTypeAnnotationContextsArray, visibleTypeAnnotationsCounter, invisibleTypeAnnotationsCounter);
                    }
                }
            }
        }
        if ((fieldBinding.tagBits & 0x80L) != 0x0L) {
            this.missingTypes = fieldBinding.type.collectMissingTypes(this.missingTypes);
        }
        return attributesNumber;
    }
    
    private void addFieldInfo(final FieldBinding fieldBinding) {
        if (this.contentsOffset + 8 >= this.contents.length) {
            this.resizeContents(8);
        }
        int accessFlags = fieldBinding.getAccessFlags();
        if (this.targetJDK < 3211264L) {
            accessFlags &= 0xFFFFEFFF;
        }
        this.contents[this.contentsOffset++] = (byte)(accessFlags >> 8);
        this.contents[this.contentsOffset++] = (byte)accessFlags;
        final int nameIndex = this.constantPool.literalIndex(fieldBinding.name);
        this.contents[this.contentsOffset++] = (byte)(nameIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)nameIndex;
        final int descriptorIndex = this.constantPool.literalIndex(fieldBinding.type);
        this.contents[this.contentsOffset++] = (byte)(descriptorIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)descriptorIndex;
        int fieldAttributeOffset = this.contentsOffset;
        int attributeNumber = 0;
        this.contentsOffset += 2;
        attributeNumber += this.addFieldAttributes(fieldBinding, fieldAttributeOffset);
        if (this.contentsOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contents[fieldAttributeOffset++] = (byte)(attributeNumber >> 8);
        this.contents[fieldAttributeOffset] = (byte)attributeNumber;
    }
    
    public void addFieldInfos() {
        final SourceTypeBinding currentBinding = this.referenceBinding;
        final FieldBinding[] syntheticFields = currentBinding.syntheticFields();
        final int fieldCount = currentBinding.fieldCount() + ((syntheticFields == null) ? 0 : syntheticFields.length);
        if (fieldCount > 65535) {
            this.referenceBinding.scope.problemReporter().tooManyFields(this.referenceBinding.scope.referenceType());
        }
        this.contents[this.contentsOffset++] = (byte)(fieldCount >> 8);
        this.contents[this.contentsOffset++] = (byte)fieldCount;
        final FieldDeclaration[] fieldDecls = currentBinding.scope.referenceContext.fields;
        for (int i = 0, max = (fieldDecls == null) ? 0 : fieldDecls.length; i < max; ++i) {
            final FieldDeclaration fieldDecl = fieldDecls[i];
            if (fieldDecl.binding != null) {
                this.addFieldInfo(fieldDecl.binding);
            }
        }
        if (syntheticFields != null) {
            for (int i = 0, max = syntheticFields.length; i < max; ++i) {
                this.addFieldInfo(syntheticFields[i]);
            }
        }
    }
    
    private void addMissingAbstractProblemMethod(final MethodDeclaration methodDeclaration, final MethodBinding methodBinding, final CategorizedProblem problem, final CompilationResult compilationResult) {
        this.generateMethodInfoHeader(methodBinding, methodBinding.modifiers & 0xFFFFF2FF);
        final int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        ++attributeNumber;
        final int codeAttributeOffset = this.contentsOffset;
        this.generateCodeAttributeHeader();
        final StringBuffer buffer = new StringBuffer(25);
        buffer.append("\t" + problem.getMessage() + "\n");
        buffer.insert(0, Messages.compilation_unresolvedProblem);
        final String problemString = buffer.toString();
        this.codeStream.init(this);
        this.codeStream.preserveUnusedLocals = true;
        this.codeStream.initializeMaxLocals(methodBinding);
        this.codeStream.generateCodeAttributeForProblemMethod(problemString);
        this.completeCodeAttributeForMissingAbstractProblemMethod(methodBinding, codeAttributeOffset, compilationResult.getLineSeparatorPositions(), problem.getSourceLineNumber());
        this.completeMethodInfo(methodBinding, methodAttributeOffset, attributeNumber);
    }
    
    public void addProblemClinit(final CategorizedProblem[] problems) {
        this.generateMethodInfoHeaderForClinit();
        this.contentsOffset -= 2;
        int attributeOffset = this.contentsOffset;
        this.contentsOffset += 2;
        int attributeNumber = 0;
        final int codeAttributeOffset = this.contentsOffset;
        this.generateCodeAttributeHeader();
        this.codeStream.resetForProblemClinit(this);
        String problemString = "";
        int problemLine = 0;
        if (problems != null) {
            final int max = problems.length;
            final StringBuffer buffer = new StringBuffer(25);
            int count = 0;
            for (int i = 0; i < max; ++i) {
                final CategorizedProblem problem = problems[i];
                if (problem != null && problem.isError()) {
                    buffer.append("\t" + problem.getMessage() + "\n");
                    ++count;
                    if (problemLine == 0) {
                        problemLine = problem.getSourceLineNumber();
                    }
                    problems[i] = null;
                }
            }
            if (count > 1) {
                buffer.insert(0, Messages.compilation_unresolvedProblems);
            }
            else {
                buffer.insert(0, Messages.compilation_unresolvedProblem);
            }
            problemString = buffer.toString();
        }
        this.codeStream.generateCodeAttributeForProblemMethod(problemString);
        ++attributeNumber;
        this.completeCodeAttributeForClinit(codeAttributeOffset, problemLine);
        if (this.contentsOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contents[attributeOffset++] = (byte)(attributeNumber >> 8);
        this.contents[attributeOffset] = (byte)attributeNumber;
    }
    
    public void addProblemConstructor(final AbstractMethodDeclaration method, final MethodBinding methodBinding, final CategorizedProblem[] problems) {
        if (methodBinding.declaringClass.isInterface()) {
            method.abort(8, null);
        }
        this.generateMethodInfoHeader(methodBinding, methodBinding.modifiers & 0xFFFFF2FF);
        final int methodAttributeOffset = this.contentsOffset;
        int attributesNumber = this.generateMethodInfoAttributes(methodBinding);
        ++attributesNumber;
        final int codeAttributeOffset = this.contentsOffset;
        this.generateCodeAttributeHeader();
        this.codeStream.reset(method, this);
        String problemString = "";
        int problemLine = 0;
        if (problems != null) {
            final int max = problems.length;
            final StringBuffer buffer = new StringBuffer(25);
            int count = 0;
            for (final CategorizedProblem problem : problems) {
                if (problem != null && problem.isError()) {
                    buffer.append("\t" + problem.getMessage() + "\n");
                    ++count;
                    if (problemLine == 0) {
                        problemLine = problem.getSourceLineNumber();
                    }
                }
            }
            if (count > 1) {
                buffer.insert(0, Messages.compilation_unresolvedProblems);
            }
            else {
                buffer.insert(0, Messages.compilation_unresolvedProblem);
            }
            problemString = buffer.toString();
        }
        this.codeStream.generateCodeAttributeForProblemMethod(problemString);
        this.completeCodeAttributeForProblemMethod(method, methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions(), problemLine);
        this.completeMethodInfo(methodBinding, methodAttributeOffset, attributesNumber);
    }
    
    public void addProblemConstructor(final AbstractMethodDeclaration method, final MethodBinding methodBinding, final CategorizedProblem[] problems, final int savedOffset) {
        this.contentsOffset = savedOffset;
        --this.methodCount;
        this.addProblemConstructor(method, methodBinding, problems);
    }
    
    public void addProblemMethod(final AbstractMethodDeclaration method, final MethodBinding methodBinding, final CategorizedProblem[] problems) {
        if (methodBinding.isAbstract() && methodBinding.declaringClass.isInterface()) {
            method.abort(8, null);
        }
        this.generateMethodInfoHeader(methodBinding, methodBinding.modifiers & 0xFFFFF2FF);
        final int methodAttributeOffset = this.contentsOffset;
        int attributesNumber = this.generateMethodInfoAttributes(methodBinding);
        ++attributesNumber;
        final int codeAttributeOffset = this.contentsOffset;
        this.generateCodeAttributeHeader();
        this.codeStream.reset(method, this);
        String problemString = "";
        int problemLine = 0;
        if (problems != null) {
            final int max = problems.length;
            final StringBuffer buffer = new StringBuffer(25);
            int count = 0;
            for (int i = 0; i < max; ++i) {
                final CategorizedProblem problem = problems[i];
                if (problem != null && problem.isError() && problem.getSourceStart() >= method.declarationSourceStart && problem.getSourceEnd() <= method.declarationSourceEnd) {
                    buffer.append("\t" + problem.getMessage() + "\n");
                    ++count;
                    if (problemLine == 0) {
                        problemLine = problem.getSourceLineNumber();
                    }
                    problems[i] = null;
                }
            }
            if (count > 1) {
                buffer.insert(0, Messages.compilation_unresolvedProblems);
            }
            else {
                buffer.insert(0, Messages.compilation_unresolvedProblem);
            }
            problemString = buffer.toString();
        }
        this.codeStream.generateCodeAttributeForProblemMethod(problemString);
        this.completeCodeAttributeForProblemMethod(method, methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions(), problemLine);
        this.completeMethodInfo(methodBinding, methodAttributeOffset, attributesNumber);
    }
    
    public void addProblemMethod(final AbstractMethodDeclaration method, final MethodBinding methodBinding, final CategorizedProblem[] problems, final int savedOffset) {
        this.contentsOffset = savedOffset;
        --this.methodCount;
        this.addProblemMethod(method, methodBinding, problems);
    }
    
    public void addSpecialMethods() {
        this.generateMissingAbstractMethods(this.referenceBinding.scope.referenceType().missingAbstractMethods, this.referenceBinding.scope.referenceCompilationUnit().compilationResult);
        final MethodBinding[] defaultAbstractMethods = this.referenceBinding.getDefaultAbstractMethods();
        for (int i = 0, max = defaultAbstractMethods.length; i < max; ++i) {
            final MethodBinding methodBinding = defaultAbstractMethods[i];
            this.generateMethodInfoHeader(methodBinding);
            final int methodAttributeOffset = this.contentsOffset;
            final int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
            this.completeMethodInfo(methodBinding, methodAttributeOffset, attributeNumber);
        }
        int emittedSyntheticsCount = 0;
        SyntheticMethodBinding deserializeLambdaMethod = null;
        boolean continueScanningSynthetics = true;
        while (continueScanningSynthetics) {
            continueScanningSynthetics = false;
            final SyntheticMethodBinding[] syntheticMethods = this.referenceBinding.syntheticMethods();
            final int currentSyntheticsCount = (syntheticMethods == null) ? 0 : syntheticMethods.length;
            if (emittedSyntheticsCount != currentSyntheticsCount) {
                for (int j = emittedSyntheticsCount, max2 = currentSyntheticsCount; j < max2; ++j) {
                    final SyntheticMethodBinding syntheticMethod = syntheticMethods[j];
                    switch (syntheticMethod.purpose) {
                        case 1:
                        case 3: {
                            this.addSyntheticFieldReadAccessMethod(syntheticMethod);
                            break;
                        }
                        case 2:
                        case 4: {
                            this.addSyntheticFieldWriteAccessMethod(syntheticMethod);
                            break;
                        }
                        case 5:
                        case 7:
                        case 8: {
                            this.addSyntheticMethodAccessMethod(syntheticMethod);
                            break;
                        }
                        case 6: {
                            this.addSyntheticConstructorAccessMethod(syntheticMethod);
                            break;
                        }
                        case 9: {
                            this.addSyntheticEnumValuesMethod(syntheticMethod);
                            break;
                        }
                        case 10: {
                            this.addSyntheticEnumValueOfMethod(syntheticMethod);
                            break;
                        }
                        case 11: {
                            this.addSyntheticSwitchTable(syntheticMethod);
                            break;
                        }
                        case 12: {
                            this.addSyntheticEnumInitializationMethod(syntheticMethod);
                            break;
                        }
                        case 13: {
                            syntheticMethod.lambda.generateCode(this.referenceBinding.scope, this);
                            continueScanningSynthetics = true;
                            break;
                        }
                        case 14: {
                            this.addSyntheticArrayConstructor(syntheticMethod);
                            break;
                        }
                        case 15: {
                            this.addSyntheticArrayClone(syntheticMethod);
                            break;
                        }
                        case 16: {
                            this.addSyntheticFactoryMethod(syntheticMethod);
                            break;
                        }
                        case 17: {
                            deserializeLambdaMethod = syntheticMethod;
                            break;
                        }
                    }
                }
                emittedSyntheticsCount = currentSyntheticsCount;
            }
        }
        if (deserializeLambdaMethod != null) {
            int problemResetPC = 0;
            this.codeStream.wideMode = false;
            boolean restart = false;
            do {
                try {
                    problemResetPC = this.contentsOffset;
                    this.addSyntheticDeserializeLambda(deserializeLambdaMethod, this.referenceBinding.syntheticMethods());
                    restart = false;
                }
                catch (final AbortMethod e) {
                    if (e.compilationResult != CodeStream.RESTART_IN_WIDE_MODE) {
                        throw new AbortType(this.referenceBinding.scope.referenceContext.compilationResult, e.problem);
                    }
                    this.contentsOffset = problemResetPC;
                    --this.methodCount;
                    this.codeStream.resetInWideMode();
                    restart = true;
                }
            } while (restart);
        }
    }
    
    public void addSyntheticArrayConstructor(final SyntheticMethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        final int codeAttributeOffset = this.contentsOffset;
        ++attributeNumber;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForArrayConstructor(methodBinding);
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        this.contents[methodAttributeOffset++] = (byte)(attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }
    
    public void addSyntheticArrayClone(final SyntheticMethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        final int codeAttributeOffset = this.contentsOffset;
        ++attributeNumber;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForArrayClone(methodBinding);
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        this.contents[methodAttributeOffset++] = (byte)(attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }
    
    public void addSyntheticFactoryMethod(final SyntheticMethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        final int codeAttributeOffset = this.contentsOffset;
        ++attributeNumber;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForFactoryMethod(methodBinding);
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        this.contents[methodAttributeOffset++] = (byte)(attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }
    
    public void addSyntheticConstructorAccessMethod(final SyntheticMethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        final int codeAttributeOffset = this.contentsOffset;
        ++attributeNumber;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForConstructorAccess(methodBinding);
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        this.contents[methodAttributeOffset++] = (byte)(attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }
    
    public void addSyntheticEnumValueOfMethod(final SyntheticMethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        final int codeAttributeOffset = this.contentsOffset;
        ++attributeNumber;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForEnumValueOf(methodBinding);
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        if ((this.produceAttributes & 0x40) != 0x0) {
            attributeNumber += this.generateMethodParameters(methodBinding);
        }
        this.contents[methodAttributeOffset++] = (byte)(attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }
    
    public void addSyntheticEnumValuesMethod(final SyntheticMethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        final int codeAttributeOffset = this.contentsOffset;
        ++attributeNumber;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForEnumValues(methodBinding);
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        this.contents[methodAttributeOffset++] = (byte)(attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }
    
    public void addSyntheticEnumInitializationMethod(final SyntheticMethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        final int codeAttributeOffset = this.contentsOffset;
        ++attributeNumber;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForEnumInitializationMethod(methodBinding);
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        this.contents[methodAttributeOffset++] = (byte)(attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }
    
    public void addSyntheticFieldReadAccessMethod(final SyntheticMethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        final int codeAttributeOffset = this.contentsOffset;
        ++attributeNumber;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForFieldReadAccess(methodBinding);
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        this.contents[methodAttributeOffset++] = (byte)(attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }
    
    public void addSyntheticFieldWriteAccessMethod(final SyntheticMethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        final int codeAttributeOffset = this.contentsOffset;
        ++attributeNumber;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForFieldWriteAccess(methodBinding);
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        this.contents[methodAttributeOffset++] = (byte)(attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }
    
    public void addSyntheticMethodAccessMethod(final SyntheticMethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        final int codeAttributeOffset = this.contentsOffset;
        ++attributeNumber;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForMethodAccess(methodBinding);
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        this.contents[methodAttributeOffset++] = (byte)(attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }
    
    public void addSyntheticSwitchTable(final SyntheticMethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        final int codeAttributeOffset = this.contentsOffset;
        ++attributeNumber;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForSwitchTable(methodBinding);
        this.completeCodeAttributeForSyntheticMethod(true, methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        this.contents[methodAttributeOffset++] = (byte)(attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }
    
    public void completeCodeAttribute(final int codeAttributeOffset) {
        this.contents = this.codeStream.bCodeStream;
        int localContentsOffset = this.codeStream.classFileOffset;
        final int code_length = this.codeStream.position;
        if (code_length > 65535) {
            if (this.codeStream.methodDeclaration != null) {
                this.codeStream.methodDeclaration.scope.problemReporter().bytecodeExceeds64KLimit(this.codeStream.methodDeclaration);
            }
            else {
                this.codeStream.lambdaExpression.scope.problemReporter().bytecodeExceeds64KLimit(this.codeStream.lambdaExpression);
            }
        }
        if (localContentsOffset + 20 >= this.contents.length) {
            this.resizeContents(20);
        }
        final int max_stack = this.codeStream.stackMax;
        this.contents[codeAttributeOffset + 6] = (byte)(max_stack >> 8);
        this.contents[codeAttributeOffset + 7] = (byte)max_stack;
        final int max_locals = this.codeStream.maxLocals;
        this.contents[codeAttributeOffset + 8] = (byte)(max_locals >> 8);
        this.contents[codeAttributeOffset + 9] = (byte)max_locals;
        this.contents[codeAttributeOffset + 10] = (byte)(code_length >> 24);
        this.contents[codeAttributeOffset + 11] = (byte)(code_length >> 16);
        this.contents[codeAttributeOffset + 12] = (byte)(code_length >> 8);
        this.contents[codeAttributeOffset + 13] = (byte)code_length;
        final boolean addStackMaps = (this.produceAttributes & 0x8) != 0x0;
        final ExceptionLabel[] exceptionLabels = this.codeStream.exceptionLabels;
        int exceptionHandlersCount = 0;
        for (int i = 0, length = this.codeStream.exceptionLabelsCounter; i < length; ++i) {
            exceptionHandlersCount += this.codeStream.exceptionLabels[i].getCount() / 2;
        }
        final int exSize = exceptionHandlersCount * 8 + 2;
        if (exSize + localContentsOffset >= this.contents.length) {
            this.resizeContents(exSize);
        }
        this.contents[localContentsOffset++] = (byte)(exceptionHandlersCount >> 8);
        this.contents[localContentsOffset++] = (byte)exceptionHandlersCount;
        for (int j = 0, max = this.codeStream.exceptionLabelsCounter; j < max; ++j) {
            final ExceptionLabel exceptionLabel = exceptionLabels[j];
            if (exceptionLabel != null) {
                int iRange = 0;
                final int maxRange = exceptionLabel.getCount();
                if ((maxRange & 0x1) != 0x0) {
                    if (this.codeStream.methodDeclaration != null) {
                        this.codeStream.methodDeclaration.scope.problemReporter().abortDueToInternalError(Messages.bind(Messages.abort_invalidExceptionAttribute, new String(this.codeStream.methodDeclaration.selector)), this.codeStream.methodDeclaration);
                    }
                    else {
                        this.codeStream.lambdaExpression.scope.problemReporter().abortDueToInternalError(Messages.bind(Messages.abort_invalidExceptionAttribute, new String(this.codeStream.lambdaExpression.binding.selector)), this.codeStream.lambdaExpression);
                    }
                }
                while (iRange < maxRange) {
                    final int start = exceptionLabel.ranges[iRange++];
                    this.contents[localContentsOffset++] = (byte)(start >> 8);
                    this.contents[localContentsOffset++] = (byte)start;
                    final int end = exceptionLabel.ranges[iRange++];
                    this.contents[localContentsOffset++] = (byte)(end >> 8);
                    this.contents[localContentsOffset++] = (byte)end;
                    final int handlerPC = exceptionLabel.position;
                    if (addStackMaps) {
                        final StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
                        stackMapFrameCodeStream.addFramePosition(handlerPC);
                    }
                    this.contents[localContentsOffset++] = (byte)(handlerPC >> 8);
                    this.contents[localContentsOffset++] = (byte)handlerPC;
                    if (exceptionLabel.exceptionType == null) {
                        this.contents[localContentsOffset++] = 0;
                        this.contents[localContentsOffset++] = 0;
                    }
                    else {
                        int nameIndex;
                        if (exceptionLabel.exceptionType == TypeBinding.NULL) {
                            nameIndex = this.constantPool.literalIndexForType(ConstantPool.JavaLangClassNotFoundExceptionConstantPoolName);
                        }
                        else {
                            nameIndex = this.constantPool.literalIndexForType(exceptionLabel.exceptionType);
                        }
                        this.contents[localContentsOffset++] = (byte)(nameIndex >> 8);
                        this.contents[localContentsOffset++] = (byte)nameIndex;
                    }
                }
            }
        }
        int codeAttributeAttributeOffset = localContentsOffset;
        int attributesNumber = 0;
        localContentsOffset += 2;
        if (localContentsOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contentsOffset = localContentsOffset;
        if ((this.produceAttributes & 0x2) != 0x0) {
            attributesNumber += this.generateLineNumberAttribute();
        }
        if ((this.produceAttributes & 0x4) != 0x0) {
            final boolean methodDeclarationIsStatic = (this.codeStream.methodDeclaration != null) ? this.codeStream.methodDeclaration.isStatic() : this.codeStream.lambdaExpression.binding.isStatic();
            attributesNumber += this.generateLocalVariableTableAttribute(code_length, methodDeclarationIsStatic, false);
        }
        if (addStackMaps) {
            attributesNumber += this.generateStackMapTableAttribute((this.codeStream.methodDeclaration != null) ? this.codeStream.methodDeclaration.binding : this.codeStream.lambdaExpression.binding, code_length, codeAttributeOffset, max_locals, false);
        }
        if ((this.produceAttributes & 0x10) != 0x0) {
            attributesNumber += this.generateStackMapAttribute((this.codeStream.methodDeclaration != null) ? this.codeStream.methodDeclaration.binding : this.codeStream.lambdaExpression.binding, code_length, codeAttributeOffset, max_locals, false);
        }
        if ((this.produceAttributes & 0x20) != 0x0) {
            attributesNumber += this.generateTypeAnnotationsOnCodeAttribute();
        }
        this.contents[codeAttributeAttributeOffset++] = (byte)(attributesNumber >> 8);
        this.contents[codeAttributeAttributeOffset] = (byte)attributesNumber;
        final int codeAttributeLength = this.contentsOffset - (codeAttributeOffset + 6);
        this.contents[codeAttributeOffset + 2] = (byte)(codeAttributeLength >> 24);
        this.contents[codeAttributeOffset + 3] = (byte)(codeAttributeLength >> 16);
        this.contents[codeAttributeOffset + 4] = (byte)(codeAttributeLength >> 8);
        this.contents[codeAttributeOffset + 5] = (byte)codeAttributeLength;
    }
    
    public int generateTypeAnnotationsOnCodeAttribute() {
        int attributesNumber = 0;
        final List allTypeAnnotationContexts = ((TypeAnnotationCodeStream)this.codeStream).allTypeAnnotationContexts;
        int invisibleTypeAnnotationsCounter = 0;
        int visibleTypeAnnotationsCounter = 0;
        for (int i = 0, max = this.codeStream.allLocalsCounter; i < max; ++i) {
            final LocalVariableBinding localVariable = this.codeStream.locals[i];
            if (!localVariable.isCatchParameter()) {
                final LocalDeclaration declaration = localVariable.declaration;
                if (declaration != null && (!declaration.isArgument() || (declaration.bits & 0x20000000) != 0x0) && localVariable.initializationCount != 0) {
                    if ((declaration.bits & 0x100000) != 0x0) {
                        final int targetType = ((localVariable.tagBits & 0x2000L) == 0x0L) ? 64 : 65;
                        declaration.getAllAnnotationContexts(targetType, localVariable, allTypeAnnotationContexts);
                    }
                }
            }
        }
        final ExceptionLabel[] exceptionLabels = this.codeStream.exceptionLabels;
        for (int j = 0, max2 = this.codeStream.exceptionLabelsCounter; j < max2; ++j) {
            final ExceptionLabel exceptionLabel = exceptionLabels[j];
            if (exceptionLabel.exceptionTypeReference != null && (exceptionLabel.exceptionTypeReference.bits & 0x100000) != 0x0) {
                exceptionLabel.exceptionTypeReference.getAllAnnotationContexts(66, j, allTypeAnnotationContexts, exceptionLabel.se7Annotations);
            }
        }
        final int size = allTypeAnnotationContexts.size();
        if (size != 0) {
            final AnnotationContext[] allTypeAnnotationContextsArray = new AnnotationContext[size];
            allTypeAnnotationContexts.toArray(allTypeAnnotationContextsArray);
            for (int k = 0, max3 = allTypeAnnotationContextsArray.length; k < max3; ++k) {
                final AnnotationContext annotationContext = allTypeAnnotationContextsArray[k];
                if ((annotationContext.visibility & 0x2) != 0x0) {
                    ++invisibleTypeAnnotationsCounter;
                }
                else {
                    ++visibleTypeAnnotationsCounter;
                }
            }
            attributesNumber += this.generateRuntimeTypeAnnotations(allTypeAnnotationContextsArray, visibleTypeAnnotationsCounter, invisibleTypeAnnotationsCounter);
        }
        return attributesNumber;
    }
    
    public void completeCodeAttributeForClinit(final int codeAttributeOffset) {
        this.contents = this.codeStream.bCodeStream;
        int localContentsOffset = this.codeStream.classFileOffset;
        final int code_length = this.codeStream.position;
        if (code_length > 65535) {
            this.codeStream.methodDeclaration.scope.problemReporter().bytecodeExceeds64KLimit(this.codeStream.methodDeclaration.scope.referenceType());
        }
        if (localContentsOffset + 20 >= this.contents.length) {
            this.resizeContents(20);
        }
        final int max_stack = this.codeStream.stackMax;
        this.contents[codeAttributeOffset + 6] = (byte)(max_stack >> 8);
        this.contents[codeAttributeOffset + 7] = (byte)max_stack;
        final int max_locals = this.codeStream.maxLocals;
        this.contents[codeAttributeOffset + 8] = (byte)(max_locals >> 8);
        this.contents[codeAttributeOffset + 9] = (byte)max_locals;
        this.contents[codeAttributeOffset + 10] = (byte)(code_length >> 24);
        this.contents[codeAttributeOffset + 11] = (byte)(code_length >> 16);
        this.contents[codeAttributeOffset + 12] = (byte)(code_length >> 8);
        this.contents[codeAttributeOffset + 13] = (byte)code_length;
        final boolean addStackMaps = (this.produceAttributes & 0x8) != 0x0;
        final ExceptionLabel[] exceptionLabels = this.codeStream.exceptionLabels;
        int exceptionHandlersCount = 0;
        for (int i = 0, length = this.codeStream.exceptionLabelsCounter; i < length; ++i) {
            exceptionHandlersCount += this.codeStream.exceptionLabels[i].getCount() / 2;
        }
        final int exSize = exceptionHandlersCount * 8 + 2;
        if (exSize + localContentsOffset >= this.contents.length) {
            this.resizeContents(exSize);
        }
        this.contents[localContentsOffset++] = (byte)(exceptionHandlersCount >> 8);
        this.contents[localContentsOffset++] = (byte)exceptionHandlersCount;
        for (int j = 0, max = this.codeStream.exceptionLabelsCounter; j < max; ++j) {
            final ExceptionLabel exceptionLabel = exceptionLabels[j];
            if (exceptionLabel != null) {
                int iRange = 0;
                final int maxRange = exceptionLabel.getCount();
                if ((maxRange & 0x1) != 0x0) {
                    this.codeStream.methodDeclaration.scope.problemReporter().abortDueToInternalError(Messages.bind(Messages.abort_invalidExceptionAttribute, new String(this.codeStream.methodDeclaration.selector)), this.codeStream.methodDeclaration);
                }
                while (iRange < maxRange) {
                    final int start = exceptionLabel.ranges[iRange++];
                    this.contents[localContentsOffset++] = (byte)(start >> 8);
                    this.contents[localContentsOffset++] = (byte)start;
                    final int end = exceptionLabel.ranges[iRange++];
                    this.contents[localContentsOffset++] = (byte)(end >> 8);
                    this.contents[localContentsOffset++] = (byte)end;
                    final int handlerPC = exceptionLabel.position;
                    this.contents[localContentsOffset++] = (byte)(handlerPC >> 8);
                    this.contents[localContentsOffset++] = (byte)handlerPC;
                    if (addStackMaps) {
                        final StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
                        stackMapFrameCodeStream.addFramePosition(handlerPC);
                    }
                    if (exceptionLabel.exceptionType == null) {
                        this.contents[localContentsOffset++] = 0;
                        this.contents[localContentsOffset++] = 0;
                    }
                    else {
                        int nameIndex;
                        if (exceptionLabel.exceptionType == TypeBinding.NULL) {
                            nameIndex = this.constantPool.literalIndexForType(ConstantPool.JavaLangClassNotFoundExceptionConstantPoolName);
                        }
                        else {
                            nameIndex = this.constantPool.literalIndexForType(exceptionLabel.exceptionType);
                        }
                        this.contents[localContentsOffset++] = (byte)(nameIndex >> 8);
                        this.contents[localContentsOffset++] = (byte)nameIndex;
                    }
                }
            }
        }
        int codeAttributeAttributeOffset = localContentsOffset;
        int attributesNumber = 0;
        localContentsOffset += 2;
        if (localContentsOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contentsOffset = localContentsOffset;
        if ((this.produceAttributes & 0x2) != 0x0) {
            attributesNumber += this.generateLineNumberAttribute();
        }
        if ((this.produceAttributes & 0x4) != 0x0) {
            attributesNumber += this.generateLocalVariableTableAttribute(code_length, true, false);
        }
        if ((this.produceAttributes & 0x8) != 0x0) {
            attributesNumber += this.generateStackMapTableAttribute(null, code_length, codeAttributeOffset, max_locals, true);
        }
        if ((this.produceAttributes & 0x10) != 0x0) {
            attributesNumber += this.generateStackMapAttribute(null, code_length, codeAttributeOffset, max_locals, true);
        }
        if ((this.produceAttributes & 0x20) != 0x0) {
            attributesNumber += this.generateTypeAnnotationsOnCodeAttribute();
        }
        if (codeAttributeAttributeOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contents[codeAttributeAttributeOffset++] = (byte)(attributesNumber >> 8);
        this.contents[codeAttributeAttributeOffset] = (byte)attributesNumber;
        final int codeAttributeLength = this.contentsOffset - (codeAttributeOffset + 6);
        this.contents[codeAttributeOffset + 2] = (byte)(codeAttributeLength >> 24);
        this.contents[codeAttributeOffset + 3] = (byte)(codeAttributeLength >> 16);
        this.contents[codeAttributeOffset + 4] = (byte)(codeAttributeLength >> 8);
        this.contents[codeAttributeOffset + 5] = (byte)codeAttributeLength;
    }
    
    public void completeCodeAttributeForClinit(final int codeAttributeOffset, final int problemLine) {
        this.contents = this.codeStream.bCodeStream;
        int localContentsOffset = this.codeStream.classFileOffset;
        final int code_length = this.codeStream.position;
        if (code_length > 65535) {
            this.codeStream.methodDeclaration.scope.problemReporter().bytecodeExceeds64KLimit(this.codeStream.methodDeclaration.scope.referenceType());
        }
        if (localContentsOffset + 20 >= this.contents.length) {
            this.resizeContents(20);
        }
        final int max_stack = this.codeStream.stackMax;
        this.contents[codeAttributeOffset + 6] = (byte)(max_stack >> 8);
        this.contents[codeAttributeOffset + 7] = (byte)max_stack;
        final int max_locals = this.codeStream.maxLocals;
        this.contents[codeAttributeOffset + 8] = (byte)(max_locals >> 8);
        this.contents[codeAttributeOffset + 9] = (byte)max_locals;
        this.contents[codeAttributeOffset + 10] = (byte)(code_length >> 24);
        this.contents[codeAttributeOffset + 11] = (byte)(code_length >> 16);
        this.contents[codeAttributeOffset + 12] = (byte)(code_length >> 8);
        this.contents[codeAttributeOffset + 13] = (byte)code_length;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        int codeAttributeAttributeOffset = localContentsOffset;
        int attributesNumber = 0;
        localContentsOffset += 2;
        if (localContentsOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contentsOffset = localContentsOffset;
        if ((this.produceAttributes & 0x2) != 0x0) {
            attributesNumber += this.generateLineNumberAttribute(problemLine);
        }
        localContentsOffset = this.contentsOffset;
        if ((this.produceAttributes & 0x4) != 0x0) {
            final int localVariableNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.LocalVariableTableName);
            if (localContentsOffset + 8 >= this.contents.length) {
                this.resizeContents(8);
            }
            this.contents[localContentsOffset++] = (byte)(localVariableNameIndex >> 8);
            this.contents[localContentsOffset++] = (byte)localVariableNameIndex;
            this.contents[localContentsOffset++] = 0;
            this.contents[localContentsOffset++] = 0;
            this.contents[localContentsOffset++] = 0;
            this.contents[localContentsOffset++] = 2;
            this.contents[localContentsOffset++] = 0;
            this.contents[localContentsOffset++] = 0;
            ++attributesNumber;
        }
        this.contentsOffset = localContentsOffset;
        if ((this.produceAttributes & 0x8) != 0x0) {
            attributesNumber += this.generateStackMapTableAttribute(null, code_length, codeAttributeOffset, max_locals, true);
        }
        if ((this.produceAttributes & 0x10) != 0x0) {
            attributesNumber += this.generateStackMapAttribute(null, code_length, codeAttributeOffset, max_locals, true);
        }
        if ((this.produceAttributes & 0x20) != 0x0) {
            attributesNumber += this.generateTypeAnnotationsOnCodeAttribute();
        }
        if (codeAttributeAttributeOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contents[codeAttributeAttributeOffset++] = (byte)(attributesNumber >> 8);
        this.contents[codeAttributeAttributeOffset] = (byte)attributesNumber;
        final int codeAttributeLength = this.contentsOffset - (codeAttributeOffset + 6);
        this.contents[codeAttributeOffset + 2] = (byte)(codeAttributeLength >> 24);
        this.contents[codeAttributeOffset + 3] = (byte)(codeAttributeLength >> 16);
        this.contents[codeAttributeOffset + 4] = (byte)(codeAttributeLength >> 8);
        this.contents[codeAttributeOffset + 5] = (byte)codeAttributeLength;
    }
    
    public void completeCodeAttributeForMissingAbstractProblemMethod(final MethodBinding binding, final int codeAttributeOffset, final int[] startLineIndexes, int problemLine) {
        this.contents = this.codeStream.bCodeStream;
        int localContentsOffset = this.codeStream.classFileOffset;
        final int max_stack = this.codeStream.stackMax;
        this.contents[codeAttributeOffset + 6] = (byte)(max_stack >> 8);
        this.contents[codeAttributeOffset + 7] = (byte)max_stack;
        final int max_locals = this.codeStream.maxLocals;
        this.contents[codeAttributeOffset + 8] = (byte)(max_locals >> 8);
        this.contents[codeAttributeOffset + 9] = (byte)max_locals;
        final int code_length = this.codeStream.position;
        this.contents[codeAttributeOffset + 10] = (byte)(code_length >> 24);
        this.contents[codeAttributeOffset + 11] = (byte)(code_length >> 16);
        this.contents[codeAttributeOffset + 12] = (byte)(code_length >> 8);
        this.contents[codeAttributeOffset + 13] = (byte)code_length;
        if (localContentsOffset + 50 >= this.contents.length) {
            this.resizeContents(50);
        }
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        int codeAttributeAttributeOffset = localContentsOffset;
        int attributesNumber = 0;
        localContentsOffset += 2;
        if (localContentsOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contentsOffset = localContentsOffset;
        if ((this.produceAttributes & 0x2) != 0x0) {
            if (problemLine == 0) {
                problemLine = Util.getLineNumber(binding.sourceStart(), startLineIndexes, 0, startLineIndexes.length - 1);
            }
            attributesNumber += this.generateLineNumberAttribute(problemLine);
        }
        if ((this.produceAttributes & 0x8) != 0x0) {
            attributesNumber += this.generateStackMapTableAttribute(binding, code_length, codeAttributeOffset, max_locals, false);
        }
        if ((this.produceAttributes & 0x10) != 0x0) {
            attributesNumber += this.generateStackMapAttribute(binding, code_length, codeAttributeOffset, max_locals, false);
        }
        if (codeAttributeAttributeOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contents[codeAttributeAttributeOffset++] = (byte)(attributesNumber >> 8);
        this.contents[codeAttributeAttributeOffset] = (byte)attributesNumber;
        final int codeAttributeLength = this.contentsOffset - (codeAttributeOffset + 6);
        this.contents[codeAttributeOffset + 2] = (byte)(codeAttributeLength >> 24);
        this.contents[codeAttributeOffset + 3] = (byte)(codeAttributeLength >> 16);
        this.contents[codeAttributeOffset + 4] = (byte)(codeAttributeLength >> 8);
        this.contents[codeAttributeOffset + 5] = (byte)codeAttributeLength;
    }
    
    public void completeCodeAttributeForProblemMethod(final AbstractMethodDeclaration method, final MethodBinding binding, final int codeAttributeOffset, final int[] startLineIndexes, int problemLine) {
        this.contents = this.codeStream.bCodeStream;
        int localContentsOffset = this.codeStream.classFileOffset;
        final int max_stack = this.codeStream.stackMax;
        this.contents[codeAttributeOffset + 6] = (byte)(max_stack >> 8);
        this.contents[codeAttributeOffset + 7] = (byte)max_stack;
        final int max_locals = this.codeStream.maxLocals;
        this.contents[codeAttributeOffset + 8] = (byte)(max_locals >> 8);
        this.contents[codeAttributeOffset + 9] = (byte)max_locals;
        final int code_length = this.codeStream.position;
        this.contents[codeAttributeOffset + 10] = (byte)(code_length >> 24);
        this.contents[codeAttributeOffset + 11] = (byte)(code_length >> 16);
        this.contents[codeAttributeOffset + 12] = (byte)(code_length >> 8);
        this.contents[codeAttributeOffset + 13] = (byte)code_length;
        if (localContentsOffset + 50 >= this.contents.length) {
            this.resizeContents(50);
        }
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        int codeAttributeAttributeOffset = localContentsOffset;
        int attributesNumber = 0;
        localContentsOffset += 2;
        if (localContentsOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contentsOffset = localContentsOffset;
        if ((this.produceAttributes & 0x2) != 0x0) {
            if (problemLine == 0) {
                problemLine = Util.getLineNumber(binding.sourceStart(), startLineIndexes, 0, startLineIndexes.length - 1);
            }
            attributesNumber += this.generateLineNumberAttribute(problemLine);
        }
        if ((this.produceAttributes & 0x4) != 0x0) {
            final boolean methodDeclarationIsStatic = this.codeStream.methodDeclaration.isStatic();
            attributesNumber += this.generateLocalVariableTableAttribute(code_length, methodDeclarationIsStatic, false);
        }
        if ((this.produceAttributes & 0x8) != 0x0) {
            attributesNumber += this.generateStackMapTableAttribute(binding, code_length, codeAttributeOffset, max_locals, false);
        }
        if ((this.produceAttributes & 0x10) != 0x0) {
            attributesNumber += this.generateStackMapAttribute(binding, code_length, codeAttributeOffset, max_locals, false);
        }
        if (codeAttributeAttributeOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contents[codeAttributeAttributeOffset++] = (byte)(attributesNumber >> 8);
        this.contents[codeAttributeAttributeOffset] = (byte)attributesNumber;
        final int codeAttributeLength = this.contentsOffset - (codeAttributeOffset + 6);
        this.contents[codeAttributeOffset + 2] = (byte)(codeAttributeLength >> 24);
        this.contents[codeAttributeOffset + 3] = (byte)(codeAttributeLength >> 16);
        this.contents[codeAttributeOffset + 4] = (byte)(codeAttributeLength >> 8);
        this.contents[codeAttributeOffset + 5] = (byte)codeAttributeLength;
    }
    
    public void completeCodeAttributeForSyntheticMethod(final boolean hasExceptionHandlers, final SyntheticMethodBinding binding, final int codeAttributeOffset, final int[] startLineIndexes) {
        this.contents = this.codeStream.bCodeStream;
        int localContentsOffset = this.codeStream.classFileOffset;
        final int max_stack = this.codeStream.stackMax;
        this.contents[codeAttributeOffset + 6] = (byte)(max_stack >> 8);
        this.contents[codeAttributeOffset + 7] = (byte)max_stack;
        final int max_locals = this.codeStream.maxLocals;
        this.contents[codeAttributeOffset + 8] = (byte)(max_locals >> 8);
        this.contents[codeAttributeOffset + 9] = (byte)max_locals;
        final int code_length = this.codeStream.position;
        this.contents[codeAttributeOffset + 10] = (byte)(code_length >> 24);
        this.contents[codeAttributeOffset + 11] = (byte)(code_length >> 16);
        this.contents[codeAttributeOffset + 12] = (byte)(code_length >> 8);
        this.contents[codeAttributeOffset + 13] = (byte)code_length;
        if (localContentsOffset + 40 >= this.contents.length) {
            this.resizeContents(40);
        }
        final boolean addStackMaps = (this.produceAttributes & 0x8) != 0x0;
        if (hasExceptionHandlers) {
            final ExceptionLabel[] exceptionLabels = this.codeStream.exceptionLabels;
            int exceptionHandlersCount = 0;
            for (int i = 0, length = this.codeStream.exceptionLabelsCounter; i < length; ++i) {
                exceptionHandlersCount += this.codeStream.exceptionLabels[i].getCount() / 2;
            }
            final int exSize = exceptionHandlersCount * 8 + 2;
            if (exSize + localContentsOffset >= this.contents.length) {
                this.resizeContents(exSize);
            }
            this.contents[localContentsOffset++] = (byte)(exceptionHandlersCount >> 8);
            this.contents[localContentsOffset++] = (byte)exceptionHandlersCount;
            for (int j = 0, max = this.codeStream.exceptionLabelsCounter; j < max; ++j) {
                final ExceptionLabel exceptionLabel = exceptionLabels[j];
                if (exceptionLabel != null) {
                    int iRange = 0;
                    final int maxRange = exceptionLabel.getCount();
                    if ((maxRange & 0x1) != 0x0) {
                        this.referenceBinding.scope.problemReporter().abortDueToInternalError(Messages.bind(Messages.abort_invalidExceptionAttribute, new String(binding.selector), this.referenceBinding.scope.problemReporter().referenceContext));
                    }
                    while (iRange < maxRange) {
                        final int start = exceptionLabel.ranges[iRange++];
                        this.contents[localContentsOffset++] = (byte)(start >> 8);
                        this.contents[localContentsOffset++] = (byte)start;
                        final int end = exceptionLabel.ranges[iRange++];
                        this.contents[localContentsOffset++] = (byte)(end >> 8);
                        this.contents[localContentsOffset++] = (byte)end;
                        final int handlerPC = exceptionLabel.position;
                        if (addStackMaps) {
                            final StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
                            stackMapFrameCodeStream.addFramePosition(handlerPC);
                        }
                        this.contents[localContentsOffset++] = (byte)(handlerPC >> 8);
                        this.contents[localContentsOffset++] = (byte)handlerPC;
                        if (exceptionLabel.exceptionType == null) {
                            this.contents[localContentsOffset++] = 0;
                            this.contents[localContentsOffset++] = 0;
                        }
                        else {
                            int nameIndex = 0;
                            switch (exceptionLabel.exceptionType.id) {
                                case 12: {
                                    nameIndex = this.constantPool.literalIndexForType(ConstantPool.JavaLangClassNotFoundExceptionConstantPoolName);
                                    break;
                                }
                                case 7: {
                                    nameIndex = this.constantPool.literalIndexForType(ConstantPool.JavaLangNoSuchFieldErrorConstantPoolName);
                                    break;
                                }
                                default: {
                                    nameIndex = this.constantPool.literalIndexForType(exceptionLabel.exceptionType);
                                    break;
                                }
                            }
                            this.contents[localContentsOffset++] = (byte)(nameIndex >> 8);
                            this.contents[localContentsOffset++] = (byte)nameIndex;
                        }
                    }
                }
            }
        }
        else {
            this.contents[localContentsOffset++] = 0;
            this.contents[localContentsOffset++] = 0;
        }
        int codeAttributeAttributeOffset = localContentsOffset;
        int attributesNumber = 0;
        localContentsOffset += 2;
        if (localContentsOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contentsOffset = localContentsOffset;
        if ((this.produceAttributes & 0x2) != 0x0) {
            final int lineNumber = Util.getLineNumber(binding.sourceStart, startLineIndexes, 0, startLineIndexes.length - 1);
            attributesNumber += this.generateLineNumberAttribute(lineNumber);
        }
        if ((this.produceAttributes & 0x4) != 0x0) {
            final boolean methodDeclarationIsStatic = binding.isStatic();
            attributesNumber += this.generateLocalVariableTableAttribute(code_length, methodDeclarationIsStatic, true);
        }
        if (addStackMaps) {
            attributesNumber += this.generateStackMapTableAttribute(binding, code_length, codeAttributeOffset, max_locals, false);
        }
        if ((this.produceAttributes & 0x10) != 0x0) {
            attributesNumber += this.generateStackMapAttribute(binding, code_length, codeAttributeOffset, max_locals, false);
        }
        if (codeAttributeAttributeOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        this.contents[codeAttributeAttributeOffset++] = (byte)(attributesNumber >> 8);
        this.contents[codeAttributeAttributeOffset] = (byte)attributesNumber;
        final int codeAttributeLength = this.contentsOffset - (codeAttributeOffset + 6);
        this.contents[codeAttributeOffset + 2] = (byte)(codeAttributeLength >> 24);
        this.contents[codeAttributeOffset + 3] = (byte)(codeAttributeLength >> 16);
        this.contents[codeAttributeOffset + 4] = (byte)(codeAttributeLength >> 8);
        this.contents[codeAttributeOffset + 5] = (byte)codeAttributeLength;
    }
    
    public void completeCodeAttributeForSyntheticMethod(final SyntheticMethodBinding binding, final int codeAttributeOffset, final int[] startLineIndexes) {
        this.completeCodeAttributeForSyntheticMethod(false, binding, codeAttributeOffset, startLineIndexes);
    }
    
    private void completeArgumentAnnotationInfo(final Argument[] arguments, final List allAnnotationContexts) {
        for (int i = 0, max = arguments.length; i < max; ++i) {
            final Argument argument = arguments[i];
            if ((argument.bits & 0x100000) != 0x0) {
                argument.getAllAnnotationContexts(22, i, allAnnotationContexts);
            }
        }
    }
    
    public void completeMethodInfo(final MethodBinding binding, int methodAttributeOffset, int attributesNumber) {
        if ((this.produceAttributes & 0x20) != 0x0) {
            final List allTypeAnnotationContexts = new ArrayList();
            int invisibleTypeAnnotationsCounter = 0;
            int visibleTypeAnnotationsCounter = 0;
            final AbstractMethodDeclaration methodDeclaration = binding.sourceMethod();
            if (methodDeclaration != null) {
                if ((methodDeclaration.bits & 0x100000) != 0x0) {
                    final Argument[] arguments = methodDeclaration.arguments;
                    if (arguments != null) {
                        this.completeArgumentAnnotationInfo(arguments, allTypeAnnotationContexts);
                    }
                    final Receiver receiver = methodDeclaration.receiver;
                    if (receiver != null && (receiver.type.bits & 0x100000) != 0x0) {
                        receiver.type.getAllAnnotationContexts(21, allTypeAnnotationContexts);
                    }
                }
                final Annotation[] annotations = methodDeclaration.annotations;
                if (annotations != null && !methodDeclaration.isClinit() && (methodDeclaration.isConstructor() || binding.returnType.id != 6)) {
                    methodDeclaration.getAllAnnotationContexts(20, allTypeAnnotationContexts);
                }
                if (!methodDeclaration.isConstructor() && !methodDeclaration.isClinit() && binding.returnType.id != 6) {
                    final MethodDeclaration declaration = (MethodDeclaration)methodDeclaration;
                    final TypeReference typeReference = declaration.returnType;
                    if ((typeReference.bits & 0x100000) != 0x0) {
                        typeReference.getAllAnnotationContexts(20, allTypeAnnotationContexts);
                    }
                }
                final TypeReference[] thrownExceptions = methodDeclaration.thrownExceptions;
                if (thrownExceptions != null) {
                    for (int i = 0, max = thrownExceptions.length; i < max; ++i) {
                        final TypeReference thrownException = thrownExceptions[i];
                        thrownException.getAllAnnotationContexts(23, i, allTypeAnnotationContexts);
                    }
                }
                final TypeParameter[] typeParameters = methodDeclaration.typeParameters();
                if (typeParameters != null) {
                    for (int j = 0, max2 = typeParameters.length; j < max2; ++j) {
                        final TypeParameter typeParameter = typeParameters[j];
                        if ((typeParameter.bits & 0x100000) != 0x0) {
                            typeParameter.getAllAnnotationContexts(1, j, allTypeAnnotationContexts);
                        }
                    }
                }
            }
            else if (binding.sourceLambda() != null) {
                final LambdaExpression lambda = binding.sourceLambda();
                if ((lambda.bits & 0x100000) != 0x0 && lambda.arguments != null) {
                    this.completeArgumentAnnotationInfo(lambda.arguments, allTypeAnnotationContexts);
                }
            }
            final int size = allTypeAnnotationContexts.size();
            if (size != 0) {
                final AnnotationContext[] allTypeAnnotationContextsArray = new AnnotationContext[size];
                allTypeAnnotationContexts.toArray(allTypeAnnotationContextsArray);
                for (int k = 0, max3 = allTypeAnnotationContextsArray.length; k < max3; ++k) {
                    final AnnotationContext annotationContext = allTypeAnnotationContextsArray[k];
                    if ((annotationContext.visibility & 0x2) != 0x0) {
                        ++invisibleTypeAnnotationsCounter;
                    }
                    else {
                        ++visibleTypeAnnotationsCounter;
                    }
                }
                attributesNumber += this.generateRuntimeTypeAnnotations(allTypeAnnotationContextsArray, visibleTypeAnnotationsCounter, invisibleTypeAnnotationsCounter);
            }
        }
        if ((this.produceAttributes & 0x40) != 0x0) {
            attributesNumber += this.generateMethodParameters(binding);
        }
        this.contents[methodAttributeOffset++] = (byte)(attributesNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributesNumber;
    }
    
    private void dumpLocations(final int[] locations) {
        if (locations == null) {
            if (this.contentsOffset + 1 >= this.contents.length) {
                this.resizeContents(1);
            }
            this.contents[this.contentsOffset++] = 0;
        }
        else {
            final int length = locations.length;
            if (this.contentsOffset + length >= this.contents.length) {
                this.resizeContents(length + 1);
            }
            this.contents[this.contentsOffset++] = (byte)(locations.length / 2);
            for (int i = 0; i < length; ++i) {
                this.contents[this.contentsOffset++] = (byte)locations[i];
            }
        }
    }
    
    private void dumpTargetTypeContents(final int targetType, final AnnotationContext annotationContext) {
        switch (targetType) {
            case 0:
            case 1: {
                this.contents[this.contentsOffset++] = (byte)annotationContext.info;
                break;
            }
            case 17: {
                this.contents[this.contentsOffset++] = (byte)annotationContext.info;
                this.contents[this.contentsOffset++] = (byte)annotationContext.info2;
            }
            case 22: {
                this.contents[this.contentsOffset++] = (byte)annotationContext.info;
                break;
            }
            case 66:
            case 67:
            case 68:
            case 69:
            case 70: {
                this.contents[this.contentsOffset++] = (byte)(annotationContext.info >> 8);
                this.contents[this.contentsOffset++] = (byte)annotationContext.info;
                break;
            }
            case 71: {
                this.contents[this.contentsOffset++] = (byte)(annotationContext.info >> 8);
                this.contents[this.contentsOffset++] = (byte)annotationContext.info;
                this.contents[this.contentsOffset++] = (byte)annotationContext.info2;
                break;
            }
            case 72:
            case 73:
            case 74:
            case 75: {
                this.contents[this.contentsOffset++] = (byte)(annotationContext.info >> 8);
                this.contents[this.contentsOffset++] = (byte)annotationContext.info;
                this.contents[this.contentsOffset++] = (byte)annotationContext.info2;
                break;
            }
            case 16:
            case 23: {
                this.contents[this.contentsOffset++] = (byte)(annotationContext.info >> 8);
                this.contents[this.contentsOffset++] = (byte)annotationContext.info;
                break;
            }
            case 64:
            case 65: {
                int localVariableTableOffset = this.contentsOffset;
                final LocalVariableBinding localVariable = annotationContext.variableBinding;
                int actualSize = 0;
                final int initializationCount = localVariable.initializationCount;
                actualSize += 2 + 6 * initializationCount;
                if (this.contentsOffset + actualSize >= this.contents.length) {
                    this.resizeContents(actualSize);
                }
                this.contentsOffset += 2;
                int numberOfEntries = 0;
                for (int j = 0; j < initializationCount; ++j) {
                    final int startPC = localVariable.initializationPCs[j << 1];
                    final int endPC = localVariable.initializationPCs[(j << 1) + 1];
                    if (startPC != endPC) {
                        ++numberOfEntries;
                        this.contents[this.contentsOffset++] = (byte)(startPC >> 8);
                        this.contents[this.contentsOffset++] = (byte)startPC;
                        final int length = endPC - startPC;
                        this.contents[this.contentsOffset++] = (byte)(length >> 8);
                        this.contents[this.contentsOffset++] = (byte)length;
                        final int resolvedPosition = localVariable.resolvedPosition;
                        this.contents[this.contentsOffset++] = (byte)(resolvedPosition >> 8);
                        this.contents[this.contentsOffset++] = (byte)resolvedPosition;
                    }
                }
                this.contents[localVariableTableOffset++] = (byte)(numberOfEntries >> 8);
                this.contents[localVariableTableOffset] = (byte)numberOfEntries;
                break;
            }
            case 18: {
                this.contents[this.contentsOffset++] = (byte)annotationContext.info;
                this.contents[this.contentsOffset++] = (byte)annotationContext.info2;
                break;
            }
        }
    }
    
    public char[] fileName() {
        return this.constantPool.UTF8Cache.returnKeyFor(2);
    }
    
    private void generateAnnotation(final Annotation annotation, final int currentOffset) {
        final int startingContentsOffset = currentOffset;
        if (this.contentsOffset + 4 >= this.contents.length) {
            this.resizeContents(4);
        }
        final TypeBinding annotationTypeBinding = annotation.resolvedType;
        if (annotationTypeBinding == null) {
            this.contentsOffset = startingContentsOffset;
            return;
        }
        if (annotationTypeBinding.isMemberType()) {
            this.recordInnerClasses(annotationTypeBinding);
        }
        final int typeIndex = this.constantPool.literalIndex(annotationTypeBinding.signature());
        this.contents[this.contentsOffset++] = (byte)(typeIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)typeIndex;
        if (annotation instanceof NormalAnnotation) {
            final NormalAnnotation normalAnnotation = (NormalAnnotation)annotation;
            final MemberValuePair[] memberValuePairs = normalAnnotation.memberValuePairs;
            final int memberValuePairOffset = this.contentsOffset;
            if (memberValuePairs != null) {
                int memberValuePairsCount = 0;
                int memberValuePairsLengthPosition = this.contentsOffset;
                this.contentsOffset += 2;
                int resetPosition = this.contentsOffset;
                for (final MemberValuePair memberValuePair : memberValuePairs) {
                    if (this.contentsOffset + 2 >= this.contents.length) {
                        this.resizeContents(2);
                    }
                    final int elementNameIndex = this.constantPool.literalIndex(memberValuePair.name);
                    this.contents[this.contentsOffset++] = (byte)(elementNameIndex >> 8);
                    this.contents[this.contentsOffset++] = (byte)elementNameIndex;
                    final MethodBinding methodBinding = memberValuePair.binding;
                    if (methodBinding == null) {
                        this.contentsOffset = resetPosition;
                    }
                    else {
                        try {
                            this.generateElementValue(memberValuePair.value, methodBinding.returnType, memberValuePairOffset);
                            if (this.contentsOffset == memberValuePairOffset) {
                                this.contents[this.contentsOffset++] = 0;
                                this.contents[this.contentsOffset++] = 0;
                                break;
                            }
                            ++memberValuePairsCount;
                            resetPosition = this.contentsOffset;
                        }
                        catch (final ClassCastException ex) {
                            this.contentsOffset = resetPosition;
                        }
                        catch (final ShouldNotImplement shouldNotImplement) {
                            this.contentsOffset = resetPosition;
                        }
                    }
                }
                this.contents[memberValuePairsLengthPosition++] = (byte)(memberValuePairsCount >> 8);
                this.contents[memberValuePairsLengthPosition++] = (byte)memberValuePairsCount;
            }
            else {
                this.contents[this.contentsOffset++] = 0;
                this.contents[this.contentsOffset++] = 0;
            }
        }
        else if (annotation instanceof SingleMemberAnnotation) {
            final SingleMemberAnnotation singleMemberAnnotation = (SingleMemberAnnotation)annotation;
            this.contents[this.contentsOffset++] = 0;
            this.contents[this.contentsOffset++] = 1;
            if (this.contentsOffset + 2 >= this.contents.length) {
                this.resizeContents(2);
            }
            final int elementNameIndex2 = this.constantPool.literalIndex(ClassFile.VALUE);
            this.contents[this.contentsOffset++] = (byte)(elementNameIndex2 >> 8);
            this.contents[this.contentsOffset++] = (byte)elementNameIndex2;
            final MethodBinding methodBinding2 = singleMemberAnnotation.memberValuePairs()[0].binding;
            if (methodBinding2 == null) {
                this.contentsOffset = startingContentsOffset;
            }
            else {
                final int memberValuePairOffset2 = this.contentsOffset;
                try {
                    this.generateElementValue(singleMemberAnnotation.memberValue, methodBinding2.returnType, memberValuePairOffset2);
                    if (this.contentsOffset == memberValuePairOffset2) {
                        this.contentsOffset = startingContentsOffset;
                    }
                }
                catch (final ClassCastException ex2) {
                    this.contentsOffset = startingContentsOffset;
                }
                catch (final ShouldNotImplement shouldNotImplement2) {
                    this.contentsOffset = startingContentsOffset;
                }
            }
        }
        else {
            this.contents[this.contentsOffset++] = 0;
            this.contents[this.contentsOffset++] = 0;
        }
    }
    
    private int generateAnnotationDefaultAttribute(final AnnotationMethodDeclaration declaration, final int attributeOffset) {
        int attributesNumber = 0;
        final int annotationDefaultNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.AnnotationDefaultName);
        if (this.contentsOffset + 6 >= this.contents.length) {
            this.resizeContents(6);
        }
        this.contents[this.contentsOffset++] = (byte)(annotationDefaultNameIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)annotationDefaultNameIndex;
        int attributeLengthOffset = this.contentsOffset;
        this.contentsOffset += 4;
        this.generateElementValue(declaration.defaultValue, declaration.binding.returnType, attributeOffset);
        if (this.contentsOffset != attributeOffset) {
            final int attributeLength = this.contentsOffset - attributeLengthOffset - 4;
            this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 24);
            this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 16);
            this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 8);
            this.contents[attributeLengthOffset++] = (byte)attributeLength;
            ++attributesNumber;
        }
        return attributesNumber;
    }
    
    public void generateCodeAttributeHeader() {
        if (this.contentsOffset + 20 >= this.contents.length) {
            this.resizeContents(20);
        }
        final int constantValueNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.CodeName);
        this.contents[this.contentsOffset++] = (byte)(constantValueNameIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)constantValueNameIndex;
        this.contentsOffset += 12;
    }
    
    private int generateConstantValueAttribute(final Constant fieldConstant, final FieldBinding fieldBinding, final int fieldAttributeOffset) {
        int localContentsOffset = this.contentsOffset;
        int attributesNumber = 1;
        if (localContentsOffset + 8 >= this.contents.length) {
            this.resizeContents(8);
        }
        final int constantValueNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.ConstantValueName);
        this.contents[localContentsOffset++] = (byte)(constantValueNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)constantValueNameIndex;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 2;
        switch (fieldConstant.typeID()) {
            case 5: {
                final int booleanValueIndex = this.constantPool.literalIndex(fieldConstant.booleanValue() ? 1 : 0);
                this.contents[localContentsOffset++] = (byte)(booleanValueIndex >> 8);
                this.contents[localContentsOffset++] = (byte)booleanValueIndex;
                break;
            }
            case 2:
            case 3:
            case 4:
            case 10: {
                final int integerValueIndex = this.constantPool.literalIndex(fieldConstant.intValue());
                this.contents[localContentsOffset++] = (byte)(integerValueIndex >> 8);
                this.contents[localContentsOffset++] = (byte)integerValueIndex;
                break;
            }
            case 9: {
                final int floatValueIndex = this.constantPool.literalIndex(fieldConstant.floatValue());
                this.contents[localContentsOffset++] = (byte)(floatValueIndex >> 8);
                this.contents[localContentsOffset++] = (byte)floatValueIndex;
                break;
            }
            case 8: {
                final int doubleValueIndex = this.constantPool.literalIndex(fieldConstant.doubleValue());
                this.contents[localContentsOffset++] = (byte)(doubleValueIndex >> 8);
                this.contents[localContentsOffset++] = (byte)doubleValueIndex;
                break;
            }
            case 7: {
                final int longValueIndex = this.constantPool.literalIndex(fieldConstant.longValue());
                this.contents[localContentsOffset++] = (byte)(longValueIndex >> 8);
                this.contents[localContentsOffset++] = (byte)longValueIndex;
                break;
            }
            case 11: {
                final int stringValueIndex = this.constantPool.literalIndex(((StringConstant)fieldConstant).stringValue());
                if (stringValueIndex != -1) {
                    this.contents[localContentsOffset++] = (byte)(stringValueIndex >> 8);
                    this.contents[localContentsOffset++] = (byte)stringValueIndex;
                    break;
                }
                if (!this.creatingProblemType) {
                    final TypeDeclaration typeDeclaration = this.referenceBinding.scope.referenceContext;
                    final FieldDeclaration[] fieldDecls = typeDeclaration.fields;
                    for (int max = (fieldDecls == null) ? 0 : fieldDecls.length, i = 0; i < max; ++i) {
                        if (fieldDecls[i].binding == fieldBinding) {
                            typeDeclaration.scope.problemReporter().stringConstantIsExceedingUtf8Limit(fieldDecls[i]);
                        }
                    }
                    break;
                }
                this.contentsOffset = fieldAttributeOffset;
                attributesNumber = 0;
                break;
            }
        }
        this.contentsOffset = localContentsOffset;
        return attributesNumber;
    }
    
    private int generateDeprecatedAttribute() {
        int localContentsOffset = this.contentsOffset;
        if (localContentsOffset + 6 >= this.contents.length) {
            this.resizeContents(6);
        }
        final int deprecatedAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.DeprecatedName);
        this.contents[localContentsOffset++] = (byte)(deprecatedAttributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)deprecatedAttributeNameIndex;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contentsOffset = localContentsOffset;
        return 1;
    }
    
    private void generateElementValue(final Expression defaultValue, final TypeBinding memberValuePairReturnType, final int attributeOffset) {
        final Constant constant = defaultValue.constant;
        final TypeBinding defaultValueBinding = defaultValue.resolvedType;
        if (defaultValueBinding == null) {
            this.contentsOffset = attributeOffset;
        }
        else {
            if (defaultValueBinding.isMemberType()) {
                this.recordInnerClasses(defaultValueBinding);
            }
            if (memberValuePairReturnType.isMemberType()) {
                this.recordInnerClasses(memberValuePairReturnType);
            }
            if (memberValuePairReturnType.isArrayType() && !defaultValueBinding.isArrayType()) {
                if (this.contentsOffset + 3 >= this.contents.length) {
                    this.resizeContents(3);
                }
                this.contents[this.contentsOffset++] = 91;
                this.contents[this.contentsOffset++] = 0;
                this.contents[this.contentsOffset++] = 1;
            }
            if (constant != null && constant != Constant.NotAConstant) {
                this.generateElementValue(attributeOffset, defaultValue, constant, memberValuePairReturnType.leafComponentType());
            }
            else {
                this.generateElementValueForNonConstantExpression(defaultValue, attributeOffset, defaultValueBinding);
            }
        }
    }
    
    private void generateElementValue(final int attributeOffset, final Expression defaultValue, final Constant constant, final TypeBinding binding) {
        if (this.contentsOffset + 3 >= this.contents.length) {
            this.resizeContents(3);
        }
        switch (binding.id) {
            case 5: {
                this.contents[this.contentsOffset++] = 90;
                final int booleanValueIndex = this.constantPool.literalIndex(constant.booleanValue() ? 1 : 0);
                this.contents[this.contentsOffset++] = (byte)(booleanValueIndex >> 8);
                this.contents[this.contentsOffset++] = (byte)booleanValueIndex;
                break;
            }
            case 3: {
                this.contents[this.contentsOffset++] = 66;
                final int integerValueIndex = this.constantPool.literalIndex(constant.intValue());
                this.contents[this.contentsOffset++] = (byte)(integerValueIndex >> 8);
                this.contents[this.contentsOffset++] = (byte)integerValueIndex;
                break;
            }
            case 2: {
                this.contents[this.contentsOffset++] = 67;
                final int integerValueIndex = this.constantPool.literalIndex(constant.intValue());
                this.contents[this.contentsOffset++] = (byte)(integerValueIndex >> 8);
                this.contents[this.contentsOffset++] = (byte)integerValueIndex;
                break;
            }
            case 10: {
                this.contents[this.contentsOffset++] = 73;
                final int integerValueIndex = this.constantPool.literalIndex(constant.intValue());
                this.contents[this.contentsOffset++] = (byte)(integerValueIndex >> 8);
                this.contents[this.contentsOffset++] = (byte)integerValueIndex;
                break;
            }
            case 4: {
                this.contents[this.contentsOffset++] = 83;
                final int integerValueIndex = this.constantPool.literalIndex(constant.intValue());
                this.contents[this.contentsOffset++] = (byte)(integerValueIndex >> 8);
                this.contents[this.contentsOffset++] = (byte)integerValueIndex;
                break;
            }
            case 9: {
                this.contents[this.contentsOffset++] = 70;
                final int floatValueIndex = this.constantPool.literalIndex(constant.floatValue());
                this.contents[this.contentsOffset++] = (byte)(floatValueIndex >> 8);
                this.contents[this.contentsOffset++] = (byte)floatValueIndex;
                break;
            }
            case 8: {
                this.contents[this.contentsOffset++] = 68;
                final int doubleValueIndex = this.constantPool.literalIndex(constant.doubleValue());
                this.contents[this.contentsOffset++] = (byte)(doubleValueIndex >> 8);
                this.contents[this.contentsOffset++] = (byte)doubleValueIndex;
                break;
            }
            case 7: {
                this.contents[this.contentsOffset++] = 74;
                final int longValueIndex = this.constantPool.literalIndex(constant.longValue());
                this.contents[this.contentsOffset++] = (byte)(longValueIndex >> 8);
                this.contents[this.contentsOffset++] = (byte)longValueIndex;
                break;
            }
            case 11: {
                this.contents[this.contentsOffset++] = 115;
                final int stringValueIndex = this.constantPool.literalIndex(((StringConstant)constant).stringValue().toCharArray());
                if (stringValueIndex != -1) {
                    this.contents[this.contentsOffset++] = (byte)(stringValueIndex >> 8);
                    this.contents[this.contentsOffset++] = (byte)stringValueIndex;
                    break;
                }
                if (!this.creatingProblemType) {
                    final TypeDeclaration typeDeclaration = this.referenceBinding.scope.referenceContext;
                    typeDeclaration.scope.problemReporter().stringConstantIsExceedingUtf8Limit(defaultValue);
                    break;
                }
                this.contentsOffset = attributeOffset;
                break;
            }
        }
    }
    
    private void generateElementValueForNonConstantExpression(final Expression defaultValue, final int attributeOffset, final TypeBinding defaultValueBinding) {
        if (defaultValueBinding != null) {
            if (defaultValueBinding.isEnum()) {
                if (this.contentsOffset + 5 >= this.contents.length) {
                    this.resizeContents(5);
                }
                this.contents[this.contentsOffset++] = 101;
                FieldBinding fieldBinding = null;
                if (defaultValue instanceof QualifiedNameReference) {
                    final QualifiedNameReference nameReference = (QualifiedNameReference)defaultValue;
                    fieldBinding = (FieldBinding)nameReference.binding;
                }
                else if (defaultValue instanceof SingleNameReference) {
                    final SingleNameReference nameReference2 = (SingleNameReference)defaultValue;
                    fieldBinding = (FieldBinding)nameReference2.binding;
                }
                else {
                    this.contentsOffset = attributeOffset;
                }
                if (fieldBinding != null) {
                    final int enumConstantTypeNameIndex = this.constantPool.literalIndex(fieldBinding.type.signature());
                    final int enumConstantNameIndex = this.constantPool.literalIndex(fieldBinding.name);
                    this.contents[this.contentsOffset++] = (byte)(enumConstantTypeNameIndex >> 8);
                    this.contents[this.contentsOffset++] = (byte)enumConstantTypeNameIndex;
                    this.contents[this.contentsOffset++] = (byte)(enumConstantNameIndex >> 8);
                    this.contents[this.contentsOffset++] = (byte)enumConstantNameIndex;
                }
            }
            else if (defaultValueBinding.isAnnotationType()) {
                if (this.contentsOffset + 1 >= this.contents.length) {
                    this.resizeContents(1);
                }
                this.contents[this.contentsOffset++] = 64;
                this.generateAnnotation((Annotation)defaultValue, attributeOffset);
            }
            else if (defaultValueBinding.isArrayType()) {
                if (this.contentsOffset + 3 >= this.contents.length) {
                    this.resizeContents(3);
                }
                this.contents[this.contentsOffset++] = 91;
                if (defaultValue instanceof ArrayInitializer) {
                    final ArrayInitializer arrayInitializer = (ArrayInitializer)defaultValue;
                    final int arrayLength = (arrayInitializer.expressions != null) ? arrayInitializer.expressions.length : 0;
                    this.contents[this.contentsOffset++] = (byte)(arrayLength >> 8);
                    this.contents[this.contentsOffset++] = (byte)arrayLength;
                    for (int i = 0; i < arrayLength; ++i) {
                        this.generateElementValue(arrayInitializer.expressions[i], defaultValueBinding.leafComponentType(), attributeOffset);
                    }
                }
                else {
                    this.contentsOffset = attributeOffset;
                }
            }
            else {
                if (this.contentsOffset + 3 >= this.contents.length) {
                    this.resizeContents(3);
                }
                this.contents[this.contentsOffset++] = 99;
                if (defaultValue instanceof ClassLiteralAccess) {
                    final ClassLiteralAccess classLiteralAccess = (ClassLiteralAccess)defaultValue;
                    final int classInfoIndex = this.constantPool.literalIndex(classLiteralAccess.targetType.signature());
                    this.contents[this.contentsOffset++] = (byte)(classInfoIndex >> 8);
                    this.contents[this.contentsOffset++] = (byte)classInfoIndex;
                }
                else {
                    this.contentsOffset = attributeOffset;
                }
            }
        }
        else {
            this.contentsOffset = attributeOffset;
        }
    }
    
    private int generateEnclosingMethodAttribute() {
        int localContentsOffset = this.contentsOffset;
        if (localContentsOffset + 10 >= this.contents.length) {
            this.resizeContents(10);
        }
        final int enclosingMethodAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.EnclosingMethodName);
        this.contents[localContentsOffset++] = (byte)(enclosingMethodAttributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)enclosingMethodAttributeNameIndex;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 4;
        final int enclosingTypeIndex = this.constantPool.literalIndexForType(this.referenceBinding.enclosingType().constantPoolName());
        this.contents[localContentsOffset++] = (byte)(enclosingTypeIndex >> 8);
        this.contents[localContentsOffset++] = (byte)enclosingTypeIndex;
        byte methodIndexByte1 = 0;
        byte methodIndexByte2 = 0;
        if (this.referenceBinding instanceof LocalTypeBinding) {
            final MethodBinding methodBinding = ((LocalTypeBinding)this.referenceBinding).enclosingMethod;
            if (methodBinding != null) {
                final int enclosingMethodIndex = this.constantPool.literalIndexForNameAndType(methodBinding.selector, methodBinding.signature(this));
                methodIndexByte1 = (byte)(enclosingMethodIndex >> 8);
                methodIndexByte2 = (byte)enclosingMethodIndex;
            }
        }
        this.contents[localContentsOffset++] = methodIndexByte1;
        this.contents[localContentsOffset++] = methodIndexByte2;
        this.contentsOffset = localContentsOffset;
        return 1;
    }
    
    private int generateExceptionsAttribute(final ReferenceBinding[] thrownsExceptions) {
        int localContentsOffset = this.contentsOffset;
        final int length = thrownsExceptions.length;
        final int exSize = 8 + length * 2;
        if (exSize + this.contentsOffset >= this.contents.length) {
            this.resizeContents(exSize);
        }
        final int exceptionNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.ExceptionsName);
        this.contents[localContentsOffset++] = (byte)(exceptionNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)exceptionNameIndex;
        final int attributeLength = length * 2 + 2;
        this.contents[localContentsOffset++] = (byte)(attributeLength >> 24);
        this.contents[localContentsOffset++] = (byte)(attributeLength >> 16);
        this.contents[localContentsOffset++] = (byte)(attributeLength >> 8);
        this.contents[localContentsOffset++] = (byte)attributeLength;
        this.contents[localContentsOffset++] = (byte)(length >> 8);
        this.contents[localContentsOffset++] = (byte)length;
        for (int i = 0; i < length; ++i) {
            final int exceptionIndex = this.constantPool.literalIndexForType(thrownsExceptions[i]);
            this.contents[localContentsOffset++] = (byte)(exceptionIndex >> 8);
            this.contents[localContentsOffset++] = (byte)exceptionIndex;
        }
        this.contentsOffset = localContentsOffset;
        return 1;
    }
    
    private int generateHierarchyInconsistentAttribute() {
        int localContentsOffset = this.contentsOffset;
        if (localContentsOffset + 6 >= this.contents.length) {
            this.resizeContents(6);
        }
        final int inconsistentHierarchyNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.InconsistentHierarchy);
        this.contents[localContentsOffset++] = (byte)(inconsistentHierarchyNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)inconsistentHierarchyNameIndex;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contentsOffset = localContentsOffset;
        return 1;
    }
    
    private int generateInnerClassAttribute(final int numberOfInnerClasses, final ReferenceBinding[] innerClasses) {
        int localContentsOffset = this.contentsOffset;
        final int exSize = 8 * numberOfInnerClasses + 8;
        if (exSize + localContentsOffset >= this.contents.length) {
            this.resizeContents(exSize);
        }
        final int attributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.InnerClassName);
        this.contents[localContentsOffset++] = (byte)(attributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)attributeNameIndex;
        final int value = (numberOfInnerClasses << 3) + 2;
        this.contents[localContentsOffset++] = (byte)(value >> 24);
        this.contents[localContentsOffset++] = (byte)(value >> 16);
        this.contents[localContentsOffset++] = (byte)(value >> 8);
        this.contents[localContentsOffset++] = (byte)value;
        this.contents[localContentsOffset++] = (byte)(numberOfInnerClasses >> 8);
        this.contents[localContentsOffset++] = (byte)numberOfInnerClasses;
        for (final ReferenceBinding innerClass : innerClasses) {
            int accessFlags = innerClass.getAccessFlags();
            final int innerClassIndex = this.constantPool.literalIndexForType(innerClass.constantPoolName());
            this.contents[localContentsOffset++] = (byte)(innerClassIndex >> 8);
            this.contents[localContentsOffset++] = (byte)innerClassIndex;
            if (innerClass.isMemberType()) {
                final int outerClassIndex = this.constantPool.literalIndexForType(innerClass.enclosingType().constantPoolName());
                this.contents[localContentsOffset++] = (byte)(outerClassIndex >> 8);
                this.contents[localContentsOffset++] = (byte)outerClassIndex;
            }
            else {
                this.contents[localContentsOffset++] = 0;
                this.contents[localContentsOffset++] = 0;
            }
            if (!innerClass.isAnonymousType()) {
                final int nameIndex = this.constantPool.literalIndex(innerClass.sourceName());
                this.contents[localContentsOffset++] = (byte)(nameIndex >> 8);
                this.contents[localContentsOffset++] = (byte)nameIndex;
            }
            else {
                this.contents[localContentsOffset++] = 0;
                this.contents[localContentsOffset++] = 0;
            }
            if (innerClass.isAnonymousType()) {
                accessFlags &= 0xFFFFFFEF;
            }
            else if (innerClass.isMemberType() && innerClass.isInterface()) {
                accessFlags |= 0x8;
            }
            this.contents[localContentsOffset++] = (byte)(accessFlags >> 8);
            this.contents[localContentsOffset++] = (byte)accessFlags;
        }
        this.contentsOffset = localContentsOffset;
        return 1;
    }
    
    private int generateBootstrapMethods(final List functionalExpressionList) {
        final ReferenceBinding methodHandlesLookup = this.referenceBinding.scope.getJavaLangInvokeMethodHandlesLookup();
        if (methodHandlesLookup == null) {
            return 0;
        }
        this.recordInnerClasses(methodHandlesLookup);
        final ReferenceBinding javaLangInvokeLambdaMetafactory = this.referenceBinding.scope.getJavaLangInvokeLambdaMetafactory();
        int indexForMetaFactory = 0;
        int indexForAltMetaFactory = 0;
        final int numberOfBootstraps = functionalExpressionList.size();
        int localContentsOffset = this.contentsOffset;
        final int exSize = 10 * numberOfBootstraps + 8;
        if (exSize + localContentsOffset >= this.contents.length) {
            this.resizeContents(exSize);
        }
        final int attributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.BootstrapMethodsName);
        this.contents[localContentsOffset++] = (byte)(attributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)attributeNameIndex;
        int attributeLengthPosition = localContentsOffset;
        localContentsOffset += 4;
        this.contents[localContentsOffset++] = (byte)(numberOfBootstraps >> 8);
        this.contents[localContentsOffset++] = (byte)numberOfBootstraps;
        for (int i = 0; i < numberOfBootstraps; ++i) {
            final FunctionalExpression functional = functionalExpressionList.get(i);
            final MethodBinding[] bridges = functional.getRequiredBridges();
            TypeBinding[] markerInterfaces = null;
            if ((functional instanceof LambdaExpression && (markerInterfaces = ((LambdaExpression)functional).getMarkerInterfaces()) != null) || bridges != null || functional.isSerializable) {
                int extraSpace = 2;
                if (markerInterfaces != null) {
                    extraSpace += 2 + 2 * markerInterfaces.length;
                }
                if (bridges != null) {
                    extraSpace += 2 + 2 * bridges.length;
                }
                if (extraSpace + 10 + localContentsOffset >= this.contents.length) {
                    this.resizeContents(extraSpace + 10);
                }
                if (indexForAltMetaFactory == 0) {
                    indexForAltMetaFactory = this.constantPool.literalIndexForMethodHandle(6, javaLangInvokeLambdaMetafactory, ConstantPool.ALTMETAFACTORY, ConstantPool.JAVA_LANG_INVOKE_LAMBDAMETAFACTORY_ALTMETAFACTORY_SIGNATURE, false);
                }
                this.contents[localContentsOffset++] = (byte)(indexForAltMetaFactory >> 8);
                this.contents[localContentsOffset++] = (byte)indexForAltMetaFactory;
                this.contents[localContentsOffset++] = 0;
                this.contents[localContentsOffset++] = (byte)(4 + ((markerInterfaces == null) ? 0 : (1 + markerInterfaces.length)) + ((bridges == null) ? 0 : (1 + bridges.length)));
                final int functionalDescriptorIndex = this.constantPool.literalIndexForMethodType(functional.descriptor.original().signature());
                this.contents[localContentsOffset++] = (byte)(functionalDescriptorIndex >> 8);
                this.contents[localContentsOffset++] = (byte)functionalDescriptorIndex;
                final int methodHandleIndex = this.constantPool.literalIndexForMethodHandle(functional.binding.original());
                this.contents[localContentsOffset++] = (byte)(methodHandleIndex >> 8);
                this.contents[localContentsOffset++] = (byte)methodHandleIndex;
                final char[] instantiatedSignature = functional.descriptor.signature();
                final int methodTypeIndex = this.constantPool.literalIndexForMethodType(instantiatedSignature);
                this.contents[localContentsOffset++] = (byte)(methodTypeIndex >> 8);
                this.contents[localContentsOffset++] = (byte)methodTypeIndex;
                int bitflags = 0;
                if (functional.isSerializable) {
                    bitflags |= 0x1;
                }
                if (markerInterfaces != null) {
                    bitflags |= 0x2;
                }
                if (bridges != null) {
                    bitflags |= 0x4;
                }
                final int indexForBitflags = this.constantPool.literalIndex(bitflags);
                this.contents[localContentsOffset++] = (byte)(indexForBitflags >> 8);
                this.contents[localContentsOffset++] = (byte)indexForBitflags;
                if (markerInterfaces != null) {
                    final int markerInterfaceCountIndex = this.constantPool.literalIndex(markerInterfaces.length);
                    this.contents[localContentsOffset++] = (byte)(markerInterfaceCountIndex >> 8);
                    this.contents[localContentsOffset++] = (byte)markerInterfaceCountIndex;
                    for (int m = 0, maxm = markerInterfaces.length; m < maxm; ++m) {
                        final int classTypeIndex = this.constantPool.literalIndexForType(markerInterfaces[m]);
                        this.contents[localContentsOffset++] = (byte)(classTypeIndex >> 8);
                        this.contents[localContentsOffset++] = (byte)classTypeIndex;
                    }
                }
                if (bridges != null) {
                    final int bridgeCountIndex = this.constantPool.literalIndex(bridges.length);
                    this.contents[localContentsOffset++] = (byte)(bridgeCountIndex >> 8);
                    this.contents[localContentsOffset++] = (byte)bridgeCountIndex;
                    for (int m = 0, maxm = bridges.length; m < maxm; ++m) {
                        final char[] bridgeSignature = bridges[m].signature();
                        final int bridgeMethodTypeIndex = this.constantPool.literalIndexForMethodType(bridgeSignature);
                        this.contents[localContentsOffset++] = (byte)(bridgeMethodTypeIndex >> 8);
                        this.contents[localContentsOffset++] = (byte)bridgeMethodTypeIndex;
                    }
                }
            }
            else {
                if (10 + localContentsOffset >= this.contents.length) {
                    this.resizeContents(10);
                }
                if (indexForMetaFactory == 0) {
                    indexForMetaFactory = this.constantPool.literalIndexForMethodHandle(6, javaLangInvokeLambdaMetafactory, ConstantPool.METAFACTORY, ConstantPool.JAVA_LANG_INVOKE_LAMBDAMETAFACTORY_METAFACTORY_SIGNATURE, false);
                }
                this.contents[localContentsOffset++] = (byte)(indexForMetaFactory >> 8);
                this.contents[localContentsOffset++] = (byte)indexForMetaFactory;
                this.contents[localContentsOffset++] = 0;
                this.contents[localContentsOffset++] = 3;
                final int functionalDescriptorIndex2 = this.constantPool.literalIndexForMethodType(functional.descriptor.original().signature());
                this.contents[localContentsOffset++] = (byte)(functionalDescriptorIndex2 >> 8);
                this.contents[localContentsOffset++] = (byte)functionalDescriptorIndex2;
                final int methodHandleIndex2 = this.constantPool.literalIndexForMethodHandle(functional.binding.original());
                this.contents[localContentsOffset++] = (byte)(methodHandleIndex2 >> 8);
                this.contents[localContentsOffset++] = (byte)methodHandleIndex2;
                final char[] instantiatedSignature2 = functional.descriptor.signature();
                final int methodTypeIndex2 = this.constantPool.literalIndexForMethodType(instantiatedSignature2);
                this.contents[localContentsOffset++] = (byte)(methodTypeIndex2 >> 8);
                this.contents[localContentsOffset++] = (byte)methodTypeIndex2;
            }
        }
        final int attributeLength = localContentsOffset - attributeLengthPosition - 4;
        this.contents[attributeLengthPosition++] = (byte)(attributeLength >> 24);
        this.contents[attributeLengthPosition++] = (byte)(attributeLength >> 16);
        this.contents[attributeLengthPosition++] = (byte)(attributeLength >> 8);
        this.contents[attributeLengthPosition++] = (byte)attributeLength;
        this.contentsOffset = localContentsOffset;
        return 1;
    }
    
    private int generateLineNumberAttribute() {
        int localContentsOffset = this.contentsOffset;
        int attributesNumber = 0;
        final int[] pcToSourceMapTable;
        if ((pcToSourceMapTable = this.codeStream.pcToSourceMap) != null && this.codeStream.pcToSourceMapSize != 0) {
            final int lineNumberNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.LineNumberTableName);
            if (localContentsOffset + 8 >= this.contents.length) {
                this.resizeContents(8);
            }
            this.contents[localContentsOffset++] = (byte)(lineNumberNameIndex >> 8);
            this.contents[localContentsOffset++] = (byte)lineNumberNameIndex;
            int lineNumberTableOffset = localContentsOffset;
            localContentsOffset += 6;
            int numberOfEntries = 0;
            int pc;
            int lineNumber;
            for (int length = this.codeStream.pcToSourceMapSize, i = 0; i < length; pc = pcToSourceMapTable[i++], this.contents[localContentsOffset++] = (byte)(pc >> 8), this.contents[localContentsOffset++] = (byte)pc, lineNumber = pcToSourceMapTable[i++], this.contents[localContentsOffset++] = (byte)(lineNumber >> 8), this.contents[localContentsOffset++] = (byte)lineNumber, ++numberOfEntries) {
                if (localContentsOffset + 4 >= this.contents.length) {
                    this.resizeContents(4);
                }
            }
            final int lineNumberAttr_length = numberOfEntries * 4 + 2;
            this.contents[lineNumberTableOffset++] = (byte)(lineNumberAttr_length >> 24);
            this.contents[lineNumberTableOffset++] = (byte)(lineNumberAttr_length >> 16);
            this.contents[lineNumberTableOffset++] = (byte)(lineNumberAttr_length >> 8);
            this.contents[lineNumberTableOffset++] = (byte)lineNumberAttr_length;
            this.contents[lineNumberTableOffset++] = (byte)(numberOfEntries >> 8);
            this.contents[lineNumberTableOffset++] = (byte)numberOfEntries;
            attributesNumber = 1;
        }
        this.contentsOffset = localContentsOffset;
        return attributesNumber;
    }
    
    private int generateLineNumberAttribute(final int problemLine) {
        int localContentsOffset = this.contentsOffset;
        if (localContentsOffset + 12 >= this.contents.length) {
            this.resizeContents(12);
        }
        final int lineNumberNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.LineNumberTableName);
        this.contents[localContentsOffset++] = (byte)(lineNumberNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)lineNumberNameIndex;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 6;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 1;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = (byte)(problemLine >> 8);
        this.contents[localContentsOffset++] = (byte)problemLine;
        this.contentsOffset = localContentsOffset;
        return 1;
    }
    
    private int generateLocalVariableTableAttribute(final int code_length, final boolean methodDeclarationIsStatic, final boolean isSynthetic) {
        int attributesNumber = 0;
        int localContentsOffset = this.contentsOffset;
        int numberOfEntries = 0;
        final int localVariableNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.LocalVariableTableName);
        int maxOfEntries = 8 + 10 * (methodDeclarationIsStatic ? 0 : 1);
        for (int i = 0; i < this.codeStream.allLocalsCounter; ++i) {
            final LocalVariableBinding localVariableBinding = this.codeStream.locals[i];
            maxOfEntries += 10 * localVariableBinding.initializationCount;
        }
        if (localContentsOffset + maxOfEntries >= this.contents.length) {
            this.resizeContents(maxOfEntries);
        }
        this.contents[localContentsOffset++] = (byte)(localVariableNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)localVariableNameIndex;
        int localVariableTableOffset = localContentsOffset;
        localContentsOffset += 6;
        SourceTypeBinding declaringClassBinding = null;
        if (!methodDeclarationIsStatic && !isSynthetic) {
            ++numberOfEntries;
            this.contents[localContentsOffset++] = 0;
            this.contents[localContentsOffset++] = 0;
            this.contents[localContentsOffset++] = (byte)(code_length >> 8);
            this.contents[localContentsOffset++] = (byte)code_length;
            final int nameIndex = this.constantPool.literalIndex(ConstantPool.This);
            this.contents[localContentsOffset++] = (byte)(nameIndex >> 8);
            this.contents[localContentsOffset++] = (byte)nameIndex;
            declaringClassBinding = (SourceTypeBinding)((this.codeStream.methodDeclaration != null) ? this.codeStream.methodDeclaration.binding.declaringClass : this.codeStream.lambdaExpression.binding.declaringClass);
            final int descriptorIndex = this.constantPool.literalIndex(declaringClassBinding.signature());
            this.contents[localContentsOffset++] = (byte)(descriptorIndex >> 8);
            this.contents[localContentsOffset++] = (byte)descriptorIndex;
            this.contents[localContentsOffset++] = 0;
            this.contents[localContentsOffset++] = 0;
        }
        int genericLocalVariablesCounter = 0;
        LocalVariableBinding[] genericLocalVariables = null;
        int numberOfGenericEntries = 0;
        for (int j = 0, max = this.codeStream.allLocalsCounter; j < max; ++j) {
            final LocalVariableBinding localVariable = this.codeStream.locals[j];
            final int initializationCount = localVariable.initializationCount;
            if (initializationCount != 0) {
                if (localVariable.declaration != null) {
                    final TypeBinding localVariableTypeBinding = localVariable.type;
                    final boolean isParameterizedType = localVariableTypeBinding.isParameterizedType() || localVariableTypeBinding.isTypeVariable();
                    if (isParameterizedType) {
                        if (genericLocalVariables == null) {
                            genericLocalVariables = new LocalVariableBinding[max];
                        }
                        genericLocalVariables[genericLocalVariablesCounter++] = localVariable;
                    }
                    for (int k = 0; k < initializationCount; ++k) {
                        final int startPC = localVariable.initializationPCs[k << 1];
                        final int endPC = localVariable.initializationPCs[(k << 1) + 1];
                        if (startPC != endPC) {
                            if (endPC == -1) {
                                localVariable.declaringScope.problemReporter().abortDueToInternalError(Messages.bind(Messages.abort_invalidAttribute, new String(localVariable.name)), (ASTNode)localVariable.declaringScope.methodScope().referenceContext);
                            }
                            if (isParameterizedType) {
                                ++numberOfGenericEntries;
                            }
                            ++numberOfEntries;
                            this.contents[localContentsOffset++] = (byte)(startPC >> 8);
                            this.contents[localContentsOffset++] = (byte)startPC;
                            final int length = endPC - startPC;
                            this.contents[localContentsOffset++] = (byte)(length >> 8);
                            this.contents[localContentsOffset++] = (byte)length;
                            final int nameIndex = this.constantPool.literalIndex(localVariable.name);
                            this.contents[localContentsOffset++] = (byte)(nameIndex >> 8);
                            this.contents[localContentsOffset++] = (byte)nameIndex;
                            final int descriptorIndex = this.constantPool.literalIndex(localVariableTypeBinding.signature());
                            this.contents[localContentsOffset++] = (byte)(descriptorIndex >> 8);
                            this.contents[localContentsOffset++] = (byte)descriptorIndex;
                            final int resolvedPosition = localVariable.resolvedPosition;
                            this.contents[localContentsOffset++] = (byte)(resolvedPosition >> 8);
                            this.contents[localContentsOffset++] = (byte)resolvedPosition;
                        }
                    }
                }
            }
        }
        int value = numberOfEntries * 10 + 2;
        this.contents[localVariableTableOffset++] = (byte)(value >> 24);
        this.contents[localVariableTableOffset++] = (byte)(value >> 16);
        this.contents[localVariableTableOffset++] = (byte)(value >> 8);
        this.contents[localVariableTableOffset++] = (byte)value;
        this.contents[localVariableTableOffset++] = (byte)(numberOfEntries >> 8);
        this.contents[localVariableTableOffset] = (byte)numberOfEntries;
        ++attributesNumber;
        final boolean currentInstanceIsGeneric = !methodDeclarationIsStatic && declaringClassBinding != null && declaringClassBinding.typeVariables != Binding.NO_TYPE_VARIABLES;
        if (genericLocalVariablesCounter != 0 || currentInstanceIsGeneric) {
            numberOfGenericEntries += (currentInstanceIsGeneric ? 1 : 0);
            maxOfEntries = 8 + numberOfGenericEntries * 10;
            if (localContentsOffset + maxOfEntries >= this.contents.length) {
                this.resizeContents(maxOfEntries);
            }
            final int localVariableTypeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.LocalVariableTypeTableName);
            this.contents[localContentsOffset++] = (byte)(localVariableTypeNameIndex >> 8);
            this.contents[localContentsOffset++] = (byte)localVariableTypeNameIndex;
            value = numberOfGenericEntries * 10 + 2;
            this.contents[localContentsOffset++] = (byte)(value >> 24);
            this.contents[localContentsOffset++] = (byte)(value >> 16);
            this.contents[localContentsOffset++] = (byte)(value >> 8);
            this.contents[localContentsOffset++] = (byte)value;
            this.contents[localContentsOffset++] = (byte)(numberOfGenericEntries >> 8);
            this.contents[localContentsOffset++] = (byte)numberOfGenericEntries;
            if (currentInstanceIsGeneric) {
                this.contents[localContentsOffset++] = 0;
                this.contents[localContentsOffset++] = 0;
                this.contents[localContentsOffset++] = (byte)(code_length >> 8);
                this.contents[localContentsOffset++] = (byte)code_length;
                final int nameIndex = this.constantPool.literalIndex(ConstantPool.This);
                this.contents[localContentsOffset++] = (byte)(nameIndex >> 8);
                this.contents[localContentsOffset++] = (byte)nameIndex;
                final int descriptorIndex = this.constantPool.literalIndex(declaringClassBinding.genericTypeSignature());
                this.contents[localContentsOffset++] = (byte)(descriptorIndex >> 8);
                this.contents[localContentsOffset++] = (byte)descriptorIndex;
                this.contents[localContentsOffset++] = 0;
                this.contents[localContentsOffset++] = 0;
            }
            for (final LocalVariableBinding localVariable2 : genericLocalVariables) {
                for (int m = 0; m < localVariable2.initializationCount; ++m) {
                    final int startPC2 = localVariable2.initializationPCs[m << 1];
                    final int endPC2 = localVariable2.initializationPCs[(m << 1) + 1];
                    if (startPC2 != endPC2) {
                        this.contents[localContentsOffset++] = (byte)(startPC2 >> 8);
                        this.contents[localContentsOffset++] = (byte)startPC2;
                        final int length2 = endPC2 - startPC2;
                        this.contents[localContentsOffset++] = (byte)(length2 >> 8);
                        this.contents[localContentsOffset++] = (byte)length2;
                        final int nameIndex = this.constantPool.literalIndex(localVariable2.name);
                        this.contents[localContentsOffset++] = (byte)(nameIndex >> 8);
                        this.contents[localContentsOffset++] = (byte)nameIndex;
                        final int descriptorIndex = this.constantPool.literalIndex(localVariable2.type.genericTypeSignature());
                        this.contents[localContentsOffset++] = (byte)(descriptorIndex >> 8);
                        this.contents[localContentsOffset++] = (byte)descriptorIndex;
                        final int resolvedPosition2 = localVariable2.resolvedPosition;
                        this.contents[localContentsOffset++] = (byte)(resolvedPosition2 >> 8);
                        this.contents[localContentsOffset++] = (byte)resolvedPosition2;
                    }
                }
            }
            ++attributesNumber;
        }
        this.contentsOffset = localContentsOffset;
        return attributesNumber;
    }
    
    public int generateMethodInfoAttributes(final MethodBinding methodBinding) {
        this.contentsOffset += 2;
        if (this.contentsOffset + 2 >= this.contents.length) {
            this.resizeContents(2);
        }
        int attributesNumber = 0;
        final ReferenceBinding[] thrownsExceptions;
        if ((thrownsExceptions = methodBinding.thrownExceptions) != Binding.NO_EXCEPTIONS) {
            attributesNumber += this.generateExceptionsAttribute(thrownsExceptions);
        }
        if (methodBinding.isDeprecated()) {
            attributesNumber += this.generateDeprecatedAttribute();
        }
        if (this.targetJDK < 3211264L) {
            if (methodBinding.isSynthetic()) {
                attributesNumber += this.generateSyntheticAttribute();
            }
            if (methodBinding.isVarargs()) {
                attributesNumber += this.generateVarargsAttribute();
            }
        }
        final char[] genericSignature = methodBinding.genericSignature();
        if (genericSignature != null) {
            attributesNumber += this.generateSignatureAttribute(genericSignature);
        }
        if (this.targetJDK >= 3145728L) {
            AbstractMethodDeclaration methodDeclaration = methodBinding.sourceMethod();
            if (methodBinding instanceof SyntheticMethodBinding) {
                final SyntheticMethodBinding syntheticMethod = (SyntheticMethodBinding)methodBinding;
                if (syntheticMethod.purpose == 7 && CharOperation.equals(syntheticMethod.selector, syntheticMethod.targetMethod.selector)) {
                    methodDeclaration = ((SyntheticMethodBinding)methodBinding).targetMethod.sourceMethod();
                }
            }
            if (methodDeclaration != null) {
                final Annotation[] annotations = methodDeclaration.annotations;
                if (annotations != null) {
                    attributesNumber += this.generateRuntimeAnnotations(annotations, methodBinding.isConstructor() ? 1099511627776L : 274877906944L);
                }
                if ((methodBinding.tagBits & 0x400L) != 0x0L) {
                    final Argument[] arguments = methodDeclaration.arguments;
                    if (arguments != null) {
                        attributesNumber += this.generateRuntimeAnnotationsForParameters(arguments);
                    }
                }
            }
            else {
                final LambdaExpression lambda = methodBinding.sourceLambda();
                if (lambda != null && (methodBinding.tagBits & 0x400L) != 0x0L) {
                    Argument[] arguments = lambda.arguments();
                    if (arguments != null) {
                        final int parameterCount = methodBinding.parameters.length;
                        final int argumentCount = arguments.length;
                        if (parameterCount > argumentCount) {
                            final int redShift = parameterCount - argumentCount;
                            System.arraycopy(arguments, 0, arguments = new Argument[parameterCount], redShift, argumentCount);
                            for (int i = 0; i < redShift; ++i) {
                                arguments[i] = new Argument(CharOperation.NO_CHAR, 0L, null, 0);
                            }
                        }
                        attributesNumber += this.generateRuntimeAnnotationsForParameters(arguments);
                    }
                }
            }
        }
        if ((methodBinding.tagBits & 0x80L) != 0x0L) {
            this.missingTypes = methodBinding.collectMissingTypes(this.missingTypes);
        }
        return attributesNumber;
    }
    
    public int generateMethodInfoAttributes(final MethodBinding methodBinding, final AnnotationMethodDeclaration declaration) {
        int attributesNumber = this.generateMethodInfoAttributes(methodBinding);
        final int attributeOffset = this.contentsOffset;
        if ((declaration.modifiers & 0x20000) != 0x0) {
            attributesNumber += this.generateAnnotationDefaultAttribute(declaration, attributeOffset);
        }
        return attributesNumber;
    }
    
    public void generateMethodInfoHeader(final MethodBinding methodBinding) {
        this.generateMethodInfoHeader(methodBinding, methodBinding.modifiers);
    }
    
    public void generateMethodInfoHeader(final MethodBinding methodBinding, int accessFlags) {
        ++this.methodCount;
        if (this.contentsOffset + 10 >= this.contents.length) {
            this.resizeContents(10);
        }
        if (this.targetJDK < 3211264L) {
            accessFlags &= 0xFFFFEF7F;
        }
        if ((methodBinding.tagBits & 0x200L) != 0x0L) {
            accessFlags &= 0xFFFFFFFD;
        }
        this.contents[this.contentsOffset++] = (byte)(accessFlags >> 8);
        this.contents[this.contentsOffset++] = (byte)accessFlags;
        final int nameIndex = this.constantPool.literalIndex(methodBinding.selector);
        this.contents[this.contentsOffset++] = (byte)(nameIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)nameIndex;
        final int descriptorIndex = this.constantPool.literalIndex(methodBinding.signature(this));
        this.contents[this.contentsOffset++] = (byte)(descriptorIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)descriptorIndex;
    }
    
    public void addSyntheticDeserializeLambda(final SyntheticMethodBinding methodBinding, final SyntheticMethodBinding[] syntheticMethodBindings) {
        this.generateMethodInfoHeader(methodBinding);
        int methodAttributeOffset = this.contentsOffset;
        int attributeNumber = this.generateMethodInfoAttributes(methodBinding);
        final int codeAttributeOffset = this.contentsOffset;
        ++attributeNumber;
        this.generateCodeAttributeHeader();
        this.codeStream.init(this);
        this.codeStream.generateSyntheticBodyForDeserializeLambda(methodBinding, syntheticMethodBindings);
        final int code_length = this.codeStream.position;
        if (code_length > 65535) {
            this.referenceBinding.scope.problemReporter().bytecodeExceeds64KLimit(methodBinding, this.referenceBinding.sourceStart(), this.referenceBinding.sourceEnd());
        }
        this.completeCodeAttributeForSyntheticMethod(methodBinding, codeAttributeOffset, ((SourceTypeBinding)methodBinding.declaringClass).scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions());
        if ((this.produceAttributes & 0x40) != 0x0) {
            attributeNumber += this.generateMethodParameters(methodBinding);
        }
        this.contents[methodAttributeOffset++] = (byte)(attributeNumber >> 8);
        this.contents[methodAttributeOffset] = (byte)attributeNumber;
    }
    
    public void generateMethodInfoHeaderForClinit() {
        ++this.methodCount;
        if (this.contentsOffset + 10 >= this.contents.length) {
            this.resizeContents(10);
        }
        this.contents[this.contentsOffset++] = 0;
        this.contents[this.contentsOffset++] = 8;
        final int nameIndex = this.constantPool.literalIndex(ConstantPool.Clinit);
        this.contents[this.contentsOffset++] = (byte)(nameIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)nameIndex;
        final int descriptorIndex = this.constantPool.literalIndex(ConstantPool.ClinitSignature);
        this.contents[this.contentsOffset++] = (byte)(descriptorIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)descriptorIndex;
        this.contents[this.contentsOffset++] = 0;
        this.contents[this.contentsOffset++] = 1;
    }
    
    public void generateMissingAbstractMethods(final MethodDeclaration[] methodDeclarations, final CompilationResult compilationResult) {
        if (methodDeclarations != null) {
            final TypeDeclaration currentDeclaration = this.referenceBinding.scope.referenceContext;
            final int typeDeclarationSourceStart = currentDeclaration.sourceStart();
            final int typeDeclarationSourceEnd = currentDeclaration.sourceEnd();
            for (int i = 0, max = methodDeclarations.length; i < max; ++i) {
                final MethodDeclaration methodDeclaration = methodDeclarations[i];
                final MethodBinding methodBinding = methodDeclaration.binding;
                final String readableName = new String(methodBinding.readableName());
                final CategorizedProblem[] problems = compilationResult.problems;
                for (int problemsCount = compilationResult.problemCount, j = 0; j < problemsCount; ++j) {
                    final CategorizedProblem problem = problems[j];
                    if (problem != null && problem.getID() == 67109264 && problem.getMessage().indexOf(readableName) != -1 && problem.getSourceStart() >= typeDeclarationSourceStart && problem.getSourceEnd() <= typeDeclarationSourceEnd) {
                        this.addMissingAbstractProblemMethod(methodDeclaration, methodBinding, problem, compilationResult);
                    }
                }
            }
        }
    }
    
    private void generateMissingTypesAttribute() {
        final int initialSize = this.missingTypes.size();
        final int[] missingTypesIndexes = new int[initialSize];
        int numberOfMissingTypes = 0;
        if (initialSize > 1) {
            Collections.sort(this.missingTypes, new Comparator() {
                @Override
                public int compare(final Object o1, final Object o2) {
                    final TypeBinding typeBinding1 = (TypeBinding)o1;
                    final TypeBinding typeBinding2 = (TypeBinding)o2;
                    return CharOperation.compareTo(typeBinding1.constantPoolName(), typeBinding2.constantPoolName());
                }
            });
        }
        int previousIndex = 0;
        for (int i = 0; i < initialSize; ++i) {
            final int missingTypeIndex = this.constantPool.literalIndexForType(this.missingTypes.get(i));
            if (previousIndex != missingTypeIndex) {
                previousIndex = missingTypeIndex;
                missingTypesIndexes[numberOfMissingTypes++] = missingTypeIndex;
            }
        }
        final int attributeLength = numberOfMissingTypes * 2 + 2;
        if (this.contentsOffset + attributeLength + 6 >= this.contents.length) {
            this.resizeContents(attributeLength + 6);
        }
        final int missingTypesNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.MissingTypesName);
        this.contents[this.contentsOffset++] = (byte)(missingTypesNameIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)missingTypesNameIndex;
        this.contents[this.contentsOffset++] = (byte)(attributeLength >> 24);
        this.contents[this.contentsOffset++] = (byte)(attributeLength >> 16);
        this.contents[this.contentsOffset++] = (byte)(attributeLength >> 8);
        this.contents[this.contentsOffset++] = (byte)attributeLength;
        this.contents[this.contentsOffset++] = (byte)(numberOfMissingTypes >> 8);
        this.contents[this.contentsOffset++] = (byte)numberOfMissingTypes;
        for (final int missingTypeIndex2 : missingTypesIndexes) {
            this.contents[this.contentsOffset++] = (byte)(missingTypeIndex2 >> 8);
            this.contents[this.contentsOffset++] = (byte)missingTypeIndex2;
        }
    }
    
    private boolean jdk16packageInfoAnnotation(final long annotationMask, final long targetMask) {
        return this.targetJDK <= 3276800L && targetMask == 8796093022208L && annotationMask != 0L && (annotationMask & 0x80000000000L) == 0x0L;
    }
    
    private int generateRuntimeAnnotations(final Annotation[] annotations, final long targetMask) {
        int attributesNumber = 0;
        final int length = annotations.length;
        int visibleAnnotationsCounter = 0;
        int invisibleAnnotationsCounter = 0;
        for (int i = 0; i < length; ++i) {
            final Annotation annotation;
            if ((annotation = annotations[i].getPersistibleAnnotation()) != null) {
                final long annotationMask = (annotation.resolvedType != null) ? (annotation.resolvedType.getAnnotationTagBits() & 0x600FF800000000L) : 0L;
                if (annotationMask == 0L || (annotationMask & targetMask) != 0x0L || this.jdk16packageInfoAnnotation(annotationMask, targetMask)) {
                    if (annotation.isRuntimeInvisible() || annotation.isRuntimeTypeInvisible()) {
                        ++invisibleAnnotationsCounter;
                    }
                    else if (annotation.isRuntimeVisible() || annotation.isRuntimeTypeVisible()) {
                        ++visibleAnnotationsCounter;
                    }
                }
            }
        }
        int annotationAttributeOffset = this.contentsOffset;
        int constantPOffset = this.constantPool.currentOffset;
        int constantPoolIndex = this.constantPool.currentIndex;
        if (invisibleAnnotationsCounter != 0) {
            if (this.contentsOffset + 10 >= this.contents.length) {
                this.resizeContents(10);
            }
            final int runtimeInvisibleAnnotationsAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.RuntimeInvisibleAnnotationsName);
            this.contents[this.contentsOffset++] = (byte)(runtimeInvisibleAnnotationsAttributeNameIndex >> 8);
            this.contents[this.contentsOffset++] = (byte)runtimeInvisibleAnnotationsAttributeNameIndex;
            int attributeLengthOffset = this.contentsOffset;
            this.contentsOffset += 4;
            int annotationsLengthOffset = this.contentsOffset;
            this.contentsOffset += 2;
            int counter = 0;
            for (int j = 0; j < length && invisibleAnnotationsCounter != 0; ++j) {
                final Annotation annotation2;
                if ((annotation2 = annotations[j].getPersistibleAnnotation()) != null) {
                    final long annotationMask2 = (annotation2.resolvedType != null) ? (annotation2.resolvedType.getAnnotationTagBits() & 0x600FF800000000L) : 0L;
                    if (annotationMask2 == 0L || (annotationMask2 & targetMask) != 0x0L || this.jdk16packageInfoAnnotation(annotationMask2, targetMask)) {
                        if (annotation2.isRuntimeInvisible() || annotation2.isRuntimeTypeInvisible()) {
                            final int currentAnnotationOffset = this.contentsOffset;
                            this.generateAnnotation(annotation2, currentAnnotationOffset);
                            --invisibleAnnotationsCounter;
                            if (this.contentsOffset != currentAnnotationOffset) {
                                ++counter;
                            }
                        }
                    }
                }
            }
            if (counter != 0) {
                this.contents[annotationsLengthOffset++] = (byte)(counter >> 8);
                this.contents[annotationsLengthOffset++] = (byte)counter;
                final int attributeLength = this.contentsOffset - attributeLengthOffset - 4;
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 24);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 16);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 8);
                this.contents[attributeLengthOffset++] = (byte)attributeLength;
                ++attributesNumber;
            }
            else {
                this.contentsOffset = annotationAttributeOffset;
                this.constantPool.resetForAttributeName(AttributeNamesConstants.RuntimeInvisibleAnnotationsName, constantPoolIndex, constantPOffset);
            }
        }
        annotationAttributeOffset = this.contentsOffset;
        constantPOffset = this.constantPool.currentOffset;
        constantPoolIndex = this.constantPool.currentIndex;
        if (visibleAnnotationsCounter != 0) {
            if (this.contentsOffset + 10 >= this.contents.length) {
                this.resizeContents(10);
            }
            final int runtimeVisibleAnnotationsAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.RuntimeVisibleAnnotationsName);
            this.contents[this.contentsOffset++] = (byte)(runtimeVisibleAnnotationsAttributeNameIndex >> 8);
            this.contents[this.contentsOffset++] = (byte)runtimeVisibleAnnotationsAttributeNameIndex;
            int attributeLengthOffset = this.contentsOffset;
            this.contentsOffset += 4;
            int annotationsLengthOffset = this.contentsOffset;
            this.contentsOffset += 2;
            int counter = 0;
            for (int j = 0; j < length && visibleAnnotationsCounter != 0; ++j) {
                final Annotation annotation2;
                if ((annotation2 = annotations[j].getPersistibleAnnotation()) != null) {
                    final long annotationMask2 = (annotation2.resolvedType != null) ? (annotation2.resolvedType.getAnnotationTagBits() & 0x600FF800000000L) : 0L;
                    if (annotationMask2 == 0L || (annotationMask2 & targetMask) != 0x0L || this.jdk16packageInfoAnnotation(annotationMask2, targetMask)) {
                        if (annotation2.isRuntimeVisible() || annotation2.isRuntimeTypeVisible()) {
                            --visibleAnnotationsCounter;
                            final int currentAnnotationOffset = this.contentsOffset;
                            this.generateAnnotation(annotation2, currentAnnotationOffset);
                            if (this.contentsOffset != currentAnnotationOffset) {
                                ++counter;
                            }
                        }
                    }
                }
            }
            if (counter != 0) {
                this.contents[annotationsLengthOffset++] = (byte)(counter >> 8);
                this.contents[annotationsLengthOffset++] = (byte)counter;
                final int attributeLength = this.contentsOffset - attributeLengthOffset - 4;
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 24);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 16);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 8);
                this.contents[attributeLengthOffset++] = (byte)attributeLength;
                ++attributesNumber;
            }
            else {
                this.contentsOffset = annotationAttributeOffset;
                this.constantPool.resetForAttributeName(AttributeNamesConstants.RuntimeVisibleAnnotationsName, constantPoolIndex, constantPOffset);
            }
        }
        return attributesNumber;
    }
    
    private int generateRuntimeAnnotationsForParameters(final Argument[] arguments) {
        final int argumentsLength = arguments.length;
        int invisibleParametersAnnotationsCounter = 0;
        int visibleParametersAnnotationsCounter = 0;
        final int[][] annotationsCounters = new int[argumentsLength][2];
        for (int i = 0; i < argumentsLength; ++i) {
            final Argument argument = arguments[i];
            final Annotation[] annotations = argument.annotations;
            if (annotations != null) {
                for (int j = 0, max2 = annotations.length; j < max2; ++j) {
                    final Annotation annotation;
                    if ((annotation = annotations[j].getPersistibleAnnotation()) != null) {
                        final long annotationMask = (annotation.resolvedType != null) ? (annotation.resolvedType.getAnnotationTagBits() & 0x600FF800000000L) : 0L;
                        if (annotationMask == 0L || (annotationMask & 0x8000000000L) != 0x0L) {
                            if (annotation.isRuntimeInvisible()) {
                                final int[] array = annotationsCounters[i];
                                final int n = 1;
                                ++array[n];
                                ++invisibleParametersAnnotationsCounter;
                            }
                            else if (annotation.isRuntimeVisible()) {
                                final int[] array2 = annotationsCounters[i];
                                final int n2 = 0;
                                ++array2[n2];
                                ++visibleParametersAnnotationsCounter;
                            }
                        }
                    }
                }
            }
        }
        int attributesNumber = 0;
        final int annotationAttributeOffset = this.contentsOffset;
        if (invisibleParametersAnnotationsCounter != 0) {
            int globalCounter = 0;
            if (this.contentsOffset + 7 >= this.contents.length) {
                this.resizeContents(7);
            }
            final int attributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.RuntimeInvisibleParameterAnnotationsName);
            this.contents[this.contentsOffset++] = (byte)(attributeNameIndex >> 8);
            this.contents[this.contentsOffset++] = (byte)attributeNameIndex;
            int attributeLengthOffset = this.contentsOffset;
            this.contentsOffset += 4;
            this.contents[this.contentsOffset++] = (byte)argumentsLength;
            for (int k = 0; k < argumentsLength; ++k) {
                if (this.contentsOffset + 2 >= this.contents.length) {
                    this.resizeContents(2);
                }
                if (invisibleParametersAnnotationsCounter == 0) {
                    this.contents[this.contentsOffset++] = 0;
                    this.contents[this.contentsOffset++] = 0;
                }
                else {
                    final int numberOfInvisibleAnnotations = annotationsCounters[k][1];
                    int invisibleAnnotationsOffset = this.contentsOffset;
                    this.contentsOffset += 2;
                    int counter = 0;
                    if (numberOfInvisibleAnnotations != 0) {
                        final Argument argument2 = arguments[k];
                        final Annotation[] annotations2 = argument2.annotations;
                        for (int l = 0, max3 = annotations2.length; l < max3; ++l) {
                            final Annotation annotation2;
                            if ((annotation2 = annotations2[l].getPersistibleAnnotation()) != null) {
                                final long annotationMask2 = (annotation2.resolvedType != null) ? (annotation2.resolvedType.getAnnotationTagBits() & 0x600FF800000000L) : 0L;
                                if (annotationMask2 == 0L || (annotationMask2 & 0x8000000000L) != 0x0L) {
                                    if (annotation2.isRuntimeInvisible()) {
                                        final int currentAnnotationOffset = this.contentsOffset;
                                        this.generateAnnotation(annotation2, currentAnnotationOffset);
                                        if (this.contentsOffset != currentAnnotationOffset) {
                                            ++counter;
                                            ++globalCounter;
                                        }
                                        --invisibleParametersAnnotationsCounter;
                                    }
                                }
                            }
                        }
                    }
                    this.contents[invisibleAnnotationsOffset++] = (byte)(counter >> 8);
                    this.contents[invisibleAnnotationsOffset] = (byte)counter;
                }
            }
            if (globalCounter != 0) {
                final int attributeLength = this.contentsOffset - attributeLengthOffset - 4;
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 24);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 16);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 8);
                this.contents[attributeLengthOffset++] = (byte)attributeLength;
                ++attributesNumber;
            }
            else {
                this.contentsOffset = annotationAttributeOffset;
            }
        }
        if (visibleParametersAnnotationsCounter != 0) {
            int globalCounter = 0;
            if (this.contentsOffset + 7 >= this.contents.length) {
                this.resizeContents(7);
            }
            final int attributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.RuntimeVisibleParameterAnnotationsName);
            this.contents[this.contentsOffset++] = (byte)(attributeNameIndex >> 8);
            this.contents[this.contentsOffset++] = (byte)attributeNameIndex;
            int attributeLengthOffset = this.contentsOffset;
            this.contentsOffset += 4;
            this.contents[this.contentsOffset++] = (byte)argumentsLength;
            for (int k = 0; k < argumentsLength; ++k) {
                if (this.contentsOffset + 2 >= this.contents.length) {
                    this.resizeContents(2);
                }
                if (visibleParametersAnnotationsCounter == 0) {
                    this.contents[this.contentsOffset++] = 0;
                    this.contents[this.contentsOffset++] = 0;
                }
                else {
                    final int numberOfVisibleAnnotations = annotationsCounters[k][0];
                    int visibleAnnotationsOffset = this.contentsOffset;
                    this.contentsOffset += 2;
                    int counter = 0;
                    if (numberOfVisibleAnnotations != 0) {
                        final Argument argument2 = arguments[k];
                        final Annotation[] annotations2 = argument2.annotations;
                        for (int l = 0, max3 = annotations2.length; l < max3; ++l) {
                            final Annotation annotation2;
                            if ((annotation2 = annotations2[l].getPersistibleAnnotation()) != null) {
                                final long annotationMask2 = (annotation2.resolvedType != null) ? (annotation2.resolvedType.getAnnotationTagBits() & 0x600FF800000000L) : 0L;
                                if (annotationMask2 == 0L || (annotationMask2 & 0x8000000000L) != 0x0L) {
                                    if (annotation2.isRuntimeVisible()) {
                                        final int currentAnnotationOffset = this.contentsOffset;
                                        this.generateAnnotation(annotation2, currentAnnotationOffset);
                                        if (this.contentsOffset != currentAnnotationOffset) {
                                            ++counter;
                                            ++globalCounter;
                                        }
                                        --visibleParametersAnnotationsCounter;
                                    }
                                }
                            }
                        }
                    }
                    this.contents[visibleAnnotationsOffset++] = (byte)(counter >> 8);
                    this.contents[visibleAnnotationsOffset] = (byte)counter;
                }
            }
            if (globalCounter != 0) {
                final int attributeLength = this.contentsOffset - attributeLengthOffset - 4;
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 24);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 16);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 8);
                this.contents[attributeLengthOffset++] = (byte)attributeLength;
                ++attributesNumber;
            }
            else {
                this.contentsOffset = annotationAttributeOffset;
            }
        }
        return attributesNumber;
    }
    
    private int generateRuntimeTypeAnnotations(final AnnotationContext[] annotationContexts, final int visibleTypeAnnotationsNumber, final int invisibleTypeAnnotationsNumber) {
        int attributesNumber = 0;
        final int length = annotationContexts.length;
        int visibleTypeAnnotationsCounter = visibleTypeAnnotationsNumber;
        int invisibleTypeAnnotationsCounter = invisibleTypeAnnotationsNumber;
        int annotationAttributeOffset = this.contentsOffset;
        int constantPOffset = this.constantPool.currentOffset;
        int constantPoolIndex = this.constantPool.currentIndex;
        if (invisibleTypeAnnotationsCounter != 0) {
            if (this.contentsOffset + 10 >= this.contents.length) {
                this.resizeContents(10);
            }
            final int runtimeInvisibleAnnotationsAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.RuntimeInvisibleTypeAnnotationsName);
            this.contents[this.contentsOffset++] = (byte)(runtimeInvisibleAnnotationsAttributeNameIndex >> 8);
            this.contents[this.contentsOffset++] = (byte)runtimeInvisibleAnnotationsAttributeNameIndex;
            int attributeLengthOffset = this.contentsOffset;
            this.contentsOffset += 4;
            int annotationsLengthOffset = this.contentsOffset;
            this.contentsOffset += 2;
            int counter = 0;
            for (int i = 0; i < length && invisibleTypeAnnotationsCounter != 0; ++i) {
                final AnnotationContext annotationContext = annotationContexts[i];
                if ((annotationContext.visibility & 0x2) != 0x0) {
                    final int currentAnnotationOffset = this.contentsOffset;
                    this.generateTypeAnnotation(annotationContext, currentAnnotationOffset);
                    --invisibleTypeAnnotationsCounter;
                    if (this.contentsOffset != currentAnnotationOffset) {
                        ++counter;
                    }
                }
            }
            if (counter != 0) {
                this.contents[annotationsLengthOffset++] = (byte)(counter >> 8);
                this.contents[annotationsLengthOffset++] = (byte)counter;
                final int attributeLength = this.contentsOffset - attributeLengthOffset - 4;
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 24);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 16);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 8);
                this.contents[attributeLengthOffset++] = (byte)attributeLength;
                ++attributesNumber;
            }
            else {
                this.contentsOffset = annotationAttributeOffset;
                this.constantPool.resetForAttributeName(AttributeNamesConstants.RuntimeInvisibleTypeAnnotationsName, constantPoolIndex, constantPOffset);
            }
        }
        annotationAttributeOffset = this.contentsOffset;
        constantPOffset = this.constantPool.currentOffset;
        constantPoolIndex = this.constantPool.currentIndex;
        if (visibleTypeAnnotationsCounter != 0) {
            if (this.contentsOffset + 10 >= this.contents.length) {
                this.resizeContents(10);
            }
            final int runtimeVisibleAnnotationsAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.RuntimeVisibleTypeAnnotationsName);
            this.contents[this.contentsOffset++] = (byte)(runtimeVisibleAnnotationsAttributeNameIndex >> 8);
            this.contents[this.contentsOffset++] = (byte)runtimeVisibleAnnotationsAttributeNameIndex;
            int attributeLengthOffset = this.contentsOffset;
            this.contentsOffset += 4;
            int annotationsLengthOffset = this.contentsOffset;
            this.contentsOffset += 2;
            int counter = 0;
            for (int i = 0; i < length && visibleTypeAnnotationsCounter != 0; ++i) {
                final AnnotationContext annotationContext = annotationContexts[i];
                if ((annotationContext.visibility & 0x1) != 0x0) {
                    --visibleTypeAnnotationsCounter;
                    final int currentAnnotationOffset = this.contentsOffset;
                    this.generateTypeAnnotation(annotationContext, currentAnnotationOffset);
                    if (this.contentsOffset != currentAnnotationOffset) {
                        ++counter;
                    }
                }
            }
            if (counter != 0) {
                this.contents[annotationsLengthOffset++] = (byte)(counter >> 8);
                this.contents[annotationsLengthOffset++] = (byte)counter;
                final int attributeLength = this.contentsOffset - attributeLengthOffset - 4;
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 24);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 16);
                this.contents[attributeLengthOffset++] = (byte)(attributeLength >> 8);
                this.contents[attributeLengthOffset++] = (byte)attributeLength;
                ++attributesNumber;
            }
            else {
                this.contentsOffset = annotationAttributeOffset;
                this.constantPool.resetForAttributeName(AttributeNamesConstants.RuntimeVisibleTypeAnnotationsName, constantPoolIndex, constantPOffset);
            }
        }
        return attributesNumber;
    }
    
    private int generateMethodParameters(final MethodBinding binding) {
        int initialContentsOffset = this.contentsOffset;
        int length = 0;
        AbstractMethodDeclaration methodDeclaration = binding.sourceMethod();
        final boolean isConstructor = binding.isConstructor();
        TypeBinding[] targetParameters = binding.parameters;
        final ReferenceBinding declaringClass = binding.declaringClass;
        if (declaringClass.isEnum()) {
            if (isConstructor) {
                length = this.writeArgumentName(ConstantPool.EnumName, 4096, length);
                length = this.writeArgumentName(ConstantPool.EnumOrdinal, 4096, length);
            }
            else if (binding instanceof SyntheticMethodBinding && CharOperation.equals(ConstantPool.ValueOf, binding.selector)) {
                length = this.writeArgumentName(ConstantPool.Name, 32768, length);
                targetParameters = Binding.NO_PARAMETERS;
            }
        }
        final boolean needSynthetics = isConstructor && declaringClass.isNestedType();
        if (needSynthetics) {
            final boolean anonymousWithLocalSuper = declaringClass.isAnonymousType() && declaringClass.superclass().isLocalType();
            final boolean anonymousWithNestedSuper = declaringClass.isAnonymousType() && declaringClass.superclass().isNestedType();
            final boolean isImplicitlyDeclared = (!declaringClass.isPrivate() || declaringClass.isAnonymousType()) && !anonymousWithLocalSuper;
            final ReferenceBinding[] syntheticArgumentTypes = declaringClass.syntheticEnclosingInstanceTypes();
            if (syntheticArgumentTypes != null) {
                for (int i = 0, count = syntheticArgumentTypes.length; i < count; ++i) {
                    final boolean couldForwardToMandated = !anonymousWithNestedSuper || declaringClass.superclass().enclosingType().equals(syntheticArgumentTypes[i]);
                    final int modifier = (couldForwardToMandated && isImplicitlyDeclared) ? 32768 : 4096;
                    final char[] name = CharOperation.concat(TypeConstants.SYNTHETIC_ENCLOSING_INSTANCE_PREFIX, String.valueOf(i).toCharArray());
                    length = this.writeArgumentName(name, modifier | 0x10, length);
                }
            }
            if (binding instanceof SyntheticMethodBinding) {
                targetParameters = ((SyntheticMethodBinding)binding).targetMethod.parameters;
                methodDeclaration = ((SyntheticMethodBinding)binding).targetMethod.sourceMethod();
            }
        }
        if (targetParameters != Binding.NO_PARAMETERS) {
            Argument[] arguments = null;
            if (methodDeclaration != null && methodDeclaration.arguments != null) {
                arguments = methodDeclaration.arguments;
            }
            else if (binding.sourceLambda() != null) {
                arguments = binding.sourceLambda().arguments;
            }
            int j = 0;
            final int max = targetParameters.length;
            final int argumentsLength = (arguments != null) ? arguments.length : 0;
            while (j < max) {
                if (argumentsLength > j && arguments[j] != null) {
                    final Argument argument = arguments[j];
                    length = this.writeArgumentName(argument.name, argument.binding.modifiers, length);
                }
                else {
                    length = this.writeArgumentName(null, 4096, length);
                }
                ++j;
            }
        }
        if (needSynthetics) {
            final SyntheticArgumentBinding[] syntheticOuterArguments = declaringClass.syntheticOuterLocalVariables();
            for (int count2 = (syntheticOuterArguments == null) ? 0 : syntheticOuterArguments.length, k = 0; k < count2; ++k) {
                length = this.writeArgumentName(syntheticOuterArguments[k].name, syntheticOuterArguments[k].modifiers | 0x1000, length);
            }
            for (int k = targetParameters.length, extraLength = binding.parameters.length; k < extraLength; ++k) {
                final TypeBinding parameter = binding.parameters[k];
                length = this.writeArgumentName(parameter.constantPoolName(), 4096, length);
            }
        }
        if (length > 0) {
            final int attributeLength = 1 + 4 * length;
            if (this.contentsOffset + 6 + attributeLength >= this.contents.length) {
                this.resizeContents(6 + attributeLength);
            }
            final int methodParametersNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.MethodParametersName);
            this.contents[initialContentsOffset++] = (byte)(methodParametersNameIndex >> 8);
            this.contents[initialContentsOffset++] = (byte)methodParametersNameIndex;
            this.contents[initialContentsOffset++] = (byte)(attributeLength >> 24);
            this.contents[initialContentsOffset++] = (byte)(attributeLength >> 16);
            this.contents[initialContentsOffset++] = (byte)(attributeLength >> 8);
            this.contents[initialContentsOffset++] = (byte)attributeLength;
            this.contents[initialContentsOffset++] = (byte)length;
            return 1;
        }
        return 0;
    }
    
    private int writeArgumentName(final char[] name, final int modifiers, final int oldLength) {
        int ensureRoomForBytes = 4;
        if (oldLength == 0) {
            ensureRoomForBytes += 7;
            this.contentsOffset += 7;
        }
        if (this.contentsOffset + ensureRoomForBytes > this.contents.length) {
            this.resizeContents(ensureRoomForBytes);
        }
        final int parameterNameIndex = (name == null) ? 0 : this.constantPool.literalIndex(name);
        this.contents[this.contentsOffset++] = (byte)(parameterNameIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)parameterNameIndex;
        final int flags = modifiers & 0x9010;
        this.contents[this.contentsOffset++] = (byte)(flags >> 8);
        this.contents[this.contentsOffset++] = (byte)flags;
        return oldLength + 1;
    }
    
    private int generateSignatureAttribute(final char[] genericSignature) {
        int localContentsOffset = this.contentsOffset;
        if (localContentsOffset + 8 >= this.contents.length) {
            this.resizeContents(8);
        }
        final int signatureAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.SignatureName);
        this.contents[localContentsOffset++] = (byte)(signatureAttributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)signatureAttributeNameIndex;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 2;
        final int signatureIndex = this.constantPool.literalIndex(genericSignature);
        this.contents[localContentsOffset++] = (byte)(signatureIndex >> 8);
        this.contents[localContentsOffset++] = (byte)signatureIndex;
        this.contentsOffset = localContentsOffset;
        return 1;
    }
    
    private int generateSourceAttribute(final String fullFileName) {
        int localContentsOffset = this.contentsOffset;
        if (localContentsOffset + 8 >= this.contents.length) {
            this.resizeContents(8);
        }
        final int sourceAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.SourceName);
        this.contents[localContentsOffset++] = (byte)(sourceAttributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)sourceAttributeNameIndex;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 2;
        final int fileNameIndex = this.constantPool.literalIndex(fullFileName.toCharArray());
        this.contents[localContentsOffset++] = (byte)(fileNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)fileNameIndex;
        this.contentsOffset = localContentsOffset;
        return 1;
    }
    
    private int generateStackMapAttribute(final MethodBinding methodBinding, final int code_length, final int codeAttributeOffset, final int max_locals, final boolean isClinit) {
        int attributesNumber = 0;
        int localContentsOffset = this.contentsOffset;
        final StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
        stackMapFrameCodeStream.removeFramePosition(code_length);
        if (stackMapFrameCodeStream.hasFramePositions()) {
            final Map frames = new HashMap();
            final List realFrames = this.traverse(isClinit ? null : methodBinding, max_locals, this.contents, codeAttributeOffset + 14, code_length, frames, isClinit);
            int numberOfFrames = realFrames.size();
            if (numberOfFrames > 1) {
                final int stackMapTableAttributeOffset = localContentsOffset;
                if (localContentsOffset + 8 >= this.contents.length) {
                    this.resizeContents(8);
                }
                final int stackMapAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.StackMapName);
                this.contents[localContentsOffset++] = (byte)(stackMapAttributeNameIndex >> 8);
                this.contents[localContentsOffset++] = (byte)stackMapAttributeNameIndex;
                int stackMapAttributeLengthOffset = localContentsOffset;
                localContentsOffset += 4;
                if (localContentsOffset + 4 >= this.contents.length) {
                    this.resizeContents(4);
                }
                int numberOfFramesOffset = localContentsOffset;
                localContentsOffset += 2;
                if (localContentsOffset + 2 >= this.contents.length) {
                    this.resizeContents(2);
                }
                StackMapFrame currentFrame = realFrames.get(0);
                for (int j = 1; j < numberOfFrames; ++j) {
                    currentFrame = realFrames.get(j);
                    final int frameOffset = currentFrame.pc;
                    if (localContentsOffset + 5 >= this.contents.length) {
                        this.resizeContents(5);
                    }
                    this.contents[localContentsOffset++] = (byte)(frameOffset >> 8);
                    this.contents[localContentsOffset++] = (byte)frameOffset;
                    int numberOfLocalOffset = localContentsOffset;
                    localContentsOffset += 2;
                    int numberOfLocalEntries = 0;
                    final int numberOfLocals = currentFrame.getNumberOfLocals();
                    int numberOfEntries = 0;
                    for (int localsLength = (currentFrame.locals == null) ? 0 : currentFrame.locals.length, i = 0; i < localsLength && numberOfLocalEntries < numberOfLocals; ++i) {
                        if (localContentsOffset + 3 >= this.contents.length) {
                            this.resizeContents(3);
                        }
                        final VerificationTypeInfo info = currentFrame.locals[i];
                        if (info == null) {
                            this.contents[localContentsOffset++] = 0;
                        }
                        else {
                            Label_0642: {
                                switch (info.id()) {
                                    case 2:
                                    case 3:
                                    case 4:
                                    case 5:
                                    case 10: {
                                        this.contents[localContentsOffset++] = 1;
                                        break;
                                    }
                                    case 9: {
                                        this.contents[localContentsOffset++] = 2;
                                        break;
                                    }
                                    case 7: {
                                        this.contents[localContentsOffset++] = 4;
                                        ++i;
                                        break;
                                    }
                                    case 8: {
                                        this.contents[localContentsOffset++] = 3;
                                        ++i;
                                        break;
                                    }
                                    case 12: {
                                        this.contents[localContentsOffset++] = 5;
                                        break;
                                    }
                                    default: {
                                        this.contents[localContentsOffset++] = (byte)info.tag;
                                        switch (info.tag) {
                                            case 8: {
                                                final int offset = info.offset;
                                                this.contents[localContentsOffset++] = (byte)(offset >> 8);
                                                this.contents[localContentsOffset++] = (byte)offset;
                                                break Label_0642;
                                            }
                                            case 7: {
                                                final int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
                                                this.contents[localContentsOffset++] = (byte)(indexForType >> 8);
                                                this.contents[localContentsOffset++] = (byte)indexForType;
                                                break Label_0642;
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                            ++numberOfLocalEntries;
                        }
                        ++numberOfEntries;
                    }
                    if (localContentsOffset + 4 >= this.contents.length) {
                        this.resizeContents(4);
                    }
                    this.contents[numberOfLocalOffset++] = (byte)(numberOfEntries >> 8);
                    this.contents[numberOfLocalOffset] = (byte)numberOfEntries;
                    final int numberOfStackItems = currentFrame.numberOfStackItems;
                    this.contents[localContentsOffset++] = (byte)(numberOfStackItems >> 8);
                    this.contents[localContentsOffset++] = (byte)numberOfStackItems;
                    for (int k = 0; k < numberOfStackItems; ++k) {
                        if (localContentsOffset + 3 >= this.contents.length) {
                            this.resizeContents(3);
                        }
                        final VerificationTypeInfo info2 = currentFrame.stackItems[k];
                        if (info2 == null) {
                            this.contents[localContentsOffset++] = 0;
                        }
                        else {
                            switch (info2.id()) {
                                case 2:
                                case 3:
                                case 4:
                                case 5:
                                case 10: {
                                    this.contents[localContentsOffset++] = 1;
                                    break;
                                }
                                case 9: {
                                    this.contents[localContentsOffset++] = 2;
                                    break;
                                }
                                case 7: {
                                    this.contents[localContentsOffset++] = 4;
                                    break;
                                }
                                case 8: {
                                    this.contents[localContentsOffset++] = 3;
                                    break;
                                }
                                case 12: {
                                    this.contents[localContentsOffset++] = 5;
                                    break;
                                }
                                default: {
                                    this.contents[localContentsOffset++] = (byte)info2.tag;
                                    switch (info2.tag) {
                                        case 8: {
                                            final int offset2 = info2.offset;
                                            this.contents[localContentsOffset++] = (byte)(offset2 >> 8);
                                            this.contents[localContentsOffset++] = (byte)offset2;
                                            continue;
                                        }
                                        case 7: {
                                            final int indexForType2 = this.constantPool.literalIndexForType(info2.constantPoolName());
                                            this.contents[localContentsOffset++] = (byte)(indexForType2 >> 8);
                                            this.contents[localContentsOffset++] = (byte)indexForType2;
                                            continue;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                if (--numberOfFrames != 0) {
                    this.contents[numberOfFramesOffset++] = (byte)(numberOfFrames >> 8);
                    this.contents[numberOfFramesOffset] = (byte)numberOfFrames;
                    final int attributeLength = localContentsOffset - stackMapAttributeLengthOffset - 4;
                    this.contents[stackMapAttributeLengthOffset++] = (byte)(attributeLength >> 24);
                    this.contents[stackMapAttributeLengthOffset++] = (byte)(attributeLength >> 16);
                    this.contents[stackMapAttributeLengthOffset++] = (byte)(attributeLength >> 8);
                    this.contents[stackMapAttributeLengthOffset] = (byte)attributeLength;
                    ++attributesNumber;
                }
                else {
                    localContentsOffset = stackMapTableAttributeOffset;
                }
            }
        }
        this.contentsOffset = localContentsOffset;
        return attributesNumber;
    }
    
    private int generateStackMapTableAttribute(final MethodBinding methodBinding, final int code_length, final int codeAttributeOffset, final int max_locals, final boolean isClinit) {
        int attributesNumber = 0;
        int localContentsOffset = this.contentsOffset;
        final StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
        stackMapFrameCodeStream.removeFramePosition(code_length);
        if (stackMapFrameCodeStream.hasFramePositions()) {
            final Map frames = new HashMap();
            final List realFrames = this.traverse(isClinit ? null : methodBinding, max_locals, this.contents, codeAttributeOffset + 14, code_length, frames, isClinit);
            int numberOfFrames = realFrames.size();
            if (numberOfFrames > 1) {
                final int stackMapTableAttributeOffset = localContentsOffset;
                if (localContentsOffset + 8 >= this.contents.length) {
                    this.resizeContents(8);
                }
                final int stackMapTableAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.StackMapTableName);
                this.contents[localContentsOffset++] = (byte)(stackMapTableAttributeNameIndex >> 8);
                this.contents[localContentsOffset++] = (byte)stackMapTableAttributeNameIndex;
                int stackMapTableAttributeLengthOffset = localContentsOffset;
                localContentsOffset += 4;
                if (localContentsOffset + 4 >= this.contents.length) {
                    this.resizeContents(4);
                }
                int numberOfFramesOffset = localContentsOffset;
                localContentsOffset += 2;
                if (localContentsOffset + 2 >= this.contents.length) {
                    this.resizeContents(2);
                }
                StackMapFrame currentFrame = realFrames.get(0);
                StackMapFrame prevFrame = null;
                for (int j = 1; j < numberOfFrames; ++j) {
                    prevFrame = currentFrame;
                    currentFrame = realFrames.get(j);
                    final int offsetDelta = currentFrame.getOffsetDelta(prevFrame);
                    switch (currentFrame.getFrameType(prevFrame)) {
                        case 2: {
                            if (localContentsOffset + 3 >= this.contents.length) {
                                this.resizeContents(3);
                            }
                            int numberOfDifferentLocals = currentFrame.numberOfDifferentLocals(prevFrame);
                            this.contents[localContentsOffset++] = (byte)(251 + numberOfDifferentLocals);
                            this.contents[localContentsOffset++] = (byte)(offsetDelta >> 8);
                            this.contents[localContentsOffset++] = (byte)offsetDelta;
                            final int index = currentFrame.getIndexOfDifferentLocals(numberOfDifferentLocals);
                            final int numberOfLocals = currentFrame.getNumberOfLocals();
                            for (int i = index; i < currentFrame.locals.length; ++i) {
                                if (numberOfDifferentLocals <= 0) {
                                    break;
                                }
                                if (localContentsOffset + 6 >= this.contents.length) {
                                    this.resizeContents(6);
                                }
                                final VerificationTypeInfo info = currentFrame.locals[i];
                                if (info == null) {
                                    this.contents[localContentsOffset++] = 0;
                                }
                                else {
                                    Label_0706: {
                                        switch (info.id()) {
                                            case 2:
                                            case 3:
                                            case 4:
                                            case 5:
                                            case 10: {
                                                this.contents[localContentsOffset++] = 1;
                                                break;
                                            }
                                            case 9: {
                                                this.contents[localContentsOffset++] = 2;
                                                break;
                                            }
                                            case 7: {
                                                this.contents[localContentsOffset++] = 4;
                                                ++i;
                                                break;
                                            }
                                            case 8: {
                                                this.contents[localContentsOffset++] = 3;
                                                ++i;
                                                break;
                                            }
                                            case 12: {
                                                this.contents[localContentsOffset++] = 5;
                                                break;
                                            }
                                            default: {
                                                this.contents[localContentsOffset++] = (byte)info.tag;
                                                switch (info.tag) {
                                                    case 8: {
                                                        final int offset = info.offset;
                                                        this.contents[localContentsOffset++] = (byte)(offset >> 8);
                                                        this.contents[localContentsOffset++] = (byte)offset;
                                                        break Label_0706;
                                                    }
                                                    case 7: {
                                                        final int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
                                                        this.contents[localContentsOffset++] = (byte)(indexForType >> 8);
                                                        this.contents[localContentsOffset++] = (byte)indexForType;
                                                        break Label_0706;
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }
                                    --numberOfDifferentLocals;
                                }
                            }
                            break;
                        }
                        case 0: {
                            if (localContentsOffset + 1 >= this.contents.length) {
                                this.resizeContents(1);
                            }
                            this.contents[localContentsOffset++] = (byte)offsetDelta;
                            break;
                        }
                        case 3: {
                            if (localContentsOffset + 3 >= this.contents.length) {
                                this.resizeContents(3);
                            }
                            this.contents[localContentsOffset++] = -5;
                            this.contents[localContentsOffset++] = (byte)(offsetDelta >> 8);
                            this.contents[localContentsOffset++] = (byte)offsetDelta;
                            break;
                        }
                        case 1: {
                            if (localContentsOffset + 3 >= this.contents.length) {
                                this.resizeContents(3);
                            }
                            final int numberOfDifferentLocals = -currentFrame.numberOfDifferentLocals(prevFrame);
                            this.contents[localContentsOffset++] = (byte)(251 - numberOfDifferentLocals);
                            this.contents[localContentsOffset++] = (byte)(offsetDelta >> 8);
                            this.contents[localContentsOffset++] = (byte)offsetDelta;
                            break;
                        }
                        case 5: {
                            if (localContentsOffset + 4 >= this.contents.length) {
                                this.resizeContents(4);
                            }
                            this.contents[localContentsOffset++] = (byte)(offsetDelta + 64);
                            if (currentFrame.stackItems[0] == null) {
                                this.contents[localContentsOffset++] = 0;
                                break;
                            }
                            switch (currentFrame.stackItems[0].id()) {
                                case 2:
                                case 3:
                                case 4:
                                case 5:
                                case 10: {
                                    this.contents[localContentsOffset++] = 1;
                                    continue;
                                }
                                case 9: {
                                    this.contents[localContentsOffset++] = 2;
                                    continue;
                                }
                                case 7: {
                                    this.contents[localContentsOffset++] = 4;
                                    continue;
                                }
                                case 8: {
                                    this.contents[localContentsOffset++] = 3;
                                    continue;
                                }
                                case 12: {
                                    this.contents[localContentsOffset++] = 5;
                                    continue;
                                }
                                default: {
                                    final VerificationTypeInfo info2 = currentFrame.stackItems[0];
                                    final byte tag = (byte)info2.tag;
                                    switch (this.contents[localContentsOffset++] = tag) {
                                        case 8: {
                                            final int offset = info2.offset;
                                            this.contents[localContentsOffset++] = (byte)(offset >> 8);
                                            this.contents[localContentsOffset++] = (byte)offset;
                                            break;
                                        }
                                        case 7: {
                                            final int indexForType = this.constantPool.literalIndexForType(info2.constantPoolName());
                                            this.contents[localContentsOffset++] = (byte)(indexForType >> 8);
                                            this.contents[localContentsOffset++] = (byte)indexForType;
                                            break;
                                        }
                                    }
                                    continue;
                                }
                            }
                            break;
                        }
                        case 6: {
                            if (localContentsOffset + 6 >= this.contents.length) {
                                this.resizeContents(6);
                            }
                            this.contents[localContentsOffset++] = -9;
                            this.contents[localContentsOffset++] = (byte)(offsetDelta >> 8);
                            this.contents[localContentsOffset++] = (byte)offsetDelta;
                            if (currentFrame.stackItems[0] == null) {
                                this.contents[localContentsOffset++] = 0;
                                break;
                            }
                            switch (currentFrame.stackItems[0].id()) {
                                case 2:
                                case 3:
                                case 4:
                                case 5:
                                case 10: {
                                    this.contents[localContentsOffset++] = 1;
                                    continue;
                                }
                                case 9: {
                                    this.contents[localContentsOffset++] = 2;
                                    continue;
                                }
                                case 7: {
                                    this.contents[localContentsOffset++] = 4;
                                    continue;
                                }
                                case 8: {
                                    this.contents[localContentsOffset++] = 3;
                                    continue;
                                }
                                case 12: {
                                    this.contents[localContentsOffset++] = 5;
                                    continue;
                                }
                                default: {
                                    final VerificationTypeInfo info2 = currentFrame.stackItems[0];
                                    final byte tag = (byte)info2.tag;
                                    switch (this.contents[localContentsOffset++] = tag) {
                                        case 8: {
                                            final int offset = info2.offset;
                                            this.contents[localContentsOffset++] = (byte)(offset >> 8);
                                            this.contents[localContentsOffset++] = (byte)offset;
                                            break;
                                        }
                                        case 7: {
                                            final int indexForType = this.constantPool.literalIndexForType(info2.constantPoolName());
                                            this.contents[localContentsOffset++] = (byte)(indexForType >> 8);
                                            this.contents[localContentsOffset++] = (byte)indexForType;
                                            break;
                                        }
                                    }
                                    continue;
                                }
                            }
                            break;
                        }
                        default: {
                            if (localContentsOffset + 5 >= this.contents.length) {
                                this.resizeContents(5);
                            }
                            this.contents[localContentsOffset++] = -1;
                            this.contents[localContentsOffset++] = (byte)(offsetDelta >> 8);
                            this.contents[localContentsOffset++] = (byte)offsetDelta;
                            int numberOfLocalOffset = localContentsOffset;
                            localContentsOffset += 2;
                            int numberOfLocalEntries = 0;
                            final int numberOfLocals = currentFrame.getNumberOfLocals();
                            int numberOfEntries = 0;
                            for (int localsLength = (currentFrame.locals == null) ? 0 : currentFrame.locals.length, k = 0; k < localsLength && numberOfLocalEntries < numberOfLocals; ++k) {
                                if (localContentsOffset + 3 >= this.contents.length) {
                                    this.resizeContents(3);
                                }
                                final VerificationTypeInfo info3 = currentFrame.locals[k];
                                if (info3 == null) {
                                    this.contents[localContentsOffset++] = 0;
                                }
                                else {
                                    Label_2010: {
                                        switch (info3.id()) {
                                            case 2:
                                            case 3:
                                            case 4:
                                            case 5:
                                            case 10: {
                                                this.contents[localContentsOffset++] = 1;
                                                break;
                                            }
                                            case 9: {
                                                this.contents[localContentsOffset++] = 2;
                                                break;
                                            }
                                            case 7: {
                                                this.contents[localContentsOffset++] = 4;
                                                ++k;
                                                break;
                                            }
                                            case 8: {
                                                this.contents[localContentsOffset++] = 3;
                                                ++k;
                                                break;
                                            }
                                            case 12: {
                                                this.contents[localContentsOffset++] = 5;
                                                break;
                                            }
                                            default: {
                                                this.contents[localContentsOffset++] = (byte)info3.tag;
                                                switch (info3.tag) {
                                                    case 8: {
                                                        final int offset2 = info3.offset;
                                                        this.contents[localContentsOffset++] = (byte)(offset2 >> 8);
                                                        this.contents[localContentsOffset++] = (byte)offset2;
                                                        break Label_2010;
                                                    }
                                                    case 7: {
                                                        final int indexForType2 = this.constantPool.literalIndexForType(info3.constantPoolName());
                                                        this.contents[localContentsOffset++] = (byte)(indexForType2 >> 8);
                                                        this.contents[localContentsOffset++] = (byte)indexForType2;
                                                        break Label_2010;
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }
                                    ++numberOfLocalEntries;
                                }
                                ++numberOfEntries;
                            }
                            if (localContentsOffset + 4 >= this.contents.length) {
                                this.resizeContents(4);
                            }
                            this.contents[numberOfLocalOffset++] = (byte)(numberOfEntries >> 8);
                            this.contents[numberOfLocalOffset] = (byte)numberOfEntries;
                            final int numberOfStackItems = currentFrame.numberOfStackItems;
                            this.contents[localContentsOffset++] = (byte)(numberOfStackItems >> 8);
                            this.contents[localContentsOffset++] = (byte)numberOfStackItems;
                            for (int l = 0; l < numberOfStackItems; ++l) {
                                if (localContentsOffset + 3 >= this.contents.length) {
                                    this.resizeContents(3);
                                }
                                final VerificationTypeInfo info4 = currentFrame.stackItems[l];
                                if (info4 == null) {
                                    this.contents[localContentsOffset++] = 0;
                                }
                                else {
                                    switch (info4.id()) {
                                        case 2:
                                        case 3:
                                        case 4:
                                        case 5:
                                        case 10: {
                                            this.contents[localContentsOffset++] = 1;
                                            break;
                                        }
                                        case 9: {
                                            this.contents[localContentsOffset++] = 2;
                                            break;
                                        }
                                        case 7: {
                                            this.contents[localContentsOffset++] = 4;
                                            break;
                                        }
                                        case 8: {
                                            this.contents[localContentsOffset++] = 3;
                                            break;
                                        }
                                        case 12: {
                                            this.contents[localContentsOffset++] = 5;
                                            break;
                                        }
                                        default: {
                                            this.contents[localContentsOffset++] = (byte)info4.tag;
                                            switch (info4.tag) {
                                                case 8: {
                                                    final int offset3 = info4.offset;
                                                    this.contents[localContentsOffset++] = (byte)(offset3 >> 8);
                                                    this.contents[localContentsOffset++] = (byte)offset3;
                                                    continue;
                                                }
                                                case 7: {
                                                    final int indexForType3 = this.constantPool.literalIndexForType(info4.constantPoolName());
                                                    this.contents[localContentsOffset++] = (byte)(indexForType3 >> 8);
                                                    this.contents[localContentsOffset++] = (byte)indexForType3;
                                                    continue;
                                                }
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
                if (--numberOfFrames != 0) {
                    this.contents[numberOfFramesOffset++] = (byte)(numberOfFrames >> 8);
                    this.contents[numberOfFramesOffset] = (byte)numberOfFrames;
                    final int attributeLength = localContentsOffset - stackMapTableAttributeLengthOffset - 4;
                    this.contents[stackMapTableAttributeLengthOffset++] = (byte)(attributeLength >> 24);
                    this.contents[stackMapTableAttributeLengthOffset++] = (byte)(attributeLength >> 16);
                    this.contents[stackMapTableAttributeLengthOffset++] = (byte)(attributeLength >> 8);
                    this.contents[stackMapTableAttributeLengthOffset] = (byte)attributeLength;
                    ++attributesNumber;
                }
                else {
                    localContentsOffset = stackMapTableAttributeOffset;
                }
            }
        }
        this.contentsOffset = localContentsOffset;
        return attributesNumber;
    }
    
    private int generateSyntheticAttribute() {
        int localContentsOffset = this.contentsOffset;
        if (localContentsOffset + 6 >= this.contents.length) {
            this.resizeContents(6);
        }
        final int syntheticAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.SyntheticName);
        this.contents[localContentsOffset++] = (byte)(syntheticAttributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)syntheticAttributeNameIndex;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contentsOffset = localContentsOffset;
        return 1;
    }
    
    private void generateTypeAnnotation(final AnnotationContext annotationContext, final int currentOffset) {
        final Annotation annotation = annotationContext.annotation.getPersistibleAnnotation();
        if (annotation == null || annotation.resolvedType == null) {
            return;
        }
        final int targetType = annotationContext.targetType;
        final int[] locations = Annotation.getLocations(annotationContext.typeReference, annotationContext.annotation);
        if (this.contentsOffset + 5 >= this.contents.length) {
            this.resizeContents(5);
        }
        this.contents[this.contentsOffset++] = (byte)targetType;
        this.dumpTargetTypeContents(targetType, annotationContext);
        this.dumpLocations(locations);
        this.generateAnnotation(annotation, currentOffset);
    }
    
    private int generateTypeAnnotationAttributeForTypeDeclaration() {
        final TypeDeclaration typeDeclaration = this.referenceBinding.scope.referenceContext;
        if ((typeDeclaration.bits & 0x100000) == 0x0) {
            return 0;
        }
        int attributesNumber = 0;
        int visibleTypeAnnotationsCounter = 0;
        int invisibleTypeAnnotationsCounter = 0;
        final TypeReference superclass = typeDeclaration.superclass;
        final List allTypeAnnotationContexts = new ArrayList();
        if (superclass != null && (superclass.bits & 0x100000) != 0x0) {
            superclass.getAllAnnotationContexts(16, -1, allTypeAnnotationContexts);
        }
        final TypeReference[] superInterfaces = typeDeclaration.superInterfaces;
        if (superInterfaces != null) {
            for (int i = 0; i < superInterfaces.length; ++i) {
                final TypeReference superInterface = superInterfaces[i];
                if ((superInterface.bits & 0x100000) != 0x0) {
                    superInterface.getAllAnnotationContexts(16, i, allTypeAnnotationContexts);
                }
            }
        }
        final TypeParameter[] typeParameters = typeDeclaration.typeParameters;
        if (typeParameters != null) {
            for (int j = 0, max = typeParameters.length; j < max; ++j) {
                final TypeParameter typeParameter = typeParameters[j];
                if ((typeParameter.bits & 0x100000) != 0x0) {
                    typeParameter.getAllAnnotationContexts(0, j, allTypeAnnotationContexts);
                }
            }
        }
        final int size = allTypeAnnotationContexts.size();
        if (size != 0) {
            final AnnotationContext[] allTypeAnnotationContextsArray = new AnnotationContext[size];
            allTypeAnnotationContexts.toArray(allTypeAnnotationContextsArray);
            for (int k = 0, max2 = allTypeAnnotationContextsArray.length; k < max2; ++k) {
                final AnnotationContext annotationContext = allTypeAnnotationContextsArray[k];
                if ((annotationContext.visibility & 0x2) != 0x0) {
                    ++invisibleTypeAnnotationsCounter;
                    allTypeAnnotationContexts.add(annotationContext);
                }
                else {
                    ++visibleTypeAnnotationsCounter;
                    allTypeAnnotationContexts.add(annotationContext);
                }
            }
            attributesNumber += this.generateRuntimeTypeAnnotations(allTypeAnnotationContextsArray, visibleTypeAnnotationsCounter, invisibleTypeAnnotationsCounter);
        }
        return attributesNumber;
    }
    
    private int generateVarargsAttribute() {
        int localContentsOffset = this.contentsOffset;
        if (localContentsOffset + 6 >= this.contents.length) {
            this.resizeContents(6);
        }
        final int varargsAttributeNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.VarargsName);
        this.contents[localContentsOffset++] = (byte)(varargsAttributeNameIndex >> 8);
        this.contents[localContentsOffset++] = (byte)varargsAttributeNameIndex;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contents[localContentsOffset++] = 0;
        this.contentsOffset = localContentsOffset;
        return 1;
    }
    
    public byte[] getBytes() {
        if (this.bytes == null) {
            this.bytes = new byte[this.headerOffset + this.contentsOffset];
            System.arraycopy(this.header, 0, this.bytes, 0, this.headerOffset);
            System.arraycopy(this.contents, 0, this.bytes, this.headerOffset, this.contentsOffset);
        }
        return this.bytes;
    }
    
    public char[][] getCompoundName() {
        return CharOperation.splitOn('/', this.fileName());
    }
    
    private int getParametersCount(final char[] methodSignature) {
        int i = CharOperation.indexOf('(', methodSignature);
        ++i;
        char currentCharacter = methodSignature[i];
        if (currentCharacter == ')') {
            return 0;
        }
        int result = 0;
        while (true) {
            currentCharacter = methodSignature[i];
            if (currentCharacter == ')') {
                return result;
            }
            switch (currentCharacter) {
                case '[': {
                    final int scanType = this.scanType(methodSignature, i + 1);
                    ++result;
                    i = scanType + 1;
                    continue;
                }
                case 'L': {
                    final int scanType = CharOperation.indexOf(';', methodSignature, i + 1);
                    ++result;
                    i = scanType + 1;
                    continue;
                }
                case 'B':
                case 'C':
                case 'D':
                case 'F':
                case 'I':
                case 'J':
                case 'S':
                case 'Z': {
                    ++result;
                    ++i;
                    continue;
                }
                default: {
                    throw new IllegalArgumentException("Invalid starting type character : " + currentCharacter);
                }
            }
        }
    }
    
    private char[] getReturnType(final char[] methodSignature) {
        final int paren = CharOperation.lastIndexOf(')', methodSignature);
        return CharOperation.subarray(methodSignature, paren + 1, methodSignature.length);
    }
    
    private final int i4At(final byte[] reference, final int relativeOffset, final int structOffset) {
        int position = relativeOffset + structOffset;
        return ((reference[position++] & 0xFF) << 24) + ((reference[position++] & 0xFF) << 16) + ((reference[position++] & 0xFF) << 8) + (reference[position] & 0xFF);
    }
    
    protected void initByteArrays() {
        final int members = this.referenceBinding.methods().length + this.referenceBinding.fields().length;
        this.header = new byte[1500];
        this.contents = new byte[(members < 15) ? 400 : 1500];
    }
    
    public void initialize(final SourceTypeBinding aType, final ClassFile parentClassFile, final boolean createProblemType) {
        this.header[this.headerOffset++] = -54;
        this.header[this.headerOffset++] = -2;
        this.header[this.headerOffset++] = -70;
        this.header[this.headerOffset++] = -66;
        final long targetVersion = this.targetJDK;
        this.header[this.headerOffset++] = (byte)(targetVersion >> 8);
        this.header[this.headerOffset++] = (byte)(targetVersion >> 0);
        this.header[this.headerOffset++] = (byte)(targetVersion >> 24);
        this.header[this.headerOffset++] = (byte)(targetVersion >> 16);
        this.constantPoolOffset = this.headerOffset;
        this.headerOffset += 2;
        this.constantPool.initialize(this);
        int accessFlags = aType.getAccessFlags();
        if (aType.isPrivate()) {
            accessFlags &= 0xFFFFFFFE;
        }
        if (aType.isProtected()) {
            accessFlags |= 0x1;
        }
        accessFlags &= 0xFFFFF6D1;
        if (!aType.isInterface()) {
            accessFlags |= 0x20;
        }
        if (aType.isAnonymousType()) {
            accessFlags &= 0xFFFFFFEF;
        }
        final int finalAbstract = 1040;
        if ((accessFlags & finalAbstract) == finalAbstract) {
            accessFlags &= ~finalAbstract;
        }
        this.enclosingClassFile = parentClassFile;
        this.contents[this.contentsOffset++] = (byte)(accessFlags >> 8);
        this.contents[this.contentsOffset++] = (byte)accessFlags;
        final int classNameIndex = this.constantPool.literalIndexForType(aType);
        this.contents[this.contentsOffset++] = (byte)(classNameIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)classNameIndex;
        int superclassNameIndex;
        if (aType.isInterface()) {
            superclassNameIndex = this.constantPool.literalIndexForType(ConstantPool.JavaLangObjectConstantPoolName);
        }
        else if (aType.superclass != null) {
            if ((aType.superclass.tagBits & 0x80L) != 0x0L) {
                superclassNameIndex = this.constantPool.literalIndexForType(ConstantPool.JavaLangObjectConstantPoolName);
            }
            else {
                superclassNameIndex = this.constantPool.literalIndexForType(aType.superclass);
            }
        }
        else {
            superclassNameIndex = 0;
        }
        this.contents[this.contentsOffset++] = (byte)(superclassNameIndex >> 8);
        this.contents[this.contentsOffset++] = (byte)superclassNameIndex;
        final ReferenceBinding[] superInterfacesBinding = aType.superInterfaces();
        final int interfacesCount = superInterfacesBinding.length;
        int interfacesCountPosition = this.contentsOffset;
        this.contentsOffset += 2;
        int interfaceCounter = 0;
        for (final ReferenceBinding binding : superInterfacesBinding) {
            if ((binding.tagBits & 0x80L) == 0x0L) {
                ++interfaceCounter;
                final int interfaceIndex = this.constantPool.literalIndexForType(binding);
                this.contents[this.contentsOffset++] = (byte)(interfaceIndex >> 8);
                this.contents[this.contentsOffset++] = (byte)interfaceIndex;
            }
        }
        this.contents[interfacesCountPosition++] = (byte)(interfaceCounter >> 8);
        this.contents[interfacesCountPosition] = (byte)interfaceCounter;
        this.creatingProblemType = createProblemType;
        this.codeStream.maxFieldCount = aType.scope.outerMostClassScope().referenceType().maxFieldCount;
    }
    
    private void initializeDefaultLocals(final StackMapFrame frame, final MethodBinding methodBinding, final int maxLocals, final int codeLength) {
        if (maxLocals != 0) {
            int resolvedPosition = 0;
            final boolean isConstructor = methodBinding.isConstructor();
            if (isConstructor || !methodBinding.isStatic()) {
                final LocalVariableBinding localVariableBinding = new LocalVariableBinding(ConstantPool.This, methodBinding.declaringClass, 0, false);
                localVariableBinding.resolvedPosition = 0;
                this.codeStream.record(localVariableBinding);
                localVariableBinding.recordInitializationStartPC(0);
                localVariableBinding.recordInitializationEndPC(codeLength);
                frame.putLocal(resolvedPosition, new VerificationTypeInfo(isConstructor ? 6 : 7, methodBinding.declaringClass));
                ++resolvedPosition;
            }
            if (isConstructor) {
                if (methodBinding.declaringClass.isEnum()) {
                    LocalVariableBinding localVariableBinding = new LocalVariableBinding(" name".toCharArray(), this.referenceBinding.scope.getJavaLangString(), 0, false);
                    localVariableBinding.resolvedPosition = resolvedPosition;
                    this.codeStream.record(localVariableBinding);
                    localVariableBinding.recordInitializationStartPC(0);
                    localVariableBinding.recordInitializationEndPC(codeLength);
                    frame.putLocal(resolvedPosition, new VerificationTypeInfo(11, ConstantPool.JavaLangStringConstantPoolName));
                    ++resolvedPosition;
                    localVariableBinding = new LocalVariableBinding(" ordinal".toCharArray(), TypeBinding.INT, 0, false);
                    localVariableBinding.resolvedPosition = resolvedPosition;
                    this.codeStream.record(localVariableBinding);
                    localVariableBinding.recordInitializationStartPC(0);
                    localVariableBinding.recordInitializationEndPC(codeLength);
                    frame.putLocal(resolvedPosition, new VerificationTypeInfo(TypeBinding.INT));
                    ++resolvedPosition;
                }
                if (methodBinding.declaringClass.isNestedType()) {
                    final ReferenceBinding[] enclosingInstanceTypes;
                    if ((enclosingInstanceTypes = methodBinding.declaringClass.syntheticEnclosingInstanceTypes()) != null) {
                        for (int i = 0, max = enclosingInstanceTypes.length; i < max; ++i) {
                            final LocalVariableBinding localVariableBinding2 = new LocalVariableBinding((" enclosingType" + i).toCharArray(), enclosingInstanceTypes[i], 0, false);
                            localVariableBinding2.resolvedPosition = resolvedPosition;
                            this.codeStream.record(localVariableBinding2);
                            localVariableBinding2.recordInitializationStartPC(0);
                            localVariableBinding2.recordInitializationEndPC(codeLength);
                            frame.putLocal(resolvedPosition, new VerificationTypeInfo(enclosingInstanceTypes[i]));
                            ++resolvedPosition;
                        }
                    }
                    final TypeBinding[] arguments;
                    if ((arguments = methodBinding.parameters) != null) {
                        for (int j = 0, max2 = arguments.length; j < max2; ++j) {
                            final TypeBinding typeBinding = arguments[j];
                            frame.putLocal(resolvedPosition, new VerificationTypeInfo(typeBinding));
                            switch (typeBinding.id) {
                                case 7:
                                case 8: {
                                    resolvedPosition += 2;
                                    break;
                                }
                                default: {
                                    ++resolvedPosition;
                                    break;
                                }
                            }
                        }
                    }
                    final SyntheticArgumentBinding[] syntheticArguments;
                    if ((syntheticArguments = methodBinding.declaringClass.syntheticOuterLocalVariables()) != null) {
                        for (int k = 0, max3 = syntheticArguments.length; k < max3; ++k) {
                            final TypeBinding typeBinding2 = syntheticArguments[k].type;
                            final LocalVariableBinding localVariableBinding3 = new LocalVariableBinding((" synthetic" + k).toCharArray(), typeBinding2, 0, false);
                            localVariableBinding3.resolvedPosition = resolvedPosition;
                            this.codeStream.record(localVariableBinding3);
                            localVariableBinding3.recordInitializationStartPC(0);
                            localVariableBinding3.recordInitializationEndPC(codeLength);
                            frame.putLocal(resolvedPosition, new VerificationTypeInfo(typeBinding2));
                            switch (typeBinding2.id) {
                                case 7:
                                case 8: {
                                    resolvedPosition += 2;
                                    break;
                                }
                                default: {
                                    ++resolvedPosition;
                                    break;
                                }
                            }
                        }
                    }
                }
                else {
                    final TypeBinding[] arguments2;
                    if ((arguments2 = methodBinding.parameters) != null) {
                        for (int i = 0, max = arguments2.length; i < max; ++i) {
                            final TypeBinding typeBinding3 = arguments2[i];
                            frame.putLocal(resolvedPosition, new VerificationTypeInfo(typeBinding3));
                            switch (typeBinding3.id) {
                                case 7:
                                case 8: {
                                    resolvedPosition += 2;
                                    break;
                                }
                                default: {
                                    ++resolvedPosition;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            else {
                final TypeBinding[] arguments2;
                if ((arguments2 = methodBinding.parameters) != null) {
                    for (int i = 0, max = arguments2.length; i < max; ++i) {
                        final TypeBinding typeBinding3 = arguments2[i];
                        final LocalVariableBinding localVariableBinding4 = new LocalVariableBinding((" synthetic" + i).toCharArray(), typeBinding3, 0, true);
                        localVariableBinding4.resolvedPosition = i;
                        this.codeStream.record(localVariableBinding4);
                        localVariableBinding4.recordInitializationStartPC(0);
                        localVariableBinding4.recordInitializationEndPC(codeLength);
                        frame.putLocal(resolvedPosition, new VerificationTypeInfo(typeBinding3));
                        switch (typeBinding3.id) {
                            case 7:
                            case 8: {
                                resolvedPosition += 2;
                                break;
                            }
                            default: {
                                ++resolvedPosition;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void initializeLocals(final boolean isStatic, final int currentPC, final StackMapFrame currentFrame) {
        final VerificationTypeInfo[] locals = currentFrame.locals;
        final int localsLength = locals.length;
        int i = 0;
        if (!isStatic) {
            i = 1;
        }
        while (i < localsLength) {
            locals[i] = null;
            ++i;
        }
        i = 0;
        for (int max = this.codeStream.allLocalsCounter; i < max; ++i) {
            final LocalVariableBinding localVariable = this.codeStream.locals[i];
            if (localVariable != null) {
                final int resolvedPosition = localVariable.resolvedPosition;
                final TypeBinding localVariableTypeBinding = localVariable.type;
                for (int j = 0; j < localVariable.initializationCount; ++j) {
                    final int startPC = localVariable.initializationPCs[j << 1];
                    final int endPC = localVariable.initializationPCs[(j << 1) + 1];
                    if (currentPC >= startPC) {
                        if (currentPC < endPC) {
                            if (currentFrame.locals[resolvedPosition] == null) {
                                currentFrame.locals[resolvedPosition] = new VerificationTypeInfo(localVariableTypeBinding);
                                break;
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
    
    public ClassFile outerMostEnclosingClassFile() {
        ClassFile current;
        for (current = this; current.enclosingClassFile != null; current = current.enclosingClassFile) {}
        return current;
    }
    
    public void recordInnerClasses(final TypeBinding binding) {
        this.recordInnerClasses(binding, false);
    }
    
    public void recordInnerClasses(final TypeBinding binding, final boolean onBottomForBug445231) {
        if (this.innerClassesBindings == null) {
            this.innerClassesBindings = new HashMap<TypeBinding, Boolean>(5);
        }
        final ReferenceBinding innerClass = (ReferenceBinding)binding;
        this.innerClassesBindings.put(innerClass.erasure().unannotated(), onBottomForBug445231);
        for (ReferenceBinding enclosingType = innerClass.enclosingType(); enclosingType != null && enclosingType.isNestedType(); enclosingType = enclosingType.enclosingType()) {
            this.innerClassesBindings.put(enclosingType.erasure().unannotated(), onBottomForBug445231);
        }
    }
    
    public int recordBootstrapMethod(final FunctionalExpression expression) {
        if (this.bootstrapMethods == null) {
            this.bootstrapMethods = new ArrayList();
        }
        if (expression instanceof ReferenceExpression) {
            for (int i = 0; i < this.bootstrapMethods.size(); ++i) {
                final FunctionalExpression fexp = this.bootstrapMethods.get(i);
                if (fexp.binding == expression.binding && TypeBinding.equalsEquals(fexp.expectedType(), expression.expectedType())) {
                    return expression.bootstrapMethodNumber = i;
                }
            }
        }
        this.bootstrapMethods.add(expression);
        return expression.bootstrapMethodNumber = this.bootstrapMethods.size() - 1;
    }
    
    public void reset(final SourceTypeBinding typeBinding) {
        final CompilerOptions options = typeBinding.scope.compilerOptions();
        this.referenceBinding = typeBinding;
        this.isNestedType = typeBinding.isNestedType();
        this.targetJDK = options.targetJDK;
        this.produceAttributes = options.produceDebugAttributes;
        if (this.targetJDK >= 3276800L) {
            this.produceAttributes |= 0x8;
            if (this.targetJDK >= 3407872L) {
                this.produceAttributes |= 0x20;
                if (options.produceMethodParameters) {
                    this.produceAttributes |= 0x40;
                }
            }
        }
        else if (this.targetJDK == 2949124L) {
            this.targetJDK = 2949123L;
            this.produceAttributes |= 0x10;
        }
        this.bytes = null;
        this.constantPool.reset();
        this.codeStream.reset(this);
        this.constantPoolOffset = 0;
        this.contentsOffset = 0;
        this.creatingProblemType = false;
        this.enclosingClassFile = null;
        this.headerOffset = 0;
        this.methodCount = 0;
        this.methodCountOffset = 0;
        if (this.innerClassesBindings != null) {
            this.innerClassesBindings.clear();
        }
        if (this.bootstrapMethods != null) {
            this.bootstrapMethods.clear();
        }
        this.missingTypes = null;
        this.visitedTypes = null;
    }
    
    private final void resizeContents(final int minimalSize) {
        int toAdd;
        final int length = toAdd = this.contents.length;
        if (toAdd < minimalSize) {
            toAdd = minimalSize;
        }
        System.arraycopy(this.contents, 0, this.contents = new byte[length + toAdd], 0, length);
    }
    
    private VerificationTypeInfo retrieveLocal(final int currentPC, final int resolvedPosition) {
        for (int i = 0, max = this.codeStream.allLocalsCounter; i < max; ++i) {
            final LocalVariableBinding localVariable = this.codeStream.locals[i];
            if (localVariable != null) {
                if (resolvedPosition == localVariable.resolvedPosition) {
                    for (int j = 0; j < localVariable.initializationCount; ++j) {
                        final int startPC = localVariable.initializationPCs[j << 1];
                        final int endPC = localVariable.initializationPCs[(j << 1) + 1];
                        if (currentPC >= startPC) {
                            if (currentPC < endPC) {
                                return new VerificationTypeInfo(localVariable.type);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private int scanType(final char[] methodSignature, final int index) {
        switch (methodSignature[index]) {
            case '[': {
                return this.scanType(methodSignature, index + 1);
            }
            case 'L': {
                return CharOperation.indexOf(';', methodSignature, index + 1);
            }
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'I':
            case 'J':
            case 'S':
            case 'Z': {
                return index;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    public void setForMethodInfos() {
        this.methodCountOffset = this.contentsOffset;
        this.contentsOffset += 2;
    }
    
    private List filterFakeFrames(final Set realJumpTargets, final Map frames, final int codeLength) {
        realJumpTargets.remove(codeLength);
        final List result = new ArrayList();
        for (final Integer jumpTarget : realJumpTargets) {
            final StackMapFrame frame = frames.get(jumpTarget);
            if (frame != null) {
                result.add(frame);
            }
        }
        Collections.sort((List<Object>)result, new Comparator() {
            @Override
            public int compare(final Object o1, final Object o2) {
                final StackMapFrame frame = (StackMapFrame)o1;
                final StackMapFrame frame2 = (StackMapFrame)o2;
                return frame.pc - frame2.pc;
            }
        });
        return result;
    }
    
    public List traverse(final MethodBinding methodBinding, final int maxLocals, final byte[] bytecodes, final int codeOffset, final int codeLength, final Map frames, final boolean isClinit) {
        final Set realJumpTarget = new HashSet();
        final StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
        final int[] framePositions = stackMapFrameCodeStream.getFramePositions();
        int pc = codeOffset;
        final int[] constantPoolOffsets = this.constantPool.offsets;
        final byte[] poolContents = this.constantPool.poolContent;
        int indexInFramePositions = 0;
        final int framePositionsLength = framePositions.length;
        int currentFramePosition = framePositions[0];
        int indexInStackDepthMarkers = 0;
        final StackMapFrameCodeStream.StackDepthMarker[] stackDepthMarkers = stackMapFrameCodeStream.getStackDepthMarkers();
        final int stackDepthMarkersLength = (stackDepthMarkers == null) ? 0 : stackDepthMarkers.length;
        boolean hasStackDepthMarkers = stackDepthMarkersLength != 0;
        StackMapFrameCodeStream.StackDepthMarker stackDepthMarker = null;
        if (hasStackDepthMarkers) {
            stackDepthMarker = stackDepthMarkers[0];
        }
        int indexInStackMarkers = 0;
        final StackMapFrameCodeStream.StackMarker[] stackMarkers = stackMapFrameCodeStream.getStackMarkers();
        final int stackMarkersLength = (stackMarkers == null) ? 0 : stackMarkers.length;
        boolean hasStackMarkers = stackMarkersLength != 0;
        StackMapFrameCodeStream.StackMarker stackMarker = null;
        if (hasStackMarkers) {
            stackMarker = stackMarkers[0];
        }
        int indexInExceptionMarkers = 0;
        final StackMapFrameCodeStream.ExceptionMarker[] exceptionMarkers = stackMapFrameCodeStream.getExceptionMarkers();
        final int exceptionsMarkersLength = (exceptionMarkers == null) ? 0 : exceptionMarkers.length;
        boolean hasExceptionMarkers = exceptionsMarkersLength != 0;
        StackMapFrameCodeStream.ExceptionMarker exceptionMarker = null;
        if (hasExceptionMarkers) {
            exceptionMarker = exceptionMarkers[0];
        }
        final StackMapFrame frame = new StackMapFrame(maxLocals);
        if (!isClinit) {
            this.initializeDefaultLocals(frame, methodBinding, maxLocals, codeLength);
        }
        frame.pc = -1;
        this.add(frames, frame.duplicate());
        this.addRealJumpTarget(realJumpTarget, -1);
        for (int i = 0, max = this.codeStream.exceptionLabelsCounter; i < max; ++i) {
            final ExceptionLabel exceptionLabel = this.codeStream.exceptionLabels[i];
            if (exceptionLabel != null) {
                this.addRealJumpTarget(realJumpTarget, exceptionLabel.position);
            }
        }
        do {
            final int currentPC = pc - codeOffset;
            if (hasStackMarkers && stackMarker.pc == currentPC) {
                final VerificationTypeInfo[] infos = frame.stackItems;
                final VerificationTypeInfo[] tempInfos = new VerificationTypeInfo[frame.numberOfStackItems];
                System.arraycopy(infos, 0, tempInfos, 0, frame.numberOfStackItems);
                stackMarker.setInfos(tempInfos);
            }
            else if (hasStackMarkers && stackMarker.destinationPC == currentPC) {
                final VerificationTypeInfo[] infos = stackMarker.infos;
                frame.stackItems = infos;
                frame.numberOfStackItems = infos.length;
                if (++indexInStackMarkers < stackMarkersLength) {
                    stackMarker = stackMarkers[indexInStackMarkers];
                }
                else {
                    hasStackMarkers = false;
                }
            }
            if (hasStackDepthMarkers && stackDepthMarker.pc == currentPC) {
                final TypeBinding typeBinding = stackDepthMarker.typeBinding;
                if (typeBinding != null) {
                    if (stackDepthMarker.delta > 0) {
                        frame.addStackItem(new VerificationTypeInfo(typeBinding));
                    }
                    else {
                        frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(typeBinding);
                    }
                }
                else {
                    final StackMapFrame stackMapFrame = frame;
                    --stackMapFrame.numberOfStackItems;
                }
                if (++indexInStackDepthMarkers < stackDepthMarkersLength) {
                    stackDepthMarker = stackDepthMarkers[indexInStackDepthMarkers];
                }
                else {
                    hasStackDepthMarkers = false;
                }
            }
            if (hasExceptionMarkers && exceptionMarker.pc == currentPC) {
                frame.numberOfStackItems = 0;
                frame.addStackItem(new VerificationTypeInfo(0, 7, exceptionMarker.constantPoolName));
                if (++indexInExceptionMarkers < exceptionsMarkersLength) {
                    exceptionMarker = exceptionMarkers[indexInExceptionMarkers];
                }
                else {
                    hasExceptionMarkers = false;
                }
            }
            if (currentFramePosition < currentPC) {
                do {
                    if (++indexInFramePositions < framePositionsLength) {
                        currentFramePosition = framePositions[indexInFramePositions];
                    }
                    else {
                        currentFramePosition = Integer.MAX_VALUE;
                    }
                } while (currentFramePosition < currentPC);
            }
            if (currentFramePosition == currentPC) {
                final StackMapFrame currentFrame = frame.duplicate();
                currentFrame.pc = currentPC;
                this.initializeLocals(isClinit || methodBinding.isStatic(), currentPC, currentFrame);
                this.add(frames, currentFrame);
                if (++indexInFramePositions < framePositionsLength) {
                    currentFramePosition = framePositions[indexInFramePositions];
                }
                else {
                    currentFramePosition = Integer.MAX_VALUE;
                }
            }
            byte opcode = (byte)this.u1At(bytecodes, 0, pc);
            switch (opcode) {
                case 0: {
                    ++pc;
                    continue;
                }
                case 1: {
                    frame.addStackItem(TypeBinding.NULL);
                    ++pc;
                    continue;
                }
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8: {
                    frame.addStackItem(TypeBinding.INT);
                    ++pc;
                    continue;
                }
                case 9:
                case 10: {
                    frame.addStackItem(TypeBinding.LONG);
                    ++pc;
                    continue;
                }
                case 11:
                case 12:
                case 13: {
                    frame.addStackItem(TypeBinding.FLOAT);
                    ++pc;
                    continue;
                }
                case 14:
                case 15: {
                    frame.addStackItem(TypeBinding.DOUBLE);
                    ++pc;
                    continue;
                }
                case 16: {
                    frame.addStackItem(TypeBinding.BYTE);
                    pc += 2;
                    continue;
                }
                case 17: {
                    frame.addStackItem(TypeBinding.SHORT);
                    pc += 3;
                    continue;
                }
                case 18: {
                    final int index = this.u1At(bytecodes, 1, pc);
                    switch (this.u1At(poolContents, 0, constantPoolOffsets[index])) {
                        case 8: {
                            frame.addStackItem(new VerificationTypeInfo(11, ConstantPool.JavaLangStringConstantPoolName));
                            break;
                        }
                        case 3: {
                            frame.addStackItem(TypeBinding.INT);
                            break;
                        }
                        case 4: {
                            frame.addStackItem(TypeBinding.FLOAT);
                            break;
                        }
                        case 7: {
                            frame.addStackItem(new VerificationTypeInfo(16, ConstantPool.JavaLangClassConstantPoolName));
                            break;
                        }
                    }
                    pc += 2;
                    continue;
                }
                case 19: {
                    final int index = this.u2At(bytecodes, 1, pc);
                    switch (this.u1At(poolContents, 0, constantPoolOffsets[index])) {
                        case 8: {
                            frame.addStackItem(new VerificationTypeInfo(11, ConstantPool.JavaLangStringConstantPoolName));
                            break;
                        }
                        case 3: {
                            frame.addStackItem(TypeBinding.INT);
                            break;
                        }
                        case 4: {
                            frame.addStackItem(TypeBinding.FLOAT);
                            break;
                        }
                        case 7: {
                            frame.addStackItem(new VerificationTypeInfo(16, ConstantPool.JavaLangClassConstantPoolName));
                            break;
                        }
                    }
                    pc += 3;
                    continue;
                }
                case 20: {
                    final int index = this.u2At(bytecodes, 1, pc);
                    switch (this.u1At(poolContents, 0, constantPoolOffsets[index])) {
                        case 6: {
                            frame.addStackItem(TypeBinding.DOUBLE);
                            break;
                        }
                        case 5: {
                            frame.addStackItem(TypeBinding.LONG);
                            break;
                        }
                    }
                    pc += 3;
                    continue;
                }
                case 21: {
                    frame.addStackItem(TypeBinding.INT);
                    pc += 2;
                    continue;
                }
                case 22: {
                    frame.addStackItem(TypeBinding.LONG);
                    pc += 2;
                    continue;
                }
                case 23: {
                    frame.addStackItem(TypeBinding.FLOAT);
                    pc += 2;
                    continue;
                }
                case 24: {
                    frame.addStackItem(TypeBinding.DOUBLE);
                    pc += 2;
                    continue;
                }
                case 25: {
                    final int index = this.u1At(bytecodes, 1, pc);
                    final VerificationTypeInfo localsN = this.retrieveLocal(currentPC, index);
                    frame.addStackItem(localsN);
                    pc += 2;
                    continue;
                }
                case 26:
                case 27:
                case 28:
                case 29: {
                    frame.addStackItem(TypeBinding.INT);
                    ++pc;
                    continue;
                }
                case 30:
                case 31:
                case 32:
                case 33: {
                    frame.addStackItem(TypeBinding.LONG);
                    ++pc;
                    continue;
                }
                case 34:
                case 35:
                case 36:
                case 37: {
                    frame.addStackItem(TypeBinding.FLOAT);
                    ++pc;
                    continue;
                }
                case 38:
                case 39:
                case 40:
                case 41: {
                    frame.addStackItem(TypeBinding.DOUBLE);
                    ++pc;
                    continue;
                }
                case 42: {
                    VerificationTypeInfo locals0 = frame.locals[0];
                    if (locals0 == null || locals0.tag != 6) {
                        locals0 = this.retrieveLocal(currentPC, 0);
                    }
                    frame.addStackItem(locals0);
                    ++pc;
                    continue;
                }
                case 43: {
                    final VerificationTypeInfo locals2 = this.retrieveLocal(currentPC, 1);
                    frame.addStackItem(locals2);
                    ++pc;
                    continue;
                }
                case 44: {
                    final VerificationTypeInfo locals3 = this.retrieveLocal(currentPC, 2);
                    frame.addStackItem(locals3);
                    ++pc;
                    continue;
                }
                case 45: {
                    final VerificationTypeInfo locals4 = this.retrieveLocal(currentPC, 3);
                    frame.addStackItem(locals4);
                    ++pc;
                    continue;
                }
                case 46: {
                    final StackMapFrame stackMapFrame2 = frame;
                    stackMapFrame2.numberOfStackItems -= 2;
                    frame.addStackItem(TypeBinding.INT);
                    ++pc;
                    continue;
                }
                case 47: {
                    final StackMapFrame stackMapFrame3 = frame;
                    stackMapFrame3.numberOfStackItems -= 2;
                    frame.addStackItem(TypeBinding.LONG);
                    ++pc;
                    continue;
                }
                case 48: {
                    final StackMapFrame stackMapFrame4 = frame;
                    stackMapFrame4.numberOfStackItems -= 2;
                    frame.addStackItem(TypeBinding.FLOAT);
                    ++pc;
                    continue;
                }
                case 49: {
                    final StackMapFrame stackMapFrame5 = frame;
                    stackMapFrame5.numberOfStackItems -= 2;
                    frame.addStackItem(TypeBinding.DOUBLE);
                    ++pc;
                    continue;
                }
                case 50: {
                    final StackMapFrame stackMapFrame6 = frame;
                    --stackMapFrame6.numberOfStackItems;
                    frame.replaceWithElementType();
                    ++pc;
                    continue;
                }
                case 51: {
                    final StackMapFrame stackMapFrame7 = frame;
                    stackMapFrame7.numberOfStackItems -= 2;
                    frame.addStackItem(TypeBinding.BYTE);
                    ++pc;
                    continue;
                }
                case 52: {
                    final StackMapFrame stackMapFrame8 = frame;
                    stackMapFrame8.numberOfStackItems -= 2;
                    frame.addStackItem(TypeBinding.CHAR);
                    ++pc;
                    continue;
                }
                case 53: {
                    final StackMapFrame stackMapFrame9 = frame;
                    stackMapFrame9.numberOfStackItems -= 2;
                    frame.addStackItem(TypeBinding.SHORT);
                    ++pc;
                    continue;
                }
                case 54:
                case 55:
                case 56:
                case 57: {
                    final StackMapFrame stackMapFrame10 = frame;
                    --stackMapFrame10.numberOfStackItems;
                    pc += 2;
                    continue;
                }
                case 58: {
                    final int index = this.u1At(bytecodes, 1, pc);
                    final StackMapFrame stackMapFrame11 = frame;
                    --stackMapFrame11.numberOfStackItems;
                    pc += 2;
                    continue;
                }
                case 75: {
                    frame.locals[0] = frame.stackItems[frame.numberOfStackItems - 1];
                    final StackMapFrame stackMapFrame12 = frame;
                    --stackMapFrame12.numberOfStackItems;
                    ++pc;
                    continue;
                }
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                case 72:
                case 73:
                case 74:
                case 76:
                case 77:
                case 78: {
                    final StackMapFrame stackMapFrame13 = frame;
                    --stackMapFrame13.numberOfStackItems;
                    ++pc;
                    continue;
                }
                case 79:
                case 80:
                case 81:
                case 82:
                case 83:
                case 84:
                case 85:
                case 86: {
                    final StackMapFrame stackMapFrame14 = frame;
                    stackMapFrame14.numberOfStackItems -= 3;
                    ++pc;
                    continue;
                }
                case 87: {
                    final StackMapFrame stackMapFrame15 = frame;
                    --stackMapFrame15.numberOfStackItems;
                    ++pc;
                    continue;
                }
                case 88: {
                    final int numberOfStackItems = frame.numberOfStackItems;
                    switch (frame.stackItems[numberOfStackItems - 1].id()) {
                        case 7:
                        case 8: {
                            final StackMapFrame stackMapFrame16 = frame;
                            --stackMapFrame16.numberOfStackItems;
                            break;
                        }
                        default: {
                            final StackMapFrame stackMapFrame17 = frame;
                            stackMapFrame17.numberOfStackItems -= 2;
                            break;
                        }
                    }
                    ++pc;
                    continue;
                }
                case 89: {
                    frame.addStackItem(frame.stackItems[frame.numberOfStackItems - 1]);
                    ++pc;
                    continue;
                }
                case 90: {
                    final VerificationTypeInfo info = frame.stackItems[frame.numberOfStackItems - 1];
                    final StackMapFrame stackMapFrame18 = frame;
                    --stackMapFrame18.numberOfStackItems;
                    final VerificationTypeInfo info2 = frame.stackItems[frame.numberOfStackItems - 1];
                    final StackMapFrame stackMapFrame19 = frame;
                    --stackMapFrame19.numberOfStackItems;
                    frame.addStackItem(info);
                    frame.addStackItem(info2);
                    frame.addStackItem(info);
                    ++pc;
                    continue;
                }
                case 91: {
                    final VerificationTypeInfo info = frame.stackItems[frame.numberOfStackItems - 1];
                    final StackMapFrame stackMapFrame20 = frame;
                    --stackMapFrame20.numberOfStackItems;
                    final VerificationTypeInfo info2 = frame.stackItems[frame.numberOfStackItems - 1];
                    final StackMapFrame stackMapFrame21 = frame;
                    --stackMapFrame21.numberOfStackItems;
                    switch (info2.id()) {
                        case 7:
                        case 8: {
                            frame.addStackItem(info);
                            frame.addStackItem(info2);
                            frame.addStackItem(info);
                            break;
                        }
                        default: {
                            final int numberOfStackItems = frame.numberOfStackItems;
                            final VerificationTypeInfo info3 = frame.stackItems[numberOfStackItems - 1];
                            final StackMapFrame stackMapFrame22 = frame;
                            --stackMapFrame22.numberOfStackItems;
                            frame.addStackItem(info);
                            frame.addStackItem(info3);
                            frame.addStackItem(info2);
                            frame.addStackItem(info);
                            break;
                        }
                    }
                    ++pc;
                    continue;
                }
                case 92: {
                    final VerificationTypeInfo info = frame.stackItems[frame.numberOfStackItems - 1];
                    final StackMapFrame stackMapFrame23 = frame;
                    --stackMapFrame23.numberOfStackItems;
                    switch (info.id()) {
                        case 7:
                        case 8: {
                            frame.addStackItem(info);
                            frame.addStackItem(info);
                            break;
                        }
                        default: {
                            final VerificationTypeInfo info2 = frame.stackItems[frame.numberOfStackItems - 1];
                            final StackMapFrame stackMapFrame24 = frame;
                            --stackMapFrame24.numberOfStackItems;
                            frame.addStackItem(info2);
                            frame.addStackItem(info);
                            frame.addStackItem(info2);
                            frame.addStackItem(info);
                            break;
                        }
                    }
                    ++pc;
                    continue;
                }
                case 93: {
                    final VerificationTypeInfo info = frame.stackItems[frame.numberOfStackItems - 1];
                    final StackMapFrame stackMapFrame25 = frame;
                    --stackMapFrame25.numberOfStackItems;
                    final VerificationTypeInfo info2 = frame.stackItems[frame.numberOfStackItems - 1];
                    final StackMapFrame stackMapFrame26 = frame;
                    --stackMapFrame26.numberOfStackItems;
                    switch (info.id()) {
                        case 7:
                        case 8: {
                            frame.addStackItem(info);
                            frame.addStackItem(info2);
                            frame.addStackItem(info);
                            break;
                        }
                        default: {
                            final VerificationTypeInfo info3 = frame.stackItems[frame.numberOfStackItems - 1];
                            final StackMapFrame stackMapFrame27 = frame;
                            --stackMapFrame27.numberOfStackItems;
                            frame.addStackItem(info2);
                            frame.addStackItem(info);
                            frame.addStackItem(info3);
                            frame.addStackItem(info2);
                            frame.addStackItem(info);
                            break;
                        }
                    }
                    ++pc;
                    continue;
                }
                case 94: {
                    int numberOfStackItems = frame.numberOfStackItems;
                    final VerificationTypeInfo info = frame.stackItems[numberOfStackItems - 1];
                    final StackMapFrame stackMapFrame28 = frame;
                    --stackMapFrame28.numberOfStackItems;
                    final VerificationTypeInfo info2 = frame.stackItems[frame.numberOfStackItems - 1];
                    final StackMapFrame stackMapFrame29 = frame;
                    --stackMapFrame29.numberOfStackItems;
                    Label_3790: {
                        switch (info.id()) {
                            case 7:
                            case 8: {
                                switch (info2.id()) {
                                    case 7:
                                    case 8: {
                                        frame.addStackItem(info);
                                        frame.addStackItem(info2);
                                        frame.addStackItem(info);
                                        break Label_3790;
                                    }
                                    default: {
                                        numberOfStackItems = frame.numberOfStackItems;
                                        final VerificationTypeInfo info3 = frame.stackItems[numberOfStackItems - 1];
                                        final StackMapFrame stackMapFrame30 = frame;
                                        --stackMapFrame30.numberOfStackItems;
                                        frame.addStackItem(info);
                                        frame.addStackItem(info3);
                                        frame.addStackItem(info2);
                                        frame.addStackItem(info);
                                        break Label_3790;
                                    }
                                }
                                break;
                            }
                            default: {
                                numberOfStackItems = frame.numberOfStackItems;
                                final VerificationTypeInfo info3 = frame.stackItems[numberOfStackItems - 1];
                                final StackMapFrame stackMapFrame31 = frame;
                                --stackMapFrame31.numberOfStackItems;
                                switch (info3.id()) {
                                    case 7:
                                    case 8: {
                                        frame.addStackItem(info2);
                                        frame.addStackItem(info);
                                        frame.addStackItem(info3);
                                        frame.addStackItem(info2);
                                        frame.addStackItem(info);
                                        break Label_3790;
                                    }
                                    default: {
                                        numberOfStackItems = frame.numberOfStackItems;
                                        final VerificationTypeInfo info4 = frame.stackItems[numberOfStackItems - 1];
                                        final StackMapFrame stackMapFrame32 = frame;
                                        --stackMapFrame32.numberOfStackItems;
                                        frame.addStackItem(info2);
                                        frame.addStackItem(info);
                                        frame.addStackItem(info4);
                                        frame.addStackItem(info3);
                                        frame.addStackItem(info2);
                                        frame.addStackItem(info);
                                        break Label_3790;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    ++pc;
                    continue;
                }
                case 95: {
                    final int numberOfStackItems = frame.numberOfStackItems;
                    final VerificationTypeInfo info = frame.stackItems[numberOfStackItems - 1];
                    final VerificationTypeInfo info2 = frame.stackItems[numberOfStackItems - 2];
                    frame.stackItems[numberOfStackItems - 1] = info2;
                    frame.stackItems[numberOfStackItems - 2] = info;
                    ++pc;
                    continue;
                }
                case Byte.MIN_VALUE:
                case -127:
                case -126:
                case -125:
                case 96:
                case 97:
                case 98:
                case 99:
                case 100:
                case 101:
                case 102:
                case 103:
                case 104:
                case 105:
                case 106:
                case 107:
                case 108:
                case 109:
                case 110:
                case 111:
                case 112:
                case 113:
                case 114:
                case 115:
                case 120:
                case 121:
                case 122:
                case 123:
                case 124:
                case 125:
                case 126:
                case Byte.MAX_VALUE: {
                    final StackMapFrame stackMapFrame33 = frame;
                    --stackMapFrame33.numberOfStackItems;
                    ++pc;
                    continue;
                }
                case 116:
                case 117:
                case 118:
                case 119: {
                    ++pc;
                    continue;
                }
                case -124: {
                    pc += 3;
                    continue;
                }
                case -123: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.LONG);
                    ++pc;
                    continue;
                }
                case -122: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.FLOAT);
                    ++pc;
                    continue;
                }
                case -121: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.DOUBLE);
                    ++pc;
                    continue;
                }
                case -120: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.INT);
                    ++pc;
                    continue;
                }
                case -119: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.FLOAT);
                    ++pc;
                    continue;
                }
                case -118: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.DOUBLE);
                    ++pc;
                    continue;
                }
                case -117: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.INT);
                    ++pc;
                    continue;
                }
                case -116: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.LONG);
                    ++pc;
                    continue;
                }
                case -115: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.DOUBLE);
                    ++pc;
                    continue;
                }
                case -114: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.INT);
                    ++pc;
                    continue;
                }
                case -113: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.LONG);
                    ++pc;
                    continue;
                }
                case -112: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.FLOAT);
                    ++pc;
                    continue;
                }
                case -111: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.BYTE);
                    ++pc;
                    continue;
                }
                case -110: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.CHAR);
                    ++pc;
                    continue;
                }
                case -109: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.SHORT);
                    ++pc;
                    continue;
                }
                case -108:
                case -107:
                case -106:
                case -105:
                case -104: {
                    final StackMapFrame stackMapFrame34 = frame;
                    stackMapFrame34.numberOfStackItems -= 2;
                    frame.addStackItem(TypeBinding.INT);
                    ++pc;
                    continue;
                }
                case -103:
                case -102:
                case -101:
                case -100:
                case -99:
                case -98: {
                    final StackMapFrame stackMapFrame35 = frame;
                    --stackMapFrame35.numberOfStackItems;
                    this.addRealJumpTarget(realJumpTarget, currentPC + this.i2At(bytecodes, 1, pc));
                    pc += 3;
                    continue;
                }
                case -97:
                case -96:
                case -95:
                case -94:
                case -93:
                case -92:
                case -91:
                case -90: {
                    final StackMapFrame stackMapFrame36 = frame;
                    stackMapFrame36.numberOfStackItems -= 2;
                    this.addRealJumpTarget(realJumpTarget, currentPC + this.i2At(bytecodes, 1, pc));
                    pc += 3;
                    continue;
                }
                case -89: {
                    this.addRealJumpTarget(realJumpTarget, currentPC + this.i2At(bytecodes, 1, pc));
                    pc += 3;
                    this.addRealJumpTarget(realJumpTarget, pc - codeOffset);
                    continue;
                }
                case -86: {
                    ++pc;
                    while ((pc - codeOffset & 0x3) != 0x0) {
                        ++pc;
                    }
                    this.addRealJumpTarget(realJumpTarget, currentPC + this.i4At(bytecodes, 0, pc));
                    pc += 4;
                    final int low = this.i4At(bytecodes, 0, pc);
                    pc += 4;
                    final int high = this.i4At(bytecodes, 0, pc);
                    pc += 4;
                    for (int length = high - low + 1, j = 0; j < length; ++j) {
                        this.addRealJumpTarget(realJumpTarget, currentPC + this.i4At(bytecodes, 0, pc));
                        pc += 4;
                    }
                    final StackMapFrame stackMapFrame37 = frame;
                    --stackMapFrame37.numberOfStackItems;
                    continue;
                }
                case -85: {
                    ++pc;
                    while ((pc - codeOffset & 0x3) != 0x0) {
                        ++pc;
                    }
                    this.addRealJumpTarget(realJumpTarget, currentPC + this.i4At(bytecodes, 0, pc));
                    pc += 4;
                    final int npairs = (int)this.u4At(bytecodes, 0, pc);
                    pc += 4;
                    for (int k = 0; k < npairs; ++k) {
                        pc += 4;
                        this.addRealJumpTarget(realJumpTarget, currentPC + this.i4At(bytecodes, 0, pc));
                        pc += 4;
                    }
                    final StackMapFrame stackMapFrame38 = frame;
                    --stackMapFrame38.numberOfStackItems;
                    continue;
                }
                case -84:
                case -83:
                case -82:
                case -81:
                case -80: {
                    final StackMapFrame stackMapFrame39 = frame;
                    --stackMapFrame39.numberOfStackItems;
                    ++pc;
                    this.addRealJumpTarget(realJumpTarget, pc - codeOffset);
                    continue;
                }
                case -79: {
                    ++pc;
                    this.addRealJumpTarget(realJumpTarget, pc - codeOffset);
                    continue;
                }
                case -78: {
                    final int index = this.u2At(bytecodes, 1, pc);
                    final int nameAndTypeIndex = this.u2At(poolContents, 3, constantPoolOffsets[index]);
                    final int utf8index = this.u2At(poolContents, 3, constantPoolOffsets[nameAndTypeIndex]);
                    final char[] descriptor = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    if (descriptor.length == 1) {
                        switch (descriptor[0]) {
                            case 'Z': {
                                frame.addStackItem(TypeBinding.BOOLEAN);
                                break;
                            }
                            case 'B': {
                                frame.addStackItem(TypeBinding.BYTE);
                                break;
                            }
                            case 'C': {
                                frame.addStackItem(TypeBinding.CHAR);
                                break;
                            }
                            case 'D': {
                                frame.addStackItem(TypeBinding.DOUBLE);
                                break;
                            }
                            case 'F': {
                                frame.addStackItem(TypeBinding.FLOAT);
                                break;
                            }
                            case 'I': {
                                frame.addStackItem(TypeBinding.INT);
                                break;
                            }
                            case 'J': {
                                frame.addStackItem(TypeBinding.LONG);
                                break;
                            }
                            case 'S': {
                                frame.addStackItem(TypeBinding.SHORT);
                                break;
                            }
                        }
                    }
                    else if (descriptor[0] == '[') {
                        frame.addStackItem(new VerificationTypeInfo(0, descriptor));
                    }
                    else {
                        frame.addStackItem(new VerificationTypeInfo(0, CharOperation.subarray(descriptor, 1, descriptor.length - 1)));
                    }
                    pc += 3;
                    continue;
                }
                case -77: {
                    final StackMapFrame stackMapFrame40 = frame;
                    --stackMapFrame40.numberOfStackItems;
                    pc += 3;
                    continue;
                }
                case -76: {
                    final int index = this.u2At(bytecodes, 1, pc);
                    final int nameAndTypeIndex = this.u2At(poolContents, 3, constantPoolOffsets[index]);
                    final int utf8index = this.u2At(poolContents, 3, constantPoolOffsets[nameAndTypeIndex]);
                    final char[] descriptor = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    final StackMapFrame stackMapFrame41 = frame;
                    --stackMapFrame41.numberOfStackItems;
                    if (descriptor.length == 1) {
                        switch (descriptor[0]) {
                            case 'Z': {
                                frame.addStackItem(TypeBinding.BOOLEAN);
                                break;
                            }
                            case 'B': {
                                frame.addStackItem(TypeBinding.BYTE);
                                break;
                            }
                            case 'C': {
                                frame.addStackItem(TypeBinding.CHAR);
                                break;
                            }
                            case 'D': {
                                frame.addStackItem(TypeBinding.DOUBLE);
                                break;
                            }
                            case 'F': {
                                frame.addStackItem(TypeBinding.FLOAT);
                                break;
                            }
                            case 'I': {
                                frame.addStackItem(TypeBinding.INT);
                                break;
                            }
                            case 'J': {
                                frame.addStackItem(TypeBinding.LONG);
                                break;
                            }
                            case 'S': {
                                frame.addStackItem(TypeBinding.SHORT);
                                break;
                            }
                        }
                    }
                    else if (descriptor[0] == '[') {
                        frame.addStackItem(new VerificationTypeInfo(0, descriptor));
                    }
                    else {
                        frame.addStackItem(new VerificationTypeInfo(0, CharOperation.subarray(descriptor, 1, descriptor.length - 1)));
                    }
                    pc += 3;
                    continue;
                }
                case -75: {
                    final StackMapFrame stackMapFrame42 = frame;
                    stackMapFrame42.numberOfStackItems -= 2;
                    pc += 3;
                    continue;
                }
                case -74: {
                    final int index = this.u2At(bytecodes, 1, pc);
                    final int nameAndTypeIndex = this.u2At(poolContents, 3, constantPoolOffsets[index]);
                    int utf8index = this.u2At(poolContents, 3, constantPoolOffsets[nameAndTypeIndex]);
                    final char[] descriptor = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    utf8index = this.u2At(poolContents, 1, constantPoolOffsets[nameAndTypeIndex]);
                    final char[] name = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    final StackMapFrame stackMapFrame43 = frame;
                    stackMapFrame43.numberOfStackItems -= this.getParametersCount(descriptor) + 1;
                    final char[] returnType = this.getReturnType(descriptor);
                    if (returnType.length == 1) {
                        switch (returnType[0]) {
                            case 'Z': {
                                frame.addStackItem(TypeBinding.BOOLEAN);
                                break;
                            }
                            case 'B': {
                                frame.addStackItem(TypeBinding.BYTE);
                                break;
                            }
                            case 'C': {
                                frame.addStackItem(TypeBinding.CHAR);
                                break;
                            }
                            case 'D': {
                                frame.addStackItem(TypeBinding.DOUBLE);
                                break;
                            }
                            case 'F': {
                                frame.addStackItem(TypeBinding.FLOAT);
                                break;
                            }
                            case 'I': {
                                frame.addStackItem(TypeBinding.INT);
                                break;
                            }
                            case 'J': {
                                frame.addStackItem(TypeBinding.LONG);
                                break;
                            }
                            case 'S': {
                                frame.addStackItem(TypeBinding.SHORT);
                                break;
                            }
                        }
                    }
                    else if (returnType[0] == '[') {
                        frame.addStackItem(new VerificationTypeInfo(0, returnType));
                    }
                    else {
                        frame.addStackItem(new VerificationTypeInfo(0, CharOperation.subarray(returnType, 1, returnType.length - 1)));
                    }
                    pc += 3;
                    continue;
                }
                case -70: {
                    final int index = this.u2At(bytecodes, 1, pc);
                    final int nameAndTypeIndex = this.u2At(poolContents, 3, constantPoolOffsets[index]);
                    final int utf8index = this.u2At(poolContents, 3, constantPoolOffsets[nameAndTypeIndex]);
                    final char[] descriptor = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    final StackMapFrame stackMapFrame44 = frame;
                    stackMapFrame44.numberOfStackItems -= this.getParametersCount(descriptor);
                    final char[] returnType = this.getReturnType(descriptor);
                    if (returnType.length == 1) {
                        switch (returnType[0]) {
                            case 'Z': {
                                frame.addStackItem(TypeBinding.BOOLEAN);
                                break;
                            }
                            case 'B': {
                                frame.addStackItem(TypeBinding.BYTE);
                                break;
                            }
                            case 'C': {
                                frame.addStackItem(TypeBinding.CHAR);
                                break;
                            }
                            case 'D': {
                                frame.addStackItem(TypeBinding.DOUBLE);
                                break;
                            }
                            case 'F': {
                                frame.addStackItem(TypeBinding.FLOAT);
                                break;
                            }
                            case 'I': {
                                frame.addStackItem(TypeBinding.INT);
                                break;
                            }
                            case 'J': {
                                frame.addStackItem(TypeBinding.LONG);
                                break;
                            }
                            case 'S': {
                                frame.addStackItem(TypeBinding.SHORT);
                                break;
                            }
                        }
                    }
                    else if (returnType[0] == '[') {
                        frame.addStackItem(new VerificationTypeInfo(0, returnType));
                    }
                    else {
                        frame.addStackItem(new VerificationTypeInfo(0, CharOperation.subarray(returnType, 1, returnType.length - 1)));
                    }
                    pc += 5;
                    continue;
                }
                case -73: {
                    final int index = this.u2At(bytecodes, 1, pc);
                    final int nameAndTypeIndex = this.u2At(poolContents, 3, constantPoolOffsets[index]);
                    int utf8index = this.u2At(poolContents, 3, constantPoolOffsets[nameAndTypeIndex]);
                    final char[] descriptor = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    utf8index = this.u2At(poolContents, 1, constantPoolOffsets[nameAndTypeIndex]);
                    final char[] name = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    final StackMapFrame stackMapFrame45 = frame;
                    stackMapFrame45.numberOfStackItems -= this.getParametersCount(descriptor);
                    if (CharOperation.equals(ConstantPool.Init, name)) {
                        frame.stackItems[frame.numberOfStackItems - 1].tag = 7;
                    }
                    final StackMapFrame stackMapFrame46 = frame;
                    --stackMapFrame46.numberOfStackItems;
                    final char[] returnType = this.getReturnType(descriptor);
                    if (returnType.length == 1) {
                        switch (returnType[0]) {
                            case 'Z': {
                                frame.addStackItem(TypeBinding.BOOLEAN);
                                break;
                            }
                            case 'B': {
                                frame.addStackItem(TypeBinding.BYTE);
                                break;
                            }
                            case 'C': {
                                frame.addStackItem(TypeBinding.CHAR);
                                break;
                            }
                            case 'D': {
                                frame.addStackItem(TypeBinding.DOUBLE);
                                break;
                            }
                            case 'F': {
                                frame.addStackItem(TypeBinding.FLOAT);
                                break;
                            }
                            case 'I': {
                                frame.addStackItem(TypeBinding.INT);
                                break;
                            }
                            case 'J': {
                                frame.addStackItem(TypeBinding.LONG);
                                break;
                            }
                            case 'S': {
                                frame.addStackItem(TypeBinding.SHORT);
                                break;
                            }
                        }
                    }
                    else if (returnType[0] == '[') {
                        frame.addStackItem(new VerificationTypeInfo(0, returnType));
                    }
                    else {
                        frame.addStackItem(new VerificationTypeInfo(0, CharOperation.subarray(returnType, 1, returnType.length - 1)));
                    }
                    pc += 3;
                    continue;
                }
                case -72: {
                    final int index = this.u2At(bytecodes, 1, pc);
                    final int nameAndTypeIndex = this.u2At(poolContents, 3, constantPoolOffsets[index]);
                    int utf8index = this.u2At(poolContents, 3, constantPoolOffsets[nameAndTypeIndex]);
                    final char[] descriptor = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    utf8index = this.u2At(poolContents, 1, constantPoolOffsets[nameAndTypeIndex]);
                    final char[] name = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    final StackMapFrame stackMapFrame47 = frame;
                    stackMapFrame47.numberOfStackItems -= this.getParametersCount(descriptor);
                    final char[] returnType = this.getReturnType(descriptor);
                    if (returnType.length == 1) {
                        switch (returnType[0]) {
                            case 'Z': {
                                frame.addStackItem(TypeBinding.BOOLEAN);
                                break;
                            }
                            case 'B': {
                                frame.addStackItem(TypeBinding.BYTE);
                                break;
                            }
                            case 'C': {
                                frame.addStackItem(TypeBinding.CHAR);
                                break;
                            }
                            case 'D': {
                                frame.addStackItem(TypeBinding.DOUBLE);
                                break;
                            }
                            case 'F': {
                                frame.addStackItem(TypeBinding.FLOAT);
                                break;
                            }
                            case 'I': {
                                frame.addStackItem(TypeBinding.INT);
                                break;
                            }
                            case 'J': {
                                frame.addStackItem(TypeBinding.LONG);
                                break;
                            }
                            case 'S': {
                                frame.addStackItem(TypeBinding.SHORT);
                                break;
                            }
                        }
                    }
                    else if (returnType[0] == '[') {
                        frame.addStackItem(new VerificationTypeInfo(0, returnType));
                    }
                    else {
                        frame.addStackItem(new VerificationTypeInfo(0, CharOperation.subarray(returnType, 1, returnType.length - 1)));
                    }
                    pc += 3;
                    continue;
                }
                case -71: {
                    final int index = this.u2At(bytecodes, 1, pc);
                    final int nameAndTypeIndex = this.u2At(poolContents, 3, constantPoolOffsets[index]);
                    int utf8index = this.u2At(poolContents, 3, constantPoolOffsets[nameAndTypeIndex]);
                    final char[] descriptor = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    utf8index = this.u2At(poolContents, 1, constantPoolOffsets[nameAndTypeIndex]);
                    final char[] name = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    final StackMapFrame stackMapFrame48 = frame;
                    stackMapFrame48.numberOfStackItems -= this.getParametersCount(descriptor) + 1;
                    final char[] returnType = this.getReturnType(descriptor);
                    if (returnType.length == 1) {
                        switch (returnType[0]) {
                            case 'Z': {
                                frame.addStackItem(TypeBinding.BOOLEAN);
                                break;
                            }
                            case 'B': {
                                frame.addStackItem(TypeBinding.BYTE);
                                break;
                            }
                            case 'C': {
                                frame.addStackItem(TypeBinding.CHAR);
                                break;
                            }
                            case 'D': {
                                frame.addStackItem(TypeBinding.DOUBLE);
                                break;
                            }
                            case 'F': {
                                frame.addStackItem(TypeBinding.FLOAT);
                                break;
                            }
                            case 'I': {
                                frame.addStackItem(TypeBinding.INT);
                                break;
                            }
                            case 'J': {
                                frame.addStackItem(TypeBinding.LONG);
                                break;
                            }
                            case 'S': {
                                frame.addStackItem(TypeBinding.SHORT);
                                break;
                            }
                        }
                    }
                    else if (returnType[0] == '[') {
                        frame.addStackItem(new VerificationTypeInfo(0, returnType));
                    }
                    else {
                        frame.addStackItem(new VerificationTypeInfo(0, CharOperation.subarray(returnType, 1, returnType.length - 1)));
                    }
                    pc += 5;
                    continue;
                }
                case -69: {
                    final int index = this.u2At(bytecodes, 1, pc);
                    final int utf8index = this.u2At(poolContents, 1, constantPoolOffsets[index]);
                    final char[] className = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    final VerificationTypeInfo verificationTypeInfo = new VerificationTypeInfo(0, 8, className);
                    verificationTypeInfo.offset = currentPC;
                    frame.addStackItem(verificationTypeInfo);
                    pc += 3;
                    continue;
                }
                case -68: {
                    char[] constantPoolName = null;
                    switch (this.u1At(bytecodes, 1, pc)) {
                        case 10: {
                            constantPoolName = new char[] { '[', 'I' };
                            break;
                        }
                        case 8: {
                            constantPoolName = new char[] { '[', 'B' };
                            break;
                        }
                        case 4: {
                            constantPoolName = new char[] { '[', 'Z' };
                            break;
                        }
                        case 9: {
                            constantPoolName = new char[] { '[', 'S' };
                            break;
                        }
                        case 5: {
                            constantPoolName = new char[] { '[', 'C' };
                            break;
                        }
                        case 11: {
                            constantPoolName = new char[] { '[', 'J' };
                            break;
                        }
                        case 6: {
                            constantPoolName = new char[] { '[', 'F' };
                            break;
                        }
                        case 7: {
                            constantPoolName = new char[] { '[', 'D' };
                            break;
                        }
                    }
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(1, constantPoolName);
                    pc += 2;
                    continue;
                }
                case -67: {
                    final int index = this.u2At(bytecodes, 1, pc);
                    final int utf8index = this.u2At(poolContents, 1, constantPoolOffsets[index]);
                    final char[] className = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    final int classNameLength = className.length;
                    char[] constantPoolName;
                    if (className[0] != '[') {
                        System.arraycopy(className, 0, constantPoolName = new char[classNameLength + 3], 2, classNameLength);
                        constantPoolName[0] = '[';
                        constantPoolName[1] = 'L';
                        constantPoolName[classNameLength + 2] = ';';
                    }
                    else {
                        System.arraycopy(className, 0, constantPoolName = new char[classNameLength + 1], 1, classNameLength);
                        constantPoolName[0] = '[';
                    }
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(0, constantPoolName);
                    pc += 3;
                    continue;
                }
                case -66: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.INT);
                    ++pc;
                    continue;
                }
                case -65: {
                    final StackMapFrame stackMapFrame49 = frame;
                    --stackMapFrame49.numberOfStackItems;
                    ++pc;
                    this.addRealJumpTarget(realJumpTarget, pc - codeOffset);
                    continue;
                }
                case -64: {
                    final int index = this.u2At(bytecodes, 1, pc);
                    final int utf8index = this.u2At(poolContents, 1, constantPoolOffsets[index]);
                    final char[] className = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(0, className);
                    pc += 3;
                    continue;
                }
                case -63: {
                    frame.stackItems[frame.numberOfStackItems - 1] = new VerificationTypeInfo(TypeBinding.INT);
                    pc += 3;
                    continue;
                }
                case -62:
                case -61: {
                    final StackMapFrame stackMapFrame50 = frame;
                    --stackMapFrame50.numberOfStackItems;
                    ++pc;
                    continue;
                }
                case -60: {
                    opcode = (byte)this.u1At(bytecodes, 1, pc);
                    if (opcode == -124) {
                        pc += 6;
                        continue;
                    }
                    final int index = this.u2At(bytecodes, 2, pc);
                    switch (opcode) {
                        case 21: {
                            frame.addStackItem(TypeBinding.INT);
                            break;
                        }
                        case 23: {
                            frame.addStackItem(TypeBinding.FLOAT);
                            break;
                        }
                        case 25: {
                            VerificationTypeInfo localsN = frame.locals[index];
                            if (localsN == null) {
                                localsN = this.retrieveLocal(currentPC, index);
                            }
                            frame.addStackItem(localsN);
                            break;
                        }
                        case 22: {
                            frame.addStackItem(TypeBinding.LONG);
                            break;
                        }
                        case 24: {
                            frame.addStackItem(TypeBinding.DOUBLE);
                            break;
                        }
                        case 54: {
                            final StackMapFrame stackMapFrame51 = frame;
                            --stackMapFrame51.numberOfStackItems;
                            break;
                        }
                        case 56: {
                            final StackMapFrame stackMapFrame52 = frame;
                            --stackMapFrame52.numberOfStackItems;
                            break;
                        }
                        case 58: {
                            frame.locals[index] = frame.stackItems[frame.numberOfStackItems - 1];
                            final StackMapFrame stackMapFrame53 = frame;
                            --stackMapFrame53.numberOfStackItems;
                            break;
                        }
                        case 55: {
                            final StackMapFrame stackMapFrame54 = frame;
                            --stackMapFrame54.numberOfStackItems;
                            break;
                        }
                        case 57: {
                            final StackMapFrame stackMapFrame55 = frame;
                            --stackMapFrame55.numberOfStackItems;
                            break;
                        }
                    }
                    pc += 4;
                    continue;
                }
                case -59: {
                    final int index = this.u2At(bytecodes, 1, pc);
                    final int utf8index = this.u2At(poolContents, 1, constantPoolOffsets[index]);
                    final char[] className = this.utf8At(poolContents, constantPoolOffsets[utf8index] + 3, this.u2At(poolContents, 1, constantPoolOffsets[utf8index]));
                    final int dimensions = this.u1At(bytecodes, 3, pc);
                    final StackMapFrame stackMapFrame56 = frame;
                    stackMapFrame56.numberOfStackItems -= dimensions;
                    final int classNameLength = className.length;
                    final char[] constantPoolName = new char[classNameLength];
                    System.arraycopy(className, 0, constantPoolName, 0, classNameLength);
                    frame.addStackItem(new VerificationTypeInfo(0, constantPoolName));
                    pc += 4;
                    continue;
                }
                case -58:
                case -57: {
                    final StackMapFrame stackMapFrame57 = frame;
                    --stackMapFrame57.numberOfStackItems;
                    this.addRealJumpTarget(realJumpTarget, currentPC + this.i2At(bytecodes, 1, pc));
                    pc += 3;
                    continue;
                }
                case -56: {
                    this.addRealJumpTarget(realJumpTarget, currentPC + this.i4At(bytecodes, 1, pc));
                    pc += 5;
                    this.addRealJumpTarget(realJumpTarget, pc - codeOffset);
                    continue;
                }
                default: {
                    if (this.codeStream.methodDeclaration != null) {
                        this.codeStream.methodDeclaration.scope.problemReporter().abortDueToInternalError(Messages.bind(Messages.abort_invalidOpcode, new Object[] { opcode, pc, new String(methodBinding.shortReadableName()) }), this.codeStream.methodDeclaration);
                        continue;
                    }
                    this.codeStream.lambdaExpression.scope.problemReporter().abortDueToInternalError(Messages.bind(Messages.abort_invalidOpcode, new Object[] { opcode, pc, new String(methodBinding.shortReadableName()) }), this.codeStream.lambdaExpression);
                    continue;
                }
            }
        } while (pc < codeLength + codeOffset);
        return this.filterFakeFrames(realJumpTarget, frames, codeLength);
    }
    
    private void addRealJumpTarget(final Set realJumpTarget, final int pc) {
        realJumpTarget.add(pc);
    }
    
    private void add(final Map frames, final StackMapFrame frame) {
        frames.put(frame.pc, frame);
    }
    
    private final int u1At(final byte[] reference, final int relativeOffset, final int structOffset) {
        return reference[relativeOffset + structOffset] & 0xFF;
    }
    
    private final int u2At(final byte[] reference, final int relativeOffset, final int structOffset) {
        int position = relativeOffset + structOffset;
        return ((reference[position++] & 0xFF) << 8) + (reference[position] & 0xFF);
    }
    
    private final long u4At(final byte[] reference, final int relativeOffset, final int structOffset) {
        int position = relativeOffset + structOffset;
        return (((long)reference[position++] & 0xFFL) << 24) + ((reference[position++] & 0xFF) << 16) + ((reference[position++] & 0xFF) << 8) + (reference[position] & 0xFF);
    }
    
    private final int i2At(final byte[] reference, final int relativeOffset, final int structOffset) {
        int position = relativeOffset + structOffset;
        return (reference[position++] << 8) + (reference[position] & 0xFF);
    }
    
    public char[] utf8At(final byte[] reference, final int absoluteOffset, final int bytesAvailable) {
        int length = bytesAvailable;
        char[] outputBuf = new char[bytesAvailable];
        int outputPos = 0;
        int readOffset = absoluteOffset;
        while (length != 0) {
            int x = reference[readOffset++] & 0xFF;
            --length;
            if ((0x80 & x) != 0x0) {
                if ((x & 0x20) != 0x0) {
                    length -= 2;
                    x = ((x & 0xF) << 12 | (reference[readOffset++] & 0x3F) << 6 | (reference[readOffset++] & 0x3F));
                }
                else {
                    --length;
                    x = ((x & 0x1F) << 6 | (reference[readOffset++] & 0x3F));
                }
            }
            outputBuf[outputPos++] = (char)x;
        }
        if (outputPos != bytesAvailable) {
            System.arraycopy(outputBuf, 0, outputBuf = new char[outputPos], 0, outputPos);
        }
        return outputBuf;
    }
}
