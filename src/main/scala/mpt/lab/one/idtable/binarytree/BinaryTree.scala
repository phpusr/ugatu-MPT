package mpt.lab.one.idtable.binarytree

import mpt.lab.one.idtable.{NodeAbstract, IdTableAbstract}
import mpt.lab.one.idtable.IndexType._
import scala.collection.mutable.ListBuffer
import org.dyndns.phpusr.log.Logger

/**
 * @author phpusr
 *         Date: 16.04.14
 *         Time: 21:28
 */

/**
 * Элемент дерева
 */
class Node(name: String, hash: Index, var left: Option[Node], var right: Option[Node]) extends NodeAbstract(name, hash) {
  def this(name: String, hash: Index) = this(name, hash, None, None)

  override def toString = s"hash: $hash, name: $name, " +
    s"L: ${if (!left.isEmpty) left.get.name else "-"} " +
    s"R: ${if (!right.isEmpty) right.get.name else "-"} "

  /** Вывод поддерева начиная с текущего элемента */
  def toSubtreeString: String = s"hash: $hash, name: $name" +
    s"\nL: ${if (!left.isEmpty) left.get.toSubtreeString else "-"} " +
    s"\nR: ${if (!right.isEmpty) right.get.toSubtreeString else "-"} "
}

/**
 * Организация таблиц идентификаторов
 * Метод: Бинарное дерево
 */
class BinaryTree(MaxTableSize: Index) extends IdTableAbstract(MaxTableSize) {

  /** Корневой элемент дерева */
  private var root: Option[Node] = None

  /** Логирование */
  private val logger = Logger(infoEnable = false, debugEnable = false, traceEnable = false)

  ///////////////////////////////////////////

  init()

  ///////////////////////////////////////////

  /** Инициализация таблицы идентификаторов */
  override def init() = clear()

  /** Добавление элемента в таблицу */
  override def add(idName: String): Option[Node] = {
    // Инкремент счетчика кол-ва добавленных элементов
    addStat.newElement()

    // Добавление элемента в дерево
    val hash = getHash(idName)
    root = addRec(idName, hash, root)

    // Поиск добавленного элемента и его возврат
    find(idName)
  }

  /** Рекурсивное добавление элемента в дерево */
  private def addRec(idName: String, hash: Index, node: Option[Node]): Option[Node] = {
    // Инкремент счетчика кол-ва итераций добавления элемента
    addStat.inc()

    // Добавление элемента в дерево
    if (node.isEmpty) { // Если узел пустой
      new Some(new Node(idName, hash)) //Создаем новый узел, знач-е узла берем из idName
    } else if (node.get.name == idName) { // Если элемент уже есть, не добавляем его
      logger.debug(">> Already exists!")
      node
    } else if (hash > node.get.hash) { // Если hash > текущего узла
      node.get.right = addRec(idName, hash, node.get.right)
      node
    } else if (hash <= node.get.hash) { // Если hash <= текущего узла
      node.get.left = addRec(idName, hash, node.get.left)
      node
    } else {
      assert(assertion = false, ">> The condition is not provided") //TODO
      None
    }
  }

  /** Поиск элемента в таблице по имени */
  override def find(idName: String): Option[Node] = {
    // Инкремент счетчика кол-ва поисков
    findStat.newElement()
    // Рекурсивный поиск элемента
    findRec(idName, root)
  }

  /** Рекурсивный поиск элемента в таблице по имени */
  private def findRec(idName: String, node: Option[Node]): Option[Node] = {
    // Инкремент счетчика кол-ва итераций поиска элемента
    findStat.inc()

    // Поиск элемента
    if (node.isEmpty) {
      None // Если текущий узел пустой, то возвращаем None
    } else if (node.get.name == idName) node // Если элемент найден, то возвращаем его
    else {
      // Иначе запускаем поиск для левого и прового поддеревьев
      val left = findRec(idName, node.get.left)
      val right = findRec(idName, node.get.right)
      // Еслил рез-т поиска в левом поддереве не пустой, то возвращаем его, иначе рез-т правого
      if (!left.isEmpty) left else right
    }
  }

  /** Очистка таблицы */
  override def clear() {
    root = None
  }

  /** Возврат таблицы идентификаторов */
  override def getIdTable: Seq[String] = {
    println(">> root: " + root.get.toSubtreeString) //TODO
    val list = ListBuffer[String]()
    buildIdTableRec(root, list)
    
    list
  }

  /** Рекурсивный возврат таблицы идентификаторов */
  private def buildIdTableRec(node: Option[Node], list: ListBuffer[String]) {
    if (!node.isEmpty) {
      buildIdTableRec(node.get.left, list)
      list += node.get.name
      buildIdTableRec(node.get.right, list)
    }
  }

}
