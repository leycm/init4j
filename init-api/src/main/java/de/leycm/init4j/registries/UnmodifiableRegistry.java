package de.leycm.init4j.registries;

import de.leycm.init4j.identifier.Identifier;
import de.leycm.init4j.registry.Registry;
import lombok.NonNull;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A {@link Registry} decorator that blocks all mutating operations.
 *
 * <p>All read operations are delegated to a backing {@link Registry} instance.
 * Any attempt to modify the registry via {@link #register(Identifier, Object)},
 * {@link #unregister(Identifier)}, or {@link #computeIfAbsent(Identifier, Function)}
 * will throw {@link UnsupportedOperationException}.</p>
 *
 * <p>Use {@link Registries#unmodifiableRegistry(Registry)} to obtain an instance.
 * This wrapper is useful for safely exposing a registry to external consumers
 * without granting write access.</p>
 *
 * <p>Thread Safety: Thread safety depends entirely on the backing {@link Registry}
 * implementation passed at construction time.</p>
 *
 * @param <T> the type of values stored in this registry
 * @since 1.0.0
 * @see Registries
 * @see Registry
 * @author Lennard <a href="mailto:leycm@proton.me">leycm@proton.me</a>
 */
public class UnmodifiableRegistry<T> implements Registry<T> {

    // backing registry all read operations are delegated to
    private final Registry<T> store;

    /**
     * Creates a new unmodifiable registry wrapping the given backing {@link Registry}.
     *
     * @param store the backing registry; must not be {@code null}
     */
    @ApiStatus.Internal
    protected UnmodifiableRegistry(final @NonNull Registry<T> store) {
        this.store = store;
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public void register(final @NonNull Identifier id, final @NotNull T value) {
        throw new UnsupportedOperationException("This registry is unmodifiable");
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public T unregister(final @NonNull Identifier id) {
        throw new UnsupportedOperationException("This registry is unmodifiable");
    }

    /** {@inheritDoc} */
    @Override
    public @NotNull T get(final @NonNull Identifier id) {
        return store.get(id);
    }

    /** {@inheritDoc} */
    @Override
    public @NonNull Optional<T> find(final @NonNull Identifier id) {
        return store.find(id);
    }

    /** {@inheritDoc} */
    @Override
    public boolean has(final @NonNull Identifier id) {
        return store.has(id);
    }

    /**
     * Always throws {@link UnsupportedOperationException}.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public @NotNull T computeIfAbsent(final @NonNull Identifier id, final @NonNull Function<Identifier, T> mappingFunction) {
        throw new UnsupportedOperationException("This registry is unmodifiable");
    }

    /** {@inheritDoc} */
    @Override
    public @NotNull T getOrDefault(final @NonNull Identifier id, final @NonNull Supplier<T> supplier) {
        return store.getOrDefault(id, supplier);
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

    /** {@inheritDoc} */
    @Override
    public Iterator<T> iterator() {
        return store.iterator();
    }
}