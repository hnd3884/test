package org.jscep.transport.response;

import org.slf4j.LoggerFactory;
import java.util.Set;
import java.io.IOException;
import java.util.HashSet;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.apache.commons.io.Charsets;
import java.io.ByteArrayInputStream;
import java.util.EnumSet;
import org.slf4j.Logger;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public final class GetCaCapsResponseHandler implements ScepResponseHandler<Capabilities>
{
    private static final String TEXT_PLAIN = "text/plain";
    private static final Logger LOGGER;
    
    @Override
    public Capabilities getResponse(final byte[] content, final String mimeType) throws ContentException {
        if (mimeType == null || !mimeType.startsWith("text/plain")) {
            GetCaCapsResponseHandler.LOGGER.warn("Content-Type mismatch: was '{}', expected 'text/plain'", (Object)mimeType);
        }
        final EnumSet<Capability> caps = EnumSet.noneOf(Capability.class);
        if (GetCaCapsResponseHandler.LOGGER.isDebugEnabled()) {
            GetCaCapsResponseHandler.LOGGER.debug("CA capabilities:");
        }
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content), Charset.forName(Charsets.US_ASCII.name())));
        final Set<String> caCaps = new HashSet<String>();
        try {
            String capability;
            while ((capability = reader.readLine()) != null) {
                caCaps.add(capability);
            }
        }
        catch (final IOException e) {
            throw new InvalidContentTypeException(e);
        }
        finally {
            try {
                reader.close();
            }
            catch (final IOException e2) {
                GetCaCapsResponseHandler.LOGGER.error("Error closing reader", (Throwable)e2);
            }
        }
        for (final Capability enumValue : Capability.values()) {
            if (caCaps.contains(enumValue.toString())) {
                GetCaCapsResponseHandler.LOGGER.debug("[\u2713] {}", (Object)enumValue.getDescription());
                caps.add(enumValue);
            }
            else {
                GetCaCapsResponseHandler.LOGGER.debug("[\u2717] {}", (Object)enumValue.getDescription());
            }
        }
        return new Capabilities((Capability[])caps.toArray(new Capability[caps.size()]));
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)GetCaCapsResponseHandler.class);
    }
}
