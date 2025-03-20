package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

public class PutUser implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private static final String TABLE_NAME = "usuarios";
    private final DynamoDbClient dynamoDbClient = DynamoDbClient.create();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 1. Obtener parámetros del path
            Map<String, String> pathParams = (Map<String, String>) event.get("pathParameters");
            String id = pathParams.get("id");

            // 2. Parsear el body desde JSON String a Map
            String bodyStr = (String) event.get("body");
            Map<String, String> body = objectMapper.readValue(
                    bodyStr,
                    new TypeReference<Map<String, String>>() {}
            );

            // 3. Validaciones
            if (id == null || id.isEmpty()) {
                return errorResponse(400, "Se requiere el ID en el path");
            }

            String nombre = body.get("nombre");
            String email = body.get("email");

            if (nombre == null || email == null) {
                return errorResponse(400, "Nombre y email son requeridos");
            }

            // 4. Construir UpdateExpression
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("id", AttributeValue.builder().s(id).build());

            Map<String, AttributeValueUpdate> updates = new HashMap<>();
            updates.put("nombre", AttributeValueUpdate.builder()
                    .value(AttributeValue.builder().s(nombre).build())
                    .action(AttributeAction.PUT)
                    .build());

            updates.put("email", AttributeValueUpdate.builder()
                    .value(AttributeValue.builder().s(email).build())
                    .action(AttributeAction.PUT)
                    .build());

            // 5. Ejecutar actualización
            UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .key(key)
                    .attributeUpdates(updates)
                    .returnValues(ReturnValue.UPDATED_NEW)
                    .build();

            dynamoDbClient.updateItem(updateRequest);

            // 6. Respuesta exitosa
            response.put("statusCode", 200);
            response.put("headers", Map.of("Content-Type", "application/json"));
            response.put("body", "{\"message\":\"Usuario actualizado\"}");

        } catch (IOException e) {
            context.getLogger().log("Error de parsing JSON: " + e.getMessage());
            return errorResponse(400, "Formato JSON inválido");
        } catch (DynamoDbException e) {
            context.getLogger().log("Error de DynamoDB: " + e.getMessage());
            return errorResponse(500, "Error al actualizar el usuario");
        } catch (Exception e) {
            context.getLogger().log("Error general: " + e.getMessage());
            return errorResponse(500, "Error interno del servidor");
        }

        return response;
    }

    private Map<String, Object> errorResponse(int code, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", code);
        response.put("body", "{\"error\":\"" + message + "\"}");
        return response;
    }
}