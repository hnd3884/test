package org.glassfish.jersey.jaxb.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

final class NounInflector
{
    private static transient NounInflector instance;
    private final List<Replacer> plurals;
    private final List<Replacer> singulars;
    private final List<String> uncountables;
    
    private NounInflector() {
        this.plurals = new LinkedList<Replacer>();
        this.singulars = new ArrayList<Replacer>();
        this.uncountables = new LinkedList<String>();
        this.addPlural("$", "s", false);
        this.addPlural("(.*)$", "\\1s");
        this.addPlural("(.*)(ax|test)is$", "\\1\\2es");
        this.addPlural("(.*)(octop|vir)us$", "\\1\\2i");
        this.addPlural("(.*)(alias|status)$", "\\1\\2es");
        this.addPlural("(.*)(bu)s$", "\\1\\2ses");
        this.addPlural("(.*)(buffal|tomat)o$", "\\1\\2oes");
        this.addPlural("(.*)([ti])um$", "\\1\\2a");
        this.addPlural("(.*)sis$", "\\1ses");
        this.addPlural("(.*)(?:([^f])fe|([lr])f)$", "\\1\\3ves");
        this.addPlural("(.*)(hive)$", "\\1\\2s");
        this.addPlural("(.*)(tive)$", "\\1\\2s");
        this.addPlural("(.*)([^aeiouy]|qu)y$", "\\1\\2ies");
        this.addPlural("(.*)(series)$", "\\1\\2");
        this.addPlural("(.*)(movie)$", "\\1\\2s");
        this.addPlural("(.*)(x|ch|ss|sh)$", "\\1\\2es");
        this.addPlural("(.*)(matr|vert|ind)ix|ex$", "\\1\\2ices");
        this.addPlural("(.*)(o)$", "\\1\\2es");
        this.addPlural("(.*)(shoe)$", "\\1\\2s");
        this.addPlural("(.*)([m|l])ouse$", "\\1\\2ice");
        this.addPlural("^(ox)$", "\\1en");
        this.addPlural("(.*)(vert|ind)ex$", "\\1\\2ices");
        this.addPlural("(.*)(matr)ix$", "\\1\\2ices");
        this.addPlural("(.*)(quiz)$", "\\1\\2zes");
        this.addSingular("(.*)s$", "\\1");
        this.addSingular("(.*)(n)ews$", "\\1\\2ews");
        this.addSingular("(.*)([ti])a$", "\\1\\2um");
        this.addSingular("(.*)((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$", "\\1\\2sis");
        this.addSingular("(.*)(^analy)ses$", "\\1\\2sis");
        this.addSingular("(.*)([^f])ves$", "\\1\\2fe");
        this.addSingular("(.*)(hive)s$", "\\1\\2");
        this.addSingular("(.*)(tive)s$", "\\1\\2");
        this.addSingular("(.*)([lr])ves$", "\\1\\2f");
        this.addSingular("(.*)([^aeiouy]|qu)ies$", "\\1\\2y");
        this.addSingular("(.*)(s)eries$", "\\1\\2eries");
        this.addSingular("(.*)(m)ovies$", "\\1\\2ovie");
        this.addSingular("(.*)(x|ch|ss|sh)es$", "\\1\\2");
        this.addSingular("(.*)([m|l])ice$", "\\1\\2ouse");
        this.addSingular("(.*)(bus)es$", "\\1\\2");
        this.addSingular("(.*)(o)es$", "\\1\\2");
        this.addSingular("(.*)(shoe)s$", "\\1\\2");
        this.addSingular("(.*)(cris|ax|test)es$", "\\1\\2is");
        this.addSingular("(.*)(octop|vir)i$", "\\1\\2us");
        this.addSingular("(.*)(alias|status)es$", "\\1\\2");
        this.addSingular("^(ox)en", "\\1");
        this.addSingular("(.*)(vert|ind)ices$", "\\1\\2ex");
        this.addSingular("(.*)(matr)ices$", "\\1\\2ix");
        this.addSingular("(.*)(quiz)zes$", "\\1\\2");
        this.addIrregular("child", "children");
        this.addIrregular("man", "men");
        this.addIrregular("move", "moves");
        this.addIrregular("person", "people");
        this.addIrregular("sex", "sexes");
        this.addUncountable("equipment");
        this.addUncountable("fish");
        this.addUncountable("information");
        this.addUncountable("money");
        this.addUncountable("rice");
        this.addUncountable("series");
        this.addUncountable("sheep");
        this.addUncountable("species");
    }
    
    public static NounInflector getInstance() {
        if (NounInflector.instance == null) {
            NounInflector.instance = new NounInflector();
        }
        return NounInflector.instance;
    }
    
    public String camelize(final String word) {
        return this.camelize(word, false);
    }
    
    public String camelize(final String word, final boolean flag) {
        if (word.length() == 0) {
            return word;
        }
        final StringBuilder sb = new StringBuilder(word.length());
        if (flag) {
            sb.append(Character.toLowerCase(word.charAt(0)));
        }
        else {
            sb.append(Character.toUpperCase(word.charAt(0)));
        }
        boolean capitalize = false;
        for (int i = 1; i < word.length(); ++i) {
            final char ch = word.charAt(i);
            if (capitalize) {
                sb.append(Character.toUpperCase(ch));
                capitalize = false;
            }
            else if (ch == '_') {
                capitalize = true;
            }
            else if (ch == '/') {
                capitalize = true;
                sb.append('.');
            }
            else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
    
    public String classify(String tableName) {
        final int period = tableName.lastIndexOf(46);
        if (period >= 0) {
            tableName = tableName.substring(period + 1);
        }
        return this.camelize(this.singularize(tableName));
    }
    
    public String dasherize(final String word) {
        return word.replace('_', '-');
    }
    
    public String demodulize(final String className) {
        final int period = className.lastIndexOf(46);
        if (period >= 0) {
            return className.substring(period + 1);
        }
        return className;
    }
    
    public String foreignKey(final String className) {
        return this.foreignKey(className, true);
    }
    
    public String foreignKey(final String className, final boolean underscore) {
        return this.underscore(this.demodulize(className) + (underscore ? "_id" : "id"));
    }
    
    public String humanize(String words) {
        if (words.endsWith("_id")) {
            words = words.substring(0, words.length() - 3);
        }
        final StringBuilder sb = new StringBuilder(words.length());
        sb.append(Character.toUpperCase(words.charAt(0)));
        for (int i = 1; i < words.length(); ++i) {
            final char ch = words.charAt(i);
            if (ch == '_') {
                sb.append(' ');
            }
            else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
    
    public String ordinalize(final int number) {
        final int modulo = number % 100;
        if (modulo >= 11 && modulo <= 13) {
            return "" + number + "th";
        }
        switch (number % 10) {
            case 1: {
                return "" + number + "st";
            }
            case 2: {
                return "" + number + "nd";
            }
            case 3: {
                return "" + number + "rd";
            }
            default: {
                return "" + number + "th";
            }
        }
    }
    
    public String pluralize(final String word) {
        for (final String uncountable : this.uncountables) {
            if (uncountable.equals(word)) {
                return word;
            }
        }
        for (final Replacer plural : this.plurals) {
            final String replacement = plural.replacement(word);
            if (replacement != null) {
                return replacement;
            }
        }
        return word;
    }
    
    public String singularize(final String word) {
        for (final String uncountable : this.uncountables) {
            if (uncountable.equals(word)) {
                return word;
            }
        }
        for (final Replacer singular : this.singulars) {
            final String replacement = singular.replacement(word);
            if (replacement != null) {
                return replacement;
            }
        }
        return word;
    }
    
    public String tableize(final String className) {
        return this.pluralize(this.underscore(className));
    }
    
    public String titleize(final String words) {
        final StringBuilder sb = new StringBuilder(words.length());
        boolean capitalize = true;
        for (int i = 0; i < words.length(); ++i) {
            final char ch = words.charAt(i);
            if (Character.isWhitespace(ch)) {
                sb.append(' ');
                capitalize = true;
            }
            else if (ch == '-') {
                sb.append(' ');
                capitalize = true;
            }
            else if (capitalize) {
                sb.append(Character.toUpperCase(ch));
                capitalize = false;
            }
            else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
    
    public String decapitalize(final String word) {
        if (word == null || word.length() < 1) {
            return word;
        }
        final char first = word.charAt(0);
        if (Character.isLowerCase(first)) {
            return word;
        }
        final StringBuilder sb = new StringBuilder(word.length());
        sb.append(Character.toLowerCase(first));
        sb.append(word.substring(1));
        return sb.toString();
    }
    
    public String underscore(final String word) {
        final StringBuilder sb = new StringBuilder(word.length() + 5);
        boolean uncapitalize = false;
        for (int i = 0; i < word.length(); ++i) {
            final char ch = word.charAt(i);
            if (uncapitalize) {
                sb.append(Character.toLowerCase(ch));
                uncapitalize = false;
            }
            else if (ch == '.') {
                sb.append('/');
                uncapitalize = true;
            }
            else if (Character.isUpperCase(ch)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(ch));
            }
            else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
    
    public void addIrregular(final String singular, final String plural) {
        this.addPlural("(.*)(" + singular.substring(0, 1) + ")" + singular.substring(1) + "$", "\\1\\2" + plural.substring(1));
        this.addSingular("(.*)(" + plural.substring(0, 1) + ")" + plural.substring(1) + "$", "\\1\\2" + singular.substring(1));
    }
    
    public void addPlural(final String match, final String rule) {
        this.addPlural(match, rule, true);
    }
    
    public void addPlural(final String match, final String rule, final boolean insensitive) {
        this.plurals.add(0, new Replacer(match, rule, insensitive));
    }
    
    public void addSingular(final String match, final String rule) {
        this.addSingular(match, rule, true);
    }
    
    public void addSingular(final String match, final String rule, final boolean insensitive) {
        this.singulars.add(0, new Replacer(match, rule, insensitive));
    }
    
    public void addUncountable(final String word) {
        this.uncountables.add(0, word.toLowerCase());
    }
    
    static {
        NounInflector.instance = null;
    }
    
    private static class Replacer
    {
        private Pattern pattern;
        private String rule;
        
        public Replacer(final String match, final String rule, final boolean insensitive) {
            this.pattern = null;
            this.rule = null;
            this.pattern = Pattern.compile(match, insensitive ? 2 : 0);
            this.rule = rule;
        }
        
        public String replacement(final String input) {
            final Matcher matcher = this.pattern.matcher(input);
            if (matcher.matches()) {
                final StringBuilder sb = new StringBuilder();
                boolean group = false;
                for (int i = 0; i < this.rule.length(); ++i) {
                    final char ch = this.rule.charAt(i);
                    if (group) {
                        sb.append(matcher.group(Character.digit(ch, 10)));
                        group = false;
                    }
                    else if (ch == '\\') {
                        group = true;
                    }
                    else {
                        sb.append(ch);
                    }
                }
                return sb.toString();
            }
            return null;
        }
    }
}
