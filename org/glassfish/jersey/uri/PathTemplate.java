package org.glassfish.jersey.uri;

import org.glassfish.jersey.uri.internal.UriTemplateParser;

public final class PathTemplate extends UriTemplate
{
    public PathTemplate(final String path) {
        super(new PathTemplateParser(prefixWithSlash(path)));
    }
    
    private static String prefixWithSlash(final String path) {
        return (!path.isEmpty() && path.charAt(0) == '/') ? path : ("/" + path);
    }
    
    private static final class PathTemplateParser extends UriTemplateParser
    {
        public PathTemplateParser(final String path) {
            super(path);
        }
        
        @Override
        protected String encodeLiteralCharacters(final String literalCharacters) {
            return UriComponent.contextualEncode(literalCharacters, UriComponent.Type.PATH);
        }
    }
}
