package jdk.jfr.events;

import jdk.jfr.Description;
import jdk.jfr.Category;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Name("jdk.JavaExceptionThrow")
@Label("Java Exception")
@Category({ "Java Application" })
@Description("An object derived from java.lang.Exception has been created")
public final class ExceptionThrownEvent extends AbstractJDKEvent
{
    @Label("Message")
    public String message;
    @Label("Class")
    public Class<?> thrownClass;
}
