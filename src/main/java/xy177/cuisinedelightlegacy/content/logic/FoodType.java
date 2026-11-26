package xy177.cuisinedelightlegacy.content.logic;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.util.Locale;

public enum FoodType {
    NONE(TextFormatting.GRAY), MEAT(TextFormatting.LIGHT_PURPLE), VEG(TextFormatting.GREEN),
    CARB(TextFormatting.YELLOW), SEAFOOD(TextFormatting.AQUA);

    public final TextFormatting format;

    FoodType(TextFormatting format) {
        this.format = format;
    }

    public ITextComponent get() {
        return new TextComponentTranslation("cuisinedelightlegacy.food_type." + name().toLowerCase(Locale.ROOT));
    }
}
