package de.leycm.init4j.identifier;

import lombok.NonNull;

/**
 * Represents an object that carries a unique {@link Identifier}.
 *
 * <p>Implementing this interface signals that an object has a stable,
 * namespaced identity that can be used for lookup, comparison, or
 * registration purposes. The identity is exposed via {@link #identifier()}
 * and must remain consistent throughout the object's lifecycle.</p>
 *
 * <p>Two default utility methods are provided to simplify identity
 * comparisons against both raw {@link Identifier} instances and other
 * {@link Identifiable} objects, delegating equality to
 * {@link Identifier#equals(Object)}.</p>
 *
 * @since 1.0.0
 * @see Identifier
 * @author Lennard <a href="mailto:leycm@proton.me">leycm@proton.me</a>
 */
public interface Identifiable {

    /**
     * Returns the unique identifier of this object.
     *
     * @return the identifier; never {@code null}
     */
    @NonNull Identifier getIdentifier();

    /**
     * Returns {@code true} if this object's identifier equals the given identifier.
     *
     * @param identifier the identifier to compare against; must not be {@code null}
     * @return {@code true} if the identifiers are equal, {@code false} otherwise
     * @throws NullPointerException when {@code identifier} is {@code null}
     */
    default boolean identify(final @NonNull Identifier identifier) {
        return this.getIdentifier().equals(identifier);
    }

    /**
     * Returns {@code true} if this object's identifier equals the identifier
     * of the given {@link Identifiable}.
     *
     * @param identifiable the identifiable to compare against; must not be {@code null}
     * @return {@code true} if the identifiers are equal, {@code false} otherwise
     * @throws NullPointerException when {@code identifiable} is {@code null}
     */
    default boolean identify(final @NonNull Identifiable identifiable) {
        return this.getIdentifier().equals(identifiable.getIdentifier());
    }

}
