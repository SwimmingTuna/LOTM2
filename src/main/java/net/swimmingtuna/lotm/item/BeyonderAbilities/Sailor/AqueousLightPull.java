package net.swimmingtuna.lotm.item.BeyonderAbilities.Sailor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.swimmingtuna.lotm.LOTM;
import net.swimmingtuna.lotm.caps.BeyonderHolder;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import net.swimmingtuna.lotm.entity.AqueousLightEntityPull;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = LOTM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AqueousLightPull extends Item {
    public AqueousLightPull(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        if (!pPlayer.level().isClientSide()) {
            BeyonderHolder holder = BeyonderHolderAttacher.getHolder(pPlayer).orElse(null);
            if (!holder.isSailorClass()) {
                pPlayer.displayClientMessage(Component.literal("You are not of the Sailor pathway").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }
            if (holder.getSpirituality() < 75) {
                pPlayer.displayClientMessage(Component.literal("You need 75 spirituality in order to use this").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE), true);
            }

        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(tyrantSequence -> {
            if (holder.isSailorClass() && tyrantSequence.getCurrentSequence() <= 7 && tyrantSequence.useSpirituality(75)) {
                shootLight(pPlayer);
            }
            if (!pPlayer.getAbilities().instabuild)
                pPlayer.getCooldowns().addCooldown(this, 60);

        });
            }
        return super.use(level, pPlayer, hand);
    }

    public static void shootLight(Player pPlayer) {
        Vec3 eyePosition = pPlayer.getEyePosition(1.0f);
        Vec3 direction = pPlayer.getViewVector(1.0f);
        Vec3 initialVelocity = direction.scale(2.0);
        AqueousLightEntityPull.summonEntityWithSpeed(direction, initialVelocity, eyePosition, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), pPlayer);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, shoots a projectile that upon hit, pulls the target towards the user\n" +
                    "Spirituality Used: 75\n" +
                    "Cooldown: 3 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
}