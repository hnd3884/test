package org.htmlparser.parserapplications;

import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.NodeClassFilter;
import javax.swing.Icon;
import java.awt.Component;
import javax.swing.JOptionPane;

public class LinkExtractor
{
    public static void main(final String[] args) {
        String url;
        if (0 >= args.length) {
            url = (String)JOptionPane.showInputDialog(null, "Enter the URL to extract links from:", "Web Site", -1, null, null, "http://htmlparser.sourceforge.net/wiki/");
            if (null == url) {
                System.exit(1);
            }
        }
        else {
            url = args[0];
        }
        NodeFilter filter = new NodeClassFilter(LinkTag.class);
        if (1 < args.length && args[1].equalsIgnoreCase("-maillinks")) {
            filter = new AndFilter(filter, new NodeFilter() {
                public boolean accept(final Node node) {
                    return ((LinkTag)node).isMailLink();
                }
            });
        }
        try {
            final Parser parser = new Parser(url);
            final NodeList list = parser.extractAllNodesThatMatch(filter);
            for (int i = 0; i < list.size(); ++i) {
                System.out.println(list.elementAt(i).toHtml());
            }
        }
        catch (final ParserException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
