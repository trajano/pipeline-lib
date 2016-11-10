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
     * Checks out code from SCM.
     */
    private def checkoutScm() {
        steps.stage(stages['checkout']) {
            steps.checkout scm
        }
    }
    /**
     * This does nothing, it is primarily used to test if the build
     * environment is working.
     */
    def noop() {
        steps.node {
            checkoutScm()
            steps.stage(stages['build']) {
            }
        }
    }
}
