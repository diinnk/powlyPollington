package com.github.diinnk.powly.common

trait DBType

case class FileDBType() extends DBType
case class MemoryDBType() extends DBType
