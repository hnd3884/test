package io.opencensus.contrib.http.util;

import io.opencensus.stats.Measure;
import io.opencensus.tags.TagKey;
import io.opencensus.stats.BucketBoundaries;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;
import io.opencensus.stats.View;
import com.google.common.annotations.VisibleForTesting;
import io.opencensus.stats.Aggregation;

public final class HttpViewConstants
{
    @VisibleForTesting
    static final Aggregation COUNT;
    @VisibleForTesting
    static final Aggregation SIZE_DISTRIBUTION;
    @VisibleForTesting
    static final Aggregation LATENCY_DISTRIBUTION;
    public static final View HTTP_CLIENT_COMPLETED_COUNT_VIEW;
    public static final View HTTP_CLIENT_SENT_BYTES_VIEW;
    public static final View HTTP_CLIENT_RECEIVED_BYTES_VIEW;
    public static final View HTTP_CLIENT_ROUNDTRIP_LATENCY_VIEW;
    public static final View HTTP_SERVER_COMPLETED_COUNT_VIEW;
    public static final View HTTP_SERVER_RECEIVED_BYTES_VIEW;
    public static final View HTTP_SERVER_SENT_BYTES_VIEW;
    public static final View HTTP_SERVER_LATENCY_VIEW;
    
    private HttpViewConstants() {
    }
    
    static {
        COUNT = (Aggregation)Aggregation.Count.create();
        SIZE_DISTRIBUTION = (Aggregation)Aggregation.Distribution.create(BucketBoundaries.create((List)Collections.unmodifiableList((List<?>)Arrays.asList(0.0, 1024.0, 2048.0, 4096.0, 16384.0, 65536.0, 262144.0, 1048576.0, 4194304.0, 1.6777216E7, 6.7108864E7, 2.68435456E8, 1.073741824E9, 4.294967296E9))));
        LATENCY_DISTRIBUTION = (Aggregation)Aggregation.Distribution.create(BucketBoundaries.create((List)Collections.unmodifiableList((List<?>)Arrays.asList(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 8.0, 10.0, 13.0, 16.0, 20.0, 25.0, 30.0, 40.0, 50.0, 65.0, 80.0, 100.0, 130.0, 160.0, 200.0, 250.0, 300.0, 400.0, 500.0, 650.0, 800.0, 1000.0, 2000.0, 5000.0, 10000.0, 20000.0, 50000.0, 100000.0))));
        HTTP_CLIENT_COMPLETED_COUNT_VIEW = View.create(View.Name.create("opencensus.io/http/client/completed_count"), "Count of client-side HTTP requests completed", (Measure)HttpMeasureConstants.HTTP_CLIENT_ROUNDTRIP_LATENCY, HttpViewConstants.COUNT, (List)Arrays.asList(HttpMeasureConstants.HTTP_CLIENT_METHOD, HttpMeasureConstants.HTTP_CLIENT_STATUS));
        HTTP_CLIENT_SENT_BYTES_VIEW = View.create(View.Name.create("opencensus.io/http/client/sent_bytes"), "Size distribution of client-side HTTP request body", (Measure)HttpMeasureConstants.HTTP_CLIENT_SENT_BYTES, HttpViewConstants.SIZE_DISTRIBUTION, (List)Arrays.asList(HttpMeasureConstants.HTTP_CLIENT_METHOD, HttpMeasureConstants.HTTP_CLIENT_STATUS));
        HTTP_CLIENT_RECEIVED_BYTES_VIEW = View.create(View.Name.create("opencensus.io/http/client/received_bytes"), "Size distribution of client-side HTTP response body", (Measure)HttpMeasureConstants.HTTP_CLIENT_RECEIVED_BYTES, HttpViewConstants.SIZE_DISTRIBUTION, (List)Arrays.asList(HttpMeasureConstants.HTTP_CLIENT_METHOD, HttpMeasureConstants.HTTP_CLIENT_STATUS));
        HTTP_CLIENT_ROUNDTRIP_LATENCY_VIEW = View.create(View.Name.create("opencensus.io/http/client/roundtrip_latency"), "Roundtrip latency distribution of client-side HTTP requests", (Measure)HttpMeasureConstants.HTTP_CLIENT_ROUNDTRIP_LATENCY, HttpViewConstants.LATENCY_DISTRIBUTION, (List)Arrays.asList(HttpMeasureConstants.HTTP_CLIENT_METHOD, HttpMeasureConstants.HTTP_CLIENT_STATUS));
        HTTP_SERVER_COMPLETED_COUNT_VIEW = View.create(View.Name.create("opencensus.io/http/server/completed_count"), "Count of HTTP server-side requests serving completed", (Measure)HttpMeasureConstants.HTTP_SERVER_LATENCY, HttpViewConstants.COUNT, (List)Arrays.asList(HttpMeasureConstants.HTTP_SERVER_METHOD, HttpMeasureConstants.HTTP_SERVER_ROUTE, HttpMeasureConstants.HTTP_SERVER_STATUS));
        HTTP_SERVER_RECEIVED_BYTES_VIEW = View.create(View.Name.create("opencensus.io/http/server/received_bytes"), "Size distribution of server-side HTTP request body", (Measure)HttpMeasureConstants.HTTP_SERVER_RECEIVED_BYTES, HttpViewConstants.SIZE_DISTRIBUTION, (List)Arrays.asList(HttpMeasureConstants.HTTP_SERVER_METHOD, HttpMeasureConstants.HTTP_SERVER_ROUTE, HttpMeasureConstants.HTTP_SERVER_STATUS));
        HTTP_SERVER_SENT_BYTES_VIEW = View.create(View.Name.create("opencensus.io/http/server/sent_bytes"), "Size distribution of server-side HTTP response body", (Measure)HttpMeasureConstants.HTTP_SERVER_SENT_BYTES, HttpViewConstants.SIZE_DISTRIBUTION, (List)Arrays.asList(HttpMeasureConstants.HTTP_SERVER_METHOD, HttpMeasureConstants.HTTP_SERVER_ROUTE, HttpMeasureConstants.HTTP_SERVER_STATUS));
        HTTP_SERVER_LATENCY_VIEW = View.create(View.Name.create("opencensus.io/http/server/server_latency"), "Latency distribution of server-side HTTP requests serving", (Measure)HttpMeasureConstants.HTTP_SERVER_LATENCY, HttpViewConstants.LATENCY_DISTRIBUTION, (List)Arrays.asList(HttpMeasureConstants.HTTP_SERVER_METHOD, HttpMeasureConstants.HTTP_SERVER_ROUTE, HttpMeasureConstants.HTTP_SERVER_STATUS));
    }
}
