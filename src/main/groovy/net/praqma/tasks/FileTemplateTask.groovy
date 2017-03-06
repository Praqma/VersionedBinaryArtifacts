package net.praqma.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class FileTemplateTask extends DefaultTask {

    @Input
    String template

    @Optional
    @Input
    String targetDir

    @Input
    Map getAllExpansions() {
        // May add expansion configuration to the task
        project.buildproperties.expansions
    }

    @OutputFile
    File getDestinationFile() {
        if (targetDir) {
            new File(targetDir, templateFileName)
        } else {
            new File(project.buildDir, "templatingOut/${template}")
        }
    }

    /**
     *
     * @return part after last '/' in template
     */
    String getTemplateFileName() {
        new File(template).name
    }

    @TaskAction
    void createFile() {
        logger.info "Writing to ${destinationFile}"
        File destDir = destinationFile.parentFile
        String fileName = destinationFile.name
        project.copy {
            into destDir
            from(extractedTemplateFile) {
                rename { fileName }
                expand allExpansions
            }
        }
    }

    /**
     * Extract the template file from the classpath and write it to a file.
     * @return the extracted template file
     */
    File getExtractedTemplateFile() {
        File dest = new File(project.buildDir, "tmp/extractedTemplates/${template}")
        if (!dest.exists()) {
            dest.parentFile.mkdirs()
            InputStream stream = getClass().getResourceAsStream(template)
            if (stream == null) {
                throw new GradleException("Template '${template} not found")
            }
            dest.bytes = stream.bytes // Not a good way for large files
        }

        return dest
    }

}
