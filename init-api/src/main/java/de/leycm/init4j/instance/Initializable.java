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

    static <T extends Initializable> @NonNull T getInstance(final @NonNull Class<T> clazz)
            throws NullPointerException {
        return InitializableRegistry.getInstance(clazz);
    }

    static <T extends Initializable> @NonNull T computeIfAbsent(final @NonNull Class<T> clazz,
                                                                final @NonNull Function<Class<?>, T> mappingFunction
    ) throws NullPointerException {

        return InitializableRegistry.computeIfAbsent(clazz, mappingFunction);
    }

    static boolean hasInstance(final @NonNull Class<?> clazz
    ) throws NullPointerException {

        return InitializableRegistry.hasInstance(clazz);
    }

    static <T extends Initializable> void register(final @NonNull T instance,
                                                   final @NonNull Class<T> clazz
    ) throws NullPointerException {

        InitializableRegistry.register(instance, clazz);
    }

    static <T extends Initializable> void unregister(final @NonNull Class<T> clazz
    ) throws NullPointerException {

        InitializableRegistry.unregister(clazz);
    }

    default void onInstall() {}

    default void onUninstall() {}
}
