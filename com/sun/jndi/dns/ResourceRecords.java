package com.sun.jndi.dns;

import javax.naming.CommunicationException;
import javax.naming.NamingException;
import java.util.Vector;

class ResourceRecords
{
    Vector<ResourceRecord> question;
    Vector<ResourceRecord> answer;
    Vector<ResourceRecord> authority;
    Vector<ResourceRecord> additional;
    boolean zoneXfer;
    
    ResourceRecords(final byte[] array, final int n, final Header header, final boolean zoneXfer) throws NamingException {
        this.question = new Vector<ResourceRecord>();
        this.answer = new Vector<ResourceRecord>();
        this.authority = new Vector<ResourceRecord>();
        this.additional = new Vector<ResourceRecord>();
        if (zoneXfer) {
            this.answer.ensureCapacity(8192);
        }
        this.zoneXfer = zoneXfer;
        this.add(array, n, header);
    }
    
    int getFirstAnsType() {
        if (this.answer.size() == 0) {
            return -1;
        }
        return this.answer.firstElement().getType();
    }
    
    int getLastAnsType() {
        if (this.answer.size() == 0) {
            return -1;
        }
        return this.answer.lastElement().getType();
    }
    
    void add(final byte[] array, final int n, final Header header) throws NamingException {
        int n2 = 12;
        try {
            for (int i = 0; i < header.numQuestions; ++i) {
                final ResourceRecord resourceRecord = new ResourceRecord(array, n, n2, true, false);
                if (!this.zoneXfer) {
                    this.question.addElement(resourceRecord);
                }
                n2 += resourceRecord.size();
            }
            for (int j = 0; j < header.numAnswers; ++j) {
                final ResourceRecord resourceRecord2 = new ResourceRecord(array, n, n2, false, !this.zoneXfer);
                this.answer.addElement(resourceRecord2);
                n2 += resourceRecord2.size();
            }
            if (this.zoneXfer) {
                return;
            }
            for (int k = 0; k < header.numAuthorities; ++k) {
                final ResourceRecord resourceRecord3 = new ResourceRecord(array, n, n2, false, true);
                this.authority.addElement(resourceRecord3);
                n2 += resourceRecord3.size();
            }
        }
        catch (final IndexOutOfBoundsException ex) {
            throw new CommunicationException("DNS error: corrupted message");
        }
    }
}
