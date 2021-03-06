package moriyashiine.extraorigins.mixin;

import moriyashiine.extraorigins.common.registry.EOPowers;
import moriyashiine.extraorigins.common.registry.EOTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Shadow
	public abstract Item getItem();
	
	@Shadow
	public abstract boolean damage(int amount, Random random, @Nullable ServerPlayerEntity player);
	
	@Inject(method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At("HEAD"), cancellable = true)
	private <T extends LivingEntity> void damage(int amount, T entity, Consumer<T> breakCallback, CallbackInfo callbackInfo) {
		if (EOPowers.ALL_THAT_GLITTERS.get(entity) != null) {
			if (getItem() instanceof ToolItem) {
				if (EOTags.GOLDEN_TOOLS.contains(getItem())) {
					if (entity.world.random.nextFloat() < 15 / 16f) {
						callbackInfo.cancel();
					}
				}
				else if (entity.getRandom().nextBoolean()) {
					damage(1, entity.getRandom(), null);
				}
			}
			if (getItem() instanceof ArmorItem) {
				if (EOTags.GOLDEN_ARMOR_0.contains(getItem()) || EOTags.GOLDEN_ARMOR_1.contains(getItem())) {
					if (entity.world.random.nextFloat() < 3 / 4f) {
						callbackInfo.cancel();
					}
				}
				else if (entity.getRandom().nextBoolean()) {
					damage(1, entity.getRandom(), null);
				}
			}
		}
	}
	
	@Environment(EnvType.CLIENT)
	@Inject(method = "getTooltip", at = @At("RETURN"))
	private void getTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> callbackInfo) {
		if (EOPowers.ALL_THAT_GLITTERS.get(player) != null) {
			if (EOTags.GOLDEN_TOOLS.contains(getItem())) {
				callbackInfo.getReturnValue().add(4, new LiteralText(" ").append(new TranslatableText("attribute.modifier.equals.0", ItemStack.MODIFIER_FORMAT.format(2.5), new TranslatableText(EntityAttributes.GENERIC_ATTACK_DAMAGE.getTranslationKey())).formatted(Formatting.GOLD)));
			}
			if (getItem() instanceof ArmorItem) {
				int amount = 0;
				if (EOTags.GOLDEN_ARMOR_0.contains(getItem())) {
					amount = 1;
				}
				else if (EOTags.GOLDEN_ARMOR_1.contains(getItem())) {
					amount = 2;
				}
				if (amount != 0) {
					callbackInfo.getReturnValue().add(4, new TranslatableText("attribute.modifier.plus.0", ItemStack.MODIFIER_FORMAT.format(amount), new TranslatableText(EntityAttributes.GENERIC_ARMOR.getTranslationKey())).formatted(Formatting.GOLD));
				}
			}
		}
	}
}
