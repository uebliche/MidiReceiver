package net.uebliche.midiReceiver;

import com.google.common.io.ByteStreams;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.IOException;

public final class MidiReceiver extends JavaPlugin implements PluginMessageListener, Listener {
    
    private static final Logger log = LoggerFactory.getLogger(MidiReceiver.class);
    
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getMessenger().registerIncomingPluginChannel(this, "midi:action", this);
    }
    
    @EventHandler
    public void onMidiEvent(PlayerMidiActionEvent event) {
        Player player = event.getPlayer();
        MidiAction action = event.getAction();
        if (action.command() != 144) // Key Down
            return;
        int midiNote = action.data1();
        int mcNote = midiNote - 54; // 54 = F#3 → Notenblock-Note 0
        if (mcNote < 0 || mcNote > 24) return;
        player.playNote(
        player.getLocation(),
        Instrument.PIANO,
        new Note(mcNote)
        );
    }
    
    
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] bytes) {
        if (!channel.equals("midi:action")) return;
        if (bytes.length < 2)  return;
        
        DataInput in = ByteStreams.newDataInput(bytes);
        try {
            String name = readMinecraftUtf(in);
            int command = in.readInt();
            int channelNum = in.readInt();
            int data1 = in.readInt();
            int data2 = in.readInt();
            MidiAction action = new MidiAction(name, command, channelNum, data1, data2);
            getServer().getPluginManager().callEvent(new PlayerMidiActionEvent(player, action));
        } catch (Exception e) {
            log.error("Exception while reading midi action", e);
        }
        
        
    }
    
    public static String readMinecraftUtf(DataInput in) throws IOException {
        int length = readVarInt(in);
        byte[] bytes = new byte[length];
        in.readFully(bytes);
        return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
    }
    
    public static int readVarInt(DataInput in) throws IOException {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            read = in.readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));
            
            numRead++;
            if (numRead > 5) throw new IOException("VarInt too big");
        } while ((read & 0b10000000) != 0);
        
        return result;
    }
}
