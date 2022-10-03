package jdk.nashorn.api.scripting;

import jdk.Exported;

@Exported
public interface ClassFilter
{
    boolean exposeToScripts(final String p0);
}
