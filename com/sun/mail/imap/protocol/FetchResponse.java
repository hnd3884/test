package com.sun.mail.imap.protocol;

import java.util.HashMap;
import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.iap.ParsingException;
import java.util.ArrayList;
import java.util.List;
import com.sun.mail.iap.Response;
import com.sun.mail.iap.ProtocolException;
import java.io.IOException;
import com.sun.mail.iap.Protocol;
import java.util.Map;

public class FetchResponse extends IMAPResponse
{
    private Item[] items;
    private Map<String, Object> extensionItems;
    private final FetchItem[] fitems;
    private static final char[] HEADER;
    private static final char[] TEXT;
    
    public FetchResponse(final Protocol p) throws IOException, ProtocolException {
        super(p);
        this.fitems = null;
        this.parse();
    }
    
    public FetchResponse(final IMAPResponse r) throws IOException, ProtocolException {
        this(r, null);
    }
    
    public FetchResponse(final IMAPResponse r, final FetchItem[] fitems) throws IOException, ProtocolException {
        super(r);
        this.fitems = fitems;
        this.parse();
    }
    
    public int getItemCount() {
        return this.items.length;
    }
    
    public Item getItem(final int index) {
        return this.items[index];
    }
    
    public <T extends Item> T getItem(final Class<T> c) {
        for (int i = 0; i < this.items.length; ++i) {
            if (c.isInstance(this.items[i])) {
                return c.cast(this.items[i]);
            }
        }
        return null;
    }
    
    public static <T extends Item> T getItem(final Response[] r, final int msgno, final Class<T> c) {
        if (r == null) {
            return null;
        }
        for (int i = 0; i < r.length; ++i) {
            if (r[i] != null && r[i] instanceof FetchResponse) {
                if (((FetchResponse)r[i]).getNumber() == msgno) {
                    final FetchResponse f = (FetchResponse)r[i];
                    for (int j = 0; j < f.items.length; ++j) {
                        if (c.isInstance(f.items[j])) {
                            return c.cast(f.items[j]);
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public static <T extends Item> List<T> getItems(final Response[] r, final int msgno, final Class<T> c) {
        final List<T> items = new ArrayList<T>();
        if (r == null) {
            return items;
        }
        for (int i = 0; i < r.length; ++i) {
            if (r[i] != null && r[i] instanceof FetchResponse) {
                if (((FetchResponse)r[i]).getNumber() == msgno) {
                    final FetchResponse f = (FetchResponse)r[i];
                    for (int j = 0; j < f.items.length; ++j) {
                        if (c.isInstance(f.items[j])) {
                            items.add(c.cast(f.items[j]));
                        }
                    }
                }
            }
        }
        return items;
    }
    
    public Map<String, Object> getExtensionItems() {
        return this.extensionItems;
    }
    
    private void parse() throws ParsingException {
        if (!this.isNextNonSpace('(')) {
            throw new ParsingException("error in FETCH parsing, missing '(' at index " + this.index);
        }
        final List<Item> v = new ArrayList<Item>();
        Item i = null;
        this.skipSpaces();
        while (this.index < this.size) {
            i = this.parseItem();
            if (i != null) {
                v.add(i);
            }
            else if (!this.parseExtensionItem()) {
                throw new ParsingException("error in FETCH parsing, unrecognized item at index " + this.index + ", starts with \"" + this.next20() + "\"");
            }
            if (this.isNextNonSpace(')')) {
                this.items = v.toArray(new Item[v.size()]);
                return;
            }
        }
        throw new ParsingException("error in FETCH parsing, ran off end of buffer, size " + this.size);
    }
    
    private String next20() {
        if (this.index + 20 > this.size) {
            return ASCIIUtility.toString(this.buffer, this.index, this.size);
        }
        return ASCIIUtility.toString(this.buffer, this.index, this.index + 20) + "...";
    }
    
    private Item parseItem() throws ParsingException {
        switch (this.buffer[this.index]) {
            case 69:
            case 101: {
                if (this.match(ENVELOPE.name)) {
                    return new ENVELOPE(this);
                }
                break;
            }
            case 70:
            case 102: {
                if (this.match(FLAGS.name)) {
                    return new FLAGS(this);
                }
                break;
            }
            case 73:
            case 105: {
                if (this.match(INTERNALDATE.name)) {
                    return new INTERNALDATE(this);
                }
                break;
            }
            case 66:
            case 98: {
                if (this.match(BODYSTRUCTURE.name)) {
                    return new BODYSTRUCTURE(this);
                }
                if (!this.match(BODY.name)) {
                    break;
                }
                if (this.buffer[this.index] == 91) {
                    return new BODY(this);
                }
                return new BODYSTRUCTURE(this);
            }
            case 82:
            case 114: {
                if (this.match(RFC822SIZE.name)) {
                    return new RFC822SIZE(this);
                }
                if (this.match(RFC822DATA.name)) {
                    boolean isHeader = false;
                    if (this.match(FetchResponse.HEADER)) {
                        isHeader = true;
                    }
                    else if (this.match(FetchResponse.TEXT)) {
                        isHeader = false;
                    }
                    return new RFC822DATA(this, isHeader);
                }
                break;
            }
            case 85:
            case 117: {
                if (this.match(UID.name)) {
                    return new UID(this);
                }
                break;
            }
            case 77:
            case 109: {
                if (this.match(MODSEQ.name)) {
                    return new MODSEQ(this);
                }
                break;
            }
        }
        return null;
    }
    
    private boolean parseExtensionItem() throws ParsingException {
        if (this.fitems == null) {
            return false;
        }
        for (int i = 0; i < this.fitems.length; ++i) {
            if (this.match(this.fitems[i].getName())) {
                if (this.extensionItems == null) {
                    this.extensionItems = new HashMap<String, Object>();
                }
                this.extensionItems.put(this.fitems[i].getName(), this.fitems[i].parseItem(this));
                return true;
            }
        }
        return false;
    }
    
    private boolean match(final char[] itemName) {
        final int len = itemName.length;
        int i = 0;
        int j = this.index;
        while (i < len) {
            if (Character.toUpperCase((char)this.buffer[j++]) != itemName[i++]) {
                return false;
            }
        }
        this.index += len;
        return true;
    }
    
    private boolean match(final String itemName) {
        final int len = itemName.length();
        int i = 0;
        int j = this.index;
        while (i < len) {
            if (Character.toUpperCase((char)this.buffer[j++]) != itemName.charAt(i++)) {
                return false;
            }
        }
        this.index += len;
        return true;
    }
    
    static {
        HEADER = new char[] { '.', 'H', 'E', 'A', 'D', 'E', 'R' };
        TEXT = new char[] { '.', 'T', 'E', 'X', 'T' };
    }
}
