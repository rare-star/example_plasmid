package su.trap.example_plasmid.map_manager;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import su.trap.example_plasmid.config.GameConfig;
import xyz.nucleoid.plasmid.game.GameOpenException;
import xyz.nucleoid.plasmid.map.template.MapTemplate;
import xyz.nucleoid.plasmid.map.template.MapTemplateMetadata;
import xyz.nucleoid.plasmid.map.template.MapTemplateSerializer;
import xyz.nucleoid.plasmid.util.BlockBounds;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MapBuilder {

    private final GameConfig config;
    private static final Random random = new Random();

    public MapBuilder(GameConfig config) {
        this.config = config;
    }

    private ResourceLocation randomMap(List<ResourceLocation> mapPool) {
        return mapPool.get(random.nextInt(mapPool.size()));
    }

    public GameMap create() throws GameOpenException {
        try {
            MapTemplate template = MapTemplateSerializer.INSTANCE.loadFromResource(randomMap(this.config.map_id()));
            // or if you want an empty world (new BlockPos should be inside default game bounds):
            // MapTemplate template = MapTemplate.createEmpty();
            // template.setBlockState(new BlockPos(0, 64, 0), Blocks.AIR.defaultBlockState());

            // Random world spawn. You can just create a new BlockPos(x,y,z) if you want to make that the only one possible spawn position.
            BlockPos spawn = randomPosition(template, "spawn");
            GameMap map = new GameMap(template, spawn);
            template.setBiome(Biomes.PLAINS);

            return map;
        } catch (IOException e) {
            throw new GameOpenException(new TextComponent("Failed to load template"), e);
        }
    }

    public static BlockPos randomPosition(MapTemplate template, String marker) {
        // Function name is self-explanatory.
        MapTemplateMetadata metadata = template.getMetadata();
        BlockBounds spawn_bounds = metadata.getFirstRegionBounds(marker);
        BlockPos min = spawn_bounds.getMin();
        BlockPos max = spawn_bounds.getMax();

        int x = random.nextInt(max.getX() - min.getX() + 1) + min.getX();
        int z = random.nextInt(max.getZ() - min.getZ() + 1) + min.getZ();
        return new BlockPos(x, template.getTopY(x, z, Heightmap.Types.WORLD_SURFACE), z);
    }
}
