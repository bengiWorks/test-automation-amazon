pipeline {
    agent any

    tools {
        maven 'Maven311'
        jdk 'JDK21'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/bengiWorks/test-automation-amazon.git'
            }
        }

        stage('Prepare Environment') {
            steps {
                // Maven temizleme
                bat 'mvn clean'

                // Allure results klasörünü temizle
                bat 'rmdir /s /q allure-results || echo Folder does not exist'
                bat 'mkdir allure-results'
            }
        }

        stage('Build') {
            steps {
                // Testleri çalıştırmadan projeyi derle ve bağımlılıkları indir
                bat 'mvn install -DskipTests'
            }
        }

        /*
        stage('Run Tests') {
            steps {
                bat 'mvn test'
            }
            post {
                always {
                    // Surefire test raporlarını Jenkins'e tanıt
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        */

        stage('Run Tests') {
            steps {
                // Test failures ignore ediliyor, Allure raporu oluşacak
                bat 'mvn test -Dmaven.test.failure.ignore=true'
            }
            post {
                always {
                    // Surefire test raporlarını Jenkins'e tanıt
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }


        stage('Allure Report') {
            steps {
                allure([
                    includeProperties: false,
                    jdk: '',
                    results: [[path: 'allure-results']]
                ])
            }
        }
    }
}
