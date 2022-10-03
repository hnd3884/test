package com.turo.pushy.apns.util;

import com.google.gson.GsonBuilder;
import java.util.Collections;
import java.util.Iterator;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import com.google.gson.Gson;
import java.util.HashMap;
import java.io.CharArrayWriter;

public class ApnsPayloadBuilder
{
    private String alertBody;
    private String localizedAlertKey;
    private String[] localizedAlertArguments;
    private String alertTitle;
    private String localizedAlertTitleKey;
    private String[] localizedAlertTitleArguments;
    private String alertSubtitle;
    private String localizedAlertSubtitleKey;
    private String[] localizedAlertSubtitleArguments;
    private String launchImageFileName;
    private boolean showActionButton;
    private String actionButtonLabel;
    private String localizedActionButtonKey;
    private Integer badgeNumber;
    private String soundFileName;
    private SoundForCriticalAlert soundForCriticalAlert;
    private String categoryName;
    private boolean contentAvailable;
    private boolean mutableContent;
    private String threadId;
    private String targetContentId;
    private String summaryArgument;
    private Integer summaryArgumentCount;
    private String[] urlArguments;
    private boolean preferStringRepresentationForAlerts;
    private final CharArrayWriter buffer;
    private static final String APS_KEY = "aps";
    private static final String ALERT_KEY = "alert";
    private static final String BADGE_KEY = "badge";
    private static final String SOUND_KEY = "sound";
    private static final String CATEGORY_KEY = "category";
    private static final String CONTENT_AVAILABLE_KEY = "content-available";
    private static final String MUTABLE_CONTENT_KEY = "mutable-content";
    private static final String THREAD_ID_KEY = "thread-id";
    private static final String TARGET_CONTENT_ID_KEY = "target-content-id";
    private static final String SUMMARY_ARGUMENT_KEY = "summary-arg";
    private static final String SUMMARY_ARGUMENT_COUNT_KEY = "summary-arg-count";
    private static final String URL_ARGS_KEY = "url-args";
    private static final String ALERT_TITLE_KEY = "title";
    private static final String ALERT_TITLE_LOC_KEY = "title-loc-key";
    private static final String ALERT_TITLE_ARGS_KEY = "title-loc-args";
    private static final String ALERT_SUBTITLE_KEY = "subtitle";
    private static final String ALERT_SUBTITLE_LOC_KEY = "subtitle-loc-key";
    private static final String ALERT_SUBTITLE_ARGS_KEY = "subtitle-loc-args";
    private static final String ALERT_BODY_KEY = "body";
    private static final String ALERT_LOC_KEY = "loc-key";
    private static final String ALERT_ARGS_KEY = "loc-args";
    private static final String ACTION_KEY = "action";
    private static final String ACTION_LOC_KEY = "action-loc-key";
    private static final String LAUNCH_IMAGE_KEY = "launch-image";
    private static final String MDM_KEY = "mdm";
    private final HashMap<String, Object> customProperties;
    private static final String ABBREVIATION_SUBSTRING = "\u2026";
    private static final Gson GSON;
    public static final String DEFAULT_SOUND_FILENAME = "default";
    public static final int DEFAULT_MAXIMUM_PAYLOAD_SIZE = 4096;
    
    public ApnsPayloadBuilder() {
        this.alertBody = null;
        this.localizedAlertKey = null;
        this.localizedAlertArguments = null;
        this.alertTitle = null;
        this.localizedAlertTitleKey = null;
        this.localizedAlertTitleArguments = null;
        this.alertSubtitle = null;
        this.localizedAlertSubtitleKey = null;
        this.localizedAlertSubtitleArguments = null;
        this.launchImageFileName = null;
        this.showActionButton = true;
        this.actionButtonLabel = null;
        this.localizedActionButtonKey = null;
        this.badgeNumber = null;
        this.soundFileName = null;
        this.soundForCriticalAlert = null;
        this.categoryName = null;
        this.contentAvailable = false;
        this.mutableContent = false;
        this.threadId = null;
        this.targetContentId = null;
        this.summaryArgument = null;
        this.summaryArgumentCount = null;
        this.urlArguments = null;
        this.preferStringRepresentationForAlerts = false;
        this.buffer = new CharArrayWriter(1024);
        this.customProperties = new HashMap<String, Object>();
    }
    
    public ApnsPayloadBuilder setPreferStringRepresentationForAlerts(final boolean preferStringRepresentationForAlerts) {
        this.preferStringRepresentationForAlerts = preferStringRepresentationForAlerts;
        return this;
    }
    
    public ApnsPayloadBuilder setAlertBody(final String alertBody) {
        this.alertBody = alertBody;
        this.localizedAlertKey = null;
        this.localizedAlertArguments = null;
        return this;
    }
    
    public ApnsPayloadBuilder setLocalizedAlertMessage(final String localizedAlertKey, final String... alertArguments) {
        this.localizedAlertKey = localizedAlertKey;
        this.localizedAlertArguments = (String[])((alertArguments != null && alertArguments.length > 0) ? alertArguments : null);
        this.alertBody = null;
        return this;
    }
    
    public ApnsPayloadBuilder setAlertTitle(final String alertTitle) {
        this.alertTitle = alertTitle;
        this.localizedAlertTitleKey = null;
        this.localizedAlertTitleArguments = null;
        return this;
    }
    
    public ApnsPayloadBuilder setLocalizedAlertTitle(final String localizedAlertTitleKey, final String... alertTitleArguments) {
        this.localizedAlertTitleKey = localizedAlertTitleKey;
        this.localizedAlertTitleArguments = (String[])((alertTitleArguments != null && alertTitleArguments.length > 0) ? alertTitleArguments : null);
        this.alertTitle = null;
        return this;
    }
    
    public ApnsPayloadBuilder setAlertSubtitle(final String alertSubtitle) {
        this.alertSubtitle = alertSubtitle;
        this.localizedAlertSubtitleKey = null;
        this.localizedAlertSubtitleArguments = null;
        return this;
    }
    
    public ApnsPayloadBuilder setLocalizedAlertSubtitle(final String localizedAlertSubtitleKey, final String... alertSubtitleArguments) {
        this.localizedAlertSubtitleKey = localizedAlertSubtitleKey;
        this.localizedAlertSubtitleArguments = (String[])((alertSubtitleArguments != null && alertSubtitleArguments.length > 0) ? alertSubtitleArguments : null);
        this.alertSubtitle = null;
        return this;
    }
    
    public ApnsPayloadBuilder setLaunchImageFileName(final String launchImageFilename) {
        this.launchImageFileName = launchImageFilename;
        return this;
    }
    
    public ApnsPayloadBuilder setShowActionButton(final boolean showActionButton) {
        this.showActionButton = showActionButton;
        return this;
    }
    
    public ApnsPayloadBuilder setActionButtonLabel(final String action) {
        this.actionButtonLabel = action;
        this.localizedActionButtonKey = null;
        return this;
    }
    
    public ApnsPayloadBuilder setLocalizedActionButtonKey(final String localizedActionButtonKey) {
        this.localizedActionButtonKey = localizedActionButtonKey;
        this.actionButtonLabel = null;
        return this;
    }
    
    public ApnsPayloadBuilder setBadgeNumber(final Integer badgeNumber) {
        this.badgeNumber = badgeNumber;
        return this;
    }
    
    public ApnsPayloadBuilder setCategoryName(final String categoryName) {
        this.categoryName = categoryName;
        return this;
    }
    
    @Deprecated
    public ApnsPayloadBuilder setSoundFileName(final String soundFileName) {
        return this.setSound(soundFileName);
    }
    
    public ApnsPayloadBuilder setSound(final String soundFileName) {
        this.soundFileName = soundFileName;
        this.soundForCriticalAlert = null;
        return this;
    }
    
    public ApnsPayloadBuilder setSound(final String soundFileName, final boolean isCriticalAlert, final double soundVolume) {
        Objects.requireNonNull(soundFileName, "Sound file name must not be null.");
        if (soundVolume < 0.0 || soundVolume > 1.0) {
            throw new IllegalArgumentException("Sound volume must be between 0.0 and 1.0 (inclusive).");
        }
        this.soundFileName = null;
        this.soundForCriticalAlert = new SoundForCriticalAlert(soundFileName, isCriticalAlert, soundVolume);
        return this;
    }
    
    public ApnsPayloadBuilder setContentAvailable(final boolean contentAvailable) {
        this.contentAvailable = contentAvailable;
        return this;
    }
    
    public ApnsPayloadBuilder setMutableContent(final boolean mutableContent) {
        this.mutableContent = mutableContent;
        return this;
    }
    
    public ApnsPayloadBuilder setThreadId(final String threadId) {
        this.threadId = threadId;
        return this;
    }
    
    public ApnsPayloadBuilder setTargetContentId(final String targetContentId) {
        this.targetContentId = targetContentId;
        return this;
    }
    
    public ApnsPayloadBuilder setSummaryArgument(final String summaryArgument) {
        this.summaryArgument = summaryArgument;
        return this;
    }
    
    public ApnsPayloadBuilder setSummaryArgumentCount(final Integer summaryArgumentCount) {
        if (summaryArgumentCount != null && summaryArgumentCount < 1) {
            throw new IllegalArgumentException("Summary argument count must be positive.");
        }
        this.summaryArgumentCount = summaryArgumentCount;
        return this;
    }
    
    public ApnsPayloadBuilder setUrlArguments(final List<String> arguments) {
        return this.setUrlArguments((String[])((arguments != null) ? ((String[])arguments.toArray(new String[0])) : null));
    }
    
    public ApnsPayloadBuilder setUrlArguments(final String... arguments) {
        this.urlArguments = arguments;
        return this;
    }
    
    public ApnsPayloadBuilder addCustomProperty(final String key, final Object value) {
        this.customProperties.put(key, value);
        return this;
    }
    
    public String buildWithDefaultMaximumLength() {
        return this.buildWithMaximumLength(4096);
    }
    
    public String buildWithMaximumLength(final int maximumPayloadSize) {
        final Map<String, Object> payload = new HashMap<String, Object>();
        final Map<String, Object> aps = new HashMap<String, Object>();
        if (this.badgeNumber != null) {
            aps.put("badge", this.badgeNumber);
        }
        if (this.soundFileName != null) {
            aps.put("sound", this.soundFileName);
        }
        else if (this.soundForCriticalAlert != null) {
            aps.put("sound", this.soundForCriticalAlert);
        }
        if (this.categoryName != null) {
            aps.put("category", this.categoryName);
        }
        if (this.contentAvailable) {
            aps.put("content-available", 1);
        }
        if (this.mutableContent) {
            aps.put("mutable-content", 1);
        }
        if (this.threadId != null) {
            aps.put("thread-id", this.threadId);
        }
        if (this.targetContentId != null) {
            aps.put("target-content-id", this.targetContentId);
        }
        if (this.urlArguments != null) {
            aps.put("url-args", this.urlArguments);
        }
        final Map<String, Object> alert = new HashMap<String, Object>();
        if (this.alertBody != null) {
            alert.put("body", this.alertBody);
        }
        if (this.alertTitle != null) {
            alert.put("title", this.alertTitle);
        }
        if (this.alertSubtitle != null) {
            alert.put("subtitle", this.alertSubtitle);
        }
        if (this.summaryArgument != null) {
            alert.put("summary-arg", this.summaryArgument);
        }
        if (this.summaryArgumentCount != null) {
            alert.put("summary-arg-count", this.summaryArgumentCount);
        }
        if (this.showActionButton) {
            if (this.localizedActionButtonKey != null) {
                alert.put("action-loc-key", this.localizedActionButtonKey);
            }
            if (this.actionButtonLabel != null) {
                alert.put("action", this.actionButtonLabel);
            }
        }
        else {
            alert.put("action-loc-key", null);
        }
        if (this.localizedAlertKey != null) {
            alert.put("loc-key", this.localizedAlertKey);
            if (this.localizedAlertArguments != null) {
                alert.put("loc-args", Arrays.asList(this.localizedAlertArguments));
            }
        }
        if (this.localizedAlertTitleKey != null) {
            alert.put("title-loc-key", this.localizedAlertTitleKey);
            if (this.localizedAlertTitleArguments != null) {
                alert.put("title-loc-args", Arrays.asList(this.localizedAlertTitleArguments));
            }
        }
        if (this.localizedAlertSubtitleKey != null) {
            alert.put("subtitle-loc-key", this.localizedAlertSubtitleKey);
            if (this.localizedAlertSubtitleArguments != null) {
                alert.put("subtitle-loc-args", Arrays.asList(this.localizedAlertSubtitleArguments));
            }
        }
        if (this.launchImageFileName != null) {
            alert.put("launch-image", this.launchImageFileName);
        }
        if (alert.size() == 1 && alert.containsKey("body") && this.preferStringRepresentationForAlerts) {
            aps.put("alert", alert.get("body"));
        }
        else if (!alert.isEmpty()) {
            aps.put("alert", alert);
        }
        payload.put("aps", aps);
        for (final Map.Entry<String, Object> entry : this.customProperties.entrySet()) {
            payload.put(entry.getKey(), entry.getValue());
        }
        this.buffer.reset();
        ApnsPayloadBuilder.GSON.toJson((Object)payload, (Appendable)this.buffer);
        final String payloadString = this.buffer.toString();
        final int initialPayloadSize = payloadString.getBytes(StandardCharsets.UTF_8).length;
        String fittedPayloadString;
        if (initialPayloadSize <= maximumPayloadSize) {
            fittedPayloadString = payloadString;
        }
        else {
            if (this.alertBody == null) {
                throw new IllegalArgumentException(String.format("Payload size is %d bytes (with a maximum of %d bytes) and cannot be shortened.", initialPayloadSize, maximumPayloadSize));
            }
            this.replaceMessageBody(payload, "");
            this.buffer.reset();
            ApnsPayloadBuilder.GSON.toJson((Object)payload, (Appendable)this.buffer);
            final int payloadSizeWithEmptyMessage = this.buffer.toString().getBytes(StandardCharsets.UTF_8).length;
            if (payloadSizeWithEmptyMessage >= maximumPayloadSize) {
                throw new IllegalArgumentException("Payload exceeds maximum size even with an empty message body.");
            }
            final int maximumEscapedMessageBodySize = maximumPayloadSize - payloadSizeWithEmptyMessage - "\u2026".getBytes(StandardCharsets.UTF_8).length;
            final String fittedMessageBody = this.alertBody.substring(0, getLengthOfJsonEscapedUtf8StringFittingSize(this.alertBody, maximumEscapedMessageBodySize));
            this.replaceMessageBody(payload, fittedMessageBody + "\u2026");
            this.buffer.reset();
            ApnsPayloadBuilder.GSON.toJson((Object)payload, (Appendable)this.buffer);
            fittedPayloadString = this.buffer.toString();
        }
        return fittedPayloadString;
    }
    
    public static String buildMdmPayload(final String pushMagicValue) {
        return ApnsPayloadBuilder.GSON.toJson((Object)Collections.singletonMap("mdm", pushMagicValue));
    }
    
    private void replaceMessageBody(final Map<String, Object> payload, final String messageBody) {
        final Map<String, Object> aps = payload.get("aps");
        final Object alert = aps.get("alert");
        if (alert != null) {
            if (alert instanceof String) {
                aps.put("alert", messageBody);
            }
            else {
                final Map<String, Object> alertObject = (Map<String, Object>)alert;
                if (alertObject.get("body") == null) {
                    throw new IllegalArgumentException("Payload has no message body.");
                }
                alertObject.put("body", messageBody);
            }
            return;
        }
        throw new IllegalArgumentException("Payload has no message body.");
    }
    
    static int getLengthOfJsonEscapedUtf8StringFittingSize(final String string, final int maximumSize) {
        int i = 0;
        int cumulativeSize = 0;
        for (i = 0; i < string.length(); ++i) {
            final char c = string.charAt(i);
            final int charSize = getSizeOfJsonEscapedUtf8Character(c);
            if (cumulativeSize + charSize > maximumSize) {
                break;
            }
            cumulativeSize += charSize;
            if (Character.isHighSurrogate(c)) {
                ++i;
            }
        }
        return i;
    }
    
    static int getSizeOfJsonEscapedUtf8Character(final char c) {
        int charSize;
        if (c == '\"' || c == '\\' || c == '\b' || c == '\f' || c == '\n' || c == '\r' || c == '\t') {
            charSize = 2;
        }
        else if (c <= '\u001f' || c == '\u2028' || c == '\u2029') {
            charSize = 6;
        }
        else if (c <= '\u007f') {
            charSize = 1;
        }
        else if (c <= '\u07ff') {
            charSize = 2;
        }
        else if (Character.isHighSurrogate(c)) {
            charSize = 4;
        }
        else {
            charSize = 3;
        }
        return charSize;
    }
    
    static {
        GSON = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
    }
    
    private static class SoundForCriticalAlert
    {
        private final String name;
        private final int critical;
        private final double volume;
        
        private SoundForCriticalAlert(final String name, final boolean critical, final double volume) {
            this.name = name;
            this.critical = (critical ? 1 : 0);
            this.volume = volume;
        }
    }
}
