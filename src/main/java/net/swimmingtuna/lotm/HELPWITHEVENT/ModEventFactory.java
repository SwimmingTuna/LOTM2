package net.swimmingtuna.lotm.HELPWITHEVENT;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.event.IModBusEvent;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;

public class ModEventFactory {
    public static <T extends Event & IModBusEvent> void fireModEvent(T event) {
        ModLoader.get().postEvent(event);
    }

    public static void onSailorShootProjectile(Projectile projectile, HitResult ray) {
        ProjectileEvent.ProjectileControlEvent event = new ProjectileEvent.ProjectileControlEvent(projectile,ray);
        LivingEntity pPlayer = event.getOwner();
        ModEventFactory.onSailorShootProjectile(projectile, ray);
        ProjectileEvent.ProjectileControlEvent projectileEvent = new ProjectileEvent.ProjectileControlEvent(projectile,ray);
        MinecraftForge.EVENT_BUS.post(projectileEvent);
        if (!projectile.level().isClientSide()) {
            LivingEntity target = event.getTarget(30,0);
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder((Player) pPlayer).orElse(null);
            pPlayer.sendSystemMessage(Component.literal("working"));
            if (holder.isSailorClass() && holder.getCurrentSequence() <= 7) {
                Vec3 projectilePos = projectile.position();
                Vec3 targetPos = new Vec3(target.getX(), target.getY(), target.getZ());
                Vec3 projectileVelocity = projectile.getDeltaMovement();
                Vec3 directionToTarget = targetPos.subtract(projectilePos).normalize();

                double curveStrength = 2.0;
                Vec3 newVelocity = projectileVelocity.add(directionToTarget.scale(curveStrength));
                projectile.setDeltaMovement(newVelocity);
                projectile.hurtMarked = true;

            }
        }
        fireModEvent(event);
    }
}
