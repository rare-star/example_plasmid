package su.trap.example_plasmid.Config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record GameConfig(String type, String name, List<ResourceLocation> map_id) {
    // This is parser for game config json.
    public static final Codec<GameConfig> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.STRING.fieldOf("type").forGetter(GameConfig::type),
                Codec.STRING.fieldOf("name").forGetter(GameConfig::name),
                ResourceLocation.CODEC.listOf().fieldOf("map_id").forGetter(GameConfig::map_id)
        ).apply(instance, GameConfig::new);
    });
}