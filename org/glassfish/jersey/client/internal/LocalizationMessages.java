package org.glassfish.jersey.client.internal;

import org.glassfish.jersey.internal.l10n.Localizable;
import org.glassfish.jersey.internal.l10n.Localizer;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory;

public final class LocalizationMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizableCLIENT_INSTANCE_CLOSED() {
        return LocalizationMessages.messageFactory.getMessage("client.instance.closed", new Object[0]);
    }
    
    public static String CLIENT_INSTANCE_CLOSED() {
        return LocalizationMessages.localizer.localize(localizableCLIENT_INSTANCE_CLOSED());
    }
    
    public static Localizable localizableCLIENT_TARGET_LINK_NULL() {
        return LocalizationMessages.messageFactory.getMessage("client.target.link.null", new Object[0]);
    }
    
    public static String CLIENT_TARGET_LINK_NULL() {
        return LocalizationMessages.localizer.localize(localizableCLIENT_TARGET_LINK_NULL());
    }
    
    public static Localizable localizableERROR_HTTP_METHOD_ENTITY_NULL(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.http.method.entity.null", new Object[] { arg0 });
    }
    
    public static String ERROR_HTTP_METHOD_ENTITY_NULL(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_HTTP_METHOD_ENTITY_NULL(arg0));
    }
    
    public static Localizable localizableCLIENT_RESPONSE_STATUS_NULL() {
        return LocalizationMessages.messageFactory.getMessage("client.response.status.null", new Object[0]);
    }
    
    public static String CLIENT_RESPONSE_STATUS_NULL() {
        return LocalizationMessages.localizer.localize(localizableCLIENT_RESPONSE_STATUS_NULL());
    }
    
    public static Localizable localizableCHUNKED_INPUT_STREAM_CLOSING_ERROR() {
        return LocalizationMessages.messageFactory.getMessage("chunked.input.stream.closing.error", new Object[0]);
    }
    
    public static String CHUNKED_INPUT_STREAM_CLOSING_ERROR() {
        return LocalizationMessages.localizer.localize(localizableCHUNKED_INPUT_STREAM_CLOSING_ERROR());
    }
    
    public static Localizable localizableIGNORED_ASYNC_THREADPOOL_SIZE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("ignored.async.threadpool.size", new Object[] { arg0 });
    }
    
    public static String IGNORED_ASYNC_THREADPOOL_SIZE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableIGNORED_ASYNC_THREADPOOL_SIZE(arg0));
    }
    
    public static Localizable localizableCLIENT_RX_PROVIDER_NOT_REGISTERED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("client.rx.provider.not.registered", new Object[] { arg0 });
    }
    
    public static String CLIENT_RX_PROVIDER_NOT_REGISTERED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableCLIENT_RX_PROVIDER_NOT_REGISTERED(arg0));
    }
    
    public static Localizable localizableCLIENT_URI_BUILDER_NULL() {
        return LocalizationMessages.messageFactory.getMessage("client.uri.builder.null", new Object[0]);
    }
    
    public static String CLIENT_URI_BUILDER_NULL() {
        return LocalizationMessages.localizer.localize(localizableCLIENT_URI_BUILDER_NULL());
    }
    
    public static Localizable localizableAUTHENTICATION_CREDENTIALS_REQUEST_PASSWORD_UNSUPPORTED() {
        return LocalizationMessages.messageFactory.getMessage("authentication.credentials.request.password.unsupported", new Object[0]);
    }
    
    public static String AUTHENTICATION_CREDENTIALS_REQUEST_PASSWORD_UNSUPPORTED() {
        return LocalizationMessages.localizer.localize(localizableAUTHENTICATION_CREDENTIALS_REQUEST_PASSWORD_UNSUPPORTED());
    }
    
    public static Localizable localizableERROR_REQUEST_CANCELLED() {
        return LocalizationMessages.messageFactory.getMessage("error.request.cancelled", new Object[0]);
    }
    
    public static String ERROR_REQUEST_CANCELLED() {
        return LocalizationMessages.localizer.localize(localizableERROR_REQUEST_CANCELLED());
    }
    
    public static Localizable localizableERROR_HTTP_METHOD_ENTITY_NOT_NULL(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.http.method.entity.not.null", new Object[] { arg0 });
    }
    
    public static String ERROR_HTTP_METHOD_ENTITY_NOT_NULL(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_HTTP_METHOD_ENTITY_NOT_NULL(arg0));
    }
    
    public static Localizable localizableNULL_SSL_CONTEXT() {
        return LocalizationMessages.messageFactory.getMessage("null.ssl.context", new Object[0]);
    }
    
    public static String NULL_SSL_CONTEXT() {
        return LocalizationMessages.localizer.localize(localizableNULL_SSL_CONTEXT());
    }
    
    public static Localizable localizableERROR_LISTENER_CLOSE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.listener.close", new Object[] { arg0 });
    }
    
    public static String ERROR_LISTENER_CLOSE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_LISTENER_CLOSE(arg0));
    }
    
    public static Localizable localizableHTTPURLCONNECTION_REPLACES_GET_WITH_ENTITY() {
        return LocalizationMessages.messageFactory.getMessage("httpurlconnection.replaces.get.with.entity", new Object[0]);
    }
    
    public static String HTTPURLCONNECTION_REPLACES_GET_WITH_ENTITY() {
        return LocalizationMessages.localizer.localize(localizableHTTPURLCONNECTION_REPLACES_GET_WITH_ENTITY());
    }
    
    public static Localizable localizableREQUEST_ENTITY_WRITER_NULL() {
        return LocalizationMessages.messageFactory.getMessage("request.entity.writer.null", new Object[0]);
    }
    
    public static String REQUEST_ENTITY_WRITER_NULL() {
        return LocalizationMessages.localizer.localize(localizableREQUEST_ENTITY_WRITER_NULL());
    }
    
    public static Localizable localizableERROR_COMMITTING_OUTPUT_STREAM() {
        return LocalizationMessages.messageFactory.getMessage("error.committing.output.stream", new Object[0]);
    }
    
    public static String ERROR_COMMITTING_OUTPUT_STREAM() {
        return LocalizationMessages.localizer.localize(localizableERROR_COMMITTING_OUTPUT_STREAM());
    }
    
    public static Localizable localizableUSING_FIXED_ASYNC_THREADPOOL(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("using.fixed.async.threadpool", new Object[] { arg0 });
    }
    
    public static String USING_FIXED_ASYNC_THREADPOOL(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableUSING_FIXED_ASYNC_THREADPOOL(arg0));
    }
    
    public static Localizable localizableCLIENT_RESPONSE_RESOLVED_URI_NOT_ABSOLUTE() {
        return LocalizationMessages.messageFactory.getMessage("client.response.resolved.uri.not.absolute", new Object[0]);
    }
    
    public static String CLIENT_RESPONSE_RESOLVED_URI_NOT_ABSOLUTE() {
        return LocalizationMessages.localizer.localize(localizableCLIENT_RESPONSE_RESOLVED_URI_NOT_ABSOLUTE());
    }
    
    public static Localizable localizableREQUEST_ENTITY_ALREADY_WRITTEN() {
        return LocalizationMessages.messageFactory.getMessage("request.entity.already.written", new Object[0]);
    }
    
    public static String REQUEST_ENTITY_ALREADY_WRITTEN() {
        return LocalizationMessages.localizer.localize(localizableREQUEST_ENTITY_ALREADY_WRITTEN());
    }
    
    public static Localizable localizableCLIENT_URI_NULL() {
        return LocalizationMessages.messageFactory.getMessage("client.uri.null", new Object[0]);
    }
    
    public static String CLIENT_URI_NULL() {
        return LocalizationMessages.localizer.localize(localizableCLIENT_URI_NULL());
    }
    
    public static Localizable localizableAUTHENTICATION_CREDENTIALS_MISSING_DIGEST() {
        return LocalizationMessages.messageFactory.getMessage("authentication.credentials.missing.digest", new Object[0]);
    }
    
    public static String AUTHENTICATION_CREDENTIALS_MISSING_DIGEST() {
        return LocalizationMessages.localizer.localize(localizableAUTHENTICATION_CREDENTIALS_MISSING_DIGEST());
    }
    
    public static Localizable localizableCHUNKED_INPUT_CLOSED() {
        return LocalizationMessages.messageFactory.getMessage("chunked.input.closed", new Object[0]);
    }
    
    public static String CHUNKED_INPUT_CLOSED() {
        return LocalizationMessages.localizer.localize(localizableCHUNKED_INPUT_CLOSED());
    }
    
    public static Localizable localizableNULL_KEYSTORE() {
        return LocalizationMessages.messageFactory.getMessage("null.keystore", new Object[0]);
    }
    
    public static String NULL_KEYSTORE() {
        return LocalizationMessages.localizer.localize(localizableNULL_KEYSTORE());
    }
    
    public static Localizable localizableNEGATIVE_INPUT_PARAMETER(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("negative.input.parameter", new Object[] { arg0 });
    }
    
    public static String NEGATIVE_INPUT_PARAMETER(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableNEGATIVE_INPUT_PARAMETER(arg0));
    }
    
    public static Localizable localizableDIGEST_FILTER_QOP_UNSUPPORTED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("digest.filter.qop.unsupported", new Object[] { arg0 });
    }
    
    public static String DIGEST_FILTER_QOP_UNSUPPORTED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableDIGEST_FILTER_QOP_UNSUPPORTED(arg0));
    }
    
    public static Localizable localizableCHUNKED_INPUT_MEDIA_TYPE_NULL() {
        return LocalizationMessages.messageFactory.getMessage("chunked.input.media.type.null", new Object[0]);
    }
    
    public static String CHUNKED_INPUT_MEDIA_TYPE_NULL() {
        return LocalizationMessages.localizer.localize(localizableCHUNKED_INPUT_MEDIA_TYPE_NULL());
    }
    
    public static Localizable localizableAUTHENTICATION_CREDENTIALS_MISSING_BASIC() {
        return LocalizationMessages.messageFactory.getMessage("authentication.credentials.missing.basic", new Object[0]);
    }
    
    public static String AUTHENTICATION_CREDENTIALS_MISSING_BASIC() {
        return LocalizationMessages.localizer.localize(localizableAUTHENTICATION_CREDENTIALS_MISSING_BASIC());
    }
    
    public static Localizable localizableCLIENT_RX_PROVIDER_NULL() {
        return LocalizationMessages.messageFactory.getMessage("client.rx.provider.null", new Object[0]);
    }
    
    public static String CLIENT_RX_PROVIDER_NULL() {
        return LocalizationMessages.localizer.localize(localizableCLIENT_RX_PROVIDER_NULL());
    }
    
    public static Localizable localizableRESTRICTED_HEADER_POSSIBLY_IGNORED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("restricted.header.possibly.ignored", new Object[] { arg0 });
    }
    
    public static String RESTRICTED_HEADER_POSSIBLY_IGNORED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableRESTRICTED_HEADER_POSSIBLY_IGNORED(arg0));
    }
    
    public static Localizable localizableERROR_SHUTDOWNHOOK_CLOSE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.shutdownhook.close", new Object[] { arg0 });
    }
    
    public static String ERROR_SHUTDOWNHOOK_CLOSE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_SHUTDOWNHOOK_CLOSE(arg0));
    }
    
    public static Localizable localizableRESTRICTED_HEADER_PROPERTY_SETTING_TRUE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("restricted.header.property.setting.true", new Object[] { arg0 });
    }
    
    public static String RESTRICTED_HEADER_PROPERTY_SETTING_TRUE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableRESTRICTED_HEADER_PROPERTY_SETTING_TRUE(arg0));
    }
    
    public static Localizable localizableNULL_TRUSTSTORE() {
        return LocalizationMessages.messageFactory.getMessage("null.truststore", new Object[0]);
    }
    
    public static String NULL_TRUSTSTORE() {
        return LocalizationMessages.localizer.localize(localizableNULL_TRUSTSTORE());
    }
    
    public static Localizable localizableERROR_CLOSING_OUTPUT_STREAM() {
        return LocalizationMessages.messageFactory.getMessage("error.closing.output.stream", new Object[0]);
    }
    
    public static String ERROR_CLOSING_OUTPUT_STREAM() {
        return LocalizationMessages.localizer.localize(localizableERROR_CLOSING_OUTPUT_STREAM());
    }
    
    public static Localizable localizableERROR_LISTENER_INIT(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.listener.init", new Object[] { arg0 });
    }
    
    public static String ERROR_LISTENER_INIT(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_LISTENER_INIT(arg0));
    }
    
    public static Localizable localizableERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_RESPONSE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.service.locator.provider.instance.response", new Object[] { arg0 });
    }
    
    public static String ERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_RESPONSE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_RESPONSE(arg0));
    }
    
    public static Localizable localizableERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_REQUEST(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("error.service.locator.provider.instance.request", new Object[] { arg0 });
    }
    
    public static String ERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_REQUEST(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_REQUEST(arg0));
    }
    
    public static Localizable localizableUNEXPECTED_ERROR_RESPONSE_PROCESSING() {
        return LocalizationMessages.messageFactory.getMessage("unexpected.error.response.processing", new Object[0]);
    }
    
    public static String UNEXPECTED_ERROR_RESPONSE_PROCESSING() {
        return LocalizationMessages.localizer.localize(localizableUNEXPECTED_ERROR_RESPONSE_PROCESSING());
    }
    
    public static Localizable localizableRESPONSE_TO_EXCEPTION_CONVERSION_FAILED() {
        return LocalizationMessages.messageFactory.getMessage("response.to.exception.conversion.failed", new Object[0]);
    }
    
    public static String RESPONSE_TO_EXCEPTION_CONVERSION_FAILED() {
        return LocalizationMessages.localizer.localize(localizableRESPONSE_TO_EXCEPTION_CONVERSION_FAILED());
    }
    
    public static Localizable localizableNULL_KEYSTORE_PASWORD() {
        return LocalizationMessages.messageFactory.getMessage("null.keystore.pasword", new Object[0]);
    }
    
    public static String NULL_KEYSTORE_PASWORD() {
        return LocalizationMessages.localizer.localize(localizableNULL_KEYSTORE_PASWORD());
    }
    
    public static Localizable localizableCLIENT_URI_TEMPLATE_NULL() {
        return LocalizationMessages.messageFactory.getMessage("client.uri.template.null", new Object[0]);
    }
    
    public static String CLIENT_URI_TEMPLATE_NULL() {
        return LocalizationMessages.localizer.localize(localizableCLIENT_URI_TEMPLATE_NULL());
    }
    
    public static Localizable localizableCLIENT_RESPONSE_RESOLVED_URI_NULL() {
        return LocalizationMessages.messageFactory.getMessage("client.response.resolved.uri.null", new Object[0]);
    }
    
    public static String CLIENT_RESPONSE_RESOLVED_URI_NULL() {
        return LocalizationMessages.localizer.localize(localizableCLIENT_RESPONSE_RESOLVED_URI_NULL());
    }
    
    public static Localizable localizableCLIENT_INVOCATION_LINK_NULL() {
        return LocalizationMessages.messageFactory.getMessage("client.invocation.link.null", new Object[0]);
    }
    
    public static String CLIENT_INVOCATION_LINK_NULL() {
        return LocalizationMessages.localizer.localize(localizableCLIENT_INVOCATION_LINK_NULL());
    }
    
    public static Localizable localizableNULL_INPUT_PARAMETER(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("null.input.parameter", new Object[] { arg0 });
    }
    
    public static String NULL_INPUT_PARAMETER(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableNULL_INPUT_PARAMETER(arg0));
    }
    
    public static Localizable localizableRESPONSE_TYPE_IS_NULL() {
        return LocalizationMessages.messageFactory.getMessage("response.type.is.null", new Object[0]);
    }
    
    public static String RESPONSE_TYPE_IS_NULL() {
        return LocalizationMessages.localizer.localize(localizableRESPONSE_TYPE_IS_NULL());
    }
    
    public static Localizable localizableNULL_SCHEDULED_EXECUTOR_SERVICE() {
        return LocalizationMessages.messageFactory.getMessage("null.scheduled.executor.service", new Object[0]);
    }
    
    public static String NULL_SCHEDULED_EXECUTOR_SERVICE() {
        return LocalizationMessages.localizer.localize(localizableNULL_SCHEDULED_EXECUTOR_SERVICE());
    }
    
    public static Localizable localizableUSE_ENCODING_IGNORED(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.messageFactory.getMessage("use.encoding.ignored", new Object[] { arg0, arg1, arg2 });
    }
    
    public static String USE_ENCODING_IGNORED(final Object arg0, final Object arg1, final Object arg2) {
        return LocalizationMessages.localizer.localize(localizableUSE_ENCODING_IGNORED(arg0, arg1, arg2));
    }
    
    public static Localizable localizableRESTRICTED_HEADER_PROPERTY_SETTING_FALSE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("restricted.header.property.setting.false", new Object[] { arg0 });
    }
    
    public static String RESTRICTED_HEADER_PROPERTY_SETTING_FALSE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableRESTRICTED_HEADER_PROPERTY_SETTING_FALSE(arg0));
    }
    
    public static Localizable localizableNULL_CONNECTOR_PROVIDER() {
        return LocalizationMessages.messageFactory.getMessage("null.connector.provider", new Object[0]);
    }
    
    public static String NULL_CONNECTOR_PROVIDER() {
        return LocalizationMessages.localizer.localize(localizableNULL_CONNECTOR_PROVIDER());
    }
    
    public static Localizable localizableNULL_EXECUTOR_SERVICE() {
        return LocalizationMessages.messageFactory.getMessage("null.executor.service", new Object[0]);
    }
    
    public static String NULL_EXECUTOR_SERVICE() {
        return LocalizationMessages.localizer.localize(localizableNULL_EXECUTOR_SERVICE());
    }
    
    public static Localizable localizableERROR_DIGEST_FILTER_GENERATOR() {
        return LocalizationMessages.messageFactory.getMessage("error.digest.filter.generator", new Object[0]);
    }
    
    public static String ERROR_DIGEST_FILTER_GENERATOR() {
        return LocalizationMessages.localizer.localize(localizableERROR_DIGEST_FILTER_GENERATOR());
    }
    
    public static Localizable localizableNEGATIVE_CHUNK_SIZE(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("negative.chunk.size", new Object[] { arg0, arg1 });
    }
    
    public static String NEGATIVE_CHUNK_SIZE(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableNEGATIVE_CHUNK_SIZE(arg0, arg1));
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("org.glassfish.jersey.client.internal.localization");
        localizer = new Localizer();
    }
}
