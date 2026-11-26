package xy177.cuisinedelightlegacy;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;
import xy177.cuisinedelightlegacy.content.block.CuisineSkilletBlockEntity;
import xy177.cuisinedelightlegacy.init.data.CDConfig;
import xy177.cuisinedelightlegacy.init.data.CDConfigGen;
import xy177.cuisinedelightlegacy.init.data.CustomConfig;
import xy177.cuisinedelightlegacy.init.data.RecipeGen;

import java.nio.file.Path;

@Mod(
        modid = CuisineDelightLegacy.MOD_ID,
        name = CuisineDelightLegacy.NAME,
        version = CuisineDelightLegacy.VERSION,
        dependencies = "required-after:farmersdelight;after:farmers_future_delight"
)
public final class CuisineDelightLegacy {

    public static final String MOD_ID = "cuisinedelightlegacy";
    public static final String NAME = "Cuisine Delight Legacy";
    public static final String VERSION = "1.0.0";

    private Path configDir;
    private Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CDConfig.load(event.getSuggestedConfigurationFile());
        configDir = event.getModConfigurationDirectory().toPath().resolve(MOD_ID);
        logger = event.getModLog();
        GameRegistry.registerTileEntity(CuisineSkilletBlockEntity.class, loc("cuisine_skillet"));
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        CDConfigGen.init();
        CustomConfig.load(configDir, logger);
        RecipeGen.init();
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
