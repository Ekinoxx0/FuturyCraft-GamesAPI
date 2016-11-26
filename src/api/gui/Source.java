package api.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Created by loucass003 on 24/11/16.
 */
public class Source {
    private Button button;
    private Inventory inventory;

    private Player source;

    public Source(Button button, Inventory inventory, Player source)
    {
        this.button = button;
        this.inventory = inventory;
        this.source = source;
    }

    public Button getButton() {
        return button;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Player getSource() {
        return source;
    }
}
