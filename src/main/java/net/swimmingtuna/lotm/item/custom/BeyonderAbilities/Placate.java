package net.swimmingtuna.lotm.item.custom.BeyonderAbilities;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.swimmingtuna.lotm.caps.BeyonderHolderAttacher;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class Placate extends Item {
    public Placate(Properties pProperties) {
        super(pProperties);
    }

    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        BeyonderHolderAttacher.getHolder(pPlayer).ifPresent(spectatorSequence -> {
            if (spectatorSequence.getCurrentSequence() <= 7 && spectatorSequence.useSpirituality(50)) {
                removeHarmfulEffects(pInteractionTarget);
            }
            if (spectatorSequence.getCurrentSequence() < 7 && spectatorSequence.getCurrentSequence() > 4 && spectatorSequence.useSpirituality(50)){
                halfHarmfulEffects(pInteractionTarget);
            }
            if (!pPlayer.getAbilities().instabuild) {
                pPlayer.getCooldowns().addCooldown(this, 120);
            }
        });
        return InteractionResult.SUCCESS;
    }

    private void removeHarmfulEffects(LivingEntity entity) {
        for (MobEffectInstance effect : entity.getActiveEffects()) {
            MobEffect type = effect.getEffect();
            if (!type.isBeneficial()) {
                entity.removeEffect(type);
            }
        }
    }

    private void halfHarmfulEffects(LivingEntity entity) {
        for (MobEffectInstance effect : entity.getActiveEffects()) {
            MobEffect type = effect.getEffect();
            if (!type.isBeneficial()) {
                int newDuration = (effect.getDuration() + 1) / 2;
                entity.addEffect(new MobEffectInstance(type, newDuration, effect.getAmplifier(), effect.isAmbient(), effect.isVisible()));
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level level, List<Component> componentList, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown()) {
            componentList.add(Component.literal("Upon use, reduces or removes the targeted living entity's harmful potion effects\n" +
                    "Spirituality Used: 125\n" +
                    "Cooldown: 15 seconds"));
        }
        super.appendHoverText(pStack, level, componentList, tooltipFlag);
    }
}