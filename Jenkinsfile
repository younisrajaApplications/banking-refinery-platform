pipeline {
    agent any // Use any available jenkins agent

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        APP_DIR = 'app/java-ingestion-service'

        CI_COMPOSE_FILE = 'docker-compose.ci.yml'
        DB_CONTAINER = 'banking-refinery-postgres-ci'
        DB_NAME = 'banking_refinery'

        FLYWAY_URL = 'jdbc:postgresql://host.docker.internal:5432/banking_refinery'
        FLYWAY_USER = 'refinery_user'
        FLYWAY_PASSWORD = 'refinery_password'

        SPRING_DATASOURCE_URL = 'jdbc:postgresql://host.docker.internal:5433/banking_refinery'
        SPRING_DATASOURCE_USERNAME = 'refinery_user'
        SPRING_DATASOURCE_PASSWORD = 'refinery_password'
    }

    stages {
        stage('Verify Tools') {
            steps {
                sh 'java -version'
                sh 'git --version'
                sh 'docker --version'
                sh 'docker compose version'
                sh 'curl --version'
            }
        }

        stage('Start Postgres') {
            steps {
                sh '''
                    docker compose -f "${CI_COMPOSE_FILE}" down -v || true
                    docker compose -f "${CI_COMPOSE_FILE}" up -d postgres
                    docker compose -f "${CI_COMPOSE_FILE}" ps
                '''
            }
        }

        stage('Wait for Postgres') {
            steps {
                sh '''
                    for i in $(seq 1 30); do
                        if docker compose -f "${CI_COMPOSE_FILE}" exec -T postgres \
                            pg_isready -U "${FLYWAY_USER}" -d "${DB_NAME}"; then

                            echo "Postgres is ready"
                            break
                        fi

                        echo "Waiting for Postgres"
                        sleep 2
                    done

                    echo "Postgres did not become ready in time."
                    docker compose -f "${CI_COMPOSE_FILE}" logs --no-color postgres || true
                    exit 1
                '''
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
                    sh '''
                        SPRING_DATASOURCE_URL="${SPRING_DATASOURCE_URL}" \
                        SPRING_DATASOURCE_USERNAME="${SPRING_DATASOURCE_USERNAME}" \
                        SPRING_DATASOURCE_PASSWORD="${SPRING_DATASOURCE_PASSWORD}" \
                        ./mvnw clean test
                    '''
                }
            }
        }

        stage('Run End-to-End Ingestion Check') {
            steps {
                sh './scripts/run-e2e-ingestion-check.sh'
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
                docker compose -f "${CI_COMPOSE_FILE}" logs --no-color postgres > postgres-ci.log || true
            '''

            archiveArtifacts allowEmptyArchive: true, artifacts: '''
                postgres-ci.log,
                e2e-app.log,
                e2e-ingestion-response.json,
                e2e-ingestion-detail.json
            '''

            sh '''
                docker compose -f "${CI_COMPOSE_FILE}" down -v || true
            '''
        }

        success {
            echo 'Pipeline completed successfully.'
        }

        failure {
            echo 'Pipeline failed. Check the failed stage and logs.'
        }
    }
}