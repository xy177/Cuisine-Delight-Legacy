package xy177.cuisinedelightlegacy.init.data;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import static xy177.cuisinedelightlegacy.CuisineDelightLegacy.MOD_ID;

public enum LangData {
    SERVE_SIZE("tooltip.size", TextFormatting.GRAY),
    SCORE("tooltip.score", TextFormatting.AQUA),
    BAD_FOOD("tooltip.bad", TextFormatting.RED),
    CREATIVE("tooltip.creative", TextFormatting.GRAY),
    SHIFT("tooltip.shift", TextFormatting.GRAY),
    BAD_BURNT("tooltip.burnt", TextFormatting.DARK_RED),
    BAD_RAW("tooltip.raw", TextFormatting.DARK_RED),
    BAD_OVERCOOKED("tooltip.overcooked", TextFormatting.DARK_RED),
    GOOD("tooltip.good", TextFormatting.DARK_GREEN),
    INFO_MIN_TIME("info.min_time", TextFormatting.AQUA),
    INFO_MAX_TIME("info.max_time", TextFormatting.AQUA),
    INFO_STIR_TIME("info.stir_time", TextFormatting.AQUA),
    INFO_SIZE("info.size", TextFormatting.GREEN),
    INFO_NUTRITION("info.nutrition", TextFormatting.GREEN),
    INFO_RAW_PENALTY("info.raw_penalty", TextFormatting.RED),
    INFO_OVERCOOK_PENALTY("info.overcook_penalty", TextFormatting.RED),
    INFO_DISPLAY("info.display", TextFormatting.AQUA),
    MSG_NO_HEAT("msg.no_heat", TextFormatting.RED),
    MSG_FULL("msg.full", TextFormatting.RED),
    MSG_NOT_INGREDIENT("msg.not_ingredient", TextFormatting.RED),
    MSG_PLACE_HELP("msg.place_help", TextFormatting.GRAY),
    ENCH_SHIFT("ench.shift", TextFormatting.GRAY),
    ENCH_SILK("ench.silk", TextFormatting.GRAY),
    ENCH_FIRE("ench.fire", TextFormatting.GRAY),
    ENCH_EFFICIENCY("ench.efficiency", TextFormatting.GRAY),
    JEI_TITLE("jei.title", TextFormatting.RESET),
    JEI_FOOD_TYPE("jei.food_type", TextFormatting.GREEN),
    JEI_INGREDIENT_AMOUNT("jei.ingredient_amount", TextFormatting.GRAY);

    private final String key;
    private final TextFormatting format;

    LangData(String key, TextFormatting format) {
        this.key = MOD_ID + "." + key;
        this.format = format;
    }

    public ITextComponent get(Object... args) {
        ITextComponent text = new TextComponentTranslation(key, args);
        text.getStyle().setColor(format);
        return text;
    }
}
