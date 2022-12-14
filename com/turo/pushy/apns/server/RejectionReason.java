package com.turo.pushy.apns.server;

import io.netty.handler.codec.http.HttpResponseStatus;

public enum RejectionReason
{
    BAD_COLLAPSE_ID("BadCollapseId", HttpResponseStatus.BAD_REQUEST), 
    BAD_DEVICE_TOKEN("BadDeviceToken", HttpResponseStatus.BAD_REQUEST), 
    BAD_EXPIRATION_DATE("BadExpirationDate", HttpResponseStatus.BAD_REQUEST), 
    BAD_MESSAGE_ID("BadMessageId", HttpResponseStatus.BAD_REQUEST), 
    BAD_PRIORITY("BadPriority", HttpResponseStatus.BAD_REQUEST), 
    BAD_TOPIC("BadTopic", HttpResponseStatus.BAD_REQUEST), 
    DEVICE_TOKEN_NOT_FOR_TOPIC("DeviceTokenNotForTopic", HttpResponseStatus.BAD_REQUEST), 
    DUPLICATE_HEADERS("DuplicateHeaders", HttpResponseStatus.BAD_REQUEST), 
    IDLE_TIMEOUT("IdleTimeout", HttpResponseStatus.BAD_REQUEST), 
    INVALID_PUSH_TYPE("InvalidPushType", HttpResponseStatus.BAD_REQUEST), 
    MISSING_DEVICE_TOKEN("MissingDeviceToken", HttpResponseStatus.BAD_REQUEST), 
    MISSING_TOPIC("MissingTopic", HttpResponseStatus.BAD_REQUEST), 
    PAYLOAD_EMPTY("PayloadEmpty", HttpResponseStatus.BAD_REQUEST), 
    TOPIC_DISALLOWED("TopicDisallowed", HttpResponseStatus.BAD_REQUEST), 
    BAD_CERTIFICATE("BadCertificate", HttpResponseStatus.FORBIDDEN), 
    BAD_CERTIFICATE_ENVIRONMENT("BadCertificateEnvironment", HttpResponseStatus.FORBIDDEN), 
    EXPIRED_PROVIDER_TOKEN("ExpiredProviderToken", HttpResponseStatus.FORBIDDEN), 
    FORBIDDEN("Forbidden", HttpResponseStatus.FORBIDDEN), 
    INVALID_PROVIDER_TOKEN("InvalidProviderToken", HttpResponseStatus.FORBIDDEN), 
    MISSING_PROVIDER_TOKEN("MissingProviderToken", HttpResponseStatus.FORBIDDEN), 
    BAD_PATH("BadPath", HttpResponseStatus.NOT_FOUND), 
    METHOD_NOT_ALLOWED("MethodNotAllowed", HttpResponseStatus.METHOD_NOT_ALLOWED), 
    UNREGISTERED("Unregistered", HttpResponseStatus.GONE), 
    PAYLOAD_TOO_LARGE("PayloadTooLarge", HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE), 
    TOO_MANY_PROVIDER_TOKEN_UPDATES("TooManyProviderTokenUpdates", HttpResponseStatus.TOO_MANY_REQUESTS), 
    TOO_MANY_REQUESTS("TooManyRequests", HttpResponseStatus.TOO_MANY_REQUESTS), 
    INTERNAL_SERVER_ERROR("InternalServerError", HttpResponseStatus.INTERNAL_SERVER_ERROR), 
    SERVICE_UNAVAILABLE("ServiceUnavailable", HttpResponseStatus.SERVICE_UNAVAILABLE), 
    SHUTDOWN("Shutdown", HttpResponseStatus.SERVICE_UNAVAILABLE);
    
    private final String reasonText;
    private final HttpResponseStatus httpResponseStatus;
    
    private RejectionReason(final String reasonText, final HttpResponseStatus httpResponseStatus) {
        this.reasonText = reasonText;
        this.httpResponseStatus = httpResponseStatus;
    }
    
    String getReasonText() {
        return this.reasonText;
    }
    
    HttpResponseStatus getHttpResponseStatus() {
        return this.httpResponseStatus;
    }
}
