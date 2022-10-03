package jdk.internal.org.objectweb.asm.util;

import jdk.internal.org.objectweb.asm.Label;
import java.util.Map;

public interface ASMifiable
{
    void asmify(final StringBuffer p0, final String p1, final Map<Label, String> p2);
}
