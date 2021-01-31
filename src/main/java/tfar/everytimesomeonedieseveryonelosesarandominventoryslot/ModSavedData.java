package tfar.everytimesomeonedieseveryonelosesarandominventoryslot;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import java.util.*;

public class ModSavedData extends WorldSavedData {

    private final HashSet<Integer> locked = new HashSet<>();

    private static final HashSet<Integer> all = new HashSet<>();

    private static final ItemStack LOCK = new ItemStack(Items.BARRIER);

    static {
        for (int i = 0; i < 41;i++) {
            all.add(i);
        }
        LOCK.getOrCreateTag().putBoolean("locked",true);
        LOCK.setDisplayName(new StringTextComponent("locked"));
    }

    public ModSavedData(String name) {
        super(name);
    }

    private static final String ID = "locked";

    public static ModSavedData getDefaultInstance(ServerWorld world) {
        return world.getServer().getWorld(World.OVERWORLD).getSavedData().getOrCreate(() -> new ModSavedData(ID), ID);//overworld storage
    }

    private static final Random rand = new Random();


    public void onPlayerClone(ServerWorld world, ServerPlayerEntity died) {
        List<Integer> allCopy = new ArrayList<>(all);
        allCopy.removeIf(locked::contains);
        if (!allCopy.isEmpty()) {
            int next = allCopy.get(rand.nextInt(allCopy.size()));
            locked.add(next);
            //mojang, why is it immutable?
            List<ServerPlayerEntity> otherplayers = new ArrayList<>(world.getServer().getPlayerList().getPlayers());
            otherplayers.add(died);

            for (ServerPlayerEntity player : otherplayers) {
                ItemStack lockCopy = LOCK.copy();
                lockCopy.getTag().putInt("slot",next);
                if (next == 40) {
                    player.inventory.offHandInventory.set(0, lockCopy);
                } else if (next > 35) {
                    int i = next - 36;
                    player.inventory.armorInventory.set(i, lockCopy);
                } else {
                    player.inventory.mainInventory.set(next, lockCopy);
                }
            }
            markDirty();
        }
    }

    @Override
    public void read(CompoundNBT nbt) {
        for (int i : nbt.getIntArray("locked")) {
            locked.add(i);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putIntArray("locked",toPrimitive(locked.toArray(new Integer[0])));
        return compound;
    }

    public static int[] toPrimitive(Integer[] IntegerArray) {
        return Arrays.stream(IntegerArray).mapToInt(integer -> integer).toArray();
    }
}
