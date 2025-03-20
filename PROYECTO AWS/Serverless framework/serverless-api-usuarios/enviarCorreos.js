const { SNSClient, PublishCommand } = require("@aws-sdk/client-sns");

const snsClient = new SNSClient({});

exports.enviarCorreos = async (event) => {
    console.log("Evento recibido:", JSON.stringify(event, null, 2));

    // Validar si event.Records existe y es un array
    if (!event.Records || !Array.isArray(event.Records)) {
        console.error("El evento recibido no tiene un formato válido de SQS.");
        return {
            statusCode: 400,
            body: JSON.stringify({ message: "Formato de evento inválido", event })
        };
    }

    for (const record of event.Records) {
        const { id, nombre, email } = JSON.parse(record.body);

        const paramsSNS = {
            Subject: "Bienvenido a Nuestro Servicio",
            Message: `Hola ${nombre},\n\nGracias por registrarte. Tu ID de usuario es ${id}.`,
            TopicArn: process.env.SNS_TOPIC_ARN
        };

        try {
            await snsClient.send(new PublishCommand(paramsSNS));
            console.log(`Correo enviado a ${email}`);
        } catch (error) {
            console.error(`Error al enviar correo a ${email}:`, error);
        }
    }

    return {
        statusCode: 200,
        body: JSON.stringify({ message: "Procesamiento completado" })
    };
};
