import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.roots.*
import com.intellij.util.lang.UrlClassLoader
import org.jetbrains.org.objectweb.asm.ClassReader
import org.jetbrains.org.objectweb.asm.util.TraceClassVisitor
import java.io.File
import java.io.PrintWriter
import java.net.URL

class ClassRuntime : AnAction() {

    override fun actionPerformed(e: AnActionEvent?) {
        val project = e?.project ?: return
        val module = ModuleManager.getInstance(project).modules.first() ?: return
        val instance = CompilerProjectExtension.getInstance(project)
        val compilerOutputUrl = instance?.compilerOutputUrl ?: return
        println("output url = $compilerOutputUrl")
        val classesRoots = ProjectRootManager.getInstance(project).orderEntries().classesRoots
        val urlsString = classesRoots.map { "file://${File(it.path).path.removeSuffix("!")}" }
        val urls = urlsString.map {
            when {
                it.endsWith(".jar") -> URL(it)
                else -> URL("$it/")
            }
        }
        val loader = UrlClassLoader.build().urls(urls).get()
        invokeGlobalMethod(loader)

        loader.getResourceAsStream("Global.class").use {
            val bytes = it?.readBytes() ?: return
            val classReader = ClassReader(bytes)
            val writer = PrintWriter(System.out)
            val visitor = TraceClassVisitor(writer)
            classReader.accept(visitor, 0)
        }

        val clazz = loader.loadClass("com.sample.runtime.services.MyService")
        val i = clazz.newInstance()
        val method = clazz.getMethod("getSamples", String::class.java, String::class.java, String::class.java, String::class.java, String::class.java)
        val result = method.invoke(i, "one", "two", "three", "four", "five")
        println("R : $result")
    }

    private fun invokeGlobalMethod(loader: UrlClassLoader) {
        val clazz = loader.loadClass("Global")
        val i = clazz.newInstance()
        val result = clazz.getMethod("obtainMethod").invoke(i)
        println(result)

        val method = clazz.getMethod("obtainMethods", String::class.java)
        val results = method.invoke(i, "String")
        println(results)

        println("R: DONE")
    }
}
