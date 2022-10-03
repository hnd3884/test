package org.tartarus.snowball;

import java.util.Locale;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandle;

public final class Among
{
    final int s_size;
    final char[] s;
    final int substring_i;
    final int result;
    final MethodHandle method;
    
    public Among(final String s, final int substring_i, final int result, final String methodname, final MethodHandles.Lookup methodobject) {
        this.s_size = s.length();
        this.s = s.toCharArray();
        this.substring_i = substring_i;
        this.result = result;
        if (methodname.isEmpty()) {
            this.method = null;
        }
        else {
            final Class<? extends SnowballProgram> clazz = methodobject.lookupClass().asSubclass(SnowballProgram.class);
            try {
                this.method = methodobject.findVirtual(clazz, methodname, MethodType.methodType(Boolean.TYPE)).asType(MethodType.methodType(Boolean.TYPE, SnowballProgram.class));
            }
            catch (final NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(String.format(Locale.ENGLISH, "Snowball program '%s' is broken, cannot access method: boolean %s()", clazz.getSimpleName(), methodname), e);
            }
        }
    }
}
