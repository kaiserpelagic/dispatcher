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
  val FORM = "application/x-www-form-urlencoded"

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
  
  def getJs[T](path: String)(fromRespJs: JsValue => T): T = {
    getJs(path, Map.empty[String, String])(fromRespJs)
  }

  def getJs[T](path: String, params: Map[String, String])(fromRespJs: JsValue => T): T = {
    execute(svc / path <<? params <:< (header + (ACCEPT -> JSON)) ># { fromRespJs })
  }

  def getXml[T](path: String, params: Map[String, String])(fromRespXml: NodeSeq => T): T  = {
    execute(svc / path <<? params <:< (header + (ACCEPT -> XML)) <> { fromRespXml })
  }

  def getXml[T](path: String)(fromRespXml: NodeSeq => T): T = {
    getXml(path, Map.empty[String, String])(fromRespXml)
  }

  def getXml[T](path: List[String], params: Map[String, String])(fromRespXml: NodeSeq => T): T  = {
    getXml(foldPath(path), params)(fromRespXml)
  }

  def getXml[T](path: List[String])(fromRespXml: NodeSeq => T): T = {
    getXml(foldPath(path))(fromRespXml)
  }

  def put[T](path: String, body: NodeSeq)(fromRespXml:NodeSeq => T):T = { 
    execute(svc / path <:< (header + (CONTENT_TYPE -> XML)) <<< body.toString <> fromRespXml)
  }

  def put[T](path: List[String], body: NodeSeq)(fromRespXml:NodeSeq => T): T = {
    put(foldPath(path), body)(fromRespXml)
  }

  def post[T](path: List[String], body: NodeSeq, params: Map[String, String])(fromRespXml:NodeSeq => T):T = {
    execute(svc / foldPath(path) <<? params <:< (header + (CONTENT_TYPE -> XML)) << body.toString <> fromRespXml)
  }

  def post[T](path: List[String], body: NodeSeq)(fromRespXml:NodeSeq => T):T = {
    post(path, body, Map.empty[String,String])(fromRespXml)  
  }

  def postForm[T](path: List[String], body: String)(fromRespXml:NodeSeq => T):T = {
    execute(svc / foldPath(path) <:< (header + (CONTENT_TYPE -> FORM)) << body <> fromRespXml)
  }

  def delete[T](path: List[String], params: Map[String, String])(fromRespXml: NodeSeq => T): T = {
    execute(svc.DELETE / foldPath(path) <<? params <:< (header + (CONTENT_TYPE -> XML)) <> fromRespXml)
  }

  def delete[T](path: List[String])(fromRespXml: NodeSeq => T): T = {
    delete(path, Map.empty[String, String])(fromRespXml)
  }

  def foldPath(path: List[String]): String = path.foldLeft("")((sofar, p) => sofar + p + "/")
  
  def header = Map.empty[String, String]
}
