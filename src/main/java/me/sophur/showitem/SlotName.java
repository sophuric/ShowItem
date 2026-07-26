package me.sophur.showitem;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.commands.arguments.StringRepresentableArgument;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;
import org.jspecify.annotations.NonNull;

import java.util.Locale;

enum SlotName implements StringRepresentable {
    MAIN(EquipmentSlot.MAINHAND),
    MAINHAND(EquipmentSlot.MAINHAND),
    OFFHAND(EquipmentSlot.OFFHAND),

    HEAD(EquipmentSlot.HEAD),
    HELMET(EquipmentSlot.HEAD),
    CAP(EquipmentSlot.HEAD),
    HAT(EquipmentSlot.HEAD),

    CHEST(EquipmentSlot.CHEST),
    CHESTPLATE(EquipmentSlot.CHEST),
    TUNIC(EquipmentSlot.CHEST),

    LEGS(EquipmentSlot.LEGS),
    LEGGINGS(EquipmentSlot.LEGS),
    PANTS(EquipmentSlot.LEGS),

    FEET(EquipmentSlot.FEET),
    BOOTS(EquipmentSlot.FEET),
    ;

    public final EquipmentSlot equipmentSlot;

    SlotName(EquipmentSlot equipmentSlot) {
        this.equipmentSlot = equipmentSlot;
    }

    @Override
    public @NonNull String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static final Codec<SlotName> CODEC = StringRepresentable.fromEnum(SlotName::values);

    public static class SlotNameArgumentType extends StringRepresentableArgument<SlotName> {
        private static final SlotNameArgumentType INSTANCE = new SlotNameArgumentType();

        private SlotNameArgumentType() {
            super(CODEC, SlotName::values);
        }

        public static SlotNameArgumentType get() {
            return INSTANCE;
        }

        private static boolean registered = false;

        public static void register() {
            if (registered) return;
            registered = true;
            ArgumentTypeRegistry.registerArgumentType(ShowItem.identifier("slot_name"), SlotName.SlotNameArgumentType.class,
                    SingletonArgumentInfo.contextFree(SlotName.SlotNameArgumentType::get));
        }
    }
}
