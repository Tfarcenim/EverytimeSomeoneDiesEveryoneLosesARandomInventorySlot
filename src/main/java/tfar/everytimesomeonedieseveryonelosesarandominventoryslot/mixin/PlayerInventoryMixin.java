package tfar.everytimesomeonedieseveryonelosesarandominventoryslot.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.everytimesomeonedieseveryonelosesarandominventoryslot.EverytimeSomeoneDiesEveryoneLosesARandomInventorySlot;

import java.util.List;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

	@Shadow @Final private List<NonNullList<ItemStack>> allInventories;

	@Shadow @Final public PlayerEntity player;

	@Inject(method = "decrStackSize",at = @At("HEAD"),cancellable = true)
	private void locked(int index, int count, CallbackInfoReturnable<ItemStack> cir) {
		if (EverytimeSomeoneDiesEveryoneLosesARandomInventorySlot.cancel(allInventories,index)) {
			cir.setReturnValue(ItemStack.EMPTY);
		}
	}

	@Inject(method = "removeStackFromSlot",at = @At("HEAD"),cancellable = true)
	private void locked(int index, CallbackInfoReturnable<ItemStack> cir) {
		if (EverytimeSomeoneDiesEveryoneLosesARandomInventorySlot.cancel(allInventories,index)) {
			cir.setReturnValue(ItemStack.EMPTY);
		}
	}

	//return true to skip dropping
	@Redirect(method = "dropAllItems",at = @At(value = "INVOKE",target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
	private boolean locked(ItemStack stack) {
		return stack.isEmpty() || EverytimeSomeoneDiesEveryoneLosesARandomInventorySlot.test(stack);
	}
}
