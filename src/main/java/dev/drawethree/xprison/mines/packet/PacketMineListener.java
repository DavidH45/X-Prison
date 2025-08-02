package dev.drawethree.xprison.mines.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

/**
 * Listens for player join and block dig packets to power mines.
 */
public class PacketMineListener implements Listener {

    private final PacketMineManager manager;

    public PacketMineListener(PacketMineManager manager) {
        this.manager = manager;
        Plugin plugin = manager.getPlugin().getCore();
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.BLOCK_DIG) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                PacketMine mine = manager.getMine(player);
                if (mine == null) {
                    return;
                }
                BlockPosition pos = event.getPacket().getBlockPositionModifier().read(0);
                if (mine.isInside(pos)) {
                    mine.handleBreak(player, pos);
                    if (mine.getProgress() >= 0.90) {
                        mine.reset(player);
                    }
                    event.setCancelled(true);
                }
            }
        });
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        manager.getOrCreateMine(player);
    }
}
