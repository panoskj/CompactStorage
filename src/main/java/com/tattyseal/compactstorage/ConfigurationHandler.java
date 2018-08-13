package com.tattyseal.compactstorage;

import com.google.common.collect.Lists;
import com.tattyseal.compactstorage.CompactStorage;
import com.tattyseal.compactstorage.ConfigurationState;
import com.tattyseal.compactstorage.network.packet.C03PacketUpdateConfig;
import com.tattyseal.compactstorage.exception.InvalidConfigurationException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.util.List;
import java.util.Arrays;

/**
 * Created by Toby on 07/11/2014.
 */
public class ConfigurationHandler
{
    private static ConfigurationState localConfig;
    
    public static Configuration configuration;
    public static File configFile;

    public static boolean firstTimeRun;

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

    public static boolean newFeatures;

    public static int capacityBarrel;
    public static int capacityDrum;

    public static class EventHandler
    {
        @SubscribeEvent
        public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
            if(eventArgs.getModID().equals("compactstorage"))
                ConfigurationHandler.refresh();
        }

        @SubscribeEvent
        public void onPlayerLoggedIn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
            if (event.player instanceof net.minecraft.entity.player.EntityPlayerMP)
                CompactStorage.instance.wrapper.sendTo(new C03PacketUpdateConfig(localConfig), (net.minecraft.entity.player.EntityPlayerMP)event.player);
        }
    }

    public static void disableMessage()
    {
        newFeatures = false;
        configuration.get("internal", "newFeatures", true).set(false);
        configuration.save();
    }

    public static void loadConfig(Configuration configuration)
    {
        firstTimeRun = configuration.getBoolean("firstTimeRun", "internal", false, "This is used internally for the GUI shown when you first start the game.");
        if(firstTimeRun) configuration.get("internal", "firstTimeRun", false).set(false);
        newFeatures = configuration.getBoolean("newFeatures", "internal", true, "Should the text be shown on startup informing you about new features?");

        localConfig.storage = configuration.getString("chestStorage", "builder", "minecraft:chest", "This is used as the first component in the Builder when building a CHEST.");
        localConfig.storageBackpack = configuration.getString("backpackStorage", "builder", "minecraft:wool", "This is used as the first component in the Builder when building a BACKPACK.");

        localConfig.primary = configuration.getStringList("primaryItem", "builder", new String[]{"minecraft:iron_ingot"}, "These values are used for the first material cost in the chest builder, you can add as many values as you like, it will configure itself to use all of them.");
        localConfig.secondary = configuration.getStringList("secondaryItem", "builder", new String[]{"minecraft:iron_bars"}, "These values are used for the second material cost in the chest builder, you can add as many values as you like, it will configure itself to use all of them.");

        localConfig.binder = configuration.getString("chestBinder", "builder", "minecraft:clay_ball", "This is used as the binder material when making a CHEST.");
        localConfig.binderBackpack = configuration.getString("backpackBinder", "builder", "minecraft:string", "This is used as the binder material when making a BACKPACK.");

        localConfig.storageModifier = configuration.getFloat("storageModifier", "builder", 1F, 0F, 1F, "This determines how much of the item is required.");
        localConfig.primaryModifier = configuration.getFloat("primaryModifier", "builder", 1F, 0F, 1F, "This determines how much of the item is required.");
        localConfig.secondaryModifier = configuration.getFloat("secondaryModifier", "builder", 1F, 0F, 1F, "This determines how much of the item is required.");
        localConfig.binderModifier = configuration.getFloat("binderModifier", "builder", 1F, 0F, 1F, "This determines how much of the item is required.");

        localConfig.shouldConnect = configuration.getBoolean("shouldConnectToNetworks", "chest", true, "This determines whether chests will connect to ES networks.");

		localConfig.capacityBarrel = configuration.getInt("capacityBarrel", "barrel/drum", 64, 0, 1024, "This determines how many stacks can be stored in a barrel.");
		localConfig.capacityDrum = configuration.getInt("capacityDrum", "barrel/drum", 32, 0, 1024, "This determines how many buckets can be stored in a drum.");
    }
    
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

    public static void init()
    {
        configuration = new Configuration(configFile);
        localConfig = new ConfigurationState();
        
        loadConfig(configuration);

        configuration.setCategoryComment("builder", "Format for item names is modid:name@meta or leave @meta for all possible metadata of that item. These are not unlocalized names. If you do something wrong or it uses the defaut values check your log!!! Look for an InvalidConfigurationException and it will tell you why!");

        configuration.setCategoryPropertyOrder("builder", Arrays.asList(
            "chestStorage", "backpackStorage", "primaryItem", "secondaryItem", "chestBinder", "backpackBinder",
            "storageModifier", "primaryModifier", "secondaryModifier", "binderModifier"
        ));

        if(configuration.hasChanged()) configuration.save();

        updateConfig(localConfig);
    }

    public static void refresh()
    {
        loadConfig(configuration);
        
        if(configuration.hasChanged()) configuration.save();

        MinecraftServer server = net.minecraftforge.fml.common.FMLCommonHandler.instance().getMinecraftServerInstance();
        
        if(server != null && !server.isDedicatedServer())
        {
            // Update these config's only if running an integrated server.
            updateConfig(localConfig);
            CompactStorage.instance.wrapper.sendToAll(new C03PacketUpdateConfig(localConfig));
        }
    }

    public static ItemStack getItemFromString(String itemName, String propertyName, String defaultString)
    {
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

        if(item == null)
        {
            new InvalidConfigurationException("Could not find item " + itemName + " for property " + propertyName + " in the CompactStorage config! Reverting to default.").printStackTrace();


            modId = defaultString.contains(":") ? defaultString.split(":", 2)[0] : "minecraft";
            itemId = defaultString.contains(":") ? defaultString.split(":", 2)[1] : defaultString;

            if(itemName.contains("@"))
            {
                meta = Integer.parseInt(itemId.split("@")[1]);
                itemId = itemId.split("@")[0];
            }
            else
            {
                meta = OreDictionary.WILDCARD_VALUE;
            }
            item = Item.REGISTRY.getObject(new ResourceLocation(modId, itemId));

            return new ItemStack(item, 1, meta);
        }

        return new ItemStack(item, 1, meta);
    }

    public static ItemStack[] getItemsFromStringList(String[] itemNames, String propertyName, String[] defaultItems)
    {
        List<ItemStack> items = Lists.newArrayList();

        boolean breakOff = false;

        for(String itemName : itemNames)
        {
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

            if(item == null)
            {
                new InvalidConfigurationException("Could not find item " + itemName + " for property " + propertyName + " in the CompactStorage config! Reverting to default.").printStackTrace();
                breakOff = true;
                break;
            }

            items.add(new ItemStack(item, 1, meta));
        }

        if(breakOff)
        {
            items.clear();

            for(String itemName : defaultItems)
            {
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
                items.add(new ItemStack(item, 1, meta));
            }
        }

        return items.toArray(new ItemStack[items.size()]);
    }
}
