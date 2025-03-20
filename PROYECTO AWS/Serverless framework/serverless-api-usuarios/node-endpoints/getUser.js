"use strict";

// Importar clientes del AWS SDK v3 (modular)
const { DynamoDBClient } = require("@aws-sdk/client-dynamodb");
const { DynamoDBDocumentClient, ScanCommand, PutCommand } = require("@aws-sdk/lib-dynamodb");
const { SQSClient, SendMessageCommand } = require("@aws-sdk/client-sqs");

// Inicializar cliente de DynamoDB y convertirlo en un DocumentClient
const client = new DynamoDBClient({});
const dynamoDB = DynamoDBDocumentClient.from(client);

module.exports.getUser = async (event) => {
    const params = {
        TableName: "usuarios"
    };

    try {
        const data = await dynamoDB.send(new ScanCommand(params));
        return {
            statusCode: 200,
            body: JSON.stringify(data.Items)
        };
    } catch (error) {
        console.error("Error al obtener usuarios:", error);
        return {
            statusCode: 500,
            body: JSON.stringify({ message: "Error al obtener usuarios" })
        };
    }
};
