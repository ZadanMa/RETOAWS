package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.HashMap;

public class DeleteUser implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private static final String TABLE_NAME = "usuarios";
    private final DynamoDbClient dynamoDbClient = DynamoDbClient.create();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Obtener parámetro de path
            Map<String, String> pathParameters = (Map<String, String>) event.get("pathParameters");
            String id = pathParameters.get("id");

            // Validar ID
            if (id == null || id.isEmpty()) {
                response.put("statusCode", 400);
                response.put("body", "{\"error\":\"Se requiere el parámetro ID\"}");
                return response;
            }

            // Construir clave primaria
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("id", AttributeValue.builder().s(id).build());

            // Ejecutar eliminación
            DeleteItemRequest deleteRequest = DeleteItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .key(key)
                    .build();

            dynamoDbClient.deleteItem(deleteRequest);

            // Configurar respuesta exitosa
            response.put("statusCode", 200);
            response.put("headers", Map.of("Content-Type", "application/json"));
            response.put("body", "{\"message\":\"Usuario eliminado correctamente\"}");

        } catch (DynamoDbException e) {
            context.getLogger().log("Error de DynamoDB: " + e.getMessage());
            response.put("statusCode", 500);
            response.put("body", "{\"error\":\"Error al eliminar el usuario\"}");
        } catch (Exception e) {
            context.getLogger().log("Error general: " + e.getMessage());
            response.put("statusCode", 500);
            response.put("body", "{\"error\":\"Error interno del servidor\"}");
        }

        return response;
    }
}