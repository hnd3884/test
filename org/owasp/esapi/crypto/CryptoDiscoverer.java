package org.owasp.esapi.crypto;

import java.util.Iterator;
import java.util.List;
import java.security.Provider;
import java.security.Security;
import java.util.regex.Pattern;
import java.util.Arrays;

public class CryptoDiscoverer
{
    private static String EOL;
    
    public static void main(final String... args) {
        String provider = ".*";
        String algorithm = ".*";
        if (args.length > 0) {
            if (args[0].equals("--help")) {
                usage();
                System.exit(0);
            }
            final List<String> argList = Arrays.asList(args);
            int argIdx = argList.indexOf("--provider");
            if (argIdx > -1 && argList.size() > argIdx + 1) {
                provider = argList.get(argIdx + 1);
            }
            argIdx = argList.indexOf("--algorithm");
            if (argIdx > -1 && argList.size() > argIdx + 1) {
                algorithm = argList.get(argIdx + 1);
            }
        }
        final Pattern providerPattern = Pattern.compile(provider);
        final Pattern algorithmPattern = Pattern.compile(algorithm);
        System.out.println("Searching for Providers Matching: " + provider);
        System.out.println("Searching for Algorithms Matching: " + algorithm);
        System.out.println();
        for (final Provider p : Security.getProviders()) {
            if (providerPattern.matcher(p.getName()).matches()) {
                System.out.println("Provider: " + p.getName());
                for (final Provider.Service service : p.getServices()) {
                    if (algorithmPattern.matcher(service.getAlgorithm()).matches()) {
                        System.out.println("\tAlgorithm: " + service.getAlgorithm());
                    }
                }
            }
        }
    }
    
    private static void usage() {
        System.out.println("CryptoDiscoverer - Discover or Query for available Crypto Providers and Algorithms");
        System.out.println(CryptoDiscoverer.EOL + "\t--help\t\t\t\t\tShows this message" + CryptoDiscoverer.EOL + "\t--provider <regex>\t\tSearch for particular Provider" + CryptoDiscoverer.EOL + "\t--algorithm <regex>\t\tSearch for a particular Algorithm" + CryptoDiscoverer.EOL + CryptoDiscoverer.EOL);
    }
    
    static {
        CryptoDiscoverer.EOL = System.getProperty("line.separator", "\n");
    }
}
