package dev.jab125.minimega.grf.minecraft.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public record DialogPayload(Optional<Component> message, boolean append) implements CustomPacketPayload {
	public static final Type<DialogPayload> TYPE = new Type<>(Identifier.parse("minimega_grf_minecraft:dialog_payload"));
	public static final StreamCodec<ByteBuf, DialogPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.optional(ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC), DialogPayload::message,
			ByteBufCodecs.BOOL, DialogPayload::append,
			DialogPayload::new
	);

	public DialogPayload(Component message, boolean append) {
		this(Optional.ofNullable(message), append);
	}

	@Override
	public Type<DialogPayload> type() {
		return TYPE;
	}
}
