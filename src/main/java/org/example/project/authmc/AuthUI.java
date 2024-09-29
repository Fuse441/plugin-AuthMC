package org.example.project.authmc;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.bukkit.ChatColor;
import me.clip.placeholderapi.PlaceholderAPI;
import java.io.*;


public class AuthUI implements Listener {
    private boolean loginSuccess = false;
    private final Plugin plugin;
    Inventory gui;
    private int maxPIN = 6;
    private int countPIN = 0;
    private  String currentPIN = "";
    private String userPIN = null;
    private  String title = "title";
    private  String failText = "failText";
    private  String subTitle = "Welcome";
    private  int CIR_isSelect = 0;
    private  int CIR_isNonSelect = 0;
    private  Material materialIsSelect = Material.EGG;
    private  Material materialIsNonSelect = Material.SNOWBALL;
    public AuthUI(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }
    public void readConfig(){
        try {
            File configFile = new File(plugin.getDataFolder(), "Config/config.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), "UTF-8"));

            StringBuilder jsonData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonData.append(line);
            }
            reader.close();

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonData.toString());

            JSONObject config = (JSONObject) json.get("configuration");
            title = ChatColor.translateAlternateColorCodes('&',config.get("title").toString());

            subTitle = ChatColor.translateAlternateColorCodes('&',config.get("subTitle").toString());
            failText = ChatColor.translateAlternateColorCodes('&',config.get("failText").toString());

            materialIsSelect = Material.valueOf(config.get("isSelect").toString());
            materialIsNonSelect = Material.valueOf(config.get("isNonSelect").toString());


            CIR_isSelect = Integer.parseInt(config.get("CIR_isSelect").toString());
            CIR_isNonSelect = Integer.parseInt(config.get("CIR_isNonSelect").toString());

        } catch (IOException | ParseException e) {
            plugin.getLogger().warning(e.toString());
            e.printStackTrace();
        }



    }
    public void openAuthGUI(Player player,String formName,String PIN) {
        readConfig();
        loginSuccess = false;
        int maxInven = 27;
        String title = "Login";
        userPIN = PIN;
        gui = Bukkit.getServer().createInventory(null, maxInven, formName);


        ItemStack snowball = new ItemStack(materialIsNonSelect);
        ItemMeta snowballMeta = snowball.getItemMeta();
        snowballMeta.setDisplayName("Enter to PIN");
        snowballMeta.setCustomModelData(CIR_isNonSelect);
        snowball.setItemMeta(snowballMeta);
        for (int i = 0; i < maxInven; i++) {
            gui.setItem(i, snowball);
        }

        player.openInventory(gui);

    }
    public void registerUser(Player player,String PIN){
        try {
            File authUserFile = new File(plugin.getDataFolder(), "AuthUser/user.json");
            BufferedReader reader = new BufferedReader(new FileReader(authUserFile));

            StringBuilder jsonData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonData.append(line);
            }
            reader.close();

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonData.toString());
            JSONArray usersArray = (JSONArray) json.get("users");

            JSONObject newUser = new JSONObject();
            newUser.put("Name", player.getName());
            newUser.put("Pin", PIN);
            newUser.put("UUID",player.getUniqueId().toString());
            usersArray.add(newUser);

            json.put("users", usersArray);

            FileWriter writer = new FileWriter(authUserFile);
            writer.write(json.toJSONString());
            writer.flush();
            writer.close();


        } catch (IOException | ParseException e) {
            plugin.getLogger().warning(e.toString());
            e.printStackTrace();
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {


        if (!event.getView().getTitle().equals("Login") && !event.getView().getTitle().equals("Register")) {

            return;
        }else {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            ItemStack clickedItem = event.getCurrentItem();


            ItemStack slimeball = new ItemStack(materialIsSelect);
            ItemMeta slimeballMeta = slimeball.getItemMeta();
            slimeballMeta.setDisplayName("PIN is selected");
            slimeballMeta.setCustomModelData(CIR_isSelect);
            slimeball.setItemMeta(slimeballMeta);

            ItemStack snowball = new ItemStack(materialIsNonSelect);
            ItemMeta snowballMeta = snowball.getItemMeta();
            snowballMeta.setDisplayName("Enter to PIN");
            snowballMeta.setCustomModelData(CIR_isNonSelect);
            snowball.setItemMeta(snowballMeta);



            if (clickedItem.getType() == materialIsNonSelect) {
                countPIN++;
                currentPIN += slot;

                if (countPIN == maxPIN) {
                    if (event.getView().getTitle().equals("Login")) {
                        if (currentPIN.equals(userPIN)) {
                            event.setCancelled(false);

                            loginSuccess = true;
                            currentPIN = "";
                            countPIN = 0;
                            player.sendTitle(title, subTitle);
                            player.closeInventory();

                            return;
                        } else {
                            setInit();

                            player.kickPlayer(failText);
                            return;
                        }
                    }else if(event.getView().getTitle().equals("Register")){
                        event.setCancelled(false);
                        registerUser(player,currentPIN);
                        loginSuccess = true;
                        currentPIN = "";
                        countPIN = 0;
                        player.sendTitle("Register Success", "Welcome to Server ");
                        player.closeInventory();


                    }

                }
                event.getInventory().setItem(slot, slimeball);
            }
            if (clickedItem.getType() == materialIsSelect) {
                if (countPIN != 0) {
                    countPIN--;
                    currentPIN = currentPIN.substring(0, currentPIN.length() - 1);
                }

                event.getInventory().setItem(slot, snowball);
            }
            plugin.getLogger().info(player.getName() + " clicked on slot " + slot + " count " + countPIN);



        }




    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(gui) && !loginSuccess) {
            setInit();
            Player player = (Player) event.getPlayer();
            player.kickPlayer("Invalid PIN. Please try again.");
        }
    }
    public void setInit(){
        loginSuccess = false;
        countPIN = 0;
        currentPIN = "";
    }
}
