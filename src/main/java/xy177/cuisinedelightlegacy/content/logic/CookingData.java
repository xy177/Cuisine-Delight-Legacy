package xy177.cuisinedelightlegacy.content.logic;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import xy177.cuisinedelightlegacy.content.logic.transform.Stage;

import java.util.ArrayList;
import java.util.List;

public class CookingData {
    private long lastActionTime;
    private float speed = 1;
    public final List<CookingEntry> contents = new ArrayList<>();

    public void update(long time) {
        lastActionTime = time;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void stir(long time, int reduce) {
        update(time);
        for (CookingEntry entry : contents) {
            entry.maxStirTime = Math.max(entry.maxStirTime, (int) (time - entry.lastStirTime));
            entry.lastStirTime = time + reduce;
        }
    }

    public void addItem(ItemStack item, long time) {
        update(time);
        contents.add(new CookingEntry(item, time, time, 0));
    }

    public NBTTagCompound write() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setLong("Time", lastActionTime);
        tag.setFloat("Speed", speed);
        NBTTagList items = new NBTTagList();
        for (CookingEntry entry : contents) {
            NBTTagCompound item = new NBTTagCompound();
            item.setTag("Item", entry.item.writeToNBT(new NBTTagCompound()));
            item.setLong("Start", entry.startTime);
            item.setLong("Stir", entry.lastStirTime);
            item.setInteger("MaxStir", entry.maxStirTime);
            items.appendTag(item);
        }
        tag.setTag("Items", items);
        return tag;
    }

    public static CookingData read(NBTTagCompound tag) {
        CookingData data = new CookingData();
        data.lastActionTime = tag.getLong("Time");
        if (tag.hasKey("Speed", NBT.TAG_FLOAT)) {
            data.speed = tag.getFloat("Speed");
        }
        NBTTagList items = tag.getTagList("Items", NBT.TAG_COMPOUND);
        for (int i = 0; i < items.tagCount(); i++) {
            NBTTagCompound item = items.getCompoundTagAt(i);
            ItemStack stack = new ItemStack(item.getCompoundTag("Item"));
            if (!stack.isEmpty()) {
                data.contents.add(new CookingEntry(stack, item.getLong("Start"), item.getLong("Stir"),
                        item.getInteger("MaxStir")));
            }
        }
        return data;
    }

    public static class CookingEntry {
        private final ItemStack item;
        private final long startTime;
        private long lastStirTime;
        private int maxStirTime;

        CookingEntry(ItemStack item, long startTime, long lastStirTime, int maxStirTime) {
            this.item = item;
            this.startTime = startTime;
            this.lastStirTime = lastStirTime;
            this.maxStirTime = maxStirTime;
        }

        public ItemStack getItem() {
            return item;
        }

        public long seed() {
            return startTime;
        }

        public float getDuration(CookingData data, float partialTick) {
            return (data.lastActionTime - startTime + partialTick) * data.speed;
        }

        public float timeSinceStir(CookingData data, float partialTick) {
            return Math.max(0, data.lastActionTime - lastStirTime + partialTick) * data.speed;
        }

        public float getMaxStirTime(CookingData data) {
            return maxStirTime * data.speed;
        }

        public Stage getStage(CookingData data) {
            IngredientConfig.IngredientEntry config = IngredientConfig.getEntry(item);
            float time = getDuration(data, 0);
            if (config == null || time < config.minTime) {
                return Stage.RAW;
            }
            return time <= config.maxTime ? Stage.COOKED : Stage.OVERCOOKED;
        }
    }
}
