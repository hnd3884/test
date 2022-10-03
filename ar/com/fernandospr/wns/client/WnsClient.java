package ar.com.fernandospr.wns.client;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.Invocation;
import ar.com.fernandospr.wns.model.WnsNotificationResponse;
import ar.com.fernandospr.wns.model.WnsNotificationRequestOptional;
import ar.com.fernandospr.wns.model.WnsAbstractNotification;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.WebTarget;
import ar.com.fernandospr.wns.exceptions.WnsException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.glassfish.jersey.client.RequestEntityProcessing;
import org.glassfish.jersey.client.spi.ConnectorProvider;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.logging.LoggingFeature;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.client.ClientBuilder;
import org.glassfish.jersey.client.ClientConfig;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.fasterxml.jackson.jaxrs.xml.JacksonJaxbXMLProvider;
import ar.com.fernandospr.wns.WnsProxyProperties;
import javax.ws.rs.client.Client;
import ar.com.fernandospr.wns.model.WnsOAuthToken;

public class WnsClient
{
    private static final String SCOPE = "notify.windows.com";
    private static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    private static final String AUTHENTICATION_URI = "https://login.live.com/accesstoken.srf";
    private String sid;
    private String clientSecret;
    private WnsOAuthToken token;
    private Client client;
    
    public WnsClient(final String sid, final String clientSecret, final boolean logging) {
        this.sid = sid;
        this.clientSecret = clientSecret;
        this.client = createClient(logging);
    }
    
    public WnsClient(final String sid, final String clientSecret, final WnsProxyProperties proxyProps, final boolean logging) {
        this.sid = sid;
        this.clientSecret = clientSecret;
        this.client = createClient(logging, proxyProps);
    }
    
    protected String getAuthenticationUri() {
        return "https://login.live.com/accesstoken.srf";
    }
    
    private static Client createClient(final boolean logging) {
        final ClientConfig clientConfig = new ClientConfig(new Class[] { JacksonJaxbXMLProvider.class, JacksonJsonProvider.class });
        clientConfig.property("jersey.config.client.readTimeout", (Object)10000);
        clientConfig.property("jersey.config.client.connectTimeout", (Object)10000);
        Client client = ClientBuilder.newClient((Configuration)clientConfig);
        if (logging) {
            final LoggingFeature loggingFilter = new LoggingFeature(Logger.getLogger(WnsClient.class.getName()), Level.INFO, LoggingFeature.Verbosity.PAYLOAD_TEXT, Integer.valueOf(2048));
            client = (Client)client.register((Object)loggingFilter);
        }
        return client;
    }
    
    private static Client createClient(final boolean logging, final WnsProxyProperties proxyProps) {
        final ClientConfig clientConfig = new ClientConfig(new Class[] { JacksonJaxbXMLProvider.class, JacksonJsonProvider.class }).connectorProvider((ConnectorProvider)new ApacheConnectorProvider());
        clientConfig.property("jersey.config.client.request.entity.processing", (Object)RequestEntityProcessing.BUFFERED);
        clientConfig.property("jersey.config.client.readTimeout", (Object)10000);
        clientConfig.property("jersey.config.client.connectTimeout", (Object)10000);
        setProxyCredentials(clientConfig, proxyProps);
        Client client = ClientBuilder.newClient((Configuration)clientConfig);
        if (logging) {
            final LoggingFeature loggingFilter = new LoggingFeature(Logger.getLogger(WnsClient.class.getName()), Level.INFO, LoggingFeature.Verbosity.PAYLOAD_TEXT, Integer.valueOf(2048));
            client = (Client)client.register((Object)loggingFilter);
        }
        return client;
    }
    
    private static void setProxyCredentials(final ClientConfig clientConfig, final WnsProxyProperties proxyProps) {
        if (proxyProps != null) {
            final String proxyProtocol = proxyProps.getProtocol();
            final String proxyHost = proxyProps.getHost();
            final int proxyPort = proxyProps.getPort();
            final String proxyUser = proxyProps.getUser();
            final String proxyPass = proxyProps.getPass();
            if (proxyHost != null && !proxyHost.trim().isEmpty()) {
                clientConfig.property("jersey.config.client.proxy.uri", (Object)(proxyProtocol + "://" + proxyHost + ":" + proxyPort));
                if (!proxyUser.trim().isEmpty()) {
                    clientConfig.property("jersey.config.client.proxy.password", (Object)proxyPass);
                    clientConfig.property("jersey.config.client.proxy.username", (Object)proxyUser);
                }
            }
        }
    }
    
    public void refreshAccessToken() throws WnsException {
        final WebTarget target = this.client.target(this.getAuthenticationUri());
        final MultivaluedStringMap formData = new MultivaluedStringMap();
        formData.add((Object)"grant_type", (Object)"client_credentials");
        formData.add((Object)"client_id", (Object)this.sid);
        formData.add((Object)"client_secret", (Object)this.clientSecret);
        formData.add((Object)"scope", (Object)"notify.windows.com");
        final Response response = target.request(new MediaType[] { MediaType.APPLICATION_FORM_URLENCODED_TYPE }).accept(new MediaType[] { MediaType.APPLICATION_JSON_TYPE }).post(Entity.form((MultivaluedMap)formData));
        if (response.getStatus() != 200) {
            throw new WnsException("Authentication failed. HTTP error code: " + response.getStatus());
        }
        this.token = (WnsOAuthToken)response.readEntity((Class)WnsOAuthToken.class);
    }
    
    public WnsNotificationResponse push(final WnsResourceBuilder resourceBuilder, final String channelUri, final WnsAbstractNotification notification, int retriesLeft, final WnsNotificationRequestOptional optional) throws WnsException {
        final WebTarget target = this.client.target(channelUri);
        final Invocation.Builder webResourceBuilder = resourceBuilder.build(target, notification, this.getToken().access_token, optional);
        final String type = notification.getType().equals("wns/raw") ? "application/octet-stream" : "text/xml";
        final Response response = webResourceBuilder.buildPost(Entity.entity(resourceBuilder.getEntityToSendWithNotification(notification), type)).invoke();
        final WnsNotificationResponse notificationResponse = new WnsNotificationResponse(channelUri, response.getStatus(), (MultivaluedMap<String, String>)response.getStringHeaders());
        if (notificationResponse.code == 200) {
            return notificationResponse;
        }
        if (notificationResponse.code == 401 && retriesLeft > 0) {
            --retriesLeft;
            this.refreshAccessToken();
            return this.push(resourceBuilder, channelUri, notification, retriesLeft, optional);
        }
        return notificationResponse;
    }
    
    private WnsOAuthToken getToken() throws WnsException {
        if (this.token == null) {
            this.refreshAccessToken();
        }
        return this.token;
    }
    
    public List<WnsNotificationResponse> push(final WnsResourceBuilder resourceBuilder, final List<String> channelUris, final WnsAbstractNotification notification, final int retriesLeft, final WnsNotificationRequestOptional optional) throws WnsException {
        final List<WnsNotificationResponse> responses = new ArrayList<WnsNotificationResponse>();
        for (final String channelUri : channelUris) {
            final WnsNotificationResponse response = this.push(resourceBuilder, channelUri, notification, retriesLeft, optional);
            responses.add(response);
        }
        return responses;
    }
}
