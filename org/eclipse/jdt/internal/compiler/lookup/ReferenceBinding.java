package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import java.util.Arrays;
import org.eclipse.jdt.core.compiler.CharOperation;
import java.util.Comparator;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;

public abstract class ReferenceBinding extends TypeBinding
{
    public char[][] compoundName;
    public char[] sourceName;
    public int modifiers;
    public PackageBinding fPackage;
    char[] fileName;
    char[] constantPoolName;
    char[] signature;
    private SimpleLookupTable compatibleCache;
    int typeBits;
    protected MethodBinding[] singleAbstractMethod;
    public static final ReferenceBinding LUB_GENERIC;
    private static final Comparator<FieldBinding> FIELD_COMPARATOR;
    private static final Comparator<MethodBinding> METHOD_COMPARATOR;
    protected static ProblemMethodBinding samProblemBinding;
    
    static {
        LUB_GENERIC = new ReferenceBinding() {
            {
                this.id = 0;
            }
            
            @Override
            public boolean hasTypeBit(final int bit) {
                return false;
            }
        };
        FIELD_COMPARATOR = new Comparator<FieldBinding>() {
            @Override
            public int compare(final FieldBinding o1, final FieldBinding o2) {
                final char[] n1 = o1.name;
                final char[] n2 = o2.name;
                return ReferenceBinding.compare(n1, n2, n1.length, n2.length);
            }
        };
        METHOD_COMPARATOR = new Comparator<MethodBinding>() {
            @Override
            public int compare(final MethodBinding o1, final MethodBinding o2) {
                final MethodBinding m1 = o1;
                final MethodBinding m2 = o2;
                final char[] s1 = m1.selector;
                final char[] s2 = m2.selector;
                final int c = ReferenceBinding.compare(s1, s2, s1.length, s2.length);
                return (c == 0) ? (m1.parameters.length - m2.parameters.length) : c;
            }
        };
        ReferenceBinding.samProblemBinding = new ProblemMethodBinding(TypeConstants.ANONYMOUS_METHOD, null, 17);
    }
    
    public ReferenceBinding(final ReferenceBinding prototype) {
        super(prototype);
        this.compoundName = prototype.compoundName;
        this.sourceName = prototype.sourceName;
        this.modifiers = prototype.modifiers;
        this.fPackage = prototype.fPackage;
        this.fileName = prototype.fileName;
        this.constantPoolName = prototype.constantPoolName;
        this.signature = prototype.signature;
        this.compatibleCache = prototype.compatibleCache;
        this.typeBits = prototype.typeBits;
        this.singleAbstractMethod = prototype.singleAbstractMethod;
    }
    
    public ReferenceBinding() {
    }
    
    public static FieldBinding binarySearch(final char[] name, final FieldBinding[] sortedFields) {
        if (sortedFields == null) {
            return null;
        }
        final int max = sortedFields.length;
        if (max == 0) {
            return null;
        }
        int left = 0;
        int right = max - 1;
        final int nameLength = name.length;
        int mid = 0;
        while (left <= right) {
            mid = left + (right - left) / 2;
            final char[] midName;
            final int compare = compare(name, midName = sortedFields[mid].name, nameLength, midName.length);
            if (compare < 0) {
                right = mid - 1;
            }
            else {
                if (compare <= 0) {
                    return sortedFields[mid];
                }
                left = mid + 1;
            }
        }
        return null;
    }
    
    public static long binarySearch(final char[] selector, final MethodBinding[] sortedMethods) {
        if (sortedMethods == null) {
            return -1L;
        }
        final int max = sortedMethods.length;
        if (max == 0) {
            return -1L;
        }
        int left = 0;
        int right = max - 1;
        final int selectorLength = selector.length;
        int mid = 0;
        while (left <= right) {
            mid = left + (right - left) / 2;
            final char[] midSelector;
            final int compare = compare(selector, midSelector = sortedMethods[mid].selector, selectorLength, midSelector.length);
            if (compare < 0) {
                right = mid - 1;
            }
            else {
                if (compare <= 0) {
                    int start = mid;
                    int end = mid;
                    while (start > left) {
                        if (!CharOperation.equals(sortedMethods[start - 1].selector, selector)) {
                            break;
                        }
                        --start;
                    }
                    while (end < right && CharOperation.equals(sortedMethods[end + 1].selector, selector)) {
                        ++end;
                    }
                    return start + ((long)end << 32);
                }
                left = mid + 1;
            }
        }
        return -1L;
    }
    
    static int compare(final char[] str1, final char[] str2, final int len1, final int len2) {
        int n = Math.min(len1, len2);
        int i = 0;
        while (n-- != 0) {
            final char c1 = str1[i];
            final char c2 = str2[i++];
            if (c1 != c2) {
                return c1 - c2;
            }
        }
        return len1 - len2;
    }
    
    public static void sortFields(final FieldBinding[] sortedFields, final int left, final int right) {
        Arrays.sort(sortedFields, left, right, ReferenceBinding.FIELD_COMPARATOR);
    }
    
    public static void sortMethods(final MethodBinding[] sortedMethods, final int left, final int right) {
        Arrays.sort(sortedMethods, left, right, ReferenceBinding.METHOD_COMPARATOR);
    }
    
    public FieldBinding[] availableFields() {
        return this.fields();
    }
    
    public MethodBinding[] availableMethods() {
        return this.methods();
    }
    
    @Override
    public boolean canBeInstantiated() {
        return (this.modifiers & 0x6600) == 0x0;
    }
    
    public boolean canBeSeenBy(final PackageBinding invocationPackage) {
        return this.isPublic() || (!this.isPrivate() && invocationPackage == this.fPackage);
    }
    
    public boolean canBeSeenBy(final ReferenceBinding receiverType, final ReferenceBinding invocationType) {
        if (this.isPublic()) {
            return true;
        }
        if (TypeBinding.equalsEquals(invocationType, this) && TypeBinding.equalsEquals(invocationType, receiverType)) {
            return true;
        }
        if (this.isProtected()) {
            if (TypeBinding.equalsEquals(invocationType, this)) {
                return true;
            }
            if (invocationType.fPackage == this.fPackage) {
                return true;
            }
            TypeBinding currentType = invocationType.erasure();
            final TypeBinding declaringClass = this.enclosingType().erasure();
            if (TypeBinding.equalsEquals(declaringClass, invocationType)) {
                return true;
            }
            if (declaringClass == null) {
                return false;
            }
            while (currentType.findSuperTypeOriginatingFrom(declaringClass) == null) {
                currentType = currentType.enclosingType();
                if (currentType == null) {
                    return false;
                }
            }
            return true;
        }
        else {
            if (this.isPrivate()) {
                Label_0196: {
                    if (!TypeBinding.equalsEquals(receiverType, this) && !TypeBinding.equalsEquals(receiverType, this.enclosingType())) {
                        if (receiverType.isTypeVariable()) {
                            final TypeVariableBinding typeVariable = (TypeVariableBinding)receiverType;
                            if (typeVariable.environment.globalOptions.complianceLevel <= 3276800L) {
                                if (typeVariable.isErasureBoundTo(this.erasure())) {
                                    break Label_0196;
                                }
                                if (typeVariable.isErasureBoundTo(this.enclosingType().erasure())) {
                                    break Label_0196;
                                }
                            }
                        }
                        return false;
                    }
                }
                if (TypeBinding.notEquals(invocationType, this)) {
                    ReferenceBinding outerInvocationType = invocationType;
                    for (ReferenceBinding temp = outerInvocationType.enclosingType(); temp != null; temp = temp.enclosingType()) {
                        outerInvocationType = temp;
                    }
                    ReferenceBinding outerDeclaringClass = (ReferenceBinding)this.erasure();
                    for (ReferenceBinding temp = outerDeclaringClass.enclosingType(); temp != null; temp = temp.enclosingType()) {
                        outerDeclaringClass = temp;
                    }
                    if (TypeBinding.notEquals(outerInvocationType, outerDeclaringClass)) {
                        return false;
                    }
                }
                return true;
            }
            if (invocationType.fPackage != this.fPackage) {
                return false;
            }
            ReferenceBinding currentType2 = receiverType;
            final TypeBinding originalDeclaringClass = ((this.enclosingType() == null) ? this : this.enclosingType()).original();
            do {
                if (currentType2.isCapture()) {
                    if (TypeBinding.equalsEquals(originalDeclaringClass, currentType2.erasure().original())) {
                        return true;
                    }
                }
                else if (TypeBinding.equalsEquals(originalDeclaringClass, currentType2.original())) {
                    return true;
                }
                final PackageBinding currentPackage = currentType2.fPackage;
                if (currentPackage != null && currentPackage != this.fPackage) {
                    return false;
                }
            } while ((currentType2 = currentType2.superclass()) != null);
            return false;
        }
    }
    
    @Override
    public boolean canBeSeenBy(final Scope scope) {
        if (this.isPublic()) {
            return true;
        }
        final SourceTypeBinding invocationType = scope.enclosingSourceType();
        if (TypeBinding.equalsEquals(invocationType, this)) {
            return true;
        }
        if (invocationType == null) {
            return !this.isPrivate() && scope.getCurrentPackage() == this.fPackage;
        }
        if (this.isProtected()) {
            if (invocationType.fPackage == this.fPackage) {
                return true;
            }
            TypeBinding declaringClass = this.enclosingType();
            if (declaringClass == null) {
                return false;
            }
            declaringClass = declaringClass.erasure();
            TypeBinding currentType = invocationType.erasure();
            while (!TypeBinding.equalsEquals(declaringClass, invocationType)) {
                if (currentType.findSuperTypeOriginatingFrom(declaringClass) != null) {
                    return true;
                }
                currentType = currentType.enclosingType();
                if (currentType == null) {
                    return false;
                }
            }
            return true;
        }
        else {
            if (this.isPrivate()) {
                ReferenceBinding outerInvocationType = invocationType;
                for (ReferenceBinding temp = outerInvocationType.enclosingType(); temp != null; temp = temp.enclosingType()) {
                    outerInvocationType = temp;
                }
                ReferenceBinding outerDeclaringClass = (ReferenceBinding)this.erasure();
                for (ReferenceBinding temp = outerDeclaringClass.enclosingType(); temp != null; temp = temp.enclosingType()) {
                    outerDeclaringClass = temp;
                }
                return TypeBinding.equalsEquals(outerInvocationType, outerDeclaringClass);
            }
            return invocationType.fPackage == this.fPackage;
        }
    }
    
    public char[] computeGenericTypeSignature(final TypeVariableBinding[] typeVariables) {
        final boolean isMemberOfGeneric = this.isMemberType() && (this.enclosingType().modifiers & 0x40000000) != 0x0;
        if (typeVariables == Binding.NO_TYPE_VARIABLES && !isMemberOfGeneric) {
            return this.signature();
        }
        final StringBuffer sig = new StringBuffer(10);
        if (isMemberOfGeneric) {
            final char[] typeSig = this.enclosingType().genericTypeSignature();
            sig.append(typeSig, 0, typeSig.length - 1);
            sig.append('.');
            sig.append(this.sourceName);
        }
        else {
            final char[] typeSig = this.signature();
            sig.append(typeSig, 0, typeSig.length - 1);
        }
        if (typeVariables == Binding.NO_TYPE_VARIABLES) {
            sig.append(';');
        }
        else {
            sig.append('<');
            for (int i = 0, length = typeVariables.length; i < length; ++i) {
                sig.append(typeVariables[i].genericTypeSignature());
            }
            sig.append(">;");
        }
        final int sigLength = sig.length();
        final char[] result = new char[sigLength];
        sig.getChars(0, sigLength, result, 0);
        return result;
    }
    
    public void computeId() {
        Label_2829: {
            switch (this.compoundName.length) {
                case 3: {
                    char[] packageName = this.compoundName[0];
                    switch (packageName.length) {
                        case 3: {
                            if (CharOperation.equals(TypeConstants.ORG_JUNIT_ASSERT, this.compoundName)) {
                                this.id = 70;
                            }
                            return;
                        }
                        case 4: {
                            if (!CharOperation.equals(TypeConstants.JAVA, packageName)) {
                                return;
                            }
                            packageName = this.compoundName[1];
                            if (packageName.length == 0) {
                                return;
                            }
                            final char[] typeName = this.compoundName[2];
                            if (typeName.length == 0) {
                                return;
                            }
                            if (!CharOperation.equals(TypeConstants.LANG, this.compoundName[1])) {
                                switch (packageName[0]) {
                                    case 'i': {
                                        if (CharOperation.equals(packageName, TypeConstants.IO)) {
                                            switch (typeName[0]) {
                                                case 'C': {
                                                    if (CharOperation.equals(typeName, TypeConstants.JAVA_IO_CLOSEABLE[2])) {
                                                        this.typeBits |= 0x2;
                                                    }
                                                    return;
                                                }
                                                case 'E': {
                                                    if (CharOperation.equals(typeName, TypeConstants.JAVA_IO_EXTERNALIZABLE[2])) {
                                                        this.id = 56;
                                                    }
                                                    return;
                                                }
                                                case 'I': {
                                                    if (CharOperation.equals(typeName, TypeConstants.JAVA_IO_IOEXCEPTION[2])) {
                                                        this.id = 58;
                                                    }
                                                    return;
                                                }
                                                case 'O': {
                                                    if (CharOperation.equals(typeName, TypeConstants.JAVA_IO_OBJECTSTREAMEXCEPTION[2])) {
                                                        this.id = 57;
                                                    }
                                                    return;
                                                }
                                                case 'P': {
                                                    if (CharOperation.equals(typeName, TypeConstants.JAVA_IO_PRINTSTREAM[2])) {
                                                        this.id = 53;
                                                    }
                                                    return;
                                                }
                                                case 'S': {
                                                    if (CharOperation.equals(typeName, TypeConstants.JAVA_IO_SERIALIZABLE[2])) {
                                                        this.id = 37;
                                                    }
                                                }
                                            }
                                        }
                                        return;
                                    }
                                    case 'u': {
                                        if (CharOperation.equals(packageName, TypeConstants.UTIL)) {
                                            switch (typeName[0]) {
                                                case 'C': {
                                                    if (CharOperation.equals(typeName, TypeConstants.JAVA_UTIL_COLLECTION[2])) {
                                                        this.id = 59;
                                                    }
                                                    return;
                                                }
                                                case 'I': {
                                                    if (CharOperation.equals(typeName, TypeConstants.JAVA_UTIL_ITERATOR[2])) {
                                                        this.id = 39;
                                                    }
                                                    return;
                                                }
                                                case 'O': {
                                                    if (CharOperation.equals(typeName, TypeConstants.JAVA_UTIL_OBJECTS[2])) {
                                                        this.id = 74;
                                                    }
                                                }
                                            }
                                        }
                                        return;
                                    }
                                    default: {
                                        return;
                                    }
                                }
                            }
                            else {
                                switch (typeName[0]) {
                                    case 'A': {
                                        switch (typeName.length) {
                                            case 13: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_AUTOCLOSEABLE[2])) {
                                                    this.id = 62;
                                                    this.typeBits |= 0x1;
                                                }
                                                return;
                                            }
                                            case 14: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ASSERTIONERROR[2])) {
                                                    this.id = 35;
                                                }
                                                return;
                                            }
                                            default: {
                                                return;
                                            }
                                        }
                                        break;
                                    }
                                    case 'B': {
                                        switch (typeName.length) {
                                            case 4: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_BYTE[2])) {
                                                    this.id = 26;
                                                }
                                                return;
                                            }
                                            case 7: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_BOOLEAN[2])) {
                                                    this.id = 33;
                                                }
                                                return;
                                            }
                                            default: {
                                                return;
                                            }
                                        }
                                        break;
                                    }
                                    case 'C': {
                                        switch (typeName.length) {
                                            case 5: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_CLASS[2])) {
                                                    this.id = 16;
                                                }
                                                return;
                                            }
                                            case 9: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_CHARACTER[2])) {
                                                    this.id = 28;
                                                }
                                                else if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_CLONEABLE[2])) {
                                                    this.id = 36;
                                                }
                                                return;
                                            }
                                            case 22: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_CLASSNOTFOUNDEXCEPTION[2])) {
                                                    this.id = 23;
                                                }
                                                return;
                                            }
                                            default: {
                                                return;
                                            }
                                        }
                                        break;
                                    }
                                    case 'D': {
                                        switch (typeName.length) {
                                            case 6: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_DOUBLE[2])) {
                                                    this.id = 32;
                                                }
                                                return;
                                            }
                                            case 10: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_DEPRECATED[2])) {
                                                    this.id = 44;
                                                }
                                                return;
                                            }
                                            default: {
                                                return;
                                            }
                                        }
                                        break;
                                    }
                                    case 'E': {
                                        switch (typeName.length) {
                                            case 4: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ENUM[2])) {
                                                    this.id = 41;
                                                }
                                                return;
                                            }
                                            case 5: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ERROR[2])) {
                                                    this.id = 19;
                                                }
                                                return;
                                            }
                                            case 9: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_EXCEPTION[2])) {
                                                    this.id = 25;
                                                }
                                                return;
                                            }
                                            default: {
                                                return;
                                            }
                                        }
                                        break;
                                    }
                                    case 'F': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_FLOAT[2])) {
                                            this.id = 31;
                                        }
                                        else if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_FUNCTIONAL_INTERFACE[2])) {
                                            this.id = 77;
                                        }
                                        return;
                                    }
                                    case 'I': {
                                        switch (typeName.length) {
                                            case 7: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_INTEGER[2])) {
                                                    this.id = 29;
                                                }
                                                return;
                                            }
                                            case 8: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ITERABLE[2])) {
                                                    this.id = 38;
                                                }
                                                return;
                                            }
                                            case 24: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ILLEGALARGUMENTEXCEPTION[2])) {
                                                    this.id = 42;
                                                }
                                                return;
                                            }
                                            default: {
                                                return;
                                            }
                                        }
                                        break;
                                    }
                                    case 'L': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_LONG[2])) {
                                            this.id = 30;
                                        }
                                        return;
                                    }
                                    case 'N': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_NOCLASSDEFERROR[2])) {
                                            this.id = 22;
                                        }
                                        return;
                                    }
                                    case 'O': {
                                        switch (typeName.length) {
                                            case 6: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_OBJECT[2])) {
                                                    this.id = 1;
                                                }
                                                return;
                                            }
                                            case 8: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_OVERRIDE[2])) {
                                                    this.id = 47;
                                                }
                                                return;
                                            }
                                            default: {
                                                return;
                                            }
                                        }
                                        break;
                                    }
                                    case 'R': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_RUNTIMEEXCEPTION[2])) {
                                            this.id = 24;
                                            break Label_2829;
                                        }
                                        break Label_2829;
                                    }
                                    case 'S': {
                                        switch (typeName.length) {
                                            case 5: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_SHORT[2])) {
                                                    this.id = 27;
                                                }
                                                return;
                                            }
                                            case 6: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_STRING[2])) {
                                                    this.id = 11;
                                                }
                                                else if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_SYSTEM[2])) {
                                                    this.id = 18;
                                                }
                                                return;
                                            }
                                            case 11: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_SAFEVARARGS[2])) {
                                                    this.id = 60;
                                                }
                                                return;
                                            }
                                            case 12: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_STRINGBUFFER[2])) {
                                                    this.id = 17;
                                                }
                                                return;
                                            }
                                            case 13: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_STRINGBUILDER[2])) {
                                                    this.id = 40;
                                                }
                                                return;
                                            }
                                            case 16: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_SUPPRESSWARNINGS[2])) {
                                                    this.id = 49;
                                                }
                                                return;
                                            }
                                            default: {
                                                return;
                                            }
                                        }
                                        break;
                                    }
                                    case 'T': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_THROWABLE[2])) {
                                            this.id = 21;
                                        }
                                        return;
                                    }
                                    case 'V': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_VOID[2])) {
                                            this.id = 34;
                                        }
                                        return;
                                    }
                                    default: {
                                        break Label_2829;
                                    }
                                }
                            }
                            break;
                        }
                        case 5: {
                            switch (packageName[1]) {
                                case 'a': {
                                    if (CharOperation.equals(TypeConstants.JAVAX_ANNOTATION_INJECT_INJECT, this.compoundName)) {
                                        this.id = 80;
                                    }
                                    return;
                                }
                                case 'u': {
                                    if (CharOperation.equals(TypeConstants.JUNIT_FRAMEWORK_ASSERT, this.compoundName)) {
                                        this.id = 69;
                                    }
                                    return;
                                }
                                default: {
                                    return;
                                }
                            }
                            break;
                        }
                        default: {
                            return;
                        }
                    }
                    break;
                }
                case 4: {
                    if (CharOperation.equals(TypeConstants.COM_GOOGLE_INJECT_INJECT, this.compoundName)) {
                        this.id = 81;
                        return;
                    }
                    if (!CharOperation.equals(TypeConstants.JAVA, this.compoundName[0])) {
                        return;
                    }
                    char[] packageName = this.compoundName[1];
                    if (packageName.length == 0) {
                        return;
                    }
                    packageName = this.compoundName[2];
                    if (packageName.length == 0) {
                        return;
                    }
                    final char[] typeName = this.compoundName[3];
                    if (typeName.length == 0) {
                        return;
                    }
                    switch (packageName[0]) {
                        case 'a': {
                            if (CharOperation.equals(packageName, TypeConstants.ANNOTATION)) {
                                switch (typeName[0]) {
                                    case 'A': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_ANNOTATION[3])) {
                                            this.id = 43;
                                        }
                                        return;
                                    }
                                    case 'D': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_DOCUMENTED[3])) {
                                            this.id = 45;
                                        }
                                        return;
                                    }
                                    case 'E': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_ELEMENTTYPE[3])) {
                                            this.id = 52;
                                        }
                                        return;
                                    }
                                    case 'I': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_INHERITED[3])) {
                                            this.id = 46;
                                        }
                                        return;
                                    }
                                    case 'R': {
                                        switch (typeName.length) {
                                            case 9: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_RETENTION[3])) {
                                                    this.id = 48;
                                                }
                                                return;
                                            }
                                            case 10: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_REPEATABLE[3])) {
                                                    this.id = 90;
                                                }
                                                return;
                                            }
                                            case 15: {
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_RETENTIONPOLICY[3])) {
                                                    this.id = 51;
                                                }
                                                return;
                                            }
                                            default: {
                                                return;
                                            }
                                        }
                                        break;
                                    }
                                    case 'T': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_TARGET[3])) {
                                            this.id = 50;
                                        }
                                    }
                                }
                            }
                            return;
                        }
                        case 'i': {
                            if (CharOperation.equals(packageName, TypeConstants.INVOKE)) {
                                if (typeName.length == 0) {
                                    return;
                                }
                                switch (typeName[0]) {
                                    case 'M': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_INVOKE_METHODHANDLE_$_POLYMORPHICSIGNATURE[3])) {
                                            this.id = 61;
                                        }
                                    }
                                }
                            }
                            return;
                        }
                        case 'r': {
                            if (CharOperation.equals(packageName, TypeConstants.REFLECT)) {
                                switch (typeName[0]) {
                                    case 'C': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_REFLECT_CONSTRUCTOR[2])) {
                                            this.id = 20;
                                        }
                                        return;
                                    }
                                    case 'F': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_REFLECT_FIELD[2])) {
                                            this.id = 54;
                                        }
                                        return;
                                    }
                                    case 'M': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_REFLECT_METHOD[2])) {
                                            this.id = 55;
                                        }
                                    }
                                }
                            }
                            return;
                        }
                        default: {
                            break Label_2829;
                        }
                    }
                    break;
                }
                case 5: {
                    char[] packageName = this.compoundName[0];
                    switch (packageName[0]) {
                        case 'j': {
                            if (!CharOperation.equals(TypeConstants.JAVA, this.compoundName[0])) {
                                return;
                            }
                            packageName = this.compoundName[1];
                            if (packageName.length == 0) {
                                return;
                            }
                            if (!CharOperation.equals(TypeConstants.LANG, packageName)) {
                                return;
                            }
                            packageName = this.compoundName[2];
                            if (packageName.length == 0) {
                                return;
                            }
                            switch (packageName[0]) {
                                case 'i': {
                                    if (CharOperation.equals(packageName, TypeConstants.INVOKE)) {
                                        final char[] typeName = this.compoundName[3];
                                        if (typeName.length == 0) {
                                            return;
                                        }
                                        switch (typeName[0]) {
                                            case 'M': {
                                                final char[] memberTypeName = this.compoundName[4];
                                                if (memberTypeName.length == 0) {
                                                    return;
                                                }
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_INVOKE_METHODHANDLE_POLYMORPHICSIGNATURE[3]) && CharOperation.equals(memberTypeName, TypeConstants.JAVA_LANG_INVOKE_METHODHANDLE_POLYMORPHICSIGNATURE[4])) {
                                                    this.id = 61;
                                                }
                                            }
                                        }
                                    }
                                    return;
                                }
                                default: {
                                    return;
                                }
                            }
                            break;
                        }
                        case 'o': {
                            if (!CharOperation.equals(TypeConstants.ORG, this.compoundName[0])) {
                                return;
                            }
                            packageName = this.compoundName[1];
                            if (packageName.length == 0) {
                                return;
                            }
                            switch (packageName[0]) {
                                case 'e': {
                                    if (!CharOperation.equals(TypeConstants.ECLIPSE, packageName)) {
                                        return;
                                    }
                                    packageName = this.compoundName[2];
                                    if (packageName.length == 0) {
                                        return;
                                    }
                                    switch (packageName[0]) {
                                        case 'c': {
                                            if (CharOperation.equals(packageName, TypeConstants.CORE)) {
                                                final char[] typeName = this.compoundName[3];
                                                if (typeName.length == 0) {
                                                    return;
                                                }
                                                switch (typeName[0]) {
                                                    case 'r': {
                                                        final char[] memberTypeName = this.compoundName[4];
                                                        if (memberTypeName.length == 0) {
                                                            return;
                                                        }
                                                        if (CharOperation.equals(typeName, TypeConstants.ORG_ECLIPSE_CORE_RUNTIME_ASSERT[3]) && CharOperation.equals(memberTypeName, TypeConstants.ORG_ECLIPSE_CORE_RUNTIME_ASSERT[4])) {
                                                            this.id = 68;
                                                        }
                                                    }
                                                }
                                            }
                                            return;
                                        }
                                        default: {
                                            return;
                                        }
                                    }
                                    break;
                                }
                                case 'a': {
                                    if (CharOperation.equals(TypeConstants.APACHE, packageName) && CharOperation.equals(TypeConstants.COMMONS, this.compoundName[2])) {
                                        if (CharOperation.equals(TypeConstants.ORG_APACHE_COMMONS_LANG_VALIDATE, this.compoundName)) {
                                            this.id = 71;
                                        }
                                        else if (CharOperation.equals(TypeConstants.ORG_APACHE_COMMONS_LANG3_VALIDATE, this.compoundName)) {
                                            this.id = 72;
                                        }
                                    }
                                    return;
                                }
                                default: {
                                    return;
                                }
                            }
                            break;
                        }
                        case 'c': {
                            if (!CharOperation.equals(TypeConstants.COM, this.compoundName[0])) {
                                return;
                            }
                            if (CharOperation.equals(TypeConstants.COM_GOOGLE_COMMON_BASE_PRECONDITIONS, this.compoundName)) {
                                this.id = 73;
                            }
                            return;
                        }
                        default: {
                            break Label_2829;
                        }
                    }
                    break;
                }
                case 6: {
                    if (!CharOperation.equals(TypeConstants.ORG, this.compoundName[0])) {
                        break;
                    }
                    if (CharOperation.equals(TypeConstants.SPRING, this.compoundName[1])) {
                        if (CharOperation.equals(TypeConstants.AUTOWIRED, this.compoundName[5]) && CharOperation.equals(TypeConstants.ORG_SPRING_AUTOWIRED, this.compoundName)) {
                            this.id = 82;
                        }
                        return;
                    }
                    if (!CharOperation.equals(TypeConstants.JDT, this.compoundName[2]) || !CharOperation.equals(TypeConstants.ITYPEBINDING, this.compoundName[5])) {
                        return;
                    }
                    if (CharOperation.equals(TypeConstants.ORG_ECLIPSE_JDT_CORE_DOM_ITYPEBINDING, this.compoundName)) {
                        this.typeBits |= 0x10;
                        break;
                    }
                    break;
                }
                case 7: {
                    if (!CharOperation.equals(TypeConstants.JDT, this.compoundName[2]) || !CharOperation.equals(TypeConstants.TYPEBINDING, this.compoundName[6])) {
                        return;
                    }
                    if (CharOperation.equals(TypeConstants.ORG_ECLIPSE_JDT_INTERNAL_COMPILER_LOOKUP_TYPEBINDING, this.compoundName)) {
                        this.typeBits |= 0x10;
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    public void computeId(final LookupEnvironment environment) {
        environment.getUnannotatedType(this);
    }
    
    @Override
    public char[] computeUniqueKey(final boolean isLeaf) {
        if (!isLeaf) {
            return this.signature();
        }
        return this.genericTypeSignature();
    }
    
    @Override
    public char[] constantPoolName() {
        if (this.constantPoolName != null) {
            return this.constantPoolName;
        }
        return this.constantPoolName = CharOperation.concatWith(this.compoundName, '/');
    }
    
    @Override
    public String debugName() {
        return (this.compoundName != null) ? (this.hasTypeAnnotations() ? this.annotatedDebugName() : new String(this.readableName())) : "UNNAMED TYPE";
    }
    
    @Override
    public int depth() {
        int depth = 0;
        ReferenceBinding current = this;
        while ((current = current.enclosingType()) != null) {
            ++depth;
        }
        return depth;
    }
    
    public boolean detectAnnotationCycle() {
        if ((this.tagBits & 0x100000000L) != 0x0L) {
            return false;
        }
        if ((this.tagBits & 0x80000000L) != 0x0L) {
            return true;
        }
        this.tagBits |= 0x80000000L;
        final MethodBinding[] currentMethods = this.methods();
        boolean inCycle = false;
        for (int i = 0, l = currentMethods.length; i < l; ++i) {
            final TypeBinding returnType = currentMethods[i].returnType.leafComponentType().erasure();
            if (TypeBinding.equalsEquals(this, returnType)) {
                if (this instanceof SourceTypeBinding) {
                    final MethodDeclaration decl = (MethodDeclaration)currentMethods[i].sourceMethod();
                    ((SourceTypeBinding)this).scope.problemReporter().annotationCircularity(this, this, (decl != null) ? decl.returnType : null);
                }
            }
            else if (returnType.isAnnotationType() && ((ReferenceBinding)returnType).detectAnnotationCycle()) {
                if (this instanceof SourceTypeBinding) {
                    final MethodDeclaration decl = (MethodDeclaration)currentMethods[i].sourceMethod();
                    ((SourceTypeBinding)this).scope.problemReporter().annotationCircularity(this, returnType, (decl != null) ? decl.returnType : null);
                }
                inCycle = true;
            }
        }
        if (inCycle) {
            return true;
        }
        this.tagBits |= 0x100000000L;
        return false;
    }
    
    public final ReferenceBinding enclosingTypeAt(int relativeDepth) {
        ReferenceBinding current;
        for (current = this; relativeDepth-- > 0 && current != null; current = current.enclosingType()) {}
        return current;
    }
    
    public int enumConstantCount() {
        int count = 0;
        final FieldBinding[] fields = this.fields();
        for (int i = 0, length = fields.length; i < length; ++i) {
            if ((fields[i].modifiers & 0x4000) != 0x0) {
                ++count;
            }
        }
        return count;
    }
    
    public int fieldCount() {
        return this.fields().length;
    }
    
    public FieldBinding[] fields() {
        return Binding.NO_FIELDS;
    }
    
    public final int getAccessFlags() {
        return this.modifiers & 0xFFFF;
    }
    
    @Override
    public AnnotationBinding[] getAnnotations() {
        return this.retrieveAnnotations(this);
    }
    
    @Override
    public long getAnnotationTagBits() {
        return this.tagBits;
    }
    
    public int getEnclosingInstancesSlotSize() {
        return (!this.isStatic() && this.enclosingType() != null) ? 1 : 0;
    }
    
    public MethodBinding getExactConstructor(final TypeBinding[] argumentTypes) {
        return null;
    }
    
    public MethodBinding getExactMethod(final char[] selector, final TypeBinding[] argumentTypes, final CompilationUnitScope refScope) {
        return null;
    }
    
    public FieldBinding getField(final char[] fieldName, final boolean needResolve) {
        return null;
    }
    
    public char[] getFileName() {
        return this.fileName;
    }
    
    public ReferenceBinding getMemberType(final char[] typeName) {
        final ReferenceBinding[] memberTypes = this.memberTypes();
        int i = memberTypes.length;
        while (--i >= 0) {
            if (CharOperation.equals(memberTypes[i].sourceName, typeName)) {
                return memberTypes[i];
            }
        }
        return null;
    }
    
    @Override
    public MethodBinding[] getMethods(final char[] selector) {
        return Binding.NO_METHODS;
    }
    
    public MethodBinding[] getMethods(final char[] selector, final int suggestedParameterLength) {
        return this.getMethods(selector);
    }
    
    public int getOuterLocalVariablesSlotSize() {
        return 0;
    }
    
    @Override
    public PackageBinding getPackage() {
        return this.fPackage;
    }
    
    public TypeVariableBinding getTypeVariable(final char[] variableName) {
        final TypeVariableBinding[] typeVariables = this.typeVariables();
        int i = typeVariables.length;
        while (--i >= 0) {
            if (CharOperation.equals(typeVariables[i].sourceName, variableName)) {
                return typeVariables[i];
            }
        }
        return null;
    }
    
    @Override
    public int hashCode() {
        return (this.compoundName == null || this.compoundName.length == 0) ? super.hashCode() : CharOperation.hashCode(this.compoundName[this.compoundName.length - 1]);
    }
    
    public boolean hasIncompatibleSuperType(final ReferenceBinding otherType) {
        if (TypeBinding.equalsEquals(this, otherType)) {
            return false;
        }
        ReferenceBinding[] interfacesToVisit = null;
        int nextPosition = 0;
        ReferenceBinding currentType = this;
        do {
            final TypeBinding match = otherType.findSuperTypeOriginatingFrom(currentType);
            if (match != null && match.isProvablyDistinct(currentType)) {
                return true;
            }
            final ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
            if (itsInterfaces == null || itsInterfaces == Binding.NO_SUPERINTERFACES) {
                continue;
            }
            if (interfacesToVisit == null) {
                interfacesToVisit = itsInterfaces;
                nextPosition = interfacesToVisit.length;
            }
            else {
                final int itsLength = itsInterfaces.length;
                if (nextPosition + itsLength >= interfacesToVisit.length) {
                    System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
                }
                int a = 0;
            Label_0161:
                while (a < itsLength) {
                    final ReferenceBinding next = itsInterfaces[a];
                    while (true) {
                        for (int b = 0; b < nextPosition; ++b) {
                            if (TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                ++a;
                                continue Label_0161;
                            }
                        }
                        interfacesToVisit[nextPosition++] = next;
                        continue;
                    }
                }
            }
        } while ((currentType = currentType.superclass()) != null);
        for (int i = 0; i < nextPosition; ++i) {
            currentType = interfacesToVisit[i];
            if (TypeBinding.equalsEquals(currentType, otherType)) {
                return false;
            }
            final TypeBinding match = otherType.findSuperTypeOriginatingFrom(currentType);
            if (match != null && match.isProvablyDistinct(currentType)) {
                return true;
            }
            final ReferenceBinding[] itsInterfaces2 = currentType.superInterfaces();
            if (itsInterfaces2 != null && itsInterfaces2 != Binding.NO_SUPERINTERFACES) {
                final int itsLength2 = itsInterfaces2.length;
                if (nextPosition + itsLength2 >= interfacesToVisit.length) {
                    System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength2 + 5], 0, nextPosition);
                }
                int a2 = 0;
            Label_0333:
                while (a2 < itsLength2) {
                    final ReferenceBinding next2 = itsInterfaces2[a2];
                    while (true) {
                        for (int b2 = 0; b2 < nextPosition; ++b2) {
                            if (TypeBinding.equalsEquals(next2, interfacesToVisit[b2])) {
                                ++a2;
                                continue Label_0333;
                            }
                        }
                        interfacesToVisit[nextPosition++] = next2;
                        continue;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean hasMemberTypes() {
        return false;
    }
    
    boolean hasNonNullDefaultFor(final int location, final boolean useTypeAnnotations) {
        for (ReferenceBinding currentType = this; currentType != null; currentType = currentType.enclosingType()) {
            if (useTypeAnnotations) {
                final int nullDefault = ((ReferenceBinding)currentType.original()).getNullDefault();
                if (nullDefault != 0) {
                    return (nullDefault & location) != 0x0;
                }
            }
            else {
                if ((currentType.tagBits & 0x200000000000000L) != 0x0L) {
                    return true;
                }
                if ((currentType.tagBits & 0x400000000000000L) != 0x0L) {
                    return false;
                }
            }
        }
        if (useTypeAnnotations) {
            return (this.getPackage().defaultNullness & location) != 0x0;
        }
        return this.getPackage().defaultNullness == 1;
    }
    
    int getNullDefault() {
        return 0;
    }
    
    @Override
    public boolean acceptsNonNullDefault() {
        return true;
    }
    
    public final boolean hasRestrictedAccess() {
        return (this.modifiers & 0x40000) != 0x0;
    }
    
    public boolean hasNullBit(final int mask) {
        return (this.typeBits & mask) != 0x0;
    }
    
    public boolean implementsInterface(final ReferenceBinding anInterface, final boolean searchHierarchy) {
        if (TypeBinding.equalsEquals(this, anInterface)) {
            return true;
        }
        ReferenceBinding[] interfacesToVisit = null;
        int nextPosition = 0;
        ReferenceBinding currentType = this;
        do {
            final ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
            if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
                if (interfacesToVisit == null) {
                    interfacesToVisit = itsInterfaces;
                    nextPosition = interfacesToVisit.length;
                }
                else {
                    final int itsLength = itsInterfaces.length;
                    if (nextPosition + itsLength >= interfacesToVisit.length) {
                        System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
                    }
                    int a = 0;
                Label_0143:
                    while (a < itsLength) {
                        final ReferenceBinding next = itsInterfaces[a];
                        while (true) {
                            for (int b = 0; b < nextPosition; ++b) {
                                if (TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                    ++a;
                                    continue Label_0143;
                                }
                            }
                            interfacesToVisit[nextPosition++] = next;
                            continue;
                        }
                    }
                }
            }
        } while (searchHierarchy && (currentType = currentType.superclass()) != null);
        for (int i = 0; i < nextPosition; ++i) {
            currentType = interfacesToVisit[i];
            if (currentType.isEquivalentTo(anInterface)) {
                return true;
            }
            final ReferenceBinding[] itsInterfaces2 = currentType.superInterfaces();
            if (itsInterfaces2 != null && itsInterfaces2 != Binding.NO_SUPERINTERFACES) {
                final int itsLength2 = itsInterfaces2.length;
                if (nextPosition + itsLength2 >= interfacesToVisit.length) {
                    System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength2 + 5], 0, nextPosition);
                }
                int a2 = 0;
            Label_0299:
                while (a2 < itsLength2) {
                    final ReferenceBinding next2 = itsInterfaces2[a2];
                    while (true) {
                        for (int b2 = 0; b2 < nextPosition; ++b2) {
                            if (TypeBinding.equalsEquals(next2, interfacesToVisit[b2])) {
                                ++a2;
                                continue Label_0299;
                            }
                        }
                        interfacesToVisit[nextPosition++] = next2;
                        continue;
                    }
                }
            }
        }
        return false;
    }
    
    boolean implementsMethod(final MethodBinding method) {
        final char[] selector = method.selector;
        for (ReferenceBinding type = this; type != null; type = type.superclass()) {
            final MethodBinding[] methods = type.methods();
            final long range;
            if ((range = binarySearch(selector, methods)) >= 0L) {
                final int start = (int)range;
                for (int end = (int)(range >> 32), i = start; i <= end; ++i) {
                    if (methods[i].areParametersEqual(method)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public final boolean isAbstract() {
        return (this.modifiers & 0x400) != 0x0;
    }
    
    @Override
    public boolean isAnnotationType() {
        return (this.modifiers & 0x2000) != 0x0;
    }
    
    public final boolean isBinaryBinding() {
        return (this.tagBits & 0x40L) != 0x0L;
    }
    
    @Override
    public boolean isClass() {
        return (this.modifiers & 0x6200) == 0x0;
    }
    
    @Override
    public boolean isProperType(final boolean admitCapture18) {
        final ReferenceBinding outer = this.enclosingType();
        return (outer == null || outer.isProperType(admitCapture18)) && super.isProperType(admitCapture18);
    }
    
    @Override
    public boolean isCompatibleWith(final TypeBinding otherType, final Scope captureScope) {
        if (TypeBinding.equalsEquals(otherType, this)) {
            return true;
        }
        if (otherType.id == 1) {
            return true;
        }
        if (this.compatibleCache == null) {
            this.compatibleCache = new SimpleLookupTable(3);
            final Object result = null;
        }
        else {
            final Object result = this.compatibleCache.get(otherType);
            if (result != null) {
                return result == Boolean.TRUE;
            }
        }
        this.compatibleCache.put(otherType, Boolean.FALSE);
        if (this.isCompatibleWith0(otherType, captureScope)) {
            this.compatibleCache.put(otherType, Boolean.TRUE);
            return true;
        }
        if (captureScope == null && this instanceof TypeVariableBinding && ((TypeVariableBinding)this).firstBound instanceof ParameterizedTypeBinding) {
            this.compatibleCache.put(otherType, null);
        }
        return false;
    }
    
    private boolean isCompatibleWith0(final TypeBinding otherType, final Scope captureScope) {
        if (TypeBinding.equalsEquals(otherType, this)) {
            return true;
        }
        if (otherType.id == 1) {
            return true;
        }
        if (this.isEquivalentTo(otherType)) {
            return true;
        }
        CaptureBinding otherCapture;
        TypeBinding otherLowerBound;
        final MethodScope methodScope;
        final ReferenceContext referenceContext;
        switch (otherType.kind()) {
            case 516:
            case 8196: {
                return false;
            }
            case 4100:
                Label_0188: {
                    if (otherType.isCapture()) {
                        otherCapture = (CaptureBinding)otherType;
                        if ((otherLowerBound = otherCapture.lowerBound) != null) {
                            return !otherLowerBound.isArrayType() && this.isCompatibleWith(otherLowerBound);
                        }
                    }
                    if (!(otherType instanceof InferenceVariable) || captureScope == null) {
                        break Label_0188;
                    }
                    methodScope = captureScope.methodScope();
                    if (methodScope == null) {
                        break Label_0188;
                    }
                    referenceContext = methodScope.referenceContext;
                    if (referenceContext instanceof LambdaExpression && ((LambdaExpression)referenceContext).inferenceContext != null) {
                        return true;
                    }
                    break Label_0188;
                }
            case 4:
            case 260:
            case 1028:
            case 2052: {
                switch (this.kind()) {
                    case 260:
                    case 1028:
                    case 2052: {
                        if (TypeBinding.equalsEquals(this.erasure(), otherType.erasure())) {
                            return false;
                        }
                        break;
                    }
                }
                final ReferenceBinding otherReferenceType = (ReferenceBinding)otherType;
                if (otherReferenceType.isInterface()) {
                    if (this.implementsInterface(otherReferenceType, true)) {
                        return true;
                    }
                    if (this instanceof TypeVariableBinding && captureScope != null) {
                        final TypeVariableBinding typeVariable = (TypeVariableBinding)this;
                        if (typeVariable.firstBound instanceof ParameterizedTypeBinding) {
                            final TypeBinding bound = typeVariable.firstBound.capture(captureScope, -1, -1);
                            return bound.isCompatibleWith(otherReferenceType);
                        }
                    }
                }
                return !this.isInterface() && otherReferenceType.isSuperclassOf(this);
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public boolean isSubtypeOf(final TypeBinding other) {
        if (this.isSubTypeOfRTL(other)) {
            return true;
        }
        final TypeBinding candidate = this.findSuperTypeOriginatingFrom(other);
        if (candidate == null) {
            return false;
        }
        if (TypeBinding.equalsEquals(candidate, other)) {
            return true;
        }
        if (other.isRawType() && TypeBinding.equalsEquals(candidate.erasure(), other.erasure())) {
            return true;
        }
        final TypeBinding[] sis = other.typeArguments();
        final TypeBinding[] tis = candidate.typeArguments();
        if (tis == null || sis == null) {
            return false;
        }
        if (sis.length != tis.length) {
            return false;
        }
        for (int i = 0; i < sis.length; ++i) {
            if (!tis[i].isTypeArgumentContainedBy(sis[i])) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean isSubTypeOfRTL(final TypeBinding other) {
        if (TypeBinding.equalsEquals(this, other)) {
            return true;
        }
        if (other instanceof CaptureBinding) {
            final TypeBinding lower = ((CaptureBinding)other).lowerBound;
            return lower != null && this.isSubtypeOf(lower);
        }
        if (other instanceof ReferenceBinding) {
            final TypeBinding[] intersecting = other.getIntersectingTypes();
            if (intersecting != null) {
                for (int i = 0; i < intersecting.length; ++i) {
                    if (!this.isSubtypeOf(intersecting[i])) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    public final boolean isDefault() {
        return (this.modifiers & 0x7) == 0x0;
    }
    
    public final boolean isDeprecated() {
        return (this.modifiers & 0x100000) != 0x0;
    }
    
    @Override
    public boolean isEnum() {
        return (this.modifiers & 0x4000) != 0x0;
    }
    
    public final boolean isFinal() {
        return (this.modifiers & 0x10) != 0x0;
    }
    
    public boolean isHierarchyBeingConnected() {
        return (this.tagBits & 0x200L) == 0x0L && (this.tagBits & 0x100L) != 0x0L;
    }
    
    public boolean isHierarchyBeingActivelyConnected() {
        return (this.tagBits & 0x200L) == 0x0L && (this.tagBits & 0x100L) != 0x0L && (this.tagBits & 0x80000L) == 0x0L;
    }
    
    public boolean isHierarchyConnected() {
        return true;
    }
    
    @Override
    public boolean isInterface() {
        return (this.modifiers & 0x200) != 0x0;
    }
    
    @Override
    public boolean isFunctionalInterface(final Scope scope) {
        final MethodBinding method;
        return this.isInterface() && (method = this.getSingleAbstractMethod(scope, true)) != null && method.isValidBinding();
    }
    
    public final boolean isPrivate() {
        return (this.modifiers & 0x2) != 0x0;
    }
    
    public final boolean isOrEnclosedByPrivateType() {
        if (this.isLocalType()) {
            return true;
        }
        for (ReferenceBinding type = this; type != null; type = type.enclosingType()) {
            if ((type.modifiers & 0x2) != 0x0) {
                return true;
            }
        }
        return false;
    }
    
    public final boolean isProtected() {
        return (this.modifiers & 0x4) != 0x0;
    }
    
    public final boolean isPublic() {
        return (this.modifiers & 0x1) != 0x0;
    }
    
    @Override
    public final boolean isStatic() {
        return (this.modifiers & 0x208) != 0x0 || (this.tagBits & 0x4L) == 0x0L;
    }
    
    public final boolean isStrictfp() {
        return (this.modifiers & 0x800) != 0x0;
    }
    
    public boolean isSuperclassOf(ReferenceBinding otherType) {
        while ((otherType = otherType.superclass()) != null) {
            if (otherType.isEquivalentTo(this)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isThrowable() {
        ReferenceBinding current = this;
        do {
            switch (current.id) {
                case 19:
                case 21:
                case 24:
                case 25: {
                    return true;
                }
                default: {
                    continue;
                }
            }
        } while ((current = current.superclass()) != null);
        return false;
    }
    
    @Override
    public boolean isUncheckedException(final boolean includeSupertype) {
        switch (this.id) {
            case 19:
            case 24: {
                return true;
            }
            case 21:
            case 25: {
                return includeSupertype;
            }
            default: {
                ReferenceBinding current = this;
                while ((current = current.superclass()) != null) {
                    switch (current.id) {
                        case 19:
                        case 24: {
                            return true;
                        }
                        case 21:
                        case 25: {
                            return false;
                        }
                        default: {
                            continue;
                        }
                    }
                }
                return false;
            }
        }
    }
    
    public final boolean isUsed() {
        return (this.modifiers & 0x8000000) != 0x0;
    }
    
    public final boolean isViewedAsDeprecated() {
        return (this.modifiers & 0x300000) != 0x0 || this.getPackage().isViewedAsDeprecated();
    }
    
    public ReferenceBinding[] memberTypes() {
        return Binding.NO_MEMBER_TYPES;
    }
    
    public MethodBinding[] methods() {
        return Binding.NO_METHODS;
    }
    
    public final ReferenceBinding outermostEnclosingType() {
        ReferenceBinding current = this;
        ReferenceBinding last;
        do {
            last = current;
        } while ((current = current.enclosingType()) != null);
        return last;
    }
    
    @Override
    public char[] qualifiedSourceName() {
        if (this.isMemberType()) {
            return CharOperation.concat(this.enclosingType().qualifiedSourceName(), this.sourceName(), '.');
        }
        return this.sourceName();
    }
    
    @Override
    public char[] readableName() {
        char[] readableName;
        if (this.isMemberType()) {
            readableName = CharOperation.concat(this.enclosingType().readableName(), this.sourceName, '.');
        }
        else {
            readableName = CharOperation.concatWith(this.compoundName, '.');
        }
        final TypeVariableBinding[] typeVars;
        if ((typeVars = this.typeVariables()) != Binding.NO_TYPE_VARIABLES) {
            final StringBuffer nameBuffer = new StringBuffer(10);
            nameBuffer.append(readableName).append('<');
            for (int i = 0, length = typeVars.length; i < length; ++i) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(typeVars[i].readableName());
            }
            nameBuffer.append('>');
            final int nameLength = nameBuffer.length();
            readableName = new char[nameLength];
            nameBuffer.getChars(0, nameLength, readableName, 0);
        }
        return readableName;
    }
    
    protected void appendNullAnnotation(final StringBuffer nameBuffer, final CompilerOptions options) {
        if (options.isAnnotationBasedNullAnalysisEnabled) {
            if (options.usesNullTypeAnnotations()) {
                AnnotationBinding[] typeAnnotations;
                for (int length = (typeAnnotations = this.typeAnnotations).length, i = 0; i < length; ++i) {
                    final AnnotationBinding annotation = typeAnnotations[i];
                    final ReferenceBinding annotationType = annotation.getAnnotationType();
                    if (annotationType.hasNullBit(96)) {
                        nameBuffer.append('@').append(annotationType.shortReadableName()).append(' ');
                    }
                }
            }
            else {
                if ((this.tagBits & 0x100000000000000L) != 0x0L) {
                    final char[][] nonNullAnnotationName = options.nonNullAnnotationName;
                    nameBuffer.append('@').append(nonNullAnnotationName[nonNullAnnotationName.length - 1]).append(' ');
                }
                if ((this.tagBits & 0x80000000000000L) != 0x0L) {
                    final char[][] nullableAnnotationName = options.nullableAnnotationName;
                    nameBuffer.append('@').append(nullableAnnotationName[nullableAnnotationName.length - 1]).append(' ');
                }
            }
        }
    }
    
    public AnnotationHolder retrieveAnnotationHolder(final Binding binding, final boolean forceInitialization) {
        final SimpleLookupTable store = this.storedAnnotations(forceInitialization);
        return (store == null) ? null : ((AnnotationHolder)store.get(binding));
    }
    
    AnnotationBinding[] retrieveAnnotations(final Binding binding) {
        final AnnotationHolder holder = this.retrieveAnnotationHolder(binding, true);
        return (holder == null) ? Binding.NO_ANNOTATIONS : holder.getAnnotations();
    }
    
    @Override
    public void setAnnotations(final AnnotationBinding[] annotations) {
        this.storeAnnotations(this, annotations);
    }
    
    public void setContainerAnnotationType(final ReferenceBinding value) {
    }
    
    public void tagAsHavingDefectiveContainerType() {
    }
    
    @Override
    public char[] nullAnnotatedReadableName(final CompilerOptions options, final boolean shortNames) {
        if (shortNames) {
            return this.nullAnnotatedShortReadableName(options);
        }
        return this.nullAnnotatedReadableName(options);
    }
    
    char[] nullAnnotatedReadableName(final CompilerOptions options) {
        final StringBuffer nameBuffer = new StringBuffer(10);
        if (this.isMemberType()) {
            nameBuffer.append(this.enclosingType().nullAnnotatedReadableName(options, false));
            nameBuffer.append('.');
            this.appendNullAnnotation(nameBuffer, options);
            nameBuffer.append(this.sourceName);
        }
        else if (this.compoundName != null) {
            int l;
            int i;
            for (l = this.compoundName.length, i = 0; i < l - 1; ++i) {
                nameBuffer.append(this.compoundName[i]);
                nameBuffer.append('.');
            }
            this.appendNullAnnotation(nameBuffer, options);
            nameBuffer.append(this.compoundName[i]);
        }
        else {
            this.appendNullAnnotation(nameBuffer, options);
            if (this.sourceName != null) {
                nameBuffer.append(this.sourceName);
            }
            else {
                nameBuffer.append(this.readableName());
            }
        }
        final TypeBinding[] arguments = this.typeArguments();
        if (arguments != null && arguments.length > 0) {
            nameBuffer.append('<');
            for (int j = 0, length = arguments.length; j < length; ++j) {
                if (j > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(arguments[j].nullAnnotatedReadableName(options, false));
            }
            nameBuffer.append('>');
        }
        final int nameLength = nameBuffer.length();
        final char[] readableName = new char[nameLength];
        nameBuffer.getChars(0, nameLength, readableName, 0);
        return readableName;
    }
    
    char[] nullAnnotatedShortReadableName(final CompilerOptions options) {
        final StringBuffer nameBuffer = new StringBuffer(10);
        if (this.isMemberType()) {
            nameBuffer.append(this.enclosingType().nullAnnotatedReadableName(options, true));
            nameBuffer.append('.');
            this.appendNullAnnotation(nameBuffer, options);
            nameBuffer.append(this.sourceName);
        }
        else {
            this.appendNullAnnotation(nameBuffer, options);
            if (this.sourceName != null) {
                nameBuffer.append(this.sourceName);
            }
            else {
                nameBuffer.append(this.shortReadableName());
            }
        }
        final TypeBinding[] arguments = this.typeArguments();
        if (arguments != null && arguments.length > 0) {
            nameBuffer.append('<');
            for (int i = 0, length = arguments.length; i < length; ++i) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(arguments[i].nullAnnotatedReadableName(options, true));
            }
            nameBuffer.append('>');
        }
        final int nameLength = nameBuffer.length();
        final char[] shortReadableName = new char[nameLength];
        nameBuffer.getChars(0, nameLength, shortReadableName, 0);
        return shortReadableName;
    }
    
    @Override
    public char[] shortReadableName() {
        char[] shortReadableName;
        if (this.isMemberType()) {
            shortReadableName = CharOperation.concat(this.enclosingType().shortReadableName(), this.sourceName, '.');
        }
        else {
            shortReadableName = this.sourceName;
        }
        final TypeVariableBinding[] typeVars;
        if ((typeVars = this.typeVariables()) != Binding.NO_TYPE_VARIABLES) {
            final StringBuffer nameBuffer = new StringBuffer(10);
            nameBuffer.append(shortReadableName).append('<');
            for (int i = 0, length = typeVars.length; i < length; ++i) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(typeVars[i].shortReadableName());
            }
            nameBuffer.append('>');
            final int nameLength = nameBuffer.length();
            shortReadableName = new char[nameLength];
            nameBuffer.getChars(0, nameLength, shortReadableName, 0);
        }
        return shortReadableName;
    }
    
    @Override
    public char[] signature() {
        if (this.signature != null) {
            return this.signature;
        }
        return this.signature = CharOperation.concat('L', this.constantPoolName(), ';');
    }
    
    @Override
    public char[] sourceName() {
        return this.sourceName;
    }
    
    void storeAnnotationHolder(final Binding binding, final AnnotationHolder holder) {
        if (holder == null) {
            final SimpleLookupTable store = this.storedAnnotations(false);
            if (store != null) {
                store.removeKey(binding);
            }
        }
        else {
            final SimpleLookupTable store = this.storedAnnotations(true);
            if (store != null) {
                store.put(binding, holder);
            }
        }
    }
    
    void storeAnnotations(final Binding binding, final AnnotationBinding[] annotations) {
        AnnotationHolder holder = null;
        if (annotations == null || annotations.length == 0) {
            final SimpleLookupTable store = this.storedAnnotations(false);
            if (store != null) {
                holder = (AnnotationHolder)store.get(binding);
            }
            if (holder == null) {
                return;
            }
        }
        else {
            final SimpleLookupTable store = this.storedAnnotations(true);
            if (store == null) {
                return;
            }
            holder = (AnnotationHolder)store.get(binding);
            if (holder == null) {
                holder = new AnnotationHolder();
            }
        }
        this.storeAnnotationHolder(binding, holder.setAnnotations(annotations));
    }
    
    SimpleLookupTable storedAnnotations(final boolean forceInitialize) {
        return null;
    }
    
    @Override
    public ReferenceBinding superclass() {
        return null;
    }
    
    @Override
    public ReferenceBinding[] superInterfaces() {
        return Binding.NO_SUPERINTERFACES;
    }
    
    public ReferenceBinding[] syntheticEnclosingInstanceTypes() {
        if (this.isStatic()) {
            return null;
        }
        final ReferenceBinding enclosingType = this.enclosingType();
        if (enclosingType == null) {
            return null;
        }
        return new ReferenceBinding[] { enclosingType };
    }
    
    MethodBinding[] unResolvedMethods() {
        return this.methods();
    }
    
    public FieldBinding[] unResolvedFields() {
        return Binding.NO_FIELDS;
    }
    
    protected int applyCloseableClassWhitelists() {
        switch (this.compoundName.length) {
            case 3: {
                if (CharOperation.equals(TypeConstants.JAVA, this.compoundName[0]) && CharOperation.equals(TypeConstants.IO, this.compoundName[1])) {
                    final char[] simpleName = this.compoundName[2];
                    for (int l = TypeConstants.JAVA_IO_WRAPPER_CLOSEABLES.length, i = 0; i < l; ++i) {
                        if (CharOperation.equals(simpleName, TypeConstants.JAVA_IO_WRAPPER_CLOSEABLES[i])) {
                            return 4;
                        }
                    }
                    for (int l = TypeConstants.JAVA_IO_RESOURCE_FREE_CLOSEABLES.length, i = 0; i < l; ++i) {
                        if (CharOperation.equals(simpleName, TypeConstants.JAVA_IO_RESOURCE_FREE_CLOSEABLES[i])) {
                            return 8;
                        }
                    }
                    break;
                }
                break;
            }
            case 4: {
                if (CharOperation.equals(TypeConstants.JAVA, this.compoundName[0]) && CharOperation.equals(TypeConstants.UTIL, this.compoundName[1]) && CharOperation.equals(TypeConstants.ZIP, this.compoundName[2])) {
                    final char[] simpleName = this.compoundName[3];
                    for (int l = TypeConstants.JAVA_UTIL_ZIP_WRAPPER_CLOSEABLES.length, i = 0; i < l; ++i) {
                        if (CharOperation.equals(simpleName, TypeConstants.JAVA_UTIL_ZIP_WRAPPER_CLOSEABLES[i])) {
                            return 4;
                        }
                    }
                    break;
                }
                break;
            }
        }
        for (int j = TypeConstants.OTHER_WRAPPER_CLOSEABLES.length, k = 0; k < j; ++k) {
            if (CharOperation.equals(this.compoundName, TypeConstants.OTHER_WRAPPER_CLOSEABLES[k])) {
                return 4;
            }
        }
        return 0;
    }
    
    protected int applyCloseableInterfaceWhitelists() {
        switch (this.compoundName.length) {
            case 4: {
                if (CharOperation.equals(this.compoundName, TypeConstants.RESOURCE_FREE_CLOSEABLE_STREAM)) {
                    return 8;
                }
                break;
            }
        }
        return 0;
    }
    
    protected MethodBinding[] getInterfaceAbstractContracts(final Scope scope, final boolean replaceWildcards) throws InvalidInputException {
        if (!this.isInterface() || !this.isValidBinding()) {
            throw new InvalidInputException("Not a functional interface");
        }
        final MethodBinding[] methods = this.methods();
        MethodBinding[] contracts = new MethodBinding[0];
        int contractsCount = 0;
        int contractsLength = 0;
        final ReferenceBinding[] superInterfaces = this.superInterfaces();
        for (int i = 0, length = superInterfaces.length; i < length; ++i) {
            final MethodBinding[] superInterfaceContracts = superInterfaces[i].getInterfaceAbstractContracts(scope, replaceWildcards);
            final int superInterfaceContractsLength = (superInterfaceContracts == null) ? 0 : superInterfaceContracts.length;
            if (superInterfaceContractsLength != 0) {
                if (contractsLength < contractsCount + superInterfaceContractsLength) {
                    System.arraycopy(contracts, 0, contracts = new MethodBinding[contractsLength = contractsCount + superInterfaceContractsLength], 0, contractsCount);
                }
                System.arraycopy(superInterfaceContracts, 0, contracts, contractsCount, superInterfaceContractsLength);
                contractsCount += superInterfaceContractsLength;
            }
        }
        for (int i = 0, length = (methods == null) ? 0 : methods.length; i < length; ++i) {
            final MethodBinding method = methods[i];
            if (method != null && !method.isStatic()) {
                if (!method.redeclaresPublicObjectMethod(scope)) {
                    if (!method.isValidBinding()) {
                        throw new InvalidInputException("Not a functional interface");
                    }
                    int j = 0;
                    while (j < contractsCount) {
                        if (contracts[j] != null && MethodVerifier.doesMethodOverride(method, contracts[j], scope.environment())) {
                            --contractsCount;
                            if (j < contractsCount) {
                                System.arraycopy(contracts, j + 1, contracts, j, contractsCount - j);
                                continue;
                            }
                        }
                        ++j;
                    }
                    if (!method.isDefaultMethod()) {
                        if (contractsCount == contractsLength) {
                            final MethodBinding[] array = contracts;
                            final int n = 0;
                            contractsLength += 16;
                            System.arraycopy(array, n, contracts = new MethodBinding[contractsLength], 0, contractsCount);
                        }
                        contracts[contractsCount++] = method;
                    }
                }
            }
        }
        if (contractsCount < contractsLength) {
            System.arraycopy(contracts, 0, contracts = new MethodBinding[contractsCount], 0, contractsCount);
        }
        return contracts;
    }
    
    @Override
    public MethodBinding getSingleAbstractMethod(final Scope scope, final boolean replaceWildcards) {
        final int index = replaceWildcards ? 0 : 1;
        if (this.singleAbstractMethod != null) {
            if (this.singleAbstractMethod[index] != null) {
                return this.singleAbstractMethod[index];
            }
        }
        else {
            this.singleAbstractMethod = new MethodBinding[2];
        }
        if (this.compoundName != null) {
            scope.compilationUnitScope().recordQualifiedReference(this.compoundName);
        }
        MethodBinding[] methods = null;
        try {
            methods = this.getInterfaceAbstractContracts(scope, replaceWildcards);
            if (methods == null || methods.length == 0) {
                return this.singleAbstractMethod[index] = ReferenceBinding.samProblemBinding;
            }
            int contractParameterLength = 0;
            char[] contractSelector = null;
            for (int i = 0, length = methods.length; i < length; ++i) {
                final MethodBinding method = methods[i];
                if (method != null) {
                    if (contractSelector == null) {
                        contractSelector = method.selector;
                        contractParameterLength = ((method.parameters == null) ? 0 : method.parameters.length);
                    }
                    else {
                        final int methodParameterLength = (method.parameters == null) ? 0 : method.parameters.length;
                        if (methodParameterLength != contractParameterLength || !CharOperation.equals(method.selector, contractSelector)) {
                            return this.singleAbstractMethod[index] = ReferenceBinding.samProblemBinding;
                        }
                    }
                }
            }
        }
        catch (final InvalidInputException ex) {
            return this.singleAbstractMethod[index] = ReferenceBinding.samProblemBinding;
        }
        if (methods.length == 1) {
            return this.singleAbstractMethod[index] = methods[0];
        }
        final LookupEnvironment environment = scope.environment();
        boolean genericMethodSeen = false;
        final int length2 = methods.length;
        final boolean analyseNullAnnotations = environment.globalOptions.isAnnotationBasedNullAnalysisEnabled;
        int j = length2 - 1;
    Label_0988:
        while (j >= 0) {
            MethodBinding method2 = methods[j];
            MethodBinding otherMethod = null;
            if (method2.typeVariables != Binding.NO_TYPE_VARIABLES) {
                genericMethodSeen = true;
            }
            TypeBinding returnType = method2.returnType;
            TypeBinding[] parameters = method2.parameters;
            for (int k = 0; k < length2; ++k) {
                if (j != k) {
                    otherMethod = methods[k];
                    if (otherMethod.typeVariables != Binding.NO_TYPE_VARIABLES) {
                        genericMethodSeen = true;
                    }
                    Label_0985: {
                        if (genericMethodSeen) {
                            otherMethod = MethodVerifier.computeSubstituteMethod(otherMethod, method2, environment);
                            if (otherMethod == null) {
                                break Label_0985;
                            }
                        }
                        if (MethodVerifier.isSubstituteParameterSubsignature(method2, otherMethod, environment)) {
                            if (MethodVerifier.areReturnTypesCompatible(method2, otherMethod, environment)) {
                                if (analyseNullAnnotations) {
                                    returnType = NullAnnotationMatching.strongerType(returnType, otherMethod.returnType, environment);
                                    parameters = NullAnnotationMatching.weakerTypes(parameters, otherMethod.parameters, environment);
                                }
                                continue;
                            }
                        }
                    }
                    --j;
                    continue Label_0988;
                }
            }
            ReferenceBinding[] exceptions = new ReferenceBinding[0];
            int exceptionsCount = 0;
            int exceptionsLength = 0;
            final MethodBinding theAbstractMethod = method2;
            final boolean shouldEraseThrows = theAbstractMethod.typeVariables == Binding.NO_TYPE_VARIABLES && genericMethodSeen;
            final boolean shouldAdaptThrows = theAbstractMethod.typeVariables != Binding.NO_TYPE_VARIABLES;
            final int typeVariableLength = theAbstractMethod.typeVariables.length;
        Label_0902:
            for (j = 0; j < length2; ++j) {
                method2 = methods[j];
                ReferenceBinding[] methodThrownExceptions = method2.thrownExceptions;
                final int methodExceptionsLength = (methodThrownExceptions == null) ? 0 : methodThrownExceptions.length;
                if (methodExceptionsLength == 0) {
                    break;
                }
                if (shouldAdaptThrows && method2 != theAbstractMethod) {
                    System.arraycopy(methodThrownExceptions, 0, methodThrownExceptions = new ReferenceBinding[methodExceptionsLength], 0, methodExceptionsLength);
                    for (int tv = 0; tv < typeVariableLength; ++tv) {
                        if (methodThrownExceptions[tv] instanceof TypeVariableBinding) {
                            methodThrownExceptions[tv] = theAbstractMethod.typeVariables[tv];
                        }
                    }
                }
                int l = 0;
            Label_0885:
                while (l < methodExceptionsLength) {
                    ReferenceBinding methodException = methodThrownExceptions[l];
                    if (shouldEraseThrows) {
                        methodException = (ReferenceBinding)methodException.erasure();
                    }
                    while (true) {
                    Label_0835:
                        for (int m = 0; m < length2; ++m) {
                            if (j != m) {
                                otherMethod = methods[m];
                                ReferenceBinding[] otherMethodThrownExceptions = otherMethod.thrownExceptions;
                                final int otherMethodExceptionsLength = (otherMethodThrownExceptions == null) ? 0 : otherMethodThrownExceptions.length;
                                if (otherMethodExceptionsLength == 0) {
                                    break Label_0902;
                                }
                                if (shouldAdaptThrows && otherMethod != theAbstractMethod) {
                                    System.arraycopy(otherMethodThrownExceptions, 0, otherMethodThrownExceptions = new ReferenceBinding[otherMethodExceptionsLength], 0, otherMethodExceptionsLength);
                                    for (int tv2 = 0; tv2 < typeVariableLength; ++tv2) {
                                        if (otherMethodThrownExceptions[tv2] instanceof TypeVariableBinding) {
                                            otherMethodThrownExceptions[tv2] = theAbstractMethod.typeVariables[tv2];
                                        }
                                    }
                                }
                                for (ReferenceBinding otherException : otherMethodThrownExceptions) {
                                    if (shouldEraseThrows) {
                                        otherException = (ReferenceBinding)otherException.erasure();
                                    }
                                    if (methodException.isCompatibleWith(otherException)) {
                                        continue Label_0835;
                                    }
                                }
                                ++l;
                                continue Label_0885;
                            }
                        }
                        if (exceptionsCount == exceptionsLength) {
                            final ReferenceBinding[] array = exceptions;
                            final int n = 0;
                            exceptionsLength += 16;
                            System.arraycopy(array, n, exceptions = new ReferenceBinding[exceptionsLength], 0, exceptionsCount);
                        }
                        exceptions[exceptionsCount++] = methodException;
                        continue;
                    }
                }
            }
            if (exceptionsCount != exceptionsLength) {
                System.arraycopy(exceptions, 0, exceptions = new ReferenceBinding[exceptionsCount], 0, exceptionsCount);
            }
            this.singleAbstractMethod[index] = new MethodBinding(theAbstractMethod.modifiers | 0x1000, theAbstractMethod.selector, returnType, parameters, exceptions, theAbstractMethod.declaringClass);
            this.singleAbstractMethod[index].typeVariables = theAbstractMethod.typeVariables;
            return this.singleAbstractMethod[index];
        }
        return this.singleAbstractMethod[index] = ReferenceBinding.samProblemBinding;
    }
    
    public static boolean isConsistentIntersection(final TypeBinding[] intersectingTypes) {
        final TypeBinding[] ci = new TypeBinding[intersectingTypes.length];
        for (int i = 0; i < ci.length; ++i) {
            final TypeBinding current = intersectingTypes[i];
            ci[i] = ((current.isClass() || current.isArrayType()) ? current : current.superclass());
        }
        TypeBinding mostSpecific = ci[0];
        for (int j = 1; j < ci.length; ++j) {
            final TypeBinding current2 = ci[j];
            if (!current2.isTypeVariable() && !current2.isWildcard()) {
                if (current2.isProperType(true)) {
                    if (!mostSpecific.isSubtypeOf(current2)) {
                        if (!current2.isSubtypeOf(mostSpecific)) {
                            return false;
                        }
                        mostSpecific = current2;
                    }
                }
            }
        }
        return true;
    }
}
