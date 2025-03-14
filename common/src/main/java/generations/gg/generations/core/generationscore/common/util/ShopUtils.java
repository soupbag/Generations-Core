package generations.gg.generations.core.generationscore.common.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import generations.gg.generations.core.generationscore.common.api.player.PlayerMoneyHandler;
import generations.gg.generations.core.generationscore.common.world.entity.ShopOfferProvider;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ShopUtils {

    public static void buy(Player player, ItemStack stack, int perItemPrice, int amount) {
        Consumer<Boolean> var = ab -> {
            if(ab) giveStack(player, stack);
        };
        takePokeDollars(player, perItemPrice * amount).thenAccept(var);
    }

    public static void sell(Player player, ItemStack stack, int perItemPrice, int amount) {
        Consumer<Boolean> var = ab -> {
            if(ab) takeStack(player, stack);
        };

        if (hasAmount(player, stack, amount)) {
            givePokeDollars(player, perItemPrice * amount).thenAccept(var);
        }
    }

    public static void giveStack(Player player, ItemStack itemstack) {
        if (itemstack.isEmpty()) return;

        ItemEntity itemEntity = player.drop(itemstack, true, true);
        if (itemEntity != null) {
            itemEntity.setNoPickUpDelay();
            itemEntity.setTarget(player.getUUID());
        }
    }

    public static void giveStack(Player player, ItemStack itemstack, int amount) {
        if (itemstack.isEmpty()) return;

        itemstack = itemstack.copyWithCount(itemstack.getCount() * amount);

        ItemEntity itemEntity = player.drop(itemstack, true, true);
        if (itemEntity != null) {
            itemEntity.setNoPickUpDelay();
            itemEntity.setTarget(player.getUUID());
        }
    }

    public static void takeStack(Player player, ItemStack itemStack) {
        takeStack(player, itemStack, 1);
    }

    public static void takeStack(Player player, ItemStack itemStack, int neededCount) {
        if (itemStack.isEmpty()) return;

        neededCount *= itemStack.getCount();

        for (ItemStack stack : player.getInventory().items) {
            if (matches(itemStack, stack)) {
                int count = stack.getCount();
                stack.shrink(neededCount);
                neededCount -= count;
            }

            if (neededCount <= 0) {
                break;
            }
        }
    }

    public static boolean matches(ItemStack stack1, ItemStack stack2) {
        return ItemStack.isSameItem(stack1, stack2) && ItemStack.isSameItemSameTags(stack1, stack2);
    }

    public static boolean hasAmount(Player player, ItemStack itemStack) {
        if (itemStack.isEmpty()) return true;

        return getAmountInInventory(player, itemStack) >= itemStack.getCount();
    }

    public static boolean hasAmount(Player player, ItemStack itemStack, int amount) {
        if (itemStack.isEmpty()) return true;

        return getAmountInInventory(player, itemStack) >= (itemStack.getCount() * amount);
    }

    public static boolean hasItem(Player player, ItemStack itemStack) {
        if (itemStack.isEmpty()) return true;

        for(ItemStack stack : player.getInventory().items) {
            if (matches(itemStack, stack))
                return true;
        }

        return false;
    }

    public static int getAmountInInventory(Player player, ItemStack itemStack) {
        if (itemStack.isEmpty()) return 0;

        int currentCount = 0;
        for(ItemStack stack : player.getInventory().items) {
            if (matches(itemStack, stack))
                currentCount += stack.getCount();
        }

        return currentCount;
    }

    public static ItemStack stringToStack(String s) {
        HolderLookup<Item> items = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY).lookupOrThrow(Registries.ITEM);
        try {
            ItemParser.ItemResult itemResult = ItemParser.parseForItem(items, new StringReader(s));
            ItemInput itemInput = new ItemInput(itemResult.item(), itemResult.nbt());
            return itemInput.createItemStack(1, false);
        } catch (CommandSyntaxException e) {
            return ItemStack.EMPTY;
        }
    }

    public static String stackToString(ItemStack itemStack) {
        return itemStack == null || itemStack.isEmpty() ? "minecraft:empty" :
                BuiltInRegistries.ITEM.getKey(itemStack.getItem())
                        +(itemStack.hasTag() ? itemStack.getTag().toString() : "");
    }

//    public static boolean canAfford(Player player, int pokedollars) {
//        return PlayerPokeDollars.of(player).getBalance() >= pokedollars;
//    }

    public static CompletableFuture<Boolean> givePokeDollars(Player player, int pokedollars) {
        return PlayerMoneyHandler.of(player).deposit(BigDecimal.valueOf(pokedollars));
    }

    public static CompletableFuture<Boolean> takePokeDollars(Player player, int pokedollars) {
        return PlayerMoneyHandler.of(player).withdraw(BigDecimal.valueOf(pokedollars));
    }

    public static boolean validateItemForNpc(ShopOfferProvider provider, ItemStack stack, int price, boolean isBuy) {
        if (provider.getOffers() == null)
            return false;

        return Arrays.stream(provider.getOffers().getEntries())
                .anyMatch(entry -> matches(entry.getItem(), stack)
                        && entry.getItem().getCount() == stack.getCount()
                        && (isBuy ? entry.getBuyPrice() == price : entry.getSellPrice() == price));
    }

}