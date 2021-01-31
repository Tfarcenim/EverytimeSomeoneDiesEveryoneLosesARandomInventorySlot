package tfar.everytimesomeonedieseveryonelosesarandominventoryslot;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EverytimeSomeoneDiesEveryoneLosesARandomInventorySlot.MODID)
public class EverytimeSomeoneDiesEveryoneLosesARandomInventorySlot {
    public static final String MODID = "everytimesomeonedieseveryonelosesarandominventoryslot";

    public EverytimeSomeoneDiesEveryoneLosesARandomInventorySlot() {
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerClone);
        MinecraftForge.EVENT_BUS.addListener(this::place);
    }

    public static boolean test(ItemStack stack) {
        return stack.getItem() == Items.BARRIER && stack.hasTag() && stack.getTag().getBoolean("locked");
    }

    public void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            copyLocked((ServerPlayerEntity)event.getOriginal(),(ServerPlayerEntity)event.getPlayer());
            ServerWorld world = (ServerWorld) event.getEntityLiving().world;
            ModSavedData modSavedData = ModSavedData.getDefaultInstance(world);
            modSavedData.onPlayerClone(world, (ServerPlayerEntity) event.getPlayer());
        }
    }

    public static void copyLocked(ServerPlayerEntity from, ServerPlayerEntity to) {
        PlayerInventory fromInv = from.inventory;
        PlayerInventory toInv = to.inventory;
        for(int i = 0; i < toInv.getSizeInventory(); ++i) {
            if (test(fromInv.getStackInSlot(i))) {
                toInv.setInventorySlotContents(i, fromInv.getStackInSlot(i));
            }
        }
        toInv.currentItem = fromInv.currentItem;
    }

    public void place(PlayerInteractEvent.RightClickBlock event) {
        if (test(event.getItemStack())) {
            event.setCanceled(true);
        }
    }

    public static boolean cancel(List<NonNullList<ItemStack>> allInventories,int index) {
        NonNullList<ItemStack> nonnulllist = null;

        for (NonNullList<ItemStack> nonnulllist1 : allInventories) {
            if (index < nonnulllist1.size()) {
                nonnulllist = nonnulllist1;
                break;
            }

            index -= nonnulllist1.size();
        }

        if (nonnulllist != null && !nonnulllist.get(index).isEmpty()) {
            ItemStack itemstack = nonnulllist.get(index);
            return EverytimeSomeoneDiesEveryoneLosesARandomInventorySlot.test(itemstack);
        }
        return false;
    }

}
