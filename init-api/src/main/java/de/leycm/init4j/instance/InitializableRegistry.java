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

    private static final Map<Class<?>, Initializable> REGISTRY = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked") // is checked by clazz.isInstance(instance) before
    protected static <T extends Initializable> @NonNull T getInstance(final @NonNull Class<T> clazz)
            throws NullPointerException, ClassCastException {
        Initializable instance = REGISTRY.get(clazz);

        if (instance == null)
            throw new NullPointerException("No instance registered for " + clazz.getSimpleName());

        if (!clazz.isInstance(instance))
            throw new ClassCastException("Registered instance is not of type " + clazz.getSimpleName());

        return (T) instance;
    }

    @SuppressWarnings("unchecked") // is checked by clazz.isInstance(instance) before
    protected static <T extends Initializable> @NonNull T computeIfAbsent(final @NonNull Class<T> clazz,
                                                                          final @NonNull Function<Class<?>, T> mappingFunction
    ) throws NullPointerException, ClassCastException {
        Initializable instance = REGISTRY.computeIfAbsent(clazz, mappingFunction);

        if (!clazz.isInstance(instance))
            throw new ClassCastException("Registered instance is not of type " + clazz.getSimpleName());

        return (T) instance;
    }

    protected static boolean hasInstance(final @NonNull Class<?> clazz)
            throws NullPointerException {
        return REGISTRY.containsKey(clazz);
    }

    protected static <T extends Initializable> void register(final @NonNull T instance,
                                                             final @NonNull Class<T> clazz
    ) throws NullPointerException {

        if (REGISTRY.containsKey(clazz))
            throw new NullPointerException("An instance of " + clazz.getSimpleName() + " is already registered");

        instance.onInstall();
        REGISTRY.put(clazz, instance);
    }

    protected static <T extends Initializable> void unregister(final @NonNull Class<T> clazz)
            throws NullPointerException {

        if (!REGISTRY.containsKey(clazz))
            throw new NullPointerException("There is no instance of " + clazz.getSimpleName());

        REGISTRY.get(clazz).onUninstall();
        REGISTRY.remove(clazz);
    }
}