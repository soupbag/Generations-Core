package generations.gg.generations.core.generationscore.common.network

import generations.gg.generations.core.generationscore.common.network.packets.*
import generations.gg.generations.core.generationscore.common.network.packets.shop.*
import generations.gg.generations.core.generationscore.common.network.packets.statue.S2COpenStatueEditorScreenHandler
import generations.gg.generations.core.generationscore.common.network.packets.statue.S2COpenStatueEditorScreenPacket
import generations.gg.generations.core.generationscore.common.network.packets.statue.UpdateStatueHandler
import generations.gg.generations.core.generationscore.common.network.packets.statue.UpdateStatuePacket
import generations.gg.generations.core.generationscore.common.network.spawn.SpawnExtraDataEntityHandler
import generations.gg.generations.core.generationscore.common.network.spawn.SpawnStatuePacket
import generations.gg.generations.core.generationscore.common.world.shop.ShopPresetRegistrySyncPacket
import generations.gg.generations.core.generationscore.common.world.shop.ShopRegistrySyncPacket
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import java.util.function.Consumer

class ClientPacketProxy : PacketProxy() {
    override val processS2COpenMailEditScreenPacket : Consumer<S2COpenMailEditScreenPacket> = createConsumer(S2COpenMailEditScreenPacketHandler)
    override val processS2COpenMailPacket : Consumer<S2COpenMailPacket> = createConsumer(S2COpenMailPacketHandler)
    override val processS2CUnlockReloadPacket : Consumer<S2CUnlockReloadPacket> = createConsumer(UnlockReloadPacketHandler)
    override val processS2COpenStatueEditorScreenPacket : Consumer<S2COpenStatueEditorScreenPacket> = createConsumer(S2COpenStatueEditorScreenHandler)
    override val processShopRegistrySyncPacket : Consumer<ShopRegistrySyncPacket> = createConsumer(DataRegistrySyncPacketHandler())
    override val processShopPresetRegistrySyncPacket : Consumer<ShopPresetRegistrySyncPacket> = createConsumer(DataRegistrySyncPacketHandler())
    override val processS2COpenShopPacket : Consumer<S2COpenShopPacket> = createConsumer(S2COpenShopHandler)
    override val processS2CSyncPlayerMoneyPacket : Consumer<S2CSyncPlayerMoneyPacket> = createConsumer(S2CSyncPlayerMoneyHandler())
    override val processSpawnStatuePacket : Consumer<SpawnStatuePacket> = createConsumer(SpawnExtraDataEntityHandler())
    override val processS2CPlaySoundPacket : Consumer<S2CPlaySoundPacket> = createConsumer(S2CPlaySoundHandler())

    override fun <V, T : UpdateStatuePacket<V, T>> processStatueUpdate(packet: T, handler: UpdateStatueHandler<V, T>) {
        handler.accept(packet, Minecraft.getInstance().player as LocalPlayer)
    }

    private fun <T : GenerationsNetworkPacket<T>> createConsumer(handler: ClientNetworkPacketHandler<T>): Consumer<T> {
        return Consumer {
            var minecraft =Minecraft.getInstance()
            minecraft.execute { handler.handle(it, minecraft) }
        }
    }
}
