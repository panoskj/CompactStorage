package com.tattyseal.compactstorage.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class RenderUtil
{
    public static final ResourceLocation slotTexture = new ResourceLocation("compactstorage", "textures/gui/chestSlots.png");
    public static final ResourceLocation backgroundTexture = new ResourceLocation("compactstorage", "textures/gui/chest.png");
    public static final ResourceLocation colorTexture = new ResourceLocation("compactstorage", "textures/gui/colorGrid.png");
    private static double slotTextureWidth = 432d;
    private static double slotTextureHeight = 216d;
    private static double chestTextureWidth = 15d;
    private static double chestTextureHeight = 15d;

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void renderSlots(int x, int y, int width, int height)
    {
        renderSlots(Color.white, x, y, width, height);
    }

    public static void renderSlots(Color color, int x, int y, int width, int height)
    {
        mc.renderEngine.bindTexture(slotTexture);

        int realWidth = (width * 18);
        int realHeight = (height * 18);

        double ux = (1D / slotTextureWidth) * realWidth;
        double uz = (1D / slotTextureHeight) * realHeight;

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldRenderer.pos(x + 0, y + realHeight, 0).tex(0, uz).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
        worldRenderer.pos(x + realWidth, y + realHeight, 0).tex(ux, uz).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();//(1 / slotTextureWidth) * (width), (1 / slotTextureHeight) * (height));
        worldRenderer.pos(x + realWidth, y + 0, 0).tex(ux, 0).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();//1 / slotTextureWidth) * (width), 0);
        worldRenderer.pos(x + 0, y + 0, 0).tex(0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
        tessellator.draw();
    }

    public static void renderChestBackground(Color color, GuiContainer gui, int x, int y, int width, int height)
    {
        renderBackground(color, gui, x, y, Math.max(9, width) * 18, height * 18);
    }

    public static void renderBackground(Color color, GuiContainer gui, int x, int y, int width, int height)
    {
        mc.renderEngine.bindTexture(backgroundTexture);

        int realWidth = 7 + (width) + 7;
        int realHeight = 15 + (height) + 13 + 54 + 4 + 18 + 7;

        int by = y + (realHeight - 7);

        renderPartBackground(color, x, y, 0, 0, 7, 7, 7, 7);
        renderPartBackground(color, x + 7, y, 8, 0, 8, 7, (width), 7);
        renderPartBackground(color, x + 7 + (width), y, 9, 0, 15, 7, 7, 7);

        renderPartBackground(color, x, by, 0, 8, 7, 15, 7, 7);
        renderPartBackground(color, x + 7, by, 8, 8, 7, 15, (width), 7);
        renderPartBackground(color, x + 7 + (width), by, 9, 8, 15, 15, 7, 7);

        renderPartBackground(color, x, y + 7, 0, 7, 7, 7, 7, (realHeight - 14));
        renderPartBackground(color, x + realWidth - 8, y + 7, 8, 7, 15, 7, 8, (realHeight - 14));

        renderPartBackground(color, x + 7, y + 7, 8, 8, 8, 8, (width), realHeight - 14);
    }

    private static void renderPartBackground(Color color, int x, int y, int startX, int startY, int endX, int endY, int width, int height)
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        worldRenderer.pos((double) x, (double) y + height, 0).tex(getEnd(chestTextureWidth, startX), getEnd(chestTextureHeight, endY)).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
        worldRenderer.pos((double) x + width, (double) y + height, 0).tex(getEnd(chestTextureWidth, endX), getEnd(chestTextureHeight, endY)).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
        worldRenderer.pos((double) x + width, (double) y + 0, 0).tex(getEnd(chestTextureWidth, endX), getEnd(chestTextureHeight, startY)).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
        worldRenderer.pos((double) x, (double) y, 0).tex(getEnd(chestTextureWidth, startX), getEnd(chestTextureHeight, startY)).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();

        tessellator.draw();
    }

    private static double getEnd(double width, double other)
    {
        return (1D / width) * other;
    }

    public static void drawTexturedQuadFit(double x, double y, double width, double height, double zLevel)
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(x + 0, y + height, zLevel).tex(0,1).endVertex();
        worldRenderer.pos(x + width, y + height, zLevel).tex(1, 1).endVertex();
        worldRenderer.pos(x + width, y + 0, zLevel).tex(1,0).endVertex();
        worldRenderer.pos(x + 0, y + 0, zLevel).tex(0, 0).endVertex();
        tessellator.draw();
    }

    public static boolean shouldContrast(Color color)
    {
        double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return y >= 128 ? true : false;
    }

    public static Color getContrastColor(Color color)
    {
        return shouldContrast(color) ? Color.black : Color.white;
    }
}
