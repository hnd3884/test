package com.me.devicemanagement.framework.server.pushnotification.notifiers.ios.fcm;

import java.io.IOException;
import java.util.logging.Level;
import org.json.simple.JSONValue;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.net.HttpURLConnection;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.pushnotification.notifiers.android.fcm.ProxySender;

public class ApnsSender extends ProxySender
{
    private static final String MUTABLE_CONTENT_PARAM = "mutable_content";
    private Logger logger;
    
    public ApnsSender(final String key, final Properties proxyProperties) {
        super(key, proxyProperties);
        this.logger = Logger.getLogger(ApnsSender.class.getName());
    }
    
    protected HttpURLConnection post(final String url, final String contentType, String body) throws IOException {
        final JSONParser parser = new JSONParser();
        try {
            if (!body.isEmpty()) {
                final JSONObject modifiedJSON = (JSONObject)parser.parse(body);
                if (!modifiedJSON.isEmpty()) {
                    modifiedJSON.put((Object)"mutable_content", (Object)true);
                    body = JSONValue.toJSONString((Object)modifiedJSON);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error occurred while parsing iOS Notification body: ", ex);
        }
        return super.post(url, contentType, body);
    }
}
