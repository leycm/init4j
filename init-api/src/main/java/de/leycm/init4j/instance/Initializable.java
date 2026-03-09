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

import java.util.function.Function;

/**
 * Contract for components managed by the {@link InitializableRegistry}.
 *
 * <p>Implementations of this interface can be registered under a namespace
 * and class type, allowing the registry to store, retrieve, and lifecycle-manage
 * them across an application. Each instance is associated with exactly one
 * class type per namespace.</p>
 *
 * <p>Static convenience methods are provided for both explicit namespace usage
 * and the {@link #DEFAULT_NAMESPACE}, which defaults to {@code "de.leycm.init4j"}.
 * Lifecycle callbacks {@link #onInstall()} and {@link #onUninstall()} are invoked
 * automatically by the registry on registration and removal.</p>
 *
 * <p>Thread Safety: Thread safety is delegated to {@link InitializableRegistry},
 * which uses {@link java.util.concurrent.ConcurrentHashMap} internally.</p>
 *
 * @since 1.0.0
 * @see InitializableRegistry
 * @author Lennard <a href="mailto:leycm@proton.me">leycm@proton.me</a>
 */
public interface Initializable {

    /**
     * The default namespace used when no explicit namespace is provided.
     *
     * <p>Defaults to {@code "de.leycm.init4j"}. All overloads without a
     * {@code namespace} parameter operate against this namespace.</p>
     */
    String DEFAULT_NAMESPACE = "de.leycm.init4j";

    /**
     * Retrieves a registered instance by namespace and class type.
     *
     * @param namespace the target namespace; must not be {@code null}
     * @param clazz the class type of the instance to retrieve; must not be {@code null}
     * @param <T> the type of the {@link Initializable} instance
     * @return the registered instance; never {@code null}
     * @throws NullPointerException when no instance is registered for the given class in the namespace
     */
    static <T extends Initializable> @NonNull T getInstance(
            final @NonNull String namespace,
            final @NonNull Class<T> clazz
    ) throws NullPointerException {
        return InitializableRegistry.getInstance(namespace, clazz);
    }

    /**
     * Returns the existing instance for the given namespace and class type, or computes and registers one if absent.
     *
     * @param namespace the target namespace; must not be {@code null}
     * @param clazz the class type of the instance; must not be {@code null}
     * @param mappingFunction the function used to compute a new instance if none exists; must not be {@code null}
     * @param <T> the type of the {@link Initializable} instance
     * @return the existing or newly computed instance; never {@code null}
     * @throws NullPointerException when the computed instance is {@code null}
     */
    static <T extends Initializable> @NonNull T computeIfAbsent(
            final @NonNull String namespace,
            final @NonNull Class<T> clazz,
            final @NonNull Function<Class<?>, T> mappingFunction
    ) throws NullPointerException {
        return InitializableRegistry.computeIfAbsent(namespace, clazz, mappingFunction);
    }

    /**
     * Returns whether an instance is registered for the given namespace and class type.
     *
     * @param namespace the target namespace; must not be {@code null}
     * @param clazz the class type to check; must not be {@code null}
     * @return {@code true} if an instance is registered, {@code false} otherwise
     * @throws NullPointerException when {@code namespace} or {@code clazz} is {@code null}
     */
    static boolean hasInstance(
            final @NonNull String namespace,
            final @NonNull Class<?> clazz
    ) throws NullPointerException {
        return InitializableRegistry.hasInstance(namespace, clazz);
    }

    /**
     * Registers an instance under the given namespace and class type.
     *
     * <p>Triggers {@link #onInstall()} on the instance before it is stored.
     * Throws if an instance of the same class is already registered in the namespace.</p>
     *
     * @param namespace the target namespace; must not be {@code null}
     * @param instance the instance to register; must not be {@code null}
     * @param clazz the class type to associate with the instance; must not be {@code null}
     * @param <T> the type of the {@link Initializable} instance
     * @throws NullPointerException when an instance of {@code clazz} is already registered in the namespace
     */
    static <T extends Initializable> void register(
            final @NonNull String namespace,
            final @NonNull T instance,
            final @NonNull Class<T> clazz
    ) throws NullPointerException {
        InitializableRegistry.register(namespace, instance, clazz);
    }

    /**
     * Removes the registered instance for the given namespace and class type.
     *
     * <p>Triggers {@link #onUninstall()} on the instance before removal.
     * Throws if no instance of the given class is currently registered in the namespace.</p>
     *
     * @param namespace the target namespace; must not be {@code null}
     * @param clazz the class type of the instance to remove; must not be {@code null}
     * @param <T> the type of the {@link Initializable} instance
     * @throws NullPointerException when no instance of {@code clazz} is registered in the namespace
     */
    static <T extends Initializable> void unregister(
            final @NonNull String namespace,
            final @NonNull Class<T> clazz
    ) throws NullPointerException {
        InitializableRegistry.unregister(namespace, clazz);
    }

    /**
     * Retrieves a registered instance from the {@link #DEFAULT_NAMESPACE} by class type.
     *
     * @param clazz the class type of the instance to retrieve; must not be {@code null}
     * @param <T> the type of the {@link Initializable} instance
     * @return the registered instance; never {@code null}
     * @throws NullPointerException when no instance is registered for the given class
     * @see #getInstance(String, Class)
     */
    static <T extends Initializable> @NonNull T getInstance(
            final @NonNull Class<T> clazz
    ) throws NullPointerException {
        return InitializableRegistry.getInstance(DEFAULT_NAMESPACE, clazz);
    }

    /**
     * Returns the existing instance from the {@link #DEFAULT_NAMESPACE}, or computes and registers one if absent.
     *
     * @param clazz the class type of the instance; must not be {@code null}
     * @param mappingFunction the function used to compute a new instance if none exists; must not be {@code null}
     * @param <T> the type of the {@link Initializable} instance
     * @return the existing or newly computed instance; never {@code null}
     * @throws NullPointerException when the computed instance is {@code null}
     * @see #computeIfAbsent(String, Class, Function)
     */
    static <T extends Initializable> @NonNull T computeIfAbsent(
            final @NonNull Class<T> clazz,
            final @NonNull Function<Class<?>, T> mappingFunction
    ) throws NullPointerException {
        return InitializableRegistry.computeIfAbsent(DEFAULT_NAMESPACE, clazz, mappingFunction);
    }

    /**
     * Returns whether an instance is registered in the {@link #DEFAULT_NAMESPACE} for the given class type.
     *
     * @param clazz the class type to check; must not be {@code null}
     * @return {@code true} if an instance is registered, {@code false} otherwise
     * @throws NullPointerException when {@code clazz} is {@code null}
     * @see #hasInstance(String, Class)
     */
    static boolean hasInstance(
            final @NonNull Class<?> clazz
    ) throws NullPointerException {
        return InitializableRegistry.hasInstance(DEFAULT_NAMESPACE, clazz);
    }

    /**
     * Registers an instance in the {@link #DEFAULT_NAMESPACE} under the given class type.
     *
     * <p>Triggers {@link #onInstall()} on the instance before it is stored.
     * Throws if an instance of the same class is already registered.</p>
     *
     * @param instance the instance to register; must not be {@code null}
     * @param clazz the class type to associate with the instance; must not be {@code null}
     * @param <T> the type of the {@link Initializable} instance
     * @throws NullPointerException when an instance of {@code clazz} is already registered
     * @see #register(String, Initializable, Class)
     */
    static <T extends Initializable> void register(
            final @NonNull T instance,
            final @NonNull Class<T> clazz
    ) throws NullPointerException {
        InitializableRegistry.register(DEFAULT_NAMESPACE, instance, clazz);
    }

    /**
     * Removes the registered instance from the {@link #DEFAULT_NAMESPACE} for the given class type.
     *
     * <p>Triggers {@link #onUninstall()} on the instance before removal.
     * Throws if no instance of the given class is currently registered.</p>
     *
     * @param clazz the class type of the instance to remove; must not be {@code null}
     * @param <T> the type of the {@link Initializable} instance
     * @throws NullPointerException when no instance of {@code clazz} is registered
     * @see #unregister(String, Class)
     */
    static <T extends Initializable> void unregister(
            final @NonNull Class<T> clazz
    ) throws NullPointerException {
        InitializableRegistry.unregister(DEFAULT_NAMESPACE, clazz);
    }

    /**
     * Called when this instance is registered in the {@link InitializableRegistry}.
     *
     * <p>Override to perform setup logic that should run at install time.
     * The default implementation does nothing.</p>
     */
    default void onInstall() {}

    /**
     * Called when this instance is removed from the {@link InitializableRegistry}.
     *
     * <p>Override to perform teardown or cleanup logic that should run at uninstall time.
     * The default implementation does nothing.</p>
     */
    default void onUninstall() {}
}