package jdk.jfr.internal.settings;

import jdk.jfr.internal.Type;
import java.util.Iterator;
import jdk.jfr.internal.Utils;
import java.util.Set;
import java.util.Objects;
import jdk.jfr.internal.PlatformEventType;
import jdk.jfr.Timespan;
import jdk.jfr.Name;
import jdk.jfr.Description;
import jdk.jfr.Label;
import jdk.jfr.MetadataDefinition;
import jdk.jfr.internal.Control;

@MetadataDefinition
@Label("Cutoff")
@Description("Limit running time of event")
@Name("jdk.settings.Cutoff")
@Timespan
public final class CutoffSetting extends Control
{
    private static final long typeId;
    private String value;
    private final PlatformEventType eventType;
    
    public CutoffSetting(final PlatformEventType platformEventType, final String s) {
        super(s);
        this.value = "0 ns";
        this.eventType = Objects.requireNonNull(platformEventType);
    }
    
    @Override
    public String combine(final Set<String> set) {
        long n = 0L;
        String s = "0 ns";
        for (final String s2 : set) {
            final long timespanWithInfinity = Utils.parseTimespanWithInfinity(s2);
            if (timespanWithInfinity > n) {
                s = s2;
                n = timespanWithInfinity;
            }
        }
        return s;
    }
    
    @Override
    public void setValue(final String value) {
        final long timespanWithInfinity = Utils.parseTimespanWithInfinity(value);
        this.value = value;
        this.eventType.setCutoff(timespanWithInfinity);
    }
    
    @Override
    public String getValue() {
        return this.value;
    }
    
    public static boolean isType(final long n) {
        return CutoffSetting.typeId == n;
    }
    
    public static long parseValueSafe(final String s) {
        if (s == null) {
            return 0L;
        }
        try {
            return Utils.parseTimespanWithInfinity(s);
        }
        catch (final NumberFormatException ex) {
            return 0L;
        }
    }
    
    static {
        typeId = Type.getTypeId(CutoffSetting.class);
    }
}
