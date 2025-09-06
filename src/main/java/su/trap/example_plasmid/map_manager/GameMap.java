package su.trap.example_plasmid.map_manager;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.ChunkGenerator;
import xyz.nucleoid.plasmid.map.template.MapTemplate;
import xyz.nucleoid.plasmid.map.template.TemplateChunkGenerator;

public record GameMap(MapTemplate template, BlockPos spawn) {
    public ChunkGenerator asGenerator(MinecraftServer server) {
        return new TemplateChunkGenerator(server, this.template);
    }

    public void playerSpawn(ServerLevel world, ServerPlayer player) {
        player.teleportTo(world, spawn.getX(), spawn.getY(), spawn.getZ(), 90.0F, 0.0F);
    }

    public void playerSpawn(ServerLevel world, ServerPlayer player, BlockPos blockPos) {
        player.teleportTo(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 90.0F, 0.0F);
    }

    public void playerSpawn(ServerLevel world, ServerPlayer player, float yaw, float pitch) {
        player.teleportTo(world, spawn.getX(), spawn.getY(), spawn.getZ(), yaw, pitch);
    }

    public void playerSpawn(ServerLevel world, ServerPlayer player, BlockPos blockPos, float yaw, float pitch) {
        player.teleportTo(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), yaw, pitch);
    }
}
