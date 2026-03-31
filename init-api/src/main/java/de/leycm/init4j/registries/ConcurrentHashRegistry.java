package de.leycm.init4j.registries;

import de.leycm.init4j.identifier.Identifier;
import de.leycm.init4j.registry.MapRegistry;
import de.leycm.init4j.registry.Registry;
import lombok.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A thread-safe {@link Registry} implementation backed by a
 * {@link ConcurrentHashMap} that allows overwriting existing entries.
 *
 * <p>Thread Safety: All operations are backed by a {@link ConcurrentHashMap}
 * and are safe for concurrent use without external synchronization.</p>
 *
 * @param <T> the type of values stored in this registry
 * @since 1.0
 * @author Lennard <a href="mailto:leycm@proton.me">leycm@proton.me</a>
 */
public class ConcurrentHashRegistry<T> extends MapRegistry<T> {

    /**
     * Creates a new registry backed by a {@link ConcurrentHashMap}.
     *
     * <p>All registry operations will be thread-safe, allowing concurrent
     * registration and retrieval of entries without external synchronization.</p>
     */
    public ConcurrentHashRegistry() {
        super(new ConcurrentHashMap<>());
    }

    /**
     * Creates a new registry with the specified initial capacity.
     *
     * <p>The initial capacity is passed to the underlying {@link ConcurrentHashMap},
     * which may help optimize performance if the expected number of entries is known.</p>
     *
     * @param initialCapacity the initial capacity of the registry; must be non-negative
     * @throws IllegalArgumentException if {@code initialCapacity} is negative
     */
    public ConcurrentHashRegistry(final int initialCapacity) {
        super(new ConcurrentHashMap<>(initialCapacity));
    }

    /**
     * Creates a new registry pre-populated with the entries from the given map.
     *
     * <p>All entries from {@code preset} are copied into the underlying
     * {@link ConcurrentHashMap}. Modifications to {@code preset} after
     * construction will not affect this registry.</p>
     *
     * @param preset the initial entries to populate the registry with; must not be {@code null}
     */
    public ConcurrentHashRegistry(final @NonNull Map<Identifier, ? extends T> preset) {
        super(new ConcurrentHashMap<>(preset));
    }

    /**
     * Creates a new registry with the specified initial capacity and load factor.
     *
     * <p>Both values are forwarded to the underlying {@link ConcurrentHashMap}
     * and can be used to tune memory usage and rehashing behavior when the
     * expected number of entries and density are known in advance.</p>
     *
     * @param initialCapacity the initial capacity; must be non-negative
     * @param loadFactor the load factor threshold for resizing; must be positive
     * @throws IllegalArgumentException if {@code initialCapacity} is negative or {@code loadFactor} is non-positive
     */
    public ConcurrentHashRegistry(final int initialCapacity, final float loadFactor) {
        super(new ConcurrentHashMap<>(initialCapacity, loadFactor));
    }

    /**
     * Creates a new registry with the specified initial capacity, load factor, and concurrency level.
     *
     * <p>All three tuning parameters are passed directly to the underlying
     * {@link ConcurrentHashMap}. The {@code concurrencyLevel} is a hint for
     * the number of concurrently updating threads, which may improve throughput
     * under heavy write contention.</p>
     *
     * @param initialCapacity the initial capacity; must be non-negative
     * @param loadFactor the load factor threshold for resizing; must be positive
     * @param concurrencyLevel the estimated number of concurrently updating threads; must be positive
     * @throws IllegalArgumentException if any parameter violates its constraint
     */
    public ConcurrentHashRegistry(final int initialCapacity, final float loadFactor, final int concurrencyLevel) {
        super(new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel));
    }

}