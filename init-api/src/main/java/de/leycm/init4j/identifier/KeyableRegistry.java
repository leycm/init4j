package de.leycm.init4j.identifier;

import lombok.NonNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Internal registry for managing type-to-key mapping functions.
 *
 * <p>This class maintains a registry of {@link Function} instances that convert
 * objects of specific types to string keys. It is used internally by {@link Keyable}
 * to provide automatic key generation for objects that don't implement {@link Keyable}
 * directly.</p>
 *
 * <p>The registry supports inheritance-based lookup, checking superclasses and
 * interfaces when no direct mapping is found. A default fallback mapping using
 * {@link Identifier#sanitizeKey(String)} is provided for unmapped types.</p>
 *
 * <p>Thread Safety: This class is thread-safe and uses {@link ConcurrentHashMap}
 * for both the main registry and lookup cache.</p>
 *
 * @since 1.3
 * @see Keyable
 * @see Identifier
 * @author Lennard <a href="mailto:leycm@proton.me">leycm@proton.me</a>
 */
public final class KeyableRegistry {

    /**
     * Private constructor to prevent instantiation.
     *
     * <p>This class is designed to be used statically and should not be instantiated.
     * Attempting to create an instance will result in an {@link UnsupportedOperationException}.</p>
     *
     * @throws UnsupportedOperationException always
     */
    private KeyableRegistry() {
        throw new UnsupportedOperationException("KeyableRegistry is a static only class and cannot be instantiated");
    }

    // type -> mapping function
    private static final Map<Class<?>, Function<?, String>> REGISTRY = new ConcurrentHashMap<>();
    // type -> cached resolved mapping function
    private static final Map<Class<?>, Function<?, String>> CACHE = new ConcurrentHashMap<>();

    // Pre-registers common mappings for consistency with Identifier factory methods
    static {
        // note: for consistency with Identifier#of(String, ...)
        register(Class.class, Class::getName);
        register(UUID.class, UUID::toString);
    }

    /**
     * Registers a mapping function for the given type.
     *
     * <p>The mapping function will be used to convert instances of the given type
     * to string keys. Clears the lookup cache to ensure new mappings take effect.
     * Throws if a mapping for the type is already registered.</p>
     *
     * @param type the class type to register a mapping for; must not be {@code null}
     * @param mapping the function that converts instances of {@code type} to string keys; must not be {@code null}
     * @param <T> the type being registered
     * @throws IllegalStateException when a mapping for {@code type} is already registered
     * @throws NullPointerException when {@code type} or {@code mapping} is {@code null}
     */
    static <T> void register(
            final @NonNull Class<T> type,
            final @NonNull Function<T, String> mapping
    ) throws IllegalStateException {
        if (REGISTRY.containsKey(type)) {
            throw new IllegalStateException("A mapping for type " + type.getName() + " is already registered");
        }

        REGISTRY.put(type, mapping);
        CACHE.clear();
    }

    /**
     * Resolves a {@link Keyable} instance for the given object using registered mappings.
     *
     * <p>The method looks up a mapping function for the object's type, using a cache
     * for performance. If no direct mapping is found, it searches superclasses and
     * interfaces. Falls back to {@link Identifier#sanitizeKey(String)} if no mapping exists.</p>
     *
     * @param o the object to resolve; must not be {@code null}
     * @return a {@link Keyable} instance that delegates to the resolved key; never {@code null}
     * @throws NullPointerException when {@code o} is {@code null}
     */
    @SuppressWarnings("unchecked") // cause: we force this via KeyableRegistry#register(Class, Function)
    static @NonNull Keyable resolve(final @NonNull Object o) {
        final Function<Object, String> mapper = (Function<Object, String>) CACHE.computeIfAbsent(o.getClass(), KeyableRegistry::lookup);
        // note: resolving once for performance
        final String key = mapper.apply(o);
        return () -> key;
    }

    /**
     * Looks up a mapping function for the given type, checking inheritance hierarchy.
     *
     * <p>First checks for a direct mapping, then searches superclasses (excluding {@link Object}),
     * and finally all interfaces. Returns a fallback mapping that uses
     * {@link Identifier#sanitizeKey(String)} if no mapping is found.</p>
     *
     * @param type the class type to look up; must not be {@code null}
     * @return a mapping function for the type; never {@code null}
     * @throws NullPointerException when {@code type} is {@code null}
     */
    private static @NonNull Function<?, String> lookup(final @NonNull Class<?> type) {
        Function<?, String> fn = REGISTRY.get(type);
        if (fn != null) return fn;

        Class<?> superClass = type.getSuperclass();
        while (superClass != null && superClass != Object.class) {
            fn = REGISTRY.get(superClass);
            if (fn != null) return fn;
            superClass = superClass.getSuperclass();
        }

        for (Class<?> iface : type.getInterfaces()) {
            fn = REGISTRY.get(iface);
            if (fn != null) return fn;
        }

        return o -> Identifier.sanitizeKey(String.valueOf(o));
    }
}