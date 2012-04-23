package com.dispatcher

import dispatch._
import dispatch.Request._
import json._
import JsHttp._
import scala.xml._

trait Dispatcher {
  val host: String 
  val port: Int
  val context: String
  def svc = :/(host, port) / context
 
  val CONTENT_TYPE = "Content-type"
  val ACCEPT = "Accept"
  val JSON = "application/json"
  val XML = "application/xml"

  def http = new Http with thread.Safety 
  
  def execute[T](hand: Handler[T]): T = {
    val h = http
    try {
      h x (hand) {
        case(_, _, _, out) => out()
      }
    } finally {
      h.shutdown()
    }
  }
  
  def getJs[T](path: List[String])(fromRespJs: JsValue => T): T = {
    getJs(path, emptyHeader)(fromRespJs)
  }

  def getJs[T](path: List[String], params: Map[String, String])(fromRespJs: JsValue => T): T = {
    execute(svc / toUrl(path) <<? params <:< Map(ACCEPT -> JSON) ># { fromRespJs })
  }

  def getXml[T](path: List[String], params: Map[String, String])(fromRespXml: NodeSeq => T): T  = {
    execute(svc / toUrl(path) <<? params <:< Map(ACCEPT -> XML) <> { fromRespXml })
  }

  def getXml[T](path: List[String])(fromRespXml: NodeSeq => T): T = {
    getXml(path)(fromRespXml)
  }

  def put[T](path: List[String], body: NodeSeq)(fromRespXml:NodeSeq => T): T = { 
    execute(svc / toUrl(path) <:<  Map(CONTENT_TYPE -> XML) <<< body.toString <> fromRespXml)
  }

  def post[T](path: List[String], body: NodeSeq, params: Map[String, String])(fromRespXml:NodeSeq => T): T = {
    execute(svc / toUrl(path) <<? params <:< Map(CONTENT_TYPE -> XML) << body.toString <> fromRespXml)
  }

  def post[T](path: List[String], body: NodeSeq)(fromRespXml:NodeSeq => T):T = {
    post(path, body, emptyHeader)(fromRespXml)  
  }

  def delete[T](path: List[String], params: Map[String, String])(fromRespXml: NodeSeq => T): T = {
    execute(svc.DELETE / toUrl(path) <<? params <:< Map(CONTENT_TYPE -> XML) <> fromRespXml)
  }

  def delete[T](path: List[String])(fromRespXml: NodeSeq => T): T = {
    delete(path, emptyHeader)(fromRespXml)
  }

  def toUrl(path: List[String]): String = path.foldLeft("")((sofar, p) => sofar + p + "/")
  
  def emptyHeader = Map.empty[String, String]
}
