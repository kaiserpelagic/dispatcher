package com.dispatcher

import dispatch._
import dispatch.Request._
import json._
import JsHttp._
import scala.xml._

trait Dispatcher extends HTTPConstants {
  val host: String
  def svc = :/(host)

  val http = HTTPManager.getHTTPExector
 
  def withHTTP[T](hand: Handler[T]): T = HTTPManager.executeWithHttp(http, hand)

  def getJS[T](path: List[String])(respFunc: JsValue => T): T = {
    getJs(path, header)(respFunc)
  }

  def getJS[T](path: List[String], params: Map[String, String])(respFunc: JsValue => T): T = {
    withHTTP(svc / toUrl(path) <<? params <:< Map(ACCEPT -> JSON) ># { respFunc })
  }
  
  def getXML[T](path: List[String])(respFunc: NodeSeq => T): T = {
    getXML(path)(respFunc)
  }

  def getXML[T](path: List[String], params: Map[String, String])(respFunc: NodeSeq => T): T  = {
    withHTTP(svc / toUrl(path) <<? params <:< Map(ACCEPT -> XML) <> { respFunc })
  }

  def put[T](path: List[String], body: NodeSeq)(respFunc: NodeSeq => T): T = { 
    withHTTP(svc / toUrl(path) <:<  Map(CONTENT_TYPE -> XML) <<< body.toString <> respFunc)
  }

  def post[T](path: List[String], body: NodeSeq)(respFunc: NodeSeq => T):T = {
    withHTTP(svc / toUrl(path) <:< Map(CONTENT_TYPE -> XML) << body.toString <> respFunc)
  }

  def delete[T](path: List[String])(respFunc: NodeSeq => T): T = {
    withHTTP(svc.DELETE / toUrl(path) <:< Map(CONTENT_TYPE -> XML) <> respFunc)
  }

  def toUrl(path: List[String]): String = path.foldLeft("")((sofar, p) => sofar + p + "/").dropRight(1)
  
  def header = Map.empty[String, String]
}
