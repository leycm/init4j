package de.leycm.init4j.registries;

import de.leycm.init4j.identifier.Identifier;
import de.leycm.init4j.registry.MapRegistry;
import de.leycm.init4j.registry.Registry;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link Registry} implementation backed by a {@link HashMap} that allows
 * overwriting existing entries.
 *
 * <p>Thread Safety: This implementation is <em>not</em> thread-safe. If
 * concurrent access is required, use {@link ConcurrentHashRegistry} instead.</p>
 *
 * @param <T> the type of values stored in this registry
 * @since 1.0.0
 * @author Lennard <a href="mailto:leycm@proton.me">leycm@proton.me</a>
 */
public class HashRegistry<T> extends MapRegistry<T> {

    /**
     * Creates a new registry backed by a {@link HashMap}.
     */
    public HashRegistry() {
        super(new HashMap<>());
    }

    /**
     * Creates a new registry with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the registry; must be non-negative
     * @throws IllegalArgumentException if {@code initialCapacity} is negative
     */
    public HashRegistry(final int initialCapacity) {
        super(new HashMap<>(initialCapacity));
    }

    /**
     * Creates a new registry pre-populated with the entries from the given map.
     *
     * <p>All entries from {@code preset} are copied into the underlying
     * {@link HashMap}. Modifications to {@code preset} after construction
     * will not affect this registry.</p>
     *
     * @param preset the initial entries to populate the registry with; must not be {@code null}
     */
    public HashRegistry(final @NonNull Map<Identifier, ? extends T> preset) {
        super(new HashMap<>(preset));
    }

    /**
     * Creates a new registry with the specified initial capacity and load factor.
     *
     * @param initialCapacity the initial capacity; must be non-negative
     * @param loadFactor the load factor threshold for resizing; must be positive
     * @throws IllegalArgumentException if {@code initialCapacity} is negative or {@code loadFactor} is non-positive
     */
    public HashRegistry(final int initialCapacity, final float loadFactor) {
        super(new HashMap<>(initialCapacity, loadFactor));
    }

}