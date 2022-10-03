package jdk.jfr.internal.settings;

import jdk.jfr.internal.Type;
import java.util.Set;
import java.util.Objects;
import jdk.jfr.internal.PlatformEventType;
import jdk.jfr.BooleanFlag;
import jdk.jfr.Description;
import jdk.jfr.Name;
import jdk.jfr.Label;
import jdk.jfr.MetadataDefinition;
import jdk.jfr.internal.Control;

@MetadataDefinition
@Label("Stack Trace")
@Name("jdk.settings.StackTrace")
@Description("Record stack traces")
@BooleanFlag
public final class StackTraceSetting extends Control
{
    private static final long typeId;
    private final BooleanValue booleanValue;
    private final PlatformEventType eventType;
    
    public StackTraceSetting(final PlatformEventType platformEventType, final String s) {
        super(s);
        this.booleanValue = BooleanValue.valueOf(s);
        this.eventType = Objects.requireNonNull(platformEventType);
    }
    
    @Override
    public String combine(final Set<String> set) {
        return this.booleanValue.union(set);
    }
    
    @Override
    public void setValue(final String value) {
        this.booleanValue.setValue(value);
        this.eventType.setStackTraceEnabled(this.booleanValue.getBoolean());
    }
    
    @Override
    public String getValue() {
        return this.booleanValue.getValue();
    }
    
    public static boolean isType(final long n) {
        return StackTraceSetting.typeId == n;
    }
    
    static {
        typeId = Type.getTypeId(StackTraceSetting.class);
    }
}
