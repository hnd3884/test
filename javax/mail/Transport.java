package javax.mail;

import java.util.EventListener;
import javax.mail.event.MailEvent;
import javax.mail.event.TransportEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import javax.mail.event.TransportListener;
import java.util.Vector;

public abstract class Transport extends Service
{
    private volatile Vector<TransportListener> transportListeners;
    
    public Transport(final Session session, final URLName urlname) {
        super(session, urlname);
        this.transportListeners = null;
    }
    
    public static void send(final Message msg) throws MessagingException {
        msg.saveChanges();
        send0(msg, msg.getAllRecipients(), null, null);
    }
    
    public static void send(final Message msg, final Address[] addresses) throws MessagingException {
        msg.saveChanges();
        send0(msg, addresses, null, null);
    }
    
    public static void send(final Message msg, final String user, final String password) throws MessagingException {
        msg.saveChanges();
        send0(msg, msg.getAllRecipients(), user, password);
    }
    
    public static void send(final Message msg, final Address[] addresses, final String user, final String password) throws MessagingException {
        msg.saveChanges();
        send0(msg, addresses, user, password);
    }
    
    private static void send0(final Message msg, final Address[] addresses, final String user, final String password) throws MessagingException {
        if (addresses == null || addresses.length == 0) {
            throw new SendFailedException("No recipient addresses");
        }
        final Map<String, List<Address>> protocols = new HashMap<String, List<Address>>();
        final List<Address> invalid = new ArrayList<Address>();
        final List<Address> validSent = new ArrayList<Address>();
        final List<Address> validUnsent = new ArrayList<Address>();
        for (int i = 0; i < addresses.length; ++i) {
            if (protocols.containsKey(addresses[i].getType())) {
                final List<Address> v = protocols.get(addresses[i].getType());
                v.add(addresses[i]);
            }
            else {
                final List<Address> w = new ArrayList<Address>();
                w.add(addresses[i]);
                protocols.put(addresses[i].getType(), w);
            }
        }
        final int dsize = protocols.size();
        if (dsize == 0) {
            throw new SendFailedException("No recipient addresses");
        }
        final Session s = (msg.session != null) ? msg.session : Session.getDefaultInstance(System.getProperties(), null);
        if (dsize == 1) {
            final Transport transport = s.getTransport(addresses[0]);
            try {
                if (user != null) {
                    transport.connect(user, password);
                }
                else {
                    transport.connect();
                }
                transport.sendMessage(msg, addresses);
            }
            finally {
                transport.close();
            }
            return;
        }
        MessagingException chainedEx = null;
        boolean sendFailed = false;
        for (final List<Address> v2 : protocols.values()) {
            final Address[] protaddresses = new Address[v2.size()];
            v2.toArray(protaddresses);
            final Transport transport;
            if ((transport = s.getTransport(protaddresses[0])) == null) {
                for (int j = 0; j < protaddresses.length; ++j) {
                    invalid.add(protaddresses[j]);
                }
            }
            else {
                try {
                    transport.connect();
                    transport.sendMessage(msg, protaddresses);
                }
                catch (final SendFailedException sex) {
                    sendFailed = true;
                    if (chainedEx == null) {
                        chainedEx = sex;
                    }
                    else {
                        chainedEx.setNextException(sex);
                    }
                    Address[] a = sex.getInvalidAddresses();
                    if (a != null) {
                        for (int k = 0; k < a.length; ++k) {
                            invalid.add(a[k]);
                        }
                    }
                    a = sex.getValidSentAddresses();
                    if (a != null) {
                        for (int l = 0; l < a.length; ++l) {
                            validSent.add(a[l]);
                        }
                    }
                    final Address[] c = sex.getValidUnsentAddresses();
                    if (c != null) {
                        for (int m = 0; m < c.length; ++m) {
                            validUnsent.add(c[m]);
                        }
                    }
                }
                catch (final MessagingException mex) {
                    sendFailed = true;
                    if (chainedEx == null) {
                        chainedEx = mex;
                    }
                    else {
                        chainedEx.setNextException(mex);
                    }
                }
                finally {
                    transport.close();
                }
            }
        }
        if (sendFailed || invalid.size() != 0 || validUnsent.size() != 0) {
            Address[] a2 = null;
            Address[] b = null;
            Address[] c2 = null;
            if (validSent.size() > 0) {
                a2 = new Address[validSent.size()];
                validSent.toArray(a2);
            }
            if (validUnsent.size() > 0) {
                b = new Address[validUnsent.size()];
                validUnsent.toArray(b);
            }
            if (invalid.size() > 0) {
                c2 = new Address[invalid.size()];
                invalid.toArray(c2);
            }
            throw new SendFailedException("Sending failed", chainedEx, a2, b, c2);
        }
    }
    
    public abstract void sendMessage(final Message p0, final Address[] p1) throws MessagingException;
    
    public synchronized void addTransportListener(final TransportListener l) {
        if (this.transportListeners == null) {
            this.transportListeners = new Vector<TransportListener>();
        }
        this.transportListeners.addElement(l);
    }
    
    public synchronized void removeTransportListener(final TransportListener l) {
        if (this.transportListeners != null) {
            this.transportListeners.removeElement(l);
        }
    }
    
    protected void notifyTransportListeners(final int type, final Address[] validSent, final Address[] validUnsent, final Address[] invalid, final Message msg) {
        if (this.transportListeners == null) {
            return;
        }
        final TransportEvent e = new TransportEvent(this, type, validSent, validUnsent, invalid, msg);
        this.queueEvent(e, this.transportListeners);
    }
}
