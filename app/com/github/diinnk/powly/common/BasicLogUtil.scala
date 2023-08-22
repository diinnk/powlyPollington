package com.github.diinnk.powly.common

trait BasicLogUtil {
  implicit protected val log: BasicLogger = BasicLogger(this.getClass)
}
