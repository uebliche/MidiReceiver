package net.uebliche.midiReceiver;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerMidiActionEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
    private final Player player;
    private final MidiAction action;
    
    public PlayerMidiActionEvent(Player player, MidiAction action) {
        this.player = player;
        this.action = action;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public MidiAction getAction() {
        return action;
    }
}
