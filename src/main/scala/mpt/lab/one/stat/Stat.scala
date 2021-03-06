package mpt.lab.one.stat

/**
 * @author phpusr
 *         Date: 16.04.14
 *         Time: 19:00
 */

/**
 * Подсчет статистики получения элементов
 */
// TODO add tests
class Stat {
  /** Кол-во элементов */
  var elementsCount = 0
  /** Число выполненных операций для текущего элемента  */
  var currentElementCounter = 0
  /** Число выполненных операций для всех элементов  */
  var allElementsCounter = 0

  /**
   * Считать новый элемент
   *
   * Сбросить подсчет текущего
   * Увеличить кол-во элементов
   */
  def newElement() {
    currentElementCounter = 0
    elementsCount += 1
  }

  /** Увеличить счетчик для текущего элемента */
  def inc() {
    currentElementCounter += 1
    allElementsCounter += 1
  }

  /** Среднее число операций для элемента */
  def avg() = allElementsCounter.toFloat / elementsCount

  /** Сброс статистики */
  def reset() {
    elementsCount = 0
    currentElementCounter = 0
    allElementsCounter = 0
  }
}

