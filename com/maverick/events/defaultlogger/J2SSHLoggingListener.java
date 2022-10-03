package com.maverick.events.defaultlogger;

import com.maverick.events.J2SSHEventMessages;
import com.maverick.events.Event;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import java.util.Hashtable;
import com.maverick.events.EventListener;

public class J2SSHLoggingListener implements EventListener
{
    String e;
    Hashtable c;
    Log d;
    boolean b;
    
    public void setProduct(final String e) {
        this.e = e;
    }
    
    public String getProduct() {
        return this.e;
    }
    
    public J2SSHLoggingListener() {
        this.e = "J2SSH:";
        this.c = new Hashtable();
        this.d = LogFactory.getLog(J2SSHLoggingListener.class);
        this.b = false;
    }
    
    public J2SSHLoggingListener(final boolean ignoreLogEvents) {
        this.e = "J2SSH:";
        this.c = new Hashtable();
        this.d = LogFactory.getLog(J2SSHLoggingListener.class);
        this.b = false;
        this.setIgnoreLogEvents(ignoreLogEvents);
    }
    
    public void setIgnoreLogEvents(final boolean b) {
        this.b = b;
    }
    
    public void processEvent(final Event event) {
        if ((event.getId() == 110 || event.getId() == 111 || event.getId() == 112) && !this.b) {
            final Class<?> class1 = event.getSource().getClass();
            if (!this.c.containsKey(class1)) {
                this.c.put(class1, LogFactory.getLog((Class)class1));
            }
            switch (event.getId()) {
                case 110: {
                    ((Log)this.c.get(class1)).info((Object)(this.e + event.getAttribute("LOG_MESSAGE")));
                    break;
                }
                case 111: {
                    ((Log)this.c.get(class1)).debug((Object)(this.e + event.getAttribute("LOG_MESSAGE")));
                    break;
                }
                case 112: {
                    ((Log)this.c.get(class1)).error((Object)(this.e + event.getAttribute("LOG_MESSAGE")), (Throwable)event.getAttribute("THROWABLE"));
                    break;
                }
            }
        }
        else {
            this.d.info((Object)(this.e + J2SSHEventMessages.messageCodes.get(new Integer(event.getId())) + event.getAllAttributes()));
        }
    }
}
