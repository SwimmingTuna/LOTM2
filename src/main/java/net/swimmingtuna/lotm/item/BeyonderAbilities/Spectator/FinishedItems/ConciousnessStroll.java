package net.swimmingtuna.lotm.item.BeyonderAbilities.Spectator.FinishedItems;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ConciousnessStroll extends Item {

    public ConciousnessStroll(Properties pProperties) {
        super(pProperties);
    }

    @SubscribeEvent
    public static void onChatMessage(ServerChatEvent event) {
        Level level = event.getPlayer().serverLevel();
        Player pPlayer = event.getPlayer();
        String message = event.getMessage().getString().toLowerCase();
        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            if (!pPlayer.level().isClientSide() && pPlayer.getMainHandItem().getItem() instanceof ConciousnessStroll && spectatorSequence.getCurrentSequence() <= 2) {
                Player targetPlayer = null;
                for (Player serverPlayer : level.players()) {
                    if (serverPlayer.getUUID().toString().equals(message)) {
                        targetPlayer = serverPlayer;
                        break;
                    }
                }
                if (targetPlayer != null) {
                    int x = (int) targetPlayer.getX();
                    int y = (int) targetPlayer.getY();
                    int z = (int) targetPlayer.getZ();
                    pPlayer.teleportTo(x, y, z);
                    int strollTimer = pPlayer.getPersistentData().getInt("StrollTimer");
                    pPlayer.getPersistentData().putInt("StrollTimer", 1);
                    spectatorSequence.useSpirituality(350);
                    event.setCanceled(true);
                } else {
                    event.getPlayer().sendSystemMessage(Component.literal("Player:" + message + " not found"), true);
                    event.setCanceled(true);
                }
            }
        });
    }

    @SubscribeEvent
    public static void tickCounter(TickEvent.PlayerTickEvent event) {
        Player pPlayer = event.player;
        if (event.phase == TickEvent.Phase.START && !pPlayer.level().isClientSide()) {
            int strollTimer = pPlayer.getPersistentData().getInt("StrollTimer");
            double currentX = pPlayer.getPersistentData().getDouble("currentX");
            double currentY = pPlayer.getPersistentData().getDouble("currentY");
            double currentZ = pPlayer.getPersistentData().getDouble("currentZ");
            int locationKeeper = pPlayer.getPersistentData().getInt("Location");
            if (strollTimer == 1 && pPlayer instanceof ServerPlayer) {
                int strollCounter = pPlayer.getPersistentData().getInt("StrollCounter");
                strollCounter++;
                ((ServerPlayer) pPlayer).setGameMode(GameType.SPECTATOR);
                pPlayer.getPersistentData().putInt("StrollCounter", strollCounter);
                pPlayer.getPersistentData().putInt("Location", 1);
                if (strollCounter == 60) {
                    ((ServerPlayer) pPlayer).setGameMode(GameType.SURVIVAL);
                    pPlayer.getPersistentData().putInt("StrollTimer", 0);
                    pPlayer.getPersistentData().putInt("StrollCounter", 0);
                    pPlayer.getPersistentData().putInt("Location", 1);
                    pPlayer.teleportTo(currentX,currentY,currentZ);
                }
            }
            if (locationKeeper == 0) {
                currentX = pPlayer.getX();
                currentY = pPlayer.getY();
                currentZ = pPlayer.getZ();
            } else {
                currentX = 0;
                currentY = 0;
                currentZ = 0;
                pPlayer.getPersistentData().putInt("Location", 0);
            }
        }
    }
}
