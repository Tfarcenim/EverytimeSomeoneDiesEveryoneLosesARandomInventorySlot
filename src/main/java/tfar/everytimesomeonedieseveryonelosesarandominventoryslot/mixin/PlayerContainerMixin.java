package tfar.everytimesomeonedieseveryonelosesarandominventoryslot.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.everytimesomeonedieseveryonelosesarandominventoryslot.EverytimeSomeoneDiesEveryoneLosesARandomInventorySlot;

import javax.annotation.Nullable;

@Mixin(PlayerContainer.class)
abstract class PlayerContainerMixin extends Container {

    protected PlayerContainerMixin(@Nullable ContainerType<?> type, int id) {
        super(type, id);
    }

    @Inject(method = "transferStackInSlot",at = @At("HEAD"),cancellable = true)
    private void no(PlayerEntity playerIn, int index, CallbackInfoReturnable<ItemStack> cir) {
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && EverytimeSomeoneDiesEveryoneLosesARandomInventorySlot.test(slot.getStack())) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}
