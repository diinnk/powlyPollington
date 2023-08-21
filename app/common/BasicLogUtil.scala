package common

trait BasicLogUtil {
  implicit protected val log: BasicLogger = BasicLogger(this.getClass)
}
