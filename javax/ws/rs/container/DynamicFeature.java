package javax.ws.rs.container;

import javax.ws.rs.core.FeatureContext;

public interface DynamicFeature
{
    void configure(final ResourceInfo p0, final FeatureContext p1);
}
