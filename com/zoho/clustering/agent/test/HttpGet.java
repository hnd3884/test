package com.zoho.clustering.agent.test;

import java.io.IOException;
import com.zoho.clustering.util.HttpMethod;
import java.util.TimerTask;
import java.util.Timer;

public class HttpGet
{
    public static void main(final String[] args) {
        if (args.length != 2) {
            System.out.print("\nInCorrect usage. Following args are expected");
            System.out.print("\n\targs[0] = url to fetch");
            System.out.print("\n\targs[1] = periodic-interval (in millis)");
            System.exit(1);
        }
        final Timer timer = new Timer();
        timer.schedule(new Worker(args[0]), 0L, Long.parseLong(args[1]));
    }
    
    private static class Worker extends TimerTask
    {
        private String url;
        private int counter;
        
        Worker(final String uri) {
            this.url = null;
            this.counter = 0;
            this.url = uri;
        }
        
        @Override
        public void run() {
            try {
                final HttpMethod meth = new HttpMethod(this.url);
                final int status = meth.execute();
                System.out.print("\n" + ++this.counter + "," + ((status == 200) ? meth.getResponseAsString() : ("Error - " + status)));
            }
            catch (final IOException exp) {
                System.out.print("\n" + ++this.counter + "," + exp.getMessage());
            }
        }
    }
}
