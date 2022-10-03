package jdk.jfr.events;

import jdk.jfr.Description;
import jdk.jfr.Name;
import jdk.jfr.Label;
import jdk.jfr.Category;

@Category({ "Java Development Kit", "Security" })
@Label("Security Property Modification")
@Name("jdk.SecurityPropertyModification")
@Description("Modification of Security property")
public final class SecurityPropertyModificationEvent extends AbstractJDKEvent
{
    @Label("Key")
    public String key;
    @Label("Value")
    public String value;
}
