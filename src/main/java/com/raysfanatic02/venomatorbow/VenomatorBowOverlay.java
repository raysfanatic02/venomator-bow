package com.raysfanatic02.venomatorbow;

import com.google.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.*;

import java.awt.*;
import java.util.Map;

public class VenomatorBowOverlay extends Overlay
{
    private final VenomatorBowPlugin plugin;
    private final VenomatorBowConfig config;

    @Inject
    public VenomatorBowOverlay(VenomatorBowPlugin plugin, VenomatorBowConfig config)
    {
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        for (Map.Entry<Integer, VenomatorBowPlugin.State> e : plugin.getStates().entrySet())
        {
            NPC npc = plugin.getNpcByIndex(e.getKey());
            if (npc == null)
            {
                continue;
            }

            RenderSpec spec = getRenderSpec(e.getValue());
            if (spec == null || spec.text.isBlank())
            {
                continue;
            }

            Point p = npc.getCanvasTextLocation(graphics, spec.text, config.yOffset());
            if (p == null)
            {
                continue;
            }

            if (config.shadow())
            {
                // simple 1px shadow for contrast
                renderText(graphics, new Point(p.getX() + 1, p.getY() + 1), spec.text, Color.BLACK);
            }

            renderText(graphics, p, spec.text, spec.color);
        }

        return null;
    }

    private void renderText(Graphics2D g, Point p, String text, Color color)
    {
        g.setColor(color);
        g.drawString(text, p.getX(), p.getY());
    }

    private RenderSpec getRenderSpec(VenomatorBowPlugin.State s)
    {
        // Priority: VENOMED > POISONED > TAGGED
        switch (s)
        {
            case VENOMED:
                return config.showVenomed() ? new RenderSpec(config.venomedText(), config.venomedColor()) : null;
            case POISONED:
                return config.showPoisoned() ? new RenderSpec(config.poisonedText(), config.poisonedColor()) : null;
            case TAGGED:
                return config.showTagged() ? new RenderSpec(config.taggedText(), config.taggedColor()) : null;
            default:
                return null;
        }
    }

    private static class RenderSpec
    {
        final String text;
        final Color color;

        RenderSpec(String text, Color color)
        {
            this.text = text == null ? "" : text;
            this.color = color == null ? Color.WHITE : color;
        }
    }
}
