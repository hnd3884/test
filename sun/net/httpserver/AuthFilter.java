package sun.net.httpserver;

import java.io.IOException;
import java.io.InputStream;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Filter;

public class AuthFilter extends Filter
{
    private Authenticator authenticator;
    
    public AuthFilter(final Authenticator authenticator) {
        this.authenticator = authenticator;
    }
    
    @Override
    public String description() {
        return "Authentication filter";
    }
    
    public void setAuthenticator(final Authenticator authenticator) {
        this.authenticator = authenticator;
    }
    
    public void consumeInput(final HttpExchange httpExchange) throws IOException {
        final InputStream requestBody = httpExchange.getRequestBody();
        while (requestBody.read(new byte[4096]) != -1) {}
        requestBody.close();
    }
    
    @Override
    public void doFilter(final HttpExchange httpExchange, final Chain chain) throws IOException {
        if (this.authenticator != null) {
            final Authenticator.Result authenticate = this.authenticator.authenticate(httpExchange);
            if (authenticate instanceof Authenticator.Success) {
                ExchangeImpl.get(httpExchange).setPrincipal(((Authenticator.Success)authenticate).getPrincipal());
                chain.doFilter(httpExchange);
            }
            else if (authenticate instanceof Authenticator.Retry) {
                final Authenticator.Retry retry = (Authenticator.Retry)authenticate;
                this.consumeInput(httpExchange);
                httpExchange.sendResponseHeaders(retry.getResponseCode(), -1L);
            }
            else if (authenticate instanceof Authenticator.Failure) {
                final Authenticator.Failure failure = (Authenticator.Failure)authenticate;
                this.consumeInput(httpExchange);
                httpExchange.sendResponseHeaders(failure.getResponseCode(), -1L);
            }
        }
        else {
            chain.doFilter(httpExchange);
        }
    }
}
