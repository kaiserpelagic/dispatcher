package com.dispatcher

import dispatch._
import dispatch.Request._
import json._
import JsHttp._
import scala.xml._

trait Dispatcher extends HTTPConstants {
  val host: String 
  val port: Int
  val context: String
  def svc = :/(host, port) / context
 
  val http = HTTPManager.getHTTPExector
 
  def withHttp[T](hand: Handler[T]): T = HTTPManager.executeWithHttp(http, hand)

  def getJs[T](path: List[String])(fromRespJs: JsValue => T): T = {
    getJs(path, header)(fromRespJs)
  }

  def getJs[T](path: List[String], params: Map[String, String])(fromRespJs: JsValue => T): T = {
    withHttp(svc / toUrl(path) <<? params <:< Map(ACCEPT -> JSON) ># { fromRespJs })
  }
  
  def getXml[T](path: List[String])(fromRespXml: NodeSeq => T): T = {
    getXml(path)(fromRespXml)
  }

  def getXml[T](path: List[String], params: Map[String, String])(fromRespXml: NodeSeq => T): T  = {
    withHttp(svc / toUrl(path) <<? params <:< Map(ACCEPT -> XML) <> { fromRespXml })
  }

  def put[T](path: List[String], body: NodeSeq)(fromRespXml:NodeSeq => T): T = { 
    withHttp(svc / toUrl(path) <:<  Map(CONTENT_TYPE -> XML) <<< body.toString <> fromRespXml)
  }

  def post[T](path: List[String], body: NodeSeq)(fromRespXml:NodeSeq => T):T = {
    withHttp(svc / toUrl(path) <:< Map(CONTENT_TYPE -> XML) << body.toString <> fromRespXml)
  }

  def delete[T](path: List[String])(fromRespXml: NodeSeq => T): T = {
    withHttp(svc.DELETE / toUrl(path) <:< Map(CONTENT_TYPE -> XML) <> fromRespXml)
  }

  def toUrl(path: List[String]): String = path.foldLeft("")((sofar, p) => sofar + p + "/")
  
  def header = Map.empty[String, String]
}
