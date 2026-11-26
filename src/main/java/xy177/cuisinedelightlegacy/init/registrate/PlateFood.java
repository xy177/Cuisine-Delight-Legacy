package xy177.cuisinedelightlegacy.init.registrate;

import xy177.cuisinedelightlegacy.content.item.BaseFoodItem;

import java.util.Locale;

public enum PlateFood {
    SUSPICIOUS_MIX, HAM_FRIED_RICE,
    FRIED_RICE, MIXED_FRIED_RICE,
    MEAT_WITH_SEAFOOD, MEAT_WITH_VEGETABLES, SEAFOOD_WITH_VEGETABLES,
    FRIED_PASTA, MIXED_PASTA,
    MEAT_FRIED_RICE, MEAT_PASTA, MEAT_PLATTER,
    SEAFOOD_FRIED_RICE, SEAFOOD_PASTA, SEAFOOD_PLATTER,
    VEGETABLE_FRIED_RICE, VEGETABLE_PASTA, VEGETABLE_PLATTER,
    FRIED_MUSHROOM, FRIED_MEAT_AND_MELON, SCRAMBLED_EGG_AND_TOMATO;

    public final BaseFoodItem item = new BaseFoodItem();

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }
}
