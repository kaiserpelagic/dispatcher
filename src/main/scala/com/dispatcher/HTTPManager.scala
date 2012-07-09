package com.dispatcher

import dispatch._

object HTTPManager {

  def getHTTPExector = http
  
  val http = new Http with thread.Safety {
    override def maxConnections = 100
  }

  def executeWithHttp[T](h: Http, hand: Handler[T]): T = {
    // TODO: handle various codes, i.e. 404, 500's etc.
    h x (hand) {
      case(_, _, _, out) =>  out()
    }
  } 
}
