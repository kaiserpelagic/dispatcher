package com.dispatcher

object TwitterClient extends Dispatcher {
  val host = "api.twitter.com"
  val context = "1"
  val port = 8080

  def getStatus(id: String) = getJs(List("statuses", "show.json"), Map("id" -> id)) { js => js }
}
