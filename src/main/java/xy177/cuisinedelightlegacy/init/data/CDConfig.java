package xy177.cuisinedelightlegacy.init.data;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public final class CDConfig {
    public static int baseServe, nourishmentDuration, maxIngredient;
    public static float baseNutrition, varietyBonus, perfectionBonus, uiScale;
    public static int uiXAnchor, uiXOffset, uiYAnchor, uiYOffset;

    private CDConfig() {
    }

    public static void load(File file) {
        Configuration config = new Configuration(file);
        config.load();
        baseServe = config.getInt("baseServe", "server", 4, 1, 100,
                "每份料理的基础饥饿值。\nBase hunger points per serving.");
        baseNutrition = config.getFloat("baseNutrition", "server", 0.1F, 0, 100,
                "料理饱和度系数。\nSaturation factor for cooked dishes.");
        varietyBonus = config.getFloat("varietyBonus", "server", 0.2F, 0, 100,
                "每增加一种食材类别时的饥饿值加成比例。\nHunger bonus per additional food type.");
        perfectionBonus = config.getFloat("perfectionBonus", "server", 0.5F, 0, 100,
                "完美料理的额外饥饿值加成比例。\nAdditional hunger bonus for perfect dishes.");
        nourishmentDuration = config.getInt("nourishmentDuration", "server", 100, 1, 10000,
                "完美料理中每种食材类别提供的滋养时间，以刻为单位。\nNourishment ticks per food type in a perfect dish.");
        maxIngredient = config.getInt("maxIngredient", "server", 9, 1, 20,
                "一口炒锅同时容纳的食材组数。\nMaximum ingredient entries in a skillet.");
        uiScale = config.getFloat("uiScale", "client", 1, 0, 16,
                "烹饪界面缩放倍率，设为 0 时隐藏。\nCooking overlay scale, or 0 to hide it.");
        uiXAnchor = config.getInt("uiXAnchor", "client", -1, -1, 1,
                "烹饪界面水平对齐位置，-1 左侧，0 居中，1 右侧。\nHorizontal overlay anchor, -1 left, 0 center, 1 right.");
        uiXOffset = config.getInt("uiXOffset", "client", 8, -1000, 1000,
                "烹饪界面水平偏移量。\nHorizontal cooking overlay offset.");
        uiYAnchor = config.getInt("uiYAnchor", "client", 0, -1, 1,
                "烹饪界面垂直对齐位置，-1 顶部，0 居中，1 底部。\nVertical overlay anchor, -1 top, 0 center, 1 bottom.");
        uiYOffset = config.getInt("uiYOffset", "client", 0, -1000, 1000,
                "烹饪界面垂直偏移量。\nVertical cooking overlay offset.");
        if (config.hasChanged()) {
            config.save();
        }
    }
}
