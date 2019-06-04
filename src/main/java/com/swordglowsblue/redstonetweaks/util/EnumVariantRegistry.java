package com.swordglowsblue.redstonetweaks.util;

import java.util.EnumSet;
import java.util.Set;

public class EnumVariantRegistry<T extends Enum<T>> extends AbstractVariantRegistry<T> {
    protected EnumVariantRegistry(Set<T> keySet, IdFormatter idFormatter) { super(keySet, idFormatter); }

    public static <T extends Enum<T>, B extends Builder<T, B, EnumVariantRegistry<T>>>
        IBuilderAwaitingKeySet<T, B, EnumVariantRegistry<T>> create(Class<T> enumClass) {
        return new Builder<>(EnumVariantRegistry<T>::new, EnumSet.allOf(enumClass));
    }
}
