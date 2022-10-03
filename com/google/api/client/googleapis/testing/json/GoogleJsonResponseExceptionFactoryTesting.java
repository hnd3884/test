package com.google.api.client.googleapis.testing.json;

import java.io.IOException;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.testing.http.HttpTesting;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.Beta;

@Beta
public final class GoogleJsonResponseExceptionFactoryTesting
{
    public static GoogleJsonResponseException newMock(final JsonFactory jsonFactory, final int httpCode, final String reasonPhrase) throws IOException {
        final MockLowLevelHttpResponse otherServiceUnavaiableLowLevelResponse = new MockLowLevelHttpResponse().setStatusCode(httpCode).setReasonPhrase(reasonPhrase).setContentType("application/json; charset=UTF-8").setContent("{ \"error\": { \"errors\": [ { \"reason\": \"" + reasonPhrase + "\" } ], \"code\": " + httpCode + " } }");
        final MockHttpTransport otherTransport = new MockHttpTransport.Builder().setLowLevelHttpResponse(otherServiceUnavaiableLowLevelResponse).build();
        final HttpRequest otherRequest = otherTransport.createRequestFactory().buildGetRequest(HttpTesting.SIMPLE_GENERIC_URL);
        otherRequest.setThrowExceptionOnExecuteError(false);
        final HttpResponse otherServiceUnavailableResponse = otherRequest.execute();
        return GoogleJsonResponseException.from(jsonFactory, otherServiceUnavailableResponse);
    }
}
