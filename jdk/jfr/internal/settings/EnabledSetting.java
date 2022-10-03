package jdk.jfr.internal.settings;

import java.util.Set;
import java.util.Objects;
import jdk.jfr.internal.PlatformEventType;
import jdk.jfr.BooleanFlag;
import jdk.jfr.Name;
import jdk.jfr.Description;
import jdk.jfr.Label;
import jdk.jfr.MetadataDefinition;
import jdk.jfr.internal.Control;

@MetadataDefinition
@Label("Enabled")
@Description("Record event")
@Name("jdk.settings.Enabled")
@BooleanFlag
public final class EnabledSetting extends Control
{
    private final BooleanValue booleanValue;
    private final PlatformEventType eventType;
    
    public EnabledSetting(final PlatformEventType platformEventType, final String s) {
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
        this.eventType.setEnabled(this.booleanValue.getBoolean());
        if (this.eventType.isEnabled() && !this.eventType.isJVM() && !this.eventType.isInstrumented()) {
            this.eventType.markForInstrumentation(true);
        }
    }
    
    @Override
    public String getValue() {
        return this.booleanValue.getValue();
    }
}
