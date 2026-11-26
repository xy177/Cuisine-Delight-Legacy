package xy177.cuisinedelightlegacy.content.logic;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import xy177.cuisinedelightlegacy.content.logic.CookTransformConfig.CookTransform;
import xy177.cuisinedelightlegacy.content.logic.CookingData.CookingEntry;
import xy177.cuisinedelightlegacy.content.logic.IngredientConfig.IngredientEntry;
import xy177.cuisinedelightlegacy.content.logic.transform.Stage;
import xy177.cuisinedelightlegacy.init.data.CDConfig;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CookedFoodData {
    public final int total;
    public int size;
    public int nutrition;
    public final int score;
    public final EnumSet<FoodType> types;
    public final List<Entry> entries;

    private CookedFoodData(int total, int size, int nutrition, int score, EnumSet<FoodType> types, List<Entry> entries) {
        this.total = total;
        this.size = size;
        this.nutrition = nutrition;
        this.score = score;
        this.types = types;
        this.entries = entries;
    }

    public static CookedFoodData of(CookingData data) {
        EnumSet<FoodType> types = EnumSet.noneOf(FoodType.class);
        List<Entry> entries = new ArrayList<>();
        int size = 0;
        int nutrition = 0;
        float penalty = 0;
        for (CookingEntry entry : data.contents) {
            ItemStack stack = entry.getItem();
            IngredientEntry config = IngredientConfig.getEntry(stack);
            if (config == null) continue;
            float duration = entry.getDuration(data, 0);
            boolean raw = duration < config.minTime;
            boolean overcooked = duration > config.maxTime;
            boolean burnt = entry.getMaxStirTime(data) > config.stirTime;
            int itemSize = config.size * stack.getCount();
            if (raw) penalty += itemSize * config.rawPenalty;
            if (overcooked || burnt) penalty += itemSize * config.overcookPenalty;
            size += itemSize;
            nutrition += config.nutrition * itemSize;
            if (config.type != FoodType.NONE) types.add(config.type);
            entries.add(new Entry(stack, itemSize, burnt, raw, overcooked));
        }
        float quality = size == 0 ? 0 : MathHelper.clamp(1 - penalty / size, 0, 1);
        return new CookedFoodData(size, size, size == 0 ? 0 : Math.round(quality * nutrition / size),
                Math.round(quality * 100), types, entries);
    }

    public boolean canEat() {
        return score >= 60 && total > 0 && size > 0;
    }

    public int getHunger() {
        float multiplier = 1 + (types.size() - 1) * CDConfig.varietyBonus;
        if (score == 100) multiplier += CDConfig.perfectionBonus;
        return Math.max(0, (int) (multiplier * CDConfig.baseServe));
    }

    public float getSaturation() {
        return nutrition * CDConfig.baseNutrition;
    }

    public List<PotionEffect> getEffects() {
        Map<Potion, EffectData> effects = new LinkedHashMap<>();
        List<PotionEffect> result = new ArrayList<>();
        if (!canEat()) return result;
        if (score == 100 && types.size() > 1) {
            Potion nourishment = ForgeRegistries.POTIONS.getValue(new ResourceLocation("farmersdelight", "nourishment"));
            if (nourishment != null) result.add(new PotionEffect(nourishment, CDConfig.nourishmentDuration * types.size()));
        }
        for (Entry entry : entries) {
            IngredientEntry config = IngredientConfig.getEntry(entry.stack);
            if (config == null || entry.burnt || entry.raw || entry.overcooked) continue;
            for (PotionEffect effect : config.effects) {
                EffectData existing = effects.get(effect.getPotion());
                int amplifier = effect.getAmplifier();
                float duration = (float) effect.getDuration() * entry.stack.getCount() / total;
                if (existing == null || existing.amplifier < amplifier) {
                    effects.put(effect.getPotion(), new EffectData(amplifier, duration));
                } else if (existing.amplifier == amplifier) {
                    existing.duration += duration;
                }
            }
        }
        effects.forEach((potion, effect) -> result.add(new PotionEffect(potion, Math.round(effect.duration), effect.amplifier)));
        return result;
    }

    public NBTTagCompound write() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Total", total);
        tag.setInteger("Size", size);
        tag.setInteger("Nutrition", nutrition);
        tag.setInteger("Quality", score);
        int flags = 0;
        for (FoodType type : types) flags |= 1 << type.ordinal();
        tag.setInteger("Types", flags);
        NBTTagList items = new NBTTagList();
        for (Entry entry : entries) {
            NBTTagCompound item = new NBTTagCompound();
            item.setTag("Item", entry.stack.writeToNBT(new NBTTagCompound()));
            item.setInteger("Size", entry.itemSize);
            item.setBoolean("Burnt", entry.burnt);
            item.setBoolean("Raw", entry.raw);
            item.setBoolean("Overcooked", entry.overcooked);
            items.appendTag(item);
        }
        tag.setTag("Items", items);
        return tag;
    }

    public static CookedFoodData read(NBTTagCompound tag) {
        EnumSet<FoodType> types = EnumSet.noneOf(FoodType.class);
        int flags = tag.getInteger("Types");
        for (FoodType type : FoodType.values()) {
            if ((flags & (1 << type.ordinal())) != 0) types.add(type);
        }
        List<Entry> entries = new ArrayList<>();
        NBTTagList items = tag.getTagList("Items", NBT.TAG_COMPOUND);
        for (int i = 0; i < items.tagCount(); i++) {
            NBTTagCompound item = items.getCompoundTagAt(i);
            ItemStack stack = new ItemStack(item.getCompoundTag("Item"));
            if (!stack.isEmpty()) {
                entries.add(new Entry(stack, item.getInteger("Size"), item.getBoolean("Burnt"),
                        item.getBoolean("Raw"), item.getBoolean("Overcooked")));
            }
        }
        return new CookedFoodData(tag.getInteger("Total"), tag.getInteger("Size"), tag.getInteger("Nutrition"),
                tag.getInteger("Quality"), types, entries);
    }

    public static final class Entry {
        public final ItemStack stack;
        public final int itemSize;
        public final boolean burnt, raw, overcooked;

        Entry(ItemStack stack, int itemSize, boolean burnt, boolean raw, boolean overcooked) {
            this.stack = stack;
            this.itemSize = itemSize;
            this.burnt = burnt;
            this.raw = raw;
            this.overcooked = overcooked;
        }

        public ItemStack getEatenStack() {
            CookTransform transform = CookTransformConfig.get(stack);
            return transform != null && transform.stage != Stage.RAW ? transform.renderStack(Stage.COOKED, stack) : stack;
        }
    }

    private static final class EffectData {
        final int amplifier;
        float duration;

        EffectData(int amplifier, float duration) {
            this.amplifier = amplifier;
            this.duration = duration;
        }
    }
}
