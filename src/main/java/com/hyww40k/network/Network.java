package com.hyww40k.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class Network {
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation("hyw_w40k_bridge", "main"))
            .networkProtocolVersion(() -> PROTOCOL)
            .clientAcceptedVersions(PROTOCOL::equals)
            .serverAcceptedVersions(PROTOCOL::equals)
            .simpleChannel();

    private static int id = 0;

    private Network() {}

    public static void register() {
        CHANNEL.messageBuilder(C2SAttackCommand.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SAttackCommand::new)
                .encoder(C2SAttackCommand::encode)
                .consumerMainThread(C2SAttackCommand::handle)
                .add();
    }
}
