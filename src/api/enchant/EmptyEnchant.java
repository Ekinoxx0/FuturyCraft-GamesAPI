package api.enchant;


import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

/**
 * Created by loucass003 on 24/11/16.
 */
public class EmptyEnchant extends Enchantment {

    public EmptyEnchant(int id) {
        super(id);
    }

    @Override
    public boolean canEnchantItem(ItemStack arg0) {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment arg0) {
        return false;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return null;
    }

    @Override
    public int getMaxLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getStartLevel() {
        return 0;
    }

}

