package com.tattyseal.compactstorage;

import com.google.common.collect.Lists;
import com.tattyseal.compactstorage.ConfigurationState;
import com.tattyseal.compactstorage.network.packet.S03PacketUpdateConfig;
import com.tattyseal.compactstorage.exception.InvalidConfigurationException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;


import java.io.File;
import java.util.List;
import java.util.Arrays;

/**
 * Created by Toby on 07/11/2014.
 */
public class ConfigurationHandler
{
    // This cached config will be sent to logged in clients.
    public static ConfigurationState configForServer;

    public static Configuration configuration;
    public static File configFile;


    public static boolean firstTimeRun;
    public static boolean newFeatures;

    public static ItemStack storage;
    public static ItemStack storageBackpack;

    public static ItemStack[] primary;
    public static ItemStack[] secondary;

    public static ItemStack binder;
    public static ItemStack binderBackpack;

    public static float storageModifier;
    public static float primaryModifier;
    public static float secondaryModifier;
    public static float binderModifier;

    public static boolean shouldConnect;

    public static int capacityBarrel;
    public static int capacityDrum;


    public static class EventHandler
    {
        @SubscribeEvent
        public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
            if(eventArgs.getModID().equals("compactstorage"))
                ConfigurationHandler.refresh();
        }
    }

    public static void disableMessage()
    {
        newFeatures = false;
        configuration.get("internal", "newFeatures", true).set(false);
        configuration.save();
    }

    // Called to (partially) set up the Configuration object
    // and cache its state for connecting clients.
    public static void loadConfig(Configuration configuration)
    {
        firstTimeRun = configuration.getBoolean("firstTimeRun", "internal", false, "This is used internally for the GUI shown when you first start the game.");
        if(firstTimeRun) configuration.get("internal", "firstTimeRun", false).set(false);
        newFeatures = configuration.getBoolean("newFeatures", "internal", true, "Should the text be shown on startup informing you about new features?");

        configForServer.storage = configuration.getString("chestStorage", "builder", "minecraft:chest", "This is used as the first component in the Builder when building a CHEST.");
        configForServer.storageBackpack = configuration.getString("backpackStorage", "builder", "minecraft:wool", "This is used as the first component in the Builder when building a BACKPACK.");

        configForServer.primary = configuration.getStringList("primaryItem", "builder", new String[]{"minecraft:iron_ingot"}, "These values are used for the first material cost in the chest builder, you can add as many values as you like, it will configure itself to use all of them.");
        configForServer.secondary = configuration.getStringList("secondaryItem", "builder", new String[]{"minecraft:iron_bars"}, "These values are used for the second material cost in the chest builder, you can add as many values as you like, it will configure itself to use all of them.");

        configForServer.binder = configuration.getString("chestBinder", "builder", "minecraft:clay_ball", "This is used as the binder material when making a CHEST.");
        configForServer.binderBackpack = configuration.getString("backpackBinder", "builder", "minecraft:string", "This is used as the binder material when making a BACKPACK.");

        configForServer.storageModifier = configuration.getFloat("storageModifier", "builder", 1F, 0F, 1F, "This determines how much of the item is required.");
        configForServer.primaryModifier = configuration.getFloat("primaryModifier", "builder", 1F, 0F, 1F, "This determines how much of the item is required.");
        configForServer.secondaryModifier = configuration.getFloat("secondaryModifier", "builder", 1F, 0F, 1F, "This determines how much of the item is required.");
        configForServer.binderModifier = configuration.getFloat("binderModifier", "builder", 1F, 0F, 1F, "This determines how much of the item is required.");

        configForServer.shouldConnect = configuration.getBoolean("shouldConnectToNetworks", "chest", true, "This determines whether chests will connect to ES networks.");

		configForServer.capacityBarrel = configuration.getInt("barrelCapacity", "barrel/drum", 64, 0, 1024, "This determines how many stacks can be stored in a barrel.");
		configForServer.capacityDrum = configuration.getInt("drumCapacity", "barrel/drum", 32, 0, 1024, "This determines how many buckets can be stored in a drum.");
    }

    // Sets the effective configuration. Call either with configForServer (logical server)
    // or with a ConfigurationState object received from the server (logical client).
    public static void updateConfig(ConfigurationState newConfig)
    {
        storage = getItemFromString(newConfig.storage, "chestStorage", "minecraft:chest");
        storageBackpack = getItemFromString(newConfig.storageBackpack, "backpackStorage", "minecraft:wool");

        primary = getItemsFromStringList(newConfig.primary, "primaryItem", new String[]{"minecraft:iron_ingot"});
        secondary = getItemsFromStringList(newConfig.secondary, "secondaryItem", new String[]{"minecraft:iron_bars"});

        binder = getItemFromString(newConfig.binder, "chestBinder", "minecraft:clay_ball");
        binderBackpack = getItemFromString(newConfig.binderBackpack, "backpackBinder", "minecraft:string");

        storageModifier = newConfig.storageModifier;
        primaryModifier = newConfig.primaryModifier;
        secondaryModifier = newConfig.secondaryModifier;
        binderModifier = newConfig.binderModifier;

        shouldConnect = newConfig.shouldConnect;

		capacityBarrel = newConfig.capacityBarrel;
		capacityDrum = newConfig.capacityDrum;
    }

    // Called once to initialize the Configuration object.
    public static void init()
    {
        configuration = new Configuration(configFile);
        configForServer = new ConfigurationState();

        // Set up the Configuration object and cache its state.
        loadConfig(configuration);

        // This is done only once, hence it's not in the loadConfig method.
        configuration.setCategoryComment("builder", "Format for item names is modid:name@meta or leave @meta for all possible metadata of that item. These are not unlocalized names. If you do something wrong or it uses the defaut values check your log!!! Look for an InvalidConfigurationException and it will tell you why!");
        configuration.setCategoryPropertyOrder("builder", Arrays.asList(
            "chestStorage", "backpackStorage", "primaryItem", "secondaryItem", "chestBinder", "backpackBinder",
            "storageModifier", "primaryModifier", "secondaryModifier", "binderModifier"
        ));

        if(configuration.hasChanged()) configuration.save();

        // Initially, our config is the effective config.
        // Later, we may receive a new config from server.
        updateConfig(configForServer);
    }

    // Called to load and cache the changes when the Configuration object is updated.
    public static void refresh()
    {
        loadConfig(configuration);
        
        if(configuration.hasChanged()) configuration.save();

        if(CompactStorage.isIntegratedServer())
        {
            // Update these config's only if running an integrated server.
            updateConfig(configForServer);
            CompactStorage.instance.wrapper.sendToAll(new S03PacketUpdateConfig(configForServer));
        }
    }


    private static ItemStack getItemByName(String itemName)
    {
        if (itemName == null) return null;
        
        String modId = itemName.contains(":") ? itemName.split(":", 2)[0] : "minecraft";
        String itemId = itemName.contains(":") ? itemName.split(":", 2)[1] : itemName;
        int meta;

        if(itemName.contains("@"))
        {
            meta = Integer.parseInt(itemId.split("@")[1]);
            itemId = itemId.split("@")[0];
        }
        else
        {
            meta = OreDictionary.WILDCARD_VALUE;
        }

        Item item = Item.REGISTRY.getObject(new ResourceLocation(modId, itemId));

        return item == null ? null : new ItemStack(item, 1, meta);
    }
    
    private static ItemStack getItemFromString(String itemName, String propertyName, String defaultString)
    {
        ItemStack item = getItemByName(itemName);

        if(item != null) return item;

        new InvalidConfigurationException("Could not find item " + itemName + " for property " + propertyName + " in the CompactStorage config! Reverting to default.").printStackTrace();

        return getItemByName(defaultString);
    }

    private static ItemStack[] getItemsFromStringList(String[] itemNames, String propertyName, String[] defaultItems)
    {
        List<ItemStack> items = Lists.newArrayList();

        boolean breakOff = false;

        for(String itemName : itemNames)
        {
            ItemStack item = getItemFromString(itemName, propertyName, null);

            if (item == null)
            {
                breakOff = true;
                break;
            }

            items.add(item);
        }

        if(breakOff)
        {
            items.clear();

            for(String itemName : defaultItems)
                items.add(getItemByName(itemName));
        }

        return items.toArray(new ItemStack[items.size()]);
    }
}
