package dev.jab125.minimega.grf.minecraft.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record ShowItemInDialogPayload(Optional<ItemStack> message) implements CustomPacketPayload {
	public static final Type<ShowItemInDialogPayload> TYPE = new Type<>(Identifier.parse("minimega_grf_minecraft:show_item_in_dialog_payload"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ShowItemInDialogPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.optional(ItemStack.STREAM_CODEC), ShowItemInDialogPayload::message,
			ShowItemInDialogPayload::new
	);

	@Override
	public Type<ShowItemInDialogPayload> type() {
		return TYPE;
	}
}
