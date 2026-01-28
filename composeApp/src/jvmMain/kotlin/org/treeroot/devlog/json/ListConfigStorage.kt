package org.treeroot.devlog.json

import com.google.gson.reflect.TypeToken

/**
 * 专门用于存储列表类型的配置存储基类
 * 处理 List<T> 类型的配置存储
 */
abstract class ListConfigStorage<T>(configFileName: String) : BaseConfigStorage<List<T>>(configFileName) {

    /**
     * 从文件加载配置列表
     */
    fun loadConfig(): List<T> {
        return try {
            if (configFile.exists() && configFile.length() > 0) {
                val jsonContent = configFile.readText()
                val listType = getListType()
                val configs = gson.fromJson<List<T>>(jsonContent, listType)

                // 如果解析失败，返回空列表
                configs ?: emptyList()
            } else {
                // 如果文件不存在，返回空列表
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            System.err.println("Failed to load config list: ${e.message}")
            // 返回空列表
            emptyList()
        }
    }

    /**
     * 获取泛型列表类型，用于Gson反序列化
     */
    protected abstract fun getListType(): java.lang.reflect.Type

    override fun getConfigClass(): Class<List<T>> {
        @Suppress("UNCHECKED_CAST")
        return List::class.java as Class<List<T>>
    }


}