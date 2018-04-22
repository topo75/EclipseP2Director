package com.github.topo75.eclipsep2director

import org.gradle.api.Project
import org.gradle.util.Configurable
import org.gradle.util.ConfigureUtil

class DirectorExtension implements Configurable<DirectorExtension> {
    private final Project project

    DirectorExtension(Project project) {
        this.project = project

    }

    @Override
    DirectorExtension configure(Closure cl) {
        DirectorAction action = ConfigureUtil.configure(cl, new DirectorAction(project))

        action.execute()
        return null
    }
}
