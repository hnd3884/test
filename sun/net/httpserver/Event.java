package sun.net.httpserver;

class Event
{
    ExchangeImpl exchange;
    
    protected Event(final ExchangeImpl exchange) {
        this.exchange = exchange;
    }
}
