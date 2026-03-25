package de.leycm.init4j.identifier;

import lombok.NonNull;
import org.jetbrains.annotations.Contract;

import java.util.function.Function;

/**
 * Contract for objects that can provide a string key representation.
 *
 * <p>Implementations of this interface can convert themselves to a string key
 * that can be used for identification, registration, or lookup purposes.
 * The key format is implementation-specific but should be consistent for
 * equal objects and ideally human-readable.</p>
 *
 * <p>Static factory methods are provided for registering type-specific
 * key mapping functions and for resolving any object to a {@link Keyable}
 * instance, either by direct implementation or through registered mappings.</p>
 *
 * <p>Thread Safety: The registry operations are thread-safe and backed by
* {@link ConcurrentHashMap}.</p>
 *
 * @since 1.0.0
 * @see KeyableRegistry
 * @see Identifier
 * @author Lennard <a href="mailto:leycm@proton.me">leycm@proton.me</a>
 */
public interface Keyable {

    /**
     * Registers a mapping function for the given type to convert instances to string keys.
     *
     * <p>The mapping function will be used by {@link #lookup(Object)} when an object
     * of the given type needs to be converted to a {@link Keyable} instance.
     * Throws if a mapping for the type is already registered.</p>
     *
     * @param type the class type to register a mapping for; must not be {@code null}
     * @param mapping the function that converts instances of {@code type} to string keys; must not be {@code null}
     * @param <T> the type being registered
     * @throws IllegalArgumentException when a mapping for {@code type} is already registered
     * @throws NullPointerException when {@code type} or {@code mapping} is {@code null}
     */
    static <T> void register(
            final @NonNull Class<T> type,
            final @NonNull Function<T, String> mapping
    ) throws IllegalArgumentException {
        KeyableRegistry.register(type, mapping);
    }

    /**
     * Returns a {@link Keyable} representation of the given object.
     *
     * <p>If the object already implements {@link Keyable}, it is returned directly.
     * Otherwise, a registered mapping function is used to create a {@link Keyable}
     * instance that delegates to the mapped key.</p>
     *
     * @param o the object to convert; must not be {@code null}
     * @return a {@link Keyable} instance for the object; never {@code null}
     * @throws NullPointerException when {@code o} is {@code null}
     */
    @Contract(pure = true)
    static @NonNull Keyable lookup(final @NonNull Object o) {
        // note: this is a bit of a hack to avoid unnecessary registry lookups
        if (o instanceof Keyable k) return k;
        return KeyableRegistry.resolve(o);
    }

    /**
     * Returns a string key representing this object.
     *
     * <p>The exact format of the key is implementation-specific and may vary
     * between implementations, but it must be consistent for equal objects
     * and should ideally be human-readable.</p>
     *
     * <p><b>Note:</b> The implementation should always pass an
     * {@link Identifier#validateKey(String)} check.</p>
     *
     * @return a non-{@code null} string key that uniquely identifies this object
     */
    @NonNull String toKey();

}
