package org.htmlparser.tags;

import java.util.Vector;
import org.htmlparser.util.ParserUtils;
import java.util.Locale;
import org.htmlparser.Attribute;
import org.htmlparser.nodes.TagNode;

public class ImageTag extends TagNode
{
    private static final String[] mIds;
    protected String imageURL;
    
    public ImageTag() {
        this.imageURL = null;
    }
    
    public String[] getIds() {
        return ImageTag.mIds;
    }
    
    public String extractImageLocn() {
        String ret = "";
        int state = 0;
        final Vector attributes = this.getAttributesEx();
        for (int size = attributes.size(), i = 0; i < size && state < 3; ++i) {
            final Attribute attribute = attributes.elementAt(i);
            String string = attribute.getName();
            final String data = attribute.getValue();
            switch (state) {
                case 0: {
                    if (null == string) {
                        break;
                    }
                    final String name = string.toUpperCase(Locale.ENGLISH);
                    if (name.equals("SRC")) {
                        state = 1;
                        if (null == data) {
                            break;
                        }
                        if ("".equals(data)) {
                            state = 2;
                            break;
                        }
                        ret = data;
                        i = size;
                        break;
                    }
                    else {
                        if (name.startsWith("SRC")) {
                            string = string.substring(3);
                            if (string.startsWith("\"") && string.endsWith("\"") && 1 < string.length()) {
                                string = string.substring(1, string.length() - 1);
                            }
                            if (string.startsWith("'") && string.endsWith("'") && 1 < string.length()) {
                                string = string.substring(1, string.length() - 1);
                            }
                            ret = string;
                            state = 0;
                            break;
                        }
                        break;
                    }
                    break;
                }
                case 1: {
                    if (null == string || !string.startsWith("=")) {
                        break;
                    }
                    state = 2;
                    if (1 < string.length()) {
                        ret = string.substring(1);
                        state = 0;
                        break;
                    }
                    if (null != data) {
                        ret = string.substring(1);
                        state = 0;
                        break;
                    }
                    break;
                }
                case 2: {
                    if (null != string) {
                        if (null == data) {
                            ret = string;
                        }
                        state = 0;
                        break;
                    }
                    break;
                }
                default: {
                    throw new IllegalStateException("we're not supposed to in state " + state);
                }
            }
        }
        ret = ParserUtils.removeChars(ret, '\n');
        ret = ParserUtils.removeChars(ret, '\r');
        return ret;
    }
    
    public String getImageURL() {
        if (null == this.imageURL && null != this.getPage()) {
            this.imageURL = this.getPage().getAbsoluteURL(this.extractImageLocn());
        }
        return this.imageURL;
    }
    
    public void setImageURL(final String url) {
        this.setAttribute("SRC", this.imageURL = url);
    }
    
    static {
        mIds = new String[] { "IMG" };
    }
}
