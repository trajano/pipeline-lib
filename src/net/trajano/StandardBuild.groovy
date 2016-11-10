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
     * Implements a Maven build.
     */
    def maven() {
        step.node {
            // Environment setup
            def mvnHome = tool name:'maven-3.3.9', type: 'maven'
            env.PATH = "${mvnHome}/bin:${env.PATH}"

            checkoutScm()
            if (env.BRANCH_NAME == "master") {
                steps.stage(stages['build']) {
                    mvn 'deploy site'
                }
                steps.stage(stages['codeQualityAnalysis']) {
                    withSonarQubeEnv {
                        mvn 'org.sonarsource.scanner.maven:sonar-maven-plugin:3.2:sonar'
                    }
                }
                boolean release = false
                steps.stage(stages['confirmRelease']) {
                    try {
                        timeout(time: 1, unit: 'MINUTES') {
                            input 'Release to Central?'
                            release = true
                        }
                    } catch (ignored) {
                    }
                }
                if (release) {
                    steps.stage(stages['release']) {
                        echo "mvn 'release:prepare'"
                        echo "mvn 'release:perform'"
                    }
                }
            } else {
                steps.stage(stages['build']) {
                    mvn 'install site'
                }
            }

        }
    }
    /**
     * Support step to execute mvn from the shell and capture any test results.
     */
    @NonCPS
    private def mvn(String args) {
        steps.sh "mvn --batch-mode ${args}"
        steps.junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
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
