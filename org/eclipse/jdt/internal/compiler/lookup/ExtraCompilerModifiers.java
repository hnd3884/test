package org.eclipse.jdt.internal.compiler.lookup;

public interface ExtraCompilerModifiers
{
    public static final int AccJustFlag = 65535;
    public static final int AccDefaultMethod = 65536;
    public static final int AccRestrictedAccess = 262144;
    public static final int AccFromClassFile = 524288;
    public static final int AccDefaultAbstract = 524288;
    public static final int AccDeprecatedImplicitly = 2097152;
    public static final int AccAlternateModifierProblem = 4194304;
    public static final int AccModifierProblem = 8388608;
    public static final int AccSemicolonBody = 16777216;
    public static final int AccUnresolved = 33554432;
    public static final int AccBlankFinal = 67108864;
    public static final int AccIsDefaultConstructor = 67108864;
    public static final int AccLocallyUsed = 134217728;
    public static final int AccVisibilityMASK = 7;
    public static final int AccOverriding = 268435456;
    public static final int AccImplementing = 536870912;
    public static final int AccGenericSignature = 1073741824;
}
