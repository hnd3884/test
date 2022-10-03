package org.eclipse.jdt.internal.compiler;

import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayReference;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import java.util.Map;
import org.eclipse.jdt.internal.compiler.util.HashtableOfObjectToInt;

public class SourceElementNotifier
{
    ISourceElementRequestor requestor;
    boolean reportReferenceInfo;
    char[][] typeNames;
    char[][] superTypeNames;
    int nestedTypeIndex;
    LocalDeclarationVisitor localDeclarationVisitor;
    HashtableOfObjectToInt sourceEnds;
    Map nodesToCategories;
    int initialPosition;
    int eofPosition;
    
    public SourceElementNotifier(final ISourceElementRequestor requestor, final boolean reportLocalDeclarations) {
        this.localDeclarationVisitor = null;
        this.requestor = requestor;
        if (reportLocalDeclarations) {
            this.localDeclarationVisitor = new LocalDeclarationVisitor();
        }
        this.typeNames = new char[4][];
        this.superTypeNames = new char[4][];
        this.nestedTypeIndex = 0;
    }
    
    protected Object[][] getArgumentInfos(final Argument[] arguments) {
        final int argumentLength = arguments.length;
        final char[][] argumentTypes = new char[argumentLength][];
        final char[][] argumentNames = new char[argumentLength][];
        final ISourceElementRequestor.ParameterInfo[] parameterInfos = new ISourceElementRequestor.ParameterInfo[argumentLength];
        for (int i = 0; i < argumentLength; ++i) {
            final Argument argument = arguments[i];
            argumentTypes[i] = CharOperation.concatWith(argument.type.getParameterizedTypeName(), '.');
            final char[] name = argument.name;
            argumentNames[i] = name;
            final ISourceElementRequestor.ParameterInfo parameterInfo = new ISourceElementRequestor.ParameterInfo();
            parameterInfo.declarationStart = argument.declarationSourceStart;
            parameterInfo.declarationEnd = argument.declarationSourceEnd;
            parameterInfo.nameSourceStart = argument.sourceStart;
            parameterInfo.nameSourceEnd = argument.sourceEnd;
            parameterInfo.modifiers = argument.modifiers;
            parameterInfo.name = name;
            parameterInfos[i] = parameterInfo;
        }
        return new Object[][] { parameterInfos, { argumentTypes, argumentNames } };
    }
    
    protected char[][] getInterfaceNames(final TypeDeclaration typeDeclaration) {
        char[][] interfaceNames = null;
        int superInterfacesLength = 0;
        TypeReference[] superInterfaces = typeDeclaration.superInterfaces;
        if (superInterfaces != null) {
            superInterfacesLength = superInterfaces.length;
            interfaceNames = new char[superInterfacesLength][];
        }
        else if ((typeDeclaration.bits & 0x200) != 0x0) {
            final QualifiedAllocationExpression alloc = typeDeclaration.allocation;
            if (alloc != null && alloc.type != null) {
                superInterfaces = new TypeReference[] { alloc.type };
                superInterfacesLength = 1;
                interfaceNames = new char[][] { null };
            }
        }
        if (superInterfaces != null) {
            for (int i = 0; i < superInterfacesLength; ++i) {
                interfaceNames[i] = CharOperation.concatWith(superInterfaces[i].getParameterizedTypeName(), '.');
            }
        }
        return interfaceNames;
    }
    
    protected char[] getSuperclassName(final TypeDeclaration typeDeclaration) {
        final TypeReference superclass = typeDeclaration.superclass;
        return (char[])((superclass != null) ? CharOperation.concatWith(superclass.getParameterizedTypeName(), '.') : null);
    }
    
    protected char[][] getThrownExceptions(final AbstractMethodDeclaration methodDeclaration) {
        char[][] thrownExceptionTypes = null;
        final TypeReference[] thrownExceptions = methodDeclaration.thrownExceptions;
        if (thrownExceptions != null) {
            final int thrownExceptionLength = thrownExceptions.length;
            thrownExceptionTypes = new char[thrownExceptionLength][];
            for (int i = 0; i < thrownExceptionLength; ++i) {
                thrownExceptionTypes[i] = CharOperation.concatWith(thrownExceptions[i].getParameterizedTypeName(), '.');
            }
        }
        return thrownExceptionTypes;
    }
    
    protected char[][] getTypeParameterBounds(final TypeParameter typeParameter) {
        final TypeReference firstBound = typeParameter.type;
        final TypeReference[] otherBounds = typeParameter.bounds;
        char[][] typeParameterBounds = null;
        if (firstBound != null) {
            if (otherBounds != null) {
                final int otherBoundsLength = otherBounds.length;
                final char[][] boundNames = new char[otherBoundsLength + 1][];
                boundNames[0] = CharOperation.concatWith(firstBound.getParameterizedTypeName(), '.');
                for (int j = 0; j < otherBoundsLength; ++j) {
                    boundNames[j + 1] = CharOperation.concatWith(otherBounds[j].getParameterizedTypeName(), '.');
                }
                typeParameterBounds = boundNames;
            }
            else {
                typeParameterBounds = new char[][] { CharOperation.concatWith(firstBound.getParameterizedTypeName(), '.') };
            }
        }
        else {
            typeParameterBounds = CharOperation.NO_CHAR_CHAR;
        }
        return typeParameterBounds;
    }
    
    private ISourceElementRequestor.TypeParameterInfo[] getTypeParameterInfos(final TypeParameter[] typeParameters) {
        if (typeParameters == null) {
            return null;
        }
        final int typeParametersLength = typeParameters.length;
        final ISourceElementRequestor.TypeParameterInfo[] result = new ISourceElementRequestor.TypeParameterInfo[typeParametersLength];
        for (int i = 0; i < typeParametersLength; ++i) {
            final TypeParameter typeParameter = typeParameters[i];
            final char[][] typeParameterBounds = this.getTypeParameterBounds(typeParameter);
            final ISourceElementRequestor.TypeParameterInfo typeParameterInfo = new ISourceElementRequestor.TypeParameterInfo();
            typeParameterInfo.typeAnnotated = ((typeParameter.bits & 0x100000) != 0x0);
            typeParameterInfo.declarationStart = typeParameter.declarationSourceStart;
            typeParameterInfo.declarationEnd = typeParameter.declarationSourceEnd;
            typeParameterInfo.name = typeParameter.name;
            typeParameterInfo.nameSourceStart = typeParameter.sourceStart;
            typeParameterInfo.nameSourceEnd = typeParameter.sourceEnd;
            typeParameterInfo.bounds = typeParameterBounds;
            result[i] = typeParameterInfo;
        }
        return result;
    }
    
    private boolean hasDeprecatedAnnotation(final Annotation[] annotations) {
        if (annotations != null) {
            for (int i = 0, length = annotations.length; i < length; ++i) {
                final Annotation annotation = annotations[i];
                if (CharOperation.equals(annotation.type.getLastToken(), TypeConstants.JAVA_LANG_DEPRECATED[2])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected void notifySourceElementRequestor(final AbstractMethodDeclaration methodDeclaration, final TypeDeclaration declaringType, final ImportReference currentPackage) {
        final boolean isInRange = this.initialPosition <= methodDeclaration.declarationSourceStart && this.eofPosition >= methodDeclaration.declarationSourceEnd;
        if (methodDeclaration.isClinit()) {
            this.visitIfNeeded(methodDeclaration);
            return;
        }
        if (methodDeclaration.isDefaultConstructor()) {
            if (this.reportReferenceInfo) {
                final ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration)methodDeclaration;
                final ExplicitConstructorCall constructorCall = constructorDeclaration.constructorCall;
                if (constructorCall != null) {
                    switch (constructorCall.accessMode) {
                        case 3: {
                            this.requestor.acceptConstructorReference(this.typeNames[this.nestedTypeIndex - 1], (constructorCall.arguments == null) ? 0 : constructorCall.arguments.length, constructorCall.sourceStart);
                            break;
                        }
                        case 1:
                        case 2: {
                            this.requestor.acceptConstructorReference(this.superTypeNames[this.nestedTypeIndex - 1], (constructorCall.arguments == null) ? 0 : constructorCall.arguments.length, constructorCall.sourceStart);
                            break;
                        }
                    }
                }
            }
            return;
        }
        char[][] argumentTypes = null;
        char[][] argumentNames = null;
        boolean isVarArgs = false;
        final Argument[] arguments = methodDeclaration.arguments;
        ISourceElementRequestor.ParameterInfo[] parameterInfos = null;
        final ISourceElementRequestor.MethodInfo methodInfo = new ISourceElementRequestor.MethodInfo();
        methodInfo.typeAnnotated = ((methodDeclaration.bits & 0x100000) != 0x0);
        if (arguments != null) {
            final Object[][] argumentInfos = this.getArgumentInfos(arguments);
            parameterInfos = (ISourceElementRequestor.ParameterInfo[])argumentInfos[0];
            argumentTypes = (char[][])argumentInfos[1][0];
            argumentNames = (char[][])argumentInfos[1][1];
            isVarArgs = arguments[arguments.length - 1].isVarArgs();
        }
        final char[][] thrownExceptionTypes = this.getThrownExceptions(methodDeclaration);
        int selectorSourceEnd = -1;
        if (methodDeclaration.isConstructor()) {
            selectorSourceEnd = this.sourceEnds.get(methodDeclaration);
            if (isInRange) {
                int currentModifiers = methodDeclaration.modifiers;
                currentModifiers &= 0x10FFFF;
                if (isVarArgs) {
                    currentModifiers |= 0x80;
                }
                if (this.hasDeprecatedAnnotation(methodDeclaration.annotations)) {
                    currentModifiers |= 0x100000;
                }
                methodInfo.isConstructor = true;
                methodInfo.declarationStart = methodDeclaration.declarationSourceStart;
                methodInfo.modifiers = currentModifiers;
                methodInfo.name = methodDeclaration.selector;
                methodInfo.nameSourceStart = methodDeclaration.sourceStart;
                methodInfo.nameSourceEnd = selectorSourceEnd;
                methodInfo.parameterTypes = argumentTypes;
                methodInfo.parameterNames = argumentNames;
                methodInfo.exceptionTypes = thrownExceptionTypes;
                methodInfo.typeParameters = this.getTypeParameterInfos(methodDeclaration.typeParameters());
                methodInfo.parameterInfos = parameterInfos;
                methodInfo.categories = this.nodesToCategories.get(methodDeclaration);
                methodInfo.annotations = methodDeclaration.annotations;
                methodInfo.declaringPackageName = ((currentPackage == null) ? CharOperation.NO_CHAR : CharOperation.concatWith(currentPackage.tokens, '.'));
                methodInfo.declaringTypeModifiers = declaringType.modifiers;
                methodInfo.extraFlags = ExtraFlags.getExtraFlags(declaringType);
                methodInfo.node = methodDeclaration;
                this.requestor.enterConstructor(methodInfo);
            }
            if (this.reportReferenceInfo) {
                final ConstructorDeclaration constructorDeclaration2 = (ConstructorDeclaration)methodDeclaration;
                final ExplicitConstructorCall constructorCall2 = constructorDeclaration2.constructorCall;
                if (constructorCall2 != null) {
                    switch (constructorCall2.accessMode) {
                        case 3: {
                            this.requestor.acceptConstructorReference(this.typeNames[this.nestedTypeIndex - 1], (constructorCall2.arguments == null) ? 0 : constructorCall2.arguments.length, constructorCall2.sourceStart);
                            break;
                        }
                        case 1:
                        case 2: {
                            this.requestor.acceptConstructorReference(this.superTypeNames[this.nestedTypeIndex - 1], (constructorCall2.arguments == null) ? 0 : constructorCall2.arguments.length, constructorCall2.sourceStart);
                            break;
                        }
                    }
                }
            }
            this.visitIfNeeded(methodDeclaration);
            if (isInRange) {
                this.requestor.exitConstructor(methodDeclaration.declarationSourceEnd);
            }
            return;
        }
        selectorSourceEnd = this.sourceEnds.get(methodDeclaration);
        if (isInRange) {
            int currentModifiers = methodDeclaration.modifiers;
            currentModifiers &= 0x13FFFF;
            if (isVarArgs) {
                currentModifiers |= 0x80;
            }
            if (this.hasDeprecatedAnnotation(methodDeclaration.annotations)) {
                currentModifiers |= 0x100000;
            }
            final TypeReference returnType = (methodDeclaration instanceof MethodDeclaration) ? ((MethodDeclaration)methodDeclaration).returnType : null;
            methodInfo.isAnnotation = (methodDeclaration instanceof AnnotationMethodDeclaration);
            methodInfo.declarationStart = methodDeclaration.declarationSourceStart;
            methodInfo.modifiers = currentModifiers;
            methodInfo.returnType = (char[])((returnType == null) ? null : CharOperation.concatWith(returnType.getParameterizedTypeName(), '.'));
            methodInfo.name = methodDeclaration.selector;
            methodInfo.nameSourceStart = methodDeclaration.sourceStart;
            methodInfo.nameSourceEnd = selectorSourceEnd;
            methodInfo.parameterTypes = argumentTypes;
            methodInfo.parameterNames = argumentNames;
            methodInfo.exceptionTypes = thrownExceptionTypes;
            methodInfo.typeParameters = this.getTypeParameterInfos(methodDeclaration.typeParameters());
            methodInfo.parameterInfos = parameterInfos;
            methodInfo.categories = this.nodesToCategories.get(methodDeclaration);
            methodInfo.annotations = methodDeclaration.annotations;
            methodInfo.node = methodDeclaration;
            methodInfo.enclosingType = declaringType;
            methodInfo.declaringPackageName = ((currentPackage == null) ? CharOperation.NO_CHAR : CharOperation.concatWith(currentPackage.tokens, '.'));
            this.requestor.enterMethod(methodInfo);
        }
        this.visitIfNeeded(methodDeclaration);
        if (isInRange) {
            if (methodDeclaration instanceof AnnotationMethodDeclaration) {
                final AnnotationMethodDeclaration annotationMethodDeclaration = (AnnotationMethodDeclaration)methodDeclaration;
                final Expression expression = annotationMethodDeclaration.defaultValue;
                if (expression != null) {
                    this.requestor.exitMethod(methodDeclaration.declarationSourceEnd, expression);
                    return;
                }
            }
            this.requestor.exitMethod(methodDeclaration.declarationSourceEnd, (Expression)null);
        }
    }
    
    public void notifySourceElementRequestor(final CompilationUnitDeclaration parsedUnit, final int sourceStart, final int sourceEnd, final boolean reportReference, final HashtableOfObjectToInt sourceEndsMap, final Map nodesToCategoriesMap) {
        this.initialPosition = sourceStart;
        this.eofPosition = sourceEnd;
        this.reportReferenceInfo = reportReference;
        this.sourceEnds = sourceEndsMap;
        this.nodesToCategories = nodesToCategoriesMap;
        try {
            final boolean isInRange = this.initialPosition <= parsedUnit.sourceStart && this.eofPosition >= parsedUnit.sourceEnd;
            int length = 0;
            ASTNode[] nodes = null;
            if (isInRange) {
                this.requestor.enterCompilationUnit();
            }
            final ImportReference currentPackage = parsedUnit.currentPackage;
            if (this.localDeclarationVisitor != null) {
                this.localDeclarationVisitor.currentPackage = currentPackage;
            }
            final ImportReference[] imports = parsedUnit.imports;
            final TypeDeclaration[] types = parsedUnit.types;
            length = ((currentPackage != null) ? 1 : 0) + ((imports == null) ? 0 : imports.length) + ((types == null) ? 0 : types.length);
            nodes = new ASTNode[length];
            int index = 0;
            if (currentPackage != null) {
                nodes[index++] = currentPackage;
            }
            if (imports != null) {
                for (int i = 0, max = imports.length; i < max; ++i) {
                    nodes[index++] = imports[i];
                }
            }
            if (types != null) {
                for (int i = 0, max = types.length; i < max; ++i) {
                    nodes[index++] = types[i];
                }
            }
            if (length > 0) {
                quickSort(nodes, 0, length - 1);
                for (final ASTNode node : nodes) {
                    if (node instanceof ImportReference) {
                        final ImportReference importRef = (ImportReference)node;
                        if (node == parsedUnit.currentPackage) {
                            this.notifySourceElementRequestor(importRef, true);
                        }
                        else {
                            this.notifySourceElementRequestor(importRef, false);
                        }
                    }
                    else {
                        this.notifySourceElementRequestor((TypeDeclaration)node, true, null, currentPackage);
                    }
                }
            }
            if (isInRange) {
                this.requestor.exitCompilationUnit(parsedUnit.sourceEnd);
            }
        }
        finally {
            this.reset();
        }
        this.reset();
    }
    
    protected void notifySourceElementRequestor(final FieldDeclaration fieldDeclaration, final TypeDeclaration declaringType) {
        final boolean isInRange = this.initialPosition <= fieldDeclaration.declarationSourceStart && this.eofPosition >= fieldDeclaration.declarationSourceEnd;
        switch (fieldDeclaration.getKind()) {
            case 3: {
                if (this.reportReferenceInfo && fieldDeclaration.initialization instanceof AllocationExpression) {
                    final AllocationExpression alloc = (AllocationExpression)fieldDeclaration.initialization;
                    this.requestor.acceptConstructorReference(declaringType.name, (alloc.arguments == null) ? 0 : alloc.arguments.length, alloc.sourceStart);
                }
            }
            case 1: {
                int fieldEndPosition = this.sourceEnds.get(fieldDeclaration);
                if (fieldEndPosition == -1) {
                    fieldEndPosition = fieldDeclaration.declarationSourceEnd;
                }
                if (isInRange) {
                    int currentModifiers = fieldDeclaration.modifiers;
                    final boolean deprecated = (currentModifiers & 0x100000) != 0x0 || this.hasDeprecatedAnnotation(fieldDeclaration.annotations);
                    char[] typeName = null;
                    if (fieldDeclaration.type == null) {
                        typeName = declaringType.name;
                        currentModifiers |= 0x4000;
                    }
                    else {
                        typeName = CharOperation.concatWith(fieldDeclaration.type.getParameterizedTypeName(), '.');
                    }
                    final ISourceElementRequestor.FieldInfo fieldInfo = new ISourceElementRequestor.FieldInfo();
                    fieldInfo.typeAnnotated = ((fieldDeclaration.bits & 0x100000) != 0x0);
                    fieldInfo.declarationStart = fieldDeclaration.declarationSourceStart;
                    fieldInfo.name = fieldDeclaration.name;
                    fieldInfo.modifiers = (deprecated ? ((currentModifiers & 0xFFFF) | 0x100000) : (currentModifiers & 0xFFFF));
                    fieldInfo.type = typeName;
                    fieldInfo.nameSourceStart = fieldDeclaration.sourceStart;
                    fieldInfo.nameSourceEnd = fieldDeclaration.sourceEnd;
                    fieldInfo.categories = this.nodesToCategories.get(fieldDeclaration);
                    fieldInfo.annotations = fieldDeclaration.annotations;
                    fieldInfo.node = fieldDeclaration;
                    this.requestor.enterField(fieldInfo);
                }
                this.visitIfNeeded(fieldDeclaration, declaringType);
                if (isInRange) {
                    this.requestor.exitField((fieldDeclaration.initialization == null || fieldDeclaration.initialization instanceof ArrayInitializer || fieldDeclaration.initialization instanceof AllocationExpression || fieldDeclaration.initialization instanceof ArrayAllocationExpression || fieldDeclaration.initialization instanceof Assignment || fieldDeclaration.initialization instanceof ClassLiteralAccess || fieldDeclaration.initialization instanceof MessageSend || fieldDeclaration.initialization instanceof ArrayReference || fieldDeclaration.initialization instanceof ThisReference) ? -1 : fieldDeclaration.initialization.sourceStart, fieldEndPosition, fieldDeclaration.declarationSourceEnd);
                    break;
                }
                break;
            }
            case 2: {
                if (isInRange) {
                    this.requestor.enterInitializer(fieldDeclaration.declarationSourceStart, fieldDeclaration.modifiers);
                }
                this.visitIfNeeded((Initializer)fieldDeclaration);
                if (isInRange) {
                    this.requestor.exitInitializer(fieldDeclaration.declarationSourceEnd);
                    break;
                }
                break;
            }
        }
    }
    
    protected void notifySourceElementRequestor(final ImportReference importReference, final boolean isPackage) {
        if (isPackage) {
            this.requestor.acceptPackage(importReference);
        }
        else {
            final boolean onDemand = (importReference.bits & 0x20000) != 0x0;
            this.requestor.acceptImport(importReference.declarationSourceStart, importReference.declarationSourceEnd, importReference.sourceStart, onDemand ? importReference.trailingStarPosition : importReference.sourceEnd, importReference.tokens, onDemand, importReference.modifiers);
        }
    }
    
    protected void notifySourceElementRequestor(final TypeDeclaration typeDeclaration, final boolean notifyTypePresence, final TypeDeclaration declaringType, final ImportReference currentPackage) {
        if (CharOperation.equals(TypeConstants.PACKAGE_INFO_NAME, typeDeclaration.name)) {
            return;
        }
        final boolean isInRange = this.initialPosition <= typeDeclaration.declarationSourceStart && this.eofPosition >= typeDeclaration.declarationSourceEnd;
        final FieldDeclaration[] fields = typeDeclaration.fields;
        final AbstractMethodDeclaration[] methods = typeDeclaration.methods;
        final TypeDeclaration[] memberTypes = typeDeclaration.memberTypes;
        final int fieldCounter = (fields == null) ? 0 : fields.length;
        final int methodCounter = (methods == null) ? 0 : methods.length;
        final int memberTypeCounter = (memberTypes == null) ? 0 : memberTypes.length;
        int fieldIndex = 0;
        int methodIndex = 0;
        int memberTypeIndex = 0;
        if (notifyTypePresence) {
            final char[][] interfaceNames = this.getInterfaceNames(typeDeclaration);
            final int kind = TypeDeclaration.kind(typeDeclaration.modifiers);
            char[] implicitSuperclassName = TypeConstants.CharArray_JAVA_LANG_OBJECT;
            final ISourceElementRequestor.TypeInfo typeInfo = new ISourceElementRequestor.TypeInfo();
            typeInfo.typeAnnotated = ((typeDeclaration.bits & 0x100000) != 0x0);
            if (isInRange) {
                int currentModifiers = typeDeclaration.modifiers;
                final boolean deprecated = (currentModifiers & 0x100000) != 0x0 || this.hasDeprecatedAnnotation(typeDeclaration.annotations);
                final boolean isEnumInit = typeDeclaration.allocation != null && typeDeclaration.allocation.enumConstant != null;
                char[] superclassName;
                if (isEnumInit) {
                    currentModifiers |= 0x4000;
                    superclassName = declaringType.name;
                }
                else {
                    superclassName = this.getSuperclassName(typeDeclaration);
                }
                if (typeDeclaration.allocation == null) {
                    typeInfo.declarationStart = typeDeclaration.declarationSourceStart;
                }
                else if (isEnumInit) {
                    typeInfo.declarationStart = typeDeclaration.allocation.enumConstant.sourceStart;
                }
                else {
                    typeInfo.declarationStart = typeDeclaration.allocation.sourceStart;
                }
                typeInfo.modifiers = (deprecated ? ((currentModifiers & 0xFFFF) | 0x100000) : (currentModifiers & 0xFFFF));
                typeInfo.name = typeDeclaration.name;
                typeInfo.nameSourceStart = (isEnumInit ? typeDeclaration.allocation.enumConstant.sourceStart : typeDeclaration.sourceStart);
                typeInfo.nameSourceEnd = this.sourceEnd(typeDeclaration);
                typeInfo.superclass = superclassName;
                typeInfo.superinterfaces = interfaceNames;
                typeInfo.typeParameters = this.getTypeParameterInfos(typeDeclaration.typeParameters);
                typeInfo.categories = this.nodesToCategories.get(typeDeclaration);
                typeInfo.secondary = typeDeclaration.isSecondary();
                typeInfo.anonymousMember = (typeDeclaration.allocation != null && typeDeclaration.allocation.enclosingInstance != null);
                typeInfo.annotations = typeDeclaration.annotations;
                typeInfo.extraFlags = ExtraFlags.getExtraFlags(typeDeclaration);
                typeInfo.node = typeDeclaration;
                this.requestor.enterType(typeInfo);
                switch (kind) {
                    case 1: {
                        if (superclassName != null) {
                            implicitSuperclassName = superclassName;
                            break;
                        }
                        break;
                    }
                    case 2: {
                        implicitSuperclassName = TypeConstants.CharArray_JAVA_LANG_OBJECT;
                        break;
                    }
                    case 3: {
                        implicitSuperclassName = TypeConstants.CharArray_JAVA_LANG_ENUM;
                        break;
                    }
                    case 4: {
                        implicitSuperclassName = TypeConstants.CharArray_JAVA_LANG_ANNOTATION_ANNOTATION;
                        break;
                    }
                }
            }
            if (this.nestedTypeIndex == this.typeNames.length) {
                System.arraycopy(this.typeNames, 0, this.typeNames = new char[this.nestedTypeIndex * 2][], 0, this.nestedTypeIndex);
                System.arraycopy(this.superTypeNames, 0, this.superTypeNames = new char[this.nestedTypeIndex * 2][], 0, this.nestedTypeIndex);
            }
            this.typeNames[this.nestedTypeIndex] = typeDeclaration.name;
            this.superTypeNames[this.nestedTypeIndex++] = implicitSuperclassName;
        }
        while (fieldIndex < fieldCounter || memberTypeIndex < memberTypeCounter || methodIndex < methodCounter) {
            FieldDeclaration nextFieldDeclaration = null;
            AbstractMethodDeclaration nextMethodDeclaration = null;
            TypeDeclaration nextMemberDeclaration = null;
            int position = Integer.MAX_VALUE;
            int nextDeclarationType = -1;
            if (fieldIndex < fieldCounter) {
                nextFieldDeclaration = fields[fieldIndex];
                if (nextFieldDeclaration.declarationSourceStart < position) {
                    position = nextFieldDeclaration.declarationSourceStart;
                    nextDeclarationType = 0;
                }
            }
            if (methodIndex < methodCounter) {
                nextMethodDeclaration = methods[methodIndex];
                if (nextMethodDeclaration.declarationSourceStart < position) {
                    position = nextMethodDeclaration.declarationSourceStart;
                    nextDeclarationType = 1;
                }
            }
            if (memberTypeIndex < memberTypeCounter) {
                nextMemberDeclaration = memberTypes[memberTypeIndex];
                if (nextMemberDeclaration.declarationSourceStart < position) {
                    position = nextMemberDeclaration.declarationSourceStart;
                    nextDeclarationType = 2;
                }
            }
            switch (nextDeclarationType) {
                default: {
                    continue;
                }
                case 0: {
                    ++fieldIndex;
                    this.notifySourceElementRequestor(nextFieldDeclaration, typeDeclaration);
                    continue;
                }
                case 1: {
                    ++methodIndex;
                    this.notifySourceElementRequestor(nextMethodDeclaration, typeDeclaration, currentPackage);
                    continue;
                }
                case 2: {
                    ++memberTypeIndex;
                    this.notifySourceElementRequestor(nextMemberDeclaration, true, null, currentPackage);
                    continue;
                }
            }
        }
        if (notifyTypePresence) {
            if (isInRange) {
                this.requestor.exitType(typeDeclaration.declarationSourceEnd);
            }
            --this.nestedTypeIndex;
        }
    }
    
    private static void quickSort(final ASTNode[] sortedCollection, int left, int right) {
        final int original_left = left;
        final int original_right = right;
        final ASTNode mid = sortedCollection[left + (right - left) / 2];
        while (true) {
            if (sortedCollection[left].sourceStart >= mid.sourceStart) {
                while (mid.sourceStart < sortedCollection[right].sourceStart) {
                    --right;
                }
                if (left <= right) {
                    final ASTNode tmp = sortedCollection[left];
                    sortedCollection[left] = sortedCollection[right];
                    sortedCollection[right] = tmp;
                    ++left;
                    --right;
                }
                if (left > right) {
                    break;
                }
                continue;
            }
            else {
                ++left;
            }
        }
        if (original_left < right) {
            quickSort(sortedCollection, original_left, right);
        }
        if (left < original_right) {
            quickSort(sortedCollection, left, original_right);
        }
    }
    
    private void reset() {
        this.typeNames = new char[4][];
        this.superTypeNames = new char[4][];
        this.nestedTypeIndex = 0;
        this.sourceEnds = null;
    }
    
    private int sourceEnd(final TypeDeclaration typeDeclaration) {
        if ((typeDeclaration.bits & 0x200) == 0x0) {
            return typeDeclaration.sourceEnd;
        }
        final QualifiedAllocationExpression allocation = typeDeclaration.allocation;
        if (allocation.enumConstant != null) {
            return allocation.enumConstant.sourceEnd;
        }
        return allocation.type.sourceEnd;
    }
    
    private void visitIfNeeded(final AbstractMethodDeclaration method) {
        if (this.localDeclarationVisitor != null && (method.bits & 0x2) != 0x0) {
            if (method instanceof ConstructorDeclaration) {
                final ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration)method;
                if (constructorDeclaration.constructorCall != null) {
                    constructorDeclaration.constructorCall.traverse(this.localDeclarationVisitor, method.scope);
                }
            }
            if (method.statements != null) {
                for (int statementsLength = method.statements.length, i = 0; i < statementsLength; ++i) {
                    method.statements[i].traverse(this.localDeclarationVisitor, method.scope);
                }
            }
        }
    }
    
    private void visitIfNeeded(final FieldDeclaration field, final TypeDeclaration declaringType) {
        if (this.localDeclarationVisitor != null && (field.bits & 0x2) != 0x0 && field.initialization != null) {
            try {
                this.localDeclarationVisitor.pushDeclaringType(declaringType);
                field.initialization.traverse(this.localDeclarationVisitor, (BlockScope)null);
            }
            finally {
                this.localDeclarationVisitor.popDeclaringType();
            }
            this.localDeclarationVisitor.popDeclaringType();
        }
    }
    
    private void visitIfNeeded(final Initializer initializer) {
        if (this.localDeclarationVisitor != null && (initializer.bits & 0x2) != 0x0 && initializer.block != null) {
            initializer.block.traverse(this.localDeclarationVisitor, null);
        }
    }
    
    public class LocalDeclarationVisitor extends ASTVisitor
    {
        public ImportReference currentPackage;
        ArrayList declaringTypes;
        
        public void pushDeclaringType(final TypeDeclaration declaringType) {
            if (this.declaringTypes == null) {
                this.declaringTypes = new ArrayList();
            }
            this.declaringTypes.add(declaringType);
        }
        
        public void popDeclaringType() {
            this.declaringTypes.remove(this.declaringTypes.size() - 1);
        }
        
        public TypeDeclaration peekDeclaringType() {
            if (this.declaringTypes == null) {
                return null;
            }
            final int size = this.declaringTypes.size();
            if (size == 0) {
                return null;
            }
            return this.declaringTypes.get(size - 1);
        }
        
        @Override
        public boolean visit(final TypeDeclaration typeDeclaration, final BlockScope scope) {
            SourceElementNotifier.this.notifySourceElementRequestor(typeDeclaration, true, this.peekDeclaringType(), this.currentPackage);
            return false;
        }
        
        @Override
        public boolean visit(final TypeDeclaration typeDeclaration, final ClassScope scope) {
            SourceElementNotifier.this.notifySourceElementRequestor(typeDeclaration, true, this.peekDeclaringType(), this.currentPackage);
            return false;
        }
    }
}
