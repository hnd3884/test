package jdk.jfr.internal.settings;

import jdk.jfr.internal.Type;
import java.util.Iterator;
import jdk.jfr.internal.Utils;
import java.util.Set;
import java.util.Objects;
import jdk.jfr.internal.PlatformEventType;
import jdk.jfr.Name;
import jdk.jfr.Description;
import jdk.jfr.Label;
import jdk.jfr.MetadataDefinition;
import jdk.jfr.internal.Control;

@MetadataDefinition
@Label("Period")
@Description("Record event at interval")
@Name("jdk.settings.Period")
public final class PeriodSetting extends Control
{
    private static final long typeId;
    public static final String EVERY_CHUNK = "everyChunk";
    public static final String BEGIN_CHUNK = "beginChunk";
    public static final String END_CHUNK = "endChunk";
    public static final String NAME = "period";
    private final PlatformEventType eventType;
    private String value;
    
    public PeriodSetting(final PlatformEventType platformEventType, final String s) {
        super(s);
        this.value = "everyChunk";
        this.eventType = Objects.requireNonNull(platformEventType);
    }
    
    @Override
    public String combine(final Set<String> set) {
        boolean b = false;
        boolean b2 = false;
        Long n = null;
        String s = null;
        for (final String s3 : set) {
            final String s2 = s3;
            switch (s3) {
                case "everyChunk": {
                    b = true;
                    b2 = true;
                    continue;
                }
                case "beginChunk": {
                    b = true;
                    continue;
                }
                case "endChunk": {
                    b2 = true;
                    continue;
                }
                default: {
                    final long timespanWithInfinity = Utils.parseTimespanWithInfinity(s2);
                    if (n == null) {
                        s = s2;
                        n = timespanWithInfinity;
                        continue;
                    }
                    if (timespanWithInfinity < n) {
                        s = s2;
                        n = timespanWithInfinity;
                        continue;
                    }
                    continue;
                }
            }
        }
        if (n != null) {
            return s;
        }
        if (b && !b2) {
            return "beginChunk";
        }
        if (!b && b2) {
            return "endChunk";
        }
        return "everyChunk";
    }
    
    @Override
    public void setValue(final String value) {
        switch (value) {
            case "everyChunk": {
                this.eventType.setPeriod(0L, true, true);
                break;
            }
            case "beginChunk": {
                this.eventType.setPeriod(0L, true, false);
                break;
            }
            case "endChunk": {
                this.eventType.setPeriod(0L, false, true);
                break;
            }
            default: {
                final long timespanWithInfinity = Utils.parseTimespanWithInfinity(value);
                if (timespanWithInfinity != Long.MAX_VALUE) {
                    this.eventType.setPeriod(timespanWithInfinity / 1000000L, false, false);
                    break;
                }
                this.eventType.setPeriod(Long.MAX_VALUE, false, false);
                break;
            }
        }
        this.value = value;
    }
    
    @Override
    public String getValue() {
        return this.value;
    }
    
    public static boolean isType(final long n) {
        return PeriodSetting.typeId == n;
    }
    
    static {
        typeId = Type.getTypeId(PeriodSetting.class);
    }
}
