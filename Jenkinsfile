@Library('pipeline-library') _
def resourceGroupName = null
pipeline {
    agent {label 'swarm'}
    options {
      ansiColor('xterm')
      timestamps()
  }
   environment {
       SERVICE_NAME = "soaprequest-microservice"
       SONARQUBE_PROJECT_NAME = "Imports-soaprequest-microservice"
       SERVICE_VERSION = "1.0.${BUILD_NUMBER}"
       ENVIRONMENT = "Sandpit"
   }

   stages {
       stage('Azure login') {
           steps {
             azLoginClient()
           }
       }

       stage('Compile') {
           steps {
             getFile 'settings/maven.xml'
             sh 'mvn -f service/pom.xml clean compile -Dapi_version="${SERVICE_VERSION}" --settings ./settings/maven.xml'
           }
       }

       stage('Unit Test') {
           steps {
             sh 'mvn -f service/pom.xml test --settings ./settings/maven.xml'
           }
           post {
             success {
               junit '**/service/target/surefire-reports/*.xml'
             }
           }
       }

       stage('Package') {
           steps {
               sh 'mvn -f service/pom.xml resources:resources -Dapi_version="${BUILD_NUMBER}"'
               sh 'mvn -f service/pom.xml package -Dapi_version="${BUILD_NUMBER}" --settings ./settings/maven.xml'
           }
       }

       stage('Docker build') {
           steps {
               dockerBuild("${SERVICE_NAME}", "${ENVIRONMENT}", "${SERVICE_VERSION}")
               dockerBuildDatabaseMigrations("${SERVICE_NAME}", "${ENVIRONMENT}", "${SERVICE_VERSION}")
           }
       }

       stage('Docker push') {
           steps {
               dockerPushToRegistry("${SERVICE_NAME}", "${ENVIRONMENT}", "${SERVICE_VERSION}")
               dockerPushToRegistry("${SERVICE_NAME}-database-migration", "${ENVIRONMENT}", "${SERVICE_VERSION}")
           }
       }

       stage('Deploy') {
           steps {
             lock ('environmentReservations') {
               script {
                 resourceGroupName = environmentGet("${BRANCH_NAME}")
               }
             }
             environmentImportsResync(resourceGroupName, "${ENVIRONMENT}", "${SERVICE_NAME}")
             databaseCreate(resourceGroupName, "${SERVICE_NAME}", "${ENVIRONMENT}", true)
             databaseRunMigrations("${SERVICE_NAME}", resourceGroupName, "${ENVIRONMENT}", "${SERVICE_VERSION}")
             deployComponent(resourceGroupName, "${SERVICE_NAME}", "${ENVIRONMENT}", "${BRANCH_NAME}", "${SERVICE_VERSION}")
           }
       }

       stage('SonarQube Analysis') {
         when {
             environment name: 'BRANCH_NAME', value: 'master'
         }
         steps {
           runSonarQube("${SONARQUBE_PROJECT_NAME}")
         }
       }

       stage('Integration Tests') {
            steps {
              runServiceIntegrationTests("${SERVICE_NAME}", "${ENVIRONMENT}", "${BRANCH_NAME}", resourceGroupName, "regression")
            }
            post {
              always {
                junit '**/integration/target/surefire-reports/*.xml'
              }
            }
        }

       stage('Selenium Tests') {
           steps {
             runSeleniumGridTests("${SERVICE_NAME}", "${ENVIRONMENT}", "master", "regression")
           }
       }

       stage('Promote') {
           when {
               environment name: 'BRANCH_NAME', value: 'master'
           }
           steps {
             dockerPromote("${SERVICE_NAME}", "${ENVIRONMENT}", "${SERVICE_VERSION}")
             dockerPromoteConfiguration("${SERVICE_NAME}", "${ENVIRONMENT}", "${SERVICE_VERSION}")
           }
       }
   }

   post {
       always {
         lock ('environmentReservations') {
           environmentReturn(resourceGroupName)
         }
         step([$class: 'WsCleanup'])
       }
       failure{
        notifySlack("BUILD OF ${SERVICE_NAME} HAS FAILED: ${BUILD_URL}", "#FF9FA1")
       }
   }
}
