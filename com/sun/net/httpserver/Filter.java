package com.sun.net.httpserver;

import java.util.List;
import java.util.ListIterator;
import java.io.IOException;
import jdk.Exported;

@Exported
public abstract class Filter
{
    protected Filter() {
    }
    
    public abstract void doFilter(final HttpExchange p0, final Chain p1) throws IOException;
    
    public abstract String description();
    
    @Exported
    public static class Chain
    {
        private ListIterator<Filter> iter;
        private HttpHandler handler;
        
        public Chain(final List<Filter> list, final HttpHandler handler) {
            this.iter = list.listIterator();
            this.handler = handler;
        }
        
        public void doFilter(final HttpExchange httpExchange) throws IOException {
            if (!this.iter.hasNext()) {
                this.handler.handle(httpExchange);
            }
            else {
                this.iter.next().doFilter(httpExchange, this);
            }
        }
    }
}
