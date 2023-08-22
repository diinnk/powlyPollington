package com.github.diinnk.powly.common

import H2Helper.connString
import com.github.diinnk.powly.config.ConfigManager

import java.sql.{Connection, DriverManager, ResultSet, Statement}
import scala.util.{Failure, Success, Try}

trait H2Helper extends BasicLogUtil with AutoClose {
  // get H2 connection
  def getH2Conn: Connection = DriverManager.getConnection(connString)

  // get statement from a connection
  private def getStmt(implicit conn: Connection): Statement = conn.createStatement()

  // execute a sql statement against the H2 database
  protected def execH2Cmd(cmdStr: String, returnID: Boolean = false)(implicit conn: Connection): Int = Try{
    val stmt: Statement = getStmt
    if (returnID) {
      stmt.execute(s"$cmdStr", Statement.RETURN_GENERATED_KEYS)
      val generatedKeyRS = stmt.getGeneratedKeys
      generatedKeyRS.next()
      generatedKeyRS.getLong(1)
    } else {
      stmt.execute(s"$cmdStr")
      0
    }
  } match {
    case Success(s) => s.toInt
    case Failure(e) =>
      log.error(s"execH2Cmd failed for stmt: $cmdStr\n${e.getMessage}")
      e.printStackTrace()
      throw e
  }

  protected def getH2ResultSet(cmdStr: String)(implicit conn: Connection): ResultSet = Try {
    val stmt: Statement = getStmt
    stmt.executeQuery(s"$cmdStr")
  } match {
    case Success(s) => s
    case Failure(e) =>
      log.error(s"getH2ResultSet failed for stmt: $cmdStr\n${e.getMessage}")
      throw e
  }
}

object H2Helper {
  // get H2 connection string, either of in-memory or file type based off of application.conf definition
  val connString: String = ConfigManager.dbType match {
    case MemoryDBType() => "jdbc:h2:mem:PowlyPollington;DB_CLOSE_DELAY=-1"
    case FileDBType() =>
      val resolvedPath = (ConfigManager.dbFilePath + "/").replace("//", "/") + "PowlyPollington"
      s"jdbc:h2:file:$resolvedPath;USER=${ConfigManager.dbFileUser};PASSWORD=${ConfigManager.dbFilePassword}"
  }
}
