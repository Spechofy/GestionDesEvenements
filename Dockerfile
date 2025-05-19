# Utilise une image Java officielle
FROM eclipse-temurin:21-jdk-alpine

# Dossier de travail dans le conteneur
WORKDIR /app

# Copie le build Maven (tu dois builder avant avec ./mvnw package)
COPY target/event-service-0.0.1-SNAPSHOT.jar app.jar

# Expose le port 8080 (port du backend)
EXPOSE 8080

# Commande pour lancer l'app
ENTRYPOINT ["java", "-jar", "app.jar"]
