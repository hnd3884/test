package jdk.jfr.events;

import jdk.jfr.Description;
import jdk.jfr.Category;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Name("jdk.JavaErrorThrow")
@Label("Java Error")
@Category({ "Java Application" })
@Description("An object derived from java.lang.Error has been created. OutOfMemoryErrors are ignored")
public final class ErrorThrownEvent extends AbstractJDKEvent
{
    @Label("Message")
    public String message;
    @Label("Class")
    public Class<?> thrownClass;
}
