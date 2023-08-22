package com.github.diinnk.powly.controllers.health

case class HealthInfo(appName: String,
                      versionNumber: String,
                      versionDesc: String,
                      uptime: UptimeInfo,
                      memory: MemoryInfo,
                      warning: Option[String],
                      dbConnectable: Boolean
                     )
