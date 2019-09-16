package me.yankashi.ArmorStandHologrammAPI.Hologramms;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Abstract class for holograms
 */
public abstract class Hologram {

    //List with containing stands
    List<ArmorStand> armorStands;

    /**
     * Constructor for any hologram
     *
     * @param armorStands(ArrayList[ArmorStand]): List with all armorstands which are used in the hologram
     */
    Hologram(List<ArmorStand> armorStands) {
        this.armorStands = armorStands;
    }

    /**
     * Getter for location of the display.
     * Is equal to location of base stand
     *
     * @return Copy of location of base stand or null if there is no base stand
     */
    public Location getLoc() {
        return armorStands.isEmpty() ? null : armorStands.get(0).getLocation().clone();
    }

    /**
     * Method to remove the hologram. Will remove all stands
     */
    public void suicide() {
        for (int i = armorStands.size()-1; i >= 0; i--) {
            armorStands.get(i).remove();
        }
        armorStands.clear();
    }

    /**
     * Utility function to summon a armorstand with a custom name at a given location
     *
     * @param loc(Location): Location of the armorstand
     * @param name(String): Name of the armorstand
     *
     * @return Armorstand: The summoned armorstand
     */
    private static ArmorStand summon(@NotNull Location loc, String name) {
        ArmorStand as = loc.getWorld().spawn(loc, ArmorStand.class);
        as.setCustomName(name);
        as.setCustomNameVisible(true);
        as.setGravity(false);
        as.setMarker(true);
        as.setVisible(false);
        return as;
    }

    /**
     * Package private utility function which creates the armorstand and allocates data
     *
     * @param loc(Location): Location of the stand
     * @param name(String): Name of the stand
     * @param stands(List[ArmorStand]): List to add stand to
     * @param UUIDs(List[String]): List of UUIDs of armorstands to be added to
     */
    static void createStand(Location loc, String name, @NotNull List<ArmorStand> stands, @NotNull List<String> UUIDs) {
        //Create a armorstand and add the data to the lists
        ArmorStand placeHolder = summon(loc, name);
        UUIDs.add(placeHolder.getUniqueId().toString());
        stands.add(placeHolder);
        //Move location up for next stand
        loc.add(0, 0.22, 0);
    }

    /**
     * Method which is to be implemented by all holograms. Will update with correct tokens
     */
    abstract void update();

    /**
     * Utility function to find a stand based on a given location
     *
     * @param loc(Location): The location to look for
     * @param keyword(Keys): The type stand to look for
     *
     * @return ArrayList[ArmorStand]: Stands of the hologram or null: if there is no hologram
     */
    @Nullable
    static List<ArmorStand> findStand(@NotNull Location loc, Keys keyword) {
        //Assume the chunk is not forcefully loaded
        boolean forceLoaded = false;
        //Create a list for the stands
        List<ArmorStand> armorStands = new ArrayList<>();
        //Look at the chunk and load it if needed
        Chunk c = loc.getChunk();
        if (!c.isLoaded()) {
            forceLoaded = true;
            c.load();
        }
        //Get all entities in the chunk
        List<Entity> entities = Arrays.asList(c.getEntities());
        //Armorstand <= Entities
        List<ArmorStand> stands = new ArrayList<>(entities.size());
        //Find all armorstands
        for (Entity e : entities) {
            if (e instanceof ArmorStand) {
                stands.add((ArmorStand) e);
            }
        }
        //Look for a key stand
        ArmorStand key = null;
        List<String> uuidStrings = new ArrayList<>();
        for (ArmorStand as : stands) {
            ItemStack item = as.getChestplate();
            if (item != null && item.getType() == Material.STONE) {
                ItemMeta meta = item.getItemMeta();
                //Key stand is found
                if (meta.getDisplayName().equalsIgnoreCase(keyword.getKey()) && as.getLocation().equals(loc)) {
                    uuidStrings = meta.getLore();
                    key = as;
                    //Remove from list to not iterate twice
                    stands.remove(as);
                    armorStands.add(as);
                    break;
                }
            }
        }
        //If no key stand was found return null and unload chunk if needed
        if (key == null) {
            if (forceLoaded) c.unload();
            return null;
        }
        //Go through all UUIDs and find corresponding stand
        for (String uuid : uuidStrings) {
            UUID toCompare = UUID.fromString(uuid);
            for (ArmorStand stand : stands) {
                if (stand.getUniqueId().equals(toCompare)) {
                    armorStands.add(stand);
                    //Remove stand from list to not iterate twice over it
                    stands.remove(stand);
                    break;
                }
            }
        }
        //Return list and unload chunk if needed
        if (forceLoaded) c.unload();
        return armorStands;
    }

    /**
     * Function to update all holograms.
     * Needs to be called separately for some reason
     */
    public static void updateAll() {
        //Go through all worlds and look for holograms
        for (World w : Bukkit.getServer().getWorlds()) {
            Collection<ArmorStand> allStands = w.getEntitiesByClass(ArmorStand.class);
            for (ArmorStand stand : allStands) {
                ItemStack item = stand.getChestplate();
                if (item != null && item.getType() == Material.STONE) {
                    String key = item.getItemMeta().getDisplayName();
                    Hologram hologram;
                    if (key.equals(Keys.PLOT.getKey())) {
                        hologram = getHolo(stand.getLocation(), Keys.PLOT);
                    } else if (key.equals(Keys.SHOP.getKey())) {
                        hologram = getHolo(stand.getLocation(), Keys.SHOP);
                    } else {
                        continue;
                    }
                    if (hologram != null) hologram.update();
                }
            }
        }
    }

    /**
     * Function to get a Hologram from a given location
     *
     * @param loc(Location): The location to look at
     * @param type(Keys): Type of the hologram
     *
     * @return Hologram if there is a hologram and null if not
     */
    @Nullable
    public static Hologram getHolo(Location loc, Keys type) {
        List<ArmorStand> armorStands = findStand(loc, type);
        if (armorStands == null) return null;
        switch (type) {
            case SHOP:
                return new ShopHologram(armorStands);
            case DISPLAY:
                return new DisplayHologram(armorStands);
            case PLOT:
                return new PlotHologram(armorStands);
            default:
                return null;
        }
    }
}


