package mpt.lab.two.form.main

import scala.swing._
import scala.swing.TabbedPane.Page
import scala.swing.event.ButtonClicked
import scala.io.Source
import mpt.lab.two.automat.LexAuto
import mpt.lab.two.lexem.LexElem
import scala.collection.mutable.ListBuffer
import java.awt.Font
import scala.swing.Font
import java.io.File
import javax.swing.JTabbedPane

/**
 * @author phpusr
 *         Date: 01.05.14
 *         Time: 15:48
 */

/**
 * Главная форма
 */
object LabTwoForm extends SimpleSwingApplication {

  /** Путь к файлу с данными */
  private val filePathTextField = new TextField {
    editable = false
    preferredSize = defaultSize()
  }
  /** Кнопка выбора файла */
  private val browseFileButton = new Button("Browse") {
    preferredSize = defaultSize()
  }
  /** Кнопка обработки программы */
  private val processingButton = new Button("Processing") {
    preferredSize = defaultSize()
  }
  /** Текстовое поле просмотра содержимого файла */
  private val fileContentTextArea = new TextArea(20, 60) {
    border = Swing.EtchedBorder
    lineWrap = true
    font = new Font("Arial", Font.PLAIN, 12)
  }

  /** Таблица лексем */
  private val lexemTable = new Table {
    model = new LexTableModel
    peer.getColumnModel.getColumn(0).setPreferredWidth(10)
    peer.getColumnModel.getColumn(2).setPreferredWidth(300)
  }
  private val lexemModel = lexemTable.model.asInstanceOf[LexTableModel]

  /** Лейбл статуса разбора */
  private val parsingStatusLabel = new Label
  /** Кнопка выхода */
  private val exitButton = new Button("Exit") {
    preferredSize = new Dimension(200, preferredSize.height)
  }

  /** Компонент с вкладками */
  private var tabbedPane: JTabbedPane = null

  //////////////////////////////////////////////////////////////

  // !!! Тестирование
  private val test = new Thread(new Runnable {
    override def run() {
      fileContentTextArea.text = Source.fromFile("data/TestLex.txt").mkString
      Thread.sleep(1000)
      processing()
    }
  })
  if (true) test.start()

  //////////////////////////////////////////////////////////////

  // Генерация компонентов по умолчанию
  private def defaultScrollPane = (c: Component) => new ScrollPane() {
    viewportView = c
    verticalScrollBarPolicy = ScrollPane.BarPolicy.AsNeeded
  }
  private def defaultSize = () => new Dimension(130, 25)

  def top = new MainFrame {
    contents = new BorderPanel {
      // Центральная панель
      layout(new TabbedPane {
        tabbedPane = peer

        // Источник (Вкладка 1)
        val NameTabSource = "Source"
        pages += new Page(NameTabSource, new GridBagPanel {
          border = Swing.TitledBorder(Swing.EtchedBorder, "Source data")

          val c = new Constraints
          c.insets = new Insets(5, 5, 5, 5)

          c.weightx = 1
          c.fill = GridBagPanel.Fill.Horizontal
          layout(filePathTextField) = c

          c.weightx = 0
          layout(browseFileButton) = c

          c.gridx = 1
          c.gridy = 1
          layout(processingButton) = c

          c.gridx = 0
          c.gridy = 2
          c.gridwidth = 2
          c.weighty = 1
          c.fill = GridBagPanel.Fill.Both
          layout(defaultScrollPane(fileContentTextArea)) = c
        }, NameTabSource)

        // Таблица лексем (Вкладка 2)
        private val NameTabLexem = "Lexem table"
        pages += new Page(NameTabLexem, new GridBagPanel {
          val c = new Constraints
          c.weightx = 1
          c.weighty = 1
          c.fill = GridBagPanel.Fill.Both
          layout(new ScrollPane(lexemTable)) = c
        }, NameTabLexem)

      }) = BorderPanel.Position.Center

      // Нижняя панель
      layout(new GridBagPanel {
        val c = new Constraints
        c.insets = new Insets(5, 5, 5, 5)

        c.weightx = 1
        layout(parsingStatusLabel) = c

        c.anchor = GridBagPanel.Anchor.East
        layout(exitButton) = c
      }) = BorderPanel.Position.South
    }

    centerOnScreen()
  }

  // Обработчики событий формы
  listenTo(browseFileButton, processingButton)
  listenTo(exitButton)

  /** Диалог выбора файла */
  private val fileChooser = new FileChooser(new File("data"))

  reactions += {
    // Выбор файла
    case ButtonClicked(`browseFileButton`) =>
      val result = fileChooser.showDialog(null, "Select file")
      if (result == FileChooser.Result.Approve) {
        fileContentTextArea.text = Source.fromFile(fileChooser.selectedFile).mkString
        filePathTextField.text = fileChooser.selectedFile.getAbsolutePath
      }

    // Обработка текста программы
    case ButtonClicked(`processingButton`) => processing()

    // Выход из программы
    case ButtonClicked(`exitButton`) => System.exit(0)
  }

  /** Обработка текста программы */
  private def processing() {
    // Очистка таблицы
    lexemModel.clear()

    // Разбор текста
    val auto = new LexAuto
    val lines = fileContentTextArea.text.split("\n")
    val out = ListBuffer[LexElem]()
    val statusOrPos = auto.makeLexList(lines, out)

    // Установка статуса разбора текста
    parsingStatusLabel.text = if (statusOrPos == LexAuto.NoErrors) {
      "Разбор выполнен без ошибок"
    } else "В ходе разбора обнаружена ошибка"

    // Добавление лексем в таблицу
    out.zipWithIndex.map {
      case (e, index) => Seq(s"${index+1}", e.lexInfo.name, e.value)
    }.foreach(lexemModel.addRow)

    // Переключение на вкладку с таблицей
    tabbedPane.setSelectedIndex(1)
  }

}
