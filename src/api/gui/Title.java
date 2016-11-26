package api.gui;

import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by loucass003 on 20/11/16.
 */
public class Title {

    private class TitleData {
        public String text;
        public String color;
    }


    private int fadeIn;
    private int fadeOut;
    private int stay;

    private final TitleData data;
    private Title sub;

    private Title(String text, ChatColor color, int fadeIn, int stay, int fadeOut)
    {
        this.data = new TitleData();
        this.data.text = text;
        this.data.color = color.name().toLowerCase();
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    public Title(String text, ChatColor color)
    {
        this(text, color, 5, 20, 5);
    }

    public Title(String text)
    {
        this(text, ChatColor.WHITE, 5, 20, 5);
    }

    public Title()
    {
        this("", ChatColor.WHITE, 5, 20, 5);
    }

    public Title setFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
        return this;
    }

    public Title setFadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
        return this;
    }

    public Title setStay(int stay) {
        this.stay = stay;
        return this;
    }

    public Title setText(String text) {
        this.data.text = text;
        return this;
    }

    public Title setColor(ChatColor color) {
        this.data.color = color.name().toLowerCase();
        return this;
    }

    public Title setSubtitle(Title sub)
    {
        this.sub = sub;
        return this;
    }

    public void sendTitle(Player p)
    {
        if(this.sub != null)
            this.sub.sendTitle(EnumTitleAction.SUBTITLE, p);
        this.sendTitle(EnumTitleAction.TITLE, p);
    }

    private void sendTitle(EnumTitleAction type, Player p)
    {
        IChatBaseComponent chatTitle = ChatSerializer.a(new Gson().toJson(this.data));
        PacketPlayOutTitle title = new PacketPlayOutTitle(type, chatTitle);
        PacketPlayOutTitle length = new PacketPlayOutTitle(this.fadeIn, this.stay, this.fadeOut);
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(title);
        if(type == EnumTitleAction.TITLE)
            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(length);
    }
}
