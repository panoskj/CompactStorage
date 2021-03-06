package com.tattyseal.compactstorage.proxy;

import com.tattyseal.compactstorage.CompactStorage;
import com.tattyseal.compactstorage.block.BlockBarrel;
import com.tattyseal.compactstorage.block.BlockChest;
import com.tattyseal.compactstorage.client.render.TileEntityBarrelFluidRenderer;
import com.tattyseal.compactstorage.client.render.TileEntityBarrelRenderer;
import com.tattyseal.compactstorage.client.render.TileEntityChestRenderer;
import com.tattyseal.compactstorage.event.ConnectionHandler;
import com.tattyseal.compactstorage.item.ItemBackpack;
import com.tattyseal.compactstorage.item.ItemBlockChest;
import com.tattyseal.compactstorage.tileentity.IBarrel;
import com.tattyseal.compactstorage.tileentity.TileEntityBarrel;
import com.tattyseal.compactstorage.tileentity.TileEntityBarrelFluid;
import com.tattyseal.compactstorage.tileentity.TileEntityChest;
import com.tattyseal.compactstorage.util.LogHelper;
import com.tattyseal.compactstorage.util.ModelUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nullable;
import java.awt.Color;

/**
 * Created by Toby on 06/11/2014.
 */
public class ClientProxy implements IProxy
{
    public void registerRenderers()
    {

        ItemColors itemColors = Minecraft.getMinecraft().getItemColors();

        itemColors.registerItemColorHandler(new IItemColor() {
            @Override
            public int colorMultiplier(ItemStack stack, int color)
            {
                return CompactStorage.getColorFromNBT(stack);
            }
        }, CompactStorage.ModItems.backpack, Item.getItemFromBlock(CompactStorage.ModBlocks.chest));

    }


}
