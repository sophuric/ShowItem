package me.sophur.showitem;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.network.chat.*;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.world.entity.EquipmentSlot.MAINHAND;

public final class ShowItem implements ModInitializer {
    public static final String MOD_ID = "ShowItem";
    public static final String MOD_ID_LOWER = "showitem";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier identifier(String name) {
        return Identifier.fromNamespaceAndPath(MOD_ID_LOWER, name);
    }

    @Override
    public void onInitialize() {
        SlotName.SlotNameArgumentType.register();
        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> {
            var command = dispatcher.register(
                    literal("showitem")
                            .then(argument("slot", SlotName.SlotNameArgumentType.get())
                                    .executes(context -> {
                                        var slot = context.getArgument("slot", SlotName.class);
                                        return showItemRunner(slot).run(context);
                                    }))
                            .executes(showItemRunner(MAINHAND)));
            for (var alias : List.of("show"))
                dispatcher.register(literal(alias).redirect(command).executes(showItemRunner(MAINHAND)));
        });
    }

    private static Command<CommandSourceStack> showItemRunner(SlotName slot) {
        return showItemRunner(slot.equipmentSlot);
    }

    private static Command<CommandSourceStack> showItemRunner(EquipmentSlot slot) {
        return context -> {
            CommandSourceStack source = context.getSource();
            ServerPlayer player = source.getPlayerOrException();

            ItemStack item = player.getItemBySlot(slot);
            if (item.isEmpty()) {
                source.sendFailure(Component.translatable("commands.showitem.empty." + slot.getName()));
                return 0;
            }

            var slotType = slot.getType().name().toLowerCase(Locale.ROOT);
            PlayerList playerList = source.getServer().getPlayerList();

            var playerName = player.getDisplayName();
            var itemName = item.getDisplayName();
            MutableComponent message;

            if (item.isStackable()) message = Component.translatable(
                    "chat.showitem.stack." + slotType, playerName, item.getCount(), itemName);
            else message = Component.translatable(
                    "chat.showitem." + slotType, playerName, itemName);

            playerList.broadcastSystemMessage(message, false);
            player.connection.detectRateSpam();

            return 1;
        };
    }
}
