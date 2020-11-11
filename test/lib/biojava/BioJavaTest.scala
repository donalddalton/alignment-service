package lib.biojava

import org.scalatestplus.play.PlaySpec

class BioJavaTest extends PlaySpec {
  "pairwiseAignment" must {

    val target = "ATGCATGCATGCATGC"

    "be defined for query that matches a target sequence" in {
      val query = target.substring(5, 10)
      BioJava.pairwiseAlignment(query, target, "foo") must not be None
    }

    "not be defined for a query that doesn't match a target sequence" in {
      val query = "bar"
      BioJava.pairwiseAlignment(query, target, "foo") mustBe None
    }
  }
}
