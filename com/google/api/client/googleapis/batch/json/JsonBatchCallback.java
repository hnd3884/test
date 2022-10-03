package com.google.api.client.googleapis.batch.json;

import com.google.api.client.googleapis.json.GoogleJsonError;
import java.io.IOException;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.googleapis.json.GoogleJsonErrorContainer;
import com.google.api.client.googleapis.batch.BatchCallback;

public abstract class JsonBatchCallback<T> implements BatchCallback<T, GoogleJsonErrorContainer>
{
    @Override
    public final void onFailure(final GoogleJsonErrorContainer e, final HttpHeaders responseHeaders) throws IOException {
        this.onFailure(e.getError(), responseHeaders);
    }
    
    public abstract void onFailure(final GoogleJsonError p0, final HttpHeaders p1) throws IOException;
}
