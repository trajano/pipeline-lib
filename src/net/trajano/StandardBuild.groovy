package net.trajano

import java.io.StringReader
import java.util.Properties
/**
 * Provides standard build targets for Trajano organizational builds.
 */
class StandardBuild implements Serializable {
    def steps
    Map<String,String> stages
    StandardBuild(steps) {
        this.steps = steps
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
                steps.sh("env")
            }
            steps.stage(stages['build']) {
                steps.sh("env")
            }
        }
    }
}
