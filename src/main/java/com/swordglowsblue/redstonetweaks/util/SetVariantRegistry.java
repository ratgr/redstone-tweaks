package com.swordglowsblue.redstonetweaks.util;

import java.util.Set;

public class SetVariantRegistry<T> extends AbstractVariantRegistry<T> {
    protected SetVariantRegistry(Set<T> keySet, IdFormatter idFormatter) { super(keySet, idFormatter); }

    public static <T, B extends Builder<T, B, SetVariantRegistry<T>>>
        IBuilderAwaitingIDInfo<T, B, SetVariantRegistry<T>> create(Set<T> keySet) {
        return new Builder<>(SetVariantRegistry::new, keySet);
    }
}
