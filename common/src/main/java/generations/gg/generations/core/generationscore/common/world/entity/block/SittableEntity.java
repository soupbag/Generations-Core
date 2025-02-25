package generations.gg.generations.core.generationscore.common.world.entity.block;

import dev.architectury.networking.NetworkManager;
import generations.gg.generations.core.generationscore.common.world.entity.GenerationsEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SittableEntity extends Entity {

    public SittableEntity(EntityType<? extends Entity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    private SittableEntity(Level level, BlockPos pos, double offset, float yaw) {
        this(GenerationsEntities.SEAT.get(), level);
        this.setPos(pos.getX() + 0.5, pos.getY() + offset, pos.getZ() + 0.5);
        this.setRot(yaw, 0F);
    }

    @Override
    public void tick()
    {
        super.tick();
        if(!this.level().isClientSide)
        {
            if(this.getPassengers().isEmpty() || this.level().isEmptyBlock(this.blockPosition()))
            {
                this.remove(RemovalReason.DISCARDED);
                this.level().updateNeighbourForOutputSignal(blockPosition(), this.level().getBlockState(blockPosition()).getBlock());
            }
        }
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {}

    @Override
    public double getPassengersRidingOffset()
    {
        return 0.0;
    }

    @Override
    protected boolean canRide(Entity entity)
    {
        return true;
    }

    // Call to mount the player to a newly created SittableEntity
    public static InteractionResult mount(Level level, BlockPos pos, double yOffset, Player player, float direction)
    {
        if(!level.isClientSide())
        {
            List<SittableEntity> seats = level.getEntitiesOfClass(SittableEntity.class, new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.0, pos.getZ() + 1.0));
            if(seats.isEmpty())
            {
                SittableEntity seat = new SittableEntity(level, pos, yOffset, direction);
                level.addFreshEntity(seat);
                player.startRiding(seat, true);
            }
        }
        return InteractionResult.SUCCESS;
    }


    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkManager.createAddEntityPacket(this);
    }

    // Tick the key and check if the block is removed or if there are no more passengers
    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity entity)
    {
        Direction original = this.getDirection();
        Direction[] offsets = {original, original.getClockWise(), original.getCounterClockWise(), original.getOpposite()};
        for(Direction dir : offsets)
        {
            Vec3 safeVec = DismountHelper.findSafeDismountLocation(entity.getType(), this.level(), this.blockPosition().relative(dir), false);
            if(safeVec != null)
            {
                return safeVec.add(0, 0.25, 0);
            }
        }
        return super.getDismountLocationForPassenger(entity);
    }

    @Override
    protected void addPassenger(Entity entity)
    {
        super.addPassenger(entity);
        entity.setYRot(this.getYRot());
    }

    @Override
    public void positionRider(Entity entity, Entity.MoveFunction function)
    {
        super.positionRider(entity, function);
        this.clampYaw(entity);
    }

    @Override
    public void onPassengerTurned(Entity entity)
    {
        this.clampYaw(entity);
    }

    private void clampYaw(Entity passenger)
    {
        passenger.setYBodyRot(this.getYRot());
        float wrappedYaw = Mth.wrapDegrees(passenger.getYRot() - this.getYRot());
        float clampedYaw = Mth.clamp(wrappedYaw, -120.0F, 120.0F);
        passenger.yRotO += clampedYaw - wrappedYaw;
        passenger.setYRot(passenger.getYRot() + clampedYaw - wrappedYaw);
        passenger.setYHeadRot(passenger.getYRot());
    }
}