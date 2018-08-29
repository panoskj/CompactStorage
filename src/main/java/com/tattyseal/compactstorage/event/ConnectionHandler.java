package com.tattyseal.compactstorage.event;

import com.tattyseal.compactstorage.CompactStorage;
import com.tattyseal.compactstorage.ConfigurationHandler;
import com.tattyseal.compactstorage.network.packet.S03PacketUpdateConfig;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by tobystrong on 02/05/2017.
 */
public class ConnectionHandler
{
    @SubscribeEvent
    public void connect(PlayerEvent.PlayerLoggedInEvent e)
    {
        if(CompactStorage.isIntegratedServer() && ConfigurationHandler.newFeatures)
        {            
            e.player.sendMessage(new TextComponentString("What's new in CompactStorage 3.0?"));
            e.player.sendMessage(new TextComponentString(" - New barrels and drums!."));
            e.player.sendMessage(new TextComponentString(" - Barrels allow 64 stacks of one item to be stored in them, right click to insert and punch to remove items!"));
            e.player.sendMessage(new TextComponentString(" - Drums allow 32,000mB of storage, these can also be interfaced with pipes etc!"));
            e.player.sendMessage(new TextComponentString("This message will only be displayed once and can be re-enabled in the configuration file."));
            ConfigurationHandler.disableMessage();
        }

        if(e.player instanceof EntityPlayerMP)
            CompactStorage.instance.wrapper.sendTo(new S03PacketUpdateConfig(ConfigurationHandler.configForServer), (EntityPlayerMP)e.player);
    }
}
