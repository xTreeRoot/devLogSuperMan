package org.treeroot.devlog.business.view

/**
 * 错误回调处理器接口
 * 所有需要错误回调功能的ViewModel都应该实现此接口
 */
interface ErrorCallbackHandler {
    /**
     * 设置错误回调函数
     */
    fun setErrorCallback(errorCallback: (String) -> Unit)

    /**
     * 清除错误回调函数
     */
    fun clearErrorCallback()
}