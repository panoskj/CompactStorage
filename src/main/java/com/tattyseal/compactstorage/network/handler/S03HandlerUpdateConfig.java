package com.tattyseal.compactstorage.network.handler;

import com.tattyseal.compactstorage.network.packet.C03PacketUpdateConfig;

import com.tattyseal.compactstorage.ConfigurationHandler;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraft.client.Minecraft;

public class C03HandlerUpdateConfig implements IMessageHandler<C03PacketUpdateConfig, IMessage>
{
	@Override
	public IMessage onMessage(final C03PacketUpdateConfig message, MessageContext ctx)
	{
		if(!ctx.side.equals(Side.CLIENT))
			return null;

		Minecraft.getMinecraft().addScheduledTask(new Runnable()
		{
			@Override
			public void run() 
			{
				System.out.println("Received message to update config!!!");
				ConfigurationHandler.updateConfig(message.config);
			}
		});

		return null;
	}
}
