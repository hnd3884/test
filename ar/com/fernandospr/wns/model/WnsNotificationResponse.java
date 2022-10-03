package ar.com.fernandospr.wns.model;

import java.util.List;
import javax.ws.rs.core.MultivaluedMap;

public class WnsNotificationResponse
{
    public final String channelUri;
    public final int code;
    public final String debugTrace;
    public final String deviceConnectionStatus;
    public final String errorDescription;
    public final String msgID;
    public final String notificationStatus;
    
    public WnsNotificationResponse(final String channelUri, final int responseCode, final MultivaluedMap<String, String> headers) {
        this.channelUri = channelUri;
        this.code = responseCode;
        this.debugTrace = ((headers.get((Object)"X-WNS-Debug-Trace") != null) ? ((List)headers.get((Object)"X-WNS-Debug-Trace")).get(0) : null);
        this.deviceConnectionStatus = ((headers.get((Object)"X-WNS-DeviceConnectionStatus") != null) ? ((List)headers.get((Object)"X-WNS-DeviceConnectionStatus")).get(0) : null);
        this.errorDescription = ((headers.get((Object)"X-WNS-Error-Description") != null) ? ((List)headers.get((Object)"X-WNS-Error-Description")).get(0) : null);
        this.msgID = ((headers.get((Object)"X-WNS-Msg-ID") != null) ? ((List)headers.get((Object)"X-WNS-Msg-ID")).get(0) : null);
        this.notificationStatus = ((headers.get((Object)"X-WNS-Status") != null) ? ((List)headers.get((Object)"X-WNS-Status")).get(0) : null);
    }
}
