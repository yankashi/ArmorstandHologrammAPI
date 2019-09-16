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
 * Class for chest holograms
 */
public class ShopHologram extends Hologram {

    //Tokens which are used in display
    private static String[] tokens = {"Owner", "Item", "Stock", "Buy", "Sell"};
    private static String spacer = ": ";
    private static String unit = "";

    /**
     * Getter for currently used tokens
     *
     * @return String[5]: Returns array with 5 tokens
     */
    @Contract(pure = true)
    public static String[] getTokens() {
        return tokens;
    }

    /**
     * Sets tokens which should be used for displaying
     *
     * @param newTokens(String[5]): Array with 5 tokens which should be used in displaying
     *
     * @throws IllegalArgumentException: Thrown if the array is not exactly 5 elements
     */
    public static void setTokens(@NotNull String[] newTokens) {
        if (newTokens.length != 5) throw new IllegalArgumentException("Must specify exactly 5 tokens");
        tokens = newTokens;
    }

    /**
     * Getter for unit
     *
     * @return String: Currently displayed unit
     */
    @Contract(pure = true)
    public static String getUnit() {
        return unit;
    }

    /**
     * Setter for unit
     *
     * @param newUnit(String): Unit which should be used in displaying
     */
    public static void setUnit(String newUnit) {
        unit = newUnit;
    }

    //Data of shop
    private Player owner;
    private String item;
    private int buy;
    private int sell;
    private int stock;

    /**
     * Package private constructor for ShopHologram. Should only be used internally
     *
     * @param armorStands(ArrayList[ArmorStand]): List with all armorstands
     */
    ShopHologram(List<ArmorStand> armorStands) {
        super(armorStands);
        ArmorStand base = armorStands.get(0);
        List<String> data = base.getLeggings().getItemMeta().getLore();
        sell = Integer.parseInt(data.get(0));
        buy = Integer.parseInt(data.get(1));
        stock = Integer.parseInt(data.get(2));
        item = data.get(3);
        owner = Bukkit.getServer().getPlayer(UUID.fromString(data.get(4)));
    }

    /**
     * Constructor for a shop hologram
     *
     * @param loc(Location): Location of the hologram
     * @param owner(Player): Player who owns the shop
     * @param buy(int): Prize to buy the item
     * @param item(ItemStack): Item which is to be sold
     * @param stock(int): Current stock
     * @param sell(int): Prize to sell the item for
     */
    public ShopHologram(Location loc, Player owner, int buy, @NotNull ItemStack item, int stock, int sell) {
        super(createStands(loc, owner, item.getType(), stock, buy, sell));
        this.owner = owner;
        this.item = item.getType().toString();
        this.stock = stock;
        this.buy = buy;
        this.sell = sell;
    }

    /**
     * Overloaded constructor call with buy prize 0
     *
     * @param loc(Location): Location of the hologram
     * @param owner(Player): Player who owns the shop
     * @param item(ItemStack): Item which is to be sold
     * @param stock(int): Current stock
     * @param sell(int): Prize to sell the item for
     */
    public ShopHologram(Location loc, Player owner, ItemStack item, int stock, int sell) {
        this(loc, owner, 0, item, stock, sell);
    }

    /**
     * Overloaded constructor call with sell prize 0
     *
     * @param loc(Location): Location of the hologram
     * @param owner(Player): Player who owns the shop
     * @param buy(int): Prize to buy the item
     * @param item(ItemStack): Item which is to be sold
     * @param stock(int): Current stock
     */
    public ShopHologram(Location loc, Player owner, int buy, ItemStack item, int stock) {
        this(loc, owner, buy, item, stock, 0);
    }

    /**
     * Getter for owner
     *
     * @return Player: Owner of the shop
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Setter for owner
     *
     * @param owner(Player): New owner of the shop
     */
    public void setOwner(Player owner) {
        this.owner = owner;
        //Owner is always on top
        armorStands.get(armorStands.size()-1).setCustomName(tokens[0]+spacer + owner);
    }

    /**
     * Getter for item
     *
     * @return Material: Material current item to sell or buy
     */
    public Material getItem() {
        return Material.valueOf(item);
    }

    /**
     * Setter for item
     *
     * @param item(ItemStack): New item to sell or buy
     */
    public void setItem(ItemStack item) {
        this.item = item.getType().toString();
        //Will always be penultimate
        armorStands.get(armorStands.size()-2).setCustomName(tokens[1]+spacer + item.toString());
    }

    /**
     * Getter for stock
     *
     * @return int: Current stock
     */
    public int getStock() {
        return stock;
    }

    /**
     * Setter for stock. Should be called on purchase or sale
     *
     * @param stock(int): New stock of item
     */
    public void setStock(int stock) {
        this.stock = stock;
        //Will always be last third
        armorStands.get(armorStands.size()-3).setCustomName(tokens[2]+spacer + stock);
    }

    /**
     * Getter for buy
     *
     * @return int: Current buy prize
     */
    public int getBuy() {
        return buy;
    }

    /**
     * Setter for buy
     *
     * @param buy(int): New buy prize
     */
    public void setBuy(int buy) {
        this.buy = buy;
        update();
    }

    /**
     * Getter for sell
     *
     * @return int: Current sell prize
     */
    public int getSell() {
        return sell;
    }

    /**
     * Setter for buy
     *
     * @param sell(int): New sell prize
     */
    public void setSell(int sell) {
        this.sell = sell;
        update();
    }

    /**
     * Method to update all stands with current data
     *
     * Updates with current tokens
     */
    @Override
    void update() {
        Location loc = armorStands.get(0).getLocation();
        this.suicide();
        this.armorStands = createStands(loc, owner, getItem(),stock, buy, sell);
    }

    /**
     * Method to retrieve a shop hologram from a specified location
     *
     * @param loc(Location): Location to look at
     *
     * @return ShopHologram if one is found and null if not
     */
    @Nullable
    public static ShopHologram getShopHolo(Location loc) {
        List<ArmorStand> armorStands = findStand(loc, Keys.SHOP);
        return armorStands != null ? new ShopHologram(armorStands) : null;
    }

    /**
     * Method to create a shop hologram. Internal use only
     *
     * @param location(Location): The location of where to create the hologram.
     * @param owner(Player): Owner of the shop
     * @param mat(Material): Item type which is being sold
     * @param stock(int): Current stock of item
     * @param buy(int): Buy prize for item
     * @param sell(int): Sell prize for item
     *
     * @return ArrayList[ArmorStand]: A list with all armorstands in the display
     */
    private static List<ArmorStand> createStands(@NotNull Location location, Player owner, Material mat, int stock, int buy, int sell) {
        //Copy location to be independent
        Location loc = location.clone();
        //Create items to store the data
        List<ArmorStand> armorStands = new ArrayList<>();
        ItemStack standsItem = new ItemStack(Material.STONE, 1);
        ItemMeta standsMeta = standsItem.getItemMeta();
        List<String> standsData = new ArrayList<>();
        ItemStack informationItem = new ItemStack(Material.STONE, 1);
        ItemMeta informationMeta = informationItem.getItemMeta();
        List<String> informationData = new ArrayList<>();
        //Only create a stand if needed
        if (sell != 0) {
            createStand(loc, tokens[4] + spacer + sell + unit, armorStands, standsData);
        }
        informationData.add(String.valueOf(sell));
        if (buy != 0) {
            createStand(loc, tokens[3] + spacer + buy + unit, armorStands, standsData);
        }
        //Create needed stands and add information
        informationData.add(String.valueOf(buy));
        createStand(loc, tokens[2] + spacer + stock, armorStands, standsData);
        informationData.add(String.valueOf(stock));
        createStand(loc, tokens[1] + spacer + mat.toString(), armorStands, standsData);
        informationData.add(mat.toString());
        createStand(loc, tokens[0] + spacer + owner.getName(), armorStands, standsData);
        informationData.add(owner.getUniqueId().toString());
        //Set data to item and store it inside base stand
        informationMeta.setLore(informationData);
        informationItem.setItemMeta(informationMeta);
        armorStands.get(0).setLeggings(informationItem);
        //Store stand UUIDs in item inside base stand
        standsMeta.setLore(standsData);
        standsMeta.setDisplayName(Keys.SHOP.getKey());
        standsItem.setItemMeta(standsMeta);
        armorStands.get(0).setChestplate(standsItem);
        return armorStands;
    }
}
