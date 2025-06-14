package net.Managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class RecipeManager {

    private static NamespacedKey getNamespaceKey(String key) {
        return new NamespacedKey("metaverse", key);
    }

    public static void registerBetterRecipes() {
        enchantedGoldenApple();
        goldenApple();
        netheriteIngot();
        cobweb();
        breezeRod();
        netherWart();
        soulSoil();
        shulkerBox();
        paper();
        bookshelf();
        goldenCarrot();
        tnt();
        sugar();
        magmaCream();
        magmaBlock();
        anvil();
        trident();
        netherite_upgrade();
        totem();
        obsidian();

        registerArmorTrimDupes();
    }

    public static void disableRecipe(@NotNull Material result, @NotNull Plugin plugin) {
        Iterator<Recipe> it = plugin.getServer().recipeIterator();
        while (it.hasNext()) {
            Recipe recipe = it.next();
            if (recipe != null && recipe.getResult().getType() == result) {
                it.remove();
            }
        }
    }

    private static void enchantedGoldenApple() {
        ShapedRecipe recipe = new ShapedRecipe(getNamespaceKey("enchanted_golden_apple"), new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
        recipe.shape("GGG", "GAG", "GGG");
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('A', Material.APPLE);
        Bukkit.addRecipe(recipe);
    }

    private static void goldenApple() {
        ShapedRecipe recipe = new ShapedRecipe(getNamespaceKey("golden_apple"), new ItemStack(Material.GOLDEN_APPLE));
        recipe.shape(" G ", "GAG", " G ");
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('A', Material.APPLE);
        Bukkit.addRecipe(recipe);
    }

    private static void netheriteIngot() {
        ShapelessRecipe recipe = new ShapelessRecipe(getNamespaceKey("netherite_ingot"), new ItemStack(Material.NETHERITE_INGOT));
        recipe.addIngredient(2, Material.DIAMOND);
        recipe.addIngredient(2, Material.NETHERITE_SCRAP);
        Bukkit.addRecipe(recipe);
    }

    private static void cobweb() {
        ItemStack result = new ItemStack(Material.COBWEB);
        result.setAmount(3);

        ShapedRecipe recipe = new ShapedRecipe(getNamespaceKey("cobweb"), result);
        recipe.shape("CCC", "CCC", "CCC");
        recipe.setIngredient('C', Material.STRING);
        Bukkit.addRecipe(recipe);
    }

    private static void breezeRod() {
        ShapelessRecipe recipe = new ShapelessRecipe(getNamespaceKey("breeze_rod"), new ItemStack(Material.BREEZE_ROD));
        recipe.addIngredient(4, Material.WIND_CHARGE);
        Bukkit.addRecipe(recipe);
    }

    private static void netherWart() {
        ItemStack result = new ItemStack(Material.NETHER_WART);
        result.setAmount(9);

        ShapedRecipe recipe = new ShapedRecipe(getNamespaceKey("nether_wart"), result);
        recipe.shape("NNN", "NBN", "NNN");
        recipe.setIngredient('N', Material.NETHER_WART_BLOCK);
        recipe.setIngredient('B', Material.BLAZE_POWDER);
        Bukkit.addRecipe(recipe);
    }

    private static void soulSoil() {
        ItemStack result = new ItemStack(Material.SOUL_SOIL);
        result.setAmount(4);

        ShapelessRecipe recipe = new ShapelessRecipe(getNamespaceKey("soul_soil"), result);
        recipe.addIngredient(4, Material.SOUL_SAND);
        Bukkit.addRecipe(recipe);
    }

    private static void shulkerBox() {
        ShapelessRecipe recipe = new ShapelessRecipe(getNamespaceKey("shulkerbox"), new ItemStack(Material.SHULKER_BOX));
        recipe.addIngredient(2, Material.SHULKER_SHELL);
        recipe.addIngredient(1, Material.CHEST);
        Bukkit.addRecipe(recipe);
    }

    private static void paper() {
        ItemStack result = new ItemStack(Material.PAPER);
        result.setAmount(3);

        ShapelessRecipe recipe = new ShapelessRecipe(getNamespaceKey("paper"), result);
        recipe.addIngredient(3, Material.SUGAR_CANE);
        Bukkit.addRecipe(recipe);
    }

    private static void bookshelf() {
        ItemStack result = new ItemStack(Material.BOOKSHELF);
        result.setAmount(3);

        ShapedRecipe recipe = new ShapedRecipe(getNamespaceKey("bookshelf_flexible"), result);
        recipe.shape("WWW", "BBB", "WWW");
        recipe.setIngredient('W', new RecipeChoice.MaterialChoice(Material.OAK_PLANKS, Material.SPRUCE_PLANKS, Material.BIRCH_PLANKS,
                Material.JUNGLE_PLANKS, Material.ACACIA_PLANKS, Material.DARK_OAK_PLANKS, Material.MANGROVE_PLANKS,
                Material.CHERRY_PLANKS, Material.BAMBOO_PLANKS, Material.CRIMSON_PLANKS, Material.WARPED_PLANKS, Material.PALE_OAK_PLANKS));
        recipe.setIngredient('B', Material.BOOK);
        Bukkit.addRecipe(recipe);
    }

    private static void goldenCarrot() {
        ItemStack result = new ItemStack(Material.GOLDEN_CARROT);
        result.setAmount(2);

        ShapelessRecipe recipe = new ShapelessRecipe(getNamespaceKey("golden_carrot"), result);
        recipe.addIngredient(Material.CARROT);
        recipe.addIngredient(Material.GOLD_INGOT);
        Bukkit.addRecipe(recipe);
    }

    private static void tnt() {
        ItemStack result = new ItemStack(Material.TNT);
        result.setAmount(2);

        ShapedRecipe recipe = new ShapedRecipe(getNamespaceKey("tnt"), result);
        recipe.shape("SRS", "GGG", "SRS");
        recipe.setIngredient('G',Material.GUNPOWDER);
        recipe.setIngredient('R', Material.REDSTONE);
        recipe.setIngredient('S', Material.SAND);
        Bukkit.addRecipe(recipe);
    }

    private static void sugar() {
        ItemStack result = new ItemStack(Material.SUGAR);
        result.setAmount(2);

        ShapelessRecipe recipe = new ShapelessRecipe(getNamespaceKey("sugar"), result);
        recipe.addIngredient(Material.SUGAR_CANE);
        Bukkit.addRecipe(recipe);
    }

    private static void magmaCream() {
        ItemStack result = new ItemStack(Material.MAGMA_CREAM);
        result.setAmount(2);

        ShapelessRecipe recipe = new ShapelessRecipe(getNamespaceKey("magma_cream"), result);
        recipe.addIngredient(Material.MAGMA_BLOCK);
        Bukkit.addRecipe(recipe);
    }

    private static void magmaBlock() {
        ShapelessRecipe recipe = new ShapelessRecipe(getNamespaceKey("magma_block"), new ItemStack(Material.MAGMA_BLOCK));
        recipe.addIngredient(2, Material.MAGMA_CREAM);
        recipe.addIngredient(2, Material.COBBLESTONE);
        Bukkit.addRecipe(recipe);
    }

    private static void anvil() {
    ShapedRecipe recipe = new ShapedRecipe(getNamespaceKey("anvil"), new ItemStack(Material.ANVIL));
        recipe.shape("III", " B ", "III");
        recipe.setIngredient('I',Material.IRON_INGOT);
        recipe.setIngredient('B', Material.IRON_BLOCK);
        Bukkit.addRecipe(recipe);
    }

    private static void trident() {
        ShapedRecipe recipe = new ShapedRecipe(getNamespaceKey("trident"), new ItemStack(Material.TRIDENT));
        recipe.shape("PPP", " B ", " B ");
        recipe.setIngredient('P',Material.PRISMARINE_SHARD);
        recipe.setIngredient('B', Material.BREEZE_ROD);
        Bukkit.addRecipe(recipe);
    }

    private static void netherite_upgrade() {
        ShapedRecipe recipe = new ShapedRecipe(getNamespaceKey("neth_upgrade"), new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE));
        recipe.shape("NDN", "NIN", "NNN");
        recipe.setIngredient('N',Material.NETHERRACK);
        recipe.setIngredient('D', Material.DIAMOND);
        recipe.setIngredient('I', Material.NETHERITE_INGOT);
        Bukkit.addRecipe(recipe);
    }

    private static void totem() {
        ShapedRecipe recipe = new ShapedRecipe(getNamespaceKey("totem"), new ItemStack(Material.TOTEM_OF_UNDYING));
        recipe.shape("GGG", "ETE", "GGG");
        recipe.setIngredient('G',Material.GOLD_BLOCK);
        recipe.setIngredient('E', Material.EMERALD_BLOCK);
        recipe.setIngredient('T', Material.GHAST_TEAR);
        Bukkit.addRecipe(recipe);
    }

    private static void obsidian() {
        ShapelessRecipe recipe = new ShapelessRecipe(getNamespaceKey("obsidian"), new ItemStack(Material.OBSIDIAN));
        recipe.addIngredient(Material.WATER_BUCKET);
        recipe.addIngredient(Material.LAVA_BUCKET);

        Bukkit.addRecipe(recipe);
    }

    private static void registerArmorTrimDupes() {
        for (Material material : Material.values()) {
            if (!material.name().endsWith("_TRIM_SMITHING_TEMPLATE")) continue;

            ItemStack result = new ItemStack(material);
            result.setAmount(2);

            ShapedRecipe recipe = new ShapedRecipe(getNamespaceKey("dupe_" + material.name().toLowerCase()), result);
            recipe.shape(" D ", "DTD", " D ");
            recipe.setIngredient('T', new RecipeChoice.ExactChoice(new ItemStack(material)));
            recipe.setIngredient('D', Material.DIAMOND);
            Bukkit.addRecipe(recipe);
        }
    }


}
