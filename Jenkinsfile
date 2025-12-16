pipeline {
    agent any

    environment {
        APP_DIR    = "D:/IITU/mini_task2/todo_app/todo_app"
        IMAGE_NAME = "aibatyr/todo-app"
        IMAGE_TAG  = "1.0"
    }

    stages {
        stage('Build') {
            steps {
                dir("${APP_DIR}") {
                    bat "mvn clean package"
                }
            }
        }

        stage('Test') {
            steps {
                dir("${APP_DIR}") {
                    bat "mvn test"
                }
            }
        }

        stage('Docker Build') {
            steps {
                dir("${APP_DIR}") {
                    bat "docker build -t %IMAGE_NAME%:%IMAGE_TAG% -f Dockerfile_BakhitbekovAibatyr ."
                }
            }
        }

        stage('Docker Push') {
            steps {
                dir("${APP_DIR}") {
                    bat "docker push %IMAGE_NAME%:%IMAGE_TAG%"
                }
            }
        }
    }

    post {
        success { echo "Pipeline finished successfully" }
        failure { echo "Pipeline failed" }
    }
}
