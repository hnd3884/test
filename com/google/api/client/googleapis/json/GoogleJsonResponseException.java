package com.google.api.client.googleapis.json;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.json.JsonParser;
import com.google.api.client.util.StringUtils;
import com.google.api.client.util.Strings;
import java.io.IOException;
import com.google.api.client.json.JsonToken;
import com.google.api.client.http.HttpMediaType;
import com.google.api.client.util.Preconditions;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.HttpResponseException;

public class GoogleJsonResponseException extends HttpResponseException
{
    private static final long serialVersionUID = 409811126989994864L;
    private final transient GoogleJsonError details;
    
    public GoogleJsonResponseException(final HttpResponseException.Builder builder, final GoogleJsonError details) {
        super(builder);
        this.details = details;
    }
    
    public final GoogleJsonError getDetails() {
        return this.details;
    }
    
    public static GoogleJsonResponseException from(final JsonFactory jsonFactory, final HttpResponse response) {
        final HttpResponseException.Builder builder = new HttpResponseException.Builder(response.getStatusCode(), response.getStatusMessage(), response.getHeaders());
        Preconditions.checkNotNull((Object)jsonFactory);
        GoogleJsonError details = null;
        String detailString = null;
        try {
            if (!response.isSuccessStatusCode() && HttpMediaType.equalsIgnoreParameters("application/json; charset=UTF-8", response.getContentType()) && response.getContent() != null) {
                JsonParser parser = null;
                try {
                    parser = jsonFactory.createJsonParser(response.getContent());
                    JsonToken currentToken = parser.getCurrentToken();
                    if (currentToken == null) {
                        currentToken = parser.nextToken();
                    }
                    if (currentToken != null) {
                        parser.skipToKey("error");
                        if (parser.getCurrentToken() == JsonToken.VALUE_STRING) {
                            detailString = parser.getText();
                        }
                        else if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
                            details = (GoogleJsonError)parser.parseAndClose((Class)GoogleJsonError.class);
                            detailString = details.toPrettyString();
                        }
                    }
                }
                catch (final IOException exception) {
                    exception.printStackTrace();
                }
                finally {
                    if (parser == null) {
                        response.ignore();
                    }
                    else if (details == null) {
                        parser.close();
                    }
                }
            }
            else {
                detailString = response.parseAsString();
            }
        }
        catch (final IOException exception2) {
            exception2.printStackTrace();
        }
        final StringBuilder message = HttpResponseException.computeMessageBuffer(response);
        if (!Strings.isNullOrEmpty(detailString)) {
            message.append(StringUtils.LINE_SEPARATOR).append(detailString);
            builder.setContent(detailString);
        }
        builder.setMessage(message.toString());
        return new GoogleJsonResponseException(builder, details);
    }
    
    public static HttpResponse execute(final JsonFactory jsonFactory, final HttpRequest request) throws GoogleJsonResponseException, IOException {
        Preconditions.checkNotNull((Object)jsonFactory);
        final boolean originalThrowExceptionOnExecuteError = request.getThrowExceptionOnExecuteError();
        if (originalThrowExceptionOnExecuteError) {
            request.setThrowExceptionOnExecuteError(false);
        }
        final HttpResponse response = request.execute();
        request.setThrowExceptionOnExecuteError(originalThrowExceptionOnExecuteError);
        if (!originalThrowExceptionOnExecuteError || response.isSuccessStatusCode()) {
            return response;
        }
        throw from(jsonFactory, response);
    }
}
