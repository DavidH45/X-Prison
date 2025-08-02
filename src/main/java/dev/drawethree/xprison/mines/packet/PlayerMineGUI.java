package dev.drawethree.xprison.mines.packet;

import com.cryptomorin.xseries.XMaterial;
import dev.drawethree.xprison.utils.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import org.bukkit.entity.Player;

/**
 * Main GUI for player mines.
 */
public class PlayerMineGUI extends Gui {

    private final PacketMineManager manager;

    public PlayerMineGUI(PacketMineManager manager, Player player) {
        super(player, 6, "Your Mine");
        this.manager = manager;
    }

    @Override
    public void redraw() {
        for (int i = 0; i < 6 * 9; i++) {
            this.setItem(i, ItemStackBuilder.of(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()).name(" ").buildItem().build());
        }

        this.setItem(10, ItemStackBuilder.of(XMaterial.CHEST.parseItem()).name("&eSize Upgrades").build(() -> {
            // TODO: open size upgrades GUI
        }));

        this.setItem(12, ItemStackBuilder.of(XMaterial.GRASS_BLOCK.parseItem()).name("&eBlock Editor").build(() -> {
            // TODO: open block editor GUI
        }));

        this.setItem(14, ItemStackBuilder.of(XMaterial.WHITE_WOOL.parseItem()).name("&eBackground Editor").build(() -> {
            // TODO: open background editor GUI
        }));

        this.setItem(16, ItemStackBuilder.of(XMaterial.REDSTONE_BLOCK.parseItem()).name("&eReset Mine").build(() -> {
            manager.resetMine(this.getPlayer());
            this.redraw();
        }));

        this.setItem(28, ItemStackBuilder.of(XMaterial.LEVER.parseItem()).name("&eAuto Reset").lore("&7Coming soon...").build(() -> {
            // toggle auto reset
        }));

        this.setItem(30, ItemStackBuilder.of(XMaterial.BEACON.parseItem()).name("&eActive Boosters").lore("&7Coming soon...").build(() -> {
            // boosters placeholder
        }));

        this.setItem(40, ItemStackBuilder.of(XMaterial.BARRIER.parseItem()).name("&cClose").build(() -> this.close()));
    }
}
