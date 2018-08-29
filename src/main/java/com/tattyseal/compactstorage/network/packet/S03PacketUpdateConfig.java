package com.tattyseal.compactstorage.network.packet;

import com.tattyseal.compactstorage.ConfigurationState;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class S03PacketUpdateConfig implements IMessage
{
	public S03PacketUpdateConfig()
	{
		this(null);
	}

	public S03PacketUpdateConfig(ConfigurationState config)
	{
		this.config = config;
	}

	public ConfigurationState config;

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		config = new ConfigurationState();

		config.storage = readString(buf);
		config.storageBackpack = readString(buf);
		config.primary = readStringList(buf);
		config.secondary = readStringList(buf);
		config.binder = readString(buf);
		config.binderBackpack = readString(buf);

		config.storageModifier = buf.readFloat();
		config.primaryModifier = buf.readFloat();
		config.secondaryModifier = buf.readFloat();
		config.binderModifier = buf.readFloat();

		config.shouldConnect = buf.readBoolean();

		config.capacityBarrel = buf.readInt();
		config.capacityDrum = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		if(config == null)
		{
			config = new ConfigurationState();
		}

		writeString(buf, config.storage);
		writeString(buf, config.storageBackpack);
		writeStringList(buf, config.primary);
		writeStringList(buf, config.secondary);
		writeString(buf, config.binder);
		writeString(buf, config.binderBackpack);

		buf.writeFloat(config.storageModifier);
		buf.writeFloat(config.primaryModifier);
		buf.writeFloat(config.secondaryModifier);
		buf.writeFloat(config.binderModifier);

		buf.writeBoolean(config.shouldConnect);

		buf.writeInt(config.capacityBarrel);
		buf.writeInt(config.capacityDrum);
	}
	
	private static void writeString(ByteBuf buf, String str)
	{
		buf.writeInt(str.length());

		for(int i = 0; i < str.length(); ++i)
			buf.writeChar(str.charAt(i));
	}

	private static void writeStringList(ByteBuf buf, String[] strs)
	{
		buf.writeInt(strs.length);
		
		for(String str : strs) writeString(buf, str);
	}

	private static String readString(ByteBuf buf)
	{
		int len = buf.readInt();

		StringBuilder sb = new StringBuilder(len);

		for(int i = 0; i < len; ++i)
			sb.append(buf.readChar());

		return sb.toString();
	}

	private static String[] readStringList(ByteBuf buf)
	{
		int len = buf.readInt();

		String[] strs = new String[len];

		for(int i = 0; i < len; ++i)
			strs[i] = readString(buf);

		return strs;
	}
}
