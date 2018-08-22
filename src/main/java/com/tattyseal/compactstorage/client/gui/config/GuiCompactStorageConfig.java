package com.tattyseal.compactstorage.client.gui.config;

import com.tattyseal.compactstorage.ConfigurationHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tobystrong on 03/05/2017.
 */
public class GuiCompactStorageConfig extends GuiConfig
{
    private static List<IConfigElement> elementList;

    static
    {
        elementList = new ArrayList<IConfigElement>();
        elementList.add(new ConfigElement(ConfigurationHandler.configuration.getCategory("internal")));
        elementList.add(new ConfigElement(ConfigurationHandler.configuration.getCategory("builder")));
        elementList.add(new ConfigElement(ConfigurationHandler.configuration.getCategory("chest")));
        elementList.add(new ConfigElement(ConfigurationHandler.configuration.getCategory("barrel/drum")));
    }

    public GuiCompactStorageConfig(GuiScreen parentScreen, String modid, String title)
    {
        super(parentScreen, elementList, modid, title, false, false, GuiConfig.getAbridgedConfigPath(ConfigurationHandler.configuration.toString()));
    }
}
