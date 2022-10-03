package org.apache.tika.sax;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.TreeMap;
import java.util.Map;
import java.util.ArrayList;

public class StandardsText
{
    private static final String REGEX_HEADER = "(\\d+\\.(\\d+\\.?)*)\\p{Blank}+([A-Z]+(\\s[A-Z]+)*){5,}";
    private static final String REGEX_APPLICABLE_DOCUMENTS = "(?i:.*APPLICABLE\\sDOCUMENTS|REFERENCE|STANDARD|REQUIREMENT|GUIDELINE|COMPLIANCE.*)";
    private static final String REGEX_IDENTIFIER = "(?<identifier>([0-9]{3,}|([A-Z]+(-|_|\\.)?[0-9]{2,}))((-|_|\\.)?[A-Z0-9]+)*)";
    private static final String REGEX_ORGANIZATION;
    private static final String REGEX_STANDARD_TYPE = "(\\s(?i:Publication|Standard))";
    private static final String REGEX_FALLBACK = "\\(?(?<mainOrganization>[A-Z]\\w+)\\)?((\\s?(?<separator>\\/)\\s?)(\\w+\\s)*\\(?(?<secondOrganization>[A-Z]\\w+)\\)?)?(\\s(?i:Publication|Standard))?(-|\\s)?(?<identifier>([0-9]{3,}|([A-Z]+(-|_|\\.)?[0-9]{2,}))((-|_|\\.)?[A-Z0-9]+)*)";
    private static final String REGEX_STANDARD;
    
    public static ArrayList<StandardReference> extractStandardReferences(final String text, final double threshold) {
        final Map<Integer, String> headers = findHeaders(text);
        return findStandards(text, headers, threshold);
    }
    
    private static Map<Integer, String> findHeaders(final String text) {
        final Map<Integer, String> headers = new TreeMap<Integer, String>();
        final Pattern pattern = Pattern.compile("(\\d+\\.(\\d+\\.?)*)\\p{Blank}+([A-Z]+(\\s[A-Z]+)*){5,}");
        final Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            headers.put(matcher.start(), matcher.group());
        }
        return headers;
    }
    
    private static ArrayList<StandardReference> findStandards(final String text, final Map<Integer, String> headers, final double threshold) {
        final ArrayList<StandardReference> standards = new ArrayList<StandardReference>();
        double score = 0.0;
        final Pattern pattern = Pattern.compile("\\(?(?<mainOrganization>[A-Z]\\w+)\\)?((\\s?(?<separator>\\/)\\s?)(\\w+\\s)*\\(?(?<secondOrganization>[A-Z]\\w+)\\)?)?(\\s(?i:Publication|Standard))?(-|\\s)?(?<identifier>([0-9]{3,}|([A-Z]+(-|_|\\.)?[0-9]{2,}))((-|_|\\.)?[A-Z0-9]+)*)");
        final Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            final StandardReference.StandardReferenceBuilder builder = new StandardReference.StandardReferenceBuilder(matcher.group("mainOrganization"), matcher.group("identifier")).setSecondOrganization(matcher.group("separator"), matcher.group("secondOrganization"));
            score = 0.25;
            if (matcher.group().matches(StandardsText.REGEX_STANDARD)) {
                score += 0.25;
            }
            if (matcher.group().matches(".*(\\s(?i:Publication|Standard)).*")) {
                score += 0.25;
            }
            int startHeader = 0;
            int endHeader = 0;
            boolean headerFound = false;
            for (Iterator<Map.Entry<Integer, String>> iterator = headers.entrySet().iterator(); iterator.hasNext() && !headerFound; headerFound = true) {
                startHeader = endHeader;
                endHeader = (int)iterator.next().getKey();
                if (endHeader > matcher.start()) {}
            }
            final String header = headers.get(startHeader);
            if (header != null && headers.get(startHeader).matches("(?i:.*APPLICABLE\\sDOCUMENTS|REFERENCE|STANDARD|REQUIREMENT|GUIDELINE|COMPLIANCE.*)")) {
                score += 0.25;
            }
            builder.setScore(score);
            if (score >= threshold) {
                standards.add(builder.build());
            }
        }
        return standards;
    }
    
    static {
        REGEX_ORGANIZATION = StandardOrganizations.getOrganzationsRegex();
        REGEX_STANDARD = ".*" + StandardsText.REGEX_ORGANIZATION + ".+" + StandardsText.REGEX_ORGANIZATION + "?.*";
    }
}
