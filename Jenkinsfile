pipeline {
    agent any

    environment {
        IMAGE_NAME = "aibatyr/todo-app"
        IMAGE_TAG  = "1.0"
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Source code already checked out by SCM'
                sh 'ls -la'
            }
        }

        stage('Build') {
            steps {
                echo 'Building project with Maven'
                dir('todo_app') {
                    sh 'ls -la'
                    sh 'mvn -v'
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests'
                dir('todo_app') {
                    sh 'mvn test'
                }
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Building Docker image'
                dir('todo_app') {
                    sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -f Dockerfile_BakhitbekovAibatyr ."
                }
            }
        }

        stage('(Optional) Docker Push') {
            when { expression { return false } }   // чтобы не пушить случайно на защите
            steps {
                echo 'Pushing image to Docker Hub'
                sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
            }
        }
    }

    post {
        success {
            echo 'Pipeline finished successfully'
        }
        failure {
            echo 'Pipeline failed'
        }
    }
}
