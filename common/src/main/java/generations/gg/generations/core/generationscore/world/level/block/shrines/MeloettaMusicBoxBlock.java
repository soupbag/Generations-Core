package generations.gg.generations.core.generationscore.world.level.block.shrines;

import generations.gg.generations.core.generationscore.world.level.block.entities.MeloettaMusicBoxBlockEntity;
import generations.gg.generations.core.generationscore.world.level.block.entities.GenerationsBlockEntities;
import generations.gg.generations.core.generationscore.world.level.block.entities.PokeModBlockEntityModels;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class MeloettaMusicBoxBlock extends ShrineBlock<MeloettaMusicBoxBlockEntity> {

    public MeloettaMusicBoxBlock(BlockBehaviour.Properties properties) {
        super(properties, GenerationsBlockEntities.MELOETTA_MUSIC_BOX, PokeModBlockEntityModels.MELOETTA_MUSIC_BOX);
    }
}
