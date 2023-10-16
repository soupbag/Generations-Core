package generations.gg.generations.core.generationscore.network.packets.npc;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import generations.gg.generations.core.generationscore.GenerationsCore;
import generations.gg.generations.core.generationscore.client.screen.npc.CustomizeNpcScreen;
import generations.gg.generations.core.generationscore.network.ClientNetworkPacketHandler;
import generations.gg.generations.core.generationscore.network.packets.GenerationsNetworkPacket;
import generations.gg.generations.core.generationscore.world.entity.PlayerNpcEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record S2COpenNpcCustomizationScreenPacket(int entityId) implements GenerationsNetworkPacket<S2COpenNpcCustomizationScreenPacket> {
    public static final ResourceLocation ID = GenerationsCore.id("open_npc_customization");

    public S2COpenNpcCustomizationScreenPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

}