package io.opencensus.contrib.http.util;

import javax.annotation.Nullable;
import io.opencensus.trace.Status;

public final class HttpTraceUtil
{
    private static final Status STATUS_100;
    private static final Status STATUS_101;
    private static final Status STATUS_402;
    private static final Status STATUS_405;
    private static final Status STATUS_406;
    private static final Status STATUS_407;
    private static final Status STATUS_408;
    private static final Status STATUS_409;
    private static final Status STATUS_410;
    private static final Status STATUS_411;
    private static final Status STATUS_412;
    private static final Status STATUS_413;
    private static final Status STATUS_414;
    private static final Status STATUS_415;
    private static final Status STATUS_416;
    private static final Status STATUS_417;
    private static final Status STATUS_500;
    private static final Status STATUS_502;
    private static final Status STATUS_505;
    
    private HttpTraceUtil() {
    }
    
    public static final Status parseResponseStatus(final int statusCode, @Nullable final Throwable error) {
        String message = null;
        if (error != null) {
            message = error.getMessage();
            if (message == null) {
                message = error.getClass().getSimpleName();
            }
        }
        if (statusCode == 0) {
            return Status.UNKNOWN.withDescription(message);
        }
        if (statusCode >= 200 && statusCode < 400) {
            return Status.OK;
        }
        switch (statusCode) {
            case 100: {
                return HttpTraceUtil.STATUS_100;
            }
            case 101: {
                return HttpTraceUtil.STATUS_101;
            }
            case 400: {
                return Status.INVALID_ARGUMENT.withDescription(message);
            }
            case 401: {
                return Status.UNAUTHENTICATED.withDescription(message);
            }
            case 402: {
                return HttpTraceUtil.STATUS_402;
            }
            case 403: {
                return Status.PERMISSION_DENIED.withDescription(message);
            }
            case 404: {
                return Status.NOT_FOUND.withDescription(message);
            }
            case 405: {
                return HttpTraceUtil.STATUS_405;
            }
            case 406: {
                return HttpTraceUtil.STATUS_406;
            }
            case 407: {
                return HttpTraceUtil.STATUS_407;
            }
            case 408: {
                return HttpTraceUtil.STATUS_408;
            }
            case 409: {
                return HttpTraceUtil.STATUS_409;
            }
            case 410: {
                return HttpTraceUtil.STATUS_410;
            }
            case 411: {
                return HttpTraceUtil.STATUS_411;
            }
            case 412: {
                return HttpTraceUtil.STATUS_412;
            }
            case 413: {
                return HttpTraceUtil.STATUS_413;
            }
            case 414: {
                return HttpTraceUtil.STATUS_414;
            }
            case 415: {
                return HttpTraceUtil.STATUS_415;
            }
            case 416: {
                return HttpTraceUtil.STATUS_416;
            }
            case 417: {
                return HttpTraceUtil.STATUS_417;
            }
            case 429: {
                return Status.RESOURCE_EXHAUSTED.withDescription(message);
            }
            case 500: {
                return HttpTraceUtil.STATUS_500;
            }
            case 501: {
                return Status.UNIMPLEMENTED.withDescription(message);
            }
            case 502: {
                return HttpTraceUtil.STATUS_502;
            }
            case 503: {
                return Status.UNAVAILABLE.withDescription(message);
            }
            case 504: {
                return Status.DEADLINE_EXCEEDED.withDescription(message);
            }
            case 505: {
                return HttpTraceUtil.STATUS_505;
            }
            default: {
                return Status.UNKNOWN.withDescription(message);
            }
        }
    }
    
    static {
        STATUS_100 = Status.UNKNOWN.withDescription("Continue");
        STATUS_101 = Status.UNKNOWN.withDescription("Switching Protocols");
        STATUS_402 = Status.UNKNOWN.withDescription("Payment Required");
        STATUS_405 = Status.UNKNOWN.withDescription("Method Not Allowed");
        STATUS_406 = Status.UNKNOWN.withDescription("Not Acceptable");
        STATUS_407 = Status.UNKNOWN.withDescription("Proxy Authentication Required");
        STATUS_408 = Status.UNKNOWN.withDescription("Request Time-out");
        STATUS_409 = Status.UNKNOWN.withDescription("Conflict");
        STATUS_410 = Status.UNKNOWN.withDescription("Gone");
        STATUS_411 = Status.UNKNOWN.withDescription("Length Required");
        STATUS_412 = Status.UNKNOWN.withDescription("Precondition Failed");
        STATUS_413 = Status.UNKNOWN.withDescription("Request Entity Too Large");
        STATUS_414 = Status.UNKNOWN.withDescription("Request-URI Too Large");
        STATUS_415 = Status.UNKNOWN.withDescription("Unsupported Media Type");
        STATUS_416 = Status.UNKNOWN.withDescription("Requested range not satisfiable");
        STATUS_417 = Status.UNKNOWN.withDescription("Expectation Failed");
        STATUS_500 = Status.UNKNOWN.withDescription("Internal Server Error");
        STATUS_502 = Status.UNKNOWN.withDescription("Bad Gateway");
        STATUS_505 = Status.UNKNOWN.withDescription("HTTP Version not supported");
    }
}
