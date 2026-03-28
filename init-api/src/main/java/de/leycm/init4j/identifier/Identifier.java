package de.leycm.init4j.identifier;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Immutable value type representing a namespaced identifier.
 *
 * <p>An {@link Identifier} consists of a {@code namespace} and a {@code key},
 * separated by a colon (e.g. {@code "my.namespace:my-key"}). Both parts are
 * validated against a strict character allowlist on construction, ensuring
 * that every instance is well-formed and safe for use as a map key, registry
 * entry, or persistent reference.</p>
 *
 * <p>Instances are created exclusively through the static {@link #of} factory
 * methods, which accept {@link String}, {@link Class}, {@link UUID}, or arbitrary
 * {@link Object} values as the key. The constructor is private; validation
 * runs eagerly and throws {@link IllegalArgumentException} on invalid input.
 * {@link #toString()}, {@link #hashCode()}, and {@link #equals(Object)} are
 * all derived from the combined {@code namespace:key} string and cached at
 * construction time for performance.</p>
 *
 * <p>Thread Safety: This class is unconditionally thread-safe. All fields are
 * {@code final} and the instance state is fully initialized before the
 * constructor returns.</p>
 *
 * @since 1.0.0
 * @see Keyable
 * @author Lennard <a href="mailto:leycm@proton.me">leycm@proton.me</a>
 */
public class Identifier {

    @SuppressWarnings("RegExpRedundantEscape") // cause: needed for regex even if it doesn't need it for Java string
    private static final String KEY_CHARS = "a-zA-Z0-9._\\-/*+#,;";
    private static final String NAMESPACE_CHARS = "a-z0-9._-";

    private static final Pattern NAMESPACE_PATTERN =
            Pattern.compile("^[" + NAMESPACE_CHARS + "]+$");

    private static final Pattern KEY_PATTERN =
            Pattern.compile("^[" + KEY_CHARS + "]+$");

    private static final Pattern NAMESPACE_SANITIZE =
            Pattern.compile("[^" + NAMESPACE_CHARS + "]");

    private static final Pattern KEY_SANITIZE =
            Pattern.compile("[^" + KEY_CHARS + "]");

    private final @NonNull String namespace;
    private final @NonNull String key;

    private final @NonNull String toStringCache;
    private final int hashCodeCache;

    // ==== Helper ============================================================

    /**
     * Validates that the given namespace is non-blank and contains only allowed characters.
     *
     * <p>Valid namespaces match the pattern {@code [a-z0-9._-]+}.
     * Use {@link #sanitizeNamespace(String)} to produce a valid namespace
     * from arbitrary input instead of validating manually.</p>
     *
     * @param namespace the namespace to validate; must not be {@code null}
     * @throws IllegalArgumentException when the namespace is blank or contains invalid characters
     * @throws NullPointerException when {@code namespace} is {@code null}
     */
    public static void validateNamespace(final @NonNull String namespace) {
        if (namespace.isBlank()) {
            throw new IllegalArgumentException("namespace is blank");
        }
        if (!NAMESPACE_PATTERN.matcher(namespace).matches()) {
            throw new IllegalArgumentException("namespace contains invalid characters: " + namespace);
        }
    }

    /**
     * Validates that the given key is non-blank and contains only allowed characters.
     *
     * <p>Valid keys match the pattern {@code [a-zA-Z0-9._\-/*+#,;]+}.
     * Use {@link #sanitizeKey(String)} to produce a valid key
     * from arbitrary input instead of validating manually.</p>
     *
     * @param key the key to validate; must not be {@code null}
     * @throws IllegalArgumentException when the key is blank or contains invalid characters
     * @throws NullPointerException when {@code key} is {@code null}
     */
    public static void validateKey(final @NonNull String key) {
        if (key.isBlank()) {
            throw new IllegalArgumentException("key can not be blank");
        }
        if (!KEY_PATTERN.matcher(key).matches()) {
            throw new IllegalArgumentException("key contains invalid characters: " + key);
        }
    }

    /**
     * Returns {@code true} if the given namespace is valid.
     *
     * <p>Equivalent to calling {@link #validateNamespace(String)} and catching
     * any exception. Returns {@code false} for {@code null} input.</p>
     *
     * @param namespace the namespace to check; never {@code null}
     * @return {@code true} if the namespace is valid, {@code false} otherwise
     */
    public static boolean isValidNamespace(final @Nullable String namespace) {
        if (namespace == null) return false;
        try {
            validateNamespace(namespace);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

    /**
     * Returns {@code true} if the given key is valid.
     *
     * <p>Equivalent to calling {@link #validateKey(String)} and catching
     * any exception. Returns {@code false} for {@code null} input.</p>
     *
     * @param key the key to check; never {@code null}
     * @return {@code true} if the key is valid, {@code false} otherwise
     */
    public static boolean isValidKey(final @Nullable String key) {
        if (key == null) return false;
        try {
            validateKey(key);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

    /**
     * Returns a sanitized copy of the given namespace string.
     *
     * <p>The input is lowercased and any character not in {@code [a-z0-9._-]}
     * is replaced with {@code _}. The result is not guaranteed to be valid if
     * the input is blank or becomes blank after sanitization.</p>
     *
     * @param namespace the namespace string to sanitize; must not be {@code null}
     * @return the sanitized namespace; never {@code null}
     * @throws NullPointerException when {@code namespace} is {@code null}
     */
    public static @NonNull String sanitizeNamespace(final @NonNull String namespace) {
        return NAMESPACE_SANITIZE.matcher(namespace.toLowerCase()).replaceAll("_");
    }

    /**
     * Returns a sanitized copy of the given key string.
     *
     * <p>Any character not in {@code [a-zA-Z0-9._\-/*+#,;]} is replaced
     * with {@code _}. The result is not guaranteed to be valid if the input
     * is blank or becomes blank after sanitization.</p>
     *
     * @param key the key string to sanitize; must not be {@code null}
     * @return the sanitized key; never {@code null}
     * @throws NullPointerException when {@code key} is {@code null}
     */
    public static @NonNull String sanitizeKey(final @NonNull String key) {
        return KEY_SANITIZE.matcher(key).replaceAll("_");
    }

    // ==== Constructor =======================================================

    /**
     * Creates a new {@link Identifier} using the string representation of the given class as the key.
     *
     * @param namespace the namespace; must not be {@code null} or blank
     * @param clazz the class whose {@link Class#toString()} value is used as the key; must not be {@code null}
     * @return a new identifier; never {@code null}
     * @throws IllegalArgumentException when the namespace is blank or the derived key contains invalid characters
     * @throws NullPointerException when {@code namespace} or {@code clazz} is {@code null}
     */
    public static @NonNull Identifier of(final @NonNull String namespace,
                                         final @NonNull Class<?> clazz) {
        return new Identifier(namespace, clazz.getName());
    }

    /**
     * Creates a new {@link Identifier} using the string representation of the given {@link UUID} as the key.
     *
     * @param namespace the namespace; must not be {@code null} or blank
     * @param uuid the UUID to use as the key; must not be {@code null}
     * @return a new identifier; never {@code null}
     * @throws IllegalArgumentException when the namespace is blank or contains invalid characters
     * @throws NullPointerException when {@code namespace} or {@code uuid} is {@code null}
     */
    public static @NonNull Identifier of(final @NonNull String namespace,
                                         final @NonNull UUID uuid) {
        return new Identifier(namespace, uuid.toString());
    }

    /**
     * Creates a new {@link Identifier} from the given namespace and key strings.
     *
     * @param namespace the namespace; must not be {@code null} or blank
     * @param key the key; must not be {@code null} or blank
     * @return a new identifier; never {@code null}
     * @throws IllegalArgumentException when the namespace or key is blank or contains invalid characters
     * @throws NullPointerException when {@code namespace} or {@code key} is {@code null}
     */
    public static @NonNull Identifier of(final @NonNull String namespace,
                                         final @NonNull String key) {
        return new Identifier(namespace, key);
    }

    /**
     * Creates a new {@link Identifier} using {@link Keyable#toKey()} on the given object as the key.
     *
     * @param namespace the namespace; must not be {@code null} or blank
     * @param keyable the Keyable instance to resolve a Key from; must not be {@code null}
     * @return a new identifier; never {@code null}
     * @throws IllegalArgumentException when the namespace is blank or the derived key contains invalid characters
     * @throws NullPointerException when {@code namespace} or {@code keyable} is {@code null}
     */
    public static @NonNull Identifier of(final @NonNull String namespace,
                                         final @NonNull Keyable keyable) {
        return new Identifier(namespace, keyable.toKey());
    }

    /**
     * Creates a new {@link Identifier} using {@link String#valueOf(Object)} on the given object as the key.
     *
     * @param namespace the namespace; must not be {@code null} or blank
     * @param o the object whose string value is used as the key; must not be {@code null}
     * @return a new identifier; never {@code null}
     * @throws IllegalArgumentException when the namespace is blank or the derived key contains invalid characters
     * @throws NullPointerException when {@code namespace} or {@code o} is {@code null}
     */
    public static @NonNull Identifier of(final @NonNull String namespace,
                                         final @NonNull Object o) {
        return new Identifier(namespace, String.valueOf(o));
    }

    private Identifier(final @NonNull String namespace, final @NonNull String key) {
        validateKey(key);
        validateNamespace(namespace);
        this.namespace = namespace;
        this.key = key;

        this.toStringCache = namespace + ":" + key;
        this.hashCodeCache = toStringCache.hashCode();
    }

    // ==== Body Methods ======================================================

    /**
     * Returns the namespace component of this identifier.
     *
     * @return the namespace; never {@code null}
     */
    public @NonNull String namespace() {
        return namespace;
    }

    /**
     * Returns the key component of this identifier.
     *
     * @return the key; never {@code null}
     */
    public @NonNull String key() {
        return key;
    }

    /**
     * Returns the canonical string form of this identifier as {@code "namespace:key"}.
     *
     * @return the string representation; never {@code null}
     */
    @Override
    public @NonNull String toString() {
        return toStringCache;
    }

    /**
     * Returns the hash code of this identifier, derived from its canonical string form.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return hashCodeCache;
    }

    /**
     * Returns {@code true} if the given object is an {@link Identifier} with the same
     * namespace and key as this instance.
     *
     * @param obj the object to compare; never {@code null}
     * @return {@code true} if equal, {@code false} otherwise
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Identifier) obj;
        return toStringCache.equals(that.toStringCache);
    }

}
