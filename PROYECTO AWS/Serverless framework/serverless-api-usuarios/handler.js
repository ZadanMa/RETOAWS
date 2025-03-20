"use strict";

const { DynamoDBClient } = require("@aws-sdk/client-dynamodb");
const { DynamoDBDocumentClient, ScanCommand, PutCommand } = require("@aws-sdk/lib-dynamodb");
const { SQSClient, SendMessageCommand } = require("@aws-sdk/client-sqs");

const client = new DynamoDBClient({});
const dynamoDB = DynamoDBDocumentClient.from(client);

const sqsClient = new SQSClient({});

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
module.exports.postUser = async (event) => {
    // Validar que se haya enviado un cuerpo en la solicitud
    if (!event.body) {
        return {
            statusCode: 400,
            body: JSON.stringify({ message: "Error: No se proporcionó un cuerpo en la solicitud." })
        };
    }

    try {
        const body = JSON.parse(event.body);

        // Validar que el cuerpo tiene el formato esperado
        if (typeof body !== "object" || !body.id || !body.nombre || !body.email) {
            return {
                statusCode: 400,
                body: JSON.stringify({ message: "Error: El cuerpo de la solicitud no tiene el formato esperado." })
            };
        }

        // Parámetros para guardar el usuario en DynamoDB
        const paramsDynamo = {
            TableName: "usuarios",
            Item: {
                id: body.id,
                nombre: body.nombre,
                email: body.email
            }
        };

        try {
            await dynamoDB.send(new PutCommand(paramsDynamo));
        } catch (error) {
            console.error("Error al guardar en DynamoDB:", error);
            return {
                statusCode: 500,
                body: JSON.stringify({ message: "Error al guardar el usuario en la base de datos." })
            };
        }

        // Validar que la variable de entorno para SQS esté definida
        if (!process.env.CREAR_USUARIO_QUEUE_URL) {
            throw new Error("La variable de entorno CREAR_USUARIO_QUEUE_URL no está definida.");
        }

        // Parámetros para enviar mensaje a SQS con la información del usuario creado
        const paramsSQS = {
            QueueUrl: process.env.CREAR_USUARIO_QUEUE_URL,
            MessageBody: JSON.stringify({
                id: body.id.toString(),
                nombre: body.nombre,
                email: body.email,
                timestamp: new Date().toISOString()  // Añadir metadato
            })
        };
        try {
            const sqsResponse = await sqsClient.send(new SendMessageCommand(paramsSQS));
            console.log("Respuesta SQS:", sqsResponse);
        } catch (error) {
            console.error("Detalle del error SQS:", {
                message: error.message,
                code: error.code,
                queueUrl: paramsSQS.QueueUrl,
                stack: error.stack
            });
            return {
                statusCode: 500,
                body: JSON.stringify({
                    message: "Error SQS",
                    error: error.message
                })
            };
        }
        try {
            await sqsClient.send(new SendMessageCommand(paramsSQS));
        } catch (error) {
            console.error("Error al enviar mensaje a SQS:", error);
            return {
                statusCode: 500,
                body: JSON.stringify({ message: "Error al enviar el mensaje a la cola SQS." })
            };
        }

        return {
            statusCode: 201,
            body: JSON.stringify({ message: "Usuario creado y mensaje enviado a SQS correctamente." })
        };

    } catch (error) {
        console.error("Error al analizar el cuerpo de la solicitud:", error);
        return {
            statusCode: 400,
            body: JSON.stringify({ message: "Error: El cuerpo de la solicitud no es un JSON válido." })
        };
    }
};
