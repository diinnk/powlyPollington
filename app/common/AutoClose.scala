package common

trait AutoClose {
  def autoClose[B](func: B)(implicit closable: AutoCloseable): B = try func finally closable.close()
}