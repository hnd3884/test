package org.htmlparser.parserapplications;

import org.htmlparser.util.ParserException;
import org.htmlparser.beans.StringBean;

public class StringExtractor
{
    private String resource;
    
    public StringExtractor(final String resource) {
        this.resource = resource;
    }
    
    public String extractStrings(final boolean links) throws ParserException {
        final StringBean sb = new StringBean();
        sb.setLinks(links);
        sb.setURL(this.resource);
        return sb.getStrings();
    }
    
    public static void main(final String[] args) {
        boolean links = false;
        String url = null;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equalsIgnoreCase("-links")) {
                links = true;
            }
            else {
                url = args[i];
            }
        }
        if (null != url) {
            final StringExtractor se = new StringExtractor(url);
            try {
                System.out.println(se.extractStrings(links));
            }
            catch (final ParserException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Usage: java -classpath htmlparser.jar org.htmlparser.parserapplications.StringExtractor [-links] url");
        }
    }
}
