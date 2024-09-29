package org.example.project.authmc;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.bukkit.Bukkit.getLogger;

public class PlayerJoinListener implements Listener {
    private final Plugin plugin; // ประกาศตัวแปร Plugin
    private final AuthUI authUI;
    public PlayerJoinListener(Plugin plugin) {
        this.plugin = plugin; // กำหนดค่า plugin จาก constructor
        this.authUI = new AuthUI(plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();


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

            // ตรวจสอบว่าชื่อผู้เล่นมีอยู่ในข้อมูล JSON หรือไม่
            JSONArray usersArray = (JSONArray) json.get("users");

            boolean playerExists = false;
            String PIN = null;
            for (Object userObject : usersArray) {
                JSONObject user = (JSONObject) userObject;
                if (user.get("Name").equals(playerName)) {
                    PIN = (String) user.get("Pin");
                    playerExists = true;
                    break;
                }
            }

            if (playerExists) {
                getLogger().info("Player " + playerName + " exists in the user data.");

                authUI.openAuthGUI(event.getPlayer(),"Login",PIN);
            } else {
                getLogger().info("Player " + playerName + " does not exist in the user data.");
                authUI.openAuthGUI(event.getPlayer(),"Register",PIN);
            }
        } catch (IOException | ParseException e) {
            getLogger().warning(e.toString());
            e.printStackTrace();
        }
    }
}
