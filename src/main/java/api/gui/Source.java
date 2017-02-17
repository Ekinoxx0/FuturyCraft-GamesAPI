package api.gui;

import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Created by loucass003 on 24/11/16.
 */
@Data
public class Source
{
	private final Button button;
	private final Inventory inventory;
	private final Player source;
}
