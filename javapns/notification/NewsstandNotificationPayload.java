package javapns.notification;

import org.json.JSONException;
import org.json.JSONObject;

public class NewsstandNotificationPayload extends Payload
{
    private final JSONObject apsDictionary;
    
    private NewsstandNotificationPayload() {
        this.apsDictionary = new JSONObject();
        try {
            final JSONObject payload = this.getPayload();
            payload.put("aps", (Object)this.apsDictionary);
        }
        catch (final JSONException e) {
            e.printStackTrace();
        }
    }
    
    public static NewsstandNotificationPayload contentAvailable() {
        final NewsstandNotificationPayload payload = complex();
        try {
            payload.addContentAvailable();
        }
        catch (final JSONException ex) {}
        return payload;
    }
    
    private static NewsstandNotificationPayload complex() {
        final NewsstandNotificationPayload payload = new NewsstandNotificationPayload();
        return payload;
    }
    
    private void addContentAvailable() throws JSONException {
        this.addContentAvailable(1);
    }
    
    private void addContentAvailable(final int contentAvailable) throws JSONException {
        NewsstandNotificationPayload.logger.debug("Adding ContentAvailable [" + contentAvailable + "]");
        this.apsDictionary.put("content-available", contentAvailable);
    }
}
