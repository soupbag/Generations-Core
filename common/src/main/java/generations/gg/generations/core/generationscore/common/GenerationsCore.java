/**
 * The Main Class of the Generations-Core mod. (Common)
 * Registers the mod's items and blocks with Minecraft using Architectury.
 * @author Joseph T. McQuigg
 *
 * CopyRight (c) 2023 Generations-Mod
 */

package generations.gg.generations.core.generationscore.common;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.data.DataProvider;
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail;
import com.cobblemon.mod.common.api.storage.player.PlayerDataExtensionRegistry;
import com.cobblemon.mod.common.client.render.layer.PokemonOnShoulderRenderer;
import com.cobblemon.mod.common.platform.events.ServerEvent;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import dev.architectury.event.events.common.LootEvent;
import generations.gg.generations.core.generationscore.common.api.GenerationsMolangFunctions;
import generations.gg.generations.core.generationscore.common.api.data.GenerationsCoreEntityDataSerializers;
import generations.gg.generations.core.generationscore.common.api.player.AccountInfo;
import generations.gg.generations.core.generationscore.common.api.player.BiomesVisited;
import generations.gg.generations.core.generationscore.common.api.player.Caught;
import generations.gg.generations.core.generationscore.common.api.player.CurryDex;
import generations.gg.generations.core.generationscore.common.client.render.rarecandy.ModelRegistry;
import generations.gg.generations.core.generationscore.common.config.Config;
import generations.gg.generations.core.generationscore.common.config.ConfigLoader;
import generations.gg.generations.core.generationscore.common.config.LegendKeys;
import generations.gg.generations.core.generationscore.common.recipe.GenerationsIngredidents;
import generations.gg.generations.core.generationscore.common.world.container.GenerationsContainers;
import generations.gg.generations.core.generationscore.common.world.entity.GenerationsEntities;
import generations.gg.generations.core.generationscore.common.world.item.GenerationsArmor;
import generations.gg.generations.core.generationscore.common.world.item.GenerationsCobblemonInteractions;
import generations.gg.generations.core.generationscore.common.world.item.GenerationsItems;
import generations.gg.generations.core.generationscore.common.world.item.GenerationsTools;
import generations.gg.generations.core.generationscore.common.world.item.creativetab.GenerationsCreativeTabs;
import generations.gg.generations.core.generationscore.common.world.item.legends.EnchantableItem;
import generations.gg.generations.core.generationscore.common.world.level.block.*;
import generations.gg.generations.core.generationscore.common.world.level.block.entities.GenerationsBlockEntities;
import generations.gg.generations.core.generationscore.common.world.loot.LootItemConditionTypes;
import generations.gg.generations.core.generationscore.common.world.loot.LootPoolEntryTypes;
import generations.gg.generations.core.generationscore.common.world.loot.SpeciesKeyCondition;
import generations.gg.generations.core.generationscore.common.world.recipe.*;
import generations.gg.generations.core.generationscore.common.world.sound.GenerationsSounds;
import generations.gg.generations.core.generationscore.common.world.spawning.ZygardeCellDetail;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * The Main Class of the Generations-Core mod. (Common)
 * Registers the mod's items and blocks with Minecraft using Architectury.
 * @author Joseph T. McQuigg, WaterPicker
 */
public class GenerationsCore {


	/** The mod id of the Generations-Core mod. */
	public static final String MOD_ID = "generations_core";

	/** The logger for the Generations-Core mod. */
	public static final Logger LOGGER = LogUtils.getLogger();

	/** The config for the Generations-Core mod. */
	public static Config CONFIG;
	public static GenerationsImplementation implementation;

	public static DataProvider dataProvider = GenerationsDataProvider.INSTANCE;

	/**
	 * Initializes the Generations-Core mod.
	 */
	public static void init(GenerationsImplementation implementation) {
		CONFIG = ConfigLoader.loadConfig(Config.class, "core", "main");
		GenerationsCore.implementation = implementation;



//		GenerationsDataProvider.INSTANCE.register(ShopPresets.instance());
//		GenerationsDataProvider.INSTANCE.register(Shops.instance());

		LootEvent.MODIFY_LOOT_TABLE.register((lootDataManager, id, context, builtin) -> {
            if(id.getNamespace().equals("minecraft") && id.getPath().contains("chests") && !id.getPath().contains("inject")) {
                var inject = new ResourceLocation(id.getNamespace(), id.getPath().replace("chests", "chests/inject"));
                context.addPool(LootPool.lootPool().add(LootTableReference.lootTableReference(inject)));
            } else if(id.toString().equals("minecraft:blocks/carrots")) {
				context.addPool(LootPool.lootPool().add(LootTableReference.lootTableReference(GenerationsCore.id("blocks/calyrex_roots"))));
			}
        });

		SpawnDetail.Companion.registerSpawnType(ZygardeCellDetail.TYPE, ZygardeCellDetail.class);

		GenerationsCoreEntityDataSerializers.init();
		GenerationsSounds.init();
		GenerationsBlocks.init();
		GenerationsPokeDolls.init();
		GenerationsWood.init();
		GenerationsOres.init();
		GenerationsDecorationBlocks.init();
		LootPoolEntryTypes.init();
		LootItemConditionTypes.init();
		GenerationsUtilityBlocks.init();
		GenerationsShrines.init();
		GenerationsItems.init();
		GenerationsBlockEntities.init();
		GenerationsEntities.init();
		GenerationsCreativeTabs.init();
		GenerationsArmor.init();
		GenerationsTools.init();
		GenerationsPaintings.init();
		GenerationsContainers.init();
		RksResultType.init();
		initRecipes();
		GenerationsCoreRecipeTypes.init();
		GenerationsCoreRecipeSerializers.init();
		GenerationsCoreStats.init();

		GenerationsDataProvider.INSTANCE.registerDefaults();

		PlayerDataExtensionRegistry.INSTANCE.register(AccountInfo.KEY, AccountInfo.class, false);
		PlayerDataExtensionRegistry.INSTANCE.register(Caught.KEY, Caught.class, false);
		PlayerDataExtensionRegistry.INSTANCE.register(BiomesVisited.KEY, BiomesVisited.class, false);
//		PlayerDataExtensionRegistry.INSTANCE.register(CurryDex.KEY, CurryDex.class, false);

		GenerationsMolangFunctions.init();

		GenerationsCobblemonEvents.init();
		GenerationsArchitecturyEvents.init();

		GenerationsCobblemonInteractions.INSTANCE.registerDefaultCustomInteractions();


//		BuiltInRegistries.BLOCK.stream().map(a -> a.arch$registryName() + ": " + a.getLootTable()).forEach(a -> System.out.println(a));
	}

	private static void initRecipes() {
		GenerationsIngredidents.register(ItemIngredient.Companion.getID(), ItemIngredientSerializer.INSTANCE);
		GenerationsIngredidents.register(TimeCapsuleIngredient.Companion.getID(), TimeCapsuleIngredientSerializer.INSTANCE);
		GenerationsIngredidents.register(PokemonItemIngredient.Companion.getID(), PokemonItemIngredient.PokemonItemIngredientSerializer.INSTANCE);
		GenerationsIngredidents.register(DamageIngredient.Companion.getID(), DamageIngredientSerializer.INSTANCE);
		GenerationsIngredidents.register(ItemTagIngredient.Companion.getID(), ItemTagIngredientSerializer.INSTANCE);
		GenerationsIngredidents.register(GenerationsIngredient.EmptyIngredient.INSTANCE.getId(), new GenerationsIngredientSerializer<>() {
			@NotNull
			@Override
			public GenerationsIngredient read(@NotNull FriendlyByteBuf buf) {
				return GenerationsIngredient.EmptyIngredient.INSTANCE;
			}

			@NotNull
			@Override
			public GenerationsIngredient read(@NotNull JsonObject jsonObject) {
				return GenerationsIngredient.EmptyIngredient.INSTANCE;
			}
		});
	}

	public static void initBuiltinPacks(TriConsumer<PackType, ResourceLocation, MutableComponent> consumer) {
//		consumer.accept(PackType.CLIENT_RESOURCES, GenerationsCore.id("smooth_pokemon"), Component.literal("Smooth Pokemon Models"));
	}

	public static void onAnvilChange(ItemStack left, ItemStack right, Player player, Consumer<ItemStack> output, IntConsumer cost, IntConsumer materialCost) {
		if(player instanceof ServerPlayer && left.getItem() instanceof EnchantableItem enchantableItem && enchantableItem.neededEnchantmentLevel(player) > 0 && !EnchantableItem.isEnchanted(left) && !EnchantableItem.isUsed(left) && right.isEmpty()) {
			output.accept(EnchantableItem.setEnchanted(left.copy(), true));
			cost.accept(100);
			materialCost.accept(0);
		}
	}

	/**
	 * Creates a {@link ResourceLocation} with the Generations-Core Mod id.
	 */
	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	public static GenerationsImplementation getImplementation() {
		return implementation;
	}

	public static void setImplementation(GenerationsImplementation implementation) {
		GenerationsCore.implementation = implementation;
	}
}