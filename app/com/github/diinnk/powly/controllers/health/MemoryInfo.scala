package com.github.diinnk.powly.controllers.health

case class MemoryInfo(usedMB: Long,
                      usedPercentage: Int,
                      freeMB: Long,
                      totalMB: Long,
                      maxMB: Long
                     )
