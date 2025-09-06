package su.trap.example_plasmid.Game;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import su.trap.example_plasmid.Config.GameConfig;
import su.trap.example_plasmid.MapManager.GameMap;
import su.trap.example_plasmid.MapManager.MapBuilder;
import xyz.nucleoid.fantasy.BubbleWorldConfig;
import xyz.nucleoid.plasmid.game.*;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;
import xyz.nucleoid.plasmid.game.event.GameTickListener;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.RequestStartListener;

import static su.trap.example_plasmid.MapManager.MapBuilder.randomPosition;

public class GameWaiting {
    private final GameSpace gameSpace;
    private final GameMap map;
    private final GameConfig config;
    private final ServerLevel world;

    private GameWaiting(GameSpace gameSpace, GameMap map, GameConfig config, ServerLevel world) {
        this.gameSpace = gameSpace;
        this.map = map;
        this.config = config;
        this.world = world;
    }

    public static GameOpenProcedure open(GameOpenContext<GameConfig> context) {
        // Everything before context.createOpenProcedure is world creation logic.
        GameConfig config = context.getConfig();
        MapBuilder generator = new MapBuilder(context.getConfig());
        GameMap map = generator.create();

        BubbleWorldConfig worldConfig = new BubbleWorldConfig()
                .setGenerator(map.asGenerator(context.getServer()))
                .setDefaultGameMode(GameType.SPECTATOR);

        return context.createOpenProcedure(worldConfig, game -> {
            // This creates a lobby "game", so logic for lobby "game" start is here.
            // It's preferred for you to use an GameOpenListener.EVENT event for any Game Open logic.

            GameWaiting waiting = new GameWaiting(game.getSpace(), map, context.getConfig(), game.getSpace().getWorld());
            GameWaitingLobby.applyTo(game, new PlayerConfig(1, 16));
            game.on(RequestStartListener.EVENT, waiting::requestStart);
            game.on(GameTickListener.EVENT, waiting::tick);
            game.on(PlayerAddListener.EVENT, waiting::addPlayer);
            worldConfig.setTimeOfDay(100);
        });
    }

    private void tick() {
        // This happens every tick that players are in lobby.
        // You can just leave this as is.
        for (ServerPlayer player : this.gameSpace.getWorld().players()) {
            if (!this.map.template().getBounds().contains(player.blockPosition())) {
                map.PlayerSpawn(this.gameSpace.getWorld(), player, randomPosition(this.map.template(), "spawn"));
            }
        }
    }

    private StartResult requestStart() {
        // Game start. This is automatically handled by plasmid if we just set an event, obviously.
        // Leave this as is.
        GameActive.open(this.gameSpace, this.config, this.world, this.map);
        return StartResult.OK;
    }

    private void addPlayer(ServerPlayer player) {
        // This function teleports a player to a spawn position on map.
        // Here we are randomizing spawn position of player to position within a spawn region.
        map.PlayerSpawn(this.gameSpace.getWorld(), player, randomPosition(this.map.template(), "spawn"));
        // or map.PlayerSpawn(this.gameSpace.getWorld(), player); // if you want to spawn every one in map's single default spawn point.
    }
}