package tethys.jackson

import tethys.core.readers._
import org.scalatest.{FlatSpec, Matchers}

class JacksonTokenIteratorTest extends FlatSpec with Matchers {

  behavior of "JacksonTokenIterator"

  it should "properly iterate over json string" in {
    val json = """{"a":1,"b":["s",true,{"a":null},1.0,false]}"""
    val it = json.toTokenIterator
    it.currentToken().isObjectStart shouldBe true

    it.nextToken().isFieldName shouldBe true
    it.fieldName() should contain("a")
    it.nextToken().isNumberValue shouldBe true
    it.number() should contain(1)

    it.nextToken().isFieldName shouldBe true
    it.fieldName() should contain("b")
    it.nextToken().isArrayStart shouldBe true

    it.nextToken().isStringValue shouldBe true
    it.string() should contain("s")

    it.nextToken().isBooleanValue shouldBe true
    it.boolean() should contain(true)

    it.nextToken().isObjectStart shouldBe true
    it.nextToken().isFieldName shouldBe true
    it.fieldName() should contain("a")
    it.nextToken().isNullValue shouldBe true
    it.nextToken().isObjectEnd shouldBe true

    it.nextToken().isNumberValue shouldBe true
    it.number() should contain(1.0)

    it.nextToken().isBooleanValue shouldBe true
    it.boolean() should contain(false)

    it.nextToken().isArrayEnd shouldBe true
    it.nextToken().isObjectEnd shouldBe true

    it.nextToken().isEmpty shouldBe true
  }

  it should "correctly skip next expressions" in {
    val json = """{"a":1,"b":["s",true,{"a":null},1.0,false]}"""
    val it = json.toTokenIterator
    it.currentToken().isObjectStart shouldBe true

    it.nextToken().isFieldName shouldBe true
    it.fieldName() should contain("a")
    it.next().skipExpression()

    it.currentToken().isFieldName shouldBe true
    it.fieldName() should contain("b")
    it.next().skipExpression()
    it.currentToken().isObjectEnd shouldBe true

    it.nextToken().isEmpty shouldBe true
  }

  it should "correctly collect expressions" in {
    val json = """{"a":1,"b":["s",true,{"a":null},1.0,false]}"""
    val it = json.toTokenIterator
    it.currentToken().isObjectStart shouldBe true

    it.nextToken().isFieldName shouldBe true
    it.fieldName() should contain("a")

    val a = it.next().collectExpression()
    a.currentToken().isNumberValue shouldBe true
    a.number() should contain(1)
    a.nextToken().isEmpty shouldBe true

    it.currentToken().isFieldName shouldBe true
    it.fieldName() should contain("b")

    val b = it.next().collectExpression()
    b.currentToken().isArrayStart shouldBe true

    b.nextToken().isStringValue shouldBe true
    b.string() should contain("s")

    b.nextToken().isBooleanValue shouldBe true
    b.boolean() should contain(true)

    b.nextToken().isObjectStart shouldBe true
    b.nextToken().isFieldName shouldBe true
    b.fieldName() should contain("a")
    b.nextToken().isNullValue shouldBe true
    b.nextToken().isObjectEnd shouldBe true

    b.nextToken().isNumberValue shouldBe true
    b.number() should contain(1.0)

    b.nextToken().isBooleanValue shouldBe true
    b.boolean() should contain(false)

    b.nextToken().isArrayEnd shouldBe true
    b.nextToken().isEmpty shouldBe true


    it.currentToken().isObjectEnd shouldBe true

    it.nextToken().isEmpty shouldBe true

  }
}