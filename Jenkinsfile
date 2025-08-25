pipeline {
    agent any // 1. Ajan Seçimi

    tools { // 2. Araç Tanımlaması
        maven 'Maven311'
        jdk 'JDK21'
    }

    stages {
        stage('Checkout') { // 3. Kodu İndirme
            steps {
                git branch: 'main',
                    url: 'https://github.com/bengiWorks/amazon-test-automation.git'
            }
        }

        stage('Build') { // 4. Projeyi Derleme
            steps {
                // Testleri çalıştırmadan projeyi derle ve bağımlılıkları indir.
                bat 'mvn clean install -DskipTests'
            }
        }

        stage('Run Tests') { // 5. Testleri Çalıştırma
            steps {
                bat 'mvn test'
            }
            post { // 6. Test Sonuçlarını Raporlama
                always {
                    // Surefire plugin'inin oluşturduğu test raporlarını Jenkins'e tanıt.
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
    }
}