package org.bouncycastle.cms;

import org.bouncycastle.asn1.x500.X500Name;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.bouncycastle.util.Iterable;

public class RecipientInformationStore implements Iterable<RecipientInformation>
{
    private final List all;
    private final Map table;
    
    public RecipientInformationStore(final RecipientInformation recipientInformation) {
        this.table = new HashMap();
        (this.all = new ArrayList(1)).add(recipientInformation);
        this.table.put(recipientInformation.getRID(), this.all);
    }
    
    public RecipientInformationStore(final Collection<RecipientInformation> collection) {
        this.table = new HashMap();
        for (final RecipientInformation recipientInformation : collection) {
            final RecipientId rid = recipientInformation.getRID();
            ArrayList list = this.table.get(rid);
            if (list == null) {
                list = new ArrayList(1);
                this.table.put(rid, list);
            }
            list.add(recipientInformation);
        }
        this.all = new ArrayList(collection);
    }
    
    public RecipientInformation get(final RecipientId recipientId) {
        final Collection<Recipient> recipients = this.getRecipients(recipientId);
        return (recipients.size() == 0) ? null : ((RecipientInformation)recipients.iterator().next());
    }
    
    public int size() {
        return this.all.size();
    }
    
    public Collection<RecipientInformation> getRecipients() {
        return new ArrayList<RecipientInformation>(this.all);
    }
    
    public Collection<Recipient> getRecipients(final RecipientId recipientId) {
        if (recipientId instanceof KeyTransRecipientId) {
            final KeyTransRecipientId keyTransRecipientId = (KeyTransRecipientId)recipientId;
            final X500Name issuer = keyTransRecipientId.getIssuer();
            final byte[] subjectKeyIdentifier = keyTransRecipientId.getSubjectKeyIdentifier();
            if (issuer != null && subjectKeyIdentifier != null) {
                final ArrayList list = new ArrayList();
                final Collection<Recipient> recipients = this.getRecipients(new KeyTransRecipientId(issuer, keyTransRecipientId.getSerialNumber()));
                if (recipients != null) {
                    list.addAll(recipients);
                }
                final Collection<Recipient> recipients2 = this.getRecipients(new KeyTransRecipientId(subjectKeyIdentifier));
                if (recipients2 != null) {
                    list.addAll(recipients2);
                }
                return list;
            }
        }
        final ArrayList list2 = this.table.get(recipientId);
        return (list2 == null) ? new ArrayList<Recipient>() : new ArrayList<Recipient>(list2);
    }
    
    public Iterator<RecipientInformation> iterator() {
        return this.getRecipients().iterator();
    }
}
