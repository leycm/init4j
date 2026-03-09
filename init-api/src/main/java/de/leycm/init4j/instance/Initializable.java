/*
 * This file is part of the linguae Library.
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

public interface Initializable {

    String DEFAULT_NAMESPACE = "de.leycm.init4j";

    static <T extends Initializable> @NonNull T getInstance(
            final @NonNull String namespace,
            final @NonNull Class<T> clazz
    ) throws NullPointerException {
        return InitializableRegistry.getInstance(namespace, clazz);
    }

    static <T extends Initializable> @NonNull T computeIfAbsent(
            final @NonNull String namespace,
            final @NonNull Class<T> clazz,
            final @NonNull Function<Class<?>, T> mappingFunction
    ) throws NullPointerException {
        return InitializableRegistry.computeIfAbsent(namespace, clazz, mappingFunction);
    }

    static boolean hasInstance(
            final @NonNull String namespace,
            final @NonNull Class<?> clazz
    ) throws NullPointerException {
        return InitializableRegistry.hasInstance(namespace, clazz);
    }

    static <T extends Initializable> void register(
            final @NonNull String namespace,
            final @NonNull T instance,
            final @NonNull Class<T> clazz
    ) throws NullPointerException {
        InitializableRegistry.register(namespace, instance, clazz);
    }

    static <T extends Initializable> void unregister(
            final @NonNull String namespace,
            final @NonNull Class<T> clazz
    ) throws NullPointerException {
        InitializableRegistry.unregister(namespace, clazz);
    }

    static <T extends Initializable> @NonNull T getInstance(
            final @NonNull Class<T> clazz
    ) throws NullPointerException {
        return InitializableRegistry.getInstance(DEFAULT_NAMESPACE, clazz);
    }

    static <T extends Initializable> @NonNull T computeIfAbsent(
            final @NonNull Class<T> clazz,
            final @NonNull Function<Class<?>, T> mappingFunction
    ) throws NullPointerException {
        return InitializableRegistry.computeIfAbsent(DEFAULT_NAMESPACE, clazz, mappingFunction);
    }

    static boolean hasInstance(
            final @NonNull Class<?> clazz
    ) throws NullPointerException {
        return InitializableRegistry.hasInstance(DEFAULT_NAMESPACE, clazz);
    }

    static <T extends Initializable> void register(
            final @NonNull T instance,
            final @NonNull Class<T> clazz
    ) throws NullPointerException {
        InitializableRegistry.register(DEFAULT_NAMESPACE, instance, clazz);
    }

    static <T extends Initializable> void unregister(
            final @NonNull Class<T> clazz
    ) throws NullPointerException {
        InitializableRegistry.unregister(DEFAULT_NAMESPACE, clazz);
    }

    default void onInstall() {}

    default void onUninstall() {}
}