package org.eclipse.jdt.internal.compiler.problem;

import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.CaptureBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.FakedTrackingVariable;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.ast.BranchStatement;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.ArrayReference;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.parser.JavadocTagConstants;
import org.eclipse.jdt.internal.compiler.parser.RecoveryScanner;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import java.util.Iterator;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
import java.util.List;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.ast.UnaryExpression;
import org.eclipse.jdt.internal.compiler.ast.CompoundAssignment;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.QualifiedSuperReference;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Receiver;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.NameReference;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Literal;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.FunctionalExpression;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.CharConversionException;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.parser.Scanner;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;

public class ProblemReporter extends ProblemHandler
{
    public ReferenceContext referenceContext;
    private Scanner positionScanner;
    private boolean underScoreIsLambdaParameter;
    private static final byte FIELD_ACCESS = 4;
    private static final byte CONSTRUCTOR_ACCESS = 8;
    private static final byte METHOD_ACCESS = 12;
    
    public ProblemReporter(final IErrorHandlingPolicy policy, final CompilerOptions options, final IProblemFactory problemFactory) {
        super(policy, options, problemFactory);
    }
    
    private static int getElaborationId(final int leadProblemId, final byte elaborationVariant) {
        return leadProblemId << 8 | elaborationVariant;
    }
    
    public static int getIrritant(final int problemID) {
        switch (problemID) {
            case 16777381: {
                return 8;
            }
            case 268435844: {
                return 1024;
            }
            case 67108974: {
                return 1;
            }
            case 67109274: {
                return 2;
            }
            case 67109277:
            case 67109278: {
                return 16384;
            }
            case 16777221:
            case 33554505:
            case 67108967:
            case 67109276:
            case 134217861: {
                return 4;
            }
            case 536870973: {
                return 16;
            }
            case 536870974: {
                return 32;
            }
            case 536870997: {
                return 1074003968;
            }
            case 536871063: {
                return 64;
            }
            case 33554622:
            case 33554623:
            case 67109056:
            case 67109057: {
                return 128;
            }
            case 536871173:
            case 536871177: {
                return 256;
            }
            case 536871352: {
                return 512;
            }
            case 536871353: {
                return 536870928;
            }
            case 570425420:
            case 603979893: {
                return 2048;
            }
            case 553648146:
            case 570425422:
            case 603979895: {
                return 268435456;
            }
            case 536871090: {
                return 8192;
            }
            case 553648135:
            case 570425421:
            case 603979894:
            case 603979910: {
                return 32768;
            }
            case 536871002:
            case 536871006:
            case 536871007:
            case 570425435: {
                return 65536;
            }
            case 570425436:
            case 570425437: {
                return 131072;
            }
            case 16777249:
            case 16777787:
            case 16777792:
            case 16777793: {
                return 536871936;
            }
            case 536871091: {
                return 262144;
            }
            case 536871092:
            case 553648316: {
                return 524288;
            }
            case 536871372: {
                return 134217728;
            }
            case 553648309:
            case 553648311: {
                return 67108864;
            }
            case 536871096: {
                return 16777216;
            }
            case 536871097:
            case 536871098: {
                return 8388608;
            }
            case 570425423: {
                return 4194304;
            }
            case 536871101: {
                return 536870913;
            }
            case 16777746:
            case 16777747:
            case 16777748:
            case 16777752:
            case 16777761:
            case 16777785:
            case 16777786:
            case 16777801:
            case 67109423:
            case 67109438:
            case 67109670: {
                return 536870914;
            }
            case 16777788: {
                return 536936448;
            }
            case 67109491:
            case 67109500: {
                return 536872960;
            }
            case 536871540:
            case 536871541:
            case 536871542: {
                return 536879104;
            }
            case 16777753: {
                return 536870916;
            }
            case 536871008: {
                return 536870920;
            }
            case 16777523: {
                return 536870944;
            }
            case 16777496: {
                return 536887296;
            }
            case 67109665:
            case 134218530: {
                return 536870976;
            }
            case 33555356:
            case 536871363:
            case 536871373:
            case 536871584: {
                return 536871040;
            }
            case 536871364:
            case 536871371:
            case 536871585:
            case 536871831:
            case 536871863:
            case 536871864: {
                return 538968064;
            }
            case 536871365:
            case 536871366:
            case 536871367:
            case 536871368:
            case 536871369:
            case 536871370:
            case 536871582:
            case 536871583:
            case 536871832:
            case 536871843:
            case 536871844:
            case 536871848:
            case 536871849:
            case 536871850:
            case 536871853:
            case 536871854:
            case 536871873: {
                return 541065216;
            }
            case 975:
            case 16778126:
            case 33555366:
            case 33555367:
            case 67109778:
            case 67109779:
            case 67109780:
            case 67109782:
            case 67109803:
            case 67109804:
            case 67109821:
            case 67109823:
            case 67109837:
            case 67109838:
            case 536871833:
            case 536871841:
            case 536871845:
            case 536871865:
            case 536871866:
            case 536871876:
            case 536871877:
            case 536871878: {
                return 1073742848;
            }
            case 969:
            case 970:
            case 976:
            case 977:
            case 978:
            case 16778195: {
                return 1074266112;
            }
            case 16778196:
            case 16778197: {
                return 1074790400;
            }
            case 67109781: {
                return 1073872896;
            }
            case 16778127: {
                return 1073743872;
            }
            case 16778128:
            case 67109822:
            case 67109824:
            case 536871867:
            case 536871868:
            case 536871879: {
                return 1073745920;
            }
            case 67109786:
            case 536871837:
            case 536871838:
            case 536871839:
            case 536871840: {
                return 1073750016;
            }
            case 536871632:
            case 536871633: {
                return 536871168;
            }
            case 33555193:
            case 33555200: {
                return 536875008;
            }
            case 536871678:
            case 536871679: {
                return 1073774592;
            }
            case 16777842: {
                return 536871424;
            }
            case 536871543: {
                return 536903680;
            }
            case 536871547: {
                return 570425344;
            }
            case 536871111: {
                return 537001984;
            }
            case -1610612274:
            case -1610612273:
            case -1610612272:
            case -1610612271:
            case -1610612270:
            case -1610612269:
            case -1610612268:
            case -1610612267:
            case -1610612266:
            case -1610612264:
            case -1610612263:
            case -1610612262:
            case -1610612260:
            case -1610612258:
            case -1610612257:
            case -1610612256:
            case -1610612255:
            case -1610612254:
            case -1610612253:
            case -1610612252:
            case -1610612251:
            case -1610612249:
            case -1610612248:
            case -1610612247:
            case -1610612246:
            case -1610612245:
            case -1610612244:
            case -1610612243:
            case -1610612242:
            case -1610612241:
            case -1610612240:
            case -1610612239:
            case -1610612238:
            case -1610612237:
            case -1610612236:
            case -1610612235:
            case -1610612234:
            case -1610612233:
            case -1610612232:
            case -1610612231:
            case -1610612230:
            case -1610612229:
            case -1610612228:
            case -1610612227:
            case -1610612226:
            case -1610612225:
            case -1610612224:
            case -1610612223:
            case -1610612221:
            case -1610612220:
            case -1610612219:
            case -1610612218:
            case -1610612217:
            case -1610611886:
            case -1610611885:
            case -1610611884:
            case -1610611883:
            case -1610611882:
            case -1610611881:
            case -1610611880:
            case -1610611879:
            case -1610611878:
            case -1610611877: {
                return 33554432;
            }
            case -1610612265:
            case -1610612261:
            case -1610612259: {
                return 2097152;
            }
            case -1610612250: {
                return 1048576;
            }
            case 536870971: {
                return 537133056;
            }
            case 536871106: {
                return 537395200;
            }
            case 67109280: {
                return 537919488;
            }
            case 67109443:
            case 67109524: {
                return 553648128;
            }
            case 16777547: {
                return 603979776;
            }
            case 536871123: {
                return 671088640;
            }
            case 67109281: {
                return 805306368;
            }
            case 16777548: {
                return 1073741825;
            }
            case 536871061: {
                return 1073741826;
            }
            case 536871362: {
                return 1073741828;
            }
            case 536871060: {
                return 1073741832;
            }
            case 603979897: {
                return 1073741840;
            }
            case 603979898: {
                return 1073741856;
            }
            case 536871799:
            case 536871800: {
                return 1073741952;
            }
            case 536871797:
            case 536871798: {
                return 1073742080;
            }
            case 536871801: {
                return 1073742336;
            }
            case 16778100: {
                return 1073741888;
            }
            case 536871825:
            case 536871842: {
                return 1073758208;
            }
            case 16777877: {
                return 1073807360;
            }
            default: {
                return 0;
            }
        }
    }
    
    public static int getProblemCategory(final int severity, final int problemID) {
        if ((severity & 0x80) == 0x0) {
            final int irritant = getIrritant(problemID);
            switch (irritant) {
                case 1:
                case 128:
                case 512:
                case 2048:
                case 4194304:
                case 134217728:
                case 268435456:
                case 536870916:
                case 536870928:
                case 536871168:
                case 536871424:
                case 536872960:
                case 536879104:
                case 537133056:
                case 1073741840:
                case 1073741856:
                case 1073742336: {
                    return 80;
                }
                case 8:
                case 64:
                case 8192:
                case 262144:
                case 524288:
                case 16777216:
                case 536870920:
                case 536870976:
                case 536871040:
                case 536875008:
                case 537395200:
                case 537919488:
                case 538968064:
                case 541065216:
                case 671088640:
                case 805306368:
                case 1073741825:
                case 1073741826:
                case 1073741832:
                case 1073741952:
                case 1073742080:
                case 1073774592:
                case 1074266112:
                case 1074790400: {
                    return 90;
                }
                case 2:
                case 16384:
                case 65536:
                case 131072:
                case 536871936: {
                    return 100;
                }
                case 16:
                case 32:
                case 1024:
                case 32768:
                case 8388608:
                case 67108864:
                case 536870913:
                case 536903680:
                case 537001984:
                case 570425344:
                case 603979776:
                case 1073741888:
                case 1073807360:
                case 1074003968: {
                    return 120;
                }
                case 4: {
                    return 110;
                }
                case 256: {
                    return 140;
                }
                case 4096: {
                    return 0;
                }
                case 1048576:
                case 2097152:
                case 33554432:
                case 33554436: {
                    return 70;
                }
                case 536870914:
                case 536936448: {
                    return 130;
                }
                case 536870944:
                case 536887296: {
                    return 150;
                }
                case 1073742848:
                case 1073743872:
                case 1073745920:
                case 1073758208:
                case 1073872896: {
                    return 90;
                }
                case 1073750016: {
                    return 120;
                }
            }
        }
        switch (problemID) {
            case 16777540:
            case 536871612: {
                return 10;
            }
            default: {
                if ((problemID & 0x40000000) != 0x0) {
                    return 20;
                }
                if ((problemID & 0x10000000) != 0x0) {
                    return 30;
                }
                if ((problemID & 0x1000000) != 0x0) {
                    return 40;
                }
                if ((problemID & 0xE000000) != 0x0) {
                    return 50;
                }
                return 60;
            }
        }
    }
    
    public void abortDueToInternalError(final String errorMessage) {
        this.abortDueToInternalError(errorMessage, null);
    }
    
    public void abortDueToInternalError(final String errorMessage, final ASTNode location) {
        final String[] arguments = { errorMessage };
        this.handle(0, arguments, arguments, 159, (location == null) ? 0 : location.sourceStart, (location == null) ? 0 : location.sourceEnd);
    }
    
    public void abstractMethodCannotBeOverridden(final SourceTypeBinding type, final MethodBinding concreteMethod) {
        this.handle(67109275, new String[] { new String(type.sourceName()), new String(CharOperation.concat(concreteMethod.declaringClass.readableName(), concreteMethod.readableName(), '.')) }, new String[] { new String(type.sourceName()), new String(CharOperation.concat(concreteMethod.declaringClass.shortReadableName(), concreteMethod.shortReadableName(), '.')) }, type.sourceStart(), type.sourceEnd());
    }
    
    public void abstractMethodInAbstractClass(final SourceTypeBinding type, final AbstractMethodDeclaration methodDecl) {
        if (type.isEnum() && type.isLocalType()) {
            final FieldBinding field = type.scope.enclosingMethodScope().initializedField;
            final FieldDeclaration decl = field.sourceField();
            final String[] arguments = { new String(decl.name), new String(methodDecl.selector) };
            this.handle(67109629, arguments, arguments, methodDecl.sourceStart, methodDecl.sourceEnd);
        }
        else {
            final String[] arguments2 = { new String(type.sourceName()), new String(methodDecl.selector) };
            this.handle(67109227, arguments2, arguments2, methodDecl.sourceStart, methodDecl.sourceEnd);
        }
    }
    
    public void abstractMethodInConcreteClass(final SourceTypeBinding type) {
        if (type.isEnum() && type.isLocalType()) {
            final FieldBinding field = type.scope.enclosingMethodScope().initializedField;
            final FieldDeclaration decl = field.sourceField();
            final String[] arguments = { new String(decl.name) };
            this.handle(67109628, arguments, arguments, decl.sourceStart(), decl.sourceEnd());
        }
        else {
            final String[] arguments2 = { new String(type.sourceName()) };
            this.handle(16777549, arguments2, arguments2, type.sourceStart(), type.sourceEnd());
        }
    }
    
    public void abstractMethodMustBeImplemented(final SourceTypeBinding type, final MethodBinding abstractMethod) {
        if (type.isEnum() && type.isLocalType()) {
            final FieldBinding field = type.scope.enclosingMethodScope().initializedField;
            final FieldDeclaration decl = field.sourceField();
            this.handle(67109627, new String[] { new String(abstractMethod.selector), this.typesAsString(abstractMethod, false), new String(decl.name) }, new String[] { new String(abstractMethod.selector), this.typesAsString(abstractMethod, true), new String(decl.name) }, decl.sourceStart(), decl.sourceEnd());
        }
        else {
            this.handle(67109264, new String[] { new String(abstractMethod.selector), this.typesAsString(abstractMethod, false), new String(abstractMethod.declaringClass.readableName()), new String(type.readableName()) }, new String[] { new String(abstractMethod.selector), this.typesAsString(abstractMethod, true), new String(abstractMethod.declaringClass.shortReadableName()), new String(type.shortReadableName()) }, type.sourceStart(), type.sourceEnd());
        }
    }
    
    public void abstractMethodMustBeImplemented(final SourceTypeBinding type, final MethodBinding abstractMethod, final MethodBinding concreteMethod) {
        this.handle(67109282, new String[] { new String(abstractMethod.selector), this.typesAsString(abstractMethod, false), new String(abstractMethod.declaringClass.readableName()), new String(type.readableName()), new String(concreteMethod.selector), this.typesAsString(concreteMethod, false), new String(concreteMethod.declaringClass.readableName()) }, new String[] { new String(abstractMethod.selector), this.typesAsString(abstractMethod, true), new String(abstractMethod.declaringClass.shortReadableName()), new String(type.shortReadableName()), new String(concreteMethod.selector), this.typesAsString(concreteMethod, true), new String(concreteMethod.declaringClass.shortReadableName()) }, type.sourceStart(), type.sourceEnd());
    }
    
    public void abstractMethodNeedingNoBody(final AbstractMethodDeclaration method) {
        this.handle(603979889, ProblemReporter.NoArgument, ProblemReporter.NoArgument, method.sourceStart, method.sourceEnd, method, method.compilationResult());
    }
    
    public void alreadyDefinedLabel(final char[] labelName, final ASTNode location) {
        final String[] arguments = { new String(labelName) };
        this.handle(536871083, arguments, arguments, location.sourceStart, location.sourceEnd);
    }
    
    public void annotationCannotOverrideMethod(final MethodBinding overrideMethod, final MethodBinding inheritedMethod) {
        final ASTNode location = overrideMethod.sourceMethod();
        this.handle(67109480, new String[] { new String(overrideMethod.declaringClass.readableName()), new String(inheritedMethod.declaringClass.readableName()), new String(inheritedMethod.selector), this.typesAsString(inheritedMethod, false) }, new String[] { new String(overrideMethod.declaringClass.shortReadableName()), new String(inheritedMethod.declaringClass.shortReadableName()), new String(inheritedMethod.selector), this.typesAsString(inheritedMethod, true) }, location.sourceStart, location.sourceEnd);
    }
    
    public void annotationCircularity(final TypeBinding sourceType, final TypeBinding otherType, final TypeReference reference) {
        if (TypeBinding.equalsEquals(sourceType, otherType)) {
            this.handle(16777822, new String[] { new String(sourceType.readableName()) }, new String[] { new String(sourceType.shortReadableName()) }, reference.sourceStart, reference.sourceEnd);
        }
        else {
            this.handle(16777823, new String[] { new String(sourceType.readableName()), new String(otherType.readableName()) }, new String[] { new String(sourceType.shortReadableName()), new String(otherType.shortReadableName()) }, reference.sourceStart, reference.sourceEnd);
        }
    }
    
    public void annotationMembersCannotHaveParameters(final AnnotationMethodDeclaration annotationMethodDeclaration) {
        this.handle(1610613353, ProblemReporter.NoArgument, ProblemReporter.NoArgument, annotationMethodDeclaration.sourceStart, annotationMethodDeclaration.sourceEnd);
    }
    
    public void annotationMembersCannotHaveTypeParameters(final AnnotationMethodDeclaration annotationMethodDeclaration) {
        this.handle(1610613354, ProblemReporter.NoArgument, ProblemReporter.NoArgument, annotationMethodDeclaration.sourceStart, annotationMethodDeclaration.sourceEnd);
    }
    
    public void annotationTypeDeclarationCannotHaveConstructor(final ConstructorDeclaration constructorDeclaration) {
        this.handle(1610613360, ProblemReporter.NoArgument, ProblemReporter.NoArgument, constructorDeclaration.sourceStart, constructorDeclaration.sourceEnd);
    }
    
    public void annotationTypeDeclarationCannotHaveSuperclass(final TypeDeclaration typeDeclaration) {
        this.handle(1610613355, ProblemReporter.NoArgument, ProblemReporter.NoArgument, typeDeclaration.sourceStart, typeDeclaration.sourceEnd);
    }
    
    public void annotationTypeDeclarationCannotHaveSuperinterfaces(final TypeDeclaration typeDeclaration) {
        this.handle(1610613356, ProblemReporter.NoArgument, ProblemReporter.NoArgument, typeDeclaration.sourceStart, typeDeclaration.sourceEnd);
    }
    
    public void annotationTypeUsedAsSuperinterface(final SourceTypeBinding type, final TypeReference superInterfaceRef, final ReferenceBinding superType) {
        this.handle(16777842, new String[] { new String(superType.readableName()), new String(type.sourceName()) }, new String[] { new String(superType.shortReadableName()), new String(type.sourceName()) }, superInterfaceRef.sourceStart, superInterfaceRef.sourceEnd);
    }
    
    public void annotationValueMustBeAnnotation(final TypeBinding annotationType, final char[] name, final Expression value, final TypeBinding expectedType) {
        final String str = new String(name);
        this.handle(536871537, new String[] { new String(annotationType.readableName()), str, new String(expectedType.readableName()) }, new String[] { new String(annotationType.shortReadableName()), str, new String(expectedType.readableName()) }, value.sourceStart, value.sourceEnd);
    }
    
    public void annotationValueMustBeArrayInitializer(final TypeBinding annotationType, final char[] name, final Expression value) {
        final String str = new String(name);
        this.handle(536871544, new String[] { new String(annotationType.readableName()), str }, new String[] { new String(annotationType.shortReadableName()), str }, value.sourceStart, value.sourceEnd);
    }
    
    public void annotationValueMustBeClassLiteral(final TypeBinding annotationType, final char[] name, final Expression value) {
        final String str = new String(name);
        this.handle(536871524, new String[] { new String(annotationType.readableName()), str }, new String[] { new String(annotationType.shortReadableName()), str }, value.sourceStart, value.sourceEnd);
    }
    
    public void annotationValueMustBeConstant(final TypeBinding annotationType, final char[] name, final Expression value, final boolean isEnum) {
        final String str = new String(name);
        if (isEnum) {
            this.handle(536871545, new String[] { new String(annotationType.readableName()), str }, new String[] { new String(annotationType.shortReadableName()), str }, value.sourceStart, value.sourceEnd);
        }
        else {
            this.handle(536871525, new String[] { new String(annotationType.readableName()), str }, new String[] { new String(annotationType.shortReadableName()), str }, value.sourceStart, value.sourceEnd);
        }
    }
    
    public void anonymousClassCannotExtendFinalClass(final TypeReference reference, final TypeBinding type) {
        this.handle(16777245, new String[] { new String(type.readableName()) }, new String[] { new String(type.shortReadableName()) }, reference.sourceStart, reference.sourceEnd);
    }
    
    public void argumentTypeCannotBeVoid(final ASTNode methodDecl, final Argument arg) {
        final String[] arguments = { new String(arg.name) };
        this.handle(67109228, arguments, arguments, methodDecl.sourceStart, methodDecl.sourceEnd);
    }
    
    public void argumentTypeCannotBeVoidArray(final Argument arg) {
        this.handle(536870966, ProblemReporter.NoArgument, ProblemReporter.NoArgument, arg.type.sourceStart, arg.type.sourceEnd);
    }
    
    public void arrayConstantsOnlyInArrayInitializers(final int sourceStart, final int sourceEnd) {
        this.handle(1610612944, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    public void assignmentHasNoEffect(final AbstractVariableDeclaration location, final char[] name) {
        final int severity = this.computeSeverity(536871090);
        if (severity == 256) {
            return;
        }
        final String[] arguments = { new String(name) };
        final int start = location.sourceStart;
        int end = location.sourceEnd;
        if (location.initialization != null) {
            end = location.initialization.sourceEnd;
        }
        this.handle(536871090, arguments, arguments, severity, start, end);
    }
    
    public void assignmentHasNoEffect(final Assignment location, final char[] name) {
        final int severity = this.computeSeverity(536871090);
        if (severity == 256) {
            return;
        }
        final String[] arguments = { new String(name) };
        this.handle(536871090, arguments, arguments, severity, location.sourceStart, location.sourceEnd);
    }
    
    public void attemptToReturnNonVoidExpression(final ReturnStatement returnStatement, final TypeBinding expectedType) {
        this.handle(67108969, new String[] { new String(expectedType.readableName()) }, new String[] { new String(expectedType.shortReadableName()) }, returnStatement.sourceStart, returnStatement.sourceEnd);
    }
    
    public void attemptToReturnVoidValue(final ReturnStatement returnStatement) {
        this.handle(67108970, ProblemReporter.NoArgument, ProblemReporter.NoArgument, returnStatement.sourceStart, returnStatement.sourceEnd);
    }
    
    public void autoboxing(final Expression expression, final TypeBinding originalType, final TypeBinding convertedType) {
        if (this.options.getSeverity(536871168) == 256) {
            return;
        }
        this.handle(originalType.isBaseType() ? 536871632 : 536871633, new String[] { new String(originalType.readableName()), new String(convertedType.readableName()) }, new String[] { new String(originalType.shortReadableName()), new String(convertedType.shortReadableName()) }, expression.sourceStart, expression.sourceEnd);
    }
    
    public void boundCannotBeArray(final ASTNode location, final TypeBinding type) {
        this.handle(16777784, new String[] { new String(type.readableName()) }, new String[] { new String(type.shortReadableName()) }, location.sourceStart, location.sourceEnd);
    }
    
    public void boundMustBeAnInterface(final ASTNode location, final TypeBinding type) {
        this.handle(16777745, new String[] { new String(type.readableName()) }, new String[] { new String(type.shortReadableName()) }, location.sourceStart, location.sourceEnd);
    }
    
    public void bytecodeExceeds64KLimit(final MethodBinding method, final int start, final int end) {
        this.handle(536870975, new String[] { new String(method.selector), this.typesAsString(method, false) }, new String[] { new String(method.selector), this.typesAsString(method, true) }, 159, start, end);
    }
    
    public void bytecodeExceeds64KLimit(final AbstractMethodDeclaration location) {
        final MethodBinding method = location.binding;
        if (location.isConstructor()) {
            this.handle(536870981, new String[] { new String(location.selector), this.typesAsString(method, false) }, new String[] { new String(location.selector), this.typesAsString(method, true) }, 159, location.sourceStart, location.sourceEnd);
        }
        else {
            this.bytecodeExceeds64KLimit(method, location.sourceStart, location.sourceEnd);
        }
    }
    
    public void bytecodeExceeds64KLimit(final LambdaExpression location) {
        this.bytecodeExceeds64KLimit(location.binding, location.sourceStart, location.diagnosticsSourceEnd());
    }
    
    public void bytecodeExceeds64KLimit(final TypeDeclaration location) {
        this.handle(536870976, ProblemReporter.NoArgument, ProblemReporter.NoArgument, 159, location.sourceStart, location.sourceEnd);
    }
    
    public void cannotAllocateVoidArray(final Expression expression) {
        this.handle(536870966, ProblemReporter.NoArgument, ProblemReporter.NoArgument, expression.sourceStart, expression.sourceEnd);
    }
    
    public void cannotAssignToFinalField(final FieldBinding field, final ASTNode location) {
        this.handle(33554512, new String[] { (field.declaringClass == null) ? "array" : new String(field.declaringClass.readableName()), new String(field.readableName()) }, new String[] { (field.declaringClass == null) ? "array" : new String(field.declaringClass.shortReadableName()), new String(field.shortReadableName()) }, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
    }
    
    public void cannotAssignToFinalLocal(final LocalVariableBinding local, final ASTNode location) {
        int problemId = 0;
        if ((local.tagBits & 0x1000L) != 0x0L) {
            problemId = 536871782;
        }
        else if ((local.tagBits & 0x2000L) != 0x0L) {
            problemId = 536871784;
        }
        else {
            problemId = 536870970;
        }
        final String[] arguments = { new String(local.readableName()) };
        this.handle(problemId, arguments, arguments, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }
    
    public void cannotAssignToFinalOuterLocal(final LocalVariableBinding local, final ASTNode location) {
        final String[] arguments = { new String(local.readableName()) };
        this.handle(536870972, arguments, arguments, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }
    
    public void cannotDefineDimensionsAndInitializer(final ArrayAllocationExpression expresssion) {
        this.handle(536871070, ProblemReporter.NoArgument, ProblemReporter.NoArgument, expresssion.sourceStart, expresssion.sourceEnd);
    }
    
    public void cannotDireclyInvokeAbstractMethod(final ASTNode invocationSite, final MethodBinding method) {
        this.handle(67108968, new String[] { new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false) }, new String[] { new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true) }, invocationSite.sourceStart, invocationSite.sourceEnd);
    }
    
    public void cannotExtendEnum(final SourceTypeBinding type, final TypeReference superclass, final TypeBinding superTypeBinding) {
        final String name = new String(type.sourceName());
        final String superTypeFullName = new String(superTypeBinding.readableName());
        String superTypeShortName = new String(superTypeBinding.shortReadableName());
        if (superTypeShortName.equals(name)) {
            superTypeShortName = superTypeFullName;
        }
        this.handle(16777972, new String[] { superTypeFullName, name }, new String[] { superTypeShortName, name }, superclass.sourceStart, superclass.sourceEnd);
    }
    
    public void cannotImportPackage(final ImportReference importRef) {
        final String[] arguments = { CharOperation.toString(importRef.tokens) };
        this.handleUntagged(268435843, arguments, arguments, importRef.sourceStart, importRef.sourceEnd);
    }
    
    public void cannotInstantiate(final Expression typeRef, final TypeBinding type) {
        this.handle(16777373, new String[] { new String(type.readableName()) }, new String[] { new String(type.shortReadableName()) }, typeRef.sourceStart, typeRef.sourceEnd);
    }
    
    public void cannotInvokeSuperConstructorInEnum(final ExplicitConstructorCall constructorCall, final MethodBinding enumConstructor) {
        this.handle(67109621, new String[] { new String(enumConstructor.declaringClass.sourceName()), this.typesAsString(enumConstructor, false) }, new String[] { new String(enumConstructor.declaringClass.sourceName()), this.typesAsString(enumConstructor, true) }, constructorCall.sourceStart, constructorCall.sourceEnd);
    }
    
    public void cannotReadSource(final CompilationUnitDeclaration unit, final AbortCompilationUnit abortException, final boolean verbose) {
        final String fileName = new String(unit.compilationResult.fileName);
        if (abortException.exception instanceof CharConversionException) {
            String encoding = abortException.encoding;
            if (encoding == null) {
                encoding = System.getProperty("file.encoding");
            }
            final String[] arguments = { fileName, encoding };
            this.handle(536871613, arguments, arguments, 0, 0);
            return;
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        if (verbose) {
            abortException.exception.printStackTrace(writer);
            System.err.println(stringWriter.toString());
            stringWriter = new StringWriter();
            writer = new PrintWriter(stringWriter);
        }
        writer.print(abortException.exception.getClass().getName());
        writer.print(':');
        writer.print(abortException.exception.getMessage());
        final String exceptionTrace = stringWriter.toString();
        final String[] arguments2 = { fileName, exceptionTrace };
        this.handle(536871614, arguments2, arguments2, 0, 0);
    }
    
    public void cannotReferToNonFinalOuterLocal(final LocalVariableBinding local, final ASTNode location) {
        final String[] arguments = { new String(local.readableName()) };
        this.handle(536870937, arguments, arguments, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }
    
    public void cannotReferToNonEffectivelyFinalOuterLocal(final LocalVariableBinding local, final ASTNode location) {
        final String[] arguments = { new String(local.readableName()) };
        this.handle(536871575, arguments, arguments, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }
    
    public void cannotReturnInInitializer(final ASTNode location) {
        this.handle(536871074, ProblemReporter.NoArgument, ProblemReporter.NoArgument, location.sourceStart, location.sourceEnd);
    }
    
    public void cannotThrowNull(final ASTNode expression) {
        this.handle(536871089, ProblemReporter.NoArgument, ProblemReporter.NoArgument, expression.sourceStart, expression.sourceEnd);
    }
    
    public void cannotThrowType(final ASTNode exception, final TypeBinding expectedType) {
        this.handle(16777536, new String[] { new String(expectedType.readableName()) }, new String[] { new String(expectedType.shortReadableName()) }, exception.sourceStart, exception.sourceEnd);
    }
    
    public void illegalArrayOfUnionType(final char[] identifierName, final TypeReference typeReference) {
        this.handle(16777878, ProblemReporter.NoArgument, ProblemReporter.NoArgument, typeReference.sourceStart, typeReference.sourceEnd);
    }
    
    public void cannotUseQualifiedEnumConstantInCaseLabel(final Reference location, final FieldBinding field) {
        this.handle(33555187, new String[] { String.valueOf(field.declaringClass.readableName()), String.valueOf(field.name) }, new String[] { String.valueOf(field.declaringClass.shortReadableName()), String.valueOf(field.name) }, location.sourceStart(), location.sourceEnd());
    }
    
    public void cannotUseSuperInCodeSnippet(final int start, final int end) {
        this.handle(536871334, ProblemReporter.NoArgument, ProblemReporter.NoArgument, 159, start, end);
    }
    
    public void cannotUseSuperInJavaLangObject(final ASTNode reference) {
        this.handle(16777217, ProblemReporter.NoArgument, ProblemReporter.NoArgument, reference.sourceStart, reference.sourceEnd);
    }
    
    public void targetTypeIsNotAFunctionalInterface(final FunctionalExpression target) {
        this.handle(553648781, ProblemReporter.NoArgument, ProblemReporter.NoArgument, target.sourceStart, target.diagnosticsSourceEnd());
    }
    
    public void illFormedParameterizationOfFunctionalInterface(final FunctionalExpression target) {
        this.handle(553648783, ProblemReporter.NoArgument, ProblemReporter.NoArgument, target.sourceStart, target.diagnosticsSourceEnd());
    }
    
    public void lambdaSignatureMismatched(final LambdaExpression target) {
        this.handle(553648784, new String[] { new String(target.descriptor.readableName()) }, new String[] { new String(target.descriptor.shortReadableName()) }, target.sourceStart, target.diagnosticsSourceEnd());
    }
    
    public void lambdaParameterTypeMismatched(final Argument argument, final TypeReference type, final TypeBinding expectedParameterType) {
        final String name = new String(argument.name);
        final String expectedTypeFullName = new String(expectedParameterType.readableName());
        final String expectedTypeShortName = new String(expectedParameterType.shortReadableName());
        this.handle(expectedParameterType.isTypeVariable() ? 553648786 : 553648785, new String[] { name, expectedTypeFullName }, new String[] { name, expectedTypeShortName }, type.sourceStart, type.sourceEnd);
    }
    
    public void lambdaExpressionCannotImplementGenericMethod(final LambdaExpression lambda, final MethodBinding sam) {
        final String selector = new String(sam.selector);
        this.handle(553648787, new String[] { selector, new String(sam.declaringClass.readableName()) }, new String[] { selector, new String(sam.declaringClass.shortReadableName()) }, lambda.sourceStart, lambda.diagnosticsSourceEnd());
    }
    
    public void caseExpressionMustBeConstant(final Expression expression) {
        this.handle(536871065, ProblemReporter.NoArgument, ProblemReporter.NoArgument, expression.sourceStart, expression.sourceEnd);
    }
    
    public void classExtendFinalClass(final SourceTypeBinding type, final TypeReference superclass, final TypeBinding superTypeBinding) {
        final String name = new String(type.sourceName());
        final String superTypeFullName = new String(superTypeBinding.readableName());
        String superTypeShortName = new String(superTypeBinding.shortReadableName());
        if (superTypeShortName.equals(name)) {
            superTypeShortName = superTypeFullName;
        }
        this.handle(16777529, new String[] { superTypeFullName, name }, new String[] { superTypeShortName, name }, superclass.sourceStart, superclass.sourceEnd);
    }
    
    public void codeSnippetMissingClass(final String missing, final int start, final int end) {
        final String[] arguments = { missing };
        this.handle(536871332, arguments, arguments, 159, start, end);
    }
    
    public void codeSnippetMissingMethod(final String className, final String missingMethod, final String argumentTypes, final int start, final int end) {
        final String[] arguments = { className, missingMethod, argumentTypes };
        this.handle(536871333, arguments, arguments, 159, start, end);
    }
    
    public void comparingIdenticalExpressions(final Expression comparison) {
        final int severity = this.computeSeverity(536871123);
        if (severity == 256) {
            return;
        }
        this.handle(536871123, ProblemReporter.NoArgument, ProblemReporter.NoArgument, severity, comparison.sourceStart, comparison.sourceEnd);
    }
    
    @Override
    public int computeSeverity(final int problemID) {
        switch (problemID) {
            case 67109667: {
                return 0;
            }
            case 16777538: {
                return 0;
            }
            case -1610612270:
            case -1610612268:
            case -1610612264:
            case -1610612263:
            case -1610612262:
            case -1610612258:
            case -1610612256:
            case -1610612255:
            case -1610612254:
            case -1610612248:
            case -1610612246:
            case -1610612244:
            case -1610612242:
            case -1610612240:
            case -1610612238:
            case -1610612236:
            case -1610612235:
            case -1610612234:
            case -1610612233:
            case -1610612231:
            case -1610612229:
            case -1610612228:
            case -1610612227:
            case -1610612226:
            case -1610612225:
            case -1610612219:
            case -1610611886:
            case -1610611885:
            case -1610611884:
            case -1610611883:
            case -1610611882:
            case -1610611881:
            case -1610611880:
            case -1610611879:
            case -1610611878:
            case -1610611877: {
                if (!this.options.reportInvalidJavadocTags) {
                    return 256;
                }
                break;
            }
            case -1610612245:
            case -1610612241:
            case -1610612237:
            case -1610612230: {
                if (!this.options.reportInvalidJavadocTags || !this.options.reportInvalidJavadocTagsDeprecatedRef) {
                    return 256;
                }
                break;
            }
            case -1610612271:
            case -1610612247:
            case -1610612243:
            case -1610612239:
            case -1610612232: {
                if (!this.options.reportInvalidJavadocTags || !this.options.reportInvalidJavadocTagsNotVisibleRef) {
                    return 256;
                }
                break;
            }
            case -1610612220: {
                if ("no_tag".equals(this.options.reportMissingJavadocTagDescription)) {
                    return 256;
                }
                break;
            }
            case -1610612273: {
                if (!"all_standard_tags".equals(this.options.reportMissingJavadocTagDescription)) {
                    return 256;
                }
                break;
            }
            case 16778125:
            case 1610613402: {
                return 0;
            }
            case 1610613179: {
                return this.underScoreIsLambdaParameter ? 1 : 0;
            }
        }
        final int irritant = getIrritant(problemID);
        if (irritant == 0) {
            return 129;
        }
        if ((problemID & Integer.MIN_VALUE) != 0x0 && !this.options.docCommentSupport) {
            return 256;
        }
        return this.options.getSeverity(irritant);
    }
    
    public void conditionalArgumentsIncompatibleTypes(final ConditionalExpression expression, final TypeBinding trueType, final TypeBinding falseType) {
        this.handle(16777232, new String[] { new String(trueType.readableName()), new String(falseType.readableName()) }, new String[] { new String(trueType.shortReadableName()), new String(falseType.shortReadableName()) }, expression.sourceStart, expression.sourceEnd);
    }
    
    public void conflictingImport(final ImportReference importRef) {
        final String[] arguments = { CharOperation.toString(importRef.tokens) };
        this.handleUntagged(268435841, arguments, arguments, importRef.sourceStart, importRef.sourceEnd);
    }
    
    public void constantOutOfRange(final Literal literal, final TypeBinding literalType) {
        final String[] arguments = { new String(literalType.readableName()), new String(literal.source()) };
        this.handle(536871066, arguments, arguments, literal.sourceStart, literal.sourceEnd);
    }
    
    public void corruptedSignature(final TypeBinding enclosingType, final char[] signature, final int position) {
        this.handle(536871612, new String[] { new String(enclosingType.readableName()), new String(signature), String.valueOf(position) }, new String[] { new String(enclosingType.shortReadableName()), new String(signature), String.valueOf(position) }, 159, 0, 0);
    }
    
    public void defaultMethodOverridesObjectMethod(final MethodBinding currentMethod) {
        final AbstractMethodDeclaration method = currentMethod.sourceMethod();
        int sourceStart = 0;
        int sourceEnd = 0;
        if (method != null) {
            sourceStart = method.sourceStart;
            sourceEnd = method.sourceEnd;
        }
        this.handle(67109915, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    public void defaultModifierIllegallySpecified(final int sourceStart, final int sourceEnd) {
        this.handle(67109922, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    public void deprecatedField(final FieldBinding field, final ASTNode location) {
        final int severity = this.computeSeverity(33554505);
        if (severity == 256) {
            return;
        }
        this.handle(33554505, new String[] { new String(field.declaringClass.readableName()), new String(field.name) }, new String[] { new String(field.declaringClass.shortReadableName()), new String(field.name) }, severity, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
    }
    
    public void deprecatedMethod(final MethodBinding method, final ASTNode location) {
        final boolean isConstructor = method.isConstructor();
        final int severity = this.computeSeverity(isConstructor ? 134217861 : 67108967);
        if (severity == 256) {
            return;
        }
        if (isConstructor) {
            int start = -1;
            if (location instanceof AllocationExpression) {
                final AllocationExpression allocationExpression = (AllocationExpression)location;
                if (allocationExpression.enumConstant != null) {
                    start = allocationExpression.enumConstant.sourceStart;
                }
                start = allocationExpression.type.sourceStart;
            }
            this.handle(134217861, new String[] { new String(method.declaringClass.readableName()), this.typesAsString(method, false) }, new String[] { new String(method.declaringClass.shortReadableName()), this.typesAsString(method, true) }, severity, (start == -1) ? location.sourceStart : start, location.sourceEnd);
        }
        else {
            int start = -1;
            if (location instanceof MessageSend) {
                start = (int)(((MessageSend)location).nameSourcePosition >>> 32);
            }
            this.handle(67108967, new String[] { new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false) }, new String[] { new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true) }, severity, (start == -1) ? location.sourceStart : start, location.sourceEnd);
        }
    }
    
    public void deprecatedType(final TypeBinding type, final ASTNode location) {
        this.deprecatedType(type, location, Integer.MAX_VALUE);
    }
    
    public void deprecatedType(TypeBinding type, final ASTNode location, final int index) {
        if (location == null) {
            return;
        }
        final int severity = this.computeSeverity(16777221);
        if (severity == 256) {
            return;
        }
        type = type.leafComponentType();
        int sourceStart = -1;
        if (location instanceof QualifiedTypeReference) {
            final QualifiedTypeReference ref = (QualifiedTypeReference)location;
            if (index < Integer.MAX_VALUE) {
                sourceStart = (int)(ref.sourcePositions[index] >> 32);
            }
        }
        this.handle(16777221, new String[] { new String(type.readableName()) }, new String[] { new String(type.shortReadableName()) }, severity, (sourceStart == -1) ? location.sourceStart : sourceStart, this.nodeSourceEnd(null, location, index));
    }
    
    public void disallowedTargetForAnnotation(final Annotation annotation) {
        this.handle(16777838, new String[] { new String(annotation.resolvedType.readableName()) }, new String[] { new String(annotation.resolvedType.shortReadableName()) }, annotation.sourceStart, annotation.sourceEnd);
    }
    
    public void explitAnnotationTargetRequired(final Annotation annotation) {
        this.handle(16777865, ProblemReporter.NoArgument, ProblemReporter.NoArgument, annotation.sourceStart, annotation.sourceEnd);
    }
    
    public void polymorphicMethodNotBelow17(final ASTNode node) {
        this.handle(67109740, ProblemReporter.NoArgument, ProblemReporter.NoArgument, node.sourceStart, node.sourceEnd);
    }
    
    public void multiCatchNotBelow17(final ASTNode node) {
        this.handle(1610613611, ProblemReporter.NoArgument, ProblemReporter.NoArgument, node.sourceStart, node.sourceEnd);
    }
    
    public void duplicateAnnotation(final Annotation annotation, final long sourceLevel) {
        this.handle((sourceLevel >= 3407872L) ? 16778113 : 16777824, new String[] { new String(annotation.resolvedType.readableName()) }, new String[] { new String(annotation.resolvedType.shortReadableName()) }, annotation.sourceStart, annotation.sourceEnd);
    }
    
    public void duplicateAnnotationValue(final TypeBinding annotationType, final MemberValuePair memberValuePair) {
        final String name = new String(memberValuePair.name);
        this.handle(536871522, new String[] { name, new String(annotationType.readableName()) }, new String[] { name, new String(annotationType.shortReadableName()) }, memberValuePair.sourceStart, memberValuePair.sourceEnd);
    }
    
    public void duplicateBounds(final ASTNode location, final TypeBinding type) {
        this.handle(16777783, new String[] { new String(type.readableName()) }, new String[] { new String(type.shortReadableName()) }, location.sourceStart, location.sourceEnd);
    }
    
    public void duplicateCase(final CaseStatement caseStatement) {
        this.handle(33554602, ProblemReporter.NoArgument, ProblemReporter.NoArgument, caseStatement.sourceStart, caseStatement.sourceEnd);
    }
    
    public void duplicateDefaultCase(final ASTNode statement) {
        this.handle(536871078, ProblemReporter.NoArgument, ProblemReporter.NoArgument, statement.sourceStart, statement.sourceEnd);
    }
    
    public void duplicateEnumSpecialMethod(final SourceTypeBinding type, final AbstractMethodDeclaration methodDecl) {
        final MethodBinding method = methodDecl.binding;
        this.handle(67109618, new String[] { new String(methodDecl.selector), new String(method.declaringClass.readableName()), this.typesAsString(method, false) }, new String[] { new String(methodDecl.selector), new String(method.declaringClass.shortReadableName()), this.typesAsString(method, true) }, methodDecl.sourceStart, methodDecl.sourceEnd);
    }
    
    public void duplicateFieldInType(final SourceTypeBinding type, final FieldDeclaration fieldDecl) {
        this.handle(33554772, new String[] { new String(type.sourceName()), new String(fieldDecl.name) }, new String[] { new String(type.shortReadableName()), new String(fieldDecl.name) }, fieldDecl.sourceStart, fieldDecl.sourceEnd);
    }
    
    public void duplicateImport(final ImportReference importRef) {
        final String[] arguments = { CharOperation.toString(importRef.tokens) };
        this.handleUntagged(268435842, arguments, arguments, importRef.sourceStart, importRef.sourceEnd);
    }
    
    public void duplicateInheritedMethods(final SourceTypeBinding type, final MethodBinding inheritedMethod1, final MethodBinding inheritedMethod2, final boolean isJava8) {
        if (TypeBinding.notEquals(inheritedMethod1.declaringClass, inheritedMethod2.declaringClass)) {
            int problemID = 67109447;
            if (inheritedMethod1.isDefaultMethod() && inheritedMethod2.isDefaultMethod()) {
                if (!isJava8) {
                    return;
                }
                problemID = 67109917;
            }
            this.handle(problemID, new String[] { new String(inheritedMethod1.selector), this.typesAsString(inheritedMethod1, inheritedMethod1.original().parameters, false), this.typesAsString(inheritedMethod2, inheritedMethod2.original().parameters, false), new String(inheritedMethod1.declaringClass.readableName()), new String(inheritedMethod2.declaringClass.readableName()) }, new String[] { new String(inheritedMethod1.selector), this.typesAsString(inheritedMethod1, inheritedMethod1.original().parameters, true), this.typesAsString(inheritedMethod2, inheritedMethod2.original().parameters, true), new String(inheritedMethod1.declaringClass.shortReadableName()), new String(inheritedMethod2.declaringClass.shortReadableName()) }, type.sourceStart(), type.sourceEnd());
            return;
        }
        this.handle(67109429, new String[] { new String(inheritedMethod1.selector), new String(inheritedMethod1.declaringClass.readableName()), this.typesAsString(inheritedMethod1, inheritedMethod1.original().parameters, false), this.typesAsString(inheritedMethod2, inheritedMethod2.original().parameters, false) }, new String[] { new String(inheritedMethod1.selector), new String(inheritedMethod1.declaringClass.shortReadableName()), this.typesAsString(inheritedMethod1, inheritedMethod1.original().parameters, true), this.typesAsString(inheritedMethod2, inheritedMethod2.original().parameters, true) }, type.sourceStart(), type.sourceEnd());
    }
    
    public void duplicateInitializationOfBlankFinalField(final FieldBinding field, final Reference reference) {
        final String[] arguments = { new String(field.readableName()) };
        this.handle(33554514, arguments, arguments, this.nodeSourceStart(field, reference), this.nodeSourceEnd(field, reference));
    }
    
    public void duplicateInitializationOfFinalLocal(final LocalVariableBinding local, final ASTNode location) {
        final String[] arguments = { new String(local.readableName()) };
        this.handle(536870969, arguments, arguments, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }
    
    public void duplicateMethodInType(final AbstractMethodDeclaration methodDecl, final boolean equalParameters, final int severity) {
        final MethodBinding method = methodDecl.binding;
        if (equalParameters) {
            this.handle(67109219, new String[] { new String(methodDecl.selector), new String(method.declaringClass.readableName()), this.typesAsString(method, false) }, new String[] { new String(methodDecl.selector), new String(method.declaringClass.shortReadableName()), this.typesAsString(method, true) }, severity, methodDecl.sourceStart, methodDecl.sourceEnd);
        }
        else {
            this.handle(16777743, new String[] { new String(methodDecl.selector), new String(method.declaringClass.readableName()), this.typesAsString(method, false) }, new String[] { new String(methodDecl.selector), new String(method.declaringClass.shortReadableName()), this.typesAsString(method, true) }, severity, methodDecl.sourceStart, methodDecl.sourceEnd);
        }
    }
    
    public void duplicateModifierForField(final ReferenceBinding type, final FieldDeclaration fieldDecl) {
        final String[] arguments = { new String(fieldDecl.name) };
        this.handle(33554773, arguments, arguments, fieldDecl.sourceStart, fieldDecl.sourceEnd);
    }
    
    public void duplicateModifierForMethod(final ReferenceBinding type, final AbstractMethodDeclaration methodDecl) {
        this.handle(67109221, new String[] { new String(type.sourceName()), new String(methodDecl.selector) }, new String[] { new String(type.shortReadableName()), new String(methodDecl.selector) }, methodDecl.sourceStart, methodDecl.sourceEnd);
    }
    
    public void duplicateModifierForType(final SourceTypeBinding type) {
        final String[] arguments = { new String(type.sourceName()) };
        this.handle(16777517, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }
    
    public void duplicateModifierForVariable(final LocalDeclaration localDecl, final boolean complainForArgument) {
        final String[] arguments = { new String(localDecl.name) };
        this.handle(complainForArgument ? 67109232 : 67109259, arguments, arguments, localDecl.sourceStart, localDecl.sourceEnd);
    }
    
    public void duplicateNestedType(final TypeDeclaration typeDecl) {
        final String[] arguments = { new String(typeDecl.name) };
        this.handle(16777535, arguments, arguments, typeDecl.sourceStart, typeDecl.sourceEnd);
    }
    
    public void duplicateSuperinterface(final SourceTypeBinding type, final TypeReference reference, final ReferenceBinding superType) {
        this.handle(16777530, new String[] { new String(superType.readableName()), new String(type.sourceName()) }, new String[] { new String(superType.shortReadableName()), new String(type.sourceName()) }, reference.sourceStart, reference.sourceEnd);
    }
    
    public void duplicateTargetInTargetAnnotation(final TypeBinding annotationType, final NameReference reference) {
        final FieldBinding field = reference.fieldBinding();
        final String name = new String(field.name);
        this.handle(536871533, new String[] { name, new String(annotationType.readableName()) }, new String[] { name, new String(annotationType.shortReadableName()) }, this.nodeSourceStart(field, reference), this.nodeSourceEnd(field, reference));
    }
    
    public void duplicateTypeParameterInType(final TypeParameter typeParameter) {
        this.handle(536871432, new String[] { new String(typeParameter.name) }, new String[] { new String(typeParameter.name) }, typeParameter.sourceStart, typeParameter.sourceEnd);
    }
    
    public void duplicateTypes(final CompilationUnitDeclaration compUnitDecl, final TypeDeclaration typeDecl) {
        final String[] arguments = { new String(compUnitDecl.getFileName()), new String(typeDecl.name) };
        this.referenceContext = typeDecl;
        int end = typeDecl.sourceEnd;
        if (end <= 0) {
            end = -1;
        }
        this.handle(16777539, arguments, arguments, typeDecl.sourceStart, end, compUnitDecl.compilationResult);
    }
    
    public void emptyControlFlowStatement(final int sourceStart, final int sourceEnd) {
        this.handle(553648316, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    public void enumAbstractMethodMustBeImplemented(final AbstractMethodDeclaration method) {
        final MethodBinding abstractMethod = method.binding;
        this.handle(67109622, new String[] { new String(abstractMethod.selector), this.typesAsString(abstractMethod, false), new String(abstractMethod.declaringClass.readableName()) }, new String[] { new String(abstractMethod.selector), this.typesAsString(abstractMethod, true), new String(abstractMethod.declaringClass.shortReadableName()) }, method.sourceStart(), method.sourceEnd());
    }
    
    public void enumConstantMustImplementAbstractMethod(final AbstractMethodDeclaration method, final FieldDeclaration field) {
        final MethodBinding abstractMethod = method.binding;
        this.handle(67109627, new String[] { new String(abstractMethod.selector), this.typesAsString(abstractMethod, false), new String(field.name) }, new String[] { new String(abstractMethod.selector), this.typesAsString(abstractMethod, true), new String(field.name) }, field.sourceStart(), field.sourceEnd());
    }
    
    public void enumConstantsCannotBeSurroundedByParenthesis(final Expression expression) {
        this.handle(1610613178, ProblemReporter.NoArgument, ProblemReporter.NoArgument, expression.sourceStart, expression.sourceEnd);
    }
    
    public void enumStaticFieldUsedDuringInitialization(final FieldBinding field, final ASTNode location) {
        this.handle(33555194, new String[] { new String(field.declaringClass.readableName()), new String(field.name) }, new String[] { new String(field.declaringClass.shortReadableName()), new String(field.name) }, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
    }
    
    public void enumSwitchCannotTargetField(final Reference reference, final FieldBinding field) {
        this.handle(33555191, new String[] { String.valueOf(field.declaringClass.readableName()), String.valueOf(field.name) }, new String[] { String.valueOf(field.declaringClass.shortReadableName()), String.valueOf(field.name) }, this.nodeSourceStart(field, reference), this.nodeSourceEnd(field, reference));
    }
    
    public void errorNoMethodFor(final MessageSend messageSend, final TypeBinding recType, final TypeBinding[] params) {
        final StringBuffer buffer = new StringBuffer();
        final StringBuffer shortBuffer = new StringBuffer();
        for (int i = 0, length = params.length; i < length; ++i) {
            if (i != 0) {
                buffer.append(", ");
                shortBuffer.append(", ");
            }
            buffer.append(new String(params[i].readableName()));
            shortBuffer.append(new String(params[i].shortReadableName()));
        }
        final int id = recType.isArrayType() ? 67108980 : 67108978;
        this.handle(id, new String[] { new String(recType.readableName()), new String(messageSend.selector), buffer.toString() }, new String[] { new String(recType.shortReadableName()), new String(messageSend.selector), shortBuffer.toString() }, messageSend.sourceStart, messageSend.sourceEnd);
    }
    
    public void errorNoMethodFor(final Expression expression, final TypeBinding recType, final char[] selector, final TypeBinding[] params) {
        final StringBuffer buffer = new StringBuffer();
        final StringBuffer shortBuffer = new StringBuffer();
        for (int i = 0, length = params.length; i < length; ++i) {
            if (i != 0) {
                buffer.append(", ");
                shortBuffer.append(", ");
            }
            buffer.append(new String(params[i].readableName()));
            shortBuffer.append(new String(params[i].shortReadableName()));
        }
        final int id = recType.isArrayType() ? 67108980 : 67108978;
        this.handle(id, new String[] { new String(recType.readableName()), new String(selector), buffer.toString() }, new String[] { new String(recType.shortReadableName()), new String(selector), shortBuffer.toString() }, expression.sourceStart, expression.sourceEnd);
    }
    
    public void errorThisSuperInStatic(final ASTNode reference) {
        final String[] arguments = { reference.isSuper() ? "super" : "this" };
        this.handle(536871112, arguments, arguments, reference.sourceStart, reference.sourceEnd);
    }
    
    public void errorNoSuperInInterface(final ASTNode reference) {
        this.handle(1610612962, ProblemReporter.NoArgument, ProblemReporter.NoArgument, reference.sourceStart, reference.sourceEnd);
    }
    
    public void expressionShouldBeAVariable(final Expression expression) {
        this.handle(1610612959, ProblemReporter.NoArgument, ProblemReporter.NoArgument, expression.sourceStart, expression.sourceEnd);
    }
    
    public void fakeReachable(final ASTNode location) {
        int sourceStart = location.sourceStart;
        int sourceEnd = location.sourceEnd;
        if (location instanceof LocalDeclaration) {
            final LocalDeclaration declaration = (LocalDeclaration)location;
            sourceStart = declaration.declarationSourceStart;
            sourceEnd = declaration.declarationSourceEnd;
        }
        this.handle(536871061, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    public void fieldHiding(final FieldDeclaration fieldDecl, final Binding hiddenVariable) {
        final FieldBinding field = fieldDecl.binding;
        if (CharOperation.equals(TypeConstants.SERIALVERSIONUID, field.name) && field.isStatic() && field.isPrivate() && field.isFinal() && TypeBinding.equalsEquals(TypeBinding.LONG, field.type)) {
            final ReferenceBinding referenceBinding = field.declaringClass;
            if (referenceBinding != null && referenceBinding.findSuperTypeOriginatingFrom(37, false) != null) {
                return;
            }
        }
        if (CharOperation.equals(TypeConstants.SERIALPERSISTENTFIELDS, field.name) && field.isStatic() && field.isPrivate() && field.isFinal() && field.type.dimensions() == 1 && CharOperation.equals(TypeConstants.CharArray_JAVA_IO_OBJECTSTREAMFIELD, field.type.leafComponentType().readableName())) {
            final ReferenceBinding referenceBinding = field.declaringClass;
            if (referenceBinding != null && referenceBinding.findSuperTypeOriginatingFrom(37, false) != null) {
                return;
            }
        }
        final boolean isLocal = hiddenVariable instanceof LocalVariableBinding;
        final int severity = this.computeSeverity(isLocal ? 570425436 : 570425437);
        if (severity == 256) {
            return;
        }
        if (isLocal) {
            this.handle(570425436, new String[] { new String(field.declaringClass.readableName()), new String(field.name) }, new String[] { new String(field.declaringClass.shortReadableName()), new String(field.name) }, severity, this.nodeSourceStart(hiddenVariable, fieldDecl), this.nodeSourceEnd(hiddenVariable, fieldDecl));
        }
        else if (hiddenVariable instanceof FieldBinding) {
            final FieldBinding hiddenField = (FieldBinding)hiddenVariable;
            this.handle(570425437, new String[] { new String(field.declaringClass.readableName()), new String(field.name), new String(hiddenField.declaringClass.readableName()) }, new String[] { new String(field.declaringClass.shortReadableName()), new String(field.name), new String(hiddenField.declaringClass.shortReadableName()) }, severity, this.nodeSourceStart(hiddenField, fieldDecl), this.nodeSourceEnd(hiddenField, fieldDecl));
        }
    }
    
    public void fieldsOrThisBeforeConstructorInvocation(final ASTNode reference) {
        this.handle(134217866, ProblemReporter.NoArgument, ProblemReporter.NoArgument, reference.sourceStart, (reference instanceof LambdaExpression) ? ((LambdaExpression)reference).diagnosticsSourceEnd() : reference.sourceEnd);
    }
    
    public void finallyMustCompleteNormally(final Block finallyBlock) {
        this.handle(536871096, ProblemReporter.NoArgument, ProblemReporter.NoArgument, finallyBlock.sourceStart, finallyBlock.sourceEnd);
    }
    
    public void finalMethodCannotBeOverridden(final MethodBinding currentMethod, final MethodBinding inheritedMethod) {
        this.handle(67109265, new String[] { new String(inheritedMethod.declaringClass.readableName()) }, new String[] { new String(inheritedMethod.declaringClass.shortReadableName()) }, currentMethod.sourceStart(), currentMethod.sourceEnd());
    }
    
    public void finalVariableBound(final TypeVariableBinding typeVariable, final TypeReference typeRef) {
        if (this.options.sourceLevel < 3211264L) {
            return;
        }
        final int severity = this.computeSeverity(16777753);
        if (severity == 256) {
            return;
        }
        this.handle(16777753, new String[] { new String(typeVariable.sourceName()), new String(typeRef.resolvedType.readableName()) }, new String[] { new String(typeVariable.sourceName()), new String(typeRef.resolvedType.shortReadableName()) }, severity, typeRef.sourceStart, typeRef.sourceEnd);
    }
    
    public void forbiddenReference(final FieldBinding field, final ASTNode location, final byte classpathEntryType, final String classpathEntryName, final int problemId) {
        final int severity = this.computeSeverity(problemId);
        if (severity == 256) {
            return;
        }
        this.handle(problemId, new String[] { new String(field.readableName()) }, getElaborationId(16777523, (byte)(0x4 | classpathEntryType)), new String[] { classpathEntryName, new String(field.shortReadableName()), new String(field.declaringClass.shortReadableName()) }, severity, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
    }
    
    public void forbiddenReference(final MethodBinding method, final ASTNode location, final byte classpathEntryType, final String classpathEntryName, final int problemId) {
        final int severity = this.computeSeverity(problemId);
        if (severity == 256) {
            return;
        }
        if (method.isConstructor()) {
            this.handle(problemId, new String[] { new String(method.readableName()) }, getElaborationId(16777523, (byte)(0x8 | classpathEntryType)), new String[] { classpathEntryName, new String(method.shortReadableName()) }, severity, location.sourceStart, location.sourceEnd);
        }
        else {
            this.handle(problemId, new String[] { new String(method.readableName()) }, getElaborationId(16777523, (byte)(0xC | classpathEntryType)), new String[] { classpathEntryName, new String(method.shortReadableName()), new String(method.declaringClass.shortReadableName()) }, severity, location.sourceStart, location.sourceEnd);
        }
    }
    
    public void forbiddenReference(final TypeBinding type, final ASTNode location, final byte classpathEntryType, final String classpathEntryName, final int problemId) {
        if (location == null) {
            return;
        }
        final int severity = this.computeSeverity(problemId);
        if (severity == 256) {
            return;
        }
        this.handle(problemId, new String[] { new String(type.readableName()) }, getElaborationId(16777523, classpathEntryType), new String[] { classpathEntryName, new String(type.shortReadableName()) }, severity, location.sourceStart, location.sourceEnd);
    }
    
    public void forwardReference(final Reference reference, final int indexInQualification, final FieldBinding field) {
        this.handle(570425419, ProblemReporter.NoArgument, ProblemReporter.NoArgument, this.nodeSourceStart(field, reference, indexInQualification), this.nodeSourceEnd(field, reference, indexInQualification));
    }
    
    public void forwardTypeVariableReference(final ASTNode location, final TypeVariableBinding type) {
        this.handle(16777744, new String[] { new String(type.readableName()) }, new String[] { new String(type.shortReadableName()) }, location.sourceStart, location.sourceEnd);
    }
    
    public void genericTypeCannotExtendThrowable(final TypeDeclaration typeDecl) {
        final ASTNode location = typeDecl.binding.isAnonymousType() ? typeDecl.allocation.type : typeDecl.superclass;
        this.handle(16777773, new String[] { new String(typeDecl.binding.readableName()) }, new String[] { new String(typeDecl.binding.shortReadableName()) }, location.sourceStart, location.sourceEnd);
    }
    
    private void handle(final int problemId, final String[] problemArguments, final int elaborationId, final String[] messageArguments, final int severity, final int problemStartPosition, final int problemEndPosition) {
        this.handle(problemId, problemArguments, elaborationId, messageArguments, severity, problemStartPosition, problemEndPosition, this.referenceContext, (this.referenceContext == null) ? null : this.referenceContext.compilationResult());
        this.referenceContext = null;
    }
    
    private void handle(final int problemId, final String[] problemArguments, final String[] messageArguments, final int problemStartPosition, final int problemEndPosition) {
        this.handle(problemId, problemArguments, messageArguments, problemStartPosition, problemEndPosition, this.referenceContext, (this.referenceContext == null) ? null : this.referenceContext.compilationResult());
        this.referenceContext = null;
    }
    
    private void handle(final int problemId, final String[] problemArguments, final String[] messageArguments, final int problemStartPosition, final int problemEndPosition, final CompilationResult unitResult) {
        this.handle(problemId, problemArguments, messageArguments, problemStartPosition, problemEndPosition, this.referenceContext, unitResult);
        this.referenceContext = null;
    }
    
    private void handle(final int problemId, final String[] problemArguments, final String[] messageArguments, final int severity, final int problemStartPosition, final int problemEndPosition) {
        this.handle(problemId, problemArguments, 0, messageArguments, severity, problemStartPosition, problemEndPosition);
    }
    
    protected void handleUntagged(final int problemId, final String[] problemArguments, final String[] messageArguments, final int problemStartPosition, final int problemEndPosition) {
        final boolean oldSuppressing = this.suppressTagging;
        this.suppressTagging = true;
        try {
            this.handle(problemId, problemArguments, messageArguments, problemStartPosition, problemEndPosition);
        }
        finally {
            this.suppressTagging = oldSuppressing;
        }
        this.suppressTagging = oldSuppressing;
    }
    
    public void hiddenCatchBlock(final ReferenceBinding exceptionType, final ASTNode location) {
        this.handle(16777381, new String[] { new String(exceptionType.readableName()) }, new String[] { new String(exceptionType.shortReadableName()) }, location.sourceStart, location.sourceEnd);
    }
    
    public void hierarchyCircularity(final SourceTypeBinding sourceType, final ReferenceBinding superType, final TypeReference reference) {
        int start = 0;
        int end = 0;
        if (reference == null) {
            start = sourceType.sourceStart();
            end = sourceType.sourceEnd();
        }
        else {
            start = reference.sourceStart;
            end = reference.sourceEnd;
        }
        if (TypeBinding.equalsEquals(sourceType, superType)) {
            this.handle(16777532, new String[] { new String(sourceType.readableName()) }, new String[] { new String(sourceType.shortReadableName()) }, start, end);
        }
        else {
            this.handle(16777533, new String[] { new String(sourceType.readableName()), new String(superType.readableName()) }, new String[] { new String(sourceType.shortReadableName()), new String(superType.shortReadableName()) }, start, end);
        }
    }
    
    public void hierarchyCircularity(final TypeVariableBinding type, final ReferenceBinding superType, final TypeReference reference) {
        int start = 0;
        int end = 0;
        start = reference.sourceStart;
        end = reference.sourceEnd;
        if (TypeBinding.equalsEquals(type, superType)) {
            this.handle(16777532, new String[] { new String(type.readableName()) }, new String[] { new String(type.shortReadableName()) }, start, end);
        }
        else {
            this.handle(16777533, new String[] { new String(type.readableName()), new String(superType.readableName()) }, new String[] { new String(type.shortReadableName()), new String(superType.shortReadableName()) }, start, end);
        }
    }
    
    public void hierarchyHasProblems(final SourceTypeBinding type) {
        final String[] arguments = { new String(type.sourceName()) };
        this.handle(16777543, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }
    
    public void illegalAbstractModifierCombinationForMethod(final ReferenceBinding type, final AbstractMethodDeclaration methodDecl) {
        final String[] arguments = { new String(type.sourceName()), new String(methodDecl.selector) };
        this.handle(67109226, arguments, arguments, methodDecl.sourceStart, methodDecl.sourceEnd);
    }
    
    public void illegalAbstractModifierCombinationForMethod(final AbstractMethodDeclaration methodDecl) {
        final String[] arguments = { new String(methodDecl.selector) };
        this.handle(67109921, arguments, arguments, methodDecl.sourceStart, methodDecl.sourceEnd);
    }
    
    public void illegalAccessFromTypeVariable(final TypeVariableBinding variable, final ASTNode location) {
        if ((location.bits & 0x8000) != 0x0) {
            this.javadocInvalidReference(location.sourceStart, location.sourceEnd);
        }
        else {
            final String[] arguments = { new String(variable.sourceName) };
            this.handle(16777791, arguments, arguments, location.sourceStart, location.sourceEnd);
        }
    }
    
    public void illegalClassLiteralForTypeVariable(final TypeVariableBinding variable, final ASTNode location) {
        final String[] arguments = { new String(variable.sourceName) };
        this.handle(16777774, arguments, arguments, location.sourceStart, location.sourceEnd);
    }
    
    public void illegalExtendedDimensions(final AnnotationMethodDeclaration annotationTypeMemberDeclaration) {
        this.handle(67109465, ProblemReporter.NoArgument, ProblemReporter.NoArgument, annotationTypeMemberDeclaration.sourceStart, annotationTypeMemberDeclaration.sourceEnd);
    }
    
    public void illegalExtendedDimensions(final Argument argument) {
        this.handle(1610613536, ProblemReporter.NoArgument, ProblemReporter.NoArgument, argument.sourceStart, argument.sourceEnd);
    }
    
    public void illegalGenericArray(final TypeBinding leafComponentType, final ASTNode location) {
        this.handle(16777751, new String[] { new String(leafComponentType.readableName()) }, new String[] { new String(leafComponentType.shortReadableName()) }, location.sourceStart, location.sourceEnd);
    }
    
    public void illegalInstanceOfGenericType(final TypeBinding checkedType, final ASTNode location) {
        final TypeBinding erasedType = checkedType.leafComponentType().erasure();
        final StringBuffer recommendedFormBuffer = new StringBuffer(10);
        if (erasedType instanceof ReferenceBinding) {
            final ReferenceBinding referenceBinding = (ReferenceBinding)erasedType;
            recommendedFormBuffer.append(referenceBinding.qualifiedSourceName());
        }
        else {
            recommendedFormBuffer.append(erasedType.sourceName());
        }
        final int count = erasedType.typeVariables().length;
        if (count > 0) {
            recommendedFormBuffer.append('<');
            for (int i = 0; i < count; ++i) {
                if (i > 0) {
                    recommendedFormBuffer.append(',');
                }
                recommendedFormBuffer.append('?');
            }
            recommendedFormBuffer.append('>');
        }
        for (int i = 0, dim = checkedType.dimensions(); i < dim; ++i) {
            recommendedFormBuffer.append("[]");
        }
        final String recommendedForm = recommendedFormBuffer.toString();
        if (checkedType.leafComponentType().isTypeVariable()) {
            this.handle(536871459, new String[] { new String(checkedType.readableName()), recommendedForm }, new String[] { new String(checkedType.shortReadableName()), recommendedForm }, location.sourceStart, location.sourceEnd);
            return;
        }
        this.handle(536871458, new String[] { new String(checkedType.readableName()), recommendedForm }, new String[] { new String(checkedType.shortReadableName()), recommendedForm }, location.sourceStart, location.sourceEnd);
    }
    
    public void illegalLocalTypeDeclaration(final TypeDeclaration typeDeclaration) {
        if (this.isRecoveredName(typeDeclaration.name)) {
            return;
        }
        int problemID = 0;
        if ((typeDeclaration.modifiers & 0x4000) != 0x0) {
            problemID = 536870943;
        }
        else if ((typeDeclaration.modifiers & 0x2000) != 0x0) {
            problemID = 536870942;
        }
        else if ((typeDeclaration.modifiers & 0x200) != 0x0) {
            problemID = 536870938;
        }
        if (problemID != 0) {
            final String[] arguments = { new String(typeDeclaration.name) };
            this.handle(problemID, arguments, arguments, typeDeclaration.sourceStart, typeDeclaration.sourceEnd);
        }
    }
    
    public void illegalModifierCombinationFinalAbstractForClass(final SourceTypeBinding type) {
        final String[] arguments = { new String(type.sourceName()) };
        this.handle(16777524, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }
    
    public void illegalModifierCombinationFinalVolatileForField(final ReferenceBinding type, final FieldDeclaration fieldDecl) {
        final String[] arguments = { new String(fieldDecl.name) };
        this.handle(33554777, arguments, arguments, fieldDecl.sourceStart, fieldDecl.sourceEnd);
    }
    
    public void illegalModifierCombinationForInterfaceMethod(final AbstractMethodDeclaration methodDecl) {
        final String[] arguments = { new String(methodDecl.selector) };
        this.handle(67109920, arguments, arguments, methodDecl.sourceStart, methodDecl.sourceEnd);
    }
    
    public void illegalModifierForAnnotationField(final FieldDeclaration fieldDecl) {
        final String name = new String(fieldDecl.name);
        this.handle(536871527, new String[] { new String(fieldDecl.binding.declaringClass.readableName()), name }, new String[] { new String(fieldDecl.binding.declaringClass.shortReadableName()), name }, fieldDecl.sourceStart, fieldDecl.sourceEnd);
    }
    
    public void illegalModifierForAnnotationMember(final AbstractMethodDeclaration methodDecl) {
        this.handle(67109464, new String[] { new String(methodDecl.binding.declaringClass.readableName()), new String(methodDecl.selector) }, new String[] { new String(methodDecl.binding.declaringClass.shortReadableName()), new String(methodDecl.selector) }, methodDecl.sourceStart, methodDecl.sourceEnd);
    }
    
    public void illegalModifierForAnnotationMemberType(final SourceTypeBinding type) {
        final String[] arguments = { new String(type.sourceName()) };
        this.handle(16777820, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }
    
    public void illegalModifierForAnnotationType(final SourceTypeBinding type) {
        final String[] arguments = { new String(type.sourceName()) };
        this.handle(16777819, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }
    
    public void illegalModifierForClass(final SourceTypeBinding type) {
        final String[] arguments = { new String(type.sourceName()) };
        this.handle(16777518, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }
    
    public void illegalModifierForEnum(final SourceTypeBinding type) {
        final String[] arguments = { new String(type.sourceName()) };
        this.handle(16777966, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }
    
    public void illegalModifierForEnumConstant(final ReferenceBinding type, final FieldDeclaration fieldDecl) {
        final String[] arguments = { new String(fieldDecl.name) };
        this.handle(33555183, arguments, arguments, fieldDecl.sourceStart, fieldDecl.sourceEnd);
    }
    
    public void illegalModifierForEnumConstructor(final AbstractMethodDeclaration constructor) {
        this.handle(67109624, ProblemReporter.NoArgument, ProblemReporter.NoArgument, constructor.sourceStart, constructor.sourceEnd);
    }
    
    public void illegalModifierForField(final ReferenceBinding type, final FieldDeclaration fieldDecl) {
        final String[] arguments = { new String(fieldDecl.name) };
        this.handle(33554774, arguments, arguments, fieldDecl.sourceStart, fieldDecl.sourceEnd);
    }
    
    public void illegalModifierForInterface(final SourceTypeBinding type) {
        final String[] arguments = { new String(type.sourceName()) };
        this.handle(16777519, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }
    
    public void illegalModifierForInterfaceField(final FieldDeclaration fieldDecl) {
        final String name = new String(fieldDecl.name);
        this.handle(33554775, new String[] { new String(fieldDecl.binding.declaringClass.readableName()), name }, new String[] { new String(fieldDecl.binding.declaringClass.shortReadableName()), name }, fieldDecl.sourceStart, fieldDecl.sourceEnd);
    }
    
    public void illegalModifierForInterfaceMethod(final AbstractMethodDeclaration methodDecl, final boolean isJDK18orGreater) {
        this.handle(isJDK18orGreater ? 67109914 : 67109223, new String[] { new String(methodDecl.selector) }, new String[] { new String(methodDecl.selector) }, methodDecl.sourceStart, methodDecl.sourceEnd);
    }
    
    public void illegalModifierForLocalClass(final SourceTypeBinding type) {
        final String[] arguments = { new String(type.sourceName()) };
        this.handle(16777522, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }
    
    public void illegalModifierForMemberClass(final SourceTypeBinding type) {
        final String[] arguments = { new String(type.sourceName()) };
        this.handle(16777520, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }
    
    public void illegalModifierForMemberEnum(final SourceTypeBinding type) {
        final String[] arguments = { new String(type.sourceName()) };
        this.handle(16777969, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }
    
    public void illegalModifierForMemberInterface(final SourceTypeBinding type) {
        final String[] arguments = { new String(type.sourceName()) };
        this.handle(16777521, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }
    
    public void illegalModifierForMethod(final AbstractMethodDeclaration methodDecl) {
        this.handle(methodDecl.isConstructor() ? 67109233 : 67109222, new String[] { new String(methodDecl.selector) }, new String[] { new String(methodDecl.selector) }, methodDecl.sourceStart, methodDecl.sourceEnd);
    }
    
    public void illegalModifierForVariable(final LocalDeclaration localDecl, final boolean complainAsArgument) {
        final String[] arguments = { new String(localDecl.name) };
        this.handle(complainAsArgument ? 67109220 : 67109260, arguments, arguments, localDecl.sourceStart, localDecl.sourceEnd);
    }
    
    public void illegalPrimitiveOrArrayTypeForEnclosingInstance(final TypeBinding enclosingType, final ASTNode location) {
        this.handle(16777243, new String[] { new String(enclosingType.readableName()) }, new String[] { new String(enclosingType.shortReadableName()) }, location.sourceStart, location.sourceEnd);
    }
    
    public void illegalQualifiedParameterizedTypeAllocation(final TypeReference qualifiedTypeReference, final TypeBinding allocatedType) {
        this.handle(16777782, new String[] { new String(allocatedType.readableName()), new String(allocatedType.enclosingType().readableName()) }, new String[] { new String(allocatedType.shortReadableName()), new String(allocatedType.enclosingType().shortReadableName()) }, qualifiedTypeReference.sourceStart, qualifiedTypeReference.sourceEnd);
    }
    
    public void illegalStaticModifierForMemberType(final SourceTypeBinding type) {
        final String[] arguments = { new String(type.sourceName()) };
        this.handle(16777527, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }
    
    public void illegalUsageOfQualifiedTypeReference(final QualifiedTypeReference qualifiedTypeReference) {
        final StringBuffer buffer = new StringBuffer();
        final char[][] tokens = qualifiedTypeReference.tokens;
        for (int i = 0; i < tokens.length; ++i) {
            if (i > 0) {
                buffer.append('.');
            }
            buffer.append(tokens[i]);
        }
        final String[] arguments = { String.valueOf(buffer) };
        this.handle(1610612934, arguments, arguments, qualifiedTypeReference.sourceStart, qualifiedTypeReference.sourceEnd);
    }
    
    public void illegalUsageOfWildcard(final TypeReference wildcard) {
        this.handle(1610613314, ProblemReporter.NoArgument, ProblemReporter.NoArgument, wildcard.sourceStart, wildcard.sourceEnd);
    }
    
    public void illegalVararg(final Argument argType, final AbstractMethodDeclaration methodDecl) {
        final String[] arguments = { CharOperation.toString(argType.type.getTypeName()), new String(methodDecl.selector) };
        this.handle(67109279, arguments, arguments, argType.sourceStart, argType.sourceEnd);
    }
    
    public void illegalVarargInLambda(final Argument argType) {
        final String[] arguments = { CharOperation.toString(argType.type.getTypeName()) };
        this.handle(553648782, arguments, arguments, argType.sourceStart, argType.sourceEnd);
    }
    
    public void illegalThisDeclaration(final Argument argument) {
        final String[] arguments = ProblemReporter.NoArgument;
        this.handle(1610613378, arguments, arguments, argument.sourceStart, argument.sourceEnd);
    }
    
    public void illegalSourceLevelForThis(final Argument argument) {
        final String[] arguments = ProblemReporter.NoArgument;
        this.handle(1610613379, arguments, arguments, argument.sourceStart, argument.sourceEnd);
    }
    
    public void disallowedThisParameter(final Receiver receiver) {
        final String[] arguments = ProblemReporter.NoArgument;
        this.handle(1610613374, arguments, arguments, receiver.sourceStart, receiver.sourceEnd);
    }
    
    public void illegalQualifierForExplicitThis(final Receiver receiver, final TypeBinding expectedType) {
        final String[] problemArguments = { new String(expectedType.sourceName()) };
        this.handle(1610613387, problemArguments, problemArguments, (receiver.qualifyingName == null) ? receiver.sourceStart : receiver.qualifyingName.sourceStart, receiver.sourceEnd);
    }
    
    public void illegalQualifierForExplicitThis2(final Receiver receiver) {
        this.handle(1610613388, ProblemReporter.NoArgument, ProblemReporter.NoArgument, receiver.qualifyingName.sourceStart, receiver.sourceEnd);
    }
    
    public void illegalTypeForExplicitThis(final Receiver receiver, final TypeBinding expectedType) {
        this.handle(1610613386, new String[] { new String(expectedType.readableName()) }, new String[] { new String(expectedType.shortReadableName()) }, receiver.type.sourceStart, receiver.type.sourceEnd);
    }
    
    public void illegalThis(final Argument argument) {
        final String[] arguments = ProblemReporter.NoArgument;
        this.handle(1610613384, arguments, arguments, argument.sourceStart, argument.sourceEnd);
    }
    
    public void defaultMethodsNotBelow18(final MethodDeclaration md) {
        this.handle(1610613380, ProblemReporter.NoArgument, ProblemReporter.NoArgument, md.sourceStart, md.sourceEnd);
    }
    
    public void interfaceSuperInvocationNotBelow18(final QualifiedSuperReference qualifiedSuperReference) {
        this.handle(1610613403, ProblemReporter.NoArgument, ProblemReporter.NoArgument, qualifiedSuperReference.sourceStart, qualifiedSuperReference.sourceEnd);
    }
    
    public void staticInterfaceMethodsNotBelow18(final MethodDeclaration md) {
        this.handle(1610613632, ProblemReporter.NoArgument, ProblemReporter.NoArgument, md.sourceStart, md.sourceEnd);
    }
    
    public void referenceExpressionsNotBelow18(final ReferenceExpression rexp) {
        this.handle(rexp.isMethodReference() ? 1610613382 : 1610613383, ProblemReporter.NoArgument, ProblemReporter.NoArgument, rexp.sourceStart, rexp.sourceEnd);
    }
    
    public void lambdaExpressionsNotBelow18(final LambdaExpression lexp) {
        this.handle(1610613381, ProblemReporter.NoArgument, ProblemReporter.NoArgument, lexp.sourceStart, lexp.diagnosticsSourceEnd());
    }
    
    public void illegalVisibilityModifierCombinationForField(final ReferenceBinding type, final FieldDeclaration fieldDecl) {
        final String[] arguments = { new String(fieldDecl.name) };
        this.handle(33554776, arguments, arguments, fieldDecl.sourceStart, fieldDecl.sourceEnd);
    }
    
    public void illegalVisibilityModifierCombinationForMemberType(final SourceTypeBinding type) {
        final String[] arguments = { new String(type.sourceName()) };
        this.handle(16777526, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }
    
    public void illegalVisibilityModifierCombinationForMethod(final ReferenceBinding type, final AbstractMethodDeclaration methodDecl) {
        final String[] arguments = { new String(type.sourceName()), new String(methodDecl.selector) };
        this.handle(67109224, arguments, arguments, methodDecl.sourceStart, methodDecl.sourceEnd);
    }
    
    public void illegalVisibilityModifierForInterfaceMemberType(final SourceTypeBinding type) {
        final String[] arguments = { new String(type.sourceName()) };
        this.handle(16777525, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }
    
    public void illegalVoidExpression(final ASTNode location) {
        this.handle(536871076, ProblemReporter.NoArgument, ProblemReporter.NoArgument, location.sourceStart, location.sourceEnd);
    }
    
    public void importProblem(final ImportReference importRef, final Binding expectedImport) {
        if (expectedImport instanceof FieldBinding) {
            int id = 33554502;
            final FieldBinding field = (FieldBinding)expectedImport;
            String[] readableArguments = null;
            String[] shortArguments = null;
            switch (expectedImport.problemId()) {
                case 2: {
                    id = 33554503;
                    readableArguments = new String[] { CharOperation.toString(importRef.tokens), new String(field.declaringClass.readableName()) };
                    shortArguments = new String[] { CharOperation.toString(importRef.tokens), new String(field.declaringClass.shortReadableName()) };
                    break;
                }
                case 3: {
                    id = 33554504;
                    readableArguments = new String[] { new String(field.readableName()) };
                    shortArguments = new String[] { new String(field.readableName()) };
                    break;
                }
                case 8: {
                    id = 16777219;
                    readableArguments = new String[] { new String(field.declaringClass.leafComponentType().readableName()) };
                    shortArguments = new String[] { new String(field.declaringClass.leafComponentType().shortReadableName()) };
                    break;
                }
            }
            this.handleUntagged(id, readableArguments, shortArguments, this.nodeSourceStart(field, importRef), this.nodeSourceEnd(field, importRef));
            return;
        }
        if (expectedImport.problemId() == 1) {
            final char[][] tokens = (expectedImport instanceof ProblemReferenceBinding) ? ((ProblemReferenceBinding)expectedImport).compoundName : importRef.tokens;
            final String[] arguments = { CharOperation.toString(tokens) };
            this.handleUntagged(268435846, arguments, arguments, importRef.sourceStart, (int)importRef.sourcePositions[tokens.length - 1]);
            return;
        }
        if (expectedImport.problemId() == 14) {
            final char[][] tokens = importRef.tokens;
            final String[] arguments = { CharOperation.toString(tokens) };
            this.handleUntagged(268435847, arguments, arguments, importRef.sourceStart, (int)importRef.sourcePositions[tokens.length - 1]);
            return;
        }
        this.invalidType(importRef, (TypeBinding)expectedImport);
    }
    
    public void incompatibleExceptionInThrowsClause(final SourceTypeBinding type, final MethodBinding currentMethod, final MethodBinding inheritedMethod, final ReferenceBinding exceptionType) {
        if (TypeBinding.equalsEquals(type, currentMethod.declaringClass)) {
            int id;
            if (currentMethod.declaringClass.isInterface() && !inheritedMethod.isPublic()) {
                id = 67109278;
            }
            else {
                id = 67109266;
            }
            this.handle(id, new String[] { new String(exceptionType.sourceName()), new String(CharOperation.concat(inheritedMethod.declaringClass.readableName(), inheritedMethod.readableName(), '.')) }, new String[] { new String(exceptionType.sourceName()), new String(CharOperation.concat(inheritedMethod.declaringClass.shortReadableName(), inheritedMethod.shortReadableName(), '.')) }, currentMethod.sourceStart(), currentMethod.sourceEnd());
        }
        else {
            this.handle(67109267, new String[] { new String(exceptionType.sourceName()), new String(CharOperation.concat(currentMethod.declaringClass.sourceName(), currentMethod.readableName(), '.')), new String(CharOperation.concat(inheritedMethod.declaringClass.readableName(), inheritedMethod.readableName(), '.')) }, new String[] { new String(exceptionType.sourceName()), new String(CharOperation.concat(currentMethod.declaringClass.sourceName(), currentMethod.shortReadableName(), '.')), new String(CharOperation.concat(inheritedMethod.declaringClass.shortReadableName(), inheritedMethod.shortReadableName(), '.')) }, type.sourceStart(), type.sourceEnd());
        }
    }
    
    public void incompatibleReturnType(final MethodBinding currentMethod, final MethodBinding inheritedMethod) {
        final StringBuffer methodSignature = new StringBuffer();
        methodSignature.append(inheritedMethod.declaringClass.readableName()).append('.').append(inheritedMethod.readableName());
        final StringBuffer shortSignature = new StringBuffer();
        shortSignature.append(inheritedMethod.declaringClass.shortReadableName()).append('.').append(inheritedMethod.shortReadableName());
        final ReferenceBinding declaringClass = currentMethod.declaringClass;
        int id;
        if (declaringClass.isInterface() && !inheritedMethod.isPublic()) {
            id = 67109277;
        }
        else {
            id = 67109268;
        }
        final AbstractMethodDeclaration method = currentMethod.sourceMethod();
        int sourceStart = 0;
        int sourceEnd = 0;
        if (method == null) {
            if (declaringClass instanceof SourceTypeBinding) {
                final SourceTypeBinding sourceTypeBinding = (SourceTypeBinding)declaringClass;
                sourceStart = sourceTypeBinding.sourceStart();
                sourceEnd = sourceTypeBinding.sourceEnd();
            }
        }
        else if (method.isConstructor()) {
            sourceStart = method.sourceStart;
            sourceEnd = method.sourceEnd;
        }
        else {
            final TypeReference returnType = ((MethodDeclaration)method).returnType;
            sourceStart = returnType.sourceStart;
            if (returnType instanceof ParameterizedSingleTypeReference) {
                final ParameterizedSingleTypeReference typeReference = (ParameterizedSingleTypeReference)returnType;
                final TypeReference[] typeArguments = typeReference.typeArguments;
                if (typeArguments[typeArguments.length - 1].sourceEnd > typeReference.sourceEnd) {
                    sourceEnd = this.retrieveClosingAngleBracketPosition(typeReference.sourceEnd);
                }
                else {
                    sourceEnd = returnType.sourceEnd;
                }
            }
            else if (returnType instanceof ParameterizedQualifiedTypeReference) {
                final ParameterizedQualifiedTypeReference typeReference2 = (ParameterizedQualifiedTypeReference)returnType;
                sourceEnd = this.retrieveClosingAngleBracketPosition(typeReference2.sourceEnd);
            }
            else {
                sourceEnd = returnType.sourceEnd;
            }
        }
        this.handle(id, new String[] { methodSignature.toString() }, new String[] { shortSignature.toString() }, sourceStart, sourceEnd);
    }
    
    public void incorrectArityForParameterizedType(final ASTNode location, final TypeBinding type, final TypeBinding[] argumentTypes) {
        this.incorrectArityForParameterizedType(location, type, argumentTypes, Integer.MAX_VALUE);
    }
    
    public void incorrectArityForParameterizedType(final ASTNode location, final TypeBinding type, final TypeBinding[] argumentTypes, final int index) {
        if (location == null) {
            this.handle(16777741, new String[] { new String(type.readableName()), this.typesAsString(argumentTypes, false) }, new String[] { new String(type.shortReadableName()), this.typesAsString(argumentTypes, true) }, 131, 0, 0);
            return;
        }
        this.handle(16777741, new String[] { new String(type.readableName()), this.typesAsString(argumentTypes, false) }, new String[] { new String(type.shortReadableName()), this.typesAsString(argumentTypes, true) }, location.sourceStart, this.nodeSourceEnd(null, location, index));
    }
    
    public void diamondNotBelow17(final ASTNode location) {
        this.diamondNotBelow17(location, Integer.MAX_VALUE);
    }
    
    public void diamondNotBelow17(final ASTNode location, final int index) {
        if (location == null) {
            this.handle(16778099, ProblemReporter.NoArgument, ProblemReporter.NoArgument, 131, 0, 0);
            return;
        }
        this.handle(16778099, ProblemReporter.NoArgument, ProblemReporter.NoArgument, location.sourceStart, this.nodeSourceEnd(null, location, index));
    }
    
    public void incorrectLocationForNonEmptyDimension(final ArrayAllocationExpression expression, final int index) {
        this.handle(536871114, ProblemReporter.NoArgument, ProblemReporter.NoArgument, expression.dimensions[index].sourceStart, expression.dimensions[index].sourceEnd);
    }
    
    public void incorrectSwitchType(final Expression expression, final TypeBinding testType) {
        if (this.options.sourceLevel < 3342336L) {
            if (testType.id == 11) {
                this.handle(16778097, new String[] { new String(testType.readableName()) }, new String[] { new String(testType.shortReadableName()) }, expression.sourceStart, expression.sourceEnd);
            }
            else if (this.options.sourceLevel < 3211264L && testType.isEnum()) {
                this.handle(16778106, new String[] { new String(testType.readableName()) }, new String[] { new String(testType.shortReadableName()) }, expression.sourceStart, expression.sourceEnd);
            }
            else {
                this.handle(16777385, new String[] { new String(testType.readableName()) }, new String[] { new String(testType.shortReadableName()) }, expression.sourceStart, expression.sourceEnd);
            }
        }
        else {
            this.handle(16778093, new String[] { new String(testType.readableName()) }, new String[] { new String(testType.shortReadableName()) }, expression.sourceStart, expression.sourceEnd);
        }
    }
    
    public void indirectAccessToStaticField(final ASTNode location, final FieldBinding field) {
        final int severity = this.computeSeverity(570425422);
        if (severity == 256) {
            return;
        }
        this.handle(570425422, new String[] { new String(field.declaringClass.readableName()), new String(field.name) }, new String[] { new String(field.declaringClass.shortReadableName()), new String(field.name) }, severity, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
    }
    
    public void indirectAccessToStaticMethod(final ASTNode location, final MethodBinding method) {
        final int severity = this.computeSeverity(603979895);
        if (severity == 256) {
            return;
        }
        this.handle(603979895, new String[] { new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false) }, new String[] { new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true) }, severity, location.sourceStart, location.sourceEnd);
    }
    
    public void inheritedDefaultMethodConflictsWithOtherInherited(final SourceTypeBinding type, final MethodBinding defaultMethod, final MethodBinding otherMethod) {
        final TypeDeclaration typeDecl = type.scope.referenceContext;
        final String[] problemArguments = { String.valueOf(defaultMethod.readableName()), String.valueOf(defaultMethod.declaringClass.readableName()), String.valueOf(otherMethod.declaringClass.readableName()) };
        final String[] messageArguments = { String.valueOf(defaultMethod.shortReadableName()), String.valueOf(defaultMethod.declaringClass.shortReadableName()), String.valueOf(otherMethod.declaringClass.shortReadableName()) };
        this.handle(67109916, problemArguments, messageArguments, typeDecl.sourceStart, typeDecl.sourceEnd);
    }
    
    private void inheritedMethodReducesVisibility(final int sourceStart, final int sourceEnd, final MethodBinding concreteMethod, final MethodBinding[] abstractMethods) {
        final StringBuffer concreteSignature = new StringBuffer();
        concreteSignature.append(concreteMethod.declaringClass.readableName()).append('.').append(concreteMethod.readableName());
        final StringBuffer shortSignature = new StringBuffer();
        shortSignature.append(concreteMethod.declaringClass.shortReadableName()).append('.').append(concreteMethod.shortReadableName());
        this.handle(67109269, new String[] { concreteSignature.toString(), new String(abstractMethods[0].declaringClass.readableName()) }, new String[] { shortSignature.toString(), new String(abstractMethods[0].declaringClass.shortReadableName()) }, sourceStart, sourceEnd);
    }
    
    public void inheritedMethodReducesVisibility(final SourceTypeBinding type, final MethodBinding concreteMethod, final MethodBinding[] abstractMethods) {
        this.inheritedMethodReducesVisibility(type.sourceStart(), type.sourceEnd(), concreteMethod, abstractMethods);
    }
    
    public void inheritedMethodReducesVisibility(final TypeParameter typeParameter, final MethodBinding concreteMethod, final MethodBinding[] abstractMethods) {
        this.inheritedMethodReducesVisibility(typeParameter.sourceStart(), typeParameter.sourceEnd(), concreteMethod, abstractMethods);
    }
    
    public void inheritedMethodsHaveIncompatibleReturnTypes(final ASTNode location, final MethodBinding[] inheritedMethods, final int length) {
        final StringBuffer methodSignatures = new StringBuffer();
        final StringBuffer shortSignatures = new StringBuffer();
        int i = length;
        while (--i >= 0) {
            methodSignatures.append(inheritedMethods[i].declaringClass.readableName()).append('.').append(inheritedMethods[i].readableName());
            shortSignatures.append(inheritedMethods[i].declaringClass.shortReadableName()).append('.').append(inheritedMethods[i].shortReadableName());
            if (i != 0) {
                methodSignatures.append(", ");
                shortSignatures.append(", ");
            }
        }
        this.handle(67109283, new String[] { methodSignatures.toString() }, new String[] { shortSignatures.toString() }, location.sourceStart, location.sourceEnd);
    }
    
    public void inheritedMethodsHaveIncompatibleReturnTypes(final SourceTypeBinding type, final MethodBinding[] inheritedMethods, final int length, final boolean[] isOverridden) {
        final StringBuffer methodSignatures = new StringBuffer();
        final StringBuffer shortSignatures = new StringBuffer();
        int i = length;
        while (--i >= 0) {
            if (isOverridden[i]) {
                continue;
            }
            methodSignatures.append(inheritedMethods[i].declaringClass.readableName()).append('.').append(inheritedMethods[i].readableName());
            shortSignatures.append(inheritedMethods[i].declaringClass.shortReadableName()).append('.').append(inheritedMethods[i].shortReadableName());
            if (i == 0) {
                continue;
            }
            methodSignatures.append(", ");
            shortSignatures.append(", ");
        }
        this.handle(67109283, new String[] { methodSignatures.toString() }, new String[] { shortSignatures.toString() }, type.sourceStart(), type.sourceEnd());
    }
    
    public void inheritedMethodsHaveNameClash(final SourceTypeBinding type, final MethodBinding oneMethod, final MethodBinding twoMethod) {
        this.handle(67109424, new String[] { new String(oneMethod.selector), this.typesAsString(oneMethod.original(), false), new String(oneMethod.declaringClass.readableName()), this.typesAsString(twoMethod.original(), false), new String(twoMethod.declaringClass.readableName()) }, new String[] { new String(oneMethod.selector), this.typesAsString(oneMethod.original(), true), new String(oneMethod.declaringClass.shortReadableName()), this.typesAsString(twoMethod.original(), true), new String(twoMethod.declaringClass.shortReadableName()) }, type.sourceStart(), type.sourceEnd());
    }
    
    public void initializerMustCompleteNormally(final FieldDeclaration fieldDecl) {
        this.handle(536871075, ProblemReporter.NoArgument, ProblemReporter.NoArgument, fieldDecl.sourceStart, fieldDecl.sourceEnd);
    }
    
    public void innerTypesCannotDeclareStaticInitializers(final ReferenceBinding innerType, final Initializer initializer) {
        this.handle(536870936, new String[] { new String(innerType.readableName()) }, new String[] { new String(innerType.shortReadableName()) }, initializer.sourceStart, initializer.sourceStart);
    }
    
    public void interfaceCannotHaveConstructors(final ConstructorDeclaration constructor) {
        this.handle(1610612943, ProblemReporter.NoArgument, ProblemReporter.NoArgument, constructor.sourceStart, constructor.sourceEnd, constructor, constructor.compilationResult());
    }
    
    public void interfaceCannotHaveInitializers(final char[] sourceName, final FieldDeclaration fieldDecl) {
        final String[] arguments = { new String(sourceName) };
        this.handle(16777516, arguments, arguments, fieldDecl.sourceStart, fieldDecl.sourceEnd);
    }
    
    public void invalidAnnotationMemberType(final MethodDeclaration methodDecl) {
        this.handle(16777821, new String[] { new String(methodDecl.binding.returnType.readableName()), new String(methodDecl.selector), new String(methodDecl.binding.declaringClass.readableName()) }, new String[] { new String(methodDecl.binding.returnType.shortReadableName()), new String(methodDecl.selector), new String(methodDecl.binding.declaringClass.shortReadableName()) }, methodDecl.returnType.sourceStart, methodDecl.returnType.sourceEnd);
    }
    
    public void invalidBreak(final ASTNode location) {
        this.handle(536871084, ProblemReporter.NoArgument, ProblemReporter.NoArgument, location.sourceStart, location.sourceEnd);
    }
    
    public void invalidConstructor(final Statement statement, final MethodBinding targetConstructor) {
        final boolean insideDefaultConstructor = this.referenceContext instanceof ConstructorDeclaration && ((ConstructorDeclaration)this.referenceContext).isDefaultConstructor();
        final boolean insideImplicitConstructorCall = statement instanceof ExplicitConstructorCall && ((ExplicitConstructorCall)statement).accessMode == 1;
        int sourceStart = statement.sourceStart;
        int sourceEnd = statement.sourceEnd;
        if (statement instanceof AllocationExpression) {
            final AllocationExpression allocation = (AllocationExpression)statement;
            if (allocation.enumConstant != null) {
                sourceStart = allocation.enumConstant.sourceStart;
                sourceEnd = allocation.enumConstant.sourceEnd;
            }
        }
        int id = 134217858;
        MethodBinding shownConstructor = targetConstructor;
        switch (targetConstructor.problemId()) {
            case 1: {
                final ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
                if (problemConstructor.closestMatch != null && (problemConstructor.closestMatch.tagBits & 0x80L) != 0x0L) {
                    this.missingTypeInConstructor(statement, problemConstructor.closestMatch);
                    return;
                }
                if (insideDefaultConstructor) {
                    id = 134217868;
                    break;
                }
                if (insideImplicitConstructorCall) {
                    id = 134217871;
                    break;
                }
                id = 134217858;
                break;
            }
            case 2: {
                if (insideDefaultConstructor) {
                    id = 134217869;
                }
                else if (insideImplicitConstructorCall) {
                    id = 134217872;
                }
                else {
                    id = 134217859;
                }
                final ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
                if (problemConstructor.closestMatch != null) {
                    shownConstructor = problemConstructor.closestMatch.original();
                    break;
                }
                break;
            }
            case 3: {
                if (insideDefaultConstructor) {
                    id = 134217870;
                    break;
                }
                if (insideImplicitConstructorCall) {
                    id = 134217873;
                    break;
                }
                id = 134217860;
                break;
            }
            case 10: {
                final ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
                final ParameterizedGenericMethodBinding substitutedConstructor = (ParameterizedGenericMethodBinding)problemConstructor.closestMatch;
                shownConstructor = substitutedConstructor.original();
                final int augmentedLength = problemConstructor.parameters.length;
                final TypeBinding inferredTypeArgument = problemConstructor.parameters[augmentedLength - 2];
                final TypeVariableBinding typeParameter = (TypeVariableBinding)problemConstructor.parameters[augmentedLength - 1];
                final TypeBinding[] invocationArguments = new TypeBinding[augmentedLength - 2];
                System.arraycopy(problemConstructor.parameters, 0, invocationArguments, 0, augmentedLength - 2);
                this.handle(16777760, new String[] { new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, false), new String(shownConstructor.declaringClass.readableName()), this.typesAsString(invocationArguments, false), new String(inferredTypeArgument.readableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, false) }, new String[] { new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, true), new String(shownConstructor.declaringClass.shortReadableName()), this.typesAsString(invocationArguments, true), new String(inferredTypeArgument.shortReadableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, true) }, sourceStart, sourceEnd);
                return;
            }
            case 11: {
                final ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
                shownConstructor = problemConstructor.closestMatch;
                if (shownConstructor.typeVariables == Binding.NO_TYPE_VARIABLES) {
                    this.handle(16777767, new String[] { new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, false), new String(shownConstructor.declaringClass.readableName()), this.typesAsString(targetConstructor, false) }, new String[] { new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, true), new String(shownConstructor.declaringClass.shortReadableName()), this.typesAsString(targetConstructor, true) }, sourceStart, sourceEnd);
                }
                else {
                    this.handle(16777768, new String[] { new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, false), new String(shownConstructor.declaringClass.readableName()), this.typesAsString(shownConstructor.typeVariables, false), this.typesAsString(targetConstructor, false) }, new String[] { new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, true), new String(shownConstructor.declaringClass.shortReadableName()), this.typesAsString(shownConstructor.typeVariables, true), this.typesAsString(targetConstructor, true) }, sourceStart, sourceEnd);
                }
                return;
            }
            case 12: {
                final ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
                shownConstructor = problemConstructor.closestMatch;
                this.handle(16777769, new String[] { new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, false), new String(shownConstructor.declaringClass.readableName()), this.typesAsString(((ParameterizedGenericMethodBinding)shownConstructor).typeArguments, false), this.typesAsString(targetConstructor, false) }, new String[] { new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, true), new String(shownConstructor.declaringClass.shortReadableName()), this.typesAsString(((ParameterizedGenericMethodBinding)shownConstructor).typeArguments, true), this.typesAsString(targetConstructor, true) }, sourceStart, sourceEnd);
                return;
            }
            case 13: {
                final ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
                shownConstructor = problemConstructor.closestMatch;
                this.handle(16777771, new String[] { new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, false), new String(shownConstructor.declaringClass.readableName()), this.typesAsString(targetConstructor, false) }, new String[] { new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, true), new String(shownConstructor.declaringClass.shortReadableName()), this.typesAsString(targetConstructor, true) }, sourceStart, sourceEnd);
                return;
            }
            case 16: {
                final ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
                shownConstructor = problemConstructor.closestMatch;
                final TypeBinding varargsElementType = shownConstructor.parameters[shownConstructor.parameters.length - 1].leafComponentType();
                this.handle(134218536, new String[] { new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, false), new String(shownConstructor.declaringClass.readableName()), new String(varargsElementType.readableName()) }, new String[] { new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, true), new String(shownConstructor.declaringClass.shortReadableName()), new String(varargsElementType.shortReadableName()) }, sourceStart, sourceEnd);
                return;
            }
            case 23:
            case 27: {
                final ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
                shownConstructor = problemConstructor.closestMatch;
                this.handle(16777233, new String[] { String.valueOf(shownConstructor.returnType.readableName()), (problemConstructor.returnType != null) ? String.valueOf(problemConstructor.returnType.readableName()) : "<unknown>" }, new String[] { String.valueOf(shownConstructor.returnType.shortReadableName()), (problemConstructor.returnType != null) ? String.valueOf(problemConstructor.returnType.shortReadableName()) : "<unknown>" }, statement.sourceStart, statement.sourceEnd);
                return;
            }
            case 25: {
                final ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
                this.contradictoryNullAnnotationsInferred(problemConstructor.closestMatch, statement);
                return;
            }
            default: {
                this.needImplementation(statement);
                break;
            }
        }
        this.handle(id, new String[] { new String(targetConstructor.declaringClass.readableName()), this.typesAsString(shownConstructor, false) }, new String[] { new String(targetConstructor.declaringClass.shortReadableName()), this.typesAsString(shownConstructor, true) }, sourceStart, sourceEnd);
    }
    
    public void invalidContinue(final ASTNode location) {
        this.handle(536871085, ProblemReporter.NoArgument, ProblemReporter.NoArgument, location.sourceStart, location.sourceEnd);
    }
    
    public void invalidEnclosingType(final Expression expression, final TypeBinding type, ReferenceBinding enclosingType) {
        if (enclosingType.isAnonymousType()) {
            enclosingType = enclosingType.superclass();
        }
        if (enclosingType.sourceName != null && enclosingType.sourceName.length == 0) {
            return;
        }
        int flag = 16777218;
        switch (type.problemId()) {
            case 1: {
                flag = 16777218;
                break;
            }
            case 2: {
                flag = 16777219;
                break;
            }
            case 3: {
                flag = 16777220;
                break;
            }
            case 4: {
                flag = 16777222;
                break;
            }
            default: {
                this.needImplementation(expression);
                break;
            }
        }
        this.handle(flag, new String[] { String.valueOf(new String(enclosingType.readableName())) + "." + new String(type.readableName()) }, new String[] { String.valueOf(new String(enclosingType.shortReadableName())) + "." + new String(type.shortReadableName()) }, expression.sourceStart, expression.sourceEnd);
    }
    
    public void invalidExplicitConstructorCall(final ASTNode location) {
        this.handle(1207959691, ProblemReporter.NoArgument, ProblemReporter.NoArgument, location.sourceStart, location.sourceEnd);
    }
    
    public void invalidExpressionAsStatement(final Expression expression) {
        this.handle(1610612958, ProblemReporter.NoArgument, ProblemReporter.NoArgument, expression.sourceStart, expression.sourceEnd);
    }
    
    public void invalidField(final FieldReference fieldRef, final TypeBinding searchedType) {
        if (this.isRecoveredName(fieldRef.token)) {
            return;
        }
        int id = 33554502;
        final FieldBinding field = fieldRef.binding;
        switch (field.problemId()) {
            case 1: {
                if ((searchedType.tagBits & 0x80L) != 0x0L) {
                    this.handle(16777218, new String[] { new String(searchedType.leafComponentType().readableName()) }, new String[] { new String(searchedType.leafComponentType().shortReadableName()) }, fieldRef.receiver.sourceStart, fieldRef.receiver.sourceEnd);
                    return;
                }
                id = 33554502;
                break;
            }
            case 2: {
                this.handle(33554503, new String[] { new String(fieldRef.token), new String(field.declaringClass.readableName()) }, new String[] { new String(fieldRef.token), new String(field.declaringClass.shortReadableName()) }, this.nodeSourceStart(field, fieldRef), this.nodeSourceEnd(field, fieldRef));
                return;
            }
            case 3: {
                id = 33554504;
                break;
            }
            case 28: {
                this.noSuchEnclosingInstance(fieldRef.actualReceiverType, fieldRef.receiver, false);
                return;
            }
            case 7: {
                id = 33554506;
                break;
            }
            case 6: {
                id = 134217863;
                break;
            }
            case 5: {
                id = 33554628;
                break;
            }
            case 8: {
                this.handle(16777219, new String[] { new String(searchedType.leafComponentType().readableName()) }, new String[] { new String(searchedType.leafComponentType().shortReadableName()) }, fieldRef.receiver.sourceStart, fieldRef.receiver.sourceEnd);
                return;
            }
            default: {
                this.needImplementation(fieldRef);
                break;
            }
        }
        final String[] arguments = { new String(field.readableName()) };
        this.handle(id, arguments, arguments, this.nodeSourceStart(field, fieldRef), this.nodeSourceEnd(field, fieldRef));
    }
    
    public void invalidField(final NameReference nameRef, final FieldBinding field) {
        if (nameRef instanceof QualifiedNameReference) {
            final QualifiedNameReference ref = (QualifiedNameReference)nameRef;
            if (this.isRecoveredName(ref.tokens)) {
                return;
            }
        }
        else {
            final SingleNameReference ref2 = (SingleNameReference)nameRef;
            if (this.isRecoveredName(ref2.token)) {
                return;
            }
        }
        int id = 33554502;
        switch (field.problemId()) {
            case 1: {
                final TypeBinding declaringClass = field.declaringClass;
                if (declaringClass != null && (declaringClass.tagBits & 0x80L) != 0x0L) {
                    this.handle(16777218, new String[] { new String(field.declaringClass.readableName()) }, new String[] { new String(field.declaringClass.shortReadableName()) }, nameRef.sourceStart, nameRef.sourceEnd);
                    return;
                }
                final String[] arguments = { new String(field.readableName()) };
                this.handle(id, arguments, arguments, this.nodeSourceStart(field, nameRef), this.nodeSourceEnd(field, nameRef));
                return;
            }
            case 2: {
                char[] name = field.readableName();
                name = CharOperation.lastSegment(name, '.');
                this.handle(33554503, new String[] { new String(name), new String(field.declaringClass.readableName()) }, new String[] { new String(name), new String(field.declaringClass.shortReadableName()) }, this.nodeSourceStart(field, nameRef), this.nodeSourceEnd(field, nameRef));
                return;
            }
            case 3: {
                id = 33554504;
                break;
            }
            case 7: {
                id = 33554506;
                break;
            }
            case 6: {
                id = 134217863;
                break;
            }
            case 5: {
                id = 33554628;
                break;
            }
            case 8: {
                this.handle(16777219, new String[] { new String(field.declaringClass.readableName()) }, new String[] { new String(field.declaringClass.shortReadableName()) }, nameRef.sourceStart, nameRef.sourceEnd);
                return;
            }
            default: {
                this.needImplementation(nameRef);
                break;
            }
        }
        final String[] arguments2 = { new String(field.readableName()) };
        this.handle(id, arguments2, arguments2, nameRef.sourceStart, nameRef.sourceEnd);
    }
    
    public void invalidField(final QualifiedNameReference nameRef, final FieldBinding field, final int index, final TypeBinding searchedType) {
        if (this.isRecoveredName(nameRef.tokens)) {
            return;
        }
        if (searchedType.isBaseType()) {
            this.handle(33554653, new String[] { new String(searchedType.readableName()), CharOperation.toString(CharOperation.subarray(nameRef.tokens, 0, index)), new String(nameRef.tokens[index]) }, new String[] { new String(searchedType.sourceName()), CharOperation.toString(CharOperation.subarray(nameRef.tokens, 0, index)), new String(nameRef.tokens[index]) }, nameRef.sourceStart, (int)nameRef.sourcePositions[index]);
            return;
        }
        int id = 33554502;
        switch (field.problemId()) {
            case 1: {
                if ((searchedType.tagBits & 0x80L) != 0x0L) {
                    this.handle(16777218, new String[] { new String(searchedType.leafComponentType().readableName()) }, new String[] { new String(searchedType.leafComponentType().shortReadableName()) }, nameRef.sourceStart, (int)nameRef.sourcePositions[index - 1]);
                    return;
                }
                final String fieldName = new String(nameRef.tokens[index]);
                final String[] arguments = { fieldName };
                this.handle(id, arguments, arguments, this.nodeSourceStart(field, nameRef), this.nodeSourceEnd(field, nameRef));
                return;
            }
            case 2: {
                final String fieldName = new String(nameRef.tokens[index]);
                this.handle(33554503, new String[] { fieldName, new String(field.declaringClass.readableName()) }, new String[] { fieldName, new String(field.declaringClass.shortReadableName()) }, this.nodeSourceStart(field, nameRef), this.nodeSourceEnd(field, nameRef));
                return;
            }
            case 3: {
                id = 33554504;
                break;
            }
            case 7: {
                id = 33554506;
                break;
            }
            case 6: {
                id = 134217863;
                break;
            }
            case 5: {
                id = 33554628;
                break;
            }
            case 8: {
                this.handle(16777219, new String[] { new String(searchedType.leafComponentType().readableName()) }, new String[] { new String(searchedType.leafComponentType().shortReadableName()) }, nameRef.sourceStart, (int)nameRef.sourcePositions[index - 1]);
                return;
            }
            default: {
                this.needImplementation(nameRef);
                break;
            }
        }
        final String[] arguments2 = { CharOperation.toString(CharOperation.subarray(nameRef.tokens, 0, index + 1)) };
        this.handle(id, arguments2, arguments2, nameRef.sourceStart, (int)nameRef.sourcePositions[index]);
    }
    
    public void invalidFileNameForPackageAnnotations(final Annotation annotation) {
        this.handle(1610613338, ProblemReporter.NoArgument, ProblemReporter.NoArgument, annotation.sourceStart, annotation.sourceEnd);
    }
    
    public void invalidMethod(final MessageSend messageSend, final MethodBinding method, final Scope scope) {
        if (this.isRecoveredName(messageSend.selector)) {
            return;
        }
        int id = 67108964;
        MethodBinding shownMethod = method;
        switch (method.problemId()) {
            case 26: {
                return;
            }
            case 1: {
                if ((method.declaringClass.tagBits & 0x80L) != 0x0L) {
                    this.handle(16777218, new String[] { new String(method.declaringClass.readableName()) }, new String[] { new String(method.declaringClass.shortReadableName()) }, messageSend.receiver.sourceStart, messageSend.receiver.sourceEnd);
                    return;
                }
                id = 67108964;
                final ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
                if (problemMethod.closestMatch == null) {
                    break;
                }
                shownMethod = problemMethod.closestMatch;
                if ((shownMethod.tagBits & 0x80L) != 0x0L) {
                    this.missingTypeInMethod(messageSend, shownMethod);
                    return;
                }
                String closestParameterTypeNames = this.typesAsString(shownMethod, false);
                String parameterTypeNames = this.typesAsString(problemMethod.parameters, false);
                String closestParameterTypeShortNames = this.typesAsString(shownMethod, true);
                String parameterTypeShortNames = this.typesAsString(problemMethod.parameters, true);
                if (closestParameterTypeNames.equals(parameterTypeNames)) {
                    closestParameterTypeNames = this.typesAsString(shownMethod, false, true);
                    parameterTypeNames = this.typesAsString(problemMethod.parameters, false, true);
                    closestParameterTypeShortNames = this.typesAsString(shownMethod, true, true);
                    parameterTypeShortNames = this.typesAsString(problemMethod.parameters, true, true);
                }
                if (closestParameterTypeShortNames.equals(parameterTypeShortNames)) {
                    closestParameterTypeShortNames = closestParameterTypeNames;
                    parameterTypeShortNames = parameterTypeNames;
                }
                this.handle(67108979, new String[] { new String(shownMethod.declaringClass.readableName()), new String(shownMethod.selector), closestParameterTypeNames, parameterTypeNames }, new String[] { new String(shownMethod.declaringClass.shortReadableName()), new String(shownMethod.selector), closestParameterTypeShortNames, parameterTypeShortNames }, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            case 2: {
                id = 67108965;
                final ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
                if (problemMethod.closestMatch != null) {
                    shownMethod = problemMethod.closestMatch.original();
                    break;
                }
                break;
            }
            case 3: {
                id = 67108966;
                break;
            }
            case 5: {
                id = 67109059;
                break;
            }
            case 6: {
                id = 134217864;
                break;
            }
            case 7: {
                id = 603979977;
                break;
            }
            case 20: {
                this.handle(67108948, new String[] { new String(method.declaringClass.readableName()), new String(method.selector) }, new String[] { new String(method.declaringClass.shortReadableName()), new String(method.selector) }, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            case 29: {
                this.handle(1610613404, new String[] { new String(method.declaringClass.readableName()), new String(method.selector) }, new String[] { new String(method.declaringClass.shortReadableName()), new String(method.selector) }, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            case 8: {
                this.handle(16777219, new String[] { new String(method.declaringClass.readableName()) }, new String[] { new String(method.declaringClass.shortReadableName()) }, messageSend.receiver.sourceStart, messageSend.receiver.sourceEnd);
                return;
            }
            case 10: {
                final ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
                final ParameterizedGenericMethodBinding substitutedMethod = (ParameterizedGenericMethodBinding)problemMethod.closestMatch;
                shownMethod = substitutedMethod.original();
                final int augmentedLength = problemMethod.parameters.length;
                final TypeBinding inferredTypeArgument = problemMethod.parameters[augmentedLength - 2];
                final TypeVariableBinding typeParameter = (TypeVariableBinding)problemMethod.parameters[augmentedLength - 1];
                final TypeBinding[] invocationArguments = new TypeBinding[augmentedLength - 2];
                System.arraycopy(problemMethod.parameters, 0, invocationArguments, 0, augmentedLength - 2);
                this.handle(16777759, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()), this.typesAsString(invocationArguments, false), new String(inferredTypeArgument.readableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, false) }, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()), this.typesAsString(invocationArguments, true), new String(inferredTypeArgument.shortReadableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, true) }, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            case 11: {
                final ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
                shownMethod = problemMethod.closestMatch;
                if (shownMethod.typeVariables == Binding.NO_TYPE_VARIABLES) {
                    this.handle(16777764, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()), this.typesAsString(method, false) }, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()), this.typesAsString(method, true) }, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                }
                else {
                    this.handle(16777765, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()), this.typesAsString(shownMethod.typeVariables, false), this.typesAsString(method, false) }, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()), this.typesAsString(shownMethod.typeVariables, true), this.typesAsString(method, true) }, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                }
                return;
            }
            case 12: {
                final ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
                shownMethod = problemMethod.closestMatch;
                this.handle(16777766, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()), this.typesAsString(((ParameterizedGenericMethodBinding)shownMethod).typeArguments, false), this.typesAsString(method, false) }, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()), this.typesAsString(((ParameterizedGenericMethodBinding)shownMethod).typeArguments, true), this.typesAsString(method, true) }, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            case 13: {
                final ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
                shownMethod = problemMethod.closestMatch;
                this.handle(16777770, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()), this.typesAsString(method, false) }, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()), this.typesAsString(method, true) }, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            case 23:
            case 27: {
                final ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
                shownMethod = problemMethod.closestMatch;
                if (problemMethod.returnType == shownMethod.returnType) {
                    if (messageSend.expressionContext == ExpressionContext.VANILLA_CONTEXT) {
                        final TypeVariableBinding[] typeVariables = method.shallowOriginal().typeVariables;
                        final String typeArguments = this.typesAsString(typeVariables, false);
                        this.handle(16778275, new String[] { typeArguments, String.valueOf(shownMethod.original().readableName()) }, new String[] { typeArguments, String.valueOf(shownMethod.original().shortReadableName()) }, messageSend.sourceStart, messageSend.sourceEnd);
                    }
                    return;
                }
                final TypeBinding shownMethodReturnType = shownMethod.returnType.capture(scope, messageSend.sourceStart, messageSend.sourceEnd);
                this.handle(16777233, new String[] { String.valueOf(shownMethodReturnType.readableName()), (problemMethod.returnType != null) ? String.valueOf(problemMethod.returnType.readableName()) : "<unknown>" }, new String[] { String.valueOf(shownMethodReturnType.shortReadableName()), (problemMethod.returnType != null) ? String.valueOf(problemMethod.returnType.shortReadableName()) : "<unknown>" }, messageSend.sourceStart, messageSend.sourceEnd);
                return;
            }
            case 16: {
                final ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
                if (problemMethod.closestMatch != null) {
                    shownMethod = problemMethod.closestMatch.original();
                }
                final TypeBinding varargsElementType = shownMethod.parameters[shownMethod.parameters.length - 1].leafComponentType();
                this.handle(67109671, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()), new String(varargsElementType.readableName()) }, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()), new String(varargsElementType.shortReadableName()) }, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            case 24: {
                final ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
                if (problemMethod.closestMatch != null) {
                    shownMethod = problemMethod.closestMatch.original();
                }
                this.handle(67109673, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()) }, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()) }, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            case 25: {
                final ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
                this.contradictoryNullAnnotationsInferred(problemMethod.closestMatch, messageSend);
                return;
            }
            default: {
                this.needImplementation(messageSend);
                break;
            }
        }
        this.handle(id, new String[] { new String(method.declaringClass.readableName()), new String(shownMethod.selector), this.typesAsString(shownMethod, false) }, new String[] { new String(method.declaringClass.shortReadableName()), new String(shownMethod.selector), this.typesAsString(shownMethod, true) }, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
    }
    
    public void invalidNullToSynchronize(final Expression expression) {
        this.handle(536871088, ProblemReporter.NoArgument, ProblemReporter.NoArgument, expression.sourceStart, expression.sourceEnd);
    }
    
    public void invalidOperator(final BinaryExpression expression, final TypeBinding leftType, final TypeBinding rightType) {
        final String leftName = new String(leftType.readableName());
        final String rightName = new String(rightType.readableName());
        String leftShortName = new String(leftType.shortReadableName());
        String rightShortName = new String(rightType.shortReadableName());
        if (leftShortName.equals(rightShortName)) {
            leftShortName = leftName;
            rightShortName = rightName;
        }
        this.handle(536871072, new String[] { expression.operatorToString(), String.valueOf(leftName) + ", " + rightName }, new String[] { expression.operatorToString(), String.valueOf(leftShortName) + ", " + rightShortName }, expression.sourceStart, expression.sourceEnd);
    }
    
    public void invalidOperator(final CompoundAssignment assign, final TypeBinding leftType, final TypeBinding rightType) {
        final String leftName = new String(leftType.readableName());
        final String rightName = new String(rightType.readableName());
        String leftShortName = new String(leftType.shortReadableName());
        String rightShortName = new String(rightType.shortReadableName());
        if (leftShortName.equals(rightShortName)) {
            leftShortName = leftName;
            rightShortName = rightName;
        }
        this.handle(536871072, new String[] { assign.operatorToString(), String.valueOf(leftName) + ", " + rightName }, new String[] { assign.operatorToString(), String.valueOf(leftShortName) + ", " + rightShortName }, assign.sourceStart, assign.sourceEnd);
    }
    
    public void invalidOperator(final UnaryExpression expression, final TypeBinding type) {
        this.handle(536871072, new String[] { expression.operatorToString(), new String(type.readableName()) }, new String[] { expression.operatorToString(), new String(type.shortReadableName()) }, expression.sourceStart, expression.sourceEnd);
    }
    
    public void invalidParameterizedExceptionType(final TypeBinding exceptionType, final ASTNode location) {
        this.handle(16777750, new String[] { new String(exceptionType.readableName()) }, new String[] { new String(exceptionType.shortReadableName()) }, location.sourceStart, location.sourceEnd);
    }
    
    public void invalidParenthesizedExpression(final ASTNode reference) {
        this.handle(1610612961, ProblemReporter.NoArgument, ProblemReporter.NoArgument, reference.sourceStart, reference.sourceEnd);
    }
    
    public void invalidType(final ASTNode location, final TypeBinding type) {
        if (type instanceof ReferenceBinding) {
            if (this.isRecoveredName(((ReferenceBinding)type).compoundName)) {
                return;
            }
        }
        else if (type instanceof ArrayBinding) {
            final TypeBinding leafType = ((ArrayBinding)type).leafComponentType;
            if (leafType instanceof ReferenceBinding && this.isRecoveredName(((ReferenceBinding)leafType).compoundName)) {
                return;
            }
        }
        if (type.isParameterizedType()) {
            final List missingTypes = type.collectMissingTypes(null);
            if (missingTypes != null) {
                final ReferenceContext savedContext = this.referenceContext;
                final Iterator iterator = missingTypes.iterator();
                while (iterator.hasNext()) {
                    try {
                        this.invalidType(location, iterator.next());
                    }
                    finally {
                        this.referenceContext = savedContext;
                    }
                    this.referenceContext = savedContext;
                }
                return;
            }
        }
        int id = 16777218;
        switch (type.problemId()) {
            case 1: {
                id = 16777218;
                break;
            }
            case 2: {
                id = 16777219;
                break;
            }
            case 3: {
                id = 16777220;
                break;
            }
            case 4: {
                id = 16777222;
                break;
            }
            case 5: {
                id = 16777413;
                break;
            }
            case 7: {
                id = 536871434;
                break;
            }
            case 9: {
                id = 536871433;
                break;
            }
            default: {
                this.needImplementation(location);
                break;
            }
        }
        int end = location.sourceEnd;
        if (location instanceof QualifiedNameReference) {
            final QualifiedNameReference ref = (QualifiedNameReference)location;
            if (this.isRecoveredName(ref.tokens)) {
                return;
            }
            if (ref.indexOfFirstFieldBinding >= 1) {
                end = (int)ref.sourcePositions[ref.indexOfFirstFieldBinding - 1];
            }
        }
        else if (location instanceof ParameterizedQualifiedTypeReference) {
            final ParameterizedQualifiedTypeReference ref2 = (ParameterizedQualifiedTypeReference)location;
            if (this.isRecoveredName(ref2.tokens)) {
                return;
            }
            if (type instanceof ReferenceBinding) {
                final char[][] name = ((ReferenceBinding)type).compoundName;
                end = (int)ref2.sourcePositions[name.length - 1];
            }
        }
        else if (location instanceof ArrayQualifiedTypeReference) {
            final ArrayQualifiedTypeReference arrayQualifiedTypeReference = (ArrayQualifiedTypeReference)location;
            if (this.isRecoveredName(arrayQualifiedTypeReference.tokens)) {
                return;
            }
            final TypeBinding leafType2 = type.leafComponentType();
            if (leafType2 instanceof ReferenceBinding) {
                final char[][] name2 = ((ReferenceBinding)leafType2).compoundName;
                end = (int)arrayQualifiedTypeReference.sourcePositions[name2.length - 1];
            }
            else {
                final long[] positions = arrayQualifiedTypeReference.sourcePositions;
                end = (int)positions[positions.length - 1];
            }
        }
        else if (location instanceof QualifiedTypeReference) {
            final QualifiedTypeReference ref3 = (QualifiedTypeReference)location;
            if (this.isRecoveredName(ref3.tokens)) {
                return;
            }
            if (type instanceof ReferenceBinding) {
                final char[][] name = ((ReferenceBinding)type).compoundName;
                if (name.length <= ref3.sourcePositions.length) {
                    end = (int)ref3.sourcePositions[name.length - 1];
                }
            }
        }
        else if (location instanceof ImportReference) {
            final ImportReference ref4 = (ImportReference)location;
            if (this.isRecoveredName(ref4.tokens)) {
                return;
            }
            if (type instanceof ReferenceBinding) {
                final char[][] name = ((ReferenceBinding)type).compoundName;
                end = (int)ref4.sourcePositions[name.length - 1];
            }
        }
        else if (location instanceof ArrayTypeReference) {
            final ArrayTypeReference arrayTypeReference = (ArrayTypeReference)location;
            if (this.isRecoveredName(arrayTypeReference.token)) {
                return;
            }
            end = arrayTypeReference.originalSourceEnd;
        }
        int start = location.sourceStart;
        if (location instanceof SingleTypeReference) {
            final SingleTypeReference ref5 = (SingleTypeReference)location;
            if (ref5.annotations != null) {
                start = end - ref5.token.length + 1;
            }
        }
        else if (location instanceof QualifiedTypeReference) {
            final QualifiedTypeReference ref6 = (QualifiedTypeReference)location;
            if (ref6.annotations != null) {
                start = (int)(ref6.sourcePositions[0] & 0xFFFFFFFFL) - ref6.tokens[0].length + 1;
            }
        }
        this.handle(id, new String[] { new String(type.leafComponentType().readableName()) }, new String[] { new String(type.leafComponentType().shortReadableName()) }, start, end);
    }
    
    public void invalidTypeForCollection(final Expression expression) {
        this.handle(536871493, ProblemReporter.NoArgument, ProblemReporter.NoArgument, expression.sourceStart, expression.sourceEnd);
    }
    
    public void invalidTypeForCollectionTarget14(final Expression expression) {
        this.handle(536871494, ProblemReporter.NoArgument, ProblemReporter.NoArgument, expression.sourceStart, expression.sourceEnd);
    }
    
    public void invalidTypeToSynchronize(final Expression expression, final TypeBinding type) {
        this.handle(536871087, new String[] { new String(type.readableName()) }, new String[] { new String(type.shortReadableName()) }, expression.sourceStart, expression.sourceEnd);
    }
    
    public void invalidTypeVariableAsException(final TypeBinding exceptionType, final ASTNode location) {
        this.handle(16777749, new String[] { new String(exceptionType.readableName()) }, new String[] { new String(exceptionType.shortReadableName()) }, location.sourceStart, location.sourceEnd);
    }
    
    public void invalidUnaryExpression(final Expression expression) {
        this.handle(1610612942, ProblemReporter.NoArgument, ProblemReporter.NoArgument, expression.sourceStart, expression.sourceEnd);
    }
    
    public void invalidUsageOfAnnotation(final Annotation annotation) {
        this.handle(1610613332, ProblemReporter.NoArgument, ProblemReporter.NoArgument, annotation.sourceStart, annotation.sourceEnd);
    }
    
    public void invalidUsageOfAnnotationDeclarations(final TypeDeclaration annotationTypeDeclaration) {
        this.handle(1610613333, ProblemReporter.NoArgument, ProblemReporter.NoArgument, annotationTypeDeclaration.sourceStart, annotationTypeDeclaration.sourceEnd);
    }
    
    public void invalidUsageOfEnumDeclarations(final TypeDeclaration enumDeclaration) {
        this.handle(1610613330, ProblemReporter.NoArgument, ProblemReporter.NoArgument, enumDeclaration.sourceStart, enumDeclaration.sourceEnd);
    }
    
    public void invalidUsageOfForeachStatements(final LocalDeclaration elementVariable, final Expression collection) {
        this.handle(1610613328, ProblemReporter.NoArgument, ProblemReporter.NoArgument, elementVariable.declarationSourceStart, collection.sourceEnd);
    }
    
    public void invalidUsageOfStaticImports(final ImportReference staticImport) {
        this.handle(1610613327, ProblemReporter.NoArgument, ProblemReporter.NoArgument, staticImport.declarationSourceStart, staticImport.declarationSourceEnd);
    }
    
    public void invalidUsageOfTypeArguments(final TypeReference firstTypeReference, final TypeReference lastTypeReference) {
        this.handle(1610613329, ProblemReporter.NoArgument, ProblemReporter.NoArgument, firstTypeReference.sourceStart, lastTypeReference.sourceEnd);
    }
    
    public void invalidUsageOfTypeParameters(final TypeParameter firstTypeParameter, final TypeParameter lastTypeParameter) {
        this.handle(1610613326, ProblemReporter.NoArgument, ProblemReporter.NoArgument, firstTypeParameter.declarationSourceStart, lastTypeParameter.declarationSourceEnd);
    }
    
    public void invalidUsageOfTypeParametersForAnnotationDeclaration(final TypeDeclaration annotationTypeDeclaration) {
        final TypeParameter[] parameters = annotationTypeDeclaration.typeParameters;
        final int length = parameters.length;
        this.handle(1610613334, ProblemReporter.NoArgument, ProblemReporter.NoArgument, parameters[0].declarationSourceStart, parameters[length - 1].declarationSourceEnd);
    }
    
    public void invalidUsageOfTypeParametersForEnumDeclaration(final TypeDeclaration annotationTypeDeclaration) {
        final TypeParameter[] parameters = annotationTypeDeclaration.typeParameters;
        final int length = parameters.length;
        this.handle(1610613335, ProblemReporter.NoArgument, ProblemReporter.NoArgument, parameters[0].declarationSourceStart, parameters[length - 1].declarationSourceEnd);
    }
    
    public void invalidUsageOfVarargs(final Argument argument) {
        this.handle(1610613331, ProblemReporter.NoArgument, ProblemReporter.NoArgument, argument.type.sourceStart, argument.sourceEnd);
    }
    
    public void invalidUsageOfTypeAnnotations(final Annotation annotation) {
        this.handle(1610613373, ProblemReporter.NoArgument, ProblemReporter.NoArgument, annotation.sourceStart, annotation.sourceEnd);
    }
    
    public void toleratedMisplacedTypeAnnotations(final Annotation first, final Annotation last) {
        this.handle(1610613402, ProblemReporter.NoArgument, ProblemReporter.NoArgument, first.sourceStart, last.sourceEnd);
    }
    
    public void misplacedTypeAnnotations(final Annotation first, final Annotation last) {
        this.handle(1610613375, ProblemReporter.NoArgument, ProblemReporter.NoArgument, first.sourceStart, last.sourceEnd);
    }
    
    public void illegalUsageOfTypeAnnotations(final Annotation annotation) {
        this.handle(1610613377, ProblemReporter.NoArgument, ProblemReporter.NoArgument, annotation.sourceStart, annotation.sourceEnd);
    }
    
    public void illegalTypeAnnotationsInStaticMemberAccess(final Annotation first, final Annotation last) {
        this.handle(1610613376, ProblemReporter.NoArgument, ProblemReporter.NoArgument, first.sourceStart, last.sourceEnd);
    }
    
    public void isClassPathCorrect(final char[][] wellKnownTypeName, final CompilationUnitDeclaration compUnitDecl, final Object location) {
        final ReferenceContext savedContext = this.referenceContext;
        this.referenceContext = compUnitDecl;
        final String[] arguments = { CharOperation.toString(wellKnownTypeName) };
        int start = 0;
        int end = 0;
        if (location != null) {
            if (location instanceof InvocationSite) {
                final InvocationSite site = (InvocationSite)location;
                start = site.sourceStart();
                end = site.sourceEnd();
            }
            else if (location instanceof ASTNode) {
                final ASTNode node = (ASTNode)location;
                start = node.sourceStart();
                end = node.sourceEnd();
            }
        }
        try {
            this.handle(16777540, arguments, arguments, start, end);
        }
        finally {
            this.referenceContext = savedContext;
        }
        this.referenceContext = savedContext;
    }
    
    private boolean isIdentifier(final int token) {
        return token == 22;
    }
    
    private boolean isKeyword(final int token) {
        switch (token) {
            case 17:
            case 34:
            case 35:
            case 36:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 67:
            case 68:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 95:
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
            case 111:
            case 112:
            case 114: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private boolean isLiteral(final int token) {
        return Scanner.isLiteral(token);
    }
    
    private boolean isRecoveredName(final char[] simpleName) {
        return simpleName == RecoveryScanner.FAKE_IDENTIFIER;
    }
    
    private boolean isRecoveredName(final char[][] qualifiedName) {
        if (qualifiedName == null) {
            return false;
        }
        for (int i = 0; i < qualifiedName.length; ++i) {
            if (qualifiedName[i] == RecoveryScanner.FAKE_IDENTIFIER) {
                return true;
            }
        }
        return false;
    }
    
    public void javadocAmbiguousMethodReference(final int sourceStart, final int sourceEnd, final Binding fieldBinding, final int modifiers) {
        final int severity = this.computeSeverity(-1610612225);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            final String[] arguments = { new String(fieldBinding.readableName()) };
            this.handle(-1610612225, arguments, arguments, severity, sourceStart, sourceEnd);
        }
    }
    
    public void javadocDeprecatedField(final FieldBinding field, final ASTNode location, final int modifiers) {
        final int severity = this.computeSeverity(-1610612245);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            this.handle(-1610612245, new String[] { new String(field.declaringClass.readableName()), new String(field.name) }, new String[] { new String(field.declaringClass.shortReadableName()), new String(field.name) }, severity, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
        }
    }
    
    public void javadocDeprecatedMethod(final MethodBinding method, final ASTNode location, final int modifiers) {
        final boolean isConstructor = method.isConstructor();
        final int severity = this.computeSeverity(isConstructor ? -1610612241 : -1610612237);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            if (isConstructor) {
                this.handle(-1610612241, new String[] { new String(method.declaringClass.readableName()), this.typesAsString(method, false) }, new String[] { new String(method.declaringClass.shortReadableName()), this.typesAsString(method, true) }, severity, location.sourceStart, location.sourceEnd);
            }
            else {
                this.handle(-1610612237, new String[] { new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false) }, new String[] { new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true) }, severity, location.sourceStart, location.sourceEnd);
            }
        }
    }
    
    public void javadocDeprecatedType(final TypeBinding type, final ASTNode location, final int modifiers) {
        this.javadocDeprecatedType(type, location, modifiers, Integer.MAX_VALUE);
    }
    
    public void javadocDeprecatedType(final TypeBinding type, final ASTNode location, final int modifiers, final int index) {
        if (location == null) {
            return;
        }
        final int severity = this.computeSeverity(-1610612230);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            if (type.isMemberType() && type instanceof ReferenceBinding && !this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, ((ReferenceBinding)type).modifiers)) {
                this.handle(-1610612271, ProblemReporter.NoArgument, ProblemReporter.NoArgument, location.sourceStart, location.sourceEnd);
            }
            else {
                this.handle(-1610612230, new String[] { new String(type.readableName()) }, new String[] { new String(type.shortReadableName()) }, severity, location.sourceStart, this.nodeSourceEnd(null, location, index));
            }
        }
    }
    
    public void javadocDuplicatedParamTag(final char[] token, final int sourceStart, final int sourceEnd, final int modifiers) {
        final int severity = this.computeSeverity(-1610612263);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            final String[] arguments = { String.valueOf(token) };
            this.handle(-1610612263, arguments, arguments, severity, sourceStart, sourceEnd);
        }
    }
    
    public void javadocDuplicatedReturnTag(final int sourceStart, final int sourceEnd) {
        this.handle(-1610612260, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    public void javadocDuplicatedTag(final char[] tagName, final int sourceStart, final int sourceEnd) {
        final String[] arguments = { new String(tagName) };
        this.handle(-1610612272, arguments, arguments, sourceStart, sourceEnd);
    }
    
    public void javadocDuplicatedThrowsClassName(final TypeReference typeReference, final int modifiers) {
        final int severity = this.computeSeverity(-1610612256);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            final String[] arguments = { String.valueOf(typeReference.resolvedType.sourceName()) };
            this.handle(-1610612256, arguments, arguments, severity, typeReference.sourceStart, typeReference.sourceEnd);
        }
    }
    
    public void javadocEmptyReturnTag(final int sourceStart, final int sourceEnd, final int modifiers) {
        final int severity = this.computeSeverity(-1610612220);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            final String[] arguments = { new String(JavadocTagConstants.TAG_RETURN) };
            this.handle(-1610612220, arguments, arguments, sourceStart, sourceEnd);
        }
    }
    
    public void javadocErrorNoMethodFor(final MessageSend messageSend, final TypeBinding recType, final TypeBinding[] params, final int modifiers) {
        final int id = recType.isArrayType() ? -1610612234 : -1610612236;
        final int severity = this.computeSeverity(id);
        if (severity == 256) {
            return;
        }
        final StringBuffer buffer = new StringBuffer();
        final StringBuffer shortBuffer = new StringBuffer();
        for (int i = 0, length = params.length; i < length; ++i) {
            if (i != 0) {
                buffer.append(", ");
                shortBuffer.append(", ");
            }
            buffer.append(new String(params[i].readableName()));
            shortBuffer.append(new String(params[i].shortReadableName()));
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            this.handle(id, new String[] { new String(recType.readableName()), new String(messageSend.selector), buffer.toString() }, new String[] { new String(recType.shortReadableName()), new String(messageSend.selector), shortBuffer.toString() }, severity, messageSend.sourceStart, messageSend.sourceEnd);
        }
    }
    
    public void javadocHiddenReference(final int sourceStart, final int sourceEnd, final Scope scope, final int modifiers) {
        for (Scope currentScope = scope; currentScope.parent.kind != 4; currentScope = currentScope.parent) {
            if (!this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, currentScope.getDeclarationModifiers())) {
                return;
            }
        }
        final String[] arguments = { this.options.getVisibilityString(this.options.reportInvalidJavadocTagsVisibility), this.options.getVisibilityString(modifiers) };
        this.handle(-1610612271, arguments, arguments, sourceStart, sourceEnd);
    }
    
    public void javadocInvalidConstructor(final Statement statement, final MethodBinding targetConstructor, final int modifiers) {
        if (!this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            return;
        }
        int sourceStart = statement.sourceStart;
        int sourceEnd = statement.sourceEnd;
        if (statement instanceof AllocationExpression) {
            final AllocationExpression allocation = (AllocationExpression)statement;
            if (allocation.enumConstant != null) {
                sourceStart = allocation.enumConstant.sourceStart;
                sourceEnd = allocation.enumConstant.sourceEnd;
            }
        }
        int id = -1610612244;
        ProblemMethodBinding problemConstructor = null;
        MethodBinding shownConstructor = null;
        switch (targetConstructor.problemId()) {
            case 1: {
                id = -1610612244;
                break;
            }
            case 2: {
                id = -1610612243;
                break;
            }
            case 3: {
                id = -1610612242;
                break;
            }
            case 10: {
                final int severity = this.computeSeverity(-1610611881);
                if (severity == 256) {
                    return;
                }
                problemConstructor = (ProblemMethodBinding)targetConstructor;
                final ParameterizedGenericMethodBinding substitutedConstructor = (ParameterizedGenericMethodBinding)problemConstructor.closestMatch;
                shownConstructor = substitutedConstructor.original();
                final int augmentedLength = problemConstructor.parameters.length;
                final TypeBinding inferredTypeArgument = problemConstructor.parameters[augmentedLength - 2];
                final TypeVariableBinding typeParameter = (TypeVariableBinding)problemConstructor.parameters[augmentedLength - 1];
                final TypeBinding[] invocationArguments = new TypeBinding[augmentedLength - 2];
                System.arraycopy(problemConstructor.parameters, 0, invocationArguments, 0, augmentedLength - 2);
                this.handle(-1610611881, new String[] { new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, false), new String(shownConstructor.declaringClass.readableName()), this.typesAsString(invocationArguments, false), new String(inferredTypeArgument.readableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, false) }, new String[] { new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, true), new String(shownConstructor.declaringClass.shortReadableName()), this.typesAsString(invocationArguments, true), new String(inferredTypeArgument.shortReadableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, true) }, severity, sourceStart, sourceEnd);
                return;
            }
            case 11: {
                problemConstructor = (ProblemMethodBinding)targetConstructor;
                shownConstructor = problemConstructor.closestMatch;
                final boolean noTypeVariables = shownConstructor.typeVariables == Binding.NO_TYPE_VARIABLES;
                final int severity = this.computeSeverity(noTypeVariables ? -1610611880 : -1610611879);
                if (severity == 256) {
                    return;
                }
                if (noTypeVariables) {
                    this.handle(-1610611880, new String[] { new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, false), new String(shownConstructor.declaringClass.readableName()), this.typesAsString(targetConstructor, false) }, new String[] { new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, true), new String(shownConstructor.declaringClass.shortReadableName()), this.typesAsString(targetConstructor, true) }, severity, sourceStart, sourceEnd);
                }
                else {
                    this.handle(-1610611879, new String[] { new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, false), new String(shownConstructor.declaringClass.readableName()), this.typesAsString(shownConstructor.typeVariables, false), this.typesAsString(targetConstructor, false) }, new String[] { new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, true), new String(shownConstructor.declaringClass.shortReadableName()), this.typesAsString(shownConstructor.typeVariables, true), this.typesAsString(targetConstructor, true) }, severity, sourceStart, sourceEnd);
                }
                return;
            }
            case 12: {
                final int severity = this.computeSeverity(-1610611878);
                if (severity == 256) {
                    return;
                }
                problemConstructor = (ProblemMethodBinding)targetConstructor;
                shownConstructor = problemConstructor.closestMatch;
                this.handle(-1610611878, new String[] { new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, false), new String(shownConstructor.declaringClass.readableName()), this.typesAsString(((ParameterizedGenericMethodBinding)shownConstructor).typeArguments, false), this.typesAsString(targetConstructor, false) }, new String[] { new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, true), new String(shownConstructor.declaringClass.shortReadableName()), this.typesAsString(((ParameterizedGenericMethodBinding)shownConstructor).typeArguments, true), this.typesAsString(targetConstructor, true) }, severity, sourceStart, sourceEnd);
                return;
            }
            case 13: {
                final int severity = this.computeSeverity(-1610611877);
                if (severity == 256) {
                    return;
                }
                problemConstructor = (ProblemMethodBinding)targetConstructor;
                shownConstructor = problemConstructor.closestMatch;
                this.handle(-1610611877, new String[] { new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, false), new String(shownConstructor.declaringClass.readableName()), this.typesAsString(targetConstructor, false) }, new String[] { new String(shownConstructor.declaringClass.sourceName()), this.typesAsString(shownConstructor, true), new String(shownConstructor.declaringClass.shortReadableName()), this.typesAsString(targetConstructor, true) }, severity, sourceStart, sourceEnd);
                return;
            }
            default: {
                this.needImplementation(statement);
                break;
            }
        }
        final int severity = this.computeSeverity(id);
        if (severity == 256) {
            return;
        }
        this.handle(id, new String[] { new String(targetConstructor.declaringClass.readableName()), this.typesAsString(targetConstructor, false) }, new String[] { new String(targetConstructor.declaringClass.shortReadableName()), this.typesAsString(targetConstructor, true) }, severity, statement.sourceStart, statement.sourceEnd);
    }
    
    public void javadocInvalidField(final FieldReference fieldRef, final Binding fieldBinding, final TypeBinding searchedType, final int modifiers) {
        int id = -1610612248;
        switch (fieldBinding.problemId()) {
            case 1: {
                id = -1610612248;
                break;
            }
            case 2: {
                id = -1610612247;
                break;
            }
            case 3: {
                id = -1610612246;
                break;
            }
            default: {
                this.needImplementation(fieldRef);
                break;
            }
        }
        final int severity = this.computeSeverity(id);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            final String[] arguments = { new String(fieldBinding.readableName()) };
            this.handle(id, arguments, arguments, severity, fieldRef.sourceStart, fieldRef.sourceEnd);
        }
    }
    
    public void javadocInvalidMemberTypeQualification(final int sourceStart, final int sourceEnd, final int modifiers) {
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            this.handle(-1610612270, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
        }
    }
    
    public void javadocInvalidMethod(final MessageSend messageSend, final MethodBinding method, final int modifiers) {
        if (!this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            return;
        }
        ProblemMethodBinding problemMethod = null;
        MethodBinding shownMethod = null;
        int id = -1610612240;
        switch (method.problemId()) {
            case 1: {
                id = -1610612240;
                problemMethod = (ProblemMethodBinding)method;
                if (problemMethod.closestMatch == null) {
                    break;
                }
                final int severity = this.computeSeverity(-1610612235);
                if (severity == 256) {
                    return;
                }
                final String closestParameterTypeNames = this.typesAsString(problemMethod.closestMatch, false);
                final String parameterTypeNames = this.typesAsString(method, false);
                String closestParameterTypeShortNames = this.typesAsString(problemMethod.closestMatch, true);
                String parameterTypeShortNames = this.typesAsString(method, true);
                if (closestParameterTypeShortNames.equals(parameterTypeShortNames)) {
                    closestParameterTypeShortNames = closestParameterTypeNames;
                    parameterTypeShortNames = parameterTypeNames;
                }
                this.handle(-1610612235, new String[] { new String(problemMethod.closestMatch.declaringClass.readableName()), new String(problemMethod.closestMatch.selector), closestParameterTypeNames, parameterTypeNames }, new String[] { new String(problemMethod.closestMatch.declaringClass.shortReadableName()), new String(problemMethod.closestMatch.selector), closestParameterTypeShortNames, parameterTypeShortNames }, severity, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            case 2: {
                id = -1610612239;
                break;
            }
            case 3: {
                id = -1610612238;
                break;
            }
            case 10: {
                final int severity = this.computeSeverity(-1610611886);
                if (severity == 256) {
                    return;
                }
                problemMethod = (ProblemMethodBinding)method;
                final ParameterizedGenericMethodBinding substitutedMethod = (ParameterizedGenericMethodBinding)problemMethod.closestMatch;
                shownMethod = substitutedMethod.original();
                final int augmentedLength = problemMethod.parameters.length;
                final TypeBinding inferredTypeArgument = problemMethod.parameters[augmentedLength - 2];
                final TypeVariableBinding typeParameter = (TypeVariableBinding)problemMethod.parameters[augmentedLength - 1];
                final TypeBinding[] invocationArguments = new TypeBinding[augmentedLength - 2];
                System.arraycopy(problemMethod.parameters, 0, invocationArguments, 0, augmentedLength - 2);
                this.handle(-1610611886, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()), this.typesAsString(invocationArguments, false), new String(inferredTypeArgument.readableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, false) }, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()), this.typesAsString(invocationArguments, true), new String(inferredTypeArgument.shortReadableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, true) }, severity, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            case 11: {
                problemMethod = (ProblemMethodBinding)method;
                shownMethod = problemMethod.closestMatch;
                final boolean noTypeVariables = shownMethod.typeVariables == Binding.NO_TYPE_VARIABLES;
                final int severity = this.computeSeverity(noTypeVariables ? -1610611885 : -1610611884);
                if (severity == 256) {
                    return;
                }
                if (noTypeVariables) {
                    this.handle(-1610611885, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()), this.typesAsString(method, false) }, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()), this.typesAsString(method, true) }, severity, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                }
                else {
                    this.handle(-1610611884, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()), this.typesAsString(shownMethod.typeVariables, false), this.typesAsString(method, false) }, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()), this.typesAsString(shownMethod.typeVariables, true), this.typesAsString(method, true) }, severity, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                }
                return;
            }
            case 12: {
                final int severity = this.computeSeverity(-1610611883);
                if (severity == 256) {
                    return;
                }
                problemMethod = (ProblemMethodBinding)method;
                shownMethod = problemMethod.closestMatch;
                this.handle(-1610611883, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()), this.typesAsString(((ParameterizedGenericMethodBinding)shownMethod).typeArguments, false), this.typesAsString(method, false) }, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()), this.typesAsString(((ParameterizedGenericMethodBinding)shownMethod).typeArguments, true), this.typesAsString(method, true) }, severity, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            case 13: {
                final int severity = this.computeSeverity(-1610611882);
                if (severity == 256) {
                    return;
                }
                problemMethod = (ProblemMethodBinding)method;
                shownMethod = problemMethod.closestMatch;
                this.handle(-1610611882, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, false), new String(shownMethod.declaringClass.readableName()), this.typesAsString(method, false) }, new String[] { new String(shownMethod.selector), this.typesAsString(shownMethod, true), new String(shownMethod.declaringClass.shortReadableName()), this.typesAsString(method, true) }, severity, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
                return;
            }
            default: {
                this.needImplementation(messageSend);
                break;
            }
        }
        final int severity = this.computeSeverity(id);
        if (severity == 256) {
            return;
        }
        this.handle(id, new String[] { new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false) }, new String[] { new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true) }, severity, (int)(messageSend.nameSourcePosition >>> 32), (int)messageSend.nameSourcePosition);
    }
    
    public void javadocInvalidParamTagName(final int sourceStart, final int sourceEnd) {
        this.handle(-1610612217, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    public void javadocInvalidParamTypeParameter(final int sourceStart, final int sourceEnd) {
        this.handle(-1610612267, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    public void javadocInvalidReference(final int sourceStart, final int sourceEnd) {
        this.handle(-1610612253, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    public void javadocInvalidSeeHref(final int sourceStart, final int sourceEnd) {
        this.handle(-1610612252, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    public void javadocInvalidSeeReferenceArgs(final int sourceStart, final int sourceEnd) {
        this.handle(-1610612251, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    public void javadocInvalidSeeUrlReference(final int sourceStart, final int sourceEnd) {
        this.handle(-1610612274, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    public void javadocInvalidTag(final int sourceStart, final int sourceEnd) {
        this.handle(-1610612249, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    public void javadocInvalidThrowsClass(final int sourceStart, final int sourceEnd) {
        this.handle(-1610612257, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    public void javadocInvalidThrowsClassName(final TypeReference typeReference, final int modifiers) {
        final int severity = this.computeSeverity(-1610612255);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            final String[] arguments = { String.valueOf(typeReference.resolvedType.sourceName()) };
            this.handle(-1610612255, arguments, arguments, severity, typeReference.sourceStart, typeReference.sourceEnd);
        }
    }
    
    public void javadocInvalidType(final ASTNode location, final TypeBinding type, final int modifiers) {
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            int id = -1610612233;
            switch (type.problemId()) {
                case 1: {
                    id = -1610612233;
                    break;
                }
                case 2: {
                    id = -1610612232;
                    break;
                }
                case 3: {
                    id = -1610612231;
                    break;
                }
                case 4: {
                    id = -1610612229;
                    break;
                }
                case 5: {
                    id = -1610612226;
                    break;
                }
                case 7: {
                    id = -1610612268;
                    break;
                }
                default: {
                    this.needImplementation(location);
                    break;
                }
            }
            final int severity = this.computeSeverity(id);
            if (severity == 256) {
                return;
            }
            this.handle(id, new String[] { new String(type.readableName()) }, new String[] { new String(type.shortReadableName()) }, severity, location.sourceStart, location.sourceEnd);
        }
    }
    
    public void javadocInvalidValueReference(final int sourceStart, final int sourceEnd, final int modifiers) {
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            this.handle(-1610612219, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
        }
    }
    
    public void javadocMalformedSeeReference(final int sourceStart, final int sourceEnd) {
        this.handle(-1610612223, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    public void javadocMissing(final int sourceStart, final int sourceEnd, final int modifiers) {
        final int severity = this.computeSeverity(-1610612250);
        this.javadocMissing(sourceStart, sourceEnd, severity, modifiers);
    }
    
    public void javadocMissing(final int sourceStart, final int sourceEnd, final int severity, final int modifiers) {
        if (severity == 256) {
            return;
        }
        final boolean overriding = (modifiers & 0x30000000) != 0x0;
        final boolean report = this.options.getSeverity(1048576) != 256 && (!overriding || this.options.reportMissingJavadocCommentsOverriding);
        if (report) {
            final String arg = this.javadocVisibilityArgument(this.options.reportMissingJavadocCommentsVisibility, modifiers);
            if (arg != null) {
                final String[] arguments = { arg };
                this.handle(-1610612250, arguments, arguments, severity, sourceStart, sourceEnd);
            }
        }
    }
    
    public void javadocMissingHashCharacter(final int sourceStart, final int sourceEnd, final String ref) {
        final int severity = this.computeSeverity(-1610612221);
        if (severity == 256) {
            return;
        }
        final String[] arguments = { ref };
        this.handle(-1610612221, arguments, arguments, severity, sourceStart, sourceEnd);
    }
    
    public void javadocMissingIdentifier(final int sourceStart, final int sourceEnd, final int modifiers) {
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            this.handle(-1610612269, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
        }
    }
    
    public void javadocMissingParamName(final int sourceStart, final int sourceEnd, final int modifiers) {
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            this.handle(-1610612264, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
        }
    }
    
    public void javadocMissingParamTag(final char[] name, final int sourceStart, final int sourceEnd, final int modifiers) {
        final int severity = this.computeSeverity(-1610612265);
        if (severity == 256) {
            return;
        }
        final boolean overriding = (modifiers & 0x30000000) != 0x0;
        final boolean report = this.options.getSeverity(2097152) != 256 && (!overriding || this.options.reportMissingJavadocTagsOverriding);
        if (report && this.javadocVisibility(this.options.reportMissingJavadocTagsVisibility, modifiers)) {
            final String[] arguments = { String.valueOf(name) };
            this.handle(-1610612265, arguments, arguments, severity, sourceStart, sourceEnd);
        }
    }
    
    public void javadocMissingReference(final int sourceStart, final int sourceEnd, final int modifiers) {
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            this.handle(-1610612254, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
        }
    }
    
    public void javadocMissingReturnTag(final int sourceStart, final int sourceEnd, final int modifiers) {
        final boolean overriding = (modifiers & 0x30000000) != 0x0;
        final boolean report = this.options.getSeverity(2097152) != 256 && (!overriding || this.options.reportMissingJavadocTagsOverriding);
        if (report && this.javadocVisibility(this.options.reportMissingJavadocTagsVisibility, modifiers)) {
            this.handle(-1610612261, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
        }
    }
    
    public void javadocMissingTagDescription(final char[] tokenName, final int sourceStart, final int sourceEnd, final int modifiers) {
        final int severity = this.computeSeverity(-1610612273);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            final String[] arguments = { new String(tokenName) };
            this.handle(-1610612220, arguments, arguments, sourceStart, sourceEnd);
        }
    }
    
    public void javadocMissingTagDescriptionAfterReference(final int sourceStart, final int sourceEnd, final int modifiers) {
        final int severity = this.computeSeverity(-1610612273);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            this.handle(-1610612273, ProblemReporter.NoArgument, ProblemReporter.NoArgument, severity, sourceStart, sourceEnd);
        }
    }
    
    public void javadocMissingThrowsClassName(final int sourceStart, final int sourceEnd, final int modifiers) {
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            this.handle(-1610612258, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
        }
    }
    
    public void javadocMissingThrowsTag(final TypeReference typeRef, final int modifiers) {
        final int severity = this.computeSeverity(-1610612259);
        if (severity == 256) {
            return;
        }
        final boolean overriding = (modifiers & 0x30000000) != 0x0;
        final boolean report = this.options.getSeverity(2097152) != 256 && (!overriding || this.options.reportMissingJavadocTagsOverriding);
        if (report && this.javadocVisibility(this.options.reportMissingJavadocTagsVisibility, modifiers)) {
            final String[] arguments = { String.valueOf(typeRef.resolvedType.sourceName()) };
            this.handle(-1610612259, arguments, arguments, severity, typeRef.sourceStart, typeRef.sourceEnd);
        }
    }
    
    public void javadocUndeclaredParamTagName(final char[] token, final int sourceStart, final int sourceEnd, final int modifiers) {
        final int severity = this.computeSeverity(-1610612262);
        if (severity == 256) {
            return;
        }
        if (this.javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
            final String[] arguments = { String.valueOf(token) };
            this.handle(-1610612262, arguments, arguments, severity, sourceStart, sourceEnd);
        }
    }
    
    public void javadocUnexpectedTag(final int sourceStart, final int sourceEnd) {
        this.handle(-1610612266, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    public void javadocUnexpectedText(final int sourceStart, final int sourceEnd) {
        this.handle(-1610612218, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    public void javadocUnterminatedInlineTag(final int sourceStart, final int sourceEnd) {
        this.handle(-1610612224, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    private boolean javadocVisibility(final int visibility, final int modifiers) {
        if (modifiers < 0) {
            return true;
        }
        switch (modifiers & 0x7) {
            case 1: {
                return true;
            }
            case 4: {
                return visibility != 1;
            }
            case 0: {
                return visibility == 0 || visibility == 2;
            }
            case 2: {
                return visibility == 2;
            }
            default: {
                return true;
            }
        }
    }
    
    private String javadocVisibilityArgument(final int visibility, final int modifiers) {
        String argument = null;
        switch (modifiers & 0x7) {
            case 1: {
                argument = "public";
                break;
            }
            case 4: {
                if (visibility != 1) {
                    argument = "protected";
                    break;
                }
                break;
            }
            case 0: {
                if (visibility == 0 || visibility == 2) {
                    argument = "default";
                    break;
                }
                break;
            }
            case 2: {
                if (visibility == 2) {
                    argument = "private";
                    break;
                }
                break;
            }
        }
        return argument;
    }
    
    public void localVariableHiding(final LocalDeclaration local, final Binding hiddenVariable, final boolean isSpecialArgHidingField) {
        if (hiddenVariable instanceof LocalVariableBinding) {
            final int id = (local instanceof Argument) ? 536871006 : 536871002;
            final int severity = this.computeSeverity(id);
            if (severity == 256) {
                return;
            }
            final String[] arguments = { new String(local.name) };
            this.handle(id, arguments, arguments, severity, this.nodeSourceStart(hiddenVariable, local), this.nodeSourceEnd(hiddenVariable, local));
        }
        else if (hiddenVariable instanceof FieldBinding) {
            if (isSpecialArgHidingField && !this.options.reportSpecialParameterHidingField) {
                return;
            }
            final int id = (local instanceof Argument) ? 536871007 : 570425435;
            final int severity = this.computeSeverity(id);
            if (severity == 256) {
                return;
            }
            final FieldBinding field = (FieldBinding)hiddenVariable;
            this.handle(id, new String[] { new String(local.name), new String(field.declaringClass.readableName()) }, new String[] { new String(local.name), new String(field.declaringClass.shortReadableName()) }, severity, local.sourceStart, local.sourceEnd);
        }
    }
    
    public void localVariableNonNullComparedToNull(final LocalVariableBinding local, final ASTNode location) {
        final int severity = this.computeSeverity(536871370);
        if (severity == 256) {
            return;
        }
        String[] arguments;
        int problemId;
        if (local.isNonNull()) {
            final char[][] annotationName = this.options.nonNullAnnotationName;
            arguments = new String[] { new String(local.name), new String(annotationName[annotationName.length - 1]) };
            problemId = 536871844;
        }
        else {
            arguments = new String[] { new String(local.name) };
            problemId = 536871370;
        }
        this.handle(problemId, arguments, arguments, severity, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }
    
    public void localVariableNullComparedToNonNull(final LocalVariableBinding local, final ASTNode location) {
        final int severity = this.computeSeverity(536871366);
        if (severity == 256) {
            return;
        }
        final String[] arguments = { new String(local.name) };
        this.handle(536871366, arguments, arguments, severity, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }
    
    public boolean expressionNonNullComparison(Expression expr, final boolean checkForNull) {
        int problemId = 0;
        Binding binding = null;
        String[] arguments = null;
        int start = 0;
        int end = 0;
        final Expression location = expr;
        if (expr.resolvedType != null) {
            final long tagBits = expr.resolvedType.tagBits & 0x180000000000000L;
            if (tagBits == 72057594037927936L) {
                problemId = 536871873;
                arguments = new String[] { String.valueOf(expr.resolvedType.nullAnnotatedReadableName(this.options, true)) };
                start = this.nodeSourceStart(location);
                end = this.nodeSourceEnd(location);
                this.handle(problemId, arguments, arguments, start, end);
                return true;
            }
        }
        while (!(expr instanceof Assignment)) {
            if (!(expr instanceof CastExpression)) {
                if (expr instanceof MessageSend) {
                    problemId = (checkForNull ? 536871848 : 536871832);
                    final MethodBinding method = (MethodBinding)(binding = ((MessageSend)expr).binding);
                    arguments = new String[] { new String(method.shortReadableName()) };
                    start = location.sourceStart;
                    end = location.sourceEnd;
                }
                else if (expr instanceof Reference && !(expr instanceof ThisReference) && !(expr instanceof ArrayReference)) {
                    final FieldBinding field = ((Reference)expr).lastFieldBinding();
                    if (field == null) {
                        return false;
                    }
                    if (field.isNonNull()) {
                        problemId = (checkForNull ? 536871850 : 536871849);
                        final char[][] nonNullName = this.options.nonNullAnnotationName;
                        arguments = new String[] { new String(field.name), new String(nonNullName[nonNullName.length - 1]) };
                    }
                    else {
                        problemId = (checkForNull ? 536871854 : 536871853);
                        arguments = new String[] { String.valueOf(field.name) };
                    }
                    binding = field;
                    start = this.nodeSourceStart(binding, location);
                    end = this.nodeSourceEnd(binding, location);
                }
                else if (!(expr instanceof AllocationExpression) && !(expr instanceof ArrayAllocationExpression) && !(expr instanceof ArrayInitializer) && !(expr instanceof ClassLiteralAccess) && !(expr instanceof ThisReference)) {
                    if (expr instanceof Literal || expr instanceof ConditionalExpression) {
                        if (expr instanceof NullLiteral) {
                            this.needImplementation(location);
                            return false;
                        }
                        if (expr.resolvedType != null && expr.resolvedType.isBaseType()) {
                            return false;
                        }
                    }
                    else {
                        if (!(expr instanceof BinaryExpression)) {
                            this.needImplementation(expr);
                            return false;
                        }
                        if ((expr.bits & 0xF) != 0xB) {
                            return false;
                        }
                    }
                }
                if (problemId == 0) {
                    problemId = (checkForNull ? 536871582 : 536871583);
                    start = location.sourceStart;
                    end = location.sourceEnd;
                    arguments = ProblemReporter.NoArgument;
                }
                this.handle(problemId, arguments, arguments, start, end);
                return true;
            }
            expr = ((CastExpression)expr).expression;
        }
        return false;
    }
    
    public void nullAnnotationUnsupportedLocation(final Annotation annotation) {
        final String[] arguments = { String.valueOf(annotation.resolvedType.readableName()) };
        final String[] shortArguments = { String.valueOf(annotation.resolvedType.shortReadableName()) };
        int severity = 129;
        if (annotation.recipient instanceof ReferenceBinding && ((ReferenceBinding)annotation.recipient).isAnnotationType()) {
            severity = 0;
        }
        this.handle(536871874, arguments, shortArguments, severity, annotation.sourceStart, annotation.sourceEnd);
    }
    
    public void nullAnnotationUnsupportedLocation(final TypeReference type) {
        int sourceEnd = type.sourceEnd;
        if (type instanceof ParameterizedSingleTypeReference) {
            final ParameterizedSingleTypeReference typeReference = (ParameterizedSingleTypeReference)type;
            final TypeReference[] typeArguments = typeReference.typeArguments;
            if (typeArguments[typeArguments.length - 1].sourceEnd > typeReference.sourceEnd) {
                sourceEnd = this.retrieveClosingAngleBracketPosition(typeReference.sourceEnd);
            }
            else {
                sourceEnd = type.sourceEnd;
            }
        }
        else if (type instanceof ParameterizedQualifiedTypeReference) {
            final ParameterizedQualifiedTypeReference typeReference2 = (ParameterizedQualifiedTypeReference)type;
            sourceEnd = this.retrieveClosingAngleBracketPosition(typeReference2.sourceEnd);
        }
        else {
            sourceEnd = type.sourceEnd;
        }
        this.handle(536871875, ProblemReporter.NoArgument, ProblemReporter.NoArgument, type.sourceStart, sourceEnd);
    }
    
    public void localVariableNullInstanceof(final LocalVariableBinding local, final ASTNode location) {
        final int severity = this.computeSeverity(536871368);
        if (severity == 256) {
            return;
        }
        final String[] arguments = { new String(local.name) };
        this.handle(536871368, arguments, arguments, severity, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }
    
    public void localVariableNullReference(final LocalVariableBinding local, final ASTNode location) {
        if (location instanceof Expression && (((Expression)location).implicitConversion & 0x400) != 0x0) {
            this.nullUnboxing(location, local.type);
            return;
        }
        final int severity = this.computeSeverity(536871363);
        if (severity == 256) {
            return;
        }
        final String[] arguments = { new String(local.name) };
        this.handle(536871363, arguments, arguments, severity, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }
    
    public void fieldFreeTypeVariableReference(final FieldBinding variable, final long position) {
        final char[][] nullableName = this.options.nullableAnnotationName;
        final String[] arguments = { new String(variable.type.readableName()), new String(nullableName[nullableName.length - 1]) };
        this.handle(976, arguments, arguments, (int)(position >>> 32), (int)position);
    }
    
    public void localVariableFreeTypeVariableReference(final LocalVariableBinding local, final ASTNode location) {
        final int severity = this.computeSeverity(976);
        if (severity == 256) {
            return;
        }
        final char[][] nullableName = this.options.nullableAnnotationName;
        final String[] arguments = { new String(local.type.readableName()), new String(nullableName[nullableName.length - 1]) };
        this.handle(976, arguments, arguments, severity, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }
    
    public void methodReturnTypeFreeTypeVariableReference(final MethodBinding method, final ASTNode location) {
        final int severity = this.computeSeverity(976);
        if (severity == 256) {
            return;
        }
        final char[][] nullableName = this.options.nullableAnnotationName;
        final String[] arguments = { new String(method.returnType.readableName()), new String(nullableName[nullableName.length - 1]) };
        this.handle(976, arguments, arguments, location.sourceStart, location.sourceEnd);
    }
    
    public void localVariablePotentialNullReference(final LocalVariableBinding local, final ASTNode location) {
        if (local.type.isFreeTypeVariable()) {
            this.localVariableFreeTypeVariableReference(local, location);
            return;
        }
        if (location instanceof Expression && (((Expression)location).implicitConversion & 0x400) != 0x0) {
            this.potentialNullUnboxing(location, local.type);
            return;
        }
        if ((local.type.tagBits & 0x80000000000000L) != 0x0L && location instanceof Expression) {
            this.dereferencingNullableExpression((Expression)location);
            return;
        }
        final int severity = this.computeSeverity(536871364);
        if (severity == 256) {
            return;
        }
        final String[] arguments = { new String(local.name) };
        this.handle(536871364, arguments, arguments, severity, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }
    
    public void potentialNullUnboxing(final ASTNode expression, final TypeBinding boxType) {
        final String[] arguments = { String.valueOf(boxType.readableName()) };
        final String[] argumentsShort = { String.valueOf(boxType.shortReadableName()) };
        this.handle(536871371, arguments, argumentsShort, expression.sourceStart, expression.sourceEnd);
    }
    
    public void nullUnboxing(final ASTNode expression, final TypeBinding boxType) {
        final String[] arguments = { String.valueOf(boxType.readableName()) };
        final String[] argumentsShort = { String.valueOf(boxType.shortReadableName()) };
        this.handle(536871373, arguments, argumentsShort, expression.sourceStart, expression.sourceEnd);
    }
    
    public void nullableFieldDereference(final FieldBinding variable, final long position) {
        final char[][] nullableName = this.options.nullableAnnotationName;
        final String[] arguments = { new String(variable.name), new String(nullableName[nullableName.length - 1]) };
        this.handle(33555356, arguments, arguments, (int)(position >>> 32), (int)position);
    }
    
    public void localVariableRedundantCheckOnNonNull(final LocalVariableBinding local, final ASTNode location) {
        final int severity = this.computeSeverity(536871369);
        if (severity == 256) {
            return;
        }
        String[] arguments;
        int problemId;
        if (local.isNonNull()) {
            final char[][] annotationName = this.options.nonNullAnnotationName;
            arguments = new String[] { new String(local.name), new String(annotationName[annotationName.length - 1]) };
            problemId = 536871843;
        }
        else {
            arguments = new String[] { new String(local.name) };
            problemId = 536871369;
        }
        this.handle(problemId, arguments, arguments, severity, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }
    
    public void localVariableRedundantCheckOnNull(final LocalVariableBinding local, final ASTNode location) {
        final int severity = this.computeSeverity(536871365);
        if (severity == 256) {
            return;
        }
        final String[] arguments = { new String(local.name) };
        this.handle(536871365, arguments, arguments, severity, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }
    
    public void localVariableRedundantNullAssignment(final LocalVariableBinding local, final ASTNode location) {
        if ((location.bits & 0x8) != 0x0) {
            return;
        }
        final int severity = this.computeSeverity(536871367);
        if (severity == 256) {
            return;
        }
        final String[] arguments = { new String(local.name) };
        this.handle(536871367, arguments, arguments, severity, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }
    
    public void methodMustOverride(final AbstractMethodDeclaration method, final long complianceLevel) {
        final MethodBinding binding = method.binding;
        this.handle((complianceLevel == 3211264L) ? 67109487 : 67109498, new String[] { new String(binding.selector), this.typesAsString(binding, false), new String(binding.declaringClass.readableName()) }, new String[] { new String(binding.selector), this.typesAsString(binding, true), new String(binding.declaringClass.shortReadableName()) }, method.sourceStart, method.sourceEnd);
    }
    
    public void methodNameClash(final MethodBinding currentMethod, final MethodBinding inheritedMethod, final int severity) {
        this.handle(67109424, new String[] { new String(currentMethod.selector), this.typesAsString(currentMethod, false), new String(currentMethod.declaringClass.readableName()), this.typesAsString(inheritedMethod, false), new String(inheritedMethod.declaringClass.readableName()) }, new String[] { new String(currentMethod.selector), this.typesAsString(currentMethod, true), new String(currentMethod.declaringClass.shortReadableName()), this.typesAsString(inheritedMethod, true), new String(inheritedMethod.declaringClass.shortReadableName()) }, severity, currentMethod.sourceStart(), currentMethod.sourceEnd());
    }
    
    public void methodNameClashHidden(final MethodBinding currentMethod, final MethodBinding inheritedMethod) {
        this.handle(67109448, new String[] { new String(currentMethod.selector), this.typesAsString(currentMethod, currentMethod.parameters, false), new String(currentMethod.declaringClass.readableName()), this.typesAsString(inheritedMethod, inheritedMethod.parameters, false), new String(inheritedMethod.declaringClass.readableName()) }, new String[] { new String(currentMethod.selector), this.typesAsString(currentMethod, currentMethod.parameters, true), new String(currentMethod.declaringClass.shortReadableName()), this.typesAsString(inheritedMethod, inheritedMethod.parameters, true), new String(inheritedMethod.declaringClass.shortReadableName()) }, currentMethod.sourceStart(), currentMethod.sourceEnd());
    }
    
    public void methodNeedBody(final AbstractMethodDeclaration methodDecl) {
        this.handle(603979883, ProblemReporter.NoArgument, ProblemReporter.NoArgument, methodDecl.sourceStart, methodDecl.sourceEnd);
    }
    
    public void methodNeedingNoBody(final MethodDeclaration methodDecl) {
        this.handle(((methodDecl.modifiers & 0x100) != 0x0) ? 603979888 : 603979889, ProblemReporter.NoArgument, ProblemReporter.NoArgument, methodDecl.sourceStart, methodDecl.sourceEnd);
    }
    
    public void methodWithConstructorName(final MethodDeclaration methodDecl) {
        this.handle(67108974, ProblemReporter.NoArgument, ProblemReporter.NoArgument, methodDecl.sourceStart, methodDecl.sourceEnd);
    }
    
    public void methodCanBeDeclaredStatic(final MethodDeclaration methodDecl) {
        final int severity = this.computeSeverity(603979897);
        if (severity == 256) {
            return;
        }
        final MethodBinding method = methodDecl.binding;
        this.handle(603979897, new String[] { new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false) }, new String[] { new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true) }, severity, methodDecl.sourceStart, methodDecl.sourceEnd);
    }
    
    public void methodCanBePotentiallyDeclaredStatic(final MethodDeclaration methodDecl) {
        final int severity = this.computeSeverity(603979898);
        if (severity == 256) {
            return;
        }
        final MethodBinding method = methodDecl.binding;
        this.handle(603979898, new String[] { new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false) }, new String[] { new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true) }, severity, methodDecl.sourceStart, methodDecl.sourceEnd);
    }
    
    public void missingDeprecatedAnnotationForField(final FieldDeclaration field) {
        final int severity = this.computeSeverity(536871540);
        if (severity == 256) {
            return;
        }
        final FieldBinding binding = field.binding;
        this.handle(536871540, new String[] { new String(binding.declaringClass.readableName()), new String(binding.name) }, new String[] { new String(binding.declaringClass.shortReadableName()), new String(binding.name) }, severity, this.nodeSourceStart(binding, field), this.nodeSourceEnd(binding, field));
    }
    
    public void missingDeprecatedAnnotationForMethod(final AbstractMethodDeclaration method) {
        final int severity = this.computeSeverity(536871541);
        if (severity == 256) {
            return;
        }
        final MethodBinding binding = method.binding;
        this.handle(536871541, new String[] { new String(binding.selector), this.typesAsString(binding, false), new String(binding.declaringClass.readableName()) }, new String[] { new String(binding.selector), this.typesAsString(binding, true), new String(binding.declaringClass.shortReadableName()) }, severity, method.sourceStart, method.sourceEnd);
    }
    
    public void missingDeprecatedAnnotationForType(final TypeDeclaration type) {
        final int severity = this.computeSeverity(536871542);
        if (severity == 256) {
            return;
        }
        final TypeBinding binding = type.binding;
        this.handle(536871542, new String[] { new String(binding.readableName()) }, new String[] { new String(binding.shortReadableName()) }, severity, type.sourceStart, type.sourceEnd);
    }
    
    public void notAFunctionalInterface(final TypeDeclaration type) {
        final TypeBinding binding = type.binding;
        this.handle(553648792, new String[] { new String(binding.readableName()) }, new String[] { new String(binding.shortReadableName()) }, type.sourceStart, type.sourceEnd);
    }
    
    public void missingEnumConstantCase(final SwitchStatement switchStatement, final FieldBinding enumConstant) {
        this.handle((switchStatement.defaultCase == null) ? 33555193 : 33555200, new String[] { new String(enumConstant.declaringClass.readableName()), new String(enumConstant.name) }, new String[] { new String(enumConstant.declaringClass.shortReadableName()), new String(enumConstant.name) }, switchStatement.expression.sourceStart, switchStatement.expression.sourceEnd);
    }
    
    public void missingDefaultCase(final SwitchStatement switchStatement, final boolean isEnumSwitch, final TypeBinding expressionType) {
        if (isEnumSwitch) {
            this.handle(536871678, new String[] { new String(expressionType.readableName()) }, new String[] { new String(expressionType.shortReadableName()) }, switchStatement.expression.sourceStart, switchStatement.expression.sourceEnd);
        }
        else {
            this.handle(536871679, ProblemReporter.NoArgument, ProblemReporter.NoArgument, switchStatement.expression.sourceStart, switchStatement.expression.sourceEnd);
        }
    }
    
    public void missingOverrideAnnotation(final AbstractMethodDeclaration method) {
        final int severity = this.computeSeverity(67109491);
        if (severity == 256) {
            return;
        }
        final MethodBinding binding = method.binding;
        this.handle(67109491, new String[] { new String(binding.selector), this.typesAsString(binding, false), new String(binding.declaringClass.readableName()) }, new String[] { new String(binding.selector), this.typesAsString(binding, true), new String(binding.declaringClass.shortReadableName()) }, severity, method.sourceStart, method.sourceEnd);
    }
    
    public void missingOverrideAnnotationForInterfaceMethodImplementation(final AbstractMethodDeclaration method) {
        final int severity = this.computeSeverity(67109500);
        if (severity == 256) {
            return;
        }
        final MethodBinding binding = method.binding;
        this.handle(67109500, new String[] { new String(binding.selector), this.typesAsString(binding, false), new String(binding.declaringClass.readableName()) }, new String[] { new String(binding.selector), this.typesAsString(binding, true), new String(binding.declaringClass.shortReadableName()) }, severity, method.sourceStart, method.sourceEnd);
    }
    
    public void missingReturnType(final AbstractMethodDeclaration methodDecl) {
        this.handle(16777327, ProblemReporter.NoArgument, ProblemReporter.NoArgument, methodDecl.sourceStart, methodDecl.sourceEnd);
    }
    
    public void missingSemiColon(final Expression expression) {
        this.handle(1610612960, ProblemReporter.NoArgument, ProblemReporter.NoArgument, expression.sourceStart, expression.sourceEnd);
    }
    
    public void missingSerialVersion(final TypeDeclaration typeDecl) {
        final String[] arguments = { new String(typeDecl.name) };
        this.handle(536871008, arguments, arguments, typeDecl.sourceStart, typeDecl.sourceEnd);
    }
    
    public void missingSynchronizedOnInheritedMethod(final MethodBinding currentMethod, final MethodBinding inheritedMethod) {
        this.handle(67109281, new String[] { new String(currentMethod.declaringClass.readableName()), new String(currentMethod.selector), this.typesAsString(currentMethod, false) }, new String[] { new String(currentMethod.declaringClass.shortReadableName()), new String(currentMethod.selector), this.typesAsString(currentMethod, true) }, currentMethod.sourceStart(), currentMethod.sourceEnd());
    }
    
    public void missingTypeInConstructor(final ASTNode location, final MethodBinding constructor) {
        final List missingTypes = constructor.collectMissingTypes(null);
        if (missingTypes == null) {
            System.err.println("The constructor " + constructor + " is wrongly tagged as containing missing types");
            return;
        }
        final TypeBinding missingType = missingTypes.get(0);
        int start = location.sourceStart;
        int end = location.sourceEnd;
        if (location instanceof QualifiedAllocationExpression) {
            final QualifiedAllocationExpression qualifiedAllocation = (QualifiedAllocationExpression)location;
            if (qualifiedAllocation.anonymousType != null) {
                start = qualifiedAllocation.anonymousType.sourceStart;
                end = qualifiedAllocation.anonymousType.sourceEnd;
            }
        }
        this.handle(134217857, new String[] { new String(constructor.declaringClass.readableName()), this.typesAsString(constructor, false), new String(missingType.readableName()) }, new String[] { new String(constructor.declaringClass.shortReadableName()), this.typesAsString(constructor, true), new String(missingType.shortReadableName()) }, start, end);
    }
    
    public void missingTypeInLambda(final LambdaExpression lambda, final MethodBinding method) {
        final int nameSourceStart = lambda.sourceStart();
        final int nameSourceEnd = lambda.diagnosticsSourceEnd();
        final List missingTypes = method.collectMissingTypes(null);
        if (missingTypes == null) {
            System.err.println("The lambda expression " + method + " is wrongly tagged as containing missing types");
            return;
        }
        final TypeBinding missingType = missingTypes.get(0);
        this.handle(67109135, new String[] { new String(missingType.readableName()) }, new String[] { new String(missingType.shortReadableName()) }, nameSourceStart, nameSourceEnd);
    }
    
    public void missingTypeInMethod(final ASTNode astNode, final MethodBinding method) {
        int nameSourceStart;
        int nameSourceEnd;
        if (astNode instanceof MessageSend) {
            final MessageSend messageSend = (astNode instanceof MessageSend) ? ((MessageSend)astNode) : null;
            nameSourceStart = (int)(messageSend.nameSourcePosition >>> 32);
            nameSourceEnd = (int)messageSend.nameSourcePosition;
        }
        else {
            nameSourceStart = astNode.sourceStart;
            nameSourceEnd = astNode.sourceEnd;
        }
        final List missingTypes = method.collectMissingTypes(null);
        if (missingTypes == null) {
            System.err.println("The method " + method + " is wrongly tagged as containing missing types");
            return;
        }
        final TypeBinding missingType = missingTypes.get(0);
        this.handle(67108984, new String[] { new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false), new String(missingType.readableName()) }, new String[] { new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true), new String(missingType.shortReadableName()) }, nameSourceStart, nameSourceEnd);
    }
    
    public void missingValueForAnnotationMember(final Annotation annotation, final char[] memberName) {
        final String memberString = new String(memberName);
        this.handle(16777825, new String[] { new String(annotation.resolvedType.readableName()), memberString }, new String[] { new String(annotation.resolvedType.shortReadableName()), memberString }, annotation.sourceStart, annotation.sourceEnd);
    }
    
    public void mustDefineDimensionsOrInitializer(final ArrayAllocationExpression expression) {
        this.handle(536871071, ProblemReporter.NoArgument, ProblemReporter.NoArgument, expression.sourceStart, expression.sourceEnd);
    }
    
    public void mustUseAStaticMethod(final MessageSend messageSend, final MethodBinding method) {
        this.handle(603979977, new String[] { new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false) }, new String[] { new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true) }, messageSend.sourceStart, messageSend.sourceEnd);
    }
    
    public void nativeMethodsCannotBeStrictfp(final ReferenceBinding type, final AbstractMethodDeclaration methodDecl) {
        final String[] arguments = { new String(type.sourceName()), new String(methodDecl.selector) };
        this.handle(67109231, arguments, arguments, methodDecl.sourceStart, methodDecl.sourceEnd);
    }
    
    public void needImplementation(final ASTNode location) {
        this.abortDueToInternalError(Messages.abort_missingCode, location);
    }
    
    public void needToEmulateFieldAccess(final FieldBinding field, final ASTNode location, final boolean isReadAccess) {
        final int id = isReadAccess ? 33554622 : 33554623;
        final int severity = this.computeSeverity(id);
        if (severity == 256) {
            return;
        }
        this.handle(id, new String[] { new String(field.declaringClass.readableName()), new String(field.name) }, new String[] { new String(field.declaringClass.shortReadableName()), new String(field.name) }, severity, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
    }
    
    public void needToEmulateMethodAccess(final MethodBinding method, final ASTNode location) {
        if (method.isConstructor()) {
            final int severity = this.computeSeverity(67109057);
            if (severity == 256) {
                return;
            }
            if (method.declaringClass.isEnum()) {
                return;
            }
            this.handle(67109057, new String[] { new String(method.declaringClass.readableName()), this.typesAsString(method, false) }, new String[] { new String(method.declaringClass.shortReadableName()), this.typesAsString(method, true) }, severity, location.sourceStart, location.sourceEnd);
        }
        else {
            final int severity = this.computeSeverity(67109056);
            if (severity == 256) {
                return;
            }
            this.handle(67109056, new String[] { new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false) }, new String[] { new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true) }, severity, location.sourceStart, location.sourceEnd);
        }
    }
    
    public void noAdditionalBoundAfterTypeVariable(final TypeReference boundReference) {
        this.handle(16777789, new String[] { new String(boundReference.resolvedType.readableName()) }, new String[] { new String(boundReference.resolvedType.shortReadableName()) }, boundReference.sourceStart, boundReference.sourceEnd);
    }
    
    private int nodeSourceEnd(final ASTNode node) {
        if (node instanceof Reference) {
            final Binding field = ((Reference)node).lastFieldBinding();
            if (field != null) {
                return this.nodeSourceEnd(field, node);
            }
        }
        return node.sourceEnd;
    }
    
    private int nodeSourceEnd(final Binding field, final ASTNode node) {
        return this.nodeSourceEnd(field, node, 0);
    }
    
    private int nodeSourceEnd(final Binding field, final ASTNode node, final int index) {
        if (node instanceof ArrayTypeReference) {
            return ((ArrayTypeReference)node).originalSourceEnd;
        }
        if (node instanceof QualifiedNameReference) {
            final QualifiedNameReference ref = (QualifiedNameReference)node;
            if (ref.binding == field) {
                if (index == 0) {
                    return (int)ref.sourcePositions[ref.indexOfFirstFieldBinding - 1];
                }
                final int length = ref.sourcePositions.length;
                if (index < length) {
                    return (int)ref.sourcePositions[index];
                }
                return (int)ref.sourcePositions[0];
            }
            else {
                final FieldBinding[] otherFields = ref.otherBindings;
                if (otherFields != null) {
                    final int offset = ref.indexOfFirstFieldBinding;
                    if (index != 0) {
                        for (int i = 0, length2 = otherFields.length; i < length2; ++i) {
                            if (otherFields[i] == field && i + offset == index) {
                                return (int)ref.sourcePositions[i + offset];
                            }
                        }
                    }
                    else {
                        for (int i = 0, length2 = otherFields.length; i < length2; ++i) {
                            if (otherFields[i] == field) {
                                return (int)ref.sourcePositions[i + offset];
                            }
                        }
                    }
                }
            }
        }
        else if (node instanceof ParameterizedQualifiedTypeReference) {
            final ParameterizedQualifiedTypeReference reference = (ParameterizedQualifiedTypeReference)node;
            if (index < reference.sourcePositions.length) {
                return (int)reference.sourcePositions[index];
            }
        }
        else if (node instanceof ArrayQualifiedTypeReference) {
            final ArrayQualifiedTypeReference reference2 = (ArrayQualifiedTypeReference)node;
            final int length = reference2.sourcePositions.length;
            if (index < length) {
                return (int)reference2.sourcePositions[index];
            }
            return (int)reference2.sourcePositions[length - 1];
        }
        else if (node instanceof QualifiedTypeReference) {
            final QualifiedTypeReference reference3 = (QualifiedTypeReference)node;
            final int length = reference3.sourcePositions.length;
            if (index < length) {
                return (int)reference3.sourcePositions[index];
            }
        }
        return node.sourceEnd;
    }
    
    private int nodeSourceStart(final ASTNode node) {
        if (node instanceof Reference) {
            final Binding field = ((Reference)node).lastFieldBinding();
            if (field != null) {
                return this.nodeSourceStart(field, node);
            }
        }
        return node.sourceStart;
    }
    
    private int nodeSourceStart(final Binding field, final ASTNode node) {
        return this.nodeSourceStart(field, node, 0);
    }
    
    private int nodeSourceStart(final Binding field, final ASTNode node, final int index) {
        if (node instanceof FieldReference) {
            final FieldReference fieldReference = (FieldReference)node;
            return (int)(fieldReference.nameSourcePosition >> 32);
        }
        if (node instanceof QualifiedNameReference) {
            final QualifiedNameReference ref = (QualifiedNameReference)node;
            if (ref.binding == field) {
                if (index == 0) {
                    return (int)(ref.sourcePositions[ref.indexOfFirstFieldBinding - 1] >> 32);
                }
                return (int)(ref.sourcePositions[index] >> 32);
            }
            else {
                final FieldBinding[] otherFields = ref.otherBindings;
                if (otherFields != null) {
                    final int offset = ref.indexOfFirstFieldBinding;
                    if (index != 0) {
                        for (int i = 0, length = otherFields.length; i < length; ++i) {
                            if (otherFields[i] == field && i + offset == index) {
                                return (int)(ref.sourcePositions[i + offset] >> 32);
                            }
                        }
                    }
                    else {
                        for (int i = 0, length = otherFields.length; i < length; ++i) {
                            if (otherFields[i] == field) {
                                return (int)(ref.sourcePositions[i + offset] >> 32);
                            }
                        }
                    }
                }
            }
        }
        else if (node instanceof ParameterizedQualifiedTypeReference) {
            final ParameterizedQualifiedTypeReference reference = (ParameterizedQualifiedTypeReference)node;
            return (int)(reference.sourcePositions[0] >>> 32);
        }
        return node.sourceStart;
    }
    
    public void noMoreAvailableSpaceForArgument(final LocalVariableBinding local, final ASTNode location) {
        final String[] arguments = { new String(local.name) };
        this.handle((local instanceof SyntheticArgumentBinding) ? 536870979 : 536870977, arguments, arguments, 159, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }
    
    public void noMoreAvailableSpaceForConstant(final TypeDeclaration typeDeclaration) {
        this.handle(536871343, new String[] { new String(typeDeclaration.binding.readableName()) }, new String[] { new String(typeDeclaration.binding.shortReadableName()) }, 159, typeDeclaration.sourceStart, typeDeclaration.sourceEnd);
    }
    
    public void noMoreAvailableSpaceForLocal(final LocalVariableBinding local, final ASTNode location) {
        final String[] arguments = { new String(local.name) };
        this.handle(536870978, arguments, arguments, 159, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }
    
    public void noMoreAvailableSpaceInConstantPool(final TypeDeclaration typeDeclaration) {
        this.handle(536871342, new String[] { new String(typeDeclaration.binding.readableName()) }, new String[] { new String(typeDeclaration.binding.shortReadableName()) }, 159, typeDeclaration.sourceStart, typeDeclaration.sourceEnd);
    }
    
    public void nonExternalizedStringLiteral(final ASTNode location) {
        this.handle(536871173, ProblemReporter.NoArgument, ProblemReporter.NoArgument, location.sourceStart, location.sourceEnd);
    }
    
    public void nonGenericTypeCannotBeParameterized(final int index, final ASTNode location, final TypeBinding type, final TypeBinding[] argumentTypes) {
        if (location == null) {
            this.handle(16777740, new String[] { new String(type.readableName()), this.typesAsString(argumentTypes, false) }, new String[] { new String(type.shortReadableName()), this.typesAsString(argumentTypes, true) }, 131, 0, 0);
            return;
        }
        this.handle(16777740, new String[] { new String(type.readableName()), this.typesAsString(argumentTypes, false) }, new String[] { new String(type.shortReadableName()), this.typesAsString(argumentTypes, true) }, this.nodeSourceStart(null, location), this.nodeSourceEnd(null, location, index));
    }
    
    public void nonStaticAccessToStaticField(final ASTNode location, final FieldBinding field) {
        this.nonStaticAccessToStaticField(location, field, -1);
    }
    
    public void nonStaticAccessToStaticField(final ASTNode location, final FieldBinding field, final int index) {
        final int severity = this.computeSeverity(570425420);
        if (severity == 256) {
            return;
        }
        this.handle(570425420, new String[] { new String(field.declaringClass.readableName()), new String(field.name) }, new String[] { new String(field.declaringClass.shortReadableName()), new String(field.name) }, severity, this.nodeSourceStart(field, location, index), this.nodeSourceEnd(field, location, index));
    }
    
    public void nonStaticAccessToStaticMethod(final ASTNode location, final MethodBinding method) {
        this.handle(603979893, new String[] { new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false) }, new String[] { new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true) }, location.sourceStart, location.sourceEnd);
    }
    
    public void nonStaticContextForEnumMemberType(final SourceTypeBinding type) {
        final String[] arguments = { new String(type.sourceName()) };
        this.handle(536870944, arguments, arguments, type.sourceStart(), type.sourceEnd());
    }
    
    public void noSuchEnclosingInstance(final TypeBinding targetType, final ASTNode location, final boolean isConstructorCall) {
        int id;
        if (isConstructorCall) {
            id = 536870940;
        }
        else if (location instanceof ExplicitConstructorCall && ((ExplicitConstructorCall)location).accessMode == 1) {
            id = 16777236;
        }
        else if (location instanceof AllocationExpression && (((AllocationExpression)location).binding.declaringClass.isMemberType() || (((AllocationExpression)location).binding.declaringClass.isAnonymousType() && ((AllocationExpression)location).binding.declaringClass.superclass().isMemberType()))) {
            id = 16777237;
        }
        else {
            id = 16777238;
        }
        this.handle(id, new String[] { new String(targetType.readableName()) }, new String[] { new String(targetType.shortReadableName()) }, location.sourceStart, (location instanceof LambdaExpression) ? ((LambdaExpression)location).diagnosticsSourceEnd() : location.sourceEnd);
    }
    
    public void notCompatibleTypesError(final EqualExpression expression, final TypeBinding leftType, final TypeBinding rightType) {
        final String leftName = new String(leftType.readableName());
        final String rightName = new String(rightType.readableName());
        String leftShortName = new String(leftType.shortReadableName());
        String rightShortName = new String(rightType.shortReadableName());
        if (leftShortName.equals(rightShortName)) {
            leftShortName = leftName;
            rightShortName = rightName;
        }
        this.handle(16777231, new String[] { leftName, rightName }, new String[] { leftShortName, rightShortName }, expression.sourceStart, expression.sourceEnd);
    }
    
    public void notCompatibleTypesError(final InstanceOfExpression expression, final TypeBinding leftType, final TypeBinding rightType) {
        final String leftName = new String(leftType.readableName());
        final String rightName = new String(rightType.readableName());
        String leftShortName = new String(leftType.shortReadableName());
        String rightShortName = new String(rightType.shortReadableName());
        if (leftShortName.equals(rightShortName)) {
            leftShortName = leftName;
            rightShortName = rightName;
        }
        this.handle(16777232, new String[] { leftName, rightName }, new String[] { leftShortName, rightShortName }, expression.sourceStart, expression.sourceEnd);
    }
    
    public void notCompatibleTypesErrorInForeach(final Expression expression, final TypeBinding leftType, final TypeBinding rightType) {
        final String leftName = new String(leftType.readableName());
        final String rightName = new String(rightType.readableName());
        String leftShortName = new String(leftType.shortReadableName());
        String rightShortName = new String(rightType.shortReadableName());
        if (leftShortName.equals(rightShortName)) {
            leftShortName = leftName;
            rightShortName = rightName;
        }
        this.handle(16777796, new String[] { leftName, rightName }, new String[] { leftShortName, rightShortName }, expression.sourceStart, expression.sourceEnd);
    }
    
    public void objectCannotBeGeneric(final TypeDeclaration typeDecl) {
        this.handle(536871435, ProblemReporter.NoArgument, ProblemReporter.NoArgument, typeDecl.typeParameters[0].sourceStart, typeDecl.typeParameters[typeDecl.typeParameters.length - 1].sourceEnd);
    }
    
    public void objectCannotHaveSuperTypes(final SourceTypeBinding type) {
        this.handle(536871241, ProblemReporter.NoArgument, ProblemReporter.NoArgument, type.sourceStart(), type.sourceEnd());
    }
    
    public void objectMustBeClass(final SourceTypeBinding type) {
        this.handle(536871242, ProblemReporter.NoArgument, ProblemReporter.NoArgument, type.sourceStart(), type.sourceEnd());
    }
    
    public void operatorOnlyValidOnNumericType(final CompoundAssignment assignment, final TypeBinding leftType, final TypeBinding rightType) {
        final String leftName = new String(leftType.readableName());
        final String rightName = new String(rightType.readableName());
        String leftShortName = new String(leftType.shortReadableName());
        String rightShortName = new String(rightType.shortReadableName());
        if (leftShortName.equals(rightShortName)) {
            leftShortName = leftName;
            rightShortName = rightName;
        }
        this.handle(16777233, new String[] { leftName, rightName }, new String[] { leftShortName, rightShortName }, assignment.sourceStart, assignment.sourceEnd);
    }
    
    public void overridesDeprecatedMethod(final MethodBinding localMethod, final MethodBinding inheritedMethod) {
        this.handle(67109276, new String[] { new String(CharOperation.concat(localMethod.declaringClass.readableName(), localMethod.readableName(), '.')), new String(inheritedMethod.declaringClass.readableName()) }, new String[] { new String(CharOperation.concat(localMethod.declaringClass.shortReadableName(), localMethod.shortReadableName(), '.')), new String(inheritedMethod.declaringClass.shortReadableName()) }, localMethod.sourceStart(), localMethod.sourceEnd());
    }
    
    public void overridesMethodWithoutSuperInvocation(final MethodBinding localMethod) {
        this.handle(67109280, new String[] { new String(CharOperation.concat(localMethod.declaringClass.readableName(), localMethod.readableName(), '.')) }, new String[] { new String(CharOperation.concat(localMethod.declaringClass.shortReadableName(), localMethod.shortReadableName(), '.')) }, localMethod.sourceStart(), localMethod.sourceEnd());
    }
    
    public void overridesPackageDefaultMethod(final MethodBinding localMethod, final MethodBinding inheritedMethod) {
        this.handle(67109274, new String[] { new String(CharOperation.concat(localMethod.declaringClass.readableName(), localMethod.readableName(), '.')), new String(inheritedMethod.declaringClass.readableName()) }, new String[] { new String(CharOperation.concat(localMethod.declaringClass.shortReadableName(), localMethod.shortReadableName(), '.')), new String(inheritedMethod.declaringClass.shortReadableName()) }, localMethod.sourceStart(), localMethod.sourceEnd());
    }
    
    public void packageCollidesWithType(final CompilationUnitDeclaration compUnitDecl) {
        final String[] arguments = { CharOperation.toString(compUnitDecl.currentPackage.tokens) };
        this.handle(16777537, arguments, arguments, compUnitDecl.currentPackage.sourceStart, compUnitDecl.currentPackage.sourceEnd);
    }
    
    public void packageIsNotExpectedPackage(final CompilationUnitDeclaration compUnitDecl) {
        final boolean hasPackageDeclaration = compUnitDecl.currentPackage == null;
        final String[] arguments = { CharOperation.toString(compUnitDecl.compilationResult.compilationUnit.getPackageName()), hasPackageDeclaration ? "" : CharOperation.toString(compUnitDecl.currentPackage.tokens) };
        int end;
        if (compUnitDecl.sourceEnd <= 0) {
            end = -1;
        }
        else {
            end = (hasPackageDeclaration ? 0 : compUnitDecl.currentPackage.sourceEnd);
        }
        this.handle(536871240, arguments, arguments, hasPackageDeclaration ? 0 : compUnitDecl.currentPackage.sourceStart, end);
    }
    
    public void parameterAssignment(final LocalVariableBinding local, final ASTNode location) {
        final int severity = this.computeSeverity(536870971);
        if (severity == 256) {
            return;
        }
        final String[] arguments = { new String(local.readableName()) };
        this.handle(536870971, arguments, arguments, severity, this.nodeSourceStart(local, location), this.nodeSourceEnd(local, location));
    }
    
    private String parameterBoundAsString(final TypeVariableBinding typeVariable, final boolean makeShort) {
        final StringBuffer nameBuffer = new StringBuffer(10);
        if (TypeBinding.equalsEquals(typeVariable.firstBound, typeVariable.superclass)) {
            nameBuffer.append(makeShort ? typeVariable.superclass.shortReadableName() : typeVariable.superclass.readableName());
        }
        final int length;
        if ((length = typeVariable.superInterfaces.length) > 0) {
            for (int i = 0; i < length; ++i) {
                if (i > 0 || TypeBinding.equalsEquals(typeVariable.firstBound, typeVariable.superclass)) {
                    nameBuffer.append(" & ");
                }
                nameBuffer.append(makeShort ? typeVariable.superInterfaces[i].shortReadableName() : typeVariable.superInterfaces[i].readableName());
            }
        }
        return nameBuffer.toString();
    }
    
    public void parameterizedMemberTypeMissingArguments(final ASTNode location, final TypeBinding type, final int index) {
        if (location == null) {
            this.handle(16777778, new String[] { new String(type.readableName()) }, new String[] { new String(type.shortReadableName()) }, 131, 0, 0);
            return;
        }
        this.handle(16777778, new String[] { new String(type.readableName()) }, new String[] { new String(type.shortReadableName()) }, location.sourceStart, this.nodeSourceEnd(null, location, index));
    }
    
    public void parseError(final int startPosition, final int endPosition, final int currentToken, final char[] currentTokenSource, String errorTokenName, final String[] possibleTokens) {
        if (possibleTokens.length == 0) {
            if (this.isKeyword(currentToken)) {
                final String[] arguments = { new String(currentTokenSource) };
                this.handle(1610612946, arguments, arguments, startPosition, endPosition);
                return;
            }
            final String[] arguments = { errorTokenName };
            this.handle(1610612941, arguments, arguments, startPosition, endPosition);
        }
        else {
            final StringBuffer list = new StringBuffer(20);
            for (int i = 0, max = possibleTokens.length; i < max; ++i) {
                if (i > 0) {
                    list.append(", ");
                }
                list.append('\"');
                list.append(possibleTokens[i]);
                list.append('\"');
            }
            if (this.isKeyword(currentToken)) {
                final String[] arguments2 = { new String(currentTokenSource), list.toString() };
                this.handle(1610612945, arguments2, arguments2, startPosition, endPosition);
                return;
            }
            if (this.isLiteral(currentToken) || this.isIdentifier(currentToken)) {
                errorTokenName = new String(currentTokenSource);
            }
            final String[] arguments2 = { errorTokenName, list.toString() };
            this.handle(1610612940, arguments2, arguments2, startPosition, endPosition);
        }
    }
    
    public void parseErrorDeleteToken(final int start, final int end, final int currentKind, final char[] errorTokenSource, final String errorTokenName) {
        this.syntaxError(1610612968, start, end, currentKind, errorTokenSource, errorTokenName, null);
    }
    
    public void parseErrorDeleteTokens(final int start, final int end) {
        this.handle(1610612969, ProblemReporter.NoArgument, ProblemReporter.NoArgument, start, end);
    }
    
    public void parseErrorInsertAfterToken(final int start, final int end, final int currentKind, final char[] errorTokenSource, final String errorTokenName, final String expectedToken) {
        this.syntaxError(1610612967, start, end, currentKind, errorTokenSource, errorTokenName, expectedToken);
    }
    
    public void parseErrorInsertBeforeToken(final int start, final int end, final int currentKind, final char[] errorTokenSource, final String errorTokenName, final String expectedToken) {
        this.syntaxError(1610612966, start, end, currentKind, errorTokenSource, errorTokenName, expectedToken);
    }
    
    public void parseErrorInsertToComplete(final int start, final int end, final String inserted, final String completed) {
        final String[] arguments = { inserted, completed };
        this.handle(1610612976, arguments, arguments, start, end);
    }
    
    public void parseErrorInsertToCompletePhrase(final int start, final int end, final String inserted) {
        final String[] arguments = { inserted };
        this.handle(1610612978, arguments, arguments, start, end);
    }
    
    public void parseErrorInsertToCompleteScope(final int start, final int end, final String inserted) {
        final String[] arguments = { inserted };
        this.handle(1610612977, arguments, arguments, start, end);
    }
    
    public void parseErrorInvalidToken(final int start, final int end, final int currentKind, final char[] errorTokenSource, final String errorTokenName, final String expectedToken) {
        this.syntaxError(1610612971, start, end, currentKind, errorTokenSource, errorTokenName, expectedToken);
    }
    
    public void parseErrorMergeTokens(final int start, final int end, final String expectedToken) {
        final String[] arguments = { expectedToken };
        this.handle(1610612970, arguments, arguments, start, end);
    }
    
    public void parseErrorMisplacedConstruct(final int start, final int end) {
        this.handle(1610612972, ProblemReporter.NoArgument, ProblemReporter.NoArgument, start, end);
    }
    
    public void parseErrorNoSuggestion(final int start, final int end, final int currentKind, final char[] errorTokenSource, final String errorTokenName) {
        this.syntaxError(1610612941, start, end, currentKind, errorTokenSource, errorTokenName, null);
    }
    
    public void parseErrorNoSuggestionForTokens(final int start, final int end) {
        this.handle(1610612974, ProblemReporter.NoArgument, ProblemReporter.NoArgument, start, end);
    }
    
    public void parseErrorReplaceToken(final int start, final int end, final int currentKind, final char[] errorTokenSource, final String errorTokenName, final String expectedToken) {
        this.syntaxError(1610612940, start, end, currentKind, errorTokenSource, errorTokenName, expectedToken);
    }
    
    public void parseErrorReplaceTokens(final int start, final int end, final String expectedToken) {
        final String[] arguments = { expectedToken };
        this.handle(1610612973, arguments, arguments, start, end);
    }
    
    public void parseErrorUnexpectedEnd(final int start, final int end) {
        String[] arguments;
        if (this.referenceContext instanceof ConstructorDeclaration) {
            arguments = new String[] { Messages.parser_endOfConstructor };
        }
        else if (this.referenceContext instanceof MethodDeclaration) {
            arguments = new String[] { Messages.parser_endOfMethod };
        }
        else if (this.referenceContext instanceof TypeDeclaration) {
            arguments = new String[] { Messages.parser_endOfInitializer };
        }
        else {
            arguments = new String[] { Messages.parser_endOfFile };
        }
        this.handle(1610612975, arguments, arguments, start, end);
    }
    
    public void possibleAccidentalBooleanAssignment(final Assignment assignment) {
        this.handle(536871091, ProblemReporter.NoArgument, ProblemReporter.NoArgument, assignment.sourceStart, assignment.sourceEnd);
    }
    
    public void possibleFallThroughCase(final CaseStatement caseStatement) {
        this.handle(536871106, ProblemReporter.NoArgument, ProblemReporter.NoArgument, caseStatement.sourceStart, caseStatement.sourceEnd);
    }
    
    public void publicClassMustMatchFileName(final CompilationUnitDeclaration compUnitDecl, final TypeDeclaration typeDecl) {
        this.referenceContext = typeDecl;
        final String[] arguments = { new String(compUnitDecl.getFileName()), new String(typeDecl.name) };
        this.handle(16777541, arguments, arguments, typeDecl.sourceStart, typeDecl.sourceEnd, compUnitDecl.compilationResult);
    }
    
    public void rawMemberTypeCannotBeParameterized(final ASTNode location, final ReferenceBinding type, final TypeBinding[] argumentTypes) {
        if (location == null) {
            this.handle(16777777, new String[] { new String(type.readableName()), this.typesAsString(argumentTypes, false), new String(type.enclosingType().readableName()) }, new String[] { new String(type.shortReadableName()), this.typesAsString(argumentTypes, true), new String(type.enclosingType().shortReadableName()) }, 131, 0, 0);
            return;
        }
        this.handle(16777777, new String[] { new String(type.readableName()), this.typesAsString(argumentTypes, false), new String(type.enclosingType().readableName()) }, new String[] { new String(type.shortReadableName()), this.typesAsString(argumentTypes, true), new String(type.enclosingType().shortReadableName()) }, location.sourceStart, location.sourceEnd);
    }
    
    public void rawTypeReference(final ASTNode location, TypeBinding type) {
        if (this.options.sourceLevel < 3211264L) {
            return;
        }
        type = type.leafComponentType();
        this.handle(16777788, new String[] { new String(type.readableName()), new String(type.erasure().readableName()) }, new String[] { new String(type.shortReadableName()), new String(type.erasure().shortReadableName()) }, location.sourceStart, this.nodeSourceEnd(null, location, Integer.MAX_VALUE));
    }
    
    public void recursiveConstructorInvocation(final ExplicitConstructorCall constructorCall) {
        this.handle(134217865, new String[] { new String(constructorCall.binding.declaringClass.readableName()), this.typesAsString(constructorCall.binding, false) }, new String[] { new String(constructorCall.binding.declaringClass.shortReadableName()), this.typesAsString(constructorCall.binding, true) }, constructorCall.sourceStart, constructorCall.sourceEnd);
    }
    
    public void redefineArgument(final Argument arg) {
        final String[] arguments = { new String(arg.name) };
        this.handle(536870968, arguments, arguments, arg.sourceStart, arg.sourceEnd);
    }
    
    public void redefineLocal(final LocalDeclaration localDecl) {
        final String[] arguments = { new String(localDecl.name) };
        this.handle(536870967, arguments, arguments, localDecl.sourceStart, localDecl.sourceEnd);
    }
    
    public void redundantSuperInterface(final SourceTypeBinding type, final TypeReference reference, final ReferenceBinding superinterface, final ReferenceBinding declaringType) {
        final int severity = this.computeSeverity(16777547);
        if (severity != 256) {
            this.handle(16777547, new String[] { new String(superinterface.readableName()), new String(type.readableName()), new String(declaringType.readableName()) }, new String[] { new String(superinterface.shortReadableName()), new String(type.shortReadableName()), new String(declaringType.shortReadableName()) }, severity, reference.sourceStart, reference.sourceEnd);
        }
    }
    
    public void referenceMustBeArrayTypeAt(final TypeBinding arrayType, final ArrayReference arrayRef) {
        this.handle(536871062, new String[] { new String(arrayType.readableName()) }, new String[] { new String(arrayType.shortReadableName()) }, arrayRef.sourceStart, arrayRef.sourceEnd);
    }
    
    public void repeatedAnnotationWithContainer(final Annotation annotation, final Annotation container) {
        this.handle(16778115, new String[] { new String(annotation.resolvedType.readableName()), new String(container.resolvedType.readableName()) }, new String[] { new String(annotation.resolvedType.shortReadableName()), new String(container.resolvedType.shortReadableName()) }, annotation.sourceStart, annotation.sourceEnd);
    }
    
    public void containerAnnotationTypeMustHaveValue(final ASTNode markerNode, final ReferenceBinding containerAnnotationType) {
        this.handle(16778119, new String[] { new String(containerAnnotationType.readableName()) }, new String[] { new String(containerAnnotationType.shortReadableName()) }, markerNode.sourceStart, markerNode.sourceEnd);
    }
    
    public void containerAnnotationTypeHasWrongValueType(final ASTNode markerNode, final ReferenceBinding containerAnnotationType, final ReferenceBinding annotationType, final TypeBinding returnType) {
        this.handle(16778118, new String[] { new String(containerAnnotationType.readableName()), new String(annotationType.readableName()), new String(returnType.readableName()) }, new String[] { new String(containerAnnotationType.shortReadableName()), new String(annotationType.shortReadableName()), new String(returnType.shortReadableName()) }, markerNode.sourceStart, markerNode.sourceEnd);
    }
    
    public void containerAnnotationTypeHasNonDefaultMembers(final ASTNode markerNode, final ReferenceBinding containerAnnotationType, final char[] selector) {
        this.handle(16778120, new String[] { new String(containerAnnotationType.readableName()), new String(selector) }, new String[] { new String(containerAnnotationType.shortReadableName()), new String(selector) }, markerNode.sourceStart, markerNode.sourceEnd);
    }
    
    public void containerAnnotationTypeHasShorterRetention(final ASTNode markerNode, final ReferenceBinding annotationType, final String annotationRetention, final ReferenceBinding containerAnnotationType, final String containerRetention) {
        this.handle(16778121, new String[] { new String(annotationType.readableName()), annotationRetention, new String(containerAnnotationType.readableName()), containerRetention }, new String[] { new String(annotationType.shortReadableName()), annotationRetention, new String(containerAnnotationType.shortReadableName()), containerRetention }, markerNode.sourceStart, markerNode.sourceEnd);
    }
    
    public void repeatableAnnotationTypeTargetMismatch(final ASTNode markerNode, final ReferenceBinding annotationType, final ReferenceBinding containerAnnotationType, final String unmetTargets) {
        this.handle(16778122, new String[] { new String(annotationType.readableName()), new String(containerAnnotationType.readableName()), unmetTargets }, new String[] { new String(annotationType.shortReadableName()), new String(containerAnnotationType.shortReadableName()), unmetTargets }, markerNode.sourceStart, markerNode.sourceEnd);
    }
    
    public void repeatableAnnotationTypeIsDocumented(final ASTNode markerNode, final ReferenceBinding annotationType, final ReferenceBinding containerAnnotationType) {
        this.handle(16778123, new String[] { new String(annotationType.readableName()), new String(containerAnnotationType.readableName()) }, new String[] { new String(annotationType.shortReadableName()), new String(containerAnnotationType.shortReadableName()) }, markerNode.sourceStart, markerNode.sourceEnd);
    }
    
    public void repeatableAnnotationTypeIsInherited(final ASTNode markerNode, final ReferenceBinding annotationType, final ReferenceBinding containerAnnotationType) {
        this.handle(16778124, new String[] { new String(annotationType.readableName()), new String(containerAnnotationType.readableName()) }, new String[] { new String(annotationType.shortReadableName()), new String(containerAnnotationType.shortReadableName()) }, markerNode.sourceStart, markerNode.sourceEnd);
    }
    
    public void repeatableAnnotationWithRepeatingContainer(final Annotation annotation, final ReferenceBinding containerType) {
        this.handle(16778125, new String[] { new String(annotation.resolvedType.readableName()), new String(containerType.readableName()) }, new String[] { new String(annotation.resolvedType.shortReadableName()), new String(containerType.shortReadableName()) }, annotation.sourceStart, annotation.sourceEnd);
    }
    
    public void reset() {
        this.positionScanner = null;
    }
    
    public void resourceHasToImplementAutoCloseable(final TypeBinding binding, final TypeReference typeReference) {
        if (this.options.sourceLevel < 3342336L) {
            return;
        }
        this.handle(16778087, new String[] { new String(binding.readableName()) }, new String[] { new String(binding.shortReadableName()) }, typeReference.sourceStart, typeReference.sourceEnd);
    }
    
    private int retrieveClosingAngleBracketPosition(final int start) {
        if (this.referenceContext == null) {
            return start;
        }
        final CompilationResult compilationResult = this.referenceContext.compilationResult();
        if (compilationResult == null) {
            return start;
        }
        final ICompilationUnit compilationUnit = compilationResult.getCompilationUnit();
        if (compilationUnit == null) {
            return start;
        }
        final char[] contents = compilationUnit.getContents();
        if (contents.length == 0) {
            return start;
        }
        if (this.positionScanner == null) {
            this.positionScanner = new Scanner(false, false, false, this.options.sourceLevel, this.options.complianceLevel, null, null, false);
            this.positionScanner.returnOnlyGreater = true;
        }
        this.positionScanner.setSource(contents);
        this.positionScanner.resetTo(start, contents.length);
        int end = start;
        int count = 0;
        Label_0214: {
            try {
                int token;
                while ((token = this.positionScanner.getNextToken()) != 60) {
                    switch (token) {
                        case 15: {
                            if (--count == 0) {
                                end = this.positionScanner.currentPosition - 1;
                                break Label_0214;
                            }
                            continue;
                        }
                        case 49: {
                            break Label_0214;
                        }
                        default: {
                            continue;
                        }
                        case 11: {
                            ++count;
                            continue;
                        }
                    }
                }
            }
            catch (final InvalidInputException ex) {}
        }
        return end;
    }
    
    private int retrieveEndingPositionAfterOpeningParenthesis(final int sourceStart, final int sourceEnd, final int numberOfParen) {
        if (this.referenceContext == null) {
            return sourceEnd;
        }
        final CompilationResult compilationResult = this.referenceContext.compilationResult();
        if (compilationResult == null) {
            return sourceEnd;
        }
        final ICompilationUnit compilationUnit = compilationResult.getCompilationUnit();
        if (compilationUnit == null) {
            return sourceEnd;
        }
        final char[] contents = compilationUnit.getContents();
        if (contents.length == 0) {
            return sourceEnd;
        }
        if (this.positionScanner == null) {
            this.positionScanner = new Scanner(false, false, false, this.options.sourceLevel, this.options.complianceLevel, null, null, false);
        }
        this.positionScanner.setSource(contents);
        this.positionScanner.resetTo(sourceStart, sourceEnd);
        try {
            int previousSourceEnd = sourceEnd;
            int token;
            while ((token = this.positionScanner.getNextToken()) != 60) {
                switch (token) {
                    case 25: {
                        return previousSourceEnd;
                    }
                    default: {
                        previousSourceEnd = this.positionScanner.currentPosition - 1;
                        continue;
                    }
                }
            }
        }
        catch (final InvalidInputException ex) {}
        return sourceEnd;
    }
    
    private int retrieveStartingPositionAfterOpeningParenthesis(final int sourceStart, final int sourceEnd, final int numberOfParen) {
        if (this.referenceContext == null) {
            return sourceStart;
        }
        final CompilationResult compilationResult = this.referenceContext.compilationResult();
        if (compilationResult == null) {
            return sourceStart;
        }
        final ICompilationUnit compilationUnit = compilationResult.getCompilationUnit();
        if (compilationUnit == null) {
            return sourceStart;
        }
        final char[] contents = compilationUnit.getContents();
        if (contents.length == 0) {
            return sourceStart;
        }
        if (this.positionScanner == null) {
            this.positionScanner = new Scanner(false, false, false, this.options.sourceLevel, this.options.complianceLevel, null, null, false);
        }
        this.positionScanner.setSource(contents);
        this.positionScanner.resetTo(sourceStart, sourceEnd);
        int count = 0;
        try {
            int token;
            while ((token = this.positionScanner.getNextToken()) != 60) {
                switch (token) {
                    case 24: {
                        if (++count == numberOfParen) {
                            this.positionScanner.getNextToken();
                            return this.positionScanner.startPosition;
                        }
                        continue;
                    }
                    default: {
                        continue;
                    }
                }
            }
        }
        catch (final InvalidInputException ex) {}
        return sourceStart;
    }
    
    public void scannerError(final Parser parser, final String errorTokenName) {
        final Scanner scanner = parser.scanner;
        int flag = 1610612941;
        int startPos = scanner.startPosition;
        int endPos = scanner.currentPosition - 1;
        if (errorTokenName.equals("End_Of_Source")) {
            flag = 1610612986;
        }
        else if (errorTokenName.equals("Invalid_Hexa_Literal")) {
            flag = 1610612987;
        }
        else if (errorTokenName.equals("Illegal_Hexa_Literal")) {
            flag = 1610613006;
        }
        else if (errorTokenName.equals("Invalid_Octal_Literal")) {
            flag = 1610612988;
        }
        else if (errorTokenName.equals("Invalid_Character_Constant")) {
            flag = 1610612989;
        }
        else if (errorTokenName.equals("Invalid_Escape")) {
            flag = 1610612990;
        }
        else if (errorTokenName.equals("Invalid_Unicode_Escape")) {
            flag = 1610612992;
            final char[] source = scanner.source;
            int checkPos = scanner.currentPosition - 1;
            if (checkPos >= source.length) {
                checkPos = source.length - 1;
            }
            while (checkPos >= startPos && source[checkPos] != '\\') {
                --checkPos;
            }
            startPos = checkPos;
        }
        else if (errorTokenName.equals("Invalid_Low_Surrogate")) {
            flag = 1610612999;
        }
        else if (errorTokenName.equals("Invalid_High_Surrogate")) {
            flag = 1610613000;
            char[] source;
            int checkPos;
            for (source = scanner.source, checkPos = scanner.startPosition + 1; checkPos <= endPos && source[checkPos] != '\\'; ++checkPos) {}
            endPos = checkPos - 1;
        }
        else if (errorTokenName.equals("Invalid_Float_Literal")) {
            flag = 1610612993;
        }
        else if (errorTokenName.equals("Unterminated_String")) {
            flag = 1610612995;
        }
        else if (errorTokenName.equals("Unterminated_Comment")) {
            flag = 1610612996;
        }
        else if (errorTokenName.equals("Invalid_Char_In_String")) {
            flag = 1610612995;
        }
        else if (errorTokenName.equals("Invalid_Digit")) {
            flag = 1610612998;
        }
        else if (errorTokenName.equals("Invalid_Binary_Literal")) {
            flag = 1610613002;
        }
        else if (errorTokenName.equals("Binary_Literal_Not_Below_17")) {
            flag = 1610613003;
        }
        else if (errorTokenName.equals("Invalid_Underscore")) {
            flag = 1610613004;
        }
        else if (errorTokenName.equals("Underscores_In_Literals_Not_Below_17")) {
            flag = 1610613005;
        }
        final String[] arguments = (flag == 1610612941) ? new String[] { errorTokenName } : ProblemReporter.NoArgument;
        this.handle(flag, arguments, arguments, startPos, endPos, parser.compilationUnit.compilationResult);
    }
    
    public void shouldImplementHashcode(final SourceTypeBinding type) {
        this.handle(16777548, new String[] { new String(type.readableName()) }, new String[] { new String(type.shortReadableName()) }, type.sourceStart(), type.sourceEnd());
    }
    
    public void shouldReturn(final TypeBinding returnType, final ASTNode location) {
        int sourceStart = location.sourceStart;
        int sourceEnd = location.sourceEnd;
        if (location instanceof LambdaExpression) {
            final LambdaExpression exp = (LambdaExpression)location;
            sourceStart = exp.sourceStart;
            sourceEnd = exp.diagnosticsSourceEnd();
        }
        this.handle(this.methodHasMissingSwitchDefault() ? 67109635 : 603979884, new String[] { new String(returnType.readableName()) }, new String[] { new String(returnType.shortReadableName()) }, sourceStart, sourceEnd);
    }
    
    public void signalNoImplicitStringConversionForCharArrayExpression(final Expression expression) {
        this.handle(536871063, ProblemReporter.NoArgument, ProblemReporter.NoArgument, expression.sourceStart, expression.sourceEnd);
    }
    
    public void staticAndInstanceConflict(final MethodBinding currentMethod, final MethodBinding inheritedMethod) {
        if (currentMethod.isStatic()) {
            this.handle(67109271, new String[] { new String(inheritedMethod.declaringClass.readableName()) }, new String[] { new String(inheritedMethod.declaringClass.shortReadableName()) }, currentMethod.sourceStart(), currentMethod.sourceEnd());
        }
        else {
            this.handle(67109270, new String[] { new String(inheritedMethod.declaringClass.readableName()) }, new String[] { new String(inheritedMethod.declaringClass.shortReadableName()) }, currentMethod.sourceStart(), currentMethod.sourceEnd());
        }
    }
    
    public void staticFieldAccessToNonStaticVariable(final ASTNode location, final FieldBinding field) {
        final String[] arguments = { new String(field.readableName()) };
        this.handle(33554506, arguments, arguments, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
    }
    
    public void staticInheritedMethodConflicts(final SourceTypeBinding type, final MethodBinding concreteMethod, final MethodBinding[] abstractMethods) {
        this.handle(67109272, new String[] { new String(concreteMethod.readableName()), new String(abstractMethods[0].declaringClass.readableName()) }, new String[] { new String(concreteMethod.readableName()), new String(abstractMethods[0].declaringClass.shortReadableName()) }, type.sourceStart(), type.sourceEnd());
    }
    
    public void staticMemberOfParameterizedType(final ASTNode location, final ReferenceBinding type, final int index) {
        if (location == null) {
            this.handle(16777779, new String[] { new String(type.readableName()), new String(type.enclosingType().readableName()) }, new String[] { new String(type.shortReadableName()), new String(type.enclosingType().shortReadableName()) }, 131, 0, 0);
            return;
        }
        this.handle(16777779, new String[] { new String(type.readableName()), new String(type.enclosingType().readableName()) }, new String[] { new String(type.shortReadableName()), new String(type.enclosingType().shortReadableName()) }, location.sourceStart, this.nodeSourceEnd(null, location, index));
    }
    
    public void stringConstantIsExceedingUtf8Limit(final ASTNode location) {
        this.handle(536871064, ProblemReporter.NoArgument, ProblemReporter.NoArgument, location.sourceStart, location.sourceEnd);
    }
    
    public void superclassMustBeAClass(final SourceTypeBinding type, final TypeReference superclassRef, final ReferenceBinding superType) {
        this.handle(16777528, new String[] { new String(superType.readableName()), new String(type.sourceName()) }, new String[] { new String(superType.shortReadableName()), new String(type.sourceName()) }, superclassRef.sourceStart, superclassRef.sourceEnd);
    }
    
    public void superfluousSemicolon(final int sourceStart, final int sourceEnd) {
        this.handle(536871092, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    public void superinterfaceMustBeAnInterface(final SourceTypeBinding type, final TypeReference superInterfaceRef, final ReferenceBinding superType) {
        this.handle(16777531, new String[] { new String(superType.readableName()), new String(type.sourceName()) }, new String[] { new String(superType.shortReadableName()), new String(type.sourceName()) }, superInterfaceRef.sourceStart, superInterfaceRef.sourceEnd);
    }
    
    public void superinterfacesCollide(final TypeBinding type, final ASTNode decl, final TypeBinding superType, final TypeBinding inheritedSuperType) {
        this.handle(16777755, new String[] { new String(superType.readableName()), new String(inheritedSuperType.readableName()), new String(type.sourceName()) }, new String[] { new String(superType.shortReadableName()), new String(inheritedSuperType.shortReadableName()), new String(type.sourceName()) }, decl.sourceStart, decl.sourceEnd);
    }
    
    public void superTypeCannotUseWildcard(final SourceTypeBinding type, final TypeReference superclass, final TypeBinding superTypeBinding) {
        final String name = new String(type.sourceName());
        final String superTypeFullName = new String(superTypeBinding.readableName());
        String superTypeShortName = new String(superTypeBinding.shortReadableName());
        if (superTypeShortName.equals(name)) {
            superTypeShortName = superTypeFullName;
        }
        this.handle(16777772, new String[] { superTypeFullName, name }, new String[] { superTypeShortName, name }, superclass.sourceStart, superclass.sourceEnd);
    }
    
    private void syntaxError(final int id, final int startPosition, final int endPosition, final int currentKind, final char[] currentTokenSource, final String errorTokenName, String expectedToken) {
        if (currentKind == 37 && expectedToken != null && expectedToken.equals("@")) {
            return;
        }
        String eTokenName;
        if (this.isKeyword(currentKind) || this.isLiteral(currentKind) || this.isIdentifier(currentKind)) {
            eTokenName = new String(currentTokenSource);
        }
        else {
            eTokenName = errorTokenName;
        }
        String[] arguments;
        if (expectedToken != null) {
            expectedToken = this.replaceIfSynthetic(expectedToken);
            arguments = new String[] { eTokenName, expectedToken };
        }
        else {
            arguments = new String[] { eTokenName };
        }
        this.handle(id, arguments, arguments, startPosition, endPosition);
    }
    
    private String replaceIfSynthetic(final String token) {
        if (token.equals("BeginTypeArguments")) {
            return ".";
        }
        if (token.equals("BeginLambda")) {
            return "(";
        }
        return token;
    }
    
    public void task(final String tag, final String message, final String priority, final int start, final int end) {
        this.handle(536871362, new String[] { tag, message, priority }, new String[] { tag, message, priority }, start, end);
    }
    
    public void tooManyDimensions(final ASTNode expression) {
        this.handle(536870980, ProblemReporter.NoArgument, ProblemReporter.NoArgument, expression.sourceStart, expression.sourceEnd);
    }
    
    public void tooManyFields(final TypeDeclaration typeDeclaration) {
        this.handle(536871344, new String[] { new String(typeDeclaration.binding.readableName()) }, new String[] { new String(typeDeclaration.binding.shortReadableName()) }, 159, typeDeclaration.sourceStart, typeDeclaration.sourceEnd);
    }
    
    public void tooManyMethods(final TypeDeclaration typeDeclaration) {
        this.handle(536871345, new String[] { new String(typeDeclaration.binding.readableName()) }, new String[] { new String(typeDeclaration.binding.shortReadableName()) }, 159, typeDeclaration.sourceStart, typeDeclaration.sourceEnd);
    }
    
    public void tooManyParametersForSyntheticMethod(final AbstractMethodDeclaration method) {
        final MethodBinding binding = method.binding;
        String selector = null;
        if (binding.isConstructor()) {
            selector = new String(binding.declaringClass.sourceName());
        }
        else {
            selector = new String(method.selector);
        }
        this.handle(536871346, new String[] { selector, this.typesAsString(binding, false), new String(binding.declaringClass.readableName()) }, new String[] { selector, this.typesAsString(binding, true), new String(binding.declaringClass.shortReadableName()) }, 145, method.sourceStart, method.sourceEnd);
    }
    
    public void typeCastError(final CastExpression expression, final TypeBinding leftType, final TypeBinding rightType) {
        final String leftName = new String(leftType.readableName());
        final String rightName = new String(rightType.readableName());
        String leftShortName = new String(leftType.shortReadableName());
        String rightShortName = new String(rightType.shortReadableName());
        if (leftShortName.equals(rightShortName)) {
            leftShortName = leftName;
            rightShortName = rightName;
        }
        this.handle(16777372, new String[] { rightName, leftName }, new String[] { rightShortName, leftShortName }, expression.sourceStart, expression.sourceEnd);
    }
    
    public void typeCollidesWithEnclosingType(final TypeDeclaration typeDecl) {
        final String[] arguments = { new String(typeDecl.name) };
        this.handle(16777534, arguments, arguments, typeDecl.sourceStart, typeDecl.sourceEnd);
    }
    
    public void typeCollidesWithPackage(final CompilationUnitDeclaration compUnitDecl, final TypeDeclaration typeDecl) {
        this.referenceContext = typeDecl;
        final String[] arguments = { new String(compUnitDecl.getFileName()), new String(typeDecl.name) };
        this.handle(16777538, arguments, arguments, typeDecl.sourceStart, typeDecl.sourceEnd, compUnitDecl.compilationResult);
    }
    
    public void typeHiding(final TypeDeclaration typeDecl, final TypeBinding hiddenType) {
        final int severity = this.computeSeverity(16777249);
        if (severity == 256) {
            return;
        }
        this.handle(16777249, new String[] { new String(typeDecl.name), new String(hiddenType.shortReadableName()) }, new String[] { new String(typeDecl.name), new String(hiddenType.readableName()) }, severity, typeDecl.sourceStart, typeDecl.sourceEnd);
    }
    
    public void typeHiding(final TypeDeclaration typeDecl, final TypeVariableBinding hiddenTypeParameter) {
        final int severity = this.computeSeverity(16777792);
        if (severity == 256) {
            return;
        }
        if (hiddenTypeParameter.declaringElement instanceof TypeBinding) {
            final TypeBinding declaringType = (TypeBinding)hiddenTypeParameter.declaringElement;
            this.handle(16777792, new String[] { new String(typeDecl.name), new String(hiddenTypeParameter.readableName()), new String(declaringType.readableName()) }, new String[] { new String(typeDecl.name), new String(hiddenTypeParameter.shortReadableName()), new String(declaringType.shortReadableName()) }, severity, typeDecl.sourceStart, typeDecl.sourceEnd);
        }
        else {
            final MethodBinding declaringMethod = (MethodBinding)hiddenTypeParameter.declaringElement;
            this.handle(16777793, new String[] { new String(typeDecl.name), new String(hiddenTypeParameter.readableName()), new String(declaringMethod.selector), this.typesAsString(declaringMethod, false), new String(declaringMethod.declaringClass.readableName()) }, new String[] { new String(typeDecl.name), new String(hiddenTypeParameter.shortReadableName()), new String(declaringMethod.selector), this.typesAsString(declaringMethod, true), new String(declaringMethod.declaringClass.shortReadableName()) }, severity, typeDecl.sourceStart, typeDecl.sourceEnd);
        }
    }
    
    public void typeHiding(final TypeParameter typeParam, final Binding hidden) {
        final int severity = this.computeSeverity(16777787);
        if (severity == 256) {
            return;
        }
        final TypeBinding hiddenType = (TypeBinding)hidden;
        this.handle(16777787, new String[] { new String(typeParam.name), new String(hiddenType.readableName()) }, new String[] { new String(typeParam.name), new String(hiddenType.shortReadableName()) }, severity, typeParam.sourceStart, typeParam.sourceEnd);
    }
    
    public void notAnnotationType(final TypeBinding actualType, final ASTNode location) {
        this.handle(16777250, new String[] { new String(actualType.leafComponentType().readableName()) }, new String[] { new String(actualType.leafComponentType().shortReadableName()) }, location.sourceStart, location.sourceEnd);
    }
    
    public void typeMismatchError(TypeBinding actualType, TypeBinding expectedType, final ASTNode location, final ASTNode expectingLocation) {
        if (this.options.sourceLevel < 3211264L) {
            if (actualType instanceof TypeVariableBinding) {
                actualType = actualType.erasure();
            }
            if (expectedType instanceof TypeVariableBinding) {
                expectedType = expectedType.erasure();
            }
        }
        if (actualType != null && (actualType.tagBits & 0x80L) != 0x0L) {
            if (location instanceof Annotation) {
                return;
            }
            this.handle(16777218, new String[] { new String(actualType.leafComponentType().readableName()) }, new String[] { new String(actualType.leafComponentType().shortReadableName()) }, location.sourceStart, location.sourceEnd);
        }
        else {
            if (expectingLocation != null && (expectedType.tagBits & 0x80L) != 0x0L) {
                this.handle(16777218, new String[] { new String(expectedType.leafComponentType().readableName()) }, new String[] { new String(expectedType.leafComponentType().shortReadableName()) }, expectingLocation.sourceStart, expectingLocation.sourceEnd);
                return;
            }
            char[] actualShortReadableName = actualType.shortReadableName();
            char[] expectedShortReadableName = expectedType.shortReadableName();
            char[] actualReadableName = actualType.readableName();
            char[] expectedReadableName = expectedType.readableName();
            if (CharOperation.equals(actualShortReadableName, expectedShortReadableName)) {
                if (CharOperation.equals(actualReadableName, expectedReadableName)) {
                    actualReadableName = actualType.nullAnnotatedReadableName(this.options, false);
                    expectedReadableName = expectedType.nullAnnotatedReadableName(this.options, false);
                    actualShortReadableName = actualType.nullAnnotatedReadableName(this.options, true);
                    expectedShortReadableName = expectedType.nullAnnotatedReadableName(this.options, true);
                }
                else {
                    actualShortReadableName = actualReadableName;
                    expectedShortReadableName = expectedReadableName;
                }
            }
            this.handle((expectingLocation instanceof ReturnStatement) ? 16777235 : 16777233, new String[] { new String(actualReadableName), new String(expectedReadableName) }, new String[] { new String(actualShortReadableName), new String(expectedShortReadableName) }, location.sourceStart, location.sourceEnd);
        }
    }
    
    public void typeMismatchError(final TypeBinding typeArgument, final TypeVariableBinding typeParameter, final ReferenceBinding genericType, final ASTNode location) {
        if (location == null) {
            this.handle(16777742, new String[] { new String(typeArgument.readableName()), new String(genericType.readableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, false) }, new String[] { new String(typeArgument.shortReadableName()), new String(genericType.shortReadableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, true) }, 131, 0, 0);
            return;
        }
        this.handle(16777742, new String[] { new String(typeArgument.readableName()), new String(genericType.readableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, false) }, new String[] { new String(typeArgument.shortReadableName()), new String(genericType.shortReadableName()), new String(typeParameter.sourceName()), this.parameterBoundAsString(typeParameter, true) }, location.sourceStart, location.sourceEnd);
    }
    
    private String typesAsString(final MethodBinding methodBinding, final boolean makeShort) {
        return this.typesAsString(methodBinding, methodBinding.parameters, makeShort);
    }
    
    private String typesAsString(final MethodBinding methodBinding, final TypeBinding[] parameters, final boolean makeShort) {
        return this.typesAsString(methodBinding, parameters, makeShort, false);
    }
    
    private String typesAsString(final MethodBinding methodBinding, final boolean makeShort, final boolean showNullAnnotations) {
        return this.typesAsString(methodBinding, methodBinding.parameters, makeShort, showNullAnnotations);
    }
    
    private String typesAsString(final MethodBinding methodBinding, final TypeBinding[] parameters, final boolean makeShort, final boolean showNullAnnotations) {
        if (methodBinding.isPolymorphic()) {
            final TypeBinding[] types = methodBinding.original().parameters;
            final StringBuffer buffer = new StringBuffer(10);
            for (int i = 0, length = types.length; i < length; ++i) {
                if (i != 0) {
                    buffer.append(", ");
                }
                TypeBinding type = types[i];
                final boolean isVarargType = i == length - 1;
                if (isVarargType) {
                    type = ((ArrayBinding)type).elementsType();
                }
                if (showNullAnnotations) {
                    buffer.append(new String(type.nullAnnotatedReadableName(this.options, makeShort)));
                }
                else {
                    buffer.append(new String(makeShort ? type.shortReadableName() : type.readableName()));
                }
                if (isVarargType) {
                    buffer.append("...");
                }
            }
            return buffer.toString();
        }
        final StringBuffer buffer2 = new StringBuffer(10);
        for (int j = 0, length2 = parameters.length; j < length2; ++j) {
            if (j != 0) {
                buffer2.append(", ");
            }
            TypeBinding type2 = parameters[j];
            final boolean isVarargType2 = methodBinding.isVarargs() && j == length2 - 1;
            if (isVarargType2) {
                type2 = ((ArrayBinding)type2).elementsType();
            }
            if (showNullAnnotations) {
                buffer2.append(new String(type2.nullAnnotatedReadableName(this.options, makeShort)));
            }
            else {
                buffer2.append(new String(makeShort ? type2.shortReadableName() : type2.readableName()));
            }
            if (isVarargType2) {
                buffer2.append("...");
            }
        }
        return buffer2.toString();
    }
    
    private String typesAsString(final TypeBinding[] types, final boolean makeShort) {
        return this.typesAsString(types, makeShort, false);
    }
    
    private String typesAsString(final TypeBinding[] types, final boolean makeShort, final boolean showNullAnnotations) {
        final StringBuffer buffer = new StringBuffer(10);
        for (int i = 0, length = types.length; i < length; ++i) {
            if (i != 0) {
                buffer.append(", ");
            }
            final TypeBinding type = types[i];
            if (showNullAnnotations) {
                buffer.append(new String(type.nullAnnotatedReadableName(this.options, makeShort)));
            }
            else {
                buffer.append(new String(makeShort ? type.shortReadableName() : type.readableName()));
            }
        }
        return buffer.toString();
    }
    
    public void undefinedAnnotationValue(final TypeBinding annotationType, final MemberValuePair memberValuePair) {
        if (this.isRecoveredName(memberValuePair.name)) {
            return;
        }
        final String name = new String(memberValuePair.name);
        this.handle(67109475, new String[] { name, new String(annotationType.readableName()) }, new String[] { name, new String(annotationType.shortReadableName()) }, memberValuePair.sourceStart, memberValuePair.sourceEnd);
    }
    
    public void undefinedLabel(final BranchStatement statement) {
        if (this.isRecoveredName(statement.label)) {
            return;
        }
        final String[] arguments = { new String(statement.label) };
        this.handle(536871086, arguments, arguments, statement.sourceStart, statement.sourceEnd);
    }
    
    public void undefinedTypeVariableSignature(final char[] variableName, final ReferenceBinding binaryType) {
        this.handle(536871450, new String[] { new String(variableName), new String(binaryType.readableName()) }, new String[] { new String(variableName), new String(binaryType.shortReadableName()) }, 131, 0, 0);
    }
    
    public void undocumentedEmptyBlock(final int blockStart, final int blockEnd) {
        this.handle(536871372, ProblemReporter.NoArgument, ProblemReporter.NoArgument, blockStart, blockEnd);
    }
    
    public void unexpectedStaticModifierForField(final SourceTypeBinding type, final FieldDeclaration fieldDecl) {
        final String[] arguments = { new String(fieldDecl.name) };
        this.handle(33554778, arguments, arguments, fieldDecl.sourceStart, fieldDecl.sourceEnd);
    }
    
    public void unexpectedStaticModifierForMethod(final ReferenceBinding type, final AbstractMethodDeclaration methodDecl) {
        final String[] arguments = { new String(type.sourceName()), new String(methodDecl.selector) };
        this.handle(67109225, arguments, arguments, methodDecl.sourceStart, methodDecl.sourceEnd);
    }
    
    public void unhandledException(final TypeBinding exceptionType, final ASTNode location) {
        final boolean insideDefaultConstructor = this.referenceContext instanceof ConstructorDeclaration && ((ConstructorDeclaration)this.referenceContext).isDefaultConstructor();
        final boolean insideImplicitConstructorCall = location instanceof ExplicitConstructorCall && ((ExplicitConstructorCall)location).accessMode == 1;
        int sourceEnd = location.sourceEnd;
        if (location instanceof LocalDeclaration) {
            sourceEnd = ((LocalDeclaration)location).declarationEnd;
        }
        this.handle(insideDefaultConstructor ? 16777362 : (insideImplicitConstructorCall ? 134217871 : 16777384), new String[] { new String(exceptionType.readableName()) }, new String[] { new String(exceptionType.shortReadableName()) }, location.sourceStart, sourceEnd);
    }
    
    public void unhandledExceptionFromAutoClose(final TypeBinding exceptionType, final ASTNode location) {
        final LocalVariableBinding localBinding = ((LocalDeclaration)location).binding;
        if (localBinding != null) {
            this.handle(16778098, new String[] { new String(exceptionType.readableName()), new String(localBinding.readableName()) }, new String[] { new String(exceptionType.shortReadableName()), new String(localBinding.shortReadableName()) }, location.sourceStart, location.sourceEnd);
        }
    }
    
    public void unhandledWarningToken(final Expression token) {
        final String[] arguments = { token.constant.stringValue() };
        this.handle(536871543, arguments, arguments, token.sourceStart, token.sourceEnd);
    }
    
    public void uninitializedBlankFinalField(final FieldBinding field, final ASTNode location) {
        final String[] arguments = { new String(field.readableName()) };
        this.handle(this.methodHasMissingSwitchDefault() ? 33555202 : 33554513, arguments, arguments, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
    }
    
    public void uninitializedNonNullField(final FieldBinding field, final ASTNode location) {
        final char[][] nonNullAnnotationName = this.options.nonNullAnnotationName;
        if (!field.isNonNull()) {
            final String[] arguments = { new String(field.readableName()), new String(field.type.readableName()), new String(nonNullAnnotationName[nonNullAnnotationName.length - 1]) };
            this.handle(this.methodHasMissingSwitchDefault() ? 978 : 977, arguments, arguments, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
            return;
        }
        final String[] arguments = { new String(nonNullAnnotationName[nonNullAnnotationName.length - 1]), new String(field.readableName()) };
        this.handle(this.methodHasMissingSwitchDefault() ? 33555367 : 33555366, arguments, arguments, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
    }
    
    public void uninitializedLocalVariable(final LocalVariableBinding binding, final ASTNode location) {
        binding.tagBits |= 0x100L;
        final String[] arguments = { new String(binding.readableName()) };
        this.handle(this.methodHasMissingSwitchDefault() ? 536871681 : 536870963, arguments, arguments, this.nodeSourceStart(binding, location), this.nodeSourceEnd(binding, location));
    }
    
    private boolean methodHasMissingSwitchDefault() {
        MethodScope methodScope = null;
        if (this.referenceContext instanceof Block) {
            methodScope = ((Block)this.referenceContext).scope.methodScope();
        }
        else if (this.referenceContext instanceof AbstractMethodDeclaration) {
            methodScope = ((AbstractMethodDeclaration)this.referenceContext).scope;
        }
        return methodScope != null && methodScope.hasMissingSwitchDefault;
    }
    
    public void unmatchedBracket(final int position, final ReferenceContext context, final CompilationResult compilationResult) {
        this.handle(1610612956, ProblemReporter.NoArgument, ProblemReporter.NoArgument, position, position, context, compilationResult);
    }
    
    public void unnecessaryCast(final CastExpression castExpression) {
        if (castExpression.expression instanceof FunctionalExpression) {
            return;
        }
        final int severity = this.computeSeverity(553648309);
        if (severity == 256) {
            return;
        }
        final TypeBinding castedExpressionType = castExpression.expression.resolvedType;
        this.handle(553648309, new String[] { new String(castedExpressionType.readableName()), new String(castExpression.type.resolvedType.readableName()) }, new String[] { new String(castedExpressionType.shortReadableName()), new String(castExpression.type.resolvedType.shortReadableName()) }, severity, castExpression.sourceStart, castExpression.sourceEnd);
    }
    
    public void unnecessaryElse(final ASTNode location) {
        this.handle(536871101, ProblemReporter.NoArgument, ProblemReporter.NoArgument, location.sourceStart, location.sourceEnd);
    }
    
    public void unnecessaryEnclosingInstanceSpecification(final Expression expression, final ReferenceBinding targetType) {
        this.handle(16777239, new String[] { new String(targetType.readableName()) }, new String[] { new String(targetType.shortReadableName()) }, expression.sourceStart, expression.sourceEnd);
    }
    
    public void unnecessaryInstanceof(final InstanceOfExpression instanceofExpression, final TypeBinding checkType) {
        final int severity = this.computeSeverity(553648311);
        if (severity == 256) {
            return;
        }
        final TypeBinding expressionType = instanceofExpression.expression.resolvedType;
        this.handle(553648311, new String[] { new String(expressionType.readableName()), new String(checkType.readableName()) }, new String[] { new String(expressionType.shortReadableName()), new String(checkType.shortReadableName()) }, severity, instanceofExpression.sourceStart, instanceofExpression.sourceEnd);
    }
    
    public void unnecessaryNLSTags(final int sourceStart, final int sourceEnd) {
        this.handle(536871177, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    public void unnecessaryTypeArgumentsForMethodInvocation(final MethodBinding method, final TypeBinding[] genericTypeArguments, final TypeReference[] typeArguments) {
        final String methodName = method.isConstructor() ? new String(method.declaringClass.shortReadableName()) : new String(method.selector);
        this.handle(method.isConstructor() ? 67109524 : 67109443, new String[] { methodName, this.typesAsString(method, false), new String(method.declaringClass.readableName()), this.typesAsString(genericTypeArguments, false) }, new String[] { methodName, this.typesAsString(method, true), new String(method.declaringClass.shortReadableName()), this.typesAsString(genericTypeArguments, true) }, typeArguments[0].sourceStart, typeArguments[typeArguments.length - 1].sourceEnd);
    }
    
    public void unqualifiedFieldAccess(final NameReference reference, final FieldBinding field) {
        int sourceStart = reference.sourceStart;
        int sourceEnd = reference.sourceEnd;
        if (reference instanceof SingleNameReference) {
            final int numberOfParens = (reference.bits & 0x1FE00000) >> 21;
            if (numberOfParens != 0) {
                sourceStart = this.retrieveStartingPositionAfterOpeningParenthesis(sourceStart, sourceEnd, numberOfParens);
                sourceEnd = this.retrieveEndingPositionAfterOpeningParenthesis(sourceStart, sourceEnd, numberOfParens);
            }
            else {
                sourceStart = this.nodeSourceStart(field, reference);
                sourceEnd = this.nodeSourceEnd(field, reference);
            }
        }
        else {
            sourceStart = this.nodeSourceStart(field, reference);
            sourceEnd = this.nodeSourceEnd(field, reference);
        }
        this.handle(570425423, new String[] { new String(field.declaringClass.readableName()), new String(field.name) }, new String[] { new String(field.declaringClass.shortReadableName()), new String(field.name) }, sourceStart, sourceEnd);
    }
    
    public void unreachableCatchBlock(final ReferenceBinding exceptionType, final ASTNode location) {
        this.handle(83886247, new String[] { new String(exceptionType.readableName()) }, new String[] { new String(exceptionType.shortReadableName()) }, location.sourceStart, location.sourceEnd);
    }
    
    public void unreachableCode(final Statement statement) {
        int sourceStart = statement.sourceStart;
        int sourceEnd = statement.sourceEnd;
        if (statement instanceof LocalDeclaration) {
            final LocalDeclaration declaration = (LocalDeclaration)statement;
            sourceStart = declaration.declarationSourceStart;
            sourceEnd = declaration.declarationSourceEnd;
        }
        else if (statement instanceof Expression) {
            final int statemendEnd = ((Expression)statement).statementEnd;
            if (statemendEnd != -1) {
                sourceEnd = statemendEnd;
            }
        }
        this.handle(536871073, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    public void unresolvableReference(final NameReference nameRef, final Binding binding) {
        final String[] arguments = { new String(binding.readableName()) };
        int end = nameRef.sourceEnd;
        int sourceStart = nameRef.sourceStart;
        if (nameRef instanceof QualifiedNameReference) {
            final QualifiedNameReference ref = (QualifiedNameReference)nameRef;
            if (this.isRecoveredName(ref.tokens)) {
                return;
            }
            if (ref.indexOfFirstFieldBinding >= 1) {
                end = (int)ref.sourcePositions[ref.indexOfFirstFieldBinding - 1];
            }
        }
        else {
            final SingleNameReference ref2 = (SingleNameReference)nameRef;
            if (this.isRecoveredName(ref2.token)) {
                return;
            }
            final int numberOfParens = (ref2.bits & 0x1FE00000) >> 21;
            if (numberOfParens != 0) {
                sourceStart = this.retrieveStartingPositionAfterOpeningParenthesis(sourceStart, end, numberOfParens);
                end = this.retrieveEndingPositionAfterOpeningParenthesis(sourceStart, end, numberOfParens);
            }
        }
        final int problemId = ((nameRef.bits & 0x3) != 0x0 && (nameRef.bits & 0x4) == 0x0) ? 33554515 : 570425394;
        this.handle(problemId, arguments, arguments, sourceStart, end);
    }
    
    public void unsafeCast(final CastExpression castExpression, final Scope scope) {
        if (this.options.sourceLevel < 3211264L) {
            return;
        }
        final int severity = this.computeSeverity(16777761);
        if (severity == 256) {
            return;
        }
        final TypeBinding castedExpressionType = castExpression.expression.resolvedType;
        final TypeBinding castExpressionResolvedType = castExpression.resolvedType;
        this.handle(16777761, new String[] { new String(castedExpressionType.readableName()), new String(castExpressionResolvedType.readableName()) }, new String[] { new String(castedExpressionType.shortReadableName()), new String(castExpressionResolvedType.shortReadableName()) }, severity, castExpression.sourceStart, castExpression.sourceEnd);
    }
    
    public void unsafeNullnessCast(final CastExpression castExpression, final Scope scope) {
        final TypeBinding castedExpressionType = castExpression.expression.resolvedType;
        final TypeBinding castExpressionResolvedType = castExpression.resolvedType;
        this.handle(536871879, new String[] { new String(castedExpressionType.nullAnnotatedReadableName(this.options, false)), new String(castExpressionResolvedType.nullAnnotatedReadableName(this.options, false)) }, new String[] { new String(castedExpressionType.nullAnnotatedReadableName(this.options, true)), new String(castExpressionResolvedType.nullAnnotatedReadableName(this.options, true)) }, castExpression.sourceStart, castExpression.sourceEnd);
    }
    
    public void unsafeGenericArrayForVarargs(final TypeBinding leafComponentType, final ASTNode location) {
        final int severity = this.computeSeverity(67109438);
        if (severity == 256) {
            return;
        }
        this.handle(67109438, new String[] { new String(leafComponentType.readableName()) }, new String[] { new String(leafComponentType.shortReadableName()) }, severity, location.sourceStart, location.sourceEnd);
    }
    
    public void unsafeRawFieldAssignment(final FieldBinding field, final TypeBinding expressionType, final ASTNode location) {
        if (this.options.sourceLevel < 3211264L) {
            return;
        }
        final int severity = this.computeSeverity(16777752);
        if (severity == 256) {
            return;
        }
        this.handle(16777752, new String[] { new String(expressionType.readableName()), new String(field.name), new String(field.declaringClass.readableName()), new String(field.declaringClass.erasure().readableName()) }, new String[] { new String(expressionType.shortReadableName()), new String(field.name), new String(field.declaringClass.shortReadableName()), new String(field.declaringClass.erasure().shortReadableName()) }, severity, this.nodeSourceStart(field, location), this.nodeSourceEnd(field, location));
    }
    
    public void unsafeRawGenericMethodInvocation(final ASTNode location, final MethodBinding rawMethod, final TypeBinding[] argumentTypes) {
        if (this.options.sourceLevel < 3211264L) {
            return;
        }
        final boolean isConstructor = rawMethod.isConstructor();
        final int severity = this.computeSeverity(isConstructor ? 16777785 : 16777786);
        if (severity == 256) {
            return;
        }
        if (isConstructor) {
            this.handle(16777785, new String[] { new String(rawMethod.declaringClass.sourceName()), this.typesAsString(rawMethod.original(), false), new String(rawMethod.declaringClass.readableName()), this.typesAsString(argumentTypes, false) }, new String[] { new String(rawMethod.declaringClass.sourceName()), this.typesAsString(rawMethod.original(), true), new String(rawMethod.declaringClass.shortReadableName()), this.typesAsString(argumentTypes, true) }, severity, location.sourceStart, location.sourceEnd);
        }
        else {
            this.handle(16777786, new String[] { new String(rawMethod.selector), this.typesAsString(rawMethod.original(), false), new String(rawMethod.declaringClass.readableName()), this.typesAsString(argumentTypes, false) }, new String[] { new String(rawMethod.selector), this.typesAsString(rawMethod.original(), true), new String(rawMethod.declaringClass.shortReadableName()), this.typesAsString(argumentTypes, true) }, severity, location.sourceStart, location.sourceEnd);
        }
    }
    
    public void unsafeRawInvocation(final ASTNode location, final MethodBinding rawMethod) {
        if (this.options.sourceLevel < 3211264L) {
            return;
        }
        final boolean isConstructor = rawMethod.isConstructor();
        final int severity = this.computeSeverity(isConstructor ? 16777746 : 16777747);
        if (severity == 256) {
            return;
        }
        if (isConstructor) {
            this.handle(16777746, new String[] { new String(rawMethod.declaringClass.readableName()), this.typesAsString(rawMethod.original(), rawMethod.parameters, false), new String(rawMethod.declaringClass.erasure().readableName()) }, new String[] { new String(rawMethod.declaringClass.shortReadableName()), this.typesAsString(rawMethod.original(), rawMethod.parameters, true), new String(rawMethod.declaringClass.erasure().shortReadableName()) }, severity, location.sourceStart, location.sourceEnd);
        }
        else {
            this.handle(16777747, new String[] { new String(rawMethod.selector), this.typesAsString(rawMethod.original(), rawMethod.parameters, false), new String(rawMethod.declaringClass.readableName()), new String(rawMethod.declaringClass.erasure().readableName()) }, new String[] { new String(rawMethod.selector), this.typesAsString(rawMethod.original(), rawMethod.parameters, true), new String(rawMethod.declaringClass.shortReadableName()), new String(rawMethod.declaringClass.erasure().shortReadableName()) }, severity, location.sourceStart, location.sourceEnd);
        }
    }
    
    public void unsafeReturnTypeOverride(final MethodBinding currentMethod, final MethodBinding inheritedMethod, final SourceTypeBinding type) {
        if (this.options.sourceLevel < 3211264L) {
            return;
        }
        final int severity = this.computeSeverity(67109423);
        if (severity == 256) {
            return;
        }
        int start = type.sourceStart();
        int end = type.sourceEnd();
        if (TypeBinding.equalsEquals(currentMethod.declaringClass, type)) {
            final ASTNode location = ((MethodDeclaration)currentMethod.sourceMethod()).returnType;
            start = location.sourceStart();
            end = location.sourceEnd();
        }
        this.handle(67109423, new String[] { new String(currentMethod.returnType.readableName()), new String(currentMethod.selector), this.typesAsString(currentMethod.original(), false), new String(currentMethod.declaringClass.readableName()), new String(inheritedMethod.returnType.readableName()), new String(inheritedMethod.declaringClass.readableName()) }, new String[] { new String(currentMethod.returnType.shortReadableName()), new String(currentMethod.selector), this.typesAsString(currentMethod.original(), true), new String(currentMethod.declaringClass.shortReadableName()), new String(inheritedMethod.returnType.shortReadableName()), new String(inheritedMethod.declaringClass.shortReadableName()) }, severity, start, end);
    }
    
    public void unsafeTypeConversion(final Expression expression, final TypeBinding expressionType, final TypeBinding expectedType) {
        if (this.options.sourceLevel < 3211264L) {
            return;
        }
        final int severity = this.computeSeverity(16777748);
        if (severity == 256) {
            return;
        }
        if (!this.options.reportUnavoidableGenericTypeProblems && expression.forcedToBeRaw(this.referenceContext)) {
            return;
        }
        this.handle(16777748, new String[] { new String(expressionType.readableName()), new String(expectedType.readableName()), new String(expectedType.erasure().readableName()) }, new String[] { new String(expressionType.shortReadableName()), new String(expectedType.shortReadableName()), new String(expectedType.erasure().shortReadableName()) }, severity, expression.sourceStart, expression.sourceEnd);
    }
    
    public void unsafeElementTypeConversion(final Expression expression, final TypeBinding expressionType, final TypeBinding expectedType) {
        if (this.options.sourceLevel < 3211264L) {
            return;
        }
        final int severity = this.computeSeverity(16777801);
        if (severity == 256) {
            return;
        }
        if (!this.options.reportUnavoidableGenericTypeProblems && expression.forcedToBeRaw(this.referenceContext)) {
            return;
        }
        this.handle(16777801, new String[] { new String(expressionType.readableName()), new String(expectedType.readableName()), new String(expectedType.erasure().readableName()) }, new String[] { new String(expressionType.shortReadableName()), new String(expectedType.shortReadableName()), new String(expectedType.erasure().shortReadableName()) }, severity, expression.sourceStart, expression.sourceEnd);
    }
    
    public void unusedArgument(final LocalDeclaration localDecl) {
        final int severity = this.computeSeverity(536870974);
        if (severity == 256) {
            return;
        }
        final String[] arguments = { new String(localDecl.name) };
        this.handle(536870974, arguments, arguments, severity, localDecl.sourceStart, localDecl.sourceEnd);
    }
    
    public void unusedExceptionParameter(final LocalDeclaration exceptionParameter) {
        final int severity = this.computeSeverity(536870997);
        if (severity == 256) {
            return;
        }
        final String[] arguments = { new String(exceptionParameter.name) };
        this.handle(536870997, arguments, arguments, severity, exceptionParameter.sourceStart, exceptionParameter.sourceEnd);
    }
    
    public void unusedDeclaredThrownException(final ReferenceBinding exceptionType, final AbstractMethodDeclaration method, final ASTNode location) {
        final boolean isConstructor = method.isConstructor();
        final int severity = this.computeSeverity(isConstructor ? 536871098 : 536871097);
        if (severity == 256) {
            return;
        }
        if (isConstructor) {
            this.handle(536871098, new String[] { new String(method.binding.declaringClass.readableName()), this.typesAsString(method.binding, false), new String(exceptionType.readableName()) }, new String[] { new String(method.binding.declaringClass.shortReadableName()), this.typesAsString(method.binding, true), new String(exceptionType.shortReadableName()) }, severity, location.sourceStart, location.sourceEnd);
        }
        else {
            this.handle(536871097, new String[] { new String(method.binding.declaringClass.readableName()), new String(method.selector), this.typesAsString(method.binding, false), new String(exceptionType.readableName()) }, new String[] { new String(method.binding.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method.binding, true), new String(exceptionType.shortReadableName()) }, severity, location.sourceStart, location.sourceEnd);
        }
    }
    
    public void unusedImport(final ImportReference importRef) {
        final int severity = this.computeSeverity(268435844);
        if (severity == 256) {
            return;
        }
        final String[] arguments = { CharOperation.toString(importRef.tokens) };
        this.handle(268435844, arguments, arguments, severity, importRef.sourceStart, importRef.sourceEnd);
    }
    
    public void unusedLabel(final LabeledStatement statement) {
        final int severity = this.computeSeverity(536871111);
        if (severity == 256) {
            return;
        }
        final String[] arguments = { new String(statement.label) };
        this.handle(536871111, arguments, arguments, severity, statement.sourceStart, statement.labelEnd);
    }
    
    public void unusedLocalVariable(final LocalDeclaration localDecl) {
        final int severity = this.computeSeverity(536870973);
        if (severity == 256) {
            return;
        }
        final String[] arguments = { new String(localDecl.name) };
        this.handle(536870973, arguments, arguments, severity, localDecl.sourceStart, localDecl.sourceEnd);
    }
    
    public void unusedObjectAllocation(final AllocationExpression allocationExpression) {
        this.handle(536871060, ProblemReporter.NoArgument, ProblemReporter.NoArgument, allocationExpression.sourceStart, allocationExpression.sourceEnd);
    }
    
    public void unusedPrivateConstructor(final ConstructorDeclaration constructorDecl) {
        final int severity = this.computeSeverity(603979910);
        if (severity == 256) {
            return;
        }
        if (this.excludeDueToAnnotation(constructorDecl.annotations, 603979910)) {
            return;
        }
        final MethodBinding constructor = constructorDecl.binding;
        this.handle(603979910, new String[] { new String(constructor.declaringClass.readableName()), this.typesAsString(constructor, false) }, new String[] { new String(constructor.declaringClass.shortReadableName()), this.typesAsString(constructor, true) }, severity, constructorDecl.sourceStart, constructorDecl.sourceEnd);
    }
    
    public void unusedPrivateField(final FieldDeclaration fieldDecl) {
        final int severity = this.computeSeverity(570425421);
        if (severity == 256) {
            return;
        }
        final FieldBinding field = fieldDecl.binding;
        if (CharOperation.equals(TypeConstants.SERIALVERSIONUID, field.name) && field.isStatic() && field.isFinal() && TypeBinding.equalsEquals(TypeBinding.LONG, field.type)) {
            final ReferenceBinding referenceBinding = field.declaringClass;
            if (referenceBinding != null && referenceBinding.findSuperTypeOriginatingFrom(37, false) != null) {
                return;
            }
        }
        if (CharOperation.equals(TypeConstants.SERIALPERSISTENTFIELDS, field.name) && field.isStatic() && field.isFinal() && field.type.dimensions() == 1 && CharOperation.equals(TypeConstants.CharArray_JAVA_IO_OBJECTSTREAMFIELD, field.type.leafComponentType().readableName())) {
            final ReferenceBinding referenceBinding = field.declaringClass;
            if (referenceBinding != null && referenceBinding.findSuperTypeOriginatingFrom(37, false) != null) {
                return;
            }
        }
        if (this.excludeDueToAnnotation(fieldDecl.annotations, 570425421)) {
            return;
        }
        this.handle(570425421, new String[] { new String(field.declaringClass.readableName()), new String(field.name) }, new String[] { new String(field.declaringClass.shortReadableName()), new String(field.name) }, severity, this.nodeSourceStart(field, fieldDecl), this.nodeSourceEnd(field, fieldDecl));
    }
    
    public void unusedPrivateMethod(final AbstractMethodDeclaration methodDecl) {
        final int severity = this.computeSeverity(603979894);
        if (severity == 256) {
            return;
        }
        final MethodBinding method = methodDecl.binding;
        if (!method.isStatic() && TypeBinding.VOID == method.returnType && method.parameters.length == 1 && method.parameters[0].dimensions() == 0 && CharOperation.equals(method.selector, TypeConstants.READOBJECT) && CharOperation.equals(TypeConstants.CharArray_JAVA_IO_OBJECTINPUTSTREAM, method.parameters[0].readableName())) {
            return;
        }
        if (!method.isStatic() && TypeBinding.VOID == method.returnType && method.parameters.length == 1 && method.parameters[0].dimensions() == 0 && CharOperation.equals(method.selector, TypeConstants.WRITEOBJECT) && CharOperation.equals(TypeConstants.CharArray_JAVA_IO_OBJECTOUTPUTSTREAM, method.parameters[0].readableName())) {
            return;
        }
        if (!method.isStatic() && 1 == method.returnType.id && method.parameters.length == 0 && CharOperation.equals(method.selector, TypeConstants.READRESOLVE)) {
            return;
        }
        if (!method.isStatic() && 1 == method.returnType.id && method.parameters.length == 0 && CharOperation.equals(method.selector, TypeConstants.WRITEREPLACE)) {
            return;
        }
        if (this.excludeDueToAnnotation(methodDecl.annotations, 603979894)) {
            return;
        }
        this.handle(603979894, new String[] { new String(method.declaringClass.readableName()), new String(method.selector), this.typesAsString(method, false) }, new String[] { new String(method.declaringClass.shortReadableName()), new String(method.selector), this.typesAsString(method, true) }, severity, methodDecl.sourceStart, methodDecl.sourceEnd);
    }
    
    private boolean excludeDueToAnnotation(final Annotation[] annotations, final int problemId) {
        int annotationsLen = 0;
        if (annotations == null) {
            return false;
        }
        annotationsLen = annotations.length;
        if (annotationsLen == 0) {
            return false;
        }
        for (int i = 0; i < annotationsLen; ++i) {
            final TypeBinding resolvedType = annotations[i].resolvedType;
            if (resolvedType != null) {
                switch (resolvedType.id) {
                    case 44:
                    case 49:
                    case 60: {
                        break;
                    }
                    case 80:
                    case 81:
                    case 82: {
                        if (problemId != 570425421) {
                            return true;
                        }
                        break;
                    }
                    default: {
                        if (resolvedType instanceof ReferenceBinding && ((ReferenceBinding)resolvedType).hasNullBit(224)) {
                            break;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public void unusedPrivateType(final TypeDeclaration typeDecl) {
        final int severity = this.computeSeverity(553648135);
        if (severity == 256) {
            return;
        }
        if (this.excludeDueToAnnotation(typeDecl.annotations, 553648135)) {
            return;
        }
        final ReferenceBinding type = typeDecl.binding;
        this.handle(553648135, new String[] { new String(type.readableName()) }, new String[] { new String(type.shortReadableName()) }, severity, typeDecl.sourceStart, typeDecl.sourceEnd);
    }
    
    public void unusedTypeParameter(final TypeParameter typeParameter) {
        final int severity = this.computeSeverity(16777877);
        if (severity == 256) {
            return;
        }
        final String[] arguments = { new String(typeParameter.name) };
        this.handle(16777877, arguments, arguments, typeParameter.sourceStart, typeParameter.sourceEnd);
    }
    
    public void unusedWarningToken(final Expression token) {
        final String[] arguments = { token.constant.stringValue() };
        this.handle(536871547, arguments, arguments, token.sourceStart, token.sourceEnd);
    }
    
    public void useAssertAsAnIdentifier(final int sourceStart, final int sourceEnd) {
        this.handle(536871352, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    public void useEnumAsAnIdentifier(final int sourceStart, final int sourceEnd) {
        this.handle(536871353, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
    }
    
    public void illegalUseOfUnderscoreAsAnIdentifier(final int sourceStart, final int sourceEnd, final boolean lambdaParameter) {
        this.underScoreIsLambdaParameter = lambdaParameter;
        try {
            this.handle(1610613179, ProblemReporter.NoArgument, ProblemReporter.NoArgument, sourceStart, sourceEnd);
        }
        finally {
            this.underScoreIsLambdaParameter = false;
        }
        this.underScoreIsLambdaParameter = false;
    }
    
    public void varargsArgumentNeedCast(final MethodBinding method, final TypeBinding argumentType, final InvocationSite location) {
        final int severity = this.options.getSeverity(536870976);
        if (severity == 256) {
            return;
        }
        final ArrayBinding varargsType = (ArrayBinding)method.parameters[method.parameters.length - 1];
        if (method.isConstructor()) {
            this.handle(134218530, new String[] { new String(argumentType.readableName()), new String(varargsType.readableName()), new String(method.declaringClass.readableName()), this.typesAsString(method, false), new String(varargsType.elementsType().readableName()) }, new String[] { new String(argumentType.shortReadableName()), new String(varargsType.shortReadableName()), new String(method.declaringClass.shortReadableName()), this.typesAsString(method, true), new String(varargsType.elementsType().shortReadableName()) }, severity, location.sourceStart(), location.sourceEnd());
        }
        else {
            this.handle(67109665, new String[] { new String(argumentType.readableName()), new String(varargsType.readableName()), new String(method.selector), this.typesAsString(method, false), new String(method.declaringClass.readableName()), new String(varargsType.elementsType().readableName()) }, new String[] { new String(argumentType.shortReadableName()), new String(varargsType.shortReadableName()), new String(method.selector), this.typesAsString(method, true), new String(method.declaringClass.shortReadableName()), new String(varargsType.elementsType().shortReadableName()) }, severity, location.sourceStart(), location.sourceEnd());
        }
    }
    
    public void varargsConflict(final MethodBinding method1, final MethodBinding method2, final SourceTypeBinding type) {
        this.handle(67109667, new String[] { new String(method1.selector), this.typesAsString(method1, false), new String(method1.declaringClass.readableName()), this.typesAsString(method2, false), new String(method2.declaringClass.readableName()) }, new String[] { new String(method1.selector), this.typesAsString(method1, true), new String(method1.declaringClass.shortReadableName()), this.typesAsString(method2, true), new String(method2.declaringClass.shortReadableName()) }, TypeBinding.equalsEquals(method1.declaringClass, type) ? method1.sourceStart() : type.sourceStart(), TypeBinding.equalsEquals(method1.declaringClass, type) ? method1.sourceEnd() : type.sourceEnd());
    }
    
    public void safeVarargsOnFixedArityMethod(final MethodBinding method) {
        final String[] arguments = { new String(method.isConstructor() ? method.declaringClass.shortReadableName() : method.selector) };
        this.handle(67109668, arguments, arguments, method.sourceStart(), method.sourceEnd());
    }
    
    public void safeVarargsOnNonFinalInstanceMethod(final MethodBinding method) {
        final String[] arguments = { new String(method.isConstructor() ? method.declaringClass.shortReadableName() : method.selector) };
        this.handle(67109669, arguments, arguments, method.sourceStart(), method.sourceEnd());
    }
    
    public void possibleHeapPollutionFromVararg(final AbstractVariableDeclaration vararg) {
        final String[] arguments = { new String(vararg.name) };
        this.handle(67109670, arguments, arguments, vararg.sourceStart, vararg.sourceEnd);
    }
    
    public void variableTypeCannotBeVoid(final AbstractVariableDeclaration varDecl) {
        final String[] arguments = { new String(varDecl.name) };
        this.handle(536870964, arguments, arguments, varDecl.sourceStart, varDecl.sourceEnd);
    }
    
    public void variableTypeCannotBeVoidArray(final AbstractVariableDeclaration varDecl) {
        this.handle(536870966, ProblemReporter.NoArgument, ProblemReporter.NoArgument, varDecl.type.sourceStart, varDecl.type.sourceEnd);
    }
    
    public void visibilityConflict(final MethodBinding currentMethod, final MethodBinding inheritedMethod) {
        this.handle(67109273, new String[] { new String(inheritedMethod.declaringClass.readableName()) }, new String[] { new String(inheritedMethod.declaringClass.shortReadableName()) }, currentMethod.sourceStart(), currentMethod.sourceEnd());
    }
    
    public void wildcardAssignment(final TypeBinding variableType, final TypeBinding expressionType, final ASTNode location) {
        this.handle(16777758, new String[] { new String(expressionType.readableName()), new String(variableType.readableName()) }, new String[] { new String(expressionType.shortReadableName()), new String(variableType.shortReadableName()) }, location.sourceStart, location.sourceEnd);
    }
    
    public void wildcardInvocation(final ASTNode location, final TypeBinding receiverType, final MethodBinding method, final TypeBinding[] arguments) {
        TypeBinding offendingArgument = null;
        TypeBinding offendingParameter = null;
        for (int i = 0, length = method.parameters.length; i < length; ++i) {
            final TypeBinding parameter = method.parameters[i];
            if (parameter.isWildcard() && ((WildcardBinding)parameter).boundKind != 2) {
                offendingParameter = parameter;
                offendingArgument = arguments[i];
                break;
            }
        }
        if (method.isConstructor()) {
            this.handle(16777756, new String[] { new String(receiverType.sourceName()), this.typesAsString(method, false), new String(receiverType.readableName()), this.typesAsString(arguments, false), new String(offendingArgument.readableName()), new String(offendingParameter.readableName()) }, new String[] { new String(receiverType.sourceName()), this.typesAsString(method, true), new String(receiverType.shortReadableName()), this.typesAsString(arguments, true), new String(offendingArgument.shortReadableName()), new String(offendingParameter.shortReadableName()) }, location.sourceStart, location.sourceEnd);
        }
        else {
            this.handle(16777757, new String[] { new String(method.selector), this.typesAsString(method, false), new String(receiverType.readableName()), this.typesAsString(arguments, false), new String(offendingArgument.readableName()), new String(offendingParameter.readableName()) }, new String[] { new String(method.selector), this.typesAsString(method, true), new String(receiverType.shortReadableName()), this.typesAsString(arguments, true), new String(offendingArgument.shortReadableName()), new String(offendingParameter.shortReadableName()) }, location.sourceStart, location.sourceEnd);
        }
    }
    
    public void wrongSequenceOfExceptionTypesError(final TypeReference typeRef, final TypeBinding exceptionType, final TypeBinding hidingExceptionType) {
        this.handle(553648315, new String[] { new String(exceptionType.readableName()), new String(hidingExceptionType.readableName()) }, new String[] { new String(exceptionType.shortReadableName()), new String(hidingExceptionType.shortReadableName()) }, typeRef.sourceStart, typeRef.sourceEnd);
    }
    
    public void wrongSequenceOfExceptionTypes(final TypeReference typeRef, final TypeBinding exceptionType, final TypeBinding hidingExceptionType) {
        this.handle(553649001, new String[] { new String(exceptionType.readableName()), new String(hidingExceptionType.readableName()) }, new String[] { new String(exceptionType.shortReadableName()), new String(hidingExceptionType.shortReadableName()) }, typeRef.sourceStart, typeRef.sourceEnd);
    }
    
    public void autoManagedResourcesNotBelow17(final LocalDeclaration[] resources) {
        this.handle(1610613610, ProblemReporter.NoArgument, ProblemReporter.NoArgument, resources[0].declarationSourceStart, resources[resources.length - 1].declarationSourceEnd);
    }
    
    public void cannotInferElidedTypes(final AllocationExpression allocationExpression) {
        final String[] arguments = { allocationExpression.type.toString() };
        this.handle(16778094, arguments, arguments, allocationExpression.sourceStart, allocationExpression.sourceEnd);
    }
    
    public void diamondNotWithExplicitTypeArguments(final TypeReference[] typeArguments) {
        this.handle(16778095, ProblemReporter.NoArgument, ProblemReporter.NoArgument, typeArguments[0].sourceStart, typeArguments[typeArguments.length - 1].sourceEnd);
    }
    
    public void rawConstructorReferenceNotWithExplicitTypeArguments(final TypeReference[] typeArguments) {
        this.handle(16778219, ProblemReporter.NoArgument, ProblemReporter.NoArgument, typeArguments[0].sourceStart, typeArguments[typeArguments.length - 1].sourceEnd);
    }
    
    public void diamondNotWithAnoymousClasses(final TypeReference type) {
        this.handle(16778096, ProblemReporter.NoArgument, ProblemReporter.NoArgument, type.sourceStart, type.sourceEnd);
    }
    
    public void redundantSpecificationOfTypeArguments(final ASTNode location, final TypeBinding[] argumentTypes) {
        final int severity = this.computeSeverity(16778100);
        if (severity != 256) {
            int sourceStart = -1;
            if (location instanceof QualifiedTypeReference) {
                final QualifiedTypeReference ref = (QualifiedTypeReference)location;
                sourceStart = (int)(ref.sourcePositions[ref.sourcePositions.length - 1] >> 32);
            }
            else {
                sourceStart = location.sourceStart;
            }
            this.handle(16778100, new String[] { this.typesAsString(argumentTypes, false) }, new String[] { this.typesAsString(argumentTypes, true) }, severity, sourceStart, location.sourceEnd);
        }
    }
    
    public void potentiallyUnclosedCloseable(final FakedTrackingVariable trackVar, final ASTNode location) {
        final String[] args = { trackVar.nameForReporting(location, this.referenceContext) };
        if (location == null) {
            this.handle(536871797, args, args, trackVar.sourceStart, trackVar.sourceEnd);
        }
        else {
            this.handle(536871798, args, args, location.sourceStart, location.sourceEnd);
        }
    }
    
    public void unclosedCloseable(final FakedTrackingVariable trackVar, final ASTNode location) {
        final String[] args = { String.valueOf(trackVar.name) };
        if (location == null) {
            this.handle(536871799, args, args, trackVar.sourceStart, trackVar.sourceEnd);
        }
        else {
            this.handle(536871800, args, args, location.sourceStart, location.sourceEnd);
        }
    }
    
    public void explicitlyClosedAutoCloseable(final FakedTrackingVariable trackVar) {
        final String[] args = { String.valueOf(trackVar.name) };
        this.handle(536871801, args, args, trackVar.sourceStart, trackVar.sourceEnd);
    }
    
    public void nullityMismatch(final Expression expression, final TypeBinding providedType, final TypeBinding requiredType, final int nullStatus, final char[][] annotationName) {
        if ((nullStatus & 0x2) != 0x0) {
            this.nullityMismatchIsNull(expression, requiredType);
            return;
        }
        if (expression instanceof MessageSend && (((MessageSend)expression).binding.tagBits & 0x80000000000000L) != 0x0L) {
            this.nullityMismatchSpecdNullable(expression, requiredType, this.options.nonNullAnnotationName);
            return;
        }
        if ((nullStatus & 0x10) == 0x0) {
            if (this.options.usesNullTypeAnnotations()) {
                this.nullityMismatchingTypeAnnotation(expression, providedType, requiredType, NullAnnotationMatching.NULL_ANNOTATIONS_UNCHECKED);
            }
            else {
                this.nullityMismatchIsUnknown(expression, providedType, requiredType, annotationName);
            }
            return;
        }
        VariableBinding var = expression.localVariableBinding();
        if (var == null && expression instanceof Reference) {
            var = ((Reference)expression).lastFieldBinding();
        }
        if (var != null && var.type.isFreeTypeVariable()) {
            this.nullityMismatchVariableIsFreeTypeVariable(var, expression);
            return;
        }
        if (var != null && var.isNullable()) {
            this.nullityMismatchSpecdNullable(expression, requiredType, annotationName);
            return;
        }
        this.nullityMismatchPotentiallyNull(expression, requiredType, annotationName);
    }
    
    public void nullityMismatchIsNull(final Expression expression, TypeBinding requiredType) {
        int problemId = 16778126;
        final boolean useNullTypeAnnotations = this.options.usesNullTypeAnnotations();
        if (useNullTypeAnnotations && requiredType.isTypeVariable() && !requiredType.hasNullTypeAnnotations()) {
            problemId = 969;
        }
        if (requiredType instanceof CaptureBinding) {
            final CaptureBinding capture = (CaptureBinding)requiredType;
            if (capture.wildcard != null) {
                requiredType = capture.wildcard;
            }
        }
        String[] arguments;
        String[] argumentsShort;
        if (!useNullTypeAnnotations) {
            arguments = new String[] { this.annotatedTypeName(requiredType, this.options.nonNullAnnotationName) };
            argumentsShort = new String[] { this.shortAnnotatedTypeName(requiredType, this.options.nonNullAnnotationName) };
        }
        else if (problemId == 969) {
            arguments = new String[] { new String(requiredType.sourceName()) };
            argumentsShort = new String[] { new String(requiredType.sourceName()) };
        }
        else {
            arguments = new String[] { new String(requiredType.nullAnnotatedReadableName(this.options, false)) };
            argumentsShort = new String[] { new String(requiredType.nullAnnotatedReadableName(this.options, true)) };
        }
        this.handle(problemId, arguments, argumentsShort, expression.sourceStart, expression.sourceEnd);
    }
    
    public void nullityMismatchSpecdNullable(final Expression expression, final TypeBinding requiredType, final char[][] annotationName) {
        int problemId = 536871845;
        final char[][] nullableName = this.options.nullableAnnotationName;
        final String[] arguments = { this.annotatedTypeName(requiredType, annotationName), String.valueOf(CharOperation.concatWith(nullableName, '.')) };
        final String[] argumentsShort = { this.shortAnnotatedTypeName(requiredType, annotationName), String.valueOf(nullableName[nullableName.length - 1]) };
        if (expression.resolvedType != null && expression.resolvedType.hasNullTypeAnnotations()) {
            problemId = 536871865;
            arguments[1] = String.valueOf(expression.resolvedType.nullAnnotatedReadableName(this.options, false));
            argumentsShort[1] = String.valueOf(expression.resolvedType.nullAnnotatedReadableName(this.options, true));
        }
        this.handle(problemId, arguments, argumentsShort, expression.sourceStart, expression.sourceEnd);
    }
    
    public void nullityMismatchPotentiallyNull(final Expression expression, final TypeBinding requiredType, final char[][] annotationName) {
        final int problemId = 16778127;
        final char[][] nullableName = this.options.nullableAnnotationName;
        final String[] arguments = { this.annotatedTypeName(requiredType, annotationName), String.valueOf(CharOperation.concatWith(nullableName, '.')) };
        final String[] argumentsShort = { this.shortAnnotatedTypeName(requiredType, annotationName), String.valueOf(nullableName[nullableName.length - 1]) };
        this.handle(problemId, arguments, argumentsShort, expression.sourceStart, expression.sourceEnd);
    }
    
    public void nullityMismatchIsUnknown(final Expression expression, final TypeBinding providedType, final TypeBinding requiredType, final char[][] annotationName) {
        final int problemId = 16778128;
        final String[] arguments = { String.valueOf(providedType.readableName()), this.annotatedTypeName(requiredType, annotationName) };
        final String[] argumentsShort = { String.valueOf(providedType.shortReadableName()), this.shortAnnotatedTypeName(requiredType, annotationName) };
        this.handle(problemId, arguments, argumentsShort, expression.sourceStart, expression.sourceEnd);
    }
    
    private void nullityMismatchIsFreeTypeVariable(final TypeBinding providedType, final int sourceStart, final int sourceEnd) {
        final char[][] nullableName = this.options.nullableAnnotationName;
        final char[][] nonNullName = this.options.nonNullAnnotationName;
        final String[] arguments = { new String(nonNullName[nonNullName.length - 1]), new String(providedType.readableName()), new String(nullableName[nullableName.length - 1]) };
        this.handle(16778195, arguments, arguments, sourceStart, sourceEnd);
    }
    
    public void nullityMismatchVariableIsFreeTypeVariable(final VariableBinding variable, final ASTNode location) {
        final int severity = this.computeSeverity(16778195);
        if (severity == 256) {
            return;
        }
        this.nullityMismatchIsFreeTypeVariable(variable.type, this.nodeSourceStart(variable, location), this.nodeSourceEnd(variable, location));
    }
    
    public void illegalRedefinitionToNonNullParameter(final Argument argument, final ReferenceBinding declaringClass, final char[][] inheritedAnnotationName) {
        int sourceStart = argument.type.sourceStart;
        if (argument.annotations != null) {
            for (int i = 0; i < argument.annotations.length; ++i) {
                final Annotation annotation = argument.annotations[i];
                if (annotation.hasNullBit(96)) {
                    sourceStart = annotation.sourceStart;
                    break;
                }
            }
        }
        if (inheritedAnnotationName == null) {
            this.handle(67109780, new String[] { new String(argument.name), new String(declaringClass.readableName()) }, new String[] { new String(argument.name), new String(declaringClass.shortReadableName()) }, sourceStart, argument.type.sourceEnd);
        }
        else {
            this.handle(67109779, new String[] { new String(argument.name), new String(declaringClass.readableName()), CharOperation.toString(inheritedAnnotationName) }, new String[] { new String(argument.name), new String(declaringClass.shortReadableName()), new String(inheritedAnnotationName[inheritedAnnotationName.length - 1]) }, sourceStart, argument.type.sourceEnd);
        }
    }
    
    public void parameterLackingNullableAnnotation(final Argument argument, final ReferenceBinding declaringClass, final char[][] inheritedAnnotationName) {
        this.handle(67109782, new String[] { new String(declaringClass.readableName()), CharOperation.toString(inheritedAnnotationName) }, new String[] { new String(declaringClass.shortReadableName()), new String(inheritedAnnotationName[inheritedAnnotationName.length - 1]) }, argument.type.sourceStart, argument.type.sourceEnd);
    }
    
    public void parameterLackingNonnullAnnotation(final Argument argument, final ReferenceBinding declaringClass, final char[][] inheritedAnnotationName) {
        int sourceStart = 0;
        int sourceEnd = 0;
        if (argument != null) {
            sourceStart = argument.type.sourceStart;
            sourceEnd = argument.type.sourceEnd;
        }
        else if (this.referenceContext instanceof TypeDeclaration) {
            sourceStart = ((TypeDeclaration)this.referenceContext).sourceStart;
            sourceEnd = ((TypeDeclaration)this.referenceContext).sourceEnd;
        }
        this.handle(67109781, new String[] { new String(declaringClass.readableName()), CharOperation.toString(inheritedAnnotationName) }, new String[] { new String(declaringClass.shortReadableName()), new String(inheritedAnnotationName[inheritedAnnotationName.length - 1]) }, sourceStart, sourceEnd);
    }
    
    public void illegalParameterRedefinition(final Argument argument, final ReferenceBinding declaringClass, final TypeBinding inheritedParameter) {
        int sourceStart = argument.type.sourceStart;
        if (argument.annotations != null) {
            for (int i = 0; i < argument.annotations.length; ++i) {
                final Annotation annotation = argument.annotations[i];
                if (annotation.hasNullBit(96)) {
                    sourceStart = annotation.sourceStart;
                    break;
                }
            }
        }
        this.handle(67109836, new String[] { new String(argument.name), new String(declaringClass.readableName()), new String(inheritedParameter.nullAnnotatedReadableName(this.options, false)) }, new String[] { new String(argument.name), new String(declaringClass.shortReadableName()), new String(inheritedParameter.nullAnnotatedReadableName(this.options, true)) }, sourceStart, argument.type.sourceEnd);
    }
    
    public void illegalReturnRedefinition(final AbstractMethodDeclaration abstractMethodDecl, final MethodBinding inheritedMethod, final char[][] nonNullAnnotationName) {
        final MethodDeclaration methodDecl = (MethodDeclaration)abstractMethodDecl;
        final StringBuffer methodSignature = new StringBuffer();
        methodSignature.append(inheritedMethod.declaringClass.readableName()).append('.').append(inheritedMethod.readableName());
        final StringBuffer shortSignature = new StringBuffer();
        shortSignature.append(inheritedMethod.declaringClass.shortReadableName()).append('.').append(inheritedMethod.shortReadableName());
        int sourceStart = methodDecl.returnType.sourceStart;
        final Annotation[] annotations = methodDecl.annotations;
        final Annotation annotation = this.findAnnotation(annotations, 64);
        if (annotation != null) {
            sourceStart = annotation.sourceStart;
        }
        final TypeBinding inheritedReturnType = inheritedMethod.returnType;
        int problemId = 67109778;
        final StringBuilder returnType = new StringBuilder();
        final StringBuilder returnTypeShort = new StringBuilder();
        if (this.options.usesNullTypeAnnotations()) {
            if (inheritedReturnType.isTypeVariable() && (inheritedReturnType.tagBits & 0x180000000000000L) == 0x0L) {
                problemId = 67109838;
                returnType.append(inheritedReturnType.readableName());
                returnTypeShort.append(inheritedReturnType.shortReadableName());
            }
            else {
                returnType.append(inheritedReturnType.nullAnnotatedReadableName(this.options, false));
                returnTypeShort.append(inheritedReturnType.nullAnnotatedReadableName(this.options, true));
            }
        }
        else {
            returnType.append('@').append(CharOperation.concatWith(nonNullAnnotationName, '.'));
            returnType.append(' ').append(inheritedReturnType.readableName());
            returnTypeShort.append('@').append(nonNullAnnotationName[nonNullAnnotationName.length - 1]);
            returnTypeShort.append(' ').append(inheritedReturnType.shortReadableName());
        }
        final String[] arguments = { methodSignature.toString(), returnType.toString() };
        final String[] argumentsShort = { shortSignature.toString(), returnTypeShort.toString() };
        this.handle(problemId, arguments, argumentsShort, sourceStart, methodDecl.returnType.sourceEnd);
    }
    
    public void referenceExpressionArgumentNullityMismatch(final ReferenceExpression location, final TypeBinding requiredType, final TypeBinding providedType, final MethodBinding descriptorMethod, final int idx, final NullAnnotationMatching status) {
        final StringBuffer methodSignature = new StringBuffer();
        methodSignature.append(descriptorMethod.declaringClass.readableName()).append('.').append(descriptorMethod.readableName());
        final StringBuffer shortSignature = new StringBuffer();
        shortSignature.append(descriptorMethod.declaringClass.shortReadableName()).append('.').append(descriptorMethod.shortReadableName());
        this.handle(status.isUnchecked() ? 67109822 : 67109821, new String[] { String.valueOf(idx + 1), String.valueOf(requiredType.nullAnnotatedReadableName(this.options, false)), String.valueOf(providedType.nullAnnotatedReadableName(this.options, false)), methodSignature.toString() }, new String[] { String.valueOf(idx + 1), String.valueOf(requiredType.nullAnnotatedReadableName(this.options, true)), String.valueOf(providedType.nullAnnotatedReadableName(this.options, true)), shortSignature.toString() }, location.sourceStart, location.sourceEnd);
    }
    
    public void illegalReturnRedefinition(final ASTNode location, final MethodBinding descriptorMethod, final boolean isUnchecked, final TypeBinding providedType) {
        final StringBuffer methodSignature = new StringBuffer().append(descriptorMethod.declaringClass.readableName()).append('.').append(descriptorMethod.readableName());
        final StringBuffer shortSignature = new StringBuffer().append(descriptorMethod.declaringClass.shortReadableName()).append('.').append(descriptorMethod.shortReadableName());
        this.handle(isUnchecked ? 67109824 : 67109823, new String[] { methodSignature.toString(), String.valueOf(descriptorMethod.returnType.nullAnnotatedReadableName(this.options, false)), String.valueOf(providedType.nullAnnotatedReadableName(this.options, false)) }, new String[] { shortSignature.toString(), String.valueOf(descriptorMethod.returnType.nullAnnotatedReadableName(this.options, true)), String.valueOf(providedType.nullAnnotatedReadableName(this.options, true)) }, location.sourceStart, location.sourceEnd);
    }
    
    public void messageSendPotentialNullReference(final MethodBinding method, final ASTNode location) {
        final String[] arguments = { new String(method.readableName()) };
        this.handle(536871831, arguments, arguments, location.sourceStart, location.sourceEnd);
    }
    
    public void messageSendRedundantCheckOnNonNull(final MethodBinding method, final ASTNode location) {
        final String[] arguments = { new String(method.readableName()) };
        this.handle(536871832, arguments, arguments, location.sourceStart, location.sourceEnd);
    }
    
    public void expressionNullReference(final ASTNode location) {
        this.handle(536871584, ProblemReporter.NoArgument, ProblemReporter.NoArgument, location.sourceStart, location.sourceEnd);
    }
    
    public void expressionPotentialNullReference(final ASTNode location) {
        this.handle(536871585, ProblemReporter.NoArgument, ProblemReporter.NoArgument, location.sourceStart, location.sourceEnd);
    }
    
    public void cannotImplementIncompatibleNullness(final MethodBinding currentMethod, final MethodBinding inheritedMethod, final boolean showReturn) {
        int sourceStart = 0;
        int sourceEnd = 0;
        if (this.referenceContext instanceof TypeDeclaration) {
            sourceStart = ((TypeDeclaration)this.referenceContext).sourceStart;
            sourceEnd = ((TypeDeclaration)this.referenceContext).sourceEnd;
        }
        final String[] problemArguments = { showReturn ? (String.valueOf(new String(currentMethod.returnType.nullAnnotatedReadableName(this.options, false))) + ' ') : "", new String(currentMethod.selector), this.typesAsString(currentMethod, false, true), new String(currentMethod.declaringClass.readableName()), new String(inheritedMethod.declaringClass.readableName()) };
        final String[] messageArguments = { showReturn ? (String.valueOf(new String(currentMethod.returnType.nullAnnotatedReadableName(this.options, true))) + ' ') : "", new String(currentMethod.selector), this.typesAsString(currentMethod, true, true), new String(currentMethod.declaringClass.shortReadableName()), new String(inheritedMethod.declaringClass.shortReadableName()) };
        this.handle(536871833, problemArguments, messageArguments, sourceStart, sourceEnd);
    }
    
    public void nullAnnotationIsRedundant(final AbstractMethodDeclaration sourceMethod, final int i) {
        int sourceStart;
        int sourceEnd;
        if (i == -1) {
            final MethodDeclaration methodDecl = (MethodDeclaration)sourceMethod;
            final Annotation annotation = this.findAnnotation(methodDecl.annotations, 32);
            sourceStart = ((annotation != null) ? annotation.sourceStart : methodDecl.returnType.sourceStart);
            sourceEnd = methodDecl.returnType.sourceEnd;
        }
        else {
            final Argument arg = sourceMethod.arguments[i];
            sourceStart = arg.declarationSourceStart;
            sourceEnd = arg.sourceEnd;
        }
        this.handle(67109786, ProblemHandler.NoArgument, ProblemHandler.NoArgument, sourceStart, sourceEnd);
    }
    
    public void nullAnnotationIsRedundant(final FieldDeclaration sourceField) {
        final Annotation annotation = this.findAnnotation(sourceField.annotations, 32);
        final int sourceStart = (annotation != null) ? annotation.sourceStart : sourceField.type.sourceStart;
        final int sourceEnd = sourceField.type.sourceEnd;
        this.handle(67109786, ProblemHandler.NoArgument, ProblemHandler.NoArgument, sourceStart, sourceEnd);
    }
    
    public void nullDefaultAnnotationIsRedundant(final ASTNode location, final Annotation[] annotations, final Binding outer) {
        final Annotation annotation = this.findAnnotation(annotations, 128);
        final int start = (annotation != null) ? annotation.sourceStart : location.sourceStart;
        final int end = (annotation != null) ? annotation.sourceEnd : location.sourceStart;
        String[] args = ProblemReporter.NoArgument;
        String[] shortArgs = ProblemReporter.NoArgument;
        if (outer != null) {
            args = new String[] { new String(outer.readableName()) };
            shortArgs = new String[] { new String(outer.shortReadableName()) };
        }
        int problemId = 536871837;
        if (outer instanceof PackageBinding) {
            problemId = 536871838;
        }
        else if (outer instanceof ReferenceBinding) {
            problemId = 536871839;
        }
        else if (outer instanceof MethodBinding) {
            problemId = 536871840;
        }
        this.handle(problemId, args, shortArgs, start, end);
    }
    
    public void contradictoryNullAnnotations(final Annotation annotation) {
        this.contradictoryNullAnnotations(annotation.sourceStart, annotation.sourceEnd);
    }
    
    public void contradictoryNullAnnotations(final Annotation[] annotations) {
        this.contradictoryNullAnnotations(annotations[0].sourceStart, annotations[annotations.length - 1].sourceEnd);
    }
    
    public void contradictoryNullAnnotations(final int sourceStart, final int sourceEnd) {
        final char[][] nonNullAnnotationName = this.options.nonNullAnnotationName;
        final char[][] nullableAnnotationName = this.options.nullableAnnotationName;
        final String[] arguments = { new String(CharOperation.concatWith(nonNullAnnotationName, '.')), new String(CharOperation.concatWith(nullableAnnotationName, '.')) };
        final String[] shortArguments = { new String(nonNullAnnotationName[nonNullAnnotationName.length - 1]), new String(nullableAnnotationName[nullableAnnotationName.length - 1]) };
        this.handle(536871841, arguments, shortArguments, sourceStart, sourceEnd);
    }
    
    public void contradictoryNullAnnotationsInferred(final MethodBinding inferredMethod, final ASTNode location) {
        this.contradictoryNullAnnotationsInferred(inferredMethod, location.sourceStart, location.sourceEnd, false);
    }
    
    public void contradictoryNullAnnotationsInferred(final MethodBinding inferredMethod, final int sourceStart, final int sourceEnd, final boolean isFunctionalExpression) {
        final char[][] nonNullAnnotationName = this.options.nonNullAnnotationName;
        final char[][] nullableAnnotationName = this.options.nullableAnnotationName;
        final String[] arguments = { new String(CharOperation.concatWith(nonNullAnnotationName, '.')), new String(CharOperation.concatWith(nullableAnnotationName, '.')), new String(inferredMethod.returnType.nullAnnotatedReadableName(this.options, false)), new String(inferredMethod.selector), this.typesAsString(inferredMethod, false, true) };
        final String[] shortArguments = { new String(nonNullAnnotationName[nonNullAnnotationName.length - 1]), new String(nullableAnnotationName[nullableAnnotationName.length - 1]), new String(inferredMethod.returnType.nullAnnotatedReadableName(this.options, true)), new String(inferredMethod.selector), this.typesAsString(inferredMethod, true, true) };
        this.handle(isFunctionalExpression ? 67109837 : 536871878, arguments, shortArguments, sourceStart, sourceEnd);
    }
    
    public void contradictoryNullAnnotationsOnBounds(final Annotation annotation, final long previousTagBit) {
        final char[][] annotationName = (previousTagBit == 72057594037927936L) ? this.options.nonNullAnnotationName : this.options.nullableAnnotationName;
        final String[] arguments = { new String(CharOperation.concatWith(annotationName, '.')) };
        final String[] shortArguments = { new String(annotationName[annotationName.length - 1]) };
        this.handle(536871877, arguments, shortArguments, annotation.sourceStart, annotation.sourceEnd);
    }
    
    public void conflictingNullAnnotations(final MethodBinding currentMethod, final ASTNode location, final MethodBinding inheritedMethod) {
        final char[][] nonNullAnnotationName = this.options.nonNullAnnotationName;
        final char[][] nullableAnnotationName = this.options.nullableAnnotationName;
        final String[] arguments = { new String(CharOperation.concatWith(nonNullAnnotationName, '.')), new String(CharOperation.concatWith(nullableAnnotationName, '.')), new String(inheritedMethod.declaringClass.readableName()) };
        final String[] shortArguments = { new String(nonNullAnnotationName[nonNullAnnotationName.length - 1]), new String(nullableAnnotationName[nullableAnnotationName.length - 1]), new String(inheritedMethod.declaringClass.shortReadableName()) };
        this.handle(67109803, arguments, shortArguments, location.sourceStart, location.sourceEnd);
    }
    
    public void conflictingInheritedNullAnnotations(final ASTNode location, final boolean previousIsNonNull, final MethodBinding previousInherited, final boolean isNonNull, final MethodBinding inheritedMethod) {
        final char[][] previousAnnotationName = previousIsNonNull ? this.options.nonNullAnnotationName : this.options.nullableAnnotationName;
        final char[][] annotationName = isNonNull ? this.options.nonNullAnnotationName : this.options.nullableAnnotationName;
        final String[] arguments = { new String(CharOperation.concatWith(previousAnnotationName, '.')), new String(previousInherited.declaringClass.readableName()), new String(CharOperation.concatWith(annotationName, '.')), new String(inheritedMethod.declaringClass.readableName()) };
        final String[] shortArguments = { new String(previousAnnotationName[previousAnnotationName.length - 1]), new String(previousInherited.declaringClass.shortReadableName()), new String(annotationName[annotationName.length - 1]), new String(inheritedMethod.declaringClass.shortReadableName()) };
        this.handle(67109804, arguments, shortArguments, location.sourceStart, location.sourceEnd);
    }
    
    public void illegalAnnotationForBaseType(final TypeReference type, final Annotation[] annotations, final long nullAnnotationTagBit) {
        final int typeBit = (nullAnnotationTagBit == 36028797018963968L) ? 64 : 32;
        final char[][] annotationNames = (nullAnnotationTagBit == 72057594037927936L) ? this.options.nonNullAnnotationName : this.options.nullableAnnotationName;
        final String[] args = { new String(annotationNames[annotationNames.length - 1]), new String(type.getLastToken()) };
        final Annotation annotation = this.findAnnotation(annotations, typeBit);
        final int start = (annotation != null) ? annotation.sourceStart : type.sourceStart;
        final int end = (annotation != null) ? annotation.sourceEnd : type.sourceEnd;
        this.handle(16778139, args, args, start, end);
    }
    
    public void illegalAnnotationForBaseType(final Annotation annotation, final TypeBinding type) {
        final String[] args = { new String(annotation.resolvedType.shortReadableName()), new String(type.readableName()) };
        this.handle(16778139, args, args, annotation.sourceStart, annotation.sourceEnd);
    }
    
    private String annotatedTypeName(final TypeBinding type, final char[][] annotationName) {
        if ((type.tagBits & 0x180000000000000L) != 0x0L) {
            return String.valueOf(type.nullAnnotatedReadableName(this.options, false));
        }
        final int dims = 0;
        final char[] typeName = type.readableName();
        final char[] annotationDisplayName = CharOperation.concatWith(annotationName, '.');
        return this.internalAnnotatedTypeName(annotationDisplayName, typeName, dims);
    }
    
    private String shortAnnotatedTypeName(final TypeBinding type, final char[][] annotationName) {
        if ((type.tagBits & 0x180000000000000L) != 0x0L) {
            return String.valueOf(type.nullAnnotatedReadableName(this.options, true));
        }
        final int dims = 0;
        final char[] typeName = type.shortReadableName();
        final char[] annotationDisplayName = annotationName[annotationName.length - 1];
        return this.internalAnnotatedTypeName(annotationDisplayName, typeName, dims);
    }
    
    String internalAnnotatedTypeName(final char[] annotationName, final char[] typeName, final int dims) {
        char[] fullName;
        if (dims > 0) {
            final int plainLen = annotationName.length + typeName.length + 2;
            fullName = new char[plainLen + 2 * dims];
            System.arraycopy(typeName, 0, fullName, 0, typeName.length);
            fullName[typeName.length] = ' ';
            fullName[typeName.length + 1] = '@';
            System.arraycopy(annotationName, 0, fullName, typeName.length + 2, annotationName.length);
            for (int i = 0; i < dims; ++i) {
                fullName[plainLen + i] = '[';
                fullName[plainLen + i + 1] = ']';
            }
        }
        else {
            fullName = new char[annotationName.length + typeName.length + 2];
            fullName[0] = '@';
            System.arraycopy(annotationName, 0, fullName, 1, annotationName.length);
            fullName[annotationName.length + 1] = ' ';
            System.arraycopy(typeName, 0, fullName, annotationName.length + 2, typeName.length);
        }
        return String.valueOf(fullName);
    }
    
    private Annotation findAnnotation(final Annotation[] annotations, final int typeBit) {
        if (annotations != null) {
            for (int length = annotations.length, j = 0; j < length; ++j) {
                if (annotations[j].hasNullBit(typeBit)) {
                    return annotations[j];
                }
            }
        }
        return null;
    }
    
    public void missingNonNullByDefaultAnnotation(final TypeDeclaration type) {
        final CompilationUnitDeclaration compUnitDecl = type.getCompilationUnitDeclaration();
        if (compUnitDecl.currentPackage == null) {
            final int severity = this.computeSeverity(536871842);
            if (severity == 256) {
                return;
            }
            final TypeBinding binding = type.binding;
            this.handle(536871842, new String[] { new String(binding.readableName()) }, new String[] { new String(binding.shortReadableName()) }, severity, type.sourceStart, type.sourceEnd);
        }
        else {
            final int severity = this.computeSeverity(536871825);
            if (severity == 256) {
                return;
            }
            final String[] arguments = { CharOperation.toString(compUnitDecl.currentPackage.tokens) };
            this.handle(536871825, arguments, arguments, severity, compUnitDecl.currentPackage.sourceStart, compUnitDecl.currentPackage.sourceEnd);
        }
    }
    
    public void illegalModifiersForElidedType(final Argument argument) {
        final String[] arg = { new String(argument.name) };
        this.handle(536871913, arg, arg, argument.declarationSourceStart, argument.declarationSourceEnd);
    }
    
    public void illegalModifiers(final int modifierSourceStart, final int modifiersSourceEnd) {
        this.handle(536871914, ProblemReporter.NoArgument, ProblemReporter.NoArgument, modifierSourceStart, modifiersSourceEnd);
    }
    
    public void arrayReferencePotentialNullReference(final ArrayReference arrayReference) {
        this.handle(536871863, ProblemReporter.NoArgument, ProblemReporter.NoArgument, arrayReference.sourceStart, arrayReference.sourceEnd);
    }
    
    public void nullityMismatchingTypeAnnotation(final Expression expression, final TypeBinding providedType, final TypeBinding requiredType, final NullAnnotationMatching status) {
        if (providedType.id == 12 || status.nullStatus == 2) {
            this.nullityMismatchIsNull(expression, requiredType);
            return;
        }
        if (!status.isPotentiallyNullMismatch() || (requiredType.tagBits & 0x100000000000000L) == 0x0L || (providedType.tagBits & 0x80000000000000L) != 0x0L) {
            int problemId = 0;
            String superHint = null;
            String superHintShort = null;
            if (status.superTypeHint != null && requiredType.isParameterizedType()) {
                problemId = (status.isUnchecked() ? 536871868 : 536871866);
                superHint = status.superTypeHintName(this.options, false);
                superHintShort = status.superTypeHintName(this.options, true);
            }
            else {
                problemId = (status.isUnchecked() ? 536871867 : ((requiredType.isTypeVariable() && !requiredType.hasNullTypeAnnotations()) ? 970 : 536871865));
                if (problemId == 970) {
                    final String[] arguments = { null, null, new String(requiredType.sourceName()) };
                    final String[] shortArguments = { null, null, new String(requiredType.sourceName()) };
                }
                else {
                    final String[] arguments = new String[2];
                    final String[] array = new String[2];
                }
            }
            String requiredName;
            String requiredNameShort;
            if (problemId == 970) {
                requiredName = new String(requiredType.sourceName());
                requiredNameShort = new String(requiredType.sourceName());
            }
            else {
                requiredName = new String(requiredType.nullAnnotatedReadableName(this.options, false));
                requiredNameShort = new String(requiredType.nullAnnotatedReadableName(this.options, true));
            }
            final String providedName = String.valueOf(providedType.nullAnnotatedReadableName(this.options, false));
            final String providedNameShort = String.valueOf(providedType.nullAnnotatedReadableName(this.options, true));
            String[] arguments;
            String[] shortArguments;
            if (superHint != null) {
                arguments = new String[] { requiredName, providedName, superHint };
                shortArguments = new String[] { requiredNameShort, providedNameShort, superHintShort };
            }
            else {
                arguments = new String[] { requiredName, providedName };
                shortArguments = new String[] { requiredNameShort, providedNameShort };
            }
            this.handle(problemId, arguments, shortArguments, expression.sourceStart, expression.sourceEnd);
            return;
        }
        if (this.options.pessimisticNullAnalysisForFreeTypeVariablesEnabled && providedType.isTypeVariable() && !providedType.hasNullTypeAnnotations()) {
            this.nullityMismatchIsFreeTypeVariable(providedType, expression.sourceStart, expression.sourceEnd);
            return;
        }
        this.nullityMismatchPotentiallyNull(expression, requiredType, this.options.nonNullAnnotationName);
    }
    
    public void nullityMismatchTypeArgument(final TypeBinding typeVariable, final TypeBinding typeArgument, final ASTNode location) {
        final String[] arguments = { String.valueOf(typeVariable.nullAnnotatedReadableName(this.options, false)), String.valueOf(typeArgument.nullAnnotatedReadableName(this.options, false)) };
        final String[] shortArguments = { String.valueOf(typeVariable.nullAnnotatedReadableName(this.options, true)), String.valueOf(typeArgument.nullAnnotatedReadableName(this.options, true)) };
        this.handle(536871876, arguments, shortArguments, location.sourceStart, location.sourceEnd);
    }
    
    public void cannotRedefineTypeArgumentNullity(final TypeBinding typeVariable, final Binding superElement, final ASTNode location) {
        final String[] arguments = new String[2];
        final String[] shortArguments = new String[2];
        arguments[0] = String.valueOf(typeVariable.nullAnnotatedReadableName(this.options, false));
        shortArguments[0] = String.valueOf(typeVariable.nullAnnotatedReadableName(this.options, true));
        if (superElement instanceof MethodBinding) {
            final ReferenceBinding declaringClass = ((MethodBinding)superElement).declaringClass;
            arguments[1] = String.valueOf(CharOperation.concat(declaringClass.readableName(), superElement.shortReadableName(), '.'));
            shortArguments[1] = String.valueOf(CharOperation.concat(declaringClass.shortReadableName(), superElement.shortReadableName(), '.'));
        }
        else {
            arguments[1] = String.valueOf(superElement.readableName());
            shortArguments[1] = String.valueOf(superElement.shortReadableName());
        }
        this.handle(975, arguments, shortArguments, location.sourceStart, location.sourceEnd);
    }
    
    public void implicitObjectBoundNoNullDefault(final TypeReference reference) {
        this.handle(971, ProblemReporter.NoArgument, ProblemReporter.NoArgument, 0, reference.sourceStart, reference.sourceEnd);
    }
    
    public void nonNullTypeVariableInUnannotatedBinary(final LookupEnvironment environment, final MethodBinding method, final Expression expression, final int providedSeverity) {
        final TypeBinding declaredReturnType = method.original().returnType;
        int severity = this.computeSeverity(16778196);
        if ((severity & 0x501) == 0x0) {
            severity = providedSeverity;
        }
        if (declaredReturnType instanceof TypeVariableBinding) {
            final TypeVariableBinding typeVariable = (TypeVariableBinding)declaredReturnType;
            final TypeBinding declaringClass = method.declaringClass;
            final char[][] nonNullName = this.options.nonNullAnnotationName;
            final String shortNonNullName = String.valueOf(nonNullName[nonNullName.length - 1]);
            if (typeVariable.declaringElement instanceof ReferenceBinding) {
                final String[] arguments = { shortNonNullName, String.valueOf(declaringClass.nullAnnotatedReadableName(this.options, false)), String.valueOf(declaringClass.original().readableName()) };
                final String[] shortArguments = { shortNonNullName, String.valueOf(declaringClass.nullAnnotatedReadableName(this.options, true)), String.valueOf(declaringClass.original().shortReadableName()) };
                this.handle(16778196, arguments, shortArguments, severity, expression.sourceStart, expression.sourceEnd);
            }
            else if (typeVariable.declaringElement instanceof MethodBinding && method instanceof ParameterizedGenericMethodBinding) {
                final TypeBinding substitution = ((ParameterizedGenericMethodBinding)method).typeArguments[typeVariable.rank];
                final String[] arguments2 = { shortNonNullName, String.valueOf(typeVariable.readableName()), String.valueOf(substitution.nullAnnotatedReadableName(this.options, false)), String.valueOf(declaringClass.original().readableName()) };
                final String[] shortArguments2 = { shortNonNullName, String.valueOf(typeVariable.shortReadableName()), String.valueOf(substitution.nullAnnotatedReadableName(this.options, true)), String.valueOf(declaringClass.original().shortReadableName()) };
                this.handle(16778197, arguments2, shortArguments2, severity, expression.sourceStart, expression.sourceEnd);
            }
        }
    }
    
    public void dereferencingNullableExpression(final Expression expression) {
        if (expression instanceof MessageSend) {
            final MessageSend send = (MessageSend)expression;
            this.messageSendPotentialNullReference(send.binding, send);
            return;
        }
        final char[][] nullableName = this.options.nullableAnnotationName;
        final char[] nullableShort = nullableName[nullableName.length - 1];
        final String[] arguments = { String.valueOf(nullableShort) };
        final int start = this.nodeSourceStart(expression);
        final int end = this.nodeSourceEnd(expression);
        this.handle(536871864, arguments, arguments, start, end);
    }
    
    public void dereferencingNullableExpression(final long positions, final LookupEnvironment env) {
        final char[][] nullableName = env.getNullableAnnotationName();
        final char[] nullableShort = nullableName[nullableName.length - 1];
        final String[] arguments = { String.valueOf(nullableShort) };
        this.handle(536871864, arguments, arguments, (int)(positions >>> 32), (int)(positions & 0xFFFFL));
    }
    
    public void onlyReferenceTypesInIntersectionCast(final TypeReference typeReference) {
        this.handle(16778108, ProblemReporter.NoArgument, ProblemReporter.NoArgument, typeReference.sourceStart, typeReference.sourceEnd);
    }
    
    public void illegalArrayTypeInIntersectionCast(final TypeReference typeReference) {
        this.handle(16778109, ProblemReporter.NoArgument, ProblemReporter.NoArgument, typeReference.sourceStart, typeReference.sourceEnd);
    }
    
    public void intersectionCastNotBelow18(final TypeReference[] typeReferences) {
        final int length = typeReferences.length;
        this.handle(16778107, ProblemReporter.NoArgument, ProblemReporter.NoArgument, typeReferences[0].sourceStart, typeReferences[length - 1].sourceEnd);
    }
    
    public void duplicateBoundInIntersectionCast(final TypeReference typeReference) {
        this.handle(16778110, ProblemReporter.NoArgument, ProblemReporter.NoArgument, typeReference.sourceStart, typeReference.sourceEnd);
    }
    
    public void lambdaRedeclaresArgument(final Argument argument) {
        final String[] arguments = { new String(argument.name) };
        this.handle(536871009, arguments, arguments, argument.sourceStart, argument.sourceEnd);
    }
    
    public void lambdaRedeclaresLocal(final LocalDeclaration local) {
        final String[] arguments = { new String(local.name) };
        this.handle(536871010, arguments, arguments, local.sourceStart, local.sourceEnd);
    }
    
    public void descriptorHasInvisibleType(final FunctionalExpression expression, final ReferenceBinding referenceBinding) {
        this.handle(99, new String[] { new String(referenceBinding.readableName()) }, new String[] { new String(referenceBinding.shortReadableName()) }, expression.sourceStart, expression.diagnosticsSourceEnd());
    }
    
    public void methodReferenceSwingsBothWays(final ReferenceExpression expression, final MethodBinding instanceMethod, final MethodBinding nonInstanceMethod) {
        final char[] selector = instanceMethod.selector;
        final TypeBinding receiverType = instanceMethod.declaringClass;
        final StringBuffer buffer1 = new StringBuffer();
        final StringBuffer shortBuffer1 = new StringBuffer();
        TypeBinding[] parameters = instanceMethod.parameters;
        for (int i = 0, length = parameters.length; i < length; ++i) {
            if (i != 0) {
                buffer1.append(", ");
                shortBuffer1.append(", ");
            }
            buffer1.append(new String(parameters[i].readableName()));
            shortBuffer1.append(new String(parameters[i].shortReadableName()));
        }
        final StringBuffer buffer2 = new StringBuffer();
        final StringBuffer shortBuffer2 = new StringBuffer();
        parameters = nonInstanceMethod.parameters;
        for (int j = 0, length2 = parameters.length; j < length2; ++j) {
            if (j != 0) {
                buffer2.append(", ");
                shortBuffer2.append(", ");
            }
            buffer2.append(new String(parameters[j].readableName()));
            shortBuffer2.append(new String(parameters[j].shortReadableName()));
        }
        final int id = 603979899;
        this.handle(id, new String[] { new String(receiverType.readableName()), new String(selector), buffer1.toString(), new String(selector), buffer2.toString() }, new String[] { new String(receiverType.shortReadableName()), new String(selector), shortBuffer1.toString(), new String(selector), shortBuffer2.toString() }, expression.sourceStart, expression.sourceEnd);
    }
    
    public void methodMustBeAccessedStatically(final ReferenceExpression expression, final MethodBinding nonInstanceMethod) {
        final TypeBinding receiverType = nonInstanceMethod.declaringClass;
        final char[] selector = nonInstanceMethod.selector;
        final StringBuffer buffer = new StringBuffer();
        final StringBuffer shortBuffer = new StringBuffer();
        final TypeBinding[] parameters = nonInstanceMethod.parameters;
        for (int i = 0, length = parameters.length; i < length; ++i) {
            if (i != 0) {
                buffer.append(", ");
                shortBuffer.append(", ");
            }
            buffer.append(new String(parameters[i].readableName()));
            shortBuffer.append(new String(parameters[i].shortReadableName()));
        }
        final int id = 603979900;
        this.handle(id, new String[] { new String(receiverType.readableName()), new String(selector), buffer.toString() }, new String[] { new String(receiverType.shortReadableName()), new String(selector), shortBuffer.toString() }, expression.sourceStart, expression.sourceEnd);
    }
    
    public void methodMustBeAccessedWithInstance(final ReferenceExpression expression, final MethodBinding instanceMethod) {
        final TypeBinding receiverType = instanceMethod.declaringClass;
        final char[] selector = instanceMethod.selector;
        final StringBuffer buffer = new StringBuffer();
        final StringBuffer shortBuffer = new StringBuffer();
        final TypeBinding[] parameters = instanceMethod.parameters;
        for (int i = 0, length = parameters.length; i < length; ++i) {
            if (i != 0) {
                buffer.append(", ");
                shortBuffer.append(", ");
            }
            buffer.append(new String(parameters[i].readableName()));
            shortBuffer.append(new String(parameters[i].shortReadableName()));
        }
        final int id = 603979977;
        this.handle(id, new String[] { new String(receiverType.readableName()), new String(selector), buffer.toString() }, new String[] { new String(receiverType.shortReadableName()), new String(selector), shortBuffer.toString() }, expression.sourceStart, expression.sourceEnd);
    }
    
    public void invalidArrayConstructorReference(final ReferenceExpression expression, final TypeBinding lhsType, final TypeBinding[] parameters) {
        final StringBuffer buffer = new StringBuffer();
        final StringBuffer shortBuffer = new StringBuffer();
        for (int i = 0, length = parameters.length; i < length; ++i) {
            if (i != 0) {
                buffer.append(", ");
                shortBuffer.append(", ");
            }
            buffer.append(new String(parameters[i].readableName()));
            shortBuffer.append(new String(parameters[i].shortReadableName()));
        }
        final int id = 603979901;
        this.handle(id, new String[] { new String(lhsType.readableName()), buffer.toString() }, new String[] { new String(lhsType.shortReadableName()), shortBuffer.toString() }, expression.sourceStart, expression.sourceEnd);
    }
    
    public void constructedArrayIncompatible(final ReferenceExpression expression, final TypeBinding receiverType, final TypeBinding returnType) {
        this.handle(603979902, new String[] { new String(receiverType.readableName()), new String(returnType.readableName()) }, new String[] { new String(receiverType.shortReadableName()), new String(returnType.shortReadableName()) }, expression.sourceStart, expression.sourceEnd);
    }
    
    public void danglingReference(final ReferenceExpression expression, final TypeBinding receiverType, final char[] selector, final TypeBinding[] descriptorParameters) {
        final StringBuffer buffer = new StringBuffer();
        final StringBuffer shortBuffer = new StringBuffer();
        final TypeBinding[] parameters = descriptorParameters;
        for (int i = 0, length = parameters.length; i < length; ++i) {
            if (i != 0) {
                buffer.append(", ");
                shortBuffer.append(", ");
            }
            buffer.append(new String(parameters[i].readableName()));
            shortBuffer.append(new String(parameters[i].shortReadableName()));
        }
        final int id = 603979903;
        this.handle(id, new String[] { new String(receiverType.readableName()), new String(selector), buffer.toString() }, new String[] { new String(receiverType.shortReadableName()), new String(selector), shortBuffer.toString() }, expression.sourceStart, expression.sourceEnd);
    }
    
    public void unhandledException(final TypeBinding exceptionType, final ReferenceExpression location) {
        this.handle(16777384, new String[] { new String(exceptionType.readableName()) }, new String[] { new String(exceptionType.shortReadableName()) }, location.sourceStart, location.sourceEnd);
    }
    
    public void incompatibleReturnType(final ReferenceExpression expression, final MethodBinding method, final TypeBinding returnType) {
        if (method.isConstructor()) {
            this.handle(553648793, new String[] { new String(method.declaringClass.readableName()), new String(returnType.readableName()) }, new String[] { new String(method.declaringClass.shortReadableName()), new String(returnType.shortReadableName()) }, expression.sourceStart, expression.sourceEnd);
        }
        else {
            final StringBuffer buffer = new StringBuffer();
            final StringBuffer shortBuffer = new StringBuffer();
            final TypeBinding[] parameters = method.parameters;
            for (int i = 0, length = parameters.length; i < length; ++i) {
                if (i != 0) {
                    buffer.append(", ");
                    shortBuffer.append(", ");
                }
                buffer.append(new String(parameters[i].readableName()));
                shortBuffer.append(new String(parameters[i].shortReadableName()));
            }
            final String selector = new String(method.selector);
            this.handle(603979904, new String[] { selector, buffer.toString(), new String(method.declaringClass.readableName()), new String(method.returnType.readableName()), new String(returnType.readableName()) }, new String[] { selector, shortBuffer.toString(), new String(method.declaringClass.shortReadableName()), new String(method.returnType.shortReadableName()), new String(returnType.shortReadableName()) }, expression.sourceStart, expression.sourceEnd);
        }
    }
    
    public void illegalSuperAccess(final TypeBinding superType, final TypeBinding directSuperType, final ASTNode location) {
        if (directSuperType.problemId() == 29) {
            this.interfaceSuperInvocationNotBelow18((QualifiedSuperReference)location);
            return;
        }
        if (directSuperType.problemId() != 21) {
            this.needImplementation(location);
        }
        this.handle(16778270, new String[] { String.valueOf(superType.readableName()), String.valueOf(directSuperType.readableName()) }, new String[] { String.valueOf(superType.shortReadableName()), String.valueOf(directSuperType.shortReadableName()) }, location.sourceStart, location.sourceEnd);
    }
    
    public void illegalSuperCallBypassingOverride(final InvocationSite location, final MethodBinding targetMethod, final ReferenceBinding overrider) {
        this.handle(67109919, new String[] { String.valueOf(targetMethod.readableName()), String.valueOf(targetMethod.declaringClass.readableName()), String.valueOf(overrider.readableName()) }, new String[] { String.valueOf(targetMethod.shortReadableName()), String.valueOf(targetMethod.declaringClass.shortReadableName()), String.valueOf(overrider.shortReadableName()) }, location.sourceStart(), location.sourceEnd());
    }
    
    public void disallowedTargetForContainerAnnotation(final Annotation annotation, final TypeBinding containerAnnotationType) {
        this.handle(16778114, new String[] { new String(annotation.resolvedType.readableName()), new String(containerAnnotationType.readableName()) }, new String[] { new String(annotation.resolvedType.shortReadableName()), new String(containerAnnotationType.shortReadableName()) }, annotation.sourceStart, annotation.sourceEnd);
    }
    
    public void genericInferenceError(final String message, final InvocationSite invocationSite) {
        this.genericInferenceProblem(message, invocationSite, 1);
    }
    
    public void genericInferenceProblem(final String message, final InvocationSite invocationSite, final int severity) {
        final String[] args = { message };
        int start = 0;
        int end = 0;
        if (invocationSite != null) {
            start = invocationSite.sourceStart();
            end = invocationSite.sourceEnd();
        }
        this.handle(1100, args, args, severity | 0x200, start, end);
    }
    
    public void uninternedIdentityComparison(final EqualExpression expr, final TypeBinding lhs, final TypeBinding rhs, final CompilationUnitDeclaration unit) {
        final char[] lhsName = lhs.sourceName();
        final char[] rhsName = rhs.sourceName();
        if (CharOperation.equals(lhsName, "VoidTypeBinding".toCharArray()) || CharOperation.equals(lhsName, "NullTypeBinding".toCharArray()) || CharOperation.equals(lhsName, "ProblemReferenceBinding".toCharArray())) {
            return;
        }
        if (CharOperation.equals(rhsName, "VoidTypeBinding".toCharArray()) || CharOperation.equals(rhsName, "NullTypeBinding".toCharArray()) || CharOperation.equals(rhsName, "ProblemReferenceBinding".toCharArray())) {
            return;
        }
        final boolean[] validIdentityComparisonLines = unit.validIdentityComparisonLines;
        if (validIdentityComparisonLines != null) {
            final int problemStartPosition = expr.left.sourceStart;
            int[] lineEnds;
            final int lineNumber = (problemStartPosition >= 0) ? Util.getLineNumber(problemStartPosition, lineEnds = unit.compilationResult().getLineSeparatorPositions(), 0, lineEnds.length - 1) : 0;
            if (lineNumber <= validIdentityComparisonLines.length && validIdentityComparisonLines[lineNumber - 1]) {
                return;
            }
        }
        this.handle(1610613180, new String[] { new String(lhs.readableName()), new String(rhs.readableName()) }, new String[] { new String(lhs.shortReadableName()), new String(rhs.shortReadableName()) }, expr.sourceStart, expr.sourceEnd);
    }
    
    public void invalidTypeArguments(final TypeReference[] typeReference) {
        this.handle(83886666, ProblemReporter.NoArgument, ProblemReporter.NoArgument, typeReference[0].sourceStart, typeReference[typeReference.length - 1].sourceEnd);
    }
}
