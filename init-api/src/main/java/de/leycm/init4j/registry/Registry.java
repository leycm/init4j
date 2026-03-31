package de.leycm.init4j.registry;

import de.leycm.init4j.identifier.Identifier;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A generic registry that maps {@link Identifier} keys to values of type {@code T}.
 *
 * <p>All lookup and registration operations are keyed exclusively by {@link Identifier},
 * ensuring namespaced, collision-free access. Implementations may choose to allow or
 * forbid overwriting existing entries.</p>
 *
 * @param <T> the type of values stored in this registry
 * @since 1.0
 * @author Lennard <a href="mailto:leycm@proton.me">leycm@proton.me</a>
 */
public interface Registry<T> extends Iterable<RegistryPair<T>> {

    // ==== Registry methods =================================================

    /**
     * Registers a value under the given identifier.
     *
     * @param id    the identifier; must not be {@code null}
     * @param value the value to register; must not be {@code null}
     * @throws NullPointerException when {@code id} or {@code value} is {@code null}
     */
    void register(@NonNull Identifier id, @NonNull T value);

    /**
     * Removes the value associated with the given identifier.
     *
     * @param id the identifier to remove; must not be {@code null}
     * @return the removed value, or {@code null} if no entry was registered for {@code id}
     * @throws NullPointerException when {@code id} is {@code null}
     */
    @Nullable T unregister(@NonNull Identifier id);

    /**
     * Retrieves the value associated with the given identifier.
     *
     * <p>Unlike most {@link Map} lookups, this method never returns
     * {@code null} - implementations are expected to throw when the key is absent.</p>
     *
     * @param id the identifier to look up; must not be {@code null}
     * @return the registered value; never {@code null}
     * @throws NullPointerException when {@code id} is {@code null}, or when
     *                              no value is registered for {@code id}
     */
    @NonNull T get(@NonNull Identifier id);

    /**
     * Retrieves the value as an {@link Optional}, returning empty if not present.
     *
     * @param id the identifier to look up; must not be {@code null}
     * @return an {@link Optional} containing the value, or empty; never {@code null}
     * @throws NullPointerException when {@code id} is {@code null}
     */
    @NonNull Optional<T> find(@NonNull Identifier id);

    /**
     * Returns whether a value is registered for the given identifier.
     *
     * @param id the identifier to check; must not be {@code null}
     * @return {@code true} if a value is registered, {@code false} otherwise
     * @throws NullPointerException when {@code id} is {@code null}
     */
    boolean has(@NonNull Identifier id);

    /**
     * Returns the existing value for {@code id}, or computes and registers one if absent.
     *
     * @param id              the identifier; must not be {@code null}
     * @param mappingFunction the function to compute a new value; must not be {@code null}
     * @return the existing or newly computed value; never {@code null}
     * @throws NullPointerException when any argument is {@code null}
     */
    @NonNull T computeIfAbsent(@NonNull Identifier id, @NonNull Function<Identifier, T> mappingFunction);

    /**
     * Returns the existing value for {@code id}, or {@code value} if no entry exists.
     *
     * <p>Note: Implementations that don't evaluate the {@code supplier} eagerly.
     * should overwrite this.</p>
     *
     * @param id    the identifier; must not be {@code null}
     * @param value the fallback value; must not be {@code null}
     * @return the existing or fallback value; never {@code null}
     * @throws NullPointerException when any argument is {@code null}
     */
    default @NonNull T getOrDefault(@NonNull Identifier id, @NonNull T value) {
        return getOrDefault(id, () -> value);
    }

    /**
     * Returns the existing value for {@code id}, or the value produced by {@code supplier}
     * if no entry exists. Unlike {@link #computeIfAbsent}, the supplied value is
     * <em>not</em> registered.
     *
     * <p>Note: Implementations may evaluate the {@code supplier} eagerly.
     * Callers should not rely on lazy evaluation of the fallback.</p>
     *
     * @param id       the identifier; must not be {@code null}
     * @param supplier the supplier for a fallback value; must not be {@code null}
     * @return the existing or supplied value; never {@code null}
     * @throws NullPointerException when any argument is {@code null}
     */
    @NonNull T getOrDefault(@NonNull Identifier id, @NonNull Supplier<T> supplier);

    // ==== Object methods ====================================================

    /**
     * Returns the number of registered entries.
     *
     * <p>Note: Returns a positive integer if the registry contains entries, or zero if it is empty.
     * Everything higher than {@link Integer#MAX_VALUE} will return {@link Integer#MAX_VALUE}.</p>
     *
     * @return the entry count; never negative
     */
    int size();

    /**
     * Returns whether this registry contains no entries.
     *
     * @return {@code true} if empty, {@code false} otherwise
     */
    boolean isEmpty();

    /**
     * Returns a map view of this registry, where keys are identifiers and values are the registered entries.
     *
     * <p>Modifications to the returned map may <b>not</b> affect the registry.
     * The map is guaranteed to reflect the current state of the registry at the time of calling.</p>
     *
     * @return a map view of this registry; never {@code null}
     */
    @NonNull Map<Identifier, T> toMap();

    /**
     * Returns an iterator over the registered entries as {@link RegistryPair} instances.
     *
     * <p>The iteration order depends on the implementation and may reflect insertion order, natural ordering,
     * or be arbitrary. The returned iterator is unmodifiable and does not support {@code remove()}.</p>
     *
     * @return an iterator over the registry entries; never {@code null}
     */
    @NonNull Iterator<RegistryPair<T>> iterator();
}