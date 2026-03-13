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
 * A {@link Registry} decorator that prevents overwriting existing entries.
 *
 * <p>All operations are delegated to a backing {@link Registry} instance.
 * {@link #register(Identifier, Object)} additionally enforces that no entry
 * exists for the given {@link Identifier} before delegating — an
 * {@link IllegalStateException} is thrown otherwise.</p>
 *
 * <p>Use {@link Registries#noOverwriteRegistry(Registry)} to obtain an instance.</p>
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
public class NoOverwriteRegistry<T> implements Registry<T> {

    // backing registry all operations are delegated to
    private final Registry<T> store;

    /**
     * Creates a new no-overwrite registry wrapping the given backing {@link Registry}.
     *
     * @param store the backing registry; must not be {@code null}
     */
    @ApiStatus.Internal
    protected NoOverwriteRegistry(final @NonNull Registry<T> store) {
        this.store = store;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException when an entry is already registered for {@code id}
     */
    @Override
    public void register(final @NonNull Identifier id, final @NotNull T value) {
        if (store.has(id)) {
            throw new IllegalStateException("Entry already registered for id: " + id);
        }
        store.register(id, value);
    }

    /** {@inheritDoc} */
    @Override
    public T unregister(final @NonNull Identifier id) {
        return store.unregister(id);
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

    /** {@inheritDoc} */
    @Override
    public @NotNull T computeIfAbsent(final @NonNull Identifier id, final @NonNull Function<Identifier, T> mappingFunction) {
        return store.computeIfAbsent(id, mappingFunction);
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