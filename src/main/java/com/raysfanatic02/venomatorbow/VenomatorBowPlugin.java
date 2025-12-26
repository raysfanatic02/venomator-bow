package com.raysfanatic02.venomatorbow;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.*;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@PluginDescriptor(
    name = "Venomator Bow",
    description = "Shows configurable text over Slayer task NPCs to indicate tagged, poisoned, or venomed targets",
    tags = {"slayer", "venom", "poison", "overlay", "venator"}
)
public class VenomatorBowPlugin extends Plugin
{
    enum State { NONE, TAGGED, POISONED, VENOMED }

    @Inject private Client client;
    @Inject private OverlayManager overlayManager;
    @Inject private VenomatorBowOverlay overlay;
    @Inject private VenomatorBowConfig config;

    @Getter
    private final Map<Integer, State> states = new HashMap<>();

    // Slayer task detection via VarPlayer
    private String slayerTaskCreatureName = null;
    private int slayerTaskRemaining = 0;

    @Provides
    VenomatorBowConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(VenomatorBowConfig.class);
    }

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
        states.clear();
        refreshTaskFromVars();
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        states.clear();
        slayerTaskCreatureName = null;
        slayerTaskRemaining = 0;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged e)
    {
        if (e.getGameState() == GameState.LOGGED_IN)
        {
            refreshTaskFromVars();
        }
        if (e.getGameState() == GameState.LOGIN_SCREEN || e.getGameState() == GameState.HOPPING)
        {
            states.clear();
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged e)
    {
        // Fires often; refresh is cheap
        refreshTaskFromVars();
    }

    private void refreshTaskFromVars()
    {
        int creatureId = client.getVarpValue(VarPlayer.SLAYER_TASK_CREATURE);
        slayerTaskRemaining = client.getVarpValue(VarPlayer.SLAYER_TASK_SIZE);

        slayerTaskCreatureName = null;

        if (slayerTaskRemaining > 0 && creatureId > 0)
        {
            NPCComposition comp = client.getNpcDefinition(creatureId);
            if (comp != null && comp.getName() != null)
            {
                slayerTaskCreatureName = sanitize(comp.getName());
            }
        }
    }

    private String sanitize(String s)
    {
        return s.toLowerCase(Locale.ROOT).trim();
    }

    private boolean isOnTask(NPC npc)
    {
        if (!config.onlyOnTask())
        {
            return true;
        }

        if (slayerTaskCreatureName == null || slayerTaskCreatureName.isBlank())
        {
            return false;
        }

        String npcName = npc.getName();
        if (npcName == null)
        {
            return false;
        }

        // Loose match: handles pluralization/variants reasonably well
        String n = sanitize(npcName);
        return n.contains(slayerTaskCreatureName) || slayerTaskCreatureName.contains(n);
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned e)
    {
        NPC npc = e.getNpc();
        if (isOnTask(npc))
        {
            states.put(npc.getIndex(), State.NONE);
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned e)
    {
        states.remove(e.getNpc().getIndex());
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied e)
    {
        if (!(e.getActor() instanceof NPC))
        {
            return;
        }

        NPC npc = (NPC) e.getActor();
        int idx = npc.getIndex();

        // Ensure we only consider task NPCs (and allow “late tracking” for already-spawned NPCs)
        if (!states.containsKey(idx))
        {
            if (!isOnTask(npc))
            {
                return;
            }
            states.put(idx, State.NONE);
        }

        final int hitsplatType = e.getHitsplat().getHitsplatType();

        // Confirmed state transitions
        if (hitsplatType == HitsplatID.VENOM)
        {
            states.put(idx, State.VENOMED);
            return;
        }

        if (hitsplatType == HitsplatID.POISON)
        {
            if (states.get(idx) != State.VENOMED)
            {
                states.put(idx, State.POISONED);
            }
            return;
        }

        // Tagged heuristic:
        // If you are interacting with the NPC and it receives any hitsplat,
        // mark it tagged unless it's already poison/venom.
        Player me = client.getLocalPlayer();
        if (me != null && me.getInteracting() == npc)
        {
            State cur = states.get(idx);
            if (cur == State.NONE)
            {
                states.put(idx, State.TAGGED);
            }
        }
    }

    NPC getNpcByIndex(int index)
    {
        for (NPC npc : client.getNpcs())
        {
            if (npc != null && npc.getIndex() == index)
            {
                return npc;
            }
        }
        return null;
    }
}
