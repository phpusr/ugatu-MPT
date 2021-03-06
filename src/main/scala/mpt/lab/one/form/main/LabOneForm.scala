package mpt.lab.one.form.main

import scala.swing._
import scala.swing.event.ButtonClicked
import scala.util.Random
import mpt.lab.one.idtable.binarytree.BinaryTree
import mpt.lab.one.idtable.rehash.RehashTable

/**
 * @author phpusr
 *         Date: 17.04.14
 *         Time: 11:56
 */

/**
 * Главная форма
 */
object LabOneForm extends SimpleSwingApplication {

  /** Кол-во сгенерированных ид-ов */
  private val IdsCount = 10
  /** Кол-во символов в ид-е */
  private val IdSymbolsCount = 32

  /** Кнопка генерации идентификаторов */
  private val generateIdsButton = new Button("Generate")
  /** Текстовая область ввода ид-ов */
  private val idsTextArea = new TextArea {
    preferredSize = new Dimension(300, 0)
    lineWrap = true
  }
  /** Кнопка очистки области ввода ид-ов */
  private val clearIdsButton = new Button("Clear") {
    preferredSize = new Dimension(130, preferredSize.height)
  }
  
  /** Поле ввода для поиска */
  private val searchTextField = new TextField() {
    preferredSize = new Dimension(200, preferredSize.height)
  }
  /** Кнопка поиска ид-а */
  private val searchButton = new Button("Find")
  /** Кнопка сброса статистики поиска */
  private val resetButton = new Button("Reset")
  /** Кнопка найти все */
  private val findAllButton = new Button("Find all")
  /** Лейбл всего найдено */
  private val allFoundLabel = defaultValueLabel()

  /** Кнопка выхода */
  private val exitButton = new Button("Exit") {
    preferredSize = new Dimension(150, preferredSize.height)
  }

  // Компоненты статистики
  private val (rehashStatPanel, rEqualsLabel, rAllEqualsLabel, rAvgEqualsLabel, rFoundLabel) = defaultStatComponents("Rehash")
  private val (binaryWoodStatPanel, bEqualsLabel, bAllEqualsLabel, bAvgEqualsLabel, bFoundLabel) = defaultStatComponents("Binary")


  //--------------------------------

  /** Заголовок для панели (по умолчанию) */
  private def defaultTitleBorder = (title: String) => Swing.TitledBorder(Swing.EtchedBorder, title)
  /** Панель со значением (по умолчанию) */
  private def defaultValuePanel = (title: String, valueLabel: Label) => new FlowPanel {
    contents += new Label(title)
    contents += valueLabel
  }
  /** Лейбл для значения (по умолчанию) */
  private def defaultValueLabel = () => new Label("0")
  /** Панель статистики (по умолчанию) */
  private def defaultStatComponents = (title: String) => {
    val (equalsLabel, allEqualsLabel, avgEqualsLabel) = (defaultValueLabel(), defaultValueLabel(), defaultValueLabel())
    val foundLabel = new Label("Id found")
    val panel = new GridBagPanel {
      border = defaultTitleBorder(title)
      val c = new Constraints
      c.weightx = 1
      c.anchor = GridBagPanel.Anchor.Center
      layout(foundLabel) = c
      c.anchor = GridBagPanel.Anchor.West
      c.gridy = 1
      layout(defaultValuePanel("Equals:", equalsLabel)) = c
      c.gridy = 2
      layout(defaultValuePanel("All equals:", allEqualsLabel)) = c
      c.gridy = 3
      layout(defaultValuePanel("Avg equals:", avgEqualsLabel)) = c
    }

    (panel, equalsLabel, allEqualsLabel, avgEqualsLabel, foundLabel)
  }

  // Форма MainForm
  def top: Frame = new MainFrame {
    contents = new GridBagPanel {
      import GridBagPanel.Fill._

      // Левая часть
      val c = new Constraints
      c.weightx = 0.5
      c.weighty = 1
      c.fill = Both
      layout(new GridBagPanel {
        border = defaultTitleBorder("Input data")
        val c = new Constraints
        c.weightx = 1
        c.weighty = 1
        c.fill = Both
        layout(idsTextArea) = c
        c.gridy = 1
        c.weighty = 0
        c.fill = None
        layout(clearIdsButton) = c
      }) = c

      // Правая часть
      layout(new GridBagPanel {
        // Панель исходных данных
        val c = new Constraints
        c.weightx = 1
        c.fill = Both
        layout(new FlowPanel {
          border = defaultTitleBorder("Source")
          contents += generateIdsButton
        }) = c

        // Панель поиска идентификатора
        c.gridy = 1
        layout(new GridBagPanel {
          // Поле ввода элемента для поиска
          border = defaultTitleBorder("Find ids")
          val c = new Constraints
          c.fill = Horizontal
          c.weightx = 1
          layout(searchTextField) = c

          // Кнопка поиска
          c.fill = None
          c.weightx = 0
          layout(searchButton) = c

          // Панель с выводом кол-ва и кнопкой сброса
          c.gridy = 1
          c.anchor = GridBagPanel.Anchor.West
          layout(new FlowPanel {
            contents += new Label("All found:")
            contents += allFoundLabel
          }) = c
          layout(resetButton) = c

          // Кнопка найти все
          c.gridy = 2
          layout(findAllButton) = c

          // Панель с выводом статистики
          c.gridy = 3
          c.gridwidth = 2
          c.fill = Both
          layout(new GridBagPanel {
            // Левая панель
            val c = new Constraints
            c.weightx = 0.5
            c.fill = Both
            layout(rehashStatPanel) = c

            // Правая панель
            layout(binaryWoodStatPanel) = c
          }) = c

        }) = c // end search panel


        // Кнопка выхода
        c.gridy = 2
        c.fill = None
        c.anchor = GridBagPanel.Anchor.Center
        layout(exitButton) = c

      }) = c // end right part
    } // end main panel

    //size = new Dimension(600, 300)
    centerOnScreen()
  }

  // Таблицы ид-ов
  val MaxTableSize = 500
  val binaryTree = new BinaryTree(MaxTableSize)
  val rehashTable = new RehashTable(MaxTableSize)

  // Обработчики событий формы
  listenTo(generateIdsButton, clearIdsButton)
  listenTo(searchButton, resetButton, findAllButton)
  listenTo(exitButton)

  reactions += {
    // Генерация ид-ов
    case ButtonClicked(`generateIdsButton`) =>
      val ids = generateIds()
      idsTextArea.append(ids.mkString("\n"))

      // Загрузка id-ов в таблицу с рехэшированием
      rehashTable.clear()
      ids.foreach(rehashTable.add)

      // Загрузка id-ов в бинарное дерево
      binaryTree.clear()
      ids.foreach(binaryTree.add)

    // Поиск элемента
    case ButtonClicked(`searchButton`) =>
      val rNode = rehashTable.find(searchTextField.text)
      rFoundLabel.text = if (rNode.isDefined) "Id found" else "Id not found"

      val bNode = binaryTree.find(searchTextField.text)
      bFoundLabel.text = if (bNode.isDefined) "Id found" else "Id not found"

      updateStat()

    //TODO
    case ButtonClicked(`findAllButton`) => ???
      //TODO implement

    // Сброс статистики поиска
    case ButtonClicked(`resetButton`) => resetSearchStat()

    // Очистка поле ввода ид-во
    case ButtonClicked(`clearIdsButton`) => idsTextArea.text = ""

    // Выход из программы
    case ButtonClicked(`exitButton`) => System.exit(0)
  }

  // Статистика поиска
  val rFindStat = rehashTable.getStat.get("find").get
  val bFindStat = binaryTree.getStat.get("find").get

  /** Сброс статистики поиска */
  private def resetSearchStat() {
    searchTextField.text = ""
    rFindStat.reset()
    bFindStat.reset()
    updateStat()
  }

  /** Обновить статистику на форме */
  private def updateStat() {
    val format = "%.3f"

    // Статистика для таблици рехэширвония
    rEqualsLabel.text = rFindStat.currentElementCounter.toString
    rAllEqualsLabel.text = rFindStat.allElementsCounter.toString
    rAvgEqualsLabel.text = rFindStat.avg().formatted(format)

    // Статистика для бинарного дерева
    bEqualsLabel.text = bFindStat.currentElementCounter.toString
    bAllEqualsLabel.text = bFindStat.allElementsCounter.toString
    bAvgEqualsLabel.text = bFindStat.avg().formatted(format)
  }

  /** Генерация строки с ид-ми */
  private def generateIds = () => {
    for (i <- 1 to IdsCount) yield generateRandomString(IdSymbolsCount)
  }

  /** Генерация рандомной строки из латинских больших и малых букв */
  private def generateRandomString = (size: Int) => {
    // Кодировка символов
    val CharsetType = "utf8"
    // Начальный символ, сооветсвует: A
    val StartSymbol = 65
    // Конечный символ, сооветсвует: z
    val EndSymbol = 122

    val bytes = for (i <- 1 to size) yield (Random.nextInt(EndSymbol - StartSymbol + 1) + StartSymbol).toByte
    new String(bytes.toArray, CharsetType)
  }

}
