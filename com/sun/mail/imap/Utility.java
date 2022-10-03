package com.sun.mail.imap;

import com.sun.mail.imap.protocol.UIDSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import com.sun.mail.imap.protocol.MessageSet;
import javax.mail.Message;

public final class Utility
{
    private Utility() {
    }
    
    public static MessageSet[] toMessageSet(final Message[] msgs, final Condition cond) {
        final List<MessageSet> v = new ArrayList<MessageSet>(1);
        for (int i = 0; i < msgs.length; ++i) {
            IMAPMessage msg = (IMAPMessage)msgs[i];
            if (!msg.isExpunged()) {
                int current = msg.getSequenceNumber();
                if (cond == null || cond.test(msg)) {
                    final MessageSet set = new MessageSet();
                    set.start = current;
                    ++i;
                    while (i < msgs.length) {
                        msg = (IMAPMessage)msgs[i];
                        if (!msg.isExpunged()) {
                            final int next = msg.getSequenceNumber();
                            if (cond == null || cond.test(msg)) {
                                if (next != current + 1) {
                                    --i;
                                    break;
                                }
                                current = next;
                            }
                        }
                        ++i;
                    }
                    set.end = current;
                    v.add(set);
                }
            }
        }
        if (v.isEmpty()) {
            return null;
        }
        return v.toArray(new MessageSet[v.size()]);
    }
    
    public static MessageSet[] toMessageSetSorted(Message[] msgs, final Condition cond) {
        msgs = msgs.clone();
        Arrays.sort(msgs, new Comparator<Message>() {
            @Override
            public int compare(final Message msg1, final Message msg2) {
                return msg1.getMessageNumber() - msg2.getMessageNumber();
            }
        });
        return toMessageSet(msgs, cond);
    }
    
    public static UIDSet[] toUIDSet(final Message[] msgs) {
        final List<UIDSet> v = new ArrayList<UIDSet>(1);
        for (int i = 0; i < msgs.length; ++i) {
            IMAPMessage msg = (IMAPMessage)msgs[i];
            if (!msg.isExpunged()) {
                long current = msg.getUID();
                final UIDSet set = new UIDSet();
                set.start = current;
                ++i;
                while (i < msgs.length) {
                    msg = (IMAPMessage)msgs[i];
                    if (!msg.isExpunged()) {
                        final long next = msg.getUID();
                        if (next != current + 1L) {
                            --i;
                            break;
                        }
                        current = next;
                    }
                    ++i;
                }
                set.end = current;
                v.add(set);
            }
        }
        if (v.isEmpty()) {
            return null;
        }
        return v.toArray(new UIDSet[v.size()]);
    }
    
    public static UIDSet[] getResyncUIDSet(final ResyncData rd) {
        return rd.getUIDSet();
    }
    
    public interface Condition
    {
        boolean test(final IMAPMessage p0);
    }
}
