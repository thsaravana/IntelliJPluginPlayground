import com.intellij.execution.RunManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemRunConfiguration
import com.intellij.openapi.project.Project

class EnvironmentVariables : AnAction() {

    override fun actionPerformed(e: AnActionEvent?) {
        val project = e?.project ?: return
        val key = "GATEWAY_CLIENT_ID"
        var value: String? = checkInCurrentConfiguration(project, key)
        if (value == null) {
            value = checkInAllConfigurations(project, key)
            if (value == null) {
                value = System.getenv(key)
            }
        }
        println("$key = $value")
    }

    private fun checkInAllConfigurations(project: Project, key: String): String? {
        RunManager.getInstance(project).allConfigurationsList.forEach {
            if (it is ExternalSystemRunConfiguration) {
                val value = it.settings.env[key]
                if (value != null) return value
            }
        }
        return null
    }

    private fun checkInCurrentConfiguration(project: Project, key: String): String? {
        RunManager.getInstance(project).selectedConfiguration?.let { config ->
            val configuration = config.configuration
            if (configuration is ExternalSystemRunConfiguration) {
                return configuration.settings.env[key]
            }
        }
        return null
    }
}