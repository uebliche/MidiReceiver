package net.uebliche.midiReceiver;

public record MidiAction(
String name,
int command,
int channel,
int data1,
int data2
) {
}
