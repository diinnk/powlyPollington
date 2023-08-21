package common

import play.api.libs.json.{JsArray, JsBoolean, JsNumber, JsString, JsValue}

import scala.util.{Failure, Success, Try}

class GetAttribute(json: JsValue) extends BasicLogUtil {
  //Method to get a required attribute from JsValue, forcing a throw in the event the required attribute is not found
  def getRequiredAttr[T](x: String): T = Try {getAttr[T](x)} match {
    case Success(s) => s
    case Failure(e: NoSuchElementException) => log.error(s"${this.getClass.getSimpleName}.getRequiredAttr: " +
      s"required attributed named '$x' is not provided in the incoming payload, throwing")
      throw e
    case Failure(e) =>
      log.error(s"${this.getClass.getSimpleName}.getRequiredAttr: Unknown Error getting required attribute: ${e.getMessage}")
      throw e
  }

  //Generic typed get attribute method
  def getAttr[T](x: String): T = (json \ x).get match {
    case s: JsString => s.as[String].asInstanceOf[T]
    case n: JsNumber => n.as[Int].asInstanceOf[T]
    case b: JsBoolean => b.as[Boolean].asInstanceOf[T]
    case a: JsArray => a.head.get match {
      case _: JsNumber => a.as[List[Int]].asInstanceOf[T]
      case _: JsString => a.as[List[String]].asInstanceOf[T]
      case _ => List.empty.asInstanceOf[T]
    }
    case j: JsValue => j.asInstanceOf[T]
    case o => o.as[String].asInstanceOf[T]
  }

  //Generic types get attribute with provided default in the event no attribute is found
  def getAttr[T](x: String, default: T): T = getAttrOption(x).getOrElse(default)

  //Generic types get attribute resolving to an option
  def getAttrOption[T](x: String): Option[T] = Try(getAttr[T](x)).toOption

  //Get list of string from JsValue
  def getAttrList[T](x: String): List[T] = getAttr[List[T]](x, List.empty)

}

