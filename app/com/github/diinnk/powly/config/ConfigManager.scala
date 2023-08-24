package com.github.diinnk.powly.config

import com.github.diinnk.powly.VersionInfo
import com.github.diinnk.powly.common.{AutoClose, BasicLogUtil, DBType, FileDBType, MemoryDBType}
import com.typesafe.config.ConfigFactory

import javax.inject.Singleton

@Singleton
class ConfigManager
object ConfigManager extends BasicLogUtil with AutoClose {
  private val config = ConfigFactory.load()

  private def getOptionalConfigValue[T](path: String, default: T): T = if(config.hasPath(path)) config.getAnyRef(path).asInstanceOf[T] else default

  val title: String = getOptionalConfigValue("title", "PowlyPollington by Dinnk")
  val welcomeMessage: String = getOptionalConfigValue("welcomeMessage", title)

  val includeStaticVoteCountOnCastVotePage: Boolean = getOptionalConfigValue("includeStaticVoteCountOnCastVotePage", false)
  val hideCreateAPoleOnFrontPage: Boolean = getOptionalConfigValue("hideCreateAPoleOnFrontPage", false)
  val resultsPageRefreshIntervalSeconds: Int = getOptionalConfigValue("resultsPageRefreshIntervalSeconds", 300)

  val dbType: DBType = getOptionalConfigValue("db.type", "file") match {
    case s if s.toLowerCase.contains("mem") => MemoryDBType()
    case _ => FileDBType()
  }
  val dbFilePath: String = dbType match {
    case MemoryDBType() => "mem"
    case FileDBType() => getOptionalConfigValue("db.filePath","/opt/powly")
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
  log.info(VersionInfo.finalVerStr)

}
