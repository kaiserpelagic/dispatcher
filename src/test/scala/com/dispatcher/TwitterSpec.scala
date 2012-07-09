import org.specs._

import com.dispatcher._

object Twitter extends Specification {

  "Twitter Search" should {
    "should find search for scala" in {
      val res = TwitterSearch.search("scala")
      res.isEmpty must beFalse
      false must beFalse
    }
  }
}
