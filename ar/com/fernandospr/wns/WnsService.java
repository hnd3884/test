package ar.com.fernandospr.wns;

import ar.com.fernandospr.wns.model.WnsRaw;
import ar.com.fernandospr.wns.model.WnsBadge;
import ar.com.fernandospr.wns.model.WnsToast;
import java.util.List;
import ar.com.fernandospr.wns.model.WnsAbstractNotification;
import ar.com.fernandospr.wns.exceptions.WnsException;
import ar.com.fernandospr.wns.model.WnsNotificationRequestOptional;
import ar.com.fernandospr.wns.model.WnsNotificationResponse;
import ar.com.fernandospr.wns.model.WnsTile;
import ar.com.fernandospr.wns.client.WnsRawResourceBuilder;
import ar.com.fernandospr.wns.client.WnsXmlResourceBuilder;
import ar.com.fernandospr.wns.client.WnsResourceBuilder;
import ar.com.fernandospr.wns.client.WnsClient;

public class WnsService
{
    private int retryPolicy;
    private WnsClient client;
    private WnsResourceBuilder xmlResourceBuilder;
    private WnsResourceBuilder rawResourceBuilder;
    
    public WnsService(final String sid, final String clientSecret) {
        this(sid, clientSecret, false);
    }
    
    public WnsService(final String sid, final String clientSecret, final WnsProxyProperties proxyProperties) {
        this(sid, clientSecret, proxyProperties, false);
    }
    
    public WnsService(final String sid, final String clientSecret, final WnsProxyProperties proxyProperties, final boolean logging) {
        this.retryPolicy = 5;
        this.client = new WnsClient(sid, clientSecret, proxyProperties, logging);
        this.xmlResourceBuilder = new WnsXmlResourceBuilder();
        this.rawResourceBuilder = new WnsRawResourceBuilder();
    }
    
    public WnsService(final String sid, final String clientSecret, final boolean logging) {
        this.retryPolicy = 5;
        this.client = new WnsClient(sid, clientSecret, logging);
        this.xmlResourceBuilder = new WnsXmlResourceBuilder();
        this.rawResourceBuilder = new WnsRawResourceBuilder();
    }
    
    public WnsNotificationResponse pushTile(final String channelUri, final WnsTile tile) throws WnsException {
        return this.pushTile(channelUri, null, tile);
    }
    
    public WnsNotificationResponse pushTile(final String channelUri, final WnsNotificationRequestOptional optional, final WnsTile tile) throws WnsException {
        return this.client.push(this.xmlResourceBuilder, channelUri, tile, this.retryPolicy, optional);
    }
    
    public List<WnsNotificationResponse> pushTile(final List<String> channelUris, final WnsTile tile) throws WnsException {
        return this.pushTile(channelUris, null, tile);
    }
    
    public List<WnsNotificationResponse> pushTile(final List<String> channelUris, final WnsNotificationRequestOptional optional, final WnsTile tile) throws WnsException {
        return this.client.push(this.xmlResourceBuilder, channelUris, tile, this.retryPolicy, optional);
    }
    
    public WnsNotificationResponse pushToast(final String channelUri, final WnsToast toast) throws WnsException {
        return this.pushToast(channelUri, null, toast);
    }
    
    public WnsNotificationResponse pushToast(final String channelUri, final WnsNotificationRequestOptional optional, final WnsToast toast) throws WnsException {
        return this.client.push(this.xmlResourceBuilder, channelUri, toast, this.retryPolicy, optional);
    }
    
    public List<WnsNotificationResponse> pushToast(final List<String> channelUris, final WnsToast toast) throws WnsException {
        return this.pushToast(channelUris, null, toast);
    }
    
    public List<WnsNotificationResponse> pushToast(final List<String> channelUris, final WnsNotificationRequestOptional optional, final WnsToast toast) throws WnsException {
        return this.client.push(this.xmlResourceBuilder, channelUris, toast, this.retryPolicy, optional);
    }
    
    public WnsNotificationResponse pushBadge(final String channelUri, final WnsBadge badge) throws WnsException {
        return this.pushBadge(channelUri, null, badge);
    }
    
    public WnsNotificationResponse pushBadge(final String channelUri, final WnsNotificationRequestOptional optional, final WnsBadge badge) throws WnsException {
        return this.client.push(this.xmlResourceBuilder, channelUri, badge, this.retryPolicy, optional);
    }
    
    public List<WnsNotificationResponse> pushBadge(final List<String> channelUris, final WnsBadge badge) throws WnsException {
        return this.pushBadge(channelUris, null, badge);
    }
    
    public List<WnsNotificationResponse> pushBadge(final List<String> channelUris, final WnsNotificationRequestOptional optional, final WnsBadge badge) throws WnsException {
        return this.client.push(this.xmlResourceBuilder, channelUris, badge, this.retryPolicy, optional);
    }
    
    public WnsNotificationResponse pushRaw(final String channelUri, final WnsRaw raw) throws WnsException {
        return this.pushRaw(channelUri, null, raw);
    }
    
    public WnsNotificationResponse pushRaw(final String channelUri, final WnsNotificationRequestOptional optional, final WnsRaw raw) throws WnsException {
        return this.client.push(this.rawResourceBuilder, channelUri, raw, this.retryPolicy, optional);
    }
    
    public List<WnsNotificationResponse> pushRaw(final List<String> channelUris, final WnsRaw raw) throws WnsException {
        return this.pushRaw(channelUris, null, raw);
    }
    
    public List<WnsNotificationResponse> pushRaw(final List<String> channelUris, final WnsNotificationRequestOptional optional, final WnsRaw raw) throws WnsException {
        return this.client.push(this.rawResourceBuilder, channelUris, raw, this.retryPolicy, optional);
    }
}
