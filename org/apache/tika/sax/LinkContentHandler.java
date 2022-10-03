package org.apache.tika.sax;

import java.util.Iterator;
import org.xml.sax.Attributes;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import org.xml.sax.helpers.DefaultHandler;

public class LinkContentHandler extends DefaultHandler
{
    private final LinkedList<LinkBuilder> builderStack;
    private final List<Link> links;
    private final boolean collapseWhitespaceInAnchor;
    
    public LinkContentHandler() {
        this(false);
    }
    
    public LinkContentHandler(final boolean collapseWhitespaceInAnchor) {
        this.builderStack = new LinkedList<LinkBuilder>();
        this.links = new ArrayList<Link>();
        this.collapseWhitespaceInAnchor = collapseWhitespaceInAnchor;
    }
    
    public List<Link> getLinks() {
        return this.links;
    }
    
    @Override
    public void startElement(final String uri, final String local, final String name, final Attributes attributes) {
        if ("http://www.w3.org/1999/xhtml".equals(uri)) {
            if ("a".equals(local)) {
                final LinkBuilder builder = new LinkBuilder("a");
                builder.setURI(attributes.getValue("", "href"));
                builder.setTitle(attributes.getValue("", "title"));
                builder.setRel(attributes.getValue("", "rel"));
                this.builderStack.addFirst(builder);
            }
            else if ("link".equals(local)) {
                final LinkBuilder builder = new LinkBuilder("link");
                builder.setURI(attributes.getValue("", "href"));
                builder.setRel(attributes.getValue("", "rel"));
                this.builderStack.addFirst(builder);
            }
            else if ("script".equals(local)) {
                if (attributes.getValue("", "src") != null) {
                    final LinkBuilder builder = new LinkBuilder("script");
                    builder.setURI(attributes.getValue("", "src"));
                    this.builderStack.addFirst(builder);
                }
            }
            else if ("iframe".equals(local)) {
                final LinkBuilder builder = new LinkBuilder("iframe");
                builder.setURI(attributes.getValue("", "src"));
                this.builderStack.addFirst(builder);
            }
            else if ("img".equals(local)) {
                final LinkBuilder builder = new LinkBuilder("img");
                builder.setURI(attributes.getValue("", "src"));
                builder.setTitle(attributes.getValue("", "title"));
                builder.setRel(attributes.getValue("", "rel"));
                this.builderStack.addFirst(builder);
                final String alt = attributes.getValue("", "alt");
                if (alt != null) {
                    final char[] ch = alt.toCharArray();
                    this.characters(ch, 0, ch.length);
                }
            }
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) {
        for (final LinkBuilder builder : this.builderStack) {
            builder.characters(ch, start, length);
        }
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) {
        this.characters(ch, start, length);
    }
    
    @Override
    public void endElement(final String uri, final String local, final String name) {
        if (!this.builderStack.isEmpty() && "http://www.w3.org/1999/xhtml".equals(uri) && ("a".equals(local) || "img".equals(local) || "link".equals(local) || "script".equals(local) || "iframe".equals(local)) && this.builderStack.getFirst().getType().equals(local)) {
            final LinkBuilder builder = this.builderStack.removeFirst();
            this.links.add(builder.getLink(this.collapseWhitespaceInAnchor));
        }
    }
}
