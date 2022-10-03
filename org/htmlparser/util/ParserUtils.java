package org.htmlparser.util;

import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.lexer.Page;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.tags.CompositeTag;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.Node;

public class ParserUtils
{
    public static String removeChars(final String s, final char occur) {
        final StringBuffer newString = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            final char ch = s.charAt(i);
            if (ch != occur) {
                newString.append(ch);
            }
        }
        return newString.toString();
    }
    
    public static String removeEscapeCharacters(String inputString) {
        inputString = removeChars(inputString, '\r');
        inputString = removeChars(inputString, '\n');
        inputString = removeChars(inputString, '\t');
        return inputString;
    }
    
    public static String removeTrailingBlanks(String text) {
        char ch = ' ';
        while (ch == ' ') {
            ch = text.charAt(text.length() - 1);
            if (ch == ' ') {
                text = text.substring(0, text.length() - 1);
            }
        }
        return text;
    }
    
    public static Node[] findTypeInNode(final Node node, final Class type) {
        final NodeList ret = new NodeList();
        final NodeFilter filter = new NodeClassFilter(type);
        node.collectInto(ret, filter);
        return ret.toNodeArray();
    }
    
    public static String[] splitButDigits(final String input, final String charsDoNotBeRemoved) {
        final ArrayList output = new ArrayList();
        int minCapacity = 0;
        StringBuffer str = new StringBuffer();
        boolean charFound = false;
        boolean toBeAdd = false;
        for (int index = 0; index < input.length(); ++index) {
            charFound = false;
            for (int charsCount = 0; charsCount < charsDoNotBeRemoved.length(); ++charsCount) {
                if (charsDoNotBeRemoved.charAt(charsCount) == input.charAt(index)) {
                    charFound = true;
                }
            }
            if (Character.isDigit(input.charAt(index)) || charFound) {
                str.append(input.charAt(index));
                toBeAdd = false;
            }
            else if (!toBeAdd) {
                toBeAdd = true;
            }
            if (toBeAdd && str.length() != 0) {
                ++minCapacity;
                output.ensureCapacity(minCapacity);
                if (output.add(str.toString())) {
                    str = new StringBuffer();
                }
                else {
                    --minCapacity;
                }
            }
        }
        if (str.length() != 0) {
            ++minCapacity;
            output.ensureCapacity(minCapacity);
            if (output.add(str.toString())) {
                str = new StringBuffer();
            }
            else {
                --minCapacity;
            }
        }
        output.trimToSize();
        final Object[] outputObj = output.toArray();
        final String[] outputStr = new String[output.size()];
        for (int i = 0; i < output.size(); ++i) {
            outputStr[i] = new String((String)outputObj[i]);
        }
        return outputStr;
    }
    
    public static String trimButDigits(final String input, final String charsDoNotBeRemoved) {
        final StringBuffer output = new StringBuffer();
        boolean charFound = false;
        for (int index = 0; index < input.length(); ++index) {
            charFound = false;
            for (int charsCount = 0; charsCount < charsDoNotBeRemoved.length(); ++charsCount) {
                if (charsDoNotBeRemoved.charAt(charsCount) == input.charAt(index)) {
                    charFound = true;
                }
            }
            if (Character.isDigit(input.charAt(index)) || charFound) {
                output.append(input.charAt(index));
            }
        }
        return output.toString();
    }
    
    public static String trimButDigitsBeginEnd(final String input, final String charsDoNotBeRemoved) {
        String output = new String();
        int begin = 0;
        int end = input.length() - 1;
        boolean charFound = false;
        boolean ok = true;
        for (int index = begin; index < input.length() && ok; ++index) {
            charFound = false;
            for (int charsCount = 0; charsCount < charsDoNotBeRemoved.length(); ++charsCount) {
                if (charsDoNotBeRemoved.charAt(charsCount) == input.charAt(index)) {
                    charFound = true;
                }
            }
            if (Character.isDigit(input.charAt(index)) || charFound) {
                begin = index;
                ok = false;
            }
        }
        ok = true;
        for (int index = end; index >= 0 && ok; --index) {
            charFound = false;
            for (int charsCount = 0; charsCount < charsDoNotBeRemoved.length(); ++charsCount) {
                if (charsDoNotBeRemoved.charAt(charsCount) == input.charAt(index)) {
                    charFound = true;
                }
            }
            if (Character.isDigit(input.charAt(index)) || charFound) {
                end = index;
                ok = false;
            }
        }
        output = input.substring(begin, end + 1);
        return output;
    }
    
    public static String[] splitSpaces(final String input, final String charsToBeRemoved) {
        final ArrayList output = new ArrayList();
        int minCapacity = 0;
        StringBuffer str = new StringBuffer();
        boolean charFound = false;
        boolean toBeAdd = false;
        for (int index = 0; index < input.length(); ++index) {
            charFound = false;
            for (int charsCount = 0; charsCount < charsToBeRemoved.length(); ++charsCount) {
                if (charsToBeRemoved.charAt(charsCount) == input.charAt(index)) {
                    charFound = true;
                }
            }
            if (!Character.isWhitespace(input.charAt(index)) && !Character.isSpaceChar(input.charAt(index)) && !charFound) {
                str.append(input.charAt(index));
                toBeAdd = false;
            }
            else if (!toBeAdd) {
                toBeAdd = true;
            }
            if (toBeAdd && str.length() != 0) {
                ++minCapacity;
                output.ensureCapacity(minCapacity);
                if (output.add(str.toString())) {
                    str = new StringBuffer();
                }
                else {
                    --minCapacity;
                }
            }
        }
        if (str.length() != 0) {
            ++minCapacity;
            output.ensureCapacity(minCapacity);
            if (output.add(str.toString())) {
                str = new StringBuffer();
            }
            else {
                --minCapacity;
            }
        }
        output.trimToSize();
        final Object[] outputObj = output.toArray();
        final String[] outputStr = new String[output.size()];
        for (int i = 0; i < output.size(); ++i) {
            outputStr[i] = new String((String)outputObj[i]);
        }
        return outputStr;
    }
    
    public static String trimSpaces(final String input, final String charsToBeRemoved) {
        final StringBuffer output = new StringBuffer();
        boolean charFound = false;
        for (int index = 0; index < input.length(); ++index) {
            charFound = false;
            for (int charsCount = 0; charsCount < charsToBeRemoved.length(); ++charsCount) {
                if (charsToBeRemoved.charAt(charsCount) == input.charAt(index)) {
                    charFound = true;
                }
            }
            if (!Character.isWhitespace(input.charAt(index)) && !Character.isSpaceChar(input.charAt(index)) && !charFound) {
                output.append(input.charAt(index));
            }
        }
        return output.toString();
    }
    
    public static String trimSpacesBeginEnd(final String input, final String charsToBeRemoved) {
        String output = new String();
        int begin = 0;
        int end = input.length() - 1;
        boolean charFound = false;
        boolean ok = true;
        for (int index = begin; index < input.length() && ok; ++index) {
            charFound = false;
            for (int charsCount = 0; charsCount < charsToBeRemoved.length(); ++charsCount) {
                if (charsToBeRemoved.charAt(charsCount) == input.charAt(index)) {
                    charFound = true;
                }
            }
            if (!Character.isWhitespace(input.charAt(index)) && !Character.isSpaceChar(input.charAt(index)) && !charFound) {
                begin = index;
                ok = false;
            }
        }
        ok = true;
        for (int index = end; index >= 0 && ok; --index) {
            charFound = false;
            for (int charsCount = 0; charsCount < charsToBeRemoved.length(); ++charsCount) {
                if (charsToBeRemoved.charAt(charsCount) == input.charAt(index)) {
                    charFound = true;
                }
            }
            if (!Character.isWhitespace(input.charAt(index)) && !Character.isSpaceChar(input.charAt(index)) && !charFound) {
                end = index;
                ok = false;
            }
        }
        output = input.substring(begin, end + 1);
        return output;
    }
    
    public static String[] splitButChars(final String input, final String charsDoNotBeRemoved) {
        final ArrayList output = new ArrayList();
        int minCapacity = 0;
        StringBuffer str = new StringBuffer();
        boolean charFound = false;
        boolean toBeAdd = false;
        for (int index = 0; index < input.length(); ++index) {
            charFound = false;
            for (int charsCount = 0; charsCount < charsDoNotBeRemoved.length(); ++charsCount) {
                if (charsDoNotBeRemoved.charAt(charsCount) == input.charAt(index)) {
                    charFound = true;
                }
            }
            if (charFound) {
                str.append(input.charAt(index));
                toBeAdd = false;
            }
            else if (!toBeAdd) {
                toBeAdd = true;
            }
            if (toBeAdd && str.length() != 0) {
                ++minCapacity;
                output.ensureCapacity(minCapacity);
                if (output.add(str.toString())) {
                    str = new StringBuffer();
                }
                else {
                    --minCapacity;
                }
            }
        }
        if (str.length() != 0) {
            ++minCapacity;
            output.ensureCapacity(minCapacity);
            if (output.add(str.toString())) {
                str = new StringBuffer();
            }
            else {
                --minCapacity;
            }
        }
        output.trimToSize();
        final Object[] outputObj = output.toArray();
        final String[] outputStr = new String[output.size()];
        for (int i = 0; i < output.size(); ++i) {
            outputStr[i] = new String((String)outputObj[i]);
        }
        return outputStr;
    }
    
    public static String trimButChars(final String input, final String charsDoNotBeRemoved) {
        final StringBuffer output = new StringBuffer();
        boolean charFound = false;
        for (int index = 0; index < input.length(); ++index) {
            charFound = false;
            for (int charsCount = 0; charsCount < charsDoNotBeRemoved.length(); ++charsCount) {
                if (charsDoNotBeRemoved.charAt(charsCount) == input.charAt(index)) {
                    charFound = true;
                }
            }
            if (charFound) {
                output.append(input.charAt(index));
            }
        }
        return output.toString();
    }
    
    public static String trimButCharsBeginEnd(final String input, final String charsDoNotBeRemoved) {
        String output = new String();
        int begin = 0;
        int end = input.length() - 1;
        boolean charFound = false;
        boolean ok = true;
        for (int index = begin; index < input.length() && ok; ++index) {
            charFound = false;
            for (int charsCount = 0; charsCount < charsDoNotBeRemoved.length(); ++charsCount) {
                if (charsDoNotBeRemoved.charAt(charsCount) == input.charAt(index)) {
                    charFound = true;
                }
            }
            if (charFound) {
                begin = index;
                ok = false;
            }
        }
        ok = true;
        for (int index = end; index >= 0 && ok; --index) {
            charFound = false;
            for (int charsCount = 0; charsCount < charsDoNotBeRemoved.length(); ++charsCount) {
                if (charsDoNotBeRemoved.charAt(charsCount) == input.charAt(index)) {
                    charFound = true;
                }
            }
            if (charFound) {
                end = index;
                ok = false;
            }
        }
        output = input.substring(begin, end + 1);
        return output;
    }
    
    public static String[] splitChars(final String input, final String charsToBeRemoved) {
        final ArrayList output = new ArrayList();
        int minCapacity = 0;
        StringBuffer str = new StringBuffer();
        boolean charFound = false;
        boolean toBeAdd = false;
        for (int index = 0; index < input.length(); ++index) {
            charFound = false;
            for (int charsCount = 0; charsCount < charsToBeRemoved.length(); ++charsCount) {
                if (charsToBeRemoved.charAt(charsCount) == input.charAt(index)) {
                    charFound = true;
                }
            }
            if (!charFound) {
                str.append(input.charAt(index));
                toBeAdd = false;
            }
            else if (!toBeAdd) {
                toBeAdd = true;
            }
            if (toBeAdd && str.length() != 0) {
                ++minCapacity;
                output.ensureCapacity(minCapacity);
                if (output.add(str.toString())) {
                    str = new StringBuffer();
                }
                else {
                    --minCapacity;
                }
            }
        }
        if (str.length() != 0) {
            ++minCapacity;
            output.ensureCapacity(minCapacity);
            if (output.add(str.toString())) {
                str = new StringBuffer();
            }
            else {
                --minCapacity;
            }
        }
        output.trimToSize();
        final Object[] outputObj = output.toArray();
        final String[] outputStr = new String[output.size()];
        for (int i = 0; i < output.size(); ++i) {
            outputStr[i] = new String((String)outputObj[i]);
        }
        return outputStr;
    }
    
    public static String trimChars(final String input, final String charsToBeRemoved) {
        final StringBuffer output = new StringBuffer();
        boolean charFound = false;
        for (int index = 0; index < input.length(); ++index) {
            charFound = false;
            for (int charsCount = 0; charsCount < charsToBeRemoved.length(); ++charsCount) {
                if (charsToBeRemoved.charAt(charsCount) == input.charAt(index)) {
                    charFound = true;
                }
            }
            if (!charFound) {
                output.append(input.charAt(index));
            }
        }
        return output.toString();
    }
    
    public static String trimCharsBeginEnd(final String input, final String charsToBeRemoved) {
        String output = new String();
        int begin = 0;
        int end = input.length() - 1;
        boolean charFound = false;
        boolean ok = true;
        for (int index = begin; index < input.length() && ok; ++index) {
            charFound = false;
            for (int charsCount = 0; charsCount < charsToBeRemoved.length(); ++charsCount) {
                if (charsToBeRemoved.charAt(charsCount) == input.charAt(index)) {
                    charFound = true;
                }
            }
            if (!charFound) {
                begin = index;
                ok = false;
            }
        }
        ok = true;
        for (int index = end; index >= 0 && ok; --index) {
            charFound = false;
            for (int charsCount = 0; charsCount < charsToBeRemoved.length(); ++charsCount) {
                if (charsToBeRemoved.charAt(charsCount) == input.charAt(index)) {
                    charFound = true;
                }
            }
            if (!charFound) {
                end = index;
                ok = false;
            }
        }
        output = input.substring(begin, end + 1);
        return output;
    }
    
    public static String[] splitTags(final String input, final String[] tags) throws ParserException, UnsupportedEncodingException {
        return splitTags(input, tags, true, true);
    }
    
    public static String[] splitTags(final String input, final String[] tags, final boolean recursive, final boolean insideTag) throws ParserException, UnsupportedEncodingException {
        ArrayList outputArrayList = new ArrayList();
        int minCapacity = 0;
        String output = new String();
        String inputModified = new String(input);
        String[] outputStr = new String[0];
        String dummyString = createDummyString(' ', input.length());
        for (int i = 0; i < tags.length; ++i) {
            final NodeList links = getLinks(inputModified, tags[i], recursive);
            for (int j = 0; j < links.size(); ++j) {
                final CompositeTag beginTag = (CompositeTag)links.elementAt(j);
                final Tag endTag = beginTag.getEndTag();
                final int beginTagBegin = beginTag.getStartPosition();
                final int endTagBegin = beginTag.getEndPosition();
                final int beginTagEnd = endTag.getStartPosition();
                final int endTagEnd = endTag.getEndPosition();
                if (insideTag) {
                    dummyString = modifyDummyString(new String(dummyString), beginTagBegin, endTagEnd);
                }
                else {
                    dummyString = modifyDummyString(new String(dummyString), beginTagBegin, endTagBegin);
                    dummyString = modifyDummyString(new String(dummyString), beginTagEnd, endTagEnd);
                }
            }
            int k = dummyString.indexOf(32);
            while (k < dummyString.length() && k != -1) {
                final int kNew = dummyString.indexOf(42, k);
                if (kNew != -1) {
                    output = inputModified.substring(k, kNew);
                    k = dummyString.indexOf(32, kNew);
                    ++minCapacity;
                    outputArrayList.ensureCapacity(minCapacity);
                    if (outputArrayList.add(output)) {
                        output = new String();
                    }
                    else {
                        --minCapacity;
                    }
                }
                else {
                    output = inputModified.substring(k, dummyString.length());
                    k = kNew;
                    ++minCapacity;
                    outputArrayList.ensureCapacity(minCapacity);
                    if (outputArrayList.add(output)) {
                        output = new String();
                    }
                    else {
                        --minCapacity;
                    }
                }
            }
            final StringBuffer outputStringBuffer = new StringBuffer();
            outputArrayList.trimToSize();
            final Object[] outputObj = outputArrayList.toArray();
            outputStr = new String[outputArrayList.size()];
            for (int l = 0; l < outputArrayList.size(); ++l) {
                outputStringBuffer.append(outputStr[l] = new String((String)outputObj[l]));
            }
            outputArrayList = new ArrayList();
            inputModified = new String(outputStringBuffer.toString());
            dummyString = createDummyString(' ', inputModified.length());
        }
        return outputStr;
    }
    
    public static String[] splitTags(final String input, final Class nodeType) throws ParserException, UnsupportedEncodingException {
        return splitTags(input, new NodeClassFilter(nodeType), true, true);
    }
    
    public static String[] splitTags(final String input, final Class nodeType, final boolean recursive, final boolean insideTag) throws ParserException, UnsupportedEncodingException {
        return splitTags(input, new NodeClassFilter(nodeType), recursive, insideTag);
    }
    
    public static String[] splitTags(final String input, final NodeFilter filter) throws ParserException, UnsupportedEncodingException {
        return splitTags(input, filter, true, true);
    }
    
    public static String[] splitTags(final String input, final NodeFilter filter, final boolean recursive, final boolean insideTag) throws ParserException, UnsupportedEncodingException {
        final ArrayList outputArrayList = new ArrayList();
        int minCapacity = 0;
        String output = new String();
        String dummyString = createDummyString(' ', input.length());
        final NodeList links = getLinks(input, filter, recursive);
        for (int j = 0; j < links.size(); ++j) {
            final CompositeTag beginTag = (CompositeTag)links.elementAt(j);
            final Tag endTag = beginTag.getEndTag();
            final int beginTagBegin = beginTag.getStartPosition();
            final int endTagBegin = beginTag.getEndPosition();
            final int beginTagEnd = endTag.getStartPosition();
            final int endTagEnd = endTag.getEndPosition();
            if (insideTag) {
                dummyString = modifyDummyString(new String(dummyString), beginTagBegin, endTagEnd);
            }
            else {
                dummyString = modifyDummyString(new String(dummyString), beginTagBegin, endTagBegin);
                dummyString = modifyDummyString(new String(dummyString), beginTagEnd, endTagEnd);
            }
        }
        int k = dummyString.indexOf(32);
        while (k < dummyString.length() && k != -1) {
            final int kNew = dummyString.indexOf(42, k);
            if (kNew != -1) {
                output = input.substring(k, kNew);
                k = dummyString.indexOf(32, kNew);
                ++minCapacity;
                outputArrayList.ensureCapacity(minCapacity);
                if (outputArrayList.add(output)) {
                    output = new String();
                }
                else {
                    --minCapacity;
                }
            }
            else {
                output = input.substring(k, dummyString.length());
                k = kNew;
                ++minCapacity;
                outputArrayList.ensureCapacity(minCapacity);
                if (outputArrayList.add(output)) {
                    output = new String();
                }
                else {
                    --minCapacity;
                }
            }
        }
        outputArrayList.trimToSize();
        final Object[] outputObj = outputArrayList.toArray();
        final String[] outputStr = new String[outputArrayList.size()];
        for (int i = 0; i < outputArrayList.size(); ++i) {
            outputStr[i] = new String((String)outputObj[i]);
        }
        return outputStr;
    }
    
    public static String trimAllTags(final String input, final boolean inside) {
        final StringBuffer output = new StringBuffer();
        if (inside) {
            if (input.indexOf(60) == -1 || input.lastIndexOf(62) == -1 || input.lastIndexOf(62) < input.indexOf(60)) {
                output.append(input);
            }
            else {
                output.append(input.substring(0, input.indexOf(60)));
                output.append(input.substring(input.lastIndexOf(62) + 1, input.length()));
            }
        }
        else {
            boolean write = true;
            for (int index = 0; index < input.length(); ++index) {
                if (input.charAt(index) == '<' && write) {
                    write = false;
                }
                if (write) {
                    output.append(input.charAt(index));
                }
                if (input.charAt(index) == '>' && !write) {
                    write = true;
                }
            }
        }
        return output.toString();
    }
    
    public static String trimTags(final String input, final String[] tags) throws ParserException, UnsupportedEncodingException {
        return trimTags(input, tags, true, true);
    }
    
    public static String trimTags(final String input, final String[] tags, final boolean recursive, final boolean insideTag) throws ParserException, UnsupportedEncodingException {
        StringBuffer output = new StringBuffer();
        String inputModified = new String(input);
        String dummyString = createDummyString(' ', input.length());
        for (int i = 0; i < tags.length; ++i) {
            output = new StringBuffer();
            final NodeList links = getLinks(inputModified, tags[i], recursive);
            for (int j = 0; j < links.size(); ++j) {
                final CompositeTag beginTag = (CompositeTag)links.elementAt(j);
                final Tag endTag = beginTag.getEndTag();
                final int beginTagBegin = beginTag.getStartPosition();
                final int endTagBegin = beginTag.getEndPosition();
                final int beginTagEnd = endTag.getStartPosition();
                final int endTagEnd = endTag.getEndPosition();
                if (insideTag) {
                    dummyString = modifyDummyString(new String(dummyString), beginTagBegin, endTagEnd);
                }
                else {
                    dummyString = modifyDummyString(new String(dummyString), beginTagBegin, endTagBegin);
                    dummyString = modifyDummyString(new String(dummyString), beginTagEnd, endTagEnd);
                }
            }
            int k = dummyString.indexOf(32);
            while (k < dummyString.length() && k != -1) {
                final int kNew = dummyString.indexOf(42, k);
                if (kNew != -1) {
                    output = output.append(inputModified.substring(k, kNew));
                    k = dummyString.indexOf(32, kNew);
                }
                else {
                    output = output.append(inputModified.substring(k, dummyString.length()));
                    k = kNew;
                }
            }
            inputModified = new String(output);
            dummyString = createDummyString(' ', inputModified.length());
        }
        return output.toString();
    }
    
    public static String trimTags(final String input, final Class nodeType) throws ParserException, UnsupportedEncodingException {
        return trimTags(input, new NodeClassFilter(nodeType), true, true);
    }
    
    public static String trimTags(final String input, final Class nodeType, final boolean recursive, final boolean insideTag) throws ParserException, UnsupportedEncodingException {
        return trimTags(input, new NodeClassFilter(nodeType), recursive, insideTag);
    }
    
    public static String trimTags(final String input, final NodeFilter filter) throws ParserException, UnsupportedEncodingException {
        return trimTags(input, filter, true, true);
    }
    
    public static String trimTags(final String input, final NodeFilter filter, final boolean recursive, final boolean insideTag) throws ParserException, UnsupportedEncodingException {
        StringBuffer output = new StringBuffer();
        String dummyString = createDummyString(' ', input.length());
        final NodeList links = getLinks(input, filter, recursive);
        for (int j = 0; j < links.size(); ++j) {
            final CompositeTag beginTag = (CompositeTag)links.elementAt(j);
            final Tag endTag = beginTag.getEndTag();
            final int beginTagBegin = beginTag.getStartPosition();
            final int endTagBegin = beginTag.getEndPosition();
            final int beginTagEnd = endTag.getStartPosition();
            final int endTagEnd = endTag.getEndPosition();
            if (insideTag) {
                dummyString = modifyDummyString(new String(dummyString), beginTagBegin, endTagEnd);
            }
            else {
                dummyString = modifyDummyString(new String(dummyString), beginTagBegin, endTagBegin);
                dummyString = modifyDummyString(new String(dummyString), beginTagEnd, endTagEnd);
            }
        }
        int k = dummyString.indexOf(32);
        while (k < dummyString.length() && k != -1) {
            final int kNew = dummyString.indexOf(42, k);
            if (kNew != -1) {
                output = output.append(input.substring(k, kNew));
                k = dummyString.indexOf(32, kNew);
            }
            else {
                output = output.append(input.substring(k, dummyString.length()));
                k = kNew;
            }
        }
        return output.toString();
    }
    
    public static Parser createParserParsingAnInputString(final String input) throws ParserException, UnsupportedEncodingException {
        final Parser parser = new Parser();
        final Lexer lexer = new Lexer();
        final Page page = new Page(input);
        lexer.setPage(page);
        parser.setLexer(lexer);
        return parser;
    }
    
    private static NodeList getLinks(final String output, final String tag, final boolean recursive) throws ParserException, UnsupportedEncodingException {
        Parser parser = new Parser();
        final NodeFilter filterLink = new TagNameFilter(tag);
        NodeList links = new NodeList();
        parser = createParserParsingAnInputString(output);
        links = parser.extractAllNodesThatMatch(filterLink);
        if (!recursive) {
            for (int j = 0; j < links.size(); ++j) {
                final CompositeTag jStartTag = (CompositeTag)links.elementAt(j);
                final Tag jEndTag = jStartTag.getEndTag();
                final int jStartTagBegin = jStartTag.getStartPosition();
                final int jEndTagEnd = jEndTag.getEndPosition();
                for (int k = 0; k < links.size(); ++k) {
                    final CompositeTag kStartTag = (CompositeTag)links.elementAt(k);
                    final Tag kEndTag = kStartTag.getEndTag();
                    final int kStartTagBegin = kStartTag.getStartPosition();
                    final int kEndTagEnd = kEndTag.getEndPosition();
                    if (k != j && kStartTagBegin > jStartTagBegin && kEndTagEnd < jEndTagEnd) {
                        links.remove(k);
                        --k;
                        --j;
                    }
                }
            }
        }
        return links;
    }
    
    private static NodeList getLinks(final String output, final NodeFilter filter, final boolean recursive) throws ParserException, UnsupportedEncodingException {
        Parser parser = new Parser();
        NodeList links = new NodeList();
        parser = createParserParsingAnInputString(output);
        links = parser.extractAllNodesThatMatch(filter);
        if (!recursive) {
            for (int j = 0; j < links.size(); ++j) {
                final CompositeTag jStartTag = (CompositeTag)links.elementAt(j);
                final Tag jEndTag = jStartTag.getEndTag();
                final int jStartTagBegin = jStartTag.getStartPosition();
                final int jEndTagEnd = jEndTag.getEndPosition();
                for (int k = 0; k < links.size(); ++k) {
                    final CompositeTag kStartTag = (CompositeTag)links.elementAt(k);
                    final Tag kEndTag = kStartTag.getEndTag();
                    final int kStartTagBegin = kStartTag.getStartPosition();
                    final int kEndTagEnd = kEndTag.getEndPosition();
                    if (k != j && kStartTagBegin > jStartTagBegin && kEndTagEnd < jEndTagEnd) {
                        links.remove(k);
                        --k;
                        --j;
                    }
                }
            }
        }
        return links;
    }
    
    private static String createDummyString(final char fillingChar, final int length) {
        StringBuffer dummyStringBuffer = new StringBuffer();
        for (int j = 0; j < length; ++j) {
            dummyStringBuffer = dummyStringBuffer.append(fillingChar);
        }
        return new String(dummyStringBuffer);
    }
    
    private static String modifyDummyString(final String dummyString, final int beginTag, final int endTag) {
        final String dummyStringInterval = createDummyString('*', endTag - beginTag);
        return new String(dummyString.substring(0, beginTag) + dummyStringInterval + dummyString.substring(endTag, dummyString.length()));
    }
}
