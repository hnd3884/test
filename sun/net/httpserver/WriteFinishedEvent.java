package sun.net.httpserver;

class WriteFinishedEvent extends Event
{
    WriteFinishedEvent(final ExchangeImpl exchangeImpl) {
        super(exchangeImpl);
        assert !exchangeImpl.writefinished;
        exchangeImpl.writefinished = true;
    }
}
