package javax.ws.rs;

import javax.ws.rs.core.Response;

public class WebApplicationException extends RuntimeException
{
    private static final long serialVersionUID = 8273970399584007146L;
    private final Response response;
    
    public WebApplicationException() {
        this((Throwable)null, Response.Status.INTERNAL_SERVER_ERROR);
    }
    
    public WebApplicationException(final String message) {
        this(message, null, Response.Status.INTERNAL_SERVER_ERROR);
    }
    
    public WebApplicationException(final Response response) {
        this((Throwable)null, response);
    }
    
    public WebApplicationException(final String message, final Response response) {
        this(message, null, response);
    }
    
    public WebApplicationException(final int status) {
        this((Throwable)null, status);
    }
    
    public WebApplicationException(final String message, final int status) {
        this(message, null, status);
    }
    
    public WebApplicationException(final Response.Status status) {
        this((Throwable)null, status);
    }
    
    public WebApplicationException(final String message, final Response.Status status) {
        this(message, null, status);
    }
    
    public WebApplicationException(final Throwable cause) {
        this(cause, Response.Status.INTERNAL_SERVER_ERROR);
    }
    
    public WebApplicationException(final String message, final Throwable cause) {
        this(message, cause, Response.Status.INTERNAL_SERVER_ERROR);
    }
    
    public WebApplicationException(final Throwable cause, final Response response) {
        this(computeExceptionMessage(response), cause, response);
    }
    
    public WebApplicationException(final String message, final Throwable cause, final Response response) {
        super(message, cause);
        if (response == null) {
            this.response = Response.serverError().build();
        }
        else {
            this.response = response;
        }
    }
    
    private static String computeExceptionMessage(final Response response) {
        Response.StatusType statusInfo;
        if (response != null) {
            statusInfo = response.getStatusInfo();
        }
        else {
            statusInfo = Response.Status.INTERNAL_SERVER_ERROR;
        }
        return "HTTP " + statusInfo.getStatusCode() + ' ' + statusInfo.getReasonPhrase();
    }
    
    public WebApplicationException(final Throwable cause, final int status) {
        this(cause, Response.status(status).build());
    }
    
    public WebApplicationException(final String message, final Throwable cause, final int status) {
        this(message, cause, Response.status(status).build());
    }
    
    public WebApplicationException(final Throwable cause, final Response.Status status) throws IllegalArgumentException {
        this(cause, Response.status(status).build());
    }
    
    public WebApplicationException(final String message, final Throwable cause, final Response.Status status) throws IllegalArgumentException {
        this(message, cause, Response.status(status).build());
    }
    
    public Response getResponse() {
        return this.response;
    }
    
    static Response validate(final Response response, final Response.Status expectedStatus) {
        if (expectedStatus.getStatusCode() != response.getStatus()) {
            throw new IllegalArgumentException(String.format("Invalid response status code. Expected [%d], was [%d].", expectedStatus.getStatusCode(), response.getStatus()));
        }
        return response;
    }
    
    static Response validate(final Response response, final Response.Status.Family expectedStatusFamily) {
        if (response.getStatusInfo().getFamily() != expectedStatusFamily) {
            throw new IllegalArgumentException(String.format("Status code of the supplied response [%d] is not from the required status code family \"%s\".", response.getStatus(), expectedStatusFamily));
        }
        return response;
    }
}
