package org.glassfish.jersey.client.internal.jdkconnector;

import org.glassfish.jersey.internal.l10n.Localizable;
import org.glassfish.jersey.internal.l10n.Localizer;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory;

public final class LocalizationMessages
{
    private static final LocalizableMessageFactory messageFactory;
    private static final Localizer localizer;
    
    public static Localizable localizablePROXY_FAIL_AUTH_HEADER() {
        return LocalizationMessages.messageFactory.getMessage("proxy.fail.auth.header", new Object[0]);
    }
    
    public static String PROXY_FAIL_AUTH_HEADER() {
        return LocalizationMessages.localizer.localize(localizablePROXY_FAIL_AUTH_HEADER());
    }
    
    public static Localizable localizableREAD_LISTENER_SET_ONLY_ONCE() {
        return LocalizationMessages.messageFactory.getMessage("read.listener.set.only.once", new Object[0]);
    }
    
    public static String READ_LISTENER_SET_ONLY_ONCE() {
        return LocalizationMessages.localizer.localize(localizableREAD_LISTENER_SET_ONLY_ONCE());
    }
    
    public static Localizable localizableCLOSED_BY_CLIENT_WHILE_RECEIVING() {
        return LocalizationMessages.messageFactory.getMessage("closed.by.client.while.receiving", new Object[0]);
    }
    
    public static String CLOSED_BY_CLIENT_WHILE_RECEIVING() {
        return LocalizationMessages.localizer.localize(localizableCLOSED_BY_CLIENT_WHILE_RECEIVING());
    }
    
    public static Localizable localizableUNEXPECTED_DATA_IN_BUFFER() {
        return LocalizationMessages.messageFactory.getMessage("unexpected.data.in.buffer", new Object[0]);
    }
    
    public static String UNEXPECTED_DATA_IN_BUFFER() {
        return LocalizationMessages.localizer.localize(localizableUNEXPECTED_DATA_IN_BUFFER());
    }
    
    public static Localizable localizablePROXY_QOP_NO_SUPPORTED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("proxy.qop.no.supported", new Object[] { arg0 });
    }
    
    public static String PROXY_QOP_NO_SUPPORTED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizablePROXY_QOP_NO_SUPPORTED(arg0));
    }
    
    public static Localizable localizablePROXY_PASSWORD_MISSING() {
        return LocalizationMessages.messageFactory.getMessage("proxy.password.missing", new Object[0]);
    }
    
    public static String PROXY_PASSWORD_MISSING() {
        return LocalizationMessages.localizer.localize(localizablePROXY_PASSWORD_MISSING());
    }
    
    public static Localizable localizableASYNC_OPERATION_NOT_SUPPORTED() {
        return LocalizationMessages.messageFactory.getMessage("async.operation.not.supported", new Object[0]);
    }
    
    public static String ASYNC_OPERATION_NOT_SUPPORTED() {
        return LocalizationMessages.localizer.localize(localizableASYNC_OPERATION_NOT_SUPPORTED());
    }
    
    public static Localizable localizableHTTP_BODY_SIZE_OVERFLOW() {
        return LocalizationMessages.messageFactory.getMessage("http.body.size.overflow", new Object[0]);
    }
    
    public static String HTTP_BODY_SIZE_OVERFLOW() {
        return LocalizationMessages.localizer.localize(localizableHTTP_BODY_SIZE_OVERFLOW());
    }
    
    public static Localizable localizableREDIRECT_ERROR_DETERMINING_LOCATION() {
        return LocalizationMessages.messageFactory.getMessage("redirect.error.determining.location", new Object[0]);
    }
    
    public static String REDIRECT_ERROR_DETERMINING_LOCATION() {
        return LocalizationMessages.localizer.localize(localizableREDIRECT_ERROR_DETERMINING_LOCATION());
    }
    
    public static Localizable localizableCLOSED_BY_CLIENT_WHILE_RECEIVING_BODY() {
        return LocalizationMessages.messageFactory.getMessage("closed.by.client.while.receiving.body", new Object[0]);
    }
    
    public static String CLOSED_BY_CLIENT_WHILE_RECEIVING_BODY() {
        return LocalizationMessages.localizer.localize(localizableCLOSED_BY_CLIENT_WHILE_RECEIVING_BODY());
    }
    
    public static Localizable localizableCONNECTOR_CONFIGURATION(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("connector.configuration", new Object[] { arg0 });
    }
    
    public static String CONNECTOR_CONFIGURATION(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableCONNECTOR_CONFIGURATION(arg0));
    }
    
    public static Localizable localizableHTTP_NEGATIVE_CONTENT_LENGTH() {
        return LocalizationMessages.messageFactory.getMessage("http.negative.content.length", new Object[0]);
    }
    
    public static String HTTP_NEGATIVE_CONTENT_LENGTH() {
        return LocalizationMessages.localizer.localize(localizableHTTP_NEGATIVE_CONTENT_LENGTH());
    }
    
    public static Localizable localizableHTTP_CHUNK_ENCODING_PREFIX_OVERFLOW() {
        return LocalizationMessages.messageFactory.getMessage("http.chunk.encoding.prefix.overflow", new Object[0]);
    }
    
    public static String HTTP_CHUNK_ENCODING_PREFIX_OVERFLOW() {
        return LocalizationMessages.localizer.localize(localizableHTTP_CHUNK_ENCODING_PREFIX_OVERFLOW());
    }
    
    public static Localizable localizableWRITE_LISTENER_SET_ONLY_ONCE() {
        return LocalizationMessages.messageFactory.getMessage("write.listener.set.only.once", new Object[0]);
    }
    
    public static String WRITE_LISTENER_SET_ONLY_ONCE() {
        return LocalizationMessages.localizer.localize(localizableWRITE_LISTENER_SET_ONLY_ONCE());
    }
    
    public static Localizable localizableHTTP_PACKET_HEADER_OVERFLOW() {
        return LocalizationMessages.messageFactory.getMessage("http.packet.header.overflow", new Object[0]);
    }
    
    public static String HTTP_PACKET_HEADER_OVERFLOW() {
        return LocalizationMessages.localizer.localize(localizableHTTP_PACKET_HEADER_OVERFLOW());
    }
    
    public static Localizable localizableHTTP_CONNECTION_NOT_IDLE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("http.connection.not.idle", new Object[] { arg0 });
    }
    
    public static String HTTP_CONNECTION_NOT_IDLE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableHTTP_CONNECTION_NOT_IDLE(arg0));
    }
    
    public static Localizable localizableHTTP_REQUEST_BODY_SIZE_NOT_AVAILABLE() {
        return LocalizationMessages.messageFactory.getMessage("http.request.body.size.not.available", new Object[0]);
    }
    
    public static String HTTP_REQUEST_BODY_SIZE_NOT_AVAILABLE() {
        return LocalizationMessages.localizer.localize(localizableHTTP_REQUEST_BODY_SIZE_NOT_AVAILABLE());
    }
    
    public static Localizable localizableHTTP_TRAILER_HEADER_OVERFLOW() {
        return LocalizationMessages.messageFactory.getMessage("http.trailer.header.overflow", new Object[0]);
    }
    
    public static String HTTP_TRAILER_HEADER_OVERFLOW() {
        return LocalizationMessages.localizer.localize(localizableHTTP_TRAILER_HEADER_OVERFLOW());
    }
    
    public static Localizable localizableHTTP_REQUEST_NO_BUFFERED_BODY() {
        return LocalizationMessages.messageFactory.getMessage("http.request.no.buffered.body", new Object[0]);
    }
    
    public static String HTTP_REQUEST_NO_BUFFERED_BODY() {
        return LocalizationMessages.localizer.localize(localizableHTTP_REQUEST_NO_BUFFERED_BODY());
    }
    
    public static Localizable localizableSSL_SESSION_CLOSED() {
        return LocalizationMessages.messageFactory.getMessage("ssl.session.closed", new Object[0]);
    }
    
    public static String SSL_SESSION_CLOSED() {
        return LocalizationMessages.localizer.localize(localizableSSL_SESSION_CLOSED());
    }
    
    public static Localizable localizableCLOSED_WHILE_RECEIVING_RESPONSE() {
        return LocalizationMessages.messageFactory.getMessage("closed.while.receiving.response", new Object[0]);
    }
    
    public static String CLOSED_WHILE_RECEIVING_RESPONSE() {
        return LocalizationMessages.localizer.localize(localizableCLOSED_WHILE_RECEIVING_RESPONSE());
    }
    
    public static Localizable localizableWRITING_FAILED() {
        return LocalizationMessages.messageFactory.getMessage("writing.failed", new Object[0]);
    }
    
    public static String WRITING_FAILED() {
        return LocalizationMessages.localizer.localize(localizableWRITING_FAILED());
    }
    
    public static Localizable localizableTRANSPORT_EXECUTOR_QUEUE_LIMIT_REACHED() {
        return LocalizationMessages.messageFactory.getMessage("transport.executor.queue.limit.reached", new Object[0]);
    }
    
    public static String TRANSPORT_EXECUTOR_QUEUE_LIMIT_REACHED() {
        return LocalizationMessages.localizer.localize(localizableTRANSPORT_EXECUTOR_QUEUE_LIMIT_REACHED());
    }
    
    public static Localizable localizableSTREAM_CLOSED_FOR_INPUT() {
        return LocalizationMessages.messageFactory.getMessage("stream.closed.for.input", new Object[0]);
    }
    
    public static String STREAM_CLOSED_FOR_INPUT() {
        return LocalizationMessages.localizer.localize(localizableSTREAM_CLOSED_FOR_INPUT());
    }
    
    public static Localizable localizableHTTP_UNEXPECTED_CHUNK_HEADER() {
        return LocalizationMessages.messageFactory.getMessage("http.unexpected.chunk.header", new Object[0]);
    }
    
    public static String HTTP_UNEXPECTED_CHUNK_HEADER() {
        return LocalizationMessages.localizer.localize(localizableHTTP_UNEXPECTED_CHUNK_HEADER());
    }
    
    public static Localizable localizableHTTP_INVALID_CONTENT_LENGTH() {
        return LocalizationMessages.messageFactory.getMessage("http.invalid.content.length", new Object[0]);
    }
    
    public static String HTTP_INVALID_CONTENT_LENGTH() {
        return LocalizationMessages.localizer.localize(localizableHTTP_INVALID_CONTENT_LENGTH());
    }
    
    public static Localizable localizableWRITE_WHEN_NOT_READY() {
        return LocalizationMessages.messageFactory.getMessage("write.when.not.ready", new Object[0]);
    }
    
    public static String WRITE_WHEN_NOT_READY() {
        return LocalizationMessages.localizer.localize(localizableWRITE_WHEN_NOT_READY());
    }
    
    public static Localizable localizableTHREAD_POOL_CORE_SIZE_TOO_SMALL() {
        return LocalizationMessages.messageFactory.getMessage("thread.pool.core.size.too.small", new Object[0]);
    }
    
    public static String THREAD_POOL_CORE_SIZE_TOO_SMALL() {
        return LocalizationMessages.localizer.localize(localizableTHREAD_POOL_CORE_SIZE_TOO_SMALL());
    }
    
    public static Localizable localizableTHREAD_POOL_MAX_SIZE_TOO_SMALL() {
        return LocalizationMessages.messageFactory.getMessage("thread.pool.max.size.too.small", new Object[0]);
    }
    
    public static String THREAD_POOL_MAX_SIZE_TOO_SMALL() {
        return LocalizationMessages.localizer.localize(localizableTHREAD_POOL_MAX_SIZE_TOO_SMALL());
    }
    
    public static Localizable localizableTIMEOUT_RECEIVING_RESPONSE_BODY() {
        return LocalizationMessages.messageFactory.getMessage("timeout.receiving.response.body", new Object[0]);
    }
    
    public static String TIMEOUT_RECEIVING_RESPONSE_BODY() {
        return LocalizationMessages.localizer.localize(localizableTIMEOUT_RECEIVING_RESPONSE_BODY());
    }
    
    public static Localizable localizableSTREAM_CLOSED() {
        return LocalizationMessages.messageFactory.getMessage("stream.closed", new Object[0]);
    }
    
    public static String STREAM_CLOSED() {
        return LocalizationMessages.localizer.localize(localizableSTREAM_CLOSED());
    }
    
    public static Localizable localizableCONNECTION_TIMEOUT() {
        return LocalizationMessages.messageFactory.getMessage("connection.timeout", new Object[0]);
    }
    
    public static String CONNECTION_TIMEOUT() {
        return LocalizationMessages.localizer.localize(localizableCONNECTION_TIMEOUT());
    }
    
    public static Localizable localizableCONNECTION_CLOSED() {
        return LocalizationMessages.messageFactory.getMessage("connection.closed", new Object[0]);
    }
    
    public static String CONNECTION_CLOSED() {
        return LocalizationMessages.localizer.localize(localizableCONNECTION_CLOSED());
    }
    
    public static Localizable localizableCLOSED_WHILE_RECEIVING_BODY() {
        return LocalizationMessages.messageFactory.getMessage("closed.while.receiving.body", new Object[0]);
    }
    
    public static String CLOSED_WHILE_RECEIVING_BODY() {
        return LocalizationMessages.localizer.localize(localizableCLOSED_WHILE_RECEIVING_BODY());
    }
    
    public static Localizable localizableHTTP_INITIAL_LINE_OVERFLOW() {
        return LocalizationMessages.messageFactory.getMessage("http.initial.line.overflow", new Object[0]);
    }
    
    public static String HTTP_INITIAL_LINE_OVERFLOW() {
        return LocalizationMessages.localizer.localize(localizableHTTP_INITIAL_LINE_OVERFLOW());
    }
    
    public static Localizable localizableTRANSPORT_EXECUTOR_CLOSED() {
        return LocalizationMessages.messageFactory.getMessage("transport.executor.closed", new Object[0]);
    }
    
    public static String TRANSPORT_EXECUTOR_CLOSED() {
        return LocalizationMessages.localizer.localize(localizableTRANSPORT_EXECUTOR_CLOSED());
    }
    
    public static Localizable localizableTRANSPORT_CONNECTION_NOT_CLOSED() {
        return LocalizationMessages.messageFactory.getMessage("transport.connection.not.closed", new Object[0]);
    }
    
    public static String TRANSPORT_CONNECTION_NOT_CLOSED() {
        return LocalizationMessages.localizer.localize(localizableTRANSPORT_CONNECTION_NOT_CLOSED());
    }
    
    public static Localizable localizableHTTP_INVALID_CHUNK_SIZE_HEX_VALUE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("http.invalid.chunk.size.hex.value", new Object[] { arg0 });
    }
    
    public static String HTTP_INVALID_CHUNK_SIZE_HEX_VALUE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableHTTP_INVALID_CHUNK_SIZE_HEX_VALUE(arg0));
    }
    
    public static Localizable localizablePROXY_CONNECT_FAIL(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("proxy.connect.fail", new Object[] { arg0 });
    }
    
    public static String PROXY_CONNECT_FAIL(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizablePROXY_CONNECT_FAIL(arg0));
    }
    
    public static Localizable localizableTIMEOUT_RECEIVING_RESPONSE() {
        return LocalizationMessages.messageFactory.getMessage("timeout.receiving.response", new Object[0]);
    }
    
    public static String TIMEOUT_RECEIVING_RESPONSE() {
        return LocalizationMessages.localizer.localize(localizableTIMEOUT_RECEIVING_RESPONSE());
    }
    
    public static Localizable localizableHTTP_CONNECTION_ESTABLISHING_ILLEGAL_STATE(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("http.connection.establishing.illegal.state", new Object[] { arg0 });
    }
    
    public static String HTTP_CONNECTION_ESTABLISHING_ILLEGAL_STATE(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableHTTP_CONNECTION_ESTABLISHING_ILLEGAL_STATE(arg0));
    }
    
    public static Localizable localizableCONNECTION_CHANGING_STATE(final Object arg0, final Object arg1, final Object arg2, final Object arg3) {
        return LocalizationMessages.messageFactory.getMessage("connection.changing.state", new Object[] { arg0, arg1, arg2, arg3 });
    }
    
    public static String CONNECTION_CHANGING_STATE(final Object arg0, final Object arg1, final Object arg2, final Object arg3) {
        return LocalizationMessages.localizer.localize(localizableCONNECTION_CHANGING_STATE(arg0, arg1, arg2, arg3));
    }
    
    public static Localizable localizablePROXY_UNSUPPORTED_SCHEME(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("proxy.unsupported.scheme", new Object[] { arg0 });
    }
    
    public static String PROXY_UNSUPPORTED_SCHEME(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizablePROXY_UNSUPPORTED_SCHEME(arg0));
    }
    
    public static Localizable localizablePROXY_MISSING_AUTH_HEADER() {
        return LocalizationMessages.messageFactory.getMessage("proxy.missing.auth.header", new Object[0]);
    }
    
    public static String PROXY_MISSING_AUTH_HEADER() {
        return LocalizationMessages.localizer.localize(localizablePROXY_MISSING_AUTH_HEADER());
    }
    
    public static Localizable localizableHTTP_REQUEST_NO_BODY() {
        return LocalizationMessages.messageFactory.getMessage("http.request.no.body", new Object[0]);
    }
    
    public static String HTTP_REQUEST_NO_BODY() {
        return LocalizationMessages.localizer.localize(localizableHTTP_REQUEST_NO_BODY());
    }
    
    public static Localizable localizableBUFFER_INCORRECT_LENGTH() {
        return LocalizationMessages.messageFactory.getMessage("buffer.incorrect.length", new Object[0]);
    }
    
    public static String BUFFER_INCORRECT_LENGTH() {
        return LocalizationMessages.localizer.localize(localizableBUFFER_INCORRECT_LENGTH());
    }
    
    public static Localizable localizableTRANSPORT_SET_CLASS_LOADER_FAILED() {
        return LocalizationMessages.messageFactory.getMessage("transport.set.class.loader.failed", new Object[0]);
    }
    
    public static String TRANSPORT_SET_CLASS_LOADER_FAILED() {
        return LocalizationMessages.localizer.localize(localizableTRANSPORT_SET_CLASS_LOADER_FAILED());
    }
    
    public static Localizable localizableREDIRECT_LIMIT_REACHED(final Object arg0) {
        return LocalizationMessages.messageFactory.getMessage("redirect.limit.reached", new Object[] { arg0 });
    }
    
    public static String REDIRECT_LIMIT_REACHED(final Object arg0) {
        return LocalizationMessages.localizer.localize(localizableREDIRECT_LIMIT_REACHED(arg0));
    }
    
    public static Localizable localizableSYNC_OPERATION_NOT_SUPPORTED() {
        return LocalizationMessages.messageFactory.getMessage("sync.operation.not.supported", new Object[0]);
    }
    
    public static String SYNC_OPERATION_NOT_SUPPORTED() {
        return LocalizationMessages.localizer.localize(localizableSYNC_OPERATION_NOT_SUPPORTED());
    }
    
    public static Localizable localizableCLOSED_BY_CLIENT_WHILE_SENDING() {
        return LocalizationMessages.messageFactory.getMessage("closed.by.client.while.sending", new Object[0]);
    }
    
    public static String CLOSED_BY_CLIENT_WHILE_SENDING() {
        return LocalizationMessages.localizer.localize(localizableCLOSED_BY_CLIENT_WHILE_SENDING());
    }
    
    public static Localizable localizableREDIRECT_INFINITE_LOOP() {
        return LocalizationMessages.messageFactory.getMessage("redirect.infinite.loop", new Object[0]);
    }
    
    public static String REDIRECT_INFINITE_LOOP() {
        return LocalizationMessages.localizer.localize(localizableREDIRECT_INFINITE_LOOP());
    }
    
    public static Localizable localizableREDIRECT_NO_LOCATION() {
        return LocalizationMessages.messageFactory.getMessage("redirect.no.location", new Object[0]);
    }
    
    public static String REDIRECT_NO_LOCATION() {
        return LocalizationMessages.localizer.localize(localizableREDIRECT_NO_LOCATION());
    }
    
    public static Localizable localizablePROXY_USER_NAME_MISSING() {
        return LocalizationMessages.messageFactory.getMessage("proxy.user.name.missing", new Object[0]);
    }
    
    public static String PROXY_USER_NAME_MISSING() {
        return LocalizationMessages.localizer.localize(localizablePROXY_USER_NAME_MISSING());
    }
    
    public static Localizable localizablePROXY_407_TWICE() {
        return LocalizationMessages.messageFactory.getMessage("proxy.407.twice", new Object[0]);
    }
    
    public static String PROXY_407_TWICE() {
        return LocalizationMessages.localizer.localize(localizablePROXY_407_TWICE());
    }
    
    public static Localizable localizableCLOSED_WHILE_SENDING_REQUEST() {
        return LocalizationMessages.messageFactory.getMessage("closed.while.sending.request", new Object[0]);
    }
    
    public static String CLOSED_WHILE_SENDING_REQUEST() {
        return LocalizationMessages.localizer.localize(localizableCLOSED_WHILE_SENDING_REQUEST());
    }
    
    public static Localizable localizableNEGATIVE_CHUNK_SIZE(final Object arg0, final Object arg1) {
        return LocalizationMessages.messageFactory.getMessage("negative.chunk.size", new Object[] { arg0, arg1 });
    }
    
    public static String NEGATIVE_CHUNK_SIZE(final Object arg0, final Object arg1) {
        return LocalizationMessages.localizer.localize(localizableNEGATIVE_CHUNK_SIZE(arg0, arg1));
    }
    
    static {
        messageFactory = new LocalizableMessageFactory("org.glassfish.jersey.client.internal.jdkconnector.localization");
        localizer = new Localizer();
    }
}
