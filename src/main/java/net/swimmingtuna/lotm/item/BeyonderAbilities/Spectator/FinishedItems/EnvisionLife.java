package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;


import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.spirituality.ModAttributes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnvisionLife extends Item {

    public EnvisionLife(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("While holding this item, type in a mob and that mob will be spawned, targeting the nearest player within 100 blocks\n" +
                    "In the case of Modded Mobs, type in the Mod ID followed by the mob name, e.g. (lotm:black_panter\n" +
                    "Spirituality Used: 1500\n" +
                    "Cooldown: 0 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }

    @SubscribeEvent
    public static void onChatMessage(ServerChatEvent event) {
        Level level = event.getPlayer().serverLevel();
        Player pPlayer = event.getPlayer();
        String message = event.getMessage().getString().toLowerCase();

        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            AttributeInstance dreamIntoReality = pPlayer.getAttribute(ModAttributes.DIR.get());
            if (!pPlayer.level().isClientSide() && pPlayer.getMainHandItem().getItem() instanceof EnvisionLife && spectatorSequence.getCurrentSequence() == 0) {
                spawnMob(pPlayer, message);
                if (dreamIntoReality.getValue() == 2) {
                    spawnMob(pPlayer,message);}
                spectatorSequence.useSpirituality(1500);
                event.setCanceled(true);
            }
        });
    }

    private static void spawnMob(Player pPlayer, String mobName) {
        // Get the world level and position of the player
        Level level = pPlayer.level();
        double x = pPlayer.getX();
        double y = pPlayer.getY();
        double z = pPlayer.getZ();

        // Find the EntityType based on the mobName (assuming it's a valid EntityType)
        EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(mobName));

        // Check if the EntityType is valid
        if (entityType != null) {
            Entity entity = entityType.create(level);
            if (entity != null) {
                Mob mob = (Mob) entity;
                entity.setPos(x, y, z);
                Player nearestPlayer = findNearestPlayer(level, x, y, z, 100, pPlayer);
                if (nearestPlayer != null) {
                mob.setLastHurtByPlayer(nearestPlayer);
                }
                level.addFreshEntity(entity);
            }
        }
        else {
            pPlayer.sendSystemMessage(Component.literal("Mob not valid"));
        }
    }
    private static Player findNearestPlayer(Level world, double x, double y, double z, double range, Player excludedPlayer) {
        List<Player> players = world.getEntitiesOfClass(Player.class, excludedPlayer.getBoundingBox().inflate(range, range, range), player -> player != excludedPlayer);
        if (players.isEmpty()) {
            return null;
        }

        players.sort((p1, p2) -> {
            double d1 = p1.distanceToSqr(x, y, z);
            double d2 = p2.distanceToSqr(x, y, z);
            return Double.compare(d1, d2);
        });

        return players.get(0);
    }
}