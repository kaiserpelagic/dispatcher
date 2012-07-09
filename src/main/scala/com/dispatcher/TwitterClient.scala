package com.dispatcher

import dispatch._
import json._
import JsHttp._

object TwitterSearch extends Dispatcher {
  val host = "search.twitter.com"

  def search(q: String) = getJS(List("search.json"), Map("q" -> q)) { ('results ! (list ! obj)) }
}

