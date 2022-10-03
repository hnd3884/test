package org.glassfish.jersey.servlet.spi;

import java.util.List;
import javax.servlet.FilterConfig;

public interface FilterUrlMappingsProvider
{
    List<String> getFilterUrlMappings(final FilterConfig p0);
}
