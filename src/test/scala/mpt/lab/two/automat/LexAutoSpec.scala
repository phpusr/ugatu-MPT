package mpt.lab.two.automat

import org.scalatest.FlatSpec
import mpt.lab.two.lexem.{LexType, LexElem}
import scala.collection.mutable.ListBuffer

/**
 * @author phpusr
 *         Date: 30.04.14
 *         Time: 15:34
 */

/**
 * Тестирование LexAuto
 */
class LexAutoSpec extends FlatSpec {


  //------------------ State D ------------------//

  /** Проверка обработки числовых констант */
  it should "processing digit" in {
    println("\n>> State D")

    val out = ListBuffer[LexElem]()
    val auto = new LexAuto

    // Добавление цифр к числу
    auto.makeLexList(Array("0123456789"), out)
    assert(auto.prevState == AutoPos.D)

    // Начало комментария
    auto.makeLexList(Array("1{"), out)
    assert(auto.currentState == AutoPos.C)
    assert(out(1).lexInfo.name == LexType.Const)
    assert(out(1).lexInfo.info == "1")

    // Начало присваивания
    auto.makeLexList(Array("2:="), out)
    assert(auto.prevState == AutoPos.F)
    assert(auto.currentState == AutoPos.F)
    assert(out(2).lexInfo.name == LexType.Const)
    assert(out(2).lexInfo.info == "2")

    assert(out.size == 4)

    // Незначащие символы
    var eIndex = 4
    "(,).;".foreach { e =>
      auto.makeLexList(Array("3" + e.toString), out)
      assert(auto.currentState == AutoPos.F)
      assert(out(eIndex).lexInfo.name == LexType.Const)
      assert(out(eIndex).lexInfo.info == "3")
      assert(out(eIndex+1).lexInfo.name == LexType.MeaninglessSymbol)
      assert(out(eIndex+1).lexInfo.info == e.toString)

      eIndex += 2
    }

    // Символы-разделители
    " \t".foreach { e =>
      auto.makeLexList(Array("4" + e.toString), out)
      assert(out(eIndex).lexInfo.name == LexType.Const)
      assert(out(eIndex).lexInfo.info == "4")

      eIndex += 1
    }

    // Не поддерживаемые символы
    val outSize = out.size
    "!№%?*".foreach { e =>
      intercept[MatchError] {
        auto.makeLexList(Array("5" + e.toString), out)
      }
    }

    assert(out.size == outSize)
  }

}
