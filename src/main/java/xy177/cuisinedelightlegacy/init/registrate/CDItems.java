package xy177.cuisinedelightlegacy.init.registrate;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import xy177.cuisinedelightlegacy.CuisineDelightLegacy;
import xy177.cuisinedelightlegacy.content.block.CuisineSkilletBlock;
import xy177.cuisinedelightlegacy.content.item.CuisineSkilletItem;
import xy177.cuisinedelightlegacy.content.item.PlateItem;
import xy177.cuisinedelightlegacy.content.item.SpatulaItem;

import static xy177.cuisinedelightlegacy.CuisineDelightLegacy.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public final class CDItems {
    public static final CreativeTabs TAB = new CreativeTabs(MOD_ID) {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(SKILLET);
        }
    };

    public static final CuisineSkilletBlock SKILLET_BLOCK = new CuisineSkilletBlock();
    public static final CuisineSkilletItem SKILLET = new CuisineSkilletItem(SKILLET_BLOCK);
    public static final Item PLATE = new PlateItem();
    public static final Item SPATULA = new SpatulaItem();
    public static final Item FRIED_EGG = new Item();

    private CDItems() {
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        SKILLET_BLOCK.setRegistryName(CuisineDelightLegacy.loc("cuisine_skillet"));
        SKILLET_BLOCK.setUnlocalizedName(MOD_ID + ".cuisine_skillet");
        SKILLET_BLOCK.setCreativeTab(TAB);
        event.getRegistry().register(SKILLET_BLOCK);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        register(registry, SKILLET, "cuisine_skillet");
        register(registry, PLATE, "plate");
        register(registry, SPATULA, "spatula");
        FRIED_EGG.setRegistryName(CuisineDelightLegacy.loc("fried_egg"));
        FRIED_EGG.setUnlocalizedName(MOD_ID + ".fried_egg");
        registry.register(FRIED_EGG);
        for (PlateFood food : PlateFood.values()) {
            register(registry, food.item, food.id());
        }
    }

    private static void register(IForgeRegistry<Item> registry, Item item, String name) {
        item.setRegistryName(CuisineDelightLegacy.loc(name));
        item.setUnlocalizedName(MOD_ID + "." + name);
        item.setCreativeTab(TAB);
        registry.register(item);
    }
}
