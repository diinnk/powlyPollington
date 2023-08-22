package com.github.diinnk.powly.controllers.health

import com.github.diinnk.powly.VersionInfo
import com.github.diinnk.powly.common.GlobalWritesAndFormats
import com.github.diinnk.powly.db.PollDBHelper
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import java.lang.management.ManagementFactory
import java.sql.Connection
import javax.inject.Inject
import scala.util.Try

class HealthController @Inject()(val controllerComponents: ControllerComponents)
extends BaseController with PollDBHelper with GlobalWritesAndFormats {
  def index(): Action[AnyContent] = Action {
    val mb = 1024*1024
    val runtime = Runtime.getRuntime
    val used = (runtime.totalMemory - runtime.freeMemory) / mb
    val free = runtime.freeMemory / mb
    val total = runtime.totalMemory / mb
    val max = runtime.maxMemory / mb
    val usedP = ((used.toDouble / max.toDouble)*100).toInt
    val warning = usedP > 90
    val dbConnectable = Try {
      implicit val h2Conn: Connection = getH2Conn
      autoClose(execH2Cmd("""select 1"""))
    }.toOption.isDefined
    val healthInfo = HealthInfo(
      appName = "PowlyPollington",
      versionNumber = VersionInfo.verNumCombo,
      versionDesc = VersionInfo.rawVerText,
      uptime = getUptimeInfo,
      memory = MemoryInfo(usedMB = used, usedPercentage = usedP, freeMB = free, totalMB = total, maxMB = max),
      warning =
        if (!dbConnectable) Option("DB not connectable, returning BadRequest")
        else if (warning) Option("Above 90% memory usage")
        else None,
      dbConnectable = dbConnectable
    )
    val healthInfoJson = Json.toJson(healthInfo)

    if (!healthInfo.dbConnectable) BadRequest(healthInfoJson)
    else if (warning) new Status(208)(healthInfoJson)
    else Ok(healthInfoJson)
  }

  private def getUptimeInfo: UptimeInfo = {
    val uptimeMinutes: BigDecimal = ManagementFactory.getRuntimeMXBean.getUptime.toDouble/1000/60
    val uptimeDays: Int = (uptimeMinutes/1440).toInt
    val uptimeHours: Int = ((uptimeMinutes-(uptimeDays*1440))/60).toInt
    val uptimeFMins: BigDecimal = (uptimeMinutes-(uptimeDays*1440)-(uptimeHours*60)).
      setScale(2, BigDecimal.RoundingMode.HALF_UP)
    UptimeInfo(days = uptimeDays, hours = uptimeHours, minutes = uptimeFMins)
  }
}
