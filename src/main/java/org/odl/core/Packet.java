package org.odl.core;

public class Packet {
    public final String source;
    public final String destination;
    public final String message;

    public Packet(String source, String destination, String message) {
        this.source = source;
        this.destination = destination;
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Packet packet = (Packet) o;

        if (!destination.equals(packet.destination)) return false;
        if (!message.equals(packet.message)) return false;
        if (!source.equals(packet.source)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + destination.hashCode();
        result = 31 * result + message.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
