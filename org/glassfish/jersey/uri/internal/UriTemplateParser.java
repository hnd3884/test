package org.glassfish.jersey.uri.internal;

import java.util.regex.Matcher;
import org.glassfish.jersey.uri.UriComponent;
import java.util.NoSuchElementException;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.util.regex.PatternSyntaxException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Set;

public class UriTemplateParser
{
    static final int[] EMPTY_INT_ARRAY;
    private static final Set<Character> RESERVED_REGEX_CHARACTERS;
    private static final String[] HEX_TO_UPPERCASE_REGEX;
    public static final Pattern TEMPLATE_VALUE_PATTERN;
    private final String template;
    private final StringBuffer regex;
    private final StringBuffer normalizedTemplate;
    private final StringBuffer literalCharactersBuffer;
    private final Pattern pattern;
    private final List<String> names;
    private final List<Integer> groupCounts;
    private final Map<String, Pattern> nameToPattern;
    private int numOfExplicitRegexes;
    private int skipGroup;
    private int literalCharacters;
    
    private static Set<Character> initReserved() {
        final char[] reserved = { '.', '^', '&', '!', '?', '-', ':', '<', '(', '[', '$', '=', ')', ']', ',', '>', '*', '+', '|' };
        final Set<Character> s = new HashSet<Character>(reserved.length);
        for (final char c : reserved) {
            s.add(c);
        }
        return s;
    }
    
    public UriTemplateParser(final String template) throws IllegalArgumentException {
        this.regex = new StringBuffer();
        this.normalizedTemplate = new StringBuffer();
        this.literalCharactersBuffer = new StringBuffer();
        this.names = new ArrayList<String>();
        this.groupCounts = new ArrayList<Integer>();
        this.nameToPattern = new HashMap<String, Pattern>();
        if (template == null || template.isEmpty()) {
            throw new IllegalArgumentException("Template is null or has zero length");
        }
        this.template = template;
        this.parse(new CharacterIterator(template));
        try {
            this.pattern = Pattern.compile(this.regex.toString());
        }
        catch (final PatternSyntaxException ex) {
            throw new IllegalArgumentException("Invalid syntax for the template expression '" + (Object)this.regex + "'", ex);
        }
    }
    
    public final String getTemplate() {
        return this.template;
    }
    
    public final Pattern getPattern() {
        return this.pattern;
    }
    
    public final String getNormalizedTemplate() {
        return this.normalizedTemplate.toString();
    }
    
    public final Map<String, Pattern> getNameToPattern() {
        return this.nameToPattern;
    }
    
    public final List<String> getNames() {
        return this.names;
    }
    
    public final List<Integer> getGroupCounts() {
        return this.groupCounts;
    }
    
    public final int[] getGroupIndexes() {
        if (this.names.isEmpty()) {
            return UriTemplateParser.EMPTY_INT_ARRAY;
        }
        final int[] indexes = new int[this.names.size()];
        indexes[0] = 0 + this.groupCounts.get(0);
        for (int i = 1; i < indexes.length; ++i) {
            indexes[i] = indexes[i - 1] + this.groupCounts.get(i);
        }
        return indexes;
    }
    
    public final int getNumberOfExplicitRegexes() {
        return this.numOfExplicitRegexes;
    }
    
    public final int getNumberOfRegexGroups() {
        if (this.groupCounts.isEmpty()) {
            return 0;
        }
        final int[] groupIndex = this.getGroupIndexes();
        return groupIndex[groupIndex.length - 1] + this.skipGroup;
    }
    
    public final int getNumberOfLiteralCharacters() {
        return this.literalCharacters;
    }
    
    protected String encodeLiteralCharacters(final String characters) {
        return characters;
    }
    
    private void parse(final CharacterIterator ci) {
        try {
            while (ci.hasNext()) {
                final char c = ci.next();
                if (c == '{') {
                    this.processLiteralCharacters();
                    this.skipGroup = this.parseName(ci, this.skipGroup);
                }
                else {
                    this.literalCharactersBuffer.append(c);
                }
            }
            this.processLiteralCharacters();
        }
        catch (final NoSuchElementException ex) {
            throw new IllegalArgumentException(LocalizationMessages.ERROR_TEMPLATE_PARSER_INVALID_SYNTAX_TERMINATED(this.template), ex);
        }
    }
    
    private void processLiteralCharacters() {
        if (this.literalCharactersBuffer.length() > 0) {
            this.literalCharacters += this.literalCharactersBuffer.length();
            final String s = this.encodeLiteralCharacters(this.literalCharactersBuffer.toString());
            this.normalizedTemplate.append(s);
            for (int i = 0; i < s.length(); ++i) {
                final char c = s.charAt(i);
                if (UriTemplateParser.RESERVED_REGEX_CHARACTERS.contains(c)) {
                    this.regex.append("\\");
                    this.regex.append(c);
                }
                else if (c == '%') {
                    final char c2 = s.charAt(i + 1);
                    final char c3 = s.charAt(i + 2);
                    if (UriComponent.isHexCharacter(c2) && UriComponent.isHexCharacter(c3)) {
                        this.regex.append("%").append(UriTemplateParser.HEX_TO_UPPERCASE_REGEX[c2]).append(UriTemplateParser.HEX_TO_UPPERCASE_REGEX[c3]);
                        i += 2;
                    }
                }
                else {
                    this.regex.append(c);
                }
            }
            this.literalCharactersBuffer.setLength(0);
        }
    }
    
    private static String[] initHexToUpperCaseRegex() {
        final String[] table = new String[128];
        for (int i = 0; i < table.length; ++i) {
            table[i] = String.valueOf((char)i);
        }
        for (char c = 'a'; c <= 'f'; ++c) {
            table[c] = "[" + c + (char)(c - 'a' + 65) + "]";
        }
        for (char c = 'A'; c <= 'F'; ++c) {
            table[c] = "[" + (char)(c - 'A' + 97) + c + "]";
        }
        return table;
    }
    
    private int parseName(final CharacterIterator ci, int skipGroup) {
        char c = this.consumeWhiteSpace(ci);
        char paramType = 'p';
        final StringBuilder nameBuffer = new StringBuilder();
        if (c == '?' || c == ';') {
            paramType = c;
            c = ci.next();
        }
        if (Character.isLetterOrDigit(c) || c == '_') {
            nameBuffer.append(c);
            String nameRegexString = "";
            while (true) {
                c = ci.next();
                if (Character.isLetterOrDigit(c) || c == '_' || c == '-' || c == '.') {
                    nameBuffer.append(c);
                }
                else {
                    if (c != ',' || paramType == 'p') {
                        break;
                    }
                    nameBuffer.append(c);
                }
            }
            if (c == ':' && paramType == 'p') {
                nameRegexString = this.parseRegex(ci);
            }
            else if (c != '}') {
                if (c != ' ') {
                    throw new IllegalArgumentException(LocalizationMessages.ERROR_TEMPLATE_PARSER_ILLEGAL_CHAR_PART_OF_NAME(c, ci.pos(), this.template));
                }
                c = this.consumeWhiteSpace(ci);
                if (c == ':') {
                    nameRegexString = this.parseRegex(ci);
                }
                else if (c != '}') {
                    throw new IllegalArgumentException(LocalizationMessages.ERROR_TEMPLATE_PARSER_ILLEGAL_CHAR_AFTER_NAME(c, ci.pos(), this.template));
                }
            }
            String name = nameBuffer.toString();
            try {
                Pattern namePattern;
                if (paramType == '?' || paramType == ';') {
                    final String[] subNames = name.split(",\\s?");
                    final StringBuilder regexBuilder = new StringBuilder((paramType == '?') ? "\\?" : ";");
                    final String separator = (paramType == '?') ? "\\&" : ";/\\?";
                    boolean first = true;
                    regexBuilder.append("(");
                    for (final String subName : subNames) {
                        regexBuilder.append("(&?");
                        regexBuilder.append(subName);
                        regexBuilder.append("(=([^");
                        regexBuilder.append(separator);
                        regexBuilder.append("]*))?");
                        regexBuilder.append(")");
                        if (!first) {
                            regexBuilder.append("|");
                        }
                        this.names.add(subName);
                        this.groupCounts.add(first ? 5 : 3);
                        first = false;
                    }
                    skipGroup = 1;
                    regexBuilder.append(")*");
                    namePattern = Pattern.compile(regexBuilder.toString());
                    name = paramType + name;
                }
                else {
                    this.names.add(name);
                    if (!nameRegexString.isEmpty()) {
                        ++this.numOfExplicitRegexes;
                    }
                    namePattern = (nameRegexString.isEmpty() ? UriTemplateParser.TEMPLATE_VALUE_PATTERN : Pattern.compile(nameRegexString));
                    if (this.nameToPattern.containsKey(name)) {
                        if (!this.nameToPattern.get(name).equals(namePattern)) {
                            throw new IllegalArgumentException(LocalizationMessages.ERROR_TEMPLATE_PARSER_NAME_MORE_THAN_ONCE(name, this.template));
                        }
                    }
                    else {
                        this.nameToPattern.put(name, namePattern);
                    }
                    final Matcher m = namePattern.matcher("");
                    final int g = m.groupCount();
                    this.groupCounts.add(1 + skipGroup);
                    skipGroup = g;
                }
                this.regex.append('(').append(namePattern).append(')');
                this.normalizedTemplate.append('{').append(name).append('}');
            }
            catch (final PatternSyntaxException ex) {
                throw new IllegalArgumentException(LocalizationMessages.ERROR_TEMPLATE_PARSER_INVALID_SYNTAX(nameRegexString, name, this.template), ex);
            }
            return skipGroup;
        }
        throw new IllegalArgumentException(LocalizationMessages.ERROR_TEMPLATE_PARSER_ILLEGAL_CHAR_START_NAME(c, ci.pos(), this.template));
    }
    
    private String parseRegex(final CharacterIterator ci) {
        final StringBuilder regexBuffer = new StringBuilder();
        int braceCount = 1;
        while (true) {
            final char c = ci.next();
            if (c == '{') {
                ++braceCount;
            }
            else if (c == '}' && --braceCount == 0) {
                break;
            }
            regexBuffer.append(c);
        }
        return regexBuffer.toString().trim();
    }
    
    private char consumeWhiteSpace(final CharacterIterator ci) {
        char c;
        do {
            c = ci.next();
        } while (Character.isWhitespace(c));
        return c;
    }
    
    static {
        EMPTY_INT_ARRAY = new int[0];
        RESERVED_REGEX_CHARACTERS = initReserved();
        HEX_TO_UPPERCASE_REGEX = initHexToUpperCaseRegex();
        TEMPLATE_VALUE_PATTERN = Pattern.compile("[^/]+");
    }
}
