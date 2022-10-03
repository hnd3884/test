package jdk.jfr.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.Collection;
import jdk.jfr.internal.handlers.EventHandler;
import java.util.ArrayList;
import jdk.jfr.Event;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

final class SettingsManager
{
    private Map<String, InternalSetting> availableSettings;
    
    SettingsManager() {
        this.availableSettings = new LinkedHashMap<String, InternalSetting>();
    }
    
    void setSettings(final List<Map<String, String>> list) {
        this.availableSettings = this.createSettingsMap(list);
        final List<EventControl> eventControls = MetadataRepository.getInstance().getEventControls();
        if (!JVM.getJVM().isRecording()) {
            final Iterator<EventControl> iterator = eventControls.iterator();
            while (iterator.hasNext()) {
                iterator.next().disable();
            }
        }
        else {
            if (Logger.shouldLog(LogTag.JFR_SETTING, LogLevel.INFO)) {
                Collections.sort((List<Object>)eventControls, (eventControl, eventControl2) -> eventControl.getEventType().getName().compareTo(eventControl2.getEventType().getName()));
            }
            final Iterator<EventControl> iterator2 = eventControls.iterator();
            while (iterator2.hasNext()) {
                this.setEventControl(iterator2.next());
            }
        }
        if (JVM.getJVM().getAllowedToDoEventRetransforms()) {
            this.updateRetransform(JVM.getJVM().getAllEventClasses());
        }
    }
    
    public void updateRetransform(final List<Class<? extends Event>> list) {
        final ArrayList list2 = new ArrayList();
        for (final Class clazz : list) {
            final EventHandler handler = Utils.getHandler(clazz);
            if (handler != null) {
                final PlatformEventType platformEventType = handler.getPlatformEventType();
                if (!platformEventType.isMarkedForInstrumentation()) {
                    continue;
                }
                list2.add(clazz);
                platformEventType.markForInstrumentation(false);
                platformEventType.setInstrumented();
            }
        }
        if (!list2.isEmpty()) {
            JVM.getJVM().retransformClasses((Class<?>[])list2.toArray(new Class[0]));
        }
    }
    
    private Map<String, InternalSetting> createSettingsMap(final List<Map<String, String>> list) {
        final LinkedHashMap linkedHashMap = new LinkedHashMap(list.size());
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            for (final InternalSetting internalSetting : this.makeInternalSettings((Map<String, String>)iterator.next())) {
                final InternalSetting internalSetting2 = (InternalSetting)linkedHashMap.get(internalSetting.getSettingsId());
                if (internalSetting2 == null) {
                    linkedHashMap.put(internalSetting.getSettingsId(), internalSetting);
                }
                else {
                    internalSetting2.add(internalSetting);
                }
            }
        }
        return linkedHashMap;
    }
    
    private Collection<InternalSetting> makeInternalSettings(final Map<String, String> map) {
        final LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (final Map.Entry entry : map.entrySet()) {
            final String s = (String)entry.getKey();
            final String s2 = (String)entry.getValue();
            final int index = s.indexOf("#");
            if (index > 1 && index < s.length() - 2) {
                final String upgradeLegacyJDKEvent = Utils.upgradeLegacyJDKEvent(s.substring(0, index));
                InternalSetting internalSetting = (InternalSetting)linkedHashMap.get(upgradeLegacyJDKEvent);
                final String trim = s.substring(index + 1).trim();
                if (internalSetting == null) {
                    internalSetting = new InternalSetting(upgradeLegacyJDKEvent);
                    linkedHashMap.put(upgradeLegacyJDKEvent, internalSetting);
                }
                internalSetting.add(trim, s2);
            }
        }
        final Iterator iterator2 = linkedHashMap.values().iterator();
        while (iterator2.hasNext()) {
            ((InternalSetting)iterator2.next()).finish();
        }
        return linkedHashMap.values();
    }
    
    void setEventControl(final EventControl eventControl) {
        final InternalSetting internalSetting = this.getInternalSetting(eventControl);
        Logger.log(LogTag.JFR_SETTING, LogLevel.INFO, "Applied settings for " + eventControl.getEventType().getLogName() + " {");
        for (final Map.Entry entry : eventControl.getEntries()) {
            Set<String> values = null;
            final String s = (String)entry.getKey();
            if (internalSetting != null) {
                values = internalSetting.getValues(s);
            }
            final Control control = (Control)entry.getValue();
            if (values != null) {
                control.apply(values);
                final String lastValue = control.getLastValue();
                if (!Logger.shouldLog(LogTag.JFR_SETTING, LogLevel.INFO) || !Utils.isSettingVisible(control, eventControl.getEventType().hasEventHook())) {
                    continue;
                }
                if (values.size() > 1) {
                    final StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
                    final Iterator<String> iterator2 = values.iterator();
                    while (iterator2.hasNext()) {
                        stringJoiner.add("\"" + iterator2.next() + "\"");
                    }
                    Logger.log(LogTag.JFR_SETTING, LogLevel.INFO, "  " + s + "= " + stringJoiner.toString() + " => \"" + lastValue + "\"");
                }
                else {
                    Logger.log(LogTag.JFR_SETTING, LogLevel.INFO, "  " + s + "=\"" + control.getLastValue() + "\"");
                }
            }
            else {
                control.setDefault();
                if (!Logger.shouldLog(LogTag.JFR_SETTING, LogLevel.INFO)) {
                    continue;
                }
                Logger.log(LogTag.JFR_SETTING, LogLevel.INFO, "  " + s + "=\"" + control.getLastValue() + "\"");
            }
        }
        eventControl.writeActiveSettingEvent();
        Logger.log(LogTag.JFR_SETTING, LogLevel.INFO, "}");
    }
    
    private InternalSetting getInternalSetting(final EventControl eventControl) {
        final InternalSetting internalSetting = this.availableSettings.get(eventControl.getEventType().getName());
        final InternalSetting internalSetting2 = this.availableSettings.get(eventControl.getSettingsId());
        if (internalSetting == null && internalSetting2 == null) {
            return null;
        }
        if (internalSetting2 == null) {
            return internalSetting;
        }
        if (internalSetting == null) {
            return internalSetting2;
        }
        final InternalSetting internalSetting3 = new InternalSetting(internalSetting.getSettingsId());
        internalSetting3.add(internalSetting);
        internalSetting3.add(internalSetting2);
        return internalSetting3;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final Iterator<InternalSetting> iterator = this.availableSettings.values().iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next().toString());
            sb.append("\n");
        }
        return sb.toString();
    }
    
    boolean isEnabled(final String s) {
        final InternalSetting internalSetting = this.availableSettings.get(s);
        return internalSetting != null && internalSetting.isEnabled();
    }
    
    private static class InternalSetting
    {
        private final String identifier;
        private Map<String, Set<String>> enabledMap;
        private Map<String, Set<String>> allMap;
        private boolean enabled;
        
        public InternalSetting(final String identifier) {
            this.enabledMap = new LinkedHashMap<String, Set<String>>(5);
            this.allMap = new LinkedHashMap<String, Set<String>>(5);
            this.identifier = identifier;
        }
        
        public Set<String> getValues(final String s) {
            if (this.enabled) {
                return this.enabledMap.get(s);
            }
            return this.allMap.get(s);
        }
        
        public void add(final String s, final String s2) {
            if ("enabled".equals(s) && "true".equals(s2)) {
                this.enabled = true;
                this.allMap = null;
            }
            this.addToMap(this.enabledMap, s, s2);
            if (this.allMap != null) {
                this.addToMap(this.allMap, s, s2);
            }
        }
        
        private void addToMap(final Map<String, Set<String>> map, final String s, final String s2) {
            Set set = map.get(s);
            if (set == null) {
                set = new HashSet(5);
                map.put(s, set);
            }
            set.add(s2);
        }
        
        public String getSettingsId() {
            return this.identifier;
        }
        
        public void add(final InternalSetting internalSetting) {
            for (final Map.Entry entry : internalSetting.enabledMap.entrySet()) {
                final Iterator iterator2 = ((Set)entry.getValue()).iterator();
                while (iterator2.hasNext()) {
                    this.add((String)entry.getKey(), (String)iterator2.next());
                }
            }
        }
        
        public boolean isEnabled() {
            return this.enabled;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(this.identifier);
            sb.append(": ");
            sb.append(this.enabledMap.toString());
            return sb.toString();
        }
        
        public void finish() {
            if (!this.enabled) {
                final HashMap enabledMap = new HashMap(2);
                final HashSet set = new HashSet(2);
                set.add("false");
                enabledMap.put("enabled", set);
                this.enabledMap = enabledMap;
            }
        }
    }
}
