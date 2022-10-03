package jdk.internal.org.objectweb.asm.util;

import jdk.internal.org.objectweb.asm.Label;
import java.util.Map;

public interface Textifiable
{
    void textify(final StringBuffer p0, final Map<Label, String> p1);
}
