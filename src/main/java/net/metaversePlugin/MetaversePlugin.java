package net.metaversePlugin;

import net.Abilities.Items.ElysianWilds.NaturesMace;
import net.Abilities.Items.Frostveil.FrozenMace;
import net.Abilities.Items.OverWorld.MomentumMace;
import net.Abilities.Items.ShatteredRealm.ShatteredMace;
import net.Commands.Ability.*;
import net.Commands.Operator.giveItem;
import net.Commands.Trusted.TrustedAdd;
import net.Commands.Trusted.TrustedCommand;
import net.Commands.Trusted.TrustedList;
import net.Commands.Trusted.TrustedRemove;
import net.Dimensions.ElysianWilds.ElysianWilds;
import net.Dimensions.Farlands.Farlands;
import net.Dimensions.Frostveil.Frostveil;
import net.Dimensions.Overworld.Overworld;
import net.Dimensions.ShatteredRealm.ShatteredRealm;
import net.Dimensions.Void.Void;
import net.Utils.Listeners.*;
import net.Managers.*;
import net.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class MetaversePlugin extends JavaPlugin {

    public static final Logger logger = Bukkit.getLogger();
    public static final Component prefix = Component.text("[MetaVerse] ").color(NamedTextColor.LIGHT_PURPLE);

    @Override
    public void onEnable() {
        logger.info("Enabling MetaversePlugin...");

        registerItems();
        registerCommands();

        generateDimensions();
        if (getDataFolder().mkdirs())
            MetaversePlugin.logger.log(Level.CONFIG,"created plugin dataFolder.");

        ActionBarManager actionBar = new ActionBarManager();
        actionBar.start(this);

        Utils.disableRecipe(Material.MACE,this);

        getServer().getPluginManager().registerEvents(new TestListener(),this);
        getServer().getPluginManager().registerEvents(new SnowBallListener(),this);
        getServer().getPluginManager().registerEvents(new AbilityListener(), this);
        getServer().getPluginManager().registerEvents(new ShieldSound(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerJoin(this), this);

        logger.info("enabled MetaversePlugin!");
    }

    @Override
    public void onDisable() {
        logger.info("Disabling MetaversePlugin...");

        FileManager manager = new FileManager(this);
        for (Player player : Bukkit.getOnlinePlayers())
            manager.savePlayerData(player);

        logger.info("Disabled MetaversePlugin!");
    }

    private void registerCommands() {
        getCommand("giveItem").setExecutor(new giveItem());

        AbilityCommand abilityCommand = new AbilityCommand();
        getCommand("Ability").setExecutor(abilityCommand);

        abilityCommand.registerExtensions(new ActivateAbility());
        abilityCommand.registerExtensions(new SetAbility());
        abilityCommand.registerExtensions(new SetCooldown());
        abilityCommand.registerExtensions(new WithdrawAbility());
        abilityCommand.registerExtensions(new SwitchAbility());

        TrustedCommand trustedCommand = new TrustedCommand();
        getCommand("Trust").setExecutor(trustedCommand);

        trustedCommand.registerExtensions(new TrustedAdd());
        trustedCommand.registerExtensions(new TrustedRemove());
        trustedCommand.registerExtensions(new TrustedList());
    }

    private void registerItems() {
        ItemManager itemManager = new ItemManager(this);

        itemManager.registerItem(new MomentumMace(this));
        itemManager.registerItem(new FrozenMace(this));
        itemManager.registerItem(new ShatteredMace());
        itemManager.registerItem(new NaturesMace(this));
    }

    private void generateDimensions() {
        Void void_world = new Void();
        if (Bukkit.getWorld(void_world.getDisplayName()) == null)
            void_world.generateWorld();

        Overworld overworld = new Overworld();
        if (Bukkit.getWorld("world") == null)
            overworld.generateWorld();

        ElysianWilds elysianWilds = new ElysianWilds();
        elysianWilds.registerAbilities(this);
        if (Bukkit.getWorld(elysianWilds.getDisplayName()) == null)
            elysianWilds.generateWorld();

        Farlands farlands = new Farlands();
        farlands.registerAbilities(this);
        if (Bukkit.getWorld(farlands.getDisplayName()) == null)
            farlands.generateWorld();

        Frostveil frostveil = new Frostveil();
        frostveil.registerAbilities(this);
        if (Bukkit.getWorld(frostveil.getDisplayName()) == null)
            frostveil.generateWorld();

        ShatteredRealm realm = new ShatteredRealm();
        realm.registerAbilities(this);
        if (Bukkit.getWorld(realm.getDisplayName()) == null)
            realm.generateWorld();

        DimensionManager.registerDimension(void_world);
        DimensionManager.registerDimension(overworld);
        DimensionManager.registerDimension(elysianWilds);
        DimensionManager.registerDimension(farlands);
        DimensionManager.registerDimension(frostveil);
        DimensionManager.registerDimension(realm);
    }
}
