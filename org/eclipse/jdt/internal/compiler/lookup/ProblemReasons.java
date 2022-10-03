package org.eclipse.jdt.internal.compiler.lookup;

public interface ProblemReasons
{
    public static final int NoError = 0;
    public static final int NotFound = 1;
    public static final int NotVisible = 2;
    public static final int Ambiguous = 3;
    public static final int InternalNameProvided = 4;
    public static final int InheritedNameHidesEnclosingName = 5;
    public static final int NonStaticReferenceInConstructorInvocation = 6;
    public static final int NonStaticReferenceInStaticContext = 7;
    public static final int ReceiverTypeNotVisible = 8;
    public static final int IllegalSuperTypeVariable = 9;
    public static final int ParameterBoundMismatch = 10;
    public static final int TypeParameterArityMismatch = 11;
    public static final int ParameterizedMethodTypeMismatch = 12;
    public static final int TypeArgumentsForRawGenericMethod = 13;
    public static final int InvalidTypeForStaticImport = 14;
    public static final int InvalidTypeForAutoManagedResource = 15;
    public static final int VarargsElementTypeNotVisible = 16;
    public static final int NoSuchSingleAbstractMethod = 17;
    public static final int NotAWellFormedParameterizedType = 18;
    public static final int NonStaticOrAlienTypeReceiver = 20;
    public static final int AttemptToBypassDirectSuper = 21;
    public static final int DefectiveContainerAnnotationType = 22;
    public static final int InvocationTypeInferenceFailure = 23;
    public static final int ApplicableMethodOverriddenByInapplicable = 24;
    public static final int ContradictoryNullAnnotations = 25;
    public static final int NoSuchMethodOnArray = 26;
    public static final int InferredApplicableMethodInapplicable = 27;
    public static final int NoProperEnclosingInstance = 28;
    public static final int InterfaceMethodInvocationNotBelow18 = 29;
}
