package HTTPClient;

import java.io.IOException;

class RetryException extends IOException
{
    Request request;
    Response response;
    RetryException first;
    RetryException next;
    IOException exception;
    boolean conn_reset;
    boolean restart;
    
    public RetryException() {
        this.conn_reset = true;
        this.restart = false;
    }
    
    public RetryException(final String s) {
        super(s);
        this.conn_reset = true;
        this.restart = false;
    }
    
    void addToListAfter(final RetryException re) {
        if (re == null) {
            return;
        }
        if (re.next != null) {
            this.next = re.next;
        }
        re.next = this;
    }
}
