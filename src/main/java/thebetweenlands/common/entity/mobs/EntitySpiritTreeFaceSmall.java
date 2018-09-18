package thebetweenlands.common.entity.mobs;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationSpiritTree;

public class EntitySpiritTreeFaceSmall extends EntitySpiritTreeFace implements IEntityAdditionalSpawnData {
	private int variant;

	public EntitySpiritTreeFaceSmall(World world) {
		super(world);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();

		this.tasks.addTask(0, new AITrackTarget(this, true, 24.0D));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(2.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		this.setVariant(this.rand.nextInt(2));
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.SPIRIT_TREE_FACE_SMALL;
	}

	@Override
	public List<BlockPos> findNearbyWoodBlocks() {
		List<LocationSpiritTree> locations = BetweenlandsWorldStorage.forWorld(this.world).getLocalStorageHandler().getLocalStorages(LocationSpiritTree.class, this.getEntityBoundingBox(), loc -> loc.isInside(this));
		if(!locations.isEmpty()) {
			List<BlockPos> positions = locations.get(0).getSmallFacePositions();
			if(!positions.isEmpty()) {
				return positions;
			}
		}
		return super.findNearbyWoodBlocks();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("variant", this.getVariant());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.setVariant(nbt.getInteger("variant"));
	}

	public void setVariant(int variant) {
		this.variant = variant;
	}

	public int getVariant() {
		return this.variant;
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(this.variant);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		this.variant = additionalData.readInt();
	}
}
