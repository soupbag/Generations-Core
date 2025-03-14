package generations.gg.generations.core.generationscore.forge.compat;

import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.client.render.models.blockbench.repository.PokemonModelRepository;
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import generations.gg.generations.core.generationscore.common.GenerationsCore;
import generations.gg.generations.core.generationscore.common.client.model.RareCandyBone;
import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.event.forge.EntityRadarUpdateEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@ClientPlugin
public class JourneyMapCompatClientForge implements IClientPlugin {
    @Override
    public void initialize(IClientAPI jmClientApi) {
        MinecraftForge.EVENT_BUS.addListener(this::onRadarEvent);
    }

    @Override
    public String getModId() {
        return GenerationsCore.MOD_ID;
    }

    @Override
    public void onEvent(ClientEvent event) {

    }

    private final RenderContext context = new RenderContext();



    public void onRadarEvent(EntityRadarUpdateEvent event) {
        var entity = event.getWrappedEntity();

        if(entity.getEntityLivingRef().get().getType().equals(CobblemonEntities.POKEMON)) {
            var pokemon = (PokemonEntity) entity.getEntityLivingRef().get();

            var species = pokemon.getExposedSpecies().resourceIdentifier;
            var aspects = pokemon.getAspects();

            var model = PokemonModelRepository.INSTANCE.getPoser(species, aspects);

            if(model.getRootPart() instanceof RareCandyBone bone) {
                context.put(RenderContext.Companion.getSPECIES(), species);
                context.put(RenderContext.Companion.getASPECTS(), aspects);
                context.put(RenderContext.Companion.getRENDER_STATE(), RenderContext.RenderState.PROFILE);


                var id = bone.getSprite(context);

                entity.setEntityIconLocation(id);
            }
        }
    }
}
