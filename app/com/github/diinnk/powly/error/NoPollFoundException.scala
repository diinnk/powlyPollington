package com.github.diinnk.powly.error

case class NoPollFoundException(private val message: String = "",
                                private val cause: Throwable = None.orNull
                               ) extends Exception(message, cause)
