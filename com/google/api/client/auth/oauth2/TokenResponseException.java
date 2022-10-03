package com.google.api.client.auth.oauth2;

import com.google.api.client.util.StringUtils;
import com.google.api.client.util.Strings;
import java.io.IOException;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.http.HttpMediaType;
import com.google.api.client.util.Preconditions;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.HttpResponseException;

public class TokenResponseException extends HttpResponseException
{
    private static final long serialVersionUID = 4020689092957439244L;
    private final transient TokenErrorResponse details;
    
    TokenResponseException(final HttpResponseException.Builder builder, final TokenErrorResponse details) {
        super(builder);
        this.details = details;
    }
    
    public final TokenErrorResponse getDetails() {
        return this.details;
    }
    
    public static TokenResponseException from(final JsonFactory jsonFactory, final HttpResponse response) {
        final HttpResponseException.Builder builder = new HttpResponseException.Builder(response.getStatusCode(), response.getStatusMessage(), response.getHeaders());
        Preconditions.checkNotNull((Object)jsonFactory);
        TokenErrorResponse details = null;
        String detailString = null;
        final String contentType = response.getContentType();
        try {
            if (!response.isSuccessStatusCode() && contentType != null && response.getContent() != null && HttpMediaType.equalsIgnoreParameters("application/json; charset=UTF-8", contentType)) {
                details = (TokenErrorResponse)new JsonObjectParser(jsonFactory).parseAndClose(response.getContent(), response.getContentCharset(), (Class)TokenErrorResponse.class);
                detailString = details.toPrettyString();
            }
            else {
                detailString = response.parseAsString();
            }
        }
        catch (final IOException exception) {
            exception.printStackTrace();
        }
        final StringBuilder message = HttpResponseException.computeMessageBuffer(response);
        if (!Strings.isNullOrEmpty(detailString)) {
            message.append(StringUtils.LINE_SEPARATOR).append(detailString);
            builder.setContent(detailString);
        }
        builder.setMessage(message.toString());
        return new TokenResponseException(builder, details);
    }
}
