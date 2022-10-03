package org.openjsse.sun.security.util;

import java.util.Collection;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.security.AccessController;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.File;
import java.security.PrivilegedAction;
import org.openjsse.sun.security.ssl.SSLLogger;
import java.util.zip.ZipInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

class DomainName
{
    private static final Map<String, Rules> cache;
    
    private DomainName() {
    }
    
    public static RegisteredDomain registeredDomain(final String domain) {
        final Match match = getMatch(domain);
        return (match != null) ? match.registeredDomain() : null;
    }
    
    private static Match getMatch(final String domain) {
        if (domain == null) {
            throw new NullPointerException();
        }
        final Rules rules = Rules.getRules(domain);
        return (rules == null) ? null : rules.match(domain);
    }
    
    static {
        cache = new ConcurrentHashMap<String, Rules>();
    }
    
    private static class Rules
    {
        private final LinkedList<RuleSet> ruleSets;
        private final boolean hasExceptions;
        
        private Rules(final InputStream is) throws IOException {
            this.ruleSets = new LinkedList<RuleSet>();
            final InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            final BufferedReader reader = new BufferedReader(isr);
            boolean hasExceptions = false;
            String line;
            for (int type = reader.read(); type != -1 && (line = reader.readLine()) != null; type = reader.read()) {
                final int numLabels = numLabels(line);
                if (numLabels != 0) {
                    final RuleSet ruleset = this.getRuleSet(numLabels - 1);
                    ruleset.addRule(type, line);
                    hasExceptions |= ruleset.hasExceptions;
                }
            }
            this.hasExceptions = hasExceptions;
        }
        
        static Rules getRules(final String domain) {
            final String tld = getTopLevelDomain(domain);
            if (tld.isEmpty()) {
                return null;
            }
            return DomainName.cache.computeIfAbsent(tld, k -> createRules(tld));
        }
        
        private static String getTopLevelDomain(final String domain) {
            final int n = domain.lastIndexOf(46);
            if (n == -1) {
                return domain;
            }
            return domain.substring(n + 1);
        }
        
        private static Rules createRules(final String tld) {
            try (final InputStream pubSuffixStream = getPubSuffixStream()) {
                if (pubSuffixStream == null) {
                    return null;
                }
                return getRules(tld, new ZipInputStream(pubSuffixStream));
            }
            catch (final IOException e) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.fine("cannot parse public suffix data for " + tld + ": " + e.getMessage(), new Object[0]);
                }
                return null;
            }
        }
        
        private static InputStream getPubSuffixStream() {
            final InputStream is = AccessController.doPrivileged((PrivilegedAction<InputStream>)new PrivilegedAction<InputStream>() {
                @Override
                public InputStream run() {
                    final File f = new File(System.getProperty("java.home"), "lib/security/public_suffix_list.dat");
                    try {
                        return new FileInputStream(f);
                    }
                    catch (final FileNotFoundException e) {
                        return null;
                    }
                }
            });
            if (is == null && SSLLogger.isOn && SSLLogger.isOn("ssl") && SSLLogger.isOn("trustmanager")) {
                SSLLogger.fine("lib/security/public_suffix_list.dat not found", new Object[0]);
            }
            return is;
        }
        
        private static Rules getRules(final String tld, final ZipInputStream zis) throws IOException {
            boolean found = false;
            ZipEntry ze = zis.getNextEntry();
            while (ze != null && !found) {
                if (ze.getName().equals(tld)) {
                    found = true;
                }
                else {
                    ze = zis.getNextEntry();
                }
            }
            if (!found) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.fine("Domain " + tld + " not found", new Object[0]);
                }
                return null;
            }
            return new Rules(zis);
        }
        
        private RuleSet getRuleSet(final int index) {
            if (index < this.ruleSets.size()) {
                return this.ruleSets.get(index);
            }
            RuleSet r = null;
            for (int i = this.ruleSets.size(); i <= index; ++i) {
                r = new RuleSet(i + 1);
                this.ruleSets.add(r);
            }
            return r;
        }
        
        Match match(final String domain) {
            Match possibleMatch = null;
            final Iterator<RuleSet> it = this.ruleSets.descendingIterator();
            while (it.hasNext()) {
                final RuleSet ruleSet = it.next();
                final Match match = ruleSet.match(domain);
                if (match != null) {
                    if (match.type() == Rule.Type.EXCEPTION || !this.hasExceptions) {
                        return match;
                    }
                    if (possibleMatch != null) {
                        continue;
                    }
                    possibleMatch = match;
                }
            }
            return possibleMatch;
        }
        
        private static class RuleSet
        {
            private final int numLabels;
            private final Set<Rule> rules;
            boolean hasExceptions;
            private static final RegisteredDomain.Type[] AUTHS;
            
            RuleSet(final int n) {
                this.rules = new HashSet<Rule>();
                this.hasExceptions = false;
                this.numLabels = n;
            }
            
            void addRule(final int auth, final String rule) {
                if (rule.startsWith("!")) {
                    this.rules.add(new Rule(rule.substring(1), Rule.Type.EXCEPTION, RuleSet.AUTHS[auth]));
                    this.hasExceptions = true;
                }
                else if (rule.startsWith("*.") && rule.lastIndexOf(42) == 0) {
                    this.rules.add(new Rule(rule.substring(2), Rule.Type.WILDCARD, RuleSet.AUTHS[auth]));
                }
                else if (rule.indexOf(42) == -1) {
                    this.rules.add(new Rule(rule, Rule.Type.NORMAL, RuleSet.AUTHS[auth]));
                }
                else {
                    this.rules.add(new OtherRule(rule, RuleSet.AUTHS[auth], split(rule)));
                }
            }
            
            Match match(final String domain) {
                Match match = null;
                for (final Rule rule : this.rules) {
                    switch (rule.type) {
                        case NORMAL: {
                            if (match == null) {
                                match = this.matchNormal(domain, rule);
                                continue;
                            }
                            continue;
                        }
                        case WILDCARD: {
                            if (match == null) {
                                match = this.matchWildcard(domain, rule);
                                continue;
                            }
                            continue;
                        }
                        case OTHER: {
                            if (match == null) {
                                match = this.matchOther(domain, rule);
                                continue;
                            }
                            continue;
                        }
                        case EXCEPTION: {
                            final Match excMatch = this.matchException(domain, rule);
                            if (excMatch != null) {
                                return excMatch;
                            }
                            continue;
                        }
                    }
                }
                return match;
            }
            
            private static LinkedList<String> split(final String rule) {
                final String[] labels = rule.split("\\.");
                return new LinkedList<String>(Arrays.asList(labels));
            }
            
            private static int numLabels(final String rule) {
                if (rule.equals("")) {
                    return 0;
                }
                final int len = rule.length();
                int count = 0;
                int pos;
                for (int index = 0; index < len; index = pos + 1, ++count) {
                    if ((pos = rule.indexOf(46, index)) == -1) {
                        return count + 1;
                    }
                }
                return count;
            }
            
            private Match matchNormal(final String domain, final Rule rule) {
                final int index = labels(domain, this.numLabels);
                if (index == -1) {
                    return null;
                }
                final String substring = domain.substring(index);
                if (rule.domain.equals(substring)) {
                    return new CommonMatch(domain, rule, index);
                }
                return null;
            }
            
            private Match matchWildcard(final String domain, final Rule rule) {
                final int index = labels(domain, this.numLabels - 1);
                if (index > 0) {
                    final String substring = domain.substring(index);
                    if (rule.domain.equals(substring)) {
                        return new CommonMatch(domain, rule, labels(domain, this.numLabels));
                    }
                }
                return null;
            }
            
            private Match matchException(final String domain, final Rule rule) {
                final int index = labels(domain, this.numLabels);
                if (index == -1) {
                    return null;
                }
                final String substring = domain.substring(index);
                if (rule.domain.equals(substring)) {
                    return new CommonMatch(domain, rule, labels(domain, this.numLabels - 1));
                }
                return null;
            }
            
            private Match matchOther(final String domain, final Rule rule) {
                final OtherRule otherRule = (OtherRule)rule;
                final LinkedList<String> target = split(domain);
                final int diff = target.size() - this.numLabels;
                if (diff < 0) {
                    return null;
                }
                boolean found = true;
                for (int i = 0; i < this.numLabels; ++i) {
                    final String ruleLabel = otherRule.labels.get(i);
                    final String targetLabel = target.get(i + diff);
                    if (ruleLabel.charAt(0) != '*' && !ruleLabel.equalsIgnoreCase(targetLabel)) {
                        found = false;
                        break;
                    }
                }
                if (found) {
                    return new OtherMatch(rule, this.numLabels, target);
                }
                return null;
            }
            
            private static int labels(final String s, final int n) {
                if (n < 1) {
                    return -1;
                }
                int index = s.length();
                int i = 0;
                while (i < n) {
                    final int next = s.lastIndexOf(46, index);
                    if (next == -1) {
                        if (i == n - 1) {
                            return 0;
                        }
                        return -1;
                    }
                    else {
                        index = next - 1;
                        ++i;
                    }
                }
                return index + 2;
            }
            
            static {
                AUTHS = RegisteredDomain.Type.values();
            }
        }
    }
    
    private static class Rule
    {
        String domain;
        Type type;
        RegisteredDomain.Type auth;
        
        Rule(final String domain, final Type type, final RegisteredDomain.Type auth) {
            this.domain = domain;
            this.type = type;
            this.auth = auth;
        }
        
        enum Type
        {
            EXCEPTION, 
            NORMAL, 
            OTHER, 
            WILDCARD;
        }
    }
    
    private static class OtherRule extends Rule
    {
        List<String> labels;
        
        OtherRule(final String domain, final RegisteredDomain.Type auth, final List<String> labels) {
            super(domain, Type.OTHER, auth);
            this.labels = labels;
        }
    }
    
    private static class RegisteredDomainImpl implements RegisteredDomain
    {
        private final String name;
        private final Type type;
        private final String publicSuffix;
        
        RegisteredDomainImpl(final String name, final Type type, final String publicSuffix) {
            this.name = name;
            this.type = type;
            this.publicSuffix = publicSuffix;
        }
        
        @Override
        public String name() {
            return this.name;
        }
        
        @Override
        public Type type() {
            return this.type;
        }
        
        @Override
        public String publicSuffix() {
            return this.publicSuffix;
        }
    }
    
    private static class CommonMatch implements Match
    {
        private String domain;
        private int publicSuffix;
        private int registeredDomain;
        private final Rule rule;
        
        CommonMatch(final String domain, final Rule rule, final int publicSuffix) {
            this.domain = domain;
            this.publicSuffix = publicSuffix;
            this.rule = rule;
            this.registeredDomain = domain.lastIndexOf(46, publicSuffix - 2);
            if (this.registeredDomain == -1) {
                this.registeredDomain = 0;
            }
            else {
                ++this.registeredDomain;
            }
        }
        
        @Override
        public RegisteredDomain registeredDomain() {
            if (this.publicSuffix == 0) {
                return null;
            }
            return new RegisteredDomainImpl(this.domain.substring(this.registeredDomain), this.rule.auth, this.domain.substring(this.publicSuffix));
        }
        
        @Override
        public Rule.Type type() {
            return this.rule.type;
        }
    }
    
    private static class OtherMatch implements Match
    {
        private final Rule rule;
        private final int numLabels;
        private final LinkedList<String> target;
        
        OtherMatch(final Rule rule, final int numLabels, final LinkedList<String> target) {
            this.rule = rule;
            this.numLabels = numLabels;
            this.target = target;
        }
        
        @Override
        public RegisteredDomain registeredDomain() {
            final int nlabels = this.numLabels + 1;
            if (nlabels > this.target.size()) {
                return null;
            }
            return new RegisteredDomainImpl(this.getSuffixes(nlabels), this.rule.auth, this.getSuffixes(this.numLabels));
        }
        
        @Override
        public Rule.Type type() {
            return this.rule.type;
        }
        
        private String getSuffixes(int n) {
            final Iterator<String> targetIter = this.target.descendingIterator();
            final StringBuilder sb = new StringBuilder();
            while (n > 0 && targetIter.hasNext()) {
                final String s = targetIter.next();
                sb.insert(0, s);
                if (n > 1) {
                    sb.insert(0, '.');
                }
                --n;
            }
            return sb.toString();
        }
    }
    
    private interface Match
    {
        RegisteredDomain registeredDomain();
        
        Rule.Type type();
    }
}
