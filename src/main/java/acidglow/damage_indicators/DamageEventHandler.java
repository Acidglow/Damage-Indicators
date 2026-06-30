package acidglow.damage_indicators;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class DamageEventHandler {
    private static final float HIGH_DAMAGE_THRESHOLD = 15.0F;

    private final IntSet criticalTargets = new IntOpenHashSet();

    @SubscribeEvent
    public void onCriticalHit(CriticalHitEvent event) {
        if (!event.getEntity().level().isClientSide() && event.isCriticalHit()) {
            this.criticalTargets.add(event.getTarget().getId());
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent.Post event) {
        LivingEntity entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel) || event.getNewDamage() <= 0.0F) {
            return;
        }

        DamageCategory category = this.categoryFor(entity, event.getSource(), event.getNewDamage());
        DamageIndicatorPayload payload = new DamageIndicatorPayload(
                entity.getId(),
                entity.getX(),
                entity.getY() + entity.getBbHeight(),
                entity.getZ(),
                event.getNewDamage(),
                category);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, payload);
    }

    private DamageCategory categoryFor(LivingEntity entity, DamageSource source, float amount) {
        if (this.criticalTargets.remove(entity.getId())) {
            return DamageCategory.CRITICAL;
        }

        if (isDamageOverTime(source)) {
            return DamageCategory.ENTITY;
        }

        Entity attacker = source.getEntity();
        if (attacker instanceof LivingEntity && !(attacker instanceof Player)) {
            return DamageCategory.ENTITY;
        }

        return amount > HIGH_DAMAGE_THRESHOLD ? DamageCategory.HIGH : DamageCategory.LOW;
    }

    private static boolean isDamageOverTime(DamageSource source) {
        return source.is(DamageTypes.ON_FIRE)
                || source.is(DamageTypes.IN_FIRE)
                || source.is(DamageTypes.CAMPFIRE)
                || source.is(DamageTypes.LAVA)
                || source.is(DamageTypes.HOT_FLOOR)
                || source.is(DamageTypes.WITHER)
                || source.is(DamageTypes.MAGIC)
                || source.is(DamageTypes.DROWN)
                || source.is(DamageTypes.STARVE)
                || source.is(DamageTypes.FREEZE)
                || source.is(DamageTypes.CACTUS)
                || source.is(DamageTypes.SWEET_BERRY_BUSH);
    }
}
