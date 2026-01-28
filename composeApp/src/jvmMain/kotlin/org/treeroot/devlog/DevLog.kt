package org.treeroot.devlog
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object DevLog {
    private val logger: Logger = LoggerFactory.getLogger("devLog_superMain")
    
    /**
     * 记录信息级别日志
     * @param message 日志消息
     * @param throwable 可选的异常对象
     */
    fun info(message: String, throwable: Throwable? = null) {
        logger.info(message, throwable)
    }
    
    /**
     * 记录警告级别日志
     * @param message 日志消息
     * @param throwable 可选的异常对象
     */
    fun warn(message: String, throwable: Throwable? = null) {
        logger.warn(message, throwable)
    }
    
    /**
     * 记录错误级别日志
     * @param message 日志消息
     * @param throwable 可选的异常对象
     */
    fun error(message: String, throwable: Throwable? = null) {
        logger.error(message, throwable)
    }
}

