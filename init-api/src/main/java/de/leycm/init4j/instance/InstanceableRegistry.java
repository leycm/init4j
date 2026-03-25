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

import de.leycm.init4j.identifier.Identifier;
import de.leycm.init4j.registries.NoOverwriteRegistry;
import de.leycm.init4j.registry.Registries;
import de.leycm.init4j.registries.ConcurrentHashRegistry;
import de.leycm.init4j.registry.Registry;
import lombok.NonNull;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A registry for {@link Instanceable} instances, organized by namespace and class.
 *
 * <p>This class provides methods to register, retrieve, and manage instances of
 * {@link Instanceable} implementations. Each instance is associated with a specific
 * namespace and class type, allowing for flexible and modular management of
 * instanceable components.</p>
 *
 * <p>Namespaces group related instances together, preventing naming conflicts and
 * allowing multiple implementations of the same type within different contexts.</p>
 *
 * <p>Thread Safety: This class is thread-safe. All registry operations are backed
 * by {@link NoOverwriteRegistry}, which uses a {@link ConcurrentHashMap}
 * internally, ensuring safe concurrent access without external synchronization.</p>
 *
 * @since 1.0.0
 * @see Instanceable
 * @author Lennard <a href="mailto:leycm@proton.me">leycm@proton.me</a>
 */
public class InstanceableRegistry {

    /**
     * Private constructor to prevent instantiation.
     *
     * <p>This class is designed to be used statically and should not be instantiated.
     * Attempting to create an instance will result in an {@link UnsupportedOperationException}.</p>
     * @throws UnsupportedOperationException always
     */
    private InstanceableRegistry() {
        throw new UnsupportedOperationException("InstanceableRegistry is a static only class and cannot be instantiated");
    }

    @ApiStatus.Internal
    private static final Registry<Instanceable> REGISTRY = Registries.noOverwriteRegistry(new ConcurrentHashRegistry<>());

    // ==== Helper ============================================================

    /**
     * Builds a composite {@link Identifier} from a namespace string and a class type.
     *
     * <p>The namespace is sanitized via {@link Identifier#sanitizeNamespace(String)} and
     * the class simple name is used as the key, also sanitized via
     * {@link Identifier#sanitizeKey(String)}.</p>
     *
     * @param namespace the target namespace; must not be {@code null}
     * @param clazz the class type; must not be {@code null}
     * @return a well-formed {@link Identifier}; never {@code null}
     */
    @ApiStatus.Internal
    private static @NonNull Identifier toIdentifier(
            final @NonNull String namespace,
            final @NonNull Class<?> clazz
    ) {
        return Identifier.of(
                Identifier.sanitizeNamespace(namespace),
                Identifier.sanitizeKey(clazz.getName())
        );
    }

    // ==== Registry ==========================================================

    /**
     * Retrieves a registered instance by namespace and class type.
     *
     * <p>The {@code @SuppressWarnings("unchecked")} is safe because
     * {@link Class#isInstance(Object)} is checked prior to the cast.</p>
     *
     * @param namespace the target namespace; must not be {@code null}
     * @param clazz the class type of the instance to retrieve; must not be {@code null}
     * @param <T> the type of the {@link Instanceable} instance
     * @return the registered instance; never {@code null}
     * @throws NullPointerException when {@code namespace} or {@code clazz} is {@code null},
     *                              or when no instance is registered for {@code clazz} in the namespace
     * @throws ClassCastException when the registered instance is not assignment-compatible with {@code clazz}
     */
    @ApiStatus.Internal
    @SuppressWarnings("unchecked") // is checked by clazz.isInstance(instance) before
    protected static <T extends Instanceable> @NonNull T getInstance(
            final @NonNull String namespace,
            final @NonNull Class<T> clazz
    ) throws NullPointerException, ClassCastException {
        Identifier id = toIdentifier(namespace, clazz);
        Instanceable instance = REGISTRY.get(id);

        if (!clazz.isInstance(instance))
            throw new ClassCastException(
                    "Registered instance is not of type " + clazz.getSimpleName());

        return (T) instance;
    }

    /**
     * Returns the existing instance for the given namespace and class type, or computes and registers one if absent.
     *
     * <p>The {@code @SuppressWarnings("unchecked")} is safe because {@link Class#isInstance(Object)}
     * is checked prior to the cast.</p>
     *
     * @param namespace the target namespace; must not be {@code null}
     * @param clazz the class type of the instance; must not be {@code null}
     * @param mappingFunction the function used to compute a new instance if none exists; must not be {@code null}
     * @param <T> the type of the {@link Instanceable} instance
     * @return the existing or newly computed instance; never {@code null}
     * @throws NullPointerException when {@code namespace}, {@code clazz}, or {@code mappingFunction} is {@code null},
     *                              or when the mapping function returns {@code null}
     * @throws ClassCastException when the resulting instance is not assignment-compatible with {@code clazz}
     */
    @ApiStatus.Internal
    @SuppressWarnings("unchecked") // is checked by clazz.isInstance(instance) before
    protected static <T extends Instanceable> @NonNull T computeIfAbsent(
            final @NonNull String namespace,
            final @NonNull Class<T> clazz,
            final @NonNull Function<Class<?>, T> mappingFunction
    ) throws NullPointerException, ClassCastException {
        Identifier id = toIdentifier(namespace, clazz);
        Instanceable instance = REGISTRY.computeIfAbsent(id, ignored -> mappingFunction.apply(clazz));

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
        return REGISTRY.has(toIdentifier(namespace, clazz));
    }

    /**
     * Registers an instance under the given namespace and class type.
     *
     * <p>Invokes {@link Instanceable#onInstall()} on the instance before storing it.
     * Throws if an instance of the same class type is already registered in the namespace.</p>
     *
     * @param namespace the target namespace; must not be {@code null}
     * @param instance the instance to register; must not be {@code null}
     * @param clazz the class type to associate with the instance; must not be {@code null}
     * @param <T> the type of the {@link Instanceable} instance
     * @throws IllegalStateException when an instance of {@code clazz} is already registered in the namespace
     * @throws NullPointerException when {@code namespace}, {@code instance}, or {@code clazz} is {@code null}
     */
    @ApiStatus.Internal
    protected static <T extends Instanceable> void register(
            final @NonNull String namespace,
            final @NonNull Class<T> clazz,
            final @NonNull T instance
    ) throws NullPointerException, IllegalStateException {
        instance.onInstall();
        REGISTRY.register(toIdentifier(namespace, clazz), instance);
    }

    /**
     * Removes the registered instance for the given namespace and class type.
     *
     * <p>Invokes {@link Instanceable#onUninstall()} on the instance before removing it.
     * Throws if no instance of the given class type is currently registered in the namespace.</p>
     *
     * @param namespace the target namespace; must not be {@code null}
     * @param clazz the class type of the instance to remove; must not be {@code null}
     * @param <T> the type of the {@link Instanceable} instance
     * @throws IllegalStateException when no instance of {@code clazz} is registered in the namespace
     * @throws NullPointerException when {@code namespace} or {@code clazz} is {@code null}
     */
    @ApiStatus.Internal
    protected static <T extends Instanceable> void unregister(
            final @NonNull String namespace,
            final @NonNull Class<T> clazz
    ) throws NullPointerException, IllegalStateException {
        Identifier id = toIdentifier(namespace, clazz);
        REGISTRY.unregister(id).onUninstall();
    }
}