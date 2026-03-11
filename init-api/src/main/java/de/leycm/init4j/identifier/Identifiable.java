package de.leycm.init4j.identifier;

import lombok.NonNull;

public interface Identifiable {

    @NonNull Identifier identifier();

    default boolean identify(@NonNull Identifier identifier) {
        return this.identifier().equals(identifier);
    }

    default boolean identify(@NonNull Identifiable identifiable) {
        return this.identifier().equals(identifiable.identifier());
    }

}
