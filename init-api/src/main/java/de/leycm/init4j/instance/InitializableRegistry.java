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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class InitializableRegistry {

    // namespace -> (class -> instance)
    private static final Map<String, Map<Class<?>, Initializable>> REGISTRY = new ConcurrentHashMap<>();

    private static Map<Class<?>, Initializable> getNamespace(final @NonNull String namespace) {
        return REGISTRY.computeIfAbsent(namespace, ns -> new ConcurrentHashMap<>());
    }

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

    protected static boolean hasInstance(
            final @NonNull String namespace,
            final @NonNull Class<?> clazz
    ) throws NullPointerException {
        return getNamespace(namespace).containsKey(clazz);
    }

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