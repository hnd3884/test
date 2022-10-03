package org.apache.tika.parser.multiple;

import java.util.Arrays;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.ContentHandler;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.config.Param;
import java.util.Map;
import org.apache.tika.parser.Parser;
import java.util.Collection;
import org.apache.tika.mime.MediaTypeRegistry;
import java.util.List;

public class FallbackParser extends AbstractMultipleParser
{
    public static final List<MetadataPolicy> allowedPolicies;
    private static final long serialVersionUID = 5844409020977206167L;
    
    public FallbackParser(final MediaTypeRegistry registry, final Collection<? extends Parser> parsers, final Map<String, Param> params) {
        super(registry, parsers, params);
    }
    
    public FallbackParser(final MediaTypeRegistry registry, final MetadataPolicy policy, final Collection<? extends Parser> parsers) {
        super(registry, policy, parsers);
    }
    
    public FallbackParser(final MediaTypeRegistry registry, final MetadataPolicy policy, final Parser... parsers) {
        super(registry, policy, parsers);
    }
    
    @Override
    protected boolean parserCompleted(final Parser parser, final Metadata metadata, final ContentHandler handler, final ParseContext context, final Exception exception) {
        return exception != null;
    }
    
    static {
        allowedPolicies = Arrays.asList(MetadataPolicy.values());
    }
}
