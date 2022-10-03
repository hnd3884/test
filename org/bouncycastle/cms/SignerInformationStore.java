package org.bouncycastle.cms;

import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import org.bouncycastle.util.Iterable;

public class SignerInformationStore implements Iterable<SignerInformation>
{
    private List all;
    private Map table;
    
    public SignerInformationStore(final SignerInformation signerInformation) {
        this.all = new ArrayList();
        this.table = new HashMap();
        (this.all = new ArrayList(1)).add(signerInformation);
        this.table.put(signerInformation.getSID(), this.all);
    }
    
    public SignerInformationStore(final Collection<SignerInformation> collection) {
        this.all = new ArrayList();
        this.table = new HashMap();
        for (final SignerInformation signerInformation : collection) {
            final SignerId sid = signerInformation.getSID();
            ArrayList list = this.table.get(sid);
            if (list == null) {
                list = new ArrayList(1);
                this.table.put(sid, list);
            }
            list.add(signerInformation);
        }
        this.all = new ArrayList(collection);
    }
    
    public SignerInformation get(final SignerId signerId) {
        final Collection<SignerInformation> signers = this.getSigners(signerId);
        return (signers.size() == 0) ? null : signers.iterator().next();
    }
    
    public int size() {
        return this.all.size();
    }
    
    public Collection<SignerInformation> getSigners() {
        return new ArrayList<SignerInformation>(this.all);
    }
    
    public Collection<SignerInformation> getSigners(final SignerId signerId) {
        if (signerId.getIssuer() != null && signerId.getSubjectKeyIdentifier() != null) {
            final ArrayList list = new ArrayList();
            final Collection<SignerInformation> signers = this.getSigners(new SignerId(signerId.getIssuer(), signerId.getSerialNumber()));
            if (signers != null) {
                list.addAll(signers);
            }
            final Collection<SignerInformation> signers2 = this.getSigners(new SignerId(signerId.getSubjectKeyIdentifier()));
            if (signers2 != null) {
                list.addAll(signers2);
            }
            return list;
        }
        final ArrayList list2 = this.table.get(signerId);
        return (list2 == null) ? new ArrayList<SignerInformation>() : new ArrayList<SignerInformation>(list2);
    }
    
    public Iterator<SignerInformation> iterator() {
        return this.getSigners().iterator();
    }
}
