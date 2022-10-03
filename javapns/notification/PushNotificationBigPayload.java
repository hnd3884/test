package javapns.notification;

import org.json.JSONException;

public class PushNotificationBigPayload extends PushNotificationPayload
{
    private static final int MAXIMUM_PAYLOAD_LENGTH = 2048;
    
    private PushNotificationBigPayload() {
    }
    
    private PushNotificationBigPayload(final String rawJSON) throws JSONException {
        super(rawJSON);
    }
    
    public static PushNotificationBigPayload complex() {
        return new PushNotificationBigPayload();
    }
    
    public static PushNotificationBigPayload fromJSON(final String rawJSON) throws JSONException {
        return new PushNotificationBigPayload(rawJSON);
    }
    
    @Override
    public int getMaximumPayloadSize() {
        return 2048;
    }
}
