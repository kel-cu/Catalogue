package com.mrcrayfish.catalogue.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.IConfigurable;
import net.minecraftforge.forgespi.language.IModInfo;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ForgeModData implements IModData
{
    public static final ResourceLocation VERSION_CHECK_ICONS = ResourceLocation.fromNamespaceAndPath("forge", "textures/gui/version_check_icons.png");

    private final IModInfo info;
    private final Type type;

    public ForgeModData(IModInfo info)
    {
        this.info = info;
        this.type = analyzeType(info);
    }

    @Override
    public Type getType()
    {
        return this.type;
    }

    @Override
    public String getModId()
    {
        return this.info.getModId();
    }

    @Override
    public String getDisplayName()
    {
        return this.info.getDisplayName();
    }

    @Override
    public String getVersion()
    {
        return this.info.getVersion().toString();
    }

    @Override
    public String getDescription()
    {
        return this.info.getDescription();
    }

    @Override
    @Nullable
    public String getItemIcon()
    {
        String itemIcon = (String) this.info.getModProperties().get("catalogueItemIcon");
        if(itemIcon == null)
        {
            // Fallback to old method for backwards compatibility on Forge
            itemIcon = (String) ((ModInfo) this.info).getConfigElement("itemIcon").orElse(null);
        }
        return itemIcon;
    }

    @Nullable
    @Override
    public String getImageIcon()
    {
        return this.info.getModProperties().get("catalogueImageIcon") instanceof String s ? s : null;
    }

    @Override
    public String getLicense()
    {
        return this.info.getOwningFile().getLicense();
    }

    @Override
    public String getCredits()
    {
        return this.getConfigString("credits");
    }

    @Nullable
    @Override
    public String getAuthors()
    {
        return this.getConfigString("authors");
    }

    @Nullable
    @Override
    public String getHomepage()
    {
        return this.getConfigString("displayURL");
    }

    @Nullable
    @Override
    public String getIssueTracker()
    {
        return this.info.getOwningFile().getConfig().<String>getConfigElement("issueTrackerURL").orElse(null);
    }

    @Nullable
    @Override
    public String getBanner()
    {
        return this.info.getLogoFile().orElse(null);
    }

    @Nullable
    @Override
    public String getBackground()
    {
        Object properties = this.info.getOwningFile().getConfig().getConfigElement("modproperties", this.info.getModId());
        if(properties != null)
        {
            //.get("catalogueBackground")
        }
        return null;
    }

    @Override
    public Update getUpdate()
    {
        VersionChecker.CheckResult result = VersionChecker.getResult(this.info);
        if(result.status().shouldDraw())
        {
            return new Update(result.status().isAnimated(), result.url(), result.status().getSheetOffset(), VERSION_CHECK_ICONS);
        }
        return null;
    }

    @Override
    public boolean hasConfig()
    {
        return ConfigScreenHandler.getScreenFactoryFor(this.info).isPresent();
    }

    @Override
    public boolean isLogoSmooth()
    {
        return this.info.getLogoBlur();
    }

    @Override
    public boolean isInternal()
    {
        return this.info.getModId().equals("forge") || this.info.getModId().equals("minecraft");
    }

    @Override
    public void openConfigScreen(Screen parent)
    {
        ConfigScreenHandler.getScreenFactoryFor(this.info).map(f -> f.apply(Minecraft.getInstance(), parent)).ifPresent(newScreen -> Minecraft.getInstance().setScreen(newScreen));
    }

    @Override
    public void drawUpdateIcon(GuiGraphics graphics, Update update, int x, int y)
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int vOffset = update.animated() && (System.currentTimeMillis() / 800 & 1) == 1 ? 8 : 0;
        graphics.blit(RenderType::guiTextured, update.textures(), x, y, update.texOffset() * 8, vOffset, 8, 8, 64, 16);
    }

    @Nullable
    private String getConfigString(String key)
    {
        return ((ModInfo) this.info).getConfigElement(key).map(Object::toString).orElse(null);
    }

    private Type analyzeType(IModInfo info)
    {
        return switch(info.getOwningFile().getFile().getType())
        {
            case MOD -> Type.DEFAULT;
            case LIBRARY, LANGPROVIDER, GAMELIBRARY -> Type.LIBRARY;
        };
    }
}
