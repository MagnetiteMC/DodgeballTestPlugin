package dev.magnetite.dodgeball.assemble;

import dev.magnetite.dodgeball.DodgeballPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.List;

public class DodgeballScoreboardAdapter implements AssembleAdapter {
    @Override
    public Component getTitle(Player player) {
        if (DodgeballPlayer.of(player).getGame() == null) return null;

        return MiniMessage.miniMessage().deserialize("<gradient:#b92b27:#1565C0><bold>Dodgeball</bold></gradient>");
    }

    @Override
    public List<String> getLines(Player player) {
        if (DodgeballPlayer.of(player).getGame() == null) return null;

        return DodgeballPlayer.of(player).getGame().getState().updateScoreboard(player);
    }
}
