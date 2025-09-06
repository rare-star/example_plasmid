package su.trap.example_plasmid.game;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.GameType;
import su.trap.example_plasmid.config.GameConfig;
import su.trap.example_plasmid.map_manager.GameMap;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.event.GameOpenListener;
import xyz.nucleoid.plasmid.game.event.GameTickListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;

import java.util.Random;

public class GameActive {
    public final GameSpace gameSpace;
    public final GameConfig config;
    public final GameMap map;
    public final ServerLevel world;
    public long startTime;
    public long timePassed;
    private static final Random random = new Random();

    public GameActive(GameSpace gameSpace, GameConfig config, GameMap map, ServerLevel world, long timePassed) {
        this.gameSpace = gameSpace;
        this.config = config;
        this.map = map;
        this.world = world;
        this.timePassed = timePassed;
        this.startTime = timePassed;
    }

    public static void open(GameSpace gameSpace, GameConfig config, ServerLevel world, GameMap map) {
        // This function gets called to start the game.
        // You can obviously write some logic inside gameSpace.openGame,
        // but it is preferred for you to write game start logic in a specific for it function.
        // You should modify game rules here though.
        gameSpace.openGame(game -> {
            game.setRule(GameRule.PVP, RuleResult.ALLOW);
            game.setRule(GameRule.HUNGER, RuleResult.DENY);
            game.setRule(GameRule.TEAM_CHAT, RuleResult.DENY);
            game.setRule(GameRule.CRAFTING, RuleResult.DENY);
            GameActive active = new GameActive(gameSpace, config, map, world, world.getGameTime());
            game.on(GameTickListener.EVENT, active::tick);
            game.on(PlayerDeathListener.EVENT, active::onPlayerDeath);
            game.on(GameOpenListener.EVENT, active::onGameOpen);
            for (ServerPlayer player : gameSpace.getPlayers()) {
                map.playerSpawn(world, player);
                player.setGameMode(GameType.ADVENTURE);
            }
        });
    }

    private void onGameOpen() {
        // game start logic.
    }

    private void tick() {
        // This function is called every tick when game is active.
        ServerLevel world = this.gameSpace.getWorld();
        long time = world.getGameTime();

        int alive = peopleAlive(gameSpace);

        if (alive > 1) {
            // Put game logic here (one that if there's people alive)

            this.timePassed += 1; // Time counter.
            // Return because there's still more than one person alive.
            // We need this so our later logic for game's end doesn't get called.
            return;
        }
        // Here you can place any logic you want after the game ended that should be called every tick.



        // We know that only one person is not in spectators,
        // so we are searching for him and sending out message that he's the winner.
        for (ServerPlayer player : this.gameSpace.getPlayers()) {
            if (!player.isSpectator()) {
                if (world.getGameTime() == this.timePassed + 2) {
                    this.gameSpace.getPlayers().sendMessage(new TextComponent("Winner: §l" + player.getName().getString()));
                    this.gameSpace.getPlayers().sendTitle(new TextComponent("Winner: §l" + player.getName().getString()), 10, 180, 10);
                    player.setGameMode(GameType.SPECTATOR);
                    for (ServerPlayer allPlayers : this.gameSpace.getPlayers()) {
                        allPlayers.teleportTo(0.5,72, 0.5);
                    }
                }
            }
        }

        // Close the game if 10 seconds from game end passed.
        if (world.getGameTime() == this.timePassed + 20 * 10)
            this.gameSpace.close();
    }

    private InteractionResult onPlayerDeath(ServerPlayer player, DamageSource source) {
        // Spectator mode here is used like a mode for people that are already dead.
        // We also count people alive by searching for people not in spectator mode.
        player.setGameMode(GameType.SPECTATOR);
        return InteractionResult.FAIL;
    }

    private int peopleAlive(GameSpace gameSpace) {
        // Spectator mode here is used like a mode for people that are already dead.
        // We also count people alive by searching for people not in spectator mode.
        int alive = 0;
        for (ServerPlayer player : this.gameSpace.getPlayers()) {
            if (!player.isSpectator()) {
                alive += 1;
            }
        }
        return alive;
    }
}
