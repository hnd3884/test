package jdk.jfr.internal.settings;

import jdk.jfr.internal.Type;
import java.util.Iterator;
import jdk.jfr.internal.Utils;
import java.util.Set;
import java.util.Objects;
import jdk.jfr.internal.PlatformEventType;
import jdk.jfr.Timespan;
import jdk.jfr.Description;
import jdk.jfr.Name;
import jdk.jfr.Label;
import jdk.jfr.MetadataDefinition;
import jdk.jfr.internal.Control;

@MetadataDefinition
@Label("Threshold")
@Name("jdk.settings.Threshold")
@Description("Record event with duration above or equal to threshold")
@Timespan
public final class ThresholdSetting extends Control
{
    private static final long typeId;
    private String value;
    private final PlatformEventType eventType;
    
    public ThresholdSetting(final PlatformEventType platformEventType, final String s) {
        super(s);
        this.value = "0 ns";
        this.eventType = Objects.requireNonNull(platformEventType);
    }
    
    @Override
    public String combine(final Set<String> set) {
        Long n = null;
        String s = null;
        for (final String s2 : set) {
            final long timespanWithInfinity = Utils.parseTimespanWithInfinity(s2);
            if (n == null) {
                n = timespanWithInfinity;
                s = s2;
            }
            else {
                if (timespanWithInfinity >= n) {
                    continue;
                }
                s = s2;
                n = timespanWithInfinity;
            }
        }
        return (s == null) ? "0 ns" : s;
    }
    
    @Override
    public void setValue(final String value) {
        final long timespanWithInfinity = Utils.parseTimespanWithInfinity(value);
        this.value = value;
        this.eventType.setThreshold(timespanWithInfinity);
    }
    
    @Override
    public String getValue() {
        return this.value;
    }
    
    public static boolean isType(final long n) {
        return ThresholdSetting.typeId == n;
    }
    
    static {
        typeId = Type.getTypeId(ThresholdSetting.class);
    }
}
