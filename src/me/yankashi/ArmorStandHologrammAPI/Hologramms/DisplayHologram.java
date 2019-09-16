package me.yankashi.ArmorStandHologrammAPI.Hologramms;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Class for display holograms
 */
public class DisplayHologram extends Hologram {

    /**
     * Package private constructor for ShopHologram. Should only be used internally
     *
     * @param armorStands(ArrayList[ArmorStand]): List with all armorstands
     */
    DisplayHologram(List<ArmorStand> armorStands) {
        super(armorStands);
    }

    /**
     * Constructor for a display hologram
     *
     * @param loc(Location): Location of where to display the hologram
     * @param text(ArrayList[String]): Text which should be displayed
     */
    public DisplayHologram(Location loc, List<String> text) {
        super(createStands(loc, text));
    }

    /**
     * Getter for text
     *
     * @return List[String]: Text of hologram
     */
    public List<String> getText() {
        //Text is just names of stands
        List<String> text = new ArrayList<>();
        for (ArmorStand as : armorStands) {
            text.add(as.getCustomName());
        }
        return text;
    }

    /**
     * Setter for text
     *
     * @param text(List[String]): New text
     */
    public void setText(List<String> text) {
        //Remove old text and replace with new text
        Location loc = armorStands.get(0).getLocation();
        this.suicide();
        this.armorStands = createStands(loc, text);
    }

    /**
     * Method to retrieve a display hologram from a specified location
     *
     * @param loc(Location): Location to look at
     *
     * @return DisplayHologram if one is found and null if not
     */
    @Nullable
    public static DisplayHologram getDisplayHolo(Location loc) {
        List<ArmorStand> armorStands = findStand(loc, Keys.DISPLAY);
        return armorStands != null ? new DisplayHologram(armorStands) : null;
    }

    /**
     * Update method does nothing for display
     */
    @Override
    void update() {
        //Updating displays does not change anything
    }
    /**
     * Method to create a shop hologram. Internal use only
     *
     * @param location(Location): Where to create the display
     * @param text(List[String]): Text which is to be displayed
     *
     * @return ArrayList[ArmorStand]: A list with all armorstands in the display
     */
    private static List<ArmorStand> createStands(@NotNull Location location, @NotNull List<String> text) {
        //Copy location to be independent
        Location loc = location.clone();
        //Create item to store UUIDs in
        List<ArmorStand> stands = new ArrayList<>();
        ItemStack item = new ItemStack(Material.STONE, 1);
        ItemMeta meta = item.getItemMeta();
        //List for UUIDs of stands
        List<String> UUIDs = new ArrayList<>();
        //Create stands and store their UUID in list
        for (int i = text.size()-1; i >= 0; i--) {
            createStand(loc, text.get(i), stands, UUIDs);
        }
        //Set data in item and add it to base armorstand
        meta.setDisplayName(Keys.DISPLAY.getKey());
        meta.setLore(UUIDs);
        item.setItemMeta(meta);
        stands.get(0).setChestplate(item);
        return stands;
    }
}
