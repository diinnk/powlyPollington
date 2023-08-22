package com.github.diinnk.powly.common

trait AutoClose {
  def autoClose[B](func: B)(implicit closable: AutoCloseable): B = try func finally closable.close()
}