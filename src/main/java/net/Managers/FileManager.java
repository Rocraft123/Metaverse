package net.Managers;

import net.Abilities.Model.Ability;
import net.Dimensions.Dimension;
import net.metaversePlugin.MetaversePlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class FileManager {

    private final Plugin plugin;

    public FileManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean hasPlayerData(UUID uuid) {
        File playerDataPath = new File(plugin.getDataFolder(), "PlayerData");
        if (playerDataPath.mkdirs())
            MetaversePlugin.logger.log(Level.CONFIG, "Created PlayerData folder.");

        File playerData = new File(playerDataPath, uuid.toString() + ".json");
        return playerData.exists();
    }

    public void loadPlayerData(UUID uuid) {
        File playerDataPath = new File(plugin.getDataFolder(), "PlayerData");
        if (playerDataPath.mkdirs())
            MetaversePlugin.logger.log(Level.CONFIG, "Created PlayerData folder.");

        File playerData = new File(playerDataPath, uuid.toString() + ".json");
        if (!playerData.exists())
            generatePlayerData(uuid);

        try (FileReader reader = new FileReader(playerData)) {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(reader);

            String mainName = (String) jsonObject.getOrDefault("main-ability", null);
            String invName = (String) jsonObject.getOrDefault("inventory-ability", null);

            Ability mainAbility = mainName != null ? AbilityManager.getAbility(mainName) : null;
            Ability inventoryAbility = invName != null ? AbilityManager.getAbility(invName) : null;

            List<UUID> trustedPlayers = new ArrayList<>();
            Object trustedObj = jsonObject.get("trusted");
            if (trustedObj instanceof JSONArray jsonTrustedArray) {
                for (Object element : jsonTrustedArray) {
                    if (element instanceof String uuidStr) {
                        try {
                            trustedPlayers.add(UUID.fromString(uuidStr));
                        } catch (IllegalArgumentException ignored) {}
                    }
                }
            }

            if (mainAbility != null)
                AbilityManager.setSelectedAbility(uuid, mainAbility);

            if (inventoryAbility != null)
                AbilityManager.setSecondAbility(uuid, inventoryAbility);

            for (UUID trusted : trustedPlayers)
                TrustManager.addTrustedPlayer(uuid, trusted);

            MetaversePlugin.logger.log(Level.CONFIG, "Loaded data for " + uuid);
        } catch (ParseException | IOException e) {
            MetaversePlugin.logger.log(Level.WARNING, "Failed to load data for " + uuid, e);
            throw new RuntimeException(e);
        }
    }

    public void savePlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        File playerFile = new File(plugin.getDataFolder(), "PlayerData/" + uuid + ".json");

        JSONObject data = new JSONObject();
        Ability main = AbilityManager.getSelectedAbility(player);
        Ability inv = AbilityManager.getSecondAbility(player);

        if (main != null) {
            Dimension dimension = DimensionManager.getDimension(main);
            if (dimension == null) data.put("main-ability", null);
            else data.put("main-ability", dimension.getDisplayName().toLowerCase().replace(" ", "") + ":" +
                    main.getName());
        } else data.put("main-ability", null);

        if (inv != null) {
            Dimension dimension = DimensionManager.getDimension(inv);
            if (dimension == null) data.put("inventory-ability", null);
            else data.put("inventory-ability", dimension.getDisplayName().toLowerCase().replace(" ", "") + ":" +
                    inv.getName());
        } else data.put("inventory-ability", null);

        JSONArray trustedArray = new JSONArray();
        for (UUID trusted : TrustManager.getTrustedUUIDs(player))
            trustedArray.add(trusted.toString());
        data.put("trusted", trustedArray);

        writeJsonToFile(playerFile, data);
    }


    public void generatePlayerData(UUID uuid) {
        File playerDataPath = new File(plugin.getDataFolder(), "PlayerData");
        if (!playerDataPath.exists() && playerDataPath.mkdirs())
            MetaversePlugin.logger.log(Level.CONFIG, "Created PlayerData folder.");

        File playerData = new File(playerDataPath, uuid.toString() + ".json");
        if (!playerData.exists()) {
            try {
                if (playerData.createNewFile()) {
                    MetaversePlugin.logger.log(Level.CONFIG, "Created a player data file for: " + uuid);
                    writeJsonToFile(playerData, new JSONObject());
                }
            } catch (IOException e) {
                MetaversePlugin.logger.warning("Failed to create player data file: " + uuid);
                throw new RuntimeException(e);
            }
        }
    }


    private void writeJsonToFile(File file, JSONObject jsonObject) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(jsonObject.toJSONString());
            writer.flush();
        } catch (IOException e) {
            MetaversePlugin.logger.warning("Failed to write JSON to file: " + file.getName());
            throw new RuntimeException(e);
        }
    }

}
