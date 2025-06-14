package net.metaversePlugin;

import net.Commands.Ability.*;
import net.Commands.Command;
import net.Commands.Dimension.SetDimension;
import net.Commands.Item.GiveItem;
import net.Commands.Item.SetCooldownItem;
import net.Commands.Item.SetDisabledItem;
import net.Commands.Trusted.TrustedAdd;
import net.Commands.Trusted.TrustedList;
import net.Commands.Trusted.TrustedRemove;
import net.Dimensions.ElysianWilds.ElysianWilds;
import net.Dimensions.Frostveil.Frostveil;
import net.Dimensions.Overworld.Overworld;
import net.Dimensions.ShatteredRealm.ShatteredRealm;
import net.Dimensions.Void.Void;
import net.Items.AbilityBeacon.AbilityBeacon;
import net.Utils.Listeners.*;
import net.Managers.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class MetaversePlugin extends JavaPlugin {

    public static Logger logger;
    public static final Component prefix = Component.text("[MetaVerse] ").color(NamedTextColor.LIGHT_PURPLE);

    @Override
    public void onEnable() {
        logger = getLogger();
        logger.info("Enabling MetaversePlugin...");

        generateDimensions();
        registerItems();
        registerCommands();

        if (getDataFolder().mkdirs())
            MetaversePlugin.logger.log(Level.CONFIG,"created plugin dataFolder.");

        new DragonEggListener(this);

        ActionBarManager actionBar = new ActionBarManager();
        actionBar.start(this);

        RecipeManager.registerBetterRecipes();
        RecipeManager.disableRecipe(Material.MACE,this);

        getServer().getPluginManager().registerEvents(new SnowBallListener(),this);
        getServer().getPluginManager().registerEvents(new AbilityListener(), this);
        getServer().getPluginManager().registerEvents(new ShieldSound(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerJoin(this), this);
        getServer().getPluginManager().registerEvents(new CustomEventListener(), this);

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

    private void registerItems() {
        ItemManager manager = new ItemManager(this);
        manager.registerItem(new AbilityBeacon(this));
    }

    private void registerCommands() {
        Command abilityCommand = new Command();
        getCommand("Ability").setExecutor(abilityCommand);

        abilityCommand.registerExtensions(new ActivateAbility());
        abilityCommand.registerExtensions(new SetAbility());
        abilityCommand.registerExtensions(new SetCooldown());
        abilityCommand.registerExtensions(new WithdrawAbility());
        abilityCommand.registerExtensions(new SwitchAbility());
        abilityCommand.registerExtensions(new SetDisabled());

        Command trustedCommand = new Command();
        getCommand("Trust").setExecutor(trustedCommand);

        trustedCommand.registerExtensions(new TrustedAdd());
        trustedCommand.registerExtensions(new TrustedRemove());
        trustedCommand.registerExtensions(new TrustedList());

        Command itemCommand = new Command();
        getCommand("Item").setExecutor(itemCommand);

        itemCommand.registerExtensions(new GiveItem());
        itemCommand.registerExtensions(new SetCooldownItem());
        itemCommand.registerExtensions(new SetDisabledItem());

        Command dimensionCommand = new Command();
        getCommand("Dimension").setExecutor(dimensionCommand);

        dimensionCommand.registerExtensions(new SetDimension());
    }

    private void generateDimensions() {
        Void void_world = new Void();

        Overworld overworld = new Overworld();
        overworld.registerAbilities(this);
        overworld.registerItems(this);

        ElysianWilds elysianWilds = new ElysianWilds();
        elysianWilds.registerAbilities(this);
        elysianWilds.registerItems(this);

        Frostveil frostveil = new Frostveil();
        frostveil.registerAbilities(this);
        frostveil.registerItems(this);

        ShatteredRealm realm = new ShatteredRealm();
        realm.registerAbilities(this);
        realm.registerItems(this);

        DimensionManager.registerDimension(void_world);
        DimensionManager.registerDimension(overworld);
        DimensionManager.registerDimension(elysianWilds);
        DimensionManager.registerDimension(frostveil);
        DimensionManager.registerDimension(realm);
    }
}
