package acidglow.damage_indicators;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DamageIndicatorPayload(int entityId, double x, double y, double z, float amount, DamageCategory category) implements CustomPacketPayload {
    public static final Type<DamageIndicatorPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(AcidglowsDamageIndicators.MODID, "damage_indicator"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DamageIndicatorPayload> STREAM_CODEC = StreamCodec.ofMember(
            DamageIndicatorPayload::write, DamageIndicatorPayload::read);

    private static DamageIndicatorPayload read(RegistryFriendlyByteBuf buf) {
        return new DamageIndicatorPayload(
                buf.readVarInt(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readFloat(),
                buf.readEnum(DamageCategory.class));
    }

    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeFloat(this.amount);
        buf.writeEnum(this.category);
    }

    public Vec3 position() {
        return new Vec3(this.x, this.y, this.z);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(DamageIndicatorPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientDamageIndicators.add(payload));
    }
}
