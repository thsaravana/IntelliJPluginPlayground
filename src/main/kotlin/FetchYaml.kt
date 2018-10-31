import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import org.jetbrains.yaml.YAMLUtil
import org.jetbrains.yaml.psi.YAMLFile

class FetchYaml : AnAction() {

    override fun actionPerformed(e: AnActionEvent?) {
        val project = e?.project ?: return
        val srcDirs = ProjectRootManager.getInstance(project).contentSourceRoots
        srcDirs.forEach { srcDir ->
            ProjectFileIndex.SERVICE.getInstance(project).iterateContentUnderDirectory(srcDir) {
                if (it.name == "application.yml") {
                    println(it.path)
                    val psiFile = it.psiFile(project)
                    println(psiFile?.name)
                    if (psiFile is YAMLFile) {
                        val value = YAMLUtil.getValue(psiFile, "server", "port")
                        println("server.port = ${value?.second}")
                    }
                }
                true
            }
        }
    }
}


fun VirtualFile.psiFile(project: Project): PsiFile? {
    return PsiManager.getInstance(project).findFile(this)
}