package de.leycm.init4j.registry;

import de.leycm.init4j.identifier.Identifier;
import de.leycm.init4j.registries.NoOverwriteRegistry;
import de.leycm.init4j.registries.UnmodifiableRegistry;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Factory interface for creating decorated {@link Registry} views.
 *
 * <p>Provides static utility methods that wrap an existing {@link Registry}
 * with additional behavioral constraints. All returned instances delegate
 * their operations to the provided backing registry.</p>
 *
 * @since 1.0
 * @see UnmodifiableRegistry
 * @see NoOverwriteRegistry
 * @author Lennard <a href="mailto:leycm@proton.me">leycm@proton.me</a>
 */
public interface Registries {

    /**
     * Wraps the given {@link Registry} in an unmodifiable view.
     *
     * <p>All mutating operations on the returned registry will throw
     * {@link UnsupportedOperationException}. Read operations are
     * forwarded to the backing registry unchanged.</p>
     *
     * @param <T>      the type of values stored in the registry
     * @param registry the registry to wrap; must not be {@code null}
     * @return an unmodifiable view of the given registry; never {@code null}
     */
    @Contract(value = "_ -> new", pure = true)
    static <T> @NotNull Registry<T> unmodifiableRegistry(final @NonNull Registry<T> registry) {
        return new UnmodifiableRegistry<>(registry);
    }

    /**
     * Wraps the given {@link Registry} in a no-overwrite view.
     *
     * <p>Attempting to register a value under an already occupied {@link Identifier}
     * will throw {@link IllegalStateException}. All other operations are
     * forwarded to the backing registry unchanged.</p>
     *
     * @param <T>      the type of values stored in the registry
     * @param registry the registry to wrap; must not be {@code null}
     * @return a no-overwrite view of the given registry; never {@code null}
     */
    @Contract(value = "_ -> new", pure = true)
    static <T> @NotNull Registry<T> noOverwriteRegistry(final @NonNull Registry<T> registry) {
        return new NoOverwriteRegistry<>(registry);
    }
}