package org.treeroot.devlog.db

import org.treeroot.devlog.model.AppConfig
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class AppConfigDao(private val databasePath: String) {

    init {
        // 加载数据库驱动
        Class.forName("org.sqlite.JDBC")
        initializeDatabase()
    }

    private fun getConnection(): Connection {
        return DriverManager.getConnection("jdbc:sqlite:$databasePath")
    }

    private fun initializeDatabase() {
        val connection = getConnection()
        try {
            val statement = connection.createStatement()
            statement.executeUpdate(
                """CREATE TABLE IF NOT EXISTS app_config (
                    id INTEGER NOT NULL PRIMARY KEY DEFAULT 1,
                    background_image_path TEXT DEFAULT '',
                    background_opacity REAL NOT NULL DEFAULT 1.0,
                    enable_clipboard_monitor INTEGER NOT NULL DEFAULT 0
                )"""
            )
            statement.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection.close()
        }
    }

    fun getConfig(): AppConfig {
        val connection = getConnection()
        try {
            val statement = connection.prepareStatement("SELECT * FROM app_config WHERE id = ?")
            statement.setInt(1, 1) // 固定ID
            val resultSet = statement.executeQuery()

            return if (resultSet.next()) {
                AppConfig(
                    id = resultSet.getLong("id"),
                    backgroundImagePath = resultSet.getString("background_image_path") ?: "",
                    backgroundOpacity = resultSet.getDouble("background_opacity").toFloat(),
                    enableClipboardMonitor = resultSet.getInt("enable_clipboard_monitor") == 1
                )
            } else {
                // 如果没有记录，返回默认配置
                AppConfig()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            return AppConfig() // 返回默认配置
        } finally {
            connection.close()
        }
    }

    fun saveConfig(config: AppConfig) {
        val connection = getConnection()
        try {
            val statement = connection.prepareStatement(
                """INSERT OR REPLACE INTO app_config 
                (id, background_image_path, background_opacity, enable_clipboard_monitor) 
                VALUES (?, ?, ?, ?)"""
            )
            statement.setLong(1, config.id)
            statement.setString(2, config.backgroundImagePath)
            statement.setDouble(3, config.backgroundOpacity.toDouble())
            statement.setInt(4, if (config.enableClipboardMonitor) 1 else 0)

            statement.executeUpdate()
            statement.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection.close()
        }
    }

    fun configExists(): Boolean {
        val connection = getConnection()
        try {
            val statement = connection.prepareStatement("SELECT COUNT(*) FROM app_config WHERE id = ?")
            statement.setLong(1, 1)
            val resultSet = statement.executeQuery()

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection.close()
        }
        return false
    }
}