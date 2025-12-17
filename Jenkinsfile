pipeline {
  agent any

  environment {
    IMAGE_NAME = "aibatyr/todo-app"
    IMAGE_TAG  = "1.0"
  }

  stages {
    stage('Build') {
      steps {
        sh 'mvn -B -Djava.net.preferIPv4Stack=true clean package'
      }
    }

    stage('Test') {
      steps {
        sh 'mvn -B -Djava.net.preferIPv4Stack=true test'
      }
    }

    stage('Docker Build') {
      steps {
        sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -f Dockerfile_BakhitbekovAibatyr ."
      }
    }

    stage('Docker Login') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
          sh 'echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin'
        }
      }
    }

    stage('Docker Push') {
      steps {
        sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
      }
    }

    stage('Deploy (docker compose)') {
      steps {
        // если деплой на той же машине где Docker (и сокет примонтирован) — работает сразу
        sh 'docker compose pull'
        sh 'docker compose up -d'
        sh 'docker ps | grep todo-app || true'
      }
    }
  }

  post {
    success { echo '✅ Part 3 done: pushed + deployed' }
    failure { echo '❌ Pipeline failed' }
    always  { sh 'docker logout || true' }
  }
}
