package com.raysfanatic02.venomatorbow;

import net.runelite.client.config.*;

import java.awt.Color;

@ConfigGroup("venomatorbow")
public interface VenomatorBowConfig extends Config
{
    @ConfigItem(
        keyName = "onlyOnTask",
        name = "Only on Slayer task",
        description = "Only show text on NPCs that match your current Slayer task."
    )
    default boolean onlyOnTask() { return true; }

    @ConfigSection(
        name = "Tagged",
        description = "Tagged state options",
        position = 0
    )
    String taggedSection = "taggedSection";

    @ConfigItem(
        keyName = "showTagged",
        name = "Show tagged",
        description = "Show tagged state text when the NPC is interacted with and receives a hitsplat.",
        section = taggedSection,
        position = 0
    )
    default boolean showTagged() { return true; }

    @ConfigItem(
        keyName = "taggedText",
        name = "Tagged text",
        description = "Text to show for tagged state (e.g. T or Tagged).",
        section = taggedSection,
        position = 1
    )
    default String taggedText() { return "T"; }

    @ConfigItem(
        keyName = "taggedColor",
        name = "Tagged color",
        description = "Color for tagged text.",
        section = taggedSection,
        position = 2
    )
    default Color taggedColor() { return new Color(120, 200, 255); } // light blue

    @ConfigSection(
        name = "Poisoned",
        description = "Poisoned state options",
        position = 1
    )
    String poisonedSection = "poisonedSection";

    @ConfigItem(
        keyName = "showPoisoned",
        name = "Show poisoned",
        description = "Show poisoned state text when poison hitsplat is observed.",
        section = poisonedSection,
        position = 0
    )
    default boolean showPoisoned() { return true; }

    @ConfigItem(
        keyName = "poisonedText",
        name = "Poisoned text",
        description = "Text to show for poisoned state (e.g. P).",
        section = poisonedSection,
        position = 1
    )
    default String poisonedText() { return "P"; }

    @ConfigItem(
        keyName = "poisonedColor",
        name = "Poisoned color",
        description = "Color for poisoned text.",
        section = poisonedSection,
        position = 2
    )
    default Color poisonedColor() { return new Color(0, 200, 0); }

    @ConfigSection(
        name = "Venomed",
        description = "Venomed state options",
        position = 2
    )
    String venomedSection = "venomedSection";

    @ConfigItem(
        keyName = "showVenomed",
        name = "Show venomed",
        description = "Show venomed state text when venom hitsplat is observed.",
        section = venomedSection,
        position = 0
    )
    default boolean showVenomed() { return true; }

    @ConfigItem(
        keyName = "venomedText",
        name = "Venomed text",
        description = "Text to show for venomed state (e.g. V).",
        section = venomedSection,
        position = 1
    )
    default String venomedText() { return "V"; }

    @ConfigItem(
        keyName = "venomedColor",
        name = "Venomed color",
        description = "Color for venomed text.",
        section = venomedSection,
        position = 2
    )
    default Color venomedColor() { return new Color(0, 255, 0); } // bright green

    @ConfigSection(
        name = "Overlay",
        description = "Overlay positioning options",
        position = 3
    )
    String overlaySection = "overlaySection";

    @Range(min = -60, max = 60)
    @ConfigItem(
        keyName = "yOffset",
        name = "Text Y offset",
        description = "Moves the text up/down relative to the NPC.",
        section = overlaySection,
        position = 0
    )
    default int yOffset() { return 0; }

    @ConfigItem(
        keyName = "shadow",
        name = "Text shadow",
        description = "Draw a simple black shadow behind text for visibility.",
        section = overlaySection,
        position = 1
    )
    default boolean shadow() { return true; }
}
