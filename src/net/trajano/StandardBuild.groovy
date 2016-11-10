package net.trajano

import java.io.StringReader
import java.util.Properties
/**
 * Provides standard build targets for Trajano organizational builds.
 */
class StandardBuild implements Serializable {
    def steps
    def scm
    Map<String,String> stages
    StandardBuild(steps, scm) {
        this.steps = steps
        this.scm = scm
        stages = new Properties()
        stages.load(new StringReader(steps.libraryResource("Stages.properties")))
    }

    /**
     * This does nothing, it is primarily used to test if the build
     * environment is working.
     */
    def noop() {
        steps.node {
            steps.stage(stages['checkout']) {
                steps.checkout scm
            }
            steps.stage(stages['build']) {
                steps.sh("env")
            }
        }
    }
}
