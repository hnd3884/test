package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;
import java.util.List;
import java.util.ArrayList;

public class ListInfo
{
    public String name;
    public char separator;
    public boolean hasInferiors;
    public boolean canOpen;
    public int changeState;
    public String[] attrs;
    public static final int CHANGED = 1;
    public static final int UNCHANGED = 2;
    public static final int INDETERMINATE = 3;
    
    public ListInfo(final IMAPResponse r) throws ParsingException {
        this.name = null;
        this.separator = '/';
        this.hasInferiors = true;
        this.canOpen = true;
        this.changeState = 3;
        final String[] s = r.readSimpleList();
        final List<String> v = new ArrayList<String>();
        if (s != null) {
            for (int i = 0; i < s.length; ++i) {
                if (s[i].equalsIgnoreCase("\\Marked")) {
                    this.changeState = 1;
                }
                else if (s[i].equalsIgnoreCase("\\Unmarked")) {
                    this.changeState = 2;
                }
                else if (s[i].equalsIgnoreCase("\\Noselect")) {
                    this.canOpen = false;
                }
                else if (s[i].equalsIgnoreCase("\\Noinferiors")) {
                    this.hasInferiors = false;
                }
                v.add(s[i]);
            }
        }
        this.attrs = v.toArray(new String[v.size()]);
        r.skipSpaces();
        if (r.readByte() == 34) {
            if ((this.separator = (char)r.readByte()) == '\\') {
                this.separator = (char)r.readByte();
            }
            r.skip(1);
        }
        else {
            r.skip(2);
        }
        r.skipSpaces();
        this.name = r.readAtomString();
        if (!r.supportsUtf8()) {
            this.name = BASE64MailboxDecoder.decode(this.name);
        }
    }
}
