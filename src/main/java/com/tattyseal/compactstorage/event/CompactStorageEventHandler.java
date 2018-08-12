package com.tattyseal.compactstorage.event;

import com.tattyseal.compactstorage.CompactStorage;
import com.tattyseal.compactstorage.block.BlockBarrel;
import com.tattyseal.compactstorage.block.BlockChest;
import com.tattyseal.compactstorage.inventory.ContainerChest;
import com.tattyseal.compactstorage.inventory.InventoryBackpack;

import net.minecraft.block.Block;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CompactStorageEventHandler
{
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event)
	{
		Block block = event.getEntity().world.getBlockState(event.getPos()).getBlock();

		if(block instanceof BlockChest || block instanceof BlockBarrel)
		{
			event.setUseBlock(Result.ALLOW);
		}
	}
	
	@SubscribeEvent
	public void onItemPickupEvent(EntityItemPickupEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		
		if (player.getHeldItem(EnumHand.MAIN_HAND).getItem().equals(CompactStorage.ModItems.backpack))
		{
			InventoryBackpack inventory = new InventoryBackpack(player.getHeldItem(EnumHand.MAIN_HAND));
			
			ContainerChest container = new ContainerChest(player.world, inventory, player, new BlockPos(player.posX, player.posY, player.posZ));
			
			container.placeItemStack(event.getItem().getItem());
			
			inventory.closeInventory(player);
			
			if (event.getItem().getItem().isEmpty())
				// If the whole stack was picked up,
				// add it to stats and play the animation.
				event.setResult(Result.ALLOW);
			
			// Otherwise, adding the remaining stack to the inventory will be attempted.
			// Btw, vanilla doesn't add stats nor plays the animation when the player
			// doesn't pick up the whole stack.
		}
	}
}
