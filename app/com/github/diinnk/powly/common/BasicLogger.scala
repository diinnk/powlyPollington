package com.github.diinnk.powly.common

import org.slf4j.{Logger, LoggerFactory}

object BasicLogger {
  def apply(clazz: Class[_]): BasicLogger = new BasicLogger(LoggerFactory.getLogger(clazz))
}

class BasicLogger private(val logger: Logger) extends Serializable {
  // Error
  def error(message: String): Unit = {
    val extras = List.empty
    logger.error(message, extras:_*)
  }
  def error(message: String, cause: Throwable): Unit =
    logger.error(message, cause)

  // Warn
  def warn(message: String): Unit = {
    val extras = List.empty
    logger.warn(message, extras:_*)
  }
  def warn(message: String, cause: Throwable): Unit =
    logger.warn(message, cause)

  // Info
  def info(message: String): Unit = {
    val extras = List.empty
    logger.info(message, extras:_*)
  }
  def info(message: String, cause: Throwable): Unit =
    logger.info(message, cause)
}

