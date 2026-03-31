package de.leycm.init4j.registry;

import de.leycm.init4j.identifier.Identifiable;
import de.leycm.init4j.identifier.Identifier;
import lombok.NonNull;

import java.util.Map;

/**
 * Represents a key-value pair in a {@link de.leycm.init4j.registry.Registry} where the key
 * is an {@link Identifier} and the value is of type {@code T}.
 *
 * <p>This interface provides access to the identifier and value independently of the underlying
 * registry storage. Implementations such as {@link RegistryPair.Simple} are immutable copies,
 * meaning that modifying the returned {@code RegistryPair} does <em>not</em> affect the original registry.</p>
 *
 * <p>Pairs can optionally validate themselves if the value implements {@link Identifiable},
 * ensuring that the identifier matches the value's internal identity.</p>
 *
 * @param <T> the type of the value in this pair
 * @since 1.0
 * @author Lennard
 * <a href="mailto:leycm@proton.me">leycm@proton.me</a>
 */
public interface RegistryPair<T> {

    /**
     * Returns the identifier associated with this pair.
     *
     * @return the non-null identifier
     */
    @NonNull Identifier getIdentifier();

    /**
     * Returns the value associated with this pair.
     *
     * @return the non-null value
     */
    @NonNull T getValue();

    /**
     * Checks whether this pair is valid.
     *
     * <p>If the value implements {@link Identifiable}, the identifier is validated
     * against the value's internal identity.</p>
     *
     * @return {@code true} if valid, {@code false} otherwise
     */
    default boolean isValid() {
        if (!(getValue() instanceof Identifiable identifiable)) return true;
        return identifiable.identify(getIdentifier());
    }

    /**
     * Validates this pair and throws an exception if it is invalid.
     *
     * <p>If the value implements {@link Identifiable} and its internal identifier
     * does not match this pair's identifier, an {@link IllegalStateException} is thrown.</p>
     *
     * @throws IllegalStateException if the pair is invalid
     */
    default void validate() {
        if (!(getValue() instanceof Identifiable identifiable)) return;
        if (identifiable.identify(getIdentifier())) return;

        throw new IllegalStateException("Value's identifier does not match the registry key");
    }

    /**
     * A simple immutable implementation of {@link RegistryPair}.
     *
     * <p>Constructs a pair from an identifier and a value, or from an existing
     * {@link Map.Entry} without keeping a reference to the map.</p>
     *
     * @param <T> the type of the value in this pair
     */
    record Simple<T>(
            @NonNull Identifier id,
            @NonNull T value
    ) implements RegistryPair<T> {

        /**
         * Constructs a new simple pair with the given identifier and value.
         *
         * @param id    the identifier; must not be {@code null}
         * @param value the value; must not be {@code null}
         */
        public Simple {

        }

        /**
         * Constructs a new simple pair from a {@link Map.Entry}.
         *
         * @param entry the entry to copy; must not be {@code null}
         */
        public Simple(final @NonNull Map.Entry<Identifier, T> entry) {
            this(entry.getKey(), entry.getValue());
        }

        /** {@inheritDoc} */
        @Override
        public @NonNull Identifier getIdentifier() {
            return id;
        }

        /** {@inheritDoc} */
        @Override
        public @NonNull T getValue() {
            return value;
        }
    }

}