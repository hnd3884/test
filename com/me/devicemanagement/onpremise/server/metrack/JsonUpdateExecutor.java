package com.me.devicemanagement.onpremise.server.metrack;

import org.json.JSONException;
import java.util.Iterator;
import org.json.JSONObject;

public class JsonUpdateExecutor
{
    public static void addOrUpdateJSON(final JsonUpdator jsonUpdator, final JSONObject destination, final JSONObject source) throws JSONException, Exception {
        final Iterator currentIterator = source.keys();
        while (currentIterator.hasNext()) {
            final String key = currentIterator.next().toString();
            if (destination.has(key)) {
                jsonUpdator.execute(destination, source, key);
            }
            else {
                destination.put(key, source.get(key));
            }
        }
    }
}
