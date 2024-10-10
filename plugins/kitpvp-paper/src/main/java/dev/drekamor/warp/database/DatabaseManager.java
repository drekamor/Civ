package dev.drekamor.warp.database;

import dev.drekamor.warp.util.Warp;
import net.civmc.kitpvp.KitPvpPlugin;
import vg.civcraft.mc.civmodcore.dao.ManagedDatasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {
    private final KitPvpPlugin plugin;
    private final ManagedDatasource dataSource;

    public DatabaseManager (KitPvpPlugin plugin, ManagedDatasource dataSource) {
        this.plugin = plugin;
        this.dataSource = dataSource;
        this.plugin.info("Initialised a database connection");


        initialiseTables();
        dataSource.updateDatabase();
    }

    public Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to obtain a data source");
            plugin.getLogger().severe(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }

    private void initialiseTables() {
        dataSource.registerMigration(2, false, """
            CREATE TABLE IF NOT EXISTS warps (
                name VARCHAR(64) UNIQUE PRIMARY KEY NOT NULL,
                world VARCHAR(64) NOT NULL,
                x REAL NOT NULL,
                y REAL NOT NULL,
                z REAL NOT NULL,
                pitch REAL NOT NULL,
                yaw REAL NOT NULL,
                gamemode VARCHAR(16) NOT NULL
            );
            """);

    }

    public Map<String, Warp> getWarps() {

        Map<String, Warp> warps = new HashMap<>();
        try (Connection connection = this.getConnection();){
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM warps;");
            ResultSet results = statement.executeQuery();
            while(results.next()) {
                warps.put(
                        results.getString("name"),
                        new Warp(
                                results.getString("name"),
                                results.getString("world"),
                                results.getDouble("x"),
                                results.getDouble("y"),
                                results.getDouble("z"),
                                results.getFloat("pitch"),
                                results.getFloat("yaw"),
                                results.getString("gamemode")
                        )
                );
            }
            connection.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to retrieve warps");
            plugin.getLogger().severe(Arrays.toString(e.getStackTrace()));
        }
        return warps;
    }

    public List<String> getWarpNames() {
        List<String> warps = new ArrayList<>();
        try(Connection connection = this.getConnection()) {
            ResultSet results = connection.prepareStatement("SELECT name FROM warps;").executeQuery();
            while (results.next()) {
                warps.add(results.getString("name"));
            }
            connection.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to retrieve warp names");
            plugin.getLogger().severe(Arrays.toString(e.getStackTrace()));
        }
        return warps;
    }

    public boolean addWarp(Warp warp) {
        try (Connection connection = this.getConnection()){
            PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO warps(name, world, x, y, z, pitch, yaw, gamemode) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, warp.name());
            statement.setString(2, warp.world());
            statement.setDouble(3, warp.x());
            statement.setDouble(4, warp.y());
            statement.setDouble(5, warp.z());
            statement.setFloat(6, warp.pitch());
            statement.setFloat(7, warp.yaw());
            statement.setString(8, warp.gamemode());
            statement.execute();
            connection.close();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to save warp %s".formatted(warp.name()));
            plugin.getLogger().severe(Arrays.toString(e.getStackTrace()));
        }
        return false;
    }

    public boolean deleteWarp(String name) {
        try (Connection connection = this.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM warps WHERE name=?;");
            statement.setString(1, name);
            statement.execute();
            connection.close();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to delete warp %s".formatted(name));
            plugin.getLogger().severe(Arrays.toString(e.getStackTrace()));
        }
        return false;
    }
}
