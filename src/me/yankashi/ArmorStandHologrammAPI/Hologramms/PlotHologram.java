package me.yankashi.ArmorStandHologrammAPI.Hologramms;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Class for shop holograms
 */
public class PlotHologram extends Hologram{

    //Tokens which are used in display
    private static String[] tokens = {"Owner", "ID", "Plotname", "Tenants"};
    //Max number of tenants which should be displayed
    private static int maxTenants = 5;

    /**
     * Getter for maxTenants
     *
     * @return int: Maximum number of tenants which should be displayed
     */
    @Contract(pure = true)
    public static int getMaxTenants() {
        return maxTenants;
    }

    /**
     * Setter for maxTenants
     *
     * @param newMaxTenants(int): New number of max Tenants
     */
    public static void setMaxTenants(int newMaxTenants) {
        maxTenants = newMaxTenants;
    }

    /**
     * Getter for tokens
     *
     * @return String[4]: The four tenants which are used in displaying
     */
    @Contract(pure = true)
    public static String[] getTokens() {
        return tokens;
    }

    /**
     * Setter for tokens
     *
     * @param newTokens(String[4]): Array with new tokens to display
     *
     * @throws IllegalArgumentException: If array is not exactly 4 tokens
     */
    public static void setTokens(@NotNull String[] newTokens) {
        if (newTokens.length != 4) throw new IllegalArgumentException("Must specify exactly 4 tokens");
        tokens = newTokens;
    }

    //Data of plot
    private Player owner;
    private int id;
    private String plotName;
    private boolean showsPlotname;
    private List<Player> tenants;
    private boolean showTenants;

    /**
     * Package private constructor for ShopHologram. Should only be used internally
     *
     * @param armorStands(ArrayList[ArmorStand]): List with all armorstands
     */
    PlotHologram(List<ArmorStand> armorStands) {
        super(armorStands);
        //Get the base stand
        ArmorStand base = armorStands.get(0);
        //Retrieve information from item
        List<String> information = base.getLeggings().getItemMeta().getLore();
        owner = Bukkit.getServer().getPlayer(UUID.fromString(information.get(0)));
        id = Integer.parseInt(information.get(1));
        plotName = information.get(2);
        showsPlotname = Boolean.parseBoolean(information.get(3));
        showTenants = Boolean.parseBoolean(information.get(4));
        //Retrieve tenants from item
        List<String> tenantsUUIDs = base.getBoots().getItemMeta().getLore();
        //Only display up to maxTenants tenants
        if (tenantsUUIDs != null) {
            int max = tenantsUUIDs.size() < maxTenants ? tenantsUUIDs.size() : maxTenants;
            tenantsUUIDs = tenantsUUIDs.subList(0, max);
            List<Player> players = new ArrayList<>(tenantsUUIDs.size());
            for (String uuid : tenantsUUIDs) {
                players.add(Bukkit.getServer().getPlayer(UUID.fromString(uuid)));
            }
            tenants = players;
        } else {
            tenants = new ArrayList<>();
        }
    }

    /**
     * Constructor for PlotHologram
     *
     * @param loc(Location): Location of where to create the hologram
     * @param owner(Player): Owner of the plot
     * @param id(int): ID of the plot
     * @param plotName(String): Name of the plot
     * @param showName(boolean): If the name should be displayed or not
     */
    public PlotHologram(Location loc, Player owner, int id, boolean showName, String plotName, boolean showTenants) {
        super(createStands(loc, owner, id, plotName, showName, new ArrayList<>(), showTenants));
        this.owner = owner;
        this.id = id;
        this.plotName = plotName;
        this.showsPlotname = showName;
        this.tenants = new ArrayList<>();
        this.showTenants = true;
    }

    /**
     * Overloaded constructor for PlotHologram. Will display plotname by default
     *
     * @param loc(Location): Location of where to create the hologram
     * @param owner(Player): Owner of the plot
     * @param id(int): ID of the plot
     * @param plotName(String): Name of the plot
     */
    public PlotHologram(Location loc, Player owner, int id, String plotName, boolean showTenants) {
        this(loc, owner, id, true, plotName, showTenants);
    }

    /**
     * Overloaded constructor for PlotHologram. Will display plotname by default and not show tenants
     *
     * @param loc(Location): Location of where to create the hologram
     * @param owner(Player): Owner of the plot
     * @param id(int): ID of the plot
     * @param plotName(String): Name of the plot
     */
    public PlotHologram(Location loc, Player owner, int id, String plotName) {
        this(loc, owner, id, true, plotName, false);
    }


    /**
     * Overloaded constructor for PlotHologram without a plotname and wont show tenants
     *
     * @param loc(Location): Location of where to create the hologram
     * @param owner(Player): Owner of the plot
     * @param id(int): ID of the plot
     */
    public PlotHologram(Location loc, Player owner, int id) {
        this(loc, owner, id, false,"§Default§", false);
    }

    /**
     * Getter for owner
     *
     * @return Player: Owner of the plot
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Setter for owner
     *
     * @param owner(Player): The new owner of the plot
     */
    public void setOwner(Player owner) {
        this.owner = owner;
        update();
    }

    /**
     * Getter for id
     *
     * @return int: The id of the plot
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for plot
     *
     * @param id: New id of plot
     */
    public void setId(int id) {
        this.id = id;
        update();
    }

    /**
     * Getter for plotname
     *
     * @return String: Name of plot or null: No name was given
     */
    public String getPlotName() {
        return plotName.equals("§Default§") ? null : plotName;
    }

    /**
     * Setter for plotName
     *
     * @param plotName(String): New name of the plot
     */
    public void setPlotName(String plotName) {
        this.plotName = plotName;
        update();
    }

    /**
     * Getter for tenants
     *
     * @return List[Player]: List with all displayed tenants
     */
    public List<Player> getTenants() {
        return tenants;
    }

    /**
     * Setter for tenants
     *
     * @param newTenants(List[Player]): List with new tenants
     * @see #maxTenants
     */
    public void setTenants(List<Player> newTenants) {
        int max = newTenants.size() < maxTenants ? newTenants.size() : maxTenants;
        tenants = newTenants.subList(0, max);
        update();
    }

    /**
     * Removes all tenants from plot
     */
    public void removeTenants() {
        tenants = new ArrayList<>();
        update();
    }

    /**
     * Adds a tenant to the plot
     *
     * @param p(Player): Player which is to be added
     */
    public void addTenant(Player p) {
        if (tenants.size() < maxTenants) {
            tenants.add(p);
            update();
        }
    }

    /**
     * Removes the tenant from the plot
     *
     * @param p(Player): Player which is to be removed
     */
    public void removeTenant(Player p) {
        if (tenants.remove(p)) update();
    }

    /**
     * Getter for showTenants
     *
     * @return boolean: Whether or not tenants are shown
     */
    public boolean isShowTenants() {
        return showTenants;
    }

    /**
     * Setter for tenants
     *
     * @param showTenants(boolean): Whether or not tenants are shown
     */
    public void setShowTenants(boolean showTenants) {
        this.showTenants = showTenants;
        update();
    }

    /**
     * Getter for showsPlotname
     *
     * @return boolean: Whether plotname is shown or not
     */
    public boolean isShowsPlotname() {
        return this.showsPlotname;
    }

    /**
     * Setter fof showsPlotname
     *
     * @param val(boolean): Whether or not the name should be displayed
     */
    public void showPlotname(boolean val) {
        this.showsPlotname = val;
        update();
    }

    /**
     * Method to update all stands with current data
     *
     * Updates with current tokens
     */
    @Override
    void update() {
        Location oldLoc = armorStands.get(0).getLocation();
        this.suicide();
        armorStands = createStands(oldLoc, owner, id, plotName, showsPlotname, tenants, showTenants);
    }

    /**
     * Method to retrieve a plot hologram from a specified location
     *
     * @param loc(Location): Location to look at
     *
     * @return PlotHologram if one is found and null if not
     */
    @Nullable
    public static PlotHologram getPlotHolo(Location loc) {
        List<ArmorStand> armorStands = findStand(loc, Keys.PLOT);
        return armorStands != null ? new PlotHologram(armorStands) : null;
    }

    /**
     * Method to create a plot hologram. Internal use only
     *
     * @param location(Location): The location of where to create the hologram.
     * @param owner(Player): Owner of the plot
     * @param id(int): ID of the plot
     * @param plotName(String): Name of the plot
     * @param showName(boolean): If the plotname should be displayed or not
     * @param tenants(List[Player]): List of tenants
     *
     * @return ArrayList[ArmorStand]: A list with all armorstands in the display
     */
    private static List<ArmorStand> createStands(@NotNull Location location, @NotNull Player owner, int id, String plotName, boolean showName, List<Player> tenants, boolean showTenants) {
        //Copy location to be independent
        Location loc = location.clone();
        //Create list for all stands
        List<ArmorStand> armorStands = new ArrayList<>();
        //Create items to store stands, information and tenants
        ItemStack standsItem = new ItemStack(Material.STONE, 1);
        ItemMeta standsMeta = standsItem.getItemMeta();
        List<String> standsData = new ArrayList<>();
        ItemStack informationItem = new ItemStack(Material.STONE, 1);
        ItemMeta informationMeta = informationItem.getItemMeta();
        List<String> informationData = new ArrayList<>();
        ItemStack tenantItem = new ItemStack(Material.STONE, 1);
        ItemMeta tenantMeta = tenantItem.getItemMeta();
        List<String> tenantData = new ArrayList<>();
        //Set the information
        informationData.add(owner.getUniqueId().toString());
        informationData.add(String.valueOf(id));
        informationData.add(plotName);
        informationData.add(String.valueOf(showName));
        informationData.add(String.valueOf(showTenants));
        informationMeta.setLore(informationData);
        informationItem.setItemMeta(informationMeta);
        //Space which is used in display
        String spacer = ": ";
        //Only display tenants if needed
        if (showTenants && !tenants.isEmpty()) {
            int max = tenants.size() < maxTenants ? tenants.size() : maxTenants;
            for (int i = 0; i < max; i++) {
                String tenantName = tenants.get(i).getName();
                createStand(loc, tenantName, armorStands, standsData);
                tenantData.add(tenants.get(i).getUniqueId().toString());
            }
            tenantMeta.setLore(tenantData);
            tenantItem.setItemMeta(tenantMeta);
            createStand(loc, tokens[3] + spacer, armorStands, standsData);
        }
        //Only display plotname if needed
        if (showName && !plotName.equals("§Default§")) {
            createStand(loc, tokens[2] + spacer + plotName, armorStands, standsData);
        }
        //Create needed stands
        createStand(loc, tokens[1] + spacer + id, armorStands, standsData);
        createStand(loc, tokens[0] + spacer + owner.getName(), armorStands, standsData);
        //Set data to item
        standsMeta.setLore(standsData);
        standsMeta.setDisplayName(Keys.PLOT.getKey());
        standsItem.setItemMeta(standsMeta);
        //Set items in base stand
        ArmorStand base = armorStands.get(0);
        base.setChestplate(standsItem);
        base.setLeggings(informationItem);
        base.setBoots(tenantItem);
        return armorStands;
    }
}
