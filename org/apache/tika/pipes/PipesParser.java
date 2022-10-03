package org.apache.tika.pipes;

import java.util.Iterator;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.List;
import java.io.Closeable;

public class PipesParser implements Closeable
{
    private final PipesConfig pipesConfig;
    private final List<PipesClient> clients;
    private final ArrayBlockingQueue<PipesClient> clientQueue;
    
    public PipesParser(final PipesConfig pipesConfig) {
        this.clients = new ArrayList<PipesClient>();
        this.pipesConfig = pipesConfig;
        this.clientQueue = new ArrayBlockingQueue<PipesClient>(pipesConfig.getNumClients());
        for (int i = 0; i < pipesConfig.getNumClients(); ++i) {
            final PipesClient client = new PipesClient(pipesConfig);
            this.clientQueue.offer(client);
            this.clients.add(client);
        }
    }
    
    public PipesResult parse(final FetchEmitTuple t) throws PipesException, IOException {
        PipesClient client = null;
        try {
            client = this.clientQueue.poll(this.pipesConfig.getMaxWaitForClientMillis(), TimeUnit.MILLISECONDS);
            if (client == null) {
                return PipesResult.CLIENT_UNAVAILABLE_WITHIN_MS;
            }
            return client.process(t);
        }
        catch (final InterruptedException e) {
            throw new PipesException(e);
        }
        finally {
            if (client != null) {
                this.clientQueue.offer(client);
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        final List<IOException> exceptions = new ArrayList<IOException>();
        for (final PipesClient pipesClient : this.clients) {
            try {
                pipesClient.close();
            }
            catch (final IOException e) {
                exceptions.add(e);
            }
        }
        if (exceptions.size() > 0) {
            throw exceptions.get(0);
        }
    }
}
