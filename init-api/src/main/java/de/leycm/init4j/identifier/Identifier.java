package de.leycm.init4j.identifier;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.regex.Pattern;

public final class Identifier {

    @SuppressWarnings("RegExpRedundantEscape") // needed for regex even if it doesn't need it for Java string
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


    public static void validateNamespace(@NonNull String namespace) {
        if (namespace.isBlank()) {
            throw new IllegalArgumentException("namespace is blank");
        }
        if (!NAMESPACE_PATTERN.matcher(namespace).matches()) {
            throw new IllegalArgumentException("namespace contains invalid characters: " + namespace);
        }
    }

    public static void validateKey(@NonNull String key) {
        if (key.isBlank()) {
            throw new IllegalArgumentException("key can not be blank");
        }
        if (!KEY_PATTERN.matcher(key).matches()) {
            throw new IllegalArgumentException("key contains invalid characters: " + key);
        }
    }

    public static boolean isValidNamespace(@Nullable String namespace) {
        if (namespace == null) return false;
        try {
            validateNamespace(namespace);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

    public static boolean isValidKey(@Nullable String key) {
        if (key == null) return false;
        try {
            validateKey(key);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

    public static @NonNull String sanitizeNamespace(@NonNull String namespace) {
        return NAMESPACE_SANITIZE.matcher(namespace.toLowerCase()).replaceAll("_");
    }

    public static @NonNull String sanitizeKey(@NonNull String key) {
        return KEY_SANITIZE.matcher(key).replaceAll("_");
    }

    public static @NonNull Identifier of(@NonNull String namespace, @NonNull String key) {
        return new Identifier(namespace, key);
    }

    public static @NonNull Identifier of(@NonNull String namespace, @NonNull Class<?> key) {
        return new Identifier(namespace, key.toString());
    }

    public static @NonNull Identifier of(@NonNull String namespace, @NonNull UUID key) {
        return new Identifier(namespace, key.toString());
    }

    public static @NonNull Identifier of(@NonNull String namespace, @NonNull Object key) {
        return new Identifier(namespace, String.valueOf(key));
    }

    private Identifier(@NonNull String namespace, @NonNull String key) {
        validateKey(key);
        validateNamespace(namespace);
        this.namespace = namespace;
        this.key = key;

        this.toStringCache = namespace + ":" + key;
        this.hashCodeCache = toStringCache.hashCode();
    }

    public @NonNull String namespace() {
        return namespace;
    }

    public @NonNull String key() {
        return key;
    }

    @Override
    public @NonNull String toString() {
        return toStringCache;
    }

    @Override
    public int hashCode() {
        return hashCodeCache;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Identifier) obj;
        return toStringCache.equals(that.toStringCache);
    }

}
