/*
 * This file is part of the init4j Library.
 *
 * Licensed under the GNU Lesser General Public License v3.0 (LGPL-3.0)
 * You should have received a copy of the license in LICENSE.LGPL
 * If not, see https://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Copyright (c) leycm <leycm@proton.me>
 * Copyright (c) maintainers
 */
package de.leycm.init4j.instance;

import lombok.NonNull;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A registry for {@link Initializable} instances, organized by namespace and class.
 *
 * <p>This class provides methods to register, retrieve, and manage instances of
 * {@link Initializable} implementations. Each instance is associated with a specific
 * namespace and class type, allowing for flexible and modular management of
 * initializable components.</p>
 *
 * <p>Namespaces group related instances together, preventing naming conflicts and
 * allowing multiple implementations of the same type within different contexts.</p>
 *
 * <p>Thread Safety: This class is thread-safe. All registry operations are backed
 * by {@link ConcurrentHashMap}, ensuring safe concurrent access without external
 * synchronization.</p>
 *
 * @since 1.0.0
 * @see Initializable
 * @author Lennard <a href="mailto:leycm@proton.me">leycm@proton.me</a>
 */
public class InitializableRegistry {

    // namespace -> (class -> instance)
    @ApiStatus.Internal
    private static final Map<String, Map<Class<?>, Initializable>> REGISTRY = new ConcurrentHashMap<>();

    /**
     * Returns the inner class-to-instance map for the given namespace, creating it if absent.
     *
     * @param namespace the target namespace; must not be {@code null}
     * @return the namespace map; never {@code null}
     * @throws NullPointerException when {@code namespace} is {@code null}
     */
    @ApiStatus.Internal
    private static Map<Class<?>, Initializable> getNamespace(final @NonNull String namespace) {
        return REGISTRY.computeIfAbsent(namespace, ns -> new ConcurrentHashMap<>());
    }

    /**
     * Retrieves a registered instance by namespace and class type.
     *
     * <p>Validates that the stored instance is assignment-compatible with {@code clazz}
     * before casting. The {@code @SuppressWarnings("unchecked")} is safe because
     * {@link Class#isInstance(Object)} is checked prior to the cast.</p>
     *
     * @param namespace the target namespace; must not be {@code null}
     * @param clazz the class type of the instance to retrieve; must not be {@code null}
     * @param <T> the type of the {@link Initializable} instance
     * @return the registered instance; never {@code null}
     * @throws NullPointerException when {@code namespace} or {@code clazz} is {@code null},
     *                              or when no instance is registered for {@code clazz} in the namespace
     * @throws ClassCastException when the registered instance is not assignment-compatible with {@code clazz}
     */
    @ApiStatus.Internal
    @SuppressWarnings("unchecked") // is checked by clazz.isInstance(instance) before
    protected static <T extends Initializable> @NonNull T getInstance(
            final @NonNull String namespace,
            final @NonNull Class<T> clazz
    ) throws NullPointerException, ClassCastException {
        Initializable instance = getNamespace(namespace).get(clazz);

        if (instance == null)
            throw new NullPointerException(
                    "No instance registered for " + clazz.getSimpleName() + " in namespace '" + namespace + "'");

        if (!clazz.isInstance(instance))
            throw new ClassCastException(
                    "Registered instance is not of type " + clazz.getSimpleName());

        return (T) instance;
    }

    /**
     * Returns the existing instance for the given namespace and class type, or computes and registers one if absent.
     *
     * <p>Delegates to {@link Map#computeIfAbsent(Object, Function)} on the namespace map.
     * The {@code @SuppressWarnings("unchecked")} is safe because {@link Class#isInstance(Object)}
     * is checked prior to the cast.</p>
     *
     * @param namespace the target namespace; must not be {@code null}
     * @param clazz the class type of the instance; must not be {@code null}
     * @param mappingFunction the function used to compute a new instance if none exists; must not be {@code null}
     * @param <T> the type of the {@link Initializable} instance
     * @return the existing or newly computed instance; never {@code null}
     * @throws ClassCastException when the resulting instance is not assignment-compatible with {@code clazz}
     * @throws NullPointerException when {@code namespace}, {@code clazz}, or {@code mappingFunction} is {@code null},
     *                              or when the mapping function returns {@code null}
     * @throws ClassCastException when the resulting instance is not assignment-compatible with {@code clazz}
     */
    @ApiStatus.Internal
    @SuppressWarnings("unchecked") // is checked by clazz.isInstance(instance) before
    protected static <T extends Initializable> @NonNull T computeIfAbsent(
            final @NonNull String namespace,
            final @NonNull Class<T> clazz,
            final @NonNull Function<Class<?>, T> mappingFunction
    ) throws NullPointerException, ClassCastException {
        Initializable instance = getNamespace(namespace).computeIfAbsent(clazz, mappingFunction);

        if (!clazz.isInstance(instance))
            throw new ClassCastException(
                    "Registered instance is not of type " + clazz.getSimpleName());

        return (T) instance;
    }

    /**
     * Returns whether an instance is registered for the given namespace and class type.
     *
     * @param namespace the target namespace; must not be {@code null}
     * @param clazz the class type to check; must not be {@code null}
     * @return {@code true} if an instance is registered, {@code false} otherwise
     * @throws NullPointerException when {@code namespace} or {@code clazz} is {@code null}
     */
    @ApiStatus.Internal
    protected static boolean hasInstance(
            final @NonNull String namespace,
            final @NonNull Class<?> clazz
    ) throws NullPointerException {
        return getNamespace(namespace).containsKey(clazz);
    }

    /**
     * Registers an instance under the given namespace and class type.
     *
     * <p>Invokes {@link Initializable#onInstall()} on the instance before storing it.
     * Throws if an instance of the same class type is already registered in the namespace.</p>
     *
     * @param namespace the target namespace; must not be {@code null}
     * @param instance the instance to register; must not be {@code null}
     * @param clazz the class type to associate with the instance; must not be {@code null}
     * @param <T> the type of the {@link Initializable} instance
     * @throws NullPointerException when {@code namespace}, {@code instance}, or {@code clazz} is {@code null},
     *                              or when an instance of {@code clazz} is already registered in the namespace
     */
    @ApiStatus.Internal
    protected static <T extends Initializable> void register(
            final @NonNull String namespace,
            final @NonNull T instance,
            final @NonNull Class<T> clazz
    ) throws NullPointerException {
        Map<Class<?>, Initializable> ns = getNamespace(namespace);

        if (ns.containsKey(clazz))
            throw new NullPointerException(
                    "An instance of " + clazz.getSimpleName() + " is already registered in namespace '" + namespace + "'");

        instance.onInstall();
        ns.put(clazz, instance);
    }

    /**
     * Removes the registered instance for the given namespace and class type.
     *
     * <p>Invokes {@link Initializable#onUninstall()} on the instance before removing it.
     * Throws if no instance of the given class type is currently registered in the namespace.</p>
     *
     * @param namespace the target namespace; must not be {@code null}
     * @param clazz the class type of the instance to remove; must not be {@code null}
     * @param <T> the type of the {@link Initializable} instance
     * @throws NullPointerException when {@code namespace} or {@code clazz} is {@code null},
     *                              or when no instance of {@code clazz} is registered in the namespace
     */
    @ApiStatus.Internal
    protected static <T extends Initializable> void unregister(
            final @NonNull String namespace,
            final @NonNull Class<T> clazz
    ) throws NullPointerException {
        Map<Class<?>, Initializable> ns = getNamespace(namespace);

        if (!ns.containsKey(clazz))
            throw new NullPointerException(
                    "There is no instance of " + clazz.getSimpleName() + " in namespace '" + namespace + "'");

        ns.get(clazz).onUninstall();
        ns.remove(clazz);
    }
}