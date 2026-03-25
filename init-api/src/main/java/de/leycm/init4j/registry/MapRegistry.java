package de.leycm.init4j.registry;

import de.leycm.init4j.identifier.Identifier;
import lombok.NonNull;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Abstract base {@link Registry} implementation backed by any {@link Map}.
 *
 * <p>Subclasses supply the concrete map via {@link MapRegistry#MapRegistry(Map)}} and provide
 * their own Javadoc.</p>
 *
 * @param <T> the type of values stored in this registry
 * @since 1.0.0
 * @author Lennard <a href="mailto:leycm@proton.me">leycm@proton.me</a>
 */
public abstract class MapRegistry<T> implements Registry<T> {

    private final Map<Identifier, T> store;

    /**
     * Creates a new registry with the given backing map. Subclasses must call this
     * constructor with a non-{@code null} map instance, which will be used to
     * store all entries in this registry.
     *
     * @param store the backing map; must not be {@code null}
      */
    @ApiStatus.Internal
    protected MapRegistry(final @NonNull Map<Identifier, T> store) {
        this.store = store;
    }

    /** {@inheritDoc} */
    @Override
    public void register(final @NonNull Identifier id, final @NonNull T value) {
        store.put(id, value);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException when no value is registered for {@code id}
     */
    @Override
    public T unregister(final @NonNull Identifier id) {
        T value = get(id);
        store.remove(id);
        return value;
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException when no value is registered for {@code id}
     */
    @Override
    public @NonNull T get(final @NonNull Identifier id) {
        T value = store.get(id);
        if (value == null)
            throw new NullPointerException("No entry registered for identifier \"" + id + "\"");
        return value;
    }

    /** {@inheritDoc} */
    @Override
    public @NonNull Optional<T> find(final @NonNull Identifier id) {
        return Optional.ofNullable(store.get(id));
    }

    /** {@inheritDoc} */
    @Override
    public boolean has(final @NonNull Identifier id) {
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
            final @NonNull Identifier id,
            final @NonNull Function<Identifier, T> mappingFunction
    ) {
        T value = store.computeIfAbsent(id, mappingFunction);
        if (value == null)
            throw new NullPointerException("Mapping function returned null for identifier '" + id + "'");
        return value;
    }

    @Override
    public @NotNull T getOrDefault(@NonNull Identifier id, @NotNull T value) {
        return store.getOrDefault(id, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull T getOrDefault(final @NonNull Identifier id,
                                   final @NonNull Supplier<T> supplier) {
        T value = store.get(id);
        if (value == null) {
            value = supplier.get();
        }

        if (value == null)
            throw new NullPointerException("Supplier returned null for identifier '" + id + "'");

        return value;
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
     * <p>The iteration order depends on the backing map supplied by
     * {@link MapRegistry#MapRegistry(Map)}.</p>
     *
     * @return an iterator over the values; never {@code null}
     */
    @Override
    public @NonNull Iterator<T> iterator() {
        return Collections.unmodifiableCollection(store.values()).iterator();
    }
}