package org.example.project.authmc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuthMC extends JavaPlugin {
    private final FolderAuth folder = new FolderAuth();

    @Override
    public void onEnable() {
        // Plugin startup logic
        folder.createConfigFile(this);
        folder.writeConfigValue(this);
        folder.createAuthUserFolderAndFile(this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getLogger().info("AuthMC has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("AuthMC has been disabled!");
    }
}
