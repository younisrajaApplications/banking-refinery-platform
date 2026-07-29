pipeline {
    agent any // Use any available jenkins agent

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        APP_DIR = 'app/java-ingestion-service'
        FLYWAY_URL = 'jdbc:postgresql://host.docker.internal:5432/banking_refinery'
        FLYWAY_USER = 'refinery_user'
        FLYWAY_PASSWORD = 'refinery_password'
    }

    stages {
        stage('Verify Tools') {
            steps {
                sh 'java -version'
                sh 'git --version'
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

        stage('Run Database Migrations') {
            steps {
                dir("${APP_DIR}") {
                    sh '''
                        ./mvnw flyway:migrate \
                          -Dflyway.url="${FLYWAY_URL}" \
                          -Dflyway.user="${FLYWAY_USER}" \
                          -Dflyway.password="${FLYWAY_PASSWORD}" \
                          -Dflyway.locations=filesystem:src/main/resources/db/migration
                    '''
                }
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