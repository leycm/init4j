package de.leycm.init4j.registry;

import de.leycm.init4j.identifier.Identifier;
import lombok.NonNull;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A thread-safe {@link Registry} implementation that forbids overwriting existing entries.
 *
 * <p>Once a value is registered under an {@link Identifier}, it cannot be replaced.
 * Attempting to {@link #register} an already-present key throws {@link IllegalStateException}.
 * Entries may still be removed via {@link #unregister}.</p>
 *
 * <p>Thread Safety: All operations are backed by a {@link ConcurrentHashMap} and
 * are safe for concurrent use without external synchronization.</p>
 *
 * @param <T> the type of values stored in this registry
 * @since 1.0.0
 * @author Lennard <a href="mailto:leycm@proton.me">leycm@proton.me</a>
 */
public class ImmutableRegistry<T> implements Registry<T> {

    @ApiStatus.Internal
    private final Map<Identifier, T> store = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException when {@code id} is already registered
     */
    @Override
    public void register(@NonNull Identifier id, @NonNull T value) {
        if (store.containsKey(id))
            throw new IllegalStateException(
                    "Identifier '" + id + "' is already registered and cannot be overwritten");

        store.put(id, value);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException when no value is registered for {@code id}
     */
    @Override
    public void unregister(@NonNull Identifier id) {
        if (!store.containsKey(id))
            throw new IllegalStateException(
                    "No entry registered for identifier '" + id + "'");

        store.remove(id);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException when no value is registered for {@code id}
     */
    @Override
    public @NonNull T get(@NonNull Identifier id) {
        T value = store.get(id);
        if (value == null)
            throw new NullPointerException(
                    "No entry registered for identifier '" + id + "'");
        return value;
    }

    /** {@inheritDoc} */
    @Override
    public @NonNull Optional<T> find(@NonNull Identifier id) {
        return Optional.ofNullable(store.get(id));
    }

    /** {@inheritDoc} */
    @Override
    public boolean has(@NonNull Identifier id) {
        return store.containsKey(id);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Uses {@link Map#computeIfAbsent} under the hood — the mapping function
     * is only called when no value exists for {@code id}.</p>
     *
     * @throws NullPointerException when the mapping function returns {@code null}
     */
    @Override
    public @NonNull T computeIfAbsent(
            @NonNull Identifier id,
            @NonNull Function<Identifier, T> mappingFunction
    ) {
        T value = store.computeIfAbsent(id, mappingFunction);
        if (value == null)
            throw new NullPointerException(
                    "Mapping function returned null for identifier '" + id + "'");
        return value;
    }

    /** {@inheritDoc} */
    @Override
    public @NonNull T getOrDefault(@NonNull Identifier id, @NonNull Supplier<T> supplier) {
        return store.getOrDefault(id, supplier.get());
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return store.size();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return store.isEmpty();
    }

    /**
     * Returns an unmodifiable iterator over all registered values.
     *
     * <p>The iteration order is undefined, as the backing store is a {@link ConcurrentHashMap}.</p>
     *
     * @return an iterator over the values; never {@code null}
     */
    @Override
    public @NonNull Iterator<T> iterator() {
        return Collections.unmodifiableCollection(store.values()).iterator();
    }
}