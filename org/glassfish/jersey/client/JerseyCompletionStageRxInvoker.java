package org.glassfish.jersey.client;

import javax.ws.rs.core.GenericType;
import java.util.concurrent.Executor;
import java.util.concurrent.CompletableFuture;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.SyncInvoker;
import java.util.concurrent.ExecutorService;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.CompletionStageRxInvoker;
import java.util.concurrent.CompletionStage;

public class JerseyCompletionStageRxInvoker extends AbstractRxInvoker<CompletionStage> implements CompletionStageRxInvoker
{
    JerseyCompletionStageRxInvoker(final Invocation.Builder builder, final ExecutorService executor) {
        super((SyncInvoker)builder, executor);
    }
    
    public <T> CompletionStage<T> method(final String name, final Entity<?> entity, final Class<T> responseType) {
        final ExecutorService executorService = this.getExecutorService();
        return (CompletionStage<T>)((executorService == null) ? CompletableFuture.supplyAsync(() -> this.getSyncInvoker().method(name, entity, responseType)) : CompletableFuture.supplyAsync(() -> this.getSyncInvoker().method(name, entity, responseType), executorService));
    }
    
    public <T> CompletionStage<T> method(final String name, final Entity<?> entity, final GenericType<T> responseType) {
        final ExecutorService executorService = this.getExecutorService();
        return (CompletionStage<T>)((executorService == null) ? CompletableFuture.supplyAsync(() -> this.getSyncInvoker().method(name, entity, responseType)) : CompletableFuture.supplyAsync(() -> this.getSyncInvoker().method(name, entity, responseType), executorService));
    }
}
