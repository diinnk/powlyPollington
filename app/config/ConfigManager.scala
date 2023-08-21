package config

import com.typesafe.config.ConfigFactory
import common.{AutoClose, BasicLogUtil, DBType, FileDBType, MemoryDBType}

import javax.inject.Singleton

@Singleton
class ConfigManager
object ConfigManager extends BasicLogUtil with AutoClose {
  private val config = ConfigFactory.load()

  private def getOptionalConfigValue[T](path: String, default: T): T = if(config.hasPath(path)) config.getString(path).asInstanceOf[T] else default

  val title: String = getOptionalConfigValue("title", "PowlyPollington by Dinnk")
  val welcomeMessage: String = getOptionalConfigValue("welcomeMessage", title)

  val dbType: DBType = getOptionalConfigValue("db.type", "file") match {
    case s if s.toLowerCase.contains("mem") => MemoryDBType()
    case _ => FileDBType()
  }
  val dbFilePath: String = dbType match {
    case MemoryDBType() => "mem"
    case FileDBType() => getOptionalConfigValue("db.FilePath","./")
  }
  val dbFileUser: String = dbType match {
    case MemoryDBType() => ""
    case FileDBType() => getOptionalConfigValue("db.fileUser", "admin")
  }
  val dbFilePassword: String = dbType match {
    case MemoryDBType() => ""
    case FileDBType() => getOptionalConfigValue("db.filePassword", "adminPasswordChangeMe")
  }
  val limitAllRowCount: Int = getOptionalConfigValue("db.limitAllRowCount", 20)

  log.info(s"title: $title")
  log.info(s"welcomeMessage: $welcomeMessage")
  log.info(s"dbType: $dbType")
  log.info(s"dbFilePath: $dbFilePath")

  //TODO: remove me
  log.info(s"dbFileUser: $dbFilePassword")
  log.info(s"dbFilePassword: $dbFilePassword")
}
