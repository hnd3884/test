package org.eclipse.jdt.internal.compiler;

import org.eclipse.jdt.internal.compiler.ast.IntersectionCastTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ReferenceExpression;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.ast.WhileStatement;
import org.eclipse.jdt.internal.compiler.ast.UnionTypeReference;
import org.eclipse.jdt.internal.compiler.ast.UnaryExpression;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedThisReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedSuperReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.PrefixExpression;
import org.eclipse.jdt.internal.compiler.ast.PostfixExpression;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.OR_OR_Expression;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
import org.eclipse.jdt.internal.compiler.ast.StringLiteralConcatenation;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.LongLiteral;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
import org.eclipse.jdt.internal.compiler.ast.JavadocSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocSingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.JavadocQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocMessageSend;
import org.eclipse.jdt.internal.compiler.ast.JavadocImplicitTypeReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocFieldReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocArraySingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocArgumentExpression;
import org.eclipse.jdt.internal.compiler.ast.JavadocAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Javadoc;
import org.eclipse.jdt.internal.compiler.ast.IntLiteral;
import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.ForStatement;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.FloatLiteral;
import org.eclipse.jdt.internal.compiler.ast.FieldReference;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FalseLiteral;
import org.eclipse.jdt.internal.compiler.ast.ExtendedStringLiteral;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
import org.eclipse.jdt.internal.compiler.ast.EmptyStatement;
import org.eclipse.jdt.internal.compiler.ast.DoubleLiteral;
import org.eclipse.jdt.internal.compiler.ast.DoStatement;
import org.eclipse.jdt.internal.compiler.ast.ContinueStatement;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
import org.eclipse.jdt.internal.compiler.ast.CompoundAssignment;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Clinit;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.CharLiteral;
import org.eclipse.jdt.internal.compiler.ast.CastExpression;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.BreakStatement;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.AssertStatement;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AND_AND_Expression;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.core.compiler.IProblem;

public abstract class ASTVisitor
{
    public void acceptProblem(final IProblem problem) {
    }
    
    public void endVisit(final AllocationExpression allocationExpression, final BlockScope scope) {
    }
    
    public void endVisit(final AND_AND_Expression and_and_Expression, final BlockScope scope) {
    }
    
    public void endVisit(final AnnotationMethodDeclaration annotationTypeDeclaration, final ClassScope classScope) {
    }
    
    public void endVisit(final Argument argument, final BlockScope scope) {
    }
    
    public void endVisit(final Argument argument, final ClassScope scope) {
    }
    
    public void endVisit(final ArrayAllocationExpression arrayAllocationExpression, final BlockScope scope) {
    }
    
    public void endVisit(final ArrayInitializer arrayInitializer, final BlockScope scope) {
    }
    
    public void endVisit(final ArrayInitializer arrayInitializer, final ClassScope scope) {
    }
    
    public void endVisit(final ArrayQualifiedTypeReference arrayQualifiedTypeReference, final BlockScope scope) {
    }
    
    public void endVisit(final ArrayQualifiedTypeReference arrayQualifiedTypeReference, final ClassScope scope) {
    }
    
    public void endVisit(final ArrayReference arrayReference, final BlockScope scope) {
    }
    
    public void endVisit(final ArrayTypeReference arrayTypeReference, final BlockScope scope) {
    }
    
    public void endVisit(final ArrayTypeReference arrayTypeReference, final ClassScope scope) {
    }
    
    public void endVisit(final AssertStatement assertStatement, final BlockScope scope) {
    }
    
    public void endVisit(final Assignment assignment, final BlockScope scope) {
    }
    
    public void endVisit(final BinaryExpression binaryExpression, final BlockScope scope) {
    }
    
    public void endVisit(final Block block, final BlockScope scope) {
    }
    
    public void endVisit(final BreakStatement breakStatement, final BlockScope scope) {
    }
    
    public void endVisit(final CaseStatement caseStatement, final BlockScope scope) {
    }
    
    public void endVisit(final CastExpression castExpression, final BlockScope scope) {
    }
    
    public void endVisit(final CharLiteral charLiteral, final BlockScope scope) {
    }
    
    public void endVisit(final ClassLiteralAccess classLiteral, final BlockScope scope) {
    }
    
    public void endVisit(final Clinit clinit, final ClassScope scope) {
    }
    
    public void endVisit(final CompilationUnitDeclaration compilationUnitDeclaration, final CompilationUnitScope scope) {
    }
    
    public void endVisit(final CompoundAssignment compoundAssignment, final BlockScope scope) {
    }
    
    public void endVisit(final ConditionalExpression conditionalExpression, final BlockScope scope) {
    }
    
    public void endVisit(final ConstructorDeclaration constructorDeclaration, final ClassScope scope) {
    }
    
    public void endVisit(final ContinueStatement continueStatement, final BlockScope scope) {
    }
    
    public void endVisit(final DoStatement doStatement, final BlockScope scope) {
    }
    
    public void endVisit(final DoubleLiteral doubleLiteral, final BlockScope scope) {
    }
    
    public void endVisit(final EmptyStatement emptyStatement, final BlockScope scope) {
    }
    
    public void endVisit(final EqualExpression equalExpression, final BlockScope scope) {
    }
    
    public void endVisit(final ExplicitConstructorCall explicitConstructor, final BlockScope scope) {
    }
    
    public void endVisit(final ExtendedStringLiteral extendedStringLiteral, final BlockScope scope) {
    }
    
    public void endVisit(final FalseLiteral falseLiteral, final BlockScope scope) {
    }
    
    public void endVisit(final FieldDeclaration fieldDeclaration, final MethodScope scope) {
    }
    
    public void endVisit(final FieldReference fieldReference, final BlockScope scope) {
    }
    
    public void endVisit(final FieldReference fieldReference, final ClassScope scope) {
    }
    
    public void endVisit(final FloatLiteral floatLiteral, final BlockScope scope) {
    }
    
    public void endVisit(final ForeachStatement forStatement, final BlockScope scope) {
    }
    
    public void endVisit(final ForStatement forStatement, final BlockScope scope) {
    }
    
    public void endVisit(final IfStatement ifStatement, final BlockScope scope) {
    }
    
    public void endVisit(final ImportReference importRef, final CompilationUnitScope scope) {
    }
    
    public void endVisit(final Initializer initializer, final MethodScope scope) {
    }
    
    public void endVisit(final InstanceOfExpression instanceOfExpression, final BlockScope scope) {
    }
    
    public void endVisit(final IntLiteral intLiteral, final BlockScope scope) {
    }
    
    public void endVisit(final Javadoc javadoc, final BlockScope scope) {
    }
    
    public void endVisit(final Javadoc javadoc, final ClassScope scope) {
    }
    
    public void endVisit(final JavadocAllocationExpression expression, final BlockScope scope) {
    }
    
    public void endVisit(final JavadocAllocationExpression expression, final ClassScope scope) {
    }
    
    public void endVisit(final JavadocArgumentExpression expression, final BlockScope scope) {
    }
    
    public void endVisit(final JavadocArgumentExpression expression, final ClassScope scope) {
    }
    
    public void endVisit(final JavadocArrayQualifiedTypeReference typeRef, final BlockScope scope) {
    }
    
    public void endVisit(final JavadocArrayQualifiedTypeReference typeRef, final ClassScope scope) {
    }
    
    public void endVisit(final JavadocArraySingleTypeReference typeRef, final BlockScope scope) {
    }
    
    public void endVisit(final JavadocArraySingleTypeReference typeRef, final ClassScope scope) {
    }
    
    public void endVisit(final JavadocFieldReference fieldRef, final BlockScope scope) {
    }
    
    public void endVisit(final JavadocFieldReference fieldRef, final ClassScope scope) {
    }
    
    public void endVisit(final JavadocImplicitTypeReference implicitTypeReference, final BlockScope scope) {
    }
    
    public void endVisit(final JavadocImplicitTypeReference implicitTypeReference, final ClassScope scope) {
    }
    
    public void endVisit(final JavadocMessageSend messageSend, final BlockScope scope) {
    }
    
    public void endVisit(final JavadocMessageSend messageSend, final ClassScope scope) {
    }
    
    public void endVisit(final JavadocQualifiedTypeReference typeRef, final BlockScope scope) {
    }
    
    public void endVisit(final JavadocQualifiedTypeReference typeRef, final ClassScope scope) {
    }
    
    public void endVisit(final JavadocReturnStatement statement, final BlockScope scope) {
    }
    
    public void endVisit(final JavadocReturnStatement statement, final ClassScope scope) {
    }
    
    public void endVisit(final JavadocSingleNameReference argument, final BlockScope scope) {
    }
    
    public void endVisit(final JavadocSingleNameReference argument, final ClassScope scope) {
    }
    
    public void endVisit(final JavadocSingleTypeReference typeRef, final BlockScope scope) {
    }
    
    public void endVisit(final JavadocSingleTypeReference typeRef, final ClassScope scope) {
    }
    
    public void endVisit(final LabeledStatement labeledStatement, final BlockScope scope) {
    }
    
    public void endVisit(final LocalDeclaration localDeclaration, final BlockScope scope) {
    }
    
    public void endVisit(final LongLiteral longLiteral, final BlockScope scope) {
    }
    
    public void endVisit(final MarkerAnnotation annotation, final BlockScope scope) {
    }
    
    public void endVisit(final MarkerAnnotation annotation, final ClassScope scope) {
    }
    
    public void endVisit(final MemberValuePair pair, final BlockScope scope) {
    }
    
    public void endVisit(final MemberValuePair pair, final ClassScope scope) {
    }
    
    public void endVisit(final MessageSend messageSend, final BlockScope scope) {
    }
    
    public void endVisit(final MethodDeclaration methodDeclaration, final ClassScope scope) {
    }
    
    public void endVisit(final StringLiteralConcatenation literal, final BlockScope scope) {
    }
    
    public void endVisit(final NormalAnnotation annotation, final BlockScope scope) {
    }
    
    public void endVisit(final NormalAnnotation annotation, final ClassScope scope) {
    }
    
    public void endVisit(final NullLiteral nullLiteral, final BlockScope scope) {
    }
    
    public void endVisit(final OR_OR_Expression or_or_Expression, final BlockScope scope) {
    }
    
    public void endVisit(final ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference, final BlockScope scope) {
    }
    
    public void endVisit(final ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference, final ClassScope scope) {
    }
    
    public void endVisit(final ParameterizedSingleTypeReference parameterizedSingleTypeReference, final BlockScope scope) {
    }
    
    public void endVisit(final ParameterizedSingleTypeReference parameterizedSingleTypeReference, final ClassScope scope) {
    }
    
    public void endVisit(final PostfixExpression postfixExpression, final BlockScope scope) {
    }
    
    public void endVisit(final PrefixExpression prefixExpression, final BlockScope scope) {
    }
    
    public void endVisit(final QualifiedAllocationExpression qualifiedAllocationExpression, final BlockScope scope) {
    }
    
    public void endVisit(final QualifiedNameReference qualifiedNameReference, final BlockScope scope) {
    }
    
    public void endVisit(final QualifiedNameReference qualifiedNameReference, final ClassScope scope) {
    }
    
    public void endVisit(final QualifiedSuperReference qualifiedSuperReference, final BlockScope scope) {
    }
    
    public void endVisit(final QualifiedSuperReference qualifiedSuperReference, final ClassScope scope) {
    }
    
    public void endVisit(final QualifiedThisReference qualifiedThisReference, final BlockScope scope) {
    }
    
    public void endVisit(final QualifiedThisReference qualifiedThisReference, final ClassScope scope) {
    }
    
    public void endVisit(final QualifiedTypeReference qualifiedTypeReference, final BlockScope scope) {
    }
    
    public void endVisit(final QualifiedTypeReference qualifiedTypeReference, final ClassScope scope) {
    }
    
    public void endVisit(final ReturnStatement returnStatement, final BlockScope scope) {
    }
    
    public void endVisit(final SingleMemberAnnotation annotation, final BlockScope scope) {
    }
    
    public void endVisit(final SingleMemberAnnotation annotation, final ClassScope scope) {
    }
    
    public void endVisit(final SingleNameReference singleNameReference, final BlockScope scope) {
    }
    
    public void endVisit(final SingleNameReference singleNameReference, final ClassScope scope) {
    }
    
    public void endVisit(final SingleTypeReference singleTypeReference, final BlockScope scope) {
    }
    
    public void endVisit(final SingleTypeReference singleTypeReference, final ClassScope scope) {
    }
    
    public void endVisit(final StringLiteral stringLiteral, final BlockScope scope) {
    }
    
    public void endVisit(final SuperReference superReference, final BlockScope scope) {
    }
    
    public void endVisit(final SwitchStatement switchStatement, final BlockScope scope) {
    }
    
    public void endVisit(final SynchronizedStatement synchronizedStatement, final BlockScope scope) {
    }
    
    public void endVisit(final ThisReference thisReference, final BlockScope scope) {
    }
    
    public void endVisit(final ThisReference thisReference, final ClassScope scope) {
    }
    
    public void endVisit(final ThrowStatement throwStatement, final BlockScope scope) {
    }
    
    public void endVisit(final TrueLiteral trueLiteral, final BlockScope scope) {
    }
    
    public void endVisit(final TryStatement tryStatement, final BlockScope scope) {
    }
    
    public void endVisit(final TypeDeclaration localTypeDeclaration, final BlockScope scope) {
    }
    
    public void endVisit(final TypeDeclaration memberTypeDeclaration, final ClassScope scope) {
    }
    
    public void endVisit(final TypeDeclaration typeDeclaration, final CompilationUnitScope scope) {
    }
    
    public void endVisit(final TypeParameter typeParameter, final BlockScope scope) {
    }
    
    public void endVisit(final TypeParameter typeParameter, final ClassScope scope) {
    }
    
    public void endVisit(final UnaryExpression unaryExpression, final BlockScope scope) {
    }
    
    public void endVisit(final UnionTypeReference unionTypeReference, final BlockScope scope) {
    }
    
    public void endVisit(final UnionTypeReference unionTypeReference, final ClassScope scope) {
    }
    
    public void endVisit(final WhileStatement whileStatement, final BlockScope scope) {
    }
    
    public void endVisit(final Wildcard wildcard, final BlockScope scope) {
    }
    
    public void endVisit(final Wildcard wildcard, final ClassScope scope) {
    }
    
    public void endVisit(final LambdaExpression lambdaExpression, final BlockScope blockScope) {
    }
    
    public void endVisit(final ReferenceExpression referenceExpression, final BlockScope blockScope) {
    }
    
    public void endVisit(final IntersectionCastTypeReference intersectionCastTypeReference, final ClassScope scope) {
    }
    
    public void endVisit(final IntersectionCastTypeReference intersectionCastTypeReference, final BlockScope scope) {
    }
    
    public boolean visit(final AllocationExpression allocationExpression, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final AND_AND_Expression and_and_Expression, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final AnnotationMethodDeclaration annotationTypeDeclaration, final ClassScope classScope) {
        return true;
    }
    
    public boolean visit(final Argument argument, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final Argument argument, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final ArrayAllocationExpression arrayAllocationExpression, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final ArrayInitializer arrayInitializer, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final ArrayInitializer arrayInitializer, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final ArrayQualifiedTypeReference arrayQualifiedTypeReference, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final ArrayQualifiedTypeReference arrayQualifiedTypeReference, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final ArrayReference arrayReference, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final ArrayTypeReference arrayTypeReference, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final ArrayTypeReference arrayTypeReference, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final AssertStatement assertStatement, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final Assignment assignment, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final BinaryExpression binaryExpression, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final Block block, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final BreakStatement breakStatement, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final CaseStatement caseStatement, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final CastExpression castExpression, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final CharLiteral charLiteral, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final ClassLiteralAccess classLiteral, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final Clinit clinit, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final CompilationUnitDeclaration compilationUnitDeclaration, final CompilationUnitScope scope) {
        return true;
    }
    
    public boolean visit(final CompoundAssignment compoundAssignment, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final ConditionalExpression conditionalExpression, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final ConstructorDeclaration constructorDeclaration, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final ContinueStatement continueStatement, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final DoStatement doStatement, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final DoubleLiteral doubleLiteral, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final EmptyStatement emptyStatement, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final EqualExpression equalExpression, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final ExplicitConstructorCall explicitConstructor, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final ExtendedStringLiteral extendedStringLiteral, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final FalseLiteral falseLiteral, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final FieldDeclaration fieldDeclaration, final MethodScope scope) {
        return true;
    }
    
    public boolean visit(final FieldReference fieldReference, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final FieldReference fieldReference, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final FloatLiteral floatLiteral, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final ForeachStatement forStatement, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final ForStatement forStatement, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final IfStatement ifStatement, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final ImportReference importRef, final CompilationUnitScope scope) {
        return true;
    }
    
    public boolean visit(final Initializer initializer, final MethodScope scope) {
        return true;
    }
    
    public boolean visit(final InstanceOfExpression instanceOfExpression, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final IntLiteral intLiteral, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final Javadoc javadoc, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final Javadoc javadoc, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final JavadocAllocationExpression expression, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final JavadocAllocationExpression expression, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final JavadocArgumentExpression expression, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final JavadocArgumentExpression expression, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final JavadocArrayQualifiedTypeReference typeRef, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final JavadocArrayQualifiedTypeReference typeRef, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final JavadocArraySingleTypeReference typeRef, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final JavadocArraySingleTypeReference typeRef, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final JavadocFieldReference fieldRef, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final JavadocFieldReference fieldRef, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final JavadocImplicitTypeReference implicitTypeReference, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final JavadocImplicitTypeReference implicitTypeReference, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final JavadocMessageSend messageSend, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final JavadocMessageSend messageSend, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final JavadocQualifiedTypeReference typeRef, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final JavadocQualifiedTypeReference typeRef, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final JavadocReturnStatement statement, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final JavadocReturnStatement statement, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final JavadocSingleNameReference argument, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final JavadocSingleNameReference argument, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final JavadocSingleTypeReference typeRef, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final JavadocSingleTypeReference typeRef, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final LabeledStatement labeledStatement, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final LocalDeclaration localDeclaration, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final LongLiteral longLiteral, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final MarkerAnnotation annotation, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final MarkerAnnotation annotation, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final MemberValuePair pair, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final MemberValuePair pair, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final MessageSend messageSend, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final MethodDeclaration methodDeclaration, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final StringLiteralConcatenation literal, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final NormalAnnotation annotation, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final NormalAnnotation annotation, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final NullLiteral nullLiteral, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final OR_OR_Expression or_or_Expression, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final ParameterizedSingleTypeReference parameterizedSingleTypeReference, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final ParameterizedSingleTypeReference parameterizedSingleTypeReference, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final PostfixExpression postfixExpression, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final PrefixExpression prefixExpression, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final QualifiedAllocationExpression qualifiedAllocationExpression, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final QualifiedNameReference qualifiedNameReference, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final QualifiedNameReference qualifiedNameReference, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final QualifiedSuperReference qualifiedSuperReference, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final QualifiedSuperReference qualifiedSuperReference, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final QualifiedThisReference qualifiedThisReference, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final QualifiedThisReference qualifiedThisReference, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final QualifiedTypeReference qualifiedTypeReference, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final QualifiedTypeReference qualifiedTypeReference, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final ReturnStatement returnStatement, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final SingleMemberAnnotation annotation, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final SingleMemberAnnotation annotation, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final SingleNameReference singleNameReference, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final SingleNameReference singleNameReference, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final SingleTypeReference singleTypeReference, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final SingleTypeReference singleTypeReference, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final StringLiteral stringLiteral, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final SuperReference superReference, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final SwitchStatement switchStatement, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final SynchronizedStatement synchronizedStatement, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final ThisReference thisReference, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final ThisReference thisReference, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final ThrowStatement throwStatement, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final TrueLiteral trueLiteral, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final TryStatement tryStatement, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final TypeDeclaration localTypeDeclaration, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final TypeDeclaration memberTypeDeclaration, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final TypeDeclaration typeDeclaration, final CompilationUnitScope scope) {
        return true;
    }
    
    public boolean visit(final TypeParameter typeParameter, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final TypeParameter typeParameter, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final UnaryExpression unaryExpression, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final UnionTypeReference unionTypeReference, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final UnionTypeReference unionTypeReference, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final WhileStatement whileStatement, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final Wildcard wildcard, final BlockScope scope) {
        return true;
    }
    
    public boolean visit(final Wildcard wildcard, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final LambdaExpression lambdaExpression, final BlockScope blockScope) {
        return true;
    }
    
    public boolean visit(final ReferenceExpression referenceExpression, final BlockScope blockScope) {
        return true;
    }
    
    public boolean visit(final IntersectionCastTypeReference intersectionCastTypeReference, final ClassScope scope) {
        return true;
    }
    
    public boolean visit(final IntersectionCastTypeReference intersectionCastTypeReference, final BlockScope scope) {
        return true;
    }
}
