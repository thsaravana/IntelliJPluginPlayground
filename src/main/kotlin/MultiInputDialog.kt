import com.intellij.execution.util.ListTableWithButtons
import com.intellij.execution.util.StringWithNewLinesCellEditor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.util.ui.ListTableModel
import java.awt.BorderLayout
import java.util.*
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.table.TableCellEditor

class MultiInputDialog : AnAction() {

    override fun actionPerformed(e: AnActionEvent?) {
        MyEnvironmentVariablesDialog(e?.project!!).show()
    }

    class MyEnvironmentVariablesDialog(project: Project) : DialogWrapper(project, true) {
        private val myEnvVariablesTable: EnvVariablesTable = EnvVariablesTable()
        private val myWholePanel = JPanel(BorderLayout())

        init {
            myEnvVariablesTable.setValues(listOf(EnvironmentVariable("what","1111"),
                    EnvironmentVariable("How", "2222"),
                    EnvironmentVariable("See that", "3333")))
            myWholePanel.add(myEnvVariablesTable.component, BorderLayout.CENTER)
            val useDefaultPanel = JPanel(BorderLayout())

            myWholePanel.add(useDefaultPanel, BorderLayout.SOUTH)
            title = "My Dialog"
            init()
        }

        override fun createCenterPanel(): JComponent? {
            return myWholePanel
        }

        override fun doOKAction() {
            myEnvVariablesTable.stopEditing()
            val envs = LinkedHashMap<String, String>()
            for (variable in myEnvVariablesTable.environmentVariables) {
                envs[variable.name] = variable.value
            }
            super.doOKAction()
        }
    }
}

class EnvVariablesTable : ListTableWithButtons<EnvironmentVariable>() {

    val environmentVariables: List<EnvironmentVariable>
        get() = elements

    init {
        tableView.emptyText.text = "No variables"
    }

    override fun createListModel(): ListTableModel<*> {
        val name: ElementsColumnInfoBase<EnvironmentVariable> = object : ListTableWithButtons.ElementsColumnInfoBase<EnvironmentVariable>("Name") {
            override fun valueOf(environmentVariable: EnvironmentVariable): String? {
                return environmentVariable.name
            }

            override fun isCellEditable(environmentVariable: EnvironmentVariable) = false

            override fun setValue(environmentVariable: EnvironmentVariable?, s: String) {
                if (s == valueOf(environmentVariable!!)) {
                    return
                }
                environmentVariable.name = s
                setModified()
            }

            override fun getDescription(environmentVariable: EnvironmentVariable): String? {
                return null
            }
        }

        val value: ElementsColumnInfoBase<EnvironmentVariable> = object : ListTableWithButtons.ElementsColumnInfoBase<EnvironmentVariable>("Value") {
            override fun valueOf(environmentVariable: EnvironmentVariable): String? {
                return environmentVariable.value
            }

            override fun isCellEditable(environmentVariable: EnvironmentVariable): Boolean {
                return true
            }

            override fun setValue(environmentVariable: EnvironmentVariable?, s: String) {
                if (s == valueOf(environmentVariable!!)) {
                    return
                }
                environmentVariable.value = s
                setModified()
            }

            override fun getDescription(environmentVariable: EnvironmentVariable): String? {
                return null
            }

            override fun getEditor(variable: EnvironmentVariable?): TableCellEditor? {
                val editor = StringWithNewLinesCellEditor()
                editor.clickCountToStart = 1
                return editor
            }
        }

        return ListTableModel<ElementsColumnInfoBase<EnvironmentVariable>>(name, value)
    }

    override fun createElement(): EnvironmentVariable {
        return EnvironmentVariable("", "")
    }

    override fun isEmpty(element: EnvironmentVariable): Boolean {
        return element.name.isEmpty() && element.value.isEmpty()
    }

    override fun cloneElement(envVariable: EnvironmentVariable): EnvironmentVariable {
        return envVariable.copy()
    }

    override fun canDeleteElement(selection: EnvironmentVariable): Boolean {
        return false
    }
}

data class EnvironmentVariable(var name: String, var value: String)