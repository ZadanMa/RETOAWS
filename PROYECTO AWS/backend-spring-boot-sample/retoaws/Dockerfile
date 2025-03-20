# Usa una imagen base de Java (puedes ajustar la versión según tus necesidades)
FROM amazoncorretto:17

# Copia el archivo JAR de tu aplicación al contenedor
COPY target/retoaws-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto 8090
EXPOSE 8090

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]