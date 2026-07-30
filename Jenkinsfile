pipeline {
    agent any // Use any available jenkins agent

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        APP_DIR = 'app/java-ingestion-service'
    }

    stages {
        stage('Verify Tools') {
            steps {
                sh 'java -version'
                sh 'docker --version'
                sh 'docker compose version'
            }
        }

        stage('Start Postgres') {
            steps {
                sh 'docker compose up -d postgres'
                sh 'docker compose ps'
            }
        }

        stage('Run Java Tests') {
            steps {
                dir("${APP_DIR}") {
                    sh './mvnw clean test'
                }
            }
        }

        stage('Run SQL Data Quality Checks') {
            steps {
                sh './scripts/run-postgres-quality-checks-ci.sh'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'app/java-ingestion-service/target/surefire-reports/*.xml'

            sh '''
                docker compose logs --no-color postgres > postgres.log || true
            '''

            archiveArtifacts allowEmptyArchive: true, artifacts: 'postgres.log'
        }

        success {
            echo 'Pipeline completed successfully.'
        }

        failure {
            echo 'Pipeline failed. Check the failed stage and logs.'
        }
    }
}