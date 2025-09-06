package su.trap.example_plasmid;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import su.trap.example_plasmid.Config.GameConfig;
import su.trap.example_plasmid.Game.GameWaiting;
import xyz.nucleoid.plasmid.game.GameType;

public class Entrypoint implements ModInitializer {
    public static final GameType<GameConfig> TYPE = GameType.register(
            new ResourceLocation("example_plasmid", "game"),
            GameWaiting::open,
            GameConfig.CODEC
    );

	@Override
	public void onInitialize() {
        Logging.log(new String[]{
                "EXAMPLE MOD | INITIALIZATION",
                "EXAMPLE MOD | INITIALIZATION",
                "EXAMPLE MOD | INITIALIZATION"
        });
	}
}
