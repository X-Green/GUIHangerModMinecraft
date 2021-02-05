package dev.eeasee.hud_hanger.network;

import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.PacketByteBuf;

public class HUDHangerClientNetworkHandler {

    public static void handleData(PacketByteBuf data) {
        if (data != null) {
            byte id = data.readByte();
            if (id == HUDHangerClient.HI)
                onHi(data);
            if (id == HUDHangerClient.DATA)
                onSyncData(data);
        }
    }

    private static void onHi(PacketByteBuf data) {
        synchronized (HUDHangerClient.sync) {
            HUDHangerClient.isServerSupported = true;
            if (MinecraftClient.getInstance().player != null) {
                respondHello(MinecraftClient.getInstance().player);
            }
        }
    }

    public static void respondHello(ClientPlayerEntity clientPlayerEntity) {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeByte(HUDHangerClient.HELLO);
        clientPlayerEntity.networkHandler.sendPacket(new CustomPayloadC2SPacket(
                HUDHangerClient.HUD_HANGER_CHANNEL, buffer));
    }

    private static void onSyncData(PacketByteBuf data) {
        int objectID = data.readVarInt();
        while (true) {
            int key = data.readByte();
            if (key <= 0) {
                break;
            }
            PacketDataType dataType = PacketDataType.VALUES[key];
            dataType.packetReader.accept(data, objectID);
        }
    }
}