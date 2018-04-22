package com.github.topo75.eclipsep2director

import org.gradle.api.Plugin
import org.gradle.api.Project

class EclipseP2Plugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.extensions.create("director", DirectorExtension.class, project)
        //project.director.extensions.create("p2", DirectorAction.P2)
    }
}
