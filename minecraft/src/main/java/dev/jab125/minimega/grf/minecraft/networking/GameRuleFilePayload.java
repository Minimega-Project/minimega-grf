package dev.jab125.minimega.grf.minecraft.networking;

import dev.jab125.minimega.grf.element.__ROOT__;
import dev.jab125.minimega.grf.minecraft.GRFStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public record GameRuleFilePayload(Optional<__ROOT__> root) implements CustomPacketPayload {
	public static final Type<GameRuleFilePayload> TYPE = new Type<>(Identifier.parse("minimega_grf_minecraft:grf_payload"));
	public static final StreamCodec<ByteBuf, GameRuleFilePayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.optional((StreamCodec<? super ByteBuf, __ROOT__>) GRFStreamCodecs.ELEMENT_STREAM_CODEC), GameRuleFilePayload::root,
			GameRuleFilePayload::new
	);
	@Override
	public Type<GameRuleFilePayload> type() {
		return TYPE;
	}
}
