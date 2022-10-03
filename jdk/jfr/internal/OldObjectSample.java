package jdk.jfr.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import jdk.jfr.RecordingState;
import java.util.List;
import jdk.jfr.internal.test.WhiteBox;
import jdk.jfr.internal.settings.CutoffSetting;

public final class OldObjectSample
{
    private static final String EVENT_NAME = "jdk.OldObjectSample";
    private static final String OLD_OBJECT_CUTOFF = "jdk.OldObjectSample#cutoff";
    private static final String OLD_OBJECT_ENABLED = "jdk.OldObjectSample#enabled";
    
    public static void emit(final PlatformRecording platformRecording) {
        if (isEnabled(platformRecording)) {
            JVM.getJVM().emitOldObjectSamples(Utils.nanosToTicks(CutoffSetting.parseValueSafe(platformRecording.getSettings().get("jdk.OldObjectSample#cutoff"))), WhiteBox.getWriteAllObjectSamples());
        }
    }
    
    public static void emit(final List<PlatformRecording> list, final Boolean b) {
        boolean b2 = false;
        long max = Boolean.TRUE.equals(b) ? Long.MAX_VALUE : 0L;
        for (final PlatformRecording platformRecording : list) {
            if (platformRecording.getState() == RecordingState.RUNNING && isEnabled(platformRecording)) {
                b2 = true;
                max = Math.max(CutoffSetting.parseValueSafe(platformRecording.getSettings().get("jdk.OldObjectSample#cutoff")), max);
            }
        }
        if (b2) {
            JVM.getJVM().emitOldObjectSamples(Utils.nanosToTicks(max), WhiteBox.getWriteAllObjectSamples());
        }
    }
    
    public static void updateSettingPathToGcRoots(final Map<String, String> map, final Boolean b) {
        if (b != null) {
            map.put("jdk.OldObjectSample#cutoff", ((boolean)b) ? "infinity" : "0 ns");
        }
    }
    
    public static Map<String, String> createSettingsForSnapshot(final PlatformRecording platformRecording, final Boolean b) {
        final HashMap hashMap = new HashMap((Map<? extends K, ? extends V>)platformRecording.getSettings());
        updateSettingPathToGcRoots(hashMap, b);
        return hashMap;
    }
    
    private static boolean isEnabled(final PlatformRecording platformRecording) {
        return "true".equals(platformRecording.getSettings().get("jdk.OldObjectSample#enabled"));
    }
}
