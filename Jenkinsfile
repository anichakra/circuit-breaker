#!groovy
// Pipeline as code using Jenkinsfile for a microservice
// @author Anirban Chakraborty

node {  
  ws("workspace/${env.JOB_NAME}/${env.BRANCH_NAME}") {
    try {      
      // Docker image details - might not be required to be changed often    
      def MAVEN_IMAGE    = "maven:3-jdk-11"
      def MAVEN_VOLUME   = "-v maven-repo/.m2:/root/.m2"
    
      sh('printenv | sort')
      println "Pipeline started in workspace/" + env.JOB_NAME + "/" + env.BRANCH_NAME
      
      stage('SCM Checkout') {
        println "########## Checking out latest from git repo ##########"
        checkout scm
      }
    
      stage('JAR Installation') {
        println "########## Installing jar files in local maven repository ##########"
        docker.image(MAVEN_IMAGE).inside(MAVEN_VOLUME) {
          sh('mvn clean install -Dmaven.test.skip=true')
        }
      }
      
      stage('JAR Deploy') {
        println "########## Installing jar files in local maven repository ##########"
        docker.image(MAVEN_IMAGE).inside(MAVEN_VOLUME) {
          sh('mvn deploy -DskipTests -Dmaven.install.skip=true')
        }
      }
    } catch(e) {
      println "Err: Incremental Build failed with Error: " + e.toString()
      currentBuild.result = 'FAILED'
      throw e
    } finally  {
      stage('Cleanup') {
        println "Cleaning up"
        deleteDir()
      }          
    }
  }
}