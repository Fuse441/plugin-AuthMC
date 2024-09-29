package org.example.project.authmc;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class FolderAuth {

    public void createConfigFile(JavaPlugin plugin) {
        // สร้างโฟลเดอร์ AuthUser
        File configFolder = new File(plugin.getDataFolder(), "Config");
        if (!configFolder.exists()) {
            if (configFolder.mkdirs()) {
                plugin.getLogger().info("Config folder created.");
            } else {
                plugin.getLogger().severe("Failed to create Config folder.");
                return;
            }
        } else {
            plugin.getLogger().info("Config folder already exists.");
        }

        // สร้างไฟล์ user.json ในโฟลเดอร์ AuthUser
//        File configFile = new File(configFolder, "config.json");
//        if (!configFile.exists()) {
//            try {
//                if (configFile.createNewFile()) {
//                    // เขียนเนื้อหาเริ่มต้นลงใน user.json
//                    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(configFile), "UTF-8");
//
//                    writer.write("{\n\t\"configuration\": {}\n}");
//                    writer.flush();
//                    writer.close();
//                    plugin.getLogger().info("config.json file created.");
//                } else {
//                    plugin.getLogger().severe("Failed to create config.json file.");
//                }
//            } catch (IOException e) {
//                plugin.getLogger().severe("Could not create config.json file!");
//                e.printStackTrace();
//            }
//        } else {
//            plugin.getLogger().info("config.json file already exists.");
//        }
    }
    public void writeConfigValue(JavaPlugin plugin) {

            File configFile = new File(plugin.getDataFolder(), "Config/config.json");
            if (!configFile.exists()) {
                try {
                    if (configFile.createNewFile()) {

                        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(configFile), "UTF-8");

                        writer.write("{\n\t\"configuration\": {}\n}");
                        writer.flush();
                        writer.close();
                        plugin.getLogger().info("config.json file created.");


                        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), "UTF-8"));
                        StringBuilder jsonData = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            jsonData.append(line);
                        }
                        reader.close();

                        JSONParser parserConfig = new JSONParser();
                        JSONObject json = (JSONObject) parserConfig.parse(jsonData.toString());
                        plugin.getLogger().info(" JSON: " + jsonData);
                        JSONObject config = (JSONObject) json.get("configuration");

                        String[] key = {"CIR_isSelect","CIR_isNonSelect","failText","title", "subTitle", "isSelect", "isNonSelect"};
                        String[] value = {"100","200","Fail PIN","Login Success", "Welcome to Server", "SLIME_BALL","SNOWBALL"};

                        for (int i = 0; i < key.length; i++) {
                            config.put(key[i], value[i]);
                        }
                        json.put("configuration", config);
                        plugin.getLogger().info("Updated JSON: " + json.toJSONString());

                        FileWriter writerConfig = new FileWriter(configFile);
                        writerConfig.write(json.toJSONString());
                        writerConfig.flush();
                        writerConfig.close();

                    } else {
                        plugin.getLogger().severe("Failed to create config.json file.");
                    }
                } catch (IOException e) {
                    plugin.getLogger().severe("Could not create config.json file!");
                    e.printStackTrace();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else {
                plugin.getLogger().info("config.json file already exists.");
            }

    }
    public void createAuthUserFolderAndFile(JavaPlugin plugin) {
        // สร้างโฟลเดอร์ AuthUser
        File authUserFolder = new File(plugin.getDataFolder(), "AuthUser");
        if (!authUserFolder.exists()) {
            if (authUserFolder.mkdirs()) {
                plugin.getLogger().info("AuthUser folder created.");
            } else {
                plugin.getLogger().severe("Failed to create AuthUser folder.");
                return;
            }
        } else {
            plugin.getLogger().info("AuthUser folder already exists.");
        }

        // สร้างไฟล์ user.json ในโฟลเดอร์ AuthUser
        File userFile = new File(authUserFolder, "user.json");
        if (!userFile.exists()) {
            try {
                if (userFile.createNewFile()) {
                    // เขียนเนื้อหาเริ่มต้นลงใน user.json
                    FileWriter writer = new FileWriter(userFile);
                    writer.write("{\n\t\"users\": []\n}");
                    writer.flush();
                    writer.close();
                    plugin.getLogger().info("user.json file created.");
                } else {
                    plugin.getLogger().severe("Failed to create user.json file.");
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create user.json file!");
                e.printStackTrace();
            }
        } else {
            plugin.getLogger().info("user.json file already exists.");
        }
    }
}
