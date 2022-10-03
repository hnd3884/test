package org.glassfish.jersey.uri;

import java.util.Comparator;

public final class PathPattern extends PatternWithGroups
{
    public static final PathPattern EMPTY_PATTERN;
    public static final PathPattern END_OF_PATH_PATTERN;
    public static final PathPattern OPEN_ROOT_PATH_PATTERN;
    public static final Comparator<PathPattern> COMPARATOR;
    private final UriTemplate template;
    
    public static PathPattern asClosed(final PathPattern pattern) {
        return new PathPattern(pattern.getTemplate().getTemplate(), RightHandPath.capturingZeroSegments);
    }
    
    private PathPattern() {
        this.template = UriTemplate.EMPTY;
    }
    
    public PathPattern(final String template) {
        this(new PathTemplate(template));
    }
    
    public PathPattern(final PathTemplate template) {
        super(postfixWithCapturingGroup(template.getPattern().getRegex()), addIndexForRightHandPathCapturingGroup(template.getNumberOfRegexGroups(), template.getPattern().getGroupIndexes()));
        this.template = template;
    }
    
    public PathPattern(final String template, final RightHandPath rhpp) {
        this(new PathTemplate(template), rhpp);
    }
    
    public PathPattern(final PathTemplate template, final RightHandPath rhpp) {
        super(postfixWithCapturingGroup(template.getPattern().getRegex(), rhpp), addIndexForRightHandPathCapturingGroup(template.getNumberOfRegexGroups(), template.getPattern().getGroupIndexes()));
        this.template = template;
    }
    
    public UriTemplate getTemplate() {
        return this.template;
    }
    
    private static String postfixWithCapturingGroup(final String regex) {
        return postfixWithCapturingGroup(regex, RightHandPath.capturingZeroOrMoreSegments);
    }
    
    private static String postfixWithCapturingGroup(String regex, final RightHandPath rhpp) {
        if (regex.endsWith("/")) {
            regex = regex.substring(0, regex.length() - 1);
        }
        return regex + rhpp.getRegex();
    }
    
    private static int[] addIndexForRightHandPathCapturingGroup(final int numberOfGroups, final int[] indexes) {
        if (indexes.length == 0) {
            return indexes;
        }
        final int[] cgIndexes = new int[indexes.length + 1];
        System.arraycopy(indexes, 0, cgIndexes, 0, indexes.length);
        cgIndexes[indexes.length] = numberOfGroups + 1;
        return cgIndexes;
    }
    
    static {
        EMPTY_PATTERN = new PathPattern();
        END_OF_PATH_PATTERN = new PathPattern("", RightHandPath.capturingZeroSegments);
        OPEN_ROOT_PATH_PATTERN = new PathPattern("", RightHandPath.capturingZeroOrMoreSegments);
        COMPARATOR = new Comparator<PathPattern>() {
            @Override
            public int compare(final PathPattern o1, final PathPattern o2) {
                return UriTemplate.COMPARATOR.compare(o1.template, o2.template);
            }
        };
    }
    
    public enum RightHandPath
    {
        capturingZeroOrMoreSegments("(/.*)?"), 
        capturingZeroSegments("(/)?");
        
        private final String regex;
        
        private RightHandPath(final String regex) {
            this.regex = regex;
        }
        
        private String getRegex() {
            return this.regex;
        }
    }
}
