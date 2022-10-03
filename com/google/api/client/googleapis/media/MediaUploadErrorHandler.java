package com.google.api.client.googleapis.media;

import com.google.api.client.http.HttpResponse;
import java.io.IOException;
import java.util.logging.Level;
import com.google.api.client.util.Preconditions;
import com.google.api.client.http.HttpRequest;
import java.util.logging.Logger;
import com.google.api.client.util.Beta;
import com.google.api.client.http.HttpIOExceptionHandler;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;

@Beta
class MediaUploadErrorHandler implements HttpUnsuccessfulResponseHandler, HttpIOExceptionHandler
{
    static final Logger LOGGER;
    private final MediaHttpUploader uploader;
    private final HttpIOExceptionHandler originalIOExceptionHandler;
    private final HttpUnsuccessfulResponseHandler originalUnsuccessfulHandler;
    
    public MediaUploadErrorHandler(final MediaHttpUploader uploader, final HttpRequest request) {
        this.uploader = (MediaHttpUploader)Preconditions.checkNotNull((Object)uploader);
        this.originalIOExceptionHandler = request.getIOExceptionHandler();
        this.originalUnsuccessfulHandler = request.getUnsuccessfulResponseHandler();
        request.setIOExceptionHandler((HttpIOExceptionHandler)this);
        request.setUnsuccessfulResponseHandler((HttpUnsuccessfulResponseHandler)this);
    }
    
    public boolean handleIOException(final HttpRequest request, final boolean supportsRetry) throws IOException {
        final boolean handled = this.originalIOExceptionHandler != null && this.originalIOExceptionHandler.handleIOException(request, supportsRetry);
        if (handled) {
            try {
                this.uploader.serverErrorCallback();
            }
            catch (final IOException e) {
                MediaUploadErrorHandler.LOGGER.log(Level.WARNING, "exception thrown while calling server callback", e);
            }
        }
        return handled;
    }
    
    public boolean handleResponse(final HttpRequest request, final HttpResponse response, final boolean supportsRetry) throws IOException {
        final boolean handled = this.originalUnsuccessfulHandler != null && this.originalUnsuccessfulHandler.handleResponse(request, response, supportsRetry);
        if (handled && supportsRetry && response.getStatusCode() / 100 == 5) {
            try {
                this.uploader.serverErrorCallback();
            }
            catch (final IOException e) {
                MediaUploadErrorHandler.LOGGER.log(Level.WARNING, "exception thrown while calling server callback", e);
            }
        }
        return handled;
    }
    
    static {
        LOGGER = Logger.getLogger(MediaUploadErrorHandler.class.getName());
    }
}
