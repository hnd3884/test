package javapns.notification;

import java.util.List;
import javapns.notification.exceptions.PayloadAlertAlreadyExistsException;
import org.json.JSONException;
import org.json.JSONObject;

public class PushNotificationPayload extends Payload
{
    private static final int MAXIMUM_PAYLOAD_LENGTH = 256;
    private JSONObject apsDictionary;
    
    PushNotificationPayload() {
        this.apsDictionary = new JSONObject();
        try {
            final JSONObject payload = this.getPayload();
            if (!payload.has("aps")) {
                payload.put("aps", (Object)this.apsDictionary);
            }
        }
        catch (final JSONException e) {
            e.printStackTrace();
        }
    }
    
    PushNotificationPayload(final String rawJSON) throws JSONException {
        super(rawJSON);
        try {
            final JSONObject payload = this.getPayload();
            this.apsDictionary = payload.getJSONObject("aps");
            if (this.apsDictionary == null) {
                payload.put("aps", (Object)(this.apsDictionary = new JSONObject()));
            }
        }
        catch (final JSONException e) {
            e.printStackTrace();
        }
    }
    
    public PushNotificationPayload(final String alert, final int badge, final String sound) throws JSONException {
        this();
        if (alert != null) {
            this.addAlert(alert);
        }
        this.addBadge(badge);
        if (sound != null) {
            this.addSound(sound);
        }
    }
    
    public static PushNotificationPayload alert(final String message) {
        if (message == null) {
            throw new IllegalArgumentException("Alert cannot be null");
        }
        final PushNotificationPayload payload = complex();
        try {
            payload.addAlert(message);
        }
        catch (final JSONException ex) {}
        return payload;
    }
    
    public static PushNotificationPayload badge(final int badge) {
        final PushNotificationPayload payload = complex();
        try {
            payload.addBadge(badge);
        }
        catch (final JSONException ex) {}
        return payload;
    }
    
    public static PushNotificationPayload sound(final String sound) {
        if (sound == null) {
            throw new IllegalArgumentException("Sound name cannot be null");
        }
        final PushNotificationPayload payload = complex();
        try {
            payload.addSound(sound);
        }
        catch (final JSONException ex) {}
        return payload;
    }
    
    public static PushNotificationPayload combined(final String message, final int badge, final String sound) {
        if (message == null && badge < 0 && sound == null) {
            throw new IllegalArgumentException("Must provide at least one non-null argument");
        }
        final PushNotificationPayload payload = complex();
        try {
            if (message != null) {
                payload.addAlert(message);
            }
            if (badge >= 0) {
                payload.addBadge(badge);
            }
            if (sound != null) {
                payload.addSound(sound);
            }
        }
        catch (final JSONException ex) {}
        return payload;
    }
    
    public static PushNotificationPayload test() {
        final PushNotificationPayload payload = complex();
        payload.setPreSendConfiguration(1);
        return payload;
    }
    
    public static PushNotificationPayload complex() {
        final PushNotificationPayload payload = new PushNotificationPayload();
        return payload;
    }
    
    public static PushNotificationPayload fromJSON(final String rawJSON) throws JSONException {
        final PushNotificationPayload payload = new PushNotificationPayload(rawJSON);
        return payload;
    }
    
    public void addBadge(final int badge) throws JSONException {
        PushNotificationPayload.logger.debug("Adding badge [" + badge + "]");
        this.put("badge", badge, this.apsDictionary, true);
    }
    
    public void addSound(final String sound) throws JSONException {
        PushNotificationPayload.logger.debug("Adding sound [" + sound + "]");
        this.put("sound", sound, this.apsDictionary, true);
    }
    
    public void addAlert(final String alertMessage) throws JSONException {
        final String previousAlert = this.getCompatibleProperty("alert", String.class, "A custom alert (\"%s\") was already added to this payload");
        PushNotificationPayload.logger.debug("Adding alert [" + alertMessage + "]" + ((previousAlert != null) ? (" replacing previous alert [" + previousAlert + "]") : ""));
        this.put("alert", alertMessage, this.apsDictionary, false);
    }
    
    private JSONObject getOrAddCustomAlert() throws JSONException {
        JSONObject alert = this.getCompatibleProperty("alert", JSONObject.class, "A simple alert (\"%s\") was already added to this payload");
        if (alert == null) {
            alert = new JSONObject();
            this.put("alert", alert, this.apsDictionary, false);
        }
        return alert;
    }
    
    private <T> T getCompatibleProperty(final String propertyName, final Class<T> expectedClass, final String exceptionMessage) throws JSONException {
        return this.getCompatibleProperty(propertyName, expectedClass, exceptionMessage, this.apsDictionary);
    }
    
    private <T> T getCompatibleProperty(final String propertyName, final Class<T> expectedClass, String exceptionMessage, final JSONObject dictionary) throws JSONException {
        Object propertyValue = null;
        try {
            propertyValue = dictionary.get(propertyName);
        }
        catch (final Exception ex) {}
        if (propertyValue == null) {
            return null;
        }
        if (propertyValue.getClass().equals(expectedClass)) {
            return (T)propertyValue;
        }
        try {
            exceptionMessage = String.format(exceptionMessage, propertyValue);
        }
        catch (final Exception ex2) {}
        throw new PayloadAlertAlreadyExistsException(exceptionMessage);
    }
    
    public void addCustomAlertBody(final String body) throws JSONException {
        this.put("body", body, this.getOrAddCustomAlert(), false);
    }
    
    public void addCustomAlertActionLocKey(final String actionLocKey) throws JSONException {
        final Object value = (actionLocKey != null) ? actionLocKey : JSONObject.NULL;
        this.put("action-loc-key", value, this.getOrAddCustomAlert(), false);
    }
    
    public void addCustomAlertLocKey(final String locKey) throws JSONException {
        this.put("loc-key", locKey, this.getOrAddCustomAlert(), false);
    }
    
    public void addCustomAlertLocArgs(final List args) throws JSONException {
        this.put("loc-args", args, this.getOrAddCustomAlert(), false);
    }
    
    public void setContentAvailable(final boolean available) throws JSONException {
        if (available) {
            this.put("content-available", 1, this.apsDictionary, false);
        }
        else {
            this.remove("content-available", this.apsDictionary);
        }
    }
    
    public int getMaximumPayloadSize() {
        return 256;
    }
    
    @Override
    void verifyPayloadIsNotEmpty() {
        if (this.getPreSendConfiguration() != 0) {
            return;
        }
        if (this.toString().equals("{\"aps\":{}}")) {
            throw new IllegalArgumentException("Payload cannot be empty");
        }
    }
}
