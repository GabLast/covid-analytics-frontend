package com.myorg.utils.broadcaster;

import com.vaadin.flow.shared.Registration;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public abstract class BroadcasterUI<T> {

    protected final Executor executor = Executors.newSingleThreadExecutor();

    protected final LinkedList<Consumer<T>> listeners = new LinkedList<>();

    public synchronized Registration register(Consumer<T> listener) {
        listeners.add(listener);

        return () -> {
            synchronized (BroadcasterUI.class) {
                listeners.remove(listener);
            }
        };
    }

    public synchronized void broadcast(T entity) {
        for (Consumer<T> listener : listeners) {
            executor.execute(() -> listener.accept(entity));
        }
    }
}
