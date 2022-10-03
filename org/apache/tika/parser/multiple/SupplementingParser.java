package org.apache.tika.parser.multiple;

import org.apache.tika.parser.ParseContext;
import org.xml.sax.ContentHandler;
import org.apache.tika.metadata.Metadata;
import java.util.Arrays;
import org.apache.tika.config.Param;
import java.util.Map;
import org.apache.tika.parser.Parser;
import java.util.Collection;
import org.apache.tika.mime.MediaTypeRegistry;
import java.util.List;

public class SupplementingParser extends AbstractMultipleParser
{
    public static final List<MetadataPolicy> allowedPolicies;
    private static final long serialVersionUID = 313179254565350994L;
    
    public SupplementingParser(final MediaTypeRegistry registry, final Collection<? extends Parser> parsers, final Map<String, Param> params) {
        super(registry, parsers, params);
    }
    
    public SupplementingParser(final MediaTypeRegistry registry, final MetadataPolicy policy, final Parser... parsers) {
        this(registry, policy, Arrays.asList(parsers));
    }
    
    public SupplementingParser(final MediaTypeRegistry registry, final MetadataPolicy policy, final Collection<? extends Parser> parsers) {
        super(registry, policy, parsers);
        if (!SupplementingParser.allowedPolicies.contains(policy)) {
            throw new IllegalArgumentException("Unsupported policy for SupplementingParser: " + policy);
        }
    }
    
    @Override
    protected boolean parserCompleted(final Parser parser, final Metadata metadata, final ContentHandler handler, final ParseContext context, final Exception exception) {
        return exception != null || true;
    }
    
    static {
        allowedPolicies = Arrays.asList(MetadataPolicy.FIRST_WINS, MetadataPolicy.LAST_WINS, MetadataPolicy.KEEP_ALL);
    }
}
