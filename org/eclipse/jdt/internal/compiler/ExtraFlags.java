package org.eclipse.jdt.internal.compiler;

import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.compiler.env.IBinaryNestedType;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;

public final class ExtraFlags
{
    public static final int HasNonPrivateStaticMemberTypes = 1;
    public static final int IsMemberType = 2;
    public static final int IsLocalType = 4;
    public static final int ParameterTypesStoredAsSignature = 16;
    
    public static int getExtraFlags(final ClassFileReader reader) {
        int extraFlags = 0;
        if (reader.isNestedType()) {
            extraFlags |= 0x2;
        }
        if (reader.isLocal()) {
            extraFlags |= 0x4;
        }
        final IBinaryNestedType[] memberTypes = reader.getMemberTypes();
        final int memberTypeCounter = (memberTypes == null) ? 0 : memberTypes.length;
        if (memberTypeCounter > 0) {
            for (int i = 0; i < memberTypeCounter; ++i) {
                final int modifiers = memberTypes[i].getModifiers();
                if ((modifiers & 0x8) != 0x0 && (modifiers & 0x2) == 0x0) {
                    extraFlags |= 0x1;
                    break;
                }
            }
        }
        return extraFlags;
    }
    
    public static int getExtraFlags(final IType type) throws JavaModelException {
        int extraFlags = 0;
        if (type.isMember()) {
            extraFlags |= 0x2;
        }
        if (type.isLocal()) {
            extraFlags |= 0x4;
        }
        final IType[] memberTypes = type.getTypes();
        final int memberTypeCounter = (memberTypes == null) ? 0 : memberTypes.length;
        if (memberTypeCounter > 0) {
            for (int i = 0; i < memberTypeCounter; ++i) {
                final int flags = memberTypes[i].getFlags();
                if ((flags & 0x8) != 0x0 && (flags & 0x2) == 0x0) {
                    extraFlags |= 0x1;
                    break;
                }
            }
        }
        return extraFlags;
    }
    
    public static int getExtraFlags(final TypeDeclaration typeDeclaration) {
        int extraFlags = 0;
        if (typeDeclaration.enclosingType != null) {
            extraFlags |= 0x2;
        }
        final TypeDeclaration[] memberTypes = typeDeclaration.memberTypes;
        final int memberTypeCounter = (memberTypes == null) ? 0 : memberTypes.length;
        if (memberTypeCounter > 0) {
            for (int i = 0; i < memberTypeCounter; ++i) {
                final int modifiers = memberTypes[i].modifiers;
                if ((modifiers & 0x8) != 0x0 && (modifiers & 0x2) == 0x0) {
                    extraFlags |= 0x1;
                    break;
                }
            }
        }
        return extraFlags;
    }
}
