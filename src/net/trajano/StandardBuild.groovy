package net.trajano

import java.io.StringReader
import java.util.Properties
/**
 * Provides standard build targets for Trajano organizational builds.
 */
class StandardBuild implements Serializable {
    def steps
    def stages
    StandardBuild(steps) {
        this.steps = steps
        stages = new Properties()
        stages.load(new StringReader(steps.libraryResource("Stages.properties")))
    }

    private def nstage(key) {
        return steps.stage(key)
    }
    /**
     * This does nothing, it is primarily used to test if the build
     * environment is working.
     */
    def noop() {
        steps.node {
            nstage("scm") {
                //steps.checkout(steps.scm)
            }
            nstage("build") {
                steps.sh("env")
            }
        }
    }
}
