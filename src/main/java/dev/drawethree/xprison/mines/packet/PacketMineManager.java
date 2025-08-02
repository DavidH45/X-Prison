package dev.drawethree.xprison.mines.packet;

import dev.drawethree.xprison.mines.XPrisonMines;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles creation and management of player mines.
 */
public class PacketMineManager {

    @Getter
    private final XPrisonMines plugin;
    private final Map<UUID, PacketMine> mines = new HashMap<>();
    private final Map<Integer, SizeLevel> sizeLevels = new HashMap<>();

    public PacketMineManager(XPrisonMines plugin) {
        this.plugin = plugin;
        loadSizeLevels();
    }

    private void loadSizeLevels() {
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("size_levels");
        if (sec == null) {
            return;
        }
        for (String key : sec.getKeys(false)) {
            int level = Integer.parseInt(key);
            int size = sec.getInt(key + ".size");
            double cost = sec.getDouble(key + ".cost");
            sizeLevels.put(level, new SizeLevel(level, size, cost));
        }
    }

    public PacketMine getMine(Player player) {
        return mines.get(player.getUniqueId());
    }

    public PacketMine getOrCreateMine(Player player) {
        return mines.computeIfAbsent(player.getUniqueId(), uuid -> {
            SizeLevel level = sizeLevels.getOrDefault(1, new SizeLevel(1, 20, 0));
            Location origin = player.getLocation().clone().add(-level.getSize() / 2.0, -1, -level.getSize() / 2.0);
            PacketMine mine = new PacketMine(uuid, origin, level.getSize());
            mine.render(player);
            return mine;
        });
    }

    public void resetMine(Player player) {
        PacketMine mine = getMine(player);
        if (mine != null) {
            mine.reset(player);
        }
    }

    public Map<Integer, SizeLevel> getSizeLevels() {
        return sizeLevels;
    }

    public void openMainGUI(Player player) {
        new PlayerMineGUI(this, player).open();
    }

    /**
     * Represents a size level entry.
     */
    @Getter
    public static class SizeLevel {
        private final int level;
        private final int size;
        private final double cost;

        public SizeLevel(int level, int size, double cost) {
            this.level = level;
            this.size = size;
            this.cost = cost;
        }
    }
}
