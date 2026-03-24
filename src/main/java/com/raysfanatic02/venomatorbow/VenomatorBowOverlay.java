package com.raysfanatic02.venomatorbow;

import com.google.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

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
        Font originalFont = graphics.getFont();

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

            Font derivedFont = config.boldText()
                ? originalFont.deriveFont(Font.BOLD, (float) spec.fontSize)
                : originalFont.deriveFont(Font.PLAIN, (float) spec.fontSize);

            graphics.setFont(derivedFont);

            Point p = npc.getCanvasTextLocation(graphics, spec.text, config.yOffset());
            if (p == null)
            {
                continue;
            }

            Point adjusted = new Point(p.getX() + config.xOffset(), p.getY());

            if (config.shadow())
            {
                renderText(graphics, new Point(adjusted.getX() + 1, adjusted.getY() + 1), spec.text, Color.BLACK);
            }

            renderText(graphics, adjusted, spec.text, spec.color);
        }

        graphics.setFont(originalFont);
        return null;
    }

    private void renderText(Graphics2D graphics, Point point, String text, Color color)
    {
        graphics.setColor(color);
        graphics.drawString(text, point.getX(), point.getY());
    }

    private RenderSpec getRenderSpec(VenomatorBowPlugin.State state)
    {
        switch (state)
        {
            case VENOMED:
                return config.showVenomed()
                    ? new RenderSpec(config.venomedText(), config.venomedColor(), config.venomedTextSize())
                    : null;
            case POISONED:
                return config.showPoisoned()
                    ? new RenderSpec(config.poisonedText(), config.poisonedColor(), config.poisonedTextSize())
                    : null;
            case TAGGED:
                return config.showTagged()
                    ? new RenderSpec(config.taggedText(), config.taggedColor(), config.taggedTextSize())
                    : null;
            default:
                return null;
        }
    }

    private static class RenderSpec
    {
        final String text;
        final Color color;
        final int fontSize;

        RenderSpec(String text, Color color, int fontSize)
        {
            this.text = text == null ? "" : text;
            this.color = color == null ? Color.WHITE : color;
            this.fontSize = fontSize;
        }
    }
}
