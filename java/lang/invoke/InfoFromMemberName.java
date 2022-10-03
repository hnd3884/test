package java.lang.invoke;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Member;

final class InfoFromMemberName implements MethodHandleInfo
{
    private final MemberName member;
    private final int referenceKind;
    
    InfoFromMemberName(final MethodHandles.Lookup lookup, final MemberName member, final byte referenceKind) {
        assert member.isResolved() || member.isMethodHandleInvoke();
        assert member.referenceKindIsConsistentWith(referenceKind);
        this.member = member;
        this.referenceKind = referenceKind;
    }
    
    @Override
    public Class<?> getDeclaringClass() {
        return this.member.getDeclaringClass();
    }
    
    @Override
    public String getName() {
        return this.member.getName();
    }
    
    @Override
    public MethodType getMethodType() {
        return this.member.getMethodOrFieldType();
    }
    
    @Override
    public int getModifiers() {
        return this.member.getModifiers();
    }
    
    @Override
    public int getReferenceKind() {
        return this.referenceKind;
    }
    
    @Override
    public String toString() {
        return MethodHandleInfo.toString(this.getReferenceKind(), this.getDeclaringClass(), this.getName(), this.getMethodType());
    }
    
    @Override
    public <T extends Member> T reflectAs(final Class<T> clazz, final MethodHandles.Lookup lookup) {
        if (this.member.isMethodHandleInvoke() && !this.member.isVarargs()) {
            throw new IllegalArgumentException("cannot reflect signature polymorphic method");
        }
        final Member member = AccessController.doPrivileged((PrivilegedAction<Member>)new PrivilegedAction<Member>() {
            @Override
            public Member run() {
                try {
                    return InfoFromMemberName.this.reflectUnchecked();
                }
                catch (final ReflectiveOperationException ex) {
                    throw new IllegalArgumentException(ex);
                }
            }
        });
        try {
            final Class<?> declaringClass = this.getDeclaringClass();
            final byte b = (byte)this.getReferenceKind();
            lookup.checkAccess(b, declaringClass, convertToMemberName(b, member));
        }
        catch (final IllegalAccessException ex) {
            throw new IllegalArgumentException(ex);
        }
        return clazz.cast(member);
    }
    
    private Member reflectUnchecked() throws ReflectiveOperationException {
        final byte b = (byte)this.getReferenceKind();
        final Class<?> declaringClass = this.getDeclaringClass();
        final boolean public1 = Modifier.isPublic(this.getModifiers());
        if (MethodHandleNatives.refKindIsMethod(b)) {
            if (public1) {
                return declaringClass.getMethod(this.getName(), (Class[])this.getMethodType().parameterArray());
            }
            return declaringClass.getDeclaredMethod(this.getName(), (Class[])this.getMethodType().parameterArray());
        }
        else if (MethodHandleNatives.refKindIsConstructor(b)) {
            if (public1) {
                return declaringClass.getConstructor((Class[])this.getMethodType().parameterArray());
            }
            return declaringClass.getDeclaredConstructor((Class[])this.getMethodType().parameterArray());
        }
        else {
            if (!MethodHandleNatives.refKindIsField(b)) {
                throw new IllegalArgumentException("referenceKind=" + b);
            }
            if (public1) {
                return declaringClass.getField(this.getName());
            }
            return declaringClass.getDeclaredField(this.getName());
        }
    }
    
    private static MemberName convertToMemberName(final byte b, final Member member) throws IllegalAccessException {
        if (member instanceof Method) {
            return new MemberName((Method)member, b == 7);
        }
        if (member instanceof Constructor) {
            return new MemberName((Constructor<?>)member);
        }
        if (member instanceof Field) {
            return new MemberName((Field)member, b == 3 || b == 4);
        }
        throw new InternalError(member.getClass().getName());
    }
}
